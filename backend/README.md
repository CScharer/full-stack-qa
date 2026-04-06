# ONE GOAL Backend API

FastAPI-based REST API for the ONE GOAL job search application.

## 📋 Overview

This backend provides REST endpoints for managing job applications, companies, clients, contacts, notes, and job search sites.

**Technology Stack**:
- **Framework**: FastAPI
- **Database**: SQLite (environment-based: `full_stack_qa_dev.db` by default)
- **Validation**: Pydantic
- **API Version**: Configurable via `config/environments.json` (default: v1 `/api/v1/*`)

**Note**: The API base path is centralized in `config/environments.json` under `api.basePath`. To change the API version, update `api.basePath` in the config file. All code (backend routes, frontend client, tests, scripts) automatically uses the configured value.

## 🚀 Quick Start

### Prerequisites

- Python 3.12+
- Database file: `data/core/full_stack_qa_dev.db` (development database - already created)
  - The backend automatically uses `full_stack_qa_dev.db` by default
  - Use `ENVIRONMENT` env var to select different databases (dev/test/prod)

### Installation

1. **Create virtual environment**:
   ```bash
   cd backend
   python3 -m venv venv
   source venv/bin/activate  # On Windows: venv\Scripts\activate
   ```

2. **Install dependencies**:
   ```bash
   pip install -r requirements.txt
   ```

3. **Configure environment**:
   ```bash
   cp .env.example .env
   # Edit .env if needed (defaults should work)
   ```

4. **Run the API**:
   ```bash
   # Option 1: Using helper script (recommended)
   ../scripts/start-be.sh                    # Default: dev environment
   ../scripts/start-be.sh --env dev          # Explicit dev
   ../scripts/start-be.sh -e test            # Test environment
   
   # Option 2: Using uvicorn directly
   uvicorn app.main:app --reload --host localhost --port 8003
   
   # Option 3: Using Python
   python -m app.main
   ```

5. **Access API**:
   - API: http://localhost:8003 (dev), http://localhost:8004 (test), http://localhost:8005 (prod)
   - Docs: http://localhost:8003/docs
   - ReDoc: http://localhost:8003/redoc

## 📁 Project Structure

```
backend/
├── app/
│   ├── __init__.py
│   ├── main.py              # FastAPI app initialization
│   ├── config.py            # Configuration settings
│   ├── api/
│   │   ├── __init__.py
│   │   └── v1/              # API v1 endpoints
│   │       ├── __init__.py
│   │       ├── applications.py
│   │       ├── companies.py
│   │       ├── clients.py
│   │       ├── contacts.py
│   │       ├── notes.py
│   │       └── job_search_sites.py
│   ├── models/              # Pydantic models
│   │   ├── __init__.py
│   │   ├── application.py
│   │   ├── company.py
│   │   ├── client.py
│   │   ├── contact.py
│   │   ├── note.py
│   │   └── job_search_site.py
│   ├── database/
│   │   ├── __init__.py
│   │   ├── connection.py   # Database connection
│   │   └── queries.py       # Database queries
│   └── utils/
│       ├── __init__.py
│       └── errors.py          # Custom error classes
├── tests/                    # API tests
│   ├── __init__.py
│   ├── test_applications.py
│   ├── test_companies.py
│   ├── test_clients.py
│   ├── test_contacts.py
│   └── test_notes.py
├── requirements.txt
├── .env.example
└── README.md
```

## 🔧 Configuration

Configuration is managed through:
1. **Centralized Config** (`config/environments.json`) - Single source of truth for ports, database, API paths, CORS, timeouts
2. **Environment Variables** (see `.env.example`) - Can override config values

### Environment Variables

- `ENVIRONMENT`: Environment name (dev/test/prod) - selects database automatically (default: `dev` → `full_stack_qa_dev.db`)
- `DATABASE_PATH`: Full path to database file (optional, overrides ENVIRONMENT)
- `DATABASE_NAME`: Database filename only (optional, used with default directory)
- `API_HOST`: API host (default: `localhost`)
- `API_PORT`: API port (read from `config/environments.json` by default)
- `CORS_ORIGINS`: Comma-separated list of allowed origins (read from `config/environments.json` by default)

### API Version Configuration

The API base path (e.g., `/api/v1`) is configured in `config/environments.json` under `api.basePath`. The backend automatically uses this value for all router prefixes. To change the API version:

1. Edit `config/environments.json`:
   ```json
   {
     "api": {
       "basePath": "/api/v2"  // Change from "/api/v1" to "/api/v2"
     }
   }
   ```

2. Restart the backend - all routes will automatically use the new base path.

## 📚 API Documentation

- **OpenAPI Docs**: http://localhost:8000/docs
- **ReDoc**: http://localhost:8000/redoc
- **API Contract**: See `docs/new_app/API_CONTRACT.md`

## 🧪 Testing

```bash
# Run all tests
pytest

# Run with coverage
pytest --cov=app --cov-report=html

# Run specific test file
pytest tests/test_applications.py
```

## 📝 Development

### Code Style

- **Formatter**: Black
- **Linter**: Ruff

```bash
# Format code
black app/ tests/

# Lint code
ruff check app/ tests/
```

## 🔗 Related Documentation

- **API Contract**: `docs/new_app/API_CONTRACT.md`
- **API Versioning**: `docs/new_app/API_VERSIONING_GUIDE.md`
- **Database Work**: `docs/new_app/WORK_DATABASE.md`
- **Delete Behavior**: `docs/new_app/DELETE_BEHAVIOR.md`

## ✅ Status

- ✅ Project structure created
- ✅ FastAPI app initialized
- ✅ Database connection module created
- ✅ Error handling utilities created
- ⏭️ Pydantic models (in progress)
- ⏭️ API endpoints (pending)
- ⏭️ Tests (pending)

---

**Last Updated**: 2026-04-06

Dependency floors in `requirements.txt` follow the current stable FastAPI / Starlette / Uvicorn line (see `docs/process/VERSION_TRACKING.md`).
