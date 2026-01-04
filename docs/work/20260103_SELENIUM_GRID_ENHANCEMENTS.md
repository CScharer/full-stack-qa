# Selenium Grid Enhancements - Version Validation and Retry Logic

**Date Created**: 2026-01-03  
**Status**: 🔍 In Progress  
**Priority**: 🟡 Medium  
**Estimated Effort**: 8-12 hours

**Implementation Progress**:
- ✅ Phase 1: Version Validation Utility - COMPLETE
- ✅ Phase 2: Retry Logic Utility - COMPLETE
- ⏳ Phase 3: Enhance SeleniumGridConfig - PENDING
- ⏳ Phase 4: Update SeleniumWebDriver - PENDING
- ⏳ Phase 5: Enhance Wait Scripts - PENDING
- ⏳ Phase 6: Pre-Push Version Validation - PENDING
- ⏳ Phase 7: Update CI/CD Workflows - PENDING
- ⏳ Phase 8: Create Test Utilities - PENDING
- ⏳ Phase 9: Documentation - PENDING

---

## 📋 Overview

This document outlines the implementation plan for adding version validation and improved retry logic to Selenium Grid connections. These enhancements will improve reliability by catching version mismatches early and handling transient connection issues more robustly.

---

## 🎯 Goals

1. **Pre-Push Version Validation**: Catch version mismatches before code is pushed (NEW - Phase 6)
2. **Runtime Version Validation**: Verify that Selenium Grid server version matches client version at runtime
3. **Improved Retry Logic**: Implement exponential backoff with configurable retry parameters
4. **Better Error Handling**: Categorize errors and retry only on transient failures
5. **Enhanced Logging**: Provide clear diagnostics for version mismatches and retry attempts

---

## 🚀 Quick Start: Pre-Push Validation (Recommended First Step)

**Why start here?** Pre-push validation catches version mismatches before code reaches the repository, preventing broken builds and saving time.

**What we have**:
- ✅ `scripts/validate-dependency-versions.sh` - Already validates `pom.xml` vs workflow files
- ❌ **NOT integrated into pre-push hook** - Currently only runs in scheduled workflow

**What to add**:
1. Integrate version validation into `.git/hooks/pre-push`
2. Enhance script to check Docker Compose files
3. Fail push if versions don't match

**Benefits**:
- Catches issues before push (fast feedback)
- Prevents broken code from reaching remote
- Fast validation (< 5 seconds)
- No runtime Grid required (validates config files only)

---

## 📊 Current State Analysis

### Existing Retry Logic
- **Location**: `src/test/java/com/cjs/qa/selenium/SeleniumWebDriver.java` (lines 457-476)
- **Current Implementation**:
  - Fixed retry loop (up to 100 attempts)
  - No exponential backoff
  - No version validation
  - Basic error handling (logs every 10th attempt)
  - No distinction between transient and permanent errors

### Existing Grid Configuration
- **Location**: `src/test/java/com/cjs/qa/utilities/SeleniumGridConfig.java`
- **Current Implementation**:
  - Simple URL resolution from environment variable
  - No version checking
  - No health/readiness validation

### Existing Wait Scripts
- **Location**: `scripts/ci/wait-for-grid.sh`
- **Current Implementation**:
  - Basic connectivity check
  - No version validation
  - Simple timeout mechanism

### Version Tracking
- **Client Version**: Selenium 4.39.0 (from `pom.xml`)
- **Grid Server Version**: Managed via Docker images (selenium/hub:4.39.0)
- **Validation**: Only checks config file alignment, not runtime compatibility

### Existing Pre-Push Version Validation
- **Location**: `scripts/validate-dependency-versions.sh`
- **Current Implementation**:
  - ✅ Validates Selenium version alignment between `pom.xml` and `.github/workflows/env-fe.yml`
  - ✅ Checks for version mismatches in configuration files
  - ✅ Generates JSON/CSV reports
  - ❌ **NOT currently called in pre-push hook** (only runs in scheduled workflow)
  - ❌ Only checks config file alignment, not Docker image versions
  - ❌ No validation of Docker Compose files

### Pre-Push Hook
- **Location**: `.git/hooks/pre-push`
- **Current Implementation**:
  - Runs code quality checks (`format-code.sh --ci-mode`)
  - Runs comprehensive validation (`validate-pre-commit.sh`)
  - ❌ **Does NOT run version validation** (`validate-dependency-versions.sh`)
  - Skips checks for documentation-only changes

---

## 🔧 Implementation Plan

### Phase 1: Create Version Validation Utility ✅ COMPLETE

#### Step 1.1: Create `SeleniumGridVersionValidator.java` ✅
**Location**: `src/test/java/com/cjs/qa/utilities/SeleniumGridVersionValidator.java`

**Status**: ✅ **COMPLETE** - Implemented and ready for testing

**Purpose**: Utility class to validate Grid server version against client version

**Features**:
- ✅ Query Grid status endpoint (`/wd/hub/status`) to get server version
- ✅ Compare with client version from Selenium library or system properties
- ✅ Support configurable version tolerance (exact match, minor version, patch version)
- ✅ Provide clear error messages for mismatches
- ✅ Comprehensive error handling with QAException
- ✅ Detailed logging with GuardedLogger

**Implementation Details**:
```java
public class SeleniumGridVersionValidator {
  // Methods:
  // - validateVersion(String gridUrl) throws QAException ✅
  // - getGridServerVersion(String gridUrl) throws QAException ✅
  // - getClientVersion() returns String ✅
  // - isVersionCompatible(String serverVersion, String clientVersion, VersionTolerance tolerance) ✅
  // - getVersionTolerance() returns VersionTolerance ✅
}
```

**Configuration Options**:
- `SELENIUM_GRID_VERSION_TOLERANCE` environment variable or `selenium.grid.version.tolerance` system property:
  - `EXACT` - Must match exactly (default) ✅
  - `MINOR` - Allow minor version differences ✅
  - `PATCH` - Allow patch version differences ✅
  - `NONE` - Skip version validation (for testing) ✅
- `SELENIUM_VERSION` environment variable or `selenium.version` system property for client version ✅

---

### Phase 2: Create Retry Logic Utility ✅ COMPLETE

#### Step 2.1: Create `RetryableGridConnection.java` ✅
**Location**: `src/test/java/com/cjs/qa/utilities/RetryableGridConnection.java`

**Status**: ✅ **COMPLETE** - Implemented and ready for testing

**Purpose**: Utility class for retrying Grid connections with exponential backoff

**Features**:
- ✅ Exponential backoff with configurable base delay and max delay
- ✅ Jitter to prevent thundering herd (10% jitter factor)
- ✅ Error categorization (transient vs. permanent)
- ✅ Configurable retry attempts and timeouts
- ✅ Detailed logging of retry attempts
- ✅ Timeout checking across all retry attempts
- ✅ Comprehensive error handling

**Implementation Details**:
```java
public class RetryableGridConnection {
  // Methods:
  // - connectWithRetry(String gridUrl, Capabilities capabilities) throws QAException ✅
  // - isTransientError(Exception e) returns boolean ✅
  // - calculateBackoff(int attempt) returns long (milliseconds) ✅
  // - getMaxRetries() returns int ✅
  // - getRetryBaseDelay() returns long ✅
  // - getRetryMaxDelay() returns long ✅
  // - getRetryTimeout() returns long ✅
}
```

**Configuration Options**:
- ✅ `SELENIUM_GRID_MAX_RETRIES` (default: 5) - via env var or system property
- ✅ `SELENIUM_GRID_RETRY_BASE_DELAY_MS` (default: 1000) - via env var or system property
- ✅ `SELENIUM_GRID_RETRY_MAX_DELAY_MS` (default: 10000) - via env var or system property
- ✅ `SELENIUM_GRID_RETRY_TIMEOUT_MS` (default: 30000) - via env var or system property

**Error Categories**:
- **Transient** (should retry) ✅:
  - Connection refused ✅
  - Timeout exceptions ✅
  - Socket exceptions ✅
  - Grid not ready (HTTP 503, 502, 504) ✅
  
- **Permanent** (don't retry) ✅:
  - Version mismatch ✅
  - Authentication failures ✅
  - Invalid capabilities ✅
  - Malformed URL ✅
  - Unknown host ✅

---

### Phase 3: Enhance SeleniumGridConfig

#### Step 3.1: Add Version Validation to `SeleniumGridConfig.java`
**Location**: `src/test/java/com/cjs/qa/utilities/SeleniumGridConfig.java`

**Changes**:
- Add `validateGridVersion()` method
- Add `isGridReady()` method (health check)
- Add configuration getters for retry parameters
- Add static initialization that validates Grid on first access (optional)

**New Methods**:
```java
public static void validateGridVersion(String gridUrl) throws VersionMismatchException
public static boolean isGridReady(String gridUrl) throws GridConnectionException
public static int getMaxRetries()
public static long getRetryBaseDelay()
public static long getRetryMaxDelay()
public static long getRetryTimeout()
```

---

### Phase 4: Update SeleniumWebDriver

#### Step 4.1: Refactor `initializeWebDriver()` Method
**Location**: `src/test/java/com/cjs/qa/selenium/SeleniumWebDriver.java`

**Changes**:
- Replace existing retry loop with `RetryableGridConnection.connectWithRetry()`
- Add version validation before attempting connection
- Add health check before attempting connection
- Improve error messages
- Add structured logging

**Implementation Flow**:
1. Resolve Grid URL (existing logic)
2. Check if Grid is ready (health check)
3. Validate Grid version (new)
4. Attempt connection with retry logic (new)
5. Log success/failure with details

**Error Handling**:
- Version mismatch → Fail fast with clear error message
- Grid not ready → Retry with exponential backoff
- Connection failures → Retry with exponential backoff
- Other errors → Fail fast with error details

---

### Phase 5: Enhance Wait Scripts

#### Step 5.1: Update `wait-for-grid.sh`
**Location**: `scripts/ci/wait-for-grid.sh`

**Changes**:
- Add version validation check (optional, can be skipped for faster startup)
- Add retry logic with exponential backoff
- Improve error messages
- Add option to skip version validation (`SKIP_VERSION_CHECK=true`)

**New Features**:
- Check Grid version if `SELENIUM_VERSION` environment variable is set
- Configurable timeout and retry attempts
- Better error reporting

**Usage**:
```bash
# Basic usage (existing)
./scripts/ci/wait-for-grid.sh

# With version validation
SELENIUM_VERSION=4.39.0 ./scripts/ci/wait-for-grid.sh

# Skip version check (faster)
SKIP_VERSION_CHECK=true ./scripts/ci/wait-for-grid.sh
```

---

### Phase 6: Add Pre-Push Version Validation

#### Step 6.1: Integrate Version Validation into Pre-Push Hook
**Location**: `.git/hooks/pre-push`

**Purpose**: Catch version mismatches before code is pushed to remote

**Changes**:
- Add call to `validate-dependency-versions.sh` in pre-push hook
- Only run for code changes (skip for documentation-only changes)
- Fail push if version mismatches detected
- Provide clear error messages

**Implementation**:
```bash
# In pre-push hook, after code quality checks:
if [ -f "scripts/validate-dependency-versions.sh" ]; then
    echo -e "${BLUE}🔍 Validating dependency versions...${NC}"
    chmod +x scripts/validate-dependency-versions.sh
    if ./scripts/validate-dependency-versions.sh; then
        echo -e "${GREEN}✅ Version validation passed${NC}"
        echo ""
    else
        echo -e "${RED}❌ Version validation failed${NC}"
        echo -e "${YELLOW}💡 Fix version mismatches before pushing${NC}"
        exit 1
    fi
fi
```

**Benefits**:
- Catches version mismatches before push
- Prevents broken code from reaching remote
- Fast validation (< 5 seconds)
- Clear error messages

#### Step 6.2: Enhance Version Validation Script
**Location**: `scripts/validate-dependency-versions.sh`

**Enhancements**:
- Add Docker Compose version validation
- Check `docker-compose.yml` for Selenium Grid image versions
- Compare Docker image versions with `pom.xml` version
- Add validation for all Docker Compose files (dev, prod, base)

**New Checks**:
- Validate `selenium/hub` image version in `docker-compose.yml`
- Validate `selenium/node-chrome` image version
- Validate `selenium/node-firefox` image version
- Validate `selenium/node-edge` image version (if present)
- Compare all Docker image versions with `pom.xml` version

**Implementation**:
```bash
# Extract Selenium version from docker-compose.yml
SELENIUM_VERSION_DOCKER=$(grep 'image:.*selenium.*hub' docker-compose.yml | sed 's/.*:\([0-9.]*\).*/\1/' | head -1)

# Compare with pom.xml version
if [ "$SELENIUM_VERSION_POM" != "$SELENIUM_VERSION_DOCKER" ]; then
    print_error "Docker Compose version mismatch: pom.xml=$SELENIUM_VERSION_POM, docker-compose.yml=$SELENIUM_VERSION_DOCKER"
fi
```

### Phase 7: Update CI/CD Workflows

#### Step 7.1: Add Version Validation to Workflows
**Location**: `.github/workflows/env-fe.yml` and other relevant workflows

**Changes**:
- Set `SELENIUM_VERSION` environment variable from workflow input
- Add version validation step before tests
- Add retry configuration via environment variables
- Update Grid startup to include version validation

**Workflow Steps**:
1. Start Selenium Grid
2. Wait for Grid to be ready (existing)
3. Validate Grid version (new)
4. Run tests

---

### Phase 8: Create Test Utilities

#### Step 8.1: Create Test Helper Methods
**Location**: `src/test/java/com/cjs/qa/utilities/GridTestUtils.java`

**Purpose**: Utility methods for testing Grid functionality

**Methods**:
- `waitForGridReady(String gridUrl, int timeoutSeconds)`
- `validateGridVersion(String gridUrl, String expectedVersion)`
- `getGridStatus(String gridUrl)`
- `isGridHealthy(String gridUrl)`

---

### Phase 9: Add Configuration Documentation

#### Step 8.1: Document Configuration Options
**Location**: `docs/guides/infrastructure/SELENIUM_GRID.md` (create if needed)

**Content**:
- Environment variables for retry configuration
- Version validation options
- Troubleshooting guide
- Examples of common scenarios

---

## 📝 Detailed Implementation Steps

### Step 1: Create Version Validator Class

1. Create `src/test/java/com/cjs/qa/utilities/SeleniumGridVersionValidator.java`
2. Implement `getGridServerVersion()` method:
   - Query `/wd/hub/status` endpoint
   - Parse JSON response to extract version
   - Handle connection errors gracefully
3. Implement `getClientVersion()` method:
   - Read from Selenium library properties
   - Fallback to `pom.xml` version if needed
4. Implement `validateVersion()` method:
   - Compare server and client versions
   - Support configurable tolerance
   - Throw `VersionMismatchException` if incompatible
5. Add unit tests for version comparison logic

### Step 2: Create Retry Utility Class

1. Create `src/test/java/com/cjs/qa/utilities/RetryableGridConnection.java`
2. Implement exponential backoff calculation:
   - Base delay: 1 second
   - Max delay: 10 seconds
   - Formula: `min(baseDelay * 2^attempt, maxDelay) + jitter`
3. Implement error categorization:
   - Check exception type and message
   - Determine if error is transient
4. Implement retry loop:
   - Attempt connection
   - Catch exceptions
   - Check if should retry
   - Calculate backoff delay
   - Sleep and retry
5. Add comprehensive logging:
   - Log each retry attempt
   - Log backoff delay
   - Log final success/failure

### Step 3: Enhance SeleniumGridConfig

1. Add version validation method
2. Add health check method
3. Add configuration getters
4. Add static validation on first access (optional)
5. Update JavaDoc comments

### Step 4: Refactor SeleniumWebDriver

1. Extract retry logic to use `RetryableGridConnection`
2. Add version validation before connection
3. Add health check before connection
4. Improve error messages
5. Add structured logging
6. Maintain backward compatibility

### Step 5: Update Wait Scripts

1. Add version validation option to `wait-for-grid.sh`
2. Add retry logic with exponential backoff
3. Improve error messages
4. Add configuration options
5. Update script documentation

### Step 6: Add Pre-Push Version Validation

1. Update pre-push hook to call `validate-dependency-versions.sh`
2. Add Docker Compose version validation to script
3. Test pre-push hook with version mismatches
4. Test pre-push hook with matching versions
5. Update hook installation script if needed

### Step 7: Update CI/CD Workflows

1. Add `SELENIUM_VERSION` environment variable
2. Add version validation step
3. Configure retry parameters
4. Update documentation

### Step 8: Create Test Utilities

1. Create `GridTestUtils.java`
2. Implement helper methods
3. Add unit tests
4. Add integration tests

### Step 9: Documentation

1. Create/update Grid documentation
2. Document configuration options
3. Add troubleshooting guide
4. Add examples
5. Document pre-push validation process

---

## 🧪 Testing Plan

### Unit Tests

1. **Version Validator Tests**:
   - Test exact version match
   - Test minor version tolerance
   - Test patch version tolerance
   - Test version mismatch detection
   - Test connection error handling

2. **Retry Logic Tests**:
   - Test exponential backoff calculation
   - Test error categorization
   - Test retry attempts
   - Test timeout handling
   - Test jitter application

### Integration Tests

1. **Grid Connection Tests**:
   - Test successful connection
   - Test version validation
   - Test retry on transient errors
   - Test failure on permanent errors
   - Test timeout scenarios

2. **End-to-End Tests**:
   - Test full test execution with new logic
   - Test error scenarios
   - Test configuration options

### Manual Testing

1. Test with matching versions
2. Test with mismatched versions
3. Test with Grid not ready
4. Test with connection failures
5. Test retry behavior
6. Test configuration options

---

## 📋 Configuration Reference

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SELENIUM_GRID_VERSION_TOLERANCE` | `EXACT` | Version matching tolerance: `EXACT`, `MINOR`, `PATCH`, `NONE` |
| `SELENIUM_GRID_MAX_RETRIES` | `5` | Maximum retry attempts |
| `SELENIUM_GRID_RETRY_BASE_DELAY_MS` | `1000` | Base delay for exponential backoff (milliseconds) |
| `SELENIUM_GRID_RETRY_MAX_DELAY_MS` | `10000` | Maximum delay for exponential backoff (milliseconds) |
| `SELENIUM_GRID_RETRY_TIMEOUT_MS` | `30000` | Total timeout for all retry attempts (milliseconds) |
| `SELENIUM_VERSION` | (from pom.xml) | Expected Selenium version for validation |
| `SKIP_VERSION_CHECK` | `false` | Skip version validation (for testing) |

### System Properties

| Property | Default | Description |
|----------|---------|-------------|
| `selenium.grid.version.tolerance` | `EXACT` | Version matching tolerance |
| `selenium.grid.max.retries` | `5` | Maximum retry attempts |
| `selenium.grid.retry.base.delay.ms` | `1000` | Base delay for exponential backoff |
| `selenium.grid.retry.max.delay.ms` | `10000` | Maximum delay for exponential backoff |
| `selenium.grid.retry.timeout.ms` | `30000` | Total timeout for retry attempts |

---

## 🐛 Error Handling

### Version Mismatch Errors

**Error**: `VersionMismatchException`
**Message**: "Selenium Grid server version (X.X.X) does not match client version (Y.Y.Y)"
**Action**: Fail fast, do not retry
**Resolution**: Update Grid server or client to matching versions

### Grid Not Ready Errors

**Error**: `GridNotReadyException`
**Message**: "Selenium Grid is not ready at {url}"
**Action**: Retry with exponential backoff
**Resolution**: Wait for Grid to start, check Grid health

### Connection Errors

**Error**: `GridConnectionException`
**Message**: "Failed to connect to Selenium Grid at {url}: {error}"
**Action**: Retry if transient (connection refused, timeout), fail fast if permanent (invalid URL)
**Resolution**: Check Grid URL, network connectivity, firewall settings

---

## 📚 References

- Selenium Grid Status API: `/wd/hub/status`
- Selenium Version API: Response includes `value.version`
- Current Selenium Version: 4.39.0 (from `pom.xml`)
- Grid Docker Images: `selenium/hub:4.39.0`, `selenium/node-chrome:4.39.0`, etc.

---

## ✅ Acceptance Criteria

### Runtime Validation
- [x] Version validation utility class created (Phase 1)
- [ ] Version validation works correctly for matching versions (needs testing)
- [ ] Version validation fails fast for mismatched versions (needs testing)
- [x] Retry logic uses exponential backoff with jitter (Phase 2)
- [x] Retry logic only retries on transient errors (Phase 2)
- [ ] Configuration options are respected (Phase 1 complete)
- [x] Error messages are clear and actionable (Phase 1)
- [x] Logging provides useful diagnostics (Phase 1)

### Pre-Push Validation
- [ ] Pre-push hook validates Selenium versions before push
- [ ] Version validation script checks `pom.xml` vs workflow files
- [ ] Version validation script checks Docker Compose files
- [ ] Pre-push hook fails push on version mismatches
- [ ] Clear error messages for version mismatches
- [ ] Validation is fast (< 5 seconds)
- [ ] Validation skips for documentation-only changes

### Testing
- [ ] All existing tests pass
- [ ] New unit tests pass
- [ ] Integration tests pass
- [ ] Pre-push hook tested with version mismatches
- [ ] Pre-push hook tested with matching versions

### Documentation
- [ ] Documentation is updated
- [ ] CI/CD workflows are updated
- [ ] Pre-push validation process documented

---

## 🚀 Next Steps

1. Review and approve this implementation plan
2. Start with Phase 1 (Version Validator)
3. Implement incrementally, testing after each phase
4. Update documentation as implementation progresses
5. Add to remaining work summary when complete

---

**Last Updated**: 2026-01-03  
**Document Location**: `docs/work/20260103_SELENIUM_GRID_ENHANCEMENTS.md`

