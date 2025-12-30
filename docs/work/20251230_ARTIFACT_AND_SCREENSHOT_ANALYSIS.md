# Artifact and Screenshot Capture Analysis

**Date**: 2025-12-29  
**Status**: ✅ **Resolved** (Code fixes complete, artifact behavior documented)

---

## Issues Identified

### 1. Duplicate `captureScreenshot` Methods ✅ **FIXED**

**Problem**:  
Both `MobileBrowserTests.java` and `ResponsiveDesignTests.java` had duplicate private `captureScreenshot` methods, duplicating functionality that already exists in `AllureHelper.java`.

**Files Affected**:
- `src/test/java/com/cjs/qa/junit/tests/mobile/MobileBrowserTests.java`
- `src/test/java/com/cjs/qa/junit/tests/mobile/ResponsiveDesignTests.java`

**Root Cause**:  
Code duplication - each test class implemented its own screenshot capture method instead of using the centralized utility.

**Solution Implemented**:
- ✅ Removed duplicate `captureScreenshot` methods from both files
- ✅ Added `import com.cjs.qa.utilities.AllureHelper;`
- ✅ Updated `tearDown` methods to use `AllureHelper.captureScreenshot()`
- ✅ Enhanced failure handling to also capture page source and browser info (matching other test classes)
- ✅ Removed unused imports (`ByteArrayInputStream`, `OutputType`, `TakesScreenshot`)

**Benefits**:
- Code consistency across all test classes
- Centralized screenshot logic (easier to maintain)
- Enhanced failure diagnostics (page source + browser info)
- Reduced code duplication

---

### 2. Screenshot Capture Behavior ✅ **VERIFIED CORRECT**

**Current Behavior**:  
Screenshots are captured **only on test failure** in the `@AfterMethod tearDown()` method. This is the correct behavior.

**Verification**:
- ✅ `MobileBrowserTests.java`: Screenshots only captured when `result.isSuccess() == false`
- ✅ `ResponsiveDesignTests.java`: Screenshots only captured when `result.isSuccess() == false`
- ✅ Other test classes (`SimpleGridTest`, `EnhancedGridTests`, etc.) follow the same pattern

**If screenshots are appearing for passing tests**, possible causes:
1. Tests are actually failing but marked as passed (check test logs)
2. Screenshots from previous failed runs are still in the report
3. Allure history is preserving old screenshots

**Recommendation**:  
The current implementation is correct. Screenshots should only be captured on failure.

---

### 3. GitHub Actions Artifact Visibility Issue ⚠️ **EXPECTED BEHAVIOR**

**Problem**:  
When tests are re-run, artifact counts increase, but artifacts are only visible in GitHub for the tests that were re-run.

**Root Cause**:  
This is **expected GitHub Actions behavior**. Each workflow run creates separate artifacts, even if they have the same name.

**How GitHub Actions Artifacts Work**:
1. **Artifact Naming**: Artifacts are named with a pattern like `*-results-{environment}` (e.g., `mobile-results-dev`)
2. **Per-Run Artifacts**: Each workflow run creates its own set of artifacts, even with the same name
3. **Artifact Visibility**: GitHub Actions UI shows artifacts from the current run by default
4. **Artifact Retention**: Artifacts are retained for 3-7 days (as configured in workflows)

**Example Scenario**:
```
Run 1 (Initial):
  - mobile-results-dev (100 files)
  - responsive-results-dev (50 files)

Run 2 (Re-run after failure):
  - mobile-results-dev (100 files) ← NEW artifact, separate from Run 1
  - responsive-results-dev (50 files) ← NEW artifact, separate from Run 1
```

**Why Artifacts Appear Only for Re-run Tests**:
- GitHub Actions UI shows artifacts from the **most recent run** by default
- When you navigate to a specific run, you see artifacts from **that specific run**
- Artifacts from previous runs are still available but not shown in the default view

**How to Access Artifacts from Previous Runs**:
1. Navigate to the specific workflow run in GitHub Actions
2. Scroll down to the "Artifacts" section
3. Download artifacts from that specific run

**Artifact Retention Settings**:
- Test result artifacts: `retention-days: 3` (in `env-fe.yml`)
- Allure reports: `retention-days: 7` (in `env-fe.yml`)

**Recommendations**:
1. ✅ **Current behavior is correct** - artifacts are properly separated by run
2. Consider adding run ID or timestamp to artifact names if you need to distinguish them:
   ```yaml
   name: mobile-results-dev-${{ github.run_id }}
   ```
3. For combined reports, the `prepare-combined-allure-results.sh` script already handles merging artifacts from all runs

---

## Code Changes Summary

### Files Modified:
1. `src/test/java/com/cjs/qa/junit/tests/mobile/MobileBrowserTests.java`
   - Removed duplicate `captureScreenshot` method
   - Added `AllureHelper` import
   - Updated `tearDown` to use `AllureHelper.captureScreenshot()`
   - Enhanced failure handling with page source and browser info

2. `src/test/java/com/cjs/qa/junit/tests/mobile/ResponsiveDesignTests.java`
   - Removed duplicate `captureScreenshot` method
   - Added `AllureHelper` import
   - Updated `tearDown` to use `AllureHelper.captureScreenshot()`
   - Enhanced failure handling with page source and browser info

### Files Unchanged (Reference):
- `src/test/java/com/cjs/qa/utilities/AllureHelper.java` - Centralized utility (already correct)
- Other test classes already use `AllureHelper.captureScreenshot()` correctly

---

## Verification Checklist

- [x] Duplicate methods removed
- [x] AllureHelper import added
- [x] tearDown methods updated
- [x] Unused imports removed
- [x] Linter checks passed
- [x] Screenshot capture verified (only on failure)
- [x] Artifact behavior documented

---

## Next Steps

1. ✅ **Code fixes complete** - Ready for review
2. ⏳ **Test the changes** - Run tests to verify screenshot capture works correctly
3. ⏳ **Monitor artifact behavior** - Verify artifacts are created as expected
4. ⏳ **Consider artifact naming** - If needed, add run ID to artifact names for better tracking

---

## Related Documentation

- [Allure Reporting Guide](../guides/testing/ALLURE_REPORTING.md)
- [Pipeline Workflow Guide](../guides/infrastructure/PIPELINE_WORKFLOW.md)
- [AllureHelper.java](../../src/test/java/com/cjs/qa/utilities/AllureHelper.java)

