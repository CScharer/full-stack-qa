# Test Data Centralization Proposal

## 🎯 Goal

Create a centralized, framework-agnostic test data storage solution that all testing frameworks (Cypress, Playwright, Robot Framework, Selenide) can use.

---

## 📍 Recommended Location

**`/test-data/` at project root**

### Why This Location?

✅ **Accessible to All Frameworks**
- Cypress: Can read from project root
- Playwright: Can read from project root  
- Robot Framework: Can read from project root
- Selenide/Java: Can read from project root

✅ **Framework Agnostic**
- Not tied to Maven's `src/test/resources/` structure
- Not tied to any specific framework's conventions
- Clear, obvious location for all developers

✅ **Easy Path Resolution**
- Simple relative paths from any framework
- Works in CI/CD environments
- Works in Docker containers

### Alternative Locations Considered

❌ `src/test/resources/test-data/` - Java-centric, harder for JS frameworks  
❌ `common/test-data/` - "common" is ambiguous  
❌ Framework-specific folders - Creates duplication  
❌ `Data/test-data/` - Conflicts with existing `Data/` folder for database

---

## 📁 Proposed Structure

```
test-data/
├── README.md                    # Documentation and usage guide
├── demoqa/
│   └── practice-form.json      # DemoQA form test data
├── schemas/                     # Optional: JSON schemas for validation
│   └── practice-form.schema.json
└── .gitkeep                     # Ensure directory is tracked
```

**Future expansion:**
```
test-data/
├── demoqa/
│   ├── practice-form.json
│   ├── elements.json
│   └── widgets.json
├── login/
│   └── credentials.json
└── schemas/
    ├── practice-form.schema.json
    └── login.schema.json
```

---

## 📝 Format: JSON

### Why JSON?

✅ **Universal Support**
- Cypress: Native JSON support
- Playwright: Native JSON support (Node.js)
- Robot Framework: Built-in JSON library
- Java/Selenide: Gson already in dependencies

✅ **Human Readable**
- Easy to edit manually
- Easy to review in git diffs
- No special tools needed

✅ **Structured & Validatable**
- Can use JSON Schema for validation
- Type-safe when loaded
- Supports nested objects and arrays

### Format Example

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

## 🛠️ Implementation Strategy

### Phase 1: Create Infrastructure ✅
- [x] Create `test-data/` directory structure
- [x] Create JSON test data file
- [x] Create helper utilities for each framework
- [x] Create documentation

### Phase 2: Update Tests ✅
- [x] Update Cypress test to use shared data
- [x] Update Playwright test to use shared data
- [x] Update Robot Framework test to use shared data
- [x] Update Selenide test to use shared data

### Phase 3: Validation ✅
- [x] Add JSON Schema validation
- [x] Add pre-commit hooks to validate JSON
- [x] Add CI checks for JSON validity

---

## 🔧 Helper Utilities

### Cypress (`cypress/cypress/support/test-data-loader.ts`)
```typescript
import { loadTestData } from '../support/test-data-loader';

const testData = await loadTestData('demoqa/practice-form.json');
```

### Playwright (`playwright/tests/utils/test-data-loader.ts`)
```typescript
import { loadTestData } from './utils/test-data-loader';

const testData = loadTestData('demoqa/practice-form.json');
```

### Robot Framework
```robot
*** Settings ***
Library    JSON

*** Variables ***
${TEST_DATA}=    Load JSON From File    ${CURDIR}${/}..${/}..${/}test-data${/}demoqa${/}practice-form.json
```

### Selenide/Java (`src/test/java/com/cjs/qa/utilities/TestDataLoader.java`)
```java
import com.cjs.qa.utilities.TestDataLoader;

JsonObject testData = TestDataLoader.loadTestData("demoqa/practice-form.json");
String firstName = testData.get("firstName").getAsString();
```

---

## ✅ Benefits

1. **Single Source of Truth**: One file to update, all tests benefit
2. **No Code Changes**: Update test data without touching test code
3. **Easy Maintenance**: Non-developers can update test data
4. **Consistency**: All frameworks use identical data
5. **Version Control**: Track test data changes in git
6. **Validation**: Can validate structure with JSON Schema

---

## 🔄 Migration Path

1. Create `test-data/` structure (✅ Done)
2. Create helper utilities (✅ Done)
3. Update each test framework to use shared data
4. Remove hardcoded test data from tests
5. Document the new approach

---

## 📊 Comparison

| Aspect | Current (Hardcoded) | Proposed (Centralized) |
|--------|-------------------|----------------------|
| **Location** | Scattered in test files | Single `test-data/` folder |
| **Update Process** | Edit code in 4 places | Edit 1 JSON file |
| **Consistency** | Risk of divergence | Guaranteed consistency |
| **Maintainability** | Requires code changes | Edit JSON only |
| **Accessibility** | Developers only | Anyone can edit JSON |

---

## 🚀 Next Steps

1. Review and approve this proposal
2. Update all test files to use shared data
3. Remove hardcoded test data
4. Add to `.gitignore` exclusions (ensure test-data is tracked)
5. Update documentation

---

## 💡 Future Enhancements

- **Environment Overrides**: `practice-form.dev.json`, `practice-form.prod.json`
- **Data Generation**: Scripts to generate test data
- **Validation**: Pre-commit hooks for JSON Schema validation
- **Templates**: Base templates for common test data structures
