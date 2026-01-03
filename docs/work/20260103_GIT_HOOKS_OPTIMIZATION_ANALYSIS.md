# Git Hooks Optimization Analysis

## Current Behavior

### Pre-Commit Hook
**Location**: `.git/hooks/pre-commit`

**What it does**:
1. Detects if code files changed (filters out docs)
2. Runs `scripts/format-code.sh --skip-compilation`
   - Formats code (Prettier, Spotless, Google Java Format)
   - Removes unused imports
   - Runs Checkstyle and PMD checks
   - **Skips compilation** (to save time on commit)
3. Stages auto-fixed files

**Time**: ~20-40 seconds for code changes

### Pre-Push Hook
**Location**: `.git/hooks/pre-push`

**What it does**:
1. Detects if code files changed (filters out docs)
2. **Step 1**: Runs `scripts/format-code.sh` (full, with compilation)
   - Formats code (Prettier, Spotless, Google Java Format) - **DUPLICATE**
   - Removes unused imports - **DUPLICATE**
   - Runs Checkstyle and PMD checks - **DUPLICATE**
   - **Compiles code** (Maven compile + test-compile)
3. **Step 2**: Runs `scripts/validate-pre-commit.sh`
   - Checks Git branch (not main/master)
   - **Compiles code again** (Maven compile + test-compile) - **DUPLICATE**
   - Runs Node.js checks
   - Validates GitHub Actions workflows
   - Validates shell scripts

**Time**: ~30-60 seconds for code changes

---

## Duplication Identified

### 1. Code Formatting (DUPLICATE)
- **Pre-commit**: Formats code with `format-code.sh --skip-compilation`
- **Pre-push**: Formats code again with `format-code.sh` (full)
- **Impact**: Code is formatted twice, wasting 20-40 seconds

### 2. Import Removal (DUPLICATE)
- **Pre-commit**: Removes unused imports via Spotless
- **Pre-push**: Removes unused imports again via Spotless
- **Impact**: Redundant import processing

### 3. Code Quality Checks (DUPLICATE)
- **Pre-commit**: Runs Checkstyle and PMD
- **Pre-push**: Runs Checkstyle and PMD again
- **Impact**: Redundant quality checks

### 4. Compilation (DUPLICATE)
- **Pre-push Step 1**: Compiles via `format-code.sh`
- **Pre-push Step 2**: Compiles again via `validate-pre-commit.sh`
- **Impact**: Code is compiled twice in pre-push, wasting 10-20 seconds

---

## Optimization Strategy

### Recommended Approach: Skip Formatting in Pre-Push

**Rationale**:
- Code is already formatted in pre-commit
- If formatting is needed, it should be caught before commit
- Pre-push should focus on validation and compilation

### Proposed Changes

#### Pre-Commit Hook (Keep as-is)
- ✅ Format code (fast, no compilation)
- ✅ Stage auto-fixed files
- **Time**: ~20-40 seconds

#### Pre-Push Hook (Optimized)
- ❌ **Remove**: `format-code.sh` call (already formatted in pre-commit)
- ✅ **Keep**: `validate-pre-commit.sh` (compilation + validation)
- **Time**: ~15-30 seconds (saves 20-40 seconds)

**Benefits**:
- Eliminates duplicate formatting (saves 20-40 seconds)
- Eliminates duplicate import removal
- Eliminates duplicate Checkstyle/PMD checks
- Still ensures code compiles and validates before push
- Faster push experience

**Trade-offs**:
- If someone bypasses pre-commit with `--no-verify`, code won't be formatted before push
  - **Mitigation**: `validate-pre-commit.sh` can detect formatting issues and fail

---

## Alternative Approach: Conditional Formatting

If we want to keep formatting in pre-push as a safety net:

### Option A: Check if files changed since last commit
```bash
# In pre-push hook
if git diff --quiet HEAD@{1} HEAD -- '*.java' '*.js' '*.ts' '*.py' 2>/dev/null; then
    echo "No code files changed since last commit - skipping formatting"
else
    echo "Code files changed - formatting..."
    ./scripts/format-code.sh
fi
```

### Option B: Use a flag file
```bash
# In pre-commit hook: touch .formatted
# In pre-push hook: check if .formatted exists and is recent
```

### Option C: Skip formatting, only validate
- Pre-push only runs `validate-pre-commit.sh`
- Validation script can check formatting and fail if needed
- Faster, but requires validation script to detect formatting issues

---

## Recommended Solution

### Optimize Pre-Push Hook: Remove Formatting, Keep All Validation

**Change pre-push hook to**:
1. Detect code files changed
2. Run code quality checks (Checkstyle & PMD) using `format-code.sh --ci-mode`
   - Verifies code quality without formatting or compilation
   - Ensures Checkstyle and PMD checks are still performed
3. Run `validate-pre-commit.sh` (includes compilation and all other validations)
4. Skip formatting entirely (already done in pre-commit)

**Rationale**:
- Code should be formatted before commit (pre-commit hook)
- Pre-push should validate and compile (catch issues before push)
- Faster push experience (saves 20-40 seconds)
- Clear separation of concerns:
  - **Pre-commit**: Format code (Prettier, Spotless, Google Java Format)
  - **Pre-push**: Validate code (Checkstyle, PMD, Compilation, Node.js, TypeScript, GitHub Actions, Shell scripts, Security)
- **No steps lost**: All validation steps are preserved

**Implementation**:
```bash
# In pre-push hook, replace:
# Step 1: Format code (REMOVE - already done in pre-commit)
if [ -f "scripts/format-code.sh" ]; then
    ./scripts/format-code.sh  # REMOVE THIS
    ...
fi

# With:
# Step 1: Run code quality checks only (no formatting, no compilation)
if [ -f "scripts/format-code.sh" ]; then
    ./scripts/format-code.sh --ci-mode  # Checkstyle & PMD only
    ...
fi

# Step 2: Run comprehensive validation (KEEP - includes compilation)
if [ -f "scripts/validate-pre-commit.sh" ]; then
    ./scripts/validate-pre-commit.sh  # KEEP THIS
    ...
fi
```

---

## Time Savings

### Current (with duplication)
- **Pre-commit**: 20-40 seconds
- **Pre-push**: 30-60 seconds
- **Total**: 50-100 seconds

### Optimized (no duplication)
- **Pre-commit**: 20-40 seconds (unchanged)
- **Pre-push**: 15-30 seconds (removed formatting)
- **Total**: 35-70 seconds

**Savings**: ~15-30 seconds per push (30-40% faster)

---

## Additional Optimization: Remove Duplicate Compilation

The `validate-pre-commit.sh` script also compiles code. Since `format-code.sh` already compiles when run without `--skip-compilation`, we could:

1. **Option 1**: Remove compilation from `format-code.sh` in pre-push (use `--skip-compilation`)
2. **Option 2**: Remove compilation from `validate-pre-commit.sh` (rely on format-code.sh)
3. **Option 3**: Keep both but add a flag to skip compilation in validate-pre-commit.sh if already compiled

**Recommendation**: If we remove formatting from pre-push, then `validate-pre-commit.sh` should keep compilation (it's the only place it happens).

---

## Summary

### Current Issues
1. ✅ Code formatting happens twice (pre-commit + pre-push)
2. ✅ Import removal happens twice
3. ✅ Checkstyle/PMD checks happen twice
4. ✅ Compilation happens twice in pre-push (format-code.sh + validate-pre-commit.sh)

### Recommended Fix
1. **Keep pre-commit as-is**: Format code (fast, no compilation)
   - Prettier (formatting)
   - Spotless (import removal and reordering)
   - Google Java Format (line length)
   - Checkstyle (code quality - warning only)
   - PMD (code analysis - warning only)
   - **No compilation** (saves time)

2. **Optimize pre-push**: 
   - Remove formatting (already done in pre-commit)
   - Run `format-code.sh --ci-mode` for Checkstyle & PMD verification only
   - Run `validate-pre-commit.sh` for all other validations
   - **Result**: All validation steps preserved, no duplication

3. **All Steps Preserved**:
   - ✅ Prettier formatting (pre-commit only)
   - ✅ Spotless import removal (pre-commit only)
   - ✅ Google Java Format (pre-commit only)
   - ✅ Checkstyle verification (pre-push)
   - ✅ PMD verification (pre-push)
   - ✅ Maven compilation (pre-push via validate-pre-commit.sh)
   - ✅ Node.js validation (pre-push via validate-pre-commit.sh)
   - ✅ TypeScript validation (pre-push via validate-pre-commit.sh)
   - ✅ GitHub Actions validation (pre-push via validate-pre-commit.sh)
   - ✅ Shell script validation (pre-push via validate-pre-commit.sh)
   - ✅ Security checks (pre-push via validate-pre-commit.sh)

### Expected Improvement
- **Time saved**: 20-40 seconds per push (formatting removed)
- **Reduced duplication**: 4 areas of duplication eliminated
- **Clearer workflow**: Format on commit, validate on push
- **No steps lost**: All validation steps are preserved

