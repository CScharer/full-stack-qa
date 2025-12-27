#!/bin/bash
# Create Allure Categories Configuration File
# Usage: ./scripts/ci/create-allure-categories.sh <results-dir>
#
# Arguments:
#   results-dir  - Directory where Allure results are stored (default: allure-results-combined)
#
# Examples:
#   ./scripts/ci/create-allure-categories.sh allure-results-combined

set -e

RESULTS_DIR="${1:-allure-results-combined}"

# Create directory if it doesn't exist
mkdir -p "$RESULTS_DIR"

# Create categories.json file
# This defines custom test categories for the Allure report
# Note: Allure Categories section primarily shows defects (failed/broken tests)
# If all tests pass, the section may appear empty
cat > "$RESULTS_DIR/categories.json" << 'EOF'
[
  {
    "name": "Product Defects",
    "matchedStatuses": ["failed"],
    "messageRegex": ".*"
  },
  {
    "name": "Test Defects",
    "matchedStatuses": ["broken"],
    "messageRegex": ".*"
  },
  {
    "name": "Skipped Tests",
    "matchedStatuses": ["skipped"],
    "messageRegex": ".*"
  }
]
EOF

# Verify file was created
if [ ! -f "$RESULTS_DIR/categories.json" ]; then
    echo "❌ Error: Failed to create categories.json file"
    exit 1
fi

# Verify JSON is valid
if ! python3 -m json.tool "$RESULTS_DIR/categories.json" > /dev/null 2>&1; then
    echo "❌ Error: categories.json contains invalid JSON"
    exit 1
fi

echo "✅ Created Allure categories file: $RESULTS_DIR/categories.json"
echo "   Categories defined:"
echo "     - Product Defects (failed tests)"
echo "     - Test Defects (broken tests)"
echo "     - Skipped Tests"
echo "     - Passed Tests"

