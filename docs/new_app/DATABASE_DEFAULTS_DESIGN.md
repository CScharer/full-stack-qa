# Database Defaults Design - ONE GOAL Project

**Created**: 2025-12-14  
**Purpose**: Design a centralized defaults management system using a reference table  
**Status**: 📋 Design Proposal

---

## 🎯 Overview

Instead of hardcoding default values in the schema, we'll create a centralized defaults reference table that stores all default values. This allows:

- **Single source of truth** for all defaults
- **Easy updates** without schema changes
- **Automatic application** of defaults in application code
- **Audit trail** of default value changes
- **Flexibility** to change defaults without migrations

---

## 📊 Proposed Design

### Option 1: Simple Defaults Table with User Support

```sql
-- Default values reference table with user-specific defaults
CREATE TABLE "default_value" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "table_name" TEXT NOT NULL,
    "field_name" TEXT NOT NULL,
    "default_value" TEXT NOT NULL,
    "data_type" TEXT NOT NULL,  -- 'TEXT', 'INTEGER', 'BOOLEAN'
    "user_id" TEXT NOT NULL DEFAULT 'system',  -- 'system' = default for all users, specific user_id = user-specific
    "description" TEXT,
    "is_active" INTEGER DEFAULT 1,  -- 1 = active, 0 = deprecated
    "created_on" TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
    "modified_on" TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
    "created_by" TEXT NOT NULL,
    "modified_by" TEXT NOT NULL,
    UNIQUE("table_name", "field_name", "user_id", "is_active")
);

-- Index for quick lookups (user-specific first, then system)
CREATE INDEX "idx_default_value_table_field_user" ON "default_value"("table_name", "field_name", "user_id", "is_active");
CREATE INDEX "idx_default_value_user" ON "default_value"("user_id", "is_active");
```

**User Default Logic**:
- `user_id = 'system'` → Default for all users (fallback)
- `user_id = 'user@example.com'` → User-specific default (takes precedence)
- Lookup order: Check user-specific first, then fall back to 'system'

### Option 2: Defaults Table with Versioning and User Support

```sql
-- Default values reference table with versioning and user support
CREATE TABLE "default_value" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "table_name" TEXT NOT NULL,
    "field_name" TEXT NOT NULL,
    "default_value" TEXT NOT NULL,
    "data_type" TEXT NOT NULL,  -- 'TEXT', 'INTEGER', 'BOOLEAN'
    "user_id" TEXT NOT NULL DEFAULT 'system',  -- 'system' = default for all users, specific user_id = user-specific
    "description" TEXT,
    "version" INTEGER DEFAULT 1,  -- Version number for tracking changes
    "is_active" INTEGER DEFAULT 1,  -- 1 = active, 0 = deprecated
    "created_on" TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
    "modified_on" TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
    "created_by" TEXT NOT NULL,
    "modified_by" TEXT NOT NULL,
    UNIQUE("table_name", "field_name", "user_id", "is_active")
);

-- History table for tracking changes
CREATE TABLE "default_value_history" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "default_value_id" INTEGER NOT NULL,
    "old_value" TEXT,
    "new_value" TEXT,
    "user_id" TEXT,  -- Track which user's default changed
    "changed_on" TIMESTAMP NOT NULL DEFAULT (datetime('now', 'localtime')),
    "changed_by" TEXT NOT NULL,
    FOREIGN KEY("default_value_id") REFERENCES "default_value"("id")
);
```

---

## 📋 Default Values Data

### Initial Default Values (System Defaults)

All initial defaults use `user_id = 'system'` to serve as fallback defaults for all users.

| Table Name | Field Name | Default Value | Data Type | User ID | Description |
|------------|------------|---------------|-----------|---------|-------------|
| application | status | Pending | TEXT | system | Application status |
| application | work_setting | Remote | TEXT | system | Work setting (Remote, Hybrid, On-site) |
| application | entered_iwd | 0 | INTEGER | system | Flag for entered into IWD system |
| company | country | United States | TEXT | system | Country name |
| company | job_type | Technology | TEXT | system | Industry/Job type |
| contact | title | Recruiter | TEXT | system | Contact title/role |
| contact_email | email_type | Work | TEXT | system | Email type (Personal, Work, Other) |
| contact_email | is_primary | 0 | INTEGER | system | Boolean: 1 for primary email, 0 for others |
| contact_phone | phone_type | Work | TEXT | system | Phone type (Home, Cell, Work, Other) |
| contact_phone | is_primary | 0 | INTEGER | system | Boolean: 1 for primary phone, 0 for others |

**User-Specific Defaults Example**:
- User `john@example.com` prefers `work_setting = 'Hybrid'` → Creates user-specific default
- User `jane@example.com` prefers `country = 'Canada'` → Creates user-specific default
- Other users without specific defaults → Use system defaults

---

## 🔧 Implementation Approach

### 1. Database Schema Changes

**Remove defaults from schema** (except `created_on`, `modified_on`, `is_deleted`):

```sql
-- Before (with hardcoded defaults)
CREATE TABLE "application" (
    "status" TEXT NOT NULL DEFAULT 'Pending',
    "work_setting" TEXT NOT NULL DEFAULT 'Remote',
    ...
);

-- After (no defaults, except standard ones)
CREATE TABLE "application" (
    "status" TEXT NOT NULL,  -- No default
    "work_setting" TEXT NOT NULL,  -- No default
    ...
);
```

### 2. Application Code Integration

**Backend (FastAPI)** - Apply defaults when creating records:

```python
# app/database/defaults.py
import sqlite3
from typing import Any, Optional

def get_default_value(table_name: str, field_name: str, user_id: str, db_conn: sqlite3.Connection) -> Optional[Any]:
    """Get default value for a table/field combination, checking user-specific first, then system."""
    # First try user-specific default
    cursor = db_conn.execute("""
        SELECT default_value, data_type
        FROM default_value
        WHERE table_name = ? AND field_name = ? AND user_id = ? AND is_active = 1
        LIMIT 1
    """, (table_name, field_name, user_id))
    
    result = cursor.fetchone()
    
    # If no user-specific default, fall back to system default
    if not result:
        cursor = db_conn.execute("""
            SELECT default_value, data_type
            FROM default_value
            WHERE table_name = ? AND field_name = ? AND user_id = 'system' AND is_active = 1
            LIMIT 1
        """, (table_name, field_name))
        result = cursor.fetchone()
    
    if result:
        value, data_type = result
        # Convert based on data type
        if data_type == 'INTEGER':
            return int(value)
        elif data_type == 'BOOLEAN':
            return bool(int(value))
        else:
            return value
    return None

def apply_defaults(table_name: str, data: dict, user_id: str, db_conn: sqlite3.Connection) -> dict:
    """Apply defaults to a data dictionary for a given table, using user-specific or system defaults."""
    for field_name, value in data.items():
        if value is None:
            default = get_default_value(table_name, field_name, user_id, db_conn)
            if default is not None:
                data[field_name] = default
    return data
```

**Usage in API endpoints**:

```python
# app/api/v1/applications.py
from app.database.defaults import apply_defaults

@router.post("/applications")
async def create_application(
    application: ApplicationCreate, 
    current_user: str = Depends(get_current_user),  # Get current user from auth
    db: Session = Depends(get_db)
):
    # Convert Pydantic model to dict
    app_data = application.dict()
    
    # Apply defaults (user-specific first, then system)
    app_data = apply_defaults("application", app_data, current_user, db.connection)
    
    # Create application
    db_application = Application(**app_data)
    db.add(db_application)
    db.commit()
    return db_application
```

### 3. Seed Data Script

**Create seed script for defaults**:

```sql
-- database/seeds/00_defaults.sql
-- System defaults (user_id = 'system') - used as fallback for all users
INSERT INTO "default_value" ("table_name", "field_name", "default_value", "data_type", "user_id", "description", "created_by", "modified_by")
VALUES
    ('application', 'status', 'Pending', 'TEXT', 'system', 'Application status', 'system', 'system'),
    ('application', 'work_setting', 'Remote', 'TEXT', 'system', 'Work setting (Remote, Hybrid, On-site)', 'system', 'system'),
    ('application', 'entered_iwd', '0', 'INTEGER', 'system', 'Flag for entered into IWD system', 'system', 'system'),
    ('company', 'country', 'United States', 'TEXT', 'system', 'Country name', 'system', 'system'),
    ('company', 'job_type', 'Technology', 'TEXT', 'system', 'Industry/Job type', 'system', 'system'),
    ('contact', 'title', 'Recruiter', 'TEXT', 'system', 'Contact title/role', 'system', 'system'),
    ('contact_email', 'email_type', 'Work', 'TEXT', 'system', 'Email type (Personal, Work, Other)', 'system', 'system'),
    ('contact_email', 'is_primary', '0', 'INTEGER', 'system', 'Boolean: 1 for primary email, 0 for others', 'system', 'system'),
    ('contact_phone', 'phone_type', 'Work', 'TEXT', 'system', 'Phone type (Home, Cell, Work, Other)', 'system', 'system'),
    ('contact_phone', 'is_primary', '0', 'INTEGER', 'system', 'Boolean: 1 for primary phone, 0 for others', 'system', 'system');
```

**Example: User-Specific Defaults**

```sql
-- User-specific default example
-- User john@example.com prefers Hybrid work setting
INSERT INTO "default_value" ("table_name", "field_name", "default_value", "data_type", "user_id", "description", "created_by", "modified_by")
VALUES
    ('application', 'work_setting', 'Hybrid', 'TEXT', 'john@example.com', 'User prefers Hybrid work setting', 'john@example.com', 'john@example.com');

-- User jane@example.com prefers Canada as default country
INSERT INTO "default_value" ("table_name", "field_name", "default_value", "data_type", "user_id", "description", "created_by", "modified_by")
VALUES
    ('company', 'country', 'Canada', 'TEXT', 'jane@example.com', 'User prefers Canada', 'jane@example.com', 'jane@example.com');
```

---

## ✅ Benefits

1. **Centralized Management**: All defaults in one place
2. **Easy Updates**: Change defaults without schema migrations
3. **User Personalization**: Users can have their own default preferences
4. **System Fallback**: System defaults apply when user has no preference
5. **Audit Trail**: Track when defaults change (with history table)
6. **Flexibility**: Different defaults for different environments and users
7. **Documentation**: Defaults are self-documenting in the database
8. **Testing**: Easy to test different default scenarios

---

## ⚠️ Considerations

### Pros
- ✅ Flexible and maintainable
- ✅ No schema changes needed to update defaults
- ✅ Can have different defaults per environment
- ✅ **User personalization** - users can customize their defaults
- ✅ **System fallback** - always has a default even if user hasn't set one
- ✅ Audit trail of changes

### Cons
- ⚠️ Requires application code to apply defaults
- ⚠️ Slight performance overhead (lookup on insert - user-specific then system)
- ⚠️ More complex than schema defaults
- ⚠️ Need to ensure defaults are always applied
- ⚠️ Need to handle user authentication/identification

### Mitigation
- Cache default values in application memory (by user_id)
- Use database triggers (SQLite limited support)
- Validate defaults are applied in tests
- Cache lookup results to minimize database queries

---

## 🔄 Alternative: Hybrid Approach

Keep schema defaults for **standard patterns** (like `is_deleted = 0`), use reference table for **business logic defaults**:

- **Schema defaults**: `is_deleted`, `created_on`, `modified_on` (standard patterns)
- **Reference table defaults**: `status`, `work_setting`, `country`, `job_type`, etc. (business logic)

---

## 📋 Implementation Checklist

- [ ] Design defaults table structure
- [ ] Create defaults table in schema
- [ ] Remove hardcoded defaults from schema (except standard ones)
- [ ] Create seed data for defaults
- [ ] Implement defaults lookup function in backend
- [ ] Update API endpoints to apply defaults
- [ ] Add caching for defaults (performance)
- [ ] Write tests for defaults application
- [ ] Document defaults management process

---

## 🎯 Recommendation

**Recommended Approach**: **Option 1 (Simple Defaults Table)** with caching

**Reasoning**:
- Simpler to implement and maintain
- Versioning/history can be added later if needed
- Caching addresses performance concerns
- Good balance of flexibility and simplicity

---

**Last Updated**: 2025-12-14  
**Status**: 📋 Design Proposal  
**Next Step**: Review and approve design, then implement
