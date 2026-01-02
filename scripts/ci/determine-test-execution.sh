#!/bin/bash
# scripts/ci/determine-test-execution.sh
# Determines what tests to run based on event type and inputs

set -e

EVENT_NAME=$1
REF=$2
TEST_TYPE_INPUT=$3
PERF_TYPE_INPUT=$4
PERF_ENV_INPUT=$5

# Determine default test type based on event
# Pull Requests: default to 'all' (FE + BE) in 'dev'
# Push to main/develop: default to 'fe-only' across 'all' envs
# Manual Runs: default to 'fe-only' (user can override)

IS_MAIN_PUSH=false
IS_BRANCH_PUSH=false
if [ "$EVENT_NAME" == "pull_request" ]; then
  IS_BRANCH_PUSH=true
  echo "🌿 Pull request detected - defaulting to ALL tests (FE + BE) in DEV"
elif [ "$EVENT_NAME" == "push" ]; then
  if [ "$REF" == "refs/heads/main" ]; then
    IS_MAIN_PUSH=true
    echo "🚀 Push to main detected - defaulting to ALL tests (FE + BE smoke) across ALL envs"
  else
    echo "📦 Develop push detected - defaulting to fe-only across ALL environments"
  fi
else
  echo "📦 Manual trigger or other event - defaulting to fe-only"
fi

if [ "$IS_BRANCH_PUSH" == "true" ]; then
  DEFAULT_TEST_TYPE="all"
  DEFAULT_PERF_ENV="dev"
  DEFAULT_PERF_TYPE="smoke"
elif [ "$IS_MAIN_PUSH" == "true" ]; then
  DEFAULT_TEST_TYPE="all"
  DEFAULT_PERF_ENV="dev-test"
  DEFAULT_PERF_TYPE="smoke"
else
  DEFAULT_TEST_TYPE="fe-only"
  DEFAULT_PERF_ENV="dev"
  DEFAULT_PERF_TYPE="smoke"
fi

# Use input if provided, otherwise use default
if [ -n "$TEST_TYPE_INPUT" ]; then
  TEST_TYPE="$TEST_TYPE_INPUT"
else
  TEST_TYPE="$DEFAULT_TEST_TYPE"
fi

if [ -n "$PERF_TYPE_INPUT" ]; then
  PERF_TYPE="$PERF_TYPE_INPUT"
else
  PERF_TYPE="$DEFAULT_PERF_TYPE"
fi

if [ -n "$PERF_ENV_INPUT" ]; then
  PERF_ENV="$PERF_ENV_INPUT"
else
  PERF_ENV="$DEFAULT_PERF_ENV"
fi

echo "📊 Test Type Selection: $TEST_TYPE"
echo "📊 BE Test Type: $PERF_TYPE"
echo "📊 BE Environment: $PERF_ENV"
echo "📊 Default Test Type: $DEFAULT_TEST_TYPE"
echo "📊 Default BE Env: $DEFAULT_PERF_ENV"

# BE tests NEVER run in prod
if [ "$PERF_ENV" == "prod" ]; then
  echo "⚠️  BE tests cannot run in prod, defaulting to dev"
  PERF_ENV="dev"
fi

# ⚠️ TEMPORARY: ALWAYS force UI tests to run regardless of test_type
# TODO: REVERT - Remove this temporary override before merging PR
# This ensures FE tests run in all environments during testing
if [ "$TEST_TYPE" == "fe-only" ] || [ -z "$TEST_TYPE" ]; then
  echo "run_ui_tests=true" >> $GITHUB_OUTPUT
  echo "run_be_tests=false" >> $GITHUB_OUTPUT
  echo "be_env_dev=false" >> $GITHUB_OUTPUT
  echo "be_env_test=false" >> $GITHUB_OUTPUT
  if [ -z "$TEST_TYPE" ]; then
    echo "✅ TEMPORARY: Will run FE tests only (no test_type input provided - forcing UI tests)"
  else
    echo "✅ Will run FE tests only"
  fi
elif [ "$TEST_TYPE" == "be-only" ]; then
  # ⚠️ TEMPORARY: Force UI tests even for be-only
  echo "run_ui_tests=true" >> $GITHUB_OUTPUT
  echo "run_be_tests=true" >> $GITHUB_OUTPUT
  echo "be_test_mode=$PERF_TYPE" >> $GITHUB_OUTPUT
  # Determine BE test environments
  if [ "$PERF_ENV" == "dev" ]; then
    echo "be_env_dev=true" >> $GITHUB_OUTPUT
    echo "be_env_test=false" >> $GITHUB_OUTPUT
  elif [ "$PERF_ENV" == "test" ]; then
    echo "be_env_dev=false" >> $GITHUB_OUTPUT
    echo "be_env_test=true" >> $GITHUB_OUTPUT
  elif [ "$PERF_ENV" == "dev-test" ]; then
    echo "be_env_dev=true" >> $GITHUB_OUTPUT
    echo "be_env_test=true" >> $GITHUB_OUTPUT
  else
    echo "be_env_dev=true" >> $GITHUB_OUTPUT
    echo "be_env_test=false" >> $GITHUB_OUTPUT
    echo "⚠️  Unknown be_environment, defaulting to dev"
  fi
  echo "✅ TEMPORARY: Will run BOTH FE and BE tests (be-only overridden to include FE)"
elif [ "$TEST_TYPE" == "all" ]; then
  echo "run_ui_tests=true" >> $GITHUB_OUTPUT
  echo "run_be_tests=true" >> $GITHUB_OUTPUT
  echo "be_test_mode=$PERF_TYPE" >> $GITHUB_OUTPUT
  # Determine BE test environments
  if [ "$PERF_ENV" == "dev" ]; then
    echo "be_env_dev=true" >> $GITHUB_OUTPUT
    echo "be_env_test=false" >> $GITHUB_OUTPUT
    echo "✅ BE tests will run in DEV environment"
  elif [ "$PERF_ENV" == "test" ]; then
    echo "be_env_dev=false" >> $GITHUB_OUTPUT
    echo "be_env_test=true" >> $GITHUB_OUTPUT
    echo "✅ BE tests will run in TEST environment"
  elif [ "$PERF_ENV" == "dev-test" ]; then
    echo "be_env_dev=true" >> $GITHUB_OUTPUT
    echo "be_env_test=true" >> $GITHUB_OUTPUT
    echo "✅ BE tests will run in DEV and TEST environments"
  else
    echo "be_env_dev=true" >> $GITHUB_OUTPUT
    echo "be_env_test=false" >> $GITHUB_OUTPUT
    echo "⚠️  Unknown be_environment '$PERF_ENV', defaulting to dev"
  fi
  echo "✅ Will run BOTH FE and BE tests in parallel"
  echo "   FE tests: all environments (default)"
  echo "   BE tests: $PERF_ENV ($PERF_TYPE)"
  echo "🔍 DEBUG: Set run_be_tests=true and be_env_dev=true for PERF_ENV=$PERF_ENV"
else
  # ⚠️ TEMPORARY: Force UI tests to run even for unknown test_type
  # TODO: REVERT - Remove this temporary override before merging PR
  echo "run_ui_tests=true" >> $GITHUB_OUTPUT
  echo "run_be_tests=false" >> $GITHUB_OUTPUT
  echo "be_env_dev=false" >> $GITHUB_OUTPUT
  echo "be_env_test=false" >> $GITHUB_OUTPUT
  echo "⚠️  Unknown test_type '$TEST_TYPE', defaulting to fe-only (TEMPORARY: forcing UI tests)"
fi

# ⚠️ TEMPORARY: Final override - ALWAYS ensure UI tests are enabled
# TODO: REVERT - Remove this final override before merging PR
echo "run_ui_tests=true" >> $GITHUB_OUTPUT
echo "⚠️  TEMPORARY OVERRIDE: Forced run_ui_tests=true (will be reverted before merge)"
