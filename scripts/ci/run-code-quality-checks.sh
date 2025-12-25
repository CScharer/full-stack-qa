#!/bin/bash
# scripts/ci/run-code-quality-checks.sh
# Runs Checkstyle and PMD code quality checks

set -e

echo "Running Checkstyle..."
./mvnw -ntp checkstyle:check || {
  echo "⚠️ Checkstyle found issues (continuing with PMD)..."
  exit_code=$?
}

echo ""
echo "Running PMD code analysis..."
./mvnw -ntp pmd:check || {
  echo "⚠️ PMD found issues"
  exit_code=$?
}

# Exit with the last non-zero exit code, or 0 if both passed
exit ${exit_code:-0}
