#!/bin/bash
# Convert Playwright Test Results to Allure Format
# Usage: ./scripts/ci/convert-playwright-to-allure.sh <allure-results-dir> <playwright-results-dir> [environment]
#
# Arguments:
#   allure-results-dir    - Directory where Allure results should be stored
#   playwright-results-dir - Directory containing Playwright test results
#   environment           - Optional environment name (dev, test, prod)

set -e

ALLURE_RESULTS_DIR="${1:-allure-results-combined}"
PLAYWRIGHT_RESULTS_DIR="${2:-playwright/test-results}"
ENVIRONMENT="${3:-}"

echo "🔄 Converting Playwright Test Results to Allure Format..."
echo "   Allure Results: $ALLURE_RESULTS_DIR"
echo "   Playwright Results: $PLAYWRIGHT_RESULTS_DIR"
echo "   Environment: ${ENVIRONMENT:-not specified}"
echo ""

# Ensure Allure results directory exists
mkdir -p "$ALLURE_RESULTS_DIR"

# Check for Playwright results
if [ ! -d "$PLAYWRIGHT_RESULTS_DIR" ]; then
    echo "⚠️  Playwright results directory not found: $PLAYWRIGHT_RESULTS_DIR"
    exit 0
fi

# Use Python to parse Playwright results.json files
python3 <<PYTHON_SCRIPT
import json
import os
import sys
from pathlib import Path
import uuid
import hashlib
from datetime import datetime

playwright_dir = "$PLAYWRIGHT_RESULTS_DIR"
allure_dir = "$ALLURE_RESULTS_DIR"
env = "$ENVIRONMENT" if "$ENVIRONMENT" else None

converted = 0

# Look for Playwright results.json files
results_files = list(Path(playwright_dir).rglob("results.json"))

if not results_files:
    print("ℹ️  No Playwright results.json files found")
    print("   This is expected if Playwright tests haven't run")
    sys.exit(0)

for json_file in results_files:
    try:
        with open(json_file, 'r') as f:
            data = json.load(f)
        
        # Playwright results structure: { "stats": { "total": N, "expected": M, ... }, "suites": [...] }
        if 'stats' in data:
            stats = data['stats']
            total = stats.get('total', 0)
            expected = stats.get('expected', 0)  # Passed tests
            failures = total - expected
            
            print(f"📊 Playwright Stats: {total} tests, {expected} passed, {failures} failed")
            
            if total > 0:
                status = "passed" if failures == 0 else "failed"
                duration = stats.get('duration', 0) or 60000  # Default 1 minute
                
                test_uuid = uuid.uuid4().hex[:32]
                timestamp = int(datetime.now().timestamp() * 1000)
                test_name = "Playwright Test Suite"
                full_name = f"Playwright.{test_name}"
                history_id = hashlib.md5(f"{full_name}:{env or ''}".encode()).hexdigest()
                
                labels = [
                    {"name": "suite", "value": "Playwright Tests"},
                    {"name": "testClass", "value": "Playwright"},
                    {"name": "epic", "value": "Playwright E2E Testing"},
                    {"name": "feature", "value": "Playwright Tests"}
                ]
                
                if env and env not in ["unknown", "combined"]:
                    labels.append({"name": "environment", "value": env})
                
                params = []
                if env and env not in ["unknown", "combined"]:
                    params.append({"name": "Environment", "value": env.upper()})
                
                result = {
                    "uuid": test_uuid,
                    "historyId": history_id,
                    "fullName": full_name,
                    "labels": labels,
                    "name": f"{test_name} ({expected} passed, {failures} failed)",
                    "status": status,
                    "statusDetails": {
                        "known": False,
                        "muted": False,
                        "flaky": False
                    },
                    "stage": "finished",
                    "description": f"Playwright Test Suite: {total} tests total, {expected} passed, {failures} failed",
                    "steps": [],
                    "attachments": [],
                    "parameters": params,
                    "start": timestamp,
                    "stop": timestamp + duration
                }
                
                output_file = os.path.join(allure_dir, f"{test_uuid}-result.json")
                with open(output_file, 'w') as f:
                    json.dump(result, f, indent=2)
                
                print(f"✅ Created Allure result: {output_file}")
                print(f"   Status: {status}, Tests: {total}, Passed: {expected}, Failed: {failures}")
                converted += 1
        
        # Also process individual test results from suites if available
        if 'suites' in data:
            suites = data.get('suites', [])
            print(f"📊 Found {len(suites)} test suites")
            # Could process individual tests here if needed
        
    except Exception as e:
        print(f"⚠️  Error parsing Playwright results from {json_file}: {e}", file=sys.stderr)
        continue

if converted == 0:
    print("ℹ️  No Playwright results were converted")
    sys.exit(0)

print(f"\n✅ Converted {converted} Playwright test result(s)")
PYTHON_SCRIPT

echo ""
echo "✅ Playwright to Allure conversion complete!"
echo "   Results saved to: $ALLURE_RESULTS_DIR"

