# API Mock Data Tests Plan

**Date**: January 20, 2026  
**Status**: 📋 Planning  
**Goal**: Implement comprehensive API testing using mock data to enable fast, reliable, and isolated API tests without requiring a running backend

---

## 📋 Executive Summary

Currently, API tests require:
- A running backend server
- A test database
- Network connectivity
- Full application stack

This document outlines a plan to implement API tests using mock data that:
1. Run without requiring a backend server
2. Test API contracts and response structures
3. Validate request/response schemas
4. Enable faster test execution
5. Provide better test isolation
6. Support offline development

---

## 🎯 Goals and Objectives

### Primary Goals

1. **Speed**: API tests run faster without backend overhead
2. **Isolation**: Tests don't depend on external services
3. **Reliability**: Tests don't fail due to network issues or backend downtime
4. **Contract Testing**: Validate API contracts without running backend
5. **Schema Validation**: Ensure request/response structures match schemas
6. **Mock Data Reusability**: Share mock data across all frameworks

### Success Criteria

- ✅ API tests can run without backend server
- ✅ Mock data fixtures exist for all API endpoints
- ✅ Schema validation works for all requests/responses
- ✅ Contract tests validate API structure
- ✅ Mock data is reusable across frameworks
- ✅ Tests run significantly faster than integration tests
- ✅ Documentation includes usage examples for all frameworks

---

## 🔍 Current State Analysis

### 1. Current API Testing Approaches

#### **Python (pytest)** ✅ **Integration Tests**

**Location**: `backend/tests/test_*_api.py`

**Current Approach**:
- Uses FastAPI `TestClient` with real database
- Tests against actual backend code
- Requires database setup via fixtures
- Tests are integration tests, not unit tests

**Example**:
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
    assert response.status_code == 201
    data = response.json()
    assert data["status"] == "Pending"
```

**Strengths**:
- ✅ Tests actual backend code
- ✅ Validates database interactions
- ✅ Tests full request/response cycle

**Gaps**:
- ❌ Requires database setup
- ❌ Slower execution
- ❌ Tests can interfere with each other
- ❌ No mock data for API responses
- ❌ No contract testing without backend

#### **Java (REST Assured)** ⚠️ **External API Tests**

**Location**: `src/test/java/com/cjs/qa/junit/tests/api/APIContractTests.java`

**Current Approach**:
- Tests external APIs (e.g., JSONPlaceholder)
- Uses REST Assured for HTTP requests
- Validates response schemas
- No mocking of internal APIs

**Example**:
```java
@Test
public void testGetPost() {
    given()
        .baseUri("https://jsonplaceholder.typicode.com")
        .when()
        .get("/posts/1")
        .then()
        .statusCode(200)
        .body("id", equalTo(1));
}
```

**Strengths**:
- ✅ Schema validation
- ✅ Contract testing approach

**Gaps**:
- ❌ Tests external APIs, not internal APIs
- ❌ No mock data for internal APIs
- ❌ No offline testing capability

#### **Robot Framework** ⚠️ **External API Tests**

**Location**: `src/test/robot/APITests.robot`

**Current Approach**:
- Tests external APIs (e.g., JSONPlaceholder)
- Uses RequestsLibrary for HTTP requests
- No mocking of internal APIs

**Gaps**:
- ❌ Tests external APIs, not internal APIs
- ❌ No mock data for internal APIs
- ❌ No offline testing capability

#### **Cypress** ❌ **No API Mock Tests**

**Current Approach**:
- UI tests make real API calls
- No API mocking in tests
- Tests depend on backend being available

**Gaps**:
- ❌ No API mock tests
- ❌ Tests fail if backend unavailable
- ❌ No contract testing

#### **Playwright** ❌ **No API Mock Tests**

**Current Approach**:
- UI tests make real API calls
- No API mocking in tests
- Tests depend on backend being available

**Gaps**:
- ❌ No API mock tests
- ❌ Tests fail if backend unavailable
- ❌ No contract testing

### 2. Existing Mock Data

#### **Frontend Mock Data** ✅ **Exists**

**Location**: `frontend/__mocks__/data.ts`

**Current Mock Data**:
- `mockApplications`: Application entities
- `mockCompanies`: Company entities
- `mockContacts`: Contact entities
- `mockClients`: Client entities
- `mockNotes`: Note entities
- `mockJobSearchSites`: Job search site entities
- API response structures (`mockApplicationResponse`, etc.)

**Usage**: Frontend unit tests (Vitest)

**Strengths**:
- ✅ Comprehensive mock data
- ✅ Includes API response structures
- ✅ TypeScript types

**Gaps**:
- ❌ Not reusable across frameworks
- ❌ Not in centralized location
- ❌ No request mock data (only responses)

---

## 📁 Proposed Mock Data Structure

### Directory Organization

```
test-data/
├── fixtures/                     # Existing (from fixtures plan)
│   ├── api/                      # API response fixtures
│   │   ├── applications.json
│   │   ├── companies.json
│   │   ├── contacts.json
│   │   ├── clients.json
│   │   ├── notes.json
│   │   └── job-search-sites.json
│   └── entities/                 # Entity fixtures
├── mocks/                        # NEW: API mock data
│   ├── requests/                 # Request mock data
│   │   ├── applications/
│   │   │   ├── create.json
│   │   │   ├── update.json
│   │   │   └── query.json
│   │   ├── companies/
│   │   │   ├── create.json
│   │   │   ├── update.json
│   │   │   └── query.json
│   │   └── ...
│   ├── responses/                # Response mock data
│   │   ├── applications/
│   │   │   ├── get-single.json
│   │   │   ├── get-list.json
│   │   │   ├── create-success.json
│   │   │   ├── create-error.json
│   │   │   ├── update-success.json
│   │   │   ├── update-error.json
│   │   │   ├── delete-success.json
│   │   │   └── delete-error.json
│   │   ├── companies/
│   │   │   └── ...
│   │   └── ...
│   ├── schemas/                  # Request/Response schemas
│   │   ├── applications/
│   │   │   ├── create-request.schema.json
│   │   │   ├── create-response.schema.json
│   │   │   ├── update-request.schema.json
│   │   │   └── update-response.schema.json
│   │   └── ...
│   └── scenarios/                # Scenario-based mocks
│       ├── crud-flow.json
│       ├── error-handling.json
│       └── pagination.json
└── schemas/                      # Existing JSON schemas
```

### Mock Data File Examples

#### **Request Mock**: `test-data/mocks/requests/applications/create.json`
```json
{
  "valid": {
    "status": "Pending",
    "work_setting": "Remote",
    "position": "Software Engineer",
    "created_by": "test@example.com",
    "modified_by": "test@example.com"
  },
  "invalid": {
    "status": "InvalidStatus",
    "work_setting": "InvalidSetting"
  },
  "minimal": {
    "status": "Pending",
    "work_setting": "Remote",
    "created_by": "test@example.com",
    "modified_by": "test@example.com"
  },
  "complete": {
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
    "created_by": "test@example.com",
    "modified_by": "test@example.com"
  }
}
```

#### **Response Mock**: `test-data/mocks/responses/applications/get-single.json`
```json
{
  "success": {
    "id": 1,
    "status": "Pending",
    "requirement": "Full-time",
    "work_setting": "Remote",
    "compensation": "$120,000",
    "position": "Senior Software Engineer",
    "job_description": "Build amazing products",
    "job_link": "https://example.com/job/1",
    "location": "San Francisco, CA",
    "resume": "resume.pdf",
    "cover_letter": "cover.pdf",
    "entered_iwd": 0,
    "date_close": null,
    "company_id": 1,
    "client_id": 1,
    "is_deleted": 0,
    "created_on": "2025-01-01T00:00:00Z",
    "modified_on": "2025-01-01T00:00:00Z",
    "created_by": "test-user",
    "modified_by": "test-user"
  },
  "not_found": {
    "detail": "Application not found"
  },
  "error": {
    "detail": "Internal server error"
  }
}
```

#### **Response Mock**: `test-data/mocks/responses/applications/get-list.json`
```json
{
  "success": {
    "data": [
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
    "pagination": {
      "page": 1,
      "limit": 10,
      "total": 2,
      "pages": 1
    }
  },
  "empty": {
    "data": [],
    "pagination": {
      "page": 1,
      "limit": 10,
      "total": 0,
      "pages": 0
    }
  },
  "pagination": {
    "data": [
      {
        "id": 1,
        "status": "Pending",
        "position": "Senior Software Engineer"
      }
    ],
    "pagination": {
      "page": 1,
      "limit": 1,
      "total": 10,
      "pages": 10
    }
  }
}
```

#### **Schema**: `test-data/mocks/schemas/applications/create-request.schema.json`
```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "properties": {
    "status": {
      "type": "string",
      "enum": ["Pending", "Applied", "Interview", "Offer", "Rejected", "Withdrawn"]
    },
    "work_setting": {
      "type": "string",
      "enum": ["Remote", "Hybrid", "On-site"]
    },
    "position": {
      "type": "string",
      "minLength": 1
    },
    "created_by": {
      "type": "string",
      "format": "email"
    },
    "modified_by": {
      "type": "string",
      "format": "email"
    }
  },
  "required": ["status", "work_setting", "created_by", "modified_by"]
}
```

---

## 🛠️ Implementation Plan

### Phase 1: Create Mock Data Files

#### **1.1 Request Mock Data**

**Files to Create**:
- `test-data/mocks/requests/applications/create.json`
- `test-data/mocks/requests/applications/update.json`
- `test-data/mocks/requests/applications/query.json`
- `test-data/mocks/requests/companies/create.json`
- `test-data/mocks/requests/companies/update.json`
- `test-data/mocks/requests/companies/query.json`
- `test-data/mocks/requests/contacts/create.json`
- `test-data/mocks/requests/contacts/update.json`
- `test-data/mocks/requests/contacts/query.json`
- `test-data/mocks/requests/clients/create.json`
- `test-data/mocks/requests/clients/update.json`
- `test-data/mocks/requests/clients/query.json`
- `test-data/mocks/requests/notes/create.json`
- `test-data/mocks/requests/notes/update.json`
- `test-data/mocks/requests/notes/query.json`
- `test-data/mocks/requests/job-search-sites/create.json`
- `test-data/mocks/requests/job-search-sites/update.json`
- `test-data/mocks/requests/job-search-sites/query.json`

#### **1.2 Response Mock Data**

**Files to Create**:
- `test-data/mocks/responses/applications/get-single.json`
- `test-data/mocks/responses/applications/get-list.json`
- `test-data/mocks/responses/applications/create-success.json`
- `test-data/mocks/responses/applications/create-error.json`
- `test-data/mocks/responses/applications/update-success.json`
- `test-data/mocks/responses/applications/update-error.json`
- `test-data/mocks/responses/applications/delete-success.json`
- `test-data/mocks/responses/applications/delete-error.json`
- Similar files for companies, contacts, clients, notes, job-search-sites

#### **1.3 Schema Files**

**Files to Create**:
- Request schemas for all entities and operations
- Response schemas for all entities and operations
- Error response schemas

### Phase 2: Create Mock Utilities

#### **2.1 Python (pytest) Mock Utilities**

**File**: `backend/tests/mocks/api_mocks.py` (new file)

**Implementation**:
```python
"""
API mock utilities for pytest tests.
"""
import json
from pathlib import Path
from typing import Dict, Any, Optional
from unittest.mock import Mock, patch
from fastapi.testclient import TestClient

PROJECT_ROOT = Path(__file__).parent.parent.parent
MOCKS_DIR = PROJECT_ROOT / "test-data" / "mocks"


def load_request_mock(entity: str, operation: str, variant: str = "valid") -> Dict[str, Any]:
    """
    Load request mock data.
    
    Args:
        entity: Entity name (e.g., "applications", "companies")
        operation: Operation name (e.g., "create", "update", "query")
        variant: Variant name (e.g., "valid", "invalid", "minimal", "complete")
    
    Returns:
        Request mock data dictionary
    """
    mock_path = MOCKS_DIR / "requests" / entity / f"{operation}.json"
    
    with open(mock_path) as f:
        data = json.load(f)
    
    return data.get(variant, {})


def load_response_mock(entity: str, operation: str, variant: str = "success") -> Dict[str, Any]:
    """
    Load response mock data.
    
    Args:
        entity: Entity name (e.g., "applications", "companies")
        operation: Operation name (e.g., "get-single", "get-list", "create-success")
        variant: Variant name (e.g., "success", "error", "not_found")
    
    Returns:
        Response mock data dictionary
    """
    mock_path = MOCKS_DIR / "responses" / entity / f"{operation}.json"
    
    with open(mock_path) as f:
        data = json.load(f)
    
    return data.get(variant, {})


def mock_api_response(client: TestClient, method: str, url: str, mock_response: Dict[str, Any], status_code: int = 200):
    """
    Mock an API response for testing.
    
    Args:
        client: FastAPI TestClient
        method: HTTP method (e.g., "GET", "POST", "PUT", "DELETE")
        url: API URL
        mock_response: Mock response data
        status_code: HTTP status code
    
    Returns:
        Mocked response
    """
    # This is a simplified example - actual implementation would use httpx mocking
    # or similar approach
    pass


@pytest.fixture
def api_mocks():
    """Fixture to load all API mocks."""
    mocks = {}
    
    entities = ["applications", "companies", "contacts", "clients", "notes", "job-search-sites"]
    
    for entity in entities:
        mocks[entity] = {
            "requests": {},
            "responses": {}
        }
        
        # Load request mocks
        request_dir = MOCKS_DIR / "requests" / entity
        if request_dir.exists():
            for operation_file in request_dir.glob("*.json"):
                operation = operation_file.stem
                with open(operation_file) as f:
                    mocks[entity]["requests"][operation] = json.load(f)
        
        # Load response mocks
        response_dir = MOCKS_DIR / "responses" / entity
        if response_dir.exists():
            for operation_file in response_dir.glob("*.json"):
                operation = operation_file.stem
                with open(operation_file) as f:
                    mocks[entity]["responses"][operation] = json.load(f)
    
    return mocks
```

**Usage Example**:
```python
from mocks.api_mocks import load_request_mock, load_response_mock, api_mocks

def test_create_application_contract(api_mocks):
    """Test API contract using mock data."""
    request_data = api_mocks["applications"]["requests"]["create"]["valid"]
    expected_response = api_mocks["applications"]["responses"]["create-success"]["success"]
    
    # Validate request schema
    # Validate response schema
    # Test contract compliance
```

#### **2.2 Cypress Mock Utilities**

**File**: `cypress/cypress/support/api-mocks.ts` (new file)

**Implementation**:
```typescript
/**
 * Cypress API mock utilities
 */

/**
 * Load request mock data
 */
export function loadRequestMock(entity: string, operation: string, variant: string = 'valid') {
  return cy.fixture(`../../test-data/mocks/requests/${entity}/${operation}.json`).then((data: any) => {
    return data[variant];
  });
}

/**
 * Load response mock data
 */
export function loadResponseMock(entity: string, operation: string, variant: string = 'success') {
  return cy.fixture(`../../test-data/mocks/responses/${entity}/${operation}.json`).then((data: any) => {
    return data[variant];
  });
}

/**
 * Mock API response using cy.intercept
 */
export function mockApiResponse(method: string, url: string, response: any, statusCode: number = 200) {
  cy.intercept(method, url, {
    statusCode,
    body: response
  }).as(`${method.toLowerCase()}_${url.replace(/\//g, '_')}`);
}

/**
 * Mock API endpoint with mock data
 */
export function mockApiEndpoint(entity: string, operation: string, method: string = 'GET', variant: string = 'success') {
  loadResponseMock(entity, operation, variant).then((mockResponse) => {
    const url = `/api/v1/${entity}${operation.includes('single') ? '/1' : ''}`;
    mockApiResponse(method, url, mockResponse);
  });
}
```

**Usage Example**:
```typescript
import { mockApiEndpoint, loadRequestMock } from '../support/api-mocks';

it('should create an application', () => {
  mockApiEndpoint('applications', 'create-success', 'POST');
  
  loadRequestMock('applications', 'create', 'valid').then((requestData) => {
    cy.request({
      method: 'POST',
      url: '/api/v1/applications',
      body: requestData
    }).then((response) => {
      expect(response.status).to.eq(201);
      expect(response.body).to.have.property('id');
    });
  });
});
```

#### **2.3 Playwright Mock Utilities**

**File**: `playwright/tests/utils/api-mocks.ts` (new file)

**Implementation**:
```typescript
import { loadTestData } from './test-data-loader';

/**
 * Load request mock data
 */
export function loadRequestMock(entity: string, operation: string, variant: string = 'valid'): any {
  const data = loadTestData(`mocks/requests/${entity}/${operation}.json`);
  return data[variant];
}

/**
 * Load response mock data
 */
export function loadResponseMock(entity: string, operation: string, variant: string = 'success'): any {
  const data = loadTestData(`mocks/responses/${entity}/${operation}.json`);
  return data[variant];
}

/**
 * Mock API response using Playwright route
 */
export function mockApiResponse(page: any, method: string, url: string, response: any, status: number = 200) {
  page.route(url, (route: any) => {
    if (route.request().method() === method) {
      route.fulfill({
        status,
        body: JSON.stringify(response)
      });
    } else {
      route.continue();
    }
  });
}

/**
 * Mock API endpoint with mock data
 */
export function mockApiEndpoint(page: any, entity: string, operation: string, method: string = 'GET', variant: string = 'success') {
  const mockResponse = loadResponseMock(entity, operation, variant);
  const url = `/api/v1/${entity}${operation.includes('single') ? '/1' : ''}`;
  mockApiResponse(page, method, url, mockResponse);
}
```

**Usage Example**:
```typescript
import { test } from '@playwright/test';
import { mockApiEndpoint, loadRequestMock } from './utils/api-mocks';

test('should create an application', async ({ page }) => {
  mockApiEndpoint(page, 'applications', 'create-success', 'POST');
  
  const requestData = loadRequestMock('applications', 'create', 'valid');
  
  const response = await page.request.post('/api/v1/applications', {
    data: requestData
  });
  
  expect(response.status()).toBe(201);
  const body = await response.json();
  expect(body).toHaveProperty('id');
});
```

#### **2.4 Robot Framework Mock Utilities**

**File**: `src/test/robot/resources/APIMocks.robot` (new file)

**Implementation**:
```robot
*** Settings ***
Library    JSON
Library    OperatingSystem
Library    RequestsLibrary

*** Keywords ***
Load Request Mock
    [Documentation]    Load request mock data
    [Arguments]    ${entity}    ${operation}    ${variant}=valid
    [Documentation]    Loads request mock data
    ...                entity: Entity name (e.g., applications, companies)
    ...                operation: Operation name (e.g., create, update)
    ...                variant: Variant name (e.g., valid, invalid, minimal)
    
    ${project_root}=    Get Project Root
    ${mock_path}=    Join Path    ${project_root}    test-data    mocks    requests    ${entity}    ${operation}.json
    
    ${mock_data}=    Load JSON From File    ${mock_path}
    ${result}=    Get From Dictionary    ${mock_data}    ${variant}
    
    [Return]    ${result}

Load Response Mock
    [Documentation]    Load response mock data
    [Arguments]    ${entity}    ${operation}    ${variant}=success
    [Documentation]    Loads response mock data
    ...                entity: Entity name (e.g., applications, companies)
    ...                operation: Operation name (e.g., get-single, get-list)
    ...                variant: Variant name (e.g., success, error, not_found)
    
    ${project_root}=    Get Project Root
    ${mock_path}=    Join Path    ${project_root}    test-data    mocks    responses    ${entity}    ${operation}.json
    
    ${mock_data}=    Load JSON From File    ${mock_path}
    ${result}=    Get From Dictionary    ${mock_data}    ${variant}
    
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
Create Application Contract Test
    ${request_data}=    Load Request Mock    applications    create    valid
    ${expected_response}=    Load Response Mock    applications    create-success    success
    # Validate request/response schemas
    # Test contract compliance
```

#### **2.5 Java Mock Utilities**

**File**: `src/test/java/com/cjs/qa/utilities/APIMockLoader.java` (new file)

**Implementation**:
```java
package com.cjs.qa.utilities;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class APIMockLoader {
    private static final String PROJECT_ROOT = System.getProperty("user.dir");
    private static final String MOCKS_DIR = "test-data/mocks";
    
    /**
     * Load request mock data
     * @param entity Entity name (e.g., "applications", "companies")
     * @param operation Operation name (e.g., "create", "update")
     * @param variant Variant name (e.g., "valid", "invalid", "minimal")
     * @return JsonObject with mock data
     */
    public static JsonObject loadRequestMock(String entity, String operation, String variant) {
        try {
            Path mockPath = Paths.get(PROJECT_ROOT, MOCKS_DIR, "requests", entity, operation + ".json");
            JsonObject data = JsonParser.parseReader(new FileReader(mockPath.toFile())).getAsJsonObject();
            return data.getAsJsonObject(variant);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load request mock: " + entity + "/" + operation + "/" + variant, e);
        }
    }
    
    /**
     * Load response mock data
     * @param entity Entity name (e.g., "applications", "companies")
     * @param operation Operation name (e.g., "get-single", "get-list")
     * @param variant Variant name (e.g., "success", "error", "not_found")
     * @return JsonObject with mock data
     */
    public static JsonObject loadResponseMock(String entity, String operation, String variant) {
        try {
            Path mockPath = Paths.get(PROJECT_ROOT, MOCKS_DIR, "responses", entity, operation + ".json");
            JsonObject data = JsonParser.parseReader(new FileReader(mockPath.toFile())).getAsJsonObject();
            return data.getAsJsonObject(variant);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load response mock: " + entity + "/" + operation + "/" + variant, e);
        }
    }
}
```

**Usage Example**:
```java
import com.cjs.qa.utilities.APIMockLoader;
import com.google.gson.JsonObject;

@Test
public void testCreateApplicationContract() {
    JsonObject requestData = APIMockLoader.loadRequestMock("applications", "create", "valid");
    JsonObject expectedResponse = APIMockLoader.loadResponseMock("applications", "create-success", "success");
    
    // Validate request/response schemas
    // Test contract compliance
}
```

### Phase 3: Create Contract Tests

#### **3.1 Schema Validation Tests**

**Purpose**: Validate that request/response data matches schemas

**Implementation**: Use JSON Schema validation libraries for each framework

#### **3.2 Contract Tests**

**Purpose**: Test API contracts without running backend

**Test Types**:
- Request structure validation
- Response structure validation
- Required field validation
- Data type validation
- Enum value validation
- Error response validation

### Phase 4: Integration with Existing Tests

#### **4.1 Update Python Tests**

Add contract tests alongside integration tests:
- Keep integration tests for full stack testing
- Add contract tests for fast validation

#### **4.2 Add Cypress API Tests**

Create new test file: `cypress/cypress/e2e/api-contracts.cy.ts`

#### **4.3 Add Playwright API Tests**

Create new test file: `playwright/tests/api-contracts.spec.ts`

---

## 📊 Implementation Checklist

### Phase 1: Create Mock Data Files
- [ ] Create `test-data/mocks/` directory structure
- [ ] Create request mock files for all entities
- [ ] Create response mock files for all entities
- [ ] Create schema files for requests/responses
- [ ] Create scenario-based mock files

### Phase 2: Create Mock Utilities
- [ ] Create Python mock utilities (`backend/tests/mocks/api_mocks.py`)
- [ ] Create Cypress mock utilities (`cypress/cypress/support/api-mocks.ts`)
- [ ] Create Playwright mock utilities (`playwright/tests/utils/api-mocks.ts`)
- [ ] Create Robot Framework mock utilities (`src/test/robot/resources/APIMocks.robot`)
- [ ] Create Java mock utilities (`src/test/java/com/cjs/qa/utilities/APIMockLoader.java`)

### Phase 3: Create Contract Tests
- [ ] Create schema validation tests
- [ ] Create contract tests for all endpoints
- [ ] Create error handling tests
- [ ] Create pagination tests

### Phase 4: Integration
- [ ] Add contract tests to Python test suite
- [ ] Add API tests to Cypress
- [ ] Add API tests to Playwright
- [ ] Update Robot Framework API tests
- [ ] Update Java API tests

### Phase 5: Documentation
- [ ] Update `test-data/README.md` with mock data documentation
- [ ] Create API mock testing guide
- [ ] Create usage examples for all frameworks
- [ ] Update main project README if needed

---

## 🚨 Risks and Mitigation

### Risk 1: Mock Data Drift
**Risk**: Mock data may become out of sync with actual API  
**Mitigation**:
- Use schema validation to catch drift
- Regularly update mocks based on actual API responses
- Use contract testing to validate consistency

### Risk 2: Incomplete Mock Coverage
**Risk**: Not all API scenarios may be mocked  
**Mitigation**:
- Create comprehensive mock data for all endpoints
- Document all mock scenarios
- Use scenario-based mocks for complex flows

### Risk 3: Test Maintenance Overhead
**Risk**: Maintaining mocks may require significant effort  
**Mitigation**:
- Automate mock generation from API responses where possible
- Use schema validation to catch changes early
- Document mock update process

---

## 📈 Success Metrics

1. **Speed**: API contract tests run 10x faster than integration tests
2. **Coverage**: All API endpoints have mock data
3. **Reliability**: Tests don't fail due to backend unavailability
4. **Maintainability**: Mock data is easy to update and maintain
5. **Documentation**: Complete usage examples for all frameworks

---

## 🎯 Next Steps

1. **Review this plan** and get approval
2. **Create mock data files** for all API endpoints
3. **Create mock utilities** for each framework
4. **Create contract tests** for API validation
5. **Integrate with existing tests**
6. **Test and validate**
7. **Update documentation**

---

## 📝 Notes

- Mock data tests complement integration tests, they don't replace them
- Use mock data for contract testing and fast validation
- Use integration tests for full stack testing
- Consider using tools like MSW (Mock Service Worker) for browser-based mocking
- Consider using tools like WireMock for Java-based mocking

---

**Last Updated**: January 20, 2026  
**Status**: Ready for Review
