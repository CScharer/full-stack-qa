#!/bin/bash
# Generate Combined Allure Report - SIMPLIFIED VERSION
# This version lets Allure3 handle history creation naturally
# Usage: ./scripts/ci/generate-combined-allure-report-simplified.sh [results-dir] [report-dir]

set -e

RESULTS_DIR="${1:-allure-results-combined}"
REPORT_DIR="${2:-allure-report-combined}"

echo "📊 Generating combined Allure report (simplified - letting Allure3 handle history)..."
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

# SIMPLIFIED APPROACH: Just ensure history exists in results directory
# Allure3 will handle merging and creation automatically
if [ -d "$RESULTS_DIR/history" ] && [ "$(find "$RESULTS_DIR/history" -type f -name "*.json" 2>/dev/null | wc -l | tr -d ' ')" -gt 0 ]; then
    echo ""
    echo "📊 History found in results directory:"
    HISTORY_FILE_COUNT=$(find "$RESULTS_DIR/history" -type f -name "*.json" 2>/dev/null | wc -l | tr -d ' ')
    echo "   Files: $HISTORY_FILE_COUNT file(s)"
    echo "   Size: $(du -sh "$RESULTS_DIR/history" 2>/dev/null | cut -f1 || echo 'unknown')"
    echo "   ✅ History will be processed by Allure3 during report generation"
    echo "   Allure3 will merge existing history with new test results"
else
    echo ""
    echo "ℹ️  No history found in results directory (expected for first few runs)"
    echo "   Allure3 will create history naturally from test results"
    echo "   History will appear after 2-3 pipeline runs"
fi

# Generate report - Allure3 will handle history automatically
echo ""
echo "🔄 Generating Allure report..."
echo "   Allure3 will process history and create updated history in the report"
rm -rf "$REPORT_DIR"
allure generate "$RESULTS_DIR" -o "$REPORT_DIR"

# Check if Allure3 created history
if [ -d "$REPORT_DIR/history" ] && [ "$(find "$REPORT_DIR/history" -type f -name "*.json" 2>/dev/null | wc -l | tr -d ' ')" -gt 0 ]; then
    echo ""
    echo "✅ Allure3 created history in report"
    HISTORY_FILE_COUNT=$(find "$REPORT_DIR/history" -type f -name "*.json" 2>/dev/null | wc -l | tr -d ' ')
    echo "   Files: $HISTORY_FILE_COUNT file(s)"
    echo "   Size: $(du -sh "$REPORT_DIR/history" 2>/dev/null | cut -f1 || echo 'unknown')"
    
    # Preserve history for next run
    echo ""
    echo "📊 Preserving history for next run..."
    mkdir -p "$RESULTS_DIR/history"
    cp -r "$REPORT_DIR/history"/* "$RESULTS_DIR/history/" 2>/dev/null || true
    PRESERVED_COUNT=$(find "$RESULTS_DIR/history" -type f -name "*.json" 2>/dev/null | wc -l | tr -d ' ')
    echo "✅ History preserved: $PRESERVED_COUNT file(s) ready for next report generation"
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


