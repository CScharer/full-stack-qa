# Test Suites Reference

> **Living Document** - This document provides a comprehensive reference of all test suites and their execution configurations across all testing frameworks in the project.

## 📋 Table of Contents

- [Overview](#overview)
- [TestNG Test Suites](#testng-test-suites)
- [Playwright Test Configuration](#playwright-test-configuration)
- [Cypress Test Configuration](#cypress-test-configuration)
- [Robot Framework Configuration](#robot-framework-configuration)
- [Selenide Test Configuration](#selenide-test-configuration)
- [Vibium Test Configuration](#vibium-test-configuration)
- [CI/CD Execution Strategy](#cicd-execution-strategy)
- [Parallel Execution Summary](#parallel-execution-summary)

---

## Overview

This project uses multiple testing frameworks, each with different test suites and execution strategies:

| Framework | Test Suites | Execution Type | CI/CD Job |
|-----------|-------------|----------------|-----------|
| **TestNG** | 9 suites | Maven Surefire | Multiple jobs |
| **Playwright** | Integration tests | npm test | `playwright-tests` |
| **Cypress** | E2E tests | npm run cypress:run | `cypress-tests` |
| **Robot Framework** | Acceptance tests | Python robot.run | `robot-tests` |
| **Selenide** | 1 suite | Maven Surefire | `selenide-tests` |
| **Vibium** | Visual regression | npm scripts | `vibium-tests` |

---

## TestNG Test Suites

TestNG is the primary testing framework, using Maven Surefire Plugin for execution.

### Maven Surefire Configuration

**Location**: `pom.xml` (lines 776-807)

```xml
<configuration>
    <parallel>methods</parallel>
    <threadCount>5</threadCount>
    <perCoreThreadCount>true</perCoreThreadCount>
    <systemPropertyVariables>
        <cucumber.execution.parallel.enabled>true</cucumber.execution.parallel.enabled>
        <cucumber.execution.parallel.config.strategy>fixed</cucumber.execution.parallel.config.strategy>
        <cucumber.execution.parallel.config.fixed.parallelism>5</cucumber.execution.parallel.config.fixed.parallelism>
    </systemPropertyVariables>
</configuration>
```

**Default Behavior**:
- Parallel execution at method level
- 5 threads per core
- Cucumber parallel execution enabled (5 workers)

### TestNG Suite Files

All TestNG suite files are located in: `src/test/resources/`

#### 1. Smoke Test Suite

**File**: `testng-smoke-suite.xml`

**Configuration**:
- **Parallel**: ❌ No (sequential execution)
- **Thread Count**: N/A
- **Preserve Order**: ✅ Yes
- **Listeners**: AllureTestNg, GlobalRetryListener

**Test Classes**:
- `com.cjs.qa.junit.tests.SmokeTests`
- `com.cjs.qa.junit.tests.SecretManagerSmokeTest`
- `com.cjs.qa.utilities.PageObjectGeneratorBrowserTest`

**Groups**: `smoke`

**CI/CD Usage**: 
- Job: `smoke-tests`
- Timeout: 5 minutes (configurable via `smoke_tests_timeout_minutes` input)
- Command: Uses `scripts/ci/run-maven-tests.sh` script
- Retry Count: Configurable via `test_retry_count` input (default: 1)

**Execution Time**: ~250 seconds (4.17 minutes) for 99 tests

---

#### 2. CI Test Suite

**File**: `testng-ci-suite.xml`

**Configuration**:
- **Parallel**: ❌ No (sequential execution)
- **Thread Count**: N/A
- **Preserve Order**: ✅ Yes
- **Listeners**: AllureTestNg, GlobalRetryListener

**Test Classes**:
- `com.cjs.qa.junit.tests.SimpleGridTest`
- `com.cjs.qa.junit.tests.EnhancedGridTests`

**CI/CD Usage**: 
- Used when `test_suite: ci` is selected
- Command: `./mvnw -ntp test -DsuiteXmlFile=testng-ci-suite.xml`

---

#### 3. Extended Test Suite

**File**: `testng-extended-suite.xml`

**Configuration**:
- **Parallel**: ✅ Yes (`parallel="tests"`)
- **Thread Count**: 3
- **Preserve Order**: ✅ Yes (within each test)
- **Listeners**: AllureTestNg, GlobalRetryListener

**Test Groups**:
1. **Data-Driven Tests**: `com.cjs.qa.junit.tests.DataDrivenTests`
2. **Negative Tests**: `com.cjs.qa.junit.tests.NegativeTests`
3. **Advanced Features Tests**: `com.cjs.qa.junit.tests.AdvancedFeaturesTests`
4. **Mobile Browser Tests**: `com.cjs.qa.junit.tests.mobile.MobileBrowserTests`
5. **Responsive Design Tests**: `com.cjs.qa.junit.tests.mobile.ResponsiveDesignTests`

**CI/CD Usage**: 
- Used when `test_suite: extended` is selected
- Command: `./mvnw -ntp test -DsuiteXmlFile=testng-extended-suite.xml`

---

#### 4. Grid Test Suite

**File**: `testng-grid-suite.xml`

**Configuration**:
- **Parallel**: ✅ Yes (`parallel="tests"`)
- **Thread Count**: 3
- **Preserve Order**: ✅ Yes (within each test)
- **Listeners**: AllureTestNg, GlobalRetryListener

**Test Groups**:
1. **Chrome Browser Tests**: `com.cjs.qa.junit.tests.EnhancedGridTests` (browser=chrome)
2. **Simple Grid Tests**: `com.cjs.qa.junit.tests.SimpleGridTest`

**Note**: Firefox tests are commented out (disabled)

**CI/CD Usage**: 
- Job: `grid-tests` (with matrix: chrome, firefox, edge)
- Timeout: 15 minutes
- Command: `./mvnw -ntp test -DsuiteXmlFile=testng-grid-suite.xml -Dbrowser=${{ matrix.browser }}`

---

#### 5. Mobile Browser Test Suite

**File**: `testng-mobile-browser-suite.xml`

**Configuration**:
- **Parallel**: ❌ No (sequential execution)
- **Thread Count**: N/A
- **Preserve Order**: ✅ Yes
- **Listeners**: AllureTestNg, GlobalRetryListener

**Test Classes**:
- `com.cjs.qa.junit.tests.mobile.MobileBrowserTests`

**CI/CD Usage**: 
- Job: `mobile-browser-tests`
- Timeout: 10 minutes
- Command: `./mvnw -ntp test -DsuiteXmlFile=testng-mobile-browser-suite.xml`

---

#### 6. Responsive Design Test Suite

**File**: `testng-responsive-suite.xml`

**Configuration**:
- **Parallel**: ❌ No (sequential execution)
- **Thread Count**: N/A
- **Preserve Order**: ✅ Yes
- **Listeners**: AllureTestNg, GlobalRetryListener

**Test Classes**:
- `com.cjs.qa.junit.tests.mobile.ResponsiveDesignTests`

**CI/CD Usage**: 
- Job: `responsive-design-tests`
- Timeout: 10 minutes
- Command: `./mvnw -ntp test -DsuiteXmlFile=testng-responsive-suite.xml`

---

#### 7. Selenide Test Suite

**File**: `testng-selenide-suite.xml`

**Configuration**:
- **Parallel**: ❌ No (sequential execution)
- **Thread Count**: N/A
- **Preserve Order**: ✅ Yes
- **Listeners**: AllureTestNg, GlobalRetryListener

**Test Classes**:
- `com.cjs.qa.junit.tests.HomePageTests`

**CI/CD Usage**: 
- Job: `selenide-tests`
- Timeout: 15 minutes
- Command: `./mvnw -ntp test -DsuiteXmlFile=testng-selenide-suite.xml`

---

#### 8. API Test Suite

**File**: `testng-api-suite.xml`

**Configuration**:
- **Parallel**: ✅ Yes (`parallel="tests"`)
- **Thread Count**: 3
- **Preserve Order**: ✅ Yes (within each test)
- **Listeners**: AllureTestNg, GlobalRetryListener

**Test Groups**:
1. **Basic API Tests**: `com.cjs.qa.api.tests.BasicApiTests`
2. **CRUD API Tests**: `com.cjs.qa.api.tests.CrudApiTests`
3. **Data-Driven API Tests**: `com.cjs.qa.api.tests.ApiDataDrivenTests`
4. **JSON Validation Tests**: `com.cjs.qa.api.tests.JsonValidationTests`

**CI/CD Usage**: 
- Not directly used in CI/CD (API tests may be part of other suites)
- Command: `./mvnw -ntp test -DsuiteXmlFile=testng-api-suite.xml`

---

#### 9. Mobile Test Suite

**File**: `testng-mobile-suite.xml`

**Configuration**:
- **Parallel**: ✅ Yes (`parallel="tests"`)
- **Thread Count**: 2
- **Preserve Order**: ✅ Yes (within each test)
- **Listeners**: AllureTestNg, GlobalRetryListener

**Note**: This appears to be a different mobile suite (separate from mobile-browser-suite)

---

## Playwright Test Configuration

**Location**: `playwright/playwright.config.ts`

**Configuration**:
```typescript
{
  fullyParallel: true,
  retries: 1,
  workers: process.env.CI ? 1 : undefined,  // Sequential in CI, parallel locally
  projects: process.env.CI ? [
    { name: 'chromium', use: { ...devices['Desktop Chrome'] } }
  ] : [
    { name: 'chromium', ... },
    { name: 'firefox', ... },
    { name: 'webkit', ... }
  ]
}
```

**Test Directory**: `playwright/tests/`

**Execution**:
- **Local**: `npm test` (parallel, all browsers)
- **CI**: `npm test` (sequential, chromium only)

**CI/CD Usage**: 
- Job: `playwright-tests`
- Timeout: 10 minutes
- Command: `npm test` (from `playwright/` directory)

**Parallel Execution**:
- ✅ **Local**: Fully parallel (`fullyParallel: true`, `workers: undefined`)
- ❌ **CI**: Sequential (`workers: 1`)

---

## Cypress Test Configuration

**Location**: `cypress/cypress.config.ts`

**Configuration**:
```typescript
{
  e2e: {
    baseUrl: process.env.CYPRESS_BASE_URL || 'http://localhost:3003',
    retries: {
      runMode: 1,  // Retry once in CI
      openMode: 0  // No retry in interactive mode
    }
  }
}
```

**Test Directory**: `cypress/cypress/e2e/`

**Execution**:
- **Local**: `npm run cypress:run` or `npm run cypress:open`
- **CI**: `xvfb-run -a npm run cypress:run`

**CI/CD Usage**: 
- Job: `cypress-tests`
- Timeout: 10 minutes
- Command: `xvfb-run -a npm run cypress:run` (from `cypress/` directory)

**Parallel Execution**:
- ❌ **Not configured** - Cypress runs tests sequentially by default
- To enable parallel execution, requires Cypress Dashboard or custom configuration

---

## Robot Framework Configuration

**Location**: `src/test/robot/` (test files)

**Execution**:
- **Command**: `python3 -m robot.run`
- **Output**: `target/robot-reports/`
- **Retry**: Manual retry logic in CI/CD (runs twice if first fails)

**CI/CD Usage**: 
- Job: `robot-tests`
- Timeout: 10 minutes
- Command: `python3 -m robot.run --outputdir target/robot-reports src/test/robot/`

**Parallel Execution**:
- ❌ **Not configured** - Robot Framework runs tests sequentially
- To enable parallel execution, requires `pabot` (Parallel Robot Framework)

---

## Selenide Test Configuration

**Location**: TestNG suite file: `testng-selenide-suite.xml`

**Test Classes**: `com.cjs.qa.junit.tests.HomePageTests`

**Execution**: Via Maven Surefire (same as other TestNG suites)

**CI/CD Usage**: 
- Job: `selenide-tests`
- Timeout: 15 minutes
- Command: `./mvnw -ntp test -DsuiteXmlFile=testng-selenide-suite.xml`

**Parallel Execution**:
- ❌ **Sequential** (suite has no parallel configuration)

---

## Vibium Test Configuration

**Location**: `vibium/` directory

**Execution**:
- **Script**: `scripts/run-vibium-tests.sh`
- **Framework**: Visual regression testing

**CI/CD Usage**: 
- Job: `vibium-tests`
- Timeout: 10 minutes
- Command: `./scripts/run-vibium-tests.sh`

**Parallel Execution**:
- ❓ **Unknown** - Depends on Vibium framework configuration

---

## CI/CD Execution Strategy

### Workflow: `.github/workflows/env-fe.yml`

All test jobs run in **parallel** (no dependencies between them):

### Dynamic Configuration Inputs

> **Status**: ✅ **Implemented** - Workflow inputs for timeout, version, and test configuration allow dynamic configuration without modifying workflow files.

**Implemented Inputs**:

**Version Configuration Inputs**:
- `java_version` (default: `'21'`) ✅ - Java version for JDK setup
- `python_version` (default: `'3.11'`) ✅ - Python version for setup-python
- `node_version` (default: `'20'`) ✅ - Node.js version for setup-node

**Test Configuration Inputs**:
- `test_retry_count` (default: `1`) ✅ - Number of retries for failed tests

**Timeout Inputs - Service/Grid Waits**:
- `grid_wait_timeout_seconds` (default: `60`) ✅ - Timeout in seconds for waiting for Selenium Grid
- `service_wait_timeout_seconds` (default: `30`) ✅ - Timeout in seconds for waiting for services (frontend/backend)

**Resource Configuration Inputs**:
- `maven_memory` (default: `'2048m'`) ✅ - Maven heap size (e.g., `2048m`, `4096m`)
- `allure_version` (default: `'2.25.0'`) ✅ - Allure CLI version
- `docker_shm_size` (default: `'2gb'`) ✅ - Docker shared memory size for Selenium nodes (e.g., `2gb`, `4gb`)

**Timeout Inputs - Test Execution** (for each test type):
> **Standard**: All timeout defaults are **5 minutes** for consistency. Tests should be optimized to complete within this timeframe.

- `smoke_tests_timeout_minutes` (default: 5 minutes) ✅
- `grid_tests_timeout_minutes` (default: 5 minutes) ✅
- `mobile_tests_timeout_minutes` (default: 5 minutes) ✅
- `responsive_tests_timeout_minutes` (default: 5 minutes) ✅
- `cypress_tests_timeout_minutes` (default: 5 minutes) ✅
- `playwright_tests_timeout_minutes` (default: 5 minutes) ✅
- `robot_tests_timeout_minutes` (default: 5 minutes) ✅
- `selenide_tests_timeout_minutes` (default: 5 minutes) ✅
- `vibium_tests_timeout_minutes` (default: 5 minutes) ✅

**Legend**:
- ✅ All timeout inputs are implemented and default to 5 minutes
- ✅ Version inputs are implemented and allow flexible version selection
- ✅ Test retry count is configurable via input

**Parallel Execution Inputs** (for TestNG-based tests):
- `smoke_tests_parallel` (default: `none`) - Strategy: `none`, `tests`, `methods`, `classes`
- `smoke_tests_thread_count` (default: 1)
- `grid_tests_parallel` (default: `tests`)
- `grid_tests_thread_count` (default: 3)
- `mobile_tests_parallel` (default: `none`)
- `mobile_tests_thread_count` (default: 1)
- `responsive_tests_parallel` (default: `none`)
- `responsive_tests_thread_count` (default: 1)
- `selenide_tests_parallel` (default: `none`)
- `selenide_tests_thread_count` (default: 1)

**Playwright-Specific Inputs**:
- `playwright_tests_workers` (default: 1) - Number of workers in CI

**Usage Example**:
```yaml
# In calling workflow (ci.yml)
uses: ./.github/workflows/env-fe.yml
with:
  environment: 'test'
  # Version Configuration
  java_version: '21'
  python_version: '3.11'
  node_version: '20'
  # Test Configuration
  test_retry_count: 2
  # Timeout Configuration - Test Execution
  smoke_tests_timeout_minutes: 7
  grid_tests_timeout_minutes: 20
  # Timeout Configuration - Service/Grid Waits
  grid_wait_timeout_seconds: 90
  service_wait_timeout_seconds: 45
  # Resource Configuration
  maven_memory: '4096m'
  allure_version: '2.26.0'
  docker_shm_size: '4gb'
  # Parallel Configuration (planned)
  smoke_tests_parallel: 'tests'
  smoke_tests_thread_count: 3
  playwright_tests_workers: 4
```

**CI/CD Scripts**:

**Maven Test Script**:
All Maven-based tests (TestNG, Selenide) now use the centralized `scripts/ci/run-maven-tests.sh` script:
- **Location**: `scripts/ci/run-maven-tests.sh`
- **Usage**: `./scripts/ci/run-maven-tests.sh <environment> <suite-file> [retry-count] [browser] [additional-args...]`
- **Benefits**: 
  - Centralized Maven test execution
  - Consistent retry handling
  - Easier maintenance and updates

**Allure Environment Properties Script**:
All Allure environment property creation now uses `scripts/ci/create-allure-env-properties.sh`:
- **Location**: `scripts/ci/create-allure-env-properties.sh`
- **Usage**: `./scripts/ci/create-allure-env-properties.sh <environment> <browser> <hub-port> <base-url> <test-suite> [additional-properties...]`
- **Used in**: Grid Tests, Mobile Browser Tests, Responsive Tests, Selenide Tests
- **Benefits**: 
  - Consistent Allure reporting
  - Centralized property management
  - Support for additional custom properties

**Robot Framework Installation Script**:
Robot Framework installation uses `scripts/ci/install-robot-framework.sh`:
- **Location**: `scripts/ci/install-robot-framework.sh`
- **Usage**: `./scripts/ci/install-robot-framework.sh [python-exe]`
- **Benefits**: 
  - Simplified installation process
  - Automatic verification
  - Consistent setup across environments

**Service Verification Script**:
Service verification uses `scripts/ci/verify-services.sh`:
- **Location**: `scripts/ci/verify-services.sh`
- **Usage**: `./scripts/ci/verify-services.sh <base-url> [timeout-seconds]`
- **Benefits**: 
  - Configurable timeout
  - Clear error messages
  - Reusable across test jobs
  - Uses centralized `port-config.sh` for port configuration (single source of truth)

**Benefits**:
- ✅ No need to modify workflow files for timeout adjustments
- ✅ Easy to experiment with parallel execution settings
- ✅ Environment-specific configurations possible
- ✅ Can be set per workflow run or via workflow_dispatch inputs

**Test Isolation Requirements**:
- ✅ **All tests must be isolated** - No dependencies on other tests
- ✅ **All tests must run independently** - Can execute in any order
- ✅ **All tests must clean up** - No shared state between test runs
- ✅ **All timeouts default to 5 minutes** - Standardized for consistency

**Tests That Cannot Use Parallel Configuration**:
While all tests can have timeout configured, some have limitations for parallel execution:

1. **Integration Tests (Playwright)**:
   - ✅ Timeout: Can be configured
   - ⚠️ Parallel: Limited - Requires shared services and may share database state
   - **Current**: Runs with `workers: 1` in CI to avoid conflicts

2. **Database-Dependent Tests**:
   - ✅ Timeout: Can be configured
   - ⚠️ Parallel: May need sequential execution if tests modify shared database state
   - **Solution**: Use isolated test databases or proper cleanup

3. **Service-Dependent Tests**:
   - ✅ Timeout: Can be configured
   - ⚠️ Parallel: May conflict if tests require exclusive service access
   - **Solution**: Design tests to share services safely

```
┌─────────────────┐
│  smoke-tests    │
│  grid-tests     │
│  mobile-tests   │
│  responsive     │
│  cypress        │
│  playwright     │
│  robot          │
│  selenide       │
│  vibium         │
└─────────────────┘
        │
        ▼
┌─────────────────┐
│  Allure Report  │
│  (aggregates)   │
└─────────────────┘
```

### Job Configuration Summary

| Job | Framework | Timeout | Parallel in CI? | Parallel Within? | Notes |
|-----|-----------|---------|----------------|------------------|-------|
| `smoke-tests` | TestNG | 5 min ✅ | ✅ Yes | ❌ No | Standard timeout |
| `grid-tests` | TestNG | 5 min 🔧 | ✅ Yes | ✅ Yes (3 threads) | Will update to 5 min |
| `mobile-browser-tests` | TestNG | 5 min 🔧 | ✅ Yes | ❌ No | Will update to 5 min |
| `responsive-design-tests` | TestNG | 5 min 🔧 | ✅ Yes | ❌ No | Will update to 5 min |
| `cypress-tests` | Cypress | 5 min 🔧 | ✅ Yes | ❌ No | Will update to 5 min |
| `playwright-tests` | Playwright | 5 min 🔧 | ✅ Yes | ❌ No (workers: 1) | Will update to 5 min; Limited parallel due to shared services |
| `robot-tests` | Robot Framework | 5 min 🔧 | ✅ Yes | ❌ No | Will update to 5 min |
| `selenide-tests` | Selenide/TestNG | 5 min 🔧 | ✅ Yes | ❌ No | Will update to 5 min |
| `vibium-tests` | Vibium | 5 min 🔧 | ✅ Yes | ❓ Unknown | Will update to 5 min |

**Legend**:
- ✅ Already at standard (5 minutes)
- 🔧 Will be updated to 5 minutes standard

---

## Parallel Execution Summary

### TestNG Suites - Parallel Configuration

| Suite | Parallel | Thread Count | Notes |
|-------|----------|--------------|-------|
| `testng-smoke-suite.xml` | ❌ No | N/A | Sequential execution |
| `testng-ci-suite.xml` | ❌ No | N/A | Sequential execution |
| `testng-extended-suite.xml` | ✅ Yes | 3 | `parallel="tests"` |
| `testng-grid-suite.xml` | ✅ Yes | 3 | `parallel="tests"` |
| `testng-mobile-browser-suite.xml` | ❌ No | N/A | Sequential execution |
| `testng-responsive-suite.xml` | ❌ No | N/A | Sequential execution |
| `testng-selenide-suite.xml` | ❌ No | N/A | Sequential execution |
| `testng-api-suite.xml` | ✅ Yes | 3 | `parallel="tests"` |
| `testng-mobile-suite.xml` | ✅ Yes | 2 | `parallel="tests"` |

### Maven Surefire Default

- **Parallel**: `methods` (method-level parallelization)
- **Thread Count**: 5 per core
- **Note**: Suite XML files can override this with their own `parallel` attribute

### Framework-Level Parallel Execution

| Framework | Local | CI | Configuration |
|-----------|-------|----|--------------| 
| **TestNG** | ✅ Yes (methods, 5 threads/core) | ✅ Yes (if suite allows) | `pom.xml` + suite XML |
| **Playwright** | ✅ Yes (fully parallel) | ❌ No (workers: 1) | `playwright.config.ts` |
| **Cypress** | ❌ No | ❌ No | Not configured |
| **Robot Framework** | ❌ No | ❌ No | Not configured |
| **Selenide** | ✅ Yes (via Maven) | ✅ Yes (if suite allows) | TestNG suite |
| **Vibium** | ❓ Unknown | ❓ Unknown | Framework-dependent |

---

## Notes

1. **TestNG Suite Override**: Even though Maven Surefire is configured for parallel execution at the method level, individual TestNG suite XML files can override this with their own `parallel` attribute.

2. **CI vs Local**: Playwright runs sequentially in CI (`workers: 1`) but parallel locally (`workers: undefined`).

3. **Timeout Issues**: The smoke test suite takes ~250 seconds (4.17 minutes) with a 5-minute timeout, leaving minimal buffer for cleanup.

4. **Grid Tests Matrix**: The `grid-tests` job uses a matrix strategy to run tests across multiple browsers (chrome, firefox, edge) in parallel.

5. **Planned: Dynamic Configuration**: Workflow inputs for timeout and parallel execution will be added to `.github/workflows/env-fe.yml` to allow dynamic configuration without modifying workflow files. This will enable:
   - Per-run timeout adjustments
   - Easy experimentation with parallel execution settings
   - Environment-specific configurations
   - Configuration via workflow_dispatch inputs
   - See [Planned: Dynamic Configuration Inputs](#planned-dynamic-configuration-inputs) section above for details

6. **Test Isolation Standard**: All tests are designed to be **isolated and run independently**. Tests should:
   - Have no dependencies on other tests
   - Be executable in any order
   - Clean up after themselves
   - Use isolated test data when possible

7. **Standard Timeout**: All test timeouts default to **5 minutes** for consistency. Tests should be optimized to complete within this timeframe. If a test consistently exceeds 5 minutes, it should be:
   - Optimized for performance
   - Split into smaller tests
   - Or have its timeout increased via workflow input (not hardcoded)

8. **Parallel Execution Limitations**: While all tests can have timeout configured in the workflow, some tests have limitations for parallel execution:
   - **Integration tests** (Playwright) may need sequential execution due to shared services/database
   - **Database-dependent tests** may need sequential execution if they modify shared state
   - **Service-dependent tests** may conflict if they require exclusive access
   - **Solution**: Design tests to be parallel-safe with proper isolation and cleanup

---

**Last Updated**: 2025-12-27  
**Maintained By**: Development Team  
**Review Frequency**: Quarterly or when test suite configurations change

