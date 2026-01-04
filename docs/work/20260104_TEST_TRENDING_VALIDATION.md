# Test Trending Validation - What Can Be Tested Without Merging

**Date Created**: 2026-01-04  
**Related PR**: #67  
**Status**: 📋 Validation Guide

---

## ✅ What Can Be Validated in PR Pipeline (Without Merging)

### 1. Script Syntax and Structure ✅

**What**: Validate that the download script is syntactically correct and executable.

**How to Verify**:
- ✅ Script exists: `scripts/ci/download-allure-history.sh`
- ✅ Script is executable (chmod +x)
- ✅ Bash syntax is valid (no syntax errors)
- ✅ Script handles errors gracefully

**Expected in PR Pipeline**:
```
✅ Script syntax validation (if script runs)
✅ Script structure verification
```

**Status**: ✅ **Already validated locally** - Script syntax checked, dry run successful

---

### 2. Workflow Syntax ✅

**What**: Validate that the workflow YAML is syntactically correct.

**How to Verify**:
- ✅ Workflow YAML is valid
- ✅ Job dependencies are correct
- ✅ Step conditions are valid
- ✅ No syntax errors

**Expected in PR Pipeline**:
```
✅ GitHub Actions validates workflow syntax
✅ Workflow runs without syntax errors
```

**Status**: ✅ **Will be validated by GitHub Actions** when PR is created

---

### 3. Report Generation Works ✅

**What**: Validate that Allure reports can be generated with the new workflow steps.

**How to Verify**:
- ✅ Report generation job runs successfully
- ✅ Report is created in `allure-report-combined/`
- ✅ Report structure is correct (index.html, data/, widgets/, etc.)
- ✅ No errors during report generation

**Expected in PR Pipeline**:
```
✅ combined-allure-report job runs
✅ Report generated successfully
✅ Report structure verified
```

**Status**: ✅ **Will be validated in PR pipeline** - Report generation runs on all branches

---

### 4. History Verification Steps Run ✅

**What**: Validate that verification steps execute correctly (even if history is empty).

**How to Verify**:
- ✅ "Verify History Download" step runs
- ✅ "Verify History in Report" step runs
- ✅ Steps handle empty history gracefully (first run scenario)
- ✅ Steps provide informative output

**Expected in PR Pipeline**:
```
✅ Verification steps execute
✅ Steps handle empty history (expected for PR)
✅ Informative messages logged
```

**Status**: ✅ **Will be validated in PR pipeline** - Verification steps run on all branches

**Note**: History download steps will be **skipped** on PR (main-only condition), but verification steps will run and show "expected for first run" messages.

---

### 5. Script Execution Logic (Partial) ⚠️

**What**: Validate that the download script logic is sound (even if it doesn't find history).

**How to Verify**:
- ✅ Script attempts to download from GitHub Pages
- ✅ Script handles "no history found" gracefully
- ✅ Script provides informative output
- ✅ Script doesn't fail if history doesn't exist

**Expected in PR Pipeline**:
```
⚠️  History download steps SKIPPED (main-only condition)
✅ But we can verify script would work by checking:
   - Script is present and executable
   - Script logic is sound (from code review)
```

**Status**: ⚠️ **Partially validated** - Script logic verified, but actual download won't run on PR

---

### 6. Artifact Upload Works ✅

**What**: Validate that report artifacts are uploaded correctly.

**How to Verify**:
- ✅ Report artifact is uploaded
- ✅ Artifact contains report files
- ✅ Artifact can be downloaded and viewed

**Expected in PR Pipeline**:
```
✅ Report artifact uploaded
✅ Artifact contains complete report
✅ Artifact can be downloaded for review
```

**Status**: ✅ **Will be validated in PR pipeline** - Artifact upload runs on all branches

**Note**: History artifact upload will be **skipped** on PR (main-only condition).

---

## ❌ What CANNOT Be Validated in PR Pipeline (Requires Merge)

### 1. Actual History Download from GitHub Pages ❌

**Why**: History download steps have `if: always() && github.ref == 'refs/heads/main'` condition.

**What's Missing**:
- ❌ Can't test downloading history from GitHub Pages
- ❌ Can't test GitHub API download method
- ❌ Can't verify history files are actually downloaded

**Requires**: Merge to main, then second run to test history download.

---

### 2. History Upload to Artifact ❌

**Why**: History upload step has `if: always() && github.ref == 'refs/heads/main'` condition.

**What's Missing**:
- ❌ Can't test uploading history as artifact
- ❌ Can't verify artifact retention (90 days)
- ❌ Can't verify artifact is available for next run

**Requires**: Merge to main, then verify artifact is created.

---

### 3. History Persistence Across Runs ❌

**Why**: Requires multiple runs on main branch with history preservation.

**What's Missing**:
- ❌ Can't test history persistence across multiple runs
- ❌ Can't verify history accumulates correctly
- ❌ Can't test artifact fallback mechanism

**Requires**: 
1. Merge to main (first run - creates history)
2. Second run on main (downloads history, updates it)
3. Third run on main (verifies history persists)

---

### 4. Trends Appearing in Reports ❌

**Why**: Trends require historical data from multiple runs.

**What's Missing**:
- ❌ Can't see trends in Allure report (no historical data)
- ❌ Can't verify trend graphs are populated
- ❌ Can't test trend accuracy

**Requires**: 
1. Merge to main
2. Multiple runs on main (2-3+ runs to see trends)
3. View report on GitHub Pages

---

### 5. GitHub Pages Deployment with History ❌

**Why**: GitHub Pages deployment only happens on main branch.

**What's Missing**:
- ❌ Can't test GitHub Pages deployment
- ❌ Can't verify history is included in deployment
- ❌ Can't test history download from deployed Pages

**Requires**: Merge to main, then verify deployment includes history.

---

## 📊 Validation Summary Table

| Validation Item | Can Test in PR? | Status | Notes |
|----------------|----------------|--------|-------|
| **Script Syntax** | ✅ Yes | ✅ Validated | Already checked locally |
| **Workflow Syntax** | ✅ Yes | ✅ Will Validate | GitHub Actions validates |
| **Report Generation** | ✅ Yes | ✅ Will Validate | Runs on all branches |
| **Verification Steps** | ✅ Yes | ✅ Will Validate | Runs on all branches |
| **Script Logic** | ⚠️ Partial | ⚠️ Code Review | Download skipped on PR |
| **Artifact Upload** | ✅ Yes | ✅ Will Validate | Report artifact only |
| **History Download** | ❌ No | ❌ Requires Merge | Main-only condition |
| **History Upload** | ❌ No | ❌ Requires Merge | Main-only condition |
| **History Persistence** | ❌ No | ❌ Requires Merge | Needs multiple runs |
| **Trends in Reports** | ❌ No | ❌ Requires Merge | Needs historical data |
| **GitHub Pages Deploy** | ❌ No | ❌ Requires Merge | Main-only deployment |

---

## 🎯 Recommended Validation Strategy

### Phase 1: PR Validation (Before Merge) ✅

**What to Check in PR Pipeline**:
1. ✅ Workflow runs without errors
2. ✅ Report generation succeeds
3. ✅ Verification steps run and show appropriate messages
4. ✅ Report artifact is uploaded and downloadable
5. ✅ No syntax or structural errors

**Expected PR Pipeline Output**:
```
✅ combined-allure-report job: SUCCESS
✅ Report generated successfully
✅ Verify History Download: "ℹ️  No history directory (expected for first run)"
✅ Verify History in Report: "✅ History included in report" (empty history)
✅ Report artifact uploaded
```

### Phase 2: Post-Merge Validation (After Merge) 🔄

**What to Check After Merge**:
1. **First Run (Merge)**: 
   - ✅ History download steps run (no history found - expected)
   - ✅ History created during report generation
   - ✅ History uploaded as artifact
   - ✅ History deployed to GitHub Pages

2. **Second Run (Next Pipeline)**:
   - ✅ History downloaded from GitHub Pages
   - ✅ History merged with new results
   - ✅ History updated in report
   - ✅ History uploaded and deployed

3. **Third Run (Trends Visible)**:
   - ✅ Trends section shows data from 2+ runs
   - ✅ Trend graphs are populated
   - ✅ Historical data is accurate

---

## ✅ PR Validation Checklist

Before merging, verify in PR pipeline:

- [ ] Workflow runs successfully (no errors)
- [ ] `combined-allure-report` job completes
- [ ] Report generation succeeds
- [ ] "Verify History Download" step shows: "ℹ️  No history directory (expected for first run)"
- [ ] "Verify History in Report" step shows: "✅ History included in report" (may be empty)
- [ ] Report artifact is uploaded and downloadable
- [ ] Report can be viewed locally (download artifact)
- [ ] No workflow syntax errors
- [ ] No script execution errors

**Note**: History download/upload steps will be **skipped** on PR (expected behavior).

---

## 🔄 Post-Merge Validation Checklist

**Note**: Multiple merges are required for full validation. Use `scripts/temp/test-trending-merge-tracker.sh` to track merge iterations.

After merging to main, verify:

### Merge 1: First Run (Initial Merge)

**Update**: `scripts/temp/test-trending-merge-tracker.sh` - Set `MERGE_NUMBER=1`

**Purpose**: Create initial history
- [ ] History download steps run (no history found - expected)
- [ ] Report generation succeeds
- [ ] History created in report (`allure-report-combined/history/`)
- [ ] History uploaded as artifact (`allure-history`)
- [ ] GitHub Pages deployment succeeds
- [ ] History included in GitHub Pages deployment

### Merge 2: Second Run (History Download & Update)

**Update**: `scripts/temp/test-trending-merge-tracker.sh` - Set `MERGE_NUMBER=2`

**Purpose**: Download and update history
- [ ] History downloaded from GitHub Pages (or artifact)
- [ ] History merged with new results
- [ ] Report shows updated history
- [ ] History uploaded and deployed

### Merge 3: Third Run (Trends Visible)

**Update**: `scripts/temp/test-trending-merge-tracker.sh` - Set `MERGE_NUMBER=3`

**Purpose**: Verify trends are visible
- [ ] Trends section in Allure report shows data
- [ ] Trend graphs are populated
- [ ] Historical data is accurate
- [ ] Multiple runs visible in trends

---

## 📝 Notes

**Key Points**:
- ✅ **PR validation** focuses on structural/syntax validation
- ✅ **Post-merge validation** focuses on functional validation
- ⚠️ **History functionality** requires main branch (by design)
- ✅ **Report generation** works on all branches (for review)

**Why History is Main-Only**:
- Centralized history (single source of truth)
- GitHub Pages only deploys from main
- Feature branch reports are for review, not production history

---

**Last Updated**: 2026-01-04  
**Document Location**: `docs/work/20260104_TEST_TRENDING_VALIDATION.md`

