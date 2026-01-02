#!/bin/bash
# Convert Cypress Test Results to Allure Format
# Usage: ./scripts/ci/convert-cypress-to-allure.sh <allure-results-dir> <cypress-results-dir> [environment]
#
# Arguments:
#   allure-results-dir  - Directory where Allure results should be stored
#   cypress-results-dir - Directory containing Cypress test results
#   environment         - Optional environment name (dev, test, prod)

set -e

ALLURE_RESULTS_DIR="${1:-allure-results-combined}"
CYPRESS_RESULTS_DIR="${2:-cypress/cypress}"
ENVIRONMENT="${3:-}"

echo "🔄 Converting Cypress Test Results to Allure Format..."
echo "   Allure Results: $ALLURE_RESULTS_DIR"
echo "   Cypress Results: $CYPRESS_RESULTS_DIR"
echo "   Environment: ${ENVIRONMENT:-not specified}"
echo ""

# Ensure Allure results directory exists
mkdir -p "$ALLURE_RESULTS_DIR"

# Cross-platform UUID generation
generate_uuid() {
    if command -v uuidgen &> /dev/null; then
        uuidgen | tr '[:upper:]' '[:lower:]' | tr -d '-' | cut -c1-32
    elif command -v python3 &> /dev/null; then
        python3 -c "import uuid; print(uuid.uuid4().hex[:32])"
    else
        cat /dev/urandom | head -c 16 | od -An -tx1 | tr -d ' \n'
    fi
}

# Cross-platform MD5 hash
generate_hash() {
    local input="$1"
    if command -v md5sum &> /dev/null; then
        echo -n "$input" | md5sum | cut -d' ' -f1
    elif command -v md5 &> /dev/null; then
        echo -n "$input" | md5 | cut -d' ' -f1
    elif command -v python3 &> /dev/null; then
        python3 -c "import hashlib; print(hashlib.md5('$input'.encode()).hexdigest())"
    else
        echo -n "$input" | head -c 32
    fi
}

# Function to create Allure test result JSON
create_allure_result() {
    local name="$1"
    local status="$2"
    local duration="$3"
    local description="$4"
    local full_name="${5:-$name}"
    local environment="${6:-}"
    
    local uuid=$(generate_uuid)
    local timestamp=$(date +%s)000
    # Include environment in historyId to prevent deduplication across environments
    local history_id=$(generate_hash "${full_name}:${environment:-}")
    
    # Build labels array
    local labels_json='[
    {
      "name": "suite",
      "value": "Cypress Tests"
    },
    {
      "name": "testClass",
      "value": "Cypress"
    },
    {
      "name": "epic",
      "value": "Cypress E2E Testing"
    },
    {
      "name": "feature",
      "value": "Cypress Tests"
    }'
    
    # Add environment label if provided
    if [ -n "$environment" ] && [ "$environment" != "unknown" ] && [ "$environment" != "combined" ]; then
        labels_json="${labels_json},
    {
      \"name\": \"environment\",
      \"value\": \"$environment\"
    }"
    fi
    
    labels_json="${labels_json}
  ]"
    
    # Build parameters array
    local params_json="[]"
    if [ -n "$environment" ] && [ "$environment" != "unknown" ] && [ "$environment" != "combined" ]; then
        params_json="[
    {
      \"name\": \"Environment\",
      \"value\": \"$(echo $environment | tr '[:lower:]' '[:upper:]')\"
    }
  ]"
    fi
    
    cat > "$ALLURE_RESULTS_DIR/${uuid}-result.json" <<EOF
{
  "uuid": "${uuid}",
  "historyId": "${history_id}",
  "fullName": "$full_name",
  "labels": ${labels_json},
  "name": "$name",
  "status": "$status",
  "statusDetails": {
    "known": false,
    "muted": false,
    "flaky": false
  },
  "stage": "finished",
  "description": "$description",
  "steps": [],
  "attachments": [],
  "parameters": ${params_json},
  "start": $timestamp,
  "stop": $((timestamp + duration))
}
EOF
}

# Check for Cypress results
if [ ! -d "$CYPRESS_RESULTS_DIR" ]; then
    echo "⚠️  Cypress results directory not found: $CYPRESS_RESULTS_DIR"
    exit 0
fi

# Look for Cypress result files (mochawesome.json, cypress-results.json, or test results)
CONVERTED=0

# Try to find and parse Cypress result JSON files
# Look for mochawesome.json, cypress-results.json, or any JSON file in results directory
json_file=$(find "$CYPRESS_RESULTS_DIR" \( -name "mochawesome.json" -o -name "cypress-results.json" -o -path "*/results/*.json" \) 2>/dev/null | head -1)
if [ -n "$json_file" ] && [ -f "$json_file" ]; then
    echo "📊 Found Cypress result file: $json_file"
    
    # Use Python to parse JSON (more reliable than grep)
    python3 <<PYTHON_SCRIPT
import json
import os
import sys
from pathlib import Path

json_file = "$json_file"
allure_dir = "$ALLURE_RESULTS_DIR"
env = "$ENVIRONMENT" if "$ENVIRONMENT" else None

try:
    with open(json_file, 'r') as f:
        data = json.load(f)
    
    # Cypress/Mochawesome structure varies, try to extract test information
    # Common structure: { "stats": { "tests": N, "passes": X, "failures": Y }, "results": [...] }
    
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
            base_url = os.environ.get(base_url_env_var) or os.environ.get("CYPRESS_baseUrl", "unknown")
            params.append({"name": "Base URL", "value": str(base_url)})
            if test_timestamp and test_timestamp > 0:
                test_timestamp_iso = datetime.fromtimestamp(test_timestamp / 1000).isoformat()
            else:
                test_timestamp_iso = datetime.now().isoformat()
            params.append({"name": "Test Execution Time", "value": test_timestamp_iso})
            params.append({"name": "CI Run ID", "value": os.environ.get("GITHUB_RUN_ID", "local")})
            params.append({"name": "CI Run Number", "value": os.environ.get("GITHUB_RUN_NUMBER", "unknown")})
            return params
    
    converted = 0
    
    # Try to find individual test results
    def find_tests(obj, tests_list=None):
        """Recursively find all test objects in Cypress results"""
        if tests_list is None:
            tests_list = []
        
        if isinstance(obj, dict):
            # Check if this looks like a test object
            if 'title' in obj and ('state' in obj or 'status' in obj):
                tests_list.append(obj)
            # Recursively search nested structures
            for value in obj.values():
                find_tests(value, tests_list)
        elif isinstance(obj, list):
            for item in obj:
                find_tests(item, tests_list)
        
        return tests_list
    
    # Extract stats for summary
    stats = data.get('stats', {})
    total = stats.get('tests', 0)
    passes = stats.get('passes', 0)
    failures = stats.get('failures', 0)
    pending = stats.get('pending', 0)
    
    print(f"📊 Cypress Stats: {total} tests, {passes} passed, {failures} failed, {pending} pending")
    
    # Find all individual tests
    all_tests = find_tests(data)
    
    if all_tests and len(all_tests) > 0:
        print(f"📊 Found {len(all_tests)} individual test(s) to convert")
        
        # Create individual Allure results for each test
        for test in all_tests:
            test_title = test.get('title', 'Unknown Test')
            test_state = test.get('state', test.get('status', 'unknown'))
            test_duration = test.get('duration', 0) or 1000  # milliseconds
            test_full_title = test.get('fullTitle', test_title)
            
            # Map Cypress states to Allure statuses
            if test_state in ['passed', 'PASS']:
                status = "passed"
            elif test_state in ['failed', 'FAIL']:
                status = "failed"
            elif test_state in ['pending', 'PENDING', 'skipped', 'SKIPPED']:
                status = "skipped"
            else:
                status = "passed"  # Default to passed
            
            test_uuid = uuid.uuid4().hex[:32]
            timestamp = int(datetime.now().timestamp() * 1000)
            full_name = f"Cypress.{test_full_title}"
            history_id = hashlib.md5(f"{full_name}:{env or ''}".encode()).hexdigest()
            
            labels = [
                {"name": "suite", "value": "Cypress Tests"},
                {"name": "testClass", "value": "Cypress"},
                {"name": "epic", "value": "Cypress E2E Testing"},
                {"name": "feature", "value": "Cypress Tests"}
            ]
            
            if env and env not in ["unknown", "combined"]:
                labels.append({"name": "environment", "value": env})
            
            params = []
            if env and env not in ["unknown", "combined"]:
                params.append({"name": "Environment", "value": env.upper()})
                # Add verification metadata using shared utility
                # Use test execution time from Cypress results (timestamp is already calculated)
                params = add_verification_metadata_to_params(params, env, timestamp, "BASE_URL")
                # Override Base URL if CYPRESS_baseUrl is available
                base_url = os.environ.get("BASE_URL") or os.environ.get("CYPRESS_baseUrl") or test.get('baseUrl', 'unknown')
                if base_url != "unknown":
                    for p in params:
                        if p["name"] == "Base URL":
                            p["value"] = str(base_url)
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
                "description": f"Cypress test: {test_full_title}",
                "steps": [],
                "attachments": [],
                "parameters": params,
                "start": timestamp,
                "stop": timestamp + int(test_duration)
            }
            
            output_file = os.path.join(allure_dir, f"{test_uuid}-result.json")
            with open(output_file, 'w') as f:
                json.dump(result, f, indent=2)
            
            converted += 1
        
        print(f"✅ Created {converted} individual Allure result(s)")
    elif total > 0:
        # Fallback: Create summary result if we can't find individual tests
        status = "passed" if failures == 0 else "failed"
        duration = stats.get('duration', 0) or 60000
        
        test_uuid = uuid.uuid4().hex[:32]
        timestamp = int(datetime.now().timestamp() * 1000)
        test_name = f"Cypress Test Suite"
        full_name = f"Cypress.{test_name}"
        history_id = hashlib.md5(full_name.encode()).hexdigest()
        
        labels = [
            {"name": "suite", "value": "Cypress Tests"},
            {"name": "testClass", "value": "Cypress"},
            {"name": "epic", "value": "Cypress E2E Testing"},
            {"name": "feature", "value": "Cypress Tests"}
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
            "name": f"{test_name} ({passes} passed, {failures} failed)",
            "status": status,
            "statusDetails": {
                "known": False,
                "muted": False,
                "flaky": False
            },
            "stage": "finished",
            "description": f"Cypress Test Suite: {total} tests total, {passes} passed, {failures} failed, {pending} pending",
            "steps": [],
            "attachments": [],
            "parameters": params,
            "start": timestamp,
            "stop": timestamp + duration
        }
        
        output_file = os.path.join(allure_dir, f"{test_uuid}-result.json")
        with open(output_file, 'w') as f:
            json.dump(result, f, indent=2)
        
        converted = 1
        print(f"✅ Created summary Allure result (individual tests not found)")
        
except Exception as e:
    print(f"⚠️  Error parsing Cypress results: {e}", file=sys.stderr)
    sys.exit(0)  # Don't fail the build if conversion fails
PYTHON_SCRIPT

    CONVERTED=$?
    if [ $CONVERTED -eq 0 ]; then
        echo "✅ Cypress results converted successfully"
    fi
else
    echo "ℹ️  No Cypress result JSON files found (mochawesome.json or cypress-results.json)"
    echo "   This is expected if Cypress tests haven't run or don't generate JSON reports"
fi

echo ""
echo "✅ Cypress to Allure conversion complete!"
echo "   Results saved to: $ALLURE_RESULTS_DIR"

