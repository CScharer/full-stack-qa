"""
Notes API endpoints.
"""
from fastapi import APIRouter, Query, Path, HTTPException, Body
from typing import Optional
from app.models import NoteCreate, NoteUpdate, NoteResponse
from app.database import queries as db
from app.database.validators import validate_sort_field
from app.utils.errors import NotFoundError, ValidationError

router = APIRouter()


@router.get("", response_model=dict)
async def list_notes(
    application_id: Optional[int] = Query(None, description="Filter by application"),
    page: int = Query(1, ge=1),
    limit: int = Query(50, ge=1, le=100),
    sort: str = Query("created_on"),
    order: str = Query("desc", pattern="^(asc|desc)$"),
    include_deleted: bool = Query(False)
):
    """List notes with pagination and filtering."""
    try:
        # Validate sort field to prevent SQL injection
        validated_sort = validate_sort_field("note", sort)
        return db.list_notes(
            application_id=application_id,
            page=page,
            limit=limit,
            sort=validated_sort,
            order=order,
            include_deleted=include_deleted
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.get("/{note_id}", response_model=NoteResponse)
async def get_note(note_id: int = Path(...)):
    """Get a single note by ID."""
    note = db.get_note_by_id(note_id)
    if not note:
        raise NotFoundError("Note", note_id)
    return note


@router.post("", response_model=NoteResponse, status_code=201)
async def create_note(note: NoteCreate):
    """Create a new note."""
    try:
        data = note.model_dump()
        return db.create_note(data)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.put("/{note_id}", response_model=NoteResponse)
async def update_note(
    note_id: int = Path(...),
    note: NoteUpdate = Body(...)
):
    """Update a note."""
    try:
        data = note.model_dump(exclude_unset=True)
        return db.update_note(note_id, data)
    except NotFoundError:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.delete("/{note_id}", status_code=204)
async def delete_note(note_id: int = Path(...)):
    """Delete a note (hard delete).
    
    ⚠️ Warning: This permanently deletes the note.
    """
    try:
        db.delete_note(note_id)
        return None
    except NotFoundError:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
