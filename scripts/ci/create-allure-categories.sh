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
  },
  {
    "name": "Passed Tests",
    "matchedStatuses": ["passed"],
    "messageRegex": ".*"
  }
]
EOF

echo "✅ Created Allure categories file: $RESULTS_DIR/categories.json"
echo "   Categories defined:"
echo "     - Product Defects (failed tests)"
echo "     - Test Defects (broken tests)"
echo "     - Skipped Tests"
echo "     - Passed Tests"

