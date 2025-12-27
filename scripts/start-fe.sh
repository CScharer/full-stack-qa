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
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$SCRIPT_DIR"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
FRONTEND_DIR="${SCRIPT_DIR}/frontend"

# Parse command-line arguments for environment
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
        --help|-h)
            echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
            echo -e "${BLUE}Usage: ./scripts/start-fe.sh [OPTIONS]${NC}"
            echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
            echo ""
            echo -e "${YELLOW}ENVIRONMENT OPTIONS:${NC}"
            echo "  --env ENV       Environment: dev, test, or prod (default: dev)"
            echo "  -e ENV          Short form of --env"
            echo "  --env=ENV       Environment with equals sign"
            echo "  -e=ENV          Short form with equals sign"
            echo ""
            echo -e "${YELLOW}OTHER OPTIONS:${NC}"
            echo "  --help, -h      Show this help message"
            echo ""
            echo -e "${YELLOW}EXAMPLES:${NC}"
            echo "  ./scripts/start-fe.sh                    # Default: dev environment"
            echo "  ./scripts/start-fe.sh --env test         # Test environment"
            echo "  ./scripts/start-fe.sh -e prod            # Production environment"
            echo "  ./scripts/start-fe.sh --env=dev          # Dev with equals syntax"
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            echo "Use --help for usage information"
            exit 1
            ;;
    esac
done

# Set environment with priority: command-line param > env var > default
if [ -n "$ENVIRONMENT_PARAM" ]; then
    ENVIRONMENT=$(echo "$ENVIRONMENT_PARAM" | tr '[:upper:]' '[:lower:]')
elif [ -n "$ENVIRONMENT" ]; then
    ENVIRONMENT=$(echo "$ENVIRONMENT" | tr '[:upper:]' '[:lower:]')
else
    ENVIRONMENT="dev"
fi

# Validate environment value
if [[ ! "$ENVIRONMENT" =~ ^(dev|test|prod)$ ]]; then
    echo -e "${RED}❌ Invalid environment: '$ENVIRONMENT'${NC}"
    echo -e "${YELLOW}   Must be one of: dev, test, prod${NC}"
    exit 1
fi

# Function to load environment-specific ports from .env file
load_environment_ports() {
    # Convert to lowercase (compatible with bash 3.2+)
    local env=$(echo "${ENVIRONMENT}" | tr '[:upper:]' '[:lower:]')
    local env_file="${FRONTEND_DIR}/.env"
    
    # Default port (DEV)
    local frontend_port=3003
    
    # Load port from .env if it exists
    if [ -f "$env_file" ]; then
        local env_upper=$(echo "${env}" | tr '[:lower:]' '[:upper:]')
        local port_var="${env_upper}_PORT"
        
        # Extract port
        local extracted_port=$(grep -E "^${port_var}=" "$env_file" | cut -d'=' -f2 | tr -d ' ' || echo "")
        if [ -n "$extracted_port" ]; then
            frontend_port="$extracted_port"
        fi
    fi
    
    # Export the port (allow override from environment)
    export PORT=${PORT:-"$frontend_port"}
}

# Load environment-specific ports
load_environment_ports

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
echo -e "${BLUE}   Port: $PORT${NC}"
echo -e "${BLUE}   URL: http://127.0.0.1:$PORT${NC}"
echo ""
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo ""

cd "$FRONTEND_DIR"
export PORT="$PORT"
# Pass port via -p flag
exec npm run dev -- -p "$PORT"
