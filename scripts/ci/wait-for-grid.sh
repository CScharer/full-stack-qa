#!/bin/bash
# scripts/ci/wait-for-grid.sh
# Waits for Selenium Grid to be ready

set -e

GRID_URL=${1:-"http://localhost:4444/wd/hub/status"}
TIMEOUT=${2:-60}

echo "⏳ Waiting for Grid at $GRID_URL..."

if timeout "$TIMEOUT" bash -c "until curl -sf $GRID_URL; do sleep 2; done"; then
  sleep 5
  echo "✅ Grid ready!"
else
  echo "❌ Grid failed to start within ${TIMEOUT}s"
  exit 1
fi
