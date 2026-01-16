#!/bin/bash
# scripts/lib/common.sh
# Common functions and utilities for shell scripts
# 
# Usage:
#   source "$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)/scripts/lib/common.sh"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Get script directory (project root)
# Usage: SCRIPT_DIR=$(get_script_dir)
get_script_dir() {
    echo "$(cd "$(dirname "${BASH_SOURCE[1]}")/.." && pwd)"
}

# Parse environment parameter from command line arguments
# Sets ENVIRONMENT_PARAM variable
# Usage: parse_environment_param "$@"
parse_environment_param() {
    ENVIRONMENT_PARAM=""
    while [[ $# -gt 0 ]]; do
        case $1 in
            --env|-e)
                ENVIRONMENT_PARAM="$2"
                shift 2
                ;;
            --env=*|-e=*)
                ENVIRONMENT_PARAM="${1#*=}"
                shift
                ;;
            *)
                # Not an environment parameter, continue
                shift
                ;;
        esac
    done
}

# Set and validate environment
# Sets ENVIRONMENT variable with priority: command-line param > env var > default
# Validates that environment is one of: dev, test, prod
# Usage: set_and_validate_environment [default_env]
# Returns: 0 if valid, 1 if invalid
set_and_validate_environment() {
    local default_env="${1:-dev}"
    
    # Set environment with priority: command-line param > env var > default
    if [ -n "$ENVIRONMENT_PARAM" ]; then
        ENVIRONMENT=$(echo "$ENVIRONMENT_PARAM" | tr '[:upper:]' '[:lower:]')
    elif [ -n "$ENVIRONMENT" ]; then
        ENVIRONMENT=$(echo "$ENVIRONMENT" | tr '[:upper:]' '[:lower:]')
    else
        ENVIRONMENT="$default_env"
    fi
    
    # Validate environment value
    if [[ ! "$ENVIRONMENT" =~ ^(dev|test|prod)$ ]]; then
        echo -e "${RED}❌ Invalid environment: '$ENVIRONMENT'${NC}" >&2
        echo -e "${YELLOW}   Must be one of: dev, test, prod${NC}" >&2
        return 1
    fi
    
    return 0
}

# Print help text header
# Usage: print_help_header "Usage: ./script.sh [OPTIONS]"
print_help_header() {
    local usage_text="$1"
    echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}${usage_text}${NC}"
    echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
    echo ""
}

# Print help section header
# Usage: print_help_section "SECTION NAME"
print_help_section() {
    local section_name="$1"
    echo -e "${YELLOW}${section_name}:${NC}"
}

# Print help example
# Usage: print_help_example "./script.sh --env test"
print_help_example() {
    local example="$1"
    echo "  $example"
}

# Print error message
# Usage: print_error "Error message"
print_error() {
    local message="$1"
    echo -e "${RED}❌ ${message}${NC}" >&2
}

# Print warning message
# Usage: print_warning "Warning message"
print_warning() {
    local message="$1"
    echo -e "${YELLOW}⚠️  ${message}${NC}" >&2
}

# Print success message
# Usage: print_success "Success message"
print_success() {
    local message="$1"
    echo -e "${GREEN}✅ ${message}${NC}"
}

# Print info message
# Usage: print_info "Info message"
print_info() {
    local message="$1"
    echo -e "${BLUE}ℹ️  ${message}${NC}"
}
