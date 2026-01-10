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

# Get PR number from GitHub context or branch name
PR_NUMBER=""
if [ -n "${GITHUB_EVENT_NAME}" ] && [ "${GITHUB_EVENT_NAME}" = "pull_request" ]; then
  # Try to get PR number from event payload
  if [ -f "${GITHUB_EVENT_PATH}" ]; then
    PR_NUMBER=$(jq -r '.pull_request.number // .number // ""' "${GITHUB_EVENT_PATH}" 2>/dev/null || echo "")
  fi
fi

# If PR number not found, try to extract from branch name (e.g., "fix/issue-123" or "pr-456")
if [ -z "$PR_NUMBER" ] && [ -n "${GITHUB_HEAD_REF}" ]; then
  # Try patterns like "pr-123", "pr/123", "fix/pr-123", etc.
  PR_NUMBER=$(echo "${GITHUB_HEAD_REF}" | grep -oE '(pr|PR)[-/]?[0-9]+' | grep -oE '[0-9]+' | head -1 || echo "")
fi

# Construct report name with pipeline and PR information
REPORT_NAME="Allure Report"
if [ -n "${BUILD_ORDER}" ] && [ "${BUILD_ORDER}" != "1" ]; then
  if [ -n "$PR_NUMBER" ]; then
    REPORT_NAME="Allure Report - Pipeline #${BUILD_ORDER} - PR #${PR_NUMBER}"
  else
    REPORT_NAME="Allure Report - Pipeline #${BUILD_ORDER}"
  fi
elif [ -n "$PR_NUMBER" ]; then
  REPORT_NAME="Allure Report - PR #${PR_NUMBER}"
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
  "reportName": "${REPORT_NAME}"
}
EOF

echo "✅ Created Allure executor file: $RESULTS_DIR/executor.json"
echo "   Build Name: $BUILD_NAME"
echo "   Build Order: $BUILD_ORDER"
echo "   Report Name: $REPORT_NAME"
if [ -n "$PR_NUMBER" ]; then
  echo "   PR Number: $PR_NUMBER"
fi
echo "   Build URL: ${BUILD_URL:-N/A}"

