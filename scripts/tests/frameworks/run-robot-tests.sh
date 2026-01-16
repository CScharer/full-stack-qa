#!/bin/bash

# Run Robot Framework tests
# Usage: ./scripts/run-robot-tests.sh [test-file]
# Example: ./scripts/run-robot-tests.sh HomePageTests.robot

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

