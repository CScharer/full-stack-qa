#!/bin/bash

# JSON Schema Validator Script for Test Data
# Validates JSON files against their schemas

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TEST_DATA_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
PROJECT_ROOT="$(cd "$TEST_DATA_DIR/.." && pwd)"

cd "$PROJECT_ROOT"

# Check if Node.js is available
if ! command -v node &> /dev/null; then
    echo "❌ Node.js is required but not installed"
    exit 1
fi

# Check if ajv-cli is available, if not try to use local node_modules
if command -v ajv &> /dev/null; then
    VALIDATOR="ajv"
elif [ -f "node_modules/.bin/ajv-cli" ]; then
    VALIDATOR="node_modules/.bin/ajv-cli"
else
    # Fall back to custom validator script
    if [ -f "test-data/scripts/validate-json.js" ]; then
        node test-data/scripts/validate-json.js "$@"
        exit $?
    else
        echo "❌ No JSON validator found. Install ajv-cli: npm install -g ajv-cli"
        exit 1
    fi
fi

# Validate specific file or all files
if [ -n "$1" ]; then
    echo "🔍 Validating: $1"
    $VALIDATOR validate -s "test-data/schemas/practice-form.schema.json" -d "$1"
else
    echo "🔍 Validating all test data files..."
    $VALIDATOR validate -s "test-data/schemas/practice-form.schema.json" -d "test-data/demoqa/practice-form.json"
fi

echo "✅ Validation complete!"
