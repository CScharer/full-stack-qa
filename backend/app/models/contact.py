"""
Pydantic models for Contact entity.
"""
from pydantic import BaseModel, Field, ConfigDict
from typing import Optional, List
from datetime import datetime


class ContactEmailBase(BaseModel):
    """Base model for contact email."""
    email: str = Field(..., description="Email address")
    email_type: str = Field(default="Work", description="Email type (Personal, Work)")
    is_primary: int = Field(default=0, description="Primary email flag (0 or 1)")


class ContactEmailCreate(ContactEmailBase):
    """Model for creating a contact email."""
    pass


class ContactEmailResponse(ContactEmailBase):
    """Model for contact email response."""
    id: int
    contact_id: int
    is_deleted: int = Field(default=0, description="Soft delete flag")
    created_on: datetime
    modified_on: datetime
    created_by: str
    modified_by: str

    model_config = ConfigDict(from_attributes=True)


class ContactPhoneBase(BaseModel):
    """Base model for contact phone."""
    phone: str = Field(..., description="Phone number")
    phone_type: str = Field(default="Cell", description="Phone type (Home, Cell, Work)")
    is_primary: int = Field(default=0, description="Primary phone flag (0 or 1)")


class ContactPhoneCreate(ContactPhoneBase):
    """Model for creating a contact phone."""
    pass


class ContactPhoneResponse(ContactPhoneBase):
    """Model for contact phone response."""
    id: int
    contact_id: int
    is_deleted: int = Field(default=0, description="Soft delete flag")
    created_on: datetime
    modified_on: datetime
    created_by: str
    modified_by: str

    model_config = ConfigDict(from_attributes=True)


class ContactBase(BaseModel):
    """Base model with common contact fields."""
    first_name: str = Field(..., description="Contact first name")
    last_name: str = Field(..., description="Contact last name")
    title: str = Field(default="Recruiter", description="Contact title")
    linkedin: Optional[str] = Field(None, description="LinkedIn profile URL")
    contact_type: str = Field(..., description="Contact type (Recruiter, Manager, Lead, Account Manager)")
    company_id: Optional[int] = Field(None, description="Company ID")
    application_id: Optional[int] = Field(None, description="Application ID")
    client_id: Optional[int] = Field(None, description="Client ID")


class ContactCreate(ContactBase):
    """Model for creating a new contact."""
    created_by: str = Field(..., description="User who created the record")
    modified_by: str = Field(..., description="User who last modified the record")
    emails: Optional[List[ContactEmailCreate]] = Field(default=[], description="Contact emails")
    phones: Optional[List[ContactPhoneCreate]] = Field(default=[], description="Contact phones")


class ContactUpdate(BaseModel):
    """Model for updating a contact."""
    first_name: Optional[str] = None
    last_name: Optional[str] = None
    title: Optional[str] = None
    linkedin: Optional[str] = None
    contact_type: Optional[str] = None
    company_id: Optional[int] = None
    application_id: Optional[int] = None
    client_id: Optional[int] = None
    modified_by: str = Field(..., description="User who is modifying the record")


class ContactResponse(ContactBase):
    """Model for contact response."""
    id: int
    is_deleted: int = Field(default=0, description="Soft delete flag")
    created_on: datetime
    modified_on: datetime
    created_by: str
    modified_by: str

    model_config = ConfigDict(from_attributes=True)


class ContactFullResponse(ContactResponse):
    """Model for contact response with emails and phones."""
    emails: List[ContactEmailResponse] = Field(default=[], description="Contact emails")
    phones: List[ContactPhoneResponse] = Field(default=[], description="Contact phones")
