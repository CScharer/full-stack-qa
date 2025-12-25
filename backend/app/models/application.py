"""
Pydantic models for Application entity.
"""
from pydantic import BaseModel, Field
from typing import Optional
from datetime import datetime


class ApplicationBase(BaseModel):
    """Base model with common application fields."""
    status: str = Field(default="Pending", description="Application status")
    requirement: Optional[str] = Field(None, description="Job requirements")
    work_setting: str = Field(default="Remote", description="Work setting (Remote, Hybrid, On-site)")
    compensation: Optional[str] = Field(None, description="Compensation details")
    position: Optional[str] = Field(None, description="Job position/title")
    job_description: Optional[str] = Field(None, description="Job description")
    job_link: Optional[str] = Field(None, description="Link to job posting")
    location: Optional[str] = Field(None, description="Job location")
    resume: Optional[str] = Field(None, description="Resume used")
    cover_letter: Optional[str] = Field(None, description="Cover letter used")
    entered_iwd: int = Field(default=0, description="Entered IWD flag (0 or 1)")
    date_close: Optional[str] = Field(None, description="Date closed")
    company_id: Optional[int] = Field(None, description="Company ID")
    client_id: Optional[int] = Field(None, description="Client ID")


class ApplicationCreate(ApplicationBase):
    """Model for creating a new application."""
    created_by: str = Field(..., description="User who created the record")
    modified_by: str = Field(..., description="User who last modified the record")


class ApplicationUpdate(BaseModel):
    """Model for updating an application."""
    status: Optional[str] = None
    requirement: Optional[str] = None
    work_setting: Optional[str] = None
    compensation: Optional[str] = None
    position: Optional[str] = None
    job_description: Optional[str] = None
    job_link: Optional[str] = None
    location: Optional[str] = None
    resume: Optional[str] = None
    cover_letter: Optional[str] = None
    entered_iwd: Optional[int] = None
    date_close: Optional[str] = None
    company_id: Optional[int] = None
    client_id: Optional[int] = None
    modified_by: str = Field(..., description="User who is modifying the record")


class ApplicationResponse(ApplicationBase):
    """Model for application response."""
    id: int
    is_deleted: int = Field(default=0, description="Soft delete flag")
    created_on: datetime
    modified_on: datetime
    created_by: str
    modified_by: str

    class Config:
        from_attributes = True
