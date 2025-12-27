#!/bin/bash
# Convert Robot Framework Test Results to Allure Format
# Usage: ./scripts/ci/convert-robot-to-allure.sh <allure-results-dir> <robot-results-dir> [environment]
#
# Arguments:
#   allure-results-dir  - Directory where Allure results should be stored
#   robot-results-dir   - Directory containing Robot Framework test results (output.xml)
#   environment         - Optional environment name (dev, test, prod)

set -e

ALLURE_RESULTS_DIR="${1:-allure-results-combined}"
ROBOT_RESULTS_DIR="${2:-target/robot-reports}"
ENVIRONMENT="${3:-}"

echo "🔄 Converting Robot Framework Test Results to Allure Format..."
echo "   Allure Results: $ALLURE_RESULTS_DIR"
echo "   Robot Results: $ROBOT_RESULTS_DIR"
echo "   Environment: ${ENVIRONMENT:-not specified}"
echo ""

# Ensure Allure results directory exists
mkdir -p "$ALLURE_RESULTS_DIR"

# Check for Robot Framework results
if [ ! -d "$ROBOT_RESULTS_DIR" ]; then
    echo "⚠️  Robot Framework results directory not found: $ROBOT_RESULTS_DIR"
    exit 0
fi

# Look for output.xml file
OUTPUT_XML="$ROBOT_RESULTS_DIR/output.xml"

if [ ! -f "$OUTPUT_XML" ]; then
    echo "ℹ️  No Robot Framework output.xml found"
    echo "   This is expected if Robot Framework tests haven't run"
    exit 0
fi

# Use Python to parse Robot Framework XML
python3 <<PYTHON_SCRIPT
import xml.etree.ElementTree as ET
import os
import sys
import uuid
import hashlib
from datetime import datetime

output_xml = "$OUTPUT_XML"
allure_dir = "$ALLURE_RESULTS_DIR"
env = "$ENVIRONMENT" if "$ENVIRONMENT" else None

try:
    tree = ET.parse(output_xml)
    root = tree.getroot()
    
    # Robot Framework XML structure: <robot> -> <statistics> -> <total> -> <stat>
    # Also has <suite> elements with test cases
    
    # Get statistics
    stats = root.find('statistics/total/stat')
    total = 0
    passed = 0
    failed = 0
    
    if stats is not None:
        total = int(stats.get('pass', 0)) + int(stats.get('fail', 0))
        passed = int(stats.get('pass', 0))
        failed = int(stats.get('fail', 0))
    
    # Also try to count from test elements
    if total == 0:
        tests = root.findall('.//test')
        total = len(tests)
        for test in tests:
            status = test.find('status')
            if status is not None:
                if status.get('status') == 'PASS':
                    passed += 1
                else:
                    failed += 1
    
    print(f"📊 Robot Framework Stats: {total} tests, {passed} passed, {failed} failed")
    
    if total > 0:
        status = "passed" if failed == 0 else "failed"
        
        # Get duration from robot XML (in milliseconds)
        duration = 60000  # Default 1 minute
        suite = root.find('suite')
        if suite is not None:
            suite_status = suite.find('status')
            if suite_status is not None:
                # Robot Framework duration is in format "HH:MM:SS.mmm"
                duration_str = suite_status.get('endtime', '') or suite_status.get('starttime', '')
                # Try to parse if available, otherwise use default
        
        test_uuid = uuid.uuid4().hex[:32]
        timestamp = int(datetime.now().timestamp() * 1000)
        test_name = "Robot Framework Test Suite"
        full_name = f"RobotFramework.{test_name}"
        history_id = hashlib.md5(f"{full_name}:{env or ''}".encode()).hexdigest()
        
        labels = [
            {"name": "suite", "value": "Robot Framework Tests"},
            {"name": "testClass", "value": "RobotFramework"},
            {"name": "epic", "value": "Robot Framework Acceptance Testing"},
            {"name": "feature", "value": "Robot Framework Tests"}
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
            "name": f"{test_name} ({passed} passed, {failed} failed)",
            "status": status,
            "statusDetails": {
                "known": False,
                "muted": False,
                "flaky": False
            },
            "stage": "finished",
            "description": f"Robot Framework Test Suite: {total} tests total, {passed} passed, {failed} failed",
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
        print(f"   Status: {status}, Tests: {total}, Passed: {passed}, Failed: {failed}")
    else:
        print("ℹ️  No tests found in Robot Framework output")
        
except Exception as e:
    print(f"⚠️  Error parsing Robot Framework results: {e}", file=sys.stderr)
    import traceback
    traceback.print_exc()
    sys.exit(0)  # Don't fail the build if conversion fails

PYTHON_SCRIPT

echo ""
echo "✅ Robot Framework to Allure conversion complete!"
echo "   Results saved to: $ALLURE_RESULTS_DIR"

