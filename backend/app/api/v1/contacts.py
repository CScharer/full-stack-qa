"""
Contacts API endpoints.
"""
from fastapi import APIRouter, Query, Path, HTTPException, Body
from typing import Optional
from app.models import ContactCreate, ContactUpdate, ContactResponse, ContactFullResponse
from app.database import queries as db
from app.database.validators import validate_sort_field
from app.utils.errors import NotFoundError, ValidationError

router = APIRouter()


@router.get("", response_model=dict)
async def list_contacts(
    page: int = Query(1, ge=1),
    limit: int = Query(50, ge=1, le=100),
    company_id: Optional[int] = Query(None),
    application_id: Optional[int] = Query(None),
    client_id: Optional[int] = Query(None),
    contact_type: Optional[str] = Query(None),
    sort: str = Query("created_on"),
    order: str = Query("desc", regex="^(asc|desc)$"),
    include_deleted: bool = Query(False)
):
    """List contacts with pagination and filtering."""
    try:
        # Validate sort field to prevent SQL injection
        validated_sort = validate_sort_field("contact", sort)
        return db.list_contacts(
            page=page,
            limit=limit,
            company_id=company_id,
            application_id=application_id,
            client_id=client_id,
            contact_type=contact_type,
            sort=validated_sort,
            order=order,
            include_deleted=include_deleted
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.get("/{contact_id}", response_model=ContactFullResponse)
async def get_contact(
    contact_id: int = Path(...),
    include_emails: bool = Query(True),
    include_phones: bool = Query(True)
):
    """Get a single contact by ID with emails and phones."""
    contact = db.get_contact_by_id(contact_id, include_related=True)
    if not contact:
        raise NotFoundError("Contact", contact_id)
    return contact


@router.post("", response_model=ContactFullResponse, status_code=201)
async def create_contact(contact: ContactCreate):
    """Create a new contact with emails and phones."""
    try:
        data = contact.model_dump()
        return db.create_contact(data)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.put("/{contact_id}", response_model=ContactFullResponse)
async def update_contact(
    contact_id: int = Path(...),
    contact: ContactUpdate = Body(...)
):
    """Update a contact."""
    try:
        data = contact.model_dump(exclude_unset=True)
        return db.update_contact(contact_id, data)
    except NotFoundError:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.delete("/{contact_id}", status_code=204)
async def delete_contact(contact_id: int = Path(...)):
    """Delete a contact (hard delete with cascading).
    
    ⚠️ Warning: This permanently deletes the contact and:
    - Deletes all associated contact_email records
    - Deletes all associated contact_phone records
    """
    try:
        db.delete_contact(contact_id)
        return None
    except NotFoundError:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
