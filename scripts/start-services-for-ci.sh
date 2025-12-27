#!/bin/bash
# Start Backend and Frontend services for CI/CD testing
# This script checks if services are running and starts them if needed
# It's idempotent - safe to run multiple times

set -e

# Get the script directory (project root)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$SCRIPT_DIR"

# Configuration
BACKEND_DIR="${SCRIPT_DIR}/backend"
FRONTEND_DIR="${SCRIPT_DIR}/frontend"
ENVIRONMENT=${ENVIRONMENT:-"dev"}  # dev, test, or prod
API_RELOAD=${API_RELOAD:-"false"}  # Disable reload for CI
MAX_WAIT=${MAX_WAIT:-120}  # Maximum wait time in seconds
FORCE_STOP=${FORCE_STOP:-"false"}  # Force stop existing services on ports

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
            sleep 2
            elapsed=$((elapsed + 2))
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
                    sleep 2
                    if kill -0 $pid 2>/dev/null; then
                        echo "   🛑 Force killing $service_name (PID: $pid)..."
                        kill -9 $pid 2>/dev/null || true
                    fi
                    sleep 1
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
            sleep 2
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

    # Start backend in background
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

    # Wait for backend to be ready
    wait_for_service "http://localhost:$API_PORT/docs" "Backend API" || {
        echo "Backend logs:"
        tail -20 "$SCRIPT_DIR/backend.log" || true
        exit 1
    }
fi  # End of if is_port_in_use "$API_PORT"

# Check and start Frontend
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
            sleep 2
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
    # Pass port via -p flag since package.json no longer has default port
    nohup npm run dev -- -p "$FRONTEND_PORT" \
        > "$SCRIPT_DIR/frontend.log" 2>&1 &
    FRONTEND_PID=$!
    disown $FRONTEND_PID 2>/dev/null || true
    echo "   ✅ Frontend started (PID: $FRONTEND_PID)"

    # Wait for frontend to be ready
    wait_for_service "http://localhost:$FRONTEND_PORT" "Frontend" || {
        echo "Frontend logs:"
        tail -20 "$SCRIPT_DIR/frontend.log" || true
        exit 1
    }
fi  # End of if is_port_in_use "$FRONTEND_PORT"

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
    echo "   ./scripts/stop-services.sh"
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
