# API Versioning Guide

**Created**: 2025-01-XX  
**Purpose**: Guide for implementing API versioning in the ONE GOAL project  
**Status**: Recommended Best Practice

---

## 🎯 Why API Versioning?

API versioning allows you to:
- Make breaking changes without breaking existing clients
- Support multiple API versions simultaneously
- Provide clear migration paths for clients
- Maintain backward compatibility during transitions

---

## 📋 Recommended Approach: URL-Based Versioning

### Structure
```
/api/v1/applications
/api/v1/applications/{id}
/api/v1/companies
/api/v1/contacts
```

### Benefits
- ✅ Explicit and clear
- ✅ Easy to route in FastAPI
- ✅ Works with all HTTP clients
- ✅ Visible in logs and monitoring

---

## 🏗️ FastAPI Implementation

### Directory Structure
```
backend/
├── app/
│   ├── api/
│   │   ├── v1/
│   │   │   ├── __init__.py
│   │   │   ├── applications.py
│   │   │   ├── companies.py
│   │   │   ├── contacts.py
│   │   │   └── notes.py
│   │   └── __init__.py
│   ├── models/
│   │   └── ...
│   └── main.py
```

### main.py Example
```python
from fastapi import FastAPI
from app.api.v1 import applications, companies, contacts, notes

app = FastAPI(title="ONE GOAL API", version="1.0.0")

# API v1 routes
app.include_router(
    applications.router,
    prefix="/api/v1/applications",
    tags=["applications"]
)
app.include_router(
    companies.router,
    prefix="/api/v1/companies",
    tags=["companies"]
)
app.include_router(
    contacts.router,
    prefix="/api/v1/contacts",
    tags=["contacts"]
)
app.include_router(
    notes.router,
    prefix="/api/v1/notes",
    tags=["notes"]
)
```

### Future v2 Example
```python
# When v2 is needed, create new directory
# app/api/v2/applications.py

# Then include both versions:
app.include_router(
    applications_v1.router,
    prefix="/api/v1/applications",
    tags=["applications-v1"]
)
app.include_router(
    applications_v2.router,
    prefix="/api/v2/applications",
    tags=["applications-v2"]
)
```

---

## 📝 Versioning Strategy

### When to Create a New Version

**Create v2 when:**
- Breaking changes to request/response formats
- Removing endpoints
- Changing authentication/authorization
- Major schema changes

**Don't create v2 for:**
- Adding new endpoints (backward compatible)
- Adding optional fields (backward compatible)
- Bug fixes
- Performance improvements

### Deprecation Policy

1. **Announce deprecation** - Add deprecation header to v1 responses
2. **Support period** - Maintain v1 for 6-12 months
3. **Migration guide** - Provide clear migration documentation
4. **Sunset date** - Set clear end-of-life date

---

## 🔧 Implementation Details

### Response Headers
```python
from fastapi import Response

@router.get("/applications")
async def get_applications(response: Response):
    response.headers["API-Version"] = "1.0.0"
    response.headers["Deprecation"] = "false"  # Set to "true" when deprecating
    # ... rest of endpoint
```

### Version Detection
```python
from fastapi import Request

async def get_api_version(request: Request) -> str:
    """Extract API version from request path"""
    path = request.url.path
    if path.startswith("/api/v1/"):
        return "1.0.0"
    elif path.startswith("/api/v2/"):
        return "2.0.0"
    return "unknown"
```

---

## 📚 Best Practices

1. **Start with v1** - Even if it's the first version, use `/api/v1/`
2. **Document versions** - Include version in OpenAPI/Swagger docs
3. **Version in response** - Include version in response headers
4. **Test both versions** - When v2 exists, test both versions
5. **Clear migration path** - Provide examples for upgrading

---

## 🎯 Example API Endpoints (v1)

### Applications
```
GET    /api/v1/applications              # List all applications
GET    /api/v1/applications/{id}         # Get single application
POST   /api/v1/applications              # Create application
PUT    /api/v1/applications/{id}         # Update application
DELETE /api/v1/applications/{id}         # Soft delete application
```

### Companies
```
GET    /api/v1/companies
GET    /api/v1/companies/{id}
POST   /api/v1/companies
PUT    /api/v1/companies/{id}
DELETE /api/v1/companies/{id}
```

### Contacts
```
GET    /api/v1/contacts
GET    /api/v1/contacts/{id}
POST   /api/v1/contacts
PUT    /api/v1/contacts/{id}
DELETE /api/v1/contacts/{id}
GET    /api/v1/contacts/{id}/emails     # Get contact emails
POST   /api/v1/contacts/{id}/emails      # Add email to contact
GET    /api/v1/contacts/{id}/phones      # Get contact phones
POST   /api/v1/contacts/{id}/phones      # Add phone to contact
```

---

## 🔄 Migration Example

### v1 Response
```json
{
  "id": 1,
  "status": "Pending",
  "position": "Software Engineer"
}
```

### v2 Response (if breaking change needed)
```json
{
  "application": {
    "id": 1,
    "status": "pending",  // lowercase in v2
    "role": "Software Engineer"  // renamed from "position"
  }
}
```

### Migration Guide
```markdown
## Migrating from v1 to v2

### Changes:
1. `position` → `role`
2. Status values are now lowercase
3. Response structure wrapped in `application` object

### Example:
v1: GET /api/v1/applications/1
v2: GET /api/v2/applications/1
```

---

## ✅ Checklist for Implementation

- [ ] Use `/api/v1/` prefix for all endpoints
- [ ] Include version in OpenAPI documentation
- [ ] Add version headers to responses
- [ ] Document versioning strategy
- [ ] Plan for future v2 (when needed)
- [ ] Test version routing
- [ ] Update frontend to use versioned endpoints

---

**Last Updated**: 2025-01-XX  
**Status**: Ready for implementation
