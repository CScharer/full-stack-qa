#!/bin/bash
# scripts/ci/verify-services.sh
# Service Verification
#
# Purpose: Verify that Backend and Frontend services are running and responding
#
# Usage:
#   ./scripts/ci/verify-services.sh <BASE_URL> [TIMEOUT_SECONDS]
#
# Parameters:
#   BASE_URL         Base URL for the environment (e.g., http://localhost:3003)
#   TIMEOUT_SECONDS  Timeout in seconds for waiting for services (default: 5, or from config)
#
# Examples:
#   ./scripts/ci/verify-services.sh http://localhost:3003
#   ./scripts/ci/verify-services.sh http://localhost:3004 10
#
# Description:
#   This script verifies that both Backend and Frontend services are running and
#   responding to HTTP requests. It checks:
#   - Frontend service (BASE_URL)
#   - Backend API service (BASE_URL/api/v1/health or /docs)
#
#   Uses timeout from centralized config (config/environments.json) if available.
#
# Dependencies:
#   - curl (for HTTP health checks)
#   - config/environments.json (for timeout configuration, optional)
#   - jq (for reading config, optional)
#   - Services must be running
#
# Output:
#   - Console output showing verification status
#   - Exit code: 0 if both services verified, 1 if verification fails
#
# Notes:
#   - Used in CI/CD pipeline to verify services before tests
#   - Reads timeout from centralized config if available
#   - Fails fast if either service is not responding
#   - Provides clear error messages for troubleshooting
#
# Last Updated: January 2026

set -e

BASE_URL=${1}
# Get timeout from centralized config if available
SCRIPT_DIR_FULL="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ENV_CONFIG="${SCRIPT_DIR_FULL}/config/environments.json"
if [ -f "$ENV_CONFIG" ] && command -v jq &> /dev/null; then
    DEFAULT_TIMEOUT=$(jq -r '.timeouts.serviceVerification' "$ENV_CONFIG" 2>/dev/null || echo "5")
else
    DEFAULT_TIMEOUT=5
fi
TIMEOUT=${2:-$DEFAULT_TIMEOUT}

# Validate required arguments
if [ -z "$BASE_URL" ]; then
  echo "❌ Error: Base URL is required"
  echo "Usage: $0 <base-url> [timeout-seconds]"
  exit 1
fi

# Get script directory to source port config and utilities
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PORT_CONFIG="${SCRIPT_DIR}/scripts/ci/port-config.sh"
PORT_UTILS="${SCRIPT_DIR}/scripts/ci/port-utils.sh"

# Source port utilities if available
if [ -f "$PORT_UTILS" ]; then
    source "$PORT_UTILS"
fi

# Source centralized port configuration
if [ -f "$PORT_CONFIG" ]; then
  source "$PORT_CONFIG"
else
  echo "⚠️  Warning: port-config.sh not found, using fallback values" >&2
fi

echo "🔍 Verifying services are running..."
echo "   Base URL: $BASE_URL"
echo "   Timeout: ${TIMEOUT}s"

# Extract environment from base_url by checking port
ENVIRONMENT="dev"  # Default to dev
if [[ "$BASE_URL" == *":3003"* ]]; then
  ENVIRONMENT="dev"
elif [[ "$BASE_URL" == *":3004"* ]]; then
  ENVIRONMENT="test"
elif [[ "$BASE_URL" == *":3005"* ]]; then
  ENVIRONMENT="prod"
fi

# Get ports for this environment using centralized config
if [ -f "$PORT_CONFIG" ]; then
  PORT_VARS=$(get_ports_for_environment "$ENVIRONMENT")
  eval "$PORT_VARS"
else
  # Fallback to hardcoded values if port-config.sh doesn't exist
  case "$ENVIRONMENT" in
    test)
      FRONTEND_PORT=3004
      API_PORT=8004
      ;;
    prod)
      FRONTEND_PORT=3005
      API_PORT=8005
      ;;
    *)
      FRONTEND_PORT=3003
      API_PORT=8003
      ;;
  esac
fi

echo "   Frontend Port: $FRONTEND_PORT"
echo "   API Port: $API_PORT"
echo ""

# Use centralized wait-for-service.sh utility
WAIT_SCRIPT="${SCRIPT_DIR}/scripts/ci/wait-for-service.sh"

# Check Frontend
if [ -f "$WAIT_SCRIPT" ]; then
  "$WAIT_SCRIPT" "http://localhost:$FRONTEND_PORT" "Frontend" "$TIMEOUT" 2 || {
    echo "❌ Frontend not responding on port $FRONTEND_PORT"
    echo "Checking if process is running:"
    if [ -f "$PORT_UTILS" ]; then
      check_port_status "$FRONTEND_PORT" "Frontend" || echo "No process found on port $FRONTEND_PORT"
    else
      lsof -i :"$FRONTEND_PORT" || echo "No process found on port $FRONTEND_PORT"
    fi
    exit 1
  }
else
  # Fallback to inline logic if utility doesn't exist
  echo "Checking Frontend on port $FRONTEND_PORT..."
  timeout "$TIMEOUT" bash -c "until curl -sf http://localhost:$FRONTEND_PORT > /dev/null; do echo '  Waiting for frontend...'; sleep 1; done" || {
    echo "❌ Frontend not responding on port $FRONTEND_PORT"
    echo "Checking if process is running:"
    if [ -f "$PORT_UTILS" ]; then
      check_port_status "$FRONTEND_PORT" "Frontend" || echo "No process found on port $FRONTEND_PORT"
    else
      lsof -i :"$FRONTEND_PORT" || echo "No process found on port $FRONTEND_PORT"
    fi
    exit 1
  }
  echo "✅ Frontend is responding on port $FRONTEND_PORT"
fi

# Check Backend
if [ -f "$WAIT_SCRIPT" ]; then
  "$WAIT_SCRIPT" "http://localhost:$API_PORT/docs" "Backend" "$TIMEOUT" 2 || {
    echo "❌ Backend not responding on port $API_PORT"
    echo "Checking if process is running:"
    if [ -f "$PORT_UTILS" ]; then
      check_port_status "$API_PORT" "Backend" || echo "No process found on port $API_PORT"
    else
      lsof -i :"$API_PORT" || echo "No process found on port $API_PORT"
    fi
    exit 1
  }
else
  # Fallback to inline logic if utility doesn't exist
  echo "Checking Backend on port $API_PORT..."
  timeout "$TIMEOUT" bash -c "until curl -sf http://localhost:$API_PORT/docs > /dev/null; do echo '  Waiting for backend...'; sleep 1; done" || {
    echo "❌ Backend not responding on port $API_PORT"
    echo "Checking if process is running:"
    if [ -f "$PORT_UTILS" ]; then
      check_port_status "$API_PORT" "Backend" || echo "No process found on port $API_PORT"
    else
      lsof -i :"$API_PORT" || echo "No process found on port $API_PORT"
    fi
    exit 1
  }
  echo "✅ Backend is responding on port $API_PORT"
fi

echo ""
echo "✅ All services verified and ready!"

