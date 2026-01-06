# Allure3 History Root Cause Investigation

**Date Created**: 2026-01-05  
**Status**: 🔍 Critical Investigation  
**Pipelines Reviewed**: #20727244460 (PR #91), #20727996478 (PR #92), #20728568937 (PR #93)  
**Issue**: Allure3 not creating history after 3 runs with actual test results

---

## 📊 Current Status

### Pipeline Execution Summary
- ✅ **3 successful pipeline runs** with actual test execution
- ✅ **286 result files processed** in each run
- ✅ **Build orders**: 447, 449, 451 (incremental)
- ✅ **Test results include historyId** (confirmed in codebase)
- ✅ **executor.json created** with buildOrder for each run
- ❌ **Allure3 NOT creating history directory** in generated reports
- ❌ **Empty history files persist** in GitHub Pages (3 bytes each)

### What's Working
1. ✅ History download from GitHub Pages/artifacts
2. ✅ Empty history file detection and removal
3. ✅ Allure3 report generation (reports are created successfully)
4. ✅ Test results have `historyId` attributes
5. ✅ `executor.json` has `buildOrder` values

### What's NOT Working
1. ❌ Allure3 not creating `history/` directory in generated reports
2. ❌ History files remain empty in GitHub Pages
3. ❌ No trend data visible in Allure Reports

---

## 🔍 Root Cause Analysis

### Key Finding from Allure Documentation

According to Allure documentation:
> "Enabling the history-related features is a matter of copying the `history` subdirectory into the test results directory before generating the next test report."

**The Process Should Be**:
1. Generate report → Allure3 creates `REPORT_DIR/history/`
2. Copy `REPORT_DIR/history/` → `RESULTS_DIR/history/` for next run
3. Next run: Allure3 merges `RESULTS_DIR/history/` with new results
4. Allure3 creates updated `REPORT_DIR/history/` with merged data
5. Repeat cycle

### The Problem

**Allure3 is NOT creating `REPORT_DIR/history/` on the first run**, which breaks the cycle:

```
Run 1:
  1. No history in RESULTS_DIR ✅ (expected)
  2. Generate report → Allure3 creates report but NO history directory ❌
  3. Copy history → Nothing to copy (no history directory exists) ❌
  4. Upload history → Nothing to upload ❌

Run 2:
  1. Download history → Empty files (just []) ✅
  2. Remove empty files → History removed ✅
  3. Generate report → Allure3 still doesn't create history ❌
  4. Cycle continues... ❌
```

### Why Allure3 Isn't Creating History

Based on investigation and Allure documentation:

1. **Allure3 requires existing history to merge with** - It doesn't bootstrap history on first run
2. **Empty arrays `[]` are not recognized as valid history** - Allure3 ignores them
3. **Allure3 needs actual history entries** with test execution data to merge with new results
4. **History is created when Allure3 merges old history with new results** - Without old history, no new history is created

### The Chicken-and-Egg Problem

```
Problem: Allure3 needs history to create history
Solution: We need to bootstrap the first history entry
```

---

## 💡 Proposed Solution

### Understanding Allure3 History Format

Allure3 history files have a specific structure:
- `history/history-trend.json` - Array of build entries with test execution data
- `history/duration-trend.json` - Array of build entries with duration data
- `history/retry-trend.json` - Array of build entries with retry data
- `history/{md5-hash}.json` - Individual test history files (one per test)

### Solution: Bootstrap History from First Run

Instead of waiting for Allure3 to create history (which it won't do without existing history), we should:

1. **After first report generation**, if no history exists, create a bootstrap history entry from the current run's test results
2. **Extract test execution data** from the generated report or result files
3. **Create valid history structure** with actual test data from the first run
4. **Preserve this bootstrap history** for the next run
5. **Allure3 will then merge** this bootstrap history with new results in subsequent runs

### Implementation Approach

**Option 1: Extract History from Report Metadata** (Recommended)
- After `allure generate`, check if `REPORT_DIR/history/` exists
- If not, extract test execution data from result files
- Create minimal valid history entries with actual test data
- This provides Allure3 with valid history to merge with

**Option 2: Use Allure3's Internal History Generation**
- Research if Allure3 has a flag or option to force history creation
- Check if there's a way to tell Allure3 to create history even without existing history

**Option 3: Manual History Bootstrap**
- After first run, manually create history files with structure:
  ```json
  [
    {
      "buildOrder": 447,
      "reportUrl": "",
      "reportName": "Allure Report",
      "data": [
        {
          "uid": "test-uuid",
          "status": "passed",
          "time": {...}
        }
      ]
    }
  ]
  ```

---

## 🔧 Recommended Implementation

### Step 1: Create History Bootstrap Function

Create a function in `generate-combined-allure-report.sh` that:
1. Checks if `REPORT_DIR/history/` exists after `allure generate`
2. If not, extracts test execution data from result files
3. Creates valid history structure with actual test data
4. Saves to `REPORT_DIR/history/` so it can be preserved

### Step 2: Extract Test Data from Results

From `RESULTS_DIR/*-result.json` files, extract:
- `historyId` (already present)
- `status` (passed/failed/skipped)
- `start` and `stop` timestamps
- `fullName`

### Step 3: Create Valid History Structure

Create history files with format:
```json
[
  {
    "buildOrder": 447,
    "reportUrl": "",
    "reportName": "Allure Report",
    "data": [
      {
        "uid": "test-uuid-from-result",
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

### Step 4: Preserve Bootstrap History

After creating bootstrap history:
1. Copy `REPORT_DIR/history/` → `RESULTS_DIR/history/` for next run
2. Upload as artifact
3. Deploy to GitHub Pages

### Step 5: Verify in Next Run

In the next run:
1. Download history from GitHub Pages/artifact
2. Allure3 should merge bootstrap history with new results
3. Allure3 should create updated history with both runs' data
4. History should now be visible in Allure Reports

---

## 📋 Implementation Checklist

- [ ] Create function to extract test data from result files
- [ ] Create function to generate valid history structure
- [ ] Add bootstrap logic after `allure generate` if no history exists
- [ ] Test bootstrap history creation locally
- [ ] Verify bootstrap history structure is valid
- [ ] Update script to preserve bootstrap history
- [ ] Test in pipeline (should work on first run with bootstrap)
- [ ] Verify history appears in Allure Reports after second run

---

## ⚠️ Important Notes

1. **Bootstrap history must contain actual test data** - Empty arrays won't work
2. **History structure must match Allure3's expected format** - Need to verify exact format
3. **historyId must match** between result files and history entries
4. **buildOrder must be correct** - Should match executor.json

---

## 🔗 References

- [Allure History Documentation](https://allurereport.org/docs/history-and-retries/)
- Allure3 CLI source code (if available)
- Previous investigation documents:
  - `docs/work/20260105_ALLURE_HISTORY_FORMAT_INVESTIGATION.md`
  - `docs/work/20260105_ALLURE_HISTORY_FINAL_SOLUTION.md`
  - `docs/work/20260105_ALLURE_HISTORY_ROOT_CAUSE_ANALYSIS.md`

---

**Last Updated**: 2026-01-05  
**Next Steps**: Implement history bootstrap function

