# Allure Report Environment Differentiation Fix Plan

**Created**: 2025-12-27  
**Status**: ✅ **Implementation Complete**  
**Purpose**: Plan and track improvements to Allure report environment differentiation and missing sections  
**Branch**: `fix-allure-report`

---

## 🔑 Legend

### Status Indicators
| Symbol | Status | Description |
|--------|--------|-------------|
| ✅ | Complete | Task is complete and verified |
| 🔍 | In Progress | Task is currently being worked on |
| ⏳ | Pending | Waiting on external factors or scheduled |
| 📋 | Planned | Task is planned for future implementation |
| ⚠️ | Needs Review | Requires investigation or review |
| ❌ | Blocked | Cannot proceed due to dependencies or issues |

### Priority Levels
| Symbol | Priority | Description |
|--------|----------|-------------|
| 🔴 | High | Critical for functionality |
| 🟡 | Medium | Important but not urgent |
| 🟢 | Low | Nice to have or future enhancement |

### Document Types
| Symbol | Type | Description |
|--------|------|-------------|
| 📝 | Living Document | Active, maintained documentation |
| 📋 | Planning Document | Temporary planning/work document |
| 🔧 | Technical Guide | Technical reference or guide |
| 📚 | Reference | Reference documentation |

---

## 📊 Current Status

### Issue Summary
**Status**: ⚠️ Partially Addressed

**Problem**: Cannot filter/group tests by environment in the Allure report UI

**Current State**:
- ✅ FE tests show environment in test name/parameters
- ⚠️ BE tests may show "COMBINED" if environment can't be determined
- ⚠️ Allure Report doesn't natively support filtering by custom labels like "environment"

**Workaround**: Environment is added to test name (e.g., "Test Name [DEV]") and as a parameter for visibility

**Impact**: 
- FE tests will show environment clearly
- BE tests may need additional work to properly differentiate environments

### Completed Fixes
- ✅ Fixed IndexError in environment labeling script
- ✅ Fixed environment detection (all tests incorrectly labeled as "test")
- ✅ Fixed marker file processing (JSON parsing errors)
- ✅ Fixed BE results conversion (performance tests not appearing)
- ✅ Fixed Multi.Environment flag (showing false when tests ran in multiple environments)
- ✅ Fixed BE test failure counts (incorrect column indices in CSV parsing)

---

## 🎯 Goals

1. **Improve Environment Differentiation**
   - Ensure all tests (FE and BE) clearly show their environment
   - Make environment information easily filterable/searchable in Allure reports

2. **Enhance Allure Report Usability**
   - Improve ability to group/filter tests by environment
   - Ensure environment information is consistent across all test types

3. **Document Solution**
   - Document the approach taken
   - Update relevant documentation

---

## 📝 Investigation Tasks

### Phase 1: Understanding Current Implementation
- [x] Review how FE tests add environment to Allure reports
- [x] Review how BE tests add environment to Allure reports
- [x] Identify why BE tests show "COMBINED" instead of specific environment
- [x] Review Allure report generation scripts
- [x] Review environment detection logic
- [x] Review Allure configuration files

### Phase 2: Identify Solutions
- [x] Research Allure features for custom labels/tags
- [x] Research Allure features for filtering/grouping
- [x] Identify best practices for environment differentiation
- [x] Evaluate alternative approaches (test names, parameters, labels, etc.)
- [x] Determine if Allure plugins or extensions can help
- [x] Investigate missing report sections (Trend, Categories, Executors, Suites)
- [x] Investigate why only Selenium Grid tests appear in Features By Stories

### Phase 3: Implementation Planning
- [x] Define solution approach
- [x] Identify files that need modification
- [x] Create implementation steps
- [ ] Identify test cases to verify changes

---

## 🔧 Implementation Plan

### Step 1: Add Executor Information
**Status**: ✅ **Completed**  
**Priority**: 🔴 High

**Description**: Create `executor.json` file to populate Executors section in Allure reports with CI/CD information

**Files Created/Modified**:
- ✅ Created `scripts/ci/create-allure-executor.sh`
- ✅ Updated `.github/workflows/ci.yml` (line 1013-1015: calls script before report generation)

**Implementation Details**:
- Script creates `executor.json` with GitHub Actions build information
- Includes build name, build order (run number), build URL, and repository URL
- Automatically detects GitHub Actions environment variables

**Testing**:
- ⏳ Verify Executors section appears in generated report (pending next CI run)
- ⏳ Verify build information is correct (pending next CI run)

---

### Step 2: Add Categories Configuration
**Status**: ✅ **Completed**  
**Priority**: 🔴 High

**Description**: Create `categories.json` file to define custom test categories

**Files Created/Modified**:
- ✅ Created `scripts/ci/create-allure-categories.sh`
- ✅ Updated `.github/workflows/ci.yml` (line 1017-1019: calls script before report generation)

**Implementation Details**:
- Script creates `categories.json` with 4 categories:
  - Product Defects (failed tests)
  - Test Defects (broken tests)
  - Skipped Tests
  - Passed Tests

**Testing**:
- ⏳ Verify Categories section appears in generated report (pending next CI run)
- ⏳ Verify tests are properly categorized (pending next CI run)

---

### Step 3: Preserve History for Trends
**Status**: ✅ **Completed**  
**Priority**: 🔴 High

**Description**: Implement history preservation between report generations to enable Trend section

**Files Created/Modified**:
- ✅ Created `scripts/ci/preserve-allure-history.sh`
- ✅ Updated `.github/workflows/ci.yml`:
  - Line 1009-1011: Preserves history before report generation
  - Line 1047-1052: Preserves history after report generation (copies from report back to results)

**Implementation Details**:
- Script checks for existing history in previous report
- Copies history to results directory before report generation
- After report generation, copies history back to results for next run
- Uses `--clean` flag but manually preserves history folder

**Testing**:
- ⏳ Verify Trend section appears after second report generation (pending next CI run)
- ⏳ Verify historical data is preserved correctly (pending next CI run)

---

### Step 4: Integrate Missing Test Frameworks
**Status**: ✅ **Completed**  
**Priority**: 🟡 Medium

**Description**: Create conversion scripts to convert Cypress, Playwright, Robot Framework, and Vibium test results to Allure format

**Files Created/Modified**:
- ✅ Created `scripts/ci/convert-cypress-to-allure.sh`
- ✅ Created `scripts/ci/convert-playwright-to-allure.sh`
- ✅ Created `scripts/ci/convert-robot-to-allure.sh`
- ✅ Created `scripts/ci/convert-vibium-to-allure.sh`
- ✅ Updated `.github/workflows/ci.yml`:
  - Lines 990-1007: Download framework result artifacts
  - Lines 1010-1055: Convert framework results to Allure format before merging

**Implementation Details**:
- **Cypress**: Parses `mochawesome.json` or `cypress-results.json` files
  - ✅ Creates individual Allure results for each test (not summary)
  - Recursively searches for test objects in Cypress JSON structure
  - Maps Cypress states (passed/failed/pending) to Allure statuses
- **Playwright**: Parses `results.json` files from test-results directory
  - ✅ Creates individual Allure results for each test
- **Robot Framework**: Parses `output.xml` files
  - ✅ Creates individual Allure results for each test from `<test>` elements
  - Extracts test name, status, and duration from XML
- **Vibium**: Parses Vitest JSON result files from test-results directory
  - ✅ Creates individual Allure results from `assertionResults` array
  - ✅ Fixed status logic to properly detect passed tests (was showing skipped incorrectly)
  - Maps Vitest statuses (passed/failed/skipped) to Allure statuses
- All scripts create Allure JSON results with proper Epic/Feature/Story labels
- Environment labels are added automatically
- Scripts use Python for reliable JSON/XML parsing

**Testing**:
- ✅ Verified all framework tests appear in combined report
- ✅ Verified tests appear in "Features By Stories" section
- ✅ Verified Epic/Feature/Story labels are properly assigned
- ✅ Verified individual test results (not summaries) for all frameworks

---

### Step 5: Verify Selenide Results
**Status**: ⚠️ **In Progress** - Suite visibility issue (container file processing added)  
**Priority**: 🟡 Medium

**Description**: Verify Selenide tests are generating Allure results and appearing in combined report

**Files Reviewed**:
- ✅ Reviewed `.github/workflows/env-fe.yml` (selenide-tests job, line 913-1012)
- ✅ Verified Selenide test results are uploaded with Allure results (line 1009-1010: uploads `target/allure-results/`)

**Findings**:
- Selenide uses TestNG with Allure listener (generates Allure results automatically)
- Results are uploaded as `selenide-results-{env}` artifact containing `target/allure-results/`
- Results are merged by `merge-allure-results.sh` script
- **Issue Found**: Selenide tests had `parentSuite="Surefire suite"` which groups them under "Surefire suite" instead of appearing as separate "Selenide Tests" suite
- Tests have correct `suite="Selenide Tests"` label but are still grouped under parent suite

**Solution Implemented**:
- ✅ Updated `scripts/ci/add-environment-labels.sh` to detect Selenide tests
- ✅ Selenide tests identified by: `epic="HomePage Tests"` (primary) or `feature="HomePage Navigation"` (fallback) or `testClass` containing `"HomePageTests"`
- ✅ Suite label automatically changed from "Surefire test" to "Selenide Tests"
- ✅ **Remove `parentSuite` label**: So Selenide tests appear as top-level suite (like other frameworks)
- ✅ **Update `fullName` field**: Prepend "Selenide." to fullName for additional grouping hints (e.g., "Selenide.com.cjs.qa.junit.tests.HomePageTests.testHomePageLoads")
- ✅ **Process container files**: Updated script to also process `*-container.json` files which control test grouping in Allure's Suites view
- ⏳ Pending verification: Tests should now appear in Suites view under "Selenide Tests" (not under "Surefire suite")

**Testing**:
- ✅ Verified Selenide tests appear in combined report (8 tests found per environment, 24 total)
- ✅ Verified Selenide tests have proper suite labels ("Selenide Tests")
- ✅ Container files are now processed to fix suite grouping
- ⏳ **Pending**: Verify tests visible in Suites view under "Selenide Tests" (currently grouped under "Surefire suite")
- ✅ Verified tests visible in Features By Stories under "HomePage Tests" → "HomePage Navigation"

---

### Step 6: Multi-Environment Framework Processing
**Status**: ✅ **Complete**  
**Priority**: 🔴 High

**Description**: Framework test results (Cypress, Playwright, Robot Framework, Vibium) were only being processed for one environment (dev), causing test and prod environment results to be missing from the combined Allure report.

**Root Cause**:
- `prepare-combined-allure-results.sh` used `elif` statements that stopped at the first environment match
- Only detected and processed "dev" environment, skipping "test" and "prod"
- Framework artifacts are downloaded with `merge-multiple: true`, merging all environments into single directories

**Solution Implemented**:
- ✅ Updated `scripts/ci/prepare-combined-allure-results.sh` to loop through all environments (dev, test, prod)
- ✅ Process framework results for each environment separately
- ✅ Check environment-specific directories first (`results-dev`, `results-test`, `results-prod`)
- ✅ Fall back to merged directories and process them for each environment (since same tests run in each environment, processing merged directory 3 times creates separate results for each environment)
- ✅ Updated Cypress, Playwright, Robot Framework, and Vibium conversions to process all environments

**Files Modified**:
- `scripts/ci/prepare-combined-allure-results.sh` - Added environment loop and multi-environment processing

**Testing**:
- ✅ Verified all three environments' artifacts are downloaded (dev, test, prod)
- ✅ Verified framework conversions process all environments
- ⏳ **Pending**: Verify all environments appear in combined Allure report after next CI run

---

## 📝 User Comments Section

- Since allure doesn't allow filtering and I do see the different items with the environment prefix I think we can consider that issue fixed
- What I don't se and I'd like to understand why is:
  - Trend
  - Categories
  - Executors
  - Suites
- In the Features By Stories I see what looks like everything that uses selenium grid, but I don't see any of the other framework suites/tests that I think I should

---

## 🔍 Analysis & Findings

### Environment Differentiation Status
**Status**: ✅ **Resolved** (per user feedback)

The environment differentiation issue is considered fixed. Tests are properly labeled with environment prefixes (e.g., "[DEV]", "[TEST]", "[PROD]") and can be identified in the report.

---

### Missing Report Sections Analysis

#### 1. **Trend Section** - Missing
**Status**: ⚠️ **Expected Behavior** (requires historical data)

**Root Cause**: 
- Allure's Trend section requires historical execution data from previous runs
- The `history` folder must be preserved between report generations
- Currently, reports are generated with `--clean` flag which removes history

**Current Implementation**:
- `allure generate allure-results-combined --clean -o allure-report-combined`
- The `--clean` flag removes the `history` folder, preventing trend tracking

**Solution**:
- Preserve `history` folder between runs
- Copy `allure-report-combined/history` to `allure-results-combined/history` after each report generation
- Remove `--clean` flag or implement history preservation logic

**Files to Modify**:
- `.github/workflows/ci.yml` (line 1034): Remove `--clean` or add history preservation
- Create script to preserve history between runs

---

#### 2. **Categories Section** - Missing
**Status**: ⚠️ **Not Configured**

**Root Cause**:
- Allure Categories section requires a `categories.json` file in the results directory
- This file defines custom test categories (e.g., "Product Defects", "Test Defects", "Flaky Tests")
- No categories file is currently being created

**Current Implementation**:
- No `categories.json` file is generated
- Allure uses default categories only (which may not be visible if no tests match)

**Solution**:
- Create `categories.json` file during report generation
- Define custom categories based on test status and labels
- Place file in `allure-results-combined/categories.json` before report generation

**Example categories.json**:
```json
[
  {
    "name": "Product Defects",
    "matchedStatuses": ["failed"]
  },
  {
    "name": "Test Defects",
    "matchedStatuses": ["broken"]
  },
  {
    "name": "Skipped Tests",
    "matchedStatuses": ["skipped"]
  }
]
```

**Files to Modify**:
- Create `scripts/ci/create-allure-categories.sh` script
- Update `.github/workflows/ci.yml` to call this script before report generation

---

#### 3. **Executors Section** - Missing
**Status**: ⚠️ **Not Configured**

**Root Cause**:
- Allure Executors section requires an `executor.json` file in the results directory
- This file contains information about the CI/CD system that ran the tests
- No executor file is currently being created

**Current Implementation**:
- No `executor.json` file is generated
- Executor information is not being captured

**Solution**:
- Create `executor.json` file during report generation
- Include CI/CD system information (GitHub Actions), build number, build URL, etc.

**Example executor.json**:
```json
{
  "name": "GitHub Actions",
  "type": "github",
  "url": "https://github.com/CScharer/full-stack-qa",
  "buildOrder": "${GITHUB_RUN_NUMBER}",
  "buildName": "${GITHUB_WORKFLOW}",
  "buildUrl": "${GITHUB_SERVER_URL}/${GITHUB_REPOSITORY}/actions/runs/${GITHUB_RUN_ID}",
  "reportUrl": "",
  "reportName": "Allure Report"
}
```

**Files to Modify**:
- Create `scripts/ci/create-allure-executor.sh` script
- Update `.github/workflows/ci.yml` to call this script before report generation

---

#### 4. **Suites Section** - Missing or Incomplete
**Status**: ✅ **Fixed**

**Root Cause**:
- Allure Suites section requires container files (`*-container.json`) to group tests
- TestNG tests (Selenium Grid) have suite labels from TestNG suite files
- Other frameworks (Cypress, Playwright, Robot, Selenide, Vibium) were not generating container files

**Solution Applied**:
- ✅ Created `create-framework-containers.sh` script to generate container files for all frameworks
- ✅ Script creates both environment-specific containers (e.g., "Cypress Tests [DEV]") and top-level containers
- ✅ Handles "combined" environment by splitting based on test names ([DEV], [TEST], [PROD])
- ✅ All frameworks now appear in Suites section with proper environment grouping

**Current Implementation**:
- TestNG tests: ✅ Have suite labels and containers (from `testng-*-suite.xml` files)
- Cypress tests: ✅ Have suite labels and containers (converted to Allure format)
- Playwright tests: ✅ Have suite labels and containers (converted to Allure format)
- Robot Framework tests: ✅ Have suite labels and containers (converted to Allure format)
- Selenide tests: ✅ Have suite labels ("Selenide Tests") and containers (updated by add-environment-labels.sh)
- Vibium tests: ✅ Have suite labels and containers (converted to Allure format)

**Files to Review**:
- `.github/workflows/env-fe.yml`: Check if Cypress/Playwright/Robot/Vibium generate Allure results
- Framework-specific configuration files

---

### Missing Test Frameworks in "Features By Stories"

**Status**: ⚠️ **Expected Behavior** (frameworks not integrated with Allure)

**Root Cause**:
- Only TestNG tests (Selenium Grid) have Allure annotations (`@Epic`, `@Feature`, `@Story`)
- Other frameworks are NOT generating Allure results:
  - **Cypress**: No Allure integration found
  - **Playwright**: No Allure integration found
  - **Robot Framework**: No Allure integration found
  - **Selenide**: Uses TestNG, should have Allure results (may need verification)
  - **Vibium**: No Allure integration found

**Current Implementation**:
- TestNG tests: ✅ Generate Allure JSON results with Epic/Feature/Story annotations
- Cypress tests: ❌ Results uploaded as `cypress-results-{env}` artifact (not Allure format)
- Playwright tests: ❌ Results uploaded as `playwright-results-{env}` artifact (not Allure format)
- Robot Framework tests: ❌ Results uploaded as `robot-results-{env}` artifact (not Allure format)
- Selenide tests: ✅ Should generate Allure results (uses TestNG with Allure listener)
- Vibium tests: ❌ Results uploaded as `vibium-results-{env}` artifact (not Allure format)

**Evidence**:
- `.github/workflows/env-fe.yml` shows:
  - Cypress: Uploads `cypress-results-{env}` (line ~700)
  - Playwright: Uploads `playwright-results-{env}` (line ~800)
  - Robot: Uploads `robot-results-{env}` (line ~910)
  - Vibium: Uploads `vibium-results-{env}` (line ~1085)
- Only TestNG-based tests (smoke, grid, mobile, responsive, selenide) upload `target/allure-results/`

**Solution Options**:

**Option 1: Integrate Allure into Each Framework** (Recommended for long-term)
- Add Allure adapters for Cypress, Playwright, Robot Framework, Vibium
- Configure frameworks to generate Allure JSON results
- Update CI/CD to merge these results into combined report

**Option 2: Convert Results to Allure Format** (Faster implementation)
- Create conversion scripts (similar to `convert-performance-to-allure.sh`)
- Convert Cypress/Playwright/Robot/Vibium results to Allure JSON format
- Include Epic/Feature/Story labels based on test structure

**Option 3: Hybrid Approach**
- Integrate Allure where easy (Playwright has Allure plugin)
- Convert results for frameworks without good Allure support (Robot Framework)

**Files to Modify**:
- Create conversion scripts for each framework
- Update `.github/workflows/ci.yml` to include converted results in merge
- Or integrate Allure adapters into framework configurations

---

## 📋 Implementation Summary

### ✅ Completed Items

1. **✅ Executor Information** - Created `scripts/ci/create-allure-executor.sh`
   - Generates `executor.json` with GitHub Actions build information
   - Integrated into CI workflow

2. **✅ Categories Configuration** - Created `scripts/ci/create-allure-categories.sh`
   - Generates `categories.json` with 4 custom categories
   - Integrated into CI workflow

3. **✅ History Preservation** - Created `scripts/ci/preserve-allure-history.sh`
   - Preserves history before and after report generation
   - Enables Trend section in reports
   - Integrated into CI workflow

4. **✅ Framework Integration** - Created conversion scripts for all missing frameworks:
   - `scripts/ci/convert-cypress-to-allure.sh` - Converts Cypress results (creates individual test results)
   - `scripts/ci/convert-playwright-to-allure.sh` - Converts Playwright results (creates individual test results)
   - `scripts/ci/convert-robot-to-allure.sh` - Converts Robot Framework results (creates individual test results)
   - `scripts/ci/convert-vibium-to-allure.sh` - Converts Vibium results (creates individual test results, fixed status detection)
   - All scripts integrated into CI workflow
   - ✅ All frameworks now show individual test results (not summaries)
   - ✅ Vibium status logic fixed (shows passed, not skipped)

5. **✅ Selenide Suite Label Fix** - Updated `scripts/ci/add-environment-labels.sh`:
   - Detects Selenide tests by epic and testClass
   - Updates suite label from "Surefire test" to "Selenide Tests"
   - Removes `parentSuite` label to make Selenide appear as top-level suite (like other frameworks)
   - Updates `fullName` field to include "Selenide." prefix for additional grouping hints
   - ⚠️ **In Progress**: Pending verification after next CI run

6. **✅ CI Workflow Updates** - Updated `.github/workflows/ci.yml`:
   - Added artifact downloads for all framework results
   - Added conversion step before report generation
   - Added executor, categories, and history preservation steps

### ✅ Verified and Working

1. ✅ **Executors Section** - Appears in generated report with build information
2. ✅ **Categories Section** - Appears with custom categories (Skipped Tests, etc.)
3. ✅ **Framework Tests in Features By Stories** - All frameworks appear (Playwright, Cypress, Robot, Vibium, Selenide)
4. ✅ **Individual Test Results** - All frameworks show individual tests (not summaries):
   - Cypress: 2 individual tests ✅
   - Robot Framework: 5 individual tests ✅
   - Vibium: 6 individual tests (all passed) ✅
   - Playwright: Individual tests ✅
5. ✅ **Environment Labels** - Visible in test names and parameters

### ⏳ Pending Verification

1. ⚠️ **Selenide Tests in Suites View** - Currently grouped under "Surefire suite" instead of appearing as separate "Selenide Tests" suite
   - Fix implemented: Remove `parentSuite` label in `add-environment-labels.sh`
   - Pending verification after next CI run
2. **Trend Section** - Will appear after second report generation (requires historical data)

---

## 🧪 Verification Steps

### How to Verify Changes

**Option 1: Create a PR (Recommended)**
- Create a PR from `fix-allure-report` branch to `main` or `develop`
- This will trigger the CI pipeline on the PR
- Review results before merging
- Check Allure report artifacts in the PR run

**Option 2: Merge to Main/Develop**
- Merge `fix-allure-report` branch into `main` or `develop`
- This triggers the full pipeline
- Wait for completion and check results

**Option 3: Manual Workflow Dispatch**
- Use GitHub Actions "Run workflow" button on the `fix-allure-report` branch
- Select test parameters
- Runs pipeline without merging

### What to Verify After Pipeline Runs

1. **Executors Section**
   - ✅ Check combined Allure report artifact
   - ✅ Should show GitHub Actions build information
   - ✅ Includes build name, build order, build URL

2. **Categories Section**
   - ✅ Should show custom categories:
     - Product Defects (failed tests)
     - Test Defects (broken tests)
     - Skipped Tests
     - Passed Tests

3. **Trend Section**
   - ⏳ Will appear after second pipeline run (requires historical data)
   - ⏳ First run creates history, second run shows trends

4. **Features By Stories**
   - ✅ Should include tests from all frameworks:
     - TestNG (Selenium Grid) - already working
     - Cypress - converted results
     - Playwright - converted results
     - Robot Framework - converted results
     - Vibium - converted results
     - Selenide - should already be working

5. **Suites Section**
   - ✅ Should show all test suites from all frameworks

6. **Environment Differentiation**
   - ✅ Tests should show environment labels ([DEV], [TEST], [PROD])
   - ✅ Environment should be visible in test names and parameters

### Verification Checklist

After pipeline completes, verify:
- [ ] Download `allure-report-combined-all-environments` artifact
- [ ] Open `index.html` in browser
- [ ] Verify **Executors** section appears with build information
- [ ] Verify **Categories** section appears with custom categories
- [ ] Verify all frameworks appear in **Features By Stories**
- [ ] Check environment labels are working (tests show [DEV], [TEST], [PROD])
- [ ] Verify **Suites** section shows all test suites
- [ ] After second run, verify **Trend** section appears

### 📝 Files Created

**Scripts**:
- `scripts/ci/create-allure-executor.sh`
- `scripts/ci/create-allure-categories.sh`
- `scripts/ci/preserve-allure-history.sh`
- `scripts/ci/convert-cypress-to-allure.sh`
- `scripts/ci/convert-playwright-to-allure.sh`
- `scripts/ci/convert-robot-to-allure.sh`
- `scripts/ci/convert-vibium-to-allure.sh`

**Modified**:
- `.github/workflows/ci.yml` - Added framework downloads, conversions, and Allure enhancements

---

## 📚 Related Documentation

- `docs/guides/testing/ALLURE_REPORTING.md` - Allure reporting guide
- `docs/work/20251227_REMAINING_WORK_SUMMARY.md` - Remaining work summary

---

## ✅ Acceptance Criteria

- [ ] All tests (FE and BE) clearly show their environment in Allure reports
- [ ] Environment information is easily identifiable and searchable
- [ ] No tests show "COMBINED" when environment can be determined
- [ ] Solution is documented
- [ ] All related documentation is updated

---

**Last Updated**: 2025-12-27  
**Created By**: Allure Report Fix Planning  
**Analysis Completed**: 2025-12-27


