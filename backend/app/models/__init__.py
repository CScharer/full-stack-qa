"""
Pydantic models package.
"""
from app.models.application import (
    ApplicationBase,
    ApplicationCreate,
    ApplicationUpdate,
    ApplicationResponse,
)
from app.models.company import (
    CompanyBase,
    CompanyCreate,
    CompanyUpdate,
    CompanyResponse,
)
from app.models.client import (
    ClientBase,
    ClientCreate,
    ClientUpdate,
    ClientResponse,
)
from app.models.contact import (
    ContactBase,
    ContactCreate,
    ContactUpdate,
    ContactResponse,
    ContactFullResponse,
    ContactEmailCreate,
    ContactEmailResponse,
    ContactPhoneCreate,
    ContactPhoneResponse,
)
from app.models.note import (
    NoteBase,
    NoteCreate,
    NoteUpdate,
    NoteResponse,
)
from app.models.job_search_site import (
    JobSearchSiteBase,
    JobSearchSiteCreate,
    JobSearchSiteUpdate,
    JobSearchSiteResponse,
)

__all__ = [
    # Application
    "ApplicationBase",
    "ApplicationCreate",
    "ApplicationUpdate",
    "ApplicationResponse",
    # Company
    "CompanyBase",
    "CompanyCreate",
    "CompanyUpdate",
    "CompanyResponse",
    # Client
    "ClientBase",
    "ClientCreate",
    "ClientUpdate",
    "ClientResponse",
    # Contact
    "ContactBase",
    "ContactCreate",
    "ContactUpdate",
    "ContactResponse",
    "ContactFullResponse",
    "ContactEmailCreate",
    "ContactEmailResponse",
    "ContactPhoneCreate",
    "ContactPhoneResponse",
    # Note
    "NoteBase",
    "NoteCreate",
    "NoteUpdate",
    "NoteResponse",
    # JobSearchSite
    "JobSearchSiteBase",
    "JobSearchSiteCreate",
    "JobSearchSiteUpdate",
    "JobSearchSiteResponse",
]
