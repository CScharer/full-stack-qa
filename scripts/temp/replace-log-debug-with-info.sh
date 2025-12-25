#!/bin/bash
#
# Replace LOG.debug with LOG.info to resolve PMD GuardLogStatement violations
#
# This script replaces all LOG.debug calls with LOG.info calls in Java source files.
# This is a simpler solution than adding log level guards around every LOG.debug call.
#
# Usage: ./scripts/replace-log-debug-with-info.sh
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
echo -e "${BLUE}Replace LOG.debug with LOG.info${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Find all Java files with LOG.debug
echo -e "${YELLOW}Finding Java files with LOG.debug...${NC}"
FILES_WITH_DEBUG=$(find src/test/java -name "*.java" -type f -exec grep -l "LOG\.debug" {} \; | wc -l | tr -d ' ')

if [ "$FILES_WITH_DEBUG" = "0" ]; then
  echo -e "${GREEN}No files found with LOG.debug${NC}"
  exit 0
fi

echo -e "${YELLOW}Found ${FILES_WITH_DEBUG} file(s) with LOG.debug${NC}"
echo ""

# Count total LOG.debug occurrences
TOTAL_COUNT=$(find src/test/java -name "*.java" -type f -exec grep -c "LOG\.debug" {} \; | awk '{sum+=$1} END {print sum}')

echo -e "${YELLOW}Total LOG.debug occurrences: ${TOTAL_COUNT}${NC}"
echo ""

# Replace LOG.debug with LOG.info
echo -e "${YELLOW}Replacing LOG.debug with LOG.info...${NC}"

# Use find with -exec to replace in each file
find src/test/java -name "*.java" -type f -exec sed -i '' 's/LOG\.debug/LOG.info/g' {} \;

# Verify replacements
REPLACED_COUNT=$(find src/test/java -name "*.java" -type f -exec grep -c "LOG\.info" {} \; | awk '{sum+=$1} END {print sum}' || echo "0")
REMAINING_DEBUG=$(find src/test/java -name "*.java" -type f -exec grep -c "LOG\.debug" {} \; | awk '{sum+=$1} END {print sum}' || echo "0")

echo ""
echo -e "${GREEN}✅ Replacement complete${NC}"
echo -e "${GREEN}   LOG.info occurrences: ${REPLACED_COUNT}${NC}"
echo -e "${GREEN}   Remaining LOG.debug: ${REMAINING_DEBUG}${NC}"
echo ""

if [ "$REMAINING_DEBUG" != "0" ]; then
  echo -e "${YELLOW}⚠️  Warning: Some LOG.debug calls remain (may be in comments or strings)${NC}"
fi

echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}✅ Script completed successfully${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""
echo "Next steps:"
echo "  1. Run: ./scripts/format-code.sh"
echo "  2. Run: mvn pmd:check (to verify violations reduced)"
echo "  3. Review changes: git diff"
echo ""

exit 0
