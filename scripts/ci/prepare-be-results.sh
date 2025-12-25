#!/bin/bash
# scripts/ci/prepare-be-results.sh
# Prepares BE test results for Allure conversion by copying them to expected locations

set -e

BE_RESULTS_DIR="${1:-be-results}"
OUTPUT_DIR="${2:-allure-be-results}"

echo "🔄 Converting BE test results to Allure format..."
echo "   BE_RESULTS_DIR: $BE_RESULTS_DIR"
echo "   OUTPUT_DIR: $OUTPUT_DIR"
echo "   Checking BE_RESULTS_DIR structure:"
ls -la "$BE_RESULTS_DIR" 2>/dev/null | head -20 || echo "   (BE_RESULTS_DIR not found or empty)"

# Detect environment from BE_RESULTS_DIR structure
# BE results are downloaded as: locust-be-results-dev, locust-be-results-test, etc.
DETECTED_ENV="unknown"
if echo "$BE_RESULTS_DIR" | grep -qiE "-be-results-dev[/-]"; then
    DETECTED_ENV="dev"
elif echo "$BE_RESULTS_DIR" | grep -qiE "-be-results-test[/-]"; then
    DETECTED_ENV="test"
elif echo "$BE_RESULTS_DIR" | grep -qiE "-be-results-prod[/-]"; then
    DETECTED_ENV="prod"
else
    # Check subdirectories for environment indicators
    for subdir in "$BE_RESULTS_DIR"/*; do
        if [ -d "$subdir" ]; then
            subdir_name=$(basename "$subdir")
            if echo "$subdir_name" | grep -qiE "-be-results-dev"; then
                DETECTED_ENV="dev"
                break
            elif echo "$subdir_name" | grep -qiE "-be-results-test"; then
                DETECTED_ENV="test"
                break
            elif echo "$subdir_name" | grep -qiE "-be-results-prod"; then
                DETECTED_ENV="prod"
                break
            fi
        fi
    done
fi

echo "   🔍 Detected environment: $DETECTED_ENV"
export DETECTED_ENV

# Ensure converter script is executable
chmod +x scripts/convert-performance-to-allure.sh || true

# Create output directory
mkdir -p "$OUTPUT_DIR"

# Copy downloaded results to expected locations for converter script
mkdir -p target/gatling target/jmeter target/locust

# Handle dev environment results
if [ -d "$BE_RESULTS_DIR/gatling-be-results-dev" ]; then
  echo "📊 Processing Gatling results (DEV) - nested structure..."
  cp -r "$BE_RESULTS_DIR/gatling-be-results-dev"/* target/gatling/ 2>/dev/null || true
  echo "   Files copied to target/gatling/: $(ls -1 target/gatling/ 2>/dev/null | wc -l | tr -d ' ')"
  # Set environment for this conversion run
  if [ "$DETECTED_ENV" = "unknown" ]; then
    DETECTED_ENV="dev"
    export ALLURE_ENVIRONMENT="dev"
  fi
elif find "$BE_RESULTS_DIR" -maxdepth 1 -type f \( -name "*.log" -o -name "*.html" \) -path "*/gatling*" 2>/dev/null | grep -q .; then
  echo "📊 Processing Gatling results (DEV) - flattened structure..."
  find "$BE_RESULTS_DIR" -maxdepth 1 -type f \( -name "*.log" -o -name "*.html" -o -name "*.json" \) -path "*/gatling*" -exec cp {} target/gatling/ \; 2>/dev/null || true
  echo "   Files copied to target/gatling/: $(ls -1 target/gatling/ 2>/dev/null | wc -l | tr -d ' ')"
fi
if [ -d "$BE_RESULTS_DIR/jmeter-be-results-dev" ]; then
  echo "📊 Processing JMeter results (DEV) - nested structure..."
  cp -r "$BE_RESULTS_DIR/jmeter-be-results-dev"/* target/jmeter/ 2>/dev/null || true
  echo "   Files copied to target/jmeter/: $(ls -1 target/jmeter/ 2>/dev/null | wc -l | tr -d ' ')"
elif find "$BE_RESULTS_DIR" -maxdepth 1 -type f \( -name "*.jtl" -o -name "*.csv" \) -path "*/jmeter*" 2>/dev/null | grep -q .; then
  echo "📊 Processing JMeter results (DEV) - flattened structure..."
  find "$BE_RESULTS_DIR" -maxdepth 1 -type f \( -name "*.jtl" -o -name "*.csv" \) -path "*/jmeter*" -exec cp {} target/jmeter/ \; 2>/dev/null || true
  echo "   Files copied to target/jmeter/: $(ls -1 target/jmeter/ 2>/dev/null | wc -l | tr -d ' ')"
fi
if [ -d "$BE_RESULTS_DIR/locust-be-results-dev" ]; then
  echo "📊 Processing Locust results (DEV) - nested structure..."
  echo "   Source directory: $BE_RESULTS_DIR/locust-be-results-dev"
  echo "   Source directory exists: $(test -d "$BE_RESULTS_DIR/locust-be-results-dev" && echo 'yes' || echo 'no')"
  echo "   Source directory contents:"
  ls -la "$BE_RESULTS_DIR/locust-be-results-dev/" 2>/dev/null | head -15 || echo "   (directory empty or not found)"
  echo "   Recursive find of CSV files:"
  find "$BE_RESULTS_DIR/locust-be-results-dev" -name "*.csv" 2>/dev/null | head -10 || echo "   (no CSV files found)"
  echo "   Copying files..."
  cp -r "$BE_RESULTS_DIR/locust-be-results-dev"/* target/locust/ 2>&1 || echo "   Copy failed or directory empty"
  echo "   Files copied to target/locust/:"
  ls -la target/locust/ 2>/dev/null | head -15 || echo "   (target/locust/ is empty)"
  echo "   CSV files in target/locust/:"
  find target/locust -name "*.csv" 2>/dev/null | head -10 || echo "   (no CSV files found)"
  # Set environment for this conversion run
  if [ "$DETECTED_ENV" = "unknown" ]; then
    DETECTED_ENV="dev"
    export ALLURE_ENVIRONMENT="dev"
  fi
elif find "$BE_RESULTS_DIR" -maxdepth 1 -type f -name "*_stats.csv" 2>/dev/null | grep -q .; then
  echo "📊 Processing Locust results (DEV) - flattened structure..."
  echo "   Found Locust CSV files directly in $BE_RESULTS_DIR"
  echo "   Locust CSV files found:"
  find "$BE_RESULTS_DIR" -maxdepth 1 -type f -name "*_stats.csv" 2>/dev/null | head -10 || echo "   (no CSV files found)"
  # Try to detect environment from file location or check for dev-specific files
  if find "$BE_RESULTS_DIR" -maxdepth 1 -type d -name "*locust-be-results-dev*" 2>/dev/null | grep -q .; then
    DETECTED_ENV="dev"
    export ALLURE_ENVIRONMENT="dev"
  fi
  echo "   Copying Locust CSV files..."
  find "$BE_RESULTS_DIR" -maxdepth 1 -type f \( -name "*_stats.csv" -o -name "*_failures.csv" -o -name "*_exceptions.csv" -o -name "*.html" \) -exec cp {} target/locust/ \; 2>/dev/null || true
  echo "   Files copied to target/locust/:"
  ls -la target/locust/ 2>/dev/null | head -15 || echo "   (target/locust/ is empty)"
  echo "   CSV files in target/locust/:"
  find target/locust -name "*.csv" 2>/dev/null | head -10 || echo "   (no CSV files found)"
fi

# Handle test environment results (merge with dev if both exist)
if [ -d "$BE_RESULTS_DIR/gatling-be-results-test" ]; then
  echo "📊 Processing Gatling results (TEST) - nested structure..."
  cp -r "$BE_RESULTS_DIR/gatling-be-results-test"/* target/gatling/ 2>/dev/null || true
  echo "   Files in target/gatling/: $(ls -1 target/gatling/ 2>/dev/null | wc -l | tr -d ' ')"
elif find "$BE_RESULTS_DIR" -maxdepth 1 -type f \( -name "*.log" -o -name "*.html" \) -path "*/gatling*" 2>/dev/null | grep -q .; then
  echo "📊 Processing Gatling results (TEST) - flattened structure..."
  find "$BE_RESULTS_DIR" -maxdepth 1 -type f \( -name "*.log" -o -name "*.html" -o -name "*.json" \) -path "*/gatling*" -exec cp {} target/gatling/ \; 2>/dev/null || true
  echo "   Files in target/gatling/: $(ls -1 target/gatling/ 2>/dev/null | wc -l | tr -d ' ')"
fi
if [ -d "$BE_RESULTS_DIR/jmeter-be-results-test" ]; then
  echo "📊 Processing JMeter results (TEST) - nested structure..."
  cp -r "$BE_RESULTS_DIR/jmeter-be-results-test"/* target/jmeter/ 2>/dev/null || true
  echo "   Files in target/jmeter/: $(ls -1 target/jmeter/ 2>/dev/null | wc -l | tr -d ' ')"
elif find "$BE_RESULTS_DIR" -maxdepth 1 -type f \( -name "*.jtl" -o -name "*.csv" \) -path "*/jmeter*" 2>/dev/null | grep -q .; then
  echo "📊 Processing JMeter results (TEST) - flattened structure..."
  find "$BE_RESULTS_DIR" -maxdepth 1 -type f \( -name "*.jtl" -o -name "*.csv" \) -path "*/jmeter*" -exec cp {} target/jmeter/ \; 2>/dev/null || true
  echo "   Files in target/jmeter/: $(ls -1 target/jmeter/ 2>/dev/null | wc -l | tr -d ' ')"
fi
if [ -d "$BE_RESULTS_DIR/locust-be-results-test" ]; then
  echo "📊 Processing Locust results (TEST) - nested structure..."
  echo "   Source directory: $BE_RESULTS_DIR/locust-be-results-test"
  echo "   Source directory exists: $(test -d "$BE_RESULTS_DIR/locust-be-results-test" && echo 'yes' || echo 'no')"
  echo "   Source directory contents:"
  ls -la "$BE_RESULTS_DIR/locust-be-results-test/" 2>/dev/null | head -15 || echo "   (directory empty or not found)"
  echo "   Recursive find of CSV files:"
  find "$BE_RESULTS_DIR/locust-be-results-test" -name "*.csv" 2>/dev/null | head -10 || echo "   (no CSV files found)"
  echo "   Copying files..."
  cp -r "$BE_RESULTS_DIR/locust-be-results-test"/* target/locust/ 2>&1 || echo "   Copy failed or directory empty"
  echo "   Files in target/locust/ after merge:"
  ls -la target/locust/ 2>/dev/null | head -15 || echo "   (target/locust/ is empty)"
  echo "   CSV files in target/locust/ after merge:"
  find target/locust -name "*.csv" 2>/dev/null | head -10 || echo "   (no CSV files found)"
  # Update environment if test is found (but keep dev if both exist - we'll handle multi-env later)
  if [ "$DETECTED_ENV" = "unknown" ] || [ "$DETECTED_ENV" = "dev" ]; then
    # If we already have dev, we can't determine which environment the results are from
    # For now, if both exist, we'll use the first one detected
    if [ "$DETECTED_ENV" = "unknown" ]; then
      DETECTED_ENV="test"
      export ALLURE_ENVIRONMENT="test"
    fi
  fi
elif find "$BE_RESULTS_DIR" -maxdepth 1 -type f -name "*_stats.csv" 2>/dev/null | grep -q .; then
  echo "📊 Processing Locust results (TEST) - flattened structure..."
  echo "   Found Locust CSV files directly in $BE_RESULTS_DIR"
  echo "   Locust CSV files found:"
  find "$BE_RESULTS_DIR" -maxdepth 1 -type f -name "*_stats.csv" 2>/dev/null | head -10 || echo "   (no CSV files found)"
  # Try to detect environment from file location
  if find "$BE_RESULTS_DIR" -maxdepth 1 -type d -name "*locust-be-results-test*" 2>/dev/null | grep -q .; then
    if [ "$DETECTED_ENV" = "unknown" ]; then
      DETECTED_ENV="test"
      export ALLURE_ENVIRONMENT="test"
    fi
  fi
  echo "   Copying Locust CSV files (merging with existing)..."
  find "$BE_RESULTS_DIR" -maxdepth 1 -type f \( -name "*_stats.csv" -o -name "*_failures.csv" -o -name "*_exceptions.csv" -o -name "*.html" \) -exec cp {} target/locust/ \; 2>/dev/null || true
  echo "   Files in target/locust/ after merge:"
  ls -la target/locust/ 2>/dev/null | head -15 || echo "   (target/locust/ is empty)"
  echo "   CSV files in target/locust/ after merge:"
  find target/locust -name "*.csv" 2>/dev/null | head -10 || echo "   (no CSV files found)"
fi

# Run conversion script, allow it to fail gracefully
# Pass environment information if detected (use the last detected environment)
if [ "$DETECTED_ENV" != "unknown" ]; then
  export ALLURE_ENVIRONMENT="$DETECTED_ENV"
  echo "   📝 Passing environment '$DETECTED_ENV' to conversion script"
else
  echo "   ⚠️  Could not detect environment from BE results structure"
  echo "   📝 BE tests will be labeled as 'combined' if environment cannot be determined"
fi

./scripts/convert-performance-to-allure.sh "$OUTPUT_DIR" target || {
  echo "⚠️ Conversion script returned non-zero exit code, but continuing..."
}

echo "✅ Conversion complete!"
RESULT_COUNT=$(find "$OUTPUT_DIR" -name "*-result.json" 2>/dev/null | wc -l | tr -d ' ')
echo "  Allure results: $RESULT_COUNT"

# Output to GITHUB_OUTPUT if available
if [ -n "$GITHUB_OUTPUT" ]; then
  if [ "$RESULT_COUNT" -gt 0 ]; then
    echo "has_be_results=true" >> $GITHUB_OUTPUT
  else
    echo "has_be_results=false" >> $GITHUB_OUTPUT
  fi
fi
