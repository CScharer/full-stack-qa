# Local Development Guide

**Last Updated**: 2025-12-14  
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

The database must be created before running the backend. The database file is located at:

```
Data/Core/full_stack_qa.db
```

### Quick Setup

If the database doesn't exist yet, you can create it using the schema:

```bash
# Navigate to project root (adjust path as needed)
cd /path/to/full-stack-qa

# Create database directory if it doesn't exist
mkdir -p Data/Core

# Create database from schema (if needed)
sqlite3 Data/Core/full_stack_qa.db < docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql

# Apply delete triggers
sqlite3 Data/Core/full_stack_qa.db < docs/new_app/DELETE_TRIGGERS.sql
```

### Verify Database

```bash
# Check if database exists and has tables
sqlite3 Data/Core/full_stack_qa.db ".tables"

# Should show: application, company, client, contact, contact_email, contact_phone, note, job_search_site, application_sync, default_value
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

Create a `.env` file in the `backend` directory:

```bash
# backend/.env
DATABASE_PATH=../Data/Core/full_stack_qa.db
API_HOST=0.0.0.0
API_PORT=8008
CORS_ORIGINS=http://127.0.0.1:3003,http://localhost:3003
```

### 5. Run Backend Server

```bash
# Using uvicorn directly
uvicorn app.main:app --reload --host 0.0.0.0 --port 8008

# Or using the helper script (if available)
../scripts/start-backend.sh
```

### 6. Verify Backend is Running

- **API**: http://localhost:8008
- **API Docs (Swagger)**: http://localhost:8008/docs
- **API Docs (ReDoc)**: http://localhost:8008/redoc
- **Health Check**: http://localhost:8008/health

### Backend Test Endpoints

```bash
# Test API health
curl http://localhost:8008/health

# Test applications endpoint
curl http://localhost:8008/api/v1/applications
```

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
NEXT_PUBLIC_API_URL=http://localhost:8008/api/v1
```

### 4. Run Frontend Development Server

```bash
# Start Next.js development server
npm run dev

# Or using the helper script (if available)
../scripts/start-frontend.sh
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
uvicorn app.main:app --reload --host 0.0.0.0 --port 8008
```

**Terminal 2 - Frontend:**
```bash
cd frontend
PORT=3003 npm run dev
```

### Option 2: Using Helper Scripts

```bash
# Terminal 1 - Backend
./scripts/start-backend.sh

# Terminal 2 - Frontend
./scripts/start-frontend.sh
```

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
# Solution: Ensure database exists at Data/Core/full_stack_qa.db
# Create it using the schema files if needed
```

**Issue: Port 8008 already in use**
```bash
# Solution: Change port in .env file or kill process using port 8008
# macOS/Linux:
lsof -ti:8008 | xargs kill -9

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
# Check: NEXT_PUBLIC_API_URL=http://localhost:8008/api/v1
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
│       └── full_stack_qa.db  # SQLite database
│
└── scripts/                # Helper scripts
    ├── start-backend.sh
    └── start-frontend.sh
```

---

## 🔗 Quick Reference

### Backend Endpoints

- **Base URL**: `http://localhost:8008`
- **API Base**: `http://localhost:8008/api/v1`
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

- **Backend API Documentation**: http://localhost:8008/docs (when backend is running)
- **Database Schema**: `docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql`
- **API Contract**: `docs/new_app/API_CONTRACT.md`
- **Backend Work Plan**: `docs/new_app/WORK_BACKEND.md`
- **Frontend Work Plan**: `docs/new_app/WORK_FRONTEND.md`

---

## ✅ Verification Checklist

Before starting development, verify:

- [ ] Database exists at `Data/Core/full_stack_qa.db`
- [ ] Backend virtual environment created and activated
- [ ] Backend dependencies installed (`pip install -r requirements.txt`)
- [ ] Backend `.env` file configured
- [ ] Backend server runs on http://localhost:8008
- [ ] Frontend dependencies installed (`npm install`)
- [ ] Frontend `.env.local` file configured
- [ ] Frontend server runs on http://127.0.0.1:3003
- [ ] Frontend can connect to backend API
- [ ] Tests pass for both backend and frontend

---

**Need Help?** Check the troubleshooting section above or review the work plan documents in `docs/new_app/`.
