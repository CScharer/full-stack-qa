#!/bin/bash
# Run tests locally without Docker
# This script runs all test frameworks that don't require Docker

set -e

# Get the script directory (project root)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$SCRIPT_DIR"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Counters
TESTS_PASSED=0
TESTS_FAILED=0

# Default values
BASE_URL=${BASE_URL:-"https://www.google.com"}
TEST_ENVIRONMENT=${TEST_ENVIRONMENT:-"local"}

echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}🧪 Running Tests Locally (No Docker)${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo ""
echo "Environment: $TEST_ENVIRONMENT"
echo "Base URL: $BASE_URL"
echo ""

# Function to run a test suite
run_test_suite() {
    local suite_name=$1
    local command=$2
    
    echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}🧪 Running: $suite_name${NC}"
    echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
    
    if eval "$command"; then
        echo -e "${GREEN}✅ $suite_name: PASSED${NC}"
        ((TESTS_PASSED++))
    else
        echo -e "${RED}❌ $suite_name: FAILED${NC}"
        ((TESTS_FAILED++))
    fi
    echo ""
}

# Check prerequisites
echo -e "${YELLOW}📋 Checking Prerequisites...${NC}"

# Check Node.js
if ! command -v node &> /dev/null; then
    echo -e "${RED}❌ Node.js is not installed. Please install Node.js 20+${NC}"
    exit 1
fi
NODE_VERSION=$(node --version)
echo -e "${GREEN}✅ Node.js: $NODE_VERSION${NC}"

# Check Java
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Java is not installed. Please install Java 21+${NC}"
    exit 1
fi
JAVA_VERSION=$(java -version 2>&1 | head -n 1)
echo -e "${GREEN}✅ Java: $JAVA_VERSION${NC}"

# Check Python (for Robot Framework)
if ! command -v python3 &> /dev/null; then
    echo -e "${YELLOW}⚠️  Python 3 is not installed. Robot Framework tests will be skipped.${NC}"
    SKIP_ROBOT=true
else
    PYTHON_VERSION=$(python3 --version)
    echo -e "${GREEN}✅ Python: $PYTHON_VERSION${NC}"
    SKIP_ROBOT=false
fi

echo ""
echo -e "${YELLOW}⚠️  Note: Selenium/Java tests require Selenium Grid.${NC}"
echo -e "${YELLOW}   They will be skipped in this local run.${NC}"
echo -e "${YELLOW}   To run them, use: ./scripts/run-smoke-tests.sh${NC}"
echo ""

# Get the script directory (project root)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$SCRIPT_DIR"

# 1. Cypress Tests
if [ -d "$SCRIPT_DIR/cypress" ]; then
    run_test_suite "Cypress Tests" \
        "cd \"$SCRIPT_DIR/cypress\" && \
        if [ ! -d 'node_modules' ]; then npm install; fi && \
        export CYPRESS_BASE_URL=\"$BASE_URL\" && \
        export TEST_ENVIRONMENT=\"$TEST_ENVIRONMENT\" && \
        npm run cypress:run"
else
    echo -e "${YELLOW}⚠️  Cypress directory not found, skipping...${NC}"
fi

# 2. Playwright Tests  
if [ -d "$SCRIPT_DIR/playwright" ] && [ -f "$SCRIPT_DIR/playwright/package.json" ]; then
    run_test_suite "Playwright Tests" \
        "cd \"$SCRIPT_DIR/playwright\" && \
        if [ ! -d 'node_modules' ]; then npm install && npx playwright install --with-deps chromium; fi && \
        export BASE_URL=\"$BASE_URL\" && \
        export TEST_ENVIRONMENT=\"$TEST_ENVIRONMENT\" && \
        export CI=true && \
        npm test"
else
    echo -e "${YELLOW}⚠️  Playwright directory or package.json not found, skipping...${NC}"
fi

# 3. Robot Framework Tests (if Python is available)
if [ "$SKIP_ROBOT" = false ]; then
    # Check if Robot Framework is installed
    if ! python3 -c "import robot" 2>/dev/null; then
        echo -e "${YELLOW}📦 Installing Robot Framework dependencies...${NC}"
        if [ -f "$SCRIPT_DIR/requirements.txt" ]; then
            pip3 install --user -r "$SCRIPT_DIR/requirements.txt" || pip3 install --user robotframework robotframework-seleniumlibrary robotframework-requests
        else
            pip3 install --user robotframework robotframework-seleniumlibrary robotframework-requests
        fi
    fi
    
    # Note: Robot Framework tests may need Selenium Grid for web tests
    # But API tests should work without Grid
    if [ -d "$SCRIPT_DIR/src/test/robot" ]; then
        echo -e "${YELLOW}⚠️  Robot Framework tests may require Selenium Grid for web tests.${NC}"
        echo -e "${YELLOW}   Attempting to run tests (will fail gracefully if Grid is needed)...${NC}"
        
        # Try to run Robot Framework tests
        # They will fail if they need Grid, but API tests should work
        # Note: We expect failures for web tests that need Grid
        echo -e "${YELLOW}⚠️  Note: Robot Framework web tests require Selenium Grid.${NC}"
        echo -e "${YELLOW}   API tests may work, but web tests will fail without Grid.${NC}"
        # Run but don't fail the script if Grid is needed (expected behavior)
        cd "$SCRIPT_DIR"
        export BASE_URL="$BASE_URL"
        export TEST_ENVIRONMENT="$TEST_ENVIRONMENT"
        if ./mvnw test -Probot 2>&1 | grep -q "Selenium Grid\|WebDriverException"; then
            echo -e "${YELLOW}⚠️  Robot Framework tests require Selenium Grid (expected).${NC}"
            # Don't count as failure since it's expected
        else
            run_test_suite "Robot Framework Tests" "./mvnw test -Probot"
        fi
    else
        echo -e "${YELLOW}⚠️  Robot Framework test directory not found, skipping...${NC}"
    fi
else
    echo -e "${YELLOW}⚠️  Robot Framework tests skipped (Python not available)${NC}"
fi

# Summary
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}📊 Test Summary${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${GREEN}✅ Passed: $TESTS_PASSED${NC}"
echo -e "${RED}❌ Failed: $TESTS_FAILED${NC}"
echo ""

if [ $TESTS_FAILED -eq 0 ]; then
    echo -e "${GREEN}🎉 All local tests passed!${NC}"
    echo ""
    echo -e "${YELLOW}Note: Selenium/Java tests were not run (require Selenium Grid).${NC}"
    echo -e "${YELLOW}To run them, use: ./scripts/run-smoke-tests.sh${NC}"
    exit 0
else
    echo -e "${RED}💥 Some tests failed. Check the output above for details.${NC}"
    exit 1
fi
