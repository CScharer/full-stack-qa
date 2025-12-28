# Selenide Tests Display Fix - Analysis and Local Testing

**Date**: 2024-12-28  
**Status**: 🔍 **Testing Complete** - Local test shows fix works, but CI verification needed

---

## Issue Summary

Selenide tests are appearing nested under "Surefire test" instead of as a top-level "Selenide Tests" suite in the Allure report.

---

## Root Cause Analysis

### Problem 1: Container Field Mismatch ✅ **FIXED**
- **Issue**: Script was checking `childrenUuid` field, but Allure containers use `children` (array of UUIDs)
- **Fix**: Updated script to check both `children` and `childrenUuid` fields
- **Commit**: `4320247`

### Problem 2: Nested Container Detection ✅ **FIXED**  
- **Issue**: Nested "Selenide Tests" containers had `parentSuite="Surefire test"` which created the hierarchy
- **Fix**: First pass now removes `parentSuite` from Selenide containers
- **Status**: Working in local tests

---

## Local Testing Results

### Test Script Created
- **File**: `scripts/test/test-selenide-fix.sh`
- **Purpose**: Test the fix locally without running full CI pipeline

### Test Results ✅
```
✅ Parent container name updated correctly! (Surefire test → Selenide Tests)
✅ Parent container suite updated correctly! (Surefire test → Selenide Tests)
✅ Nested containers: ParentSuite removed!
```

### What the Test Shows:
1. **First Pass**: Correctly detects and updates Selenide containers
   - Removes `parentSuite` labels
   - Updates `suite` labels to "Selenide Tests"
   - Updates container names

2. **Second Pass**: Finds parent containers with Selenide children
   - Detects containers with `name="Surefire test"` that have Selenide children
   - Renames them to "Selenide Tests"
   - Removes Selenide container UUIDs from parent's `children` array

3. **Third Pass**: Updates nested containers (redundant - already done in first pass)
   - Should remove `parentSuite` from nested containers
   - Currently not needed because first pass handles it

---

## Current Implementation

### Three-Pass Approach:

**First Pass** (Lines 221-412):
- Processes all result and container files
- Detects Selenide tests by:
  - `epic="HomePage Tests"` (primary)
  - `feature="HomePage Navigation"` (fallback)
  - `testClass` containing `"HomePageTests"`
- For Selenide tests:
  - Removes `parentSuite` labels
  - Updates `suite` labels to "Selenide Tests"
  - Updates container names to "Selenide Tests"

**Second Pass** (Lines 414-540):
- Finds containers with `name="Surefire test"` or `suite="Surefire test"`
- Finds containers that have "Selenide Tests" as children (checks both `children` and `childrenUuid`)
- Updates parent containers:
  - Renames to "Selenide Tests"
  - Removes Selenide container UUIDs from `children`/`childrenUuid` arrays
  - Updates suite labels

**Third Pass** (Lines 550-610):
- Finds containers with `name="Selenide Tests"`
- Removes `parentSuite` labels
- Ensures suite label is "Selenide Tests"

---

## Why It Might Not Work in CI

### Possible Issues:

1. **Container File Naming**: 
   - CI containers might have different UUIDs or naming patterns
   - Need to verify actual container file names in CI artifacts

2. **Detection Logic**:
   - First pass detection might miss some containers
   - Containers might not have `epic`/`feature` labels initially

3. **Timing/Order**:
   - Containers might be processed in a different order
   - Parent containers might be processed before children

4. **File Structure**:
   - CI artifacts might have a different directory structure
   - Files might be in nested directories

---

## Next Steps

### 1. Verify CI Logs ✅ **IN PROGRESS**
- Check actual CI run logs to see what's happening
- Look for "Second pass" and "Third pass" output
- Check if containers are being found and updated

### 2. Download Actual CI Artifacts
- Download real Selenide results from CI
- Run the script locally on actual CI data
- Compare structure with test data

### 3. Fix Any Remaining Issues
- If detection fails, improve detection logic
- If containers aren't found, check file patterns
- If updates aren't applied, check file writing logic

### 4. Add Better Debugging
- Add more verbose output to script
- Log which containers are found/updated
- Show before/after states

---

## Test Script Usage

```bash
# Run the test script
./scripts/test/test-selenide-fix.sh

# This will:
# 1. Download sample data (or create test data)
# 2. Run the add-environment-labels.sh script
# 3. Show before/after analysis
# 4. Save test files to /tmp/selenide-fix-test
```

---

## Files Modified

1. `scripts/ci/add-environment-labels.sh`
   - Added check for both `children` and `childrenUuid` fields
   - Enhanced container detection logic
   - Three-pass approach for comprehensive updates

2. `scripts/test/test-selenide-fix.sh` (NEW)
   - Local testing script
   - Creates sample data
   - Runs and verifies the fix

---

## Confidence Level

**Local Testing**: ✅ **90%** - Test shows fix works correctly  
**CI Verification**: ⏳ **Pending** - Need to verify with actual CI data

---

## Recommendations

1. ✅ **Run local test** before committing changes
2. ⏳ **Check CI logs** to see actual behavior
3. ⏳ **Download CI artifacts** and test locally
4. ⏳ **Add debug output** to script for better visibility
5. ⏳ **Verify in next CI run** after fixes

