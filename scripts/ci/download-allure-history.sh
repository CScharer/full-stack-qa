#!/bin/bash
# Download Allure History from GitHub Pages or Artifact
# Usage: ./scripts/ci/download-allure-history.sh <target-dir> [method]
#
# Arguments:
#   target-dir  - Directory where Allure results are stored (default: allure-results-combined)
#   method      - Download method: "pages" (default) or "artifact"
#
# This script downloads the history folder from the previous Allure report deployment
# so that trends can be tracked across multiple pipeline runs.
#
# Examples:
#   ./scripts/ci/download-allure-history.sh allure-results-combined
#   ./scripts/ci/download-allure-history.sh allure-results-combined pages
#   ./scripts/ci/download-allure-history.sh allure-results-combined artifact

set -e

TARGET_DIR="${1:-allure-results-combined}"
METHOD="${2:-pages}"
GITHUB_PAGES_URL="https://cscharer.github.io/full-stack-qa"
HISTORY_URL="$GITHUB_PAGES_URL/history"

echo "📥 Downloading Allure history for trend tracking..."
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
        
            # Download the entire history directory from GitHub Pages
            # Allure3 creates history files with MD5 hash filenames in history/ directory
            # We need to download the entire directory structure
            
            echo ""
            echo "🔍 Attempting to download history directory via GitHub API..."
            
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
                
                if echo "$API_RESPONSE" | jq -e '. | type == "array"' >/dev/null 2>&1; then
                    # Directory exists and has files
                    echo "$API_RESPONSE" | jq -r '.[] | select(.type == "file") | .download_url' 2>/dev/null | while read -r url; do
                        if [ -n "$url" ] && [ "$url" != "null" ]; then
                            filename=$(basename "$url")
                            if curl -f -s "$url" -o "$TARGET_DIR/history/$filename" 2>/dev/null; then
                                echo "   ✅ Downloaded: $filename"
                                DOWNLOADED_COUNT=$((DOWNLOADED_COUNT + 1))
                            fi
                        fi
                    done
                    
                    # Count actual downloaded files
                    ACTUAL_COUNT=$(find "$TARGET_DIR/history" -type f 2>/dev/null | wc -l | tr -d ' ')
                    if [ "$ACTUAL_COUNT" -gt 0 ]; then
                        echo "   ✅ GitHub API download completed: $ACTUAL_COUNT file(s)"
                        DOWNLOADED_COUNT=$ACTUAL_COUNT
                    else
                        echo "   ⚠️  GitHub API returned files but download failed or directory is empty"
                    fi
                elif echo "$API_RESPONSE" | jq -e '.message' >/dev/null 2>&1; then
                    # API returned an error (likely 404 - directory doesn't exist)
                    echo "   ℹ️  History directory not found in GitHub Pages (expected for first run)"
                else
                    echo "   ⚠️  Unexpected API response format"
                fi
            else
                echo "   ⚠️  jq not available, skipping API download"
            fi
        
        if [ "$DOWNLOADED_COUNT" -gt 0 ]; then
            echo ""
            echo "✅ Successfully downloaded $DOWNLOADED_COUNT history file(s)"
            HISTORY_SIZE=$(du -sh "$TARGET_DIR/history" 2>/dev/null | cut -f1 || echo "unknown")
            echo "   History size: $HISTORY_SIZE"
        else
            echo ""
            echo "ℹ️  No history files found (this is expected for first run)"
            echo "   History will be created after first report generation"
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

