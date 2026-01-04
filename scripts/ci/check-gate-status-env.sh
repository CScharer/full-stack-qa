#!/bin/bash
# scripts/ci/check-gate-status-env.sh
# Checks gate status for a specific environment (DEV, TEST, or PROD)
# This script consolidates the complex gate checking logic that was repeated 3 times

set -e

ENVIRONMENT="$1"
shift  # Remove first argument, rest are job:result pairs

if [ -z "$ENVIRONMENT" ]; then
  echo "❌ Error: Environment not specified"
  exit 1
fi

ENVIRONMENT_UPPER=$(echo "$ENVIRONMENT" | tr '[:lower:]' '[:upper:]')

echo "🔍 Checking ${ENVIRONMENT_UPPER} testing jobs status..."

# Parse all job:result pairs into associative array
declare -A JOB_RESULTS
declare -A JOB_OUTPUTS

while [ $# -gt 0 ]; do
  IFS=':' read -r KEY VALUE <<< "$1"
  if [[ "$KEY" == *"."* ]]; then
    # This is an output (e.g., determine-environments.run_dev)
    JOB_OUTPUTS["$KEY"]="$VALUE"
  else
    # This is a result (e.g., gate-setup:success)
    JOB_RESULTS["$KEY"]="$VALUE"
  fi
  shift
done

# Print status information
case "$ENVIRONMENT" in
  dev)
    echo "  Setup Gate: ${JOB_RESULTS[gate-setup]:-unknown}"
    echo "  FE Tests (DEV): ${JOB_RESULTS[test-fe-dev]:-unknown}"
    echo "  BE Tests (DEV): ${JOB_RESULTS[test-be-dev]:-unknown}"
    echo "📊 BE Test Mode: ${JOB_OUTPUTS[determine-test-execution.be_test_mode]:-unknown}"
    echo "📊 Run BE Tests: ${JOB_OUTPUTS[determine-test-execution.run_be_tests]:-unknown}"
    echo "📊 BE Env DEV: ${JOB_OUTPUTS[determine-test-execution.be_env_dev]:-unknown}"
    ;;
  test)
    echo "  DEV Gate: ${JOB_RESULTS[gate-dev]:-unknown}"
    echo "  FE Tests (TEST): ${JOB_RESULTS[test-fe-test]:-unknown}"
    echo "  BE Tests (TEST): ${JOB_RESULTS[test-be-test]:-unknown}"
    echo "📊 BE Test Mode: ${JOB_OUTPUTS[determine-test-execution.be_test_mode]:-unknown}"
    echo "📊 Run BE Tests: ${JOB_OUTPUTS[determine-test-execution.run_be_tests]:-unknown}"
    echo "📊 BE Env TEST: ${JOB_OUTPUTS[determine-test-execution.be_env_test]:-unknown}"
    ;;
  prod)
    echo "  TEST Gate: ${JOB_RESULTS[gate-test]:-unknown}"
    echo "  FE Tests (PROD): ${JOB_RESULTS[test-fe-prod]:-unknown}"
    ;;
esac

FAILED_JOBS=""

# Helper function to check if a job failed or timed out
check_status() {
  local job_name=$1
  local result=$2
  if [ "$result" == "failure" ] || [ "$result" == "timed_out" ]; then
    FAILED_JOBS="${FAILED_JOBS} ${job_name}"
  fi
}

# Check previous gate (if applicable)
case "$ENVIRONMENT" in
  dev)
    check_status "gate-setup" "${JOB_RESULTS[gate-setup]:-}"
    
    # Check FE Tests
    if [ "${JOB_RESULTS[test-fe-dev]:-}" == "failure" ] || [ "${JOB_RESULTS[test-fe-dev]:-}" == "timed_out" ]; then
      FAILED_JOBS="${FAILED_JOBS} test-fe-dev"
    elif [ "${JOB_RESULTS[test-fe-dev]:-}" == "skipped" ]; then
      # Only expect test to run if code-changed is true (documentation-only changes skip tests)
      if [ "${JOB_OUTPUTS[determine-schedule-type.code-changed]:-}" == "true" ] && \
         [ "${JOB_OUTPUTS[determine-envs.run_dev]:-}" == "true" ] && \
         [ "${JOB_OUTPUTS[determine-test-execution.run_ui_tests]:-}" == "true" ] && \
         [ "${JOB_RESULTS[gate-setup]:-}" == "success" ]; then
        echo "❌ ERROR: test-fe-dev was skipped but was expected to run"
        FAILED_JOBS="${FAILED_JOBS} test-fe-dev(unexpected-skip)"
      fi
    fi
    
    # Check BE Tests
    if [ "${JOB_RESULTS[test-be-dev]:-}" == "failure" ] || [ "${JOB_RESULTS[test-be-dev]:-}" == "timed_out" ]; then
      FAILED_JOBS="${FAILED_JOBS} test-be-dev"
    elif [ "${JOB_RESULTS[test-be-dev]:-}" == "skipped" ]; then
      # Only expect test to run if code-changed is true (documentation-only changes skip tests)
      if [ "${JOB_OUTPUTS[determine-schedule-type.code-changed]:-}" == "true" ] && \
         [ "${JOB_OUTPUTS[determine-envs.run_dev]:-}" == "true" ] && \
         [ "${JOB_OUTPUTS[determine-test-execution.run_be_tests]:-}" == "true" ] && \
         [ "${JOB_OUTPUTS[determine-test-execution.be_env_dev]:-}" == "true" ] && \
         [ "${JOB_RESULTS[gate-setup]:-}" == "success" ]; then
        echo "❌ ERROR: test-be-dev was skipped but was expected to run"
        FAILED_JOBS="${FAILED_JOBS} test-be-dev(unexpected-skip)"
      fi
    fi
    ;;
  test)
    check_status "gate-dev" "${JOB_RESULTS[gate-dev]:-}"
    
    # Check FE Tests
    if [ "${JOB_RESULTS[test-fe-test]:-}" == "failure" ] || [ "${JOB_RESULTS[test-fe-test]:-}" == "timed_out" ]; then
      FAILED_JOBS="${FAILED_JOBS} test-fe-test"
    elif [ "${JOB_RESULTS[test-fe-test]:-}" == "skipped" ]; then
      # Only expect test to run if code-changed is true (documentation-only changes skip tests)
      if [ "${JOB_OUTPUTS[determine-schedule-type.code-changed]:-}" == "true" ] && \
         [ "${JOB_OUTPUTS[determine-envs.run_test]:-}" == "true" ] && \
         [ "${JOB_OUTPUTS[determine-test-execution.run_ui_tests]:-}" == "true" ] && \
         [ "${JOB_RESULTS[gate-dev]:-}" == "success" ]; then
        echo "❌ ERROR: test-fe-test was skipped but was expected to run"
        FAILED_JOBS="${FAILED_JOBS} test-fe-test(unexpected-skip)"
      fi
    fi
    
    # Check BE Tests
    if [ "${JOB_RESULTS[test-be-test]:-}" == "failure" ] || [ "${JOB_RESULTS[test-be-test]:-}" == "timed_out" ]; then
      FAILED_JOBS="${FAILED_JOBS} test-be-test"
    elif [ "${JOB_RESULTS[test-be-test]:-}" == "skipped" ]; then
      # Only expect test to run if code-changed is true (documentation-only changes skip tests)
      if [ "${JOB_OUTPUTS[determine-schedule-type.code-changed]:-}" == "true" ] && \
         [ "${JOB_OUTPUTS[determine-envs.run_test]:-}" == "true" ] && \
         [ "${JOB_OUTPUTS[determine-test-execution.run_be_tests]:-}" == "true" ] && \
         [ "${JOB_OUTPUTS[determine-test-execution.be_env_test]:-}" == "true" ] && \
         [ "${JOB_RESULTS[gate-dev]:-}" == "success" ]; then
        echo "❌ ERROR: test-be-test was skipped but was expected to run"
        FAILED_JOBS="${FAILED_JOBS} test-be-test(unexpected-skip)"
      fi
    fi
    ;;
  prod)
    check_status "gate-test" "${JOB_RESULTS[gate-test]:-}"
    
    # Check FE Tests
    if [ "${JOB_RESULTS[test-fe-prod]:-}" == "failure" ] || [ "${JOB_RESULTS[test-fe-prod]:-}" == "timed_out" ]; then
      FAILED_JOBS="${FAILED_JOBS} test-fe-prod"
    elif [ "${JOB_RESULTS[test-fe-prod]:-}" == "skipped" ]; then
      # Only expect test to run if code-changed is true (documentation-only changes skip tests)
      if [ "${JOB_OUTPUTS[determine-schedule-type.code-changed]:-}" == "true" ] && \
         [ "${JOB_OUTPUTS[determine-envs.run_prod]:-}" == "true" ] && \
         [ "${JOB_RESULTS[gate-test]:-}" == "success" ]; then
        echo "❌ ERROR: test-fe-prod was skipped but was expected to run"
        FAILED_JOBS="${FAILED_JOBS} test-fe-prod(unexpected-skip)"
      fi
    fi
    ;;
esac

if [ -n "$FAILED_JOBS" ]; then
  echo "❌ ${ENVIRONMENT_UPPER} gate FAILED - The following jobs failed or were unexpectedly skipped:$FAILED_JOBS"
  exit 1
else
  echo "✅ ${ENVIRONMENT_UPPER} gate PASSED - All ${ENVIRONMENT_UPPER} testing jobs completed successfully"
fi
