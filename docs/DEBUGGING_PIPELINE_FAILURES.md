# Debugging Pipeline Failures - Local Testing Guide

## 🎯 Overview

This guide helps you debug test failures in the CI/CD pipeline by running tests locally **without Docker**, which is especially useful when:
- You're running out of disk space for Docker
- You want faster iteration during development
- You need to identify and fix test failures quickly

## ✅ What We've Set Up

### 1. Local Test Runner Script

**File**: `./scripts/run-tests-local.sh`

This script runs all test frameworks that don't require Docker:
- ✅ Cypress Tests (TypeScript E2E)
- ✅ Playwright Tests (TypeScript E2E)
- ✅ Robot Framework Tests (Python - API tests only)
- ⚠️ Selenium/Java Tests (skipped - require Selenium Grid)

**Usage:**
```bash
./scripts/run-tests-local.sh
```

### 2. Documentation

- **LOCAL_TESTING_GUIDE.md** - Complete guide for running tests locally
- **DOCKER_TESTING_STATUS.md** - Updated with local testing option

## 🔍 Debugging Workflow

### Step 1: Run Local Tests

First, identify which tests are failing:

```bash
# Run all local tests
./scripts/run-tests-local.sh

# Or run individual frameworks
cd cypress && npm run cypress:run
cd playwright && npm test
```

### Step 2: Compare with Pipeline

The pipeline runs tests in this order (from `.github/workflows/test-environment.yml`):

1. **Smoke Tests** (Selenium/Java) - `testng-smoke-suite.xml`
2. **Grid Tests** (Selenium/Java) - `testng-ci-suite.xml`
3. **Cypress Tests** - `cd cypress && xvfb-run -a npm run cypress:run`
4. **Playwright Tests** - `cd playwright && npm test`
5. **Robot Framework Tests** - `./mvnw test -Probot`

### Step 3: Match Pipeline Environment

Set the same environment variables as the pipeline:

```bash
export BASE_URL="https://www.google.com"
export TEST_ENVIRONMENT="local"  # or "dev", "test", "prod"
export CI=true
export CYPRESS_BASE_URL="https://www.google.com"
```

### Step 4: Debug Specific Failures

#### Cypress Failures

```bash
cd cypress

# Run with verbose output
CYPRESS_BASE_URL="https://www.google.com" npm run cypress:run

# Or run interactively to see what's happening
npm run cypress:open

# Check for specific errors in:
# - cypress/cypress/videos/ (test recordings)
# - cypress/cypress/screenshots/ (failure screenshots)
```

**Common Cypress Issues:**
- Selector changes (Google may have updated their page structure)
- Cookie consent dialogs blocking interactions
- Network timeouts
- Base URL not set correctly

#### Playwright Failures

```bash
cd playwright

# Run with trace (helps debug failures)
BASE_URL="https://www.google.com" CI=true npx playwright test --trace on

# View the trace
npx playwright show-trace

# Run with UI mode to see what's happening
npm run test:ui
```

**Common Playwright Issues:**
- Timeout issues (increase timeout in config)
- Selector not found (page structure changed)
- Network issues
- Base URL configuration

#### Robot Framework Failures

```bash
# Run with verbose logging
export BASE_URL="https://www.google.com"
export TEST_ENVIRONMENT="local"
./mvnw test -Probot -X  # -X for debug output

# Check results in:
# target/robot-reports/report.html
```

**Common Robot Framework Issues:**
- Selenium Grid connection (web tests need Grid)
- Missing Python dependencies
- API endpoint changes

## 🐛 Common Test Failures

### Issue: Tests Pass Locally But Fail in Pipeline

**Possible Causes:**
1. **Environment Variables**: Pipeline uses different values
2. **Timing Issues**: Pipeline may be slower, causing timeouts
3. **Browser Versions**: Different browser versions between local and CI
4. **Network Issues**: CI environment may have different network conditions

**Solution:**
```bash
# Match pipeline environment exactly
export BASE_URL="https://www.google.com"  # Check pipeline for actual URL
export TEST_ENVIRONMENT="local"
export CI=true
export CYPRESS_BASE_URL="https://www.google.com"

# Run tests
./scripts/run-tests-local.sh
```

### Issue: Selector Not Found

**Symptoms:**
- `cy.get('input[name="q"]').should('be.visible')` fails
- `page.locator('#search')` times out

**Possible Causes:**
- Google updated their page structure
- Cookie consent dialog blocking elements
- Page not fully loaded

**Solution:**
1. Check if selectors need updating
2. Add waits for elements to appear
3. Handle cookie consent dialogs if present

### Issue: Timeout Errors

**Symptoms:**
- Tests timeout waiting for elements
- Network timeouts

**Solution:**
```bash
# Increase timeouts in config files
# Cypress: cypress.config.ts - increase defaultCommandTimeout
# Playwright: playwright.config.ts - increase timeout in use block
```

## 📊 Understanding Test Results

### Cypress Results
- **Location**: `cypress/cypress/videos/` and `cypress/cypress/screenshots/`
- **View**: Open video files to see what happened during test execution
- **Logs**: Check terminal output for specific error messages

### Playwright Results
- **Location**: `playwright/test-results/` and `playwright/playwright-report/`
- **View**: `cd playwright && npm run test:report`
- **Traces**: Use `npx playwright show-trace` to see step-by-step execution

### Robot Framework Results
- **Location**: `target/robot-reports/`
- **View**: Open `target/robot-reports/report.html` in browser
- **Logs**: Check `target/robot-reports/log.html` for detailed execution logs

## 🔧 Quick Fixes

### Fix 1: Update Selectors

If Google changed their page structure:

**Cypress:**
```typescript
// cypress/cypress/e2e/google-search.cy.ts
// Update selectors to match current page structure
cy.get('textarea[name="q"]').should('be.visible')  // New selector
```

**Playwright:**
```typescript
// playwright/tests/pages/GoogleSearchPage.ts
// Update selectors in page object
searchInput = page.locator('textarea[name="q"]')  // New selector
```

### Fix 2: Handle Cookie Consent

If cookie consent dialogs are blocking tests:

**Cypress:**
```typescript
beforeEach(() => {
  cy.visit('/')
  // Accept cookies if dialog appears
  cy.get('body').then(($body) => {
    if ($body.find('button:contains("Accept")').length > 0) {
      cy.get('button:contains("Accept")').click()
    }
  })
})
```

**Playwright:**
```typescript
test.beforeEach(async ({ page }) => {
  await page.goto(baseURL)
  // Accept cookies if dialog appears
  const acceptButton = page.locator('button:has-text("Accept")')
  if (await acceptButton.isVisible()) {
    await acceptButton.click()
  }
})
```

### Fix 3: Increase Timeouts

**Cypress** (`cypress.config.ts`):
```typescript
export default defineConfig({
  e2e: {
    defaultCommandTimeout: 15000,  // Increase from 10000
    requestTimeout: 15000,
    responseTimeout: 15000,
  }
})
```

**Playwright** (`playwright.config.ts`):
```typescript
use: {
  timeout: 30000,  // Increase timeout
  actionTimeout: 15000,
}
```

## 📝 Next Steps

1. **Run Local Tests**: Start with `./scripts/run-tests-local.sh`
2. **Identify Failures**: Check which framework is failing
3. **Debug Specific Tests**: Use framework-specific debugging tools
4. **Fix Issues**: Update selectors, timeouts, or test logic
5. **Verify Fixes**: Re-run local tests to confirm fixes
6. **Check Pipeline**: Push changes and verify pipeline passes

## 💡 Tips

1. **Start Small**: Fix one framework at a time
2. **Check Logs**: Always check test output for specific error messages
3. **Use Screenshots/Videos**: Visual debugging helps identify issues
4. **Match Environment**: Use same environment variables as pipeline
5. **Test Incrementally**: Run tests after each fix to verify

## 🔗 Related Documentation

- [LOCAL_TESTING_GUIDE.md](LOCAL_TESTING_GUIDE.md) - Complete local testing guide
- [DOCKER_TESTING_STATUS.md](DOCKER_TESTING_STATUS.md) - Docker testing status
- [.github/workflows/test-environment.yml](../.github/workflows/test-environment.yml) - Pipeline configuration

---

**Last Updated**: 2025-01-XX
**Status**: Ready for debugging pipeline failures locally
