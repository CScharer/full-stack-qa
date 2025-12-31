#!/bin/bash
# scripts/ci/wait-for-grid.sh
# Waits for Selenium Grid to be ready (Optimized: Aggressive timeout and minimal sleep)
# This script is a wrapper around wait-for-service.sh for Selenium Grid

set -e

GRID_URL=${1:-"http://localhost:4444/wd/hub/status"}
TIMEOUT=${2:-5}

# Get script directory to find wait-for-service.sh
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
WAIT_SCRIPT="${SCRIPT_DIR}/scripts/ci/wait-for-service.sh"

# Use centralized wait-for-service.sh utility if available
if [ -f "$WAIT_SCRIPT" ]; then
  "$WAIT_SCRIPT" "$GRID_URL" "Selenium Grid" "$TIMEOUT" 1
  # No sleep needed - wait-for-service.sh only exits when Grid is ready
  echo "✅ Grid ready!"
else
  # Fallback to original logic if utility doesn't exist (optimized)
  echo "⏳ Waiting for Grid at $GRID_URL..."
  if timeout "$TIMEOUT" bash -c "until curl -sf $GRID_URL; do sleep 1; done"; then
    # No sleep needed - curl success confirms Grid is ready
    echo "✅ Grid ready!"
  else
    echo "❌ Grid failed to start within ${TIMEOUT}s"
    exit 1
  fi
fi
