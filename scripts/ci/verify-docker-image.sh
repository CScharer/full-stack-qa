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

# Test that the container can run and execute mvnw
echo "Testing container execution..."
docker run --rm --entrypoint="/bin/bash" "$IMAGE_NAME" -c "cd /app && ./mvnw --version"

echo "✅ Docker image verification successful"
