#!/bin/bash

# Script to run Playwright integration tests
# Tests the full stack: Frontend + Backend + Database

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
PLAYWRIGHT_DIR="$PROJECT_ROOT/playwright"

# Set environment (defaults to dev, can be overridden)
# This determines which database and ports to use
ENVIRONMENT=${ENVIRONMENT:-dev}
export ENVIRONMENT

echo "════════════════════════════════════════"
echo "🧪 Running Integration Tests"
echo "════════════════════════════════════════"
echo ""
echo "📋 Environment: $ENVIRONMENT"
echo "   Database: full_stack_qa_${ENVIRONMENT}.db"
echo ""

# Check prerequisites
echo "📋 Checking prerequisites..."

# Check Node.js
if ! command -v node &> /dev/null; then
    echo "❌ Node.js is not installed. Please install Node.js 18+"
    exit 1
fi
echo "✅ Node.js: $(node --version)"

# Check Python
if ! command -v python3 &> /dev/null; then
    echo "❌ Python 3 is not installed. Please install Python 3.12+"
    exit 1
fi
echo "✅ Python: $(python3 --version)"

# Check environment-specific database exists
# Integration tests use full_stack_qa_{environment}.db based on ENVIRONMENT
DB_NAME="full_stack_qa_${ENVIRONMENT}.db"
DB_PATH="$PROJECT_ROOT/data/Core/$DB_NAME"
if [ ! -f "$DB_PATH" ]; then
    echo "⚠️  Environment database not found: $DB_NAME"
    echo "   Creating $ENVIRONMENT environment database from schema..."
    mkdir -p "$PROJECT_ROOT/data/Core"
    if [ -f "$PROJECT_ROOT/docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql" ]; then
        sqlite3 "$DB_PATH" < "$PROJECT_ROOT/docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql"
        if [ -f "$PROJECT_ROOT/docs/new_app/DELETE_TRIGGERS.sql" ]; then
            sqlite3 "$DB_PATH" < "$PROJECT_ROOT/docs/new_app/DELETE_TRIGGERS.sql"
        fi
        echo "✅ $ENVIRONMENT environment database created: $DB_NAME"
    else
        echo "❌ Schema file not found. Please create the database manually."
        exit 1
    fi
else
    echo "✅ $ENVIRONMENT environment database exists: $DB_NAME"
fi

# Check backend venv
if [ ! -d "$PROJECT_ROOT/backend/venv" ]; then
    echo "⚠️  Backend virtual environment not found. Creating..."
    cd "$PROJECT_ROOT/backend"
    python3 -m venv venv
    source venv/bin/activate
    pip install -r requirements.txt
    echo "✅ Backend virtual environment created"
else
    echo "✅ Backend virtual environment exists"
fi

# Check frontend dependencies
if [ ! -d "$PROJECT_ROOT/frontend/node_modules" ]; then
    echo "⚠️  Frontend dependencies not installed. Installing..."
    cd "$PROJECT_ROOT/frontend"
    npm install --legacy-peer-deps
    echo "✅ Frontend dependencies installed"
else
    echo "✅ Frontend dependencies installed"
fi

# Check Playwright dependencies
if [ ! -d "$PLAYWRIGHT_DIR/node_modules" ]; then
    echo "⚠️  Playwright dependencies not installed. Installing..."
    cd "$PLAYWRIGHT_DIR"
    npm install
    npx playwright install chromium
    echo "✅ Playwright dependencies installed"
else
    echo "✅ Playwright dependencies installed"
fi

echo ""
echo "🚀 Starting integration tests..."
echo ""

# Run integration tests
cd "$PLAYWRIGHT_DIR"
npm run test:integration

echo ""
echo "════════════════════════════════════════"
echo "✅ Integration tests completed"
echo "════════════════════════════════════════"
