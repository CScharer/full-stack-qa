---
**Type**: Guide
**Purpose**: Complete reference for GitHub Actions CI/CD pipeline workflow and job dependencies
**Created**: 2025-11-13
**Last Updated**: 2025-12-29
**Maintained By**: CJS QA Team
**Status**: Active
**Related To**: GITHUB_ACTIONS.md, CI_TROUBLESHOOTING.md, WORKFLOW_TEST_ORGANIZATION.md
**Version**: 3.4 - Performance Target Optimization
---

# CI/CD Pipeline Workflow Reference
## Complete Guide to GitHub Actions Pipeline

**Main Workflow**: `.github/workflows/ci.yml` (orchestrator)
**Reusable Workflows**: 
  - `.github/workflows/env-fe.yml` (per-environment UI testing)
  - `.github/workflows/env-be.yml` (per-environment performance testing)

**Note on Stability**: This pipeline uses standardized GitHub Action versions (`@v4` for checkout and artifacts, `@v5` for python) to ensure maximum reliability and compatibility with the GitHub artifact backend.

**Note on Internal Routing**: Tests target the local runner (`localhost`). To avoid routing conflicts and ensure accurate targeting:
- **Frontend** services run on ports `3003` (DEV) and `3004` (TEST).
- **Backend** services run on ports `8003` (DEV) and `8004` (TEST).
Performance tests are configured to hit these ports explicitly to ensure they are testing the correct service layer.

**Note on Gating**: We use a **Fail-Fast Barrier Propagation** architecture. Each environment's Gate (`gate-dev`, `gate-test`, etc.) not only monitors its own tests but also enforces the success of the previous environment's Gate. This prevents a failure in one environment from silently allowing tests or deployments in the next environment to proceed.

---

## 🏗️ PIPELINE ARCHITECTURE

The pipeline is organized into 7 distinct stages, designed for maximum parallelism and intelligent gating.

### **High-Level Flow Diagram**:
```
STAGE 1: DETECTION & DETERMINATION
│   detect-changes → determine-schedule-type
│   Note: determine-schedule-type consolidates code-changed status and schedule info
▼
STAGE 1 & 2: PARALLEL EXECUTION (After determine-schedule-type)
│   STAGE 1 (continued):
│   ├── setup-base-urls
│   ├── determine-environments
│   └── determine-test-execution
│   
│   STAGE 2 (SHARED SETUP - Parallel):
│   ├── docker-build (runs first, no dependencies)
│   ├── code-quality (runs in parallel)
│   ├── validate-versions (runs in parallel)
│   └── validate-test-data (runs in parallel)
│   
│   STAGE 2 (After docker-build completes):
│   └── build-and-compile (waits for docker-build, then runs)
▼
GATE (SETUP) - Single point of failure for setup jobs
▼
STAGE 3: DEV ENVIRONMENT (Parallel UI + Performance)
│   ├── test-be-dev
│   ├── test-fe-dev
│   └── gate-dev (Waits for all Stage 3 + gate-setup)
│       └── deploy-dev (if main & gate-dev success)
▼
STAGE 4: TEST ENVIRONMENT (Parallel UI + Performance)
│   │   (Waits for gate-dev)
│   ├── test-be-test
│   ├── test-fe-test
│   └── gate-test (Waits for all Stage 4 + gate-dev)
│       └── deploy-test (if main & gate-test success)
▼
STAGE 5: PROD ENVIRONMENT (UI Only)
│   │   (Waits for gate-test)
│   ├── test-fe-prod
│   └── gate-prod (Waits for Stage 5 + gate-test)
│       └── deploy-prod (if main & gate-prod success)
▼
STAGE 6: COMBINED REPORTING (Intelligent Skip)
│   ├── performance-allure-conversion
│   └── combined-allure-report (Artifacts + GitHub Pages on main)
▼
STAGE 7: PIPELINE SUMMARY
    └── pipeline-summary (Always runs)
```

---

## 🔔 WORKFLOW TRIGGERS

### **Automatic Runs**:
- **Push**: Only on `main` and `develop` branches (Industry Standard).
- **Pull Request**: On PRs targeting `main` or `develop`.
- **Schedule**: 
  - **Nightly**: Daily at 9 AM UTC (runs all test types, all test suites, smoke performance tests in DEV)
  - **Weekly**: Every Sunday at 9 AM UTC (runs all test types, all test suites, all performance tests in DEV)
- **Concurrency**: Only one run per branch/PR allowed at a time (older runs automatically cancelled).

### **Manual Trigger (workflow_dispatch)**:
1. Go to: **Actions** → **Selenium Grid CI/CD Pipeline**
2. Click **Run workflow**
3. Select branch and configure inputs:
   - `environment`: `all`, `dev`, `test`, or `prod`
   - `test_type`: `ui-only`, `performance-only`, or `all`
   - `test_suite`: `smoke`, `ci`, `extended`, or `all`
   - `performance_test_type`: `smoke` (default), `all`, or tool-specific

---

## 📋 JOB DESCRIPTIONS

### **STAGE 1: DETECTION & DETERMINATION**

#### **Job 1: detect-changes**
- **Purpose**: Detects if code files changed (skips tests if only documentation changed).
- **Dependencies**: None (runs first)
- **Output**: `code-changed` (true/false)
- **Logic**: 
  - For scheduled runs: Always returns `code-changed=true`
  - For other events: Uses `scripts/ci/detect-changes.sh` to check if non-documentation files changed

#### **Job 2: determine-schedule-type**
- **Purpose**: Consolidates code-changed status and determines schedule type (nightly/weekly) for scheduled runs.
- **Dependencies**: `detect-changes` (runs after detect-changes completes)
- **Outputs**: 
  - `code-changed`: Passes through from `detect-changes`
  - `is_weekly`: true if Sunday (weekly run), false otherwise
  - `is_nightly`: true if not Sunday (nightly run), false otherwise
  - `performance_test_type`: `all` for weekly (Sunday), `smoke` for nightly or non-scheduled runs
- **Logic**:
  - For scheduled runs: Determines if it's Sunday (weekly) or another day (nightly)
  - For non-scheduled runs: Sets defaults (is_weekly=false, is_nightly=false, performance_test_type=smoke)
- **Note**: All downstream jobs depend on this job to get both code-changed status and schedule information

#### **Job 3: setup-base-urls**
- **Purpose**: Sets up base URLs for each environment (DEV, TEST, PROD).
- **Dependencies**: None (runs in parallel with determine-schedule-type)
- **Outputs**: `base_url_dev`, `base_url_test`, `base_url_prod`
- **Execution**: Runs in parallel with `determine-schedule-type` and continues into the parallel group after it completes

#### **Job 4: determine-environments**
- **Purpose**: Sets environment defaults based on event type.
- **Dependencies**: `determine-schedule-type` (consolidated dependency)
- **Execution**: Runs in parallel with Stage 2 jobs after `determine-schedule-type` completes
- **Logic**: 
  - `pull_request` → `dev` only.
  - `push` (to main/develop) → `all` environments.
  - `schedule` → `dev` only with `all` test suites.
  - `manual` → Use user input.

#### **Job 5: determine-test-execution**
- **Purpose**: Sets test type defaults.
- **Dependencies**: `determine-schedule-type` (consolidated dependency)
- **Execution**: Runs in parallel with Stage 2 jobs after `determine-schedule-type` completes
- **Logic**:
  - `pull_request` → `all` tests (UI + Perf), defaults Perf to `smoke` in `dev`.
  - `push` (to `main`) → `all` tests (UI + Perf), defaults Perf to `smoke` in `dev` and `test` environments (never prod).
  - `push` (to `develop`) → `ui-only` across all environments.
  - `schedule` → `all` tests (UI + Perf), uses schedule-specific performance_test_type (smoke for nightly, all for weekly).
  - `manual` → `ui-only` across all environments (unless overridden by inputs).

---

### **STAGE 1 & 2: PARALLEL EXECUTION (After determine-schedule-type)**

**Note**: After `determine-schedule-type` completes, several jobs run in parallel. These include both Stage 1 completion jobs and Stage 2 setup jobs. All jobs depend on `determine-schedule-type` and only run if `code-changed == 'true'`.

#### **Execution Order**:

**Immediately after determine-schedule-type (Parallel Group 1)**:
- `setup-base-urls` (Stage 1)
- `determine-environments` (Stage 1)
- `determine-test-execution` (Stage 1)
- `docker-build` (Stage 2)
- `code-quality` (Stage 2)
- `validate-versions` (Stage 2)
- `validate-test-data` (Stage 2)

**After docker-build completes (Sequential)**:
- `build-and-compile` (Stage 2) - **Waits for docker-build**, then runs

---

### **STAGE 2: SHARED SETUP JOBS**

#### **Job 6: docker-build**
- **Dependencies**: `determine-schedule-type` only
- **Action**: Builds and verifies the test container (`docker compose build tests`).
- **Execution**: Runs in parallel with other Stage 2 jobs immediately after `determine-schedule-type`

#### **Job 7: build-and-compile** (Longest Job)
- **Dependencies**: `determine-schedule-type`, `docker-build`
- **Action**: JDK 21 setup, Maven compile, and artifact upload of `compiled-classes`.
- **Execution**: **Waits for docker-build to complete**, then runs. This is the only Stage 2 job that doesn't run in the initial parallel group.
- **Optimization**: The `compiled-classes` artifact is downloaded by test jobs to avoid redundant compilation, saving ~2.5-3.5 minutes per Java-based test job.

#### **Job 8: code-quality**
- **Dependencies**: `determine-schedule-type` only
- **Action**: Runs Checkstyle and PMD static analysis.
- **Execution**: Runs in parallel with other Stage 2 jobs immediately after `determine-schedule-type`

#### **Job 9: validate-versions**
- **Dependencies**: `determine-schedule-type` only
- **Action**: Validates dependency versions across the project (Selenium, TypeScript, Python) to ensure consistency.
- **Script**: `scripts/validate-dependency-versions.sh`
- **Execution**: Runs in parallel with `code-quality` and other Stage 2 jobs immediately after `determine-schedule-type`
- **Note**: Only reads configuration files (pom.xml, package.json, requirements.txt, workflow files) and doesn't require compiled classes.

#### **Job 10: validate-test-data**
- **Dependencies**: `determine-schedule-type` only
- **Action**: Validates JSON syntax and schema for all files in `test-data/`.
- **Execution**: Runs in parallel with other Stage 2 jobs immediately after `determine-schedule-type`

---

### **GATE: SETUP**
- **Job**: `gate-setup`
- **Logic**: Waits for ALL Stage 2 jobs. If any fail (except those marked `continue-on-error`), the entire pipeline stops here.

---

### **STAGE 3-5: ENVIRONMENT TESTING**

#### **UI Tests** (`test-dev-environment`, etc.)
- **Workflow**: Calls `.github/workflows/env-fe.yml`.
- **Parallelism**: Runs in parallel with performance tests in the same environment.

#### **Performance Tests** (`performance-dev`, etc.)
- **Workflow**: Calls `.github/workflows/env-be.yml`.
- **Types**: `all` (Gatling+JMeter+Locust) or `smoke` (Quick 30s check).

#### **Gating & Deployment** (`gate-dev`, `deploy-dev`, etc.)
- **Logic**: Each Gate job monitors its environment's UI and Performance tests.
- **Fail-Fast Barrier Propagation**: Stage 4 strictly waits for `gate-dev`. Stage 5 strictly waits for `gate-test`.
- **Skip Logic**: If a Gate fails, all subsequent environment tests and deployments are automatically skipped, even if they use `always()` (due to explicit result checks).
- **Deployment Safety**: `deploy-xxx` jobs only run if their environment's **Gate** has fully passed (`success`). This ensures that a performance failure correctly blocks a UI deployment.

---

### **STAGE 6: COMBINED REPORTING**

#### **Job: performance-allure-conversion**
- **Condition**: Only runs if performance tests were executed.
- **Action**: Converts raw results from Gatling/JMeter/Locust into Allure-compatible JSON.

#### **Job: combined-allure-report**
- **Condition**: Only runs if tests were executed and not cancelled.
- **Action**: Merges all UI and Performance results into one report.
- **Deployment**: Deploys to GitHub Pages ONLY on `main` branch.

---

### **STAGE 7: PIPELINE SUMMARY**

#### **Job: pipeline-summary**
- **Condition**: Always runs (`if: always()`).
- **Purpose**: Provides a clean visual summary of the entire run in the GitHub Actions UI.

---

## 📦 ARTIFACT MANAGEMENT

| Artifact Name | Produced By | Retention | Purpose |
|---------------|-------------|-----------|---------|
| `compiled-classes` | build-and-compile | 1 day | Shared classes for test jobs |
| `code-quality-reports` | code-quality | 1 day | Checkstyle/PMD results |
| `*-results-{env}` | test-{env} | 7 days | UI test result JSON/PNG |
| `*-performance-results` | performance-{env} | 7 days | Raw performance metrics |
| `performance-allure-results` | allure-conversion | 7 days | Processed performance JSON |
| `allure-report-combined-*` | combined-report | 7 days | Consolidated HTML report |

---

## 🔧 TROUBLESHOOTING

1. **Why did my job skip?**
   - Check `detect-changes` output. If you only changed markdown files, most jobs skip.
2. **Why didn't the report deploy?**
   - Live reports only deploy on merges to `main`. Download the `allure-report-combined` artifact for feature branches.
3. **Why did the gate fail?**
   - Gates check the explicit results of their prerequisite jobs. Check the "Gate" job logs for a list of failed dependencies.
