#!/bin/bash
# scripts/ci/wait-for-services.sh
# Application Services Waiter
#
# Purpose: Wait for Backend and Frontend services to be ready before running tests
#
# Usage:
#   ./scripts/ci/wait-for-services.sh <FRONTEND_URL> <BACKEND_URL> [MAX_ATTEMPTS] [ENVIRONMENT]
#
# Parameters:
#   FRONTEND_URL  Frontend service URL (e.g., http://localhost:3003)
#   BACKEND_URL   Backend API URL (e.g., http://localhost:8003)
#   MAX_ATTEMPTS  Maximum number of connection attempts (default: 5)
#   ENVIRONMENT   Environment name for logging (default: "unknown")
#
# Examples:
#   ./scripts/ci/wait-for-services.sh http://localhost:3003 http://localhost:8003
#   ./scripts/ci/wait-for-services.sh http://localhost:3004 http://localhost:8004 10 test
#
# Description:
#   This script waits for both Backend and Frontend services to be ready by checking
#   their HTTP endpoints. It uses the centralized wait-for-service.sh utility with
#   aggressive timeouts (5 seconds) for fast CI/CD pipeline execution.
#
# Dependencies:
#   - scripts/ci/wait-for-service.sh (centralized wait utility)
#   - curl (for HTTP health checks)
#   - Services must be starting or already running
#
# Output:
#   - Console output showing wait progress
#   - Exit code: 0 if both services ready, 1 if timeout
#
# Notes:
#   - Uses aggressive 5-second timeout for fast CI/CD execution
#   - Wrapper around wait-for-service.sh for convenience
#   - Fails fast if either service doesn't become ready
#   - Used in CI/CD pipeline before test execution
#
# Last Updated: January 2026

set -e

FRONTEND_URL=$1
BACKEND_URL=$2
MAX_ATTEMPTS=${3:-5}
ENVIRONMENT=${4:-"unknown"}

# Get script directory to find wait-for-service.sh
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
WAIT_SCRIPT="${SCRIPT_DIR}/scripts/ci/wait-for-service.sh"

# Use aggressive timeout (max 5 seconds)
TIMEOUT=5

echo "⏳ Waiting for services to be ready ($ENVIRONMENT)..."
echo "  Frontend: $FRONTEND_URL"
echo "  Backend: $BACKEND_URL"

# Use centralized wait-for-service.sh utility if available
if [ -f "$WAIT_SCRIPT" ]; then
  # Wait for Frontend
  "$WAIT_SCRIPT" "$FRONTEND_URL" "Frontend ($ENVIRONMENT)" "$TIMEOUT" 1 || {
    echo "❌ Frontend failed to start within the time limit"
    echo "Frontend: $FRONTEND_URL"
    exit 1
  }
  
  # Wait for Backend (check health endpoint)
  BACKEND_HEALTH_URL="${BACKEND_URL}/health"
  "$WAIT_SCRIPT" "$BACKEND_HEALTH_URL" "Backend ($ENVIRONMENT)" "$TIMEOUT" 1 || {
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
    sleep 1
  done
  
  if [ "$READY" = "false" ]; then
    echo "❌ Services failed to start within the time limit"
    echo "Frontend: $FRONTEND_URL"
    echo "Backend: $BACKEND_URL"
    exit 1
  fi
fi
