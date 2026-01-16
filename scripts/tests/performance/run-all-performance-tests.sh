#!/bin/bash
# scripts/tests/performance/run-all-performance-tests.sh
# Master Performance Test Runner
#
# Purpose: Run all performance testing tools in sequence (Locust, Gatling, JMeter)
#
# Usage:
#   ./scripts/tests/performance/run-all-performance-tests.sh
#
# Description:
#   This script runs all protocol-level performance testing tools:
#   - Locust: 30% allocation (Python-based, real-time UI)
#   - Gatling: 25% allocation (Scala-based, detailed reports)
#   - JMeter: 25% allocation (Java-based, industry standard)
#
#   Note: Artillery + Playwright (20% - Browser-level) runs separately
#         See: cd playwright && npm run load:test:homepage
#
# Examples:
#   ./scripts/tests/performance/run-all-performance-tests.sh
#
# Dependencies:
#   - Python 3.13+ (for Locust)
#   - Java 21+ (for Gatling, JMeter)
#   - Maven wrapper (./mvnw)
#   - Gatling Maven plugin
#   - JMeter (installed or downloaded)
#   - Locust Python package
#
# Output:
#   - Locust results in target/locust/
#   - Gatling results in target/gatling/
#   - JMeter results in target/jmeter/
#   - Exit code: 0 on success, non-zero on failure
#
# Notes:
#   - Tests run in sequence (not parallel)
#   - Each tool generates its own reports
#   - Browser-level testing (Artillery) is separate
#
# Last Updated: January 2026

set -e

echo "🚀 COMPREHENSIVE PERFORMANCE TESTING"
echo "======================================================================"
echo ""
echo "Test Allocation (Protocol-Level Tools):"
echo "   - Locust:  30% (Python-based, real-time UI)"
echo "   - Gatling: 25% (Scala-based, detailed reports)"
echo "   - JMeter:  25% (Java-based, industry standard)"
echo ""
echo "Note: Artillery + Playwright (20% - Browser-level) runs separately"
echo "      See: cd playwright && npm run load:test:homepage"
echo ""
echo "======================================================================"
echo ""

# Track overall results
OVERALL_RESULT=0

# Step 1: Locust (30%) - Protocol-level API testing
echo ""
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║  STEP 1/3: LOCUST TESTS (30%)                                  ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

if command -v locust &> /dev/null; then
    if [ -f "scripts/run-locust-tests.sh" ]; then
        chmod +x scripts/run-locust-tests.sh
        
        # Run in headless mode for automation
        locust -f src/test/locust/comprehensive_load_test.py \
               --headless \
               --users 100 \
               --spawn-rate 10 \
               --run-time 2m \
               --html target/locust/report.html \
               --csv target/locust/stats || OVERALL_RESULT=1
    else
        echo "⚠️  Locust script not found - skipping"
        OVERALL_RESULT=1
    fi
else
    echo "⚠️  Locust not installed - skipping"
    echo "   Install: pip install -r requirements.txt"
    OVERALL_RESULT=1
fi

# Step 2: Gatling (25%)
echo ""
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║  STEP 2/3: GATLING TESTS (25%)                                 ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

if [ -d "src/test/scala" ]; then
    ./mvnw gatling:test -Pgatling || OVERALL_RESULT=1
else
    echo "⚠️  No Gatling tests found - skipping"
    OVERALL_RESULT=1
fi

# Step 3: JMeter (25%)
echo ""
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║  STEP 3/3: JMETER TESTS (25%)                                  ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

if [ -f "scripts/run-jmeter-tests.sh" ]; then
    chmod +x scripts/run-jmeter-tests.sh
    ./scripts/run-jmeter-tests.sh || OVERALL_RESULT=1
else
    echo "⚠️  JMeter script not found - skipping"
    OVERALL_RESULT=1
fi

# Summary
echo ""
echo "======================================================================"
echo "📊 PERFORMANCE TESTING SUMMARY"
echo "======================================================================"
echo ""

echo "Results Locations:"
echo "   Locust:  target/locust/report.html"
echo "   Gatling: target/gatling/*/index.html"
echo "   JMeter API:  target/jmeter/reports/api/index.html"
echo "   JMeter Web:  target/jmeter/reports/web/index.html"
echo "   Artillery:  playwright/artillery-results/*.json (run separately)"
echo ""

echo "Metrics Collected:"
echo "   ✅ Response times (min/max/avg/p95/p99)"
echo "   ✅ Throughput (requests per second)"
echo "   ✅ Error rates"
echo "   ✅ Concurrent users"
echo "   ✅ Resource utilization"
echo ""

if [ $OVERALL_RESULT -eq 0 ]; then
    echo "🎉 ALL PERFORMANCE TESTS COMPLETED SUCCESSFULLY!"
else
    echo "⚠️  Some performance tests completed with errors"
    echo "   Check individual tool outputs above"
fi

echo ""
echo "======================================================================"

exit $OVERALL_RESULT

