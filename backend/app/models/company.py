"""
Pydantic models for Company entity.
"""
from pydantic import BaseModel, Field, ConfigDict
from typing import Optional
from datetime import datetime


class CompanyBase(BaseModel):
    """Base model with common company fields."""
    name: str = Field(..., description="Company name")
    address: Optional[str] = Field(None, description="Street address")
    city: Optional[str] = Field(None, description="City")
    state: Optional[str] = Field(None, description="State")
    zip: Optional[str] = Field(None, description="ZIP code")
    country: str = Field(default="United States", description="Country")
    job_type: str = Field(default="Technology", description="Job type/industry")


class CompanyCreate(CompanyBase):
    """Model for creating a new company."""
    created_by: str = Field(..., description="User who created the record")
    modified_by: str = Field(..., description="User who last modified the record")


class CompanyUpdate(BaseModel):
    """Model for updating a company."""
    name: Optional[str] = None
    address: Optional[str] = None
    city: Optional[str] = None
    state: Optional[str] = None
    zip: Optional[str] = None
    country: Optional[str] = None
    job_type: Optional[str] = None
    modified_by: str = Field(..., description="User who is modifying the record")


class CompanyResponse(CompanyBase):
    """Model for company response."""
    id: int
    is_deleted: int = Field(default=0, description="Soft delete flag")
    created_on: datetime
    modified_on: datetime
    created_by: str
    modified_by: str

    model_config = ConfigDict(from_attributes=True)
