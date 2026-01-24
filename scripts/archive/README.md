# Archived Scripts

**Purpose**: This directory contains one-off migration and fix scripts that were used for specific code quality improvements but are no longer actively needed.

**Status**: Archived for historical reference only. These scripts should not be run unless you understand their purpose and impact.

---

## Scripts in This Directory

### Migration Scripts

#### `migrate-to-guarded-logger.py`
**Purpose**: Migrated Logger declarations to GuardedLogger across the codebase.  
**When Used**: January 2026  
**Status**: ✅ Migration complete - All files migrated

#### `migrate_logging_to_log4j.py`
**Purpose**: Migration script for logging framework changes.  
**When Used**: Historical  
**Status**: ✅ Migration complete

### Code Quality Fix Scripts

#### `fix-guard-log-statement.py`
**Purpose**: Fixed PMD GuardLogStatement violations.  
**When Used**: Historical  
**Status**: ✅ Fixes applied

#### `fix-javahelpers-qualifiers.sh`
**Purpose**: Fixed JavaHelpers qualifier issues.  
**When Used**: Historical  
**Status**: ✅ Fixes applied

#### `fix-missing-imports.sh`
**Purpose**: Fixed missing import statements.  
**When Used**: Historical  
**Status**: ✅ Fixes applied

#### `fix-pmd-violations.py` / `fix-pmd-violations.sh`
**Purpose**: Fixed PMD code quality violations.  
**When Used**: Historical  
**Status**: ✅ Fixes applied

#### `fix-singular-field.py`
**Purpose**: Fixed PMD SingularField violations.  
**When Used**: Historical  
**Status**: ✅ Fixes applied

#### `fix-unnecessary-local-before-return.py`
**Purpose**: Fixed unnecessary local variables before return statements.  
**When Used**: Historical  
**Status**: ✅ Fixes applied

#### `fix-unnecessary-qualified-names.sh`
**Purpose**: Fixed unnecessary qualified names in code.  
**When Used**: Historical  
**Status**: ✅ Fixes applied

#### `replace-log-debug-with-info.sh`
**Purpose**: Replaced LOG.debug() calls with LOG.info().  
**When Used**: Historical  
**Status**: ✅ Changes applied

#### `add-suppress-warnings-to-classes.py`
**Purpose**: Added @SuppressWarnings annotations to classes.  
**When Used**: Historical  
**Status**: ✅ Annotations added

#### `fix-all-pmd-violations.py`
**Purpose**: Comprehensive PMD violation fixes.  
**When Used**: Historical  
**Status**: ✅ Fixes applied

---

## Notes

- These scripts are kept for historical reference only
- Do not run these scripts unless you understand their purpose
- All migrations and fixes have been completed
- Scripts may reference old code patterns that no longer exist

---

**Last Updated**: January 16, 2026
