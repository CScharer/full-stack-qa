# Test Data Directory

**Purpose**: Centralized test data storage for all testing frameworks  
**Location**: `/test-data/` (project root)  
**Format**: JSON (universally supported across all frameworks)

---

## 📁 Directory Structure

```
test-data/
├── README.md                    # This file
├── demoqa/
│   └── practice-form.json      # DemoQA automation practice form test data
└── schemas/                     # Optional: JSON schemas for validation
    └── practice-form.schema.json
```

---

## 🎯 Design Principles

1. **Single Source of Truth**: All test data in one location
2. **Framework Agnostic**: JSON format readable by all frameworks
3. **Easy to Update**: Human-readable, no code changes needed
4. **Organized by Feature**: Group related test data together
5. **Version Controlled**: All test data tracked in git

---

## 📝 Usage Examples

### Cypress (TypeScript)
```typescript
// Read test data
const testData = require('../../test-data/demoqa/practice-form.json');

// Or using cy.readFile (async)
cy.readFile('test-data/demoqa/practice-form.json').then((testData) => {
  // Use testData
});
```

### Playwright (TypeScript)
```typescript
import * as fs from 'fs';
import * as path from 'path';

const testDataPath = path.join(__dirname, '../../test-data/demoqa/practice-form.json');
const testData = JSON.parse(fs.readFileSync(testDataPath, 'utf-8'));
```

### Robot Framework (Python)
```robot
*** Settings ***
Library    JSON

*** Variables ***
${TEST_DATA_FILE}    ${CURDIR}${/}..${/}..${/}test-data${/}demoqa${/}practice-form.json

*** Test Cases ***
Example Test
    ${test_data}=    Load JSON From File    ${TEST_DATA_FILE}
    Log    First Name: ${test_data}[firstName]
```

### Selenide/Java
```java
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.nio.file.Files;
import java.nio.file.Paths;

// Read test data
String jsonContent = new String(Files.readAllBytes(
    Paths.get("test-data/demoqa/practice-form.json")));
JsonObject testData = new Gson().fromJson(jsonContent, JsonObject.class);
String firstName = testData.get("firstName").getAsString();
```

---

## 📋 Best Practices

1. **Use Descriptive Names**: `practice-form.json` not `data.json`
2. **Group by Feature**: `demoqa/`, `login/`, `registration/`
3. **Include Comments**: Use JSON5 or separate `_comments.json` files
4. **Validate Structure**: Use JSON schemas for validation
5. **Keep It Simple**: Avoid deeply nested structures
6. **Document Fields**: Add README.md in feature folders if needed

---

## 🔄 Updating Test Data

1. Edit the JSON file directly
2. No code changes required
3. All frameworks automatically use updated data
4. Commit changes to git

---

## 📊 File Format Example

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "gender": "Male",
  "mobile": "1234567890",
  "dateOfBirth": {
    "year": "1990",
    "month": "December",
    "day": "15"
  },
  "subjects": ["Maths", "Physics"],
  "hobbies": ["Sports", "Reading"],
  "currentAddress": "123 Main Street, Anytown, USA",
  "state": "NCR",
  "city": "Delhi"
}
```

---

## 🚀 Future Enhancements

- JSON Schema validation
- Environment-specific overrides (dev/test/prod)
- Data generation utilities
- Test data versioning
