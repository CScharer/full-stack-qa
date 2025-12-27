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

**Single Source of Truth**: `config/ports.json`

This JSON file contains the authoritative port assignments for all environments. Both shell scripts and TypeScript/JavaScript code read from this file.

**Shell Scripts**: Use `scripts/ci/port-config.sh` which reads from `config/ports.json`  
**TypeScript/JavaScript**: Use `playwright/config/port-config.ts` which imports from `config/ports.json`

**See Also**: 
- [Service Scripts Guide](./SERVICE_SCRIPTS.md) for information on how service management scripts use port configuration
- [config/README.md](../../config/README.md) for configuration file documentation

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

Port values are determined in the following order (highest to lowest priority):

1. **Environment Variables** (`API_PORT`, `FRONTEND_PORT`) - Highest priority
2. **`.env` files** (backend/.env, frontend/.env) - Can override defaults
3. **Centralized Config** (`config/ports.json`) - Single source of truth
   - Shell scripts: `scripts/ci/port-config.sh` (reads from `config/ports.json`)
   - TypeScript/JavaScript: `playwright/config/port-config.ts` (imports from `config/ports.json`)
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
   - Shell scripts: Source `scripts/ci/port-config.sh` (which reads from `config/ports.json`)
   - TypeScript/JavaScript: Import from `playwright/config/port-config.ts` (which imports from `config/ports.json`)
   - Never hardcode ports
2. **Validate environment**: Ensure `ENVIRONMENT` is set correctly before starting services (defaults to `dev`)
3. **Check port availability**: Scripts should verify ports are available before binding
4. **Document changes**: If ports need to change, update `config/ports.json` (single source of truth) and `ONE_GOAL.md`

## Files Using Port Configuration

**Shell Scripts** (via `scripts/ci/port-config.sh`):
- `scripts/start-services-for-ci.sh` - Starts services on correct ports
- `scripts/ci/determine-ports.sh` - Sets GitHub Actions outputs
- `scripts/ci/verify-services.sh` - Verifies services on correct ports
- `scripts/start-be.sh`, `scripts/start-fe.sh`, `scripts/start-env.sh` - Service startup scripts

**TypeScript/JavaScript** (via `playwright/config/port-config.ts`):
- `playwright/playwright.integration.config.ts` - Integration test configuration
- Other Playwright/TypeScript configs can import from `port-config.ts`

**Workflows**:
- `.github/workflows/env-fe.yml` - Verifies services on correct ports
- `.github/workflows/ci.yml` - Uses port configuration for environment setup

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
