#!/bin/bash
# scripts/temp/test-port-config-removal.sh
# Test Port Configuration Script After ports.json Removal
#
# Purpose: Verify that port-config.sh works correctly after removing ports.json dependency
#
# Usage:
#   ./scripts/temp/test-port-config-removal.sh
#
# Description:
#   Tests the port-config.sh script to ensure it works correctly after the removal
#   of ports.json fallback. Verifies that get_ports_for_environment() returns
#   correct values for all environments (dev, test, prod).
#
# Dependencies:
#   - scripts/ci/port-config.sh
#   - jq (optional, but recommended)
#   - config/environments.json file
#
# Output:
#   - Console output showing test results for each environment
#   - Exits with 0 on success, non-zero on failure
#
# Notes:
#   - This is a temporary test script for verifying ports.json removal
#   - Should be deleted after verification is complete
#
# Last Updated: January 2026

set -e

# Get the script directory (project root)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$SCRIPT_DIR"

echo "Testing port-config.sh after ports.json removal..."
echo

# Source the script
source scripts/ci/port-config.sh

# Test each environment
for env in dev test prod; do
    echo "Testing environment: $env"
    eval "$(get_ports_for_environment "$env")"
    echo "  FRONTEND_PORT=$FRONTEND_PORT"
    echo "  API_PORT=$API_PORT"
    echo "  FRONTEND_URL=$FRONTEND_URL"
    echo "  API_URL=$API_URL"
    echo
    
    # Verify values are set
    if [ -z "$FRONTEND_PORT" ] || [ -z "$API_PORT" ]; then
        echo "❌ Failed: Ports not set for $env"
        exit 1
    fi
    
    # Verify URLs are set
    if [ -z "$FRONTEND_URL" ] || [ -z "$API_URL" ]; then
        echo "❌ Failed: URLs not set for $env"
        exit 1
    fi
done

echo "✅ All port-config.sh tests passed!"
