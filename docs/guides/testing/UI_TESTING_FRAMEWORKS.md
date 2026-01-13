# GUI Testing Frameworks Guide

This guide covers all GUI testing frameworks available in this project: **Selenium**, **Cypress**, **Playwright**, **Vibium**, and **Robot Framework**.

---

## 📋 Overview

This framework supports **5 different UI testing tools**, each with unique strengths:

| Framework | Language | Best For | Speed | Learning Curve |
|-----------|----------|----------|-------|----------------|
| **Selenium** | Java | Legacy support, Grid | Medium | Medium |
| **Playwright** | TypeScript | Modern apps, reliability | Fast | Medium |
| **Cypress** | TypeScript | Frontend-heavy apps | Fast | Easy |
| **Vibium** | TypeScript | AI-native automation | Fast | Easy |
| **Robot Framework** | Python | Non-technical testers | Medium | Easy |

---

## 🎯 Quick Start

### Selenium (Java)
```bash
# Run Selenium tests
./scripts/run-tests.sh Scenarios chrome

# Or with Maven
./mvnw test -DsuiteXmlFile=testng-ci-suite.xml
```

### Playwright (TypeScript)
```bash
# Run Playwright tests
./scripts/run-playwright-tests.sh chromium

# Or directly
cd playwright && npm test
```

### Cypress (TypeScript)
```bash
# Run Cypress tests
./scripts/run-cypress-tests.sh run chrome

# Interactive mode
./scripts/run-cypress-tests.sh open
```

### Vibium (TypeScript)
```bash
# Run Vibium tests
./scripts/run-vibium-tests.sh

# With options
./scripts/run-vibium-tests.sh --watch    # Watch mode
./scripts/run-vibium-tests.sh --ui       # UI mode
./scripts/run-vibium-tests.sh --coverage # Coverage

# Or directly
cd vibium && npm test
```

### Robot Framework (Python)
```bash
# Run Robot Framework tests
./scripts/run-robot-tests.sh

# Or with Maven
./mvnw test -Probot
```

---

## 🔧 Selenium

### Overview
Selenium is the industry-standard web automation framework with extensive browser and language support.

### Setup
Already configured in `pom.xml`. No additional setup needed!

### Running Tests

```bash
# Default test suite
./scripts/run-tests.sh Scenarios chrome

# Specific test class
./mvnw test -Dtest=Scenarios#Google

# With specific browser
./mvnw test -Dtest=Scenarios#Microsoft -Dbrowser=firefox
```

### Test Structure

```
src/test/java/com/cjs/qa/
├── google/
│   ├── Google.java              # Test class
│   └── pages/                   # Page Objects
├── microsoft/
│   ├── Microsoft.java
│   └── pages/
└── selenium/                    # Selenium wrappers
    ├── SeleniumWebDriver.java
    └── Page.java
```

### Features

- ✅ **Selenium Grid** - Distributed testing
- ✅ **Multi-browser** - Chrome, Firefox, Edge
- ✅ **Page Object Model** - Clean architecture
- ✅ **TestNG Integration** - Advanced test management
- ✅ **Allure Reports** - Beautiful test reports
- ✅ **Parallel Execution** - 5 threads by default

### Example Test

```java
@Test
public void testGoogleSearch() {
    GoogleSearchPage googlePage = new GoogleSearchPage(driver);
    googlePage.navigate();
    googlePage.search("Selenium WebDriver");
    Assert.assertTrue(googlePage.areSearchResultsVisible());
}
```

### Best Use Cases

- Legacy applications
- Selenium Grid requirements
- Cross-browser matrix testing
- Large existing test suites
- Enterprise environments

---

## 🎭 Playwright (TypeScript)

### Overview
Playwright is a modern, fast, and reliable end-to-end testing framework with excellent browser automation capabilities.

### Setup

```bash
cd playwright
npm install
npx playwright install
```

### Running Tests

```bash
# All browsers
cd playwright && npm test

# Specific browser
npm run test:chrome
npm run test:firefox
npm run test:webkit

# UI mode (interactive)
npm run test:ui

# Debug mode
npm run test:debug
```

### Test Structure

```
playwright/
├── tests/
│   ├── homepage.spec.ts         # Test files
│   ├── pages/                   # Shared Page Objects
│   │   ├── BasePage.ts         # Base class with common methods
│   │   └── HomePage.ts         # HomePage Page Object
│   └── integration/
│       ├── pages/              # Integration-specific Page Objects
│       │   └── HomePage.ts     # Integration HomePage (uses data-qa)
│       └── applications.spec.ts
├── playwright.config.ts          # Configuration
└── tsconfig.json                 # TypeScript config
```

**Page Object Model**: All Playwright tests use the Page Object Model pattern. Shared page objects are in `tests/pages/`, and integration-specific page objects are in `tests/integration/pages/`. All selectors use `data-qa` attributes for consistency.

### Features

- ✅ **TypeScript** - Type safety and better IDE support
- ✅ **Auto-waiting** - No manual waits needed
- ✅ **Network Interception** - Mock API calls
- ✅ **Multi-browser** - Chromium, Firefox, WebKit
- ✅ **Screenshot/Video** - Automatic capture
- ✅ **HTML Reports** - Beautiful test reports
- ✅ **Parallel Execution** - Built-in support

### Example Test (Using Page Object Model)

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

**Page Objects**: Located in `tests/pages/` with `BasePage.ts` providing common functionality. All selectors use `data-qa` attributes from the frontend for cross-framework consistency.

### Best Use Cases

- Modern web applications
- API mocking needs
- Fast execution requirements
- Multi-browser testing
- TypeScript projects

---

## 🎬 Cypress (TypeScript)

### Overview
Cypress is a modern JavaScript/TypeScript testing framework that runs directly in the browser, providing excellent debugging capabilities.

### Setup

```bash
cd cypress
npm install
```

### Running Tests

⚠️ **Prerequisites**: Services must be running before executing tests.
```bash
# Start services (from project root)
./scripts/start-env.sh                    # Default: dev environment
```

```bash
# Interactive mode (Test Runner)
./scripts/run-cypress-tests.sh open
# Or: cd cypress && npm run cypress:open

# Headless mode
./scripts/run-cypress-tests.sh run chrome
# Or: cd cypress && npm run cypress:run

# Run specific test file
cd cypress
npx cypress run --browser chrome --spec cypress/e2e/wizard.cy.ts

# Specific browser
./scripts/run-cypress-tests.sh run firefox
./scripts/run-cypress-tests.sh run edge
```

### Test Structure

```
cypress/
├── cypress/
│   ├── e2e/
│   │   ├── homepage.cy.ts        # Homepage tests
│   │   └── wizard.cy.ts          # Comprehensive wizard test suite (8 test cases)
│   ├── page-objects/             # Page Object Model classes (14 total)
│   │   ├── BasePage.ts          # Base class with common methods
│   │   ├── HomePage.ts          # Home page object
│   │   ├── ApplicationsPage.ts  # Applications list page
│   │   ├── ApplicationFormPage.ts # Application form page
│   │   ├── ApplicationDetailPage.ts # Application detail page
│   │   ├── CompaniesPage.ts     # Companies list page
│   │   ├── CompanyFormPage.ts    # Company form page
│   │   ├── ContactsPage.ts      # Contacts list page
│   │   ├── ContactFormPage.ts   # Contact form page
│   │   ├── ClientsPage.ts       # Clients list page
│   │   ├── ClientFormPage.ts    # Client form page
│   │   ├── NotesPage.ts         # Notes list page
│   │   ├── JobSearchSitesPage.ts # Job search sites list page
│   │   └── WizardStep1Page.ts  # Wizard step 1 page
│   └── support/
│       ├── commands.ts            # Custom commands
│       └── e2e.ts                 # Support file
├── cypress.config.ts              # Configuration
└── tsconfig.json                  # TypeScript config
```

**Page Object Model**: All Cypress tests use the Page Object Model pattern with `data-qa` selectors for consistency. **14 page objects** are available, matching the Playwright implementation.

### Features

- ✅ **TypeScript** - Type safety and better IDE support
- ✅ **Time-travel Debugging** - See every step of test execution
- ✅ **Real-time Reloads** - See changes instantly
- ✅ **Automatic Waiting** - No manual waits needed
- ✅ **Network Stubbing** - Mock API responses
- ✅ **Screenshot/Video** - Automatic capture
- ✅ **Cross-browser** - Chrome, Firefox, Edge

### Example Test (Using Page Object Model)

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
})
```

**Page Objects**: Located in `cypress/cypress/page-objects/` with `BasePage.ts` providing common functionality. All selectors use `data-qa` attributes from the frontend.

**Available Page Objects (14 total):**
- **Base & Home**: `BasePage.ts`, `HomePage.ts`
- **List Pages**: `ApplicationsPage.ts`, `CompaniesPage.ts`, `ContactsPage.ts`, `ClientsPage.ts`, `NotesPage.ts`, `JobSearchSitesPage.ts`
- **Form Pages**: `ApplicationFormPage.ts`, `CompanyFormPage.ts`, `ContactFormPage.ts`, `ClientFormPage.ts`
- **Detail Pages**: `ApplicationDetailPage.ts`
- **Wizard Pages**: `WizardStep1Page.ts`

**Test Files:**
- `wizard.cy.ts` - Comprehensive test suite with 8 test cases covering navigation, forms, and API verification
- `homepage.cy.ts` - Homepage tests

**Service Prerequisites**: Backend and frontend services must be running. Use `./scripts/start-env.sh` to start both services.

**Environment Configuration**: 
- Backend URL is determined from environment using `getBackendUrl()` helper
- ✅ Uses shared `config/port_config.py` via `ConfigHelper.py`
- See `config/environments.json` for environment port mappings
- See [Shared Test Configuration](../work/20260111_SHARED_TEST_CONFIGURATION.md) for details

### Best Use Cases

- Frontend-heavy applications
- JavaScript/TypeScript teams
- Time-travel debugging needs
- Component testing
- Real-time development workflow

---

## ⚡ Vibium (TypeScript)

### Overview
Vibium is an AI-native browser automation framework developed by Jason Huggins (creator of Selenium and Appium). It provides both async and sync APIs for browser automation.

### Setup

```bash
cd vibium
npm install
```

This installs:
- `vibium@0.1.2` (released version)
- `@vibium/darwin-arm64@0.1.2` (clicker binary for macOS ARM64)
- TypeScript and type definitions

### Running Tests

```bash
# Using the test script (recommended)
./scripts/run-vibium-tests.sh

# With options
./scripts/run-vibium-tests.sh --watch    # Watch mode
./scripts/run-vibium-tests.sh --ui       # UI mode
./scripts/run-vibium-tests.sh --coverage # Coverage report

# Or directly
cd vibium && npm test
```

### Test Structure

```
vibium/
├── tests/
│   └── example.spec.ts      # Test files (TypeScript)
├── helpers/
│   └── example.ts            # Helper functions
├── types/
│   └── vibium.d.ts          # Type definitions
├── vitest.config.ts          # Vitest configuration
└── package.json              # Dependencies
```

### Features

- ✅ **AI-Native** - Designed for AI agents and humans
- ✅ **Dual API** - Both async (`browser`) and sync (`browserSync`) APIs
- ✅ **TypeScript** - Full type safety
- ✅ **Vitest Integration** - Modern test runner
- ✅ **Auto-waiting** - Built-in element waiting
- ✅ **Screenshot Support** - Easy screenshot capture
- ✅ **Chrome for Testing** - Automatically downloads Chrome

### Example Test

```typescript
import { browser } from 'vibium'

describe('Vibium Tests', () => {
  it('should navigate and interact', async () => {
    const vibe = await browser.launch({ headless: true })
    await vibe.go('https://example.com')
    
    const link = await vibe.find('a')
    const text = await link.text()
    console.log(`Found link: ${text}`)
    
    const screenshot = await vibe.screenshot()
    await vibe.quit()
  })
})
```

### Best Use Cases

- AI agent automation
- Modern TypeScript projects
- Quick browser automation scripts
- Both async and sync API needs
- Chrome-based testing

---

## 🤖 Robot Framework

### Overview
Robot Framework is a keyword-driven test automation framework that uses human-readable syntax, making it accessible to non-programmers.

### Prerequisites

- Python 3.13+ (latest stable version)

### Setup

```bash
pip install robotframework
pip install robotframework-seleniumlibrary
pip install robotframework-requests
```

### Running Tests

```bash
# All tests
./scripts/run-robot-tests.sh

# Specific test file
./scripts/run-robot-tests.sh GoogleSearchTests.robot

# Via Maven
./mvnw test -Probot

# Via Robot Framework CLI
robot src/test/robot/GoogleSearchTests.robot
```

### Test Structure

```
src/test/robot/
├── HomePageTests.robot        # UI tests (uses Page Object Model)
├── APITests.robot             # API tests
├── resources/                 # Page Object Resources
│   ├── Common.robot         # Common keywords and variables
│   └── HomePage.robot       # HomePage Page Object
├── WebDriverManager.py
└── README.md
```

**Page Object Model**: Robot Framework uses Resource files for the Page Object Model pattern. Common keywords are in `resources/Common.robot`, and page-specific resources are in `resources/`. All selectors use `data-qa` attributes for consistency.

### Features

- ✅ **Human-readable** - Keyword-driven syntax
- ✅ **Built-in Libraries** - Selenium, Requests, etc.
- ✅ **Data-driven Testing** - Easy test data management
- ✅ **HTML Reports** - Comprehensive test reports
- ✅ **Easy to Learn** - Non-programmers can write tests
- ✅ **Extensible** - Custom libraries support

### Example Test (Using Page Object Model)

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

**Page Objects**: Resource files in `resources/` provide reusable keywords and selectors. `Common.robot` contains shared setup/teardown and common keywords, while page-specific resources like `HomePage.robot` contain page-specific keywords. All selectors use `data-qa` attributes for consistency.

### Best Use Cases

- Non-technical testers
- Keyword-driven approach
- BDD-style tests
- API + UI combined testing
- Teams with mixed technical skills

---

## 🔀 Framework Selection Guide

### When to Use Selenium

✅ **Choose Selenium if:**
- You need Selenium Grid for distributed testing
- Working with legacy applications
- Team is already familiar with Java
- Need extensive browser support
- Enterprise environment requirements

### When to Use Playwright

✅ **Choose Playwright if:**
- Building modern web applications
- Need fast, reliable test execution
- Want TypeScript support
- Need network interception/mocking
- Want auto-waiting capabilities

### When to Use Cypress

✅ **Choose Cypress if:**
- Frontend-heavy applications
- JavaScript/TypeScript team
- Need time-travel debugging
- Want real-time test development
- Component testing needs

### When to Use Vibium

✅ **Choose Vibium if:**
- AI-native automation needs
- TypeScript projects
- Need both async and sync APIs
- Quick browser automation scripts
- Chrome-based testing is sufficient

### When to Use Robot Framework

✅ **Choose Robot Framework if:**
- Non-technical team members
- Keyword-driven approach preferred
- BDD-style tests needed
- API + UI combined testing
- Easy-to-read test syntax required

---

## 📊 Comparison Matrix

| Feature | Selenium | Playwright | Cypress | Vibium | Robot Framework |
|---------|----------|------------|---------|--------|-----------------|
| **Language** | Java | TypeScript | TypeScript | TypeScript | Python |
| **Browser Support** | All major | Chromium, Firefox, WebKit | Chrome, Firefox, Edge | Chrome | All via Selenium |
| **Auto-waiting** | Manual | ✅ Automatic | ✅ Automatic | ✅ Automatic | Manual |
| **Network Mocking** | Limited | ✅ Full support | ✅ Full support | Limited | Limited |
| **AI-Native** | ❌ | ❌ | ❌ | ✅ | ❌ |
| **Parallel Execution** | ✅ Yes | ✅ Yes | ⚠️ Limited | ✅ Yes |
| **Grid Support** | ✅ Yes | ⚠️ Limited | ❌ No | ✅ Yes |
| **Type Safety** | ✅ Java | ✅ TypeScript | ✅ TypeScript | ❌ No |
| **Learning Curve** | Medium | Medium | Easy | Easy |
| **Speed** | Medium | Fast | Fast | Medium |
| **Debugging** | Good | Excellent | Excellent | Good |
| **Reports** | Allure | HTML | HTML | HTML |

---

## 🚀 CI/CD Integration

All frameworks run in parallel in the CI/CD pipeline:

```
┌─────────────────┐
│  Smoke Tests    │ (Selenium)
├─────────────────┤
│  Grid Tests     │ (Selenium)
├─────────────────┤
│  Mobile Tests   │ (Selenium)
├─────────────────┤
│  Responsive     │ (Selenium)
├─────────────────┤
│  Cypress Tests  │ (TypeScript) ← NEW
├─────────────────┤
│ Playwright Tests│ (TypeScript) ← NEW
├─────────────────┤
│ Selenide Tests  │ (Selenium)
└─────────────────┘
```

All tests run **simultaneously** for faster execution!

---

## 🛠️ Troubleshooting

### Selenium

**Issue:** Grid connection failed
```bash
# Check Grid is running
curl http://localhost:4444/wd/hub/status

# Start Grid
docker-compose up -d selenium-hub chrome-node-1
```

### Playwright

**Issue:** Browsers not installed
```bash
cd playwright
npx playwright install --with-deps chromium
```

**Issue:** TypeScript errors
```bash
cd playwright
npx tsc --noEmit
```

### Cypress

**Issue:** Node modules not found
```bash
cd cypress
npm install
```

**Issue:** TypeScript errors
```bash
cd cypress
npm run build
```

### Robot Framework

**Issue:** Library not found
```bash
pip install robotframework-seleniumlibrary
```

**Issue:** Browser driver not found
```bash
# Robot Framework uses Selenium, so ensure drivers are available
# Or use WebDriverManager in your setup
```

---

## 📚 Additional Resources

- [Selenium Documentation](https://www.selenium.dev/documentation/)
- [Playwright Documentation](https://playwright.dev/)
- [Cypress Documentation](https://docs.cypress.io/)
- [Vibium Documentation](https://vibium.com/)
- [Robot Framework Documentation](https://robotframework.org/)
- [Multi-Framework Setup Guide](MULTI_FRAMEWORK_SETUP.md)

---

## 🎯 Best Practices

### 1. Choose the Right Framework
- Match framework to your team's skills
- Consider application requirements
- Evaluate maintenance needs

### 2. Page Object Model
- ✅ **Implemented**: All frameworks (Cypress, Playwright, Robot Framework) use Page Object Model
- **Cypress**: Page objects in `cypress/cypress/page-objects/` with `BasePage.ts` base class
- **Playwright**: Page objects in `playwright/tests/pages/` with `BasePage.ts` base class
- **Robot Framework**: Resource files in `src/test/robot/resources/` with `Common.robot` for shared keywords
- **Selector Strategy**: All frameworks use consistent `data-qa` attributes from frontend (`Sidebar.tsx`, `app/page.tsx`)
- Keep page objects reusable and maintainable
- Separate test logic from page interactions

### 3. Test Data Management
- Use external data files
- Avoid hardcoded values
- Use environment variables

### 4. Reporting
- Generate consistent reports
- Include screenshots on failures
- Track test execution history

### 5. CI/CD Integration
- Run tests in parallel
- Fail fast on critical tests
- Generate reports automatically

---

**Created**: January 2025  
**Last Updated**: January 2026

---

## 🧪 Frontend Unit Testing with Vitest

### Overview

The frontend application uses **Vitest** for unit testing and **React Testing Library** for component testing. In addition to functional tests, the frontend includes comprehensive **snapshot tests** to catch unintended UI changes.

### Setup

```bash
cd frontend
npm install
```

### Running Tests

```bash
# Run all tests (functional + snapshot)
cd frontend
npm test

# Run tests in watch mode
npm run test:watch

# Run tests with coverage
npm run test:coverage

# Run tests in UI mode (interactive)
npm run test:ui

# Run only snapshot tests
npm test -- __tests__/**/*.snapshot.test.tsx

# Update snapshots
npm test -- -u
```

### Test Structure

```
frontend/
├── __tests__/
│   ├── components/
│   │   ├── ui/
│   │   │   ├── Button.snapshot.test.tsx
│   │   │   ├── Input.snapshot.test.tsx
│   │   │   ├── Error.snapshot.test.tsx
│   │   │   ├── Loading.snapshot.test.tsx
│   │   │   └── __snapshots__/          # Auto-generated
│   │   ├── Sidebar.snapshot.test.tsx
│   │   ├── StatusBar.snapshot.test.tsx
│   │   ├── EntitySelect.snapshot.test.tsx
│   │   ├── EntityCreateModal.snapshot.test.tsx
│   │   └── __snapshots__/              # Auto-generated
│   └── pages/
│       ├── home.snapshot.test.tsx
│       ├── applications.snapshot.test.tsx
│       ├── companies.snapshot.test.tsx
│       ├── contacts.snapshot.test.tsx
│       ├── clients.snapshot.test.tsx
│       ├── notes.snapshot.test.tsx
│       ├── job-search-sites.snapshot.test.tsx
│       └── __snapshots__/               # Auto-generated
├── vitest.config.ts                     # Configuration
└── vitest.setup.ts                      # Test setup
```

### Snapshot Testing

**Snapshot tests** capture the rendered output of components and compare it against previously saved snapshots. They help catch unintended UI changes and serve as living documentation.

#### Snapshot Test Coverage

- **UI Components**: 23 snapshot tests (Button, Input, Error, Loading)
- **Complex Components**: 19 snapshot tests (Sidebar, StatusBar, EntitySelect, EntityCreateModal)
- **Page Components**: 23 snapshot tests (all main pages with loading, error, success, and empty states)
- **Total**: 65 snapshot tests

#### Running Snapshot Tests

```bash
# Run all snapshot tests
cd frontend
npm test -- __tests__/**/*.snapshot.test.tsx

# Run specific snapshot test file
npm test -- __tests__/components/ui/Button.snapshot.test.tsx

# Update snapshots after intentional changes
npm test -- -u

# Update specific snapshot file
npm test -- __tests__/components/ui/Button.snapshot.test.tsx -u
```

#### CI/CD Integration

Snapshot tests run automatically in the CI pipeline for each environment:
- **Jobs**: 
  - `test-fe-ss-dev` - Runs for dev environment
  - `test-fe-ss-test` - Runs for test environment  
  - `test-fe-ss-prod` - Runs for prod environment
- **Dependencies**: Same as FE E2E tests (gate-setup, determine-envs, determine-test-execution)
- **Control**: Enabled/disabled via `enable_snapshot_tests` output (defaults to `true`)
- **Execution**: Runs in parallel with FE E2E tests (no services required)
- **Gate Integration**: Included in environment gate jobs (`gate-dev`, `gate-test`, `gate-prod`)
- **Failure Impact**: Failures cause the pipeline to fail (same as other FE tests)
- **Location**: `.github/workflows/ci.yml`

#### Using data-qa Attributes in Unit Tests

Unit tests use `data-qa` attributes for stable, maintainable selectors, consistent with the Page Object Model approach used in E2E tests.

**Test Helper Utility**: `frontend/__tests__/utils/test-helpers.ts`

```typescript
import { getByQa } from '../utils/test-helpers';
import { within } from '@testing-library/react';

// Query by data-qa attribute
const title = getByQa('applications-title');
expect(title).toHaveTextContent('Applications');

// Query within a container that has data-qa
const tableBody = getByQa('applications-table-body');
expect(within(tableBody).getByText('Senior Software Engineer')).toBeInTheDocument();
```

**Benefits**:
- More stable than text-based queries
- Aligns with E2E test approach
- Less brittle to UI text changes
- Scoped queries within containers

#### Snapshot Test Example

```typescript
import { describe, it, expect } from 'vitest';
import { render } from '@testing-library/react';
import { Button } from '@/components/ui/Button';

describe('Button Snapshot Tests', () => {
  it('matches snapshot for primary button', () => {
    const { container } = render(
      <Button data-qa="test-button">Click me</Button>
    );
    expect(container.firstChild).toMatchSnapshot();
  });
});
```

#### Best Practices for Snapshot Tests

- ✅ Test stable, reusable components
- ✅ Test different states (loading, error, success, empty)
- ✅ Test different variants and props combinations
- ✅ Review snapshot changes before accepting
- ✅ Update snapshots intentionally, not automatically
- ⚠️ Avoid testing highly dynamic content without mocking

#### Managing Snapshots

- **Snapshot files** are auto-generated in `__snapshots__/` directories
- **Commit snapshots** to version control
- **Review changes** in PRs before accepting
- **Update snapshots** when making intentional UI changes: `npm test -- -u`

### Features

- ✅ **Vitest** - Fast, modern test runner
- ✅ **React Testing Library** - Component testing utilities
- ✅ **Snapshot Testing** - 65 snapshot tests for UI regression detection
- ✅ **TypeScript** - Full type safety
- ✅ **Mock Data** - Comprehensive mock data for all entities
- ✅ **Coverage Reports** - Code coverage tracking

### Best Use Cases

- Component unit testing
- UI regression detection
- Quick feedback on UI changes
- Documentation of component structure
- Catching unintended changes

---

## Playwright Data-qa Migration (Completed 2026-01-11)

### Migration Summary

✅ **Status**: Complete - All Playwright Page Objects now use `data-qa` attributes exclusively.

### What Was Done

1. **Frontend Updates**: Added `data-qa` attributes to all form inputs, page titles, tables, empty states, and dynamic elements across:
   - List pages (Companies, Clients, Notes, Job Search Sites, Applications)
   - Detail pages (Application Detail)
   - Form pages (Company, Contact, Client creation forms, Application wizard steps, Application edit form)

2. **Page Object Updates**: Updated all Page Objects to use `data-qa` selectors:
   - Replaced all fallback selectors (CSS classes, text content) with `data-qa` attributes
   - Added ID-based methods for dynamic elements (table rows, links, buttons)
   - Kept intentional fallback methods for backward compatibility where needed

3. **New Page Objects Created**:
   - `CompanyFormPage.ts` - Company creation form
   - `ContactFormPage.ts` - Contact creation form
   - `ClientFormPage.ts` - Client creation form
   - `WizardStep1Page.ts` - Application wizard step 1 (contact selection)

4. **Testing**: All tests verified and passing with new `data-qa` selectors.

### Benefits

- **Stable Selectors**: `data-qa` attributes are less likely to change than CSS classes or text content
- **Cross-Framework Consistency**: Same `data-qa` attributes used by Cypress, Playwright, and Robot Framework
- **Maintainability**: Clear separation between test selectors and styling/implementation details
- **Reliability**: Reduced test flakiness from UI changes that don't affect functionality

### Best Practices

- ✅ Always use `data-qa` attributes in Page Objects
- ✅ Never use direct locators (`page.locator()`, `getByLabel()`, etc.) in test files
- ✅ Use Page Object methods for all interactions
- ✅ For dynamic elements, use ID-based `data-qa` patterns: `data-qa="${entity-type}-${id}-${action}"`
