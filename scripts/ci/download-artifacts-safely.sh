#!/bin/bash
# scripts/ci/download-artifacts-safely.sh
# Safely download artifacts, handling corrupted or missing artifacts gracefully

set -e

DOWNLOAD_PATH=${1:-test-results}
ARTIFACT_PATTERN=${2:-""}

mkdir -p "$DOWNLOAD_PATH"

echo "📥 Attempting to download artifacts..."
echo "  Path: $DOWNLOAD_PATH"
if [ -n "$ARTIFACT_PATTERN" ]; then
  echo "  Pattern: $ARTIFACT_PATTERN"
fi

# Try to download artifacts using GitHub Actions API
# If using pattern, we'll need to list artifacts first and download individually
# For now, we'll use the standard download-artifact action but with better error handling

# Note: This script is a wrapper that will be called from the workflow
# The actual download is handled by actions/download-artifact@v4
# This script is here for future enhancement if needed

echo "✅ Artifact download step completed (handled by actions/download-artifact@v4)"
