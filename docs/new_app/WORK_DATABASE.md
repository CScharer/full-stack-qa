# Database Work - ONE GOAL Project

**Created**: 2025-12-14  
**Last Updated**: 2025-12-14  
**Purpose**: Detailed work plan for database implementation  
**Status**: 🟢 In Progress  
**Priority**: 🔴 HIGHEST (Must be completed before Backend/Frontend)

---

## 📋 Overview

The database is the foundation of the ONE GOAL application. All backend and frontend work depends on having a working, tested database with proper schema, seed data, and migration scripts.

**Current Status**:
- ✅ Schema designed and documented (`ONE_GOAL_SCHEMA_CORRECTED.sql`)
- ✅ Relationships documented (`ENTITY_RELATIONSHIPS.md`)
- ✅ Source of truth established (`SCHEMA_SOURCE_OF_TRUTH.md`)
- ✅ **Schema database created** (`data/Core/full_stack_qa.db` - template only)
- ✅ **Environment databases created** (`full_stack_qa_dev.db`, `full_stack_qa_test.db`)
- ✅ **Comprehensive test suite** (62 tests covering schema, FKs, constraints, relationships, triggers)
- ✅ **Delete triggers implemented** (`DELETE_TRIGGERS.sql`)
- ✅ **Delete behavior documented** (`DELETE_BEHAVIOR.md`)
- ✅ **Default value table** (user-specific defaults support)
- ⏭️ **Migration system** - Pending
- ⏭️ **Seed data scripts** - Pending
- ⏭️ **Database utilities** - Pending

**Note**: The project now uses environment-specific databases:
- **Schema Database** (`full_stack_qa.db`): Template/reference only - NEVER used for runtime
- **Development Database** (`full_stack_qa_dev.db`): Default for local development
- **Test Database** (`full_stack_qa_test.db`): Used for integration testing
- **Production Database** (`full_stack_qa_prod.db`): For production (create when needed)

---

## 🎯 Goals

1. Create the SQLite database from the schema
2. Implement database migration system
3. Create seed data scripts
4. Test all relationships and constraints
5. Document database operations
6. Create database utilities/helpers

---

## 📝 Tasks

### Phase 1: Database Setup & Schema Creation

#### Task 1.1: Create Database File
**Status**: ✅ **COMPLETED**  
**Priority**: 🔴 Critical  
**Completed**: 2025-12-14

**What Was Done**:
1. ✅ Created schema database at `data/Core/full_stack_qa.db` (template)
2. ✅ Created environment databases (`full_stack_qa_dev.db`, `full_stack_qa_test.db`)
2. ✅ Applied schema from `ONE_GOAL_SCHEMA_CORRECTED.sql`
3. ✅ Added `default_value` table for centralized defaults management
4. ✅ Applied delete triggers from `DELETE_TRIGGERS.sql`
5. ✅ Verified all 10 tables created
6. ✅ Verified all indexes created
7. ✅ Verified foreign keys enabled

**Database Location**:
- **Schema Database Path**: `data/Core/full_stack_qa.db` (template only)
- **Development Database Path**: `data/Core/full_stack_qa_dev.db` (default for runtime)
- **Test Database Path**: `data/Core/full_stack_qa_test.db` (for testing)
- **Name**: Matches repository name (`full-stack-qa` → `full_stack_qa_{env}.db`)

**Verification Commands**:
```bash
# Check tables
sqlite3 data/Core/full_stack_qa.db ".tables"

# Check Foreign Keys
sqlite3 data/Core/full_stack_qa.db "PRAGMA foreign_keys;"
# Returns: 1

# View schema
sqlite3 data/Core/full_stack_qa.db ".schema"
```

**Acceptance Criteria**:
- [x] Database file created successfully
- [x] All 10 tables created (application, company, client, contact, contact_email, contact_phone, note, job_search_site, application_sync, default_value)
- [x] All indexes created
- [x] Foreign keys enabled
- [x] No errors in schema creation
- [x] Delete triggers applied

**Files Created**:
- ✅ `data/Core/full_stack_qa.db` (Schema database - template only)
- ✅ `data/Core/full_stack_qa_dev.db` (Development database - default for runtime)
- ✅ `data/Core/full_stack_qa_test.db` (Test database - for integration testing)
- ✅ `data/Core/README.md` (Database documentation)

---

#### Task 1.2: Create Delete Triggers
**Status**: ✅ **COMPLETED**  
**Priority**: 🔴 Critical  
**Completed**: 2025-12-14

**What Was Done**:
1. ✅ Created `DELETE_TRIGGERS.sql` with 4 triggers
2. ✅ Documented delete behavior in `DELETE_BEHAVIOR.md`
3. ✅ Updated API contract with delete endpoints and warnings
4. ✅ Created comprehensive trigger tests

**Triggers Implemented**:

1. **trg_application_delete_cascade**:
   - Deletes all `application_sync` records
   - Sets `contact.application_id` to NULL

2. **trg_contact_delete_cascade**:
   - Explicitly deletes `contact_email` records
   - Explicitly deletes `contact_phone` records

3. **trg_company_delete_cascade**:
   - Sets `application.company_id` to NULL
   - Sets `contact.company_id` to NULL

4. **trg_client_delete_cascade**:
   - Sets `application.client_id` to NULL
   - Sets `contact.client_id` to NULL

**Documentation**:
- ✅ `DELETE_TRIGGERS.sql` - Trigger definitions
- ✅ `DELETE_BEHAVIOR.md` - Complete guide for frontend team
- ✅ `API_CONTRACT.md` - Updated with delete endpoints and warnings

**Frontend Requirements**:
- All delete operations must show confirmation dialogs
- Warning messages must list affected records
- User must explicitly confirm deletion

**Acceptance Criteria**:
- [x] All triggers created and tested
- [x] Delete behavior documented
- [x] Frontend requirements specified
- [x] All trigger tests passing (8/8)

**Files Created**:
- ✅ `docs/new_app/DELETE_TRIGGERS.sql`
- ✅ `docs/new_app/DELETE_BEHAVIOR.md`
- ✅ `data/Core/tests/test_delete_triggers.py`

---

#### Task 1.2: Create Migration System
**Status**: ⏭️ Pending  
**Priority**: 🔴 Critical  
**Estimated Time**: 2-3 hours

**Steps**:
1. Create migration script structure:
   ```
   database/
   ├── migrations/
   │   ├── 20251214_000000_initial_schema.sql
   │   └── README.md
   ├── scripts/
   │   └── migrate.py
   └── schema_version.txt
   ```

2. Copy schema to initial migration:
   ```bash
   cp docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql data/Core/migrations/20251214_000000_initial_schema.sql
   cp docs/new_app/DELETE_TRIGGERS.sql data/Core/migrations/20251214_000001_delete_triggers.sql
   ```

3. Create `migrate.py` script:
   - Read `schema_version.txt` to track current version
   - Apply migrations in order
   - Update version after successful migration
   - Rollback support (optional but recommended)

4. Create migration tracking table:
   ```sql
   CREATE TABLE IF NOT EXISTS "schema_migrations" (
       "id" INTEGER PRIMARY KEY AUTOINCREMENT,
       "version" TEXT NOT NULL UNIQUE,
       "applied_on" TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
       "description" TEXT
   );
   ```

**Acceptance Criteria**:
- [ ] Migration script can apply initial schema
- [ ] Migration script tracks applied versions
- [ ] Migration script prevents duplicate application
- [ ] Migration script provides clear error messages
- [ ] Migration script can be run multiple times safely (idempotent)

**Files to Create**:
- `data/Core/migrations/20251214_000000_initial_schema.sql`
- `data/Core/migrations/20251214_000001_delete_triggers.sql`
- `data/Core/scripts/migrate.py`
- `data/Core/schema_version.txt`
- `data/Core/migrations/README.md`

**Example `migrate.py` structure**:
```python
#!/usr/bin/env python3
"""
Database migration script for ONE GOAL project.
Applies migrations in order and tracks version.
"""
import sqlite3
import os
from pathlib import Path
from datetime import datetime

# Database path (development database - default)
DB_PATH = Path(__file__).parent.parent / "Data" / "Core" / "full_stack_qa_dev.db"
# Note: In production code, use environment-based selection via backend config

def get_current_version(db_path):
    """Get current schema version from database."""
    # Implementation here
    pass

def apply_migration(db_path, migration_file):
    """Apply a single migration file."""
    # Implementation here
    pass

def main():
    """Main migration function."""
    # Implementation here
    pass

if __name__ == "__main__":
    main()
```

**Note**: The database is located at `data/Core/full_stack_qa.db` (not `database/test-data.db`).

---

#### Task 1.3: Test Schema & Relationships
**Status**: ✅ **COMPLETED**  
**Priority**: 🔴 Critical  
**Completed**: 2025-12-14

**What Was Done**:
1. ✅ Created comprehensive test suite at `data/Core/tests/`
2. ✅ 62 tests covering all aspects of the database
3. ✅ All tests passing

**Test Coverage**:

**test_schema.py** (10 tests):
- All tables created
- Foreign keys enabled
- Table structures verified
- Indexes created
- Primary keys verified

**test_foreign_keys.py** (7 tests):
- Application → Company FK
- Application → Client FK
- Contact → Company/Application/Client FKs
- Contact Email/Phone → Contact FKs
- Note → Application FK
- Application Sync → Application FK

**test_cascade_deletes.py** (4 tests):
- Contact deletion cascades to emails/phones
- Application deletion cascades to notes
- No CASCADE where not defined

**test_defaults.py** (11 tests):
- `is_deleted` defaults to 0
- Timestamp defaults
- Application defaults (status, work_setting)
- Company defaults (country, job_type)
- Contact defaults (title)
- Email/Phone defaults

**test_constraints.py** (12 tests):
- NOT NULL constraints (10 tests)
- UNIQUE constraints (2 tests)

**test_relationships.py** (5 tests):
- Application with company and client
- Contact with multiple emails
- Contact with multiple phones
- Application with multiple notes
- Complex relationship scenarios

**test_default_value_table.py** (5 tests):
- System defaults
- User-specific defaults
- Lookup logic
- Updates
- Soft deletes

**test_delete_triggers.py** (8 tests):
- Application delete triggers (sync deletion, contact FK null)
- Company delete triggers (application FK null, contact FK null)
- Client delete triggers (application FK null, contact FK null)
- Contact delete triggers (email/phone deletion)

**Test Infrastructure**:
- ✅ `conftest.py`: Pytest fixtures for test database setup
- ✅ Session-scoped fixture creates empty database before all tests
- ✅ Function-scoped fixtures provide fresh connections per test
- ✅ Automatic cleanup of temporary database

**Running Tests**:
```bash
# Run all tests
pytest data/Core/tests/ -v

# Run specific test file
pytest data/Core/tests/test_schema.py -v

# Run with coverage
pytest data/Core/tests/ --cov=data/Core/tests --cov-report=html -v
```

**Acceptance Criteria**:
- [x] All Foreign Key constraints enforced
- [x] CASCADE deletes work correctly
- [x] Unique constraints enforced
- [x] NOT NULL constraints enforced
- [x] Default values applied correctly
- [x] Indexes created and verified
- [x] All test scenarios pass (62/62 passing)
- [x] Delete triggers tested and working

**Files Created**:
- ✅ `data/Core/tests/__init__.py`
- ✅ `data/Core/tests/conftest.py`
- ✅ `data/Core/tests/test_schema.py`
- ✅ `data/Core/tests/test_foreign_keys.py`
- ✅ `data/Core/tests/test_cascade_deletes.py`
- ✅ `data/Core/tests/test_defaults.py`
- ✅ `data/Core/tests/test_constraints.py`
- ✅ `data/Core/tests/test_relationships.py`
- ✅ `data/Core/tests/test_default_value_table.py`
- ✅ `data/Core/tests/test_delete_triggers.py`
- ✅ `data/Core/tests/requirements.txt`
- ✅ `data/Core/tests/README.md`

---

### Phase 2: Seed Data & Test Data

#### Task 2.1: Create Seed Data Scripts
**Status**: ⏭️ Pending  
**Priority**: 🟡 High  
**Estimated Time**: 2-3 hours

**Steps**:
1. Create seed data structure:
   ```
   data/Core/
   ├── seeds/
   │   ├── 01_default_values.sql (populate default_value table)
   │   ├── 02_companies.sql
   │   ├── 03_clients.sql
   │   ├── 04_applications.sql
   │   ├── 05_contacts.sql
   │   ├── 06_contact_emails.sql
   │   ├── 07_contact_phones.sql
   │   ├── 08_notes.sql
   │   ├── 09_job_search_sites.sql
   │   └── seed_all.py
   ```

2. Create realistic test data:
   - 3-5 companies (recruiting firms)
   - 5-10 clients (companies where jobs are)
   - 10-20 applications (various statuses)
   - 15-25 contacts (recruiters, managers, leads)
   - Multiple emails/phones per contact
   - 20-30 notes (various applications)
   - 5-10 job search sites

3. Create `seed_all.py` script:
   - Apply seeds in order (respecting Foreign Keys)
   - Check for existing data (optional: clear first)
   - Provide feedback on what was created

**Example seed data**:
```sql
-- 01_companies.sql
INSERT INTO "company" ("name", "address", "city", "state", "zip", "country", "job_type", "created_by", "modified_by")
VALUES 
    ('Tech Recruiters Inc', '123 Main St', 'San Francisco', 'CA', '94102', 'United States', 'Technology', 'system', 'system'),
    ('Global Staffing Solutions', '456 Market St', 'New York', 'NY', '10001', 'United States', 'Technology', 'system', 'system'),
    ('Elite Talent Group', '789 Broadway', 'Seattle', 'WA', '98101', 'United States', 'Technology', 'system', 'system');
```

**Acceptance Criteria**:
- [ ] Seed data creates realistic test scenarios
- [ ] All Foreign Key relationships satisfied
- [ ] Seed script can be run multiple times safely
- [ ] Seed data covers various statuses, types, etc.
- [ ] Seed script provides clear output

**Files to Create**:
- `data/Core/seeds/01_default_values.sql` (populate default_value table - see `DATABASE_DEFAULTS_REFERENCE.md`)
- `data/Core/seeds/02_companies.sql`
- `data/Core/seeds/03_clients.sql`
- `data/Core/seeds/04_applications.sql`
- `data/Core/seeds/05_contacts.sql`
- `data/Core/seeds/06_contact_emails.sql`
- `data/Core/seeds/07_contact_phones.sql`
- `data/Core/seeds/08_notes.sql`
- `data/Core/seeds/09_job_search_sites.sql`
- `data/Core/seeds/seed_all.py`

---

#### Task 2.2: Create Database Reset Script
**Status**: ⏭️ Pending  
**Priority**: 🟡 High  
**Estimated Time**: 1 hour

**Steps**:
1. Create `data/Core/scripts/reset_db.py`:
   - Drop all tables (in correct order to respect Foreign Keys)
   - Re-run migrations
   - Re-run seed data
   - Provide confirmation prompt

2. Alternative: Create fresh database file
   - Backup existing database (optional)
   - Delete database file
   - Recreate from migrations
   - Apply seeds

**Acceptance Criteria**:
- [ ] Script can reset database to clean state
- [ ] Script applies migrations after reset
- [ ] Script applies seed data after reset
- [ ] Script provides clear feedback
- [ ] Script has safety confirmation

**Files to Create**:
- `data/Core/scripts/reset_db.py`

**Note**: Database file is at `data/Core/full_stack_qa.db`

---

### Phase 3: Database Utilities & Helpers

#### Task 3.1: Create Database Connection Helper
**Status**: ⏭️ Pending  
**Priority**: 🟡 High  
**Estimated Time**: 1-2 hours

**Steps**:
1. Create `data/Core/scripts/db_connection.py`:
   - Database connection function
   - Connection context manager
   - Error handling
   - Foreign key enforcement check

2. Features:
   - Path resolution (works from any directory)
   - Connection pooling (if needed)
   - Transaction support
   - Query execution helpers

**Example**:
```python
# data/Core/scripts/db_connection.py
import sqlite3
from pathlib import Path
from contextlib import contextmanager

DB_PATH = Path(__file__).parent.parent / "Data" / "Core" / "full_stack_qa_dev.db"
# Note: In production code, use environment-based selection via backend config

@contextmanager
def get_db_connection():
    """Get database connection with proper configuration."""
    conn = sqlite3.connect(str(DB_PATH))
    conn.row_factory = sqlite3.Row  # Return rows as dict-like objects
    conn.execute("PRAGMA foreign_keys = ON")
    try:
        yield conn
        conn.commit()
    except Exception:
        conn.rollback()
        raise
    finally:
        conn.close()
```

**Acceptance Criteria**:
- [ ] Connection helper works from any directory
- [ ] Foreign keys automatically enabled
- [ ] Proper error handling
- [ ] Transaction support
- [ ] Row factory for dict-like access

**Files to Create**:
- `data/Core/scripts/db_connection.py`

---

#### Task 3.2: Create Query Helpers
**Status**: ⏭️ Pending  
**Priority**: 🟢 Medium  
**Estimated Time**: 2-3 hours

**Steps**:
1. Create common query functions in `data/Core/scripts/query_helpers.py`:
   - `get_application_by_id(id)`
   - `get_applications_by_status(status)`
   - `get_contact_with_emails_phones(contact_id)`
   - `get_application_with_relationships(application_id)`
   - `soft_delete_application(id, user)`
   - `soft_delete_contact(id, user)`

2. Create helper functions for:
   - Soft delete operations
   - Audit field updates (created_by, modified_by, timestamps)
   - Common joins (application + company + client)
   - Filtering by is_deleted

**Example**:
```python
# data/Core/scripts/query_helpers.py
from db_connection import get_db_connection

def get_application_by_id(app_id):
    """Get application by ID with company and client info."""
    with get_db_connection() as conn:
        cursor = conn.execute("""
            SELECT a.*, c.name as company_name, cl.name as client_name
            FROM application a
            LEFT JOIN company c ON a.company_id = c.id
            LEFT JOIN client cl ON a.client_id = cl.id
            WHERE a.id = ? AND a.is_deleted = 0
        """, (app_id,))
        return cursor.fetchone()
```

**Acceptance Criteria**:
- [ ] Common queries implemented
- [ ] Soft delete helpers work correctly
- [ ] Audit fields updated automatically
- [ ] Queries filter by is_deleted = 0 by default
- [ ] Queries handle relationships correctly

**Files to Create**:
- `data/Core/scripts/query_helpers.py`

---

#### Task 3.3: Create Database Documentation
**Status**: ⏭️ Pending  
**Priority**: 🟢 Medium  
**Estimated Time**: 1 hour

**Steps**:
1. Update `data/Core/README.md` (already exists, add migration/seed info):
   - Database structure overview
   - How to run migrations
   - How to seed data
   - How to reset database
   - Common queries examples
   - Troubleshooting

2. Document database operations:
   - Migration process
   - Seed data process
   - Reset process
   - Backup/restore (if needed)

**Acceptance Criteria**:
- [ ] Clear instructions for all operations
- [ ] Examples provided
- [ ] Troubleshooting section
- [ ] Links to related documentation

**Files to Update**:
- `data/Core/README.md` (add migration/seed/utility sections)

---

## 🧪 Testing Checklist

### Schema Tests
- [x] All tables created correctly (10 tables + default_value)
- [x] All Foreign Keys enforced (7 tests passing)
- [x] CASCADE deletes work (4 tests passing)
- [x] Delete triggers work (8 tests passing)
- [x] Unique constraints work (2 tests passing)
- [x] NOT NULL constraints work (10 tests passing)
- [x] Default values applied (11 tests passing)
- [x] Indexes created and used (verified in schema tests)

### Data Tests
- [ ] Seed data creates successfully (pending)
- [x] Foreign Key relationships satisfied (5 tests passing)
- [x] Default value table works (5 tests passing)
- [x] Audit fields populated (tested in defaults)
- [x] Can query with relationships (5 tests passing)

### Utility Tests
- [ ] Migration script works (pending)
- [ ] Seed script works (pending)
- [ ] Reset script works (pending)
- [ ] Connection helper works (pending)
- [ ] Query helpers work (pending)

### Delete Behavior Tests
- [x] Application delete triggers tested
- [x] Contact delete triggers tested
- [x] Company delete triggers tested
- [x] Client delete triggers tested
- [x] Delete behavior documented for frontend

---

## 📁 Final Directory Structure

```
data/Core/
├── full_stack_qa.db          ✅ Created
├── README.md                      ✅ Created
├── migrations/                    ⏭️ Pending
│   ├── 20251214_000000_initial_schema.sql
│   ├── 20251214_000001_delete_triggers.sql
│   └── README.md
├── seeds/                         ⏭️ Pending
│   ├── 01_default_values.sql
│   ├── 02_companies.sql
│   ├── 03_clients.sql
│   ├── 04_applications.sql
│   ├── 05_contacts.sql
│   ├── 06_contact_emails.sql
│   ├── 07_contact_phones.sql
│   ├── 08_notes.sql
│   ├── 09_job_search_sites.sql
│   └── seed_all.py
├── scripts/                       ⏭️ Pending
│   ├── migrate.py
│   ├── reset_db.py
│   ├── db_connection.py
│   └── query_helpers.py
├── tests/                         ✅ Created
│   ├── __init__.py
│   ├── conftest.py
│   ├── test_schema.py
│   ├── test_foreign_keys.py
│   ├── test_cascade_deletes.py
│   ├── test_defaults.py
│   ├── test_constraints.py
│   ├── test_relationships.py
│   ├── test_default_value_table.py
│   ├── test_delete_triggers.py
│   ├── requirements.txt
│   └── README.md
├── schema_version.txt             ⏭️ Pending
└── .gitignore                     ⏭️ Pending
```

---

## 🚀 Getting Started

### Quick Start Commands

```bash
# 1. Database already created at data/Core/full_stack_qa.db ✅

# 2. Verify schema
sqlite3 data/Core/full_stack_qa.db ".schema"

# 3. Test Foreign Keys
sqlite3 data/Core/full_stack_qa.db "PRAGMA foreign_keys;"
# Should return: 1

# 4. Run all tests
pytest data/Core/tests/ -v

# 5. View tables
sqlite3 data/Core/full_stack_qa.db ".tables"
```

---

## 📚 Related Documentation

- **Schema Source**: `docs/new_app/SCHEMA_SOURCE_OF_TRUTH.md`
- **Schema File**: `docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql`
- **Delete Triggers**: `docs/new_app/DELETE_TRIGGERS.sql`
- **Delete Behavior**: `docs/new_app/DELETE_BEHAVIOR.md`
- **Relationships**: `docs/new_app/ENTITY_RELATIONSHIPS.md`
- **API Contract**: `docs/new_app/API_CONTRACT.md`
- **Defaults Design**: `docs/new_app/DATABASE_DEFAULTS_DESIGN.md`
- **Defaults Reference**: `docs/new_app/DATABASE_DEFAULTS_REFERENCE.md`
- **Database README**: `data/Core/README.md`
- **Test Documentation**: `data/Core/tests/README.md`

---

## ✅ Definition of Done

The database work is complete when:

1. ✅ **Database file created and schema applied** - `data/Core/full_stack_qa.db`
2. ✅ **Delete triggers implemented** - `DELETE_TRIGGERS.sql` with 4 triggers
3. ✅ **Delete behavior documented** - `DELETE_BEHAVIOR.md` for frontend team
4. ✅ **All schema tests pass** - 62/62 tests passing
5. ⏭️ **Migration system working** - Pending
6. ⏭️ **Seed data scripts created and tested** - Pending
7. ⏭️ **Database utilities/helpers created** - Pending
8. ✅ **Core documentation complete** - Schema, relationships, API contract, delete behavior
9. ✅ **Test suite complete** - Comprehensive coverage of all database functionality
10. ⏭️ **Can be used by backend API** - Database ready, backend integration pending

---

## 🎯 Completed Work Summary

### ✅ Phase 1: Database Setup & Schema Creation
- ✅ Task 1.1: Database file created (`full_stack_qa.db`)
- ✅ Task 1.2: Delete triggers implemented and tested
- ✅ Task 1.3: Comprehensive test suite (62 tests)

### ⏭️ Phase 2: Seed Data & Test Data
- ⏭️ Task 2.1: Seed data scripts (pending)
- ⏭️ Task 2.2: Database reset script (pending)

### ⏭️ Phase 3: Database Utilities & Helpers
- ⏭️ Task 3.1: Database connection helper (pending)
- ⏭️ Task 3.2: Query helpers (pending)
- ⏭️ Task 3.3: Database documentation updates (pending)

---

**Last Updated**: 2025-12-14  
**Status**: 🟢 In Progress  
**Next Step**: Begin Task 2.1 - Create Seed Data Scripts
