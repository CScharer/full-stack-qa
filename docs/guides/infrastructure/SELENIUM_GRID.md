# Selenium Grid Configuration Guide

Complete guide for configuring and using Selenium Grid with version validation and retry logic enhancements.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Version Validation](#version-validation)
- [Retry Logic Configuration](#retry-logic-configuration)
- [Environment Variables](#environment-variables)
- [System Properties](#system-properties)
- [Usage Examples](#usage-examples)
- [Troubleshooting](#troubleshooting)
- [Related Documentation](#related-documentation)

---

## 🎯 Overview

This guide covers the enhanced Selenium Grid features including:
- **Version Validation** - Ensures Grid server and client versions match
- **Retry Logic** - Automatic retry with exponential backoff for transient errors
- **Health Checks** - Grid readiness and health monitoring
- **Configuration Options** - Flexible configuration via environment variables and system properties

For general Docker and Grid setup, see [DOCKER.md](./DOCKER.md).

---

## 🔍 Version Validation

### Overview

Version validation ensures that the Selenium Grid server version matches the client version before attempting connections. This prevents compatibility issues and provides clear error messages when versions don't match.

### How It Works

1. **Runtime Validation**: Automatically validates Grid version when connecting via `SeleniumWebDriver`
2. **Pre-Push Validation**: Validates versions in `pom.xml`, workflow files, and Docker Compose files before push
3. **Wait Script Validation**: Optional version validation in `wait-for-grid.sh` script

### Configuration

**Version Tolerance Levels:**

| Tolerance | Description | Example |
|-----------|-------------|---------|
| `EXACT` | Must match exactly (default) | `4.39.0` == `4.39.0` ✅ |
| `MINOR` | Allow minor version differences | `4.39.0` == `4.40.0` ✅ |
| `PATCH` | Allow patch version differences | `4.39.0` == `4.39.1` ✅ |
| `NONE` | Skip version validation | Always passes |

**Set Tolerance:**

```bash
# Environment variable
export SELENIUM_GRID_VERSION_TOLERANCE=MINOR

# System property
-Dselenium.grid.version.tolerance=MINOR
```

**Skip Version Validation:**

```bash
# Skip in Java code
export SKIP_VERSION_CHECK=true

# Skip in wait script
SKIP_VERSION_CHECK=true ./scripts/ci/wait-for-grid.sh
```

### Usage Examples

**In Java Code:**
```java
// Automatic validation (via SeleniumWebDriver)
SeleniumWebDriver driver = new SeleniumWebDriver();
driver.initializeWebDriver(); // Version validation runs automatically

// Manual validation
SeleniumGridConfig.validateGridVersion("http://localhost:4444/wd/hub");

// Using test utilities
GridTestUtils.validateGridVersion(gridUrl, "4.39.0");
```

**In Bash Scripts:**
```bash
# With version validation
SELENIUM_VERSION="4.39.0" ./scripts/ci/wait-for-grid.sh

# Skip version check
SKIP_VERSION_CHECK=true ./scripts/ci/wait-for-grid.sh
```

---

## 🔄 Retry Logic Configuration

### Overview

The retry logic automatically retries Grid connections when transient errors occur (connection refused, timeouts, etc.). It uses exponential backoff with jitter to prevent thundering herd problems.

### How It Works

1. **Error Categorization**: Distinguishes between transient errors (retry) and permanent errors (fail fast)
2. **Exponential Backoff**: Delays increase exponentially: 1s, 2s, 4s, 8s, 10s (max)
3. **Jitter**: Adds ±10% random variation to prevent synchronized retries
4. **Timeout Protection**: Overall timeout across all retry attempts

### Configuration

**Retry Parameters:**

| Parameter | Default | Description |
|-----------|---------|-------------|
| `SELENIUM_GRID_MAX_RETRIES` | `5` | Maximum number of retry attempts |
| `SELENIUM_GRID_RETRY_BASE_DELAY_MS` | `1000` | Base delay for exponential backoff (milliseconds) |
| `SELENIUM_GRID_RETRY_MAX_DELAY_MS` | `10000` | Maximum delay cap (milliseconds) |
| `SELENIUM_GRID_RETRY_TIMEOUT_MS` | `30000` | Total timeout for all retries (milliseconds) |

**Set Retry Configuration:**

```bash
# Environment variables
export SELENIUM_GRID_MAX_RETRIES=3
export SELENIUM_GRID_RETRY_BASE_DELAY_MS=500
export SELENIUM_GRID_RETRY_MAX_DELAY_MS=5000
export SELENIUM_GRID_RETRY_TIMEOUT_MS=15000

# System properties
-Dselenium.grid.max.retries=3
-Dselenium.grid.retry.base.delay.ms=500
-Dselenium.grid.retry.max.delay.ms=5000
-Dselenium.grid.retry.timeout.ms=15000
```

### Error Categories

**Transient Errors (Retried):**
- `ConnectException` - Connection refused
- `SocketException` - Socket errors
- `SocketTimeoutException` - Connection timeouts
- HTTP 503, 502, 504 - Service unavailable

**Permanent Errors (Fail Fast):**
- `UnknownHostException` - Invalid hostname
- `MalformedURLException` - Invalid URL
- `IllegalArgumentException` - Invalid arguments
- Version mismatch errors
- Authentication failures

### Usage Examples

**In Java Code:**
```java
// Automatic retry (via SeleniumWebDriver)
SeleniumWebDriver driver = new SeleniumWebDriver();
driver.initializeWebDriver(); // Retry logic runs automatically

// Manual retry
RemoteWebDriver driver = RetryableGridConnection.connectWithRetry(
    gridUrl, 
    capabilities
);
```

---

## ⚙️ Environment Variables

### Version Validation

| Variable | Default | Description |
|----------|---------|-------------|
| `SELENIUM_GRID_VERSION_TOLERANCE` | `EXACT` | Version matching tolerance |
| `SELENIUM_VERSION` | (from pom.xml) | Expected Selenium version |
| `SKIP_VERSION_CHECK` | `false` | Skip version validation |

### Retry Configuration

| Variable | Default | Description |
|----------|---------|-------------|
| `SELENIUM_GRID_MAX_RETRIES` | `5` | Maximum retry attempts |
| `SELENIUM_GRID_RETRY_BASE_DELAY_MS` | `1000` | Base delay (milliseconds) |
| `SELENIUM_GRID_RETRY_MAX_DELAY_MS` | `10000` | Maximum delay (milliseconds) |
| `SELENIUM_GRID_RETRY_TIMEOUT_MS` | `30000` | Total timeout (milliseconds) |

### Grid Connection

| Variable | Default | Description |
|----------|---------|-------------|
| `SELENIUM_REMOTE_URL` | `http://localhost:4444/wd/hub` | Grid hub URL |

### Example Configuration

```bash
# Complete configuration example
export SELENIUM_REMOTE_URL="http://localhost:4444/wd/hub"
export SELENIUM_VERSION="4.39.0"
export SELENIUM_GRID_VERSION_TOLERANCE="EXACT"
export SELENIUM_GRID_MAX_RETRIES="5"
export SELENIUM_GRID_RETRY_BASE_DELAY_MS="1000"
export SELENIUM_GRID_RETRY_MAX_DELAY_MS="10000"
export SELENIUM_GRID_RETRY_TIMEOUT_MS="30000"
```

---

## 🔧 System Properties

All environment variables can also be set as system properties with dot notation:

| Property | Equivalent Environment Variable |
|----------|----------------------------------|
| `selenium.grid.version.tolerance` | `SELENIUM_GRID_VERSION_TOLERANCE` |
| `selenium.version` | `SELENIUM_VERSION` |
| `selenium.grid.max.retries` | `SELENIUM_GRID_MAX_RETRIES` |
| `selenium.grid.retry.base.delay.ms` | `SELENIUM_GRID_RETRY_BASE_DELAY_MS` |
| `selenium.grid.retry.max.delay.ms` | `SELENIUM_GRID_RETRY_MAX_DELAY_MS` |
| `selenium.grid.retry.timeout.ms` | `SELENIUM_GRID_RETRY_TIMEOUT_MS` |

**Usage:**
```bash
mvn test -Dselenium.grid.max.retries=3 \
         -Dselenium.grid.retry.base.delay.ms=500 \
         -Dselenium.version=4.39.0
```

---

## 📝 Usage Examples

### Basic Usage

**Java Test:**
```java
@BeforeMethod
public void setUp() {
    // Version validation and retry logic run automatically
    SeleniumWebDriver driver = new SeleniumWebDriver();
    driver.initializeWebDriver();
}
```

**Bash Script:**
```bash
# Wait for Grid with version validation
SELENIUM_VERSION="4.39.0" ./scripts/ci/wait-for-grid.sh
```

### Advanced Configuration

**Custom Retry Settings:**
```java
// Set via system properties before test
System.setProperty("selenium.grid.max.retries", "10");
System.setProperty("selenium.grid.retry.base.delay.ms", "500");

// Or via environment variables
// export SELENIUM_GRID_MAX_RETRIES=10
// export SELENIUM_GRID_RETRY_BASE_DELAY_MS=500
```

**Using Test Utilities:**
```java
// Wait for Grid with custom timeout
GridTestUtils.waitForGridReady(gridUrl, 60); // 60 second timeout

// Validate version
GridTestUtils.validateGridVersion(gridUrl, "4.39.0");

// Check Grid health (comprehensive check)
if (GridTestUtils.isGridHealthy(gridUrl)) {
    // Grid is ready and healthy
}

// Get status information
String status = GridTestUtils.getGridStatus(gridUrl);
System.out.println(status);
```

### CI/CD Pipeline

**GitHub Actions:**
```yaml
- name: Wait for Selenium Grid
  env:
    SELENIUM_VERSION: ${{ inputs.selenium_version || '4.39.0' }}
  run: |
    ./scripts/ci/wait-for-grid.sh "http://localhost:4444/wd/hub/status" 10

- name: Run Grid Tests
  env:
    SELENIUM_REMOTE_URL: http://localhost:4444/wd/hub
    SELENIUM_VERSION: ${{ inputs.selenium_version || '4.39.0' }}
    SELENIUM_GRID_MAX_RETRIES: '5'
    SELENIUM_GRID_RETRY_BASE_DELAY_MS: '1000'
    SELENIUM_GRID_RETRY_MAX_DELAY_MS: '10000'
    SELENIUM_GRID_RETRY_TIMEOUT_MS: '30000'
  run: |
    mvn test
```

---

## 🐛 Troubleshooting

### Version Mismatch Errors

**Error:** `Selenium Grid server version (X.X.X) does not match client version (Y.Y.Y)`

**Solutions:**
1. Update Grid Docker images to match client version:
   ```bash
   # Update docker-compose.yml
   image: selenium/hub:4.39.0  # Match pom.xml version
   ```

2. Update `pom.xml` to match Grid version:
   ```xml
   <selenium.version>4.39.0</selenium.version>
   ```

3. Use version tolerance (if appropriate):
   ```bash
   export SELENIUM_GRID_VERSION_TOLERANCE=MINOR
   ```

4. Skip validation (for testing only):
   ```bash
   export SKIP_VERSION_CHECK=true
   ```

### Connection Retry Issues

**Problem:** Tests fail after all retries exhausted

**Solutions:**
1. Increase retry attempts:
   ```bash
   export SELENIUM_GRID_MAX_RETRIES=10
   ```

2. Increase timeout:
   ```bash
   export SELENIUM_GRID_RETRY_TIMEOUT_MS=60000  # 60 seconds
   ```

3. Check Grid is actually running:
   ```bash
   curl http://localhost:4444/wd/hub/status
   ```

4. Verify Grid URL:
   ```bash
   echo $SELENIUM_REMOTE_URL
   ```

### Grid Not Ready

**Problem:** `Grid is not ready` warnings

**Solutions:**
1. Wait longer for Grid to start:
   ```java
   GridTestUtils.waitForGridReady(gridUrl, 60); // 60 second timeout
   ```

2. Check Grid logs:
   ```bash
   docker-compose logs selenium-hub
   ```

3. Verify Grid health:
   ```java
   boolean healthy = GridTestUtils.isGridHealthy(gridUrl);
   ```

### Pre-Push Validation Failures

**Error:** Version validation fails in pre-push hook

**Solutions:**
1. Check version mismatches:
   ```bash
   ./scripts/validate-dependency-versions.sh
   ```

2. Update mismatched versions:
   - Update `pom.xml` if workflow version is correct
   - Update workflow file if `pom.xml` version is correct
   - Update Docker Compose files if using versioned tags

3. Bypass hook (not recommended):
   ```bash
   git push --no-verify
   ```

---

## 🧪 Testing & Verification

### Pipeline Testing

When running in CI/CD pipelines, look for these log messages to verify the enhancements are working:

**Version Validation:**
```
✅ Version validation passed: 4.39.0
🔍 Validating Grid version...
```

**Retry Logic:**
```
Connection attempt 1/5 to Grid at http://localhost:4444/wd/hub
Transient error on attempt 1/5: Connection refused. Retrying in 1000ms...
✅ Successfully connected to Grid on attempt 2/5
```

**Pre-Push Validation:**
```
Phase 4: Docker Compose Version Validation
Checking docker-compose.yml...
✅ Version matches pom.xml: 4.39.0
```

### Key Test Scenarios

1. **Version Matching**: Tests should pass when Grid and client versions match
2. **Version Mismatch**: Tests should fail fast with clear error message
3. **Grid Not Ready**: Tests should retry with exponential backoff
4. **Connection Failures**: Transient errors should retry, permanent errors should fail fast
5. **Configuration**: Custom retry parameters should be respected

---

## 📚 Related Documentation

- **[DOCKER.md](./DOCKER.md)** - Docker and Grid setup guide
- **[VERSION_TRACKING.md](../../process/VERSION_TRACKING.md)** - Version tracking system
- **[VERSION_MONITORING.md](../../process/VERSION_MONITORING.md)** - Version monitoring and alerting

---

## 🔗 API Reference

### Utility Classes

**SeleniumGridConfig:**
- `getGridUrl()` - Get Grid URL from environment or default
- `validateGridVersion(String gridUrl)` - Validate Grid version
- `isGridReady(String gridUrl)` - Check if Grid is ready
- `getMaxRetries()` - Get retry configuration
- `getRetryBaseDelay()` - Get base delay
- `getRetryMaxDelay()` - Get max delay
- `getRetryTimeout()` - Get timeout

**SeleniumGridVersionValidator:**
- `validateVersion(String gridUrl)` - Validate version compatibility
- `getGridServerVersion(String gridUrl)` - Get Grid server version
- `getClientVersion()` - Get client version
- `isVersionCompatible(...)` - Check version compatibility

**RetryableGridConnection:**
- `connectWithRetry(String gridUrl, Capabilities)` - Connect with retry logic
- `isTransientError(Exception)` - Check if error is transient
- `calculateBackoff(int attempt)` - Calculate backoff delay

**GridTestUtils:**
- `waitForGridReady(String gridUrl, int timeoutSeconds)` - Wait for Grid (returns boolean)
- `waitForGridReady(String gridUrl)` - Wait for Grid with default timeout (returns boolean)
- `validateGridVersion(String gridUrl, String expectedVersion)` - Validate version (void, throws QAException)
- `getGridStatus(String gridUrl)` - Get status information (returns String JSON, throws QAException)
- `isGridHealthy(String gridUrl)` - Health check (returns boolean)

---

**Last Updated**: 2026-01-04  
**Implementation Completed**: 2026-01-04

