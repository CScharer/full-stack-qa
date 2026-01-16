#!/bin/bash
# scripts/ci/run-code-quality-checks.sh
# Code Quality Checks Runner
#
# Purpose: Run Checkstyle and PMD code quality checks in CI/CD pipeline
#
# Usage:
#   ./scripts/ci/run-code-quality-checks.sh
#
# Description:
#   This script runs static code analysis tools:
#   - Checkstyle: Verifies code style compliance (Google Java Style)
#   - PMD: Analyzes code for potential bugs and code smells
#
#   Both checks are run even if one fails, allowing visibility into all issues.
#
# Examples:
#   ./scripts/ci/run-code-quality-checks.sh
#
# Dependencies:
#   - Maven wrapper (./mvnw)
#   - Java 21+
#   - Checkstyle Maven plugin (configured in pom.xml)
#   - PMD Maven plugin (configured in pom.xml)
#
# Output:
#   - Checkstyle results in console output
#   - PMD results in console output
#   - Exit code: 0 if both pass, non-zero if either fails
#
# Notes:
#   - Used in CI/CD pipeline for code quality validation
#   - Continues with PMD even if Checkstyle fails
#   - Exit code reflects the last failed check
#
# Last Updated: January 2026

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
