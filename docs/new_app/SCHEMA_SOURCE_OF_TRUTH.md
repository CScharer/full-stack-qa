# Schema Source of Truth

**Created**: 2025-01-XX  
**Purpose**: Document the single source of truth for the ONE GOAL database schema  
**Status**: ✅ **CANONICAL SCHEMA DEFINITION**

---

## 🎯 Single Source of Truth

**The canonical database schema is defined in:**
```
docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql
```

**This is the ONLY authoritative source for the database schema.**

---

## 📋 Schema File Details

### File Location
- **Path**: `/docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql`
- **Type**: SQLite DDL (Data Definition Language)
- **Database**: SQLite 3.x
- **Encoding**: UTF-8

### Schema Version
- **Version**: 1.0.0
- **Last Updated**: 2025-01-XX
- **Status**: ✅ Production Ready

---

## ✅ What This Schema Includes

### Core Tables
1. **application** - Job applications
2. **company** - Recruiting firms/companies
3. **client** - Client companies (where jobs are located)
4. **contact** - Recruiters, managers, leads, account managers
5. **contact_email** - Multiple emails per contact
6. **contact_phone** - Multiple phone numbers per contact
7. **note** - Application notes
8. **job_search_site** - Reference data for job search sites
9. **application_sync** - SQLite ↔ MongoDB synchronization

### Features
- ✅ **Foreign Key constraints** on all relationships
- ✅ **CASCADE deletes** on related tables
- ✅ **Soft deletes** (`is_deleted` flag) on all tables
- ✅ **Audit logging** (`created_by`, `modified_by`, `created_on`, `modified_on`)
- ✅ **Performance indexes** on Foreign Keys and frequently queried columns
- ✅ **Standard naming** (no `t_` prefixes)

---

## 🚫 Other Schema Files (Reference Only)

The following files are **NOT** the source of truth and should **NOT** be used for schema creation:

### Deprecated/Reference Files
- `ONE_GOAL.sql` - ❌ **OLD/INCORRECT** - Contains errors, do not use
- `ONE_GOAL.json` - ❌ **REFERENCE ONLY** - Used by Python script for Excel export
- `ONE_GOAL.md` - ❌ **DOCUMENTATION ONLY** - Contains original `t_JobSearch` table reference

**These files are kept for reference and historical purposes only.**

---

## 📖 How to Use This Schema

### Creating the Database

```bash
# Navigate to project root
cd /path/to/full-stack-qa

# Create database from schema
sqlite3 full_stack_testing.db < docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql

# Verify schema
sqlite3 full_stack_testing.db ".schema"
```

### Python Script Usage

The `ONE_GOAL.py` script reads from `ONE_GOAL.json` (reference data) but the actual database should be created from `ONE_GOAL_SCHEMA_CORRECTED.sql`.

---

## 🔄 Schema Changes

### Making Changes

1. **Edit ONLY** `ONE_GOAL_SCHEMA_CORRECTED.sql`
2. **Test** the schema changes:
   ```bash
   sqlite3 :memory: < docs/new_app/ONE_GOAL_SCHEMAT_CORRECTED.sql
   ```
3. **Document** changes in commit message
4. **Update** this document if structure changes significantly

### Version Control

- All schema changes must be committed to Git
- Use descriptive commit messages explaining the change
- Consider creating migration scripts for production databases

---

## 📊 Schema Validation

### Validation Checklist

- [x] All Foreign Keys properly defined
- [x] All data types correct (INTEGER for FKs, not TIMESTAMP)
- [x] All indexes created
- [x] Soft delete flags on all tables
- [x] Audit fields on all tables
- [x] No `t_` prefixes (standard naming)
- [x] All fields from `t_JobSearch` mapped

### Validation Command

```bash
# Validate schema syntax
python3 -c "
import sqlite3
conn = sqlite3.connect(':memory:')
conn.executescript(open('docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql').read())
print('✅ Schema validation: PASSED')
tables = [row[0] for row in conn.execute(\"SELECT name FROM sqlite_master WHERE type='table'\").fetchall()]
print(f'✅ Tables created: {len(tables)}')
"
```

---

## 🔗 Related Documentation

- **Entity Relationships**: See `ENTITY_RELATIONSHIPS.md`
- **API Contract**: See `API_CONTRACT.md` or OpenAPI spec
- **Schema Review**: See `ONE_GOAL_AI_THOUGHTS.md`
- **API Versioning**: See `API_VERSIONING_GUIDE.md`

---

## ✅ Checklist for Developers

When working with the schema:

- [ ] Always use `ONE_GOAL_SCHEMA_CORRECTED.sql` as the source
- [ ] Never create tables from `ONE_GOAL.sql` or other files
- [ ] Validate schema changes before committing
- [ ] Update related documentation if schema changes
- [ ] Test Foreign Key constraints after changes
- [ ] Verify indexes are created correctly

---

**Last Updated**: 2025-01-XX  
**Maintained By**: Development Team  
**Status**: ✅ Active - Single Source of Truth
