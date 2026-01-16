# Folder Cleanup Analysis

**Date**: January 16, 2026  
**Status**: Working Document  
**Purpose**: Review folders with mixed case names to determine if they're still needed and what actions are required for renaming to lowercase or deletion

---

## Executive Summary

This document analyzes 17 folders to determine:
1. **Current Usage**: Are they still actively used?
2. **References**: Where are they referenced in code/config?
3. **Rename Impact**: What needs to be updated if renamed to lowercase
4. **Delete Impact**: What would break if deleted
5. **Recommendations**: Keep, rename, or delete

---

## Folder Analysis

### 1. `/Configurations` (Root Level)

**Current Status**: ✅ **ACTIVE - Move Contents to `/config`**

**Contents**:
- `Environments.xml` (gitignored - contains sensitive data)
- `Environments.xml.template` (safe to commit)
- `README.md`

**References Found**: 41 files
- Java test files (Environment classes)
- Configuration documentation
- `.gitignore` (line 77: `Configurations/Environments.xml`)

**Usage**:
- Contains environment configuration templates
- Used by Java test framework for environment setup
- Referenced in multiple environment classes

**Move contents to `/config/`**:
- ✅ Move `Environments.xml.template` → `config/Environments.xml.template`
- ✅ Move `README.md` → `config/Configurations_README.md` (or merge with `config/README.md`)
- ✅ Update `.gitignore` (line 77: `Configurations/Environments.xml` → `config/Environments.xml`)
- ✅ Update all Java references (41 files) to point to `config/` instead of `Configurations/`
- ✅ Update documentation references
- ✅ Delete empty `/Configurations` folder after move
- ⚠️ **Impact**: Medium - Many Java files reference this, but consolidation into existing `config/` makes sense

**Delete**: ✅ **AFTER MOVE** - Delete `/Configurations` folder after contents are moved

**Recommendation**: ✅ **MOVE CONTENTS to `/config/`** - Consolidate with existing config folder, update 41+ references, then delete empty folder

---

### 2. `/XML` (Root Level)

**Current Status**: ✅ **ACTIVE - Keep and Rename**

**Contents**:
- `Companies.xml` (gitignored - contains sensitive data)
- `Companies.xml.template` (safe to commit)
- `UserSettings.xml` (gitignored - contains sensitive data)
- `UserSettings.xml.template` (safe to commit)
- `JVMSettings.xml`
- `README.md`

**References Found**: 28 files
- Java utilities (`XML.java`, `Email.java`)
- Test files (API tests, SharePoint tests)
- Configuration files (pom.xml, Dockerfile)
- Documentation

**Usage**:
- XML configuration files for companies and user settings
- Used by Java test framework
- Referenced in utilities and test classes

**Rename to `xml/`**:
- ✅ Update all Java references (28 files)
- ✅ Update documentation references
- ✅ Update pom.xml if referenced
- ⚠️ **Impact**: Medium - Multiple Java files reference this

**Delete**: ❌ **NOT RECOMMENDED** - Actively used for XML configuration

**Recommendation**: ✅ **RENAME to `xml/`** - Update 28+ references

---

### 3. `/PRIVATE` (Root Level)

**Current Status**: ✅ **ACTIVE - Keep As Is**

**Contents**:
- `ANALYSIS.md`
- `ANALYSIS_PS_RESULTS.md`
- `ANALYSIS_SUGGESTIONS.md`
- `INTEGRATION_COMPLETE.md`
- `FIX_CRITICAL_ISSUES.md`
- `ITEMS_TO_ROTATE.md`
- `PRIVATE.xlsm` (Excel file)
- `~$PRIVATE.xlsm` (Excel temp file)

**References Found**: 8 files
- Documentation references (README.md, NAVIGATION.md)
- Security documentation
- Scripts

**Usage**:
- Contains sensitive/private documentation
- Referenced in documentation as "moved to PRIVATE/"
- In `.gitignore` (line 96: `PRIVATE/`)

**Rename**: ❌ **NOT RECOMMENDED** - Leave as-is per user request

**Delete**: ❌ **NOT RECOMMENDED** - Contains important private documentation

**Recommendation**: ✅ **LEAVE AS IS** - Do not rename or modify

---

### 4. `/Data` (Root Level)

**Current Status**: ✅ **ACTIVE - Keep and Rename**

**Contents**:
- `Core/` - Database files and scripts
- `Scripts/` - SQL scripts
- `Templates/` - HTML templates
- `SQL/` - Vivit SQL scripts
- Various batch files and data files

**References Found**: 27 files
- Backend configuration (`backend/app/config.py`)
- Shell scripts (service start scripts)
- Documentation
- Database configuration files

**Usage**:
- **Primary database location**: `Data/Core/` contains SQLite databases
- Referenced in `backend/app/config.py` for database paths
- Used by service startup scripts
- Referenced in `config/environments.json` for database directory

**Rename to `data/`**:
- ✅ Update `backend/app/config.py` (database path logic)
- ✅ Update `config/environments.json` (database.directory)
- ✅ Update shell scripts (service start scripts)
- ✅ Update documentation (27 files)
- ⚠️ **Impact**: **HIGH** - Core database location, critical for application

**Delete**: ❌ **NOT RECOMMENDED** - Contains databases and critical data

**Recommendation**: ✅ **RENAME to `data/`** - **HIGH PRIORITY** - Update database path references

---

### 5. `/Data/Core`

**Current Status**: ✅ **ACTIVE - Keep (Parent will be renamed)**

**Contents**:
- SQLite database files (`full_stack_qa_*.db`)
- `scripts/` - Database scripts
- `tests/` - Database tests
- `README.md`

**References Found**: Included in `/Data` references (27 files)

**Usage**:
- Contains all SQLite database files
- Referenced in `backend/app/config.py`
- Referenced in `config/environments.json`

**Rename to `data/core/`**:
- ✅ Will be handled when parent `/Data` is renamed to `data/`
- ✅ Update database path references
- ⚠️ **Impact**: **HIGH** - Database location

**Delete**: ❌ **NOT RECOMMENDED** - Contains databases

**Recommendation**: ✅ **RENAME to `data/core/`** - Part of `/Data` rename

---

### 6. `/Data/Scripts`

**Current Status**: ⚠️ **UNCERTAIN - Review Usage**

**Contents**:
- `MS Access - CScharer.sql`
- `MS Access - Vivit-Operating_Budget.sql`
- `SQLite - qadb.sql`

**References Found**: Included in `/Data` references

**Usage**:
- Legacy SQL scripts
- May be referenced in documentation or scripts
- In `.gitignore` for backup files

**Rename to `data/scripts/`**:
- ✅ Will be handled when parent `/Data` is renamed
- ⚠️ **Impact**: Low - Check for specific references

**Delete**: ⚠️ **REVIEW** - May be legacy, verify no active usage

**Recommendation**: ⚠️ **REVIEW USAGE** - If unused, can be deleted; otherwise rename to `data/scripts/`

---

### 7. `/Data/Templates`

**Current Status**: ⚠️ **UNCERTAIN - Review Usage**

**Contents**:
- HTML templates (`AutomationReport*.htm`)
- Text templates (`TemplateYMAPIClass.txt`, etc.)
- Logo source files

**References Found**: Included in `/Data` references

**Usage**:
- Template files for reports
- May be used by test frameworks or reporting tools
- Need to verify active usage

**Rename to `data/templates/`**:
- ✅ Will be handled when parent `/Data` is renamed
- ⚠️ **Impact**: Low - Check for specific references

**Delete**: ⚠️ **REVIEW** - Verify if templates are actively used

**Recommendation**: ⚠️ **REVIEW USAGE** - If unused, can be deleted; otherwise rename to `data/templates/`

---

### 8. `/Data/SQL`

**Current Status**: ⚠️ **UNCERTAIN - Review Usage**

**Contents**:
- Vivit SQL scripts (9 files)
- All scripts start with `Vivit_`

**References Found**: Included in `/Data` references

**Usage**:
- Vivit-specific SQL scripts
- May be legacy or used for specific test suites
- Need to verify active usage

**Rename to `data/sql/`**:
- ✅ Will be handled when parent `/Data` is renamed
- ⚠️ **Impact**: Low - Check for specific references

**Delete**: ⚠️ **REVIEW** - Verify if Vivit scripts are still needed

**Recommendation**: ⚠️ **REVIEW USAGE** - If unused, can be deleted; otherwise rename to `data/sql/`

---

### 9. `/src/test/resources/Drivers`

**Current Status**: ⚠️ **LEGACY - Likely Safe to Delete**

**Contents**:
- `ReadMe.txt` (42 bytes)

**References Found**: 19 files
- Java test files (Selenium, DBUnit, etc.)
- Documentation
- `.gitignore` (lines 103-111) - Ignores driver executables but keeps README

**Usage**:
- **Legacy**: Used to store WebDriver executables
- **Current**: WebDriverManager is used instead (no manual driver storage needed)
- Only contains a README file
- `.gitignore` explicitly ignores driver executables

**Rename to `src/test/resources/drivers/`**:
- ✅ Update `.gitignore` (lines 103-111)
- ✅ Update Java references (19 files)
- ⚠️ **Impact**: Low - Mostly legacy references

**Delete**: ✅ **RECOMMENDED** - Legacy directory, WebDriverManager handles drivers now

**Recommendation**: ✅ **DELETE** - Legacy, WebDriverManager replaces manual driver management

---

### 10. `/src/test/resources/DataSets`

**Current Status**: ✅ **ACTIVE - Keep and Rename**

**Contents**:
- `dataset.dtd` (empty)
- `FlatXML_DataSet.xml`
- `XlsDataSet.xls`
- `XML_DataSet.xml`

**References Found**: 19 files
- Java test files (DBUnit tests, DataSet utilities)
- Test classes use these files for data-driven testing

**Usage**:
- Used by DBUnit framework for test data
- Referenced in `BaseDBUnitTestForJPADao.java`
- Referenced in `DataSetUtilDemoTests.java`
- Active test data files

**Rename to `src/test/resources/datasets/`**:
- ✅ Update Java references (19 files)
- ✅ Update test classes
- ⚠️ **Impact**: Medium - Active test data files

**Delete**: ❌ **NOT RECOMMENDED** - Active test data files

**Recommendation**: ✅ **RENAME to `datasets/`** - Update 19+ references

---

### 11. `/src/test/resources/TableDef`

**Current Status**: ⚠️ **UNCERTAIN - Review Usage**

**Contents**:
- `b2csite.dll.sql` (empty file, 0 bytes)

**References Found**: Included in general "TableDef" searches

**Usage**:
- Contains empty SQL file
- May be legacy or placeholder
- Need to verify if referenced in code

**Rename to `src/test/resources/tabledef/`**:
- ✅ Update any references
- ⚠️ **Impact**: Low - Empty file, likely unused

**Delete**: ⚠️ **REVIEW** - Empty file, likely safe to delete if unused

**Recommendation**: ⚠️ **REVIEW USAGE** - If empty and unused, delete; otherwise rename to `tabledef/`

---

### 12. `/target/test-classes/Drivers`

**Current Status**: ❌ **BUILD OUTPUT - Ignore**

**Contents**: Build output directory (Maven target)

**Usage**:
- Maven build output directory
- Automatically generated
- In `.gitignore` (line 56: `/target/`)

**Rename**: ❌ **NOT APPLICABLE** - Build output, ignored by git

**Delete**: ❌ **NOT APPLICABLE** - Will be regenerated by Maven

**Recommendation**: ✅ **IGNORE** - Build output, already in `.gitignore`

---

### 13. `/target/test-classes/DataSets`

**Current Status**: ❌ **BUILD OUTPUT - Ignore**

**Contents**: Build output directory (Maven target)

**Usage**:
- Maven build output directory
- Automatically generated from `src/test/resources/DataSets`
- In `.gitignore` (line 56: `/target/`)

**Rename**: ❌ **NOT APPLICABLE** - Build output, ignored by git

**Delete**: ❌ **NOT APPLICABLE** - Will be regenerated by Maven

**Recommendation**: ✅ **IGNORE** - Build output, automatically generated

---

### 14. `/target/test-classes/TableDef`

**Current Status**: ❌ **BUILD OUTPUT - Ignore**

**Contents**: Build output directory (Maven target)

**Usage**:
- Maven build output directory
- Automatically generated from `src/test/resources/TableDef`
- In `.gitignore` (line 56: `/target/`)

**Rename**: ❌ **NOT APPLICABLE** - Build output, ignored by git

**Delete**: ❌ **NOT APPLICABLE** - Will be regenerated by Maven

**Recommendation**: ✅ **IGNORE** - Build output, automatically generated

---

### 15. `/scripts/tests/Data`

**Current Status**: ⚠️ **EMPTY - Review**

**Contents**: Empty directory (only contains `Core/` subdirectory)

**Usage**:
- Empty parent directory
- Only contains `Core/` subdirectory (also empty)

**Rename to `scripts/tests/data/`**:
- ⚠️ **Impact**: None - Empty directory

**Delete**: ✅ **RECOMMENDED** - Empty directory, no content

**Recommendation**: ✅ **DELETE** - Empty directory

---

### 16. `/scripts/tests/Data/Core`

**Current Status**: ⚠️ **EMPTY - Review**

**Contents**: Empty directory

**Usage**:
- Empty directory
- No files or subdirectories

**Rename**: ❌ **NOT APPLICABLE** - Empty

**Delete**: ✅ **RECOMMENDED** - Empty directory

**Recommendation**: ✅ **DELETE** - Empty directory (part of `/scripts/tests/Data`)

---

### 17. `/.github/ISSUE_TEMPLATE`

**Current Status**: ✅ **ACTIVE - Keep As Is**

**Contents**:
- `bug_report.md`
- `config.yml`
- `feature_request.md`
- `test_failure.md`

**References Found**: GitHub automatically uses this directory

**Usage**:
- GitHub issue templates
- Standard GitHub directory name (case-sensitive)
- Used by GitHub UI for issue creation

**Rename**: ❌ **NOT RECOMMENDED** - Leave as-is per user request

**Delete**: ❌ **NOT RECOMMENDED** - GitHub issue templates

**Recommendation**: ✅ **LEAVE AS IS** - Do not rename or modify

---

## Summary by Category

### ✅ Move Contents and Consolidate

1. **`/Configurations` → Move contents to `/config/`** - 41 references, consolidate with existing config folder

### ✅ Keep and Rename to Lowercase

2. **`/XML` → `xml/`** - 28 references
3. **`/Data` → `data/`** - **HIGH PRIORITY** - 27 references, database location
4. **`/Data/Core` → `data/core/`** - Part of `/Data` rename
5. **`/src/test/resources/DataSets` → `datasets/`** - 19 references

### ⚠️ Review Usage Before Action

7. **`/Data/Scripts` → `data/scripts/`** - Review if legacy
8. **`/Data/Templates` → `data/templates/`** - Review if legacy
9. **`/Data/SQL` → `data/sql/`** - Review if legacy
10. **`/src/test/resources/TableDef` → `tabledef/`** - Empty file, review usage

### ✅ Delete (Not Needed)

11. **`/src/test/resources/Drivers`** - Legacy, WebDriverManager used now
12. **`/scripts/tests/Data`** - Empty directory
13. **`/scripts/tests/Data/Core`** - Empty directory

### ❌ Ignore (Build Output)

14. **`/target/test-classes/Drivers`** - Build output
15. **`/target/test-classes/DataSets`** - Build output
16. **`/target/test-classes/TableDef`** - Build output

### ✅ Keep As Is (Leave Unchanged)

3. **`/PRIVATE`** - Leave as-is per user request
17. **`/.github/ISSUE_TEMPLATE`** - Leave as-is per user request

---

## Implementation Plan

### Phase 1: High Priority Renames

1. **`/Data` → `data/`** (HIGH PRIORITY - Database location)
   - Update `backend/app/config.py`
   - Update `config/environments.json`
   - Update shell scripts
   - Update documentation

2. **`/Configurations` → Move contents to `/config/`**
   - Move `Environments.xml.template` to `config/`
   - Move/merge `README.md` with `config/README.md`
   - Update `.gitignore` (line 77: `config/Environments.xml`)
   - Update 41 Java files to reference `config/` instead of `Configurations/`
   - Update documentation references
   - Delete empty `/Configurations` folder

3. **`/XML` → `xml/`**
   - Update 28 Java files
   - Update documentation
   - Update pom.xml if needed

### Phase 2: Medium Priority Renames

4. **`/src/test/resources/DataSets` → `datasets/`**
   - Update 19 Java files
   - Update test classes

### Phase 3: Review and Cleanup

5. Review usage of `/Data/Scripts`, `/Data/Templates`, `/Data/SQL`
6. Review usage of `/src/test/resources/TableDef`
7. Delete `/src/test/resources/Drivers` (legacy)
8. Delete `/scripts/tests/Data` and `/scripts/tests/Data/Core` (empty)

---

## Risk Assessment

### High Risk
- **`/Data` rename**: Database location, critical for application functionality
- **`/Configurations` move**: Many Java files reference this, need to update all 41 references

### Medium Risk
- **`/XML` rename**: Multiple Java files reference this
- **`/src/test/resources/DataSets` rename**: Active test data files

### Low Risk
- **Deletions**: Empty directories and legacy folders

---

## Testing Checklist

After renaming/deleting:

- [ ] Verify database paths work correctly (`data/core/`)
- [ ] Run Java tests (verify `config/` references work after `/Configurations` move and `xml/` references)
- [ ] Verify DBUnit tests can find datasets
- [ ] Check service startup scripts (database paths)
- [ ] Verify backend can access database
- [ ] Check all documentation links
- [ ] Run full test suite

---

**Last Updated**: January 16, 2026  
**Next Steps**: Review usage of uncertain folders, then proceed with Phase 1 renames
