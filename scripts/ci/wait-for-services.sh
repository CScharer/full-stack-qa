#!/bin/bash
# scripts/ci/wait-for-services.sh
# Waits for services to be ready with retry logic

set -e

FRONTEND_URL=$1
BACKEND_URL=$2
MAX_ATTEMPTS=${3:-30}
ENVIRONMENT=${4:-"unknown"}

echo "⏳ Waiting for services to be ready ($ENVIRONMENT)..."
echo "  Frontend: $FRONTEND_URL"
echo "  Backend: $BACKEND_URL"

READY=false
for i in $(seq 1 $MAX_ATTEMPTS); do
  if curl -sf "$FRONTEND_URL" >/dev/null 2>&1 && curl -sf "$BACKEND_URL/health" >/dev/null 2>&1; then
    echo "✅ Services are ready!"
    READY=true
    break
  fi
  echo "Waiting... ($i/$MAX_ATTEMPTS)"
  sleep 2
done

if [ "$READY" = "false" ]; then
  echo "❌ Services failed to start within the time limit"
  echo "Frontend: $FRONTEND_URL"
  echo "Backend: $BACKEND_URL"
  exit 1
fi
