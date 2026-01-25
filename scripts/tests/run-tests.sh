#!/bin/bash
# scripts/tests/run-tests.sh
# CJS QA Test Runner Script
#
# Purpose: Run test suites with specified browser using Maven
#
# Usage:
#   ./scripts/tests/run-tests.sh [TEST_SUITE] [BROWSER]
#
# Parameters:
#   TEST_SUITE    Test suite name (default: "Scenarios")
#   BROWSER       Browser to use: chrome, firefox, edge (default: "chrome")
#
# Examples:
#   ./scripts/tests/run-tests.sh                    # Run Scenarios suite with Chrome
#   ./scripts/tests/run-tests.sh Scenarios firefox # Run Scenarios suite with Firefox
#   ./scripts/tests/run-tests.sh Scenarios edge    # Run Scenarios suite with Edge
#
# Dependencies:
#   - Maven wrapper (./mvnw)
#   - Java 21+
#   - TestNG test suite configured in pom.xml
#
# Output:
#   - Test results in target/surefire-reports/
#   - Exit code: 0 on success, non-zero on failure
#
# Last Updated: January 2026

set -e

echo "🧪 CJS QA Automated Tests"
echo "========================="
echo ""

# Default values
TEST_SUITE=${1:-"Scenarios"}
BROWSER=${2:-"chrome"}

echo "Test Suite: $TEST_SUITE"
echo "Browser: $BROWSER"
echo ""

# Run tests
./mvnw clean test \
  -Dsurefire.skip=false \
  -Dtest=$TEST_SUITE \
  -Dbrowser=$BROWSER \
  -DfailIfNoTests=false

echo ""
echo "✅ Tests completed!"
echo "📊 Reports available at: target/surefire-reports/"
