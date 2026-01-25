#!/bin/bash
# scripts/tests/run-specific-test.sh
# Run Specific Test Method
#
# Purpose: Run a specific test method from a test class using Maven
#
# Usage:
#   ./scripts/tests/run-specific-test.sh <TEST_CLASS> <TEST_METHOD>
#
# Parameters:
#   TEST_CLASS    Test class name (e.g., "Scenarios")
#   TEST_METHOD   Test method name (e.g., "Google", "Microsoft")
#
# Examples:
#   ./scripts/tests/run-specific-test.sh Scenarios Google
#   ./scripts/tests/run-specific-test.sh Scenarios Microsoft
#   ./scripts/tests/run-specific-test.sh HomePageTests testHome
#
# Dependencies:
#   - Maven wrapper (./mvnw)
#   - Java 21+
#   - Test class and method must exist
#
# Output:
#   - Test results in target/surefire-reports/
#   - Exit code: 0 on success, 1 on failure or missing parameters
#
# Last Updated: January 2026

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

./mvnw test -Dmaven.test.skip=false -Dtest="${TEST_CLASS}#${TEST_METHOD}" -DfailIfNoTests=false

echo ""
echo "✅ Test completed!"
