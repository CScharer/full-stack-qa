#!/bin/bash
# scripts/ci/check-test-data-changes.sh
# Checks if test data JSON files were modified in the current commit

set -e

BEFORE_SHA=$1
CURRENT_SHA=$2

if git diff --name-only "$BEFORE_SHA" "$CURRENT_SHA" | grep -q "^test-data/.*\.json$"; then
  echo "📝 Test data JSON files were modified"
  echo "test-data-changed=true" >> $GITHUB_ENV
else
  echo "test-data-changed=false" >> $GITHUB_ENV
fi
