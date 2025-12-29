#!/bin/bash
# scripts/test/analyze-allure-containers.sh
# Analyzes Allure results to diagnose Suites tab issues
# Can work with downloaded artifacts or local allure-results directories
#
# Usage:
#   ./scripts/test/analyze-allure-containers.sh [results-dir]
#   ./scripts/test/analyze-allure-containers.sh allure-results-combined
#   ./scripts/test/analyze-allure-containers.sh /path/to/downloaded/artifact

set -e

RESULTS_DIR="${1:-allure-results-combined}"

if [ ! -d "$RESULTS_DIR" ]; then
    echo "❌ Error: Results directory not found: $RESULTS_DIR"
    echo ""
    echo "Usage:"
    echo "  ./scripts/test/analyze-allure-containers.sh [results-dir]"
    echo ""
    echo "To download from a recent pipeline run:"
    echo "  gh run download <run-id> --name 'allure-results-combined-all-environments'"
    echo "  ./scripts/test/analyze-allure-containers.sh allure-results-combined-all-environments"
    exit 1
fi

echo "═══════════════════════════════════════════════════════════════"
echo "🔍 Allure Container Analysis"
echo "═══════════════════════════════════════════════════════════════"
echo "   Results directory: $RESULTS_DIR"
echo ""

python3 <<'PYTHON_SCRIPT'
import json
import os
import sys
from pathlib import Path
from collections import defaultdict

results_dir = os.environ.get('RESULTS_DIR', 'allure-results-combined')

if not os.path.isdir(results_dir):
    print(f"❌ Error: Results directory not found: {results_dir}")
    sys.exit(1)

# Find all files
result_files = list(Path(results_dir).glob("*-result.json"))
container_files = list(Path(results_dir).glob("*-container.json"))

print(f"📊 File Counts:")
print(f"   Result files: {len(result_files)}")
print(f"   Container files: {len(container_files)}")
print(f"")

if len(result_files) == 0:
    print("⚠️  WARNING: No result files found!")
    print("   This directory may not contain Allure results")
    sys.exit(1)

# Analyze result files
print("═══════════════════════════════════════════════════════════════")
print("📋 Result Files Analysis")
print("═══════════════════════════════════════════════════════════════")

suite_counts = defaultdict(int)
env_counts = defaultdict(int)
files_without_suite = []
files_without_env = []
files_without_uuid = []

for result_file in result_files:
    try:
        with open(result_file, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        # Check for suite label
        has_suite = False
        suite_name = None
        env_name = None
        has_uuid = 'uuid' in data and data.get('uuid')
        
        if 'labels' in data:
            for label in data['labels']:
                if label.get('name') == 'suite':
                    suite_name = label.get('value', '')
                    suite_counts[suite_name] += 1
                    has_suite = True
                    break
                elif label.get('name') == 'environment':
                    env_name = label.get('value', '')
                    env_counts[env_name] += 1
        
        if not has_suite:
            files_without_suite.append(result_file.name)
        if not env_name:
            files_without_env.append(result_file.name)
        if not has_uuid:
            files_without_uuid.append(result_file.name)
    except Exception as e:
        print(f"⚠️  Error reading {result_file.name}: {e}")

print(f"📊 Suite Distribution:")
if suite_counts:
    for suite, count in sorted(suite_counts.items(), key=lambda x: -x[1]):
        print(f"   - {suite}: {count} test(s)")
else:
    print("   ⚠️  No suite labels found in any result files!")

print(f"")
print(f"📊 Environment Distribution:")
if env_counts:
    for env, count in sorted(env_counts.items(), key=lambda x: -x[1]):
        print(f"   - {env}: {count} test(s)")
else:
    print("   ⚠️  No environment labels found in any result files!")

if files_without_suite:
    print(f"")
    print(f"⚠️  WARNING: {len(files_without_suite)} result file(s) without suite labels")
    if len(files_without_suite) <= 5:
        for f in files_without_suite:
            print(f"   - {f}")

if files_without_env:
    print(f"")
    print(f"⚠️  WARNING: {len(files_without_env)} result file(s) without environment labels")
    if len(files_without_env) <= 5:
        for f in files_without_env:
            print(f"   - {f}")

if files_without_uuid:
    print(f"")
    print(f"⚠️  WARNING: {len(files_without_uuid)} result file(s) without UUID")
    if len(files_without_uuid) <= 5:
        for f in files_without_uuid:
            print(f"   - {f}")

# Analyze container files
print(f"")
print("═══════════════════════════════════════════════════════════════")
print("📦 Container Files Analysis")
print("═══════════════════════════════════════════════════════════════")

if len(container_files) == 0:
    print("❌ ERROR: No container files found!")
    print("")
    print("This is the root cause of the Suites tab issue.")
    print("Container files are required for Allure's Suites tab to display frameworks.")
    print("")
    print("Possible causes:")
    print("  1. Container creation script did not run")
    print("  2. Container creation script failed silently")
    print("  3. Container files were deleted after creation")
    print("  4. Container files are in a different directory")
    sys.exit(1)

print(f"✅ Found {len(container_files)} container file(s)")
print(f"")

# Analyze container structure
container_by_type = {'top-level': [], 'env-specific': [], 'unknown': []}
container_issues = []

for container_file in container_files:
    try:
        with open(container_file, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        container_name = data.get('name', 'N/A')
        container_uuid = data.get('uuid', 'N/A')
        children = data.get('children', [])
        children_count = len(children)
        labels = data.get('labels', [])
        
        # Determine container type
        has_env_label = any(l.get('name') == 'environment' for l in labels)
        has_parent_suite = any(l.get('name') == 'parentSuite' for l in labels)
        suite_label = next((l.get('value') for l in labels if l.get('name') == 'suite'), None)
        
        # Check for environment suffix in name (e.g., "[DEV]", "[TEST]", "[PROD]")
        has_env_suffix = any(env in container_name for env in ['[DEV]', '[TEST]', '[PROD]'])
        
        if has_env_suffix or has_env_label:
            container_type = 'env-specific'
        elif not has_parent_suite and children_count > 0:
            # Top-level containers typically have env containers as children
            # Check if children are container UUIDs (not result UUIDs)
            container_type = 'top-level'
        else:
            container_type = 'unknown'
        
        container_by_type[container_type].append({
            'file': container_file.name,
            'name': container_name,
            'uuid': container_uuid,
            'children_count': children_count,
            'suite': suite_label,
            'has_env': has_env_label,
            'has_parent_suite': has_parent_suite
        })
        
        # Validate container structure
        issues = []
        if not container_uuid or container_uuid == 'N/A':
            issues.append("Missing UUID")
        if not container_name or container_name == 'N/A':
            issues.append("Missing name")
        if not suite_label:
            issues.append("Missing suite label")
        if children_count == 0:
            issues.append("No children (empty container)")
        
        if issues:
            container_issues.append({
                'file': container_file.name,
                'name': container_name,
                'issues': issues
            })
    except Exception as e:
        print(f"⚠️  Error reading container {container_file.name}: {e}")

# Report container types
print(f"📊 Container Type Distribution:")
print(f"   Top-level containers: {len(container_by_type['top-level'])}")
print(f"   Environment-specific containers: {len(container_by_type['env-specific'])}")
print(f"   Unknown/Other containers: {len(container_by_type['unknown'])}")
print(f"")

# Show top-level containers
if container_by_type['top-level']:
    print(f"🔍 Top-Level Containers:")
    for container in container_by_type['top-level'][:10]:
        print(f"   - {container['name']} (suite: {container['suite']}, children: {container['children_count']})")
    if len(container_by_type['top-level']) > 10:
        print(f"   ... and {len(container_by_type['top-level']) - 10} more")
    print(f"")

# Show env-specific containers
if container_by_type['env-specific']:
    print(f"🔍 Environment-Specific Containers:")
    for container in container_by_type['env-specific'][:10]:
        env_info = "has env label" if container['has_env'] else "no env label"
        parent_info = f"parentSuite: {container['has_parent_suite']}"
        print(f"   - {container['name']} (suite: {container['suite']}, children: {container['children_count']}, {env_info}, {parent_info})")
    if len(container_by_type['env-specific']) > 10:
        print(f"   ... and {len(container_by_type['env-specific']) - 10} more")
    print(f"")

# Report issues
if container_issues:
    print(f"⚠️  Container Structure Issues Found:")
    for issue in container_issues:
        print(f"   - {issue['file']} ({issue['name']}): {', '.join(issue['issues'])}")
    print(f"")

# Validate Allure requirements
print("═══════════════════════════════════════════════════════════════")
print("✅ Allure Requirements Validation")
print("═══════════════════════════════════════════════════════════════")

requirements_met = True

# Requirement 1: Container files exist
if len(container_files) == 0:
    print("❌ FAIL: No container files found")
    requirements_met = False
else:
    print(f"✅ PASS: {len(container_files)} container file(s) found")

# Requirement 2: Top-level containers exist
if len(container_by_type['top-level']) == 0:
    print("❌ FAIL: No top-level containers found")
    print("   Allure's Suites tab requires top-level containers for each framework")
    requirements_met = False
else:
    print(f"✅ PASS: {len(container_by_type['top-level'])} top-level container(s) found")

# Requirement 3: Environment-specific containers exist
if len(container_by_type['env-specific']) == 0:
    print("⚠️  WARNING: No environment-specific containers found")
    print("   This may be OK if only one environment ran, but multi-env reports need these")
else:
    print(f"✅ PASS: {len(container_by_type['env-specific'])} environment-specific container(s) found")

# Requirement 4: Containers have suite labels
containers_without_suite = [c for c in container_by_type['top-level'] + container_by_type['env-specific'] if not c['suite']]
if containers_without_suite:
    print(f"❌ FAIL: {len(containers_without_suite)} container(s) without suite labels")
    requirements_met = False
else:
    print("✅ PASS: All containers have suite labels")

# Requirement 5: Top-level containers have children
top_level_without_children = [c for c in container_by_type['top-level'] if c['children_count'] == 0]
if top_level_without_children:
    print(f"❌ FAIL: {len(top_level_without_children)} top-level container(s) have no children")
    requirements_met = False
else:
    print("✅ PASS: All top-level containers have children")

# Requirement 6: Env-specific containers have children
env_without_children = [c for c in container_by_type['env-specific'] if c['children_count'] == 0]
if env_without_children:
    print(f"⚠️  WARNING: {len(env_without_children)} environment-specific container(s) have no children")
else:
    print("✅ PASS: All environment-specific containers have children")

print(f"")
if requirements_met:
    print("✅ All critical requirements met!")
    print("")
    print("If containers still don't appear in Suites tab, possible causes:")
    print("  1. Allure version compatibility issue")
    print("  2. Container files not included in report generation")
    print("  3. Allure report generation timing issue")
    print("  4. Browser cache issue (try hard refresh)")
else:
    print("❌ Some requirements are not met - this is likely the root cause")
    print("")
    print("Next steps:")
    print("  1. Fix missing containers or structure issues")
    print("  2. Re-run container creation script")
    print("  3. Verify containers are created correctly")

PYTHON_SCRIPT

echo ""
echo "═══════════════════════════════════════════════════════════════"
echo "✅ Analysis complete"
echo "═══════════════════════════════════════════════════════════════"

