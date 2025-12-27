#!/bin/bash
# scripts/ci/wait-for-services.sh
# Waits for Backend and Frontend services to be ready
# This script is a wrapper around wait-for-service.sh for application services

set -e

FRONTEND_URL=$1
BACKEND_URL=$2
MAX_ATTEMPTS=${3:-30}
ENVIRONMENT=${4:-"unknown"}

# Get script directory to find wait-for-service.sh
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
WAIT_SCRIPT="${SCRIPT_DIR}/scripts/ci/wait-for-service.sh"

# Calculate timeout from max attempts (each attempt is ~2 seconds)
TIMEOUT=$((MAX_ATTEMPTS * 2))

echo "⏳ Waiting for services to be ready ($ENVIRONMENT)..."
echo "  Frontend: $FRONTEND_URL"
echo "  Backend: $BACKEND_URL"

# Use centralized wait-for-service.sh utility if available
if [ -f "$WAIT_SCRIPT" ]; then
  # Wait for Frontend
  "$WAIT_SCRIPT" "$FRONTEND_URL" "Frontend ($ENVIRONMENT)" "$TIMEOUT" 2 || {
    echo "❌ Frontend failed to start within the time limit"
    echo "Frontend: $FRONTEND_URL"
    exit 1
  }
  
  # Wait for Backend (check health endpoint)
  BACKEND_HEALTH_URL="${BACKEND_URL}/health"
  "$WAIT_SCRIPT" "$BACKEND_HEALTH_URL" "Backend ($ENVIRONMENT)" "$TIMEOUT" 2 || {
    echo "❌ Backend failed to start within the time limit"
    echo "Backend: $BACKEND_URL"
    exit 1
  }
  
  echo "✅ Services are ready!"
else
  # Fallback to original logic if utility doesn't exist
  READY=false
  for i in $(seq 1 $MAX_ATTEMPTS); do
    if curl -sf "$FRONTEND_URL" >/dev/null 2>&1 && curl -sf "$BACKEND_URL/health" >/dev/null 2>&1; then
      echo "✅ Services are ready!"
      READY=true
      break
    fi
    echo "Waiting... ($i/$MAX_ATTEMPTS)"
    sleep 2
  done
  
  if [ "$READY" = "false" ]; then
    echo "❌ Services failed to start within the time limit"
    echo "Frontend: $FRONTEND_URL"
    echo "Backend: $BACKEND_URL"
    exit 1
  fi
fi
