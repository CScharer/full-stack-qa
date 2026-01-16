# ONE GOAL Databases

**Database Type**: SQLite 3.x  
**Last Updated**: 2025-12-26

---

## 🔑 Database Types

This project uses **environment-specific databases** for runtime data:

| Database File | Type | Status | Purpose |
|---------------|------|--------|---------|
| `full_stack_qa.db` | 📐 Schema Database | ✅ Exists | **Template only** - Contains canonical schema. Used as reference for creating environment databases. **NEVER used for runtime.** |
| `full_stack_qa_dev.db` | 🔧 Environment Database | ✅ Exists | **Development** - Default database for local development work |
| `full_stack_qa_test.db` | 🔧 Environment Database | ✅ Exists | **Testing** - Used for integration testing and CI/CD |
| `full_stack_qa_prod.db` | 🔧 Environment Database | ⏭️ Optional | **Production** - Used for production deployments (create when needed) |

### Important Rules

1. **Schema Database** (`full_stack_qa.db`):
   - ✅ Contains the canonical database schema
   - ✅ Used as a template for creating environment databases
   - ❌ **NEVER used for runtime data**
   - ❌ **NEVER modified directly**

2. **Environment Databases**:
   - ✅ Used for runtime application data
   - ✅ Separate databases for each environment (dev/test/prod)
   - ✅ Created from schema database template
   - ✅ Can be modified and contain actual data

---

## 📊 Database Summary

### Tables Created: 10

1. **application** - Core job application records
2. **application_sync** - SQLite ↔ MongoDB synchronization
3. **client** - Client companies (where jobs are located)
4. **company** - Recruiting firms/companies
5. **contact** - Recruiters, managers, leads, account managers
6. **contact_email** - Multiple emails per contact
7. **contact_phone** - Multiple phone numbers per contact
8. **default_value** - Centralized defaults management (user-specific support)
9. **job_search_site** - Reference data for job search sites
10. **note** - Application notes

### Indexes Created: 20+

Indexes on:
- Foreign Keys (for performance)
- Status fields
- Contact types
- Soft delete flags (`is_deleted`)
- Default value lookups

### Features

- ✅ **Foreign Keys**: Enabled and enforced
- ✅ **Soft Deletes**: `is_deleted` flag on all tables
- ✅ **Audit Logging**: `created_by`, `modified_by`, `created_on`, `modified_on` on all tables
- ✅ **CASCADE Deletes**: Related records automatically cleaned up
- ✅ **User-Specific Defaults**: `default_value` table supports user personalization

---

## 🔍 Quick Verification

### Check Tables (Development Database)
```bash
sqlite3 Data/Core/full_stack_qa_dev.db ".tables"
# Should show: application, company, client, contact, contact_email, contact_phone, note, job_search_site, application_sync
```

### Check Tables (Schema Database - Reference Only)
```bash
sqlite3 Data/Core/full_stack_qa.db ".tables"
```

### Check Foreign Keys
```bash
# Check development database
sqlite3 Data/Core/full_stack_qa_dev.db "PRAGMA foreign_keys;"
# Should return: 1

# Check schema database (reference)
sqlite3 Data/Core/full_stack_qa.db "PRAGMA foreign_keys;"
# Should return: 1
```

### View Schema
```bash
# View development database schema
sqlite3 Data/Core/full_stack_qa_dev.db ".schema"

# View schema database (reference)
sqlite3 Data/Core/full_stack_qa.db ".schema"
```

### View Specific Table
```bash
# Development database
sqlite3 Data/Core/full_stack_qa_dev.db ".schema application"

# Schema database (reference)
sqlite3 Data/Core/full_stack_qa.db ".schema application"
```

---

## 📚 Related Documentation

- **Schema Source**: `docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql`
- **Schema Documentation**: `docs/new_app/SCHEMA_SOURCE_OF_TRUTH.md`
- **Entity Relationships**: `docs/new_app/ENTITY_RELATIONSHIPS.md`
- **Defaults Design**: `docs/new_app/DATABASE_DEFAULTS_DESIGN.md`
- **Defaults Reference**: `docs/new_app/DATABASE_DEFAULTS_REFERENCE.md`

---

## 🛠️ Database Tools

### SQLite Command Line
```bash
# Open development database (default)
sqlite3 Data/Core/full_stack_qa_dev.db

# Open test database
sqlite3 Data/Core/full_stack_qa_test.db

# Open schema database (reference only)
sqlite3 Data/Core/full_stack_qa.db
```

### GUI Tools
- **DB Browser for SQLite** (free, cross-platform)
- **TablePlus** (macOS, paid)
- **DBeaver** (free, cross-platform)

---

## 📝 Creating Environment Databases

If you need to recreate an environment database from the schema:

```bash
# Create development database
sqlite3 Data/Core/full_stack_qa_dev.db < ../docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql
sqlite3 Data/Core/full_stack_qa_dev.db < ../docs/new_app/DELETE_TRIGGERS.sql

# Create test database
sqlite3 Data/Core/full_stack_qa_test.db < ../docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql
sqlite3 Data/Core/full_stack_qa_test.db < ../docs/new_app/DELETE_TRIGGERS.sql

# Create production database (if needed)
sqlite3 Data/Core/full_stack_qa_prod.db < ../docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql
sqlite3 Data/Core/full_stack_qa_prod.db < ../docs/new_app/DELETE_TRIGGERS.sql
```

## 📝 Next Steps

1. ✅ Schema database created
2. ✅ Environment databases created (dev, test)
3. ⏭️ Add seed data (see `WORK_DATABASE.md`)
4. ⏭️ Populate `default_value` table with system defaults
5. ⏭️ Test relationships and constraints
6. ✅ Backend API connected to environment databases

---

**Last Updated**: 2025-12-26  
**Status**: ✅ Ready for Use
