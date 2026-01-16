#!/bin/bash
# scripts/tests/frameworks/run-frontend-tests.sh
# Frontend Test Runner
#
# Purpose: Run frontend tests for the ONE GOAL Next.js application
#
# Usage:
#   ./scripts/tests/frameworks/run-frontend-tests.sh
#
# Description:
#   This script runs the Next.js frontend test suite using Vitest.
#   Tests are located in frontend/__tests__/ and cover React components,
#   API routes, and user interactions.
#
# Examples:
#   ./scripts/tests/frameworks/run-frontend-tests.sh
#
# Dependencies:
#   - Node.js 20+
#   - npm (installed in frontend/ directory)
#   - Frontend dependencies (installed via npm)
#   - Vitest test framework (installed via npm)
#
# Output:
#   - Test results in console output
#   - Coverage reports (if configured)
#   - Exit code: 0 on success, non-zero on failure
#
# Notes:
#   - Installs dependencies if node_modules/ is missing
#   - Uses Vitest for test execution
#   - Supports snapshot testing
#   - May require frontend service to be running for integration tests
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
