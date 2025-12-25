# Smoke Test Suite - Implementation Plan

**Priority**: High
**Estimated Time**: 1-2 hours
**Impact**: Very High - Fast feedback loops
**Target Completion**: Next session

---

## 🎯 Objective

Create a **fast, reliable smoke test suite** that verifies critical functionality in under 2 minutes.

### **Goals:**
- ✅ Run in < 2 minutes (vs 15+ minutes for full suite)
- ✅ Cover critical paths only
- ✅ 100% reliable (no flaky tests)
- ✅ Run before commits (pre-push hook)
- ✅ Run first in CI/CD pipeline

---

## 📋 Smoke Test Requirements

### **What Makes a Good Smoke Test:**

**Must Be:**
- ⚡ **Fast** - Each test < 15 seconds
- 🎯 **Critical** - Tests core functionality
- 🛡️ **Stable** - Never flaky, always reliable
- 🔒 **Independent** - No dependencies between tests
- 📊 **Informative** - Failure indicates major issue

**Must Cover:**
- Grid connectivity
- Browser initialization
- Basic navigation
- Core application functionality
- Authentication (if applicable)

---

## 🎯 Proposed Smoke Tests (5 tests)

### **Test 1: Grid Connection** (Priority: BLOCKER)
```java
@Test
@Tag("smoke")
@Severity(SeverityLevel.BLOCKER)
@Description("Verify Selenium Grid is accessible and responsive")
public void smokeTest_GridConnection() {
    // Connect to Grid
    // Verify driver initializes
    // Check Grid has available sessions
    // Time: ~5 seconds
}
```

**Why Critical:** If Grid is down, nothing else works

---

### **Test 2: Homepage Load** (Priority: CRITICAL)
```java
@Test
@Tag("smoke")
@Severity(SeverityLevel.CRITICAL)
@Description("Verify primary application homepage loads")
public void smokeTest_HomepageLoads() {
    // Navigate to Google (or your main app)
    // Verify page title
    // Verify key element visible
    // Time: ~10 seconds
}
```

**Why Critical:** Homepage must always be accessible

---

### **Test 3: Search Functionality** (Priority: CRITICAL)
```java
@Test
@Tag("smoke")
@Severity(SeverityLevel.CRITICAL)
@Description("Verify basic search works")
public void smokeTest_SearchWorks() {
    // Navigate to search page
    // Enter search term
    // Verify results appear
    // Time: ~15 seconds
}
```

**Why Critical:** Search is core user functionality

---

### **Test 4: Navigation** (Priority: CRITICAL)
```java
@Test
@Tag("smoke")
@Severity(SeverityLevel.CRITICAL)
@Description("Verify navigation between pages")
public void smokeTest_NavigationWorks() {
    // Navigate to multiple pages
    // Verify each loads successfully
    // Verify back/forward works
    // Time: ~20 seconds
}
```

**Why Critical:** Users must be able to navigate

---

### **Test 5: Form Interaction** (Priority: NORMAL)
```java
@Test
@Tag("smoke")
@Severity(SeverityLevel.NORMAL)
@Description("Verify basic form input works")
public void smokeTest_FormInput() {
    // Find input field
    // Enter text
    // Verify text accepted
    // Clear field
    // Time: ~10 seconds
}
```

**Why Important:** Forms are common user interaction

---

## 📂 File Structure

### **Create New File:**
```
src/test/java/com/cjs/qa/junit/tests/SmokeTests.java
```

### **File Template:**
```java
package com.cjs.qa.junit.tests;

import com.cjs.qa.utilities.AllureHelper;
import io.qameta.allure.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.net.URL;
import java.time.Duration;

@Epic("Smoke Tests")
@Feature("Critical Path Verification")
public class SmokeTests {

    private WebDriver driver;
    private String gridUrl;

    @BeforeMethod
    public void setUp() throws Exception {
        // Fast setup - no extra options
        gridUrl = System.getenv("SELENIUM_REMOTE_URL");
        if (gridUrl == null || gridUrl.isEmpty()) {
            gridUrl = "http://localhost:4444/wd/hub";
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new RemoteWebDriver(new URL(gridUrl), options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15));
    }

    @Test(priority = 1)
    @Tag("smoke")
    @Severity(SeverityLevel.BLOCKER)
    public void smokeTest_GridConnection() {
        // Test implementation
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (driver != null) {
            if (result.getStatus() == ITestResult.FAILURE) {
                AllureHelper.captureScreenshot(driver, "SMOKE-FAILURE-" + result.getName());
            }
            driver.quit();
        }
    }
}
```

---

## ⚙️ Configuration

### **TestNG Suite for Smoke Tests:**

**Create:** `src/test/resources/testng-smoke-suite.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
<suite name="Smoke Test Suite" verbose="1">
    <test name="Critical Path Smoke Tests">
        <groups>
            <run>
                <include name="smoke"/>
            </run>
        </groups>
        <classes>
            <class name="com.cjs.qa.junit.tests.SmokeTests"/>
        </classes>
    </test>
</suite>
```

---

## 🚀 Usage

### **Run Smoke Tests:**

```bash
# Via Docker
docker-compose up -d selenium-hub chrome-node-1
docker-compose run --rm tests -Dtest=SmokeTests
docker-compose down

# Via TestNG suite
docker-compose run --rm tests -DsuiteXmlFile=testng-smoke-suite.xml

# Via Maven with groups
./mvnw test -Dgroups=smoke

# Quick script
./scripts/run-smoke-tests.sh
```

### **In CI/CD:**

Add to `.github/workflows/ci.yml`:

```yaml
jobs:
  smoke-tests:
    name: Smoke Tests (Fast)
    runs-on: ubuntu-latest

    steps:
      # ... setup steps ...

      - name: Run Smoke Tests
        run: |
          docker compose up -d selenium-hub chrome-node-1
          timeout 3m docker compose run --rm tests -Dtest=SmokeTests
          docker compose down
        timeout-minutes: 5
```

---

## ⏱️ Performance Targets

| Metric | Target | Current Full Suite |
|--------|--------|-------------------|
| **Total Time** | < 2 minutes | 15+ minutes |
| **Per Test** | < 15 seconds | varies |
| **Setup** | < 10 seconds | ~10 seconds |
| **Teardown** | < 5 seconds | ~5 seconds |
| **Tests** | 5 tests | 11 tests |

---

## 🎯 Success Criteria

### **Must Achieve:**
- [ ] All 5 smoke tests pass consistently
- [ ] Total execution < 2 minutes
- [ ] No flaky tests (100% pass rate)
- [ ] Meaningful failures (catches real issues)
- [ ] Works in Docker and locally

### **Nice to Have:**
- [ ] Screenshot on smoke test failure
- [ ] Smoke tests run on pre-push hook
- [ ] Separate CI job for smoke tests
- [ ] Smoke test badge in README

---

## 📝 Implementation Checklist

### **Phase 1: Create Tests (30 minutes)**
- [ ] Create `SmokeTests.java`
- [ ] Implement 5 smoke test methods
- [ ] Add Allure annotations
- [ ] Add screenshot capture on failure

### **Phase 2: Configuration (15 minutes)**
- [ ] Create `testng-smoke-suite.xml`
- [ ] Configure @Tag("smoke")
- [ ] Test locally with Docker

### **Phase 3: CI Integration (15 minutes)**
- [ ] Add smoke-tests job to workflow
- [ ] Run before full tests
- [ ] Configure to fail fast
- [ ] Test in GitHub Actions

### **Phase 4: Documentation (15 minutes)**
- [ ] Create `scripts/run-smoke-tests.sh`
- [ ] Add smoke test section to README
- [ ] Document in CI_TROUBLESHOOTING.md
- [ ] Update CHANGE.log

---

## 🎨 Helper Script

**Create:** `scripts/run-smoke-tests.sh`

```bash
#!/bin/bash
echo "🔥 Running Smoke Tests..."
echo "========================================"

# Start Grid (lightweight)
echo "Starting Grid..."
docker-compose -f docker-compose.dev.yml up -d

# Wait for ready
sleep 5

# Run smoke tests
echo "Running tests..."
docker-compose run --rm tests -Dtest=SmokeTests

# Capture exit code
TEST_EXIT=$?

# Stop Grid
echo "Stopping Grid..."
docker-compose down

# Report results
echo "========================================"
if [ $TEST_EXIT -eq 0 ]; then
    echo "✅ All smoke tests PASSED!"
else
    echo "❌ Smoke tests FAILED!"
fi

exit $TEST_EXIT
```

---

## 📊 Expected Results

### **Before Smoke Tests:**
```
Full test run: 15+ minutes
Feedback time: Too slow for pre-commit
CI/CD time: 6-7 minutes
```

### **After Smoke Tests:**
```
Smoke tests: < 2 minutes ✅
Full tests: Still 15+ minutes (when needed)
Feedback: Fast (use smoke for quick checks)
CI/CD: Smoke first (fast fail), then full tests
```

### **Workflow Strategy:**
```
On Push → Run Smoke Tests (2 min)
  ↓
If Pass → Run Full Tests (15 min)
  ↓
If Pass → Deploy/Merge
```

---

## 💡 Advanced Features (Future)

### **Smoke Test Variations:**

**1. Browser-Specific Smoke Tests**
```java
@Test(groups = {"smoke", "chrome-only"})
```

**2. Environment-Specific**
```java
@Test(groups = {"smoke", "production"})
```

**3. Tiered Smoke Tests**
```java
@Test(groups = {"smoke", "tier1"})  // Most critical
@Test(groups = {"smoke", "tier2"})  // Important
```

### **Integration Options:**

**Pre-Push Hook:**
```bash
# .git/hooks/pre-push
./scripts/run-smoke-tests.sh || exit 1
```

**VS Code Task:**
```json
{
    "label": "Run Smoke Tests",
    "type": "shell",
    "command": "./scripts/run-smoke-tests.sh"
}
```

**IntelliJ Run Configuration:**
- Create new TestNG run config
- Suite: testng-smoke-suite.xml
- Name: "Smoke Tests"

---

## 🎯 Benefits

### **For Developers:**
- ✅ Fast feedback (2 min vs 15 min)
- ✅ Run before commit
- ✅ Quick sanity check
- ✅ Confidence in changes

### **For CI/CD:**
- ✅ Fail fast (save resources)
- ✅ Quick PR checks
- ✅ Faster feedback to developers
- ✅ Reduced pipeline time

### **For Team:**
- ✅ Consistent quality checks
- ✅ Catch breaking changes early
- ✅ Reduce debugging time
- ✅ Improve confidence

---

## 📈 ROI Analysis

### **Time Savings:**

**Current:**
- Full test run: 15 minutes
- Feedback delay: 15 minutes
- False positive debugging: 30+ minutes

**With Smoke Tests:**
- Smoke run: 2 minutes ✅
- Feedback delay: 2 minutes ✅
- Quick verification: No full run needed ✅

**Savings:** ~13 minutes per check × 10 checks/day = **2+ hours/day**

---

## 🎊 Success Story (Future)

**After Implementation:**

```bash
$ ./scripts/run-smoke-tests.sh

🔥 Running Smoke Tests...
========================================
Starting Grid...
✅ Grid ready

Running tests...
✅ Grid Connection (3.2s)
✅ Homepage Loads (8.1s)
✅ Search Works (12.5s)
✅ Navigation Works (18.3s)
✅ Form Input (7.9s)

Tests run: 5, Failures: 0, Errors: 0
Time: 52 seconds

Stopping Grid...
========================================
✅ All smoke tests PASSED!
```

**Developer Reaction:** "This is amazing! I know immediately if my changes broke anything!" 🎉

---

## 📋 Implementation Order

### **Next Session - Do This:**

1. **Create SmokeTests.java** (20 min)
2. **Create testng-smoke-suite.xml** (5 min)
3. **Test locally** (10 min)
4. **Create run-smoke-tests.sh** (10 min)
5. **Add to GitHub Actions** (15 min)
6. **Update documentation** (10 min)
7. **Commit & push** (5 min)

**Total:** ~75 minutes

---

## 🎯 Acceptance Criteria

Test is complete when:

- [x] 5 smoke tests created and passing
- [x] Execution time < 2 minutes
- [x] 100% pass rate (no flakiness)
- [x] TestNG suite configured
- [x] Helper script created
- [x] CI/CD integrated
- [x] Documentation updated
- [x] Tested in both Docker and CI

---

## 🚀 Quick Start Template

**Copy-paste to start next session:**

```bash
# 1. Create smoke test file
cat > src/test/java/com/cjs/qa/junit/tests/SmokeTests.java << 'EOF'
package com.cjs.qa.junit.tests;

import com.cjs.qa.utilities.AllureHelper;
import io.qameta.allure.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.net.URL;
import java.time.Duration;

@Epic("Smoke Tests")
@Feature("Critical Path Verification")
public class SmokeTests {

    private WebDriver driver;
    private String gridUrl;

    @BeforeMethod
    public void setUp() throws Exception {
        gridUrl = System.getenv("SELENIUM_REMOTE_URL");
        if (gridUrl == null || gridUrl.isEmpty()) {
            gridUrl = "http://localhost:4444/wd/hub";
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");
        driver = new RemoteWebDriver(new URL(gridUrl), options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15));
    }

    @Test(priority = 1, groups = "smoke")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify Selenium Grid connection")
    public void smokeTest_GridConnection() {
        Allure.step("Verify driver initialized");
        Assert.assertNotNull(driver, "Driver should be initialized");
    }

    @Test(priority = 2, groups = "smoke")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify homepage loads")
    public void smokeTest_HomepageLoads() {
        Allure.step("Navigate to homepage");
        driver.get("https://www.google.com");

        Allure.step("Verify page loaded");
        String title = driver.getTitle();
        Assert.assertTrue(title.contains("Google"));
    }

    // Add 3 more tests...

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (driver != null) {
            if (result.getStatus() == ITestResult.FAILURE) {
                AllureHelper.captureScreenshot(driver, "SMOKE-FAIL-" + result.getName());
            }
            driver.quit();
        }
    }
}
EOF

# 2. Create TestNG suite
cat > src/test/resources/testng-smoke-suite.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
<suite name="Smoke Test Suite" verbose="1">
    <test name="Critical Path Smoke Tests">
        <groups>
            <run>
                <include name="smoke"/>
            </run>
        </groups>
        <classes>
            <class name="com.cjs.qa.junit.tests.SmokeTests"/>
        </classes>
    </test>
</suite>
EOF

# 3. Test it
docker-compose up -d selenium-hub chrome-node-1
docker-compose run --rm tests -Dtest=SmokeTests
docker-compose down

# Done! If all pass, commit it!
```

---

## 📊 Comparison

### **Smoke Tests vs Full Tests:**

| Aspect | Smoke Tests | Full Tests |
|--------|-------------|------------|
| **Purpose** | Quick verification | Comprehensive coverage |
| **Tests** | 5 critical | 11+ comprehensive |
| **Time** | < 2 minutes | 15+ minutes |
| **Frequency** | Before every commit | On PR, nightly |
| **Coverage** | Critical paths | All functionality |
| **Browsers** | Chrome only | Chrome + Firefox |
| **When** | Pre-push, on every push | PR merge, scheduled |

---

## 🎯 Integration Plan

### **GitHub Actions Workflow:**

```yaml
jobs:
  smoke-tests:
    name: Smoke Tests (Fast Check)
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: 'maven'

      - name: Start Grid
        run: docker compose -f docker-compose.dev.yml up -d

      - name: Run Smoke Tests
        timeout-minutes: 3
        run: docker compose run --rm tests -Dtest=SmokeTests

      - name: Stop Grid
        if: always()
        run: docker compose down

  full-tests:
    name: Full Test Suite
    needs: smoke-tests  # Only run if smoke passes
    if: success()
    # ... full test configuration ...
```

**Benefit:** If smoke fails (2 min), skip full tests (15 min) - save 13 minutes!

---

## 📝 Documentation Updates Needed

After implementing smoke tests:

1. **README.md** - Add smoke test section
2. **docs/GITHUB_ACTIONS.md** - Document smoke tests job
3. **docs/CHANGE.log** - Record implementation
4. **scripts/README.md** - Add run-smoke-tests.sh

---

## 🎊 Expected Outcome

**After Implementation:**

**Developer Experience:**
```bash
# Before committing large changes:
$ ./scripts/run-smoke-tests.sh

# 2 minutes later:
✅ All critical paths working!
# Confidence to push!
```

**CI/CD Experience:**
```
Push to PR → Smoke tests (2 min) → ✅ Pass → Full tests (15 min) → ✅ Pass → Merge!

vs

Push to PR → Full tests (15 min) → ❌ Fail → Fix → 15 min → ❌ Fail → ...
```

**Time Saved:** Hours per week!

---

## 🏆 Success Metrics

Track these after implementation:

- Smoke test execution time
- Smoke test pass rate
- False positive rate
- Time saved vs full suite
- Developer adoption rate
- Issues caught by smoke tests

---

**Status**: 📋 Planned for next session
**Priority**: 🔥 High
**Estimated ROI**: Very High (2+ hours/day time savings)

---

**Ready to implement? This will be a quick, high-value addition!** 🚀
