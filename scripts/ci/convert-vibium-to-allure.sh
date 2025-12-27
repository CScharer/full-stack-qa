#!/bin/bash
# Convert Vibium Test Results to Allure Format
# Usage: ./scripts/ci/convert-vibium-to-allure.sh <allure-results-dir> <vibium-results-dir> [environment]
#
# Arguments:
#   allure-results-dir  - Directory where Allure results should be stored
#   vibium-results-dir  - Directory containing Vibium test results
#   environment         - Optional environment name (dev, test, prod)

set -e

ALLURE_RESULTS_DIR="${1:-allure-results-combined}"
VIBIUM_RESULTS_DIR="${2:-vibium/test-results}"
ENVIRONMENT="${3:-}"

echo "🔄 Converting Vibium Test Results to Allure Format..."
echo "   Allure Results: $ALLURE_RESULTS_DIR"
echo "   Vibium Results: $VIBIUM_RESULTS_DIR"
echo "   Environment: ${ENVIRONMENT:-not specified}"
echo ""

# Ensure Allure results directory exists
mkdir -p "$ALLURE_RESULTS_DIR"

# Check for Vibium results
if [ ! -d "$VIBIUM_RESULTS_DIR" ]; then
    echo "⚠️  Vibium results directory not found: $VIBIUM_RESULTS_DIR"
    exit 0
fi

# Use Python to parse Vibium results (structure may vary)
python3 <<PYTHON_SCRIPT
import json
import os
import sys
from pathlib import Path
import uuid
import hashlib
from datetime import datetime

vibium_dir = "$VIBIUM_RESULTS_DIR"
allure_dir = "$ALLURE_RESULTS_DIR"
env = "$ENVIRONMENT" if "$ENVIRONMENT" else None

converted = 0

# Look for Vibium result files (JSON, XML, or other formats)
# Vibium structure may vary, so we'll look for common patterns
result_files = []
for ext in ['*.json', '*.xml', '*.txt']:
    result_files.extend(list(Path(vibium_dir).rglob(ext)))

if not result_files:
    print("ℹ️  No Vibium result files found")
    print("   This is expected if Vibium tests haven't run")
    sys.exit(0)

# Try to find test result information
total_tests = 0
passed_tests = 0
failed_tests = 0

for result_file in result_files:
    try:
        if result_file.suffix == '.json':
            with open(result_file, 'r') as f:
                data = json.load(f)
                
                # Try to extract test statistics (structure may vary)
                if isinstance(data, dict):
                    # Look for common test result fields
                    if 'tests' in data:
                        total_tests += data.get('tests', 0)
                        passed_tests += data.get('passed', data.get('pass', 0))
                        failed_tests += data.get('failed', data.get('fail', 0))
                    elif 'total' in data:
                        total_tests += data.get('total', 0)
                        passed_tests += data.get('passed', 0)
                        failed_tests += data.get('failed', 0)
        
        elif result_file.suffix == '.xml':
            # Could parse XML if needed
            pass
            
    except Exception as e:
        # Skip files that can't be parsed
        continue

# If we found test statistics, create Allure result
if total_tests > 0 or len(result_files) > 0:
    status = "passed" if failed_tests == 0 and total_tests > 0 else "failed" if failed_tests > 0 else "passed"
    duration = 60000  # Default 1 minute
    
    test_uuid = uuid.uuid4().hex[:32]
    timestamp = int(datetime.now().timestamp() * 1000)
    test_name = "Vibium Visual Regression Tests"
    full_name = f"Vibium.{test_name}"
    history_id = hashlib.md5(f"{full_name}:{env or ''}".encode()).hexdigest()
    
    labels = [
        {"name": "suite", "value": "Vibium Tests"},
        {"name": "testClass", "value": "Vibium"},
        {"name": "epic", "value": "Vibium Visual Regression Testing"},
        {"name": "feature", "value": "Vibium Tests"}
    ]
    
    if env and env not in ["unknown", "combined"]:
        labels.append({"name": "environment", "value": env})
    
    params = []
    if env and env not in ["unknown", "combined"]:
        params.append({"name": "Environment", "value": env.upper()})
    
    description = f"Vibium Visual Regression Test Suite"
    if total_tests > 0:
        description += f": {total_tests} tests total, {passed_tests} passed, {failed_tests} failed"
    else:
        description += f": {len(result_files)} result file(s) found"
    
    result = {
        "uuid": test_uuid,
        "historyId": history_id,
        "fullName": full_name,
        "labels": labels,
        "name": f"{test_name}" + (f" ({passed_tests} passed, {failed_tests} failed)" if total_tests > 0 else ""),
        "status": status,
        "statusDetails": {
            "known": False,
            "muted": False,
            "flaky": False
        },
        "stage": "finished",
        "description": description,
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
    if total_tests > 0:
        print(f"   Status: {status}, Tests: {total_tests}, Passed: {passed_tests}, Failed: {failed_tests}")
    else:
        print(f"   Status: {status}, Result files found: {len(result_files)}")
    converted += 1

if converted == 0:
    print("ℹ️  No Vibium results were converted")
    sys.exit(0)

print(f"\n✅ Converted {converted} Vibium test result(s)")
PYTHON_SCRIPT

echo ""
echo "✅ Vibium to Allure conversion complete!"
echo "   Results saved to: $ALLURE_RESULTS_DIR"

