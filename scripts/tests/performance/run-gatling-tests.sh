#!/bin/bash
# Gatling Performance Test Runner
# 30% of performance testing allocation

set -e

echo "⚡ Running Gatling Performance Tests..."
echo "=========================================="

# Configuration
GATLING_VERSION="3.10.3"
RESULTS_DIR="target/gatling"

echo "🎯 Gatling Load Testing Framework"
echo "   - Scala-based performance testing"
echo "   - Beautiful HTML reports"
echo "   - Real-time metrics"
echo ""

# Check if Scala files exist
if [ ! -d "src/test/scala" ]; then
    echo "❌ No Scala test directory found"
    echo "   Expected: src/test/scala/"
    exit 1
fi

echo "📋 Available Simulations:"
find src/test/scala -name "*.scala" -exec basename {} \; | sed 's/^/   - /'
echo ""

# Run Gatling tests
echo "🚀 Executing Gatling simulations..."
echo ""

./mvnw gatling:test -Pgatling

GATLING_EXIT_CODE=$?

echo ""
echo "=========================================="
if [ $GATLING_EXIT_CODE -eq 0 ]; then
    echo "✅ Gatling Tests COMPLETED"
else
    echo "⚠️  Gatling Tests completed with errors"
fi
echo "=========================================="

# Display results location
echo ""
echo "📊 Gatling Reports:"
if [ -d "$RESULTS_DIR" ]; then
    LATEST_REPORT=$(find $RESULTS_DIR -name "index.html" | sort -r | head -1)
    if [ -n "$LATEST_REPORT" ]; then
        echo "   Latest Report: $LATEST_REPORT"
        echo ""
        echo "💡 Open in browser:"
        echo "   open $LATEST_REPORT  (macOS)"
        echo "   xdg-open $LATEST_REPORT  (Linux)"
        echo ""
        
        # Auto-open on macOS
        if [[ "$OSTYPE" == "darwin"* ]]; then
            read -p "Open report now? (y/n) " -n 1 -r
            echo
            if [[ $REPLY =~ ^[Yy]$ ]]; then
                open "$LATEST_REPORT"
            fi
        fi
    else
        echo "   No report found in $RESULTS_DIR"
    fi
else
    echo "   Results directory not found"
fi

echo ""
echo "📈 Performance Metrics:"
echo "   - Response times (min/max/avg/percentiles)"
echo "   - Requests per second"
echo "   - Success/failure rates"
echo "   - User concurrency graphs"

exit $GATLING_EXIT_CODE

