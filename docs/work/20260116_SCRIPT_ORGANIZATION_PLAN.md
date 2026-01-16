# Script Organization Plan (Item #8)

**Date**: 2026-01-16  
**Status**: Proposed  
**Priority**: Low  
**Effort**: Low  
**Risk**: Low (need to update references)

## Current State

The `scripts/` directory has 30+ scripts in the root, making it difficult to find specific scripts. Some organization already exists:
- `scripts/ci/` - CI/CD scripts (59 files)
- `scripts/docker/` - Docker-related scripts (4 files)
- `scripts/lib/` - Common library functions (1 file)
- `scripts/test/` - Test utilities (4 files)
- `scripts/temp/` - Temporary scripts (15 files)

## Proposed Organization

### New Directory Structure

```
scripts/
├── services/          # Service management (start/stop services)
│   ├── start-be.sh
│   ├── start-fe.sh
│   ├── start-env.sh
│   ├── start-services-for-ci.sh
│   └── stop-services.sh
│
├── tests/            # Test execution scripts
│   ├── run-tests.sh
│   ├── run-tests-local.sh
│   ├── run-specific-test.sh
│   ├── run-all-tests-docker.sh
│   ├── run-smoke-tests.sh
│   │
│   ├── frameworks/   # Framework-specific test runners
│   │   ├── run-cypress-tests.sh
│   │   ├── run-playwright-tests.sh
│   │   ├── run-robot-tests.sh
│   │   ├── run-vibium-tests.sh
│   │   ├── run-backend-tests.sh
│   │   ├── run-frontend-tests.sh
│   │   ├── run-api-tests.sh
│   │   └── run-integration-tests.sh
│   │
│   └── performance/  # Performance test runners
│       ├── run-all-performance-tests.sh
│       ├── run-gatling-tests.sh
│       ├── run-jmeter-tests.sh
│       └── run-locust-tests.sh
│
├── build/            # Build and compilation
│   └── compile.sh
│
├── quality/          # Code quality and validation
│   ├── format-code.sh
│   ├── validate-pre-commit.sh
│   ├── validate-dependency-versions.sh
│   └── check_unused_imports.py
│
├── reporting/        # Report generation
│   ├── generate-allure-report.sh
│   └── convert-performance-to-allure.sh
│
├── utils/            # General utilities
│   ├── install-git-hooks.sh
│   ├── cleanup-disk-space.sh
│   └── test-page-object-generator.sh
│
├── ci/               # (existing) CI/CD scripts
├── docker/           # (existing) Docker scripts
├── lib/              # (existing) Common library
├── test/             # (existing) Test utilities
└── temp/             # (existing) Temporary scripts
```

## Benefits

1. **Better Organization**: Scripts grouped by purpose
2. **Easier Discovery**: Developers can find scripts faster
3. **Clearer Structure**: Logical grouping makes the codebase more maintainable
4. **Scalability**: Easy to add new scripts in appropriate categories

## Migration Plan

### Phase 1: Create Directories
- Create new subdirectories: `services/`, `tests/frameworks/`, `tests/performance/`, `build/`, `quality/`, `reporting/`, `utils/`

### Phase 2: Move Scripts
- Move scripts to appropriate subdirectories
- Maintain git history using `git mv` to preserve file history

### Phase 3: Update References
- Search for all references to moved scripts
- Update:
  - GitHub Actions workflows (`.github/workflows/*.yml`)
  - Documentation files (`docs/**/*.md`)
  - Other scripts that call these scripts
  - README files

### Phase 4: Create Wrapper Scripts (Optional)
- Create backward-compatible wrapper scripts in root for commonly used scripts
- Example: `scripts/run-tests.sh` → symlink or wrapper to `scripts/tests/run-tests.sh`

### Phase 5: Update Documentation
- Update `scripts/README.md`
- Update any guides that reference scripts
- Add migration notes

## Files That Need Reference Updates

### GitHub Actions Workflows
- `.github/workflows/ci.yml`
- `.github/workflows/env-*.yml`
- Any other workflows that call scripts

### Documentation
- `scripts/README.md`
- `docs/guides/**/*.md`
- Any other docs referencing scripts

### Other Scripts
- Scripts in `scripts/ci/` that call root-level scripts
- Scripts in `scripts/test/` that call root-level scripts

## Risk Assessment

**Low Risk** because:
- Scripts are self-contained
- References are easy to find with grep
- Can be done incrementally
- Git history preserved with `git mv`

**Mitigation**:
- Test all script references after moving
- Update documentation thoroughly
- Consider keeping wrapper scripts for backward compatibility during transition

## Implementation Notes

1. Use `git mv` to preserve file history
2. Test all workflows after changes
3. Update documentation as scripts are moved
4. Consider adding a migration script to help update references

## Alternative: Keep Root Scripts, Add Organized Structure

Instead of moving everything, we could:
- Keep commonly used scripts in root (e.g., `run-tests.sh`, `start-env.sh`)
- Move less commonly used scripts to subdirectories
- This reduces the number of references to update

## Decision Needed

Should we:
1. **Full reorganization** - Move all scripts to subdirectories (more work, cleaner structure)
2. **Partial reorganization** - Keep common scripts in root, organize others (less work, mixed structure)
3. **Documentation only** - Keep current structure but improve documentation (minimal work, no structural change)
