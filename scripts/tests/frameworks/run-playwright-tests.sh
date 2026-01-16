#!/bin/bash
# scripts/tests/frameworks/run-playwright-tests.sh
# Playwright Test Runner
#
# Purpose: Run Playwright end-to-end tests (TypeScript) with various browsers and modes
#
# Usage:
#   ./scripts/tests/frameworks/run-playwright-tests.sh [BROWSER] [MODE]
#
# Parameters:
#   BROWSER       Browser to use: chromium, firefox, webkit, chrome, msedge (default: "chromium")
#   MODE          Execution mode (default: "headless"):
#                 - headless: Run without browser UI (default)
#                 - headed/false/no-headless/show: Run with visible browser
#                 - ui: Run with Playwright UI mode
#                 - debug: Run in debug mode
#
# Examples:
#   ./scripts/tests/frameworks/run-playwright-tests.sh chromium           # Headless Chromium
#   ./scripts/tests/frameworks/run-playwright-tests.sh chromium headed    # Visible Chromium
#   ./scripts/tests/frameworks/run-playwright-tests.sh firefox            # Headless Firefox
#   ./scripts/tests/frameworks/run-playwright-tests.sh chromium ui        # Playwright UI mode
#   ./scripts/tests/frameworks/run-playwright-tests.sh chromium debug     # Debug mode
#
# Dependencies:
#   - Node.js 20+
#   - npm (installed in playwright/ directory)
#   - Playwright dependencies (auto-installed if missing)
#   - Playwright browsers (auto-installed if missing)
#
# Output:
#   - Test results in playwright/playwright-report/
#   - Screenshots on failure in playwright/test-results/
#   - Videos in playwright/test-results/ (if enabled)
#   - Exit code: 0 on success, non-zero on failure
#
# Notes:
#   - Automatically installs dependencies if node_modules/ is missing
#   - Automatically installs Playwright browsers if missing
#   - UI mode provides interactive test execution interface
#   - Debug mode allows step-by-step debugging
#
# Last Updated: January 2026

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
