# Shared Test Configuration Implementation Plan

**Date**: 2026-01-11  
**Status**: 🚧 **PLANNING**  
**Purpose**: Eliminate duplication between Cypress and Playwright by creating shared configuration utilities

**Branch**: `feat/shared-test-config`  
**Target Completion**: TBD

---

## Overview

This document outlines the plan to eliminate duplication between Cypress and Playwright frameworks by creating shared configuration utilities. The analysis identified several areas where code can be consolidated.

---

## Analysis Summary

### High Priority Duplications (Should Be Shared)

1. **Environment Configuration** ⚠️ **CRITICAL**
   - **Issue**: Cypress has hardcoded `getBackendUrl()` function in `wizard.cy.ts`
   - **Current**: Duplicates backend URL logic from `config/environments.json`
   - **Solution**: Create shared config utility for Cypress (similar to Playwright's `port-config.ts`)
   - **Impact**: Eliminates hardcoded values, ensures single source of truth

2. **Base URL Environment Variable Naming**
   - **Issue**: Inconsistent naming (`CYPRESS_BASE_URL` vs `BASE_URL`)
   - **Current**: Cypress uses `CYPRESS_BASE_URL`, Playwright uses `BASE_URL`
   - **Solution**: Standardize or document the difference
   - **Impact**: Reduces confusion, improves consistency

3. **TypeScript Base Configuration**
   - **Issue**: 90% identical `tsconfig.json` files
   - **Current**: Both have nearly identical compiler options
   - **Solution**: Create base `tsconfig.base.json` that both extend
   - **Impact**: Reduces duplication, easier to maintain

### Medium Priority (Could Be Shared)

4. **Timeout Values**
   - **Issue**: Cypress sets explicit timeouts, Playwright uses defaults
   - **Solution**: Standardize timeout values or document rationale
   - **Impact**: Consistent behavior across frameworks

5. **Retry Configuration**
   - **Issue**: Same intent, different syntax
   - **Solution**: Document the difference (framework-specific)
   - **Impact**: Better understanding of framework differences

---

## Implementation Plan

### Phase 1: Shared Environment Configuration (Priority: High) 🚧

**Goal**: Create shared config utility for Cypress to use `config/environments.json`

**Tasks**:
- [ ] Create `cypress/cypress/support/config/port-config.ts` (or similar location)
  - Import from `config/environments.json`
  - Provide `getEnvironmentConfig()` function
  - Provide `getBackendUrl()` helper function
  - Match Playwright's `port-config.ts` API
- [ ] Update `cypress/cypress/e2e/wizard.cy.ts`
  - Remove hardcoded `getBackendUrl()` function
  - Import and use shared config utility
  - Use `getEnvironmentConfig()` for backend URL
- [ ] Update `cypress/cypress.config.ts` (if needed)
  - Use shared config for base URL determination
- [ ] Test changes locally
- [ ] Verify TypeScript compilation
- [ ] Update documentation

**Files to Create**:
- `cypress/cypress/support/config/port-config.ts` (or `cypress/cypress/config/port-config.ts`)

**Files to Modify**:
- `cypress/cypress/e2e/wizard.cy.ts` - Remove hardcoded function, use shared config
- `cypress/README.md` - Update documentation

**Reference**:
- `playwright/config/port-config.ts` - Reference implementation
- `config/environments.json` - Source of truth

**Status**: ⏳ **PENDING**

---

### Phase 2: TypeScript Base Configuration (Priority: Medium) 🚧

**Goal**: Create shared base TypeScript config

**Tasks**:
- [ ] Create `tsconfig.base.json` at project root
  - Extract common compiler options
  - Keep framework-specific options separate
- [ ] Update `cypress/tsconfig.json`
  - Extend base config
  - Add Cypress-specific options (types, paths)
- [ ] Update `playwright/tsconfig.json`
  - Extend base config
  - Add Playwright-specific options (types, paths)
- [ ] Test TypeScript compilation in both projects
- [ ] Verify IDE support still works

**Files to Create**:
- `tsconfig.base.json` (project root)

**Files to Modify**:
- `cypress/tsconfig.json` - Extend base config
- `playwright/tsconfig.json` - Extend base config

**Status**: ⏳ **PENDING**

---

### Phase 3: Standardize Environment Variable Naming (Priority: Medium) 🚧

**Goal**: Document or standardize base URL environment variable naming

**Tasks**:
- [ ] Review current usage of `CYPRESS_BASE_URL` vs `BASE_URL`
- [ ] Decide: Standardize on one name OR document why they differ
- [ ] If standardizing:
  - Update Cypress config to use `BASE_URL`
  - Update workflow files
  - Update documentation
- [ ] If documenting:
  - Add clear documentation explaining the difference
  - Update README files
- [ ] Test changes

**Files to Modify**:
- `cypress/cypress.config.ts` (if standardizing)
- `.github/workflows/env-fe.yml` (if standardizing)
- `cypress/README.md` - Documentation
- `docs/guides/testing/UI_TESTING_FRAMEWORKS.md` - Documentation

**Status**: ⏳ **PENDING**

---

### Phase 4: Standardize Timeout Values (Priority: Low) 🚧

**Goal**: Document or standardize timeout values

**Tasks**:
- [ ] Review timeout values in both frameworks
- [ ] Create shared timeout constants (if beneficial)
- [ ] OR document why they differ
- [ ] Update documentation

**Files to Create/Modify**:
- `cypress/cypress/support/config/timeouts.ts` (if creating shared constants)
- Documentation files

**Status**: ⏳ **PENDING**

---

## Implementation Guidelines

### Shared Config Utility Pattern

**Playwright Pattern** (Reference):
```typescript
// playwright/config/port-config.ts
import envConfig from '../../config/environments.json';

export function getEnvironmentConfig(environment: string = 'dev'): EnvironmentConfig {
  const env = (environment || 'dev').toLowerCase() as Environment;
  if (envConfig.environments && env in envConfig.environments) {
    return envConfig.environments[env];
  }
  return envConfig.environments['dev'];
}
```

**Cypress Pattern** (Target):
```typescript
// cypress/cypress/support/config/port-config.ts
import envConfig from '../../../config/environments.json';

export function getEnvironmentConfig(environment: string = 'dev'): EnvironmentConfig {
  const env = (environment || 'dev').toLowerCase() as Environment;
  if (envConfig.environments && env in envConfig.environments) {
    return envConfig.environments[env];
  }
  return envConfig.environments['dev'];
}

export function getBackendUrl(environment: string = 'dev'): string {
  const envConfig = getEnvironmentConfig(environment);
  return envConfig.backend.url;
}
```

### TypeScript Base Config Pattern

**Base Config** (`tsconfig.base.json`):
```json
{
  "compilerOptions": {
    "target": "ES2020",
    "lib": ["ES2020"],
    "module": "commonjs",
    "moduleResolution": "node",
    "strict": true,
    "esModuleInterop": true,
    "skipLibCheck": true,
    "forceConsistentCasingInFileNames": true,
    "resolveJsonModule": true,
    "baseUrl": "."
  }
}
```

**Cypress Config** (`cypress/tsconfig.json`):
```json
{
  "extends": "../tsconfig.base.json",
  "compilerOptions": {
    "types": ["node", "cypress"],
    "paths": {
      "@/*": ["./cypress/*"]
    }
  },
  "include": ["cypress/**/*.ts", "cypress.config.ts"]
}
```

**Playwright Config** (`playwright/tsconfig.json`):
```json
{
  "extends": "../tsconfig.base.json",
  "compilerOptions": {
    "types": ["node", "@playwright/test"],
    "paths": {
      "@/*": ["./tests/*"]
    }
  },
  "include": ["tests/**/*.ts", "config/**/*.ts", "../config/**/*.json"]
}
```

---

## Benefits

### Immediate Benefits
- ✅ Single source of truth for environment configuration
- ✅ Reduced code duplication
- ✅ Easier maintenance (update config in one place)
- ✅ Consistent behavior across frameworks

### Long-term Benefits
- ✅ Easier to add new test frameworks (reuse config)
- ✅ Reduced risk of configuration drift
- ✅ Better documentation of configuration
- ✅ Easier onboarding for new developers

---

## Testing Strategy

### Phase 1 Testing
- [ ] Verify Cypress tests still work with shared config
- [ ] Verify backend URL resolution works correctly
- [ ] Test in all environments (dev, test, prod)
- [ ] Verify TypeScript compilation

### Phase 2 Testing
- [ ] Verify TypeScript compilation in both projects
- [ ] Verify IDE autocomplete still works
- [ ] Test that both frameworks can still build

### Phase 3 Testing
- [ ] Verify environment variable changes don't break CI/CD
- [ ] Test locally with both variable names (if keeping both)
- [ ] Verify documentation is clear

---

## Success Criteria

1. ✅ Cypress uses shared `config/environments.json` (no hardcoded values)
2. ✅ TypeScript configs share common base configuration
3. ✅ Environment variable naming is documented or standardized
4. ✅ All tests pass in both frameworks
5. ✅ TypeScript compilation works in both projects
6. ✅ CI/CD pipeline still works
7. ✅ Documentation is updated

---

## Files to Review

### Current Duplications

**Environment Config:**
- `cypress/cypress/e2e/wizard.cy.ts` - Lines 64-79 (hardcoded `getBackendUrl()`)
- `playwright/config/port-config.ts` - Uses `config/environments.json` ✅

**TypeScript Config:**
- `cypress/tsconfig.json` - 90% identical to Playwright
- `playwright/tsconfig.json` - 90% identical to Cypress

**Base URL:**
- `cypress/cypress.config.ts` - Uses `CYPRESS_BASE_URL`
- `playwright/playwright.config.ts` - Uses `BASE_URL`

---

## References

- **Playwright Config**: `playwright/config/port-config.ts`
- **Environment Config**: `config/environments.json`
- **Cypress Config**: `cypress/cypress.config.ts`
- **Playwright Config**: `playwright/playwright.config.ts`
- **Cypress Wizard Test**: `cypress/cypress/e2e/wizard.cy.ts`
- **Playwright Wizard Test**: `playwright/tests/wizard.spec.ts`

---

**Last Updated**: 2026-01-11  
**Document Status**: 🚧 **PLANNING** - Ready for implementation
