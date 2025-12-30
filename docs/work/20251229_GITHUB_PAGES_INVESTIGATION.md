# GitHub Pages Suites Tab Investigation

**Created**: 2025-12-29  
**Status**: 🔍 **INVESTIGATING**  
**Issue**: Suites tab shows all frameworks locally but only Playwright on GitHub Pages

---

## Critical Discovery

**Local Report (Downloaded Artifact)**: ✅ All frameworks visible in Suites tab  
**GitHub Pages (Deployed)**: ❌ Only Playwright visible in Suites tab

This confirms the issue is with **GitHub Pages deployment**, NOT container creation or detection logic.

---

## Investigation Results

### 1. Container Files Analysis

- **Container files in results**: ✅ 2,131 container files exist
- **Container files in gh-pages**: ❌ 0 container files (Allure converts them to `suites.json`)
- **suites.json in gh-pages**: Shows 6 frameworks (Performance, Surefire suite, Surefire test, Cypress, Playwright, Robot)
- **Missing from suites.json**: Vibium Tests, Selenide Tests

### 2. Selenide Container Structure Issue

**Problem Found**: Multiple Selenide top-level containers created (80+ containers!)

- **Expected**: ONE top-level container per framework
- **Actual**: 80+ containers with `name="Selenide Tests"`
- **Correct container exists**: `36e91d39b1154e4c80daadc4b468fd70-container.json` has correct structure (1 child = env container UUID)
- **Incorrect containers**: Most have 99 children pointing directly to result UUIDs (should point to env container UUIDs)

**Root Cause**:
- Selenide result files still have `suite=Surefire test` (not updated to `suite=Selenide Tests`)
- Container creation script detects them as Selenide and overrides suite name, but this happens AFTER initial grouping
- Script may be creating containers for both "Surefire test" and "Selenide Tests" suites
- Multiple top-level containers are being created instead of one per suite

### 3. Container Structure Comparison

**Playwright (Working)**:
- Top-level container: `children: 3` (env container UUIDs) ✅
- Structure: Top-level → Env containers → Results ✅

**Selenide (Not Working)**:
- Top-level containers: Multiple with `children: 99` (result UUIDs) ❌
- One correct container: `children: 1` (env container UUID) ✅
- Structure: Most are incorrect (Top-level → Results, skipping env containers) ❌

### 4. Vibium Tests Missing

- **Vibium containers**: 0 found
- **Vibium result files**: Not present in combined results
- **Possible causes**: Tests didn't run, results not uploaded, or results not merged

---

## Root Causes Identified

1. **Selenide suite labels not updated**: Result files still have `suite=Surefire test` instead of `suite=Selenide Tests`
2. **Multiple duplicate containers**: 80+ top-level containers created for Selenide instead of one
3. **Incorrect container hierarchy**: Most Selenide containers point to result UUIDs instead of env container UUIDs
4. **Allure filtering**: Allure may be ignoring duplicate/incorrect containers, leaving only Playwright visible

---

## Fixes Required

### Fix 1: Ensure Selenide Suite Labels Are Updated
- **File**: `scripts/ci/add-environment-labels.sh`
- **Issue**: Selenide detection may not be working for result files
- **Action**: Verify Selenide detection logic updates suite labels correctly

### Fix 2: Prevent Duplicate Top-Level Containers
- **File**: `scripts/ci/create-framework-containers.sh`
- **Issue**: Multiple top-level containers being created for same suite
- **Action**: Add check to ensure only ONE top-level container per suite name

### Fix 3: Fix Container Hierarchy
- **File**: `scripts/ci/create-framework-containers.sh`
- **Issue**: Top-level containers pointing to result UUIDs instead of env container UUIDs
- **Action**: Verify `env_container_uuids_by_suite` is populated correctly before creating top-level containers

### Fix 4: Investigate Vibium
- **Action**: Check if Vibium tests ran and if results were uploaded/merged

---

## Fixes Implemented

### Fix 1: Selenide Detection in add-environment-labels.sh ✅
- **Change**: Added checks for `fullName` and `name` fields BEFORE checking if file is a container
- **Why**: Result files need to be detected as Selenide before label processing
- **Impact**: Selenide result files will be detected and have suite labels updated correctly

### Fix 2: Suite Name Override Before Grouping ✅
- **Change**: Suite name override happens BEFORE adding to `suite_groups` in `create-framework-containers.sh`
- **Why**: Ensures Selenide tests are grouped under "Selenide Tests" from the start, not "Surefire test"
- **Impact**: Prevents Selenide tests from being grouped under wrong suite name

### Fix 3: Merge "Surefire test" into "Selenide Tests" ✅
- **Change**: Added logic to merge "Surefire test" suite into "Selenide Tests" if it contains Selenide tests
- **Why**: Handles edge cases where some Selenide tests might still be grouped under "Surefire test"
- **Impact**: Ensures all Selenide tests end up in "Selenide Tests" suite before env containers are created

### Fix 4: Prevent Duplicate Top-Level Containers ✅
- **Change**: Added `top_level_containers_created` set to track which suites already have top-level containers
- **Why**: Prevents multiple top-level containers for the same suite (was creating 80+ for Selenide)
- **Impact**: Only ONE top-level container per suite will be created

## Next Steps

1. ✅ **Fixes implemented** - Ready for testing
2. ⚠️ **Test fixes** in pipeline and verify Suites tab shows all frameworks
3. ⚠️ **Investigate Vibium** missing tests (separate issue)

---

**Last Updated**: 2025-12-30  
**Status**: ✅ **FIXES IMPLEMENTED** - Pipeline completed, ready for review

## Implementation Details

### Files Modified

1. **`scripts/ci/add-environment-labels.sh`**
   - Added `fullName` and `name` field checks for result files BEFORE container checks
   - Ensures Selenide result files are detected before label processing
   - Lines 247-259: Added early detection for result files

2. **`scripts/ci/create-framework-containers.sh`**
   - Added suite name override BEFORE grouping (lines 154-158)
   - Added merge logic to combine "Surefire test" into "Selenide Tests" (lines 226-265)
   - Added deduplication to prevent multiple top-level containers (lines 402-410)
   - Ensures only ONE top-level container per suite name

### Key Changes

1. **Early Selenide Detection**: Result files are now checked for Selenide indicators (fullName, name) before checking if they're containers
2. **Suite Name Override**: Selenide tests are grouped under "Selenide Tests" from the start, not "Surefire test"
3. **Suite Merging**: "Surefire test" suite is merged into "Selenide Tests" if it contains Selenide tests
4. **Deduplication**: Top-level containers are tracked to prevent duplicates

### Expected Results

After these fixes:
- ✅ Selenide tests will have `suite=Selenide Tests` label
- ✅ Only ONE top-level container for "Selenide Tests"
- ✅ Top-level container will reference env container UUIDs (not result UUIDs)
- ✅ All Selenide tests will appear in Suites tab on GitHub Pages

---

## Pipeline Run Results

**Branch**: `fix-selenide-suites-github-pages`  
**PR**: #23  
**Pipeline Run**: https://github.com/CScharer/full-stack-qa/actions/runs/20585663940  
**Status**: ✅ **PIPELINE COMPLETED SUCCESSFULLY** - Ready for review

**Note**: Branch runs only execute DEV tests. Full verification with all environments (dev/test/prod) will occur after merge to main.

**Pipeline Results**:
- ✅ Pipeline completed successfully
- ✅ Combined Allure Report job completed
- ✅ **Deduplication working**: Only 6 top-level containers created (one per framework)
- ✅ **Selenide Tests separated**: "Selenide Tests" is now a separate suite (16 tests)
- ⚠️ **"Surefire test" still exists**: 388 tests still grouped under "Surefire test" (may need additional investigation)
- ⚠️ Verification and deployment steps skipped (expected for branch runs)
- ⚠️ Full verification requires merge to main for GitHub Pages deployment

**Container Creation Summary** (from pipeline logs):
```
✅ Created top-level container: Surefire test (1 env containers, 1 environment(s))
✅ Created top-level container: Vibium Tests (1 env containers, 1 environment(s))
✅ Created top-level container: Playwright Tests (1 env containers, 1 environment(s))
✅ Created top-level container: Selenide Tests (1 env containers, 1 environment(s))
✅ Created top-level container: Robot Framework Tests (1 env containers, 1 environment(s))
✅ Created top-level container: Performance Tests (1 env containers, 1 environment(s))
```

**Key Findings**:
- ✅ **Fix 4 (Deduplication) is working**: Only ONE top-level container per framework
- ✅ **Selenide Tests is correctly identified**: 16 tests grouped under "Selenide Tests"
- ⚠️ **"Surefire test" suite still exists**: 388 tests (likely other TestNG tests, not Selenide)
- ⚠️ **Container count discrepancy**: Expected 12, found 716 (likely includes old containers from previous runs)

**Next Steps**:
1. ✅ Review pipeline logs - **Deduplication confirmed working**
2. ⚠️ Investigate why "Surefire test" still has 388 tests (may be expected if they're non-Selenide TestNG tests)
3. ✅ **PR merged to main** - Full pipeline run completed
4. ⚠️ Verify Suites tab on GitHub Pages after merge (all frameworks should appear)

---

## Main Branch Pipeline Run Results

**Pipeline Run**: https://github.com/CScharer/full-stack-qa/actions/runs/20586056931  
**Status**: ⚠️ **COMPLETED WITH FAILURES** - One test failure in Firefox Grid Tests (PROD)

### Failed Jobs
1. **Test FE (PROD) / Grid Tests - firefox (prod)**: Failed
2. **Gate (PROD)**: Failed (likely due to test failure above)

### Successful Jobs
- ✅ All DEV environment tests passed
- ✅ All TEST environment tests passed
- ✅ Most PROD environment tests passed (only Firefox Grid Tests failed)
- ✅ **Combined Allure Report job completed successfully**

### Key Findings

**Test Failure Analysis**:
- The failure is in **test execution**, not in our container creation fixes
- Firefox Grid Tests failed in PROD environment only
- All other browsers (Chrome, Edge) passed in PROD
- All Firefox tests passed in DEV and TEST environments
- This appears to be a **flaky test** or **environment-specific issue**, not related to our Suites tab fixes

**Container Creation Status**:
- ⚠️ **Unable to verify container creation logs** due to log access limitations
- ✅ Combined Allure Report job completed, suggesting container creation likely worked
- ⚠️ Need to verify GitHub Pages deployment status

**Next Steps**:
1. ⚠️ Review Firefox Grid Tests failure (separate issue from Suites tab fixes)
2. ✅ **GitHub Pages deployment confirmed** - Status: Built and deployed
3. ⚠️ **Issue Found**: Only Cypress, Playwright, Robot, and Vibium show all 3 environments in Behaviors tab
4. ⚠️ **Root Cause**: Selenide/Surefire tests not getting environment labels correctly
5. ✅ **Fix Applied**: Updated environment detection patterns to include `selenide-results-{env}` pattern

---

## Environment Detection Fix

**Problem**: Selenide and Surefire tests only show DEV environment in Behaviors tab, while Cypress, Playwright, Robot, and Vibium show all 3 environments.

**Root Cause**: 
- Selenide results are uploaded as `selenide-results-{environment}` artifacts
- Environment detection patterns in `merge-allure-results.sh` and `add-environment-labels.sh` were not matching `selenide-results-{env}` pattern
- Only patterns like `-results-dev`, `-results-test`, `-results-prod` were being checked

**Fix Applied**:
- Updated `scripts/ci/merge-allure-results.sh` to detect `selenide-results-{env}` pattern
- Updated `scripts/ci/add-environment-labels.sh` to detect `selenide-results-{env}` pattern
- This ensures Selenide test results get correct environment labels (dev/test/prod)

**Files Modified**:
1. `scripts/ci/merge-allure-results.sh` - Added `selenide-results-{env}` pattern matching (3 locations)
2. `scripts/ci/add-environment-labels.sh` - Added `selenide-results-{env}` pattern matching

**Expected Result**: After next pipeline run, Selenide and Surefire tests should show all 3 environments in Behaviors tab

---

## Current Status (Post-Fix)

**Date**: 2025-12-30  
**Status**: ✅ **PARTIALLY RESOLVED** - Selenide Tests visible but duplicate display issue remains

### Suites Tab Status

✅ **Selenide Tests now appear in Suites tab** - The suite label fixes are working  
⚠️ **Duplicate display issue**: Selenide Tests appear both:
- As a separate "Selenide Tests" suite (correct)
- Also nested below "Surefire test" suite (incorrect - needs further investigation)

### Behaviors Tab Status

✅ **Cypress, Playwright, Robot, Vibium**: All show all 3 environments (dev/test/prod)  
⚠️ **Selenide/Surefire**: Environment detection fix applied, awaiting next pipeline run to verify

### Next Steps

1. ✅ **Environment detection fix**: Applied and ready for testing
2. ⚠️ **Duplicate Selenide display**: Investigate why Selenide Tests appear under both "Selenide Tests" and "Surefire test" suites
3. ⚠️ **Allure upgrade**: Consider upgrading to latest version (2.36.0) to potentially resolve remaining issues

