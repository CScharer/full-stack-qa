#!/bin/bash
# Extract UselessParentheses violations from PMD report

awk '
/<file name=/ {
    file = $0;
    sub(/.*name="[^\/]*\//, "", file);
    sub(/".*/, "", file);
}
/rule="UselessParentheses"/ {
    match($0, /beginline="([0-9]+)"/, line);
    match($0, /method="([^"]*)"/, method);
    print file ":" line[1] " (method: " method[1] ")";
}
' pmd-current.xml | sort > useless-parens-violations.txt

echo "✅ Extracted UselessParentheses violations"
wc -l useless-parens-violations.txt
echo ""
echo "Files with most violations:"
cut -d: -f1 useless-parens-violations.txt | sort | uniq -c | sort -rn | head -20 | awk '{printf "%3d  %s\n", $1, $2}'
