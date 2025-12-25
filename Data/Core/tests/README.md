# Database Unit Tests

**Location**: `Data/Core/tests/`  
**Purpose**: Unit tests for the ONE GOAL database schema and functionality  
**Status**: ✅ Ready to Run

---

## 📋 Overview

Comprehensive unit tests for the database schema, covering:
- Schema creation and structure
- Foreign Key constraints
- CASCADE delete behavior
- Default values
- Database constraints (NOT NULL, UNIQUE)
- Entity relationships
- Default value table functionality

---

## 🧪 Test Structure

### Test Files

1. **`test_schema.py`** - Schema creation and structure tests
   - Table creation
   - Column definitions
   - Primary keys
   - Indexes
   - Foreign key enablement

2. **`test_foreign_keys.py`** - Foreign Key constraint tests
   - Application → Company FK
   - Application → Client FK
   - Contact → Company/Application/Client FKs
   - Contact Email/Phone → Contact FKs
   - Note → Application FK
   - Application Sync → Application FK

3. **`test_cascade_deletes.py`** - CASCADE delete behavior tests
   - Contact deletion cascades to emails/phones
   - Application deletion cascades to notes
   - Verify no CASCADE where not defined

4. **`test_defaults.py`** - Default value tests
   - `is_deleted` defaults to 0
   - Timestamp defaults (`created_on`, `modified_on`)
   - Application defaults (`status`, `work_setting`)
   - Company defaults (`country`, `job_type`)
   - Contact defaults (`title`)
   - Email/Phone defaults (`email_type`, `phone_type`, `is_primary`)

5. **`test_constraints.py`** - Database constraint tests
   - NOT NULL constraints
   - UNIQUE constraints
   - Required fields

6. **`test_relationships.py`** - Entity relationship tests
   - Application with company and client
   - Contact with multiple emails
   - Contact with multiple phones
   - Application with multiple notes
   - Contact linked to multiple entities

7. **`test_default_value_table.py`** - Default value table tests
   - System defaults
   - User-specific defaults
   - Lookup logic
   - Updates
   - Soft deletes

---

## 🚀 Running Tests

### Prerequisites

```bash
# Install dependencies
pip install -r Data/Core/tests/requirements.txt
```

### Run All Tests

```bash
# From project root
pytest Data/Core/tests/ -v

# With coverage
pytest Data/Core/tests/ --cov=Data/Core/tests --cov-report=html -v
```

### Run Specific Test File

```bash
pytest Data/Core/tests/test_schema.py -v
pytest Data/Core/tests/test_foreign_keys.py -v
pytest Data/Core/tests/test_cascade_deletes.py -v
```

### Run Specific Test

```bash
pytest Data/Core/tests/test_schema.py::TestSchemaCreation::test_all_tables_created -v
```

---

## 🔧 Test Fixtures

### Session-Scoped Fixtures (Run Once)

- **`test_db_path`** - Creates temporary database file
- **`schema_file_path`** - Path to schema SQL file
- **`default_value_schema`** - SQL for default_value table
- **`test_database`** - Fresh database with schema applied (before all tests)

### Function-Scoped Fixtures (Run Per Test)

- **`db_connection`** - Fresh database connection for each test
- **`clean_db`** - Clean database (deletes all data, keeps schema)

---

## 📊 Test Coverage

Tests cover:
- ✅ All 10 tables
- ✅ All Foreign Key relationships
- ✅ All CASCADE delete scenarios
- ✅ All default values
- ✅ All constraints (NOT NULL, UNIQUE)
- ✅ Entity relationships
- ✅ Default value table functionality

---

## 🎯 Test Execution Flow

1. **Before All Tests** (`test_database` fixture):
   - Create temporary database file
   - Apply schema from `ONE_GOAL_SCHEMA_CORRECTED.sql`
   - Add `default_value` table
   - Enable foreign keys

2. **Before Each Test** (`db_connection` fixture):
   - Get fresh connection to test database
   - Enable foreign keys

3. **After Each Test**:
   - Close connection
   - Database state may persist (tests should be independent)

4. **After All Tests**:
   - Remove temporary database file
   - Clean up temporary directory

---

## ✅ Expected Results

All tests should pass:
- Schema tests: Verify structure is correct
- Foreign Key tests: Verify constraints are enforced
- CASCADE tests: Verify cascade behavior works
- Default tests: Verify defaults are applied
- Constraint tests: Verify constraints are enforced
- Relationship tests: Verify relationships work correctly

---

## 📝 Adding New Tests

When adding new tests:

1. Create test file in `Data/Core/tests/`
2. Use fixtures from `conftest.py`
3. Follow naming convention: `test_*.py` and `Test*` classes
4. Use `db_connection` fixture for database access
5. Use `clean_db` fixture if you need a clean state

**Example**:
```python
def test_my_new_feature(db_connection):
    """Test description."""
    # Your test code here
    db_connection.execute("...")
    db_connection.commit()
    # Assertions
```

---

## 🐛 Troubleshooting

### Tests Fail with "Foreign Key Constraint Failed"
- Ensure foreign keys are enabled: `PRAGMA foreign_keys = ON`
- Check that referenced records exist before creating foreign key relationships

### Tests Fail with "No Such Table"
- Verify schema was applied correctly
- Check that `test_database` fixture ran successfully

### Database File Not Found
- Ensure `schema_file_path` fixture can find `ONE_GOAL_SCHEMA_CORRECTED.sql`
- Check file path is correct

---

**Last Updated**: 2025-12-14  
**Status**: ✅ Ready to Run
