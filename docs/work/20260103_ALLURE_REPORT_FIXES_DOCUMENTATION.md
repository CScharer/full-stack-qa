# Allure Report Fixes Documentation

**Date**: 2026-01-03  
**Status**: ✅ Completed and Verified  
**Pipeline Run**: #20672792728 (Success)

## Overview

This document documents the fixes applied to ensure Allure reports correctly display test results for all frameworks across all environments with accurate test execution times.

## Issues Fixed

### 1. Environment-Specific Data Detection
**Problem**: Test results were not being found for all environments, or were using flat structure data that couldn't distinguish between environments.

**Solution**: Modified `scripts/ci/prepare-combined-allure-results.sh` to:
- **Prioritize environment-specific directories**: Check `results-{env}/` directories first for each framework
- **Multiple structure patterns**: Check various possible artifact structures:
  - `results-{env}/{framework}-results-{env}/`
  - `results-{env}/{framework}-results/`
  - `results-{env}/` (recursive search)
- **Flat structure fallback**: Only process flat structure once (for first environment) when environment-specific directories don't exist
- **Debug logging**: Added warnings when flat structure is used to indicate potential data duplication

**Frameworks Fixed**:
- ✅ Cypress: Now finds `results-{env}/cypress-results-{env}/` or `results-{env}/cypress-results/`
- ✅ Playwright: Now finds `results-{env}/playwright-results-{env}/` or `results-{env}/playwright-results/`
- ✅ Vibium: Now finds `results-{env}/vibium-results-{env}/` or `results-{env}/vibium-results/`
- ✅ Robot Framework: Now finds `results-{env}/robot-results-{env}/` or `results-{env}/robot-results/`
- ✅ FS (Artillery): Now finds `results-{env}/fs-results-{env}/` or falls back to flat structure

### 2. Test Execution Time Accuracy
**Problem**: Test execution times were using conversion time instead of actual test execution time from test result files.

**Solution**: 
- **Vibium**: Updated `scripts/ci/convert-vibium-to-allure.sh` to extract `startTime` from Vitest JSON results, with fallback to file modification time
- **All frameworks**: Ensure converters extract timestamps from actual test result files (JSON, XML) rather than using `datetime.now()`

**Verification**: Test execution times now show actual test run times (e.g., `05:23:49.272` for dev, `05:25:55.321` for test) instead of conversion time.

### 3. Syntax Errors Fixed
**Problem**: Two syntax errors prevented pipeline from completing:
1. Python indentation error in `convert-vibium-to-allure.sh` (missing `test_index = 0` initialization)
2. Bash syntax error in `prepare-combined-allure-results.sh` (missing `fi` to close `if` block)

**Solution**: Fixed both syntax errors and verified with syntax checks.

## Key Files Modified

1. **`scripts/ci/prepare-combined-allure-results.sh`**
   - Added environment-specific directory detection for all frameworks
   - Improved structure pattern matching
   - Added debug logging and warnings
   - Fixed missing `fi` statement

2. **`scripts/ci/convert-vibium-to-allure.sh`**
   - Fixed Python indentation error
   - Added timestamp extraction from Vitest JSON `startTime` field
   - Added fallback to file modification time

## Verification Results

**Pipeline Run #20672792728** (Success):
- ✅ All frameworks using environment-specific data
- ✅ Test execution times extracted from actual test runs
- ✅ All environments showing distinct results:
  - Cypress: 6 tests across 3 environments
  - Playwright: 33 tests across 3 environments
  - Vibium: 18 tests across 3 environments
  - Robot: 15 tests across 3 environments
  - FS: 1 test (dev only - expected due to flat structure)

## Important Notes

### Environment-Specific Directory Priority
The script now follows this priority order:
1. **First**: Check `results-{env}/` directories (environment-specific)
2. **Second**: Check merged directories with environment subdirectories (e.g., `fs-results/fs-results-{env}/`)
3. **Last**: Fall back to flat structure (only for first environment to avoid duplicates)

### Flat Structure Warning
When flat structure is used (due to `merge-multiple: true`), the script:
- Processes files only once (for first environment)
- Adds warnings indicating same data will be used for all environments
- Cannot distinguish between environments in flat structure

### Test Execution Time Sources
- **Cypress**: Extracted from `cypress-results.json` timestamps
- **Playwright**: Extracted from `junit.xml` or test result JSON
- **Vibium**: Extracted from Vitest JSON `startTime` field
- **Robot**: Extracted from `output.xml` timestamps
- **FS**: Extracted from Artillery JSON timestamps

## Future Considerations

1. **FS Tests Environment-Specific Data**: Currently FS tests only show for dev when flat structure is used. Consider uploading FS test artifacts with environment-specific paths to enable multi-environment reporting.

2. **Test Count Accuracy**: The Pipeline Summary script may need updates to correctly count tests per environment from Allure results (see separate issue).

## Related Issues

- Initial issue: Framework tests missing from Allure reports
- Follow-up: Test execution times using conversion time instead of actual test time
- Follow-up: Environment-specific data not being detected correctly

## Testing

All fixes have been verified in pipeline run #20672792728:
- ✅ Syntax checks pass
- ✅ All frameworks convert successfully
- ✅ Environment-specific data detected
- ✅ Test execution times accurate
- ✅ All environments show results

