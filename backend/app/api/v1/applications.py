"""
Applications API endpoints.
"""
from fastapi import APIRouter, Query, Path, HTTPException, Body
from typing import Optional
from app.models import ApplicationCreate, ApplicationUpdate, ApplicationResponse
from app.database import queries as db
from app.database.validators import validate_sort_field
from app.utils.errors import NotFoundError, ValidationError

router = APIRouter()


@router.get("", response_model=dict)
async def list_applications(
    page: int = Query(1, ge=1, description="Page number"),
    limit: int = Query(50, ge=1, le=100, description="Items per page"),
    status: Optional[str] = Query(None, description="Filter by status"),
    company_id: Optional[int] = Query(None, description="Filter by company"),
    client_id: Optional[int] = Query(None, description="Filter by client"),
    sort: str = Query("created_on", description="Sort field"),
    order: str = Query("desc", regex="^(asc|desc)$", description="Sort order"),
    include_deleted: bool = Query(False, description="Include soft-deleted records")
):
    """List applications with pagination and filtering."""
    try:
        # Validate sort field to prevent SQL injection
        validated_sort = validate_sort_field("application", sort)
        result = db.list_applications(
            page=page,
            limit=limit,
            status=status,
            company_id=company_id,
            client_id=client_id,
            sort=validated_sort,
            order=order,
            include_deleted=include_deleted
        )
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.get("/{application_id}", response_model=ApplicationResponse)
async def get_application(
    application_id: int = Path(..., description="Application ID")
):
    """Get a single application by ID."""
    application = db.get_application_by_id(application_id)
    if not application:
        raise NotFoundError("Application", application_id)
    return application


@router.post("", response_model=ApplicationResponse, status_code=201)
async def create_application(application: ApplicationCreate):
    """Create a new application."""
    try:
        data = application.model_dump()
        return db.create_application(data)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.put("/{application_id}", response_model=ApplicationResponse)
async def update_application(
    application_id: int = Path(..., description="Application ID"),
    application: ApplicationUpdate = Body(..., description="Application update data")
):
    """Update an application."""
    try:
        data = application.model_dump(exclude_unset=True)
        return db.update_application(application_id, data)
    except NotFoundError:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.delete("/{application_id}", status_code=204)
async def delete_application(
    application_id: int = Path(..., description="Application ID")
):
    """Delete an application (hard delete with cascading).
    
    ⚠️ Warning: This permanently deletes the application and:
    - Deletes all associated notes
    - Deletes all application_sync records
    - Sets contact.application_id to NULL for linked contacts
    """
    try:
        db.delete_application(application_id)
        return None
    except NotFoundError:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
