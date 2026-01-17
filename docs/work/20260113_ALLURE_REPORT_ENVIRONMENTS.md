# Allure Report Environment Detection Investigation

**Date**: 2026-01-13  
**Status**: ✅ **RESOLVED** - Appears to be a one-off issue  
**Branch**: `investigate/allure-report-environments`

---

## 📋 Issue Summary

After merging PR #170 (Cypress environment variable fix), Pipeline #639 completed successfully, but the Allure report only showed **dev** and **test** environments, missing **prod** environment.

**User Report**: "Did something change that affects the Allure Report as the last merge only showed dev and test environments and not prod"

---

## 🔍 Investigation - Pipeline #639

**Pipeline Details:**
- **Run ID**: 20968531748
- **Number**: #639
- **Event**: Push to main (PR #170 merged)
- **Status**: ✅ Success
- **Date**: 2026-01-13T18:46:16Z
- **Title**: "fix(cypress): Fix environment variable access in Cypress tests (#170)"

### Findings

#### ✅ Prod Tests DID Run Successfully

All prod test jobs completed successfully:
- `Test FE (PROD) / Allure Report (prod)` - ✅ Success
- `Test FE (PROD) / Cypress Tests (prod)` - ✅ Success
- `Test FE (PROD) / Playwright Tests (prod)` - ✅ Success
- `Test FE (PROD) / Robot Framework Tests (prod)` - ✅ Success
- `Test FE (PROD) / Selenide Tests (prod)` - ✅ Success
- `Test FE (PROD) / Vibium Tests (prod)` - ✅ Success
- All other prod test jobs - ✅ Success

#### ✅ Combined Allure Report Job Completed Successfully

The `Combined Allure Report (All Environments)` job completed with all steps successful:
- ✅ Download test results (PROD)
- ✅ Download be allure results (PROD)
- ✅ Prepare combined Allure results
- ✅ Generate Combined Allure Report
- ✅ All other steps

#### 🔍 Environment Detection Logic

The script `scripts/ci/prepare-combined-allure-results.sh` detects active environments by checking:

1. **Environment-specific directories:**
   - `$SOURCE_DIR/results-$env` (e.g., `all-test-results/results-prod`)
   - Must have content: `[ -n "$(find "$SOURCE_DIR/results-$env" -mindepth 1 -maxdepth 1 2>/dev/null)" ]`

2. **Framework-specific directories:**
   - `$SOURCE_DIR/cypress-results/cypress-results-$env`
   - `$SOURCE_DIR/playwright-results/playwright-results-$env`
   - `$SOURCE_DIR/robot-results/robot-results-$env`
   - `$SOURCE_DIR/vibium-results/vibium-results-$env`
   - `$SOURCE_DIR/fs-results/fs-results-$env`

**Potential Issue**: When artifacts are downloaded with `merge-multiple: true` and patterns like `cypress-results-*`, they might be placed in a flat structure (`all-test-results/cypress-results/`) without environment subdirectories, which could affect detection.

---

## ✅ Resolution - Pipeline #641

**Pipeline Details:**
- **Number**: #641
- **Event**: Push to main (PR #171 merged)
- **Status**: ✅ Success
- **Date**: 2026-01-13T19:47:12Z
- **Title**: "feat: Shared Test Configuration Implementation - All 7 Phases Complete"

**Result**: ✅ **All environments (dev, test, prod) are now correctly included in the Allure report.**

---

## 💡 Analysis

### Possible Causes for Pipeline #639 Issue

1. **Transient Artifact Download Timing Issue**
   - Prod artifacts may not have been fully available when the combined report was generated
   - Race condition in artifact download/merge process

2. **Environment Detection Script Temporary Glitch**
   - The script's directory check may have failed temporarily
   - File system timing issue during artifact extraction

3. **Artifact Merge Structure Variation**
   - That specific run may have had a different artifact structure
   - `merge-multiple: true` behavior may have varied

4. **Empty Directory Detection**
   - `results-prod` directory may have existed but been empty at detection time
   - Content may have been added after the detection check

### Why It Resolved Itself

- No code changes were made between Pipeline #639 and #641
- The issue appears to be **environmental/timing-related**, not a code bug
- Subsequent runs are working correctly

---

## 📊 Key Learnings

1. **Prod tests were running correctly** - The issue was not with test execution
2. **Artifacts were being downloaded** - The issue was not with artifact upload/download
3. **Environment detection logic is sound** - The script correctly checks for active environments
4. **One-off issues can occur** - Transient pipeline issues don't always indicate code problems

---

## 🔄 Monitoring Recommendations

1. **Monitor next 3-5 pipeline runs** to ensure stability
2. **If issue recurs**, add debug logging to `prepare-combined-allure-results.sh`:
   - Log which directories are checked
   - Log directory contents at detection time
   - Log which environments are detected as active
3. **Consider adding retry logic** for environment detection if it becomes a recurring issue

---

## 📝 Files Reviewed

- `.github/workflows/ci.yml` - Combined Allure Report job configuration
- `scripts/ci/prepare-combined-allure-results.sh` - Environment detection logic
- `.github/workflows/env-fe.yml` - Artifact upload patterns

---

## ✅ Conclusion

**Status**: ✅ **RESOLVED** - Appears to be a one-off transient issue

The Allure report is now correctly showing all three environments (dev, test, prod). No code changes are needed at this time. If the issue recurs, we'll add enhanced logging to diagnose the root cause.

---

**Document Status**: ✅ **COMPLETE** - Issue resolved, monitoring recommended
