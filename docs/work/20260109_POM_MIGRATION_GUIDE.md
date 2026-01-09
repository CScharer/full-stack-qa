# Page Object Model (POM) Migration Guide

**Date Created**: 2026-01-09  
**Last Updated**: 2026-01-10  
**Status**: ✅ **COMPLETE** - HomePage POM Migration Finished for All Frameworks  
**Purpose**: Comprehensive guide for converting tests to Page Object Model pattern across Cypress, Playwright, and Robot Framework  
**Document Location**: `docs/work/20260109_POM_MIGRATION_GUIDE.md`

---

## 📋 Executive Summary

This document provides a comprehensive guide for migrating tests to the **Page Object Model (POM)** pattern across three testing frameworks:

- **Cypress** - JavaScript/TypeScript E2E testing
- **Playwright** - Node.js multi-browser automation
- **Robot Framework** - Python-based acceptance testing

### Overall Migration Status

| Framework | Status | HomePage POM | Tests Passing |
|-----------|--------|--------------|---------------|
| **Cypress** | ✅ Complete | ✅ Implemented | ✅ Verified |
| **Playwright** | ✅ Complete | ✅ Implemented | ✅ Verified |
| **Robot Framework** | ✅ Complete | ✅ Implemented | ✅ Verified |

### Benefits of Page Object Model

- ✅ **Maintainability**: Selectors defined in one place
- ✅ **Reusability**: Page objects shared across multiple tests
- ✅ **Readability**: Tests focus on behavior, not implementation details
- ✅ **Scalability**: Easier to add new tests as application grows
- ✅ **Team Collaboration**: Clear separation of concerns
- ✅ **Consistency**: All frameworks use `data-qa` attributes from frontend

### Data-QA Attribute Strategy

All three frameworks now use consistent `data-qa` selectors from the frontend:

- **Home Page Title**: `[data-qa="home-title"]` (from `frontend/app/page.tsx`)
- **Sidebar Elements**: `[data-qa="sidebar"]`, `[data-qa="sidebar-title"]`, `[data-qa="sidebar-navigation"]`
- **Navigation Cards**: `[data-qa="sidebar-nav-applications"]`, `[data-qa="sidebar-nav-companies"]`, etc. (from `frontend/components/Sidebar.tsx`)

---

## 🎯 Framework-Specific Migrations

### 1. Cypress POM Migration

**Status**: ✅ **COMPLETE** - HomePage POM Migration Finished

#### Current State
- ✅ `cypress/cypress/page-objects/BasePage.ts` - Base class with common methods
- ✅ `cypress/cypress/page-objects/HomePage.ts` - HomePage with data-qa selectors
- ✅ `cypress/cypress/e2e/homepage.cy.ts` - Migrated to use HomePage POM
- ✅ All tests passing

#### Directory Structure
```
cypress/
├── cypress/
│   ├── e2e/
│   │   └── homepage.cy.ts          # Test files (behavior-focused)
│   ├── page-objects/              # Page Object classes
│   │   ├── BasePage.ts            # Base class with common methods
│   │   └── HomePage.ts            # HomePage Page Object
│   └── support/
│       ├── commands.ts
│       └── e2e.ts
├── cypress.config.ts
└── package.json
```

#### Implementation Details

**BasePage.ts** - Common functionality:
```typescript
export class BasePage {
  visit(path: string = '/'): void
  waitForPageLoad(): void
  getTitle(): Cypress.Chainable<string>
  verifyTitleContains(text: string): void
  setViewport(width: number, height: number): void
}
```

**HomePage.ts** - Extends BasePage with:
- Selectors using `data-qa` attributes
- Methods: `navigate()`, `verifyPageLoaded()`, `verifySidebarVisible()`, `verifyNavigationTitle()`, `verifyNavigationElements()`, `clickApplications()`, etc.

**Test File** (`homepage.cy.ts`):
```typescript
import { HomePage } from '../page-objects/HomePage';

describe('HomePage', () => {
  let homePage: HomePage;

  beforeEach(() => {
    homePage = new HomePage();
    homePage.setViewport(1920, 1080);
    homePage.navigate();
  });

  it('should load the home page', () => {
    homePage.verifyPageLoaded();
  });

  it('should display the navigation panel', () => {
    homePage.verifySidebarVisible();
    homePage.verifyNavigationTitle('Navigation');
    homePage.verifyNavigationElements();
  });
});
```

#### Migration Checklist
- [x] Create `cypress/cypress/page-objects/` directory
- [x] Create `BasePage.ts` with common functionality
- [x] Create `HomePage.ts` page object
- [x] Migrate `homepage.cy.ts` to use `HomePage`
- [x] Add `data-qa` attributes to `frontend/components/Sidebar.tsx`
- [x] Update `HomePage.ts` to use `data-qa` selectors
- [x] Test migrated tests pass

#### Verification
```bash
cd cypress
npm run cypress:run
# Result: 2 tests passing
```

---

### 2. Playwright POM Migration

**Status**: ✅ **COMPLETE** - HomePage POM Migration Finished

#### Current State
- ✅ `playwright/tests/pages/BasePage.ts` - Base class with common methods
- ✅ `playwright/tests/pages/HomePage.ts` - HomePage extending BasePage with data-qa selectors
- ✅ `playwright/tests/homepage.spec.ts` - Migrated to use HomePage POM
- ✅ `playwright/tests/integration/pages/HomePage.ts` - Updated to use data-qa selectors
- ✅ All tests passing

#### Directory Structure
```
playwright/
├── tests/
│   ├── integration/
│   │   ├── pages/                    # Existing Page Objects (keep)
│   │   │   └── HomePage.ts           # Updated with data-qa selectors
│   │   ├── applications.spec.ts      # Uses POM ✅
│   │   └── companies.spec.ts         # Uses POM ✅
│   ├── pages/                        # NEW: Shared Page Objects
│   │   ├── BasePage.ts              # Base class with common methods
│   │   └── HomePage.ts              # Enhanced HomePage
│   └── homepage.spec.ts             # Migrated to use POM
├── playwright.config.ts
└── package.json
```

#### Implementation Details

**BasePage.ts** - Common functionality:
```typescript
export class BasePage {
  readonly page: Page;
  constructor(page: Page)
  async navigate(path: string = '/'): Promise<void>
  async waitForPageLoad(): Promise<void>
  async getTitle(): Promise<string>
  async verifyTitleContains(text: string | RegExp): Promise<void>
  async setViewport(width: number, height: number): Promise<void>
  async waitForVisible(locator: Locator, timeout: number): Promise<void>
}
```

**HomePage.ts** - Extends BasePage with:
- Selectors using `data-qa` attributes (`home-title`, `sidebar`, `sidebar-nav-*`)
- Methods: `navigate()`, `verifyPageLoaded()`, `verifySidebarVisible()`, `verifyNavigationTitle()`, `verifyNavigationElements()`, `clickApplications()`, etc.

**Test File** (`homepage.spec.ts`):
```typescript
import { test } from '@playwright/test';
import { HomePage } from './pages/HomePage';

test.describe('HomePage', () => {
  let homePage: HomePage;

  test.beforeEach(async ({ page }) => {
    homePage = new HomePage(page);
    await homePage.setViewport(1920, 1080);
    await homePage.navigate();
  });

  test('should load the home page', async () => {
    await homePage.verifyPageLoaded();
  });

  test('should display the navigation panel', async () => {
    await homePage.verifySidebarVisible();
    await homePage.verifyNavigationTitle('Navigation');
    await homePage.verifyNavigationElements();
  });
});
```

#### Migration Checklist
- [x] Create `playwright/tests/pages/` directory
- [x] Create `BasePage.ts` with common functionality
- [x] Create enhanced `HomePage.ts` in `tests/pages/`
- [x] Migrate `homepage.spec.ts` to use Page Object
- [x] Update `playwright/tests/integration/pages/HomePage.ts` to use data-qa selectors
- [x] Test migrated tests pass

#### Verification
```bash
cd playwright
npm run test:chrome
# Result: 2 tests passing (homepage.spec.ts)
```

---

### 3. Robot Framework POM Migration

**Status**: ✅ **COMPLETE** - HomePage POM Migration Finished

#### Current State
- ✅ `src/test/robot/resources/Common.robot` - Common keywords and variables
- ✅ `src/test/robot/resources/HomePage.robot` - HomePage Page Object with data-qa selectors
- ✅ `src/test/robot/HomePageTests.robot` - Migrated to use Resource files
- ✅ All tests passing

#### Directory Structure
```
src/test/robot/
├── HomePageTests.robot          # Test files (behavior-focused)
├── APITests.robot
├── resources/                    # Page Object Resources
│   ├── Common.robot             # Common keywords and variables
│   └── HomePage.robot           # HomePage Page Object
├── WebDriverManager.py
└── README.md
```

#### Implementation Details

**Common.robot** - Common keywords and variables:
```robot
*** Variables ***
${BASE_URL}               http://localhost:3003
${SELENIUM_REMOTE_URL}    ${EMPTY}
${BROWSER}                chrome
${TIMEOUT}                10s
${SHORT_TIMEOUT}          5s

*** Keywords ***
Setup WebDriver And Open Browser
Close Browser And Cleanup
Navigate To Page    [Arguments]    ${path}=${EMPTY}
Verify Page Title Contains    [Arguments]    ${expected_text}
Verify Page Loaded
```

**HomePage.robot** - HomePage Page Object:
```robot
*** Variables ***
${HOME_PAGE_SIDEBAR}                  css:[data-qa="sidebar"]
${HOME_PAGE_SIDEBAR_TITLE}            css:[data-qa="sidebar-title"]
${HOME_PAGE_SIDEBAR_NAVIGATION}       css:[data-qa="sidebar-navigation"]
${HOME_PAGE_SIDEBAR_NAV_HOME}         css:[data-qa="sidebar-nav-home"]
${HOME_PAGE_APPLICATIONS_CARD}        css:[data-qa="sidebar-nav-applications"]
# ... other navigation cards

*** Keywords ***
Navigate To Home Page
Verify Home Page Loaded
Verify Sidebar Visible
Verify Navigation Title    [Arguments]    ${expected_text}=Navigation
Verify Navigation Elements Present
Click Applications Card
# ... other click methods
```

**Test File** (`HomePageTests.robot`):
```robot
*** Settings ***
Resource          ${CURDIR}${/}resources${/}Common.robot
Resource          ${CURDIR}${/}resources${/}HomePage.robot
Test Setup        Setup WebDriver And Open Browser
Test Teardown     Close Browser And Cleanup

*** Test Cases ***
Home Page Should Load
    Navigate To Home Page
    Verify Home Page Loaded

Home Page Should Display Navigation Panel
    Navigate To Home Page
    Verify Sidebar Visible
    Verify Navigation Title    Navigation
    Verify Navigation Elements Present
```

#### Migration Checklist
- [x] Create `src/test/robot/resources/` directory
- [x] Create `Common.robot` with common keywords and variables
- [x] Create `HomePage.robot` resource file with data-qa selectors
- [x] Migrate `HomePageTests.robot` to use Resource files
- [x] Test migrated tests pass

#### Verification
```bash
cd src/test/robot
python3 -m robot --variable BASE_URL:http://localhost:3003 \
  --outputdir ../../../target/robot-reports \
  HomePageTests.robot
# Result: 2 tests passing
```

---

## 🔧 Best Practices (All Frameworks)

### 1. Selector Strategy

**Use data-qa attributes** (consistent across all frameworks):
- **Cypress**: `private readonly sidebar = '[data-qa="sidebar"]';`
- **Playwright**: `readonly sidebar = page.locator('[data-qa="sidebar"]');`
- **Robot Framework**: `${HOME_PAGE_SIDEBAR}    css:[data-qa="sidebar"]`

**Fallback selectors** (when data-qa not available):
- CSS selectors: `css:h1.display-5, css:h1.display-4, css:h1`
- XPath (Robot Framework): `xpath://table//tbody//tr[contains(., '${position}')]`
- Role-based (Playwright): `page.getByRole('button', { name: 'Submit' })`

### 2. Method/Keyword Naming Conventions

**Actions**: Use verbs (`click`, `navigate`, `fill`, `select`)
- Cypress: `clickApplications()`, `fillForm()`
- Playwright: `async clickApplications(): Promise<void>`
- Robot Framework: `Click Applications Card`

**Verifications**: Use `verify` prefix
- Cypress: `verifyPageLoaded()`, `verifySidebarVisible()`
- Playwright: `async verifyPageLoaded(): Promise<void>`
- Robot Framework: `Verify Home Page Loaded`, `Verify Sidebar Visible`

**Getters**: Use `get` prefix
- Cypress: `getTitle()`, `getApplicationRow()`
- Playwright: `async getTitle(): Promise<string>`
- Robot Framework: `Get Application Row`

### 3. Page Object Structure

**Cypress**:
```typescript
export class PageName extends BasePage {
  // 1. Private selectors (at the top)
  private readonly selector1 = '...';
  // 2. Navigation methods
  navigate(): void { }
  // 3. Action methods
  clickButton(): void { }
  // 4. Verification methods
  verifySomething(): void { }
}
```

**Playwright**:
```typescript
export class PageName extends BasePage {
  // 1. Readonly selectors (at the top)
  readonly selector1: Locator;
  // 2. Constructor
  constructor(page: Page) { super(page); }
  // 3. Navigation methods
  async navigate(): Promise<void> { }
  // 4. Action methods
  async clickButton(): Promise<void> { }
  // 5. Verification methods
  async verifySomething(): Promise<void> { }
}
```

**Robot Framework**:
```robot
*** Settings ***
Resource          Common.robot

*** Variables ***
${SELECTOR_1}    css:...

*** Keywords ***
Navigate To Page Name
Click Button Name
Verify Something
```

### 4. Waiting Strategies

**Explicit waits** (preferred):
- **Cypress**: `cy.get(this.element).should('be.visible');`
- **Playwright**: `await locator.waitFor({ state: 'visible', timeout: 10000 });`
- **Robot Framework**: `Wait Until Element Is Visible    ${SELECTOR}    timeout=${TIMEOUT}`

**Avoid hard-coded waits**:
- ❌ Bad: `cy.wait(1000)`, `await page.waitForTimeout(1000)`, `Sleep    5s`
- ✅ Good: Use framework-specific wait mechanisms with conditions

### 5. Error Handling

**Add timeout messages**:
- **Cypress**: `cy.get(this.sidebar, { timeout: 10000 }).should('be.visible');`
- **Playwright**: `await this.sidebar.waitFor({ state: 'visible', timeout: 10000 });`
- **Robot Framework**: `Wait Until Element Is Visible    ${HOME_PAGE_SIDEBAR}    timeout=${SHORT_TIMEOUT}`

---

## 📊 Combined Migration Checklist

### Phase 1: Foundation ✅ COMPLETE (All Frameworks)
- [x] **Cypress**: Create `cypress/cypress/page-objects/` directory and `BasePage.ts`
- [x] **Playwright**: Create `playwright/tests/pages/` directory and `BasePage.ts`
- [x] **Robot Framework**: Create `src/test/robot/resources/` directory and `Common.robot`

### Phase 2: Core Pages ✅ COMPLETE (All Frameworks)
- [x] **Cypress**: Create `HomePage.ts`, migrate `homepage.cy.ts`, add data-qa to Sidebar.tsx
- [x] **Playwright**: Create `HomePage.ts`, migrate `homepage.spec.ts`, update integration HomePage.ts
- [x] **Robot Framework**: Create `HomePage.robot`, migrate `HomePageTests.robot`
- [x] All tests verified passing locally

### Phase 3: Additional Pages (As Needed)
- [ ] Create `ApplicationsPage` for all frameworks
- [ ] Create `CompaniesPage` for all frameworks
- [ ] Create `ContactsPage` for all frameworks
- [ ] Create `ClientsPage` for all frameworks
- [ ] Create `NotesPage` for all frameworks
- [ ] Create `JobSearchSitesPage` for all frameworks

### Phase 4: Advanced Features
- [ ] Create component objects (for reusable UI components)
- [ ] Add helper utilities
- [ ] Create page factory pattern (if needed)

### Phase 5: Documentation & Cleanup
- [x] Update test documentation (this document)
- [x] Add JSDoc/Documentation comments to page objects
- [ ] Review and refactor for consistency
- [ ] Remove duplicate code

---

## 🎯 Migration Completion Summary

**Completed**: 2026-01-10

### Cypress POM Migration ✅

1. **Page Objects Created**:
   - `cypress/cypress/page-objects/BasePage.ts` - Base class with common methods
   - `cypress/cypress/page-objects/HomePage.ts` - HomePage with data-qa selectors

2. **Tests Migrated**:
   - `cypress/cypress/e2e/homepage.cy.ts` - Now uses `HomePage` Page Object

3. **Application Updates**:
   - `frontend/components/Sidebar.tsx` - Navigation `Link` components have `data-qa` attributes

4. **Verification**: All Cypress tests pass (`npm run cypress:run`)

### Playwright POM Migration ✅

1. **Shared Page Objects Created**:
   - `playwright/tests/pages/BasePage.ts` - Base class with common methods
   - `playwright/tests/pages/HomePage.ts` - HomePage extending BasePage with data-qa selectors

2. **Tests Migrated**:
   - `playwright/tests/homepage.spec.ts` - Now uses `HomePage` page object

3. **Integration HomePage Enhanced**:
   - `playwright/tests/integration/pages/HomePage.ts` - Updated to use data-qa selectors

4. **Verification**: `homepage.spec.ts` tests pass (`npm run test:chrome`)

### Robot Framework POM Migration ✅

1. **Resource Files Created**:
   - `src/test/robot/resources/Common.robot` - Common keywords and variables
   - `src/test/robot/resources/HomePage.robot` - HomePage Page Object with data-qa selectors

2. **Test File Migrated**:
   - `src/test/robot/HomePageTests.robot` - Now imports Resource files, uses Page Object keywords

3. **Verification**: Tests pass when run locally with `python3 -m robot`

---

## 🔍 Framework-Specific Troubleshooting

### Cypress

**Issue**: TypeScript cannot find module
```typescript
// Error: Cannot find module '../page-objects/HomePage'
```
**Solution**: Ensure `/// <reference types="cypress" />` is at the top of the file.

**Issue**: Cypress commands not recognized
```typescript
// Error: Property 'get' does not exist on type 'HomePage'
```
**Solution**: Ensure `/// <reference types="cypress" />` is at the top of the file.

### Playwright

**Issue**: TypeScript cannot find module
```typescript
// Error: Cannot find module './pages/HomePage'
```
**Solution**: Ensure TypeScript paths are configured correctly in `tsconfig.json`.

**Issue**: Page Object methods not awaiting
```typescript
// Error: A promise was returned but not awaited
homePage.navigate(); // Missing await
```
**Solution**: Always use `await` with async methods: `await homePage.navigate();`

**Issue**: Locator not found
```typescript
// Error: locator.click: Target closed
```
**Solution**: Ensure page is loaded and element is visible before clicking.

### Robot Framework

**Issue**: Resource file not found
```robot
# Error: Resource file 'resources/HomePage.robot' not found
```
**Solution**: Use correct path relative to test file: `Resource ${CURDIR}${/}resources${/}HomePage.robot`

**Issue**: Variable not found
```robot
# Error: Variable '${HOME_PAGE_SIDEBAR}' not found
```
**Solution**: Ensure Resource file is imported in Settings section.

**Issue**: Keyword not found
```robot
# Error: No keyword with name 'Navigate To Home Page' found
```
**Solution**: Ensure keyword exists in imported Resource file and spelling matches exactly.

**Issue**: Circular dependency
```robot
# Error: Circular resource file import detected
```
**Solution**: Avoid circular imports. Use Common.robot for shared functionality.

---

## 📚 Additional Resources

### General
- [Page Object Model Pattern](https://martinfowler.com/bliki/PageObject.html)

### Cypress
- [Cypress Best Practices](https://docs.cypress.io/guides/references/best-practices)
- [Cypress TypeScript Support](https://docs.cypress.io/guides/tooling/typescript-support)

### Playwright
- [Playwright Best Practices](https://playwright.dev/docs/best-practices)
- [Playwright TypeScript Support](https://playwright.dev/docs/test-typescript)
- [Playwright Locators](https://playwright.dev/docs/locators)

### Robot Framework
- [Robot Framework User Guide](https://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html)
- [Robot Framework Resource Files](https://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html#resource-files)
- [SeleniumLibrary Documentation](https://robotframework.org/SeleniumLibrary/SeleniumLibrary.html)

---

## ✅ Success Criteria

Migration is complete when:

1. ✅ All test files use Page Objects instead of direct selectors
2. ✅ Selectors are centralized in Page Object classes/Resource files
3. ✅ Tests are more readable and focused on behavior
4. ✅ Page Objects follow consistent naming conventions
5. ✅ All tests pass after migration
6. ✅ Code is maintainable and scalable
7. ✅ All frameworks use consistent `data-qa` selectors from frontend

---

## 🔄 Cross-Framework Consistency

### Data-QA Attribute Mapping

All frameworks use the same `data-qa` attributes from the frontend:

| Frontend Element | data-qa Attribute | Cypress Selector | Playwright Selector | Robot Framework Variable |
|------------------|-------------------|------------------|---------------------|--------------------------|
| Home Page Title | `home-title` | `[data-qa="home-title"]` | `page.locator('[data-qa="home-title"]')` | `${HOME_PAGE_TITLE}` |
| Sidebar | `sidebar` | `[data-qa="sidebar"]` | `page.locator('[data-qa="sidebar"]')` | `${HOME_PAGE_SIDEBAR}` |
| Sidebar Title | `sidebar-title` | `[data-qa="sidebar-title"]` | `page.locator('[data-qa="sidebar-title"]')` | `${HOME_PAGE_SIDEBAR_TITLE}` |
| Applications Card | `sidebar-nav-applications` | `[data-qa="sidebar-nav-applications"]` | `page.locator('[data-qa="sidebar-nav-applications"]')` | `${HOME_PAGE_APPLICATIONS_CARD}` |
| Companies Card | `sidebar-nav-companies` | `[data-qa="sidebar-nav-companies"]` | `page.locator('[data-qa="sidebar-nav-companies"]')` | `${HOME_PAGE_COMPANIES_CARD}` |
| Contacts Card | `sidebar-nav-contacts` | `[data-qa="sidebar-nav-contacts"]` | `page.locator('[data-qa="sidebar-nav-contacts"]')` | `${HOME_PAGE_CONTACTS_CARD}` |
| Clients Card | `sidebar-nav-clients` | `[data-qa="sidebar-nav-clients"]` | `page.locator('[data-qa="sidebar-nav-clients"]')` | `${HOME_PAGE_CLIENTS_CARD}` |
| Notes Card | `sidebar-nav-notes` | `[data-qa="sidebar-nav-notes"]` | `page.locator('[data-qa="sidebar-nav-notes"]')` | `${HOME_PAGE_NOTES_CARD}` |
| Job Search Sites Card | `sidebar-nav-job-search-sites` | `[data-qa="sidebar-nav-job-search-sites"]` | `page.locator('[data-qa="sidebar-nav-job-search-sites"]')` | `${HOME_PAGE_JOB_SEARCH_SITES_CARD}` |

### Method/Keyword Naming Consistency

| Action | Cypress | Playwright | Robot Framework |
|--------|---------|------------|-----------------|
| Navigate to home | `navigate()` | `async navigate()` | `Navigate To Home Page` |
| Verify page loaded | `verifyPageLoaded()` | `async verifyPageLoaded()` | `Verify Home Page Loaded` |
| Verify sidebar visible | `verifySidebarVisible()` | `async verifySidebarVisible()` | `Verify Sidebar Visible` |
| Click applications | `clickApplications()` | `async clickApplications()` | `Click Applications Card` |

---

**Last Updated**: 2026-01-10  
**Status**: ✅ **COMPLETE** - HomePage POM migration finished for Cypress, Playwright, and Robot Framework. All tests passing.
