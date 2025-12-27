# Workflow Test Organization

> **Living Document** - This document explains how test jobs are organized and grouped in the GitHub Actions workflow, including the rationale for the grouping structure.

**Last Updated**: 2025-12-27  
**Related Files**: 
- `.github/workflows/env-fe.yml` (reusable workflow for frontend tests)
- `.github/workflows/ci.yml` (main orchestrator workflow)

---

## 📋 Table of Contents

- [Overview](#overview)
- [Test Job Groups](#test-job-groups)
- [Why Tests Are Grouped This Way](#why-tests-are-grouped-this-way)
- [Test Result Reporting](#test-result-reporting)
- [Visual Grouping in GitHub Actions](#visual-grouping-in-github-actions)

---

## Overview

The frontend test workflow (`.github/workflows/env-fe.yml`) organizes test jobs into logical groups based on their execution methods, infrastructure requirements, and framework characteristics. This organization helps:

- **Understand test architecture** - Clear separation between different test execution methods
- **Troubleshoot issues** - Quickly identify which group a failing test belongs to
- **Optimize resources** - Understand resource requirements for each test type
- **Maintain consistency** - Similar tests are grouped together

**Key Principle**: All test jobs run in **parallel** (no dependencies between test jobs). The grouping is organizational, not functional.

---

## Test Job Groups

### Group 1: Backend Tests

**Job**: `Test BE ({env}) / BE Tests ({env})`

- **Workflow**: `env-be.yml` (reusable workflow)
- **Framework**: Gatling, JMeter, Locust (Performance/Load testing)
- **Execution**: Independent, runs in parallel with FE tests
- **Infrastructure**: Backend services only

---

### Group 2: Grid Tests (Matrix)

**Job**: `Test FE ({env}) / Grid Tests ({env})`

- **Matrix Strategy**: `chrome`, `firefox`, `edge` (3 parallel jobs)
- **Framework**: TestNG (Java/Maven)
- **Infrastructure**: Selenium Grid (Docker services)
- **Execution**: Parallel matrix execution (3 jobs per environment)

---

### Group 3: Selenium Grid + Maven Tests

**Jobs**:
- `Test FE ({env}) / Smoke Tests ({env})`
- `Test FE ({env}) / Mobile Browser Tests ({env})`
- `Test FE ({env}) / Responsive Design Tests ({env})`
- `Test FE ({env}) / Selenide Tests ({env})`
- `Test FE ({env}) / Vibium Tests ({env})`

**Common Characteristics**:
- ✅ All use **Selenium Grid** (Docker services: `selenium-hub`, `chrome-node`)
- ✅ All use **Maven/Java** (`run-maven-tests.sh` script)
- ✅ All use **TestNG** test suites
- ✅ All have similar job structure and dependencies
- ✅ All start backend/frontend services via `start-services-for-ci.sh`

**Execution Method**: Maven Surefire Plugin with TestNG

---

### Group 4: Alternative Execution Methods

**Jobs**:
- `Test FE ({env}) / Cypress Tests ({env})`
- `Test FE ({env}) / Playwright Tests ({env})`
- `Test FE ({env}) / Robot Tests ({env})`

**Common Characteristics**:
- ✅ **Cypress**: Does NOT use Selenium Grid, uses Node.js/npm directly
- ✅ **Playwright**: Does NOT use Selenium Grid, uses Node.js/npm directly
- ✅ **Robot**: Uses Selenium Grid BUT runs via **Python** (not Maven)
- ✅ All start their own backend/frontend services

**Execution Methods**:
- **Cypress**: `npm run cypress:run` (Node.js/JavaScript)
- **Playwright**: `npm test` (Node.js/TypeScript)
- **Robot**: `python -m robot.run` (Python)

---

## Why Tests Are Grouped This Way

### Technical Differences

#### 1. Execution Method

| Group | Execution Tool | Language/Runtime |
|-------|---------------|------------------|
| Group 3 | Maven (`run-maven-tests.sh`) | Java (TestNG) |
| Group 4 - Cypress | npm (`npm run cypress:run`) | Node.js/JavaScript |
| Group 4 - Playwright | npm (`npm test`) | Node.js/TypeScript |
| Group 4 - Robot | Python (`python -m robot.run`) | Python |

#### 2. Selenium Grid Dependency

| Test Type | Uses Selenium Grid? | Grid Configuration |
|-----------|-------------------|-------------------|
| Smoke, Mobile, Responsive, Selenide, Vibium | ✅ Yes | `services:` section with `selenium-hub` and `chrome-node` |
| Cypress | ❌ No | N/A - Uses built-in browser automation |
| Playwright | ❌ No | N/A - Uses built-in browser automation |
| Robot | ✅ Yes | `services:` section with `selenium-hub` and `chrome-node` |

#### 3. Resource Requirements

- **Group 3**: Requires Docker services (Selenium Grid) + Java/Maven
- **Group 4 - Cypress/Playwright**: Requires Node.js, no Docker services
- **Group 4 - Robot**: Requires Docker services + Python

#### 4. Service Management

- **Group 3**: All start backend/frontend services via `start-services-for-ci.sh`, then use Selenium Grid
- **Group 4 - Cypress/Playwright**: Start their own services, don't need Selenium Grid
- **Group 4 - Robot**: Starts services AND uses Selenium Grid, but runs via Python

---

## Test Result Reporting

### Test Summary Job

**Job Name**: `environment-test-summary`  
**Location**: `.github/workflows/env-fe.yml` (line 1160)

**Purpose**: Provides a comprehensive test result summary for all test frameworks in the GitHub Actions UI.

**Includes**:
- ✅ **Group 3 Tests** (Selenium Grid + Maven): Smoke, Grid, Mobile, Responsive, Selenide, Vibium
- ✅ **Group 4 Tests** (Alternative Frameworks): Cypress, Playwright, Robot Framework

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

**Dependencies**: Waits for all test jobs to complete (Groups 3 + 4)

### Allure Report Job

**Job Name**: `environment-allure-report`  
**Location**: `.github/workflows/env-fe.yml` (line 1082)

**Purpose**: Generates comprehensive Allure HTML reports for all test frameworks.

**Includes**: All test frameworks (Groups 3 + 4)

**Output**: 
- HTML report with detailed test results
- Screenshots, logs, and attachments
- Test execution timeline
- Environment-specific reports per environment (dev, test, prod)

**Deployment**: 
- Artifacts uploaded for each environment
- Combined report generated in main workflow (`combined-allure-report`)
- GitHub Pages deployment (main branch only)

---

## Visual Grouping in GitHub Actions

### Current Grouping Behavior

**Update (2025-12-27)**: After updating the `environment-test-summary` job to include Group 4 tests, GitHub Actions now visually groups **all test jobs together** (Groups 3 + 4 appear in the same visual group).

### Why Groups Are Now Together

GitHub Actions UI groups jobs visually based on:

1. **Job Dependencies** (`needs:`): 
   - All test jobs have NO dependencies (run in parallel)
   - **Key Change**: The `environment-test-summary` job now depends on **all test jobs** (Groups 3 + 4)
   - This dependency relationship causes GitHub Actions to group all dependent jobs together visually

2. **Job Characteristics**:
   - **Group 3**: All use `services:` (Selenium Grid), Maven, similar structure
   - **Group 4**: Different execution methods, different infrastructure needs
   - However, when a reporting job depends on all of them, they appear grouped together

3. **Resource Requirements**:
   - **Group 3**: Requires Docker services (Selenium Grid) + Java/Maven
   - **Group 4 - Cypress/Playwright**: Requires Node.js, no Docker services
   - **Group 4 - Robot**: Requires Docker services + Python
   - These differences still exist but don't affect visual grouping when all are dependencies of the same job

### Important Notes

- ✅ **Visual grouping does NOT affect execution** - All tests run in parallel regardless of visual grouping
- ✅ **Visual grouping changed** - After adding Group 4 to `test-summary` dependencies, all tests now appear in the same visual group
- ✅ **Both reporting jobs include all test frameworks** (Groups 3 + 4)
- ✅ **The technical differences remain** - Groups 3 and 4 still have different execution methods and infrastructure requirements, but they're now visually grouped together

---

## Summary

**Test Organization Principles**:

1. **Group 3 (Selenium Grid + Maven)**: Tests that use Selenium Grid infrastructure and Maven/Java execution
2. **Group 4 (Alternative Frameworks)**: Tests that use different execution methods (npm/Python) or don't require Selenium Grid

**Key Points**:

- All test jobs run in **parallel** (no dependencies between test jobs)
- **Visual grouping**: After updating `test-summary` to include all tests, Groups 3 and 4 now appear together in GitHub Actions UI
- Both **Test Summary** and **Allure Report** include all test frameworks (Groups 3 + 4)
- Technical differences between groups remain (execution methods, infrastructure), but they're now visually grouped together

**Related Documentation**:
- [Test Suites Reference](../testing/TEST_SUITES_REFERENCE.md) - Detailed test suite configurations
- [Pipeline Workflow](PIPELINE_WORKFLOW.md) - Complete pipeline architecture
- [GitHub Actions](GITHUB_ACTIONS.md) - CI/CD pipeline overview

---

**Last Updated**: 2025-12-27  
**Maintained By**: Development Team  
**Review Frequency**: When test job organization changes

