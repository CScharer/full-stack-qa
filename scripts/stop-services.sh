#!/bin/bash
# Stop Backend and Frontend services
# This script stops services started by start-services-for-ci.sh
# It can also stop services running on the configured ports

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PID_FILE="$SCRIPT_DIR/.service-pids"
API_PORT=${API_PORT:-"8003"}  # Default to DEV port per ONE_GOAL.md
FRONTEND_PORT=${FRONTEND_PORT:-"3003"}  # Default to DEV port per ONE_GOAL.md

# Source port utilities if available
PORT_UTILS="${SCRIPT_DIR}/scripts/ci/port-utils.sh"
if [ -f "$PORT_UTILS" ]; then
    source "$PORT_UTILS"
else
    # Fallback: define port functions inline if utility doesn't exist
    stop_port() {
        local port=$1
        local service_name=$2
        
        if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
            local pid=$(lsof -ti :$port 2>/dev/null | head -1)
            if [ -n "$pid" ]; then
                echo "   Stopping $service_name on port $port (PID: $pid)..."
                kill $pid 2>/dev/null || true
                # Wait a moment and force kill if still running
                sleep 2
                if kill -0 $pid 2>/dev/null; then
                    echo "   Force killing $service_name (PID: $pid)..."
                    kill -9 $pid 2>/dev/null || true
                fi
                echo "   ✅ $service_name stopped"
            fi
        else
            echo "   ℹ️  $service_name is not running on port $port"
        fi
    }
fi

echo "🛑 Stopping services..."

# Stop services by PID file if it exists
if [ -f "$PID_FILE" ]; then
    PIDS=$(cat "$PID_FILE" | tr ' ' '\n' | grep -v '^$' | sort -u)
    if [ -n "$PIDS" ]; then
        echo "   Stopping services from PID file..."
        for pid in $PIDS; do
            if kill -0 $pid 2>/dev/null; then
                echo "   Stopping process $pid..."
                kill $pid 2>/dev/null || true
                sleep 1
                if kill -0 $pid 2>/dev/null; then
                    kill -9 $pid 2>/dev/null || true
                fi
            fi
        done
        rm -f "$PID_FILE"
    fi
fi

# Also stop services by port (in case PID file is missing or stale)
stop_port "$API_PORT" "Backend"
stop_port "$FRONTEND_PORT" "Frontend"

# Fallback: try to kill by process name (less reliable)
echo ""
echo "   Checking for any remaining service processes..."
pkill -f "uvicorn app.main:app" 2>/dev/null && echo "   ✅ Stopped backend processes" || true
pkill -f "next dev" 2>/dev/null && echo "   ✅ Stopped frontend processes" || true

# Clean up Next.js lock files
FRONTEND_DIR="$SCRIPT_DIR/frontend"
if [ -d "$FRONTEND_DIR" ]; then
    LOCK_FILE="$FRONTEND_DIR/.next/dev/lock"
    if [ -f "$LOCK_FILE" ]; then
        echo "   Removing Next.js lock file..."
        rm -f "$LOCK_FILE" 2>/dev/null && echo "   ✅ Next.js lock file removed" || true
    fi
fi

# Verify ports are free
echo ""
echo "   Verifying ports are free..."
if [ -f "$PORT_UTILS" ]; then
    if is_port_in_use "$API_PORT"; then
        echo "   ⚠️  Port $API_PORT is still in use"
    else
        echo "   ✅ Port $API_PORT is free"
    fi
    
    if is_port_in_use "$FRONTEND_PORT"; then
        echo "   ⚠️  Port $FRONTEND_PORT is still in use"
    else
        echo "   ✅ Port $FRONTEND_PORT is free"
    fi
else
    # Fallback to inline check if utility doesn't exist
    if lsof -Pi :$API_PORT -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo "   ⚠️  Port $API_PORT is still in use"
    else
        echo "   ✅ Port $API_PORT is free"
    fi
    
    if lsof -Pi :$FRONTEND_PORT -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo "   ⚠️  Port $FRONTEND_PORT is still in use"
    else
        echo "   ✅ Port $FRONTEND_PORT is free"
    fi
fi

echo ""
echo "✅ Service cleanup complete"
