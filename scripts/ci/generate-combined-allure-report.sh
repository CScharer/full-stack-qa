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
else
    echo "   ✅ Container files present - Suites tab should display correctly"
    # Show container file breakdown by framework
    echo "   📊 Container breakdown:"
    find "$RESULTS_DIR" -name "*-container.json" -exec basename {} \; 2>/dev/null | head -20 | while read -r container_file; do
        # Try to extract framework name from container file content
        if [ -f "$RESULTS_DIR/$container_file" ]; then
            container_name=$(grep -o '"name"[[:space:]]*:[[:space:]]*"[^"]*"' "$RESULTS_DIR/$container_file" 2>/dev/null | head -1 | sed 's/.*"name"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/' || echo "unknown")
            echo "      - $container_file: $container_name"
        fi
    done
    if [ "$CONTAINER_COUNT" -gt 20 ]; then
        echo "      ... and $((CONTAINER_COUNT - 20)) more container files"
    fi
fi

# Generate report
# Note: We preserve history manually, so we can use --clean for fresh report
echo ""
echo "🔄 Generating Allure report..."
allure generate "$RESULTS_DIR" --clean -o "$REPORT_DIR"

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

