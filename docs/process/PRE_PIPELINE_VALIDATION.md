# Pre-Pipeline Validation Checklist

**Purpose**: This document outlines all validation steps that MUST be completed locally before committing or pushing changes to prevent pipeline failures and catch issues early.

**⚠️ CRITICAL**: If ANY step fails, DO NOT commit or push. Fix the issue and re-run all validation steps.

**🚨 AUTHORIZATION REQUIRED**: Even after all validation steps pass, you MUST get explicit approval for **EACH** commit, push, or PR action. Authorization is required for **EACH** action, even if similar actions were approved previously. **DO NOT** assume that because you were told to commit/push/create PR before, you can do it again without fresh approval after local review.

**🚨 PROTECTED TEST CODE DIRECTORY**: The `src/test/java/` directory contains protected legacy test code. **NEVER** modify, delete, or refactor code in this directory without explicit user approval. You MUST verify with the user **at least TWO times** (before starting work AND before committing). Most code is not currently used but is preserved for future use. See `src/test/java/README.md` for detailed protection rules.

---

## 🔑 Status Legend

- `[✅]` = Completed / Verified / Current
- `[❌]` = Not Started / Needs Action / Failed
- `[🔍]` = In Progress / Under Investigation / Needs Review
- `[⚠️]` = Warning / Critical Issue / Update Available
- `[⏳]` = Pending / Waiting
- `[⏭️]` = Skipped (with justification)
- `[🔒]` = Locked (Do not update without approval)

**Note**: `[ ]` (empty checkbox) is used for markdown checklist items only, not as a status indicator.

---

## 📋 Pre-Commit/Pre-Push Validation Checklist

> **⏱️ Time Estimates** (based on actual testing):
> - **Fast Path** (minimum required): ~50 seconds - 2 minutes
> - **Full Path** (comprehensive): ~15-30 minutes
> 
> **💡 Tip**: Use the automated script for fast path: `./scripts/quality/validate-pre-commit.sh`

### Phase 1: Git Workflow Verification

**Estimated Time**: ~20 seconds

#### 1.1 Branch Verification
- `[ ]` **Verify you're on a feature branch** (NOT `main` or `master`)
  ```bash
  git branch
  # Should show: * feature/your-branch-name
  # NOT: * main
  ```
- `[ ]` **Verify branch is up to date with remote** (if branch exists remotely)
  ```bash
  git fetch origin
  git status
  # Should show: "Your branch is up to date with 'origin/feature/your-branch-name'"
  ```

#### 1.2 Uncommitted Changes Check
- `[ ]` **Review all changes before committing**
  ```bash
  git status
  git diff
  # Verify no unintended files are staged
  # Verify no sensitive data (secrets, passwords, API keys)
  ```

#### 1.3 .gitignore Compliance
- `[ ]` **Verify no ignored files are staged**
  ```bash
  git status --ignored
  # Check that build artifacts, node_modules, target/, etc. are not staged
  ```

---

### Phase 2: Code Quality & Compilation

**Estimated Time**: ~30 seconds - 2 minutes (fast path)

#### 2.0 Code Formatting (⚠️ **REQUIRED BEFORE EVERY COMMIT**)

- `[ ]` **Run Automated Formatting Script** (MANDATORY)
  ```bash
  ./scripts/quality/format-code.sh
  ```
  **Required**: All steps pass (formatting, import sorting, line length fixes, checkstyle verification, compilation)
  
  **What it does**:
  - Formats code and sorts imports (Prettier)
  - Fixes line length issues (Google Java Format)
  - Verifies code quality (Checkstyle)
  - Ensures compilation works
  
  **⚠️ CRITICAL**: This step is **REQUIRED** before every commit to maintain code quality and ensure zero checkstyle violations.

#### 2.1 Java/Maven Build Verification

- `[ ]` **Maven Clean Compile (Main + Test Sources)**
  ```bash
  ./mvnw clean compile test-compile
  ```
  **Required**: BUILD SUCCESS
  
  **⚠️ CRITICAL**: Pipeline runs `compile test-compile`
  - `compile` only compiles `src/main/java`
  - `test-compile` compiles `src/test/java` (where most test code is!)
  - Missing `test-compile` = test compilation errors reach pipeline!

- `[ ]` **Maven Dependency Resolution**
  ```bash
  ./mvnw dependency:resolve
  ```
  **Required**: All dependencies resolve successfully
  
- `[ ]` **Maven Dependency Check (Security)**
  ```bash
  ./mvnw dependency-check:check
  ```
  **Required**: No critical vulnerabilities (warnings acceptable for review)

#### 2.2 Node.js/npm Projects Verification

For each Node.js project (`cypress/`, `playwright/`, `vibium/`, `frontend/`) **that has changes**:

> **💡 Tip**: Use `git diff --name-only` to identify which projects have changes. Only check projects with modifications.

- `[ ]` **Cypress Project** (if `cypress/` files changed)
  ```bash
  cd cypress
  npm ci --dry-run  # Fast validation (recommended)
  # OR: npm ci  # Full install (if --dry-run fails or for final commit)
  ```
  **Required**: No errors, `package-lock.json` in sync
  **Time**: ~2 seconds with `--dry-run`, ~30-60 seconds with full `npm ci`

- `[ ]` **Playwright Project** (if `playwright/` files changed)
  ```bash
  cd playwright
  npm ci --dry-run  # Fast validation (recommended)
  # OR: npm ci  # Full install (if --dry-run fails or for final commit)
  ```
  **Required**: No errors, `package-lock.json` in sync

- `[ ]` **Vibium Project** (if `vibium/` files changed)
  ```bash
  cd vibium
  npm ci --dry-run  # Fast validation (recommended)
  # OR: npm ci  # Full install (if --dry-run fails or for final commit)
  ```
  **Required**: No errors, `package-lock.json` in sync

- `[ ]` **Frontend Project** (if `frontend/` files changed)
  ```bash
  cd frontend
  npm ci --dry-run  # Fast validation (recommended)
  # OR: npm ci  # Full install (if --dry-run fails or for final commit)
  ```
  **Required**: No errors, `package-lock.json` in sync

**⚠️ CRITICAL**: `npm ci` requires `package-lock.json` to be in sync with `package.json`
- **Fast Path**: Use `npm ci --dry-run` for quick validation (~2 seconds per project)
- **Full Path**: Use `npm ci` before final commit to ensure everything installs correctly
- If `npm ci --dry-run` fails, run `npm install` to update `package-lock.json`
- Commit the updated `package-lock.json` file

#### 2.3 TypeScript Compilation (if applicable)

> **💡 Tip**: Only check TypeScript projects that have changes. Use `git diff --name-only` to identify modified projects.

- `[ ]` **TypeScript Type Checking** (for projects with TypeScript changes)
  ```bash
  # For each TypeScript project with changes
  cd cypress && npx tsc --noEmit      # If cypress/ files changed
  cd playwright && npx tsc --noEmit   # If playwright/ files changed
  cd vibium && npx tsc --noEmit       # If vibium/ files changed
  cd frontend && npx tsc --noEmit     # If frontend/ files changed
  ```
  **Required**: No type errors
  **Time**: ~5-10 seconds per project
  **Note**: Can skip projects with no TypeScript file changes

#### 2.4 Python Projects Verification (if applicable)

- `[ ]` **Python Dependencies**
  ```bash
  # If requirements.txt exists
  pip install -r requirements.txt --dry-run
  ```
  **Required**: All dependencies can be resolved

---

### Phase 3: Code Quality Checks

#### 3.1 Java Code Quality (Maven Plugins)

- `[ ]` **Checkstyle** (if enabled)
  ```bash
  ./mvnw checkstyle:check
  ```
  **Required**: No violations (or acceptable violations documented)

- `[ ]` **SpotBugs** (if enabled)
  ```bash
  ./mvnw spotbugs:check
  ```
  **Required**: No critical bugs

- `[ ]` **PMD** (if enabled)
  ```bash
  ./mvnw pmd:check
  ```
  **Required**: No critical violations

**Note**: The formatting script (`./scripts/quality/format-code.sh`) automatically runs checkstyle verification as part of the required pre-commit workflow. You can run checkstyle separately if needed, but the script is the recommended approach.

#### 3.2 JavaScript/TypeScript Linting

- `[ ]` **ESLint (if configured)**
  ```bash
  # For each project with ESLint
  cd cypress && npm run lint
  cd playwright && npm run lint
  cd vibium && npm run lint
  cd frontend && npm run lint
  ```
  **Required**: No linting errors (warnings acceptable for review)

#### 3.3 Python Code Quality (if applicable)

- `[ ]` **Ruff/Black Formatting** (if configured)
  ```bash
  ruff check .
  black --check .
  ```
  **Required**: Code is properly formatted

---

### Phase 4: Local Test Execution

#### 4.1 Fast Smoke Tests (Recommended Before Every Commit)

- `[ ]` **Java Smoke Tests** (if Selenium Grid available and Java code changed)
  ```bash
  # Option 1: With Docker Compose (if Grid is running)
  docker-compose run --rm tests test -Dtest=SmokeTests -Dcheckstyle.skip=true
  
  # Option 2: With local Maven (if Grid is running locally)
  ./mvnw test -Dtest=SmokeTests -Dcheckstyle.skip=true
  ```
  **Required**: All smoke tests pass (Tests run: X, Failures: 0)
  **Duration**: ~2-3 minutes
  **Purpose**: Catch compilation and basic runtime issues before pushing
  
  **⏭️ When to Skip**:
  - Documentation-only changes (no Java code modified)
  - Selenium Grid not available/not running
  - Only frontend/Node.js changes (no Java test changes)

#### 4.2 Node.js Test Execution (No Grid Required)

- `[ ]` **Cypress Tests** (if Cypress changes made)
  ```bash
  cd cypress
  export BASE_URL="https://www.google.com"  # Or your test URL
  export ENVIRONMENT="local"
  npm run cypress:run
  ```
  **Required**: Tests pass or failures are expected/acceptable

- `[ ]` **Playwright Tests** (if Playwright changes made)
  ```bash
  cd playwright
  export BASE_URL="https://www.google.com"  # Or your test URL
  export ENVIRONMENT="local"
  export CI=true
  npm test
  ```
  **Required**: Tests pass or failures are expected/acceptable

- `[ ]` **Vibium Tests** (if Vibium changes made)
  ```bash
  cd vibium
  npm test
  ```
  **Required**: Tests pass or failures are expected/acceptable

#### 4.3 Full Test Suite (Periodic Checkpoints)

- `[ ]` **Full Maven Test Suite** (Every 5-10 commits as checkpoint)
  ```bash
  ./mvnw test -Dcheckstyle.skip=true
  ```
  **Required**: Run periodically, not every commit
  **Duration**: ~10-15 minutes
  **Frequency**: Every 5-10 batches or before major merges

#### 4.4 Docker Build Test (Periodic)

- `[ ]` **Docker Image Build** (Every 3-5 commits)
  ```bash
  docker-compose build tests
  ```
  **Required**: Image builds successfully
  **Frequency**: Every 3-5 batches (not every single batch)

---

### Phase 5: Dependency & Security Checks

#### 5.1 Dependency Version Alignment

- `[ ]` **Verify Selenium Client/Server Version Match** (if Selenium changes)
  - Check `pom.xml` for Selenium client version
  - Check `.github/workflows/test-environment.yml` for `selenium_version` input default value
  - **Required**: Versions must match (e.g., client 4.39.0 = server 4.39.0)
  - **Note**: Server version is now centralized in workflow input variable `selenium_version` (default: 4.39.0)
  - **Reference**: See troubleshooting documentation for Selenium Grid issues

#### 5.2 npm Audit (Security)

- `[ ]` **npm Audit for All Node.js Projects**
  ```bash
  cd cypress && npm audit
  cd playwright && npm audit
  cd vibium && npm audit
  cd frontend && npm audit
  ```
  **Required**: No critical vulnerabilities (moderate/low acceptable for review)

#### 5.3 Maven Dependency Check (Security)

- `[ ]` **Maven Dependency Vulnerability Scan**
  ```bash
  ./mvnw dependency-check:check
  ```
  **Required**: No critical vulnerabilities

---

### Phase 6: Documentation & Configuration

#### 6.1 Documentation Updates

- `[ ]` **README.md Updated** (if functionality changed)
  - Version badges updated
  - Usage instructions current
  - Examples work

- `[ ]` **CHANGE.log Updated** (if applicable)
  - Entry added with timestamp
  - Changes documented
  - Commit hash marked as `[PENDING]` (updated after commit)

- `[ ]` **Process Documentation Updated** (if process changed)
  - Relevant docs in `docs/process/` updated
  - Guides in `docs/guides/` updated if needed

#### 6.2 Configuration File Verification

- `[ ]` **CI/CD Workflow Files** (if `.github/workflows/` changed)
  - Verify YAML syntax is valid
  - Check that all referenced scripts exist
  - Verify environment variables are set correctly

- `[ ]` **Docker Configuration** (if `Dockerfile` or `docker-compose.yml` changed)
  - Verify Dockerfile builds successfully
  - Verify docker-compose services start correctly

---

### Phase 7: Security & Secrets Verification

#### 7.1 Secrets & Credentials Check

- `[ ]` **No Hardcoded Secrets**
  - No API keys in code
  - No passwords in code
  - No tokens in code
  - Use environment variables or secret management

- `[ ]` **No Sensitive Data in Commits**
  ```bash
  git diff
  # Review all changes for:
  # - API keys
  # - Passwords
  # - Tokens
  # - Private keys
  # - Database credentials
  ```

- `[ ]` **.env Files Not Committed**
  ```bash
  git status
  # Verify no .env files are staged
  # Verify .env is in .gitignore
  ```

#### 7.2 File Permissions (if applicable)

- `[ ]` **Script Executability**
  ```bash
  # Verify scripts have correct permissions
  ls -la scripts/
  # Should show executable permissions for .sh files
  ```

---

### Phase 8: Final Pre-Push Verification

#### 8.1 Git Status Final Check

- `[ ]` **Review Final Git Status**
  ```bash
  git status
  # Verify only intended files are staged
  # Verify no build artifacts
  # Verify no temporary files
  ```

#### 8.2 Commit Message Quality

- `[ ]` **Commit Message Follows Standards**
  - Descriptive and clear
  - References issue/PR if applicable
  - Follows project conventions

#### 8.3 Branch Protection

- `[ ]` **Never Push Directly to `main` or `master`**
  ```bash
  git branch
  # MUST be on feature branch
  # If on main/master, create feature branch first
  ```

---

## 🚨 Critical Failure Handling

**If ANY validation step fails:**

1. ❌ **DO NOT commit or push**
2. 🔍 **Identify the root cause**
3. 🔧 **Fix the issue**
4. ✅ **Re-run ALL validation steps from the beginning**
5. ✅ **Proceed only after ALL verifications pass**

---

## ⚡ Quick Validation (Fast Path)

For rapid iteration during development, minimum required checks:

1. ✅ **Git branch verification** (must be feature branch)
2. ✅ **Maven compile + test-compile** (`./mvnw clean compile test-compile`)
3. ✅ **npm ci for affected projects** (if Node.js changes)
4. ✅ **Smoke tests** (if test changes)

**⚠️ Important**: Before final commit/push, run the FULL checklist above.

---

## 🤖 Automated Git Hooks

### Overview

Git hooks are automatically installed via `scripts/install-git-hooks.sh` and provide automatic code formatting and validation.

### Pre-Commit Hook

**Behavior**:
- **Documentation-only changes**: Skips all checks (<1 second)
- **Code changes**: Automatically formats code using `scripts/format-code.sh --skip-compilation --skip-quality-checks` (15-30 seconds)
- **Formatting only**: Prettier, Spotless (imports), Google Java Format
- **No validation**: Checkstyle, PMD, and compilation are skipped (happens in pre-push hook)
- Fast commits: Only formatting, no validation overhead

**Installation**:
```bash
./scripts/utils/install-git-hooks.sh
```

### Pre-Push Hook

**Behavior**:
- **Documentation-only changes**: Skips all checks (<1 second)
- **Code changes**: 
  - Runs code quality checks using `scripts/format-code.sh --ci-mode` (Checkstyle & PMD verification)
  - Validates code using `scripts/validate-pre-commit.sh` (compilation, Node.js, TypeScript, GitHub Actions, Shell scripts, security)
  - **No formatting**: Code is already formatted in pre-commit hook
  - Ensures code quality before reaching main branch (15-30 seconds, faster than before)

**Workflow Validation**:
- Validates all `.github/workflows/*.yml` files using `actionlint`
- Categorizes errors: YAML/syntax errors vs shellcheck issues
- Blocks push if workflow files are invalid
- Fast validation (~1-2 seconds)

**Installation**:
```bash
# Install actionlint for workflow validation (macOS)
brew install actionlint

# Or download from: https://github.com/rhysd/actionlint/releases
```

---

## 🤖 Automated Validation Script

### Overview

An automated validation script is available to streamline the pre-commit validation process: `scripts/validate-pre-commit.sh`

**Note**: This script is automatically run by the pre-push hook for code changes. You can also run it manually before committing.

### Usage

```bash
./scripts/quality/validate-pre-commit.sh
```

### What It Checks

The script automates the following checks:

#### Phase 1: Git Workflow Verification
- ✅ Verifies you're on a feature branch (not `main` or `master`)
- ✅ Checks for uncommitted changes
- ✅ Warns about potentially ignored files in staging area

#### Phase 2: Code Quality & Compilation
- ✅ **Maven**: Runs `clean compile test-compile` (with checkstyle skipped for speed)
- ✅ **Node.js Projects**: Checks `package-lock.json` sync for:
  - `cypress/`
  - `playwright/`
  - `vibium/`
  - `frontend/`
- ✅ **TypeScript**: Runs `tsc --noEmit` for all TypeScript projects

#### Phase 2.4: GitHub Actions Workflow Validation
- ✅ Validates all `.github/workflows/*.yml` files using `actionlint`
- ✅ Categorizes errors: YAML/syntax errors vs shellcheck issues
- ✅ Provides helpful error messages and installation instructions

#### Phase 3: Security & Secrets Verification
- ✅ Scans staged files for common secret patterns:
  - Passwords, API keys, secrets, tokens
  - AWS access keys
  - Private keys
- ✅ Checks for `.env` files in staging area

### Output

The script provides color-coded output:
- 🟢 **Green (✅)**: Check passed
- 🟡 **Yellow (⚠️)**: Warning (non-blocking)
- 🔴 **Red (❌)**: Error (blocking)

### Exit Codes

- `0`: All checks passed (or warnings only)
- `1`: Errors found - fix before committing

### Integration with Checklist

The script covers the **Quick Validation (Fast Path)** checks automatically. For comprehensive validation, still follow the full checklist above, especially:
- Code quality checks (Checkstyle, SpotBugs, PMD)
- Local test execution
- Full dependency security scans
- Documentation updates

### Example Output

```
═══════════════════════════════════════════════════════════════
🚀 Pre-Commit Validation Script
═══════════════════════════════════════════════════════════════

Phase 1: Git Workflow Verification
✅ On feature branch: my-feature-branch
✅ No uncommitted changes

Phase 2: Code Quality & Compilation
✅ Maven compile + test-compile successful
✅ Checked 4 Node.js project(s)
✅ TypeScript type check passed

Phase 3: Security & Secrets Verification
✅ No obvious secret patterns found in staged files
✅ No .env files in staging area

Validation Summary
✅ All checks passed! Ready to commit.
```

### Troubleshooting

**Script fails with "command not found" errors:**
- Ensure you have `mvnw`, `npm`, and `npx` in your PATH
- Make sure the script is executable: `chmod +x scripts/validate-pre-commit.sh`

**TypeScript errors found:**
- Run `npx tsc --noEmit` in the failing project directory to see detailed errors
- Fix the TypeScript errors before committing

**Maven compile fails:**
- Run `./mvnw clean compile test-compile` manually to see detailed errors
- Fix compilation errors before committing

**Secret patterns detected:**
- Review the flagged files carefully
- Remove any actual secrets (use environment variables or secret management)
- False positives are possible - review and adjust if needed

---

## 📝 Validation Checklist Template

Copy this template for each commit/push session:

```markdown
## Pre-Pipeline Validation - [Date] - [Branch Name]

### Phase 1: Git Workflow
- [ ] Branch verification
- [ ] Uncommitted changes review
- [ ] .gitignore compliance

### Phase 2: Code Quality & Compilation
- [ ] Maven clean compile + test-compile
- [ ] npm ci (cypress, playwright, vibium, frontend)
- [ ] TypeScript type checking

### Phase 3: Code Quality Checks
- [ ] Checkstyle/SpotBugs/PMD (if applicable)
- [ ] ESLint (if applicable)

### Phase 4: Local Test Execution
- [ ] Smoke tests
- [ ] Node.js tests (if applicable)

### Phase 5: Dependency & Security
- [ ] Selenium version alignment (if applicable)
- [ ] npm audit
- [ ] Maven dependency check

### Phase 6: Documentation
- [ ] README.md updated (if needed)
- [ ] CHANGE.log updated (if applicable)

### Phase 7: Security
- [ ] No hardcoded secrets
- [ ] No sensitive data in commits

### Phase 8: Final Verification
- [ ] Git status final check
- [ ] Commit message quality
- [ ] Branch protection verified

**Status**: [✅ Ready to Commit] / [❌ Issues Found - See Notes]
**Notes**: [Any issues or warnings]
```

---

## 🔗 Related Documents

- [Quick Reference Guide](./QUICK_REFERENCE.md) - One-page summary of critical checks
- [AI Workflow Rules](./AI_WORKFLOW_RULES.md) - Detailed workflow rules and guidelines for AI-assisted development (includes commit/push approval requirements)
- Selenium Grid Intermittent Failures (archived) - Version alignment reference and Selenium client/server version matching
- [Local Testing Guide](../guides/testing/LOCAL_TESTING.md) - How to run tests locally and debug pipeline failures
- [Version Tracking](./VERSION_TRACKING.md) - Dependency version tracking and update scheduling

---

## 📅 Document History

- **Created**: 2025-12-20
- **Last Updated**: 2025-12-20
- **Purpose**: Comprehensive pre-commit/pre-push validation checklist to prevent pipeline failures

---

## 💡 Tips

1. **Run Fast Checks First**: Start with compilation and quick tests before running full suites
2. **Use Checkpoints**: Run full test suite every 5-10 commits, not every commit
3. **Automate When Possible**: Consider Git hooks or scripts to automate common checks
4. **Document Exceptions**: If you skip a check, document why in commit message
5. **Test Locally First**: Always test locally before pushing to save CI/CD time

---

**Remember**: The goal is to catch issues locally before they reach the pipeline. A few minutes of local validation can save hours of debugging pipeline failures!
