#!/bin/bash
# scripts/ci/setup-base-urls.sh
# Sets up base URLs for DEV, TEST, and PROD environments with fallback logic

set -e

BASE_URL_DEV="${1:-}"
BASE_URL_TEST="${2:-}"
BASE_URL_PROD="${3:-}"

# Fallback to defaults if vars are empty
if [ -z "$BASE_URL_DEV" ]; then
  BASE_URL_DEV="http://localhost:3003"
fi
if [ -z "$BASE_URL_TEST" ]; then
  BASE_URL_TEST="http://localhost:3004"
fi
if [ -z "$BASE_URL_PROD" ]; then
  BASE_URL_PROD="http://localhost:3005"
fi

# Output to GITHUB_OUTPUT
echo "base_url_dev=$BASE_URL_DEV" >> $GITHUB_OUTPUT
echo "base_url_test=$BASE_URL_TEST" >> $GITHUB_OUTPUT
echo "base_url_prod=$BASE_URL_PROD" >> $GITHUB_OUTPUT

echo "✅ Base URLs configured:"
echo "  DEV:  $BASE_URL_DEV"
echo "  TEST: $BASE_URL_TEST"
echo "  PROD: $BASE_URL_PROD"
