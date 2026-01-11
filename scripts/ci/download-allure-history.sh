#!/bin/bash
# Download Allure History (supports both Allure2 and Allure3)
# Usage: ./scripts/ci/download-allure-history.sh <target-dir> [method]
#
# Arguments:
#   target-dir  - Directory where Allure results are stored (default: allure-results-combined)
#   method      - Download method: "pages" (default) or "artifact"
#
# Configuration:
#   Reads from config/environments.json (allure.reportVersion)
#   Can be overridden via ALLURE_REPORT_VERSION environment variable
#
# This script downloads the history folder from the previous Allure report deployment
# so that trends can be tracked across multiple pipeline runs.
#
# Examples:
#   ./scripts/ci/download-allure-history.sh allure-results-combined
#   ./scripts/ci/download-allure-history.sh allure-results-combined pages
#   ./scripts/ci/download-allure-history.sh allure-results-combined artifact

set -e

# Read Allure version from config file, fallback to environment variable, then default
if [ -f "config/environments.json" ]; then
    ALLURE_VERSION="${ALLURE_REPORT_VERSION:-$(jq -r '.allure.reportVersion // 3' config/environments.json)}"
else
    # Fallback if config file doesn't exist
    ALLURE_VERSION="${ALLURE_REPORT_VERSION:-3}"
fi

if [ "$ALLURE_VERSION" != "2" ] && [ "$ALLURE_VERSION" != "3" ]; then
    echo "❌ Invalid ALLURE_REPORT_VERSION: $ALLURE_VERSION (must be 2 or 3)"
    exit 1
fi

TARGET_DIR="${1:-allure-results-combined}"
METHOD="${2:-pages}"
GITHUB_PAGES_URL="https://cscharer.github.io/full-stack-qa"
HISTORY_URL="$GITHUB_PAGES_URL/history"

echo "📥 Downloading Allure history (Version: $ALLURE_VERSION)..."
echo "   Configuration source: $([ -f "config/environments.json" ] && echo "config/environments.json" || echo "default/environment variable")"
echo "   Target directory: $TARGET_DIR"
echo "   Method: $METHOD"

# Create history directory
mkdir -p "$TARGET_DIR/history"

if [ "$METHOD" = "pages" ]; then
    echo ""
    echo "🌐 Attempting to download from GitHub Pages..."
    
    # Check if GitHub Pages is accessible
    if curl -f -s -I "$GITHUB_PAGES_URL" > /dev/null 2>&1; then
        echo "✅ GitHub Pages is accessible"
        
        if [ "$ALLURE_VERSION" = "3" ]; then
            # Allure3: Download history.jsonl and individual history files
            echo ""
            echo "🔍 Downloading Allure3 history (history.jsonl format)..."
            echo "   Attempting to download history directory via GitHub API..."
            
            # Get repository info from git
            REPO_OWNER="CScharer"
            REPO_NAME="full-stack-qa"
            BRANCH="gh-pages"
            
            # Try to get history directory contents via GitHub API
            API_URL="https://api.github.com/repos/$REPO_OWNER/$REPO_NAME/contents/history?ref=$BRANCH"
            
            DOWNLOADED_COUNT=0
            
            if command -v jq >/dev/null 2>&1; then
                # Use GitHub API to get file listing and download all files
                API_RESPONSE=$(curl -s -H "Accept: application/vnd.github.v3+json" "$API_URL" 2>/dev/null)
                CURL_EXIT_CODE=$?
                
                if [ $CURL_EXIT_CODE -ne 0 ]; then
                    echo "   ❌ ERROR: Failed to connect to GitHub API (curl exit code: $CURL_EXIT_CODE)"
                    echo "   This must be fixed - cannot download history"
                    exit 1
                fi
                
                if echo "$API_RESPONSE" | jq -e '. | type == "array"' >/dev/null 2>&1; then
                    # Directory exists and has files
                    FILE_COUNT=$(echo "$API_RESPONSE" | jq '[.[] | select(.type == "file")] | length')
                    echo "   Found $FILE_COUNT file(s) in history directory"
                    
                    if [ "$FILE_COUNT" -gt 0 ]; then
                        # Download files and track failures
                        FAILED_DOWNLOADS=0
                        while IFS= read -r url; do
                            if [ -n "$url" ] && [ "$url" != "null" ]; then
                                filename=$(basename "$url")
                                if curl -f -s "$url" -o "$TARGET_DIR/history/$filename" 2>/dev/null; then
                                    echo "   ✅ Downloaded: $filename"
                                    DOWNLOADED_COUNT=$((DOWNLOADED_COUNT + 1))
                                else
                                    echo "   ❌ ERROR: Failed to download $filename"
                                    FAILED_DOWNLOADS=$((FAILED_DOWNLOADS + 1))
                                fi
                            fi
                        done < <(echo "$API_RESPONSE" | jq -r '.[] | select(.type == "file") | .download_url' 2>/dev/null)
                        
                        # Count actual downloaded files
                        ACTUAL_COUNT=$(find "$TARGET_DIR/history" -type f 2>/dev/null | wc -l | tr -d ' ')
                        if [ "$ACTUAL_COUNT" -gt 0 ]; then
                            if [ "$ACTUAL_COUNT" -lt "$FILE_COUNT" ] || [ "$FAILED_DOWNLOADS" -gt 0 ]; then
                                echo "   ❌ ERROR: Downloaded $ACTUAL_COUNT of $FILE_COUNT files ($FAILED_DOWNLOADS failed)"
                                echo "   Incomplete history will cause issues - this must be fixed"
                                exit 1
                            else
                                echo "   ✅ GitHub API download completed: $ACTUAL_COUNT file(s)"
                            fi
                            DOWNLOADED_COUNT=$ACTUAL_COUNT
                        else
                            echo "   ❌ ERROR: GitHub API returned $FILE_COUNT files but none were downloaded"
                            echo "   This indicates a critical download failure that must be fixed"
                            exit 1
                        fi
                    else
                        # Check if directory exists with .gitkeep (from previous fix attempt)
                        if [ -f "$TARGET_DIR/history/.gitkeep" ]; then
                            echo "   ℹ️  History directory exists but only contains .gitkeep"
                            echo "   This indicates empty history structure from previous run"
                            echo "   Valid history JSON files should be created in next run"
                        else
                            echo "   ℹ️  History directory exists but is empty (expected for first run)"
                        fi
                    fi
                elif echo "$API_RESPONSE" | jq -e '.message' >/dev/null 2>&1; then
                    # API returned an error (likely 404 - directory doesn't exist)
                    ERROR_MSG=$(echo "$API_RESPONSE" | jq -r '.message' 2>/dev/null || echo "Unknown error")
                    if echo "$ERROR_MSG" | grep -q "404\|Not Found"; then
                        echo "   ℹ️  History directory not found in GitHub Pages (expected for first run)"
                    else
                        echo "   ❌ ERROR: GitHub API returned error: $ERROR_MSG"
                        echo "   This must be fixed - cannot determine history status"
                        exit 1
                    fi
                else
                    echo "   ❌ ERROR: Unexpected API response format"
                    echo "   API Response: $API_RESPONSE"
                    echo "   This must be fixed - cannot parse history directory"
                    exit 1
                fi
            else
                echo "   ❌ ERROR: jq is not available"
                echo "   Cannot parse GitHub API response - this must be fixed"
                exit 1
            fi
        
            if [ "$DOWNLOADED_COUNT" -gt 0 ]; then
                echo ""
                echo "✅ Successfully downloaded $DOWNLOADED_COUNT Allure3 history file(s)"
                HISTORY_SIZE=$(du -sh "$TARGET_DIR/history" 2>/dev/null | cut -f1 || echo "unknown")
                echo "   History size: $HISTORY_SIZE"
            else
                echo ""
                echo "ℹ️  No Allure3 history files found (this is expected for first run)"
                echo "   History will be created after first report generation"
            fi
        else
            # Allure2: Download individual JSON files
            echo ""
            echo "🔍 Downloading Allure2 history (individual JSON files)..."
            echo "   Attempting to download history directory via GitHub API..."
            
            # Get repository info from git
            REPO_OWNER="CScharer"
            REPO_NAME="full-stack-qa"
            BRANCH="gh-pages"
            
            # Try to get history directory contents via GitHub API
            API_URL="https://api.github.com/repos/$REPO_OWNER/$REPO_NAME/contents/history?ref=$BRANCH"
            
            DOWNLOADED_COUNT=0
            
            if command -v jq >/dev/null 2>&1; then
                # Use GitHub API to get file listing and download all JSON files
                API_RESPONSE=$(curl -s -H "Accept: application/vnd.github.v3+json" "$API_URL" 2>/dev/null)
                CURL_EXIT_CODE=$?
                
                if [ $CURL_EXIT_CODE -ne 0 ]; then
                    echo "   ❌ ERROR: Failed to connect to GitHub API (curl exit code: $CURL_EXIT_CODE)"
                    echo "   This must be fixed - cannot download history"
                    exit 1
                fi
                
                if echo "$API_RESPONSE" | jq -e '. | type == "array"' >/dev/null 2>&1; then
                    # Directory exists and has files
                    # CRITICAL: For Allure2, exclude history-trend.json, duration-trend.json, etc. from results directory
                    # Allure2 expects individual history JSON files (one per test), not aggregated trend files
                    # Trend files (history-trend.json) should only be in report directory, not results directory
                    # If history-trend.json is in results directory, Allure2 will try to read it and fail with format mismatch
                    FILE_COUNT=$(echo "$API_RESPONSE" | jq '[.[] | select(.type == "file" and (.name | endswith(".json")) and (.name | test("^(history-trend|duration-trend|retry-trend|categories-trend)\\.json$") | not))] | length')
                    echo "   Found $FILE_COUNT JSON file(s) in history directory (excluding trend files)"
                    
                    if [ "$FILE_COUNT" -gt 0 ]; then
                        # Download JSON files (excluding trend files)
                        FAILED_DOWNLOADS=0
                        while IFS= read -r url; do
                            if [ -n "$url" ] && [ "$url" != "null" ]; then
                                filename=$(basename "$url")
                                # Skip trend files - they should not be in results directory
                                if echo "$filename" | grep -qE "^(history-trend|duration-trend|retry-trend|categories-trend)\.json$"; then
                                    echo "   ⏭️  Skipping $filename (trend files should not be in results directory)"
                                    continue
                                fi
                                if curl -f -s "$url" -o "$TARGET_DIR/history/$filename" 2>/dev/null; then
                                    echo "   ✅ Downloaded: $filename"
                                    DOWNLOADED_COUNT=$((DOWNLOADED_COUNT + 1))
                                else
                                    echo "   ❌ ERROR: Failed to download $filename"
                                    FAILED_DOWNLOADS=$((FAILED_DOWNLOADS + 1))
                                fi
                            fi
                        done < <(echo "$API_RESPONSE" | jq -r '.[] | select(.type == "file" and (.name | endswith(".json")) and (.name | test("^(history-trend|duration-trend|retry-trend|categories-trend)\\.json$") | not)) | .download_url' 2>/dev/null)
                        
                        # Count actual downloaded files (excluding trend files)
                        ACTUAL_COUNT=$(find "$TARGET_DIR/history" -name "*.json" -type f ! -name "history-trend.json" ! -name "duration-trend.json" ! -name "retry-trend.json" ! -name "categories-trend.json" 2>/dev/null | wc -l | tr -d ' ')
                        if [ "$ACTUAL_COUNT" -gt 0 ]; then
                            if [ "$ACTUAL_COUNT" -lt "$FILE_COUNT" ] || [ "$FAILED_DOWNLOADS" -gt 0 ]; then
                                echo "   ⚠️  Downloaded $ACTUAL_COUNT of $FILE_COUNT files ($FAILED_DOWNLOADS failed)"
                                echo "   Continuing with partial history..."
                            else
                                echo "   ✅ GitHub API download completed: $ACTUAL_COUNT file(s)"
                            fi
                            DOWNLOADED_COUNT=$ACTUAL_COUNT
                        else
                            echo "   ℹ️  No Allure2 history JSON files found (this is expected for first run)"
                        fi
                    else
                        echo "   ℹ️  History directory exists but is empty (expected for first run)"
                    fi
                elif echo "$API_RESPONSE" | jq -e '.message' >/dev/null 2>&1; then
                    # API returned an error (likely 404 - directory doesn't exist)
                    ERROR_MSG=$(echo "$API_RESPONSE" | jq -r '.message' 2>/dev/null || echo "Unknown error")
                    if echo "$ERROR_MSG" | grep -q "404\|Not Found"; then
                        echo "   ℹ️  History directory not found in GitHub Pages (expected for first run)"
                    else
                        echo "   ❌ ERROR: GitHub API returned error: $ERROR_MSG"
                        echo "   This must be fixed - cannot determine history status"
                        exit 1
                    fi
                else
                    echo "   ❌ ERROR: Unexpected API response format"
                    echo "   API Response: $API_RESPONSE"
                    echo "   This must be fixed - cannot parse history directory"
                    exit 1
                fi
            else
                echo "   ❌ ERROR: jq is not available"
                echo "   Cannot parse GitHub API response - this must be fixed"
                exit 1
            fi
            
            if [ "$DOWNLOADED_COUNT" -gt 0 ]; then
                echo ""
                echo "✅ Successfully downloaded $DOWNLOADED_COUNT Allure2 history file(s)"
                HISTORY_SIZE=$(du -sh "$TARGET_DIR/history" 2>/dev/null | cut -f1 || echo "unknown")
                echo "   History size: $HISTORY_SIZE"
            else
                echo ""
                echo "ℹ️  No Allure2 history files found (this is expected for first run)"
                echo "   History will be created after first report generation"
            fi
        fi
        
    else
        echo "⚠️  GitHub Pages not accessible"
        echo "   Falling back to artifact method..."
        METHOD="artifact"
    fi
fi

if [ "$METHOD" = "artifact" ]; then
    echo ""
    echo "📦 Attempting to download from GitHub Actions artifact..."
    
    # Note: Artifact download should be done via GitHub Actions workflow step
    # This script just verifies the artifact was downloaded
    if [ -d "$TARGET_DIR/history" ] && [ "$(find "$TARGET_DIR/history" -type f 2>/dev/null | wc -l | tr -d ' ')" -gt 0 ]; then
        echo "✅ History found in artifact"
        HISTORY_SIZE=$(du -sh "$TARGET_DIR/history" 2>/dev/null | cut -f1 || echo "unknown")
        echo "   History size: $HISTORY_SIZE"
        echo "   Files: $(find "$TARGET_DIR/history" -type f 2>/dev/null | wc -l | tr -d ' ') file(s)"
    else
        echo "ℹ️  No history artifact found (this is expected for first run)"
        echo "   History will be created after first report generation"
    fi
fi

# Verify history directory exists
if [ -d "$TARGET_DIR/history" ]; then
    FILE_COUNT=$(find "$TARGET_DIR/history" -type f 2>/dev/null | wc -l | tr -d ' ')
    if [ "$FILE_COUNT" -gt 0 ]; then
        echo ""
        echo "✅ History download complete"
        echo "   Location: $TARGET_DIR/history"
        echo "   Files: $FILE_COUNT"
    else
        echo ""
        echo "✅ History directory created (empty - first run)"
    fi
else
    echo ""
    echo "✅ History directory will be created during report generation"
fi

