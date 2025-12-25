# Multi-Framework Testing Setup

This guide explains how to use the multiple testing frameworks available in this project: Selenium, Playwright, Cypress, and Robot Framework.

---

## 📋 Overview

This framework now supports **4 different UI testing tools**:

1. **Cypress** (JavaScript) - Frontend-focused, time-travel debugging
2. **Playwright** (Java) - Modern, fast, reliable
3. **Robot Framework** (Python) - Keyword-driven, human-readable
4. **Selenium** (Java) - Legacy support, Grid compatibility

---

## 🎭 Playwright (TypeScript)

### Setup

```bash
cd playwright
npm install
npx playwright install
```

### Running Tests

```bash
# Using helper script
./scripts/run-playwright-tests.sh chromium

# All browsers
cd playwright && npm test

# Specific browser
cd playwright && npm run test:chrome
cd playwright && npm run test:firefox

# UI mode (interactive)
cd playwright && npm run test:ui
```

### Test Structure

```
playwright/
├── tests/
│   ├── google-search.spec.ts    # Test files
│   └── pages/
│       └── GoogleSearchPage.ts  # Page Object Model
├── playwright.config.ts          # Configuration
└── tsconfig.json                 # TypeScript config
```

### Features

- ✅ TypeScript for type safety
- ✅ Auto-waiting for elements
- ✅ Network interception
- ✅ Multi-browser (Chromium, Firefox, WebKit)
- ✅ Screenshot/video capture
- ✅ HTML reports

### Example Test

```typescript
import { test, expect } from '@playwright/test'
import { GoogleSearchPage } from './pages/GoogleSearchPage'

test('should perform a search', async ({ page }) => {
  const googlePage = new GoogleSearchPage(page)
  await googlePage.navigate()
  await googlePage.search('Playwright')
  await expect(googlePage.searchResults).toBeVisible()
})
```

---

## 🎬 Cypress (TypeScript)

### Setup

```bash
cd cypress
npm install
```

### Running Tests

```bash
# Interactive mode (Test Runner)
./scripts/run-cypress-tests.sh open

# Headless mode
./scripts/run-cypress-tests.sh run chrome

# Specific browser
./scripts/run-cypress-tests.sh run firefox
```

### Test Structure

```
cypress/
├── cypress/
│   ├── e2e/
│   │   └── google-search.cy.ts    # Test files (TypeScript)
│   └── support/
│       ├── commands.ts             # Custom commands
│       └── e2e.ts                  # Support file
├── cypress.config.ts               # Configuration (TypeScript)
├── tsconfig.json                    # TypeScript config
└── package.json
```

### Features

- ✅ TypeScript for type safety
- ✅ Time-travel debugging
- ✅ Real-time reloads
- ✅ Automatic waiting
- ✅ Network stubbing
- ✅ Screenshot/video capture

### Example Test

```typescript
describe('Google Search Tests', () => {
  it('should perform a search', () => {
    cy.visit('/')
    cy.get('input[name="q"]').type('Cypress{enter}')
    cy.url().should('include', 'search')
  })
})
```

---

## 🤖 Robot Framework

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
```

### Test Structure

```
src/test/robot/
├── GoogleSearchTests.robot    # UI tests
├── APITests.robot              # API tests
└── README.md
```

### Features

- ✅ Human-readable keyword syntax
- ✅ Built-in libraries
- ✅ Data-driven testing
- ✅ HTML reports
- ✅ Easy for non-programmers

### Example Test

```robot
*** Test Cases ***
Perform Google Search
    Open Browser    https://www.google.com    chrome
    Input Text      name:q    Robot Framework
    Press Keys      name:q    RETURN
    Wait Until Page Contains    Robot Framework
    Close Browser
```

---

## 🔀 Running Multiple Frameworks

### Maven Profiles

Use Maven profiles to run specific frameworks:

```bash
# Selenium only (default)
./mvnw test

# Playwright only
./mvnw test -Pplaywright

# Robot Framework only
./mvnw test -Probot

# All frameworks (run sequentially)
./mvnw test -Pselenium,playwright,robot
```

### Framework Selection Matrix

| Framework | Language | Maven Profile | Script | Best For |
|-----------|----------|---------------|--------|----------|
| Selenium | Java | `selenium` (default) | `run-tests.sh` | Legacy, Grid |
| Playwright | Java | `playwright` | `run-playwright-tests.sh` | Modern apps |
| Cypress | JavaScript | N/A | `run-cypress-tests.sh` | Frontend-heavy |
| Robot Framework | Python | `robot` | `run-robot-tests.sh` | Non-technical |

---

## 📊 Comparison

### Speed
1. **Playwright** - Fastest (auto-waiting, parallel execution)
2. **Cypress** - Fast (runs in browser)
3. **Selenium** - Medium (depends on driver)
4. **Robot Framework** - Medium (Python overhead)

### Learning Curve
1. **Robot Framework** - Easiest (keyword-driven)
2. **Cypress** - Easy (JavaScript, good docs)
3. **Selenium** - Medium (Java, WebDriver API)
4. **Playwright** - Medium (Java, async concepts)

### Best Use Cases

**Selenium:**
- Legacy applications
- Selenium Grid requirements
- Cross-browser matrix testing
- Large existing test suites

**Playwright:**
- Modern web applications
- API mocking needs
- Fast execution requirements
- Multi-browser testing

**Cypress:**
- Frontend-heavy applications
- JavaScript/TypeScript teams
- Time-travel debugging needs
- Component testing

**Robot Framework:**
- Non-technical testers
- Keyword-driven approach
- BDD-style tests
- API + UI combined testing

---

## 🛠️ Troubleshooting

### Playwright

**Issue:** Browsers not installed
```bash
./mvnw exec:java -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install chromium"
```

### Cypress

**Issue:** Node modules not found
```bash
cd cypress && npm install
```

### Robot Framework

**Issue:** Library not found
```bash
pip install robotframework-seleniumlibrary
```

---

## 📚 Additional Resources

- [Playwright Documentation](https://playwright.dev/java/)
- [Cypress Documentation](https://docs.cypress.io/)
- [Robot Framework Documentation](https://robotframework.org/)
- [Selenium Documentation](https://www.selenium.dev/documentation/)

---

**Created**: January 2025
**Last Updated**: January 2025

