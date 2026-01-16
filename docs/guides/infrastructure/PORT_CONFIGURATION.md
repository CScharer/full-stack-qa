# Port Configuration Guide

## Overview

This document describes the centralized port configuration system used in this repository. Port assignments are managed through a single source of truth to prevent configuration mismatches.

## Port Assignments

Port assignments are documented in `docs/new_app/ONE_GOAL.md` and enforced via `config/ports.json` (single source of truth).

### Environment Port Mapping

| Environment | Frontend Port | Backend Port | Frontend URL | Backend URL |
|------------|---------------|--------------|-------------|-------------|
| **DEV**    | 3003          | 8003         | http://localhost:3003 | http://localhost:8003 |
| **TEST**   | 3004          | 8004         | http://localhost:3004 | http://localhost:8004 |
| **PROD**   | 3005          | 8005         | http://localhost:3005 | http://localhost:8005 |

## Centralized Configuration

**Single Source of Truth**: `config/environments.json` (recommended) or `config/ports.json` (legacy)

The `config/environments.json` file contains comprehensive configuration for all environments including:
- Ports and URLs (frontend/backend)
- Database paths and naming patterns
- API endpoints (base path `/api/v1` is configurable via `api.basePath`)
- CORS origins
- Timeout values

**API Version Configuration**: The API base path (e.g., `/api/v1`) is centralized in `config/environments.json` under `api.basePath`. All code (backend, frontend, tests, scripts) reads from this single source of truth. To change the API version, update `api.basePath` in the config file. See `config/README.md` for details.

The `config/ports.json` file is maintained for backward compatibility (ports only).

**Shell Scripts**: 
- Use `scripts/ci/env-config.sh` for comprehensive config (recommended)
- Use `scripts/ci/port-config.sh` for ports only (backward compatibility)

**TypeScript/JavaScript**: Use `config/port-config.ts` which reads from `config/environments.json`

**See Also**: 
- [Service Scripts Guide](./SERVICE_SCRIPTS.md) for information on how service management scripts use port configuration
- Configuration files are documented in `config/environments.json` and `config/ports.json` (see inline comments)

### Usage

```bash
# Source the port configuration
source scripts/ci/port-config.sh

# Get ports for a specific environment
ENVIRONMENT=test
eval "$(get_ports_for_environment "$ENVIRONMENT")"

# Ports are now available as environment variables
echo "Frontend: $FRONTEND_PORT"
echo "Backend: $API_PORT"
```

## Configuration Priority

Configuration values are resolved using a consistent priority order across all frameworks. This ensures predictable behavior and allows for flexible overrides when needed.

### General Priority Order (Highest to Lowest)

1. **Command-Line Arguments** (`--env`, `-e`, `--port`, etc.) - Highest priority
2. **Environment Variables** (`ENVIRONMENT`, `API_PORT`, `FRONTEND_PORT`, `DATABASE_PATH`, etc.)
3. **`.env` Files** (backend/.env, frontend/.env.local) - Framework-specific
4. **Centralized Config Files** (`config/environments.json` or `config/ports.json`) - Single source of truth
5. **Default Values** - Framework-specific defaults (only used if all above sources fail)

### Framework-Specific Implementations

#### Shell Scripts (`scripts/start-fe.sh`, `scripts/start-be.sh`, etc.)

**Priority Order:**
1. Command-line arguments (`--env ENV` or `-e ENV`)
2. Environment variables (`ENVIRONMENT`, `PORT`, `API_PORT`, etc.)
3. Centralized config (`config/environments.json` via `scripts/ci/env-config.sh`)

**Example:**
```bash
# Command-line argument takes precedence
./scripts/services/start-fe.sh --env test

# Environment variable takes precedence over config
ENVIRONMENT=test ./scripts/services/start-fe.sh

# Falls back to config/environments.json (defaults to 'dev')
./scripts/services/start-fe.sh
```

**Implementation Details:**
- Scripts use `scripts/lib/common.sh` for environment parsing
- `parse_environment_param()` extracts `--env` or `-e` arguments
- `set_and_validate_environment()` resolves environment with priority: CLI arg → `ENVIRONMENT` env var → default ('dev')
- Config is loaded via `scripts/ci/env-config.sh` which reads from `config/environments.json`

#### Backend Python (`backend/app/config.py`)

**Priority Order:**
1. Environment variables (`DATABASE_PATH`, `DATABASE_NAME`, `ENVIRONMENT`, `API_PORT`, `CORS_ORIGINS`)
2. `.env` file (via Pydantic `Settings` with `env_file=".env"`)
3. Centralized config (`config/port_config.py` → `config/environments.json`)
4. Default values in `Settings` class

**Example:**
```python
# Environment variable takes precedence
DATABASE_PATH=/custom/path.db python -m backend.app.main

# .env file overrides config
# backend/.env: DATABASE_NAME=custom.db
python -m backend.app.main

# Falls back to config/environments.json
python -m backend.app.main
```

**Implementation Details:**
- Uses Pydantic `BaseSettings` with `env_file=".env"` for automatic `.env` loading
- Database path resolution (in `get_database_path()`):
  1. `DATABASE_PATH` env var (full path)
  2. `DATABASE_NAME` env var + `DATABASE_DIR` env var
  3. `ENVIRONMENT` env var → `full_stack_qa_{env}.db`
  4. Default → `full_stack_qa_dev.db`
- API port and CORS origins are loaded from shared config if env vars are not set

#### TypeScript/JavaScript (`config/port-config.ts`)

**Priority Order:**
1. Environment variables (`process.env.ENVIRONMENT`, `process.env.API_PORT`, etc.)
2. Centralized config (`config/environments.json`)
3. Legacy fallback (`config/ports.json`) - for backward compatibility
4. Hardcoded defaults (only in fallback scenarios)

**Example:**
```typescript
// Environment variable takes precedence
process.env.ENVIRONMENT = 'test';
const config = getEnvironmentConfig('dev'); // Uses 'test' from env var

// Falls back to config/environments.json
const config = getEnvironmentConfig('dev'); // Uses 'dev' from config

// Legacy fallback to config/ports.json (if environments.json missing)
// Then hardcoded defaults (if ports.json also missing)
```

**Implementation Details:**
- Functions like `getEnvironmentConfig()` accept `environment` parameter
- If `environment` is not provided, reads from `process.env.ENVIRONMENT`
- Falls back to `config/ports.json` if `config/environments.json` is missing (backward compatibility)
- Used by: Cypress, Playwright, Vibium, Frontend

#### Python Tests (`config/port_config.py`)

**Priority Order:**
1. Environment variables (`os.getenv('ENVIRONMENT')`)
2. Function parameters (e.g., `get_backend_url('test')`)
3. Centralized config (`config/environments.json`)
4. Legacy fallback (`config/ports.json`) - for backward compatibility
5. Hardcoded defaults (only in fallback scenarios)

**Example:**
```python
# Function parameter takes precedence
backend_url = get_backend_url('test')  # Uses 'test' even if ENVIRONMENT=dev

# Environment variable used if parameter not provided
os.environ['ENVIRONMENT'] = 'test'
backend_url = get_backend_url()  # Uses 'test' from env var

# Falls back to config/environments.json
backend_url = get_backend_url('dev')  # Uses 'dev' from config
```

**Implementation Details:**
- Functions accept `environment` parameter with optional `default_env`
- If `environment` is `None` or empty, reads from `os.getenv('ENVIRONMENT', default_env)`
- Falls back to `config/ports.json` if `config/environments.json` is missing
- Used by: Robot Framework (via `ConfigHelper.py`), Locust tests, Backend tests

#### Java Tests (`src/test/java/com/cjs/qa/config/EnvironmentConfig.java`)

**Priority Order:**
1. Environment variables (`System.getenv("ENVIRONMENT")`)
2. Centralized config (`config/environments.json` via JSON parsing)
3. Default values (hardcoded in Java code)

**Example:**
```java
// Environment variable takes precedence
System.setProperty("ENVIRONMENT", "test");
String url = EnvironmentConfig.getFrontendUrl(); // Uses 'test' from env var

// Falls back to config/environments.json
String url = EnvironmentConfig.getFrontendUrl(); // Uses 'dev' from config
```

**Implementation Details:**
- Reads `ENVIRONMENT` from `System.getenv("ENVIRONMENT")` or defaults to 'dev'
- Parses `config/environments.json` directly using Java JSON libraries
- Used by: Selenium/Java tests (optional, newer tests)

### Environment Detection

Different frameworks detect the environment using different methods, but all follow the same priority:

1. **Explicit parameter** (if function accepts environment parameter)
2. **Environment variable** (`ENVIRONMENT`, `process.env.ENVIRONMENT`, `os.getenv('ENVIRONMENT')`, `System.getenv("ENVIRONMENT")`)
3. **Default value** (typically 'dev')

**Framework-Specific Environment Detection:**

> **Note**: The frameworks below are listed alphabetically. The order does not indicate priority or importance. Each framework's environment detection method is independent.

| Framework | Method | Default |
|-----------|--------|---------|
| Artillery | Separate config files (`dev.yml`, `test.yml`, `prod.yml`) | N/A |
| Backend Python | `ENVIRONMENT` env var → 'dev' | 'dev' |
| Cypress | `Cypress.env('ENVIRONMENT')` → `process.env.ENVIRONMENT` → 'dev' | 'dev' |
| Java Tests (EnvironmentConfig) | `System.getProperty("ENVIRONMENT")` → `System.getenv("ENVIRONMENT")` → 'dev' | 'dev' |
| Java Tests (Legacy Environment) | `System.getProperty("test.environment")` → XML config → 'TST' | 'TST' |
| JMeter | Command-line properties (`-JbaseUrl=...`) → hardcoded default | `http://localhost:8003` |
| Playwright | `process.env.ENVIRONMENT` → 'dev' | 'dev' |
| Python Tests (Locust) | Function param → `os.getenv('ENVIRONMENT')` → 'dev' | 'dev' |
| Robot Framework | `Get Environment Variable ENVIRONMENT default=dev` | 'dev' |
| Shell Scripts | `--env` arg → `$ENVIRONMENT` → 'dev' | 'dev' |
| TypeScript/JS (Shared) | `process.env.ENVIRONMENT` → 'dev' | 'dev' |
| Vibium | `process.env.ENVIRONMENT` → 'dev' (via shared config) | 'dev' |

### Configuration Override Examples

**Example 1: Override Port via Environment Variable**
```bash
# Shell script
API_PORT=9000 ./scripts/services/start-be.sh --env test
# Result: Backend starts on port 9000 (env var overrides config)

# Python
API_PORT=9000 python -m backend.app.main
# Result: Backend starts on port 9000 (env var overrides config)
```

**Example 2: Override Environment via Command-Line**
```bash
# Shell script
./scripts/services/start-fe.sh --env prod
# Result: Uses 'prod' environment config (CLI arg overrides ENVIRONMENT env var)

# Even if ENVIRONMENT=test is set
ENVIRONMENT=test ./scripts/services/start-fe.sh --env prod
# Result: Uses 'prod' (CLI arg has highest priority)
```

**Example 3: Override Database Path**
```bash
# Python Backend
DATABASE_PATH=/custom/path/custom.db python -m backend.app.main
# Result: Uses /custom/path/custom.db (env var overrides all config)
```

**Example 4: TypeScript Environment Override**
```typescript
// In test file
process.env.ENVIRONMENT = 'test';
const backendUrl = getBackendUrl('dev'); // Parameter 'dev' is ignored, uses 'test' from env
// Result: Uses 'test' environment config
```

### Best Practices

1. **Use centralized config as default**: Always read from `config/environments.json` when no overrides are needed
2. **Respect priority order**: When implementing new config loaders, follow the documented priority order
3. **Document overrides**: If your code allows overrides, document the priority order clearly
4. **Validate environment values**: Always validate that environment is one of: `dev`, `test`, `prod`
5. **Avoid hardcoded fallbacks**: Use centralized config instead of hardcoded values (see Item #1 in repository improvements)
6. **Use environment variables for CI/CD**: In GitHub Actions, use environment variables or workflow inputs to override config
7. **Test override behavior**: Ensure that overrides work as expected in all frameworks

### Troubleshooting Configuration Priority

**Issue: Configuration not being overridden**

1. Check environment variable is set: `echo $ENVIRONMENT`
2. Verify command-line argument syntax: `--env test` or `-e test`
3. Check for `.env` file that might be overriding: `cat backend/.env`
4. Verify config file exists: `ls config/environments.json`
5. Review framework-specific implementation to understand priority order

**Issue: Wrong environment being used**

1. Check all environment variable sources: `env | grep ENVIRONMENT`
2. Verify command-line arguments are parsed correctly
3. Check for conflicting `.env` files
4. Review framework logs for environment detection messages

## Why Centralized Configuration?

### Problem
Previously, ports were hardcoded in multiple places:
- `scripts/start-services-for-ci.sh`
- `scripts/ci/determine-ports.sh`
- `.github/workflows/*.yml`
- Various test configuration files

This led to:
- **Configuration drift**: Ports could differ between scripts
- **Maintenance burden**: Changes required updates in multiple files
- **Runtime failures**: Services starting on wrong ports (e.g., test env using dev ports)

### Solution
Centralized configuration ensures:
- ✅ **Single source of truth**: One file defines all ports
- ✅ **Automatic synchronization**: All scripts use the same values
- ✅ **Easy maintenance**: Update ports in one place
- ✅ **Validation**: Scripts can verify ports match expectations

## Are Ports Private/Sensitive?

**No, these ports are not sensitive or private.**

- They are **localhost ports** for local development/testing
- They are **documented publicly** in `ONE_GOAL.md`
- They are **hardcoded in workflows** (visible in GitHub)
- They are **standard development ports** (3000-8000 range)

**Security Note**: These ports are only accessible on `localhost` (127.0.0.1) and are not exposed to the internet. They are safe to commit to version control.

## Best Practices

1. **Always use the centralized config**: 
   - Shell scripts: Source `scripts/ci/env-config.sh` (comprehensive) or `scripts/ci/port-config.sh` (ports only)
   - TypeScript/JavaScript: Import from `config/port-config.ts` (which reads from `config/environments.json`)
   - Python: Import from `config/port_config.py` (which reads from `config/environments.json`)
   - Never hardcode configuration values (ports, timeouts, API paths, API versions, etc.)
   - All API paths should use `getApiBasePath()` or read from config, never hardcode `/api/v1`
2. **Validate environment**: Ensure `ENVIRONMENT` is set correctly before starting services (defaults to `dev`)
3. **Check port availability**: Scripts should verify ports are available before binding
4. **Document changes**: If configuration needs to change, update `config/environments.json` (single source of truth) and `ONE_GOAL.md`
5. **Use comprehensive config**: Prefer `config/environments.json` over `config/ports.json` for new code

## Files Using Configuration

**Shell Scripts** (via `scripts/ci/env-config.sh` or `scripts/ci/port-config.sh`):
- `scripts/start-services-for-ci.sh` - Starts services, uses timeout and API base path from config
- `scripts/ci/determine-ports.sh` - Sets GitHub Actions outputs
- `scripts/ci/verify-services.sh` - Verifies services, uses timeout from config
- `scripts/start-be.sh`, `scripts/start-fe.sh`, `scripts/start-env.sh` - Service startup scripts (use API base path from config)

**TypeScript/JavaScript** (via `config/port-config.ts`):
- `playwright/playwright.integration.config.ts` - Integration test configuration (ports, timeouts, API paths, CORS)
- `frontend/lib/api/client.ts` - API client (timeout and base path from config)
- `cypress/cypress/e2e/*.cy.ts` - Cypress tests (use shared API utilities)
- `lib/api-utils.ts` - Shared API request utility (reads API version from config)
- Other Playwright/TypeScript configs can import from `config/port-config.ts`

**Workflows**:
- `.github/workflows/env-fe.yml` - Verifies services on correct ports
- `.github/workflows/ci.yml` - Uses port configuration for environment setup

**Configuration Values Used**:
- Ports: Frontend/backend ports per environment
- Database: Database paths and naming patterns
- API: Base path (configurable via `api.basePath`, default `/api/v1`), health endpoint (`/health`), docs endpoints
- Timeouts: Service startup (120s), verification (30s), API client (10s), web server (120s)
- CORS: Allowed origins per environment

**API Version Centralization**: The API base path is fully centralized. All code (backend routes, frontend client, test frameworks, shell scripts, performance tests) reads from `config/environments.json`. This means changing the API version requires updating only one file. See `config/README.md` for details on changing the API version.

## Troubleshooting

### Services starting on wrong ports

1. Check `ENVIRONMENT` variable is set correctly
2. Verify `scripts/ci/port-config.sh` exists and is executable
3. Check for `.env` file overrides
4. Review service startup logs for port assignments

### Port conflicts

If a port is already in use:
- Check what process is using it: `lsof -i :PORT`
- Stop conflicting services: `./scripts/services/stop-services.sh`
- Use `FORCE_STOP=true` to force stop existing services

## Future Improvements

- [ ] Add port validation script to check all scripts use centralized config
- [ ] Add CI check to verify port consistency across files
- [ ] Consider environment-specific port ranges for better isolation
