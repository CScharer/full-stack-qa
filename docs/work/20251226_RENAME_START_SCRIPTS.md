# Rename Start Scripts and Add Environment Parameter

**Created**: 2025-12-26  
**Last Updated**: 2025-12-26  
**Type**: Refactoring / Script Improvement  
**Status**: ✅ **TESTED & READY FOR REVIEW** - All changes complete, tested, and ready for local review  
**Priority**: 🟡 Medium  
**Branch**: `refactor/rename-start-scripts-add-env-param`

---

## 🔑 Legend

### Status Indicators
| Symbol | Status | Description |
|--------|--------|-------------|
| ✅ | Completed | Task is complete and verified |
| 🔍 | In Progress | Task is currently being worked on |
| ⏭️ | Pending | Waiting to be started |
| ❌ | Not Started | Task has not been started |

### Script Types
| Symbol | Type | Description |
|--------|------|-------------|
| 🔧 | Backend Script | Script that starts/manages backend service |
| 🎨 | Frontend Script | Script that starts/manages frontend service |
| 🚀 | Combined Script | Script that starts both backend and frontend |

### Environment Types
| Symbol | Environment | Description |
|--------|-------------|-------------|
| 🔵 | dev | Development environment (default) |
| 🟡 | test | Test environment |
| 🔴 | prod | Production environment |

---

## 📋 Overview

This work refactors the start scripts to:
1. **Rename scripts** for consistency and brevity
2. **Add environment parameter support** to all start scripts
3. **Standardize environment handling** across all scripts

### Current State
- Scripts use environment variables (`ENVIRONMENT`) but don't accept command-line parameters
- Script names are verbose (`start-backend.sh`, `start-frontend.sh`, `start-dev.sh`)
- Environment defaults to `dev` but can't be easily changed via command line

### Target State
- Scripts accept environment as command-line parameter: `--env dev|test|prod` or `-e dev|test|prod`
- Environment defaults to `dev` if not specified
- Script names are shorter and more consistent: `start-be.sh`, `start-fe.sh`, `start-env.sh`

---

## 📊 Script Files

### Rename Mapping

| Current Name | New Name | Type | Environment Support |
|--------------|----------|------|---------------------|
| `scripts/start-backend.sh` | `scripts/start-be.sh` | 🔧 Backend | ✅ Accept `--env` or `-e` parameter (default: dev) |
| `scripts/start-frontend.sh` | `scripts/start-fe.sh` | 🎨 Frontend | ✅ Accept `--env` or `-e` parameter (default: dev) |
| `scripts/start-dev.sh` | `scripts/start-env.sh` | 🚀 Combined | ✅ Accept `--env` or `-e` parameter (default: dev) |

---

## 🎯 Requirements

### 1. Environment Parameter Support
- All scripts must accept environment parameter via command line
- Parameter format: `--env <env>` or `-e <env>`
- Valid values: `dev`, `test`, `prod`
- Default value: `dev` (if not specified)
- Parameter validation: Reject invalid environment values

### 2. Script Renaming
- Rename files according to mapping table
- Update all references to old script names in:
  - Documentation files
  - Other scripts
  - README files
  - Any configuration files

### 3. Backward Compatibility
- Scripts should still respect `ENVIRONMENT` environment variable (for CI/CD)
- Command-line parameter takes precedence over environment variable
- Priority: Command-line parameter > Environment variable > Default (dev)

---

## 📝 Implementation Plan

### Phase 1: Update Scripts with Environment Parameter Support

#### 1.1 Update `start-backend.sh` → `start-be.sh` ✅ **COMPLETED**
**Status**: ✅ Completed

**Changes Needed**:
- [ ] Add command-line argument parsing for `--env` or `-e` parameter
- [ ] Validate environment value (must be dev, test, or prod)
- [ ] Set priority: command-line param > ENVIRONMENT env var > default (dev)
- [ ] Update script to use parsed environment value
- [ ] Update help/usage messages

**Current Behavior**:
- Reads `ENVIRONMENT` from environment variable
- Defaults to `dev` if not set
- Exports `ENVIRONMENT` for backend config

**New Behavior**:
- Accepts `--env dev|test|prod` or `-e dev|test|prod` command-line parameter
- Still respects `ENVIRONMENT` environment variable if parameter not provided
- Defaults to `dev` if neither parameter nor env var is set
- Validates environment value and shows error for invalid values

#### 1.2 Update `start-frontend.sh` → `start-fe.sh` ✅ **COMPLETED**
**Status**: ✅ Completed

**Changes Needed**:
- [ ] Add command-line argument parsing for `--env` or `-e` parameter
- [ ] Validate environment value (must be dev, test, or prod)
- [ ] Set priority: command-line param > ENVIRONMENT env var > default (dev)
- [ ] Update script to use parsed environment value
- [ ] Update help/usage messages

**Current Behavior**:
- Reads `ENVIRONMENT` from environment variable
- Defaults to `dev` if not set
- Uses environment to determine port configuration

**New Behavior**:
- Accepts `--env dev|test|prod` or `-e dev|test|prod` command-line parameter
- Still respects `ENVIRONMENT` environment variable if parameter not provided
- Defaults to `dev` if neither parameter nor env var is set
- Validates environment value and shows error for invalid values

#### 1.3 Update `start-dev.sh` → `start-env.sh` ✅ **COMPLETED**
**Status**: ✅ Completed

**Changes Needed**:
- [ ] Add command-line argument parsing for `--env` or `-e` parameter
- [ ] Validate environment value (must be dev, test, or prod)
- [ ] Set priority: command-line param > ENVIRONMENT env var > default (dev)
- [ ] Update script to use parsed environment value
- [ ] Update help/usage messages
- [ ] Update script description and comments

**Current Behavior**:
- Hardcodes `ENVIRONMENT=dev` (line 129)
- Accepts port parameters (`be=PORT`, `fe=PORT`)
- Accepts flags (`--background`, `--force`)

**New Behavior**:
- Accepts `--env dev|test|prod` or `-e dev|test|prod` command-line parameter
- Still respects `ENVIRONMENT` environment variable if parameter not provided
- Defaults to `dev` if neither parameter nor env var is set
- Validates environment value and shows error for invalid values
- Maintains existing port and flag functionality

### Phase 2: Rename Script Files

#### 2.1 Rename Scripts ✅ **COMPLETED**
**Status**: ✅ Completed

**Tasks**:
- [x] Rename `scripts/start-backend.sh` → `scripts/start-be.sh`
- [x] Rename `scripts/start-frontend.sh` → `scripts/start-fe.sh`
- [x] Rename `scripts/start-dev.sh` → `scripts/start-env.sh`
- [x] Update file permissions (ensure scripts are executable)

### Phase 3: Update References

#### 3.1 Find All References ✅ **COMPLETED**
**Status**: ✅ Completed

**Tasks**:
- [x] Search for references to `start-backend.sh`
- [x] Search for references to `start-frontend.sh`
- [x] Search for references to `start-dev.sh`
- [x] Create list of files that need updates

#### 3.2 Update Documentation ✅ **COMPLETED**
**Status**: ✅ Completed

**Files Updated**:
- [x] `docs/QUICK_START.md` - Updated all script references
- [x] `docs/LOCAL_DEVELOPMENT.md` - Updated all script references
- [x] `docs/guides/infrastructure/DATABASES.md` - Updated script reference with new usage
- [x] `docs/work/20251226_ENVIRONMENT_DATABASES.md` - Updated script references

#### 3.3 Update Other Scripts
**Status**: ⏭️ Pending

**Files to Check/Update**:
- [ ] `scripts/start-services-for-ci.sh` (if it references these scripts)
- [ ] `scripts/stop-services.sh` (if it references these scripts)
- [ ] Any other scripts that call these scripts

#### 3.4 Update Configuration Files
**Status**: ⏭️ Pending

**Files to Check/Update**:
- [ ] `.github/workflows/*.yml` (if any workflows reference these scripts)
- [ ] Any CI/CD configuration files

### Phase 4: Testing and Validation

#### 4.1 Test Script Functionality
**Status**: ⏭️ Pending

**Tests**:
- [ ] Test `start-be.sh` with `--env dev` (default)
- [ ] Test `start-be.sh` with `--env test`
- [ ] Test `start-be.sh` with `--env prod`
- [ ] Test `start-be.sh` with `-e dev`
- [ ] Test `start-be.sh` with invalid environment (should error)
- [ ] Test `start-fe.sh` with `--env dev` (default)
- [ ] Test `start-fe.sh` with `--env test`
- [ ] Test `start-fe.sh` with `--env prod`
- [ ] Test `start-fe.sh` with invalid environment (should error)
- [ ] Test `start-env.sh` with `--env dev` (default)
- [ ] Test `start-env.sh` with `--env test`
- [ ] Test `start-env.sh` with `--env prod`
- [ ] Test `start-env.sh` with invalid environment (should error)
- [ ] Test that `ENVIRONMENT` environment variable still works (backward compatibility)

#### 4.2 Test Environment Variable Priority
**Status**: ⏭️ Pending

**Tests**:
- [ ] Command-line parameter overrides environment variable
- [ ] Environment variable used when parameter not provided
- [ ] Default (dev) used when neither parameter nor env var provided

#### 4.3 Verify All References Updated
**Status**: ⏭️ Pending

**Tasks**:
- [ ] Verify no broken references to old script names
- [ ] Verify all documentation updated
- [ ] Verify all scripts updated
- [ ] Verify all workflows updated (if applicable)

---

## 📋 Implementation Steps

### Step 1: Update Scripts with Environment Parameter ✅ **COMPLETED**
1. ✅ Update `start-backend.sh` to accept `--env` or `-e` parameter
2. ✅ Update `start-frontend.sh` to accept `--env` or `-e` parameter
3. ✅ Update `start-dev.sh` to accept `--env` or `-e` parameter
4. ✅ Add validation for environment values
5. ✅ Update help/usage messages

### Step 2: Rename Script Files ✅ **COMPLETED**
1. ✅ Rename `start-backend.sh` → `start-be.sh`
2. ✅ Rename `start-frontend.sh` → `start-fe.sh`
3. ✅ Rename `start-dev.sh` → `start-env.sh`
4. ✅ Ensure scripts remain executable

### Step 3: Find and Update References ✅ **COMPLETED**
1. ✅ Search codebase for old script names
2. ✅ Update all documentation references
3. ✅ Update all script references
4. ✅ Update any workflow/CI references

### Step 4: Test and Validate ✅ **COMPLETED**
1. ✅ Test all scripts with different environment values (dev, test, prod)
2. ✅ Test environment variable priority (command-line > env var > default)
3. ✅ Verify backward compatibility (ENVIRONMENT env var still works)
4. ✅ Verify all references are updated
5. ✅ Test invalid environment values (validation works)
6. ✅ Test help output for all scripts

---

## ✅ Acceptance Criteria

### Functionality
- [x] All scripts accept `--env` or `-e` parameter ✅ **COMPLETED**
- [x] Environment parameter defaults to `dev` if not specified ✅ **COMPLETED**
- [x] Invalid environment values are rejected with clear error message ✅ **COMPLETED**
- [x] Command-line parameter takes precedence over environment variable ✅ **COMPLETED**
- [x] Environment variable still works for backward compatibility ✅ **COMPLETED**
- [x] All scripts renamed according to mapping table ✅ **COMPLETED**

### Documentation
- [x] All documentation updated with new script names ✅ **COMPLETED**
- [x] Usage examples show environment parameter ✅ **COMPLETED**
- [x] Help messages updated in scripts ✅ **COMPLETED**

### References
- [x] No broken references to old script names ✅ **COMPLETED**
- [x] All scripts that call these scripts are updated ✅ **COMPLETED**
- [x] All workflows/CI configurations updated (if applicable) ✅ **COMPLETED** (N/A - no CI references found)

### Testing
- [x] All scripts tested with valid environment values (dev, test, prod) ✅ **COMPLETED**
- [x] All scripts tested with invalid environment values (should error) ✅ **COMPLETED**
- [x] Environment variable priority tested and verified ✅ **COMPLETED**
- [x] Backward compatibility verified ✅ **COMPLETED**

---

## 📝 Usage Examples

### After Implementation

**Backend Script**:
```bash
# Default (dev)
./scripts/start-be.sh

# Explicit dev
./scripts/start-be.sh --env dev
./scripts/start-be.sh -e dev

# Test environment
./scripts/start-be.sh --env test
./scripts/start-be.sh -e test

# Production environment
./scripts/start-be.sh --env prod
./scripts/start-be.sh -e prod

# Invalid (should error)
./scripts/start-be.sh --env invalid
# Error: Invalid environment 'invalid'. Must be one of: dev, test, prod
```

**Frontend Script**:
```bash
# Default (dev)
./scripts/start-fe.sh

# Explicit dev
./scripts/start-fe.sh --env dev
./scripts/start-fe.sh -e dev

# Test environment
./scripts/start-fe.sh --env test
./scripts/start-fe.sh -e test

# Production environment
./scripts/start-fe.sh --env prod
./scripts/start-fe.sh -e prod
```

**Combined Script**:
```bash
# Default (dev)
./scripts/start-env.sh

# Explicit dev
./scripts/start-env.sh --env dev
./scripts/start-env.sh -e dev

# Test environment
./scripts/start-env.sh --env test
./scripts/start-env.sh -e test

# Production environment
./scripts/start-env.sh --env prod
./scripts/start-env.sh -e prod

# With port overrides (existing functionality maintained)
./scripts/start-env.sh --env test be=8004 fe=3004
./scripts/start-env.sh -e prod --background
```

**Backward Compatibility** (Environment Variable):
```bash
# Still works - environment variable respected if parameter not provided
ENVIRONMENT=test ./scripts/start-be.sh
ENVIRONMENT=prod ./scripts/start-fe.sh

# Command-line parameter takes precedence
ENVIRONMENT=dev ./scripts/start-be.sh --env test
# Uses: test (parameter overrides env var)
```

---

## 🔄 Environment Priority

The scripts will use the following priority order:

1. **Command-line parameter** (`--env` or `-e`) - **Highest Priority**
2. **Environment variable** (`ENVIRONMENT`) - **Medium Priority**
3. **Default value** (`dev`) - **Lowest Priority**

---

## 📚 Related Documentation

- **Database Configuration**: `docs/work/20251226_ENVIRONMENT_DATABASES.md`
- **Local Development**: `docs/LOCAL_DEVELOPMENT.md`
- **Quick Start**: `docs/QUICK_START.md`

---

**Status**: 📋 Ready for Implementation  
**Next Steps**: Begin Phase 1 - Update scripts with environment parameter support

