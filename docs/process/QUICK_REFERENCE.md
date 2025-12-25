# Pre-Pipeline Validation Quick Reference

**Status**: 📋 Living Document  
**Purpose**: One-page quick reference for critical pre-commit/pre-push validation checks  
**Full Guide**: See [PRE_PIPELINE_VALIDATION.md](./PRE_PIPELINE_VALIDATION.md) for comprehensive checklist

---

## ⚡ Fast Path (Minimum Required Checks)

**Time**: ~5-10 minutes

1. ✅ **Git branch verification** (must be feature branch, not main/master)
2. ✅ **Maven compile + test-compile** (`./mvnw clean compile test-compile`)
3. ✅ **npm ci for affected projects** (if Node.js changes)
4. ✅ **Smoke tests** (if test changes) - `./mvnw test -Dtest=SmokeTests -Dcheckstyle.skip=true`

**⚠️ Before final commit/push**: Run the [full checklist](./PRE_PIPELINE_VALIDATION.md)

---

## 🚨 Critical Checks (Never Skip)

### Git Workflow
- [ ] On feature branch (NOT main/master)
- [ ] No uncommitted changes (or reviewed)
- [ ] No ignored files staged

### Compilation
- [ ] Maven: `./mvnw clean compile test-compile` → BUILD SUCCESS
- [ ] Node.js: `npm ci` passes for affected projects
- [ ] TypeScript: `npx tsc --noEmit` passes (if TypeScript changes)

### Security
- [ ] No hardcoded secrets in staged files
- [ ] No .env files in staging area
- [ ] Review `git diff` for sensitive data

---

## 📋 Quick Commands

```bash
# Branch check
git branch --show-current

# Maven compile
./mvnw clean compile test-compile

# Node.js projects
cd cypress && npm ci && cd ..
cd playwright && npm ci && cd ..
cd vibium && npm ci && cd ..
cd frontend && npm ci && cd ..

# TypeScript check
cd frontend && npx tsc --noEmit && cd ..

# Smoke tests
./mvnw test -Dtest=SmokeTests -Dcheckstyle.skip=true
```

---

## 🤖 Automated Validation Script

An automated validation script is available: `scripts/validate-pre-commit.sh`

**Usage:**
```bash
./scripts/validate-pre-commit.sh
```

The script automates the Fast Path checks above. See the script for details.

## 🔗 Full Documentation

- **[PRE_PIPELINE_VALIDATION.md](./PRE_PIPELINE_VALIDATION.md)** - Complete validation checklist
- **[AI_WORKFLOW_RULES.md](./AI_WORKFLOW_RULES.md)** - Detailed workflow rules (includes Rule 0: NEVER commit to main)
- **[20251220_NEXT_STEPS_AFTER_PR53.md](../archive/2025-12/20251220_NEXT_STEPS_AFTER_PR53.md)** - Work plan and priorities (archived)

---

## 🚨 Critical Workflow Rules (AI-Assisted Development)

**⚠️ MANDATORY - Never Skip These Steps:**

0. **✅ Protected Test Code Directory** 🚨 **CRITICAL**
   - ❌ **NEVER** modify, delete, or refactor code in `src/test/java/` without explicit approval
   - ✅ **ALWAYS** verify with user **at least TWO times** (before starting AND before committing)
   - ✅ **ALWAYS** explain planned changes and wait for approval
   - ⚠️ **Most code is NOT currently used** but is **PRESERVED for future use**
   - 📍 See `src/test/java/README.md` for detailed rules

1. **✅ Create Feature Branch First**
   - ❌ **NEVER** make changes directly on `main` or `master`
   - ✅ **ALWAYS** create a feature branch: `git checkout -b feature/descriptive-name`
   - ✅ **ALWAYS** pull latest `main` before creating branch: `git checkout main && git pull origin main`

2. **✅ Include Status Legend in Documents**
   - ✅ **ALWAYS** include the status legend in any document being created or modified
   - ✅ Use standardized legend symbols: `[✅]`, `[❌]`, `[🔍]`, `[⚠️]`, `[⏳]`, `[⏭️]`, `[🔒]`
   - ✅ See [PRE_PIPELINE_VALIDATION.md](./PRE_PIPELINE_VALIDATION.md) for legend definitions

3. **✅ Wait for Authorization (EACH ACTION REQUIRES FRESH APPROVAL)**
   - ❌ **NEVER** commit without explicit approval
   - ❌ **NEVER** push without explicit approval
   - ❌ **NEVER** create PR without explicit approval
   - ✅ **ALWAYS** wait for local review and authorization before committing, pushing, or creating PRs
   - ✅ Stage changes and notify when ready for review
   - ⚠️ **CRITICAL**: Authorization is required for **EACH** commit/push/PR action, even if similar actions were approved previously
   - ⚠️ **DO NOT** assume that because you were told to commit/push/create PR before, you can do it again without fresh approval
   - ✅ **ALWAYS** wait for explicit approval after local review before proceeding with any Git action

4. **✅ Test Locally First**
   - ✅ Run validation checks locally before committing
   - ✅ Fix issues locally before pushing
   - ✅ Use `./scripts/validate-pre-commit.sh` to catch issues early

5. **✅ Document Changes**
   - ✅ Update relevant documentation when making changes
   - ✅ Include clear commit messages describing changes
   - ✅ Reference related issues/PRs when applicable
   - ✅ Use date-prefixed naming for new documents (e.g., `20251220_DOCUMENT_NAME.md`)
   - ✅ Living documents (not archived) don't need date prefix (e.g., `VERSION_TRACKING.md`)

---

## ⚠️ Remember

**If ANY check fails**: ❌ **DO NOT commit or push**. Fix the issue and re-run all validation steps.

**Goal**: Catch issues locally before they reach the pipeline. A few minutes of validation can save hours of debugging!
