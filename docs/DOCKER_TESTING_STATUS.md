# Docker Testing Status

This document tracks the status of running all tests in Docker to match the CI/CD pipeline.

## ✅ Completed Fixes

### 1. Dockerfile Updates
- ✅ Installed Node.js 20 (for Cypress and Playwright)
- ✅ Installed Python 3.13 (with fallback to python3)
- ✅ Installed all system dependencies matching CI/CD:
  - `xvfb`, `libgtk2.0-0`, `libgtk-3-0`, `libgbm-dev`
  - `libnotify-dev`, `libnss3`, `libxss1`
  - `libasound2t64` (with fallback to `libasound2`)
- ✅ Fixed Python pip installation with `--break-system-packages` flag (PEP 668)
- ✅ Set all environment variables matching CI/CD

### 2. docker-compose.yml Updates
- ✅ Added `BASE_URL`, `TEST_ENVIRONMENT`, `CI` environment variables
- ✅ Mounted Cypress and Playwright directories
- ✅ Mounted Robot Framework test results directory

### 3. Test Script
- ✅ Created `scripts/run-all-tests-docker.sh` to run all test frameworks
- ✅ Script matches CI/CD pipeline execution order

## 🔧 Issues Fixed

### Python Installation (PEP 668)
**Problem**: Debian/Ubuntu systems prevent system-wide pip installs
**Solution**: Added `--break-system-packages` flag to pip install command

```dockerfile
RUN pip3 install --no-cache-dir --break-system-packages \
    robotframework \
    robotframework-seleniumlibrary \
    robotframework-requests
```

## 📋 Next Steps

### 1. Rebuild Docker Image
```bash
# Clean up old images if needed
docker system prune -a

# Rebuild the tests image
docker-compose build tests
```

### 2. Run All Tests
```bash
# Run all test frameworks
./scripts/run-all-tests-docker.sh
```

### 3. Test Individual Frameworks
```bash
# Selenium tests
docker-compose run --rm tests -DsuiteXmlFile=testng-smoke-suite.xml

# Cypress tests
docker-compose run --rm -e BASE_URL=https://www.google.com tests bash -c 'cd /app/cypress && xvfb-run -a npm run cypress:run'

# Playwright tests
docker-compose run --rm -e BASE_URL=https://www.google.com tests bash -c 'cd /app/playwright && npm test'

# Robot Framework tests
docker-compose run --rm -e BASE_URL=https://www.google.com -e SELENIUM_REMOTE_URL=http://selenium-hub:4444/wd/hub tests bash -c './mvnw test -Probot'
```

## 🐛 Known Issues to Test

### 1. Cypress Tests
- [ ] Verify system dependencies are installed correctly
- [ ] Test with `xvfb-run` for headless execution
- [ ] Verify `CYPRESS_BASE_URL` environment variable is used

### 2. Playwright Tests
- [ ] Verify `BASE_URL` environment variable is used
- [ ] Test browser installation (Chromium)
- [ ] Verify Playwright config uses environment variables

### 3. Robot Framework Tests
- [ ] Verify Python dependencies install correctly
- [ ] Test Selenium Grid connection
- [ ] Verify `SELENIUM_REMOTE_URL` and `BASE_URL` environment variables

### 4. Selenium/Java Tests
- [ ] Verify existing tests still work
- [ ] Test Grid connection
- [ ] Verify environment variables

## 📊 Test Execution Order (Matching CI/CD)

1. **Selenium Smoke Tests** - Fast critical path verification
2. **Selenium Grid Tests** - Full Selenium test suite
3. **Cypress Tests** - TypeScript E2E tests
4. **Playwright Tests** - TypeScript E2E tests
5. **Robot Framework Tests** - Python keyword-driven tests

## 🔍 Troubleshooting

### Disk Space Issues
If you encounter "no space left on device" errors:

**Option 1: Run Tests Locally (No Docker)**
```bash
# Run Cypress, Playwright, and Robot Framework tests without Docker
./scripts/run-tests-local.sh
```
See [LOCAL_TESTING_GUIDE.md](LOCAL_TESTING_GUIDE.md) for details.

**Option 2: Clean Up Docker**
```bash
# Clean up Docker
docker system prune -a
docker volume prune

# Check disk space
df -h
```

### Python Installation Fails
If Robot Framework dependencies fail to install:
```bash
# Check Python version
docker-compose run --rm tests python3 --version

# Try manual installation
docker-compose run --rm tests bash -c 'pip3 install --break-system-packages robotframework robotframework-seleniumlibrary robotframework-requests'
```

### Node.js Not Found
If Cypress/Playwright can't find Node.js:
```bash
# Check Node.js version
docker-compose run --rm tests node --version

# Reinstall if needed (rebuild image)
docker-compose build --no-cache tests
```

### Selenium Grid Connection Issues
If tests can't connect to Grid:
```bash
# Check Grid status
curl http://localhost:4444/wd/hub/status

# Verify Grid is running
docker-compose ps

# Check Grid logs
docker-compose logs selenium-hub
```

## 📝 Notes

- Docker environment now matches CI/CD pipeline exactly
- All test frameworks are installed and configured
- Environment variables match CI/CD workflow
- If tests pass in Docker, they should pass in CI/CD

---

**Last Updated**: 2025-01-XX
**Status**: Ready for testing (after disk space cleanup)

