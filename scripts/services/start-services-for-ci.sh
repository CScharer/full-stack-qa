#!/bin/bash
# scripts/services/start-services-for-ci.sh
# CI/CD Services Starter
#
# Purpose: Start Backend and Frontend services for CI/CD testing (idempotent)
#
# Usage:
#   ./scripts/services/start-services-for-ci.sh [ENVIRONMENT]
#
# Parameters:
#   ENVIRONMENT   Environment to start services for: dev, test, prod (default: "dev" or from ENVIRONMENT env var)
#
# Examples:
#   ./scripts/services/start-services-for-ci.sh
#   ./scripts/services/start-services-for-ci.sh test
#   ENVIRONMENT=prod ./scripts/services/start-services-for-ci.sh
#
# Description:
#   This script starts Backend and Frontend services for CI/CD testing. It's idempotent -
#   safe to run multiple times. It checks if services are already running and only starts
#   them if needed. Services run without auto-reload for CI/CD stability.
#
# Dependencies:
#   - Python 3.13+ (for Backend)
#   - Node.js 20+ (for Frontend)
#   - Backend virtual environment (backend/venv/)
#   - Frontend dependencies (frontend/node_modules/)
#   - scripts/ci/env-config.sh (for port configuration)
#
# Output:
#   - Service startup status
#   - PID files created (.service-pids)
#   - Exit code: 0 on success, non-zero on failure
#
# Notes:
#   - Idempotent: safe to run multiple times
#   - Checks if services are already running before starting
#   - Disables auto-reload for CI/CD stability
#   - Stores PIDs for graceful shutdown
#   - Used in GitHub Actions workflows
#
# Last Updated: January 2026

set -e

# Get the script directory (project root)
# Since this script is in scripts/services/, we need to go up two levels
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$SCRIPT_DIR"

# Configuration
BACKEND_DIR="${SCRIPT_DIR}/backend"
FRONTEND_DIR="${SCRIPT_DIR}/frontend"
ENVIRONMENT=${ENVIRONMENT:-"dev"}  # dev, test, or prod
API_RELOAD=${API_RELOAD:-"false"}  # Disable reload for CI
# Load timeout from centralized config if available
SCRIPT_DIR_FULL="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
ENV_CONFIG="${SCRIPT_DIR_FULL}/config/environments.json"
if [ -f "$ENV_CONFIG" ] && command -v jq &> /dev/null; then
    MAX_WAIT=${MAX_WAIT:-$(jq -r '.timeouts.serviceStartup' "$ENV_CONFIG" 2>/dev/null || echo "120")}
else
    MAX_WAIT=${MAX_WAIT:-120}  # Fallback: Maximum wait time in seconds
fi
FORCE_STOP=${FORCE_STOP:-"false"}  # Force stop existing services on ports

# Source environment configuration to get DATABASE_PATH, CORS_ORIGINS, etc.
ENV_CONFIG_SCRIPT="${SCRIPT_DIR}/scripts/ci/env-config.sh"
if [ -f "$ENV_CONFIG_SCRIPT" ]; then
    # Temporarily disable exit on error when sourcing to handle errors gracefully
    set +e
    source "$ENV_CONFIG_SCRIPT"
    set -e
    
    # Load environment configuration (same approach as start-be.sh)
    # Source the config script to get access to functions
    env=$(echo "${ENVIRONMENT}" | tr '[:upper:]' '[:lower:]')
    eval "$(get_ports_for_environment "$env")"
    eval "$(get_database_for_environment "$env")"
    eval "$(get_api_endpoints)"
    eval "$(get_timeouts)"
    
    # Set ports (allow override from environment)
    export API_PORT=${API_PORT:-"$API_PORT"}
    export API_HOST=${API_HOST:-"0.0.0.0"}
    
    # Set database configuration (same logic as start-be.sh)
    # Always use DATABASE_PATH (absolute path) for highest priority in backend config
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
        if [[ "$DATABASE_DIR" != /* ]]; then
            # Relative path - make it absolute from project root
            export DATABASE_PATH="${SCRIPT_DIR}/${DATABASE_DIR}/${DATABASE_NAME}"
        else
            # DATABASE_DIR is absolute
            export DATABASE_PATH="${DATABASE_DIR}/${DATABASE_NAME}"
        fi
    else
        # Fallback: construct from environment
        export DATABASE_PATH="${SCRIPT_DIR}/data/core/full_stack_qa_${env}.db"
    fi
    
    # Ensure DATABASE_PATH is absolute and doesn't include scripts/ (same as start-be.sh)
    # This must happen BEFORE changing directory
    if [ -n "$DATABASE_PATH" ]; then
        original_path="$DATABASE_PATH"
        
        # Remove any leading ./ or scripts/ prefix (works for both relative and absolute)
        DATABASE_PATH="${DATABASE_PATH#./}"
        # Remove scripts/ prefix if present (handle both relative and absolute paths)
        if [[ "$DATABASE_PATH" == scripts/* ]]; then
            DATABASE_PATH="${DATABASE_PATH#scripts/}"
        fi
        # Remove /scripts/ from anywhere in the path (handles absolute paths like /path/scripts/Data)
        DATABASE_PATH=$(echo "$DATABASE_PATH" | sed 's|/scripts/|/|g')
        
        # Convert to absolute if still relative
        if [[ "$DATABASE_PATH" != /* ]]; then
            export DATABASE_PATH="${SCRIPT_DIR}/${DATABASE_PATH}"
        else
            export DATABASE_PATH
        fi
        
        if [ "$original_path" != "$DATABASE_PATH" ]; then
            echo "   🔧 Database path corrected: $original_path -> $DATABASE_PATH" >&2
        fi
    fi
    
    # Get CORS_ORIGINS and set it directly (same approach as start-be.sh)
    # Extract value after = sign - it's already in JSON format with quotes
    cors_line=$(get_cors_origins "$env")
    cors_value="${cors_line#CORS_ORIGINS=}"
    # Export directly - the value already has quotes, don't add more
    export CORS_ORIGINS=${cors_value}
    
    # Pydantic Settings expects JSON array format for List fields
    if [ -z "$CORS_ORIGINS" ]; then
        # Fallback to default if config parsing fails
        case "$env" in
            dev)
                export CORS_ORIGINS='["http://127.0.0.1:3003","http://localhost:3003","http://0.0.0.0:3003"]'
                ;;
            test)
                export CORS_ORIGINS='["http://127.0.0.1:3004","http://localhost:3004","http://0.0.0.0:3004"]'
                ;;
            prod)
                export CORS_ORIGINS='["http://127.0.0.1:3005","http://localhost:3005","http://0.0.0.0:3005"]'
                ;;
        esac
    fi
fi

# Function to load environment-specific ports from .env files
load_environment_ports() {
    # Convert to lowercase (compatible with bash 3.2+)
    local env=$(echo "${ENVIRONMENT:-dev}" | tr '[:upper:]' '[:lower:]')
    local backend_env_file="${BACKEND_DIR}/.env"
    local frontend_env_file="${FRONTEND_DIR}/.env"
    
    # Source centralized port configuration (single source of truth)
    # This ensures all scripts use the same port values
    local port_config_script="${SCRIPT_DIR}/scripts/ci/port-config.sh"
    if [ -f "$port_config_script" ]; then
        # Source the port config to get default ports
        source "$port_config_script"
        # Get ports for this environment
        local port_config=$(get_ports_for_environment "$env")
        eval "$port_config"
        
        # Use centralized config as defaults (can still be overridden by env vars or .env files)
        api_port=${API_PORT:-$API_PORT}
        frontend_port=${FRONTEND_PORT:-$FRONTEND_PORT}
    else
        # Fallback to hardcoded values if config file doesn't exist (shouldn't happen)
        echo "⚠️  Warning: port-config.sh not found, using fallback values" >&2
        case "$env" in
            dev)
                api_port=8003
                frontend_port=3003
                ;;
            test)
                api_port=8004
                frontend_port=3004
                ;;
            prod)
                api_port=8005
                frontend_port=3005
                ;;
            *)
                api_port=8003
                frontend_port=3003
                ;;
        esac
    fi
    
    # Allow environment variables to override (highest priority)
    api_port=${API_PORT:-$api_port}
    frontend_port=${FRONTEND_PORT:-$frontend_port}
    
    local api_host="0.0.0.0"
    
    # Load backend ports from .env if it exists (only if not already set via environment variable)
    if [ -f "$backend_env_file" ] && [ -z "$API_PORT" ]; then
        # Source the .env file and extract the appropriate port
        local env_upper=$(echo "${env}" | tr '[:lower:]' '[:upper:]')
        local port_var="${env_upper}_PORT"
        
        # Use grep to extract the port value (handles comments and whitespace)
        local extracted_port=$(grep -E "^${port_var}=" "$backend_env_file" | cut -d'=' -f2 | tr -d ' ' || echo "")
        if [ -n "$extracted_port" ]; then
            api_port="$extracted_port"
        fi
        
        # Extract host
        local host_var="${env_upper}_HOST"
        local extracted_host=$(grep -E "^${host_var}=" "$backend_env_file" | cut -d'=' -f2 | tr -d ' ' || echo "")
        if [ -n "$extracted_host" ]; then
            api_host="$extracted_host"
        fi
    fi
    
    # Load frontend ports from .env if it exists (only if not already set via environment variable)
    if [ -f "$frontend_env_file" ] && [ -z "$FRONTEND_PORT" ]; then
        local env_upper=$(echo "${env}" | tr '[:lower:]' '[:upper:]')
        local port_var="${env_upper}_PORT"
        local extracted_port=$(grep -E "^${port_var}=" "$frontend_env_file" | cut -d'=' -f2 | tr -d ' ' || echo "")
        if [ -n "$extracted_port" ]; then
            frontend_port="$extracted_port"
        fi
    fi
    
    # Export the ports
    export API_PORT="$api_port"
    export FRONTEND_PORT="$frontend_port"
    export API_HOST="$api_host"
    
    echo "📋 Environment: ${env}"
    echo "   Backend: ${api_host}:${api_port}"
    echo "   Frontend: localhost:${frontend_port}"
}

# Load environment-specific ports
load_environment_ports

echo "═══════════════════════════════════════════════════════════════"
echo "🚀 Checking and Starting Services for CI/CD Testing"
echo "═══════════════════════════════════════════════════════════════"
echo ""

# Source port utilities if available
PORT_UTILS="${SCRIPT_DIR}/scripts/ci/port-utils.sh"
if [ -f "$PORT_UTILS" ]; then
    source "$PORT_UTILS"
else
    # Fallback: define port functions inline if utility doesn't exist
    is_port_in_use() {
        local port=$1
        if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1 || nc -z localhost $port 2>/dev/null; then
            return 0  # Port is in use
        else
            return 1  # Port is not in use
        fi
    }
    
    get_port_pid() {
        local port=$1
        lsof -ti :$port 2>/dev/null | head -1 || echo ""
    }
fi

# Function to check if a service is ready (uses centralized utility)
wait_for_service() {
    local url=$1
    local service_name=$2
    
    # Use centralized wait-for-service.sh utility
    local wait_script="${SCRIPT_DIR}/scripts/ci/wait-for-service.sh"
    if [ -f "$wait_script" ]; then
        if "$wait_script" "$url" "$service_name" "$MAX_WAIT" 2; then
            return 0
        else
            return 1
        fi
    else
        # Fallback to inline logic if utility doesn't exist
        local elapsed=0
        echo "⏳ Waiting for $service_name to be ready..."
        while [ $elapsed -lt $MAX_WAIT ]; do
            if curl -sf "$url" > /dev/null 2>&1; then
                echo "✅ $service_name is ready!"
                return 0
            fi
            sleep 1
            elapsed=$((elapsed + 1))
            if [ $((elapsed % 10)) -eq 0 ]; then
                echo "   Still waiting... (${elapsed}s/${MAX_WAIT}s)"
            fi
        done
        echo "❌ $service_name failed to start within ${MAX_WAIT}s"
        return 1
    fi
}

# Function to stop process on a port (uses port-utils.sh if available)
stop_port_if_in_use() {
    local port=$1
    local service_name=$2
    
    if [ -f "$PORT_UTILS" ]; then
        # Use centralized port utility
        if is_port_in_use "$port"; then
            if [ "$FORCE_STOP" = "true" ]; then
                stop_port "$port" "$service_name" "true"
                return 0
            else
                return 1  # Port in use and not forcing stop
            fi
        fi
        return 0
    else
        # Fallback to inline logic if utility doesn't exist
        if is_port_in_use "$port"; then
            local pid=$(get_port_pid "$port")
            if [ -n "$pid" ]; then
                if [ "$FORCE_STOP" = "true" ]; then
                    echo "   🛑 Stopping existing $service_name on port $port (PID: $pid)..."
                    kill $pid 2>/dev/null || true
                    sleep 1
                    if kill -0 $pid 2>/dev/null; then
                        echo "   🛑 Force killing $service_name (PID: $pid)..."
                        kill -9 $pid 2>/dev/null || true
                        sleep 1
                    fi
                else
                    return 1  # Port in use and not forcing stop
                fi
            fi
        fi
        return 0
    fi
}

# Check and start Backend
echo "📦 Checking Backend API..."
BACKEND_PID=""
if is_port_in_use "$API_PORT"; then
    # Try to find the PID of the process using the port
    BACKEND_PID=$(get_port_pid "$API_PORT")
    if [ -n "$BACKEND_PID" ]; then
        # Verify it's actually the backend by checking the endpoint
        if curl -sf "http://localhost:$API_PORT/docs" > /dev/null 2>&1; then
            echo "   ✅ Backend is already running on port $API_PORT (PID: $BACKEND_PID)"
            echo "   ✅ Backend is responding correctly"
        else
            echo "   ⚠️  Port $API_PORT is in use but backend endpoint not responding"
            if [ "$FORCE_STOP" = "true" ]; then
                echo "   🛑 Force stopping stale process on port $API_PORT..."
                stop_port_if_in_use "$API_PORT" "Backend"
                BACKEND_PID=""
            else
                echo "   ⚠️  This might be a different service. Continuing anyway..."
            fi
        fi
    else
        echo "   ⚠️  Port $API_PORT is in use but couldn't determine PID"
        if [ "$FORCE_STOP" = "true" ]; then
            echo "   🛑 Attempting to free port $API_PORT..."
            pkill -f "uvicorn.*:$API_PORT" 2>/dev/null || true
            sleep 1
        fi
    fi
else
    echo "   Backend is not running. Starting it..."
    
    if [ ! -d "$BACKEND_DIR" ]; then
        echo "❌ Backend directory not found: $BACKEND_DIR"
        exit 1
    fi

    # Setup backend virtual environment if needed
    if [ ! -d "$BACKEND_DIR/venv" ] || [ ! -f "$BACKEND_DIR/venv/bin/activate" ]; then
        if [ -d "$BACKEND_DIR/venv" ]; then
            echo "   Virtual environment exists but is incomplete. Recreating..."
            rm -rf "$BACKEND_DIR/venv"
        else
            echo "   Creating virtual environment..."
        fi
        cd "$BACKEND_DIR"
        python3 -m venv venv
        cd "$SCRIPT_DIR"
    fi

    # Install backend dependencies
    source "$BACKEND_DIR/venv/bin/activate"
    pip install -q --upgrade pip
    pip install -q -r "$BACKEND_DIR/requirements.txt" || true

    # Start backend in background (optimized: parallel startup)
    # Ensure DATABASE_PATH and CORS_ORIGINS are exported for the backend process
    # Note: CORS_ORIGINS must be passed as a JSON array string (Pydantic Settings expects JSON for List fields)
    # Export variables first to ensure they're available to the uvicorn process
    export DATABASE_PATH
    export CORS_ORIGINS
    export ENVIRONMENT
    
    cd "$BACKEND_DIR"
    if [ "$API_RELOAD" = "true" ]; then
        nohup uvicorn app.main:app \
            --host "$API_HOST" \
            --port "$API_PORT" \
            --reload \
            > "$SCRIPT_DIR/backend.log" 2>&1 &
        BACKEND_PID=$!
        disown $BACKEND_PID 2>/dev/null || true
    else
        # Don't include --reload flag when reload is disabled
        nohup uvicorn app.main:app \
            --host "$API_HOST" \
            --port "$API_PORT" \
            > "$SCRIPT_DIR/backend.log" 2>&1 &
        BACKEND_PID=$!
        disown $BACKEND_PID 2>/dev/null || true
    fi
    echo "   ✅ Backend started (PID: $BACKEND_PID)"
    if [ -n "$DATABASE_PATH" ]; then
        echo "   📁 Database: $DATABASE_PATH"
    fi
fi  # End of if is_port_in_use "$API_PORT"

# Check and start Frontend (optimized: start in parallel with backend)
echo ""
echo "📦 Checking Frontend..."
FRONTEND_PID=""
if is_port_in_use "$FRONTEND_PORT"; then
    # Try to find the PID of the process using the port
    FRONTEND_PID=$(get_port_pid "$FRONTEND_PORT")
    if [ -n "$FRONTEND_PID" ]; then
        # Verify it's actually the frontend by checking the endpoint
        if curl -sf "http://localhost:$FRONTEND_PORT" > /dev/null 2>&1; then
            echo "   ✅ Frontend is already running on port $FRONTEND_PORT (PID: $FRONTEND_PID)"
            echo "   ✅ Frontend is responding correctly"
        else
            echo "   ⚠️  Port $FRONTEND_PORT is in use but frontend endpoint not responding"
            if [ "$FORCE_STOP" = "true" ]; then
                echo "   🛑 Force stopping stale process on port $FRONTEND_PORT..."
                stop_port_if_in_use "$FRONTEND_PORT" "Frontend"
                FRONTEND_PID=""
            else
                echo "   ⚠️  This might be a different service. Continuing anyway..."
            fi
        fi
    else
        echo "   ⚠️  Port $FRONTEND_PORT is in use but couldn't determine PID"
        if [ "$FORCE_STOP" = "true" ]; then
            echo "   🛑 Attempting to free port $FRONTEND_PORT..."
            pkill -f "next dev.*:$FRONTEND_PORT" 2>/dev/null || true
            pkill -f "node.*next dev" 2>/dev/null || true
            sleep 1
        fi
    fi
else
    echo "   Frontend is not running. Starting it..."
    
    if [ ! -d "$FRONTEND_DIR" ]; then
        echo "❌ Frontend directory not found: $FRONTEND_DIR"
        exit 1
    fi

    # Install frontend dependencies if needed
    if [ ! -d "$FRONTEND_DIR/node_modules" ]; then
        echo "   Installing dependencies..."
        cd "$FRONTEND_DIR"
        npm install --silent
        cd "$SCRIPT_DIR"
    fi

    # Start frontend in background
    cd "$FRONTEND_DIR"
    export PORT="$FRONTEND_PORT"
    
    # Get API base path from config
    API_BASE_PATH="/api/v1"  # Default fallback
    if [ -f "${SCRIPT_DIR}/config/environments.json" ] && command -v jq &> /dev/null; then
        API_BASE_PATH=$(jq -r '.api.basePath // "/api/v1"' "${SCRIPT_DIR}/config/environments.json" 2>/dev/null || echo "/api/v1")
    fi
    
    # Set NEXT_PUBLIC_API_URL based on environment
    if [ -n "$API_URL" ]; then
        export NEXT_PUBLIC_API_URL="${API_URL}${API_BASE_PATH}"
    else
        # Fallback: construct from API_PORT
        export NEXT_PUBLIC_API_URL="http://localhost:${API_PORT}${API_BASE_PATH}"
    fi
    # Pass port via -p flag since package.json no longer has default port
    nohup npm run dev -- -p "$FRONTEND_PORT" \
        > "$SCRIPT_DIR/frontend.log" 2>&1 &
    FRONTEND_PID=$!
    disown $FRONTEND_PID 2>/dev/null || true
    echo "   ✅ Frontend started (PID: $FRONTEND_PID)"
    echo "   🌐 API URL: $NEXT_PUBLIC_API_URL"
fi  # End of if is_port_in_use "$FRONTEND_PORT"

# Wait for both services in parallel (optimized: wait concurrently)
echo ""
echo "⏳ Waiting for services to be ready..."
if [ -n "$BACKEND_PID" ] && [ -z "$FRONTEND_PID" ]; then
    # Only backend needs to start
    wait_for_service "http://localhost:$API_PORT/docs" "Backend API" || {
        echo "Backend logs:"
        tail -20 "$SCRIPT_DIR/backend.log" || true
        exit 1
    }
elif [ -z "$BACKEND_PID" ] && [ -n "$FRONTEND_PID" ]; then
    # Only frontend needs to start
    wait_for_service "http://localhost:$FRONTEND_PORT" "Frontend" || {
        echo "Frontend logs:"
        tail -20 "$SCRIPT_DIR/frontend.log" || true
        exit 1
    }
elif [ -n "$BACKEND_PID" ] && [ -n "$FRONTEND_PID" ]; then
    # Both need to start - wait in parallel
    (
        wait_for_service "http://localhost:$API_PORT/docs" "Backend API" || {
            echo "Backend logs:"
            tail -20 "$SCRIPT_DIR/backend.log" || true
            exit 1
        }
    ) &
    BACKEND_WAIT_PID=$!
    (
        wait_for_service "http://localhost:$FRONTEND_PORT" "Frontend" || {
            echo "Frontend logs:"
            tail -20 "$SCRIPT_DIR/frontend.log" || true
            exit 1
        }
    ) &
    FRONTEND_WAIT_PID=$!
    
    # Wait for both wait processes
    wait $BACKEND_WAIT_PID || BACKEND_WAIT_FAILED=1
    wait $FRONTEND_WAIT_PID || FRONTEND_WAIT_FAILED=1
    
    if [ -n "$BACKEND_WAIT_FAILED" ] || [ -n "$FRONTEND_WAIT_FAILED" ]; then
        echo "❌ One or more services failed to start"
        exit 1
    fi
fi

echo ""
echo "═══════════════════════════════════════════════════════════════"
echo "✅ All services are ready!"
echo "═══════════════════════════════════════════════════════════════"
echo "   Backend:  http://localhost:$API_PORT"
echo "   Frontend: http://localhost:$FRONTEND_PORT"
echo ""

if [ -n "$BACKEND_PID" ] && [ -n "$FRONTEND_PID" ]; then
    echo "   Backend PID:  $BACKEND_PID"
    echo "   Frontend PID: $FRONTEND_PID"
    echo ""
    echo "   To stop services, run:"
    echo "   ./scripts/services/stop-services.sh"
    echo "   or: kill $BACKEND_PID $FRONTEND_PID"
    
    # Save PIDs to file for cleanup (only if we started them)
    if [ -f "$SCRIPT_DIR/.service-pids" ]; then
        # Merge with existing PIDs if any
        EXISTING_PIDS=$(cat "$SCRIPT_DIR/.service-pids" 2>/dev/null || echo "")
        echo "$BACKEND_PID $FRONTEND_PID $EXISTING_PIDS" | tr ' ' '\n' | sort -u | tr '\n' ' ' > "$SCRIPT_DIR/.service-pids"
    else
        echo "$BACKEND_PID $FRONTEND_PID" > "$SCRIPT_DIR/.service-pids"
    fi
else
    echo "   Note: Some services were already running"
    echo "   PIDs may not be available for services started externally"
fi

echo "═══════════════════════════════════════════════════════════════"
