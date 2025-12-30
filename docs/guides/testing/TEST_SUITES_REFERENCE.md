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
- [Test Result Reporting](#test-result-reporting)

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
- **Parallel**: ✅ Yes (`parallel="tests"`, 4 threads)
- **Thread Count**: 4
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
- **Parallel**: ✅ Yes (`parallel="tests"`, 4 threads)
- **Thread Count**: 4
- **Preserve Order**: ✅ Yes (within each test)
- **Listeners**: AllureTestNg, GlobalRetryListener

**Test Classes**:
- `com.cjs.qa.junit.tests.SimpleGridTest` (separate test group)
- `com.cjs.qa.junit.tests.EnhancedGridTests` (separate test group)

**CI/CD Usage**: 
- Used when `test_suite: ci` is selected
- Command: `./mvnw -ntp test -DsuiteXmlFile=testng-ci-suite.xml`

**Note**: Test classes are split into separate `<test>` elements to enable parallel execution at the test group level.

---

#### 3. Extended Test Suite

**File**: `testng-extended-suite.xml`

**Configuration**:
- **Parallel**: ✅ Yes (`parallel="tests"`)
- **Thread Count**: 4
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
- **Thread Count**: 4
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
- **Parallel**: ✅ Yes (`parallel="tests"`, 4 threads)
- **Thread Count**: 4
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
- **Parallel**: ✅ Yes (`parallel="tests"`, 4 threads)
- **Thread Count**: 4
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
- **Parallel**: ✅ Yes (`parallel="tests"`, 4 threads)
- **Thread Count**: 4
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
- **Thread Count**: 4
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
- **Thread Count**: 4
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
- **Status**: ✅ **Enabled by default** (`enable_robot_tests: default: true`)
- Timeout: 5 minutes (configurable via `robot_tests_timeout_minutes` input)
- Command: `python3 -m robot.run --outputdir target/robot-reports src/test/robot/`
- **Docker Networking**: Uses `172.17.0.1` (Docker bridge gateway) in GitHub Actions to access services

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
- ✅ **Parallel** (`parallel="tests"`, 4 threads)

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
- `allure_version` (default: `'2.36.0'`) ✅ - Allure CLI version
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
- `grid_tests_thread_count` (default: 4)
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
  allure_version: '2.36.0'
  docker_shm_size: '4gb'
  # Parallel Configuration (planned)
  smoke_tests_parallel: 'tests'
  smoke_tests_thread_count: 4
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
  - Uses `wait-for-service.sh` utility for consistent waiting behavior

**Service Waiting Utility**:
All service waiting logic uses `scripts/ci/wait-for-service.sh`:
- **Location**: `scripts/ci/wait-for-service.sh`
- **Usage**: `./scripts/ci/wait-for-service.sh <url> <service-name> [timeout-seconds] [check-interval]`
- **Used by**: `start-services-for-ci.sh`, `verify-services.sh`, `wait-for-grid.sh`, `wait-for-services.sh`
- **Benefits**:
  - Consistent waiting behavior across all scripts
  - Configurable timeout and check interval
  - Progress reporting every 10 seconds
  - Clear error messages with attempt counts

**Port Utilities**:
All port-related operations use `scripts/ci/port-utils.sh`:
- **Location**: `scripts/ci/port-utils.sh`
- **Functions**: `is_port_in_use`, `get_port_pid`, `stop_port`, `check_port_status`
- **Used by**: `start-services-for-ci.sh`, `stop-services.sh`, `verify-services.sh`
- **Benefits**:
  - Consistent port checking behavior across all scripts
  - Single place to fix bugs and add features
  - Better error messages and status reporting
  - Reusable functions for port management

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
        ├─────────────────┐
        ▼                 ▼
┌─────────────────┐  ┌─────────────────┐
│  Allure Report  │  │  Test Summary   │
│  (aggregates)   │  │  (Group 3)      │
└─────────────────┘  └─────────────────┘
```

### Job Configuration Summary

> **Note**: This table shows jobs that run by default in CI/CD. Some TestNG suites (`testng-ci-suite.xml`, `testng-extended-suite.xml`, `testng-api-suite.xml`, `testng-mobile-suite.xml`) are only used conditionally when `test_suite` input is set to `ci`, `extended`, or `all` in workflow_dispatch. See individual suite sections above for details.

| Job | Framework | Timeout | Parallel in CI? | Parallel Within? | Notes |
|-----|-----------|---------|----------------|------------------|-------|
| `smoke-tests` | TestNG | 5 min ✅ | ✅ Yes | ✅ Yes (4 threads) | Uses `testng-smoke-suite.xml` |
| `grid-tests` | TestNG | 5 min 🔧 | ✅ Yes | ✅ Yes (4 threads) | Uses `testng-grid-suite.xml` (or `testng-ci-suite.xml` if `test_suite=ci`) |
| `mobile-browser-tests` | TestNG | 5 min 🔧 | ✅ Yes | ✅ Yes (4 threads) | Uses `testng-mobile-browser-suite.xml` |
| `responsive-design-tests` | TestNG | 5 min 🔧 | ✅ Yes | ✅ Yes (4 threads) | Uses `testng-responsive-suite.xml` |
| `cypress-tests` | Cypress | 5 min 🔧 | ✅ Yes | ❌ No | Will update to 5 min |
| `playwright-tests` | Playwright | 5 min 🔧 | ✅ Yes | ❌ No (workers: 1) | Will update to 5 min; Limited parallel due to shared services |
| `robot-tests` | Robot Framework | 5 min 🔧 | ✅ Yes | ❌ No | Will update to 5 min |
| `selenide-tests` | Selenide/TestNG | 5 min 🔧 | ✅ Yes | ✅ Yes (4 threads) | Uses `testng-selenide-suite.xml` |
| `vibium-tests` | Vibium | 5 min 🔧 | ✅ Yes | ❓ Unknown | Will update to 5 min |

**Legend**:
- ✅ Already at standard (5 minutes)
- 🔧 Will be updated to 5 minutes standard

---

## Parallel Execution Summary

### TestNG Suites - Parallel Configuration

> **Note**: This table shows ALL TestNG suite files and their parallel configuration. Some suites are only used conditionally (see "CI/CD Usage" in individual suite sections above). The "Job Configuration Summary" table above shows which jobs run by default in CI/CD.

| Suite | Parallel | Thread Count | CI/CD Usage | Notes |
|-------|----------|--------------|-------------|-------|
| `testng-smoke-suite.xml` | ✅ Yes | 4 | ✅ Always (`smoke-tests` job) | `parallel="tests"` |
| `testng-grid-suite.xml` | ✅ Yes | 4 | ✅ Always (`grid-tests` job) | `parallel="tests"` |
| `testng-mobile-browser-suite.xml` | ✅ Yes | 4 | ✅ Always (`mobile-browser-tests` job) | `parallel="tests"` |
| `testng-responsive-suite.xml` | ✅ Yes | 4 | ✅ Always (`responsive-design-tests` job) | `parallel="tests"` |
| `testng-selenide-suite.xml` | ✅ Yes | 4 | ✅ Always (`selenide-tests` job) | `parallel="tests"` |
| `testng-ci-suite.xml` | ✅ Yes | 4 | ⚠️ Conditional (`test_suite=ci`) | `parallel="tests"` |
| `testng-extended-suite.xml` | ✅ Yes | 4 | ⚠️ Conditional (`test_suite=extended`) | `parallel="tests"` |
| `testng-api-suite.xml` | ✅ Yes | 4 | ⚠️ Conditional (not directly used in CI/CD) | `parallel="tests"` |
| `testng-mobile-suite.xml` | ✅ Yes | 4 | ⚠️ Conditional (not directly used in CI/CD) | `parallel="tests"` |

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

## Test Result Reporting

### Test Summary Job

**Job Name**: `environment-test-summary`  
**Location**: `.github/workflows/env-fe.yml` (line 1160)

**Purpose**: Provides a comprehensive test result summary for all test frameworks in the GitHub Actions UI.

**Includes**:
- ✅ **All Frontend Tests** (Group 3):
  - **Category A** (Selenium Grid + Maven): Smoke, Grid, Mobile, Responsive, Selenide, Vibium
  - **Category B** (Alternative Frameworks): Cypress, Playwright, Robot Framework

**Result Parsing**:
- **Maven Surefire XML** (`TEST-*.xml`) - For TestNG-based tests
- **Allure JSON** (`*-result.json`) - For Allure-integrated tests
- **Playwright JSON** (`results.json`) - For Playwright tests
- **Cypress JSON** (`mochawesome.json`, `cypress-results.json`) - For Cypress tests
- **Robot Framework XML** (`output.xml`) - For Robot Framework tests

**Output**: 
- Total tests count
- Passed/Failed/Error counts
- Status summary (✅ PASSED / ❌ FAILED / ⚠️ NO TESTS FOUND)

**Dependencies**: Waits for all test jobs to complete (Group 3 - all frontend tests)

### Allure Report Job

**Job Name**: `environment-allure-report`  
**Location**: `.github/workflows/env-fe.yml` (line 1082)

**Purpose**: Generates comprehensive Allure HTML reports for all test frameworks.

**Includes**: All test frameworks (Group 3 - all frontend tests)

**Output**: 
- HTML report with detailed test results
- Screenshots, logs, and attachments
- Test execution timeline
- Environment-specific reports per environment (dev, test, prod)

**Deployment**: 
- Artifacts uploaded for each environment
- Combined report generated in main workflow (`combined-allure-report`)
- GitHub Pages deployment (main branch only)

**Note**: The Allure Report includes all test frameworks, while the Test Summary provides a quick overview in the GitHub Actions UI.

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

## Related Documentation

- [Workflow Test Organization](../infrastructure/WORKFLOW_TEST_ORGANIZATION.md) - Test job grouping and organization in GitHub Actions
- [Pipeline Workflow](../infrastructure/PIPELINE_WORKFLOW.md) - Complete pipeline architecture and job dependencies
- [GitHub Actions](../infrastructure/GITHUB_ACTIONS.md) - CI/CD pipeline overview
- [Test Suites Update Guide](TEST_SUITES_UPDATE_GUIDE.md) - Step-by-step guide for updating test configurations

---

**Last Updated**: 2025-12-27  
**Maintained By**: Development Team  
**Review Frequency**: Quarterly or when test suite configurations change

