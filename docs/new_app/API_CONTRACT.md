# API Contract Specification

**Created**: 2025-01-XX  
**Purpose**: Define the complete API contract for the ONE GOAL application before implementation  
**Status**: ✅ Pre-Implementation Contract  
**API Version**: v1  
**Base URL**: `http://localhost:8000/api/v1`

---

## 📋 Table of Contents

1. [Overview](#overview)
2. [Authentication](#authentication)
3. [Common Patterns](#common-patterns)
4. [Applications API](#applications-api)
5. [Companies API](#companies-api)
6. [Clients API](#clients-api)
7. [Contacts API](#contacts-api)
8. [Notes API](#notes-api)
9. [Job Search Sites API](#job-search-sites-api)
10. [Error Handling](#error-handling)
11. [Response Codes](#response-codes)

---

## 🎯 Overview

### API Design Principles

- **RESTful**: Follow REST conventions
- **Versioned**: All endpoints use `/api/v1/` prefix
- **Consistent**: Uniform request/response formats
- **Documented**: OpenAPI/Swagger documentation available
- **Validated**: Input validation on all endpoints
- **Hard Deletes**: Delete operations permanently remove records (with cascading behavior via triggers)
- **Soft Deletes**: `is_deleted` flag available for audit purposes, but delete endpoints perform hard deletes

### Base URL

```
http://localhost:8000/api/v1
```

### Content Type

- **Request**: `application/json`
- **Response**: `application/json`

---

## 🔐 Authentication

**Status**: TBD (for initial implementation, may be optional)

For production-like testing, consider:
- API Key authentication
- JWT tokens
- Basic authentication

**Note**: Initial implementation may skip authentication for simplicity.

---

## 📝 Common Patterns

### Pagination

All list endpoints support pagination:

**Query Parameters**:
- `page` (integer, default: 1) - Page number
- `limit` (integer, default: 50, max: 100) - Items per page

**Response**:
```json
{
  "data": [...],
  "pagination": {
    "page": 1,
    "limit": 50,
    "total": 150,
    "pages": 3
  }
}
```

### Filtering

All list endpoints support filtering by common fields:

**Query Parameters**:
- `status` (string) - Filter by status
- `is_deleted` (boolean, default: false) - Include soft-deleted records
- `created_after` (datetime) - Filter by creation date
- `created_before` (datetime) - Filter by creation date

### Sorting

All list endpoints support sorting:

**Query Parameters**:
- `sort` (string) - Field to sort by (default: `created_on`)
- `order` (string: `asc` | `desc`, default: `desc`) - Sort order

### Soft Deletes

By default, soft-deleted records (`is_deleted = 1`) are excluded. To include them:
- Add `?include_deleted=true` to query parameters

---

## 📦 Applications API

### List Applications

**Endpoint**: `GET /api/v1/applications`

**Query Parameters**:
- `page` (integer, default: 1)
- `limit` (integer, default: 50)
- `status` (string) - Filter by status
- `company_id` (integer) - Filter by company
- `client_id` (integer) - Filter by client
- `sort` (string, default: `created_on`)
- `order` (string: `asc` | `desc`, default: `desc`)
- `include_deleted` (boolean, default: false)

**Response 200**:
```json
{
  "data": [
    {
      "id": 1,
      "status": "Pending",
      "requirement": "5+ years experience",
      "work_setting": "Remote",
      "compensation": "$120k-$150k",
      "position": "Senior Software Engineer",
      "job_description": "We are looking for...",
      "job_link": "https://example.com/job/123",
      "location": "San Francisco, CA",
      "resume": "resume.pdf",
      "cover_letter": "cover_letter.pdf",
      "entered_iwd": 0,
      "date_close": "2025-02-01",
      "company_id": 1,
      "client_id": 2,
      "is_deleted": 0,
      "created_on": "2025-01-01T10:00:00",
      "modified_on": "2025-01-01T10:00:00",
      "created_by": "user@example.com",
      "modified_by": "user@example.com"
    }
  ],
  "pagination": {
    "page": 1,
    "limit": 50,
    "total": 1,
    "pages": 1
  }
}
```

---

### Get Application

**Endpoint**: `GET /api/v1/applications/{id}`

**Path Parameters**:
- `id` (integer) - Application ID

**Response 200**:
```json
{
  "id": 1,
  "status": "Pending",
  "requirement": "5+ years experience",
  "work_setting": "Remote",
  "compensation": "$120k-$150k",
  "position": "Senior Software Engineer",
  "job_description": "We are looking for...",
  "job_link": "https://example.com/job/123",
  "location": "San Francisco, CA",
  "resume": "resume.pdf",
  "cover_letter": "cover_letter.pdf",
  "entered_iwd": 0,
  "date_close": "2025-02-01",
  "company_id": 1,
  "client_id": 2,
  "is_deleted": 0,
  "created_on": "2025-01-01T10:00:00",
  "modified_on": "2025-01-01T10:00:00",
  "created_by": "user@example.com",
  "modified_by": "user@example.com"
}
```

**Response 404**:
```json
{
  "error": "Application not found",
  "code": 404,
  "details": {
    "id": 999
  }
}
```

---

### Create Application

**Endpoint**: `POST /api/v1/applications`

**Request Body**:
```json
{
  "status": "Pending",
  "requirement": "5+ years experience",
  "work_setting": "Remote",
  "compensation": "$120k-$150k",
  "position": "Senior Software Engineer",
  "job_description": "We are looking for...",
  "job_link": "https://example.com/job/123",
  "location": "San Francisco, CA",
  "resume": "resume.pdf",
  "cover_letter": "cover_letter.pdf",
  "entered_iwd": 0,
  "date_close": "2025-02-01",
  "company_id": 1,
  "client_id": 2,
  "created_by": "user@example.com",
  "modified_by": "user@example.com"
}
```

**Required Fields**:
- `status` (string)
- `work_setting` (string)
- `created_by` (string)
- `modified_by` (string)

**Response 201**:
```json
{
  "id": 1,
  "status": "Pending",
  "requirement": "5+ years experience",
  "work_setting": "Remote",
  "compensation": "$120k-$150k",
  "position": "Senior Software Engineer",
  "job_description": "We are looking for...",
  "job_link": "https://example.com/job/123",
  "location": "San Francisco, CA",
  "resume": "resume.pdf",
  "cover_letter": "cover_letter.pdf",
  "entered_iwd": 0,
  "date_close": "2025-02-01",
  "company_id": 1,
  "client_id": 2,
  "is_deleted": 0,
  "created_on": "2025-01-01T10:00:00",
  "modified_on": "2025-01-01T10:00:00",
  "created_by": "user@example.com",
  "modified_by": "user@example.com"
}
```

**Response 400** (Validation Error):
```json
{
  "error": "Validation error",
  "code": 400,
  "details": {
    "field": "status",
    "message": "Status is required"
  }
}
```

---

### Update Application

**Endpoint**: `PUT /api/v1/applications/{id}`

**Path Parameters**:
- `id` (integer) - Application ID

**Request Body**: Same as Create, all fields optional (only provided fields updated)

**Response 200**: Same as Get Application

**Response 404**: Same as Get Application

---

### Delete Application (Hard Delete)

**Endpoint**: `DELETE /api/v1/applications/{id}`

**Path Parameters**:
- `id` (integer) - Application ID

**⚠️ Warning**: This is a **hard delete** that permanently removes the record and cascades to related records:
- **Deletes**: All notes for this application, all application_sync records
- **Updates**: Sets `contact.application_id` to NULL for any contacts linked to this application

**Response 204**: No Content (successful deletion)

**Response 404**:
```json
{
  "error": "Application not found",
  "code": 404,
  "details": {
    "id": 999
  }
}
```

**Frontend Requirements**:
- Must show confirmation dialog with warning message
- Must list all affected records (notes, sync records, contacts)
- User must explicitly confirm deletion

---

## 🏢 Companies API

### List Companies

**Endpoint**: `GET /api/v1/companies`

**Query Parameters**: Same pagination/filtering as Applications

**Response 200**:
```json
{
  "data": [
    {
      "id": 1,
      "name": "Tech Recruiters Inc",
      "address": "123 Main St",
      "city": "San Francisco",
      "state": "CA",
      "zip": "94102",
      "country": "United States",
      "job_type": "Technology",
      "is_deleted": 0,
      "created_on": "2025-01-01T10:00:00",
      "modified_on": "2025-01-01T10:00:00",
      "created_by": "user@example.com",
      "modified_by": "user@example.com"
    }
  ],
  "pagination": {
    "page": 1,
    "limit": 50,
    "total": 1,
    "pages": 1
  }
}
```

---

### Get Company

**Endpoint**: `GET /api/v1/companies/{id}`

**Path Parameters**:
- `id` (integer) - Company ID

**Response 200**: Single company object (same structure as in list)

---

### Create Company

**Endpoint**: `POST /api/v1/companies`

**Request Body**:
```json
{
  "name": "Tech Recruiters Inc",
  "address": "123 Main St",
  "city": "San Francisco",
  "state": "CA",
  "zip": "94102",
  "country": "United States",
  "job_type": "Technology",
  "created_by": "user@example.com",
  "modified_by": "user@example.com"
}
```

**Required Fields**:
- `name` (string)
- `country` (string, default: "United States")
- `job_type` (string, default: "Technology")
- `created_by` (string)
- `modified_by` (string)

**Response 201**: Created company object

---

### Update Company

**Endpoint**: `PUT /api/v1/companies/{id}`

**Path Parameters**:
- `id` (integer) - Company ID

**Request Body**: Same as Create (all fields optional)

**Response 200**: Updated company object

---

### Delete Company (Hard Delete)

**Endpoint**: `DELETE /api/v1/companies/{id}`

**Path Parameters**:
- `id` (integer) - Company ID

**⚠️ Warning**: This is a **hard delete** that permanently removes the record:
- **Updates**: Sets `application.company_id` and `contact.company_id` to NULL for any records linked to this company

**Response 204**: No Content (successful deletion)

**Response 404**: Same as Get Company

**Frontend Requirements**:
- Must show confirmation dialog with warning message
- Must indicate that applications and contacts will be unlinked (not deleted)
- User must explicitly confirm deletion

---

## 🏛️ Clients API

### List Clients

**Endpoint**: `GET /api/v1/clients`

**Query Parameters**: Same pagination/filtering as Applications

**Response 200**:
```json
{
  "data": [
    {
      "id": 1,
      "name": "Google",
      "is_deleted": 0,
      "created_on": "2025-01-01T10:00:00",
      "modified_on": "2025-01-01T10:00:00",
      "created_by": "user@example.com",
      "modified_by": "user@example.com"
    }
  ],
  "pagination": {
    "page": 1,
    "limit": 50,
    "total": 1,
    "pages": 1
  }
}
```

---

### Get Client

**Endpoint**: `GET /api/v1/clients/{id}`

**Path Parameters**:
- `id` (integer) - Client ID

**Response 200**: Single client object

---

### Create Client

**Endpoint**: `POST /api/v1/clients`

**Request Body**:
```json
{
  "name": "Google",
  "created_by": "user@example.com",
  "modified_by": "user@example.com"
}
```

**Required Fields**:
- `created_by` (string)
- `modified_by` (string)

**Response 201**: Created client object

---

### Update Client

**Endpoint**: `PUT /api/v1/clients/{id}`

**Path Parameters**:
- `id` (integer) - Client ID

**Request Body**: Same as Create (all fields optional)

**Response 200**: Updated client object

---

### Delete Client (Hard Delete)

**Endpoint**: `DELETE /api/v1/clients/{id}`

**Path Parameters**:
- `id` (integer) - Client ID

**⚠️ Warning**: This is a **hard delete** that permanently removes the record:
- **Updates**: Sets `application.client_id` and `contact.client_id` to NULL for any records linked to this client

**Response 204**: No Content (successful deletion)

**Response 404**: Same as Get Client

**Frontend Requirements**:
- Must show confirmation dialog with warning message
- Must indicate that applications and contacts will be unlinked (not deleted)
- User must explicitly confirm deletion

---

## 👥 Contacts API

### List Contacts

**Endpoint**: `GET /api/v1/contacts`

**Query Parameters**:
- Same pagination/filtering as Applications
- `contact_type` (string) - Filter by contact type ('Recruiter', 'Manager', 'Lead', 'Account Manager')
- `company_id` (integer) - Filter by company
- `application_id` (integer) - Filter by application
- `client_id` (integer) - Filter by client

**Response 200**:
```json
{
  "data": [
    {
      "id": 1,
      "company_id": 1,
      "application_id": null,
      "client_id": null,
      "name": "John Doe",
      "title": "Senior Recruiter",
      "linkedin": "https://linkedin.com/in/johndoe",
      "contact_type": "Recruiter",
      "is_deleted": 0,
      "created_on": "2025-01-01T10:00:00",
      "modified_on": "2025-01-01T10:00:00",
      "created_by": "user@example.com",
      "modified_by": "user@example.com"
    }
  ],
  "pagination": {
    "page": 1,
    "limit": 50,
    "total": 1,
    "pages": 1
  }
}
```

---

### Get Contact

**Endpoint**: `GET /api/v1/contacts/{id}`

**Path Parameters**:
- `id` (integer) - Contact ID

**Response 200**: Single contact object (same structure as in list)

---

### Get Contact with Emails and Phones

**Endpoint**: `GET /api/v1/contacts/{id}/full`

**Path Parameters**:
- `id` (integer) - Contact ID

**Response 200**:
```json
{
  "id": 1,
  "company_id": 1,
  "application_id": null,
  "client_id": null,
  "name": "John Doe",
  "title": "Senior Recruiter",
  "linkedin": "https://linkedin.com/in/johndoe",
  "contact_type": "Recruiter",
  "is_deleted": 0,
  "created_on": "2025-01-01T10:00:00",
  "modified_on": "2025-01-01T10:00:00",
  "created_by": "user@example.com",
  "modified_by": "user@example.com",
  "emails": [
    {
      "id": 1,
      "email": "john.doe@example.com",
      "email_type": "Work",
      "is_primary": 1,
      "is_deleted": 0
    }
  ],
  "phones": [
    {
      "id": 1,
      "phone": "+1-555-123-4567",
      "phone_type": "Work",
      "is_primary": 1,
      "is_deleted": 0
    }
  ]
}
```

---

### Create Contact

**Endpoint**: `POST /api/v1/contacts`

**Request Body**:
```json
{
  "company_id": 1,
  "application_id": null,
  "client_id": null,
  "name": "John Doe",
  "title": "Senior Recruiter",
  "linkedin": "https://linkedin.com/in/johndoe",
  "contact_type": "Recruiter",
  "created_by": "user@example.com",
  "modified_by": "user@example.com"
}
```

**Required Fields**:
- `name` (string)
- `title` (string, default: "Recruiter")
- `contact_type` (string: 'Recruiter' | 'Manager' | 'Lead' | 'Account Manager')
- `created_by` (string)
- `modified_by` (string)

**Response 201**: Created contact object

---

### Update Contact

**Endpoint**: `PUT /api/v1/contacts/{id}`

**Path Parameters**:
- `id` (integer) - Contact ID

**Request Body**: Same as Create (all fields optional)

**Response 200**: Updated contact object

---

### Delete Contact (Hard Delete)

**Endpoint**: `DELETE /api/v1/contacts/{id}`

**Path Parameters**:
- `id` (integer) - Contact ID

**⚠️ Warning**: This is a **hard delete** that permanently removes the record and cascades to related records:
- **Deletes**: All email addresses and phone numbers for this contact

**Response 204**: No Content (successful deletion)

**Response 404**: Same as Get Contact

**Frontend Requirements**:
- Must show confirmation dialog with warning message
- Must list all affected records (emails, phones)
- User must explicitly confirm deletion

---

### Contact Emails Sub-resource

#### List Contact Emails

**Endpoint**: `GET /api/v1/contacts/{id}/emails`

**Path Parameters**:
- `id` (integer) - Contact ID

**Response 200**:
```json
{
  "data": [
    {
      "id": 1,
      "contact_id": 1,
      "email": "john.doe@example.com",
      "email_type": "Work",
      "is_primary": 1,
      "is_deleted": 0,
      "created_on": "2025-01-01T10:00:00",
      "modified_on": "2025-01-01T10:00:00",
      "created_by": "user@example.com",
      "modified_by": "user@example.com"
    }
  ]
}
```

#### Add Contact Email

**Endpoint**: `POST /api/v1/contacts/{id}/emails`

**Path Parameters**:
- `id` (integer) - Contact ID

**Request Body**:
```json
{
  "email": "john.doe@example.com",
  "email_type": "Work",
  "is_primary": 1,
  "created_by": "user@example.com",
  "modified_by": "user@example.com"
}
```

**Required Fields**:
- `email` (string, must be valid email format)
- `email_type` (string: 'Personal' | 'Work' | 'Other', default: 'Work')
- `created_by` (string)
- `modified_by` (string)

**Response 201**: Created email object

---

### Contact Phones Sub-resource

#### List Contact Phones

**Endpoint**: `GET /api/v1/contacts/{id}/phones`

**Path Parameters**:
- `id` (integer) - Contact ID

**Response 200**: Array of phone objects (same structure as emails)

#### Add Contact Phone

**Endpoint**: `POST /api/v1/contacts/{id}/phones`

**Path Parameters**:
- `id` (integer) - Contact ID

**Request Body**:
```json
{
  "phone": "+1-555-123-4567",
  "phone_type": "Work",
  "is_primary": 1,
  "created_by": "user@example.com",
  "modified_by": "user@example.com"
}
```

**Required Fields**:
- `phone` (string)
- `phone_type` (string: 'Home' | 'Cell' | 'Work' | 'Other', default: 'Work')
- `created_by` (string)
- `modified_by` (string)

**Response 201**: Created phone object

---

## 📝 Notes API

### List Notes

**Endpoint**: `GET /api/v1/notes`

**Query Parameters**:
- Same pagination/filtering as Applications
- `application_id` (integer) - Filter by application (required)

**Response 200**:
```json
{
  "data": [
    {
      "id": 1,
      "application_id": 1,
      "note": "Had initial phone screen, went well.",
      "is_deleted": 0,
      "created_on": "2025-01-01T10:00:00",
      "modified_on": "2025-01-01T10:00:00",
      "created_by": "user@example.com",
      "modified_by": "user@example.com"
    }
  ],
  "pagination": {
    "page": 1,
    "limit": 50,
    "total": 1,
    "pages": 1
  }
}
```

---

### Get Note

**Endpoint**: `GET /api/v1/notes/{id}`

**Path Parameters**:
- `id` (integer) - Note ID

**Response 200**: Single note object

---

### Create Note

**Endpoint**: `POST /api/v1/notes`

**Request Body**:
```json
{
  "application_id": 1,
  "note": "Had initial phone screen, went well.",
  "created_by": "user@example.com",
  "modified_by": "user@example.com"
}
```

**Required Fields**:
- `application_id` (integer)
- `note` (string)
- `created_by` (string)
- `modified_by` (string)

**Response 201**: Created note object

---

### Update Note

**Endpoint**: `PUT /api/v1/notes/{id}`

**Path Parameters**:
- `id` (integer) - Note ID

**Request Body**: Same as Create (all fields optional)

**Response 200**: Updated note object

---

### Delete Note (Hard Delete)

**Endpoint**: `DELETE /api/v1/notes/{id}`

**Path Parameters**:
- `id` (integer) - Note ID

**⚠️ Warning**: This is a **hard delete** that permanently removes the record.

**Response 204**: No Content (successful deletion)

**Response 404**: Same as Get Note

**Frontend Requirements**:
- Must show confirmation dialog
- User must explicitly confirm deletion

---

## 🔍 Job Search Sites API

### List Job Search Sites

**Endpoint**: `GET /api/v1/job-search-sites`

**Query Parameters**: Same pagination/filtering as Applications

**Response 200**:
```json
{
  "data": [
    {
      "id": 1,
      "site_name": "LinkedIn",
      "is_deleted": 0,
      "created_on": "2025-01-01T10:00:00",
      "modified_on": "2025-01-01T10:00:00",
      "created_by": "user@example.com",
      "modified_by": "user@example.com"
    }
  ],
  "pagination": {
    "page": 1,
    "limit": 50,
    "total": 1,
    "pages": 1
  }
}
```

---

### Get Job Search Site

**Endpoint**: `GET /api/v1/job-search-sites/{id}`

**Path Parameters**:
- `id` (integer) - Site ID

**Response 200**: Single site object

---

### Create Job Search Site

**Endpoint**: `POST /api/v1/job-search-sites`

**Request Body**:
```json
{
  "site_name": "LinkedIn",
  "created_by": "user@example.com",
  "modified_by": "user@example.com"
}
```

**Required Fields**:
- `site_name` (string, must be unique)
- `created_by` (string)
- `modified_by` (string)

**Response 201**: Created site object

---

### Update Job Search Site

**Endpoint**: `PUT /api/v1/job-search-sites/{id}`

**Path Parameters**:
- `id` (integer) - Site ID

**Request Body**: Same as Create (all fields optional)

**Response 200**: Updated site object

---

### Delete Job Search Site (Hard Delete)

**Endpoint**: `DELETE /api/v1/job-search-sites/{id}`

**Path Parameters**:
- `id` (integer) - Site ID

**⚠️ Warning**: This is a **hard delete** that permanently removes the record.

**Response 204**: No Content (successful deletion)

**Response 404**: Same as Get Job Search Site

**Frontend Requirements**:
- Must show confirmation dialog
- User must explicitly confirm deletion

---

## ⚠️ Error Handling

### Error Response Format

All errors follow this format:

```json
{
  "error": "Error message",
  "code": 400,
  "details": {
    "field": "field_name",
    "message": "Specific error message"
  }
}
```

### Common Error Codes

- **400 Bad Request**: Validation error, invalid input
- **404 Not Found**: Resource not found
- **409 Conflict**: Unique constraint violation (e.g., duplicate site_name)
- **422 Unprocessable Entity**: Foreign key constraint violation
- **500 Internal Server Error**: Server error

### Validation Errors

When multiple fields fail validation:

```json
{
  "error": "Validation error",
  "code": 400,
  "details": [
    {
      "field": "status",
      "message": "Status is required"
    },
    {
      "field": "created_by",
      "message": "created_by is required"
    }
  ]
}
```

---

## 📊 Response Codes

| Code | Meaning | Usage |
|------|---------|-------|
| 200 | OK | Successful GET, PUT, DELETE |
| 201 | Created | Successful POST |
| 400 | Bad Request | Validation error, invalid input |
| 404 | Not Found | Resource doesn't exist |
| 409 | Conflict | Unique constraint violation |
| 422 | Unprocessable Entity | Foreign key constraint violation |
| 500 | Internal Server Error | Server error |

---

## ✅ Implementation Checklist

- [ ] Set up FastAPI project structure
- [ ] Implement database models (Pydantic)
- [ ] Create CRUD endpoints for all entities
- [ ] Add pagination support
- [ ] Add filtering and sorting
- [ ] Add input validation
- [ ] Add error handling
- [ ] Add CORS configuration
- [ ] Generate OpenAPI/Swagger docs
- [ ] Write API tests
- [ ] Document authentication (if needed)

---

**Last Updated**: 2025-01-XX  
**Status**: ✅ Pre-Implementation Contract  
**Next Step**: Begin FastAPI implementation using this contract
