# Database Configuration Guide

**Last Updated**: 2025-12-27  
**Status**: ✅ Active  
**Related**: Database configuration is documented in this guide

---

## 📋 Overview

This guide explains the database architecture for the full-stack-qa project, including the distinction between schema databases and environment databases, and how to configure and use them.

---

## 🔑 Key Concepts

### Schema Database vs Environment Databases

<!-- prettier-ignore-start -->
| Type | Purpose | Location | Usage |
| -- | -- | -- | -- |
| **Schema Database** | Single source of truth for database structure | `data/core/full_stack_qa.db` | Reference template only - **NEVER used for runtime** |
| **Environment Databases** | Runtime databases for specific environments | `data/core/full_stack_qa_{env}.db` | Used for actual application data (dev/test/prod) |
<!-- prettier-ignore-end -->

### Important Rules

1. **Schema Database (`full_stack_qa.db`)**:
   - ✅ Contains the canonical database schema
   - ✅ Used as a template for creating environment databases
   - ❌ **NEVER used for runtime data**
   - ❌ **NEVER modified directly**
   - ⚠️ Only ONE schema database exists

2. **Environment Databases**:
   - ✅ Used for runtime application data
   - ✅ Separate databases for each environment (dev/test/prod)
   - ✅ Created from schema database template
   - ✅ Can be modified and contain actual data

---

## 📊 Database Files

### Complete Database Inventory

<!-- prettier-ignore-start -->
| Database File | Type | Status | Purpose | Used By |
| -- | -- | -- | -- | -- |
| `full_stack_qa.db` | 📐 Schema | ✅ Exists | Schema template (read-only) | Schema reference only |
| `pytest_temp_full_stack_qa_{env}.db` | 🧪 Test | 🗑️ Temporary | Auto-created during pytest (environment-aware) | `backend/tests/conftest.py`, `data/core/tests/conftest.py` |
| `full_stack_qa_dev.db` | 🔧 Environment | ✅ Exists | Development runtime data | Backend API (dev), Local scripts |
| `full_stack_qa_test.db` | 🔧 Environment | ✅ Exists | Test runtime data | Integration tests, CI/CD |
| `full_stack_qa_prod.db` | 🔧 Environment | ⏭️ Planned | Production runtime data | Production deployments |
<!-- prettier-ignore-end -->

### Note on Temporary Test Database

**`pytest_temp_full_stack_qa_{env}.db`** is a temporary database created automatically by pytest fixtures. It is:
- ✅ **NOT a persistent file** - Created in a temporary directory and auto-deleted after tests
- ✅ **Environment-aware** - Name includes environment suffix (dev/test/prod)
- ✅ **Default to dev** - Uses `dev` environment if `ENVIRONMENT` variable not set
- ✅ **Prefixed with `pytest_temp_`** - Clearly indicates it's a temporary pytest database

**Examples:**
- `ENVIRONMENT=dev` (or unset) → `pytest_temp_full_stack_qa_dev.db`
- `ENVIRONMENT=test` → `pytest_temp_full_stack_qa_test.db`
- `ENVIRONMENT=prod` → `pytest_temp_full_stack_qa_prod.db`

**Important**: This should NOT be confused with `full_stack_qa_test.db` (the persistent environment database for integration tests).

---

## ⚙️ Configuration

### Environment Variables

The database path is resolved using the following priority order:

1. **`DATABASE_PATH`** (highest priority)
   - Full path to database file
   - Example: `DATABASE_PATH=/custom/path/my_database.db`

2. **`DATABASE_NAME`** + **`DATABASE_DIR`**
   - Database filename and directory
   - Example: `DATABASE_NAME=custom.db DATABASE_DIR=data/core`

3. **`ENVIRONMENT`**
   - Environment name (dev/test/prod)
   - Automatically selects: `full_stack_qa_{env}.db`
   - Example: `ENVIRONMENT=test` → `full_stack_qa_test.db`

4. **Default**
   - Falls back to: `full_stack_qa_dev.db`
   - Location: `data/core/full_stack_qa_dev.db`

### Configuration Examples

```bash
# Development (default)
# Uses: data/core/full_stack_qa_dev.db
# No environment variables needed

# Test environment
ENVIRONMENT=test
# Uses: data/core/full_stack_qa_test.db

# Production environment
ENVIRONMENT=prod
# Uses: data/core/full_stack_qa_prod.db

# Custom path (highest priority)
DATABASE_PATH=/custom/path/my_database.db
# Uses: /custom/path/my_database.db

# Custom name in default directory
DATABASE_NAME=my_custom.db
# Uses: data/core/my_custom.db
```

---

## 🚀 Usage

### Backend Application

The backend automatically uses the appropriate database based on environment variables:

```python
from app.config import get_database_path
from app.database.connection import get_db_connection

# Database path is automatically resolved
db_path = get_database_path()

# Use database connection
with get_db_connection() as conn:
    # Your database operations
    pass
```

### Scripts

Scripts should set the `ENVIRONMENT` variable before running:

```bash
# Development
./scripts/start-be.sh --env dev
# Or: ./scripts/start-be.sh -e dev
# Or: ENVIRONMENT=dev ./scripts/start-be.sh (backward compatible)

# Testing
ENVIRONMENT=test ./scripts/tests/frameworks/run-integration-tests.sh
```

### Testing

Unit tests use temporary databases (auto-created and auto-deleted):

```python
# Tests automatically use temporary databases
# No configuration needed
```

---

## 🛡️ Validation

### Schema Database Protection

The system automatically prevents using the schema database for runtime:

```python
# This will raise ValueError
DATABASE_PATH=data/core/full_stack_qa.db python app.py
# Error: Cannot use schema database 'full_stack_qa.db' for runtime
```

### Logging

Database connections are logged for debugging:

```
INFO: Connecting to database: /path/to/full_stack_qa_dev.db
DEBUG: Database connection established: full_stack_qa_dev.db
```

---

## 📝 Creating Environment Databases

### From Schema Database

Environment databases should be created from the schema database:

```bash
# Create dev database
sqlite3 data/core/full_stack_qa_dev.db < docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql

# Create test database
sqlite3 data/core/full_stack_qa_test.db < docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql

# Create prod database (if needed)
sqlite3 data/core/full_stack_qa_prod.db < docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql
```

### Verify Schema

After creating, verify the schema matches:

```bash
sqlite3 data/core/full_stack_qa_dev.db ".schema" > dev_schema.sql
sqlite3 data/core/full_stack_qa.db ".schema" > schema.sql
diff schema.sql dev_schema.sql
```

---

## 🧪 Testing Configuration

### Test Script

Run the configuration test script:

```bash
cd backend
python tests/test_database_config.py
```

This verifies:
- ✅ Default database path resolution
- ✅ Environment-based selection
- ✅ Environment variable priority
- ✅ Schema database validation

---

## 📚 Related Documentation

- Database configuration details are documented in this guide
- **[Database Schema Source](../../new_app/SCHEMA_SOURCE_OF_TRUTH.md)** - Schema database documentation
- **[Local Development Guide](../setup/LOCAL_DEVELOPMENT.md)** - Development setup
- **[Integration Testing Guide](../testing/INTEGRATION_TESTING.md)** - Testing setup

---

## ⚠️ Common Issues

### Issue: "Database file not found"

**Solution**: Create the environment database from the schema database:
```bash
sqlite3 data/core/full_stack_qa_dev.db < docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql
```

### Issue: "Cannot use schema database for runtime"

**Solution**: Use an environment database instead:
```bash
ENVIRONMENT=dev python app.py
```

### Issue: Wrong database being used

**Solution**: Check environment variables:
```bash
echo $ENVIRONMENT
echo $DATABASE_PATH
echo $DATABASE_NAME
```

---

## 🔄 Next Steps

For implementation details and update plans, see:
- Database configuration details are documented in this guide

---

**Last Updated**: 2025-12-27  
**Maintained By**: Development Team

