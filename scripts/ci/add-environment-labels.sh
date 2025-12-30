#!/bin/bash
# scripts/ci/add-environment-labels.sh
# Adds environment labels to Allure result files to prevent deduplication across environments
# This script processes results from the merged directory and adds environment labels based on
# the source artifact path or file metadata

set -e

RESULTS_DIR="${1:-allure-results-combined}"
SOURCE_DIR="${2:-all-test-results}"

if [ ! -d "$RESULTS_DIR" ]; then
    echo "⚠️  Results directory not found: $RESULTS_DIR"
    exit 0
fi

echo "🏷️  Adding environment labels to Allure results..."
echo "   Results directory: $RESULTS_DIR"
echo "   Source directory: $SOURCE_DIR"
echo ""

# Count files before processing
BEFORE_COUNT=$(find "$RESULTS_DIR" -name "*-result.json" 2>/dev/null | wc -l | tr -d ' ')
echo "📊 Found $BEFORE_COUNT result files to process"

# Create a mapping of result file UUIDs to their source environment
# by checking which artifact directory they came from
echo "🔍 Analyzing source artifacts to determine environments..."

# Process each result file and determine its environment
PROCESSED=0
SKIPPED=0
ERRORS=0

export RESULTS_DIR SOURCE_DIR
python3 <<'PYTHON_SCRIPT'
import json
import os
import sys
import hashlib
from pathlib import Path

results_dir = os.environ.get('RESULTS_DIR', 'allure-results-combined')
source_dir = os.environ.get('SOURCE_DIR', 'all-test-results')

if not os.path.isdir(results_dir):
    print(f"⚠️  Results directory not found: {results_dir}")
    sys.exit(0)

# Find all result files and container files (exclude .env.* marker files)
result_files = [f for f in Path(results_dir).glob("*-result.json") if not f.name.startswith(".env.")]
container_files = [f for f in Path(results_dir).glob("*-container.json") if not f.name.startswith(".env.")]
all_files = result_files + container_files
print(f"📊 Processing {len(result_files)} result files and {len(container_files)} container files...")

processed = 0
skipped = 0
errors = 0
selenide_updated = 0
name_fixed_count = 0

# If we have source directory, try to map files to environments
env_mapping = {}
if source_dir and os.path.isdir(source_dir):
    # Look for environment indicators in source paths
    # Prioritize artifact patterns: *-results-dev, *-results-test, *-results-prod
    for root, dirs, files in os.walk(source_dir):
        for file in files:
            if file.endswith("-result.json"):
                # Extract environment from path
                path_str = root.lower()
                env = "unknown"
                
                # Check for explicit -results-{env} pattern (most reliable)
                # Also check for be-results-{env}/ for BE results
                if "-results-dev" in path_str or "/results-dev/" in path_str or "/be-results-dev/" in path_str:
                    env = "dev"
                elif "-results-test" in path_str or "/results-test/" in path_str or "/be-results-test/" in path_str:
                    env = "test"
                elif "-results-prod" in path_str or "/results-prod/" in path_str or "/be-results-prod/" in path_str:
                    env = "prod"
                # Fallback: check for environment in directory names (more specific)
                elif ("/dev/" in path_str or "/development/" in path_str) and not any(x in path_str for x in ["/test/", "/testing/", "/prod/", "/production/"]):
                    env = "dev"
                elif ("/test/" in path_str or "/testing/" in path_str) and not any(x in path_str for x in ["/prod/", "/production/"]):
                    env = "test"
                elif "/prod/" in path_str or "/production/" in path_str:
                    env = "prod"
                
                # Store mapping by filename (UUID)
                if env != "unknown":
                    env_mapping[file] = env

# Also check for .env.* marker files created during merge
# Marker files use .marker extension to avoid being processed by Allure
results_path = Path(results_dir)
marker_files_found = 0
marker_files_read = 0
for env_file in results_path.glob(".env.*.marker"):
    marker_files_found += 1
    # Extract the UUID from filename: .env.{uuid}.marker -> {uuid}-result.json
    marker_name = env_file.name.replace(".env.", "").replace(".marker", "")
    result_filename = f"{marker_name}-result.json"
    try:
        with open(env_file, 'r') as f:
            env = f.read().strip()
            if env and env != "unknown":
                env_mapping[result_filename] = env
                marker_files_read += 1
    except Exception as e:
        print(f"⚠️  Error reading marker file {env_file.name}: {e}", file=sys.stderr)

if marker_files_found > 0:
    print(f"📋 Found {marker_files_found} marker files, successfully read {marker_files_read}")
    print(f"   Environment mapping contains {len(env_mapping)} entries")

for result_file in all_files:
    try:
        with open(result_file, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        # FIX: Ensure 'name' field is a string, not an array (fixes JSON deserialization errors)
        # Some files may have been created with name as an array, which causes Allure to skip them
        name_fixed = False
        if 'name' in data and isinstance(data['name'], list):
            # Convert array to string (take first element or join)
            if len(data['name']) > 0:
                data['name'] = str(data['name'][0]) if isinstance(data['name'][0], str) else ' '.join(str(x) for x in data['name'])
            else:
                data['name'] = 'Unnamed Test'
            name_fixed = True
        
        # Determine environment
        env = "unknown"
        
        # First, check if already has environment label
        # IMPORTANT: If environment label already exists and is valid (dev/test/prod), keep it
        # Don't skip - we still need to ensure it's properly formatted and add parameters
        has_valid_env = False
        if 'labels' in data:
            for label in data['labels']:
                if label.get('name') == 'environment':
                    existing_env = label.get('value', 'unknown')
                    if existing_env in ['dev', 'test', 'prod']:
                        env = existing_env
                        has_valid_env = True
                        # Don't break - continue to check other labels and add parameters
                    break
        
        # If no valid environment label found, try to determine from file mapping or labels
        if not has_valid_env and env == "unknown":
            # Check if we have a mapping for this file
            filename = result_file.name
            if filename in env_mapping:
                env = env_mapping[filename]
            else:
                # For BE tests, check if they're in be-allure-results directory
                # and try to infer from source directory structure
                result_path_str = str(result_file.parent).lower()
                if "be-allure-results" in result_path_str or "allure-be-results" in result_path_str:
                    # BE results might be in all-test-results/be-allure-results/ or similar
                    # Check parent directories for environment indicators
                    parent_path = str(result_file.parent)
                    if source_dir and os.path.isdir(source_dir):
                        # Look for be-allure-results in source and check its parent
                        for root, dirs, files in os.walk(source_dir):
                            if "be-allure-results" in root.lower() or "allure-be-results" in root.lower():
                                # Check if parent directory has environment info
                                parent = os.path.dirname(root)
                                if "-be-results-dev" in parent.lower() or "/be-results-dev/" in parent.lower():
                                    env = "dev"
                                    break
                                elif "-be-results-test" in parent.lower() or "/be-results-test/" in parent.lower():
                                    env = "test"
                                    break
                                elif "-be-results-prod" in parent.lower() or "/be-results-prod/" in parent.lower():
                                    env = "prod"
                                    break
                
                # Try to infer from existing labels
                if env == "unknown" and 'labels' in data:
                    for label in data['labels']:
                        label_name = label.get('name', '').lower()
                        label_value = label.get('value', '').lower()
                        if 'env' in label_name or 'environment' in label_name:
                            env = label_value
                        elif label_value in ['dev', 'test', 'prod']:
                            env = label_value
                
                # Default to "combined" if we can't determine
                if env == "unknown":
                    env = "combined"
        
        # Add or update environment label
        if 'labels' not in data:
            data['labels'] = []
        
        # Remove existing environment label if present
        data['labels'] = [l for l in data['labels'] if l.get('name') != 'environment']
        
        # Add environment label
        data['labels'].append({
            "name": "environment",
            "value": env
        })
        
        # Add environment as a parameter for better visibility and filtering
        if 'parameters' not in data:
            data['parameters'] = []
        
        # Remove existing environment parameter if present
        data['parameters'] = [p for p in data['parameters'] if p.get('name') != 'Environment']
        
        # Add environment parameter
        data['parameters'].append({
            "name": "Environment",
            "value": env.upper()
        })
        
        # Also append environment to test name for visibility (if not already present)
        if 'name' in data:
            test_name = data.get('name', '')
            # Only append if environment is not already in the name
            if f"[{env.upper()}]" not in test_name and f"({env})" not in test_name:
                data['name'] = f"{test_name} [{env.upper()}]"
        
        # Update suite labels for Selenide tests to make them more visible
        # Selenide tests have epic="HomePage Tests" and feature="HomePage Navigation"
        # They currently have parentSuite="Surefire suite" and suite="Surefire test" which groups them incorrectly
        # Detection: Use epic="HomePage Tests" as primary identifier (most reliable)
        # Fallback: Also check for feature="HomePage Navigation" or testClass containing "HomePageTests"
        # For container files: Check for "Surefire suite" or "Surefire test" in suite/parentSuite labels
        if 'labels' in data:
            labels = data.get('labels', [])
            epic_value = None
            feature_value = None
            test_class_value = None
            suite_label_index = None
            parent_suite_label_index = None
            suite_value = None
            parent_suite_value = None
            is_selenide_test = False
            
            # Check if this is a container file (has children or childrenUuid)
            is_container = 'children' in data or 'childrenUuid' in data
            
            # CRITICAL: Check fullName and name fields FIRST for result files (not just containers)
            # This must happen BEFORE label processing to catch Selenide result files
            # fullName often has "Selenide." prepended, which is a reliable indicator
            if 'fullName' in data:
                full_name = data.get('fullName', '')
                if 'Selenide' in full_name or 'HomePageTests' in full_name or 'HomePage' in full_name:
                    is_selenide_test = True
            
            # Also check name field for Selenide indicators (works for both result files and containers)
            if 'name' in data:
                name_value = data.get('name', '')
                if 'Selenide' in name_value or 'HomePageTests' in name_value or 'HomePage' in name_value:
                    is_selenide_test = True
            
            # Also check name/fullName fields for container files (might contain "Surefire" or "HomePage")
            # CRITICAL: If a container has name="Surefire test", it's likely a parent container
            # that's causing Selenide tests to nest under it. We need to update it.
            if is_container:
                if 'name' in data:
                    container_name = data.get('name', '')
                    if 'Surefire' in container_name or 'HomePage' in container_name or 'HomePageTests' in container_name:
                        is_selenide_test = True
                    # Special case: If container name is "Surefire test", it's a parent container for Selenide
                    # We need to update it to "Selenide Tests" so tests don't nest under "Surefire test"
                    if container_name == 'Surefire test':
                        is_selenide_test = True
                if 'fullName' in data:
                    container_fullname = data.get('fullName', '')
                    if 'HomePage' in container_fullname or 'HomePageTests' in container_fullname:
                        is_selenide_test = True
            
            for i, label in enumerate(labels):
                label_name = label.get('name', '')
                label_value = label.get('value', '')
                
                if label_name == 'epic' and label_value == 'HomePage Tests':
                    epic_value = label_value
                    is_selenide_test = True  # Primary detection: epic is most reliable
                elif label_name == 'feature' and label_value == 'HomePage Navigation':
                    feature_value = label_value
                    # If we have the feature but not epic, still consider it Selenide
                    if not is_selenide_test:
                        is_selenide_test = True
                elif label_name == 'testClass' and 'HomePageTests' in label_value:
                    test_class_value = label_value
                    # Additional confirmation if we have epic
                    if epic_value == 'HomePage Tests':
                        is_selenide_test = True
                elif label_name == 'suite':
                    suite_label_index = i
                    suite_value = label_value
                    # For container files, check if suite contains "Surefire" (Selenide containers)
                    if is_container and 'Surefire' in label_value:
                        is_selenide_test = True
                    # Special case: If suite is "Surefire test", it's a parent container that needs updating
                    if is_container and label_value == 'Surefire test':
                        is_selenide_test = True
            
                elif label_name == 'parentSuite':
                    parent_suite_label_index = i
                    parent_suite_value = label_value
                    # For container files, check if parentSuite contains "Surefire" (Selenide containers)
                    if is_container and 'Surefire' in label_value:
                        is_selenide_test = True
            
            # After processing all labels, check if container has suite="Surefire test"
            # This needs to be checked after suite_value is set in the loop above
            if is_container and suite_value == 'Surefire test':
                is_selenide_test = True
            
            # If this is a Selenide test, update labels for proper grouping
            # Allure uses parentSuite for top-level grouping in Suites view
            if is_selenide_test:
                selenide_file_updated = False
                
                # Update or remove parentSuite to make Selenide tests appear as top-level suite
                if parent_suite_label_index is not None:
                    # Remove parentSuite label so tests appear at top level (like other frameworks)
                    labels.pop(parent_suite_label_index)
                    selenide_file_updated = True
                
                # Update suite label to "Selenide Tests"
                # CRITICAL: This must happen for ALL Selenide tests to appear as top-level suite
                suite_updated = False
                if suite_label_index is not None:
                    old_suite_value = labels[suite_label_index].get('value', '')
                    if old_suite_value != 'Selenide Tests':
                        labels[suite_label_index]['value'] = 'Selenide Tests'
                        suite_updated = True
                        selenide_file_updated = True
                else:
                    # Add suite label if it doesn't exist
                    labels.append({'name': 'suite', 'value': 'Selenide Tests'})
                    suite_updated = True
                    selenide_file_updated = True
                
                # Ensure suite label is set even if we missed it above
                if not suite_updated:
                    # Find and update any existing suite label
                    for i, label in enumerate(labels):
                        if label.get('name') == 'suite':
                            if label.get('value') != 'Selenide Tests':
                                labels[i]['value'] = 'Selenide Tests'
                                selenide_file_updated = True
                                break
                    else:
                        # No suite label found, add it
                        labels.append({'name': 'suite', 'value': 'Selenide Tests'})
                        selenide_file_updated = True
                
                # Also update fullName to include "Selenide" for additional grouping hints
                # This helps Allure group tests properly and makes them easier to find
                if 'fullName' in data:
                    full_name = data.get('fullName', '')
                    # Only update if "Selenide" is not already in the fullName
                    if 'Selenide' not in full_name:
                        # Prepend "Selenide." to the fullName (similar to how other frameworks are named)
                        data['fullName'] = f"Selenide.{full_name}"
                        selenide_file_updated = True
                elif 'name' in data:
                    # If fullName doesn't exist, create it from name
                    test_name = data.get('name', '')
                    if 'Selenide' not in test_name:
                        data['fullName'] = f"Selenide.{test_name}"
                        selenide_file_updated = True
                
                # For container files, also update the name field to match the suite label
                # Allure uses container file names for grouping in Suites view
                # This is critical - container file names control how tests are grouped
                # We want the container name to be "Selenide Tests" to match the suite label
                if is_container and 'name' in data:
                    container_name = data.get('name', '')
                    # If container name contains "Surefire" or doesn't contain "Selenide Tests", update it
                    if 'Surefire' in container_name or ('Selenide Tests' not in container_name and is_selenide_test):
                        # Set container name to "Selenide Tests" to match the suite label
                        # This ensures Allure groups them correctly in the Suites view
                        data['name'] = 'Selenide Tests'
                        selenide_file_updated = True
                
                # CRITICAL: Also ensure parentSuite is removed from container files
                # If a container has parentSuite="Surefire test", it will nest under "Surefire test"
                # We already removed parentSuite above, but double-check for containers
                if is_container and parent_suite_label_index is not None:
                    # Remove parentSuite from container to make it top-level
                    labels.pop(parent_suite_label_index)
                    selenide_file_updated = True
                
                if selenide_file_updated:
                    data['labels'] = labels
                    selenide_updated += 1
                    # Debug: Print info about updated files (only for first few to avoid spam)
                    if selenide_updated <= 5:
                        file_type = "container" if is_container else "result"
                        file_name = result_file.name
                        suite_val = next((l.get('value', '') for l in labels if l.get('name') == 'suite'), 'N/A')
                        parent_val = next((l.get('value', '') for l in labels if l.get('name') == 'parentSuite'), 'N/A')
                        name_val = data.get('name', 'N/A')
                        print(f"   🔧 Updated {file_type} file: {file_name[:50]}... (suite={suite_val}, parentSuite={parent_val}, name={name_val[:50] if name_val != 'N/A' else 'N/A'})")
        
        # Update historyId to include environment to prevent cross-environment deduplication
        # This allows the same test from different environments to be shown separately
        # Note: Allure will still group executions with the same historyId (same test+env) for trend tracking
        if 'fullName' in data:
            full_name = data.get('fullName', '')
            # Include environment in historyId to prevent cross-environment deduplication
            # Same test in different environments will have different historyIds
            new_history_id = hashlib.md5(f"{full_name}:{env}".encode()).hexdigest()
            data['historyId'] = new_history_id
        elif 'name' in data:
            # Fallback: use name if fullName doesn't exist
            test_name = data.get('name', '')
            new_history_id = hashlib.md5(f"{test_name}:{env}".encode()).hexdigest()
            data['historyId'] = new_history_id
        
        # Write back to file (always write to ensure environment labels/params are added)
        # We always add environment labels and parameters, so we should always write
        with open(result_file, 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=2, ensure_ascii=False)
        if name_fixed:
            name_fixed_count += 1
        
        processed += 1
        
    except Exception as e:
        print(f"❌ Error processing {result_file}: {e}", file=sys.stderr)
        errors += 1

# SECOND PASS: Find and update all containers with name="Surefire test" or suite="Surefire test"
# These are parent containers that create the hierarchy, even if they don't have epic/feature labels
# CRITICAL: Also find containers that have "Selenide Tests" as a child (via childrenUuid)
print("\n🔍 Second pass: Finding and updating parent 'Surefire test' containers...")
parent_containers_updated = 0
surefire_containers_found = 0

# First, find all container UUIDs that have "Selenide Tests" as name
selenide_container_uids = set()
for container_file in container_files:
    try:
        with open(container_file, 'r', encoding='utf-8') as f:
            data = json.load(f)
        if data.get('name') == 'Selenide Tests':
            selenide_container_uids.add(data.get('uuid', ''))
    except:
        pass

print(f"   📊 Found {len(selenide_container_uids)} container(s) with name='Selenide Tests'")
if len(selenide_container_uids) > 0:
    print(f"   🔍 Selenide container UUIDs: {list(selenide_container_uids)[:5]}...")  # Show first 5

print(f"   📊 Processing {len(container_files)} container files in second pass...")

for container_file in container_files:
    try:
        with open(container_file, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        # Check if this is a container with "Surefire test" in name or suite
        is_surefire_parent = False
        container_name = data.get('name', '')
        container_suite = None
        has_selenide_child = False
        
        # Check if this container has "Selenide Tests" as a child
        # Allure uses both 'children' (array of UUIDs) and 'childrenUuid' (array of UUIDs)
        # We need to check both fields
        children_uuids = []
        if 'childrenUuid' in data:
            children_uuids = data.get('childrenUuid', [])
        elif 'children' in data:
            # 'children' field contains array of UUIDs (strings)
            children_uuids = data.get('children', [])
        
        if children_uuids and any(uid in selenide_container_uids for uid in children_uuids):
            has_selenide_child = True
            is_surefire_parent = True
            surefire_containers_found += 1
            if surefire_containers_found <= 3:  # Debug: show first few
                matching_uuids = [uid for uid in children_uuids if uid in selenide_container_uids]
                print(f"   🔍 Found parent container with Selenide children: {container_file.name[:50]}... (has {len(matching_uuids)} Selenide children)")
        
        if container_name == 'Surefire test':
            is_surefire_parent = True
            surefire_containers_found += 1
            if surefire_containers_found <= 3:  # Debug: show first few
                print(f"   🔍 Found 'Surefire test' container: {container_file.name[:50]}...")
        
        if 'labels' in data:
            labels = data['labels']
            for label in labels:
                if label.get('name') == 'suite' and label.get('value') == 'Surefire test':
                    is_surefire_parent = True
                    container_suite = label.get('value')
                    surefire_containers_found += 1
                    break
                elif label.get('name') == 'parentSuite' and 'Surefire' in label.get('value', ''):
                    is_surefire_parent = True
                    surefire_containers_found += 1
                    break
        
        if is_surefire_parent:
            # Update this container to "Selenide Tests"
            updated = False
            if 'labels' in data:
                labels = data['labels']
                # Remove parentSuite labels first (need to do this in reverse to avoid index issues)
                labels_to_remove = []
                for i, label in enumerate(labels):
                    if label.get('name') == 'parentSuite':
                        labels_to_remove.append(i)
                    elif label.get('name') == 'suite' and label.get('value') == 'Surefire test':
                        labels[i]['value'] = 'Selenide Tests'
                        updated = True
                
                # Remove parentSuite labels (in reverse order to maintain indices)
                for i in reversed(labels_to_remove):
                    labels.pop(i)
                    updated = True
                
                # Ensure suite label exists and is "Selenide Tests"
                suite_found = False
                for label in labels:
                    if label.get('name') == 'suite':
                        suite_found = True
                        if label.get('value') != 'Selenide Tests':
                            label['value'] = 'Selenide Tests'
                            updated = True
                        break
                
                if not suite_found:
                    labels.append({'name': 'suite', 'value': 'Selenide Tests'})
                    updated = True
                
                if updated:
                    data['labels'] = labels
            
            # Update container name - ALWAYS update if it's a Surefire parent
            if container_name == 'Surefire test' or has_selenide_child or container_suite == 'Surefire test':
                if data.get('name') != 'Selenide Tests':
                    data['name'] = 'Selenide Tests'
                    updated = True
            
            # If this container has "Selenide Tests" as a child, we need to flatten the hierarchy
            # by removing the nested "Selenide Tests" container UUIDs from children/childrenUuid
            # This breaks the parent-child relationship so "Selenide Tests" appears as top-level
            if has_selenide_child:
                if parent_containers_updated < 3:  # Debug output
                    print(f"   🔧 Processing parent container with Selenide children: {container_file.name[:50]}...")
                # Check both 'childrenUuid' and 'children' fields
                if 'childrenUuid' in data:
                    children_uuids = data.get('childrenUuid', [])
                    original_count = len(children_uuids)
                    children_uuids = [uid for uid in children_uuids if uid not in selenide_container_uids]
                    if len(children_uuids) < original_count:
                        data['childrenUuid'] = children_uuids
                        updated = True
                elif 'children' in data:
                    children_uuids = data.get('children', [])
                    original_count = len(children_uuids)
                    children_uuids = [uid for uid in children_uuids if uid not in selenide_container_uids]
                    if len(children_uuids) < original_count:
                        data['children'] = children_uuids
                        updated = True
            
            if updated:
                with open(container_file, 'w', encoding='utf-8') as f:
                    json.dump(data, f, indent=2, ensure_ascii=False)
                
                # Rename the container file to match the new name (if it contains "Surefire")
                # This helps Allure recognize it as "Selenide Tests" container
                # Note: container_file is already a Path object from the glob
                if 'Surefire' in container_file.name or 'surefire' in container_file.name.lower():
                    new_filename = container_file.name.replace('Surefire', 'Selenide').replace('surefire', 'selenide')
                    if new_filename != container_file.name:
                        new_filepath = container_file.parent / new_filename
                        try:
                            container_file.rename(new_filepath)
                            # Update the container_files list reference for subsequent passes
                            container_file = new_filepath
                            if parent_containers_updated < 3:
                                print(f"   📝 Renamed container file: {new_filepath.name[:50]}...")
                        except Exception as e:
                            if parent_containers_updated < 3:
                                print(f"   ⚠️  Could not rename container file: {e}")
                
                parent_containers_updated += 1
                if parent_containers_updated <= 5:
                    name_val = data.get('name', 'N/A')
                    suite_val = next((l.get('value', '') for l in data.get('labels', []) if l.get('name') == 'suite'), 'N/A')
                    children_info = f", children={len(data.get('childrenUuid', []))}" if 'childrenUuid' in data else f", children={len(data.get('children', []))}" if 'children' in data else ""
                    print(f"   🔧 Updated parent container: {container_file.name[:50]}... (name={name_val}, suite={suite_val}{children_info})")
    except Exception as e:
        print(f"⚠️  Error processing container {container_file}: {e}", file=sys.stderr)

# THIRD PASS: Update nested "Selenide Tests" containers to remove parentSuite and make them top-level
# This breaks the hierarchy so "Selenide Tests" appears as a top-level suite
print("\n🔍 Third pass: Updating nested 'Selenide Tests' containers to be top-level...")
nested_selenide_updated = 0
selenide_containers_found = 0
for container_file in container_files:
    try:
        with open(container_file, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        # Find containers with name="Selenide Tests" that might have parentSuite
        if data.get('name') == 'Selenide Tests':
            selenide_containers_found += 1
            updated = False
            has_parent_suite = any(l.get('name') == 'parentSuite' for l in data.get('labels', []))
            if nested_selenide_updated < 3 and has_parent_suite:  # Debug output
                parent_suite_val = next((l.get('value', '') for l in data.get('labels', []) if l.get('name') == 'parentSuite'), 'N/A')
                print(f"   🔍 Found nested Selenide container with parentSuite: {container_file.name[:50]}... (parentSuite={parent_suite_val})")
            if 'labels' in data:
                labels = data['labels']
                # Remove parentSuite to make it top-level
                labels_to_remove = []
                for i, label in enumerate(labels):
                    if label.get('name') == 'parentSuite':
                        labels_to_remove.append(i)
                        updated = True
                
                # Remove parentSuite labels (in reverse order)
                for i in reversed(labels_to_remove):
                    labels.pop(i)
                
                # Ensure suite label is "Selenide Tests"
                suite_found = False
                for label in labels:
                    if label.get('name') == 'suite':
                        suite_found = True
                        if label.get('value') != 'Selenide Tests':
                            label['value'] = 'Selenide Tests'
                            updated = True
                        break
                
                if not suite_found:
                    labels.append({'name': 'suite', 'value': 'Selenide Tests'})
                    updated = True
                
                if updated:
                    data['labels'] = labels
            
            if updated:
                with open(container_file, 'w', encoding='utf-8') as f:
                    json.dump(data, f, indent=2, ensure_ascii=False)
                nested_selenide_updated += 1
                if nested_selenide_updated <= 3:
                    suite_val = next((l.get('value', '') for l in data.get('labels', []) if l.get('name') == 'suite'), 'N/A')
                    parent_val = next((l.get('value', '') for l in data.get('labels', []) if l.get('name') == 'parentSuite'), 'N/A')
                    print(f"   🔧 Updated nested Selenide container: {container_file.name[:50]}... (suite={suite_val}, parentSuite={parent_val})")
    except Exception as e:
        print(f"⚠️  Error processing nested Selenide container {container_file}: {e}", file=sys.stderr)

if surefire_containers_found > 0:
    print(f"   📊 Found {surefire_containers_found} container(s) with 'Surefire test'")
if parent_containers_updated > 0:
    print(f"✅ Updated {parent_containers_updated} parent 'Surefire test' container(s) to 'Selenide Tests'")
else:
    print(f"   ℹ️  No parent 'Surefire test' containers found to update")
print(f"   📊 Found {selenide_containers_found} container(s) with name='Selenide Tests' in third pass")
if nested_selenide_updated > 0:
    print(f"✅ Updated {nested_selenide_updated} nested 'Selenide Tests' container(s) to be top-level")
else:
    if selenide_containers_found > 0:
        print(f"   ℹ️  Found {selenide_containers_found} 'Selenide Tests' container(s) but none needed updating (parentSuite already removed)")

# Create environment.properties file for Allure report ENVIRONMENT section
env_properties_file = Path(results_dir) / "environment.properties"
envs_found = set()
for result_file in result_files:
    try:
        with open(result_file, 'r', encoding='utf-8') as f:
            data = json.load(f)
        if 'labels' in data:
            for label in data['labels']:
                if label.get('name') == 'environment':
                    envs_found.add(label.get('value', 'unknown'))
    except:
        pass

# Write environment.properties file
with open(env_properties_file, 'w', encoding='utf-8') as f:
    f.write("# Combined Allure Report Environment Information\n")
    f.write(f"Environments={', '.join(sorted(envs_found)) if envs_found else 'combined'}\n")
    f.write("Report.Type=Combined\n")
    f.write("Execution.Type=CI/CD\n")
    if len(envs_found) > 1:
        f.write("Multi.Environment=true\n")
    else:
        f.write("Multi.Environment=false\n")

print(f"\n✅ Processing complete!")
print(f"   Processed: {processed} files")
print(f"   Skipped (already labeled): {skipped} files")
print(f"   Errors: {errors} files")
print(f"   Selenide tests updated: {selenide_updated} files")
if name_fixed_count > 0:
    print(f"   Files with invalid 'name' field fixed: {name_fixed_count} files")
print(f"   Total: {len(all_files)} files ({len(result_files)} result files, {len(container_files)} container files)")
print(f"   Environments found: {', '.join(sorted(envs_found)) if envs_found else 'none'}")

sys.exit(0)
PYTHON_SCRIPT

if [ $? -ne 0 ]; then
    echo "⚠️  Error running Python script"
    exit 1
fi

