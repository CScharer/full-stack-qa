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

**Java Tests**: This file is automatically copied to `src/test/resources/config/environments.json` during Maven build (see `pom.xml` maven-resources-plugin configuration). Java tests load it from the classpath. **Do not edit the copy in `src/test/resources/config/`** - always edit `config/environments.json` (the source of truth).

### `ports.json` (Ports Only - Legacy) ❌ **REMOVED**

**Purpose**: ~~Port assignments and URLs for all environments.~~ **This file has been removed.**

**Status**: ❌ **REMOVED** - File deleted. All code now uses `environments.json` as primary source with hardcoded fallback values.

**Removal Date**: January 17, 2026

**Migration Complete**:
- ✅ **Phase 1 (Complete)**: All code migrated to use `environments.json` as primary source
- ✅ **Phase 2 (Complete)**: Fallback logic updated to use hardcoded values instead of `ports.json`
- ✅ **Phase 3 (Complete)**: `ports.json` file removed

**Fallback Behavior**:
- **Primary**: `environments.json` (comprehensive configuration)
- **Fallback**: Hardcoded values (matching previous `ports.json` values)
  - `dev`: frontend=3003, backend=8003
  - `test`: frontend=3004, backend=8004
  - `prod`: frontend=3005, backend=8005

**Note**: 
- All port information previously in `ports.json` is available in `environments.json` under each environment's `frontend` and `backend` sections
- **All code uses `environments.json`** - `ports.json` has been removed
- If `environments.json` is unavailable, hardcoded fallback values are used (matching previous `ports.json` values for backward compatibility)

## Usage

#### Shell Scripts - Comprehensive Configuration

**Using `env-config.sh` utility (recommended):**
```bash
source scripts/ci/env-config.sh
export_environment_config "dev"
# Now all config vars are set: FRONTEND_PORT, API_PORT, DATABASE_NAME, API_BASE_PATH, etc.
```

**Using `port-config.sh` (ports only - legacy):**
```bash
source scripts/ci/port-config.sh
eval "$(get_ports_for_environment "dev")"
# Ports are set: FRONTEND_PORT, API_PORT, FRONTEND_URL, API_URL
# Note: Falls back to hardcoded values if environments.json unavailable
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
import { getEnvironmentConfig, getApiConfig, getApiBasePath, getTimeoutConfig, getBackendUrl, getFrontendUrl } from '../config/port-config';
const env = getEnvironmentConfig('dev');
const api = getApiConfig();
const apiBasePath = getApiBasePath(); // "/api/v1" (from config)
console.log(env.frontend.port); // 3003
console.log(env.backend.port); // 8003
console.log(api.basePath); // "/api/v1"
console.log(apiBasePath); // "/api/v1"
console.log(env.database.name); // "full_stack_qa_dev.db"

// Helper functions for common use cases
const backendUrl = getBackendUrl('dev'); // "http://localhost:8003"
const frontendUrl = getFrontendUrl('test'); // "http://localhost:3004"
```

**Importing directly:**
```typescript
import config from '../../config/environments.json';
const env = config.environments.dev;
console.log(env.frontend.port); // 3003
console.log(config.api.basePath); // "/api/v1"
```

#### Python - Comprehensive Configuration

**Using `port_config.py` utility (recommended):**
```python
from config.port_config import get_environment_config, get_backend_url, get_frontend_url, get_api_config, get_api_base_path

env_config = get_environment_config('dev')
backend_url = get_backend_url('dev')  # "http://localhost:8003"
frontend_url = get_frontend_url('test')  # "http://localhost:3004"
api_config = get_api_config()
api_base_path = get_api_base_path()  # "/api/v1" (from config)
```

**Robot Framework Usage:**
```robotframework
Library    ${CURDIR}${/}ConfigHelper.py
${base_url}=    Get Base Url From Shared Config
```

#### Java - Comprehensive Configuration

**Using `EnvironmentConfig.java` utility (optional, for newer tests):**
```java
import com.cjs.qa.config.EnvironmentConfig;

String baseUrl = EnvironmentConfig.getFrontendUrl(); // Uses config/environments.json
String backendUrl = EnvironmentConfig.getBackendUrl(); // Uses config/environments.json
int backendPort = EnvironmentConfig.getBackendPort("dev");
```

**Note**: Java tests can use either:
- XML-based config (`config/Environments.xml`) - For user-specific settings (browser, timeouts, logging)
- JSON-based config (`EnvironmentConfig.java`) - For environment-specific URLs/ports (dev, test, prod)

### XML Configuration Files (Legacy for Java User-Specific Settings)

**Purpose**: Contains XML configuration files for user-specific settings (e.g., browser preferences, timeouts, logging flags) for Java tests.

**Important Note**: These files (`Environments.xml`, `Companies.xml`, `UserSettings.xml`) are **NOT committed to git** as they may contain sensitive data or user-specific settings. Templates (`.template` files) are provided for easy setup.

**Setup Instructions**:

1. **Copy template file**:
   ```bash
   cp config/Environments.xml.template config/Environments.xml
   ```

2. **Configure Google Cloud authentication**:
   ```bash
   gcloud auth application-default login
   gcloud config set project cscharer
   ```

3. **Run your tests** - Passwords are automatically fetched from Google Cloud Secret Manager!

**Security Notice**: All sensitive credentials are stored in **Google Cloud Secret Manager**, not in these configuration files. The application automatically retrieves passwords at runtime using the `SecureConfig.java` utility class.

**See Also**: 
- For XML company/user settings: See `xml/README.md`
- For environment configuration (ports, URLs, database): See `environments.json` above

### Configuration Values

#### Port Assignments

| Environment | Frontend Port | Backend Port |
|-------------|---------------|--------------|
| dev | 3003 | 8003 |
| test | 3004 | 8004 |
| prod | 3005 | 8005 |

#### API Endpoints (from environments.json)
- Base Path: `/api/v1` (configurable in `api.basePath`)
- Health Check: `/health`
- API Docs: `/docs`
- ReDoc: `/redoc`

**Note**: The API base path is centralized in `config/environments.json` under `api.basePath`. All code (backend, frontend, tests, scripts) reads from this single source of truth. To change the API version, update `api.basePath` in `config/environments.json` (e.g., change `/api/v1` to `/api/v2`).

#### Database Configuration (from environments.json)
- Directory: `data/core`
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
2. **All frameworks automatically use the new values**:
   - Shell scripts: Via `scripts/ci/env-config.sh` or `scripts/ci/port-config.sh`
   - TypeScript/JavaScript: Via `config/port-config.ts` (used by Cypress, Playwright, Vibium, Frontend)
   - Python: Via `config/port_config.py` (used by Robot Framework, Backend)
   - Java: Via `src/test/java/com/cjs/qa/config/EnvironmentConfig.java` (optional, for newer tests)
3. **All configuration values** (ports, database, API paths, timeouts, CORS) are in one place

### Changing API Version

**To change the API version (e.g., from v1 to v2):**

1. **Edit `config/environments.json`**:
   ```json
   {
     "api": {
       "basePath": "/api/v2",  // Change from "/api/v1" to "/api/v2"
       "healthEndpoint": "/health",
       "docsEndpoint": "/docs",
       "redocEndpoint": "/redoc"
     }
   }
   ```

2. **All code automatically uses the new version**:
   - ✅ Backend (`backend/app/main.py`) - Router prefixes updated automatically
   - ✅ Frontend (`frontend/lib/api/client.ts`) - API base URL updated automatically
   - ✅ All test files (Cypress, Playwright, Backend tests) - Use shared utilities
   - ✅ Shell scripts (`start-fe.sh`, `start-be.sh`, etc.) - Read from config
   - ✅ Performance tests (Locust, JMeter) - Use config values

3. **No code changes needed** - The API version is centralized and all code reads from `config/environments.json`

**Example**: If you change `api.basePath` from `/api/v1` to `/api/v2`, all API endpoints will automatically use `/api/v2` instead of `/api/v1`.

**⚠️ Deprecated: Update `ports.json` (ports only)**
- ~~If you only need to change ports, you can update `config/ports.json`~~ **DEPRECATED**
- **Do not update `ports.json`** - This file is deprecated and will be removed
- **Always update `config/environments.json`** instead - it contains all port information and more
- Scripts automatically read from `environments.json` first, then fall back to `ports.json` only if `environments.json` is unavailable

**Requirements**: 
- Shell scripts require `jq` for JSON parsing (install with `brew install jq` on macOS or `apt-get install jq` on Linux)
- If `jq` is not installed, scripts will fall back to hardcoded values

---

## Framework-Specific Configuration

All test frameworks use `config/environments.json` as the single source of truth. Each framework has a utility that reads from this shared configuration:

### TypeScript Frameworks (Cypress, Playwright, Vibium, Frontend)

**Shared Utility**: `config/port-config.ts`

All TypeScript frameworks import from the shared `config/port-config.ts` utility:

```typescript
// Cypress, Playwright, Vibium
import { getBackendUrl, getFrontendUrl, getEnvironmentConfig } from '../config/port-config';
// or
import { getBackendUrl, getFrontendUrl, getEnvironmentConfig } from '../../config/port-config';

const environment = process.env.ENVIRONMENT || 'dev';
const backendUrl = getBackendUrl(environment);
const frontendUrl = getFrontendUrl(environment);
```

**TypeScript Base Configuration**: All TypeScript frameworks extend `tsconfig.base.json` for common compiler options, reducing duplication.

### Python Frameworks (Robot Framework, Backend)

**Shared Utility**: `config/port_config.py`

Python frameworks use the shared `config/port_config.py` utility:

```python
from config.port_config import get_backend_url, get_frontend_url, get_environment_config

environment = os.getenv('ENVIRONMENT', 'dev')
backend_url = get_backend_url(environment)
frontend_url = get_frontend_url(environment)
```

**Robot Framework Helper**: `src/test/robot/resources/ConfigHelper.py` wraps the shared Python config for Robot Framework usage.

### Java Frameworks (Selenium/Java)

**Optional Utility**: `src/test/java/com/cjs/qa/config/EnvironmentConfig.java`

Newer Java tests can use the optional `EnvironmentConfig.java` utility:

```java
import com.cjs.qa.config.EnvironmentConfig;

String environment = EnvironmentConfig.getEnvironment(); // Reads from ENVIRONMENT env var
String baseUrl = EnvironmentConfig.getFrontendUrl(environment);
String backendUrl = EnvironmentConfig.getBackendUrl(environment);
```

**Note**: Java tests can use either:
- **XML Config** (`config/Environments.xml`) - For user-specific settings (browser preferences, timeouts, logging flags)
- **JSON Config** (`EnvironmentConfig.java`) - For environment-specific URLs/ports (dev, test, prod)

These serve different purposes and can coexist.

### Framework Status

| Framework | Language | Config Source | Status |
|-----------|----------|---------------|--------|
| **Cypress** | TypeScript | `config/port-config.ts` | ✅ Complete |
| **Playwright** | TypeScript | `config/port-config.ts` | ✅ Complete |
| **Vibium** | TypeScript | `config/port-config.ts` | ✅ Complete |
| **Frontend** | TypeScript | `config/port-config.ts` | ✅ Complete |
| **Robot Framework** | Python | `config/port_config.py` via `ConfigHelper.py` | ✅ Complete |
| **Backend** | Python | `config/port_config.py` | ✅ Complete |
| **Selenium/Java** | Java | Optional `EnvironmentConfig.java` | ✅ Complete (Optional) |

### Benefits of Shared Configuration

- ✅ **Single Source of Truth**: All configuration in `config/environments.json`
- ✅ **No Hardcoded Values**: All frameworks read from shared config
- ✅ **Consistent Behavior**: Same configuration across all frameworks
- ✅ **Easy Maintenance**: Update config in one place
- ✅ **Environment Support**: Automatic support for dev, test, prod environments
- ✅ **Type Safety**: TypeScript utilities provide type-safe access
