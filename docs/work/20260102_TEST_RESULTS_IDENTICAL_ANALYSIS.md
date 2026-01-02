# Test Results Appearing Identical Across Environments - Comprehensive Analysis

**Date:** January 2, 2026  
**Last Updated:** January 2, 2026  
**Issue:** Test results from **ALL frameworks** (FS, Cypress, Playwright, Robot, Vibium, Selenide, BE tests) appear identical across dev, test, and prod environments in the Allure report.

**Important Note:** It's acceptable if tests produce identical results/data. The concern is **verifying that results are truly from different test runs in different environments**, not that the results themselves are different.

## Current Status (Post-Metadata Implementation)

**Verification Metadata Implementation:** ✅ **COMPLETED**  
**Metadata Parameters Added:**
- Environment (DEV, TEST, PROD)
- Base URL (http://localhost:3003, :3004, :3005)
- Test Execution Time (ISO timestamp)
- CI Run ID (GitHub Actions run ID)
- CI Run Number (GitHub Actions run number)

**GitHub Results Analysis (Post-PR #50):**
After reviewing the Allure report metadata in GitHub Actions results, the following was observed:

**Finding:** The **only difference** in metadata across environments is the **Environment** parameter itself. All other metadata parameters (Base URL, Test Execution Time, CI Run ID, CI Run Number) are **identical** across dev, test, and prod environments.

**This confirms the fallback logic issue is still occurring:**
- ✅ Environment parameter correctly shows DEV, TEST, PROD
- ❌ Base URL is the same across all environments (should be different)
- ❌ Test Execution Time is identical (should be different if from different runs)
- ❌ CI Run ID is identical (expected if same pipeline, but timestamps should differ)
- ❌ CI Run Number is identical (expected if same pipeline, but timestamps should differ)

**Conclusion:** The fallback logic in `prepare-combined-allure-results.sh` is still processing the **same artifact files** for each environment, creating duplicate results with different environment labels but identical underlying data and metadata (except the environment label itself).

## Problem Statement

**ALL test framework results** displayed in the Allure report show identical metrics, test data, and outcomes across dev, test, and prod environments. 

**Key Concern:** We need a way to **verify that results are truly from different test runs in different environments**, not duplicates of the same run with different labels.

**Note:** It's acceptable if tests produce identical results - the issue is ensuring we can distinguish between:
- ✅ Different test runs in different environments (same results = OK)
- ❌ Same test run processed multiple times with different environment labels (NOT OK)

This affects:

- **FS (Full-Stack) Tests** - Artillery load tests
- **Cypress Tests** - E2E browser tests
- **Playwright Tests** - E2E browser tests
- **Robot Framework Tests** - Acceptance tests
- **Vibium Tests** - Visual regression tests
- **Selenide Tests** - TestNG-based UI tests
- **BE Performance Tests** - Gatling, JMeter, Locust

This is unexpected because:

1. Different environments should have different configurations
2. Different base URLs should produce different response times
3. Different service configurations should yield different performance characteristics
4. Different test data should produce different results

## Root Cause Analysis

### CRITICAL ISSUE: Fallback Logic Processing Same Files for All Environments ⚠️ **CONFIRMED**

**Location:** `scripts/ci/prepare-combined-allure-results.sh`

**Affected Frameworks:** ALL frameworks (Cypress, Playwright, Robot, Vibium, FS)

**Status:** ⚠️ **STILL OCCURRING** - Verified via metadata analysis in GitHub Actions results

**The Problem:**
When artifacts are downloaded with `merge-multiple: true`, if the structure is flat (all files in root directory), the fallback logic processes the **SAME files for EACH environment** in a loop, creating duplicate results with different environment labels but identical test data.

**Verification Evidence (Post-Metadata Implementation):**
- ✅ Environment parameter correctly differentiates (DEV, TEST, PROD)
- ❌ Base URL is identical across all environments (should be different: :3003, :3004, :3005)
- ❌ Test Execution Time is identical (should differ if from different test runs)
- ❌ CI Run ID is identical (expected for same pipeline, but timestamps prove same file processed)
- ❌ CI Run Number is identical (expected for same pipeline, but timestamps prove same file processed)

**This confirms:** The same artifact files are being processed multiple times (once per environment), with only the environment label being changed. The underlying test data, timestamps, and all other metadata remain identical.

**Evidence in Code:**

#### Cypress (lines 139-151):
```bash
# If no environment-specific subdirectory, check the merged root directory
# Process for each environment to ensure all environments are covered
else
    json_file=$(find "$SOURCE_DIR/cypress-results" ... | head -1)
    if [ -n "$json_file" ]; then
        ./scripts/ci/convert-cypress-to-allure.sh "$TARGET_DIR" "$json_dir" "$env" || true
        # ⚠️ SAME json_file processed for dev, test, prod!
    fi
fi
```

#### Playwright (lines 175-179):
```bash
for env in "${ACTIVE_ENVIRONMENTS[@]}"; do
    if [ -d "$SOURCE_DIR/playwright-results/test-results" ]; then
        ./scripts/ci/convert-playwright-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/playwright-results/test-results" "$env" || true
        # ⚠️ SAME directory processed for dev, test, prod!
    fi
done
```

#### Robot Framework (lines 204-210):
```bash
for env in "${ACTIVE_ENVIRONMENTS[@]}"; do
    output_xml=$(find "$SOURCE_DIR/robot-results" -name "output.xml" 2>/dev/null | head -1)
    if [ -n "$output_xml" ]; then
        ./scripts/ci/convert-robot-to-allure.sh "$TARGET_DIR" "$output_dir" "$env" || true
        # ⚠️ SAME output.xml processed for dev, test, prod!
    fi
done
```

#### Vibium (similar pattern):
Same issue - processes same merged directory for each environment.

#### FS Tests (lines 303-311):
Same fallback issue already identified.

**Impact:**
- Same test data converted 3 times (dev, test, prod)
- Each conversion gets different UUID and environment label
- But test data/metrics are identical
- Allure shows them as separate tests but with identical results

### 1. Identical Smoke Test Configurations (FS-SPECIFIC ISSUE)

**Location:** `playwright/artillery/config/dev-smoke.yml` and `playwright/artillery/config/test-smoke.yml`

Both smoke test configuration files are **completely identical**:

```yaml
# Both files have:
phases:
  - duration: 10
    arrivalRate: 1
    name: "Smoke test - light load"
```

**Impact:**
- Dev and test environments run identical load profiles
- Same arrival rate (1 user/sec)
- Same duration (10 seconds)
- Only difference is base URL (localhost:3003 vs localhost:3004)
- If services behave similarly, metrics will be identical

**Evidence:**
- `dev-smoke.yml`: `arrivalRate: 1`, `duration: 10`, `baseUrl: http://localhost:3003`
- `test-smoke.yml`: `arrivalRate: 1`, `duration: 10`, `baseUrl: http://localhost:3004`

### 2. TestNG-Based Tests (Selenide) Environment Detection Issue

**Location:** `scripts/ci/merge-allure-results.sh` (lines 52-105)

**The Problem:**
TestNG-based tests (Selenide, Smoke, Grid, Mobile, Responsive) are merged by `merge-allure-results.sh` which copies all result files to a single directory. Environment detection relies on path patterns:

```bash
if echo "$rel_path" | grep -qiE "(results-dev/|-results-dev[/-]|be-results-dev/|selenide-results-dev)"; then
    env="dev"
elif echo "$rel_path" | grep -qiE "(results-test/|-results-test[/-]|be-results-test/|selenide-results-test)"; then
    env="test"
# ...
```

**If path patterns don't match:**
- All tests get labeled as "unknown" or the same environment
- Results appear identical because they're all from the same source
- Environment labels may not be correctly applied

**Evidence:**
- Marker files created for environment tracking (lines 99-104)
- Warning if no markers created (lines 129-135)
- Environment detection may fail if artifact structure doesn't match expected patterns

### 4. Artifact Download and Merge Behavior

**Location:** `scripts/ci/prepare-combined-allure-results.sh` (lines 303-311)

There is a fallback mechanism that processes root-level JSON files if environment-specific directories aren't found:

```bash
if [ "$ENV_PROCESSED" -eq 0 ]; then
    json_files=$(find "$SOURCE_DIR/fs-results" -maxdepth 1 -name "*.json" -type f 2>/dev/null)
    if [ -n "$json_files" ]; then
        echo "   🔄 Converting FS test results for $env (found $json_count JSON file(s) in root)..."
        ./scripts/ci/convert-artillery-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/fs-results" "$env" || true
        ENV_PROCESSED=1
    fi
fi
```

**Problem:**
- If artifact structure is flat (all JSON files in root directory)
- The fallback processes the **same JSON files** for **each environment** (dev, test, prod)
- Creates duplicate Allure results with different environment labels
- But identical test data and metrics

**Expected Artifact Structure:**
```
fs-results/
  fs-results-dev/artillery-results/*.json
  fs-results-test/artillery-results/*.json
```

**Actual Structure (if fallback triggers):**
```
fs-results/
  smoke-results.json  (same file processed 3 times)
```

### 4. Artifact Download and Merge Behavior

**Location:** `.github/workflows/ci.yml` (lines 1017-1111)

**Pattern for ALL Frameworks:**
```yaml
- name: Download [framework] results
  uses: actions/download-artifact@v4
  continue-on-error: true
  with:
    pattern: "[framework]-results-*"
    path: all-test-results/[framework]-results
    merge-multiple: true
```

**Behavior:**
- Artifacts uploaded as: `[framework]-results-dev`, `[framework]-results-test`, `[framework]-results-prod`
- Downloaded with `merge-multiple: true` to `all-test-results/[framework]-results`
- **Expected Structure:** `[framework]-results/[framework]-results-{env}/...`
- **Actual Structure (if flat):** `[framework]-results/*.json` (all files in root)
- If merge creates flat structure, environment-specific directories don't exist
- Fallback logic then processes same files for each environment

**Affected Frameworks:**
- Cypress: `cypress-results-*` → `all-test-results/cypress-results`
- Playwright: `playwright-results-*` → `all-test-results/playwright-results`
- Robot: `robot-results-*` → `all-test-results/robot-results`
- Vibium: `vibium-results-*` → `all-test-results/vibium-results`
- FS: `fs-results-*` → `all-test-results/fs-results`

### 5. HistoryId and UUID Generation

**Location:** `scripts/ci/convert-artillery-to-allure.sh` (lines 112-114)

```python
test_uuid = str(uuid.uuid4())
history_id_content = f"artillery:{test_name}:{env or 'unknown'}"
history_id = hashlib.md5(history_id_content.encode()).hexdigest()
```

**Analysis:**
- Each conversion generates a new UUID (different for each environment)
- HistoryId includes environment name (should be different per environment)
- However, if the same JSON file is processed multiple times:
  - Different UUIDs are generated
  - But test data/metrics are identical
  - Allure may deduplicate or show as identical results

### 6. FS Tests Should Not Run in Prod

**Location:** `scripts/ci/prepare-combined-allure-results.sh` (line 248)

```bash
FS_ENVIRONMENTS=("dev" "test")
```

**Note:** FS tests are configured to run **only in dev and test**, never in prod. If prod results are appearing, they may be:
1. From a different test framework (FE/BE tests)
2. Incorrectly labeled in Allure
3. From a prod FS job that shouldn't exist

## Verification Steps Needed

To confirm the root causes, verify:

### For ALL Frameworks:

1. **Artifact Structure After Download**
   - Check if environment-specific subdirectories exist:
     - `cypress-results/cypress-results-dev/`
     - `playwright-results/playwright-results-dev/`
     - `robot-results/robot-results-dev/`
     - `vibium-results/vibium-results-dev/`
     - `fs-results/fs-results-dev/`
   - Check if files are in root directories (flat structure)
   - Verify if `merge-multiple: true` creates expected structure

2. **Processing Logs**
   - Check which path is being used for each environment
   - Verify if fallback logic is being triggered for each framework
   - Confirm which files are processed for each environment
   - Check for warnings about "merged artifacts" processing

3. **Environment Detection**
   - Check marker files created by `merge-allure-results.sh`
   - Verify environment labels in Allure result JSON files
   - Confirm environment parameters are set correctly

### For FS Tests Specifically:

4. **Test Configuration Usage**
   - Verify smoke tests are using `dev-smoke.yml` and `test-smoke.yml`
   - Confirm different environments use different config files
   - Check if full configs (`dev.yml`, `test.yml`) have different values

5. **Actual Test Execution**
   - Verify different base URLs are being used
   - Confirm different services are being tested
   - Check if services have different configurations

## Recommended Fixes

### Fix 1: Fix Fallback Logic for ALL Frameworks (CRITICAL PRIORITY)

**Action:** Prevent fallback from processing same files for multiple environments

**Affected Files:**
- `scripts/ci/prepare-combined-allure-results.sh` (Cypress, Playwright, Robot, Vibium, FS)

**Current Problem:**
All frameworks have fallback logic that processes the same merged files for each environment, creating duplicates.

**Recommended Change Pattern (apply to all frameworks):**

**For Cypress (lines 139-151):**
```bash
# BEFORE (WRONG):
else
    json_file=$(find "$SOURCE_DIR/cypress-results" ... | head -1)
    if [ -n "$json_file" ]; then
        ./scripts/ci/convert-cypress-to-allure.sh "$TARGET_DIR" "$json_dir" "$env" || true
        # ⚠️ Processes same file for dev, test, prod
    fi
fi

# AFTER (CORRECT):
else
    # Only process root files if NO environment-specific directories exist at all
    env_specific_dirs=$(find "$SOURCE_DIR/cypress-results" -type d -name "cypress-results-*" 2>/dev/null | wc -l | tr -d ' ')
    if [ "$env_specific_dirs" -eq 0 ]; then
        # Only process for FIRST environment to prevent duplicates
        if [ "$env" == "${ACTIVE_ENVIRONMENTS[0]}" ]; then
            json_file=$(find "$SOURCE_DIR/cypress-results" ... | head -1)
            if [ -n "$json_file" ]; then
                echo "   ⚠️  WARNING: No environment-specific directories found, processing root files for $env only"
                ./scripts/ci/convert-cypress-to-allure.sh "$TARGET_DIR" "$json_dir" "$env" || true
                # Mark that root files have been processed
                touch "$SOURCE_DIR/cypress-results/.root-processed"
            fi
        else
            echo "   ⏭️  Skipping $env (root files already processed for ${ACTIVE_ENVIRONMENTS[0]})"
        fi
    else
        echo "   ⚠️  Environment-specific directories exist but not found for $env, skipping root fallback"
    fi
fi
```

**Apply same pattern to:**
- Playwright (lines 175-179)
- Robot Framework (lines 204-210)
- Vibium (similar location)
- FS Tests (lines 303-311)

**Rationale:**
- Prevents same files from being processed multiple times
- Only processes root files if no environment-specific directories exist
- Processes root files only once (for first environment)
- Adds warnings to identify when fallback is used

### Fix 2: Differentiate Smoke Test Configurations (FS-SPECIFIC, HIGH PRIORITY)

**Action:** Make test smoke config have different load profile than dev

**File:** `playwright/artillery/config/test-smoke.yml`

**Change:**
```yaml
phases:
  - duration: 10
    arrivalRate: 2  # Changed from 1 to 2 for test environment
    name: "Smoke test - light load"
```

**Rationale:**
- Different arrival rates will produce different metrics
- Test environment can handle slightly higher load
- Results will be visibly different in Allure report

### Fix 3: Improve Environment Detection for TestNG Tests (MEDIUM PRIORITY)

**Action:** Enhance environment detection in merge-allure-results.sh

**File:** `scripts/ci/merge-allure-results.sh`

**Current Issue:**
Environment detection relies on path patterns. If patterns don't match, all tests get labeled as "unknown" or the same environment.

**Recommended Changes:**

1. **Add More Pattern Matching:**
```bash
# Add checks for artifact name patterns
if echo "$rel_path" | grep -qiE "(artifact.*dev|dev.*artifact)"; then
    env="dev"
# ... more patterns
```

2. **Add Validation:**
```bash
# After environment detection, validate
if [ "$env" == "unknown" ]; then
    echo "   ⚠️  WARNING: Could not detect environment for: $rel_path"
    # Try to extract from parent directory names
    parent_dirs=$(echo "$rel_path" | tr '/' '\n' | grep -iE "(dev|test|prod)" | head -1)
    if [ -n "$parent_dirs" ]; then
        env=$(echo "$parent_dirs" | tr '[:upper:]' '[:lower:]' | grep -oE "(dev|test|prod)" | head -1)
        echo "   ✅ Extracted environment from parent directory: $env"
    fi
fi
```

3. **Add Debug Logging:**
```bash
# Log first 50 files with their detected environment
if [ "$(wc -l < "$ENV_DETECTION_LOG" 2>/dev/null || echo 0)" -lt 50 ]; then
    echo "$env|$rel_path" >> "$ENV_DETECTION_LOG"
fi
```

**Rationale:**
- Improves environment detection accuracy
- Provides visibility when detection fails
- Helps identify patterns that need matching

### Fix 4: Add Environment Validation (MEDIUM PRIORITY)

**Action:** Prevent fallback from processing same files for multiple environments

**File:** `scripts/ci/prepare-combined-allure-results.sh` (lines 303-311)

**Current Code:**
```bash
if [ "$ENV_PROCESSED" -eq 0 ]; then
    json_files=$(find "$SOURCE_DIR/fs-results" -maxdepth 1 -name "*.json" -type f 2>/dev/null)
    if [ -n "$json_files" ]; then
        ./scripts/ci/convert-artillery-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/fs-results" "$env" || true
        ENV_PROCESSED=1
    fi
fi
```

**Recommended Change:**
```bash
if [ "$ENV_PROCESSED" -eq 0 ]; then
    # Only process root files if NO environment-specific directories exist at all
    # And only for the first environment that needs it
    env_specific_dirs=$(find "$SOURCE_DIR/fs-results" -type d -name "fs-results-*" 2>/dev/null | wc -l | tr -d ' ')
    if [ "$env_specific_dirs" -eq 0 ]; then
        json_files=$(find "$SOURCE_DIR/fs-results" -maxdepth 1 -name "*.json" -type f 2>/dev/null)
        if [ -n "$json_files" ]; then
            echo "   ⚠️  WARNING: No environment-specific directories found, processing root files for $env only"
            echo "   🔄 Converting FS test results for $env (found $json_count JSON file(s) in root)..."
            ./scripts/ci/convert-artillery-to-allure.sh "$TARGET_DIR" "$SOURCE_DIR/fs-results" "$env" || true
            ENV_PROCESSED=1
            # Mark that root files have been processed to prevent duplicate processing
            touch "$SOURCE_DIR/fs-results/.root-processed"
        fi
    else
        echo "   ⚠️  Environment-specific directories exist but not found for $env, skipping root fallback"
    fi
fi
```

**Rationale:**
- Prevents same files from being processed multiple times
- Only processes root files if no environment-specific directories exist
- Adds warning to identify when fallback is used

### Fix 5: Add Environment Validation (MEDIUM PRIORITY)

**Action:** Verify environment-specific directories exist before processing

**File:** `scripts/ci/prepare-combined-allure-results.sh` (lines 275-299)

**Add Before Processing:**
```bash
# Verify environment-specific directory structure
if [ -d "$env_dir" ]; then
    echo "   ✅ Found environment-specific directory: fs-results-$env"
    # Continue with processing...
else
    echo "   ⚠️  Environment-specific directory not found: fs-results-$env"
    echo "   📂 Available directories in fs-results:"
    find "$SOURCE_DIR/fs-results" -maxdepth 1 -type d 2>/dev/null | while read d; do
        echo "      - $(basename "$d")"
    done
fi
```

**Rationale:**
- Provides visibility into actual artifact structure
- Helps identify when expected structure doesn't match reality
- Aids in debugging artifact download/merge issues

### Fix 6: Enhanced Debug Logging (MEDIUM PRIORITY)

**Action:** Log which files are processed for which environment

**File:** `scripts/ci/prepare-combined-allure-results.sh`

**Add After Processing:**
```bash
if [ "$ENV_PROCESSED" -eq 1 ]; then
    echo "   📊 Files processed for $env:"
    find "$artillery_dir" -name "*.json" -type f 2>/dev/null | while read f; do
        size=$(stat -f%z "$f" 2>/dev/null || stat -c%s "$f" 2>/dev/null || echo "unknown")
        echo "      - $(basename "$f") (size: $size bytes)"
    done
fi
```

**Rationale:**
- Provides audit trail of which files were processed
- Helps identify duplicate processing
- Aids in debugging conversion issues

### Fix 7: Verify Test Configs Have Different Values (LOW PRIORITY)

**Action:** Ensure full configs (not just smoke) have different values

**Files:** `playwright/artillery/config/dev.yml`, `playwright/artillery/config/test.yml`

**Current State:**
- `dev.yml`: Warm-up `arrivalRate: 5`
- `test.yml`: Warm-up `arrivalRate: 4`

**Status:** ✅ Already different (good)

**Note:** Smoke configs are the issue, not full configs

## Implementation Priority

1. **Fix 1 (Fix Fallback Logic for ALL Frameworks)** - **CRITICAL**
   - Affects ALL test frameworks
   - Root cause of identical results across environments
   - High complexity (multiple files to update)
   - **MUST FIX FIRST**

2. **Fix 2 (Differentiate Smoke Configs)** - **HIGH**
   - FS-specific issue
   - Quick fix, high impact
   - Will immediately show different results in Allure
   - Low risk

3. **Fix 3 (Improve Environment Detection)** - **MEDIUM**
   - Affects TestNG-based tests (Selenide, etc.)
   - Improves accuracy of environment labeling
   - Medium complexity

4. **Fix 5 (Environment Validation)** - **MEDIUM**
   - Improves debugging
   - Helps identify issues early
   - Low complexity

5. **Fix 6 (Enhanced Logging)** - **MEDIUM**
   - Improves observability
   - Helps with future debugging
   - Low complexity

6. **Fix 7 (Verify Configs)** - **LOW**
   - Already verified as different
   - No action needed

## Testing Plan

After implementing fixes:

1. **Run Pipeline and Verify:**
   - Check artifact structure in debug logs for ALL frameworks
   - Verify different metrics for dev vs test vs prod
   - Confirm no duplicate processing
   - Check for fallback warnings

2. **Check Allure Report:**
   - Verify dev, test, and prod results show different metrics
   - Confirm environment labels are correct for ALL frameworks
   - Verify no duplicate test entries
   - Check that each environment has distinct results

3. **Validate Processing:**
   - Check processing logs for each framework and environment
   - Verify correct files are processed
   - Confirm environment-specific directories are found
   - Verify fallback logic is NOT triggered (or understand why it is)

4. **Verify Environment Detection:**
   - Check marker files created by merge-allure-results.sh
   - Verify environment labels in Allure JSON files
   - Confirm TestNG-based tests have correct environment labels

## Related Files

### Framework-Specific Converters:
- `scripts/ci/convert-artillery-to-allure.sh` - FS test conversion
- `scripts/ci/convert-cypress-to-allure.sh` - Cypress conversion
- `scripts/ci/convert-playwright-to-allure.sh` - Playwright conversion
- `scripts/ci/convert-robot-to-allure.sh` - Robot Framework conversion
- `scripts/ci/convert-vibium-to-allure.sh` - Vibium conversion
- `scripts/ci/prepare-be-results.sh` - BE performance tests conversion

### Processing Scripts:
- `scripts/ci/prepare-combined-allure-results.sh` - Main results processing (ALL frameworks)
- `scripts/ci/merge-allure-results.sh` - TestNG-based tests merging
- `scripts/ci/add-environment-labels.sh` - Environment label addition

### Configuration Files:
- `playwright/artillery/config/dev-smoke.yml` - Dev smoke test config
- `playwright/artillery/config/test-smoke.yml` - Test smoke test config
- `playwright/artillery/config/dev.yml` - Dev full config
- `playwright/artillery/config/test.yml` - Test full config

### Workflow Files:
- `.github/workflows/ci.yml` - CI/CD pipeline definition
- `.github/workflows/env-fs.yml` - FS test reusable workflow
- `.github/workflows/env-fe.yml` - FE test reusable workflow
- `.github/workflows/env-be.yml` - BE test reusable workflow

## Simple Verification Mechanism (MINIMAL CHANGES)

**Goal:** Add simple markers to verify results are from different test runs/environments without changing test configs or data.

### Solution: Add Run Metadata to Allure Results

Add the following to each Allure result to prove it's from a different run:

1. **Base URL** - Which environment URL was used (proves different environment)
2. **Run Timestamp** - When the test actually ran (proves different execution time)
3. **CI Run ID** - Unique identifier for the CI/CD run (proves same or different pipeline run)
4. **Environment Variable** - Explicit environment name (already exists)

**Implementation:** Add these as Allure parameters in each converter script.

**Example for FS Tests (`convert-artillery-to-allure.sh`):**
```python
# After line 131, add verification metadata:
import os
from datetime import datetime

params = []
if env and env not in ["unknown", "combined"]:
    params.append({"name": "Environment", "value": env.upper()})
    # Add verification metadata - use actual test execution timestamp
    params.append({"name": "Base URL", "value": os.environ.get("BASE_URL", "unknown")})
    # Use firstMetricAt from Artillery results (actual test execution time)
    test_timestamp = datetime.fromtimestamp(first_metric_at / 1000).isoformat() if first_metric_at > 0 else datetime.now().isoformat()
    params.append({"name": "Test Execution Time", "value": test_timestamp})
    params.append({"name": "CI Run ID", "value": os.environ.get("GITHUB_RUN_ID", "local")})
    params.append({"name": "CI Run Number", "value": os.environ.get("GITHUB_RUN_NUMBER", "unknown")})
```

**Example for Cypress (`convert-cypress-to-allure.sh`):**
```python
# In Python section, after line 237:
params = []
if env and env not in ["unknown", "combined"]:
    params.append({"name": "Environment", "value": env.upper()})
    # Add verification metadata
    import os
    from datetime import datetime
    params.append({"name": "Base URL", "value": os.environ.get("BASE_URL", os.environ.get("CYPRESS_baseUrl", "unknown"))})
    params.append({"name": "Run Timestamp", "value": datetime.now().isoformat()})
    params.append({"name": "CI Run ID", "value": os.environ.get("GITHUB_RUN_ID", "local")})
```

**Verification in Allure Report:**
- Open any test result → "Parameters" tab
- Verify "Base URL" is different for dev vs test vs prod:
  - Dev: `http://localhost:3003`
  - Test: `http://localhost:3004`
  - Prod: `http://localhost:3005`
- Verify "Test Execution Time" shows different timestamps (proves different test runs)
- Verify "CI Run ID" is the same (proves same pipeline run) or different (proves different runs)

**What This Proves:**
- ✅ **Different Base URLs** = Tests ran against different environments
- ✅ **Different Execution Times** = Tests ran at different times (different runs)
- ✅ **Same CI Run ID** = All results from same pipeline execution
- ✅ **Different CI Run IDs** = Results from different pipeline runs

**If Results Are Duplicates:**
- Same Base URL across environments = ❌ Same file processed multiple times
- Same Execution Time across environments = ❌ Same file processed multiple times
- Different Base URLs + Different Execution Times = ✅ Different test runs

**Benefits:**
- ✅ Minimal changes (~5-10 lines per converter script)
- ✅ No test config changes needed
- ✅ No test data changes needed
- ✅ Easy to verify in Allure UI (just check Parameters tab)
- ✅ Works for all frameworks
- ✅ Proves results are from different environments/runs

**Files to Update (add metadata to params):**
- `scripts/ci/convert-artillery-to-allure.sh` - Add BASE_URL, timestamp, GITHUB_RUN_ID
- `scripts/ci/convert-cypress-to-allure.sh` - Add BASE_URL, timestamp, GITHUB_RUN_ID
- `scripts/ci/convert-playwright-to-allure.sh` - Add BASE_URL, timestamp, GITHUB_RUN_ID
- `scripts/ci/convert-robot-to-allure.sh` - Add BASE_URL, timestamp, GITHUB_RUN_ID
- `scripts/ci/convert-vibium-to-allure.sh` - Add BASE_URL, timestamp, GITHUB_RUN_ID
- `scripts/ci/prepare-be-results.sh` - Add BASE_URL, timestamp, GITHUB_RUN_ID

**Estimated Changes:** ~5-10 lines per converter script = ~30-60 lines total across all frameworks

**Alternative: If BASE_URL not available, use:**
- Test execution timestamp (always different)
- CI workflow run ID (different per run)
- Environment name (already exists, but verify it's correct)

## Notes

- FS tests should **never** run in prod (by design)
- If prod results appear, investigate source
- Smoke tests are intentionally quick (10 seconds)
- Full load tests use different configs with different arrival rates
- Artifact structure depends on `merge-multiple: true` behavior
- **It's OK if test results are identical** - we just need to verify they're from different runs

## Status

- [ ] Fix 1: Fix fallback logic for ALL frameworks (CRITICAL)
- [ ] Fix 2: Differentiate smoke test configs (FS-specific)
- [ ] Fix 3: Improve environment detection for TestNG tests
- [ ] Fix 5: Add environment validation
- [ ] Fix 6: Enhanced debug logging
- [ ] Fix 7: Verify test configs (already verified)

---

## Summary

**Root Cause:** The fallback logic in `prepare-combined-allure-results.sh` processes the same merged artifact files for each environment (dev, test, prod), creating duplicate Allure results with different environment labels but identical test data. This affects **ALL test frameworks**, not just FS tests.

**Key Insight:** It's acceptable if tests produce identical results - the issue is **verifying that results are truly from different test runs in different environments**, not that the results themselves are different.

**Primary Fix Required:** 
1. **Fix fallback logic** for ALL frameworks to prevent duplicate processing
2. **Add verification metadata** (Base URL, Timestamp, Run ID) to prove results are from different runs

**Simple Verification Solution:**
- Add Base URL, Run Timestamp, and CI Run ID as Allure parameters
- Minimal changes (~5-10 lines per converter script)
- No test config or data changes needed
- Easy to verify in Allure UI (check Parameters tab)

**Secondary Fixes:**
- Differentiate FS smoke test configs (optional, for visual differentiation)
- Improve environment detection for TestNG-based tests
- Add validation and logging

**Next Steps:** 
1. **IMMEDIATE:** Add verification metadata to all converter scripts (simple, proves different runs)
2. **CRITICAL:** Implement Fix 1 (fallback logic) for all frameworks (prevents duplicates)
3. **OPTIONAL:** Implement Fix 2 (differentiate smoke configs) if desired
4. **MEDIUM:** Implement Fix 3 (environment detection improvements)

---

## Implementation Steps for Simple Verification Mechanism

**Status:** ✅ **COMPLETED AND REFACTORED** - All converters now use shared utility functions.

**What It Does:**
- Adds Base URL, Test Execution Time, CI Run ID, and CI Run Number as Allure parameters
- These parameters are visible in Allure UI under "Parameters" tab
- Allows easy verification that results are from different environments/runs
- No test config or data changes needed

**Why This Works:**
- **Base URL** proves different environments (localhost:3003 vs 3004 vs 3005)
- **Test Execution Time** proves different execution times (even if seconds apart)
- **CI Run ID** proves same or different pipeline runs
- If all three are different = ✅ Confirmed different test runs

**Implementation Approach:**
- ✅ **Shared Utilities Created**: All converters now use centralized utility functions
- ✅ **Python Module**: `scripts/ci/allure_metadata_utils.py` - Provides `add_verification_metadata_to_params()` function
- ✅ **Bash Functions**: `scripts/ci/allure-metadata-utils.sh` - Provides `get_verification_metadata_json()` function
- ✅ **DRY Principle**: Single source of truth for metadata generation, eliminates code duplication
- ✅ **Fallback Support**: All converters include inline fallback functions if import fails

### Step 1: Update FS Test Converter (Artillery) ✅ COMPLETED & REFACTORED

**File:** `scripts/ci/convert-artillery-to-allure.sh`

**Status:** ✅ Implemented and refactored to use shared utility

**Implementation:**
- Imports `add_verification_metadata_to_params()` from `allure_metadata_utils.py`
- Uses `first_metric_at` timestamp from Artillery results (actual test execution time)
- Single line call: `params = add_verification_metadata_to_params(params, env, first_metric_at)`

**Shared Utility:** `scripts/ci/allure_metadata_utils.py`

### Step 2: Update Cypress Converter ✅ COMPLETED & REFACTORED

**File:** `scripts/ci/convert-cypress-to-allure.sh`

**Status:** ✅ Implemented and refactored to use shared utility

**Implementation:**
- Imports `add_verification_metadata_to_params()` from `allure_metadata_utils.py`
- Uses test execution timestamp from Cypress results
- Overrides Base URL if `CYPRESS_baseUrl` is available
- Single line call: `params = add_verification_metadata_to_params(params, env, timestamp, "BASE_URL")`

**Shared Utility:** `scripts/ci/allure_metadata_utils.py`

### Step 3: Update Playwright Converter ✅ COMPLETED & REFACTORED

**File:** `scripts/ci/convert-playwright-to-allure.sh`

**Status:** ✅ Implemented and refactored to use shared utility

**Implementation:**
- Imports `add_verification_metadata_to_params()` from `allure_metadata_utils.py`
- Uses timestamp from Playwright test results
- Overrides Base URL if `PLAYWRIGHT_BASE_URL` is available
- Single line call: `params = add_verification_metadata_to_params(params, env, timestamp, "BASE_URL")`

**Shared Utility:** `scripts/ci/allure_metadata_utils.py`

### Step 4: Update Robot Framework Converter ✅ COMPLETED & REFACTORED

**File:** `scripts/ci/convert-robot-to-allure.sh`

**Status:** ✅ Implemented and refactored to use shared utility (both individual tests and fallback summary)

**Implementation:**
- Imports `add_verification_metadata_to_params()` from `allure_metadata_utils.py`
- Uses current time (Robot Framework doesn't provide execution timestamp)
- Overrides Base URL if `ROBOT_BASE_URL` is available
- Single line call: `params = add_verification_metadata_to_params(params, env, None, "BASE_URL")`

**Shared Utility:** `scripts/ci/allure_metadata_utils.py`

**Previous Implementation:**
```python
# Find this section (around line 157-159):
params = []
if env and env not in ["unknown", "combined"]:
    params.append({"name": "Environment", "value": env.upper()})

# Add after line 159 (inside the if block):
    # Add verification metadata
    import os
    from datetime import datetime
    params.append({"name": "Base URL", "value": os.environ.get("BASE_URL", os.environ.get("ROBOT_BASE_URL", "unknown"))})
    # Use current time (Robot Framework doesn't provide execution timestamp in output.xml)
    params.append({"name": "Test Execution Time", "value": datetime.now().isoformat()})
    params.append({"name": "CI Run ID", "value": os.environ.get("GITHUB_RUN_ID", "local")})
    params.append({"name": "CI Run Number", "value": os.environ.get("GITHUB_RUN_NUMBER", "unknown")})
```

**Note:** Robot Framework's output.xml doesn't include execution timestamp, so we use current time during conversion.

### Step 5: Update Vibium Converter ✅ COMPLETED

**File:** `scripts/ci/convert-vibium-to-allure.sh`

**Location:** After line 145 (where params are created)

**Status:** ✅ Implemented - Verification metadata added to Vibium converter (both suite-level and individual test cases)

**Change:**
```python
# Find this section (around line 143-145):
params = []
if env and env not in ["unknown", "combined"]:
    params.append({"name": "Environment", "value": env.upper()})

# Add after line 145 (inside the if block):
    # Add verification metadata
    import os
    from datetime import datetime
    params.append({"name": "Base URL", "value": os.environ.get("BASE_URL", os.environ.get("VIBIUM_BASE_URL", "unknown"))})
    # Use suite execution time if available, otherwise current time
    test_timestamp = datetime.fromtimestamp(suite_duration).isoformat() if suite_duration > 0 else datetime.now().isoformat()
    params.append({"name": "Test Execution Time", "value": test_timestamp})
    params.append({"name": "CI Run ID", "value": os.environ.get("GITHUB_RUN_ID", "local")})
    params.append({"name": "CI Run Number", "value": os.environ.get("GITHUB_RUN_NUMBER", "unknown")})
```

**Note:** Use suite duration timestamp if available from Vibium results.

### Step 6: Update BE Performance Tests Converter ✅ COMPLETED & REFACTORED

**File:** `scripts/convert-performance-to-allure.sh`

**Status:** ✅ Implemented and refactored to use shared utility

**Implementation:**
- Sources `allure-metadata-utils.sh` to get `get_verification_metadata_json()` function
- Uses bash function to generate JSON metadata
- Applies to all BE performance tests (Gatling, JMeter, Locust)
- Single function call: `verification_metadata=$(get_verification_metadata_json "$environment")`

**Shared Utility:** `scripts/ci/allure-metadata-utils.sh`

### Step 7: Test and Verify

1. **Run Pipeline:**
   - Commit and push changes
   - Wait for pipeline to complete

2. **Check Allure Report:**
   - Open any test result
   - Go to "Parameters" tab
   - Verify:
     - **Base URL** is different for each environment:
       - Dev: `http://localhost:3003`
       - Test: `http://localhost:3004`
       - Prod: `http://localhost:3005`
     - **Test Execution Time** shows different timestamps (proves different test runs)
     - **CI Run ID** is the same (proves same pipeline run) or different (proves different runs)

3. **Verify Results Are From Different Runs:**
   - ✅ **Different Base URLs** = Tests ran against different environments
   - ✅ **Different Execution Times** = Tests ran at different times (different runs)
   - ✅ **Same CI Run ID** = All results from same pipeline execution
   - ❌ **Same Base URL + Same Execution Time** = Same file processed multiple times (fallback issue)

### Step 8: Monitor and Adjust

- If Base URL shows "unknown" for any framework:
  - Check environment variable names in workflow files
  - Verify BASE_URL is set in workflow steps
- If timestamps are identical across environments:
  - Investigate if fallback logic is processing same files
  - Check artifact structure after download
- If CI Run ID is missing:
  - Verify GITHUB_RUN_ID is available in workflow context
  - Check if running locally (will show "local")

**Estimated Time:** 30-60 minutes for all frameworks

**Risk Level:** Low - Only adding parameters, not changing conversion logic

**Rollback:** Easy - Just remove the added parameter lines

**Testing:** After implementation, verify in Allure report Parameters tab that:
- Base URLs are different per environment
- Execution times are different
- CI Run IDs are present

---

## Post-Implementation Analysis (January 2, 2026)

### Metadata Implementation Status: ✅ COMPLETED

All converter scripts have been updated to include verification metadata:
- ✅ FS (Artillery) tests - `convert-artillery-to-allure.sh`
- ✅ Cypress tests - `convert-cypress-to-allure.sh`
- ✅ Playwright tests - `convert-playwright-to-allure.sh`
- ✅ Robot Framework tests - `convert-robot-to-allure.sh`
- ✅ Vibium tests - `convert-vibium-to-allure.sh`
- ✅ BE Performance tests - `convert-performance-to-allure.sh`

**Shared Utilities Created:**
- ✅ `scripts/ci/allure_metadata_utils.py` - Python utility for metadata generation
- ✅ `scripts/ci/allure-metadata-utils.sh` - Bash utility for metadata generation

### GitHub Results Verification

**Date:** January 2, 2026  
**Pipeline Run:** Post-PR #50 merge

**Metadata Analysis Results:**

| Parameter | Dev | Test | Prod | Status |
|-----------|-----|------|------|--------|
| **Environment** | DEV | TEST | PROD | ✅ Different (as expected) |
| **Base URL** | Same | Same | Same | ❌ **IDENTICAL** (should be different) |
| **Test Execution Time** | Same | Same | Same | ❌ **IDENTICAL** (should be different) |
| **CI Run ID** | Same | Same | Same | ✅ Same (expected - same pipeline) |
| **CI Run Number** | Same | Same | Same | ✅ Same (expected - same pipeline) |

**Key Finding:**
The metadata confirms that **only the Environment parameter differs** across environments. All other metadata (Base URL, Test Execution Time) is identical, which proves the fallback logic is still processing the same artifact files for each environment.

**Root Cause Confirmed:**
The fallback logic in `prepare-combined-allure-results.sh` is processing the same merged artifact files for each environment in the loop, creating duplicate Allure results with:
- ✅ Different environment labels (DEV, TEST, PROD)
- ❌ Identical test data and metrics
- ❌ Identical Base URLs (should be :3003, :3004, :3005)
- ❌ Identical execution timestamps (should be different if from different runs)

### Next Steps

**Priority:** HIGH - The fallback logic issue needs to be fixed to prevent duplicate processing.

**Recommended Actions:**
1. **Fix Fallback Logic:** Update `prepare-combined-allure-results.sh` to:
   - Only process environment-specific artifact subdirectories
   - Skip fallback processing if environment-specific directories are not found
   - Add explicit checks to prevent processing the same files multiple times

2. **Improve Artifact Structure:** Ensure artifacts are uploaded with environment-specific directory structures to avoid triggering fallback logic

3. **Add Validation:** Add checks to verify Base URLs differ before processing, and fail if they don't

4. **Monitor:** After fixes, verify in Allure report that:
   - Base URLs are different per environment
   - Test Execution Times are different (or at least not identical)
   - Results truly represent different test runs

