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
│   ├── google-search.spec.ts    # Test files
│   └── pages/
│       └── GoogleSearchPage.ts  # Page Object Model
├── playwright.config.ts          # Configuration
└── tsconfig.json                 # TypeScript config
```

### Features

- ✅ **TypeScript** - Type safety and better IDE support
- ✅ **Auto-waiting** - No manual waits needed
- ✅ **Network Interception** - Mock API calls
- ✅ **Multi-browser** - Chromium, Firefox, WebKit
- ✅ **Screenshot/Video** - Automatic capture
- ✅ **HTML Reports** - Beautiful test reports
- ✅ **Parallel Execution** - Built-in support

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

```bash
# Interactive mode (Test Runner)
./scripts/run-cypress-tests.sh open

# Headless mode
./scripts/run-cypress-tests.sh run chrome

# Specific browser
./scripts/run-cypress-tests.sh run firefox
./scripts/run-cypress-tests.sh run edge
```

### Test Structure

```
cypress/
├── cypress/
│   ├── e2e/
│   │   └── google-search.cy.ts  # Test files (TypeScript)
│   └── support/
│       ├── commands.ts            # Custom commands
│       └── e2e.ts                 # Support file
├── cypress.config.ts              # Configuration
└── tsconfig.json                  # TypeScript config
```

### Features

- ✅ **TypeScript** - Type safety and better IDE support
- ✅ **Time-travel Debugging** - See every step of test execution
- ✅ **Real-time Reloads** - See changes instantly
- ✅ **Automatic Waiting** - No manual waits needed
- ✅ **Network Stubbing** - Mock API responses
- ✅ **Screenshot/Video** - Automatic capture
- ✅ **Cross-browser** - Chrome, Firefox, Edge

### Example Test

```typescript
describe('Google Search Tests', () => {
  it('should perform a search', () => {
    cy.visit('/')
    cy.get('input[name="q"]').type('Cypress{enter}')
    cy.url().should('include', 'search')
    cy.get('#search').should('be.visible')
  })
})
```

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
├── GoogleSearchTests.robot    # UI tests
├── APITests.robot              # API tests
└── README.md
```

### Features

- ✅ **Human-readable** - Keyword-driven syntax
- ✅ **Built-in Libraries** - Selenium, Requests, etc.
- ✅ **Data-driven Testing** - Easy test data management
- ✅ **HTML Reports** - Comprehensive test reports
- ✅ **Easy to Learn** - Non-programmers can write tests
- ✅ **Extensible** - Custom libraries support

### Example Test

```robot
*** Test Cases ***
Perform Google Search
    Open Browser    https://www.google.com    chrome
    Input Text      name:q    Robot Framework
    Press Keys      name:q    RETURN
    Wait Until Page Contains    Robot Framework
    Page Should Contain Element    id:search
    Close Browser
```

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
- Use POM for all frameworks
- Keep page objects reusable
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
**Last Updated**: January 2025

