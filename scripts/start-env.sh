#!/bin/bash
# Start Backend and Frontend services for specified environment
# This script starts both services with reload enabled and handles cleanup on exit
#
# USAGE:
#   ./scripts/start-env.sh [--env ENV|-e ENV] [OPTIONS] [be=PORT] [fe=PORT]
#
# ENVIRONMENT OPTIONS:
#   --env ENV    or  -e ENV     Environment: dev, test, or prod (default: dev)
#   --env=ENV    or  -e=ENV     Same as above with equals sign
#
# EXAMPLES:
#   ./scripts/start-env.sh                           # Default: dev environment
#   ./scripts/start-env.sh --env test                # Test environment
#   ./scripts/start-env.sh -e prod                   # Production environment
#   ./scripts/start-env.sh --env dev be=3000         # Dev with custom backend port
#   ./scripts/start-env.sh -e test --background      # Test environment in background
#
# Run with --help for full usage information

set -e

# Get the script directory (project root)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$SCRIPT_DIR"

# Parse command line arguments
BACKGROUND=false
FORCE=false
BACKEND_PORT=""
FRONTEND_PORT=""
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
        --background)
            BACKGROUND=true
            shift
            ;;
        --force)
            FORCE=true
            shift
            ;;
        --help|-h)
            echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
            echo -e "${BLUE}Usage: ./scripts/start-env.sh [--env ENV|-e ENV] [OPTIONS] [be=PORT] [fe=PORT]${NC}"
            echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
            echo ""
            echo -e "${YELLOW}ENVIRONMENT OPTIONS:${NC}"
            echo "  --env ENV       Environment: dev, test, or prod (default: dev)"
            echo "  -e ENV          Short form of --env"
            echo "  --env=ENV       Environment with equals sign"
            echo "  -e=ENV          Short form with equals sign"
            echo ""
            echo -e "${YELLOW}OTHER OPTIONS:${NC}"
            echo "  --background    Run services in background"
            echo "  --force         Force stop existing services on ports before starting"
            echo "  --help, -h      Show this help message"
            echo ""
            echo -e "${YELLOW}PORT ARGUMENTS:${NC}"
            echo "  be=PORT         Custom backend port (default: 8003)"
            echo "  fe=PORT         Custom frontend port (default: 3003)"
            echo ""
            echo -e "${YELLOW}EXAMPLES:${NC}"
            echo "  ./scripts/start-env.sh                    # Default: dev environment"
            echo "  ./scripts/start-env.sh --env test         # Test environment"
            echo "  ./scripts/start-env.sh -e prod            # Production environment"
            echo "  ./scripts/start-env.sh --env=dev          # Dev with equals syntax"
            echo "  ./scripts/start-env.sh --env dev be=3000   # Dev with custom backend port"
            echo "  ./scripts/start-env.sh -e test --background  # Test in background"
            exit 0
            ;;
        be=*)
            BACKEND_PORT="${1#be=}"
            # Validate it's a number
            if ! [[ "$BACKEND_PORT" =~ ^[0-9]+$ ]]; then
                echo -e "${RED}❌ Invalid backend port: $BACKEND_PORT (must be a number)${NC}"
                exit 1
            fi
            shift
            ;;
        fe=*)
            FRONTEND_PORT="${1#fe=}"
            # Validate it's a number
            if ! [[ "$FRONTEND_PORT" =~ ^[0-9]+$ ]]; then
                echo -e "${RED}❌ Invalid frontend port: $FRONTEND_PORT (must be a number)${NC}"
                exit 1
            fi
            shift
            ;;
        *)
            echo -e "${RED}❌ Unknown argument: $1${NC}"
            echo -e "${BLUE}Usage: ./scripts/start-env.sh [--env ENV|-e ENV] [OPTIONS] [be=PORT] [fe=PORT]${NC}"
            echo -e "${BLUE}Run with --help for more information${NC}"
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

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}🚀 Starting Services for ${ENVIRONMENT} Environment${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo ""
echo -e "${BLUE}   Environment: $ENVIRONMENT${NC}"
echo ""

# Track if we should cleanup on exit
CLEANUP_ON_EXIT=false
BACKEND_PID=""
FRONTEND_PID=""

# Function to cleanup on exit
cleanup() {
    if [ "$CLEANUP_ON_EXIT" = "true" ]; then
        echo ""
        echo -e "${YELLOW}🛑 Stopping services...${NC}"
        # Stop backend if running
        if [ -n "$BACKEND_PID" ] && kill -0 "$BACKEND_PID" 2>/dev/null; then
            echo -e "${YELLOW}   Stopping backend (PID: $BACKEND_PID)...${NC}"
            kill "$BACKEND_PID" 2>/dev/null || true
        fi
        # Stop frontend if running
        if [ -n "$FRONTEND_PID" ] && kill -0 "$FRONTEND_PID" 2>/dev/null; then
            echo -e "${YELLOW}   Stopping frontend (PID: $FRONTEND_PID)...${NC}"
            kill "$FRONTEND_PID" 2>/dev/null || true
        fi
        # Also try using stop-services.sh as fallback
        if [ -f "$SCRIPT_DIR/scripts/stop-services.sh" ]; then
            bash "$SCRIPT_DIR/scripts/stop-services.sh" 2>/dev/null || true
        fi
        echo -e "${GREEN}✅ Cleanup complete${NC}"
    fi
    exit 0
}

# Set up signal handlers for cleanup (SIGINT = Ctrl+C, SIGTERM = termination)
# Note: We don't trap EXIT here because we want services to keep running on normal exit
trap cleanup SIGINT SIGTERM

# If --force flag is set, stop existing services first
if [ "$FORCE" = "true" ]; then
    echo -e "${YELLOW}⚠️  Force mode: Stopping existing services on configured ports...${NC}"
    if [ -f "$SCRIPT_DIR/scripts/stop-services.sh" ]; then
        bash "$SCRIPT_DIR/scripts/stop-services.sh" || true
        sleep 2
    fi
    echo ""
fi

# Set environment variables
export ENVIRONMENT
export API_RELOAD=true
export FORCE_STOP=${FORCE:-"false"}

# Default ports
DEFAULT_BACKEND_PORT=8003
DEFAULT_FRONTEND_PORT=3003

# Validate port assignments and check for conflicts
if [ -n "$BACKEND_PORT" ]; then
    # Check if backend port conflicts with default frontend port
    if [ "$BACKEND_PORT" = "$DEFAULT_FRONTEND_PORT" ]; then
        echo -e "${RED}❌ Error: Backend port $BACKEND_PORT conflicts with default frontend port ($DEFAULT_FRONTEND_PORT)${NC}"
        echo -e "${YELLOW}   Please choose a different backend port${NC}"
        exit 1
    fi
    # Check if backend port conflicts with specified frontend port
    if [ -n "$FRONTEND_PORT" ] && [ "$BACKEND_PORT" = "$FRONTEND_PORT" ]; then
        echo -e "${RED}❌ Error: Backend port $BACKEND_PORT cannot be the same as frontend port $FRONTEND_PORT${NC}"
        echo -e "${YELLOW}   Please choose different ports for backend and frontend${NC}"
        exit 1
    fi
    export API_PORT="$BACKEND_PORT"
    echo -e "${BLUE}📌 Using custom backend port: $BACKEND_PORT${NC}"
fi

if [ -n "$FRONTEND_PORT" ]; then
    # Check if frontend port conflicts with default backend port
    if [ "$FRONTEND_PORT" = "$DEFAULT_BACKEND_PORT" ]; then
        echo -e "${RED}❌ Error: Frontend port $FRONTEND_PORT conflicts with default backend port ($DEFAULT_BACKEND_PORT)${NC}"
        echo -e "${YELLOW}   Please choose a different frontend port${NC}"
        exit 1
    fi
    # Check if frontend port conflicts with specified backend port (already checked above, but double-check)
    if [ -n "$BACKEND_PORT" ] && [ "$FRONTEND_PORT" = "$BACKEND_PORT" ]; then
        echo -e "${RED}❌ Error: Frontend port $FRONTEND_PORT cannot be the same as backend port $BACKEND_PORT${NC}"
        echo -e "${YELLOW}   Please choose different ports for backend and frontend${NC}"
        exit 1
    fi
    export FRONTEND_PORT="$FRONTEND_PORT"
    export PORT="$FRONTEND_PORT"  # Frontend script uses PORT, not FRONTEND_PORT
    echo -e "${BLUE}📌 Using custom frontend port: $FRONTEND_PORT${NC}"
fi

if [ -n "$BACKEND_PORT" ] || [ -n "$FRONTEND_PORT" ]; then
    echo ""
fi

# Start services using the individual scripts (with environment and port support)
# The API_PORT and PORT environment variables are already set above and will be picked up by the scripts
if [ "$BACKGROUND" = "true" ]; then
    echo -e "${BLUE}📦 Starting services in background...${NC}"
    echo ""
    # Start backend in background (API_PORT is already exported)
    bash "$SCRIPT_DIR/scripts/start-be.sh" --env "$ENVIRONMENT" > "$SCRIPT_DIR/dev-backend.log" 2>&1 &
    BACKEND_PID=$!
    # Start frontend in background (PORT is already exported)
    bash "$SCRIPT_DIR/scripts/start-fe.sh" --env "$ENVIRONMENT" > "$SCRIPT_DIR/dev-frontend.log" 2>&1 &
    FRONTEND_PID=$!
    echo -e "${GREEN}✅ Services starting in background${NC}"
    echo -e "${BLUE}   Backend PID: $BACKEND_PID${NC}"
    echo -e "${BLUE}   Frontend PID: $FRONTEND_PID${NC}"
    echo -e "${BLUE}   Backend logs: $SCRIPT_DIR/dev-backend.log${NC}"
    echo -e "${BLUE}   Frontend logs: $SCRIPT_DIR/dev-frontend.log${NC}"
    echo ""
    echo -e "${YELLOW}   To stop services, run: ./scripts/stop-services.sh${NC}"
    echo -e "${YELLOW}   Or: kill $BACKEND_PID $FRONTEND_PID${NC}"
    echo ""
    # Wait for both processes
    wait $BACKEND_PID $FRONTEND_PID
else
    echo -e "${BLUE}📦 Starting services in foreground...${NC}"
    echo -e "${YELLOW}   Press Ctrl+C to stop both services${NC}"
    echo ""
    # Start backend in background (API_PORT is already exported)
    bash "$SCRIPT_DIR/scripts/start-be.sh" --env "$ENVIRONMENT" &
    BACKEND_PID=$!
    # Start frontend in background (PORT is already exported)
    bash "$SCRIPT_DIR/scripts/start-fe.sh" --env "$ENVIRONMENT" &
    FRONTEND_PID=$!
    
    # Enable cleanup on exit
    CLEANUP_ON_EXIT=true
    
    # Wait for both processes or signal (Ctrl+C will trigger cleanup)
    wait $BACKEND_PID $FRONTEND_PID 2>/dev/null || true
fi
