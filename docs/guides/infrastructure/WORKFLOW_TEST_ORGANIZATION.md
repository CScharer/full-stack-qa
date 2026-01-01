# Workflow Test Organization

> **Living Document** - This document explains how test jobs are organized and grouped in the GitHub Actions workflow, including the rationale for the grouping structure.

**Last Updated**: 2025-12-31  
**Related Files**: 
- `.github/workflows/env-fe.yml` (reusable workflow for frontend tests)
- `.github/workflows/env-be.yml` (reusable workflow for backend/API performance tests)
- `.github/workflows/env-artillery.yml` (reusable workflow for full-stack browser load tests)
- `.github/workflows/ci.yml` (main orchestrator workflow)

---

## 📋 Table of Contents

- [Overview](#overview)
- [Reusable Workflows Comparison](#reusable-workflows-comparison)
- [Test Job Groups](#test-job-groups)
- [Technical Differences Between Test Categories](#technical-differences-between-test-categories)
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

## Reusable Workflows Comparison

The CI/CD pipeline uses three reusable workflows for environment-specific testing. Each serves a distinct purpose:

| Aspect | BE Reusable<br/>(`env-be.yml`) | FE Reusable<br/>(`env-fe.yml`) | FS Reusable<br/>(`env-artillery.yml`) |
|--------|-------------------------------|-------------------------------|--------------------------------------|
| **Purpose** | Protocol-Level Performance Testing | Functional Testing | Browser-Based Load Testing |
| **Workflow Name** | Test Single Environment (BE Reusable) | Test Single Environment (FE Reusable) | Test Single Environment (FS Reusable) |
| **Uses Browsers?** | ❌ No | ✅ Yes (for functionality) | ✅ Yes (for performance) |
| **Test Focus** | API endpoints, database performance, HTTP/HTTPS protocol | UI behavior, user workflows, feature correctness | Full stack (frontend + backend) under load with real browser rendering |
| **Frameworks/Tools** | Gatling, JMeter, Locust | Selenium, Cypress, Playwright, Robot, Selenide, Vibium | Artillery + Playwright |
| **Execution Level** | Protocol layer (HTTP/HTTPS requests) | Application layer (browser interactions) | Browser layer (real rendering + JavaScript) |
| **Metrics Collected** | Response times, throughput, concurrent users | Test pass/fail, screenshots, logs | Core Web Vitals (LCP, FID, CLS, FCP), page load times, browser metrics |
| **Primary Question** | How fast/stable are the APIs under load? | Does it work correctly? | How does the app perform in real browsers under load? |
| **Service Requirements** | Frontend + Backend | Frontend + Backend + Selenium Grid (for some tests) | Frontend + Backend |
| **Environments** | dev, test | dev, test, prod | dev, test |
| **Language/Runtime** | Java (Gatling/JMeter), Python (Locust) | Java (TestNG), Node.js, Python | Node.js (Artillery + Playwright) |

### Key Distinctions

- **BE Reusable**: Tests **API performance** - measures protocol-level performance without browsers
- **FE Reusable**: Tests **functionality** - verifies features work as expected
- **FS Reusable**: Tests **browser performance** - measures full-stack performance with real browser rendering

**Note**: The FS (Full-Stack) Reusable workflow was previously named "Artillery Reusable" but was renamed to better reflect its purpose of testing the entire application stack (frontend + backend) under browser load conditions.

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

### Group 3: Frontend Tests (All Frameworks)

**Visual Grouping**: All frontend test jobs appear together in GitHub Actions UI as a single visual group.

**All Jobs in Group 3**:
- `Test FE ({env}) / Smoke Tests ({env})`
- `Test FE ({env}) / Mobile Browser Tests ({env})`
- `Test FE ({env}) / Responsive Design Tests ({env})`
- `Test FE ({env}) / Selenide Tests ({env})`
- `Test FE ({env}) / Vibium Tests ({env})`
- `Test FE ({env}) / Cypress Tests ({env})`
- `Test FE ({env}) / Playwright Tests ({env})`
- `Test FE ({env}) / Robot Tests ({env})`

**Why They're Grouped Together**: The `environment-test-summary` job depends on all of these test jobs, causing GitHub Actions to visually group them together.

#### Technical Categories (Within Group 3)

While all tests appear in the same visual group, they can be categorized by their technical characteristics:

##### Category A: Selenium Grid + Maven Tests

**Tests**: Smoke, Mobile Browser, Responsive Design, Selenide, Vibium

**Common Characteristics**:
- ✅ All use **Selenium Grid** (Docker services: `selenium-hub`, `chrome-node`)
- ✅ All use **Maven/Java** (`run-maven-tests.sh` script)
- ✅ All use **TestNG** test suites
- ✅ All have similar job structure and dependencies
- ✅ All start backend/frontend services via `start-services-for-ci.sh`

**Execution Method**: Maven Surefire Plugin with TestNG

##### Category B: Alternative Execution Methods

**Tests**: Cypress, Playwright, Robot Framework

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

## Technical Differences Between Test Categories

While all frontend tests appear in the same visual group (Group 3), they have distinct technical characteristics that are important to understand:

### 1. Execution Method

| Category | Test Types | Execution Tool | Language/Runtime |
|----------|-----------|---------------|------------------|
| Category A (Selenium Grid + Maven) | Smoke, Mobile, Responsive, Selenide, Vibium | Maven (`run-maven-tests.sh`) | Java (TestNG) |
| Category B - Cypress | Cypress | npm (`npm run cypress:run`) | Node.js/JavaScript |
| Category B - Playwright | Playwright | npm (`npm test`) | Node.js/TypeScript |
| Category B - Robot | Robot Framework | Python (`python -m robot.run`) | Python |

### 2. Selenium Grid Dependency

| Test Type | Uses Selenium Grid? | Grid Configuration |
|-----------|-------------------|-------------------|
| Smoke, Mobile, Responsive, Selenide, Vibium | ✅ Yes | `services:` section with `selenium-hub` and `chrome-node` |
| Cypress | ❌ No | N/A - Uses built-in browser automation |
| Playwright | ❌ No | N/A - Uses built-in browser automation |
| Robot | ✅ Yes | `services:` section with `selenium-hub` and `chrome-node` |

### 3. Resource Requirements

- **Category A (Selenium Grid + Maven)**: Requires Docker services (Selenium Grid) + Java/Maven
- **Category B - Cypress/Playwright**: Requires Node.js, no Docker services
- **Category B - Robot**: Requires Docker services + Python

### 4. Service Management

- **Category A (Selenium Grid + Maven)**: All start backend/frontend services via `start-services-for-ci.sh`, then use Selenium Grid
- **Category B - Cypress/Playwright**: Start their own services, don't need Selenium Grid
- **Category B - Robot**: Starts services AND uses Selenium Grid, but runs via Python

### Why These Differences Matter

Understanding these technical differences is important for:
- **Troubleshooting**: Knowing which infrastructure a test uses helps diagnose failures
- **Resource Planning**: Different tests require different resources (Docker, Java, Node.js, Python)
- **Maintenance**: Tests with similar execution methods share similar maintenance needs
- **Optimization**: Understanding execution methods helps optimize test performance

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

---

## Visual Grouping in GitHub Actions

### Current Visual Grouping

**Current State (2025-12-27)**: All frontend test jobs appear together in a single visual group (Group 3) in the GitHub Actions UI.

### Why All Tests Appear Together

GitHub Actions UI groups jobs visually based on:

1. **Job Dependencies** (`needs:`): 
   - All test jobs have NO dependencies between them (run in parallel)
   - **Key Factor**: The `environment-test-summary` job depends on **all frontend test jobs**
   - This dependency relationship causes GitHub Actions to group all dependent jobs together visually

2. **Shared Reporting Dependency**:
   - When multiple jobs are dependencies of the same reporting job, they tend to be grouped together
   - Both `environment-test-summary` and `environment-allure-report` depend on all frontend tests
   - This creates a visual grouping of all frontend test jobs

### Important Notes

- ✅ **Visual grouping does NOT affect execution** - All tests run in parallel regardless of visual grouping
- ✅ **Visual grouping is unified** - All frontend tests appear in Group 3 visually
- ✅ **Technical differences remain** - Tests still have distinct execution methods and infrastructure requirements (see [Technical Differences](#technical-differences-between-test-categories) section)
- ✅ **Both reporting jobs include all test frameworks** - Test Summary and Allure Report aggregate results from all frontend tests
- ✅ **Understanding technical categories is still important** - Even though visually grouped, knowing the technical differences helps with troubleshooting and optimization

---

## Summary

**Visual Organization**:

- **Group 3**: All frontend test jobs appear together in a single visual group in GitHub Actions UI
- This unified grouping is due to shared dependencies on reporting jobs (`test-summary` and `allure-report`)

**Technical Categories** (within Group 3):

1. **Category A (Selenium Grid + Maven)**: Tests that use Selenium Grid infrastructure and Maven/Java execution
   - Smoke, Mobile Browser, Responsive Design, Selenide, Vibium
2. **Category B (Alternative Frameworks)**: Tests that use different execution methods (npm/Python) or don't require Selenium Grid
   - Cypress, Playwright, Robot Framework

**Key Points**:

- All test jobs run in **parallel** (no dependencies between test jobs)
- **Visual grouping**: All frontend tests appear together in Group 3
- **Technical differences**: Tests still have distinct execution methods and infrastructure requirements (see Technical Differences section)
- Both **Test Summary** and **Allure Report** include all test frameworks
- Understanding technical categories helps with troubleshooting, resource planning, and optimization

**Related Documentation**:
- [Test Suites Reference](../testing/TEST_SUITES_REFERENCE.md) - Detailed test suite configurations
- [Pipeline Workflow](PIPELINE_WORKFLOW.md) - Complete pipeline architecture
- [GitHub Actions](GITHUB_ACTIONS.md) - CI/CD pipeline overview

---

**Last Updated**: 2025-12-31  
**Maintained By**: Development Team  
**Review Frequency**: When test job organization changes

