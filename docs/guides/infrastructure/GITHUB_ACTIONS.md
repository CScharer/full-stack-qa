# GitHub Actions CI/CD Pipeline

**Status**: ✅ Active (7-Stage Architecture)
**Workflow**: `.github/workflows/ci.yml`
**Last Updated**: 2025-12-31
**Version**: 3.5 - Performance Optimizations Complete

---

## 🎯 Overview

Automated CI/CD pipeline built on a **7-stage parallel architecture**. It features environment-aware testing (DEV → TEST → PROD), integrated performance testing (Gatling, JMeter, Locust) targeting internal service ports (8003/3003), and a robust **Fail-Fast Barrier Propagation** system that ensures failures in one environment correctly block all downstream activity.

> **Note**: For a complete, detailed reference of the current pipeline jobs and logic, please see the **[Pipeline Workflow Reference Guide](PIPELINE_WORKFLOW.md)**. For information about how test jobs are organized and grouped, see **[Workflow Test Organization](WORKFLOW_TEST_ORGANIZATION.md)**.

---

## 🚀 What Gets Automated

### **Triggers**
- ✅ Push to `main` or `develop` branches
- ✅ Pull requests targeting `main` or `develop`
- ✅ Manual workflow dispatch with environment/test-type overrides

### **7-Stage Architecture**

1.  **Stage 1: Detection & Determination** - Change detection and environment selection.
2.  **Stage 2: Shared Setup** - Build, Docker build, and code quality (Parallel).
3.  **Gate (SETUP)** - Stop/Go barrier for all subsequent tests.
4.  **Stage 3: DEV Environment** - UI and Performance tests (Parallel) + Gate.
5.  **Stage 4: TEST Environment** - UI and Performance tests (Parallel) + Gate (Waits for `gate-dev`).
6.  **Stage 5: PROD Environment** - UI tests + Gate (Waits for `gate-test`).
7.  **Stage 6: Combined Reporting** - Allure report generation and deployment.
8.  **Stage 7: Pipeline Summary** - Final execution summary.

---

## 📋 Fail-Fast Barrier Propagation

The pipeline uses a **Chain of Gates** architecture:
*   **Sequential Enforcement**: Stage 4 (TEST) strictly waits for the **DEV Gate** to pass. Stage 5 (PROD) strictly waits for the **TEST Gate** to pass.
*   **Deployment Blocking**: Deployments to any environment are only permitted if the corresponding environment's Gate is successful.
*   **Logic Integrity**: If a lower-tier Gate fails, all downstream testing and deployment jobs are automatically skipped via explicit result checks, preventing silent failure propagation.

---

## 📊 Performance Testing Integration

Performance tests are now a first-class citizen of the CI pipeline:
- **Tools**: Gatling, JMeter, and Locust.
- **Modes**: `smoke` (30s check) or `all` (full load).
- **Environment Aware**: Automatically runs in `dev` and `test` environments during `main` branch pushes.

---

## ✅ Artifacts & Reporting

| Artifact Name | Produced By | Purpose |
|---------------|-------------|---------|
| `*-results-{env}` | UI Tests | XML/JSON results and failure screenshots |
| `*-performance-results` | Performance Tests | Raw metrics from Locust/JMeter/Gatling |
| `allure-report-combined-*` | Reporting Job | Consolidated HTML report for all environments |

---

## 🔧 Troubleshooting

1.  **Gate Failure**: If a "Gate" job fails, it will list exactly which job (UI or Performance) caused the failure in its logs.
2.  **Skipped Jobs**: Jobs in later stages will automatically skip if a previous environment's gate fails or if the environment wasn't selected for the run.
3.  **Performance Result Missing**: The report job now handles missing performance results gracefully, only including them if they were actually executed.

---

## 📊 Code Quality Pipeline

### Code Quality Job

The `code-quality` job runs in **Stage 2** (Shared Setup) and executes in parallel with other setup jobs.

**What it does**:
- Runs Checkstyle verification (read-only)
- Runs PMD verification (read-only)
- Provides detailed violation reports if issues are found

**Script**: `scripts/ci/verify-code-quality.sh`

**Optimization**:
- ✅ **Read-only verification** - No file modifications in CI
- ✅ **No compilation duplication** - Uses existing compiled classes
- ✅ **Fast execution** - 20-50 seconds faster than full formatting
- ✅ **Detailed diagnostics** - Violation counts, file locations, rule types

### Formatting Verification

A separate workflow (`.github/workflows/verify-formatting.yml`) enforces formatting standards:
- Verifies code formatting with Spotless
- Checks for unused imports with Checkstyle
- Verifies PMD code quality
- Provides detailed violation information

**Benefits**:
- Server-side enforcement via GitHub branch protection
- Clear violation reporting
- Automatic blocking of non-compliant code

**See**: [Code Quality Guide](../java/CODE_QUALITY.md) for complete documentation

---

## 📦 Dependency Submission

### Python Dependency Submission

A dedicated workflow (`.github/workflows/dependency-submission.yml`) automatically submits Python dependencies to GitHub's dependency graph for security scanning and vulnerability detection.

**What it does**:
- Submits dependencies from `backend/requirements.txt` (FastAPI backend project)
- Submits dependencies from `requirements.txt` (performance testing tools)
- Runs automatically on push/PR when dependency files change
- Can be triggered manually via `workflow_dispatch`

**Benefits**:
- ✅ Automatic dependency tracking for security scanning
- ✅ Proper validation of Python projects in subdirectories
- ✅ Resolves GitHub's automatic dependency submission validation errors
- ✅ Separate tracking for backend and performance testing dependencies

**Workflow Structure**:
- **Job 1**: `submit-backend-dependencies` - Submits FastAPI backend dependencies
- **Job 2**: `submit-performance-dependencies` - Submits performance testing dependencies

**Triggers**:
- Push to `main` or `develop` branches (when dependency files change)
- Pull requests targeting `main` or `develop` (when dependency files change)
- Manual workflow dispatch

**Note**: This workflow replaces GitHub's automatic dependency submission, which was failing because it couldn't validate the repository root as a Python project. The custom workflow correctly targets the actual Python projects in their respective directories.

---

## ⚡ Performance Optimizations

The pipeline has been optimized for faster test execution through several improvements (completed 2025-12-31):

### Optimizations Implemented

1. **Optimized Timeouts** ✅
   - Grid wait timeout: Reduced from 60s to 5s
   - Service wait timeout: Reduced from 30s to 5s
   - Test-level timeouts: Element waits 5s, page loads 10s
   - **Savings**: ~3.5 minutes total across all jobs

2. **Parallel Service Startup** ✅
   - Backend and frontend services start concurrently
   - **Savings**: ~15-20 seconds per startup

3. **Dependency Caching** ✅
   - Frontend `node_modules` cached based on `package-lock.json`
   - Backend `venv` cached based on `requirements.txt`
   - **Savings**: ~10-15 seconds per job when cache hits

4. **Reduced Sleep Statements** ✅
   - Removed unnecessary fixed delays
   - Replaced with actual readiness checks
   - **Savings**: ~5-10 seconds per job

### Current Timeout Values

| Component | Timeout | Location |
|-----------|---------|----------|
| Grid Wait | 5 seconds | `scripts/ci/wait-for-grid.sh` |
| Service Wait | 5 seconds | `scripts/ci/wait-for-service.sh` |
| Element Wait | 5 seconds | Test code (Environment.java) |
| Page Load | 10 seconds | Test code (Environment.java) |
| Test Execution | 5 minutes | Workflow job timeout |

### Performance Impact

**Total Estimated Savings**: ~4-5 minutes across all test jobs

**Test Execution Times** (after optimizations):
- Fast tests (Cypress, Playwright, Robot, Vibium): < 2 minutes
- Maven/Grid tests: ~3-4 minutes (down from 4-5 minutes)

**Note**: Shared service startup was attempted but reverted because GitHub Actions jobs run on separate runners and cannot share services across jobs. Each test job starts its own services.

---

---

## 🔒 Security Scanning

### CodeQL Analysis

**Workflow**: `.github/workflows/codeql-analysis.yml`  
**Status**: ✅ Active  
**Last Updated**: 2025-12-31

**Features**:
- **Automated security scanning** for Java, JavaScript/TypeScript, and Python
- **Weekly scheduled scans** (Sundays at 14:00 UTC = 08:00 CST / 09:00 CDT)
- **Runs on push/PR** to `main` and `develop` branches
- **GitHub Copilot Autofix** enabled (AI-powered fix suggestions for vulnerabilities)
- **Results in Security tab** - View findings in GitHub Security dashboard

**Configuration**:
- Languages: Java, JavaScript (covers TypeScript), Python
- Matrix strategy for parallel analysis
- Results appear in GitHub Security tab
- Copilot Autofix provides automatic fix suggestions in PRs

**See Also**: 
- [PIPELINE_WORKFLOW.md](PIPELINE_WORKFLOW.md) - Definitive technical specification
- [WORKFLOW_TEST_ORGANIZATION.md](WORKFLOW_TEST_ORGANIZATION.md) - Test job grouping and organization
