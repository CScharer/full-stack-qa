"""
Clients API endpoints.
"""
from fastapi import APIRouter, Query, Path, HTTPException, Body
from typing import Optional
from app.models import ClientCreate, ClientUpdate, ClientResponse
from app.database import queries as db
from app.database.validators import validate_sort_field
from app.utils.errors import NotFoundError, ValidationError

router = APIRouter()


@router.get("", response_model=dict)
async def list_clients(
    page: int = Query(1, ge=1),
    limit: int = Query(50, ge=1, le=100),
    sort: str = Query("created_on"),
    order: str = Query("desc", pattern="^(asc|desc)$"),
    include_deleted: bool = Query(False)
):
    """List clients with pagination."""
    try:
        # Validate sort field to prevent SQL injection
        validated_sort = validate_sort_field("client", sort)
        return db.list_clients(
            page=page,
            limit=limit,
            sort=validated_sort,
            order=order,
            include_deleted=include_deleted
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.get("/{client_id}", response_model=ClientResponse)
async def get_client(client_id: int = Path(...)):
    """Get a single client by ID."""
    client = db.get_client_by_id(client_id)
    if not client:
        raise NotFoundError("Client", client_id)
    return client


@router.post("", response_model=ClientResponse, status_code=201)
async def create_client(client: ClientCreate):
    """Create a new client."""
    try:
        data = client.model_dump()
        return db.create_client(data)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.put("/{client_id}", response_model=ClientResponse)
async def update_client(
    client_id: int = Path(...),
    client: ClientUpdate = Body(...)
):
    """Update a client."""
    try:
        data = client.model_dump(exclude_unset=True)
        return db.update_client(client_id, data)
    except NotFoundError:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.delete("/{client_id}", status_code=204)
async def delete_client(client_id: int = Path(...)):
    """Delete a client (hard delete with cascading).
    
    ⚠️ Warning: This permanently deletes the client and:
    - Sets application.client_id to NULL for linked applications
    - Sets contact.client_id to NULL for linked contacts
    """
    try:
        db.delete_client(client_id)
        return None
    except NotFoundError:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
