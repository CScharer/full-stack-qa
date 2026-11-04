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
#   Uses GITHUB_TOKEN or GH_TOKEN for authenticated GitHub API calls (required in CI
#   to avoid unauthenticated rate limits of 60 req/hour per IP)
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

# Prefer GITHUB_TOKEN (Actions default) or GH_TOKEN (gh CLI convention)
AUTH_TOKEN="${GITHUB_TOKEN:-${GH_TOKEN:-}}"

# Known Allure history files published to GitHub Pages (no API needed)
ALLURE3_KNOWN_FILES=(
    "history.jsonl"
    "history.json"
    "history-trend.json"
    "categories-trend.json"
    "duration-trend.json"
    "retry-trend.json"
)

echo "📥 Downloading Allure history (Version: $ALLURE_VERSION)..."
echo "   Configuration source: $([ -f "config/environments.json" ] && echo "config/environments.json" || echo "default/environment variable")"
echo "   Target directory: $TARGET_DIR"
echo "   Method: $METHOD"
if [ -n "$AUTH_TOKEN" ]; then
    echo "   GitHub API auth: ✅ token available"
else
    echo "   GitHub API auth: ⚠️  no token (Pages direct download preferred; API may hit rate limits)"
fi

# Create history directory
mkdir -p "$TARGET_DIR/history"

# Download a single file from GitHub Pages. Returns 0 on success, 1 on miss/failure.
download_pages_file() {
    local filename="$1"
    local dest="$TARGET_DIR/history/$filename"
    local url="$HISTORY_URL/$filename"

    if curl -f -sL "$url" -o "$dest" 2>/dev/null; then
        # Reject empty downloads (except intentional empty markers)
        if [ ! -s "$dest" ]; then
            rm -f "$dest"
            return 1
        fi
        echo "   ✅ Downloaded from Pages: $filename"
        return 0
    fi
    rm -f "$dest"
    return 1
}

# Download known Allure3 history files directly from GitHub Pages (avoids API rate limits)
# Sets PAGES_DOWNLOAD_COUNT on success path.
download_allure3_from_pages() {
    PAGES_DOWNLOAD_COUNT=0
    echo "   Attempting direct download from GitHub Pages (no API)..."
    for filename in "${ALLURE3_KNOWN_FILES[@]}"; do
        if download_pages_file "$filename"; then
            PAGES_DOWNLOAD_COUNT=$((PAGES_DOWNLOAD_COUNT + 1))
        fi
    done
}

# List and download history files via GitHub Contents API (authenticated when possible)
# Args: $1 = jq expression selecting objects to download (applied as: jq -r "$1 | .download_url")
download_history_via_api() {
    local jq_select="$1"
    local label="$2"

    local REPO_OWNER="${GITHUB_REPOSITORY_OWNER:-CScharer}"
    local REPO_NAME
    if [ -n "${GITHUB_REPOSITORY:-}" ]; then
        REPO_NAME="${GITHUB_REPOSITORY#*/}"
    else
        REPO_NAME="full-stack-qa"
    fi
    local BRANCH="gh-pages"
    local API_URL="https://api.github.com/repos/$REPO_OWNER/$REPO_NAME/contents/history?ref=$BRANCH"

    echo "   Attempting to download history directory via GitHub API ($label)..."

    if ! command -v jq >/dev/null 2>&1; then
        echo "   ❌ ERROR: jq is not available"
        echo "   Cannot parse GitHub API response - this must be fixed"
        return 1
    fi

    local API_RESPONSE HTTP_CODE
    if [ -n "$AUTH_TOKEN" ]; then
        API_RESPONSE=$(curl -s -w "\n%{http_code}" \
            -H "Accept: application/vnd.github.v3+json" \
            -H "Authorization: Bearer $AUTH_TOKEN" \
            -H "X-GitHub-Api-Version: 2022-11-28" \
            "$API_URL" 2>/dev/null) || true
    else
        API_RESPONSE=$(curl -s -w "\n%{http_code}" \
            -H "Accept: application/vnd.github.v3+json" \
            "$API_URL" 2>/dev/null) || true
    fi
    HTTP_CODE=$(echo "$API_RESPONSE" | tail -n1)
    API_RESPONSE=$(echo "$API_RESPONSE" | sed '$d')

    if [ -z "$HTTP_CODE" ] || [ "$HTTP_CODE" = "000" ]; then
        echo "   ❌ ERROR: Failed to connect to GitHub API"
        return 1
    fi

    if [ "$HTTP_CODE" = "403" ] || [ "$HTTP_CODE" = "429" ]; then
        local ERROR_MSG
        ERROR_MSG=$(echo "$API_RESPONSE" | jq -r '.message // "rate limit or forbidden"' 2>/dev/null || echo "rate limit or forbidden")
        echo "   ❌ ERROR: GitHub API returned HTTP $HTTP_CODE: $ERROR_MSG"
        if [ -z "$AUTH_TOKEN" ]; then
            echo "   Tip: set GITHUB_TOKEN (or GH_TOKEN) so authenticated requests use a higher rate limit"
        fi
        return 1
    fi

    if [ "$HTTP_CODE" = "404" ]; then
        echo "   ℹ️  History directory not found in gh-pages (expected for first run)"
        return 0
    fi

    if [ "$HTTP_CODE" != "200" ]; then
        local ERROR_MSG
        ERROR_MSG=$(echo "$API_RESPONSE" | jq -r '.message // "Unknown error"' 2>/dev/null || echo "Unknown error")
        echo "   ❌ ERROR: GitHub API returned HTTP $HTTP_CODE: $ERROR_MSG"
        return 1
    fi

    if ! echo "$API_RESPONSE" | jq -e '. | type == "array"' >/dev/null 2>&1; then
        echo "   ❌ ERROR: Unexpected API response format"
        echo "   API Response: $API_RESPONSE"
        return 1
    fi

    local FILE_COUNT API_DOWNLOADED_COUNT=0 FAILED_DOWNLOADS=0
    FILE_COUNT=$(echo "$API_RESPONSE" | jq "[$jq_select] | length")
    echo "   Found $FILE_COUNT file(s) in history directory via API"

    if [ "$FILE_COUNT" -eq 0 ]; then
        echo "   ℹ️  History directory exists but has no matching files (expected for first run)"
        return 0
    fi

    while IFS= read -r url; do
        if [ -n "$url" ] && [ "$url" != "null" ]; then
            local filename
            filename=$(basename "$url")
            if curl -f -sL "$url" -o "$TARGET_DIR/history/$filename" 2>/dev/null; then
                echo "   ✅ Downloaded: $filename"
                API_DOWNLOADED_COUNT=$((API_DOWNLOADED_COUNT + 1))
            else
                echo "   ❌ ERROR: Failed to download $filename"
                FAILED_DOWNLOADS=$((FAILED_DOWNLOADS + 1))
            fi
        fi
    done < <(echo "$API_RESPONSE" | jq -r "$jq_select | .download_url" 2>/dev/null)

    if [ "$API_DOWNLOADED_COUNT" -eq 0 ]; then
        echo "   ❌ ERROR: GitHub API returned $FILE_COUNT files but none were downloaded"
        return 1
    fi

    if [ "$FAILED_DOWNLOADS" -gt 0 ]; then
        echo "   ❌ ERROR: Downloaded $API_DOWNLOADED_COUNT of $FILE_COUNT files ($FAILED_DOWNLOADS failed)"
        return 1
    fi

    echo "   ✅ GitHub API download completed: $API_DOWNLOADED_COUNT file(s)"
    return 0
}

if [ "$METHOD" = "pages" ]; then
    echo ""
    echo "🌐 Attempting to download from GitHub Pages..."

    # Check if GitHub Pages is accessible
    if curl -f -s -I "$GITHUB_PAGES_URL" > /dev/null 2>&1; then
        echo "✅ GitHub Pages is accessible"

        DOWNLOADED_COUNT=0

        if [ "$ALLURE_VERSION" = "3" ]; then
            # Allure3: Prefer direct Pages download of known files (no API / no rate limit)
            echo ""
            echo "🔍 Downloading Allure3 history (history.jsonl format)..."

            download_allure3_from_pages

            if [ "${PAGES_DOWNLOAD_COUNT:-0}" -gt 0 ]; then
                DOWNLOADED_COUNT=$PAGES_DOWNLOAD_COUNT
                echo "   ✅ Direct Pages download completed: $DOWNLOADED_COUNT file(s)"
            else
                echo "   ℹ️  No known Allure3 history files found on Pages; trying GitHub API..."
                if download_history_via_api '.[] | select(.type == "file")' "Allure3"; then
                    DOWNLOADED_COUNT=$(find "$TARGET_DIR/history" -type f ! -name '.gitkeep' 2>/dev/null | wc -l | tr -d ' ')
                else
                    echo "   ❌ ERROR: History download from GitHub Pages failed"
                    exit 1
                fi
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
            # Allure2: Individual hashed JSON files require API listing (or sparse checkout).
            # Authenticate to avoid shared-runner unauthenticated rate limits.
            echo ""
            echo "🔍 Downloading Allure2 history (individual JSON files)..."

            # Exclude trend/aggregate files from results history (Allure2 expects per-test hashes only)
            ALLURE2_FILTER='.[] | select(.type == "file" and (.name | endswith(".json")) and (.name | test("^(history-trend|duration-trend|retry-trend|categories-trend)\\.json$") | not))'
            if download_history_via_api "$ALLURE2_FILTER" "Allure2"; then
                DOWNLOADED_COUNT=$(find "$TARGET_DIR/history" -name "*.json" -type f \
                    ! -name "history-trend.json" ! -name "duration-trend.json" \
                    ! -name "retry-trend.json" ! -name "categories-trend.json" \
                    2>/dev/null | wc -l | tr -d ' ')
            else
                echo "   ❌ ERROR: History download via GitHub API failed"
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
