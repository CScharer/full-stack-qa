#!/bin/bash
# Analyze and Fix Allure Timestamp Issues
# Usage: ./scripts/ci/analyze-and-fix-allure-timestamps.sh [options] [results-dir]
#
# Arguments:
#   results-dir  - Directory containing Allure results (default: allure-results-combined)
#
# Options:
#   --analyze-only    - Only analyze, don't fix
#   --fix             - Automatically fix issues found
#   --backup          - Create backup before fixing (default: true)
#   --no-backup       - Don't create backup
#
# Configuration:
#   Reads from config/environments.json (allure.reportVersion)
#   Can be overridden via ALLURE_REPORT_VERSION environment variable
#   Supports both Allure2 (history JSON files) and Allure3 (history.jsonl)
#
# Examples:
#   ./scripts/ci/analyze-and-fix-allure-timestamps.sh --analyze-only
#   ./scripts/ci/analyze-and-fix-allure-timestamps.sh --fix --backup allure-results-combined

set -e

# Read Allure version from config file, fallback to environment variable, then default
if [ -f "config/environments.json" ]; then
    ALLURE_VERSION="${ALLURE_REPORT_VERSION:-$(jq -r '.allure.reportVersion // 2' config/environments.json 2>/dev/null || echo "2")}"
else
    # Fallback if config file doesn't exist
    ALLURE_VERSION="${ALLURE_REPORT_VERSION:-2}"
fi

if [ "$ALLURE_VERSION" != "2" ] && [ "$ALLURE_VERSION" != "3" ]; then
    echo "⚠️  Warning: Invalid ALLURE_REPORT_VERSION: $ALLURE_VERSION (must be 2 or 3)"
    echo "   Defaulting to Allure2"
    ALLURE_VERSION=2
fi

# Default values
RESULTS_DIR="${1:-allure-results-combined}"
ANALYZE_ONLY=false
AUTO_FIX=false
CREATE_BACKUP=true

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --analyze-only)
            ANALYZE_ONLY=true
            shift
            ;;
        --fix)
            AUTO_FIX=true
            shift
            ;;
        --backup)
            CREATE_BACKUP=true
            shift
            ;;
        --no-backup)
            CREATE_BACKUP=false
            shift
            ;;
        *)
            if [ -d "$1" ]; then
                RESULTS_DIR="$1"
            fi
            shift
            ;;
    esac
done

echo "🔍 Allure Timestamp Analysis and Fix Tool"
echo "=========================================="
echo ""
echo "Results directory: $RESULTS_DIR"
echo "Allure version: $ALLURE_VERSION (from config/environments.json)"
echo "Mode: $([ "$ANALYZE_ONLY" = true ] && echo "Analyze only" || echo "Analyze + Fix")"
echo ""

# Check if results directory exists
if [ ! -d "$RESULTS_DIR" ]; then
    echo "❌ Error: Results directory not found: $RESULTS_DIR"
    exit 1
fi

# Check if jq is available
if ! command -v jq &> /dev/null; then
    echo "❌ Error: jq is required but not installed"
    echo "   Install with: brew install jq (macOS) or apt-get install jq (Linux)"
    exit 1
fi

# Current time in milliseconds (for reference)
CURRENT_TIME_MS=$(date +%s%3N 2>/dev/null || echo "$(date +%s)000")
CURRENT_TIME_SEC=$((CURRENT_TIME_MS / 1000))
EPOCH_TIME_MS=0
EPOCH_TIME_SEC=0

# Unix epoch: January 1, 1970 00:00:00 UTC
# Reasonable minimum: January 1, 2020 00:00:00 UTC (1577836800000 ms)
REASONABLE_MIN_MS=1577836800000
REASONABLE_MIN_SEC=$((REASONABLE_MIN_MS / 1000))

echo "📊 Timestamp Reference:"
echo "   Current time: $(date -r $CURRENT_TIME_SEC '+%Y-%m-%d %H:%M:%S' 2>/dev/null || date '+%Y-%m-%d %H:%M:%S')"
echo "   Current timestamp (ms): $CURRENT_TIME_MS"
echo "   Reasonable minimum (2020-01-01): $REASONABLE_MIN_MS"
echo ""

# Track issues
ISSUES_FOUND=0
CORRUPTED_FILES=()
INVALID_TIMESTAMPS=()
FIXED_FILES=()

# ============================================================================
# 1. ANALYZE RESULT FILES
# ============================================================================
echo "🔍 Step 1: Analyzing result files (*-result.json)..."
echo ""

RESULT_FILES=$(find "$RESULTS_DIR" -name "*-result.json" -type f 2>/dev/null | head -1000)
RESULT_COUNT=$(echo "$RESULT_FILES" | grep -c . || echo "0")

if [ "$RESULT_COUNT" -eq 0 ]; then
    echo "   ⚠️  No result files found"
else
    echo "   Found $RESULT_COUNT result file(s)"
    
    MIN_START=$CURRENT_TIME_MS
    MAX_STOP=0
    INVALID_COUNT=0
    SECONDS_COUNT=0
    EPOCH_COUNT=0
    FUTURE_COUNT=0
    
    while IFS= read -r file; do
        if [ -z "$file" ]; then continue; fi
        
        # Extract start and stop timestamps
        start=$(jq -r '.start // 0' "$file" 2>/dev/null || echo "0")
        stop=$(jq -r '.stop // 0' "$file" 2>/dev/null || echo "0")
        
        # Check if timestamps are valid
        if [ "$start" = "0" ] || [ "$stop" = "0" ] || [ "$start" = "null" ] || [ "$stop" = "null" ]; then
            INVALID_COUNT=$((INVALID_COUNT + 1))
            INVALID_TIMESTAMPS+=("$file: missing start/stop")
            continue
        fi
        
        # Check if timestamp is in seconds (10 digits) instead of milliseconds (13 digits)
        if [ ${#start} -lt 12 ]; then
            SECONDS_COUNT=$((SECONDS_COUNT + 1))
            INVALID_TIMESTAMPS+=("$file: start timestamp in seconds (${#start} digits: $start)")
        fi
        
        if [ ${#stop} -lt 12 ]; then
            SECONDS_COUNT=$((SECONDS_COUNT + 1))
            INVALID_TIMESTAMPS+=("$file: stop timestamp in seconds (${#stop} digits: $stop)")
        fi
        
        # Check if timestamp is at or before epoch (1970)
        if [ "$start" -le "$EPOCH_TIME_MS" ] || [ "$start" -lt "$REASONABLE_MIN_MS" ]; then
            EPOCH_COUNT=$((EPOCH_COUNT + 1))
            INVALID_TIMESTAMPS+=("$file: start timestamp too old ($start)")
        fi
        
        # Check if timestamp is in the future (more than 1 hour ahead)
        FUTURE_THRESHOLD=$((CURRENT_TIME_MS + 3600000))
        if [ "$start" -gt "$FUTURE_THRESHOLD" ]; then
            FUTURE_COUNT=$((FUTURE_COUNT + 1))
            INVALID_TIMESTAMPS+=("$file: start timestamp in future ($start)")
        fi
        
        # Track min/max for duration calculation
        if [ "$start" -lt "$MIN_START" ] && [ "$start" -gt "$REASONABLE_MIN_MS" ]; then
            MIN_START=$start
        fi
        if [ "$stop" -gt "$MAX_STOP" ] && [ "$stop" -lt "$FUTURE_THRESHOLD" ]; then
            MAX_STOP=$stop
        fi
    done <<< "$RESULT_FILES"
    
    echo "   ✅ Analysis complete:"
    echo "      Total files: $RESULT_COUNT"
    echo "      Invalid timestamps: $INVALID_COUNT"
    echo "      Timestamps in seconds (should be ms): $SECONDS_COUNT"
    echo "      Timestamps too old (<= 2020): $EPOCH_COUNT"
    echo "      Timestamps in future: $FUTURE_COUNT"
    
    if [ "$INVALID_COUNT" -gt 0 ] || [ "$SECONDS_COUNT" -gt 0 ] || [ "$EPOCH_COUNT" -gt 0 ] || [ "$FUTURE_COUNT" -gt 0 ]; then
        ISSUES_FOUND=$((ISSUES_FOUND + INVALID_COUNT + SECONDS_COUNT + EPOCH_COUNT + FUTURE_COUNT))
        echo ""
        echo "   ⚠️  Issues found in result files!"
        if [ ${#INVALID_TIMESTAMPS[@]} -gt 0 ] && [ ${#INVALID_TIMESTAMPS[@]} -le 10 ]; then
            echo "      Sample issues:"
            for issue in "${INVALID_TIMESTAMPS[@]:0:5}"; do
                echo "        - $issue"
            done
        fi
    fi
    
    # Calculate expected duration
    if [ "$MIN_START" -lt "$CURRENT_TIME_MS" ] && [ "$MAX_STOP" -gt 0 ] && [ "$MAX_STOP" -gt "$MIN_START" ]; then
        DURATION_MS=$((MAX_STOP - MIN_START))
        DURATION_SEC=$((DURATION_MS / 1000))
        DURATION_MIN=$((DURATION_SEC / 60))
        DURATION_HOUR=$((DURATION_MIN / 60))
        
        echo ""
        echo "   📊 Timestamp Range Analysis:"
        MIN_START_SEC=$((MIN_START / 1000))
        MAX_STOP_SEC=$((MAX_STOP / 1000))
        echo "      Earliest start: $(date -r $MIN_START_SEC '+%Y-%m-%d %H:%M:%S' 2>/dev/null || echo "N/A")"
        echo "      Latest stop: $(date -r $MAX_STOP_SEC '+%Y-%m-%d %H:%M:%S' 2>/dev/null || echo "N/A")"
        echo "      Calculated duration: ${DURATION_HOUR}h ${DURATION_MIN}m ${DURATION_SEC}s"
        
        if [ "$DURATION_HOUR" -gt 24 ]; then
            echo "      ⚠️  WARNING: Duration exceeds 24 hours - likely timestamp issue!"
            ISSUES_FOUND=$((ISSUES_FOUND + 1))
        fi
    fi
fi

echo ""

# ============================================================================
# 2. ANALYZE HISTORY DATA
# ============================================================================
echo "🔍 Step 2: Analyzing history data (Allure$ALLURE_VERSION format)..."
echo ""

HISTORY_ISSUES=0

# Allure3 uses history.jsonl format
if [ "$ALLURE_VERSION" = "3" ]; then
    # Check history.jsonl (Allure3 format)
    if [ -f "$RESULTS_DIR/history/history.jsonl" ]; then
    echo "   Found history.jsonl (Allure3 format)"
    HISTORY_LINES=$(wc -l < "$RESULTS_DIR/history/history.jsonl" 2>/dev/null | tr -d ' ' || echo "0")
    echo "   History entries: $HISTORY_LINES"
    
    LINE_NUM=0
    while IFS= read -r line; do
        LINE_NUM=$((LINE_NUM + 1))
        if [ -z "$line" ]; then continue; fi
        
        # Extract buildOrder and check data array
        build_order=$(echo "$line" | jq -r '.buildOrder // 0' 2>/dev/null || echo "0")
        data_array=$(echo "$line" | jq '.data // []' 2>/dev/null || echo "[]")
        
        # Check if data is an array
        data_type=$(echo "$data_array" | jq 'type' 2>/dev/null || echo "null")
        if [ "$data_type" != '"array"' ]; then
            HISTORY_ISSUES=$((HISTORY_ISSUES + 1))
            echo "      ⚠️  Line $LINE_NUM: data is not an array (type: $data_type)"
        fi
        
        # Check timestamps in data array
        if [ "$data_type" = '"array"' ]; then
            data_count=$(echo "$data_array" | jq 'length' 2>/dev/null || echo "0")
            if [ "$data_count" -gt 0 ]; then
                # Check each test's time.start and time.stop
                test_index=0
                while [ "$test_index" -lt "$data_count" ]; do
                    test_data=$(echo "$data_array" | jq ".[$test_index]" 2>/dev/null)
                    if [ "$test_data" != "null" ] && [ -n "$test_data" ]; then
                        time_obj=$(echo "$test_data" | jq '.time // {}' 2>/dev/null)
                        start=$(echo "$time_obj" | jq -r '.start // 0' 2>/dev/null || echo "0")
                        stop=$(echo "$time_obj" | jq -r '.stop // 0' 2>/dev/null || echo "0")
                        
                        # Check for invalid timestamps
                        if [ "$start" != "0" ] && [ "$start" != "null" ]; then
                            if [ ${#start} -lt 12 ]; then
                                HISTORY_ISSUES=$((HISTORY_ISSUES + 1))
                                echo "      ⚠️  Line $LINE_NUM, test $test_index: start timestamp in seconds ($start)"
                            elif [ "$start" -lt "$REASONABLE_MIN_MS" ]; then
                                HISTORY_ISSUES=$((HISTORY_ISSUES + 1))
                                echo "      ⚠️  Line $LINE_NUM, test $test_index: start timestamp too old ($start)"
                            fi
                        fi
                        
                        if [ "$stop" != "0" ] && [ "$stop" != "null" ]; then
                            if [ ${#stop} -lt 12 ]; then
                                HISTORY_ISSUES=$((HISTORY_ISSUES + 1))
                                echo "      ⚠️  Line $LINE_NUM, test $test_index: stop timestamp in seconds ($stop)"
                            elif [ "$stop" -lt "$REASONABLE_MIN_MS" ]; then
                                HISTORY_ISSUES=$((HISTORY_ISSUES + 1))
                                echo "      ⚠️  Line $LINE_NUM, test $test_index: stop timestamp too old ($stop)"
                            fi
                        fi
                    fi
                    test_index=$((test_index + 1))
                done
            fi
        fi
    done < "$RESULTS_DIR/history/history.jsonl"
    
        if [ "$HISTORY_ISSUES" -eq 0 ]; then
            echo "   ✅ No issues found in history.jsonl"
        else
            echo "   ⚠️  Found $HISTORY_ISSUES issue(s) in history.jsonl"
            ISSUES_FOUND=$((ISSUES_FOUND + HISTORY_ISSUES))
        fi
    else
        echo "   ℹ️  No history.jsonl found (expected for first few runs with Allure3)"
    fi
fi

# Allure2 uses individual JSON files per test (or history-trend.json)
if [ "$ALLURE_VERSION" = "2" ]; then
    # Check for Allure2 history format (individual JSON files)
    if [ -d "$RESULTS_DIR/history" ]; then
        HISTORY_FILES=$(find "$RESULTS_DIR/history" -name "*.json" ! -name "history-trend.json" ! -name "duration-trend.json" ! -name "retry-trend.json" -type f 2>/dev/null | wc -l | tr -d ' ')
        if [ "$HISTORY_FILES" -gt 0 ]; then
            echo "   Found Allure2 history format: $HISTORY_FILES individual JSON file(s)"
            
            # Sample a few files to check for timestamp issues
            SAMPLE_FILES=$(find "$RESULTS_DIR/history" -name "*.json" ! -name "history-trend.json" ! -name "duration-trend.json" ! -name "retry-trend.json" -type f 2>/dev/null | head -10)
            SAMPLE_COUNT=0
            
            while IFS= read -r file; do
                if [ -z "$file" ]; then continue; fi
                SAMPLE_COUNT=$((SAMPLE_COUNT + 1))
                
                # Check for timestamp fields in Allure2 history format
                # Allure2 history files may have different structures, check common patterns
                if jq -e '.time.start' "$file" >/dev/null 2>&1; then
                    start=$(jq -r '.time.start // 0' "$file" 2>/dev/null || echo "0")
                    if [ "$start" != "0" ] && [ "$start" != "null" ]; then
                        if [ ${#start} -lt 12 ]; then
                            HISTORY_ISSUES=$((HISTORY_ISSUES + 1))
                            echo "      ⚠️  $file: start timestamp in seconds ($start)"
                        elif [ "$start" -lt "$REASONABLE_MIN_MS" ]; then
                            HISTORY_ISSUES=$((HISTORY_ISSUES + 1))
                            echo "      ⚠️  $file: start timestamp too old ($start)"
                        fi
                    fi
                fi
            done <<< "$SAMPLE_FILES"
            
            if [ "$HISTORY_ISSUES" -eq 0 ]; then
                echo "   ✅ No issues found in Allure2 history files (sampled $SAMPLE_COUNT of $HISTORY_FILES)"
            else
                echo "   ⚠️  Found $HISTORY_ISSUES issue(s) in Allure2 history files"
                ISSUES_FOUND=$((ISSUES_FOUND + HISTORY_ISSUES))
            fi
        else
            echo "   ℹ️  No Allure2 history files found (expected for first few runs)"
        fi
    fi
fi

# Check history-trend.json (used by both Allure2 and Allure3 for UI display)
if [ -f "$RESULTS_DIR/history/history-trend.json" ]; then
    echo "   Found history-trend.json (UI format)"
    TREND_ENTRIES=$(jq 'length' "$RESULTS_DIR/history/history-trend.json" 2>/dev/null || echo "0")
    echo "   Trend entries: $TREND_ENTRIES"
    
    # Check for object data (should be array)
    OBJECT_DATA_COUNT=$(jq '[.[] | select(.data | type == "object")] | length' "$RESULTS_DIR/history/history-trend.json" 2>/dev/null || echo "0")
    if [ "$OBJECT_DATA_COUNT" -gt 0 ]; then
        echo "   ⚠️  Found $OBJECT_DATA_COUNT entry/entries with object data (should be array)"
        ISSUES_FOUND=$((ISSUES_FOUND + OBJECT_DATA_COUNT))
    fi
fi

echo ""

# ============================================================================
# 3. SUMMARY AND FIX OPTIONS
# ============================================================================
echo "📊 Summary"
echo "=========="
echo "   Total issues found: $ISSUES_FOUND"
echo ""

if [ "$ISSUES_FOUND" -eq 0 ]; then
    echo "✅ No timestamp issues detected!"
    exit 0
fi

echo "⚠️  Issues detected:"
echo "   - Invalid/missing timestamps in result files"
echo "   - Timestamps in wrong format (seconds vs milliseconds)"
echo "   - Timestamps too old (epoch or before 2020)"
echo "   - History data format issues"
echo ""

if [ "$ANALYZE_ONLY" = true ]; then
    echo "ℹ️  Analysis complete. Run with --fix to automatically fix issues."
    exit 0
fi

if [ "$AUTO_FIX" = false ]; then
    echo "❓ Would you like to fix these issues? (y/n)"
    read -r response
    if [ "$response" != "y" ] && [ "$response" != "Y" ]; then
        echo "ℹ️  Fix cancelled. Run with --fix to automatically fix."
        exit 0
    fi
fi

# ============================================================================
# 4. FIX ISSUES
# ============================================================================
echo ""
echo "🔧 Fixing issues..."
echo ""

# Create backup if requested
if [ "$CREATE_BACKUP" = true ]; then
    BACKUP_DIR="${RESULTS_DIR}-backup-$(date +%s)"
    echo "📦 Creating backup: $BACKUP_DIR"
    cp -r "$RESULTS_DIR" "$BACKUP_DIR" 2>/dev/null || {
        echo "⚠️  Warning: Could not create full backup, backing up history only"
        mkdir -p "$BACKUP_DIR/history"
        cp -r "$RESULTS_DIR/history"/* "$BACKUP_DIR/history/" 2>/dev/null || true
    }
    echo "✅ Backup created"
    echo ""
fi

# Fix result files with invalid timestamps
FIXED_COUNT=0
if [ "$RESULT_COUNT" -gt 0 ]; then
    echo "🔧 Fixing result files..."
    
    while IFS= read -r file; do
        if [ -z "$file" ]; then continue; fi
        
        start=$(jq -r '.start // 0' "$file" 2>/dev/null || echo "0")
        stop=$(jq -r '.stop // 0' "$file" 2>/dev/null || echo "0")
        needs_fix=false
        new_start=$start
        new_stop=$stop
        
        # Fix if timestamp is in seconds (convert to milliseconds)
        if [ "$start" != "0" ] && [ "$start" != "null" ] && [ ${#start} -lt 12 ]; then
            new_start=$((start * 1000))
            needs_fix=true
        fi
        
        if [ "$stop" != "0" ] && [ "$stop" != "null" ] && [ ${#stop} -lt 12 ]; then
            new_stop=$((stop * 1000))
            needs_fix=true
        fi
        
        # Fix if timestamp is too old (use current time as fallback)
        if [ "$new_start" -lt "$REASONABLE_MIN_MS" ] || [ "$new_start" = "0" ] || [ "$new_start" = "null" ]; then
            new_start=$CURRENT_TIME_MS
            new_stop=$((CURRENT_TIME_MS + 1000))
            needs_fix=true
        fi
        
        if [ "$new_stop" -lt "$new_start" ]; then
            new_stop=$((new_start + 1000))
            needs_fix=true
        fi
        
        if [ "$needs_fix" = true ]; then
            # Update the file
            jq --argjson new_start "$new_start" --argjson new_stop "$new_stop" \
               '.start = $new_start | .stop = $new_stop' "$file" > "${file}.tmp" 2>/dev/null && \
            mv "${file}.tmp" "$file" 2>/dev/null && {
                FIXED_COUNT=$((FIXED_COUNT + 1))
                FIXED_FILES+=("$file")
            } || echo "   ⚠️  Could not fix: $file"
        fi
    done <<< "$RESULT_FILES"
    
    echo "   ✅ Fixed $FIXED_COUNT result file(s)"
fi

# Fix Allure2 history files (individual JSON files)
if [ "$ALLURE_VERSION" = "2" ] && [ "$HISTORY_ISSUES" -gt 0 ] && [ -d "$RESULTS_DIR/history" ]; then
    echo "🔧 Fixing Allure2 history files..."
    
    HISTORY_FILES_TO_FIX=$(find "$RESULTS_DIR/history" -name "*.json" ! -name "history-trend.json" ! -name "duration-trend.json" ! -name "retry-trend.json" -type f 2>/dev/null | head -100)
    FIXED_HISTORY_COUNT=0
    
    while IFS= read -r file; do
        if [ -z "$file" ]; then continue; fi
        
        # Check if file has timestamp issues and fix
        if jq -e '.time.start' "$file" >/dev/null 2>&1; then
            start=$(jq -r '.time.start // 0' "$file" 2>/dev/null || echo "0")
            stop=$(jq -r '.time.stop // 0' "$file" 2>/dev/null || echo "0")
            needs_fix=false
            new_start=$start
            new_stop=$stop
            
            # Fix if timestamp is in seconds
            if [ "$start" != "0" ] && [ "$start" != "null" ] && [ ${#start} -lt 12 ]; then
                new_start=$((start * 1000))
                needs_fix=true
            fi
            
            if [ "$stop" != "0" ] && [ "$stop" != "null" ] && [ ${#stop} -lt 12 ]; then
                new_stop=$((stop * 1000))
                needs_fix=true
            fi
            
            # Fix if timestamp is too old
            if [ "$new_start" -lt "$REASONABLE_MIN_MS" ] || [ "$new_start" = "0" ] || [ "$new_start" = "null" ]; then
                new_start=$CURRENT_TIME_MS
                new_stop=$((CURRENT_TIME_MS + 1000))
                needs_fix=true
            fi
            
            if [ "$needs_fix" = true ]; then
                jq --argjson new_start "$new_start" --argjson new_stop "$new_stop" \
                   '.time.start = $new_start | .time.stop = $new_stop' "$file" > "${file}.tmp" 2>/dev/null && \
                mv "${file}.tmp" "$file" 2>/dev/null && {
                    FIXED_HISTORY_COUNT=$((FIXED_HISTORY_COUNT + 1))
                } || echo "   ⚠️  Could not fix: $file"
            fi
        fi
    done <<< "$HISTORY_FILES_TO_FIX"
    
    if [ "$FIXED_HISTORY_COUNT" -gt 0 ]; then
        echo "   ✅ Fixed $FIXED_HISTORY_COUNT Allure2 history file(s)"
    else
        echo "   ℹ️  No Allure2 history files needed fixing"
    fi
fi

# Fix history.jsonl (Allure3 only)
if [ "$ALLURE_VERSION" = "3" ] && [ -f "$RESULTS_DIR/history/history.jsonl" ] && [ "$HISTORY_ISSUES" -gt 0 ]; then
    echo "🔧 Fixing history.jsonl (Allure3 format)..."
    
    TEMP_HISTORY=$(mktemp)
    FIXED_HISTORY_COUNT=0
    
    while IFS= read -r line; do
        if [ -z "$line" ]; then
            echo "" >> "$TEMP_HISTORY"
            continue
        fi
        
        # Check and fix timestamps in data array
        fixed_line=$(echo "$line" | jq '
            if .data and (.data | type == "array") then
                .data = (.data | map(
                    if .time then
                        .time = (.time | 
                            (if .start and (.start | type == "number") and (.start | tostring | length) < 12 then
                                .start = (.start * 1000)
                            else . end) |
                            (if .stop and (.stop | type == "number") and (.stop | tostring | length) < 12 then
                                .stop = (.stop * 1000)
                            else . end) |
                            (if .start and .start < 1577836800000 then
                                .start = (now * 1000 | floor)
                            else . end) |
                            (if .stop and .stop < 1577836800000 then
                                .stop = ((now * 1000 | floor) + 1000)
                            else . end)
                        )
                    else . end
                ))
            else . end
        ' 2>/dev/null || echo "$line")
        
        echo "$fixed_line" >> "$TEMP_HISTORY"
        if [ "$fixed_line" != "$line" ]; then
            FIXED_HISTORY_COUNT=$((FIXED_HISTORY_COUNT + 1))
        fi
    done < "$RESULTS_DIR/history/history.jsonl"
    
    if [ "$FIXED_HISTORY_COUNT" -gt 0 ]; then
        mv "$TEMP_HISTORY" "$RESULTS_DIR/history/history.jsonl"
        echo "   ✅ Fixed $FIXED_HISTORY_COUNT history entry/entries"
    else
        rm -f "$TEMP_HISTORY"
        echo "   ℹ️  No history entries needed fixing"
    fi
fi

# Rebuild history-trend.json if needed (Allure3 only - converts from history.jsonl)
if [ "$ALLURE_VERSION" = "3" ] && [ -f "$RESULTS_DIR/history/history.jsonl" ] && [ -f "$RESULTS_DIR/history/history-trend.json" ]; then
    OBJECT_DATA_COUNT=$(jq '[.[] | select(.data | type == "object")] | length' "$RESULTS_DIR/history/history-trend.json" 2>/dev/null || echo "0")
    if [ "$OBJECT_DATA_COUNT" -gt 0 ]; then
        echo "🔧 Rebuilding history-trend.json from history.jsonl..."
        
        TEMP_TREND=$(mktemp)
        echo "[]" > "$TEMP_TREND"
        
        while IFS= read -r line; do
            if [ -n "$line" ]; then
                build_order=$(echo "$line" | jq -r '.buildOrder // 0' 2>/dev/null || echo "0")
                report_name=$(echo "$line" | jq -r '.reportName // "Allure Report"' 2>/dev/null || echo "Allure Report")
                report_url=$(echo "$line" | jq -r '.reportUrl // ""' 2>/dev/null || echo "")
                data_array=$(echo "$line" | jq '.data // []' 2>/dev/null || echo "[]")
                
                # Ensure data is an array
                data_type=$(echo "$data_array" | jq 'type' 2>/dev/null || echo "null")
                if [ "$data_type" = '"object"' ]; then
                    data_array=$(echo "$data_array" | jq '[.]' 2>/dev/null || echo "[]")
                fi
                
                if [ "$data_array" != "[]" ] && [ "$data_array" != "null" ]; then
                    jq --argjson build_order "$build_order" \
                       --arg report_name "$report_name" \
                       --arg report_url "$report_url" \
                       --argjson data_array "$data_array" \
                       '. += [{
                         "buildOrder": $build_order,
                         "reportName": $report_name,
                         "reportUrl": $report_url,
                         "data": $data_array
                       }]' "$TEMP_TREND" > "${TEMP_TREND}.tmp" 2>/dev/null && \
                    mv "${TEMP_TREND}.tmp" "$TEMP_TREND" 2>/dev/null || true
                fi
            fi
        done < "$RESULTS_DIR/history/history.jsonl"
        
        mv "$TEMP_TREND" "$RESULTS_DIR/history/history-trend.json"
        echo "   ✅ Rebuilt history-trend.json with correct array format"
    fi
fi

echo ""
echo "✅ Fix complete!"
echo "   Fixed files: $FIXED_COUNT"
if [ "$CREATE_BACKUP" = true ]; then
    echo "   Backup location: $BACKUP_DIR"
fi
echo ""
echo "💡 Next steps:"
echo "   1. Regenerate Allure report: ./scripts/ci/generate-combined-allure-report.sh"
echo "   2. Verify the Overview shows correct time range"
echo "   3. If issues persist, check the backup and restore if needed"
