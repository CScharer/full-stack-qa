#!/bin/bash
# Fix Allure Summary Timestamps
# Recalculates widgets/summary.json to use only current run's timestamps (not history)
# Usage: ./scripts/ci/fix-allure-summary-timestamps.sh [results-dir] [report-dir]

set -e

RESULTS_DIR="${1:-allure-results-combined}"
REPORT_DIR="${2:-allure-report-combined}"

echo "🔧 Fixing Allure Summary Timestamps"
echo "===================================="
echo "   Results directory: $RESULTS_DIR"
echo "   Report directory: $REPORT_DIR"
echo ""

if [ ! -f "$REPORT_DIR/widgets/summary.json" ]; then
    echo "⚠️  Summary file not found: $REPORT_DIR/widgets/summary.json"
    echo "   Skipping summary timestamp fix"
    exit 0
fi

# Get current buildOrder from executor.json to identify current run
CURRENT_BUILD_ORDER=""
if [ -f "$RESULTS_DIR/executor.json" ]; then
    CURRENT_BUILD_ORDER=$(jq -r '.buildOrder // ""' "$RESULTS_DIR/executor.json" 2>/dev/null || echo "")
fi

if [ -z "$CURRENT_BUILD_ORDER" ] || [ "$CURRENT_BUILD_ORDER" = "null" ]; then
    echo "⚠️  Could not determine current buildOrder from executor.json"
    echo "   Will use all result files (excluding history)"
fi

# Calculate min start and max stop from ONLY current run's result files
# Exclude history files - only use actual test result files
echo "📊 Calculating timestamps from current run's result files..."

python3 <<'PYTHON_SCRIPT'
import json
import os
import sys
from pathlib import Path
from datetime import datetime

results_dir = os.environ.get('RESULTS_DIR', 'allure-results-combined')
report_dir = os.environ.get('REPORT_DIR', 'allure-report-combined')
current_build_order = os.environ.get('CURRENT_BUILD_ORDER', '')

# Find all result files (exclude history and container files)
result_files = list(Path(results_dir).glob("*-result.json"))

if not result_files:
    print("⚠️  No result files found")
    sys.exit(0)

print(f"   Found {len(result_files)} result file(s)")

# Calculate min start and max stop from current run's results only
min_start = None
max_stop = None
valid_timestamps = 0

for result_file in result_files:
    try:
        with open(result_file, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        start = data.get('start')
        stop = data.get('stop')
        
        # Skip if timestamps are missing or invalid
        if not start or not stop or start == 0 or stop == 0:
            continue
        
        # Ensure timestamps are in milliseconds (13+ digits)
        if len(str(start)) < 12:
            start = int(start) * 1000
        if len(str(stop)) < 12:
            stop = int(stop) * 1000
        
        # Update min/max
        if min_start is None or start < min_start:
            min_start = start
        if max_stop is None or stop > max_stop:
            max_stop = stop
        
        valid_timestamps += 1
    except Exception as e:
        continue

if min_start is None or max_stop is None:
    print("⚠️  Could not calculate valid timestamps from result files")
    sys.exit(0)

# Calculate duration
duration = max_stop - min_start

print(f"   Valid timestamps found: {valid_timestamps}")
print(f"   Min start: {min_start} ({datetime.fromtimestamp(min_start / 1000)})")
print(f"   Max stop: {max_stop} ({datetime.fromtimestamp(max_stop / 1000)})")
print(f"   Duration: {duration}ms ({duration / 1000}s)")

# Get reportName from executor.json to preserve it in summary
report_name = "Allure Report"
executor_file = Path(results_dir) / "executor.json"
if executor_file.exists():
    try:
        with open(executor_file, 'r', encoding='utf-8') as f:
            executor_data = json.load(f)
            report_name = executor_data.get('reportName', 'Allure Report')
    except Exception:
        pass

# Update summary.json
summary_file = Path(report_dir) / "widgets" / "summary.json"
if not summary_file.exists():
    print(f"⚠️  Summary file not found: {summary_file}")
    sys.exit(0)

try:
    with open(summary_file, 'r', encoding='utf-8') as f:
        summary = json.load(f)
    
    # Store original values for comparison
    old_start = summary.get('time', {}).get('start')
    old_stop = summary.get('time', {}).get('stop')
    old_duration = summary.get('time', {}).get('duration')
    
    # Update time section
    if 'time' not in summary:
        summary['time'] = {}
    
    summary['time']['start'] = min_start
    summary['time']['stop'] = max_stop
    summary['time']['duration'] = duration
    
    # Write updated summary
    with open(summary_file, 'w', encoding='utf-8') as f:
        json.dump(summary, f, indent=2, ensure_ascii=False)
    
    print(f"")
    print(f"✅ Summary timestamps updated:")
    print(f"   Start: {old_start} -> {min_start}")
    print(f"   Stop: {old_stop} -> {max_stop}")
    print(f"   Duration: {old_duration} -> {duration}")
    
except Exception as e:
    print(f"❌ Error updating summary: {e}")
    sys.exit(1)

PYTHON_SCRIPT

EXIT_CODE=$?

if [ $EXIT_CODE -eq 0 ]; then
    echo ""
    echo "✅ Summary timestamp fix completed"
else
    echo ""
    echo "⚠️  Summary timestamp fix had issues (exit code: $EXIT_CODE)"
    echo "   Report will use Allure's calculated timestamps"
fi
