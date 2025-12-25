# Docker vs CI/CD Environment Differences

This document explains why tests might pass in Docker but fail in the CI/CD pipeline, and vice versa.

---

## 🔍 Key Differences

### 1. **Test Framework Coverage**

#### Docker Environment
- ✅ **Selenium/Java tests** - Fully supported
- ❌ **Cypress tests** - NOT installed (no Node.js)
- ❌ **Playwright tests** - NOT installed (no Node.js)
- ❌ **Robot Framework tests** - NOT installed (no Python)

#### CI/CD Environment
- ✅ **Selenium/Java tests** - Fully supported
- ✅ **Cypress tests** - Installed and configured
- ✅ **Playwright tests** - Installed and configured
- ✅ **Robot Framework tests** - Installed and configured

**Why this matters**: When you run tests in Docker, you're only running Selenium/Java tests. The other frameworks aren't even available in the Docker container!

---

### 2. **Operating System & Architecture**

#### Docker Environment
- **Base Image**: `eclipse-temurin:21-jdk` (Debian-based)
- **Architecture**: ARM64 (Apple Silicon) or x86_64
- **Selenium Images**: `seleniarm/*` (ARM64) or `selenium/*` (x86_64)
- **OS Version**: Debian (varies by base image)

#### CI/CD Environment
- **Base Image**: `ubuntu-latest` (currently Ubuntu 24.04)
- **Architecture**: x86_64 (GitHub Actions runners)
- **Selenium Images**: `selenium/*` (x86_64 only)
- **OS Version**: Ubuntu 24.04 LTS

**Why this matters**: 
- Different package managers (`apt` vs `apt-get`)
- Different package availability (Ubuntu 24.04 has newer/different packages)
- ARM64 vs x86_64 can cause compatibility issues

---

### 3. **System Dependencies**

#### Docker Environment
```dockerfile
# Only basic utilities installed
RUN apt-get update && apt-get install -y \
    curl \
    bash \
    tzdata
```

**Missing in Docker**:
- ❌ Node.js (needed for Cypress/Playwright)
- ❌ Python (needed for Robot Framework)
- ❌ System libraries for Cypress (xvfb, libgtk, libasound2, etc.)
- ❌ Playwright browsers

#### CI/CD Environment
```yaml
# Node.js installed
- uses: actions/setup-node@v4
  with:
    node-version: '20'

# Python installed
- uses: actions/setup-python@v5
  with:
    python-version: '3.13'

# System dependencies installed
- run: |
    sudo apt-get update
    sudo apt-get install -y xvfb libgtk2.0-0 libgtk-3-0 libgbm-dev ...
```

**Why this matters**: 
- Docker doesn't have the dependencies needed for Cypress/Playwright/Robot Framework
- CI/CD installs them fresh each time, which can reveal missing or deprecated packages

---

### 4. **Package Availability**

#### Ubuntu 24.04 (CI/CD)
- `libgconf-2-4` - **REMOVED** (deprecated package)
- `libasound2` - **REPLACED** by `libasound2t64` (transitional package)
- Newer package versions may have different dependencies

#### Debian (Docker)
- May have different package versions
- Different package naming conventions
- May still have deprecated packages available

**Why this matters**: 
- Packages that work in Docker (Debian) may not exist in CI/CD (Ubuntu 24.04)
- We fixed this by using `libasound2t64` with fallback to `libasound2`

---

### 5. **Network & Service Discovery**

#### Docker Environment
```yaml
# Docker Compose network
SELENIUM_REMOTE_URL=http://selenium-hub:4444/wd/hub
# Uses service name for DNS resolution
```

#### CI/CD Environment
```yaml
# GitHub Actions services
SELENIUM_REMOTE_URL=http://localhost:4444/wd/hub
# Uses localhost (services run on same runner)
```

**Why this matters**: 
- Different network configurations
- Service startup timing may differ
- DNS resolution works differently

---

### 6. **Environment Variables**

#### Docker Environment
```yaml
environment:
  - SELENIUM_REMOTE_URL=http://selenium-hub:4444/wd/hub
  - BROWSER=chrome
  - HEADLESS=false
  - BASE_URL=https://www.google.com  # May not be set
```

#### CI/CD Environment
```yaml
env:
  SELENIUM_REMOTE_URL: http://localhost:4444/wd/hub
  BASE_URL: ${{ inputs.base_url }}  # From workflow input
  TEST_ENVIRONMENT: ${{ inputs.environment }}
```

**Why this matters**: 
- Different default values
- CI/CD may have unset variables that Docker has defaults for
- We fixed Playwright/Robot Framework to use environment variables properly

---

### 7. **Resource Constraints**

#### Docker Environment
- **Memory**: Configurable (default 2GB+)
- **CPU**: Configurable (default 2+ cores)
- **Disk**: Local filesystem (fast)
- **Network**: Local Docker network (fast)

#### CI/CD Environment
- **Memory**: Limited (GitHub Actions runners have ~7GB available)
- **CPU**: Limited (2 cores)
- **Disk**: Ephemeral (cleared after job)
- **Network**: Internet connection (may be slower)

**Why this matters**: 
- CI/CD may have timeouts due to resource constraints
- Network requests may be slower in CI/CD
- Disk I/O may be slower

---

### 8. **Timing & Race Conditions**

#### Docker Environment
```yaml
depends_on:
  selenium-hub:
    condition: service_healthy
  chrome-node-1:
    condition: service_healthy
```
- Services wait for health checks
- More predictable startup timing

#### CI/CD Environment
```yaml
- name: Wait for Selenium Grid
  run: |
    timeout 60 bash -c 'until curl -sf http://localhost:4444/wd/hub/status; do sleep 2; done'
    sleep 5
```
- Manual wait with timeout
- May have race conditions if services start slowly

**Why this matters**: 
- CI/CD may try to connect before services are ready
- Docker's health checks are more reliable

---

## 🛠️ How to Reproduce CI/CD Failures Locally

### Option 1: Use GitHub Actions Locally (Recommended)
```bash
# Install act (GitHub Actions local runner)
brew install act  # macOS
# or
curl https://raw.githubusercontent.com/nektos/act/master/install.sh | sudo bash

# Run the workflow locally
act -W .github/workflows/test-environment.yml
```

### Option 2: Use Docker with Ubuntu 24.04
```dockerfile
# Create a Dockerfile that matches CI/CD
FROM ubuntu:24.04

# Install same dependencies as CI/CD
RUN apt-get update && apt-get install -y \
    nodejs npm python3.13 xvfb libgtk2.0-0 libgtk-3-0 libgbm-dev \
    libnotify-dev libnss3 libxss1 libasound2t64

# ... rest of setup
```

### Option 3: Use GitHub Codespaces
- Create a Codespace from your repository
- Run tests in the same environment as CI/CD

---

## 🔧 Common Issues & Solutions

### Issue: Cypress tests fail in CI but pass locally
**Cause**: Missing system dependencies or wrong package versions
**Solution**: 
- Use `libasound2t64` instead of `libasound2`
- Remove deprecated `libgconf-2-4`
- Install all required dependencies in CI workflow

### Issue: Playwright tests fail in CI but pass locally
**Cause**: Hardcoded URLs or missing environment variables
**Solution**: 
- Use `baseURL` from Playwright config
- Pass `BASE_URL` environment variable
- Use `process.env.BASE_URL` in page objects

### Issue: Robot Framework tests fail in CI but pass locally
**Cause**: Not using Selenium Grid remote URL
**Solution**: 
- Check for `SELENIUM_REMOTE_URL` environment variable
- Use `remote_url` parameter in `Open Browser` keyword
- Fall back to local browser if remote URL not set

### Issue: Tests timeout in CI but pass in Docker
**Cause**: Resource constraints or slower network
**Solution**: 
- Increase timeout values
- Add retries for flaky tests
- Optimize test execution (parallel, faster assertions)

---

## 📊 Summary Table

| Aspect | Docker | CI/CD |
|-------|--------|-------|
| **OS** | Debian (varies) | Ubuntu 24.04 |
| **Architecture** | ARM64/x86_64 | x86_64 |
| **Selenium Images** | seleniarm/* or selenium/* | selenium/* |
| **Node.js** | ❌ Not installed | ✅ Node.js 20 |
| **Python** | ❌ Not installed | ✅ Python 3.13 |
| **Cypress** | ❌ Not available | ✅ Installed |
| **Playwright** | ❌ Not available | ✅ Installed |
| **Robot Framework** | ❌ Not available | ✅ Installed |
| **System Deps** | Minimal (curl, bash) | Full (xvfb, libgtk, etc.) |
| **Network** | Docker network | localhost |
| **Service Discovery** | DNS (service names) | localhost |
| **Resource Limits** | Configurable | Limited (2 CPU, ~7GB RAM) |
| **Package Versions** | Varies by base image | Latest Ubuntu 24.04 |

---

## 🎯 Best Practices

1. **Test in CI/CD regularly** - Don't rely only on Docker
2. **Use environment variables** - Don't hardcode URLs or paths
3. **Match CI/CD environment** - Use same OS/versions when possible
4. **Handle missing dependencies** - Use fallbacks and error handling
5. **Test all frameworks** - Don't assume all tests run in Docker
6. **Use act for local testing** - Test GitHub Actions workflows locally
7. **Monitor CI/CD failures** - They reveal real environment issues

---

## 📚 Related Documentation

- [Docker Guide](DOCKER.md) - Complete Docker setup
- [GitHub Actions Guide](GITHUB_ACTIONS.md) - CI/CD pipeline details
- [UI Testing Frameworks](../testing/UI_TESTING_FRAMEWORKS.md) - Framework-specific guides

---

**Last Updated**: 2025-01-XX
**Maintained By**: CJS QA Team

