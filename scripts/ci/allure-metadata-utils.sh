#!/bin/bash
# scripts/ci/allure-metadata-utils.sh
# Allure Metadata Utilities Library
#
# Purpose: Provide shared functions for adding verification metadata to Allure test results
#
# Usage:
#   # Source this file in other scripts:
#   source scripts/ci/allure-metadata-utils.sh
#   # Or with full path:
#   source "$(dirname "$0")/allure-metadata-utils.sh"
#
# Functions:
#   get_verification_metadata_json <env> [test_timestamp] [base_url_env_var]
#     - Generate verification metadata as JSON string for bash-based converters
#     - Returns JSON array of parameter objects
#     - Usage: params_json=$(get_verification_metadata_json "dev")
#
# Description:
#   This library provides shared functions for adding verification metadata to Allure
#   test results. It's designed to be sourced by other scripts that convert test
#   results to Allure format (e.g., convert-cypress-to-allure.sh, convert-playwright-to-allure.sh).
#
# Dependencies:
#   - Bash shell
#   - jq (JSON processor) for JSON manipulation
#   - config/environments.json (for environment configuration)
#
# Examples:
#   # In another script:
#   source scripts/ci/allure-metadata-utils.sh
#   params=$(get_verification_metadata_json "dev" "$(date -u +%Y-%m-%dT%H:%M:%SZ)" "FRONTEND_URL")
#   echo "$params" | jq '.'
#
# Notes:
#   - This script is designed to be SOURCED, not executed directly
#   - Provides consistent metadata format across all Allure converters
#   - Includes environment, timestamp, and base URL information
#
# Last Updated: January 2026

# Function to generate verification metadata as JSON for bash-based converters
get_verification_metadata_json() {
    local env="$1"
    local test_timestamp="${2:-}"
    local base_url_env_var="${3:-BASE_URL}"
    
    # Return empty array if env is not valid
    if [ -z "$env" ] || [ "$env" = "unknown" ] || [ "$env" = "combined" ]; then
        echo "[]"
        return
    fi
    
    # Get values from environment variables
    local base_url="${!base_url_env_var:-}"
    # Derive BASE_URL from environment if not set
    if [ -z "$base_url" ] || [ "$base_url" = "unknown" ]; then
        case "$env" in
            dev)
                base_url="http://localhost:3003"
                ;;
            test)
                base_url="http://localhost:3004"
                ;;
            prod)
                base_url="http://localhost:3005"
                ;;
            *)
                base_url="unknown"
                ;;
        esac
    fi
    local ci_run_id="${GITHUB_RUN_ID:-local}"
    local ci_run_number="${GITHUB_RUN_NUMBER:-unknown}"
    
    # Get test execution time
    local test_timestamp_iso
    if [ -n "$test_timestamp" ] && [ "$test_timestamp" != "None" ] && [ "$test_timestamp" != "0" ]; then
        # Convert milliseconds to ISO timestamp (requires Python or date command)
        if command -v python3 &> /dev/null; then
            test_timestamp_iso=$(python3 -c "from datetime import datetime; print(datetime.fromtimestamp($test_timestamp / 1000).isoformat())")
        else
            # Fallback: use current time
            test_timestamp_iso=$(date -u +"%Y-%m-%dT%H:%M:%S" 2>/dev/null || date +"%Y-%m-%dT%H:%M:%S")
        fi
    else
        # Use current time
        test_timestamp_iso=$(date -u +"%Y-%m-%dT%H:%M:%S" 2>/dev/null || date +"%Y-%m-%dT%H:%M:%S")
    fi
    
    # Build JSON array
    cat <<EOF
[
    {
      "name": "Base URL",
      "value": "$base_url"
    },
    {
      "name": "Test Execution Time",
      "value": "$test_timestamp_iso"
    },
    {
      "name": "CI Run ID",
      "value": "$ci_run_id"
    },
    {
      "name": "CI Run Number",
      "value": "$ci_run_number"
    }
]
EOF
}

