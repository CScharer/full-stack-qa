#!/bin/bash
# Review Pipeline Results
# Usage: ./scripts/test/review-pipeline-results.sh [run-id]

set -e

RUN_ID="${1:-}"

if [ -z "$RUN_ID" ]; then
    echo "🔍 Finding latest pipeline run on main..."
    RUN_ID=$(gh run list --branch main --workflow "Selenium Grid CI/CD Pipeline" --limit 1 --json databaseId --jq '.[0].databaseId')
fi

echo "📊 Reviewing Pipeline Results"
echo "=============================="
echo "   Run ID: $RUN_ID"
echo "   URL: https://github.com/CScharer/full-stack-qa/actions/runs/$RUN_ID"
echo ""

# Get run status
STATUS=$(gh run view $RUN_ID --json status,conclusion --jq -r '.status')
CONCLUSION=$(gh run view $RUN_ID --json status,conclusion --jq -r '.conclusion // "in_progress"')

echo "📋 Run Status: $STATUS"
if [ "$CONCLUSION" != "in_progress" ] && [ "$CONCLUSION" != "null" ]; then
    echo "   Conclusion: $CONCLUSION"
fi
echo ""

if [ "$STATUS" != "completed" ]; then
    echo "⏳ Pipeline is still running. Please wait for it to complete."
    echo "   Run: gh run watch $RUN_ID"
    exit 0
fi

echo "🔍 Checking key sections..."
echo ""

# Check container creation output
echo "1️⃣ Container Creation (Step 4.5):"
echo "   Checking if all frameworks have containers..."
gh run view $RUN_ID --log | grep -A 5 "Created top-level container" | head -20
echo ""

# Check timestamp fix output
echo "2️⃣ Timestamp Analysis (Step 7.5):"
echo "   Checking timestamp fix results..."
gh run view $RUN_ID --log | grep -A 10 "Step 7.5\|Analyzing and fixing timestamp" | head -30
echo ""

# Check summary timestamp fix
echo "3️⃣ Summary Timestamp Fix:"
echo "   Checking summary fix results..."
gh run view $RUN_ID --log | grep -A 10 "Fixing summary timestamps\|Summary timestamps fixed" | head -20
echo ""

# Check framework summary
echo "4️⃣ Framework Summary:"
echo "   Checking which frameworks were converted..."
gh run view $RUN_ID --log | grep -A 15 "Framework Summary" | head -25
echo ""

# Check for any warnings or errors
echo "5️⃣ Warnings and Errors:"
WARNINGS=$(gh run view $RUN_ID --log | grep -E "⚠️|WARNING" | grep -i "suite\|container\|framework\|timestamp" | head -20)
if [ -n "$WARNINGS" ]; then
    echo "$WARNINGS"
else
    echo "   ✅ No relevant warnings found"
fi
echo ""

echo "✅ Review complete!"
echo ""
echo "📥 To download the Allure report:"
echo "   gh run download $RUN_ID --name allure-report-combined-all-environments"
echo ""
echo "🌐 View pipeline: https://github.com/CScharer/full-stack-qa/actions/runs/$RUN_ID"
