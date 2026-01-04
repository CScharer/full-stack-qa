# Test Trending Implementation - Track Test Results Over Time

**Date Created**: 2026-01-04  
**Status**: 📋 Planning  
**Priority**: 🟢 Low Priority  
**Estimated Time**: 16 hours  
**Related**: Allure Reports - Historical Trends

---

## 📋 Overview

This document outlines the implementation plan to enable test trending in Allure reports. Test trending allows you to track test results over time, showing:
- Test pass/fail rates over multiple runs
- Duration trends (performance regressions)
- Flaky test detection
- Historical test execution data

**Current Issue**: Allure reports mention historical trends, but trends are not showing up because history is not being preserved across GitHub Pages deployments.

---

## 🎯 Current State Analysis

### What Exists

1. **Allure History Preservation Script** (`scripts/ci/preserve-allure-history.sh`)
   - ✅ Script exists to preserve history
   - ✅ Copies history from report back to results directory
   - ⚠️ **Problem**: Only works within the same pipeline run

2. **History Preservation in Report Generation** (`scripts/ci/generate-combined-allure-report.sh`)
   - ✅ Preserves history after report generation
   - ✅ Copies history from report to results directory
   - ⚠️ **Problem**: History is lost when GitHub Pages is deployed with `keep_files: false`

3. **GitHub Pages Deployment** (`.github/workflows/ci.yml`)
   - ✅ Reports are deployed to GitHub Pages
   - ⚠️ **Problem**: `keep_files: false` wipes the entire `gh-pages` branch on each deployment
   - ⚠️ **Problem**: No mechanism to download previous report's history before generating new report

### Why Trends Aren't Showing

**Root Cause**: The history folder from the previous report is not available when generating the new report because:
1. GitHub Pages deployment uses `keep_files: false`, which deletes all files before deploying
2. The history folder is not downloaded from the previous deployment before generating the new report
3. Allure needs the history folder in the results directory BEFORE generating the report

**Current Flow (Broken)**:
```
Pipeline Run 1:
  1. Generate report (no history) → Creates history/
  2. Deploy to GitHub Pages → History is deployed
  3. Preserve history in results → But results are artifacts, not used in next run

Pipeline Run 2:
  1. Generate report (no history available) → Creates NEW history/ (empty)
  2. Deploy to GitHub Pages → Wipes old history, deploys new (empty) history
  3. History is lost!
```

**Required Flow (Fixed)**:
```
Pipeline Run 1:
  1. Generate report (no history) → Creates history/
  2. Deploy to GitHub Pages → History is deployed

Pipeline Run 2:
  1. Download previous report's history from GitHub Pages
  2. Copy history to results directory
  3. Generate report (with history) → Allure merges old + new history
  4. Deploy to GitHub Pages → History is preserved and updated
```

---

## 🛠️ Implementation Plan

### Phase 1: Download Previous History from GitHub Pages (4 hours)

**Goal**: Download the history folder from the previous GitHub Pages deployment before generating the new report.

#### Step 1.1: Create History Download Script ✅

**File**: `scripts/ci/download-allure-history.sh`

**Purpose**: Download the `history/` folder from the current GitHub Pages deployment.

**Status**: ✅ **COMPLETED**

**Implementation**:
```bash
#!/bin/bash
# Download Allure History from GitHub Pages
# Usage: ./scripts/ci/download-allure-history.sh <target-dir>

TARGET_DIR="${1:-allure-results-combined}"
GITHUB_PAGES_URL="https://cscharer.github.io/full-stack-qa/history"

echo "📥 Downloading Allure history from GitHub Pages..."

# Create history directory
mkdir -p "$TARGET_DIR/history"

# Download history files from GitHub Pages
# Note: GitHub Pages serves files directly, so we can use curl/wget
if curl -f -s "$GITHUB_PAGES_URL" > /dev/null 2>&1; then
    echo "✅ GitHub Pages is accessible"
    
    # Download history JSON files
    # Allure history typically contains:
    # - history-trend.json
    # - duration-trend.json
    # - retry-trend.json
    # - etc.
    
    # Download each history file
    for history_file in "history-trend.json" "duration-trend.json" "retry-trend.json"; do
        if curl -f -s "$GITHUB_PAGES_URL/$history_file" -o "$TARGET_DIR/history/$history_file" 2>/dev/null; then
            echo "   ✅ Downloaded: $history_file"
        else
            echo "   ⚠️  Not found: $history_file (may not exist yet)"
        fi
    done
    
    # Also try to download the entire history directory structure
    # GitHub Pages may serve it as a directory listing or we may need to download individual files
else
    echo "⚠️  GitHub Pages not accessible or first run (no history yet)"
    echo "   This is expected for the first pipeline run"
fi

echo "✅ History download complete"
```

**Features Implemented**:
- ✅ Downloads from GitHub Pages (primary method)
- ✅ Falls back to artifact method if GitHub Pages unavailable
- ✅ Uses GitHub API for reliable file listing
- ✅ Handles first run gracefully (no history expected)
- ✅ Verifies downloaded files
- ✅ Provides detailed logging

**Alternative Approach**: Use GitHub API to download from `gh-pages` branch (✅ Implemented):
```bash
# Download history from gh-pages branch using GitHub API
GITHUB_API="https://api.github.com/repos/CScharer/full-stack-qa/contents"
BRANCH="gh-pages"
HISTORY_PATH="history"

# Get file listing from gh-pages branch
curl -s "$GITHUB_API/$HISTORY_PATH?ref=$BRANCH" | jq -r '.[] | select(.type == "file") | .download_url' | while read url; do
    filename=$(basename "$url")
    curl -s "$url" -o "$TARGET_DIR/history/$filename"
    echo "   ✅ Downloaded: $filename"
done
```

#### Step 1.2: Integrate into CI Workflow ✅

**Location**: `.github/workflows/ci.yml` - `combined-allure-report` job

**Status**: ✅ **COMPLETED**

**Implementation**:
```yaml
- name: Download Previous Allure History
  if: always() && github.ref == 'refs/heads/main'
  run: |
    chmod +x scripts/ci/download-allure-history.sh
    ./scripts/ci/download-allure-history.sh "allure-results-combined" "pages"
```

**Placement**: ✅ Added after "Prepare combined Allure results" step, before "Install Allure3 CLI" step.

**Details**:
- ✅ Runs only on `main` branch (where GitHub Pages is deployed)
- ✅ Uses `pages` method (downloads from GitHub Pages)
- ✅ Runs with `if: always()` to ensure it runs even if previous steps fail
- ✅ Executes before report generation so history is available

#### Step 1.3: Handle First Run ✅

**Consideration**: First pipeline run won't have history. Script should handle this gracefully.

**Status**: ✅ **COMPLETED** - Already implemented in script

**Implementation**: 
- ✅ Script checks if GitHub Pages is accessible
- ✅ If no history files found, logs informative message (expected for first run)
- ✅ Creates empty history directory (Allure will populate it)
- ✅ Script continues without errors even if no history exists
- ✅ All error conditions are handled gracefully with informative messages

---

### Phase 2: Verify History Integration (2 hours) ✅

**Goal**: Ensure history is properly integrated into the report generation process.

**Status**: ✅ **COMPLETED**

#### Step 2.1: Verify History Download ✅

**Check**: History files are downloaded before report generation.

**Status**: ✅ **COMPLETED**

**Implementation**:
```yaml
- name: Verify History Download
  if: always() && github.ref == 'refs/heads/main'
  run: |
    # Checks history directory exists and shows file count, size, and sample files
    # Handles first run gracefully
```

**Details**:
- ✅ Verifies history directory exists
- ✅ Shows file count and size
- ✅ Lists sample files for verification
- ✅ Handles first run (empty directory) gracefully
- ✅ Only runs on main branch (where history download happens)

#### Step 2.2: Verify History in Report ✅

**Check**: Generated report includes history data.

**Status**: ✅ **COMPLETED**

**Implementation**:
```yaml
- name: Verify History in Report
  if: always()
  run: |
    # Checks history directory in generated report
    # Shows file count, size, and sample files
    # Warns if history is missing
```

**Details**:
- ✅ Verifies history directory exists in generated report
- ✅ Shows file count and size
- ✅ Lists sample files for verification
- ✅ Warns if history is missing (indicates potential issue)
- ✅ Confirms history will be preserved in deployment

---

### Phase 3: Alternative: Use Artifacts for History (4 hours) ✅

**Alternative Approach**: Instead of downloading from GitHub Pages, use GitHub Actions artifacts to preserve history.

**Status**: ✅ **COMPLETED** - Implemented as fallback mechanism

#### Step 3.1: Upload History as Separate Artifact ✅

**After report generation**:

**Status**: ✅ **COMPLETED**
```yaml
- name: Upload Allure History (for next run)
  if: always() && github.ref == 'refs/heads/main'
  uses: actions/upload-artifact@v4
  with:
    name: allure-history
    path: allure-report-combined/history/
    retention-days: 90
    if-no-files-found: ignore
```

**Details**:
- ✅ Uploads history after report generation
- ✅ Keeps history for 90 days (longer than report artifacts)
- ✅ Only runs on main branch
- ✅ Handles missing history gracefully

#### Step 3.2: Download History Artifact in Next Run ✅

**Before report generation**:

**Status**: ✅ **COMPLETED**
```yaml
- name: Download Previous Allure History (Artifact Fallback)
  if: always() && github.ref == 'refs/heads/main'
  uses: actions/download-artifact@v4
  continue-on-error: true
  with:
    name: allure-history
    path: allure-results-combined/history/
    pattern: allure-history
    merge-multiple: false
```

**Implementation Strategy**:
- ✅ **Hybrid Approach**: Downloads artifact first (fallback), then tries GitHub Pages (primary)
- ✅ Artifact download runs with `continue-on-error: true` (won't fail if no artifact exists)
- ✅ GitHub Pages download runs after artifact download (can overwrite/merge)
- ✅ Best of both worlds: Artifact is reliable fallback, GitHub Pages is primary source

**Advantages**:
- ✅ More reliable than GitHub Pages (no dependency on public URL)
- ✅ Can keep history for longer (90 days vs. current deployment)
- ✅ Works even if GitHub Pages is down

**Disadvantages**:
- ⚠️ Requires artifact retention (uses GitHub Actions storage)
- ⚠️ History is separate from report (but that's fine)

---

### Phase 4: Update Documentation (2 hours) ✅

**Goal**: Update Allure reporting documentation to reflect trending implementation.

**Status**: ✅ **COMPLETED**

#### Step 4.1: Update ALLURE_REPORTING.md ✅

**Add section**: "Historical Trends - How It Works"

**Status**: ✅ **COMPLETED**

**Content Added**:
- ✅ Explained how history is preserved in CI/CD
- ✅ Documented the automatic download process
- ✅ Described history preservation methods (GitHub Pages + Artifact)
- ✅ Added note about manual local usage
- ✅ Updated status and last updated date

#### Step 4.2: Update GITHUB_PAGES_SETUP.md ✅

**Update**: "Historical Trends" section

**Status**: ✅ **COMPLETED**

**Content Added**:
- ✅ Explained that `keep_files: false` is fine (history is downloaded separately)
- ✅ Documented the history download mechanism
- ✅ Added detailed explanation of how history is preserved
- ✅ Documented implementation details (script, workflow, artifact)
- ✅ Updated status and last updated date

---

### Phase 5: Testing & Validation (4 hours)

**Goal**: Test the trending implementation across multiple pipeline runs.

**Status**: ⏳ **READY FOR TESTING** - Implementation complete, ready for pipeline validation

**Note**: Testing will be performed in the CI/CD pipeline after merging. Steps below outline what to verify.

#### Step 5.1: Test First Run

**Expected**: 
- No history available (first run)
- Report generates successfully
- History folder created in report
- History uploaded as artifact (if using artifact approach)

#### Step 5.2: Test Second Run

**Expected**:
- History downloaded from previous run
- History included in results directory
- Report generated with history
- Trends section shows data from 2 runs

#### Step 5.3: Test Multiple Runs

**Expected**:
- History accumulates over multiple runs
- Trends section shows data from all runs
- Duration trends visible
- Pass/fail rate trends visible

#### Step 5.4: Verify Trends in Report

**Check**:
- Open Allure report on GitHub Pages
- Navigate to "Trends" or "Graphs" section
- Verify historical data is displayed
- Verify multiple data points are shown

---

## 🔍 Technical Details

### How Allure History Works

**History Files**:
- `history/history-trend.json` - Test execution trends over time
- `history/duration-trend.json` - Test duration trends
- `history/retry-trend.json` - Retry attempt trends
- `history/history.json` - Overall execution history

**How Allure Uses History**:
1. Allure looks for `history/` folder in the results directory
2. If found, Allure merges old history with new results
3. Generated report includes combined history
4. History is updated with new execution data

**Key Point**: History must be in the **results directory** BEFORE `allure generate` is called.

### GitHub Pages Deployment

**Current Configuration**:
```yaml
- name: Deploy to GitHub Pages
  uses: peaceiris/actions-gh-pages@v3
  with:
    github_token: ${{ secrets.GITHUB_TOKEN }}
    publish_dir: ./allure-report-combined
    keep_files: false  # This wipes everything!
```

**Why `keep_files: false`**:
- Ensures clean deployments
- Prevents old files from accumulating
- Removes stale reports

**Impact on History**:
- History folder is deleted on each deployment
- Must be downloaded from previous deployment before generating new report

---

## 📊 Implementation Options Comparison

### Option A: Download from GitHub Pages (Recommended)

**Pros**:
- ✅ History is always with the report
- ✅ No additional artifact storage needed
- ✅ History is publicly accessible
- ✅ Simpler workflow (one download step)

**Cons**:
- ⚠️ Depends on GitHub Pages being accessible
- ⚠️ Requires public URL access
- ⚠️ May have rate limiting issues

**Implementation**: Download history from `https://cscharer.github.io/full-stack-qa/history/` before generating report.

### Option B: Use GitHub Actions Artifacts

**Pros**:
- ✅ More reliable (no public URL dependency)
- ✅ Can keep history longer (90 days)
- ✅ Works even if GitHub Pages is down
- ✅ Better for private repositories

**Cons**:
- ⚠️ Uses GitHub Actions storage quota
- ⚠️ History is separate from report
- ⚠️ Requires artifact retention management

**Implementation**: Upload history as artifact, download in next run.

### Option C: Hybrid Approach

**Pros**:
- ✅ Best of both worlds
- ✅ Fallback if GitHub Pages is unavailable
- ✅ More resilient

**Cons**:
- ⚠️ More complex implementation
- ⚠️ Requires both mechanisms

**Implementation**: Try GitHub Pages first, fallback to artifact if unavailable.

---

## 🎯 Recommended Implementation

**Recommended**: **Option A (Download from GitHub Pages)** with **Option B (Artifacts) as fallback**

**Rationale**:
- Primary: Download from GitHub Pages (simpler, history stays with report)
- Fallback: Use artifacts if GitHub Pages download fails
- Best user experience: History always available

---

## 📝 Step-by-Step Implementation

### Step 1: Create History Download Script

1. Create `scripts/ci/download-allure-history.sh`
2. Implement GitHub Pages download logic
3. Add artifact fallback logic
4. Test script locally (if possible)

### Step 2: Update CI Workflow

1. Add "Download Previous Allure History" step before report generation
2. Add verification step after download
3. Ensure history is in results directory before `allure generate`

### Step 3: Update History Preservation

1. Verify `preserve-allure-history.sh` is called after report generation
2. Ensure history is copied back to results directory
3. Verify history is included in GitHub Pages deployment

### Step 4: Test Implementation

1. Run pipeline (first run - no history)
2. Verify report generates successfully
3. Run pipeline again (second run - should download history)
4. Verify trends section shows data
5. Run multiple times to accumulate history

### Step 5: Update Documentation

1. Update `ALLURE_REPORTING.md` with trending details
2. Update `GITHUB_PAGES_SETUP.md` with history preservation info
3. Add troubleshooting section

---

## ✅ Acceptance Criteria

- [ ] History is downloaded from previous deployment before generating new report
- [ ] History is included in results directory before `allure generate`
- [ ] Generated report includes historical data
- [ ] Trends section in Allure report shows data from multiple runs
- [ ] History persists across multiple pipeline runs
- [ ] Fallback mechanism works if GitHub Pages is unavailable
- [ ] Documentation updated with implementation details
- [ ] Verification steps added to CI workflow

---

## 🚨 Troubleshooting

### History Not Showing in Report

**Possible Causes**:
1. History not downloaded before report generation
2. History in wrong location (must be in results directory)
3. History files corrupted or incomplete
4. Allure version incompatibility

**Solutions**:
1. Verify download step runs before report generation
2. Check history directory location: `allure-results-combined/history/`
3. Verify history files are valid JSON
4. Check Allure version compatibility

### GitHub Pages Download Fails

**Possible Causes**:
1. GitHub Pages not accessible
2. First run (no history exists yet)
3. Network issues
4. Rate limiting

**Solutions**:
1. Use artifact fallback mechanism
2. Check GitHub Pages URL is correct
3. Verify network connectivity
4. Add retry logic to download script

### History Not Persisting

**Possible Causes**:
1. History not uploaded as artifact
2. Artifact retention expired
3. History not included in GitHub Pages deployment

**Solutions**:
1. Verify artifact upload step
2. Check artifact retention settings
3. Verify history folder is in report before deployment

---

## 📚 Related Documentation

- [Allure Reporting Guide](../guides/testing/ALLURE_REPORTING.md) - Current Allure setup
- [GitHub Pages Setup](../guides/infrastructure/GITHUB_PAGES_SETUP.md) - Deployment configuration
- [Allure History Documentation](https://docs.qameta.io/allure/#_history) - Official Allure docs

---

## 🔗 Related Files

- `scripts/ci/preserve-allure-history.sh` - Current history preservation script
- `scripts/ci/generate-combined-allure-report.sh` - Report generation script
- `.github/workflows/ci.yml` - CI/CD workflow (combined-allure-report job)
- `docs/guides/testing/ALLURE_REPORTING.md` - Allure documentation

---

**Last Updated**: 2026-01-04  
**Document Location**: `docs/work/20260104_TEST_TRENDING_IMPLEMENTATION.md`

