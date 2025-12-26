#!/bin/bash
# scripts/ci/port-config.sh
# Centralized port configuration for all environments
# This file is the single source of truth for port assignments
# All scripts should source this file to get port values

set -e

# Port Configuration (Single Source of Truth)
# Documented in: docs/new_app/ONE_GOAL.md
# 
# Environment Port Mapping:
# - DEV:  Frontend=3003, Backend=8003
# - TEST: Frontend=3004, Backend=8004
# - PROD: Frontend=3005, Backend=8005

get_ports_for_environment() {
    local env=$(echo "${1:-dev}" | tr '[:upper:]' '[:lower:]')
    
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
