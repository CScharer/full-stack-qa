#!/bin/bash
# scripts/ci/list-available-artifacts.sh
# Lists available artifacts from the current workflow run for debugging

set -e

echo "📦 Listing available artifacts from this workflow run..."
echo "Workflow Run ID: $GITHUB_RUN_ID"
echo "Repository: $GITHUB_REPOSITORY"
echo ""

# Use GitHub API to list artifacts
# Note: This requires GITHUB_TOKEN which is available by default
if [ -n "$GITHUB_TOKEN" ]; then
  echo "Available artifacts:"
  curl -s -H "Authorization: token $GITHUB_TOKEN" \
    "https://api.github.com/repos/$GITHUB_REPOSITORY/actions/runs/$GITHUB_RUN_ID/artifacts" \
    | jq -r '.artifacts[] | "  - \(.name) (ID: \(.id), Size: \(.size_in_bytes) bytes, Created: \(.created_at))"' \
    || echo "  ⚠️  Could not list artifacts (API may not be available or no artifacts exist)"
else
  echo "⚠️  GITHUB_TOKEN not available, cannot list artifacts via API"
fi

echo ""
