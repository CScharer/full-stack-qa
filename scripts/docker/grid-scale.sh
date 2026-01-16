#!/bin/bash
# scripts/docker/grid-scale.sh
# Selenium Grid Node Scaler
#
# Purpose: Scale Selenium Grid browser nodes up or down
#
# Usage:
#   ./scripts/docker/grid-scale.sh [BROWSER] [REPLICAS]
#
# Parameters:
#   BROWSER    Browser type to scale: chrome, chromium, firefox, edge, or all (default: "chrome")
#   REPLICAS   Number of node instances to run (default: 3)
#
# Examples:
#   ./scripts/docker/grid-scale.sh                      # Scale Chrome to 3 nodes
#   ./scripts/docker/grid-scale.sh chrome 5             # Scale Chrome to 5 nodes
#   ./scripts/docker/grid-scale.sh firefox 2            # Scale Firefox to 2 nodes
#   ./scripts/docker/grid-scale.sh edge 4               # Scale Edge to 4 nodes
#
# Description:
#   This script scales Selenium Grid browser nodes using Docker Compose's --scale flag.
#   It allows dynamic scaling of browser nodes to handle varying test loads.
#
# Dependencies:
#   - Docker and Docker Compose
#   - docker-compose.yml with node service definitions
#   - Selenium Grid Hub must be running
#
# Output:
#   - Scaled browser nodes started
#   - Console output showing scaling progress
#   - Exit code: 0 on success, non-zero on failure
#
# Notes:
#   - Uses Docker Compose --scale flag
#   - Nodes automatically register with Hub
#   - Useful for load testing and parallel execution
#   - Can scale multiple browser types independently
#
# Last Updated: January 2026

set -e

BROWSER=${1:-chrome}
REPLICAS=${2:-3}

echo "⚖️  Scaling $BROWSER nodes to $REPLICAS instances"
echo "=============================================="

case $BROWSER in
    chrome|chromium)
        docker-compose up -d --scale chrome-node-1=$REPLICAS
        ;;
    firefox)
        docker-compose up -d --scale firefox-node=$REPLICAS
        ;;
    edge)
        docker-compose up -d --scale edge-node=$REPLICAS
        ;;
    *)
        echo "❌ Unknown browser: $BROWSER"
        echo "Usage: ./grid-scale.sh [chrome|firefox|edge] [replicas]"
        exit 1
        ;;
esac

echo ""
echo "✅ Scaling complete!"
echo ""
echo "Current nodes:"
docker ps --filter "name=node" --format "table {{.Names}}\t{{.Status}}"
