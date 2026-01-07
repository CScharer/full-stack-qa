#!/bin/bash
# Generate Combined Allure Report - APPROACH 4 (Individual Test History Files)
# This version creates individual {md5-hash}.json files for each test
# Usage: ./scripts/ci/generate-combined-allure-report.sh [results-dir] [report-dir]
#
# Arguments:
#   results-dir  - Directory containing Allure results (default: allure-results-combined)
#   report-dir   - Directory where Allure report will be generated (default: allure-report-combined)
#
# This script:
# 1. Verifies results directory exists and has files
# 2. Downloads history from previous runs (if exists)
# 3. Creates individual test history files ({md5(historyId)}.json) from history-trend.json
# 4. Generates Allure HTML report (Allure3 processes individual history files)
# 5. Preserves history for next run (copies from report back to results)
# 6. Verifies report was generated successfully
#
# APPROACH 4: Create individual test history files
# Allure3 might require individual {md5-hash}.json files for each test

set -e

RESULTS_DIR="${1:-allure-results-combined}"
REPORT_DIR="${2:-allure-report-combined}"

echo "📊 Generating combined Allure report (Approach 4 - individual test history files)..."
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

# Verify executor.json exists
if [ -f "$RESULTS_DIR/executor.json" ]; then
    echo "✅ Executor file found: $RESULTS_DIR/executor.json"
    # Extract buildOrder for logging
    if command -v jq &> /dev/null; then
        BUILD_ORDER=$(jq -r '.buildOrder // "unknown"' "$RESULTS_DIR/executor.json" 2>/dev/null || echo "unknown")
        echo "   Build order: $BUILD_ORDER"
    fi
else
    echo "⚠️  Warning: executor.json not found in results directory"
fi

# APPROACH 4: Create individual test history files from history-trend.json
# Allure3 might require individual {md5(historyId)}.json files for each test
if [ -d "$RESULTS_DIR/history" ] && [ -f "$RESULTS_DIR/history/history-trend.json" ]; then
    echo ""
    echo "📊 Processing history for individual test history files (Approach 4)..."
    
    # Check if history-trend.json has data
    if command -v jq &> /dev/null; then
        HISTORY_ENTRIES=$(jq 'length' "$RESULTS_DIR/history/history-trend.json" 2>/dev/null || echo "0")
        
        if [ "$HISTORY_ENTRIES" -gt 0 ]; then
            echo "   Found $HISTORY_ENTRIES build order entries in history-trend.json"
            echo "   Creating individual test history files..."
            
            # Create individual test history files
            # For each entry in history-trend.json, extract data and group by uid
            # Then create {md5(uid)}.json files
            INDIVIDUAL_COUNT=0
            
            # Process each build order entry and create individual test history files
            # Use jq to process all entries and create individual files
            jq -r '.[] | 
                .buildOrder as $build_order |
                .data[]? | 
                "\(.uid)|\($build_order)|\(.status)|\(.time.start)|\(.time.stop)|\(.time.duration)"' \
                "$RESULTS_DIR/history/history-trend.json" 2>/dev/null | \
            while IFS='|' read -r uid build_order status start stop duration; do
                if [ -n "$uid" ] && [ "$uid" != "null" ] && [ -n "$build_order" ]; then
                    # Generate MD5 hash of uid for filename
                    hash=$(echo -n "$uid" | md5sum | cut -d' ' -f1)
                    history_file="$RESULTS_DIR/history/${hash}.json"
                    
                    # Create history entry
                    history_entry=$(jq -n \
                        --argjson build_order "$build_order" \
                        --arg status "$status" \
                        --argjson start "$start" \
                        --argjson stop "$stop" \
                        --argjson duration "$duration" \
                        '{buildOrder: $build_order, status: $status, time: {start: $start, stop: $stop, duration: $duration}}')
                    
                    # Create or update individual test history file
                    if [ -f "$history_file" ]; then
                        # Append to existing history
                        jq --argjson entry "$history_entry" '.history += [$entry]' "$history_file" > "${history_file}.tmp" 2>/dev/null && \
                        mv "${history_file}.tmp" "$history_file" 2>/dev/null || true
                    else
                        # Create new history file
                        jq -n \
                            --arg uid "$uid" \
                            --argjson entry "$history_entry" \
                            '{uid: $uid, history: [$entry]}' > "$history_file" 2>/dev/null || true
                    fi
                    
                    INDIVIDUAL_COUNT=$((INDIVIDUAL_COUNT + 1))
                fi
            done
            
            # Count individual history files created
            INDIVIDUAL_FILES=$(find "$RESULTS_DIR/history" -name "*.json" ! -name "history-trend.json" ! -name "duration-trend.json" ! -name "retry-trend.json" ! -name "*.tmp" 2>/dev/null | wc -l | tr -d ' ')
            echo "   ✅ Created/updated $INDIVIDUAL_FILES individual test history file(s)"
            echo "   Individual history files ready for Allure3 processing"
        else
            echo "   ℹ️  history-trend.json is empty or has no entries"
        fi
    else
        echo "   ⚠️  jq not available - cannot process history for individual files"
    fi
else
    echo ""
    echo "ℹ️  No history found in results directory (expected for first few runs)"
    echo "   Individual test history files will be created by Allure3 after multiple runs"
fi

# Generate report - Allure3 will process individual history files
# CRITICAL: Individual history files must be in RESULTS_DIR/history/ BEFORE this command
# Allure3 will:
# 1. Read individual {md5-hash}.json files from RESULTS_DIR/history/
# 2. Merge them with new test results (matching by historyId)
# 3. Create updated history in REPORT_DIR/history/
echo ""
echo "🔄 Generating Allure report..."
echo "   Allure3 will process individual test history files and create updated history"
rm -rf "$REPORT_DIR"
allure generate "$RESULTS_DIR" -o "$REPORT_DIR"

# Check if Allure3 created history
if [ -d "$REPORT_DIR/history" ] && [ "$(find "$REPORT_DIR/history" -type f -name "*.json" 2>/dev/null | wc -l | tr -d ' ')" -gt 0 ]; then
    echo ""
    echo "✅ Allure3 created history in report"
    HISTORY_FILE_COUNT=$(find "$REPORT_DIR/history" -type f -name "*.json" 2>/dev/null | wc -l | tr -d ' ')
    INDIVIDUAL_COUNT=$(find "$REPORT_DIR/history" -name "*.json" ! -name "history-trend.json" ! -name "duration-trend.json" ! -name "retry-trend.json" 2>/dev/null | wc -l | tr -d ' ')
    echo "   Total files: $HISTORY_FILE_COUNT file(s)"
    echo "   Individual test files: $INDIVIDUAL_COUNT file(s)"
    echo "   Size: $(du -sh "$REPORT_DIR/history" 2>/dev/null | cut -f1 || echo 'unknown')"
    
    # Preserve history for next run
    # Copy history from report back to results directory so it's available for next pipeline run
    echo ""
    echo "📊 Preserving history for next run..."
    mkdir -p "$RESULTS_DIR/history"
    cp -r "$REPORT_DIR/history"/* "$RESULTS_DIR/history/" 2>/dev/null || true
    PRESERVED_COUNT=$(find "$RESULTS_DIR/history" -type f -name "*.json" 2>/dev/null | wc -l | tr -d ' ')
    echo "✅ History preserved: $PRESERVED_COUNT file(s) ready for next report generation"
    echo "   History will be uploaded as artifact and deployed to GitHub Pages"
else
    echo ""
    echo "ℹ️  Allure3 did not create history (this is normal for first few runs)"
    echo "   History will be created naturally after 2-3 more pipeline runs"
    echo "   Allure3 needs multiple runs with consistent test identifiers to build history"
fi

# Verify report was generated
if [ ! -d "$REPORT_DIR" ] || [ ! -f "$REPORT_DIR/index.html" ]; then
    echo ""
    echo "❌ Error: Report generation failed - index.html not found"
    exit 1
fi

echo ""
echo "✅ Allure report generated successfully"
echo "   Report location: $REPORT_DIR"
echo "   Report size: $(du -sh "$REPORT_DIR" 2>/dev/null | cut -f1 || echo 'unknown')"
