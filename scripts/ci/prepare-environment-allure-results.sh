#!/bin/bash
# Prepare Allure Results for Single Environment
# Usage: ./scripts/ci/prepare-environment-allure-results.sh <environment> [test-results-dir] [allure-results-dir]
#
# Arguments:
#   environment        - Environment name (dev, test, prod)
#   test-results-dir   - Directory containing downloaded test results (default: test-results)
#   allure-results-dir - Directory where Allure results will be stored (default: allure-results)
#
# This script:
# 1. Copies Allure result files from test-results to allure-results
# 2. Handles missing directories gracefully
# 3. Reports counts of results and screenshots

set -e

ENVIRONMENT="${1:-}"
TEST_RESULTS_DIR="${2:-test-results}"
ALLURE_RESULTS_DIR="${3:-allure-results}"

if [ -z "$ENVIRONMENT" ]; then
    echo "❌ Error: Environment name is required"
    echo "Usage: $0 <environment> [test-results-dir] [allure-results-dir]"
    exit 1
fi

echo "📊 Merging Allure results for $ENVIRONMENT environment..."
echo "   Test results directory: $TEST_RESULTS_DIR"
echo "   Allure results directory: $ALLURE_RESULTS_DIR"
echo ""

# Create Allure results directory
mkdir -p "$ALLURE_RESULTS_DIR"

# Copy test results from this environment (handle missing directories gracefully)
if [ -d "$TEST_RESULTS_DIR" ]; then
    echo "🔄 Copying Allure result files..."
    
    # Copy result JSON files
    result_count=$(find "$TEST_RESULTS_DIR" -name "*-result.json" -exec cp {} "$ALLURE_RESULTS_DIR/" \; 2>&1 | wc -l | tr -d ' ' || echo "0")
    
    # Copy container JSON files
    container_count=$(find "$TEST_RESULTS_DIR" -name "*-container.json" -exec cp {} "$ALLURE_RESULTS_DIR/" \; 2>&1 | wc -l | tr -d ' ' || echo "0")
    
    # Copy attachment files
    attachment_count=$(find "$TEST_RESULTS_DIR" -name "*-attachment.*" -exec cp {} "$ALLURE_RESULTS_DIR/" \; 2>&1 | wc -l | tr -d ' ' || echo "0")
    
    echo "   ✅ Copied files:"
    echo "      - Result files: $result_count"
    echo "      - Container files: $container_count"
    echo "      - Attachment files: $attachment_count"
else
    echo "⚠️  test-results directory not found, creating empty directory"
    mkdir -p "$TEST_RESULTS_DIR"
fi

# Count final results
final_result_count=$(find "$ALLURE_RESULTS_DIR" -name "*-result.json" 2>/dev/null | wc -l | tr -d ' ')
final_screenshot_count=$(find "$ALLURE_RESULTS_DIR" -name "*.png" 2>/dev/null | wc -l | tr -d ' ')

echo ""
echo "✅ Merge complete for $ENVIRONMENT!"
echo "   Results: $final_result_count"
echo "   Screenshots: $final_screenshot_count"

