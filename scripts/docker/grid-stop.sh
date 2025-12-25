#!/bin/bash
# Stop Selenium Grid

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
