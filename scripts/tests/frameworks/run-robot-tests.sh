#!/bin/bash
# scripts/tests/frameworks/run-robot-tests.sh
# Robot Framework Test Runner
#
# Purpose: Run Robot Framework keyword-driven tests (Python)
#
# Usage:
#   ./scripts/tests/frameworks/run-robot-tests.sh [TEST_FILE]
#
# Parameters:
#   TEST_FILE     Optional: Specific Robot Framework test file to run
#                 (e.g., "HomePageTests.robot", "APITests.robot")
#                 If omitted, runs all Robot Framework tests
#
# Examples:
#   ./scripts/tests/frameworks/run-robot-tests.sh                    # Run all tests
#   ./scripts/tests/frameworks/run-robot-tests.sh HomePageTests.robot # Run specific test
#   ./scripts/tests/frameworks/run-robot-tests.sh APITests.robot      # Run API tests
#
# Dependencies:
#   - Python 3.13+
#   - Robot Framework (installed via Maven plugin)
#   - Maven wrapper (./mvnw)
#   - Java 21+ (for Maven)
#
# Output:
#   - Test results in target/robotframework-reports/
#   - HTML reports with detailed test execution information
#   - Exit code: 0 on success, non-zero on failure
#
# Notes:
#   - Uses Maven Robot Framework plugin for execution
#   - Test files located in src/test/robot/
#   - Human-readable keyword syntax
#   - Supports data-driven testing
#
# Last Updated: January 2026

set -e

TEST_FILE=${1:-""}

echo "🤖 Running Robot Framework tests..."
echo ""

if [ -z "$TEST_FILE" ]; then
    echo "📋 Running all Robot Framework tests..."
    ./mvnw test -Probot
else
    echo "📋 Running specific test file: $TEST_FILE"
    ./mvnw robotframework:run -DtestCasesDirectory=src/test/robot/$TEST_FILE
fi

echo ""
echo "✅ Robot Framework tests completed!"
echo "📊 Reports available in: target/robot-reports/"

