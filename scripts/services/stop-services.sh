#!/bin/bash
# scripts/services/stop-services.sh
# Service Stopper
#
# Purpose: Stop Backend and Frontend services gracefully
#
# Usage:
#   ./scripts/services/stop-services.sh [ENVIRONMENT]
#
# Parameters:
#   ENVIRONMENT   Environment to stop services for: dev, test, prod (default: "dev" or from ENVIRONMENT env var)
#
# Examples:
#   ./scripts/services/stop-services.sh
#   ./scripts/services/stop-services.sh test
#   ENVIRONMENT=prod ./scripts/services/stop-services.sh
#
# Description:
#   This script stops Backend and Frontend services that were started by start-services-for-ci.sh
#   or start-env.sh. It can also stop services running on configured ports if PID files are missing.
#   Gracefully terminates processes and cleans up PID files.
#
# Dependencies:
#   - Services must be running (started by start scripts)
#   - scripts/ci/env-config.sh (for port configuration)
#   - Standard Unix utilities (ps, kill, etc.)
#
# Output:
#   - Service shutdown status
#   - PID file cleanup
#   - Exit code: 0 on success, non-zero on failure
#
# Notes:
#   - Gracefully stops services using stored PIDs
#   - Falls back to port-based process detection if PID file missing
#   - Cleans up PID files after stopping
#   - Safe to run multiple times (idempotent)
#
# Last Updated: January 2026

set -e

# Since this script is in scripts/services/, we need to go up two levels
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
PID_FILE="$SCRIPT_DIR/.service-pids"

# Determine environment and ports
# Priority: ENVIRONMENT env var > detect from running processes > default to dev
ENVIRONMENT=${ENVIRONMENT:-"dev"}

# Try to load environment configuration
ENV_CONFIG_SCRIPT="${SCRIPT_DIR}/scripts/ci/env-config.sh"
if [ -f "$ENV_CONFIG_SCRIPT" ]; then
    source "$ENV_CONFIG_SCRIPT"
    # Get ports for the environment
    env=$(echo "${ENVIRONMENT}" | tr '[:upper:]' '[:lower:]')
    eval "$(get_ports_for_environment "$env")"
    API_PORT=${API_PORT:-"8003"}  # Fallback to dev if not set
    FRONTEND_PORT=${FRONTEND_PORT:-"3003"}  # Fallback to dev if not set
else
    # Fallback: use environment variable or default to dev
    case "$ENVIRONMENT" in
        test)
            API_PORT=${API_PORT:-"8004"}
            FRONTEND_PORT=${FRONTEND_PORT:-"3004"}
            ;;
        prod)
            API_PORT=${API_PORT:-"8005"}
            FRONTEND_PORT=${FRONTEND_PORT:-"3005"}
            ;;
        *)
            API_PORT=${API_PORT:-"8003"}  # Default to DEV port per ONE_GOAL.md
            FRONTEND_PORT=${FRONTEND_PORT:-"3003"}  # Default to DEV port per ONE_GOAL.md
            ;;
    esac
fi

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
if [ -n "$ENVIRONMENT" ]; then
    echo "   Environment: $ENVIRONMENT"
    echo "   Backend port: $API_PORT"
    echo "   Frontend port: $FRONTEND_PORT"
fi
echo ""

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
# Stop services on the configured ports for the environment
stop_port "$API_PORT" "Backend"
stop_port "$FRONTEND_PORT" "Frontend"

# Also check and stop services on other environment ports (dev, test, prod)
# This ensures we stop services even if ENVIRONMENT wasn't set correctly
for port in 8003 8004 8005; do
    if [ "$port" != "$API_PORT" ] && lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        stop_port "$port" "Backend (port $port)"
    fi
done

for port in 3003 3004 3005; do
    if [ "$port" != "$FRONTEND_PORT" ] && lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        stop_port "$port" "Frontend (port $port)"
    fi
done

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

# Verify ports are free (check all environment ports)
echo ""
echo "   Verifying ports are free..."
# Check all backend ports (dev, test, prod)
for port in 8003 8004 8005; do
    if [ -f "$PORT_UTILS" ]; then
        if is_port_in_use "$port"; then
            echo "   ⚠️  Port $port is still in use"
        else
            echo "   ✅ Port $port is free"
        fi
    else
        # Fallback to inline check if utility doesn't exist
        if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
            echo "   ⚠️  Port $port is still in use"
        else
            echo "   ✅ Port $port is free"
        fi
    fi
done

# Check all frontend ports (dev, test, prod)
for port in 3003 3004 3005; do
    if [ -f "$PORT_UTILS" ]; then
        if is_port_in_use "$port"; then
            echo "   ⚠️  Port $port is still in use"
        else
            echo "   ✅ Port $port is free"
        fi
    else
        # Fallback to inline check if utility doesn't exist
        if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
            echo "   ⚠️  Port $port is still in use"
        else
            echo "   ✅ Port $port is free"
        fi
    fi
done

echo ""
echo "✅ Service cleanup complete"
