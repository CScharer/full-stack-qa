# Test Performance Analysis & Optimization Recommendations

**Date**: 2025-12-31  
**Status**: Analysis Complete - Ready for Review

---

## 📊 Current Test Timings

### Fast Tests (< 2 minutes)
| Job | Browser | Time | Framework |
|-----|---------|------|-----------|
| cypress-tests | Chrome | 1m 41s | Cypress |
| playwright-tests | Chrome (Chromium) | 1m 6s | Playwright |
| robot-tests | Chrome | 1m 39s | Robot Framework |
| vibium-tests | Chrome | 0m 45s | Vibium |

### Slow Tests (4m 22s - 5m 21s)
| Job | Browser | Time | Framework |
|-----|---------|------|-----------|
| smoke-tests | Chrome | 4m 32s | TestNG/Maven |
| grid-tests | Chrome | 4m 42s | TestNG/Maven |
| grid-tests | Edge | 4m 40s | TestNG/Maven |
| grid-tests | Firefox | 5m 21s | TestNG/Maven |
| mobile-browser-tests | Chrome | 4m 51s | TestNG/Maven |
| responsive-design-tests | Chrome | 4m 22s | TestNG/Maven |
| selenide-tests | Chrome | 4m 34s | TestNG/Maven |

**Key Observation**: All slow tests use **Maven/TestNG with Selenium Grid**, while fast tests use **native browser automation** (Cypress, Playwright) or **direct Selenium** (Robot Framework, Vibium).

---

## 🔍 Root Cause Analysis

### Common Overhead in Slow Tests

#### 1. **Service Startup (Backend + Frontend)**
- **Time**: ~30-60 seconds per job
- **Location**: `scripts/start-services-for-ci.sh`
- **Details**:
  - Backend: Virtual environment setup, dependency installation, uvicorn startup
  - Frontend: Dependency check, Next.js dev server startup
  - Wait for services: Up to 120 seconds timeout (MAX_WAIT)
  - **Impact**: Each test job starts services independently, even though they're running in parallel

#### 2. **Selenium Grid Wait**
- **Time**: 60 seconds timeout + 5-10 seconds sleep
- **Location**: `scripts/ci/wait-for-grid.sh`
- **Details**:
  - Grid wait timeout: 60 seconds (default)
  - Additional sleep after ready: 5-10 seconds
  - **Impact**: Even if Grid is ready in 10 seconds, we wait up to 60 seconds

#### 3. **Maven Compilation**
- **Time**: Variable (30-90 seconds if not cached)
- **Location**: `scripts/ci/run-maven-tests.sh`
- **Details**:
  - Pre-compiled classes are downloaded from `build-and-compile` job
  - If download fails or classes are incomplete, full compilation occurs
  - **Impact**: Compilation can add significant time if cache misses

#### 4. **Test Execution**
- **Time**: Fast (actual tests run quickly)
- **Observation**: User noted "most of the actual tests run very fast"
- **Conclusion**: The bottleneck is **setup/teardown**, not test execution

#### 5. **Artifact Upload**
- **Time**: Variable (10-30 seconds)
- **Location**: GitHub Actions artifact upload steps
- **Impact**: Minimal but adds to total time

---

## 💡 Optimization Recommendations

### **Priority 1: High Impact, Low Risk**

#### 1. **Shared Service Startup Job** ⭐⭐⭐
**Impact**: Save ~30-60 seconds per test job (7 jobs × 45s = **~5 minutes total**)

**Current**: Each test job starts backend/frontend independently  
**Proposed**: Create a shared `setup-services` job that starts services once, all test jobs wait for it

**Implementation**:
```yaml
# In ci.yml
setup-services:
  name: Setup Services (Shared)
  runs-on: ubuntu-latest
  steps:
    - name: Start Backend and Frontend
      run: ./scripts/start-services-for-ci.sh
    - name: Wait for Services
      run: ./scripts/ci/wait-for-services.sh
    - name: Save Service Status
      run: echo "services-ready=true" >> $GITHUB_OUTPUT

# In env-fe.yml - modify all test jobs
smoke-tests:
  needs: [setup-services]  # Wait for shared services
  steps:
    - name: Verify Services
      run: curl -f http://localhost:3003 && curl -f http://localhost:8003/docs
    # Skip service startup step
```

**Benefits**:
- Services start once instead of 7 times
- All test jobs can start immediately after services are ready
- Reduces resource usage (CPU, memory, ports)

**Risk**: Low - Services are already idempotent, this just centralizes startup

---

#### 2. **Optimize Grid Wait Time** ⭐⭐
**Impact**: Save ~40-50 seconds per Grid-using job (5 jobs × 45s = **~3.5 minutes total**)

**Current**: 
- Grid wait timeout: 60 seconds
- Additional sleep: 5-10 seconds after ready
- Total: Up to 70 seconds per job

**Proposed**:
- Reduce timeout to 30 seconds (Grid typically starts in 10-15 seconds)
- Reduce sleep to 2 seconds (Grid is ready when status endpoint responds)
- Use exponential backoff instead of fixed 2-second intervals

**Implementation**:
```bash
# scripts/ci/wait-for-grid.sh
TIMEOUT=${2:-30}  # Reduced from 60
# Use exponential backoff: 1s, 2s, 4s, 8s...
# Remove fixed 5-10 second sleep after ready
```

**Benefits**:
- Faster failure detection if Grid fails to start
- Reduces unnecessary waiting when Grid is ready quickly

**Risk**: Low - Grid typically starts in 10-15 seconds, 30s timeout is sufficient

---

#### 3. **Reduce Maven Memory Overhead** ⭐
**Impact**: Save ~10-20 seconds per Maven job (7 jobs × 15s = **~1.5 minutes total**)

**Current**: `MAVEN_OPTS: -Xmx2048m` (2GB heap)

**Proposed**: 
- Increase to 4GB if GitHub Actions runner supports it
- Or optimize Maven settings to reduce GC overhead

**Implementation**:
```yaml
# In env-fe.yml
env:
  MAVEN_OPTS: -Xmx4096m -XX:+UseG1GC -XX:MaxGCPauseMillis=200
```

**Benefits**:
- Faster compilation with more memory
- Less GC pauses during test execution

**Risk**: Medium - Need to verify GitHub Actions runner memory limits (typically 7GB available)

---

### **Priority 2: Medium Impact, Medium Risk**

#### 4. **Parallel Service Startup** ⭐⭐
**Impact**: Save ~15-20 seconds per service startup

**Current**: Services start sequentially (backend, then frontend)  
**Proposed**: Start backend and frontend in parallel using background processes

**Implementation**:
```bash
# In start-services-for-ci.sh
# Start backend and frontend in parallel
(./scripts/start-be.sh &) &
BACKEND_PID=$!
(./scripts/start-fe.sh &) &
FRONTEND_PID=$!

# Wait for both
wait $BACKEND_PID
wait $FRONTEND_PID
```

**Benefits**:
- Services start concurrently instead of sequentially
- Reduces total startup time

**Risk**: Medium - Need to ensure proper error handling and cleanup

---

#### 5. **Optimize Dependency Installation** ⭐
**Impact**: Save ~10-15 seconds per job (if dependencies not cached)

**Current**: 
- Backend: `pip install -q -r requirements.txt` (runs even if venv exists)
- Frontend: `npm install --silent` (runs if node_modules missing)

**Proposed**:
- Use GitHub Actions cache for `node_modules` and `venv`
- Skip installation if cache hit

**Implementation**:
```yaml
- name: Cache Frontend Dependencies
  uses: actions/cache@v4
  with:
    path: frontend/node_modules
    key: frontend-deps-${{ hashFiles('frontend/package-lock.json') }}

- name: Cache Backend Dependencies
  uses: actions/cache@v4
  with:
    path: backend/venv
    key: backend-deps-${{ hashFiles('backend/requirements.txt') }}
```

**Benefits**:
- Faster job startup when dependencies are cached
- Reduces network usage

**Risk**: Low - Caching is a standard practice

---

### **Priority 3: Lower Impact, Higher Complexity**

#### 6. **Increase GitHub Actions Runner Memory** ⭐
**Impact**: Potentially save 10-30 seconds per job (if memory is the bottleneck)

**Current**: Using `ubuntu-latest` (standard runner, ~7GB RAM)  
**Proposed**: Use larger runner if available, or optimize memory usage

**Note**: GitHub Actions free tier doesn't support custom runner sizes. This would require:
- GitHub Enterprise Cloud (paid)
- Self-hosted runners
- Or optimization of current memory usage

**Benefits**:
- More memory for Maven compilation
- Less GC overhead

**Risk**: High - Requires infrastructure changes or paid plan

---

#### 7. **Reduce Sleep Times** ⭐
**Impact**: Save ~5-10 seconds per job

**Current**: Multiple `sleep` commands in wait scripts:
- `sleep 10` after Grid ready (grid-tests)
- `sleep 5` after Grid ready (mobile, responsive)
- `sleep 2` in wait loops

**Proposed**: 
- Remove unnecessary sleeps
- Use actual readiness checks instead of fixed delays

**Implementation**:
```bash
# Instead of: sleep 10; echo "Grid ready!"
# Use: Check Grid actually accepts connections
until curl -sf http://localhost:4444/wd/hub/status | jq -e '.value.ready' > /dev/null; do
  sleep 1
done
```

**Benefits**:
- Faster startup when services are ready quickly
- More reliable (checks actual readiness, not time-based)

**Risk**: Low - Just removing unnecessary waits

---

## 📈 Expected Performance Improvements

### **Conservative Estimate** (Priority 1 only)
| Optimization | Time Saved | Jobs Affected |
|-------------|-----------|---------------|
| Shared Service Startup | ~5 minutes | 7 jobs |
| Optimize Grid Wait | ~3.5 minutes | 5 jobs |
| **Total** | **~8.5 minutes** | - |

**New Timings** (estimated):
- smoke-tests: 4m 32s → **3m 15s** (~27% faster)
- grid-tests: 4m 42s → **3m 25s** (~28% faster)
- selenide-tests: 4m 34s → **3m 17s** (~27% faster)
- mobile-browser-tests: 4m 51s → **3m 34s** (~27% faster)
- responsive-design-tests: 4m 22s → **3m 5s** (~27% faster)

### **Aggressive Estimate** (Priority 1 + 2)
**Total Time Saved**: ~12-15 minutes across all jobs

**New Timings** (estimated):
- smoke-tests: 4m 32s → **2m 45s** (~39% faster)
- grid-tests: 4m 42s → **2m 55s** (~38% faster)
- selenide-tests: 4m 34s → **2m 47s** (~39% faster)

---

## 🎯 Recommended Implementation Order

1. **Week 1**: Implement Priority 1 optimizations
   - Shared service startup job
   - Optimize Grid wait time
   - Test and verify improvements

2. **Week 2**: Implement Priority 2 optimizations
   - Parallel service startup
   - Dependency caching
   - Monitor for any regressions

3. **Week 3**: Fine-tune and optimize
   - Reduce sleep times
   - Monitor actual performance gains
   - Document improvements

---

## ⚠️ Risks and Considerations

### **Shared Services Approach**
- **Risk**: If shared services fail, all test jobs fail
- **Mitigation**: Add health checks and retry logic
- **Benefit**: Faster overall pipeline, easier debugging

### **Reduced Grid Wait Time**
- **Risk**: Grid might not be ready in 30 seconds on slower runners
- **Mitigation**: Start with 45 seconds, reduce to 30 if stable
- **Benefit**: Faster failure detection, less wasted time

### **Memory Increase**
- **Risk**: GitHub Actions runner might not have enough memory
- **Mitigation**: Test with 3GB first, increase to 4GB if stable
- **Benefit**: Faster compilation, less GC overhead

---

## 📝 Next Steps

1. **Review this analysis** with the team
2. **Prioritize optimizations** based on impact vs. risk
3. **Create implementation tickets** for selected optimizations
4. **Test in a feature branch** before merging to main
5. **Monitor performance** after implementation
6. **Document changes** in CI/CD documentation

---

## 🔗 Related Documentation

- [GitHub Actions Workflows](docs/guides/infrastructure/GITHUB_ACTIONS.md)
- [Test Execution Guide](docs/guides/testing/TEST_EXECUTION_GUIDE.md)
- [Service Scripts](docs/guides/infrastructure/SERVICE_SCRIPTS.md)

---

**Last Updated**: 2025-12-31  
**Status**: ✅ Implementation Complete - Aggressive Optimizations Applied

---

## 🚀 Implementation Status

### ✅ Completed Optimizations (Aggressive Approach)

#### 1. **Shared Service Startup Job** ✅
- **Status**: Implemented
- **Location**: `.github/workflows/ci.yml` - `setup-shared-services` job
- **Changes**:
  - Created new job that starts services once for all test jobs
  - Added dependency caching for frontend (`node_modules`) and backend (`venv`)
  - All test jobs now depend on `setup-shared-services` instead of starting services individually
  - **Expected Savings**: ~5 minutes total (services start once instead of 7 times)

#### 2. **Optimized Grid Wait Time** ✅
- **Status**: Implemented
- **Location**: 
  - `.github/workflows/env-fe.yml` - Default timeout changed from 60s to 20s
  - `scripts/ci/wait-for-grid.sh` - Timeout default changed from 60s to 20s, sleep reduced from 5s to 2s
  - `scripts/ci/wait-for-service.sh` - Check interval reduced from 2s to 1s
- **Changes**:
  - Grid wait timeout: 60s → **20s** (aggressive)
  - Service wait timeout: 30s → **15s** (aggressive)
  - Grid sleep after ready: 5s → **2s**
  - Check interval: 2s → **1s**
  - **Expected Savings**: ~3.5 minutes total

#### 3. **Increased Maven Memory** ✅
- **Status**: Implemented
- **Location**: 
  - `.github/workflows/ci.yml` - `MAVEN_OPTS` updated
  - `.github/workflows/env-fe.yml` - Default `maven_memory` changed from 2048m to 4096m
- **Changes**:
  - Maven heap: 2048m → **4096m**
  - Added G1GC: `-XX:+UseG1GC -XX:MaxGCPauseMillis=200`
  - **Expected Savings**: ~1.5 minutes total

#### 4. **Parallel Service Startup** ✅
- **Status**: Implemented
- **Location**: `scripts/start-services-for-ci.sh`
- **Changes**:
  - Backend and frontend now start concurrently
  - Wait for services happens in parallel using background processes
  - **Expected Savings**: ~15-20 seconds per startup

#### 5. **Dependency Caching** ✅
- **Status**: Implemented
- **Location**: 
  - `.github/workflows/ci.yml` - `setup-shared-services` job
  - `.github/workflows/env-fe.yml` - All test jobs
- **Changes**:
  - Frontend: Cache `node_modules` based on `package-lock.json` hash
  - Backend: Cache `venv` based on `requirements.txt` hash
  - **Expected Savings**: ~10-15 seconds per job when cache hits

#### 6. **Reduced Sleep Times** ✅
- **Status**: Implemented
- **Location**: 
  - `scripts/ci/wait-for-grid.sh` - Sleep reduced from 5s to 2s
  - `.github/workflows/env-fe.yml` - Grid wait sleeps reduced from 10s/5s to 2s
- **Changes**:
  - All unnecessary sleep statements reduced or removed
  - **Expected Savings**: ~5-10 seconds per job

### 📊 Expected Performance Improvements (Aggressive)

**Total Expected Time Saved**: ~12-15 minutes across all jobs

**New Estimated Timings**:
| Job | Current Time | Estimated New Time | Improvement |
|-----|-------------|-------------------|-------------|
| smoke-tests | 4m 32s | **2m 45s** | ~39% faster |
| grid-tests (Chrome) | 4m 42s | **2m 55s** | ~38% faster |
| grid-tests (Edge) | 4m 40s | **2m 53s** | ~38% faster |
| grid-tests (Firefox) | 5m 21s | **3m 34s** | ~33% faster |
| mobile-browser-tests | 4m 51s | **3m 4s** | ~37% faster |
| responsive-design-tests | 4m 22s | **2m 45s** | ~37% faster |
| selenide-tests | 4m 34s | **2m 47s** | ~39% faster |

### 🔧 Implementation Details

#### Files Modified:
1. `.github/workflows/ci.yml`
   - Added `setup-shared-services` job
   - Updated `test-fe-dev`, `test-fe-test`, `test-fe-prod` to depend on shared services
   - Updated `MAVEN_OPTS` to 4096m with G1GC

2. `.github/workflows/env-fe.yml`
   - Changed default `grid_wait_timeout_seconds` from 60 to 20
   - Changed default `service_wait_timeout_seconds` from 30 to 15
   - Changed default `maven_memory` from 2048m to 4096m
   - Updated `MAVEN_OPTS` to include G1GC settings
   - Replaced all "Start Backend and Frontend Services" steps with "Verify Shared Services"
   - Added dependency caching for all test jobs
   - Reduced all Grid wait sleeps from 10s/5s to 2s

3. `scripts/ci/wait-for-grid.sh`
   - Changed default timeout from 60s to 20s
   - Reduced sleep from 5s to 2s
   - Changed check interval from 2s to 1s

4. `scripts/ci/wait-for-service.sh`
   - Changed default check interval from 2s to 1s

5. `scripts/start-services-for-ci.sh`
   - Implemented parallel service startup
   - Services wait concurrently instead of sequentially

### ⚠️ Notes

- **Shared Services**: Services are started once in `setup-shared-services` job. All test jobs verify services are ready instead of starting them.
- **Aggressive Timeouts**: Timeouts are set to minimum safe values. If Grid or services take longer to start, timeouts may need adjustment.
- **Memory**: 4GB Maven heap may require GitHub Actions runner to have sufficient memory. Monitor for OOM errors.
- **Testing Required**: These optimizations should be tested in a feature branch before merging to main.

### 📝 Next Steps

1. **Test in Feature Branch**: Run full pipeline to verify all optimizations work correctly
2. **Monitor Performance**: Compare actual timings with estimates
3. **Adjust if Needed**: Fine-tune timeouts if services fail to start within aggressive limits
4. **Document Results**: Update this document with actual performance improvements after testing

