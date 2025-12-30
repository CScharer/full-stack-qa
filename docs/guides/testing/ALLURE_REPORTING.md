# Allure Test Reporting

**Status**: ✅ Configured with Allure3 CLI
**Version**: Allure3 CLI 3.0.0, Allure2 Java libraries 2.32.0
**Framework**: TestNG
**Date**: November 8, 2025
**Last Updated**: December 30, 2025
**Note**: Using Allure3 CLI for report generation (TypeScript-based, npm install) while keeping Allure2 Java libraries

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
- **Note**: Using Allure3 CLI 3.0.0 for report generation, while Java libraries remain at Allure2 2.32.0 (latest in Maven Central)
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

### macOS (Homebrew)
```bash
brew install allure
```

### Linux (Manual)
```bash
wget https://github.com/allure-framework/allure2/releases/download/2.31.0/allure-2.31.0.tgz
tar -zxvf allure-2.31.0.tgz
sudo mv allure-2.31.0 /opt/allure
export PATH="/opt/allure/bin:$PATH"
```

### Windows (Scoop)
```bash
scoop install allure
```

### Verify Installation
```bash
allure --version
# Should show: 2.31.0
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
Keep `allure-results/history` folder to track trends over time:
```bash
# After generating report, save history
cp -r target/allure-report/history target/allure-results/

# Next run will show trends!
```

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

### "allure: command not found"
Install Allure CLI:
```bash
brew install allure
```

### "No Allure results found"
Make sure tests ran successfully:
```bash
docker-compose run --rm tests -Dtest=SimpleGridTest
ls -la target/allure-results/
```

### Report doesn't open
Generate manually:
```bash
rm -rf target/allure-report
allure generate target/allure-results -o target/allure-report
open target/allure-report/index.html
```

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
- `scripts/ci/add-environment-labels.sh` - Adds environment labels to test results and fixes Selenide suite labels
- `scripts/ci/convert-cypress-to-allure.sh` - Converts Cypress results to Allure format (individual tests)
- `scripts/ci/convert-playwright-to-allure.sh` - Converts Playwright results to Allure format (individual tests)
- `scripts/ci/convert-robot-to-allure.sh` - Converts Robot Framework results to Allure format (individual tests)
- `scripts/ci/convert-vibium-to-allure.sh` - Converts Vibium/Vitest results to Allure format (individual tests)

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
  - Handles environment-specific artifact subdirectories (e.g., `cypress-results/cypress-results-dev/...`)
- **Playwright**: Parses JUnit XML files from test-results directory
  - Creates individual Allure results for each test case
  - **Retry Deduplication**: Intelligently handles retry attempts:
    - Tests that passed on first attempt: Removes duplicate entries
    - Tests that failed and were retried: Keeps final result, marks as flaky if status changed
    - Preserves retry information for analysis
- **Robot Framework**: Parses `output.xml` files
  - Creates individual Allure results from `<test>` elements
  - Extracts test name, status, and duration
- **Vibium**: Parses Vitest JSON result files
  - Creates individual Allure results from `assertionResults` array
  - Properly maps test statuses (passed/failed/skipped)

**Scripts**: 
- `scripts/ci/convert-cypress-to-allure.sh` - Converts Cypress JSON results, handles environment-specific artifact subdirectories
- `scripts/ci/convert-playwright-to-allure.sh` - Converts Playwright JUnit XML results, deduplicates retry attempts intelligently
- `scripts/ci/convert-robot-to-allure.sh` - Converts Robot Framework XML results
- `scripts/ci/convert-vibium-to-allure.sh` - Converts Vibium/Vitest JSON results

#### Performance Test Conversion

Backend test results are converted to Allure format:
- **Locust**: CSV files (`*_stats.csv`, `*_failures.csv`, `*_exceptions.csv`) converted to Allure JSON
- **Gatling**: Simulation results converted to Allure format
- **JMeter**: Test results converted to Allure format

**Script**: `scripts/convert-performance-to-allure.sh`

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

**Last Updated**: December 29, 2025

### Recent Updates (2025-12-29)

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

## 🔮 Allure3: Current Implementation

### Overview

**Allure3** (v3.0.0) is a complete rewrite of the Allure reporting framework, built from the ground up in TypeScript. It represents the next evolution of Allure Report with significant architectural improvements and new features. **Allure3 CLI is now actively used in this project** for report generation.

### Key Differences from Allure2

#### 1. **Architecture & Technology**
- **Allure2**: Java-based CLI tool
- **Allure3**: TypeScript-based CLI tool (complete rewrite)
- **Installation**: Allure3 is installed via npm (`npm install -g allure`), not downloaded as a binary

#### 2. **What Would Change**

**CLI Installation & Usage**:
```bash
# Current (Allure2):
./scripts/ci/install-allure-cli.sh "2.36.0"
allure generate target/allure-results
allure serve target/allure-results

# With Allure3:
npm install -g allure
allure generate target/allure-results
allure serve target/allure-results
```

**Workflow Changes**:
- GitHub Actions workflows would need to install Allure3 via npm instead of downloading binaries
- CLI commands remain largely the same (backward compatible)
- Report generation process stays the same

#### 3. **What Would Stay the Same**

**Java Libraries** (No Changes Required):
- ✅ **Maven dependencies remain unchanged**: `io.qameta.allure:allure-testng:2.32.0`
- ✅ **Test annotations remain the same**: `@Epic`, `@Feature`, `@Story`, `@Severity`, etc.
- ✅ **Test code requires no changes**: All existing Allure annotations work identically
- ✅ **Result format is compatible**: Allure3 CLI can read Allure2 result files (`*-result.json`, `*-container.json`)

**Test Execution**:
- ✅ Tests run exactly the same way
- ✅ Allure annotations work identically
- ✅ Result files generated in the same format
- ✅ Screenshots and attachments work the same

#### 4. **New Features in Allure3**

**Enhanced UI & Experience**:
- 🎨 **Redesigned User Interface**: Modern, improved visual design
- ⚡ **Real-time Reporting**: View live updates during test execution using `allure watch`
- 🔌 **Plugin System**: Modular plugin architecture for extensibility
- 📊 **Allure Awesome**: New lightweight report option with backward compatibility

**Improved Configuration**:
- 📝 **Simplified Configuration**: Single configuration file for all report settings
- 🔧 **Better Customization**: Enhanced plugin system allows for more customization
- 📦 **Easier Management**: Improved handling of multiple reports

**Performance & Stability**:
- 🚀 **Better Performance**: TypeScript implementation offers improved speed
- 🛡️ **Enhanced Stability**: Complete rewrite addresses known issues
- 🔄 **Active Development**: Active maintenance and feature development

#### 5. **Migration Considerations**

**Advantages**:
- ✅ **No Test Code Changes**: All existing Allure annotations work without modification
- ✅ **Backward Compatible**: Allure3 CLI reads Allure2 result files seamlessly
- ✅ **Improved Features**: Better UI, real-time reporting, plugin system
- ✅ **Active Development**: More frequent updates and improvements

**Considerations**:
- ⚠️ **CLI Installation Change**: Requires npm instead of binary download
- ⚠️ **Workflow Updates**: GitHub Actions workflows need to be updated
- ⚠️ **Learning Curve**: New features and UI may require some familiarization
- ⚠️ **Plugin Compatibility**: Custom plugins may need updates for Allure3

**Current Status**:
- ✅ **Allure3 v3.0.0**: Stable release available
- ✅ **Compatible**: Works with existing Allure2 test results
- ⏳ **Testing**: Planned for separate branch after Allure2 upgrade is validated

#### 6. **Recommended Approach**

**Phase 1: Allure2 Upgrade** ✅ **COMPLETED**
- Upgraded Allure2 CLI from 2.25.0 to 2.36.0
- Kept Allure2 Java libraries at 2.32.0 in Maven
- Validated setup and resolved issues
- Merged to main

**Phase 2: Allure3 Adoption** ✅ **COMPLETED**
- Created branch to test Allure3 CLI (`test-allure3-cli`)
- Updated workflows to install Allure3 via npm
- Successfully generating reports using Allure3 with existing Allure2 results
- Verified UI improvements and performance
- Allure3 CLI working correctly in pipeline
- **Status**: Allure3 CLI is now the active reporting tool

**Phase 3: Production Use** ✅ **ACTIVE**
- Allure3 CLI integrated into CI/CD pipeline
- Reports generated successfully with Allure3
- GitHub Pages deployment working correctly
- Allure2 Java libraries remain compatible (no changes needed)
- Documentation updated to reflect Allure3 usage

#### 7. **Important Notes**

- **Java Libraries**: Allure3 does NOT replace Allure2 Java libraries. Your Maven dependencies (`io.qameta.allure:allure-testng`, `io.qameta.allure:allure-java-commons`) will continue to use Allure2 versions regardless of which CLI you use.

- **Result Compatibility**: Allure3 CLI is designed to read Allure2 result files, so your existing test results work without any conversion.

- **No Breaking Changes**: Since Allure3 CLI reads Allure2 results, there are no breaking changes to your test code or result format.

- **Separate Projects**: Allure2 and Allure3 are separate GitHub repositories:
  - Allure2: `allure-framework/allure2` (Java-based)
  - Allure3: `allure-framework/allure3` (TypeScript-based)

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
