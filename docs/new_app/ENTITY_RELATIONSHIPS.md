# Entity Relationships Documentation

**Created**: 2025-01-XX  
**Purpose**: Clear documentation of all entity relationships in the ONE GOAL database  
**Status**: ✅ Complete

---

## 📊 Entity Relationship Diagram (Text-Based)

```
┌─────────────────┐
│    company      │
│─────────────────│
│ id (PK)         │◄─────┐
│ name            │       │
│ address         │       │
│ city            │       │
│ state           │       │
│ zip             │       │
│ country         │       │
│ job_type        │       │
│ is_deleted      │       │
│ created_on      │       │
│ modified_on     │       │
│ created_by      │       │
│ modified_by     │       │
└─────────────────┘       │
                          │
┌─────────────────┐       │
│    client       │       │
│─────────────────│       │
│ id (PK)         │◄──┐   │
│ name            │   │   │
│ is_deleted      │   │   │
│ created_on      │   │   │
│ modified_on     │   │   │
│ created_by      │   │   │
│ modified_by     │   │   │
└─────────────────┘   │   │
                      │   │
┌─────────────────┐   │   │
│  application    │   │   │
│─────────────────│   │   │
│ id (PK)         │   │   │
│ status          │   │   │
│ requirement     │   │   │
│ work_setting    │   │   │
│ compensation    │   │   │
│ position        │   │   │
│ job_description │   │   │
│ job_link        │   │   │
│ location        │   │   │
│ resume          │   │   │
│ cover_letter    │   │   │
│ entered_iwd     │   │   │
│ date_close      │   │   │
│ company_id (FK) ├───┘   │
│ client_id (FK)  ├───────┘
│ is_deleted      │
│ created_on      │
│ modified_on     │
│ created_by      │
│ modified_by     │
└─────────────────┘
         │
         │ 1:N
         │
         ▼
┌─────────────────┐
│     note        │
│─────────────────│
│ id (PK)         │
│ application_id  │◄──────┐ (FK)
│ note            │       │
│ is_deleted      │       │
│ created_on      │       │
│ modified_on     │       │
│ created_by      │       │
│ modified_by     │       │
└─────────────────┘       │
                          │
┌─────────────────┐       │
│  application_   │       │
│     sync        │       │
│─────────────────│       │
│ id (PK)         │       │
│ sqlite_id (FK)  ├───────┘
│ mongodb_id      │
│ is_deleted      │
│ created_on      │
│ modified_on     │
│ created_by      │
│ modified_by     │
└─────────────────┘

┌─────────────────┐
│    contact      │
│─────────────────│
│ id (PK)         │◄─────┐
│ company_id (FK) ├──────┼──┐
│ application_id  │      │  │ (FK)
│ client_id (FK)  ├──────┘  │
│ name            │         │
│ title           │         │
│ linkedin        │         │
│ contact_type    │         │
│ is_deleted      │         │
│ created_on      │         │
│ modified_on     │         │
│ created_by      │         │
│ modified_by     │         │
└─────────────────┘         │
         │                  │
         │ 1:N              │
         │                  │
    ┌────┴────┐             │
    │         │             │
    ▼         ▼             │
┌─────────┐ ┌─────────┐    │
│contact_ │ │contact_ │    │
│ email   │ │ phone   │    │
│─────────│ │─────────│    │
│id (PK)  │ │id (PK)  │    │
│contact_ │ │contact_ │    │
│id (FK)  │ │id (FK)  │    │
│email    │ │phone    │    │
│email_   │ │phone_   │    │
│type     │ │type     │    │
│is_      │ │is_      │    │
│primary  │ │primary  │    │
│is_      │ │is_      │    │
│deleted  │ │deleted  │    │
│created_ │ │created_ │    │
│on       │ │on       │    │
│modified │ │modified │    │
│_on      │ │_on      │    │
│created_ │ │created_ │    │
│by       │ │by       │    │
│modified │ │modified │    │
│_by      │ │_by      │    │
└─────────┘ └─────────┘    │
                           │
┌─────────────────┐         │
│ job_search_site│         │
│─────────────────│         │
│ id (PK)        │         │
│ site_name      │         │
│ is_deleted     │         │
│ created_on     │         │
│ modified_on    │         │
│ created_by     │         │
│ modified_by    │         │
└─────────────────┘         │
                            │
                            │
(No direct relationships - reference data only)
```

---

## 🔗 Relationship Details

### 1. Company → Application (One-to-Many)

**Relationship**: One company can have many applications

- **Company Side**: `company.id` (Primary Key)
- **Application Side**: `application.company_id` (Foreign Key)
- **Cardinality**: 1:N (One company, many applications)
- **Optional**: Yes (application can exist without company)
- **Cascade**: None (company can't be deleted if applications exist)

**Example**:
```sql
-- Find all applications for a company
SELECT a.* FROM application a
JOIN company c ON a.company_id = c.id
WHERE c.name = 'Tech Recruiters Inc';
```

---

### 2. Client → Application (One-to-Many)

**Relationship**: One client can have many applications

- **Client Side**: `client.id` (Primary Key)
- **Application Side**: `application.client_id` (Foreign Key)
- **Cardinality**: 1:N (One client, many applications)
- **Optional**: Yes (application can exist without client)
- **Cascade**: None

**Example**:
```sql
-- Find all applications for a client
SELECT a.* FROM application a
JOIN client c ON a.client_id = c.id
WHERE c.name = 'Google';
```

---

### 3. Application → Note (One-to-Many)

**Relationship**: One application can have many notes

- **Application Side**: `application.id` (Primary Key)
- **Note Side**: `note.application_id` (Foreign Key)
- **Cardinality**: 1:N (One application, many notes)
- **Optional**: No (note must belong to an application)
- **Cascade**: `ON DELETE CASCADE` (notes deleted when application deleted)

**Example**:
```sql
-- Get all notes for an application
SELECT n.* FROM note n
WHERE n.application_id = 1
AND n.is_deleted = 0;
```

---

### 4. Application → Application Sync (One-to-One)

**Relationship**: One application can have one sync record

- **Application Side**: `application.id` (Primary Key)
- **Sync Side**: `application_sync.sqlite_id` (Foreign Key)
- **Cardinality**: 1:1 (One application, one sync record)
- **Optional**: Yes (application can exist without sync)
- **Cascade**: None

**Example**:
```sql
-- Get sync info for an application
SELECT s.mongodb_id FROM application_sync s
WHERE s.sqlite_id = 1;
```

---

### 5. Contact Relationships

#### 5a. Company → Contact (One-to-Many, Optional)

**Relationship**: One company can have many contacts

- **Company Side**: `company.id` (Primary Key)
- **Contact Side**: `contact.company_id` (Foreign Key)
- **Cardinality**: 1:N (One company, many contacts)
- **Optional**: Yes (contact can exist without company)
- **Cascade**: None

#### 5b. Application → Contact (One-to-Many, Optional)

**Relationship**: One application can have many contacts

- **Application Side**: `application.id` (Primary Key)
- **Contact Side**: `contact.application_id` (Foreign Key)
- **Cardinality**: 1:N (One application, many contacts)
- **Optional**: Yes (contact can exist without application)
- **Cascade**: None

#### 5c. Client → Contact (One-to-Many, Optional)

**Relationship**: One client can have many contacts

- **Client Side**: `client.id` (Primary Key)
- **Contact Side**: `contact.client_id` (Foreign Key)
- **Cardinality**: 1:N (One client, many contacts)
- **Optional**: Yes (contact can exist without client)
- **Cascade**: None

**Note**: A contact can be linked to company, application, and/or client simultaneously.

---

### 6. Contact → Contact Email (One-to-Many)

**Relationship**: One contact can have many emails

- **Contact Side**: `contact.id` (Primary Key)
- **Email Side**: `contact_email.contact_id` (Foreign Key)
- **Cardinality**: 1:N (One contact, many emails)
- **Optional**: No (contact should have at least one email)
- **Cascade**: `ON DELETE CASCADE` (emails deleted when contact deleted)

**Email Types**: 'Personal', 'Work', 'Other'  
**Primary Flag**: `is_primary` (1 = primary, 0 = secondary)

**Example**:
```sql
-- Get primary email for a contact
SELECT email FROM contact_email
WHERE contact_id = 1
AND is_primary = 1
AND is_deleted = 0;
```

---

### 7. Contact → Contact Phone (One-to-Many)

**Relationship**: One contact can have many phone numbers

- **Contact Side**: `contact.id` (Primary Key)
- **Phone Side**: `contact_phone.contact_id` (Foreign Key)
- **Cardinality**: 1:N (One contact, many phones)
- **Optional**: No (contact should have at least one phone)
- **Cascade**: `ON DELETE CASCADE` (phones deleted when contact deleted)

**Phone Types**: 'Home', 'Cell', 'Work', 'Other'  
**Primary Flag**: `is_primary` (1 = primary, 0 = secondary)

**Example**:
```sql
-- Get all phone numbers for a contact
SELECT phone, phone_type, is_primary FROM contact_phone
WHERE contact_id = 1
AND is_deleted = 0
ORDER BY is_primary DESC;
```

---

### 8. Job Search Site (Standalone)

**Relationship**: None (reference data table)

- **Purpose**: Stores list of job search sites (e.g., "LinkedIn", "Indeed", "Monster")
- **Usage**: Reference data, no foreign key relationships
- **Cardinality**: N/A

---

## 📋 Relationship Summary Table

<!-- prettier-ignore-start -->
| Parent Entity | Child Entity | Relationship Type | FK Column | Cascade Delete | Optional |
| -- | -- | -- | -- | -- | -- |
| company | application | One-to-Many | `company_id` | No | Yes |
| client | application | One-to-Many | `client_id` | No | Yes |
| application | note | One-to-Many | `application_id` | Yes | No |
| application | application_sync | One-to-One | `sqlite_id` | No | Yes |
| company | contact | One-to-Many | `company_id` | No | Yes |
| application | contact | One-to-Many | `application_id` | No | Yes |
| client | contact | One-to-Many | `client_id` | No | Yes |
| contact | contact_email | One-to-Many | `contact_id` | Yes | No |
| contact | contact_phone | One-to-Many | `contact_id` | Yes | No |
| job_search_site | (none) | Standalone | N/A | N/A | N/A |
<!-- prettier-ignore-end -->

---

## 🔍 Query Examples

### Get Complete Application with All Relationships

```sql
SELECT 
    a.id,
    a.status,
    a.position,
    c.name AS company_name,
    cl.name AS client_name,
    GROUP_CONCAT(DISTINCT con.name) AS contacts,
    COUNT(DISTINCT n.id) AS note_count
FROM application a
LEFT JOIN company c ON a.company_id = c.id
LEFT JOIN client cl ON a.client_id = cl.id
LEFT JOIN contact con ON con.application_id = a.id
LEFT JOIN note n ON n.application_id = a.id
WHERE a.is_deleted = 0
GROUP BY a.id;
```

### Get Contact with All Emails and Phones

```sql
SELECT 
    c.id,
    c.name,
    c.title,
    ce.email,
    ce.email_type,
    ce.is_primary AS email_primary,
    cp.phone,
    cp.phone_type,
    cp.is_primary AS phone_primary
FROM contact c
LEFT JOIN contact_email ce ON ce.contact_id = c.id AND ce.is_deleted = 0
LEFT JOIN contact_phone cp ON cp.contact_id = c.id AND cp.is_deleted = 0
WHERE c.id = 1
AND c.is_deleted = 0;
```

---

## ⚠️ Important Notes

1. **Soft Deletes**: Always filter by `is_deleted = 0` in queries
2. **Cascade Deletes**: Only `note`, `contact_email`, and `contact_phone` cascade
3. **Optional Relationships**: Most relationships are optional (NULL allowed)
4. **Multiple Relationships**: Contacts can be linked to company, application, and client simultaneously
5. **Primary Flags**: Use `is_primary` to identify preferred email/phone for contacts

---

**Last Updated**: 2025-01-XX  
**Status**: ✅ Complete
