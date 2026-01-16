#!/bin/bash

# Run Playwright tests (TypeScript)
# Usage: ./scripts/run-playwright-tests.sh [browser] [mode]
# Example: ./scripts/run-playwright-tests.sh chromium
#          ./scripts/run-playwright-tests.sh chromium headed
#          ./scripts/run-playwright-tests.sh chromium false  (same as headed)
#          ./scripts/run-playwright-tests.sh chrome ui
# Modes: headless (default), headed/false/no-headless/show, ui, debug

set -e

BROWSER=${1:-chromium}
MODE=${2:-headless}

cd playwright

# Check if node_modules exists, if not install dependencies
if [ ! -d "node_modules" ]; then
    echo "📦 Installing Playwright dependencies..."
    npm install
    echo "📦 Installing Playwright browsers..."
    npx playwright install --with-deps chromium
fi

# Type check
echo "🔍 Type checking TypeScript files..."
npx tsc --noEmit || true

case $MODE in
    ui)
        echo "🎭 Running Playwright tests in UI mode..."
        npm run test:ui
        ;;
    debug)
        echo "🐛 Running Playwright tests in debug mode..."
        npm run test:debug
        ;;
    headed|false|no-headless|show)
        echo "🎭 Running Playwright tests in headed mode (browser visible)..."
        if [ "$BROWSER" = "chromium" ] || [ "$BROWSER" = "chrome" ]; then
            npx playwright test --project=chromium --headed
        elif [ "$BROWSER" = "firefox" ]; then
            npx playwright test --project=firefox --headed
        elif [ "$BROWSER" = "webkit" ]; then
            npx playwright test --project=webkit --headed
        else
            npm run test:headed
        fi
        ;;
    *)
        echo "🧪 Running Playwright tests in headless mode..."
        if [ "$BROWSER" = "chromium" ] || [ "$BROWSER" = "chrome" ]; then
            npm run test:chrome
        elif [ "$BROWSER" = "firefox" ]; then
            npm run test:firefox
        elif [ "$BROWSER" = "webkit" ]; then
            npm run test:webkit
        else
            npm test
        fi
        ;;
esac

cd ..

echo ""
echo "✅ Playwright tests completed!"
echo "📊 View report: cd playwright && npm run test:report"
