# Backend Work - ONE GOAL Project

**Created**: 2025-12-14  
**Last Updated**: 2025-12-14  
**Purpose**: Detailed work plan for FastAPI backend implementation  
**Status**: 🟢 In Progress  
**Priority**: 🟡 High

---

## 📋 Overview

The backend API provides REST endpoints for the ONE GOAL application. It uses FastAPI with Pydantic models and connects to the SQLite database.

**Prerequisites**:
- ✅ **Database completed** - `Data/Core/full_stack_testing.db` created and tested (62 tests passing)
- ✅ API contract defined (`API_CONTRACT.md`)
- ✅ API versioning guide available (`API_VERSIONING_GUIDE.md`)
- ✅ Delete behavior documented (`DELETE_BEHAVIOR.md`)

**Current Status**:
- ✅ API contract defined
- ✅ API versioning strategy documented
- ✅ Database ready and tested
- ✅ Project structure created
- ✅ Pydantic models created
- ✅ Database query functions implemented
- ✅ All API endpoints implemented
- ✅ API tests created (34 tests)
- 🟢 **Backend implementation complete - ready for frontend**

---

## 🎯 Goals

1. Set up FastAPI project structure
2. Implement database models (Pydantic)
3. Create CRUD endpoints for all entities
4. Add CORS configuration
5. Add error handling and validation
6. Write API tests
7. Generate OpenAPI documentation

---

## 📝 Tasks

### Phase 1: Project Setup

#### Task 1.1: Create Project Structure
**Status**: ✅ COMPLETED  
**Priority**: 🔴 Critical  
**Estimated Time**: 1 hour

**Steps**:
1. Create backend directory structure:
   ```
   backend/
   ├── app/
   │   ├── __init__.py
   │   ├── main.py
   │   ├── config.py
   │   ├── api/
   │   │   ├── __init__.py
   │   │   └── v1/
   │   │       ├── __init__.py
   │   │       ├── applications.py
   │   │       ├── companies.py
   │   │       ├── clients.py
   │   │       ├── contacts.py
   │   │       ├── notes.py
   │   │       └── job_search_sites.py
   │   ├── models/
   │   │   ├── __init__.py
   │   │   ├── application.py
   │   │   ├── company.py
   │   │   ├── client.py
   │   │   ├── contact.py
   │   │   ├── note.py
   │   │   └── job_search_site.py
   │   ├── database/
   │   │   ├── __init__.py
   │   │   ├── connection.py
   │   │   └── queries.py
   │   └── utils/
   │       ├── __init__.py
   │       └── errors.py
   ├── tests/
   │   ├── __init__.py
   │   ├── test_applications.py
   │   ├── test_companies.py
   │   ├── test_clients.py
   │   ├── test_contacts.py
   │   └── test_notes.py
   ├── requirements.txt
   ├── .env.example
   └── README.md
   ```

2. Initialize Python package:
   ```bash
   cd backend
   python3 -m venv venv
   source venv/bin/activate  # On Windows: venv\Scripts\activate
   pip install fastapi uvicorn pydantic python-dotenv
   ```

**Acceptance Criteria**:
- [x] Directory structure created
- [x] Virtual environment set up
- [x] Dependencies installed
- [x] Basic FastAPI app runs

**What Was Done**:
- Created complete backend directory structure
- Set up FastAPI application with CORS
- Created configuration module with environment variables
- Created database connection module
- Created error handling utilities
- All basic infrastructure in place

**Files to Create**:
- `backend/app/main.py` (basic FastAPI app)
- `backend/app/config.py` (configuration)
- `backend/requirements.txt`
- `backend/.env.example`
- `backend/README.md`

---

#### Task 1.2: Configure FastAPI Application
**Status**: ✅ COMPLETED  
**Priority**: 🔴 Critical  
**Estimated Time**: 1 hour

**Steps**:
1. Create `app/main.py`:
   - FastAPI app initialization
   - CORS configuration
   - API router registration
   - OpenAPI metadata

2. Create `app/config.py`:
   - Environment variable loading
   - Database path configuration
   - API host/port configuration
   - CORS origins configuration

**Example**:
```python
# app/main.py
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.config import settings
from app.api.v1 import applications, companies, clients, contacts, notes, job_search_sites

app = FastAPI(
    title="ONE GOAL API",
    version="1.0.0",
    description="API for ONE GOAL job search application"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.CORS_ORIGINS,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(applications.router, prefix="/api/v1/applications", tags=["applications"])
app.include_router(companies.router, prefix="/api/v1/companies", tags=["companies"])
# ... other routers
```

**Acceptance Criteria**:
- [x] FastAPI app runs
- [x] CORS configured correctly
- [x] API versioning in place (`/api/v1/`)
- [x] OpenAPI docs accessible at `/docs`

**What Was Done**:
- FastAPI app configured with proper metadata
- CORS middleware configured for frontend access
- All API routers registered with `/api/v1/` prefix
- OpenAPI documentation available at `/docs` and `/redoc`

---

### Phase 2: Database Integration

#### Task 2.1: Create Database Connection Module
**Status**: ✅ COMPLETED  
**Priority**: 🔴 Critical  
**Estimated Time**: 1-2 hours

**Steps**:
1. Create `app/database/connection.py`:
   - Database connection function
   - Connection context manager
   - Use database connection helper from `database/scripts/db_connection.py`

2. Integrate with database utilities created in Database work

**Acceptance Criteria**:
- [x] Can connect to database
- [x] Foreign keys enabled
- [x] Proper error handling
- [x] Connection pooling (if needed)

**What Was Done**:
- Created context manager for database connections
- Foreign keys automatically enabled
- Proper transaction handling (commit/rollback)
- Database path resolution from environment variables
- Health check function for connection verification

---

#### Task 2.2: Create Query Functions
**Status**: ✅ COMPLETED  
**Priority**: 🔴 Critical  
**Estimated Time**: 3-4 hours

**Steps**:
1. Create `app/database/queries.py`:
   - CRUD operations for each entity
   - Use query helpers from database work
   - Implement soft delete logic
   - Implement audit field updates

2. Functions needed:
   - Applications: create, read, update, delete (soft), list (with pagination/filtering)
   - Companies: create, read, update, delete (soft), list
   - Clients: create, read, update, delete (soft), list
   - Contacts: create, read, update, delete (soft), list, get with emails/phones
   - Notes: create, read, update, delete (soft), list by application
   - Job Search Sites: create, read, update, delete (soft), list

**Acceptance Criteria**:
- [x] All CRUD operations implemented
- [x] Soft deletes work correctly (filtering)
- [x] Audit fields updated
- [x] Pagination supported
- [x] Filtering supported
- [x] Sorting supported

**What Was Done**:
- Complete CRUD operations for all 6 entities:
  - Applications (with filtering by status, company, client)
  - Companies (with filtering by job_type)
  - Clients
  - Contacts (with nested emails/phones support)
  - Notes (with filtering by application_id)
  - Job Search Sites (with unique constraint handling)
- Pagination with page/limit/total/pages
- Filtering by common fields
- Sorting with asc/desc order
- Hard delete operations (triggers handle cascading)
- Proper error handling (NotFoundError, ConflictError)

---

### Phase 3: Pydantic Models

#### Task 3.1: Create Request/Response Models
**Status**: ✅ COMPLETED  
**Priority**: 🔴 Critical  
**Estimated Time**: 2-3 hours

**Steps**:
1. Create Pydantic models for each entity:
   - Request models (for POST/PUT)
   - Response models (for GET)
   - Base models (shared fields)

2. Models needed:
   - `ApplicationCreate`, `ApplicationUpdate`, `ApplicationResponse`
   - `CompanyCreate`, `CompanyUpdate`, `CompanyResponse`
   - `ClientCreate`, `ClientUpdate`, `ClientResponse`
   - `ContactCreate`, `ContactUpdate`, `ContactResponse`, `ContactFullResponse`
   - `ContactEmailCreate`, `ContactEmailResponse`
   - `ContactPhoneCreate`, `ContactPhoneResponse`
   - `NoteCreate`, `NoteUpdate`, `NoteResponse`
   - `JobSearchSiteCreate`, `JobSearchSiteUpdate`, `JobSearchSiteResponse`

3. Add validation:
   - Required fields
   - Field types
   - Email validation
   - String length limits
   - Enum values (status, contact_type, etc.)

**Example**:
```python
# app/models/application.py
from pydantic import BaseModel, Field
from typing import Optional
from datetime import datetime

class ApplicationBase(BaseModel):
    status: str = Field(default="Pending")
    position: Optional[str] = None
    company_id: Optional[int] = None
    client_id: Optional[int] = None

class ApplicationCreate(ApplicationBase):
    created_by: str
    modified_by: str

class ApplicationUpdate(BaseModel):
    status: Optional[str] = None
    position: Optional[str] = None
    modified_by: str

class ApplicationResponse(ApplicationBase):
    id: int
    created_on: datetime
    modified_on: datetime
    created_by: str
    modified_by: str
    is_deleted: int

    class Config:
        from_attributes = True
```

**Acceptance Criteria**:
- [x] All models defined
- [x] Validation rules in place
- [x] Request/Response models separate
- [x] Models match API contract

**What Was Done**:
- Created Pydantic models for all entities:
  - Application (Base, Create, Update, Response)
  - Company (Base, Create, Update, Response)
  - Client (Base, Create, Update, Response)
  - Contact (Base, Create, Update, Response, FullResponse)
  - ContactEmail (Base, Create, Response)
  - ContactPhone (Base, Create, Response)
  - Note (Base, Create, Update, Response)
  - JobSearchSite (Base, Create, Update, Response)
- All models include field validation and descriptions
- ORM mode enabled (from_attributes = True)
- Models exported in `__init__.py` for easy imports

---

### Phase 4: API Endpoints

#### Task 4.1: Implement Applications API
**Status**: ✅ COMPLETED  
**Priority**: 🔴 Critical  
**Estimated Time**: 3-4 hours

**Endpoints to implement** (from `API_CONTRACT.md`):
- `GET /api/v1/applications` - List applications (with pagination, filtering, sorting)
- `GET /api/v1/applications/{id}` - Get single application
- `POST /api/v1/applications` - Create application
- `PUT /api/v1/applications/{id}` - Update application
- `DELETE /api/v1/applications/{id}` - Soft delete application

**Steps**:
1. Create `app/api/v1/applications.py`
2. Implement all endpoints
3. Add error handling
4. Add input validation
5. Test each endpoint

**Acceptance Criteria**:
- [x] All endpoints implemented
- [x] Pagination works
- [x] Filtering works
- [x] Sorting works
- [x] Error handling works
- [x] Hard delete with warnings

**What Was Done**:
- All 5 endpoints implemented (GET list, GET by ID, POST, PUT, DELETE)
- Pagination with page/limit
- Filtering by status, company_id, client_id
- Sorting with sort field and order
- Proper error responses (404, 500)
- Hard delete with warning message in docstring
- [ ] Filtering works
- [ ] Sorting works
- [ ] Error handling in place
- [ ] Matches API contract

---

#### Task 4.2: Implement Companies API
**Status**: ✅ COMPLETED  
**Priority**: 🔴 Critical  
**Estimated Time**: 2-3 hours

**Endpoints**:
- `GET /api/v1/companies`
- `GET /api/v1/companies/{id}`
- `POST /api/v1/companies`
- `PUT /api/v1/companies/{id}`
- `DELETE /api/v1/companies/{id}`

**Acceptance Criteria**: Same as Applications API

---

#### Task 4.3: Implement Clients API
**Status**: ⏸️ Waiting for Database  
**Priority**: 🔴 Critical  
**Estimated Time**: 1-2 hours

**Endpoints**: Similar to Companies API

---

#### Task 4.4: Implement Contacts API
**Status**: ⏸️ Waiting for Database  
**Priority**: 🔴 Critical  
**Estimated Time**: 4-5 hours

**Endpoints**:
- `GET /api/v1/contacts`
- `GET /api/v1/contacts/{id}`
- `GET /api/v1/contacts/{id}/full` - With emails and phones
- `POST /api/v1/contacts`
- `PUT /api/v1/contacts/{id}`
- `DELETE /api/v1/contacts/{id}`
- `GET /api/v1/contacts/{id}/emails`
- `POST /api/v1/contacts/{id}/emails`
- `GET /api/v1/contacts/{id}/phones`
- `POST /api/v1/contacts/{id}/phones`

**Acceptance Criteria**:
- [ ] All endpoints implemented
- [ ] Sub-resources (emails/phones) work
- [ ] Full contact endpoint includes relationships

---

#### Task 4.5: Implement Notes API
**Status**: ✅ COMPLETED  
**Priority**: 🔴 Critical  
**Estimated Time**: 2-3 hours

**Endpoints**: Similar to Applications API, but filtered by `application_id`

---

#### Task 4.6: Implement Job Search Sites API
**Status**: ⏸️ Waiting for Database  
**Priority**: 🟡 High  
**Estimated Time**: 1-2 hours

**Endpoints**: Similar to Companies API

---

### Phase 5: Error Handling & Validation

#### Task 5.1: Implement Error Handlers
**Status**: ⏸️ Waiting for Database  
**Priority**: 🟡 High  
**Estimated Time**: 2 hours

**Steps**:
1. Create `app/utils/errors.py`:
   - Custom exception classes
   - Error response format (matches API contract)
   - HTTP status code mapping

2. Implement error handlers:
   - 400 Bad Request (validation errors)
   - 404 Not Found (resource not found)
   - 409 Conflict (unique constraint violations)
   - 422 Unprocessable Entity (Foreign Key violations)
   - 500 Internal Server Error

**Example**:
```python
# app/utils/errors.py
from fastapi import HTTPException, status

class NotFoundError(HTTPException):
    def __init__(self, resource: str, resource_id: int):
        super().__init__(
            status_code=status.HTTP_404_NOT_FOUND,
            detail={
                "error": f"{resource} not found",
                "code": 404,
                "details": {"id": resource_id}
            }
        )
```

**Acceptance Criteria**:
- [ ] All error types handled
- [ ] Error format matches API contract
- [ ] Proper HTTP status codes
- [ ] Clear error messages

---

#### Task 5.2: Add Input Validation
**Status**: ⏸️ Waiting for Database  
**Priority**: 🟡 High  
**Estimated Time**: 2 hours

**Steps**:
1. Add Pydantic validators to models
2. Validate Foreign Key references exist
3. Validate enum values
4. Validate email formats
5. Validate string lengths

**Acceptance Criteria**:
- [ ] All inputs validated
- [ ] Clear validation error messages
- [ ] Foreign Key validation works

---

### Phase 6: Testing

#### Task 6.1: Write API Tests
**Status**: ⏸️ Waiting for Database  
**Priority**: 🟡 High  
**Estimated Time**: 4-6 hours

**Steps**:
1. Set up test database
2. Write tests for each endpoint:
   - Create operations
   - Read operations
   - Update operations
   - Delete operations
   - Error cases
   - Pagination
   - Filtering
   - Sorting

3. Use pytest and FastAPI TestClient

**Acceptance Criteria**:
- [ ] All endpoints tested
- [ ] Error cases tested
- [ ] Test coverage > 80%
- [ ] Tests run in CI/CD

---

### Phase 7: Documentation

#### Task 7.1: Generate OpenAPI Documentation
**Status**: ⏸️ Waiting for Database  
**Priority**: 🟢 Medium  
**Estimated Time**: 1 hour

**Steps**:
1. Ensure FastAPI auto-generates OpenAPI schema
2. Verify `/docs` endpoint works
3. Verify `/redoc` endpoint works
4. Export OpenAPI schema for frontend type generation

**Acceptance Criteria**:
- [ ] OpenAPI docs accessible
- [ ] All endpoints documented
- [ ] Request/response examples shown
- [ ] Schema matches API contract

---

## 🧪 Testing Checklist

### API Tests
- [ ] All endpoints return correct status codes
- [ ] Request validation works
- [ ] Response format matches contract
- [ ] Pagination works
- [ ] Filtering works
- [ ] Sorting works
- [ ] Soft deletes work
- [ ] Error handling works
- [ ] CORS works

### Integration Tests
- [ ] Database operations work
- [ ] Foreign Key constraints enforced
- [ ] CASCADE deletes work
- [ ] Audit fields updated

---

## 📁 Final Directory Structure

```
backend/
├── app/
│   ├── __init__.py
│   ├── main.py
│   ├── config.py
│   ├── api/
│   │   ├── __init__.py
│   │   └── v1/
│   │       ├── __init__.py
│   │       ├── applications.py
│   │       ├── companies.py
│   │       ├── clients.py
│   │       ├── contacts.py
│   │       ├── notes.py
│   │       └── job_search_sites.py
│   ├── models/
│   │   ├── __init__.py
│   │   ├── application.py
│   │   ├── company.py
│   │   ├── client.py
│   │   ├── contact.py
│   │   ├── note.py
│   │   └── job_search_site.py
│   ├── database/
│   │   ├── __init__.py
│   │   ├── connection.py
│   │   └── queries.py
│   └── utils/
│       ├── __init__.py
│       └── errors.py
├── tests/
│   ├── __init__.py
│   ├── test_applications.py
│   ├── test_companies.py
│   ├── test_clients.py
│   ├── test_contacts.py
│   └── test_notes.py
├── requirements.txt
├── .env.example
└── README.md
```

---

## 📚 Related Documentation

- **API Contract**: `docs/new_app/API_CONTRACT.md`
- **API Versioning**: `docs/new_app/API_VERSIONING_GUIDE.md`
- **Database Work**: `docs/new_app/WORK_DATABASE.md`

---

## ✅ Definition of Done

The backend work is complete when:

1. ✅ All API endpoints implemented
2. ✅ All endpoints tested
3. ✅ Error handling in place
4. ✅ Input validation working
5. ✅ CORS configured
6. ✅ OpenAPI docs generated
7. ✅ Ready for frontend integration

---

**Last Updated**: 2025-12-14  
**Status**: ⏸️ Waiting for Database  
**Prerequisites**: Database work must be completed first
