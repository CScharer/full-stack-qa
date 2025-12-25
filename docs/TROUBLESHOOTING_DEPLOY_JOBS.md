# 🔧 Troubleshooting: Deploy Jobs Not Running

**Last Updated**: 2025-12-21  
**Note**: This document references `determine-schedule-type` which consolidates the `code-changed` output from `detect-changes`. All jobs now depend on `determine-schedule-type` instead of `detect-changes` directly.

## Issue
Deploy jobs (`deploy-dev`, `deploy-test`, `deploy-prod`) are not running after tests complete.

## Root Cause Analysis

### Problem 1: Reusable Workflow Result Status
When a reusable workflow is called, it returns:
- `'success'` - Only if ALL jobs in the reusable workflow succeed
- `'failure'` - If ANY job in the reusable workflow fails
- `'skipped'` - If the workflow was skipped
- `'cancelled'` - If the workflow was cancelled

**Issue**: Even if tests pass, if the Allure report job or test summary job fails, the reusable workflow returns `'failure'`, not `'success'`.

### Problem 2: Deploy Job Conditions
The original condition was:
```yaml
if: needs.test-dev-environment.result == 'success'
```

This means deploy only runs if the reusable workflow returns `'success'`, which requires ALL jobs to succeed.

## Solution Implemented

### Step 1: Updated Deploy Job Conditions
Changed from:
```yaml
if: needs.test-dev-environment.result == 'success'
```

To:
```yaml
if: |
  always() &&
  needs.determine-schedule-type.outputs.code-changed == 'true' &&
  needs.determine-environments.outputs.run_dev == 'true' &&
  (needs.test-dev-environment.result == 'success' || needs.test-dev-environment.result == 'failure') &&
  github.ref == 'refs/heads/main' &&
  github.event_name == 'push'
```

**Why**: This allows the deploy job to run if the workflow completed (success OR failure), not just success.

### Step 2: Added Test Result Verification
Added a verification step inside each deploy job that:
1. Downloads test result artifacts
2. Parses XML test results
3. Checks for failures/errors
4. Only proceeds with deployment if tests passed

```yaml
- name: Verify tests passed
  id: verify-tests
  run: |
    # Parse test results and check for failures/errors
    # Only proceed if tests passed
```

## Additional Checks

If deploy jobs still don't run, verify:

### 1. Branch and Event Type
Deploy jobs only run on:
- `github.ref == 'refs/heads/main'` (main branch only)
- `github.event_name == 'push'` (not on pull requests)

**Check**: Are you pushing to `main` branch, or is this a PR?

### 2. Code Changed Detection
Deploy jobs require:
- `needs.determine-schedule-type.outputs.code-changed == 'true'` (Note: `determine-schedule-type` consolidates the `code-changed` output from `detect-changes`)

**Check**: Are you making code changes, or only documentation?

### 3. Environment Selection
Deploy jobs require:
- `needs.determine-environments.outputs.run_dev == 'true'` (for dev)
- `needs.determine-environments.outputs.run_test == 'true'` (for test)
- `needs.determine-environments.outputs.run_prod == 'true'` (for prod)

**Check**: Is the environment selected correctly?

### 4. Reusable Workflow Status
Check the actual result status in GitHub Actions:
- Go to Actions tab
- Click on the workflow run
- Check the `test-dev-environment` job status
- Look at the result: `success`, `failure`, `skipped`, or `cancelled`

## Debugging Steps

### Step 1: Check Workflow Run
1. Go to GitHub Actions
2. Find the workflow run
3. Check if `test-dev-environment` job completed
4. Note the result status

### Step 2: Check Deploy Job Status
1. Look for `deploy-dev` job
2. If it's skipped, click on it
3. Check the "Why was this job skipped?" message
4. This will show which condition failed

### Step 3: Enable Debug Logging
Add to workflow file (temporarily):
```yaml
env:
  ACTIONS_STEP_DEBUG: true
  ACTIONS_RUNNER_DEBUG: true
```

This will show detailed information about condition evaluation.

### Step 4: Check Artifact Availability
Verify test artifacts are being uploaded:
1. Go to workflow run
2. Check "Artifacts" section
3. Look for `smoke-results-dev`, `grid-results-chrome-dev`, etc.
4. If artifacts are missing, deploy job can't verify tests

## Alternative Solution (If Still Not Working)

If the above doesn't work, we can add a final "test-success" job in the reusable workflow that only runs if all test jobs succeed, and check that job's result instead.

## Current Status

**Fixed in**: `fix/deploy-job-conditions` branch
**Changes**:
- ✅ Allow 'failure' status from reusable workflow
- ✅ Added test result verification in deploy jobs
- ✅ Updated BASE_URL_PROD to https://yourapp.com

**Next Steps**:
1. Merge the fix branch
2. Test with a push to main
3. Verify deploy jobs run
4. Check that deployment only proceeds if tests passed

---

**Related**: GitHub Actions documentation on [reusable workflows](https://docs.github.com/en/actions/using-workflows/reusing-workflows) and [job conditions](https://docs.github.com/en/actions/using-workflows/workflow-syntax-for-github-actions#jobsjob_idif)

