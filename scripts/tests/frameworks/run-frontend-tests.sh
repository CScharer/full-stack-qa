#!/bin/bash
# Run ONE GOAL Frontend Tests
# This script runs the frontend test suite

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
FRONTEND_DIR="${SCRIPT_DIR}/frontend"

echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}🧪 Running ONE GOAL Frontend Tests${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo ""

# Check if frontend directory exists
if [ ! -d "$FRONTEND_DIR" ]; then
    echo -e "${RED}❌ Frontend directory not found: $FRONTEND_DIR${NC}"
    exit 1
fi

# Check if node_modules exists
if [ ! -d "$FRONTEND_DIR/node_modules" ]; then
    echo -e "${YELLOW}📦 Installing dependencies...${NC}"
    cd "$FRONTEND_DIR"
    npm install
    echo -e "${GREEN}✅ Dependencies installed${NC}"
fi

# Run tests
echo ""
echo -e "${GREEN}🧪 Running frontend tests...${NC}"
echo ""

cd "$FRONTEND_DIR"

# Run jest tests
if npm test -- --passWithNoTests; then
    echo ""
    echo -e "${GREEN}✅ All frontend tests passed!${NC}"
    exit 0
else
    echo ""
    echo -e "${RED}❌ Some frontend tests failed${NC}"
    exit 1
fi
