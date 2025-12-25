# Version Monitoring & Alerting

**Date Created**: 2025-12-20  
**Status**: 📋 Living Document  
**Purpose**: Document version monitoring and alerting capabilities

---

## 🎯 Overview

This document describes the automated version monitoring and alerting system implemented to prevent version drift across the project.

---

## 🔧 Components

### 1. Enhanced Version Validation Script

**Location**: `scripts/validate-dependency-versions.sh`

**Features**:
- Validates Selenium versions across pom.xml and workflow files
- Checks TypeScript and @types/node versions across Node.js projects
- Validates Python requirements files
- **Report Generation**: Supports JSON and CSV report formats

**Usage**:
```bash
# Standard validation (console output only)
./scripts/validate-dependency-versions.sh

# Generate JSON report
./scripts/validate-dependency-versions.sh --report-json --report-file results/report.json

# Generate CSV report
./scripts/validate-dependency-versions.sh --report-csv --report-file results/report.csv
```

**Report Format**:
- **JSON**: Structured data with timestamp, status, errors, warnings, and detailed mismatch information
- **CSV**: Tabular format suitable for spreadsheet analysis

---

### 2. Scheduled GitHub Actions Workflow

**Location**: `.github/workflows/version-monitoring.yml`

**Features**:
- **Scheduled Execution**: Runs daily at 9 AM UTC (3 AM Central Time) - same time as Nightly CI/CD runs
- **Manual Trigger**: Can be triggered manually via `workflow_dispatch`
- **Report Generation**: Automatically generates JSON reports
- **Artifact Storage**: Reports stored as artifacts for 30 days
- **Failure Alerts**: Workflow fails if version mismatches detected

**Accessing Reports**:
1. Go to GitHub Actions tab
2. Select "Version Monitoring (Scheduled)" workflow
3. Click on a completed run
4. Download "version-validation-report" artifact

---

### 3. Git Hooks (Pre-Commit & Pre-Push)

**Location**: `.git/hooks/pre-commit` and `.git/hooks/pre-push` (installed via `scripts/install-git-hooks.sh`)

**Pre-Commit Hook Features**:
- Automatically formats code if code files are being committed (runs `scripts/format-code.sh --skip-compilation`)
- Skips formatting for documentation-only changes (`.md`, `.log`, `.txt`, `.rst`, `.adoc` files)
- Fast commits: Documentation-only commits complete in <1 second
- Code commits: Formatting takes 20-40 seconds (no compilation on commit)
- Can be bypassed with `git commit --no-verify` (use with caution)

**Pre-Push Hook Features**:
- Automatically formats, compiles, and validates code if code files are being pushed
- Skips all checks for documentation-only changes
- Runs `scripts/format-code.sh` (with compilation) and `scripts/validate-pre-commit.sh`
- Validates GitHub Actions workflow files using `actionlint`
- Ensures code quality before reaching main branch
- Can be bypassed with `git push --no-verify` (use with caution)

**Installation**:
```bash
./scripts/install-git-hooks.sh
```

**Uninstallation**:
```bash
rm .git/hooks/pre-commit
rm .git/hooks/pre-push
```

---

## 📊 Monitoring Workflow

### Daily Automated Checks

1. **Scheduled Run**: GitHub Actions workflow runs daily at 9 AM UTC (3 AM Central Time) - same time as Nightly CI/CD runs
2. **Validation**: Executes version validation script
3. **Report Generation**: Creates JSON report with results
4. **Artifact Storage**: Uploads report as artifact (retained 30 days)
5. **Failure Detection**: Workflow fails if mismatches found
6. **Notification**: GitHub sends notification if workflow fails

### Manual Checks

You can manually trigger the monitoring workflow:
1. Go to GitHub Actions
2. Select "Version Monitoring (Scheduled)"
3. Click "Run workflow"
4. Select branch and click "Run workflow"

Or run locally:
```bash
./scripts/validate-dependency-versions.sh --report-json --report-file results/report.json
```

---

## 🚨 Alerting

### Current Alerting Mechanisms

1. **GitHub Actions Workflow Failure**: 
   - Workflow fails if version mismatches detected
   - GitHub sends email notification to repository maintainers
   - Visible in GitHub Actions dashboard

2. **Pre-Commit Hook**:
   - Blocks commits if validation fails
   - Provides immediate feedback to developers
   - Can be bypassed with `--no-verify` flag

### Future Enhancements (Optional)

- GitHub Issues: Automatically create issues when mismatches detected
- Slack/Discord Integration: Send alerts to team channels
- Dashboard: Web-based dashboard for version status visualization
- Email Reports: Periodic email summaries of version status

---

## 📝 Report Structure

### JSON Report Example

```json
{
  "timestamp": "2025-12-20T02:00:00Z",
  "status": "success",
  "errors": 0,
  "warnings": 0,
  "selenium": {
    "pom_version": "4.39.0",
    "workflow_version": "4.39.0",
    "match": true
  },
  "mismatches": [],
  "warnings_list": []
}
```

### CSV Report Example

```csv
timestamp,status,errors,warnings,selenium_pom_version,selenium_workflow_version,selenium_match,mismatches,warnings_list
2025-12-20T02:00:00Z,success,0,0,4.39.0,4.39.0,true,,
```

---

## 🔗 Related Documents

- [Version Tracking](VERSION_TRACKING.md) - Dependency version tracking
- [Pre-Pipeline Validation](PRE_PIPELINE_VALIDATION.md) - Local validation checklist
- [Next Steps After PR #53](../archive/2025-12/20251220_NEXT_STEPS_AFTER_PR53.md) - Work plan and status (archived)

---

## 🛠️ Maintenance

### Updating Monitoring Schedule

Edit `.github/workflows/version-monitoring.yml`:
```yaml
schedule:
  - cron: '0 9 * * *'  # Change time as needed (currently 9 AM UTC, same as Nightly CI/CD runs)
```

### Adding New Version Checks

Edit `scripts/validate-dependency-versions.sh` to add new validation phases.

### Customizing Alerts

Modify the workflow's "Comment on Issues" step to integrate with your notification system.

---

**Last Updated**: 2025-12-20
