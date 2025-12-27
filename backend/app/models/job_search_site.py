"""
Pydantic models for JobSearchSite entity.
"""
from pydantic import BaseModel, Field, ConfigDict
from typing import Optional
from datetime import datetime


class JobSearchSiteBase(BaseModel):
    """Base model with common job search site fields."""
    name: str = Field(..., description="Job search site name")
    url: Optional[str] = Field(None, description="Site URL")


class JobSearchSiteCreate(JobSearchSiteBase):
    """Model for creating a new job search site."""
    created_by: str = Field(..., description="User who created the record")
    modified_by: str = Field(..., description="User who last modified the record")


class JobSearchSiteUpdate(BaseModel):
    """Model for updating a job search site."""
    name: Optional[str] = None
    url: Optional[str] = None
    modified_by: str = Field(..., description="User who is modifying the record")


class JobSearchSiteResponse(JobSearchSiteBase):
    """Model for job search site response."""
    id: int
    is_deleted: int = Field(default=0, description="Soft delete flag")
    created_on: datetime
    modified_on: datetime
    created_by: str
    modified_by: str

    model_config = ConfigDict(from_attributes=True)
