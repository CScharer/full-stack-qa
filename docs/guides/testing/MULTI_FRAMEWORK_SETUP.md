# Multi-Framework Testing Setup

This guide explains how to use the multiple testing frameworks available in this project: Cypress, Playwright, Robot Framework, Selenide, Selenium, and Vibium.

---

## 📋 Overview

This framework now supports **6 different UI testing tools**:

1. 🎬 [**Cypress** (TypeScript)](#cypress) - Frontend-focused, time-travel debugging
2. 🎭 [**Playwright** (TypeScript)](#playwright) - Modern, fast, reliable
3. 🤖 [**Robot Framework** (Python)](#robot-framework) - Keyword-driven, human-readable
4. 🛡️ [**Selenide** (Java)](#selenide) - Concise Selenium wrapper, fluent API
5. 🔧 [**Selenium** (Java)](#selenium) - Legacy support, Grid compatibility
6. ⚡ [**Vibium** (TypeScript)](#vibium) - AI-native automation, modern browser control

---

## 🔧 Shared Configuration

All test frameworks use **`config/environments.json`** as the single source of truth for environment configuration (ports, URLs, database, timeouts, CORS). This ensures consistency across all frameworks and eliminates hardcoded values.

### Configuration Files

- **`config/environments.json`** - Single source of truth for all environment configuration
- **`config/port-config.ts`** - Shared TypeScript utility (used by Cypress, Playwright, Vibium, Frontend)
- **`config/port_config.py`** - Shared Python utility (used by Robot Framework, Backend)
- **`src/test/java/com/cjs/qa/config/EnvironmentConfig.java`** - Java utility (optional, for newer Selenium/Java tests)

### Standardized Environment Variables

All frameworks use consistent environment variable naming:

- **`BASE_URL`** - Frontend/base URL (all frameworks)
- **`BACKEND_URL`** - Backend API URL (for API calls)
- **`ENVIRONMENT`** - Environment name (dev, test, prod)

### Framework-Specific Usage

**TypeScript Frameworks (Cypress, Playwright, Vibium):**
```typescript
import { getBackendUrl, getFrontendUrl } from '../config/port-config';
// or
import { getBackendUrl, getFrontendUrl } from '../../config/port-config';

const backendUrl = getBackendUrl('dev'); // Uses config/environments.json
const frontendUrl = getFrontendUrl('test'); // Uses config/environments.json
```

**Robot Framework:**
```robotframework
Library    ${CURDIR}${/}ConfigHelper.py
${base_url}=    Get Base Url From Shared Config
```

**Selenium/Java (Optional):**
```java
import com.cjs.qa.config.EnvironmentConfig;

String baseUrl = EnvironmentConfig.getFrontendUrl(); // Uses config/environments.json
String backendUrl = EnvironmentConfig.getBackendUrl(); // Uses config/environments.json
```

### TypeScript Base Configuration

All TypeScript frameworks (Cypress, Playwright, Vibium) extend a shared base configuration:

- **`tsconfig.base.json`** - Common TypeScript compiler options
- Each framework's `tsconfig.json` extends the base and adds framework-specific options

This reduces duplication and ensures consistent TypeScript compilation across all frameworks.

For more details, see [Configuration Directory README](../../../config/README.md).

---

## Cypress

### Overview

Cypress is a modern JavaScript/TypeScript testing framework that runs directly in the browser, providing excellent debugging capabilities and time-travel debugging.

### Prerequisites

- Node.js 18+
- npm or yarn

### Setup

```bash
cd cypress
npm install
```

### Running Tests

```bash
# Interactive mode (Test Runner) - Recommended for development
./scripts/run-cypress-tests.sh open

# Headless mode
./scripts/run-cypress-tests.sh run chrome

# Specific browser
./scripts/run-cypress-tests.sh run firefox
./scripts/run-cypress-tests.sh run edge

# Direct npm commands
cd cypress
npm run cypress:run    # Headless mode
npm run cypress:open   # Interactive mode
```

### Environment Variables

```bash
export BASE_URL="http://localhost:3003"
export ENVIRONMENT="local"
```

### Test Structure

```
cypress/
├── cypress/
│   ├── e2e/
│   │   └── homepage.cy.ts         # Test files (TypeScript)
│   ├── page-objects/              # Page Object Model classes
│   │   ├── BasePage.ts            # Base class with common methods
│   │   └── HomePage.ts            # HomePage Page Object
│   └── support/
│       ├── commands.ts             # Custom commands
│       └── e2e.ts                  # Support file
├── cypress.config.ts               # Configuration (TypeScript)
├── tsconfig.json                    # TypeScript config
└── package.json
```

**Page Object Model**: All Cypress tests use the Page Object Model pattern with `data-qa` selectors for consistency across frameworks.

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

**Page Objects**: Located in `cypress/cypress/page-objects/` with `BasePage.ts` providing common functionality and page-specific classes extending it. All selectors use `data-qa` attributes from the frontend for consistency.

### Best Use Cases

- Frontend-heavy applications
- JavaScript/TypeScript teams
- Time-travel debugging needs
- Component testing
- Real-time development workflow

### Troubleshooting

**Issue:** Node modules not found
```bash
cd cypress && npm install
```

**Issue:** TypeScript errors
```bash
cd cypress
npm run build
```

---

## Playwright

### Overview

Playwright is a modern, fast, and reliable end-to-end testing framework with excellent browser automation capabilities, auto-waiting, and network interception.

### Prerequisites

- Node.js 18+
- npm or yarn

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
cd playwright && npm run test:webkit

# UI mode (interactive)
cd playwright && npm run test:ui

# Debug mode
cd playwright && npm run test:debug

# Headed mode (see browser)
cd playwright && npm run test:headed
```

### Environment Variables

```bash
export BASE_URL="http://localhost:3003"
export ENVIRONMENT="local"
export CI=true
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

### Troubleshooting

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

---

## Robot Framework

### Overview

Robot Framework is a keyword-driven test automation framework that uses human-readable syntax, making it accessible to non-programmers. It supports both UI and API testing.

### Prerequisites

- Python 3.13+ (latest stable version)
- pip

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

# API tests only (no Grid needed)
cd src/test/robot
robot --include api tests/

# Web tests (requires Grid)
robot --include web tests/
```

### Test Structure

```
src/test/robot/
├── HomePageTests.robot        # UI tests (uses Page Object Model)
├── APITests.robot             # API tests
├── resources/                  # Page Object Resources
│   ├── Common.robot          # Common keywords and variables
│   └── HomePage.robot        # HomePage Page Object
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
- ✅ **API + UI** - Combined testing capabilities

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

### Troubleshooting

**Issue:** Library not found
```bash
pip install robotframework-seleniumlibrary
pip install robotframework-requests
```

**Issue:** Browser driver not found
```bash
# Robot Framework uses Selenium, so ensure drivers are available
# Or use WebDriverManager in your setup
```

**Note**: Web tests require Selenium Grid. See [Docker Guide](../infrastructure/DOCKER.md) for Grid setup.

---

## Selenide

### Overview

Selenide is a concise wrapper around Selenium WebDriver that provides a fluent API for writing clean and readable tests. It automatically handles waits and provides built-in screenshot capabilities.

### Prerequisites

- Java 21+
- Maven (configured in `pom.xml`)

### Setup

Already configured in `pom.xml`. No additional setup needed!

### Running Tests

```bash
# Run Selenide tests via Maven
./mvnw test -DsuiteXmlFile=testng-selenide-suite.xml

# Or using the test script
./scripts/ci/run-maven-tests.sh test selenide testng-selenide-suite.xml

# Specific test class
./mvnw test -Dtest=HomePageTests
```

### Test Structure

```
src/test/java/com/cjs/qa/junit/
├── HomePageTests.java          # Selenide test examples
└── pages/
    └── HomePage.java            # Page Object Model
```

### Features

- ✅ **Fluent API** - Concise and readable syntax
- ✅ **Automatic waits** - No explicit wait statements needed
- ✅ **Built-in screenshots** - Automatic on test failures
- ✅ **Selenium Grid support** - Works with Grid infrastructure
- ✅ **TestNG integration** - Advanced test management
- ✅ **Allure reports** - Beautiful test reports
- ✅ **Page Object Model** - Clean architecture support

### Example Test

```java
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import com.codeborne.selenide.SelenideElement;

@Test
public void testHomePage() {
    open("http://localhost:3003");
    $("#search").shouldBe(visible);
    $("#search").setValue("Selenide");
    $("#search").pressEnter();
    title().shouldContain("Search");
}
```

### Configuration

Selenide can be configured in test setup:

```java
@BeforeMethod
public void setUp() {
    Configuration.browser = "chrome";
    Configuration.headless = true;
    Configuration.timeout = 5000;
    Configuration.pageLoadTimeout = 10000;
    Configuration.browserSize = "1920x1080";
    Configuration.baseUrl = "http://localhost:3003";
}
```

### Best Use Cases

- Java-based test teams
- Teams familiar with Selenium
- Projects requiring concise test syntax
- Selenium Grid compatibility needs
- Quick test development

### Troubleshooting

**Issue:** Tests not running
```bash
# Verify Selenium Grid is running
docker-compose up -d selenium-hub chrome-node-1

# Check Maven dependencies
./mvnw dependency:tree | grep selenide
```

**Issue:** Grid connection failed
```bash
# Check Grid is running
curl http://localhost:4444/wd/hub/status
```

---

## Selenium

### Overview

Selenium is the industry-standard web automation framework with extensive browser and language support. It's the foundation for many other testing frameworks and provides robust Grid capabilities.

### Prerequisites

- Java 21+
- Maven (configured in `pom.xml`)

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

# Via Maven with suite file
./mvnw test -DsuiteXmlFile=testng-ci-suite.xml

# Smoke tests only
./scripts/run-smoke-tests.sh
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
- ✅ **Multi-browser** - Chrome, Firefox, Edge, Safari
- ✅ **Page Object Model** - Clean architecture
- ✅ **TestNG Integration** - Advanced test management
- ✅ **Allure Reports** - Beautiful test reports
- ✅ **Parallel Execution** - 5 threads by default
- ✅ **WebDriverManager** - Automatic driver management

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

### Troubleshooting

**Issue:** WebDriver not found
```bash
# WebDriverManager should handle this automatically
# If issues persist, check Grid connection
docker-compose ps
```

**Issue:** Grid connection failed
```bash
# Check Grid is running
curl http://localhost:4444/wd/hub/status

# Start Grid
docker-compose up -d selenium-hub chrome-node-1
```

---

## Vibium

### Overview

Vibium is an AI-native browser automation framework developed by Jason Huggins (creator of Selenium and Appium). It provides both async and sync APIs for browser automation and is designed for both AI agents and humans.

### Prerequisites

- Node.js 18+
- npm or yarn

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

# Watch mode
cd vibium && npm run test:watch

# UI mode
cd vibium && npm run test:ui
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

### Example Test (Async API)

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

### Example Test (Sync API)

```typescript
import { browserSync } from 'vibium'

describe('Vibium Sync Tests', () => {
  it('should navigate and interact', () => {
    const vibe = browserSync.launch()
    vibe.go('https://example.com')
    
    const link = vibe.find('a')
    const text = link.text()
    console.log(`Found link: ${text}`)
    
    const screenshot = vibe.screenshot()
    vibe.quit()
  })
})
```

### Best Use Cases

- AI-native automation projects
- Modern TypeScript teams
- Projects requiring both sync and async APIs
- Advanced browser control needs
- Quick browser automation scripts
- Chrome-based testing

### Troubleshooting

**Issue:** Vibium package not found
```bash
cd vibium && npm install
# Verify installation
npm list vibium
```

**Issue:** Clicker binary not found (macOS ARM64)
```bash
# The @vibium/darwin-arm64 package should be installed automatically
# If not, reinstall:
cd vibium && npm install @vibium/darwin-arm64
```

---

## 🔀 Running Multiple Frameworks

### Maven Profiles

Use Maven profiles to run specific frameworks:

```bash
# Selenium only (default)
./mvnw test

# Robot Framework only
./mvnw test -Probot

# All frameworks (run sequentially)
./mvnw test -Pselenium,playwright,robot
```

### Framework Selection Matrix

| Framework | Language | Maven Profile | Script | Best For |
|-----------|----------|---------------|--------|----------|
| Selenium | Java | `selenium` (default) | `run-tests.sh` | Legacy, Grid |
| Playwright | TypeScript | N/A | `run-playwright-tests.sh` | Modern apps |
| Cypress | TypeScript | N/A | `run-cypress-tests.sh` | Frontend-heavy |
| Robot Framework | Python | `robot` | `run-robot-tests.sh` | Non-technical |
| Selenide | Java | `selenium` | `run-maven-tests.sh` | Concise syntax |
| Vibium | TypeScript | N/A | `run-vibium-tests.sh` | AI-native |

---

## 📊 Comparison

### Speed
1. **Playwright** - Fastest (auto-waiting, parallel execution)
2. **Cypress** - Fast (runs in browser)
3. **Vibium** - Fast (modern browser control)
4. **Selenium** - Medium (depends on driver)
5. **Selenide** - Medium (Selenium wrapper)
6. **Robot Framework** - Medium (Python overhead)

### Learning Curve
1. **Robot Framework** - Easiest (keyword-driven)
2. **Cypress** - Easy (JavaScript, good docs)
3. **Vibium** - Easy (TypeScript, modern API)
4. **Selenide** - Medium (Java, fluent API)
5. **Selenium** - Medium (Java, WebDriver API)
6. **Playwright** - Medium (TypeScript, async concepts)

### Best Use Cases Summary

**Selenium:**
- Legacy applications
- Selenium Grid requirements
- Cross-browser matrix testing
- Large existing test suites
- Enterprise environments

**Selenide:**
- Java-based test teams
- Teams familiar with Selenium
- Projects requiring concise test syntax
- Selenium Grid compatibility needs
- Quick test development

**Playwright:**
- Modern web applications
- API mocking needs
- Fast execution requirements
- Multi-browser testing
- TypeScript projects

**Cypress:**
- Frontend-heavy applications
- JavaScript/TypeScript teams
- Time-travel debugging needs
- Component testing
- Real-time development workflow

**Robot Framework:**
- Non-technical testers
- Keyword-driven approach
- BDD-style tests
- API + UI combined testing
- Teams with mixed technical skills

**Vibium:**
- AI-native automation projects
- Modern TypeScript teams
- Projects requiring both sync and async APIs
- Advanced browser control needs
- Quick browser automation scripts

---

## 📚 Additional Resources

- [Cypress Documentation](https://docs.cypress.io/)
- [Playwright Documentation](https://playwright.dev/)
- [Robot Framework Documentation](https://robotframework.org/)
- [Selenide Documentation](https://selenide.org/)
- [Selenium Documentation](https://www.selenium.dev/documentation/)
- [Vibium Documentation](https://vibium.com/)

---

**Created**: January 2025
**Last Updated**: January 2026
