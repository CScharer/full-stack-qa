# Cypress Page Objects and Tests Implementation Plan

**Date**: 2026-01-11  
**Status**: 📋 Planning  
**Purpose**: Create comprehensive Cypress page objects and tests matching the existing Playwright implementation

**Last Updated**: 2026-01-11  
**Branch**: `feat/cypress-page-objects`

---

## Overview

This document outlines the plan to create Cypress page objects and tests that match the existing Playwright implementation. The goal is to have identical test coverage in both frameworks, ensuring consistency and providing flexibility in test execution.

---

## Quick Reference: Playwright Page Objects

**All Playwright page objects exist in `playwright/tests/pages/`:**

| # | Page Object | Type | Status |
|---|-------------|------|--------|
| 1 | BasePage.ts | Base | ✅ Complete |
| 2 | HomePage.ts | Page | ✅ Complete |
| 3 | ApplicationsPage.ts | List | ✅ Complete |
| 4 | ApplicationDetailPage.ts | Detail | ✅ Complete |
| 5 | ApplicationFormPage.ts | Form | ✅ Complete |
| 6 | CompaniesPage.ts | List | ✅ Complete |
| 7 | CompanyFormPage.ts | Form | ✅ Complete |
| 8 | ContactsPage.ts | List | ✅ Complete |
| 9 | ContactFormPage.ts | Form | ✅ Complete |
| 10 | ClientsPage.ts | List | ✅ Complete |
| 11 | ClientFormPage.ts | Form | ✅ Complete |
| 12 | NotesPage.ts | List | ✅ Complete |
| 13 | JobSearchSitesPage.ts | List | ✅ Complete |
| 14 | WizardStep1Page.ts | Wizard | ✅ Complete |

**Total: 14 page objects** (1 Base, 1 Home, 6 List, 4 Form, 1 Detail, 1 Wizard)

---

## Current State Analysis

### Playwright Implementation (Reference)

#### Complete List of Playwright Page Objects
Located in: `playwright/tests/pages/`

**All 14 Page Objects:**
1. BasePage.ts
2. HomePage.ts
3. ApplicationsPage.ts
4. ApplicationDetailPage.ts
5. ApplicationFormPage.ts
6. CompaniesPage.ts
7. CompanyFormPage.ts
8. ContactsPage.ts
9. ContactFormPage.ts
10. ClientsPage.ts
11. ClientFormPage.ts
12. NotesPage.ts
13. JobSearchSitesPage.ts
14. WizardStep1Page.ts

#### Page Objects (Complete)
Located in: `playwright/tests/pages/`

1. **BasePage.ts** ✅
   - Common methods: navigate, waitForPageLoad, getTitle, verifyTitleContains, setViewport, waitForVisible
   - Uses Playwright's `Page` and `Locator` types

2. **HomePage.ts** ✅
   - Sidebar navigation
   - Navigation cards (Applications, Companies, Contacts, Clients, Notes, Job Search Sites)
   - Uses `data-qa` selectors

3. **ApplicationsPage.ts** ✅
   - List page with table, filters, empty state
   - Methods: navigate, verifyPageLoaded, clickNewApplication, getApplicationRow, clickApplication, clickEdit, clickDelete, filterByStatus, etc.
   - Uses `data-qa` selectors

4. **ApplicationDetailPage.ts** ✅
   - Detail view page
   - Methods: verifyPageLoaded, getStatus, clickEdit, clickDelete, etc.
   - Uses `data-qa` selectors

5. **ApplicationFormPage.ts** ✅
   - Create/Edit form page
   - Methods: fillForm, submit, fillPosition, fillStatus, fillWorkSetting, etc.
   - Uses `data-qa` selectors

6. **CompaniesPage.ts** ✅
   - List page with table, filters, empty state
   - Uses `data-qa` selectors

7. **CompanyFormPage.ts** ✅
   - Create/Edit form page
   - Uses `data-qa` selectors

8. **ContactsPage.ts** ✅
   - List page with table, filters, empty state
   - Uses `data-qa` selectors

9. **ContactFormPage.ts** ✅
   - Create/Edit form page
   - Uses `data-qa` selectors

10. **ClientsPage.ts** ✅
    - List page with table, filters, empty state
    - Uses `data-qa` selectors

11. **ClientFormPage.ts** ✅
    - Create/Edit form page
    - Uses `data-qa` selectors

12. **NotesPage.ts** ✅
    - List page with table, filters, empty state
    - Uses `data-qa` selectors

13. **JobSearchSitesPage.ts** ✅
    - List page with table, filters, empty state
    - Uses `data-qa` selectors

14. **WizardStep1Page.ts** ✅
    - Application wizard step 1 (contact selection)
    - Uses `data-qa` selectors

#### Test Files (Reference)
Located in: `playwright/tests/`

1. **homepage.spec.ts** ✅
   - Home page navigation tests
   - Sidebar verification tests

2. **wizard.spec.ts** ✅
   - Application wizard tests

3. **integration/applications.spec.ts** ✅
   - Full CRUD operations (currently skipped)
   - Tests: navigate, create, view, edit, delete, list, empty state

4. **integration/companies.spec.ts** ✅
   - Basic navigation and page loading (currently skipped)

### Cypress Implementation (Current)

#### Page Objects (Partial)
Located in: `cypress/cypress/page-objects/`

1. **BasePage.ts** ✅ **ENHANCED**
   - Complete implementation with: visit, waitForPageLoad, getTitle, verifyTitleContains, setViewport
   - Added: waitForVisible, getElement, clickElement, fillInput, selectOption
   - Enhanced waitForPageLoad to wait for document.readyState
   - Uses Cypress commands (cy.visit, cy.get, etc.)

2. **HomePage.ts** ✅
   - Basic implementation with sidebar navigation
   - Uses `data-qa` selectors

#### Test Files (Minimal)
Located in: `cypress/cypress/e2e/`

1. **homepage.cy.ts** ✅
   - Basic home page tests

#### Missing Page Objects
- WizardStep1Page.ts

#### Completed Page Objects (Phase 1, 2, 3 & 4)
- BasePage.ts ✅ (Phase 1 - Enhanced)
- HomePage.ts ✅ (Existing)
- ApplicationsPage.ts ✅ (Phase 2)
- ApplicationFormPage.ts ✅ (Phase 3)
- ApplicationDetailPage.ts ✅ (Phase 4)
- CompaniesPage.ts ✅ (Phase 2)
- CompanyFormPage.ts ✅ (Phase 3)
- ContactsPage.ts ✅ (Phase 2)
- ContactFormPage.ts ✅ (Phase 3)
- ClientsPage.ts ✅ (Phase 2)
- ClientFormPage.ts ✅ (Phase 3)
- NotesPage.ts ✅ (Phase 2)
- JobSearchSitesPage.ts ✅ (Phase 2)

#### Missing Test Files
- applications.cy.ts (CRUD operations)
- companies.cy.ts
- contacts.cy.ts
- clients.cy.ts
- notes.cy.ts
- job-search-sites.cy.ts
- wizard.cy.ts

---

## Implementation Strategy

### Phase 1: Enhance BasePage (Priority: High) ✅ COMPLETE

**Goal**: Ensure BasePage has all methods needed by child pages

**Tasks**:
- [x] Review Playwright BasePage methods
- [x] Add missing methods to Cypress BasePage:
  - [x] `waitForVisible()` - Wait for element to be visible (with timeout)
  - [x] Enhanced `waitForPageLoad()` - Wait for network idle (if possible in Cypress)
  - [x] `getElement()` - Generic method to get element by selector
  - [x] `clickElement()` - Generic method to click element
  - [x] `fillInput()` - Generic method to fill input field
  - [x] `selectOption()` - Generic method to select dropdown option

**File**: `cypress/cypress/page-objects/BasePage.ts`

**Reference**: `playwright/tests/pages/BasePage.ts`

**Status**: ✅ **COMPLETE** - All methods added and tested
- Enhanced `waitForPageLoad()` to wait for document.readyState
- Added `waitForVisible()` with timeout support
- Added generic helper methods: `getElement()`, `clickElement()`, `fillInput()`, `selectOption()`
- Updated `verifyTitleContains()` to support both string and RegExp (matching Playwright)

---

### Phase 2: List Page Objects (Priority: High) ✅ COMPLETE

**Goal**: Create page objects for all list pages

**Pages to Create**:
1. **ApplicationsPage.ts** ✅
2. **CompaniesPage.ts** ✅
3. **ContactsPage.ts** ✅
4. **ClientsPage.ts** ✅
5. **NotesPage.ts** ✅
6. **JobSearchSitesPage.ts** ✅

**Common Pattern for List Pages**:
- Title selector
- New/Create button
- Filters (if applicable)
- Table/List container
- Table body
- Empty state
- Methods:
  - `navigate()` - Navigate to page
  - `verifyPageLoaded()` - Verify page loaded
  - `clickNew()` - Click new/create button
  - `getRow(id)` - Get row by ID
  - `clickRow(id)` - Click row to view details
  - `clickEdit(id)` - Click edit button for row
  - `clickDelete(id)` - Click delete button for row
  - `verifyEmptyState()` - Verify empty state is visible
  - `verifyTableVisible()` - Verify table is visible
  - Filter methods (if applicable)

**Reference Files**:
- `playwright/tests/pages/ApplicationsPage.ts`
- `playwright/tests/pages/CompaniesPage.ts`
- `playwright/tests/pages/ContactsPage.ts`
- `playwright/tests/pages/ClientsPage.ts`
- `playwright/tests/pages/NotesPage.ts`
- `playwright/tests/pages/JobSearchSitesPage.ts`

**Status**: ✅ **COMPLETE** - All 6 list page objects created
- ApplicationsPage.ts - Complete with all methods matching Playwright
- CompaniesPage.ts - Complete with ID and name-based methods
- ContactsPage.ts - Complete with all filter methods
- ClientsPage.ts - Complete with ID and name-based methods
- NotesPage.ts - Complete (notes list, not table)
- JobSearchSitesPage.ts - Complete with table verification
- All use data-qa selectors matching Playwright implementation
- All methods converted from async/await to Cypress chainable commands

---

### Phase 3: Form Page Objects (Priority: High) ✅ COMPLETE

**Goal**: Create page objects for all form pages

**Pages to Create**:
1. **ApplicationFormPage.ts** ✅
2. **CompanyFormPage.ts** ✅
3. **ContactFormPage.ts** ✅
4. **ClientFormPage.ts** ✅

**Common Pattern for Form Pages**:
- Title selector
- Form container
- Input fields (using `data-qa` selectors)
- Select/dropdown fields
- Submit button
- Cancel button (if applicable)
- Methods:
  - `verifyPageLoaded()` - Verify form loaded
  - `fillForm(data)` - Fill entire form with data object
  - `fillField(name, value)` - Fill specific field
  - `submit()` - Submit form
  - `cancel()` - Cancel form (if applicable)
  - Field-specific methods (fillPosition, fillStatus, etc.)

**Reference Files**:
- `playwright/tests/pages/ApplicationFormPage.ts`
- `playwright/tests/pages/CompanyFormPage.ts`
- `playwright/tests/pages/ContactFormPage.ts`
- `playwright/tests/pages/ClientFormPage.ts`

**Status**: ✅ **COMPLETE** - All 4 form page objects created
- ApplicationFormPage.ts - Complete with wizard step2 and edit form support
- CompanyFormPage.ts - Complete with all form fields (name, address, city, state, zip, country, job_type)
- ContactFormPage.ts - Complete with all form fields (first_name, last_name, title, linkedin, contact_type, company_id, application_id, client_id)
- ClientFormPage.ts - Complete with name field
- All use data-qa selectors matching Playwright implementation
- All methods converted from async/await to Cypress chainable commands
- ApplicationFormPage includes both wizard step2 methods and edit form methods

---

### Phase 4: Detail Page Objects (Priority: Medium) ✅ COMPLETE

**Goal**: Create page objects for detail/view pages

**Pages to Create**:
1. **ApplicationDetailPage.ts** ✅

**Common Pattern for Detail Pages**:
- Title selector
- Status badge
- Edit button
- Delete button
- Detail sections
- Methods:
  - `verifyPageLoaded()` - Verify page loaded
  - `getStatus()` - Get status text
  - `clickEdit()` - Click edit button
  - `clickDelete()` - Click delete button
  - `verifyDetails(data)` - Verify details match data

**Reference Files**:
- `playwright/tests/pages/ApplicationDetailPage.ts`

**Status**: ✅ **COMPLETE** - ApplicationDetailPage created
- ApplicationDetailPage.ts - Complete with all methods matching Playwright
- Includes navigation, title/status badge getters, edit/delete buttons
- Includes note management methods (add note, get note, delete note)
- All methods use data-qa selectors matching Playwright implementation
- All methods converted from async/await to Cypress chainable commands
- TypeScript compilation verified

---

### Phase 5: Wizard Page Objects (Priority: Medium)

**Goal**: Create page objects for wizard/multi-step forms

**Pages to Create**:
1. **WizardStep1Page.ts**

**Pattern for Wizard Pages**:
- Step indicator
- Form fields for current step
- Next button
- Previous button (if applicable)
- Cancel button
- Methods:
  - `verifyStepLoaded()` - Verify step loaded
  - `fillStep(data)` - Fill step with data
  - `clickNext()` - Go to next step
  - `clickPrevious()` - Go to previous step
  - `clickCancel()` - Cancel wizard

**Reference Files**:
- `playwright/tests/pages/WizardStep1Page.ts`

---

### Phase 6: Test Files (Priority: High)

**Goal**: Create test files matching Playwright tests

**Test Files to Create**:

1. **applications.cy.ts**
   - Match: `playwright/tests/integration/applications.spec.ts`
   - Tests:
     - Navigate to applications from home page
     - Create a new application
     - View application details
     - Edit an existing application
     - Delete an application
     - List applications
     - Handle empty applications list

2. **companies.cy.ts**
   - Match: `playwright/tests/integration/companies.spec.ts`
   - Tests:
     - Navigate to companies from home page
     - Load companies list page

3. **contacts.cy.ts**
   - Basic navigation and page loading tests

4. **clients.cy.ts**
   - Basic navigation and page loading tests

5. **notes.cy.ts**
   - Basic navigation and page loading tests

6. **job-search-sites.cy.ts**
   - Basic navigation and page loading tests

7. **wizard.cy.ts**
   - Match: `playwright/tests/wizard.spec.ts`
   - **Location**: `cypress/cypress/e2e/wizard.cy.ts`
   - **Reference**: `playwright/tests/wizard.spec.ts`
   - **Test Suite**: Wizard Tests (runs serially, not in parallel)
   - **Tests to Implement**:
     1. **test_home** - Click Home Navigation, Add Application button, then Cancel
        - Navigate to home page
        - Click Add Application button
        - Verify wizard step 1 loads
        - Click Cancel button
        - Verify back at applications page
     
     2. **test_application** - Click Applications Navigation, Add button, then Cancel
        - Navigate to applications page
        - Click Add button
        - Verify wizard step 1 loads
        - Click Cancel button
        - Verify back at applications page
     
     3. **test_companies** - Click Companies Navigation, Add button, populate all fields, then Cancel
        - Navigate to companies page
        - Click Add button
        - Verify company form loads
        - Populate all fields (name, address, city, state, zip, country, job_type)
        - Verify fields are populated
        - Click Cancel button
        - Verify back at companies page
     
     4. **test_contacts** - Click Contacts Navigation, Add button, populate all fields, then Cancel
        - Navigate to contacts page
        - Click Add button
        - Verify contact form loads
        - Populate all fields (first_name, last_name, title, linkedin, contact_type)
        - Verify fields are populated
        - Click Cancel button
        - Verify back at contacts page
     
     5. **test_clients** - Click Clients Navigation, Add button, populate all fields, then Cancel
        - Navigate to clients page
        - Click Add button
        - Verify client form loads
        - Populate all fields (name)
        - Verify field is populated
        - Click Cancel button
        - Verify back at clients page
     
     6. **test_notes** - Click Notes Navigation, verify there are no notes
        - Navigate to notes page
        - Verify page loads
        - Check if empty state is visible OR notes list is empty
        - Verify no notes exist
     
     7. **test_job_search_sites** - Click Job Search Sites Navigation, verify all Names and URLs
        - Navigate to job search sites page
        - Verify page loads
        - If sites table is visible:
          - Get all site rows
          - For each row, verify:
            - Name link exists and has text
            - URL exists (either as link or "N/A")
        - If empty state is visible, verify it
     
     8. **test_no_data** - Verify no applications, companies, contacts, clients, or notes were created
        - Use API calls to verify counts match initial counts
        - Verify no applications created
        - Verify no companies created
        - Verify no contacts created
        - Verify no clients created
        - Verify no notes created
   
   - **Key Requirements**:
     - Tests must run serially (use `describe.only` or Cypress's serial execution)
     - Get initial entity counts via API in `beforeEach`
     - Use environment config to determine backend URL
       - **Note**: Playwright uses `getEnvironmentConfig` from `playwright/config/port-config.ts`
       - **Cypress**: Either create similar utility in `cypress/cypress/support/config/port-config.ts` OR use `Cypress.env('BACKEND_URL')` with fallback to `http://localhost:8003`
     - Handle API errors gracefully
     - Use all page objects: HomePage, ApplicationsPage, CompaniesPage, CompanyFormPage, ContactsPage, ContactFormPage, ClientsPage, ClientFormPage, NotesPage, JobSearchSitesPage, WizardStep1Page
     - **File Path**: Must be created at `cypress/cypress/e2e/wizard.cy.ts` (matches Playwright's `playwright/tests/wizard.spec.ts`)

**Test Structure Pattern**:
```typescript
describe('Applications Tests', () => {
  let homePage: HomePage;
  let applicationsPage: ApplicationsPage;
  let formPage: ApplicationFormPage;
  let detailPage: ApplicationDetailPage;

  beforeEach(() => {
    cy.viewport(1920, 1080);
    homePage = new HomePage();
    applicationsPage = new ApplicationsPage();
    formPage = new ApplicationFormPage();
    detailPage = new ApplicationDetailPage();
  });

  it('should navigate to applications from home page', () => {
    homePage.navigate();
    homePage.verifyPageLoaded();
    homePage.clickApplications();
    cy.url().should('include', '/applications');
    applicationsPage.verifyPageLoaded();
  });

  // Additional tests...
});
```

**Wizard Test Structure Pattern** (Cypress-specific):
```typescript
describe('Wizard Tests', () => {
  // Run tests serially (one after another)
  // Note: Cypress runs tests serially by default, but we can use .only() or explicit ordering
  
  let homePage: HomePage;
  let applicationsPage: ApplicationsPage;
  let companiesPage: CompaniesPage;
  let companyFormPage: CompanyFormPage;
  let contactsPage: ContactsPage;
  let contactFormPage: ContactFormPage;
  let clientsPage: ClientsPage;
  let clientFormPage: ClientFormPage;
  let notesPage: NotesPage;
  let jobSearchSitesPage: JobSearchSitesPage;
  let wizardStep1Page: WizardStep1Page;
  let backendBaseUrl: string;
  let initialApplicationCount: number;
  let initialCompanyCount: number;
  let initialContactCount: number;
  let initialClientCount: number;
  let initialNoteCount: number;

  beforeEach(() => {
    cy.viewport(1920, 1080);
    
    // Initialize page objects
    homePage = new HomePage();
    applicationsPage = new ApplicationsPage();
    companiesPage = new CompaniesPage();
    companyFormPage = new CompanyFormPage();
    contactsPage = new ContactsPage();
    contactFormPage = new ContactFormPage();
    clientsPage = new ClientsPage();
    clientFormPage = new ClientFormPage();
    notesPage = new NotesPage();
    jobSearchSitesPage = new JobSearchSitesPage();
    wizardStep1Page = new WizardStep1Page();

    // Get backend URL from environment
    backendBaseUrl = Cypress.env('BACKEND_URL') || 'http://localhost:8003';

    // Get initial counts via API
    cy.request({
      method: 'GET',
      url: `${backendBaseUrl}/api/v1/applications?limit=1`,
      failOnStatusCode: false
    }).then((response) => {
      if (response.status === 200) {
        initialApplicationCount = response.body.total || 0;
      } else {
        initialApplicationCount = 0;
      }
    });

    // Similar for other entities...
  });

  it('test_home - Click Home Navigation, Add Application button, then Cancel', () => {
    homePage.navigate();
    homePage.verifyPageLoaded();
    homePage.clickAddApplication();
    wizardStep1Page.verifyPageLoaded();
    wizardStep1Page.cancel();
    applicationsPage.verifyPageLoaded();
  });

  // Additional tests...
});
```

**Cypress-Specific Considerations for Wizard Tests**:
- **API Calls**: Use `cy.request()` instead of Playwright's `request` API
- **Serial Execution**: Cypress runs tests serially by default, but ensure test order is maintained
- **Environment Variables**: Use `Cypress.env('BACKEND_URL')` or `Cypress.config('baseUrl')`
- **Error Handling**: Use `failOnStatusCode: false` in `cy.request()` to handle API errors gracefully
- **Async Handling**: Cypress automatically handles promises, but be careful with API calls in `beforeEach`

---

## Implementation Guidelines

### Cypress vs Playwright Differences

#### 1. Async/Await vs Chainable Commands

**Playwright**:
```typescript
async navigate(): Promise<void> {
  await this.page.goto(path);
  await this.waitForPageLoad();
}
```

**Cypress**:
```typescript
visit(path: string = '/'): void {
  cy.visit(path);
  this.waitForPageLoad();
}
```

**Key Points**:
- Cypress commands are automatically queued and executed
- No need for `await` in Cypress
- Cypress commands return chainable objects
- Use `cy.get()` instead of `page.locator()`

#### 2. Locator Syntax

**Playwright**:
```typescript
this.title = page.locator('[data-qa="applications-title"]');
await expect(this.title).toContainText('Applications');
```

**Cypress**:
```typescript
private readonly title = '[data-qa="applications-title"]';
cy.get(this.title).should('contain', 'Applications');
```

**Key Points**:
- Cypress uses string selectors stored as properties
- Use `cy.get(selector)` to get elements
- Use `.should()` for assertions

#### 3. Waiting and Visibility

**Playwright**:
```typescript
await locator.waitFor({ state: 'visible', timeout });
await expect(locator).toBeVisible();
```

**Cypress**:
```typescript
cy.get(selector).should('be.visible');
cy.get(selector, { timeout: 10000 }).should('be.visible');
```

**Key Points**:
- Cypress automatically waits for elements
- Use `.should('be.visible')` for visibility checks
- Set timeout in options: `{ timeout: 10000 }`

#### 4. Page Navigation

**Playwright**:
```typescript
await expect(page).toHaveURL(/.*\/applications/);
```

**Cypress**:
```typescript
cy.url().should('include', '/applications');
cy.url().should('match', /.*\/applications/);
```

#### 5. Dialog Handling

**Playwright**:
```typescript
page.on('dialog', async dialog => {
  expect(dialog.type()).toBe('confirm');
  await dialog.accept();
});
```

**Cypress**:
```typescript
cy.window().then((win) => {
  cy.stub(win, 'confirm').returns(true);
});
// Or use cy.on('window:confirm', () => true);
```

---

### Best Practices

#### 1. Use data-qa Selectors
- ✅ Always use `data-qa` attributes for selectors
- ✅ Match selectors exactly with Playwright implementation
- ✅ Document selector source in comments

#### 2. Consistent Method Names
- ✅ Match method names with Playwright implementation
- ✅ Use same parameter names and types
- ✅ Keep same method signatures where possible

#### 3. Error Handling
- ✅ Use Cypress's built-in retry logic
- ✅ Add appropriate timeouts for slow operations
- ✅ Handle network errors gracefully

#### 4. Test Data
- ✅ Use same test data structure as Playwright
- ✅ Generate unique test data (timestamps, random values)
- ✅ Clean up test data after tests (if needed)

#### 5. Page Object Structure
- ✅ Extend BasePage for all page objects
- ✅ Store selectors as private readonly properties
- ✅ Group related methods together
- ✅ Add JSDoc comments for all methods

---

## File Structure

### Target Structure

```
cypress/cypress/
├── e2e/
│   ├── homepage.cy.ts                    ✅ (exists)
│   ├── applications.cy.ts                ❌ (to create)
│   ├── companies.cy.ts                    ❌ (to create)
│   ├── contacts.cy.ts                     ❌ (to create)
│   ├── clients.cy.ts                     ❌ (to create)
│   ├── notes.cy.ts                       ❌ (to create)
│   ├── job-search-sites.cy.ts            ❌ (to create)
│   └── wizard.cy.ts                      ❌ (to create)
├── page-objects/
│   ├── BasePage.ts                       ✅ (exists, enhanced - Phase 1 complete)
│   ├── HomePage.ts                       ✅ (exists, may need updates)
│   ├── ApplicationsPage.ts               ✅ (Phase 2 complete)
│   ├── ApplicationDetailPage.ts           ✅ (Phase 4 complete)
│   ├── ApplicationFormPage.ts             ✅ (Phase 3 complete)
│   ├── CompaniesPage.ts                   ✅ (Phase 2 complete)
│   ├── CompanyFormPage.ts                 ✅ (Phase 3 complete)
│   ├── ContactsPage.ts                    ✅ (Phase 2 complete)
│   ├── ContactFormPage.ts                 ✅ (Phase 3 complete)
│   ├── ClientsPage.ts                     ✅ (Phase 2 complete)
│   ├── ClientFormPage.ts                  ✅ (Phase 3 complete)
│   ├── NotesPage.ts                       ✅ (Phase 2 complete)
│   ├── JobSearchSitesPage.ts              ✅ (Phase 2 complete)
│   └── WizardStep1Page.ts                 ❌ (to create)
└── support/
    ├── commands.ts                        ✅ (exists)
    └── e2e.ts                             ✅ (exists)
```

---

## Implementation Checklist

### Phase 1: BasePage Enhancement ✅ COMPLETE
- [x] Review Playwright BasePage
- [x] Add `waitForVisible()` method
- [x] Enhance `waitForPageLoad()` method
- [x] Add `getElement()` helper method
- [x] Add `clickElement()` helper method
- [x] Add `fillInput()` helper method
- [x] Add `selectOption()` helper method
- [x] Test BasePage methods

### Phase 2: List Page Objects ✅ COMPLETE
- [x] Create ApplicationsPage.ts
- [x] Create CompaniesPage.ts
- [x] Create ContactsPage.ts
- [x] Create ClientsPage.ts
- [x] Create NotesPage.ts
- [x] Create JobSearchSitesPage.ts
- [x] Test each page object individually (TypeScript compilation verified)

### Phase 3: Form Page Objects ✅ COMPLETE
- [x] Create ApplicationFormPage.ts
- [x] Create CompanyFormPage.ts
- [x] Create ContactFormPage.ts
- [x] Create ClientFormPage.ts
- [x] Test each page object individually (TypeScript compilation verified)

### Phase 4: Detail Page Objects ✅ COMPLETE
- [x] Create ApplicationDetailPage.ts
- [x] Test page object (TypeScript compilation verified)

### Phase 5: Wizard Page Objects
- [ ] Create WizardStep1Page.ts
- [ ] Test page object

### Phase 6: Test Files
- [ ] Create applications.cy.ts
- [ ] Create companies.cy.ts
- [ ] Create contacts.cy.ts
- [ ] Create clients.cy.ts
- [ ] Create notes.cy.ts
- [ ] Create job-search-sites.cy.ts
- [ ] Create wizard.cy.ts
  - [ ] Implement all 8 test cases matching Playwright wizard.spec.ts
  - [ ] Set up API calls for initial counts in beforeEach
  - [ ] Implement test_home (Home → Add Application → Cancel)
  - [ ] Implement test_application (Applications → Add → Cancel)
  - [ ] Implement test_companies (Companies → Add → Fill → Cancel)
  - [ ] Implement test_contacts (Contacts → Add → Fill → Cancel)
  - [ ] Implement test_clients (Clients → Add → Fill → Cancel)
  - [ ] Implement test_notes (Notes → Verify empty)
  - [ ] Implement test_job_search_sites (Job Search Sites → Verify names/URLs)
  - [ ] Implement test_no_data (API verification of no data created)
  - [ ] Verify tests run serially
  - [ ] Verify all page objects are used correctly
- [ ] Run all tests and verify they pass

### Phase 7: Documentation & Cleanup
- [ ] Update Cypress README with new page objects
- [ ] Document any Cypress-specific patterns
- [ ] Review and align with Playwright implementation
- [ ] Verify all tests pass in CI/CD

---

## Testing Strategy

### Unit Testing Page Objects
- Test each page object method individually
- Verify selectors are correct
- Test error handling

### Integration Testing
- Run full test suites
- Verify tests match Playwright behavior
- Check for any Cypress-specific issues

### CI/CD Integration
- Ensure tests run in CI pipeline
- Verify tests pass in all environments
- Check test execution time

---

## Success Criteria

1. ✅ All Playwright page objects have Cypress equivalents
2. ✅ All Playwright tests have Cypress equivalents
3. ✅ Tests use identical `data-qa` selectors
4. ✅ Tests follow same test patterns and structure
5. ✅ All tests pass in CI/CD pipeline
6. ✅ Documentation is updated
7. ✅ Code follows Cypress best practices

---

## Notes

### Cypress-Specific Considerations

1. **Automatic Waiting**: Cypress automatically waits for elements, so we don't need explicit waits in most cases
2. **Command Queueing**: Cypress commands are queued and executed automatically
3. **Retry Logic**: Cypress has built-in retry logic for assertions
4. **Network Stubbing**: Cypress can stub network requests easily with `cy.intercept()`

### Differences from Playwright

1. **No async/await**: Cypress uses chainable commands instead
2. **Selector Storage**: Store selectors as strings, not Locator objects
3. **Assertions**: Use `.should()` instead of `expect()`
4. **Page Navigation**: Use `cy.url()` instead of `page.url()`

---

## References

- **Playwright Page Objects**: `playwright/tests/pages/`
- **Playwright Tests**: `playwright/tests/`
- **Cypress Documentation**: https://docs.cypress.io/
- **Cypress Best Practices**: https://docs.cypress.io/guides/references/best-practices
- **Existing Cypress Implementation**: `cypress/cypress/page-objects/`

---

**Last Updated**: 2026-01-11  
**Document Status**: Planning Document - Ready for Implementation
