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
1. **File modification time** (primary/preferred) - Most accurate, reflects when tests actually ran
2. **Source file timestamp** (stats/XML timestamp) - Fallback if file mtime unavailable
3. **Current time** (final fallback) - Should rarely happen

This ensures each environment's results have different timestamps based on when the files were actually modified (when tests ran).

## Changes Made

### Cypress (`convert-cypress-to-allure.sh`)
- **Before**: Used `stats.start`/`stats.startedAt` from JSON, fallback to current time
- **After**: Uses file modification time as primary source, then stats timestamp, then current time
- **Rationale**: File modification time reflects when each environment's tests actually ran

### Playwright (`convert-playwright-to-allure.sh`)
- **Before**: Used JUnit XML `timestamp` attribute, fallback to current time
- **After**: Uses file modification time as primary source, then JUnit XML timestamp, then current time
- **Rationale**: File modification time reflects when each environment's tests actually ran

### Robot Framework (`convert-robot-to-allure.sh`)
- **Before**: Used XML `generated`/`starttime` attributes, fallback to current time
- **After**: Uses file modification time as fallback (after XML timestamp), then current time
- **Rationale**: File modification time reflects when each environment's tests actually ran

### Vibium (`convert-vibium-to-allure.sh`)
- **No changes needed** - Already uses file modification time as fallback

## Testing

This fix will be tested in the pipeline. Each environment should now show different Test Execution Times based on when tests actually ran.

## Files Modified

- `scripts/ci/convert-cypress-to-allure.sh`
- `scripts/ci/convert-playwright-to-allure.sh`
- `scripts/ci/convert-robot-to-allure.sh`

## Related

- This fix is part of PR #155 (Allure3 testing)
- May also fix similar issues in other frameworks if they exist
