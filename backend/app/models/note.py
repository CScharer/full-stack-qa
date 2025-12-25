"""
Pydantic models for Note entity.
"""
from pydantic import BaseModel, Field
from typing import Optional
from datetime import datetime


class NoteBase(BaseModel):
    """Base model with common note fields."""
    application_id: int = Field(..., description="Application ID")
    note: str = Field(..., description="Note content")


class NoteCreate(NoteBase):
    """Model for creating a new note."""
    created_by: str = Field(..., description="User who created the record")
    modified_by: str = Field(..., description="User who last modified the record")


class NoteUpdate(BaseModel):
    """Model for updating a note."""
    note: Optional[str] = None
    modified_by: str = Field(..., description="User who is modifying the record")


class NoteResponse(NoteBase):
    """Model for note response."""
    id: int
    is_deleted: int = Field(default=0, description="Soft delete flag")
    created_on: datetime
    modified_on: datetime
    created_by: str
    modified_by: str

    class Config:
        from_attributes = True
