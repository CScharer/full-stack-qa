# Timeout Reference Guide

This document provides a comprehensive reference for all timeout values used across testing frameworks in this project.

## Overview

Timeouts are framework-specific and serve different purposes. This guide documents:
- **Framework-specific timeouts**: Used by individual frameworks (Cypress, Playwright, Robot Framework, Selenium)
- **Shared service timeouts**: Defined in `config/environments.json` for service startup/verification
- **Best practices**: When to use which timeout values

---

## Shared Service Timeouts

Defined in `config/environments.json` (used by CI/CD and service startup scripts):

```json
{
  "timeouts": {
    "serviceStartup": 120,        // Seconds - Max time to wait for service to start
    "serviceVerification": 30,   // Seconds - Max time to verify service is ready
    "apiClient": 10000,          // Milliseconds - API request timeout
    "webServer": 120000,         // Milliseconds - Web server response timeout
    "checkInterval": 2           // Seconds - Interval between health checks
  }
}
```

**Usage**:
- Service startup scripts (`scripts/start-be.sh`, `scripts/start-fe.sh`)
- CI/CD workflows (service health checks)
- Integration tests (Playwright integration config)

---

## Framework-Specific Timeouts

### Cypress

**Configuration** (`cypress/cypress.config.ts`):
```typescript
{
  defaultCommandTimeout: 15000,    // 15s - Default timeout for commands
  requestTimeout: 15000,           // 15s - HTTP request timeout
  responseTimeout: 15000,          // 15s - HTTP response timeout
  pageLoadTimeout: 30000,         // 30s - Page load timeout
}
```

**Common Inline Timeouts**:
- `5000` (5s) - Quick element visibility checks
- `10000` (10s) - Standard element waits
- `20000` (20s) - Page navigation/URL changes

**Best Practices**:
- Use `defaultCommandTimeout` for most element interactions
- Override with inline `{ timeout: X }` for specific cases
- Use `pageLoadTimeout` for navigation-heavy tests

---

### Playwright

**Configuration** (`playwright/playwright.integration.config.ts`):
```typescript
// Uses shared config/environments.json
const timeoutConfig = getTimeoutConfig();
{
  timeout: timeoutConfig.webServer,  // 120s - Web server timeout
  navigationTimeout: 120 * 1000,    // 120s - Navigation timeout
}
```

**Common Inline Timeouts**:
- `5000` (5s) - Quick element visibility checks
- `20000` (20s) - Page navigation/URL changes
- `60000` (60s) - CI/CD scenarios with network delays

**Best Practices**:
- Use `waitFor()` with explicit timeouts for element waits
- Use `waitForURL()` with timeout for navigation
- Increase timeouts in CI/CD environments

---

### Robot Framework

**Configuration** (`src/test/robot/resources/Common.robot`):
```robot
${TIMEOUT}                10s    # Standard timeout
${SHORT_TIMEOUT}          5s     # Quick checks
```

**Usage**:
- `TIMEOUT` (10s) - Page load, element visibility
- `SHORT_TIMEOUT` (5s) - Quick element checks

**Best Practices**:
- Use `${TIMEOUT}` for most waits
- Use `${SHORT_TIMEOUT}` for quick visibility checks
- Override with `timeout=` parameter when needed

---

### Selenium/Java

**Configuration** (varies by test class):
```java
// Common patterns:
Configuration.timeout = 5000;              // 5s - Selenide default
Configuration.pageLoadTimeout = 10000;    // 10s - Page load
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
```

**Best Practices**:
- Use explicit waits over implicit waits
- Set page load timeout based on application needs
- Use WebDriverWait with explicit conditions

---

## Timeout Comparison Table

| Framework | Command/Element | Page Load | Request/API | Notes |
|-----------|----------------|-----------|-------------|-------|
| **Cypress** | 15s (default) | 30s | 15s | Can override per command |
| **Playwright** | 30s (default) | 120s | N/A | Uses shared webServer timeout |
| **Robot Framework** | 10s | 10s | N/A | Uses Selenium under the hood |
| **Selenium/Java** | 5-10s | 10s | N/A | Varies by test class |

---

## When to Use Which Timeout

### Quick Element Checks (5s)
- Element visibility after known action
- Simple DOM queries
- **Use in**: All frameworks for quick checks

### Standard Element Waits (10-15s)
- Most element interactions
- Form submissions
- **Use in**: Cypress (15s), Robot Framework (10s)

### Page Navigation (20-30s)
- Full page loads
- URL changes
- Route transitions
- **Use in**: Cypress (30s), Playwright (20s)

### Service Startup (120s)
- Backend/Frontend service startup
- Health check verification
- **Use in**: CI/CD, service scripts

### API Requests (10s)
- HTTP API calls
- Backend verification
- **Use in**: Shared config, API tests

---

## Best Practices

1. **Start with defaults**: Use framework defaults for most cases
2. **Override when needed**: Increase for slow operations, decrease for quick checks
3. **Document exceptions**: If a test needs a non-standard timeout, document why
4. **Consider environment**: CI/CD may need longer timeouts than local
5. **Use shared config**: For service-related timeouts, use `config/environments.json`

---

## Updating Timeouts

### Framework-Specific
- **Cypress**: Update `cypress/cypress.config.ts`
- **Playwright**: Update `playwright/playwright.integration.config.ts` or inline
- **Robot Framework**: Update `src/test/robot/resources/Common.robot`
- **Selenium/Java**: Update individual test classes

### Shared Service Timeouts
- Update `config/environments.json` → `timeouts` section
- All frameworks using shared config will automatically use new values

---

## Related Documentation

- [UI Testing Frameworks Guide](./UI_TESTING_FRAMEWORKS.md)
- [Shared Configuration Guide](../work/20260111_SHARED_TEST_CONFIGURATION.md)
- Framework-specific READMEs:
  - [Cypress README](../../cypress/README.md)
  - [Playwright README](../../playwright/README.md)
  - [Robot Framework README](../../src/test/robot/README.md)
