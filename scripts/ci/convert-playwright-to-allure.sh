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
        
        for suite in test_suites:
            suite_total = int(suite.get('tests', 0))
            suite_failures = int(suite.get('failures', 0))
            suite_errors = int(suite.get('errors', 0))
            suite_skipped = int(suite.get('skipped', 0))
            suite_time = float(suite.get('time', 0))
            
            total += suite_total
            failed += suite_failures + suite_errors
            skipped += suite_skipped
            total_time += suite_time
            
            # Count passed tests (total - failed - skipped)
            passed += suite_total - suite_failures - suite_errors - suite_skipped
        
        print(f"📊 Playwright Stats: {total} tests, {passed} passed, {failed} failed, {skipped} skipped")
        
        if total > 0:
            status = "passed" if failed == 0 else "failed"
            duration = int(total_time * 1000) if total_time > 0 else 60000  # Convert to milliseconds
            
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
                "name": f"{test_name} ({passed} passed, {failed} failed, {skipped} skipped)",
                "status": status,
                "statusDetails": {
                    "known": False,
                    "muted": False,
                    "flaky": False
                },
                "stage": "finished",
                "description": f"Playwright Test Suite: {total} tests total, {passed} passed, {failed} failed, {skipped} skipped",
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
            
            print(f"✅ Created Allure result: {output_file}")
            print(f"   Status: {status}, Tests: {total}, Passed: {passed}, Failed: {failed}, Skipped: {skipped}")
            converted += 1
        
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

