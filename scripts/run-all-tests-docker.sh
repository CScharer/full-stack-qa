#!/bin/bash

# Run All Tests in Docker (Matching CI/CD Pipeline)
# This script runs all test frameworks in Docker, matching the CI/CD pipeline execution

set -e

echo "═══════════════════════════════════════════════════════════════"
echo "🧪 Running All Tests in Docker (Matching CI/CD Pipeline)"
echo "═══════════════════════════════════════════════════════════════"
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Configuration
BASE_URL="${BASE_URL:-https://www.google.com}"
TEST_ENVIRONMENT="${TEST_ENVIRONMENT:-docker}"
SELENIUM_REMOTE_URL="${SELENIUM_REMOTE_URL:-http://selenium-hub:4444/wd/hub}"

echo -e "${BLUE}Configuration:${NC}"
echo "  Base URL: $BASE_URL"
echo "  Test Environment: $TEST_ENVIRONMENT"
echo "  Selenium Grid: $SELENIUM_REMOTE_URL"
echo ""

# Start Selenium Grid
echo -e "${YELLOW}📦 Starting Selenium Grid...${NC}"
docker-compose up -d selenium-hub chrome-node-1 chrome-node-2 edge-node

# Wait for Grid to be ready
echo -e "${YELLOW}⏳ Waiting for Selenium Grid to be ready...${NC}"
timeout 60 bash -c 'until curl -sf http://localhost:4444/wd/hub/status > /dev/null; do sleep 2; done'
sleep 5
echo -e "${GREEN}✅ Selenium Grid is ready!${NC}"
echo ""

# Track test results
TESTS_PASSED=0
TESTS_FAILED=0

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

# 1. Selenium/Java Tests (Smoke Tests)
run_test_suite "Selenium Smoke Tests" \
    "docker-compose run --rm tests -DsuiteXmlFile=testng-smoke-suite.xml"

# 2. Selenium/Java Tests (Grid Tests)
run_test_suite "Selenium Grid Tests" \
    "docker-compose run --rm tests -DsuiteXmlFile=testng-ci-suite.xml"

# 3. Cypress Tests
run_test_suite "Cypress Tests" \
    "docker-compose run --rm -e BASE_URL=$BASE_URL -e TEST_ENVIRONMENT=$TEST_ENVIRONMENT -e CYPRESS_BASE_URL=$BASE_URL tests bash -c 'cd /app/cypress && xvfb-run -a npm run cypress:run || npm run cypress:run'"

# 4. Playwright Tests
run_test_suite "Playwright Tests" \
    "docker-compose run --rm -e BASE_URL=$BASE_URL -e TEST_ENVIRONMENT=$TEST_ENVIRONMENT -e CI=true tests bash -c 'cd /app/playwright && npm test'"

# 5. Robot Framework Tests
run_test_suite "Robot Framework Tests" \
    "docker-compose run --rm -e BASE_URL=$BASE_URL -e TEST_ENVIRONMENT=$TEST_ENVIRONMENT -e SELENIUM_REMOTE_URL=$SELENIUM_REMOTE_URL tests bash -c './mvnw test -Probot'"

# Summary
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}📊 Test Summary${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${GREEN}✅ Passed: $TESTS_PASSED${NC}"
echo -e "${RED}❌ Failed: $TESTS_FAILED${NC}"
echo ""

if [ $TESTS_FAILED -eq 0 ]; then
    echo -e "${GREEN}🎉 All test suites passed!${NC}"
    exit 0
else
    echo -e "${RED}⚠️  Some test suites failed. Check the logs above for details.${NC}"
    exit 1
fi

