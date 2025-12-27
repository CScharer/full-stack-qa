#!/bin/bash
# Verify Backend and Frontend Services Are Running
# Usage: ./scripts/ci/verify-services.sh <base-url> [timeout-seconds]
#
# Arguments:
#   base-url       - Base URL for the environment (e.g., http://localhost:3003)
#   timeout-seconds - Timeout in seconds for waiting for services (default: 30)
#
# Examples:
#   ./scripts/ci/verify-services.sh http://localhost:3003
#   ./scripts/ci/verify-services.sh http://localhost:3004 60

set -e

BASE_URL=${1}
TIMEOUT=${2:-30}

# Validate required arguments
if [ -z "$BASE_URL" ]; then
  echo "❌ Error: Base URL is required"
  echo "Usage: $0 <base-url> [timeout-seconds]"
  exit 1
fi

echo "🔍 Verifying services are running..."
echo "   Base URL: $BASE_URL"
echo "   Timeout: ${TIMEOUT}s"

# Extract ports from base_url
if [[ "$BASE_URL" == *":3003"* ]]; then
  FRONTEND_PORT=3003
  API_PORT=8003
elif [[ "$BASE_URL" == *":3004"* ]]; then
  FRONTEND_PORT=3004
  API_PORT=8004
elif [[ "$BASE_URL" == *":3005"* ]]; then
  FRONTEND_PORT=3005
  API_PORT=8005
else
  FRONTEND_PORT=3003
  API_PORT=8003
fi

echo "   Frontend Port: $FRONTEND_PORT"
echo "   API Port: $API_PORT"
echo ""

# Check Frontend
echo "Checking Frontend on port $FRONTEND_PORT..."
timeout "$TIMEOUT" bash -c "until curl -sf http://localhost:$FRONTEND_PORT > /dev/null; do echo '  Waiting for frontend...'; sleep 2; done" || {
  echo "❌ Frontend not responding on port $FRONTEND_PORT"
  echo "Checking if process is running:"
  lsof -i :"$FRONTEND_PORT" || echo "No process found on port $FRONTEND_PORT"
  exit 1
}
echo "✅ Frontend is responding on port $FRONTEND_PORT"

# Check Backend
echo "Checking Backend on port $API_PORT..."
timeout "$TIMEOUT" bash -c "until curl -sf http://localhost:$API_PORT/docs > /dev/null; do echo '  Waiting for backend...'; sleep 2; done" || {
  echo "❌ Backend not responding on port $API_PORT"
  echo "Checking if process is running:"
  lsof -i :"$API_PORT" || echo "No process found on port $API_PORT"
  exit 1
}
echo "✅ Backend is responding on port $API_PORT"

echo ""
echo "✅ All services verified and ready!"

