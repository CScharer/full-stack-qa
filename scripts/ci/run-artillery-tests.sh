#!/bin/bash
# Run Artillery Load Tests
# Usage: ./scripts/ci/run-artillery-tests.sh <environment> <test_type> <base_url>
#
# Arguments:
#   environment    - Environment name (dev, test)
#   test_type      - Test type (smoke, all, homepage-only, applications-only)
#   base_url       - Base URL for the environment

set -e

ENVIRONMENT="${1:-dev}"
TEST_TYPE="${2:-smoke}"
BASE_URL="${3:-http://localhost:3003}"

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

# Run tests based on test type
case "$TEST_TYPE" in
    smoke)
        echo "Running smoke test (minimal homepage load test)..."
        npx artillery run artillery/scenarios/homepage-minimal-test.yml \
            --config "$ENV_CONFIG" \
            --output artillery-results/smoke-results.json || true
        ;;
    homepage-only)
        echo "Running homepage load test..."
        npx artillery run artillery/scenarios/homepage-load.yml \
            --config "$ENV_CONFIG" \
            --output artillery-results/homepage-results.json || true
        ;;
    applications-only)
        echo "Running applications flow load test..."
        npx artillery run artillery/scenarios/applications-flow.yml \
            --config "$ENV_CONFIG" \
            --output artillery-results/applications-results.json || true
        ;;
    all)
        echo "Running all Artillery test scenarios..."
        npx artillery run artillery/scenarios/homepage-load.yml \
            --config "$ENV_CONFIG" \
            --output artillery-results/homepage-results.json || true
        npx artillery run artillery/scenarios/applications-flow.yml \
            --config "$ENV_CONFIG" \
            --output artillery-results/applications-results.json || true
        ;;
    *)
        echo "⚠️  Unknown test type: $TEST_TYPE"
        echo "Running smoke test as fallback..."
        npx artillery run artillery/scenarios/homepage-minimal-test.yml \
            --config "$ENV_CONFIG" \
            --output artillery-results/smoke-results.json || true
        ;;
esac

echo ""
echo "✅ Artillery test execution completed"

