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
    # Also check merged framework-specific directories
    has_results=false
    
    # Check environment-specific directory
    if [ -d "$SOURCE_DIR/results-$env" ] && [ -n "$(find "$SOURCE_DIR/results-$env" -mindepth 1 -maxdepth 1 2>/dev/null)" ]; then
        has_results=true
    fi
    
    # Check merged framework directories
    if [ -d "$SOURCE_DIR/cypress-results/cypress-results-$env" ] || \
       [ -d "$SOURCE_DIR/playwright-results/playwright-results-$env" ] || \
       [ -d "$SOURCE_DIR/robot-results/robot-results-$env" ] || \
       [ -d "$SOURCE_DIR/vibium-results/vibium-results-$env" ] || \
       [ -d "$SOURCE_DIR/fs-results/fs-results-$env" ]; then
        has_results=true
    fi
    
    if [ "$has_results" = true ]; then
        ACTIVE_ENVIRONMENTS+=("$env")
        echo "   ✅ Detected active environment: $env"
    fi
done

if [ ${#ACTIVE_ENVIRONMENTS[@]} -eq 0 ]; then
    echo "   ⚠️  No active environments detected, defaulting to dev"
    ACTIVE_ENVIRONMENTS=("dev")
fi

echo "   📊 Active environments: ${ACTIVE_ENVIRONMENTS[*]}"
echo ""
echo "🔍 Debug: Checking for framework-specific artifact directories..."
echo "   Cypress: $([ -d "$SOURCE_DIR/cypress-results" ] && echo "✅ exists" || echo "❌ not found")"
echo "   Playwright: $([ -d "$SOURCE_DIR/playwright-results" ] && echo "✅ exists" || echo "❌ not found")"
echo "   Robot: $([ -d "$SOURCE_DIR/robot-results" ] && echo "✅ exists" || echo "❌ not found")"
echo "   Vibium: $([ -d "$SOURCE_DIR/vibium-results" ] && echo "✅ exists" || echo "❌ not found")"
echo "   FS: $([ -d "$SOURCE_DIR/fs-results" ] && echo "✅ exists" || echo "❌ not found")"
echo ""
echo "🔍 Debug: Checking for environment-specific directories (results-dev, results-test, results-prod)..."
for env in "${ACTIVE_ENVIRONMENTS[@]}"; do
    if [ -d "$SOURCE_DIR/results-$env" ]; then
        echo "   ✅ results-$env exists"
        echo "      Contents:"
        find "$SOURCE_DIR/results-$env" -maxdepth 2 -type d 2>/dev/null | head -10 | while read d; do
            echo "        - $d"
        done
    else
        echo "   ❌ results-$env not found"
    fi
done
echo ""
if [ -d "$SOURCE_DIR/cypress-results" ]; then
    echo "   📂 Cypress results structure:"
    find "$SOURCE_DIR/cypress-results" -maxdepth 3 -type d 2>/dev/null | head -15 | while read d; do
        file_count=$(find "$d" -maxdepth 1 -type f 2>/dev/null | wc -l | tr -d ' ')
        echo "      - $d ($file_count files)"
    done
    echo "   📄 Cypress JSON files found:"
    find "$SOURCE_DIR/cypress-results" -name "*.json" -type f 2>/dev/null | head -10 | while read f; do
        echo "      - $f ($(du -h "$f" 2>/dev/null | cut -f1))"
    done || echo "      (no JSON files found)"
fi
if [ -d "$SOURCE_DIR/playwright-results" ]; then
    echo "   📂 Playwright results structure:"
    find "$SOURCE_DIR/playwright-results" -maxdepth 3 -type d 2>/dev/null | head -15 | while read d; do
        file_count=$(find "$d" -maxdepth 1 -type f 2>/dev/null | wc -l | tr -d ' ')
        echo "      - $d ($file_count files)"
    done
    echo "   📄 Playwright XML files found:"
    find "$SOURCE_DIR/playwright-results" -name "*.xml" -type f 2>/dev/null | head -10 | while read f; do
        echo "      - $f ($(du -h "$f" 2>/dev/null | cut -f1))"
    done || echo "      (no XML files found)"
fi
if [ -d "$SOURCE_DIR/robot-results" ]; then
    echo "   📂 Robot results structure:"
    find "$SOURCE_DIR/robot-results" -maxdepth 3 -type d 2>/dev/null | head -15 | while read d; do
        file_count=$(find "$d" -maxdepth 1 -type f 2>/dev/null | wc -l | tr -d ' ')
        echo "      - $d ($file_count files)"
    done
    echo "   📄 Robot XML files found:"
    find "$SOURCE_DIR/robot-results" -name "output.xml" -type f 2>/dev/null | head -10 | while read f; do
        echo "      - $f ($(du -h "$f" 2>/dev/null | cut -f1))"
    done || echo "      (no output.xml files found)"
fi
if [ -d "$SOURCE_DIR/vibium-results" ]; then
    echo "   📂 Vibium results structure:"
    find "$SOURCE_DIR/vibium-results" -maxdepth 3 -type d 2>/dev/null | head -15 | while read d; do
        file_count=$(find "$d" -maxdepth 1 -type f 2>/dev/null | wc -l | tr -d ' ')
        echo "      - $d ($file_count files)"
    done
    echo "   📄 Vibium JSON files found:"
    find "$SOURCE_DIR/vibium-results" -name "*.json" -type f 2>/dev/null | head -10 | while read f; do
        echo "      - $f ($(du -h "$f" 2>/dev/null | cut -f1))"
    done || echo "      (no JSON files found)"
fi
echo ""

# Convert Cypress results for each environment
# FIXED: Check BOTH environment-specific directories AND merged directories for each environment
# This ensures we find results regardless of artifact download structure
for env in "${ACTIVE_ENVIRONMENTS[@]}"; do
    ENV_PROCESSED=0
    
    # Check environment-specific directory first (results-dev/cypress-results-dev/)
    # This is the PRIMARY source - ensures we use environment-specific data
    if [ -d "$SOURCE_DIR/results-$env" ]; then
        # Try multiple possible structures in results-$env
        # Structure 1: results-$env/cypress-results-$env/
        if [ -d "$SOURCE_DIR/results-$env/cypress-results-$env" ]; then
            echo "   ✅ Found environment-specific directory: results-$env/cypress-results-$env"
            echo "   Converting Cypress results ($env) from results-$env/cypress-results-$env..."
            chmod +x scripts/ci/convert-cypress-to-allure.sh
            json_file=$(find "$SOURCE_DIR/results-$env/cypress-results-$env" \( -name "mochawesome.json" -o -name "cypress-results.json" \) 2>/dev/null | head -1)
            if [ -n "$json_file" ] && [ -f "$json_file" ]; then
                json_dir=$(dirname "$json_file")
                echo "   📄 Found Cypress JSON file: $json_file"
                if ./scripts/ci/convert-cypress-to-allure.sh "$TARGET_DIR" "$json_dir" "$env"; then
                    ENV_PROCESSED=1
                    echo "   ✅ Cypress conversion successful for $env (environment-specific data)"
                else
                    echo "   ⚠️  Cypress conversion failed for $env (exit code: $?)"
                fi
            elif [ -d "$SOURCE_DIR/results-$env/cypress-results-$env/results" ]; then
                echo "   📂 Found Cypress results directory: $SOURCE_DIR/results-$env/cypress-results-$env/results"
                if ./scripts/ci/convert-cypress-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/results-$env/cypress-results-$env/results" "$env"; then
                    ENV_PROCESSED=1
                    echo "   ✅ Cypress conversion successful for $env (environment-specific data)"
                else
                    echo "   ⚠️  Cypress conversion failed for $env (exit code: $?)"
                fi
            else
                echo "   ⚠️  No Cypress JSON files found in $SOURCE_DIR/results-$env/cypress-results-$env"
            fi
        # Structure 2: results-$env/cypress/cypress/results/ (if artifact preserves full path)
        elif [ -d "$SOURCE_DIR/results-$env/cypress/cypress/results" ]; then
            echo "   ✅ Found environment-specific directory: results-$env/cypress/cypress/results"
            echo "   Converting Cypress results ($env) from results-$env/cypress/cypress/results..."
            chmod +x scripts/ci/convert-cypress-to-allure.sh
            if ./scripts/ci/convert-cypress-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/results-$env/cypress/cypress/results" "$env"; then
                ENV_PROCESSED=1
                echo "   ✅ Cypress conversion successful for $env (environment-specific data)"
            else
                echo "   ⚠️  Cypress conversion failed for $env (exit code: $?)"
            fi
        # Structure 3: Search recursively in results-$env for Cypress JSON files
        else
            json_file=$(find "$SOURCE_DIR/results-$env" \( -name "mochawesome.json" -o -name "cypress-results.json" -o -path "*/cypress/results/*.json" \) 2>/dev/null | head -1)
            if [ -n "$json_file" ] && [ -f "$json_file" ]; then
                json_dir=$(dirname "$json_file")
                echo "   ✅ Found Cypress JSON file in results-$env: $json_file"
                echo "   Converting Cypress results ($env) from $json_dir..."
                chmod +x scripts/ci/convert-cypress-to-allure.sh
                if ./scripts/ci/convert-cypress-to-allure.sh "$TARGET_DIR" "$json_dir" "$env"; then
                    ENV_PROCESSED=1
                    echo "   ✅ Cypress conversion successful for $env (environment-specific data)"
                else
                    echo "   ⚠️  Cypress conversion failed for $env (exit code: $?)"
                fi
            fi
        fi
    fi
    
    # Also check merged directory with environment-specific subdirectory (cypress-results/cypress-results-{env}/)
    # Artifacts preserve full upload path: cypress-results/cypress-results-{env}/cypress/cypress/results/
    if [ "$ENV_PROCESSED" -eq 0 ] && [ -d "$SOURCE_DIR/cypress-results/cypress-results-$env" ]; then
        echo "   Converting Cypress results ($env) from merged cypress-results/cypress-results-$env..."
        chmod +x scripts/ci/convert-cypress-to-allure.sh
        # Check nested path first (artifacts preserve full upload path)
        if [ -d "$SOURCE_DIR/cypress-results/cypress-results-$env/cypress/cypress/results" ]; then
            echo "   📂 Found Cypress results directory (nested path): $SOURCE_DIR/cypress-results/cypress-results-$env/cypress/cypress/results"
            if ./scripts/ci/convert-cypress-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/cypress-results/cypress-results-$env/cypress/cypress/results" "$env"; then
                ENV_PROCESSED=1
                echo "   ✅ Cypress conversion successful for $env"
            else
                echo "   ⚠️  Cypress conversion failed for $env (exit code: $?)"
            fi
        else
            # Fallback: search for JSON files recursively
            json_file=$(find "$SOURCE_DIR/cypress-results/cypress-results-$env" \( -name "mochawesome.json" -o -name "cypress-results.json" -o -path "*/results/*.json" \) 2>/dev/null | head -1)
            if [ -n "$json_file" ] && [ -f "$json_file" ]; then
                json_dir=$(dirname "$json_file")
                echo "   📄 Found Cypress JSON file: $json_file"
                if ./scripts/ci/convert-cypress-to-allure.sh "$TARGET_DIR" "$json_dir" "$env"; then
                    ENV_PROCESSED=1
                    echo "   ✅ Cypress conversion successful for $env"
                else
                    echo "   ⚠️  Cypress conversion failed for $env (exit code: $?)"
                fi
            elif [ -d "$SOURCE_DIR/cypress-results/cypress-results-$env/results" ]; then
                echo "   📂 Found Cypress results directory: $SOURCE_DIR/cypress-results/cypress-results-$env/results"
                if ./scripts/ci/convert-cypress-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/cypress-results/cypress-results-$env/results" "$env"; then
                    ENV_PROCESSED=1
                    echo "   ✅ Cypress conversion successful for $env"
                else
                    echo "   ⚠️  Cypress conversion failed for $env (exit code: $?)"
                fi
            else
                echo "   ⚠️  No Cypress JSON files found in $SOURCE_DIR/cypress-results/cypress-results-$env"
            fi
        fi
    fi
    
    # FIXED: Check flat structure as LAST RESORT (when merge-multiple: true creates flat structure)
    # WARNING: Flat structure cannot distinguish environments - only process once to avoid duplicate data
    if [ "$ENV_PROCESSED" -eq 0 ]; then
        # Check if flat structure exists (cypress-results/results/ or cypress-results/cypress/cypress/results/)
        if [ -d "$SOURCE_DIR/cypress-results/results" ] || [ -d "$SOURCE_DIR/cypress-results/cypress/cypress/results" ]; then
            # Only process for first environment to avoid processing same files multiple times
            # Flat structure means we can't distinguish which environment the files belong to
            if [ "$env" == "${ACTIVE_ENVIRONMENTS[0]}" ]; then
                echo "   ⚠️  WARNING: No environment-specific subdirectories found, processing flat structure"
                echo "   ⚠️  WARNING: Flat structure cannot distinguish environments - processing for ${ACTIVE_ENVIRONMENTS[0]} only"
                echo "   ⚠️  WARNING: Other environments will not have Cypress results if flat structure is used"
                echo "   📂 Found Cypress results in flat structure, processing for environment: $env"
                chmod +x scripts/ci/convert-cypress-to-allure.sh
                if [ -d "$SOURCE_DIR/cypress-results/results" ]; then
                    if ./scripts/ci/convert-cypress-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/cypress-results/results" "$env"; then
                        ENV_PROCESSED=1
                        echo "   ✅ Cypress conversion successful for $env (flat structure - WARNING: same data for all environments)"
                    fi
                elif [ -d "$SOURCE_DIR/cypress-results/cypress/cypress/results" ]; then
                    if ./scripts/ci/convert-cypress-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/cypress-results/cypress/cypress/results" "$env"; then
                        ENV_PROCESSED=1
                        echo "   ✅ Cypress conversion successful for $env (flat structure, nested path - WARNING: same data for all environments)"
                    fi
                fi
            else
                echo "   ⏭️  Skipping $env (flat structure already processed for ${ACTIVE_ENVIRONMENTS[0]} - cannot distinguish environments in flat structure)"
            fi
        else
            # Debug: Report if Cypress results were not found for this environment
            echo "   ⚠️  No Cypress results found for $env environment"
            echo "      Checked locations:"
            echo "        - $SOURCE_DIR/results-$env/cypress-results-$env"
            echo "        - $SOURCE_DIR/results-$env/cypress/cypress/results"
            echo "        - $SOURCE_DIR/cypress-results/cypress-results-$env"
            echo "        - $SOURCE_DIR/cypress-results/cypress-results-$env/cypress/cypress/results (nested path)"
            echo "        - $SOURCE_DIR/cypress-results/results (flat structure - only processed for first environment)"
            echo "        - $SOURCE_DIR/cypress-results/cypress/cypress/results (flat structure, nested path - only processed for first environment)"
        fi
    fi
done

# Convert Playwright results for each environment
# FIXED: Check BOTH environment-specific directories AND merged directories for each environment
for env in "${ACTIVE_ENVIRONMENTS[@]}"; do
    ENV_PROCESSED=0
    
    # Check environment-specific directory first (results-dev/playwright-results-dev/)
    # This is the PRIMARY source - ensures we use environment-specific data
    if [ -d "$SOURCE_DIR/results-$env" ]; then
        # Try multiple possible structures in results-$env
        # Structure 1: results-$env/playwright-results-$env/test-results/
        if [ -d "$SOURCE_DIR/results-$env/playwright-results-$env/test-results" ]; then
            echo "   ✅ Found environment-specific directory: results-$env/playwright-results-$env/test-results"
            echo "   Converting Playwright results ($env) from results-$env/playwright-results-$env..."
            chmod +x scripts/ci/convert-playwright-to-allure.sh
            if ./scripts/ci/convert-playwright-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/results-$env/playwright-results-$env/test-results" "$env"; then
                ENV_PROCESSED=1
                echo "   ✅ Playwright conversion successful for $env (environment-specific data)"
            else
                echo "   ⚠️  Playwright conversion failed for $env (exit code: $?)"
            fi
        # Structure 2: results-$env/playwright/test-results/ (if artifact preserves full path)
        elif [ -d "$SOURCE_DIR/results-$env/playwright/test-results" ]; then
            echo "   ✅ Found environment-specific directory: results-$env/playwright/test-results"
            echo "   Converting Playwright results ($env) from results-$env/playwright/test-results..."
            chmod +x scripts/ci/convert-playwright-to-allure.sh
            if ./scripts/ci/convert-playwright-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/results-$env/playwright/test-results" "$env"; then
                ENV_PROCESSED=1
                echo "   ✅ Playwright conversion successful for $env (environment-specific data)"
            else
                echo "   ⚠️  Playwright conversion failed for $env (exit code: $?)"
            fi
        # Structure 3: Search recursively in results-$env for Playwright XML files
        else
            xml_file=$(find "$SOURCE_DIR/results-$env" \( -name "junit.xml" -o -path "*/test-results/*.xml" \) 2>/dev/null | head -1)
            if [ -n "$xml_file" ] && [ -f "$xml_file" ]; then
                xml_dir=$(dirname "$xml_file")
                echo "   ✅ Found Playwright XML file in results-$env: $xml_file"
                echo "   Converting Playwright results ($env) from $xml_dir..."
                chmod +x scripts/ci/convert-playwright-to-allure.sh
                if ./scripts/ci/convert-playwright-to-allure.sh "$TARGET_DIR" "$xml_dir" "$env"; then
                    ENV_PROCESSED=1
                    echo "   ✅ Playwright conversion successful for $env (environment-specific data)"
                else
                    echo "   ⚠️  Playwright conversion failed for $env (exit code: $?)"
                fi
            fi
        fi
    fi
    
    # Also check merged directory with environment-specific subdirectory (playwright-results/playwright-results-{env}/)
    # Artifacts preserve full upload path: playwright-results/playwright-results-{env}/playwright/test-results/
    if [ "$ENV_PROCESSED" -eq 0 ] && [ -d "$SOURCE_DIR/playwright-results/playwright-results-$env" ]; then
        echo "   Converting Playwright results ($env) from merged playwright-results/playwright-results-$env..."
        chmod +x scripts/ci/convert-playwright-to-allure.sh
        # Check nested path first (artifacts preserve full upload path)
        if [ -d "$SOURCE_DIR/playwright-results/playwright-results-$env/playwright/test-results" ]; then
            echo "   📂 Found Playwright results directory (nested path): $SOURCE_DIR/playwright-results/playwright-results-$env/playwright/test-results"
            if ./scripts/ci/convert-playwright-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/playwright-results/playwright-results-$env/playwright/test-results" "$env"; then
                ENV_PROCESSED=1
                echo "   ✅ Playwright conversion successful for $env"
            else
                echo "   ⚠️  Playwright conversion failed for $env (exit code: $?)"
            fi
        elif [ -d "$SOURCE_DIR/playwright-results/playwright-results-$env/test-results" ]; then
            echo "   📂 Found Playwright results directory: $SOURCE_DIR/playwright-results/playwright-results-$env/test-results"
            if ./scripts/ci/convert-playwright-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/playwright-results/playwright-results-$env/test-results" "$env"; then
                ENV_PROCESSED=1
                echo "   ✅ Playwright conversion successful for $env"
            else
                echo "   ⚠️  Playwright conversion failed for $env (exit code: $?)"
            fi
        else
            echo "   ⚠️  No Playwright test-results directory found in $SOURCE_DIR/playwright-results/playwright-results-$env"
        fi
    fi
    
    # FIXED: Check flat structure as LAST RESORT (when merge-multiple: true creates flat structure)
    # WARNING: Flat structure cannot distinguish environments - only process once to avoid duplicate data
    if [ "$ENV_PROCESSED" -eq 0 ]; then
        # Check if flat structure exists (playwright-results/test-results/ or playwright-results/playwright/test-results/)
        if [ -d "$SOURCE_DIR/playwright-results/test-results" ] || [ -d "$SOURCE_DIR/playwright-results/playwright/test-results" ]; then
            # Only process for first environment to avoid processing same files multiple times
            # Flat structure means we can't distinguish which environment the files belong to
            if [ "$env" == "${ACTIVE_ENVIRONMENTS[0]}" ]; then
                echo "   ⚠️  WARNING: No environment-specific subdirectories found, processing flat structure"
                echo "   ⚠️  WARNING: Flat structure cannot distinguish environments - processing for ${ACTIVE_ENVIRONMENTS[0]} only"
                echo "   ⚠️  WARNING: Other environments will not have Playwright results if flat structure is used"
                echo "   📂 Found Playwright results in flat structure, processing for environment: $env"
                chmod +x scripts/ci/convert-playwright-to-allure.sh
                if [ -d "$SOURCE_DIR/playwright-results/test-results" ]; then
                    if ./scripts/ci/convert-playwright-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/playwright-results/test-results" "$env"; then
                        ENV_PROCESSED=1
                        echo "   ✅ Playwright conversion successful for $env (flat structure - WARNING: same data for all environments)"
                    fi
                elif [ -d "$SOURCE_DIR/playwright-results/playwright/test-results" ]; then
                    if ./scripts/ci/convert-playwright-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/playwright-results/playwright/test-results" "$env"; then
                        ENV_PROCESSED=1
                        echo "   ✅ Playwright conversion successful for $env (flat structure, nested path - WARNING: same data for all environments)"
                    fi
                fi
            else
                echo "   ⏭️  Skipping $env (flat structure already processed for ${ACTIVE_ENVIRONMENTS[0]} - cannot distinguish environments in flat structure)"
            fi
        else
            # Debug: Report if Playwright results were not found for this environment
            echo "   ⚠️  No Playwright results found for $env environment"
            echo "      Checked locations:"
            echo "        - $SOURCE_DIR/results-$env/playwright-results-$env/test-results"
            echo "        - $SOURCE_DIR/playwright-results/playwright-results-$env/test-results"
            echo "        - $SOURCE_DIR/playwright-results/playwright-results-$env/playwright/test-results (nested path)"
            echo "        - $SOURCE_DIR/playwright-results/test-results (flat structure)"
            echo "        - $SOURCE_DIR/playwright-results/playwright/test-results (flat structure, nested path)"
        fi
    fi
done

# Convert Robot Framework results for each environment
# FIXED: Check BOTH environment-specific directories AND merged directories for each environment
for env in "${ACTIVE_ENVIRONMENTS[@]}"; do
    ENV_PROCESSED=0
    
    # Check environment-specific directory first (results-dev/)
    output_xml=$(find "$SOURCE_DIR/results-$env" -name "output.xml" 2>/dev/null | head -1)
    if [ -n "$output_xml" ] && [ -f "$output_xml" ]; then
        echo "   Converting Robot Framework results ($env) from results-$env..."
        echo "   📄 Found Robot output.xml: $output_xml"
        chmod +x scripts/ci/convert-robot-to-allure.sh
        output_dir=$(dirname "$output_xml")
        if ./scripts/ci/convert-robot-to-allure.sh "$TARGET_DIR" "$output_dir" "$env"; then
            ENV_PROCESSED=1
            echo "   ✅ Robot conversion successful for $env"
        else
            echo "   ⚠️  Robot conversion failed for $env (exit code: $?)"
        fi
    fi
    
    # Also check merged directory with environment-specific subdirectory (robot-results/robot-results-{env}/)
    # Artifacts preserve full upload path: robot-results/robot-results-{env}/target/robot-reports/
    if [ "$ENV_PROCESSED" -eq 0 ] && [ -d "$SOURCE_DIR/robot-results/robot-results-$env" ]; then
        echo "   Converting Robot Framework results ($env) from merged robot-results/robot-results-$env..."
        chmod +x scripts/ci/convert-robot-to-allure.sh
        # Check nested path first (artifacts preserve full upload path)
        output_xml=$(find "$SOURCE_DIR/robot-results/robot-results-$env/target/robot-reports" -name "output.xml" 2>/dev/null | head -1)
        if [ -n "$output_xml" ] && [ -f "$output_xml" ]; then
            output_dir=$(dirname "$output_xml")
            echo "   📄 Found Robot output.xml (nested path): $output_xml"
            if ./scripts/ci/convert-robot-to-allure.sh "$TARGET_DIR" "$output_dir" "$env"; then
                ENV_PROCESSED=1
                echo "   ✅ Robot conversion successful for $env"
            else
                echo "   ⚠️  Robot conversion failed for $env (exit code: $?)"
            fi
        else
            # Fallback: search for output.xml anywhere in the directory
            output_xml=$(find "$SOURCE_DIR/robot-results/robot-results-$env" -name "output.xml" 2>/dev/null | head -1)
            if [ -n "$output_xml" ] && [ -f "$output_xml" ]; then
                output_dir=$(dirname "$output_xml")
                echo "   📄 Found Robot output.xml: $output_xml"
                if ./scripts/ci/convert-robot-to-allure.sh "$TARGET_DIR" "$output_dir" "$env"; then
                    ENV_PROCESSED=1
                    echo "   ✅ Robot conversion successful for $env"
                else
                    echo "   ⚠️  Robot conversion failed for $env (exit code: $?)"
                fi
            else
                echo "   ⚠️  No Robot output.xml found in $SOURCE_DIR/robot-results/robot-results-$env"
            fi
        fi
    fi
    
    # Debug: Report if Robot results were not found for this environment
    if [ "$ENV_PROCESSED" -eq 0 ]; then
        echo "   ⚠️  No Robot results found for $env environment"
        echo "      Checked locations:"
        echo "        - $SOURCE_DIR/results-$env (searching for output.xml)"
        if [ -d "$SOURCE_DIR/results-$env" ]; then
            echo "          Directory exists, contents:"
            find "$SOURCE_DIR/results-$env" -name "output.xml" -o -name "*.xml" 2>/dev/null | head -5 | while read f; do
                echo "            Found: $f"
            done || echo "            (no XML files found)"
        else
            echo "          Directory does not exist"
        fi
        echo "        - $SOURCE_DIR/robot-results/robot-results-$env"
        if [ -d "$SOURCE_DIR/robot-results/robot-results-$env" ]; then
            echo "          Directory exists, searching for output.xml:"
            find "$SOURCE_DIR/robot-results/robot-results-$env" -name "output.xml" 2>/dev/null | head -3 | while read f; do
                echo "            Found: $f"
            done || echo "            (no output.xml found)"
        else
            echo "          Directory does not exist"
        fi
        echo "        - $SOURCE_DIR/robot-results/robot-results-$env/target/robot-reports (nested path)"
        if [ -d "$SOURCE_DIR/robot-results/robot-results-$env/target/robot-reports" ]; then
            echo "          Directory exists, searching for output.xml:"
            find "$SOURCE_DIR/robot-results/robot-results-$env/target/robot-reports" -name "output.xml" 2>/dev/null | head -3 | while read f; do
                echo "            Found: $f"
            done || echo "            (no output.xml found)"
        else
            echo "          Directory does not exist"
        fi
    fi
    
    # FIXED: Skip flat merge fallback - if no environment-specific subdirectory exists,
    # we cannot determine which environment the files belong to, so skip to prevent duplicates
done

# Convert Vibium results for each environment
# FIXED: Check BOTH environment-specific directories AND merged directories for each environment
for env in "${ACTIVE_ENVIRONMENTS[@]}"; do
    ENV_PROCESSED=0
    
    # Check environment-specific directory first (results-dev/vibium-results-dev/)
    # This is the PRIMARY source - ensures we use environment-specific data
    if [ -d "$SOURCE_DIR/results-$env" ]; then
        # Try multiple possible structures in results-$env
        # Structure 1: results-$env/vibium-results-$env/test-results/
        if [ -d "$SOURCE_DIR/results-$env/vibium-results-$env/test-results" ]; then
            echo "   ✅ Found environment-specific directory: results-$env/vibium-results-$env/test-results"
            echo "   Converting Vibium results ($env) from results-$env/vibium-results-$env..."
            chmod +x scripts/ci/convert-vibium-to-allure.sh
            if ./scripts/ci/convert-vibium-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/results-$env/vibium-results-$env/test-results" "$env"; then
                ENV_PROCESSED=1
                echo "   ✅ Vibium conversion successful for $env (environment-specific data)"
            else
                echo "   ⚠️  Vibium conversion failed for $env (exit code: $?)"
            fi
        # Structure 2: results-$env/vibium-results-$env/.vitest/
        elif [ -d "$SOURCE_DIR/results-$env/vibium-results-$env/.vitest" ]; then
            echo "   ✅ Found environment-specific directory: results-$env/vibium-results-$env/.vitest"
            echo "   Converting Vibium results ($env) from results-$env/vibium-results-$env..."
            chmod +x scripts/ci/convert-vibium-to-allure.sh
            if ./scripts/ci/convert-vibium-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/results-$env/vibium-results-$env/.vitest" "$env"; then
                ENV_PROCESSED=1
                echo "   ✅ Vibium conversion successful for $env (environment-specific data)"
            else
                echo "   ⚠️  Vibium conversion failed for $env (exit code: $?)"
            fi
        # Structure 3: results-$env/vibium/test-results/ or results-$env/vibium/.vitest/ (if artifact preserves full path)
        elif [ -d "$SOURCE_DIR/results-$env/vibium/test-results" ]; then
            echo "   ✅ Found environment-specific directory: results-$env/vibium/test-results"
            echo "   Converting Vibium results ($env) from results-$env/vibium/test-results..."
            chmod +x scripts/ci/convert-vibium-to-allure.sh
            if ./scripts/ci/convert-vibium-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/results-$env/vibium/test-results" "$env"; then
                ENV_PROCESSED=1
                echo "   ✅ Vibium conversion successful for $env (environment-specific data)"
            else
                echo "   ⚠️  Vibium conversion failed for $env (exit code: $?)"
            fi
        elif [ -d "$SOURCE_DIR/results-$env/vibium/.vitest" ]; then
            echo "   ✅ Found environment-specific directory: results-$env/vibium/.vitest"
            echo "   Converting Vibium results ($env) from results-$env/vibium/.vitest..."
            chmod +x scripts/ci/convert-vibium-to-allure.sh
            if ./scripts/ci/convert-vibium-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/results-$env/vibium/.vitest" "$env"; then
                ENV_PROCESSED=1
                echo "   ✅ Vibium conversion successful for $env (environment-specific data)"
            else
                echo "   ⚠️  Vibium conversion failed for $env (exit code: $?)"
            fi
        # Structure 4: Search recursively in results-$env for Vibium JSON files
        else
            json_file=$(find "$SOURCE_DIR/results-$env" \( -name "vitest-results.json" -o -path "*/test-results/*.json" -o -path "*/.vitest/*.json" \) 2>/dev/null | head -1)
            if [ -n "$json_file" ] && [ -f "$json_file" ]; then
                json_dir=$(dirname "$json_file")
                echo "   ✅ Found Vibium JSON file in results-$env: $json_file"
                echo "   Converting Vibium results ($env) from $json_dir..."
                chmod +x scripts/ci/convert-vibium-to-allure.sh
                if ./scripts/ci/convert-vibium-to-allure.sh "$TARGET_DIR" "$json_dir" "$env"; then
                    ENV_PROCESSED=1
                    echo "   ✅ Vibium conversion successful for $env (environment-specific data)"
                else
                    echo "   ⚠️  Vibium conversion failed for $env (exit code: $?)"
                fi
            fi
        fi
    fi
    
    # Also check merged directory with environment-specific subdirectory (vibium-results/vibium-results-{env}/)
    # Artifacts preserve full upload path: vibium-results/vibium-results-{env}/vibium/test-results/ or vibium-results/vibium-results-{env}/vibium/.vitest/
    if [ "$ENV_PROCESSED" -eq 0 ] && [ -d "$SOURCE_DIR/vibium-results/vibium-results-$env" ]; then
        echo "   Converting Vibium results ($env) from merged vibium-results/vibium-results-$env..."
        chmod +x scripts/ci/convert-vibium-to-allure.sh
        # Check nested paths first (artifacts preserve full upload path)
        if [ -d "$SOURCE_DIR/vibium-results/vibium-results-$env/vibium/test-results" ]; then
            echo "   📂 Found Vibium results directory (nested path): $SOURCE_DIR/vibium-results/vibium-results-$env/vibium/test-results"
            if ./scripts/ci/convert-vibium-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/vibium-results/vibium-results-$env/vibium/test-results" "$env"; then
                ENV_PROCESSED=1
                echo "   ✅ Vibium conversion successful for $env"
            else
                echo "   ⚠️  Vibium conversion failed for $env (exit code: $?)"
            fi
        elif [ -d "$SOURCE_DIR/vibium-results/vibium-results-$env/vibium/.vitest" ]; then
            echo "   📂 Found Vibium .vitest directory (nested path): $SOURCE_DIR/vibium-results/vibium-results-$env/vibium/.vitest"
            if ./scripts/ci/convert-vibium-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/vibium-results/vibium-results-$env/vibium/.vitest" "$env"; then
                ENV_PROCESSED=1
                echo "   ✅ Vibium conversion successful for $env"
            else
                echo "   ⚠️  Vibium conversion failed for $env (exit code: $?)"
            fi
        elif [ -d "$SOURCE_DIR/vibium-results/vibium-results-$env/test-results" ]; then
            echo "   📂 Found Vibium results directory: $SOURCE_DIR/vibium-results/vibium-results-$env/test-results"
            if ./scripts/ci/convert-vibium-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/vibium-results/vibium-results-$env/test-results" "$env"; then
                ENV_PROCESSED=1
                echo "   ✅ Vibium conversion successful for $env"
            else
                echo "   ⚠️  Vibium conversion failed for $env (exit code: $?)"
            fi
        elif [ -d "$SOURCE_DIR/vibium-results/vibium-results-$env/.vitest" ]; then
            echo "   📂 Found Vibium .vitest directory: $SOURCE_DIR/vibium-results/vibium-results-$env/.vitest"
            if ./scripts/ci/convert-vibium-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/vibium-results/vibium-results-$env/.vitest" "$env"; then
                ENV_PROCESSED=1
                echo "   ✅ Vibium conversion successful for $env"
            else
                echo "   ⚠️  Vibium conversion failed for $env (exit code: $?)"
            fi
        else
            echo "   ⚠️  No Vibium test-results or .vitest directory found in $SOURCE_DIR/vibium-results/vibium-results-$env"
        fi
    fi
    
    # FIXED: Check flat structure as LAST RESORT (when merge-multiple: true creates flat structure)
    # WARNING: Flat structure cannot distinguish environments - only process once to avoid duplicate data
    if [ "$ENV_PROCESSED" -eq 0 ]; then
        # Check if flat structure exists (vibium-results/test-results/ or vibium-results/.vitest/)
        if [ -d "$SOURCE_DIR/vibium-results/test-results" ] || [ -d "$SOURCE_DIR/vibium-results/.vitest" ]; then
            # Only process for first environment to avoid processing same files multiple times
            # Flat structure means we can't distinguish which environment the files belong to
            if [ "$env" == "${ACTIVE_ENVIRONMENTS[0]}" ]; then
                echo "   ⚠️  WARNING: No environment-specific subdirectories found, processing flat structure"
                echo "   ⚠️  WARNING: Flat structure cannot distinguish environments - processing for ${ACTIVE_ENVIRONMENTS[0]} only"
                echo "   ⚠️  WARNING: Other environments will not have Vibium results if flat structure is used"
                echo "   📂 Found Vibium results in flat structure, processing for environment: $env"
                chmod +x scripts/ci/convert-vibium-to-allure.sh
                if [ -d "$SOURCE_DIR/vibium-results/test-results" ]; then
                    if ./scripts/ci/convert-vibium-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/vibium-results/test-results" "$env"; then
                        ENV_PROCESSED=1
                        echo "   ✅ Vibium conversion successful for $env (flat structure - WARNING: same data for all environments)"
                    fi
                elif [ -d "$SOURCE_DIR/vibium-results/.vitest" ]; then
                    if ./scripts/ci/convert-vibium-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/vibium-results/.vitest" "$env"; then
                        ENV_PROCESSED=1
                        echo "   ✅ Vibium conversion successful for $env (flat structure, .vitest path - WARNING: same data for all environments)"
                    fi
                fi
            else
                echo "   ⏭️  Skipping $env (flat structure already processed for ${ACTIVE_ENVIRONMENTS[0]} - cannot distinguish environments in flat structure)"
            fi
        else
            # Debug: Report if Vibium results were not found for this environment
            echo "   ⚠️  No Vibium results found for $env environment"
            echo "      Checked locations:"
            echo "        - $SOURCE_DIR/results-$env/vibium-results-$env/test-results"
            echo "        - $SOURCE_DIR/results-$env/vibium-results-$env/.vitest"
            echo "        - $SOURCE_DIR/vibium-results/vibium-results-$env/test-results"
            echo "        - $SOURCE_DIR/vibium-results/vibium-results-$env/.vitest"
            echo "        - $SOURCE_DIR/vibium-results/vibium-results-$env/vibium/test-results (nested path)"
            echo "        - $SOURCE_DIR/vibium-results/vibium-results-$env/vibium/.vitest (nested path)"
            echo "        - $SOURCE_DIR/vibium-results/test-results (flat structure)"
            echo "        - $SOURCE_DIR/vibium-results/.vitest (flat structure)"
        fi
    fi
done

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
# When merged, structure is: fs-results/fs-results-{env}/artillery-results/*.json
# (The artifact name becomes a directory, and the uploaded path is preserved inside it)

if [ -d "$SOURCE_DIR/fs-results" ]; then
    echo "   ✅ Found fs-results directory"
    
    # Process each active environment
    for env in "${FS_ENVIRONMENTS[@]}"; do
        # Skip if this environment wasn't active
        if [[ ! " ${ACTIVE_ENVIRONMENTS[@]} " =~ " ${env} " ]]; then
            echo "   ⏭️  Skipping FS test conversion for $env (environment not active)"
            continue
        fi
        
        ENV_PROCESSED=0
        
        # Check environment-specific directory first (results-dev, results-test)
        # This is the PRIMARY source - ensures we use environment-specific data
        if [ -d "$SOURCE_DIR/results-$env" ]; then
            # Try multiple possible structures in results-$env
            # Structure 1: results-$env/fs-results-$env/playwright/artillery-results/
            if [ -d "$SOURCE_DIR/results-$env/fs-results-$env/playwright/artillery-results" ]; then
                json_count=$(find "$SOURCE_DIR/results-$env/fs-results-$env/playwright/artillery-results" -name "*.json" -type f 2>/dev/null | wc -l | tr -d ' ')
                if [ "$json_count" -gt 0 ]; then
                    echo "   ✅ Found environment-specific directory: results-$env/fs-results-$env/playwright/artillery-results"
                    echo "   🔄 Converting FS test results for $env (found $json_count JSON file(s))..."
                    chmod +x scripts/ci/convert-artillery-to-allure.sh
                    if ./scripts/ci/convert-artillery-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/results-$env/fs-results-$env/playwright/artillery-results" "$env"; then
                        ENV_PROCESSED=1
                        echo "   ✅ FS conversion successful for $env (environment-specific data)"
                    else
                        echo "   ⚠️  FS conversion failed for $env (exit code: $?)"
                    fi
                fi
            # Structure 2: results-$env/fs-results-$env/artillery-results/
            elif [ -d "$SOURCE_DIR/results-$env/fs-results-$env/artillery-results" ]; then
                json_count=$(find "$SOURCE_DIR/results-$env/fs-results-$env/artillery-results" -name "*.json" -type f 2>/dev/null | wc -l | tr -d ' ')
                if [ "$json_count" -gt 0 ]; then
                    echo "   ✅ Found environment-specific directory: results-$env/fs-results-$env/artillery-results"
                    echo "   🔄 Converting FS test results for $env (found $json_count JSON file(s))..."
                    chmod +x scripts/ci/convert-artillery-to-allure.sh
                    if ./scripts/ci/convert-artillery-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/results-$env/fs-results-$env/artillery-results" "$env"; then
                        ENV_PROCESSED=1
                        echo "   ✅ FS conversion successful for $env (environment-specific data)"
                    else
                        echo "   ⚠️  FS conversion failed for $env (exit code: $?)"
                    fi
                fi
            # Structure 3: results-$env/playwright/artillery-results/ (if artifact preserves full path)
            elif [ -d "$SOURCE_DIR/results-$env/playwright/artillery-results" ]; then
                json_count=$(find "$SOURCE_DIR/results-$env/playwright/artillery-results" -name "*.json" -type f 2>/dev/null | wc -l | tr -d ' ')
                if [ "$json_count" -gt 0 ]; then
                    echo "   ✅ Found environment-specific directory: results-$env/playwright/artillery-results"
                    echo "   🔄 Converting FS test results for $env (found $json_count JSON file(s))..."
                    chmod +x scripts/ci/convert-artillery-to-allure.sh
                    if ./scripts/ci/convert-artillery-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/results-$env/playwright/artillery-results" "$env"; then
                        ENV_PROCESSED=1
                        echo "   ✅ FS conversion successful for $env (environment-specific data)"
                    else
                        echo "   ⚠️  FS conversion failed for $env (exit code: $?)"
                    fi
                fi
            # Structure 4: Search recursively in results-$env for Artillery JSON files
            else
                json_file=$(find "$SOURCE_DIR/results-$env" -name "*.json" -path "*/artillery-results/*" 2>/dev/null | head -1)
                if [ -n "$json_file" ] && [ -f "$json_file" ]; then
                    json_dir=$(dirname "$json_file")
                    echo "   ✅ Found FS JSON file in results-$env: $json_file"
                    echo "   🔄 Converting FS test results for $env from $json_dir..."
                    chmod +x scripts/ci/convert-artillery-to-allure.sh
                    if ./scripts/ci/convert-artillery-to-allure.sh "$TARGET_DIR" "$json_dir" "$env"; then
                        ENV_PROCESSED=1
                        echo "   ✅ FS conversion successful for $env (environment-specific data)"
                    else
                        echo "   ⚠️  FS conversion failed for $env (exit code: $?)"
                    fi
                fi
            fi
        fi
        
        # Also check merged directory with environment-specific subdirectory (fs-results/fs-results-{env}/)
        # Artifacts preserve full upload path: fs-results/fs-results-{env}/playwright/artillery-results/*.json
        if [ "$ENV_PROCESSED" -eq 0 ]; then
            env_dir="$SOURCE_DIR/fs-results/fs-results-$env"
            if [ -d "$env_dir" ]; then
            # First try: nested path (artifacts preserve full upload path)
            # Structure: fs-results/fs-results-{env}/playwright/artillery-results/*.json
            artillery_dir="$env_dir/playwright/artillery-results"
            if [ -d "$artillery_dir" ]; then
                json_count=$(find "$artillery_dir" -name "*.json" -type f 2>/dev/null | wc -l | tr -d ' ')
                if [ "$json_count" -gt 0 ]; then
                    echo "   🔄 Converting FS test results for $env (found $json_count JSON file(s) in fs-results-$env/playwright/artillery-results/)..."
                    chmod +x scripts/ci/convert-artillery-to-allure.sh
                    if ./scripts/ci/convert-artillery-to-allure.sh "$TARGET_DIR" "$artillery_dir" "$env"; then
                        ENV_PROCESSED=1
                        echo "   ✅ FS conversion successful for $env"
                    else
                        echo "   ⚠️  FS conversion failed for $env (exit code: $?)"
                    fi
                fi
            fi
            
            # Fallback: check non-nested path fs-results/fs-results-{env}/artillery-results/*.json
            if [ "$ENV_PROCESSED" -eq 0 ]; then
                artillery_dir="$env_dir/artillery-results"
                if [ -d "$artillery_dir" ]; then
                    json_count=$(find "$artillery_dir" -name "*.json" -type f 2>/dev/null | wc -l | tr -d ' ')
                    if [ "$json_count" -gt 0 ]; then
                        echo "   🔄 Converting FS test results for $env (found $json_count JSON file(s) in fs-results-$env/artillery-results/)..."
                        chmod +x scripts/ci/convert-artillery-to-allure.sh
                        if ./scripts/ci/convert-artillery-to-allure.sh "$TARGET_DIR" "$artillery_dir" "$env"; then
                            ENV_PROCESSED=1
                            echo "   ✅ FS conversion successful for $env"
                        else
                            echo "   ⚠️  FS conversion failed for $env (exit code: $?)"
                        fi
                    fi
                fi
            fi
            
            # Final fallback: check if files are directly in fs-results-{env}
            if [ "$ENV_PROCESSED" -eq 0 ]; then
                json_files=$(find "$env_dir" -maxdepth 1 -name "*.json" -type f 2>/dev/null)
                if [ -n "$json_files" ]; then
                    json_count=$(echo "$json_files" | wc -l | tr -d ' ')
                    echo "   🔄 Converting FS test results for $env (found $json_count JSON file(s) in fs-results-$env/)..."
                    chmod +x scripts/ci/convert-artillery-to-allure.sh
                    if ./scripts/ci/convert-artillery-to-allure.sh "$TARGET_DIR" "$env_dir" "$env"; then
                        ENV_PROCESSED=1
                        echo "   ✅ FS conversion successful for $env"
                    else
                        echo "   ⚠️  FS conversion failed for $env (exit code: $?)"
                    fi
                fi
            fi
            fi
        fi
        
        # FIXED: Check flat structure as LAST RESORT (when merge-multiple: true creates flat structure)
        # WARNING: Flat structure cannot distinguish environments - only process once to avoid duplicate data
        if [ "$ENV_PROCESSED" -eq 0 ]; then
            # Check if flat structure exists (fs-results/*.json or fs-results/playwright/artillery-results/*.json)
            if [ -d "$SOURCE_DIR/fs-results" ] && [ -n "$(find "$SOURCE_DIR/fs-results" -maxdepth 2 -name "*.json" -type f 2>/dev/null)" ]; then
                # Only process for first FS environment to avoid processing same files multiple times
                # Flat structure means we can't distinguish which environment the files belong to
                if [ "$env" == "${FS_ENVIRONMENTS[0]}" ]; then
                    echo "   ⚠️  WARNING: No environment-specific subdirectories found, processing flat structure"
                    echo "   ⚠️  WARNING: Flat structure cannot distinguish environments - processing for ${FS_ENVIRONMENTS[0]} only"
                    echo "   ⚠️  WARNING: Other environments will not have FS results if flat structure is used"
                    echo "   📂 Found FS results in flat structure, processing for environment: $env"
                    chmod +x scripts/ci/convert-artillery-to-allure.sh
                    # Check for nested path first
                    if [ -d "$SOURCE_DIR/fs-results/playwright/artillery-results" ]; then
                        if ./scripts/ci/convert-artillery-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/fs-results/playwright/artillery-results" "$env"; then
                            ENV_PROCESSED=1
                            echo "   ✅ FS conversion successful for $env (flat structure, nested path - WARNING: same data for all environments)"
                        fi
                    # Check for direct JSON files in fs-results root
                    elif [ -n "$(find "$SOURCE_DIR/fs-results" -maxdepth 1 -name "*.json" -type f 2>/dev/null)" ]; then
                        if ./scripts/ci/convert-artillery-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/fs-results" "$env"; then
                            ENV_PROCESSED=1
                            echo "   ✅ FS conversion successful for $env (flat structure - WARNING: same data for all environments)"
                        fi
                    fi
                else
                    echo "   ⏭️  Skipping $env (flat structure already processed for ${FS_ENVIRONMENTS[0]} - cannot distinguish environments in flat structure)"
                fi
            fi
        fi
        
        # Verify results were created
        if [ "$ENV_PROCESSED" -eq 1 ]; then
            # Count results created for this environment
            env_results=$(find "$TARGET_DIR" -name "*-result.json" -newer "$SOURCE_DIR/fs-results" -exec grep -l "\"environment\", \"value\": \"$env\"" {} \; 2>/dev/null | wc -l | tr -d ' ')
            # Fallback: just count recent results if the above doesn't work
            if [ "$env_results" -eq 0 ]; then
                env_results=$(find "$TARGET_DIR" -name "*-result.json" -exec grep -l "Artillery.*$env" {} \; 2>/dev/null | wc -l | tr -d ' ')
            fi
            if [ "$env_results" -gt 0 ]; then
                FS_PROCESSED_ENVS+=("$env")
                echo "   ✅ FS test results processed for $env ($env_results result file(s))"
            else
                echo "   ⚠️  FS test conversion ran for $env but no results were created"
            fi
        else
            echo "   ⚠️  No FS test results found for $env"
            echo "      Checked locations:"
            echo "        - $SOURCE_DIR/fs-results/fs-results-$env/playwright/artillery-results/ (nested path)"
            if [ -d "$SOURCE_DIR/fs-results/fs-results-$env/playwright/artillery-results" ]; then
                echo "          Directory exists, searching for JSON files:"
                find "$SOURCE_DIR/fs-results/fs-results-$env/playwright/artillery-results" -name "*.json" 2>/dev/null | head -5 | while read f; do
                    echo "            Found: $f"
                done || echo "            (no JSON files found)"
            else
                echo "          Directory does not exist"
            fi
            echo "        - $SOURCE_DIR/fs-results/fs-results-$env/artillery-results/"
            if [ -d "$SOURCE_DIR/fs-results/fs-results-$env/artillery-results" ]; then
                echo "          Directory exists, searching for JSON files:"
                find "$SOURCE_DIR/fs-results/fs-results-$env/artillery-results" -name "*.json" 2>/dev/null | head -5 | while read f; do
                    echo "            Found: $f"
                done || echo "            (no JSON files found)"
            else
                echo "          Directory does not exist"
            fi
            echo "        - $SOURCE_DIR/fs-results/fs-results-$env/"
            if [ -d "$SOURCE_DIR/fs-results/fs-results-$env" ]; then
                echo "          Directory exists, searching for JSON files:"
                find "$SOURCE_DIR/fs-results/fs-results-$env" -maxdepth 1 -name "*.json" 2>/dev/null | head -5 | while read f; do
                    echo "            Found: $f"
                done || echo "            (no JSON files found)"
            else
                echo "          Directory does not exist"
            fi
        fi
    done
else
    echo "   ⚠️  fs-results directory not found at: $SOURCE_DIR/fs-results"
    echo "      This is expected if FS tests did not run or artifacts were not uploaded"
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

