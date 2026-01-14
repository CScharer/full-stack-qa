# API Version Centralization Plan

**Date**: 2026-01-13  
**Status**: 📋 **PLANNING**  
**Purpose**: Centralize `/api/v1` references to use `config/environments.json` as the single source of truth

---

## 📋 Problem Statement

The API base path `/api/v1` is currently hardcoded in **33+ files** across the codebase, including:
- Backend application code (`backend/app/main.py`)
- Backend test files (6 test files)
- Frontend API client (`frontend/lib/api/client.ts`)
- Shell scripts (startup scripts, CI scripts)
- Performance test files (Locust, JMeter)
- Documentation files

**Current State**: The API base path is defined in `config/environments.json` as `"basePath": "/api/v1"`, but most code doesn't use this value.

**Goal**: All code should read the API base path from `config/environments.json` so it only needs to be changed in one place.

---

## 🎯 Current Configuration

**Location**: `config/environments.json`

```json
{
  "api": {
    "basePath": "/api/v1",
    "healthEndpoint": "/health",
    "docsEndpoint": "/docs",
    "redocEndpoint": "/redoc"
  }
}
```

**Existing Utilities**:
- ✅ `config/port-config.ts` - TypeScript utility (has `getApiConfig()` function)
- ✅ `config/port_config.py` - Python utility (has `get_api_config()` function)
- ✅ `lib/api-utils.ts` - Already uses `DEFAULT_API_VERSION = 'v1'` constant and `getEntityApiVersion()` method

---

## 📊 Inventory of Hardcoded `/api/v1` References

### Backend Application Code

| File | Lines | Usage | Priority |
|------|-------|-------|----------|
| `backend/app/main.py` | 15, 36, 49-54 | FastAPI router prefixes, OpenAPI URL, root endpoint | 🔴 **HIGH** |
| `backend/app/api/v1/__init__.py` | 3 | Comment/documentation | 🟡 **LOW** |

**Total**: 2 files, 7 hardcoded references

### Backend Test Files

| File | Count | Usage | Priority |
|------|-------|-------|----------|
| `backend/tests/test_applications_api.py` | 12 | API endpoint paths in test requests | 🔴 **HIGH** |
| `backend/tests/test_companies_api.py` | 10 | API endpoint paths in test requests | 🔴 **HIGH** |
| `backend/tests/test_contacts_api.py` | 10 | API endpoint paths in test requests | 🔴 **HIGH** |
| `backend/tests/test_clients_api.py` | 9 | API endpoint paths in test requests | 🔴 **HIGH** |
| `backend/tests/test_notes_api.py` | 12 | API endpoint paths in test requests | 🔴 **HIGH** |
| `backend/tests/test_job_search_sites_api.py` | 12 | API endpoint paths in test requests | 🔴 **HIGH** |
| `backend/tests/test_main.py` | 1 | OpenAPI JSON endpoint | 🟡 **MEDIUM** |

**Total**: 7 files, 66 hardcoded references

### Frontend Code

| File | Lines | Usage | Priority |
|------|-------|-------|----------|
| `frontend/lib/api/client.ts` | 16, 33 | Default fallback URL, server-side config reading | 🔴 **HIGH** |

**Total**: 1 file, 2 hardcoded references (but already reads from config on server-side)

### Test Framework Utilities

| File | Status | Notes |
|------|--------|-------|
| `lib/api-utils.ts` | ✅ **GOOD** | Uses `DEFAULT_API_VERSION = 'v1'` constant and `getEntityApiVersion()` method |
| `cypress/cypress/support/api-utils.ts` | ✅ **GOOD** | Uses `getEntityApiVersion()` from base class |
| `playwright/helpers/api-utils.ts` | ✅ **GOOD** | Uses `getEntityApiVersion()` from base class |

**Status**: ✅ Already using constants, but could read from config instead

### Shell Scripts

| File | Lines | Usage | Priority |
|------|-------|-------|----------|
| `scripts/start-fe.sh` | 117, 121, 125, 135, 139, 143 | Setting `NEXT_PUBLIC_API_URL` env var | 🔴 **HIGH** |
| `scripts/start-be.sh` | 246 | Display message | 🟡 **LOW** |
| `scripts/start-services-for-ci.sh` | 451, 454 | Setting `NEXT_PUBLIC_API_URL` env var | 🔴 **HIGH** |
| `scripts/ci/env-config.sh` | 113, 126 | Reading config (already uses config) | ✅ **GOOD** |

**Total**: 4 files, 9 hardcoded references (3 files need updates)

### Performance Test Files

| File | Count | Usage | Priority |
|------|-------|-------|----------|
| `src/test/locust/comprehensive_load_test.py` | 4 | API endpoint paths | 🟡 **MEDIUM** |
| `src/test/locust/api_load_test.py` | 3 | API endpoint paths | 🟡 **MEDIUM** |
| `src/test/jmeter/API_Performance_Test.jmx` | 2 | API endpoint paths in JMeter config | 🟡 **MEDIUM** |

**Total**: 3 files, 9 hardcoded references

### Documentation Files

| File | Count | Usage | Priority |
|------|-------|-------|----------|
| `cypress/README.md` | 1 | Example code | 🟢 **LOW** |
| `backend/README.md` | 1 | Documentation | 🟢 **LOW** |
| Various docs | ~10 | Documentation/examples | 🟢 **LOW** |

**Total**: ~12 files, documentation only (low priority)

---

## 🔧 Implementation Strategy

### Phase 1: Update Shared Utilities (Foundation)

**Goal**: Ensure shared utilities can provide the API base path from config.

#### Step 1.1: Update TypeScript Utilities

**File**: `config/port-config.ts`

**Current**: Has `getApiConfig()` function that returns the full API config object.

**Action**: Add helper function to get base path:
```typescript
export function getApiBasePath(): string {
  return getApiConfig().basePath;
}
```

**File**: `lib/api-utils.ts`

**Current**: Uses `DEFAULT_API_VERSION = 'v1'` constant.

**Action**: Update to read from config:
```typescript
import { getApiBasePath } from '../config/port-config';

// Extract version from basePath (e.g., "/api/v1" -> "v1")
export function getDefaultApiVersion(): string {
  const basePath = getApiBasePath();
  const match = basePath.match(/\/v(\d+)$/);
  return match ? `v${match[1]}` : 'v1'; // Default to v1 if pattern doesn't match
}

export const DEFAULT_API_VERSION = getDefaultApiVersion();
```

#### Step 1.2: Update Python Utilities

**File**: `config/port_config.py`

**Current**: Has `get_api_config()` function.

**Action**: Add helper function:
```python
def get_api_base_path() -> str:
    """Get API base path from config."""
    api_config = get_api_config()
    return api_config.get("basePath", "/api/v1")
```

---

### Phase 2: Update Backend Application Code

**Priority**: 🔴 **HIGH**

#### Step 2.1: Update `backend/app/main.py`

**Current**:
```python
app = FastAPI(
    openapi_url="/api/v1/openapi.json",
)

app.include_router(applications.router, prefix="/api/v1/applications", tags=["applications"])
# ... etc
```

**Proposed**:
```python
from app.config import settings
from config.port_config import get_api_base_path

API_BASE_PATH = get_api_base_path()  # "/api/v1"

app = FastAPI(
    openapi_url=f"{API_BASE_PATH}/openapi.json",
)

app.include_router(applications.router, prefix=f"{API_BASE_PATH}/applications", tags=["applications"])
app.include_router(companies.router, prefix=f"{API_BASE_PATH}/companies", tags=["companies"])
# ... etc
```

**Benefits**:
- Single source of truth
- Easy to change API version
- Consistent across all routers

#### Step 2.2: Update Backend Test Files

**Current**: Tests use hardcoded `/api/v1` in requests:
```python
response = client.post("/api/v1/applications", json=application_data)
```

**Proposed**: Create a test utility or use a constant:
```python
# Option 1: Import from config
from config.port_config import get_api_base_path
API_BASE_PATH = get_api_base_path()

# Option 2: Create test helper
# backend/tests/conftest.py or backend/tests/test_helpers.py
from config.port_config import get_api_base_path

API_BASE_PATH = get_api_base_path()

def api_url(endpoint: str) -> str:
    """Build full API URL from endpoint."""
    return f"{API_BASE_PATH}{endpoint}"

# Usage in tests:
response = client.post(api_url("/applications"), json=application_data)
```

**Files to Update**:
- `backend/tests/test_applications_api.py` (12 references)
- `backend/tests/test_companies_api.py` (10 references)
- `backend/tests/test_contacts_api.py` (10 references)
- `backend/tests/test_clients_api.py` (9 references)
- `backend/tests/test_notes_api.py` (12 references)
- `backend/tests/test_job_search_sites_api.py` (12 references)
- `backend/tests/test_main.py` (1 reference)

**Recommendation**: Create `backend/tests/conftest.py` with `API_BASE_PATH` constant and `api_url()` helper function.

---

### Phase 3: Update Frontend Code

**Priority**: 🔴 **HIGH**

#### Step 3.1: Update `frontend/lib/api/client.ts`

**Current**: Already reads from config on server-side, but has hardcoded fallback:
```typescript
let API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8003/api/v1';
```

**Proposed**: Use config basePath in fallback:
```typescript
import { getApiBasePath } from '../../../config/port-config';

// On server-side, read from config
const apiBasePath = getApiBasePath(); // "/api/v1"
const defaultBackendUrl = `http://localhost:8003${apiBasePath}`;
let API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || defaultBackendUrl;
```

**Note**: Frontend already reads from config on server-side (line 33), so this is mainly about the fallback default.

---

### Phase 4: Update Shell Scripts

**Priority**: 🔴 **HIGH**

#### Step 4.1: Update `scripts/start-fe.sh`

**Current**: Hardcoded `/api/v1` in multiple places:
```bash
export NEXT_PUBLIC_API_URL="http://localhost:8003/api/v1"
```

**Proposed**: Read from config:
```bash
# Read API base path from config
API_BASE_PATH=$(jq -r '.api.basePath' config/environments.json)
export NEXT_PUBLIC_API_URL="http://localhost:${API_PORT}${API_BASE_PATH}"
```

**Files to Update**:
- `scripts/start-fe.sh` (6 references)
- `scripts/start-services-for-ci.sh` (2 references)

#### Step 4.2: Update `scripts/start-be.sh`

**Current**: Hardcoded in display message:
```bash
echo -e "   API: http://$API_HOST:$API_PORT/api/v1"
```

**Proposed**: Read from config:
```bash
API_BASE_PATH=$(jq -r '.api.basePath' config/environments.json)
echo -e "   API: http://$API_HOST:$API_PORT${API_BASE_PATH}"
```

---

### Phase 5: Update Performance Test Files

**Priority**: 🟡 **MEDIUM**

#### Step 5.1: Update Locust Tests

**Files**: 
- `src/test/locust/comprehensive_load_test.py`
- `src/test/locust/api_load_test.py`

**Proposed**: Read from config:
```python
from config.port_config import get_api_base_path

API_BASE_PATH = get_api_base_path()

# Usage:
self.client.get(f"{API_BASE_PATH}/applications", name="2. Browse Applications")
```

#### Step 5.2: Update JMeter Test

**File**: `src/test/jmeter/API_Performance_Test.jmx`

**Challenge**: JMeter uses XML format, can't easily read Python config.

**Options**:
1. **Use JMeter User Defined Variables**: Set `${API_BASE_PATH}` variable, update manually when needed
2. **Generate JMeter config from template**: Use script to inject API base path from config
3. **Document**: Keep hardcoded but document that it should match `config/environments.json`

**Recommendation**: Option 1 (User Defined Variables) - simplest and most maintainable.

---

### Phase 6: Update Test Framework Utilities (Enhancement)

**Priority**: 🟡 **MEDIUM** (Already working, but could be improved)

#### Step 6.1: Update `lib/api-utils.ts`

**Current**: Uses constant `DEFAULT_API_VERSION = 'v1'`.

**Proposed**: Read from config:
```typescript
import { getApiBasePath } from '../config/port-config';

// Extract version from basePath (e.g., "/api/v1" -> "v1")
function getDefaultApiVersion(): string {
  const basePath = getApiBasePath();
  const match = basePath.match(/\/v(\d+)$/);
  return match ? `v${match[1]}` : 'v1';
}

export const DEFAULT_API_VERSION = getDefaultApiVersion();
```

**Benefits**: Automatically uses correct version from config.

---

## 📁 File Structure After Implementation

```
config/
├── environments.json          # Single source of truth (already has basePath)
├── port-config.ts             # ✅ Add getApiBasePath() helper
└── port_config.py             # ✅ Add get_api_base_path() helper

backend/
├── app/
│   └── main.py                # ✅ Use API_BASE_PATH from config
└── tests/
    ├── conftest.py            # ✅ NEW: API_BASE_PATH constant and api_url() helper
    ├── test_applications_api.py  # ✅ Use api_url() helper
    ├── test_companies_api.py     # ✅ Use api_url() helper
    ├── test_contacts_api.py      # ✅ Use api_url() helper
    ├── test_clients_api.py       # ✅ Use api_url() helper
    ├── test_notes_api.py         # ✅ Use api_url() helper
    └── test_job_search_sites_api.py  # ✅ Use api_url() helper

frontend/
└── lib/
    └── api/
        └── client.ts          # ✅ Use getApiBasePath() in fallback

lib/
└── api-utils.ts               # ✅ Read DEFAULT_API_VERSION from config

scripts/
├── start-fe.sh               # ✅ Read API_BASE_PATH from config
├── start-be.sh               # ✅ Read API_BASE_PATH from config
└── start-services-for-ci.sh  # ✅ Read API_BASE_PATH from config

src/test/
├── locust/
│   ├── comprehensive_load_test.py  # ✅ Use get_api_base_path()
│   └── api_load_test.py            # ✅ Use get_api_base_path()
└── jmeter/
    └── API_Performance_Test.jmx   # ✅ Use User Defined Variable
```

---

## ✅ Benefits

### Immediate Benefits
- ✅ **Single Source of Truth**: Change API version in one place (`config/environments.json`)
- ✅ **Consistency**: All code uses the same API base path
- ✅ **Maintainability**: Easier to update when API version changes
- ✅ **Type Safety**: TypeScript utilities provide type-safe access

### Long-term Benefits
- ✅ **API Version Migration**: Easy to migrate from v1 to v2 (or any version)
- ✅ **Testing**: Can test with different API versions
- ✅ **Documentation**: Clear where API version is configured
- ✅ **Reduced Errors**: No risk of mismatched API paths

---

## 🔍 Considerations

### Backward Compatibility
- ✅ All changes maintain backward compatibility (defaults to `/api/v1` if config not found)
- ✅ Environment variables can still override config values

### Performance
- ✅ Config is cached/read once at startup (minimal performance impact)
- ✅ Python config uses module-level caching
- ✅ TypeScript config is imported at module level

### Testing Strategy
- ✅ Update existing tests to use new utilities
- ✅ Verify all tests still pass
- ✅ Test with different API versions (if needed)

---

## 📝 Implementation Checklist

### Phase 1: Foundation
- [ ] Add `getApiBasePath()` to `config/port-config.ts`
- [ ] Add `get_api_base_path()` to `config/port_config.py`
- [ ] Update `lib/api-utils.ts` to read version from config
- [ ] Test utilities work correctly

### Phase 2: Backend
- [ ] Update `backend/app/main.py` to use config
- [ ] Create `backend/tests/conftest.py` with `API_BASE_PATH` and `api_url()` helper
- [ ] Update all backend test files to use helper
- [ ] Test backend application starts correctly
- [ ] Test all backend tests pass

### Phase 3: Frontend
- [ ] Update `frontend/lib/api/client.ts` to use config in fallback
- [ ] Test frontend builds correctly
- [ ] Test frontend API calls work

### Phase 4: Scripts
- [ ] Update `scripts/start-fe.sh` to read from config
- [ ] Update `scripts/start-be.sh` to read from config
- [ ] Update `scripts/start-services-for-ci.sh` to read from config
- [ ] Test scripts work correctly

### Phase 5: Performance Tests
- [ ] Update Locust tests to use config
- [ ] Update JMeter test to use User Defined Variable
- [ ] Test performance tests work correctly

### Phase 6: Documentation
- [ ] Update documentation to reference config
- [ ] Update examples in README files
- [ ] Document how to change API version

---

## 🧪 Testing Strategy

1. **Unit Tests**: Test config utilities return correct values
2. **Integration Tests**: Verify backend routes work with config-based paths
3. **E2E Tests**: Verify frontend can communicate with backend
4. **Script Tests**: Verify startup scripts set correct environment variables
5. **Performance Tests**: Verify Locust/JMeter tests work with config

---

## 📚 References

- **Config File**: `config/environments.json` (line 3: `"basePath": "/api/v1"`)
- **TypeScript Utility**: `config/port-config.ts`
- **Python Utility**: `config/port_config.py`
- **API Utils**: `lib/api-utils.ts` (already has version constants)

---

**Document Status**: 📋 **PLANNING** - Ready for implementation review
