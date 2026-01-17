# Test Reporting Issues

**Date**: January 17, 2026  
**Status**: Mixed - One issue fixed, one issue open  
**Purpose**: Document test reporting issues and their resolutions

---

## Overview

This document consolidates test reporting issues discovered and addressed in the Allure reporting system. It covers both resolved and open issues related to test execution time accuracy and Allure Suites tab display.

---

## Issue 1: Test Execution Time Fix ✅ **FIXED**

**Date**: January 11, 2026  
**Status**: ✅ **FIXED**  
**Severity**: Medium - Incorrect timestamps displayed in Allure Report  
**PRs**: #156 (initial fix), #157 (corrected fix)

### Problem

Test Execution Time was showing the same timestamp (e.g., `2026-01-11T16:31:44`) for all environments (dev, test, prod) in Cypress Tests, even though they ran at different times. This issue likely affected other frameworks as well.

### Root Cause

Conversion scripts were using timestamps from source files:
- **Cypress**: Used `stats.start` or `stats.startedAt` from JSON file
- **Playwright**: Used `timestamp` attribute from JUnit XML
- **Robot Framework**: Used `generated` or `starttime` from XML

If source files had the same timestamp (e.g., generated/copied together) or if no timestamp was found (fallback to current time), all environments would show the same time.

### Solution

Changed timestamp extraction priority to:
1. **Source file timestamp** (stats/XML timestamp) - Primary/preferred - Reflects actual test execution time from test framework
2. **File modification time** - Fallback - Reflects when artifact was downloaded/processed (not when tests ran)
3. **Current time** (final fallback) - Should rarely happen

**Note**: The initial fix (PR #156) incorrectly prioritized file modification time, which reflects artifact download/processing time (all environments processed at once), not actual test execution time. This update (PR #157) corrects the priority to use source file timestamps first, which accurately reflect when tests actually executed in each environment.

This ensures each environment's results show accurate timestamps based on when tests actually ran, not when artifacts were processed.

### Changes Made

#### Cypress (`convert-cypress-to-allure.sh`)
- **Before (PR #156)**: Used file modification time as primary, then stats timestamp, then current time
- **After (PR #157)**: Uses stats timestamp (`stats.start`/`stats.startedAt`) as primary source, then file modification time, then current time
- **Rationale**: Stats timestamp comes directly from Cypress and reflects actual test execution time. File modification time reflects artifact download/processing, which happens all at once for all environments.

#### Playwright (`convert-playwright-to-allure.sh`)
- **Before (PR #156)**: Used file modification time as primary, then JUnit XML timestamp, then current time
- **After (PR #157)**: Uses JUnit XML `timestamp` attribute as primary source, then file modification time, then current time
- **Rationale**: JUnit XML timestamp comes directly from Playwright and reflects actual test execution time. File modification time reflects artifact download/processing, which happens all at once for all environments.

#### Robot Framework (`convert-robot-to-allure.sh`)
- **Before (PR #156)**: Used file modification time as fallback (after XML timestamp), then current time
- **After (PR #157)**: Uses XML `generated`/`starttime` attributes as primary source, then file modification time, then current time
- **Rationale**: XML timestamps come directly from Robot Framework and reflect actual test execution time. File modification time reflects artifact download/processing, which happens all at once for all environments.

#### Vibium (`convert-vibium-to-allure.sh`)
- **No changes needed** - Already uses file modification time as fallback

### Issue with PR #156

PR #156 incorrectly prioritized file modification time, which caused timestamps to be too close together (seconds apart) because artifacts are downloaded/processed all at once. The actual test execution times should be minutes apart (e.g., dev runs, then 1.5+ minutes later test runs). This update (PR #157) corrects the priority to use source file timestamps first, which accurately reflect when tests actually executed.

### Files Modified

- `scripts/ci/convert-cypress-to-allure.sh`
- `scripts/ci/convert-playwright-to-allure.sh`
- `scripts/ci/convert-robot-to-allure.sh`

### Testing

This fix was tested in the pipeline. Each environment now shows different Test Execution Times based on when tests actually ran.

### Related

- **PR #156**: Initial fix (incorrectly prioritized file modification time)
- **PR #157**: Corrected fix (prioritizes source file timestamps)
- This fix was part of Allure3 testing (PR #155)
- May also fix similar issues in other frameworks if they exist

---

## Issue 2: Allure2 Suites Tab Issue 🔄 **OPEN**

**Date**: January 11, 2026  
**Status**: 🔄 **OPEN** - Issue persists despite multiple fixes  
**Severity**: High - Only Playwright Tests appear in Suites tab, other frameworks missing

### Problem

Despite all 9 top-level containers being created correctly, Allure2's Suites tab only displays "Playwright Tests". All other frameworks (Cypress, Robot Framework, Vibium, Selenide, Smoke, Surefire, Performance, Artillery) are missing from the Suites tab, even though they appear correctly in the Overview section.

### Observed Behavior

- **Branch runs (dev environment only)**: All frameworks sometimes appear in Suites tab
- **Main runs (all 3 environments)**: Only Playwright Tests appear in Suites tab
- **Container creation**: All 9 containers are created successfully (verified in logs)
- **Container structure**: Proper hierarchy (top-level → env-specific → results) with correct `parentSuite` labels

### Attempted Fixes

#### 1. Container Cleanup (PR #151)
- **Fix**: Added cleanup step to remove old container files before creating new ones
- **Result**: ❌ Issue persisted

#### 2. Deterministic Filenames (PR #152, #153)
- **Fix**: Changed container filenames from random UUIDs to `{suite-name}-{uuid}-container.json` for alphabetical ordering
- **Result**: ❌ Issue persisted

#### 3. Removed Suite Label from Top-Level Containers (PR #154)
- **Fix**: Removed `suite` label from top-level containers to prevent Allure2 from grouping them
- **Hypothesis**: Allure2 may group containers by suite label and only show the first one
- **Result**: ❌ Issue persisted

### Current Container Structure

All containers are created with:
- ✅ Proper hierarchy: Top-level → Env-specific → Results
- ✅ `parentSuite` labels on env-specific containers pointing to top-level containers
- ✅ Deterministic filenames for consistent ordering
- ✅ No `suite` labels on top-level containers (to prevent grouping)
- ✅ All 9 containers created successfully (verified in pipeline logs)

### Pipeline Logs

All pipeline runs show:
```
✅ Created top-level container: Artillery Load Tests (2 env containers, 2 environment(s))
✅ Created top-level container: Cypress Tests (3 env containers, 3 environment(s))
✅ Created top-level container: Performance Tests (2 env containers, 2 environment(s))
✅ Created top-level container: Playwright Tests (3 env containers, 3 environment(s))
✅ Created top-level container: Robot Framework Tests (3 env containers, 3 environment(s))
✅ Created top-level container: Selenide Tests (3 env containers, 3 environment(s))
✅ Created top-level container: Smoke Tests (3 env containers, 3 environment(s))
✅ Created top-level container: Surefire test (3 env containers, 3 environment(s))
✅ Created top-level container: Vibium Tests (3 env containers, 3 environment(s))
```

But only "Playwright Tests" appears in the Suites tab.

### Root Cause Hypothesis

1. **Allure2 Limitation/Bug**: Allure2 may have a bug or limitation where it only processes/displays the first top-level container it encounters, regardless of how many are created.

2. **Result File Processing**: Allure2 may process result files first, group them by suite label, and only create/display containers for the first suite it encounters, ignoring manually created containers.

3. **Container Processing Order**: Allure2 may process containers in a specific order (e.g., alphabetical by filename) and only display the first one when multiple environments are present.

4. **Missing Bidirectional Links**: Result files may need `parentSuite` labels pointing to env-specific containers to create bidirectional links (containers → results via `children`, results → containers via `parentSuite`).

### Next Steps

1. **Test with Allure3**: Since Allure3 is available, test if the issue persists with Allure3 (may have different container processing logic).
   - **How to Test**: Change `config/environments.json` from `"reportVersion": 2` to `"reportVersion": 3`
   - **What This Entails**: 
     - Allure3 CLI is TypeScript-based (installed via npm) vs Allure2's Java-based binary
     - Uses different history format (`history.jsonl` vs individual JSON files)
     - May have different container processing logic
     - Same result files (backward compatible)
   - **Revert**: Change back to `"reportVersion": 2` if issues persist

2. **Add parentSuite Labels to Result Files**: Try adding `parentSuite` labels to result files pointing to their env-specific containers to create bidirectional links.

3. **Investigate Allure2 Source Code**: Check if there's a known limitation or bug in Allure2's container processing logic.

4. **Alternative Container Structure**: Try a different container structure (e.g., single top-level container with all frameworks as children).

5. **Contact Allure2 Maintainers**: If this is a confirmed bug, report it to the Allure2 project.

### Testing Allure3

**Date**: 2026-01-11  
**Status**: Testing Allure3 to see if it resolves the Suites tab issue

#### What It Entails

**Simple Answer**: Yes, testing Allure3 is essentially just changing the config from 2 to 3.

**Detailed Process**:

1. **Change Configuration**:
   - Edit `config/environments.json`
   - Change `"reportVersion": 2` to `"reportVersion": 3`
   - All scripts automatically read from this config file

2. **What Happens**:
   - CI/CD pipeline will install Allure3 CLI (TypeScript-based, via npm) instead of Allure2 CLI (Java-based binary)
   - Allure3 uses different history format (`history.jsonl` vs individual JSON files)
   - Container processing logic may be different (could fix the Suites tab issue)
   - Same result files are used (backward compatible)

3. **What to Expect**:
   - **Potential Fix**: Allure3 may process containers differently and display all frameworks in Suites tab
   - **Potential Issues**: 
     - History format differences (may need conversion)
     - Different UI/behavior
     - Previous history issues with Allure3 (documented in `ALLURE_REPORTING.md`)

4. **How to Revert**:
   - Change `"reportVersion"` back to `2` in `config/environments.json`
   - Pipeline will automatically switch back to Allure2

#### Key Differences Between Allure2 and Allure3

| Aspect | Allure2 | Allure3 |
|--------|---------|---------|
| **Type** | Java-based binary | TypeScript-based (npm) |
| **Installation** | Download from GitHub releases | `npm install -g allure` |
| **History Format** | Individual `{md5-hash}.json` files | `history.jsonl` (JSON Lines) |
| **Container Processing** | May have limitation (only shows first container) | Unknown (may process differently) |
| **Maturity** | Mature, stable | Newer, still evolving |
| **Result Files** | Same format (backward compatible) | Same format (backward compatible) |

#### Previous Allure3 Experience

According to `ALLURE_REPORTING.md`:
- Allure3 was previously tested but had issues with history/trending functionality
- History format differences caused problems
- Allure2 was chosen as default due to maturity and proven history functionality

#### Recommendation

**For Testing**: Change config to Allure3 and run a pipeline to see if Suites tab issue is resolved. If it works, great! If not, revert to Allure2.

**For Production**: Allure2 is recommended due to maturity, but if Allure3 fixes the Suites tab issue, it may be worth the trade-off.

#### Testing Plan

1. Run pipeline with Allure3 configuration
2. Verify if all frameworks appear in Suites tab
3. Check if history/trending functionality works correctly
4. Document results

#### Revert Plan

If Allure3 doesn't resolve the issue or introduces new problems:
- Change `"reportVersion"` back to `2` in `config/environments.json`
- Pipeline will automatically switch back to Allure2

### Related Issues

- **Trend Chart Issue**: Main "Trend" graph not displaying (Duration, Retries, Categories trends work)
  - This may be a separate issue related to `history-trend.json` format or data structure

### Files Involved

- `scripts/ci/create-framework-containers.sh` - Creates container files
- `scripts/ci/prepare-combined-allure-results.sh` - Calls container creation (Step 4.5)
- `scripts/ci/generate-combined-allure-report.sh` - Generates Allure report
- `config/environments.json` - Controls Allure version (`reportVersion`)

### Lessons Learned

#### What We've Discovered

1. **Container Creation Works**: All containers are created correctly with proper structure
2. **Allure2 Processing Issue**: Despite correct containers, Allure2 only displays the first one
3. **Multiple Fixes Attempted**: Cleanup, deterministic filenames, removing suite labels - none resolved the issue
4. **Environment Difference**: Issue more pronounced with multiple environments (main branch) vs single environment (dev branch)

#### Key Insights

1. **Container Structure is Correct**: The hierarchy (top-level → env-specific → results) is properly implemented
2. **Allure2 May Have Limitation**: This appears to be an Allure2 limitation or bug, not a configuration issue
3. **Allure3 May Be Solution**: Since Allure3 uses different processing logic, it may handle multiple containers correctly

### Documentation Updates Needed

- ✅ Created consolidated test reporting issues document (`20260117_TEST_REPORTING_ISSUES.md`)
- ✅ Consolidated into this document (`20260117_TEST_REPORTING_ISSUES.md`)
- ⏳ Update `ALLURE_REPORTING.md` with findings (if Allure3 testing is successful)
- ⏳ Document Allure3 testing process and results

### Related Documentation

- **Main Allure Guide**: `docs/guides/testing/ALLURE_REPORTING.md`
- **Container Creation Script**: `scripts/ci/create-framework-containers.sh`
- **Report Generation Script**: `scripts/ci/generate-combined-allure-report.sh`
- **Configuration**: `config/environments.json` (controls Allure version)

---

## Summary

### Resolved Issues
- ✅ **Test Execution Time Fix**: Fixed timestamp extraction priority to use source file timestamps first, ensuring accurate test execution times per environment (PR #157)

### Open Issues
- 🔄 **Allure2 Suites Tab Issue**: Only Playwright Tests appear in Suites tab despite all 9 containers being created correctly. Testing Allure3 as potential solution.

### Next Actions
1. Continue testing Allure3 to see if it resolves the Suites tab issue
2. Document Allure3 testing results
3. Update `ALLURE_REPORTING.md` with findings
4. Consider alternative container structures if Allure3 doesn't resolve the issue

---

**Last Updated**: January 17, 2026
