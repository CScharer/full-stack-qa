# Docker Pull Timeout Fix for Chrome Grid Tests

**Date**: January 16, 2026  
**Branch**: `fix/docker-pull-retry`  
**Status**: 🔄 **IN PROGRESS** - Awaiting Approval

---

## Problem Statement

Chrome Grid Tests are failing in CI/CD pipeline due to Docker Hub timeout errors when pulling Selenium images:

```
Error response from daemon: Head "https://registry-1.docker.io/v2/selenium/hub/manifests/4.39.0": 
Get "https://auth.docker.io/token?account=githubactions&scope=repository%3Aselenium%2Fhub%3Apull&service=registry.docker.io": 
net/http: request canceled (Client.Timeout exceeded while awaiting headers)
```

**Impact**:
- Chrome Grid Tests job fails immediately
- Other grid tests (Firefox, Edge) may also be affected
- Network timeouts are transient but cause test failures

**Root Cause**:
- GitHub Actions service containers attempt to pull Docker images automatically
- Docker Hub authentication/network timeouts occur intermittently
- No retry logic exists for service container image pulls
- Service containers start in parallel, competing for network resources

---

## Solution Approach

### Strategy: Pre-pull Images with Retry Logic

Add a step **before** service containers start that:
1. Pre-pulls all required Selenium Docker images
2. Implements exponential backoff retry logic (5 attempts)
3. Uses `continue-on-error: true` to not block the job if pre-pull fails
4. Caches images locally to reduce service container pull time

### Benefits:
- ✅ Images cached locally before service containers need them
- ✅ Retry logic handles transient network issues
- ✅ Exponential backoff prevents overwhelming Docker Hub
- ✅ Non-blocking (job continues even if pre-pull fails)
- ✅ Service containers can still pull if pre-pull fails

---

## Implementation Details

### Files Modified

1. **`.github/workflows/env-fe.yml`**
   - **Location**: `grid-tests` job, after `Checkout code` step
   - **Change**: Add "Pre-pull Selenium Docker images with retry" step

### Implementation

```yaml
- name: Pre-pull Selenium Docker images with retry
  continue-on-error: true
  timeout-minutes: 2
  run: |
    echo "🐳 Pre-pulling Selenium Docker images with retry logic..."
    SELENIUM_VERSION="${{ inputs.selenium_version || '4.39.0' }}"
    
    # Function to pull with retry
    pull_with_retry() {
      local image=$1
      local max_attempts=3
      local attempt=1
      local backoff=2
      
      while [ $attempt -le $max_attempts ]; do
        echo "Attempt $attempt/$max_attempts: Pulling $image..."
        if docker pull "$image" --quiet; then
          echo "✅ Successfully pulled $image"
          return 0
        else
          echo "⚠️  Failed to pull $image (attempt $attempt/$max_attempts)"
          if [ $attempt -lt $max_attempts ]; then
            echo "⏳ Waiting ${backoff}s before retry..."
            sleep $backoff
            backoff=$((backoff * 2))  # Exponential backoff
          fi
          attempt=$((attempt + 1))
        fi
      done
      
      echo "❌ Failed to pull $image after $max_attempts attempts"
      return 1
    }
    
    # Pull all required images
    pull_with_retry "selenium/hub:${SELENIUM_VERSION}" || true
    pull_with_retry "selenium/node-chrome:${SELENIUM_VERSION}" || true
    pull_with_retry "selenium/node-firefox:${SELENIUM_VERSION}" || true
    pull_with_retry "selenium/node-edge:${SELENIUM_VERSION}" || true
    
    echo "✅ Pre-pull step completed (images may be cached for service containers)"
```

### Retry Logic Details

- **Max Attempts**: 3
- **Backoff Strategy**: Exponential (2s, 4s)
- **Pull Time**: ~30-60 seconds per attempt (if slow)
- **Total Max Time**: ~2-3 minutes per image worst case (if all retries fail)
- **Step Timeout**: 2 minutes (fails fast if Docker Hub is unavailable, allows retries for transient issues)
- **Images Pulled**:
  - `selenium/hub:4.39.0`
  - `selenium/node-chrome:4.39.0`
  - `selenium/node-firefox:4.39.0`
  - `selenium/node-edge:4.39.0`

### Error Handling

- Each image pull uses `|| true` to not fail the step
- Step uses `continue-on-error: true` to not fail the job
- Service containers will still attempt their own pull if pre-pull fails
- Job continues normally even if pre-pull step fails completely

---

## Testing Plan

### Before Implementation
- [x] Identify the failing job: `grid-tests` (Chrome)
- [x] Confirm error: Docker Hub timeout during image pull
- [x] Verify affected images: `selenium/hub:4.39.0`

### After Implementation
- [ ] Verify pre-pull step runs before service containers start
- [ ] Test retry logic with simulated network failure
- [ ] Verify job continues if pre-pull fails
- [ ] Monitor Chrome Grid Tests job for reduced timeout errors
- [ ] Check that service containers can still pull if pre-pull fails

### Success Criteria
- ✅ Chrome Grid Tests job completes successfully
- ✅ Reduced frequency of Docker Hub timeout errors
- ✅ Pre-pull step logs show retry attempts when needed
- ✅ No regression in other test jobs

---

## Risk Assessment

### Low Risk ✅
- **Non-breaking**: Uses `continue-on-error: true`
- **Backward Compatible**: Service containers still work if pre-pull fails
- **Isolated**: Only affects `grid-tests` job
- **Reversible**: Easy to remove if issues arise

### Potential Issues
- ⚠️ **Increased job time**: Adds ~10-60 seconds if images need to be pulled
- ⚠️ **Docker Hub rate limiting**: Multiple retries might hit rate limits (unlikely with backoff)
- ⚠️ **Network still fails**: If Docker Hub is completely down, service containers will still fail

### Mitigation
- Pre-pull has 2-minute timeout (should complete in <30 seconds normally)
- Exponential backoff reduces rate limit risk
- `continue-on-error` ensures job continues even if pre-pull fails

---

## Alternative Approaches Considered

### 1. Increase Service Container Timeout
- ❌ **Rejected**: Cannot configure service container timeouts directly
- Service containers are managed by GitHub Actions

### 2. Use Different Image Registry
- ❌ **Rejected**: Would require changing all Selenium image references
- Docker Hub is the official registry for Selenium images

### 3. Pre-pull in Separate Job
- ⚠️ **Considered**: Could cache images in a separate job
- ❌ **Rejected**: More complex, requires artifact caching, may not help service containers

### 4. Use Image Caching Action
- ⚠️ **Considered**: GitHub Actions image caching
- ❌ **Rejected**: Service containers don't benefit from cached images in same way

### 5. Pre-pull with Retry (Selected) ✅
- ✅ **Selected**: Simple, effective, non-blocking
- Works with service containers
- Handles transient network issues

---

## Related Issues

- **Primary Issue**: Chrome Grid Tests failing due to Docker Hub timeout
- **Affected Jobs**: `grid-tests` (all browsers: chrome, firefox, edge)
- **Workflow**: `.github/workflows/env-fe.yml`
- **Service Containers**: `selenium-hub`, `chrome-node`, `firefox-node`, `edge-node`

---

## Implementation Checklist

- [x] Create feature branch: `fix/docker-pull-retry`
- [x] Create working document
- [x] Implement pre-pull step with retry logic
- [ ] **AWAITING APPROVAL** - Do not stage or commit
- [ ] After approval: Stage changes
- [ ] After approval: Commit changes
- [ ] After approval: Push branch
- [ ] After approval: Create PR
- [ ] Monitor pipeline after merge

---

## Notes

- This fix addresses **transient network issues** with Docker Hub
- If Docker Hub is completely down, service containers will still fail (expected behavior)
- The pre-pull step is **optimistic** - it helps when network is slow/unstable but not when completely down
- Consider monitoring Docker Hub status if issues persist

---

**Last Updated**: January 16, 2026  
**Next Steps**: Awaiting user approval to proceed with staging and committing
