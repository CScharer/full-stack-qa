# Allure3 History Investigation - Source Code & Documentation Research

**Date**: 2026-01-07  
**Purpose**: Investigate Allure3 source code and documentation for undocumented requirements  
**MERGE_NUMBER**: 48

---

## 🔍 Key Findings from Documentation & Source Research

### 1. **Critical Discovery: History Directory Copy Workflow**

**Finding**: Allure3 requires a specific workflow for history management:

1. **After generating a report**: History is created in `allure-report/history/`
2. **Before next test run**: Copy `allure-report/history/` → `allure-results/history/`
3. **Generate new report**: Allure3 will merge new results with existing history

**Source**: Multiple documentation sources and community discussions confirm this is the standard workflow.

**Current Implementation Status**: ✅ We ARE doing this correctly
- Line 222 in `generate-combined-allure-report.sh`: `cp -r "$REPORT_DIR/history"/* "$RESULTS_DIR/history/"`
- We copy history from report back to results directory after generation

### 2. **Configuration File Format**

**Finding**: Allure3 supports configuration via `allurerc.mjs` (ES modules) or `allure.config.js` (CommonJS)

**Example Configuration**:
```javascript
import { defineConfig } from "allure";

export default defineConfig({
  historyPath: "./.allure/history.jsonl",
  appendHistory: true,
});
```

**Key Differences from Our Implementation**:
- Official docs show `historyPath` pointing to a **file** (`history.jsonl`), not a directory
- Our config uses `historyPath: "./history"` (directory)
- Official docs use `defineConfig` helper function

**Current Implementation**: 
- ✅ We have `allure.config.js` and `allure.config.ts`
- ⚠️ We use directory path, not file path
- ⚠️ We don't use `defineConfig` helper

### 3. **History File Format**

**Finding**: Allure3 may use `history.jsonl` (JSON Lines format) instead of separate JSON files

**Source**: Official documentation mentions `history.jsonl` file format

**Current Implementation**:
- We're using separate JSON files (`history-trend.json`, `duration-trend.json`, etc.)
- This might be the issue - Allure3 may expect a single `history.jsonl` file

### 4. **Known Issues**

**Finding**: GitHub Discussion #2127 mentions history not working in single-file report mode

**Workaround**: Run report generation twice and copy history data accordingly

**Current Implementation**:
- We're not using single-file mode
- But this suggests there may be other known issues

### 5. **Test Identifier Requirements**

**Finding**: Allure3 requires consistent `historyId` (unique test identifier) across runs

**Current Implementation**: ✅ We have `historyId` fields in our test results

### 6. **Configuration File Location**

**Finding**: Configuration file should be in the project root or specified via `--config` flag

**Current Implementation**: ✅ Config files are in project root, and we use `--config` flag

---

## 🎯 Critical Insights

### Insight 1: History Path Format
- **Official docs show**: `historyPath: "./.allure/history.jsonl"` (file path)
- **Our implementation**: `historyPath: "./history"` (directory path)
- **Potential Issue**: Allure3 may expect a file path, not a directory path

### Insight 2: History File Format
- **Official docs mention**: `history.jsonl` (JSON Lines format - single file)
- **Our implementation**: Multiple JSON files (`history-trend.json`, `duration-trend.json`, etc.)
- **Potential Issue**: Allure3 may not recognize our multi-file history format

### Insight 3: Configuration Helper
- **Official docs use**: `defineConfig()` helper function
- **Our implementation**: Plain object export
- **Potential Issue**: May not be critical, but could affect validation

### Insight 4: Manual History Creation
- **Documentation confirms**: Allure3 does NOT automatically handle history
- **Requirement**: Manual copying of history directory is required
- **Our implementation**: ✅ We're doing this correctly

---

## 🔧 Recommended Fixes

### Fix 1: Try `history.jsonl` File Format
Instead of multiple JSON files, try creating a single `history.jsonl` file in JSON Lines format.

### Fix 2: Update Configuration to Use File Path
Change `historyPath` from directory (`"./history"`) to file path (`"./history/history.jsonl"`).

### Fix 3: Use `defineConfig` Helper
Update configuration to use the official `defineConfig` helper function.

### Fix 4: Verify History File Location
Ensure history file is in the exact location specified by `historyPath` before `allure generate` runs.

---

## 📚 Sources

1. **Official Allure3 Documentation**: https://allurereport.org/docs/v3/configure/
2. **History and Retries Guide**: https://allurereport.org/docs/history-and-retries/
3. **How It Works - History Files**: https://allurereport.org/docs/how-it-works-history-files/
4. **GitHub Discussions**: https://github.com/orgs/allure-framework/discussions/2127
5. **Allure3 GitHub Repository**: https://github.com/allure-framework/allure3

---

## ✅ What We're Doing Correctly

1. ✅ Copying history from report back to results directory
2. ✅ Using configuration files (`allure.config.js` and `allure.config.ts`)
3. ✅ Using explicit `--config` flag
4. ✅ Ensuring test results have `historyId` fields
5. ✅ Preserving history across pipeline runs
6. ✅ Downloading history from GitHub Pages/artifacts

## ⚠️ Potential Issues

1. ⚠️ **History Path Format**: Using directory path instead of file path
2. ⚠️ **History File Format**: Using multiple JSON files instead of single `history.jsonl`
3. ⚠️ **Configuration Helper**: Not using `defineConfig()` helper
4. ⚠️ **File Location**: History file may not be in the exact location Allure3 expects

---

**Next Steps**: Try implementing Fix 1 and Fix 2 (history.jsonl format and file path configuration)

