# CRITICAL: GitHub Pages 404 Fix

## Problem
GitHub Pages API shows it's serving from `main` branch instead of `gh-pages`:
```json
"source": {"branch":"main","path":"/"}
```

But the Allure reports are deployed to `gh-pages` branch!

## Solution (GitHub UI - REQUIRED)

**You MUST fix this in the GitHub UI - the API won't work:**

1. Go to: https://github.com/CScharer/full-stack-qa/settings/pages
2. Under **"Build and deployment"**:
   - If it shows **"Deploy from a branch"** with `main` selected:
     - Change dropdown to **"GitHub Actions"**
     - Click **Save**
   - If it already shows **"GitHub Actions"**:
     - Click **Save** anyway (this may refresh the configuration)
3. Wait 2-3 minutes for GitHub Pages to rebuild
4. Visit: https://cscharer.github.io/full-stack-qa/

## Why This Happened
The deployment workflow correctly pushed to `gh-pages`, but GitHub Pages was still configured to serve from `main` branch root, which doesn't contain the Allure report files.

## Verification
After fixing:
- GitHub Pages should serve from `gh-pages` branch
- The URL should show the Allure report (not 404)
- Check: `gh api repos/CScharer/full-stack-qa/pages --jq '.source'` should show `{"branch":"gh-pages","path":"/"}`
