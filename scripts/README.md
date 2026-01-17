# Helper Scripts

This directory contains helper scripts for common development tasks, organized by purpose.

## 📁 Directory Structure

```
scripts/
├── services/          # Service management (start/stop services)
├── tests/             # Test execution scripts
│   ├── frameworks/    # Framework-specific test runners
│   └── performance/   # Performance test runners
├── build/             # Build and compilation
├── quality/           # Code quality and validation
├── reporting/         # Report generation
├── utils/             # General utilities
├── ci/                # CI/CD scripts (existing)
├── docker/            # Docker scripts (existing)
├── lib/               # Common library functions (existing)
├── test/              # Test utilities (existing)
└── temp/              # Temporary scripts (existing)
```

## Available Scripts

### Service Management Scripts

**Location**: `scripts/services/`

For starting, stopping, and verifying application services (Backend and Frontend):

- **`services/start-be.sh`** - Start backend service
- **`services/start-fe.sh`** - Start frontend service
- **`services/start-env.sh`** - Start both services together
- **`services/start-services-for-ci.sh`** - Start services for CI/CD (idempotent)
- **`services/stop-services.sh`** - Stop all services

**Usage**:
```bash
# Start both services (dev environment)
./scripts/services/start-env.sh

# Start backend only
./scripts/services/start-be.sh --env test

# Stop all services
./scripts/services/stop-services.sh
```

**See**: [Service Scripts Guide](../docs/guides/infrastructure/SERVICE_SCRIPTS.md) for complete documentation.

---

### Test Execution Scripts

#### Common Test Scripts

**Location**: `scripts/tests/`

- **`tests/run-tests.sh`** - Run test suite with optional parameters
- **`tests/run-specific-test.sh`** - Run a specific test method
- **`tests/run-tests-local.sh`** - Run all test frameworks locally without Docker
- **`tests/run-smoke-tests.sh`** - Run smoke test suite
- **`tests/run-all-tests-docker.sh`** - Run all test frameworks in Docker

#### Framework-Specific Test Runners

**Location**: `scripts/tests/frameworks/`

- **`tests/frameworks/run-cypress-tests.sh`** - Run Cypress tests
- **`tests/frameworks/run-playwright-tests.sh`** - Run Playwright tests
- **`tests/frameworks/run-robot-tests.sh`** - Run Robot Framework tests
- **`tests/frameworks/run-vibium-tests.sh`** - Run Vibium tests
- **`tests/frameworks/run-backend-tests.sh`** - Run backend API tests
- **`tests/frameworks/run-frontend-tests.sh`** - Run frontend tests
- **`tests/frameworks/run-api-tests.sh`** - Run API integration tests
- **`tests/frameworks/run-integration-tests.sh`** - Run integration tests

#### Performance Test Runners

**Location**: `scripts/tests/performance/`

- **`tests/performance/run-all-performance-tests.sh`** - Run all performance tests
- **`tests/performance/run-gatling-tests.sh`** - Run Gatling performance tests
- **`tests/performance/run-jmeter-tests.sh`** - Run JMeter performance tests
- **`tests/performance/run-locust-tests.sh`** - Run Locust performance tests

**Usage Examples**:
```bash
# Run Cypress tests
./scripts/tests/frameworks/run-cypress-tests.sh run chrome

# Run Playwright tests
./scripts/tests/frameworks/run-playwright-tests.sh chromium

# Run all performance tests
./scripts/tests/performance/run-all-performance-tests.sh

# Run smoke tests
./scripts/tests/run-smoke-tests.sh
```

---

### Build Scripts

**Location**: `scripts/build/`

- **`build/compile.sh`** - Compile the project without running tests

**Usage**:
```bash
./scripts/build/compile.sh
```

---

### Code Quality Scripts

**Location**: `scripts/quality/`

- **`quality/format-code.sh`** - Format code (Prettier, Spotless, Google Java Format)
- **`quality/validate-pre-commit.sh`** - Validate code before commit
- **`quality/validate-dependency-versions.sh`** - Validate dependency versions
- **`quality/check_unused_imports.py`** - Check for unused imports

**Usage**:
```bash
# Format code (required before commit)
./scripts/quality/format-code.sh

# Validate before commit
./scripts/quality/validate-pre-commit.sh

# Check dependency versions
./scripts/quality/validate-dependency-versions.sh
```

---

### Reporting Scripts

**Location**: `scripts/reporting/`

- **`reporting/generate-allure-report.sh`** - Generate Allure test reports
- **`reporting/convert-performance-to-allure.sh`** - Convert performance test results to Allure format

**Usage**:
```bash
# Generate Allure report
./scripts/reporting/generate-allure-report.sh

# Convert performance results
./scripts/reporting/convert-performance-to-allure.sh
```

---

### Utility Scripts

**Location**: `scripts/utils/`

- **`utils/install-git-hooks.sh`** - Install Git pre-commit hooks
- **`utils/cleanup-disk-space.sh`** - Clean up disk space (remove old test results, etc.)
- **`utils/test-page-object-generator.sh`** - Generate page objects for tests

**Usage**:
```bash
# Install Git hooks
./scripts/utils/install-git-hooks.sh

# Clean up disk space
./scripts/utils/cleanup-disk-space.sh
```

---

### CI/CD Utility Scripts

**Location**: `scripts/ci/` (existing directory)

CI/CD specific scripts for GitHub Actions workflows:

- **`ci/verify-services.sh`** - Verify services are running and responding
- **`ci/wait-for-services.sh`** - Wait for Backend and Frontend to be ready
- **`ci/wait-for-grid.sh`** - Wait for Selenium Grid to be ready
- **`ci/wait-for-service.sh`** - Reusable utility for waiting for any service
- **`ci/port-utils.sh`** - Port management utilities
- **`ci/env-config.sh`** - Centralized environment configuration
- **`ci/port-config.sh`** - Centralized port configuration (legacy)

**See**: [Service Scripts Guide](../docs/guides/infrastructure/SERVICE_SCRIPTS.md) for complete documentation.

---

### Other Directories

- **`scripts/docker/`** - Docker-related scripts (grid management, etc.)
- **`scripts/lib/`** - Common library functions (shared utilities)
- **`scripts/test/`** - Test utilities and helpers
- **`scripts/temp/`** - Temporary scripts (for cleanup)

---

## Common Test Scripts

**Location**: `scripts/tests/`

- **`tests/run-tests.sh`** - Run test suite (most commonly used)
- **`tests/run-specific-test.sh`** - Run specific test method

**Usage**:
```bash
# Run test suite
./scripts/tests/run-tests.sh Scenarios chrome

# Run specific test
./scripts/tests/run-specific-test.sh Scenarios Google
```

---

## Making Scripts Executable

If you need to make scripts executable:

```bash
# Make all scripts executable
chmod +x scripts/**/*.sh

# Or specific script
chmod +x scripts/services/start-env.sh
```

---

## Using Maven Wrapper

All scripts use `./mvnw` (Maven wrapper) instead of `mvn`. This ensures everyone uses the same Maven version without needing to install Maven separately.

---

## Organization Benefits

This organization provides:

- ✅ **Better discoverability** - Scripts grouped by purpose
- ✅ **Clearer structure** - Logical grouping makes maintenance easier
- ✅ **Scalability** - Easy to add new scripts in appropriate categories
- ✅ **Reduced clutter** - Root directory is cleaner
- ✅ **Backward compatibility** - Common scripts remain in root

---

## Migration Notes

**Scripts moved from root to subdirectories** (2026-01-16):

- Service scripts → `scripts/services/`
- Framework test runners → `scripts/tests/frameworks/`
- Performance test runners → `scripts/tests/performance/`
- Build scripts → `scripts/build/`
- Quality scripts → `scripts/quality/`
- Reporting scripts → `scripts/reporting/`
- Utility scripts → `scripts/utils/`

**References updated**:
- ✅ GitHub Actions workflows
- ✅ Documentation files
- ✅ Script cross-references

---

## Script Organization History

**Reorganized**: January 16, 2026

All scripts were organized into logical subdirectories for better maintainability and discoverability. This reorganization:

- **Moved 32 scripts** to appropriate subdirectories
- **Updated 60+ references** across workflows, documentation, and internal script calls
- **Fixed 11 path calculation issues** discovered during testing
- **Fully organized** - no scripts remain in root (all moved to subdirectories)

### Key Lessons Learned

1. **Path Calculations**: When moving scripts to subdirectories, always update `SCRIPT_DIR` calculations to account for the new depth:
   - `scripts/services/` → go up 2 levels (`../..`)
   - `scripts/tests/frameworks/` → go up 3 levels (`../../..`)

2. **PROJECT_ROOT**: If `SCRIPT_DIR` already points to project root, use it directly rather than calculating `PROJECT_ROOT` separately.

3. **Testing**: Comprehensive testing revealed path calculation issues that weren't obvious from code review alone.

4. **Git History**: Using `git mv` preserved file history, making it easy to track changes.

### Migration Statistics

- **Scripts Moved**: 32
- **Workflow Files Updated**: 6
- **Documentation Files Updated**: 27+
- **Internal References Updated**: 10+
- **Issues Found & Fixed**: 11
- **Total Files Modified**: 60+

### Script Headers and Documentation

**Last Updated**: January 16, 2026

All commonly used scripts (51 scripts) now have comprehensive headers that include:
- **Purpose**: Brief description of what the script does
- **Usage**: Command-line syntax and options
- **Parameters**: Detailed parameter descriptions
- **Examples**: Common usage examples
- **Dependencies**: Required tools and dependencies
- **Output**: What the script produces
- **Notes**: Additional important information
- **Last Updated**: Date of last modification

**Scripts with Headers**:
- ✅ All service scripts (5 scripts)
- ✅ All quality scripts (3 scripts)
- ✅ All test execution scripts (5 scripts)
- ✅ All framework test runners (8 scripts)
- ✅ All performance test runners (4 scripts)
- ✅ All build & reporting scripts (3 scripts)
- ✅ All Docker scripts (4 scripts)
- ✅ All utility scripts (3 scripts)
- ✅ All library scripts (1 script)
- ✅ Key CI/CD scripts (20 scripts)
- ✅ Test utility scripts (1 script)

**Header Template**:
```bash
#!/bin/bash
# scripts/path/to/script.sh
# Script Name
#
# Purpose: Brief description
#
# Usage:
#   ./scripts/path/to/script.sh [OPTIONS]
#
# Parameters:
#   PARAM    Description
#
# Examples:
#   ./scripts/path/to/script.sh
#
# Dependencies:
#   - Dependency list
#
# Output:
#   - Output description
#   - Exit code information
#
# Notes:
#   - Additional notes
#
# Last Updated: January 2026
```

### Maintenance Notes

- When adding new scripts, place them in appropriate subdirectories
- Test `SCRIPT_DIR` calculations when moving scripts
- Update this README when adding new scripts
- **Always add comprehensive headers** to new scripts using the template above