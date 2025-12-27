#!/bin/bash
# Prepare Combined Allure Results
# Usage: ./scripts/ci/prepare-combined-allure-results.sh [source-dir] [target-dir]
#
# Arguments:
#   source-dir  - Directory containing all test results (default: all-test-results)
#   target-dir  - Directory where combined Allure results will be stored (default: allure-results-combined)
#
# This script:
# 1. Merges Allure results from all environments
# 2. Verifies merged results
# 3. Converts framework results (Cypress, Playwright, Robot, Vibium) to Allure format
# 4. Adds environment labels to prevent deduplication
# 5. Preserves history from previous reports
# 6. Creates executor.json and categories.json files

set -e

SOURCE_DIR="${1:-all-test-results}"
TARGET_DIR="${2:-allure-results-combined}"

echo "📊 Preparing combined Allure results..."
echo "   Source directory: $SOURCE_DIR"
echo "   Target directory: $TARGET_DIR"
echo ""

# Step 1: Merge Allure results
echo "🔄 Step 1: Merging Allure results..."
chmod +x scripts/ci/merge-allure-results.sh
./scripts/ci/merge-allure-results.sh

# Step 2: Verify merged results
echo ""
echo "✅ Step 2: Verifying merged results..."
chmod +x scripts/ci/verify-merged-allure-results.sh
./scripts/ci/verify-merged-allure-results.sh "$TARGET_DIR"

# Step 3: Convert framework results to Allure format
echo ""
echo "🔄 Step 3: Converting framework test results to Allure format..."

# Determine environment from artifact paths
ENV_FOR_CONVERSION=""
if [ -d "$SOURCE_DIR/results-dev" ] || ( [ -d "$SOURCE_DIR/cypress-results" ] && find "$SOURCE_DIR/cypress-results" -name "*dev*" 2>/dev/null | head -1 | grep -q . ); then
    ENV_FOR_CONVERSION="dev"
elif [ -d "$SOURCE_DIR/results-test" ] || find "$SOURCE_DIR" -name "*test*" 2>/dev/null | head -1 | grep -q .; then
    ENV_FOR_CONVERSION="test"
elif [ -d "$SOURCE_DIR/results-prod" ] || find "$SOURCE_DIR" -name "*prod*" 2>/dev/null | head -1 | grep -q .; then
    ENV_FOR_CONVERSION="prod"
fi

if [ -n "$ENV_FOR_CONVERSION" ]; then
    echo "   Detected environment: $ENV_FOR_CONVERSION"
fi

# Convert Cypress results
if [ -d "$SOURCE_DIR/cypress-results" ]; then
    echo "   Converting Cypress results..."
    chmod +x scripts/ci/convert-cypress-to-allure.sh
    find "$SOURCE_DIR/cypress-results" -type d -name "cypress" | while read cypress_dir; do
        ./scripts/ci/convert-cypress-to-allure.sh "$TARGET_DIR" "$cypress_dir" "$ENV_FOR_CONVERSION" || true
    done
fi

# Convert Playwright results
if [ -d "$SOURCE_DIR/playwright-results" ]; then
    echo "   Converting Playwright results..."
    chmod +x scripts/ci/convert-playwright-to-allure.sh
    find "$SOURCE_DIR/playwright-results" -type d -name "test-results" | while read playwright_dir; do
        ./scripts/ci/convert-playwright-to-allure.sh "$TARGET_DIR" "$playwright_dir" "$ENV_FOR_CONVERSION" || true
    done
fi

# Convert Robot Framework results
if [ -d "$SOURCE_DIR/robot-results" ]; then
    echo "   Converting Robot Framework results..."
    chmod +x scripts/ci/convert-robot-to-allure.sh
    find "$SOURCE_DIR/robot-results" -type d \( -name "robot-reports" -o -name "target" \) | while read robot_dir; do
        if [ -f "$robot_dir/output.xml" ] || [ -f "$robot_dir/robot-reports/output.xml" ]; then
            OUTPUT_DIR="$robot_dir"
            if [ -f "$robot_dir/robot-reports/output.xml" ]; then
                OUTPUT_DIR="$robot_dir/robot-reports"
            fi
            ./scripts/ci/convert-robot-to-allure.sh "$TARGET_DIR" "$OUTPUT_DIR" "$ENV_FOR_CONVERSION" || true
        fi
    done
fi

# Convert Vibium results
if [ -d "$SOURCE_DIR/vibium-results" ]; then
    echo "   Converting Vibium results..."
    chmod +x scripts/ci/convert-vibium-to-allure.sh
    find "$SOURCE_DIR/vibium-results" -type d -name "test-results" | while read vibium_dir; do
        ./scripts/ci/convert-vibium-to-allure.sh "$TARGET_DIR" "$vibium_dir" "$ENV_FOR_CONVERSION" || true
    done
fi

# Step 4: Add environment labels
echo ""
echo "🏷️  Step 4: Adding environment labels..."
chmod +x scripts/ci/add-environment-labels.sh
./scripts/ci/add-environment-labels.sh "$TARGET_DIR" "$SOURCE_DIR"

# Step 5: Preserve history from previous report
echo ""
echo "📊 Step 5: Preserving history from previous report..."
chmod +x scripts/ci/preserve-allure-history.sh
./scripts/ci/preserve-allure-history.sh "$TARGET_DIR" "allure-report-combined"

# Step 6: Create executor.json
echo ""
echo "⚙️  Step 6: Creating executor.json..."
chmod +x scripts/ci/create-allure-executor.sh
./scripts/ci/create-allure-executor.sh "$TARGET_DIR"

# Step 7: Create categories.json
echo ""
echo "📋 Step 7: Creating categories.json..."
chmod +x scripts/ci/create-allure-categories.sh
./scripts/ci/create-allure-categories.sh "$TARGET_DIR"

echo ""
echo "✅ Combined Allure results prepared successfully!"
echo "   Results directory: $TARGET_DIR"
RESULT_COUNT=$(find "$TARGET_DIR" -name "*-result.json" 2>/dev/null | wc -l | tr -d ' ')
echo "   Total result files: $RESULT_COUNT"

