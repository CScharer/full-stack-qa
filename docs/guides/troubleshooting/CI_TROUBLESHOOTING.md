# CI/CD Troubleshooting Guide

**Date**: November 8, 2025
**Pipeline**: GitHub Actions
**Workflow**: `.github/workflows/ci.yml`

---

## 🎯 Quick Reference

| Issue | Quick Fix | Details |
|-------|-----------|---------|
| Docker Compose not found | Use `docker compose` (V2) | [Link](#docker-compose-not-found) |
| Allure report fails | Check test results exist | [Link](#allure-report-generation-fails) |
| Tests don't run | Check Surefire config | [Link](#tests-not-running) |
| Grid connection fails | Wait for health checks | [Link](#grid-connection-timeout) |
| Artifacts not uploading | Check paths exist | [Link](#artifacts-not-found) |

---

## 🔍 Common Issues & Solutions

### **1. Docker Compose Not Found**

**Error:**
```
docker-compose: command not found
Error: Process completed with exit code 127
```

**Cause:**
GitHub Actions runners use Docker Compose V2 (built into Docker CLI)

**Solution:**
```yaml
# ❌ Old (doesn't work):
run: docker-compose build tests

# ✅ New (works):
run: docker compose build tests
```

**Note:** This is the current configuration in the workflow.

---

### **2. Allure Report Generation Fails**

**Error 1: Docker Image Not Found**
```
openjdk:8-jre-alpine: not found
ERROR: failed to build
```

**Cause:**
Third-party action `simple-elf/allure-report-action` uses deprecated Docker image

**Solution:**
Use direct Allure CLI installation:
```yaml
- name: Install Allure CLI
  run: |
    wget -q https://github.com/allure-framework/allure2/releases/download/2.31.0/allure-2.31.0.tgz
    tar -zxf allure-2.31.0.tgz
    sudo mv allure-2.31.0 /opt/allure
    sudo ln -s /opt/allure/bin/allure /usr/local/bin/allure
    allure --version

- name: Generate Allure Report
  run: allure generate allure-results --clean -o allure-report
```

**Note:** This is the current configuration in the workflow.

**Error 2: No Results Found**
```
Allure results directory not found
```

**Cause:**
Tests didn't run or results weren't uploaded properly

**Solution:**
1. Check test execution job succeeded
2. Verify `upload-artifact` step ran
3. Check artifact name matches download pattern
4. Ensure `allure-results` directory has files

**Debug:**
```yaml
- name: Debug artifacts
  run: |
    ls -la allure-results/
    find allure-results -type f
```

---

### **3. Tests Not Running (0 Tests Executed)**

**Error:**
```
Tests run: 0, Failures: 0, Errors: 0, Skipped: 0
```

**Possible Causes:**

**A) Wrong Test Provider**
```
[INFO] Using auto detected provider org.apache.maven.surefire.testng.TestNGProvider
```
But tests are written in JUnit.

**Solution:**
Ensure tests use the detected provider (TestNG in our case), or configure Surefire explicitly:
```xml
<configuration>
    <properties>
        <property>
            <name>listener</name>
            <value>io.qameta.allure.testng.AllureTestNg</value>
        </property>
    </properties>
</configuration>
```

**B) Test Filter Doesn't Match**
```bash
-Dtest=Scenarios#Google
```
Method name doesn't match or class not found.

**Solution:**
```bash
# Verify test class exists
./mvnw test -Dtest=SimpleGridTest  # Known working test
```

**C) @Test Annotation Missing**
```java
public void myTest() { }  // ❌ Won't run
```

**Solution:**
```java
@Test
public void myTest() { }  // ✅ Will run
```

---

### **4. Grid Connection Timeout**

**Error:**
```
Could not start a new session. Could not connect to Grid
org.openqa.selenium.SessionNotCreatedException: Could not connect to http://selenium-hub:4444
```

**Cause:**
Grid not ready when tests start

**Solution:**
Add wait step in workflow:
```yaml
- name: Wait for Selenium Grid
  run: |
    timeout 60 bash -c 'until curl -sf http://localhost:4444/wd/hub/status; do sleep 2; done'
    echo "Grid is ready!"
```

**Status:** ✅ Already in workflow

---

### **5. Artifacts Not Found**

**Error:**
```
Unable to find any artifacts for the associated workflow
```

**Causes & Solutions:**

**A) Path Doesn't Exist**
```yaml
# ❌ Path doesn't exist
path: target/allure-results/

# ✅ Check first
- run: ls -la target/allure-results/ || echo "Not found"
```

**B) Job Failed Before Upload**
```yaml
# ✅ Always upload, even on failure
- uses: actions/upload-artifact@v4
  if: always()  # ← Important!
  with:
    name: test-results
    path: target/surefire-reports/
```

**C) Artifact Expired**
```yaml
retention-days: 3  # Check if expired
```

---

### **6. Service Container Issues**

**Error:**
```
selenium-hub service is unhealthy
```

**Cause:**
Health check failing

**Debug:**
```yaml
services:
  selenium-hub:
    image: selenium/hub:4.39.0
    options: >-
      --health-cmd "curl -f http://localhost:4444/wd/hub/status || exit 1"
      --health-interval 10s
      --health-retries 5
```

**Solution:**
- Increase health retries
- Increase interval
- Add startup delay

---

### **7. Build Timeout**

**Error:**
```
The job running on runner has exceeded the maximum execution time of 360 minutes
```

**Cause:**
Job hanging or taking too long

**Solutions:**

**A) Add Timeout**
```yaml
- name: Run tests
  timeout-minutes: 15  # Fail fast
  run: ./mvnw test
```

**B) Cancel Hanging Jobs**
```yaml
- name: Run tests with timeout
  run: timeout 10m ./mvnw test || exit 1
```

---

### **8. Maven Dependency Download Issues**

**Error:**
```
Could not resolve dependencies for project
Failed to collect dependencies
```

**Solutions:**

**A) Cache Issues**
```yaml
- name: Clear Maven cache
  run: rm -rf ~/.m2/repository
```

**B) Network Issues**
```yaml
- name: Retry dependency download
  run: ./mvnw dependency:go-offline -B -U
  # -U forces update
```

**C) Invalid Version**
```xml
<!-- ❌ Version doesn't exist -->
<maven-surefire-plugin.version>3.5.1</maven-surefire-plugin.version>

<!-- ✅ Valid version -->
<maven-surefire-plugin.version>3.2.5</maven-surefire-plugin.version>
```

---

### **9. Screenshot Capture Failing**

**Error:**
```
Failed to capture screenshot
ClassCastException: cannot cast to TakesScreenshot
```

**Cause:**
Driver isn't RemoteWebDriver or doesn't support screenshots

**Solution:**
```java
// ✅ Check before capturing
if (driver instanceof TakesScreenshot) {
    byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    Allure.addAttachment("Screenshot", new ByteArrayInputStream(screenshot));
}
```

---

### **10. GitHub Pages Deployment Fails**

**Error:**
```
Error: Action failed with "The process '/usr/bin/git' failed with exit code 128"
```

**Possible Causes:**

**A) gh-pages Branch Doesn't Exist**

**Solution:**
```bash
# Create gh-pages branch manually
git checkout --orphan gh-pages
git rm -rf .
echo "Allure Reports" > README.md
git add README.md
git commit -m "Initialize gh-pages"
git push origin gh-pages
git checkout main
```

**B) Permissions Issue**

**Solution:**
Enable workflow write permissions:
1. Go to repo Settings → Actions → General
2. Scroll to "Workflow permissions"
3. Select "Read and write permissions"
4. Save

---

## 🛠️ Debugging Strategies

### **1. Check Workflow File Syntax**

```bash
# Validate YAML locally
yamllint .github/workflows/ci.yml

# Or use online validator
# https://www.yamllint.com/
```

### **2. Add Debug Output**

```yaml
- name: Debug info
  run: |
    echo "Current directory: $(pwd)"
    echo "Files: $(ls -la)"
    echo "Java version: $(java -version)"
    echo "Maven version: $(./mvnw --version)"
    echo "Environment: $(env | sort)"
```

### **3. Download and Inspect Artifacts**

```bash
# Using GitHub CLI
gh run list --limit 5
gh run view <run-id>
gh run download <run-id>

# Inspect locally
cd allure-results
ls -la
# Check for *-result.json files
```

### **4. Re-run with Debug Logging**

```yaml
- name: Run tests with debug
  run: ./mvnw test -X  # Maven debug mode
```

### **5. Run Job Locally (act)**

```bash
# Install act (GitHub Actions local runner)
brew install act

# Run workflow locally
act -j build-and-compile

# Run with secrets
act -j build-and-compile -s GITHUB_TOKEN=your-token
```

---

## 🚨 Emergency Fixes

### **Quick: Skip Failing Job**

```yaml
# Add to failing job
continue-on-error: true

# Job will run but won't block pipeline
```

### **Quick: Disable Job Temporarily**

```yaml
# Add condition to skip
if: false  # Disables this job

# Or use specific condition
if: github.ref != 'refs/heads/main'  # Skip on main
```

### **Quick: Cancel All Runs**

```bash
# Using GitHub CLI
gh run list --status in_progress
gh run cancel <run-id>

# Or in GitHub UI
# Actions → Click run → Cancel workflow
```

---

## 📊 Monitoring & Logs

### **View Live Logs**

1. Go to Actions tab
2. Click running workflow
3. Click job name
4. See real-time logs

### **Download Logs**

```bash
# Using GitHub CLI
gh run view <run-id> --log > workflow-log.txt

# Or download via UI
# Actions → Run → ... menu → Download logs
```

### **Check Job Status**

```bash
# Using GitHub CLI
gh run list
gh run view <run-id>
gh run view <run-id> --log-failed  # Only failed jobs
```

---

## 🎯 Verification Checklist

Before committing workflow changes:

- [ ] YAML syntax valid (no tabs, proper indentation)
- [ ] All `uses:` actions exist and are current
- [ ] All file paths correct (`./mvnw`, not `mvn`)
- [ ] Docker commands use V2 syntax (`docker compose`)
- [ ] Secrets properly referenced (`${{ secrets.NAME }}`)
- [ ] `if: always()` where needed (upload artifacts)
- [ ] Timeouts set for long-running jobs
- [ ] Dependencies between jobs correct (`needs:`)
- [ ] Artifact names unique and descriptive
- [ ] Retention periods reasonable

---

## 💡 Best Practices

### **1. Always Upload Artifacts on Failure**

```yaml
- name: Upload logs
  if: failure()  # Only on failure
  uses: actions/upload-artifact@v4
  with:
    name: failure-logs
    path: target/surefire-reports/
```

### **2. Use Matrix for Multi-Browser Testing**

```yaml
strategy:
  matrix:
    browser: [chrome, firefox, edge]
  fail-fast: false  # Don't stop if one browser fails
```

### **3. Cache Dependencies**

```yaml
- uses: actions/setup-java@v4
  with:
    cache: 'maven'  # Caches ~/.m2/repository
```

### **4. Set Reasonable Timeouts**

```yaml
- name: Run tests
  timeout-minutes: 15  # Prevent hanging
```

### **5. Use Workflow Dispatch for Testing**

```yaml
on:
  workflow_dispatch:  # Manual trigger
  push:
    branches: [main]
```

---

## 🔗 Useful Resources

- **GitHub Actions Docs**: https://docs.github.com/actions
- **Workflow Syntax**: https://docs.github.com/actions/reference/workflow-syntax
- **Troubleshooting**: https://docs.github.com/actions/monitoring-and-troubleshooting
- **Community Forum**: https://github.community/c/actions

---

## 📞 Getting Help

### **Check These First:**
1. Workflow run logs in GitHub Actions tab
2. This troubleshooting guide
3. docs/GITHUB_ACTIONS.md

### **Still Stuck?**
1. Download artifacts and inspect locally
2. Run workflow locally with `act`
3. Check GitHub Actions status page
4. Open issue with logs attached

---

## 🔧 Troubleshooting: Deploy Jobs Not Running

**Last Updated**: 2025-12-21

### Issue
Deploy jobs (`deploy-dev`, `deploy-test`, `deploy-prod`) are not running after tests complete.

### Root Cause Analysis

#### Problem 1: Reusable Workflow Result Status
When a reusable workflow is called, it returns:
- `'success'` - Only if ALL jobs in the reusable workflow succeed
- `'failure'` - If ANY job in the reusable workflow fails
- `'skipped'` - If the workflow was skipped
- `'cancelled'` - If the workflow was cancelled

**Issue**: Even if tests pass, if the Allure report job or test summary job fails, the reusable workflow returns `'failure'`, not `'success'`.

#### Problem 2: Deploy Job Conditions
The original condition was:
```yaml
if: needs.test-dev-environment.result == 'success'
```

This means deploy only runs if the reusable workflow returns `'success'`, which requires ALL jobs to succeed.

### Solution Implemented

#### Step 1: Updated Deploy Job Conditions
Changed from:
```yaml
if: needs.test-dev-environment.result == 'success'
```

To:
```yaml
if: |
  always() &&
  needs.determine-schedule-type.outputs.code-changed == 'true' &&
  needs.determine-environments.outputs.run_dev == 'true' &&
  (needs.test-dev-environment.result == 'success' || needs.test-dev-environment.result == 'failure') &&
  github.ref == 'refs/heads/main' &&
  github.event_name == 'push'
```

**Why**: This allows the deploy job to run if the workflow completed (success OR failure), not just success.

#### Step 2: Added Test Result Verification
Added a verification step inside each deploy job that:
1. Downloads test result artifacts
2. Parses XML test results
3. Checks for failures/errors
4. Only proceeds with deployment if tests passed

```yaml
- name: Verify tests passed
  id: verify-tests
  run: |
    # Parse test results and check for failures/errors
    # Only proceed if tests passed
```

### Additional Checks

If deploy jobs still don't run, verify:

#### 1. Branch and Event Type
Deploy jobs only run on:
- `github.ref == 'refs/heads/main'` (main branch only)
- `github.event_name == 'push'` (not on pull requests)

**Check**: Are you pushing to `main` branch, or is this a PR?

#### 2. Code Changed Detection
Deploy jobs require:
- `needs.determine-schedule-type.outputs.code-changed == 'true'`

**Check**: Are you making code changes, or only documentation?

#### 3. Environment Selection
Deploy jobs require:
- `needs.determine-environments.outputs.run_dev == 'true'` (for dev)
- `needs.determine-environments.outputs.run_test == 'true'` (for test)
- `needs.determine-environments.outputs.run_prod == 'true'` (for prod)

**Check**: Is the environment selected correctly?

#### 4. Reusable Workflow Status
Check the actual result status in GitHub Actions:
- Go to Actions tab
- Click on the workflow run
- Check the `test-dev-environment` job status
- Look at the result: `success`, `failure`, `skipped`, or `cancelled`

### Debugging Steps

#### Step 1: Check Workflow Run
1. Go to GitHub Actions
2. Find the workflow run
3. Check if `test-dev-environment` job completed
4. Note the result status

#### Step 2: Check Deploy Job Status
1. Look for `deploy-dev` job
2. If it's skipped, click on it
3. Check the "Why was this job skipped?" message
4. This will show which condition failed

#### Step 3: Enable Debug Logging
Add to workflow file (temporarily):
```yaml
env:
  ACTIONS_STEP_DEBUG: true
  ACTIONS_RUNNER_DEBUG: true
```

This will show detailed information about condition evaluation.

#### Step 4: Check Artifact Availability
Verify test artifacts are being uploaded:
1. Go to workflow run
2. Check "Artifacts" section
3. Look for `smoke-results-dev`, `grid-results-chrome-dev`, etc.
4. If artifacts are missing, deploy job can't verify tests

---

**Last Updated**: 2025-12-27
**Maintainer**: CJS QA Team
