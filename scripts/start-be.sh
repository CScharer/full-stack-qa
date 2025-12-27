#!/bin/bash
# Start ONE GOAL Backend API Server
# This script starts the FastAPI backend server for the specified environment
#
# USAGE:
#   ./scripts/start-be.sh [OPTIONS]
#
# ENVIRONMENT OPTIONS:
#   --env ENV    or  -e ENV     Environment: dev, test, or prod (default: dev)
#   --env=ENV    or  -e=ENV     Same as above with equals sign
#
# EXAMPLES:
#   ./scripts/start-be.sh                    # Default: dev environment
#   ./scripts/start-be.sh --env test         # Test environment
#   ./scripts/start-be.sh -e prod            # Production environment
#   ./scripts/start-be.sh --env=dev          # Dev with equals syntax
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
BACKEND_DIR="${SCRIPT_DIR}/backend"
API_RELOAD=${API_RELOAD:-"true"}

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
            echo -e "${BLUE}Usage: ./scripts/start-be.sh [OPTIONS]${NC}"
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
            echo "  ./scripts/start-be.sh                    # Default: dev environment"
            echo "  ./scripts/start-be.sh --env test         # Test environment"
            echo "  ./scripts/start-be.sh -e prod            # Production environment"
            echo "  ./scripts/start-be.sh --env=dev          # Dev with equals syntax"
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

# Export ENVIRONMENT for backend config (uses full_stack_qa_{env}.db)
export ENVIRONMENT

# Function to load environment-specific ports from .env file
load_environment_ports() {
    # Convert to lowercase (compatible with bash 3.2+)
    local env=$(echo "${ENVIRONMENT}" | tr '[:upper:]' '[:lower:]')
    local env_file="${BACKEND_DIR}/.env"
    
    # Default ports (DEV)
    local api_port=8003
    local api_host="localhost"
    
    # Load ports from .env if it exists
    if [ -f "$env_file" ]; then
        local env_upper=$(echo "${env}" | tr '[:lower:]' '[:upper:]')
        local port_var="${env_upper}_PORT"
        local host_var="${env_upper}_HOST"
        
        # Extract port
        local extracted_port=$(grep -E "^${port_var}=" "$env_file" | cut -d'=' -f2 | tr -d ' ' || echo "")
        if [ -n "$extracted_port" ]; then
            api_port="$extracted_port"
        fi
        
        # Extract host
        local extracted_host=$(grep -E "^${host_var}=" "$env_file" | cut -d'=' -f2 | tr -d ' ' || echo "")
        if [ -n "$extracted_host" ]; then
            api_host="$extracted_host"
        fi
    fi
    
    # Export the ports (allow override from environment)
    export API_PORT=${API_PORT:-"$api_port"}
    export API_HOST=${API_HOST:-"$api_host"}
}

# Load environment-specific ports
load_environment_ports

echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}🚀 Starting ONE GOAL Backend API${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo ""

# Check if backend directory exists
if [ ! -d "$BACKEND_DIR" ]; then
    echo -e "${YELLOW}❌ Backend directory not found: $BACKEND_DIR${NC}"
    exit 1
fi

# Check if virtual environment exists
if [ ! -d "$BACKEND_DIR/venv" ]; then
    echo -e "${YELLOW}📦 Creating virtual environment...${NC}"
    cd "$BACKEND_DIR"
    python3 -m venv venv
    echo -e "${GREEN}✅ Virtual environment created${NC}"
fi

# Activate virtual environment
echo -e "${BLUE}🔧 Activating virtual environment...${NC}"
source "$BACKEND_DIR/venv/bin/activate"

# Install/upgrade dependencies
echo -e "${BLUE}📦 Installing dependencies...${NC}"
pip install -q --upgrade pip
pip install -q -r "$BACKEND_DIR/requirements.txt"

# Check if environment database exists
# Backend config will use: full_stack_qa_{ENVIRONMENT}.db
ENV_DB_PATH="${SCRIPT_DIR}/Data/Core/full_stack_qa_${ENVIRONMENT}.db"
if [ ! -f "$ENV_DB_PATH" ]; then
    echo -e "${YELLOW}⚠️  Environment database not found: $ENV_DB_PATH${NC}"
    echo -e "${YELLOW}   The backend will start but database operations may fail${NC}"
    echo -e "${YELLOW}   Create it with: sqlite3 $ENV_DB_PATH < docs/new_app/ONE_GOAL_SCHEMA_CORRECTED.sql${NC}"
fi

# Start the server
echo ""
echo -e "${GREEN}✅ Starting FastAPI server...${NC}"
echo -e "${BLUE}   Environment: $ENVIRONMENT${NC}"
echo -e "${BLUE}   Database: full_stack_qa_${ENVIRONMENT}.db${NC}"
echo -e "${BLUE}   Host: $API_HOST${NC}"
echo -e "${BLUE}   Port: $API_PORT${NC}"
echo -e "${BLUE}   Reload: $API_RELOAD${NC}"
echo ""
echo -e "${GREEN}📚 API Documentation:${NC}"
echo -e "   Swagger UI: http://$API_HOST:$API_PORT/docs"
echo -e "   ReDoc: http://$API_HOST:$API_PORT/redoc"
echo -e "   API: http://$API_HOST:$API_PORT/api/v1"
echo ""
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo ""

cd "$BACKEND_DIR"
if [ "$API_RELOAD" = "true" ]; then
    exec uvicorn app.main:app \
        --host "$API_HOST" \
        --port "$API_PORT" \
        --reload
else
    exec uvicorn app.main:app \
        --host "$API_HOST" \
        --port "$API_PORT"
fi
