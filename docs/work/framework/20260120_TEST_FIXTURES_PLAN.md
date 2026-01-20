# Test Fixtures Plan

**Date**: January 20, 2026  
**Status**: 📋 Planning  
**Goal**: Implement a comprehensive fixtures system for test data across all frameworks to improve test maintainability, reduce duplication, and enable better test isolation

---

## 📋 Executive Summary

Currently, test data is managed inconsistently across frameworks:
- **Python (pytest)**: Uses fixtures in `conftest.py` for database setup, but test data is hardcoded in test files
- **Cypress**: Has a `fixtures/` directory but it's underutilized (only contains `sample.jpg`)
- **Playwright**: No fixtures directory, test data is hardcoded in test files
- **Robot Framework**: No fixtures, test data is hardcoded or loaded from JSON
- **Java/Selenide**: Uses `TestDataLoader.java` for JSON loading, but no fixture system

This document outlines a plan to implement a comprehensive fixtures system that:
1. Provides reusable test data across all frameworks
2. Supports different fixture scopes (session, function, module)
3. Enables test isolation and cleanup
4. Integrates with the centralized test data system (`test-data/`)

---

## 🎯 Goals and Objectives

### Primary Goals

1. **Consistency**: All frameworks use fixtures for test data management
2. **Reusability**: Test data fixtures can be shared across test files
3. **Isolation**: Tests can run independently with proper setup/teardown
4. **Maintainability**: Update test data in one place, use everywhere
5. **Framework Integration**: Fixtures work seamlessly with existing test infrastructure

### Success Criteria

- ✅ All frameworks have fixture support for test data
- ✅ Fixtures integrate with centralized `test-data/` directory
- ✅ Test isolation is maintained (tests don't interfere with each other)
- ✅ Fixtures support different scopes (session, function, module)
- ✅ Documentation includes usage examples for all frameworks
- ✅ No regression in test functionality

---

## 🔍 Current State Analysis

### 1. Framework Fixture Support

#### **Python (pytest)** ✅ **Best Support**

**Location**: `backend/tests/conftest.py`

**Current Fixtures**:
- `test_db_path` (session-scoped): Creates temporary database
- `schema_file_path` (session-scoped): Path to schema SQL file
- `test_database` (session-scoped): Fresh database with schema
- `db_connection` (function-scoped): Fresh connection per test
- `clean_db` (function-scoped): Clean database before each test
- `client` (function-scoped): FastAPI TestClient with test database

**Test Data**: Hardcoded in test files (e.g., `test_applications_api.py`)

**Example**:
```python
def test_create_application(client: TestClient):
    application_data = {
        "status": "Pending",
        "work_setting": "Remote",
        "position": "Software Engineer",
        # ... hardcoded data
    }
    response = client.post(api_url(ENDPOINT), json=application_data)
```

**Strengths**:
- ✅ Comprehensive fixture system
- ✅ Proper scoping (session vs function)
- ✅ Database isolation
- ✅ Cleanup handled automatically

**Gaps**:
- ❌ Test data is hardcoded in tests
- ❌ No fixtures for entity test data (companies, contacts, clients, etc.)
- ❌ No integration with `test-data/` directory

#### **Cypress** ⚠️ **Partial Support**

**Location**: `cypress/cypress/fixtures/`

**Current Fixtures**:
- `sample.jpg`: Image file for file upload tests

**Test Data**: Hardcoded in test files (e.g., `wizard.cy.ts`)

**Example**:
```typescript
const testData = {
  name: `Test Company ${Date.now()}`,
  address: '123 Test Street',
  // ... hardcoded data
};
```

**Strengths**:
- ✅ Fixtures directory exists
- ✅ `cy.fixture()` command available

**Gaps**:
- ❌ Fixtures directory underutilized
- ❌ No JSON fixtures for test data
- ❌ No integration with `test-data/` directory
- ❌ No fixture scoping (all fixtures are function-scoped)

#### **Playwright** ❌ **No Fixtures**

**Location**: No fixtures directory

**Test Data**: Hardcoded in test files (e.g., `wizard.spec.ts`)

**Example**:
```typescript
const testData = {
  name: `Test Company ${Date.now()}`,
  address: '123 Test Street',
  // ... hardcoded data
};
```

**Strengths**:
- ✅ `playwright/tests/utils/test-data-loader.ts` exists for loading JSON

**Gaps**:
- ❌ No fixtures directory
- ❌ No fixture system
- ❌ No integration with `test-data/` directory
- ❌ No fixture scoping

#### **Robot Framework** ⚠️ **Partial Support**

**Location**: No dedicated fixtures directory

**Test Data**: Hardcoded or loaded from JSON

**Strengths**:
- ✅ Can read JSON files using `JSON` library
- ✅ Setup/Teardown keywords available

**Gaps**:
- ❌ No standardized fixture system
- ❌ No integration with `test-data/` directory
- ❌ No fixture scoping

#### **Java/Selenide** ⚠️ **Partial Support**

**Location**: `src/test/java/com/cjs/qa/utilities/TestDataLoader.java`

**Test Data**: Loaded from JSON via `TestDataLoader`

**Strengths**:
- ✅ `TestDataLoader` utility exists
- ✅ JUnit `@BeforeEach` / `@AfterEach` for setup/teardown

**Gaps**:
- ❌ No standardized fixture system
- ❌ No integration with `test-data/` directory
- ❌ No fixture scoping (JUnit annotations provide some scoping)

---

## 📁 Proposed Fixture Structure

### Directory Organization

```
test-data/
├── README.md                    # Documentation
├── entities/                    # Entity test data (existing)
│   ├── company.json
│   ├── contact.json
│   ├── client.json
│   └── application.json
├── fixtures/                     # NEW: Framework-agnostic fixtures
│   ├── api/                      # API response fixtures
│   │   ├── applications.json
│   │   ├── companies.json
│   │   ├── contacts.json
│   │   ├── clients.json
│   │   ├── notes.json
│   │   └── job-search-sites.json
│   ├── entities/                 # Entity fixtures (full objects)
│   │   ├── company.json
│   │   ├── contact.json
│   │   ├── client.json
│   │   └── application.json
│   └── scenarios/                # Scenario-based fixtures
│       ├── wizard-flow.json
│       ├── crud-operations.json
│       └── error-cases.json
└── schemas/                      # JSON schemas (existing)
```

### Framework-Specific Fixture Directories

#### **Cypress**
```
cypress/cypress/fixtures/
├── sample.jpg                    # Existing
├── entities/                      # NEW: Entity fixtures
│   ├── company.json
│   ├── contact.json
│   ├── client.json
│   └── application.json
└── api/                          # NEW: API response fixtures
    ├── applications.json
    ├── companies.json
    └── ...
```

#### **Playwright**
```
playwright/tests/fixtures/         # NEW: Create this directory
├── entities/                      # Entity fixtures
│   ├── company.json
│   ├── contact.json
│   ├── client.json
│   └── application.json
└── api/                          # API response fixtures
    ├── applications.json
    ├── companies.json
    └── ...
```

#### **Robot Framework**
```
src/test/robot/fixtures/           # NEW: Create this directory
├── entities/                      # Entity fixtures
│   ├── company.json
│   ├── contact.json
│   ├── client.json
│   └── application.json
└── api/                          # API response fixtures
    ├── applications.json
    ├── companies.json
    └── ...
```

---

## 🛠️ Implementation Plan

### Phase 1: Create Fixture Files

#### **1.1 Centralized Fixtures** (`test-data/fixtures/`)

**Purpose**: Framework-agnostic fixtures that can be used by any framework

**Files to Create**:
1. `test-data/fixtures/entities/company.json` - Company entity fixtures
2. `test-data/fixtures/entities/contact.json` - Contact entity fixtures
3. `test-data/fixtures/entities/client.json` - Client entity fixtures
4. `test-data/fixtures/entities/application.json` - Application entity fixtures
5. `test-data/fixtures/api/applications.json` - API response fixtures
6. `test-data/fixtures/api/companies.json` - API response fixtures
7. `test-data/fixtures/api/contacts.json` - API response fixtures
8. `test-data/fixtures/api/clients.json` - API response fixtures
9. `test-data/fixtures/api/notes.json` - API response fixtures
10. `test-data/fixtures/api/job-search-sites.json` - API response fixtures

**Example**: `test-data/fixtures/entities/company.json`
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
```

**Example**: `test-data/fixtures/api/applications.json`
```json
{
  "single": {
    "id": 1,
    "status": "Pending",
    "requirement": "Full-time",
    "work_setting": "Remote",
    "compensation": "$120,000",
    "position": "Senior Software Engineer",
    "job_description": "Build amazing products",
    "job_link": "https://example.com/job/1",
    "location": "San Francisco, CA",
    "company_id": 1,
    "client_id": 1,
    "is_deleted": 0,
    "created_on": "2025-01-01T00:00:00Z",
    "modified_on": "2025-01-01T00:00:00Z",
    "created_by": "test-user",
    "modified_by": "test-user"
  },
  "list": [
    {
      "id": 1,
      "status": "Pending",
      "position": "Senior Software Engineer",
      "work_setting": "Remote",
      "company_id": 1
    },
    {
      "id": 2,
      "status": "Interview",
      "position": "Frontend Developer",
      "work_setting": "Hybrid",
      "company_id": 2
    }
  ],
  "create": {
    "status": "Pending",
    "work_setting": "Remote",
    "position": "Software Engineer",
    "created_by": "test@example.com",
    "modified_by": "test@example.com"
  },
  "update": {
    "status": "Interview",
    "work_setting": "Hybrid"
  }
}
```

#### **1.2 Framework-Specific Fixtures**

**Cypress**: Create fixtures in `cypress/cypress/fixtures/entities/` and `cypress/cypress/fixtures/api/`
- Can be symlinks to `test-data/fixtures/` or copies
- Or use `cy.fixture()` to load from `test-data/fixtures/`

**Playwright**: Create fixtures in `playwright/tests/fixtures/`
- Use `test-data-loader.ts` to load from `test-data/fixtures/`

**Robot Framework**: Create fixtures in `src/test/robot/fixtures/`
- Use `JSON` library to load from `test-data/fixtures/`

**Java**: Use `TestDataLoader.java` to load from `test-data/fixtures/`

### Phase 2: Create Fixture Utilities

#### **2.1 Python (pytest) Fixtures**

**File**: `backend/tests/conftest.py` (add new fixtures)

**New Fixtures to Add**:
```python
@pytest.fixture(scope="function")
def company_fixture():
    """Load company test data from fixtures."""
    import json
    from pathlib import Path
    
    project_root = Path(__file__).parent.parent.parent
    fixture_path = project_root / "test-data" / "fixtures" / "entities" / "company.json"
    
    with open(fixture_path) as f:
        data = json.load(f)
    
    return data

@pytest.fixture(scope="function")
def contact_fixture():
    """Load contact test data from fixtures."""
    # Similar implementation
    pass

@pytest.fixture(scope="function")
def application_fixture():
    """Load application test data from fixtures."""
    # Similar implementation
    pass

@pytest.fixture(scope="function")
def api_response_fixture():
    """Load API response fixtures."""
    import json
    from pathlib import Path
    
    project_root = Path(__file__).parent.parent.parent
    fixtures = {}
    
    for entity in ["applications", "companies", "contacts", "clients", "notes", "job-search-sites"]:
        fixture_path = project_root / "test-data" / "fixtures" / "api" / f"{entity}.json"
        with open(fixture_path) as f:
            fixtures[entity] = json.load(f)
    
    return fixtures
```

**Usage Example**:
```python
def test_create_application(client: TestClient, application_fixture):
    """Test creating an application using fixture data."""
    application_data = application_fixture["create"]
    response = client.post(api_url(ENDPOINT), json=application_data)
    assert response.status_code == 201
```

#### **2.2 Cypress Fixtures**

**File**: `cypress/cypress/support/fixtures.ts` (new file)

**Implementation**:
```typescript
/**
 * Cypress fixture utilities
 */

/**
 * Load entity fixture
 * @param entity - Entity name (e.g., 'company', 'contact')
 * @param variant - Variant name (e.g., 'base', 'minimal', 'complete')
 * @returns Cypress chainable with fixture data
 */
export function loadEntityFixture(entity: string, variant: string = 'base') {
  return cy.fixture(`../../test-data/fixtures/entities/${entity}.json`).then((data: any) => {
    return data[variant];
  });
}

/**
 * Load API response fixture
 * @param entity - Entity name (e.g., 'applications', 'companies')
 * @param type - Response type (e.g., 'single', 'list', 'create', 'update')
 * @returns Cypress chainable with fixture data
 */
export function loadApiFixture(entity: string, type: string = 'single') {
  return cy.fixture(`../../test-data/fixtures/api/${entity}.json`).then((data: any) => {
    return data[type];
  });
}
```

**Usage Example**:
```typescript
import { loadEntityFixture } from '../support/fixtures';

it('should create a company', () => {
  loadEntityFixture('company', 'base').then((companyData) => {
    companyFormPage.fillForm({
      ...companyData,
      name: `${companyData.name} ${Date.now()}`
    });
  });
});
```

#### **2.3 Playwright Fixtures**

**File**: `playwright/tests/fixtures/test-fixtures.ts` (new file)

**Implementation**:
```typescript
import { test as base } from '@playwright/test';
import { loadTestData } from '../utils/test-data-loader';

/**
 * Extend Playwright test with fixtures
 */
export const test = base.extend({
  companyFixture: async ({}, use) => {
    const data = loadTestData('fixtures/entities/company.json', 'base');
    await use(data);
  },
  
  contactFixture: async ({}, use) => {
    const data = loadTestData('fixtures/entities/contact.json', 'base');
    await use(data);
  },
  
  applicationFixture: async ({}, use) => {
    const data = loadTestData('fixtures/entities/application.json', 'base');
    await use(data);
  },
  
  apiFixtures: async ({}, use) => {
    const fixtures: Record<string, any> = {};
    const entities = ['applications', 'companies', 'contacts', 'clients', 'notes', 'job-search-sites'];
    
    for (const entity of entities) {
      fixtures[entity] = loadTestData(`fixtures/api/${entity}.json`);
    }
    
    await use(fixtures);
  },
});
```

**Usage Example**:
```typescript
import { test } from './fixtures/test-fixtures';

test('should create a company', async ({ companyFixture, page }) => {
  const companyData = {
    ...companyFixture,
    name: `${companyFixture.name} ${Date.now()}`
  };
  
  await companyFormPage.fillForm(companyData);
});
```

#### **2.4 Robot Framework Fixtures**

**File**: `src/test/robot/resources/Fixtures.robot` (new file)

**Implementation**:
```robot
*** Settings ***
Library    JSON
Library    OperatingSystem

*** Keywords ***
Load Entity Fixture
    [Documentation]    Load entity fixture from test-data/fixtures
    [Arguments]    ${entity}    ${variant}=base
    [Documentation]    Loads entity fixture data
    ...                entity: Entity name (e.g., company, contact)
    ...                variant: Variant name (e.g., base, minimal, complete)
    
    ${project_root}=    Get Project Root
    ${fixture_path}=    Join Path    ${project_root}    test-data    fixtures    entities    ${entity}.json
    
    ${fixture_data}=    Load JSON From File    ${fixture_path}
    ${result}=    Get From Dictionary    ${fixture_data}    ${variant}
    
    [Return]    ${result}

Load API Fixture
    [Documentation]    Load API response fixture from test-data/fixtures
    [Arguments]    ${entity}    ${type}=single
    [Documentation]    Loads API response fixture data
    ...                entity: Entity name (e.g., applications, companies)
    ...                type: Response type (e.g., single, list, create, update)
    
    ${project_root}=    Get Project Root
    ${fixture_path}=    Join Path    ${project_root}    test-data    fixtures    api    ${entity}.json
    
    ${fixture_data}=    Load JSON From File    ${fixture_path}
    ${result}=    Get From Dictionary    ${fixture_data}    ${type}
    
    [Return]    ${result}

Get Project Root
    [Documentation]    Get the project root directory
    ${current_dir}=    Get Location
    ${project_root}=    Evaluate    os.path.abspath(os.path.join('${current_dir}', '..', '..'))    os
    [Return]    ${project_root}
```

**Usage Example**:
```robot
*** Test Cases ***
Create Company
    ${company_data}=    Load Entity Fixture    company    base
    # Use ${company_data} in test
```

#### **2.5 Java/Selenide Fixtures**

**File**: `src/test/java/com/cjs/qa/utilities/FixtureLoader.java` (new file)

**Implementation**:
```java
package com.cjs.qa.utilities;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FixtureLoader {
    private static final String PROJECT_ROOT = System.getProperty("user.dir");
    private static final String FIXTURES_DIR = "test-data/fixtures";
    
    /**
     * Load entity fixture
     * @param entity Entity name (e.g., "company", "contact")
     * @param variant Variant name (e.g., "base", "minimal", "complete")
     * @return JsonObject with fixture data
     */
    public static JsonObject loadEntityFixture(String entity, String variant) {
        try {
            Path fixturePath = Paths.get(PROJECT_ROOT, FIXTURES_DIR, "entities", entity + ".json");
            JsonObject data = JsonParser.parseReader(new FileReader(fixturePath.toFile())).getAsJsonObject();
            return data.getAsJsonObject(variant);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load fixture: " + entity + "/" + variant, e);
        }
    }
    
    /**
     * Load API response fixture
     * @param entity Entity name (e.g., "applications", "companies")
     * @param type Response type (e.g., "single", "list", "create", "update")
     * @return JsonObject with fixture data
     */
    public static JsonObject loadApiFixture(String entity, String type) {
        try {
            Path fixturePath = Paths.get(PROJECT_ROOT, FIXTURES_DIR, "api", entity + ".json");
            JsonObject data = JsonParser.parseReader(new FileReader(fixturePath.toFile())).getAsJsonObject();
            return data.getAsJsonObject(type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load API fixture: " + entity + "/" + type, e);
        }
    }
}
```

**Usage Example**:
```java
import com.cjs.qa.utilities.FixtureLoader;
import com.google.gson.JsonObject;

@Test
public void testCreateApplication() {
    JsonObject applicationData = FixtureLoader.loadApiFixture("applications", "create");
    // Use applicationData in test
}
```

### Phase 3: Update Test Files

#### **3.1 Update Python Tests**

**File**: `backend/tests/test_applications_api.py`

**Before**:
```python
def test_create_application(client: TestClient):
    application_data = {
        "status": "Pending",
        "work_setting": "Remote",
        "position": "Software Engineer",
        "created_by": "test@example.com",
        "modified_by": "test@example.com"
    }
    response = client.post(api_url(ENDPOINT), json=application_data)
```

**After**:
```python
def test_create_application(client: TestClient, api_response_fixture):
    application_data = api_response_fixture["applications"]["create"]
    response = client.post(api_url(ENDPOINT), json=application_data)
```

#### **3.2 Update Cypress Tests**

**File**: `cypress/cypress/e2e/wizard.cy.ts`

**Before**:
```typescript
const testData = {
  name: `Test Company ${Date.now()}`,
  address: '123 Test Street',
  // ... hardcoded data
};
```

**After**:
```typescript
import { loadEntityFixture } from '../support/fixtures';

loadEntityFixture('company', 'base').then((baseData) => {
  const testData = {
    ...baseData,
    name: `${baseData.name} ${Date.now()}`
  };
  // Use testData in test
});
```

#### **3.3 Update Playwright Tests**

**File**: `playwright/tests/wizard.spec.ts`

**Before**:
```typescript
const testData = {
  name: `Test Company ${Date.now()}`,
  address: '123 Test Street',
  // ... hardcoded data
};
```

**After**:
```typescript
import { test } from './fixtures/test-fixtures';

test('should create a company', async ({ companyFixture, page }) => {
  const testData = {
    ...companyFixture,
    name: `${companyFixture.name} ${Date.now()}`
  };
  // Use testData in test
});
```

### Phase 4: Integration with Centralized Test Data

#### **4.1 Relationship Between Fixtures and Test Data**

**Fixtures** (`test-data/fixtures/`):
- Complete entity objects (with IDs, timestamps, etc.)
- API response structures
- Scenario-based data sets
- Used for mocking API responses, setting up test state

**Test Data** (`test-data/entities/`):
- Form input data (without IDs, timestamps)
- Used for filling forms, creating entities
- Simpler structure focused on input fields

**Integration**:
- Fixtures can reference test data
- Fixtures can extend test data with additional fields (IDs, timestamps, etc.)
- Both can be loaded by the same utilities

#### **4.2 Unified Loader**

**Option 1**: Extend existing loaders to support both fixtures and test data

**Option 2**: Create separate loaders but share common utilities

**Recommended**: Option 1 - Extend existing loaders

---

## 📊 Implementation Checklist

### Phase 1: Create Fixture Files
- [ ] Create `test-data/fixtures/` directory structure
- [ ] Create entity fixtures (`test-data/fixtures/entities/`)
- [ ] Create API response fixtures (`test-data/fixtures/api/`)
- [ ] Create scenario fixtures (`test-data/fixtures/scenarios/`)
- [ ] Create JSON schemas for fixtures

### Phase 2: Create Fixture Utilities
- [ ] Add Python fixtures to `backend/tests/conftest.py`
- [ ] Create Cypress fixture utilities (`cypress/cypress/support/fixtures.ts`)
- [ ] Create Playwright fixtures (`playwright/tests/fixtures/test-fixtures.ts`)
- [ ] Create Robot Framework fixtures (`src/test/robot/resources/Fixtures.robot`)
- [ ] Create Java fixture loader (`src/test/java/com/cjs/qa/utilities/FixtureLoader.java`)

### Phase 3: Update Test Files
- [ ] Update Python API tests to use fixtures
- [ ] Update Cypress tests to use fixtures
- [ ] Update Playwright tests to use fixtures
- [ ] Update Robot Framework tests to use fixtures (if applicable)
- [ ] Update Java tests to use fixtures (if applicable)

### Phase 4: Integration
- [ ] Integrate fixtures with centralized test data
- [ ] Update documentation
- [ ] Create usage examples
- [ ] Run all tests to verify no regressions

### Phase 5: Documentation
- [ ] Update `test-data/README.md` with fixtures documentation
- [ ] Create framework-specific fixture usage guides
- [ ] Update main project README if needed

---

## 🚨 Risks and Mitigation

### Risk 1: Path Resolution Across Frameworks
**Risk**: Different frameworks may resolve paths differently  
**Mitigation**:
- Use absolute paths from project root
- Create helper functions in each loader to resolve paths correctly
- Test path resolution in CI/CD environments

### Risk 2: Fixture Scope Management
**Risk**: Different frameworks handle fixture scoping differently  
**Mitigation**:
- Document scope behavior for each framework
- Use appropriate scopes (session vs function) based on framework capabilities
- Test isolation to ensure fixtures don't leak between tests

### Risk 3: Dynamic Data in Fixtures
**Risk**: Fixtures may need dynamic data (timestamps, unique IDs)  
**Mitigation**:
- Provide utility functions to merge fixture data with dynamic values
- Document patterns for adding uniqueness
- Consider template replacement in fixtures

### Risk 4: Test Failures During Migration
**Risk**: Tests may fail when switching to fixtures  
**Mitigation**:
- Migrate one test file at a time
- Run tests after each migration
- Keep old code commented until verification complete
- Create a rollback plan

---

## 📈 Success Metrics

1. **Code Reduction**: Eliminate hardcoded test data from test files
2. **Consistency**: All frameworks use fixtures for test data
3. **Maintainability**: Update test data in one place (fixtures)
4. **Test Coverage**: All tests pass with fixtures
5. **Documentation**: Complete usage examples for all frameworks

---

## 🔄 Migration Strategy

### Step-by-Step Approach

1. **Create fixture files** (Phase 1)
2. **Create fixture utilities** (Phase 2)
3. **Migrate one framework at a time**:
   - Start with Python (best fixture support)
   - Then Cypress (fixtures directory exists)
   - Then Playwright (test data loader exists)
   - Then Robot Framework
   - Finally Java
4. **Test after each migration**
5. **Update documentation**
6. **Remove old hardcoded data**

### Rollback Plan

If issues arise:
1. Keep old hardcoded data commented in tests
2. Use feature flags to switch between old/new fixtures
3. Revert commits if necessary
4. Document issues for future reference

---

## 🎯 Next Steps

1. **Review this plan** and get approval
2. **Create fixture JSON files** for all entities and API responses
3. **Create fixture utilities** for each framework
4. **Migrate Python tests** (easiest, best fixture support)
5. **Migrate Cypress tests**
6. **Migrate Playwright tests**
7. **Test and validate**
8. **Update documentation**

---

## 📝 Notes

- Fixtures complement the centralized test data system (`test-data/entities/`)
- Fixtures are more complete (include IDs, timestamps) while test data is simpler (form inputs)
- Both can be used together: test data for form inputs, fixtures for API responses
- Consider creating a shared TypeScript package for type definitions if needed

---

**Last Updated**: January 20, 2026  
**Status**: Ready for Review
