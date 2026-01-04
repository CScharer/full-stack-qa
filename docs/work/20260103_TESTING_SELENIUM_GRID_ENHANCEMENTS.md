# Testing Guide: Selenium Grid Enhancements

**Date Created**: 2026-01-03  
**Status**: 📋 Testing Guide (Temporary - Remove after successful testing)  
**Related**: `20260103_SELENIUM_GRID_ENHANCEMENTS.md`

**Note**: This is a temporary testing document. Once all testing is complete and the implementation is verified, this document can be removed or archived.

---

## 🎯 Overview

This guide provides step-by-step instructions for testing the Selenium Grid enhancements implementation, including version validation, retry logic, and pre-push validation.

---

## 📋 Prerequisites

### For Pipeline Testing (Recommended - No Local Docker Required)

1. **GitHub Actions Pipeline**
   - PR created with implementation changes
   - Pipeline will automatically run tests
   - Grid is started automatically in CI environment

2. **Access to Pipeline Logs**
   - View GitHub Actions workflow runs
   - Check test results and logs
   - Verify version validation output

### For Local Testing (If Docker Available)

1. **Selenium Grid Running**
   - Local Grid via Docker Compose, or
   - Remote Grid accessible via URL

2. **Java Development Environment**
   - JDK 21+ installed
   - Maven configured
   - Project dependencies installed

3. **Bash Environment**
   - Access to terminal/command line
   - `curl` installed
   - `jq` installed (optional, for JSON parsing)

**Note**: Due to disk space constraints, testing will primarily be done in the CI/CD pipeline.

---

## 🔄 Pipeline Testing Strategy

Since local Docker testing is not available due to disk space constraints, testing will be performed in the CI/CD pipeline. Here's how to verify each component:

### Pipeline Test Checklist

1. **Version Validation in Pipeline**
   - ✅ Check "Wait for Selenium Grid" step logs
   - ✅ Look for version validation messages
   - ✅ Verify version matches between pom.xml and Grid

2. **Retry Logic in Pipeline**
   - ✅ Check "Run Grid Tests" step logs
   - ✅ Look for retry attempt messages (if Grid is slow to start)
   - ✅ Verify exponential backoff behavior

3. **Pre-Push Validation**
   - ✅ Check if `validate-dependency-versions.sh` runs in pipeline
   - ✅ Verify Docker Compose validation runs
   - ✅ Check for version mismatch errors (if any)

4. **Enhanced SeleniumWebDriver**
   - ✅ Verify tests run successfully
   - ✅ Check logs for version validation messages
   - ✅ Verify connection succeeds with retry logic

### What to Look For in Pipeline Logs

**Version Validation:**
```
✅ Version validation passed: 4.39.0
🔍 Validating Grid version...
```

**Retry Logic:**
```
Connection attempt 1/5 to Grid at http://localhost:4444/wd/hub
Transient error on attempt 1/5: Connection refused. Retrying in 1000ms...
✅ Successfully connected to Grid on attempt 2/5
```

**Pre-Push Validation:**
```
Phase 4: Docker Compose Version Validation
Checking docker-compose.yml...
✅ Version matches pom.xml: 4.39.0
```

---

## 🧪 Test Scenarios

### 1. Test Version Validation Utility

#### 1.1: Test with Matching Versions

**Setup:**
```bash
# Start Selenium Grid (ensure version matches pom.xml)
docker-compose up -d selenium-hub chrome-node-1

# Get Selenium version from pom.xml
SELENIUM_VERSION=$(grep '<selenium.version>' pom.xml | sed 's/.*<selenium.version>\([^<]*\)<\/selenium.version>.*/\1/' | head -1)
echo "Expected version: $SELENIUM_VERSION"
```

**Test:**
```bash
# Set environment variable
export SELENIUM_VERSION=$SELENIUM_VERSION

# Run a simple Java test that uses version validation
mvn test -Dtest=SimpleGridTest -Dselenium.remote.url=http://localhost:4444/wd/hub
```

**Expected Result:**
- ✅ Version validation passes
- ✅ Connection succeeds
- ✅ Test runs successfully

#### 1.2: Test with Version Mismatch

**Setup:**
```bash
# Start Grid with different version (if possible)
# Or simulate by setting wrong version
export SELENIUM_VERSION="4.40.0"  # Different from actual Grid version
```

**Test:**
```bash
# Run test - should fail fast with version mismatch error
mvn test -Dtest=SimpleGridTest -Dselenium.remote.url=http://localhost:4444/wd/hub
```

**Expected Result:**
- ❌ Version validation fails
- ❌ Clear error message: "Selenium Grid server version (X.X.X) does not match client version (Y.Y.Y)"
- ❌ Connection attempt not made

#### 1.3: Test Version Tolerance

**Test with MINOR tolerance:**
```bash
export SELENIUM_GRID_VERSION_TOLERANCE=MINOR
export SELENIUM_VERSION="4.39.0"  # Client version
# Grid version: 4.40.0 (should pass with MINOR tolerance)
mvn test -Dtest=SimpleGridTest
```

**Test with EXACT tolerance (default):**
```bash
export SELENIUM_GRID_VERSION_TOLERANCE=EXACT
export SELENIUM_VERSION="4.39.0"
# Grid version: 4.40.0 (should fail with EXACT tolerance)
mvn test -Dtest=SimpleGridTest
```

**Test with NONE tolerance (skip validation):**
```bash
export SELENIUM_GRID_VERSION_TOLERANCE=NONE
# Should skip validation entirely
mvn test -Dtest=SimpleGridTest
```

---

### 2. Test Retry Logic

#### 2.1: Test with Grid Not Ready (Transient Error)

**Setup:**
```bash
# Start Grid but don't wait for it to be fully ready
docker-compose up -d selenium-hub
# Immediately try to connect (before Grid is ready)
```

**Test:**
```bash
# Set retry parameters
export SELENIUM_GRID_MAX_RETRIES=5
export SELENIUM_GRID_RETRY_BASE_DELAY_MS=1000
export SELENIUM_GRID_RETRY_MAX_DELAY_MS=5000
export SELENIUM_GRID_RETRY_TIMEOUT_MS=30000

# Run test
mvn test -Dtest=SimpleGridTest -Dselenium.remote.url=http://localhost:4444/wd/hub
```

**Expected Result:**
- ⏳ Multiple retry attempts logged
- ⏳ Exponential backoff delays observed
- ✅ Eventually succeeds when Grid becomes ready
- 📊 Logs show retry attempts with timing

#### 2.2: Test with Permanent Error (Version Mismatch)

**Setup:**
```bash
# Set version mismatch (permanent error - should not retry)
export SELENIUM_VERSION="4.40.0"  # Wrong version
export SELENIUM_GRID_VERSION_TOLERANCE=EXACT
```

**Test:**
```bash
mvn test -Dtest=SimpleGridTest
```

**Expected Result:**
- ❌ Fails immediately (no retries)
- ❌ Clear error: "Permanent error on attempt 1/5"
- ❌ No retry attempts logged

#### 2.3: Test Retry Configuration

**Test with custom retry parameters:**
```bash
export SELENIUM_GRID_MAX_RETRIES=3
export SELENIUM_GRID_RETRY_BASE_DELAY_MS=500
export SELENIUM_GRID_RETRY_MAX_DELAY_MS=2000
export SELENIUM_GRID_RETRY_TIMEOUT_MS=10000

# Run test and observe retry behavior
mvn test -Dtest=SimpleGridTest
```

**Expected Result:**
- ⏳ Retries limited to 3 attempts
- ⏳ Shorter delays (500ms base, max 2000ms)
- ⏳ Timeout after 10 seconds

---

### 3. Test Enhanced SeleniumWebDriver

#### 3.1: Test Normal Connection Flow

**Test:**
```bash
# Use SeleniumWebDriver with Grid
mvn test -Dtest=*GridTest -Dselenium.remote.url=http://localhost:4444/wd/hub
```

**Expected Result:**
- ✅ Version validation runs first
- ✅ Grid readiness check runs
- ✅ Connection with retry logic
- ✅ Detailed logging at each step

#### 3.2: Test with SKIP_VERSION_CHECK

**Test:**
```bash
export SKIP_VERSION_CHECK=true
mvn test -Dtest=SimpleGridTest
```

**Expected Result:**
- ⏭️ Version validation skipped
- ✅ Connection proceeds normally
- 📝 Log shows: "Version validation skipped (SKIP_VERSION_CHECK=true)"

#### 3.3: Test Vendor URL (Non-Grid)

**Test:**
```bash
# Use vendor URL (should bypass Grid validation)
# This tests backward compatibility
mvn test -Dtest=SimpleGridTest -Dvendor.url=http://some-vendor-grid.com/wd/hub
```

**Expected Result:**
- ✅ Direct connection (no Grid validation)
- ✅ No version validation
- ✅ Backward compatibility maintained

---

### 4. Test Wait Scripts

#### 4.1: Test Basic Wait Script

**Test:**
```bash
# Basic usage (no version check)
./scripts/ci/wait-for-grid.sh
```

**Expected Result:**
- ✅ Waits for Grid to be ready
- ✅ Exits successfully when Grid is ready
- ✅ Colored output

#### 4.2: Test with Version Validation

**Test:**
```bash
# With version validation
SELENIUM_VERSION="4.39.0" ./scripts/ci/wait-for-grid.sh
```

**Expected Result:**
- ✅ Waits for Grid
- ✅ Validates version after Grid is ready
- ✅ Shows version match/mismatch

#### 4.3: Test with Version Mismatch

**Test:**
```bash
# Set wrong version
SELENIUM_VERSION="4.40.0" ./scripts/ci/wait-for-grid.sh
```

**Expected Result:**
- ✅ Waits for Grid
- ❌ Fails with version mismatch error
- 📝 Clear error message showing both versions

#### 4.4: Test Skip Version Check

**Test:**
```bash
SKIP_VERSION_CHECK=true ./scripts/ci/wait-for-grid.sh
```

**Expected Result:**
- ✅ Waits for Grid
- ⏭️ Skips version validation
- ✅ Faster execution

#### 4.5: Test with Custom URL and Timeout

**Test:**
```bash
./scripts/ci/wait-for-grid.sh "http://localhost:4444/wd/hub/status" 10
```

**Expected Result:**
- ✅ Uses custom URL
- ✅ Uses 10 second timeout
- ✅ Works correctly

---

### 5. Test Pre-Push Validation

#### 5.1: Test Version Validation Script

**Test:**
```bash
# Run validation script directly
./scripts/validate-dependency-versions.sh
```

**Expected Result:**
- ✅ Checks pom.xml vs workflow versions
- ✅ Checks Docker Compose versions
- ✅ Shows summary of all checks
- ✅ Exit code 0 if all pass

#### 5.2: Test with Version Mismatch

**Setup:**
```bash
# Temporarily modify pom.xml to have different version
# Or modify workflow file
```

**Test:**
```bash
./scripts/validate-dependency-versions.sh
```

**Expected Result:**
- ❌ Detects version mismatch
- ❌ Clear error messages
- ❌ Exit code 1 (failure)

#### 5.3: Test Docker Compose Validation

**Test:**
```bash
# Script should check all docker-compose files
./scripts/validate-dependency-versions.sh
```

**Expected Result:**
- ✅ Scans docker-compose.yml
- ✅ Scans docker-compose.dev.yml
- ✅ Scans docker-compose.prod.yml
- ✅ Compares image versions with pom.xml
- ⚠️ Warns if using :latest tags

#### 5.4: Test Pre-Push Hook

**Test:**
```bash
# Make a code change (not documentation)
# Try to push
git add src/test/java/com/cjs/qa/utilities/SeleniumGridVersionValidator.java
git commit -m "test: test pre-push validation"
git push
```

**Expected Result:**
- ✅ Pre-push hook runs
- ✅ Version validation runs
- ✅ Push succeeds if versions match
- ❌ Push fails if versions mismatch

#### 5.5: Test Pre-Push Hook with Documentation-Only Changes

**Test:**
```bash
# Make documentation-only change
git add docs/work/TESTING_SELENIUM_GRID_ENHANCEMENTS.md
git commit -m "docs: add testing guide"
git push
```

**Expected Result:**
- ✅ Pre-push hook detects documentation-only change
- ⏭️ Skips version validation
- ✅ Push succeeds quickly

---

### 6. Integration Testing

#### 6.1: End-to-End Test

**Test Flow:**
1. Start Grid
2. Wait for Grid (using wait-for-grid.sh)
3. Run tests (using SeleniumWebDriver)
4. Verify all features work together

**Test:**
```bash
# Start Grid
docker-compose up -d selenium-hub chrome-node-1

# Wait for Grid with version validation
SELENIUM_VERSION=$(grep '<selenium.version>' pom.xml | sed 's/.*<selenium.version>\([^<]*\)<\/selenium.version>.*/\1/' | head -1)
SELENIUM_VERSION=$SELENIUM_VERSION ./scripts/ci/wait-for-grid.sh

# Run tests
mvn test -Dtest=*GridTest
```

**Expected Result:**
- ✅ All steps complete successfully
- ✅ Version validation works
- ✅ Retry logic works
- ✅ Tests pass

#### 6.2: Test Error Scenarios

**Test 1: Grid Not Running**
```bash
# Stop Grid
docker-compose down

# Try to run tests
mvn test -Dtest=SimpleGridTest
```

**Expected Result:**
- ⏳ Retry logic attempts connection
- ❌ Eventually fails after retries exhausted
- 📝 Clear error message

**Test 2: Wrong Grid Version**
```bash
# Start Grid with different version (if possible)
# Or set wrong version expectation
export SELENIUM_VERSION="4.40.0"
export SELENIUM_GRID_VERSION_TOLERANCE=EXACT

mvn test -Dtest=SimpleGridTest
```

**Expected Result:**
- ❌ Fails immediately with version mismatch
- ❌ No connection attempt made
- 📝 Clear error message

---

## 🔍 Debugging Tips

### Enable Debug Logging

```bash
# Set log level to DEBUG
export LOG_LEVEL=DEBUG
mvn test -Dtest=SimpleGridTest
```

### Check Grid Status Manually

```bash
# Check Grid status
curl -s http://localhost:4444/wd/hub/status | jq .

# Check Grid version
curl -s http://localhost:4444/wd/hub/status | jq '.value.version'
```

### Verify Environment Variables

```bash
# Check all Selenium-related environment variables
env | grep SELENIUM
```

### Test Individual Components

```bash
# Test version validator directly (create simple test)
# Test retry logic directly (create simple test)
# Test wait script directly
```

---

## ✅ Acceptance Criteria Checklist

### Runtime Validation
- [ ] Version validation works correctly for matching versions
- [ ] Version validation fails fast for mismatched versions
- [ ] Retry logic uses exponential backoff with jitter
- [ ] Retry logic only retries on transient errors
- [ ] Configuration options are respected
- [ ] Error messages are clear and actionable
- [ ] Logging provides useful diagnostics

### Pre-Push Validation
- [ ] Pre-push hook validates Selenium versions before push
- [ ] Version validation script checks `pom.xml` vs workflow files
- [ ] Version validation script checks Docker Compose files
- [ ] Pre-push hook fails push on version mismatches
- [ ] Clear error messages for version mismatches
- [ ] Validation is fast (< 5 seconds)
- [ ] Validation skips for documentation-only changes

### Wait Scripts
- [ ] Wait script waits for Grid to be ready
- [ ] Version validation works in wait script
- [ ] Skip option works correctly
- [ ] Custom URL and timeout work correctly

---

## 📝 Test Results Template

```markdown
## Test Results - [Date]

### Phase 1: Version Validation Utility
- [ ] Test 1.1: Matching versions - PASS/FAIL
- [ ] Test 1.2: Version mismatch - PASS/FAIL
- [ ] Test 1.3: Version tolerance - PASS/FAIL

### Phase 2: Retry Logic
- [ ] Test 2.1: Transient errors - PASS/FAIL
- [ ] Test 2.2: Permanent errors - PASS/FAIL
- [ ] Test 2.3: Retry configuration - PASS/FAIL

### Phase 3: Enhanced SeleniumWebDriver
- [ ] Test 3.1: Normal flow - PASS/FAIL
- [ ] Test 3.2: Skip version check - PASS/FAIL
- [ ] Test 3.3: Vendor URL - PASS/FAIL

### Phase 4: Wait Scripts
- [ ] Test 4.1: Basic wait - PASS/FAIL
- [ ] Test 4.2: With version validation - PASS/FAIL
- [ ] Test 4.3: Version mismatch - PASS/FAIL
- [ ] Test 4.4: Skip version check - PASS/FAIL

### Phase 5: Pre-Push Validation
- [ ] Test 5.1: Version validation script - PASS/FAIL
- [ ] Test 5.2: Version mismatch detection - PASS/FAIL
- [ ] Test 5.3: Docker Compose validation - PASS/FAIL
- [ ] Test 5.4: Pre-push hook - PASS/FAIL

### Integration Tests
- [ ] Test 6.1: End-to-end - PASS/FAIL
- [ ] Test 6.2: Error scenarios - PASS/FAIL

## Notes
[Any issues, observations, or recommendations]
```

---

## 🚀 Quick Start Testing

### Pipeline Testing (Recommended)

**Test via GitHub Actions Pipeline:**

1. **Create PR with implementation changes**
   ```bash
   git checkout selenium-grid-enhancements
   git push origin selenium-grid-enhancements
   # Create PR via GitHub UI or CLI
   ```

2. **Monitor Pipeline Execution**
   - Go to GitHub Actions tab
   - Watch workflow run
   - Check "Wait for Selenium Grid" step for version validation
   - Check "Run Grid Tests" step for retry logic
   - Review logs for version validation messages

3. **Verify Pre-Push Validation**
   - Check if pre-push hook ran (if pushing from local)
   - Or verify in pipeline that version validation script runs

4. **Check Test Results**
   - Verify tests pass with new enhancements
   - Check logs for version validation output
   - Verify retry logic messages (if Grid startup is slow)

### Local Testing (If Docker Available)

**Fastest way to test everything:**

```bash
# 1. Start Grid
docker-compose up -d selenium-hub chrome-node-1

# 2. Wait for Grid with version check
SELENIUM_VERSION=$(grep '<selenium.version>' pom.xml | sed 's/.*<selenium.version>\([^<]*\)<\/selenium.version>.*/\1/' | head -1)
SELENIUM_VERSION=$SELENIUM_VERSION ./scripts/ci/wait-for-grid.sh

# 3. Run a simple Grid test
mvn test -Dtest=SimpleGridTest

# 4. Test pre-push validation
./scripts/validate-dependency-versions.sh
```

---

## 📚 Additional Resources

- Main implementation document: `20260103_SELENIUM_GRID_ENHANCEMENTS.md`
- Version tracking: `docs/process/VERSION_TRACKING.md`
- Version monitoring: `docs/process/VERSION_MONITORING.md`

---

## 📝 Document Lifecycle

**This is a temporary testing document.**

- **Created**: 2026-01-03
- **Purpose**: Guide testing of Selenium Grid enhancements implementation
- **Status**: Active during testing phase
- **Removal**: Can be deleted/archived after:
  - ✅ All test scenarios pass in pipeline
  - ✅ Implementation verified working
  - ✅ No issues found
  - ✅ Ready for production use

**After successful testing, this document can be removed or moved to archive.**

