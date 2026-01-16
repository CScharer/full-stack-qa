# Migrating to Public Repository

> **Living Document** - This guide documents the process for migrating from a private repository to a public repository. Update repository references (`<migrate_repo_from>` and `<migrate_repo_to>`) as needed for your specific migration.

**Repository References**:
- **Old repo**: `<migrate_repo_from>` (private on GitHub)
- **New repo**: `<migrate_repo_to>` (public on GitHub)

**✅ MIGRATION STATUS: 100% COMPLETE** (2025-12-26)
- All phases completed
- New repository fully functional
- Old repository local copy deleted
- Remote old repository kept as backup

---

## Overview
- **Current repo**: `<migrate_repo_from>` (private on GitHub)
- **New repo**: `<migrate_repo_to>` (public on GitHub)
- **Current repo size**: ~2.7GB (2.6GB in .git, ~100MB working files)
- **Available space**: <10GB
- **Goal**: Create new public repo, copy files locally, set up exactly like current repo, then disable automatic jobs in old repo

---

## 🎯 Setup Summary: Match Current Repo Exactly

**This section shows what MUST match your current repository vs what's optional:**

### ⚡ Required Settings (Must Match Current Repo)
- **GitHub Pages**: Source = "GitHub Actions" (current repo uses this)
- **Actions Permissions**: "Read and write permissions" (required for Pages deployment)
- **Repository Visibility**: Public (different from current private repo)
- **Workflows**: All workflow files copied as-is (they work with defaults)

### 💡 Optional Settings (For Future Benefit)
- **Repository Variables**: Skip - Not used in current repo (workflows have defaults)
- **Branch Protection**: Optional - Add if current repo has it, or add later
- **General Features**: Issues (match current), Wiki/Discussions/Projects (optional)
- **Topics**: Add if current repo has them, or add later

### 📝 Current Repo Configuration (Reference)
- **GitHub Pages**: Uses GitHub Actions deployment (`peaceiris/actions-gh-pages@v4`)
- **Actions**: Read/write permissions enabled
- **Variables**: None set (workflows use defaults from `env:` section)
- **Secrets**: Only `GITHUB_TOKEN` (automatic, no setup needed)
- **Branch Protection**: Check your current repo to see if enabled

---

## 📋 Quick Reference: GitHub UI Steps

**All steps that must be performed directly on GitHub's website:**

### Phase 2: Repository Creation & Setup
- [ ] **Step 2.1**: Create new public repository at https://github.com/new
- [ ] **Step 2.2**: Configure repository settings to match current repo:
  - ⚡ **Required**: Pages (GitHub Actions), Actions (Read/Write permissions)
  - 💡 **Optional**: General features, Topics, Branch Protection
  - 💡 **Skip**: Repository Variables (not used in current repo)

### Phase 4: Post-Push Verification
- [ ] **Step 4.3**: Verify repository files and settings on GitHub
- [ ] **Step 4.5**: Verify GitHub Pages and test workflows

### Phase 5: Disable Old Repository Workflows
- [ ] **Step 5.2**: Disable workflows in old repository (via GitHub UI or file modifications)

**Legend**:
- ⚡ **Required**: Must be done to match current repo setup
- 💡 **Optional**: Nice to have, can be done later or skipped
- ⚠️ **Critical**: Required for workflows to function correctly

**Total GitHub UI steps**: ~6-8 steps across 3 phases

---

## 🔑 Status Legend

### Progress Status
| Symbol | Status | Meaning |
|--------|--------|---------|
| ✅ | Completed | Task is complete and verified |
| ❌ | Not Started | Task has not been started |
| 🔍 | In Progress | Task is currently being worked on |
| ⚠️ | Warning | Needs attention or has issues |
| ⏳ | Pending | Waiting on external factors or scheduled |
| ⏭️ | Skipped | Skipped with justification |
| 🔒 | Locked | Do not update without approval |

### Priority Level
| Symbol | Priority | Meaning |
|--------|----------|---------|
| 🔴 | High Priority | Requires immediate action |
| 🟡 | Medium Priority | Important but not urgent |
| 🟢 | Low Priority | Future enhancement or nice-to-have |

### Migration Requirements
| Symbol | Requirement | Meaning |
|--------|-------------|---------|
| ⚡ | Required | Must be done to match current repo setup exactly |
| 💡 | Optional | Can be skipped or done later for future benefit |

### Migration Phase Status
- Use status symbols to track progress through each phase
- Update checklist items with appropriate status symbols as work progresses
- Use ⚡ for required steps, 💡 for optional steps
- Example: `[✅] Phase 1: Create new local directory and copy files`

---

## Phase 1: Prepare New Repository (Local)

### Step 1.1: Create New Local Directory
```bash
cd <parent_directory>
mkdir <migrate_repo_to>
cd <migrate_repo_to>
```

### Step 1.2: Copy Files from Current Repo (Space-Efficient)
Since we have limited space and the .git directory is 2.6GB, we'll copy files without .git initially:

```bash
# Copy all files except .git (saves 2.6GB)
rsync -av --exclude='.git' \
  --exclude='target' \
  --exclude='node_modules' \
  --exclude='.next' \
  --exclude='venv' \
  --exclude='__pycache__' \
  --exclude='*.pyc' \
  --exclude='.pytest_cache' \
  --exclude='.mypy_cache' \
  <parent_directory>/<migrate_repo_from>/ \
  <parent_directory>/<migrate_repo_to>/
```

**Estimated size after copy**: ~100-200MB (source code, configs, docs only)

### Step 1.3: Initialize New Git Repository
```bash
cd <parent_directory>/<migrate_repo_to>
git init
git branch -M main
```

**Expected result**: New git repository initialized with `main` as default branch (no commits yet).

### Step 1.4: Install Git Hooks (⚡ REQUIRED)

**⚠️ CRITICAL**: Git hooks MUST be installed to enforce local testing before pushing changes.

**Why this is required**:
- Pre-push hooks automatically run validation checks before allowing pushes
- Prevents pushing code that fails compilation or validation
- Catches issues locally before they reach the pipeline
- Saves time by catching errors early

**Installation**:
```bash
cd <parent_directory>/<migrate_repo_to>
chmod +x scripts/install-git-hooks.sh
./scripts/utils/install-git-hooks.sh
```

**What gets installed**:
- **Pre-commit hook**: Automatically formats code (skips compilation/validation - runs on push)
- **Pre-push hook**: Formats code AND runs validation checks (compilation, tests, etc.)
- **Post-checkout hook**: Auto-installs hooks when checking out branches

**Pre-push hook behavior**:
- **Documentation-only changes**: Skips all checks (<1 second)
- **Code changes**: 
  - Formats code using `scripts/format-code.sh`
  - Validates code using `scripts/validate-pre-commit.sh` (compilation, Node.js, security)
  - **Blocks push if validation fails**
  - Takes ~30-60 seconds for code changes

**Bypassing hooks** (not recommended):
- Pre-commit: `git commit --no-verify`
- Pre-push: `git push --no-verify`
- **⚠️ Only use bypass in emergencies - hooks are there to prevent pipeline failures**

**Verification**:
```bash
# Verify hooks are installed
test -f .git/hooks/pre-push && echo "✅ pre-push hook installed" || echo "❌ pre-push hook NOT installed"
test -f .git/hooks/pre-commit && echo "✅ pre-commit hook installed" || echo "❌ pre-commit hook NOT installed"
```

**Expected result**: All three hooks installed and executable. Future pushes will automatically validate code before allowing the push.

**⚠️ IMPORTANT**: Always test changes locally before pushing:
- Run `./mvnw validate` or `./mvnw compile` to verify Maven changes
- Run `npm ci` in affected Node.js projects
- Run relevant tests if test code is changed
- The pre-push hook will catch most issues, but manual testing is still recommended

### Step 1.5: Update Repository References

**⚠️ IMPORTANT**: Before committing, we need to replace all instances of `<migrate_repo_from>` with `<migrate_repo_to>` in the new repository.

**What needs to be changed:**
- Repository URLs in documentation
- README.md references
- Any hardcoded repository names in scripts
- GitHub Pages URLs
- Any other references to the old repository name

**This will be done in Phase 3** (Review and Prepare for First Commit) before the first commit.

### Step 1.6: Verify .gitignore is Present
Ensure `.gitignore` is present and includes all build artifacts (should already be copied from Step 1.2).

---

## Phase 2: Create GitHub Repository

### Step 2.1: Create Public Repository on GitHub (GitHub UI)

**⚠️ Perform this step on GitHub directly:**

1. Go to https://github.com/new
2. **Owner**: Select your account (CScharer)
3. **Repository name**: `<migrate_repo_to>`
4. **Description**: `Full Stack QA Automation Framework - Public Repository`
5. **Visibility**: Select **Public** ⚠️
6. **DO NOT** check any of these boxes:
   - ❌ Add a README file
   - ❌ Add .gitignore
   - ❌ Choose a license
7. Click **"Create repository"**

**Why manual creation?** This allows you to configure repository settings before pushing code.

### Step 2.2: Configure Repository Settings (GitHub UI)

**⚠️ Perform these steps on GitHub directly after creating the repo:**

**Goal**: Set up the new repository to match the current repository's configuration exactly.

1. **Go to Repository Settings**: 
   - Navigate to https://github.com/CScharer/<migrate_repo_to>/settings
   - Or: Click "Settings" tab in the new repository

2. **General Settings** (Match Current Repo):
   - **Features**:
     - ⚡ **Issues**: Enable (matches current repo - used for bug reports and feature requests)
     - 💡 **Wiki**: Optional - Enable if you want wiki (current repo may or may not have this enabled)
     - 💡 **Discussions**: Optional - Enable if you want discussions (for future community engagement)
     - 💡 **Projects**: Optional - Enable if you want project boards (for future project management)
   - **Topics**: Add relevant topics to match current repo (e.g., `qa-automation`, `selenium`, `testing`, `ci-cd`)
   - **Description**: Verify description matches: `Full Stack QA Automation Framework - Public Repository`
   - 💡 **Website**: Optional - Leave blank (or add if you have a project website)
   - 💡 **Social preview**: Optional - Upload an image if desired (for better social media sharing)

3. **Pages Settings** (⚡ REQUIRED - Matches Current Repo):
   - Navigate to **Settings → Pages**
   - **Source**: Select **"GitHub Actions"** ⚠️ **This is required**
   - **Why**: The `ci.yml` workflow uses `peaceiris/actions-gh-pages@v4` to deploy Allure reports automatically
   - **Current repo setup**: Uses GitHub Actions deployment (not branch-based deployment)
   - **Result**: Allure reports will be available at `https://cscharer.github.io/<migrate_repo_to>/` after first successful workflow run

4. **Actions Settings** (⚡ REQUIRED - Matches Current Repo):
   - Navigate to **Settings → Actions → General**
   - **Workflow permissions**: 
     - Select **"Read and write permissions"** ⚠️ **This is required**
     - **Why**: Required for GitHub Pages deployment via `peaceiris/actions-gh-pages@v4`
     - **Current repo**: Has "Read and write permissions" enabled
     - 💡 Check **"Allow GitHub Actions to create and approve pull requests"** (optional but useful)
   - **Artifact and log retention**: 
     - Set to **90 days** (matches GitHub default)
     - 💡 **Optional**: Adjust if you want different retention (current repo uses default)

5. **Secrets and Variables** (💡 OPTIONAL - Current Repo Doesn't Have These):
   - Navigate to **Settings → Secrets and variables → Actions**
   - **Repository variables** (click "Variables" tab):
     - **Current status**: ✅ **Confirmed - These variables are NOT set in your current repository**
     - **Why they're optional**: 
       - The workflows have default values in the `env:` section of `ci.yml`:
         ```yaml
         BASE_URL_DEV: 'http://localhost:3003'
         BASE_URL_TEST: 'http://localhost:3004'
         BASE_URL_PROD: 'http://localhost:3005'
         ```
       - The `setup-base-urls.sh` script has fallback logic that uses defaults if variables are empty
     - **When to add them**: 💡 **Future benefit** - Only add if you want to override defaults per-repository
     - **Recommendation**: ✅ **Skip this step** - workflows work perfectly with built-in defaults
   - **Repository secrets**: 
     - **Current status**: Check your current repo's secrets (Settings → Secrets and variables → Actions → Secrets)
     - **Most workflows use**: `GITHUB_TOKEN` (automatically provided by GitHub - no setup needed)
     - **If current repo has custom secrets**: Copy them to the new repo
     - **If no custom secrets exist**: ✅ **Skip this step**

6. **Branch Protection** (💡 OPTIONAL - Recommended for Future):
   - Navigate to **Settings → Branches**
   - **Current repo status**: Check if your current repo has branch protection enabled
   - **If current repo has it**: Match the same settings
   - **If current repo doesn't have it**: 🟢 **Optional - Recommended for future**:
     - Click **"Add branch protection rule"**
     - **Branch name pattern**: `main`
     - **Protect matching branches**:
       - ✅ Require a pull request before merging
       - ✅ Require approvals (set to 1 or more)
       - 🟢 Require status checks to pass (optional - can configure later)
       - ✅ Require branches to be up to date before merging
     - Click **"Create"**
   - **Note**: This is a best practice but not required for initial migration

### Step 2.3: Add Remote (Local Terminal)

**After creating the repository on GitHub, connect your local repo:**

```bash
cd <parent_directory>/<migrate_repo_to>
git remote add origin https://github.com/CScharer/<migrate_repo_to>.git
git remote -v  # Verify remote is set correctly
```

**Alternative: Using GitHub CLI**
```bash
# If you used GitHub CLI to create the repo, remote is already set
# Otherwise, use the command above
```

---

## Phase 3: Review and Prepare for First Commit

### Step 3.1: Replace Repository Name References

**⚠️ CRITICAL**: Replace all instances of `<migrate_repo_from>` with `<migrate_repo_to>` in the new repository.

**What I'll do:**
1. Search for all occurrences of `<migrate_repo_from>` in the new repo
2. Review each occurrence to determine if it needs to be changed
3. Replace repository name references with `<migrate_repo_to>`
4. Update GitHub URLs, documentation references, etc.

**Files likely to need updates:**
- `README.md` - Repository URLs and references
- `docs/guides/infrastructure/GITHUB_PAGES_SETUP.md` - GitHub Pages URL
- Any scripts that reference the repository name
- Documentation files that mention the repository
- `.github/workflows/*.yml` - If any hardcoded repository references exist

**Command to find all occurrences:**
```bash
cd <parent_directory>/<migrate_repo_to>
grep -r "<migrate_repo_from>" . --exclude-dir=.git 2>/dev/null | head -20
```

### Step 3.2: Review What Will Be Committed
```bash
cd <parent_directory>/<migrate_repo_to>
git status
git add .
git status  # Review what's staged
```

### Step 3.3: Check for Sensitive Information
Before committing, verify no sensitive data is included:
- Check for `.env` files
- Check for `*-key.json` files
- Check for `config/Environments.xml` (should be in .gitignore)
- Review `.gitignore` to ensure all sensitive patterns are covered

### Step 3.4: Make Any Other Necessary Changes
- Verify README.md is updated with new repository name
- Ensure all repository references are updated
- Ensure all secrets are properly ignored

---

## Phase 4: First Commit and Push

### Step 4.1: Create Initial Commit
```bash
cd <parent_directory>/<migrate_repo_to>
git add .
git commit -m "Initial commit: Migrate from private <migrate_repo_from> repository"
```

### Step 4.2: Push to GitHub
```bash
git push -u origin main
```

### Step 4.3: Verify Repository on GitHub (GitHub UI)

**⚠️ Perform these verification steps on GitHub directly:**

1. **Verify Files**: ✅ **COMPLETED**
   - Visit https://github.com/CScharer/<migrate_repo_to>
   - Browse the repository structure
   - ✅ Verified: All expected files and directories are present
   - ✅ Verified: Sensitive files are NOT visible (`.env`, `*-key.json`, etc.)

2. **Verify Repository Settings**: ✅ **COMPLETED**
   - Go to **Settings → General**
   - ✅ Verified: Repository is **Public**
   - ✅ Verified: Description and topics are set correctly

3. **Verify Workflows**: ✅ **COMPLETED**
   - Go to **Actions** tab
   - ✅ Verified: Workflows are present (`ci.yml`, `env-fe.yml`, `env-be.yml`, etc.)
   - ✅ Verified: Workflows are running automatically
   - ⚠️ **Issue Found**: BE tests failing with 500 errors (see Step 4.3.1 below)

4. **Verify Variables** (if you set them in Step 2.2):
   - Go to **Settings → Secrets and variables → Actions → Variables** tab
   - Verify `BASE_URL_DEV`, `BASE_URL_TEST`, `BASE_URL_PROD` are present
   - If missing, add them now (see Step 2.2 for values)
   - **Note**: Variables are optional (workflows have defaults)

5. **Test Workflow**: ✅ **COMPLETED**
   - Go to **Actions** tab
   - ✅ Verified: `verify-formatting.yml` ran automatically (did not need manual trigger)
   - ✅ Verified: Pipeline is working and passing (except BE tests - see issue below)

### Step 4.3.1: Backend Test Failures (500 Errors)

**⚠️ ISSUE IDENTIFIED**: Backend API tests are failing with 500 status codes:

```
Error report
# occurrences      Error                                                                                               
------------------|---------------------------------------------------------------------------------------------------------------------------------------------
55                 GET GET /companies: CatchResponseError('Status code: 500')                                          
52                 GET GET /applications: CatchResponseError('Status code: 500')                                       
15                 GET GET /contacts: CatchResponseError('Status code: 500')                                           
```

**Analysis**:
- **Status**: ⚠️ Backend API endpoints returning 500 (Internal Server Error)
- **Affected endpoints**: `/companies`, `/applications`, `/contacts`
- **Impact**: BE performance tests (Gatling, JMeter, Locust) are failing
- **FE tests**: ✅ Passing
- **Pipeline**: ✅ Running and working (except BE test failures)

**Possible Causes**:
1. Backend service not starting correctly in CI
2. Database connection issues (database file renamed, connection strings may need update)
3. Missing environment variables or configuration
4. Backend dependencies not installed correctly

**Next Steps** (to be addressed):
- ✅ Review backend startup logs in CI
- ✅ Verify database connection strings reference `full_stack_qa.db`
- ✅ Check backend environment configuration
- ✅ Review CI workflow for backend service setup

**✅ RESOLVED**: Backend database references updated in:
- `backend/app/config.py` - Default database path updated
- `backend/tests/conftest.py` - Test database name updated
- `backend/README.md` - Documentation references updated

**Status**: BE tests now passing in CI (PR #1)

---

## Phase 4.5: Post-Push GitHub Configuration (GitHub UI)

**⚠️ Perform these steps on GitHub after first push:**

### Step 4.5.1: Verify Repository Variables (Optional - Can Skip)
1. Go to **Settings → Secrets and variables → Actions → Variables** tab
2. **Check if variables exist**: Look for `BASE_URL_DEV`, `BASE_URL_TEST`, `BASE_URL_PROD`
3. **Current status**: ✅ **Confirmed - These variables are NOT currently set** in your old repository
4. **Why this step is optional**:
   - The workflows have default values defined in `ci.yml`:
     ```yaml
     env:
       BASE_URL_DEV: 'http://localhost:3003'
       BASE_URL_TEST: 'http://localhost:3004'
       BASE_URL_PROD: 'http://localhost:3005'
     ```
   - The `setup-base-urls.sh` script has fallback logic that uses these defaults if GitHub variables are empty
   - **Workflows will work correctly without setting these variables**
5. **When to add variables**: Only if you want to override the defaults for this specific repository
6. **Recommendation**: ✅ **You can skip this step entirely** - the workflows will use the built-in defaults

### Step 4.5.2: Verify GitHub Pages is Configured (⚡ REQUIRED) ✅ **COMPLETED**
1. Go to **Settings → Pages**
2. ✅ Verified: **Source** is set to **"GitHub Actions"**
3. **Why this is required**: 
   - The `ci.yml` workflow uses `peaceiris/actions-gh-pages@v4` to deploy
   - This matches your current repository's setup exactly
   - Allure reports will be available at `https://cscharer.github.io/<migrate_repo_to>/` after first successful run
4. **Current repo setup**: Uses GitHub Actions deployment (not branch-based)

### Step 4.5.3: Test Workflow Execution ✅ **COMPLETED**
1. Go to **Actions** tab
2. ✅ Verified: Workflows are running automatically (no manual trigger needed)
3. ✅ Verified: `verify-formatting.yml` ran automatically
4. ✅ Verified: Pipeline is working and passing (except BE tests)
5. ⚠️ **Issue Found**: Backend tests failing with 500 errors (see Step 4.3.1 for details)
6. **Status**: 
   - ✅ FE tests: Passing
   - ✅ Pipeline: Running and working
   - ⚠️ BE tests: Failing (500 errors on API endpoints)

### Step 4.5.4: Review Repository Readme
1. Go to repository main page: https://github.com/CScharer/<migrate_repo_to>
2. Verify README.md displays correctly
3. Check that all links work
4. Update README if it references the old repository name

---

## Phase 5: Verify New Repo is Working Correctly

**⚠️ IMPORTANT**: Complete this phase before disabling old repo workflows. Verify that all tests pass and the new repository is fully functional.

### Step 5.1: Verify CI/CD Pipeline

1. **Check PR Status**:
   - Go to PR #1: https://github.com/CScharer/<migrate_repo_to>/pull/1
   - Verify all CI checks are passing (green checkmarks)
   - Review any failed jobs and address issues

2. **Verify Workflow Execution**:
   - Go to **Actions** tab: https://github.com/CScharer/<migrate_repo_to>/actions
   - Check that all workflows run successfully:
     - `ci.yml` - Main CI pipeline
     - `env-fe.yml` - Frontend environment tests
     - `env-be.yml` - Backend environment tests
     - `verify-formatting.yml` - Code formatting checks

3. **Verify Test Results**:
   - ✅ Backend tests: Should pass (no more 500 errors)
   - ✅ Frontend tests: Should pass
   - ✅ Cypress tests: Should pass (port configuration fixed)
   - ✅ Performance tests: Should pass (Gatling, JMeter, Locust)
   - ✅ Code quality checks: Should pass (Spotless, Checkstyle, PMD)
   - ✅ Code Quality Analysis: Fixed (pmd-ruleset.xml added, script updated to use mvnw)

### Step 5.2: Verify Database Connections

1. **Check Backend API Endpoints**:
   - Verify `/companies` endpoint returns 200 (not 500)
   - Verify `/applications` endpoint returns 200 (not 500)
   - Verify `/contacts` endpoint returns 200 (not 500)
   - All endpoints should connect to `full_stack_qa.db` successfully

2. **Review CI Logs**:
   - Check backend service startup logs in CI
   - Verify database file is found and accessible
   - Confirm no database connection errors

### Step 5.3: Verify GitHub Pages Deployment

**⚠️ IMPORTANT**: GitHub Pages only deploys on `main` branch, so PR must be merged first.

1. **Merge PR to main** (required for GitHub Pages deployment):
   - Merge PR #1: https://github.com/CScharer/<migrate_repo_to>/pull/1
   - This will trigger a new CI run on `main` branch
   - GitHub Pages deployment will happen automatically on `main`

2. **Check Allure Reports** (after PR merge):
   - **For PR branches**: Reports are available as artifacts (not deployed to GitHub Pages)
     - Go to **Actions** tab → Select the workflow run
     - Download artifact: `allure-report-combined-all-environments`
     - Extract and open `index.html` in a browser
   - **For main branch**: Reports are deployed to GitHub Pages
     - Visit: https://cscharer.github.io/<migrate_repo_to>/
     - Verify reports are generated and accessible
     - Check that test results are displayed correctly
   - **Note**: GitHub Pages only deploys on `main` branch when code changes are detected

3. **Verify Pages Source**:
   - Go to **Settings → Pages**
   - Confirm source is set to "GitHub Actions"
   - Verify deployment is working (will show after PR is merged to main)

### Step 5.4: Final Verification Checklist

- [x] All CI workflows passing ✅ **VERIFIED**
- [x] Backend tests passing (no 500 errors) ✅ **FIXED & VERIFIED**
- [x] Frontend tests passing ✅ **VERIFIED**
- [x] Cypress tests passing (port configuration) ✅ **FIXED & VERIFIED**
- [x] Performance tests passing ✅ **VERIFIED**
- [x] Code Quality Analysis passing (Checkstyle, PMD) ✅ **FIXED & VERIFIED**
- [x] Port configuration centralized (prevents future mismatches) ✅ **FIXED**
- [x] PR #1 merged to main ✅ **COMPLETED**
- [x] PR #2 merged to main ✅ **COMPLETED**
- [x] GitHub Pages deployed and accessible ✅ **VERIFIED**
- [x] Allure Reports accessible at https://cscharer.github.io/<migrate_repo_to>/ ✅ **VERIFIED**
- [x] Database connections working ✅ **VERIFIED**
- [x] All features accessible ✅ **VERIFIED**
- [x] No critical errors in logs ✅ **VERIFIED**

**✅ Code Quality Analysis Fixes Applied:**
- Added `pmd-ruleset.xml` to repository (was missing from initial commit)
- Updated `verify-code-quality.sh` to use `./mvnw` instead of `mvn`
- Updated `.gitignore` to allow `pmd-ruleset.xml` (changed from `pmd-*.xml` to specific patterns)

**✅ MIGRATION PHASE 5 COMPLETE**: All verification items completed successfully!

**Status**: 
- All PRs merged to `main`
- GitHub Pages deployed and accessible
- Allure Reports live at: https://cscharer.github.io/<migrate_repo_to>/
- All tests passing
- All fixes applied and verified

**Next**: Proceed to Phase 6 (disable old repo workflows) - ✅ **COMPLETED**

---

## Phase 6: Disable Automatic Jobs in Old Repository

**⚠️ IMPORTANT**: Only do this AFTER the new repo is working and you've verified everything is set up correctly.

### Step 6.1: Identify Automatic Workflows ✅ **COMPLETED**

The following workflows run automatically in the old repo:

1. **`.github/workflows/ci.yml`**
   - Triggers: `push` (main/develop), `pull_request` (main/develop), `schedule` (nightly/weekly)
   - **Action**: ✅ **COMPLETED** - Scheduled runs disabled (commented out)
   - **Status**: Push/PR triggers remain active (intentional - can disable later if needed)

2. **`.github/workflows/version-monitoring.yml`**
   - Triggers: `schedule` (nightly), `workflow_dispatch`
   - **Action**: ✅ **COMPLETED** - Scheduled runs disabled (commented out)

3. **`.github/workflows/verify-formatting.yml`**
   - Triggers: `pull_request`, `push`
   - **Action**: ⚠️ **NOT DISABLED** - Left active (intentional - can disable later if needed)

### Step 5.2: Disable Workflows (Option 1: Disable in GitHub UI)

**⚠️ Perform these steps on GitHub directly:**

1. **Navigate to Repository Settings**:
   - Go to https://github.com/CScharer/<migrate_repo_from>/settings/actions

2. **Disable Workflow Runs**:
   - Scroll to **"Workflow permissions"** section
   - You can temporarily disable all workflows, but a better approach is Method 2 or 3 below

3. **Alternative: Disable Individual Workflows**:
   - Go to **Actions** tab: https://github.com/CScharer/<migrate_repo_from>/actions
   - For each workflow (`ci.yml`, `version-monitoring.yml`, `verify-formatting.yml`):
     - Click on the workflow name
     - Click **"..." (three dots) → Disable workflow**
     - Confirm the disable action
   - This disables the workflows without modifying files

4. **Verify Workflows are Disabled**:
   - Go back to **Actions** tab
   - You should see a message indicating workflows are disabled
   - Scheduled runs will not execute
   - Push/PR triggers will not execute
   - Manual triggers (`workflow_dispatch`) will still work if you want to test

### Step 5.3: Disable Workflows (Option 2: Modify Workflow Files)

**Method A: Comment out triggers** (keeps workflow files but disables them):

**For `ci.yml`**:
```yaml
on:
  # push:
  #   branches: [ main, develop ]  # Disabled for migration
  # pull_request:
  #   branches: [ main, develop ]  # Disabled for migration
  # schedule:
  #   # Disabled for migration
  #   - cron: '0 9 * * *'
  #   - cron: '0 9 * * 0'
  workflow_dispatch:
    # Keep manual trigger enabled
    ...
```

**For `version-monitoring.yml`**:
```yaml
on:
  # schedule:
  #   # Disabled for migration
  #   - cron: '0 9 * * *'
  workflow_dispatch:
    # Keep manual trigger enabled
```

**For `verify-formatting.yml`**:
```yaml
on:
  # pull_request:
  #   branches: [main, develop]  # Disabled for migration
  # push:
  #   branches: [main, develop]  # Disabled for migration
  workflow_dispatch:
    # Keep manual trigger enabled
```

**Method B: Add job-level condition** (disable all jobs but keep triggers):

Add this to each job in the workflow:
```yaml
jobs:
  job-name:
    if: false  # Disable this job
    runs-on: ubuntu-latest
    ...
```

**Method C: Rename workflow files** (temporarily disable):
```bash
cd <parent_directory>/<migrate_repo_from>
mv .github/workflows/ci.yml .github/workflows/ci.yml.disabled
mv .github/workflows/version-monitoring.yml .github/workflows/version-monitoring.yml.disabled
mv .github/workflows/verify-formatting.yml .github/workflows/verify-formatting.yml.disabled
```

### Step 5.4: Commit and Push Disable Changes (if using Method B or C)

**If you chose to modify workflow files (Method B or C from Step 5.3):**

```bash
cd <parent_directory>/<migrate_repo_from>
git checkout -b disable-automatic-workflows
# Make changes to workflow files (comment out triggers or rename files)
git add .github/workflows/
git commit -m "Disable automatic workflow triggers for repository migration"
git push origin disable-automatic-workflows
```

**Then create PR on GitHub:**
1. Go to https://github.com/CScharer/<migrate_repo_from>/pulls
2. Click **"New pull request"**
3. Select `disable-automatic-workflows` branch
4. Review changes
5. Click **"Create pull request"**
6. Merge the PR after review

**Note**: If you used Method 1 (GitHub UI disable), you don't need to commit anything - the workflows are already disabled.

---

## Phase 6: Post-Migration Cleanup

### Step 6.1: Update Documentation
- Update any references to old repo name in new repo
- Update README if needed

### Step 6.2: Database Updates Required

**⚠️ IMPORTANT**: The database file has been renamed from `full_stack_testing.db` to `full_stack_qa.db` during migration, but additional updates may be needed:

**✅ Already Completed:**
- Database file renamed: `full_stack_testing.db` → `full_stack_qa.db`
- `data/core/README.md` - All references updated (location path, all sqlite3 commands)
- `data/core/tests/conftest.py` - Test database name updated (`test_full_stack_qa.db`)
- `data/core/scripts/seed_job_search_sites.py` - Database path updated
- ✅ **Backend Code Updated** (Fixed BE Test Failures):
  - `backend/app/config.py` - Default `database_path` updated from `full_stack_testing.db` to `full_stack_qa.db`
  - `backend/tests/conftest.py` - Test database name updated from `test_full_stack_testing.db` to `test_full_stack_qa.db`
  - `backend/README.md` - All 3 documentation references updated (lines 11, 20, 105)

**🔍 Still Need to Review/Update:**

1. **Application Code**: ✅ **FIXED**
   - ✅ **Resolved**: Backend API 500 errors fixed by updating database references
   - ✅ `backend/app/config.py` - Database path configuration updated
   - ✅ `backend/tests/conftest.py` - Test database naming updated
   - ✅ `backend/README.md` - Documentation references updated
   - **Status**: Changes committed, ready to test in CI

2. **Database Schema/Content** (if database contains data):
   - Review if any database records reference the old repository name
   - Check if seed data or migrations reference the old database name
   - Consider if data migration is needed for existing records

3. **CI/CD and Scripts**:
   - Review any CI/CD workflows that reference the database file
   - Check backup/restore scripts for database file references
   - Update any deployment scripts that reference the database

4. **Documentation** (if any remaining):
   - Search for any remaining documentation that references the old database name
   - Update any database setup or migration guides

**Note**: Since only one database file was defined (`full_stack_testing.db`), we've renamed it to `full_stack_qa.db`. If there are other database files or configurations that reference this database, they will need to be updated as well.

### Step 6.3: Verify New Repo Functionality
- Test that workflows work in new repo
- Verify all features are accessible
- Test CI/CD pipeline
- Verify database connections work with new database file name

### Step 6.4: Archive Old Repository (Optional) ⏭️ **DEFERRED**

**Status**: Old repository kept as private backup (recommended)

**Options**:
- ✅ **Current**: Keep as private backup/reference (recommended)
- 💡 **Future**: Can archive on GitHub if desired (marks as read-only)
- ❌ **Not Recommended**: Delete repository (loses backup)

**Recommendation**: Keep as private backup for now. Can archive later if desired.

---

## Phase 7: Post-Migration Cleanup and Verification ✅ **MOSTLY COMPLETE**

### Step 7.1: Final Status Summary

**Migration Status**: ✅ **COMPLETE**

**New Repository (`<migrate_repo_to>`)**:
- ✅ All workflows passing
- ✅ All tests passing (Backend, Frontend, Cypress, Performance)
- ✅ Code Quality Analysis passing
- ✅ GitHub Pages deployed and accessible
- ✅ Allure Reports live at: https://cscharer.github.io/<migrate_repo_to>/
- ✅ Scheduled jobs active (nightly/weekly)
- ✅ All fixes applied and verified

**Old Repository (`<migrate_repo_from>`)**:
- ✅ Scheduled jobs disabled (cron triggers commented out)
- ⚠️ Push/PR triggers still active (intentional - can disable later)
- ✅ Manual triggers available via `workflow_dispatch`
- ✅ Kept as private backup/reference

### Step 7.2: Local Repository Cleanup ✅ **COMPLETED**

**Local Old Repository** (`<parent_directory>/<migrate_repo_from>`):
- **Status**: ✅ **DELETED** - Local copy has been removed
- **Remote repository**: ✅ **KEPT** - Remote repo remains as private backup on GitHub
- **Reason**: All code is in new repo (`<migrate_repo_to>`), old repo remote serves as backup

**Action Taken**:
- ✅ **Local old repo deleted**: Removed `<parent_directory>/<migrate_repo_from>` (~2.9GB saved)
- ✅ **Remote old repo kept**: Remains as private backup on GitHub (`CScharer/<migrate_repo_from>`)
- ✅ **New repo verified**: All functionality confirmed working before deletion

**Deletion Command Used**:
```bash
cd <parent_directory>
rm -rf <migrate_repo_from>
```

**Note**: Only the local copy was deleted. The remote repository on GitHub remains intact as a backup and can be cloned again if needed.

---

## Space-Saving Notes

- **Current repo**: 2.7GB total (2.6GB .git + ~100MB files)
- **After copy (without .git)**: ~100-200MB
- **New repo .git (after first commit)**: Will be much smaller (no history)
- **Total space used**: ~300-400MB for new repo

**Space saved by excluding .git**: ~2.6GB (new repo won't have full history)

---

## Checklist

Use the status legend symbols to track progress:

### Required Steps (Match Current Repo)
- [ ] [🔍] Phase 1: Create new local directory and copy files
  - [✅] Step 1.1: Create new local directory `<parent_directory>/<migrate_repo_to>`
  - [✅] Step 1.2: Copy files from current repo (excluding .git, build artifacts, dependencies)
  - [✅] Step 1.3: Initialize new Git repository with `main` branch
  - [✅] Step 1.4: Note added - Need to replace `<migrate_repo_from>` with `<migrate_repo_to>` in Phase 3
- [ ] [🔍] Phase 2: Create GitHub repository
  - [✅] Step 2.1: Create public GitHub repository using GitHub CLI
  - [✅] Step 2.2: Configure repository settings on GitHub (Pages, Actions permissions)
- [ ] [❌] Phase 2: Create public GitHub repository (GitHub UI)
- [ ] [❌] Phase 2.2: Configure repository settings (GitHub UI)
  - [ ] ⚡ Pages: Set to "GitHub Actions" (required)
  - [ ] ⚡ Actions: Set "Read and write permissions" (required)
  - [ ] 💡 General: Features, Topics (optional)
  - [ ] 💡 Branch Protection (optional - for future)
  - [ ] 💡 Variables: Skip (not used in current repo)
- [ ] [🔍] Phase 3: Review files and make necessary changes
  - [✅] Step 3.1: Replace all instances of `<migrate_repo_from>` with `<migrate_repo_to>` (updated all text files, kept MIGRATE_REPO.md unchanged)
  - [✅] Step 3.2: Cleaned up test results and build artifacts that shouldn't have been copied
  - [✅] Step 3.3: Review what will be committed and verify no sensitive files (55 files ready, sensitive files properly ignored)
  - [✅] Step 3.4: Fixed remaining references in data/core/README.md and src/test/robot/README.md, renamed database file (full_stack_testing.db → full_stack_qa.db), updated seed script and conftest.py
  - [✅] Step 3.5: Migration document copied to new repo and kept in sync
- [ ] [✅] Phase 4: First commit and push to new repo
  - [✅] Step 4.1: Files staged for commit
  - [✅] Step 4.2: Initial commit created with approved message (980 files, 164,742 insertions)
  - [✅] Step 4.3: Pushed to GitHub (origin/main)
  - [✅] Step 4.3: Repository verified on GitHub (files, README, sensitive files check)
  - [✅] Step 4.5.2: GitHub Pages verified (set to "GitHub Actions")
  - [✅] Step 4.5.3: Workflows verified (running automatically, verify-formatting.yml passed)
  - [✅] Step 4.3.1: Backend test failures fixed (database references updated, tests now passing)
- [ ] [🔍] Phase 5: Verify new repo is working correctly
  - [ ] Step 5.1: Verify CI/CD pipeline (check PR #1, all workflows passing)
  - [ ] Step 5.2: Verify database connections (backend endpoints returning 200)
  - [ ] Step 5.3: Verify GitHub Pages deployment (Allure reports accessible)
  - [ ] Step 5.4: Complete final verification checklist
- [ ] [❌] Phase 6: Disable automatic jobs in old repo (GitHub UI)
- [ ] [❌] Phase 7: Post-migration cleanup and verification

**How to use**: Replace `[❌]` with `[🔍]` when working on a phase, and `[✅]` when complete.

**Legend**:
- ⚡ **Required**: Must be done to match current repo setup
- 💡 **Optional**: Can be skipped or done later
- **GitHub UI Steps**: Steps marked with "(GitHub UI)" must be performed directly on GitHub's website

---

## Important Notes

1. **Don't disable old repo workflows until new repo is confirmed working**
2. **Keep old repo as backup** until migration is fully verified
3. **New repo won't have git history** (fresh start, saves space)
4. **All sensitive files should already be in .gitignore** (verify before first commit)
5. **Workflows will work with defaults** - No secrets/variables needed (they're optional)
6. **Match current repo exactly**: 
   - ⚡ GitHub Pages: Use "GitHub Actions" (matches current repo)
   - ⚡ Actions permissions: "Read and write" (required for Pages deployment)
   - 💡 Branch protection: Optional (can add later if current repo has it)
   - 💡 Repository variables: Skip (not used in current repo)
7. **Settings that match current repo are marked with ⚡ (Required)**
8. **Settings for future benefit are marked with 💡 (Optional)**
9. **✅ Can fix things as we go** - Repository settings, file updates, and configurations can be adjusted at any point during the migration process. Don't worry about getting everything perfect on the first try.
10. **📄 Document Sync**: This document exists in both repositories and should be kept in sync:
    - **Old repo**: `<parent_directory>/<migrate_repo_from>/docs/work/MIGRATE_REPO.md` (if applicable)
    - **New repo**: `<parent_directory>/<migrate_repo_to>/docs/guides/setup/MIGRATE_REPO.md`
    - **Sync process**: Update both documents when making changes during migration
    - **Purpose**: Ensures the migration process is fully documented in both locations
    - **Status**: ✅ Both documents currently match (649 lines each)