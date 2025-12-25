# Database Defaults Reference Table

**Created**: 2025-12-14  
**Purpose**: Reference table for all default values in the database  
**Status**: 📋 Proposed Design

---

## 📊 Default Values Reference

This table lists all default values that should be managed in the `default_value` reference table.

**User Support**: The `default_value` table supports user-specific defaults. Use `user_id = 'system'` for system-wide defaults that apply to all users. Individual users can override these with their own defaults.

| Table Name | Field Name | Default Value | Data Type | User ID | Description | Notes |
|------------|------------|---------------|-----------|---------|-------------|-------|
| application | status | Pending | TEXT | system | Application status | Required field |
| application | work_setting | Remote | TEXT | system | Work setting (Remote, Hybrid, On-site) | Required field |
| application | entered_iwd | 0 | INTEGER | system | Flag for entered into IWD system | Optional field |
| company | country | United States | TEXT | system | Country name | Required field |
| company | job_type | Technology | TEXT | system | Industry/Job type | Required field |
| contact | title | Recruiter | TEXT | system | Contact title/role | Required field |
| contact_email | email_type | Work | TEXT | system | Email type (Personal, Work, Other) | Required field |
| contact_email | is_primary | 0 | INTEGER | system | Boolean: 1 for primary email, 0 for others | Optional field |
| contact_phone | phone_type | Work | TEXT | system | Phone type (Home, Cell, Work, Other) | Required field |
| contact_phone | is_primary | 0 | INTEGER | system | Boolean: 1 for primary phone, 0 for others | Optional field |

**Lookup Logic**:
1. Check for user-specific default (`user_id = current_user`)
2. If not found, fall back to system default (`user_id = 'system'`)
3. If still not found, no default applied

---

## 📝 SQL Seed Data

### System Defaults (user_id = 'system')

```sql
-- Insert system default values (used as fallback for all users)
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

### User-Specific Defaults Example

```sql
-- Example: User john@example.com prefers Hybrid work setting
INSERT INTO "default_value" ("table_name", "field_name", "default_value", "data_type", "user_id", "description", "created_by", "modified_by")
VALUES
    ('application', 'work_setting', 'Hybrid', 'TEXT', 'john@example.com', 'User prefers Hybrid work setting', 'john@example.com', 'john@example.com');

-- Example: User jane@example.com prefers Canada as default country
INSERT INTO "default_value" ("table_name", "field_name", "default_value", "data_type", "user_id", "description", "created_by", "modified_by")
VALUES
    ('company', 'country', 'Canada', 'TEXT', 'jane@example.com', 'User prefers Canada', 'jane@example.com', 'jane@example.com');
```

---

## 🔄 Updates

### Update System Default

To change a system-wide default value:

```sql
-- Update system default value
UPDATE "default_value"
SET 
    "default_value" = 'NewValue',
    "modified_on" = datetime('now', 'localtime'),
    "modified_by" = 'admin@example.com'
WHERE "table_name" = 'application' 
  AND "field_name" = 'status'
  AND "user_id" = 'system'
  AND "is_active" = 1;
```

### Create/Update User-Specific Default

To create or update a user-specific default:

```sql
-- Insert or update user-specific default
INSERT INTO "default_value" ("table_name", "field_name", "default_value", "data_type", "user_id", "description", "created_by", "modified_by")
VALUES
    ('application', 'work_setting', 'Hybrid', 'TEXT', 'user@example.com', 'User prefers Hybrid', 'user@example.com', 'user@example.com')
ON CONFLICT("table_name", "field_name", "user_id", "is_active") 
DO UPDATE SET
    "default_value" = 'Hybrid',
    "modified_on" = datetime('now', 'localtime'),
    "modified_by" = 'user@example.com';
```

### Delete User-Specific Default

To remove a user-specific default (will fall back to system default):

```sql
-- Delete user-specific default (soft delete)
UPDATE "default_value"
SET 
    "is_active" = 0,
    "modified_on" = datetime('now', 'localtime'),
    "modified_by" = 'user@example.com'
WHERE "table_name" = 'application' 
  AND "field_name" = 'work_setting'
  AND "user_id" = 'user@example.com'
  AND "is_active" = 1;
```

---

## 📋 Review Status

Use this table to track review decisions:

| Table | Field | Current Default | Decision | New Value | Notes |
|-------|-------|----------------|----------|-----------|-------|
| application | status | Pending | ⏸️ Pending | - | - |
| application | work_setting | Remote | ⏸️ Pending | - | - |
| application | entered_iwd | 0 | ⏸️ Pending | - | - |
| company | country | United States | ⏸️ Pending | - | - |
| company | job_type | Technology | ⏸️ Pending | - | - |
| contact | title | Recruiter | ⏸️ Pending | - | - |
| contact_email | email_type | Work | ⏸️ Pending | - | - |
| contact_email | is_primary | 0 | ⏸️ Pending | - | - |
| contact_phone | phone_type | Work | ⏸️ Pending | - | - |
| contact_phone | is_primary | 0 | ⏸️ Pending | - | - |

**Decision Options**:
- ✅ Keep Default
- ❌ Remove Default (Require Explicit)
- 🔄 Change Default (specify new value)

---

**Last Updated**: 2025-12-14  
**Status**: 📋 For Review
