#!/bin/bash
# Smoke Test Runner
# Fast critical path verification in < 2 minutes

set -e

echo "🔥 Running Smoke Tests..."
echo "========================================"

# Configuration
GRID_SERVICE="selenium-hub"
NODE_SERVICE="chrome-node-1"
COMPOSE_FILE="docker-compose.yml"

# Start Grid (lightweight - just hub + 1 chrome node)
echo "📦 Starting Selenium Grid..."
docker-compose -f "$COMPOSE_FILE" up -d "$GRID_SERVICE" "$NODE_SERVICE"

if [ $? -ne 0 ]; then
    echo "❌ Failed to start Grid"
    exit 1
fi

# Wait for Grid to be ready
echo "⏳ Waiting for Grid..."
timeout 30 bash -c 'until curl -sf http://localhost:4444/wd/hub/status >/dev/null 2>&1; do sleep 2; done' || {
    echo "❌ Grid did not become ready in time"
    docker-compose logs "$GRID_SERVICE"
    docker-compose down
    exit 1
}

echo "✅ Grid is ready!"
echo ""

# Run smoke tests
echo "🧪 Running smoke tests..."
echo "Test suite: testng-smoke-suite.xml"
echo "Expected time: < 2 minutes"
echo ""

START_TIME=$(date +%s)

docker-compose run --rm tests -DsuiteXmlFile=testng-smoke-suite.xml

TEST_EXIT=$?
END_TIME=$(date +%s)
DURATION=$((END_TIME - START_TIME))

echo ""
echo "========================================"
echo "⏱️  Execution time: ${DURATION} seconds"

# Stop Grid
echo "🛑 Stopping Grid..."
docker-compose down

echo "========================================"

# Report results
if [ $TEST_EXIT -eq 0 ]; then
    echo "✅ All smoke tests PASSED! 🎉"
    echo ""
    echo "Next steps:"
    echo "  - Commit your changes"
    echo "  - Push to repository"
    echo "  - Run full test suite if needed"
    exit 0
else
    echo "❌ Smoke tests FAILED! 💥"
    echo ""
    echo "Action required:"
    echo "  - Check test output above"
    echo "  - Fix failing tests"
    echo "  - Re-run smoke tests"
    echo "  - DO NOT push until smoke tests pass"
    exit 1
fi

