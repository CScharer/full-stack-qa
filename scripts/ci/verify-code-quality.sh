#!/bin/bash
# scripts/ci/verify-code-quality.sh
# Code Quality Verification (Read-Only)
#
# Purpose: Verify code quality using Checkstyle and PMD (read-only, no modifications)
#
# Usage:
#   ./scripts/ci/verify-code-quality.sh
#
# Description:
#   This script runs read-only code quality checks:
#   - Checkstyle: Verifies code style compliance (Google Java Style, 120-char line length)
#   - PMD: Analyzes code for potential bugs, code smells, and best practices
#
#   This is a verification-only script - it does not modify code or format files.
#   Formatting and compilation are handled by other scripts.
#
# Examples:
#   ./scripts/ci/verify-code-quality.sh
#
# Dependencies:
#   - Maven wrapper (./mvnw)
#   - Java 21+
#   - Checkstyle Maven plugin (configured in pom.xml)
#   - PMD Maven plugin (configured in pom.xml)
#
# Output:
#   - Checkstyle violation count (0 violations expected)
#   - PMD violation count (0 violations expected)
#   - Exit code: 0 if both pass, non-zero if violations found
#
# Notes:
#   - Used in CI/CD pipeline for code quality validation
#   - Suppresses Maven progress output for cleaner logs
#   - Read-only verification (no code modifications)
#   - Project maintains 0 Checkstyle and 0 PMD violations
#
# Last Updated: January 2026

set -e

# Helper function to suppress Maven Progress lines
setup_maven_opts() {
    # Suppress Maven transfer listener progress output
    if [ -n "${MAVEN_OPTS:-}" ]; then
        export MAVEN_OPTS="${MAVEN_OPTS} -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn"
    else
        export MAVEN_OPTS="-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn"
    fi
}

# Helper function to run Maven command with Progress filtering
run_maven() {
    setup_maven_opts
    ./mvnw "$@" 2>&1 | grep -vE "^Progress"
    return ${PIPESTATUS[0]}  # Return Maven's exit code, not grep's
}

# Setup Maven options at script start
setup_maven_opts

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}CI Code Quality Verification${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Run Checkstyle and PMD in parallel for speed
echo -e "${YELLOW}Running Checkstyle and PMD checks...${NC}"

run_maven checkstyle:check > /tmp/checkstyle-output.log 2>&1 &
CHECKSTYLE_PID=$!

run_maven pmd:check > /tmp/pmd-output.log 2>&1 &
PMD_PID=$!

# Wait for both
wait $CHECKSTYLE_PID
CHECKSTYLE_EXIT=$?

wait $PMD_PID
PMD_EXIT=$?

# Report results
if [ $CHECKSTYLE_EXIT -eq 0 ]; then
    echo -e "${GREEN}✅ Checkstyle: No violations found${NC}"
else
    echo -e "${YELLOW}⚠️  Checkstyle: Violations found${NC}"
    # Show summary
    grep -E "(You have|violation)" /tmp/checkstyle-output.log | head -5 || true
fi

if [ $PMD_EXIT -eq 0 ]; then
    echo -e "${GREEN}✅ PMD: No violations found${NC}"
else
    echo -e "${YELLOW}⚠️  PMD: Violations found${NC}"
    # Show summary
    grep -E "PMD.*has found.*violation" /tmp/pmd-output.log | head -1 || true
fi

# Exit with error if either failed
if [ $CHECKSTYLE_EXIT -ne 0 ] || [ $PMD_EXIT -ne 0 ]; then
    echo -e "${RED}❌ Code quality checks failed${NC}"
    exit 1
fi

echo -e "${GREEN}✅ All code quality checks passed${NC}"
exit 0
