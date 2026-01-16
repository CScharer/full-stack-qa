#!/bin/bash
# scripts/ci/wait-for-service.sh
# Service Waiter (Reusable Utility)
#
# Purpose: Wait for any HTTP service to be ready by checking its endpoint
#
# Usage:
#   ./scripts/ci/wait-for-service.sh <URL> <SERVICE_NAME> [TIMEOUT_SECONDS] [CHECK_INTERVAL]
#
# Parameters:
#   URL             Service URL to check (e.g., http://localhost:3003, http://localhost:8003/docs)
#   SERVICE_NAME    Human-readable service name (e.g., "Frontend", "Backend API", "Selenium Grid")
#   TIMEOUT_SECONDS Maximum time to wait in seconds (default: 5)
#   CHECK_INTERVAL  Interval between checks in seconds (default: 1)
#
# Examples:
#   ./scripts/ci/wait-for-service.sh http://localhost:3003 "Frontend" 5
#   ./scripts/ci/wait-for-service.sh http://localhost:8003/docs "Backend API" 5 1
#   ./scripts/ci/wait-for-service.sh http://localhost:4444/wd/hub/status "Selenium Grid" 5
#
# Description:
#   This is a reusable utility script that waits for any HTTP service to become ready.
#   It checks the service endpoint at regular intervals until it responds successfully
#   or the timeout is reached. Used by wait-for-services.sh and wait-for-grid.sh.
#
# Dependencies:
#   - curl (for HTTP health checks)
#   - Service must be starting or already running
#
# Output:
#   - Console output showing wait progress
#   - Exit code: 0 if service ready, 1 if timeout
#
# Notes:
#   - Reusable utility for waiting on any HTTP service
#   - Used by other wait scripts (wait-for-services.sh, wait-for-grid.sh)
#   - Aggressive timeout (5 seconds) for fast CI/CD execution
#   - Provides clear error messages on timeout
#
# Last Updated: January 2026

set -e

URL=${1}
SERVICE_NAME=${2:-"Service"}
TIMEOUT=${3:-5}
CHECK_INTERVAL=${4:-1}

# Validate required arguments
if [ -z "$URL" ]; then
  echo "❌ Error: URL is required"
  echo "Usage: $0 <url> <service-name> [timeout-seconds] [check-interval]"
  exit 1
fi

echo "⏳ Waiting for $SERVICE_NAME to be ready..."
echo "   URL: $URL"
echo "   Timeout: ${TIMEOUT}s"
echo "   Check Interval: ${CHECK_INTERVAL}s"

# Calculate number of attempts
MAX_ATTEMPTS=$((TIMEOUT / CHECK_INTERVAL))
elapsed=0
attempt=0

while [ $elapsed -lt $TIMEOUT ]; do
  if curl -sf "$URL" > /dev/null 2>&1; then
    echo "✅ $SERVICE_NAME is ready!"
    exit 0
  fi
  
  sleep "$CHECK_INTERVAL"
  elapsed=$((elapsed + CHECK_INTERVAL))
  attempt=$((attempt + 1))
  
  # Show progress every 10 seconds or on last attempt
  if [ $((elapsed % 10)) -eq 0 ] || [ $elapsed -ge $TIMEOUT ]; then
    echo "   Still waiting... (${elapsed}s/${TIMEOUT}s, attempt ${attempt}/${MAX_ATTEMPTS})"
  fi
done

echo "❌ $SERVICE_NAME failed to start within ${TIMEOUT}s"
echo "   URL: $URL"
echo "   Last attempt: ${attempt}/${MAX_ATTEMPTS}"
exit 1

