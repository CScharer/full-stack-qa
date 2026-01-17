# Test Data Centralization Plan

**Date**: January 17, 2026  
**Status**: 📋 Planning  
**Goal**: Centralize all hardcoded test data into reusable, framework-agnostic JSON files

---

## 📋 Executive Summary

Currently, test data is hardcoded directly in test files across multiple frameworks (Cypress, Playwright, Robot Framework, Java/Selenide). This creates maintenance challenges, duplication, and inconsistency. This document outlines a plan to centralize all test data into the existing `test-data/` directory structure, making it reusable across all frameworks.

---

## 🧙 Wizard Test Files Status

| Framework | Has Wizard Tests? | Test File Location | Test Cases | Hardcoded Test Data | Notes |
|----------|-------------------|-------------------|------------|---------------------|-------|
| **Cypress** | ✅ Yes | `cypress/cypress/e2e/wizard.cy.ts` | 8 test cases | 3 instances (company, contact, client) | Covers navigation and form cancellation |
| **Playwright** | ✅ Yes | `playwright/tests/wizard.spec.ts` | 8 test cases | 3 instances (company, contact, client) | Matches Cypress implementation |
| **Robot Framework** | ❌ No | N/A | N/A | N/A | Current tests: `HomePageTests.robot`, `APITests.robot`. No wizard-related test files exist |
| **Java/Selenide** | ❌ No | N/A | N/A | N/A | Uses Selenide wrapper (e.g., `HomePageTests.java`). Tests Job Search Application but only has basic homepage tests, not wizard suite |
| **Java/Selenium** | ❌ No | N/A | N/A | N/A | Uses direct Selenium WebDriver (e.g., `DataDrivenTests.java`, `SmokeTests.java`, `EnhancedGridTests.java`). No wizard tests found |

### Summary

- **Frameworks with wizard tests**: 2 (Cypress, Playwright)
- **Frameworks without wizard tests**: 3 (Robot Framework, Java/Selenide, Java/Selenium)
- **Total frameworks analyzed**: 5

**Note**: The wizard test suite is a comprehensive test that navigates through all pages and verifies cancel functionality. It's currently only implemented in TypeScript-based frameworks (Cypress and Playwright). 

**Java Framework Clarification**:
- **Java/Selenide**: Uses Selenide wrapper (e.g., `HomePageTests.java`) - tests Job Search Application but only has basic homepage tests
- **Java/Selenium**: Uses direct Selenium WebDriver (e.g., `DataDrivenTests.java`, `SmokeTests.java`, `EnhancedGridTests.java`) - no wizard tests
- Java tests already use centralized test data via `TestDataLoader.java` for some tests (e.g., `DataDrivenTests.java`)

The test data centralization will primarily benefit Cypress and Playwright (which have wizard tests), but the centralized data structure will be available for future implementations in Robot Framework, Java/Selenide, and Java/Selenium if needed.

---

## 🔍 Current State Analysis

### 1. Test Data Locations

#### **Cypress Tests** (`cypress/cypress/e2e/`)

**File**: `wizard.cy.ts`
- **Line 175-183**: Company form test data
  ```typescript
  const testData = {
    name: `Test Company ${Date.now()}`,
    address: '123 Test Street',
    city: 'San Francisco',
    state: 'CA',
    zip: '94102',
    country: 'United States',
    job_type: 'Technology',
  };
  ```

- **Line 205-211**: Contact form test data
  ```typescript
  const testData = {
    first_name: `TestFirst${Date.now()}`,
    last_name: `TestLast${Date.now()}`,
    title: 'Software Engineer',
    linkedin: 'https://linkedin.com/in/test',
    contact_type: 'Recruiter',
  };
  ```

- **Line 234-236**: Client form test data
  ```typescript
  const testData = {
    name: `Test Client ${Date.now()}`,
  };
  ```

**Total**: 3 hardcoded test data objects

#### **Playwright Tests** (`playwright/tests/`)

**File**: `wizard.spec.ts`
- **Line 170-178**: Company form test data (identical to Cypress)
- **Line 200-206**: Contact form test data (identical to Cypress)
- **Line 229-231**: Client form test data (identical to Cypress)

**File**: `integration/applications.spec.ts`
- **Line 47-53**: Application creation test data
  ```typescript
  const testData = {
    position: `Test Position ${Date.now()}`,
    status: 'Pending',
    workSetting: 'Remote',
    location: 'San Francisco, CA',
    jobLink: 'https://example.com/job/123',
  };
  ```

- **Line 83-87**: Application view test data
- **Line 150-154**: Application delete test data

**Total**: 6 hardcoded test data objects

#### **Robot Framework Tests** (`src/test/robot/`)

**Status**: ✅ No hardcoded test data found in Robot Framework tests  
**Note**: Robot Framework tests use API endpoints and don't currently have form-filling tests that require test data.

#### **Java/Selenium Tests** (`src/test/java/`)

**Status**: ✅ Partially using centralized test data  
**Framework**: Uses both Selenide (wrapper) and direct Selenium WebDriver  
**Files Using Centralized Data**: 
- `DataDrivenTests.java` uses `test-data/search-queries.json` via `JSONDataProvider`
- `TestDataLoader.java` utility exists and is functional

**Files with Hardcoded Data**: 
- `HomePageTests.java` - Tests Job Search Application homepage (no form data needed)
- Other tests may have hardcoded data, but wizard tests don't exist in Java

**Total**: 0 hardcoded test data objects in wizard tests (wizard tests don't exist in Java)  
**Note**: Java tests the same Job Search Application but only has basic homepage tests, not the comprehensive wizard test suite that Cypress and Playwright have.

### 2. Existing Infrastructure

#### ✅ **Test Data Directory** (`test-data/`)

**Location**: Project root  
**Status**: Infrastructure exists, but no actual test data files yet

**Structure**:
```
test-data/
├── README.md                    # Documentation
├── PROPOSAL.md                  # Original proposal
├── TEST_EXECUTION_NOTES.md      # Execution notes
├── TEST_STATUS.md               # Status tracking
├── schemas/                     # JSON schemas (empty)
└── scripts/                     # Validation scripts
    ├── pre-commit-validate.sh
    ├── validate-json.js
    └── validate-json.sh
```

#### ✅ **Test Data Loaders**

**Playwright** (`playwright/tests/utils/test-data-loader.ts`):
- ✅ `loadTestData(filePath: string): any` - Synchronous loader
- ✅ `loadTestDataAsync(filePath: string): Promise<any>` - Async loader
- ✅ Functional and ready to use

**Java/Selenide** (`src/test/java/com/cjs/qa/utilities/TestDataLoader.java`):
- ✅ `loadTestData(String filePath): JsonObject` - JSON loader
- ✅ `loadTestData(String filePath, Class<T> clazz): T` - Typed loader
- ✅ Functional and already in use

**Cypress**:
- ❌ **Missing**: No test data loader utility exists
- **Note**: Cypress has `cypress/cypress/fixtures/` directory, but it's not being used for form test data

**Robot Framework**:
- ❌ **Missing**: No test data loader utility exists
- **Note**: Robot Framework can read JSON files directly using the `JSON` library

### 3. Test Data Patterns Identified

#### **Pattern 1: Entity Form Data**
- **Company**: name, address, city, state, zip, country, job_type
- **Contact**: first_name, last_name, title, linkedin, contact_type
- **Client**: name
- **Application**: position, status, workSetting, location, jobLink

#### **Pattern 2: Dynamic Data Generation**
- All test data uses `Date.now()` for uniqueness
- Format: `` `Test Entity ${Date.now()}` ``
- This pattern should be preserved in centralized data with placeholders

#### **Pattern 3: Duplication**
- **Cypress** and **Playwright** have identical test data for:
  - Company forms
  - Contact forms
  - Client forms
- This duplication is a maintenance risk

---

## 🎯 Goals and Objectives

### Primary Goals

1. **Eliminate Duplication**: Remove duplicate test data from Cypress and Playwright
2. **Single Source of Truth**: All frameworks use the same test data files
3. **Easy Maintenance**: Update test data without touching test code
4. **Framework Agnostic**: JSON format accessible to all frameworks
5. **Type Safety**: TypeScript interfaces for type-safe access

### Success Criteria

- ✅ All hardcoded test data moved to `test-data/` JSON files
- ✅ All frameworks can load and use centralized test data
- ✅ Test data loaders exist for all frameworks (Cypress, Playwright, Robot Framework, Java)
- ✅ Tests pass with centralized test data
- ✅ Documentation updated with usage examples
- ✅ No regression in test functionality

---

## 📁 Proposed Test Data Structure

### Directory Organization

```
test-data/
├── README.md                    # Updated documentation
├── entities/                    # Entity-specific test data
│   ├── company.json            # Company form test data
│   ├── contact.json            # Contact form test data
│   ├── client.json             # Client form test data
│   └── application.json        # Application form test data
├── schemas/                     # JSON schemas for validation
│   ├── company.schema.json
│   ├── contact.schema.json
│   ├── client.schema.json
│   └── application.schema.json
└── scripts/                     # Validation scripts (existing)
```

### File Format Examples

#### `test-data/entities/company.json`
```json
{
  "base": {
    "name": "Test Company",
    "address": "123 Test Street",
    "city": "San Francisco",
    "state": "CA",
    "zip": "94102",
    "country": "United States",
    "job_type": "Technology"
  },
  "variations": {
    "minimal": {
      "name": "Minimal Company",
      "address": "456 Simple St",
      "city": "Oakland",
      "state": "CA",
      "zip": "94601",
      "country": "United States",
      "job_type": "Technology"
    },
    "complete": {
      "name": "Complete Company",
      "address": "789 Complex Avenue",
      "city": "San Jose",
      "state": "CA",
      "zip": "95110",
      "country": "United States",
      "job_type": "Healthcare"
    }
  }
}
```

#### `test-data/entities/contact.json`
```json
{
  "base": {
    "first_name": "TestFirst",
    "last_name": "TestLast",
    "title": "Software Engineer",
    "linkedin": "https://linkedin.com/in/test",
    "contact_type": "Recruiter"
  },
  "variations": {
    "developer": {
      "first_name": "John",
      "last_name": "Developer",
      "title": "Senior Software Engineer",
      "linkedin": "https://linkedin.com/in/johndeveloper",
      "contact_type": "Developer"
    },
    "recruiter": {
      "first_name": "Jane",
      "last_name": "Recruiter",
      "title": "Technical Recruiter",
      "linkedin": "https://linkedin.com/in/janerecruiter",
      "contact_type": "Recruiter"
    }
  }
}
```

#### `test-data/entities/client.json`
```json
{
  "base": {
    "name": "Test Client"
  },
  "variations": {
    "simple": {
      "name": "Simple Client"
    },
    "complex": {
      "name": "Complex Client Name with Special Characters"
    }
  }
}
```

#### `test-data/entities/application.json`
```json
{
  "base": {
    "position": "Test Position",
    "status": "Pending",
    "workSetting": "Remote",
    "location": "San Francisco, CA",
    "jobLink": "https://example.com/job/123"
  },
  "variations": {
    "pending": {
      "position": "Pending Position",
      "status": "Pending",
      "workSetting": "Remote"
    },
    "interview": {
      "position": "Interview Position",
      "status": "Interview",
      "workSetting": "Hybrid"
    },
    "complete": {
      "position": "Complete Position",
      "status": "Pending",
      "workSetting": "Remote",
      "location": "New York, NY",
      "jobLink": "https://example.com/job/456"
    }
  }
}
```

### Dynamic Data Handling

**Challenge**: Current tests use `Date.now()` for uniqueness  
**Solution**: Test data loaders will support template replacement

**Example**:
```typescript
// In test
const testData = loadTestData('entities/company.json', 'base', {
  name: `Test Company ${Date.now()}`
});
```

Or use a utility function:
```typescript
// In test
const baseData = loadTestData('entities/company.json', 'base');
const testData = {
  ...baseData,
  name: `${baseData.name} ${Date.now()}`
};
```

---

## 🛠️ Implementation Plan

### Phase 1: Create Test Data Files ✅ Infrastructure Ready

**Tasks**:
- [x] `test-data/` directory exists
- [x] Validation scripts exist
- [ ] Create `test-data/entities/` directory
- [ ] Create JSON files for each entity type
- [ ] Create JSON schemas for validation

**Files to Create**:
1. `test-data/entities/company.json`
2. `test-data/entities/contact.json`
3. `test-data/entities/client.json`
4. `test-data/entities/application.json`
5. `test-data/schemas/company.schema.json`
6. `test-data/schemas/contact.schema.json`
7. `test-data/schemas/client.schema.json`
8. `test-data/schemas/application.schema.json`

### Phase 2: Create Missing Test Data Loaders

#### **2.1 Cypress Test Data Loader**

**File**: `cypress/cypress/support/test-data-loader.ts`

**Implementation**:
```typescript
/**
 * Test Data Loader for Cypress
 * 
 * Utility functions to load test data from the centralized test-data directory
 */

/**
 * Load test data from JSON file
 * @param filePath - Relative path from test-data directory (e.g., 'entities/company.json')
 * @param section - Optional section key (e.g., 'base', 'variations.minimal')
 * @returns Test data object
 */
export function loadTestData(filePath: string, section?: string): any {
  const projectRoot = Cypress.config('projectRoot') || Cypress.env('projectRoot');
  const fullPath = Cypress.env('projectRoot') 
    ? `${Cypress.env('projectRoot')}/test-data/${filePath}`
    : `../../test-data/${filePath}`;
  
  return cy.readFile(fullPath).then((data: any) => {
    if (section) {
      const keys = section.split('.');
      let result = data;
      for (const key of keys) {
        result = result[key];
        if (!result) {
          throw new Error(`Section '${section}' not found in ${filePath}`);
        }
      }
      return result;
    }
    return data;
  });
}

/**
 * Load test data synchronously (for use in beforeEach/setup)
 * Note: This uses cy.readFile which is async, so it should be used in commands
 */
export function loadTestDataSync(filePath: string, section?: string): any {
  // Cypress doesn't support true sync file reading
  // This is a wrapper that should be used with cy.then()
  return loadTestData(filePath, section);
}
```

**Alternative Approach** (using `require`):
```typescript
/**
 * Load test data using require (synchronous, but requires proper path resolution)
 */
export function loadTestDataSync(filePath: string, section?: string): any {
  // Resolve path relative to project root
  const path = require('path');
  const projectRoot = path.resolve(__dirname, '../../../');
  const fullPath = path.join(projectRoot, 'test-data', filePath);
  
  const data = require(fullPath);
  
  if (section) {
    const keys = section.split('.');
    let result = data;
    for (const key of keys) {
      result = result[key];
      if (!result) {
        throw new Error(`Section '${section}' not found in ${filePath}`);
      }
    }
    return result;
  }
  return data;
}
```

#### **2.2 Robot Framework Test Data Loader**

**File**: `src/test/robot/resources/TestDataLoader.robot`

**Implementation**:
```robot
*** Settings ***
Library    JSON
Library    OperatingSystem

*** Keywords ***
Load Test Data
    [Documentation]    Load test data from JSON file
    [Arguments]    ${file_path}    ${section}=${EMPTY}
    [Documentation]    Loads test data from test-data directory
    ...                file_path: Relative path from test-data (e.g., entities/company.json)
    ...                section: Optional section key (e.g., base, variations.minimal)
    
    ${project_root}=    Get Project Root
    ${full_path}=    Join Path    ${project_root}    test-data    ${file_path}
    
    ${test_data}=    Load JSON From File    ${full_path}
    
    Run Keyword If    '${section}' != '${EMPTY}'    Set Test Data Section    ${test_data}    ${section}
    
    [Return]    ${test_data}

Set Test Data Section
    [Documentation]    Extract a specific section from test data
    [Arguments]    ${test_data}    ${section}
    
    ${keys}=    Split String    ${section}    .
    ${result}=    Set Variable    ${test_data}
    
    FOR    ${key}    IN    @{keys}
        ${result}=    Get From Dictionary    ${result}    ${key}
    END
    
    [Return]    ${result}

Get Project Root
    [Documentation]    Get the project root directory
    ${current_dir}=    Get Location
    ${project_root}=    Evaluate    os.path.abspath(os.path.join('${current_dir}', '..', '..'))    os
    [Return]    ${project_root}
```

### Phase 3: Update Test Files

#### **3.1 Update Cypress Tests**

**File**: `cypress/cypress/e2e/wizard.cy.ts`

**Before**:
```typescript
const testData = {
  name: `Test Company ${Date.now()}`,
  address: '123 Test Street',
  // ... rest of data
};
```

**After**:
```typescript
import { loadTestDataSync } from '../support/test-data-loader';

// In test
const baseData = loadTestDataSync('entities/company.json', 'base');
const testData = {
  ...baseData,
  name: `${baseData.name} ${Date.now()}`
};
```

Or using async:
```typescript
import { loadTestData } from '../support/test-data-loader';

// In test
loadTestData('entities/company.json', 'base').then((baseData) => {
  const testData = {
    ...baseData,
    name: `${baseData.name} ${Date.now()}`
  };
  companyFormPage.fillForm(testData);
});
```

#### **3.2 Update Playwright Tests**

**File**: `playwright/tests/wizard.spec.ts`

**Before**:
```typescript
const testData = {
  name: `Test Company ${Date.now()}`,
  // ... rest of data
};
```

**After**:
```typescript
import { loadTestData } from './utils/test-data-loader';

// In test
const baseData = loadTestData('entities/company.json', 'base');
const testData = {
  ...baseData,
  name: `${baseData.name} ${Date.now()}`
};
```

**File**: `playwright/tests/integration/applications.spec.ts`

**Before**:
```typescript
const testData = {
  position: `Test Position ${Date.now()}`,
  // ... rest of data
};
```

**After**:
```typescript
import { loadTestData } from './utils/test-data-loader';

// In test
const baseData = loadTestData('entities/application.json', 'base');
const testData = {
  ...baseData,
  position: `${baseData.position} ${Date.now()}`
};
```

### Phase 4: Type Safety (Optional but Recommended)

#### **4.1 Create TypeScript Interfaces**

**File**: `test-data/types/entities.ts`

```typescript
/**
 * TypeScript interfaces for test data entities
 */

export interface CompanyTestData {
  name: string;
  address: string;
  city: string;
  state: string;
  zip: string;
  country: string;
  job_type: string;
}

export interface ContactTestData {
  first_name: string;
  last_name: string;
  title: string;
  linkedin: string;
  contact_type: string;
}

export interface ClientTestData {
  name: string;
}

export interface ApplicationTestData {
  position: string;
  status: string;
  workSetting: string;
  location?: string;
  jobLink?: string;
}

export interface TestDataFile<T> {
  base: T;
  variations?: Record<string, T>;
}
```

**Update Loaders to Use Types**:
```typescript
import { CompanyTestData, TestDataFile } from '../../../test-data/types/entities';

export function loadTestData<T>(
  filePath: string, 
  section?: string
): T {
  // Implementation with type safety
}
```

### Phase 5: Validation and Testing

#### **5.1 JSON Schema Validation**

**Create schemas** for each entity type to ensure data structure consistency.

**Example**: `test-data/schemas/company.schema.json`
```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "properties": {
    "base": {
      "type": "object",
      "properties": {
        "name": { "type": "string" },
        "address": { "type": "string" },
        "city": { "type": "string" },
        "state": { "type": "string" },
        "zip": { "type": "string" },
        "country": { "type": "string" },
        "job_type": { "type": "string" }
      },
      "required": ["name", "address", "city", "state", "zip", "country", "job_type"]
    },
    "variations": {
      "type": "object",
      "additionalProperties": {
        "type": "object",
        "properties": {
          "name": { "type": "string" },
          "address": { "type": "string" },
          "city": { "type": "string" },
          "state": { "type": "string" },
          "zip": { "type": "string" },
          "country": { "type": "string" },
          "job_type": { "type": "string" }
        }
      }
    }
  },
  "required": ["base"]
}
```

#### **5.2 Pre-commit Validation**

**Update**: `test-data/scripts/pre-commit-validate.sh` to validate all entity JSON files against schemas.

#### **5.3 Test Execution**

**Run all tests** to ensure they pass with centralized test data:
- [ ] Cypress wizard tests
- [ ] Playwright wizard tests
- [ ] Playwright integration tests
- [ ] Java/Selenide tests (verify no regression)
- [ ] Robot Framework tests (if applicable)

### Phase 6: Documentation Updates

#### **6.1 Update `test-data/README.md`**

Add sections for:
- Entity test data structure
- Usage examples for each framework
- Dynamic data handling
- Type safety information

#### **6.2 Update Framework-Specific Documentation**

- **Cypress README**: Add test data loader usage
- **Playwright README**: Update test data loader documentation
- **Java/Selenide**: Verify documentation is current
- **Robot Framework**: Add test data loader documentation

#### **6.3 Create Migration Guide**

**File**: `docs/guides/testing/TEST_DATA_MIGRATION.md`

Include:
- Why centralization was needed
- How to migrate existing tests
- Best practices
- Common patterns

---

## 📊 Implementation Checklist

### Phase 1: Infrastructure ✅ (Partially Complete)
- [x] `test-data/` directory exists
- [x] Validation scripts exist
- [ ] Create `test-data/entities/` directory
- [ ] Create JSON files for entities
- [ ] Create JSON schemas

### Phase 2: Loaders
- [ ] Create Cypress test data loader
- [ ] Create Robot Framework test data loader
- [ ] Verify Playwright loader works correctly
- [ ] Verify Java loader works correctly

### Phase 3: Test Updates
- [ ] Update `cypress/cypress/e2e/wizard.cy.ts` (3 instances)
- [ ] Update `playwright/tests/wizard.spec.ts` (3 instances)
- [ ] Update `playwright/tests/integration/applications.spec.ts` (3 instances)
- [ ] Verify all tests pass

### Phase 4: Type Safety (Optional)
- [ ] Create TypeScript interfaces
- [ ] Update loaders with type support
- [ ] Add type checking to CI

### Phase 5: Validation
- [ ] Create JSON schemas
- [ ] Update pre-commit validation
- [ ] Run all test suites
- [ ] Verify no regressions

### Phase 6: Documentation
- [ ] Update `test-data/README.md`
- [ ] Update framework READMEs
- [ ] Create migration guide
- [ ] Update main project README if needed

---

## 🚨 Risks and Mitigation

### Risk 1: Cypress Async File Reading
**Risk**: Cypress uses async commands, making synchronous data loading challenging  
**Mitigation**: 
- Use `cy.readFile()` with `.then()` chains
- Or use `require()` for synchronous loading (if path resolution works)
- Create wrapper utilities to simplify usage

### Risk 2: Path Resolution Across Frameworks
**Risk**: Different frameworks may resolve paths differently  
**Mitigation**:
- Use absolute paths from project root
- Create helper functions in each loader to resolve paths correctly
- Test path resolution in CI/CD environments

### Risk 3: Dynamic Data (Date.now())
**Risk**: Tests need unique data per run  
**Mitigation**:
- Provide utility functions to merge base data with dynamic values
- Document patterns for adding uniqueness
- Consider data generation utilities

### Risk 4: Type Safety
**Risk**: JSON data may not match expected types  
**Mitigation**:
- Use TypeScript interfaces
- Add JSON schema validation
- Use pre-commit hooks to validate structure

### Risk 5: Test Failures During Migration
**Risk**: Tests may fail when switching to centralized data  
**Mitigation**:
- Migrate one test file at a time
- Run tests after each migration
- Keep old code commented until verification complete
- Create a rollback plan

---

## 📈 Success Metrics

1. **Code Reduction**: Eliminate ~9 hardcoded test data objects
2. **Consistency**: All frameworks use identical test data
3. **Maintainability**: Update test data in one place
4. **Test Coverage**: All tests pass with centralized data
5. **Documentation**: Complete usage examples for all frameworks

---

## 🔄 Migration Strategy

### Step-by-Step Approach

1. **Create test data files** (Phase 1)
2. **Create missing loaders** (Phase 2)
3. **Migrate one framework at a time**:
   - Start with Playwright (loader already exists)
   - Then Cypress (new loader needed)
   - Verify Java (already using centralized data)
   - Add Robot Framework support if needed
4. **Test after each migration**
5. **Update documentation**
6. **Remove old hardcoded data**

### Rollback Plan

If issues arise:
1. Keep old hardcoded data commented in tests
2. Use feature flags to switch between old/new data
3. Revert commits if necessary
4. Document issues for future reference

---

## 🎯 Next Steps

1. **Review this plan** and get approval
2. **Create test data JSON files** for all entities
3. **Create Cypress test data loader**
4. **Create Robot Framework test data loader**
5. **Migrate Playwright tests** (easiest, loader exists)
6. **Migrate Cypress tests**
7. **Test and validate**
8. **Update documentation**

---

## 📝 Notes

- The `test-data/` infrastructure already exists, which is a significant advantage
- Java/Selenide tests already use centralized data, providing a reference implementation
- Playwright loader exists and is functional
- Main work: Create Cypress loader, create JSON files, migrate tests
- Consider creating a shared TypeScript package for type definitions if needed

---

**Last Updated**: January 17, 2026  
**Status**: Ready for Review
