# Configuration Files

This directory contains centralized configuration files used across the project.

## Port Configuration

**File**: `config/ports.json`

This is the **single source of truth** for port assignments across all environments. Both shell scripts and TypeScript/JavaScript code read from this file.

### Usage

#### Shell Scripts
```bash
# Source port-config.sh which reads from ports.json
source scripts/ci/port-config.sh
eval "$(get_ports_for_environment "dev")"
echo "Frontend: $FRONTEND_PORT"
echo "Backend: $API_PORT"
```

#### TypeScript/JavaScript
```typescript
import { getPortsForEnvironment } from './config/port-config';
const ports = getPortsForEnvironment('dev');
console.log(ports.frontend.port); // 3003
console.log(ports.backend.port); // 8003
```

### Port Assignments

| Environment | Frontend Port | Backend Port |
|-------------|---------------|--------------|
| dev | 3003 | 8003 |
| test | 3004 | 8004 |
| prod | 3005 | 8005 |

### Updating Ports

To change port assignments:
1. Update `config/ports.json` (single source of truth)
2. Shell scripts will automatically use the new values (via `port-config.sh`)
3. TypeScript/JavaScript will automatically use the new values (via `port-config.ts`)

**Note**: If `jq` is not installed, shell scripts will fall back to hardcoded values in `port-config.sh`. It's recommended to install `jq` for full JSON support.

