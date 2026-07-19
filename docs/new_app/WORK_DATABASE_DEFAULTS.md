# Database Defaults Review - ONE GOAL Project

**Created**: 2025-12-14  
**Purpose**: Review all default values in database schema (excluding `created_on` and `modified_on`)  
**Status**: 📋 For Review

---

## 📋 Overview

This document lists all fields in the database schema that have default values, excluding the timestamp fields `created_on` and `modified_on` which are handled automatically.

**Purpose**: Review each default value to determine if it should remain, be changed, or be removed.

**Note**: See `DATABASE_DEFAULTS_DESIGN.md` for a proposed centralized defaults management system using a reference table instead of hardcoded schema defaults.

---

## 🎯 Proposed Approach: Centralized Defaults Table

Instead of hardcoding defaults in the schema, we can use a **centralized defaults reference table**. This approach:

- ✅ Stores all defaults in one place (`default_value` table)
- ✅ Allows easy updates without schema changes
- ✅ Provides audit trail of default changes
- ✅ Enables different defaults per environment

**See**: `DATABASE_DEFAULTS_DESIGN.md` for complete design proposal.

**Decision Needed**: Should we use centralized defaults table or keep schema defaults?

---

## 📊 Default Values by Table

### 1. `application_sync` Table

**No defaults to review** (excluding `created_on` and `modified_on`)

---

### 2. `application` Table

<!-- prettier-ignore-start -->
| Field | Type | Current Default | Required | Notes |
| -- | -- | -- | -- | -- |
| `status` | TEXT | `'Pending'` | Yes (NOT NULL) | Application status |
| `work_setting` | TEXT | `'Remote'` | Yes (NOT NULL) | Work setting (Remote, Hybrid, On-site) |
| `entered_iwd` | INTEGER | `0` | No | Flag for entered into IWD system |
<!-- prettier-ignore-end -->

**Current Defaults**:
- `status`: `'Pending'` - New applications start as Pending
- `work_setting`: `'Remote'` - Assumes Remote by default
- `entered_iwd`: `0` - Not entered by default

**Questions**:
1. Should `status` default to `'Pending'`, or should it be required (no default)?
2. Should `work_setting` default to `'Remote'`, or should it be required (no default)?
3. Should `entered_iwd` default to `0`, or should it be explicitly set?

---

### 3. `company` Table

<!-- prettier-ignore-start -->
| Field | Type | Current Default | Required | Notes |
| -- | -- | -- | -- | -- |
| `country` | TEXT | `'United States'` | Yes (NOT NULL) | Country name |
| `job_type` | TEXT | `'Technology'` | Yes (NOT NULL) | Industry/Job type |
<!-- prettier-ignore-end -->

**Current Defaults**:
- `country`: `'United States'` - Assumes US companies by default
- `job_type`: `'Technology'` - Assumes Technology industry by default

**Questions**:
1. Should `country` default to `'United States'`, or should it be required (no default)?
2. Should `job_type` default to `'Technology'`, or should it be required (no default)?

---

### 4. `client` Table

**No defaults to review** (excluding `created_on` and `modified_on`)

---

### 5. `contact` Table

<!-- prettier-ignore-start -->
| Field | Type | Current Default | Required | Notes |
| -- | -- | -- | -- | -- |
| `title` | TEXT | `'Recruiter'` | Yes (NOT NULL) | Contact title/role |
<!-- prettier-ignore-end -->

**Current Defaults**:
- `title`: `'Recruiter'` - Assumes Recruiter by default

**Questions**:
1. Should `title` default to `'Recruiter'`, or should it be required (no default)?

**Note**: `contact_type` field does NOT have a default, which means it must be explicitly set. Consider if this should have a default.

---

### 6. `contact_email` Table

<!-- prettier-ignore-start -->
| Field | Type | Current Default | Required | Notes |
| -- | -- | -- | -- | -- |
| `email_type` | TEXT | `'Work'` | Yes (NOT NULL) | Email type (Personal, Work, Other) |
| `is_primary` | INTEGER | `0` | No | Boolean: 1 for primary email, 0 for others |
<!-- prettier-ignore-end -->

**Current Defaults**:
- `email_type`: `'Work'` - Assumes Work email by default
- `is_primary`: `0` - Not primary by default

**Questions**:
1. Should `email_type` default to `'Work'`, or should it be required (no default)?
2. Should `is_primary` default to `0`, or should it be explicitly set?

---

### 7. `contact_phone` Table

<!-- prettier-ignore-start -->
| Field | Type | Current Default | Required | Notes |
| -- | -- | -- | -- | -- |
| `phone_type` | TEXT | `'Work'` | Yes (NOT NULL) | Phone type (Home, Cell, Work, Other) |
| `is_primary` | INTEGER | `0` | No | Boolean: 1 for primary phone, 0 for others |
<!-- prettier-ignore-end -->

**Current Defaults**:
- `phone_type`: `'Work'` - Assumes Work phone by default
- `is_primary`: `0` - Not primary by default

**Questions**:
1. Should `phone_type` default to `'Work'`, or should it be required (no default)?
2. Should `is_primary` default to `0`, or should it be explicitly set?

---

### 8. `note` Table

**No defaults to review** (excluding `created_on` and `modified_on`)

---

### 9. `job_search_site` Table

**No defaults to review** (excluding `created_on` and `modified_on`)

---

## 📝 Summary of All Defaults

### Application Defaults

<!-- prettier-ignore-start -->
| Field | Default | Required |
| -- | -- | -- |
| `status` | `'Pending'` | Yes |
| `work_setting` | `'Remote'` | Yes |
| `entered_iwd` | `0` | No |
<!-- prettier-ignore-end -->

**Questions**:
- Should `status` require explicit value?
- Should `work_setting` require explicit value?
- Should `entered_iwd` require explicit value?

---

### Company Defaults

<!-- prettier-ignore-start -->
| Field | Default | Required |
| -- | -- | -- |
| `country` | `'United States'` | Yes |
| `job_type` | `'Technology'` | Yes |
<!-- prettier-ignore-end -->

**Questions**:
- Should `country` require explicit value?
- Should `job_type` require explicit value?

---

### Contact Defaults

<!-- prettier-ignore-start -->
| Field | Default | Required |
| -- | -- | -- |
| `title` | `'Recruiter'` | Yes |
<!-- prettier-ignore-end -->

**Questions**:
- Should `title` require explicit value?
- Should `contact_type` have a default? (Currently has NO default)

---

### Contact Email Defaults

<!-- prettier-ignore-start -->
| Field | Default | Required |
| -- | -- | -- |
| `email_type` | `'Work'` | Yes |
| `is_primary` | `0` | No |
<!-- prettier-ignore-end -->

**Questions**:
- Should `email_type` require explicit value?
- Should `is_primary` require explicit value?

---

### Contact Phone Defaults

<!-- prettier-ignore-start -->
| Field | Default | Required |
| -- | -- | -- |
| `phone_type` | `'Work'` | Yes |
| `is_primary` | `0` | No |
<!-- prettier-ignore-end -->

**Questions**:
- Should `phone_type` require explicit value?
- Should `is_primary` require explicit value?

---

**Note**: All tables have `is_deleted INTEGER DEFAULT 0` which defaults to Active. This is a standard pattern and does not need review.

---

## 🎯 Review Checklist

For each default value, consider:

- [ ] **Is the default value appropriate?** - Does it make sense for most use cases?
- [ ] **Should it be required instead?** - Would it be better to force explicit values?
- [ ] **Is the default value too specific?** - Does it assume too much (e.g., 'United States', 'Technology')?
- [ ] **Does it match business logic?** - Does the default align with how the application should work?

---

## 💡 Recommendations

### Keep Defaults (Standard Patterns)
- ✅ `is_primary` → `0` (contact_email, contact_phone) - Standard boolean default
- ✅ `is_deleted` → `0` (all tables) - Standard soft delete pattern (Active by default)

### Consider Removing Defaults (Require Explicit Values)
- ⚠️ `status` → `'Pending'` - Consider requiring explicit status
- ⚠️ `work_setting` → `'Remote'` - Consider requiring explicit work setting
- ⚠️ `country` → `'United States'` - Consider requiring explicit country
- ⚠️ `job_type` → `'Technology'` - Consider requiring explicit job type
- ⚠️ `title` → `'Recruiter'` - Consider requiring explicit title
- ⚠️ `email_type` → `'Work'` - Consider requiring explicit email type
- ⚠️ `phone_type` → `'Work'` - Consider requiring explicit phone type

### Consider Adding Defaults
- ⚠️ `contact.contact_type` - Currently has NO default, but is NOT NULL. Consider adding default or making it optional.

---

## 📋 Decision Template

For each field, mark your decision:

```
Field: [field_name]
Table: [table_name]
Current Default: [current_value]
Decision: [ ] Keep Default | [ ] Remove Default (Require Explicit) | [ ] Change Default to: [new_value]
Reason: [your reasoning]
```

---

## ✅ Next Steps

1. Review each default value in this document
2. Make decisions for each field
3. Update `ONE_GOAL_SCHEMA_CORRECTED.sql` with approved changes
4. Update migration scripts if needed
5. Document decisions in this file

---

**Last Updated**: 2025-12-14  
**Status**: 📋 For Review  
**Next Step**: Review defaults and make decisions
