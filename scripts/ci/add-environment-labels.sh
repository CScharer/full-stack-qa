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

# Find all result files (exclude .env.* marker files)
result_files = [f for f in Path(results_dir).glob("*-result.json") if not f.name.startswith(".env.")]
print(f"📊 Processing {len(result_files)} result files...")

processed = 0
skipped = 0
errors = 0

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

for result_file in result_files:
    try:
        with open(result_file, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        # Determine environment
        env = "unknown"
        
        # First, check if already has environment label
        if 'labels' in data:
            for label in data['labels']:
                if label.get('name') == 'environment':
                    env = label.get('value', 'unknown')
                    skipped += 1
                    break
        
        # If no environment label, try to determine from file mapping or labels
        if env == "unknown":
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
        # Selenide tests have epic="HomePage Tests" and testClass containing "HomePageTests"
        # They currently have parentSuite="Surefire suite" and suite="Surefire test" which groups them incorrectly
        if 'labels' in data:
            labels = data.get('labels', [])
            epic_value = None
            test_class_value = None
            suite_label_index = None
            parent_suite_label_index = None
            
            for i, label in enumerate(labels):
                if label.get('name') == 'epic' and label.get('value') == 'HomePage Tests':
                    epic_value = label.get('value')
                if label.get('name') == 'testClass' and 'HomePageTests' in label.get('value', ''):
                    test_class_value = label.get('value')
                if label.get('name') == 'suite':
                    suite_label_index = i
                if label.get('name') == 'parentSuite':
                    parent_suite_label_index = i
            
            # If this is a Selenide test (has HomePage Tests epic and HomePageTests class)
            # Update both parentSuite and suite labels to "Selenide Tests" for proper grouping
            # Allure uses parentSuite for top-level grouping in Suites view
            if epic_value == 'HomePage Tests' and test_class_value and 'HomePageTests' in test_class_value:
                # Update or remove parentSuite to make Selenide tests appear as top-level suite
                if parent_suite_label_index is not None:
                    # Remove parentSuite label so tests appear at top level (like other frameworks)
                    labels.pop(parent_suite_label_index)
                # Update suite label to "Selenide Tests"
                if suite_label_index is not None:
                    labels[suite_label_index]['value'] = 'Selenide Tests'
                else:
                    # Add suite label if it doesn't exist
                    labels.append({'name': 'suite', 'value': 'Selenide Tests'})
                data['labels'] = labels
                
                # Also update fullName to include "Selenide" for additional grouping hints
                # This helps Allure group tests properly and makes them easier to find
                if 'fullName' in data:
                    full_name = data.get('fullName', '')
                    # Only update if "Selenide" is not already in the fullName
                    if 'Selenide' not in full_name:
                        # Prepend "Selenide." to the fullName (similar to how other frameworks are named)
                        data['fullName'] = f"Selenide.{full_name}"
                elif 'name' in data:
                    # If fullName doesn't exist, create it from name
                    test_name = data.get('name', '')
                    if 'Selenide' not in test_name:
                        data['fullName'] = f"Selenide.{test_name}"
        
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
        
        # Write back to file
        with open(result_file, 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=2, ensure_ascii=False)
        
        processed += 1
        
    except Exception as e:
        print(f"❌ Error processing {result_file}: {e}", file=sys.stderr)
        errors += 1

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
print(f"   Total: {len(result_files)} files")
print(f"   Environments found: {', '.join(sorted(envs_found)) if envs_found else 'none'}")

sys.exit(0)
PYTHON_SCRIPT

if [ $? -ne 0 ]; then
    echo "⚠️  Error running Python script"
    exit 1
fi

