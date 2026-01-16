#!/bin/bash
# scripts/docker/grid-start.sh
# Selenium Grid Starter
#
# Purpose: Start Selenium Grid with environment-specific configuration
#
# Usage:
#   ./scripts/docker/grid-start.sh [ENVIRONMENT]
#
# Parameters:
#   ENVIRONMENT   Environment to start: dev, development, prod, production, or default (default: "default")
#
# Examples:
#   ./scripts/docker/grid-start.sh                    # Default environment
#   ./scripts/docker/grid-start.sh dev                # Development environment
#   ./scripts/docker/grid-start.sh production         # Production environment
#
# Description:
#   This script starts Selenium Grid using Docker Compose with environment-specific
#   configurations. It supports development, production, and default configurations.
#
# Dependencies:
#   - Docker and Docker Compose
#   - docker-compose.yml (default)
#   - docker-compose.dev.yml (development)
#   - docker-compose.prod.yml (production)
#
# Output:
#   - Selenium Grid services started
#   - Console output showing startup progress
#   - Exit code: 0 on success, non-zero on failure
#
# Notes:
#   - Uses Docker Compose to manage Grid services
#   - Environment determines which compose file is used
#   - Services run in detached mode (-d flag)
#
# Last Updated: January 2026

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
# No sleep needed - use proper health check instead
# Grid should be checked via wait-for-grid.sh or health check script
if command -v jq &> /dev/null; then
    ./scripts/docker/grid-health.sh
else
    echo "ℹ️  Install 'jq' for detailed health checks: brew install jq"
    echo ""
    echo "✅ Grid started!"
    echo "🌐 Grid Console: http://localhost:4444"
fi
