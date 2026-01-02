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

# Import shared metadata utilities
script_dir = os.path.join(os.path.dirname(os.path.abspath(sys.argv[0])), 'ci') if len(sys.argv) > 0 else os.path.join(os.getcwd(), 'scripts', 'ci')
if not os.path.exists(os.path.join(script_dir, 'allure_metadata_utils.py')):
    script_dir = os.path.join(os.getcwd(), 'scripts', 'ci')
sys.path.insert(0, script_dir)
try:
    from allure_metadata_utils import add_verification_metadata_to_params
except (ImportError, SystemError):
    # Fallback: define function inline if import fails
    def add_verification_metadata_to_params(params, env=None, test_timestamp=None, base_url_env_var="BASE_URL"):
        if not env or env in ["unknown", "combined"]:
            return params
        params.append({"name": "Base URL", "value": os.environ.get(base_url_env_var, "unknown")})
        if test_timestamp and test_timestamp > 0:
            test_timestamp_iso = datetime.fromtimestamp(test_timestamp / 1000).isoformat()
        else:
            test_timestamp_iso = datetime.now().isoformat()
        params.append({"name": "Test Execution Time", "value": test_timestamp_iso})
        params.append({"name": "CI Run ID", "value": os.environ.get("GITHUB_RUN_ID", "local")})
        params.append({"name": "CI Run Number", "value": os.environ.get("GITHUB_RUN_NUMBER", "unknown")})
        return params

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
        suite_duration = test_suite.get('duration', 0)
        
        # Get individual test cases from the suite
        test_cases = test_suite.get('assertionResults', [])
        
        # If no assertionResults, try to get from tasks
        if not test_cases:
            tasks = test_suite.get('tasks', [])
            for task in tasks:
                task_cases = task.get('assertionResults', [])
                test_cases.extend(task_cases)
        
        # If still no test cases, create one result for the suite
        if not test_cases:
            suite_num_passed = test_suite.get('numPassingTests', 0)
            suite_num_failed = test_suite.get('numFailingTests', 0)
            suite_num_skipped = test_suite.get('numSkippedTests', 0)
            suite_num_total = suite_num_passed + suite_num_failed + suite_num_skipped
            
            # Determine status based on suite-level stats
            if suite_num_failed > 0:
                status = "failed"
            elif suite_num_passed > 0:
                status = "passed"
            elif suite_num_skipped > 0:
                status = "skipped"
            else:
                status = "passed"  # Default to passed
            
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
                # Add verification metadata using shared utility
                # Use suite execution time if available (timestamp is already calculated)
                params = add_verification_metadata_to_params(params, env, timestamp, "BASE_URL")
                # Override Base URL if VIBIUM_BASE_URL is available
                base_url = os.environ.get("BASE_URL") or os.environ.get("VIBIUM_BASE_URL", "unknown")
                if base_url != "unknown":
                    for p in params:
                        if p["name"] == "Base URL":
                            p["value"] = base_url
                            break
            
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
        else:
            # Create individual Allure results for each test case
            print(f"📊 Found {len(test_cases)} individual test(s) in suite: {suite_name}")
            
            for test_case in test_cases:
                test_title = test_case.get('title', 'Unknown Test')
                test_status = test_case.get('status', 'unknown')
                test_duration = test_case.get('duration', 0) or 0.001  # seconds, default to 1ms
                
                # Map Vitest status to Allure status
                if test_status == 'passed':
                    status = "passed"
                elif test_status == 'failed':
                    status = "failed"
                elif test_status in ['skipped', 'pending', 'todo']:
                    status = "skipped"
                else:
                    status = "passed"  # Default to passed
                
                test_uuid = uuid.uuid4().hex[:32]
                timestamp = int(datetime.now().timestamp() * 1000)
                full_name = f"Vibium.{suite_name}.{test_title}"
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
                    # Add verification metadata using shared utility
                    # Use test execution time from Vibium results (timestamp is already calculated)
                    params = add_verification_metadata_to_params(params, env, timestamp, "BASE_URL")
                    # Override Base URL if VIBIUM_BASE_URL is available
                    base_url = os.environ.get("BASE_URL") or os.environ.get("VIBIUM_BASE_URL", "unknown")
                    if base_url != "unknown":
                        for p in params:
                            if p["name"] == "Base URL":
                                p["value"] = base_url
                                break
                
                result = {
                    "uuid": test_uuid,
                    "historyId": history_id,
                    "fullName": full_name,
                    "labels": labels,
                    "name": test_title,
                    "status": status,
                    "statusDetails": {
                        "known": False,
                        "muted": False,
                        "flaky": False
                    },
                    "stage": "finished",
                    "description": f"Vibium test: {test_title}",
                    "steps": [],
                    "attachments": [],
                    "parameters": params,
                    "start": timestamp,
                    "stop": timestamp + int(test_duration * 1000)
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

