#!/bin/bash
# Generate Pipeline Summary for GitHub Actions
# Usage: ./scripts/ci/generate-pipeline-summary.sh <summary-file> [options...]
#
# Arguments:
#   summary-file  - Path to GitHub Actions step summary file (usually $GITHUB_STEP_SUMMARY)
#   code-changed  - Whether code changes were detected (true/false)
#   test-type     - Test type (fe-only, be-only, all)
#   selected-env  - Selected environments (comma-separated)
#   test-suite    - Test suite name
#   run-be-tests  - Whether BE tests should run (true/false)
#   be-test-mode  - BE test mode (all, smoke)
#   run-dev       - Whether DEV environment should run (true/false)
#   run-test      - Whether TEST environment should run (true/false)
#   run-prod      - Whether PROD environment should run (true/false)
#   be-env-dev    - Whether BE tests should run in DEV (true/false)
#   be-env-test   - Whether BE tests should run in TEST (true/false)
#   fe-dev-result - Result of test-fe-dev job (success/failure/cancelled/skipped)
#   fe-test-result - Result of test-fe-test job
#   fe-prod-result - Result of test-fe-prod job
#   be-dev-result  - Result of test-be-dev job
#   be-test-result - Result of test-be-test job
#
# Environment Variables (fallback if not provided as args):
#   GITHUB_SHA, GITHUB_REF_NAME, GITHUB_ACTOR

set -e

SUMMARY_FILE="${1:-$GITHUB_STEP_SUMMARY}"
CODE_CHANGED="${2:-false}"
TEST_TYPE="${3:-fe-only}"
SELECTED_ENV="${4:-}"
TEST_SUITE="${5:-}"
RUN_BE_TESTS="${6:-false}"
BE_TEST_MODE="${7:-}"
RUN_DEV="${8:-false}"
RUN_TEST="${9:-false}"
RUN_PROD="${10:-false}"
BE_ENV_DEV="${11:-false}"
BE_ENV_TEST="${12:-false}"
FE_DEV_RESULT="${13:-}"
FE_TEST_RESULT="${14:-}"
FE_PROD_RESULT="${15:-}"
BE_DEV_RESULT="${16:-}"
BE_TEST_RESULT="${17:-}"

# GitHub context (from environment or defaults)
GITHUB_SHA="${GITHUB_SHA:-unknown}"
GITHUB_REF_NAME="${GITHUB_REF_NAME:-unknown}"
GITHUB_ACTOR="${GITHUB_ACTOR:-unknown}"

# Write header
{
    echo "═══════════════════════════════════════════════════════════════"
    echo "## 📊 PIPELINE EXECUTION SUMMARY"
    echo "═══════════════════════════════════════════════════════════════"
    echo ""
} >> "$SUMMARY_FILE"

if [ "$CODE_CHANGED" = "true" ]; then
    {
        echo "**Change Type**: CODE CHANGES DETECTED"
        echo ""
        echo "**Test Type**: $TEST_TYPE"
        echo "**Environment Selection**: $SELECTED_ENV"
        echo "**Test Suite**: $TEST_SUITE"
    } >> "$SUMMARY_FILE"
    
    if [ "$RUN_BE_TESTS" = "true" ] && [ -n "$BE_TEST_MODE" ]; then
        echo "**BE Test Type**: $BE_TEST_MODE" >> "$SUMMARY_FILE"
    fi
    echo "" >> "$SUMMARY_FILE"

    echo "### FE Tests - Environments Tested:" >> "$SUMMARY_FILE"

    if [ "$RUN_DEV" = "true" ]; then
        if [ "$FE_DEV_RESULT" = "success" ]; then
            echo "- ✅ **DEV**: Tests passed, Deployed" >> "$SUMMARY_FILE"
        else
            echo "- ❌ **DEV**: ${FE_DEV_RESULT:-unknown}" >> "$SUMMARY_FILE"
        fi
    fi

    if [ "$RUN_TEST" = "true" ]; then
        if [ "$FE_TEST_RESULT" = "success" ]; then
            echo "- ✅ **TEST**: Tests passed, Deployed" >> "$SUMMARY_FILE"
        else
            echo "- ❌ **TEST**: ${FE_TEST_RESULT:-unknown}" >> "$SUMMARY_FILE"
        fi
    fi

    if [ "$RUN_PROD" = "true" ]; then
        if [ "$FE_PROD_RESULT" = "success" ]; then
            echo "- ✅ **PROD**: Tests passed, Deployed" >> "$SUMMARY_FILE"
        else
            echo "- ❌ **PROD**: ${FE_PROD_RESULT:-unknown}" >> "$SUMMARY_FILE"
        fi
    fi

    if [ "$RUN_BE_TESTS" = "true" ]; then
        echo "" >> "$SUMMARY_FILE"
        echo "### BE Tests:" >> "$SUMMARY_FILE"
        
        if [ "$RUN_DEV" = "true" ] && [ "$BE_ENV_DEV" = "true" ]; then
            if [ "$BE_DEV_RESULT" = "success" ]; then
                echo "- ✅ **BE (DEV)**: Tests passed" >> "$SUMMARY_FILE"
            else
                echo "- ❌ **BE (DEV)**: ${BE_DEV_RESULT:-unknown}" >> "$SUMMARY_FILE"
            fi
        fi

        if [ "$RUN_TEST" = "true" ] && [ "$BE_ENV_TEST" = "true" ]; then
            if [ "$BE_TEST_RESULT" = "success" ]; then
                echo "- ✅ **BE (TEST)**: Tests passed" >> "$SUMMARY_FILE"
            else
                echo "- ❌ **BE (TEST)**: ${BE_TEST_RESULT:-unknown}" >> "$SUMMARY_FILE"
            fi
        fi
    fi

    {
        echo ""
        echo "### Reports:"
        echo "- 📊 Combined Allure Report: Download artifact 'allure-report-combined-all-environments'"
        echo "- 📊 Individual Environment Reports: Download 'allure-report-{env}' artifacts"
    } >> "$SUMMARY_FILE"
    if [ "$RUN_BE_TESTS" = "true" ]; then
        echo "- 📊 BE Test Results: Download '*-be-results' artifacts" >> "$SUMMARY_FILE"
    fi
else
    {
        echo "**Change Type**: DOCUMENTATION-ONLY"
        echo ""
        echo "⏭️  Skipped: Build, tests, quality checks (not needed for docs)"
        echo "✅ Documentation changes processed successfully"
    } >> "$SUMMARY_FILE"
fi

{
    echo ""
    echo "**Commit**: $GITHUB_SHA"
    echo "**Branch**: $GITHUB_REF_NAME"
    echo "**Author**: $GITHUB_ACTOR"
    echo ""
    echo "═══════════════════════════════════════════════════════════════"
} >> "$SUMMARY_FILE"

echo "✅ Pipeline summary generated successfully"

