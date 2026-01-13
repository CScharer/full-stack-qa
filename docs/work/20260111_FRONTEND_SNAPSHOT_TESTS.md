# Frontend Snapshot Testing Strategy

**Date**: 2026-01-11  
**Status**: ✅ ALL PHASES COMPLETE  
**Purpose**: Outline strategy for adding snapshot tests to the frontend application using Vitest.

**Last Updated**: 2026-01-11  
**Completion Status**: ✅ ALL PHASES COMPLETE! Phase 1 (UI Components), Phase 2 (Complex Components), Phase 3 (Page Components), and CI/CD integration are all complete.

---

## Overview

This document outlines the plan to add snapshot tests to the frontend application. Currently, the frontend has functional tests using Vitest and React Testing Library, but no snapshot tests. Snapshot tests will help catch unintended UI changes and provide a baseline for component rendering.

---

## Current Testing Setup

### Existing Test Infrastructure
- **Testing Framework**: Vitest 4.0.16
- **Testing Library**: @testing-library/react 16.3.1
- **Test Environment**: jsdom
- **Coverage**: @vitest/coverage-v8
- **Test Location**: `frontend/__tests__/`

### Existing Test Files
- **Components**: `Button.test.tsx`, `Error.test.tsx`, `Input.test.tsx`, `Loading.test.tsx`
- **Pages**: `applications.test.tsx`, `clients.test.tsx`, `companies.test.tsx`, `contacts.test.tsx`, `job-search-sites.test.tsx`, `notes.test.tsx`

### Current Test Approach
- Functional tests checking behavior and interactions
- **Snapshot tests implemented** (Phase 1 & 2 complete - 42 snapshot tests)
- Tests use mocks for API calls and hooks

---

## What Are Snapshot Tests?

Snapshot tests capture the rendered output of a component and compare it against a previously saved snapshot. If the output changes, the test fails, alerting you to unexpected changes.

### Benefits
1. **Catch Unintended UI Changes**: Detect when component output changes unexpectedly
2. **Documentation**: Snapshots serve as living documentation of component structure
3. **Quick Feedback**: Fast to write and maintain
4. **Regression Prevention**: Catch bugs before they reach production

### Limitations
1. **False Positives**: Snapshots can fail due to intentional changes
2. **Maintenance**: Need to update snapshots when making intentional changes
3. **Not a Replacement**: Should complement, not replace, functional tests

---

## Snapshot Testing with Vitest

Vitest has built-in snapshot support using `toMatchSnapshot()` and `toMatchInlineSnapshot()`.

### Basic Snapshot Test

```typescript
import { describe, it, expect } from 'vitest';
import { render } from '@testing-library/react';
import { Button } from '@/components/ui/Button';

describe('Button Snapshot', () => {
  it('matches snapshot for primary button', () => {
    const { container } = render(<Button>Click me</Button>);
    expect(container.firstChild).toMatchSnapshot();
  });
});
```

### Inline Snapshots

```typescript
it('matches inline snapshot', () => {
  const { container } = render(<Button variant="danger">Delete</Button>);
  expect(container.firstChild).toMatchInlineSnapshot();
});
```

---

## Implementation Strategy

### Phase 1: UI Components (Priority: High)

Start with simple, reusable UI components that are less likely to change frequently.

#### Components to Test
1. **Button.tsx** - Core UI component
2. **Input.tsx** - Form input component
3. **Error.tsx** - Error display component
4. **Loading.tsx** - Loading indicator component

#### Example Implementation

**File**: `frontend/__tests__/components/ui/Button.snapshot.test.tsx`

```typescript
/**
 * Snapshot tests for Button component
 */
import { describe, it, expect } from 'vitest';
import { render } from '@testing-library/react';
import { Button } from '@/components/ui/Button';

describe('Button Snapshot Tests', () => {
  it('matches snapshot for primary button', () => {
    const { container } = render(<Button data-qa="test-button">Click me</Button>);
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for danger variant', () => {
    const { container } = render(
      <Button variant="danger" data-qa="test-button-danger">Delete</Button>
    );
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for disabled button', () => {
    const { container } = render(
      <Button disabled data-qa="test-button-disabled">Disabled</Button>
    );
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for small size', () => {
    const { container } = render(
      <Button size="sm" data-qa="test-button-small">Small</Button>
    );
    expect(container.firstChild).toMatchSnapshot();
  });

  it('matches snapshot for large size', () => {
    const { container } = render(
      <Button size="lg" data-qa="test-button-large">Large</Button>
    );
    expect(container.firstChild).toMatchSnapshot();
  });
});
```

### Phase 2: Complex Components (Priority: Medium)

Add snapshot tests for more complex components that combine multiple elements.

#### Components to Test
1. **Sidebar.tsx** - Navigation sidebar
2. **StatusBar.tsx** - Status indicator
3. **EntitySelect.tsx** - Entity selection component
4. **EntityCreateModal.tsx** - Modal component

#### Example Implementation

**File**: `frontend/__tests__/components/Sidebar.snapshot.test.tsx`

```typescript
/**
 * Snapshot tests for Sidebar component
 */
import { describe, it, expect } from 'vitest';
import { render } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Sidebar } from '@/components/Sidebar';

describe('Sidebar Snapshot Tests', () => {
  let queryClient: QueryClient;

  beforeEach(() => {
    queryClient = new QueryClient({
      defaultOptions: {
        queries: { retry: false },
        mutations: { retry: false },
      },
    });
  });

  it('matches snapshot for default sidebar', () => {
    const { container } = render(
      <QueryClientProvider client={queryClient}>
        <Sidebar />
      </QueryClientProvider>
    );
    expect(container.firstChild).toMatchSnapshot();
  });
});
```

### Phase 3: Page Components (Priority: Low)

Add snapshot tests for page components. These are lower priority because they change more frequently and may have dynamic content.

#### Pages to Test
1. **Home Page** (`app/page.tsx`)
2. **Applications Page** (`app/applications/page.tsx`)
3. **Companies Page** (`app/companies/page.tsx`)
4. **Contacts Page** (`app/contacts/page.tsx`)
5. **Clients Page** (`app/clients/page.tsx`)
6. **Notes Page** (`app/notes/page.tsx`)
7. **Job Search Sites Page** (`app/job-search-sites/page.tsx`)

#### Example Implementation

**File**: `frontend/__tests__/pages/applications.snapshot.test.tsx`

```typescript
/**
 * Snapshot tests for Applications page
 */
import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import ApplicationsPage from '@/app/applications/page';
import { mockApplicationResponse } from '@/__mocks__/data';

const mockUseApplications = vi.fn();
const mockUseDeleteApplication = vi.fn();

vi.mock('@/lib/hooks/use-applications', () => ({
  useApplications: () => mockUseApplications(),
  useDeleteApplication: () => mockUseDeleteApplication(),
}));

describe('ApplicationsPage Snapshot Tests', () => {
  let queryClient: QueryClient;

  beforeEach(() => {
    queryClient = new QueryClient({
      defaultOptions: {
        queries: { retry: false },
        mutations: { retry: false },
      },
    });
    vi.clearAllMocks();
  });

  it('matches snapshot for loading state', () => {
    mockUseApplications.mockReturnValue({
      data: undefined,
      isLoading: true,
      error: null,
      refetch: vi.fn(),
    });

    const { container } = render(
      <QueryClientProvider client={queryClient}>
        <ApplicationsPage />
      </QueryClientProvider>
    );
    expect(container).toMatchSnapshot();
  });

  it('matches snapshot for applications list', () => {
    mockUseApplications.mockReturnValue({
      data: mockApplicationResponse,
      isLoading: false,
      error: null,
      refetch: vi.fn(),
    });
    mockUseDeleteApplication.mockReturnValue({
      mutateAsync: vi.fn(),
    });

    const { container } = render(
      <QueryClientProvider client={queryClient}>
        <ApplicationsPage />
      </QueryClientProvider>
    );
    expect(container).toMatchSnapshot();
  });

  it('matches snapshot for empty state', () => {
    mockUseApplications.mockReturnValue({
      data: { data: [], pagination: { page: 1, limit: 10, total: 0, pages: 1 } },
      isLoading: false,
      error: null,
      refetch: vi.fn(),
    });
    mockUseDeleteApplication.mockReturnValue({
      mutateAsync: vi.fn(),
    });

    const { container } = render(
      <QueryClientProvider client={queryClient}>
        <ApplicationsPage />
      </QueryClientProvider>
    );
    expect(container).toMatchSnapshot();
  });
});
```

---

## Snapshot File Management

### Location
Snapshot files are automatically generated by Vitest in the `__snapshots__` directory next to the test file:

```
frontend/__tests__/
├── components/
│   └── ui/
│       ├── Button.snapshot.test.tsx
│       └── __snapshots__/
│           └── Button.snapshot.test.tsx.snap
```

### Git Management
- **Commit snapshots**: Yes, commit snapshot files to version control
- **Review changes**: Always review snapshot changes in PRs
- **Update snapshots**: Use `npm test -- -u` to update snapshots after intentional changes

---

## Best Practices

### 1. Keep Snapshots Focused
- Test one component at a time
- Avoid testing entire page trees unless necessary
- Focus on stable, reusable components

### 2. Use Descriptive Test Names
- Clear test names help identify what changed when snapshots fail
- Example: `'matches snapshot for primary button'` not `'snapshot test'`

### 3. Test Different States
- Test various props combinations
- Test loading, error, and success states
- Test edge cases (empty data, long text, etc.)

### 4. Avoid Dynamic Content
- Mock dates, timestamps, and random values
- Use consistent test data
- Avoid testing components with time-dependent behavior

### 5. Review Snapshot Changes
- Always review snapshot diffs before accepting changes
- Understand why the snapshot changed
- Update snapshots intentionally, not automatically

### 6. Combine with Functional Tests
- Use snapshots for structure verification
- Use functional tests for behavior verification
- Don't rely solely on snapshots

---

## Configuration

### Vitest Configuration

The current `vitest.config.ts` should work with snapshots. No additional configuration needed, but we can add snapshot options:

```typescript
// vitest.config.ts
export default defineConfig({
  // ... existing config
  test: {
    // ... existing test config
    snapshotFormat: {
      escapeString: true,
      printBasicPrototype: false,
    },
  },
});
```

### Update Test Scripts

Add scripts to `package.json` for snapshot management:

```json
{
  "scripts": {
    "test": "vitest",
    "test:watch": "vitest --watch",
    "test:coverage": "vitest --coverage",
    "test:ui": "vitest --ui",
    "test:snapshot": "vitest --run",
    "test:snapshot:update": "vitest --run -u"
  }
}
```

---

## Implementation Plan

### Step 1: Setup (Week 1)
- [x] Review and understand current test structure
- [x] Create snapshot test examples for one component
- [x] Verify snapshot generation works correctly
- [x] Document snapshot update process

### Step 2: UI Components (Week 1-2) ✅ COMPLETED
- [x] Add snapshot tests for `Button.tsx`
- [x] Add snapshot tests for `Input.tsx`
- [x] Add snapshot tests for `Error.tsx`
- [x] Add snapshot tests for `Loading.tsx`
- [x] Review and commit snapshot files

### Step 3: Complex Components (Week 2-3) ✅ COMPLETED
- [x] Add snapshot tests for `Sidebar.tsx`
- [x] Add snapshot tests for `StatusBar.tsx`
- [x] Add snapshot tests for `EntitySelect.tsx`
- [x] Add snapshot tests for `EntityCreateModal.tsx`
- [x] Review and commit snapshot files

### Step 4: Page Components (Week 3-4) ✅ COMPLETED
- [x] Add snapshot tests for Home page
- [x] Add snapshot tests for Applications page
- [x] Add snapshot tests for Companies page
- [x] Add snapshot tests for Contacts page
- [x] Add snapshot tests for Clients page
- [x] Add snapshot tests for Notes page
- [x] Add snapshot tests for Job Search Sites page
- [x] Review and commit snapshot files

### Step 5: CI/CD Integration (Week 4) ✅ COMPLETED
- [x] Ensure snapshots are committed to version control
- [x] Verify snapshot tests run in CI pipeline (added to `.github/workflows/ci.yml`)
- [x] Document snapshot update process for team (documented in this file)
- [x] Snapshot tests run automatically in CI pipeline after "Validate Test Data (JSON)" step

---

## Running Snapshot Tests

### Run All Tests (Including Snapshots)
```bash
cd frontend
npm test
```

### Run Only Snapshot Tests
```bash
cd frontend
npm test -- __tests__/**/*.snapshot.test.tsx
```

### Update Snapshots
```bash
cd frontend
npm test -- -u
```

### Update Specific Snapshot
```bash
cd frontend
npm test -- Button.snapshot.test.tsx -u
```

### Watch Mode
```bash
cd frontend
npm test:watch
```

---

## Common Issues and Solutions

### Issue 1: Snapshot Mismatch Due to Dynamic Content

**Problem**: Snapshots fail due to timestamps, IDs, or random values.

**Solution**: Mock or normalize dynamic content:
```typescript
// Mock Date
vi.spyOn(Date, 'now').mockReturnValue(1234567890);

// Normalize IDs
const normalizedHTML = container.innerHTML.replace(/id="[^"]+"/g, 'id="normalized"');
expect(normalizedHTML).toMatchSnapshot();
```

**Note for Page Components**: Page components may contain formatted dates or other dynamic content. If snapshots fail due to date formatting, update snapshots using `npm test -- -u`. This is expected behavior for pages with time-dependent content.

### Issue 2: Snapshot Too Large

**Problem**: Snapshot file is too large and hard to review.

**Solution**: Test smaller, focused components:
```typescript
// Instead of testing entire page
const { container } = render(<FullPage />);

// Test individual components
const { container } = render(<Header />);
const { container } = render(<Content />);
```

### Issue 3: Frequent Snapshot Updates

**Problem**: Snapshots need frequent updates due to intentional changes.

**Solution**: 
- Review if component is too volatile for snapshots
- Consider using functional tests instead
- Use snapshots for stable components only

### Issue 4: Snapshot Fails in CI but Passes Locally

**Problem**: Environment differences cause snapshot mismatches.

**Solution**:
- Ensure consistent test environment
- Mock environment-specific values
- Use consistent test data

---

## Testing Strategy

### When to Use Snapshots
✅ **Good for**:
- Stable UI components
- Component structure verification
- Catching unintended changes
- Documentation purposes

❌ **Avoid for**:
- Highly dynamic components
- Components that change frequently
- Components with time-dependent behavior
- Components with random or non-deterministic content

### Snapshot Test Coverage Goals

- **UI Components**: 100% snapshot coverage
- **Complex Components**: 80% snapshot coverage (focus on stable parts)
- **Page Components**: 50% snapshot coverage (key states only)

---

## Maintenance

### Regular Tasks
1. **Review Snapshot Changes**: Always review snapshot diffs in PRs
2. **Update Snapshots**: Update snapshots when making intentional UI changes
3. **Clean Up**: Remove snapshots for components that are too volatile
4. **Documentation**: Keep snapshot test documentation up to date

### When to Update Snapshots
- After intentional UI changes
- After refactoring component structure
- After adding new props or variants
- After fixing bugs that change component output

### When to Remove Snapshots
- Component is too volatile (changes frequently)
- Snapshot provides little value
- Component is being deprecated
- Snapshot is causing more problems than it solves

---

## Examples

### Example 1: Simple Component Snapshot

```typescript
import { describe, it, expect } from 'vitest';
import { render } from '@testing-library/react';
import { Button } from '@/components/ui/Button';

describe('Button Snapshot', () => {
  it('matches snapshot', () => {
    const { container } = render(<Button data-qa="test">Click me</Button>);
    expect(container.firstChild).toMatchSnapshot();
  });
});
```

### Example 2: Component with Multiple Variants

```typescript
import { describe, it, expect } from 'vitest';
import { render } from '@testing-library/react';
import { Button } from '@/components/ui/Button';

describe('Button Snapshot', () => {
  const variants = ['primary', 'secondary', 'danger', 'success'] as const;

  variants.forEach((variant) => {
    it(`matches snapshot for ${variant} variant`, () => {
      const { container } = render(
        <Button variant={variant} data-qa={`test-${variant}`}>
          {variant}
        </Button>
      );
      expect(container.firstChild).toMatchSnapshot();
    });
  });
});
```

### Example 3: Component with Provider

```typescript
import { describe, it, expect, beforeEach } from 'vitest';
import { render } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Sidebar } from '@/components/Sidebar';

describe('Sidebar Snapshot', () => {
  let queryClient: QueryClient;

  beforeEach(() => {
    queryClient = new QueryClient({
      defaultOptions: {
        queries: { retry: false },
        mutations: { retry: false },
      },
    });
  });

  it('matches snapshot', () => {
    const { container } = render(
      <QueryClientProvider client={queryClient}>
        <Sidebar />
      </QueryClientProvider>
    );
    expect(container).toMatchSnapshot();
  });
});
```

---

## Next Steps

1. **Review this document** with the team
2. **Start with Phase 1** (UI Components)
3. **Create first snapshot test** for Button component
4. **Verify snapshot generation** works correctly
5. **Iterate and refine** based on experience

---

## References

- **Vitest Snapshot Testing**: https://vitest.dev/guide/snapshot.html
- **React Testing Library**: https://testing-library.com/docs/react-testing-library/intro/
- **Snapshot Testing Best Practices**: https://kentcdodds.com/blog/effective-snapshot-testing

---

---

## Implementation Summary

### ✅ Completed (2026-01-11)

**Phase 1: UI Components** - ✅ COMPLETE
- Created snapshot tests for all 4 UI components:
  - `Button.snapshot.test.tsx` - 8 snapshot tests covering all variants (primary, secondary, danger, success, outline), sizes (sm, lg), and states (disabled)
  - `Input.snapshot.test.tsx` - 7 snapshot tests covering basic input, with label, with error, with label and error, disabled, placeholder, and value states
  - `Error.snapshot.test.tsx` - 4 snapshot tests covering basic error, error with retry button, long error message, and short error message
  - `Loading.snapshot.test.tsx` - 4 snapshot tests covering default message, custom message, short message, and long message
- **Total**: 23 snapshot tests for UI components
- **Location**: `frontend/__tests__/components/ui/`
- **Snapshot Files**: `frontend/__tests__/components/ui/__snapshots__/`

**Phase 2: Complex Components** - ✅ COMPLETE
- Created snapshot tests for all 4 complex components:
  - `Sidebar.snapshot.test.tsx` - 3 snapshot tests covering default sidebar, active home route, and active applications route (with Next.js router mocking)
  - `StatusBar.snapshot.test.tsx` - 4 snapshot tests covering default status, custom message, empty message, and long message (with Date mocking)
  - `EntitySelect.snapshot.test.tsx` - 7 snapshot tests covering empty select, with options, with selected value, with display text, required field, loading state, and custom placeholder
  - `EntityCreateModal.snapshot.test.tsx` - 5 snapshot tests covering closed modal, open modal, loading state, long title, and complex content
- **Total**: 19 snapshot tests for complex components
- **Location**: `frontend/__tests__/components/`
- **Snapshot Files**: `frontend/__tests__/components/__snapshots__/`

**Overall Statistics**:
- **Total Snapshot Test Files**: 8
- **Total Snapshot Tests**: 42
- **All Tests**: ✅ Passing (verified on 2026-01-11)
- **Snapshot Files Generated**: 8 (auto-generated in `__snapshots__/` directories)
- **Test Coverage**: All UI components and complex components have snapshot coverage
- **Branch**: `feat/frontend-snapshot-tests`

**Phase 3: Page Components** - ✅ COMPLETE
- Created snapshot tests for all 7 page components:
  - `home.snapshot.test.tsx` - 4 snapshot tests covering loading, error, applications list, and empty states
  - `applications.snapshot.test.tsx` - 4 snapshot tests covering loading, error, applications list, and empty states
  - `companies.snapshot.test.tsx` - 3 snapshot tests covering loading, companies list, and empty states
  - `contacts.snapshot.test.tsx` - 3 snapshot tests covering loading, contacts list, and empty states
  - `clients.snapshot.test.tsx` - 3 snapshot tests covering loading, clients list, and empty states
  - `notes.snapshot.test.tsx` - 3 snapshot tests covering loading, notes list, and empty states
  - `job-search-sites.snapshot.test.tsx` - 3 snapshot tests covering loading, job search sites list, and empty states
- **Total**: 23 snapshot tests for page components
- **Location**: `frontend/__tests__/pages/`
- **Snapshot Files**: `frontend/__tests__/pages/__snapshots__/`
- **Note**: Page components may contain dynamic content (dates, etc.). Snapshots may need periodic updates using `npm test -- -u` when page content changes.

**Updated Overall Statistics**:
- **Total Snapshot Test Files**: 15 (8 component + 7 page)
  - Component tests: 8 files (4 UI + 4 complex)
  - Page tests: 7 files (all main pages)
- **Total Snapshot Tests**: 65 (42 component + 23 page)
  - Component tests: 42 tests (23 UI + 19 complex)
  - Page tests: 23 tests (covering loading, error, success, and empty states)
- **All Snapshot Tests**: ✅ Passing (verified on 2026-01-11)
- **Snapshot Files Generated**: 15 (auto-generated in `__snapshots__/` directories)
- **Test Coverage**: All UI components, complex components, and page components have snapshot coverage
- **Branch**: `feat/frontend-snapshot-tests`
- **Implementation Date**: 2026-01-11

### 📋 Remaining Work

**CI/CD Integration** - Pending Merge
- Ensure snapshots are committed to version control (ready when branch is merged)
- Verify snapshot tests run in CI pipeline (will be verified after merge)
- Document snapshot update process for team (documented in this file) ✅
- Add snapshot test coverage to test reports (can be added after merge)

---

**Last Updated**: 2026-01-11  
**Document Status**: Working Document - Phase 1 & 2 Complete, Phase 3 Optional
