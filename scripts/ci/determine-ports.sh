#!/bin/bash
# scripts/ci/determine-ports.sh
# Determines ports and URLs based on environment
# Uses centralized port-config.sh as single source of truth

set -e

ENVIRONMENT=$1

# Get script directory to source port config
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PORT_CONFIG="${SCRIPT_DIR}/scripts/ci/port-config.sh"

if [ -f "$PORT_CONFIG" ]; then
    # Source centralized port configuration
    source "$PORT_CONFIG"
    # Get ports for this environment
    PORT_VARS=$(get_ports_for_environment "$ENVIRONMENT")
    eval "$PORT_VARS"
    
    # Output to GitHub Actions
    echo "frontend_port=${FRONTEND_PORT}" >> $GITHUB_OUTPUT
    echo "api_port=${API_PORT}" >> $GITHUB_OUTPUT
    echo "backend_url=${API_URL}" >> $GITHUB_OUTPUT
    echo "frontend_url=${FRONTEND_URL}" >> $GITHUB_OUTPUT
else
    # Fallback if port-config.sh doesn't exist (shouldn't happen)
    echo "⚠️  Warning: port-config.sh not found, using fallback values" >&2
    if [ "$ENVIRONMENT" == "dev" ]; then
        echo "frontend_port=3003" >> $GITHUB_OUTPUT
        echo "api_port=8003" >> $GITHUB_OUTPUT
        echo "backend_url=http://localhost:8003" >> $GITHUB_OUTPUT
        echo "frontend_url=http://localhost:3003" >> $GITHUB_OUTPUT
    elif [ "$ENVIRONMENT" == "test" ]; then
        echo "frontend_port=3004" >> $GITHUB_OUTPUT
        echo "api_port=8004" >> $GITHUB_OUTPUT
        echo "backend_url=http://localhost:8004" >> $GITHUB_OUTPUT
        echo "frontend_url=http://localhost:3004" >> $GITHUB_OUTPUT
    else
        echo "❌ Unknown environment: $ENVIRONMENT"
        exit 1
    fi
fi

echo "✅ Determined ports for environment: $ENVIRONMENT"
