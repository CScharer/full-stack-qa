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

# Debug: Count results by suite name before grouping
suite_counts = defaultdict(int)
files_without_suite = []
selenide_files_found = []

for result_file in result_files:
    try:
        with open(result_file, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        # Check for Selenide indicators
        is_selenide = False
        if 'labels' in data:
            for label in data['labels']:
                if label.get('name') == 'suite':
                    suite_value = label.get('value', 'N/A')
                    suite_counts[suite_value] += 1
                    if 'Selenide' in suite_value or suite_value == 'Selenide Tests':
                        is_selenide = True
                    break
                elif label.get('name') == 'epic' and label.get('value') == 'HomePage Tests':
                    is_selenide = True
                elif label.get('name') == 'testClass' and 'HomePageTests' in str(label.get('value', '')):
                    is_selenide = True
        
        # Check fullName for Selenide
        if 'fullName' in data and 'Selenide' in data.get('fullName', ''):
            is_selenide = True
        
        if is_selenide:
            selenide_files_found.append(result_file)
        
        # Track files without suite labels
        if 'labels' not in data or not any(l.get('name') == 'suite' for l in data.get('labels', [])):
            if len(files_without_suite) < 10:
                files_without_suite.append(result_file)
    except Exception as e:
        if len(files_without_suite) < 10:
            files_without_suite.append((result_file, str(e)))

if suite_counts:
    print(f"🔍 Suite distribution (all files):")
    for suite, count in sorted(suite_counts.items()):
        print(f"   - {suite}: {count} file(s)")

if selenide_files_found:
    print(f"\n🔍 Found {len(selenide_files_found)} potential Selenide result file(s)")
    # Check suite labels on Selenide files
    selenide_suite_labels = defaultdict(int)
    for sel_file in selenide_files_found[:10]:  # Check first 10
        try:
            with open(sel_file, 'r', encoding='utf-8') as f:
                data = json.load(f)
                if 'labels' in data:
                    for label in data['labels']:
                        if label.get('name') == 'suite':
                            selenide_suite_labels[label.get('value', 'N/A')] += 1
                            break
        except:
            pass
    if selenide_suite_labels:
        print(f"   Selenide files have suite labels: {dict(selenide_suite_labels)}")
    else:
        print(f"   ⚠️  Selenide files are missing suite labels!")

if files_without_suite:
    print(f"\n⚠️  Found {len(files_without_suite)} file(s) without suite labels (showing first 10):")
    for item in files_without_suite[:10]:
        if isinstance(item, tuple):
            print(f"   - {item[0].name[:50]}... (error: {item[1]})")
        else:
            print(f"   - {item.name[:50]}...")

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
        is_selenide = False
        
        # Check for Selenide indicators (same logic as debug section)
        if 'labels' in data:
            for label in data['labels']:
                if label.get('name') == 'epic' and label.get('value') == 'HomePage Tests':
                    is_selenide = True
                elif label.get('name') == 'testClass' and 'HomePageTests' in str(label.get('value', '')):
                    is_selenide = True
                elif label.get('name') == 'suite':
                    suite_value = label.get('value', '')
                    if 'Selenide' in suite_value or suite_value == 'Selenide Tests':
                        is_selenide = True
                    suite_name = suite_value
                elif label.get('name') == 'environment':
                    env = label.get('value', 'unknown')
        
        # Check fullName for Selenide
        if 'fullName' in data and 'Selenide' in data.get('fullName', ''):
            is_selenide = True
        
        # CRITICAL: If this is a Selenide test but suite label says "Surefire test", override it
        # This handles cases where add-environment-labels.sh hasn't updated the suite label yet
        # or the result file was missed
        if is_selenide and suite_name == 'Surefire test':
            suite_name = 'Selenide Tests'
        
        # Skip if no suite name found
        if not suite_name:
            # Debug: Log files without suite labels (first 10)
            if len([f for f in result_files if f == result_file]) <= 10:
                print(f"   ⚠️  Skipping file without suite label: {result_file.name[:50]}...", file=sys.stderr)
            continue
        
        # Default environment if not found
        # IMPORTANT: Don't skip "combined" - we'll try to infer environment from other sources
        if not env or env == 'unknown':
            env = 'unknown'
        # Keep "combined" as-is for now - we'll handle it below
        
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
# Store env-specific container UUIDs for top-level containers
# Structure: {suite_name: [env_container_uuids]}
env_container_uuids_by_suite = defaultdict(list)
# Store container file paths for adding parentSuite labels later
# Structure: {suite_name: [(container_file_path, container_uuid)]}
env_container_files_by_suite = defaultdict(list)

containers_created = 0
for suite_name, env_groups in suite_groups.items():
    for env, results in env_groups.items():
        if not results:
            continue
        
        # Skip "unknown" environment - these tests don't have environment info
        if env == 'unknown':
            continue
        
        # For "combined" environment, try to split by environment from test names
        # Test names should have [DEV], [TEST], or [PROD] appended by add-environment-labels.sh
        if env == 'combined':
            # Try to split combined results by environment based on test names
            env_split_results = {'dev': [], 'test': [], 'prod': []}
            for r in results:
                test_name = r.get('name', '')
                if '[DEV]' in test_name:
                    env_split_results['dev'].append(r)
                elif '[TEST]' in test_name:
                    env_split_results['test'].append(r)
                elif '[PROD]' in test_name:
                    env_split_results['prod'].append(r)
            
            # If we successfully split by environment, create separate containers
            if any(env_split_results.values()):
                for split_env, split_results in env_split_results.items():
                    if not split_results:
                        continue
                    split_uuids = [r['uuid'] for r in split_results if r['uuid']]
                    if not split_uuids:
                        continue
                    
                    container_uuid = uuid.uuid4().hex
                    container_name = f"{suite_name} [{split_env.upper()}]"
                    
                    container_data = {
                        "uuid": container_uuid,
                        "name": container_name,
                        "children": split_uuids,
                        "description": f"{suite_name} test suite - {split_env.upper()} environment",
                        "labels": [
                            {"name": "suite", "value": suite_name},
                            {"name": "environment", "value": split_env}
                        ],
                        "befores": [],
                        "afters": [],
                        "start": 0,
                        "stop": 0
                    }
                    
                    container_file = Path(results_dir) / f"{container_uuid}-container.json"
                    with open(container_file, 'w', encoding='utf-8') as f:
                        json.dump(container_data, f, indent=2, ensure_ascii=False)
                    
                    containers_created += 1
                    env_container_uuids_by_suite[suite_name].append(container_uuid)
                    print(f"   ✅ Created container: {container_name} ({len(split_uuids)} tests) [from combined]")
                continue  # Skip creating a "combined" container since we split it
            else:
                # Couldn't split, create a single container without environment suffix
                container_name = suite_name
        else:
            # Normal environment (dev/test/prod) - create container with environment suffix
            container_name = f"{suite_name} [{env.upper()}]"
        
        # Generate container UUID
        container_uuid = uuid.uuid4().hex
        
        # Collect all result UUIDs
        result_uuids = [r['uuid'] for r in results if r['uuid']]
        
        if not result_uuids:
            continue
        
        # Create container file
        container_data = {
            "uuid": container_uuid,
            "name": container_name,
            "children": result_uuids,
            "description": f"{suite_name} test suite" + (f" - {env.upper()} environment" if env != 'unknown' and env != 'combined' else ""),
            "labels": [
                {"name": "suite", "value": suite_name}
            ],
            "befores": [],
            "afters": [],
            "start": 0,
            "stop": 0
        }
        
        # Add environment label if not unknown or combined
        if env != 'unknown' and env != 'combined':
            container_data["labels"].append({"name": "environment", "value": env})
        
        # Store container file path and suite name for adding parentSuite later
        # We need to add parentSuite after top-level containers are created
        
        # Write container file
        container_file = Path(results_dir) / f"{container_uuid}-container.json"
        with open(container_file, 'w', encoding='utf-8') as f:
            json.dump(container_data, f, indent=2, ensure_ascii=False)
        
        containers_created += 1
        env_container_uuids_by_suite[suite_name].append(container_uuid)
        # Store container file path for adding parentSuite later (only for env-specific containers)
        if env != 'unknown' and env != 'combined' and container_name != suite_name:
            env_container_files_by_suite[suite_name].append((container_file, container_uuid))
        # Always show debug output for framework containers (not just first 10)
        print(f"   ✅ Created container: {container_name} ({len(result_uuids)} tests)")

print(f"\n✅ Created {containers_created} container file(s)")

# Debug: Show what suite/environment combinations we found
print(f"\n🔍 Suite/Environment groups found:")
for suite_name, env_groups in suite_groups.items():
    env_list = list(env_groups.keys())
    total_tests = sum(len(results) for results in env_groups.values())
    print(f"   - {suite_name}: {total_tests} test(s) across {len(env_list)} environment(s) {env_list}")

# Also create top-level containers for each framework (grouping all environments)
# CRITICAL: Top-level containers should reference env-specific container UUIDs, not result UUIDs
# This creates the proper hierarchy: Top-level -> Env-specific -> Results
print("\n📦 Creating top-level framework containers...")
top_level_containers = 0

for suite_name, env_groups in suite_groups.items():
    # Get env-specific container UUIDs for this suite
    env_container_uuids = env_container_uuids_by_suite.get(suite_name, [])
    
    # If no env-specific containers were created (shouldn't happen), fall back to result UUIDs
    if not env_container_uuids:
        # Fallback: collect all result UUIDs across all environments
        all_result_uuids = []
        for env, results in env_groups.items():
            if env != 'unknown':
                all_result_uuids.extend([r['uuid'] for r in results if r['uuid']])
        
        if not all_result_uuids:
            continue
        
        # Use result UUIDs as children (flat structure)
        children_uuids = all_result_uuids
    else:
        # Use env-specific container UUIDs as children (proper hierarchy)
        children_uuids = env_container_uuids
    
    # Create top-level container (no environment suffix)
    top_container_uuid = uuid.uuid4().hex
    
    top_container_data = {
        "uuid": top_container_uuid,
        "name": suite_name,
        "children": children_uuids,
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
        env_count = len([e for e in env_groups.keys() if e != 'unknown'])
        child_type = "env containers" if env_container_uuids else "test results"
        print(f"   ✅ Created top-level container: {suite_name} ({len(children_uuids)} {child_type}, {env_count} environment(s))")
    
    # CRITICAL: Add parentSuite labels to env-specific containers
    # Allure's Suites tab requires parentSuite to build the hierarchy
    # Update env-specific containers to have parentSuite pointing to the top-level suite
    if env_container_uuids:
        container_files = env_container_files_by_suite.get(suite_name, [])
        for container_file_path, container_uuid in container_files:
            try:
                with open(container_file_path, 'r', encoding='utf-8') as f:
                    container_data = json.load(f)
                
                # Add parentSuite label if it doesn't exist or update it
                labels = container_data.get('labels', [])
                parent_suite_found = False
                for i, label in enumerate(labels):
                    if label.get('name') == 'parentSuite':
                        labels[i]['value'] = suite_name
                        parent_suite_found = True
                        break
                
                if not parent_suite_found:
                    labels.append({"name": "parentSuite", "value": suite_name})
                
                container_data['labels'] = labels
                
                # Write updated container file
                with open(container_file_path, 'w', encoding='utf-8') as f:
                    json.dump(container_data, f, indent=2, ensure_ascii=False)
            except Exception as e:
                print(f"⚠️  Error updating parentSuite for {container_file_path.name}: {e}", file=sys.stderr)

print(f"✅ Created {top_level_containers} top-level container file(s)")

PYTHON_SCRIPT

if [ $? -ne 0 ]; then
    echo "⚠️  Error creating container files"
    exit 1
fi

echo ""
echo "✅ Framework container files created successfully!"

