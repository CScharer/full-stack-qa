# Local Development Guide

**Last Updated**: 2025-12-27  
**Purpose**: Guide for running the ONE GOAL frontend and backend locally

---

## 📋 Prerequisites

Before running the application locally, ensure you have:

- **Node.js** (v18 or higher) - [Download](https://nodejs.org/)
- **Python** (v3.12 or higher) - [Download](https://www.python.org/downloads/)
- **Git** - [Download](https://git-scm.com/downloads)
- **SQLite3** (usually included with Python)

---

## 🗄️ Database Setup

### Database Types

The project uses **environment-specific databases** for runtime data:

- **Schema Database** (`full_stack_qa.db`): Template/reference only - **NEVER used for runtime**
- **Development Database** (`full_stack_qa_dev.db`): Default database for local development
- **Test Database** (`full_stack_qa_test.db`): Used for integration testing (when `ENVIRONMENT=test`)
- **Production Database** (`full_stack_qa_prod.db`): Used for production (if needed)

### Quick Setup

The development database should already exist. If it doesn't, create it from the schema:

```bash
# Navigate to project root
cd /path/to/full-stack-qa

# Create database directory if it doesn't exist
mkdir -p Data/Core

# Create development database from schema
sqlite3 Data/Core/full_stack_qa_dev.db < docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql

# Apply delete triggers
sqlite3 Data/Core/full_stack_qa_dev.db < docs/new_app/DELETE_TRIGGERS.sql
```

**Note**: The backend automatically uses `full_stack_qa_dev.db` by default (when `ENVIRONMENT=dev` or no environment is set).

### Verify Database

```bash
# Check if development database exists and has tables
sqlite3 Data/Core/full_stack_qa_dev.db ".tables"

# Should show: application, company, client, contact, contact_email, contact_phone, note, job_search_site, application_sync
```

---

## 🔧 Backend Setup

### 1. Navigate to Backend Directory

```bash
cd backend
```

### 2. Create Virtual Environment

```bash
# Create virtual environment
python3 -m venv venv

# Activate virtual environment
# On macOS/Linux:
source venv/bin/activate

# On Windows:
# venv\Scripts\activate
```

### 3. Install Dependencies

```bash
# Install Python packages
pip install -r requirements.txt
```

### 4. Configure Environment Variables

The backend now uses centralized environment configuration. You can set environment variables, but the scripts will automatically configure them based on the `ENVIRONMENT` variable.

Create a `.env` file in the `backend` directory (optional - defaults work for development):

```bash
# backend/.env
# Database configuration (optional - defaults to full_stack_qa_dev.db)
ENVIRONMENT=dev  # Options: dev, test, prod
# The scripts will automatically set DATABASE_PATH to the correct absolute path
# You can override with an absolute path if needed:
# DATABASE_PATH=/absolute/path/to/Data/Core/full_stack_qa_dev.db

API_HOST=0.0.0.0
API_PORT=8003  # dev: 8003, test: 8004, prod: 8005
# CORS_ORIGINS is automatically set by the scripts based on environment
```

**Note**: The backend automatically uses `full_stack_qa_dev.db` for development by default. The service scripts (`start-be.sh`, `start-env.sh`) automatically configure `DATABASE_PATH` and `CORS_ORIGINS` based on the `ENVIRONMENT` variable. Database paths are normalized to absolute paths to ensure they work correctly regardless of where the script is run from.

### 5. Run Backend Server

```bash
# Using uvicorn directly
uvicorn app.main:app --reload --host 0.0.0.0 --port 8003

# Or using the helper script (recommended)
../scripts/start-be.sh                  # Default: dev environment
../scripts/start-be.sh --env dev         # Explicit dev
../scripts/start-be.sh -e test          # Test environment
```

**Note**: The default dev port is **8003** (not 8008). See [Port Configuration Guide](../infrastructure/PORT_CONFIGURATION.md) for all port assignments.

### 6. Verify Backend is Running

- **API**: http://localhost:8003 (dev), http://localhost:8004 (test), http://localhost:8005 (prod)
- **API Docs (Swagger)**: http://localhost:8003/docs
- **API Docs (ReDoc)**: http://localhost:8003/redoc
- **Health Check**: http://localhost:8003/health

### Backend Test Endpoints

```bash
# Test API health (dev environment)
curl http://localhost:8003/health

# Test applications endpoint
curl http://localhost:8003/api/v1/applications
```

**See Also**: [Service Scripts Guide](../infrastructure/SERVICE_SCRIPTS.md) for service management scripts.

---

## 🎨 Frontend Setup

### 1. Navigate to Frontend Directory

```bash
cd frontend
```

### 2. Install Dependencies

```bash
# Install Node.js packages
npm install

# If you encounter peer dependency issues:
npm install --legacy-peer-deps
```

### 3. Configure Environment Variables

Create a `.env.local` file in the `frontend` directory:

```bash
# frontend/.env.local
NEXT_PUBLIC_API_URL=http://localhost:8003/api/v1  # dev: 8003, test: 8004, prod: 8005
```

### 4. Run Frontend Development Server

```bash
# Start Next.js development server
npm run dev

# Or using the helper script (if available)
../scripts/start-fe.sh                  # Default: dev environment
../scripts/start-fe.sh --env dev         # Explicit dev
../scripts/start-fe.sh -e test          # Test environment
```

### 5. Verify Frontend is Running

- **Frontend**: http://127.0.0.1:3003
- **Home Page**: http://127.0.0.1:3003
- **Applications**: http://127.0.0.1:3003/applications
- **Companies**: http://127.0.0.1:3003/companies
- **Contacts**: http://127.0.0.1:3003/contacts
- **Notes**: http://127.0.0.1:3003/notes
- **Clients**: http://127.0.0.1:3003/clients
- **Job Search Sites**: http://127.0.0.1:3003/job-search-sites

---

## 🚀 Running Both Services

### Option 1: Separate Terminals

**Terminal 1 - Backend:**
```bash
cd backend
source venv/bin/activate
uvicorn app.main:app --reload --host 0.0.0.0 --port 8003
```

**Terminal 2 - Frontend:**
```bash
cd frontend
PORT=3003 npm run dev
```

### Option 2: Using Helper Scripts

```bash
# Terminal 1 - Backend
./scripts/start-be.sh                    # Default: dev environment
./scripts/start-be.sh --env dev          # Explicit dev
./scripts/start-be.sh -e test            # Test environment
./scripts/start-be.sh --env=prod         # Production environment

# Terminal 2 - Frontend
./scripts/start-fe.sh                    # Default: dev environment
./scripts/start-fe.sh --env dev          # Explicit dev
./scripts/start-fe.sh -e test            # Test environment
./scripts/start-fe.sh --env=prod         # Production environment
```

### Option 3: Start Both Services Together

```bash
# Start both backend and frontend in one command
./scripts/start-env.sh                   # Default: dev environment
./scripts/start-env.sh --env test        # Test environment
./scripts/start-env.sh -e prod           # Production environment
./scripts/start-env.sh --env dev be=8004 fe=3004  # Custom ports
./scripts/start-env.sh -e test --background  # Run in background
```

**Note**: Run `./scripts/start-env.sh --help` for full usage information.

---

## 🧪 Running Tests

### Backend Tests

```bash
cd backend
source venv/bin/activate

# Run all tests
pytest

# Run with coverage
pytest --cov=app --cov-report=html

# Run specific test file
pytest tests/test_applications_api.py -v
```

### Frontend Tests

```bash
cd frontend

# Run all tests
npm test

# Run tests in watch mode
npm run test:watch

# Run tests with coverage
npm run test:coverage

# Run tests with UI
npm run test:ui
```

---

## 🔍 Troubleshooting

### Backend Issues

**Issue: Database not found**
```bash
# Solution: Ensure development database exists at Data/Core/full_stack_qa_dev.db
# Create it using the schema files if needed:
sqlite3 Data/Core/full_stack_qa_dev.db < docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql
sqlite3 Data/Core/full_stack_qa_dev.db < docs/new_app/DELETE_TRIGGERS.sql
```

**Issue: Port 8003 already in use**
```bash
# Solution: Change port in .env file or kill process using port 8003
# macOS/Linux:
lsof -ti:8003 | xargs kill -9

# Or use a different port:
uvicorn app.main:app --reload --port 8009
```

**Issue: Module not found errors**
```bash
# Solution: Ensure virtual environment is activated and dependencies installed
source venv/bin/activate
pip install -r requirements.txt
```

### Frontend Issues

**Issue: Port 3003 already in use**
```bash
# Solution: Kill process using port 3003
# macOS/Linux:
lsof -ti:3003 | xargs kill -9

# Or Next.js will automatically use the next available port
```

**Issue: API connection errors**
```bash
# Solution: Ensure backend is running and .env.local has correct API URL
# Check: NEXT_PUBLIC_API_URL=http://localhost:8003/api/v1 (dev: 8003, test: 8004, prod: 8005)
```

**Issue: Module resolution errors**
```bash
# Solution: Clear node_modules and reinstall
rm -rf node_modules package-lock.json
npm install --legacy-peer-deps
```

**Issue: Vitest tests failing**
```bash
# Solution: Ensure all dependencies are installed
npm install --legacy-peer-deps

# Clear cache and reinstall
rm -rf node_modules .next
npm install --legacy-peer-deps
```

---

## 📁 Project Structure

```
full-stack-qa/
├── backend/                 # FastAPI backend
│   ├── app/
│   │   ├── api/v1/         # API endpoints
│   │   ├── models/         # Pydantic models
│   │   ├── database/       # Database queries
│   │   └── main.py         # FastAPI app
│   ├── tests/              # Backend tests
│   ├── requirements.txt    # Python dependencies
│   └── .env                # Backend environment variables
│
├── frontend/               # Next.js frontend
│   ├── app/                # Next.js app router pages
│   ├── components/          # React components
│   ├── lib/                # Utilities, hooks, types
│   ├── __tests__/          # Frontend tests
│   ├── package.json        # Node.js dependencies
│   └── .env.local          # Frontend environment variables
│
├── Data/
│   └── Core/
│       ├── full_stack_qa.db  # Schema database (template only)
│       ├── full_stack_qa_dev.db  # Development database (default)
│       └── full_stack_qa_test.db  # Test database
│
└── scripts/                # Helper scripts
    ├── start-be.sh
    └── start-fe.sh
```

---

## 🔗 Quick Reference

### Backend Endpoints

- **Base URL**: `http://localhost:8003` (dev), `http://localhost:8004` (test), `http://localhost:8005` (prod)
- **API Base**: `http://localhost:8003/api/v1` (dev), `http://localhost:8004/api/v1` (test), `http://localhost:8005/api/v1` (prod)
- **Applications**: `GET/POST /api/v1/applications`
- **Companies**: `GET/POST /api/v1/companies`
- **Contacts**: `GET/POST /api/v1/contacts`
- **Notes**: `GET/POST /api/v1/notes`
- **Clients**: `GET/POST /api/v1/clients`
- **Job Search Sites**: `GET/POST /api/v1/job-search-sites`

### Frontend Routes

- **Home**: `/`
- **Applications**: `/applications`, `/applications/[id]`, `/applications/new`, `/applications/[id]/edit`
- **Companies**: `/companies`, `/companies/[id]`, `/companies/new`, `/companies/[id]/edit`
- **Contacts**: `/contacts`, `/contacts/[id]`, `/contacts/new`, `/contacts/[id]/edit`
- **Notes**: `/notes`, `/notes/[id]`, `/notes/new`, `/notes/[id]/edit`
- **Clients**: `/clients`, `/clients/[id]`, `/clients/new`, `/clients/[id]/edit`
- **Job Search Sites**: `/job-search-sites`, `/job-search-sites/[id]`, `/job-search-sites/new`, `/job-search-sites/[id]/edit`

---

## 📚 Additional Resources

- **Backend API Documentation**: http://localhost:8003/docs (dev), http://localhost:8004/docs (test), http://localhost:8005/docs (prod)
- **Database Schema**: `docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql`
- **API Contract**: `docs/new_app/API_CONTRACT.md`
- **Backend Work Plan**: `docs/new_app/WORK_BACKEND.md`
- **Frontend Work Plan**: `docs/new_app/WORK_FRONTEND.md`

---

## ✅ Verification Checklist

Before starting development, verify:

- [ ] Development database exists at `Data/Core/full_stack_qa_dev.db`
- [ ] Backend virtual environment created and activated
- [ ] Backend dependencies installed (`pip install -r requirements.txt`)
- [ ] Backend `.env` file configured
- [ ] Backend server runs on http://localhost:8003 (dev), http://localhost:8004 (test), or http://localhost:8005 (prod)
- [ ] Frontend dependencies installed (`npm install`)
- [ ] Frontend `.env.local` file configured
- [ ] Frontend server runs on http://127.0.0.1:3003
- [ ] Frontend can connect to backend API
- [ ] Tests pass for both backend and frontend

---

**Need Help?** Check the troubleshooting section above or review the work plan documents in `docs/new_app/`.
