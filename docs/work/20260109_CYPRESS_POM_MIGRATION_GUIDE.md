# Cypress Page Object Model (POM) Migration Guide

**Date Created**: 2026-01-09  
**Status**: 📋 Migration Planning Document  
**Purpose**: Guide for converting Cypress tests from direct selectors to Page Object Model pattern  
**Current State**: Tests use direct selectors in test files  
**Target State**: All tests use Page Object Model pattern

---

## 📋 Executive Summary

This document outlines the strategy and step-by-step process for migrating Cypress tests from the current direct selector approach to the **Page Object Model (POM)** pattern. This migration will improve test maintainability, reusability, and readability.

### Benefits of Page Object Model

- ✅ **Maintainability**: Selectors defined in one place
- ✅ **Reusability**: Page objects can be shared across multiple tests
- ✅ **Readability**: Tests focus on behavior, not implementation details
- ✅ **Scalability**: Easier to add new tests as application grows
- ✅ **Team Collaboration**: Clear separation of concerns

---

## 🎯 Current State Analysis

### Current Test Structure

**Location**: `cypress/cypress/e2e/homepage.cy.ts`

```typescript
describe('HomePage', () => {
  beforeEach(() => {
    cy.viewport(1920, 1080);
    cy.visit('/');
    cy.get('body').should('be.visible');
  });

  it('should load the home page', () => {
    cy.title().should('contain', 'Job Search Application');
    cy.get('body').should('be.visible');
  });

  it('should display the navigation panel', () => {
    cy.get('[data-qa="sidebar"]').should('be.visible');
    cy.get('[data-qa="sidebar-title"]').contains('Navigation').should('be.visible');
    cy.get('[data-qa="sidebar-navigation"]').should('be.visible');
    cy.get('[data-qa="sidebar-nav-home"]').should('be.visible');
  });
});
```

### Issues with Current Approach

1. **Selectors scattered**: Each test file contains its own selectors
2. **No reusability**: Same selectors repeated across multiple tests
3. **Hard to maintain**: UI changes require updating multiple files
4. **Mixed concerns**: Test logic mixed with page interaction details

---

## 🔄 Target Architecture

### Directory Structure

```
cypress/
├── cypress/
│   ├── e2e/
│   │   ├── homepage.cy.ts          # Test files (behavior-focused)
│   │   ├── applications.cy.ts
│   │   └── companies.cy.ts
│   ├── page-objects/              # NEW: Page Object classes
│   │   ├── HomePage.ts
│   │   ├── ApplicationsPage.ts
│   │   ├── CompaniesPage.ts
│   │   └── BasePage.ts            # Base class with common methods
│   └── support/
│       ├── commands.ts
│       └── e2e.ts
├── cypress.config.ts
└── package.json
```

---

## 📝 Step-by-Step Migration Process

### Step 1: Create Page Objects Directory

**Action**: Create the directory structure for page objects.

```bash
mkdir -p cypress/cypress/page-objects
```

---

### Step 2: Create Base Page Class

**File**: `cypress/cypress/page-objects/BasePage.ts`

**Purpose**: Common functionality shared across all pages (navigation, waiting, common actions).

```typescript
/// <reference types="cypress" />

/**
 * Base Page Object class
 * Contains common methods and properties shared across all pages
 */
export class BasePage {
  /**
   * Visit the page
   * @param path - Relative path from base URL (e.g., '/', '/applications')
   */
  visit(path: string = '/'): void {
    cy.visit(path);
    this.waitForPageLoad();
  }

  /**
   * Wait for page to load
   * Verifies body is visible as a basic page load check
   */
  waitForPageLoad(): void {
    cy.get('body').should('be.visible');
  }

  /**
   * Get page title
   */
  getTitle(): Cypress.Chainable<string> {
    return cy.title();
  }

  /**
   * Verify page title contains text
   * @param text - Text to verify in title
   */
  verifyTitleContains(text: string): void {
    cy.title().should('contain', text);
  }

  /**
   * Set viewport size
   * @param width - Viewport width
   * @param height - Viewport height
   */
  setViewport(width: number = 1920, height: number = 1080): void {
    cy.viewport(width, height);
  }
}
```

---

### Step 3: Create HomePage Page Object

**File**: `cypress/cypress/page-objects/HomePage.ts`

**Purpose**: Encapsulate all HomePage selectors and actions.

```typescript
/// <reference types="cypress" />
import { BasePage } from './BasePage';

/**
 * Page Object Model for the Home Page
 */
export class HomePage extends BasePage {
  // Selectors
  private readonly sidebar = '[data-qa="sidebar"]';
  private readonly sidebarTitle = '[data-qa="sidebar-title"]';
  private readonly sidebarNavigation = '[data-qa="sidebar-navigation"]';
  private readonly sidebarNavHome = '[data-qa="sidebar-nav-home"]';
  private readonly applicationsCard = 'a[href="/applications"]';
  private readonly companiesCard = 'a[href="/companies"]';
  private readonly contactsCard = 'a[href="/contacts"]';
  private readonly clientsCard = 'a[href="/clients"]';
  private readonly notesCard = 'a[href="/notes"]';
  private readonly jobSearchSitesCard = 'a[href="/job-search-sites"]';

  /**
   * Navigate to home page
   */
  navigate(): void {
    this.visit('/');
  }

  /**
   * Verify page has loaded
   */
  verifyPageLoaded(): void {
    this.waitForPageLoad();
    this.verifyTitleContains('Job Search Application');
  }

  /**
   * Verify sidebar is visible
   */
  verifySidebarVisible(): void {
    cy.get(this.sidebar).should('be.visible');
  }

  /**
   * Verify navigation title contains text
   * @param text - Text to verify (default: 'Navigation')
   */
  verifyNavigationTitle(text: string = 'Navigation'): void {
    cy.get(this.sidebarTitle).contains(text).should('be.visible');
  }

  /**
   * Verify navigation elements are present
   */
  verifyNavigationElements(): void {
    cy.get(this.sidebarNavigation).should('be.visible');
    cy.get(this.sidebarNavHome).should('be.visible');
  }

  /**
   * Click Applications card
   */
  clickApplications(): void {
    cy.get(this.applicationsCard).click();
  }

  /**
   * Click Companies card
   */
  clickCompanies(): void {
    cy.get(this.companiesCard).click();
  }

  /**
   * Click Contacts card
   */
  clickContacts(): void {
    cy.get(this.contactsCard).click();
  }

  /**
   * Click Clients card
   */
  clickClients(): void {
    cy.get(this.clientsCard).click();
  }

  /**
   * Click Notes card
   */
  clickNotes(): void {
    cy.get(this.notesCard).click();
  }

  /**
   * Click Job Search Sites card
   */
  clickJobSearchSites(): void {
    cy.get(this.jobSearchSitesCard).click();
  }

  /**
   * Verify all navigation cards are visible
   */
  verifyAllCardsVisible(): void {
    cy.get(this.applicationsCard).should('be.visible');
    cy.get(this.companiesCard).should('be.visible');
    cy.get(this.contactsCard).should('be.visible');
    cy.get(this.clientsCard).should('be.visible');
    cy.get(this.notesCard).should('be.visible');
    cy.get(this.jobSearchSitesCard).should('be.visible');
  }
}
```

---

### Step 4: Update Test File to Use Page Object

**File**: `cypress/cypress/e2e/homepage.cy.ts` (Updated)

**Before** (Direct selectors):
```typescript
describe('HomePage', () => {
  beforeEach(() => {
    cy.viewport(1920, 1080);
    cy.visit('/');
    cy.get('body').should('be.visible');
  });

  it('should load the home page', () => {
    cy.title().should('contain', 'Job Search Application');
    cy.get('body').should('be.visible');
  });

  it('should display the navigation panel', () => {
    cy.get('[data-qa="sidebar"]').should('be.visible');
    cy.get('[data-qa="sidebar-title"]').contains('Navigation').should('be.visible');
    cy.get('[data-qa="sidebar-navigation"]').should('be.visible');
    cy.get('[data-qa="sidebar-nav-home"]').should('be.visible');
  });
});
```

**After** (Using Page Object):
```typescript
/// <reference types="cypress" />
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

---

## 🔧 Best Practices

### 1. Selector Strategy

**Use data-qa attributes** (already in use):
```typescript
private readonly sidebar = '[data-qa="sidebar"]';
```

**Fallback selectors** (when data-qa not available):
```typescript
private readonly title = 'h1.display-5, h1.display-4, h1';
```

### 2. Method Naming Conventions

- **Actions**: Use verbs (`click`, `navigate`, `fill`, `select`)
  - `clickApplications()`
  - `fillForm()`
  - `selectOption()`

- **Verifications**: Use `verify` or `should` prefix
  - `verifyPageLoaded()`
  - `verifySidebarVisible()`
  - `shouldBeVisible()`

- **Getters**: Use `get` prefix
  - `getTitle()`
  - `getApplicationRow()`

### 3. Page Object Structure

```typescript
export class PageName extends BasePage {
  // 1. Private selectors (at the top)
  private readonly selector1 = '...';
  private readonly selector2 = '...';

  // 2. Constructor (if needed)
  constructor() {
    super();
  }

  // 3. Navigation methods
  navigate(): void { }

  // 4. Action methods (click, fill, etc.)
  clickButton(): void { }

  // 5. Verification methods
  verifySomething(): void { }

  // 6. Helper/utility methods
  private helperMethod(): void { }
}
```

### 4. Waiting Strategies

**Explicit waits** (preferred):
```typescript
cy.get(this.element).should('be.visible');
cy.get(this.element).should('contain', 'text');
```

**Avoid implicit waits**:
```typescript
// ❌ Bad
cy.wait(1000);

// ✅ Good
cy.get(this.element).should('be.visible');
```

### 5. Error Handling

**Add timeout messages**:
```typescript
verifySidebarVisible(): void {
  cy.get(this.sidebar, { timeout: 10000 })
    .should('be.visible');
}
```

---

## 📊 Migration Checklist

### Phase 1: Setup (Foundation)
- [ ] Create `cypress/cypress/page-objects/` directory
- [ ] Create `BasePage.ts` with common functionality
- [ ] Update TypeScript config if needed (add path mappings)

### Phase 2: Core Pages
- [ ] Create `HomePage.ts` page object
- [ ] Migrate `homepage.cy.ts` to use `HomePage`
- [ ] Test migrated tests pass

### Phase 3: Additional Pages (As Needed)
- [ ] Create `ApplicationsPage.ts`
- [ ] Create `CompaniesPage.ts`
- [ ] Create `ContactsPage.ts`
- [ ] Create `ClientsPage.ts`
- [ ] Create `NotesPage.ts`
- [ ] Create `JobSearchSitesPage.ts`

### Phase 4: Advanced Features
- [ ] Add custom commands to `commands.ts` (if needed)
- [ ] Create component objects (for reusable UI components)
- [ ] Add helper utilities

### Phase 5: Documentation & Cleanup
- [ ] Update test documentation
- [ ] Add JSDoc comments to page objects
- [ ] Review and refactor for consistency

---

## 🔍 Troubleshooting

### Common Issues

**Issue**: TypeScript cannot find module
```typescript
// Error: Cannot find module '../page-objects/HomePage'
```

**Solution**: Ensure TypeScript paths are configured correctly in `tsconfig.json`:
```json
{
  "compilerOptions": {
    "baseUrl": ".",
    "paths": {
      "@page-objects/*": ["cypress/page-objects/*"]
    }
  }
}
```

**Issue**: Cypress commands not recognized in Page Object
```typescript
// Error: Property 'get' does not exist on type 'HomePage'
```

**Solution**: Ensure `/// <reference types="cypress" />` is at the top of the file.

---

## ✅ Success Criteria

Migration is complete when:

1. ✅ All test files use Page Objects instead of direct selectors
2. ✅ Selectors are centralized in Page Object classes
3. ✅ Tests are more readable and focused on behavior
4. ✅ Page Objects follow consistent naming conventions
5. ✅ All tests pass after migration
6. ✅ Code is maintainable and scalable

---

**Last Updated**: 2026-01-09  
**Document Location**: `docs/guides/testing/CYPRESS_POM_MIGRATION_GUIDE.md`  
**Status**: 📋 Ready for Implementation
