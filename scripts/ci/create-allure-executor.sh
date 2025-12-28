#!/bin/bash
# Create Allure Executor Information File
# Usage: ./scripts/ci/create-allure-executor.sh <results-dir> [build-name] [build-order] [build-url]
#
# Arguments:
#   results-dir  - Directory where Allure results are stored (default: allure-results-combined)
#   build-name   - Build name (default: GitHub Actions workflow name)
#   build-order  - Build order/number (default: GitHub Actions run number)
#   build-url    - Build URL (default: GitHub Actions run URL)
#
# Examples:
#   ./scripts/ci/create-allure-executor.sh allure-results-combined
#   ./scripts/ci/create-allure-executor.sh allure-results-combined "CI Build" "123" "https://github.com/..."

set -e

RESULTS_DIR="${1:-allure-results-combined}"
BUILD_NAME="${2:-${GITHUB_WORKFLOW:-CI Build}}"
BUILD_ORDER="${3:-${GITHUB_RUN_NUMBER:-1}}"
BUILD_URL="${4:-}"

# If BUILD_URL not provided, construct from GitHub Actions context
if [ -z "$BUILD_URL" ]; then
  if [ -n "$GITHUB_SERVER_URL" ] && [ -n "$GITHUB_REPOSITORY" ] && [ -n "$GITHUB_RUN_ID" ]; then
    BUILD_URL="${GITHUB_SERVER_URL}/${GITHUB_REPOSITORY}/actions/runs/${GITHUB_RUN_ID}"
  else
    BUILD_URL=""
  fi
fi

# Create directory if it doesn't exist
mkdir -p "$RESULTS_DIR"

# Create executor.json file
cat > "$RESULTS_DIR/executor.json" << EOF
{
  "name": "GitHub Actions",
  "type": "github",
  "url": "${GITHUB_SERVER_URL:-https://github.com}/${GITHUB_REPOSITORY:-CScharer/full-stack-qa}",
  "buildOrder": "${BUILD_ORDER}",
  "buildName": "${BUILD_NAME}",
  "buildUrl": "${BUILD_URL}",
  "reportUrl": "",
  "reportName": "Allure Report"
}
EOF

echo "✅ Created Allure executor file: $RESULTS_DIR/executor.json"
echo "   Build Name: $BUILD_NAME"
echo "   Build Order: $BUILD_ORDER"
echo "   Build URL: ${BUILD_URL:-N/A}"

