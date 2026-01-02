#!/bin/bash
# scripts/ci/determine-environments.sh
# Determines which environments to run based on event type and inputs

set -e

EVENT_NAME=$1
ENV_INPUT=$2
SUITE_INPUT=$3

# Determine default environment based on event
# Pull Requests: default to 'dev' only (feature branch testing)
# Push to main/develop: default to 'all' (merge testing)
# Manual Runs: default to 'all' (user can override)

# ⚠️ TEMPORARY CHANGE FOR TESTING FALLBACK LOGIC FIX ⚠️
# This temporarily makes all branch pushes run all environments (dev, test, prod)
# to test the fix for identical results across environments.
# TODO: REVERT THIS BEFORE MERGING THE PR - uncomment original logic below
IS_BRANCH_PUSH=false
if [ "$EVENT_NAME" == "pull_request" ]; then
  IS_BRANCH_PUSH=true
  echo "🌿 Pull request detected - defaulting to DEV environment only"
elif [ "$EVENT_NAME" == "push" ]; then
  # TEMPORARY: Run all environments on any push to test fallback logic fix
  echo "📦 Push detected - TEMPORARILY running ALL environments for fallback logic testing"
  echo "⚠️  WARNING: This is a temporary change - revert before merging PR"
  IS_BRANCH_PUSH=false  # Force 'all' environments
  # ORIGINAL CODE (commented out for testing - uncomment before merge):
  # echo "📦 Main/develop push detected - defaulting to ALL environments"
  # # Original logic would check if branch is main/develop here
else
  if [ "$EVENT_NAME" == "workflow_dispatch" ]; then
    echo "📦 Manual trigger - defaulting to ALL environments (inputs can override)"
  else
    echo "❓ Unknown event '$EVENT_NAME' - defaulting to DEV only for safety"
    IS_BRANCH_PUSH=true
  fi
fi

if [ "$IS_BRANCH_PUSH" == "true" ]; then
  DEFAULT_ENV="dev"
else
  DEFAULT_ENV="all"
fi

# Use input if provided, otherwise use default
if [ -n "$ENV_INPUT" ]; then
  ENV_SELECT="$ENV_INPUT"
else
  ENV_SELECT="$DEFAULT_ENV"
fi

if [ -n "$SUITE_INPUT" ]; then
  SUITE_SELECT="$SUITE_INPUT"
else
  SUITE_SELECT="smoke"
fi

echo "📊 Environment Selection: $ENV_SELECT"
echo "📊 Test Suite Selection: $SUITE_SELECT"
echo "selected_env=$ENV_SELECT" >> $GITHUB_OUTPUT
echo "test_suite=$SUITE_SELECT" >> $GITHUB_OUTPUT

# Set test execution controls (same for all environments)
# Defaults match env-fe.yml defaults:
# - smoke/grid/mobile/responsive: false (disabled by default)
# - cypress/playwright/robot/selenide/vibium: true (enabled by default)
# These can be customized based on event type, schedule, or other conditions
echo "enable_smoke_tests=false" >> $GITHUB_OUTPUT
echo "enable_grid_tests=false" >> $GITHUB_OUTPUT
echo "enable_mobile_tests=false" >> $GITHUB_OUTPUT
echo "enable_responsive_tests=false" >> $GITHUB_OUTPUT
echo "enable_cypress_tests=true" >> $GITHUB_OUTPUT
echo "enable_playwright_tests=true" >> $GITHUB_OUTPUT
echo "enable_robot_tests=true" >> $GITHUB_OUTPUT
echo "enable_selenide_tests=true" >> $GITHUB_OUTPUT
echo "enable_vibium_tests=true" >> $GITHUB_OUTPUT
echo "✅ Test execution controls set (same for all environments)"

if [ "$ENV_SELECT" == "all" ]; then
  echo "run_dev=true" >> $GITHUB_OUTPUT
  echo "run_test=true" >> $GITHUB_OUTPUT
  echo "run_prod=true" >> $GITHUB_OUTPUT
  echo "✅ Will run ALL environments sequentially: DEV → TEST → PROD"
elif [ "$ENV_SELECT" == "dev" ]; then
  echo "run_dev=true" >> $GITHUB_OUTPUT
  echo "run_test=false" >> $GITHUB_OUTPUT
  echo "run_prod=false" >> $GITHUB_OUTPUT
  echo "✅ Will run DEV environment only"
elif [ "$ENV_SELECT" == "test" ]; then
  echo "run_dev=false" >> $GITHUB_OUTPUT
  echo "run_test=true" >> $GITHUB_OUTPUT
  echo "run_prod=false" >> $GITHUB_OUTPUT
  echo "✅ Will run TEST environment only"
elif [ "$ENV_SELECT" == "prod" ]; then
  echo "run_dev=false" >> $GITHUB_OUTPUT
  echo "run_test=false" >> $GITHUB_OUTPUT
  echo "run_prod=true" >> $GITHUB_OUTPUT
  echo "✅ Will run PROD environment only"
elif [ "$ENV_SELECT" == "dev-test" ]; then
  echo "run_dev=true" >> $GITHUB_OUTPUT
  echo "run_test=true" >> $GITHUB_OUTPUT
  echo "run_prod=false" >> $GITHUB_OUTPUT
  echo "✅ Will run DEV and TEST environments only (no PROD)"
else
  echo "run_dev=true" >> $GITHUB_OUTPUT
  echo "run_test=false" >> $GITHUB_OUTPUT
  echo "run_prod=false" >> $GITHUB_OUTPUT
  echo "⚠️  Unknown selection '$ENV_SELECT', defaulting to DEV only (safer for branch pushes)"
fi
