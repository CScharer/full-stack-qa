#!/bin/bash

# Run Cypress tests (TypeScript)
# Usage: ./scripts/run-cypress-tests.sh [mode] [browser]
# Example: ./scripts/run-cypress-tests.sh run chrome
#          ./scripts/run-cypress-tests.sh open

set -e

MODE=${1:-run}
BROWSER=${2:-chrome}

cd cypress

# Check if node_modules exists, if not install dependencies
if [ ! -d "node_modules" ]; then
    echo "📦 Installing Cypress dependencies..."
    npm install
fi

# Type check
echo "🔍 Type checking TypeScript files..."
npm run build || true

case $MODE in
    open)
        echo "🎬 Opening Cypress Test Runner..."
        npm run cypress:open
        ;;
    run)
        echo "🧪 Running Cypress tests in headless mode..."
        if [ "$BROWSER" = "chrome" ]; then
            npm run cypress:run:chrome
        elif [ "$BROWSER" = "firefox" ]; then
            npm run cypress:run:firefox
        elif [ "$BROWSER" = "edge" ]; then
            npm run cypress:run:edge
        else
            npm run cypress:run
        fi
        ;;
    *)
        echo "❌ Invalid mode. Use 'open' or 'run'"
        exit 1
        ;;
esac

cd ..

echo ""
echo "✅ Cypress tests completed!"

