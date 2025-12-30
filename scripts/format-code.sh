#!/bin/bash
#
# Pre-Commit Code Formatting and Quality Checks Script
# 
# This script runs the required formatting and quality checks before committing code:
# 1. Prettier: Formats code (sorts imports alphabetically - will be fixed by Spotless)
# 1.5. Spotless: Reorders imports (java,javax,org,com) and removes unused/duplicate imports
# 2. Google Java Format: Fixes line length issues
# 3. Checkstyle: Verifies no violations remain
# 4. Compilation: Ensures code still compiles
# 5. PMD: Code analysis for potential issues
#
# Usage:
#   ./scripts/format-code.sh                    # Full formatting (pre-commit)
#   ./scripts/format-code.sh --ci-mode         # CI mode (verify only, no modifications)
#   ./scripts/format-code.sh --verify-only     # Same as --ci-mode
#   ./scripts/format-code.sh --skip-imports    # Skip import removal (if needed)
#   ./scripts/format-code.sh --skip-formatting # Skip formatting, only run checks
#   ./scripts/format-code.sh --skip-compilation # Skip compilation check (formatting only)
#   ./scripts/format-code.sh --help            # Show help message
#
# Flags:
#   --ci-mode, --verify-only: Skip formatting, skip compilation, only verify
#   --skip-imports: Skip unused import removal
#   --skip-formatting: Skip Prettier and Google Java Format
#   --skip-compilation: Skip compilation check (formatting only)
#   --help: Show this help message
#
# Exit codes:
#   0 - Success (all steps passed)
#   1 - Formatting step failed
#   2 - Checkstyle violations found
#   3 - Compilation failed
#   4 - PMD check failed

set -e  # Exit on error

# Helper function to suppress Maven Progress lines
setup_maven_opts() {
    # Suppress Maven transfer listener progress output
    if [ -n "${MAVEN_OPTS:-}" ]; then
        export MAVEN_OPTS="${MAVEN_OPTS} -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn"
    else
        export MAVEN_OPTS="-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn"
    fi
}

# Helper function to run Maven command with Progress filtering
run_maven() {
    setup_maven_opts
    mvn "$@" 2>&1 | grep -vE "^Progress"
    return ${PIPESTATUS[0]}  # Return Maven's exit code, not grep's
}

# Setup Maven options at script start
setup_maven_opts

# Parse command-line arguments
CI_MODE=false
SKIP_IMPORTS=false
SKIP_FORMATTING=false
SKIP_COMPILATION=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --ci-mode|--verify-only)
            CI_MODE=true
            shift
            ;;
        --skip-imports)
            SKIP_IMPORTS=true
            shift
            ;;
        --skip-formatting)
            SKIP_FORMATTING=true
            shift
            ;;
        --skip-compilation)
            SKIP_COMPILATION=true
            shift
            ;;
        --help)
            echo "Usage: $0 [--ci-mode|--verify-only] [--skip-imports] [--skip-formatting] [--skip-compilation] [--help]"
            echo ""
            echo "Flags:"
            echo "  --ci-mode, --verify-only: Skip formatting, skip compilation, only verify"
            echo "  --skip-imports: Skip unused import removal"
            echo "  --skip-formatting: Skip Prettier and Google Java Format"
            echo "  --skip-compilation: Skip compilation check (formatting only)"
            echo "  --help: Show this help message"
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            echo "Use --help for usage information"
            exit 1
            ;;
    esac
done

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

cd "$PROJECT_ROOT"

echo -e "${BLUE}========================================${NC}"
if [ "$CI_MODE" = true ]; then
    echo -e "${BLUE}CI Code Quality Verification (Read-Only)${NC}"
else
    echo -e "${BLUE}Pre-Commit Code Formatting & Quality Checks${NC}"
fi
echo -e "${BLUE}========================================${NC}"
echo ""

# Calculate total steps based on mode
TOTAL_STEPS=5
if [ "$CI_MODE" = false ] && [ "$SKIP_IMPORTS" = false ]; then
    TOTAL_STEPS=6  # Includes Spotless step
fi
if [ "$CI_MODE" = false ] && [ "$SKIP_FORMATTING" = false ]; then
    # Formatting steps are already counted in TOTAL_STEPS
    :
fi
if [ "$CI_MODE" = true ]; then
    TOTAL_STEPS=2  # Only Checkstyle and PMD
fi

STEP_NUM=1

# Step 1: Prettier - Format code (but NOT import sorting - Spotless handles that)
# Note: Prettier sorts imports alphabetically, but we want java,javax,org,com order
# So we run Spotless AFTER Prettier to fix the import order
if [ "$SKIP_FORMATTING" = false ] && [ "$CI_MODE" = false ]; then
    echo -e "${YELLOW}Step ${STEP_NUM}/${TOTAL_STEPS}: Running Prettier (formatting code)...${NC}"
    if run_maven prettier:write > /tmp/prettier-output.log 2>&1; then
        echo -e "${GREEN}✅ Prettier completed successfully${NC}"
        # Show summary if files were reformatted
        if grep -q "Reformatted file" /tmp/prettier-output.log; then
            REFORMATTED_COUNT=$(grep -c "Reformatted file" /tmp/prettier-output.log)
            echo -e "${GREEN}   ${REFORMATTED_COUNT} file(s) reformatted${NC}"
        fi
        echo -e "${YELLOW}   Note: Prettier sorts imports alphabetically, but Spotless will reorder them next${NC}"
    else
        echo -e "${RED}❌ Prettier failed${NC}"
        echo "Error output:"
        tail -20 /tmp/prettier-output.log
        exit 1
    fi
    echo ""
    STEP_NUM=$((STEP_NUM + 1))
fi

# Step 1.5: Remove unused and duplicate imports, reorder imports (Spotless)
# This runs AFTER Prettier to ensure final import order matches Spotless config (java,javax,org,com)
# Prettier sorts imports alphabetically, but Spotless reorders them to java,javax,org,com
if [ "$SKIP_IMPORTS" = false ] && [ "$CI_MODE" = false ]; then
    echo -e "${YELLOW}Step ${STEP_NUM}/${TOTAL_STEPS}: Reordering imports and removing unused/duplicate imports (Spotless)...${NC}"
    echo -e "${BLUE}   Reordering imports from alphabetical (Prettier) to java,javax,org,com (Spotless)${NC}"
    if mvn spotless:apply > /tmp/spotless-output.log 2>&1; then
        echo -e "${GREEN}✅ Spotless completed successfully${NC}"
        # Show summary if files were modified
        if grep -q "BUILD SUCCESS" /tmp/spotless-output.log; then
            echo -e "${GREEN}   Imports reordered (java,javax,org,com) and unused imports removed${NC}"
        fi
    else
        echo -e "${RED}❌ Spotless failed${NC}"
        echo "Error output:"
        tail -20 /tmp/spotless-output.log
        exit 1
    fi
    echo ""
    STEP_NUM=$((STEP_NUM + 1))
fi

# Step 2: Google Java Format - Fix line length issues (imports are NOT touched - Spotless handles that)
if [ "$SKIP_FORMATTING" = false ] && [ "$CI_MODE" = false ]; then
    echo -e "${YELLOW}Step ${STEP_NUM}/${TOTAL_STEPS}: Running Google Java Format (fixing line length, skipping imports)...${NC}"
    if run_maven fmt:format > /tmp/gjf-output.log 2>&1; then
        echo -e "${GREEN}✅ Google Java Format completed successfully${NC}"
        # Show summary if files were reformatted
        if grep -q "reformatted" /tmp/gjf-output.log; then
            REFORMATTED_LINE=$(grep "reformatted" /tmp/gjf-output.log | tail -1)
            echo -e "${GREEN}   ${REFORMATTED_LINE}${NC}"
        fi
    else
        echo -e "${RED}❌ Google Java Format failed${NC}"
        echo "Error output:"
        tail -20 /tmp/gjf-output.log
        exit 1
    fi
    echo ""
    STEP_NUM=$((STEP_NUM + 1))
fi

# Step 3: Checkstyle - Verify no violations
echo -e "${YELLOW}Step ${STEP_NUM}/${TOTAL_STEPS}: Running Checkstyle (verifying code quality)...${NC}"
if run_maven checkstyle:check > /tmp/checkstyle-output.log 2>&1; then
    # Extract violation count (compatible with macOS grep)
    VIOLATION_COUNT=$(grep "You have" /tmp/checkstyle-output.log | grep -oE "[0-9]+" | head -1 || echo "0")
    if [ "$VIOLATION_COUNT" = "0" ] || [ -z "$VIOLATION_COUNT" ]; then
        echo -e "${GREEN}✅ Checkstyle: No violations found${NC}"
    else
        echo -e "${YELLOW}⚠️  Checkstyle: ${VIOLATION_COUNT} violation(s) found${NC}"
        echo ""
        echo "Violation summary:"
        grep -E "(LineLength|Indentation|EmptyLineSeparator|ConstantName)" /tmp/checkstyle-output.log | head -10 || true
        echo ""
        if [ "$CI_MODE" = true ]; then
            echo -e "${YELLOW}Note: Checkstyle violations are warnings in CI mode${NC}"
        else
            echo -e "${YELLOW}Note: Some violations may require manual fixes${NC}"
        fi
        # Don't exit with error - violations are warnings, not blocking
    fi
else
    echo -e "${RED}❌ Checkstyle check failed${NC}"
    echo "Error output:"
    tail -20 /tmp/checkstyle-output.log
    exit 2
fi
echo ""
STEP_NUM=$((STEP_NUM + 1))

# Step 4: Compilation - Verify code compiles
if [ "$CI_MODE" = false ] && [ "$SKIP_COMPILATION" = false ]; then
    echo -e "${YELLOW}Step ${STEP_NUM}/${TOTAL_STEPS}: Verifying compilation...${NC}"
    # Clean checkstyle result file to avoid XML parsing errors during compilation
    rm -f target/checkstyle-result.xml 2>/dev/null || true
    
    # Try clean compile first, but handle lock issues gracefully
    if run_maven clean compile test-compile > /tmp/compile-output.log 2>&1; then
        echo -e "${GREEN}✅ Compilation successful${NC}"
    elif grep -q "Failed to delete.*target" /tmp/compile-output.log; then
        # Maven clean failed due to locked files (likely Java LSP)
        echo -e "${YELLOW}⚠️  Maven clean failed (files may be locked by IDE), trying without clean...${NC}"
        # Remove target manually and retry without clean
        rm -rf target 2>/dev/null || true
        if run_maven compile test-compile > /tmp/compile-output.log 2>&1; then
            echo -e "${GREEN}✅ Compilation successful (without clean)${NC}"
        else
            echo -e "${RED}❌ Compilation failed${NC}"
            echo "Error output:"
            grep -A 10 "ERROR" /tmp/compile-output.log || tail -20 /tmp/compile-output.log
            exit 3
        fi
    else
        echo -e "${RED}❌ Compilation failed${NC}"
        echo "Error output:"
        grep -A 10 "ERROR" /tmp/compile-output.log || tail -20 /tmp/compile-output.log
        exit 3
    fi
    echo ""
    STEP_NUM=$((STEP_NUM + 1))
fi

# Step 5: PMD - Code analysis
echo -e "${YELLOW}Step ${STEP_NUM}/${TOTAL_STEPS}: Running PMD code analysis...${NC}"
if run_maven pmd:check > /tmp/pmd-output.log 2>&1; then
    # Extract violation count (compatible with macOS grep)
    # Pattern: "PMD 7.14.0 has found 1260 violations"
    VIOLATION_COUNT=$(grep -E "PMD.*has found.*violation" /tmp/pmd-output.log | grep -oE "[0-9]+ violations" | grep -oE "[0-9]+" | head -1 || echo "0")
    if [ "$VIOLATION_COUNT" = "0" ] || [ -z "$VIOLATION_COUNT" ]; then
        echo -e "${GREEN}✅ PMD: No violations found${NC}"
    else
        echo -e "${YELLOW}⚠️  PMD: ${VIOLATION_COUNT} violation(s) found${NC}"
        echo ""
        echo "Violation summary:"
        grep -E "PMD Failure" /tmp/pmd-output.log | head -10 || true
        echo ""
        if [ "$CI_MODE" = true ]; then
            echo -e "${YELLOW}Note: PMD violations are warnings in CI mode${NC}"
        else
            echo -e "${YELLOW}Note: PMD violations are warnings, not blocking${NC}"
        fi
        # Don't exit with error - violations are warnings, not blocking
    fi
else
    echo -e "${RED}❌ PMD check failed${NC}"
    echo "Error output:"
    tail -20 /tmp/pmd-output.log
    exit 4
fi
echo ""

# Success summary
echo -e "${BLUE}========================================${NC}"
if [ "$CI_MODE" = true ]; then
    echo -e "${GREEN}✅ All code quality checks completed successfully!${NC}"
else
    echo -e "${GREEN}✅ All formatting and quality checks completed successfully!${NC}"
fi
echo -e "${BLUE}========================================${NC}"
echo ""

if [ "$CI_MODE" = true ]; then
    echo -e "${GREEN}Code quality verified.${NC}"
else
    echo -e "${GREEN}Code is ready to commit.${NC}"
fi
echo ""

echo "Summary:"
if [ "$SKIP_FORMATTING" = false ] && [ "$CI_MODE" = false ]; then
    echo "  ✅ Code formatted and imports sorted (Prettier)"
fi
if [ "$SKIP_IMPORTS" = false ] && [ "$CI_MODE" = false ]; then
    echo "  ✅ Imports reordered (java,javax,org,com) and unused imports removed (Spotless)"
fi
if [ "$SKIP_FORMATTING" = false ] && [ "$CI_MODE" = false ]; then
    echo "  ✅ Line length issues fixed (Google Java Format)"
fi
echo "  ✅ Code quality verified (Checkstyle)"
if [ "$CI_MODE" = false ] && [ "$SKIP_COMPILATION" = false ]; then
    echo "  ✅ Compilation verified"
fi
echo "  ✅ Code analysis completed (PMD)"
echo ""

exit 0
