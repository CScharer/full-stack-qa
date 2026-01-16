#!/bin/bash
# scripts/ci/determine-ports.sh
# Port and URL Determination
#
# Purpose: Determine ports and URLs for an environment and output them for GitHub Actions
#
# Usage:
#   ./scripts/ci/determine-ports.sh <ENVIRONMENT>
#
# Parameters:
#   ENVIRONMENT   Environment name: dev, test, or prod
#
# Examples:
#   ./scripts/ci/determine-ports.sh dev
#   ./scripts/ci/determine-ports.sh test
#   ./scripts/ci/determine-ports.sh prod
#
# Description:
#   This script determines ports and URLs for a given environment using the centralized
#   port-config.sh configuration. It outputs the values to GitHub Actions output variables
#   for use in workflow steps.
#
#   Output variables:
#   - frontend_port: Frontend service port
#   - api_port: Backend API port
#   - backend_url: Backend API base URL
#   - frontend_url: Frontend application URL
#
# Dependencies:
#   - scripts/ci/port-config.sh (centralized port configuration)
#   - config/environments.json (configuration source)
#   - jq (JSON processor, optional but recommended)
#   - GitHub Actions environment (for $GITHUB_OUTPUT)
#
# Output:
#   - GitHub Actions output variables (via $GITHUB_OUTPUT)
#   - Exit code: 0 on success, non-zero on failure
#
# Notes:
#   - Used in GitHub Actions workflows to set environment-specific values
#   - Falls back to hardcoded values if port-config.sh is unavailable
#   - Single source of truth: config/environments.json
#
# Last Updated: January 2026

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
