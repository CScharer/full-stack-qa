#!/bin/bash
# scripts/ci/wait-for-grid.sh
# Waits for Selenium Grid to be ready (Optimized: Aggressive timeout and minimal sleep)
# This script is a wrapper around wait-for-service.sh for Selenium Grid
# Enhanced with optional version validation

set -e

GRID_URL=${1:-"http://localhost:4444/wd/hub/status"}
TIMEOUT=${2:-5}
SKIP_VERSION_CHECK=${SKIP_VERSION_CHECK:-"false"}
SELENIUM_VERSION=${SELENIUM_VERSION:-""}

# Get script directory to find wait-for-service.sh
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
WAIT_SCRIPT="${SCRIPT_DIR}/scripts/ci/wait-for-service.sh"

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${YELLOW}⏳ Waiting for Selenium Grid to be ready...${NC}"

# Use centralized wait-for-service.sh utility if available
if [ -f "$WAIT_SCRIPT" ]; then
  "$WAIT_SCRIPT" "$GRID_URL" "Selenium Grid" "$TIMEOUT" 1
  # No sleep needed - wait-for-service.sh only exits when Grid is ready
else
  # Fallback to original logic if utility doesn't exist (optimized)
  if timeout "$TIMEOUT" bash -c "until curl -sf $GRID_URL > /dev/null 2>&1; do sleep 1; done"; then
    # No sleep needed - curl success confirms Grid is ready
    :
  else
    echo -e "${RED}❌ Grid failed to start within ${TIMEOUT}s${NC}"
    exit 1
  fi
fi

echo -e "${GREEN}✅ Grid ready!${NC}"

# Optional version validation
if [ "$SKIP_VERSION_CHECK" != "true" ] && [ -n "$SELENIUM_VERSION" ]; then
  echo -e "${YELLOW}🔍 Validating Grid version...${NC}"
  
  # Extract base URL (remove /status if present)
  BASE_URL="${GRID_URL%/status}"
  BASE_URL="${BASE_URL%/wd/hub/status}"
  BASE_URL="${BASE_URL%/wd/hub}"
  
  # Ensure we have the status endpoint
  if [[ "$BASE_URL" != *"/wd/hub"* ]]; then
    BASE_URL="${BASE_URL}/wd/hub"
  fi
  STATUS_URL="${BASE_URL}/status"
  
  # Get Grid server version from status endpoint
  if command -v jq >/dev/null 2>&1; then
    GRID_VERSION=$(curl -sf "$STATUS_URL" | jq -r '.value.version // empty' 2>/dev/null || echo "")
  else
    # Fallback: use grep/sed if jq is not available
    GRID_VERSION=$(curl -sf "$STATUS_URL" | grep -o '"version"[[:space:]]*:[[:space:]]*"[^"]*"' | sed 's/.*"version"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/' || echo "")
  fi
  
  if [ -z "$GRID_VERSION" ]; then
    echo -e "${YELLOW}⚠️  Could not determine Grid server version, skipping validation${NC}"
  elif [ "$GRID_VERSION" != "$SELENIUM_VERSION" ]; then
    echo -e "${RED}❌ Version mismatch detected!${NC}"
    echo -e "${RED}   Grid server version: ${GRID_VERSION}${NC}"
    echo -e "${RED}   Expected version: ${SELENIUM_VERSION}${NC}"
    echo -e "${YELLOW}   Set SKIP_VERSION_CHECK=true to skip this check${NC}"
    exit 1
  else
    echo -e "${GREEN}✅ Version validation passed: ${GRID_VERSION}${NC}"
  fi
elif [ "$SKIP_VERSION_CHECK" = "true" ]; then
  echo -e "${YELLOW}⏭️  Version validation skipped (SKIP_VERSION_CHECK=true)${NC}"
elif [ -z "$SELENIUM_VERSION" ]; then
  echo -e "${YELLOW}⏭️  Version validation skipped (SELENIUM_VERSION not set)${NC}"
fi
