#!/bin/bash

# Test script for Page Object Generator
# This script runs automated tests to validate the generator

set -e

echo "🧪 Testing Page Object Generator..."
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check if ChromeDriver is available
if ! command -v chromedriver &> /dev/null; then
    echo -e "${YELLOW}⚠️  ChromeDriver not found in PATH${NC}"
    echo "   The generator uses ChromeDriver. Make sure it's installed."
    echo "   You can install it with: brew install chromedriver (macOS)"
    echo ""
fi

# Run the browser tests
echo "Running automated browser tests..."
echo ""

./mvnw test -Dtest=PageObjectGeneratorBrowserTest -Dheadless=true 2>&1 | tee /tmp/pog-test-output.log

# Check test results
if [ ${PIPESTATUS[0]} -eq 0 ]; then
    echo ""
    echo -e "${GREEN}✅ All automated tests PASSED!${NC}"
    echo ""
    echo "Generated Page Objects are in: target/generated-test-sources/page-objects/"
    echo ""
    echo "Next steps:"
    echo "  1. Review generated code in target/generated-test-sources/page-objects/"
    echo "  2. Check that locators are correct"
    echo "  3. Test generated code in your actual tests"
    echo ""
else
    echo ""
    echo -e "${RED}❌ Some tests FAILED${NC}"
    echo ""
    echo "Check the output above for details."
    echo "Full log: /tmp/pog-test-output.log"
    echo ""
    exit 1
fi

