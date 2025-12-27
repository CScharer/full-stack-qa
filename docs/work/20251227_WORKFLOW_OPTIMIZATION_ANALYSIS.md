# Workflow Optimization Analysis

> **Purpose**: Analysis of hardcoded values and repeated run commands in `.github/workflows/env-fe.yml` that could be converted to inputs or extracted to scripts.

## 📋 Legend

| Icon | Meaning | Description |
|------|---------|-------------|
| ✅ | Recommended | Should be converted to input/script |
| ⚠️ | Consider | May be worth converting depending on use case |
| ❌ | Not Recommended | Keep as-is (rarely changes or too specific) |
| 🔧 | Configurable | Can be made configurable |
| 📝 | Documentation | Needs documentation |

---

## 🔧 Hardcoded Values That Could Be Inputs

### High Priority (Frequently Used, Should Be Configurable)

| Value | Current | Location | Type | Recommendation | Reason |
|-------|---------|----------|------|----------------|--------|
| **Java Version** | `'21'` | `env.JAVA_VERSION` | string | ✅ **Input** | May need to test with different Java versions |
| **Python Version** | `'3.11'` | Multiple `setup-python` steps | string | ✅ **Input** | Repeated 7+ times, may need different versions |
| **Node Version** | `'20'` | Multiple `setup-node` steps | string | ✅ **Input** | Repeated 7+ times, may need different versions |
| **Maven Memory** | `-Xmx2048m` | `env.MAVEN_OPTS` | string | ✅ **Input** | May need adjustment for resource constraints |
| **Test Retry Count** | `1` | `-Dtest.retry.max.count=1` | number | ✅ **Input** | Repeated in all Maven test commands, may want to adjust |
| **Allure Version** | `"2.25.0"` | `install-allure-cli.sh` | string | ✅ **Input** | May need to upgrade/downgrade Allure |

### Medium Priority (Useful But Less Critical)

| Value | Current | Location | Type | Recommendation | Reason |
|-------|---------|----------|------|----------------|--------|
| **Docker Shared Memory** | `--shm-size=2gb` | Selenium node options | string | ⚠️ **Consider** | May need adjustment for different test loads |
| **Grid Wait Timeout** | `60` seconds | `wait-for-grid.sh` | number | ⚠️ **Consider** | May need longer timeout for slow environments |
| **Service Wait Timeout** | `30` seconds | Robot test service verification | number | ⚠️ **Consider** | May need adjustment based on startup time |

### Low Priority (Rarely Changes)

| Value | Current | Location | Type | Recommendation | Reason |
|-------|---------|----------|------|----------------|--------|
| **Maven Transfer Listener** | `-Dorg.slf4j...` | `env.MAVEN_OPTS` | string | ❌ **Keep** | Standard Maven configuration, rarely changes |

---

## 📜 Repeated Run Commands That Could Be Scripts

### High Priority (Repeated Many Times)

#### 1. **Start Services Pattern** (Repeated 7+ times)
**Current Pattern**:
```yaml
- name: Start Backend and Frontend Services
  if: inputs.base_url == 'http://localhost:3003' || ...
  env:
    ENVIRONMENT: ${{ inputs.environment }}
  run: |
    echo "🚀 Starting backend and frontend services..."
    echo "📋 Environment: ${{ inputs.environment }}"
    chmod +x scripts/start-services-for-ci.sh
    ./scripts/start-services-for-ci.sh
```

**Recommendation**: ✅ **Already a script** - But could simplify the workflow step:
```yaml
- name: Start Backend and Frontend Services
  if: inputs.base_url == 'http://localhost:3003' || ...
  env:
    ENVIRONMENT: ${{ inputs.environment }}
  run: ./scripts/start-services-for-ci.sh
```

**Note**: The `chmod +x` is redundant if scripts are already executable in repo.

---

#### 2. **Stop Services Pattern** (Repeated 7+ times)
**Current Pattern**:
```yaml
- name: Stop Backend and Frontend Services
  if: inputs.enable_stop_services == true
  run: |
    chmod +x scripts/stop-services.sh
    ./scripts/stop-services.sh || true
```

**Recommendation**: ✅ **Already a script** - Simplify:
```yaml
- name: Stop Backend and Frontend Services
  if: inputs.enable_stop_services == true
  run: ./scripts/stop-services.sh || true
```

---

#### 3. **Wait for Grid Pattern** (Repeated 4+ times)
**Current Pattern**:
```yaml
- name: Wait for Selenium Grid
  run: |
    chmod +x scripts/ci/wait-for-grid.sh
    ./scripts/ci/wait-for-grid.sh "http://localhost:${{ inputs.se_hub_port }}/wd/hub/status" "60"
```

**Recommendation**: ✅ **Already a script** - Could add timeout as input:
```yaml
- name: Wait for Selenium Grid
  run: |
    ./scripts/ci/wait-for-grid.sh \
      "http://localhost:${{ inputs.se_hub_port }}/wd/hub/status" \
      "${{ inputs.grid_wait_timeout_seconds || 60 }}"
```

---

#### 4. **Maven Test Command Pattern** (Repeated 5+ times)
**Current Pattern** (varies slightly):
```yaml
- name: Run [Test Type] Tests
  run: |
    ./mvnw -ntp test \
      -Dtest.environment=${{ inputs.environment }} \
      -Dtest.retry.max.count=1 \
      -DsuiteXmlFile=testng-[suite]-suite.xml
```

**Recommendation**: ✅ **Create script** - `scripts/ci/run-maven-tests.sh`
```bash
#!/bin/bash
# scripts/ci/run-maven-tests.sh
# Usage: ./scripts/ci/run-maven-tests.sh <environment> <suite-file> [retry-count]

ENVIRONMENT=${1:-dev}
SUITE_FILE=${2:-testng-smoke-suite.xml}
RETRY_COUNT=${3:-1}

./mvnw -ntp test \
  -Dtest.environment="$ENVIRONMENT" \
  -Dtest.retry.max.count="$RETRY_COUNT" \
  -DsuiteXmlFile="$SUITE_FILE"
```

**Workflow Usage**:
```yaml
- name: Run Smoke Tests
  run: ./scripts/ci/run-maven-tests.sh "${{ inputs.environment }}" "testng-smoke-suite.xml" "${{ inputs.test_retry_count || 1 }}"
```

---

#### 5. **Allure Environment Properties Creation** (Repeated 4+ times)
**Current Pattern**:
```yaml
run: |
  # Create Allure environment properties file
  mkdir -p target/allure-results
  cat > target/allure-results/environment.properties << EOF
  Environment=${{ inputs.environment }}
  Browser=${{ matrix.browser || 'chrome' }}
  Selenium.Hub=localhost:${{ inputs.se_hub_port }}
  Base.URL=${{ inputs.base_url }}
  Test.Suite=[suite-name]
  EOF
```

**Recommendation**: ✅ **Create script** - `scripts/ci/create-allure-env-properties.sh`
```bash
#!/bin/bash
# scripts/ci/create-allure-env-properties.sh
# Usage: ./scripts/ci/create-allure-env-properties.sh <environment> <browser> <hub-port> <base-url> <test-suite>

ENVIRONMENT=${1}
BROWSER=${2:-chrome}
HUB_PORT=${3:-4444}
BASE_URL=${4}
TEST_SUITE=${5}

mkdir -p target/allure-results
cat > target/allure-results/environment.properties << EOF
Environment=$ENVIRONMENT
Browser=$BROWSER
Selenium.Hub=localhost:$HUB_PORT
Base.URL=$BASE_URL
Test.Suite=$TEST_SUITE
EOF
```

**Workflow Usage**:
```yaml
- name: Create Allure Environment Properties
  run: |
    ./scripts/ci/create-allure-env-properties.sh \
      "${{ inputs.environment }}" \
      "${{ matrix.browser || 'chrome' }}" \
      "${{ inputs.se_hub_port }}" \
      "${{ inputs.base_url }}" \
      "grid"
```

---

#### 6. **Robot Framework Installation** (Repeated 1 time, but complex)
**Current Pattern**:
```yaml
- name: Install Robot Framework dependencies
  run: |
    # Get Python executable path
    PYTHON_EXE=$(which python3 || which python)
    echo "Using Python: $PYTHON_EXE"
    "$PYTHON_EXE" --version
    
    # Install Robot Framework and libraries
    "$PYTHON_EXE" -m pip install --upgrade pip
    "$PYTHON_EXE" -m pip install robotframework
    "$PYTHON_EXE" -m pip install robotframework-seleniumlibrary
    "$PYTHON_EXE" -m pip install robotframework-requests
    
    # Verify installation
    echo "Installed Robot Framework packages:"
    "$PYTHON_EXE" -m pip list | grep -i robot || echo "⚠️ Robot Framework packages not found"
    
    # Test import
    "$PYTHON_EXE" -c "from robot.libraries.BuiltIn import BuiltIn; from SeleniumLibrary import SeleniumLibrary; print('✅ SeleniumLibrary imported successfully')" || {
      echo "❌ Failed to import SeleniumLibrary"
      echo "Python path: $("$PYTHON_EXE" -c 'import sys; print(\"\\n\".join(sys.path))')"
      exit 1
    }
    
    # Set Python path for Maven plugin (if needed)
    echo "PYTHON_EXE=$PYTHON_EXE" >> "$GITHUB_ENV"
```

**Recommendation**: ✅ **Create script** - `scripts/ci/install-robot-framework.sh`
```bash
#!/bin/bash
# scripts/ci/install-robot-framework.sh
# Installs and verifies Robot Framework dependencies

PYTHON_EXE=$(which python3 || which python)
echo "Using Python: $PYTHON_EXE"
"$PYTHON_EXE" --version

# Install Robot Framework and libraries
"$PYTHON_EXE" -m pip install --upgrade pip
"$PYTHON_EXE" -m pip install robotframework
"$PYTHON_EXE" -m pip install robotframework-seleniumlibrary
"$PYTHON_EXE" -m pip install robotframework-requests

# Verify installation
echo "Installed Robot Framework packages:"
"$PYTHON_EXE" -m pip list | grep -i robot || echo "⚠️ Robot Framework packages not found"

# Test import
"$PYTHON_EXE" -c "from robot.libraries.BuiltIn import BuiltIn; from SeleniumLibrary import SeleniumLibrary; print('✅ SeleniumLibrary imported successfully')" || {
  echo "❌ Failed to import SeleniumLibrary"
  echo "Python path: $("$PYTHON_EXE" -c 'import sys; print(\"\\n\".join(sys.path))')"
  exit 1
}

# Set Python path for Maven plugin (if needed)
echo "PYTHON_EXE=$PYTHON_EXE" >> "$GITHUB_ENV"
```

**Workflow Usage**:
```yaml
- name: Install Robot Framework dependencies
  run: ./scripts/ci/install-robot-framework.sh
```

---

#### 7. **Service Verification** (Robot Tests - Complex)
**Current Pattern**:
```yaml
- name: Verify Services Are Running
  if: inputs.base_url == 'http://localhost:3003' || inputs.base_url == 'http://localhost:3004' || inputs.base_url == 'http://localhost:3005' || startsWith(inputs.base_url, 'http://localhost')
  run: |
    # Extract port from base_url
    BASE_URL="${{ inputs.base_url }}"
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
    
    echo "Checking Frontend on port $FRONTEND_PORT..."
    timeout 30 bash -c 'until curl -sf http://localhost:'"$FRONTEND_PORT"' > /dev/null; do echo "  Waiting for frontend..."; sleep 2; done' || {
      echo "❌ Frontend not responding on port $FRONTEND_PORT"
      echo "Checking if process is running:"
      lsof -i :"$FRONTEND_PORT" || echo "No process found on port $FRONTEND_PORT"
      exit 1
    }
    echo "✅ Frontend is responding on port $FRONTEND_PORT"
    
    echo "Checking Backend on port $API_PORT..."
    timeout 30 bash -c 'until curl -sf http://localhost:'"$API_PORT"'/docs > /dev/null; do echo "  Waiting for backend..."; sleep 2; done' || {
      echo "❌ Backend not responding on port $API_PORT"
      echo "Checking if process is running:"
      lsof -i :"$API_PORT" || echo "No process found on port $API_PORT"
      exit 1
    }
    echo "✅ Backend is responding on port $API_PORT"
    
    echo "✅ All services verified and ready!"
```

**Recommendation**: ✅ **Create script** - `scripts/ci/verify-services.sh`
```bash
#!/bin/bash
# scripts/ci/verify-services.sh
# Usage: ./scripts/ci/verify-services.sh <base-url> [timeout-seconds]

BASE_URL=${1}
TIMEOUT=${2:-30}

# Extract ports from base_url
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

echo "Checking Frontend on port $FRONTEND_PORT..."
timeout "$TIMEOUT" bash -c 'until curl -sf http://localhost:'"$FRONTEND_PORT"' > /dev/null; do echo "  Waiting for frontend..."; sleep 2; done' || {
  echo "❌ Frontend not responding on port $FRONTEND_PORT"
  echo "Checking if process is running:"
  lsof -i :"$FRONTEND_PORT" || echo "No process found on port $FRONTEND_PORT"
  exit 1
}
echo "✅ Frontend is responding on port $FRONTEND_PORT"

echo "Checking Backend on port $API_PORT..."
timeout "$TIMEOUT" bash -c 'until curl -sf http://localhost:'"$API_PORT"'/docs > /dev/null; do echo "  Waiting for backend..."; sleep 2; done' || {
  echo "❌ Backend not responding on port $API_PORT"
  echo "Checking if process is running:"
  lsof -i :"$API_PORT" || echo "No process found on port $API_PORT"
  exit 1
}
echo "✅ Backend is responding on port $API_PORT"

echo "✅ All services verified and ready!"
```

**Workflow Usage**:
```yaml
- name: Verify Services Are Running
  if: inputs.base_url == 'http://localhost:3003' || ...
  run: ./scripts/ci/verify-services.sh "${{ inputs.base_url }}" "${{ inputs.service_wait_timeout_seconds || 30 }}"
```

---

### Medium Priority (Repeated But Simpler)

#### 8. **Node Dependencies Installation** (Repeated 2 times)
**Current Pattern**:
```yaml
- name: Install [Cypress/Playwright] dependencies
  working-directory: ./[cypress|playwright]
  run: npm ci
```

**Recommendation**: ⚠️ **Keep as-is** - Simple enough, but could create `scripts/ci/install-node-deps.sh` if pattern grows.

---

#### 9. **Playwright Browser Installation** (Repeated 1 time)
**Current Pattern**:
```yaml
- name: Install Playwright browsers
  working-directory: ./playwright
  run: npx playwright install --with-deps chromium
```

**Recommendation**: ⚠️ **Keep as-is** - Simple one-liner, but could be script if needs to grow.

---

## 📊 Summary

### Recommended New Inputs

| Input Name | Type | Default | Description |
|------------|------|---------|-------------|
| `java_version` | string | `'21'` | Java version for JDK setup |
| `python_version` | string | `'3.11'` | Python version for setup-python |
| `node_version` | string | `'20'` | Node.js version for setup-node |
| `maven_memory` | string | `'2048m'` | Maven heap size (e.g., `2048m`, `4096m`) |
| `test_retry_count` | number | `1` | Number of retries for failed tests |
| `allure_version` | string | `'2.25.0'` | Allure CLI version |
| `grid_wait_timeout_seconds` | number | `60` | Timeout for waiting for Selenium Grid |
| `service_wait_timeout_seconds` | number | `30` | Timeout for waiting for services |
| `docker_shm_size` | string | `'2gb'` | Docker shared memory size for Selenium nodes |

### Recommended New Scripts

1. ✅ `scripts/ci/run-maven-tests.sh` - Standardize Maven test execution
2. ✅ `scripts/ci/create-allure-env-properties.sh` - Create Allure environment properties
3. ✅ `scripts/ci/install-robot-framework.sh` - Install and verify Robot Framework
4. ✅ `scripts/ci/verify-services.sh` - Verify backend/frontend services are running

### Script Simplifications

1. ✅ Remove redundant `chmod +x` commands (if scripts are executable in repo)
2. ✅ Simplify start/stop services steps (already scripts, just remove chmod)

---

## 🎯 Implementation Priority

### Phase 1: High Impact, Low Effort ✅ **COMPLETED**
1. ✅ Add version inputs (Java, Python, Node) - Used everywhere
2. ✅ Add test retry count input - Used in all Maven commands
3. ✅ Create `run-maven-tests.sh` script - Reduces duplication significantly

**Implementation Date**: 2025-12-27  
**Status**: All Phase 1 items completed and documented

### Phase 2: Medium Impact, Medium Effort ✅ **COMPLETED**
4. ✅ Create `create-allure-env-properties.sh` script
5. ✅ Create `install-robot-framework.sh` script
6. ✅ Create `verify-services.sh` script
7. ✅ Add timeout inputs (grid, service wait)

**Implementation Date**: 2025-12-27  
**Status**: All Phase 2 items completed and documented

### Phase 3: Lower Priority ✅ **COMPLETED**
8. ✅ Add Maven memory input
9. ✅ Add Allure version input
10. ✅ Add Docker shm-size input
11. ✅ Remove redundant `chmod +x` commands

**Implementation Date**: 2025-12-27  
**Status**: All Phase 3 items completed and documented

---

## 📝 Notes

- **Script Executability**: If scripts are committed with executable permissions (`git update-index --chmod=+x`), `chmod +x` commands are redundant
- **Input Naming**: Use `_minutes` suffix for timeout inputs, `_seconds` for short timeouts, `_version` for version inputs
- **Backward Compatibility**: All new inputs should have sensible defaults matching current hardcoded values
- **Documentation**: Update `TEST_SUITES_UPDATE_GUIDE.md` with new inputs and scripts

---

**Last Updated**: 2025-12-27  
**Status**: ✅ Phase 1, 2, and 3 Complete - Service Scripts Consolidation Pending

## ✅ Phase 1 Implementation Summary

**Completed Items**:
- ✅ Added 4 new workflow inputs: `java_version`, `python_version`, `node_version`, `test_retry_count`
- ✅ Updated all version references (7 Python, 7 Node, 5 Java)
- ✅ Created `scripts/ci/run-maven-tests.sh` script
- ✅ Updated all 5 Maven test commands to use the new script
- ✅ Updated documentation in `TEST_SUITES_REFERENCE.md` and `TEST_SUITES_UPDATE_GUIDE.md`

**Files Modified**:
- `.github/workflows/env-fe.yml` - Added inputs and updated all test jobs
- `scripts/ci/run-maven-tests.sh` - New script for centralized Maven test execution
- `docs/guides/testing/TEST_SUITES_REFERENCE.md` - Updated with new inputs and script info
- `docs/guides/testing/TEST_SUITES_UPDATE_GUIDE.md` - Updated with usage examples

## ✅ Phase 2 Implementation Summary

**Completed Items**:
- ✅ Created `scripts/ci/create-allure-env-properties.sh` script (replaces 4 inline Allure property creations)
- ✅ Created `scripts/ci/install-robot-framework.sh` script (replaces complex Robot Framework installation)
- ✅ Created `scripts/ci/verify-services.sh` script (replaces service verification logic)
- ✅ Added 2 new timeout inputs: `grid_wait_timeout_seconds` (default: 60), `service_wait_timeout_seconds` (default: 30)
- ✅ Updated all Allure environment property creation steps (4 locations)
- ✅ Updated Robot Framework installation step
- ✅ Updated service verification step
- ✅ Updated all grid wait steps to use timeout input

**Files Modified**:
- `.github/workflows/env-fe.yml` - Added timeout inputs, replaced inline scripts with new scripts
- `scripts/ci/create-allure-env-properties.sh` - New script for Allure environment properties
- `scripts/ci/install-robot-framework.sh` - New script for Robot Framework installation
- `scripts/ci/verify-services.sh` - New script for service verification

## ✅ Phase 3 Implementation Summary

**Completed Items**:
- ✅ Added 3 new resource configuration inputs: `maven_memory` (default: `'2048m'`), `allure_version` (default: `'2.25.0'`), `docker_shm_size` (default: `'2gb'`)
- ✅ Updated `MAVEN_OPTS` to use `maven_memory` input
- ✅ Updated all Docker `--shm-size` options (6 locations) to use `docker_shm_size` input
- ✅ Updated Allure CLI installation to use `allure_version` input
- ✅ Removed 34 redundant `chmod +x` commands (scripts are already executable in git)

**Files Modified**:
- `.github/workflows/env-fe.yml` - Added resource inputs, updated all hardcoded values, removed redundant chmod commands

**Next Steps**: Service scripts consolidation (from duplication analysis)

