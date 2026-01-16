#!/bin/bash
# scripts/ci/port-config.sh
# Centralized Port Configuration (Legacy - Use env-config.sh for new code)
#
# Purpose: Provide port and URL configuration for all environments (legacy fallback)
#
# Usage:
#   # Source this file in other scripts:
#   source scripts/ci/port-config.sh
#
#   # Then use the function:
#   get_ports_for_environment "dev"
#
# Description:
#   This script provides port and URL configuration functions, reading from:
#   - Primary: config/environments.json (comprehensive config)
#   - Fallback: config/ports.json (legacy, deprecated)
#
#   ⚠️ DEPRECATED: This script is maintained for backward compatibility.
#   New code should use env-config.sh instead, which provides more comprehensive
#   configuration options.
#
# Dependencies:
#   - jq (JSON processor) - recommended but not required
#   - config/environments.json (preferred) or config/ports.json (fallback)
#
# Functions Provided:
#   - get_ports_for_environment(env) - Get frontend port, backend port, and URLs
#
# Notes:
#   - This script is designed to be SOURCED, not executed directly
#   - Falls back to ports.json if environments.json is unavailable
#   - Legacy script - use env-config.sh for new code
#   - See config/README.md for deprecation timeline
#
# Last Updated: January 2026

set -e

# Get script directory to find config JSON
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
ENV_CONFIG_JSON="${SCRIPT_DIR}/config/environments.json"
PORT_CONFIG_JSON="${SCRIPT_DIR}/config/ports.json"  # Fallback for backward compatibility

# Configuration (Single Source of Truth)
# Documented in: docs/new_app/ONE_GOAL.md
# Source: config/environments.json (comprehensive config) or config/ports.json (fallback)

get_ports_for_environment() {
    local env=$(echo "${1:-dev}" | tr '[:upper:]' '[:lower:]')
    
    # Try to read from environments.json first (preferred - comprehensive config)
    if [ -f "$ENV_CONFIG_JSON" ] && command -v jq &> /dev/null; then
        local frontend_port=$(jq -r ".environments[\"$env\"].frontend.port" "$ENV_CONFIG_JSON" 2>/dev/null)
        local backend_port=$(jq -r ".environments[\"$env\"].backend.port" "$ENV_CONFIG_JSON" 2>/dev/null)
        local frontend_url=$(jq -r ".environments[\"$env\"].frontend.url" "$ENV_CONFIG_JSON" 2>/dev/null)
        local backend_url=$(jq -r ".environments[\"$env\"].backend.url" "$ENV_CONFIG_JSON" 2>/dev/null)
        
        if [ "$frontend_port" != "null" ] && [ -n "$frontend_port" ]; then
            echo "FRONTEND_PORT=$frontend_port"
            echo "API_PORT=$backend_port"
            echo "FRONTEND_URL=$frontend_url"
            echo "API_URL=$backend_url"
            return 0
        fi
    fi
    
    # Fallback to ports.json (backward compatibility)
    if [ -f "$PORT_CONFIG_JSON" ] && command -v jq &> /dev/null; then
        local frontend_port=$(jq -r ".[\"$env\"].frontend.port" "$PORT_CONFIG_JSON" 2>/dev/null)
        local backend_port=$(jq -r ".[\"$env\"].backend.port" "$PORT_CONFIG_JSON" 2>/dev/null)
        local frontend_url=$(jq -r ".[\"$env\"].frontend.url" "$PORT_CONFIG_JSON" 2>/dev/null)
        local backend_url=$(jq -r ".[\"$env\"].backend.url" "$PORT_CONFIG_JSON" 2>/dev/null)
        
        if [ "$frontend_port" != "null" ] && [ -n "$frontend_port" ]; then
            echo "FRONTEND_PORT=$frontend_port"
            echo "API_PORT=$backend_port"
            echo "FRONTEND_URL=$frontend_url"
            echo "API_URL=$backend_url"
            return 0
        fi
    fi
    
    # Fallback to hardcoded values if JSON not available or jq not installed
    case "$env" in
        dev)
            echo "FRONTEND_PORT=3003"
            echo "API_PORT=8003"
            echo "FRONTEND_URL=http://localhost:3003"
            echo "API_URL=http://localhost:8003"
            ;;
        test)
            echo "FRONTEND_PORT=3004"
            echo "API_PORT=8004"
            echo "FRONTEND_URL=http://localhost:3004"
            echo "API_URL=http://localhost:8004"
            ;;
        prod)
            echo "FRONTEND_PORT=3005"
            echo "API_PORT=8005"
            echo "FRONTEND_URL=http://localhost:3005"
            echo "API_URL=http://localhost:8005"
            ;;
        *)
            echo "❌ Unknown environment: $env" >&2
            echo "   Valid environments: dev, test, prod" >&2
            return 1
            ;;
    esac
}

# Export ports for current environment if ENVIRONMENT is set
if [ -n "${ENVIRONMENT:-}" ]; then
    eval "$(get_ports_for_environment "$ENVIRONMENT")"
    export FRONTEND_PORT API_PORT FRONTEND_URL API_URL
fi
