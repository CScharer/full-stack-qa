#!/bin/bash

# Pre-commit hook to validate test data JSON files
# This script is called by pre-commit framework

set -e

# Get list of staged JSON files in test-data directory
STAGED_FILES=$(git diff --cached --name-only --diff-filter=ACM | grep "^test-data/.*\.json$" || true)

if [ -z "$STAGED_FILES" ]; then
    # No test data JSON files staged, skip validation
    exit 0
fi

echo "🔍 Validating staged test data JSON files..."

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

cd "$PROJECT_ROOT"

# Validate each staged file
for file in $STAGED_FILES; do
    echo "  Checking: $file"
    
    # Check if file exists (might be deleted)
    if [ ! -f "$file" ]; then
        continue
    fi
    
    # Validate JSON syntax first
    if ! python3 -m json.tool "$file" > /dev/null 2>&1; then
        echo "❌ Invalid JSON syntax in $file"
        exit 1
    fi
    
    # If it's a test data file with a schema, validate against schema
    # Add validation for specific test data files here as needed
    # Example:
    # if [[ "$file" == "test-data/feature/test-data.json" ]]; then
    #     if [ -f "test-data/scripts/validate-json.js" ]; then
    #         node test-data/scripts/validate-json.js "feature/test-data.json" || exit 1
    #     fi
    # fi
done

echo "✅ All test data JSON files are valid!"
exit 0
