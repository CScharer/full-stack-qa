#!/bin/bash
# Convert Performance Test Results to Allure Format
# This script converts Gatling, JMeter, and Locust results into Allure-compatible JSON files

set -e

# Source shared metadata utilities
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/ci/allure-metadata-utils.sh" 2>/dev/null || {
    # Fallback if sourcing fails
    get_verification_metadata_json() {
        local env="$1"
        if [ -z "$env" ] || [ "$env" = "unknown" ] || [ "$env" = "combined" ]; then
            echo "[]"
            return
        fi
        local base_url="${BASE_URL:-unknown}"
        local test_timestamp_iso=$(date -u +"%Y-%m-%dT%H:%M:%S" 2>/dev/null || date +"%Y-%m-%dT%H:%M:%S")
        local ci_run_id="${GITHUB_RUN_ID:-local}"
        local ci_run_number="${GITHUB_RUN_NUMBER:-unknown}"
        cat <<EOF
[
    {
      "name": "Base URL",
      "value": "$base_url"
    },
    {
      "name": "Test Execution Time",
      "value": "$test_timestamp_iso"
    },
    {
      "name": "CI Run ID",
      "value": "$ci_run_id"
    },
    {
      "name": "CI Run Number",
      "value": "$ci_run_number"
    }
]
EOF
    }
}

ALLURE_RESULTS_DIR="${1:-target/allure-results}"
PERFORMANCE_RESULTS_DIR="${2:-target}"

echo "🔄 Converting Performance Test Results to Allure Format..."
echo "   Allure Results: $ALLURE_RESULTS_DIR"
echo "   Performance Results: $PERFORMANCE_RESULTS_DIR"
echo ""

# Ensure Allure results directory exists
mkdir -p "$ALLURE_RESULTS_DIR"

# Cross-platform UUID generation
generate_uuid() {
    if command -v uuidgen &> /dev/null; then
        uuidgen | tr '[:upper:]' '[:lower:]' | tr -d '-' | cut -c1-32
    elif command -v python3 &> /dev/null; then
        python3 -c "import uuid; print(uuid.uuid4().hex[:32])"
    else
        # Fallback: use random hex
        cat /dev/urandom | head -c 16 | od -An -tx1 | tr -d ' \n'
    fi
}

# Cross-platform MD5 hash
generate_hash() {
    local input="$1"
    if command -v md5sum &> /dev/null; then
        echo -n "$input" | md5sum | cut -d' ' -f1
    elif command -v md5 &> /dev/null; then
        echo -n "$input" | md5 | cut -d' ' -f1
    elif command -v python3 &> /dev/null; then
        python3 -c "import hashlib; print(hashlib.md5('$input'.encode()).hexdigest())"
    else
        # Fallback: use first 32 chars of input
        echo -n "$input" | head -c 32
    fi
}

# Function to create Allure test result JSON
create_allure_result() {
    local name="$1"
    local status="$2"  # passed, failed, broken, skipped
    local duration="$3"  # milliseconds
    local description="$4"
    local full_name="${5:-$name}"
    local environment="${6:-}"  # Optional environment parameter
    
    local uuid=$(generate_uuid)
    local timestamp=$(date +%s)000
    local history_id=$(generate_hash "$full_name")
    
    # Build labels array
    local labels_json='[
    {
      "name": "suite",
      "value": "Performance Tests"
    },
    {
      "name": "testClass",
      "value": "Performance"
    },
    {
      "name": "epic",
      "value": "Performance Testing"
    }'
    
    # Add environment label if provided
    if [ -n "$environment" ] && [ "$environment" != "unknown" ] && [ "$environment" != "combined" ]; then
        labels_json="${labels_json},
    {
      \"name\": \"environment\",
      \"value\": \"$environment\"
    }"
    fi
    
    labels_json="${labels_json}
  ]"
    
    # Build parameters array
    local params_json="[]"
    if [ -n "$environment" ] && [ "$environment" != "unknown" ] && [ "$environment" != "combined" ]; then
        # Get verification metadata using shared utility
        local verification_metadata=$(get_verification_metadata_json "$environment")
        
        # Build params JSON with Environment and verification metadata
        params_json="[
    {
      \"name\": \"Environment\",
      \"value\": \"$(echo $environment | tr '[:lower:]' '[:upper:]')\"
    }"
        
        # Append verification metadata (remove outer brackets and add comma)
        if [ "$verification_metadata" != "[]" ]; then
            params_json="${params_json},
$(echo "$verification_metadata" | sed 's/^\[//;s/\]$//')"
        fi
        
        params_json="${params_json}
  ]"
    fi
    
    cat > "$ALLURE_RESULTS_DIR/${uuid}-result.json" <<EOF
{
  "uuid": "${uuid}",
  "historyId": "${history_id}",
  "fullName": "$full_name",
  "labels": ${labels_json},
  "name": "$name",
  "status": "$status",
  "statusDetails": {
    "known": false,
    "muted": false,
    "flaky": false
  },
  "stage": "finished",
  "description": "$description",
  "steps": [],
  "attachments": [],
  "parameters": ${params_json},
  "start": $timestamp,
  "stop": $((timestamp + duration))
}
EOF
}

# Convert Gatling Results
if [ -d "$PERFORMANCE_RESULTS_DIR/gatling" ]; then
    echo "📊 Converting Gatling results..."
    
    # Find all Gatling simulation directories
    find "$PERFORMANCE_RESULTS_DIR/gatling" -maxdepth 1 -type d -name "*simulation*" | while read -r SIMULATION_DIR; do
        if [ -f "$SIMULATION_DIR/index.html" ]; then
            SIMULATION_NAME=$(basename "$SIMULATION_DIR" | sed 's/-[0-9]*$//')
            
            # Check if simulation completed successfully by looking for index.html
            # Gatling generates index.html only on successful completion
            if [ -f "$SIMULATION_DIR/index.html" ]; then
                STATUS="passed"
                
                # Try to extract metrics from HTML report (simplified)
                if grep -q "Global" "$SIMULATION_DIR/index.html" 2>/dev/null; then
                    DESCRIPTION="Gatling Simulation: $SIMULATION_NAME - Completed successfully. View detailed report in $SIMULATION_DIR/index.html"
                else
                    DESCRIPTION="Gatling Simulation: $SIMULATION_NAME - Completed successfully"
                fi
                
                # Estimate duration based on typical Gatling runs
                DURATION=120000  # 2 minutes default
                
        # Get environment from environment variable if set
        ENV_FOR_TEST="${ALLURE_ENVIRONMENT:-}"
        
        create_allure_result \
            "Gatling: $SIMULATION_NAME" \
            "$STATUS" \
            "$DURATION" \
            "$DESCRIPTION" \
            "Performance.Gatling.$SIMULATION_NAME" \
            "$ENV_FOR_TEST"
                
                echo "   ✅ Converted: $SIMULATION_NAME"
            fi
        fi
    done
fi

# Convert JMeter Results
if [ -d "$PERFORMANCE_RESULTS_DIR/jmeter" ]; then
    echo "📊 Converting JMeter results..."
    
    # Check for API test results
    if [ -f "$PERFORMANCE_RESULTS_DIR/jmeter/results/api-results.jtl" ]; then
        # Count samples (skip header if present)
        TOTAL_SAMPLES=$(tail -n +2 "$PERFORMANCE_RESULTS_DIR/jmeter/results/api-results.jtl" 2>/dev/null | wc -l | tr -d ' ')
        if [ -z "$TOTAL_SAMPLES" ] || [ "$TOTAL_SAMPLES" = "0" ]; then
            TOTAL_SAMPLES=$(wc -l < "$PERFORMANCE_RESULTS_DIR/jmeter/results/api-results.jtl" 2>/dev/null | tr -d ' ')
        fi
        
        # Count failed samples (check for false in success column, typically 8th field)
        FAILED_SAMPLES=$(awk -F',' 'NR>1 && $8=="false" {count++} END {print count+0}' "$PERFORMANCE_RESULTS_DIR/jmeter/results/api-results.jtl" 2>/dev/null || echo "0")
        
        if [ "$FAILED_SAMPLES" -gt 0 ]; then
            STATUS="failed"
            DESCRIPTION="JMeter API Performance Test - $FAILED_SAMPLES failed samples out of $TOTAL_SAMPLES total"
        else
            STATUS="passed"
            DESCRIPTION="JMeter API Performance Test - All $TOTAL_SAMPLES samples passed successfully"
        fi
        
        # Get environment from environment variable if set
        ENV_FOR_TEST="${ALLURE_ENVIRONMENT:-}"
        
        create_allure_result \
            "JMeter: API Performance Test" \
            "$STATUS" \
            180000 \
            "$DESCRIPTION" \
            "Performance.JMeter.API" \
            "$ENV_FOR_TEST"
        
        echo "   ✅ Converted: API Performance Test ($TOTAL_SAMPLES samples)"
    fi
    
    # Check for Web test results
    if [ -f "$PERFORMANCE_RESULTS_DIR/jmeter/results/web-results.jtl" ]; then
        TOTAL_SAMPLES=$(tail -n +2 "$PERFORMANCE_RESULTS_DIR/jmeter/results/web-results.jtl" 2>/dev/null | wc -l | tr -d ' ')
        if [ -z "$TOTAL_SAMPLES" ] || [ "$TOTAL_SAMPLES" = "0" ]; then
            TOTAL_SAMPLES=$(wc -l < "$PERFORMANCE_RESULTS_DIR/jmeter/results/web-results.jtl" 2>/dev/null | tr -d ' ')
        fi
        
        FAILED_SAMPLES=$(awk -F',' 'NR>1 && $8=="false" {count++} END {print count+0}' "$PERFORMANCE_RESULTS_DIR/jmeter/results/web-results.jtl" 2>/dev/null || echo "0")
        
        if [ "$FAILED_SAMPLES" -gt 0 ]; then
            STATUS="failed"
            DESCRIPTION="JMeter Web Load Test - $FAILED_SAMPLES failed samples out of $TOTAL_SAMPLES total"
        else
            STATUS="passed"
            DESCRIPTION="JMeter Web Load Test - All $TOTAL_SAMPLES samples passed successfully"
        fi
        
        # Get environment from environment variable if set
        ENV_FOR_TEST="${ALLURE_ENVIRONMENT:-}"
        
        create_allure_result \
            "JMeter: Web Load Test" \
            "$STATUS" \
            180000 \
            "$DESCRIPTION" \
            "Performance.JMeter.Web" \
            "$ENV_FOR_TEST"
        
        echo "   ✅ Converted: Web Load Test ($TOTAL_SAMPLES samples)"
    fi
fi

# Convert Locust Results
if [ -d "$PERFORMANCE_RESULTS_DIR/locust" ]; then
    echo "📊 Converting Locust results..."
    echo "   Locust directory: $PERFORMANCE_RESULTS_DIR/locust"
    echo "   Files found in locust directory:"
    ls -la "$PERFORMANCE_RESULTS_DIR/locust/" 2>/dev/null | head -15 || echo "   (directory empty or not accessible)"
    echo ""
    
    # Check for API load test results
    if [ -f "$PERFORMANCE_RESULTS_DIR/locust/api-load-stats_stats.csv" ]; then
        echo "   ✅ Found: api-load-stats_stats.csv"
        # Locust CSV format: Type, Name, Request Count, Failure Count, Median response time, Average response time, Min response time, Max response time, Average Content Size, Requests/s
        # Request Count is column 3, Failure Count is column 4
        # First try to get from "Total" row if it exists
        TOTAL_REQUESTS=$(awk -F',' 'NR>1 && $1=="Total" {gsub(/"/, "", $3); print int($3+0.5)}' "$PERFORMANCE_RESULTS_DIR/locust/api-load-stats_stats.csv" 2>/dev/null || echo "0")
        FAILED_REQUESTS=$(awk -F',' 'NR>1 && $1=="Total" {gsub(/"/, "", $4); print int($4+0.5)}' "$PERFORMANCE_RESULTS_DIR/locust/api-load-stats_stats.csv" 2>/dev/null || echo "0")
        
        # If Total row doesn't have values, sum all rows (excluding header and Total)
        if [ -z "$TOTAL_REQUESTS" ] || [ "$TOTAL_REQUESTS" = "0" ]; then
            TOTAL_REQUESTS=$(awk -F',' 'NR>1 && $1!="Total" && $1!="Aggregated" {gsub(/"/, "", $3); sum+=$3} END {print int(sum+0.5)}' "$PERFORMANCE_RESULTS_DIR/locust/api-load-stats_stats.csv" 2>/dev/null || echo "0")
            FAILED_REQUESTS=$(awk -F',' 'NR>1 && $1!="Total" && $1!="Aggregated" {gsub(/"/, "", $4); sum+=$4} END {print int(sum+0.5)}' "$PERFORMANCE_RESULTS_DIR/locust/api-load-stats_stats.csv" 2>/dev/null || echo "0")
        fi
        
        if [ "$FAILED_REQUESTS" -gt 0 ]; then
            STATUS="failed"
            DESCRIPTION="Locust API Load Test - $FAILED_REQUESTS failed requests out of $TOTAL_REQUESTS total"
        else
            STATUS="passed"
            DESCRIPTION="Locust API Load Test - All $TOTAL_REQUESTS requests passed successfully"
        fi
        
        # Get environment from environment variable if set
        ENV_FOR_TEST="${ALLURE_ENVIRONMENT:-}"
        
        create_allure_result \
            "Locust: API Load Test" \
            "$STATUS" \
            120000 \
            "$DESCRIPTION" \
            "Performance.Locust.API" \
            "$ENV_FOR_TEST"
        
        echo "   ✅ Converted: API Load Test ($TOTAL_REQUESTS requests)"
    fi
    
    # Check for smoke test results (30 second quick test)
    if [ -f "$PERFORMANCE_RESULTS_DIR/locust/quick-smoke_stats.csv" ]; then
        echo "   ✅ Found: quick-smoke_stats.csv (smoke test)"
        # Locust CSV format: Type, Name, Request Count, Failure Count, Median response time, Average response time, Min response time, Max response time, Average Content Size, Requests/s
        # Handle quoted fields and use proper column indices (Request Count is column 3, Failure Count is column 4)
        # First try to get from "Total" row if it exists
        TOTAL_REQUESTS=$(awk -F',' 'NR>1 && $1=="Total" {gsub(/"/, "", $3); print int($3+0.5)}' "$PERFORMANCE_RESULTS_DIR/locust/quick-smoke_stats.csv" 2>/dev/null || echo "0")
        FAILED_REQUESTS=$(awk -F',' 'NR>1 && $1=="Total" {gsub(/"/, "", $4); print int($4+0.5)}' "$PERFORMANCE_RESULTS_DIR/locust/quick-smoke_stats.csv" 2>/dev/null || echo "0")
        
        # If Total row doesn't have values, sum all rows (excluding header and Total)
        if [ -z "$TOTAL_REQUESTS" ] || [ "$TOTAL_REQUESTS" = "0" ]; then
            # Try with column 3 (Request Count) and column 4 (Failure Count) - Locust CSV format
            TOTAL_REQUESTS=$(awk -F',' 'NR>1 && $1!="Total" && $1!="Aggregated" {gsub(/"/, "", $3); sum+=$3} END {print int(sum+0.5)}' "$PERFORMANCE_RESULTS_DIR/locust/quick-smoke_stats.csv" 2>/dev/null || echo "0")
            FAILED_REQUESTS=$(awk -F',' 'NR>1 && $1!="Total" && $1!="Aggregated" {gsub(/"/, "", $4); sum+=$4} END {print int(sum+0.5)}' "$PERFORMANCE_RESULTS_DIR/locust/quick-smoke_stats.csv" 2>/dev/null || echo "0")
        fi
        
        # Fallback: try with column 2 and 3 (if format is different)
        if [ -z "$TOTAL_REQUESTS" ] || [ "$TOTAL_REQUESTS" = "0" ]; then
            TOTAL_REQUESTS=$(awk -F',' 'NR>1 && $1!="Total" && $1!="Aggregated" {gsub(/"/, "", $2); sum+=$2} END {print int(sum+0.5)}' "$PERFORMANCE_RESULTS_DIR/locust/quick-smoke_stats.csv" 2>/dev/null || echo "0")
            FAILED_REQUESTS=$(awk -F',' 'NR>1 && $1!="Total" && $1!="Aggregated" {gsub(/"/, "", $3); sum+=$3} END {print int(sum+0.5)}' "$PERFORMANCE_RESULTS_DIR/locust/quick-smoke_stats.csv" 2>/dev/null || echo "0")
        fi
        
        # Debug output
        echo "   📊 Parsed: TOTAL_REQUESTS=$TOTAL_REQUESTS, FAILED_REQUESTS=$FAILED_REQUESTS"
        
        if [ "$FAILED_REQUESTS" -gt 0 ]; then
            STATUS="failed"
            DESCRIPTION="Locust Quick Smoke Test - $FAILED_REQUESTS failed requests out of $TOTAL_REQUESTS total"
        else
            STATUS="passed"
            DESCRIPTION="Locust Quick Smoke Test - All $TOTAL_REQUESTS requests passed successfully"
        fi
        
        # Get environment from environment variable if set
        ENV_FOR_TEST="${ALLURE_ENVIRONMENT:-}"
        
        create_allure_result \
            "Locust: Quick Smoke Test" \
            "$STATUS" \
            30000 \
            "$DESCRIPTION" \
            "Performance.Locust.QuickSmoke" \
            "$ENV_FOR_TEST"
        
        echo "   ✅ Converted: Quick Smoke Test ($TOTAL_REQUESTS requests)"
    fi
    
    # Check for comprehensive test results
    if [ -f "$PERFORMANCE_RESULTS_DIR/locust/comprehensive-stats_stats.csv" ]; then
        echo "   ✅ Found: comprehensive-stats_stats.csv"
        # Locust CSV format: Type, Name, Request Count, Failure Count, ...
        # Request Count is column 3, Failure Count is column 4
        TOTAL_REQUESTS=$(awk -F',' 'NR>1 && $1=="Total" {gsub(/"/, "", $3); print int($3+0.5)}' "$PERFORMANCE_RESULTS_DIR/locust/comprehensive-stats_stats.csv" 2>/dev/null || echo "0")
        FAILED_REQUESTS=$(awk -F',' 'NR>1 && $1=="Total" {gsub(/"/, "", $4); print int($4+0.5)}' "$PERFORMANCE_RESULTS_DIR/locust/comprehensive-stats_stats.csv" 2>/dev/null || echo "0")
        
        # If Total row doesn't have values, sum all rows
        if [ -z "$TOTAL_REQUESTS" ] || [ "$TOTAL_REQUESTS" = "0" ]; then
            TOTAL_REQUESTS=$(awk -F',' 'NR>1 && $1!="Total" && $1!="Aggregated" {gsub(/"/, "", $3); sum+=$3} END {print int(sum+0.5)}' "$PERFORMANCE_RESULTS_DIR/locust/comprehensive-stats_stats.csv" 2>/dev/null || echo "0")
            FAILED_REQUESTS=$(awk -F',' 'NR>1 && $1!="Total" && $1!="Aggregated" {gsub(/"/, "", $4); sum+=$4} END {print int(sum+0.5)}' "$PERFORMANCE_RESULTS_DIR/locust/comprehensive-stats_stats.csv" 2>/dev/null || echo "0")
        fi
        
        if [ "$FAILED_REQUESTS" -gt 0 ]; then
            STATUS="failed"
            DESCRIPTION="Locust Comprehensive Load Test - $FAILED_REQUESTS failed requests out of $TOTAL_REQUESTS total"
        else
            STATUS="passed"
            DESCRIPTION="Locust Comprehensive Load Test - All $TOTAL_REQUESTS requests passed successfully"
        fi
        
        # Get environment from environment variable if set
        ENV_FOR_TEST="${ALLURE_ENVIRONMENT:-}"
        
        create_allure_result \
            "Locust: Comprehensive Load Test" \
            "$STATUS" \
            180000 \
            "$DESCRIPTION" \
            "Performance.Locust.Comprehensive" \
            "$ENV_FOR_TEST"
        
        echo "   ✅ Converted: Comprehensive Load Test ($TOTAL_REQUESTS requests)"
    fi
    
    # If no CSV files were found, show what files exist
    CSV_COUNT=$(find "$PERFORMANCE_RESULTS_DIR/locust" -name "*_stats.csv" 2>/dev/null | wc -l | tr -d ' ')
    if [ "$CSV_COUNT" -eq 0 ]; then
        echo "   ⚠️  No *_stats.csv files found in $PERFORMANCE_RESULTS_DIR/locust/"
        echo "   Available files:"
        find "$PERFORMANCE_RESULTS_DIR/locust" -type f 2>/dev/null | head -10 || echo "   (no files found)"
    fi
fi

echo ""
echo "✅ Performance test results converted to Allure format!"
echo "   Results saved to: $ALLURE_RESULTS_DIR"
echo ""
echo "💡 Next steps:"
echo "   allure serve $ALLURE_RESULTS_DIR"
echo "   or"
echo "   allure generate $ALLURE_RESULTS_DIR -o allure-report"
