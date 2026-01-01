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
# 3. Converts framework results (Cypress, Playwright, Robot, Vibium, Artillery) to Allure format
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

# Detect which environments actually ran by checking for artifact directories
# Only process merged directories for environments that actually have artifacts
ENVIRONMENTS=("dev" "test" "prod")
ACTIVE_ENVIRONMENTS=()

for env in "${ENVIRONMENTS[@]}"; do
    # Check if this environment has any artifacts (any directory with results)
    if [ -d "$SOURCE_DIR/results-$env" ] && [ -n "$(find "$SOURCE_DIR/results-$env" -mindepth 1 -maxdepth 1 2>/dev/null)" ]; then
        ACTIVE_ENVIRONMENTS+=("$env")
        echo "   ✅ Detected active environment: $env"
    fi
done

if [ ${#ACTIVE_ENVIRONMENTS[@]} -eq 0 ]; then
    echo "   ⚠️  No active environments detected, defaulting to dev"
    ACTIVE_ENVIRONMENTS=("dev")
fi

echo "   📊 Active environments: ${ACTIVE_ENVIRONMENTS[*]}"

# Convert Cypress results for each environment
CYPRESS_PROCESSED=0
for env in "${ACTIVE_ENVIRONMENTS[@]}"; do
    # Check environment-specific directory first
    if [ -d "$SOURCE_DIR/results-$env/cypress-results-$env" ]; then
        echo "   Converting Cypress results ($env)..."
        chmod +x scripts/ci/convert-cypress-to-allure.sh
        json_file=$(find "$SOURCE_DIR/results-$env/cypress-results-$env" \( -name "mochawesome.json" -o -name "cypress-results.json" \) 2>/dev/null | head -1)
        if [ -n "$json_file" ] && [ -f "$json_file" ]; then
            json_dir=$(dirname "$json_file")
            ./scripts/ci/convert-cypress-to-allure.sh "$TARGET_DIR" "$json_dir" "$env" || true
            CYPRESS_PROCESSED=1
        elif [ -d "$SOURCE_DIR/results-$env/cypress-results-$env/results" ]; then
            ./scripts/ci/convert-cypress-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/results-$env/cypress-results-$env/results" "$env" || true
            CYPRESS_PROCESSED=1
        fi
    fi
done

# Check merged cypress-results directory only if no environment-specific directories were found
# IMPORTANT: Process merged directory for ALL ACTIVE environments (not just the first one)
if [ "$CYPRESS_PROCESSED" -eq 0 ] && [ -d "$SOURCE_DIR/cypress-results" ]; then
    echo "   Converting Cypress results (merged artifacts - processing for active environments only)..."
    chmod +x scripts/ci/convert-cypress-to-allure.sh
    # Process merged directory for each environment that actually ran
    # When artifacts are merged, they may preserve their artifact name as a subdirectory
    # e.g., cypress-results/cypress-results-dev/... or cypress-results/cypress-results-test/...
    for env in "${ACTIVE_ENVIRONMENTS[@]}"; do
        # First, check if there's an environment-specific subdirectory in the merged artifacts
        if [ -d "$SOURCE_DIR/cypress-results/cypress-results-$env" ]; then
            echo "   Found environment-specific Cypress directory: cypress-results-$env"
            json_file=$(find "$SOURCE_DIR/cypress-results/cypress-results-$env" \( -name "mochawesome.json" -o -name "cypress-results.json" -o -path "*/results/*.json" \) 2>/dev/null | head -1)
            if [ -n "$json_file" ] && [ -f "$json_file" ]; then
                json_dir=$(dirname "$json_file")
                ./scripts/ci/convert-cypress-to-allure.sh "$TARGET_DIR" "$json_dir" "$env" || true
                CYPRESS_PROCESSED=1
            elif [ -d "$SOURCE_DIR/cypress-results/cypress-results-$env/results" ]; then
                ./scripts/ci/convert-cypress-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/cypress-results/cypress-results-$env/results" "$env" || true
                CYPRESS_PROCESSED=1
            fi
        # If no environment-specific subdirectory, check the merged root directory
        # Process for each environment to ensure all environments are covered
        else
            json_file=$(find "$SOURCE_DIR/cypress-results" \( -name "mochawesome.json" -o -name "cypress-results.json" -o -path "*/results/*.json" \) 2>/dev/null | head -1)
            if [ -n "$json_file" ] && [ -f "$json_file" ]; then
                json_dir=$(dirname "$json_file")
                ./scripts/ci/convert-cypress-to-allure.sh "$TARGET_DIR" "$json_dir" "$env" || true
                CYPRESS_PROCESSED=1
            elif [ -d "$SOURCE_DIR/cypress-results/results" ]; then
                ./scripts/ci/convert-cypress-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/cypress-results/results" "$env" || true
                CYPRESS_PROCESSED=1
            fi
        fi
    done
fi

# Convert Playwright results for each environment
PLAYWRIGHT_PROCESSED=0
for env in "${ACTIVE_ENVIRONMENTS[@]}"; do
    # Check environment-specific directory first
    if [ -d "$SOURCE_DIR/results-$env/playwright-results-$env" ]; then
        echo "   Converting Playwright results ($env)..."
        chmod +x scripts/ci/convert-playwright-to-allure.sh
        if [ -d "$SOURCE_DIR/results-$env/playwright-results-$env/test-results" ]; then
            ./scripts/ci/convert-playwright-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/results-$env/playwright-results-$env/test-results" "$env" || true
            PLAYWRIGHT_PROCESSED=1
        fi
    fi
done

# Check merged playwright-results directory only if no environment-specific directories were found
# IMPORTANT: Only process merged directory for ACTIVE environments (not all environments)
if [ "$PLAYWRIGHT_PROCESSED" -eq 0 ] && [ -d "$SOURCE_DIR/playwright-results" ]; then
    echo "   Converting Playwright results (merged artifacts - processing for active environments only)..."
    chmod +x scripts/ci/convert-playwright-to-allure.sh
    # Process merged directory only for environments that actually ran
    for env in "${ACTIVE_ENVIRONMENTS[@]}"; do
        if [ -d "$SOURCE_DIR/playwright-results/test-results" ]; then
            ./scripts/ci/convert-playwright-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/playwright-results/test-results" "$env" || true
        fi
    done
fi

# Convert Robot Framework results for each environment
ROBOT_PROCESSED=0
for env in "${ACTIVE_ENVIRONMENTS[@]}"; do
    # Check environment-specific directory first
    if [ -d "$SOURCE_DIR/results-$env" ]; then
        output_xml=$(find "$SOURCE_DIR/results-$env" -name "output.xml" 2>/dev/null | head -1)
        if [ -n "$output_xml" ] && [ -f "$output_xml" ]; then
            echo "   Converting Robot Framework results ($env)..."
            chmod +x scripts/ci/convert-robot-to-allure.sh
            output_dir=$(dirname "$output_xml")
            ./scripts/ci/convert-robot-to-allure.sh "$TARGET_DIR" "$output_dir" "$env" || true
            ROBOT_PROCESSED=1
        fi
    fi
done

# Check merged robot-results directory only if no environment-specific directories were found
# IMPORTANT: Only process merged directory for ACTIVE environments (not all environments)
if [ "$ROBOT_PROCESSED" -eq 0 ] && [ -d "$SOURCE_DIR/robot-results" ]; then
    echo "   Converting Robot Framework results (merged artifacts - processing for active environments only)..."
    chmod +x scripts/ci/convert-robot-to-allure.sh
    # Process merged directory only for environments that actually ran
    for env in "${ACTIVE_ENVIRONMENTS[@]}"; do
        output_xml=$(find "$SOURCE_DIR/robot-results" -name "output.xml" 2>/dev/null | head -1)
        if [ -n "$output_xml" ] && [ -f "$output_xml" ]; then
            output_dir=$(dirname "$output_xml")
            ./scripts/ci/convert-robot-to-allure.sh "$TARGET_DIR" "$output_dir" "$env" || true
        fi
    done
fi

# Convert Vibium results for each environment
VIBIUM_PROCESSED=0
for env in "${ACTIVE_ENVIRONMENTS[@]}"; do
    # Check environment-specific directory first
    if [ -d "$SOURCE_DIR/results-$env/vibium-results-$env" ]; then
        echo "   Converting Vibium results ($env)..."
        chmod +x scripts/ci/convert-vibium-to-allure.sh
        if [ -d "$SOURCE_DIR/results-$env/vibium-results-$env/test-results" ]; then
            ./scripts/ci/convert-vibium-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/results-$env/vibium-results-$env/test-results" "$env" || true
            VIBIUM_PROCESSED=1
        elif [ -d "$SOURCE_DIR/results-$env/vibium-results-$env/.vitest" ]; then
            ./scripts/ci/convert-vibium-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/results-$env/vibium-results-$env/.vitest" "$env" || true
            VIBIUM_PROCESSED=1
        fi
    fi
done

# Check merged vibium-results directory only if no environment-specific directories were found
# IMPORTANT: Only process merged directory for ACTIVE environments (not all environments)
if [ "$VIBIUM_PROCESSED" -eq 0 ] && [ -d "$SOURCE_DIR/vibium-results" ]; then
    echo "   Converting Vibium results (merged artifacts - processing for active environments only)..."
    chmod +x scripts/ci/convert-vibium-to-allure.sh
    # Process merged directory only for environments that actually ran
    for env in "${ACTIVE_ENVIRONMENTS[@]}"; do
        if [ -d "$SOURCE_DIR/vibium-results/test-results" ]; then
            ./scripts/ci/convert-vibium-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/vibium-results/test-results" "$env" || true
        elif [ -d "$SOURCE_DIR/vibium-results/.vitest" ]; then
            ./scripts/ci/convert-vibium-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/vibium-results/.vitest" "$env" || true
        fi
    done
fi

# Convert FS (Full-Stack) test results for each environment
# IMPORTANT: FS tests only run in dev and test (never prod)
# Only process environments where FS tests actually ran
FS_ENVIRONMENTS=("dev" "test")
FS_PROCESSED_ENVS=()

echo "   📊 FS (Full-Stack) tests run only in: dev, test (never prod)"
echo "   🔍 Checking for FS test results in dev and test environments only..."

# Process FS test results for each environment
# Artifacts: uploaded as "fs-results-{env}" from "playwright/artillery-results/"
# Downloaded to: "all-test-results/fs-results" with merge-multiple: true
# With merge-multiple: true, structure is: fs-results/fs-results-{env}/playwright/artillery-results/*.json

# FS results are downloaded to all-test-results/fs-results (not results-{env})
# With merge-multiple: true, structure is: fs-results/fs-results-{env}/playwright/artillery-results/*.json
FS_PROCESSED=0
if [ -d "$SOURCE_DIR/fs-results" ]; then
    echo "   Converting FS test results (processing for active environments only)..."
    echo "   📂 DEBUG: Directory structure:"
    find "$SOURCE_DIR/fs-results" -type d 2>/dev/null | head -20 | while read d; do
        echo "      📁 $d"
    done
    echo ""
    echo "   📄 DEBUG: All JSON files:"
    all_json=$(find "$SOURCE_DIR/fs-results" -type f -name "*.json" 2>/dev/null)
    if [ -n "$all_json" ]; then
        echo "$all_json" | while read f; do
            echo "      📄 $f"
        done
    else
        echo "      ⚠️  NO JSON FILES FOUND"
    fi
    echo ""
    
    # Process each environment that actually ran
    for env in "${FS_ENVIRONMENTS[@]}"; do
        # Skip if this environment wasn't active
        if [[ ! " ${ACTIVE_ENVIRONMENTS[@]} " =~ " ${env} " ]]; then
            echo "   ⏭️  Skipping FS test conversion for $env (environment not active)"
            continue
        fi
        
        echo "   🔍 Processing FS test results for $env..."
        
        # Try exact path: fs-results/fs-results-{env}/playwright/artillery-results/*.json
        exact_path="$SOURCE_DIR/fs-results/fs-results-$env/playwright/artillery-results"
        echo "   📂 DEBUG: Checking exact path: $exact_path"
        
        if [ -d "$exact_path" ]; then
            json_files=$(find "$exact_path" -name "*.json" -type f 2>/dev/null)
            if [ -n "$json_files" ]; then
                json_count=$(echo "$json_files" | wc -l | tr -d ' ')
                echo "   ✅ Found $json_count JSON file(s) for $env at: $exact_path"
                chmod +x scripts/ci/convert-artillery-to-allure.sh
                ./scripts/ci/convert-artillery-to-allure.sh "$TARGET_DIR" "$exact_path" "$env" || true
                FS_PROCESSED_ENVS+=("$env")
                FS_PROCESSED=1
                continue
            else
                echo "   ⚠️  DEBUG: No JSON files found in $exact_path"
            fi
        else
            echo "   ⚠️  DEBUG: Directory not found: $exact_path"
        fi
        
        # Fallback: Check parent directory (fs-results-{env})
        fallback_path="$SOURCE_DIR/fs-results/fs-results-$env"
        echo "   📂 DEBUG: Checking fallback path: $fallback_path"
        
        if [ -d "$fallback_path" ]; then
            json_files=$(find "$fallback_path" -name "*.json" -type f 2>/dev/null)
            if [ -n "$json_files" ]; then
                json_count=$(echo "$json_files" | wc -l | tr -d ' ')
                echo "   ✅ Found $json_count JSON file(s) for $env at: $fallback_path"
                chmod +x scripts/ci/convert-artillery-to-allure.sh
                ./scripts/ci/convert-artillery-to-allure.sh "$TARGET_DIR" "$fallback_path" "$env" || true
                FS_PROCESSED_ENVS+=("$env")
                FS_PROCESSED=1
                continue
            else
                echo "   ⚠️  DEBUG: No JSON files found in $fallback_path"
            fi
        else
            echo "   ⚠️  DEBUG: Directory not found: $fallback_path"
        fi
        
        # Final fallback: Search for any JSON files in fs-results that might be for this environment
        echo "   🔍 DEBUG: Searching recursively for $env results in entire fs-results directory..."
        all_json_files=$(find "$SOURCE_DIR/fs-results" -name "*.json" -type f 2>/dev/null)
        if [ -n "$all_json_files" ]; then
            # Check if any files are in a path containing the environment name
            env_files=$(echo "$all_json_files" | grep -i "$env" || echo "")
            if [ -n "$env_files" ]; then
                # Use the directory containing the first matching file
                first_file=$(echo "$env_files" | head -1)
                file_dir=$(dirname "$first_file")
                json_count=$(echo "$env_files" | wc -l | tr -d ' ')
                echo "   ✅ Found $json_count JSON file(s) for $env (recursive search, using: $file_dir)"
                chmod +x scripts/ci/convert-artillery-to-allure.sh
                ./scripts/ci/convert-artillery-to-allure.sh "$TARGET_DIR" "$file_dir" "$env" || true
                FS_PROCESSED_ENVS+=("$env")
                FS_PROCESSED=1
            else
                # If no environment-specific files found but we have files, process them once for the first environment
                if [ "$FS_PROCESSED" -eq 0 ]; then
                    json_count=$(echo "$all_json_files" | wc -l | tr -d ' ')
                    echo "   ⚠️  Found $json_count JSON file(s) but no environment-specific path for $env"
                    echo "   🔍 Processing all files (may need environment detection in converter)"
                    chmod +x scripts/ci/convert-artillery-to-allure.sh
                    ./scripts/ci/convert-artillery-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/fs-results" "$env" || true
                    FS_PROCESSED_ENVS+=("$env")
                    FS_PROCESSED=1
                fi
            fi
        else
            echo "   ⚠️  No FS test JSON files found for $env"
        fi
    done
else
    echo "   ⚠️  fs-results directory not found at: $SOURCE_DIR/fs-results"
fi
# Warn if prod is in active environments but FS tests shouldn't run there
if [[ " ${ACTIVE_ENVIRONMENTS[@]} " =~ " prod " ]]; then
    echo "   ℹ️  Note: prod is active, but FS tests never run in prod (skipped)"
fi

if [ ${#FS_PROCESSED_ENVS[@]} -gt 0 ]; then
    echo "   ✅ FS test results processed for: ${FS_PROCESSED_ENVS[*]}"
else
    echo "   ⚠️  No FS test results were processed for any environment"
fi

# Step 4: Add environment labels
echo ""
echo "🏷️  Step 4: Adding environment labels..."
chmod +x scripts/ci/add-environment-labels.sh
./scripts/ci/add-environment-labels.sh "$TARGET_DIR" "$SOURCE_DIR"

# Step 4.25: Deduplicate TestNG retry attempts
echo ""
echo "🔄 Step 4.25: Deduplicating TestNG retry attempts..."
chmod +x scripts/ci/deduplicate-testng-retries.sh
./scripts/ci/deduplicate-testng-retries.sh "$TARGET_DIR"

# Step 4.5: Create framework container files for Suites section
echo ""
echo "📦 Step 4.5: Creating framework container files..."
chmod +x scripts/ci/create-framework-containers.sh
./scripts/ci/create-framework-containers.sh "$TARGET_DIR"

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
# Patterns search for the epic/feature values anywhere in the JSON file
PLAYWRIGHT_COUNT=$(find "$TARGET_DIR" -name "*-result.json" -exec grep -l "Playwright E2E Testing" {} \; 2>/dev/null | wc -l | tr -d ' ')
CYPRESS_COUNT=$(find "$TARGET_DIR" -name "*-result.json" -exec grep -l "Cypress E2E Testing" {} \; 2>/dev/null | wc -l | tr -d ' ')
ROBOT_COUNT=$(find "$TARGET_DIR" -name "*-result.json" -exec grep -l "Robot Framework Acceptance Testing" {} \; 2>/dev/null | wc -l | tr -d ' ')
VIBIUM_COUNT=$(find "$TARGET_DIR" -name "*-result.json" -exec grep -l "Vibium Visual Regression Testing" {} \; 2>/dev/null | wc -l | tr -d ' ')
ARTILLERY_COUNT=$(find "$TARGET_DIR" -name "*-result.json" -exec grep -l "Artillery Load Tests" {} \; 2>/dev/null | wc -l | tr -d ' ')
# Selenide uses @Epic("HomePage Tests") and @Feature("HomePage Navigation")
# Match either epic or feature value (pattern searches for the string anywhere in JSON)
SELENIDE_COUNT=$(find "$TARGET_DIR" -name "*-result.json" -exec grep -lE "HomePage Tests|HomePage Navigation" {} \; 2>/dev/null | wc -l | tr -d ' ')

echo "   - Playwright: $PLAYWRIGHT_COUNT test(s)"
echo "   - Cypress: $CYPRESS_COUNT test(s)"
echo "   - Robot Framework: $ROBOT_COUNT test(s)"
echo "   - Vibium: $VIBIUM_COUNT test(s)"
echo "   - FS (Full-Stack): $ARTILLERY_COUNT test(s)"
echo "   - Selenide: $SELENIDE_COUNT test(s) (merged from TestNG results)"
echo ""
# Debug: Show sample matches for troubleshooting
if [ "$SELENIDE_COUNT" -eq 0 ]; then
    echo "   ⚠️  Selenide: No results found - check if Selenide tests ran and results were merged"
    echo "   🔍 Debug: Searching for Selenide patterns in result files..."
    SELENIDE_SAMPLE=$(find "$TARGET_DIR" -name "*-result.json" -exec grep -l "HomePage" {} \; 2>/dev/null | head -1)
    if [ -n "$SELENIDE_SAMPLE" ]; then
        echo "   💡 Found file with 'HomePage': $SELENIDE_SAMPLE"
        echo "   💡 Checking labels in sample file..."
        grep -o '"name":\s*"[^"]*",\s*"value":\s*"[^"]*"' "$SELENIDE_SAMPLE" 2>/dev/null | grep -i "homepage" | head -3 || echo "   (No HomePage labels found in expected format)"
    fi
fi
if [ "$VIBIUM_COUNT" -eq 0 ]; then
    echo "   ⚠️  Vibium: No results found - check if Vibium tests ran and artifacts were uploaded"
    echo "   💡 Note: Vibium tests only run if 'enable_vibium_tests' input is set to true"
    echo "   💡 Check workflow inputs and vibium-tests job execution"
fi
if [ "$CYPRESS_COUNT" -eq 0 ]; then
    echo "   ⚠️  Cypress: No results found - check if Cypress tests ran and artifacts were uploaded"
fi

