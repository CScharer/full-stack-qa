#!/bin/bash
# Vibium Test Runner
# Run Vibium browser automation tests using Vitest
# Usage: ./scripts/run-vibium-tests.sh [options]
# Options:
#   --watch    Run tests in watch mode
#   --ui       Run tests with UI
#   --coverage Generate coverage report

set -e

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}🧪 Running Vibium Tests${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo ""

# Change to vibium directory
VIBIUM_DIR="vibium"
if [ ! -d "$VIBIUM_DIR" ]; then
    echo -e "${RED}❌ Error: $VIBIUM_DIR directory not found${NC}"
    exit 1
fi

cd "$VIBIUM_DIR"

# Check if node_modules exists
if [ ! -d "node_modules" ]; then
    echo -e "${YELLOW}⚠️  node_modules not found. Installing dependencies...${NC}"
    npm install
    echo ""
fi

# Parse arguments
WATCH_MODE=false
UI_MODE=false
COVERAGE=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --watch)
            WATCH_MODE=true
            shift
            ;;
        --ui)
            UI_MODE=true
            shift
            ;;
        --coverage)
            COVERAGE=true
            shift
            ;;
        *)
            echo -e "${YELLOW}⚠️  Unknown option: $1${NC}"
            echo "Usage: ./scripts/run-vibium-tests.sh [--watch] [--ui] [--coverage]"
            exit 1
            ;;
    esac
done

# Build test command
TEST_CMD="npm test"

if [ "$WATCH_MODE" = true ]; then
    echo -e "${BLUE}👀 Running tests in watch mode...${NC}"
    TEST_CMD="npm run test:watch"
elif [ "$UI_MODE" = true ]; then
    echo -e "${BLUE}🖥️  Running tests with UI...${NC}"
    TEST_CMD="npm run test:ui"
fi

if [ "$COVERAGE" = true ]; then
    echo -e "${BLUE}📊 Coverage report will be generated${NC}"
    # Vitest coverage is configured in vitest.config.ts
    TEST_CMD="$TEST_CMD --coverage"
fi

echo ""
echo -e "${BLUE}📋 Test Configuration:${NC}"
echo "  Directory: $VIBIUM_DIR"
echo "  Command: $TEST_CMD"
echo ""

# Run tests
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
if eval "$TEST_CMD"; then
    echo ""
    echo -e "${GREEN}✅ Vibium tests completed successfully!${NC}"
    echo ""
    
    if [ "$COVERAGE" = true ]; then
        echo -e "${BLUE}📊 Coverage report available in: $VIBIUM_DIR/coverage/${NC}"
    fi
    
    echo -e "${BLUE}📊 Test results available in: $VIBIUM_DIR/.vitest/${NC}"
    exit 0
else
    echo ""
    echo -e "${RED}❌ Vibium tests failed${NC}"
    exit 1
fi
