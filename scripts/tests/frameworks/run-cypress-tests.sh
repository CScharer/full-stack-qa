#!/bin/bash
# scripts/tests/frameworks/run-cypress-tests.sh
# Cypress Test Runner
#
# Purpose: Run Cypress end-to-end tests (TypeScript) in interactive or headless mode
#
# Usage:
#   ./scripts/tests/frameworks/run-cypress-tests.sh [MODE] [BROWSER]
#
# Parameters:
#   MODE          Test execution mode: "run" (headless) or "open" (interactive) (default: "run")
#   BROWSER       Browser to use: chrome, firefox, edge, electron (default: "chrome")
#
# Examples:
#   ./scripts/tests/frameworks/run-cypress-tests.sh run chrome    # Headless Chrome
#   ./scripts/tests/frameworks/run-cypress-tests.sh run firefox  # Headless Firefox
#   ./scripts/tests/frameworks/run-cypress-tests.sh open         # Interactive mode (Cypress UI)
#
# Dependencies:
#   - Node.js 20+
#   - npm (installed in cypress/ directory)
#   - Cypress dependencies (auto-installed if missing)
#
# Output:
#   - Test results in cypress/cypress/results/
#   - Screenshots on failure in cypress/cypress/screenshots/
#   - Videos in cypress/cypress/videos/ (if enabled)
#   - Exit code: 0 on success, non-zero on failure
#
# Notes:
#   - Automatically installs dependencies if node_modules/ is missing
#   - Interactive mode ("open") opens Cypress Test Runner UI
#   - Headless mode ("run") executes tests without UI
#
# Last Updated: January 2026

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

