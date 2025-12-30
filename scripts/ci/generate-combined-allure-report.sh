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

# Generate report
# Note: Allure3 CLI doesn't support --clean flag, so we remove the directory first
echo ""
echo "🔄 Generating Allure report..."
rm -rf "$REPORT_DIR"
allure generate "$RESULTS_DIR" -o "$REPORT_DIR"

# Preserve history for next run (copy from report back to results)
if [ -d "$REPORT_DIR/history" ]; then
    echo ""
    echo "📊 Preserving history for next run..."
    mkdir -p "$RESULTS_DIR/history"
    cp -r "$REPORT_DIR/history"/* "$RESULTS_DIR/history/" 2>/dev/null || true
    echo "✅ History preserved for next report generation"
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

