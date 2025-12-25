# Scheduled Jobs & Cron Jobs

**Date Created**: 2025-12-21  
**Status**: 📋 Living Document  
**Purpose**: Centralized documentation of all scheduled GitHub Actions workflows and cron jobs

### 🔑 Status Legend
- `[✅]` = Active / Working / Current
- `[❌]` = Inactive / Disabled / Removed
- `[🔍]` = Under Investigation / Needs Review
- `[⚠️]` = Warning / Issue Detected
- `[⏳]` = Pending / Planned
- `[⏭️]` = Skipped / Not Applicable

---

## 🎯 Overview

This document provides a centralized reference for all scheduled jobs (cron jobs) configured in GitHub Actions workflows. Scheduled jobs run automatically at specified times and are useful for periodic maintenance, monitoring, and validation tasks.

---

## 📋 Active Scheduled Jobs

### 1. Version Monitoring

**Workflow File**: `.github/workflows/version-monitoring.yml`  
**Status**: `[✅]` Active  
**Schedule**: Daily at 9 AM UTC (`cron: '0 9 * * *'`) - 3 AM Central Time (runs at same time as Nightly CI/CD runs)

**Purpose**: 
- Validates dependency versions across the project
- Detects version mismatches between different configuration files
- Generates JSON reports stored as artifacts (retained for 30 days)
- Sends alerts if version mismatches are detected

**What It Does**:
1. Runs `scripts/validate-dependency-versions.sh`
2. Validates Selenium versions across `pom.xml` and workflow files
3. Checks TypeScript and `@types/node` versions across Node.js projects
4. Validates Python requirements files
5. Generates JSON report with results
6. Fails workflow if version mismatches detected (triggers GitHub notifications)

**Manual Trigger**: 
- Can be triggered manually via `workflow_dispatch` in GitHub Actions UI
- Or run locally: `./scripts/validate-dependency-versions.sh --report-json --report-file results/report.json`

**Accessing Reports**:
1. Go to GitHub Actions tab
2. Select "Version Monitoring (Scheduled)" workflow
3. Click on a completed run
4. Download "version-validation-report" artifact

**Documentation**: 
- See [Version Monitoring & Alerting](VERSION_MONITORING.md) for detailed documentation

---

### 2. CI/CD Pipeline - Nightly Run

**Workflow File**: `.github/workflows/ci.yml`  
**Status**: `[✅]` Active  
**Schedule**: Daily at 9 AM UTC (`cron: '0 9 * * *'`) - 3 AM Central Time

**Purpose**: 
- Runs comprehensive test suite on a daily basis
- Validates code quality and functionality in DEV environment
- Ensures continuous integration health

**What It Does**:
1. Runs full CI/CD pipeline triggered by schedule
2. Executes in **DEV environment only**
3. Runs **all test types** (UI + Performance tests)
4. Runs **all test suites** (smoke, ci, extended, all)
5. Runs **smoke performance tests only** (quick validation)
6. Includes all standard pipeline stages: build, test, quality checks

**Configuration**:
- **Environment**: `dev` (fixed for scheduled runs)
- **Test Type**: `all` (UI + Performance)
- **Test Suite**: `all` (all test suites)
- **Performance Test Type**: `smoke` (quick performance validation)

**Manual Trigger**: 
- Can be triggered manually via `workflow_dispatch` in GitHub Actions UI
- Select environment: `dev`, test_type: `all`, test_suite: `all`, performance_test_type: `smoke`

---

### 3. CI/CD Pipeline - Weekly Run

**Workflow File**: `.github/workflows/ci.yml`  
**Status**: `[✅]` Active  
**Schedule**: Every Sunday at 9 AM UTC (`cron: '0 9 * * 0'`) - 3 AM Central Time

**Purpose**: 
- Runs comprehensive test suite on a weekly basis
- Executes full performance test suite (not just smoke tests)
- Provides thorough validation of system health

**What It Does**:
1. Runs full CI/CD pipeline triggered by schedule
2. Executes in **DEV environment only**
3. Runs **all test types** (UI + Performance tests)
4. Runs **all test suites** (smoke, ci, extended, all)
5. Runs **all performance tests** (Gatling + JMeter + Locust - full suite)
6. Includes all standard pipeline stages: build, test, quality checks

**Configuration**:
- **Environment**: `dev` (fixed for scheduled runs)
- **Test Type**: `all` (UI + Performance)
- **Test Suite**: `all` (all test suites)
- **Performance Test Type**: `all` (full performance test suite)

**Manual Trigger**: 
- Can be triggered manually via `workflow_dispatch` in GitHub Actions UI
- Select environment: `dev`, test_type: `all`, test_suite: `all`, performance_test_type: `all`

**Note**: This job automatically detects if it's Sunday and runs with full performance tests. On other days, the nightly job runs with smoke performance tests only.

---

## 📊 Scheduled Jobs Summary

| Job Name | Workflow File | Schedule | Status | Purpose |
|----------|--------------|----------|--------|---------|
| Version Monitoring | `version-monitoring.yml` | Daily at 9 AM UTC | `[✅]` Active | Dependency version validation |
| CI/CD Nightly Run | `ci.yml` | Daily at 9 AM UTC | `[✅]` Active | Full test suite (smoke performance) |
| CI/CD Weekly Run | `ci.yml` | Sunday at 9 AM UTC | `[✅]` Active | Full test suite (all performance) |

---

## 🔍 Verification

### How to Verify Scheduled Jobs

**Check Workflow Files**:
```bash
# List all workflow files
ls -la .github/workflows/*.yml

# Search for scheduled workflows
grep -r "schedule:" .github/workflows/
```

**Check GitHub Actions**:
1. Go to GitHub repository → Actions tab
2. Look for workflows with "Scheduled" runs
3. Check workflow history for execution times

**Verify Cron Syntax**:
- GitHub Actions uses standard cron syntax: `minute hour day month day-of-week`
- Example: `'0 9 * * *'` = Every day at 9:00 AM UTC
- [Cron Expression Validator](https://crontab.guru/)

---

## 📝 Adding New Scheduled Jobs

### Guidelines

When adding a new scheduled job:

1. **Create Workflow File**: Add new workflow file in `.github/workflows/`
2. **Define Schedule**: Use `schedule:` trigger with cron expression
3. **Add Manual Trigger**: Include `workflow_dispatch` for manual execution
4. **Update This Document**: Add entry to "Active Scheduled Jobs" section
5. **Document Purpose**: Clearly document what the job does and why
6. **Consider Resources**: Scheduled jobs consume GitHub Actions minutes
7. **Test First**: Test manually before enabling schedule

### Example Template

```yaml
name: Your Scheduled Job Name

on:
  schedule:
    # Run daily at 9 AM UTC (3 AM Central Time)
    - cron: '0 9 * * *'
  workflow_dispatch: # Allow manual triggering

jobs:
  your-job:
    name: Your Job Description
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      # Add your steps here
```

---

## ⚠️ Important Notes

### Performance Tests
- **Performance tests ARE scheduled** as part of the CI/CD pipeline scheduled runs
- Nightly runs execute **smoke performance tests only** (quick validation)
- Weekly runs execute **all performance tests** (full suite: Gatling + JMeter + Locust)
- Performance tests also run conditionally based on PRs/pushes to main
- Performance tests use reusable workflow `env-be.yml` called by `ci.yml`
- See [Document and Cron Job Cleanup](../cleanup/20251221_DOCUMENT_AND_CRON_JOB_CLEANUP.md) for details

### Resource Considerations
- Scheduled jobs consume GitHub Actions minutes
- Free tier: 2,000 minutes/month
- Consider frequency and duration when adding new scheduled jobs
- Monitor usage in GitHub Settings → Billing & plans → Actions

### Timezone
- All cron schedules use UTC timezone
- Convert to your local timezone when planning schedules
- Example: `'0 9 * * *'` (9 AM UTC) = 4 AM EST or 1 AM PST (same day)
- Note: Version Monitoring and CI/CD Nightly runs both execute at 9 AM UTC

---

## 🔗 Related Documents

- [Version Monitoring & Alerting](VERSION_MONITORING.md) - Detailed documentation for version monitoring
- [Document and Cron Job Cleanup](../cleanup/20251221_DOCUMENT_AND_CRON_JOB_CLEANUP.md) - Investigation and cleanup of workflow references
- [GitHub Actions CI/CD Pipeline](../guides/infrastructure/GITHUB_ACTIONS.md) - Main CI/CD pipeline documentation
- [Pipeline Workflow Reference](../guides/infrastructure/PIPELINE_WORKFLOW.md) - Detailed pipeline job reference

---

## 🛠️ Maintenance

### Updating Schedules

To change when a scheduled job runs:

1. Edit the workflow file (e.g., `.github/workflows/version-monitoring.yml`)
2. Update the cron expression in the `schedule:` section
3. Test manually using `workflow_dispatch`
4. Update this document with new schedule
5. Commit and push changes

### Disabling Scheduled Jobs

To temporarily disable a scheduled job:

1. Comment out the `schedule:` section in the workflow file
2. Or remove the workflow file entirely (if no longer needed)
3. Update this document to mark job as `[❌]` Inactive or `[🔍]` Under Investigation
4. Document reason for disabling

### Monitoring Scheduled Jobs

- Check GitHub Actions dashboard regularly for failures
- Review artifact reports (if generated)
- Monitor GitHub Actions usage/billing
- Set up notifications for workflow failures

---

**Last Updated**: 2025-12-21  
**Status**: 📋 Living Document

---

## 📝 Recent Changes

### 2025-12-21: Added CI/CD Scheduled Runs
- Added Nightly CI/CD run (daily at 9 AM UTC) with smoke performance tests
- Added Weekly CI/CD run (Sunday at 9 AM UTC) with full performance test suite
- Both runs execute in DEV environment with all test types and all test suites
- See `.github/workflows/ci.yml` for implementation details

### 2025-12-21: Updated Version Monitoring Schedule
- Changed Version Monitoring schedule from 2 AM UTC to 9 AM UTC (3 AM Central Time)
- Now runs at the same time as Nightly CI/CD runs for coordinated execution
- Updated all documentation to reflect the new schedule time
