#!/bin/bash
# Generate Combined Allure Report - APPROACH 4 & 5 (Let Allure3 Create History + BuildOrder Continuity)
# This version ensures Allure3 creates history first and verifies buildOrder continuity
# Usage: ./scripts/ci/generate-combined-allure-report.sh [results-dir] [report-dir]
#
# Arguments:
#   results-dir  - Directory containing Allure results (default: allure-results-combined)
#   report-dir   - Directory where Allure report will be generated (default: allure-report-combined)
#
# This script:
# 1. Verifies results directory exists and has files
# 2. Ensures buildOrder in executor.json is higher than latest history buildOrder (Step 5)
# 3. Lets Allure3 create history first if it doesn't exist (Step 4)
# 4. Generates Allure HTML report (Allure3 processes history)
# 5. Preserves history for next run (copies from report back to results)
# 6. Verifies report was generated successfully
#
# APPROACH 4 & 5:
# - Step 4: Let Allure3 create history first (bootstrap if needed)
# - Step 5: Ensure buildOrder continuity (executor.json buildOrder > latest history buildOrder)

set -e

RESULTS_DIR="${1:-allure-results-combined}"
REPORT_DIR="${2:-allure-report-combined}"

echo "📊 Generating combined Allure report (Approach 4 & 5 - Let Allure3 create history + buildOrder continuity)..."
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
    elif [ -f "$RESULTS_DIR/history/history.jsonl" ] || [ -f "$RESULTS_DIR/history/history-trend.json" ]; then
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
else
    echo ""
    echo "ℹ️  No history found in results directory (expected for first few runs)"
    echo "   Allure3 will create history naturally from test results"
    ALLURE3_CREATED_HISTORY=false
fi

# Generate report - Allure3 will create/process history
# CRITICAL: If history was manually created, we removed it so Allure3 can bootstrap
# Allure3 will:
# 1. Create fresh history if none exists
# 2. Process existing history if it was created by Allure3
# 3. Merge with new test results (matching by historyId)
# 4. Create updated history in REPORT_DIR/history/
echo ""
echo "🔄 Generating Allure report..."
if [ "$ALLURE3_CREATED_HISTORY" = true ]; then
    echo "   Allure3 will process existing Allure3-created history and merge with new results"
else
    echo "   Allure3 will create fresh history from test results (Step 4: bootstrap)"
fi
rm -rf "$REPORT_DIR"
# Generate Allure report with explicit --config flag
echo "   Running: allure generate \"$RESULTS_DIR\" -o \"$REPORT_DIR\""
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
    echo "⚠️  Allure generate command had warnings/errors (checking log...)"
    if [ -f /tmp/allure-generate.log ]; then
        echo "   Last 50 lines of Allure output:"
        tail -50 /tmp/allure-generate.log | sed 's/^/   /'
    fi
    # Check if the command actually failed or just had warnings
    if [ ! -d "$REPORT_DIR" ] || [ ! -f "$REPORT_DIR/index.html" ]; then
        echo "❌ Allure generate command failed - report not created"
        exit 1
    else
        echo "⚠️  Allure generate had warnings but report was created successfully"
    fi
}

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
# Allure3 may create history.jsonl or multiple JSON files
HISTORY_CREATED=false
if [ -f "$REPORT_DIR/history/history.jsonl" ]; then
    HISTORY_CREATED=true
    echo ""
    echo "✅ Allure3 created/updated history in report (history.jsonl format)"
    HISTORY_SIZE=$(du -sh "$REPORT_DIR/history/history.jsonl" 2>/dev/null | cut -f1 || echo 'unknown')
    HISTORY_LINES=$(wc -l < "$REPORT_DIR/history/history.jsonl" 2>/dev/null | tr -d ' ' || echo "0")
    echo "   History file: history.jsonl"
    echo "   History entries: $HISTORY_LINES line(s)"
    echo "   Size: $HISTORY_SIZE"
    
    # Preserve history for next run
    echo ""
    echo "📊 Preserving history for next run..."
    mkdir -p "$RESULTS_DIR/history"
    cp "$REPORT_DIR/history/history.jsonl" "$RESULTS_DIR/history/history.jsonl" 2>/dev/null || true
    echo "✅ History preserved: history.jsonl ready for next report generation"
    echo "   History will be uploaded as artifact and deployed to GitHub Pages"
elif [ -d "$REPORT_DIR/history" ] && [ "$(find "$REPORT_DIR/history" -type f -name "*.json" 2>/dev/null | wc -l | tr -d ' ')" -gt 0 ]; then
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
