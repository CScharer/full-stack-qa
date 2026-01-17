# ports.json Removal Plan

**Date**: January 17, 2026  
**Status**: ✅ Phase 1 & 2 Complete - Code and Documentation Updated  
**Purpose**: Plan and document the removal of deprecated `ports.json` file

---

## Executive Summary

The `ports.json` file is deprecated and maintained only as a fallback for backward compatibility. All code has been migrated to use `environments.json` as the primary source. This document outlines the plan to remove `ports.json` and ensure proper fallback behavior when it no longer exists.

---

## Current State

### Deprecation Status

- **Status**: ⚠️ **DEPRECATED** - Maintained for backward compatibility only
- **Primary Source**: All code reads from `environments.json` first
- **Fallback Only**: `ports.json` is only used if `environments.json` is unavailable
- **No Direct Usage**: No code directly reads from `ports.json` - it's only used via fallback logic

### Files Using `ports.json` (as fallback only)

1. **`scripts/ci/port-config.sh`** (Shell script)
   - **Primary**: Reads from `environments.json`
   - **Fallback 1**: Falls back to `ports.json` if `environments.json` unavailable
   - **Fallback 2**: Falls back to hardcoded values if both JSON files unavailable
   - **Current Logic**: 
     ```bash
     1. Try environments.json → 
     2. Try ports.json → 
     3. Use hardcoded values (dev: 3003/8003, test: 3004/8004, prod: 3005/8005)
     ```

2. **`config/port_config.py`** (Python module)
   - **Primary**: Reads from `environments.json`
   - **Fallback 1**: Falls back to `ports.json` if `environments.json` unavailable
   - **Fallback 2**: **NONE** - Raises `FileNotFoundError` if `environments.json` unavailable
   - **Current Logic**:
     ```python
     1. Try environments.json → 
     2. Try ports.json (if environments.json fails) → 
     3. Raise FileNotFoundError (no hardcoded fallback)
     ```

### Current Fallback Chain

| File | Primary | Fallback 1 | Fallback 2 |
|------|---------|------------|------------|
| `port-config.sh` | `environments.json` | `ports.json` | Hardcoded values |
| `port_config.py` | `environments.json` | `ports.json` | **Error** (raises exception) |

---

## Removal Plan

### Phase 1: Update Fallback Logic ✅ **READY**

**Goal**: Ensure both files have proper fallback behavior when `ports.json` is removed.

#### 1.1 Update `port_config.py` Fallback

**Current Issue**: `port_config.py` will raise `FileNotFoundError` if `environments.json` is unavailable and `ports.json` doesn't exist.

**Solution**: Add hardcoded fallback values (matching `port-config.sh`) when both JSON files are unavailable.

**Changes Needed**:
- Modify `_load_config()` to fall back to hardcoded values if `environments.json` is unavailable
- Ensure hardcoded values match those in `port-config.sh`:
  - `dev`: frontend=3003, backend=8003
  - `test`: frontend=3004, backend=8004
  - `prod`: frontend=3005, backend=8005

**Code Location**: `config/port_config.py` - `_load_config()` function

**Example Implementation**:
```python
def _load_config() -> Dict[str, Any]:
    """Load environments.json with caching, fallback to hardcoded values."""
    global _config_cache
    if _config_cache is None:
        if ENVIRONMENTS_JSON.exists():
            with open(ENVIRONMENTS_JSON, 'r', encoding='utf-8') as f:
                _config_cache = json.load(f)
        else:
            # Fallback to hardcoded values if environments.json unavailable
            _config_cache = _get_hardcoded_config()
    return _config_cache

def _get_hardcoded_config() -> Dict[str, Any]:
    """Get hardcoded configuration as fallback."""
    return {
        "environments": {
            "dev": {
                "frontend": {"port": 3003, "url": "http://localhost:3003"},
                "backend": {"port": 8003, "url": "http://localhost:8003"},
                "database": {"name": "full_stack_qa_dev.db"}
            },
            "test": {
                "frontend": {"port": 3004, "url": "http://localhost:3004"},
                "backend": {"port": 8004, "url": "http://localhost:8004"},
                "database": {"name": "full_stack_qa_test.db"}
            },
            "prod": {
                "frontend": {"port": 3005, "url": "http://localhost:3005"},
                "backend": {"port": 8005, "url": "http://localhost:8005"},
                "database": {"name": "full_stack_qa_prod.db"}
            }
        },
        "api": {"basePath": "/api/v1"},
        "timeouts": {"default": 30, "api": 60},
        "cors": {"origins": ["*"]}
    }
```

#### 1.2 Remove `ports.json` References from `port_config.py`

**Changes Needed**:
- Remove `PORTS_JSON` constant
- Remove `_load_ports()` function (no longer needed)
- Remove any references to `ports.json` in comments or code

**Files to Update**:
- `config/port_config.py`

#### 1.3 Remove `ports.json` References from `port-config.sh`

**Changes Needed**:
- Remove `PORT_CONFIG_JSON` variable
- Remove fallback logic that reads from `ports.json`
- Update comments to reflect removal of `ports.json` fallback
- Keep hardcoded fallback values (they become Fallback 1 instead of Fallback 2)

**Files to Update**:
- `scripts/ci/port-config.sh`

**Updated Fallback Chain**:
```bash
1. Try environments.json → 
2. Use hardcoded values (dev: 3003/8003, test: 3004/8004, prod: 3005/8005)
```

---

### Phase 2: Testing and Verification

**Goal**: Verify that removal of `ports.json` doesn't break functionality.

#### 2.1 Test Scenarios

1. **Normal Operation** (both files exist):
   - ✅ Verify `environments.json` is used (primary)
   - ✅ Verify `ports.json` is ignored (not used)

2. **Missing `environments.json`** (simulate removal):
   - ✅ Verify hardcoded fallback values are used
   - ✅ Verify no errors are raised
   - ✅ Verify all environments work (dev, test, prod)

3. **Both Files Missing** (edge case):
   - ✅ Verify hardcoded fallback values are used
   - ✅ Verify no errors are raised

#### 2.2 Test Locations

- **Shell Scripts**: Test `port-config.sh` with missing `environments.json`
- **Python Code**: Test `port_config.py` with missing `environments.json`
- **CI/CD Pipeline**: Verify pipeline still works with `ports.json` removed
- **Local Development**: Verify local scripts still work

#### 2.3 Verification Checklist

- [ ] `port_config.py` works with hardcoded fallback (no `ports.json` dependency)
- [ ] `port-config.sh` works with hardcoded fallback (no `ports.json` dependency)
- [ ] All CI/CD pipelines pass
- [ ] Local development scripts work
- [ ] No references to `ports.json` in code (except documentation)
- [ ] Documentation updated to reflect removal

---

### Phase 3: Remove `ports.json` File

**Goal**: Delete the deprecated file after fallback logic is updated and tested.

#### 3.1 Pre-Removal Checklist

- [x] Phase 1 complete (fallback logic updated)
- [x] Phase 2 complete (documentation updated)
- [x] Local tests pass (✅ Verified January 17, 2026)
  - ✅ Maven compilation successful
  - ✅ Python port_config.py all functions work correctly
  - ✅ Shell port-config.sh all environments work correctly
  - ✅ environments.json is valid JSON
- [ ] CI/CD pipeline passes (pending - will verify after PR)
- [x] Documentation updated

#### 3.2 Removal Steps

1. **Delete File**:
   ```bash
   git rm config/ports.json
   ```

2. **Update `.gitignore`** (if `ports.json` is listed):
   - Remove `ports.json` from `.gitignore` (no longer needed)

3. **Update Documentation**:
   - Update `config/README.md` to remove `ports.json` section
   - Update any other documentation referencing `ports.json`
   - Mark deprecation as complete

4. **Commit**:
   ```bash
   git commit -m "chore: Remove deprecated ports.json file

   - Removed ports.json (deprecated, replaced by environments.json)
   - Updated fallback logic to use hardcoded values
   - All code now uses environments.json as primary source
   - Hardcoded fallback values match previous ports.json values"
   ```

---

## Fallback Behavior After Removal

### Updated Fallback Chain

After `ports.json` is removed, the fallback chain will be:

| File | Primary | Fallback |
|------|---------|----------|
| `port-config.sh` | `environments.json` | Hardcoded values |
| `port_config.py` | `environments.json` | Hardcoded values |

### Hardcoded Fallback Values

If `environments.json` is unavailable, both files will use these hardcoded values:

| Environment | Frontend Port | Backend Port | Frontend URL | Backend URL |
|-------------|---------------|--------------|-------------|-------------|
| `dev` | 3003 | 8003 | `http://localhost:3003` | `http://localhost:8003` |
| `test` | 3004 | 8004 | `http://localhost:3004` | `http://localhost:8004` |
| `prod` | 3005 | 8005 | `http://localhost:3005` | `http://localhost:8005` |

**Note**: These values match the current `ports.json` values, ensuring backward compatibility.

### When Fallback is Used

The hardcoded fallback will be used when:
- `environments.json` is missing or unreadable
- `jq` is not installed (for shell scripts)
- Configuration file is corrupted

**Expected Behavior**:
- ✅ No errors raised
- ✅ Application continues to work
- ✅ Uses hardcoded values matching previous `ports.json` values
- ⚠️ Warning logged (if applicable) indicating fallback is being used

---

## Risk Assessment

### Low Risk ✅

- **Reason**: `ports.json` is only used as fallback, and all code already uses `environments.json` as primary
- **Mitigation**: Hardcoded fallback values match current `ports.json` values
- **Testing**: Comprehensive testing in Phase 2 will verify no breakage

### Potential Issues

1. **Edge Case**: If `environments.json` is missing AND hardcoded fallback fails
   - **Mitigation**: Hardcoded values are simple constants, unlikely to fail
   - **Impact**: Application would fail to start (same as current behavior if both files missing)

2. **Documentation**: Need to update all references to `ports.json`
   - **Mitigation**: Comprehensive search and replace
   - **Impact**: Low - mostly documentation updates

---

## Files to Update

### Code Files

1. **`config/port_config.py`**
   - Add hardcoded fallback function
   - Remove `PORTS_JSON` constant
   - Remove `_load_ports()` function
   - Update `_load_config()` to use hardcoded fallback

2. **`scripts/ci/port-config.sh`**
   - Remove `PORT_CONFIG_JSON` variable
   - Remove `ports.json` fallback logic
   - Update comments to reflect removal
   - Keep hardcoded fallback values

### Documentation Files

1. **`config/README.md`**
   - Remove `ports.json` section
   - Update deprecation status to "Removed"
   - Update migration plan to show completion

2. **Other Documentation** (if any references exist):
   - `docs/README.md`
   - `docs/guides/infrastructure/PORT_CONFIGURATION.md`
   - `docs/QUICK_START.md`
   - Any other files referencing `ports.json`

---

## Implementation Steps

### Step 1: Update Fallback Logic

1. Update `config/port_config.py`:
   - Add `_get_hardcoded_config()` function
   - Update `_load_config()` to use hardcoded fallback
   - Remove `PORTS_JSON` and `_load_ports()` references

2. Update `scripts/ci/port-config.sh`:
   - Remove `PORT_CONFIG_JSON` variable
   - Remove `ports.json` fallback block (lines 68-82)
   - Update comments

### Step 2: Test Changes

1. Test with `environments.json` present (normal operation)
2. Test with `environments.json` missing (fallback to hardcoded values)
3. Test all environments (dev, test, prod)
4. Test CI/CD pipeline
5. Test local development scripts

### Step 3: Update Documentation

1. Update `config/README.md` to remove `ports.json` section
2. Search for all references to `ports.json` in documentation
3. Update references or remove as appropriate

### Step 4: Remove File

1. Delete `config/ports.json`
2. Update `.gitignore` if needed
3. Commit changes

---

## Success Criteria

- [ ] `ports.json` file removed
- [ ] All code works without `ports.json`
- [ ] Hardcoded fallback values work correctly
- [ ] All tests pass
- [ ] CI/CD pipeline passes
- [ ] Documentation updated
- [ ] No references to `ports.json` in code (except historical documentation)

---

## Timeline

**Estimated Effort**: 2-3 hours
- Phase 1 (Update Fallback Logic): 1 hour
- Phase 2 (Testing): 1 hour
- Phase 3 (Remove File): 30 minutes

**Recommended Approach**: 
- Complete Phase 1 and Phase 2 in one PR
- Complete Phase 3 in a separate PR (after verification)

---

## Related Documentation

- **Main Configuration Guide**: `config/README.md`
- **Port Configuration Guide**: `docs/guides/infrastructure/PORT_CONFIGURATION.md`
- **Environment Configuration**: `config/environments.json`

---

**Last Updated**: January 17, 2026  
**Status**: Ready for implementation
