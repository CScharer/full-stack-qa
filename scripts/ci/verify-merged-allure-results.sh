#!/bin/bash
# scripts/ci/verify-merged-allure-results.sh
# Verifies merged Allure results after combining from multiple environments

set -e

MERGED_DIR="${1:-allure-results-combined}"

echo ""
echo "🔍 Verifying merged results:"

if [ -d "$MERGED_DIR" ]; then
  RESULT_COUNT=$(find "$MERGED_DIR" -name "*-result.json" 2>/dev/null | wc -l | tr -d ' ')
  echo "  Total result JSON files in $MERGED_DIR: $RESULT_COUNT"
  if [ "$RESULT_COUNT" -gt 0 ]; then
    echo "  Sample files:"
    find "$MERGED_DIR" -name "*-result.json" | head -5
  else
    echo "  ⚠️  No result files found in $MERGED_DIR"
  fi
else
  echo "  ⚠️  $MERGED_DIR directory does not exist"
fi
