# 🔐 Secret Manager CI/CD Options

## Problem

The `SecretManagerSmokeTest` requires Google Cloud credentials to access Secret Manager. In CI/CD pipelines, these credentials don't exist by default, causing tests to fail.

**Security Constraint**: We cannot commit the JSON credentials file to the public repository.

---

## Solution Options

### Option 1: Skip Tests in CI/CD (Recommended for Quick Fix) ✅

**Pros:**
- ✅ Simple to implement
- ✅ No security risks
- ✅ Tests still run locally where credentials exist
- ✅ Fast to implement

**Cons:**
- ❌ Tests don't verify Secret Manager in CI/CD
- ❌ May miss issues if Secret Manager changes

**Implementation:**
- Add environment check to skip tests when `CI=true` or `GITHUB_ACTIONS=true`
- Tests run locally but skip in pipeline

---

### Option 2: Use GitHub Secrets (Recommended for Long-term) ⭐

**Pros:**
- ✅ Tests run in CI/CD
- ✅ Secure (secrets stored in GitHub, not in code)
- ✅ Verifies Secret Manager integration in pipeline
- ✅ No JSON file in repository

**Cons:**
- ⚠️ Requires setting up GitHub Secrets
- ⚠️ Slightly more complex setup

**Implementation:**
- Store service account JSON as GitHub Secret
- Inject as environment variable in CI workflow
- Write to temporary file during test execution
- Clean up after tests

---

### Option 3: Workload Identity Federation (Advanced) 🚀

**Pros:**
- ✅ Most secure (no keys stored anywhere)
- ✅ Uses OIDC tokens
- ✅ Best practice for production

**Cons:**
- ❌ More complex setup
- ❌ Requires GCP Workload Identity configuration
- ❌ May be overkill for this use case

**Implementation:**
- Configure Workload Identity Pool in GCP
- Configure GitHub Actions OIDC
- Use `google-github-actions/auth` action

---

## Recommendation

**For Immediate Fix**: Use **Option 1** (Skip in CI)
- Quick to implement
- No security concerns
- Tests still valuable locally

**For Long-term**: Use **Option 2** (GitHub Secrets)
- Better test coverage
- Still secure
- Verifies integration in CI/CD

---

## Implementation Details

See individual implementation guides below.
