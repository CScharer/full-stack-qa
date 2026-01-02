# Framework Tests Missing from Allure Report - Analysis

**Date**: January 2, 2026  
**Issue**: Only Robot Framework tests appear in Allure and Summary reports. Cypress, Playwright, and Vibium tests are missing.

## Current Status

✅ **Robot Framework tests**: Working correctly - appearing in Allure report  
❌ **Cypress tests**: Not appearing  
❌ **Playwright tests**: Not appearing  
❌ **Vibium tests**: Not appearing  
❌ **FS (Full-Stack) tests**: Not appearing (separate issue)

## Configuration Review

### Test Enablement
- ✅ Tests are enabled in `determine-environments.sh`:
  - `enable_cypress_tests=true`
  - `enable_playwright_tests=true`
  - `enable_robot_tests=true`
  - `enable_vibium_tests=true`

### Boolean Conversion
- ✅ `ci.yml` correctly converts string outputs to booleans:
  ```yaml
  enable_cypress_tests: ${{ needs.determine-envs.outputs.enable_cypress_tests == 'true' }}
  ```

### Job Conditions
- ✅ Jobs in `env-fe.yml` have correct conditions:
  ```yaml
  if: inputs.enable_cypress_tests == true
  ```

### Artifact Upload Paths
- **Cypress**: `cypress/cypress/results/`
- **Playwright**: `playwright/test-results/`
- **Robot**: `target/robot-reports/` ✅ (working)
- **Vibium**: `vibium/test-results/`, `vibium/.vitest/`

### Artifact Download Paths
- ✅ Artifacts downloaded with `merge-multiple: true` to:
  - `all-test-results/cypress-results/`
  - `all-test-results/playwright-results/`
  - `all-test-results/robot-results/` ✅ (working)
  - `all-test-results/vibium-results/`

## Potential Root Causes

### 1. Tests Not Running
**Hypothesis**: Tests are enabled but not actually executing.

**Evidence Needed**:
- Check GitHub Actions logs for `cypress-tests`, `playwright-tests`, `vibium-tests` jobs
- Verify jobs show as "completed" (not skipped)
- Check if test execution steps show any output

**Next Steps**:
- Review pipeline logs to confirm tests are running
- Check if jobs are being skipped due to conditions

### 2. Tests Running But No Output Files Created
**Hypothesis**: Tests run but don't produce result files.

**Evidence Needed**:
- Check if `cypress/cypress/results/` contains JSON files after test run
- Check if `playwright/test-results/` contains XML files after test run
- Check if `vibium/test-results/` contains JSON files after test run

**Next Steps**:
- Verify test frameworks are configured to generate output files
- Check test execution logs for errors that might prevent file generation

### 3. Artifacts Not Uploaded
**Hypothesis**: Tests run and create files, but artifacts aren't uploaded.

**Evidence Needed**:
- Check if artifacts appear in GitHub Actions artifacts list
- Verify artifact upload steps complete successfully
- Note: `if-no-files-found: ignore` means empty artifacts won't fail but also won't be uploaded

**Next Steps**:
- Check GitHub Actions artifacts tab for `cypress-results-*`, `playwright-results-*`, `vibium-results-*`
- Verify artifact upload steps show success

### 4. Artifacts Downloaded But Paths Don't Match
**Hypothesis**: Artifacts are uploaded and downloaded, but the expected directory structure doesn't match.

**Expected Structure** (with `merge-multiple: true`):
```
all-test-results/
  cypress-results/
    cypress-results-dev/
      cypress/
        cypress/
          results/
            *.json
  playwright-results/
    playwright-results-dev/
      playwright/
        test-results/
          *.xml
  robot-results/
    robot-results-dev/
      target/
        robot-reports/
          output.xml  ✅ (this works)
  vibium-results/
    vibium-results-dev/
      vibium/
        test-results/
          *.json
```

**Next Steps**:
- The enhanced debugging added will show the actual structure
- Compare actual structure to expected paths in `prepare-combined-allure-results.sh`

### 5. Converters Failing Silently
**Hypothesis**: Converters are called but fail to process files.

**Evidence Needed**:
- Check converter exit codes in logs
- Verify converters are finding files but failing to parse them
- Check for Python/script errors in converter execution

**Next Steps**:
- Review converter script logs for errors
- Verify converters have proper error handling

## Debugging Added

Enhanced debugging has been added to `prepare-combined-allure-results.sh`:

1. **Artifact Directory Detection**: Shows which framework directories exist
2. **Directory Structure**: Lists directory structure with file counts
3. **File Listing**: Shows actual JSON/XML files found with sizes
4. **Path Checking**: Detailed output for each checked path

## Next Steps

### Immediate Actions

1. **Review Next Pipeline Run**:
   - Check "Prepare combined Allure results" step logs
   - Look for the new debug output showing:
     - Which artifact directories exist
     - Directory structure and file counts
     - Actual files found

2. **Check GitHub Actions Artifacts**:
   - Go to the pipeline run
   - Check "Artifacts" section
   - Verify if `cypress-results-*`, `playwright-results-*`, `vibium-results-*` artifacts exist
   - If they don't exist, tests aren't running or aren't producing files

3. **Check Test Job Logs**:
   - Review `cypress-tests`, `playwright-tests`, `vibium-tests` job logs
   - Verify tests are actually executing
   - Check for errors that might prevent file generation

### If Tests Are Running But Artifacts Missing

1. **Verify Output File Generation**:
   - Check if Cypress generates `mochawesome.json` or `cypress-results.json`
   - Check if Playwright generates JUnit XML files
   - Check if Vibium generates JSON files in `test-results/` or `.vitest/`

2. **Check Artifact Upload Paths**:
   - Verify paths in `env-fe.yml` match where files are actually created
   - Consider adding debug steps to list files before upload

### If Artifacts Exist But Not Found

1. **Verify Download Structure**:
   - Check the actual structure after `merge-multiple: true` download
   - Compare to expected paths in `prepare-combined-allure-results.sh`

2. **Fix Path Matching**:
   - Update path checks in `prepare-combined-allure-results.sh` to match actual structure
   - Add fallback paths if structure varies

### If Converters Are Failing

1. **Add Error Handling**:
   - Ensure converters report errors clearly
   - Add try-catch blocks in Python scripts
   - Verify exit codes are properly checked

2. **Test Converters Locally**:
   - Download artifacts manually
   - Run converters with actual artifact structure
   - Verify they can process the files

## Key Differences: Why Robot Works

Robot Framework tests work because:
1. ✅ Tests run and produce `output.xml` in `target/robot-reports/`
2. ✅ Artifact uploads from `target/robot-reports/`
3. ✅ Artifact downloads to `robot-results/robot-results-{env}/target/robot-reports/`
4. ✅ Script finds `output.xml` in expected location
5. ✅ Converter successfully processes `output.xml`

The other frameworks should follow the same pattern, so the issue is likely in one of these steps.

## Recommended Investigation Order

1. **First**: Check if artifacts exist in GitHub Actions (quickest check)
2. **Second**: Review test job logs to see if tests are running
3. **Third**: Review the new debug output from `prepare-combined-allure-results.sh`
4. **Fourth**: Compare actual artifact structure to expected paths
5. **Fifth**: Test converters manually with actual artifacts

## Expected Debug Output

The next pipeline run should show output like:

```
🔍 Debug: Checking for framework-specific artifact directories...
   Cypress: ✅ exists
   Playwright: ✅ exists
   Robot: ✅ exists
   Vibium: ✅ exists
   FS: ❌ not found

   📂 Cypress results structure:
      - all-test-results/cypress-results (X files)
      - all-test-results/cypress-results/cypress-results-dev (X files)
      ...
   📄 Cypress JSON files found:
      - all-test-results/cypress-results/cypress-results-dev/cypress/cypress/results/mochawesome.json (X KB)
      ...
```

This will immediately show:
- Which frameworks have artifacts
- Where the files actually are
- Whether the paths match expectations

