# Playwright Page Object Model (POM) Migration Guide

**Date Created**: 2026-01-09  
**Status**: ✅ **COMPLETE** - HomePage POM Migration Finished  
**Purpose**: Guide for converting Playwright tests to use Page Object Model pattern consistently  
**Current State**: `homepage.spec.ts` uses POM; integration `HomePage.ts` updated with data-qa selectors  
**Target State**: ~~All tests use Page Object Model pattern consistently~~ **Phase 1-2 ACHIEVED**

---

## 📋 Executive Summary

This document outlines the strategy and step-by-step process for migrating Playwright tests to consistently use the **Page Object Model (POM)** pattern. While some Page Objects already exist, this guide ensures all tests follow the same pattern and best practices.

### Current State (Post-Migration)

- ✅ **Shared Page Objects Created**: `playwright/tests/pages/` contains `BasePage.ts` and `HomePage.ts`
- ✅ **homepage.spec.ts Migrated**: Now uses `HomePage` page object from `tests/pages/`
- ✅ **Integration HomePage Updated**: `playwright/tests/integration/pages/HomePage.ts` now uses data-qa selectors (`home-title`, `sidebar-nav-*`)
- ✅ **Data-QA Consistency**: All HomePage selectors use `data-qa` attributes from frontend (`app/page.tsx`, `Sidebar.tsx`)

### Benefits of Consistent Page Object Model

- ✅ **Maintainability**: Selectors defined in one place
- ✅ **Reusability**: Page objects shared across multiple tests
- ✅ **Readability**: Tests focus on behavior, not implementation
- ✅ **Consistency**: All tests follow the same pattern
- ✅ **Scalability**: Easier to add new tests as application grows

---

## 🎯 Current State Analysis

### Existing Page Objects (Good Examples)

**Location**: `playwright/tests/integration/pages/HomePage.ts`

```typescript
import { Page, Locator } from '@playwright/test';

export class HomePage {
  readonly page: Page;
  readonly title: Locator;
  readonly applicationsCard: Locator;
  readonly companiesCard: Locator;

  constructor(page: Page) {
    this.page = page;
    this.title = page.locator('h1.display-5, h1.display-4, h1');
    this.applicationsCard = page.locator('a[href="/applications"]');
    this.companiesCard = page.locator('a[href="/companies"]');
  }

  async navigate() {
    await this.page.goto('/');
  }

  async clickApplications() {
    await this.applicationsCard.click();
  }
}
```

### Tests Using Direct Selectors (Needs Migration)

**Location**: `playwright/tests/homepage.spec.ts`

```typescript
import { test, expect } from '@playwright/test';

test.describe('HomePage', () => {
  test.beforeEach(async ({ page }) => {
    await page.setViewportSize({ width: 1920, height: 1080 });
    await page.goto('/');
    await page.waitForLoadState('networkidle');
  });

  test('should load the home page', async ({ page }) => {
    await expect(page).toHaveTitle(/Job Search Application/i);
    await expect(page.locator('body')).toBeVisible();
  });

  test('should display the navigation panel', async ({ page }) => {
    await expect(page.locator('[data-qa="sidebar"]')).toBeVisible({ timeout: 10000 });
    const navigationTitle = page.locator('[data-qa="sidebar-title"]').filter({ hasText: 'Navigation' });
    await expect(navigationTitle).toBeVisible({ timeout: 10000 });
  });
});
```

---

## 🔄 Target Architecture

### Directory Structure

```
playwright/
├── tests/
│   ├── integration/
│   │   ├── pages/                    # Existing Page Objects (keep)
│   │   │   ├── HomePage.ts
│   │   │   ├── ApplicationsPage.ts
│   │   │   ├── ApplicationFormPage.ts
│   │   │   └── ApplicationDetailPage.ts
│   │   ├── applications.spec.ts      # Uses POM ✅
│   │   └── companies.spec.ts         # Uses POM ✅
│   ├── pages/                        # NEW: Shared Page Objects
│   │   ├── BasePage.ts              # Base class with common methods
│   │   ├── HomePage.ts              # Enhanced HomePage
│   │   └── NavigationPage.ts       # Shared navigation component
│   ├── homepage.spec.ts             # Migrate to use POM
│   └── utils/
│       └── test-data-loader.ts
├── playwright.config.ts
└── package.json
```

---

## 📝 Step-by-Step Migration Process

### Step 1: Create Base Page Class

**File**: `playwright/tests/pages/BasePage.ts`

**Purpose**: Common functionality shared across all pages.

```typescript
import { Page, Locator } from '@playwright/test';

/**
 * Base Page Object class
 * Contains common methods and properties shared across all pages
 */
export class BasePage {
  readonly page: Page;

  constructor(page: Page) {
    this.page = page;
  }

  /**
   * Navigate to a URL
   * @param path - Relative path from base URL (e.g., '/', '/applications')
   */
  async navigate(path: string = '/'): Promise<void> {
    await this.page.goto(path);
    await this.waitForPageLoad();
  }

  /**
   * Wait for page to load
   * Waits for network to be idle and body to be visible
   */
  async waitForPageLoad(): Promise<void> {
    await this.page.waitForLoadState('networkidle');
    await this.page.locator('body').waitFor({ state: 'visible' });
  }

  /**
   * Get page title
   */
  async getTitle(): Promise<string> {
    return await this.page.title();
  }

  /**
   * Verify page title contains text
   * @param text - Text to verify in title
   */
  async verifyTitleContains(text: string | RegExp): Promise<void> {
    await this.page.waitForLoadState('networkidle');
    if (typeof text === 'string') {
      await expect(this.page).toHaveTitle(new RegExp(text, 'i'));
    } else {
      await expect(this.page).toHaveTitle(text);
    }
  }

  /**
   * Set viewport size
   * @param width - Viewport width (default: 1920)
   * @param height - Viewport height (default: 1080)
   */
  async setViewport(width: number = 1920, height: number = 1080): Promise<void> {
    await this.page.setViewportSize({ width, height });
  }

  /**
   * Wait for element to be visible
   * @param locator - Element locator
   * @param timeout - Timeout in milliseconds (default: 10000)
   */
  async waitForVisible(locator: Locator, timeout: number = 10000): Promise<void> {
    await locator.waitFor({ state: 'visible', timeout });
  }

  /**
   * Take screenshot
   * @param name - Screenshot name
   */
  async takeScreenshot(name: string): Promise<void> {
    await this.page.screenshot({ path: `screenshots/${name}.png`, fullPage: true });
  }
}
```

---

### Step 2: Enhance HomePage Page Object

**File**: `playwright/tests/pages/HomePage.ts` (New enhanced version)

**Purpose**: Complete HomePage with all selectors and methods.

```typescript
import { Page, Locator, expect } from '@playwright/test';
import { BasePage } from './BasePage';

/**
 * Page Object Model for the Home Page
 * Enhanced version with all selectors and methods
 */
export class HomePage extends BasePage {
  // Selectors
  readonly title: Locator;
  readonly sidebar: Locator;
  readonly sidebarTitle: Locator;
  readonly sidebarNavigation: Locator;
  readonly sidebarNavHome: Locator;
  readonly applicationsCard: Locator;
  readonly companiesCard: Locator;
  readonly contactsCard: Locator;
  readonly clientsCard: Locator;
  readonly notesCard: Locator;
  readonly jobSearchSitesCard: Locator;

  constructor(page: Page) {
    super(page);
    // Title selector - handles multiple possible formats
    this.title = page.locator('h1.display-5, h1.display-4, h1');
    // Sidebar selectors
    this.sidebar = page.locator('[data-qa="sidebar"]');
    this.sidebarTitle = page.locator('[data-qa="sidebar-title"]');
    this.sidebarNavigation = page.locator('[data-qa="sidebar-navigation"]');
    this.sidebarNavHome = page.locator('[data-qa="sidebar-nav-home"]');
    // Navigation cards
    this.applicationsCard = page.locator('a[href="/applications"]');
    this.companiesCard = page.locator('a[href="/companies"]');
    this.contactsCard = page.locator('a[href="/contacts"]');
    this.clientsCard = page.locator('a[href="/clients"]');
    this.notesCard = page.locator('a[href="/notes"]');
    this.jobSearchSitesCard = page.locator('a[href="/job-search-sites"]');
  }

  /**
   * Navigate to home page
   */
  async navigate(): Promise<void> {
    await super.navigate('/');
  }

  /**
   * Verify page has loaded
   */
  async verifyPageLoaded(): Promise<void> {
    await this.waitForPageLoad();
    await this.verifyTitleContains('Job Search Application');
    await expect(this.page.locator('body')).toBeVisible();
  }

  /**
   * Verify sidebar is visible
   */
  async verifySidebarVisible(): Promise<void> {
    await this.waitForVisible(this.sidebar, 10000);
    await expect(this.sidebar).toBeVisible();
  }

  /**
   * Verify navigation title contains text
   * @param text - Text to verify (default: 'Navigation')
   */
  async verifyNavigationTitle(text: string = 'Navigation'): Promise<void> {
    const navigationTitle = this.sidebarTitle.filter({ hasText: text });
    await this.waitForVisible(navigationTitle, 10000);
    await expect(navigationTitle).toBeVisible();
  }

  /**
   * Verify navigation elements are present
   */
  async verifyNavigationElements(): Promise<void> {
    await this.waitForVisible(this.sidebarNavigation, 10000);
    await this.waitForVisible(this.sidebarNavHome, 10000);
    await expect(this.sidebarNavigation).toBeVisible();
    await expect(this.sidebarNavHome).toBeVisible();
  }

  /**
   * Click Applications card
   */
  async clickApplications(): Promise<void> {
    await this.applicationsCard.click();
  }

  /**
   * Click Companies card
   */
  async clickCompanies(): Promise<void> {
    await this.companiesCard.click();
  }

  /**
   * Click Contacts card
   */
  async clickContacts(): Promise<void> {
    await this.contactsCard.click();
  }

  /**
   * Click Clients card
   */
  async clickClients(): Promise<void> {
    await this.clientsCard.click();
  }

  /**
   * Click Notes card
   */
  async clickNotes(): Promise<void> {
    await this.notesCard.click();
  }

  /**
   * Click Job Search Sites card
   */
  async clickJobSearchSites(): Promise<void> {
    await this.jobSearchSitesCard.click();
  }

  /**
   * Verify all navigation cards are visible
   */
  async verifyAllCardsVisible(): Promise<void> {
    await expect(this.applicationsCard).toBeVisible();
    await expect(this.companiesCard).toBeVisible();
    await expect(this.contactsCard).toBeVisible();
    await expect(this.clientsCard).toBeVisible();
    await expect(this.notesCard).toBeVisible();
    await expect(this.jobSearchSitesCard).toBeVisible();
  }
}
```

---

### Step 3: Migrate Test File to Use Page Object

**File**: `playwright/tests/homepage.spec.ts` (Updated)

**Before** (Direct selectors):
```typescript
import { test, expect } from '@playwright/test';

test.describe('HomePage', () => {
  test.beforeEach(async ({ page }) => {
    await page.setViewportSize({ width: 1920, height: 1080 });
    await page.goto('/');
    await page.waitForLoadState('networkidle');
  });

  test('should load the home page', async ({ page }) => {
    await expect(page).toHaveTitle(/Job Search Application/i);
    await expect(page.locator('body')).toBeVisible();
  });

  test('should display the navigation panel', async ({ page }) => {
    await expect(page.locator('[data-qa="sidebar"]')).toBeVisible({ timeout: 10000 });
    const navigationTitle = page.locator('[data-qa="sidebar-title"]').filter({ hasText: 'Navigation' });
    await expect(navigationTitle).toBeVisible({ timeout: 10000 });
    await expect(page.locator('[data-qa="sidebar-navigation"]')).toBeVisible({ timeout: 10000 });
    await expect(page.locator('[data-qa="sidebar-nav-home"]')).toBeVisible({ timeout: 10000 });
  });
});
```

**After** (Using Page Object):
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

---

## 🔧 Best Practices

### 1. Selector Strategy

**Use data-qa attributes** (already in use):
```typescript
readonly sidebar = page.locator('[data-qa="sidebar"]');
```

**Fallback selectors** (when data-qa not available):
```typescript
readonly title = page.locator('h1.display-5, h1.display-4, h1');
```

**Role-based selectors** (Playwright recommended):
```typescript
readonly button = page.getByRole('button', { name: 'Submit' });
readonly link = page.getByRole('link', { name: 'Applications' });
```

### 2. Method Naming Conventions

- **Actions**: Use verbs (`click`, `navigate`, `fill`, `select`)
  ```typescript
  async clickApplications(): Promise<void>
  async fillForm(data: FormData): Promise<void>
  async selectOption(value: string): Promise<void>
  ```

- **Verifications**: Use `verify` prefix
  ```typescript
  async verifyPageLoaded(): Promise<void>
  async verifySidebarVisible(): Promise<void>
  async verifyTitleContains(text: string): Promise<void>
  ```

- **Getters**: Use `get` prefix
  ```typescript
  async getTitle(): Promise<string>
  getApplicationRow(position: string): Locator
  ```

### 3. Page Object Structure

```typescript
export class PageName extends BasePage {
  // 1. Readonly selectors (at the top)
  readonly selector1: Locator;
  readonly selector2: Locator;

  // 2. Constructor
  constructor(page: Page) {
    super(page);
    this.selector1 = page.locator('...');
  }

  // 3. Navigation methods
  async navigate(): Promise<void> { }

  // 4. Action methods (click, fill, etc.)
  async clickButton(): Promise<void> { }

  // 5. Verification methods
  async verifySomething(): Promise<void> { }

  // 6. Helper/utility methods (private)
  private async helperMethod(): Promise<void> { }
}
```

### 4. Waiting Strategies

**Explicit waits** (preferred):
```typescript
await locator.waitFor({ state: 'visible', timeout: 10000 });
await expect(locator).toBeVisible();
```

**Avoid hard-coded waits**:
```typescript
// ❌ Bad
await page.waitForTimeout(1000);

// ✅ Good
await locator.waitFor({ state: 'visible' });
```

**Use Playwright's auto-waiting**:
```typescript
// Playwright automatically waits for element to be actionable
await locator.click(); // Already waits for visible, enabled, stable
```

### 5. Error Handling

**Add timeout messages**:
```typescript
async verifySidebarVisible(): Promise<void> {
  await this.sidebar.waitFor({ 
    state: 'visible', 
    timeout: 10000 
  });
  await expect(this.sidebar).toBeVisible();
}
```

**Handle dynamic content**:
```typescript
async waitForContent(text: string): Promise<void> {
  await this.page.waitForFunction(
    (text) => document.body.innerText.includes(text),
    text
  );
}
```

---

## 📊 Migration Checklist

### Phase 1: Foundation ✅ COMPLETE
- [x] Create `playwright/tests/pages/` directory
- [x] Create `BasePage.ts` with common functionality
- [x] Review existing Page Objects in `tests/integration/pages/`

### Phase 2: Core Migration ✅ COMPLETE
- [x] Create enhanced `HomePage.ts` in `tests/pages/` (extends BasePage, uses data-qa selectors)
- [x] Migrate `homepage.spec.ts` to use Page Object
- [x] Test migrated tests pass (verified locally with `npm run test:chrome`)
- [x] Update `playwright/tests/integration/pages/HomePage.ts` to use data-qa selectors for title (`home-title`) and navigation cards (`sidebar-nav-*`)

### Phase 3: Consistency
- [ ] Ensure all Page Objects follow same pattern
- [ ] Add missing methods to existing Page Objects
- [ ] Standardize naming conventions
- [x] Add JSDoc comments (BasePage, HomePage)

### Phase 4: Advanced Features
- [ ] Create component objects (if needed)
- [ ] Add helper utilities
- [ ] Create page factory pattern (if needed)

### Phase 5: Documentation & Cleanup
- [x] Update test documentation (this document)
- [ ] Add examples to README
- [ ] Review and refactor for consistency
- [ ] Remove duplicate code

### Migration Completion Summary

**Completed**: 2026-01-10

The Playwright POM migration for HomePage is **complete**. The following was implemented:

1. **Shared Page Objects Created**:
   - `playwright/tests/pages/BasePage.ts` - Base class with common methods (`navigate`, `waitForPageLoad`, `getTitle`, `verifyTitleContains`, `setViewport`, `waitForVisible`)
   - `playwright/tests/pages/HomePage.ts` - HomePage extending BasePage with data-qa selectors (`home-title`, `sidebar`, `sidebar-title`, `sidebar-navigation`, `sidebar-nav-home`, `sidebar-nav-applications`, etc.)

2. **Tests Migrated**:
   - `playwright/tests/homepage.spec.ts` - Now uses `HomePage` page object from `./pages/HomePage` instead of direct selectors

3. **Integration HomePage Enhanced**:
   - `playwright/tests/integration/pages/HomePage.ts` - Updated selectors to use data-qa attributes:
     - `title`: `[data-qa="home-title"]` (from `frontend/app/page.tsx`)
     - Navigation cards: `[data-qa="sidebar-nav-applications"]`, `[data-qa="sidebar-nav-companies"]`, etc. (from `frontend/components/Sidebar.tsx`)

4. **Verification**: `homepage.spec.ts` tests pass when run with app services running (`npm run test:chrome` or `npx playwright test --project=chromium`)

---

## 🔍 Troubleshooting

### Common Issues

**Issue**: TypeScript cannot find module
```typescript
// Error: Cannot find module './pages/HomePage'
```

**Solution**: Ensure TypeScript paths are configured correctly in `tsconfig.json`:
```json
{
  "compilerOptions": {
    "baseUrl": ".",
    "paths": {
      "@pages/*": ["tests/pages/*"],
      "@components/*": ["tests/pages/components/*"]
    }
  }
}
```

**Issue**: Page Object methods not awaiting
```typescript
// Error: A promise was returned but not awaited
homePage.navigate(); // Missing await
```

**Solution**: Always use `await` with async methods:
```typescript
await homePage.navigate();
```

**Issue**: Locator not found
```typescript
// Error: locator.click: Target closed
```

**Solution**: Ensure page is loaded and element is visible:
```typescript
async clickButton(): Promise<void> {
  await this.button.waitFor({ state: 'visible' });
  await this.button.click();
}
```

---

## 📚 Additional Resources

- [Playwright Best Practices](https://playwright.dev/docs/best-practices)
- [Page Object Model Pattern](https://martinfowler.com/bliki/PageObject.html)
- [Playwright TypeScript Support](https://playwright.dev/docs/test-typescript)
- [Playwright Locators](https://playwright.dev/docs/locators)

---

## ✅ Success Criteria

Migration is complete when:

1. ✅ All test files use Page Objects instead of direct selectors
2. ✅ Selectors are centralized in Page Object classes
3. ✅ Tests are more readable and focused on behavior
4. ✅ Page Objects follow consistent naming conventions
5. ✅ All tests pass after migration
6. ✅ Code is maintainable and scalable
7. ✅ Existing Page Objects are enhanced and consistent

---

## 🔄 Integration with Existing Page Objects

### Current Integration Page Objects

The existing Page Objects in `tests/integration/pages/` are well-structured. To maintain consistency:

1. **Keep integration Page Objects** for integration-specific functionality
2. **Use shared Page Objects** from `tests/pages/` for common functionality
3. **Extend BasePage** in all Page Objects for consistency
4. **Share components** across integration and unit tests

### Example: Using Both

```typescript
import { HomePage } from '../pages/HomePage'; // Shared Page Object
import { ApplicationsPage } from './pages/ApplicationsPage'; // Integration-specific

test('integration test', async ({ page }) => {
  const homePage = new HomePage(page);
  const applicationsPage = new ApplicationsPage(page);
  // Use both Page Objects
});
```

---

**Last Updated**: 2026-01-10  
**Document Location**: `docs/work/20260109_PLAYWRIGHT_POM_MIGRATION_GUIDE.md`  
**Status**: ✅ **COMPLETE** - HomePage POM migration finished, homepage.spec.ts tests passing
