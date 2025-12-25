# ✅ API Secret Verification Guide

## What is API Secret Verification?

**API Secret Verification** means testing that:
1. ✅ Secrets exist in Google Cloud Secret Manager
2. ✅ Your code can successfully retrieve them
3. ✅ The APIs still work with the retrieved secrets

---

## Quick Verification Steps

### Step 1: Verify Secrets Exist in Google Cloud

```bash
# List all VIVIT secrets (should see 6)
gcloud secrets list --project=cscharer | grep AUTO_VIVIT

# Expected output:
# AUTO_VIVIT_GT_WEBINAR_USER_ID
# AUTO_VIVIT_GT_WEBINAR_PASSWORD
# AUTO_VIVIT_GT_WEBINAR_CONSUMER_KEY
# AUTO_VIVIT_GT_WEBINAR_CONSUMER_SECRET
# AUTO_VIVIT_YM_API_KEY
# AUTO_VIVIT_YM_API_SA_PASSCODE

# Also check Pluralsight secrets:
gcloud secrets list --project=cscharer | grep AUTO_PLURALSIGHT
# Expected: AUTO_PLURALSIGHT_TRAINING_USERNAME
# Expected: AUTO_PLURALSIGHT_TRAINING_PASSWORD
```

### Step 2: Test Secret Retrieval (Code Level)

Run the existing test that verifies secret retrieval:

```bash
# Test that SecureConfig can retrieve secrets
./mvnw test -Dtest=SecureConfigTest

# Expected: All tests pass ✅
```

**What this tests:**
- `SecureConfig.getPassword()` can connect to Google Cloud
- Secrets can be retrieved successfully
- Caching is working

### Step 3: Test API Functionality (End-to-End)

Test that the APIs actually work with the retrieved secrets:

```bash
# Test GoToWebinar API (if you have tests)
./mvnw test -Dtest=GTWebinarDataTests#testAuth

# Test YourMembership API (if you have tests)
./mvnw test -Dtest=YMService

# Test Pluralsight login (if you have tests)
./mvnw test -Dtest=LoginPage
```

**What this tests:**
- Secrets are retrieved correctly
- API authentication works
- No hardcoded values are being used

---

## Manual Verification (If Tests Don't Exist)

### Option 1: Simple Java Test

Create a temporary test file:

```java
// src/test/java/com/cjs/qa/utilities/VerifySecretsTest.java
package com.cjs.qa.utilities;

import com.cjs.qa.core.security.EAPIKeys;
import com.cjs.qa.core.security.EPasswords;
import org.junit.Test;

public class VerifySecretsTest {
    @Test
    public void verifyGoToWebinarSecrets() {
        // These will throw RuntimeException if secrets don't exist
        String userId = EAPIKeys.VIVIT_GT_WEBINAR_USER_ID.getValue();
        String password = EPasswords.VIVIT_GT_WEBINAR_PASSWORD.getValue();
        String consumerKey = EAPIKeys.VIVIT_GT_WEBINAR_CONSUMER_KEY.getValue();
        String consumerSecret = EAPIKeys.VIVIT_GT_WEBINAR_CONSUMER_SECRET.getValue();

        System.out.println("✅ All GoToWebinar secrets retrieved successfully!");
        System.out.println("User ID: " + (userId != null ? "✓" : "✗"));
        System.out.println("Password: " + (password != null ? "✓" : "✗"));
        System.out.println("Consumer Key: " + (consumerKey != null ? "✓" : "✗"));
        System.out.println("Consumer Secret: " + (consumerSecret != null ? "✓" : "✗"));
    }

    @Test
    public void verifyYourMembershipSecrets() {
        String apiKey = EAPIKeys.VIVIT_YM_API_KEY.getValue();
        String passcode = EAPIKeys.VIVIT_YM_API_SA_PASSCODE.getValue();

        System.out.println("✅ All YourMembership secrets retrieved successfully!");
        System.out.println("API Key: " + (apiKey != null ? "✓" : "✗"));
        System.out.println("SA Passcode: " + (passcode != null ? "✓" : "✗"));
    }

    @Test
    public void verifyPluralsightSecrets() {
        String username = EAPIKeys.PLURALSIGHT_TRAINING_USERNAME.getValue();
        String password = EPasswords.PLURALSIGHT_TRAINING_PASSWORD.getValue();

        System.out.println("✅ All Pluralsight secrets retrieved successfully!");
        System.out.println("Username: " + (username != null ? "✓" : "✗"));
        System.out.println("Password: " + (password != null ? "✓" : "✗"));
    }
}
```

Run it:
```bash
./mvnw test -Dtest=VerifySecretsTest
```

### Option 2: Check Secret Values (gcloud CLI)

```bash
# Verify a secret value exists (don't print it in production!)
gcloud secrets versions access latest \
  --secret="AUTO_VIVIT_GT_WEBINAR_CONSUMER_KEY" \
  --project=cscharer

# Should output the actual key value (not null/empty)
```

---

## What Success Looks Like

### ✅ Success Indicators:

1. **gcloud list shows all secrets:**
   ```
   AUTO_VIVIT_GT_WEBINAR_USER_ID
   AUTO_VIVIT_GT_WEBINAR_PASSWORD
   AUTO_VIVIT_GT_WEBINAR_CONSUMER_KEY
   AUTO_VIVIT_GT_WEBINAR_CONSUMER_SECRET
   AUTO_VIVIT_YM_API_KEY
   AUTO_VIVIT_YM_API_SA_PASSCODE
   AUTO_PLURALSIGHT_TRAINING_USERNAME
   AUTO_PLURALSIGHT_TRAINING_PASSWORD
   ```

2. **SecureConfigTest passes:**
   ```
   ✅ SecureConfig.getPassword() test passed!
   ✅ EPasswords integration test passed!
   ```

3. **API tests pass:**
   ```
   ✅ GTWebinarDataTests#testAuth - PASSED
   ✅ YMService tests - PASSED
   ```

4. **No RuntimeException thrown:**
   - If secrets don't exist, you'll see: `RuntimeException: Failed to fetch secret from Google Cloud Secret Manager`
   - If they exist, no exception = success ✅

---

## Common Issues

### ❌ Error: "Failed to fetch secret from Google Cloud Secret Manager"

**Possible causes:**
1. Secret doesn't exist in Google Cloud
   - **Fix**: Create the secret using `gcloud secrets create`
   - **Check**: `gcloud secrets list --project=cscharer`

2. Wrong secret name
   - **Fix**: Verify the secret name matches exactly (case-sensitive)
   - **Check**: Look at `EAPIKeys.java` for the exact name

3. No Google Cloud authentication
   - **Fix**: Run `gcloud auth application-default login`
   - **Check**: `gcloud auth list`

4. Service account doesn't have permission
   - **Fix**: Grant `roles/secretmanager.secretAccessor` role
   - **Check**: Verify service account permissions in Google Cloud Console

### ❌ Error: "Secret key cannot be null or empty"

**Cause**: Code is passing null/empty string to `getValue()`

**Fix**: Check that enum values are correct in `EAPIKeys.java`

---

## Verification Checklist

- [ ] All 8 secrets exist in Google Cloud (`gcloud secrets list`)
- [ ] `SecureConfigTest` passes
- [ ] `EAPIKeys.getValue()` works for all 6 API keys
- [ ] `EPasswords.getValue()` works for all 2 passwords
- [ ] API tests pass (GTWebinar, YourMembership, Pluralsight)
- [ ] No `RuntimeException` thrown when retrieving secrets
- [ ] Pre-commit hook passes (no hardcoded credentials detected)

---

## Summary

**API Secret Verification** = Making sure:
1. Secrets are in Google Cloud ✅
2. Code can retrieve them ✅
3. APIs work with retrieved secrets ✅

**Quick test:**
```bash
./mvnw test -Dtest=SecureConfigTest
```

If this passes, your secrets are verified! 🎉

---

**Created**: 2025-11-14
**Last Updated**: 2025-12-21
**Status**: Verification guide for completed migration
