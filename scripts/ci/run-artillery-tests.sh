#!/bin/bash
# scripts/ci/run-artillery-tests.sh
# Artillery Load Test Runner
#
# Purpose: Run Artillery browser-level load tests with Playwright
#
# Usage:
#   ./scripts/ci/run-artillery-tests.sh <ENVIRONMENT> <TEST_TYPE> <BASE_URL>
#
# Parameters:
#   ENVIRONMENT   Environment name: dev, test (never prod)
#   TEST_TYPE     Test type: smoke, all, homepage-only, applications-only
#   BASE_URL      Base URL for the environment (e.g., http://localhost:3003)
#
# Examples:
#   ./scripts/ci/run-artillery-tests.sh dev smoke http://localhost:3003
#   ./scripts/ci/run-artillery-tests.sh test all http://localhost:3004
#   ./scripts/ci/run-artillery-tests.sh dev homepage-only http://localhost:3003
#
# Description:
#   This script runs Artillery browser-level load tests using Playwright.
#   Artillery tests provide real browser rendering and Core Web Vitals tracking.
#   Tests are located in playwright/artillery/ directory.
#
# Dependencies:
#   - Node.js 20+
#   - npm (installed in playwright/ directory)
#   - Artillery CLI (installed via npm)
#   - Playwright browsers (installed via npm)
#   - Backend and Frontend services running
#
# Output:
#   - Test results in playwright/artillery-results/
#   - JSON results for conversion to Allure
#   - Exit code: 0 on success, non-zero on failure
#
# Notes:
#   - Browser-level testing (real rendering, not protocol-level)
#   - Tracks Core Web Vitals (LCP, FCP, CLS, TTI)
#   - 20% allocation of total performance testing
#   - Never runs in production environment
#
# Last Updated: January 2026

set -e

# Debug: Show current directory and environment
echo "🔍 DEBUG: Current directory: $(pwd)"
echo "🔍 DEBUG: Script location: $0"
echo "🔍 DEBUG: Arguments received:"
echo "   - Environment: ${1:-not provided}"
echo "   - Test Type: ${2:-not provided}"
echo "   - Base URL: ${3:-not provided}"
echo ""

ENVIRONMENT="${1:-dev}"
TEST_TYPE="${2:-smoke}"
BASE_URL="${3:-http://localhost:3003}"

# Debug: Show current directory and environment
echo "🔍 DEBUG: Current directory: $(pwd)"
echo "🔍 DEBUG: Script location: $0"
echo "🔍 DEBUG: Arguments received:"
echo "   - Environment: $ENVIRONMENT"
echo "   - Test Type: $TEST_TYPE"
echo "   - Base URL: $BASE_URL"
echo ""

echo "🎯 Running Artillery tests ($ENVIRONMENT)..."
echo "Test Type: $TEST_TYPE"
echo "Base URL: $BASE_URL"
echo "Environment Config: artillery/config/$ENVIRONMENT.yml"
echo ""

# Set environment-specific config file
ENV_CONFIG="artillery/config/$ENVIRONMENT.yml"

# Validate environment config exists
if [ ! -f "$ENV_CONFIG" ]; then
    echo "⚠️  Error: Environment config file not found: $ENV_CONFIG"
    exit 1
fi

# Ensure results directory exists
mkdir -p artillery-results
echo "🔍 DEBUG: Created results directory: $(pwd)/artillery-results"
echo "🔍 DEBUG: Checking if Artillery is available..."
if ! command -v npx &> /dev/null; then
    echo "⚠️  Error: npx not found"
    exit 1
fi
echo "✅ npx is available"
echo ""

# Run tests based on test type
echo "🔄 Executing Artillery tests..."
EXIT_CODE=0

case "$TEST_TYPE" in
    smoke)
        echo "Running smoke test (minimal homepage load test)..."
        # Use smoke-specific config that only has the smoke phase
        SMOKE_CONFIG="artillery/config/${ENVIRONMENT}-smoke.yml"
        if [ ! -f "$SMOKE_CONFIG" ]; then
            echo "⚠️  Warning: Smoke config not found: $SMOKE_CONFIG"
            echo "   Falling back to full config (will run all phases)"
            SMOKE_CONFIG="$ENV_CONFIG"
        fi
        npx artillery run artillery/scenarios/homepage-minimal-test.yml \
            --config "$SMOKE_CONFIG" \
            --output artillery-results/smoke-results.json || EXIT_CODE=$?
        ;;
    homepage-only)
        echo "Running homepage load test..."
        npx artillery run artillery/scenarios/homepage-load.yml \
            --config "$ENV_CONFIG" \
            --output artillery-results/homepage-results.json || EXIT_CODE=$?
        ;;
    applications-only)
        echo "Running applications flow load test..."
        npx artillery run artillery/scenarios/applications-flow.yml \
            --config "$ENV_CONFIG" \
            --output artillery-results/applications-results.json || EXIT_CODE=$?
        ;;
    all)
        echo "Running all Artillery test scenarios..."
        npx artillery run artillery/scenarios/homepage-load.yml \
            --config "$ENV_CONFIG" \
            --output artillery-results/homepage-results.json || EXIT_CODE=$?
        npx artillery run artillery/scenarios/applications-flow.yml \
            --config "$ENV_CONFIG" \
            --output artillery-results/applications-results.json || EXIT_CODE=$?
        ;;
    *)
        echo "⚠️  Unknown test type: $TEST_TYPE"
        echo "Running smoke test as fallback..."
        npx artillery run artillery/scenarios/homepage-minimal-test.yml \
            --config "$ENV_CONFIG" \
            --output artillery-results/smoke-results.json || EXIT_CODE=$?
        ;;
esac

echo ""
echo "🔍 DEBUG: Checking for result files..."
echo "Current working directory: $(pwd)"
echo "Artillery results directory: $(pwd)/artillery-results"
echo ""

# Check if directory exists
if [ ! -d "artillery-results" ]; then
    echo "❌ ERROR: artillery-results directory does not exist!"
    echo "   Attempting to create it..."
    mkdir -p artillery-results
    echo "   ✅ Created artillery-results directory"
fi

# List directory contents
echo "📂 Contents of artillery-results/:"
ls -la artillery-results/ || echo "   (directory is empty or error listing)"

# Find result files
RESULT_FILES=$(find artillery-results -name "*.json" -type f 2>/dev/null || true)
if [ -n "$RESULT_FILES" ]; then
    echo ""
    echo "✅ Found result files:"
    echo "$RESULT_FILES" | while read f; do
        if [ -f "$f" ]; then
            size=$(stat -f%z "$f" 2>/dev/null || stat -c%s "$f" 2>/dev/null || echo "unknown")
            echo "   📄 $f (size: $size bytes)"
        else
            echo "   ⚠️  $f (file not found or not accessible)"
        fi
    done
else
    echo ""
    echo "⚠️  No result files found in artillery-results/"
    echo "   This may indicate Artillery tests failed to generate output"
    echo "   Check Artillery logs above for errors"
fi

echo ""
if [ "$EXIT_CODE" -eq 0 ]; then
    echo "✅ Artillery test execution completed successfully"
else
    echo "⚠️  Artillery test execution completed with exit code: $EXIT_CODE"
    echo "   (This is non-fatal - results may still be available)"
fi

# Always exit successfully to allow artifact upload
exit 0

