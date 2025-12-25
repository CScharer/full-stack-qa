# Local Testing Guide (Without Docker)

This guide explains how to run and debug tests locally without Docker, which is useful when:
- You're running out of disk space for Docker
- You want faster iteration during development
- You need to debug specific test failures

## 🎯 What Can Be Run Locally

### ✅ Can Run Locally (No Docker Required)

1. **Cypress Tests** - TypeScript E2E tests
2. **Playwright Tests** - TypeScript E2E tests  
3. **Vibium Tests** - TypeScript browser automation tests
4. **Robot Framework API Tests** - Python keyword-driven API tests
5. **Code Compilation** - Maven build and compile

### ⚠️ Requires Selenium Grid (Docker or Local Grid)

1. **Selenium/Java Tests** - All TestNG test suites
   - Smoke Tests
   - Grid Tests
   - Mobile Browser Tests
   - Responsive Design Tests
   - Selenide Tests
2. **Robot Framework Web Tests** - Tests that use Selenium Library

## 🚀 Quick Start

### Run All Local Tests

```bash
# Run Cypress, Playwright, and Robot Framework API tests
./scripts/run-tests-local.sh
```

This script will:
- ✅ Check prerequisites (Node.js, Java, Python)
- ✅ Run Cypress tests
- ✅ Run Playwright tests
- ✅ Run Vibium tests
- ✅ Run Robot Framework tests (if Python is available)
- ⚠️ Skip Selenium/Java tests (require Grid)

### Run Individual Test Frameworks

#### Cypress Tests
```bash
cd cypress
npm install  # First time only
export CYPRESS_BASE_URL="https://www.google.com"
export TEST_ENVIRONMENT="local"
npm run cypress:run
```

#### Playwright Tests
```bash
cd playwright
npm install  # First time only
npx playwright install --with-deps chromium  # First time only
export BASE_URL="https://www.google.com"
export TEST_ENVIRONMENT="local"
export CI=true
npm test
```

#### Vibium Tests
```bash
# Using the test script (recommended)
./scripts/run-vibium-tests.sh

# Or directly
cd vibium
npm install  # First time only
npm test
```

#### Robot Framework Tests
```bash
# Install dependencies (first time only)
pip3 install --user robotframework robotframework-seleniumlibrary robotframework-requests

# Run API tests (don't require Grid)
export BASE_URL="https://www.google.com"
export TEST_ENVIRONMENT="local"
./mvnw test -Probot
```

## 🔍 Debugging Pipeline Failures

### Step 1: Run Local Tests

First, run the local test script to see if tests pass locally:

```bash
./scripts/run-tests-local.sh
```

### Step 2: Check Specific Test Failures

If a specific test framework is failing in the pipeline:

#### Cypress Failures
```bash
cd cypress
# Run with verbose output
CYPRESS_BASE_URL="https://www.google.com" npm run cypress:run -- --reporter json --reporter-options output=cypress-results.json

# Or run interactively to see what's happening
npm run cypress:open
```

#### Playwright Failures
```bash
cd playwright
# Run with trace (helps debug failures)
BASE_URL="https://www.google.com" CI=true npx playwright test --trace on

# View the trace
npx playwright show-trace
```

#### Robot Framework Failures
```bash
# Run with verbose logging
export BASE_URL="https://www.google.com"
export TEST_ENVIRONMENT="local"
./mvnw test -Probot -X  # -X for debug output
```

### Step 3: Compare with Pipeline

The pipeline runs tests in this order:

1. **Smoke Tests** (Selenium/Java) - Requires Grid
2. **Grid Tests** (Selenium/Java) - Requires Grid
3. **Cypress Tests** - ✅ Can run locally
4. **Playwright Tests** - ✅ Can run locally
5. **Robot Framework Tests** - ⚠️ Web tests require Grid, API tests don't

### Step 4: Environment Variables

Make sure you're using the same environment variables as the pipeline:

```bash
# Pipeline uses these (from test-environment.yml):
export BASE_URL="https://www.google.com"  # Or your test URL
export TEST_ENVIRONMENT="local"  # Or "dev", "test", "prod"
export CI=true  # For Playwright
export CYPRESS_BASE_URL="https://www.google.com"  # For Cypress
```

## 🐛 Common Issues

### Issue: Cypress Tests Fail Locally But Pass in Pipeline

**Possible Causes:**
- Different base URL
- Missing environment variables
- Browser version differences

**Solution:**
```bash
# Match pipeline environment exactly
export CYPRESS_BASE_URL="https://www.google.com"
export TEST_ENVIRONMENT="local"
cd cypress && npm run cypress:run
```

### Issue: Playwright Tests Timeout

**Possible Causes:**
- Network issues
- Base URL not accessible
- Missing CI environment variable

**Solution:**
```bash
# Add timeout and check base URL
export BASE_URL="https://www.google.com"
export CI=true
cd playwright && npx playwright test --timeout=60000
```

### Issue: Robot Framework Can't Find Selenium

**Possible Causes:**
- Robot Framework web tests require Selenium Grid
- Missing Python dependencies

**Solution:**
```bash
# Install dependencies
pip3 install --user robotframework robotframework-seleniumlibrary robotframework-requests

# For web tests, you need Grid. For API tests only:
# Make sure your Robot tests don't use SeleniumLibrary for API tests
```

## 📊 Test Results

### Cypress Results
- Location: `cypress/cypress/videos/` and `cypress/cypress/screenshots/`
- View: Open `cypress/cypress/videos/` to see test recordings

### Playwright Results
- Location: `playwright/test-results/` and `playwright/playwright-report/`
- View: `cd playwright && npm run test:report`

### Robot Framework Results
- Location: `target/robot-reports/`
- View: Open `target/robot-reports/report.html` in browser

## 🔄 Matching Pipeline Configuration

The CI/CD pipeline (`.github/workflows/test-environment.yml`) runs:

1. **Smoke Tests**: `./mvnw test -DsuiteXmlFile=testng-smoke-suite.xml`
2. **Grid Tests**: `./mvnw test -DsuiteXmlFile=testng-ci-suite.xml`
3. **Cypress**: `cd cypress && xvfb-run -a npm run cypress:run`
4. **Playwright**: `cd playwright && npm test`
5. **Robot Framework**: `./mvnw test -Probot`

To match the pipeline exactly for local tests:

```bash
# Set same environment variables
export BASE_URL="https://www.google.com"
export TEST_ENVIRONMENT="local"
export CI=true
export CYPRESS_BASE_URL="https://www.google.com"

# Run Cypress (matches pipeline)
cd cypress && npm run cypress:run

# Run Playwright (matches pipeline)
cd playwright && npm test

# Note: Selenium/Java tests still need Grid
```

## 💡 Tips

1. **Start with Local Tests**: Run `./scripts/run-tests-local.sh` first to catch obvious issues
2. **Check Logs**: Look at test output for specific error messages
3. **Compare Versions**: Make sure your local Node.js, Java, and Python versions match the pipeline
4. **Environment Variables**: Always set the same environment variables as the pipeline
5. **Test One Framework at a Time**: If something fails, isolate which framework is causing issues

## 📝 Next Steps

If local tests pass but pipeline fails:
1. Check GitHub Actions logs for specific error messages
2. Compare environment variables between local and pipeline
3. Check if there are timing issues (add waits/timeouts)
4. Verify all dependencies are installed correctly in pipeline

If you need to test Selenium/Java tests locally:
- Use `./scripts/run-smoke-tests.sh` (requires Docker for Grid)
- Or set up a local Selenium Grid without Docker (more complex)

---

**Last Updated**: 2025-01-XX
**Status**: Ready for local testing without Docker
