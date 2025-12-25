#!/bin/bash
# Start ONE GOAL Frontend Application
# This script starts the Next.js frontend server for development

set -e

# Get the script directory (project root)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$SCRIPT_DIR"

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
FRONTEND_DIR="${SCRIPT_DIR}/frontend"
ENVIRONMENT=${ENVIRONMENT:-"dev"}  # dev, test, or prod

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
