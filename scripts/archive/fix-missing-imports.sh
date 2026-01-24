#!/bin/bash
#
# Fix Missing Imports
# Restores imports that were incorrectly removed by the unused import script
#
# Usage: ./scripts/archive/fix-missing-imports.sh

set -e

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

cd "$PROJECT_ROOT"

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Fix Missing Imports${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Get compilation errors
echo -e "${YELLOW}Analyzing compilation errors...${NC}"
mvn compiler:testCompile -Dcheckstyle.skip=true -Dpmd.skip=true -Dfmt.skip=true > /tmp/compile-errors.log 2>&1 || true

# Extract file:line:symbol patterns
grep -E "ERROR.*\.java.*cannot find symbol" /tmp/compile-errors.log | \
  sed 's/.*ERROR.*\/\([^\/]*\.java\):\[\([0-9]*\),[0-9]*\].*symbol:.*class \([A-Za-z0-9_]*\).*/\1:\2:\3/' | \
  sort | uniq > /tmp/missing-imports.txt || true

# Process each missing import
FIXED=0
while IFS=: read -r file line symbol || [ -n "$file" ]; do
  [ -z "$file" ] && continue
  
  # Find the actual file path
  FILE_PATH=$(find src/test/java -name "$file" -type f | head -1)
  [ -z "$FILE_PATH" ] && continue
  
  # Determine import based on symbol
  IMPORT=""
  case "$symbol" in
    Logger)
      IMPORT="import org.apache.logging.log4j.Logger;"
      ;;
    WebDriver)
      IMPORT="import org.openqa.selenium.WebDriver;"
      ;;
    WebElement)
      IMPORT="import org.openqa.selenium.WebElement;"
      ;;
    QAException)
      IMPORT="import com.cjs.qa.core.QAException;"
      ;;
    Scenario)
      IMPORT="import io.cucumber.java.Scenario;"
      ;;
    JavascriptExecutor)
      IMPORT="import org.openqa.selenium.JavascriptExecutor;"
      ;;
    *)
      continue
      ;;
  esac
  
  # Check if import already exists
  if grep -q "^${IMPORT}$" "$FILE_PATH" 2>/dev/null; then
    continue
  fi
  
  # Find where to insert (after package, before class)
  if ! grep -q "^${IMPORT}$" "$FILE_PATH" 2>/dev/null; then
    # Insert after last import or after package
    if grep -q "^import " "$FILE_PATH"; then
      # Insert after last import
      awk -v imp="$IMPORT" '
        /^import / { last_import=NR }
        END {
          if (last_import) {
            # Read file and insert
            while ((getline line < FILENAME) > 0) {
              print line
              if (NR == last_import) print imp
            }
          }
        }
      ' "$FILE_PATH" > "$FILE_PATH.tmp" && mv "$FILE_PATH.tmp" "$FILE_PATH" || true
    else
      # Insert after package
      sed -i '' "/^package /a\\
\\
${IMPORT}
" "$FILE_PATH" 2>/dev/null || true
    fi
    FIXED=$((FIXED + 1))
  fi
done < /tmp/missing-imports.txt

echo -e "${GREEN}✅ Fixed ${FIXED} missing imports${NC}"
echo ""

# Verify compilation
echo -e "${YELLOW}Verifying compilation...${NC}"
if mvn compiler:testCompile -Dcheckstyle.skip=true -Dpmd.skip=true -Dfmt.skip=true -q 2>&1 | grep -q "BUILD SUCCESS"; then
  echo -e "${GREEN}✅ Compilation successful!${NC}"
else
  echo -e "${YELLOW}⚠️  Some compilation errors may remain${NC}"
fi

exit 0
