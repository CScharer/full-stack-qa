# GitHub Actions CI/CD Pipeline

**Status**: ✅ Active (7-Stage Architecture)
**Workflow**: `.github/workflows/ci.yml`
**Last Updated**: December 18, 2025
**Version**: 3.4 - Performance Target Optimization

---

## 🎯 Overview

Automated CI/CD pipeline built on a **7-stage parallel architecture**. It features environment-aware testing (DEV → TEST → PROD), integrated performance testing (Gatling, JMeter, Locust) targeting internal service ports (8003/3003), and a robust **Fail-Fast Barrier Propagation** system that ensures failures in one environment correctly block all downstream activity.

> **Note**: For a complete, detailed reference of the current pipeline jobs and logic, please see the **[Pipeline Workflow Reference Guide](PIPELINE_WORKFLOW.md)**.

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

**See Also**: [PIPELINE_WORKFLOW.md](PIPELINE_WORKFLOW.md) for the definitive technical specification.
