# Naming Standards

**Type**: Process  
**Purpose**: Living document for naming conventions used across the project  
**Created**: 2025-12-21  
**Last Updated**: 2025-12-21  
**Maintained By**: CJS QA Team  
**Status**: Active

### 🔑 Status Legend
- `[✅]` = Completed / Verified / Current
- `[❌]` = Not Started / Needs Action / Failed
- `[🔍]` = In Progress / Under Investigation / Needs Review
- `[⚠️]` = Warning / Critical Issue / Update Available
- `[⏳]` = Pending / Waiting
- `[⏭️]` = Skipped (with justification)
- `[🔒]` = Locked (Do not update without approval)

---

## 📋 CI/CD Pipeline Naming Conventions

### Job Naming
- **Format**: `kebab-case` (lowercase with hyphens)
- **Examples**: 
  - `detect-file-changes`
  - `determine-envs`
  - `test-fe-dev`
  - `test-be-dev`
  - `allure-conversion-be`

### Workflow File Naming
- **Reusable workflows**: `env-{type}.yml` (e.g., `env-fe.yml`, `env-be.yml`)
- **Main pipeline**: `ci.yml`
- **Scheduled workflows**: Descriptive names (e.g., `version-monitoring.yml`)

### Test Type Terminology
- **FE** (Frontend) - Previously "UI" or "UI tests"
- **BE** (Backend) - Previously "Performance" or "Performance tests"

### Artifact Naming
- **Format**: `{tool}-{type}-results-{environment}`
- **Examples**:
  - `gatling-be-results-dev`
  - `jmeter-be-results-test`
  - `locust-be-results-dev`
  - `*-results-{env}` (for FE test results)

---

## 📁 Document Naming Conventions

### Living Documents Only Folders
The following folders and their subfolders should **ONLY** contain living documents (no date prefixes):
- `docs/architecture/` - Architecture documentation
- `docs/assets/` - Static assets (images, SVGs, etc.)
- `docs/guides/` - How-to guides and documentation
- `docs/process/` - Team processes and standards

**Living documents** are continuously maintained and updated, not historical records.

### Living Documents
- **Location**: `docs/process/`, `docs/guides/`, `docs/architecture/` (never date-prefixed)
- **Format**: `UPPERCASE_WITH_UNDERSCORES.md` (no date prefix)
- **Examples**:
  - `NAMING_STANDARDS.md`
  - `VERSION_TRACKING.md`
  - `SCHEDULED_JOBS.md`
  - `PRE_PIPELINE_VALIDATION.md`
  - `GITHUB_ACTIONS.md`
  - `DOCKER.md`

### Date-Prefixed Documents
- **Location**: Various folders based on purpose
- **Format**: `YYYYMMDD_DESCRIPTIVE_NAME.md`
- **Purpose**: Historical records, completed work, investigations
- **Examples**:
  - `docs/archive/2025-12/20251221_PIPELINE_NAMING_FIXES_NEEDED.md`
  - `docs/issues/open/20251112_missing-performance-test-files.md`

### Archive Documents
- **Location**: `docs/archive/YYYY-MM/`
- **Format**: `YYYYMMDD_DESCRIPTIVE_NAME.md`
- **Purpose**: Completed work, superseded documents

---

## 💻 Code Naming Conventions

### Java
- **Classes**: `PascalCase` (e.g., `LoginPage`, `SeleniumGridConfig`)
- **Methods**: `camelCase` (e.g., `navigateToUrl()`, `clickSubmitButton()`)
- **Constants**: `SCREAMING_SNAKE_CASE` (e.g., `DEFAULT_TIMEOUT`, `BASE_URL`)
- **Variables**: `camelCase` (e.g., `userName`, `driver`)

### Scripts
- **Location**: `scripts/` or `scripts/ci/`
- **Format**: `kebab-case.sh` (e.g., `detect-changes.sh`, `check-gate-status-env.sh`)

---

## 🔄 Recent Changes

### 2025-12-21: Pipeline Naming Updates
- **Changed**: `ui` → `fe`, `performance` → `be` throughout CI/CD
- **Workflows**: `test-environment.yml` → `env-fe.yml`, `performance-environment.yml` → `env-be.yml`
- **Jobs**: Updated all job names and references to use FE/BE terminology
- **Status**: ✅ Completed

---

**Last Updated**: 2025-12-21
