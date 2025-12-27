#!/bin/bash
# Wait for a Service to Be Ready
# Usage: ./scripts/ci/wait-for-service.sh <url> <service-name> [timeout-seconds] [check-interval]
#
# Arguments:
#   url            - Service URL to check (e.g., http://localhost:3003)
#   service-name   - Human-readable service name (e.g., "Frontend", "Backend API", "Selenium Grid")
#   timeout-seconds - Maximum time to wait in seconds (default: 60)
#   check-interval - Interval between checks in seconds (default: 2)
#
# Examples:
#   ./scripts/ci/wait-for-service.sh http://localhost:3003 "Frontend" 30
#   ./scripts/ci/wait-for-service.sh http://localhost:8003/docs "Backend API" 60 2
#   ./scripts/ci/wait-for-service.sh http://localhost:4444/wd/hub/status "Selenium Grid" 90

set -e

URL=${1}
SERVICE_NAME=${2:-"Service"}
TIMEOUT=${3:-60}
CHECK_INTERVAL=${4:-2}

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

