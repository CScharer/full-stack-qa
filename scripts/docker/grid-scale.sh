#!/bin/bash
# Scale Selenium Grid nodes

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
