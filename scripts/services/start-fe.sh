#!/bin/bash
# Start ONE GOAL Frontend Application
# This script starts the Next.js frontend server for the specified environment
#
# USAGE:
#   ./scripts/start-fe.sh [OPTIONS]
#
# ENVIRONMENT OPTIONS:
#   --env ENV    or  -e ENV     Environment: dev, test, or prod (default: dev)
#   --env=ENV    or  -e=ENV     Same as above with equals sign
#
# EXAMPLES:
#   ./scripts/start-fe.sh                    # Default: dev environment
#   ./scripts/start-fe.sh --env test         # Test environment
#   ./scripts/start-fe.sh -e prod            # Production environment
#   ./scripts/start-fe.sh --env=dev          # Dev with equals syntax
#
# Run with --help for full usage information

set -e

# Get the script directory (project root)
# Since this script is in scripts/services/, we need to go up two levels
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$SCRIPT_DIR"

# Source common functions
source "${SCRIPT_DIR}/scripts/lib/common.sh"

# Configuration
FRONTEND_DIR="${SCRIPT_DIR}/frontend"

# Parse command-line arguments
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
        --help|-h)
            print_help_header "Usage: ./scripts/start-fe.sh [OPTIONS]"
            print_help_section "ENVIRONMENT OPTIONS"
            print_help_example "  --env ENV       Environment: dev, test, or prod (default: dev)"
            print_help_example "  -e ENV          Short form of --env"
            print_help_example "  --env=ENV       Environment with equals sign"
            print_help_example "  -e=ENV          Short form with equals sign"
            echo ""
            print_help_section "OTHER OPTIONS"
            print_help_example "  --help, -h      Show this help message"
            echo ""
            print_help_section "EXAMPLES"
            print_help_example "  ./scripts/start-fe.sh                    # Default: dev environment"
            print_help_example "  ./scripts/start-fe.sh --env test         # Test environment"
            print_help_example "  ./scripts/start-fe.sh -e prod            # Production environment"
            print_help_example "  ./scripts/start-fe.sh --env=dev          # Dev with equals syntax"
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            echo "Use --help for usage information"
            exit 1
            ;;
    esac
done

# Set and validate environment
if ! set_and_validate_environment "dev"; then
    exit 1
fi

# Function to load environment-specific configuration from config/environments.json
load_environment_config() {
    # Source the centralized config script
    local config_script="${SCRIPT_DIR}/scripts/ci/env-config.sh"
    
    if [ -f "$config_script" ]; then
        # Source the config script to get access to functions
        source "$config_script"
        
        # Get ports and URLs for the environment using eval to set variables
        local env=$(echo "${ENVIRONMENT}" | tr '[:upper:]' '[:lower:]')
        eval "$(get_ports_for_environment "$env")"
        eval "$(get_api_endpoints)"
        
        # Set port (allow override from environment)
        export PORT=${PORT:-"$FRONTEND_PORT"}
        
        # Set NEXT_PUBLIC_API_URL (required by Next.js for client-side API calls)
        if [ -n "$API_URL" ] && [ -n "$API_BASE_PATH" ]; then
            export NEXT_PUBLIC_API_URL="${API_URL}${API_BASE_PATH}"
        else
            # Fallback to default if config parsing fails - read API base path from config
            local config_json="${SCRIPT_DIR}/config/environments.json"
            local api_base_path="/api/v1"  # Default fallback
            if [ -f "$config_json" ] && command -v jq &> /dev/null; then
                api_base_path=$(jq -r '.api.basePath // "/api/v1"' "$config_json" 2>/dev/null || echo "/api/v1")
            fi
            
            case "$env" in
                dev)
                    export NEXT_PUBLIC_API_URL="http://localhost:8003${api_base_path}"
                    export PORT=${PORT:-"3003"}
                    ;;
                test)
                    export NEXT_PUBLIC_API_URL="http://localhost:8004${api_base_path}"
                    export PORT=${PORT:-"3004"}
                    ;;
                prod)
                    export NEXT_PUBLIC_API_URL="http://localhost:8005${api_base_path}"
                    export PORT=${PORT:-"3005"}
                    ;;
            esac
        fi
    else
        # Fallback if config script doesn't exist - read API base path from config
        local env=$(echo "${ENVIRONMENT}" | tr '[:upper:]' '[:lower:]')
        local config_json="${SCRIPT_DIR}/config/environments.json"
        local api_base_path="/api/v1"  # Default fallback
        if [ -f "$config_json" ] && command -v jq &> /dev/null; then
            api_base_path=$(jq -r '.api.basePath // "/api/v1"' "$config_json" 2>/dev/null || echo "/api/v1")
        fi
        
        case "$env" in
            dev)
                export NEXT_PUBLIC_API_URL="http://localhost:8003${api_base_path}"
                export PORT=${PORT:-"3003"}
                ;;
            test)
                export NEXT_PUBLIC_API_URL="http://localhost:8004${api_base_path}"
                export PORT=${PORT:-"3004"}
                ;;
            prod)
                export NEXT_PUBLIC_API_URL="http://localhost:8005${api_base_path}"
                export PORT=${PORT:-"3005"}
                ;;
        esac
    fi
}

# Load environment-specific configuration
load_environment_config

echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}🚀 Starting ONE GOAL Frontend${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo ""

# Check if frontend directory exists
if [ ! -d "$FRONTEND_DIR" ]; then
    echo -e "${YELLOW}❌ Frontend directory not found: $FRONTEND_DIR${NC}"
    exit 1
fi

# Check if node_modules exists
if [ ! -d "$FRONTEND_DIR/node_modules" ]; then
    echo -e "${YELLOW}📦 Installing dependencies...${NC}"
    cd "$FRONTEND_DIR"
    npm install
    echo -e "${GREEN}✅ Dependencies installed${NC}"
fi

# Check if .env.local exists
if [ ! -f "$FRONTEND_DIR/.env.local" ]; then
    echo -e "${YELLOW}⚠️  .env.local not found${NC}"
    echo -e "${YELLOW}   Creating from .env.local.example...${NC}"
    if [ -f "$FRONTEND_DIR/.env.local.example" ]; then
        cp "$FRONTEND_DIR/.env.local.example" "$FRONTEND_DIR/.env.local"
        echo -e "${GREEN}✅ .env.local created${NC}"
    else
        echo -e "${YELLOW}   Please create .env.local with NEXT_PUBLIC_API_URL${NC}"
    fi
fi

# Start the server
echo ""
echo -e "${GREEN}✅ Starting Next.js development server...${NC}"
echo -e "${BLUE}   Environment: $ENVIRONMENT${NC}"
echo -e "${BLUE}   Port: $PORT${NC}"
echo -e "${BLUE}   URL: http://127.0.0.1:$PORT${NC}"
echo -e "${BLUE}   API URL: ${NEXT_PUBLIC_API_URL:-not set}${NC}"
echo ""
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo ""

cd "$FRONTEND_DIR"
export PORT="$PORT"
export NEXT_PUBLIC_API_URL="$NEXT_PUBLIC_API_URL"
# Pass port via -p flag
exec npm run dev -- -p "$PORT"
