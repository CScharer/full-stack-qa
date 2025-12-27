# Integration Testing Guide

**Last Updated**: 2025-12-27  
**Purpose**: Guide for running full-stack integration tests

---

## 📋 Overview

Integration tests verify that the entire application stack works together:
- **Frontend** (Next.js/React)
- **Backend** (FastAPI)
- **Database** (SQLite)

These tests use **Playwright** to simulate real user interactions with the application running end-to-end.

---

## 🎯 What Gets Tested

### Applications Integration Tests
- ✅ Navigation from home page to applications
- ✅ Creating a new application
- ✅ Viewing application details
- ✅ Editing an existing application
- ✅ Deleting an application
- ✅ Listing applications
- ✅ Handling empty state

### Companies Integration Tests
- ✅ Navigation from home page to companies
- ✅ Loading companies list page

---

## 🚀 Quick Start

### Run All Integration Tests

```bash
./scripts/run-integration-tests.sh
```

This script will:
1. Check prerequisites (Node.js, Python, database)
2. Install dependencies if needed
3. Start backend server (environment-specific port)
4. Start frontend dev server (environment-specific port)
5. Run Playwright integration tests
6. Clean up servers after tests

**Environment Support**:
- **Default**: `dev` environment (ports 8003/3003, database: `full_stack_qa_dev.db`)
- **Override**: Set `ENVIRONMENT` env var to use `test` or `prod`
  ```bash
  ENVIRONMENT=test ./scripts/run-integration-tests.sh  # Uses test environment
  ENVIRONMENT=prod ./scripts/run-integration-tests.sh  # Uses prod environment
  ```

**Note**: See [Port Configuration Guide](./guides/infrastructure/PORT_CONFIGURATION.md) for all port assignments.

---

## 📝 Manual Setup

If you prefer to run tests manually:

### 1. Ensure Environment Database Exists

Integration tests use the **environment-specific database** based on `ENVIRONMENT`:
- `dev` → `full_stack_qa_dev.db` (default)
- `test` → `full_stack_qa_test.db`
- `prod` → `full_stack_qa_prod.db`

```bash
# For dev environment (default):
Data/Core/full_stack_qa_dev.db

# For test environment:
Data/Core/full_stack_qa_test.db

# For prod environment:
Data/Core/full_stack_qa_prod.db

# Create database from schema if needed:
mkdir -p Data/Core
sqlite3 Data/Core/full_stack_qa_dev.db < docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql
sqlite3 Data/Core/full_stack_qa_dev.db < docs/new_app/DELETE_TRIGGERS.sql
```

**Note**: The integration test script defaults to `dev` environment but can be overridden with `ENVIRONMENT` env var.

### 2. Start Backend Server

```bash
cd backend
source venv/bin/activate
# Set environment (defaults to dev if not set)
export ENVIRONMENT=${ENVIRONMENT:-dev}
export API_HOST=0.0.0.0
# Ports are automatically selected based on environment (8003/8004/8005)
python -m uvicorn app.main:app --host 0.0.0.0 --port ${API_PORT:-8003}
```

**OR using the helper script** (recommended):
```bash
# Default (dev environment)
./scripts/start-be.sh

# Test environment
./scripts/start-be.sh --env test

# Prod environment
./scripts/start-be.sh --env prod
```

**Note**: Setting `ENVIRONMENT` automatically makes the backend use the correct database and port. See [Service Scripts Guide](./guides/infrastructure/SERVICE_SCRIPTS.md) for more options.

### 3. Start Frontend Server

```bash
cd frontend
# Ports are automatically selected based on environment
export NEXT_PUBLIC_API_URL=http://localhost:${API_PORT:-8003}/api/v1
PORT=${FRONTEND_PORT:-3003} npm run dev
```

**OR using the helper script** (recommended):
```bash
# Default (dev environment)
./scripts/start-fe.sh

# Test environment
./scripts/start-fe.sh --env test

# Prod environment
./scripts/start-fe.sh --env prod
```

**Note**: Ports are automatically selected based on environment. See [Port Configuration Guide](./guides/infrastructure/PORT_CONFIGURATION.md) for all port assignments.

### 4. Run Integration Tests

```bash
cd playwright
npm run test:integration
```

---

## 🧪 Running Specific Tests

### Run with UI (Interactive)

```bash
cd playwright
npm run test:integration:ui
```

### Run in Headed Mode (See Browser)

```bash
cd playwright
npm run test:integration:headed
```

### Run Specific Test File

```bash
cd playwright
npx playwright test --config=playwright.integration.config.ts tests/integration/applications.spec.ts
```

---

## 📁 Test Structure

```
playwright/
├── playwright.integration.config.ts    # Integration test configuration
├── tests/
│   └── integration/
│       ├── applications.spec.ts        # Applications CRUD tests
│       ├── companies.spec.ts          # Companies navigation tests
│       └── pages/                     # Page Object Models
│           ├── HomePage.ts
│           ├── ApplicationsPage.ts
│           ├── ApplicationFormPage.ts
│           └── ApplicationDetailPage.ts
```

---

## 🔧 Configuration

### Playwright Integration Config

The integration test configuration (`playwright.integration.config.ts`) automatically:
- Starts backend server on environment-specific port (defaults to dev: 8003)
- Starts frontend dev server on environment-specific port (defaults to dev: 3003)
- Waits for both servers to be ready
- Runs tests against the running application
- Cleans up servers after tests

**Environment Support**:
- **Default**: `dev` environment (ports 8003/3003)
- **Override**: Set `ENVIRONMENT` env var to use `test` or `prod`
  ```bash
  ENVIRONMENT=test npm run test:integration  # Uses test environment (8004/3004)
  ENVIRONMENT=prod npm run test:integration  # Uses prod environment (8005/3005)
  ```

**Note**: See [Port Configuration Guide](./guides/infrastructure/PORT_CONFIGURATION.md) for all port assignments.

### Environment Variables

You can override defaults:

```bash
# Set environment (defaults to dev)
export ENVIRONMENT=dev  # or test, prod

# Frontend URL (optional - auto-selected based on ENVIRONMENT)
export FRONTEND_URL=http://127.0.0.1:3003  # dev: 3003, test: 3004, prod: 3005

# Backend URL (optional - auto-selected based on ENVIRONMENT)
export NEXT_PUBLIC_API_URL=http://localhost:8003/api/v1  # dev: 8003, test: 8004, prod: 8005
```

**Note**: Configuration (ports, database, API paths, timeouts, CORS) is automatically selected from `config/environments.json` based on `ENVIRONMENT`. You typically only need to set `ENVIRONMENT`.

---

## 🐛 Troubleshooting

### Issue: Backend server fails to start

**Solution:**
```bash
# Check if port is in use (check your environment's port)
# dev: 8003, test: 8004, prod: 8005
lsof -ti:8003 | xargs kill -9  # dev environment
# or
lsof -ti:8004 | xargs kill -9  # test environment

# Verify test database exists
ls -la Data/Core/full_stack_qa_test.db

# Check backend dependencies
cd backend
source venv/bin/activate
pip install -r requirements.txt
```

### Issue: Frontend server fails to start

**Solution:**
```bash
# Check if port is in use (check your environment's port)
# dev: 3003, test: 3004, prod: 3005
lsof -ti:3003 | xargs kill -9  # dev environment
# or
lsof -ti:3004 | xargs kill -9  # test environment

# Verify frontend dependencies
cd frontend
npm install --legacy-peer-deps
```

### Issue: Tests fail with connection errors

**Solution:**
- Ensure both servers are running
- Check that `NEXT_PUBLIC_API_URL` is set correctly
- Verify CORS is configured in backend (should allow environment-specific ports: dev=3003, test=3004, prod=3005)

### Issue: Database locked errors

**Solution:**
- Ensure only one test process is running at a time
- Integration tests run sequentially (not in parallel) to avoid conflicts
- Close any database connections from other processes

---

## 📊 Test Reports

After running tests, view the HTML report:

```bash
cd playwright
npx playwright show-report playwright-report-integration
```

---

## 🎯 Adding New Integration Tests

### 1. Create Page Object Model

Create a new file in `playwright/tests/integration/pages/`:

```typescript
import { Page, Locator } from '@playwright/test';

export class MyPage {
  readonly page: Page;
  readonly title: Locator;

  constructor(page: Page) {
    this.page = page;
    this.title = page.locator('h1');
  }

  async navigate() {
    await this.page.goto('/my-page');
  }
}
```

### 2. Create Test File

Create a new file in `playwright/tests/integration/`:

```typescript
import { test, expect } from '@playwright/test';
import { MyPage } from './pages/MyPage';

test.describe('My Feature Integration Tests', () => {
  test('should do something', async ({ page }) => {
    const myPage = new MyPage(page);
    await myPage.navigate();
    await expect(myPage.title).toBeVisible();
  });
});
```

### 3. Run Your Tests

```bash
cd playwright
npx playwright test --config=playwright.integration.config.ts tests/integration/my-feature.spec.ts
```

---

## ✅ Best Practices

1. **Use Page Object Models**: Encapsulate page interactions in reusable classes
2. **Test Real User Flows**: Test complete workflows, not just individual actions
3. **Clean Up**: Tests should clean up created data (or use test database)
4. **Wait for Elements**: Use Playwright's auto-waiting, but add explicit waits when needed
5. **Isolate Tests**: Each test should be independent and not rely on other tests
6. **Use Descriptive Names**: Test names should clearly describe what they're testing

---

## 📚 Related Documentation

- [Local Development Guide](../setup/LOCAL_DEVELOPMENT.md) - How to run the application locally
- [Backend API Tests](../backend/tests/) - Unit tests for backend API
- [Frontend Unit Tests](../frontend/__tests__/) - Unit tests for frontend components
