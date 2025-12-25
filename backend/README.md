# ONE GOAL Backend API

FastAPI-based REST API for the ONE GOAL job search application.

## 📋 Overview

This backend provides REST endpoints for managing job applications, companies, clients, contacts, notes, and job search sites.

**Technology Stack**:
- **Framework**: FastAPI
- **Database**: SQLite (`Data/Core/full_stack_qa.db`)
- **Validation**: Pydantic
- **API Version**: v1 (`/api/v1/*`)

## 🚀 Quick Start

### Prerequisites

- Python 3.12+
- Database file: `Data/Core/full_stack_qa.db` (already created)

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
   # Option 1: Using uvicorn directly
   uvicorn app.main:app --reload --host localhost --port 8000
   
   # Option 2: Using Python
   python -m app.main
   ```

5. **Access API**:
   - API: http://localhost:8000
   - Docs: http://localhost:8000/docs
   - ReDoc: http://localhost:8000/redoc

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

Configuration is managed through environment variables (see `.env.example`):

- `DATABASE_PATH`: Path to SQLite database (default: `../Data/Core/full_stack_qa.db`)
- `API_HOST`: API host (default: `localhost`)
- `API_PORT`: API port (default: `8008`)
- `CORS_ORIGINS`: Comma-separated list of allowed origins
- `ENVIRONMENT`: Environment name (development/production)

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

**Last Updated**: 2025-12-14
