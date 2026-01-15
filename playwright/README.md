# Playwright Tests (TypeScript)

This directory contains Playwright end-to-end tests written in TypeScript for the full-stack-qa framework.

## Prerequisites

- Node.js >= 18.0.0
- npm >= 9.0.0

## Installation

```bash
cd playwright
npm install
npx playwright install
```

## Running Tests

### All Browsers
```bash
npm test
```

### Specific Browser
```bash
npm run test:chrome
npm run test:firefox
npm run test:webkit
```

### Specific Test File
```bash
# Run a specific test file with Chrome
npx playwright test wizard.spec.ts --project=chromium

# Or using npm script
npm run test:chrome -- wizard.spec.ts

# Run a specific test file in headed mode (see browser)
npx playwright test wizard.spec.ts --project=chromium --headed

# Run a specific test file in UI mode (interactive)
npx playwright test wizard.spec.ts --project=chromium --ui
```

### Headed Mode (See Browser)
```bash
npm run test:headed
```

### UI Mode (Interactive)
```bash
npm run test:ui
```

### Debug Mode
```bash
npm run test:debug
```

## Test Structure

- `tests/` - Test files (.spec.ts)
- `tests/pages/` - Page Object Model classes
- `helpers/` - Helper utilities
  - `api-utils.ts` - API request utility adapter
  - `db-utils.ts` - Database query utility adapter
- `playwright.config.ts` - Configuration

## Configuration

Edit `playwright.config.ts` to modify:
- Base URL
- Timeouts
- Screenshot/video settings
- Browser projects
- Parallel execution

## Test Name Consistency

Test suite and test case names are centralized in `lib/test-utils.json` to ensure consistency across frameworks. Playwright tests use `lib/test-utils.ts` which reads from this JSON file.

**Usage:**
```typescript
import { getTestSuite } from '../../lib/test-utils';

const wizard = getTestSuite('wizard');
test.describe(wizard.suiteName, () => {
  test(wizard.tests.test_home, async ({ page }) => {
    // test implementation
  });
});
```

This ensures test names are consistent between Cypress and Playwright implementations.

## Reports

After running tests, view HTML report:
```bash
npm run test:report
```

