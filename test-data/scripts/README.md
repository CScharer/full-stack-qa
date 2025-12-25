# Test Data Validation Scripts

This directory contains scripts for validating test data JSON files.

## Scripts

### `validate-json.js`
Node.js script to validate JSON files against their schemas.

**Usage:**
```bash
# Validate all test data files
node test-data/scripts/validate-json.js

# Validate specific file
node test-data/scripts/validate-json.js demoqa/practice-form.json
```

**Dependencies:**
- Optional: `ajv` and `ajv-formats` for schema validation
  ```bash
  npm install ajv ajv-formats
  ```
- If ajv is not installed, only JSON syntax validation is performed

### `validate-json.sh`
Bash wrapper script for JSON validation.

**Usage:**
```bash
./test-data/scripts/validate-json.sh [file-path]
```

### `pre-commit-validate.sh`
Pre-commit hook script that validates staged test data JSON files.

**Usage:**
This script is automatically called by the pre-commit framework when JSON files in `test-data/` are staged.

## Integration

### Pre-commit Hook
The validation is integrated into `.pre-commit-config.yaml` and will run automatically when you commit test data JSON files.

### CI/CD
The validation runs as part of the GitHub Actions CI pipeline in the `validate-test-data` job.

## Schema Mapping

Test data files are mapped to their schemas in `validate-json.js`:
- `demoqa/practice-form.json` → `schemas/practice-form.schema.json`

To add new test data files:
1. Create the JSON file in the appropriate directory
2. Create a schema file in `test-data/schemas/`
3. Add the mapping to `SCHEMA_MAP` in `validate-json.js`
