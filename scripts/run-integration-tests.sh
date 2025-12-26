#!/bin/bash

# Script to run Playwright integration tests
# Tests the full stack: Frontend + Backend + Database

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
PLAYWRIGHT_DIR="$PROJECT_ROOT/playwright"

# Set environment to test for integration tests
# This ensures the backend uses full_stack_qa_test.db
export ENVIRONMENT=test

echo "════════════════════════════════════════"
echo "🧪 Running Integration Tests"
echo "════════════════════════════════════════"
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

# Check test environment database exists
# Integration tests use full_stack_qa_test.db (test environment)
TEST_DB_PATH="$PROJECT_ROOT/Data/Core/full_stack_qa_test.db"
if [ ! -f "$TEST_DB_PATH" ]; then
    echo "⚠️  Test database not found. Creating test database from schema..."
    mkdir -p "$PROJECT_ROOT/Data/Core"
    if [ -f "$PROJECT_ROOT/docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql" ]; then
        sqlite3 "$TEST_DB_PATH" < "$PROJECT_ROOT/docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql"
        if [ -f "$PROJECT_ROOT/docs/new_app/DELETE_TRIGGERS.sql" ]; then
            sqlite3 "$TEST_DB_PATH" < "$PROJECT_ROOT/docs/new_app/DELETE_TRIGGERS.sql"
        fi
        echo "✅ Test database created: full_stack_qa_test.db"
    else
        echo "❌ Schema file not found. Please create the database manually."
        exit 1
    fi
else
    echo "✅ Test database exists: full_stack_qa_test.db"
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
