#!/bin/bash
# scripts/install-git-hooks.sh
# Installs Git pre-commit hooks for validation

set -e

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

cd "$PROJECT_ROOT"

GIT_HOOKS_DIR=".git/hooks"
PRE_COMMIT_HOOK="$GIT_HOOKS_DIR/pre-commit"

echo -e "${YELLOW}📦 Installing Git pre-commit hook...${NC}"

# Check if .git directory exists
if [ ! -d ".git" ]; then
    echo "❌ Error: .git directory not found. Are you in a Git repository?"
    exit 1
fi

# Create hooks directory if it doesn't exist
mkdir -p "$GIT_HOOKS_DIR"

# Check if pre-commit hook already exists
if [ -f "$PRE_COMMIT_HOOK" ] && [ ! -L "$PRE_COMMIT_HOOK" ]; then
    echo -e "${YELLOW}⚠️  Pre-commit hook already exists. Backing up to pre-commit.backup${NC}"
    mv "$PRE_COMMIT_HOOK" "$PRE_COMMIT_HOOK.backup"
fi

# Create the pre-commit hook
cat > "$PRE_COMMIT_HOOK" << 'HOOK_EOF'
#!/bin/bash
# .git/hooks/pre-commit
# Git pre-commit hook for code formatting
# Formats code automatically if code files are being committed (no compilation/validation)
# Can be bypassed with: git commit --no-verify

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Get the project root
PROJECT_ROOT="$(git rev-parse --show-toplevel)"
cd "$PROJECT_ROOT"

# Get staged files
STAGED_FILES=$(git diff --cached --name-only 2>/dev/null || true)

# Filter out documentation files (same pattern as CI pipeline)
# Documentation file extensions: .md, .log, .txt, .rst, .adoc
CODE_FILES=$(echo "$STAGED_FILES" | grep -v -E '\.(md|log|txt|rst|adoc)$' || true)

# If only documentation files changed, skip formatting
if [ -z "$CODE_FILES" ] && [ -n "$STAGED_FILES" ]; then
    echo -e "${GREEN}✅ Documentation-only changes - skipping code formatting${NC}"
    exit 0
fi

# Code files changed - format code only (skip compilation and validation)
if [ -n "$CODE_FILES" ]; then
    echo -e "${BLUE}📝 Code files changed - formatting code...${NC}"
    
    if [ -f "scripts/format-code.sh" ]; then
        chmod +x scripts/format-code.sh
        if ./scripts/format-code.sh --skip-compilation --skip-quality-checks; then
            # Stage any auto-fixed files
            git add -u
            echo -e "${GREEN}✅ Code formatting completed${NC}"
        else
            echo -e "${RED}❌ Code formatting failed${NC}"
            echo -e "${YELLOW}💡 You can bypass this hook with: git commit --no-verify${NC}"
            exit 1
        fi
    else
        echo -e "${YELLOW}⚠️  format-code.sh not found: scripts/format-code.sh${NC}"
        echo -e "${YELLOW}   Skipping code formatting${NC}"
    fi
fi

exit 0
HOOK_EOF

chmod +x "$PRE_COMMIT_HOOK"

# Create pre-push hook
PRE_PUSH_HOOK="$GIT_HOOKS_DIR/pre-push"

echo -e "${YELLOW}📦 Installing Git pre-push hook...${NC}"

# Check if pre-push hook already exists
if [ -f "$PRE_PUSH_HOOK" ] && [ ! -L "$PRE_PUSH_HOOK" ]; then
    echo -e "${YELLOW}⚠️  Pre-push hook already exists. Backing up to pre-push.backup${NC}"
    mv "$PRE_PUSH_HOOK" "$PRE_PUSH_HOOK.backup"
fi

# Create the pre-push hook
cat > "$PRE_PUSH_HOOK" << 'HOOK_EOF'
#!/bin/bash
# .git/hooks/pre-push
# Git pre-push hook for code quality verification
# Detects documentation-only changes and skips code quality checks
# Can be bypassed with: git push --no-verify

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${YELLOW}🔍 Verifying code quality before push...${NC}"
echo ""

# Get the project root
PROJECT_ROOT="$(git rev-parse --show-toplevel)"
cd "$PROJECT_ROOT"

# Get current branch name
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)

# Get all changed files in this push
# Try multiple methods to get changed files (handles different git scenarios)
CHANGED_FILES=""
if git rev-parse --verify "origin/${CURRENT_BRANCH}" >/dev/null 2>&1; then
    # Branch exists on remote - compare with remote
    CHANGED_FILES=$(git diff --name-only "origin/${CURRENT_BRANCH}" HEAD 2>/dev/null || true)
elif [ -n "$(git log --oneline -1 HEAD@{1} 2>/dev/null)" ]; then
    # Compare with previous HEAD
    CHANGED_FILES=$(git diff --name-only HEAD@{1} HEAD 2>/dev/null || true)
else
    # Fallback: check staged files
    CHANGED_FILES=$(git diff --cached --name-only 2>/dev/null || true)
fi

# If no changed files detected, check staged files as fallback
if [ -z "$CHANGED_FILES" ]; then
    CHANGED_FILES=$(git diff --cached --name-only 2>/dev/null || true)
fi

# Filter out documentation files (same pattern as CI pipeline)
# Documentation file extensions: .md, .log, .txt, .rst, .adoc
CODE_FILES=$(echo "$CHANGED_FILES" | grep -v -E '\.(md|log|txt|rst|adoc)$' || true)

# If only documentation files changed, skip code quality checks
if [ -z "$CODE_FILES" ] && [ -n "$CHANGED_FILES" ]; then
    echo -e "${GREEN}✅ Documentation-only changes detected${NC}"
    echo -e "${BLUE}   Changed files:${NC}"
    echo "$CHANGED_FILES" | sed 's/^/   - /'
    echo ""
    echo -e "${GREEN}   Skipping code quality checks (CI pipeline will handle validation)${NC}"
    exit 0
fi

# Code files changed - run all validation checks (formatting already done in pre-commit)
if [ -n "$CODE_FILES" ]; then
    echo -e "${BLUE}📝 Code files changed - running validation checks...${NC}"
    echo -e "${BLUE}   (Formatting was already done in pre-commit hook)${NC}"
    echo ""
    
    # Step 1: Run code quality checks (Checkstyle and PMD)
    # Note: Formatting is skipped since it was already done in pre-commit
    if [ -f "scripts/format-code.sh" ]; then
        echo -e "${BLUE}🔍 Running code quality checks (Checkstyle & PMD)...${NC}"
        chmod +x scripts/format-code.sh
        # Use --ci-mode to verify code quality without formatting or compilation
        if ./scripts/format-code.sh --ci-mode; then
            echo -e "${GREEN}✅ Code quality checks passed${NC}"
            echo ""
        else
            echo -e "${RED}❌ Code quality checks failed${NC}"
            echo -e "${YELLOW}💡 You can bypass this hook with: git push --no-verify${NC}"
            exit 1
        fi
    else
        echo -e "${YELLOW}⚠️  format-code.sh not found: scripts/format-code.sh${NC}"
        echo -e "${YELLOW}   Skipping code quality checks${NC}"
    fi
    
    # Step 2: Run comprehensive validation checks
    if [ -f "scripts/validate-pre-commit.sh" ]; then
        echo -e "${BLUE}🔍 Running comprehensive validation checks...${NC}"
        chmod +x scripts/validate-pre-commit.sh
        if ./scripts/validate-pre-commit.sh; then
            echo -e "${GREEN}✅ Validation checks passed${NC}"
            echo ""
        else
            echo -e "${RED}❌ Validation checks failed${NC}"
            echo -e "${YELLOW}💡 You can bypass this hook with: git push --no-verify${NC}"
            exit 1
        fi
    else
        echo -e "${YELLOW}⚠️  validate-pre-commit.sh not found, skipping validation${NC}"
    fi
    
    # Step 3: Run dependency version validation
    if [ -f "scripts/validate-dependency-versions.sh" ]; then
        echo -e "${BLUE}🔍 Validating dependency versions...${NC}"
        chmod +x scripts/validate-dependency-versions.sh
        if ./scripts/validate-dependency-versions.sh; then
            echo -e "${GREEN}✅ Version validation passed${NC}"
            echo ""
        else
            echo -e "${RED}❌ Version validation failed${NC}"
            echo -e "${YELLOW}💡 Fix version mismatches before pushing${NC}"
            echo -e "${YELLOW}💡 You can bypass this hook with: git push --no-verify${NC}"
            exit 1
        fi
    else
        echo -e "${YELLOW}⚠️  validate-dependency-versions.sh not found, skipping version validation${NC}"
    fi
fi

echo -e "${GREEN}✅ Pre-push verification passed${NC}"
exit 0
HOOK_EOF

chmod +x "$PRE_PUSH_HOOK"

# Create post-checkout hook (auto-installs hooks on checkout)
POST_CHECKOUT_HOOK="$GIT_HOOKS_DIR/post-checkout"

echo -e "${YELLOW}📦 Installing Git post-checkout hook (auto-installs hooks)...${NC}"

# Check if post-checkout hook already exists
if [ -f "$POST_CHECKOUT_HOOK" ] && [ ! -L "$POST_CHECKOUT_HOOK" ]; then
    echo -e "${YELLOW}⚠️  Post-checkout hook already exists. Backing up to post-checkout.backup${NC}"
    mv "$POST_CHECKOUT_HOOK" "$POST_CHECKOUT_HOOK.backup"
fi

# Create the post-checkout hook
cat > "$POST_CHECKOUT_HOOK" << 'HOOK_EOF'
#!/bin/bash
# .git/hooks/post-checkout
# Git post-checkout hook to auto-install Git hooks
# Runs automatically after git checkout/clone

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Get the project root
PROJECT_ROOT="$(git rev-parse --show-toplevel 2>/dev/null)"
if [ -z "$PROJECT_ROOT" ]; then
    # Not in a git repo or during initial clone, skip silently
    exit 0
fi

cd "$PROJECT_ROOT"

# Check if install script exists
if [ ! -f "scripts/install-git-hooks.sh" ]; then
    # Script not found, skip silently (might be during initial setup)
    exit 0
fi

# Check if hooks are already installed and up to date
# We check if both pre-commit and pre-push exist and are executable
if [ -x ".git/hooks/pre-commit" ] && [ -x ".git/hooks/pre-push" ] && [ -x ".git/hooks/post-checkout" ]; then
    # All hooks exist, check if they're recent (installed in last 24 hours)
    # or if install script is newer (meaning hooks might need update)
    HOOK_AGE=$(find .git/hooks/pre-commit .git/hooks/pre-push .git/hooks/post-checkout -type f -mtime -1 2>/dev/null | wc -l)
    SCRIPT_AGE=$(find scripts/install-git-hooks.sh -type f -mtime -1 2>/dev/null | wc -l)
    
    # If hooks are recent OR install script is old, assume hooks are up to date
    if [ "$HOOK_AGE" -gt 0 ] || [ "$SCRIPT_AGE" -eq 0 ]; then
        # Hooks are up to date, skip installation
        exit 0
    fi
fi

# Hooks don't exist or need update, install them
# This will install all hooks including this post-checkout hook itself
echo -e "${YELLOW}🔧 Auto-installing Git hooks...${NC}"
chmod +x scripts/install-git-hooks.sh
if ./scripts/install-git-hooks.sh > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Git hooks auto-installed successfully${NC}"
else
    # Installation failed, but don't block checkout
    # User can manually run install-git-hooks.sh if needed
    exit 0
fi
HOOK_EOF

chmod +x "$POST_CHECKOUT_HOOK"

echo -e "${GREEN}✅ Git hooks installed successfully${NC}"
echo ""
echo "Installed hooks:"
echo "  ✅ Pre-commit hook: Formats code automatically (skips compilation/validation - runs on push)"
echo "  ✅ Pre-push hook: Formats, compiles, and validates code (skips for documentation-only changes)"
echo "  ✅ Post-checkout hook: Auto-installs hooks on checkout/clone"
echo ""
echo "To bypass hooks, use:"
echo "  - Pre-commit: git commit --no-verify"
echo "  - Pre-push: git push --no-verify"
echo ""
echo "To uninstall, run:"
echo "  - rm .git/hooks/pre-commit"
echo "  - rm .git/hooks/pre-push"
echo "  - rm .git/hooks/post-checkout"
