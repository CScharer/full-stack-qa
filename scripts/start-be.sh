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

# Function to load environment-specific configuration from config/environments.json
load_environment_config() {
    # Source the centralized config script
    local config_script="${SCRIPT_DIR}/scripts/ci/env-config.sh"
    
    if [ -f "$config_script" ]; then
        # Source the config script to get access to functions
        source "$config_script"
        
        # Get ports, URLs, CORS origins, and database config for the environment using eval to set variables
        local env=$(echo "${ENVIRONMENT}" | tr '[:upper:]' '[:lower:]')
        eval "$(get_ports_for_environment "$env")"
        eval "$(get_database_for_environment "$env")"
        
        # Set ports (allow override from environment)
        export API_PORT=${API_PORT:-"$API_PORT"}
        export API_HOST=${API_HOST:-"localhost"}
        
        # Set database configuration
        # Always use DATABASE_PATH (absolute path) for highest priority in backend config
        # This ensures the backend finds the database regardless of where it's run from
        if [ -n "$DATABASE_PATH" ]; then
            # Convert relative path to absolute if needed
            if [[ "$DATABASE_PATH" != /* ]]; then
                # Relative path - make it absolute from project root
                export DATABASE_PATH="${SCRIPT_DIR}/${DATABASE_PATH}"
            else
                # Already absolute
                export DATABASE_PATH
            fi
        elif [ -n "$DATABASE_DIR" ] && [ -n "$DATABASE_NAME" ]; then
            # If DATABASE_PATH not set, construct it from DATABASE_DIR + DATABASE_NAME
            # Convert to absolute path from project root
            if [[ "$DATABASE_DIR" != /* ]]; then
                # Relative path - make it absolute from project root
                export DATABASE_PATH="${SCRIPT_DIR}/${DATABASE_DIR}/${DATABASE_NAME}"
            else
                # DATABASE_DIR is absolute
                export DATABASE_PATH="${DATABASE_DIR}/${DATABASE_NAME}"
            fi
        else
            # Fallback: construct from environment
            export DATABASE_PATH="${SCRIPT_DIR}/Data/Core/full_stack_qa_${env}.db"
        fi
        
        # Get CORS_ORIGINS and set it directly (preserve JSON format)
        # Extract value after = sign - it's already in JSON format with quotes
        local cors_line=$(get_cors_origins "$env")
        local cors_value="${cors_line#CORS_ORIGINS=}"
        # Export directly - the value already has quotes, don't add more
        export CORS_ORIGINS=${cors_value}
        
        # Pydantic Settings expects JSON array format for List fields (e.g., ["http://localhost:3004"])
        if [ -z "$CORS_ORIGINS" ]; then
            # Fallback to default if config parsing fails
            case "$env" in
                dev)
                    export CORS_ORIGINS='["http://127.0.0.1:3003","http://localhost:3003","http://0.0.0.0:3003"]'
                    export API_PORT=${API_PORT:-"8003"}
                    ;;
                test)
                    export CORS_ORIGINS='["http://127.0.0.1:3004","http://localhost:3004","http://0.0.0.0:3004"]'
                    export API_PORT=${API_PORT:-"8004"}
                    ;;
                prod)
                    export CORS_ORIGINS='["http://127.0.0.1:3005","http://localhost:3005","http://0.0.0.0:3005"]'
                    export API_PORT=${API_PORT:-"8005"}
                    ;;
            esac
        fi
    else
        # Fallback if config script doesn't exist
        local env=$(echo "${ENVIRONMENT}" | tr '[:upper:]' '[:lower:]')
        case "$env" in
            dev)
                export CORS_ORIGINS='["http://127.0.0.1:3003","http://localhost:3003","http://0.0.0.0:3003"]'
                export API_PORT=${API_PORT:-"8003"}
                export API_HOST=${API_HOST:-"localhost"}
                ;;
            test)
                export CORS_ORIGINS='["http://127.0.0.1:3004","http://localhost:3004","http://0.0.0.0:3004"]'
                export API_PORT=${API_PORT:-"8004"}
                export API_HOST=${API_HOST:-"localhost"}
                ;;
            prod)
                export CORS_ORIGINS='["http://127.0.0.1:3005","http://localhost:3005","http://0.0.0.0:3005"]'
                export API_PORT=${API_PORT:-"8005"}
                export API_HOST=${API_HOST:-"localhost"}
                ;;
        esac
    fi
}

# Load environment-specific configuration
load_environment_config

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
if [ -n "$DATABASE_PATH" ]; then
    echo -e "${BLUE}   Database Path: ${DATABASE_PATH}${NC}"
fi
echo -e "${BLUE}   Host: $API_HOST${NC}"
echo -e "${BLUE}   Port: $API_PORT${NC}"
echo -e "${BLUE}   Reload: $API_RELOAD${NC}"
echo -e "${BLUE}   CORS Origins: ${CORS_ORIGINS:-not set}${NC}"
echo ""
# Get API base path from config
API_BASE_PATH="/api/v1"  # Default fallback
if [ -f "${SCRIPT_DIR}/config/environments.json" ] && command -v jq &> /dev/null; then
    API_BASE_PATH=$(jq -r '.api.basePath // "/api/v1"' "${SCRIPT_DIR}/config/environments.json" 2>/dev/null || echo "/api/v1")
fi

echo -e "${GREEN}📚 API Documentation:${NC}"
echo -e "   Swagger UI: http://$API_HOST:$API_PORT/docs"
echo -e "   ReDoc: http://$API_HOST:$API_PORT/redoc"
echo -e "   API: http://$API_HOST:$API_PORT${API_BASE_PATH}"
echo ""
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo ""

# Ensure DATABASE_PATH is absolute BEFORE changing directory
# Backend's Path.resolve() resolves relative to current working directory
# So we must set it to absolute path before cd'ing to backend/
if [ -n "$DATABASE_PATH" ]; then
    # Store original for debugging (use regular variable, not local)
    original_path="$DATABASE_PATH"
    
    # Remove any leading ./ or scripts/ prefix (works for both relative and absolute)
    DATABASE_PATH="${DATABASE_PATH#./}"
    # Remove scripts/ prefix if present (handle both relative and absolute paths)
    if [[ "$DATABASE_PATH" == scripts/* ]]; then
        DATABASE_PATH="${DATABASE_PATH#scripts/}"
    fi
    # Remove /scripts/ from anywhere in the path (handles absolute paths like /path/scripts/Data)
    # Use sed or parameter expansion to replace /scripts/ with /
    DATABASE_PATH=$(echo "$DATABASE_PATH" | sed 's|/scripts/|/|g')
    
    # Convert to absolute if still relative
    if [[ "$DATABASE_PATH" != /* ]]; then
        export DATABASE_PATH="${SCRIPT_DIR}/${DATABASE_PATH}"
    else
        # Already absolute - export as is (should be clean now)
        export DATABASE_PATH
    fi
    
    # Debug: show the transformation if it changed
    if [ "$original_path" != "$DATABASE_PATH" ]; then
        echo -e "${BLUE}   Database path corrected: ${original_path} -> ${DATABASE_PATH}${NC}"
    fi
    
    # Verify the path exists
    if [ ! -f "$DATABASE_PATH" ]; then
        echo -e "${YELLOW}⚠️  Warning: Database file not found at: $DATABASE_PATH${NC}"
        echo -e "${YELLOW}   The backend will fail to start if the database doesn't exist${NC}"
    else
        echo -e "${GREEN}✓ Database file found at: $DATABASE_PATH${NC}"
    fi
    
    # Unset DATABASE_DIR and DATABASE_NAME to prevent backend from using them as fallbacks
    # The backend should use DATABASE_PATH (absolute path) which has highest priority
    unset DATABASE_DIR
    unset DATABASE_NAME
    
    # Debug: Show what will be exported
    echo -e "${BLUE}   Exporting DATABASE_PATH=${DATABASE_PATH}${NC}"
fi

cd "$BACKEND_DIR"
# Export CORS_ORIGINS so Pydantic Settings can read it (JSON array format)
# Don't re-quote it - it's already in the correct format from eval
export CORS_ORIGINS

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
