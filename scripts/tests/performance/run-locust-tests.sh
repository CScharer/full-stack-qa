#!/bin/bash
# Locust Performance Test Runner
# 40% of performance testing allocation (primary tool)

set -e

echo "🐝 Running Locust Performance Tests..."
echo "=========================================="

# Configuration
LOCUST_DIR="src/test/locust"
RESULTS_DIR="target/locust"
DEFAULT_USERS=100
DEFAULT_SPAWN_RATE=10
DEFAULT_RUNTIME="3m"

echo "🎯 Locust Performance Testing Framework"
echo "   - Python-based load testing"
echo "   - Real-time web UI"
echo "   - Flexible and scriptable"
echo "   - 40% of performance test allocation"
echo ""

# Check if Locust is installed
if ! command -v locust &> /dev/null; then
    echo "⚠️  Locust not installed"
    echo ""
    echo "📦 Install Locust:"
    echo "   pip install -r requirements.txt"
    echo ""
    echo "Or:"
    echo "   pip install locust==2.20.0"
    echo ""
    exit 1
fi

echo "✅ Locust version: $(locust --version)"
echo ""

# Check if test files exist
if [ ! -d "$LOCUST_DIR" ]; then
    echo "❌ No Locust test directory found"
    echo "   Expected: $LOCUST_DIR"
    exit 1
fi

echo "📋 Available Test Files:"
find $LOCUST_DIR -name "*.py" -exec basename {} \; | sed 's/^/   - /'
echo ""

# Prepare results directory
mkdir -p $RESULTS_DIR

# Interactive or headless mode
echo "🔧 Execution Mode:"
echo "   1) Web UI (interactive, recommended)"
echo "   2) Headless (automated, CI/CD)"
echo ""

read -p "Select mode (1/2) [default: 1]: " -n 1 -r MODE
echo ""

if [[ $MODE == "2" ]]; then
    # Headless mode
    echo ""
    echo "🤖 Running in HEADLESS mode..."
    echo ""
    
    # Ask which test to run
    echo "Select test file:"
    echo "   1) api_load_test.py (API focus)"
    echo "   2) web_load_test.py (Web focus)"
    echo "   3) comprehensive_load_test.py (Complete)"
    echo ""
    
    read -p "Select test (1/2/3) [default: 3]: " -n 1 -r TEST_CHOICE
    echo ""
    
    case $TEST_CHOICE in
        1)
            TEST_FILE="api_load_test.py"
            export BASE_URL="http://localhost:8003"
            ;;
        2)
            TEST_FILE="web_load_test.py"
            export BASE_URL="http://localhost:3003"
            ;;
        *)
            TEST_FILE="comprehensive_load_test.py"
            export BASE_URL="http://localhost:8003"
            ;;
    esac
    
    echo "📊 Test Configuration:"
    echo "   File: $TEST_FILE"
    echo "   Users: $DEFAULT_USERS"
    echo "   Spawn Rate: $DEFAULT_SPAWN_RATE/sec"
    echo "   Duration: $DEFAULT_RUNTIME"
    echo ""
    
    locust -f "$LOCUST_DIR/$TEST_FILE" \
           --headless \
           --users $DEFAULT_USERS \
           --spawn-rate $DEFAULT_SPAWN_RATE \
           --run-time $DEFAULT_RUNTIME \
           --html "$RESULTS_DIR/report.html" \
           --csv "$RESULTS_DIR/stats"
    
    LOCUST_EXIT_CODE=$?
    
    echo ""
    echo "=========================================="
    if [ $LOCUST_EXIT_CODE -eq 0 ]; then
        echo "✅ Locust Tests COMPLETED"
    else
        echo "⚠️  Locust Tests completed with errors"
    fi
    echo "=========================================="
    
    echo ""
    echo "📊 Results saved:"
    echo "   - HTML Report: $RESULTS_DIR/report.html"
    echo "   - CSV Stats: $RESULTS_DIR/stats*.csv"
    echo ""
    
    # Auto-open report on macOS
    if [[ "$OSTYPE" == "darwin"* ]] && [ -f "$RESULTS_DIR/report.html" ]; then
        read -p "Open HTML report? (y/n) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            open "$RESULTS_DIR/report.html"
        fi
    fi
    
    exit $LOCUST_EXIT_CODE
    
else
    # Web UI mode (default)
    echo ""
    echo "🌐 Starting Locust Web UI..."
    echo ""
    echo "Select test file:"
    echo "   1) api_load_test.py"
    echo "   2) web_load_test.py"
    echo "   3) comprehensive_load_test.py"
    echo ""
    
    read -p "Select test (1/2/3) [default: 1]: " -n 1 -r TEST_CHOICE
    echo ""
    
    case $TEST_CHOICE in
        2)
            TEST_FILE="web_load_test.py"
            export BASE_URL="http://localhost:3003"
            ;;
        3)
            TEST_FILE="comprehensive_load_test.py"
            export BASE_URL="http://localhost:8003"
            ;;
        *)
            TEST_FILE="api_load_test.py"
            export BASE_URL="http://localhost:8003"
            ;;
    esac
    
    echo "📊 Starting: $TEST_FILE"
    echo ""
    echo "✅ Web UI will open at: http://localhost:8089"
    echo ""
    echo "Configure your test:"
    echo "   - Number of users (e.g., 100)"
    echo "   - Spawn rate (e.g., 10 users/sec)"
    echo "   - Host URL (pre-configured in test)"
    echo ""
    echo "Press Ctrl+C to stop the test"
    echo ""
    echo "=========================================="
    
    locust -f "$LOCUST_DIR/$TEST_FILE"
fi

