#!/bin/bash
# Run a specific test method
# Usage: ./scripts/run-specific-test.sh Scenarios Google

set -e

TEST_CLASS=${1:-"Scenarios"}
TEST_METHOD=${2:-""}

if [ -z "$TEST_METHOD" ]; then
    echo "Usage: $0 <TestClass> <TestMethod>"
    echo "Example: $0 Scenarios Google"
    exit 1
fi

echo "🧪 Running: ${TEST_CLASS}#${TEST_METHOD}"
echo "=========================================="

./mvnw test -Dtest="${TEST_CLASS}#${TEST_METHOD}" -DfailIfNoTests=false

echo ""
echo "✅ Test completed!"
