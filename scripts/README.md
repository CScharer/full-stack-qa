# Helper Scripts

This directory contains helper scripts for common development tasks.

## Available Scripts

### Service Management Scripts

For starting, stopping, and verifying application services (Backend and Frontend):

- **`start-be.sh`** - Start backend service
- **`start-fe.sh`** - Start frontend service
- **`start-env.sh`** - Start both services together
- **`start-services-for-ci.sh`** - Start services for CI/CD (idempotent)
- **`stop-services.sh`** - Stop all services

**See**: [Service Scripts Guide](../docs/guides/infrastructure/SERVICE_SCRIPTS.md) for complete documentation.

### CI/CD Utility Scripts

Located in `scripts/ci/` directory:

- **`verify-services.sh`** - Verify services are running and responding
- **`wait-for-services.sh`** - Wait for Backend and Frontend to be ready
- **`wait-for-grid.sh`** - Wait for Selenium Grid to be ready
- **`wait-for-service.sh`** - Reusable utility for waiting for any service
- **`port-utils.sh`** - Port management utilities
- **`port-config.sh`** - Centralized port configuration

**See**: [Service Scripts Guide](../docs/guides/infrastructure/SERVICE_SCRIPTS.md) for complete documentation.

---

### `run-tests.sh`
Run test suite with optional parameters.

```bash
# Run default test suite (Scenarios) with chrome
./scripts/run-tests.sh

# Run specific suite with specific browser
./scripts/run-tests.sh Scenarios firefox

# Run Google tests
./scripts/run-tests.sh Scenarios chrome
```

### `run-specific-test.sh`
Run a specific test method.

```bash
# Run a specific test
./scripts/run-specific-test.sh Scenarios Google

# Run Microsoft test
./scripts/run-specific-test.sh Scenarios Microsoft
```

### `compile.sh`
Compile the project without running tests.

```bash
./scripts/compile.sh
```

### `run-tests-local.sh`
Run all test frameworks locally without Docker (Cypress, Playwright, Robot Framework).

```bash
# Run all local tests (no Docker required)
./scripts/run-tests-local.sh
```

This script runs:
- ✅ Cypress tests
- ✅ Playwright tests
- ✅ Robot Framework tests (API tests only)
- ⚠️ Selenium/Java tests are skipped (require Selenium Grid)

**See**: [docs/LOCAL_TESTING_GUIDE.md](../docs/LOCAL_TESTING_GUIDE.md) for complete guide.

### `run-vibium-tests.sh`
Run Vibium browser automation tests using Vitest.

```bash
# Run tests normally
./scripts/run-vibium-tests.sh

# Run in watch mode
./scripts/run-vibium-tests.sh --watch

# Run with UI
./scripts/run-vibium-tests.sh --ui

# Run with coverage
./scripts/run-vibium-tests.sh --coverage
```

This script:
- ✅ Automatically installs dependencies if needed
- ✅ Runs Vitest test suite
- ✅ Supports watch mode and UI mode
- ✅ Generates coverage reports

## Making Scripts Executable

If you need to make scripts executable:

```bash
chmod +x scripts/*.sh
```

## Using Maven Wrapper

All scripts use `./mvnw` (Maven wrapper) instead of `mvn`. This ensures everyone uses the same Maven version without needing to install Maven separately.
