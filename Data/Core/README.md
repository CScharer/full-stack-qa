# ONE GOAL Database

**Location**: `/Users/christopherscharer/dev/full-stack-qa/Data/Core/full_stack_qa.db`  
**Created**: 2025-12-14  
**Database Type**: SQLite 3.x  
**Status**: ✅ Created and Ready

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

### Check Tables
```bash

```

### Check Foreign Keys
```bash
sqlite3 Data/Core/full_stack_qa.db "PRAGMA foreign_keys;"
# Should return: 1
```

### View Schema
```bash
sqlite3 Data/Core/full_stack_qa.db ".schema"
```

### View Specific Table
```bash
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
sqlite3 Data/Core/full_stack_qa.db
```

### GUI Tools
- **DB Browser for SQLite** (free, cross-platform)
- **TablePlus** (macOS, paid)
- **DBeaver** (free, cross-platform)

---

## 📝 Next Steps

1. ✅ Database created
2. ⏭️ Add seed data (see `WORK_DATABASE.md`)
3. ⏭️ Populate `default_value` table with system defaults
4. ⏭️ Test relationships and constraints
5. ⏭️ Connect backend API to database

---

**Last Updated**: 2025-12-14  
**Status**: ✅ Ready for Use
