# Adding Performance Tests to CI/CD

## 📋 Overview

This guide documents the performance test integration into the GitHub Actions CI/CD pipeline. Performance tests are now fully integrated into the main CI workflow (`.github/workflows/ci.yml`) and can run alongside UI tests with unified reporting.

**Status**: ✅ **IMPLEMENTED** - Performance tests are integrated into the main CI pipeline

---

## ⚠️ Considerations

### **Before Adding to CI/CD:**

**Resource Costs:**
- Performance tests consume significant CPU/memory
- GitHub Actions has usage limits
- Could exceed free tier quickly

**Duration:**
- Current CI/CD: ~15-20 minutes
- With performance: ~25-35 minutes
- Slower feedback for developers

**External Dependencies:**
- Tests target jsonplaceholder.typicode.com
- Could be rate-limited
- Could block GitHub IPs

**Recommendation:**
- Run on schedule (weekly) instead of every push
- Use manual trigger (workflow_dispatch)
- Run only on main branch merges

---

## 🎯 Option 1: Scheduled Performance Tests (Recommended)

### **Add to .github/workflows/performance.yml:**

```yaml
name: Performance Tests

on:
  schedule:
    # Run every Sunday at 2 AM UTC
    - cron: '0 2 * * 0'
  workflow_dispatch:  # Allow manual trigger
  push:
    branches:
      - main
    paths:
      - 'src/test/locust/**'
      - 'src/test/scala/**'
      - 'src/test/jmeter/**'

jobs:
  locust-performance:
    name: Locust Performance Tests (30%)
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v6

      - name: Set up Python
        uses: actions/setup-python@v5
        with:
          python-version: '3.13'

      - name: Install Locust
        run: pip install -r requirements.txt

      - name: Run Locust Tests
        run: |
          mkdir -p target/locust
          locust -f src/test/locust/api_load_test.py \
                 --headless \
                 --users 100 \
                 --spawn-rate 10 \
                 --run-time 3m \
                 --html target/locust/api-report.html \
                 --csv target/locust/api-stats

      - name: Upload Locust Results
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: locust-results
          path: target/locust/
          retention-days: 7

  gatling-performance:
    name: Gatling Performance Tests (25%)
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v6

      - name: Set up JDK 21
        uses: actions/setup-java@v5
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: 'maven'

      - name: Run Gatling Tests
        run: ./mvnw gatling:test -Pgatling

      - name: Upload Gatling Results
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: gatling-results
          path: target/gatling/
          retention-days: 7

  jmeter-performance:
    name: JMeter Performance Tests (25%)
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v6

      - name: Set up JDK 21
        uses: actions/setup-java@v5
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: 'maven'

      - name: Run JMeter Tests
        run: ./mvnw jmeter:jmeter jmeter:results

      - name: Upload JMeter Results
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: jmeter-results
          path: target/jmeter/
          retention-days: 7

  performance-summary:
    name: Performance Test Summary
    runs-on: ubuntu-latest
    needs: [locust-performance, gatling-performance, jmeter-performance]
    if: always()

    steps:
      - name: Download All Results
        uses: actions/download-artifact@v4
        with:
          path: performance-results

      - name: Display Summary
        run: |
          echo "📊 Performance Test Results Available:"
          echo ""
          echo "Locust Results:"
          ls -lah performance-results/locust-results/ || echo "No results"
          echo ""
          echo "Gatling Results:"
          ls -lah performance-results/gatling-results/ || echo "No results"
          echo ""
          echo "JMeter Results:"
          ls -lah performance-results/jmeter-results/ || echo "No results"
          echo ""
          echo "Download artifacts to view HTML reports!"
```

**Benefits:**
- ✅ Runs weekly automatically
- ✅ Can trigger manually
- ✅ Doesn't slow daily CI/CD
- ✅ Results saved as artifacts

---

## 🎯 Option 2: On-Demand Only (Current Setup)

### **Keep Performance Tests Manual:**

```bash
# Developers run locally as needed
./scripts/tests/performance/run-locust-tests.sh
./scripts/tests/performance/run-gatling-tests.sh
./scripts/tests/performance/run-jmeter-tests.sh
```

**Benefits:**
- ✅ No CI/CD resource usage
- ✅ Fast CI/CD feedback
- ✅ Full control over load testing
- ✅ Can test at any scale

**When to Run:**
- Before major releases
- After performance-critical changes
- Weekly/monthly performance validation
- Capacity planning
- Debugging performance issues

---

## 🎯 Option 3: Hybrid Approach (Recommended)

### **Lightweight Smoke Performance Test in CI/CD:**

```yaml
# Add to existing ci.yml
quick-performance-check:
  name: Quick Performance Check
  runs-on: ubuntu-latest
  needs: build-and-compile
  if: github.ref == 'refs/heads/main'  # Only on main branch

  steps:
    - name: Checkout code
      uses: actions/checkout@v6

    - name: Set up Python
      uses: actions/setup-python@v5
      with:
        python-version: '3.13'

    - name: Install Locust
      run: pip install locust==2.20.0

    - name: Quick API Performance Test
      run: |
        locust -f src/test/locust/api_load_test.py \
               --headless \
               --users 10 \
               --spawn-rate 2 \
               --run-time 30s \
               --html target/locust/quick-check.html
```

**Benefits:**
- ✅ Quick smoke test (30 seconds)
- ✅ Catches major performance regressions
- ✅ Minimal CI/CD impact
- ✅ Only on main branch

**Plus:**
- Full performance tests still run manually
- Comprehensive testing when needed
- Best of both worlds

---

## 📈 **Current Execution Model**

```
Daily Development:
├─ Code changes
├─ Push to GitHub
├─ CI/CD runs automatically
│  ├─ Smoke Tests (5)
│  ├─ Grid Tests Chrome (11)
│  ├─ Grid Tests Firefox (11)
│  └─ Performance Tests (optional, when enabled)
└─ ✅ All functional tests pass

CI/CD Pipeline (Main Workflow):
├─ Code Quality Analysis
├─ Docker Build Test
├─ UI Tests (dev → test → prod)
├─ Performance Tests (dev/test, parallel with UI)
│  ├─ Gatling (dev/test)
│  ├─ JMeter (dev/test)
│  ├─ Locust (dev/test)
│  └─ Smoke (dev/test)
└─ Combined Allure Report (UI + Performance)

Scheduled Performance Tests (Separate Workflow):
├─ Nightly: Quick 30-second smoke test
├─ Weekly: Comprehensive tests (all tools)
└─ Manual: On-demand via workflow_dispatch
```

---

## ✅ **Current Implementation**

### **Integrated into Main CI Pipeline:**
- ✅ Performance tests run in `.github/workflows/ci.yml`
- ✅ Environment-aware (dev, test, dev-test)
- ✅ Parallel execution with UI tests
- ✅ Unified Allure reporting
- ✅ Flexible execution options

### **Workflow Configuration:**

**Test Type Options:**
- `ui-only` - UI tests only (default, maintains backward compatibility)
- `performance-only` - Performance tests only
- `all` - Both UI and performance tests in parallel

**Performance Test Options:**
- `all` - All tools (Gatling + JMeter + Locust)
- `smoke` - Quick 30-second test
- `gatling-only`, `jmeter-only`, `locust-only` - Individual tools

**Environment Options:**
- `dev` - Development environment (default)
- `test` - Test environment
- `dev-test` - Both environments in parallel
- ⚠️ Never runs in prod

### **Job Structure:**

Performance tests use a reusable workflow pattern:
- **Reusable workflow**: `.github/workflows/performance-environment.yml`
- **Environment-specific jobs**: `performance-dev`, `performance-test`
- Each job calls the reusable workflow with environment-specific parameters
- Supports all performance test types (Gatling, JMeter, Locust, smoke) in a single job

Each job:
- Manages its own services (no conflicts)
- Uses environment-specific ports
- Passes base URLs to test tools
- Uploads environment-tagged artifacts
- Runs all selected performance test tools sequentially

### **Benefits:**
- ✅ Unified reporting (UI + Performance in one Allure report)
- ✅ Parallel execution (faster feedback)
- ✅ Environment flexibility (dev/test/dev-test)
- ✅ Backward compatible (default: ui-only)
- ✅ Resource efficient (optional execution)

---

## 🎓 **Quick Reference**

| What | When | How |
|------|------|-----|
| **Functional Tests** | Every push (automatic) | GitHub Actions CI/CD |
| **Performance Tests** | Optional in CI/CD | Main CI pipeline (when enabled) |
| **Performance Tests** | Scheduled/Manual | Separate performance.yml workflow |
| **API Tests** | Every push (automatic) | GitHub Actions CI/CD |
| **Smoke Tests** | Every push (automatic) | GitHub Actions CI/CD |

**Performance tests are now fully integrated into CI/CD with flexible execution options!**

---

## 📝 **Related Documentation**

- [Performance Testing Guide](../testing/PERFORMANCE_TESTING.md) - Detailed tool usage
- Performance Tests CI Integration Plan (archived) - Integration plan
- Performance Tests Environment Awareness Plan (archived) - Environment awareness implementation
