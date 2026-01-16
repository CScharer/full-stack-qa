# Local Testing Guide

**Last Updated**: 2025-12-27  
**Purpose**: Guide for running and debugging tests locally without Docker

---

## 📋 Overview

This guide explains how to run and debug tests locally without Docker, which is useful when:
- You're running out of disk space for Docker
- You want faster iteration during development
- You need to debug specific test failures
- You need to identify and fix test failures quickly

---

## 🎯 What Can Be Run Locally

### ✅ Can Run Locally (No Docker Required)

1. **Frontend Unit Tests** - Vitest + React Testing Library (functional + snapshot tests)
2. **Cypress Tests** - TypeScript E2E tests
3. **Playwright Tests** - TypeScript E2E tests  
4. **Vibium Tests** - TypeScript browser automation tests
5. **Robot Framework API Tests** - Python keyword-driven API tests
6. **Code Compilation** - Maven build and compile

### ⚠️ Requires Selenium Grid (Docker or Local Grid)

1. **Selenium/Java Tests** - All TestNG test suites
   - Smoke Tests
   - Grid Tests
   - Mobile Browser Tests
   - Responsive Design Tests
   - Selenide Tests
2. **Robot Framework Web Tests** - Tests that use Selenium Library

---

## 🚀 Quick Start

### Run All Local Tests

```bash
# Run Cypress, Playwright, and Robot Framework API tests
./scripts/tests/run-tests-local.sh
```

This script will:
- ✅ Check prerequisites (Node.js, Java, Python)
- ✅ Run Cypress tests
- ✅ Run Playwright tests
- ✅ Run Vibium tests
- ✅ Run Robot Framework tests (if Python is available)
- ⚠️ Skip Selenium/Java tests (require Grid)

### Run Individual Test Frameworks

#### Frontend Unit Tests (Vitest)

**Prerequisites:**
- Node.js 18+
- npm or yarn

**Setup:**
```bash
cd frontend
npm install
```

**Run Tests:**
```bash
# Run all tests (functional + snapshot)
npm test

# Run tests in watch mode
npm run test:watch

# Run tests with coverage
npm run test:coverage

# Run tests in UI mode (interactive)
npm run test:ui

# Run only snapshot tests
npm test -- __tests__/**/*.snapshot.test.tsx

# Update snapshots after intentional changes
npm test -- -u
```

**Using data-qa Attributes in Tests:**

Unit tests use `data-qa` attributes for stable selectors. Import the test helper:

```typescript
import { getByQa } from '../utils/test-helpers';
import { within } from '@testing-library/react';

// Query by data-qa
const title = getByQa('applications-title');

// Query within container
const table = getByQa('applications-table-body');
within(table).getByText('Expected Text');

# Update snapshots after intentional UI changes
npm test -- -u
```

**Snapshot Tests:**
- 65 snapshot tests covering UI components, complex components, and page components
- Snapshot files are auto-generated in `__snapshots__/` directories
- Update snapshots when making intentional UI changes: `npm test -- -u`
- **CI/CD Integration**: Snapshot tests run automatically in CI for each environment (dev, test, prod) as separate jobs (`test-fe-ss-dev`, `test-fe-ss-test`, `test-fe-ss-prod`)
- Snapshot tests run in parallel with FE E2E tests and can be enabled/disabled via `enable_snapshot_tests` input

#### Cypress Tests
```bash
cd cypress
npm install  # First time only
export BASE_URL="https://www.google.com"
export ENVIRONMENT="local"
npm run cypress:run
```

#### Playwright Tests
```bash
cd playwright
npm install  # First time only
npx playwright install --with-deps chromium  # First time only
export BASE_URL="https://www.google.com"
export ENVIRONMENT="local"
export CI=true
npm test
```

#### Vibium Tests
```bash
# Using the test script (recommended)
./scripts/tests/frameworks/run-vibium-tests.sh

# Or directly
cd vibium
npm install  # First time only
npm test
```

#### Robot Framework Tests
```bash
# Install dependencies (first time only)
pip3 install --user robotframework robotframework-seleniumlibrary robotframework-requests

# Run API tests (no Grid needed)
cd tests/robot
robot --include api tests/
```

---

## 🔍 Debugging Pipeline Failures

This section helps you debug test failures in the CI/CD pipeline by running tests locally.

### What We've Set Up

#### 1. Local Test Runner Script

**File**: `./scripts/tests/run-tests-local.sh`

This script runs all test frameworks that don't require Docker:
- ✅ Frontend Unit Tests (Vitest - functional + snapshot)
- ✅ Cypress Tests (TypeScript E2E)
- ✅ Playwright Tests (TypeScript E2E)
- ✅ Robot Framework Tests (Python - API tests only)
- ⚠️ Selenium/Java Tests (skipped - require Selenium Grid)

**Usage:**
```bash
./scripts/tests/run-tests-local.sh
```

#### 2. Documentation

- **This guide** - Complete guide for running tests locally
- **Docker Testing Status** - See [Docker Guide](../infrastructure/DOCKER.md) for Docker testing options

### Debugging Workflow

#### Step 1: Run Local Tests

First, identify which tests are failing:

```bash
# Run all local tests
./scripts/tests/run-tests-local.sh

# Or run individual frameworks
cd cypress && npm run cypress:run
cd playwright && npm test
```

#### Step 2: Compare with Pipeline

The pipeline runs tests in this order (from `.github/workflows/test-environment.yml`):

1. **Cypress Tests** - E2E tests
2. **Playwright Tests** - E2E tests
3. **Robot Framework Tests** - API and Web tests
4. **Selenium/Java Tests** - TestNG suites (require Grid)

**Note**: Local testing skips Selenium/Java tests that require Selenium Grid. To test those, you'll need to:
- Start Selenium Grid locally (see [Docker Guide](../infrastructure/DOCKER.md))
- Or run tests in Docker (see [Docker Guide](../infrastructure/DOCKER.md))

#### Step 3: Fix Issues Locally

Once you've identified the failing tests:

1. **Run the specific test framework**:
   ```bash
   cd cypress && npm run cypress:run
   # or
   cd playwright && npm test
   ```

2. **Run specific test files**:
   ```bash
   # Cypress
   cd cypress && npx cypress run --spec "cypress/e2e/my-test.cy.js"
   
   # Playwright
   cd playwright && npx playwright test tests/my-test.spec.ts
   ```

3. **Debug with UI**:
   ```bash
   # Cypress UI
   cd cypress && npm run cypress:open
   
   # Playwright UI
   cd playwright && npm run test:ui
   ```

#### Step 4: Verify Fixes

After fixing issues locally:

1. **Re-run local tests** to ensure they pass
2. **Commit and push** your changes
3. **Monitor the pipeline** to verify fixes work in CI/CD

---

## 📝 Framework-Specific Instructions

### Cypress

**Prerequisites:**
- Node.js 18+
- npm or yarn

**Setup:**
```bash
cd cypress
npm install
```

**Run Tests:**
```bash
# Headless mode (CI-like)
npm run cypress:run

# Interactive mode (debugging)
npm run cypress:open
```

**Environment Variables:**
```bash
export BASE_URL="http://localhost:3003"
export ENVIRONMENT="local"
```

### Playwright

**Prerequisites:**
- Node.js 18+
- npm or yarn

**Setup:**
```bash
cd playwright
npm install
npx playwright install --with-deps chromium
```

**Run Tests:**
```bash
# Headless mode (CI-like)
npm test

# UI mode (debugging)
npm run test:ui

# Headed mode (see browser)
npm run test:headed
```

**Environment Variables:**
```bash
export BASE_URL="http://localhost:3003"
export ENVIRONMENT="local"
export CI=true
```

### Robot Framework

**Prerequisites:**
- Python 3.8+
- pip

**Setup:**
```bash
pip3 install --user robotframework robotframework-seleniumlibrary robotframework-requests
```

**Run Tests:**
```bash
# API tests (no Grid needed)
cd tests/robot
robot --include api tests/

# Web tests (requires Grid)
robot --include web tests/
```

**Note**: Web tests require Selenium Grid. See [Docker Guide](../infrastructure/DOCKER.md) for Grid setup.

### Vibium

**Prerequisites:**
- Node.js 18+
- npm or yarn

**Setup:**
```bash
cd vibium
npm install
```

**Run Tests:**
```bash
npm test
```

---

## 🐛 Troubleshooting

### Issue: Tests fail locally but pass in pipeline

**Possible Causes:**
- Environment differences (Node.js version, dependencies)
- Missing environment variables
- Port conflicts
- Database state differences

**Solutions:**
1. Check Node.js version matches CI/CD
2. Verify all environment variables are set
3. Check for port conflicts: `lsof -ti:3003`
4. Ensure database is in correct state

### Issue: Cypress/Playwright can't connect to application

**Solutions:**
1. Ensure backend and frontend are running:
   ```bash
   ./scripts/services/start-env.sh
   ```
2. Check ports match environment:
   - Dev: 8003 (backend), 3003 (frontend)
   - Test: 8004 (backend), 3004 (frontend)
3. Verify CORS settings in backend

### Issue: Robot Framework tests fail

**Solutions:**
1. Verify Python version: `python3 --version`
2. Reinstall dependencies:
   ```bash
   pip3 install --user --upgrade robotframework robotframework-seleniumlibrary robotframework-requests
   ```
3. Check test results: `tests/robot/results/`

---

## 📚 Related Documentation

- [Integration Testing Guide](./INTEGRATION_TESTING.md) - Full-stack integration tests
- [Docker Guide](../infrastructure/DOCKER.md) - Running tests in Docker
- [Test Execution Guide](./TEST_EXECUTION_GUIDE.md) - General test execution
- [CI/CD Troubleshooting Guide](../troubleshooting/CI_TROUBLESHOOTING.md) - Pipeline troubleshooting

---

**Last Updated**: 2025-12-27  
**Maintained By**: Development Team

