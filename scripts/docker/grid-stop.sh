#!/bin/bash
# scripts/docker/grid-stop.sh
# Selenium Grid Stopper
#
# Purpose: Stop Selenium Grid services for a specific environment
#
# Usage:
#   ./scripts/docker/grid-stop.sh [ENVIRONMENT]
#
# Parameters:
#   ENVIRONMENT   Environment to stop: dev, development, prod, production, or default (default: "default")
#
# Examples:
#   ./scripts/docker/grid-stop.sh                      # Default environment
#   ./scripts/docker/grid-stop.sh dev                  # Development environment
#   ./scripts/docker/grid-stop.sh production           # Production environment
#
# Description:
#   This script stops Selenium Grid services using Docker Compose with environment-specific
#   configurations. It gracefully shuts down all Grid services.
#
# Dependencies:
#   - Docker and Docker Compose
#   - docker-compose.yml (default)
#   - docker-compose.dev.yml (development)
#   - docker-compose.prod.yml (production)
#
# Output:
#   - Selenium Grid services stopped
#   - Console output showing shutdown progress
#   - Exit code: 0 on success, non-zero on failure
#
# Notes:
#   - Uses Docker Compose to stop Grid services
#   - Environment determines which compose file is used
#   - Gracefully shuts down all containers
#
# Last Updated: January 2026

set -e

ENV=${1:-default}

echo "🛑 Stopping Selenium Grid ($ENV environment)"
echo "==========================================="

case $ENV in
    dev|development)
        docker-compose -f docker-compose.dev.yml down
        ;;
    prod|production)
        docker-compose -f docker-compose.prod.yml down
        ;;
    *)
        docker-compose down
        ;;
esac

echo "✅ Grid stopped successfully!"
