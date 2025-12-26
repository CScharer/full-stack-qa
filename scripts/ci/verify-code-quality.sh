#!/bin/bash
# CI Code Quality Verification (Read-Only)
# - Checkstyle: Verify code style
# - PMD: Verify code quality
# Note: Formatting and compilation are handled elsewhere

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
