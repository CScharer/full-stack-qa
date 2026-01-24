#!/bin/bash
#
# Fix PMD Violations: UnnecessaryFullyQualifiedName, UselessParentheses, LiteralsFirstInComparisons
#
# Usage: ./scripts/archive/fix-pmd-violations.sh
#

set -e

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

cd "$PROJECT_ROOT"

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Fix PMD Violations${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Step 1: Get PMD violations
echo -e "${YELLOW}Step 1: Analyzing PMD violations...${NC}"
mvn pmd:check > /tmp/pmd-check.log 2>&1 || true

QUALIFIED_COUNT=$(grep -c "UnnecessaryFullyQualifiedName" /tmp/pmd-check.log 2>/dev/null | tr -d ' \n' || echo "0")
PAREN_COUNT=$(grep -c "UselessParentheses" /tmp/pmd-check.log 2>/dev/null | tr -d ' \n' || echo "0")
LITERALS_COUNT=$(grep -c "LiteralsFirstInComparisons" /tmp/pmd-check.log 2>/dev/null | tr -d ' \n' || echo "0")

echo -e "${YELLOW}Found ${QUALIFIED_COUNT} UnnecessaryFullyQualifiedName violations${NC}"
echo -e "${YELLOW}Found ${PAREN_COUNT} UselessParentheses violations${NC}"
echo -e "${YELLOW}Found ${LITERALS_COUNT} LiteralsFirstInComparisons violations${NC}"
echo ""

FIXED_QUALIFIED=0
FIXED_PAREN=0
FIXED_LITERALS=0

# Step 2: Fix UnnecessaryFullyQualifiedName
if [ "$QUALIFIED_COUNT" -gt 0 ]; then
    echo -e "${YELLOW}Step 2: Fixing UnnecessaryFullyQualifiedName violations...${NC}"
    
    # Extract violations: Format: [WARNING] PMD Failure: class:line Rule:UnnecessaryFullyQualifiedName ... qualifier 'QualifierName'
    grep "UnnecessaryFullyQualifiedName" /tmp/pmd-check.log | \
        sed -n 's/.*PMD Failure: \([^:]*\):\([0-9]*\).*qualifier '\''\([^'\'']*\)'\''.*/\1:\2:\3/p' | \
        sort | uniq > /tmp/qualified-violations.txt || true
    
    while IFS=: read -r class_path line_num qualifier || [ -n "$class_path" ]; do
        [ -z "$class_path" ] && continue
        [ -z "$line_num" ] && continue
        [ -z "$qualifier" ] && continue
        
        FILE_PATH=$(echo "$class_path" | sed 's/\./\//g')
        FILE_PATH="src/test/java/${FILE_PATH}.java"
        
        if [ ! -f "$FILE_PATH" ]; then
            continue
        fi
        
        # Remove the qualifier from the line (escape dots in qualifier)
        ESCAPED_QUALIFIER=$(echo "$qualifier" | sed 's/\./\\./g')
        perl -i -pe "s/\\b${ESCAPED_QUALIFIER}\\.//g if \$. == ${line_num}" "$FILE_PATH"
        FIXED_QUALIFIED=$((FIXED_QUALIFIED + 1))
    done < /tmp/qualified-violations.txt 2>/dev/null || true
    
    echo -e "${GREEN}✅ Fixed ${FIXED_QUALIFIED} UnnecessaryFullyQualifiedName violations${NC}"
    echo ""
fi

# Step 3: Fix UselessParentheses
if [ "$PAREN_COUNT" -gt 0 ]; then
    echo -e "${YELLOW}Step 3: Fixing UselessParentheses violations...${NC}"
    
    # Format: [WARNING] PMD Failure: class:line Rule:UselessParentheses
    grep "UselessParentheses" /tmp/pmd-check.log | \
        sed -n 's/.*PMD Failure: \([^:]*\):\([0-9]*\).*/\1:\2/p' | \
        sort | uniq > /tmp/paren-violations.txt || true
    
    while IFS=: read -r class_path line_num || [ -n "$class_path" ]; do
        [ -z "$class_path" ] && continue
        [ -z "$line_num" ] && continue
        
        FILE_PATH=$(echo "$class_path" | sed 's/\./\//g')
        FILE_PATH="src/test/java/${FILE_PATH}.java"
        
        if [ ! -f "$FILE_PATH" ]; then
            continue
        fi
        
        # Fix !(methodCall()) -> !methodCall()
        perl -i -pe "s/!\\s*\\(([a-zA-Z_][a-zA-Z0-9_]*\\s*\\([^)]*\\))\\s*\\)/!\\1/g if \$. == ${line_num}" "$FILE_PATH"
        
        # Fix return (expression); -> return expression;
        perl -i -pe "s/return\\s+\\(([^)]+)\\)\\s*;/return \\1;/g if \$. == ${line_num}" "$FILE_PATH"
        
        # Fix ((expression)) -> (expression)
        perl -i -pe "s/\\(\\(([^)]+)\\)\\)/(\\1)/g if \$. == ${line_num}" "$FILE_PATH"
        
        FIXED_PAREN=$((FIXED_PAREN + 1))
    done < /tmp/paren-violations.txt 2>/dev/null || true
    
    echo -e "${GREEN}✅ Fixed ${FIXED_PAREN} UselessParentheses violations${NC}"
    echo ""
fi

# Step 4: Fix LiteralsFirstInComparisons
if [ "$LITERALS_COUNT" -gt 0 ]; then
    echo -e "${YELLOW}Step 4: Fixing LiteralsFirstInComparisons violations...${NC}"
    
    # Format: [WARNING] PMD Failure: class:line Rule:LiteralsFirstInComparisons
    grep "LiteralsFirstInComparisons" /tmp/pmd-check.log | \
        sed -n 's/.*PMD Failure: \([^:]*\):\([0-9]*\).*/\1:\2/p' | \
        sort | uniq > /tmp/literals-violations.txt || true
    
    while IFS=: read -r class_path line_num || [ -n "$class_path" ]; do
        [ -z "$class_path" ] && continue
        [ -z "$line_num" ] && continue
        
        FILE_PATH=$(echo "$class_path" | sed 's/\./\//g')
        FILE_PATH="src/test/java/${FILE_PATH}.java"
        
        if [ ! -f "$FILE_PATH" ]; then
            continue
        fi
        
        # Fix variable.equals("literal") -> "literal".equals(variable)
        # Fix variable.equals(Constant.VALUE) -> Constant.VALUE.equals(variable)
        perl -i -pe "
            if (\$. == ${line_num}) {
                # String literals: variable.equals(\"literal\") -> \"literal\".equals(variable)
                s/([a-zA-Z_][a-zA-Z0-9_.]*(?:\\[[^\\]]*\\])?)\\.equals\\(([\"'])([^\"']+)\\2\\)/\\2\\3\\2.equals(\\1)/g;
                # Constants: variable.equals(Constant.VALUE) -> Constant.VALUE.equals(variable)
                s/([a-zA-Z_][a-zA-Z0-9_.]*)\\.equals\\(([A-Z][a-zA-Z0-9_.]*)\\)/\\2.equals(\\1)/g;
            }
        " "$FILE_PATH"
        FIXED_LITERALS=$((FIXED_LITERALS + 1))
    done < /tmp/literals-violations.txt 2>/dev/null || true
    
    echo -e "${GREEN}✅ Fixed ${FIXED_LITERALS} LiteralsFirstInComparisons violations${NC}"
    echo ""
fi

# Summary
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}✅ Script completed${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""
echo "Summary:"
echo "  - UnnecessaryFullyQualifiedName violations fixed: ${FIXED_QUALIFIED}"
echo "  - UselessParentheses violations fixed: ${FIXED_PAREN}"
echo "  - LiteralsFirstInComparisons violations fixed: ${FIXED_LITERALS}"
echo ""

exit 0
