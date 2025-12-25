#!/bin/bash
# scripts/ci/check-gate-status.sh
# Checks job statuses for a gate and fails if any critical jobs failed

set -e

GATE_NAME=$1
shift  # Remove first argument, rest are job_name:result pairs

echo "🔍 Checking $GATE_NAME jobs status..."

FAILED_JOBS=""

# Process each job_name:result pair
while [ $# -gt 0 ]; do
  JOB_NAME=$(echo "$1" | cut -d':' -f1)
  JOB_RESULT=$(echo "$1" | cut -d':' -f2)
  
  echo "$JOB_NAME: $JOB_RESULT"
  
  # Only fail if jobs actually failed or timed out.
  # Cancelled jobs (e.g. due to concurrency) should not trigger a "Failure" notification in the gate.
  if [ "$JOB_RESULT" == "failure" ] || [ "$JOB_RESULT" == "timed_out" ]; then
    FAILED_JOBS="${FAILED_JOBS} ${JOB_NAME}"
  fi
  
  shift
done

if [ -n "$FAILED_JOBS" ]; then
  echo "❌ $GATE_NAME FAILED - The following jobs failed:$FAILED_JOBS"
  exit 1
else
  echo "✅ $GATE_NAME PASSED - All jobs completed successfully (or were skipped/cancelled without failing)"
fi
