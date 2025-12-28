#!/bin/bash
# scripts/ci/create-framework-containers.sh
# Creates Allure container files for framework test suites
# This ensures all frameworks appear in the Suites section of Allure reports

set -e

RESULTS_DIR="${1:-allure-results-combined}"

if [ ! -d "$RESULTS_DIR" ]; then
    echo "⚠️  Results directory not found: $RESULTS_DIR"
    exit 0
fi

echo "📦 Creating framework container files..."
echo "   Results directory: $RESULTS_DIR"
echo ""

# Use Python to create containers for each framework suite
python3 <<'PYTHON_SCRIPT'
import json
import os
import sys
import uuid
from pathlib import Path
from collections import defaultdict

results_dir = os.environ.get('RESULTS_DIR', 'allure-results-combined')

if not os.path.isdir(results_dir):
    print(f"⚠️  Results directory not found: {results_dir}")
    exit(0)

# Find all result files
result_files = list(Path(results_dir).glob("*-result.json"))

print(f"📊 Found {len(result_files)} result files")

# Group results by framework suite and environment
# Structure: {suite_name: {env: [result_files]}}
suite_groups = defaultdict(lambda: defaultdict(list))

for result_file in result_files:
    try:
        with open(result_file, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        # Extract suite name from labels
        suite_name = None
        env = None
        
        if 'labels' in data:
            for label in data['labels']:
                if label.get('name') == 'suite':
                    suite_name = label.get('value')
                elif label.get('name') == 'environment':
                    env = label.get('value', 'unknown')
        
        # Skip if no suite name found
        if not suite_name:
            continue
        
        # Default environment if not found
        if not env or env == 'unknown' or env == 'combined':
            env = 'unknown'
        
        # Group by suite and environment
        suite_groups[suite_name][env].append({
            'file': result_file,
            'uuid': data.get('uuid', ''),
            'name': data.get('name', ''),
            'fullName': data.get('fullName', '')
        })
    except Exception as e:
        print(f"⚠️  Error processing {result_file}: {e}", file=sys.stderr)
        continue

# Create container files for each suite/environment combination
containers_created = 0
for suite_name, env_groups in suite_groups.items():
    for env, results in env_groups.items():
        if not results:
            continue
        
        # Generate container UUID
        container_uuid = uuid.uuid4().hex
        
        # Collect all result UUIDs
        result_uuids = [r['uuid'] for r in results if r['uuid']]
        
        if not result_uuids:
            continue
        
        # Create container name (include environment if not unknown)
        if env != 'unknown':
            container_name = f"{suite_name} [{env.upper()}]"
        else:
            container_name = suite_name
        
        # Create container file
        container_data = {
            "uuid": container_uuid,
            "name": container_name,
            "children": result_uuids,
            "description": f"{suite_name} test suite" + (f" - {env.upper()} environment" if env != 'unknown' else ""),
            "labels": [
                {"name": "suite", "value": suite_name}
            ],
            "befores": [],
            "afters": [],
            "start": 0,
            "stop": 0
        }
        
        # Add environment label if not unknown
        if env != 'unknown':
            container_data["labels"].append({"name": "environment", "value": env})
        
        # Write container file
        container_file = Path(results_dir) / f"{container_uuid}-container.json"
        with open(container_file, 'w', encoding='utf-8') as f:
            json.dump(container_data, f, indent=2, ensure_ascii=False)
        
        containers_created += 1
        if containers_created <= 10:  # Debug output for first 10
            print(f"   ✅ Created container: {container_name} ({len(result_uuids)} tests)")

print(f"\n✅ Created {containers_created} container file(s)")

# Also create top-level containers for each framework (grouping all environments)
print("\n📦 Creating top-level framework containers...")
top_level_containers = 0

for suite_name, env_groups in suite_groups.items():
    # Collect all result UUIDs across all environments
    all_result_uuids = []
    for env, results in env_groups.items():
        all_result_uuids.extend([r['uuid'] for r in results if r['uuid']])
    
    if not all_result_uuids:
        continue
    
    # Create top-level container (no environment suffix)
    top_container_uuid = uuid.uuid4().hex
    
    top_container_data = {
        "uuid": top_container_uuid,
        "name": suite_name,
        "children": all_result_uuids,
        "description": f"{suite_name} test suite (all environments)",
        "labels": [
            {"name": "suite", "value": suite_name}
        ],
        "befores": [],
        "afters": [],
        "start": 0,
        "stop": 0
    }
    
    # Write top-level container file
    top_container_file = Path(results_dir) / f"{top_container_uuid}-container.json"
    with open(top_container_file, 'w', encoding='utf-8') as f:
        json.dump(top_container_data, f, indent=2, ensure_ascii=False)
    
    top_level_containers += 1
    if top_level_containers <= 10:  # Debug output
        env_count = len(env_groups)
        print(f"   ✅ Created top-level container: {suite_name} ({len(all_result_uuids)} tests, {env_count} environment(s))")

print(f"✅ Created {top_level_containers} top-level container file(s)")

PYTHON_SCRIPT

if [ $? -ne 0 ]; then
    echo "⚠️  Error creating container files"
    exit 1
fi

echo ""
echo "✅ Framework container files created successfully!"

