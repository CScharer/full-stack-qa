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
# The artifact is uploaded as "path: target/" and downloaded to "path: pre-compiled-classes"
# GitHub Actions behavior: When you upload a directory, it preserves the directory structure
# So "path: target/" uploaded and downloaded to "pre-compiled-classes" creates: pre-compiled-classes/target/
if [ -d "pre-compiled-classes" ]; then
  echo "🔍 Checking for pre-compiled classes..."
  
  # Debug: Show actual structure (first 3 levels)
  echo "   Artifact directory structure:"
  find pre-compiled-classes -maxdepth 3 -type d 2>/dev/null | head -15 | sed 's/^/      /' || echo "      (empty or not accessible)"
  
  # Check for target subdirectory (most likely - GitHub Actions preserves directory structure)
  if [ -d "pre-compiled-classes/target" ] && [ -d "pre-compiled-classes/target/classes" ] && [ -n "$(ls -A pre-compiled-classes/target/classes 2>/dev/null)" ]; then
    echo "✅ Found pre-compiled classes from build-and-compile job (target/ structure)"
    echo "📦 Reusing compiled classes to skip compilation..."
    chmod +x scripts/ci/reuse-or-compile.sh
    ./scripts/ci/reuse-or-compile.sh "pre-compiled-classes/target"
  # Fallback: Check for direct classes (in case structure is flattened during upload)
  elif [ -d "pre-compiled-classes/classes" ] && [ -n "$(ls -A pre-compiled-classes/classes 2>/dev/null)" ]; then
    echo "✅ Found pre-compiled classes (flattened structure)"
    echo "📦 Reusing compiled classes to skip compilation..."
    mkdir -p target
    # Copy main classes
    cp -r pre-compiled-classes/classes target/ 2>/dev/null || {
      echo "⚠️  Failed to copy classes, will compile"
    }
    # Copy test classes if available
    if [ -d "pre-compiled-classes/test-classes" ] && [ -n "$(ls -A pre-compiled-classes/test-classes 2>/dev/null)" ]; then
      cp -r pre-compiled-classes/test-classes target/ 2>/dev/null || true
    fi
    # Verify we have the essential compiled classes
    if [ -d "target/classes" ] && [ -n "$(ls -A target/classes 2>/dev/null)" ]; then
      echo "✅ Successfully reused compiled classes"
      # Touch class files AND target directory to make them appear newer than sources
      # This prevents Maven's incremental compilation from detecting changes
      touch target/classes 2>/dev/null || true
      find target/classes -type f -name "*.class" -exec touch {} \; 2>/dev/null || true
      if [ -d "target/test-classes" ] && [ -n "$(ls -A target/test-classes 2>/dev/null)" ]; then
        touch target/test-classes 2>/dev/null || true
        find target/test-classes -type f -name "*.class" -exec touch {} \; 2>/dev/null || true
        # Touch test source files to make them appear older than compiled classes
        # This prevents Maven from detecting them as changed
        find src/test/java -type f -name "*.java" -exec touch -r target/test-classes {} \; 2>/dev/null || true
      fi
      # Also copy maven-status to prevent dependency checking
      if [ -d "pre-compiled-classes/maven-status" ]; then
        mkdir -p target/maven-status
        cp -r pre-compiled-classes/maven-status/* target/maven-status/ 2>/dev/null || true
        # Ensure maven-status timestamps are updated
        find target/maven-status -type f -exec touch {} \; 2>/dev/null || true
      fi
      echo "   Updated timestamps - compilation will be skipped"
    else
      echo "⚠️  Classes incomplete, will compile"
    fi
  else
    echo "ℹ️  Artifact downloaded but no compiled classes found, will compile during test execution"
    echo "   Debug: Full directory tree (first 20 lines):"
    find pre-compiled-classes -type f -o -type d 2>/dev/null | head -20 | sed 's/^/      /' || echo "      (directory empty or not accessible)"
  fi
else
  echo "ℹ️  No pre-compiled classes found, will compile during test execution"
  echo "   (This is expected if artifact download failed or build-and-compile job didn't run)"
fi

# Build Maven command
# Solution 2: Skip checkstyle, formatting, and JMeter since they already run in dedicated jobs
MAVEN_CMD="./mvnw -ntp test"
MAVEN_CMD="$MAVEN_CMD -Dtest.environment=$ENVIRONMENT"
MAVEN_CMD="$MAVEN_CMD -Dtest.retry.max.count=$RETRY_COUNT"
MAVEN_CMD="$MAVEN_CMD -DsuiteXmlFile=$SUITE_FILE"

# Skip compilation if we successfully reused classes
if [ -d "target/classes" ] && [ -n "$(ls -A target/classes 2>/dev/null)" ] && [ -d "target/test-classes" ] && [ -n "$(ls -A target/test-classes 2>/dev/null)" ]; then
  echo "   Skipping compilation phases (classes already available)"
  MAVEN_CMD="$MAVEN_CMD -Dmaven.compiler.skip=true"
fi

# Skip checkstyle (runs in code-quality-analysis job)
MAVEN_CMD="$MAVEN_CMD -Dcheckstyle.skip=true"
# Skip formatting plugins (fmt-maven-plugin runs during format goal, not test)
MAVEN_CMD="$MAVEN_CMD -Dfmt.skip=true"
# Skip JMeter configuration (not needed for test execution)
# Note: JMeter plugin may not support skip, but we try anyway
MAVEN_CMD="$MAVEN_CMD -Djmeter.skip=true"

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

