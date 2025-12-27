#!/bin/bash
# scripts/ci/wait-for-grid.sh
# Waits for Selenium Grid to be ready
# This script is a wrapper around wait-for-service.sh for Selenium Grid

set -e

GRID_URL=${1:-"http://localhost:4444/wd/hub/status"}
TIMEOUT=${2:-60}

# Get script directory to find wait-for-service.sh
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
WAIT_SCRIPT="${SCRIPT_DIR}/scripts/ci/wait-for-service.sh"

# Use centralized wait-for-service.sh utility if available
if [ -f "$WAIT_SCRIPT" ]; then
  "$WAIT_SCRIPT" "$GRID_URL" "Selenium Grid" "$TIMEOUT" 2
  sleep 5  # Additional wait for Grid to fully initialize
  echo "✅ Grid ready!"
else
  # Fallback to original logic if utility doesn't exist
  echo "⏳ Waiting for Grid at $GRID_URL..."
  if timeout "$TIMEOUT" bash -c "until curl -sf $GRID_URL; do sleep 2; done"; then
    sleep 5
    echo "✅ Grid ready!"
  else
    echo "❌ Grid failed to start within ${TIMEOUT}s"
    exit 1
  fi
fi
