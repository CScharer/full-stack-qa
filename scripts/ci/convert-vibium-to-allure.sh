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

# Look for Vitest JSON result file (vitest-results.json)
# Vitest outputs JSON to test-results/vitest-results.json when configured with JSON reporter
vitest_json = Path(vibium_dir) / "test-results" / "vitest-results.json"
if not vitest_json.exists():
    # Try alternative locations
    vitest_json = Path(vibium_dir) / "vitest-results.json"
    if not vitest_json.exists():
        # Look for any JSON file in test-results
        test_results_dir = Path(vibium_dir) / "test-results"
        if test_results_dir.exists():
            json_files = list(test_results_dir.glob("*.json"))
            if json_files:
                vitest_json = json_files[0]
            else:
                print("ℹ️  No Vitest JSON result file found")
                print("   Expected: test-results/vitest-results.json")
                print("   This is expected if Vibium tests haven't run or JSON reporter isn't configured")
                sys.exit(0)
        else:
            print("ℹ️  No Vitest result files found")
            print("   This is expected if Vibium tests haven't run")
            sys.exit(0)

# Parse Vitest JSON output
total_tests = 0
passed_tests = 0
failed_tests = 0
test_results = []

try:
    with open(vitest_json, 'r') as f:
        data = json.load(f)
        
        # Vitest JSON format: { numTotalTests, numPassedTests, numFailedTests, testResults: [...] }
        if isinstance(data, dict):
            total_tests = data.get('numTotalTests', 0)
            passed_tests = data.get('numPassedTests', 0)
            failed_tests = data.get('numFailedTests', 0)
            test_results = data.get('testResults', [])
            
            print(f"📊 Found Vitest results: {total_tests} tests ({passed_tests} passed, {failed_tests} failed)")
            
except Exception as e:
    print(f"⚠️  Error parsing Vitest JSON: {e}")
    sys.exit(0)

# Create Allure results for each test suite/file
if total_tests > 0 and test_results:
    # Process each test file/suite from Vitest results
    for test_suite in test_results:
        suite_name = test_suite.get('name', 'Unknown Test Suite')
        suite_status = test_suite.get('status', 'unknown')
        suite_duration = test_suite.get('duration', 0)
        
        # Determine overall status for this suite
        suite_num_passed = test_suite.get('numPassingTests', 0)
        suite_num_failed = test_suite.get('numFailingTests', 0)
        suite_num_total = test_suite.get('numPassingTests', 0) + test_suite.get('numFailingTests', 0)
        
        if suite_num_failed > 0:
            status = "failed"
        elif suite_num_passed > 0:
            status = "passed"
        else:
            status = "skipped"
        
        # Create Allure result for this test suite
        test_uuid = uuid.uuid4().hex[:32]
        timestamp = int(datetime.now().timestamp() * 1000)
        test_name = suite_name.replace('.spec.ts', '').replace('tests/', '').replace('/', ' › ')
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
        
        description = f"Vibium Visual Regression Test Suite: {test_name}"
        if suite_num_total > 0:
            description += f" - {suite_num_total} tests ({suite_num_passed} passed, {suite_num_failed} failed)"
        
        duration_ms = int(suite_duration * 1000) if suite_duration > 0 else 1000
        
        result = {
            "uuid": test_uuid,
            "historyId": history_id,
            "fullName": full_name,
            "labels": labels,
            "name": test_name + (f" ({suite_num_passed} passed, {suite_num_failed} failed)" if suite_num_total > 0 else ""),
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
            "stop": timestamp + duration_ms
        }
        
        output_file = os.path.join(allure_dir, f"{test_uuid}-result.json")
        with open(output_file, 'w') as f:
            json.dump(result, f, indent=2)
        
        converted += 1
    
    print(f"✅ Created {converted} Allure result(s) from Vitest output")
    print(f"   Total: {total_tests} tests ({passed_tests} passed, {failed_tests} failed)")

if converted == 0:
    print("ℹ️  No Vibium results were converted")
    sys.exit(0)

print(f"\n✅ Converted {converted} Vibium test result(s)")
PYTHON_SCRIPT

echo ""
echo "✅ Vibium to Allure conversion complete!"
echo "   Results saved to: $ALLURE_RESULTS_DIR"

