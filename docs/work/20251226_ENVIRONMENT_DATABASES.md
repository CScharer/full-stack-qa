# Environment Databases Configuration

**Created**: 2025-12-26  
**Last Updated**: 2025-12-26  
**Type**: Work Item / Planning Document  
**Status**: 📋 Planning  
**Priority**: 🟡 Medium

---

## 🔑 Legend

### Database Types
| Symbol | Type | Description |
|--------|------|-------------|
| 📐 | **Schema Database** | Single source of truth for database schema/structure (read-only template) |
| 🧪 | **Test Database** | Temporary database created during test execution (auto-created, auto-deleted) |
| 🔧 | **Environment Database** | Runtime database for specific environment (dev/test/prod) |
| ⏭️ | **Planned** | Database that needs to be created |

### Status Indicators
| Symbol | Status | Description |
|--------|--------|-------------|
| ✅ | **Exists** | Database file currently exists in repository |
| ⏭️ | **Planned** | Database needs to be created |
| 🗑️ | **Temporary** | Database is created temporarily and auto-deleted |
| 📝 | **Referenced** | Database is referenced in code but may not exist yet |

---

## 📊 Database Files Inventory

### Complete List of All .db Files

| Database File | Location | Type | Status | Purpose | Used By |
|---------------|----------|------|--------|---------|---------|
| `full_stack_qa.db` | `/full-stack-qa/Data/Core/full_stack_qa.db` | 📐 Schema Database | ✅ Exists | **Schema template** - Contains canonical database schema/structure. This is the single source of truth for database schema. Used as reference for creating environment databases. **NOT used for runtime data.** | Schema reference only |
| `test_full_stack_qa.db` | Temporary (created in `tempfile.mkdtemp()`) | 🧪 Test Database | 🗑️ Temporary | **Temporary test database** - Created automatically during pytest test execution. Created in temporary directory, used for tests, then auto-deleted. Not a persistent file. | `backend/tests/conftest.py` (pytest fixtures) |
| `full_stack_qa_dev.db` | `/full-stack-qa/Data/Core/full_stack_qa_dev.db` | 🔧 Environment Database | ⏭️ Planned | **Development environment** - Runtime database for local development. Default database for development work. | Backend API (dev mode), Local development scripts |
| `full_stack_qa_test.db` | `/full-stack-qa/Data/Core/full_stack_qa_test.db` | 🔧 Environment Database | ⏭️ Planned | **Test environment** - Runtime database for integration testing and CI/CD. Used for automated testing. | Integration tests, CI/CD pipelines, Test scripts |
| `full_stack_qa_prod.db` | `/full-stack-qa/Data/Core/full_stack_qa_prod.db` | 🔧 Environment Database | ⏭️ Planned | **Production environment** - Runtime database for production (if needed). Used for production data storage. | Production deployments (if applicable) |

### Database File Summary

**Total Databases**: 5
- ✅ **1 Exists**: `full_stack_qa.db` (schema database)
- 🗑️ **1 Temporary**: `test_full_stack_qa.db` (auto-created during tests)
- ⏭️ **3 Planned**: Environment databases (dev/test/prod)

### Current Code References

| File | Line(s) | Current Reference | Should Be | Type | Status |
|------|---------|-------------------|-----------|------|--------|
| `backend/app/config.py` | 21 | `full_stack_qa.db` | `full_stack_qa_dev.db` | 🔧 Runtime | ⚠️ **INCORRECT** - Using schema DB for runtime |
| `backend/tests/conftest.py` | 21 | `test_full_stack_qa.db` | Keep (temporary) | 🧪 Test | ✅ **CORRECT** - Temporary test DB |
| `scripts/start-backend.sh` | 88 | `full_stack_qa.db` | `full_stack_qa_dev.db` | 🔧 Runtime | ⚠️ **INCORRECT** - Using schema DB |
| `scripts/run-backend-tests.sh` | 20, 52 | `test_full_stack_qa.db`, `full_stack_qa.db` | `full_stack_qa_test.db` | 🔧 Test | ⚠️ **NEEDS UPDATE** - Mixed references |
| `scripts/run-integration-tests.sh` | 35, 39, 41 | `full_stack_qa.db` | `full_stack_qa_test.db` | 🔧 Test | ⚠️ **INCORRECT** - Using schema DB |
| `playwright/playwright.integration.config.ts` | 55 | `full_stack_qa.db` | `full_stack_qa_test.db` | 🔧 Test | ⚠️ **INCORRECT** - Using schema DB |
| `Data/Core/README.md` | 3, 48, 53, 59, 64, 83 | `full_stack_qa.db` | Document both schema and env DBs | 📝 Docs | ⚠️ **NEEDS UPDATE** - Only shows schema DB |
| `docs/LOCAL_DEVELOPMENT.md` | 24, 39, 42, 49, 91, 249, 330, 378 | `full_stack_qa.db` | `full_stack_qa_dev.db` | 📝 Docs | ⚠️ **NEEDS UPDATE** - 8 references |
| `docs/INTEGRATION_TESTING.md` | 62, 66, 67, 75, 165, 180 | `full_stack_qa.db` | `full_stack_qa_test.db` | 📝 Docs | ⚠️ **NEEDS UPDATE** - 6 references |
| `docs/QUICK_START.md` | 71, 103 | `full_stack_qa.db` | `full_stack_qa_dev.db` | 📝 Docs | ⚠️ **NEEDS UPDATE** - 2 references |
| `docs/new_app/WORK_DATABASE.md` | Multiple | `full_stack_qa.db` | Clarify schema vs env | 📝 Docs | ⚠️ **NEEDS UPDATE** - 19 references |
| `docs/new_app/WORK_BACKEND.md` | 16 | `full_stack_qa.db` | `full_stack_qa_dev.db` | 📝 Docs | ⚠️ **NEEDS UPDATE** - 1 reference |
| `docs/new_app/SCHEMA_SOURCE_OF_TRUTH.md` | 80, 83 | `full_stack_qa.db` | Keep (schema DB) | 📝 Docs | ✅ **CORRECT** - Schema examples |

**Summary**:
- ⚠️ **6 Code Files** need updates (using schema DB incorrectly)
- ⚠️ **7 Documentation Files** need updates (references to update)
- ✅ **2 Files** are correct (temporary test DB, schema examples)

---

## 📋 Current State

### Schema Database (Single Source of Truth)
- **Schema Database**: `full_stack_qa.db` ⚠️ **IMPORTANT: Only ONE exists**
- **Location**: `/full-stack-qa/Data/Core/full_stack_qa.db`
- **Purpose**: Contains the canonical database schema/structure
- **Usage**: Reference schema for creating environment databases
- **Note**: This is NOT an environment database - it's the schema template

### Current Environment Database Limitation
- **Current Test Database**: `test_full_stack_qa.db` (hardcoded)
- **Location**: `/full-stack-qa/Data/Core/test_full_stack_qa.db`
- **Issue**: Only one test database exists, used for all test scenarios
- **Problem**: Cannot properly test different environments without conflicts

### Current Implementation
- Database path is hardcoded in multiple locations
- No environment-based database selection
- Test database (`test_full_stack_qa.db`) used for all scenarios

---

## 🎯 Requirements

### ⚠️ Important: Schema Database vs Environment Databases

**Schema Database** (Single, Static):
- **Name**: `full_stack_qa.db`
- **Location**: `/full-stack-qa/Data/Core/full_stack_qa.db`
- **Purpose**: Contains the canonical database schema/structure
- **Usage**: Reference template for creating environment databases
- **Status**: Read-only schema reference (not used for runtime data)
- **Note**: This database should NOT be used for application runtime

**Environment Databases** (Multiple, Runtime):
- Used for actual application runtime and testing
- Each environment has its own isolated database
- Created from the schema database template

### Environment-Specific Databases
We need separate databases for each environment:

1. **Development Environment**
   - Database: `full_stack_qa_dev.db`
   - Path: `/full-stack-qa/Data/Core/full_stack_qa_dev.db`
   - Purpose: Local development and testing
   - **Source**: Created from `full_stack_qa.db` schema

2. **Test Environment**
   - Database: `full_stack_qa_test.db`
   - Path: `/full-stack-qa/Data/Core/full_stack_qa_test.db`
   - Purpose: Integration testing and CI/CD
   - **Source**: Created from `full_stack_qa.db` schema

3. **Production Environment**
   - Database: `full_stack_qa_prod.db`
   - Path: `/full-stack-qa/Data/Core/full_stack_qa_prod.db`
   - Purpose: Production data (if needed)
   - **Source**: Created from `full_stack_qa.db` schema

### Configuration Requirements

1. **Configurable Path**
   - Database path should be configurable via environment variables
   - Support for custom database locations
   - Default path: `/full-stack-qa/Data/Core/`

2. **Configurable Database Name**
   - Database name should be configurable via environment variables
   - Default database: `full_stack_qa_dev.db`
   - Environment-based selection: `full_stack_qa_{env}.db`

3. **Default Configuration**
   - **Default Path**: `/full-stack-qa/Data/Core/`
   - **Default Database**: `full_stack_qa_dev.db`
   - **Full Default Path**: `/full-stack-qa/Data/Core/full_stack_qa_dev.db`

---

## 📝 Implementation Plan

### Phase 1: Configuration Updates

#### 1.1 Backend Configuration (`backend/app/config.py`)
- Update `Settings` class to support:
  - `database_path` (configurable, with default)
  - `database_name` (configurable, with default)
  - `environment` (dev/test/prod) to auto-select database
- Default behavior:
  - If `DATABASE_PATH` env var set → use it
  - If `DATABASE_NAME` env var set → use it with default path
  - If `ENVIRONMENT` env var set → use `full_stack_qa_{env}.db`
  - Otherwise → use default: `full_stack_qa_dev.db`

#### 1.2 Environment Variable Support
- `DATABASE_PATH` - Full path to database file (optional)
- `DATABASE_NAME` - Database filename only (optional)
- `ENVIRONMENT` - Environment name (dev/test/prod) - auto-selects database
- `DATABASE_DIR` - Database directory path (optional, defaults to `Data/Core/`)

### Phase 2: Script Updates

#### 2.1 Update Scripts
- `scripts/start-backend.sh` - Support environment-based database selection
- `scripts/run-backend-tests.sh` - Use test database
- `scripts/run-integration-tests.sh` - Use test database
- `playwright/playwright.integration.config.ts` - Use test database

#### 2.2 Test Configuration
- `backend/tests/conftest.py` - Use test database (`test_full_stack_qa.db` or `full_stack_qa_test.db`)

### Phase 3: Database Creation

#### 3.1 Schema Database (Reference)
- **Verify**: `full_stack_qa.db` exists at `/full-stack-qa/Data/Core/full_stack_qa.db`
- **Purpose**: This is the schema template (read-only reference)
- **Note**: This database should NOT be used for runtime - it's the schema source

#### 3.2 Create Environment Databases
- Create `full_stack_qa_dev.db` (development) - **from schema database**
- Create `full_stack_qa_test.db` (testing) - **from schema database**
- Create `full_stack_qa_prod.db` (production, if needed) - **from schema database**

#### 3.3 Schema Application Process
- **Source**: Copy schema from `full_stack_qa.db` (schema database)
- **Method**: Use schema SQL files or copy from schema database
- Apply schema to each environment database
- Apply delete triggers to each environment database
- Verify all environment databases have same schema as schema database

### Phase 4: Documentation Updates

#### 4.1 Update Documentation
- `docs/LOCAL_DEVELOPMENT.md` - Document environment database selection
- `docs/INTEGRATION_TESTING.md` - Document test database usage
- `docs/QUICK_START.md` - Document default database
- `Data/Core/README.md` - Document all environment databases

---

## 🔧 Technical Implementation Details

### Configuration Logic

```python
# Pseudo-code for database path resolution
def get_database_path():
    # Priority 1: Full path from DATABASE_PATH env var
    if os.getenv("DATABASE_PATH"):
        return Path(os.getenv("DATABASE_PATH"))
    
    # Priority 2: Database name + directory
    db_dir = os.getenv("DATABASE_DIR", "Data/Core")
    db_name = os.getenv("DATABASE_NAME")
    
    if db_name:
        return Path(db_dir) / db_name
    
    # Priority 3: Environment-based selection
    env = os.getenv("ENVIRONMENT", "dev")
    db_name = f"full_stack_qa_{env}.db"
    return Path(db_dir) / db_name
    
    # Priority 4: Default
    return Path("Data/Core") / "full_stack_qa_dev.db"
```

### Environment Variable Examples

```bash
# Development (default)
ENVIRONMENT=dev
# Uses: Data/Core/full_stack_qa_dev.db

# Test
ENVIRONMENT=test
# Uses: Data/Core/full_stack_qa_test.db

# Production
ENVIRONMENT=prod
# Uses: Data/Core/full_stack_qa_prod.db

# Custom path
DATABASE_PATH=/custom/path/custom.db
# Uses: /custom/path/custom.db

# Custom name in default directory
DATABASE_NAME=my_custom.db
# Uses: Data/Core/my_custom.db
```

---

## 📁 File Locations to Update

### Backend Files
- `backend/app/config.py` - Main configuration
- `backend/app/database/connection.py` - Database connection logic
- `backend/tests/conftest.py` - Test database configuration

### Scripts
- `scripts/start-backend.sh` - Backend startup script
- `scripts/run-backend-tests.sh` - Test execution script
- `scripts/run-integration-tests.sh` - Integration test script

### Configuration Files
- `playwright/playwright.integration.config.ts` - Playwright config
- `.env.example` files (if any) - Environment variable examples

### Documentation
- `docs/LOCAL_DEVELOPMENT.md`
- `docs/INTEGRATION_TESTING.md`
- `docs/QUICK_START.md`
- `Data/Core/README.md`

---

## ✅ Acceptance Criteria

### Configuration
- [ ] Database path is configurable via environment variables
- [ ] Database name is configurable via environment variables
- [ ] Default database is `full_stack_qa_dev.db` at `Data/Core/`
- [ ] Environment-based database selection works (dev/test/prod)

### Database Files
- [ ] **Schema Database**: `full_stack_qa.db` exists at `/full-stack-qa/Data/Core/full_stack_qa.db` (single source of truth)
- [ ] **Environment Databases**: 
  - [ ] `full_stack_qa_dev.db` exists and has schema applied (from schema database)
  - [ ] `full_stack_qa_test.db` exists and has schema applied (from schema database)
  - [ ] `full_stack_qa_prod.db` exists (if needed) and has schema applied (from schema database)

### Scripts
- [ ] All scripts support environment-based database selection
- [ ] Test scripts use test database
- [ ] Development scripts use dev database by default

### Documentation
- [ ] All documentation updated with new database configuration
- [ ] Examples show environment-based database selection
- [ ] Default behavior clearly documented

---

## 🔗 Related Documentation

- **Database Schema**: `docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql`
- **Database Work Plan**: `docs/new_app/WORK_DATABASE.md`
- **Backend Configuration**: `backend/app/config.py`
- **Local Development**: `docs/LOCAL_DEVELOPMENT.md`

---

## 📋 Update Plan: Code and Documentation

### Phase 1: Backend Code Updates

#### 1.1 Update `backend/app/config.py`
**Current State**:
- Hardcoded: `database_path: str = "../Data/Core/full_stack_qa.db"`
- Uses schema database for runtime (incorrect)

**Changes Needed**:
- [ ] Add `database_name` configuration option
- [ ] Add `environment` configuration option (dev/test/prod)
- [ ] Update `get_database_path()` to support environment-based selection
- [ ] Default to `full_stack_qa_dev.db` instead of `full_stack_qa.db`
- [ ] Add logic to select database based on `ENVIRONMENT` env var
- [ ] Ensure schema database (`full_stack_qa.db`) is NEVER used for runtime

**Files to Update**:
- `backend/app/config.py` - Main configuration class and path resolution

#### 1.2 Update `backend/app/database/connection.py`
**Current State**:
- Uses `get_database_path()` from config

**Changes Needed**:
- [ ] Verify it correctly uses environment-based database
- [ ] Add validation to prevent using schema database for runtime
- [ ] Add logging to show which database is being used

**Files to Update**:
- `backend/app/database/connection.py` - Connection logic

#### 1.3 Update `backend/tests/conftest.py`
**Current State**:
- Creates temporary `test_full_stack_qa.db` in temp directory
- Uses temporary database for all tests

**Changes Needed**:
- [ ] Option 1: Keep temporary database (current approach - works well)
- [ ] Option 2: Use persistent `full_stack_qa_test.db` for integration tests
- [ ] Document which approach is used
- [ ] Ensure test database is isolated from dev/prod databases

**Files to Update**:
- `backend/tests/conftest.py` - Test database configuration

### Phase 2: Script Updates

#### 2.1 Update `scripts/start-backend.sh`
**Current State**:
- Checks for: `Data/Core/full_stack_qa.db` (schema database)
- Uses schema database path

**Changes Needed**:
- [ ] Update database path check to use environment-based database
- [ ] Default to `full_stack_qa_dev.db` for development
- [ ] Support `ENVIRONMENT` env var to select database
- [ ] Add warning if schema database is detected (should not be used)

**Files to Update**:
- `scripts/start-backend.sh` - Database path check (line 88)

#### 2.2 Update `scripts/run-backend-tests.sh`
**Current State**:
- References: `test_full_stack_qa.db` (temporary test database)
- References: `full_stack_qa.db` (schema database) for integration tests

**Changes Needed**:
- [ ] Update to use `full_stack_qa_test.db` for integration tests
- [ ] Keep temporary database for unit tests (pytest fixtures)
- [ ] Clarify which database is used for which type of test

**Files to Update**:
- `scripts/run-backend-tests.sh` - Test database paths (lines 20, 52)

#### 2.3 Update `scripts/run-integration-tests.sh`
**Current State**:
- Creates/checks: `full_stack_qa.db` (schema database)
- Uses schema database for integration tests

**Changes Needed**:
- [ ] Update to use `full_stack_qa_test.db` for integration tests
- [ ] Create test database from schema database if it doesn't exist
- [ ] Never use schema database for runtime/testing

**Files to Update**:
- `scripts/run-integration-tests.sh` - Database creation and checks (lines 35, 39, 41)

#### 2.4 Update `playwright/playwright.integration.config.ts`
**Current State**:
- Environment variable: `DATABASE_PATH: '../Data/Core/full_stack_qa.db'` (schema database)

**Changes Needed**:
- [ ] Update to use `full_stack_qa_test.db` for integration tests
- [ ] Set `DATABASE_PATH: '../Data/Core/full_stack_qa_test.db'`

**Files to Update**:
- `playwright/playwright.integration.config.ts` - Environment variable (line 55)

### Phase 3: Documentation Updates

#### 3.1 Update `docs/LOCAL_DEVELOPMENT.md`
**Current State**:
- References `full_stack_qa.db` in multiple places
- Database setup instructions use schema database name

**Changes Needed**:
- [ ] Update all references to use `full_stack_qa_dev.db` for development
- [ ] Clarify that `full_stack_qa.db` is schema database (not for runtime)
- [ ] Add section explaining schema database vs environment databases
- [ ] Update environment variable examples

**Files to Update**:
- `docs/LOCAL_DEVELOPMENT.md` - All database references (8 locations)

#### 3.2 Update `docs/INTEGRATION_TESTING.md`
**Current State**:
- References `full_stack_qa.db` for integration tests

**Changes Needed**:
- [ ] Update to use `full_stack_qa_test.db` for integration tests
- [ ] Clarify test database usage
- [ ] Update environment variable examples

**Files to Update**:
- `docs/INTEGRATION_TESTING.md` - Database references (6 locations)

#### 3.3 Update `docs/QUICK_START.md`
**Current State**:
- References `full_stack_qa.db` in environment variables

**Changes Needed**:
- [ ] Update to use `full_stack_qa_dev.db` as default
- [ ] Add note about schema database

**Files to Update**:
- `docs/QUICK_START.md` - Environment variable examples (2 locations)

#### 3.4 Update `Data/Core/README.md`
**Current State**:
- Documents `full_stack_qa.db` as the database
- Verification commands use schema database

**Changes Needed**:
- [ ] Add section explaining schema database vs environment databases
- [ ] Document all environment databases
- [ ] Update verification commands to show both schema and environment databases
- [ ] Add table listing all databases and their purposes

**Files to Update**:
- `Data/Core/README.md` - Database documentation (multiple locations)

#### 3.5 Update `docs/new_app/WORK_DATABASE.md`
**Current State**:
- References `full_stack_qa.db` throughout
- Some references are to schema database, some should be environment databases

**Changes Needed**:
- [ ] Clarify which references are to schema database
- [ ] Update code examples to use environment databases
- [ ] Add note about schema database being template only

**Files to Update**:
- `docs/new_app/WORK_DATABASE.md` - Database references (19 locations)

#### 3.6 Update `docs/new_app/WORK_BACKEND.md`
**Current State**:
- References `full_stack_qa.db` in prerequisites

**Changes Needed**:
- [ ] Update to reference environment databases
- [ ] Clarify schema database vs runtime databases

**Files to Update**:
- `docs/new_app/WORK_BACKEND.md` - Prerequisites section

#### 3.7 Update `docs/new_app/SCHEMA_SOURCE_OF_TRUTH.md`
**Current State**:
- References `full_stack_qa.db` in examples

**Changes Needed**:
- [ ] Clarify this is the schema database (template)
- [ ] Add note that environment databases should be created from this
- [ ] Update examples to show creating environment databases from schema

**Files to Update**:
- `docs/new_app/SCHEMA_SOURCE_OF_TRUTH.md` - Schema examples (2 locations)

#### 3.8 Update `backend/README.md`
**Current State**:
- References `full_stack_qa.db` in documentation

**Changes Needed**:
- [ ] Update to use `full_stack_qa_dev.db` as default
- [ ] Add note about environment-based database selection

**Files to Update**:
- `backend/README.md` - Database references

### Phase 4: Database Creation

#### 4.1 Create Environment Databases
**Tasks**:
- [ ] Create `full_stack_qa_dev.db` from schema database
- [ ] Create `full_stack_qa_test.db` from schema database
- [ ] Create `full_stack_qa_prod.db` from schema database (if needed)
- [ ] Apply schema to each environment database
- [ ] Apply delete triggers to each environment database
- [ ] Verify all databases have identical schema

**Commands**:
```bash
# Create dev database from schema
sqlite3 Data/Core/full_stack_qa_dev.db < docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql
sqlite3 Data/Core/full_stack_qa_dev.db < docs/new_app/DELETE_TRIGGERS.sql

# Create test database from schema
sqlite3 Data/Core/full_stack_qa_test.db < docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql
sqlite3 Data/Core/full_stack_qa_test.db < docs/new_app/DELETE_TRIGGERS.sql

# Create prod database from schema (if needed)
sqlite3 Data/Core/full_stack_qa_prod.db < docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql
sqlite3 Data/Core/full_stack_qa_prod.db < docs/new_app/DELETE_TRIGGERS.sql
```

### Phase 5: Validation

#### 5.1 Code Validation
- [ ] Verify no code uses schema database (`full_stack_qa.db`) for runtime
- [ ] Verify all scripts use environment-appropriate databases
- [ ] Verify test code uses test database
- [ ] Verify development code uses dev database by default

#### 5.2 Documentation Validation
- [ ] Verify all documentation references correct databases
- [ ] Verify schema database is clearly distinguished from environment databases
- [ ] Verify examples use correct database names
- [ ] Verify no broken links or references

---

## 📝 Notes

### Schema Database vs Environment Databases

**Key Differences**:

| Aspect | Schema Database (`full_stack_qa.db`) | Environment Databases (`full_stack_qa_{env}.db`) |
|--------|--------------------------------------|--------------------------------------------------|
| **Quantity** | One (single) | Multiple (one per environment) |
| **Location** | `/full-stack-qa/Data/Core/full_stack_qa.db` | `/full-stack-qa/Data/Core/full_stack_qa_{env}.db` |
| **Purpose** | Schema template/reference | Runtime data storage |
| **Usage** | Read-only schema source | Read/write application data |
| **Created From** | Schema SQL files | Copied from schema database |
| **Modified** | Only when schema changes | Frequently (application data) |
| **Backup** | Schema version control | Environment-specific backups |

**Important Rules**:
- ⚠️ **NEVER** use `full_stack_qa.db` for runtime - it's the schema template only
- ✅ Always create environment databases from the schema database
- ✅ Each environment should have its own isolated database
- ✅ Schema database should remain unchanged except for schema updates

### Migration Path
- Existing code using `full_stack_qa.db` should be updated to use environment databases
- **Do NOT** alias `full_stack_qa.db` to dev - create separate `full_stack_qa_dev.db`
- Backward Compatibility: Support for old database name during transition (if needed)
- Testing: Ensure all tests work with test database (`full_stack_qa_test.db`)
- CI/CD: Use test database (`full_stack_qa_test.db`) in CI/CD pipelines

---

**Status**: 📋 Planning - Ready for implementation  
**Next Steps**: Review and approve implementation plan, then begin Phase 1

