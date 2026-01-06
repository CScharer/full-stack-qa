# Alternative Approaches for Allure History

**Date**: 2026-01-06  
**Issue**: Allure3 not recognizing manually created history files  
**Status**: Exploring alternative solutions

---

## 🔍 Root Cause Analysis

After 38+ pipeline runs, we've confirmed:
- ✅ History files are being created correctly (format, structure, data)
- ✅ History files are being preserved and accumulated (212K → 372K)
- ✅ History files are being deployed to GitHub Pages
- ✅ Test results have `historyId` fields
- ❌ Allure3 consistently refuses to process manually created history
- ❌ Trends are not visible in the Allure Reports UI

**Key Insight**: Allure3 appears to have a hard requirement that history must be created by Allure3 itself, not manually.

---

## 💡 Alternative Approaches

### Approach 1: Let Allure3 Create History Naturally (RECOMMENDED)

**Concept**: Stop manually creating history and let Allure3 create it naturally over multiple runs.

**How It Works**:
1. Copy history from previous report to results directory (as we do now)
2. Generate report with `allure generate`
3. Allure3 will merge existing history with new results
4. Allure3 will create updated history in the report
5. Copy that history back for the next run

**Changes Needed**:
- Remove manual history merge logic
- Keep history copy from report to results
- Let Allure3 handle all history creation/merging

**Pros**:
- Uses Allure3's native history handling
- Most likely to work correctly
- Less code to maintain

**Cons**:
- Requires 2-3 runs before history appears
- May lose some history if Allure3 doesn't merge correctly

**Implementation**:
```bash
# Simplified approach - just copy history and let Allure3 handle it
if [ -d "$PREVIOUS_REPORT_DIR/history" ]; then
    cp -r "$PREVIOUS_REPORT_DIR/history" "$RESULTS_DIR/"
fi

allure generate "$RESULTS_DIR" -o "$REPORT_DIR"

# Copy history back for next run
if [ -d "$REPORT_DIR/history" ]; then
    cp -r "$REPORT_DIR/history" "$RESULTS_DIR/"
fi
```

---

### Approach 2: Verify historyId Matching

**Concept**: Ensure historyId values in history files exactly match historyId values in test results.

**How It Works**:
1. Extract all historyId values from current test results
2. Verify they match the `uid` values in history files
3. If mismatches found, fix them

**Changes Needed**:
- Add validation script to check historyId matching
- Log mismatches for debugging
- Optionally fix mismatches automatically

**Implementation**:
```bash
# Extract historyIds from test results
jq -r '.historyId' "$RESULTS_DIR"/*-result.json | sort -u > /tmp/current_history_ids.txt

# Extract uids from history files
jq -r '.[].data[].uid' "$RESULTS_DIR/history/history-trend.json" | sort -u > /tmp/history_uids.txt

# Compare
diff /tmp/current_history_ids.txt /tmp/history_uids.txt
```

---

### Approach 3: Use Allure2 Instead of Allure3

**Concept**: Allure2 might be more lenient with manually created history.

**How It Works**:
1. Switch from Allure3 CLI to Allure2 CLI
2. Test if Allure2 processes manually created history better

**Pros**:
- Allure2 is more mature and stable
- May handle manual history better

**Cons**:
- Requires changing CLI installation
- May have different features/limitations
- Allure3 is the future direction

---

### Approach 4: Create Individual Test History Files

**Concept**: Allure3 might require individual `{md5-hash}.json` files for each test, not just trend files.

**How It Works**:
1. For each test with a historyId, create a `{md5(historyId)}.json` file
2. Include historical execution data in each file
3. Let Allure3 read these individual files

**Format** (per test):
```json
{
  "uid": "test-historyId",
  "history": [
    {
      "buildOrder": 474,
      "status": "passed",
      "time": {
        "start": 1234567890,
        "stop": 1234567891,
        "duration": 1000
      }
    }
  ]
}
```

**Implementation**:
```bash
# Group history entries by uid
jq -r '.[].data[] | "\(.uid)|\(.)"' "$RESULTS_DIR/history/history-trend.json" | \
while IFS='|' read -r uid entry; do
    hash=$(echo -n "$uid" | md5sum | cut -d' ' -f1)
    file="$RESULTS_DIR/history/${hash}.json"
    
    if [ -f "$file" ]; then
        jq --argjson entry "$entry" '.history += [$entry]' "$file" > "$file.tmp" && mv "$file.tmp" "$file"
    else
        echo "{\"uid\":\"$uid\",\"history\":[$entry]}" | jq '.' > "$file"
    fi
done
```

---

### Approach 5: Use Allure's History API/Plugin

**Concept**: Use Allure's built-in history handling mechanisms.

**How It Works**:
1. Research if Allure3 has a history plugin or API
2. Use official mechanisms instead of manual file creation

**Research Needed**:
- Check Allure3 documentation for history plugins
- Look for official history management tools
- Check if there's a configuration option we're missing

---

### Approach 6: Hybrid Approach - Bootstrap Then Let Allure3 Take Over

**Concept**: Create initial history structure, then let Allure3 manage it going forward.

**How It Works**:
1. For first 2-3 runs: Create minimal valid history structure
2. After that: Let Allure3 create and manage history naturally
3. Only copy history between runs, don't manually merge

**Implementation**:
```bash
# Only create history if it doesn't exist (first few runs)
if [ ! -d "$RESULTS_DIR/history" ] || [ -z "$(find "$RESULTS_DIR/history" -name "*.json" 2>/dev/null)" ]; then
    # Bootstrap: Create minimal valid structure
    echo '[{"buildOrder":'$BUILD_ORDER',"reportUrl":"","reportName":"Allure Report","data":[]}]' | \
        jq '.' > "$RESULTS_DIR/history/history-trend.json"
else
    # Normal: Just copy from previous report
    cp -r "$PREVIOUS_REPORT_DIR/history" "$RESULTS_DIR/"
fi

# Let Allure3 handle merging
allure generate "$RESULTS_DIR" -o "$REPORT_DIR"
```

---

## 🎯 Recommended Next Steps

1. **Try Approach 1 First** (Let Allure3 create history naturally)
   - Simplest and most likely to work
   - Remove manual merge logic
   - Test over 3-4 pipeline runs

2. **If Approach 1 fails, try Approach 4** (Individual test history files)
   - More complex but might be what Allure3 expects
   - Requires creating per-test history files

3. **As last resort, try Approach 3** (Switch to Allure2)
   - Only if Allure3 proves too restrictive
   - Allure2 is more mature and stable

---

## 📝 Implementation Notes

- All approaches should maintain the same history download/upload mechanisms
- All approaches should preserve history across GitHub Pages deployments
- All approaches should work with the existing CI/CD pipeline structure

---

**Last Updated**: 2026-01-06  
**Status**: Ready for implementation testing

