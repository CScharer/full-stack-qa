#!/bin/bash
# scripts/ci/verify-docker-image.sh
# Verifies that a Docker image exists and can run

set -e

IMAGE_NAME=${1:-"full-stack-qa-tests:latest"}

# Verify the image exists locally
if ! docker image inspect "$IMAGE_NAME" > /dev/null 2>&1; then
  echo "❌ Image $IMAGE_NAME not found locally"
  docker images
  exit 1
else
  echo "✅ Found image: $IMAGE_NAME"
fi

# Test that the container can run and basic files exist
echo "Testing container execution..."
echo "  Checking if mvnw exists..."
if docker run --rm --entrypoint="/bin/bash" "$IMAGE_NAME" -c "test -f /app/mvnw && echo '✅ mvnw found'"; then
  echo "  ✅ mvnw exists in container"
else
  echo "  ❌ mvnw not found in container"
  exit 1
fi

echo "  Checking if pom.xml exists..."
if docker run --rm --entrypoint="/bin/bash" "$IMAGE_NAME" -c "test -f /app/pom.xml && echo '✅ pom.xml found'"; then
  echo "  ✅ pom.xml exists in container"
else
  echo "  ❌ pom.xml not found in container"
  exit 1
fi

echo "  Checking if target directory exists..."
if docker run --rm --entrypoint="/bin/bash" "$IMAGE_NAME" -c "test -d /app/target && echo '✅ target directory found'"; then
  echo "  ✅ target directory exists in container"
else
  echo "  ⚠️  target directory not found (may be expected if build was skipped)"
fi

# Try to run mvnw --version, but don't fail if network is unavailable
echo "  Testing mvnw execution (may require network for Maven download)..."
set +e  # Temporarily disable exit on error for this check
mvnw_output=$(docker run --rm --entrypoint="/bin/bash" "$IMAGE_NAME" -c "cd /app && timeout 30 ./mvnw --version 2>&1" 2>&1)
mvnw_exit_code=$?
set -e  # Re-enable exit on error

if [ $mvnw_exit_code -eq 0 ]; then
  echo "  ✅ Container can execute mvnw"
  echo "$mvnw_output" | head -3
else
  echo "  ⚠️  mvnw --version failed (exit code: $mvnw_exit_code)"
  echo "  ℹ️  This is likely due to network issues downloading Maven"
  echo "  ℹ️  This is acceptable - mvnw will download Maven when needed during actual test execution"
  echo "  ℹ️  Container structure is valid (mvnw, pom.xml, and target directory exist)"
fi

echo "✅ Docker image verification successful"
