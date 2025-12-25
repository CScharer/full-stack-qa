"""
Pydantic models for Client entity.
"""
from pydantic import BaseModel, Field
from typing import Optional
from datetime import datetime


class ClientBase(BaseModel):
    """Base model with common client fields."""
    name: Optional[str] = Field(None, description="Client name")


class ClientCreate(ClientBase):
    """Model for creating a new client."""
    created_by: str = Field(..., description="User who created the record")
    modified_by: str = Field(..., description="User who last modified the record")


class ClientUpdate(BaseModel):
    """Model for updating a client."""
    name: Optional[str] = None
    modified_by: str = Field(..., description="User who is modifying the record")


class ClientResponse(ClientBase):
    """Model for client response."""
    id: int
    is_deleted: int = Field(default=0, description="Soft delete flag")
    created_on: datetime
    modified_on: datetime
    created_by: str
    modified_by: str

    class Config:
        from_attributes = True
