# Maven Dependency CI Scope

**Status**: Active  
**Related**: [Pipeline Workflow](PIPELINE_WORKFLOW.md), [GitHub Actions](GITHUB_ACTIONS.md)  
**Scripts**: `scripts/ci/detect-changes.sh`, `scripts/ci/determine-environments.sh`, `scripts/ci/determine-test-execution.sh`

---

## Purpose

Dependabot (and similar) Maven dependency PRs often change only `pom.xml`. Running the full frontend matrix (Cypress, Playwright, Grid, Mobile, Responsive, etc.) plus BE/FS performance suites wastes CI time and can fail on unrelated flaky UI jobs.

This document defines the **narrow CI scope** for those changes so it can be referenced when reviewing Dependabot PRs or adjusting pipeline gates.

---

## When narrow scope applies

`scripts/ci/detect-changes.sh` sets `maven-deps-only=true` when:

1. At least one **non-documentation** file changed, and
2. Every non-doc path is in the **allowed narrow set**, and
3. At least one **Maven manifest** is in the change set (`pom.xml`, `**/pom.xml`, `.mvn/**`, `mvnw`, `mvnw.cmd`).

### Allowed narrow paths

| Path pattern | Why allowed |
| --- | --- |
| `pom.xml`, `**/pom.xml` | Dependency version bumps |
| `.mvn/**`, `mvnw`, `mvnw.cmd` | Maven wrapper / toolchain |
| `.github/**` | Workflow / Dependabot config shipped with the bump |
| `scripts/ci/**` | CI helper scripts used to implement or tune this scope |
| `docs/**` | Documentation (also treated as docs-only when alone) |

Documentation-only extensions (`.md`, `.log`, `.txt`, `.rst`, `.adoc`) still set `code-changed=false` and skip build/test entirely when they are the only changes.

### When full suite still runs

If **any** non-doc file falls outside the allowed set (for example `src/`, `frontend/`, `backend/`, `cypress/`, `playwright/`, `docker-compose.yml`), then `maven-deps-only=false` and the normal PR defaults apply (full FE + BE + FS in DEV).

Pure workflow-only edits **without** a Maven manifest keep the full defaults (not classified as maven-deps-only).

---

## What runs vs skips

When `maven-deps-only=true`:

### Runs

- Change detection / environment determination
- Build & Compile
- Code Quality / formatting verification
- Dependency version validation
- Docker Build Test (still gated on `code-changed`)
- Security jobs (CodeQL, etc.) that are independent of FE matrix
- **FE Smoke Tests** only (`enable_smoke_tests=true`)

### Skips

- Grid browser matrix
- Mobile Browser Tests
- Responsive Design Tests
- Cypress, Playwright, Robot Framework, Selenide, Vibium
- FE snapshot suite
- BE performance tests
- FS (full-stack) performance tests

`determine-test-execution.sh` forces the default test type to `fe-only` (no BE/FS) unless a manual `workflow_dispatch` input overrides it.  
`determine-environments.sh` turns off every `enable_*` flag except smoke.

---

## Outputs and wiring

| Output | Job | Meaning |
| --- | --- | --- |
| `code-changed` | `detect-file-changes` → `determine-schedule-type` | Any non-doc change (existing) |
| `maven-deps-only` | `detect-file-changes` → `determine-schedule-type` | Narrow Maven/CI-infra scope |

Downstream:

- `determine-envs` receives `maven-deps-only` and sets FE framework flags.
- `determine-test-execution` receives `maven-deps-only` and sets `run_be_tests` / `run_fs_tests` to false for automatic PR/push defaults.

Gate jobs already treat skipped BE/FS as success when `run_be_tests` / `run_fs_tests` are false. Intentionally disabled FE framework jobs are skipped via `enable_*` inputs on `env-fe.yml`.

---

## Working on Dependabot branches

CI scoping fixes (and docs) for a Dependabot Maven PR can be committed **on the Dependabot branch** itself so the open PR re-runs with the narrower suite:

1. Check out `dependabot/maven/<dependency>-<version>`
2. Apply CI/docs changes
3. Commit and push to update the existing Dependabot PR

Editing a Dependabot branch is acceptable for unblocking merge. If Dependabot later needs a clean rebase, use `@dependabot rebase` or recreate on the PR.

---

## Local verification

Simulate outputs (requires `GITHUB_OUTPUT` temp file):

```bash
export GITHUB_OUTPUT=/tmp/gh-out.txt
: > "$GITHUB_OUTPUT"

# Example: pom-only
git diff --name-only main...HEAD
./scripts/ci/detect-changes.sh pull_request "$(git merge-base main HEAD)" HEAD
cat "$GITHUB_OUTPUT"
```

Expected for a Maven-deps / CI-infra PR: `code-changed=true` and `maven-deps-only=true`.

Then:

```bash
: > "$GITHUB_OUTPUT"
./scripts/ci/determine-environments.sh pull_request refs/heads/feature "" "" true
grep enable_ "$GITHUB_OUTPUT"

: > "$GITHUB_OUTPUT"
./scripts/ci/determine-test-execution.sh pull_request refs/heads/feature "" "" "" true
grep run_ "$GITHUB_OUTPUT"
```

Expect smoke enabled only, and `run_be_tests=false` / `run_fs_tests=false`.

---

## Related issues

Flaky Mobile/Responsive failures on a Dependabot Maven PR usually indicate the **wrong jobs ran**, not a regression from the dependency bump. Fix scope first; treat Mobile/Responsive flakes as a separate stability track.
