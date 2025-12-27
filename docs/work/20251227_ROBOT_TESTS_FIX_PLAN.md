# Robot Framework Tests Fix Plan

## 🔑 Legend

| Symbol | Meaning |
|--------|---------|
| ✅ | Complete/Working |
| ⚠️ | Needs Investigation/Fix |
| ❌ | Not Working/Disabled |
| 📋 | Planned |
| 🔧 | Technical Detail |
| 📝 | Documentation |

### Status Indicators
| Symbol | Status | Description |
|--------|--------|-------------|
| ✅ | Enabled/Working | Feature is enabled and working |
| ❌ | Disabled | Feature is disabled or not enabled |
| ⚠️ | Needs Fix | Requires investigation or fix |
| 📋 | Planned | Task is planned for implementation |
| 🔍 | Investigation | Item needs further investigation |

---

## 📊 Current Status

### Robot Tests Configuration

**Location**: `.github/workflows/env-fe.yml` (reusable workflow)

**Current State**: ❌ **DISABLED BY DEFAULT**

**Configuration**:
```yaml
enable_robot_tests:
  description: 'Enable Robot Framework Tests'
  type: boolean
  default: false  # ⚠️ Disabled by default
```

**Job Condition**:
```yaml
robot-tests:
  name: Robot Framework Tests (${{ inputs.environment }})
  if: inputs.enable_robot_tests == true  # Only runs if explicitly enabled
```

### Why Robot Tests Are Disabled

**Primary Reason**: The `enable_robot_tests` input defaults to `false`, and the calling workflow (`.github/workflows/ci.yml`) does **not** explicitly enable robot tests when calling `env-fe.yml`.

**Additional Context**: Robot tests were **working fine locally** but **failing in the CI/CD pipeline**, which likely led to them being disabled to prevent pipeline failures.

**Evidence**:
1. ✅ Robot test job is fully configured in `env-fe.yml` (lines 763-901)
2. ✅ Installation script exists: `scripts/ci/install-robot-framework.sh`
3. ✅ Test files exist: `src/test/robot/HomePageTests.robot`, `src/test/robot/APITests.robot`
4. ✅ WebDriverManager library exists: `src/test/robot/WebDriverManager.py`
5. ❌ **Default value is `false`** in workflow input (line 82)
6. ❌ **Not enabled in `ci.yml`** when calling `env-fe.yml`
7. ⚠️ **Tests work locally but fail in pipeline** - indicates environment-specific issues

**Calling Workflow** (`.github/workflows/ci.yml`):
```yaml
test-fe-dev:
  uses: ./.github/workflows/env-fe.yml
  with:
    environment: 'dev'
    test_suite: ${{ needs.determine-envs.outputs.test_suite }}
    base_url: ${{ needs.setup-base-urls.outputs.base_url_dev }}
    # ⚠️ Missing: enable_robot_tests: true
```

---

## 🔍 Investigation Findings

### What's Already Working

1. ✅ **Workflow Configuration**: Robot test job is fully configured with:
   - Selenium Grid services (selenium-hub, chrome-node)
   - Python setup (Python 3.11)
   - Node.js setup (Node.js 20)
   - Service startup and verification
   - Robot Framework installation script
   - Test execution with retry logic
   - Result upload

2. ✅ **Test Files**: Robot test files exist and are properly structured:
   - `src/test/robot/HomePageTests.robot` - UI tests
   - `src/test/robot/APITests.robot` - API tests
   - `src/test/robot/WebDriverManager.py` - WebDriver management library

3. ✅ **Installation Script**: `scripts/ci/install-robot-framework.sh` is complete and handles:
   - Python detection
   - pip upgrade
   - Robot Framework installation
   - SeleniumLibrary installation
   - RequestsLibrary installation
   - Verification and error handling

4. ✅ **Test Execution Logic**: The workflow correctly:
   - Uses Python directly (not Maven plugin) - avoids Jython limitations
   - Handles Selenium Grid remote URL
   - Converts BASE_URL for Docker container networking
   - Includes retry logic
   - Uploads results

### What Needs Fixing

1. ❌ **Default Value**: `enable_robot_tests` defaults to `false`
2. ❌ **Not Enabled in CI**: `ci.yml` doesn't pass `enable_robot_tests: true`
3. ⚠️ **Maven Plugin Disabled**: `pom.xml` has Robot Framework Maven plugin with `<skip>true</skip>`
   - **Note**: This is actually fine - the workflow uses Python directly, not the Maven plugin
   - **Reason**: Maven plugin uses Jython which doesn't have access to pip-installed libraries
   - **Status**: No action needed - Python execution is the correct approach
4. ⚠️ **Pipeline Failures (Local vs CI/CD)**: Tests work locally but fail in pipeline
   - **Likely Causes**:
     - Selenium Grid connection issues in CI environment
     - BASE_URL networking (localhost vs host.docker.internal)
     - Service startup timing issues
     - Missing dependencies or environment variables
     - File path resolution differences
     - Timeout issues in CI environment
5. ⚠️ **Potential Issues to Verify**:
   - Test file paths and structure
   - Selenium Grid connection in CI environment
   - BASE_URL conversion for Docker networking
   - Result file generation and upload
   - Service availability timing
   - Environment variable propagation

---

## 🔍 Local vs CI/CD Differences (Key Investigation Area)

Since robot tests **work locally but fail in CI/CD**, the focus should be on identifying environment-specific issues.

### Known Differences

Based on existing documentation (`docs/guides/infrastructure/DOCKER_VS_CI_DIFFERENCES.md`):

1. **Selenium Grid Remote URL**:
   - **Local**: May use direct browser (no Grid) or local Grid
   - **CI/CD**: Uses Docker services (selenium-hub, chrome-node)
   - **Issue**: Browser in Docker container needs `host.docker.internal` to reach services
   - **Current Fix**: BASE_URL conversion logic exists (lines 847-849)

2. **Service Availability**:
   - **Local**: Services start on localhost, immediate availability
   - **CI/CD**: Services start in parallel, may need wait time
   - **Current Fix**: `verify-services.sh` step exists

3. **Network Configuration**:
   - **Local**: `localhost` works directly
   - **CI/CD**: `localhost` in BASE_URL doesn't work from Docker container
   - **Current Fix**: Converts `localhost` to `host.docker.internal`

4. **File Paths**:
   - **Local**: Relative paths work from project root
   - **CI/CD**: Working directory may differ, paths may need adjustment

5. **Environment Variables**:
   - **Local**: May have defaults or different values
   - **CI/CD**: Must be explicitly set, may be missing

### Specific Issues to Investigate

1. **BASE_URL Conversion Verification**:
   - Check if `host.docker.internal` is accessible from Selenium Grid container
   - Verify conversion happens before test execution
   - May need to verify `host.docker.internal` works on GitHub Actions runners

2. **Selenium Grid Connection**:
   - Verify Grid is fully ready before tests start
   - Check if wait timeout is sufficient
   - May need additional wait after Grid is "ready"

3. **Service Startup Timing**:
   - Verify services are fully ready (not just responding to health check)
   - May need additional wait after service verification
   - Check if services are ready for actual test execution

4. **Test File Path Resolution**:
   - Verify `src/test/robot/` path works in CI working directory
   - May need absolute paths or path adjustment

---

## 📋 Fix Plan

### Phase 1: Enable Robot Tests (Immediate) ✅ COMPLETE

**Goal**: Enable robot tests in the CI/CD pipeline

#### Step 1.1: Update Default Value ✅ COMPLETE
- **File**: `.github/workflows/env-fe.yml`
- **Change**: Updated `enable_robot_tests` default from `false` to `true`
- **Location**: Line 82
- **Rationale**: Align with other test frameworks (Cypress, Playwright default to `true`)
- **Status**: ✅ Completed - Default changed to `true`

#### Step 1.2: Verify Test Files
- **Action**: Verify robot test files are in correct location
- **Expected**: `src/test/robot/*.robot` files exist
- **Check**: Test file structure and syntax

#### Step 1.3: Test Locally (Optional)
- **Action**: Run robot tests locally to verify they work
- **Command**: 
  ```bash
  cd src/test/robot
  robot --variable BASE_URL:http://localhost:3003 --variable SELENIUM_REMOTE_URL: HomePageTests.robot
  ```

### Phase 2: Investigate and Fix Pipeline-Specific Issues

**Goal**: Identify and fix issues that cause robot tests to fail in CI/CD but work locally

#### Step 2.1: Enable Tests and Capture Failure Details
- **Action**: Enable robot tests and run a test pipeline
- **Expected**: Tests may fail (since they were disabled due to failures)
- **Check**: CI logs for specific error messages and stack traces
- **Document**: Capture exact error messages, timing, and failure points

#### Step 2.2: Compare Local vs CI Environment
- **Action**: Identify differences between local and CI environments
- **Key Areas to Compare**:
  - **Selenium Grid**: Local may use direct browser, CI uses Docker containers
  - **BASE_URL**: Local uses `localhost`, CI needs `host.docker.internal` conversion
  - **Service Timing**: CI may have different startup timing
  - **File Paths**: CI may have different working directory
  - **Environment Variables**: May not be set correctly in CI
  - **Python/Pip**: Version differences or installation issues

#### Step 2.3: Investigate Common CI/CD Issues
- **Potential Issues** (based on "works locally, fails in CI"):
  
  **A) Selenium Grid Connection**:
  - Issue: Browser in Docker container can't reach services
  - Current Fix: BASE_URL conversion to `host.docker.internal` (already implemented)
  - Verify: Check if conversion happens correctly
  - Check: Grid wait timeout may be insufficient
  
  **B) Service Availability**:
  - Issue: Tests start before services are fully ready
  - Current Fix: `verify-services.sh` step exists
  - Verify: Check if verification is sufficient
  - Check: May need longer wait times
  
  **C) Test File Discovery**:
  - Issue: Robot Framework can't find test files in CI
  - Check: Verify `src/test/robot/` path resolution
  - Check: Working directory in CI vs local
  
  **D) Dependency Installation**:
  - Issue: Robot Framework or libraries not installed correctly
  - Current Fix: `install-robot-framework.sh` script
  - Verify: Check if script runs successfully
  - Check: Python path and pip installation in CI
  
  **E) Timeout Issues**:
  - Issue: Tests timeout in CI due to slower execution
  - Current: 5 minute timeout
  - Check: May need to increase timeout
  - Check: Test execution time in CI vs local

#### Step 2.4: Fix Identified Issues
- **Action**: Address each issue found in Steps 2.2 and 2.3
- **Common Fixes**:
  - Adjust BASE_URL conversion logic (verify `host.docker.internal` works)
  - Increase service wait times
  - Increase test timeouts if needed
  - Fix test file paths (use absolute paths if needed)
  - Verify environment variables are set correctly
  - Add additional logging for debugging
  - Improve error messages for CI debugging

### Phase 3: Validation and Documentation

**Goal**: Ensure robot tests run reliably and document the setup

#### Step 3.1: Verify Test Execution
- **Action**: Run multiple pipeline executions to verify stability
- **Check**: Tests pass consistently

#### Step 3.2: Update Documentation
- **Files to Update**:
  - `docs/guides/testing/TEST_SUITES_REFERENCE.md` - Mark robot tests as enabled
  - `docs/guides/infrastructure/WORKFLOW_TEST_ORGANIZATION.md` - Update if needed
  - Any other docs referencing robot test status

#### Step 3.3: Clean Up
- **Action**: Remove any temporary fixes or workarounds
- **Check**: Code is clean and maintainable

---

## 🔧 Technical Details

### Current Robot Test Configuration

**Execution Method**: Python (`python -m robot.run`)
- **Why**: Maven plugin uses Jython which doesn't have access to pip-installed libraries
- **Solution**: Run directly with Python executable that has libraries installed

**Selenium Grid**: ✅ Uses Selenium Grid (Docker services)
- **Hub**: `selenium-hub` service
- **Node**: `chrome-node` service
- **Remote URL**: `http://localhost:4444/wd/hub` (converted to `host.docker.internal` for browser)

**BASE_URL Handling**:
```bash
# When using Selenium Grid (container), localhost points to the container itself.
# Use host.docker.internal so the browser inside the grid can reach the services.
if [[ "$BASE_URL" == http://localhost:* ]]; then
  BASE_URL="${BASE_URL/localhost/host.docker.internal}"
fi
```

**Test Files**:
- `src/test/robot/HomePageTests.robot` - UI tests using SeleniumLibrary
- `src/test/robot/APITests.robot` - API tests using RequestsLibrary

**Dependencies**:
- Robot Framework
- robotframework-seleniumlibrary
- robotframework-requests
- webdriver-manager (for WebDriverManager.py)

### Comparison with Other Test Frameworks

| Framework | Default Enabled? | Execution Method | Selenium Grid? |
|-----------|----------------|------------------|---------------|
| Smoke Tests | ✅ Yes (`default: true`) | Maven/Java | ✅ Yes |
| Cypress | ✅ Yes (`default: true`) | npm/Node.js | ❌ No |
| Playwright | ✅ Yes (`default: true`) | npm/Node.js | ❌ No |
| **Robot** | ❌ **No (`default: false`)** | **Python** | ✅ **Yes** |
| Selenide | ✅ Yes (`default: true`) | Maven/Java | ✅ Yes |
| Vibium | ✅ Yes (`default: true`) | npm/Node.js | ❌ No |

**Inconsistency**: Robot tests are the only framework disabled by default.

**Note on Maven Plugin**: The `pom.xml` has a Robot Framework Maven plugin that is disabled (`<skip>true</skip>`). This is intentional and correct - the workflow uses Python directly instead of the Maven plugin because:
- Maven plugin uses Jython which doesn't have access to pip-installed libraries
- Python execution provides better library support and compatibility
- The workflow already handles Python execution correctly

---

## 📝 Implementation Steps

### Step 1: Enable Robot Tests (Simple Fix)

1. **Update `.github/workflows/env-fe.yml`**:
   ```yaml
   enable_robot_tests:
     description: 'Enable Robot Framework Tests'
     type: boolean
     default: true  # Change from false to true
   ```

2. **Verify**: Check that robot test job will now run by default

3. **Test**: Run a pipeline to see if tests execute

### Step 2: Investigate Any Issues

1. **Check CI Logs**: Look for errors in robot test execution
2. **Common Issues to Check**:
   - Selenium Grid connection
   - BASE_URL resolution
   - Test file discovery
   - Dependency installation
   - Timeout issues

### Step 3: Fix Issues (If Found)

1. **Address each issue** found in Step 2
2. **Test fixes** in CI
3. **Iterate** until tests pass

### Step 4: Validate and Document

1. **Run multiple pipeline executions** to verify stability
2. **Update documentation** to reflect enabled status
3. **Clean up** any temporary fixes

---

## ✅ Acceptance Criteria

- [ ] Robot tests are enabled by default (`enable_robot_tests: default: true`)
- [ ] Robot tests execute successfully in CI/CD pipeline
- [ ] Test results are uploaded as artifacts
- [ ] Test results are included in test-summary
- [ ] Test results are included in Allure reports
- [ ] Documentation updated to reflect enabled status
- [ ] No regressions in other test frameworks

---

## 🔍 Potential Issues to Watch For

### High Priority (Based on "Works Locally, Fails in CI")

1. **Selenium Grid Connection**:
   - Issue: Browser in Docker container can't reach services on localhost
   - Current Fix: BASE_URL conversion to `host.docker.internal` (already implemented)
   - **Verify**: Check if conversion works correctly in CI
   - **Check**: Grid wait timeout may be insufficient
   - **Debug**: Add logging to show actual BASE_URL used

2. **Service Availability Timing**:
   - Issue: Tests start before backend/frontend services are fully ready
   - Current Fix: `verify-services.sh` step exists
   - **Verify**: Check if verification is sufficient (may need longer wait)
   - **Check**: Service startup time in CI vs local
   - **Debug**: Add explicit waits in test setup

3. **BASE_URL Networking**:
   - Issue: `localhost` in BASE_URL doesn't work from Docker container
   - Current Fix: Conversion to `host.docker.internal` (line 847-849)
   - **Verify**: Check if conversion logic works correctly
   - **Check**: May need to verify `host.docker.internal` is accessible
   - **Debug**: Log actual BASE_URL value used

### Medium Priority

4. **Test File Discovery**:
   - Issue: Robot Framework can't find test files in CI
   - **Check**: Verify `src/test/robot/` path is correct
   - **Check**: Working directory differences (CI vs local)
   - **Fix**: Use absolute paths or verify relative paths work
   - **Debug**: Add logging to show test file paths

5. **Dependency Installation**:
   - Issue: Robot Framework or libraries not installed correctly
   - Current Fix: `install-robot-framework.sh` script
   - **Verify**: Check if script runs successfully in CI
   - **Check**: Python path and pip installation
   - **Check**: Library versions may differ
   - **Debug**: Log installed package versions

6. **Environment Variables**:
   - Issue: Environment variables not set correctly in CI
   - **Check**: `SELENIUM_REMOTE_URL`, `BASE_URL`, `TEST_ENVIRONMENT`
   - **Verify**: Variables are passed correctly to test execution
   - **Debug**: Log all environment variables at test start

### Lower Priority

7. **Timeout Issues**:
   - Issue: Tests timeout before completion in CI
   - Current: 5 minute timeout
   - **Check**: Test execution time in CI vs local
   - **Fix**: Increase timeout if needed
   - **Debug**: Log test execution times

8. **Result File Generation**:
   - Issue: `output.xml` not generated or in wrong location
   - Current: `--outputdir target/robot-reports`
   - **Verify**: Check if directory is created correctly
   - **Check**: File paths in CI environment
   - **Debug**: List files in output directory

9. **Python/Pip Version Differences**:
   - Issue: Different Python or pip versions between local and CI
   - Current: Python 3.11 in CI
   - **Check**: Local Python version vs CI version
   - **Check**: Library compatibility with Python version
   - **Debug**: Log Python and pip versions

---

## 📊 Summary

**Root Cause**: Robot tests are disabled by default (`default: false`) because they were failing in the CI/CD pipeline despite working locally.

**Solution**: 
1. Change default value to `true` to align with other test frameworks
2. Investigate and fix pipeline-specific issues (local vs CI differences)

**Complexity**: Medium - Configuration change is simple, but fixing pipeline-specific issues may require investigation and debugging.

**Risk**: Medium - Tests work locally, so the issue is likely environment-specific (Selenium Grid, networking, timing, etc.). Need to identify and fix the specific CI/CD issues.

**Estimated Time**: 
- Phase 1 (Enable): 15 minutes
- Phase 2 (Investigate & Fix Issues): 2-4 hours (investigating local vs CI differences, debugging, fixing)
- Phase 3 (Validate): 1 hour (multiple pipeline runs to verify stability)

---

**Status**: ✅ Phase 1 Complete - Tests Enabled  
**Date**: 2025-12-27  
**Branch**: `fix-robot-tests`  
**Related Files**: 
- `.github/workflows/env-fe.yml` (lines 79-82, 763-901)
- `.github/workflows/ci.yml` (calls `env-fe.yml`)
- `scripts/ci/install-robot-framework.sh`
- `src/test/robot/HomePageTests.robot`
- `src/test/robot/APITests.robot`
- `src/test/robot/WebDriverManager.py`

---

## ✅ Local Test Verification Results

**Date**: 2025-12-27  
**Status**: ✅ **ALL TESTS PASS LOCALLY**

### Test Execution Summary

**API Tests** (`src/test/robot/APITests.robot`):
- ✅ **3 tests, 3 passed, 0 failed**
- Tests executed against external API (jsonplaceholder.typicode.com)
- No local services required
- All assertions passed

**HomePage Tests** (`src/test/robot/HomePageTests.robot`):
- ✅ **2 tests, 2 passed, 0 failed**
- Tests executed against local frontend (http://localhost:3003)
- Required backend and frontend services running
- All page elements found and verified

### Test Results Details

**API Tests**:
1. ✅ Get Posts API Test - PASS
2. ✅ Get All Posts Test - PASS
3. ✅ Create Post Test - PASS

**HomePage Tests**:
1. ✅ Home Page Should Load - PASS
2. ✅ Home Page Should Display Navigation Panel - PASS

### Environment Setup

**Prerequisites Verified**:
- ✅ Python 3.12.0 installed
- ✅ Robot Framework 7.3.2 installed
- ✅ robotframework-seleniumlibrary 6.7.1 installed
- ✅ robotframework-requests 0.9.7 installed
- ✅ Backend service running on port 8003
- ✅ Frontend service running on port 3003

**Test Execution Command**:
```bash
# API Tests (no services needed)
python3 -m robot.run --outputdir target/robot-reports src/test/robot/APITests.robot

# UI Tests (services required)
python3 -m robot.run --outputdir target/robot-reports \
  --variable BASE_URL:http://localhost:3003 \
  src/test/robot/HomePageTests.robot
```

**Output Files Generated**:
- ✅ `target/robot-reports/output.xml` - Test results in XML format
- ✅ `target/robot-reports/log.html` - Detailed test execution log
- ✅ `target/robot-reports/report.html` - Test execution report

### Conclusion

**Local Verification**: ✅ **PASSED**

All robot tests execute successfully in the local environment. This confirms:
1. Test files are correctly structured
2. Dependencies are properly installed
3. Test logic is sound
4. Services can be started and accessed correctly

**Next Steps**: Since tests work locally but fail in CI/CD, the focus should be on investigating pipeline-specific issues (Selenium Grid networking, BASE_URL conversion, service timing, etc.) as outlined in Phase 2 of the fix plan.

