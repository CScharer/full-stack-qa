# Allure Report Environment Differentiation Fix Plan

**Created**: 2025-12-27  
**Status**: 📋 Planning  
**Purpose**: Plan and track improvements to Allure report environment differentiation  
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
- [ ] Review how FE tests add environment to Allure reports
- [ ] Review how BE tests add environment to Allure reports
- [ ] Identify why BE tests show "COMBINED" instead of specific environment
- [ ] Review Allure report generation scripts
- [ ] Review environment detection logic
- [ ] Review Allure configuration files

### Phase 2: Identify Solutions
- [ ] Research Allure features for custom labels/tags
- [ ] Research Allure features for filtering/grouping
- [ ] Identify best practices for environment differentiation
- [ ] Evaluate alternative approaches (test names, parameters, labels, etc.)
- [ ] Determine if Allure plugins or extensions can help

### Phase 3: Implementation Planning
- [ ] Define solution approach
- [ ] Identify files that need modification
- [ ] Create implementation steps
- [ ] Identify test cases to verify changes

---

## 🔧 Implementation Plan

### Step 1: [To be determined after investigation]
**Status**: 📋 Planned  
**Priority**: 🔴 High

**Description**: [To be filled in]

**Files to Modify**:
- [ ] File 1
- [ ] File 2

**Testing**:
- [ ] Test case 1
- [ ] Test case 2

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


