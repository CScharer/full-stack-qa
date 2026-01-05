# Allure3 History Format Investigation

**Date Created**: 2026-01-05  
**Status**: 🔍 Critical Investigation  
**Pipelines**: #20705908291 (Run 1), #20706221320 (Run 2)  
**Issue**: Allure3 not creating history even with history files in RESULTS_DIR

---

## 🔍 Critical Findings

### Run 2 (Pipeline #20706221320) Results

**What Worked ✅**:
1. History downloaded successfully: 4 files, 16K
2. History files in `RESULTS_DIR/history/` before `allure generate`
3. Allure3 was told to merge history with new results

**What Didn't Work ❌**:
1. Allure3 did NOT create history directory in generated report
2. Log shows: `⚠️  WARNING: No history directory in generated report`
3. History files remain empty (just `[]`)

### The Problem

**Allure3 is NOT creating history even when:**
- History files exist in `RESULTS_DIR/history/` before generation
- History files are valid JSON (empty arrays `[]`)
- Allure3 is instructed to merge history

**This suggests:**
- Empty arrays `[]` might not be valid history format for Allure3
- Allure3 might require actual history entries with specific structure
- Allure3 might need `historyId` in test results to match history entries

---

## 🎯 Root Cause Hypothesis

### Hypothesis 1: Empty Arrays Are Not Valid History

**Problem**: Empty arrays `[]` might not be recognized as valid history by Allure3.

**Evidence**:
- History files with `[]` are downloaded and placed in RESULTS_DIR
- Allure3 sees the files but doesn't process them
- No history directory created in REPORT_DIR

**Solution**: Create minimal valid history entries instead of empty arrays.

### Hypothesis 2: Allure3 Needs Actual History Entries

**Problem**: Allure3 might require actual history entries with:
- Test UIDs (`historyId`)
- Build numbers
- Execution timestamps
- Test status (passed/failed)

**Evidence**:
- Empty arrays don't trigger history creation
- Allure3 might need data to merge with

**Solution**: Create minimal valid history entry structure.

### Hypothesis 3: HistoryId Mismatch

**Problem**: Allure3 might need `historyId` in test results to match history entries.

**Evidence**:
- History files exist but Allure3 doesn't process them
- Might be a matching issue between test results and history

**Solution**: Ensure test results have `historyId` that matches history entries.

---

## 💡 Proposed Solution

### Create Minimal Valid History Entry Structure

Instead of empty arrays `[]`, create minimal valid history entries:

**history-trend.json**:
```json
[
  {
    "buildOrder": 1,
    "reportUrl": "",
    "reportName": "Allure Report",
    "data": []
  }
]
```

**duration-trend.json**:
```json
[
  {
    "buildOrder": 1,
    "data": []
  }
]
```

**retry-trend.json**:
```json
[
  {
    "buildOrder": 1,
    "data": []
  }
]
```

This provides a valid structure that Allure3 can recognize and merge with new results.

---

## 🔧 Implementation Plan

### Step 1: Research Allure3 History Format

- Check Allure3 source code or documentation
- Find examples of valid history file structures
- Understand required fields and format

### Step 2: Create Minimal Valid History Entries

- Update `generate-combined-allure-report.sh` to create minimal valid entries
- Include build number from `executor.json`
- Ensure structure matches Allure3 expectations

### Step 3: Test

- Run pipeline with minimal valid history entries
- Verify Allure3 processes them
- Check if history is created in REPORT_DIR

---

## ⚠️ Current Status

**Pipeline #20706221320 (Run 2)**:
- ✅ History downloaded (4 files, 16K)
- ✅ History in RESULTS_DIR before generation
- ❌ Allure3 didn't create history in REPORT_DIR
- ❌ History files still empty

**Next Steps**:
1. Research Allure3 history file format
2. Create minimal valid history entry structure
3. Update script to use valid format
4. Test in next pipeline run

---

**Last Updated**: 2026-01-05  
**Document Location**: `docs/work/20260105_ALLURE_HISTORY_FORMAT_INVESTIGATION.md`

