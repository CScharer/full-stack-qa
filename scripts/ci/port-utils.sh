#!/bin/bash
# Port Utility Functions
# Common port-related functions for service management scripts
# Source this file in other scripts: source "$(dirname "$0")/port-utils.sh"
#
# Functions:
#   is_port_in_use <port>          - Check if a port is in use (returns 0 if in use, 1 if not)
#   get_port_pid <port>             - Get the PID of the process using a port (returns PID or empty)
#   stop_port <port> <service-name> - Stop the process using a port (graceful then force kill)
#   check_port_status <port>        - Check and display port status information

# Function to check if a port is in use
is_port_in_use() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1 || nc -z localhost $port 2>/dev/null; then
        return 0  # Port is in use
    else
        return 1  # Port is not in use
    fi
}

# Function to get the PID of the process using a port
get_port_pid() {
    local port=$1
    lsof -ti :$port 2>/dev/null | head -1 || echo ""
}

# Function to stop process on a port
stop_port() {
    local port=$1
    local service_name=$2
    local force=${3:-false}  # Optional: force stop flag
    
    if is_port_in_use "$port"; then
        local pid=$(get_port_pid "$port")
        if [ -n "$pid" ]; then
            if [ "$force" = "true" ]; then
                echo "   🛑 Stopping existing $service_name on port $port (PID: $pid)..."
            else
                echo "   Stopping $service_name on port $port (PID: $pid)..."
            fi
            kill $pid 2>/dev/null || true
            # Wait a moment and force kill if still running
            sleep 1
            if kill -0 $pid 2>/dev/null; then
                if [ "$force" = "true" ]; then
                    echo "   Force killing $service_name (PID: $pid)..."
                else
                    echo "   Force killing $service_name (PID: $pid)..."
                fi
                kill -9 $pid 2>/dev/null || true
            fi
            echo "   ✅ $service_name stopped"
        fi
    else
        echo "   ℹ️  $service_name is not running on port $port"
    fi
}

# Function to check and display port status
check_port_status() {
    local port=$1
    local service_name=$2
    
    if is_port_in_use "$port"; then
        local pid=$(get_port_pid "$port")
        if [ -n "$pid" ]; then
            echo "   ✅ $service_name is running on port $port (PID: $pid)"
            return 0
        else
            echo "   ⚠️  Port $port is in use but PID could not be determined"
            return 0
        fi
    else
        echo "   ℹ️  $service_name is not running on port $port"
        return 1
    fi
}

