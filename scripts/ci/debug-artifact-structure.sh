#!/bin/bash
# scripts/ci/debug-artifact-structure.sh
# Debugs downloaded artifact structure to help diagnose issues

set -e

ARTIFACT_DIR="${1:-all-test-results}"

echo "🔍 Debugging downloaded artifact structure..."

if [ -d "$ARTIFACT_DIR" ]; then
  echo "📂 $ARTIFACT_DIR directory exists"
  echo "Directory structure (first 3 levels):"
  find "$ARTIFACT_DIR" -type d -maxdepth 3 | head -30 || true
  echo ""
  echo "📄 Looking for Allure result JSON files:"
  find "$ARTIFACT_DIR" -name "*-result.json" | head -20 || echo "  No *-result.json files found"
  echo ""
  echo "📂 Looking for allure-results directories:"
  find "$ARTIFACT_DIR" -type d -name "allure-results" | head -10 || echo "  No allure-results directories found"
else
  echo "⚠️  $ARTIFACT_DIR directory does not exist"
fi
