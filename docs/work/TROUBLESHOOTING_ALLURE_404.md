# Troubleshooting Allure Reports 404 Error

## Issue
Allure Reports showing 404 at: https://cscharer.github.io/full-stack-qa/

## Root Cause
GitHub Pages deployment only happens when:
1. ✅ Code is on `main` branch
2. ✅ `code-changed == 'true'` (detected by workflow)
3. ✅ GitHub Pages is configured to use "GitHub Actions" as source

## Quick Fix Checklist

### Step 1: Verify GitHub Pages Configuration
1. Go to: https://github.com/CScharer/full-stack-qa/settings/pages
2. Check **Source**: Should be **"GitHub Actions"** (not "Deploy from a branch")
3. If it's set to branch-based, change it to "GitHub Actions"

### Step 2: Check if PR is Merged to Main
1. Go to: https://github.com/CScharer/full-stack-qa/pulls
2. Check if PR #2 (`fix-migration-doc-updates`) is merged
3. If not merged, merge it to `main` to trigger deployment

### Step 3: Verify Deployment Job Ran
1. Go to: https://github.com/CScharer/full-stack-qa/actions
2. Find the latest workflow run on `main` branch
3. Look for job: **"Combined Allure Report (All Environments)"**
4. Check step: **"Deploy to GitHub Pages"**
   - ✅ Should show "Deploying to GitHub Pages..."
   - ✅ Should complete successfully
   - ❌ If skipped, check the condition logs

### Step 4: Check Deployment Conditions
In the workflow run, look for step: **"Check GitHub Pages Deployment Conditions"**

Expected output:
```
✅ All conditions met - GitHub Pages will be deployed
```

If you see:
```
⏭️  GitHub Pages deployment skipped: Not on main branch
```
→ The workflow ran on a PR branch, not main

If you see:
```
⏭️  GitHub Pages deployment skipped: No code changes detected
```
→ The workflow detected no code changes (scheduled run)

### Step 5: Wait for GitHub Pages Build
After deployment job completes:
- GitHub Pages can take **1-5 minutes** to build and publish
- Check: https://github.com/CScharer/full-stack-qa/settings/pages
- Look for "Your site is live at..." message
- Check deployment status in the "Deployments" section

### Step 6: Verify gh-pages Branch
1. Go to: https://github.com/CScharer/full-stack-qa/branches
2. Look for `gh-pages` branch
3. If it exists, check if it has recent commits
4. If it doesn't exist, the deployment hasn't run yet

## Alternative: Download Artifact
While waiting for GitHub Pages:
1. Go to: https://github.com/CScharer/full-stack-qa/actions
2. Select the latest workflow run
3. Scroll to **Artifacts** section
4. Download: `allure-report-combined-all-environments`
5. Extract and open `index.html` in browser

## Common Issues

### Issue 1: GitHub Pages Source is Wrong
**Symptom**: 404 error, no `gh-pages` branch
**Fix**: Change Settings → Pages → Source to "GitHub Actions"

### Issue 2: Deployment Job Skipped
**Symptom**: "Deploy to GitHub Pages" step shows "Skipped"
**Fix**: Merge PR to `main` branch (deployment only runs on main)

### Issue 3: No Code Changes Detected
**Symptom**: Deployment skipped due to "No code changes detected"
**Fix**: Make a small change and push to `main` (or wait for next scheduled run)

### Issue 4: Deployment Failed
**Symptom**: "Deploy to GitHub Pages" step shows red X
**Fix**: Check logs for error messages, verify `GITHUB_TOKEN` permissions

### Issue 5: GitHub Pages Still Building
**Symptom**: 404 but deployment job succeeded
**Fix**: Wait 1-5 minutes, then refresh the page

## Verification Steps After Fix

1. ✅ GitHub Pages source = "GitHub Actions"
2. ✅ PR merged to `main`
3. ✅ Deployment job ran successfully
4. ✅ `gh-pages` branch exists with recent commits
5. ✅ Wait 1-5 minutes for GitHub Pages to build
6. ✅ Visit: https://cscharer.github.io/full-stack-qa/
7. ✅ Should see Allure report (not 404)

## Still Not Working?

1. Check repository permissions:
   - Settings → Actions → General
   - "Workflow permissions" should be "Read and write permissions"

2. Check if `gh-pages` branch exists:
   - If missing, deployment hasn't run
   - If exists but old, deployment may have failed

3. Check workflow logs for errors:
   - Look for "Deploy to GitHub Pages" step
   - Check for permission errors or file issues

4. Try manual trigger:
   - Actions → "Selenium Grid CI/CD Pipeline" → "Run workflow"
   - Select `main` branch
   - This will force a new deployment
