#!/bin/bash
# scripts/tests/performance/run-jmeter-tests.sh
# JMeter Performance Test Runner
#
# Purpose: Run Apache JMeter performance tests (25% of performance testing allocation)
#
# Usage:
#   ./scripts/tests/performance/run-jmeter-tests.sh
#
# Description:
#   This script runs JMeter load tests using JMeter CLI directly (not Maven plugin).
#   JMeter is an industry-standard performance testing tool supporting multiple protocols.
#   Test plans are located in src/test/jmeter/.
#
# Examples:
#   ./scripts/tests/performance/run-jmeter-tests.sh
#
# Dependencies:
#   - Java 21+
#   - Apache JMeter (downloaded automatically if missing)
#   - JMeter test plans in src/test/jmeter/
#
# Output:
#   - Test results in target/jmeter/
#   - HTML reports and CSV results
#   - Exit code: 0 on success, non-zero on failure
#
# Notes:
#   - Downloads JMeter automatically if not present
#   - Uses JMeter CLI (not Maven plugin)
#   - Supports HTTP, HTTPS, and other protocols
#   - 25% allocation of total performance testing
#
# Last Updated: January 2026

set -e

echo "📊 Running JMeter Performance Tests..."
echo "=========================================="

# Configuration
JMETER_VERSION="5.6.3"
JMETER_HOME="apache-jmeter-${JMETER_VERSION}"
JMETER_BIN="${JMETER_HOME}/bin/jmeter"
TEST_DIR="src/test/jmeter"
RESULTS_DIR="target/jmeter"

echo "🎯 Apache JMeter Load Testing"
echo "   - Industry-standard performance tool"
echo "   - Comprehensive protocols"
echo "   - Detailed reports"
echo ""

# Check if JMeter test files exist
if [ ! -d "$TEST_DIR" ]; then
    echo "❌ No JMeter test directory found"
    echo "   Expected: $TEST_DIR"
    exit 1
fi

echo "📋 Available Test Plans:"
find $TEST_DIR -name "*.jmx" -exec basename {} \; | sed 's/^/   - /'
echo ""

# Download and install JMeter if not present
if [ ! -f "$JMETER_BIN" ]; then
    echo "📥 Downloading Apache JMeter ${JMETER_VERSION}..."
    curl -O https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-${JMETER_VERSION}.tgz
    
    echo "📦 Extracting JMeter..."
    tar -xzf apache-jmeter-${JMETER_VERSION}.tgz
    
    echo "✅ JMeter installed"
    $JMETER_BIN --version
else
    echo "✅ JMeter already installed"
    $JMETER_BIN --version
fi

echo ""

# Clean previous results
echo "🧹 Cleaning previous results..."
rm -rf $RESULTS_DIR/results
rm -rf $RESULTS_DIR/reports
mkdir -p $RESULTS_DIR/results
mkdir -p $RESULTS_DIR/reports

# Run JMeter tests using CLI directly
echo "🚀 Executing JMeter test plans..."
echo ""

# Run API Performance Test
echo "1️⃣ Running API_Performance_Test.jmx..."
$JMETER_BIN -n \
    -JbaseUrl=http://localhost:8003 \
    -t $TEST_DIR/API_Performance_Test.jmx \
    -l $RESULTS_DIR/results/api-results.jtl \
    -e -o $RESULTS_DIR/reports/api \
    -j $RESULTS_DIR/api-jmeter.log

API_EXIT_CODE=$?
echo "   Exit code: $API_EXIT_CODE"
echo ""

# Run Web Load Test
echo "2️⃣ Running Web_Load_Test.jmx..."
$JMETER_BIN -n \
    -JbaseUrl=http://localhost:3003 \
    -t $TEST_DIR/Web_Load_Test.jmx \
    -l $RESULTS_DIR/results/web-results.jtl \
    -e -o $RESULTS_DIR/reports/web \
    -j $RESULTS_DIR/web-jmeter.log

WEB_EXIT_CODE=$?
echo "   Exit code: $WEB_EXIT_CODE"
echo ""

JMETER_EXIT_CODE=$((API_EXIT_CODE + WEB_EXIT_CODE))

echo ""
echo "=========================================="
if [ $JMETER_EXIT_CODE -eq 0 ]; then
    echo "✅ JMeter Tests COMPLETED"
else
    echo "⚠️  JMeter Tests completed with errors"
fi
echo "=========================================="

# Display results
echo ""
echo "📊 JMeter Results:"
echo ""

if [ -f "$RESULTS_DIR/reports/api/index.html" ]; then
    echo "✅ API Performance Test Report:"
    echo "   Dashboard: $RESULTS_DIR/reports/api/index.html"
    echo "   Results: $RESULTS_DIR/results/api-results.jtl"
else
    echo "❌ API Performance Test: No report generated"
fi

echo ""

if [ -f "$RESULTS_DIR/reports/web/index.html" ]; then
    echo "✅ Web Load Test Report:"
    echo "   Dashboard: $RESULTS_DIR/reports/web/index.html"
    echo "   Results: $RESULTS_DIR/results/web-results.jtl"
else
    echo "❌ Web Load Test: No report generated"
fi

echo ""
echo "📈 Performance Metrics Available:"
echo "   - Response time statistics"
echo "   - Throughput (requests/sec)"
echo "   - Error percentage"
echo "   - Latency graphs"
echo "   - Transaction reports"

echo ""
echo "💡 Open Reports in Browser:"
if [[ "$OSTYPE" == "darwin"* ]]; then
    echo "   open $RESULTS_DIR/reports/api/index.html"
    echo "   open $RESULTS_DIR/reports/web/index.html"
    echo ""
    
    read -p "Open API report now? (y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        open "$RESULTS_DIR/reports/api/index.html"
        open "$RESULTS_DIR/reports/web/index.html"
    fi
else
    echo "   xdg-open $RESULTS_DIR/reports/api/index.html"
    echo "   xdg-open $RESULTS_DIR/reports/web/index.html"
fi

echo ""
echo "📁 All Results:"
echo "   - API Reports: $RESULTS_DIR/reports/api/"
echo "   - Web Reports: $RESULTS_DIR/reports/web/"
echo "   - Raw Data: $RESULTS_DIR/results/"

exit $JMETER_EXIT_CODE

