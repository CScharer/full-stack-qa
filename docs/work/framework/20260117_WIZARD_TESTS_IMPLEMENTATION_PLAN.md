# Wizard Tests Implementation Plan

**Date**: January 17, 2026  
**Status**: 📋 Planning  
**Goal**: Create wizard test files for Robot Framework, Java/Selenide, and Java/Selenium that perform the exact same steps as the existing Cypress and Playwright wizard tests

---

## 📋 Executive Summary

Currently, wizard tests exist only in Cypress and Playwright. This document outlines a plan to implement identical wizard tests in:
- Robot Framework
- Java/Selenide
- Java/Selenium

The new tests must perform **exactly the same steps** as the existing tests to ensure consistency across all frameworks.

---

## 🎯 Test Suite Overview

### Test Suite Name
**"Wizard Tests"** - Navigate through all pages and verify cancel functionality

### Test Suite Purpose
1. Tests navigation and cancel functionality for all entity creation flows
2. Verifies that canceling forms does not save any data
3. Verifies notes page shows no notes
4. Verifies job search sites display correctly
5. Verifies via API that no data was created

### Test Cases (8 total)

All test names are defined in `lib/test-utils.json`:

1. **test_home** - Click Home Navigation, Add Application button, then Cancel
2. **test_application** - Click Applications Navigation, Add button, then Cancel
3. **test_companies** - Click Companies Navigation, Add button, populate all fields, then Cancel
4. **test_contacts** - Click Contacts Navigation, Add button, populate all fields, then Cancel
5. **test_clients** - Click Clients Navigation, Add button, populate all fields, then Cancel
6. **test_notes** - Click Notes Navigation, verify there are no notes
7. **test_job_search_sites_api** - Click Job Search Sites Navigation, verify all Names and URLs against API
8. **test_job_search_sites_db** - Click Job Search Sites Navigation, verify all Names and URLs against database

---

## 📝 Detailed Test Steps

### Test Setup (beforeEach/beforeAll)

**Common Setup Steps:**
1. Set viewport to 1920x1080 (full screen)
2. Initialize all page objects:
   - HomePage
   - ApplicationsPage
   - CompaniesPage
   - CompanyFormPage
   - ContactsPage
   - ContactFormPage
   - ClientsPage
   - ClientFormPage
   - NotesPage
   - JobSearchSitesPage
   - WizardStep1Page
3. Initialize API request utility
4. Initialize database utility
5. Get initial entity counts:
   - Applications count
   - Companies count
   - Contacts count
   - Clients count
   - Notes count

---

### Test 1: test_home

**Steps:**
1. Click the Home Navigation
2. Verify page loaded (HomePage)
3. Click the Add Application button
4. Verify we're on wizard step 1 (WizardStep1Page)
5. Click the Cancel button
6. Verify we're back at applications page (ApplicationsPage)
7. Verify no applications were created (API count matches initial count)

---

### Test 2: test_application

**Steps:**
1. Click the Applications Navigation
2. Verify page loaded (ApplicationsPage)
3. Click the Add button
4. Verify we're on wizard step 1 (WizardStep1Page)
5. Click the Cancel button
6. Verify we're back at applications page (ApplicationsPage)
7. Verify no applications were created (API count matches initial count)

---

### Test 3: test_companies

**Steps:**
1. Click the Companies Navigation
2. Verify page loaded (CompaniesPage)
3. Click the Add button
4. Verify form is loaded (CompanyFormPage)
5. Populate all fields with test data:
   - name: `Test Company ${Date.now()}`
   - address: `123 Test Street`
   - city: `San Francisco`
   - state: `CA`
   - zip: `94102`
   - country: `United States`
   - job_type: `Technology`
6. Verify fields are populated (check name field value)
7. Click the Cancel button
8. Verify we're back at companies page (CompaniesPage)
9. Verify no companies were created (API count matches initial count)

---

### Test 4: test_contacts

**Steps:**
1. Click the Contacts Navigation
2. Verify page loaded (ContactsPage)
3. Click the Add button
4. Verify form is loaded (ContactFormPage)
5. Populate all fields with test data:
   - first_name: `TestFirst${Date.now()}`
   - last_name: `TestLast${Date.now()}`
   - title: `Software Engineer`
   - linkedin: `https://linkedin.com/in/test`
   - contact_type: `Recruiter`
6. Verify fields are populated (check first_name and last_name field values)
7. Click the Cancel button
8. Verify we're back at contacts page (ContactsPage)
9. Verify no contacts were created (API count matches initial count)

---

### Test 5: test_clients

**Steps:**
1. Click the Clients Navigation
2. Verify page loaded (ClientsPage)
3. Click the Add button
4. Verify form is loaded (ClientFormPage)
5. Populate all fields with test data:
   - name: `Test Client ${Date.now()}`
6. Verify field is populated (check name field value)
7. Click the Cancel button
8. Verify we're back at clients page (ClientsPage)
9. Verify no clients were created (API count matches initial count)

---

### Test 6: test_notes

**Steps:**
1. Click the Notes Navigation
2. Verify page loaded (NotesPage)
3. Verify there are no notes:
   - Check if empty state is visible (`[data-qa="notes-empty-state"]`)
   - OR check if notes list is visible and empty (`[data-qa*="notes-list"]` with no `[data-qa*="note-"]` items)
   - OR assume no notes if neither is visible
4. Verify no notes were created (API count matches initial count)

---

### Test 7: test_job_search_sites_api

**Steps:**
1. Get job search sites from API (using API utility, `include_deleted: false`)
2. Click the Job Search Sites Navigation
3. Verify page loaded (JobSearchSitesPage)
4. Verify page content matches API data:
   - If API returns 0 sites:
     - Verify empty state is visible (`[data-qa="job-search-sites-empty-state"]`)
   - If API returns sites:
     - Verify table is visible (`[data-qa="job-search-sites-table"]`)
     - Verify row count matches API data count
     - For each row:
       - Verify name matches (first link text equals API site name)
       - Verify URL matches:
         - If API site has URL: verify link href equals API site URL
         - If API site has no URL: verify cell text equals "N/A"

---

### Test 8: test_job_search_sites_db

**Steps:**
1. Get job search sites directly from database (using DB utility, `include_deleted: false`)
2. Click the Job Search Sites Navigation
3. Verify page loaded (JobSearchSitesPage)
4. Verify page content matches database data:
   - If database returns 0 sites:
     - Verify empty state is visible (`[data-qa="job-search-sites-empty-state"]`)
   - If database returns sites:
     - Verify table is visible (`[data-qa="job-search-sites-table"]`)
     - Verify row count matches database data count
     - For each row:
       - Verify name matches (first link text equals DB site name)
       - Verify URL matches:
         - If DB site has URL: verify link href equals DB site URL
         - If DB site has no URL: verify cell text equals "N/A"

---

## 🏗️ Required Infrastructure

### Page Objects Required

All frameworks need the following page objects:

1. **BasePage** - Base class with common navigation and utility methods
2. **HomePage** - Home page with sidebar navigation
3. **ApplicationsPage** - Applications list page
4. **CompaniesPage** - Companies list page
5. **CompanyFormPage** - Company create/edit form
6. **ContactsPage** - Contacts list page
7. **ContactFormPage** - Contact create/edit form
8. **ClientsPage** - Clients list page
9. **ClientFormPage** - Client create/edit form
10. **NotesPage** - Notes list page
11. **JobSearchSitesPage** - Job search sites list page
12. **WizardStep1Page** - Wizard step 1 (contact selection) page

### Utilities Required

1. **API Request Utility** - For making API calls and verifying entity counts
   - `getAllEntityCounts()` - Get counts for all entities
   - `verifyEntityCount(entityType, expectedCount)` - Verify entity count
   - `getJobSearchSites(options)` - Get job search sites from API

2. **Database Utility** - For querying database directly
   - `getJobSearchSites(includeDeleted)` - Get job search sites from database

3. **Test Utils** - For accessing test names
   - `getTestSuite('wizard')` - Get wizard test suite configuration

### Data-qa Selectors

All page objects must use `data-qa` attributes for selectors to ensure consistency:

**Common Selectors:**
- `[data-qa="notes-empty-state"]` - Notes empty state
- `[data-qa*="notes-list"]` - Notes list container
- `[data-qa*="note-"]` - Individual note items
- `[data-qa="job-search-sites-empty-state"]` - Job search sites empty state
- `[data-qa="job-search-sites-table"]` - Job search sites table

**Form Selectors:**
- `[data-qa="company-create-name"]` - Company name input
- `[data-qa="contact-first-name"]` - Contact first name input
- `[data-qa="contact-last-name"]` - Contact last name input
- `[data-qa="client-create-name"]` - Client name input

**Wizard Selectors:**
- `[data-qa="wizard-step1-title"]` - Wizard step 1 title
- `[data-qa="wizard-step1-cancel-button"]` - Wizard step 1 cancel button

---

## 📦 Implementation Plan by Framework

### Phase 1: Robot Framework

#### File Location
`src/test/robot/WizardTests.robot`

#### Required Resources
- `src/test/robot/resources/Common.robot` (already exists)
- `src/test/robot/resources/HomePage.robot` (may need to create)
- `src/test/robot/resources/ApplicationsPage.robot` (need to create)
- `src/test/robot/resources/CompaniesPage.robot` (need to create)
- `src/test/robot/resources/CompanyFormPage.robot` (need to create)
- `src/test/robot/resources/ContactsPage.robot` (need to create)
- `src/test/robot/resources/ContactFormPage.robot` (need to create)
- `src/test/robot/resources/ClientsPage.robot` (need to create)
- `src/test/robot/resources/ClientFormPage.robot` (need to create)
- `src/test/robot/resources/NotesPage.robot` (need to create)
- `src/test/robot/resources/JobSearchSitesPage.robot` (need to create)
- `src/test/robot/resources/WizardStep1Page.robot` (need to create)

#### Required Libraries
- `SeleniumLibrary` (already in use)
- `JSON` (for test data)
- `RequestsLibrary` (for API calls) - may need to add
- Custom Python library for database queries - may need to create

#### Implementation Approach
1. Create page object resource files (Robot Framework keywords)
2. Create API utility library (Python) for API calls
3. Create database utility library (Python) for DB queries
4. Create wizard test file with all 8 test cases
5. Ensure tests run serially (Robot Framework supports this)

#### Test Data
- Use hardcoded test data initially (matching Cypress/Playwright)
- Can migrate to centralized test data later

#### Challenges
- Robot Framework uses Python for custom libraries
- Need to create API and DB utility libraries in Python
- Need to ensure proper serial execution

---

### Phase 2: Java/Selenide

#### File Location
`src/test/java/com/cjs/qa/junit/tests/WizardTests.java`

#### Required Page Objects
- `src/test/java/com/cjs/qa/junit/pages/HomePage.java` (already exists)
- `src/test/java/com/cjs/qa/junit/pages/ApplicationsPage.java` (need to create)
- `src/test/java/com/cjs/qa/junit/pages/CompaniesPage.java` (need to create)
- `src/test/java/com/cjs/qa/junit/pages/CompanyFormPage.java` (need to create)
- `src/test/java/com/cjs/qa/junit/pages/ContactsPage.java` (need to create)
- `src/test/java/com/cjs/qa/junit/pages/ContactFormPage.java` (need to create)
- `src/test/java/com/cjs/qa/junit/pages/ClientsPage.java` (need to create)
- `src/test/java/com/cjs/qa/junit/pages/ClientFormPage.java` (need to create)
- `src/test/java/com/cjs/qa/junit/pages/NotesPage.java` (need to create)
- `src/test/java/com/cjs/qa/junit/pages/JobSearchSitesPage.java` (need to create)
- `src/test/java/com/cjs/qa/junit/pages/WizardStep1Page.java` (need to create)

#### Required Utilities
- `src/test/java/com/cjs/qa/junit/utilities/ApiRequestUtility.java` (need to create)
- `src/test/java/com/cjs/qa/junit/utilities/DbUtility.java` (need to create)
- `src/test/java/com/cjs/qa/utilities/TestDataLoader.java` (already exists)

#### Implementation Approach
1. Create all page objects using Selenide
2. Create API utility class (using OkHttp, Retrofit, or similar)
3. Create database utility class (using JDBC or existing DB utilities)
4. Create wizard test class with TestNG annotations
5. Use `@BeforeMethod` for setup
6. Ensure tests run serially (use `@Test(priority = ...)` or `dependsOnMethods`)

#### Test Data
- Use hardcoded test data initially (matching Cypress/Playwright)
- Can use `TestDataLoader` if needed

#### Challenges
- Need to create API utility (HTTP client)
- Need to create database utility (JDBC)
- Selenide syntax differs from Playwright/Cypress

---

### Phase 3: Java/Selenium

#### File Location
`src/test/java/com/cjs/qa/junit/tests/WizardTestsSelenium.java`

#### Required Page Objects
- `src/test/java/com/cjs/qa/junit/pages/selenium/HomePage.java` (need to create)
- `src/test/java/com/cjs/qa/junit/pages/selenium/ApplicationsPage.java` (need to create)
- `src/test/java/com/cjs/qa/junit/pages/selenium/CompaniesPage.java` (need to create)
- `src/test/java/com/cjs/qa/junit/pages/selenium/CompanyFormPage.java` (need to create)
- `src/test/java/com/cjs/qa/junit/pages/selenium/ContactsPage.java` (need to create)
- `src/test/java/com/cjs/qa/junit/pages/selenium/ContactFormPage.java` (need to create)
- `src/test/java/com/cjs/qa/junit/pages/selenium/ClientsPage.java` (need to create)
- `src/test/java/com/cjs/qa/junit/pages/selenium/ClientFormPage.java` (need to create)
- `src/test/java/com/cjs/qa/junit/pages/selenium/NotesPage.java` (need to create)
- `src/test/java/com/cjs/qa/junit/pages/selenium/JobSearchSitesPage.java` (need to create)
- `src/test/java/com/cjs/qa/junit/pages/selenium/WizardStep1Page.java` (need to create)

#### Required Utilities
- `src/test/java/com/cjs/qa/junit/utilities/ApiRequestUtility.java` (can reuse from Selenide)
- `src/test/java/com/cjs/qa/junit/utilities/DbUtility.java` (can reuse from Selenide)

#### Implementation Approach
1. Create all page objects using direct Selenium WebDriver
2. Reuse API and DB utilities from Selenide implementation
3. Create wizard test class with TestNG annotations
4. Use `@BeforeMethod` for setup
5. Ensure tests run serially (use `@Test(priority = ...)` or `dependsOnMethods`)

#### Test Data
- Use hardcoded test data initially (matching Cypress/Playwright)

#### Challenges
- Direct WebDriver syntax is more verbose than Selenide
- Need to handle explicit waits manually
- Page object pattern implementation differs from Selenide

---

## 🔍 Reference Implementation Analysis

### Cypress Implementation
- **Location**: `cypress/cypress/e2e/wizard.cy.ts`
- **Page Objects**: `cypress/cypress/page-objects/`
- **Utilities**: `cypress/cypress/support/api-utils.ts`, `cypress/cypress/support/db-utils.ts`
- **Test Execution**: Serial by default
- **Async Handling**: Uses Cypress promises (`.then()`)

### Playwright Implementation
- **Location**: `playwright/tests/wizard.spec.ts`
- **Page Objects**: `playwright/tests/pages/`
- **Utilities**: `playwright/helpers/api-utils.ts`, `playwright/helpers/db-utils.ts`
- **Test Execution**: Serial (configured with `test.describe.configure({ mode: 'serial' })`)
- **Async Handling**: Uses async/await

---

## ✅ Implementation Checklist

### Robot Framework
- [ ] Create page object resource files (11 files)
- [ ] Create API utility library (Python)
- [ ] Create database utility library (Python)
- [ ] Create `WizardTests.robot` test file
- [ ] Implement all 8 test cases
- [ ] Verify tests run serially
- [ ] Test execution and verify all pass
- [ ] Update Robot Framework README with wizard test documentation

### Java/Selenide
- [ ] Create page object classes (11 files)
- [ ] Create API utility class
- [ ] Create database utility class
- [ ] Create `WizardTests.java` test file
- [ ] Implement all 8 test cases
- [ ] Verify tests run serially
- [ ] Test execution and verify all pass
- [ ] Update Java/Selenide documentation

### Java/Selenium
- [ ] Create page object classes (11 files in `selenium` subpackage)
- [ ] Reuse API utility class from Selenide
- [ ] Reuse database utility class from Selenide
- [ ] Create `WizardTestsSelenium.java` test file
- [ ] Implement all 8 test cases
- [ ] Verify tests run serially
- [ ] Test execution and verify all pass
- [ ] Update Java/Selenium documentation

### Common Tasks
- [ ] Verify all tests use same `data-qa` selectors
- [ ] Verify all tests perform exact same steps
- [ ] Verify all tests use same test data
- [ ] Verify all tests verify same assertions
- [ ] Update main project README with wizard test information
- [ ] Create cross-framework comparison documentation

---

## 🎯 Success Criteria

1. ✅ All three frameworks have wizard test files
2. ✅ All tests perform exactly the same steps as Cypress/Playwright
3. ✅ All tests use the same `data-qa` selectors
4. ✅ All tests use the same test data
5. ✅ All tests verify the same assertions
6. ✅ All tests pass successfully
7. ✅ Tests run serially (in order)
8. ✅ Documentation updated for all frameworks

---

## 📊 Test Data Reference

### Company Form Test Data
```json
{
  "name": "Test Company ${Date.now()}",
  "address": "123 Test Street",
  "city": "San Francisco",
  "state": "CA",
  "zip": "94102",
  "country": "United States",
  "job_type": "Technology"
}
```

### Contact Form Test Data
```json
{
  "first_name": "TestFirst${Date.now()}",
  "last_name": "TestLast${Date.now()}",
  "title": "Software Engineer",
  "linkedin": "https://linkedin.com/in/test",
  "contact_type": "Recruiter"
}
```

### Client Form Test Data
```json
{
  "name": "Test Client ${Date.now()}"
}
```

**Note**: `${Date.now()}` should be replaced with actual timestamp generation in each framework's syntax.

---

## 🚨 Potential Challenges and Solutions

### Challenge 1: API Utility Implementation
**Issue**: Robot Framework and Java need API utilities  
**Solution**: 
- Robot Framework: Create Python library using `requests` library
- Java: Create utility class using OkHttp or Retrofit

### Challenge 2: Database Utility Implementation
**Issue**: Robot Framework and Java need database utilities  
**Solution**:
- Robot Framework: Create Python library using `sqlite3` or existing DB utilities
- Java: Create utility class using JDBC or existing DB utilities

### Challenge 3: Serial Test Execution
**Issue**: Ensure tests run in order  
**Solution**:
- Robot Framework: Use `[Documentation]` and test dependencies
- Java: Use TestNG `@Test(priority = ...)` or `dependsOnMethods`

### Challenge 4: Page Object Consistency
**Issue**: Ensure all frameworks use same selectors and methods  
**Solution**:
- Use same `data-qa` selectors across all frameworks
- Document page object interface/contract
- Review existing Cypress/Playwright page objects as reference

### Challenge 5: Test Data Consistency
**Issue**: Ensure all frameworks use same test data  
**Solution**:
- Use hardcoded test data initially (matching Cypress/Playwright)
- Document test data format
- Can migrate to centralized test data later (separate work)

---

## 📝 Next Steps

1. **Review this plan** and get approval
2. **Start with Robot Framework** (simplest to implement)
3. **Then Java/Selenide** (can reuse some infrastructure)
4. **Finally Java/Selenium** (can reuse utilities from Selenide)
5. **Test and validate** each implementation
6. **Update documentation** for all frameworks

---

## 🔗 Related Documents

- `docs/work/20260117_TEST_DATA_CENTRALIZATION_PLAN.md` - Test data centralization (separate work)
- `lib/test-utils.json` - Test names definition
- `cypress/cypress/e2e/wizard.cy.ts` - Cypress reference implementation
- `playwright/tests/wizard.spec.ts` - Playwright reference implementation

---

**Last Updated**: January 17, 2026  
**Status**: Ready for Review
