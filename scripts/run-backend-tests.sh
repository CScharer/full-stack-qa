#!/bin/bash
# Run ONE GOAL Backend API Tests
# This script runs the backend test suite

set -e

# Get the script directory (project root)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$SCRIPT_DIR"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
BACKEND_DIR="${SCRIPT_DIR}/backend"
TEST_DB_PATH="${SCRIPT_DIR}/Data/Core/test_full_stack_testing.db"

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

# Check if database exists (for integration tests)
DB_PATH="${SCRIPT_DIR}/Data/Core/full_stack_testing.db"
if [ ! -f "$DB_PATH" ]; then
    echo -e "${YELLOW}⚠️  Database file not found: $DB_PATH${NC}"
    echo -e "${YELLOW}   Some integration tests may fail${NC}"
fi

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
