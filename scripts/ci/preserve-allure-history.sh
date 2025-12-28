#!/bin/bash
# Preserve Allure History Between Report Generations
# Usage: ./scripts/ci/preserve-allure-history.sh <results-dir> <report-dir>
#
# Arguments:
#   results-dir  - Directory where Allure results are stored (default: allure-results-combined)
#   report-dir   - Directory where Allure report is generated (default: allure-report-combined)
#
# This script preserves the history folder from the previous report generation
# so that Trend section can show historical data across multiple runs.
#
# Examples:
#   ./scripts/ci/preserve-allure-history.sh allure-results-combined allure-report-combined

set -e

RESULTS_DIR="${1:-allure-results-combined}"
REPORT_DIR="${2:-allure-report-combined}"

echo "📊 Preserving Allure history for trend tracking..."

# Check if report directory exists and has history
if [ -d "$REPORT_DIR/history" ]; then
  echo "✅ Found existing history in $REPORT_DIR/history"
  
  # Copy history to results directory (Allure will use it when generating new report)
  mkdir -p "$RESULTS_DIR"
  cp -r "$REPORT_DIR/history" "$RESULTS_DIR/history" 2>/dev/null || {
    echo "⚠️  Warning: Could not copy history folder (may not exist yet)"
  }
  
  HISTORY_SIZE=$(du -sh "$RESULTS_DIR/history" 2>/dev/null | cut -f1 || echo "unknown")
  echo "   History preserved: $HISTORY_SIZE"
  echo "   History will be included in next report generation"
else
  echo "ℹ️  No existing history found (this is expected for first run)"
  echo "   History will be created after first report generation"
fi

# After report generation, copy history back for next run
# This is typically done after 'allure generate' command
if [ -d "$REPORT_DIR/history" ]; then
  echo "✅ History folder exists in report: $REPORT_DIR/history"
  echo "   This history will be preserved for next run"
fi

echo "✅ History preservation complete"

