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
# Pull Requests: default to 'all' (FE + BE + FS) in 'dev'
# Push to feature branch: default to 'all' (FE + BE + FS) in 'dev' (same as PRs)
# Push to main: default to 'all' (FE + BE + FS) in 'dev' and 'test'
# Push to develop: default to 'fe-only' across 'all' envs
# Manual Runs: default to 'fe-only' (user can override)

IS_MAIN_PUSH=false
IS_BRANCH_PUSH=false
if [ "$EVENT_NAME" == "pull_request" ]; then
  IS_BRANCH_PUSH=true
  echo "🌿 Pull request detected - defaulting to ALL tests (FE + BE) in DEV"
elif [ "$EVENT_NAME" == "push" ]; then
  if [ "$REF" == "refs/heads/main" ]; then
    IS_MAIN_PUSH=true
    echo "🚀 Push to main detected - defaulting to ALL tests (FE + BE smoke) in DEV and TEST"
  elif [ "$REF" == "refs/heads/develop" ]; then
    echo "📦 Push to develop detected - defaulting to fe-only across ALL environments"
  else
    # Push to feature branch - run BE/FS tests in dev
    IS_BRANCH_PUSH=true
    echo "🌿 Push to feature branch detected - defaulting to ALL tests (FE + BE) in DEV"
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

if [ "$TEST_TYPE" == "fe-only" ] || [ -z "$TEST_TYPE" ]; then
  echo "run_ui_tests=true" >> $GITHUB_OUTPUT
  echo "run_be_tests=false" >> $GITHUB_OUTPUT
  echo "run_fs_tests=false" >> $GITHUB_OUTPUT
  echo "be_env_dev=false" >> $GITHUB_OUTPUT
  echo "be_env_test=false" >> $GITHUB_OUTPUT
  if [ -z "$TEST_TYPE" ]; then
    echo "✅ Will run FE tests only (no test_type input provided)"
  else
    echo "✅ Will run FE tests only"
  fi
elif [ "$TEST_TYPE" == "be-only" ]; then
  echo "run_ui_tests=false" >> $GITHUB_OUTPUT
  echo "run_be_tests=true" >> $GITHUB_OUTPUT
  echo "run_fs_tests=true" >> $GITHUB_OUTPUT
  echo "be_test_mode=$PERF_TYPE" >> $GITHUB_OUTPUT
  # Determine BE test environments (FS tests use same environments)
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
  echo "✅ Will run BE and FS tests only"
elif [ "$TEST_TYPE" == "all" ]; then
  echo "run_ui_tests=true" >> $GITHUB_OUTPUT
  echo "run_be_tests=true" >> $GITHUB_OUTPUT
  echo "run_fs_tests=true" >> $GITHUB_OUTPUT
  echo "be_test_mode=$PERF_TYPE" >> $GITHUB_OUTPUT
  # Determine BE test environments (FS tests use same environments)
  if [ "$PERF_ENV" == "dev" ]; then
    echo "be_env_dev=true" >> $GITHUB_OUTPUT
    echo "be_env_test=false" >> $GITHUB_OUTPUT
    echo "✅ BE and FS tests will run in DEV environment"
  elif [ "$PERF_ENV" == "test" ]; then
    echo "be_env_dev=false" >> $GITHUB_OUTPUT
    echo "be_env_test=true" >> $GITHUB_OUTPUT
    echo "✅ BE and FS tests will run in TEST environment"
  elif [ "$PERF_ENV" == "dev-test" ]; then
    echo "be_env_dev=true" >> $GITHUB_OUTPUT
    echo "be_env_test=true" >> $GITHUB_OUTPUT
    echo "✅ BE and FS tests will run in DEV and TEST environments"
  else
    echo "be_env_dev=true" >> $GITHUB_OUTPUT
    echo "be_env_test=false" >> $GITHUB_OUTPUT
    echo "⚠️  Unknown be_environment '$PERF_ENV', defaulting to dev"
  fi
  echo "✅ Will run BOTH FE and BE/FS tests in parallel"
  echo "   FE tests: all environments (default)"
  echo "   BE/FS tests: $PERF_ENV ($PERF_TYPE)"
  echo "🔍 DEBUG: Set run_be_tests=true, run_fs_tests=true and be_env_dev=true for PERF_ENV=$PERF_ENV"
else
  echo "run_ui_tests=true" >> $GITHUB_OUTPUT
  echo "run_be_tests=false" >> $GITHUB_OUTPUT
  echo "run_fs_tests=false" >> $GITHUB_OUTPUT
  echo "be_env_dev=false" >> $GITHUB_OUTPUT
  echo "be_env_test=false" >> $GITHUB_OUTPUT
  echo "⚠️  Unknown test_type '$TEST_TYPE', defaulting to fe-only"
fi
