#!/bin/bash
# CJS QA Test Runner Script
# Usage: ./scripts/run-tests.sh [TEST_SUITE] [BROWSER]
# Example: ./scripts/run-tests.sh Scenarios chrome

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
  -Dtest=$TEST_SUITE \
  -Dbrowser=$BROWSER \
  -DfailIfNoTests=false

echo ""
echo "✅ Tests completed!"
echo "📊 Reports available at: target/surefire-reports/"
