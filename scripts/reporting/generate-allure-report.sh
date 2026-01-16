#!/bin/bash

# Generate Allure Report Script
# This script runs tests and generates an Allure HTML report

set -e

echo "=========================================="
echo "Allure Report Generation"
echo "=========================================="
echo ""

# 1. Start Selenium Grid
echo "Step 1: Starting Selenium Grid..."
docker-compose up -d selenium-hub chrome-node-1 firefox-node
echo "✅ Grid started"
echo ""

# 2. Wait for Grid to be ready
echo "Step 2: Waiting for Grid to be ready..."
sleep 5
echo "✅ Grid ready"
echo ""

# 3. Clean previous results
echo "Step 3: Cleaning previous test results..."
rm -rf target/allure-results target/allure-report
mkdir -p target/allure-results
echo "✅ Directories prepared"
echo ""

# 4. Run tests
echo "Step 4: Running tests..."
docker-compose run --rm tests -Dtest=SimpleGridTest,EnhancedGridTests
TEST_EXIT_CODE=$?
echo ""

# 5. Copy results from container
echo "Step 5: Copying Allure results..."
# Results are auto-mounted via docker-compose volume
if [ -d "target/surefire-reports" ]; then
    echo "✅ Test results available"
else
    echo "⚠️  No test results found"
fi
echo ""

# 6. Stop Grid
echo "Step 6: Stopping Selenium Grid..."
docker-compose down
echo "✅ Grid stopped"
echo ""

# 7. Check if Allure is installed
if ! command -v allure &> /dev/null; then
    echo "=========================================="
    echo "⚠️  Allure CLI not installed"
    echo "=========================================="
    echo ""
    echo "To install Allure CLI:"
    echo ""
    echo "macOS (Homebrew):"
    echo "  brew install allure"
    echo ""
    echo "Or download from:"
    echo "  https://github.com/allure-framework/allure2/releases"
    echo ""
    echo "After installation, run:"
    echo "  allure serve target/allure-results"
    echo ""
    exit 0
fi

# 8. Generate and open report
echo "Step 7: Generating Allure report..."
if [ -d "target/allure-results" ] && [ "$(ls -A target/allure-results)" ]; then
    allure serve target/allure-results
else
    echo "⚠️  No Allure results found"
    echo "Results may not have been generated. Check if tests ran successfully."
fi

echo ""
echo "=========================================="
echo "Done!"
echo "=========================================="

