#!/bin/bash
# Run Maven Tests
# Usage: ./scripts/ci/run-maven-tests.sh <environment> <suite-file> [retry-count] [browser] [additional-args...]
#
# Arguments:
#   environment    - Test environment (dev, test, prod)
#   suite-file    - TestNG suite file (e.g., testng-smoke-suite.xml)
#   retry-count   - Number of retries for failed tests (default: 1)
#   browser       - Browser name for browser-specific tests (optional)
#   additional-args - Additional Maven arguments (optional)
#
# Examples:
#   ./scripts/ci/run-maven-tests.sh dev testng-smoke-suite.xml
#   ./scripts/ci/run-maven-tests.sh test testng-grid-suite.xml 2 chrome
#   ./scripts/ci/run-maven-tests.sh dev testng-ci-suite.xml 1 firefox -Dcustom.property=value

set -e

# Parse arguments
ENVIRONMENT=${1:-dev}
SUITE_FILE=${2:-testng-smoke-suite.xml}
RETRY_COUNT=${3:-1}
BROWSER=${4:-}
ADDITIONAL_ARGS="${@:5}"  # All remaining arguments

# Validate required arguments
if [ -z "$ENVIRONMENT" ] || [ -z "$SUITE_FILE" ]; then
  echo "❌ Error: Environment and suite file are required"
  echo "Usage: $0 <environment> <suite-file> [retry-count] [browser] [additional-args...]"
  exit 1
fi

# Solution 1: Reuse compiled classes from build-and-compile job if available
# Check if pre-compiled classes exist (downloaded from compiled-classes artifact)
if [ -d "pre-compiled-classes/target" ] && [ -n "$(ls -A pre-compiled-classes/target/classes 2>/dev/null)" ]; then
  echo "✅ Found pre-compiled classes from build-and-compile job"
  echo "📦 Reusing compiled classes to skip compilation..."
  chmod +x scripts/ci/reuse-or-compile.sh
  ./scripts/ci/reuse-or-compile.sh "pre-compiled-classes/target"
else
  echo "ℹ️  No pre-compiled classes found, will compile during test execution"
fi

# Build Maven command
# Solution 2: Skip checkstyle since it already runs in code-quality-analysis job
MAVEN_CMD="./mvnw -ntp test"
MAVEN_CMD="$MAVEN_CMD -Dtest.environment=$ENVIRONMENT"
MAVEN_CMD="$MAVEN_CMD -Dtest.retry.max.count=$RETRY_COUNT"
MAVEN_CMD="$MAVEN_CMD -DsuiteXmlFile=$SUITE_FILE"
MAVEN_CMD="$MAVEN_CMD -Dcheckstyle.skip=true"

# Add browser parameter if provided
if [ -n "$BROWSER" ]; then
  MAVEN_CMD="$MAVEN_CMD -Dbrowser=$BROWSER"
fi

# Add additional arguments if provided
if [ -n "$ADDITIONAL_ARGS" ]; then
  MAVEN_CMD="$MAVEN_CMD $ADDITIONAL_ARGS"
fi

# Execute Maven command
echo "🚀 Running Maven tests..."
echo "   Environment: $ENVIRONMENT"
echo "   Suite: $SUITE_FILE"
echo "   Retry Count: $RETRY_COUNT"
[ -n "$BROWSER" ] && echo "   Browser: $BROWSER"
[ -n "$ADDITIONAL_ARGS" ] && echo "   Additional Args: $ADDITIONAL_ARGS"
echo "   Optimizations: Checkstyle skipped (already run in code-quality-analysis job)"
echo ""

eval $MAVEN_CMD

