#!/bin/bash
# Convert Artillery Test Results to Allure Format
# Usage: ./scripts/ci/convert-artillery-to-allure.sh <allure-results-dir> <artillery-results-dir> [environment]
#
# Arguments:
#   allure-results-dir    - Directory where Allure results should be stored
#   artillery-results-dir - Directory containing Artillery JSON results
#   environment           - Optional environment name (dev, test, prod)

set -e

ALLURE_RESULTS_DIR="${1:-allure-results-combined}"
ARTILLERY_RESULTS_DIR="${2:-playwright/artillery-results}"
ENVIRONMENT="${3:-}"

echo "🔄 Converting Artillery Test Results to Allure Format..."
echo "   Allure Results: $ALLURE_RESULTS_DIR"
echo "   Artillery Results: $ARTILLERY_RESULTS_DIR"
echo "   Environment: ${ENVIRONMENT:-not specified}"
echo ""
echo "🔍 Debug: Searching for JSON files in: $ARTILLERY_RESULTS_DIR"
if [ ! -d "$ARTILLERY_RESULTS_DIR" ]; then
    echo "   ⚠️  Directory does not exist!"
    exit 0
fi
echo "   ✅ Directory exists"
echo "   📂 Directory structure:"
find "$ARTILLERY_RESULTS_DIR" -type f -name "*.json" 2>/dev/null | head -10 | while read f; do
    echo "      📄 $f"
done || echo "      (no JSON files found yet)"
echo ""

# Ensure Allure results directory exists
mkdir -p "$ALLURE_RESULTS_DIR"

# Check for Artillery results
if [ ! -d "$ARTILLERY_RESULTS_DIR" ]; then
    echo "⚠️  Artillery results directory not found: $ARTILLERY_RESULTS_DIR"
    exit 0
fi

# Use Python to parse Artillery JSON files
python3 <<PYTHON_SCRIPT
import json
import os
import sys
from pathlib import Path
import uuid
import hashlib
from datetime import datetime

# Import shared metadata utilities
# Try to find the script directory (for embedded Python scripts, use current working directory)
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

artillery_dir = "$ARTILLERY_RESULTS_DIR"
allure_dir = "$ALLURE_RESULTS_DIR"
env = "$ENVIRONMENT" if "$ENVIRONMENT" else None

converted = 0

# Look for Artillery JSON result files recursively
json_files = []
if os.path.isdir(artillery_dir):
    # Search recursively for ANY JSON files (Artillery outputs various JSON files)
    # Files like: smoke-results.json, homepage-results.json, applications-results.json
    for root, dirs, files in os.walk(artillery_dir):
        for file in files:
            if file.endswith('.json'):
                # Accept any JSON file - Artillery creates various result files
                json_files.append(os.path.join(root, file))

if not json_files:
    print("ℹ️  No Artillery JSON result files found")
    sys.exit(0)

for json_file in json_files:
    try:
        with open(json_file, 'r') as f:
            data = json.load(f)
        
        # Extract test information from Artillery results
        aggregate = data.get('aggregate', {})
        counters = aggregate.get('counters', {})
        summaries = aggregate.get('summaries', {})
        
        # Get test name from filename or use default
        test_name = os.path.basename(json_file).replace('.json', '').replace('-results', '').replace('_', ' ').title()
        if 'smoke' in test_name.lower():
            test_name = "Artillery Smoke Test"
        elif 'homepage' in test_name.lower():
            test_name = "Artillery Homepage Load Test"
        elif 'applications' in test_name.lower():
            test_name = "Artillery Applications Flow Load Test"
        else:
            test_name = f"Artillery Load Test: {test_name}"
        
        # Determine test status
        total_errors = counters.get('errors.total', 0)
        vusers_completed = counters.get('vusers.completed', 0)
        vusers_failed = counters.get('vusers.failed', 0)
        
        if total_errors > 0 or vusers_failed > 0:
            status = "failed"
        elif vusers_completed > 0:
            status = "passed"
        else:
            status = "broken"
        
        # Calculate duration
        first_metric_at = aggregate.get('firstMetricAt', 0)
        last_metric_at = aggregate.get('lastMetricAt', 0)
        duration_ms = max(last_metric_at - first_metric_at, 1000) if last_metric_at > first_metric_at else 5000
        
        # Generate UUID and historyId
        test_uuid = str(uuid.uuid4())
        history_id_content = f"artillery:{test_name}:{env or 'unknown'}"
        history_id = hashlib.md5(history_id_content.encode()).hexdigest()
        
        # Create labels
        labels = [
            {"name": "suite", "value": "Artillery Load Tests"},
            {"name": "testClass", "value": "Artillery"},
            {"name": "epic", "value": "Performance Testing"},
            {"name": "feature", "value": "Browser Load Testing"},
            {"name": "story", "value": test_name}
        ]
        
        if env and env not in ["unknown", "combined"]:
            labels.append({"name": "environment", "value": env})
        
        # Create parameters with metrics
        params = []
        if env and env not in ["unknown", "combined"]:
            params.append({"name": "Environment", "value": env.upper()})
            # Add verification metadata using shared utility
            # Use firstMetricAt from Artillery results (actual test execution time)
            params = add_verification_metadata_to_params(params, env, first_metric_at)
        
        # Add performance metrics as parameters
        if 'vusers.session_length' in summaries:
            session = summaries['vusers.session_length']
            params.append({"name": "Session Length (mean)", "value": f"{session.get('mean', 0):.1f}ms"})
            params.append({"name": "Session Length (min)", "value": f"{session.get('min', 0):.1f}ms"})
            params.append({"name": "Session Length (max)", "value": f"{session.get('max', 0):.1f}ms"})
        
        if 'page.loadTime' in summaries:
            load = summaries['page.loadTime']
            params.append({"name": "Page Load Time (mean)", "value": f"{load.get('mean', 0):.1f}ms"})
        
        # Add Core Web Vitals if available
        if 'webVitals.lcp' in summaries:
            lcp = summaries['webVitals.lcp']
            params.append({"name": "LCP (Largest Contentful Paint)", "value": f"{lcp.get('mean', 0):.1f}ms"})
        
        if 'webVitals.fcp' in summaries:
            fcp = summaries['webVitals.fcp']
            params.append({"name": "FCP (First Contentful Paint)", "value": f"{fcp.get('mean', 0):.1f}ms"})
        
        if 'webVitals.cls' in summaries:
            cls = summaries['webVitals.cls']
            params.append({"name": "CLS (Cumulative Layout Shift)", "value": f"{cls.get('mean', 0):.4f}"})
        
        # Add test statistics
        params.append({"name": "Virtual Users Created", "value": str(counters.get('vusers.created', 0))})
        params.append({"name": "Users Completed", "value": str(vusers_completed)})
        params.append({"name": "Users Failed", "value": str(vusers_failed)})
        params.append({"name": "Total Errors", "value": str(total_errors)})
        
        # Create description
        description = f"Artillery Load Test: {test_name}\\n\\n"
        description += f"Virtual Users: {counters.get('vusers.created', 0)}\\n"
        description += f"Completed: {vusers_completed}\\n"
        description += f"Failed: {vusers_failed}\\n"
        description += f"Errors: {total_errors}\\n"
        
        if 'vusers.session_length' in summaries:
            session = summaries['vusers.session_length']
            description += f"\\nSession Length:\\n"
            description += f"  Mean: {session.get('mean', 0):.1f}ms\\n"
            description += f"  Min: {session.get('min', 0):.1f}ms\\n"
            description += f"  Max: {session.get('max', 0):.1f}ms\\n"
        
        # Create status details
        status_details = {
            "known": False,
            "muted": False,
            "flaky": False
        }
        
        if total_errors > 0:
            status_details["message"] = f"Load test completed with {total_errors} error(s)"
            # Try to get error details from latencies or errors array if available
            if 'errors' in data and len(data['errors']) > 0:
                error_messages = [e.get('message', 'Unknown error') for e in data['errors'][:3]]
                status_details["message"] += f": {', '.join(error_messages)}"
        
        # Calculate timestamps
        timestamp = int(first_metric_at / 1000) if first_metric_at > 0 else int(datetime.now().timestamp() * 1000)
        
        # Create Allure result
        result = {
            "uuid": test_uuid,
            "historyId": history_id,
            "fullName": f"artillery.{test_name.replace(' ', '_').lower()}.{env or 'unknown'}",
            "labels": labels,
            "name": test_name,
            "status": status,
            "statusDetails": status_details,
            "stage": "finished",
            "description": description,
            "steps": [],
            "attachments": [],
            "parameters": params,
            "start": timestamp,
            "stop": timestamp + duration_ms
        }
        
        # Save result
        output_file = os.path.join(allure_dir, f"{test_uuid}-result.json")
        with open(output_file, 'w') as f:
            json.dump(result, f, indent=2)
        
        converted += 1
        print(f"✅ Converted: {test_name} ({status})")
        
    except Exception as e:
        print(f"⚠️  Error processing {json_file}: {e}", file=sys.stderr)
        continue

if converted == 0:
    print("ℹ️  No Artillery results were converted")
    sys.exit(0)

print(f"\\n✅ Converted {converted} Artillery test result(s)")
PYTHON_SCRIPT

echo ""
echo "✅ Artillery to Allure conversion complete!"
echo "   Results saved to: $ALLURE_RESULTS_DIR"

