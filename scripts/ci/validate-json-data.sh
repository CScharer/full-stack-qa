#!/bin/bash
# scripts/ci/validate-json-data.sh
# Logic to validate JSON syntax and schemas

set -e

DATA_DIR="test-data"

if [ ! -d "$DATA_DIR" ]; then
    echo "⚠️  $DATA_DIR directory not found, skipping validation"
    exit 0
fi

echo "🔍 Validating JSON files in $DATA_DIR..."

# Validate syntax
find "$DATA_DIR" -name "*.json" -type f | while read file; do
    echo "  Validating syntax: $file"
    python3 -m json.tool "$file" > /dev/null || {
        echo "❌ Invalid JSON syntax in $file"
        exit 1
    }
done

# Validate against schemas if script exists
if [ -f "$DATA_DIR/scripts/validate-json.js" ]; then
    echo "Validating against JSON schemas..."
    node "$DATA_DIR/scripts/validate-json.js" || {
        echo "❌ Schema validation failed"
        exit 1
    }
else
    echo "⚠️  Schema validator script not found, skipping schema validation"
fi

echo "✅ All JSON validation passed!"
