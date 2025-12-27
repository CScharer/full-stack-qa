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

