#!/bin/bash
# scripts/validate-pre-commit.sh
# Pre-commit validation automation script
# Automates common pre-commit checks to prevent pipeline failures

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Track overall status
FAILED=0
WARNINGS=0

# Function to print colored output
print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
    FAILED=$((FAILED + 1))
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
    WARNINGS=$((WARNINGS + 1))
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_section() {
    echo ""
    echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
}

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Start validation
echo ""
print_section "🚀 Pre-Commit Validation Script"
echo ""

# Phase 1: Git Workflow Verification
print_section "Phase 1: Git Workflow Verification"

# 1.1 Branch Verification
print_info "Checking Git branch..."
CURRENT_BRANCH=$(git branch --show-current 2>/dev/null || echo "")

if [ -z "$CURRENT_BRANCH" ]; then
    print_error "Not in a Git repository"
    exit 1
fi

if [ "$CURRENT_BRANCH" = "main" ] || [ "$CURRENT_BRANCH" = "master" ]; then
    print_error "Cannot commit directly to '$CURRENT_BRANCH' branch. Please create a feature branch first."
    exit 1
fi

print_success "On feature branch: $CURRENT_BRANCH"

# 1.2 Check for uncommitted changes
print_info "Checking for uncommitted changes..."
if ! git diff --quiet || ! git diff --cached --quiet; then
    print_warning "There are uncommitted changes. Make sure to review them before committing."
else
    print_success "No uncommitted changes"
fi

# 1.3 Check for ignored files in staging
print_info "Checking for ignored files in staging area..."
STAGED_IGNORED=$(git ls-files --cached --ignored --exclude-standard 2>/dev/null | wc -l | tr -d ' ')
if [ "$STAGED_IGNORED" -gt 0 ]; then
    print_warning "Found $STAGED_IGNORED potentially ignored files in staging area. Review with: git status --ignored"
else
    print_success "No ignored files in staging area"
fi

# Phase 2: Code Quality & Compilation
print_section "Phase 2: Code Quality & Compilation"

# 2.1 Java/Maven Build Verification
if [ -f "pom.xml" ]; then
    print_info "Running Maven compile + test-compile..."
    if ./mvnw clean compile test-compile -Dcheckstyle.skip=true -q >/dev/null 2>&1; then
        print_success "Maven compile + test-compile successful"
    else
        print_error "Maven compile + test-compile failed"
        print_info "Run './mvnw clean compile test-compile' to see detailed errors"
    fi
else
    print_warning "No pom.xml found, skipping Maven checks"
fi

# 2.2 Node.js/npm Projects Verification
print_info "Checking Node.js projects..."

NODE_PROJECTS=("cypress" "playwright" "vibium" "frontend")
NODE_PROJECTS_FOUND=0

for project in "${NODE_PROJECTS[@]}"; do
    if [ -f "$project/package.json" ]; then
        NODE_PROJECTS_FOUND=$((NODE_PROJECTS_FOUND + 1))
        print_info "Checking $project..."
        
        cd "$project"
        
        # Check if package-lock.json exists and is in sync
        if [ -f "package-lock.json" ]; then
            if npm ci --dry-run >/dev/null 2>&1; then
                print_success "$project: package-lock.json is in sync"
            else
                print_error "$project: package-lock.json is out of sync with package.json"
                print_info "Run 'cd $project && npm install' to update package-lock.json"
            fi
        else
            print_warning "$project: No package-lock.json found. Run 'npm install' to create it."
        fi
        
        cd ..
    fi
done

if [ $NODE_PROJECTS_FOUND -eq 0 ]; then
    print_warning "No Node.js projects found"
else
    print_success "Checked $NODE_PROJECTS_FOUND Node.js project(s)"
fi

# 2.3 TypeScript Compilation
print_info "Running TypeScript type checking..."

TYPESCRIPT_PROJECTS=("cypress" "playwright" "vibium" "frontend")
TYPESCRIPT_CHECKED=0
TYPESCRIPT_FAILED=0

for project in "${TYPESCRIPT_PROJECTS[@]}"; do
    if [ -f "$project/tsconfig.json" ]; then
        print_info "Type checking $project..."
        cd "$project"
        
        if command_exists npx; then
            if npx tsc --noEmit >/dev/null 2>&1; then
                print_success "$project: TypeScript type check passed"
                TYPESCRIPT_CHECKED=$((TYPESCRIPT_CHECKED + 1))
            else
                print_error "$project: TypeScript type check failed"
                print_info "Run 'cd $project && npx tsc --noEmit' to see detailed errors"
                TYPESCRIPT_FAILED=$((TYPESCRIPT_FAILED + 1))
            fi
        else
            print_warning "npx not found, skipping TypeScript check for $project"
        fi
        
        cd ..
    fi
done

if [ $TYPESCRIPT_CHECKED -eq 0 ] && [ $TYPESCRIPT_FAILED -eq 0 ]; then
    print_warning "No TypeScript projects found or npx not available"
fi

# 2.4 GitHub Actions Workflow Validation
print_info "Validating GitHub Actions workflow files..."

WORKFLOW_FILES=$(find .github/workflows -name "*.yml" -o -name "*.yaml" 2>/dev/null || echo "")
STAGED_FILES=$(git diff --cached --name-only 2>/dev/null || echo "")

# Check if any workflow files are in the changes
# For pre-push, we might not have staged files, so check all workflows if any exist
SHOULD_CHECK_WORKFLOWS=false
WORKFLOW_FILES_TO_CHECK=""

if [ -n "$STAGED_FILES" ]; then
    # Check if any staged files are workflow files
    for file in $STAGED_FILES; do
        if [[ "$file" =~ \.github/workflows/.*\.(yml|yaml)$ ]]; then
            SHOULD_CHECK_WORKFLOWS=true
            WORKFLOW_FILES_TO_CHECK="$WORKFLOW_FILES_TO_CHECK $file"
        fi
    done
fi

# If no workflow files in staged, but workflows exist, check all (for pre-push validation)
if [ "$SHOULD_CHECK_WORKFLOWS" = false ] && [ -n "$WORKFLOW_FILES" ]; then
    SHOULD_CHECK_WORKFLOWS=true
    WORKFLOW_FILES_TO_CHECK="$WORKFLOW_FILES"
fi

if [ "$SHOULD_CHECK_WORKFLOWS" = true ] && [ -n "$WORKFLOW_FILES_TO_CHECK" ]; then
    if command_exists actionlint; then
        WORKFLOW_ERRORS=0
        TOTAL_YAML_ERRORS=0
        TOTAL_SHELLCHECK_ERRORS=0
        for workflow_file in $WORKFLOW_FILES_TO_CHECK; do
            if [ -f "$workflow_file" ]; then
                if actionlint "$workflow_file" >/tmp/actionlint-output.log 2>&1; then
                    print_success "$workflow_file: Valid"
                else
                    print_error "$workflow_file: Validation failed"
                    
                    # Categorize errors: YAML/syntax errors vs shellcheck warnings
                    YAML_ERRORS=$(grep -v "\[shellcheck\]" /tmp/actionlint-output.log | grep -E "^\.github" | wc -l | tr -d ' ')
                    SHELLCHECK_ERRORS=$(grep "\[shellcheck\]" /tmp/actionlint-output.log | grep -E "^\.github" | wc -l | tr -d ' ')
                    
                    TOTAL_YAML_ERRORS=$((TOTAL_YAML_ERRORS + YAML_ERRORS))
                    TOTAL_SHELLCHECK_ERRORS=$((TOTAL_SHELLCHECK_ERRORS + SHELLCHECK_ERRORS))
                    
                    # Show YAML/syntax errors first (if any)
                    if [ "$YAML_ERRORS" -gt 0 ]; then
                        print_error "  YAML/Syntax errors: $YAML_ERRORS"
                        grep -v "\[shellcheck\]" /tmp/actionlint-output.log | head -20
                    fi
                    
                    # Show shellcheck errors separately (if any)
                    if [ "$SHELLCHECK_ERRORS" -gt 0 ]; then
                        print_error "  Shellcheck issues: $SHELLCHECK_ERRORS"
                        grep "\[shellcheck\]" /tmp/actionlint-output.log | head -10
                        if [ "$SHELLCHECK_ERRORS" -gt 10 ]; then
                            print_info "  ... and $((SHELLCHECK_ERRORS - 10)) more shellcheck issue(s)"
                        fi
                    fi
                    
                    WORKFLOW_ERRORS=$((WORKFLOW_ERRORS + 1))
                fi
            fi
        done
        
        if [ $WORKFLOW_ERRORS -eq 0 ]; then
            print_success "All GitHub Actions workflows are valid"
        else
            print_error "Found $WORKFLOW_ERRORS invalid workflow file(s)"
            if [ "$TOTAL_YAML_ERRORS" -gt 0 ] || [ "$TOTAL_SHELLCHECK_ERRORS" -gt 0 ]; then
                print_info "  Summary: $TOTAL_YAML_ERRORS YAML/syntax error(s), $TOTAL_SHELLCHECK_ERRORS shellcheck issue(s)"
            fi
            print_info "Install actionlint: brew install actionlint (macOS) or download from https://github.com/rhysd/actionlint/releases"
            print_info "Or run manually: actionlint .github/workflows/*.yml"
        fi
    else
        print_warning "actionlint not found - skipping workflow validation"
        print_info "Install actionlint for workflow validation:"
        print_info "  macOS: brew install actionlint"
        print_info "  Linux: Download from https://github.com/rhysd/actionlint/releases"
        print_info "  Or use Docker: docker run --rm -v \"\$PWD\":/work -w /work rhysd/actionlint"
        print_info "Workflow validation is recommended to prevent CI pipeline failures"
    fi
else
    print_info "No workflow files to validate"
fi

# Phase 2.5: Shell Script Validation
print_section "Phase 2.5: Shell Script Validation"

# Check for shell scripts in scripts/ directory
STAGED_SHELL_SCRIPTS=$(git diff --cached --name-only 2>/dev/null | grep -E "^scripts/.*\.sh$" || echo "")
# Only check CI scripts and root scripts (exclude temp scripts)
CI_SHELL_SCRIPTS=$(find scripts/ci scripts -maxdepth 1 -name "*.sh" -type f 2>/dev/null | grep -v "/temp/" || echo "")

if [ -n "$STAGED_SHELL_SCRIPTS" ] || [ "$SHOULD_CHECK_WORKFLOWS" = true ]; then
    SHELL_SCRIPTS_TO_CHECK="$STAGED_SHELL_SCRIPTS"
    if [ -z "$SHELL_SCRIPTS_TO_CHECK" ] && [ "$SHOULD_CHECK_WORKFLOWS" = true ]; then
        # If no specific scripts staged but we're checking workflows, check CI scripts
        SHELL_SCRIPTS_TO_CHECK="$CI_SHELL_SCRIPTS"
    fi
    
    if [ -n "$SHELL_SCRIPTS_TO_CHECK" ]; then
        SHELL_SCRIPT_ERRORS=0
        for script_file in $SHELL_SCRIPTS_TO_CHECK; do
            if [ -f "$script_file" ]; then
                # Check syntax using bash -n (no execution, just syntax check)
                if bash -n "$script_file" >/tmp/bash-syntax-check.log 2>&1; then
                    print_success "$script_file: Syntax valid"
                else
                    print_error "$script_file: Syntax error"
                    cat /tmp/bash-syntax-check.log | head -10
                    if [ $(wc -l < /tmp/bash-syntax-check.log 2>/dev/null | tr -d ' ') -gt 10 ]; then
                        print_info "  ... and more errors (see full output above)"
                    fi
                    SHELL_SCRIPT_ERRORS=$((SHELL_SCRIPT_ERRORS + 1))
                fi
            fi
        done
        
        if [ $SHELL_SCRIPT_ERRORS -eq 0 ]; then
            print_success "All shell scripts have valid syntax"
        else
            print_error "Found $SHELL_SCRIPT_ERRORS shell script(s) with syntax errors"
            print_info "Fix syntax errors before committing"
        fi
    else
        print_info "No shell scripts to validate"
    fi
else
    print_info "No shell scripts to validate"
fi

# Phase 3: Security Checks
print_section "Phase 3: Security & Secrets Verification"

# 3.1 Check for common secret patterns in staged files
print_info "Scanning staged files for potential secrets..."

SECRET_PATTERNS=(
    "password\s*=\s*['\"][^'\"]+['\"]"
    "api[_-]?key\s*=\s*['\"][^'\"]+['\"]"
    "secret\s*=\s*['\"][^'\"]+['\"]"
    "token\s*=\s*['\"][^'\"]+['\"]"
    "aws[_-]?access[_-]?key"
    "aws[_-]?secret[_-]?key"
    "private[_-]?key"
    "BEGIN\s+(RSA\s+)?PRIVATE\s+KEY"
)

STAGED_FILES=$(git diff --cached --name-only 2>/dev/null || echo "")
SECRETS_FOUND=0

if [ -n "$STAGED_FILES" ]; then
    for file in $STAGED_FILES; do
        # Skip binary files and common non-code files
        if [[ "$file" =~ \.(jar|zip|tar|gz|png|jpg|jpeg|gif|ico|pdf|woff|woff2|ttf|eot)$ ]]; then
            continue
        fi
        
        # Skip node_modules and build directories
        if [[ "$file" =~ (node_modules|target|dist|build|\.git)/ ]]; then
            continue
        fi
        
        if [ -f "$file" ]; then
            for pattern in "${SECRET_PATTERNS[@]}"; do
                if grep -qiE "$pattern" "$file" 2>/dev/null; then
                    print_warning "Potential secret pattern found in: $file"
                    SECRETS_FOUND=$((SECRETS_FOUND + 1))
                    # Only report once per file
                    break
                fi
            done
        fi
    done
    
    if [ $SECRETS_FOUND -eq 0 ]; then
        print_success "No obvious secret patterns found in staged files"
    else
        print_warning "Found $SECRETS_FOUND file(s) with potential secret patterns. Please review carefully."
    fi
else
    print_info "No staged files to check"
fi

# 3.2 Check for .env files in staging
print_info "Checking for .env files in staging..."
STAGED_ENV_FILES=$(git diff --cached --name-only 2>/dev/null | grep -E "\.env$|\.env\." || echo "")
if [ -n "$STAGED_ENV_FILES" ]; then
    print_error "Found .env files in staging area:"
    echo "$STAGED_ENV_FILES" | while read -r file; do
        print_error "  - $file"
    done
    print_info ".env files should not be committed. Add them to .gitignore if needed."
else
    print_success "No .env files in staging area"
fi

# Summary
print_section "Validation Summary"

if [ $FAILED -eq 0 ] && [ $WARNINGS -eq 0 ]; then
    print_success "All checks passed! Ready to commit."
    echo ""
    exit 0
elif [ $FAILED -eq 0 ]; then
    print_warning "Validation completed with $WARNINGS warning(s). Review warnings before committing."
    echo ""
    exit 0
else
    print_error "Validation failed with $FAILED error(s) and $WARNINGS warning(s)."
    print_info "Please fix the errors before committing."
    echo ""
    exit 1
fi
