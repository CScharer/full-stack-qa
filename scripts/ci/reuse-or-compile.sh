#!/bin/bash
# scripts/ci/reuse-or-compile.sh
# Reuses compiled classes from Docker build if available, otherwise compiles

set -e

DOCKER_CLASSES_DIR=${1:-"docker-compiled-classes/target"}

echo "🔍 Checking for compiled classes from Docker build..."

# Check if compiled classes exist and are valid
if [ -d "$DOCKER_CLASSES_DIR" ] && [ -n "$(ls -A "$DOCKER_CLASSES_DIR/classes" 2>/dev/null)" ]; then
  echo "✅ Found compiled classes from Docker build"
  echo "📦 Copying compiled classes to target directory..."
  
  mkdir -p target
  cp -r "$DOCKER_CLASSES_DIR"/* target/ || {
    echo "⚠️  Failed to copy classes, falling back to compilation"
    ./mvnw -ntp compile test-compile
    exit 0
  }
  
  # Verify we have the essential compiled classes
  if [ ! -d "target/classes" ] || [ -z "$(ls -A target/classes 2>/dev/null)" ]; then
    echo "⚠️  Extracted classes incomplete, falling back to compilation"
    ./mvnw -ntp compile test-compile
  else
    echo "✅ Compiled classes successfully reused from Docker build"
    
    # Still run test-compile if test classes are missing
    if [ ! -d "target/test-classes" ] || [ -z "$(ls -A target/test-classes 2>/dev/null)" ]; then
      echo "📝 Compiling test classes only..."
      ./mvnw -ntp test-compile
    else
      echo "✅ Test classes also available from Docker build"
    fi
  fi
else
  echo "⚠️  No compiled classes found from Docker build, compiling from scratch"
  ./mvnw -ntp compile test-compile
fi

echo "✅ Compilation step completed"
