# Test Suites Update Guide

> **Purpose**: This document provides a step-by-step guide for updating test suite configurations, including a legend of all configuration options and their effects.

## 📋 Legend

### Status Icons

| Icon | Meaning | Description |
|------|---------|-------------|
| ✅ | Enabled/Yes | Feature is enabled or value is set |
| ❌ | Disabled/No | Feature is disabled or not set |
| ⚠️ | Warning | Requires attention or has limitations |
| 🔧 | Configurable | Can be modified |
| 📝 | Documentation | Refer to documentation |
| 🔄 | Override | Can override parent configuration |
| ⏱️ | Time-sensitive | Affects execution time |
| 🚀 | Performance | Affects performance |

### Configuration Types

| Type | Description | Location |
|------|-------------|----------|
| **TestNG Suite** | XML configuration file for TestNG test execution | `src/test/resources/testng-*.xml` |
| **Maven Config** | Maven Surefire Plugin configuration | `pom.xml` |
| **Framework Config** | Framework-specific configuration files | Framework directories |
| **CI/CD Config** | GitHub Actions workflow configuration | `.github/workflows/*.yml` |

### Parallel Execution Levels

| Level | Description | Scope |
|-------|-------------|-------|
| **Job Level** | Multiple test jobs run simultaneously | GitHub Actions |
| **Suite Level** | Multiple test groups/classes run simultaneously | TestNG suite XML |
| **Method Level** | Multiple test methods run simultaneously | Maven Surefire |
| **Framework Level** | Framework's internal parallel execution | Playwright, Cypress, etc. |

---

## 📚 Table of Contents

- [Quick Reference](#quick-reference)
- [TestNG Suite Updates](#testng-suite-updates)
- [Maven Configuration Updates](#maven-configuration-updates)
- [Framework Configuration Updates](#framework-configuration-updates)
- [CI/CD Configuration Updates](#cicd-configuration-updates)
- [Common Update Scenarios](#common-update-scenarios)
- [Validation Checklist](#validation-checklist)

---

## Quick Reference

### TestNG Suite XML Attributes

| Attribute | Values | Effect | Example |
|-----------|--------|--------|---------|
| `parallel` | `none`, `methods`, `tests`, `classes`, `instances` | Parallel execution strategy | `parallel="tests"` |
| `thread-count` | Integer (1+) | Number of parallel threads | `thread-count="3"` |
| `preserve-order` | `true`, `false` | Maintain test execution order | `preserve-order="true"` |
| `verbose` | Integer (0-10) | Logging verbosity level | `verbose="1"` |
| `name` | String | Suite/test name | `name="Smoke Test Suite"` |

### Maven Surefire Configuration

| Property | Values | Effect | Location |
|----------|--------|--------|----------|
| `<parallel>` | `methods`, `classes`, `suites`, `both`, `all` | Parallel execution level | `pom.xml` |
| `<threadCount>` | Integer | Number of threads | `pom.xml` |
| `<perCoreThreadCount>` | `true`, `false` | Scale threads per CPU core | `pom.xml` |
| `<dataproviderthreadcount>` | Integer | Threads for data providers | `pom.xml` |

### CI/CD Timeout Settings

> **Standard**: All test timeouts default to **5 minutes** for consistency. Tests should be optimized to complete within this timeframe.

| Job | Current Timeout | Standard Default | Location |
|-----|----------------|------------------|----------|
| `smoke-tests` | 5 minutes | 5 minutes ✅ | `.github/workflows/env-fe.yml:168` |
| `grid-tests` | 5 minutes | 5 minutes ✅ | `.github/workflows/env-fe.yml:287` |
| `mobile-browser-tests` | 5 minutes | 5 minutes ✅ | `.github/workflows/env-fe.yml:397` |
| `responsive-design-tests` | 5 minutes | 5 minutes ✅ | `.github/workflows/env-fe.yml:503` |
| `cypress-tests` | 5 minutes | 5 minutes ✅ | `.github/workflows/env-fe.yml:592` |
| `playwright-tests` | 5 minutes | 5 minutes ✅ | `.github/workflows/env-fe.yml:669` |
| `robot-tests` | 5 minutes | 5 minutes ✅ | `.github/workflows/env-fe.yml:836` |
| `selenide-tests` | 5 minutes | 5 minutes ✅ | `.github/workflows/env-fe.yml:968` |
| `vibium-tests` | 5 minutes | 5 minutes ✅ | `.github/workflows/env-fe.yml:1065` |

**Legend**:
- ✅ All timeouts are now standardized to 5 minutes

### CI/CD Workflow Inputs

> **Status**: ✅ **Partially Implemented** - Version, test retry, and timeout inputs are implemented. Parallel execution inputs are planned.

> **Important**: All tests are designed to be **isolated and run independently**. All timeout defaults are set to **5 minutes** for consistency.

> **⚠️ Infrastructure Constraints**: Pipeline runners, memory, CPU, and other infrastructure resources **cannot be changed**. All test configurations must work within existing resource limits. If tests fail due to resource constraints, solutions must focus on optimization, reducing parallelism, or splitting tests - not increasing resources.

**Implemented Inputs**:

**Version Configuration** (✅ Implemented):
- `java_version` (default: `'21'`) - Java version for JDK setup
- `python_version` (default: `'3.11'`) - Python version for setup-python
- `node_version` (default: `'20'`) - Node.js version for setup-node

**Test Configuration** (✅ Implemented):
- `test_retry_count` (default: `1`) - Number of retries for failed tests

**Timeout Configuration** (✅ Implemented):
- **Test Execution Timeouts**: All timeout inputs default to 5 minutes and are configurable per test type
- **Service/Grid Wait Timeouts**: 
  - `grid_wait_timeout_seconds` (default: 60) - For waiting for Selenium Grid
  - `service_wait_timeout_seconds` (default: 30) - For waiting for frontend/backend services

**Resource Configuration** (✅ Implemented):
- `maven_memory` (default: `'2048m'`) - Maven heap size
- `allure_version` (default: `'2.25.0'`) - Allure CLI version
- `docker_shm_size` (default: `'2gb'`) - Docker shared memory for Selenium nodes

**Parallel Execution Configuration** (🔧 Planned):
**New Input Structure** (to be added):

| Input Name | Type | Default | Description | Example |
|------------|------|---------|-------------|---------|
| `smoke_tests_timeout_minutes` | number | 5 | Timeout in minutes for smoke tests | `7` |
| `smoke_tests_parallel` | string | `none` | Parallel execution strategy for smoke tests | `tests` |
| `smoke_tests_thread_count` | number | 1 | Number of threads for parallel execution | `3` |
| `grid_tests_timeout_minutes` | number | 5 | Timeout in minutes for grid tests | `5` |
| `grid_tests_parallel` | string | `tests` | Parallel execution strategy for grid tests | `methods` |
| `grid_tests_thread_count` | number | 3 | Number of threads for parallel execution | `5` |
| `mobile_tests_timeout_minutes` | number | 5 | Timeout in minutes for mobile tests | `5` |
| `mobile_tests_parallel` | string | `none` | Parallel execution strategy for mobile tests | `tests` |
| `mobile_tests_thread_count` | number | 1 | Number of threads for parallel execution | `2` |
| `responsive_tests_timeout_minutes` | number | 5 | Timeout in minutes for responsive tests | `5` |
| `responsive_tests_parallel` | string | `none` | Parallel execution strategy for responsive tests | `tests` |
| `responsive_tests_thread_count` | number | 1 | Number of threads for parallel execution | `2` |
| `cypress_tests_timeout_minutes` | number | 5 | Timeout in minutes for Cypress tests | `5` |
| `playwright_tests_timeout_minutes` | number | 5 | Timeout in minutes for Playwright tests | `5` |
| `playwright_tests_workers` | number | 1 | Number of workers for Playwright (CI only) | `4` |
| `robot_tests_timeout_minutes` | number | 5 | Timeout in minutes for Robot Framework tests | `5` |
| `selenide_tests_timeout_minutes` | number | 5 | Timeout in minutes for Selenide tests | `5` |
| `selenide_tests_parallel` | string | `none` | Parallel execution strategy for Selenide tests | `tests` |
| `selenide_tests_thread_count` | number | 1 | Number of threads for parallel execution | `3` |
| `vibium_tests_timeout_minutes` | number | 5 | Timeout in minutes for Vibium tests | `5` |

**⚠️ Tests That Cannot Use Timeout/Parallel Configuration**:

All tests **CAN** have timeout configured in the workflow. However, some tests have limitations for parallel execution:

1. **Integration Tests (Playwright)**: 
   - ✅ **Timeout**: Can be configured
   - ⚠️ **Parallel**: Limited - Requires shared services (backend/frontend) and may share database state
   - **Note**: Currently runs with `workers: 1` in CI to avoid conflicts

2. **Database-Dependent Tests**:
   - ✅ **Timeout**: Can be configured
   - ⚠️ **Parallel**: May need sequential execution if tests modify shared database state
   - **Solution**: Use isolated test databases or proper cleanup between tests

3. **Service-Dependent Tests**:
   - ✅ **Timeout**: Can be configured
   - ⚠️ **Parallel**: May conflict if tests require exclusive access to services
   - **Solution**: Tests should be designed to share services safely

**Best Practice**: All tests should be designed to:
- ✅ Run independently (no dependencies on other tests)
- ✅ Use isolated test data
- ✅ Clean up after themselves
- ✅ Support parallel execution when possible

**Parallel Execution Strategy Values**:
- `none` - Sequential execution (no parallelization)
- `tests` - Run test groups in parallel (recommended for most suites)
- `methods` - Run test methods in parallel (faster but more complex)
- `classes` - Run test classes in parallel

**Usage Example** (in workflow step):
```yaml
- name: Run Smoke Tests
  timeout-minutes: ${{ inputs.smoke_tests_timeout_minutes || 5 }}
  env:
    PARALLEL_STRATEGY: ${{ inputs.smoke_tests_parallel || 'none' }}
    THREAD_COUNT: ${{ inputs.smoke_tests_thread_count || 1 }}
  run: |
    ./mvnw -ntp test \
      -DsuiteXmlFile=testng-smoke-suite.xml \
      -Dparallel=${{ inputs.smoke_tests_parallel || 'none' }} \
      -DthreadCount=${{ inputs.smoke_tests_thread_count || 1 }}
```

---

## TestNG Suite Updates

### File Locations

All TestNG suite files are located in: `src/test/resources/`

**Current Suite Files**:
- `testng-smoke-suite.xml`
- `testng-ci-suite.xml`
- `testng-extended-suite.xml`
- `testng-grid-suite.xml`
- `testng-mobile-browser-suite.xml`
- `testng-responsive-suite.xml`
- `testng-selenide-suite.xml`
- `testng-api-suite.xml`
- `testng-mobile-suite.xml`

### Step-by-Step: Enable Parallel Execution

**Scenario**: Enable parallel execution for smoke tests

1. **Open the suite file**:
   ```bash
   vim src/test/resources/testng-smoke-suite.xml
   ```

2. **Add parallel attribute to `<suite>` tag**:
   ```xml
   <!-- Before -->
   <suite name="Smoke Test Suite" verbose="1">
   
   <!-- After -->
   <suite name="Smoke Test Suite" verbose="1" parallel="tests" thread-count="3">
   ```

3. **Choose parallel strategy**:
   - `parallel="tests"` - Run test groups in parallel (recommended)
   - `parallel="methods"` - Run test methods in parallel (faster, more complex)
   - `parallel="classes"` - Run test classes in parallel

4. **Set thread count**:
   - Start with `thread-count="3"` for most suites
   - Increase to `5` for larger suites
   - Monitor resource usage and adjust

5. **Test locally**:
   ```bash
   ./mvnw -ntp test -DsuiteXmlFile=testng-smoke-suite.xml
   ```

6. **Update CI/CD timeout if needed**:
   - If tests run faster, timeout may not need change
   - If tests run slower, increase timeout in `.github/workflows/env-fe.yml`

### Step-by-Step: Add a New Test Class

**Scenario**: Add a new test class to the smoke suite

1. **Open the suite file**:
   ```bash
   vim src/test/resources/testng-smoke-suite.xml
   ```

2. **Add `<class>` tag within existing `<test>` or create new `<test>`**:
   ```xml
   <test name="Critical Path Smoke Tests" preserve-order="true">
       <classes>
           <class name="com.cjs.qa.junit.tests.SmokeTests"/>
           <class name="com.cjs.qa.junit.tests.SecretManagerSmokeTest"/>
           <class name="com.cjs.qa.junit.tests.PageObjectGeneratorBrowserTest"/>
           <!-- NEW -->
           <class name="com.cjs.qa.junit.tests.NewSmokeTest"/>
       </classes>
   </test>
   ```

3. **Verify class exists**:
   ```bash
   find src/test/java -name "NewSmokeTest.java"
   ```

4. **Test locally**:
   ```bash
   ./mvnw -ntp test -DsuiteXmlFile=testng-smoke-suite.xml
   ```

### Step-by-Step: Create a New Test Suite

**Scenario**: Create a new test suite for regression tests

1. **Create new suite file**:
   ```bash
   touch src/test/resources/testng-regression-suite.xml
   ```

2. **Add suite structure** (copy from existing suite and modify):
   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
   <suite name="Regression Test Suite" verbose="1" parallel="tests" thread-count="5">
       <listeners>
           <listener class-name="io.qameta.allure.testng.AllureTestNg"/>
           <listener class-name="com.cjs.qa.utilities.GlobalRetryListener"/>
       </listeners>
       
       <test name="Regression Tests" preserve-order="true">
           <classes>
               <class name="com.cjs.qa.junit.tests.RegressionTests"/>
           </classes>
       </test>
   </suite>
   ```

3. **Add CI/CD job** (if needed):
   - Edit `.github/workflows/env-fe.yml`
   - Add new job following existing pattern
   - Set appropriate timeout

4. **Test locally**:
   ```bash
   ./mvnw -ntp test -DsuiteXmlFile=testng-regression-suite.xml
   ```

---

## Maven Configuration Updates

### File Location

`pom.xml` (lines 776-807)

### Step-by-Step: Change Default Parallel Configuration

**Scenario**: Increase default thread count from 5 to 10

1. **Open pom.xml**:
   ```bash
   vim pom.xml
   ```

2. **Locate maven-surefire-plugin configuration** (around line 780)

3. **Update threadCount**:
   ```xml
   <!-- Before -->
   <threadCount>5</threadCount>
   
   <!-- After -->
   <threadCount>10</threadCount>
   ```

4. **Update Cucumber parallelism** (if using Cucumber):
   ```xml
   <!-- Before -->
   <cucumber.execution.parallel.config.fixed.parallelism>5</cucumber.execution.parallel.config.fixed.parallelism>
   
   <!-- After -->
   <cucumber.execution.parallel.config.fixed.parallelism>10</cucumber.execution.parallel.config.fixed.parallelism>
   ```

5. **Test locally**:
   ```bash
   ./mvnw -ntp test
   ```

6. **Monitor resource usage**:
   - Check CPU and memory usage
   - Ensure tests don't fail due to resource constraints
   - **⚠️ Note**: Pipeline resources cannot be increased - optimize tests to work within existing constraints

### Step-by-Step: Change Parallel Strategy

**Scenario**: Change from method-level to class-level parallelization

1. **Open pom.xml**

2. **Update parallel attribute**:
   ```xml
   <!-- Before -->
   <parallel>methods</parallel>
   
   <!-- After -->
   <parallel>classes</parallel>
   ```

3. **Note**: This affects all TestNG suites unless they override with their own `parallel` attribute

4. **Test locally**:
   ```bash
   ./mvnw -ntp test
   ```

---

## Framework Configuration Updates

### Playwright Configuration

**File**: `playwright/playwright.config.ts`

#### Step-by-Step: Enable Parallel Execution in CI

**Scenario**: Enable parallel execution for Playwright tests in CI

1. **Open playwright.config.ts**

2. **Update workers configuration**:
   ```typescript
   // Before
   workers: process.env.CI ? 1 : undefined,
   
   // After
   workers: process.env.CI ? 4 : undefined,  // 4 workers in CI
   ```

3. **Test in CI**:
   - Push changes
   - Monitor CI/CD pipeline execution time
   - Check for resource constraints

#### Step-by-Step: Add Retry Configuration

**Scenario**: Increase retry count for flaky tests

1. **Open playwright.config.ts**

2. **Update retries**:
   ```typescript
   // Before
   retries: 1,
   
   // After
   retries: 2,  // Retry twice on failure
   ```

3. **Test locally**:
   ```bash
   cd playwright
   npm test
   ```

### Cypress Configuration

**File**: `cypress/cypress.config.ts`

#### Step-by-Step: Enable Parallel Execution

**Scenario**: Enable parallel execution for Cypress tests

1. **Open cypress.config.ts**

2. **Add parallel configuration** (requires Cypress Dashboard or custom setup):
   ```typescript
   export default defineConfig({
     e2e: {
       // ... existing config ...
       // Note: Parallel execution requires Cypress Dashboard or custom configuration
     }
   })
   ```

3. **Alternative**: Use `cypress-parallel` package or similar

**Note**: Cypress doesn't support native parallel execution without additional setup.

---

## CI/CD Configuration Updates

### File Location

`.github/workflows/env-fe.yml`

### Step-by-Step: Using Existing Workflow Inputs

**Scenario**: Configure version, retry count, and timeout for test runs

**Version Inputs** (✅ Already Implemented):
```yaml
# In calling workflow (ci.yml)
uses: ./.github/workflows/env-fe.yml
with:
  environment: 'test'
  java_version: '21'        # Optional: defaults to '21'
  python_version: '3.11'    # Optional: defaults to '3.11'
  node_version: '20'         # Optional: defaults to '20'
  test_retry_count: 2       # Optional: defaults to 1
  smoke_tests_timeout_minutes: 7  # Optional: defaults to 5
  # Resource Configuration
  maven_memory: '4096m'     # Optional: defaults to '2048m'
  allure_version: '2.26.0'  # Optional: defaults to '2.25.0'
  docker_shm_size: '4gb'   # Optional: defaults to '2gb'
```

### Step-by-Step: Add New Workflow Inputs (Parallel Execution - Planned)

**Scenario**: Add parallel execution inputs for all test types

> **Note**: Version, retry, and timeout inputs are already implemented. This section covers adding parallel execution inputs (planned).

1. **Open env-fe.yml**:
   ```bash
   vim .github/workflows/env-fe.yml
   ```

2. **Locate inputs section** (around line 5, after existing `enable_*_tests` inputs)

3. **Add timeout inputs** (already implemented, but shown for reference):
   ```yaml
   # Timeout Configuration - Minutes (all default to 5 minutes)
   smoke_tests_timeout_minutes:
     description: 'Timeout in minutes for Smoke Tests'
     type: number
     default: 5
   grid_tests_timeout_minutes:
     description: 'Timeout in minutes for Grid Tests'
     type: number
     default: 5
   mobile_tests_timeout_minutes:
     description: 'Timeout in minutes for Mobile Browser Tests'
     type: number
     default: 5
   responsive_tests_timeout_minutes:
     description: 'Timeout in minutes for Responsive Design Tests'
     type: number
     default: 5
   cypress_tests_timeout_minutes:
     description: 'Timeout in minutes for Cypress Tests'
     type: number
     default: 5
   playwright_tests_timeout_minutes:
     description: 'Timeout in minutes for Playwright Tests'
     type: number
     default: 5
   robot_tests_timeout_minutes:
     description: 'Timeout in minutes for Robot Framework Tests'
     type: number
     default: 5
   selenide_tests_timeout_minutes:
     description: 'Timeout in minutes for Selenide Tests'
     type: number
     default: 5
   vibium_tests_timeout_minutes:
     description: 'Timeout in minutes for Vibium Tests'
     type: number
     default: 5
   ```

4. **Add parallel execution inputs** (after timeout inputs):
   ```yaml
   # Parallel Execution Configuration - TestNG Suites
   smoke_tests_parallel:
     description: 'Parallel execution strategy for Smoke Tests (none, tests, methods, classes)'
     type: string
     default: 'none'
   smoke_tests_thread_count:
     description: 'Number of threads for Smoke Tests parallel execution'
     type: number
     default: 1
   grid_tests_parallel:
     description: 'Parallel execution strategy for Grid Tests'
     type: string
     default: 'tests'
   grid_tests_thread_count:
     description: 'Number of threads for Grid Tests parallel execution'
     type: number
     default: 3
   mobile_tests_parallel:
     description: 'Parallel execution strategy for Mobile Browser Tests'
     type: string
     default: 'none'
   mobile_tests_thread_count:
     description: 'Number of threads for Mobile Browser Tests parallel execution'
     type: number
     default: 1
   responsive_tests_parallel:
     description: 'Parallel execution strategy for Responsive Design Tests'
     type: string
     default: 'none'
   responsive_tests_thread_count:
     description: 'Number of threads for Responsive Design Tests parallel execution'
     type: number
     default: 1
   selenide_tests_parallel:
     description: 'Parallel execution strategy for Selenide Tests'
     type: string
     default: 'none'
   selenide_tests_thread_count:
     description: 'Number of threads for Selenide Tests parallel execution'
     type: number
     default: 1
   
   # Parallel Execution Configuration - Playwright
   playwright_tests_workers:
     description: 'Number of workers for Playwright Tests (CI only, local uses default)'
     type: number
     default: 1
   ```

5. **Update workflow steps to use inputs**:
   ```yaml
   # Example: Update smoke-tests job (current implementation)
   - name: Run Smoke Tests
     timeout-minutes: ${{ inputs.smoke_tests_timeout_minutes || 5 }}
     run: |
       chmod +x scripts/ci/run-maven-tests.sh
       ./scripts/ci/run-maven-tests.sh \
         "${{ inputs.environment }}" \
         "testng-smoke-suite.xml" \
         "${{ inputs.test_retry_count || 1 }}"
   
   # Example: With parallel execution (planned)
   - name: Run Smoke Tests
     timeout-minutes: ${{ inputs.smoke_tests_timeout_minutes || 5 }}
     env:
       PARALLEL_STRATEGY: ${{ inputs.smoke_tests_parallel || 'none' }}
       THREAD_COUNT: ${{ inputs.smoke_tests_thread_count || 1 }}
     run: |
       chmod +x scripts/ci/run-maven-tests.sh
       ./scripts/ci/run-maven-tests.sh \
         "${{ inputs.environment }}" \
         "testng-smoke-suite.xml" \
         "${{ inputs.test_retry_count || 1 }}" \
         "" \
         "-Dparallel=${{ inputs.smoke_tests_parallel || 'none' }} -DthreadCount=${{ inputs.smoke_tests_thread_count || 1 }}"
   ```

6. **For Playwright, update config**:
   ```yaml
   - name: Run Playwright Tests
     timeout-minutes: ${{ inputs.playwright_tests_timeout_minutes || 5 }}
     env:
       PLAYWRIGHT_WORKERS: ${{ inputs.playwright_tests_workers || 1 }}
     run: |
       cd playwright
       # Update playwright.config.ts or pass via environment variable
       workers=${{ inputs.playwright_tests_workers || 1 }} npm test
   ```

### Step-by-Step: Using CI/CD Scripts

**Available Scripts** (✅ Implemented):

1. **`scripts/ci/run-maven-tests.sh`** - Run Maven tests with configurable retry
2. **`scripts/ci/create-allure-env-properties.sh`** - Create Allure environment properties
3. **`scripts/ci/install-robot-framework.sh`** - Install Robot Framework dependencies
4. **`scripts/ci/verify-services.sh`** - Verify frontend/backend services are running
5. **`scripts/ci/wait-for-service.sh`** - Reusable utility for waiting for any service (used internally by other scripts)
6. **`scripts/ci/port-utils.sh`** - Reusable utility for port management (used internally by other scripts)

**Example: Using Allure Environment Properties Script**:
```yaml
- name: Create Allure Environment Properties
  run: |
    ./scripts/ci/create-allure-env-properties.sh \
      "${{ inputs.environment }}" \
      "${{ matrix.browser }}" \
      "${{ inputs.se_hub_port }}" \
      "${{ inputs.base_url }}" \
      "${{ inputs.test_suite }}" \
      "Grid.Type=Selenium Grid"
```

**Example: Using Service Verification Script**:
```yaml
- name: Verify Services Are Running
  if: inputs.base_url == 'http://localhost:3003' || ...
  run: |
    ./scripts/ci/verify-services.sh \
      "${{ inputs.base_url }}" \
      "${{ inputs.service_wait_timeout_seconds || 30 }}"
```

**Example: Using Robot Framework Installation Script**:
```yaml
- name: Install Robot Framework dependencies
  run: |
    ./scripts/ci/install-robot-framework.sh
```

**Example: Using Grid Wait with Timeout Input**:
```yaml
- name: Wait for Selenium Grid
  run: |
    ./scripts/ci/wait-for-grid.sh \
      "http://localhost:${{ inputs.se_hub_port }}/wd/hub/status" \
      "${{ inputs.grid_wait_timeout_seconds || 60 }}"
```

**Example: Using Service Waiting Utility Directly**:
```yaml
- name: Wait for Custom Service
  run: |
    ./scripts/ci/wait-for-service.sh \
      "http://localhost:8080/health" \
      "Custom Service" \
      60 \
      2
```

---

### Step-by-Step: Update Job Timeout (Using New Inputs)

**Scenario**: Use workflow input to set smoke tests timeout and retry count

1. **Open env-fe.yml** (or the calling workflow)

2. **Locate smoke-tests job** (around line 167)

3. **Update timeout-minutes to use input**:
   ```yaml
   # Before
   - name: Run Smoke Tests
     timeout-minutes: 5
   
   # After
   - name: Run Smoke Tests
     timeout-minutes: ${{ inputs.smoke_tests_timeout_minutes || 5 }}
   ```

4. **When calling workflow, specify timeout and retry count**:
   ```yaml
   # In calling workflow (ci.yml)
   uses: ./.github/workflows/env-fe.yml
   with:
     environment: 'test'
     test_retry_count: 2  # Retry failed tests twice
     smoke_tests_timeout_minutes: 7  # Increase timeout to 7 minutes
   ```

### Step-by-Step: Enable Parallel Execution via Input

**Scenario**: Use Maven test script with custom retry count

1. **Current implementation uses `scripts/ci/run-maven-tests.sh`**:
   ```yaml
   - name: Run Smoke Tests
     timeout-minutes: ${{ inputs.smoke_tests_timeout_minutes || 5 }}
     run: |
       chmod +x scripts/ci/run-maven-tests.sh
       ./scripts/ci/run-maven-tests.sh \
         "${{ inputs.environment }}" \
         "testng-smoke-suite.xml" \
         "${{ inputs.test_retry_count || 1 }}"
   ```

2. **Script supports additional Maven arguments**:
   ```bash
   ./scripts/ci/run-maven-tests.sh \
     "test" \
     "testng-smoke-suite.xml" \
     "2" \
     "" \
     "-Dcustom.property=value"
   ```

**Scenario**: Enable parallel execution for smoke tests via workflow input (Planned)

1. **Update workflow step to use parallel input** (when implemented):
   ```yaml
   - name: Run Smoke Tests
     timeout-minutes: ${{ inputs.smoke_tests_timeout_minutes || 5 }}
     env:
       PARALLEL_STRATEGY: ${{ inputs.smoke_tests_parallel || 'none' }}
       THREAD_COUNT: ${{ inputs.smoke_tests_thread_count || 1 }}
     run: |
       # Option 1: Pass as Maven properties (if suite XML doesn't override)
       ./mvnw -ntp test \
         -DsuiteXmlFile=testng-smoke-suite.xml \
         -Dparallel=${{ inputs.smoke_tests_parallel || 'none' }} \
         -DthreadCount=${{ inputs.smoke_tests_thread_count || 1 }}
       
       # Option 2: Dynamically modify suite XML (requires script)
       # Or use a template suite XML that supports dynamic parallel config
   ```

2. **When calling workflow, specify parallel settings**:
   ```yaml
   uses: ./.github/workflows/env-fe.yml
   with:
     environment: 'test'
     smoke_tests_parallel: 'tests'
     smoke_tests_thread_count: 3
   ```

**Note**: TestNG suite XML files may override Maven properties. If the suite XML has a hardcoded `parallel` attribute, you may need to:
- Use a script to dynamically modify the suite XML before running tests
- Create template suite XML files for different parallel configurations
- Use Maven profiles to select different suite files

### Step-by-Step: Add New Test Job

**Scenario**: Add a new test job for regression tests

1. **Open env-fe.yml**

2. **Find similar job** (e.g., `smoke-tests`) and copy its structure

3. **Create new job**:
   ```yaml
   regression-tests:
     name: Regression Tests (${{ inputs.environment }})
     runs-on: ubuntu-latest
     if: inputs.enable_regression_tests == true
     
     # ... copy structure from smoke-tests ...
     
     steps:
       # ... setup steps ...
       
       - name: Run Regression Tests
         timeout-minutes: 5
         run: |
           ./mvnw -ntp test -DsuiteXmlFile=testng-regression-suite.xml
   ```

4. **Add input parameter** (if needed):
   ```yaml
   inputs:
     enable_regression_tests:
       description: 'Enable Regression Tests'
       type: boolean
       default: false
   ```

5. **Update aggregation job** (if needed):
   - Add `regression-tests` to `needs:` list in `environment-allure-report` job

---

## Common Update Scenarios

### Scenario 1: Tests Timing Out

**Symptoms**: Tests pass but job times out

**Solution**:
1. Check actual test execution time
2. Increase timeout in CI/CD workflow
3. Consider enabling parallel execution if tests are sequential
4. Optimize slow tests

**Example**:
- Smoke tests take 250 seconds (4.17 minutes)
- Timeout is 5 minutes
- **Fix**: Enable parallel execution to reduce execution time, OR if parallel is not possible, increase timeout via workflow input (not hardcoded)

### Scenario 2: Tests Failing Due to Resource Constraints

**Symptoms**: Tests fail with memory/CPU errors when running in parallel

**Solution**:
1. Reduce thread count in suite XML
2. Reduce Maven Surefire thread count
3. Split tests into smaller suites
4. Disable parallel execution for problematic tests
5. Optimize tests to use less memory/CPU

**⚠️ Important**: Pipeline runners, memory, and other infrastructure resources **cannot be changed**. All solutions must work within existing resource constraints.

### Scenario 3: Flaky Tests in Parallel Execution

**Symptoms**: Tests pass sequentially but fail in parallel

**Solution**:
1. Check for shared state between tests
2. Ensure proper test isolation
3. Use `@BeforeMethod` and `@AfterMethod` for cleanup
4. Consider using `preserve-order="true"` for specific test groups
5. Add retry logic

### Scenario 4: Need Faster Test Execution

**Solution**:
1. Enable parallel execution in suite XML
2. Increase thread count (monitor resources - stay within existing limits)
3. Split large suites into smaller, parallel suites
4. Optimize slow tests
5. Use test sharding (split tests across multiple jobs)

**⚠️ Important**: Pipeline resources are fixed - optimize tests and parallelism within existing constraints.

---

## Validation Checklist

Before committing test suite changes:

### TestNG Suite Changes

- [ ] Suite XML is valid (no syntax errors)
- [ ] All referenced test classes exist
- [ ] Parallel configuration is appropriate for test isolation
- [ ] Thread count is reasonable (not too high)
- [ ] Tests run successfully locally
- [ ] CI/CD timeout is sufficient
- [ ] Documentation updated (if needed)

### Maven Configuration Changes

- [ ] `pom.xml` is valid XML
- [ ] Thread count doesn't exceed available resources (pipeline resources are fixed)
- [ ] Parallel strategy is appropriate
- [ ] All tests still pass
- [ ] No performance degradation
- [ ] Tests work within existing pipeline resource constraints
- [ ] Documentation updated

### Framework Configuration Changes

- [ ] Configuration file is valid
- [ ] Tests run successfully locally
- [ ] CI/CD execution is successful
- [ ] Performance impact is acceptable
- [ ] Documentation updated

### CI/CD Configuration Changes

- [ ] YAML syntax is valid
- [ ] Job names are unique
- [ ] Timeouts are appropriate
- [ ] Dependencies are correct
- [ ] Artifacts are uploaded correctly
- [ ] Tests run successfully in CI/CD
- [ ] Documentation updated

---

## Testing Changes

### Local Testing

```bash
# Test specific suite
./mvnw -ntp test -DsuiteXmlFile=testng-smoke-suite.xml

# Test all suites
./mvnw -ntp test

# Test Playwright
cd playwright && npm test

# Test Cypress
cd cypress && npm run cypress:run
```

### CI/CD Testing

1. **Create feature branch**:
   ```bash
   git checkout -b update/test-suite-config
   ```

2. **Make changes**

3. **Commit and push**:
   ```bash
   git add .
   git commit -m "Update test suite configuration"
   git push origin update/test-suite-config
   ```

4. **Monitor CI/CD pipeline**

5. **Review test results**

6. **Merge if successful**

---

## Troubleshooting

### Tests Not Running in Parallel

**Check**:
1. Suite XML has `parallel` attribute set
2. Thread count is > 1
3. Maven Surefire parallel configuration is correct
4. No conflicting configurations

### Timeout Issues

**Check**:
1. Actual test execution time
2. CI/CD timeout setting (can be adjusted via workflow inputs)
3. Resource constraints (pipeline resources are fixed - cannot be increased)
4. Test optimization opportunities
5. Parallel execution opportunities (within existing resource limits)

### Resource Exhaustion

**Check**:
1. Thread count is too high - reduce to stay within existing resource limits
2. Memory limits - optimize tests to use less memory
3. CPU usage - reduce parallelism if needed
4. Consider reducing parallelism or splitting tests

**⚠️ Important**: Pipeline resources (memory, CPU, runners) **cannot be increased**. All solutions must work within existing infrastructure constraints.

---

**Last Updated**: 2025-12-27  
**Maintained By**: Development Team  
**Review Frequency**: As needed when updating test configurations

