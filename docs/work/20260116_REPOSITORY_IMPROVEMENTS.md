# Repository Improvements - Code Duplication and Hard-Coded Values Analysis

**Date**: January 16, 2026  
**Status**: Working Document  
**Purpose**: Identify areas for improvement, focusing on code duplication and hard-coded values

---

## Executive Summary

This document outlines areas for improvement in the full-stack-qa repository, with a focus on:
1. **Code Duplication** - Repeated code patterns across multiple frameworks and files
2. **Hard-Coded Values** - Ports, URLs, API paths, and other configuration values that should be centralized
3. **Configuration Inconsistencies** - Multiple configuration sources and fallback mechanisms

---

## 1. Code Duplication Issues

### 1.1 Test Utility Functions - Duplicated Across Frameworks

**Issue**: Similar utility functions exist in multiple locations with slight variations.

#### Test Name/Description Utilities
- **Location 1**: `lib/test-utils.ts` - Shared TypeScript utility with inline constant
- **Location 2**: `cypress/cypress/support/test-utils.ts` - Cypress-specific adapter that requires JSON data parameter
- **Impact**: Two different implementations for the same purpose, causing maintenance overhead

**Recommendation**: 
- Consolidate to use `lib/test-utils.ts` as the single source
- Update Cypress adapter to read from the shared utility instead of requiring separate JSON loading
- Remove duplicate test name definitions

#### API Request Utilities
- **Base Class**: `lib/api-utils.ts` - Base class with common functionality
- **Cypress Adapter**: `cypress/cypress/support/api-utils.ts` - Extends base class
- **Playwright Adapter**: `playwright/helpers/api-utils.ts` - Extends base class
- **Status**: ✅ **Well-structured** - Uses inheritance pattern correctly

#### Database Utilities
- **Location 1**: `lib/db-utils.ts` - Shared TypeScript utility
- **Location 2**: `cypress/cypress/support/db-utils.ts` - Cypress-specific adapter
- **Location 3**: `playwright/helpers/db-utils.ts` - Playwright-specific adapter
- **Status**: Need to verify if these follow the same inheritance pattern as API utilities

**Action Items**:
- [ ] Review `lib/db-utils.ts` to ensure it's a proper base class
- [ ] Verify Cypress and Playwright adapters extend the base class correctly
- [ ] Document the pattern for future utilities

### 1.2 Test Implementation Duplication

**Issue**: The same test logic is implemented in both Cypress and Playwright with nearly identical code.

#### Wizard Test Suite
- **Cypress**: `cypress/cypress/e2e/wizard.cy.ts` (~371 lines)
- **Playwright**: `playwright/tests/wizard.spec.ts` (~371 lines)
- **Similarity**: ~95% code duplication - same test steps, same page objects, same assertions

**Recommendation**:
- Consider using a shared test specification format (e.g., Gherkin/Cucumber)
- Or create a test data/configuration file that both frameworks can consume
- Document the decision: maintain separate implementations for framework-specific features vs. shared test definitions

### 1.3 Page Object Model Duplication

**Issue**: Page objects are duplicated across Cypress and Playwright frameworks.

**Locations**:
- `cypress/cypress/page-objects/` - Cypress page objects
- `playwright/tests/pages/` - Playwright page objects

**Examples**:
- `HomePage`, `ApplicationsPage`, `CompaniesPage`, `ContactsPage`, `ClientsPage`, `NotesPage`, `JobSearchSitesPage`, `WizardStep1Page`
- Each framework has its own implementation of the same page objects

**Recommendation**:
- Evaluate if a shared page object library is feasible (may be difficult due to framework-specific APIs)
- At minimum, ensure page object interfaces/contracts are documented
- Consider generating page objects from a shared specification

### 1.4 Service Start Scripts - Duplicated Logic

**Issue**: Similar logic for starting services exists in multiple scripts.

**Files**:
- `scripts/start-fe.sh` - Frontend startup script
- `scripts/start-be.sh` - Backend startup script
- `scripts/start-env.sh` - Combined startup script
- `scripts/start-services-for-ci.sh` - CI-specific startup

**Duplicated Patterns**:
- Environment parsing logic (--env parameter handling)
- Color output definitions (RED, GREEN, YELLOW, BLUE, NC)
- Help text formatting
- Port configuration loading
- Service health checking

**Recommendation**:
- Extract common functions to `scripts/lib/common.sh` or similar
- Create reusable functions for:
  - Environment parsing
  - Color output
  - Help text generation
  - Port configuration loading
  - Service health checks

### 1.5 Configuration Loading - Multiple Implementations

**Issue**: Configuration loading logic is implemented in multiple languages/locations.

**Locations**:
- **Shell**: `scripts/ci/env-config.sh` - Shell script with fallback to hardcoded values
- **TypeScript**: `config/port-config.ts` - TypeScript utility
- **Python**: `config/port_config.py` - Python utility
- **Java**: `src/test/java/com/cjs/qa/config/EnvironmentConfig.java` - Java utility

**Problem**: Each implementation has its own fallback logic and error handling, making it difficult to ensure consistency.

**Recommendation**:
- Document the expected behavior for each language's config loader
- Ensure all loaders use the same fallback values
- Consider generating config loaders from a shared specification
- Remove hardcoded fallback values from `scripts/ci/env-config.sh` (lines 60-83)

---

## 2. Hard-Coded Values

### 2.1 Port Numbers - Partially Centralized

**Status**: ⚠️ **Partially Resolved** - Most code uses centralized config, but some hardcoded values remain.

#### Centralized Configuration
- **Primary Source**: `config/environments.json` - Single source of truth
- **Legacy Source**: `config/ports.json` - Maintained for backward compatibility

#### Hard-Coded Values Found

**Shell Scripts**:
- `scripts/ci/env-config.sh` (lines 60-83): Hardcoded fallback values for dev/test/prod ports
  ```bash
  dev)
      echo "FRONTEND_PORT=3003"
      echo "API_PORT=8003"
      ...
  ```

**Documentation**:
- Multiple documentation files contain hardcoded port references
- These should reference the config file instead

**Recommendation**:
- Remove hardcoded fallback values from `scripts/ci/env-config.sh`
- Ensure all scripts use the centralized config
- Update documentation to reference `config/environments.json` instead of listing ports

### 2.2 API Base Path - Centralized but Hard-Coded in Some Places

**Status**: ✅ **Mostly Centralized** - API base path is in `config/environments.json` under `api.basePath`

**Current Value**: `/api/v1`

**Potential Issues**:
- Some code may construct API paths manually instead of using the config
- Need to verify all API clients use the centralized base path

**Files to Review**:
- `lib/api-utils.ts` - Uses `getApiBasePath()` from config ✅
- `frontend/lib/api/client.ts` - Need to verify
- `backend/app/main.py` - Need to verify
- Performance test files (Locust, JMeter) - May have hardcoded paths

**Recommendation**:
- Audit all API client code to ensure they use centralized base path
- Update performance test configurations to read from config

### 2.3 Database Paths - Partially Hard-Coded

**Status**: ⚠️ **Partially Centralized**

**Centralized**:
- `config/environments.json` contains database configuration:
  ```json
  "database": {
    "directory": "Data/Core",
    "schemaDatabase": "full_stack_qa.db",
    "namingPattern": "full_stack_qa_{env}.db"
  }
  ```

**Potential Issues**:
- Backend code may have hardcoded database paths
- Test utilities may construct paths manually

**Recommendation**:
- Verify `backend/app/config.py` uses centralized config
- Ensure all database utilities read from config

### 2.4 Timeout Values - Centralized

**Status**: ✅ **Centralized** - Timeouts are in `config/environments.json` under `timeouts`

**Current Values**:
- Service Startup: 120 seconds
- Service Verification: 30 seconds
- API Client: 10000ms
- Web Server: 120000ms
- Check Interval: 2 seconds

**Recommendation**: ✅ No action needed - well centralized

### 2.5 CORS Origins - Centralized

**Status**: ✅ **Centralized** - CORS origins are in `config/environments.json` per environment

**Recommendation**: ✅ No action needed - well centralized

### 2.6 URL Construction - Some Hard-Coded Patterns

**Issue**: Some code constructs URLs manually instead of using config utilities.

**Examples**:
- `http://localhost:3003` - Should use `getFrontendUrl(environment)`
- `http://localhost:8003` - Should use `getBackendUrl(environment)`

**Files with Hard-Coded URLs** (from grep results):
- Documentation files (acceptable for examples)
- Some test files may have hardcoded URLs in comments
- Performance test configurations

**Recommendation**:
- Review test files for hardcoded URL construction
- Update performance test configs to use environment variables or config files

---

## 3. Configuration Inconsistencies

### 3.1 Multiple Configuration Sources

**Issue**: Configuration can come from multiple sources with different priorities.

**Sources** (in priority order):
1. Command-line arguments
2. Environment variables
3. `config/environments.json`
4. `config/ports.json` (legacy)
5. Hardcoded fallback values (in some scripts)

**Problem**: Different parts of the codebase may use different priority orders, leading to inconsistent behavior.

**Recommendation**:
- Document the expected priority order
- Ensure all config loaders follow the same priority
- Remove hardcoded fallback values where possible

### 3.2 Environment Detection Inconsistencies

**Issue**: Different parts of the codebase detect the environment differently.

**Methods Found**:
- `process.env.ENVIRONMENT` (Node.js/TypeScript)
- `Cypress.env('ENVIRONMENT')` (Cypress)
- Command-line arguments (scripts)
- Default to 'dev' if not specified

**Recommendation**:
- Standardize on `process.env.ENVIRONMENT` or `ENVIRONMENT` environment variable
- Document the expected environment variable name
- Ensure all frameworks/scripts check the same variable

### 3.3 Duplicate Configuration Files

**Status**: ✅ **Resolved** - Duplicate configuration files have been consolidated

**Files**:
- `config/environments.json` - **Primary config (single source of truth)**
- `config/ports.json` - Legacy ports-only config (maintained for backward compatibility)
- `src/test/resources/config/environments.json` - **Auto-generated** from `config/environments.json` during Maven build

**Solution Implemented**:
- ✅ Configured Maven to automatically copy `config/environments.json` to `src/test/resources/config/environments.json` during `process-test-resources` phase
- ✅ Added documentation in `src/test/resources/config/README.md` explaining the auto-generation
- ✅ Updated `config/README.md` to document the relationship and mark `ports.json` as legacy
- ✅ Verified Maven copy works correctly

**Result**: 
- `config/environments.json` is the single source of truth
- Java tests automatically get the latest config during Maven build
- No manual synchronization needed
- `ports.json` remains for backward compatibility but is marked as legacy

---

## 4. Other Improvement Opportunities

### 4.1 Test Framework Organization

**Issue**: Similar test patterns exist across multiple frameworks without clear documentation of when to use which.

**Frameworks**:
- Cypress (TypeScript)
- Playwright (TypeScript)
- Robot Framework (Python)
- Selenium (Java)
- Selenide (Java)
- Vibium (TypeScript)

**Recommendation**:
- Document when to use each framework
- Create a decision matrix for choosing a framework
- Consider consolidating to fewer frameworks if some are redundant

### 4.2 Documentation Duplication

**Issue**: Similar information is documented in multiple places.

**Examples**:
- Port configuration documented in multiple files
- Service startup instructions duplicated
- API version information in multiple locations

**Recommendation**:
- Use a single source of truth for each topic
- Link to the authoritative source from other docs
- Keep documentation DRY (Don't Repeat Yourself)

### 4.3 Script Organization

**Issue**: Many scripts in `scripts/` directory without clear organization.

**Recommendation**:
- Organize scripts into subdirectories:
  - `scripts/ci/` - CI/CD scripts (already exists)
  - `scripts/docker/` - Docker-related scripts (already exists)
  - `scripts/test/` - Test execution scripts
  - `scripts/dev/` - Development helper scripts
- Create a `scripts/README.md` documenting all scripts

### 4.4 Legacy Code Protection

**Issue**: `src/test/java/` directory is marked as protected, but contains code that may be outdated.

**Recommendation**:
- Document which parts of `src/test/java/` are actively used
- Create a migration plan for legacy code
- Consider archiving truly unused code

---

## 5. Priority Recommendations

### High Priority

1. **Remove hardcoded fallback values from `scripts/ci/env-config.sh`** ✅ **COMPLETED**
   - Impact: Ensures all code uses centralized config
   - Effort: Low
   - Risk: Low
   - **Status**: Completed on 2026-01-16
   - **Changes**: Removed all hardcoded fallback values from `get_ports_for_environment()`, `get_database_for_environment()`, `get_api_endpoints()`, `get_timeouts()`, and `get_cors_origins()` functions. Added proper error handling that requires `config/environments.json` and `jq` to be available, with clear error messages if configuration cannot be read.

2. **Consolidate test utility implementations** ✅ **COMPLETED**
   - Impact: Reduces maintenance overhead
   - Effort: Medium
   - Risk: Medium (need to test all frameworks)
   - **Status**: Completed on 2026-01-16
   - **Changes**: 
     - Consolidated Cypress test utilities to use inline data (due to webpack bundler limitations)
     - Updated Cypress adapter (`cypress/cypress/support/test-utils.ts`) to contain test data inline with clear sync documentation
     - Removed `readTestUtilsJson` task from `cypress.config.ts`
     - Updated Cypress test file (`wizard.cy.ts`) to use the adapter
     - Updated Cypress README to document the sync requirement
     - Fixed frontend build error by removing problematic `require()` call in `frontend/lib/api/client.ts`
     - **Note**: Due to Cypress webpack bundler limitations with cross-project imports, the Cypress adapter contains a copy of the data that must be kept in sync with `lib/test-utils.ts` (the primary source of truth)

3. **Extract common functions from service start scripts** ✅ **COMPLETED**
   - Impact: Reduces duplication, easier maintenance
   - Effort: Medium
   - Risk: Low
   - **Status**: Completed on 2026-01-16
   - **Changes**: 
     - Created `scripts/lib/common.sh` with shared functions:
       - Color definitions (RED, GREEN, YELLOW, BLUE, NC)
       - `get_script_dir()` - Get project root directory
       - `parse_environment_param()` - Parse --env parameter from arguments
       - `set_and_validate_environment()` - Set and validate environment value
       - `print_help_header()`, `print_help_section()`, `print_help_example()` - Help text formatting
       - `print_error()`, `print_warning()`, `print_success()`, `print_info()` - Message printing functions
     - Updated `scripts/start-fe.sh` to use common library
     - Updated `scripts/start-be.sh` to use common library
     - Removed duplicate color definitions, environment parsing, and validation logic

### Medium Priority

4. **Audit and remove hardcoded URLs/ports from test files** ✅ **COMPLETED**
   - Impact: Ensures consistency across environments
   - Effort: Medium
   - Risk: Low
   - **Status**: Completed on 2026-01-16
   - **Changes**: 
     - Updated `cypress/cypress.config.ts` to use `getFrontendUrl()` from centralized config
     - Updated `playwright/playwright.config.ts` to use `getFrontendUrl()` from centralized config
     - Updated `src/test/java/com/cjs/qa/junit/tests/HomePageTests.java` to use `EnvironmentConfig.getFrontendUrl()` instead of hardcoded URL
     - Updated `src/test/locust/api_load_test.py` to use `get_backend_url()` from centralized config
     - Updated `src/test/locust/comprehensive_load_test.py` to use `get_backend_url()` from centralized config
     - Added documentation comments to Artillery config files pointing to centralized config
     - Improved Robot Framework ConfigHelper fallback warnings to indicate configuration issues
     - **Note**: Artillery YAML config files retain hardcoded URLs as they are environment-specific config files, but now include documentation pointing to centralized config

5. **Document configuration priority order**
   - Impact: Prevents confusion, ensures consistency
   - Effort: Low
   - Risk: None

6. **Review and consolidate duplicate configuration files**
   - Impact: Reduces confusion about which config to use
   - Effort: Low
   - Risk: Low

### Low Priority

7. **Consider shared test specification format** ⚠️ **DEFERRED**
   - Impact: Reduces test duplication
   - Effort: High
   - Risk: High (major architectural change)
   - **Status**: Analysis complete - Decision: Defer (see `docs/work/20260116_SHARED_TEST_SPECIFICATION_ANALYSIS.md`)
   - **Rationale**: Current test data centralization approach is sufficient; high effort (6-12 months) for incremental benefits

8. ✅ **Organize scripts into subdirectories** - **COMPLETED** 2026-01-16
   - Impact: Better organization
   - Effort: **Medium** (200+ references to update)
   - Risk: **Medium** (need to update references in workflows, docs, and other scripts)
   - **Status**: ✅ **COMPLETED** - Scripts organized into logical subdirectories
   - **Changes**: 30 scripts moved, 30+ workflow references updated, 25+ documentation references updated
   - **Status**: Plan created - see `docs/work/20260116_SCRIPT_ORGANIZATION_PLAN.md`
   - **Recommendation**: 
     - Option A: Full reorganization (move all scripts, update all references) - More work but cleaner
     - Option B: Partial reorganization (keep common scripts in root, organize others) - Less work, mixed structure
     - Option C: Improve documentation only (keep current structure, better docs) - Minimal work, no structural change
   - **References Found**: 
     - 33 references in GitHub Actions workflows
     - 178+ references in documentation files
     - Additional references in other scripts

---

## 6. Implementation Plan Template

For each improvement:

1. **Identify**: Document the specific issue
2. **Design**: Create a solution design
3. **Implement**: Make the changes
4. **Test**: Verify all frameworks still work
5. **Document**: Update documentation
6. **Review**: Get approval before committing

---

## 7. Metrics to Track

- Number of hardcoded port/URL references
- Number of duplicate utility functions
- Number of configuration sources
- Test execution time (to ensure improvements don't slow things down)

---

## 8. Notes

- This is a working document - update as improvements are made
- Some duplications may be intentional (e.g., framework-specific adapters)
- Always test changes across all frameworks before committing
- Get approval before making major architectural changes

---

**Last Updated**: January 16, 2026  
**Next Review**: After implementing high-priority items

---

## 9. Implementation Status

### Completed Items

- ✅ **Item #1** (High Priority): Remove hardcoded fallback values from `scripts/ci/env-config.sh` - Completed 2026-01-16
- ✅ **Item #2** (High Priority): Consolidate test utility implementations - Completed 2026-01-16
- ✅ **Item #3** (High Priority): Extract common functions from service start scripts - Completed 2026-01-16
- ✅ **Item #4** (Medium Priority): Audit and remove hardcoded URLs/ports from test files - Completed 2026-01-16
- ✅ **Item #5** (Medium Priority): Document configuration priority order - Completed 2026-01-16
  - Expanded `docs/guides/infrastructure/PORT_CONFIGURATION.md` with comprehensive configuration priority documentation
  - Documented priority order for all frameworks (Shell scripts, Backend Python, TypeScript/JavaScript, Python tests, Java tests)
  - Added framework-specific implementation details, examples, and troubleshooting guide
  - Documented environment detection methods across all frameworks
  - Added best practices and override examples
- ✅ **Item #6** (Medium Priority): Review and consolidate duplicate configuration files - Completed 2026-01-16
  - Configured Maven to auto-copy `config/environments.json` to `src/test/resources/config/environments.json` during build
  - Added documentation in `src/test/resources/config/README.md` explaining auto-generation
  - Updated `config/README.md` to document file relationships and mark `ports.json` as legacy
  - Verified Maven copy works correctly - files stay in sync automatically

### In Progress

- None

### Deferred Items

- ✅ **Item #7** (Low Priority): Consider shared test specification format - **DEFERRED** 2026-01-16
  - **Status**: Analysis complete - see `docs/work/20260116_SHARED_TEST_SPECIFICATION_ANALYSIS.md`
  - **Decision**: **Defer implementation** - Current test data centralization approach is sufficient
  - **Rationale**: 
    - High implementation effort (6-12 months) for incremental benefits
    - Current approach already provides most value through test data centralization (`/test-data/`) and configuration centralization (`config/environments.json`)
    - Framework diversity is valuable - different frameworks serve different purposes
    - Risk outweighs benefit - major refactoring could break existing test suites
  - **Alternative**: Continue enhancing current test data centralization approach
  - **Future Review**: When test duplication becomes a significant problem or team size grows significantly

### Completed Items

- ✅ **Item #8** (Medium Priority): Organize scripts into subdirectories - **COMPLETED** 2026-01-16
  - **Status**: Scripts organized into logical subdirectories
  - **Changes**:
    - Created subdirectories: `services/`, `tests/frameworks/`, `tests/performance/`, `build/`, `quality/`, `reporting/`, `utils/`
    - Moved 32 scripts to appropriate subdirectories (fully organized - no scripts remain in root)
    - Updated 6 references in GitHub Actions workflows
    - Updated 27+ references in documentation files
    - Updated internal script references (11 scripts fixed for path calculations)
    - Updated scripts/README.md with new organization structure
    - Fixed SCRIPT_DIR path calculations in 11 scripts
    - All scripts tested and verified working
  - **See**: `docs/work/20260116_SCRIPT_ORGANIZATION_PLAN.md` for complete details

### Pending Items

- None - All planned items have been completed or deferred
