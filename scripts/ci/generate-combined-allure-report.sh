#!/bin/bash
# Generate Combined Allure Report (supports Allure2 and Allure3)
# Usage: ./scripts/ci/generate-combined-allure-report.sh [results-dir] [report-dir]
#
# Arguments:
#   results-dir  - Directory containing Allure results (default: allure-results-combined)
#   report-dir   - Directory where Allure report will be generated (default: allure-report-combined)
#
# Configuration:
#   Reads from config/environments.json (allure.reportVersion)
#   Can be overridden via ALLURE_REPORT_VERSION environment variable
#
# This script:
# 1. Verifies results directory exists and has files
# 2. Reads Allure version from config/environments.json
# 3. Detects history format based on Allure version (Allure2: JSON files, Allure3: JSONL)
# 4. Generates Allure HTML report (version-specific processing)
# 5. Preserves history for next run
# 6. Verifies report was generated successfully

set -e

# Read Allure version from config file, fallback to environment variable, then default
if [ -f "config/environments.json" ]; then
    ALLURE_VERSION="${ALLURE_REPORT_VERSION:-$(jq -r '.allure.reportVersion // 3' config/environments.json)}"
else
    # Fallback if config file doesn't exist
    ALLURE_VERSION="${ALLURE_REPORT_VERSION:-3}"
fi

if [ "$ALLURE_VERSION" != "2" ] && [ "$ALLURE_VERSION" != "3" ]; then
    echo "❌ Invalid ALLURE_REPORT_VERSION: $ALLURE_VERSION (must be 2 or 3)"
    exit 1
fi

RESULTS_DIR="${1:-allure-results-combined}"
REPORT_DIR="${2:-allure-report-combined}"

echo "📊 Generating combined Allure report (Version: $ALLURE_VERSION)..."
echo "   Configuration source: $([ -f "config/environments.json" ] && echo "config/environments.json" || echo "default/environment variable")"
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
else
    echo "⚠️  Warning: categories.json not found in results directory"
fi

# STEP 5: Ensure buildOrder continuity
# Verify executor.json exists and check buildOrder
if [ -f "$RESULTS_DIR/executor.json" ]; then
    echo "✅ Executor file found: $RESULTS_DIR/executor.json"
    
    if command -v jq &> /dev/null; then
        CURRENT_BUILD_ORDER=$(jq -r '.buildOrder // "1"' "$RESULTS_DIR/executor.json" 2>/dev/null || echo "1")
        echo "   Current build order: $CURRENT_BUILD_ORDER"
        
        # Check if history exists and get latest buildOrder
        # Support both old format (history-trend.json) and new format (history.jsonl)
        LATEST_HISTORY_BUILD_ORDER=0
        if [ -f "$RESULTS_DIR/history/history.jsonl" ]; then
            # New format: JSON Lines - read each line as JSON and extract buildOrder
            LATEST_HISTORY_BUILD_ORDER=$(while IFS= read -r line; do
                if [ -n "$line" ]; then
                    echo "$line" | jq -r '.buildOrder // 0' 2>/dev/null || echo "0"
                fi
            done < "$RESULTS_DIR/history/history.jsonl" | sort -n | tail -1 || echo "0")
        elif [ -f "$RESULTS_DIR/history/history-trend.json" ]; then
            # Old format: JSON array - convert to JSON Lines format
            LATEST_HISTORY_BUILD_ORDER=$(jq -r '[.[] | .buildOrder] | max // 0' "$RESULTS_DIR/history/history-trend.json" 2>/dev/null || echo "0")
            # Convert old format to new format
            echo "   🔄 Converting old history format to history.jsonl..."
            mkdir -p "$RESULTS_DIR/history"
            jq -c '.[]' "$RESULTS_DIR/history/history-trend.json" > "$RESULTS_DIR/history/history.jsonl" 2>/dev/null || true
            echo "   ✅ Converted to history.jsonl format"
            
            if [ -n "$LATEST_HISTORY_BUILD_ORDER" ] && [ "$LATEST_HISTORY_BUILD_ORDER" != "null" ] && [ "$LATEST_HISTORY_BUILD_ORDER" -gt 0 ]; then
                echo "   Latest history build order: $LATEST_HISTORY_BUILD_ORDER"
                
                # Ensure current buildOrder is higher than latest history buildOrder
                if [ "$CURRENT_BUILD_ORDER" -le "$LATEST_HISTORY_BUILD_ORDER" ]; then
                    echo "   ⚠️  WARNING: Current buildOrder ($CURRENT_BUILD_ORDER) <= latest history buildOrder ($LATEST_HISTORY_BUILD_ORDER)"
                    echo "   🔧 Updating buildOrder to ensure continuity..."
                    NEW_BUILD_ORDER=$((LATEST_HISTORY_BUILD_ORDER + 2))
                    jq --arg build_order "$NEW_BUILD_ORDER" '.buildOrder = $build_order' "$RESULTS_DIR/executor.json" > "${RESULTS_DIR}/executor.json.tmp" 2>/dev/null && \
                    mv "${RESULTS_DIR}/executor.json.tmp" "$RESULTS_DIR/executor.json" 2>/dev/null || true
                    echo "   ✅ Updated buildOrder to $NEW_BUILD_ORDER (ensures continuity)"
                else
                    echo "   ✅ BuildOrder continuity verified ($CURRENT_BUILD_ORDER > $LATEST_HISTORY_BUILD_ORDER)"
                fi
            else
                echo "   ℹ️  No history buildOrder found (first run or empty history)"
            fi
        else
            echo "   ℹ️  No history file found (first run)"
        fi
    fi
else
    echo "⚠️  Warning: executor.json not found in results directory"
fi

# STEP 4: Let Allure3 create history first
# Check if history exists and if it was created by Allure3
# Allure3-created history will have individual {md5-hash}.json files
HISTORY_EXISTS=false
ALLURE3_CREATED_HISTORY=false

if [ -d "$RESULTS_DIR/history" ]; then
    HISTORY_EXISTS=true
    # Check for new format (history.jsonl) first
    if [ -f "$RESULTS_DIR/history/history.jsonl" ]; then
        ALLURE3_CREATED_HISTORY=true
        HISTORY_LINES=$(wc -l < "$RESULTS_DIR/history/history.jsonl" 2>/dev/null | tr -d ' ' || echo "0")
        echo ""
        echo "📊 History found (history.jsonl format):"
        echo "   History file: history.jsonl"
        echo "   History entries: $HISTORY_LINES line(s)"
        echo "   Size: $(du -sh "$RESULTS_DIR/history/history.jsonl" 2>/dev/null | cut -f1 || echo 'unknown')"
        echo "   ✅ History in correct format - will be processed"
    else
        HISTORY_FILE_COUNT=$(find "$RESULTS_DIR/history" -type f -name "*.json" 2>/dev/null | wc -l | tr -d ' ')
        
        # Check if individual test history files exist (created by Allure3)
        INDIVIDUAL_FILES=$(find "$RESULTS_DIR/history" -name "*.json" ! -name "history-trend.json" ! -name "duration-trend.json" ! -name "retry-trend.json" 2>/dev/null | wc -l | tr -d ' ')
        
        if [ "$INDIVIDUAL_FILES" -gt 0 ]; then
            ALLURE3_CREATED_HISTORY=true
            echo ""
            echo "📊 History found (created by Allure3 - legacy format):"
            echo "   Total files: $HISTORY_FILE_COUNT file(s)"
            echo "   Individual test files: $INDIVIDUAL_FILES file(s)"
            echo "   Size: $(du -sh "$RESULTS_DIR/history" 2>/dev/null | cut -f1 || echo 'unknown')"
            echo "   ✅ History was created by Allure3 - will be processed"
        elif [ -f "$RESULTS_DIR/history/history-trend.json" ]; then
            echo ""
            echo "📊 History found (manually created or old format):"
            echo "   Files: $HISTORY_FILE_COUNT file(s)"
            echo "   Size: $(du -sh "$RESULTS_DIR/history" 2>/dev/null | cut -f1 || echo 'unknown')"
            # Convert old format to new format if needed
            if [ -f "$RESULTS_DIR/history/history-trend.json" ] && [ ! -f "$RESULTS_DIR/history/history.jsonl" ]; then
                echo "   🔄 Converting old format to history.jsonl..."
                jq -c '.[]' "$RESULTS_DIR/history/history-trend.json" > "$RESULTS_DIR/history/history.jsonl" 2>/dev/null || true
                echo "   ✅ Converted to history.jsonl format"
            fi
            echo "   ⚠️  History appears to be manually created (no individual test files)"
            echo "   🔄 Letting Allure3 create fresh history first (Step 4)..."
            
            # Backup manually created history
            if [ -d "$RESULTS_DIR/history" ]; then
                BACKUP_DIR="${RESULTS_DIR}/history-backup-$(date +%s)"
                cp -r "$RESULTS_DIR/history" "$BACKUP_DIR" 2>/dev/null || true
                echo "   📦 Backed up existing history to: $BACKUP_DIR"
            fi
            
            # Remove manually created history to let Allure3 bootstrap
            rm -rf "$RESULTS_DIR/history"
            echo "   ✅ Removed manually created history - Allure3 will create fresh history"
            ALLURE3_CREATED_HISTORY=false
        else
            echo ""
            echo "ℹ️  History directory exists but is empty"
            ALLURE3_CREATED_HISTORY=false
        fi
    fi
else
    echo ""
    echo "ℹ️  No history found in results directory (expected for first few runs)"
    echo "   Allure3 will create history naturally from test results"
    ALLURE3_CREATED_HISTORY=false
fi

# Generate report
echo ""
echo "🔄 Generating Allure report (Version: $ALLURE_VERSION)..."
rm -rf "$REPORT_DIR"

if [ "$ALLURE_VERSION" = "3" ]; then
    # Allure3: Use config file if available
    if [ "$ALLURE3_CREATED_HISTORY" = true ]; then
        echo "   Allure3 will process existing Allure3-created history and merge with new results"
    else
        echo "   Allure3 will create fresh history from test results (Step 4: bootstrap)"
    fi
    
    CONFIG_FLAG=""
    CONFIG_FILE=""
    # Try TypeScript config first, then JavaScript
    if [ -f "allure.config.ts" ]; then
        echo "   ✅ Found allure.config.ts - using explicit --config flag"
        CONFIG_FILE="allure.config.ts"
        CONFIG_FLAG="--config allure.config.ts"
        echo "   📄 Config file contents:"
        cat allure.config.ts | sed 's/^/      /'
    elif [ -f "allure.config.js" ]; then
        echo "   ✅ Found allure.config.js - using explicit --config flag"
        CONFIG_FILE="allure.config.js"
        CONFIG_FLAG="--config allure.config.js"
        echo "   📄 Config file contents:"
        cat allure.config.js | sed 's/^/      /'
    else
        echo "   ⚠️  No allure.config.ts or allure.config.js found - Allure3 will use defaults"
    fi
    
    # Run allure generate with explicit config flag and capture all output
    echo ""
    echo "   🔍 Executing: allure generate \"$RESULTS_DIR\" -o \"$REPORT_DIR\" $CONFIG_FLAG"
    allure generate "$RESULTS_DIR" -o "$REPORT_DIR" $CONFIG_FLAG 2>&1 | tee /tmp/allure-generate.log || {
        echo "⚠️  Allure3 generate command had warnings/errors (checking log...)"
        if [ -f /tmp/allure-generate.log ]; then
            echo "   Last 50 lines of Allure output:"
            tail -50 /tmp/allure-generate.log | sed 's/^/   /'
        fi
        # Check if the command actually failed or just had warnings
        if [ ! -d "$REPORT_DIR" ] || [ ! -f "$REPORT_DIR/index.html" ]; then
            echo "❌ Allure3 generate command failed - report not created"
            exit 1
        else
            echo "⚠️  Allure3 generate had warnings but report was created successfully"
        fi
    }
else
    # Allure2: No config file needed
    echo "   Using Allure2 CLI (no config file needed)"
    echo "   Allure2 will automatically process history if present"
    
    # Run allure generate and capture all output
    echo ""
    echo "   🔍 Executing: allure generate \"$RESULTS_DIR\" -o \"$REPORT_DIR\""
    allure generate "$RESULTS_DIR" -o "$REPORT_DIR" 2>&1 | tee /tmp/allure-generate.log || {
        echo "⚠️  Allure2 generate command had warnings/errors (checking log...)"
        if [ -f /tmp/allure-generate.log ]; then
            echo "   Last 50 lines of Allure output:"
            tail -50 /tmp/allure-generate.log | sed 's/^/   /'
        fi
        # Check if the command actually failed or just had warnings
        if [ ! -d "$REPORT_DIR" ] || [ ! -f "$REPORT_DIR/index.html" ]; then
            echo "❌ Allure2 generate command failed - report not created"
            exit 1
        else
            echo "⚠️  Allure2 generate had warnings but report was created successfully"
        fi
    }
fi

# Analyze log for history-related messages
echo ""
echo "   🔍 Analyzing Allure output for history processing..."
if [ -f /tmp/allure-generate.log ]; then
    echo "   History-related messages in log:"
    grep -i -E "history|trend|merge|append|buildOrder" /tmp/allure-generate.log | sed 's/^/      /' || echo "      (No history-related messages found)"
    echo ""
    echo "   Full log saved to: /tmp/allure-generate.log"
fi

# Check if Allure3 created history
# Allure3 may create history.jsonl in RESULTS directory (where historyPath points) or REPORT directory
# historyPath is relative to RESULTS directory, so Allure3 writes to RESULTS_DIR/history/history.jsonl
HISTORY_CREATED=false
HISTORY_SOURCE=""

# Check RESULTS directory first (where historyPath points)
if [ "$ALLURE_VERSION" = "3" ]; then
    # Allure3: Check for history.jsonl
    if [ -f "$RESULTS_DIR/history/history.jsonl" ]; then
        HISTORY_CREATED=true
        HISTORY_SOURCE="results"
        echo ""
        echo "✅ Allure3 history found (history.jsonl format)"
        HISTORY_SIZE=$(du -sh "$RESULTS_DIR/history/history.jsonl" 2>/dev/null | cut -f1 || echo 'unknown')
        HISTORY_LINES=$(wc -l < "$RESULTS_DIR/history/history.jsonl" 2>/dev/null | tr -d ' ' || echo "0")
        echo "   History file: $RESULTS_DIR/history/history.jsonl"
        echo "   History entries: $HISTORY_LINES line(s)"
        echo "   Size: $HISTORY_SIZE"
        echo "   ✅ History found in results directory (where historyPath points)"
    
    # History is already in RESULTS directory, but we need to copy it to REPORT directory
    # for GitHub Pages deployment (deployment publishes REPORT directory, not RESULTS)
    # CRITICAL: Allure3 UI needs history-trend.json format for trends display, not just history.jsonl
    # Note: Allure2 automatically generates trend files, so this conversion is only for Allure3
    if [ "$ALLURE_VERSION" = "3" ]; then
        echo ""
        echo "📊 Converting history.jsonl to history-trend.json for UI trends display..."
        mkdir -p "$REPORT_DIR/history"
    
    # Copy history.jsonl (for Allure3 internal processing)
    cp "$RESULTS_DIR/history/history.jsonl" "$REPORT_DIR/history/history.jsonl" 2>/dev/null || true
    echo "✅ History.jsonl copied to report directory"
    
    # Convert history.jsonl to history-trend.json format (for UI trends display)
    # CRITICAL: Allure3 UI expects history-trend.json with ARRAY of individual test data points
    # Format: [{"buildOrder": X, "reportName": "...", "reportUrl": "", "data": [{"uid": "...", "status": "...", "time": {...}}]}]
    # The data should be an ARRAY of test objects, not aggregated statistics object
    # SVG chart rendering requires individual data points to calculate dimensions correctly
    if command -v jq &> /dev/null; then
        echo "   Converting history.jsonl to history-trend.json with individual test data (array format)..."
        
        # Get current buildOrder from executor.json
        CURRENT_BUILD_ORDER=$(jq -r '.buildOrder // "1"' "$RESULTS_DIR/executor.json" 2>/dev/null || echo "1")
        REPORT_NAME=$(jq -r '.reportName // "Allure Report"' "$RESULTS_DIR/executor.json" 2>/dev/null || echo "Allure Report")
        
        # Read existing history.jsonl and convert each entry to proper format
        # Each line in history.jsonl is a buildOrder entry - keep data as ARRAY of individual test objects
        TEMP_TREND_FILE=$(mktemp)
        echo "[]" > "$TEMP_TREND_FILE"
        
        while IFS= read -r line; do
            if [ -n "$line" ]; then
                # Extract buildOrder and data from history.jsonl entry
                build_order=$(echo "$line" | jq -r '.buildOrder // 0' 2>/dev/null || echo "0")
                report_name=$(echo "$line" | jq -r '.reportName // "Allure Report"' 2>/dev/null || echo "Allure Report")
                report_url=$(echo "$line" | jq -r '.reportUrl // ""' 2>/dev/null || echo "")
                
                # Extract data array directly (keep as array, don't aggregate)
                data_array=$(echo "$line" | jq '.data // []' 2>/dev/null || echo "[]")
                
                # Ensure data is always an array (wrap single objects in array)
                if [ "$data_array" != "[]" ] && [ "$data_array" != "null" ]; then
                    data_type=$(echo "$data_array" | jq 'type' 2>/dev/null || echo "null")
                    if [ "$data_type" = '"object"' ]; then
                        # Single test object - wrap in array
                        data_array=$(echo "$data_array" | jq '[.]' 2>/dev/null || echo "[]")
                    fi
                fi
                
                if [ "$data_array" != "[]" ] && [ "$data_array" != "null" ]; then
                    # Create history-trend.json entry with ARRAY data (individual test objects)
                    jq --argjson build_order "$build_order" \
                       --arg report_name "$report_name" \
                       --arg report_url "$report_url" \
                       --argjson data_array "$data_array" \
                       '. += [{
                         "buildOrder": $build_order,
                         "reportName": $report_name,
                         "reportUrl": $report_url,
                         "data": $data_array
                       }]' "$TEMP_TREND_FILE" > "${TEMP_TREND_FILE}.tmp" 2>/dev/null && \
                    mv "${TEMP_TREND_FILE}.tmp" "$TEMP_TREND_FILE" 2>/dev/null || true
                fi
            fi
        done < "$RESULTS_DIR/history/history.jsonl"
        
        # Add current run data if we have test results (extract individual test data from result files)
        if [ -d "$RESULTS_DIR" ] && [ "$(find "$RESULTS_DIR" -name "*-result.json" -type f | wc -l | tr -d ' ')" -gt 0 ]; then
            # Extract individual test data from current run's result files
            current_run_data=$(find "$RESULTS_DIR" -name "*-result.json" -type f | \
                head -1000 | \
                jq -s '[.[] | {
                    uid: (.historyId // .uuid),
                    status: .status,
                    time: {
                        start: .start,
                        stop: .stop,
                        duration: ((.stop // 0) - (.start // 0))
                    }
                }]' 2>/dev/null || echo "[]")
            
            if [ "$current_run_data" != "[]" ] && [ "$current_run_data" != "null" ]; then
                jq --argjson build_order "$CURRENT_BUILD_ORDER" \
                   --arg report_name "$REPORT_NAME" \
                   --argjson data_array "$current_run_data" \
                   '. += [{
                     "buildOrder": $build_order,
                     "reportName": $report_name,
                     "reportUrl": "",
                     "data": $data_array
                   }]' "$TEMP_TREND_FILE" > "${TEMP_TREND_FILE}.tmp" 2>/dev/null && \
                mv "${TEMP_TREND_FILE}.tmp" "$TEMP_TREND_FILE" 2>/dev/null || true
            fi
        fi
        
        # Copy final result to report directory
        cp "$TEMP_TREND_FILE" "$REPORT_DIR/history/history-trend.json" 2>/dev/null || true
        rm -f "$TEMP_TREND_FILE" 2>/dev/null || true
        
        echo "✅ History-trend.json created with individual test data (array format) for UI trends display"
        echo "   Format: data array with [{uid, status, time}] - required for SVG chart rendering"
        
        # Also create duration-trend.json if needed (for duration trends)
        # Format: [{"buildOrder": X, "data": [{"uid": "...", "time": {...}}]}]
        # For now, we'll create a basic version from history.jsonl
        echo "   Creating duration-trend.json for duration trends..."
        TEMP_DURATION_FILE=$(mktemp)
        echo "[]" > "$TEMP_DURATION_FILE"
        
        while IFS= read -r line; do
            if [ -n "$line" ]; then
                build_order=$(echo "$line" | jq -r '.buildOrder // 0' 2>/dev/null || echo "0")
                # Extract duration data from data array (keep individual test durations)
                duration_data=$(echo "$line" | jq '[.data[]? | {uid: .uid, time: .time}]' 2>/dev/null || echo "[]")
                
                if [ "$duration_data" != "[]" ] && [ "$duration_data" != "null" ]; then
                    jq --argjson build_order "$build_order" \
                       --argjson duration_data "$duration_data" \
                       '. += [{
                         "buildOrder": $build_order,
                         "data": $duration_data
                       }]' "$TEMP_DURATION_FILE" > "${TEMP_DURATION_FILE}.tmp" 2>/dev/null && \
                    mv "${TEMP_DURATION_FILE}.tmp" "$TEMP_DURATION_FILE" 2>/dev/null || true
                fi
            fi
        done < "$RESULTS_DIR/history/history.jsonl"
        
        # Add current run duration data if we have test results
        if [ "$TOTAL_TESTS" -gt 0 ] && [ -d "$RESULTS_DIR" ]; then
            current_duration_data=$(find "$RESULTS_DIR" -name "*-result.json" -type f | \
                head -100 | \
                jq -s '[.[] | {uid: .historyId // .uuid, time: {start: .start, stop: .stop, duration: ((.stop // 0) - (.start // 0))}}]' 2>/dev/null || echo "[]")
            
            if [ "$current_duration_data" != "[]" ] && [ "$current_duration_data" != "null" ]; then
                jq --argjson build_order "$CURRENT_BUILD_ORDER" \
                   --argjson duration_data "$current_duration_data" \
                   '. += [{
                     "buildOrder": $build_order,
                     "data": $duration_data
                   }]' "$TEMP_DURATION_FILE" > "${TEMP_DURATION_FILE}.tmp" 2>/dev/null && \
                mv "${TEMP_DURATION_FILE}.tmp" "$TEMP_DURATION_FILE" 2>/dev/null || true
            fi
        fi
        
        cp "$TEMP_DURATION_FILE" "$REPORT_DIR/history/duration-trend.json" 2>/dev/null || true
        rm -f "$TEMP_DURATION_FILE" 2>/dev/null || true
        echo "✅ Duration-trend.json created for duration trends display"
    else
        echo "⚠️  jq not available - skipping history-trend.json conversion"
    fi
    
        echo "   History will be included in GitHub Pages deployment"
        echo ""
        echo "📊 History ready for next run..."
        echo "✅ History preserved: history.jsonl ready for next report generation"
        echo "   History will be uploaded as artifact and deployed to GitHub Pages"
    fi
else
        # Allure2: History is automatically processed, just copy to report directory
        echo ""
        echo "📊 Copying Allure2 history to report directory..."
        mkdir -p "$REPORT_DIR/history"
        if [ -d "$RESULTS_DIR/history" ]; then
            cp -r "$RESULTS_DIR/history"/* "$REPORT_DIR/history/" 2>/dev/null || true
            HISTORY_FILES=$(find "$REPORT_DIR/history" -name "*.json" -type f | wc -l | tr -d ' ')
            echo "✅ Allure2 history copied to report directory ($HISTORY_FILES file(s))"
            echo "   History will be uploaded as artifact and deployed to GitHub Pages"
        fi
        
        # Step 5.5: Fix zero durations in history files (prevents NaN errors in trend charts)
        echo ""
        echo "🔧 Step 5.5: Fixing zero durations in history files..."
        chmod +x scripts/ci/fix-allure-history-zero-durations.sh
        ./scripts/ci/fix-allure-history-zero-durations.sh "$RESULTS_DIR" "$REPORT_DIR"
        echo "✅ Zero duration fix completed"
    fi
else
    # Allure2: Check for individual JSON files
    if [ -d "$RESULTS_DIR/history" ] && [ "$(find "$RESULTS_DIR/history" -name "*.json" -type f | wc -l | tr -d ' ')" -gt 0 ]; then
        HISTORY_CREATED=true
        HISTORY_SOURCE="results"
        HISTORY_FILES=$(find "$RESULTS_DIR/history" -name "*.json" -type f | wc -l | tr -d ' ')
        echo ""
        echo "✅ Allure2 history found ($HISTORY_FILES file(s))"
        echo "   History directory: $RESULTS_DIR/history"
        echo "   History files: $HISTORY_FILES JSON file(s)"
        echo "   Size: $(du -sh "$RESULTS_DIR/history" 2>/dev/null | cut -f1 || echo 'unknown')"
        echo "   ✅ Allure2 history will be automatically processed by allure generate"
    fi
fi

# Check REPORT directory (fallback - some Allure3 versions might write here)
if [ "$ALLURE_VERSION" = "3" ] && [ -f "$REPORT_DIR/history/history.jsonl" ]; then
    HISTORY_CREATED=true
    HISTORY_SOURCE="report"
    echo ""
    echo "✅ Allure3 created/updated history in report directory (history.jsonl format)"
    HISTORY_SIZE=$(du -sh "$REPORT_DIR/history/history.jsonl" 2>/dev/null | cut -f1 || echo 'unknown')
    HISTORY_LINES=$(wc -l < "$REPORT_DIR/history/history.jsonl" 2>/dev/null | tr -d ' ' || echo "0")
    echo "   History file: history.jsonl"
    echo "   History entries: $HISTORY_LINES line(s)"
    echo "   Size: $HISTORY_SIZE"
    
    # Preserve history for next run (copy from report to results)
    echo ""
    echo "📊 Preserving history for next run..."
    mkdir -p "$RESULTS_DIR/history"
    cp "$REPORT_DIR/history/history.jsonl" "$RESULTS_DIR/history/history.jsonl" 2>/dev/null || true
    echo "✅ History preserved: history.jsonl ready for next report generation"
    echo "   History will be uploaded as artifact and deployed to GitHub Pages"
elif [ "$ALLURE_VERSION" = "3" ] && [ -d "$REPORT_DIR/history" ] && [ "$(find "$REPORT_DIR/history" -type f -name "*.json" ! -name "history-trend.json" ! -name "duration-trend.json" ! -name "retry-trend.json" 2>/dev/null | wc -l | tr -d ' ')" -gt 0 ]; then
    HISTORY_CREATED=true
    echo ""
    echo "✅ Allure3 created/updated history in report (legacy JSON format)"
    HISTORY_FILE_COUNT=$(find "$REPORT_DIR/history" -type f -name "*.json" 2>/dev/null | wc -l | tr -d ' ')
    INDIVIDUAL_COUNT=$(find "$REPORT_DIR/history" -name "*.json" ! -name "history-trend.json" ! -name "duration-trend.json" ! -name "retry-trend.json" 2>/dev/null | wc -l | tr -d ' ')
    echo "   Total files: $HISTORY_FILE_COUNT file(s)"
    echo "   Individual test files: $INDIVIDUAL_COUNT file(s)"
    echo "   Size: $(du -sh "$REPORT_DIR/history" 2>/dev/null | cut -f1 || echo 'unknown')"
    
    # Convert legacy format to history.jsonl if history-trend.json exists
    if [ -f "$REPORT_DIR/history/history-trend.json" ]; then
        echo "   🔄 Converting legacy format to history.jsonl..."
        mkdir -p "$RESULTS_DIR/history"
        jq -c '.[]' "$REPORT_DIR/history/history-trend.json" > "$RESULTS_DIR/history/history.jsonl" 2>/dev/null || true
        echo "   ✅ Converted to history.jsonl format"
    else
        # Preserve history for next run (legacy format)
        echo ""
        echo "📊 Preserving history for next run..."
        mkdir -p "$RESULTS_DIR/history"
        cp -r "$REPORT_DIR/history"/* "$RESULTS_DIR/history/" 2>/dev/null || true
        PRESERVED_COUNT=$(find "$RESULTS_DIR/history" -type f -name "*.json" 2>/dev/null | wc -l | tr -d ' ')
        echo "✅ History preserved: $PRESERVED_COUNT file(s) ready for next report generation"
    fi
    echo "   History will be uploaded as artifact and deployed to GitHub Pages"
    
    # Clean up backup directory if it exists
    if [ -d "${RESULTS_DIR}/history-backup-"* ] 2>/dev/null; then
        rm -rf "${RESULTS_DIR}/history-backup-"* 2>/dev/null || true
        echo "   🗑️  Cleaned up backup history directory"
    fi
fi

if [ "$HISTORY_CREATED" = false ]; then
    echo ""
    echo "ℹ️  Allure3 did not create history (this is normal for first few runs)"
    echo "   History will be created naturally after 2-3 more pipeline runs"
    echo "   Allure3 needs multiple runs with consistent test identifiers to build history"
    
    # Restore backup if Allure3 didn't create history
    if [ -d "${RESULTS_DIR}/history-backup-"* ] 2>/dev/null; then
        BACKUP_DIR=$(ls -td "${RESULTS_DIR}/history-backup-"* 2>/dev/null | head -1)
        if [ -n "$BACKUP_DIR" ] && [ -d "$BACKUP_DIR" ]; then
            echo "   🔄 Restoring backup history..."
            cp -r "$BACKUP_DIR"/* "$RESULTS_DIR/history/" 2>/dev/null || true
            rm -rf "$BACKUP_DIR" 2>/dev/null || true
            echo "   ✅ Restored backup history"
        fi
    fi
fi

# Verify report was generated
if [ ! -d "$REPORT_DIR" ]; then
    echo ""
    echo "❌ Error: Report directory was not created: $REPORT_DIR"
    echo "   This indicates the 'allure generate' command failed"
    if [ -f /tmp/allure-generate.log ]; then
        echo ""
        echo "   Allure generate log (last 30 lines):"
        tail -30 /tmp/allure-generate.log | sed 's/^/   /'
    fi
    exit 1
fi

if [ ! -f "$REPORT_DIR/index.html" ]; then
    echo ""
    echo "❌ Error: Report generation incomplete - index.html not found in $REPORT_DIR"
    echo "   Directory exists but report is incomplete"
    if [ -f /tmp/allure-generate.log ]; then
        echo ""
        echo "   Allure generate log (last 30 lines):"
        tail -30 /tmp/allure-generate.log | sed 's/^/   /'
    fi
    exit 1
fi

echo ""
echo "✅ Allure report generated successfully"
echo "   Report location: $REPORT_DIR"
echo "   Report size: $(du -sh "$REPORT_DIR" 2>/dev/null | cut -f1 || echo 'unknown')"

# Fix summary timestamps to use only current run (not history)
echo ""
echo "🔧 Fixing summary timestamps to reflect current run only..."
chmod +x scripts/ci/fix-allure-summary-timestamps.sh
if ./scripts/ci/fix-allure-summary-timestamps.sh "$RESULTS_DIR" "$REPORT_DIR"; then
    echo "   ✅ Summary timestamps fixed"
else
    echo "   ⚠️  Summary timestamp fix had issues (report will use Allure's calculated values)"
fi

# Final verification: Ensure history-trend.json has correct format (array data, not object)
if [ -f "$REPORT_DIR/history/history-trend.json" ] && command -v jq &> /dev/null; then
    echo ""
    echo "🔍 Verifying history-trend.json format..."
    
    # Check if any entries have object data (wrong format - should be array)
    # This includes both aggregated statistics objects AND single test objects
    HAS_OBJECT_DATA=$(jq '[.[] | select(.data | type == "object")] | length' "$REPORT_DIR/history/history-trend.json" 2>/dev/null || echo "0")
    
    if [ "$HAS_OBJECT_DATA" != "0" ] && [ "$HAS_OBJECT_DATA" != "null" ]; then
        echo "   ⚠️  Found $HAS_OBJECT_DATA entry/entries with object data (wrong format - should be array)"
        echo "   🔧 Rebuilding from history.jsonl to ensure correct array format..."
        
        # Always rebuild from history.jsonl when we detect object data (source of truth)
        # This ensures all entries have array format, not single objects or aggregated stats
        TEMP_FIX_FILE=$(mktemp)
        echo "[]" > "$TEMP_FIX_FILE"
        
        if [ -f "$RESULTS_DIR/history/history.jsonl" ]; then
            while IFS= read -r line; do
                if [ -n "$line" ]; then
                    build_order=$(echo "$line" | jq -r '.buildOrder // 0' 2>/dev/null || echo "0")
                    report_name=$(echo "$line" | jq -r '.reportName // "Allure Report"' 2>/dev/null || echo "Allure Report")
                    report_url=$(echo "$line" | jq -r '.reportUrl // ""' 2>/dev/null || echo "")
                    data_array=$(echo "$line" | jq '.data // []' 2>/dev/null || echo "[]")
                    
                    # Ensure data is always an array (wrap single objects in array)
                    if [ "$data_array" != "[]" ] && [ "$data_array" != "null" ]; then
                        # Check if data is an object (single test object) - wrap in array
                        data_type=$(echo "$data_array" | jq 'type' 2>/dev/null || echo "null")
                        if [ "$data_type" = '"object"' ]; then
                            # Single test object - wrap in array
                            data_array=$(echo "$data_array" | jq '[.]' 2>/dev/null || echo "[]")
                        fi
                        
                        jq --argjson build_order "$build_order" \
                           --arg report_name "$report_name" \
                           --arg report_url "$report_url" \
                           --argjson data_array "$data_array" \
                           '. += [{
                             buildOrder: $build_order,
                             reportName: $report_name,
                             reportUrl: $report_url,
                             data: $data_array
                           }]' "$TEMP_FIX_FILE" > "${TEMP_FIX_FILE}.tmp" 2>/dev/null && \
                        mv "${TEMP_FIX_FILE}.tmp" "$TEMP_FIX_FILE" 2>/dev/null || true
                    fi
                fi
            done < "$RESULTS_DIR/history/history.jsonl"
            
            # Add current run data if not already in history.jsonl (extract from result files)
            if [ -d "$RESULTS_DIR" ] && [ "$(find "$RESULTS_DIR" -name "*-result.json" -type f | wc -l | tr -d ' ')" -gt 0 ]; then
                CURRENT_BUILD_ORDER=$(jq -r '.buildOrder // "1"' "$RESULTS_DIR/executor.json" 2>/dev/null || echo "1")
                REPORT_NAME=$(jq -r '.reportName // "Allure Report"' "$RESULTS_DIR/executor.json" 2>/dev/null || echo "Allure Report")
                
                # Check if current buildOrder already exists in rebuilt file
                BUILD_EXISTS=$(jq --argjson build_order "$CURRENT_BUILD_ORDER" '[.[] | select(.buildOrder == $build_order)] | length' "$TEMP_FIX_FILE" 2>/dev/null || echo "0")
                
                if [ "$BUILD_EXISTS" = "0" ] || [ "$BUILD_EXISTS" = "null" ]; then
                    # Extract individual test data from current run's result files
                    current_run_data=$(find "$RESULTS_DIR" -name "*-result.json" -type f | \
                        head -1000 | \
                        jq -s '[.[] | {
                            uid: (.historyId // .uuid),
                            status: .status,
                            time: {
                                start: .start,
                                stop: .stop,
                                duration: ((.stop // 0) - (.start // 0))
                            }
                        }]' 2>/dev/null || echo "[]")
                    
                    if [ "$current_run_data" != "[]" ] && [ "$current_run_data" != "null" ]; then
                        jq --argjson build_order "$CURRENT_BUILD_ORDER" \
                           --arg report_name "$REPORT_NAME" \
                           --argjson data_array "$current_run_data" \
                           '. += [{
                             "buildOrder": $build_order,
                             "reportName": $report_name,
                             "reportUrl": "",
                             "data": $data_array
                           }]' "$TEMP_FIX_FILE" > "${TEMP_FIX_FILE}.tmp" 2>/dev/null && \
                        mv "${TEMP_FIX_FILE}.tmp" "$TEMP_FIX_FILE" 2>/dev/null || true
                    fi
                fi
            fi
        fi
        
        if [ -f "$TEMP_FIX_FILE" ] && [ -s "$TEMP_FIX_FILE" ]; then
            # Verify the rebuilt file is not empty (just "[]")
            FILE_CONTENT=$(cat "$TEMP_FIX_FILE" 2>/dev/null | tr -d '[:space:]' || echo "")
            ARRAY_LENGTH=$(jq 'length' "$TEMP_FIX_FILE" 2>/dev/null || echo "0")
            
            if [ "$FILE_CONTENT" != "[]" ] && [ "$ARRAY_LENGTH" != "0" ] && [ "$ARRAY_LENGTH" != "null" ]; then
                mv "$TEMP_FIX_FILE" "$REPORT_DIR/history/history-trend.json" 2>/dev/null || true
                echo "   ✅ Rebuilt history-trend.json from history.jsonl - all entries now have array data"
            else
                echo "   ⚠️  Rebuild resulted in empty array - preserving original file (if exists)"
                rm -f "$TEMP_FIX_FILE" 2>/dev/null || true
                # Don't overwrite existing file if rebuild is empty
                if [ ! -f "$REPORT_DIR/history/history-trend.json" ] || [ ! -s "$REPORT_DIR/history/history-trend.json" ]; then
                    echo "   ⚠️  No valid history-trend.json exists - history may need to be regenerated"
                fi
            fi
        else
            echo "   ⚠️  Failed to rebuild format, but continuing..."
            rm -f "$TEMP_FIX_FILE" 2>/dev/null || true
            # Don't overwrite existing file if rebuild failed
            if [ ! -f "$REPORT_DIR/history/history-trend.json" ] || [ ! -s "$REPORT_DIR/history/history-trend.json" ]; then
                echo "   ⚠️  No valid history-trend.json exists - history may need to be regenerated"
            fi
        fi
    else
        echo "   ✅ history-trend.json format is correct (all entries have array data)"
    fi
    
    # CRITICAL: Allure3 UI may also need widgets/history-trend.json
    # Copy history-trend.json to widgets directory if it doesn't exist or is outdated
    if [ -d "$REPORT_DIR/widgets" ]; then
        echo ""
        echo "📊 Ensuring widgets/history-trend.json exists for UI..."
        if [ ! -f "$REPORT_DIR/widgets/history-trend.json" ] || \
           [ "$REPORT_DIR/history/history-trend.json" -nt "$REPORT_DIR/widgets/history-trend.json" ]; then
            cp "$REPORT_DIR/history/history-trend.json" "$REPORT_DIR/widgets/history-trend.json" 2>/dev/null || true
            echo "   ✅ Copied history-trend.json to widgets directory"
        else
            echo "   ✅ widgets/history-trend.json already exists and is up to date"
        fi
    fi
fi

# Step 5.5: Fix zero durations in history files (prevents NaN errors in trend charts)
# This must run after history is copied/created but before report is finalized
if [ -f "$REPORT_DIR/history/history-trend.json" ] || [ -f "$REPORT_DIR/history/duration-trend.json" ]; then
    echo ""
    echo "🔧 Step 5.5: Fixing zero durations in history files..."
    chmod +x scripts/ci/fix-allure-history-zero-durations.sh
    if ./scripts/ci/fix-allure-history-zero-durations.sh "$RESULTS_DIR" "$REPORT_DIR"; then
        echo "✅ Zero duration fix completed"
    else
        echo "   ⚠️  Zero duration fix had issues (continuing anyway)"
    fi
fi
