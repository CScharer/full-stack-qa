# Service Scripts Guide

**Last Updated**: 2025-12-27  
**Purpose**: Comprehensive guide to service management scripts for CI/CD and local development

---

## 📋 Overview

This guide covers all scripts used for managing application services (Backend and Frontend) and test infrastructure (Selenium Grid) in the full-stack-qa project.

### Service Types

| Service Type | Description | Scripts |
|--------------|-------------|---------|
| **Application Services** | Backend and Frontend services (the application itself) | `start-services-for-ci.sh`, `stop-services.sh`, `verify-services.sh`, `wait-for-services.sh` |
| **Test Infrastructure** | Selenium Grid (test execution infrastructure) | `wait-for-grid.sh` |

---

## 📚 Available Scripts

### Application Service Scripts

#### `scripts/start-services-for-ci.sh`
**Purpose**: Start Backend and Frontend services for CI/CD testing  
**Location**: `scripts/start-services-for-ci.sh`  
**Usage**:
```bash
# Start services with default environment (dev)
./scripts/start-services-for-ci.sh

# Start services for specific environment
ENVIRONMENT=test ./scripts/start-services-for-ci.sh

# Force stop existing services on ports
FORCE_STOP=true ./scripts/start-services-for-ci.sh
```

**Features**:
- ✅ Idempotent - safe to run multiple times
- ✅ Checks if services are already running
- ✅ Uses centralized port configuration (`port-config.sh`)
- ✅ Uses `wait-for-service.sh` utility for service readiness
- ✅ Uses `port-utils.sh` for port management
- ✅ Supports environment-specific ports (dev, test, prod)

**Environment Variables**:
- `ENVIRONMENT` - Environment name (dev, test, prod) - defaults to "dev"
- `API_RELOAD` - Enable/disable API reload (default: "false" for CI)
- `MAX_WAIT` - Maximum wait time for services (default: 120 seconds)
- `FORCE_STOP` - Force stop existing services on ports (default: "false")

---

#### `scripts/stop-services.sh`
**Purpose**: Stop Backend and Frontend services  
**Location**: `scripts/stop-services.sh`  
**Usage**:
```bash
# Stop services (uses default ports)
./scripts/stop-services.sh

# Stop services for specific environment
ENVIRONMENT=test ./scripts/stop-services.sh
```

**Features**:
- ✅ Stops services by PID file (if exists)
- ✅ Stops services by port (fallback)
- ✅ Uses `port-utils.sh` for port management
- ✅ Cleans up Next.js lock files
- ✅ Verifies ports are free after stopping

**Environment Variables**:
- `ENVIRONMENT` - Environment name (dev, test, prod) - defaults to "dev"
- `API_PORT` - Backend API port (default: 8003 for dev)
- `FRONTEND_PORT` - Frontend port (default: 3003 for dev)

---

#### `scripts/ci/verify-services.sh`
**Purpose**: Verify that Backend and Frontend services are running and responding  
**Location**: `scripts/ci/verify-services.sh`  
**Usage**:
```bash
# Verify services for dev environment
./scripts/ci/verify-services.sh http://localhost:3003

# Verify services with custom timeout
./scripts/ci/verify-services.sh http://localhost:3004 60
```

**Features**:
- ✅ Extracts environment from base URL
- ✅ Uses centralized port configuration (`port-config.sh`)
- ✅ Uses `wait-for-service.sh` utility for consistent waiting
- ✅ Uses `port-utils.sh` for port status checking
- ✅ Configurable timeout
- ✅ Clear error messages with port status

**Arguments**:
- `base-url` (required) - Base URL for the environment (e.g., http://localhost:3003)
- `timeout-seconds` (optional) - Timeout in seconds (default: 30)

**Examples**:
```bash
# Verify dev environment
./scripts/ci/verify-services.sh http://localhost:3003

# Verify test environment with longer timeout
./scripts/ci/verify-services.sh http://localhost:3004 60

# Verify prod environment
./scripts/ci/verify-services.sh http://localhost:3005
```

---

#### `scripts/ci/wait-for-services.sh`
**Purpose**: Wait for Backend and Frontend application services to be ready  
**Location**: `scripts/ci/wait-for-services.sh`  
**Usage**:
```bash
# Wait for services (default: 30 attempts, ~60 seconds)
./scripts/ci/wait-for-services.sh http://localhost:3003 http://localhost:8003

# Wait with custom max attempts
./scripts/ci/wait-for-services.sh http://localhost:3003 http://localhost:8003 60

# Wait with environment label
./scripts/ci/wait-for-services.sh http://localhost:3003 http://localhost:8003 30 test
```

**Features**:
- ✅ Wrapper around `wait-for-service.sh` for application services
- ✅ Waits for both Frontend and Backend
- ✅ Checks Frontend endpoint and Backend health endpoint
- ✅ Configurable max attempts
- ✅ Clear progress reporting

**Arguments**:
- `FRONTEND_URL` (required) - Frontend URL (e.g., http://localhost:3003)
- `BACKEND_URL` (required) - Backend URL (e.g., http://localhost:8003)
- `MAX_ATTEMPTS` (optional) - Maximum number of attempts (default: 30, ~60 seconds)
- `ENVIRONMENT` (optional) - Environment name for logging (default: "unknown")

**Used in**: `env-be.yml` workflow (backend tests)

---

### Test Infrastructure Scripts

#### `scripts/ci/wait-for-grid.sh`
**Purpose**: Wait for Selenium Grid to be ready  
**Location**: `scripts/ci/wait-for-grid.sh`  
**Usage**:
```bash
# Wait for Grid (default: 60 seconds)
./scripts/ci/wait-for-grid.sh

# Wait with custom URL and timeout
./scripts/ci/wait-for-grid.sh http://localhost:4444/wd/hub/status 90
```

**Features**:
- ✅ Wrapper around `wait-for-service.sh` for Selenium Grid
- ✅ Additional 5-second wait after Grid is ready (for full initialization)
- ✅ Configurable timeout
- ✅ Clear error messages

**Arguments**:
- `GRID_URL` (optional) - Selenium Grid status URL (default: http://localhost:4444/wd/hub/status)
- `TIMEOUT` (optional) - Timeout in seconds (default: 60)

**Used in**: `env-fe.yml` workflow (frontend tests - smoke, grid, robot, selenide)

---

## 🔧 Shared Utilities

### `scripts/ci/wait-for-service.sh`
**Purpose**: Reusable utility for waiting for any service to be ready  
**Location**: `scripts/ci/wait-for-service.sh`  
**Usage**:
```bash
# Wait for a service (default: 60 seconds, 2 second intervals)
./scripts/ci/wait-for-service.sh http://localhost:3003 "Frontend"

# Wait with custom timeout and interval
./scripts/ci/wait-for-service.sh http://localhost:8003/docs "Backend API" 90 3
```

**Features**:
- ✅ Configurable timeout and check interval
- ✅ Progress reporting every 10 seconds
- ✅ Clear error messages with attempt counts
- ✅ Proper exit codes for success/failure

**Arguments**:
- `url` (required) - Service URL to check
- `service-name` (required) - Human-readable service name
- `timeout-seconds` (optional) - Maximum time to wait (default: 60)
- `check-interval` (optional) - Interval between checks (default: 2)

**Used by**: `start-services-for-ci.sh`, `verify-services.sh`, `wait-for-grid.sh`, `wait-for-services.sh`

**Examples**:
```bash
# Wait for Frontend
./scripts/ci/wait-for-service.sh http://localhost:3003 "Frontend" 30

# Wait for Backend API
./scripts/ci/wait-for-service.sh http://localhost:8003/docs "Backend API" 60 2

# Wait for Selenium Grid
./scripts/ci/wait-for-service.sh http://localhost:4444/wd/hub/status "Selenium Grid" 90
```

---

### `scripts/ci/port-utils.sh`
**Purpose**: Reusable utility for port management operations  
**Location**: `scripts/ci/port-utils.sh`  
**Usage**: Source this file in other scripts:
```bash
source "$(dirname "$0")/port-utils.sh"
```

**Functions Provided**:

1. **`is_port_in_use <port>`**
   - Check if a port is in use
   - Returns 0 if in use, 1 if not
   - Example: `is_port_in_use 8003`

2. **`get_port_pid <port>`**
   - Get the PID of the process using a port
   - Returns PID or empty string
   - Example: `PID=$(get_port_pid 8003)`

3. **`stop_port <port> <service-name> [force]`**
   - Stop the process using a port
   - Graceful kill, then force kill if needed
   - Example: `stop_port 8003 "Backend" "true"`

4. **`check_port_status <port> <service-name>`**
   - Check and display port status information
   - Returns 0 if port is in use, 1 if not
   - Example: `check_port_status 8003 "Backend"`

**Used by**: `start-services-for-ci.sh`, `stop-services.sh`, `verify-services.sh`

**Example Usage in Scripts**:
```bash
#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PORT_UTILS="${SCRIPT_DIR}/scripts/ci/port-utils.sh"

if [ -f "$PORT_UTILS" ]; then
    source "$PORT_UTILS"
    
    # Check if port is in use
    if is_port_in_use 8003; then
        echo "Port 8003 is in use"
        PID=$(get_port_pid 8003)
        echo "PID: $PID"
    fi
    
    # Stop port
    stop_port 8003 "Backend" "true"
    
    # Check port status
    check_port_status 8003 "Backend"
fi
```

---

### `scripts/ci/env-config.sh` (Recommended)
**Purpose**: Comprehensive environment configuration utility (reads from `config/environments.json`)  
**Location**: `scripts/ci/env-config.sh`  
**Usage**: Source this file and use configuration functions:
```bash
source "$(dirname "$0")/env-config.sh"
export_environment_config "dev"
# Now all config vars are set: ports, database, API, timeouts, CORS
```

**Functions Provided**:
- **`get_ports_for_environment <environment>`** - Returns port variables
- **`get_database_for_environment <environment>`** - Returns database variables
- **`get_api_endpoints`** - Returns API endpoint paths
- **`get_timeouts`** - Returns timeout values
- **`get_cors_origins <environment>`** - Returns CORS origins
- **`export_environment_config <environment>`** - Exports all config for an environment

**Used by**: All scripts that need comprehensive configuration

**See Also**: 
- [Port Configuration Guide](./PORT_CONFIGURATION.md) for detailed configuration documentation
- `config/environments.json` - Single source of truth for all configuration

### `scripts/ci/port-config.sh` (Backward Compatibility)
**Purpose**: Port configuration only (reads from `config/environments.json` or `config/ports.json`)  
**Location**: `scripts/ci/port-config.sh`  
**Usage**: Source this file and use `get_ports_for_environment`:
```bash
source "$(dirname "$0")/port-config.sh"
PORT_VARS=$(get_ports_for_environment "dev")
eval "$PORT_VARS"
# Now $FRONTEND_PORT and $API_PORT are set
```

**Function Provided**:
- **`get_ports_for_environment <environment>`**
  - Returns environment variables for ports
  - Reads from `config/environments.json` (preferred) or `config/ports.json` (fallback)
  - Falls back to hardcoded values if `jq` is not installed
  - Example: `PORT_VARS=$(get_ports_for_environment "test")`

**Used by**: Scripts that only need port configuration (backward compatibility)

**Note**: For new code, prefer `env-config.sh` which provides comprehensive configuration.

---

## 🎯 When to Use Which Script

### Starting Services

| Scenario | Script | Example |
|----------|--------|---------|
| **CI/CD Pipeline** | `start-services-for-ci.sh` | `ENVIRONMENT=test ./scripts/start-services-for-ci.sh` |
| **Local Development** | `start-be.sh`, `start-fe.sh` | `./scripts/start-be.sh --env dev` |
| **Both Services Together** | `start-env.sh` | `./scripts/start-env.sh --env test` |

### Stopping Services

| Scenario | Script | Example |
|----------|--------|---------|
| **Stop All Services** | `stop-services.sh` | `./scripts/stop-services.sh` |
| **Stop by Environment** | `stop-services.sh` | `ENVIRONMENT=test ./scripts/stop-services.sh` |

### Verifying Services

| Scenario | Script | Example |
|----------|--------|---------|
| **Verify Services Are Running** | `verify-services.sh` | `./scripts/ci/verify-services.sh http://localhost:3003` |
| **Wait for Services to Be Ready** | `wait-for-services.sh` | `./scripts/ci/wait-for-services.sh http://localhost:3003 http://localhost:8003` |
| **Wait for Selenium Grid** | `wait-for-grid.sh` | `./scripts/ci/wait-for-grid.sh http://localhost:4444/wd/hub/status 90` |

---

## 📖 Common Workflows

### CI/CD Workflow: Start Services for Testing

```bash
# 1. Start services for test environment
ENVIRONMENT=test ./scripts/start-services-for-ci.sh

# 2. Verify services are ready
./scripts/ci/verify-services.sh http://localhost:3004 60

# 3. Run tests
# ... (your test commands)

# 4. Stop services
ENVIRONMENT=test ./scripts/stop-services.sh
```

### Local Development: Start and Verify

```bash
# 1. Start services
./scripts/start-env.sh --env dev

# 2. Verify services (optional)
./scripts/ci/verify-services.sh http://localhost:3003

# 3. Work on application
# ... (your development work)

# 4. Stop services when done
./scripts/stop-services.sh
```

### Frontend Test Workflow: Wait for Grid

```bash
# 1. Start Selenium Grid (via Docker Compose)
docker-compose up -d selenium-hub selenium-chrome selenium-firefox

# 2. Wait for Grid to be ready
./scripts/ci/wait-for-grid.sh http://localhost:4444/wd/hub/status 90

# 3. Run frontend tests
# ... (your test commands)
```

---

## 🔗 Script Relationships

```
┌─────────────────────────────────────────────────────────────┐
│                    Service Scripts                          │
└─────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
        ▼                     ▼                     ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│   Start      │    │    Stop      │    │   Verify     │
│  Services    │    │   Services   │    │   Services   │
└──────────────┘    └──────────────┘    └──────────────┘
        │                     │                     │
        │                     │                     │
        └─────────────────────┼─────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
        ▼                     ▼                     ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│   Port       │    │     Wait     │    │    Port      │
│   Utils      │    │   Service    │    │   Config     │
└──────────────┘    └──────────────┘    └──────────────┘
```

**Dependencies**:
- `start-services-for-ci.sh` → Uses `port-utils.sh`, `wait-for-service.sh`, `port-config.sh`
- `stop-services.sh` → Uses `port-utils.sh`
- `verify-services.sh` → Uses `port-utils.sh`, `wait-for-service.sh`, `port-config.sh`
- `wait-for-services.sh` → Uses `wait-for-service.sh` (internally)
- `wait-for-grid.sh` → Uses `wait-for-service.sh` (internally)

---

## 🛠️ Troubleshooting

### Services Won't Start

1. **Check if ports are already in use**:
   ```bash
   # Using port-utils.sh
   source scripts/ci/port-utils.sh
   is_port_in_use 8003 && echo "Port 8003 is in use"
   ```

2. **Force stop existing services**:
   ```bash
   FORCE_STOP=true ./scripts/start-services-for-ci.sh
   ```

3. **Check service logs**:
   ```bash
   tail -f backend.log
   tail -f frontend.log
   ```

### Services Won't Stop

1. **Stop by port**:
   ```bash
   source scripts/ci/port-utils.sh
   stop_port 8003 "Backend" "true"
   stop_port 3003 "Frontend" "true"
   ```

2. **Kill by process name** (fallback):
   ```bash
   pkill -f "uvicorn app.main:app"
   pkill -f "next dev"
   ```

### Services Not Responding

1. **Verify services are running**:
   ```bash
   ./scripts/ci/verify-services.sh http://localhost:3003 60
   ```

2. **Check port status**:
   ```bash
   source scripts/ci/port-utils.sh
   check_port_status 8003 "Backend"
   check_port_status 3003 "Frontend"
   ```

3. **Wait for services with longer timeout**:
   ```bash
   ./scripts/ci/wait-for-services.sh http://localhost:3003 http://localhost:8003 60
   ```

---

## 📝 Best Practices

1. **Always use environment variables**:
   - Set `ENVIRONMENT` explicitly when starting services
   - Use centralized port configuration (`port-config.sh`)

2. **Use shared utilities**:
   - Prefer `wait-for-service.sh` over inline waiting logic
   - Use `port-utils.sh` for port operations
   - Source utilities with fallback logic

3. **Handle errors gracefully**:
   - All scripts have fallback logic if utilities don't exist
   - Check exit codes when calling scripts
   - Use timeouts for service waiting

4. **Document script usage**:
   - Include usage examples in script headers
   - Update this guide when adding new scripts
   - Document environment variables and arguments

---

## 🔄 Migration Notes

If you're updating scripts to use the new utilities:

1. **Source utilities at the top of your script**:
   ```bash
   SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
   PORT_UTILS="${SCRIPT_DIR}/scripts/ci/port-utils.sh"
   if [ -f "$PORT_UTILS" ]; then
       source "$PORT_UTILS"
   else
       # Fallback: define functions inline
   fi
   ```

2. **Replace inline port checking**:
   ```bash
   # Old
   if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
   
   # New
   if is_port_in_use "$port"; then
   ```

3. **Replace inline service waiting**:
   ```bash
   # Old
   timeout 60 bash -c "until curl -sf $URL; do sleep 2; done"
   
   # New
   ./scripts/ci/wait-for-service.sh "$URL" "Service Name" 60 2
   ```

---

**Last Updated**: 2025-12-27  
**Status**: ✅ Complete - All service scripts documented

