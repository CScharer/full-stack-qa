"""
ONE GOAL API - Main FastAPI Application
"""
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.config import settings

# Import shared config to get API base path
from config.port_config import get_api_base_path

# Get API base path from config (e.g., "/api/v1")
API_BASE_PATH = get_api_base_path()

# Initialize FastAPI app
app = FastAPI(
    title="ONE GOAL API",
    version="1.0.0",
    description="REST API for ONE GOAL job search application",
    docs_url="/docs",
    redoc_url="/redoc",
    openapi_url=f"{API_BASE_PATH}/openapi.json",
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
        "api": API_BASE_PATH,
    }


@app.get("/health")
async def health_check():
    """Health check endpoint."""
    return {"status": "healthy"}


# API routers
from app.api.v1 import applications, companies, clients, contacts, notes, job_search_sites

app.include_router(applications.router, prefix=f"{API_BASE_PATH}/applications", tags=["applications"])
app.include_router(companies.router, prefix=f"{API_BASE_PATH}/companies", tags=["companies"])
app.include_router(clients.router, prefix=f"{API_BASE_PATH}/clients", tags=["clients"])
app.include_router(contacts.router, prefix=f"{API_BASE_PATH}/contacts", tags=["contacts"])
app.include_router(notes.router, prefix=f"{API_BASE_PATH}/notes", tags=["notes"])
app.include_router(job_search_sites.router, prefix=f"{API_BASE_PATH}/job-search-sites", tags=["job-search-sites"])


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "app.main:app",
        host=settings.api_host,
        port=settings.api_port,
        reload=settings.api_reload,
    )
