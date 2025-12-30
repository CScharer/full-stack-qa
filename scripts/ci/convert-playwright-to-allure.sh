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
        test_attempts = {}  # fullName -> list of (test_case, status, attempt_order)
        
        for suite in test_suites:
            test_cases = suite.findall('testcase')
            
            for idx, test_case in enumerate(test_cases):
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
                
                # Track all attempts for this test
                if full_name not in test_attempts:
                    test_attempts[full_name] = []
                test_attempts[full_name].append((test_case, status, idx))
        
        # Now process each test and decide what to keep
        # IMPORTANT: Playwright's retries: 1 config retries ALL tests, even if they pass
        # So we need to be smart about deduplication - only deduplicate actual retries of failed tests
        test_results = {}  # fullName -> (test_case, status, is_flaky, retry_info)
        
        for full_name, attempts in test_attempts.items():
            if len(attempts) == 1:
                # Single attempt - no retry, keep as is
                test_case, status, _ = attempts[0]
                test_results[full_name] = (test_case, status, False, None)
            else:
                # Multiple attempts - could be retries or duplicate entries
                # Sort by attempt order (idx) to get chronological order
                attempts_sorted = sorted(attempts, key=lambda x: x[2])
                first_attempt = attempts_sorted[0]
                last_attempt = attempts_sorted[-1]
                
                first_test_case, first_status, _ = first_attempt
                last_test_case, last_status, _ = last_attempt
                
                # Only deduplicate if test actually failed and was retried
                # If test passed on first attempt, keep it (even if there are duplicates from retry config)
                # This ensures all passed tests are shown
                if first_status == "passed":
                    # Test passed on first attempt - keep it (don't deduplicate)
                    # This handles Playwright's retries: 1 config that retries all tests
                    test_results[full_name] = (first_test_case, first_status, False, None)
                elif first_status != "passed" and last_status == "passed":
                    # Test failed initially but passed on retry - keep the final passed result
                    # Mark as flaky since it failed then passed
                    is_flaky = True
                    retry_info = f"Retried {len(attempts)} times. First: {first_status}, Final: {last_status}"
                    test_results[full_name] = (last_test_case, last_status, is_flaky, retry_info)
                else:
                    # Test failed on all attempts - keep the final result
                    is_flaky = False
                    retry_info = f"Retried {len(attempts)} times. All attempts: {first_status}"
                    test_results[full_name] = (last_test_case, last_status, is_flaky, retry_info)
        
        # Now convert the processed test results
        for full_name, (test_case, final_status, is_flaky, retry_info) in test_results.items():
            test_name = test_case.get('name', 'Unknown Test')
            test_class = test_case.get('classname', 'Playwright')
            test_time = float(test_case.get('time', 0))
            duration = int(test_time * 1000) if test_time > 0 else 1000
            
            # Get status message from the final test case
            skipped_elem = test_case.find('skipped')
            failure_elem = test_case.find('failure')
            error_elem = test_case.find('error')
            
            if skipped_elem is not None:
                status_message = skipped_elem.get('message', 'Test was skipped')
            elif failure_elem is not None:
                status_message = failure_elem.get('message', 'Test failed')
            elif error_elem is not None:
                status_message = error_elem.get('message', 'Test error')
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
            
            result = {
                "uuid": test_uuid,
                "historyId": history_id,
                "fullName": full_name,
                "labels": labels,
                "name": test_name,
                "status": final_status,
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
        
        # Print summary
        if 'test_cases' in locals() and test_cases:
            total = len(test_cases)
            passed = sum(1 for tc in test_cases if tc.find('skipped') is None and tc.find('failure') is None and tc.find('error') is None)
            failed = sum(1 for tc in test_cases if tc.find('failure') is not None)
            skipped = sum(1 for tc in test_cases if tc.find('skipped') is not None)
            print(f"✅ Created {converted} Playwright test result(s)")
            print(f"   Stats: {total} tests, {passed} passed, {failed} failed, {skipped} skipped")
        else:
            print(f"✅ Created {converted} Playwright test result(s)")
        
    except Exception as e:
        print(f"⚠️  Error parsing Playwright JUnit XML from {junit_file}: {e}", file=sys.stderr)
        import traceback
        traceback.print_exc()
        continue

if converted == 0:
    print("ℹ️  No Playwright results were converted")
    sys.exit(0)

print(f"\n✅ Converted {converted} Playwright test result(s)")
PYTHON_SCRIPT

echo ""
echo "✅ Playwright to Allure conversion complete!"
echo "   Results saved to: $ALLURE_RESULTS_DIR"

