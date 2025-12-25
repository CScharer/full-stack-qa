#!/bin/bash
#
# Fix PMD Violations: UselessParentheses and LiteralsFirstInComparisons
# Also removes unused imports after fixes
#
# This script fixes:
# 1. UselessParentheses - Removes unnecessary parentheses around expressions
# 2. LiteralsFirstInComparisons - Puts literals first in comparisons to avoid NPE
# 3. Removes unused imports
#
# Usage: ./scripts/fix-pmd-useless-parentheses-and-literals.sh
#
# Note: This script modifies files in place. Make sure you have a backup or are using version control.

set -e

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

cd "$PROJECT_ROOT"

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Fix PMD Violations: UselessParentheses & LiteralsFirstInComparisons${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Step 1: Get PMD violations
echo -e "${YELLOW}Step 1: Analyzing PMD violations...${NC}"
mvn pmd:check > /tmp/pmd-check.log 2>&1 || true

USELESS_PAREN_COUNT=$(grep -c "PMD Failure.*UselessParentheses" /tmp/pmd-check.log || echo "0")
LITERALS_FIRST_COUNT=$(grep -c "PMD Failure.*LiteralsFirstInComparisons" /tmp/pmd-check.log || echo "0")

echo -e "${YELLOW}Found ${USELESS_PAREN_COUNT} UselessParentheses violations${NC}"
echo -e "${YELLOW}Found ${LITERALS_FIRST_COUNT} LiteralsFirstInComparisons violations${NC}"
echo ""

FIXED_PAREN=0
FIXED_LITERALS=0

# Step 2: Fix UselessParentheses violations
if [ "$USELESS_PAREN_COUNT" -gt 0 ]; then
    echo -e "${YELLOW}Step 2: Fixing UselessParentheses violations...${NC}"
    
    # Extract file:line from PMD output
    grep "PMD Failure.*UselessParentheses" /tmp/pmd-check.log | \
        sed 's/.*PMD Failure: \([^:]*\):\([0-9]*\).*/\1:\2/' | \
        sort | uniq > /tmp/useless-paren-violations.txt || true
    
    while IFS=: read -r class_path line_num || [ -n "$class_path" ]; do
        [ -z "$class_path" ] && continue
        [ -z "$line_num" ] && continue
        
        # Convert class path to file path
        # com.cjs.qa.junit.tests.HtmlUnitUpgradeVerificationTest -> src/test/java/com/cjs/qa/junit/tests/HtmlUnitUpgradeVerificationTest.java
        FILE_PATH=$(echo "$class_path" | sed 's/\./\//g')
        FILE_PATH="src/test/java/${FILE_PATH}.java"
        
        if [ ! -f "$FILE_PATH" ]; then
            continue
        fi
        
        # Get the line content
        LINE_CONTENT=$(sed -n "${line_num}p" "$FILE_PATH" 2>/dev/null || echo "")
        
        if [ -z "$LINE_CONTENT" ]; then
            continue
        fi
        
        # Fix common patterns:
        # 1. !(expression) -> !expression (when parentheses are unnecessary)
        # 2. return (expression) -> return expression (when parentheses wrap entire return)
        # 3. ((expression)) -> (expression) (double parentheses)
        
        # Fix !(expression) pattern
        if echo "$LINE_CONTENT" | grep -qE '!\s*\([^)]+\)'; then
            # Check if it's a simple case like !(methodCall())
            if echo "$LINE_CONTENT" | grep -qE '!\s*\([a-zA-Z_][a-zA-Z0-9_]*\s*\([^)]*\)\s*\)'; then
                sed -i '' "${line_num}s/!(\([a-zA-Z_][a-zA-Z0-9_]*\s*([^)]*)\))/!\1/g" "$FILE_PATH"
                FIXED_PAREN=$((FIXED_PAREN + 1))
            fi
        fi
        
        # Fix return (expression) pattern (when parentheses wrap entire return value)
        if echo "$LINE_CONTENT" | grep -qE 'return\s+\([^)]+\)\s*;'; then
            # Remove parentheses around return value
            sed -i '' "${line_num}s/return (\([^)]*\));/return \1;/g" "$FILE_PATH"
            FIXED_PAREN=$((FIXED_PAREN + 1))
        fi
        
        # Fix double parentheses ((expression)) -> (expression)
        if echo "$LINE_CONTENT" | grep -qE "\(\([^)]+\)\)"; then
            sed -i '' "${line_num}s/((/(/g" "$FILE_PATH"
            sed -i '' "${line_num}s/))/)/g" "$FILE_PATH"
            FIXED_PAREN=$((FIXED_PAREN + 1))
        fi
    done < /tmp/useless-paren-violations.txt 2>/dev/null || true
    
    echo -e "${GREEN}✅ Fixed ${FIXED_PAREN} UselessParentheses violations${NC}"
    echo ""
fi

# Step 3: Fix LiteralsFirstInComparisons violations
if [ "$LITERALS_FIRST_COUNT" -gt 0 ]; then
    echo -e "${YELLOW}Step 3: Fixing LiteralsFirstInComparisons violations...${NC}"
    
    # Extract file:line from PMD output
    grep "PMD Failure.*LiteralsFirstInComparisons" /tmp/pmd-check.log | \
        sed 's/.*PMD Failure: \([^:]*\):\([0-9]*\).*/\1:\2/' | \
        sort | uniq > /tmp/literals-first-violations.txt || true
    
    while IFS=: read -r class_path line_num || [ -n "$class_path" ]; do
        [ -z "$class_path" ] && continue
        [ -z "$line_num" ] && continue
        
        # Convert class path to file path
        FILE_PATH=$(echo "$class_path" | sed 's/\./\//g')
        FILE_PATH="src/test/java/${FILE_PATH}.java"
        
        if [ ! -f "$FILE_PATH" ]; then
            continue
        fi
        
        # Get the line content
        LINE_CONTENT=$(sed -n "${line_num}p" "$FILE_PATH" 2>/dev/null || echo "")
        
        if [ -z "$LINE_CONTENT" ]; then
            continue
        fi
        
        # Pattern: variable.equals("literal") -> "literal".equals(variable)
        # Only fix string literal comparisons, not constant comparisons
        
        # Fix .equals() calls with string literals (quoted strings)
        if echo "$LINE_CONTENT" | grep -qE '[a-zA-Z_][a-zA-Z0-9_.]*\.equals\([\"'][^\"']+[\"']\)'; then
            # Use perl for complex replacements
            perl -i -pe '
                # Match: variable.equals("literal") or variable.equals(''literal'')
                s/([a-zA-Z_][a-zA-Z0-9_.]*(?:\[[^\]]*\])?)\.equals\(([\"'\''])([^\"'\'']+)\2\)/$2$3$2.equals($1)/g;
            ' "$FILE_PATH"
            FIXED_LITERALS=$((FIXED_LITERALS + 1))
        fi
        
        # Fix == comparisons with string literals (convert to .equals)
        if echo "$LINE_CONTENT" | grep -qE '[a-zA-Z_][a-zA-Z0-9_.]*\s*==\s*[\"'][^\"']+[\"']'; then
            perl -i -pe '
                # Match: variable == "literal" -> "literal".equals(variable)
                s/([a-zA-Z_][a-zA-Z0-9_.]*(?:\[[^\]]*\])?)\s*==\s*([\"'\''])([^\"'\'']+)\2/$2$3$2.equals($1)/g;
            ' "$FILE_PATH"
            FIXED_LITERALS=$((FIXED_LITERALS + 1))
        fi
        
        # Fix != comparisons with string literals
        if echo "$LINE_CONTENT" | grep -qE '[a-zA-Z_][a-zA-Z0-9_.]*\s*!=\s*[\"'][^\"']+[\"']'; then
            perl -i -pe '
                # Match: variable != "literal" -> !"literal".equals(variable)
                s/([a-zA-Z_][a-zA-Z0-9_.]*(?:\[[^\]]*\])?)\s*!=\s*([\"'\''])([^\"'\'']+)\2/!$2$3$2.equals($1)/g;
            ' "$FILE_PATH"
            FIXED_LITERALS=$((FIXED_LITERALS + 1))
        fi
    done < /tmp/literals-first-violations.txt 2>/dev/null || true
    
    echo -e "${GREEN}✅ Fixed ${FIXED_LITERALS} LiteralsFirstInComparisons violations${NC}"
    echo ""
fi

# Step 4: Remove unused imports
echo -e "${YELLOW}Step 4: Removing unused imports...${NC}"
if [ -f "scripts/find_all_unused_imports.py" ]; then
    # Run the Python script non-interactively
    echo "yes" | python3 scripts/find_all_unused_imports.py > /tmp/import-cleanup.log 2>&1 || true
    REMOVED_IMPORTS=$(grep -c "Removed.*import" /tmp/import-cleanup.log 2>/dev/null || echo "0")
    if [ "$REMOVED_IMPORTS" -gt 0 ]; then
        echo -e "${GREEN}✅ Removed unused imports${NC}"
    else
        echo -e "${GREEN}✅ No unused imports found${NC}"
    fi
else
    echo -e "${YELLOW}⚠️  find_all_unused_imports.py not found, skipping import cleanup${NC}"
fi
echo ""

# Summary
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}✅ Script completed${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""
echo "Summary:"
echo "  - UselessParentheses violations fixed: ${FIXED_PAREN}"
echo "  - LiteralsFirstInComparisons violations fixed: ${FIXED_LITERALS}"
echo ""
echo "Next step:"
echo "  Run: ./scripts/format-code.sh (to verify all fixes and format code)"
echo ""

exit 0
