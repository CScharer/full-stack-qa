# Cypress Tests (TypeScript)

This directory contains Cypress end-to-end tests written in **TypeScript** for the full-stack-qa framework. The tests use the **Page Object Model (POM)** pattern for maintainability and reusability.

## Prerequisites

- Node.js >= 18.0.0
- npm >= 9.0.0
- **Backend and Frontend services must be running** (see [Service Setup](#service-setup) below)

## Installation

```bash
cd cypress
npm install
```

## Service Setup

⚠️ **IMPORTANT**: Services must be running before executing tests.

### Option 1: Start Both Services Together (Recommended)
```bash
# From project root
./scripts/start-env.sh                    # Default: dev environment
./scripts/start-env.sh --env test        # Test environment
```

### Option 2: Start Services Separately (2 Terminals)
```bash
# Terminal 1 - Backend (from project root)
./scripts/start-be.sh                    # Default: dev environment

# Terminal 2 - Frontend (from project root)
./scripts/start-fe.sh                    # Default: dev environment
```

**Services will be available at:**
- Frontend: http://localhost:3003 (dev), http://localhost:3004 (test)
- Backend API: http://localhost:8003 (dev), http://localhost:8004 (test)

## Running Tests

### Interactive Mode (Cypress Test Runner)
```bash
npm run cypress:open
```
Then select the test file from the list (e.g., `wizard.cy.ts`)

### Headless Mode
```bash
npm run cypress:run
```

### Run Specific Test File
```bash
# Run wizard test
npx cypress run --browser chrome --spec cypress/e2e/wizard.cy.ts

# Or using npm script
npm run cypress:run:chrome -- --spec cypress/e2e/wizard.cy.ts
```

### Run in Specific Browser
```bash
npm run cypress:run:chrome
npm run cypress:run:firefox
npm run cypress:run:edge
```

### Run in Headed Mode (See Browser)
```bash
npx cypress run --browser chrome --headed --spec cypress/e2e/wizard.cy.ts
```

### Using Helper Script
```bash
# From project root
./scripts/run-cypress-tests.sh run chrome
./scripts/run-cypress-tests.sh open
```

## Test Structure

- `cypress/e2e/` - End-to-end test files (`.cy.ts`)
  - `wizard.cy.ts` - Comprehensive wizard test suite (8 test cases)
  - `homepage.cy.ts` - Homepage tests
- `cypress/page-objects/` - Page Object Model classes
  - `BasePage.ts` - Base class with common methods
  - `HomePage.ts` - Home page object
  - `ApplicationsPage.ts`, `CompaniesPage.ts`, `ContactsPage.ts`, `ClientsPage.ts`, `NotesPage.ts`, `JobSearchSitesPage.ts` - List page objects
  - `ApplicationFormPage.ts`, `CompanyFormPage.ts`, `ContactFormPage.ts`, `ClientFormPage.ts` - Form page objects
  - `ApplicationDetailPage.ts` - Detail page object
  - `WizardStep1Page.ts` - Wizard step 1 page object
- `cypress/support/` - Custom commands and configuration (`.ts`)
- `cypress/fixtures/` - Test data files
- `tsconfig.json` - TypeScript configuration

## Page Object Model (POM)

All page objects follow the Page Object Model pattern, matching the Playwright implementation for consistency.

### Available Page Objects

**Base & Home:**
- `BasePage.ts` - Base class with common navigation and utility methods
- `HomePage.ts` - Home page with sidebar navigation

**List Pages:**
- `ApplicationsPage.ts` - Applications list with filters and table
- `CompaniesPage.ts` - Companies list with filters and table
- `ContactsPage.ts` - Contacts list with filters and table
- `ClientsPage.ts` - Clients list with filters and table
- `NotesPage.ts` - Notes list
- `JobSearchSitesPage.ts` - Job search sites list

**Form Pages:**
- `ApplicationFormPage.ts` - Application create/edit form (supports wizard step2 and edit)
- `CompanyFormPage.ts` - Company create/edit form
- `ContactFormPage.ts` - Contact create/edit form
- `ClientFormPage.ts` - Client create/edit form

**Detail Pages:**
- `ApplicationDetailPage.ts` - Application detail view with note management

**Wizard Pages:**
- `WizardStep1Page.ts` - Application wizard step 1 (contact selection)

### Using Page Objects

```typescript
import { HomePage } from '../page-objects/HomePage';
import { ApplicationsPage } from '../page-objects/ApplicationsPage';

describe('My Test', () => {
  let homePage: HomePage;
  let applicationsPage: ApplicationsPage;

  beforeEach(() => {
    homePage = new HomePage();
    applicationsPage = new ApplicationsPage();
  });

  it('should navigate to applications', () => {
    homePage.navigate();
    homePage.verifyPageLoaded();
    homePage.clickApplications();
    applicationsPage.verifyPageLoaded();
  });
});
```

## Cypress-Specific Patterns

### Chainable Commands
Cypress uses chainable commands instead of async/await:
```typescript
// Cypress (chainable)
cy.get('[data-qa="button"]').click();
cy.get('[data-qa="input"]').type('text');

// vs Playwright (async/await)
await page.locator('[data-qa="button"]').click();
await page.locator('[data-qa="input"]').fill('text');
```

### API Calls
Use `cy.request()` for API verification:
```typescript
cy.request({
  method: 'GET',
  url: `${backendBaseUrl}/api/v1/applications?limit=1`,
  failOnStatusCode: false,
}).then((response) => {
  if (response.status === 200 && response.body) {
    const count = response.body.total || 0;
    expect(count).to.equal(expectedCount);
  }
});
```

### Environment Configuration
Backend URL is determined from environment:
```typescript
// Uses Cypress.env('BACKEND_URL') or defaults to dev
const backendBaseUrl = getBackendUrl(Cypress.env('ENVIRONMENT') || 'dev');
```

### Serial Test Execution
Cypress runs tests serially by default (unlike Playwright which requires explicit configuration):
```typescript
describe('Wizard Tests', () => {
  // Tests run serially automatically in Cypress
  it('test 1', () => { /* ... */ });
  it('test 2', () => { /* ... */ });
});
```

## TypeScript

All tests are written in TypeScript for type safety and better IDE support.

### Type Checking
```bash
npm run build  # Type check without running tests
```

## Configuration

Edit `cypress.config.ts` to modify:
- Base URL (default: http://localhost:3003)
- Viewport size (default: 1920x1080)
- Timeouts
- Screenshot/video settings

## Test Files

### wizard.cy.ts
Comprehensive test suite covering:
- Navigation and cancel functionality for all entity creation flows
- Form filling and cancellation verification
- Empty state verification (notes)
- Job search sites validation
- API verification that no data was created

**Run wizard test:**
```bash
npx cypress run --browser chrome --spec cypress/e2e/wizard.cy.ts
```

## Alignment with Playwright

The Cypress implementation is designed to match the Playwright implementation:
- ✅ Same page objects (14 total)
- ✅ Same test structure and coverage
- ✅ Same `data-qa` selectors
- ✅ Same test scenarios (wizard.cy.ts matches wizard.spec.ts)

**Key Differences:**
- Cypress uses chainable commands vs Playwright's async/await
- Cypress runs tests serially by default
- Cypress uses `cy.request()` vs Playwright's `request` context
