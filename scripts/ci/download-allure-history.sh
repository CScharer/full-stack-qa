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
        
        # Download history files from GitHub Pages
        # Allure history typically contains JSON files in the history/ directory
        # We'll try to download common history files
        
        HISTORY_FILES=(
            "history-trend.json"
            "duration-trend.json"
            "retry-trend.json"
            "history.json"
        )
        
        DOWNLOADED_COUNT=0
        for history_file in "${HISTORY_FILES[@]}"; do
            if curl -f -s "$HISTORY_URL/$history_file" -o "$TARGET_DIR/history/$history_file" 2>/dev/null; then
                echo "   ✅ Downloaded: $history_file"
                DOWNLOADED_COUNT=$((DOWNLOADED_COUNT + 1))
            else
                echo "   ⚠️  Not found: $history_file (may not exist yet)"
            fi
        done
        
        # Also try to download the entire history directory using GitHub API
        # This is more reliable as it gets the actual file structure
        echo ""
        echo "🔍 Attempting to download via GitHub API..."
        
        # Get repository info from git
        REPO_OWNER="CScharer"
        REPO_NAME="full-stack-qa"
        BRANCH="gh-pages"
        
        # Try to get history directory contents via GitHub API
        API_URL="https://api.github.com/repos/$REPO_OWNER/$REO_NAME/contents/history?ref=$BRANCH"
        
        if command -v jq >/dev/null 2>&1; then
            # Use GitHub API to get file listing
            if curl -s -H "Accept: application/vnd.github.v3+json" "$API_URL" | jq -r '.[] | select(.type == "file") | .download_url' 2>/dev/null | while read -r url; do
                if [ -n "$url" ] && [ "$url" != "null" ]; then
                    filename=$(basename "$url")
                    if curl -f -s "$url" -o "$TARGET_DIR/history/$filename" 2>/dev/null; then
                        echo "   ✅ Downloaded via API: $filename"
                        DOWNLOADED_COUNT=$((DOWNLOADED_COUNT + 1))
                    fi
                fi
            done; then
                echo "   ✅ GitHub API download completed"
            else
                echo "   ⚠️  GitHub API download failed or no files found"
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

