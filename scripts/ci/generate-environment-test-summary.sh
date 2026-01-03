#!/bin/bash
# Generate Test Summary for Single Environment
# Usage: ./scripts/ci/generate-environment-test-summary.sh <environment> <base-url> <test-suite> [test-results-dir] [summary-file]
#
# Arguments:
#   environment      - Environment name (dev, test, prod)
#   base-url         - Base URL for the environment
#   test-suite       - Test suite name
#   test-results-dir - Directory containing test results (default: test-results)
#   summary-file     - Path to GitHub Actions step summary file (default: $GITHUB_STEP_SUMMARY)
#
# This script:
# 1. Parses test results from multiple formats (Maven Surefire, Allure JSON, Playwright JUnit XML, Cypress JSON, Robot XML, Vibium JSON)
# 2. Counts total tests, passed, failed, and errors
# 3. Generates a markdown summary for GitHub Actions

set -e

ENVIRONMENT="${1:-}"
BASE_URL="${2:-}"
TEST_SUITE="${3:-}"
TEST_RESULTS_DIR="${4:-test-results}"
SUMMARY_FILE="${5:-$GITHUB_STEP_SUMMARY}"

if [ -z "$ENVIRONMENT" ] || [ -z "$BASE_URL" ] || [ -z "$TEST_SUITE" ]; then
    echo "❌ Error: Environment, base URL, and test suite are required"
    echo "Usage: $0 <environment> <base-url> <test-suite> [test-results-dir] [summary-file]"
    exit 1
fi

# Write header
{
    echo "## 🧪 Test Results - $ENVIRONMENT Environment"
    echo ""
    echo "**Environment**: $ENVIRONMENT"
    echo "**Base URL**: $BASE_URL"
    echo "**Test Suite**: $TEST_SUITE"
    echo ""
} >> "$SUMMARY_FILE"

# Debug: Show what files were downloaded
echo "🔍 Debug: Checking downloaded artifacts..."
echo "Test results directory structure:" >> "$SUMMARY_FILE"
if [ -d "$TEST_RESULTS_DIR" ]; then
    find "$TEST_RESULTS_DIR" -type f -name "*.xml" -o -name "*.json" 2>/dev/null | head -20 | while IFS= read -r file; do
        echo "  - $file" >> "$SUMMARY_FILE"
    done || echo "  No test result files found" >> "$SUMMARY_FILE"
else
    echo "  ⚠️ test-results directory not found (artifacts may not have been uploaded)" >> "$SUMMARY_FILE"
fi
{
    echo ""
} >> "$SUMMARY_FILE"

# Parse test results from multiple formats
TOTAL_TESTS=0
TOTAL_FAILURES=0
TOTAL_ERRORS=0
TOTAL_PASSED=0

# Check if test-results directory exists before parsing
if [ ! -d "$TEST_RESULTS_DIR" ]; then
    echo "⚠️  test-results directory not found, no test results to parse"
    {
        echo "### Results:"
        echo "- Total Tests: 0"
        echo "- Passed: 0"
        echo "- Failed: 0"
        echo "- Errors: 0"
        echo ""
        echo "⚠️ **Status**: NO TEST RESULTS FOUND (artifacts may not have been uploaded)"
    } >> "$SUMMARY_FILE"
    exit 0
fi

# Note: Artifacts are merged with merge-multiple: true, which preserves artifact names as directories
# Structure: test-results/{artifact-name}/... (e.g., test-results/cypress-results-dev/..., test-results/playwright-results-dev/playwright/test-results/...)
# So we need to search recursively through all subdirectories

# 1. Parse Maven Surefire XML files (TEST-*.xml) - recursively search
echo "📊 Parsing Maven Surefire XML results..."
while IFS= read -r xml_file; do
    if [ -f "$xml_file" ]; then
        tests=$(grep -oP 'tests="\K[0-9]+' "$xml_file" | head -1 || echo "0")
        failures=$(grep -oP 'failures="\K[0-9]+' "$xml_file" | head -1 || echo "0")
        errors=$(grep -oP 'errors="\K[0-9]+' "$xml_file" | head -1 || echo "0")
        
        TOTAL_TESTS=$((TOTAL_TESTS + ${tests:-0}))
        TOTAL_FAILURES=$((TOTAL_FAILURES + ${failures:-0}))
        TOTAL_ERRORS=$((TOTAL_ERRORS + ${errors:-0}))
        # Calculate passed tests: total - failures - errors
        passed=$((tests - failures - errors))
        if [ "$passed" -gt 0 ]; then
            TOTAL_PASSED=$((TOTAL_PASSED + passed))
        fi
    fi
done < <(find "$TEST_RESULTS_DIR" -type f -name "TEST-*.xml" 2>/dev/null || true)

# 2. Parse Allure result JSON files (*-result.json) - BUT ONLY if no original test files found
# Note: Allure JSON files are converted versions of original test results
# We count from original files (TEST-*.xml, junit.xml, etc.) to avoid double-counting
# Only count Allure JSON if we haven't found any original test result files
if [ "$TOTAL_TESTS" -eq 0 ]; then
    echo "📊 Parsing Allure JSON results (fallback - no original test files found)..."
    while IFS= read -r json_file; do
        if [ -f "$json_file" ]; then
            # Allure result JSON structure: { "status": "passed|failed|broken", ... }
            status=$(grep -oP '"status"\s*:\s*"\K[^"]+' "$json_file" | head -1 || echo "")
            if [ -n "$status" ]; then
                TOTAL_TESTS=$((TOTAL_TESTS + 1))
                if [ "$status" = "failed" ] || [ "$status" = "broken" ]; then
                    TOTAL_FAILURES=$((TOTAL_FAILURES + 1))
                else
                    TOTAL_PASSED=$((TOTAL_PASSED + 1))
                fi
            fi
        fi
    done < <(find "$TEST_RESULTS_DIR" -type f -name "*-result.json" 2>/dev/null || true)
else
    echo "📊 Skipping Allure JSON results (already counted from original test files)"
fi

# 3. Parse Playwright JUnit XML files (junit.xml) - recursively search
# Playwright files are in: playwright-results-{env}/playwright/test-results/junit.xml
echo "📊 Parsing Playwright JUnit XML results..."
while IFS= read -r xml_file; do
    if [ -f "$xml_file" ]; then
        # Playwright JUnit XML structure: <testsuites tests="N" failures="M" errors="K">...
        # Count from testsuites element (aggregate) or sum individual testsuite elements
        tests=$(grep -oP '<testsuites[^>]*tests="\K[0-9]+' "$xml_file" | head -1 || echo "0")
        failures=$(grep -oP '<testsuites[^>]*failures="\K[0-9]+' "$xml_file" | head -1 || echo "0")
        errors=$(grep -oP '<testsuites[^>]*errors="\K[0-9]+' "$xml_file" | head -1 || echo "0")
        
        # If no testsuites aggregate, count from individual testsuite elements
        if [ "$tests" = "0" ]; then
            tests=$(grep -oP '<testsuite[^>]*tests="\K[0-9]+' "$xml_file" | awk '{sum+=$1} END {print sum+0}' || echo "0")
            failures=$(grep -oP '<testsuite[^>]*failures="\K[0-9]+' "$xml_file" | awk '{sum+=$1} END {print sum+0}' || echo "0")
            errors=$(grep -oP '<testsuite[^>]*errors="\K[0-9]+' "$xml_file" | awk '{sum+=$1} END {print sum+0}' || echo "0")
        fi
        
        if [ -n "$tests" ] && [ "$tests" != "0" ]; then
            TOTAL_TESTS=$((TOTAL_TESTS + ${tests:-0}))
            TOTAL_FAILURES=$((TOTAL_FAILURES + ${failures:-0}))
            TOTAL_ERRORS=$((TOTAL_ERRORS + ${errors:-0}))
            # Calculate passed tests: total - failures - errors
            passed=$((tests - failures - errors))
            if [ "$passed" -gt 0 ]; then
                TOTAL_PASSED=$((TOTAL_PASSED + passed))
            fi
        fi
    fi
done < <(find "$TEST_RESULTS_DIR" -type f -name "junit.xml" -o -name "*junit*.xml" 2>/dev/null | grep -v "target/surefire-reports" || true)

# 4. Parse Cypress result JSON files (mochawesome.json or cypress-results.json) - recursively search
# Cypress files are in: cypress-results-{env}/cypress/cypress/results/cypress-results.json
echo "📊 Parsing Cypress JSON results..."
while IFS= read -r json_file; do
    if [ -f "$json_file" ]; then
        # Cypress results structure varies, try to extract stats
        tests=$(grep -oP '"tests"\s*:\s*\K[0-9]+' "$json_file" | head -1 || echo "0")
        failures=$(grep -oP '"failures"\s*:\s*\K[0-9]+' "$json_file" | head -1 || echo "0")
        if [ -n "$tests" ] && [ "$tests" != "0" ]; then
            TOTAL_TESTS=$((TOTAL_TESTS + ${tests:-0}))
            TOTAL_FAILURES=$((TOTAL_FAILURES + ${failures:-0}))
            # Calculate passed tests: total - failures
            passed=$((tests - failures))
            if [ "$passed" -gt 0 ]; then
                TOTAL_PASSED=$((TOTAL_PASSED + passed))
            fi
        fi
    fi
done < <(find "$TEST_RESULTS_DIR" -type f \( -name "mochawesome.json" -o -name "cypress-results.json" \) 2>/dev/null || true)

# 5. Parse Robot Framework XML files (output.xml) - recursively search
# Robot files are in: robot-results-{env}/target/robot-reports/output.xml
echo "📊 Parsing Robot Framework XML results..."
while IFS= read -r xml_file; do
    if [ -f "$xml_file" ]; then
        # Robot Framework output.xml structure: <robot><statistics><total><stat pass="X" fail="Y">...
        # Extract pass and fail counts from <total><stat> element
        pass=$(grep -oP '<total><stat[^>]*pass="\K[0-9]+' "$xml_file" | head -1 || echo "0")
        fail=$(grep -oP '<total><stat[^>]*fail="\K[0-9]+' "$xml_file" | head -1 || echo "0")
        if [ -n "$pass" ] || [ -n "$fail" ]; then
            total=$((pass + fail))
            if [ "$total" -gt 0 ]; then
                TOTAL_TESTS=$((TOTAL_TESTS + total))
                TOTAL_FAILURES=$((TOTAL_FAILURES + ${fail:-0}))
                TOTAL_PASSED=$((TOTAL_PASSED + ${pass:-0}))
            fi
        fi
    fi
done < <(find "$TEST_RESULTS_DIR" -type f -name "output.xml" 2>/dev/null || true)

# 6. Parse Vibium/Vitest result JSON files (vitest-results.json) - recursively search
# Vibium files are in: vibium-results-{env}/vibium/.vitest/vitest-results.json
echo "📊 Parsing Vibium/Vitest JSON results..."
while IFS= read -r json_file; do
    if [ -f "$json_file" ]; then
        # Vitest results structure: { "numTotalTests": N, "numPassedTests": M, "numFailedTests": K, ... }
        total=$(grep -oP '"numTotalTests"\s*:\s*\K[0-9]+' "$json_file" | head -1 || echo "0")
        passed=$(grep -oP '"numPassedTests"\s*:\s*\K[0-9]+' "$json_file" | head -1 || echo "0")
        failed=$(grep -oP '"numFailedTests"\s*:\s*\K[0-9]+' "$json_file" | head -1 || echo "0")
        if [ -n "$total" ] && [ "$total" != "0" ]; then
            TOTAL_TESTS=$((TOTAL_TESTS + ${total:-0}))
            TOTAL_FAILURES=$((TOTAL_FAILURES + ${failed:-0}))
            TOTAL_PASSED=$((TOTAL_PASSED + ${passed:-0}))
        fi
    fi
done < <(find "$TEST_RESULTS_DIR" -type f -name "vitest-results.json" 2>/dev/null || true)

# Final validation: ensure passed tests are calculated correctly
# This is a safety check in case any format was missed
calculated_passed=$((TOTAL_TESTS - TOTAL_FAILURES - TOTAL_ERRORS))
if [ "$calculated_passed" -ne "$TOTAL_PASSED" ] && [ "$TOTAL_TESTS" -gt 0 ]; then
    echo "⚠️  Warning: Passed count mismatch. Recalculating: $TOTAL_PASSED -> $calculated_passed"
    TOTAL_PASSED=$calculated_passed
fi

# Write results to summary
{
    echo "### Results:"
    echo "- Total Tests: $TOTAL_TESTS"
    echo "- Passed: $TOTAL_PASSED"
    echo "- Failed: $TOTAL_FAILURES"
    echo "- Errors: $TOTAL_ERRORS"
    echo ""
} >> "$SUMMARY_FILE"

# Determine status
if [ "$TOTAL_FAILURES" -gt "0" ] || [ "$TOTAL_ERRORS" -gt "0" ]; then
    echo "❌ **Status**: FAILED" >> "$SUMMARY_FILE"
elif [ "$TOTAL_TESTS" -eq "0" ]; then
    echo "⚠️ **Status**: NO TESTS FOUND" >> "$SUMMARY_FILE"
else
    echo "✅ **Status**: PASSED" >> "$SUMMARY_FILE"
fi

echo "Job summary generated at run-time" >> "$SUMMARY_FILE"

echo "✅ Test summary generated successfully"
echo "   Total Tests: $TOTAL_TESTS"
echo "   Passed: $TOTAL_PASSED"
echo "   Failed: $TOTAL_FAILURES"
echo "   Errors: $TOTAL_ERRORS"

