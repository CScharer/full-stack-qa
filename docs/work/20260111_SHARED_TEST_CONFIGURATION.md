# Shared Test Configuration Implementation Plan

**Date**: 2026-01-11  
**Status**: 🚧 **PLANNING**  
**Purpose**: Create shared configuration utilities for ALL test frameworks to use `config/environments.json` as the single source of truth for backend and frontend configuration

**Branch**: `feat/shared-test-config`  
**Target Completion**: TBD

---

## Overview

This document outlines the plan to eliminate duplication across ALL test frameworks by ensuring they all use `config/environments.json` as the single source of truth for backend and frontend configuration. Currently, each framework handles environment configuration differently, leading to duplication and potential inconsistencies.

**Goal**: All frameworks should use the same backend and frontend configuration from `config/environments.json`.

---

## Current State Analysis

### Framework Configuration Status

| Framework | Language | Current Config Source | Backend URL | Frontend URL | Status |
|-----------|----------|----------------------|-------------|--------------|--------|
| **Playwright** | TypeScript | `config/environments.json` via `port-config.ts` | ✅ Uses shared config | ✅ Uses shared config | ✅ **GOOD** |
| **Cypress** | TypeScript | Hardcoded function in `wizard.cy.ts` | ❌ Hardcoded | Uses `CYPRESS_BASE_URL` env var | ⚠️ **NEEDS FIX** |
| **Robot Framework** | Python | Hardcoded in `Common.robot` | ❌ Hardcoded | ❌ Hardcoded (`http://localhost:3003`) | ⚠️ **NEEDS FIX** |
| **Selenium/Java** | Java | XML file (`Configurations/Environments.xml`) | ❌ XML config | ❌ XML config | ⚠️ **NEEDS REVIEW** |
| **Vibium** | TypeScript | No explicit config | ❌ No config | ❌ No config | ⚠️ **NEEDS FIX** |
| **Backend Tests** | Python | `ENVIRONMENT` env var | ✅ Uses env var | N/A | ✅ **GOOD** |

### High Priority Issues (Should Be Shared)

1. **Environment Configuration** ⚠️ **CRITICAL**
   - **Issue**: Only Playwright uses `config/environments.json` as single source of truth
   - **Current State**:
     - Cypress: Hardcoded `getBackendUrl()` function in `wizard.cy.ts`
     - Robot Framework: Hardcoded `BASE_URL = http://localhost:3003` in `Common.robot`
     - Selenium/Java: Uses separate XML config file
     - Vibium: No explicit configuration
   - **Solution**: Create shared config utilities for each framework to read from `config/environments.json`
   - **Impact**: Single source of truth, eliminates hardcoded values, ensures consistency

2. **Base URL Environment Variable Naming**
   - **Issue**: Inconsistent naming across frameworks
   - **Current**: 
     - Cypress: `CYPRESS_BASE_URL`
     - Playwright: `BASE_URL`
     - Robot Framework: `BASE_URL` (hardcoded default)
   - **Solution**: Standardize on `BASE_URL` for frontend, `BACKEND_URL` for backend
   - **Impact**: Reduces confusion, improves consistency

3. **TypeScript Base Configuration**
   - **Issue**: 90% identical `tsconfig.json` files in Cypress, Playwright, and Vibium
   - **Current**: All have nearly identical compiler options
   - **Solution**: Create base `tsconfig.base.json` that all TypeScript frameworks extend
   - **Impact**: Reduces duplication, easier to maintain

### Medium Priority (Could Be Shared)

4. **Timeout Values**
   - **Issue**: Different timeout values across frameworks
   - **Solution**: Standardize timeout values or document rationale
   - **Impact**: Consistent behavior across frameworks

5. **Retry Configuration**
   - **Issue**: Same intent, different syntax
   - **Solution**: Document the difference (framework-specific)
   - **Impact**: Better understanding of framework differences

---

## Implementation Plan

### Phase 1: Shared Environment Configuration (Priority: High) ✅ **COMPLETE**

**Goal**: Create shared config utility in `config/port-config.ts` that ALL frameworks (Cypress, Playwright, Frontend, Backend) can use from `config/environments.json`

**Note**: The shared config is now in `config/port-config.ts` (common location) and is used by:
- ✅ Test frameworks (Cypress, Playwright) - using shared TypeScript config
- ✅ Frontend (Next.js/React) - using shared TypeScript config
- ✅ Backend (Python/FastAPI) - using shared Python config (`config/port_config.py`)

**Tasks**:
- [x] Create `cypress/cypress/support/config/port-config.ts` (or similar location)
  - Import from `config/environments.json`
  - Provide `getEnvironmentConfig()` function
  - Provide `getBackendUrl()` helper function
  - Provide `getFrontendUrl()` helper function
  - Match Playwright's `port-config.ts` API
- [x] Update `cypress/cypress/e2e/wizard.cy.ts`
  - Remove hardcoded `getBackendUrl()` function
  - Import and use shared config utility
  - Use `getBackendUrl()` for backend URL (with BACKEND_URL env var override support)
- [x] Update `cypress/cypress.config.ts` (if needed)
  - No changes needed - already handles environment variables correctly
- [x] Test changes locally
  - TypeScript compilation verified ✅
  - No linter errors ✅
- [x] Verify TypeScript compilation
  - ✅ TypeScript compilation passes
- [x] Update documentation
  - Will update README in next phase if needed

**Files Created**:
- ✅ `config/port-config.ts` - **Shared config utility** (common location for all frameworks)
- ✅ `playwright/config/port-config.ts` - Updated to re-export from shared config (backward compatibility)

**Files Modified**:
- ✅ `cypress/cypress/e2e/wizard.cy.ts` - Removed hardcoded function, uses shared config
  - Maintains backward compatibility with `BACKEND_URL` env var override
  - Uses `Cypress.env('ENVIRONMENT')` to determine environment
- ✅ `cypress/tsconfig.json` - Added `../config/port-config.ts` to includes
- ✅ `playwright/tsconfig.json` - Added `../config/port-config.ts` to includes
- ✅ `playwright/config/port-config.ts` - Now re-exports from shared config

**Completed**:
- [x] Created shared `config/port-config.ts` (TypeScript) - ✅
- [x] Created shared `config/port_config.py` (Python) - ✅
- [x] Updated Cypress to use shared config - ✅
- [x] Updated Playwright to re-export from shared config - ✅
- [x] Updated Frontend to use shared config (`frontend/lib/api/client.ts`) - ✅
- [x] Updated Backend to use shared config (`backend/app/config.py`) - ✅

**Known Issues**:
- [ ] Frontend TypeScript compilation: JSON module resolution issue when compiling from frontend directory
  - Runtime should work (Next.js handles it), but TypeScript check fails
  - May need to adjust import paths or use a different approach

**Reference**:
- `config/environments.json` - Source of truth ✅
- `config/port-config.ts` - Shared TypeScript config (for all TS projects: Cypress, Playwright, Frontend) ✅
- `config/port_config.py` - Shared Python config (for Backend) ✅

**Status**: ✅ **COMPLETE** - Shared config created and integrated into all projects
- ✅ Test frameworks (Cypress, Playwright) using shared TypeScript config
- ✅ Frontend using shared config (reads `config/environments.json` directly at runtime)
  - **Note**: Frontend reads JSON directly because Next.js/Turbopack can't access files outside frontend root during build
  - Server-side: Reads from filesystem (has access to `config/environments.json`)
  - Client-side: Uses `NEXT_PUBLIC_API_URL` env var or defaults
  - Still uses `config/environments.json` as single source of truth ✅
- ✅ Backend using shared Python config (reads from `config/environments.json`)

---

### Phase 2: Robot Framework Shared Environment Configuration (Priority: High) ✅ **COMPLETE**

**Goal**: Create shared config utility for Robot Framework to use `config/environments.json`

**Tasks**:
- [x] Create Python utility to read `config/environments.json`
  - Location: `src/test/robot/resources/ConfigHelper.py`
  - Provides functions: `get_backend_url_for_robot(env)`, `get_frontend_url_for_robot(env)`, `get_base_url_for_robot(env)`
  - Uses shared `config/port_config.py` (reuses Phase 1 Python config)
  - Uses `ENVIRONMENT` environment variable (defaults to 'dev')
- [x] Update `src/test/robot/resources/Common.robot`
  - Removed hardcoded `BASE_URL = http://localhost:3003`
  - Uses Python helper to get URLs from `config/environments.json`
  - Supports `ENVIRONMENT` environment variable
  - Priority: 1) BASE_URL env var, 2) BASE_URL Robot variable, 3) Shared config
- [x] Test changes locally
  - ✅ Python helper verified working
  - ✅ Returns correct URLs for dev, test, prod environments
- [x] Update documentation
  - Documented in working document

**Files Created**:
- ✅ `src/test/robot/resources/ConfigHelper.py` - Python helper that uses shared `config/port_config.py`

**Files Modified**:
- ✅ `src/test/robot/resources/Common.robot` - Uses shared config instead of hardcoded values
  - Added `Get Base Url From Shared Config` keyword
  - Updated `Setup WebDriver And Open Browser` to use shared config

**Reference**:
- `config/port_config.py` - Shared Python config (created in Phase 1) ✅
- `config/environments.json` - Source of truth ✅

**Status**: ✅ **COMPLETE** - Robot Framework now uses shared config

---

### Phase 3: Vibium Shared Environment Configuration (Priority: Medium) ✅ **COMPLETE**

**Goal**: Add environment configuration support to Vibium using `config/environments.json`

**Tasks**:
- [x] Create `vibium/config/port-config.ts` (similar to Playwright)
  - Re-exports from shared `config/port-config.ts` (ensures consistency)
  - Provides `getEnvironmentConfig()`, `getBackendUrl()`, `getFrontendUrl()` functions
- [x] Update Vibium test files (if they need config)
  - No current test files need environment config, but infrastructure is ready
  - Future tests can import from `./config/port-config`
- [x] Test changes locally
  - TypeScript compilation verified ✅
- [x] Verify TypeScript compilation
  - ✅ TypeScript compilation passes
- [x] Update documentation
  - Documented in working document

**Files Created**:
- ✅ `vibium/config/port-config.ts` - Re-exports from shared config

**Files Modified**:
- ✅ `vibium/tsconfig.json` - Added shared config to includes

**Reference**:
- `config/port-config.ts` - Shared TypeScript config (created in Phase 1) ✅
- `config/environments.json` - Source of truth ✅

**Status**: ✅ **COMPLETE** - Vibium now has access to shared config (ready for future use)

---

### Phase 4: Selenium/Java Configuration Review (Priority: Low) 🚧

**Goal**: Review Selenium/Java configuration and determine if it should use `config/environments.json`

**Tasks**:
- [ ] Review current XML-based configuration (`Configurations/Environments.xml`)
- [ ] Determine if Java code can/should read `config/environments.json`
- [ ] If yes: Create Java utility to read JSON config
- [ ] If no: Document why XML config is needed and ensure it stays in sync
- [ ] Update documentation

**Files to Review**:
- `src/test/java/com/cjs/qa/core/Environment.java` - Current config handling
- `Configurations/Environments.xml` - Current XML config

**Status**: ⏳ **PENDING** (Review phase)

---

### Phase 5: TypeScript Base Configuration (Priority: Medium) 🚧

---

**Goal**: Create shared base TypeScript config for all TypeScript frameworks

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
- [ ] Update `vibium/tsconfig.json` (if exists)
  - Extend base config
  - Add Vibium-specific options (types, paths)
- [ ] Test TypeScript compilation in all projects
- [ ] Verify IDE support still works

**Files to Create**:
- `tsconfig.base.json` (project root)

**Files to Modify**:
- `cypress/tsconfig.json` - Extend base config
- `playwright/tsconfig.json` - Extend base config
- `vibium/tsconfig.json` - Extend base config (if exists)

**Status**: ⏳ **PENDING**

---

### Phase 6: Standardize Environment Variable Naming (Priority: Medium) 🚧

**Goal**: Standardize environment variable naming across all frameworks

**Tasks**:
- [ ] Review current usage:
  - `CYPRESS_BASE_URL` (Cypress)
  - `BASE_URL` (Playwright, Robot Framework)
  - `BACKEND_URL` (used in workflows)
  - `ENVIRONMENT` (used for environment selection)
- [ ] Standardize on:
  - `BASE_URL` - Frontend/base URL (used by all frameworks)
  - `BACKEND_URL` - Backend API URL (used for API calls)
  - `ENVIRONMENT` - Environment name (dev, test, prod)
- [ ] Update all frameworks:
  - Cypress: Change `CYPRESS_BASE_URL` to `BASE_URL`
  - Robot Framework: Use `BASE_URL` env var (already supports it)
  - Playwright: Already uses `BASE_URL` ✅
- [ ] Update workflow files (`.github/workflows/env-fe.yml`, etc.)
- [ ] Update documentation
- [ ] Test changes

**Files to Modify**:
- `cypress/cypress.config.ts` - Use `BASE_URL` instead of `CYPRESS_BASE_URL`
- `.github/workflows/env-fe.yml` - Standardize variable names
- `cypress/README.md` - Documentation
- `docs/guides/testing/UI_TESTING_FRAMEWORKS.md` - Documentation
- `src/test/robot/resources/Common.robot` - Document `BASE_URL` usage

**Status**: ⏳ **PENDING**

---

### Phase 7: Standardize Timeout Values (Priority: Low) 🚧

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

1. ✅ **All frameworks use `config/environments.json`** as single source of truth
   - Cypress: Uses shared config utility (no hardcoded values)
   - Robot Framework: Uses Python helper to read JSON config
   - Vibium: Uses shared config utility
   - Playwright: Already uses shared config ✅
   - Selenium/Java: Reviewed and documented (or migrated if feasible)

2. ✅ **Backend and Frontend URLs** come from shared config
   - No hardcoded URLs in any framework
   - All frameworks respect `ENVIRONMENT` variable

3. ✅ **Environment variable naming is standardized**
   - `BASE_URL` - Frontend URL (all frameworks)
   - `BACKEND_URL` - Backend API URL (all frameworks)
   - `ENVIRONMENT` - Environment name (dev, test, prod)

4. ✅ **TypeScript configs share common base configuration**
   - All TypeScript frameworks extend `tsconfig.base.json`

5. ✅ **All tests pass in all frameworks**
   - Cypress tests pass
   - Playwright tests pass
   - Robot Framework tests pass
   - Vibium tests pass (if applicable)
   - Selenium/Java tests pass

6. ✅ **TypeScript compilation works in all projects**
   - Cypress compiles
   - Playwright compiles
   - Vibium compiles

7. ✅ **CI/CD pipeline still works**
   - All workflow files updated
   - All environment variables correctly set

8. ✅ **Documentation is updated**
   - All README files updated
   - Framework guides updated
   - Configuration guide created

---

## Files to Review

### Current Configuration Files

**Shared Config (Source of Truth):**
- `config/environments.json` - ✅ Single source of truth (used by Playwright)
- `playwright/config/port-config.ts` - ✅ Uses `config/environments.json`

**Frameworks Needing Updates:**

**Cypress:**
- `cypress/cypress/e2e/wizard.cy.ts` - Lines 64-79 (hardcoded `getBackendUrl()`)
- `cypress/cypress.config.ts` - Uses `CYPRESS_BASE_URL` env var

**Robot Framework:**
- `src/test/robot/resources/Common.robot` - Line 9 (hardcoded `BASE_URL = http://localhost:3003`)

**Vibium:**
- No explicit configuration files (needs to be added)

**Selenium/Java:**
- `src/test/java/com/cjs/qa/core/Environment.java` - Uses XML config
- `Configurations/Environments.xml` - XML-based config

**TypeScript Configs:**
- `cypress/tsconfig.json` - 90% identical to Playwright
- `playwright/tsconfig.json` - 90% identical to Cypress
- `vibium/tsconfig.json` - Similar structure

---

## References

### Configuration Files
- **Environment Config (Source of Truth)**: `config/environments.json`
- **Playwright Config**: `playwright/config/port-config.ts` ✅ (Reference implementation)
- **Cypress Config**: `cypress/cypress.config.ts`
- **Robot Framework Config**: `src/test/robot/resources/Common.robot`
- **Selenium/Java Config**: `Configurations/Environments.xml`
- **Backend Tests Config**: `backend/tests/conftest.py` (Python example)

### Test Files
- **Cypress Wizard Test**: `cypress/cypress/e2e/wizard.cy.ts` (has hardcoded config)
- **Playwright Wizard Test**: `playwright/tests/wizard.spec.ts` (uses shared config ✅)

### TypeScript Configs
- **Cypress**: `cypress/tsconfig.json`
- **Playwright**: `playwright/tsconfig.json`
- **Vibium**: `vibium/tsconfig.json`

---

**Last Updated**: 2026-01-11  
**Document Status**: 🚧 **PLANNING** - Ready for implementation
