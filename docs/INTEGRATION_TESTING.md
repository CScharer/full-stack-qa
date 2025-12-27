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
3. Start backend server (port 8004 for test environment)
4. Start frontend dev server (port 3004 for test environment)
5. Run Playwright integration tests
6. Clean up servers after tests

**Note**: Integration tests use the **test environment** (ports 8004/3004) by default. See [Port Configuration Guide](./guides/infrastructure/PORT_CONFIGURATION.md) for all port assignments.

---

## 📝 Manual Setup

If you prefer to run tests manually:

### 1. Ensure Test Database Exists

Integration tests use the **test environment database** (`full_stack_qa_test.db`):

```bash
# Test database should exist at:
Data/Core/full_stack_qa_test.db

# If not, create it from schema:
mkdir -p Data/Core
sqlite3 Data/Core/full_stack_qa_test.db < docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql
sqlite3 Data/Core/full_stack_qa_test.db < docs/new_app/DELETE_TRIGGERS.sql
```

**Note**: The integration test script automatically sets `ENVIRONMENT=test`, which makes the backend use `full_stack_qa_test.db`.

### 2. Start Backend Server

```bash
cd backend
source venv/bin/activate
# Set environment to test (uses full_stack_qa_test.db and port 8004)
export ENVIRONMENT=test
export API_HOST=0.0.0.0
export API_PORT=8004
export CORS_ORIGINS=http://127.0.0.1:3004,http://localhost:3004
python -m uvicorn app.main:app --host 0.0.0.0 --port 8004
```

**OR using the helper script**:
```bash
./scripts/start-be.sh --env test
```

**Note**: Setting `ENVIRONMENT=test` automatically makes the backend use `full_stack_qa_test.db` and port 8004. See [Service Scripts Guide](./guides/infrastructure/SERVICE_SCRIPTS.md) for more options.

### 3. Start Frontend Server

```bash
cd frontend
export NEXT_PUBLIC_API_URL=http://localhost:8004/api/v1
PORT=3004 npm run dev
```

**OR using the helper script**:
```bash
./scripts/start-fe.sh --env test
```

**Note**: Test environment uses ports 8004 (backend) and 3004 (frontend). See [Port Configuration Guide](./guides/infrastructure/PORT_CONFIGURATION.md) for all port assignments.

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
- Starts backend server on port 8004 (test environment)
- Starts frontend dev server on port 3004 (test environment)
- Waits for both servers to be ready
- Runs tests against the running application
- Cleans up servers after tests

**Note**: Integration tests use the **test environment** (ports 8004/3004). See [Port Configuration Guide](./guides/infrastructure/PORT_CONFIGURATION.md) for all port assignments.

### Environment Variables

You can override defaults:

```bash
# Frontend URL (test environment)
export FRONTEND_URL=http://127.0.0.1:3004

# Backend URL (used by frontend - test environment)
export NEXT_PUBLIC_API_URL=http://localhost:8004/api/v1

# Set environment to test (uses full_stack_qa_test.db and ports 8004/3004)
export ENVIRONMENT=test
```

---

## 🐛 Troubleshooting

### Issue: Backend server fails to start

**Solution:**
```bash
# Check if port 8004 is in use (test environment)
lsof -ti:8004 | xargs kill -9

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
# Check if port 3003 is in use
lsof -ti:3003 | xargs kill -9

# Verify frontend dependencies
cd frontend
npm install --legacy-peer-deps
```

### Issue: Tests fail with connection errors

**Solution:**
- Ensure both servers are running
- Check that `NEXT_PUBLIC_API_URL` is set correctly
- Verify CORS is configured in backend (should allow `http://127.0.0.1:3003` and `http://localhost:3003`)

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

- [Local Development Guide](./LOCAL_DEVELOPMENT.md) - How to run the application locally
- [Backend API Tests](../backend/tests/) - Unit tests for backend API
- [Frontend Unit Tests](../frontend/__tests__/) - Unit tests for frontend components
