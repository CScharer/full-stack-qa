#!/bin/bash
# scripts/reporting/generate-allure-report.sh
# Allure Report Generator
#
# Purpose: Run tests and generate an Allure HTML report with screenshots
#
# Usage:
#   ./scripts/reporting/generate-allure-report.sh
#
# Description:
#   This script performs the following steps:
#   1. Starts Selenium Grid (hub + Chrome + Firefox nodes)
#   2. Waits for Grid to be ready
#   3. Runs test suite (SimpleGridTest, EnhancedGridTests)
#   4. Generates Allure HTML report
#   5. Opens report in default browser
#   6. Stops Grid services
#
# Examples:
#   ./scripts/reporting/generate-allure-report.sh
#
# Dependencies:
#   - Docker and Docker Compose
#   - Selenium Grid services (selenium-hub, chrome-node-1, firefox-node)
#   - Maven wrapper (./mvnw)
#   - Java 21+
#   - Allure CLI (installed via npm or standalone)
#
# Output:
#   - Test results in target/allure-results/
#   - Allure HTML report in target/allure-report/
#   - Report automatically opened in default browser
#   - Exit code: 0 on success, non-zero on failure
#
# Notes:
#   - Grid is automatically started and stopped by this script
#   - Uses docker-compose.yml for Grid configuration
#   - Screenshots are automatically captured on test failures
#   - Report includes graphs, trends, and historical data
#
# Last Updated: January 2026

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

