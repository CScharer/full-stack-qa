#!/bin/bash
# scripts/ci/verify-test-results.sh
# Logic to count failures in JUnit XML files

set -e

RESULTS_DIR=$1
ENVIRONMENT=$2

if [ ! -d "$RESULTS_DIR" ]; then
    echo "⚠️  $RESULTS_DIR not found, skipping verification"
    exit 0
fi

TOTAL_FAILURES=0
TOTAL_ERRORS=0

echo "🔍 Verifying test results for $ENVIRONMENT..."

# Use find to handle nested directories
while IFS= read -r xml_file; do
    echo "  Checking: $xml_file"
    # Basic grep to extract counts (more portable than complex xml parsers)
    failures=$(grep -oP 'failures="\K[0-9]+' "$xml_file" | head -1 || echo "0")
    errors=$(grep -oP 'errors="\K[0-9]+' "$xml_file" | head -1 || echo "0")
    TOTAL_FAILURES=$((TOTAL_FAILURES + ${failures:-0}))
    TOTAL_ERRORS=$((TOTAL_ERRORS + ${errors:-0}))
done < <(find "$RESULTS_DIR" -name "TEST-*.xml")

echo "📊 Results for $ENVIRONMENT: Failures=$TOTAL_FAILURES, Errors=$TOTAL_ERRORS"

if [ "$TOTAL_FAILURES" -gt "0" ] || [ "$TOTAL_ERRORS" -gt "0" ]; then
    echo "❌ Tests failed in $ENVIRONMENT - deployment blocked"
    echo "tests_passed=false" >> $GITHUB_OUTPUT
    exit 1
else
    echo "✅ All tests passed in $ENVIRONMENT - proceeding"
    echo "tests_passed=true" >> $GITHUB_OUTPUT
fi
