# Allure Test Reporting

**Status**: ✅ Configured with configurable Allure CLI (defaults to Allure2)
**Version**: Allure2 CLI 2.36.0 (default), Allure3 CLI 3.0.0 (optional), Allure2 Java libraries 2.32.0
**Framework**: TestNG
**Date**: November 8, 2025
**Last Updated**: January 8, 2026
**Note**: Allure CLI version is configurable via `config/environments.json`. Default is Allure2 CLI 2.36.0. Can be switched to Allure3 CLI 3.0.0 by changing `allure.reportVersion` to `3`.

---

## 🎯 Overview

Allure Framework provides beautiful, interactive HTML test reports with:
- 📊 **Visual Dashboards** - Graphs, charts, and trends
- 📸 **Screenshot Support** - Attach screenshots on failures
- ⏱️ **Performance Metrics** - Test execution times
- 📈 **Historical Trends** - Track improvements over time
- 🏷️ **Categorization** - Group by Epic, Feature, Story, Severity
- 📝 **Detailed Steps** - See exactly what each test did

---

## ✅ What's Configured

### Dependencies Added (pom.xml)
- `allure-testng:2.32.0` - TestNG integration (latest in Maven Central)
- `allure-java-commons:2.32.0` - Core Allure functionality (latest in Maven Central)
- **Note**: Allure CLI version is configurable via `config/environments.json`. Default is Allure2 CLI 2.36.0. Java libraries remain at Allure2 2.32.0 (latest in Maven Central) regardless of CLI version.
- `aspectjweaver:1.9.22` - For Allure step tracking

### Maven Plugins
- `maven-surefire-plugin` - Configured with Allure listener
- `allure-maven:2.12.0` - For report generation

### Annotated Tests
- ✅ `SimpleGridTest.java` - 3 tests with Allure annotations
- ✅ `EnhancedGridTests.java` - 8 tests with Allure annotations
- Total: **11 tests** ready for Allure reporting

---

## 🚀 Quick Start

### Method 1: Using Helper Script (Easiest)

```bash
# One command does everything:
./scripts/generate-allure-report.sh

# This will:
# 1. Start Selenium Grid
# 2. Run all tests
# 3. Stop Grid
# 4. Generate Allure report
# 5. Open in browser automatically
```

### Method 2: Manual Steps

```bash
# 1. Start Grid
docker-compose up -d selenium-hub chrome-node-1

# 2. Run tests
docker-compose run --rm tests -Dtest=SimpleGridTest,EnhancedGridTests

# 3. Generate report (requires Allure CLI)
allure serve target/allure-results

# 4. Stop Grid
docker-compose down
```

### Method 3: Using Maven Plugin

```bash
# Run tests
./mvnw clean test -Dtest=SimpleGridTest

# Generate report
./mvnw allure:serve
# Opens browser with interactive report
```

---

## 📦 Installing Allure CLI

**Note**: In CI/CD, Allure CLI is automatically installed by `scripts/ci/install-allure-cli.sh`, which reads the version from `config/environments.json`. For local development, you can install manually:

### Configuration

Allure CLI version is configured in `config/environments.json`:

```json
"allure": {
  "reportVersion": 2,
  "cliVersion": {
    "2": "2.36.0",
    "3": "3.0.0"
  }
}
```

To switch versions, edit `config/environments.json` and change `reportVersion` to `2` (Allure2) or `3` (Allure3).

### Manual Installation (Local Development)

#### Allure2 CLI (Default)

**macOS (Homebrew)**:
```bash
brew install allure
```

**Linux (Manual)**:
```bash
wget https://github.com/allure-framework/allure2/releases/download/2.36.0/allure-2.36.0.tgz
tar -zxvf allure-2.36.0.tgz
sudo mv allure-2.36.0 /opt/allure
sudo ln -s /opt/allure/bin/allure /usr/local/bin/allure
```

**Windows (Scoop)**:
```bash
scoop install allure
```

#### Allure3 CLI (Optional)

```bash
npm install -g allure@3.0.0
```

### Verify Installation
```bash
allure --version
# Should show: 2.36.0 (if using Allure2) or 3.0.0 (if using Allure3)
```

---

## 📊 Report Features

### What You'll See

**1. Overview Dashboard**
- Total tests run
- Pass/Fail rate
- Test duration
- Trend graphs

**2. Test Suites**
- Organized by package
- Each test with status
- Execution time
- Error details (if any)

**3. Categorization**
```java
@Epic("Selenium Grid Testing")     // High-level feature area
@Feature("Enhanced Grid Tests")    // Specific feature
@Story("Search Functionality")     // User story
@Severity(SeverityLevel.CRITICAL)  // Importance level
@Description("...")                // Detailed description
```

**4. Test Steps**
```java
Allure.step("Navigate to Google homepage");
Allure.step("Enter search term: Selenium");
Allure.step("Submit search");
```

**5. Attachments** (can add)
```java
// Attach screenshot on failure
Allure.addAttachment("Screenshot", new ByteArrayInputStream(screenshot));

// Attach logs
Allure.addAttachment("Browser Log", "text/plain", browserLog);
```

---

## 🎨 Current Test Annotations

### SimpleGridTest (3 tests)
- **Epic**: Selenium Grid Testing
- **Feature**: Basic Grid Tests
- **Stories**: Grid Connection, Basic Navigation
- **Severities**: BLOCKER (connection), CRITICAL (navigation)

### EnhancedGridTests (8 tests)
- **Epic**: Selenium Grid Testing
- **Feature**: Enhanced Grid Tests
- **Stories**:
  - Homepage Navigation
  - Search Functionality
  - Multi-Site Navigation
  - Performance Testing
  - Browser Features
  - Form Interactions
  - Responsive Design
- **Severities**: CRITICAL, NORMAL, MINOR

---

## 📈 Example Report Structure

```
Allure Report
├── Overview
│   ├── 11 tests total
│   ├── 11 passed (100%)
│   ├── 0 failed
│   └── Duration: 16.4 seconds
│
├── Suites
│   ├── SimpleGridTest (3 tests)
│   └── EnhancedGridTests (8 tests)
│
├── Graphs
│   ├── Status pie chart
│   ├── Severity distribution
│   ├── Duration graph
│   └── Timeline
│
├── Categories
│   ├── By Epic
│   ├── By Feature
│   ├── By Story
│   └── By Severity
│
└── Timeline
    └── Test execution flow
```

---

## 💡 Usage Examples

### Run Specific Test Suite
```bash
# Run only simple tests
docker-compose run --rm tests -Dtest=SimpleGridTest
allure serve target/allure-results

# Run only enhanced tests
docker-compose run --rm tests -Dtest=EnhancedGridTests
allure serve target/allure-results

# Run both
docker-compose run --rm tests -Dtest=SimpleGridTest,EnhancedGridTests
allure serve target/allure-results
```

### Run with TestNG Suite XML
```bash
# Parallel execution across browsers
docker-compose run --rm tests -DsuiteXmlFile=testng-grid-suite.xml
allure serve target/allure-results
```

### Generate Report Without Opening
```bash
# Generate static HTML report
rm -rf target/allure-report
allure generate target/allure-results -o target/allure-report

# Open manually
open target/allure-report/index.html
```

---

## 🔧 Configuration Files

### allure.properties
Located: `src/test/resources/allure.properties`

```properties
allure.results.directory=target/allure-results
allure.link.issue.pattern=https://github.com/CScharer/full-stack-qa/issues/{}
allure.link.tms.pattern=https://github.com/CScharer/full-stack-qa/issues/{}
```

### Maven Surefire Configuration
```xml
<configuration>
    <properties>
        <property>
            <name>listener</name>
            <value>io.qameta.allure.testng.AllureTestNg</value>
        </property>
    </properties>
    <argLine>
        -javaagent:"${settings.localRepository}/org/aspectj/aspectjweaver/${aspectj.version}/aspectjweaver-${aspectj.version}.jar"
    </argLine>
</configuration>
```

---

## 🎯 Test Results

### Latest Test Run
```
✅ Tests run: 11
✅ Failures: 0
✅ Errors: 0
✅ Skipped: 0
✅ Time: 16.4 seconds
✅ Success Rate: 100%
```

### Test Breakdown
| Test Suite | Tests | Status |
|------------|-------|--------|
| SimpleGridTest | 3 | ✅ All Passing |
| EnhancedGridTests | 8 | ✅ All Passing |
| **Total** | **11** | **100%** |

---

## 📸 Adding Screenshots

**Note**: Screenshots are only captured on test failures to reduce storage and improve report performance. Passing tests do not generate screenshots.

### On Test Failure
```java
@AfterMethod
public void tearDown(ITestResult result) {
    if (result.getStatus() == ITestResult.FAILURE) {
        // Capture screenshot
        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Allure.addAttachment("Failure Screenshot",
            new ByteArrayInputStream(screenshot));
    }
    driver.quit();
}
```

### During Test Steps
```java
@Test
public void myTest() {
    Allure.step("Navigate to page", () -> {
        driver.get("https://example.com");
        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Allure.addAttachment("Page Screenshot", new ByteArrayInputStream(screenshot));
    });
}
```

---

## 🏷️ Annotation Reference

### Severity Levels
```java
@Severity(SeverityLevel.BLOCKER)   // Critical infrastructure
@Severity(SeverityLevel.CRITICAL)  // Core functionality
@Severity(SeverityLevel.NORMAL)    // Standard features
@Severity(SeverityLevel.MINOR)     // Nice-to-have
@Severity(SeverityLevel.TRIVIAL)   // Cosmetic issues
```

### Organization
```java
@Epic("Feature Area")              // High-level grouping
@Feature("Specific Feature")       // Feature being tested
@Story("User Story")               // User story reference
@Owner("Developer Name")           // Test owner
@Link("https://...")               // Related links
@Issue("JIRA-123")                // Issue tracking
@TmsLink("TC-456")                // Test management system
```

### Test Metadata
```java
@Description("Detailed test description")
@Severity(SeverityLevel.CRITICAL)
@Flaky                            // Mark as occasionally flaky
@Muted                            // Muted from results
```

---

## 📂 Report Locations

### Allure Results (JSON)
```
target/allure-results/
├── *-result.json          (Test results)
├── *-container.json       (Test containers)
└── *-attachment.*         (Screenshots, logs)
```

### Allure Report (HTML)
```
target/allure-report/
├── index.html            (Main dashboard)
├── data/                 (Report data)
├── widgets/              (UI components)
└── history/              (Historical data)
```

---

## 🎯 Next Steps

### 1. Install Allure CLI
```bash
brew install allure
```

### 2. Run Tests and Generate Report
```bash
./scripts/generate-allure-report.sh
```

### 3. View Report
The report will automatically open in your default browser!

---

## 🌟 Advanced Features

### Historical Trends

**Status**: ✅ **Active** - Automatic history preservation in CI/CD pipeline  
**Last Updated**: 2026-01-04

Allure reports automatically track test execution trends over time, showing:
- **Test pass/fail rates** over multiple runs
- **Duration trends** - Performance regressions and improvements
- **Flaky test detection** - Tests that sometimes fail
- **Historical test execution data** - Complete execution history

#### How It Works

**In CI/CD Pipeline** (Automatic):
1. Before generating a new report, the pipeline downloads history from the previous deployment
2. History is merged with new test results during report generation
3. **Zero durations are automatically fixed** (prevents NaN errors in trend charts)
4. Updated history is included in the new report
5. History is preserved for the next run (both in GitHub Pages and as artifact)

**History Preservation Methods**:
- **Primary**: Downloads from GitHub Pages (previous deployment)
- **Fallback**: Downloads from GitHub Actions artifact (if GitHub Pages unavailable)
- **Upload**: History is uploaded as artifact (90-day retention) for reliability

**Zero Duration Fix** (Automatic):
- Some tests may have `start == stop`, resulting in `duration = 0`
- Zero durations cause NaN errors in SVG chart rendering, resulting in empty trend charts
- The pipeline automatically fixes this by setting minimum duration of 1ms for affected entries
- This ensures chart coordinates can be calculated correctly
- See troubleshooting section for more details

**Manual Local Usage**:
```bash
# After generating report, save history
cp -r target/allure-report/history target/allure-results/

# Next run will show trends!
```

**Note**: In CI/CD, this is handled automatically - no manual steps required.

### Environment Information
Create `target/allure-results/environment.properties`:
```properties
Browser=Chrome
Browser.Version=120
Selenium.Version=4.39.0
Grid.URL=http://selenium-hub:4444
Java.Version=21
```

### Categories (Custom)
Create `target/allure-results/categories.json`:
```json
[
  {
    "name": "Product Defects",
    "matchedStatuses": ["failed"]
  },
  {
    "name": "Test Defects",
    "matchedStatuses": ["broken"]
  }
]
```

---

## 🎊 Benefits

### For Developers
- ✅ Clear test status at a glance
- ✅ Detailed failure information
- ✅ Easy to debug with screenshots
- ✅ Historical comparison

### For Management
- ✅ Professional visual reports
- ✅ Test coverage visibility
- ✅ Quality trends over time
- ✅ ROI demonstration

### For QA Team
- ✅ Test documentation
- ✅ Regression tracking
- ✅ Flaky test identification
- ✅ Performance monitoring

---

## 📞 Troubleshooting

### Common Installation Issues

#### "allure: command not found"
**Cause**: Allure CLI is not installed or not in PATH.

**Solution**:
```bash
# For Allure2 (default):
./scripts/ci/install-allure-cli.sh

# Or manually install:
# macOS
brew install allure

# Linux (download from GitHub releases)
wget https://github.com/allure-framework/allure2/releases/download/2.36.0/allure-2.36.0.tgz
tar -xzf allure-2.36.0.tgz
sudo mv allure-2.36.0 /opt/allure
sudo ln -s /opt/allure/bin/allure /usr/local/bin/allure
```

#### "jq: command not found"
**Cause**: `jq` is required to read configuration from `config/environments.json`.

**Solution**:
```bash
# macOS
brew install jq

# Linux
sudo apt-get install jq
# or
sudo yum install jq
```

#### "Java is not installed" (Allure2 only)
**Cause**: Allure2 CLI requires Java runtime.

**Solution**:
```bash
# macOS
brew install openjdk

# Linux
sudo apt-get install default-jdk
# or
sudo yum install java-11-openjdk-devel
```

### Report Generation Issues

#### "No Allure results found"
**Cause**: Tests haven't run or results directory is empty.

**Solution**:
```bash
# Run tests first
docker-compose run --rm tests -Dtest=SimpleGridTest

# Verify results exist
ls -la target/allure-results/

# Check for result files
find target/allure-results -name "*.json" | head -5
```

#### Report doesn't open
**Cause**: Report generation failed or file path is incorrect.

**Solution**:
```bash
# Regenerate report
rm -rf target/allure-report
allure generate target/allure-results -o target/allure-report

# Verify report was created
ls -la target/allure-report/

# Open manually
open target/allure-report/index.html
# or
python3 -m http.server 8080 -d target/allure-report
```

### History and Trending Issues

#### History not appearing in reports
**Cause**: History files not being preserved or downloaded correctly.

**Verification Steps**:
1. **Check Pipeline Logs**:
   - Look for "Download Previous Allure History" steps
   - Verify "History directory found with X file(s)" message
   - Check "History included in report" verification step

2. **Check GitHub Pages**:
   ```bash
   # View history files via GitHub API
   curl -s -H "Accept: application/vnd.github.v3+json" \
     "https://api.github.com/repos/CScharer/full-stack-qa/contents/history?ref=gh-pages" | \
     jq '.[] | select(.type == "file") | {name: .name, size: .size}'
   ```

3. **Check Artifacts**:
   - Go to Actions → Latest run → Artifacts
   - Look for `allure-history` artifact
   - Download and verify it contains history files

4. **Check Report**:
   - Open Allure report on GitHub Pages
   - Navigate to "Trends" or "Graphs" section
   - Verify historical data is displayed

**Common Causes and Solutions**:

**Issue**: "No history directory found"
- **Cause**: First run or history download failed
- **Solution**: Expected for first run. For subsequent runs, check download steps in logs

**Issue**: "History directory exists but is empty"
- **Cause**: History files are empty arrays `[]`
- **Solution**: Script removes empty history. Wait for next run to populate

**Issue**: "History not persisting across deployments"
- **Cause**: `keep_files: false` was wiping history (fixed in PR #75)
- **Solution**: Ensure `keep_files: true` in GitHub Pages deployment settings

**Issue**: "Duplicate build orders in history"
- **Cause**: History merge logic not deduplicating correctly
- **Solution**: Fixed in PR #104, #105 using `group_by(.buildOrder) | map(last)`

**Issue**: "Nested arrays in history"
- **Cause**: History merge creating nested structure
- **Solution**: Fixed in PR #102 using `flatten` in jq commands

**Issue**: "Empty trend chart with NaN errors in browser console"
- **Cause**: Zero durations (start == stop) in history-trend.json cause NaN values when Allure2 calculates chart coordinates
- **Symptoms**: 
  - Trend chart displays but is empty (no data points)
  - Browser console shows SVG errors: `Expected number, "M0,NaNL..."` and `Expected length, "NaN"`
  - History files exist and have data, but chart can't render
- **Solution**: 
  - Script `fix-allure-history-zero-durations.sh` automatically fixes zero durations
  - Sets minimum duration of 1ms for entries where start == stop
  - Runs automatically during report generation (Step 5.5)
  - Ensures all durations are valid numbers for chart calculations
- **Verification**:
  ```bash
  # Check for zero durations in history
  jq '[.[] | .data[] | select(.time.duration == 0 or .time.start == .time.stop)] | length' \
    allure-report-combined/history/history-trend.json
  # Should return 0 after fix
  ```

### Debugging Commands

#### Check History in Results Directory
```bash
# Count history files
find allure-results-combined/history -type f -name "*.json" | wc -l

# View history-trend.json structure (Allure2)
cat allure-results-combined/history/history-trend.json | jq '.'

# View history.jsonl structure (Allure3)
cat allure-results-combined/history/history.jsonl | head -5 | jq '.'

# Check for duplicates
cat allure-results-combined/history/history-trend.json | jq '[.[] | .buildOrder] | group_by(.) | map(select(length > 1))'

# Check if nested
cat allure-results-combined/history/history-trend.json | jq 'type'  # Should be "array", not "object"
```

#### Check History in Report Directory
```bash
# Count history files
find allure-report-combined/history -type f -name "*.json" | wc -l

# View history structure
cat allure-report-combined/history/history-trend.json | jq '.'
```

#### Check GitHub Pages History
```bash
# Download via curl
curl -s "https://cscharer.github.io/full-stack-qa/history/history-trend.json" | jq '.'

# Check via GitHub API
curl -s -H "Accept: application/vnd.github.v3+json" \
  "https://api.github.com/repos/CScharer/full-stack-qa/contents/history?ref=gh-pages" | \
  jq '.[] | select(.type == "file") | {name: .name, size: .size}'
```

#### Check Build Order
```bash
# From executor.json
cat allure-results-combined/executor.json | jq '.buildOrder'

# From history files
cat allure-results-combined/history/history-trend.json | jq '[.[] | .buildOrder] | sort | unique'
```

#### Verify Configuration
```bash
# Check Allure version from config
jq '.allure.reportVersion' config/environments.json

# Check CLI versions
jq '.allure.cliVersion' config/environments.json

# Verify script can read config
ALLURE_VERSION=$(jq -r '.allure.reportVersion // 2' config/environments.json)
echo "Allure version: $ALLURE_VERSION"
```

### Verification Checklist

**After Each Pipeline Run**:
- [ ] History download steps completed successfully
- [ ] History files exist in `allure-results-combined/history/`
- [ ] History files are not empty (size > 3 bytes)
- [ ] No duplicate build orders in history files
- [ ] History structure is flat array (not nested)
- [ ] History exists in `allure-report-combined/history/` after generation
- [ ] History uploaded as artifact (if on main branch)
- [ ] History deployed to GitHub Pages (if on main branch)
- [ ] History visible in Allure report (if trends section exists)

**For First Run**:
- [ ] History download steps show "No history found (expected for first run)"
- [ ] Report generation completes successfully
- [ ] History directory may or may not exist (both are acceptable)
- [ ] History upload step may be skipped (expected)

**For Subsequent Runs**:
- [ ] History downloaded from GitHub Pages or artifact
- [ ] History merged with current run's data
- [ ] History includes multiple build orders
- [ ] History deployed to GitHub Pages
- [ ] Trends visible in Allure report

### Key Learnings

#### 1. GitHub Pages `keep_files` Behavior
- `keep_files: false` wipes entire branch on each deployment
- `keep_files: true` preserves existing files
- **This was the most critical fix** (PR #75)

#### 2. Allure History Requirements
- Allure2: Requires individual JSON files in `history/` directory
- Allure3: Requires `history.jsonl` file in `history/` directory
- History must be in `RESULTS_DIR/history/` before `allure generate`
- History files must be valid JSON with actual data (not empty arrays)

#### 3. History File Format
- Must be flat array (not nested)
- Must have one entry per buildOrder (deduplicated)
- Must contain actual test execution data
- Structure must match Allure's expected format

#### 4. Configuration Best Practices
- Use `config/environments.json` for centralized configuration
- Scripts automatically read from config file
- No manual script changes needed when switching versions
- Default to Allure2 for production use

---

## 🔗 Resources

- **Allure Documentation**: https://docs.qameta.io/allure/
- **TestNG Integration**: https://docs.qameta.io/allure/#_testng
- **Allure GitHub**: https://github.com/allure-framework/allure2
- **Examples**: https://github.com/allure-examples

---

## 📝 Example Report Output

When you run `allure serve target/allure-results`, you'll see:

```
Generating report to temp directory...
Report successfully generated to /var/folders/.../allure-report
Starting web server...
Server started at <http://192.168.1.100:63342>. Press <Ctrl+C> to exit
```

Your browser will automatically open showing:
- **Overview** - Summary dashboard with graphs
- **Suites** - All test suites and their tests
- **Graphs** - Visual representation of results
- **Timeline** - Test execution timeline
- **Behaviors** - Organized by Epic/Feature/Story
- **Packages** - Organized by package structure

---

**Status**: ✅ Ready to use!
**Next Step**: Run `./scripts/generate-allure-report.sh` or install Allure CLI

---

---

## 🔄 CI/CD Combined Report Generation

**Status**: ✅ **Active** - Combined reports with multi-environment support  
**Last Updated**: 2025-12-30

### Overview

The CI/CD pipeline generates combined Allure reports that merge test results from:
- **Frontend (FE) Tests**: UI tests from multiple environments (dev, test, prod)
  - **TestNG-based**: Smoke, Grid, Mobile, Responsive, Selenide tests (native Allure support)
  - **Cypress**: E2E tests converted to Allure format
  - **Playwright**: E2E tests converted to Allure format
  - **Robot Framework**: Acceptance tests converted to Allure format
  - **Vibium**: Visual regression tests converted to Allure format
- **Backend (BE) Tests**: Performance tests (Locust, Gatling, JMeter) converted to Allure format

### Key Features

- ✅ **Multi-Environment Support**: Tests from dev, test, and prod environments are combined into a single report
- ✅ **Environment Labeling**: Each test is labeled with its environment (dev/test/prod) to prevent deduplication
- ✅ **Multi-Framework Support**: All test frameworks are converted and included:
  - TestNG-based tests (Smoke, Grid, Mobile, Responsive, Selenide) - Native Allure support
  - Cypress - Individual test results converted from JSON
  - Playwright - Individual test results converted from JSON
  - Robot Framework - Individual test results converted from XML
  - Vibium - Individual test results converted from Vitest JSON
- ✅ **Individual Test Results**: All frameworks show individual test cases (not summaries)
- ✅ **Performance Test Integration**: BE test results are automatically converted and included
- ✅ **GitHub Pages Deployment**: Reports are automatically deployed to GitHub Pages on `main` branch
- ✅ **Multi.Environment Flag**: Correctly identifies when tests ran in multiple environments

### Implementation Details

#### Environment Detection

The system uses artifact name patterns to detect environments:
- `*-results-dev` → dev environment
- `*-results-test` → test environment
- `*-results-prod` → prod environment

**Scripts**: 
- `scripts/ci/merge-allure-results.sh` - Merges results from all environments
- `scripts/ci/add-environment-labels.sh` - Adds environment labels to test results, fixes Selenide suite labels, and updates Smoke test suite labels
- `scripts/ci/deduplicate-testng-retries.sh` - Deduplicates TestNG retry attempts (keeps best result)
- `scripts/ci/create-framework-containers.sh` - Creates framework container files, detects and groups Smoke tests
- `scripts/ci/convert-cypress-to-allure.sh` - Converts Cypress results to Allure format (individual tests, all environments)
- `scripts/ci/convert-playwright-to-allure.sh` - Converts Playwright results to Allure format (individual tests, deduplicates retries)
- `scripts/ci/convert-robot-to-allure.sh` - Converts Robot Framework results to Allure format (individual tests)
- `scripts/ci/convert-vibium-to-allure.sh` - Converts Vibium/Vitest results to Allure format (individual tests)
- `scripts/ci/convert-artillery-to-allure.sh` - Converts Artillery (FS) load test results to Allure format
- `scripts/ci/allure_metadata_utils.py` - Shared Python utilities for adding verification metadata to Allure results
- `scripts/ci/allure-metadata-utils.sh` - Shared bash functions for adding verification metadata to Allure results

#### Environment Labeling

To prevent Allure from deduplicating the same test across different environments:
1. Environment labels are added to all test result files
2. `historyId` is updated to include environment: `md5(fullName:environment)`
3. Handles cases where `fullName` doesn't exist (fallback to `name` field)

#### Framework Test Conversion

Frontend framework test results are converted to Allure format:
- **Cypress**: Parses `cypress-results.json` or `mochawesome.json` files
  - Creates individual Allure results for each test
  - Recursively searches for test objects in JSON structure
  - **Environment Support**: Processes all environments (dev, test, prod) from merged artifacts
  - Handles environment-specific artifact subdirectories (e.g., `cypress-results/cypress-results-dev/...`)
- **Playwright**: Parses JUnit XML files from test-results directory
  - Creates individual Allure results for each test case
  - **Retry Deduplication**: Intelligently handles retry attempts:
    - Tests that passed on first attempt: Keeps the test (Playwright's retries: 1 retries all tests, but we keep passed ones)
    - Tests that failed and were retried: Keeps final result, marks as flaky if status changed (failed → passed)
    - Preserves retry information for analysis
    - **Note**: Only deduplicates actual retries of failed tests, not passed tests with duplicates from retry config
- **Robot Framework**: Parses `output.xml` files
  - Creates individual Allure results from `<test>` elements
  - Extracts test name, status, and duration
- **Vibium**: Parses Vitest JSON result files
  - Creates individual Allure results from `assertionResults` array
  - Properly maps test statuses (passed/failed/skipped)
- **Smoke Tests**: Automatically detected and grouped under "Smoke Tests" suite
  - Detected by `epic="Smoke Tests"` label in both `add-environment-labels.sh` and `create-framework-containers.sh`
  - Suite label updated from "Surefire test" to "Smoke Tests" in result files
  - Grouped separately from "Surefire test" suite
  - Shows for all environments if environment labels are correctly set

**Scripts**: 
- `scripts/ci/convert-cypress-to-allure.sh` - Converts Cypress JSON results, handles environment-specific artifact subdirectories
- `scripts/ci/convert-playwright-to-allure.sh` - Converts Playwright JUnit XML results, deduplicates retry attempts intelligently
- `scripts/ci/convert-robot-to-allure.sh` - Converts Robot Framework XML results
- `scripts/ci/convert-vibium-to-allure.sh` - Converts Vibium/Vitest JSON results
- `scripts/ci/convert-artillery-to-allure.sh` - Converts Artillery (FS) load test results to Allure format

**Shared Utilities**:
- `scripts/ci/allure_metadata_utils.py` - Python module providing `add_verification_metadata_to_params()` function
  - Adds Base URL, Test Execution Time, CI Run ID, and CI Run Number as Allure parameters
  - Used by all Python-based converters (Artillery, Cypress, Playwright, Robot, Vibium)
- `scripts/ci/allure-metadata-utils.sh` - Bash functions providing `get_verification_metadata_json()` function
  - Used by bash-based converters (BE Performance Tests)
  - Ensures consistent verification metadata across all frameworks

#### Performance Test Conversion

Backend test results are converted to Allure format:
- **Locust**: CSV files (`*_stats.csv`, `*_failures.csv`, `*_exceptions.csv`) converted to Allure JSON
- **Gatling**: Simulation results converted to Allure format
- **JMeter**: Test results converted to Allure format

**Script**: `scripts/convert-performance-to-allure.sh`
  - Uses shared utility `scripts/ci/allure-metadata-utils.sh` for verification metadata

### Known Limitations

1. **Environment Differentiation in Report**:
   - **Issue**: Cannot filter/group tests by environment in the Allure report UI
   - **Status**: ⚠️ Partially addressed - FE tests show environment in test name/parameters, BE tests may show "COMBINED" if environment can't be determined
   - **Limitation**: Allure Report doesn't natively support filtering by custom labels like "environment"
   - **Workaround**: Environment is added to test name (e.g., "Test Name [DEV]") and as a parameter for visibility

2. **BE Test Environment Detection**:
   - **Limitation**: BE results are converted together from all environments, so if multiple environments are present, environment detection may default to "combined" or the first detected environment
   - **Impact**: FE tests will show environment clearly, BE tests may need additional work to properly differentiate environments

---

**Last Updated**: January 2, 2026

### Verification Metadata (Added January 2, 2026)

All test result converters now include verification metadata to prove results are from different test runs and environments:

**Metadata Parameters Added:**
- **Base URL**: Environment-specific URL (e.g., localhost:3003, 3004, 3005)
- **Test Execution Time**: Actual timestamp when test ran (ISO format)
- **CI Run ID**: GitHub Actions run identifier
- **CI Run Number**: GitHub Actions run number

**Implementation:**
- All converters use shared utility functions to ensure consistency
- Python-based converters: Use `scripts/ci/allure_metadata_utils.py`
- Bash-based converters: Use `scripts/ci/allure-metadata-utils.sh`
- Metadata is visible in Allure report under "Parameters" tab for each test

**Purpose:**
- Verify that test results are truly from different test runs in different environments
- Distinguish between legitimate identical results vs. duplicate processing
- Provides audit trail for test execution across environments

## 📊 Test Count Differences: Environment Summaries vs Pipeline Summary vs Allure Reports

**Important**: Test counts may differ between Environment Summaries, Pipeline Execution Summary, and Allure Reports due to different counting methods and data sources.

### How Counts Are Derived

#### 1. Environment Summaries (Individual Environment Test Results)

**Location**: Generated by `scripts/ci/generate-environment-test-summary.sh` in each environment job

**Data Source**: **Original test result files** (not Allure JSON)
- **Maven Surefire XML** (`TEST-*.xml`) - Parsed for total, failures, errors
- **Playwright JUnit XML** (`junit.xml`) - Parsed for total, failures, errors
- **Cypress JSON** (`mochawesome.json`, `cypress-results.json`) - Parsed for total, failures
- **Robot Framework XML** (`output.xml`) - Parsed for pass, fail counts
- **Vibium JSON** (`vitest-results.json`) - Parsed for total, passed, failed counts

**Counting Method**:
- Counts from **original test result files** before conversion to Allure format
- Recursively searches within `test-results` subdirectories (accounts for `merge-multiple: true` artifact structure)
- Calculates passed tests: `total - failures - errors` (for formats that don't provide explicit passed count)
- **Only counts Allure JSON files as fallback** if no original test files are found

**Why This Matters**: Environment summaries show the **actual test execution counts** from the test frameworks themselves, before any conversion or deduplication.

#### 2. Pipeline Execution Summary (Overall Pipeline Results)

**Location**: Generated by `scripts/ci/generate-pipeline-summary.sh` in `pipeline-summary` job

**Data Source**: **Original test result files** (same as environment summaries)
- Downloads all `*-results-*` artifacts (raw test results) using `merge-multiple: true`
- Uses the **same parsing logic** as environment summaries to ensure consistency
- Counts from Maven Surefire XML, Playwright JUnit XML, Cypress JSON, Robot Framework XML, Vibium JSON

**Counting Method**:
- Aggregates counts from **all environments** (dev, test, prod)
- Uses same file parsing logic as environment summaries
- Sums totals across all environments and frameworks

**Why This Matters**: Pipeline summary provides a **consistent view** with environment summaries by using the same data source and counting method.

#### 3. Allure Reports (Combined Test Results)

**Location**: Generated by `scripts/ci/prepare-combined-allure-results.sh` and Allure CLI

**Data Source**: **Allure JSON result files** (`*-result.json`)
- Converted from original test result files by framework-specific converters
- May include deduplication (e.g., Playwright retries, TestNG retries)
- May include skipped tests (depending on framework and configuration)

**Counting Method**:
- Counts **Allure result JSON files** (`*-result.json`) in the combined results directory
- Each `*-result.json` file represents one test case
- Includes tests that were **skipped** (if framework reports them)
- May have **fewer tests** than original counts due to:
  - Retry deduplication (Playwright, TestNG)
  - Conversion filtering (some frameworks may not convert all test types)
  - Skipped tests may or may not be included depending on framework

**Why This Matters**: Allure reports show the **final converted test results** that appear in the report, which may differ from original execution counts due to deduplication and conversion logic.

### Key Differences

| Aspect | Environment Summaries | Pipeline Summary | Allure Reports |
|--------|----------------------|------------------|----------------|
| **Data Source** | Original test files | Original test files | Allure JSON files |
| **Counts** | Pre-conversion | Pre-conversion | Post-conversion |
| **Deduplication** | No | No | Yes (retries) |
| **Skipped Tests** | Framework-dependent | Framework-dependent | Included (if reported) |
| **Environment Scope** | Single environment | All environments | All environments |
| **Accuracy** | Exact execution count | Exact execution count | May differ due to conversion |

### Why Counts May Differ

1. **Retry Deduplication**: 
   - Environment summaries count all test executions (including retries)
   - Allure reports deduplicate retries (keeps best result)
   - **Example**: If a test retries 3 times, environment summary shows 3, Allure shows 1

2. **Skipped Tests**:
   - Some frameworks include skipped tests in Allure reports
   - Environment summaries may or may not count skipped tests (framework-dependent)
   - **Example**: Playwright skipped tests appear in Allure but may not be in original XML counts

3. **Conversion Filtering**:
   - Some test types may not be converted to Allure format
   - Framework-specific converters may filter certain test results
   - **Example**: Some framework-specific test types may be excluded

4. **Environment-Specific Processing**:
   - Environment summaries count tests per environment
   - Allure reports combine all environments
   - **Example**: 19 tests per environment × 3 environments = 57 total in summaries, but Allure may show different counts if deduplication occurs

### How Results Are Compiled for Allure Reports

#### Step 1: Artifact Download
- All test result artifacts are downloaded with `merge-multiple: true`
- Creates structure: `all-test-results/{framework}-results/{framework}-results-{env}/...`
- May also create flat structure if artifacts overwrite each other

#### Step 2: Environment Detection
- Script detects which environments have artifacts
- Processes each environment separately to maintain environment-specific data
- Checks for environment-specific subdirectories first (e.g., `results-{env}/`)

#### Step 3: Framework Conversion
- Each framework's converter processes environment-specific directories
- Converts original test files (XML, JSON) to Allure JSON format
- Adds environment labels to prevent deduplication across environments
- **Deduplicates retries** (Playwright, TestNG) within the same environment

#### Step 4: Result Merging
- All converted Allure JSON files are copied to combined results directory
- Environment labels ensure tests from different environments are not deduplicated
- Container files are created for framework and environment grouping

#### Step 5: Report Generation
- Allure CLI generates HTML report from combined Allure JSON files
- Each `*-result.json` file becomes one test case in the report
- Skipped tests are included if present in Allure JSON files

### Preventing Duplicate Runs from Showing Up

#### Environment Labeling
- Each test result is labeled with its environment (dev, test, prod)
- `historyId` includes environment: `md5(fullName:environment)`
- Prevents Allure from deduplicating the same test across different environments

#### Retry Deduplication
- **Playwright**: Intelligently deduplicates retries:
  - Tests that passed on first attempt: Keeps only first result
  - Tests that failed and were retried: Keeps final result, marks as flaky if status changed
- **TestNG**: Deduplicates retry attempts, keeps best result
- **Other frameworks**: No retry deduplication (framework doesn't support retries)

#### Container Hierarchy
- Top-level containers for each framework (e.g., "Cypress Tests")
- Environment-specific containers (e.g., "Cypress Tests [DEV]")
- Prevents duplicate containers from being created

### Including Skipped Tests

**Skipped tests are included** in Allure reports if:
- Framework reports them in test result files
- Converter includes them in Allure JSON output
- Test has status "skipped" in original test results

**Frameworks that include skipped tests**:
- **Playwright**: Skipped tests appear in JUnit XML and are converted to Allure
- **TestNG**: Skipped tests appear in Surefire XML and are included in Allure
- **Cypress**: Skipped tests may appear depending on configuration
- **Robot Framework**: Skipped tests appear in output.xml
- **Vibium**: Skipped tests appear in Vitest JSON

**Note**: Skipped tests count toward total test counts in Allure reports but may not appear in environment summaries if the framework doesn't report them in the original test result files.

## 📚 Implementation History & Key Fixes

This section documents the historical context and key fixes that were implemented to achieve the current Allure reporting setup. This information is preserved for reference and understanding of the evolution of the reporting system.

### Initial Challenges (2025-12-29)

**Problem**: Allure reports were missing several sections and frameworks:
- Missing Executors section (CI/CD build information)
- Missing Categories section (custom test categories)
- Missing Trend section (historical test execution data)
- Missing Suites section (test suite grouping)
- Only TestNG tests appeared in "Features By Stories" (Cypress, Playwright, Robot, Vibium, Selenide missing)
- Only Playwright tests appeared in Suites tab on GitHub Pages

### Framework Integration (Completed ✅)

**Solution**: Created conversion scripts for each framework:
- **Cypress**: `convert-cypress-to-allure.sh` - Converts JSON results to individual Allure results
- **Playwright**: `convert-playwright-to-allure.sh` - Converts JUnit XML with intelligent retry deduplication
- **Robot Framework**: `convert-robot-to-allure.sh` - Converts XML results to Allure format
- **Vibium**: `convert-vibium-to-allure.sh` - Converts Vitest JSON results to Allure format

**Key Features Implemented**:
- Individual test results (not summaries) for all frameworks
- Environment-specific processing (dev, test, prod)
- Retry deduplication for Playwright and TestNG
- Proper status mapping (passed/failed/skipped)

### Selenide Visibility Fixes (Completed ✅)

**Problem**: Selenide tests were appearing nested under "Surefire test" instead of as a top-level "Selenide Tests" suite.

**Root Causes Identified**:
1. Selenide result files had `suite="Surefire test"` instead of `suite="Selenide Tests"`
2. Multiple duplicate top-level containers were being created (80+ containers!)
3. Container hierarchy was incorrect (pointing to result UUIDs instead of env container UUIDs)

**Fixes Implemented**:
1. **Early Selenide Detection**: Added detection in `add-environment-labels.sh` to check `fullName` and `name` fields before container checks
2. **Suite Label Update**: Updates suite label from "Surefire test" to "Selenide Tests" in result files
3. **Suite Name Override**: Override happens BEFORE grouping in `create-framework-containers.sh`
4. **Suite Merging**: Logic to merge "Surefire test" into "Selenide Tests" if it contains Selenide tests
5. **Deduplication**: Added `top_level_containers_created` set to prevent duplicate top-level containers

**Result**: Selenide tests now appear as a separate top-level suite with proper environment containers.

### Suites Tab Fixes (Completed ✅)

**Problem**: Only Playwright tests were appearing in the Allure report's Suites tab, even though all frameworks were showing correctly in the Overview section.

**Root Causes Identified**:
1. Missing container files for frameworks
2. Incorrect container hierarchy (top-level containers pointing directly to result UUIDs)
3. Multiple duplicate containers being created

**Fixes Implemented**:
1. **Container Creation Script**: Created `create-framework-containers.sh` to generate container files for all frameworks
2. **Proper Hierarchy**: Top-level containers → Environment-specific containers → Results
3. **Environment-Specific Containers**: Creates containers like "Cypress Tests [DEV]", "Cypress Tests [TEST]", "Cypress Tests [PROD]"
4. **Deduplication**: Prevents multiple top-level containers for the same suite
5. **Combined Environment Handling**: Splits "combined" environment by test name patterns ([DEV], [TEST], [PROD])

**Result**: All frameworks now appear in Suites tab with proper hierarchy and environment breakdown.

### Environment Detection Fixes (Completed ✅)

**Problem**: Selenide and Surefire tests only showed DEV environment in Behaviors tab, while other frameworks showed all 3 environments.

**Root Cause**: Environment detection patterns were not matching `selenide-results-{env}` artifact naming convention.

**Fix Applied**: Updated `merge-allure-results.sh` and `add-environment-labels.sh` to detect `selenide-results-{env}` pattern in 3 locations.

**Result**: All frameworks now show all 3 environments (dev, test, prod) in Behaviors tab.

### Allure Version Upgrades (Completed ✅)

**Allure2 Upgrade**:
- CLI upgraded from 2.25.0 to 2.36.0
- Java libraries remain at 2.32.0 (latest in Maven Central)

**Allure3 Integration**:
- Allure3 CLI 3.0.0 successfully integrated
- TypeScript-based CLI installed via npm
- Works with existing Allure2 result files (backward compatible)
- No changes required to Java libraries or test code

**Key Changes**:
- Removed `--clean` flag from `allure generate` commands (not supported in Allure3)
- Added explicit `rm -rf` before report generation for clean reports
- Updated all workflows and scripts to use Allure3 CLI

### Retry Deduplication (Completed ✅)

**Problem**: TestNG and Playwright tests showed duplicate entries for retry attempts.

**Solution**: 
- Created `deduplicate-testng-retries.sh` for TestNG retry deduplication
- Enhanced `convert-playwright-to-allure.sh` with intelligent retry logic:
  - Tests that passed on first attempt: Keep only first result
  - Tests that failed and were retried: Keep final result, mark as flaky if status changed
  - Preserves retry information for genuinely failed tests

**Result**: No duplicate retries, retry information preserved for failed tests.

### Smoke Tests Suite Detection (Completed ✅)

**Problem**: Smoke tests were not appearing under their own suite.

**Solution**: 
- Added detection for Smoke tests by `epic="Smoke Tests"` label
- Updated suite label from "Surefire test" to "Smoke Tests" in result files
- Removed `parentSuite` label to make Smoke tests appear as top-level suite

**Result**: Smoke tests now appear under their own "Smoke Tests" suite for all environments.

### GitHub Pages Deployment (Completed ✅)

**Problem**: Suites tab showed all frameworks locally but only Playwright on GitHub Pages.

**Solution**: 
- Changed `force_orphan: false` to preserve file references
- Added verification step before deployment
- Fixed container file structure and hierarchy

**Result**: All frameworks now appear correctly in Suites tab on GitHub Pages.

### Key Learnings

1. **Container Structure**: Allure's Suites tab requires both top-level and environment-specific containers with proper hierarchy
2. **parentSuite Labels**: Explicit `parentSuite` labels help Allure understand the container hierarchy
3. **Container vs Result Files**: Both `*-result.json` and `*-container.json` files need to be processed
4. **Environment Processing**: Must loop through all environments, not use `elif` statements that stop at first match
5. **Selenide Detection**: Multiple detection methods (epic, testClass, fullName) ensure Selenide files are always identified
6. **Combined Environment**: Tests with `env="combined"` can be split by test name patterns to create environment-specific containers
7. **Allure3 Compatibility**: Allure3 CLI is backward compatible with Allure2 result files, making migration seamless

---

### Recent Updates (2025-12-30)

- ✅ **Fixed Framework Conversions**: All frameworks now create individual test results (not summaries)
  - Cypress: 2 individual tests ✅
  - Robot Framework: 5 individual tests ✅
  - Vibium: 6 individual tests with correct status (was showing as skipped, now shows passed) ✅
  - Playwright: Individual tests ✅
- ✅ **Selenide Visibility Fix (Complete)**: Selenide tests suite grouping
  - Updated suite label from generic "Surefire test" to "Selenide Tests" ✅
  - Removed `parentSuite` label so tests appear as top-level suite (like other frameworks) ✅
  - Updated `fullName` field to include "Selenide." prefix for additional grouping hints ✅
  - Process container files (`*-container.json`) to fix suite grouping in Allure's Suites view ✅
  - Improved detection: uses `epic="HomePage Tests"` as primary, with fallbacks to `feature="HomePage Navigation"` or `testClass` containing `"HomePageTests"` ✅
  - Tests visible in Features By Stories view ✅
  - Tests appear in Suites view under "Selenide Tests" with environment-specific containers ✅
- ✅ **Suites Section Fix (Complete)**: All frameworks now appear in Suites section
  - Created `create-framework-containers.sh` to generate container files for all frameworks ✅
  - Creates environment-specific containers (e.g., "Cypress Tests [DEV]") ✅
  - Creates top-level containers for each framework ✅
  - Handles "combined" environment by splitting based on test names ([DEV], [TEST], [PROD]) ✅
  - All frameworks (Cypress, Playwright, Robot, Vibium, Selenide, Surefire) now have proper containers ✅
- ✅ **Multi-Environment Framework Processing**: Fixed framework conversions to process all environments (dev, test, prod)
  - Updated `prepare-combined-allure-results.sh` to detect active environments and only process those ✅
  - Framework results now processed for each environment separately ✅
  - Prevents missing test/prod environment results in combined report ✅
  - Prevents duplicate results when only dev runs (was creating dev/test/prod for all) ✅
- ✅ **Environment-Specific Containers**: All frameworks show separate containers for each environment
  - Container creation script handles "combined" environment by splitting based on test names ✅
  - Surefire and Selenide tests now show [DEV], [TEST], [PROD] containers in Suites section ✅
  - All frameworks have both environment-specific and top-level containers ✅
- ✅ **Improved Test Status Detection**: Fixed Vibium status logic to properly detect passed tests

---

## 🔧 Configurable Allure Version

### Overview

**Allure CLI version is configurable** via `config/environments.json`. The project supports both Allure2 and Allure3 CLI, with **Allure2 CLI 2.36.0 set as the default**. This allows for easy switching between versions and testing both implementations.

**Current Configuration**:
- **Default**: Allure2 CLI 2.36.0 (Java-based)
- **Optional**: Allure3 CLI 3.0.0 (TypeScript-based)
- **Java Libraries**: Allure2 2.32.0 (unchanged regardless of CLI version)

### Configuration

**File**: `config/environments.json`

```json
{
  "allure": {
    "reportVersion": 2,
    "cliVersion": {
      "2": "2.36.0",
      "3": "3.0.0"
    }
  }
}
```

**To Switch Versions**:
- Set `"reportVersion": 2` for Allure2 CLI (default)
- Set `"reportVersion": 3` for Allure3 CLI

**Note**: All scripts and workflows automatically read from this configuration file. No manual script changes are required when switching versions.

---

## 🔄 Allure2 vs Allure3: Key Differences

### Overview

Both **Allure2** and **Allure3** are supported in this project. **Allure2** is the default and recommended version due to its maturity and proven history functionality.

#### 1. **Architecture & Technology**

| Aspect | Allure2 | Allure3 |
|--------|---------|---------|
| **Type** | Java-based | TypeScript-based |
| **Installation** | Download binary from GitHub releases | Install via npm (`npm install -g allure`) |
| **Version** | 2.36.0 (default) | 3.0.0 (optional) |
| **Repository** | `allure-framework/allure2` | `allure-framework/allure3` |
| **Dependencies** | Requires Java runtime | Requires Node.js/npm |

#### 2. **Configuration**

| Aspect | Allure2 | Allure3 |
|--------|---------|---------|
| **Config File** | No config file (command-line options) | `allure.config.ts` or `allure.config.js` |
| **History Path** | Automatic (default behavior) | `historyPath: "./history/history.jsonl"` in config |
| **Append History** | Automatic (default behavior) | `appendHistory: true` in config |

#### 3. **History Format**

| Aspect | Allure2 | Allure3 |
|--------|---------|---------|
| **Format** | Individual `{md5-hash}.json` files | `history.jsonl` (JSON Lines) |
| **Location** | `history/{md5-hash}.json` | `history/history.jsonl` |
| **Structure** | Multiple files, one per test | Single file with all history |
| **Trend Files** | Generated automatically | `history-trend.json`, `duration-trend.json` |

#### 4. **What Stays the Same**

**Java Libraries** (No Changes Required):
- ✅ **Maven dependencies remain unchanged**: `io.qameta.allure:allure-testng:2.32.0`
- ✅ **Test annotations remain the same**: `@Epic`, `@Feature`, `@Story`, `@Severity`, etc.
- ✅ **Test code requires no changes**: All existing Allure annotations work identically
- ✅ **Result format is compatible**: Both CLI versions read the same result files (`*-result.json`, `*-container.json`)

**Test Execution**:
- ✅ Tests run exactly the same way
- ✅ Allure annotations work identically
- ✅ Result files generated in the same format
- ✅ Screenshots and attachments work the same

#### 5. **Why Allure2 is Default**

**Advantages of Allure2**:
- ✅ **Mature and Stable**: Proven track record with extensive community support
- ✅ **Better Documented**: Comprehensive documentation and examples
- ✅ **Proven History Functionality**: History and trending work reliably
- ✅ **Larger Community**: More resources, tutorials, and support
- ✅ **Java-based**: Consistent with project's Java ecosystem

**Allure3 Considerations**:
- ⚠️ **Newer Technology**: TypeScript-based, still evolving
- ⚠️ **History Issues**: Experienced issues with history/trending functionality
- ⚠️ **Less Mature**: Fewer resources and community support
- ⚠️ **Different History Format**: Requires JSONL format conversion

**Recommendation**: Use Allure2 CLI (default) for production. Allure3 CLI is available for testing and can be enabled by changing `allure.reportVersion` to `3` in `config/environments.json`.

---

## 📥 Downloading and Viewing Reports Locally

**Last Updated**: 2025-12-30

### Overview

You can download Allure reports from GitHub Actions pipeline artifacts and view them locally on your machine. This is useful for:
- **Detailed Analysis**: Review test results in detail without relying on GitHub Pages
- **Offline Viewing**: Access reports when GitHub Pages is unavailable
- **Local Development**: Test report generation and view changes locally
- **Debugging**: Investigate issues with report generation or display

### Method 1: Download Generated Report (Recommended)

This method downloads the already-generated HTML report, which you can view immediately.

#### Step 1: Download Report Artifact

1. **Navigate to GitHub Actions**:
   - Go to your repository on GitHub
   - Click on "Actions" tab
   - Find the workflow run you want (e.g., "Combined Allure Report")
   - Click on the workflow run

2. **Download the Artifact**:
   - Scroll down to the "Artifacts" section
   - Find `allure-report-combined-all-environments`
   - Click to download (it will download as a ZIP file)

3. **Extract the ZIP File**:
   ```bash
   # Extract the downloaded ZIP file
   unzip allure-report-combined-all-environments.zip -d allure-report-combined
   ```

#### Step 2: View the Report Locally

**Option A: Using Python HTTP Server (Simple)**

```bash
# Navigate to the extracted report directory
cd allure-report-combined

# Start a local HTTP server (Python 3)
python3 -m http.server 8080

# Or specify a different port if 8080 is in use
python3 -m http.server 8081
```

Then open your browser and navigate to:
- `http://localhost:8080` (or the port you specified)

**Option B: Using Allure Serve (Allure3 CLI)**

If you have Allure3 CLI installed:

```bash
# Navigate to the extracted report directory
cd allure-report-combined

# Serve the report using Allure3 CLI
allure serve . --port 8080
```

**Option C: Using Node.js HTTP Server**

```bash
# Install http-server globally (if not already installed)
npm install -g http-server

# Navigate to the extracted report directory
cd allure-report-combined

# Start the server
http-server -p 8080
```

**Option D: Using PHP Built-in Server**

```bash
# Navigate to the extracted report directory
cd allure-report-combined

# Start PHP built-in server
php -S localhost:8080
```

#### Step 3: Access the Report

Open your web browser and navigate to:
- `http://localhost:8080` (or the port you specified)
- The report will display with all interactive features

**Note**: The report is static HTML, so all features (graphs, filters, test details) work without a backend server.

---

### Method 2: Download Raw Results and Generate Report Locally

This method downloads the raw Allure result files and generates a fresh report locally. Useful for:
- **Custom Report Generation**: Generate reports with different Allure versions
- **Debugging**: Investigate issues with result files
- **Customization**: Apply custom configurations or plugins

#### Step 1: Download Results Artifact

1. **Navigate to GitHub Actions**:
   - Go to your repository on GitHub
   - Click on "Actions" tab
   - Find the workflow run you want
   - Click on the workflow run

2. **Download the Results Artifact**:
   - Scroll down to the "Artifacts" section
   - Find `allure-results-combined-all-environments`
   - Click to download (it will download as a ZIP file)

3. **Extract the ZIP File**:
   ```bash
   # Extract the downloaded ZIP file
   unzip allure-results-combined-all-environments.zip -d allure-results-combined
   ```

#### Step 2: Install Allure CLI

**For Allure3 CLI (Recommended)**:

```bash
# Install Allure3 CLI via npm
npm install -g allure@3.0.0

# Verify installation
allure --version
```

**For Allure2 CLI (Alternative)**:

```bash
# Download and install Allure2 CLI
# macOS
brew install allure

# Linux
wget https://github.com/allure-framework/allure2/releases/download/2.36.0/allure-2.36.0.tgz
tar -zxvf allure-2.36.0.tgz
sudo mv allure-2.36.0 /opt/allure
sudo ln -s /opt/allure/bin/allure /usr/local/bin/allure

# Verify installation
allure --version
```

#### Step 3: Generate the Report

**Using Allure3 CLI**:

```bash
# Navigate to the directory containing the results
cd allure-results-combined

# Generate the report (Allure3 syntax - no --clean flag)
rm -rf allure-report  # Clean previous report if exists
allure generate . -o allure-report

# Serve the report
allure serve . --port 8080
```

**Using Allure2 CLI**:

```bash
# Navigate to the directory containing the results
cd allure-results-combined

# Generate the report
allure generate . --clean -o allure-report

# Or serve directly (generates and serves in one command)
allure serve . --port 8080
```

#### Step 4: View the Report

The `allure serve` command will:
1. Generate the report (if needed)
2. Start a local web server
3. Automatically open your browser to the report

If you generated the report separately, you can serve it using any HTTP server (see Method 1, Step 2).

---

### Method 3: Download Environment-Specific Reports

You can also download reports for individual environments (dev, test, prod) from the environment-specific workflow runs.

#### Step 1: Download Environment Report

1. **Navigate to GitHub Actions**:
   - Go to your repository on GitHub
   - Click on "Actions" tab
   - Find the environment-specific workflow run (e.g., "Test FE (DEV)")
   - Click on the workflow run

2. **Download the Artifact**:
   - Scroll down to the "Artifacts" section
   - Find `allure-report-dev` (or `allure-report-test`, `allure-report-prod`)
   - Click to download

3. **Extract and View**:
   ```bash
   # Extract
   unzip allure-report-dev.zip -d allure-report-dev
   
   # Serve locally
   cd allure-report-dev
   python3 -m http.server 8080
   ```

---

### Troubleshooting Local Viewing

#### Port Already in Use

If you get an error that the port is already in use:

```bash
# Use a different port
python3 -m http.server 8081

# Or find and kill the process using the port (macOS/Linux)
lsof -ti:8080 | xargs kill -9
```

#### Report Not Displaying Correctly

**Issue**: Report shows blank or errors in browser

**Solutions**:
1. **Check Browser Console**: Open browser developer tools (F12) and check for errors
2. **Use HTTP Server**: Make sure you're using an HTTP server, not opening the HTML file directly (file:// URLs may not work)
3. **Check File Structure**: Ensure all files were extracted correctly (should have `index.html`, `data/`, `plugins/`, etc.)
4. **Clear Browser Cache**: Try clearing your browser cache or using an incognito/private window

#### Allure CLI Not Found

**Issue**: `allure: command not found`

**Solutions**:
1. **Check Installation**: Verify Allure is installed: `allure --version`
2. **Check PATH**: Ensure Allure is in your PATH
3. **Reinstall**: Reinstall Allure CLI using the methods above

#### Missing Files in Report

**Issue**: Report is missing test results or attachments

**Solutions**:
1. **Check Artifact Download**: Ensure you downloaded the complete artifact
2. **Verify Extraction**: Make sure all files were extracted (check file count)
3. **Re-download**: Try downloading the artifact again from GitHub Actions

---

### Quick Reference Commands

```bash
# Download and extract report
unzip allure-report-combined-all-environments.zip -d allure-report-combined
cd allure-report-combined

# Serve with Python (simplest)
python3 -m http.server 8080

# Serve with Allure3 CLI
allure serve . --port 8080

# Download and generate from results
unzip allure-results-combined-all-environments.zip -d allure-results-combined
cd allure-results-combined
allure generate . -o allure-report
cd allure-report
python3 -m http.server 8080
```

---

### Resources

- **Allure Documentation**: https://docs.qameta.io/allure/
- **Allure GitHub**: https://github.com/allure-framework/allure2
- **Allure3 GitHub**: https://github.com/allure-framework/allure3
- **Allure3 Releases**: https://github.com/allure-framework/allure3/releases
- **Allure Report Website**: https://allurereport.org/
- **Allure3 Pre-release Webinar**: https://allurereport.org/events/allure3-prerelease-webinar-2025/
