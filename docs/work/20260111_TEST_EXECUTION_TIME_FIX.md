# Test Execution Time Fix

**Date**: 2026-01-11  
**Status**: Fixed  
**Severity**: Medium - Incorrect timestamps displayed in Allure Report

## Problem

Test Execution Time was showing the same timestamp (e.g., `2026-01-11T16:31:44`) for all environments (dev, test, prod) in Cypress Tests, even though they ran at different times. This issue likely affected other frameworks as well.

## Root Cause

Conversion scripts were using timestamps from source files:
- **Cypress**: Used `stats.start` or `stats.startedAt` from JSON file
- **Playwright**: Used `timestamp` attribute from JUnit XML
- **Robot Framework**: Used `generated` or `starttime` from XML

If source files had the same timestamp (e.g., generated/copied together) or if no timestamp was found (fallback to current time), all environments would show the same time.

## Solution

Changed timestamp extraction priority to:
1. **Source file timestamp** (stats/XML timestamp) - Primary/preferred - Reflects actual test execution time from test framework
2. **File modification time** - Fallback - Reflects when artifact was downloaded/processed (not when tests ran)
3. **Current time** (final fallback) - Should rarely happen

**Note**: The initial fix (PR #156) incorrectly prioritized file modification time, which reflects artifact download/processing time (all environments processed at once), not actual test execution time. This update (PR #157) corrects the priority to use source file timestamps first, which accurately reflect when tests actually executed in each environment.

This ensures each environment's results show accurate timestamps based on when tests actually ran, not when artifacts were processed.

## Changes Made

### Cypress (`convert-cypress-to-allure.sh`)
- **Before (PR #156)**: Used file modification time as primary, then stats timestamp, then current time
- **After (PR #157)**: Uses stats timestamp (`stats.start`/`stats.startedAt`) as primary source, then file modification time, then current time
- **Rationale**: Stats timestamp comes directly from Cypress and reflects actual test execution time. File modification time reflects artifact download/processing, which happens all at once for all environments.

### Playwright (`convert-playwright-to-allure.sh`)
- **Before (PR #156)**: Used file modification time as primary, then JUnit XML timestamp, then current time
- **After (PR #157)**: Uses JUnit XML `timestamp` attribute as primary source, then file modification time, then current time
- **Rationale**: JUnit XML timestamp comes directly from Playwright and reflects actual test execution time. File modification time reflects artifact download/processing, which happens all at once for all environments.

### Robot Framework (`convert-robot-to-allure.sh`)
- **Before (PR #156)**: Used file modification time as fallback (after XML timestamp), then current time
- **After (PR #157)**: Uses XML `generated`/`starttime` attributes as primary source, then file modification time, then current time
- **Rationale**: XML timestamps come directly from Robot Framework and reflect actual test execution time. File modification time reflects artifact download/processing, which happens all at once for all environments.

### Vibium (`convert-vibium-to-allure.sh`)
- **No changes needed** - Already uses file modification time as fallback

## Testing

This fix will be tested in the pipeline. Each environment should now show different Test Execution Times based on when tests actually ran.

## Files Modified

- `scripts/ci/convert-cypress-to-allure.sh`
- `scripts/ci/convert-playwright-to-allure.sh`
- `scripts/ci/convert-robot-to-allure.sh`

## Related

- **PR #156**: Initial fix (incorrectly prioritized file modification time)
- **PR #157**: Corrected fix (prioritizes source file timestamps)
- This fix is part of Allure3 testing (PR #155)
- May also fix similar issues in other frameworks if they exist

## Issue with PR #156

PR #156 incorrectly prioritized file modification time, which caused timestamps to be too close together (seconds apart) because artifacts are downloaded/processed all at once. The actual test execution times should be minutes apart (e.g., dev runs, then 1.5+ minutes later test runs). This update (PR #157) corrects the priority to use source file timestamps first, which accurately reflect when tests actually executed.
