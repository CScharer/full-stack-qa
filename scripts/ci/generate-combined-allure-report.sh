#!/bin/bash
# Generate Combined Allure Report
# Usage: ./scripts/ci/generate-combined-allure-report.sh [results-dir] [report-dir]
#
# Arguments:
#   results-dir  - Directory containing Allure results (default: allure-results-combined)
#   report-dir   - Directory where Allure report will be generated (default: allure-report-combined)
#
# This script:
# 1. Verifies results directory exists and has files
# 2. Generates Allure HTML report
# 3. Preserves history for next run
# 4. Verifies report was generated successfully

set -e

RESULTS_DIR="${1:-allure-results-combined}"
REPORT_DIR="${2:-allure-report-combined}"

echo "📊 Generating combined Allure report..."
echo "   Results directory: $RESULTS_DIR"
echo "   Report directory: $REPORT_DIR"
echo ""

# Verify results directory exists
if [ ! -d "$RESULTS_DIR" ]; then
    echo "❌ Error: Results directory not found: $RESULTS_DIR"
    exit 1
fi

# Count result files
RESULT_COUNT=$(find "$RESULTS_DIR" -name "*-result.json" 2>/dev/null | wc -l | tr -d ' ')
echo "📊 Found $RESULT_COUNT result files to process"

if [ "$RESULT_COUNT" -eq 0 ]; then
    echo "⚠️  Warning: No result files found, but continuing..."
fi

# Verify categories.json exists
if [ -f "$RESULTS_DIR/categories.json" ]; then
    echo "✅ Categories file found: $RESULTS_DIR/categories.json"
    echo "   File size: $(wc -l < "$RESULTS_DIR/categories.json" | tr -d ' ') lines"
else
    echo "⚠️  Warning: categories.json not found in results directory"
    echo "   This may cause Categories section to be missing from report"
fi

# Verify executor.json exists
if [ -f "$RESULTS_DIR/executor.json" ]; then
    echo "✅ Executor file found: $RESULTS_DIR/executor.json"
else
    echo "⚠️  Warning: executor.json not found in results directory"
fi

# Count container files (critical for Suites tab)
CONTAINER_COUNT=$(find "$RESULTS_DIR" -name "*-container.json" 2>/dev/null | wc -l | tr -d ' ')
echo "📦 Found $CONTAINER_COUNT container files (required for Suites tab)"

if [ "$CONTAINER_COUNT" -eq 0 ]; then
    echo "⚠️  WARNING: No container files found!"
    echo "   This will cause Suites tab to be empty or incomplete"
    echo "   Container files should be created by create-framework-containers.sh in Step 4.5"
    echo "   Check pipeline logs for 'Step 4.5: Creating framework container files...' output"
else
    echo "   ✅ Container files present - Suites tab should display correctly"
    # Show container file breakdown by framework
    echo "   📊 Container breakdown:"
    
    # Count containers by framework/environment
    # Extract framework names from all container files
    FRAMEWORK_COUNT_FILE=$(mktemp)
    find "$RESULTS_DIR" -name "*-container.json" 2>/dev/null | head -50 | while read -r container_file; do
        if [ -f "$container_file" ]; then
            container_name=$(grep -o '"name"[[:space:]]*:[[:space:]]*"[^"]*"' "$container_file" 2>/dev/null | head -1 | sed 's/.*"name"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/' || echo "unknown")
            # Extract framework name (remove [ENV] suffix if present)
            framework=$(echo "$container_name" | sed 's/ \[.*\]$//' || echo "$container_name")
            echo "$framework" >> "$FRAMEWORK_COUNT_FILE"
        fi
    done
    
    # Show framework counts
    if [ -f "$FRAMEWORK_COUNT_FILE" ] && [ -s "$FRAMEWORK_COUNT_FILE" ]; then
        sort "$FRAMEWORK_COUNT_FILE" | uniq -c | while read -r count framework; do
            echo "      - $framework: $count container(s)"
        done
        found_frameworks=$(sort "$FRAMEWORK_COUNT_FILE" | uniq | tr '\n' ', ' | sed 's/, $//')
        rm -f "$FRAMEWORK_COUNT_FILE"
    else
        found_frameworks="none"
    fi
    
    # Show sample container files
    echo "   📋 Sample container files (first 10):"
    find "$RESULTS_DIR" -name "*-container.json" -exec basename {} \; 2>/dev/null | head -10 | while read -r container_file; do
        if [ -f "$RESULTS_DIR/$container_file" ]; then
            container_name=$(grep -o '"name"[[:space:]]*:[[:space:]]*"[^"]*"' "$RESULTS_DIR/$container_file" 2>/dev/null | head -1 | sed 's/.*"name"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/' || echo "unknown")
            children_count=$(grep -o '"children"[[:space:]]*:[[:space:]]*\[[^]]*\]' "$RESULTS_DIR/$container_file" 2>/dev/null | grep -o '"[^"]*"' | wc -l | tr -d ' ' || echo "0")
            echo "      - $container_file: '$container_name' ($children_count children)"
        fi
    done
    if [ "$CONTAINER_COUNT" -gt 10 ]; then
        echo "      ... and $((CONTAINER_COUNT - 10)) more container files"
    fi
    
    # Expected frameworks check
    echo "   🔍 Expected frameworks: Cypress, Playwright, Robot, Vibium, Selenide, Surefire"
    echo "   📊 Found frameworks: ${found_frameworks:-none}"
fi

# Ensure history directory exists in results (needed for Allure3 to merge history)
# History should have been downloaded earlier from GitHub Pages or artifact
# CRITICAL: History MUST be in RESULTS_DIR BEFORE 'allure generate' for Allure3 to merge it
# IMPORTANT: Allure3 requires actual history entries, not empty arrays
if [ -d "$RESULTS_DIR/history" ] && [ "$(find "$RESULTS_DIR/history" -type f -name "*.json" 2>/dev/null | wc -l | tr -d ' ')" -gt 0 ]; then
    echo ""
    echo "📊 History found in results directory:"
    HISTORY_FILE_COUNT=$(find "$RESULTS_DIR/history" -type f -name "*.json" 2>/dev/null | wc -l | tr -d ' ')
    
    # Check if history files are empty (just [] or empty objects)
    EMPTY_COUNT=0
    for json_file in "$RESULTS_DIR/history"/*.json; do
        if [ -f "$json_file" ]; then
            # Check if file is empty or just contains [] or {}
            CONTENT=$(cat "$json_file" 2>/dev/null | tr -d '[:space:]' || echo "")
            if [ "$CONTENT" = "[]" ] || [ "$CONTENT" = "{}" ] || [ -z "$CONTENT" ]; then
                EMPTY_COUNT=$((EMPTY_COUNT + 1))
            fi
        fi
    done
    
    if [ "$EMPTY_COUNT" -eq "$HISTORY_FILE_COUNT" ] && [ "$HISTORY_FILE_COUNT" -gt 0 ]; then
        # All history files are empty - Allure3 doesn't recognize empty arrays as valid history
        echo "   ⚠️  All history files are empty (just [] or {})"
        echo "   Allure3 doesn't recognize empty arrays as valid history to merge"
        echo "   Removing empty history files to let Allure3 start fresh..."
        rm -f "$RESULTS_DIR/history"/*.json
        echo "   ✅ Empty history files removed"
        echo "   Allure3 will create history naturally from test results"
    else
        # History has actual data
        echo "   Files: $HISTORY_FILE_COUNT file(s)"
        echo "   Size: $(du -sh "$RESULTS_DIR/history" 2>/dev/null | cut -f1 || echo 'unknown')"
        echo "   ✅ History will be merged with new results during report generation"
        echo "   Allure3 will create updated history in the generated report"
    fi
else
    # No history exists - this is expected for the first few runs
    # Allure3 will create history naturally after 2-3 runs when it has enough data
    echo ""
    echo "ℹ️  No history found in results directory (expected for first few runs)"
    echo "   History will be created naturally by Allure3 after multiple runs"
    echo "   Allure3 needs actual test execution data to create meaningful history"
    echo "   This is normal behavior - history will appear after 2-3 pipeline runs"
fi

# Generate report
# Note: Allure3 CLI doesn't support --clean flag, so we remove the directory first
# Allure3 automatically merges history from RESULTS_DIR/history/ if it exists
# CRITICAL: History must be in RESULTS_DIR BEFORE this command for Allure3 to merge it
echo ""
echo "🔄 Generating Allure report..."
echo "   (Allure3 will merge history from $RESULTS_DIR/history/ with new results)"
rm -rf "$REPORT_DIR"
allure generate "$RESULTS_DIR" -o "$REPORT_DIR"

# Preserve history for next run (copy from report back to results)
# Allure3 creates history in REPORT_DIR/history/ after generation
# We copy it back to RESULTS_DIR so it's available for the next pipeline run
if [ -d "$REPORT_DIR/history" ]; then
    # Check if history directory has actual files (not just empty arrays)
    HISTORY_FILE_COUNT=$(find "$REPORT_DIR/history" -type f -name "*.json" 2>/dev/null | wc -l | tr -d ' ')
    
    if [ "$HISTORY_FILE_COUNT" -gt 0 ]; then
        # Check if all files are empty (just [] or {})
        EMPTY_COUNT=0
        for json_file in "$REPORT_DIR/history"/*.json; do
            if [ -f "$json_file" ]; then
                CONTENT=$(cat "$json_file" 2>/dev/null | tr -d '[:space:]' || echo "")
                if [ "$CONTENT" = "[]" ] || [ "$CONTENT" = "{}" ] || [ -z "$CONTENT" ]; then
                    EMPTY_COUNT=$((EMPTY_COUNT + 1))
                fi
            fi
        done
        
        if [ "$EMPTY_COUNT" -eq "$HISTORY_FILE_COUNT" ] && [ "$HISTORY_FILE_COUNT" -gt 0 ]; then
            # All history files are empty - remove them to prevent deployment
            echo ""
            echo "⚠️  All history files in report are empty (just [] or {})"
            echo "   Removing empty history directory to prevent deployment of empty files"
            rm -rf "$REPORT_DIR/history"
            echo "   ✅ Empty history directory removed from report"
            echo "   Allure3 will create history naturally in future runs"
        else
            # History has actual data - preserve it
            echo ""
            echo "📊 Preserving history for next run..."
            mkdir -p "$RESULTS_DIR/history"
            cp -r "$REPORT_DIR/history"/* "$RESULTS_DIR/history/" 2>/dev/null || true
            ACTUAL_FILE_COUNT=$(find "$RESULTS_DIR/history" -type f -name "*.json" 2>/dev/null | wc -l | tr -d ' ')
            echo "✅ History preserved: $ACTUAL_FILE_COUNT file(s) ready for next report generation"
            echo "   History will be merged with new results in the next pipeline run"
        fi
    else
        # History directory exists but is empty - remove it
        echo ""
        echo "⚠️  History directory exists but is empty"
        echo "   Removing empty history directory to prevent deployment of empty files"
        rm -rf "$REPORT_DIR/history"
        echo "   ✅ Empty history directory removed from report"
    fi
else
    # Allure3 didn't create history - this happens on first run when no history exists
    # We need to bootstrap history from the current run's test results
    echo ""
    echo "ℹ️  No history directory in generated report"
    echo "   Allure3 requires existing history to merge with - it doesn't bootstrap on first run"
    echo "   Bootstrapping history from current run's test results..."
    
    # Get buildOrder from executor.json
    BUILD_ORDER="1"
    if [ -f "$RESULTS_DIR/executor.json" ]; then
        BUILD_ORDER=$(grep -o '"buildOrder"[[:space:]]*:[[:space:]]*"[^"]*"' "$RESULTS_DIR/executor.json" 2>/dev/null | sed 's/.*"buildOrder"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/' || echo "1")
    fi
    
    # Create history directory
    mkdir -p "$REPORT_DIR/history"
    
    # Extract test data from result files and create bootstrap history
    BOOTSTRAP_COUNT=0
    
    # Process result files to extract test execution data
    if [ "$RESULT_COUNT" -gt 0 ]; then
        echo "   Extracting test data from $RESULT_COUNT result file(s)..."
        
        # Use jq if available to extract test data
        if command -v jq &> /dev/null; then
            # Create temporary file to collect history entries
            TEMP_HISTORY=$(mktemp)
            
            # Extract test data from result files using jq
            find "$RESULTS_DIR" -name "*-result.json" -type f 2>/dev/null | head -100 | while read -r result_file; do
                if [ -f "$result_file" ]; then
                    # Extract fields and create history entry
                    jq -c --arg build_order "$BUILD_ORDER" '
                        select(.uuid != null and .start != null and .stop != null) |
                        {
                            uid: .uuid,
                            status: .status,
                            time: {
                                start: .start,
                                stop: .stop,
                                duration: (.stop - .start)
                            }
                        }
                    ' "$result_file" 2>/dev/null >> "$TEMP_HISTORY" || true
                fi
            done
            
            # Count entries and create history structure
            if [ -f "$TEMP_HISTORY" ] && [ -s "$TEMP_HISTORY" ]; then
                BOOTSTRAP_COUNT=$(wc -l < "$TEMP_HISTORY" | tr -d ' ')
                
                # Create history-trend.json with build entry
                jq -s --argjson build_order "$BUILD_ORDER" \
                    '[{buildOrder: $build_order, reportUrl: "", reportName: "Allure Report", data: .}]' \
                    "$TEMP_HISTORY" > "$REPORT_DIR/history/history-trend.json" 2>/dev/null || true
                
                # Create duration-trend.json
                jq -s --argjson build_order "$BUILD_ORDER" \
                    '[{buildOrder: $build_order, data: [.[] | {uid: .uid, time: .time}]}]' \
                    "$TEMP_HISTORY" > "$REPORT_DIR/history/duration-trend.json" 2>/dev/null || true
                
                # Create retry-trend.json (empty for now, will be populated by Allure3 in future runs)
                echo "[{\"buildOrder\":$BUILD_ORDER,\"data\":[]}]" | jq '.' > "$REPORT_DIR/history/retry-trend.json" 2>/dev/null || true
                
                rm -f "$TEMP_HISTORY"
            else
                # No valid entries extracted - create minimal structure
                echo "[{\"buildOrder\":$BUILD_ORDER,\"reportUrl\":\"\",\"reportName\":\"Allure Report\",\"data\":[]}]" | jq '.' > "$REPORT_DIR/history/history-trend.json" 2>/dev/null || echo "[{\"buildOrder\":$BUILD_ORDER,\"reportUrl\":\"\",\"reportName\":\"Allure Report\",\"data\":[]}]" > "$REPORT_DIR/history/history-trend.json"
                echo "[{\"buildOrder\":$BUILD_ORDER,\"data\":[]}]" | jq '.' > "$REPORT_DIR/history/duration-trend.json" 2>/dev/null || echo "[{\"buildOrder\":$BUILD_ORDER,\"data\":[]}]" > "$REPORT_DIR/history/duration-trend.json"
                echo "[{\"buildOrder\":$BUILD_ORDER,\"data\":[]}]" | jq '.' > "$REPORT_DIR/history/retry-trend.json" 2>/dev/null || echo "[{\"buildOrder\":$BUILD_ORDER,\"data\":[]}]" > "$REPORT_DIR/history/retry-trend.json"
                rm -f "$TEMP_HISTORY"
            fi
        else
            # Fallback: Create minimal valid structure without jq
            echo "   ⚠️  jq not available - creating minimal bootstrap history structure"
            echo "[{\"buildOrder\":$BUILD_ORDER,\"reportUrl\":\"\",\"reportName\":\"Allure Report\",\"data\":[]}]" > "$REPORT_DIR/history/history-trend.json"
            echo "[{\"buildOrder\":$BUILD_ORDER,\"data\":[]}]" > "$REPORT_DIR/history/duration-trend.json"
            echo "[{\"buildOrder\":$BUILD_ORDER,\"data\":[]}]" > "$REPORT_DIR/history/retry-trend.json"
        fi
        
        if [ "$BOOTSTRAP_COUNT" -gt 0 ]; then
            echo "   ✅ Bootstrap history created with $BOOTSTRAP_COUNT test entry/entries"
            echo "   History will be preserved for next run, allowing Allure3 to merge and create trends"
        else
            echo "   ⚠️  Created minimal bootstrap history structure (no test data extracted)"
            echo "   History structure is valid and will allow Allure3 to start creating trends in next run"
        fi
    else
        echo "   ⚠️  No result files found - creating minimal bootstrap history structure"
        echo "[{\"buildOrder\":$BUILD_ORDER,\"reportUrl\":\"\",\"reportName\":\"Allure Report\",\"data\":[]}]" > "$REPORT_DIR/history/history-trend.json"
        echo "[{\"buildOrder\":$BUILD_ORDER,\"data\":[]}]" > "$REPORT_DIR/history/duration-trend.json"
        echo "[{\"buildOrder\":$BUILD_ORDER,\"data\":[]}]" > "$REPORT_DIR/history/retry-trend.json"
    fi
    
    # Verify bootstrap history was created
    if [ -d "$REPORT_DIR/history" ] && [ "$(find "$REPORT_DIR/history" -name "*.json" 2>/dev/null | wc -l | tr -d ' ')" -gt 0 ]; then
        echo "   ✅ Bootstrap history directory created successfully"
        echo "   Files: $(find "$REPORT_DIR/history" -name "*.json" 2>/dev/null | wc -l | tr -d ' ') file(s)"
    else
        echo "   ⚠️  Warning: Bootstrap history creation may have failed"
    fi
fi

# Verify report was generated
echo ""
echo "✅ Verifying report generation..."

if [ ! -d "$REPORT_DIR" ]; then
    echo "❌ Error: Report directory was not created"
    exit 1
fi

if [ ! -f "$REPORT_DIR/index.html" ]; then
    echo "❌ Error: Report index.html was not created"
    exit 1
fi

REPORT_SIZE=$(du -sh "$REPORT_DIR" | cut -f1)
echo "✅ Combined report generated successfully!"
echo "   Report location: $REPORT_DIR/"
echo "   Report size: $REPORT_SIZE"
echo "   Result files processed: $RESULT_COUNT"

