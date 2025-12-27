# Configuration Directory

This directory contains centralized configuration files used across the project. All environment-specific configuration (ports, database, API endpoints, timeouts, CORS) is managed here.

## Available Configuration Files

### `environments.json` (Comprehensive Configuration)

**Purpose**: Single source of truth for all environment configuration including:
- Ports and URLs (frontend/backend)
- Database paths and naming patterns
- API endpoints (`/api/v1`, `/health`, etc.)
- CORS origins (per environment)
- Service timeouts
- All environment-specific settings

This is the **primary configuration file** for all environment settings. Both shell scripts and TypeScript/JavaScript code read from this file.

### `ports.json` (Ports Only)

**Purpose**: Port assignments and URLs for all environments.

**Use Case**: Use this file if you only need port configuration. For comprehensive configuration, use `environments.json`.

**Note**: Scripts automatically read from `environments.json` first, then fall back to `ports.json` if needed.

## Usage

#### Shell Scripts - Comprehensive Configuration

**Using `env-config.sh` utility (recommended):**
```bash
source scripts/ci/env-config.sh
export_environment_config "dev"
# Now all config vars are set: FRONTEND_PORT, API_PORT, DATABASE_NAME, API_BASE_PATH, etc.
```

**Using `port-config.sh` (ports only):**
```bash
source scripts/ci/port-config.sh
eval "$(get_ports_for_environment "dev")"
# Ports are set: FRONTEND_PORT, API_PORT, FRONTEND_URL, API_URL
```

**Reading directly with jq:**
```bash
FRONTEND_PORT=$(jq -r '.environments.dev.frontend.port' config/environments.json)
API_PORT=$(jq -r '.environments.dev.backend.port' config/environments.json)
API_BASE_PATH=$(jq -r '.api.basePath' config/environments.json)
DB_NAME=$(jq -r '.environments.dev.database.name' config/environments.json)
```

#### TypeScript/JavaScript - Comprehensive Configuration

**Using `port-config.ts` utility (recommended - type-safe):**
```typescript
import { getEnvironmentConfig, getApiConfig, getTimeoutConfig } from './config/port-config';
const env = getEnvironmentConfig('dev');
const api = getApiConfig();
console.log(env.frontend.port); // 3003
console.log(env.backend.port); // 8003
console.log(api.basePath); // "/api/v1"
console.log(env.database.name); // "full_stack_qa_dev.db"
```

**Importing directly:**
```typescript
import config from '../../config/environments.json';
const env = config.environments.dev;
console.log(env.frontend.port); // 3003
console.log(config.api.basePath); // "/api/v1"
```

### Configuration Values

#### Port Assignments

| Environment | Frontend Port | Backend Port |
|-------------|---------------|--------------|
| dev | 3003 | 8003 |
| test | 3004 | 8004 |
| prod | 3005 | 8005 |

#### API Endpoints (from environments.json)
- Base Path: `/api/v1`
- Health Check: `/health`
- API Docs: `/docs`
- ReDoc: `/redoc`

#### Database Configuration (from environments.json)
- Directory: `Data/Core`
- Schema Database: `full_stack_qa.db`
- Naming Pattern: `full_stack_qa_{env}.db`
- Examples:
  - Dev: `full_stack_qa_dev.db`
  - Test: `full_stack_qa_test.db`
  - Prod: `full_stack_qa_prod.db`

#### Timeouts (from environments.json)
- Service Startup: 120 seconds
- Service Verification: 30 seconds
- API Client: 10000ms (10 seconds)
- Web Server: 120000ms (120 seconds)
- Check Interval: 2 seconds

### Updating Configuration

**To update configuration values:**

1. **Edit `config/environments.json`** (single source of truth for all config)
2. **Scripts automatically use the new values**:
   - Shell scripts: Via `scripts/ci/env-config.sh` or `scripts/ci/port-config.sh`
   - TypeScript/JavaScript: Via `playwright/config/port-config.ts`
3. **All configuration values** (ports, database, API paths, timeouts, CORS) are in one place

**Alternative: Update `ports.json` (ports only)**
- If you only need to change ports, you can update `config/ports.json`
- Scripts will automatically use the new values
- Note: For other config values (database, API paths, timeouts), use `environments.json`

**Requirements**: 
- Shell scripts require `jq` for JSON parsing (install with `brew install jq` on macOS or `apt-get install jq` on Linux)
- If `jq` is not installed, scripts will fall back to hardcoded values

