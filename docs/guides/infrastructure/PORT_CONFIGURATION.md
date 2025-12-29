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
- API endpoints (`/api/v1`, `/health`, etc.)
- CORS origins
- Timeout values

The `config/ports.json` file is maintained for backward compatibility (ports only).

**Shell Scripts**: 
- Use `scripts/ci/env-config.sh` for comprehensive config (recommended)
- Use `scripts/ci/port-config.sh` for ports only (backward compatibility)

**TypeScript/JavaScript**: Use `playwright/config/port-config.ts` which imports from `config/environments.json`

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

Configuration values are determined in the following order (highest to lowest priority):

1. **Environment Variables** (`API_PORT`, `FRONTEND_PORT`, `DATABASE_PATH`, etc.) - Highest priority
2. **`.env` files** (backend/.env, frontend/.env) - Can override defaults
3. **Centralized Config** (`config/environments.json` or `config/ports.json`) - Single source of truth
   - Shell scripts: `scripts/ci/env-config.sh` (comprehensive) or `scripts/ci/port-config.sh` (ports only)
   - TypeScript/JavaScript: `playwright/config/port-config.ts` (imports from `config/environments.json`)
4. **Hardcoded fallback** - Only used if config files are missing

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
   - TypeScript/JavaScript: Import from `playwright/config/port-config.ts` (which imports from `config/environments.json`)
   - Never hardcode configuration values (ports, timeouts, API paths, etc.)
2. **Validate environment**: Ensure `ENVIRONMENT` is set correctly before starting services (defaults to `dev`)
3. **Check port availability**: Scripts should verify ports are available before binding
4. **Document changes**: If configuration needs to change, update `config/environments.json` (single source of truth) and `ONE_GOAL.md`
5. **Use comprehensive config**: Prefer `config/environments.json` over `config/ports.json` for new code

## Files Using Configuration

**Shell Scripts** (via `scripts/ci/env-config.sh` or `scripts/ci/port-config.sh`):
- `scripts/start-services-for-ci.sh` - Starts services, uses timeout from config
- `scripts/ci/determine-ports.sh` - Sets GitHub Actions outputs
- `scripts/ci/verify-services.sh` - Verifies services, uses timeout from config
- `scripts/start-be.sh`, `scripts/start-fe.sh`, `scripts/start-env.sh` - Service startup scripts

**TypeScript/JavaScript** (via `playwright/config/port-config.ts`):
- `playwright/playwright.integration.config.ts` - Integration test configuration (ports, timeouts, API paths, CORS)
- `frontend/lib/api/client.ts` - API client timeout (should match config)
- Other Playwright/TypeScript configs can import from `port-config.ts`

**Workflows**:
- `.github/workflows/env-fe.yml` - Verifies services on correct ports
- `.github/workflows/ci.yml` - Uses port configuration for environment setup

**Configuration Values Used**:
- Ports: Frontend/backend ports per environment
- Database: Database paths and naming patterns
- API: Base path (`/api/v1`), health endpoint (`/health`), docs endpoints
- Timeouts: Service startup (120s), verification (30s), API client (10s), web server (120s)
- CORS: Allowed origins per environment

## Troubleshooting

### Services starting on wrong ports

1. Check `ENVIRONMENT` variable is set correctly
2. Verify `scripts/ci/port-config.sh` exists and is executable
3. Check for `.env` file overrides
4. Review service startup logs for port assignments

### Port conflicts

If a port is already in use:
- Check what process is using it: `lsof -i :PORT`
- Stop conflicting services: `./scripts/stop-services.sh`
- Use `FORCE_STOP=true` to force stop existing services

## Future Improvements

- [ ] Add port validation script to check all scripts use centralized config
- [ ] Add CI check to verify port consistency across files
- [ ] Consider environment-specific port ranges for better isolation
