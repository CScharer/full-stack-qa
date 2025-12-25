# Helper Scripts

This directory contains helper scripts for common development tasks.

## Available Scripts

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
