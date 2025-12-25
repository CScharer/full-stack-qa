"""
ONE GOAL API - Main FastAPI Application
"""
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.config import settings

# Initialize FastAPI app
app = FastAPI(
    title="ONE GOAL API",
    version="1.0.0",
    description="REST API for ONE GOAL job search application",
    docs_url="/docs",
    redoc_url="/redoc",
    openapi_url="/api/v1/openapi.json",
)

# Configure CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.cors_origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.get("/")
async def root():
    """Root endpoint - API information."""
    return {
        "name": "ONE GOAL API",
        "version": "1.0.0",
        "status": "running",
        "docs": "/docs",
        "api": "/api/v1",
    }


@app.get("/health")
async def health_check():
    """Health check endpoint."""
    return {"status": "healthy"}


# API routers
from app.api.v1 import applications, companies, clients, contacts, notes, job_search_sites

app.include_router(applications.router, prefix="/api/v1/applications", tags=["applications"])
app.include_router(companies.router, prefix="/api/v1/companies", tags=["companies"])
app.include_router(clients.router, prefix="/api/v1/clients", tags=["clients"])
app.include_router(contacts.router, prefix="/api/v1/contacts", tags=["contacts"])
app.include_router(notes.router, prefix="/api/v1/notes", tags=["notes"])
app.include_router(job_search_sites.router, prefix="/api/v1/job-search-sites", tags=["job-search-sites"])


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "app.main:app",
        host=settings.api_host,
        port=settings.api_port,
        reload=settings.api_reload,
    )
