#!/bin/bash
# Fix Zero Durations in Allure History Files
# Usage: ./scripts/ci/fix-allure-history-zero-durations.sh [results-dir] [report-dir]
#
# Arguments:
#   results-dir  - Directory containing Allure results (default: allure-results-combined)
#   report-dir   - Directory where Allure report is generated (default: allure-report-combined)
#
# This script fixes zero durations in history-trend.json that cause NaN errors in SVG chart rendering.
# When start == stop, duration = 0, which causes Allure2's chart calculations to produce NaN values.
#
# The fix:
# - Finds entries with duration = 0 or start == stop
# - Sets minimum duration of 1ms for those entries
# - Ensures chart coordinates can be calculated correctly
#
# Examples:
#   ./scripts/ci/fix-allure-history-zero-durations.sh allure-results-combined allure-report-combined

set -e

RESULTS_DIR="${1:-allure-results-combined}"
REPORT_DIR="${2:-allure-report-combined}"

echo "🔧 Fixing zero durations in Allure history files..."
echo "   Results directory: $RESULTS_DIR"
echo "   Report directory: $REPORT_DIR"
echo ""

if ! command -v jq &> /dev/null; then
    echo "❌ Error: 'jq' is not installed. Please install it (e.g., brew install jq)."
    exit 1
fi

FIXED_COUNT=0
FILES_FIXED=0

# Fix history-trend.json in report directory (used by UI)
# CRITICAL: Also fix widgets/history-trend.json which is what the UI actually uses for rendering
if [ -f "$REPORT_DIR/history/history-trend.json" ]; then
    echo "📊 Fixing history-trend.json in report directory..."
    
    # Check if file is empty or invalid JSON
    FILE_SIZE=$(wc -c < "$REPORT_DIR/history/history-trend.json" 2>/dev/null | tr -d ' ' || echo "0")
    if [ "$FILE_SIZE" -le 2 ]; then
        echo "   ⚠️  history-trend.json is empty or too small (size: $FILE_SIZE bytes) - skipping fix"
        echo "   ℹ️  This may indicate history was not properly generated. Check history.jsonl or previous steps."
    else
        # Verify it's valid JSON and has data
        IS_VALID=$(jq -e 'if type == "array" then length > 0 else false end' "$REPORT_DIR/history/history-trend.json" 2>/dev/null || echo "false")
        if [ "$IS_VALID" != "true" ]; then
            echo "   ⚠️  history-trend.json is not a valid non-empty array - skipping fix"
            echo "   ℹ️  File may need to be rebuilt from history.jsonl"
        else
            TEMP_FILE=$(mktemp)
            
            # Fix zero durations: if duration is 0 or start == stop, set stop = start + 1ms
            # Only process entries where .data is an array (not object or null)
            jq '[.[] | {
                buildOrder: .buildOrder,
                reportName: .reportName,
                reportUrl: .reportUrl,
                data: (if (.data | type) == "array" then
                    ([.data[] | 
                        if .time and (.time.duration == 0 or (.time.start == .time.stop)) then
                            .time.stop = ((.time.start // 0) + 1) |
                            .time.duration = 1
                        else . end
                    ])
                else
                    .data
                end)
            }]' "$REPORT_DIR/history/history-trend.json" > "$TEMP_FILE" 2>/dev/null
            
            # Verify the temp file is valid and non-empty
            if [ $? -eq 0 ] && [ -s "$TEMP_FILE" ]; then
                TEMP_VALID=$(jq -e 'if type == "array" then length > 0 else false end' "$TEMP_FILE" 2>/dev/null || echo "false")
                if [ "$TEMP_VALID" = "true" ]; then
                    # Count how many entries were fixed
                    ORIGINAL_ZERO_COUNT=$(jq '[.[] | .data[]? | select(.time and (.time.duration == 0 or .time.start == .time.stop))] | length' "$REPORT_DIR/history/history-trend.json" 2>/dev/null || echo "0")
                    NEW_ZERO_COUNT=$(jq '[.[] | .data[]? | select(.time and (.time.duration == 0 or .time.start == .time.stop))] | length' "$TEMP_FILE" 2>/dev/null || echo "0")
                    FIXED_IN_FILE=$((ORIGINAL_ZERO_COUNT - NEW_ZERO_COUNT))
                    
                    if [ "$FIXED_IN_FILE" -gt 0 ]; then
                        mv "$TEMP_FILE" "$REPORT_DIR/history/history-trend.json"
                        FIXED_COUNT=$((FIXED_COUNT + FIXED_IN_FILE))
                        FILES_FIXED=$((FILES_FIXED + 1))
                        echo "   ✅ Fixed $FIXED_IN_FILE zero-duration entry/entries in history-trend.json"
                    else
                        # Even if no fixes, preserve the file if it's valid
                        mv "$TEMP_FILE" "$REPORT_DIR/history/history-trend.json"
                        echo "   ℹ️  No zero-duration entries found in history-trend.json (file preserved)"
                    fi
                else
                    rm -f "$TEMP_FILE"
                    echo "   ⚠️  Fix resulted in invalid/empty array - preserving original file"
                fi
            else
                rm -f "$TEMP_FILE"
                echo "   ⚠️  Failed to process history-trend.json - preserving original file"
            fi
        fi
    fi
else
    echo "   ⚠️  history-trend.json not found in report directory (may not exist yet)"
fi

# Fix duration-trend.json in report directory (used for duration trends)
if [ -f "$REPORT_DIR/history/duration-trend.json" ]; then
    echo "📊 Fixing duration-trend.json in report directory..."
    
    # Check if file is empty or invalid JSON
    FILE_SIZE=$(wc -c < "$REPORT_DIR/history/duration-trend.json" 2>/dev/null | tr -d ' ' || echo "0")
    if [ "$FILE_SIZE" -le 2 ]; then
        echo "   ⚠️  duration-trend.json is empty or too small (size: $FILE_SIZE bytes) - skipping fix"
    else
        # Verify it's valid JSON and has data
        IS_VALID=$(jq -e 'if type == "array" then length > 0 else false end' "$REPORT_DIR/history/duration-trend.json" 2>/dev/null || echo "false")
        if [ "$IS_VALID" != "true" ]; then
            echo "   ⚠️  duration-trend.json is not a valid non-empty array - skipping fix"
        else
            TEMP_FILE=$(mktemp)
            
            # Fix zero durations in duration-trend.json
            # Only process entries where .data is an array (not object or null)
            jq '[.[] | {
                buildOrder: .buildOrder,
                data: (if (.data | type) == "array" then
                    ([.data[] | 
                        if .time and (.time.duration == 0 or (.time.start == .time.stop)) then
                            .time.stop = ((.time.start // 0) + 1) |
                            .time.duration = 1
                        else . end
                    ])
                else
                    .data
                end)
            }]' "$REPORT_DIR/history/duration-trend.json" > "$TEMP_FILE" 2>/dev/null
            
            # Verify the temp file is valid and non-empty
            if [ $? -eq 0 ] && [ -s "$TEMP_FILE" ]; then
                TEMP_VALID=$(jq -e 'if type == "array" then length > 0 else false end' "$TEMP_FILE" 2>/dev/null || echo "false")
                if [ "$TEMP_VALID" = "true" ]; then
                    ORIGINAL_ZERO_COUNT=$(jq '[.[] | .data[]? | select(.time and (.time.duration == 0 or (.time.start == .time.stop)))] | length' "$REPORT_DIR/history/duration-trend.json" 2>/dev/null || echo "0")
                    NEW_ZERO_COUNT=$(jq '[.[] | .data[]? | select(.time and (.time.duration == 0 or (.time.start == .time.stop)))] | length' "$TEMP_FILE" 2>/dev/null || echo "0")
                    FIXED_IN_FILE=$((ORIGINAL_ZERO_COUNT - NEW_ZERO_COUNT))
                    
                    if [ "$FIXED_IN_FILE" -gt 0 ]; then
                        mv "$TEMP_FILE" "$REPORT_DIR/history/duration-trend.json"
                        FIXED_COUNT=$((FIXED_COUNT + FIXED_IN_FILE))
                        FILES_FIXED=$((FILES_FIXED + 1))
                        echo "   ✅ Fixed $FIXED_IN_FILE zero-duration entry/entries in duration-trend.json"
                    else
                        # Even if no fixes, preserve the file if it's valid
                        mv "$TEMP_FILE" "$REPORT_DIR/history/duration-trend.json"
                        echo "   ℹ️  No zero-duration entries found in duration-trend.json (file preserved)"
                    fi
                else
                    rm -f "$TEMP_FILE"
                    echo "   ⚠️  Fix resulted in invalid/empty array - preserving original file"
                fi
            else
                rm -f "$TEMP_FILE"
                echo "   ⚠️  Failed to process duration-trend.json - preserving original file"
            fi
        fi
    fi
fi

# Also fix in results directory if it exists (for next run)
if [ -f "$RESULTS_DIR/history/history-trend.json" ]; then
    echo "📊 Fixing history-trend.json in results directory..."
    
    TEMP_FILE=$(mktemp)
    
    jq '[.[] | {
        buildOrder: .buildOrder,
        reportName: .reportName,
        reportUrl: .reportUrl,
        data: ([.data[] | 
            if .time.duration == 0 or (.time.start == .time.stop) then
                .time.stop = (.time.start + 1) |
                .time.duration = 1
            else . end
        ])
    }]' "$RESULTS_DIR/history/history-trend.json" > "$TEMP_FILE" 2>/dev/null
    
    ORIGINAL_ZERO_COUNT=$(jq '[.[] | .data[] | select(.time.duration == 0 or .time.start == .time.stop)] | length' "$RESULTS_DIR/history/history-trend.json" 2>/dev/null || echo "0")
    NEW_ZERO_COUNT=$(jq '[.[] | .data[] | select(.time.duration == 0 or .time.start == .time.stop)] | length' "$TEMP_FILE" 2>/dev/null || echo "0")
    FIXED_IN_FILE=$((ORIGINAL_ZERO_COUNT - NEW_ZERO_COUNT))
    
    if [ "$FIXED_IN_FILE" -gt 0 ]; then
        mv "$TEMP_FILE" "$RESULTS_DIR/history/history-trend.json"
        echo "   ✅ Fixed $FIXED_IN_FILE zero-duration entry/entries in results directory"
    else
        rm -f "$TEMP_FILE"
        echo "   ℹ️  No zero-duration entries found in results directory"
    fi
fi

# CRITICAL: Copy fixed history-trend.json to widgets directory
# The UI uses widgets/history-trend.json for rendering, not history/history-trend.json
if [ -f "$REPORT_DIR/history/history-trend.json" ] && [ -d "$REPORT_DIR/widgets" ]; then
    echo ""
    echo "📊 Copying fixed history-trend.json to widgets directory (UI uses this for rendering)..."
    cp "$REPORT_DIR/history/history-trend.json" "$REPORT_DIR/widgets/history-trend.json" 2>/dev/null || true
    echo "   ✅ Copied fixed history-trend.json to widgets directory"
    echo "   This ensures the UI uses the fixed data with valid timestamps"
fi

echo ""
if [ "$FIXED_COUNT" -gt 0 ]; then
    echo "✅ Zero duration fix completed"
    echo "   Fixed $FIXED_COUNT entry/entries across $FILES_FIXED file(s)"
    echo "   This should resolve NaN errors in trend chart rendering"
else
    echo "✅ No zero-duration entries found - history data is valid"
fi
