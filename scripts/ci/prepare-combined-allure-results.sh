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
echo "   Note: This merges TestNG-based tests (Smoke, Grid, Mobile, Responsive, Selenide)"
echo "   Framework-specific conversions happen in Step 3"
chmod +x scripts/ci/merge-allure-results.sh
./scripts/ci/merge-allure-results.sh

# Debug: Check if Selenide results were merged
echo ""
echo "🔍 Checking for Selenide results in merged results..."
SELENIDE_COUNT=$(find "$TARGET_DIR" -name "*-result.json" -exec grep -l "HomePage\|Selenide" {} \; 2>/dev/null | wc -l | tr -d ' ')
if [ "$SELENIDE_COUNT" -gt 0 ]; then
    echo "   ✅ Found $SELENIDE_COUNT Selenide test result(s) in merged results"
else
    echo "   ⚠️  No Selenide results found in merged results"
    echo "   🔍 Checking for selenide-results artifacts in source..."
    SELENIDE_IN_SOURCE=$(find "$SOURCE_DIR" -path "*/selenide-results-*/*" -name "*-result.json" 2>/dev/null | wc -l | tr -d ' ')
    if [ "$SELENIDE_IN_SOURCE" -gt 0 ]; then
        echo "   ✅ Found $SELENIDE_IN_SOURCE Selenide result file(s) in artifacts"
        echo "   📂 Selenide artifact locations:"
        find "$SOURCE_DIR" -path "*/selenide-results-*" -type d 2>/dev/null | head -5 | while read d; do
            count=$(find "$d" -name "*-result.json" 2>/dev/null | wc -l | tr -d ' ')
            echo "      - $d ($count result files)"
        done
        echo "   ⚠️  These should have been merged by merge-allure-results.sh"
        echo "   💡 Checking if they're in target/allure-results/ directories..."
        find "$SOURCE_DIR" -path "*/selenide-results-*/target/allure-results/*-result.json" 2>/dev/null | head -3 | while read f; do
            echo "      Found: $f"
        done || echo "      (none found in target/allure-results/)"
    else
        echo "   ⚠️  No Selenide result files found in artifacts"
        echo "   💡 Possible causes:"
        echo "      - Selenide tests didn't run (check if selenide-tests job executed)"
        echo "      - Artifacts weren't uploaded (check selenide-results-* artifacts)"
        echo "      - Results are in a different location"
        echo "   🔍 Checking for selenide-results directories:"
        find "$SOURCE_DIR" -type d -name "*selenide*" 2>/dev/null | head -5 | while read d; do
            echo "      📁 $d"
        done || echo "      (no selenide directories found)"
    fi
fi

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
    echo "   🔍 Inspecting Cypress artifact contents..."
    echo "   📂 Files in cypress-results:"
    find "$SOURCE_DIR/cypress-results" -type f 2>/dev/null | head -10 | while read f; do 
        size=$(du -h "$f" 2>/dev/null | cut -f1)
        echo "      - $f ($size)"
    done || echo "      (no files found)"
    echo "   📁 Directories in cypress-results:"
    find "$SOURCE_DIR/cypress-results" -type d 2>/dev/null | head -10 | while read d; do 
        echo "      📁 $d"
    done || echo "      (no directories found)"
    
    chmod +x scripts/ci/convert-cypress-to-allure.sh
    # Try to find result JSON files in multiple locations
    # Look for: mochawesome.json, cypress-results.json, or results/cypress-results.json
    CYPRESS_FOUND=0
    # Use a more reliable method to find JSON files
    json_file=$(find "$SOURCE_DIR/cypress-results" \( -name "mochawesome.json" -o -name "cypress-results.json" \) 2>/dev/null | head -1)
    if [ -n "$json_file" ] && [ -f "$json_file" ]; then
        echo "   ✅ Found Cypress result file: $json_file"
        json_dir=$(dirname "$json_file")
        ./scripts/ci/convert-cypress-to-allure.sh "$TARGET_DIR" "$json_dir" "$ENV_FOR_CONVERSION" || true
        CYPRESS_FOUND=1
    elif [ -d "$SOURCE_DIR/cypress-results/cypress/results" ]; then
        echo "   ✅ Found Cypress results directory: $SOURCE_DIR/cypress-results/cypress/results"
        ./scripts/ci/convert-cypress-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/cypress-results/cypress/results" "$ENV_FOR_CONVERSION" || true
        CYPRESS_FOUND=1
    elif [ -d "$SOURCE_DIR/cypress-results/cypress" ]; then
        echo "   ✅ Found Cypress directory: $SOURCE_DIR/cypress-results/cypress"
        ./scripts/ci/convert-cypress-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/cypress-results/cypress" "$ENV_FOR_CONVERSION" || true
        CYPRESS_FOUND=1
    else
        echo "   🔍 Trying root cypress-results directory as fallback..."
        ./scripts/ci/convert-cypress-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/cypress-results" "$ENV_FOR_CONVERSION" || true
    fi
    if [ "$CYPRESS_FOUND" -eq 0 ]; then
        echo "   ⚠️  No Cypress result JSON files found"
        echo "   💡 Possible causes:"
        echo "      - Cypress tests didn't run"
        echo "      - after:run hook didn't execute"
        echo "      - Result file in different location"
    fi
fi

# Convert Playwright results
if [ -d "$SOURCE_DIR/playwright-results" ]; then
    echo "   Converting Playwright results..."
    chmod +x scripts/ci/convert-playwright-to-allure.sh
    # Try multiple locations: test-results directory, playwright-results root, or any directory with results.json
    if [ -d "$SOURCE_DIR/playwright-results/test-results" ]; then
        ./scripts/ci/convert-playwright-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/playwright-results/test-results" "$ENV_FOR_CONVERSION" || true
    fi
    # Also try the root playwright-results directory
    if find "$SOURCE_DIR/playwright-results" -name "results.json" 2>/dev/null | head -1 | read results_file; then
        results_dir=$(dirname "$results_file")
        ./scripts/ci/convert-playwright-to-allure.sh "$TARGET_DIR" "$results_dir" "$ENV_FOR_CONVERSION" || true
    fi
fi

# Convert Robot Framework results
if [ -d "$SOURCE_DIR/robot-results" ]; then
    echo "   Converting Robot Framework results..."
    chmod +x scripts/ci/convert-robot-to-allure.sh
    # Try to find output.xml in various locations
    if find "$SOURCE_DIR/robot-results" -name "output.xml" 2>/dev/null | head -1 | read output_xml; then
        output_dir=$(dirname "$output_xml")
        ./scripts/ci/convert-robot-to-allure.sh "$TARGET_DIR" "$output_dir" "$ENV_FOR_CONVERSION" || true
    else
        # Fallback: try common directory names
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
fi

# Convert Vibium results
if [ -d "$SOURCE_DIR/vibium-results" ]; then
    echo "   Converting Vibium results..."
    echo "   🔍 Searching for Vibium result files..."
    echo "   📂 Contents of vibium-results:"
    find "$SOURCE_DIR/vibium-results" -type f 2>/dev/null | head -10 | while read f; do 
        size=$(du -h "$f" 2>/dev/null | cut -f1)
        echo "      - $f ($size)"
    done || echo "      (no files found)"
    find "$SOURCE_DIR/vibium-results" -type d 2>/dev/null | head -10 | while read d; do 
        echo "      📁 $d"
    done || echo "      (no directories found)"
    
    chmod +x scripts/ci/convert-vibium-to-allure.sh
    VIBIUM_FOUND=0
    # Try test-results directory first
    if [ -d "$SOURCE_DIR/vibium-results/test-results" ]; then
        echo "   ✅ Found Vibium test-results directory: $SOURCE_DIR/vibium-results/test-results"
        ./scripts/ci/convert-vibium-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/vibium-results/test-results" "$ENV_FOR_CONVERSION" || true
        VIBIUM_FOUND=1
    elif [ -d "$SOURCE_DIR/vibium-results/.vitest" ]; then
        echo "   ✅ Found Vibium .vitest directory: $SOURCE_DIR/vibium-results/.vitest"
        ./scripts/ci/convert-vibium-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/vibium-results/.vitest" "$ENV_FOR_CONVERSION" || true
        VIBIUM_FOUND=1
    else
        # Fallback: search for any result files
        find "$SOURCE_DIR/vibium-results" -type d \( -name "test-results" -o -name ".vitest" \) | while read vibium_dir; do
            echo "   ✅ Found Vibium directory: $vibium_dir"
            ./scripts/ci/convert-vibium-to-allure.sh "$TARGET_DIR" "$vibium_dir" "$ENV_FOR_CONVERSION" || true
            VIBIUM_FOUND=1
        done
    fi
    if [ "$VIBIUM_FOUND" -eq 0 ]; then
        echo "   ⚠️  No Vibium result files found in $SOURCE_DIR/vibium-results"
        echo "   💡 Note: Vitest may need reporter configuration to generate result files"
        echo "   💡 Check if vibium/vitest.config.ts has JSON/XML reporter configured"
    fi
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
echo ""
echo "📊 Framework Summary:"
echo "   TestNG-based (merged): Smoke, Grid, Mobile, Responsive, Selenide"
echo "   Converted frameworks:"
# Count results by framework (check labels in JSON files)
# Use flexible grep patterns that account for JSON formatting with whitespace/newlines
# Match on epic labels which are more consistent across frameworks
# Also check testClass values as fallback
PLAYWRIGHT_COUNT=$(find "$TARGET_DIR" -name "*-result.json" -exec grep -l "Playwright E2E Testing" {} \; 2>/dev/null | wc -l | tr -d ' ')
CYPRESS_COUNT=$(find "$TARGET_DIR" -name "*-result.json" -exec grep -l "Cypress E2E Testing" {} \; 2>/dev/null | wc -l | tr -d ' ')
ROBOT_COUNT=$(find "$TARGET_DIR" -name "*-result.json" -exec grep -l "Robot Framework Acceptance Testing" {} \; 2>/dev/null | wc -l | tr -d ' ')
VIBIUM_COUNT=$(find "$TARGET_DIR" -name "*-result.json" -exec grep -l "Vibium Visual Regression Testing" {} \; 2>/dev/null | wc -l | tr -d ' ')
SELENIDE_COUNT=$(find "$TARGET_DIR" -name "*-result.json" -exec grep -lE "HomePage Tests|HomePage Navigation" {} \; 2>/dev/null | wc -l | tr -d ' ')

echo "   - Playwright: $PLAYWRIGHT_COUNT test(s)"
echo "   - Cypress: $CYPRESS_COUNT test(s)"
echo "   - Robot Framework: $ROBOT_COUNT test(s)"
echo "   - Vibium: $VIBIUM_COUNT test(s)"
echo "   - Selenide: $SELENIDE_COUNT test(s) (merged from TestNG results)"
echo ""
if [ "$CYPRESS_COUNT" -eq 0 ]; then
    echo "   ⚠️  Cypress: No results found - check if Cypress tests ran and artifacts were uploaded"
fi
if [ "$VIBIUM_COUNT" -eq 0 ]; then
    echo "   ⚠️  Vibium: No results found - check if Vibium tests ran and artifacts were uploaded"
fi
if [ "$SELENIDE_COUNT" -eq 0 ]; then
    echo "   ⚠️  Selenide: No results found - check if Selenide tests ran and results were merged"
fi

