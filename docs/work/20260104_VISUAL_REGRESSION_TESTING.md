# Visual Regression Testing - Implement Percy/Applitools

**Date Created**: 2026-01-04  
**Status**: Planning  
**Priority**: 🟢 Low Priority  
**Estimated Time**: 8-12 hours

---

## 📋 Overview

This document outlines the implementation plan for adding Percy or Applitools visual regression testing to complement the existing Vibium framework. This enhancement would provide cloud-hosted baselines, team collaboration features, and cross-browser visual testing capabilities.

---

## 🎯 Current State

The project already has **Vibium** for visual regression testing:
- ✅ Integrated into CI/CD pipeline (`vibium-tests` job)
- ✅ Located in `vibium/` directory
- ✅ Uses TypeScript with Vitest
- ✅ Results integrated into Allure reports

---

## 🤔 Why Add Percy/Applitools?

While Vibium provides visual regression capabilities, Percy/Applitools offer additional features:

- **Cloud-hosted baselines** - No need to manage baseline storage
- **Team collaboration** - Review and approve visual changes via web UI
- **Cross-browser testing** - Automated visual testing across multiple browsers
- **AI-powered comparison** (Applitools) - Intelligent visual diff detection
- **Historical tracking** - Visual test history and trends
- **Integration with existing frameworks** - Works with Playwright, Cypress, Selenium

---

## 🔐 Account & License Requirements

### Can Applitools be used without an account/license?

**Answer: No, you cannot use Applitools without an account/license.**

**Both tools require:**
- ✅ **Free account** (email signup required)
- ✅ **API key** (provided after account creation)
- ✅ **GitHub secret** (store API key securely)

### Free Tier Limitations

**Percy:**
- ~100-500 checkpoints/month (varies by plan)
- Basic features available
- Public repositories typically supported

**Applitools:**
- ~100 checkpoints/month
- Basic features available
- Public repositories typically supported

### Setup Requirements

1. **Account creation** (free tier available)
2. **API key generation** (from dashboard)
3. **API key stored as GitHub secret** (for CI/CD)

---

## 🛠️ Implementation Options

### Option A: Percy (Recommended for Simplicity)

**Pros:**
- Simpler setup and integration
- Good documentation
- Works well with Playwright/Cypress
- Free tier is generous for most projects

**Cons:**
- Less AI-powered than Applitools
- Basic visual comparison

#### Integration Steps

1. **Create Percy account** (free tier)
   - Visit https://percy.io
   - Sign up with GitHub/GitLab
   - Create a new project

2. **Get API key from Percy dashboard**
   - Navigate to project settings
   - Copy the API token

3. **Add `PERCY_TOKEN` as GitHub secret**
   - Go to repository Settings → Secrets and variables → Actions
   - Add new secret: `PERCY_TOKEN`

4. **Install Percy CLI and framework SDKs:**
   ```bash
   # For Playwright
   cd playwright
   npm install --save-dev @percy/cli @percy/playwright
   
   # For Cypress
   cd cypress
   npm install --save-dev @percy/cli @percy/cypress
   
   # For Selenium (Java)
   # Add to pom.xml:
   # <dependency>
   #   <groupId>com.percy</groupId>
   #   <artifactId>percy-java-selenium</artifactId>
   #   <version>LATEST</version>
   # </dependency>
   ```

5. **Configure Percy in test files:**
   ```typescript
   // Playwright example
   import percy from '@percy/playwright';
   
   test('visual test', async ({ page }) => {
     await page.goto('/');
     await percy.snapshot(page, 'Homepage');
   });
   ```

   ```typescript
   // Cypress example
   import '@percy/cypress';
   
   it('visual test', () => {
     cy.visit('/');
     cy.percySnapshot('Homepage');
   });
   ```

   ```java
   // Selenium (Java) example
   import com.percy.selenium.Percy;
   
   Percy percy = new Percy(webDriver);
   percy.snapshot("Homepage");
   ```

6. **Update CI/CD workflow to run Percy:**
   ```yaml
   - name: Run Percy visual tests
     env:
       PERCY_TOKEN: ${{ secrets.PERCY_TOKEN }}
     run: npx percy exec -- npm test
   ```

#### Time Estimate: 8-12 hours

- Account setup: 15 minutes
- SDK installation: 1 hour
- Test integration: 4-6 hours
- CI/CD configuration: 2-3 hours
- Baseline creation & testing: 1-2 hours

---

### Option B: Applitools (Recommended for AI Features)

**Pros:**
- AI-powered visual comparison (ignores minor differences)
- More intelligent diff detection
- Better handling of dynamic content
- Advanced features (Ultrafast Grid, etc.)

**Cons:**
- More complex setup
- Steeper learning curve
- Free tier more limited

#### Integration Steps

1. **Create Applitools account** (free tier)
   - Visit https://applitools.com
   - Sign up with email
   - Verify email and create account

2. **Get API key from Applitools dashboard**
   - Navigate to account settings
   - Copy the API key

3. **Add `APPLITOOLS_API_KEY` as GitHub secret**
   - Go to repository Settings → Secrets and variables → Actions
   - Add new secret: `APPLITOOLS_API_KEY`

4. **Install Applitools SDK:**
   ```bash
   # For Playwright
   cd playwright
   npm install --save-dev @applitools/eyes-playwright
   
   # For Cypress
   cd cypress
   npm install --save-dev @applitools/eyes-cypress
   
   # For Selenium (Java)
   # Add to pom.xml:
   # <dependency>
   #   <groupId>com.applitools</groupId>
   #   <artifactId>eyes-selenium-java</artifactId>
   #   <version>LATEST</version>
   # </dependency>
   ```

5. **Configure Applitools in test files:**
   ```typescript
   // Playwright example
   import { Eyes } from '@applitools/eyes-playwright';
   
   test('visual test', async ({ page }) => {
     const eyes = new Eyes();
     await eyes.open(page, 'App Name', 'Test Name');
     await page.goto('/');
     await eyes.check('Homepage');
     await eyes.close();
   });
   ```

   ```typescript
   // Cypress example
   import '@applitools/eyes-cypress';
   
   it('visual test', () => {
     cy.visit('/');
     cy.eyesOpen({
       appName: 'App Name',
       testName: 'Homepage Test'
     });
     cy.eyesCheckWindow('Homepage');
     cy.eyesClose();
   });
   ```

   ```java
   // Selenium (Java) example
   import com.applitools.eyes.selenium.Eyes;
   
   Eyes eyes = new Eyes();
   eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));
   eyes.open(webDriver, "App Name", "Test Name");
   eyes.checkWindow("Homepage");
   eyes.close();
   ```

6. **Update CI/CD workflow to run Applitools**
   ```yaml
   - name: Run Applitools visual tests
     env:
       APPLITOOLS_API_KEY: ${{ secrets.APPLITOOLS_API_KEY }}
     run: npm test
   ```

#### Time Estimate: 8-12 hours (similar to Percy)

---

## 💡 Recommendation

### If Vibium meets current needs:
- ✅ Skip adding Percy/Applitools
- ✅ Focus on enhancing Vibium coverage instead
- ✅ Save 8-12 hours of implementation time

### If you need cloud-hosted baselines and team collaboration:
- ✅ Choose **Percy** for simpler setup and better documentation
- ✅ Choose **Applitools** for AI-powered features and intelligent comparison

### Decision Criteria

**Choose Percy if:**
- You want the simplest setup
- You need good documentation and community support
- Basic visual comparison is sufficient
- You're primarily using Playwright or Cypress

**Choose Applitools if:**
- You need AI-powered visual comparison
- You have dynamic content that causes false positives
- You want more intelligent diff detection
- You need advanced features like Ultrafast Grid

---

## ✅ Acceptance Criteria

- [ ] Percy or Applitools account created
- [ ] API key stored as GitHub secret
- [ ] SDK installed in relevant frameworks (Playwright/Cypress/Selenium)
- [ ] At least 5 visual test cases implemented
- [ ] CI/CD integration complete
- [ ] Baselines created and validated
- [ ] Visual tests run successfully in pipeline
- [ ] Documentation updated
- [ ] Team trained on using the tool (if applicable)

---

## 📝 Implementation Checklist

### Pre-Implementation
- [ ] Evaluate if Vibium meets current needs
- [ ] Decide between Percy and Applitools
- [ ] Create account and obtain API key
- [ ] Add API key as GitHub secret

### Implementation
- [ ] Install SDK for chosen framework(s)
- [ ] Create initial visual test cases
- [ ] Configure Percy/Applitools in test files
- [ ] Update CI/CD workflow
- [ ] Create initial baselines
- [ ] Test locally

### Post-Implementation
- [ ] Run visual tests in CI/CD
- [ ] Verify baselines are created
- [ ] Test visual diff detection
- [ ] Update documentation
- [ ] Train team (if applicable)

---

## 🔗 Related Documentation

- [Vibium Tests](../../vibium/README.md)
- [UI Testing Frameworks Guide](../../guides/testing/UI_TESTING_FRAMEWORKS.md)
- [CI/CD Workflows](../../.github/workflows/)
- [Playwright Configuration](../../playwright/playwright.config.ts)
- [Cypress Configuration](../../cypress/cypress.config.ts)

---

## 📊 Comparison Summary

| Feature | Vibium (Current) | Percy | Applitools |
|---------|------------------|-------|------------|
| **Account Required** | ❌ No | ✅ Yes (free) | ✅ Yes (free) |
| **Cloud Baselines** | ❌ No | ✅ Yes | ✅ Yes |
| **Team Collaboration** | ❌ No | ✅ Yes | ✅ Yes |
| **AI Comparison** | ❌ No | ❌ No | ✅ Yes |
| **Setup Complexity** | Low | Medium | Medium-High |
| **Free Tier** | N/A | 100-500/month | 100/month |
| **Best For** | Local testing | Simple setup | Advanced features |

---

**Last Updated**: 2026-01-04  
**Document Location**: `docs/work/20260104_VISUAL_REGRESSION_TESTING.md`

