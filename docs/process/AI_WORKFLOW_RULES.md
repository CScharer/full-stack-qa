# AI Workflow Rules for Code Changes

**Version:** 3.0 (Updated: 2025-11-15 - Major Reorganization & New Workflows)
**Last Updated:** 2025-12-20
**Applies To:** All AI-driven code changes in this repository

**Related Documents:**
- [Pre-Pipeline Validation Checklist](./PRE_PIPELINE_VALIDATION.md) - Comprehensive validation steps before committing/pushing
- [Version Tracking](./VERSION_TRACKING.md) - Dependency version management
- Next Steps After PR #53 (archived) - Work plan and priorities

---

## 🚀 ULTRA-QUICK REFERENCE (Daily Use)

**Most common commands - copy/paste ready:**

```bash
# 0. ALWAYS create branch first (Rule #0 - CRITICAL!)
git checkout main
git pull origin main
git checkout -b feature/your-branch-name

# 1. Pre-flight check
docker-compose run --rm tests compile test-compile
docker-compose run --rm tests test -Dtest=SmokeTests -Dcheckstyle.skip=true

# 2. Format code (REQUIRED before every commit)
./scripts/quality/format-code.sh
# OR manually:
# mvn prettier:write && mvn fmt:format && mvn checkstyle:check && mvn clean compile test-compile

# 3. Verify changes persist
git status --short

# 4. After code changes
docker-compose run --rm tests compile test-compile
docker-compose run --rm tests test -Dtest=SmokeTests -Dcheckstyle.skip=true

# 5. Check if docs-only (skip build/test if true)
git status --short | grep -v -E '\.(md|log|txt|rst|adoc)$'

# 6. Commit process (ON FEATURE BRANCH!)
date "+%Y-%m-%d %H:%M:%S"  # Get timestamp
git log -1 --format=%h      # Get commit hash
# Update CHANGE.log (both updates in one!)
git add -A
git commit -m "..."
git push origin feature/your-branch-name  # Push to FEATURE BRANCH, NOT main!
# Then create PR or merge following Rule 8/15
```

**⏱️ Time Estimates:** Pre-flight (2-3 min) | Format (30-60 sec) | Smoke Tests (2-3 min) | Full Suite (10-15 min)

---

## 📑 TABLE OF CONTENTS

### MANDATORY RULES
- [Rule 0: NEVER Commit Directly to Main/Master](#rule-0-never-commit-directly-to-mainmaster--critical) 🚨
- [Rule 1: Pre-Flight Verification](#rule-1-pre-flight-verification-before-any-code-changes)
- [Rule 2: Batch Size Limits](#rule-2-batch-size-limits)
- [Rule 3: Post-Change Verification](#rule-3-post-change-verification-after-each-batch)
- [Rule 4: Commit & Push Process](#rule-4-commit--push-process-optimized---single-commit)
- [Rule 5: Error Handling](#rule-5-error-handling)
- [Rule 6: Documentation](#rule-6-documentation)
- [Rule 7: Error Recovery & Rollback](#rule-7-error-recovery--rollback-enhanced)

### CODE QUALITY & TESTING
- [Rule 8: Feature Branch Workflow](#rule-8-feature-branch-workflow--new)
- [Rule 9: Test Case Maintenance & Modernization](#rule-9-test-case-maintenance--modernization--new)
- [Rule 11: Linter Warnings Fix Process](#rule-11-linter-warnings-fix-process--new)
- [Rule 12: API Testing Workflow](#rule-12-api-testing-workflow--new)
- [Rule 13: Multi-Environment Testing Workflow](#rule-13-multi-environment-testing-workflow--new)
- [Rule 14: Performance Testing Workflow](#rule-14-performance-testing-workflow--new)
- [Rule 16: Dependency Update Workflow](#rule-16-dependency-update-workflow--new)
- [Rule 17: Code Review Checklist](#rule-17-code-review-checklist--new)

### DOCUMENTATION & STANDARDS
- [Rule 2.5: Markdown File Organization](#rule-25-markdown-file-organization-)
- [Rule 10: Recommendations Must Include Industry Standards](#rule-10-recommendations-must-include-industry-standards--new)

### SECURITY
- [Rule 2.6: Secure Credential Management](#rule-26-secure-credential-management-)

### COLLABORATION & REVIEW
- [Rule 15: Pull Request Workflow](#rule-15-pull-request-workflow--new)

### REFERENCE SECTIONS
- [📋 Verification Checklist Template](#-verification-checklist-template)
- [🎯 Exception Handling](#-exception-handling)
- [✅ Quick Reference (Detailed)](#-quick-reference)
- [🎯 Smoke Tests](#-smoke-tests)
- [🚫 Excluded Tests](#-excluded-tests---never-run-automatically)
- [🔧 Troubleshooting](#-troubleshooting)
- [💡 Common Code Patterns](#-common-code-patterns)
- [📝 Notes](#-notes)
- [📋 Version History](#-version-history)

---

## 📋 RULE INDEX (Quick Find)

| Rule | Topic | Section | Priority |
|------|-------|---------|----------|
| 0 | NEVER Commit to Main | Mandatory | 🚨 **CRITICAL** |
| 1 | Pre-Flight Verification | Mandatory | 🔴 Critical |
| 2 | Batch Size Limits | Mandatory | 🔴 Critical |
| 2.5 | Markdown File Organization | Documentation | 🟡 Medium |
| 2.6 | Secure Credential Management | Security | 🔴 Critical |
| 3 | Post-Change Verification | Mandatory | 🔴 Critical |
| 4 | Commit & Push Process | Mandatory | 🔴 Critical |
| 5 | Error Handling | Mandatory | 🔴 Critical |
| 6 | Documentation | Mandatory | 🟡 Medium |
| 7 | Rollback Plan | Mandatory | 🔴 Critical |
| 8 | Feature Branch Workflow | Branching | 🟡 Medium |
| 9 | Test Maintenance | Testing | 🟡 Medium |
| 10 | Industry Standards | Documentation | 🟢 Low |
| 11 | Linter Fixes | Code Quality | 🟡 Medium |
| 12 | API Testing Workflow | Testing | 🟡 Medium |
| 13 | Multi-Environment Testing | Testing | 🟡 Medium |
| 14 | Performance Testing | Testing | 🟢 Low |
| 15 | Pull Request Workflow | Collaboration | 🟡 Medium |
| 16 | Dependency Updates | Maintenance | 🟡 Medium |
| 17 | Code Review Checklist | Quality | 🟡 Medium |

---

## 🎯 COMMAND CHEAT SHEET

| Task | Command | Duration | When |
|------|---------|----------|------|
| **Pre-flight check** | `docker-compose run --rm tests compile test-compile` | 1-2 min | Before any work |
| **Smoke tests** | `docker-compose run --rm tests test -Dtest=SmokeTests -Dcheckstyle.skip=true` | 2-3 min | Before every batch |
| **Format code** | `./scripts/quality/format-code.sh` | 1-2 min | **REQUIRED before every commit** |
| **Checkstyle** | `docker-compose run --rm tests checkstyle:checkstyle -DskipTests` | 20-30 sec | Optional, monitor progress |
| **Verify changes** | `git status --short` | 5 sec | After any edits |
| **Check docs-only** | `git status --short | grep -v -E '\.(md|log|txt|rst|adoc)$'` | 5 sec | Before commit |
| **Full test suite** | `docker-compose run --rm tests test -Dcheckstyle.skip=true` | 10-15 min | Every 5-10 batches |
| **Docker build** | `docker-compose build tests` | 3-5 min | Every 3-5 batches |
| **Get timestamp** | `date "+%Y-%m-%d %H:%M:%S"` | 1 sec | Before commit |
| **Get commit hash** | `git log -1 --format=%h` | 1 sec | Before commit |

---

## 🚨 MANDATORY RULES - NEVER SKIP THESE STEPS

### **Rule 0.5: Protected Test Code Directory** 🚨 **CRITICAL**

> **This is a CRITICAL protection rule for legacy test code preservation.**

**🚨 ABSOLUTE PROHIBITION:**
- ❌ **NEVER** modify, delete, or refactor code in `src/test/java/` without explicit user approval
- ❌ **NEVER** assume code is unused and safe to delete
- ❌ **NEVER** make changes without verifying with the user **at least TWO times**

**✅ REQUIRED WORKFLOW:**

**1. Before ANY changes to `src/test/java/`:**
- ✅ **First Verification**: Ask user for approval before starting work
- ✅ Explain what changes you plan to make and why
- ✅ Wait for explicit approval

**2. Before committing changes to `src/test/java/`:**
- ✅ **Second Verification**: Ask user for approval again before committing
- ✅ Show what was changed
- ✅ Wait for explicit approval

**3. What is Protected:**
- **ALL files** in `src/test/java/com/cjs/qa/**/*.java`
- Legacy test code (most not currently used, but preserved for future use)
- Historical code that may be valuable for future test development

**4. Examples of Protected Operations:**
- ❌ Deleting files
- ❌ Refactoring code structure
- ❌ Removing "unused" code
- ❌ Modernizing code style
- ❌ Updating dependencies
- ❌ Renaming classes/packages
- ❌ Consolidating duplicate code

**5. When Changes Are Needed:**
- ✅ Fix compilation errors (with approval)
- ✅ Add new test files (with approval)
- ✅ Update test data (with approval)
- ✅ Fix critical bugs (with approval)

**Why This Rule Exists:**
- ✅ **Preservation**: Legacy code is preserved for potential future use
- ✅ **Safety**: Prevents accidental loss of valuable historical code
- ✅ **Control**: User maintains full control over legacy codebase
- ✅ **Future Value**: Code may be incorporated into test suites later

**See Also**: `src/test/java/README.md` for detailed protection rules

---

### **Rule 0: NEVER Commit Directly to Main/Master** 🚨 **CRITICAL**

> **This is the #1 rule - ALWAYS create a feature branch first!**

**🚨 ABSOLUTE PROHIBITION:**
- ❌ **NEVER commit directly to main/master branch**
- ❌ **NEVER push changes to main/master without a branch**
- ❌ **NEVER make changes on main/master branch**

**✅ REQUIRED WORKFLOW:**

**1. ALWAYS Create Branch First:**
```bash
# Before making ANY changes:
git checkout main
git pull origin main          # Ensure main is up to date
git checkout -b feature/descriptive-name
# Examples:
#   feature/ai-workflow-rules-v3
#   feature/add-api-testing-workflow
#   feature/fix-linter-warnings
#   docs/update-workflow-rules
#   fix/compilation-error
```

**2. Make Changes on Feature Branch:**
```bash
# All your work happens on the feature branch
# Edit files, make changes, commit to feature branch
git add .
git commit -m "feat: add new workflow rules"
git push -u origin feature/descriptive-name
```

**3. Merge to Main:**
- Create Pull Request (see [Rule 15: Pull Request Workflow](#rule-15-pull-request-workflow--new))
- OR merge locally with `--no-ff` (see [Rule 8: Feature Branch Workflow](#rule-8-feature-branch-workflow--new))
- **NEVER** push directly to main

#### **Why This Rule Exists:**
- ✅ **Safety**: Main branch always stable and deployable
- ✅ **Review**: Changes can be reviewed before merge
- ✅ **Testing**: Changes can be tested on branch before affecting main
- ✅ **Rollback**: Easy to abandon branch if needed
- ✅ **Collaboration**: Others can review/test branch independently
- ✅ **History**: Clear feature development timeline

#### **Exceptions (EMERGENCY ONLY):**
**Emergency hotfixes** require immediate fix to main:
```bash
# ONLY for critical production issues:
git checkout -b hotfix/critical-issue-description
# Fix issue
git commit -m "hotfix: Fix critical production issue"
git checkout main
git merge --no-ff hotfix/critical-issue-description
git push origin main
# Cleanup
git branch -d hotfix/critical-issue-description
```

**Even hotfixes use a branch first!** Never commit directly to main.

#### **Verification:**
```bash
# Before making changes, verify you're NOT on main:
git branch
# Should show: * feature/your-branch-name
# NOT: * main

# If you see: * main
# STOP immediately and create a branch!
```

**Remember:** This is rule #0 for a reason - it's the foundation of safe development! 🚨

---

### **Rule 1: Pre-Flight Verification (BEFORE any code changes)**
Before making ANY code changes, you MUST:
1. ✅ Check git status - ensure clean working tree
2. ✅ Verify Docker build: `docker-compose run --rm tests compile -DskipTests -Dcheckstyle.skip=true`
3. ✅ Run smoke tests: `docker-compose run --rm tests test -Dtest=SmokeTests -Dcheckstyle.skip=true`
4. ✅ Verify smoke tests pass (should see "Tests run: X, Failures: 0")

**STATUS:** Must see "BUILD SUCCESS" and smoke tests passing before proceeding.

**Note:** Full test suite will run on GitHub Actions. Smoke tests provide fast verification before starting work.

---

### **Rule 2: Batch Size Limits**
- **Maximum files per batch:** 10 files
- **Maximum fixes per batch:** 25 fixes
- **Reason:** Smaller batches = easier rollback if issues occur

---

### **Rule 2.5: Markdown File Organization** 📁

**ALL .md FILES MUST BE IN docs/ FOLDER STRUCTURE**

#### **Mandatory Placement:**
- ✅ **ALL** .md files must be created in the appropriate `docs/` subfolder
- ❌ **NEVER** create .md files in `.github/`, project root, or other locations
- ✅ Organize by purpose and lifecycle

#### **Folder Selection Guide:**

**Analysis & Recommendations** → `docs/work/` (date-prefixed planning documents)
- Code reviews
- Project audits
- Improvement recommendations
- Technical assessments

**How-To Guides** → `docs/guides/[infrastructure|testing|setup|troubleshooting]/`
- Setup instructions
- Usage guides
- Best practices
- Troubleshooting steps

**Team Processes** → `docs/process/`
- Team standards
- Workflow rules
- Code of conduct
- Contributing guidelines

**Architecture Decisions** → `docs/architecture/decisions/`
- Significant design decisions
- Technology choices
- Pattern selections
- Use ADR format

**Issue Templates** → `docs/issues/open/`
- Planned improvements
- Known bugs to fix
- Technical debt items
- Missing implementations

**Historical Work** → `docs/archive/YYYY-MM/`
- Completed work summaries
- Superseded documents
- Historical milestones

**Project Overview** → `docs/` (root only for major files)
- README.md
- NAVIGATION.md
- CHANGE.log
- REORGANIZATION_COMPLETE.md (transitional)

#### **Before Creating ANY .md File:**
1. Determine the file's purpose
2. Select appropriate folder from guide above
3. Create file in correct location
4. **Add metadata block at top** (MANDATORY - see template below)
5. Update NAVIGATION.md if adding new category
6. Add README.md to new folders

#### **Mandatory Metadata Block:**

**ALL new .md files MUST start with this metadata block:**

```markdown
---
**Type**: [Guide|Analysis|Process|ADR|Issue|Archive]
**Purpose**: [One-line description of what this document does]
**Created**: YYYY-MM-DD
**Last Updated**: YYYY-MM-DD
**Maintained By**: [CJS QA Team|AI Code Analysis System|Your Name]
**Status**: [Active|Draft|Archived|Superseded|Deprecated]
---

# Document Title

Content starts here...
```

**Required Fields**:
- **Type**: Document category (Guide, Analysis, Process, ADR, Issue, Archive)
- **Purpose**: One-line description
- **Created**: Initial creation date (YYYY-MM-DD)
- **Last Updated**: Most recent change date (update when editing)
- **Maintained By**: Who keeps this current
- **Status**: Current state (Active, Draft, Archived, Superseded, Deprecated)

**Optional Fields** (add if relevant):
- **Version**: For versioned docs (e.g., v2.4)
- **Related To**: Links to related documents
- **Supersedes**: If replacing an older document
- **Superseded By**: If this doc is outdated

**When to Update**:
- **Created**: Set once, never change
- **Last Updated**: Update EVERY time you edit the file
- **Status**: Update if document state changes
- **Version**: Increment based on change significance

**Example**:
```markdown
---
**Type**: Guide
**Purpose**: Complete setup guide for Docker and Selenium Grid infrastructure
**Created**: 2025-11-08
**Last Updated**: 2025-11-12
**Maintained By**: CJS QA Team
**Status**: Active
---

# Docker & Selenium Grid Setup Guide
```

#### **Common Mistakes to Avoid:**
- ❌ Creating issue templates in `.github/` (use `docs/issues/open/`)
- ❌ Creating guides in project root (use `docs/guides/`)
- ❌ Creating analysis in project root (use `docs/work/` with date-prefixed filenames)
- ❌ Forgetting to update NAVIGATION.md

#### **Exceptions - .md Files Outside docs/:**

**ONLY these .md files are allowed outside docs/ folder:**

**GitHub UI Templates** (Required by GitHub):
- ✅ `.github/ISSUE_TEMPLATE/bug_report.md`
- ✅ `.github/ISSUE_TEMPLATE/feature_request.md`
- ✅ `.github/ISSUE_TEMPLATE/test_failure.md`
- ✅ `.github/pull_request_template.md`

**Reason**: GitHub automatically recognizes these specific locations for its UI. Moving them would break the template functionality.

**Project & Configuration READMEs** (Feature-Local Documentation):
- ✅ `README.md` (project root - GitHub landing page)
- ✅ `xml/README.md` (XML configuration guide)
- ✅ `config/README.md` (environment setup guide, includes XML configuration)
- ✅ `scripts/README.md` (script usage guide)

**Reason**: These document specific features/directories and should live with those features.

**Principle**: Documentation can be **feature-local** when it only pertains to that specific component. The `docs/` folder is for **framework-level** and **cross-cutting** documentation.

**All Other .md Files** → Must be in `docs/` structure ❌

#### **Verification:**
```bash
# Check for UNEXPECTED .md files outside docs/
find . -name "*.md" \
  -not -path "./docs/*" \
  -not -path "./.github/ISSUE_TEMPLATE/*" \
  -not -path "./.github/pull_request_template.md" \
  -not -path "./README.md" \
  -not -path "./xml/README.md" \
  -not -path "./config/README.md" \
  -not -path "./scripts/README.md" \
  -not -path "./node_modules/*" \
  -not -path "./target/*"

# Should show NO output (all .md files in correct locations)
# If any files show up → they need to be moved to docs/
```

**Expected .md file locations**:
```bash
# Framework documentation (docs/):
docs/**/*.md                              ✅ Primary location

# GitHub UI templates:
.github/ISSUE_TEMPLATE/*.md               ✅ Exception (GitHub UI)
.github/pull_request_template.md          ✅ Exception (GitHub UI)

# Feature-local documentation:
README.md                                  ✅ Exception (Project root)
xml/README.md                              ✅ Exception (Config guide)
config/README.md                           ✅ Exception (Config guide)
scripts/README.md                          ✅ Exception (Script guide)

# Everything else:
*.md anywhere else                         ❌ Move to docs/
```

#### **Auto-Fix Process:**
If you create a .md file in the wrong location:
1. STOP immediately when you realize
2. Move file to appropriate docs/ subfolder
3. Update any references/links
4. Update NAVIGATION.md if needed
5. Commit the correction

**Remember**: Well-organized documentation is as important as well-organized code! 📚

---

### **Rule 2.6: Secure Credential Management** 🔐

> **Related:** See [Rule 4: Commit & Push Process](#rule-4-commit--push-process-optimized---single-commit) for how this affects commit workflow.

**🚨 CRITICAL SECURITY RULE - NEVER BYPASS WITHOUT EXPLICIT USER APPROVAL**

#### **Security Standards:**
- ❌ **NEVER add credentials to source code**
- ❌ **NEVER commit code with credentials**
- ❌ **NEVER use `--no-verify` to bypass pre-commit security checks WITHOUT user approval**

#### **What Must Be Secured:**
- Any password in plain text in source code
- API keys, tokens, secrets in code
- Database credentials in configuration files
- Service account passwords
- Test account passwords (unless explicitly marked as dummy/fake)

#### **Pre-Commit Hook Detection:**
The pre-commit hook checks for credentials using pattern matching:
```bash
# The hook will FAIL if it finds patterns like:
password = "actual_value"
password: "actual_value"
pwd = "actual_value"
secret = "actual_value"
token = "actual_value"
```

#### **Required Process:**

**1. During Development:**
- ✅ Use Google Cloud Secret Manager for ALL credentials
- ✅ Use `EPasswords` enum for password retrieval
- ✅ Use environment variables for test credentials
- ✅ Use `.xml.template` files with placeholders

**2. Before Commit:**
```bash
# Check for credentials (pre-commit hook will run automatically)
git add -A
git commit -m "..."

# If pre-commit hook FAILS with "Possible credential detected!":
```

**3. If Pre-Commit Hook Fails:**

**Option A: Fix the Issue (REQUIRED if YOU added the password)**
```bash
# 1. Review the output - which files have passwords?
# 2. If YOU added these passwords in THIS batch:
#    - STOP immediately
#    - Remove the credentials
#    - Use Google Cloud Secret Manager instead
#    - Update code to retrieve from EPasswords enum
#    - Re-run commit
```

**Option B: Request User Approval (ONLY in exceptional circumstances)**
```bash
# If credentials are detected in files not modified by you:
# 1. STOP and notify the user
# 2. Show them the pre-commit hook output
# 3. Explain:
#    - "Pre-commit hook detected potential credentials in files"
#    - "These are NOT new changes in this commit"
#    - List the files detected
# 4. Ask: "May I use --no-verify to bypass the check for this commit?"
# 5. WAIT for explicit user approval
# 6. Only proceed with --no-verify if user approves
```

**4. User Approval Required:**
```
AI: "⚠️  Pre-commit hook detected potential credentials in these files:
     - src/test/java/com/cjs/qa/pluralsight/pages/LoginPage.java
     - src/test/java/com/cjs/qa/gt/Notes.txt

     These files were not modified in this commit.

     May I use --no-verify to bypass the check for this commit?"

User: "Yes, approved" OR "No, let's fix them first"
```

#### **When --no-verify is ACCEPTABLE:**
✅ Only when ALL of these conditions are met:
1. User has given EXPLICIT approval
2. The credentials are in files not modified by you
3. The credentials are NOT related to your current changes
4. You document the bypass in the commit message

#### **When --no-verify is FORBIDDEN:**
❌ NEVER use --no-verify if:
1. YOU added credentials to the code
2. User has not approved
3. The credentials are related to your changes
4. It's a new security concern

#### **Documentation Required:**
If user approves --no-verify, you MUST:
1. Add note to commit message:
   ```
   NOTE: Used --no-verify because pre-commit hook flagged potential
   credentials in unmodified files (not new changes).
   User approval obtained.
   ```
2. Add entry to CHANGE.log explaining the situation

#### **Zero Tolerance Policy:**
- Any attempt to add credentials to code = IMMEDIATE STOP
- Any bypass of password checks without approval = VIOLATION
- Security is NON-NEGOTIABLE

#### **Correct Approach - Example:**
```java
// ❌ WRONG - Credential in code
String password = "MySecretP@ssw0rd";

// ✅ CORRECT - Retrieved from Google Cloud Secret Manager
String password = EPasswords.MY_SERVICE.getValue();

// ✅ CORRECT - Environment variable
String password = System.getenv("MY_SERVICE_PASSWORD");

// ✅ CORRECT - Configuration file (not committed to git)
String password = config.getPassword("my-service");
```

**Remember:** This project maintains the highest security standards with all credentials managed through Google Cloud Secret Manager. Security is a top priority! 🔐

---

### **Rule 3: Post-Change Verification (AFTER each batch)**

> **Related:** See [Rule 4: Commit & Push Process](#rule-4-commit--push-process-optimized---single-commit) for what to do after verification passes.

**FIRST: Check if documentation-only change:**
```bash
git status --short | grep -v -E '\.(md|log|txt|rst|adoc)$'
```
- **If NO output:** Only doc files changed → Skip to Rule 4 (no build/test needed!) ✅
- **If output shows .java/.xml/etc:** Code/config changed → Run full verification below

**Documentation-Only Files (skip build/test):**
- `.md` files (README, CHANGE.log, rules, etc.)
- `.log` files
- `.txt` files
- `.rst`, `.adoc` files (other documentation formats)
- **Verification needed:** Git commands only (status, commit, push)

---

After making **code changes**, you MUST verify in this order:

#### **Step 0: Code Formatting (REQUIRED - MUST RUN BEFORE EVERY COMMIT)**
```bash
# ⚠️ REQUIRED: Run the automated formatting script before every commit
./scripts/quality/format-code.sh

# This script automatically:
# 1. Formats code and sorts imports (Prettier)
# 2. Fixes line length issues (Google Java Format)
# 3. Verifies code quality (Checkstyle)
# 4. Ensures compilation works

# OR manually (if not using the script):
# mvn prettier:write && mvn fmt:format && mvn checkstyle:check && mvn clean compile test-compile
```

**⚠️ MANDATORY**: This formatting step is **REQUIRED** before every commit to maintain code quality and ensure zero checkstyle violations.
**Purpose:**
- Auto-fixes redundant/unused imports
- Ensures consistent code formatting
- Validates against Checkstyle rules (optional)
- Should run BEFORE compilation to catch any formatting-introduced issues

**Checkstyle Output:**
- Shows violation count: "You have X Checkstyle violations"
- BUILD SUCCESS even with violations (failOnViolation=false)
- Use this to monitor progress, not as a gate

**Duration:** ~30-60 seconds (format), ~20-30 seconds (checkstyle)

#### **Step 0b: Verify File Changes Persist (CRITICAL!)**
```bash
# After making ANY code changes, verify they actually saved:
git status --short

# You should see:
#  M src/test/java/com/cjs/qa/some/File.java
#  M src/test/java/com/cjs/qa/other/File.java
# ... etc.

# ⚠️  If you DON'T see modified files:
# - Changes didn't persist (volume mount issue or editor problem)
# - DO NOT proceed - investigate why changes didn't save
# - Re-apply changes and verify again
```
**Required:** Modified files appear in `git status`

**CRITICAL:** If changes don't appear:
- Changes are NOT saved to filesystem
- Compilation will use old code
- Pushing will NOT include your fixes
- **MUST** re-apply and verify before proceeding

**Example Workflow:**
1. Make 10 file changes
2. Run `git status --short`
3. Verify 10 files show as modified
4. If count doesn't match → investigate and fix
5. Only proceed when all expected files are modified

**Duration:** ~5 seconds

#### **Step 1: Compilation Verification (MUST MATCH PIPELINE!)**
```bash
# ✅ REQUIRED COMMAND (matches pipeline exactly):
docker-compose run --rm tests compile test-compile

# Alternative with clean (if needed):
docker-compose run --rm tests clean compile test-compile

# ❌ WRONG: docker-compose run --rm tests compile -DskipTests
#    (This skips test-compile phase! Will miss test compilation errors!)
```
**Required:** BUILD SUCCESS

**CRITICAL:** Pipeline runs `./mvnw clean compile test-compile`
- **You MUST run:** `compile test-compile` (NOT just `compile`)
- **Why:** `compile` only compiles src/main/java
- **test-compile** compiles src/test/java (where most code is!)
- Missing `test-compile` = test compilation errors reach pipeline!

#### **Step 2: Smoke Tests (Fast)**
```bash
docker-compose run --rm tests test -Dtest=SmokeTests -Dcheckstyle.skip=true
```
**Required:** All smoke tests pass (Tests run: X, Failures: 0)
**Duration:** ~2-3 minutes (much faster than full suite)
**Purpose:** Catch compilation and basic runtime issues before pushing
**Alternative (if local Maven works):** `mvn test -Dtest=SmokeTests -Dcheckstyle.skip=true`

#### **Step 3: Docker Build Test (Every 3-5 batches)**
```bash
docker-compose build tests
```
**Required:** Image builds successfully
**Frequency:** Every 3-5 batches (not every single batch)

#### **Step 4: Full Test Suite (Checkpoints)**
```bash
docker-compose run --rm tests test -Dcheckstyle.skip=true
```
**Required:** Run every 5-10 batches as checkpoint
**Frequency:** Periodic checkpoints, not every batch
**Duration:** ~10-15 minutes

**❌ CRITICAL:** If ANY verification fails, you MUST:
1. DO NOT push the changes
2. Identify the root cause
3. Fix the issue
4. Re-run ALL verification steps
5. Proceed only after all verifications pass

---

### **Rule 4: Commit & Push Process (OPTIMIZED - Single Commit)**

⚠️ **REQUIRED BEFORE COMMIT**: Always run `./scripts/quality/format-code.sh` before committing to ensure code is properly formatted, imports are sorted, and checkstyle violations are minimized.

> **CRITICAL:** See [Rule 0: NEVER Commit Directly to Main/Master](#rule-0-never-commit-directly-to-mainmaster--critical) - ALWAYS use feature branch!

> **🚨 AUTHORIZATION REQUIRED:** You MUST get explicit approval for **EACH** commit, push, or PR action. Even if you were previously told to commit/push/create PR, you **CANNOT** do it again without fresh approval after local review. **DO NOT** assume previous authorization applies to new actions.

Only after ALL verifications pass **AND you have received explicit approval**:

**🚨 REMINDER:** You MUST be on a feature branch! Verify:
```bash
git branch
# Should show: * feature/your-branch-name
# NOT: * main
```

1. ✅ **Update CHANGE.log** (MANDATORY BEFORE COMMIT)
   - **FIRST:** Get actual current timestamp: `date "+%Y-%m-%d %H:%M:%S"` (CST timezone)
   - **SECOND:** Get last commit hash: `git log -1 --format=%h`
   - Update CHANGE.log with BOTH:
     a) **Update previous entry:** Find last `[PENDING]` and replace with actual commit hash
     b) **Add new entry** at top of file with:
        - **Timestamp:** `[YYYY-MM-DD HH:MM:SS CST]` - **MUST use actual system time from date command**
        - Commit hash: `[PENDING]` (will be updated in next batch)
        - Summary title
        - Overview section with high-level summary
        - **AI Tool & Token Status:** `[Tool: Cursor|GitHub Copilot|Other] - Tokens Used: X / Total: Y (Z remaining, A% used, B% remaining)`
        - Detailed changes, files modified, verification results
        - Impact summary and next steps
   - Format: Follow existing CHANGE.log structure
   - Content: Include all changes since last entry
   - **Token tracking:** Always include current token usage with percentages for session visibility
   - **CRITICAL:** Never guess timestamps - always use actual system time!
   - **BENEFIT:** Updates previous hash + adds new entry = 1 commit instead of 2!

2. ✅ Stage changes: `git add -A` (including docs/CHANGE.log with both updates)
3. ✅ Commit with descriptive message following established format
4. ✅ Push to feature branch: `git push origin feature/your-branch-name` ⚠️ **NOT main!**
5. ✅ Verify push succeeded
6. ✅ Create Pull Request (see [Rule 15: Pull Request Workflow](#rule-15-pull-request-workflow--new)) OR merge following [Rule 8: Feature Branch Workflow](#rule-8-feature-branch-workflow--new)
7. ✅ Monitor GitHub Actions status (on PR or branch)
8. ✅ If GitHub Actions fails, STOP and fix before merging

**Note:** Last entry of session will have `[PENDING]` - gets updated in next session or can be updated at end of session if desired.

**GitHub Actions Monitoring:**
- Check https://github.com/CScharer/full-stack-qa/actions after push
- Wait for build to complete before next batch (or proceed if user approves)
- If any job fails, investigate and fix before continuing

---

### **Rule 5: Error Handling**
If you encounter ANY error:

1. **Compilation Error:**
   - DO NOT push
   - Read the full error message
   - Identify the affected file(s)
   - Fix the error
   - Re-run compilation
   - Proceed only after BUILD SUCCESS

2. **Test Failure:**
   - Identify if it's a known flaky test (testWindowManagement, ResponsiveDesignTests)
   - If NEW failure: DO NOT push
   - If known flaky: Document and proceed (if 65+/66 pass)

3. **Docker Build Failure:**
   - DO NOT push
   - Read the full error log
   - Fix the issue
   - Re-run Docker build
   - Proceed only after successful build

4. **Network/Transient Errors:**
   - Retry the command once
   - If persists, document and notify user
   - DO NOT push code changes if verification incomplete

---

### **Rule 6: Documentation**
- ✅ Update CHANGE.log at END of session (or after major milestones)
- ✅ Use descriptive commit messages
- ✅ Document patterns fixed, files modified, verification results
- ✅ Include commit hashes for traceability

---

### **Rule 7: Error Recovery & Rollback** (ENHANCED)

> **Related:** See [Rule 5: Error Handling](#rule-5-error-handling) for error classification. See [Rule 4: Commit & Push Process](#rule-4-commit--push-process-optimized---single-commit) for normal commit workflow.

**Principle:** Different error types require different recovery strategies. Classify errors correctly to apply appropriate fixes.

#### **Error Classification:**

**1. Recoverable Errors (Fix and Retry):**
- ✅ Test failures (non-flaky)
- ✅ Compilation errors
- ✅ Linter warnings
- ✅ Checkstyle violations
- **Action:** Fix the issue and retry verification

**2. Non-Recoverable Errors (STOP and Notify):**
- 🚨 Security concerns (credentials in code, exposed secrets)
- 🚨 Data corruption risks
- 🚨 Breaking changes to critical functionality
- 🚨 Production environment failures
- **Action:** STOP immediately, notify user, wait for approval

**3. Transient Errors (Retry with Backoff):**
- ⏳ Network errors (Docker downloads, Maven dependencies)
- ⏳ Docker build timeouts
- ⏳ CI/CD pipeline transient failures
- **Action:** Retry once, if persists proceed with caution

#### **Rollback Procedures:**

**Scenario 1: Broken Build on Main**
```bash
# 1. Identify breaking commit
git log --oneline -5

# 2. Notify user immediately
# "⚠️ Build broken by commit [hash]. Proposed rollback options:
#  A) Revert commit: git revert <commit-hash>
#  B) Fix forward: Create hotfix branch"

# 3. Wait for user approval before proceeding

# Option A: Revert (recommended if fix is complex)
git revert <commit-hash>
git push origin main

# Option B: Fix Forward (recommended if fix is simple)
git checkout -b hotfix/fix-build-issue
# Make fixes
git commit -m "hotfix: Fix build issue"
git checkout main
git merge --no-ff hotfix/fix-build-issue
git push origin main
```

**Scenario 2: Failed Tests**
```bash
# 1. Check if known flaky tests
# Known flaky: testWindowManagement, ResponsiveDesignTests.tearDown

# 2. If NEW failure:
#   - DO NOT push
#   - Investigate root cause
#   - Fix the issue
#   - Re-run ALL verification steps

# 3. If known flaky:
#   - Document and proceed (if 65+/66 pass)
#   - Create GitHub issue to track flaky test
#   - Update CHANGE.log with note about flaky test
```

**Scenario 3: Docker Issues**
```bash
# 1. Retry command once
docker-compose run --rm tests compile test-compile

# 2. If persists:
#   - Document: "Docker network issue, tests deferred to CI"
#   - Note in commit message
#   - Proceed with compilation-only verification
#   - Inform user: "Docker network issue. Tests will run in CI."
```

**Scenario 4: Pre-Commit Hook Failures**
```bash
# 1. If password detection fails:
#   - Review output carefully
#   - If YOU added passwords: STOP and fix them
#   - If passwords already exist: Request user approval for --no-verify

# 2. See Rule 2.6 for detailed password handling
```

#### **Communication Process:**

**When Errors Occur:**
1. ✅ **Notify user immediately** - Don't hide errors
2. ✅ **Provide context** - What happened, why, when
3. ✅ **Propose solution** - Offer concrete options
4. ✅ **Wait for approval** - Don't proceed without user consent
5. ✅ **Document decisions** - Update CHANGE.log with error and resolution

**Example Notification:**
```
AI: "⚠️ Error encountered during compilation:

ERROR: Compilation failed in File.java:42
REASON: Type mismatch - String cannot be converted to Integer
IMPACT: Build broken, cannot proceed
SOLUTION OPTIONS:
  A) Fix type mismatch (recommended, ~2 min)
  B) Revert commit (if fix is complex)

Which option would you prefer?"
```

#### **Rollback Best Practices:**
- ✅ **Always identify root cause** before rolling back
- ✅ **Consider fix forward** vs revert (fix forward often better)
- ✅ **Test rollback** in lower environment if possible
- ✅ **Document rollback** in CHANGE.log
- ✅ **Communicate changes** to team if production affected

#### **Prevention Strategies:**
- ✅ **Run pre-flight checks** (Rule 1) before changes
- ✅ **Use feature branches** (Rule 8) for risky changes
- ✅ **Run smoke tests** after each batch
- ✅ **Verify changes persist** (Rule 3, Step 0b)
- ✅ **Follow batch size limits** (Rule 2)

**Remember:** Errors are learning opportunities. Document them to prevent recurrence! 🔄

---

### **Rule 16: Dependency Update Workflow** 📦 **NEW**

> **Related:** See [Rule 3: Post-Change Verification](#rule-3-post-change-verification-after-each-batch) for verification steps. See [Rule 8: Feature Branch Workflow](#rule-8-feature-branch-workflow--new) for branch workflow.

**Principle:** Dependency updates require careful validation to ensure compatibility and prevent breaking changes.

#### **When to Update Dependencies:**

**Immediate (Security Vulnerabilities):**
- 🚨 Critical security vulnerabilities (CVSS 9.0+)
- 🚨 Known exploits in dependencies
- 🚨 End-of-life (EOL) dependencies with security issues

**Planned (Major Releases):**
- ✅ Major version releases (review release notes)
- ✅ User requests specific dependency update
- ✅ Framework upgrades (Selenium, TestNG, etc.)
- ✅ Infrastructure dependencies (Docker, Maven)

**Deferred:**
- ⏭️ Minor/patch updates (unless security-related)
- ⏭️ Breaking changes in upstream
- ⏭️ Dependencies with known issues

#### **Dependency Update Process:**

**1. Identify Dependencies to Update:**
```bash
# Check for outdated dependencies
./mvnw versions:display-dependency-updates

# Check for outdated plugins
./mvnw versions:display-plugin-updates

# Check for security vulnerabilities
./mvnw dependency-check:check
```

**2. Review Release Notes:**
- Check dependency release notes for breaking changes
- Review migration guides
- Check compatibility with current Java version
- Review community feedback

**3. Update `pom.xml`:**
```xml
<!-- Update dependency version -->
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>4.26.0</version>  <!-- Update version -->
</dependency>
```

**4. Verification Steps:**
```bash
# Step 1: Clean build
./mvnw clean

# Step 2: Compile
./mvnw compile test-compile

# Step 3: Run smoke tests
./mvnw test -Dtest=SmokeTests -Dcheckstyle.skip=true

# Step 4: Check for deprecated API usage
# Review linter warnings for deprecated methods

# Step 5: Full test suite (if major version)
./mvnw test -Dcheckstyle.skip=true
```

**5. Handle Breaking Changes:**
```bash
# If breaking changes detected:
# 1. Review migration guide
# 2. Update code to use new API
# 3. Fix deprecated method calls
# 4. Update test code if needed
# 5. Re-run all verification steps
```

#### **Rollback Plan:**

**If Update Breaks Things:**
```bash
# 1. Revert pom.xml changes
git checkout pom.xml

# 2. Clean and rebuild
./mvnw clean compile test-compile

# 3. Verify build works with old version
./mvnw test -Dtest=SmokeTests

# 4. Document issue
# - Create GitHub issue
# - Update CHANGE.log with attempted update and failure reason
# - Note compatibility issues for future reference
```

#### **Documentation Requirements:**

**Update CHANGE.log:**
```markdown
## Dependency Updates

### Updated Dependencies
- Selenium: 4.25.0 → 4.26.0
- TestNG: 7.8.0 → 7.9.0

### Breaking Changes Addressed
- Deprecated `URL(String)` constructor → `URI.create(url).toURL()`
- Updated WebDriver API calls

### Testing Performed
- ✅ All tests pass
- ✅ No new linter warnings
- ✅ No breaking changes detected
```

#### **Common Dependency Updates:**

**Selenium Updates:**
- Check WebDriver API changes
- Update deprecated methods
- Review browser compatibility
- Test with multiple browsers

**TestNG Updates:**
- Check test execution changes
- Review annotation changes
- Verify test report format

**Maven Plugin Updates:**
- Check plugin configuration changes
- Verify build behavior
- Review plugin documentation

#### **Best Practices:**
- ✅ **Use feature branches** for major dependency updates
- ✅ **Test thoroughly** before merging
- ✅ **Update incrementally** (don't update everything at once)
- ✅ **Document breaking changes** in CHANGE.log
- ✅ **Monitor for issues** after deployment

**Remember:** Dependency updates can introduce breaking changes - test thoroughly! 📦

---

### **Rule 17: Code Review Checklist** ✅ **NEW**

> **Related:** See [Rule 3: Post-Change Verification](#rule-3-post-change-verification-after-each-batch) for verification steps. See [Rule 4: Commit & Push Process](#rule-4-commit--push-process-optimized---single-commit) for commit workflow.

**Principle:** Self-review before committing ensures code quality and reduces issues before they reach main.

#### **Pre-Commit Review Checklist:**

**1. Code Quality:**
- ✅ No linter warnings introduced
- ✅ Follows project coding standards
- ✅ No credentials in code (see [Rule 2.6](#rule-26-secure-credential-management-))
- ✅ Proper error handling
- ✅ No TODO comments left (unless documented)
- ✅ Code is readable and maintainable

**2. Testing:**
- ✅ All tests pass (smoke tests minimum)
- ✅ No test code broken
- ✅ New tests added if needed (for new features)
- ✅ Test coverage maintained/improved
- ✅ No flaky tests introduced

**3. Documentation:**
- ✅ CHANGE.log updated (see [Rule 4](#rule-4-commit--push-process-optimized---single-commit))
- ✅ Code comments where needed (complex logic)
- ✅ README updated if public API changed
- ✅ JavaDoc added for public methods (if applicable)
- ✅ Related documentation updated

**4. Security:**
- ✅ No credentials in code
- ✅ Google Cloud Secret Manager used for all credentials (see [Rule 2.6](#rule-26-secure-credential-management-))
- ✅ No sensitive data logged
- ✅ Input validation where needed
- ✅ Security best practices followed

**5. Git:**
- ✅ Meaningful commit messages (see commit message format)
- ✅ No unnecessary files staged
- ✅ Branch name follows convention (see [Rule 8](#rule-8-feature-branch-workflow--new))
- ✅ Related changes grouped in same commit
- ✅ Unrelated changes in separate commits

**6. Build & Verification:**
- ✅ Compilation successful (see [Rule 3](#rule-3-post-change-verification-after-each-batch))
- ✅ No new Checkstyle violations
- ✅ Docker build successful (if applicable)
- ✅ No deprecated API warnings (unless intentional)
- ✅ All verification steps pass

#### **Commit Message Format:**

```markdown
<type>: <subject>

<optional body>

<optional footer>

Types:
- fix: Bug fix
- feat: New feature
- docs: Documentation only
- refactor: Code refactoring
- test: Test changes
- chore: Maintenance tasks

Examples:
fix: resolve deprecated URL constructor warnings

- Replaced 20+ instances of deprecated URL(String) constructor
- Updated imports to include java.net.URI
- Progress: 68 warnings remaining (70% reduction from 229)

feat: add API testing workflow documentation

- Added Rule 12: API Testing Workflow
- Documented REST Assured test execution
- Includes troubleshooting guide
```

#### **Quick Self-Review Checklist:**

**Before Every Commit:**
```
[ ] Code compiles
[ ] Tests pass (smoke tests minimum)
[ ] No linter warnings
[ ] No credentials in code
[ ] CHANGE.log updated
[ ] Commit message descriptive
[ ] Branch name correct (if applicable)
```

#### **Code Review Best Practices:**

**For Your Own Code:**
- ✅ Review changes in context (not just individual files)
- ✅ Check for unintended side effects
- ✅ Verify error handling is complete
- ✅ Ensure tests cover edge cases
- ✅ Review performance implications

**For Team Reviews:**
- ✅ Be constructive and respectful
- ✅ Explain "why" not just "what"
- ✅ Suggest improvements, don't just criticize
- ✅ Acknowledge good practices
- ✅ Focus on code, not person

#### **Common Issues to Watch For:**

**Security:**
- ❌ Credentials in source code
- ❌ Sensitive data in logs
- ❌ Missing input validation
- ❌ SQL injection risks

**Code Quality:**
- ❌ Duplicate code
- ❌ Magic numbers/strings
- ❌ Unused imports/variables
- ❌ Complex nested conditions

**Testing:**
- ❌ Missing test coverage
- ❌ Flaky tests
- ❌ Tests that don't verify behavior
- ❌ Hardcoded test data (use data-driven)

**Documentation:**
- ❌ Missing JavaDoc for public APIs
- ❌ Unclear variable/method names
- ❌ Missing README updates
- ❌ Outdated comments

**Remember:** Good code review prevents issues from reaching main! ✅

---

### **Rule 8: Feature Branch Workflow** 🌿 **NEW**

> **Related:** See [Rule 4: Commit & Push Process](#rule-4-commit--push-process-optimized---single-commit) for commit workflow. See [Rule 11: Linter Warnings Fix Process](#rule-11-linter-warnings-fix-process--new) for code quality checks.

**When to Use Feature Branches:**

Use feature branches for:
- ✅ Risky changes (dependency updates, refactoring, architecture changes)
- ✅ Multi-step tasks that span multiple commits
- ✅ Changes requiring validation before merge
- ✅ Pipeline/workflow modifications
- ✅ Changes that might need rollback

**Skip feature branches for:**
- ⏭️ Documentation-only updates
- ⏭️ Quick fixes (single file, low risk)
- ⏭️ Hotfixes that need immediate deployment

**Mandatory Process:**

**1. Create Feature Branch:**
```bash
git checkout -b feature/descriptive-name
# Examples:
#   feature/multi-environment-pipeline
#   feature/remove-dangerous-dependencies
#   feature/standardize-logging
#   feature/add-data-driven-testing
```

**Branch Naming Convention:**
- `feature/` - New features or enhancements
- `fix/` - Bug fixes
- `refactor/` - Code refactoring
- `docs/` - Major documentation changes
- `test/` - Test-related changes

**2. Development on Feature Branch:**
- ✅ Make incremental commits (one logical change per commit)
- ✅ Test after each significant change
- ✅ Push to origin regularly (`git push -u origin feature/name`)
- ✅ Follow all other rules (compilation, testing, verification)
- ✅ Update documentation as you go

**3. Validation Before Merge:**
- ✅ **Compilation**: `./mvnw clean compile test-compile` must succeed
- ✅ **Tests**: Run relevant tests (smoke tests minimum)
- ✅ **Docker** (if applicable): Test with Grid running
- ✅ **Documentation**: Update CHANGE.log, related docs
- ✅ **User Review**: Get user confirmation flow is correct

**4. Merge to Main:**
```bash
# On main branch:
git checkout main
git merge --no-ff feature/name -m "Merge feature/name: Description

[Detailed commit message explaining:
 - What was changed
 - Why it was changed
 - Testing performed
 - Impact/benefits]"

git push origin main
```

**Use `--no-ff`** (no fast-forward) to:
- ✅ Preserve feature branch history
- ✅ Make rollback easier
- ✅ Clear merge point in git log
- ✅ Better audit trail

**5. Cleanup After Successful Merge:**
```bash
# Delete local branch
git branch -d feature/name

# Delete remote branch
git push origin --delete feature/name
```

**6. If Changes Need Iteration:**
- ✅ Make changes on feature branch
- ✅ Push updates
- ✅ Re-test
- ✅ Merge when ready
- ❌ Don't merge half-working features to main

**Exception: Emergency Hotfixes**

For critical production issues requiring immediate fix:
```bash
# Create hotfix branch from main
git checkout -b hotfix/critical-issue-description

# Fix, test, commit
git add .
git commit -m "hotfix: Description"

# Merge immediately
git checkout main
git merge --no-ff hotfix/critical-issue-description
git push origin main

# Cleanup
git branch -d hotfix/critical-issue-description
git push origin --delete hotfix/critical-issue-description
```

**Benefits of Feature Branch Workflow:**
- ✅ **Safety**: Main branch always stable
- ✅ **Testing**: Validate on branch before affecting main
- ✅ **Collaboration**: Others can review/test branch
- ✅ **Rollback**: Easy to abandon branch if needed
- ✅ **CI/CD**: Can test pipeline changes on branch first
- ✅ **History**: Clear feature development timeline

**Documentation Requirements:**
- Update CHANGE.log when merging to main
- Note branch name in CHANGE.log entry
- Document commits made on feature branch
- Include testing performed before merge

**5. Post-Merge: Update CHANGE.log (MANDATORY)**
After PR is merged to main:
```bash
# 1. Switch to main and pull latest
git checkout main
git pull origin main

# 2. Get merge commit hash
git log -1 --format=%h

# 3. Get current timestamp
date "+%Y-%m-%d %H:%M:%S"

# 4. Update CHANGE.log with:
#    - Merge commit hash (from step 2)
#    - Current timestamp (from step 3)
#    - PR number and branch name
#    - Summary of all changes in the PR
#    - Testing performed
#    - Impact and next steps

# 5. Commit and push CHANGE.log update
git add docs/CHANGE.log
git commit -m "docs: Update CHANGE.log for PR #X merge"
git push origin main
```

**CRITICAL:** CHANGE.log MUST be updated after EVERY PR merge, even if:
- PR was merged via GitHub UI (not locally)
- Changes were small/minor
- Documentation-only changes

**Why:** CHANGE.log is the official project history. Missing entries create gaps in documentation.

---

### **Rule 9: Test Case Maintenance & Modernization** 🧪 **NEW**

**Principle:** Balance backward compatibility with modernization - preserve legacy tests when needed, modernize when beneficial.

#### **Core Philosophy:**

**1. New Test Cases (Data-Driven Approach):**
- ✅ **New test cases** should be added **WITHOUT code changes** when possible
- ✅ Use **data-driven testing** (Excel, JSON, CSV) for new test scenarios
- ✅ New tests follow **current standards and best practices**
- ✅ Example: Add new login scenarios via Excel file, not new test methods

**2. Old Test Cases (Modernization with Flexibility):**
- ✅ **Old test cases should be kept current** with new standards and processes
- ✅ **BUT** there is an **option to preserve legacy tests** without updates
- ✅ Modernization is **recommended but not mandatory** for existing tests
- ✅ User can explicitly opt-out of modernizing specific test cases

#### **When to Modernize Old Test Cases:**

**✅ MODERNIZE (Default Approach):**
- When touching a test file for other reasons (bug fix, enhancement)
- When test is actively maintained and frequently modified
- When modernization improves maintainability significantly
- When test is part of critical path or frequently executed
- When user explicitly requests modernization

**⏸️ PRESERVE (Opt-Out Available):**
- When test is stable and rarely changed
- When modernization would require extensive refactoring
- When test is legacy/deprecated but still functional
- When user explicitly requests preservation
- When modernization risk outweighs benefits

#### **Modernization Process:**

**Step 1: Identify Test Cases to Modernize**
```bash
# When working on a test file, check:
# - Does it use old patterns (System.out, old logging, etc.)?
# - Does it follow current standards?
# - Is it actively maintained?
```

**Step 2: Ask User (If Unsure):**
```
AI: "I found test cases in [File.java] that could be modernized:
     - Uses old logging pattern (could use log4j 2.x)
     - Uses hardcoded test data (could use data-driven approach)
     - Uses deprecated methods (could use modern alternatives)

     Would you like me to:
     A) Modernize these test cases (recommended)
     B) Preserve them as-is (no changes)
     C) Modernize only specific ones (you choose)"
```

**Step 3: Execute Based on User Choice:**
- **Option A (Modernize):** Update to current standards
- **Option B (Preserve):** Leave unchanged, document why
- **Option C (Selective):** Modernize only specified tests

#### **Data-Driven Testing for New Test Cases:**

**Structure:**
```java
// ✅ NEW APPROACH - Data-driven (no code changes for new test cases)
@DataProvider(name = "loginScenarios")
public Object[][] getLoginData() {
    return ExcelReader.read("test-data/login-scenarios.xlsx", "Sheet1");
}

@Test(dataProvider = "loginScenarios")
public void testLogin(String email, String password, String expectedResult) {
    // Test logic - same for all scenarios
    // New scenarios added via Excel file only
}
```

**Benefits:**
- ✅ Add new test cases by updating Excel/JSON/CSV
- ✅ No code changes required
- ✅ Easier maintenance
- ✅ Business-friendly (non-developers can add cases)

#### **Legacy Test Preservation:**

**When Preserving Legacy Tests:**
```java
// ⏸️ LEGACY TEST - Preserved as-is per user request
// Note: Uses old patterns but functional
// Last updated: [date]
// Reason for preservation: [reason]
@Test
public void legacyTestOldPattern() {
    // Old code preserved intentionally
    System.out.println("Legacy test - preserved");
    // ... old test logic
}
```

**Documentation Required:**
- Add comment explaining why test is preserved
- Note date of last review
- Document any known limitations
- Link to modernization plan (if exists)

#### **Modernization Checklist:**

When modernizing a test case, update:
- ✅ Logging (System.out → log4j 2.x)
- ✅ Test data (hardcoded → data-driven)
- ✅ Assertions (old style → modern assertions)
- ✅ Page Objects (if applicable)
- ✅ Error handling (improved patterns)
- ✅ Documentation (JavaDoc, comments)

#### **User Override Process:**

**User Can Request:**
1. **"Modernize all tests in this file"** → Full modernization
2. **"Preserve all legacy tests"** → No changes, document why
3. **"Modernize only [specific tests]"** → Selective modernization
4. **"Skip modernization for now"** → Defer to later

**AI Must:**
- ✅ Respect user's explicit choice
- ✅ Document the decision
- ✅ Not modernize if user says "preserve"
- ✅ Ask if unclear (don't assume)

#### **Examples:**

**Example 1: Modernize During Bug Fix**
```java
// BEFORE (old pattern):
@Test
public void testLogin() {
    System.out.println("Testing login");
    // ... test code
}

// AFTER (modernized):
@Test
public void testLogin() {
    log.info("Testing login");
    // ... test code (same logic, modern logging)
}
```

**Example 2: Preserve Legacy Test**
```java
// LEGACY TEST - Preserved per user request (2025-11-13)
// Reason: Stable test, rarely modified, modernization risk > benefit
@Test
public void legacyTestOldPattern() {
    System.out.println("Legacy test - preserved");
    // ... old test logic unchanged
}
```

**Example 3: Data-Driven New Test**
```java
// NEW TEST - Data-driven approach
@DataProvider(name = "userScenarios")
public Object[][] getUserData() {
    return ExcelReader.read("test-data/users.xlsx", "Sheet1");
}

@Test(dataProvider = "userScenarios")
public void testUserFlow(String username, String role, String expected) {
    // New scenarios added via Excel only - no code changes needed
}
```

#### **Best Practices:**

**For New Tests:**
- ✅ Always use data-driven approach when possible
- ✅ Follow current standards from day one
- ✅ Use modern patterns (log4j 2.x, parameterized logging, etc.)
- ✅ Document test purpose and data sources

**For Old Tests:**
- ✅ Modernize when touching for other reasons
- ✅ Preserve if user requests or test is stable
- ✅ Document preservation decisions
- ✅ Plan modernization in batches (not all at once)

**For Mixed Scenarios:**
- ✅ Modernize new code, preserve old code in same file
- ✅ Use clear comments to distinguish
- ✅ Document modernization plan for future

#### **Documentation Requirements:**

When preserving legacy tests:
```java
/**
 * LEGACY TEST - Preserved as-is
 *
 * Last Modernized: Never (preserved per user request)
 * Reason: Stable test, low maintenance, modernization risk > benefit
 * Modernization Plan: Consider in future batch modernization effort
 *
 * @deprecated Uses old patterns but functional
 */
@Test
public void legacyTest() {
    // ... old code
}
```

When modernizing:
```java
/**
 * Modernized: 2025-11-13
 * Changes: Updated logging, converted to data-driven, improved assertions
 * Previous Pattern: Hardcoded data, System.out logging
 */
@Test(dataProvider = "scenarios")
public void modernizedTest(String data) {
    // ... modern code
}
```

**Remember:** The goal is maintainable, functional tests - not perfect code. Balance modernization with stability! 🧪

---

### **Rule 10: Recommendations Must Include Industry Standards** 📚 **NEW**

**Principle:** Every suggestion must be grounded in current industry standards with context about how standards have evolved.

#### **Mandatory Requirements for All Recommendations:**

**1. Current Industry Standards (REQUIRED):**
- ✅ **MUST** state what the current industry standard is
- ✅ **MUST** cite authoritative sources when possible (Selenium docs, Java best practices, etc.)
- ✅ **MUST** explain why this is the standard (benefits, adoption, community consensus)
- ✅ **MUST** provide context about the standard's relevance to the specific situation

**2. Industry Standards Evolution (WHEN APPLICABLE):**
- ✅ **MUST** explain how industry standards have changed over time
- ✅ **MUST** explain why the change occurred (security, performance, maintainability, etc.)
- ✅ **MUST** provide timeline context (when did standards shift?)
- ✅ **MUST** explain what replaced old standards and why

**3. Justification (REQUIRED):**
- ✅ **MUST** explain why following the standard benefits this project
- ✅ **MUST** connect the standard to specific project needs
- ✅ **MUST** provide concrete examples of how the standard applies

#### **Format for Recommendations:**

**Template:**
```
RECOMMENDATION: [What to do]

CURRENT INDUSTRY STANDARD:
- Standard: [What is the current standard]
- Source: [Authoritative source - Selenium docs, Java best practices, etc.]
- Adoption: [How widely adopted is this?]
- Benefits: [Why is this the standard?]

INDUSTRY STANDARDS EVOLUTION (if applicable):
- Previous Standard: [What was used before]
- When Changed: [Approximate timeframe]
- Why Changed: [Security, performance, maintainability reasons]
- Migration Path: [How industry migrated]

JUSTIFICATION FOR THIS PROJECT:
- Why This Matters: [Specific to this codebase]
- Benefits: [Concrete benefits for this project]
- Risk of Not Following: [What happens if we don't follow?]

EXAMPLE:
[Code example showing the standard in practice]
```

#### **Examples:**

**Example 1: Logging Standardization**
```
RECOMMENDATION: Replace System.out.println with log4j 2.x

CURRENT INDUSTRY STANDARD:
- Standard: Structured logging frameworks (log4j 2.x, SLF4J, Logback)
- Source: Apache Logging Services, Java Logging Best Practices
- Adoption: 95%+ of enterprise Java applications
- Benefits:
  * Structured output (timestamps, levels, formatting)
  * Configurable log levels without code changes
  * Log aggregation ready (ELK, CloudWatch, etc.)
  * Better performance (async logging, parameterized messages)
  * Production-ready (rotation, retention, filtering)

INDUSTRY STANDARDS EVOLUTION:
- Previous Standard: System.out.println (1990s-2000s)
- When Changed: 2000s-2010s (log4j 1.x), 2010s-present (log4j 2.x)
- Why Changed:
  * System.out: No levels, no configuration, no structure
  * log4j 1.x: Security vulnerabilities (Log4Shell), EOL
  * log4j 2.x: Security fixes, better performance, modern features
- Migration Path: Industry migrated from System.out → log4j 1.x → log4j 2.x

JUSTIFICATION FOR THIS PROJECT:
- Why This Matters: 1,555 logging statements need modernization
- Benefits: Professional logging, better debugging, production-ready
- Risk of Not Following: Hard to debug in production, no log aggregation

EXAMPLE:
// ❌ OLD STANDARD (1990s-2000s):
System.out.println("User logged in: " + username);

// ✅ CURRENT STANDARD (2010s-present):
log.info("User logged in: {}", username);
```

**Example 2: Data-Driven Testing**
```
RECOMMENDATION: Implement data-driven testing for new test cases

CURRENT INDUSTRY STANDARD:
- Standard: Data-driven testing using external data sources (Excel, JSON, CSV)
- Source: TestNG Best Practices, Selenium Documentation
- Adoption: 80%+ of enterprise test automation frameworks
- Benefits:
  * Separation of test logic from test data
  * Easy to add new test cases without code changes
  * Business-friendly (non-developers can add cases)
  * Easier maintenance and updates

INDUSTRY STANDARDS EVOLUTION:
- Previous Standard: Hardcoded test data in test methods (2000s-2010s)
- When Changed: 2010s-present
- Why Changed:
  * Hardcoded data: Requires code changes for new cases, harder maintenance
  * Data-driven: Faster test case addition, better separation of concerns
  * Industry moved to external data sources for scalability
- Migration Path: Hardcoded → @DataProvider → External files (Excel/JSON/CSV)

JUSTIFICATION FOR THIS PROJECT:
- Why This Matters: Need to add many test scenarios efficiently
- Benefits: Faster test case addition, easier maintenance
- Risk of Not Following: Slower test development, harder maintenance

EXAMPLE:
// ❌ OLD STANDARD (2000s-2010s):
@Test
public void testLogin1() {
    login("user1@test.com", "password1");
}
@Test
public void testLogin2() {
    login("user2@test.com", "password2");
}

// ✅ CURRENT STANDARD (2010s-present):
@DataProvider(name = "loginData")
public Object[][] getLoginData() {
    return ExcelReader.read("test-data/login.xlsx", "Sheet1");
}
@Test(dataProvider = "loginData")
public void testLogin(String email, String password) {
    login(email, password);
}
```

**Example 3: Page Object Model**
```
RECOMMENDATION: Use Page Object Model pattern for UI tests

CURRENT INDUSTRY STANDARD:
- Standard: Page Object Model (POM) pattern for UI test automation
- Source: Selenium Best Practices, Page Object Model Pattern
- Adoption: 90%+ of Selenium-based test frameworks
- Benefits:
  * Separation of page structure from test logic
  * Reusable page interactions
  * Easier maintenance when UI changes
  * Better test readability

INDUSTRY STANDARDS EVOLUTION:
- Previous Standard: Direct WebDriver calls in test methods (2000s)
- When Changed: 2010s-present
- Why Changed:
  * Direct calls: Brittle, hard to maintain, code duplication
  * POM: Maintainable, reusable, follows DRY principle
  * Industry consensus: POM is best practice for UI automation
- Migration Path: Direct calls → Helper methods → Page Objects → Page Factory

JUSTIFICATION FOR THIS PROJECT:
- Why This Matters: UI tests need maintainability as UI evolves
- Benefits: Easier maintenance, better organization
- Risk of Not Following: Brittle tests, harder maintenance

EXAMPLE:
// ❌ OLD STANDARD (2000s):
@Test
public void testLogin() {
    driver.findElement(By.id("username")).sendKeys("user");
    driver.findElement(By.id("password")).sendKeys("pass");
    driver.findElement(By.id("login")).click();
}

// ✅ CURRENT STANDARD (2010s-present):
@Test
public void testLogin() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.login("user", "pass");
}
```

#### **When Industry Standards Don't Apply:**

**If no clear industry standard exists:**
- ✅ State that clearly: "No single industry standard exists for this..."
- ✅ Present multiple approaches with pros/cons
- ✅ Explain why different approaches exist
- ✅ Recommend based on project-specific needs

**If standards are evolving:**
- ✅ Acknowledge the evolution: "Industry standards are currently shifting..."
- ✅ Present current state and emerging trends
- ✅ Explain why standards are changing
- ✅ Provide guidance on choosing approach

#### **Sources to Reference (When Possible):**

**Authoritative Sources:**
- ✅ **Selenium**: https://www.selenium.dev/documentation/
- ✅ **TestNG**: https://testng.org/doc/documentation-main.html
- ✅ **Java Best Practices**: Oracle Java Documentation, Effective Java
- ✅ **Maven**: https://maven.apache.org/guides/
- ✅ **Docker**: https://docs.docker.com/develop/dev-best-practices/
- ✅ **GitHub Actions**: https://docs.github.com/en/actions
- ✅ **Log4j**: https://logging.apache.org/log4j/2.x/

**Industry Consensus:**
- ✅ W3C WebDriver Specification
- ✅ Java Community Process (JCP) standards
- ✅ OWASP security guidelines
- ✅ Test automation community best practices

#### **What NOT to Do:**

**❌ DON'T:**
- Make recommendations without explaining current standards
- Assume user knows why standards exist
- Skip explaining industry evolution
- Make recommendations without justification
- Use vague terms like "best practice" without context

**✅ DO:**
- Always explain current industry standard
- Provide context about why it's the standard
- Explain evolution when relevant
- Connect standards to project needs
- Cite sources when possible

#### **User Benefits:**

**Why This Matters:**
- ✅ **Understanding**: User understands "why" not just "what"
- ✅ **Context**: User sees how industry has evolved
- ✅ **Confidence**: User knows recommendation is grounded in standards
- ✅ **Learning**: User learns industry standards for future decisions
- ✅ **Justification**: User can justify changes to stakeholders

**Remember:** A recommendation without context is just an opinion. Ground all suggestions in industry standards! 📚

---

### **Rule 11: Linter Warnings Fix Process** 🔧 **NEW**

> **Related:** See [Rule 3: Post-Change Verification](#rule-3-post-change-verification-after-each-batch) for verification steps. See [Rule 4: Commit & Push Process](#rule-4-commit--push-process-optimized---single-commit) for commit workflow.

**Principle:** Systematically address IDE linter warnings to improve code quality, maintainability, and reduce technical debt.

#### **Current Status (2025-11-15):**
- **Starting Point:** 229 linter warnings
- **Current:** 68 linter warnings remaining
- **Progress:** 70% reduction (161 warnings fixed)
- **Target:** Continue systematic reduction of remaining issues

#### **Categories of Linter Warnings Being Addressed:**

**1. Deprecated API Usage (HIGH PRIORITY):**
- ✅ **Deprecated `URL(String)` constructor** - COMPLETED (20+ instances)
  - **Fix:** Replace `new URL(url)` with `URI.create(url).toURL()`
  - **Reason:** Deprecated in Java 20, URI-based approach is safer
  - **Files Fixed:** SmokeTests, SimpleGridTest, GridConnectionTest, AdvancedFeaturesTests, DataDrivenTests, NegativeTests, EnhancedGridTests, MobileTestsConfiguration, GTWebinarServiceTests, SharepointServiceTests, SSOService, REST, FSOTests, YMService, ISelenium, SeleniumWebDriver, WebService

- ⏳ **Deprecated `Runtime.exec(String)`** - IN PROGRESS (6 instances)
  - **Fix:** Replace with `ProcessBuilder` API
  - **Reason:** More secure and flexible process execution
  - **Files:** CommandLineTests.java (6 instances)

- ⏳ **Deprecated `Cell.setCellType()`** - PENDING (2 instances)
  - **Fix:** Use `Cell.setBlank()` or appropriate setter methods
  - **Reason:** Deprecated in Apache POI 5.x
  - **Files:** XLS.java, XLSX.java

- ⏳ **Deprecated `CSVFormat.withHeader()`** - PENDING (2 instances)
  - **Fix:** Use `CSVFormat.Builder` pattern
  - **Reason:** Deprecated in Apache Commons CSV 1.9+
  - **Files:** SystemProcesses.java, YMDataTests.java

**2. Raw Type Warnings (MEDIUM PRIORITY):**
- ✅ **Map.Entry raw types** - COMPLETED (~30 instances)
  - **Fix:** Parameterize with `Entry<String, String>`, `Entry<Object, Object>`, etc.
  - **Files Fixed:** SQL.java, CommandLineTests.java, Convert.java, HTML.java, JavaHelpers.java, SoftAssert.java, FSOTests.java, ExcelFormulaSumTests.java, ExcelStatisticalTests.java, SauceUtils.java, ContactInfoPage.java, GroupPage.java, Groups.java, YMAPIMethodsTests.java, RTestReporter.java, StepsVivit.java, Reports.java, VitivDataTests.java, Atlassian.java

- ⏳ **Map raw types** - IN PROGRESS (~15 instances)
  - **Fix:** Parameterize with appropriate types `Map<String, String>`, `Map<String, Object>`, etc.
  - **Files:** BTSConvertDatabaseToXMLTests.java, YMDataTests.java, RTestReporter.java

- ⏳ **List/ArrayList raw types** - IN PROGRESS (~10 instances)
  - **Fix:** Parameterize with `List<String>`, `ArrayList<String>`, etc.
  - **Files:** YMDataTests.java, JavaHelpers.java, StepsVivit.java, RTestReporter.java

- ⏳ **Drawing raw type** - PENDING (2 instances)
  - **Fix:** Parameterize with `Drawing<?>` or specific type
  - **Files:** XLS.java, XLSX.java

**3. Null Pointer Access Warnings (MEDIUM PRIORITY):**
- ⏳ **Potential null pointer access** - IN PROGRESS (~8 instances)
  - **Fix:** Add null checks before accessing variables
  - **Pattern:** `if (variable != null && condition)` or `Objects.requireNonNull()`
  - **Files:** CoderTests.java, WebElementTableTests.java, DataTests.java, BingPage.java, ParameterHelper.java, Reports.java, ClaimsAndSpendingPage.java, YMAPIMethodsTests.java

- ⏳ **Null pointer access (Boolean auto-unboxing)** - PENDING (5 instances)
  - **Fix:** Check for null before unboxing or use `Boolean.TRUE.equals(value)`
  - **Files:** YMAPIDebug.java (5 instances)

**4. Unused Variables/Fields (LOW PRIORITY):**
- ⏳ **Unused local variables** - PENDING (~3 instances)
  - **Fix:** Remove if truly unused, or use if needed
  - **Files:** ISelenium.java (screenshot), Page.java (jsExecutor x2)

- ⏳ **Unused fields** - PENDING (~2 instances)
  - **Fix:** Remove or mark with `@SuppressWarnings("unused")` if intentionally kept
  - **Files:** DailyPollQuizPages.java (answersNeeded), Processes.java (log)

- ⏳ **Unused methods** - PENDING (1 instance)
  - **Fix:** Remove if not needed, or document why kept
  - **Files:** PageObjectGenerator.java (getInputType)

**5. Type Safety Warnings (MEDIUM PRIORITY):**
- ⏳ **Unchecked casts** - IN PROGRESS (~5 instances)
  - **Fix:** Add proper type checks or use `@SuppressWarnings("unchecked")` with justification
  - **Files:** EveryoneSocial.java (WebElement to List), Page.java (Object to List<String>), SeleniumWebDriver.java (Object to Map)

- ⏳ **Unnecessary @SuppressWarnings** - PENDING (2 instances)
  - **Fix:** Remove if warning is resolved or no longer needed
  - **Files:** ParameterHelper.java, UnmarshallYourMembershipResponse.java

**6. Static Method Access (COMPLETED):**
- ✅ **Static method access** - COMPLETED (~50 instances)
  - **Fix:** Use `Constants.nlTab()` instead of `c.nlTab()` (instance access)
  - **Files Fixed:** Multiple files across the codebase

#### **Fix Priority Order:**

**Phase 1: Deprecated APIs (HIGH PRIORITY)** ✅ IN PROGRESS
1. ✅ URL(String) constructor - COMPLETED
2. ⏳ Runtime.exec(String) - NEXT
3. ⏳ Cell.setCellType() - PENDING
4. ⏳ CSVFormat.withHeader() - PENDING

**Phase 2: Type Safety (MEDIUM PRIORITY)** ⏳ IN PROGRESS
1. ✅ Map.Entry raw types - COMPLETED
2. ⏳ Map raw types - IN PROGRESS
3. ⏳ List/ArrayList raw types - IN PROGRESS
4. ⏳ Unchecked casts - IN PROGRESS
5. ⏳ Drawing raw type - PENDING

**Phase 3: Null Safety (MEDIUM PRIORITY)** ⏳ IN PROGRESS
1. ⏳ Potential null pointer access - IN PROGRESS
2. ⏳ Boolean auto-unboxing - PENDING

**Phase 4: Code Cleanup (LOW PRIORITY)** ⏳ PENDING
1. ⏳ Unused variables/fields - PENDING
2. ⏳ Unnecessary @SuppressWarnings - PENDING
3. ⏳ Static method access - COMPLETED

#### **Fix Patterns:**

**Deprecated URL Constructor:**
```java
// ❌ OLD (deprecated):
final URL url = new URL(urlString);

// ✅ NEW (Java 20+ compatible):
final URL url = URI.create(urlString).toURL();
// Don't forget to add: import java.net.URI;
```

**Raw Type Map.Entry:**
```java
// ❌ OLD (raw type):
for (Map.Entry entry : map.entrySet()) {
    String key = (String) entry.getKey();
}

// ✅ NEW (parameterized):
for (Map.Entry<String, String> entry : map.entrySet()) {
    String key = entry.getKey(); // No cast needed
}
```

**Null Pointer Safety:**
```java
// ❌ OLD (potential NPE):
if (map.get("status").equals("0")) { }

// ✅ NEW (null-safe):
if (map != null && "0".equals(map.get("status"))) { }
```

**Static Method Access:**
```java
// ❌ OLD (instance access to static):
final Constants c = new Constants();
String result = c.nlTab(1, 0);

// ✅ NEW (static access):
String result = Constants.nlTab(1, 0);
```

#### **Progress Tracking:**

**Before Starting:**
- Check current linter warning count: `read_lints` tool
- Note starting count and categories

**During Fixes:**
- Fix in batches of 10-15 warnings
- Verify compilation after each batch
- Run smoke tests periodically

**After Fixes:**
- Verify warning count reduction
- Update CHANGE.log with progress
- Commit with descriptive message

#### **Verification Requirements:**

**After Each Batch:**
1. ✅ Compilation: `mvn clean compile test-compile -DskipTests`
2. ✅ Smoke Tests: `mvn test -Dtest=SmokeTests -DskipTests=false`
3. ✅ Linter Check: Verify warning count decreased

**Before Committing:**
1. ✅ All changes compile successfully
2. ✅ Smoke tests pass
3. ✅ No new warnings introduced
4. ✅ Warning count reduced

#### **Documentation:**

**Commit Messages Should Include:**
- Category of warnings fixed (e.g., "Fix deprecated URL constructors")
- Number of instances fixed
- Files modified
- Progress: "X warnings remaining (Y% reduction)"

**Example Commit Message:**
```
fix: replace deprecated URL(String) constructor with URI.create()

- Replaced 20+ instances of deprecated URL(String) constructor
- Updated imports to include java.net.URI
- Removed unused URL imports where applicable
- Progress: 68 warnings remaining (70% reduction from 229)
```

#### **Known Limitations:**

**Some warnings may require:**
- Library updates (e.g., Apache POI, Commons CSV)
- Refactoring of complex code paths
- User approval for breaking changes
- Additional testing for null safety fixes

**When to Defer:**
- Warnings requiring major refactoring
- Warnings in rarely-used code paths
- Warnings that would break existing functionality
- Warnings requiring library version updates

**Remember:** Systematic reduction of linter warnings improves code quality, but prioritize correctness and stability over speed! 🔧

---

### **Rule 12: API Testing Workflow** 🌐 **NEW**

> **Related:** See [Rule 3: Post-Change Verification](#rule-3-post-change-verification-after-each-batch) for verification steps. See [Rule 8: Feature Branch Workflow](#rule-8-feature-branch-workflow--new) for branch workflow.

**Principle:** API tests run independently of Selenium Grid and have different verification needs.

#### **When to Use API Tests:**
- ✅ Testing REST endpoints
- ✅ Contract testing
- ✅ Fast feedback loop (no browser overhead)
- ✅ CI/CD pipeline validation
- ✅ When UI tests are unnecessary for the change

#### **API Testing Workflow:**

**1. Run API Tests:**
```bash
# Option 1: Using script (recommended)
./scripts/tests/frameworks/run-api-tests.sh

# Option 2: Using Maven directly
./mvnw test -DsuiteXmlFile=testng-api-suite.xml

# Option 3: Using Docker
docker-compose run --rm tests test -DsuiteXmlFile=testng-api-suite.xml
```

**2. Verification:**
- ✅ All 31 API tests pass (31/31)
- ✅ Response validation successful
- ✅ Contract tests pass
- ✅ No Grid setup required
- ✅ No Docker required (can run with Maven directly)

#### **Benefits:**
- ✅ **Faster execution** - No browser overhead (~3-5 min vs 10-15 min for UI tests)
- ✅ **No Grid required** - Runs independently of Selenium Grid
- ✅ **Easier debugging** - Direct HTTP request/response inspection
- ✅ **Better for CI/CD** - Faster pipeline execution

#### **Test Suite Breakdown:**
**Basic API Tests (7 tests):**
- GET requests & status codes
- Response body validation
- Header verification
- Response time testing
- Query parameters
- Collection retrieval
- Error handling (404)

**CRUD Operations (7 tests):**
- CREATE (POST)
- READ (GET)
- UPDATE (PUT)
- DELETE
- Validation of operations

**Contract Testing (17 tests):**
- API contract validation
- Schema validation
- Data type validation

#### **When to Skip API Tests:**
- ⏭️ Documentation-only changes
- ⏭️ Markdown file updates
- ⏭️ Configuration file updates (non-API related)
- ⏭️ When explicitly approved by user

#### **Troubleshooting:**
**If API tests fail:**
1. Check network connectivity
2. Verify API endpoint is accessible
3. Check authentication credentials (Google Cloud Secret Manager)
4. Review API contract changes
5. Check response format matches expected schema

**Remember:** API tests provide fast feedback without Grid overhead! 🌐

---

### **Rule 13: Multi-Environment Testing Workflow** 🌍 **NEW**

> **Related:** See [Rule 3: Post-Change Verification](#rule-3-post-change-verification-after-each-batch) for verification steps. See [Rule 8: Feature Branch Workflow](#rule-8-feature-branch-workflow--new) for branch workflow.

**Principle:** Different environments (dev/test/prod) require different configurations and testing approaches.

#### **Environments:**

**Available Environments:**
- **dev** - Development environment
- **test** - Test/QA environment
- **prod** - Production environment

#### **Multi-Environment Workflow:**

**1. Select Environment:**
```bash
# Set environment variable
export ENV=dev  # or test, prod

# Or use inline for one command
ENV=dev docker-compose -f docker-compose.dev.yml up -d
```

**2. Use Environment-Specific Compose File:**
```bash
# Development environment
docker-compose -f docker-compose.dev.yml up -d selenium-hub chrome-node-1

# Test environment
docker-compose -f docker-compose.test.yml up -d selenium-hub chrome-node-1

# Production environment
docker-compose -f docker-compose.prod.yml up -d selenium-hub chrome-node-1
```

**3. Run Environment-Specific Tests:**
```bash
# Dev environment
docker-compose -f docker-compose.dev.yml run --rm tests test -Dtest=SmokeTests

# Prod environment
docker-compose -f docker-compose.prod.yml run --rm tests test -Dtest=SmokeTests
```

#### **Environment-Specific Considerations:**

**Development Environment:**
- ✅ Less strict validation
- ✅ Debugging enabled
- ✅ Verbose logging
- ✅ Fast feedback priority

**Test Environment:**
- ✅ Full test suite
- ✅ Performance monitoring
- ✅ Integration testing
- ✅ Pre-production validation

**Production Environment:**
- ✅ Smoke tests only (unless emergency)
- ✅ Minimal logging
- ✅ Security-focused
- ✅ Monitoring enabled

#### **Configuration Files:**
- `docker-compose.dev.yml` - Development configuration
- `docker-compose.test.yml` - Test environment configuration
- `docker-compose.prod.yml` - Production configuration
- `config/Environments.xml` - Environment-specific test settings

#### **GitHub Actions Integration:**
The CI/CD pipeline supports multi-environment testing:
- Automatic environment detection
- Environment-specific test execution
- Environment-based secrets management

#### **Best Practices:**
- ✅ Always test in dev before test environment
- ✅ Always test in test before prod environment
- ✅ Use feature branches for environment-specific changes
- ✅ Document environment-specific configurations
- ✅ Keep environment configurations synchronized where possible

#### **When to Use Each Environment:**

**Use Dev For:**
- Initial development
- Quick verification
- Debugging
- Experimentation

**Use Test For:**
- Pre-merge validation
- Integration testing
- Performance testing
- User acceptance testing

**Use Prod For:**
- Smoke tests after deployment
- Emergency hotfix validation
- Production monitoring verification

**Remember:** Always validate in lower environments before higher environments! 🌍

---

### **Rule 14: Performance Testing Workflow** ⚡ **NEW**

> **Related:** See [Rule 3: Post-Change Verification](#rule-3-post-change-verification-after-each-batch) for verification steps. See [🚫 Excluded Tests](#-excluded-tests---never-run-automatically) for why these are manual-only.

**Principle:** Performance tests are manual-only, resource-intensive, and require specific infrastructure setup.

#### **When to Run Performance Tests:**
- ✅ User explicitly requests performance testing
- ✅ Before major releases (scheduled/planned)
- ✅ Performance regression investigation
- ✅ Load capacity planning
- ✅ Infrastructure changes
- ❌ NEVER during routine code changes
- ❌ NEVER during PMD fixes or refactoring

#### **Available Performance Testing Tools:**

**1. Gatling (Scala-based Load Testing)**
```bash
# Run Gatling tests
./scripts/run-gatling-tests.sh

# Or manually
cd src/test/scala
# Run Gatling simulations
```
- **Duration:** 10-30 minutes
- **Files:** `src/test/scala/*LoadSimulation.scala`
- **Purpose:** Load testing, stress testing
- **Output:** HTML reports in `target/gatling/`

**2. JMeter (Java-based Performance Testing)**
```bash
# Run JMeter tests
./scripts/tests/performance/run-jmeter-tests.sh

# Or manually
jmeter -n -t src/test/jmeter/test-plan.jmx -l results.jtl
```
- **Duration:** 15-30 minutes
- **Files:** `src/test/jmeter/*.jmx`
- **Purpose:** Performance benchmarking, load testing
- **Output:** JTL files, HTML reports

**3. Locust (Python-based Load Testing)**
```bash
# Run Locust tests
./scripts/tests/performance/run-locust-tests.sh

# Or manually
cd src/test/locust
locust -f load_test.py --host=https://target-url.com
```
- **Duration:** 10-20 minutes
- **Files:** `src/test/locust/*_test.py`
- **Purpose:** HTTP load testing, stress testing
- **Output:** Web-based dashboard, CSV reports

#### **Performance Testing Workflow:**

**1. Pre-Testing Checklist:**
- ✅ Confirm user request explicitly
- ✅ Verify infrastructure is ready
- ✅ Check target environment capacity
- ✅ Schedule during off-hours (if production)
- ✅ Notify relevant teams
- ✅ Set up monitoring

**2. Run Performance Tests:**
```bash
# Choose appropriate tool based on requirements
./scripts/run-gatling-tests.sh    # For Scala-based load testing
./scripts/tests/performance/run-jmeter-tests.sh     # For Java-based performance testing
./scripts/tests/performance/run-locust-tests.sh     # For Python-based HTTP load testing
```

**3. Monitor During Execution:**
- ✅ Monitor system resources (CPU, memory, disk)
- ✅ Check network bandwidth
- ✅ Watch application logs
- ✅ Monitor target system health
- ✅ Track response times

**4. Analyze Results:**
- ✅ Review performance metrics
- ✅ Compare with baseline
- ✅ Identify bottlenecks
- ✅ Check for regressions
- ✅ Document findings

**5. Post-Testing:**
- ✅ Document results separately
- ✅ Create performance report
- ✅ Share findings with team
- ✅ Update CHANGE.log if significant findings
- ✅ Create GitHub issue if performance regressions found

#### **Performance Test Scenarios:**

**Load Testing:**
- Normal expected load
- Peak load conditions
- Sustained load over time

**Stress Testing:**
- Above-normal load
- Breaking point identification
- Recovery behavior

**Spike Testing:**
- Sudden load increases
- System response to spikes
- Recovery time

#### **Safety Guidelines:**
- ✅ **Never run against production** without explicit approval
- ✅ **Always test in isolated environment first**
- ✅ **Monitor resource usage** continuously
- ✅ **Stop immediately** if target system shows stress
- ✅ **Document everything** for reproducibility

#### **Maven Profiles to Avoid:**
❌ **DO NOT** use these profiles during normal workflow:
- `-P performance`
- `-P load-test`
- `-P gatling`
- `-P jmeter`

#### **Expected Outputs:**

**Gatling:**
- HTML reports: `target/gatling/results/`
- Statistics: Response times, throughput, errors

**JMeter:**
- JTL files: `results.jtl`
- HTML reports: Dashboard with graphs and tables

**Locust:**
- Web dashboard: `http://localhost:8089`
- CSV reports: Exportable data files

**Remember:** Performance tests generate significant load - use responsibly and only when needed! ⚡

---

### **Rule 15: Pull Request Workflow** 🔄 **NEW**

> **Related:** See [Rule 8: Feature Branch Workflow](#rule-8-feature-branch-workflow--new) for branch creation. See [Rule 4: Commit & Push Process](#rule-4-commit--push-process-optimized---single-commit) for commit workflow.

> **🚨 AUTHORIZATION REQUIRED:** Creating a PR requires explicit approval, just like commit and push. Even if you were previously told to create a PR, you **CANNOT** create another PR without fresh approval after local review.

**Principle:** Pull requests enable code review, validation, and collaboration before merging to main.

#### **When to Create Pull Request:**
- ✅ Feature branches (always)
- ✅ Major refactoring
- ✅ Breaking changes
- ✅ Multi-file changes (>5 files)
- ✅ User requests PR review
- ✅ Complex changes requiring validation
- ⏭️ Skip for: Documentation-only updates, quick fixes (<2 files, low risk)

#### **PR Creation Checklist:**

**Before Creating PR:**
- ✅ All tests pass locally
- ✅ No linter warnings introduced
- ✅ Documentation updated (CHANGE.log, README, etc.)
- ✅ Branch is up to date with main
- ✅ Commit messages are descriptive
- ✅ Related issues referenced

#### **PR Creation Process:**

**1. Prepare Your Branch:**
```bash
# Ensure branch is up to date
git checkout feature/your-branch-name
git pull origin main  # Get latest from main
git merge main         # Merge latest changes
# Or use rebase: git rebase main

# Resolve any conflicts
# Run tests again after merge
docker-compose run --rm tests test -Dtest=SmokeTests -Dcheckstyle.skip=true
```

**2. Push Branch to Remote:**
```bash
git push -u origin feature/your-branch-name
```

**3. Create PR on GitHub:**
- Go to: https://github.com/CScharer/full-stack-qa/pulls
- Click "New Pull Request"
- Select: `base: main` ← `compare: feature/your-branch-name`
- Fill in PR template

**4. PR Template Requirements:**
```markdown
## Description
Brief description of changes

## Related Issues
Fixes #123
Related to #456

## Changes Made
- Change 1
- Change 2
- Change 3

## Testing Performed
- [ ] Smoke tests pass
- [ ] Full test suite pass
- [ ] Docker build successful
- [ ] No new linter warnings

## Checklist
- [ ] Code follows project standards
- [ ] Documentation updated
- [ ] CHANGE.log entry ready (will add after merge)
- [ ] No credentials in code
- [ ] All tests pass
```

#### **PR Review Process:**

**1. Wait for CI/CD:**
- GitHub Actions will run automatically
- Wait for all jobs to complete
- Fix any failures before requesting review

**2. Request Review:**
- Assign reviewers (if applicable)
- Request team member review
- Tag with appropriate labels

**3. Address Review Comments:**
```bash
# Make changes based on feedback
git add .
git commit -m "Address PR review comments"
git push origin feature/your-branch-name
```

**4. Re-test After Changes:**
- Run smoke tests locally
- Verify CI/CD still passes
- Update PR description if needed

#### **PR Merge Process:**

**1. Pre-Merge Validation:**
- ✅ All CI/CD jobs pass
- ✅ Approved by reviewers (if required)
- ✅ No merge conflicts
- ✅ Branch is up to date with main

**2. Merge Options:**
```bash
# Option 1: Merge via GitHub UI (recommended)
# - Click "Merge pull request"
# - Choose merge type:
#   * "Create a merge commit" (preserves history)
#   * "Squash and merge" (single commit)
#   * "Rebase and merge" (linear history)

# Option 2: Merge locally
git checkout main
git pull origin main
git merge --no-ff feature/your-branch-name -m "Merge feature/branch: Description"
git push origin main
```

**3. Post-Merge: Update CHANGE.log (MANDATORY)**
```bash
# 1. Switch to main and pull latest
git checkout main
git pull origin main

# 2. Get merge commit hash
git log -1 --format=%h

# 3. Get current timestamp
date "+%Y-%m-%d %H:%M:%S"

# 4. Update CHANGE.log with:
#    - Merge commit hash (from step 2)
#    - Current timestamp (from step 3)
#    - PR number and branch name
#    - Summary of all changes in the PR
#    - Testing performed
#    - Impact and next steps

# 5. Commit and push CHANGE.log update
git add docs/CHANGE.log
git commit -m "docs: Update CHANGE.log for PR #X merge"
git push origin main
```

**4. Cleanup After Merge:**
```bash
# Delete local branch
git branch -d feature/your-branch-name

# Delete remote branch (if not auto-deleted)
git push origin --delete feature/your-branch-name
```

#### **PR Best Practices:**
- ✅ **Keep PRs focused** - One feature/fix per PR
- ✅ **Keep PRs small** - Easier to review (< 500 lines changed)
- ✅ **Write clear descriptions** - Help reviewers understand changes
- ✅ **Update documentation** - Include doc changes in PR
- ✅ **Reference issues** - Link related issues
- ✅ **Respond to feedback** - Address review comments promptly

#### **CRITICAL:** CHANGE.log MUST be updated after EVERY PR merge, even if:
- PR was merged via GitHub UI (not locally)
- Changes were small/minor
- Documentation-only changes

**Why:** CHANGE.log is the official project history. Missing entries create gaps in documentation.

**Remember:** Pull requests enable collaboration and quality assurance before code reaches main! 🔄

---

## 📋 VERIFICATION CHECKLIST TEMPLATE

Use this checklist for EVERY batch:

```
### Batch N: [Description]

**Files Modified:** [count] files
**Fixes Applied:** [count] fixes

**Pre-Push Verification:**
[ ] Step 1: Compilation - BUILD SUCCESS
[ ] Step 2: Tests - 65+/66 passing
[ ] Step 3: Docker Build - Image built successfully

**Push Status:**
[ ] Changes committed
[ ] Pushed to GitHub
[ ] Verified push succeeded

**Post-Push:**
[ ] No errors in git push
[ ] Ready for next batch OR end of session
```

---

## 🎯 EXCEPTION HANDLING

### **Known Flaky Tests (Allowed to Fail):**
- `testWindowManagement` (timing-sensitive)
- `ResponsiveDesignTests.tearDown` (cleanup race condition)

**Rule:** If 65 or 66 tests pass, proceed. If fewer than 65 pass, STOP and investigate.

### **Network Errors in Docker:**
If you see dependency download errors:
1. Retry once
2. If persists, proceed with compilation-only verification
3. Note in commit message: "Tests deferred to GitHub Actions (Docker network issue)"
4. Inform user to check GitHub Actions for test results

---

## ✅ QUICK REFERENCE

**Before EVERY batch:**
```bash
# 0. Run Google Java Format (auto-fix imports/formatting, ~30-60 sec)
docker-compose run --rm tests com.spotify.fmt:fmt-maven-plugin:format -Dcheckstyle.skip=true

# 0b. (Optional) Run Checkstyle validation to monitor violations (~20-30 sec)
docker-compose run --rm tests checkstyle:checkstyle -DskipTests

# 0c. VERIFY FILE CHANGES PERSIST (CRITICAL - 5 sec)
git status --short
# ⚠️  Count modified files - should match number of files you changed!
# If files missing, changes didn't persist - STOP and investigate!

# 1. Verify compilation (REQUIRED - every batch, MUST INCLUDE test-compile!)
docker-compose run --rm tests compile test-compile
# OR with clean: docker-compose run --rm tests clean compile test-compile
# ❌ WRONG: docker-compose run --rm tests compile -DskipTests (misses test-compile!)

# 2. Run smoke tests (REQUIRED - every batch, fast ~2-3 min)
docker-compose run --rm tests test -Dtest=SmokeTests -Dcheckstyle.skip=true

# 3. Docker build (every 3-5 batches)
docker-compose build tests

# 4. Full test suite (every 5-10 batches as checkpoint)
docker-compose run --rm tests test -Dcheckstyle.skip=true
```

**After verification passes (or if documentation-only):**
```bash
# 5. Check if documentation-only change (NEW in v2.2!)
git status --short | grep -v -E '\.(md|log|txt|rst|adoc)$'
# If NO output → Documentation-only! Skip steps 1-4 above ✅

# 6. Get actual timestamp (FIRST!)
date "+%Y-%m-%d %H:%M:%S"

# 7. Get last commit hash (SECOND!)
git log -1 --format=%h

# 8. Update CHANGE.log (MANDATORY - BOTH updates in one!)
# a) Find previous [PENDING] entry → replace with actual commit hash
# b) Add NEW entry at top with [PENDING], timestamp, summary, details

# 9. Commit and push (SINGLE commit with both updates!)
git add -A
git commit -m "..."
# NOTE: If pre-commit hook fails with password warning:
# - Review the output carefully
# - If YOU added passwords: STOP and fix them!
# - If passwords already exist: Request user approval before using --no-verify
git push origin main

# 10. Monitor GitHub Actions (if code changed)
# Check https://github.com/CScharer/full-stack-qa/actions
# Wait for green status or investigate failures
# (Documentation-only changes: pipeline will skip tests automatically)
```

**Benefits of v2.2:**
- ✅ 1 commit per batch (was 2)
- ✅ 1 push per batch (was 2)
- ✅ Cleaner git history
- ✅ More efficient workflow

**NEVER skip:**
- Compilation + smoke tests (unless explicitly approved by user)
- CHANGE.log update (MANDATORY before every push)
- Credential security check (NEVER use --no-verify without user approval)

---

## 🎯 SMOKE TESTS

**What are smoke tests?**
- Basic functionality tests that run quickly
- Located in: `src/test/java/com/cjs/qa/junit/tests/SmokeTests.java`
- Cover: Basic navigation, element finding, driver initialization
- Duration: ~2-3 minutes (vs 10-15 min for full suite)

**Why smoke tests?**
- Fast feedback on compilation AND runtime issues
- Catch most regressions without full test suite wait
- ~5x faster than full suite
- Complement comprehensive GitHub Actions testing

**Command:**
```bash
docker-compose run --rm tests test -Dtest=SmokeTests -Dcheckstyle.skip=true
```

**Alternative (if local Maven environment is properly configured):**
```bash
mvn test -Dtest=SmokeTests -Dcheckstyle.skip=true
```

**Expected Output:**
```
Tests run: X, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

**Note:** Using Docker ensures consistent environment regardless of local Maven setup.

---

## 🚫 EXCLUDED TESTS - NEVER RUN AUTOMATICALLY

### **Load & Performance Tests - MANUAL ONLY**

**❌ DO NOT run these tests during normal workflow:**

**Tools to EXCLUDE:**
1. **Gatling** (Scala-based load testing)
   - Files: `src/test/scala/*LoadSimulation.scala`
   - Reason: Resource-intensive, generates load on systems

2. **Locust** (Python-based load testing)
   - Files: `src/test/locust/*_test.py`
   - Reason: Generates HTTP load, requires specific configuration

3. **JMeter** (Performance testing)
   - Files: `src/test/jmeter/*.jmx`
   - Reason: Performance benchmarking, not functional testing

**When to run these tests:**
- ✅ Only when explicitly requested by user
- ✅ Manual performance benchmarking sessions
- ✅ Before major releases (scheduled/planned)
- ❌ NEVER during routine code changes
- ❌ NEVER during PMD fixes or refactoring

**Why exclude them?**
- Generate significant load on systems/services
- Require specific infrastructure setup
- Take considerable time to complete
- Not relevant for code quality verification
- May impact production/staging environments if misconfigured

**Maven profiles (if any exist) to AVOID:**
- `-P performance`
- `-P load-test`
- `-P gatling`
- `-P jmeter`

**If user requests load/performance testing:**
1. Confirm the request explicitly
2. Verify infrastructure is ready
3. Run in isolated environment
4. Monitor resource usage
5. Document results separately

---

## 📝 NOTES

- These rules apply to ALL code changes, not just PMD fixes
- User may override these rules on a case-by-case basis
- When in doubt, ask the user before proceeding
- Prioritize code quality over speed

---

## 🔧 TROUBLESHOOTING

### **Common Issues and Solutions**

#### **Issue: Docker Build Fails**

**Symptom:**
```bash
ERROR: failed to solve: failed to fetch https://...
```

**Solutions:**
1. **Retry once** - Network issues are often transient
   ```bash
   docker-compose build tests
   ```

2. **Use --no-cache** if retry fails:
   ```bash
   docker-compose build --no-cache tests
   ```

3. **Check Docker daemon** is running:
   ```bash
   docker ps
   ```

4. **Clean Docker resources** if space issue:
   ```bash
   docker system prune -a
   ```

#### **Issue: Tests Timeout**

**Symptom:**
```
Tests run: 65, Failures: 0, Errors: 1
ERROR: testMethod (timeout: 30000ms)
```

**Solutions:**
1. **Check Grid health:**
   ```bash
   ./scripts/docker/grid-health.sh
   ```

2. **Restart Grid:**
   ```bash
   ./scripts/docker/grid-stop.sh
   ./scripts/docker/grid-start.sh
   ```

3. **Check Grid console:** http://localhost:4444
   - Verify nodes are available
   - Check session count

4. **Increase timeout** in test if needed (document why)

#### **Issue: Pre-Commit Hook Fails**

**Symptom:**
```
pre-commit hook: Possible credential detected!
FAILED: password detection
```

**Solutions:**
1. **Review output carefully** - which files have passwords?

2. **If YOU added the password:**
   - ❌ **STOP immediately**
   - Remove credential from code
   - Use Google Cloud Secret Manager (see [Rule 2.6](#rule-26-secure-credential-management-))
   - Re-commit

3. **If credential detected in unmodified file:**
   - Request user approval for `--no-verify`
   - Document in commit message

#### **Issue: Changes Don't Persist**

**Symptom:**
```bash
git status --short
# Shows no modified files, but you just edited files
```

**Solutions:**
1. **Check Docker volume mounts:**
   ```bash
   # Verify docker-compose.yml has correct volume mounts
   cat docker-compose.yml | grep volumes
   ```

2. **Check file permissions:**
   ```bash
   ls -la src/test/java/com/cjs/qa/some/File.java
   ```

3. **Try editing directly** (not through Docker):
   ```bash
   # Edit file in your IDE, then verify
   git status --short
   ```

4. **Restart Docker** if volume mount issue:
   ```bash
   docker-compose down
   docker-compose up -d
   ```

#### **Issue: Compilation Errors After Format**

**Symptom:**
```
ERROR: Cannot resolve symbol 'SomeClass'
BUILD FAILURE
```

**Solutions:**
1. **Check imports** - Google Java Format may have removed unused imports
   ```bash
   # Re-run format to ensure consistency
   docker-compose run --rm tests com.spotify.fmt:fmt-maven-plugin:format
   ```

2. **Check for missing dependencies:**
   ```bash
   ./mvnw dependency:tree
   ```

3. **Clean and rebuild:**
   ```bash
   ./mvnw clean compile test-compile
   ```

#### **Issue: Git Push Fails**

**Symptom:**
```
ERROR: Permission denied (publickey)
ERROR: failed to push some refs
```

**Solutions:**
1. **Check SSH keys:**
   ```bash
   ssh -T git@github.com
   ```

2. **Use HTTPS instead:**
   ```bash
   git remote set-url origin https://github.com/CScharer/full-stack-qa.git
   ```

3. **Check branch name** - are you pushing to feature branch?
   ```bash
   git branch
   # Should be on feature branch, not main
   ```

#### **Issue: CI/CD Pipeline Fails**

**Symptom:**
GitHub Actions shows red X, tests fail in CI but pass locally

**Solutions:**
1. **Check CI logs** - what's different in CI?
   - Environment variables
   - Java version
   - Maven version
   - Test execution order

2. **Run tests in Docker** (matches CI environment):
   ```bash
   docker-compose run --rm tests test -Dcheckstyle.skip=true
   ```

3. **Check for flaky tests** - same test failing intermittently?
   - Document as known flaky (see [Exception Handling](#-exception-handling))
   - Create GitHub issue

4. **Verify test data** - CI might not have access to test data

#### **Issue: Merge Conflicts**

**Symptom:**
```
CONFLICT: Merge conflict in File.java
Auto-merging failed
```

**Solutions:**
1. **Update branch from main:**
   ```bash
   git checkout feature/your-branch
   git pull origin main
   ```

2. **Resolve conflicts:**
   - Open conflicted files
   - Choose correct version or merge manually
   - Mark as resolved: `git add <file>`

3. **Test after resolving:**
   ```bash
   ./mvnw clean compile test-compile
   ./mvnw test -Dtest=SmokeTests
   ```

4. **Commit resolution:**
   ```bash
   git commit -m "resolve: Merge conflicts with main"
   ```

#### **Issue: Linter Warnings Increase**

**Symptom:**
Previously had 68 warnings, now showing 75 warnings

**Solutions:**
1. **Check if warnings are new:**
   ```bash
   # Run linter check
   read_lints
   ```

2. **Identify new warnings** vs existing warnings

3. **If new warnings introduced:**
   - Fix them before committing
   - Document in CHANGE.log
   - Follow [Rule 11: Linter Warnings Fix Process](#rule-11-linter-warnings-fix-process--new)

#### **Issue: Tests Pass Locally But Fail in CI**

**Symptom:**
All tests pass locally, but CI shows failures

**Solutions:**
1. **Run tests in Docker** (matches CI environment):
   ```bash
   docker-compose run --rm tests test -Dcheckstyle.skip=true
   ```

2. **Check environment differences:**
   - Java version (CI uses Java 21)
   - Maven version
   - System properties
   - Environment variables

3. **Check for timing issues:**
   - Flaky tests due to timing
   - Race conditions
   - Network timeouts

4. **Check test isolation:**
   - Tests interfering with each other
   - Shared state between tests
   - Database/file cleanup issues

---

## 💡 COMMON CODE PATTERNS

### **Deprecated API Patterns**

#### **Deprecated URL Constructor:**
```java
// ❌ OLD (deprecated in Java 20):
final URL url = new URL(urlString);

// ✅ NEW (Java 20+ compatible):
final URL url = URI.create(urlString).toURL();
// Don't forget: import java.net.URI;
```

#### **Deprecated Runtime.exec:**
```java
// ❌ OLD (deprecated):
Runtime.getRuntime().exec("command");

// ✅ NEW (ProcessBuilder API):
ProcessBuilder pb = new ProcessBuilder("command");
Process process = pb.start();
```

#### **Deprecated Cell.setCellType:**
```java
// ❌ OLD (deprecated in Apache POI 5.x):
cell.setCellType(CellType.STRING);

// ✅ NEW:
cell.setBlank();  // or appropriate setter
// Or use: cell.setCellValue("value");
```

### **Raw Type Patterns**

#### **Raw Map.Entry:**
```java
// ❌ OLD (raw type):
for (Map.Entry entry : map.entrySet()) {
    String key = (String) entry.getKey();
}

// ✅ NEW (parameterized):
for (Map.Entry<String, String> entry : map.entrySet()) {
    String key = entry.getKey(); // No cast needed
}
```

#### **Raw Map:**
```java
// ❌ OLD (raw type):
Map map = new HashMap();
map.put("key", "value");
String value = (String) map.get("key");

// ✅ NEW (parameterized):
Map<String, String> map = new HashMap<>();
map.put("key", "value");
String value = map.get("key"); // No cast needed
```

### **Null Safety Patterns**

#### **Null Pointer Prevention:**
```java
// ❌ OLD (potential NPE):
if (map.get("status").equals("0")) { }

// ✅ NEW (null-safe):
if (map != null && "0".equals(map.get("status"))) { }
// Or use Objects.equals():
if (Objects.equals(map.get("status"), "0")) { }
```

#### **Boolean Auto-Unboxing:**
```java
// ❌ OLD (potential NPE):
Boolean flag = getFlag();
if (flag) { }  // NPE if flag is null

// ✅ NEW (null-safe):
Boolean flag = getFlag();
if (Boolean.TRUE.equals(flag)) { }
// Or check for null:
if (flag != null && flag) { }
```

### **Logging Patterns**

#### **System.out → log4j:**
```java
// ❌ OLD:
System.out.println("User logged in: " + username);

// ✅ NEW (log4j 2.x):
private static final Logger log = LogManager.getLogger(ClassName.class);
log.info("User logged in: {}", username);
```

#### **Parameterized Logging:**
```java
// ❌ OLD (creates string even if not logged):
log.debug("Processing user: " + user.getName());

// ✅ NEW (lazy evaluation):
log.debug("Processing user: {}", user.getName());
```

### **Static Method Access:**

```java
// ❌ OLD (instance access to static):
final Constants c = new Constants();
String result = c.nlTab(1, 0);

// ✅ NEW (static access):
String result = Constants.nlTab(1, 0);
```

### **Data-Driven Testing Patterns:**

```java
// ✅ NEW APPROACH - Data-driven (no code changes for new test cases)
@DataProvider(name = "loginScenarios")
public Object[][] getLoginData() {
    return ExcelReader.read("test-data/login-scenarios.xlsx", "Sheet1");
}

@Test(dataProvider = "loginScenarios")
public void testLogin(String email, String password, String expectedResult) {
    // Test logic - same for all scenarios
    // New scenarios added via Excel file only
}
```

---

## 📋 VERSION HISTORY

### 📋 Version 3.0 Changes (2025-11-15):
- **🚨 CRITICAL:** Added Rule 0 - NEVER Commit Directly to Main/Master
  - Absolute prohibition on committing directly to main/master
  - ALWAYS create feature branch first workflow
  - Verification steps to ensure on feature branch
  - Updated all workflows to emphasize feature branch usage
  - This is the #1 rule for safe development

- **📚 MAJOR REORGANIZATION:** Enhanced document structure and navigation
  - Added Ultra-Quick Reference section at top for daily use
  - Added comprehensive Table of Contents with categorized sections
  - Added Rule Index for quick lookup with priority indicators
  - Added Command Cheat Sheet table for common tasks
  - Moved version history to end (improves daily usability)
  - Added cross-references between related rules
  - Better navigation and findability

- **🔧 ADDED:** Rule 12 - API Testing Workflow
  - REST Assured test execution (31 tests)
  - No Grid required workflow
  - Fast feedback loop documentation
  - Troubleshooting guide

- **🌍 ADDED:** Rule 13 - Multi-Environment Testing Workflow
  - Dev/Test/Prod environment workflows
  - Environment-specific configurations
  - Best practices for multi-environment testing
  - GitHub Actions integration

- **⚡ ADDED:** Rule 14 - Performance Testing Workflow
  - Gatling, JMeter, Locust execution
  - Manual-only workflow documentation
  - Safety guidelines and monitoring
  - Performance test scenarios

- **🔄 ADDED:** Rule 15 - Pull Request Workflow
  - PR creation and review process
  - PR merge procedures
  - Post-merge CHANGE.log update workflow
  - PR best practices and templates

- **🔄 ENHANCED:** Rule 7 - Error Recovery & Rollback (previously minimal)
  - Error classification system (Recoverable/Non-Recoverable/Transient)
  - Detailed rollback procedures for multiple scenarios
  - Communication process for error handling
  - Prevention strategies

- **📦 ADDED:** Rule 16 - Dependency Update Workflow
  - When to update dependencies (security, planned, deferred)
  - Dependency update process with verification steps
  - Breaking change handling
  - Rollback plan for failed updates

- **✅ ADDED:** Rule 17 - Code Review Checklist
  - Pre-commit review checklist
  - Commit message format guidelines
  - Common issues to watch for
  - Code review best practices

- **🔧 ADDED:** Troubleshooting Section
  - Common issues and solutions (Docker, tests, git, CI/CD)
  - Step-by-step troubleshooting guides
  - Quick fixes for frequent problems

- **💡 ADDED:** Common Code Patterns Section
  - Deprecated API replacement patterns
  - Raw type fixes
  - Null safety patterns
  - Logging modernization patterns
  - Data-driven testing examples

- **🎯 GOAL:** Improve daily usability while maintaining comprehensive coverage
- **📊 RESULT:** Document now ~2,900 lines with 18 rules (Rule 0 + 17 existing), better organized, more navigable, and includes troubleshooting

### 📋 Version 2.9 Changes:
