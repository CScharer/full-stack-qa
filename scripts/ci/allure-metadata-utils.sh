#!/bin/bash
# Allure Metadata Utilities
# Shared functions for adding verification metadata to Allure test results.
# Source this file in other scripts: source "$(dirname "$0")/allure-metadata-utils.sh"
#
# Functions:
#   get_verification_metadata_json <env> [test_timestamp] [base_url_env_var]
#     - Generate verification metadata as JSON string for bash-based converters
#     - Returns JSON array of parameter objects
#     - Usage: params_json=$(get_verification_metadata_json "dev")

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
    local base_url="${!base_url_env_var:-unknown}"
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

