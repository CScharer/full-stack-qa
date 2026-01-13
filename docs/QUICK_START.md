# Quick Start Guide

**Last Updated**: 2025-12-27  
**Purpose**: Quick reference for starting the application

---

## 🚀 Quick Start

### Option 1: Start Both Services Together

```bash
cd /path/to/full-stack-qa
./scripts/start-env.sh                   # Default: dev environment
./scripts/start-env.sh --env test        # Test environment
./scripts/start-env.sh -e prod           # Production environment
```

**OR with custom ports:**
```bash
./scripts/start-env.sh --env dev be=8004 fe=3004
```

---

### Option 2: Start Services Separately (2 Terminals)

### Terminal 1: Start Backend

```bash
cd /path/to/full-stack-qa
./scripts/start-be.sh                    # Default: dev environment
./scripts/start-be.sh --env dev          # Explicit dev
./scripts/start-be.sh -e test            # Test environment
./scripts/start-be.sh --env=prod         # Production environment
```

**OR manually:**
```bash
cd backend
source venv/bin/activate
uvicorn app.main:app --reload --host 0.0.0.0 --port 8003
```

**Backend will be available at:**
- API: http://localhost:8003 (dev), http://localhost:8004 (test), http://localhost:8005 (prod)
- Docs: http://localhost:8003/docs

---

### Terminal 2: Start Frontend

```bash
cd /path/to/full-stack-qa
./scripts/start-fe.sh                    # Default: dev environment
./scripts/start-fe.sh --env dev          # Explicit dev
./scripts/start-fe.sh -e test            # Test environment
./scripts/start-fe.sh --env=prod         # Production environment
```

**OR manually:**
```bash
cd frontend
export NEXT_PUBLIC_API_URL=http://localhost:8003/api/v1
PORT=3003 npm run dev
```

**Frontend will be available at:**
- App: http://127.0.0.1:3003

---

## ⚠️ Common Mistakes

### ❌ Wrong Directory
```bash
# DON'T run this in backend directory:
cd backend
npm run dev  # ❌ This won't work - backend doesn't have npm scripts
```

### ✅ Correct Approach
```bash
# For frontend, go to frontend directory:
cd frontend
npm run dev  # ✅ This works
```

---

## 📝 Environment Variables

### Backend (.env file in `backend/` directory)
```bash
# Database configuration (optional - defaults to full_stack_qa_dev.db)
ENVIRONMENT=dev  # Options: dev, test, prod
# The scripts automatically set DATABASE_PATH to the correct absolute path
# You can override with an absolute path if needed:
# DATABASE_PATH=/absolute/path/to/Data/Core/full_stack_qa_dev.db

API_HOST=0.0.0.0
API_PORT=8003  # dev: 8003, test: 8004, prod: 8005
# CORS_ORIGINS is automatically set by the scripts based on environment
```

**Note**: The backend automatically uses `full_stack_qa_dev.db` for development by default. The service scripts (`start-be.sh`, `start-env.sh`) automatically configure `DATABASE_PATH` and `CORS_ORIGINS` based on the `ENVIRONMENT` variable. Database paths are normalized to absolute paths to ensure they work correctly regardless of where the script is run from.

### Frontend (.env.local file in `frontend/` directory)
```bash
NEXT_PUBLIC_API_URL=http://localhost:8003/api/v1  # dev: 8003, test: 8004, prod: 8005
```

**Note:** Environment variables set in terminal (like `export NEXT_PUBLIC_API_URL=...`) only work for that terminal session. For persistence, use `.env.local` file.

---

## ✅ Verification

### Check Backend
```bash
curl http://localhost:8003/health  # dev: 8003, test: 8004, prod: 8005
# Should return: {"status":"healthy"}
```

### Check Frontend
Open browser: http://127.0.0.1:3003

---

## 🔧 Troubleshooting

### Backend won't start
- Check if port 8003 is in use: `lsof -ti:8003 | xargs kill -9` (dev: 8003, test: 8004, prod: 8005)
- Verify development database exists: `ls -la Data/Core/full_stack_qa_dev.db`
- Check virtual environment: `source backend/venv/bin/activate`

### Frontend won't start
- Check if port 3003 is in use: `lsof -ti:3003 | xargs kill -9` (dev: 3003, test: 3004, prod: 3005)
- Verify .env.local exists: `ls -la frontend/.env.local`
- Check dependencies: `cd frontend && npm install --legacy-peer-deps`

### Frontend can't connect to backend
- Verify backend is running: `curl http://localhost:8003/health` (dev: 8003, test: 8004, prod: 8005)
- Check .env.local has correct URL: `cat frontend/.env.local`
- Check CORS settings in backend config
- Ensure port matches environment (dev=8003, test=8004, prod=8005)

---

**For detailed instructions, see:** 
- [Local Development Guide](./guides/setup/LOCAL_DEVELOPMENT.md) - Complete local development guide
- [Service Scripts Guide](./guides/infrastructure/SERVICE_SCRIPTS.md) - Service management scripts documentation
- [Port Configuration Guide](./guides/infrastructure/PORT_CONFIGURATION.md) - Port assignments and configuration
- Configuration files are documented in `config/environments.json` and `config/ports.json` (see inline comments)
