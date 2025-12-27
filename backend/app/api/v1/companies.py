"""
Companies API endpoints.
"""
from fastapi import APIRouter, Query, Path, HTTPException, Body
from typing import Optional
from app.models import CompanyCreate, CompanyUpdate, CompanyResponse
from app.database import queries as db
from app.database.validators import validate_sort_field
from app.utils.errors import NotFoundError, ValidationError

router = APIRouter()


@router.get("", response_model=dict)
async def list_companies(
    page: int = Query(1, ge=1),
    limit: int = Query(50, ge=1, le=100),
    job_type: Optional[str] = Query(None),
    sort: str = Query("created_on"),
    order: str = Query("desc", pattern="^(asc|desc)$"),
    include_deleted: bool = Query(False)
):
    """List companies with pagination and filtering."""
    try:
        # Validate sort field to prevent SQL injection
        validated_sort = validate_sort_field("company", sort)
        return db.list_companies(
            page=page,
            limit=limit,
            job_type=job_type,
            sort=validated_sort,
            order=order,
            include_deleted=include_deleted
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.get("/{company_id}", response_model=CompanyResponse)
async def get_company(company_id: int = Path(...)):
    """Get a single company by ID."""
    company = db.get_company_by_id(company_id)
    if not company:
        raise NotFoundError("Company", company_id)
    return company


@router.post("", response_model=CompanyResponse, status_code=201)
async def create_company(company: CompanyCreate):
    """Create a new company."""
    try:
        data = company.model_dump()
        return db.create_company(data)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.put("/{company_id}", response_model=CompanyResponse)
async def update_company(
    company_id: int = Path(...),
    company: CompanyUpdate = Body(...)
):
    """Update a company."""
    try:
        data = company.model_dump(exclude_unset=True)
        return db.update_company(company_id, data)
    except NotFoundError:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.delete("/{company_id}", status_code=204)
async def delete_company(company_id: int = Path(...)):
    """Delete a company (hard delete with cascading).
    
    ⚠️ Warning: This permanently deletes the company and:
    - Sets application.company_id to NULL for linked applications
    - Sets contact.company_id to NULL for linked contacts
    """
    try:
        db.delete_company(company_id)
        return None
    except NotFoundError:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
