# Service Scripts Duplication Analysis

> **Purpose**: Analysis of code duplication in service-related scripts (start, stop, verify, wait) with recommendations for consolidation.

## 📌 Important Distinctions

**Service Types**:
- **Application Services**: Backend and Frontend services (the application itself)
  - Scripts: `start-services-for-ci.sh`, `stop-services.sh`, `verify-services.sh`, `wait-for-services.sh`
  - Used in: Backend test workflows (`env-be.yml`)
- **Test Infrastructure**: Selenium Grid (test execution infrastructure)
  - Scripts: `wait-for-grid.sh`
  - Used in: Frontend test workflows (`env-fe.yml`)

**Key Point**: `wait-for-services.sh` and `wait-for-grid.sh` serve **different purposes** and should remain as separate scripts:
- `wait-for-services.sh` → Waits for **Backend and Frontend application services**
- `wait-for-grid.sh` → Waits for **Selenium Grid test infrastructure**

---

## 📋 Legend

| Icon | Meaning | Description |
|------|---------|-------------|
| ✅ | Good | No issues, follows best practices |
| ⚠️ | Duplication Found | Code is duplicated and should be consolidated |
| 🔧 | Recommendation | Suggested improvement |
| 📝 | Documentation | Needs documentation update |

---

## 🔍 Duplication Analysis

### 1. Port Extraction Logic ⚠️ **DUPLICATION FOUND**

**Issue**: Multiple scripts have different ways of extracting ports from base URLs or environments.

#### Current State:

**`scripts/ci/verify-services.sh`** (Lines 30-42):
```bash
# Hardcoded port mapping
if [[ "$BASE_URL" == *":3003"* ]]; then
  FRONTEND_PORT=3003
  API_PORT=8003
elif [[ "$BASE_URL" == *":3004"* ]]; then
  FRONTEND_PORT=3004
  API_PORT=8004
elif [[ "$BASE_URL" == *":3005"* ]]; then
  FRONTEND_PORT=3005
  API_PORT=8005
else
  FRONTEND_PORT=3003
  API_PORT=8003
fi
```

**`scripts/start-services-for-ci.sh`** (Lines 20-107):
- ✅ Uses `scripts/ci/port-config.sh` (good!)
- Has fallback hardcoded values

**`scripts/ci/port-config.sh`**:
- ✅ Centralized port configuration (single source of truth)

#### Recommendation 🔧:

**Update `verify-services.sh` to use `port-config.sh`**:
- Extract environment from base URL
- Use `get_ports_for_environment()` function
- Remove hardcoded port mapping

**Benefits**:
- Single source of truth for ports
- Consistent behavior across all scripts
- Easier to maintain (change ports in one place)

---

### 2. Service Waiting/Verification Logic ⚠️ **DUPLICATION FOUND**

**Issue**: Multiple scripts have similar logic for waiting/verifying services are ready.

#### Current State:

**`scripts/start-services-for-ci.sh`** (Lines 128-148):
```bash
wait_for_service() {
    local url=$1
    local service_name=$2
    local elapsed=0
    
    echo "⏳ Waiting for $service_name to be ready..."
    while [ $elapsed -lt $MAX_WAIT ]; do
        if curl -sf "$url" > /dev/null 2>&1; then
            echo "✅ $service_name is ready!"
            return 0
        fi
        sleep 2
        elapsed=$((elapsed + 2))
        if [ $((elapsed % 10)) -eq 0 ]; then
            echo "   Still waiting... (${elapsed}s/${MAX_WAIT}s)"
        fi
    done
    
    echo "❌ $service_name failed to start within ${MAX_WAIT}s"
    return 1
}
```

**`scripts/ci/verify-services.sh`** (Lines 50-66):
```bash
# Inline timeout/curl logic
timeout "$TIMEOUT" bash -c "until curl -sf http://localhost:$FRONTEND_PORT > /dev/null; do echo '  Waiting for frontend...'; sleep 2; done" || {
  echo "❌ Frontend not responding on port $FRONTEND_PORT"
  exit 1
}
```

**`scripts/ci/wait-for-services.sh`** (Lines 17-25):
```bash
# Waits for Backend and Frontend services (application services)
for i in $(seq 1 $MAX_ATTEMPTS); do
  if curl -sf "$FRONTEND_URL" >/dev/null 2>&1 && curl -sf "$BACKEND_URL/health" >/dev/null 2>&1; then
    echo "✅ Services are ready!"
    READY=true
    break
  fi
  echo "Waiting... ($i/$MAX_ATTEMPTS)"
  sleep 2
done
```
- **Purpose**: Waits for Backend and Frontend application services
- **Used in**: `env-be.yml` workflow (backend tests)
- **Checks**: Both frontend and backend endpoints

**`scripts/ci/wait-for-grid.sh`** (Line 12):
```bash
# Waits for Selenium Grid (test infrastructure)
timeout "$TIMEOUT" bash -c "until curl -sf $GRID_URL; do sleep 2; done"
```
- **Purpose**: Waits for Selenium Grid infrastructure
- **Used in**: `env-fe.yml` workflow (frontend tests - smoke, grid, robot, selenide)
- **Checks**: Selenium Grid status endpoint

#### Recommendation 🔧:

**Create `scripts/ci/wait-for-service.sh`** - A reusable service waiting utility:
```bash
#!/bin/bash
# Wait for a service to be ready
# Usage: ./scripts/ci/wait-for-service.sh <url> <service-name> [timeout-seconds]

URL=$1
SERVICE_NAME=$2
TIMEOUT=${3:-60}

# Wait logic with proper error handling
```

**Update all scripts to use this utility**:
- `start-services-for-ci.sh` → Use `wait-for-service.sh`
- `verify-services.sh` → Use `wait-for-service.sh`
- `wait-for-services.sh` → Use `wait-for-service.sh` internally (keep as wrapper for BE/FE services)
- `wait-for-grid.sh` → Use `wait-for-service.sh` internally (keep as wrapper for Selenium Grid)

**Note**: `wait-for-services.sh` and `wait-for-grid.sh` serve different purposes:
- `wait-for-services.sh`: Application services (Backend + Frontend)
- `wait-for-grid.sh`: Test infrastructure (Selenium Grid)
Both should remain as separate scripts but can use the shared utility internally.

**Benefits**:
- Consistent waiting behavior
- Single place to fix bugs
- Easier to add features (retry logic, better error messages)

---

### 3. Port Checking Logic ⚠️ **DUPLICATION FOUND**

**Issue**: Multiple scripts check if ports are in use with similar logic.

#### Current State:

**`scripts/start-services-for-ci.sh`** (Lines 118-125):
```bash
is_port_in_use() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1 || nc -z localhost $port 2>/dev/null; then
        return 0  # Port is in use
    else
        return 1  # Port is not in use
    fi
}
```

**`scripts/stop-services.sh`** (Line 20):
```bash
if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
```

**`scripts/ci/verify-services.sh`** (Lines 53, 63):
```bash
lsof -i :"$FRONTEND_PORT" || echo "No process found on port $FRONTEND_PORT"
```

#### Recommendation 🔧:

**Create `scripts/ci/port-utils.sh`** - Common port utilities:
```bash
#!/bin/bash
# Port utility functions
# Usage: source scripts/ci/port-utils.sh

is_port_in_use() {
    local port=$1
    # Check if port is in use
}

get_port_pid() {
    local port=$1
    # Get PID of process using port
}

stop_port() {
    local port=$1
    # Stop process on port
}
```

**Update all scripts to source this utility**:
- `start-services-for-ci.sh` → Source `port-utils.sh`
- `stop-services.sh` → Source `port-utils.sh`
- `verify-services.sh` → Source `port-utils.sh` (if needed)

**Benefits**:
- Consistent port checking
- Reusable port management functions
- Easier to maintain

---

### 4. Service Verification Duplication ⚠️ **DUPLICATION FOUND**

**Issue**: `start-services-for-ci.sh` already verifies services are responding, but `verify-services.sh` does the same thing.

#### Current State:

**`scripts/start-services-for-ci.sh`** (Lines 183, 268):
- Verifies backend is responding: `curl -sf "http://localhost:$API_PORT/docs"`
- Verifies frontend is responding: `curl -sf "http://localhost:$FRONTEND_PORT"`

**`scripts/ci/verify-services.sh`**:
- Does the same verification

#### Recommendation 🔧:

**Option 1: Remove `verify-services.sh` and use `start-services-for-ci.sh` verification**
- `start-services-for-ci.sh` already verifies services
- `verify-services.sh` is redundant if services are started with `start-services-for-ci.sh`

**Option 2: Keep `verify-services.sh` but make it use shared utilities**
- Use `wait-for-service.sh` utility
- Use `port-utils.sh` for port extraction
- Keep it as a standalone verification tool (useful when services are started externally)

**Recommendation**: **Option 2** - Keep `verify-services.sh` but refactor to use shared utilities. It's useful for verifying services started externally or in different contexts.

---

## 📊 Summary of Recommendations

### High Priority (Should Fix)

1. **Update `verify-services.sh` to use `port-config.sh`** ⚠️
   - Remove hardcoded port mapping
   - Use `get_ports_for_environment()` function
   - Extract environment from base URL

2. **Create `scripts/ci/wait-for-service.sh` utility** 🔧
   - Consolidate all service waiting logic
   - Update all scripts to use it
   - Consistent timeout and error handling

### Medium Priority (Should Consider)

3. **Create `scripts/ci/port-utils.sh` utility** 🔧
   - Consolidate port checking logic
   - Reusable port management functions
   - Update scripts to source it

4. **Refactor `verify-services.sh` to use shared utilities** 🔧
   - Use `wait-for-service.sh`
   - Use `port-config.sh` for port extraction
   - Keep as standalone verification tool

### Low Priority (Nice to Have)

5. **Keep `wait-for-services.sh` and `wait-for-grid.sh` separate** ✅
   - **`wait-for-services.sh`**: Waits for **Backend and Frontend services** (application services)
     - Used in: `env-be.yml` workflow
     - Parameters: `FRONTEND_URL`, `BACKEND_URL`, `MAX_ATTEMPTS`, `ENVIRONMENT`
     - Checks: Frontend and Backend health endpoints
   - **`wait-for-grid.sh`**: Waits for **Selenium Grid** (test infrastructure)
     - Used in: `env-fe.yml` workflow (multiple test jobs)
     - Parameters: `GRID_URL`, `TIMEOUT`
     - Checks: Selenium Grid status endpoint
   - **Recommendation**: Keep separate - they serve different purposes and have different requirements
   - **Note**: Both could use the same underlying `wait-for-service.sh` utility (if created) but should remain as separate scripts for clarity

---

## 🎯 Implementation Plan

### Phase 1: Port Configuration Consolidation
1. Update `verify-services.sh` to use `port-config.sh`
2. Test with all environments (dev, test, prod)

### Phase 2: Service Waiting Utility
1. Create `scripts/ci/wait-for-service.sh`
2. Update `start-services-for-ci.sh` to use it
3. Update `verify-services.sh` to use it
4. Update `wait-for-grid.sh` to use it internally (keep as wrapper for Selenium Grid)
5. Update `wait-for-services.sh` to use it internally (keep as wrapper for BE/FE services)
   - **Note**: Keep `wait-for-services.sh` and `wait-for-grid.sh` as separate scripts - they serve different purposes

### Phase 3: Port Utilities
1. Create `scripts/ci/port-utils.sh`
2. Update `start-services-for-ci.sh` to source it
3. Update `stop-services.sh` to source it
4. Update `verify-services.sh` to source it (if needed)

### Phase 4: Documentation
1. Update script documentation
2. Add examples of using shared utilities
3. Document when to use which script

---

## 📝 Files to Update

### New Files to Create:
- `scripts/ci/wait-for-service.sh` - Reusable service waiting utility
- `scripts/ci/port-utils.sh` - Common port utilities

### Files to Update:
- `scripts/ci/verify-services.sh` - Use `port-config.sh` and `wait-for-service.sh`
- `scripts/start-services-for-ci.sh` - Use `wait-for-service.sh` and `port-utils.sh`
- `scripts/stop-services.sh` - Use `port-utils.sh`
- `scripts/ci/wait-for-grid.sh` - Use `wait-for-service.sh` internally (keep as Selenium Grid wrapper)
- `scripts/ci/wait-for-services.sh` - Use `wait-for-service.sh` internally (keep as BE/FE services wrapper)

### Documentation to Update:
- `docs/guides/testing/TEST_SUITES_UPDATE_GUIDE.md` - Document new utilities
- Script headers - Update usage examples

---

**Last Updated**: 2025-12-27  
**Status**: 📋 Analysis Complete - Ready for Implementation

