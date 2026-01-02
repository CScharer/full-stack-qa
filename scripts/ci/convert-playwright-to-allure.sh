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

# Use Python to parse Playwright JUnit XML files (Playwright uses JUnit reporter)
python3 <<PYTHON_SCRIPT
import xml.etree.ElementTree as ET
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

playwright_dir = "$PLAYWRIGHT_RESULTS_DIR"
allure_dir = "$ALLURE_RESULTS_DIR"
env = "$ENVIRONMENT" if "$ENVIRONMENT" else None

converted = 0

# Look for Playwright JUnit XML files (Playwright uses JUnit reporter)
# Playwright generates: test-results/junit.xml or test-results/integration-junit.xml
junit_files = []
if os.path.isdir(playwright_dir):
    # Search for JUnit XML files
    junit_files = list(Path(playwright_dir).rglob("junit.xml"))
    junit_files.extend(list(Path(playwright_dir).rglob("*junit*.xml")))
    # Also check parent directory
    if os.path.isdir(os.path.dirname(playwright_dir)):
        parent_junit = list(Path(os.path.dirname(playwright_dir)).rglob("junit.xml"))
        junit_files.extend(parent_junit)

if not junit_files:
    print("ℹ️  No Playwright JUnit XML files found")
    print("   This is expected if Playwright tests haven't run")
    sys.exit(0)

# Track all attempts across ALL JUnit files to handle retries correctly
# This ensures we deduplicate even if retries are in different JUnit files
test_attempts = {}  # fullName -> list of (test_case, status, attempt_order, junit_file)

for junit_file in junit_files:
    try:
        tree = ET.parse(junit_file)
        root = tree.getroot()
        
        # JUnit XML structure: <testsuites> -> <testsuite> -> <testcase>
        # Count tests
        total = 0
        passed = 0
        failed = 0
        skipped = 0
        total_time = 0
        
        # Handle both <testsuites> and <testsuite> root elements
        if root.tag == 'testsuites':
            test_suites = root.findall('testsuite')
        elif root.tag == 'testsuite':
            test_suites = [root]
        else:
            test_suites = []
        
        # Process individual test cases to create separate Allure results
        # This allows skipped tests to appear in Categories section
        # Track all attempts for each test to handle retries correctly
        # Use a global counter across all files to maintain order
        global_attempt_counter = len(test_attempts) * 1000  # Offset to avoid collisions
        
        for suite in test_suites:
            test_cases = suite.findall('testcase')
            
            for idx, test_case in enumerate(test_cases):
                global_attempt_counter += 1
                test_name = test_case.get('name', 'Unknown Test')
                test_class = test_case.get('classname', 'Playwright')
                full_name = f"{test_class}.{test_name}"
                
                # Determine test status
                # Check for skipped, failure, or error elements
                skipped_elem = test_case.find('skipped')
                failure_elem = test_case.find('failure')
                error_elem = test_case.find('error')
                
                if skipped_elem is not None:
                    status = "skipped"
                elif failure_elem is not None:
                    status = "failed"
                elif error_elem is not None:
                    status = "broken"
                else:
                    status = "passed"
                
                # Track all attempts for this test (across all JUnit files)
                if full_name not in test_attempts:
                    test_attempts[full_name] = []
                test_attempts[full_name].append((test_case, status, global_attempt_counter, junit_file))
        
        # Continue to next JUnit file (don't process yet, collect all attempts first)
        
    except Exception as e:
        print(f"⚠️  Error parsing Playwright JUnit XML from {junit_file}: {e}", file=sys.stderr)
        import traceback
        traceback.print_exc()
        continue

# Now process each test and decide what to keep (after processing ALL JUnit files)
if not test_attempts:
    print("ℹ️  No Playwright test attempts found")
    sys.exit(0)

# IMPORTANT: Playwright's retries: 1 config retries ALL tests, even if they pass
# So we need to be smart about deduplication - only deduplicate actual retries of failed tests
test_results = {}  # fullName -> (test_case, status, is_flaky, retry_info)

for full_name, attempts in test_attempts.items():
    if len(attempts) == 1:
        # Single attempt - no retry, keep as is
        test_case, status, _, _ = attempts[0]
        test_results[full_name] = (test_case, status, False, None)
    else:
        # Multiple attempts - could be retries or duplicate entries
        # Sort by attempt order (idx) to get chronological order
        attempts_sorted = sorted(attempts, key=lambda x: x[2])
        first_attempt = attempts_sorted[0]
        last_attempt = attempts_sorted[-1]
        
        first_test_case, first_status, _, _ = first_attempt
        last_test_case, last_status, _, _ = last_attempt
        
        # Smart deduplication logic:
        # 1. If all attempts have same status (all passed, all skipped, all failed) - keep first (no retry needed)
        # 2. If status changed (failed->passed, skipped->passed, etc.) - keep the best result
        # 3. Prefer passed > failed > broken > skipped (best to worst)
        
        # Check if all attempts have the same status
        all_same_status = all(attempt[1] == first_status for attempt in attempts_sorted)
        
        if all_same_status:
            # All attempts have same status - no actual retry needed, keep first
            test_results[full_name] = (first_test_case, first_status, False, None)
        else:
            # Status changed - this is a real retry, find the best result
            # Priority: passed > failed > broken > skipped
            status_priority = {"passed": 4, "failed": 3, "broken": 2, "skipped": 1}
            best_attempt = max(attempts_sorted, key=lambda x: status_priority.get(x[1], 0))
            best_test_case, best_status, _, _ = best_attempt
            
            # Mark as flaky if status improved (failed->passed, skipped->passed)
            is_flaky = (first_status != "passed" and best_status == "passed")
            retry_info = f"Retried {len(attempts)} times. First: {first_status}, Best: {best_status}"
            test_results[full_name] = (best_test_case, best_status, is_flaky, retry_info)
        
        # Now convert the processed test results
        for full_name, (test_case, final_status, is_flaky, retry_info) in test_results.items():
            test_name = test_case.get('name', 'Unknown Test')
            test_class = test_case.get('classname', 'Playwright')
            test_time = float(test_case.get('time', 0))
            duration = int(test_time * 1000) if test_time > 0 else 1000
            
            # Get status message from the final test case
            # IMPORTANT: Use final_status (from our deduplication logic) instead of re-checking test_case
            # This ensures skipped tests are properly preserved
            skipped_elem = test_case.find('skipped')
            failure_elem = test_case.find('failure')
            error_elem = test_case.find('error')
            
            # Determine status message based on final_status (which we determined from deduplication)
            if final_status == "skipped":
                # Test is skipped - get message from skipped element or use default
                if skipped_elem is not None:
                    status_message = skipped_elem.get('message', 'Test was skipped')
                else:
                    status_message = 'Test was skipped'
            elif final_status == "failed":
                if failure_elem is not None:
                    status_message = failure_elem.get('message', 'Test failed')
                else:
                    status_message = 'Test failed'
            elif final_status == "broken":
                if error_elem is not None:
                    status_message = error_elem.get('message', 'Test error')
                else:
                    status_message = 'Test error'
            else:
                status_message = None
            
            # is_flaky and retry_info are already determined above
            
            # Create unique test result
            test_uuid = uuid.uuid4().hex[:32]
            timestamp = int(datetime.now().timestamp() * 1000)
            history_id = hashlib.md5(f"{full_name}:{env or ''}".encode()).hexdigest()
            
            labels = [
                {"name": "suite", "value": "Playwright Tests"},
                {"name": "testClass", "value": test_class},
                {"name": "epic", "value": "Playwright E2E Testing"},
                {"name": "feature", "value": "Playwright Tests"}
            ]
            
            if env and env not in ["unknown", "combined"]:
                labels.append({"name": "environment", "value": env})
            
            params = []
            if env and env not in ["unknown", "combined"]:
                params.append({"name": "Environment", "value": env.upper()})
                # Add verification metadata using shared utility
                # Use timestamp from Playwright test result (already available)
                params = add_verification_metadata_to_params(params, env, timestamp, "BASE_URL")
                # Override Base URL if PLAYWRIGHT_BASE_URL is available
                base_url = os.environ.get("BASE_URL") or os.environ.get("PLAYWRIGHT_BASE_URL", "unknown")
                if base_url != "unknown":
                    for p in params:
                        if p["name"] == "Base URL":
                            p["value"] = base_url
                            break
            
            status_details = {
                "known": False,
                "muted": False,
                "flaky": is_flaky
            }
            
            # Add retry information to description if test was retried
            description = f"Playwright test: {test_name}"
            if retry_info:
                description += f" ({retry_info})"
                if not status_message:
                    status_message = retry_info
            
            if status_message:
                status_details["message"] = status_message
            
            # CRITICAL: Ensure skipped tests are included with correct status
            # Use final_status which was determined by our deduplication logic
            result = {
                "uuid": test_uuid,
                "historyId": history_id,
                "fullName": full_name,
                "labels": labels,
                "name": test_name,
                "status": final_status,  # Use final_status from deduplication logic
                "statusDetails": status_details,
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
                import json
                json.dump(result, f, indent=2)
            
            converted += 1
        
# Print summary after processing all files
if converted > 0:
    print(f"✅ Created {converted} Playwright test result(s)")
else:
    print("ℹ️  No Playwright results were converted")
    sys.exit(0)

print(f"\n✅ Converted {converted} Playwright test result(s)")
PYTHON_SCRIPT

echo ""
echo "✅ Playwright to Allure conversion complete!"
echo "   Results saved to: $ALLURE_RESULTS_DIR"

