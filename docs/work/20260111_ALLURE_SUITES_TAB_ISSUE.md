# Allure2 Suites Tab Issue - Investigation Summary

**Date**: 2026-01-11  
**Status**: Open - Issue persists despite multiple fixes  
**Severity**: High - Only Playwright Tests appear in Suites tab, other frameworks missing

## Problem

Despite all 9 top-level containers being created correctly, Allure2's Suites tab only displays "Playwright Tests". All other frameworks (Cypress, Robot Framework, Vibium, Selenide, Smoke, Surefire, Performance, Artillery) are missing from the Suites tab, even though they appear correctly in the Overview section.

## Observed Behavior

- **Branch runs (dev environment only)**: All frameworks sometimes appear in Suites tab
- **Main runs (all 3 environments)**: Only Playwright Tests appear in Suites tab
- **Container creation**: All 9 containers are created successfully (verified in logs)
- **Container structure**: Proper hierarchy (top-level → env-specific → results) with correct `parentSuite` labels

## Attempted Fixes

### 1. Container Cleanup (PR #151)
- **Fix**: Added cleanup step to remove old container files before creating new ones
- **Result**: ❌ Issue persisted

### 2. Deterministic Filenames (PR #152, #153)
- **Fix**: Changed container filenames from random UUIDs to `{suite-name}-{uuid}-container.json` for alphabetical ordering
- **Result**: ❌ Issue persisted

### 3. Removed Suite Label from Top-Level Containers (PR #154)
- **Fix**: Removed `suite` label from top-level containers to prevent Allure2 from grouping them
- **Hypothesis**: Allure2 may group containers by suite label and only show the first one
- **Result**: ❌ Issue persisted

## Current Container Structure

All containers are created with:
- ✅ Proper hierarchy: Top-level → Env-specific → Results
- ✅ `parentSuite` labels on env-specific containers pointing to top-level containers
- ✅ Deterministic filenames for consistent ordering
- ✅ No `suite` labels on top-level containers (to prevent grouping)
- ✅ All 9 containers created successfully (verified in pipeline logs)

## Root Cause Hypothesis

1. **Allure2 Limitation/Bug**: Allure2 may have a bug or limitation where it only processes/displays the first top-level container it encounters, regardless of how many are created.

2. **Result File Processing**: Allure2 may process result files first, group them by suite label, and only create/display containers for the first suite it encounters, ignoring manually created containers.

3. **Container Processing Order**: Allure2 may process containers in a specific order (e.g., alphabetical by filename) and only display the first one when multiple environments are present.

4. **Missing Bidirectional Links**: Result files may need `parentSuite` labels pointing to env-specific containers to create bidirectional links (containers → results via `children`, results → containers via `parentSuite`).

## Next Steps

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

## Testing Allure3

### What It Entails

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

### Key Differences Between Allure2 and Allure3

| Aspect | Allure2 | Allure3 |
|--------|---------|---------|
| **Type** | Java-based binary | TypeScript-based (npm) |
| **Installation** | Download from GitHub releases | `npm install -g allure` |
| **History Format** | Individual `{md5-hash}.json` files | `history.jsonl` (JSON Lines) |
| **Container Processing** | May have limitation (only shows first container) | Unknown (may process differently) |
| **Maturity** | Mature, stable | Newer, still evolving |
| **Result Files** | Same format (backward compatible) | Same format (backward compatible) |

### Previous Allure3 Experience

According to `ALLURE_REPORTING.md`:
- Allure3 was previously tested but had issues with history/trending functionality
- History format differences caused problems
- Allure2 was chosen as default due to maturity and proven history functionality

### Recommendation

**For Testing**: Change config to Allure3 and run a pipeline to see if Suites tab issue is resolved. If it works, great! If not, revert to Allure2.

**For Production**: Allure2 is recommended due to maturity, but if Allure3 fixes the Suites tab issue, it may be worth the trade-off.

## Testing Allure3 (In Progress)

**Date**: 2026-01-11  
**Status**: Testing Allure3 to see if it resolves the Suites tab issue

### Change Made

Changed `config/environments.json` from `"reportVersion": 2` to `"reportVersion": 3` to test if Allure3's different container processing logic resolves the issue.

### Expected Behavior

- Allure3 CLI (TypeScript-based) will be installed instead of Allure2 CLI (Java-based)
- Allure3 uses different history format (`history.jsonl` vs individual JSON files)
- Allure3 may process containers differently and display all frameworks in Suites tab
- Same result files are used (backward compatible)

### Testing Plan

1. Run pipeline with Allure3 configuration
2. Verify if all frameworks appear in Suites tab
3. Check if history/trending functionality works correctly
4. Document results

### Revert Plan

If Allure3 doesn't resolve the issue or introduces new problems:
- Change `"reportVersion"` back to `2` in `config/environments.json`
- Pipeline will automatically switch back to Allure2

## Related Issues

- **Trend Chart Issue**: Main "Trend" graph not displaying (Duration, Retries, Categories trends work)
  - This may be a separate issue related to `history-trend.json` format or data structure

## Files Involved

- `scripts/ci/create-framework-containers.sh` - Creates container files
- `scripts/ci/prepare-combined-allure-results.sh` - Calls container creation (Step 4.5)
- `scripts/ci/generate-combined-allure-report.sh` - Generates Allure report

## Pipeline Logs

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

## Lessons Learned

### What We've Discovered

1. **Container Creation Works**: All containers are created correctly with proper structure
2. **Allure2 Processing Issue**: Despite correct containers, Allure2 only displays the first one
3. **Multiple Fixes Attempted**: Cleanup, deterministic filenames, removing suite labels - none resolved the issue
4. **Environment Difference**: Issue more pronounced with multiple environments (main branch) vs single environment (dev branch)

### Key Insights

1. **Container Structure is Correct**: The hierarchy (top-level → env-specific → results) is properly implemented
2. **Allure2 May Have Limitation**: This appears to be an Allure2 limitation or bug, not a configuration issue
3. **Allure3 May Be Solution**: Since Allure3 uses different processing logic, it may handle multiple containers correctly

### Documentation Updates Needed

- ✅ Created investigation document (`20260111_ALLURE_SUITES_TAB_ISSUE.md`)
- ⏳ Update `ALLURE_REPORTING.md` with findings (if Allure3 testing is successful)
- ⏳ Document Allure3 testing process and results

## Related Documentation

- **Main Allure Guide**: `docs/guides/testing/ALLURE_REPORTING.md`
- **Container Creation Script**: `scripts/ci/create-framework-containers.sh`
- **Report Generation Script**: `scripts/ci/generate-combined-allure-report.sh`
- **Configuration**: `config/environments.json` (controls Allure version)
