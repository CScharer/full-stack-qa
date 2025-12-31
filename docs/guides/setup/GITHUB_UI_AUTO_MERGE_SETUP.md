# GitHub UI: Auto-merge Configuration Guide

**Last Updated**: 2025-12-31  
**Purpose**: Step-by-step guide for configuring auto-merge in GitHub UI

---

## Overview

This guide walks you through the manual GitHub UI steps required to enable auto-merge for Dependabot security updates. These steps cannot be automated and must be done through the GitHub web interface.

---

## Step 1: Enable Auto-merge in Repository Settings

### 1.1 Navigate to Repository Settings

1. Go to your repository on GitHub: `https://github.com/CScharer/full-stack-qa`
2. Click on the **Settings** tab (top navigation bar)
3. In the left sidebar, click **General** (under "Code and automation")

### 1.2 Enable Auto-merge

1. Scroll down to the **Pull Requests** section
2. Find the **"Allow auto-merge"** checkbox
3. ✅ **Check the box** to enable auto-merge
4. Click **Save** (if a save button appears)

**What this does:**
- Allows pull requests to be automatically merged when all requirements are met
- Works in conjunction with branch protection rules
- Required for Dependabot auto-merge to function

**Visual Guide:**
```
Settings → General → Pull Requests
└── ☑️ Allow auto-merge
```

---

## Step 2: Verify Branch Protection Rules

### 2.1 Navigate to Branch Protection Settings

1. In the **Settings** tab, click **Branches** (in the left sidebar, under "Code and automation")
2. You should see a section titled **"Branch protection rules"**

### 2.2 Check Existing Rules or Create New Rule

**If you already have branch protection rules:**

1. Click on the rule for `main` (or the branch you want to protect)
2. Verify the following settings are enabled:
   - ✅ **Require a pull request before merging**
   - ✅ **Require status checks to pass before merging**
     - Ensure your CI/CD checks are listed (e.g., `test-fe-dev`, `test-be-dev`, etc.)
   - ✅ **Require branches to be up to date before merging**
   - ✅ **Allow auto-merge** (this should be visible if auto-merge is enabled in General settings)

**If you need to create a branch protection rule:**

1. Click **"Add rule"** or **"Add branch protection rule"**
2. Under **"Branch name pattern"**, enter: `main`
3. Enable the following:
   - ✅ **Require a pull request before merging**
   - ✅ **Require status checks to pass before merging**
     - Select your CI/CD checks from the list
   - ✅ **Require branches to be up to date before merging**
   - ✅ **Allow auto-merge** (if available)
4. Click **"Create"** or **"Save changes"**

### 2.3 Verify Auto-merge is Allowed

**Important**: Even if "Allow auto-merge" is enabled in General settings, you need to ensure branch protection rules don't block it.

1. In the branch protection rule, look for:
   - **"Allow auto-merge"** checkbox (should be checked)
   - Or ensure there's no setting that explicitly blocks auto-merge

**Visual Guide:**
```
Settings → Branches → Branch protection rules
└── [main branch rule]
    ├── ☑️ Require a pull request before merging
    ├── ☑️ Require status checks to pass before merging
    ├── ☑️ Require branches to be up to date before merging
    └── ☑️ Allow auto-merge
```

---

## Step 3: Verify Configuration

### 3.1 Test with a Dependabot PR

After completing the above steps:

1. Wait for Dependabot to create a security update PR (or manually trigger one)
2. Check the PR page - you should see:
   - **"Enable auto-merge"** button (if all checks pass)
   - Or the PR will auto-merge automatically after CI/CD passes (if configured)

### 3.2 Expected Behavior

**For Security Updates:**
- Dependabot creates PR with `security` update type
- CI/CD checks run automatically
- Once all checks pass, PR auto-merges (if auto-merge is enabled)
- Merge strategy: **Squash merge** (as configured in `dependabot.yml`)

**For Non-Security Updates:**
- PRs are created but require manual review
- Auto-merge does not apply (only security updates are configured for auto-merge)

---

## Troubleshooting

### Issue: "Allow auto-merge" option not visible

**Possible causes:**
- Repository doesn't have the required permissions
- Organization settings may restrict auto-merge
- You may not have admin access to the repository

**Solution:**
- Verify you have admin access to the repository
- Check organization settings if this is an organization repository

### Issue: Auto-merge button appears but doesn't work

**Possible causes:**
- Branch protection rules are blocking auto-merge
- Required status checks are not passing
- Branch is not up to date

**Solution:**
- Verify all branch protection requirements are met
- Ensure CI/CD checks are passing
- Update the branch if needed

### Issue: Dependabot PRs not auto-merging

**Possible causes:**
- Auto-merge is not enabled in repository settings
- Branch protection rules don't allow auto-merge
- PR is not a security update (only security updates are configured for auto-merge)

**Solution:**
- Verify Step 1 and Step 2 are completed correctly
- Check that the PR is a security update (should have `security` label)
- Review Dependabot configuration in `.github/dependabot.yml`

---

## Configuration Summary

After completing these steps, your repository will have:

✅ **Auto-merge enabled** in General settings  
✅ **Branch protection rules** configured  
✅ **Auto-merge allowed** in branch protection  
✅ **Dependabot configured** for security update auto-merge  

**Result**: Security update PRs from Dependabot will automatically merge after CI/CD checks pass.

---

## Related Documentation

- **Dependabot Configuration**: `.github/dependabot.yml`
- **Implementation Plan**: `docs/work/20251231_DEPENDENCY_MANAGEMENT_STATUS.md`
- **GitHub Documentation**: [Enabling auto-merge](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/incorporating-changes-from-a-pull-request/automatically-merging-a-pull-request)

---

**Last Updated**: 2025-12-31

