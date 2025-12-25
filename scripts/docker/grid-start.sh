#!/bin/bash
# Start Selenium Grid with options

set -e

# Default environment
ENV=${1:-default}

echo "🚀 Starting Selenium Grid ($ENV environment)"
echo "============================================"

case $ENV in
    dev|development)
        echo "📦 Starting development environment..."
        docker-compose -f docker-compose.dev.yml up -d
        ;;
    prod|production)
        echo "📦 Starting production environment..."
        docker-compose -f docker-compose.prod.yml up -d
        ;;
    *)
        echo "📦 Starting default environment..."
        docker-compose up -d selenium-hub chrome-node-1 firefox-node
        ;;
esac

echo ""
echo "⏳ Waiting for Grid to be ready..."
sleep 10

# Run health check
if command -v jq &> /dev/null; then
    ./scripts/docker/grid-health.sh
else
    echo "ℹ️  Install 'jq' for detailed health checks: brew install jq"
    echo ""
    echo "✅ Grid started!"
    echo "🌐 Grid Console: http://localhost:4444"
fi
