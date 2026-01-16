#!/bin/bash
# scripts/ci/env-config.sh
# Comprehensive Environment Configuration Utility
#
# Purpose: Provide centralized environment configuration functions for all CI/CD scripts
#
# Usage:
#   # Source this file in other scripts:
#   source scripts/ci/env-config.sh
#
#   # Then use the functions:
#   get_ports_for_environment "dev"
#   get_database_for_environment "test"
#   get_api_endpoints "prod"
#
# Description:
#   This script provides functions to read configuration from config/environments.json
#   (single source of truth). It provides access to:
#   - Ports (frontend, backend)
#   - URLs (frontend, backend, API)
#   - Database paths and configuration
#   - API endpoints
#   - Timeouts
#   - CORS origins
#
#   All functions read from config/environments.json using jq.
#
# Dependencies:
#   - jq (JSON processor) - recommended but not required (falls back gracefully)
#   - config/environments.json (configuration file)
#
# Functions Provided:
#   - get_ports_for_environment(env) - Get frontend and backend ports
#   - get_database_for_environment(env) - Get database path
#   - get_api_endpoints(env) - Get API base path
#   - get_timeouts(env) - Get timeout values
#   - get_cors_origins(env) - Get CORS allowed origins
#
# Notes:
#   - This script is designed to be SOURCED, not executed directly
#   - Does not use 'set -e' to allow graceful error handling in parent scripts
#   - Falls back gracefully if jq is not available (with warnings)
#   - Single source of truth: config/environments.json
#
# Last Updated: January 2026

# Note: We don't use 'set -e' here because this script is sourced by other scripts
# and we want to handle errors gracefully rather than exiting the parent script

# Get script directory to find config JSON
# env-config.sh is in scripts/ci/, so we need to go up two levels to get to project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
ENV_CONFIG_JSON="${SCRIPT_DIR}/config/environments.json"

# Check if jq is available
if ! command -v jq &> /dev/null; then
    echo "⚠️  Warning: jq is not installed. Install it for full configuration support." >&2
    echo "   macOS: brew install jq" >&2
    echo "   Linux: apt-get install jq or yum install jq" >&2
fi

# Get configuration value from environments.json
get_config_value() {
    local env=$1
    local path=$2
    local default=$3
    
    if [ -f "$ENV_CONFIG_JSON" ] && command -v jq &> /dev/null; then
        local value=$(jq -r "$path" "$ENV_CONFIG_JSON" 2>/dev/null)
        if [ "$value" != "null" ] && [ -n "$value" ]; then
            echo "$value"
            return 0
        fi
    fi
    
    if [ -n "$default" ]; then
        echo "$default"
    fi
}

# Get all ports for an environment
get_ports_for_environment() {
    local env=$(echo "${1:-dev}" | tr '[:upper:]' '[:lower:]')
    
    if [ ! -f "$ENV_CONFIG_JSON" ]; then
        echo "❌ Error: Configuration file not found: $ENV_CONFIG_JSON" >&2
        echo "   Please ensure config/environments.json exists in the project root." >&2
        return 1
    fi
    
    if ! command -v jq &> /dev/null; then
        echo "❌ Error: jq is required but not installed." >&2
        echo "   Install jq to use centralized configuration:" >&2
        echo "   macOS: brew install jq" >&2
        echo "   Linux: apt-get install jq or yum install jq" >&2
        return 1
    fi
    
    local frontend_port=$(get_config_value "$env" ".environments[\"$env\"].frontend.port")
    local backend_port=$(get_config_value "$env" ".environments[\"$env\"].backend.port")
    local frontend_url=$(get_config_value "$env" ".environments[\"$env\"].frontend.url")
    local backend_url=$(get_config_value "$env" ".environments[\"$env\"].backend.url")
    
    if [ -z "$frontend_port" ] || [ -z "$backend_port" ]; then
        echo "❌ Error: Could not read port configuration for environment: $env" >&2
        echo "   Please check that config/environments.json contains valid configuration for '$env'." >&2
        return 1
    fi
    
    echo "FRONTEND_PORT=$frontend_port"
    echo "API_PORT=$backend_port"
    echo "FRONTEND_URL=$frontend_url"
    echo "API_URL=$backend_url"
    return 0
}

# Get database configuration for an environment
get_database_for_environment() {
    local env=$(echo "${1:-dev}" | tr '[:upper:]' '[:lower:]')
    
    if [ ! -f "$ENV_CONFIG_JSON" ]; then
        echo "❌ Error: Configuration file not found: $ENV_CONFIG_JSON" >&2
        echo "   Please ensure config/environments.json exists in the project root." >&2
        return 1
    fi
    
    if ! command -v jq &> /dev/null; then
        echo "❌ Error: jq is required but not installed." >&2
        echo "   Install jq to use centralized configuration:" >&2
        echo "   macOS: brew install jq" >&2
        echo "   Linux: apt-get install jq or yum install jq" >&2
        return 1
    fi
    
    local db_name=$(get_config_value "$env" ".environments[\"$env\"].database.name")
    local db_path=$(get_config_value "$env" ".environments[\"$env\"].database.path")
    local db_dir=$(get_config_value "$env" ".database.directory" "data/core")
    
    if [ -z "$db_name" ]; then
        echo "❌ Error: Could not read database configuration for environment: $env" >&2
        echo "   Please check that config/environments.json contains valid database configuration for '$env'." >&2
        return 1
    fi
    
    echo "DATABASE_NAME=$db_name"
    echo "DATABASE_PATH=$db_path"
    echo "DATABASE_DIR=$db_dir"
    return 0
}

# Get API endpoints
get_api_endpoints() {
    if [ ! -f "$ENV_CONFIG_JSON" ]; then
        echo "❌ Error: Configuration file not found: $ENV_CONFIG_JSON" >&2
        echo "   Please ensure config/environments.json exists in the project root." >&2
        return 1
    fi
    
    if ! command -v jq &> /dev/null; then
        echo "❌ Error: jq is required but not installed." >&2
        echo "   Install jq to use centralized configuration:" >&2
        echo "   macOS: brew install jq" >&2
        echo "   Linux: apt-get install jq or yum install jq" >&2
        return 1
    fi
    
    local base_path=$(get_config_value "" ".api.basePath" "/api/v1")
    local health=$(get_config_value "" ".api.healthEndpoint" "/health")
    local docs=$(get_config_value "" ".api.docsEndpoint" "/docs")
    local redoc=$(get_config_value "" ".api.redocEndpoint" "/redoc")
    
    echo "API_BASE_PATH=$base_path"
    echo "API_HEALTH_ENDPOINT=$health"
    echo "API_DOCS_ENDPOINT=$docs"
    echo "API_REDOC_ENDPOINT=$redoc"
    return 0
}

# Get timeout values
get_timeouts() {
    if [ ! -f "$ENV_CONFIG_JSON" ]; then
        echo "❌ Error: Configuration file not found: $ENV_CONFIG_JSON" >&2
        echo "   Please ensure config/environments.json exists in the project root." >&2
        return 1
    fi
    
    if ! command -v jq &> /dev/null; then
        echo "❌ Error: jq is required but not installed." >&2
        echo "   Install jq to use centralized configuration:" >&2
        echo "   macOS: brew install jq" >&2
        echo "   Linux: apt-get install jq or yum install jq" >&2
        return 1
    fi
    
    local startup=$(get_config_value "" ".timeouts.serviceStartup" "120")
    local verification=$(get_config_value "" ".timeouts.serviceVerification" "30")
    local api_client=$(get_config_value "" ".timeouts.apiClient" "10000")
    local web_server=$(get_config_value "" ".timeouts.webServer" "120000")
    local check_interval=$(get_config_value "" ".timeouts.checkInterval" "2")
    
    echo "SERVICE_STARTUP_TIMEOUT=$startup"
    echo "SERVICE_VERIFICATION_TIMEOUT=$verification"
    echo "API_CLIENT_TIMEOUT=$api_client"
    echo "WEB_SERVER_TIMEOUT=$web_server"
    echo "CHECK_INTERVAL=$check_interval"
    return 0
}

# Get CORS origins for an environment
# Returns as JSON array format (Pydantic Settings expects JSON for List fields)
get_cors_origins() {
    local env=$(echo "${1:-dev}" | tr '[:upper:]' '[:lower:]')
    
    if [ ! -f "$ENV_CONFIG_JSON" ]; then
        echo "❌ Error: Configuration file not found: $ENV_CONFIG_JSON" >&2
        echo "   Please ensure config/environments.json exists in the project root." >&2
        return 1
    fi
    
    if ! command -v jq &> /dev/null; then
        echo "❌ Error: jq is required but not installed." >&2
        echo "   Install jq to use centralized configuration:" >&2
        echo "   macOS: brew install jq" >&2
        echo "   Linux: apt-get install jq or yum install jq" >&2
        return 1
    fi
    
    # Get as JSON array (Pydantic Settings expects JSON format for List fields)
    local origins=$(jq -c ".environments[\"$env\"].corsOrigins" "$ENV_CONFIG_JSON" 2>/dev/null)
    if [ -z "$origins" ] || [ "$origins" = "null" ]; then
        echo "❌ Error: Could not read CORS origins configuration for environment: $env" >&2
        echo "   Please check that config/environments.json contains valid CORS configuration for '$env'." >&2
        return 1
    fi
    
    echo "CORS_ORIGINS=$origins"
    return 0
}

# Export all configuration for an environment
export_environment_config() {
    local env=$(echo "${1:-dev}" | tr '[:upper:]' '[:lower:]')
    
    eval "$(get_ports_for_environment "$env")"
    eval "$(get_database_for_environment "$env")"
    eval "$(get_api_endpoints)"
    eval "$(get_timeouts)"
    eval "$(get_cors_origins "$env")"
    
    export FRONTEND_PORT API_PORT FRONTEND_URL API_URL
    export DATABASE_NAME DATABASE_PATH DATABASE_DIR
    export API_BASE_PATH API_HEALTH_ENDPOINT API_DOCS_ENDPOINT API_REDOC_ENDPOINT
    export SERVICE_STARTUP_TIMEOUT SERVICE_VERIFICATION_TIMEOUT API_CLIENT_TIMEOUT WEB_SERVER_TIMEOUT CHECK_INTERVAL
    export CORS_ORIGINS
}

# Auto-export if ENVIRONMENT is set
if [ -n "${ENVIRONMENT:-}" ]; then
    export_environment_config "$ENVIRONMENT"
fi

