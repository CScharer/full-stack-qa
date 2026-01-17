#!/bin/bash
# scripts/temp/run-all-tests-non-interactive.sh
# Run All Tests Non-Interactively
#
# Purpose: Run all local tests with non-interactive flags to prevent prompts
#
# Usage:
#   ./scripts/temp/run-all-tests-non-interactive.sh
#
# Description:
#   Runs all test frameworks with CI and non-interactive flags set to prevent
#   any prompts or waiting for user input. Uses timeout to prevent hanging.
#
# Dependencies:
#   - All test framework dependencies (Node.js, Python, Java, Maven)
#
# Output:
#   - Test results for each framework
#   - Summary of passed/failed tests
#   - Exit code: 0 if all tests pass, non-zero if any fail
#
# Notes:
#   - Sets CI=true to enable CI mode in all test frameworks
#   - Sets timeout to prevent hanging (30 minutes max)
#   - All tests run in headless/non-interactive mode
#
# Last Updated: January 2026

set -e

# Get project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$SCRIPT_DIR"

# Set non-interactive flags
export CI=true
export NON_INTERACTIVE=true
export SKIP_PROMPTS=true
export BASE_URL="${BASE_URL:-https://www.google.com}"
export ENVIRONMENT="${ENVIRONMENT:-local}"

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}🧪 Running All Tests (Non-Interactive Mode)${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo ""
echo "Environment: $ENVIRONMENT"
echo "Base URL: $BASE_URL"
echo "CI Mode: $CI"
echo ""

# Track results
TESTS_PASSED=0
TESTS_FAILED=0

# Function to run test with timeout
run_test_with_timeout() {
    local test_name=$1
    local command=$2
    local timeout_seconds=${3:-1800}  # 30 minutes default
    
    echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}🧪 Running: $test_name${NC}"
    echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
    
    if timeout $timeout_seconds bash -c "$command" 2>&1; then
        echo -e "${GREEN}✅ $test_name: PASSED${NC}"
        ((TESTS_PASSED++))
        return 0
    else
        local exit_code=$?
        if [ $exit_code -eq 124 ]; then
            echo -e "${RED}❌ $test_name: TIMEOUT (exceeded ${timeout_seconds}s)${NC}"
        else
            echo -e "${RED}❌ $test_name: FAILED${NC}"
        fi
        ((TESTS_FAILED++))
        return 1
    fi
    echo ""
}

# 1. Port Configuration Tests (quick verification)
echo -e "${YELLOW}📋 Step 1: Verifying Port Configuration${NC}"
if python3 scripts/temp/test-port-config-removal.py 2>&1; then
    echo -e "${GREEN}✅ Port configuration tests: PASSED${NC}"
    ((TESTS_PASSED++))
else
    echo -e "${RED}❌ Port configuration tests: FAILED${NC}"
    ((TESTS_FAILED++))
fi
echo ""

if bash scripts/temp/test-port-config-removal.sh 2>&1; then
    echo -e "${GREEN}✅ Port configuration script tests: PASSED${NC}"
    ((TESTS_PASSED++))
else
    echo -e "${RED}❌ Port configuration script tests: FAILED${NC}"
    ((TESTS_FAILED++))
fi
echo ""

# 2. Maven Compilation
run_test_with_timeout "Maven Compilation" "./mvnw clean compile -DskipTests" 600

# 3. Run individual test frameworks directly (to avoid path issues)
echo -e "${YELLOW}📋 Step 3: Running Individual Test Frameworks${NC}"

# 3a. Cypress Tests
if [ -d "cypress" ]; then
    run_test_with_timeout "Cypress Tests" \
        "cd cypress && \
        if [ ! -d 'node_modules' ]; then npm install --silent; fi && \
        export BASE_URL='$BASE_URL' && \
        export ENVIRONMENT='dev' && \
        export CI=true && \
        npm run cypress:run" 600
else
    echo -e "${YELLOW}⚠️  Cypress directory not found, skipping...${NC}"
fi

# 3b. Playwright Tests
if [ -d "playwright" ] && [ -f "playwright/package.json" ]; then
    run_test_with_timeout "Playwright Tests" \
        "cd playwright && \
        if [ ! -d 'node_modules' ]; then npm install --silent && npx playwright install --with-deps chromium; fi && \
        export BASE_URL='$BASE_URL' && \
        export ENVIRONMENT='dev' && \
        export CI=true && \
        npm test" 600
else
    echo -e "${YELLOW}⚠️  Playwright directory not found, skipping...${NC}"
fi

# 3c. Frontend Unit Tests (if available)
if [ -d "frontend" ] && [ -f "frontend/package.json" ]; then
    run_test_with_timeout "Frontend Unit Tests" \
        "cd frontend && \
        if [ ! -d 'node_modules' ]; then npm install --silent; fi && \
        export CI=true && \
        npm test -- --run" 300
else
    echo -e "${YELLOW}⚠️  Frontend directory not found, skipping...${NC}"
fi

# Summary
echo ""
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}📊 Test Summary${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${GREEN}✅ Passed: $TESTS_PASSED${NC}"
echo -e "${RED}❌ Failed: $TESTS_FAILED${NC}"
echo ""

if [ $TESTS_FAILED -eq 0 ]; then
    echo -e "${GREEN}🎉 All tests passed!${NC}"
    exit 0
else
    echo -e "${RED}💥 Some tests failed. Check the output above for details.${NC}"
    exit 1
fi
