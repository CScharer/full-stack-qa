#!/bin/bash
# Deduplicate TestNG Retry Attempts
# Usage: ./scripts/ci/deduplicate-testng-retries.sh <results-dir>
#
# Arguments:
#   results-dir  - Directory where Allure results are stored (default: allure-results-combined)
#
# This script deduplicates TestNG retry attempts by:
# 1. Finding all result files with the same fullName and historyId
# 2. Keeping only the best result (passed > failed > broken > skipped)
# 3. Removing duplicate retry attempts

set -e

RESULTS_DIR="${1:-allure-results-combined}"

if [ ! -d "$RESULTS_DIR" ]; then
    echo "⚠️  Results directory not found: $RESULTS_DIR"
    exit 0
fi

echo "🔄 Deduplicating TestNG retry attempts..."
echo "   Results directory: $RESULTS_DIR"
echo ""

# Count files before processing
BEFORE_COUNT=$(find "$RESULTS_DIR" -name "*-result.json" 2>/dev/null | wc -l | tr -d ' ')
echo "📊 Found $BEFORE_COUNT result files to process"

python3 <<'PYTHON_SCRIPT'
import json
import os
import sys
from pathlib import Path
from collections import defaultdict

results_dir = os.environ.get('RESULTS_DIR', 'allure-results-combined')

if not os.path.isdir(results_dir):
    print(f"⚠️  Results directory not found: {results_dir}")
    sys.exit(0)

# Find all result files
result_files = list(Path(results_dir).glob("*-result.json"))
print(f"📊 Processing {len(result_files)} result files...")

# Group results by fullName and historyId (same test, same environment)
# TestNG retries create multiple result files with same fullName+historyId
test_groups = defaultdict(list)

for result_file in result_files:
    try:
        with open(result_file, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        # Get fullName and historyId to identify duplicates
        full_name = data.get('fullName', '')
        history_id = data.get('historyId', '')
        status = data.get('status', 'unknown')
        
        # Skip if missing critical fields
        if not full_name or not history_id:
            continue
        
        # Create a key from fullName and historyId
        # Same test with same historyId = retry attempts
        key = f"{full_name}:{history_id}"
        test_groups[key].append((result_file, data, status))
    
    except Exception as e:
        print(f"⚠️  Error reading {result_file}: {e}", file=sys.stderr)
        continue

# Process each group and deduplicate
removed_count = 0
kept_count = 0

# Status priority: passed > failed > broken > skipped
status_priority = {"passed": 4, "failed": 3, "broken": 2, "skipped": 1, "unknown": 0}

for key, results in test_groups.items():
    if len(results) == 1:
        # Single result, no duplicates - keep it
        kept_count += 1
        continue
    
    # Multiple results with same fullName+historyId = retry attempts
    # Find the best result (highest priority status)
    best_result = max(results, key=lambda x: status_priority.get(x[2], 0))
    best_file, best_data, best_status = best_result
    
    # Keep the best result
    kept_count += 1
    
    # Remove all other duplicates
    for result_file, data, status in results:
        if result_file != best_file:
            try:
                result_file.unlink()
                removed_count += 1
            except Exception as e:
                print(f"⚠️  Error removing {result_file}: {e}", file=sys.stderr)

print(f"\n✅ Deduplication complete!")
print(f"   Kept: {kept_count} unique test(s)")
print(f"   Removed: {removed_count} duplicate retry attempt(s)")
print(f"   Total groups processed: {len(test_groups)}")

# Count files after processing
after_count = len(list(Path(results_dir).glob("*-result.json")))
print(f"   Files before: {len(result_files)}")
print(f"   Files after: {after_count}")
PYTHON_SCRIPT

echo ""
echo "✅ TestNG retry deduplication complete!"

