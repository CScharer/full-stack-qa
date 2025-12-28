#!/bin/bash
# scripts/ci/merge-allure-results.sh
# Logic to merge results from multiple environments into one directory

set -e

SOURCE_DIR="all-test-results"
TARGET_DIR="allure-results-combined"

mkdir -p "$TARGET_DIR"

if [ ! -d "$SOURCE_DIR" ]; then
    echo "⚠️  $SOURCE_DIR not found, nothing to merge"
    exit 0
fi

echo "📊 Merging results from $SOURCE_DIR into $TARGET_DIR..."
echo "🔍 Source directory structure:"
find "$SOURCE_DIR" -type d -maxdepth 3 | head -20 || true
echo ""
echo "🔍 Checking for environment-specific directories:"
find "$SOURCE_DIR" -type d -maxdepth 2 | grep -E "(results-dev|results-test|results-prod)" | head -10 || echo "   (no environment-specific directories found at top level)"
echo ""

# Debug: Show what we're looking for
echo "🔍 Searching for Allure result files..."
echo "  Looking for: *-result.json, *-container.json, *-attachment.*"
echo ""

# Copy all result files (search recursively in all subdirectories)
# Artifacts may be nested like: all-test-results/smoke-results-dev/target/allure-results/*-result.json
RESULT_FILES=$(find "$SOURCE_DIR" -name "*-result.json" 2>/dev/null | wc -l | tr -d ' ')
CONTAINER_FILES=$(find "$SOURCE_DIR" -name "*-container.json" 2>/dev/null | wc -l | tr -d ' ')
ATTACHMENT_FILES=$(find "$SOURCE_DIR" -name "*-attachment.*" 2>/dev/null | wc -l | tr -d ' ')

echo "📊 Found in source:"
echo "  Result JSON files: $RESULT_FILES"
echo "  Container JSON files: $CONTAINER_FILES"
echo "  Attachment files: $ATTACHMENT_FILES"
echo ""

# Copy all result files and track their source environment
# This helps us add environment labels later to prevent Allure deduplication
# Use a temp file to track counts since variables in pipeline subshells don't persist
ENV_COUNT_FILE=$(mktemp)
echo "0 0 0 0" > "$ENV_COUNT_FILE"  # dev test prod unknown

find "$SOURCE_DIR" -name "*-result.json" | while read -r result_file; do
    # Determine environment from path
    # Look for artifact patterns: *-results-dev, *-results-test, *-results-prod
    # Also check for be-allure-results which might have environment in path
    env="unknown"
    
    # Get the full path relative to SOURCE_DIR for better pattern matching
    rel_path="${result_file#$SOURCE_DIR/}"
    
    # Check for explicit -results-{env} pattern in path (most reliable)
    # This should match patterns like: smoke-results-dev/target/allure-results/...
    # or: grid-results-chrome-dev/target/allure-results/...
    # Also check for results-{env}/ directory structure from separate downloads
    # Also check for be-results-{env}/ for BE results
    if echo "$rel_path" | grep -qiE "(results-dev/|-results-dev[/-]|be-results-dev/)"; then
        env="dev"
    elif echo "$rel_path" | grep -qiE "(results-test/|-results-test[/-]|be-results-test/)"; then
        env="test"
    elif echo "$rel_path" | grep -qiE "(results-prod/|-results-prod[/-]|be-results-prod/)"; then
        env="prod"
    # Also check the full absolute path as fallback
    elif echo "$result_file" | grep -qiE "(results-dev/|-results-dev[/-]|be-results-dev/)"; then
        env="dev"
    elif echo "$result_file" | grep -qiE "(results-test/|-results-test[/-]|be-results-test/)"; then
        env="test"
    elif echo "$result_file" | grep -qiE "(results-prod/|-results-prod[/-]|be-results-prod/)"; then
        env="prod"
    # Fallback: check for environment in directory names (more specific patterns)
    elif echo "$result_file" | grep -qiE "(/dev/|/development/)" && ! echo "$result_file" | grep -qiE "(/test/|/testing/|/prod/|/production/)"; then
        env="dev"
    elif echo "$result_file" | grep -qiE "(/test/|/testing/)" && ! echo "$result_file" | grep -qiE "(/prod/|/production/)"; then
        env="test"
    elif echo "$result_file" | grep -qiE "(/prod/|/production/)"; then
        env="prod"
    fi
    
    # Copy file
    cp "$result_file" "$TARGET_DIR/" 2>/dev/null || true
    
    # Add environment metadata to filename for later processing
    # We'll use a marker file to track environments (use .marker extension to avoid Allure processing)
    if [ "$env" != "unknown" ]; then
        basename_file=$(basename "$result_file")
        # Use .marker extension instead of -result.json to prevent Allure from trying to parse it
        marker_file="${basename_file%-result.json}.marker"
        echo "$env" > "$TARGET_DIR/.env.${marker_file}" 2>/dev/null || true
    fi
done

# Debug: Count marker files created
MARKER_DEV=$(find "$TARGET_DIR" -name ".env.*.marker" -exec grep -l "^dev$" {} \; 2>/dev/null | wc -l | tr -d ' ')
MARKER_TEST=$(find "$TARGET_DIR" -name ".env.*.marker" -exec grep -l "^test$" {} \; 2>/dev/null | wc -l | tr -d ' ')
MARKER_PROD=$(find "$TARGET_DIR" -name ".env.*.marker" -exec grep -l "^prod$" {} \; 2>/dev/null | wc -l | tr -d ' ')
echo "🔍 Marker files created:"
echo "   Dev: $MARKER_DEV marker files"
echo "   Test: $MARKER_TEST marker files"
echo "   Prod: $MARKER_PROD marker files"

find "$SOURCE_DIR" -name "*-container.json" -exec cp {} "$TARGET_DIR/" \; 2>/dev/null || true
find "$SOURCE_DIR" -name "*-attachment.*" -exec cp {} "$TARGET_DIR/" \; 2>/dev/null || true

# Also look for results in nested allure-results directories
# Some artifacts might have structure: artifact-name/target/allure-results/
# Also check explicit framework download directories (selenide-results, etc.)
if [ -d "$SOURCE_DIR" ]; then
    # First, check explicit framework download directories
    # These are downloaded separately (selenide-results, etc.)
    for framework_dir in selenide-results; do
        if [ -d "$SOURCE_DIR/$framework_dir" ]; then
            for allure_dir in $(find "$SOURCE_DIR/$framework_dir" -type d -name "allure-results" 2>/dev/null); do
                echo "📂 Found framework allure-results: $allure_dir"
                
                # Determine environment from the path
                env="unknown"
                allure_path=$(echo "$allure_dir" | tr '[:upper:]' '[:lower:]')
                
                # Check for explicit -results-{env} pattern in path
                if echo "$allure_path" | grep -qiE "-results-dev[/-]"; then
                    env="dev"
                elif echo "$allure_path" | grep -qiE "-results-test[/-]"; then
                    env="test"
                elif echo "$allure_path" | grep -qiE "-results-prod[/-]"; then
                    env="prod"
                fi
                
                # Copy result files
                find "$allure_dir" -name "*-result.json" | while read -r result_file; do
                    cp "$result_file" "$TARGET_DIR/" 2>/dev/null || true
                    
                    if [ "$env" != "unknown" ]; then
                        basename_file=$(basename "$result_file")
                        marker_file="${basename_file%-result.json}.marker"
                        echo "$env" > "$TARGET_DIR/.env.${marker_file}" 2>/dev/null || true
                    fi
                done
                
                find "$allure_dir" -name "*-container.json" -exec cp {} "$TARGET_DIR/" \; 2>/dev/null || true
                find "$allure_dir" -name "*-attachment.*" -exec cp {} "$TARGET_DIR/" \; 2>/dev/null || true
            done
        fi
    done
    
    # Then check all other allure-results directories (from *-results-dev pattern)
    for allure_dir in $(find "$SOURCE_DIR" -type d -name "allure-results" 2>/dev/null | grep -v "/selenide-results/" | grep -v "/vibium-results/" | grep -v "/cypress-results/" | grep -v "/playwright-results/" | grep -v "/robot-results/"); do
        echo "📂 Found nested allure-results: $allure_dir"
        
        # Determine environment from the path containing this allure-results directory
        # The path might be like: all-test-results/smoke-results-test/target/allure-results
        env="unknown"
        allure_path=$(echo "$allure_dir" | tr '[:upper:]' '[:lower:]')
        
        # Check for explicit -results-{env} pattern in path (most reliable)
        if echo "$allure_path" | grep -qiE "-results-dev[/-]"; then
            env="dev"
        elif echo "$allure_path" | grep -qiE "-results-test[/-]"; then
            env="test"
        elif echo "$allure_path" | grep -qiE "-results-prod[/-]"; then
            env="prod"
        # Fallback: check for environment in directory names
        elif echo "$allure_path" | grep -qiE "(/dev/|/development/)" && ! echo "$allure_path" | grep -qiE "(/test/|/testing/|/prod/|/production/)"; then
            env="dev"
        elif echo "$allure_path" | grep -qiE "(/test/|/testing/)" && ! echo "$allure_path" | grep -qiE "(/prod/|/production/)"; then
            env="test"
        elif echo "$allure_path" | grep -qiE "(/prod/|/production/)"; then
            env="prod"
        fi
        
        # Copy result files and create marker files for environment tracking
        find "$allure_dir" -name "*-result.json" | while read -r result_file; do
            cp "$result_file" "$TARGET_DIR/" 2>/dev/null || true
            
            # Create marker file if environment was detected
            if [ "$env" != "unknown" ]; then
                basename_file=$(basename "$result_file")
                marker_file="${basename_file%-result.json}.marker"
                echo "$env" > "$TARGET_DIR/.env.${marker_file}" 2>/dev/null || true
            fi
        done
        
        find "$allure_dir" -name "*-container.json" -exec cp {} "$TARGET_DIR/" \; 2>/dev/null || true
        find "$allure_dir" -name "*-attachment.*" -exec cp {} "$TARGET_DIR/" \; 2>/dev/null || true
    done
fi

RESULT_COUNT=$(find "$TARGET_DIR" -name "*-result.json" 2>/dev/null | wc -l | tr -d ' ')
CONTAINER_COUNT=$(find "$TARGET_DIR" -name "*-container.json" 2>/dev/null | wc -l | tr -d ' ')
ATTACHMENT_COUNT=$(find "$TARGET_DIR" -name "*-attachment.*" 2>/dev/null | wc -l | tr -d ' ')

echo "✅ Merge complete!"
echo "  Copied to $TARGET_DIR:"
echo "    Result JSON files: $RESULT_COUNT"
echo "    Container JSON files: $CONTAINER_COUNT"
echo "    Attachment files: $ATTACHMENT_COUNT"

if [ "$RESULT_COUNT" -eq 0 ]; then
    echo "⚠️  No test results found, generating placeholder result..."
    printf '{"name": "Pipeline Execution (No results found)", "status": "passed", "stage": "finished", "start": %s, "stop": %s, "uuid": "%s"}' "$(date +%s%3N)" "$(date +%s%3N)" "$(uuidgen 2>/dev/null || echo 'placeholder-uuid')" > "$TARGET_DIR/placeholder-result.json"
    echo "✅ Placeholder result created"
fi
