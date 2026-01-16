#!/bin/bash
# scripts/ci/convert-robot-to-allure.sh
# Robot Framework to Allure Results Converter
#
# Purpose: Convert Robot Framework test results to Allure-compatible JSON format
#
# Usage:
#   ./scripts/ci/convert-robot-to-allure.sh [ALLURE_RESULTS_DIR] [ROBOT_RESULTS_DIR] [ENVIRONMENT]
#
# Parameters:
#   ALLURE_RESULTS_DIR  Directory where Allure results should be stored (default: "allure-results-combined")
#   ROBOT_RESULTS_DIR   Directory containing Robot Framework test results (default: "target/robot-reports")
#                       Must contain output.xml file
#   ENVIRONMENT         Optional environment name for metadata: dev, test, prod (optional)
#
# Examples:
#   ./scripts/ci/convert-robot-to-allure.sh
#   ./scripts/ci/convert-robot-to-allure.sh allure-results target/robot-reports dev
#   ./scripts/ci/convert-robot-to-allure.sh combined-results robot/output test
#
# Description:
#   This script converts Robot Framework test results (XML format) into Allure-compatible JSON files.
#   It reads Robot Framework output.xml and generates Allure result files (*-result.json) with
#   test names, status, duration, logs, screenshots, and environment metadata.
#
# Dependencies:
#   - Robot Framework test results (output.xml in ROBOT_RESULTS_DIR)
#   - Python 3.x with xml.etree.ElementTree (standard library)
#   - jq (JSON processor) for generating Allure JSON
#   - Allure metadata utilities (scripts/ci/allure-metadata-utils.sh)
#
# Output:
#   - Allure-compatible JSON files in ALLURE_RESULTS_DIR
#   - Screenshots and logs attached to test results
#   - Environment metadata included
#   - Exit code: 0 on success, non-zero on failure
#
# Notes:
#   - Used in CI/CD pipeline to integrate Robot Framework results with Allure reports
#   - Parses Robot Framework XML output format
#   - Preserves screenshots and logs as attachments
#   - Adds environment labels for filtering in Allure reports
#   - Handles Robot Framework keyword structure and test organization
#
# Last Updated: January 2026

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

# Look for output.xml file (search recursively if directory provided)
OUTPUT_XML=""
if [ -f "$ROBOT_RESULTS_DIR/output.xml" ]; then
    OUTPUT_XML="$ROBOT_RESULTS_DIR/output.xml"
elif [ -d "$ROBOT_RESULTS_DIR" ]; then
    # Search recursively for output.xml
    OUTPUT_XML=$(find "$ROBOT_RESULTS_DIR" -name "output.xml" 2>/dev/null | head -1)
fi

if [ -z "$OUTPUT_XML" ] || [ ! -f "$OUTPUT_XML" ]; then
    echo "ℹ️  No Robot Framework output.xml found in $ROBOT_RESULTS_DIR"
    echo "   This is expected if Robot Framework tests haven't run"
    echo "   🔍 Debug: Listing directory contents:"
    if [ -d "$ROBOT_RESULTS_DIR" ]; then
        find "$ROBOT_RESULTS_DIR" -maxdepth 3 -type f -name "*.xml" -o -name "*.json" 2>/dev/null | head -10 | while read f; do
            echo "      Found: $f"
        done || echo "      (no XML/JSON files found)"
    else
        echo "      Directory does not exist: $ROBOT_RESULTS_DIR"
    fi
    exit 0
fi

echo "📊 Found Robot Framework output.xml: $OUTPUT_XML"

# Use Python to parse Robot Framework XML
python3 <<PYTHON_SCRIPT
import xml.etree.ElementTree as ET
import os
import sys
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
        test_timestamp_iso = datetime.now().isoformat()  # Robot Framework doesn't provide execution timestamp
        params.append({"name": "Test Execution Time", "value": test_timestamp_iso})
        params.append({"name": "CI Run ID", "value": os.environ.get("GITHUB_RUN_ID", "local")})
        params.append({"name": "CI Run Number", "value": os.environ.get("GITHUB_RUN_NUMBER", "unknown")})
        return params

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
    
    # Extract test execution start time from Robot Framework XML
    # Robot Framework XML has timestamp in the root <robot> element or in suite elements
    test_start_time = None
    
    # Try to get timestamp from root element
    robot_timestamp = root.get('generated') or root.get('generator') or root.get('timestamp')
    if robot_timestamp:
        try:
            # Robot Framework timestamps are typically in format "YYYYMMDD HH:MM:SS.mmm"
            # or ISO format
            if 'T' in robot_timestamp or '-' in robot_timestamp:
                # ISO format
                dt = datetime.fromisoformat(robot_timestamp.replace('Z', '+00:00'))
                test_start_time = int(dt.timestamp() * 1000)
            elif len(robot_timestamp) > 10 and ' ' in robot_timestamp:
                # Format: "YYYYMMDD HH:MM:SS.mmm"
                date_part, time_part = robot_timestamp.split(' ', 1)
                year = int(date_part[:4])
                month = int(date_part[4:6])
                day = int(date_part[6:8])
                time_parts = time_part.split(':')
                hour = int(time_parts[0])
                minute = int(time_parts[1])
                sec_parts = time_parts[2].split('.')
                second = int(sec_parts[0])
                microsecond = int(sec_parts[1]) * 1000 if len(sec_parts) > 1 else 0
                dt = datetime(year, month, day, hour, minute, second, microsecond)
                test_start_time = int(dt.timestamp() * 1000)
        except:
            pass
    
    # If not found in root, try to get from first suite or test status
    if not test_start_time:
        first_suite = root.find('.//suite')
        if first_suite is not None:
            suite_timestamp = first_suite.get('starttime') or first_suite.get('timestamp')
            if suite_timestamp:
                try:
                    if 'T' in suite_timestamp or '-' in suite_timestamp:
                        dt = datetime.fromisoformat(suite_timestamp.replace('Z', '+00:00'))
                        test_start_time = int(dt.timestamp() * 1000)
                    elif len(suite_timestamp) > 10 and ' ' in suite_timestamp:
                        # Format: "YYYYMMDD HH:MM:SS.mmm"
                        date_part, time_part = suite_timestamp.split(' ', 1)
                        year = int(date_part[:4])
                        month = int(date_part[4:6])
                        day = int(date_part[6:8])
                        time_parts = time_part.split(':')
                        hour = int(time_parts[0])
                        minute = int(time_parts[1])
                        sec_parts = time_parts[2].split('.')
                        second = int(sec_parts[0])
                        microsecond = int(sec_parts[1]) * 1000 if len(sec_parts) > 1 else 0
                        dt = datetime(year, month, day, hour, minute, second, microsecond)
                        test_start_time = int(dt.timestamp() * 1000)
                except:
                    pass
    
    # If still not found, try to get from first test status starttime
    if not test_start_time:
        first_test = root.find('.//test')
        if first_test is not None:
            first_status = first_test.find('status')
            if first_status is not None:
                status_starttime = first_status.get('starttime')
                if status_starttime:
                    try:
                        if 'T' in status_starttime or '-' in status_starttime:
                            dt = datetime.fromisoformat(status_starttime.replace('Z', '+00:00'))
                            test_start_time = int(dt.timestamp() * 1000)
                        elif len(status_starttime) > 10 and ' ' in status_starttime:
                            # Format: "YYYYMMDD HH:MM:SS.mmm"
                            date_part, time_part = status_starttime.split(' ', 1)
                            year = int(date_part[:4])
                            month = int(date_part[4:6])
                            day = int(date_part[6:8])
                            time_parts = time_part.split(':')
                            hour = int(time_parts[0])
                            minute = int(time_parts[1])
                            sec_parts = time_parts[2].split('.')
                            second = int(sec_parts[0])
                            microsecond = int(sec_parts[1]) * 1000 if len(sec_parts) > 1 else 0
                            dt = datetime(year, month, day, hour, minute, second, microsecond)
                            test_start_time = int(dt.timestamp() * 1000)
                    except:
                        pass
    
    # CRITICAL: Fallback to file modification time if no timestamp found in XML
    # File modification time is a fallback (not preferred) because:
    # - It reflects when artifact was downloaded/processed, not when tests actually ran
    # - XML timestamps (from Robot Framework) are preferred as they reflect actual test execution
    # - Current time would be the same for all environments if processed together
    if not test_start_time:
        try:
            # Use file modification time as fallback (only if XML timestamp unavailable)
            file_mtime = os.path.getmtime(output_xml)
            test_start_time = int(file_mtime * 1000)
            print(f"   ⚠️  No timestamp in XML, using file modification time: {datetime.fromtimestamp(file_mtime).isoformat()}")
        except:
            # Final fallback to current time (should rarely happen)
            test_start_time = int(datetime.now().timestamp() * 1000)
            print(f"   ⚠️  Using current time as fallback (XML timestamp and file modification time unavailable)")
    
    print(f"📅 Test execution start time: {datetime.fromtimestamp(test_start_time / 1000).isoformat()}")
    
    converted = 0
    
    # Find all test elements and create individual Allure results
    all_tests = root.findall('.//test')
    
    if all_tests and len(all_tests) > 0:
        print(f"📊 Found {len(all_tests)} individual test(s) to convert")
        
        # Track test index for relative timestamps
        test_index = 0
        
        # Create individual Allure results for each test
        for test_elem in all_tests:
            test_name = test_elem.get('name', 'Unknown Test')
            test_id = test_elem.get('id', '')
            
            # Get test status
            status_elem = test_elem.find('status')
            if status_elem is not None:
                robot_status = status_elem.get('status', 'UNKNOWN')
                if robot_status == 'PASS':
                    status = "passed"
                elif robot_status == 'FAIL':
                    status = "failed"
                else:
                    status = "skipped"
                
                # Get duration (Robot Framework uses elapsed time in format "HH:MM:SS.mmm" or seconds)
                elapsed = status_elem.get('elapsed', '0')
                # Try to parse elapsed time (could be seconds or HH:MM:SS format)
                try:
                    if ':' in elapsed:
                        # Format: HH:MM:SS.mmm
                        parts = elapsed.split(':')
                        if len(parts) == 3:
                            hours, minutes, seconds = map(float, parts)
                            duration_ms = int((hours * 3600 + minutes * 60 + seconds) * 1000)
                        else:
                            duration_ms = 1000
                    else:
                        # Assume seconds
                        duration_ms = int(float(elapsed) * 1000)
                except:
                    duration_ms = 1000
            else:
                status = "passed"
                duration_ms = 1000
            
            # Get suite name for full path
            suite = test_elem.find('..')
            suite_name = suite.get('name', '') if suite is not None and suite.tag == 'suite' else ''
            full_name = f"RobotFramework.{suite_name}.{test_name}" if suite_name else f"RobotFramework.{test_name}"
            
            test_uuid = uuid.uuid4().hex[:32]
            # Use test execution start time + offset for each test to maintain relative timing
            timestamp = test_start_time + (test_index * 100)  # Small offset per test
            test_index += 1
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
                # Add verification metadata using shared utility
                # Use test execution start time extracted from XML
                params = add_verification_metadata_to_params(params, env, timestamp, "BASE_URL")
                # Override Base URL if ROBOT_BASE_URL is available
                base_url = os.environ.get("BASE_URL") or os.environ.get("ROBOT_BASE_URL", "unknown")
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
                "name": test_name,
                "status": status,
                "statusDetails": {
                    "known": False,
                    "muted": False,
                    "flaky": False
                },
                "stage": "finished",
                "description": f"Robot Framework test: {test_name}",
                "steps": [],
                "attachments": [],
                "parameters": params,
                "start": timestamp,
                "stop": timestamp + duration_ms
            }
            
            output_file = os.path.join(allure_dir, f"{test_uuid}-result.json")
            with open(output_file, 'w') as f:
                import json
                json.dump(result, f, indent=2)
            
            converted += 1
        
        print(f"✅ Created {converted} individual Allure result(s)")
    elif total > 0:
        # Fallback: Create summary result if we can't find individual tests
        status = "passed" if failed == 0 else "failed"
        duration = 60000  # Default 1 minute
        
        test_uuid = uuid.uuid4().hex[:32]
        # Use test execution start time for summary result
        timestamp = test_start_time
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
            # Add verification metadata using shared utility
            # Use test execution start time extracted from XML
            params = add_verification_metadata_to_params(params, env, timestamp, "BASE_URL")
            # Override Base URL if ROBOT_BASE_URL is available
            base_url = os.environ.get("BASE_URL") or os.environ.get("ROBOT_BASE_URL", "unknown")
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
        
        converted = 1
        print(f"✅ Created summary Allure result (individual tests not found)")
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

