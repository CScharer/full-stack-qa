#!/bin/bash
# scripts/ci/port-config.sh
# Centralized port configuration for all environments
# This file reads from config/ports.json (single source of truth)
# All scripts should source this file to get port values

set -e

# Get script directory to find port config JSON
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PORT_CONFIG_JSON="${SCRIPT_DIR}/config/ports.json"

# Port Configuration (Single Source of Truth)
# Documented in: docs/new_app/ONE_GOAL.md
# Source: config/ports.json (shared with TypeScript/JavaScript)

get_ports_for_environment() {
    local env=$(echo "${1:-dev}" | tr '[:upper:]' '[:lower:]')
    
    # Try to read from JSON config if available (preferred)
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
