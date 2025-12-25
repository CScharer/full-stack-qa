#!/bin/bash
# PMD Code Quality Verification Script
# This script runs PMD checks and provides detailed violation information

set -euo pipefail

echo "🔍 Verifying code quality with PMD..."
echo "Running: mvn pmd:check"

# Run PMD and capture output
# Suppress Maven progress output by:
# 1. Setting MAVEN_OPTS to reduce transfer listener output
# 2. Using --batch-mode for less verbose output
# 3. Filtering any remaining Progress lines
set +e  # Don't exit on error - we want to capture the exit code
# Append to MAVEN_OPTS if it exists, or create it if it doesn't
if [ -n "${MAVEN_OPTS:-}" ]; then
  export MAVEN_OPTS="${MAVEN_OPTS} -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn"
else
  export MAVEN_OPTS="-Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn"
fi
mvn --batch-mode pmd:check 2>&1 | grep -vE "^Progress" > /tmp/pmd-output.log
PMD_EXIT=${PIPESTATUS[0]}  # Get exit code from mvn, not grep
set -e  # Re-enable exit on error

# Always show the last part of the output for debugging (filter Progress lines again)
echo ""
echo "PMD output (last 30 lines):"
tail -30 /tmp/pmd-output.log | grep -vE "^Progress" || true
echo ""

# Check if PMD report was generated
if [ -f target/pmd.xml ]; then
  echo "✅ PMD report generated: target/pmd.xml"
  
  # Count violations in the XML report
  VIOLATION_COUNT=$(grep -c "<violation" target/pmd.xml 2>/dev/null || echo "0")
  SUPPRESSED_COUNT=$(grep -c "<suppressedviolation" target/pmd.xml 2>/dev/null || echo "0")
  
  echo ""
  echo "📊 PMD Summary:"
  echo "  Violations: $VIOLATION_COUNT"
  echo "  Suppressed: $SUPPRESSED_COUNT"
  
  # Extract violation details if any exist
  if [ "$VIOLATION_COUNT" -gt "0" ]; then
    echo ""
    echo "📋 Violation Details:"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    # Extract violations using a simple, robust approach
    CURRENT_FILE=""
    VIOLATION_NUM=0
    
    # Process XML file line by line
    while IFS= read -r line || [ -n "$line" ]; do
      # Track current file
      if echo "$line" | grep -q '<file name='; then
        CURRENT_FILE=$(echo "$line" | sed 's/.*name="\([^"]*\)".*/\1/' | sed 's|.*/||')
      fi
      
      # Process violations
      if echo "$line" | grep -q '<violation' && [ "$VIOLATION_NUM" -lt 20 ]; then
        # Extract rule and line from violation tag
        RULE=$(echo "$line" | sed 's/.*rule="\([^"]*\)".*/\1/' || echo "Unknown")
        LINE=$(echo "$line" | sed 's/.*beginline="\([^"]*\)".*/\1/' || echo "N/A")
        
        # Get message from next line (violation content)
        read -r next_line || true
        MSG=$(echo "$next_line" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//' | cut -c1-60 || echo "")
        
        # Print violation info
        printf "  📄 %-50s | Line %-5s | %-25s\n" "${CURRENT_FILE:-Unknown}" "$LINE" "$RULE" || true
        if [ -n "$MSG" ] && [ "$MSG" != "</violation>" ]; then
          printf "     └─ %s\n" "$MSG" || true
        fi
        
        VIOLATION_NUM=$((VIOLATION_NUM + 1))
      fi
    done < target/pmd.xml || true
    
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    
    # Show top violation types
    echo ""
    echo "🔝 Top Violation Types:"
    if grep -o 'rule="[^"]*"' target/pmd.xml 2>/dev/null > /dev/null; then
      grep -o 'rule="[^"]*"' target/pmd.xml 2>/dev/null | sed 's/rule="\(.*\)"/\1/' | sort | uniq -c | sort -rn | head -5 | while IFS= read -r line; do
        COUNT=$(echo "$line" | awk '{print $1}')
        RULE=$(echo "$line" | awk '{for(i=2;i<=NF;i++) printf "%s ", $i; print ""}' | sed 's/[[:space:]]*$//')
        echo "  • $RULE: $COUNT violation(s)" || true
      done || echo "  (Unable to extract violation types)"
    else
      echo "  (Unable to extract violation types)"
    fi
  else
    echo "✅ No violations found!"
  fi
else
  echo "⚠️  PMD report not found at target/pmd.xml"
fi

# Check for actual PMD execution errors (not Maven warnings)
# Look for PMD-specific error patterns, excluding Maven plugin warnings
if grep -qiE "(PMD.*error|PMD.*exception|Failed to execute.*pmd|BUILD FAILURE)" /tmp/pmd-output.log; then
  echo ""
  echo "::error::PMD encountered an error during execution"
  echo ""
  echo "Error details:"
  grep -iE "(PMD.*error|PMD.*exception|Failed to execute.*pmd|BUILD FAILURE)" /tmp/pmd-output.log | head -10
  exit 1
fi

# Check exit code - with failOnViolation=false, exit code should be 0 even with violations
if [ $PMD_EXIT -eq 0 ]; then
  echo ""
  echo "✅ PMD check completed successfully"
  # Even if exit code is 0, check for violations in output
  if grep -qi "violation" /tmp/pmd-output.log; then
    echo "⚠️  Note: PMD found violations but failOnViolation=false, so build continues"
  fi
else
  echo ""
  echo "::error::PMD check failed with exit code $PMD_EXIT"
  echo ""
  echo "Full PMD output:"
  cat /tmp/pmd-output.log
  exit 1
fi
