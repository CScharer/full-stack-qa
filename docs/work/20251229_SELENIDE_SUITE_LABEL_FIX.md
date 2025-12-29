# Selenide Suite Label Override Fix

**Date**: 2025-12-29  
**Status**: 🔧 **Fixed** - Awaiting pipeline verification

---

## Issue Summary

After the container hierarchy fix, Selenide tests were still not appearing in the Suites tab. Investigation revealed that Selenide result files still had `suite='Surefire test'` instead of `suite='Selenide Tests'` when the container script ran, causing them to be grouped under "Surefire test" instead of "Selenide Tests".

---

## Root Cause Analysis

### Problem: Selenide Files Not Grouped Correctly ❌ **FIXED**

**Issue**: The `add-environment-labels.sh` script updates Selenide files to have `suite='Selenide Tests'`, but:
1. Some Selenide result files may not be updated (only containers are)
2. The container script runs after label updates, but may read files before they're fully updated
3. Selenide files detected by epic/testClass indicators still have `suite='Surefire test'`

**Evidence from logs**:
```
🔍 Found 24 potential Selenide result file(s)
   Selenide files have suite labels: {'Surefire test': 10}
```

This shows that Selenide files were detected, but they still had `suite='Surefire test'` instead of `suite='Selenide Tests'`.

**Result**: Selenide files were grouped under "Surefire test" instead of "Selenide Tests", so they didn't appear as a separate suite in the Suites tab.

---

## Solution Applied

### Override Suite Name for Selenide Files ✅

**File**: `scripts/ci/create-framework-containers.sh`

**Changes**:
- Added Selenide detection logic in the grouping loop (same as debug section)
- Check for Selenide indicators: `epic='HomePage Tests'`, `testClass` containing `'HomePageTests'`, or `fullName` containing `'Selenide'`
- If a file is detected as Selenide but has `suite='Surefire test'`, override it to `suite='Selenide Tests'`
- This ensures Selenide files are always grouped under "Selenide Tests" regardless of their current suite label

**Code Changes**:
```python
# Check for Selenide indicators (same logic as debug section)
is_selenide = False
if 'labels' in data:
    for label in data['labels']:
        if label.get('name') == 'epic' and label.get('value') == 'HomePage Tests':
            is_selenide = True
        elif label.get('name') == 'testClass' and 'HomePageTests' in str(label.get('value', '')):
            is_selenide = True
        elif label.get('name') == 'suite':
            suite_value = label.get('value', '')
            if 'Selenide' in suite_value or suite_value == 'Selenide Tests':
                is_selenide = True
            suite_name = suite_value
        elif label.get('name') == 'environment':
            env = label.get('value', 'unknown')

# Check fullName for Selenide
if 'fullName' in data and 'Selenide' in data.get('fullName', ''):
    is_selenide = True

# CRITICAL: If this is a Selenide test but suite label says "Surefire test", override it
# This handles cases where add-environment-labels.sh hasn't updated the suite label yet
# or the result file was missed
if is_selenide and suite_name == 'Surefire test':
    suite_name = 'Selenide Tests'
```

---

## Expected Results

After this fix:

1. **Selenide files detected correctly**:
   - Files with `epic='HomePage Tests'` → grouped under "Selenide Tests"
   - Files with `testClass` containing `'HomePageTests'` → grouped under "Selenide Tests"
   - Files with `fullName` containing `'Selenide'` → grouped under "Selenide Tests"

2. **Suite distribution should show**:
   ```
   🔍 Suite distribution (all files):
      - Cypress Tests: 6 file(s)
      - Playwright Tests: 99 file(s)
      - Robot Framework Tests: 15 file(s)
      - Selenide Tests: 24 file(s)  ✅ Should now appear
      - Surefire test: 1164 file(s)  ✅ Reduced (Selenide removed)
      - Vibium Tests: 18 file(s)
   ```

3. **Suite/Environment groups should show**:
   ```
   🔍 Suite/Environment groups found:
      - Selenide Tests: 24 test(s) across 3 environment(s) ['dev', 'test', 'prod']  ✅
   ```

4. **Containers created**:
   - "Selenide Tests [DEV]" container
   - "Selenide Tests [TEST]" container
   - "Selenide Tests [PROD]" container
   - Top-level "Selenide Tests" container

5. **Suites tab should show**:
   - Selenide Tests as a separate suite (not nested under Surefire test)
   - Environment-specific containers for Selenide Tests

---

## Files Modified

- `scripts/ci/create-framework-containers.sh`
  - Added Selenide detection in grouping loop
  - Override suite name for Selenide files with `suite='Surefire test'`

---

## Verification Steps

1. **Check pipeline logs** for:
   - Suite distribution showing "Selenide Tests: 24 file(s)" (or appropriate count)
   - Suite/Environment groups showing "Selenide Tests" with environment breakdown
   - Containers created for "Selenide Tests [DEV]", "[TEST]", "[PROD]"

2. **Check Allure report**:
   - **Suites tab** should show "Selenide Tests" as a separate suite
   - Selenide Tests should NOT be nested under "Surefire test"
   - Environment-specific containers should be visible for Selenide Tests

3. **Verify grouping**:
   - All Selenide tests should be under "Selenide Tests" suite
   - Surefire test count should be reduced (Selenide tests removed)

---

## Related Issues

- **Container Hierarchy Fix**: This fix builds on the previous container hierarchy fix
- **Selenide Visibility Fix**: This completes the Selenide suite grouping by ensuring files are detected and grouped correctly

---

## Next Steps

1. ✅ Code changes complete
2. ⏳ Awaiting pipeline run
3. ⏳ Verify Allure report Suites tab shows "Selenide Tests"
4. ⏳ Verify Selenide tests are not nested under "Surefire test"

