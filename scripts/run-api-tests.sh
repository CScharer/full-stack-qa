#!/bin/bash
# API Test Runner
# Run REST API tests without requiring Selenium Grid

set -e

echo "🌐 Running API Tests..."
echo "========================================"

# Configuration
TEST_SUITE="testng-api-suite.xml"
ALLURE_RESULTS_DIR="target/allure-results"

# Clean previous results
echo "Cleaning previous results..."
rm -rf target/allure-results
rm -rf target/surefire-reports
mkdir -p target/allure-results

# Run API tests (no Docker/Grid needed!)
echo ""
echo "📊 Executing API Test Suite..."
echo "Suite: $TEST_SUITE"
echo ""

./mvnw test -DsuiteXmlFile=$TEST_SUITE

TEST_EXIT_CODE=$?

echo ""
echo "========================================"
if [ $TEST_EXIT_CODE -eq 0 ]; then
    echo "✅ API Tests PASSED"
else
    echo "⚠️  API Tests completed with failures"
fi
echo "========================================"

# Display results
echo ""
echo "📊 Test Results:"
echo "  Surefire Reports: $(find target/surefire-reports -name "*.xml" 2>/dev/null | wc -l) files"
echo "  Allure Results: $(find target/allure-results -name "*-result.json" 2>/dev/null | wc -l) tests"

# Optionally generate Allure report
if command -v allure &> /dev/null; then
    echo ""
    read -p "Generate Allure report? (y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo "Generating Allure report..."
        allure generate target/allure-results --clean -o target/allure-report
        echo "Opening report..."
        allure open target/allure-report
    fi
else
    echo ""
    echo "💡 Tip: Install Allure CLI to generate reports:"
    echo "    brew install allure  (macOS)"
    echo "    npm install -g allure-commandline"
fi

exit $TEST_EXIT_CODE

