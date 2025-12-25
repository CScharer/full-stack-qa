#!/usr/bin/env node

/**
 * JSON Schema Validator for Test Data
 * 
 * Validates all JSON files in test-data directory against their schemas
 * Usage: node test-data/scripts/validate-json.js [file-path]
 */

const fs = require('fs');
const path = require('path');

// Try to use ajv if available, otherwise fall back to basic JSON validation
let ajv, addFormats;
try {
  ajv = require('ajv');
  addFormats = require('ajv-formats');
} catch (e) {
  // ajv not installed, will use basic JSON validation only
  console.warn('⚠️  ajv not installed, will only validate JSON syntax (not schema)');
  console.warn('   Install with: npm install ajv ajv-formats');
}

// Initialize ajv if available
let ajvInstance;
if (ajv && addFormats) {
  ajvInstance = new ajv({ allErrors: true, strict: false });
  addFormats(ajvInstance);
}

// Schema mapping: test data file -> schema file
// Add new test data files and their schemas here
const SCHEMA_MAP = {
  // Example: 'feature/test-data.json': 'schemas/feature.schema.json'
};

function validateFile(testDataPath) {
  const testDataFullPath = path.join(__dirname, '..', testDataPath);
  
  if (!fs.existsSync(testDataFullPath)) {
    console.error(`❌ Test data file not found: ${testDataFullPath}`);
    process.exit(1);
  }

  const schemaPath = SCHEMA_MAP[testDataPath];
  if (!schemaPath) {
    console.warn(`⚠️  No schema defined for: ${testDataPath}`);
    return true; // Skip validation if no schema
  }

  const schemaFullPath = path.join(__dirname, '..', schemaPath);
  if (!fs.existsSync(schemaFullPath)) {
    console.error(`❌ Schema file not found: ${schemaFullPath}`);
    process.exit(1);
  }

  // Load and parse files
  let testData, schema;
  try {
    testData = JSON.parse(fs.readFileSync(testDataFullPath, 'utf-8'));
    schema = JSON.parse(fs.readFileSync(schemaFullPath, 'utf-8'));
  } catch (error) {
    console.error(`❌ Error parsing JSON: ${error.message}`);
    process.exit(1);
  }

  // If ajv is not available, just validate JSON syntax (already done above)
  if (!ajvInstance) {
    console.log(`✅ ${testDataPath} has valid JSON syntax (schema validation skipped - install ajv)`);
    return true;
  }

  // Validate against schema
  const validate = ajvInstance.compile(schema);
  const valid = validate(testData);

  if (!valid) {
    console.error(`❌ Validation failed for ${testDataPath}:`);
    validate.errors.forEach(err => {
      console.error(`   - ${err.instancePath || 'root'}: ${err.message}`);
      if (err.params) {
        console.error(`     ${JSON.stringify(err.params)}`);
      }
    });
    process.exit(1);
  }

  console.log(`✅ ${testDataPath} is valid`);
  return true;
}

function validateAll() {
  console.log('🔍 Validating all test data files...\n');
  
  let allValid = true;
  for (const [testDataPath] of Object.entries(SCHEMA_MAP)) {
    try {
      validateFile(testDataPath);
    } catch (error) {
      console.error(`❌ Error validating ${testDataPath}: ${error.message}`);
      allValid = false;
    }
  }

  if (allValid) {
    console.log('\n✅ All test data files are valid!');
    process.exit(0);
  } else {
    console.log('\n❌ Some test data files failed validation');
    process.exit(1);
  }
}

// Main execution
const filePath = process.argv[2];
if (filePath) {
  validateFile(filePath);
} else {
  validateAll();
}
