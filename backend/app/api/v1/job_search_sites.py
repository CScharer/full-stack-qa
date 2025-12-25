"""
Job Search Sites API endpoints.
"""
from fastapi import APIRouter, Query, Path, HTTPException, Body
from typing import Optional
from app.models import JobSearchSiteCreate, JobSearchSiteUpdate, JobSearchSiteResponse
from app.database import queries as db
from app.database.validators import validate_sort_field
from app.utils.errors import NotFoundError, ConflictError, ValidationError

router = APIRouter()


@router.get("", response_model=dict)
async def list_job_search_sites(
    page: int = Query(1, ge=1),
    limit: int = Query(50, ge=1, le=100),
    sort: str = Query("created_on"),
    order: str = Query("desc", regex="^(asc|desc)$"),
    include_deleted: bool = Query(False)
):
    """List job search sites with pagination."""
    try:
        # Validate sort field to prevent SQL injection
        validated_sort = validate_sort_field("job_search_site", sort)
        return db.list_job_search_sites(
            page=page,
            limit=limit,
            sort=validated_sort,
            order=order,
            include_deleted=include_deleted
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.get("/{site_id}", response_model=JobSearchSiteResponse)
async def get_job_search_site(site_id: int = Path(...)):
    """Get a single job search site by ID."""
    site = db.get_job_search_site_by_id(site_id)
    if not site:
        raise NotFoundError("JobSearchSite", site_id)
    return site


@router.post("", response_model=JobSearchSiteResponse, status_code=201)
async def create_job_search_site(site: JobSearchSiteCreate):
    """Create a new job search site."""
    try:
        data = site.model_dump()
        return db.create_job_search_site(data)
    except ConflictError:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.put("/{site_id}", response_model=JobSearchSiteResponse)
async def update_job_search_site(
    site_id: int = Path(...),
    site: JobSearchSiteUpdate = Body(...)
):
    """Update a job search site."""
    try:
        data = site.model_dump(exclude_unset=True)
        return db.update_job_search_site(site_id, data)
    except NotFoundError:
        raise
    except ConflictError:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.delete("/{site_id}", status_code=204)
async def delete_job_search_site(site_id: int = Path(...)):
    """Delete a job search site (hard delete).
    
    ⚠️ Warning: This permanently deletes the job search site.
    """
    try:
        db.delete_job_search_site(site_id)
        return None
    except NotFoundError:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
