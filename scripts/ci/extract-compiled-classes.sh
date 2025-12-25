#!/bin/bash
# scripts/ci/extract-compiled-classes.sh
# Extracts compiled classes from Docker image to reuse in build-and-compile job

set -e

IMAGE_NAME=${1:-"full-stack-qa-tests:latest"}
OUTPUT_DIR=${2:-"docker-compiled-classes"}

echo "📦 Extracting compiled classes from Docker image: $IMAGE_NAME"

# Verify the image exists
if ! docker image inspect "$IMAGE_NAME" > /dev/null 2>&1; then
  echo "❌ Image $IMAGE_NAME not found locally"
  docker images
  exit 1
fi

# Create output directory
mkdir -p "$OUTPUT_DIR"

# Create a temporary container to extract the target directory
CONTAINER_ID=$(docker create "$IMAGE_NAME")

# Copy the target directory from the container
if docker cp "$CONTAINER_ID:/app/target" "$OUTPUT_DIR/" 2>/dev/null; then
  echo "✅ Successfully extracted compiled classes from Docker build"
  docker rm "$CONTAINER_ID"
  exit 0
else
  echo "⚠️  Could not extract target directory from container"
  docker rm "$CONTAINER_ID" 2>/dev/null || true
  exit 1
fi
