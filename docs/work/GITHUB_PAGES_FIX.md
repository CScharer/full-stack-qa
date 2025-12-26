# Fix GitHub Pages 404 - Source Configuration Issue

## Problem
GitHub Pages is configured with conflicting settings:
- ✅ Build type: `workflow` (GitHub Actions) 
- ❌ Source: `{"branch":"main","path":"/"}` (should be using gh-pages branch from workflow)

This causes GitHub Pages to serve from `main` branch root instead of the `gh-pages` branch where Allure reports are deployed.

## Solution

### Step 1: Fix GitHub Pages Source (GitHub UI)
1. Go to: https://github.com/CScharer/full-stack-qa/settings/pages
2. Under **"Build and deployment"**:
   - **Source** should be set to **"GitHub Actions"** (not "Deploy from a branch")
   - If it shows "Deploy from a branch" with `main` branch selected, change it to "GitHub Actions"
3. Click **Save**
4. Wait 1-2 minutes for GitHub Pages to rebuild

### Step 2: Verify Deployment
After changing the source:
1. The workflow has already deployed to `gh-pages` branch ✅
2. GitHub Pages will now serve from `gh-pages` branch (not `main`)
3. Visit: https://cscharer.github.io/full-stack-qa/
4. Should see Allure report (not 404)

## Why This Happened
The deployment step (`peaceiris/actions-gh-pages@v4`) correctly pushed Allure reports to the `gh-pages` branch, but GitHub Pages was still configured to serve from the `main` branch root directory, which doesn't contain the Allure report files.

## Verification
After fixing:
- GitHub Pages settings should show "GitHub Actions" as source
- `gh-pages` branch should contain `allure-report-combined/` directory
- GitHub Pages URL should show Allure report
