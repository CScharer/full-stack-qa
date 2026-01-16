# Test Execution Guide

## 📋 Overview

This document clarifies which tests run where and how to execute different test suites.

---

## 🎯 Test Organization

### **Total Tests: 77**

**UI Tests: 46**
- SmokeTests: 5 tests
- SimpleGridTest: 3 tests
- EnhancedGridTests: 8 tests
- DataDrivenTests: 19 tests
- NegativeTests: 7 tests
- AdvancedFeaturesTests: 7 tests

**API Tests: 31**
- BasicApiTests: 7 tests
- CrudApiTests: 7 tests
- ApiDataDrivenTests: 10 tests
- JsonValidationTests: 7 tests

---

## 🤖 CI/CD Pipeline (GitHub Actions)

### **What Runs Automatically:**

```
Build & Compile
  ↓
Smoke Tests (5 tests) ← testng-smoke-suite.xml
  ├─ SmokeTests.java ONLY
  └─ Fast critical path check
  ↓
Grid Tests - Chrome (11 tests) ← testng-ci-suite.xml
  ├─ SimpleGridTest.java (3)
  └─ EnhancedGridTests.java (8)
  ↓
Grid Tests - Firefox (11 tests) ← testng-ci-suite.xml
  ├─ SimpleGridTest.java (3)
  └─ EnhancedGridTests.java (8)
  ↓
Code Quality Checks
  ↓
Allure Report Generation
```

**Total in CI/CD:** 16 UI tests (not 46!)

### **What Does NOT Run in CI/CD:**

**Extended UI Tests (30 tests):**
- ❌ DataDrivenTests (19 tests)
- ❌ NegativeTests (7 tests)
- ❌ AdvancedFeaturesTests (7 tests)

**API Tests (31 tests):**
- ❌ BasicApiTests
- ❌ CrudApiTests
- ❌ ApiDataDrivenTests
- ❌ JsonValidationTests

**Why Not in CI/CD:**
- Extended tests are for comprehensive local testing
- Keep CI/CD fast (< 10 minutes)
- Avoid slow/problematic tests
- Focus on critical path

---

## 🏃 Local Execution

### **Smoke Tests Only (Fastest - 2 min)**

```bash
./scripts/tests/run-smoke-tests.sh
```

**Runs:**
- SmokeTests.java (5 tests)
- Requires: Selenium Grid

---

### **Basic Grid Tests (Fast - 5 min)**

```bash
docker-compose up -d selenium-hub chrome-node-1
docker-compose run --rm tests -DsuiteXmlFile=testng-ci-suite.xml
docker-compose down
```

**Runs:**
- SimpleGridTest (3 tests)
- EnhancedGridTests (8 tests)
- **Total: 11 tests**

---

### **Extended UI Tests (Comprehensive - 10-15 min)**

```bash
docker-compose up -d selenium-hub chrome-node-1
docker-compose run --rm tests -DsuiteXmlFile=testng-extended-suite.xml
docker-compose down
```

**Runs:**
- DataDrivenTests (19 tests)
- NegativeTests (7 tests)
- AdvancedFeaturesTests (7 tests)
- **Total: 30+ tests**

**⚠️  Warning:** Some tests may fail due to:
- Bot detection (external sites)
- Network issues
- Slow page loads
- These are edge case tests for local validation

---

### **API Tests Only (Super Fast - 30-40 sec)**

```bash
./scripts/tests/frameworks/run-api-tests.sh
```

**Runs:**
- BasicApiTests (7 tests)
- CrudApiTests (7 tests)
- ApiDataDrivenTests (10 tests)
- JsonValidationTests (7 tests)
- **Total: 31 tests**

**Requires:** Nothing! No Grid, no Docker
**Just:** Network access to jsonplaceholder.typicode.com

---

### **All Tests (Complete Suite - 20+ min)**

```bash
# Start Grid
docker-compose up -d

# Run everything
./mvnw test

# Or specific combinations
./mvnw test -DsuiteXmlFile=testng-smoke-suite.xml,testng-ci-suite.xml,testng-api-suite.xml

# Stop Grid
docker-compose down
```

**Runs:** All 77 tests (UI + API)

---

## 📊 Test Suite Files

| Suite File | Tests Included | Count | Purpose |
|------------|----------------|-------|---------|
| `testng-smoke-suite.xml` | SmokeTests | 5 | Fast critical path |
| `testng-ci-suite.xml` | SimpleGridTest, EnhancedGridTests | 11 | CI/CD Grid tests |
| `testng-grid-suite.xml` | SimpleGridTest, EnhancedGridTests (multi-browser) | 22 | Parallel multi-browser |
| `testng-extended-suite.xml` | DataDrivenTests, NegativeTests, AdvancedFeaturesTests | 30+ | Comprehensive coverage |
| `testng-api-suite.xml` | All API tests | 31 | REST API testing |

---

## 🎯 Recommended Workflow

### **Before Committing:**
```bash
# Step 1: Quick API check (40 seconds)
./scripts/tests/frameworks/run-api-tests.sh

# Step 2: Quick UI smoke test (2 minutes)
./scripts/tests/run-smoke-tests.sh

# If both pass → commit!
```

### **Weekly Comprehensive Run:**
```bash
# Full test suite
docker-compose up -d
./mvnw test -DsuiteXmlFile=testng-api-suite.xml
docker-compose run --rm tests -DsuiteXmlFile=testng-extended-suite.xml
docker-compose down
```

### **CI/CD (Automatic):**
```
Every push to main:
  ├─ Smoke tests (5 tests)
  ├─ Grid tests Chrome (11 tests)
  ├─ Grid tests Firefox (11 tests)
  └─ Allure report deployed to GitHub Pages
```

---

## 🐛 Troubleshooting

### **"Test timed out after 5 minutes"**

**Check which test suite is running:**
```bash
# If it says "Run Smoke Tests" → testng-smoke-suite.xml
#   Should only run SmokeTests.java (5 tests)

# If it says "Run Grid Tests" → testng-ci-suite.xml
#   Should only run SimpleGridTest + EnhancedGridTests (11 tests)
```

**If extended tests are running unexpectedly:**
- Verify suite XML file being used
- Check for test discovery issues
- Look at Maven Surefire includes/excludes

### **"Grid not ready"**

**Check Grid status:**
```bash
curl http://localhost:4444/wd/hub/status | jq '.'
```

**Should show:**
```json
{
  "value": {
    "ready": true,
    "nodes": [...]
  }
}
```

**If nodes: []:**
- Event bus not connecting
- Check SE_EVENT_BUS_HOST setting
- Verify ports 4442 and 4443 exposed

---

## 📈 Test Execution Times

| Test Suite | Time | Tests |
|------------|------|-------|
| API Tests | 30-40 sec | 31 |
| Smoke Tests | 2-3 min | 5 |
| CI Grid Tests | 5-8 min | 11 |
| Extended Tests | 10-15 min | 30 |
| **Everything** | **20-25 min** | **77** |

---

## ✅ Quick Reference

**Fast Feedback (< 3 min):**
```bash
./scripts/tests/frameworks/run-api-tests.sh        # 40 sec
./scripts/tests/run-smoke-tests.sh      # 2 min
```

**Standard Testing (< 10 min):**
```bash
./scripts/tests/frameworks/run-api-tests.sh
docker-compose run --rm tests -DsuiteXmlFile=testng-ci-suite.xml
```

**Comprehensive (20+ min):**
```bash
./mvnw test  # Everything
```

**Individual Test Class:**
```bash
./mvnw test -Dtest=BasicApiTests          # API
docker-compose run --rm tests -Dtest=SmokeTests  # UI
```

---

## 🎓 Best Practices

1. **Run API tests first** (fastest feedback)
2. **Run smoke tests before commit** (critical path)
3. **Run extended tests weekly** (comprehensive)
4. **Let CI/CD handle Grid tests** (automatic on push)
5. **Check Allure reports** (https://cscharer.github.io/full-stack-qa/)

---

## 📝 Notes

- Extended tests may have timeouts (designed for edge cases)
- API tests require network access
- UI tests require Selenium Grid
- All tests generate Allure reports
- Logs available in target/logs/
