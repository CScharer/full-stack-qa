#!/bin/bash
# scripts/tests/frameworks/run-backend-tests.sh
# Backend API Test Runner
#
# Purpose: Run backend API tests for the ONE GOAL application
#
# Usage:
#   ./scripts/tests/frameworks/run-backend-tests.sh
#
# Description:
#   This script runs the FastAPI backend test suite using pytest.
#   Tests are located in backend/tests/ and cover API endpoints,
#   database operations, and business logic.
#
# Examples:
#   ./scripts/tests/frameworks/run-backend-tests.sh
#
# Dependencies:
#   - Python 3.13+
#   - Backend virtual environment (backend/venv/)
#   - Backend dependencies (installed in venv)
#   - Backend service running (optional, some tests may require it)
#
# Output:
#   - Test results in backend/test-results/
#   - Coverage reports (if configured)
#   - Exit code: 0 on success, non-zero on failure
#
# Notes:
#   - Activates backend virtual environment automatically
#   - Installs dependencies if venv is missing
#   - Uses pytest for test execution
#   - May require backend service to be running for integration tests
#
# Last Updated: January 2026

set -e

# Get the script directory (project root)
# Since this script is in scripts/tests/frameworks/, we need to go up three levels
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../.." && pwd)"
cd "$SCRIPT_DIR"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
BACKEND_DIR="${SCRIPT_DIR}/backend"
# Note: Backend tests use temporary databases created by pytest fixtures
# No environment database needed - tests are isolated

echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}🧪 Running ONE GOAL Backend Tests${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo ""

# Check if backend directory exists
if [ ! -d "$BACKEND_DIR" ]; then
    echo -e "${RED}❌ Backend directory not found: $BACKEND_DIR${NC}"
    exit 1
fi

# Check if virtual environment exists, create if not
if [ ! -d "$BACKEND_DIR/venv" ]; then
    echo -e "${YELLOW}📦 Creating virtual environment...${NC}"
    cd "$BACKEND_DIR"
    python3 -m venv venv
    echo -e "${GREEN}✅ Virtual environment created${NC}"
fi

# Activate virtual environment
echo -e "${BLUE}🔧 Activating virtual environment...${NC}"
cd "$BACKEND_DIR"
source venv/bin/activate

# Install/upgrade dependencies
echo -e "${BLUE}📦 Installing dependencies...${NC}"
pip install -q --upgrade pip
pip install -q -r "$BACKEND_DIR/requirements.txt"

# Note: Backend unit tests use temporary databases (auto-created by pytest)
# No database file check needed - tests are fully isolated

# Run tests
echo ""
echo -e "${GREEN}🧪 Running backend tests...${NC}"
echo ""

cd "$BACKEND_DIR"

# Run pytest with coverage
if pytest tests/ -v --tb=short --cov=app --cov-report=term-missing; then
    echo ""
    echo -e "${GREEN}✅ All backend tests passed!${NC}"
    exit 0
else
    echo ""
    echo -e "${RED}❌ Some backend tests failed${NC}"
    exit 1
fi
