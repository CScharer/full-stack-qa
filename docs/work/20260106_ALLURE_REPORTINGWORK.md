# Allure Reporting Work - Complete History

**Date Created**: 2026-01-06  
**Status**: 📋 Complete Documentation  
**Issue**: Allure3 history not appearing in reports despite multiple fix attempts  
**Timeline**: 2026-01-04 to 2026-01-06  
**Current MERGE_NUMBER**: 35  
**Latest Pipeline**: #20758689530 (2026-01-06)

---

## 📋 Executive Summary

This document tracks all work related to implementing and fixing Allure3 history/trending functionality. The implementation required **11+ Pull Requests** and **35+ merges to main** to achieve a working solution.

### Key Metrics
- **Total PRs**: 11+ (PRs #67-#105)
- **Total Pipeline Runs**: 35+ (Pipelines #388-#20758689530)
- **Total Iterations**: 35 merges to main
- **Time Span**: ~3 days (2026-01-04 to 2026-01-06)
- **Current MERGE_NUMBER**: 35 (as of 2026-01-06)

### Current Status (2026-01-06)
- **MERGE_NUMBER**: 35
- **Latest Pipeline**: #20758689530
- ✅ **History Download**: Working (via GitHub API and artifacts)
- ✅ **History Structure**: Fixed (flat array, deduplicated)
- ✅ **History Merge Logic**: Working (manual merge with deduplication)
- ✅ **History Preservation**: Working (history growing: 212K → 252K)
- ✅ **History Upload**: Working (3 files uploaded as artifact)
- ⚠️ **Allure3 Recognition**: Still not processing manually created history
- ⚠️ **Trends Display**: Not yet visible (Allure3 may require self-created history)

---

## 🔢 MERGE_NUMBER Tracking

**Current MERGE_NUMBER**: 35  
**Location**: `scripts/temp/test-trending-merge-tracker.sh`  
**Purpose**: Tracks merge iterations for test trending validation  
**Update Method**: Increment `MERGE_NUMBER` in the tracker script before each merge

**MERGE_NUMBER History**:
- Started at: 1 (PR #67)
- Current: 35 (PR #105, Pipeline #20758689530)
- Total iterations: 35 merges to main

**How to Update**:
1. Edit `scripts/temp/test-trending-merge-tracker.sh`
2. Increment `MERGE_NUMBER` value (e.g., from 35 to 36)
3. Commit and push changes
4. Create PR and merge
5. Pipeline will run with new MERGE_NUMBER

---

## 🎯 Original Goal

**Objective**: Enable test trending in Allure reports to track test results over time, showing:
- Test pass/fail rates over multiple runs
- Duration trends (performance regressions)
- Flaky test detection
- Historical test execution data

**Root Cause Identified**: Allure reports mention historical trends, but trends were not showing up because history was not being preserved across GitHub Pages deployments.

---

## 📊 Timeline of Implementation

### Phase 1: Initial Implementation (2026-01-04)

**PR #67**: Initial Implementation
- Created `download-allure-history.sh` script
- Added history download steps to CI workflow
- Added history verification steps
- Created merge tracker script (`MERGE_NUMBER=1`)

**Key Changes**:
- History download from GitHub Pages (primary)
- History download from artifacts (fallback)
- History verification before/after generation
- History upload as artifact

**Pipeline Result**: #388 - ✅ Success (first run, no history expected)

---

### Phase 2: Critical Fixes (2026-01-04)

**PR #71**: Typo Fix
- Fixed `$REO_NAME` → `$REPO_NAME` in GitHub API URL
- **Impact**: GitHub API calls now work correctly

**PR #75**: Enable `keep_files: true` ⭐ **MOST CRITICAL FIX**
- Changed `keep_files: false` to `keep_files: true` in GitHub Pages deployment
- **Impact**: History is now preserved across deployments (was being wiped)

**PR #77**: Improve History Download
- Complete rewrite of `download-allure-history.sh`
- Downloads entire `history/` directory via GitHub API
- Better error handling and file counting

---

### Phase 3: History Format Issues (2026-01-05)

**Issue Discovered**: Allure3 not creating history even with history files in place

**Attempts Made**:
1. ❌ Empty directory with `.gitkeep` - Allure3 didn't recognize it
2. ❌ Empty arrays `[]` - Allure3 didn't process them
3. ❌ History in RESULTS_DIR before generation - Allure3 still didn't create history
4. ❌ Valid structure with buildOrder - Still no history created

**Key Finding**: Allure3 requires existing history to merge with. It doesn't bootstrap history on first run.

---

### Phase 4: Manual History Merge (2026-01-05 to 2026-01-06)

**Solution Implemented**: Manual history merge before Allure3 generation

**PR #91-105**: Multiple iterations to fix history merge logic

**Key Fixes**:
1. **Nested Array Issue** (PR #102): Fixed nested arrays in history files
2. **History Structure** (PR #103): Ensured flat array format
3. **Deduplication** (PR #104, #105): Fixed duplicate build orders using `group_by(.buildOrder) | map(last)`

**Current Implementation**:
- Downloads history from GitHub Pages/artifacts
- Manually merges current run's data with existing history
- Deduplicates all build orders (keeps only latest per buildOrder)
- Ensures flat array structure (not nested)
- Copies merged history to report directory for deployment

---

## 🔍 Root Cause Analysis

### Issue #1: GitHub Pages `keep_files: false` ✅ FIXED

**Problem**: The `peaceiris/actions-gh-pages@v4` action was configured with `keep_files: false`, which wipes the entire `gh-pages` branch on each deployment.

**Impact**: History was being created and deployed, but immediately deleted on the next deployment.

**Solution**: Changed `keep_files: false` to `keep_files: true` in PR #75.

**Status**: ✅ **FIXED**

---

### Issue #2: Typo in Download Script ✅ FIXED

**Problem**: Typo in `download-allure-history.sh`: `$REO_NAME` instead of `$REPO_NAME`.

**Impact**: GitHub API download was failing silently.

**Solution**: Fixed typo in PR #71.

**Status**: ✅ **FIXED**

---

### Issue #3: Download Script Looking for Wrong Files ✅ FIXED

**Problem**: Download script was looking for specific files, but Allure3 creates MD5-hashed files.

**Impact**: Script couldn't find history files.

**Solution**: Rewrote to download entire `history/` directory via GitHub API in PR #77.

**Status**: ✅ **FIXED**

---

### Issue #4: Nested Array Structure ✅ FIXED

**Problem**: History files had nested arrays instead of flat arrays.

**Impact**: Allure3 couldn't process nested structure.

**Solution**: Fixed to use flat array structure in PR #102.

**Status**: ✅ **FIXED**

---

### Issue #5: Duplicate Build Orders ✅ FIXED

**Problem**: History had duplicate build orders (same buildOrder appearing multiple times).

**Impact**: Allure3 couldn't process history with duplicates.

**Solution**: Implemented deduplication using `group_by(.buildOrder) | map(last)` in PR #104, #105.

**Status**: ✅ **FIXED**

---

### Issue #6: Allure3 Not Recognizing Manually Created History ⚠️ CURRENT ISSUE

**Problem**: Even with correct structure, format, and data, Allure3 consistently says "didn't create history" and doesn't process manually created history files.

**Evidence**:
- History structure is correct (flat array) ✅
- History format matches Allure3 expectations ✅
- History has valid data ✅
- Deduplication working ✅
- But Allure3 still says "didn't create history" ❌

**Possible Causes**:
1. Allure3 might require history files to be created by Allure3 itself
2. Allure3 might validate history files in ways we don't understand
3. Allure3 might require specific metadata or checksums
4. Allure3 might only accept history it created itself

**Status**: ⚠️ **INVESTIGATION ONGOING**

**Estimated Success Probability**: 20-30% (based on 10+ failed attempts)

---

## 🔧 Technical Implementation

### Current Architecture

**History Flow**:
```
1. Download history from GitHub Pages/artifacts
2. Place in RESULTS_DIR/history/ before generation
3. Manually merge current run's data with existing history
4. Deduplicate all build orders (group_by + map(last))
5. Ensure flat array structure
6. Generate Allure report (Allure3 attempts to merge)
7. If Allure3 doesn't create history, copy manually merged history
8. Upload history as artifact
9. Deploy to GitHub Pages
```

### CI/CD Workflow Steps

**Location**: `.github/workflows/ci.yml` - `combined-allure-report` job

**Step 1: Download Previous Allure History (Artifact Fallback)**
- **Line**: ~1232-1270
- **Condition**: `if: always() && github.ref == 'refs/heads/main'`
- **Purpose**: Downloads history from previous successful pipeline run as artifact
- **Method**: Uses `gh run download` to get `allure-history` artifact
- **Behavior**: `continue-on-error: true` - won't fail if no artifact exists
- **Target**: `allure-results-combined/history/`

**Step 2: Download Previous Allure History (GitHub Pages)**
- **Line**: ~1274-1281
- **Condition**: `if: always() && github.ref == 'refs/heads/main'`
- **Purpose**: Downloads history from GitHub Pages deployment (primary method)
- **Method**: Calls `scripts/ci/download-allure-history.sh` with `pages` method
- **Behavior**: Fails if download fails (critical step)
- **Target**: `allure-results-combined/history/`

**Step 3: Verify History Download**
- **Line**: ~1283-1329
- **Condition**: `if: always() && github.ref == 'refs/heads/main'`
- **Purpose**: Verifies history was downloaded correctly
- **Checks**: File count, directory existence, file sizes
- **Behavior**: Fails if history should exist but wasn't downloaded

**Step 4: Generate Combined Allure Report**
- **Line**: ~1337-1341
- **Condition**: `if: always()`
- **Purpose**: Generates Allure report with history merging
- **Script**: `scripts/ci/generate-combined-allure-report.sh`
- **Arguments**: `allure-results-combined` (results) `allure-report-combined` (output)

**Step 5: Verify History in Report**
- **Line**: ~1343-1375
- **Condition**: `if: always()`
- **Purpose**: Verifies history exists in generated report
- **Checks**: History directory existence, file count, file sizes

**Step 6: Upload Allure History (for next run)**
- **Line**: ~1377-1420
- **Condition**: `if: always() && github.ref == 'refs/heads/main'`
- **Purpose**: Prepares history artifact for next run
- **Artifact Name**: `allure-history`
- **Retention**: 90 days
- **Behavior**: Only uploads if history directory exists with actual files

**Step 7: Deploy to GitHub Pages**
- **Line**: ~1506-1512
- **Condition**: `if: always() && github.ref == 'refs/heads/main' && needs.determine-schedule-type.outputs.code-changed == 'true'`
- **Action**: `peaceiris/actions-gh-pages@v4`
- **Configuration**:
  - `keep_files: true` ⭐ **CRITICAL** - Preserves history across deployments
  - `publish_dir: ./allure-report-combined`
  - `force_orphan: false`

### Key Scripts

**`scripts/ci/download-allure-history.sh`**:
- **Purpose**: Downloads history from GitHub Pages or artifacts
- **Usage**: `./scripts/ci/download-allure-history.sh <target-dir> [method]`
- **Methods**: `pages` (default) or `artifact`
- **GitHub API**: Uses `https://api.github.com/repos/CScharer/full-stack-qa/contents/history?ref=gh-pages`
- **Features**:
  - Downloads entire `history/` directory via GitHub API
  - Handles API responses (array, error, empty)
  - Properly counts downloaded files
  - Better error handling
  - Falls back to artifact method if GitHub Pages unavailable
- **Target Directory**: Creates `$TARGET_DIR/history/` directory
- **Error Handling**: Exits with error if download fails (critical step)

**`scripts/ci/generate-combined-allure-report.sh`**:
- **Purpose**: Generates Allure report with manual history merging
- **Usage**: `./scripts/ci/generate-combined-allure-report.sh <results-dir> <report-dir>`
- **Key Functions**:
  1. **History Verification** (lines ~140-277):
     - Checks if history exists in `RESULTS_DIR/history/`
     - Extracts test data from current run's result files
     - Manually merges current run's data with existing history
  2. **History Merge Logic** (lines ~188-265):
     - Uses `jq` to merge `history-trend.json` and `duration-trend.json`
     - Deduplicates using `group_by(.buildOrder) | map(last)`
     - Ensures flat array structure (not nested)
     - Extracts `historyId`, `status`, `start`, `stop` from result files
  3. **Report Generation** (lines ~278-293):
     - Runs `allure generate "$RESULTS_DIR" -o "$REPORT_DIR"`
     - Allure3 attempts to merge history from `RESULTS_DIR/history/`
  4. **History Fallback** (lines ~295-320):
     - If Allure3 doesn't create history, regenerates report
     - If still no history, copies manually merged history to report
  5. **History Preservation** (lines ~322-370):
     - Copies history from `REPORT_DIR/history/` back to `RESULTS_DIR/history/`
     - Removes empty history files (just `[]` or `{}`)
     - Preserves history for next run

**`scripts/temp/test-trending-merge-tracker.sh`**:
- **Purpose**: Tracks merge iterations for testing
- **Current Value**: `MERGE_NUMBER=35`
- **Usage**: Updated before each merge to trigger pipeline runs
- **Location**: `scripts/temp/` (temporary tracking script)

### History File Structure

**File Locations**:
- **In Results Directory**: `allure-results-combined/history/` (before generation)
- **In Report Directory**: `allure-report-combined/history/` (after generation)
- **In GitHub Pages**: `https://cscharer.github.io/full-stack-qa/history/` (deployed)
- **In Artifacts**: `allure-history/` (uploaded for next run)

**Required Files**:
- `history-trend.json` - Test execution trends over time
- `duration-trend.json` - Test duration trends
- `retry-trend.json` - Retry attempt trends (optional, may be empty)
- `{md5-hash}.json` - Individual test history files (created by Allure3, not manually)

**history-trend.json** (Flat Array Format):
```json
[
  {
    "buildOrder": 474,
    "reportUrl": "",
    "reportName": "Allure Report",
    "data": [
      {
        "uid": "test-historyId-from-result-file",
        "status": "passed",
        "time": {
          "start": 1234567890,
          "stop": 1234567891,
          "duration": 1000
        }
      }
    ]
  }
]
```

**duration-trend.json** (Flat Array Format):
```json
[
  {
    "buildOrder": 474,
    "data": [
      {
        "uid": "test-historyId-from-result-file",
        "time": {
          "start": 1234567890,
          "stop": 1234567891,
          "duration": 1000
        }
      }
    ]
  }
]
```

**Key Requirements**:
- ✅ Flat array (not nested) - `[...]` not `[[...]]`
- ✅ One entry per buildOrder (deduplicated using `group_by(.buildOrder) | map(last)`)
- ✅ Valid JSON structure
- ✅ Actual test execution data (not empty arrays `[]`)
- ✅ `uid` field must match `historyId` from test result files
- ✅ `buildOrder` must match value from `executor.json`

**Data Extraction from Result Files**:
- **Source**: `allure-results-combined/*-result.json` files
- **Fields Extracted**:
  - `historyId` → `uid` (critical for matching)
  - `status` → `status` (passed/failed/skipped)
  - `start` → `time.start`
  - `stop` → `time.stop`
  - `stop - start` → `time.duration`

---

## 📈 Pipeline Results Summary

| Pipeline | PR | Status | Key Changes | History Status |
|----------|----|----|------------|----------------|
| #388 | #67 | ✅ Success | Initial implementation | No history (expected) |
| #393 | #69 | ✅ Success | Merge 2 | History download attempted |
| #397 | #71 | ✅ Success | Typo fix | GitHub API working |
| #405 | #75 | ✅ Success | `keep_files: true` | History preserved |
| #409 | #77 | ✅ Success | Improved download | History download working |
| #20727244460 | #91 | ✅ Success | Manual merge | History merged manually |
| #20755092486 | #104 | ✅ Success | Deduplication fix | Duplicates removed |
| #20757281496 | #104 | ✅ Success | Deduplication applied | Clean history structure |
| #20758689530 | #105 | ✅ Success | MERGE_NUMBER 35 | History preserved (212K→252K), Allure3 still not recognizing |

---

## 🎯 Key Learnings

### 1. GitHub Pages `keep_files` Behavior
- `keep_files: false` wipes entire branch on each deployment
- `keep_files: true` preserves existing files
- **This was the most critical fix** (PR #75)

### 2. Allure3 History Requirements
- Allure3 requires existing history to merge with
- Allure3 doesn't bootstrap history on first run
- History must be in `RESULTS_DIR/history/` before `allure generate`
- History files must be valid JSON with actual data (not empty arrays)

### 3. History File Format
- Must be flat array (not nested)
- Must have one entry per buildOrder (deduplicated)
- Must contain actual test execution data
- Structure must match Allure3's expected format

### 4. Allure3 Recognition Issue
- Even with correct structure, format, and data, Allure3 may not recognize manually created history
- Allure3 might require history files to be created by Allure3 itself
- This is the current blocker preventing history from appearing

---

## ⚠️ Current Status (2026-01-06)

### What's Working ✅
1. History download from GitHub Pages via GitHub API
2. History download from artifacts (fallback)
3. History merge logic (manual merge with current run's data)
4. Deduplication (removes all duplicate build orders)
5. Flat array structure (not nested)
6. History upload as artifact
7. History deployment to GitHub Pages
8. `keep_files: true` preserves history across deployments

### What's Not Working ❌
1. Allure3 not recognizing manually created history
2. History trends not visible in Allure Reports
3. Allure3 consistently says "didn't create history" even with valid history

### Current Implementation
- **Manual History Merge**: We manually merge current run's data with existing history
- **Deduplication**: Using `group_by(.buildOrder) | map(last)` to remove duplicates
- **Structure**: Flat array format (not nested)
- **Deployment**: History is deployed to GitHub Pages and available for next run

---

## 🔄 Next Steps

### Option 1: Continue Current Approach (20-30% Success Probability)
- Wait for next pipeline run (MERGE_NUMBER 35)
- Verify deduplication is working (no duplicate build orders)
- Check if Allure3 recognizes properly structured history
- **Risk**: May still not work if Allure3 requires self-created history

### Option 2: Let Allure3 Create History Naturally
- Remove manual history creation
- Let Allure3 create history after 5-10 runs naturally
- Ensure history is preserved between runs
- **Risk**: May take many runs before history appears

### Option 3: Switch to Allure2
- Allure2 may have different history requirements
- May be more accepting of manually created history
- **Risk**: Requires significant changes to CI/CD pipeline

### Option 4: Accept Limitation
- Accept that history may not work with Allure3 in this CI/CD setup
- Focus on other reporting features
- **Risk**: Loses trending functionality

---

## 🔍 Troubleshooting Guide

### How to Verify History is Working

**1. Check Pipeline Logs**:
- Look for "Download Previous Allure History" steps
- Verify "History directory found with X file(s)" message
- Check "History included in report" verification step
- Look for "History uploaded as artifact" message

**2. Check GitHub Pages**:
- Visit: `https://cscharer.github.io/full-stack-qa/history/`
- Verify history files exist (should see `history-trend.json`, `duration-trend.json`)
- Check file sizes (should be > 3 bytes, not just `[]`)

**3. Check Artifacts**:
- Go to Actions → Latest run → Artifacts
- Look for `allure-history` artifact
- Download and verify it contains history files

**4. Check Report**:
- Open Allure report on GitHub Pages
- Navigate to "Trends" or "Graphs" section
- Verify historical data is displayed (should show multiple build orders)

### Common Issues and Solutions

**Issue: "No history directory found"**
- **Cause**: First run or history download failed
- **Solution**: Expected for first run. For subsequent runs, check download steps in logs

**Issue: "History directory exists but is empty"**
- **Cause**: History files are empty arrays `[]`
- **Solution**: Script removes empty history. Wait for next run to populate

**Issue: "Allure3 didn't create history"**
- **Cause**: Allure3 doesn't recognize manually created history
- **Solution**: Current blocker. May need to let Allure3 create naturally

**Issue: "Duplicate build orders in history"**
- **Cause**: History merge logic not deduplicating correctly
- **Solution**: Fixed in PR #104, #105 using `group_by(.buildOrder) | map(last)`

**Issue: "Nested arrays in history"**
- **Cause**: History merge creating nested structure
- **Solution**: Fixed in PR #102 using `flatten` in jq commands

**Issue: "History not persisting across deployments"**
- **Cause**: `keep_files: false` was wiping history
- **Solution**: Fixed in PR #75 by setting `keep_files: true`

### Debugging Commands

**Check History in Results Directory**:
```bash
# Count history files
find allure-results-combined/history -type f -name "*.json" | wc -l

# View history-trend.json structure
cat allure-results-combined/history/history-trend.json | jq '.'

# Check for duplicates
cat allure-results-combined/history/history-trend.json | jq '[.[] | .buildOrder] | group_by(.) | map(select(length > 1))'

# Check if nested
cat allure-results-combined/history/history-trend.json | jq 'type'  # Should be "array", not "object"
```

**Check History in Report Directory**:
```bash
# Count history files
find allure-report-combined/history -type f -name "*.json" | wc -l

# View history structure
cat allure-report-combined/history/history-trend.json | jq '.'
```

**Check GitHub Pages History**:
```bash
# Download via curl
curl -s "https://cscharer.github.io/full-stack-qa/history/history-trend.json" | jq '.'

# Check via GitHub API
curl -s -H "Accept: application/vnd.github.v3+json" \
  "https://api.github.com/repos/CScharer/full-stack-qa/contents/history?ref=gh-pages" | \
  jq '.[] | select(.type == "file") | {name: .name, size: .size}'
```

**Check Build Order**:
```bash
# From executor.json
cat allure-results-combined/executor.json | jq '.buildOrder'

# From history files
cat allure-results-combined/history/history-trend.json | jq '[.[] | .buildOrder] | sort | unique'
```

### Verification Checklist

**After Each Pipeline Run**:
- [ ] History download steps completed successfully
- [ ] History files exist in `allure-results-combined/history/`
- [ ] History files are not empty (size > 3 bytes)
- [ ] No duplicate build orders in history files
- [ ] History structure is flat array (not nested)
- [ ] History exists in `allure-report-combined/history/` after generation
- [ ] History uploaded as artifact (if on main branch)
- [ ] History deployed to GitHub Pages (if on main branch)
- [ ] History visible in Allure report (if trends section exists)

**For First Run**:
- [ ] History download steps show "No history found (expected for first run)"
- [ ] Report generation completes successfully
- [ ] History directory may or may not exist (both are acceptable)
- [ ] History upload step may be skipped (expected)

**For Subsequent Runs**:
- [ ] History downloaded from GitHub Pages or artifact
- [ ] History merged with current run's data
- [ ] History includes multiple build orders
- [ ] History deployed to GitHub Pages
- [ ] Trends visible in Allure report (if Allure3 recognizes history)

---

## 📝 Implementation Details

### Files Modified

1. **`.github/workflows/ci.yml`**
   - **Location**: `.github/workflows/ci.yml`
   - **Job**: `combined-allure-report`
   - **Changes**:
     - Added "Download Previous Allure History (Artifact Fallback)" step (line ~1232)
     - Added "Download Previous Allure History (GitHub Pages)" step (line ~1274)
     - Added "Verify History Download" step (line ~1283)
     - Added "Verify History in Report" step (line ~1343)
     - Added "Upload Allure History (for next run)" step (line ~1377)
     - Changed `keep_files: false` to `keep_files: true` (line ~1509) ⭐ **CRITICAL**
   - **Conditions**: All history steps run only on `main` branch (`github.ref == 'refs/heads/main'`)

2. **`scripts/ci/download-allure-history.sh`**
   - **Location**: `scripts/ci/download-allure-history.sh`
   - **Created**: PR #67
   - **Changes**:
     - Fixed typo `$REO_NAME` → `$REPO_NAME` (PR #71, line ~52)
     - Complete rewrite to download entire directory (PR #77)
   - **Key Features**:
     - Downloads from GitHub API: `https://api.github.com/repos/CScharer/full-stack-qa/contents/history?ref=gh-pages`
     - Handles API responses (array, error, empty)
     - Properly counts downloaded files
     - Exits with error if download fails (critical step)

3. **`scripts/ci/generate-combined-allure-report.sh`**
   - **Location**: `scripts/ci/generate-combined-allure-report.sh`
   - **Changes**:
     - Enhanced history verification (PR #77, lines ~140-277)
     - Added manual history merge logic (PR #91+, lines ~188-265)
     - Fixed nested array issue (PR #102, added `flatten` in jq commands)
     - Added deduplication logic (PR #104, #105, lines ~193-209, ~229-245)
   - **Key Functions**:
     - Extracts test data from `*-result.json` files
     - Merges `history-trend.json` and `duration-trend.json`
     - Deduplicates using `group_by(.buildOrder) | map(last)`
     - Ensures flat array structure
     - Copies history to report if Allure3 doesn't create it

4. **`scripts/temp/test-trending-merge-tracker.sh`**
   - **Location**: `scripts/temp/test-trending-merge-tracker.sh`
   - **Created**: PR #67
   - **Purpose**: Track merge iterations for testing
   - **Current Value**: `MERGE_NUMBER=35`
   - **Updated**: 35 times (PRs #68-#105)

### Environment Variables

**GitHub Actions Context**:
- `github.ref` - Current branch ref (must be `refs/heads/main` for history steps)
- `github.run_id` - Current run ID (used to exclude from previous run search)
- `github.token` - GitHub token for API calls
- `needs.determine-schedule-type.outputs.code-changed` - Whether code changed (affects deployment)

**Script Variables**:
- `RESULTS_DIR` - Directory containing Allure results (default: `allure-results-combined`)
- `REPORT_DIR` - Directory for generated report (default: `allure-report-combined`)
- `TARGET_DIR` - Target directory for history download (default: `allure-results-combined`)
- `CURRENT_BUILD_ORDER` - Current build order from `executor.json`

### Branch Conditions

**History Steps Only Run on Main Branch**:
- All history download/upload steps have condition: `if: always() && github.ref == 'refs/heads/main'`
- This ensures:
  - History is only collected for production/main branch
  - Feature branches don't interfere with main history
  - Single source of truth for history

**GitHub Pages Deployment**:
- Condition: `if: always() && github.ref == 'refs/heads/main' && needs.determine-schedule-type.outputs.code-changed == 'true'`
- Only deploys when:
  - On main branch
  - Code actually changed (not just documentation)

### Dependencies

**Required Tools**:
- `jq` - JSON processor (for history merge logic)
- `curl` - HTTP client (for GitHub API calls)
- `gh` - GitHub CLI (for artifact download)
- `allure` - Allure3 CLI (for report generation)

**Required Files**:
- `executor.json` - Contains `buildOrder` for current run
- `*-result.json` - Test result files with `historyId`, `status`, `start`, `stop`
- `history-trend.json` - History trend data (downloaded or created)
- `duration-trend.json` - Duration trend data (downloaded or created)

---

## 🔗 Related Documentation

- [Allure Reporting Guide](../guides/testing/ALLURE_REPORTING.md)
- [GitHub Pages Setup](../guides/infrastructure/GITHUB_PAGES_SETUP.md)
- [Allure History Documentation](https://allurereport.org/docs/history-and-retries/)

---

## 📊 Success Metrics

### Fix Success Rate
- **Total Issues Identified**: 6
- **Issues Fixed**: 5 (83%)
- **Issues Remaining**: 1 (Allure3 recognition)

### Evaluation Accuracy
- **Total Evaluations**: 10+
- **Correct Evaluations**: 0
- **Incorrect Evaluations**: 10+
- **Accuracy Rate**: 0%

**Key Learning**: Each fix addressed a valid issue, but the fundamental problem (Allure3 not recognizing manually created history) was not identified until many attempts.

---

## 🎯 Conclusion

The Allure reporting implementation required extensive work to fix multiple issues:
1. ✅ GitHub Pages `keep_files` issue
2. ✅ Typo in download script
3. ✅ Download script file matching
4. ✅ Nested array structure
5. ✅ Duplicate build orders
6. ⚠️ Allure3 recognition (ongoing)

**Current State**: All infrastructure fixes are complete, but Allure3 still doesn't recognize manually created history. The next pipeline run (MERGE_NUMBER 35) will test if the deduplication fix allows Allure3 to recognize the properly structured history.

**Recommendation**: If history still doesn't appear after MERGE_NUMBER 35, consider alternative approaches (let Allure3 create naturally, switch to Allure2, or accept limitation).

---

---

## 📚 Reference Information

### Allure3 Documentation
- [Allure History Documentation](https://allurereport.org/docs/history-and-retries/)
- [Allure3 CLI Documentation](https://allurereport.org/docs/cli/)

### GitHub Actions
- [peaceiris/actions-gh-pages@v4](https://github.com/peaceiris/actions-gh-pages)
- [actions/upload-artifact@v4](https://github.com/actions/upload-artifact)
- [actions/download-artifact@v4](https://github.com/actions/download-artifact)

### Related Internal Documentation
- [Allure Reporting Guide](../guides/testing/ALLURE_REPORTING.md)
- [GitHub Pages Setup](../guides/infrastructure/GITHUB_PAGES_SETUP.md)

### Key URLs
- **GitHub Pages Report**: `https://cscharer.github.io/full-stack-qa/`
- **GitHub Pages History**: `https://cscharer.github.io/full-stack-qa/history/`
- **GitHub API History**: `https://api.github.com/repos/CScharer/full-stack-qa/contents/history?ref=gh-pages`
- **Repository**: `https://github.com/CScharer/full-stack-qa`

### Important Notes

**Why History is Main-Only**:
- Centralized history (single source of truth)
- GitHub Pages only deploys from main
- Feature branch reports are for review, not production history
- Prevents history conflicts between branches

**Why Manual History Merge**:
- Allure3 doesn't bootstrap history on first run
- Allure3 requires existing history to merge with
- Manual merge ensures history structure is correct
- Manual merge handles deduplication and format issues

**Why Deduplication is Critical**:
- Duplicate build orders cause Allure3 to fail processing
- Same buildOrder can appear multiple times if history merge runs multiple times
- `group_by(.buildOrder) | map(last)` ensures only latest entry per buildOrder

**Why Flat Array Structure**:
- Allure3 expects flat arrays, not nested
- Nested arrays cause parsing errors
- `flatten` in jq commands ensures flat structure

---

---

## 📊 Latest Pipeline Results (Pipeline #20758689530 - MERGE_NUMBER 35)

**Date**: 2026-01-06  
**Pipeline Run**: #20758689530  
**Status**: ✅ Success  
**Build Order**: 476

### History Download Results ✅

**Artifact Fallback**:
- ✅ Successfully downloaded 3 files from previous run (20757281496)
- ✅ History files found and downloaded

**GitHub Pages Download**:
- ✅ Successfully downloaded 5 files (212K total)
- ✅ Files downloaded: `.gitkeep`, `duration-trend.json`, `duration-trend.json.tmp`, `history-trend.json`, `retry-trend.json`
- ✅ GitHub API working correctly

**History Verification**:
- ✅ History directory exists: 5 files, 212K
- ✅ Sample files: `duration-trend.json`, `history-trend.json`, `retry-trend.json`

### Report Generation Results ⚠️

**History Merge**:
- ✅ History found in results directory: 3 files, 212K
- ✅ Manually merged current run's data (100 entries) with existing history
- ✅ Build order: 476
- ✅ Merge completed successfully

**Allure3 Behavior**:
- ❌ Allure3 did NOT create history directory after first generation
- ❌ Allure3 did NOT create history after regeneration attempt
- ✅ Manually merged history copied to report directory (3 files, 252K)
- ✅ History size increased (212K → 252K), confirming merge worked

**Report Output**:
- ✅ Report generated successfully: 4.3M, 286 result files processed
- ✅ History directory exists in report: 3 files, 252K
- ✅ History preserved for next run

### History Upload Results ✅

**Artifact Upload**:
- ✅ History directory contains 3 file(s)
- ✅ Size: 252K
- ✅ History artifact ready for upload
- ✅ Artifact uploaded successfully: 35,983 bytes
- ✅ Artifact ID: 5040944181

### Key Findings

**What's Working** ✅:
1. History download from both artifact and GitHub Pages
2. History merge logic (100 entries merged successfully)
3. History preservation (size growing: 212K → 252K)
4. History upload as artifact
5. History structure appears correct (3 files, valid JSON)
6. Deduplication working (no duplicate errors)

**What's Still Not Working** ❌:
1. Allure3 still not recognizing manually created history
2. Allure3 consistently says "didn't create history" even after regeneration
3. Trends not visible in Allure Reports (likely because Allure3 doesn't process manually created history)

**Observations**:
- History is being preserved and accumulating correctly
- Manual merge is working (history size increased)
- Allure3 appears to have a hard requirement that history must be created by Allure3 itself
- Even with correct structure, format, and data, Allure3 refuses to process manually created history

**Next Steps**:
- Continue monitoring if Allure3 eventually recognizes the manually created history after more runs
- Consider alternative approaches if Allure3 continues to reject manually created history
- Verify if trends appear in the actual Allure report UI (may work even if Allure3 says it didn't create history)

---

**Last Updated**: 2026-01-06  
**Document Location**: `docs/work/20260106_ALLURE_REPORTINGWORK.md`  
**Status**: Active investigation ongoing  
**Current MERGE_NUMBER**: 35  
**Latest Pipeline**: #20758689530 (2026-01-06)

