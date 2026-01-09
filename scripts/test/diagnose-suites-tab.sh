#!/bin/bash
# Diagnostic script to check why only Playwright tests appear in Suites tab
# Usage: ./scripts/test/diagnose-suites-tab.sh [results-dir]

set -e

RESULTS_DIR="${1:-allure-results-combined}"

echo "🔍 Diagnosing Suites Tab Issue"
echo "================================"
echo ""
echo "Results directory: $RESULTS_DIR"
echo ""

if [ ! -d "$RESULTS_DIR" ]; then
    echo "❌ Results directory not found: $RESULTS_DIR"
    exit 1
fi

# Count result files by framework (check suite labels)
echo "📊 Framework Distribution (by suite label):"
echo ""

FRAMEWORKS=("Cypress Tests" "Playwright Tests" "Robot Framework Tests" "Vibium Tests" "Selenide Tests" "Smoke Tests" "Surefire test")

for framework in "${FRAMEWORKS[@]}"; do
    count=$(find "$RESULTS_DIR" -name "*-result.json" -exec grep -l "\"suite\".*\"$framework\"" {} \; 2>/dev/null | wc -l | tr -d ' ')
    echo "   - $framework: $count test(s)"
done

echo ""

# Count container files
echo "📦 Container Files:"
CONTAINER_COUNT=$(find "$RESULTS_DIR" -name "*-container.json" 2>/dev/null | wc -l | tr -d ' ')
echo "   Total container files: $CONTAINER_COUNT"

if [ "$CONTAINER_COUNT" -eq 0 ]; then
    echo "   ⚠️  WARNING: No container files found!"
    echo "   This is likely why only Playwright tests appear in Suites tab."
    echo "   Container files are required for Allure's Suites tab to display frameworks."
    echo ""
    echo "   Possible causes:"
    echo "     1. Container creation script did not run"
    echo "     2. Container creation script failed silently"
    echo "     3. Container files were deleted after creation"
    exit 1
fi

echo ""

# Analyze container structure
echo "📋 Container Structure:"
echo ""

# Count top-level vs env-specific containers
TOP_LEVEL=0
ENV_SPECIFIC=0

while IFS= read -r container_file; do
    if [ -z "$container_file" ]; then continue; fi
    
    name=$(jq -r '.name // "N/A"' "$container_file" 2>/dev/null || echo "N/A")
    has_env_suffix=$(echo "$name" | grep -E '\[DEV\]|\[TEST\]|\[PROD\]' || echo "")
    
    if [ -n "$has_env_suffix" ]; then
        ENV_SPECIFIC=$((ENV_SPECIFIC + 1))
    else
        TOP_LEVEL=$((TOP_LEVEL + 1))
    fi
done < <(find "$RESULTS_DIR" -name "*-container.json" 2>/dev/null)

echo "   Top-level containers: $TOP_LEVEL"
echo "   Environment-specific containers: $ENV_SPECIFIC"
echo ""

# Show container details
echo "📋 Container Details (first 20):"
find "$RESULTS_DIR" -name "*-container.json" 2>/dev/null | head -20 | while read -r container_file; do
    name=$(jq -r '.name // "N/A"' "$container_file" 2>/dev/null || echo "N/A")
    children_count=$(jq -r '.children | length' "$container_file" 2>/dev/null || echo "0")
    suite_label=$(jq -r '.labels[]? | select(.name == "suite") | .value' "$container_file" 2>/dev/null || echo "N/A")
    
    echo "   - $(basename "$container_file"):"
    echo "     Name: $name"
    echo "     Suite: $suite_label"
    echo "     Children: $children_count"
done

echo ""

# Check for suite labels in result files
echo "🔍 Suite Label Distribution in Result Files:"
echo ""

for framework in "${FRAMEWORKS[@]}"; do
    # Count files with this suite label
    count=$(find "$RESULTS_DIR" -name "*-result.json" -exec grep -l "\"suite\".*\"$framework\"" {} \; 2>/dev/null | wc -l | tr -d ' ')
    
    if [ "$count" -gt 0 ]; then
        # Check if there's a corresponding container
        has_container=$(find "$RESULTS_DIR" -name "*-container.json" -exec grep -l "\"suite\".*\"$framework\"" {} \; 2>/dev/null | wc -l | tr -d ' ')
        
        if [ "$has_container" -eq 0 ]; then
            echo "   ⚠️  $framework: $count result(s) but NO container file!"
        else
            echo "   ✅ $framework: $count result(s), $has_container container(s)"
        fi
    fi
done

echo ""

# Summary
echo "═══════════════════════════════════════════════════════════════"
echo "📊 Summary"
echo "═══════════════════════════════════════════════════════════════"
echo ""

TOTAL_RESULTS=$(find "$RESULTS_DIR" -name "*-result.json" 2>/dev/null | wc -l | tr -d ' ')
echo "Total result files: $TOTAL_RESULTS"
echo "Total container files: $CONTAINER_COUNT"
echo ""

if [ "$CONTAINER_COUNT" -eq 0 ]; then
    echo "❌ ISSUE FOUND: No container files exist!"
    echo "   This is why only Playwright tests appear in Suites tab."
    echo "   Solution: Run create-framework-containers.sh script"
elif [ "$TOP_LEVEL" -eq 0 ]; then
    echo "⚠️  ISSUE FOUND: No top-level containers!"
    echo "   Allure's Suites tab requires top-level containers."
    echo "   Solution: Check create-framework-containers.sh script"
else
    echo "✅ Container structure looks correct"
    echo "   If Suites tab still shows only Playwright, check:"
    echo "   1. Are all frameworks actually running in the pipeline?"
    echo "   2. Are results being converted correctly?"
    echo "   3. Is the Allure report being generated correctly?"
fi

echo ""
