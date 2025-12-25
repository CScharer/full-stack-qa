#!/bin/bash
# scripts/ci/determine-ports.sh
# Determines ports and URLs based on environment

set -e

ENVIRONMENT=$1

if [ "$ENVIRONMENT" == "dev" ]; then
  echo "frontend_port=3003" >> $GITHUB_OUTPUT
  echo "api_port=8003" >> $GITHUB_OUTPUT
  echo "backend_url=http://localhost:8003" >> $GITHUB_OUTPUT
  echo "frontend_url=http://localhost:3003" >> $GITHUB_OUTPUT
elif [ "$ENVIRONMENT" == "test" ]; then
  echo "frontend_port=3004" >> $GITHUB_OUTPUT
  echo "api_port=8004" >> $GITHUB_OUTPUT
  echo "backend_url=http://localhost:8004" >> $GITHUB_OUTPUT
  echo "frontend_url=http://localhost:3004" >> $GITHUB_OUTPUT
else
  echo "❌ Unknown environment: $ENVIRONMENT"
  exit 1
fi

echo "✅ Determined ports for environment: $ENVIRONMENT"
