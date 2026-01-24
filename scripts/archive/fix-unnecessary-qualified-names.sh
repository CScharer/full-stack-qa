#!/bin/bash
#
# Fix UnnecessaryFullyQualifiedName PMD violations
#
# This script removes unnecessary fully qualified names when the class is already
# in scope through inheritance or imports.
#
# Usage: ./scripts/archive/fix-unnecessary-qualified-names.sh
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
echo -e "${BLUE}Fix UnnecessaryFullyQualifiedName Violations${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Get list of violations from PMD
echo -e "${YELLOW}Analyzing PMD violations...${NC}"
mvn pmd:check > /tmp/pmd-check.log 2>&1 || true

# Extract violations with file and line numbers
grep -E "PMD Failure.*UnnecessaryFullyQualifiedName" /tmp/pmd-check.log | \
    awk -F: '{print $1":"$2}' | sort | uniq > /tmp/violations.txt

VIOLATION_COUNT=$(wc -l < /tmp/violations.txt | tr -d ' ')
echo -e "${YELLOW}Found ${VIOLATION_COUNT} file:line combinations with violations${NC}"
echo ""

# Common patterns to fix:
# 1. JavaHelpers.* when class extends Page (which extends JavaHelpers)
# 2. Environment.* when in Environment class itself
# 3. ClassName.* when in inner class or same class

echo -e "${YELLOW}Fixing violations...${NC}"

FIXED_COUNT=0

# Process each violation
while IFS= read -r violation; do
    FILE_PATH=$(echo "$violation" | cut -d: -f1)
    LINE_NUM=$(echo "$violation" | cut -d: -f2)
    
    # Convert PMD class path to file path
    FILE_PATH="${FILE_PATH//.//}"
    FILE_PATH="src/test/java/${FILE_PATH}.java"
    
    if [ ! -f "$FILE_PATH" ]; then
        continue
    fi
    
    # Get the actual line content
    LINE_CONTENT=$(sed -n "${LINE_NUM}p" "$FILE_PATH")
    
    # Check if line contains common patterns
    if echo "$LINE_CONTENT" | grep -q "JavaHelpers\."; then
        # Check if file extends Page (which extends JavaHelpers)
        if grep -q "extends Page" "$FILE_PATH"; then
            # Remove JavaHelpers. qualifier
            sed -i '' "${LINE_NUM}s/JavaHelpers\.//g" "$FILE_PATH"
            FIXED_COUNT=$((FIXED_COUNT + 1))
        fi
    elif echo "$LINE_CONTENT" | grep -q "Environment\."; then
        # Check if we're in Environment class
        if grep -q "^public class Environment" "$FILE_PATH" || grep -q "^class Environment" "$FILE_PATH"; then
            # Remove Environment. qualifier
            sed -i '' "${LINE_NUM}s/Environment\.//g" "$FILE_PATH"
            FIXED_COUNT=$((FIXED_COUNT + 1))
        fi
    fi
done < /tmp/violations.txt

echo ""
echo -e "${GREEN}✅ Fixed ${FIXED_COUNT} violations${NC}"
echo ""

# Re-run PMD to check remaining violations
echo -e "${YELLOW}Verifying fixes...${NC}"
mvn pmd:check > /tmp/pmd-check-after.log 2>&1 || true

REMAINING=$(grep -c "PMD Failure.*UnnecessaryFullyQualifiedName" /tmp/pmd-check-after.log || echo "0")
echo -e "${GREEN}Remaining violations: ${REMAINING}${NC}"
echo ""

echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}✅ Script completed${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""
echo "Next steps:"
echo "  1. Review changes: git diff"
echo "  2. Run: ./scripts/format-code.sh"
echo "  3. Verify: mvn pmd:check"
echo ""

exit 0
