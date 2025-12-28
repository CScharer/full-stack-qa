#!/bin/bash
# Test script to verify Selenide container fix locally
# This allows testing the fix without running full CI pipeline

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
TEST_DIR="/tmp/selenide-fix-test"
ADD_LABELS_SCRIPT="$PROJECT_ROOT/scripts/ci/add-environment-labels.sh"

echo "🧪 Testing Selenide Container Fix"
echo "=================================="
echo ""

# Clean up previous test
rm -rf "$TEST_DIR"
mkdir -p "$TEST_DIR/allure-results"

create_sample_data() {
    echo "   Creating sample container files for testing..."
    
    # Create a parent "Surefire test" container with "Selenide Tests" as child (must end with -container.json)
    cat > "$TEST_DIR/allure-results/parent-surefire-uuid-container.json" << 'EOF'
{
  "uuid": "parent-surefire-uuid",
  "name": "Surefire test",
  "children": [
    "selenide-container-uuid-1",
    "selenide-container-uuid-2",
    "other-test-uuid"
  ],
  "labels": [
    {"name": "suite", "value": "Surefire test"}
  ]
}
EOF

    # Create nested "Selenide Tests" containers (must end with -container.json for glob pattern)
    cat > "$TEST_DIR/allure-results/selenide-container-uuid-1-container.json" << 'EOF'
{
  "uuid": "selenide-container-uuid-1",
  "name": "Selenide Tests",
  "children": [
    "test-uuid-1",
    "test-uuid-2"
  ],
  "labels": [
    {"name": "suite", "value": "Selenide Tests"},
    {"name": "parentSuite", "value": "Surefire test"}
  ]
}
EOF

    cat > "$TEST_DIR/allure-results/selenide-container-uuid-2-container.json" << 'EOF'
{
  "uuid": "selenide-container-uuid-2",
  "name": "Selenide Tests",
  "children": [
    "test-uuid-3"
  ],
  "labels": [
    {"name": "suite", "value": "Selenide Tests"},
    {"name": "parentSuite", "value": "Surefire test"}
  ]
}
EOF

    # Create a Selenide test result file
    cat > "$TEST_DIR/allure-results/selenide-test-result.json" << 'EOF'
{
  "uuid": "test-uuid-1",
  "name": "testHomePageLoads",
  "fullName": "com.cjs.qa.junit.tests.HomePageTests.testHomePageLoads",
  "labels": [
    {"name": "epic", "value": "HomePage Tests"},
    {"name": "feature", "value": "HomePage Navigation"},
    {"name": "testClass", "value": "com.cjs.qa.junit.tests.HomePageTests"},
    {"name": "suite", "value": "Surefire test"},
    {"name": "parentSuite", "value": "Surefire suite"}
  ],
  "status": "passed"
}
EOF

    echo "   ✅ Created sample data"
}

echo "📥 Step 1: Downloading sample Selenide results..."
echo "   (This will download from the latest CI run)"
echo ""

# Try to download from latest run
LATEST_RUN=$(gh run list --limit 1 --json databaseId --jq '.[0].databaseId' 2>/dev/null || echo "")
if [ -n "$LATEST_RUN" ]; then
    echo "   Found latest run: $LATEST_RUN"
    cd "$TEST_DIR"
    
    # Download artifact using API - try multiple patterns
    ARTIFACT_ID=$(gh api repos/CScharer/full-stack-qa/actions/artifacts --jq ".artifacts[] | select(.name == \"selenide-results-dev\" and .workflow_run.id == $LATEST_RUN) | .id" 2>/dev/null | head -1)
    
    # If not found, try without environment suffix
    if [ -z "$ARTIFACT_ID" ]; then
        ARTIFACT_ID=$(gh api repos/CScharer/full-stack-qa/actions/artifacts --jq ".artifacts[] | select(.name | startswith(\"selenide-results\")) | .id" 2>/dev/null | head -1)
    fi
    
    if [ -n "$ARTIFACT_ID" ]; then
        echo "   Downloading artifact ID: $ARTIFACT_ID"
        curl -sL -H "Authorization: token $(gh auth token)" \
            "https://api.github.com/repos/CScharer/full-stack-qa/actions/artifacts/$ARTIFACT_ID/zip" \
            -o artifact.zip
        if [ -f artifact.zip ] && [ -s artifact.zip ]; then
            unzip -q artifact.zip -d extracted 2>/dev/null || true
            # Find and copy all JSON files (results, containers, etc.)
            find extracted -name "*.json" -type f -exec cp {} allure-results/ \; 2>/dev/null || true
            COUNT=$(find allure-results -name "*.json" -type f | wc -l | tr -d ' ')
            echo "   ✅ Downloaded $COUNT JSON files from CI artifact"
            
            # Show what we got
            echo "   📊 Artifact contents:"
            echo "      - Result files: $(find allure-results -name "*-result.json" | wc -l | tr -d ' ')"
            echo "      - Container files: $(find allure-results -name "*-container.json" | wc -l | tr -d ' ')"
        else
            echo "   ⚠️  Artifact download failed, creating sample data..."
            create_sample_data
        fi
    else
        echo "   ⚠️  Could not find artifact, creating sample data..."
        create_sample_data
    fi
else
    echo "   ⚠️  Could not get latest run, creating sample data..."
    create_sample_data
fi

echo ""
echo "📊 Step 2: Analyzing current state..."
echo ""

# Count containers
CONTAINER_COUNT=$(find "$TEST_DIR/allure-results" -name "*container.json" | wc -l | tr -d ' ')
RESULT_COUNT=$(find "$TEST_DIR/allure-results" -name "*-result.json" | wc -l | tr -d ' ')

echo "   Found:"
echo "   - Container files: $CONTAINER_COUNT"
echo "   - Result files: $RESULT_COUNT"

# Show current structure
echo ""
echo "   Current container structure:"
find "$TEST_DIR/allure-results" -name "*container.json" -exec sh -c 'echo "   - $(basename {}): name=$(jq -r .name {} 2>/dev/null), children=$(jq ".children | length" {} 2>/dev/null)"' \; 2>/dev/null | head -10

echo ""
echo "🔧 Step 3: Running add-environment-labels.sh script..."
echo ""

cd "$PROJECT_ROOT"
chmod +x "$ADD_LABELS_SCRIPT"

# Run the script with test directory
# Script expects: add-environment-labels.sh <results_dir> <source_dir>
bash "$ADD_LABELS_SCRIPT" "$TEST_DIR/allure-results" "$TEST_DIR" 2>&1 | tee "$TEST_DIR/script-output.log"

echo ""
echo "📊 Step 4: Analyzing changes..."
echo ""

# Check if parent container was updated
PARENT_FILE=$(find "$TEST_DIR/allure-results" -name "*parent*container.json" | head -1)
if [ -n "$PARENT_FILE" ] && [ -f "$PARENT_FILE" ]; then
    PARENT_NAME=$(jq -r '.name' "$PARENT_FILE" 2>/dev/null)
    PARENT_SUITE=$(jq -r '.labels[]? | select(.name=="suite") | .value' "$PARENT_FILE" 2>/dev/null)
    PARENT_CHILDREN=$(jq '.children | length' "$PARENT_FILE" 2>/dev/null)
    
    echo "   Parent container:"
    echo "   - Name: $PARENT_NAME"
    echo "   - Suite: $PARENT_SUITE"
    echo "   - Children count: $PARENT_CHILDREN"
    
    if [ "$PARENT_NAME" = "Selenide Tests" ]; then
        echo "   ✅ Parent container name updated correctly!"
    else
        echo "   ❌ Parent container name NOT updated (still: $PARENT_NAME)"
    fi
    
    if [ "$PARENT_SUITE" = "Selenide Tests" ]; then
        echo "   ✅ Parent container suite updated correctly!"
    else
        echo "   ❌ Parent container suite NOT updated (still: $PARENT_SUITE)"
    fi
fi

# Check if nested containers were updated
echo ""
echo "   Nested Selenide containers:"
for container in "$TEST_DIR/allure-results"/*selenide*container.json; do
    if [ -f "$container" ]; then
        CONTAINER_NAME=$(jq -r '.name' "$container" 2>/dev/null)
        CONTAINER_SUITE=$(jq -r '.labels[]? | select(.name=="suite") | .value' "$container" 2>/dev/null)
        HAS_PARENT_SUITE=$(jq -r '.labels[]? | select(.name=="parentSuite") | .value' "$container" 2>/dev/null || echo "none")
        
        echo "   - $(basename $container):"
        echo "     Name: $CONTAINER_NAME"
        echo "     Suite: $CONTAINER_SUITE"
        echo "     ParentSuite: $HAS_PARENT_SUITE"
        
        if [ "$HAS_PARENT_SUITE" = "none" ] || [ -z "$HAS_PARENT_SUITE" ]; then
            echo "     ✅ ParentSuite removed!"
        else
            echo "     ❌ ParentSuite still present: $HAS_PARENT_SUITE"
        fi
    fi
done

# Check script output
echo ""
echo "📋 Step 5: Script output summary..."
echo ""
grep -E "Second pass|Third pass|Found.*container|Updated.*container|Selenide" "$TEST_DIR/script-output.log" | head -20 || echo "   (No relevant output found)"

echo ""
echo "✅ Test complete!"
echo ""
echo "📁 Test files saved in: $TEST_DIR"
echo "📄 Script output: $TEST_DIR/script-output.log"
echo ""
echo "To inspect manually:"
echo "  cd $TEST_DIR/allure-results"
echo "  jq . *-container.json"

