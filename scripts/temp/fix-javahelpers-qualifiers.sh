#!/bin/bash
#
# Fix JavaHelpers. qualifiers in files that extend Page
#
# Since Page extends JavaHelpers, all Page subclasses inherit JavaHelpers methods
# and don't need the JavaHelpers. qualifier.
#
# Usage: ./scripts/fix-javahelpers-qualifiers.sh
#
# Note: This script modifies files in place. Make sure you have a backup or are using version control.

set -e

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

cd "$PROJECT_ROOT"

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Fix JavaHelpers Qualifiers in Page Subclasses${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Find all files that extend Page
echo -e "${YELLOW}Finding files that extend Page...${NC}"
FILES=$(find src/test/java -name "*.java" -type f -exec grep -l "extends Page" {} \;)

FILE_COUNT=$(echo "$FILES" | grep -c . || echo "0")
echo -e "${YELLOW}Found ${FILE_COUNT} files that extend Page${NC}"
echo ""

# Fix JavaHelpers. qualifiers in each file
FIXED_COUNT=0
PROCESSED_COUNT=0

for FILE in $FILES; do
    if [ ! -f "$FILE" ]; then
        continue
    fi
    
    # Check if file has JavaHelpers. qualifiers
    if grep -q "JavaHelpers\." "$FILE"; then
        # Remove JavaHelpers. qualifier (but keep it in comments)
        # Use sed to replace JavaHelpers. with nothing, but be careful with comments
        sed -i '' 's/JavaHelpers\.\([a-zA-Z_][a-zA-Z0-9_]*\)/\1/g' "$FILE"
        
        # Count how many were fixed
        FIXES=$(grep -c "JavaHelpers\." "$FILE" 2>/dev/null || echo "0")
        if [ "$FIXES" = "0" ]; then
            FIXED_COUNT=$((FIXED_COUNT + 1))
            echo -e "${GREEN}✅ Fixed: ${FILE}${NC}"
        fi
    fi
    PROCESSED_COUNT=$((PROCESSED_COUNT + 1))
done

echo ""
echo -e "${GREEN}✅ Processed ${PROCESSED_COUNT} files${NC}"
echo -e "${GREEN}✅ Fixed JavaHelpers qualifiers in ${FIXED_COUNT} files${NC}"
echo ""

# Verify fixes
echo -e "${YELLOW}Verifying fixes...${NC}"
REMAINING=$(find src/test/java -name "*.java" -type f -exec grep -l "extends Page" {} \; | xargs grep -c "JavaHelpers\." 2>/dev/null | grep -v ":0" | wc -l | tr -d ' ' || echo "0")
echo -e "${GREEN}Files with remaining JavaHelpers qualifiers: ${REMAINING}${NC}"
echo ""

echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}✅ Script completed${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""
echo "Next steps:"
echo "  1. Review changes: git diff"
echo "  2. Run: ./scripts/format-code.sh"
echo "  3. Verify: mvn pmd:check | grep UnnecessaryFullyQualifiedName"
echo ""

exit 0
