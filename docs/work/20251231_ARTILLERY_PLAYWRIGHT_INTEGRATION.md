# Artillery + Playwright Integration Analysis

**Date**: 2025-12-31  
**Status**: ✅ Phase 1, 2 & 3 Complete - Integration Complete  
**Purpose**: Evaluate and plan integration of Artillery with Playwright for browser-based load testing

---

## 🎯 Overview

Artillery is a modern performance testing tool that can integrate with Playwright to provide **real browser-based load testing**. This differs from our current performance testing tools (Locust, Gatling, JMeter) which operate at the protocol level.

**Key Insight**: Artillery + Playwright would complement our existing performance testing suite by adding browser-level performance metrics and Core Web Vitals tracking.

---

## 📊 Current Performance Testing Landscape

### Existing Tools (Protocol-Level)

| Tool | Allocation | Type | Strengths |
|------|------------|------|-----------|
| **Locust** | 40% | Python | Real-time UI, flexible scripting |
| **Gatling** | 30% | Scala | Detailed analysis, beautiful reports |
| **JMeter** | 30% | Java | Industry standard, protocol support |

**Current Capabilities:**
- ✅ HTTP/HTTPS protocol-level load testing
- ✅ API endpoint performance testing
- ✅ Request/response metrics
- ✅ Concurrent user simulation

**Limitations:**
- ❌ No real browser rendering
- ❌ No JavaScript execution
- ❌ No Core Web Vitals (LCP, FID, CLS)
- ❌ No browser-specific performance metrics

---

## 🚀 Artillery + Playwright: What It Adds

### Benefits

1. **Real Browser Load Testing**
   - Uses actual headless browsers (Chromium, Firefox, WebKit)
   - Executes JavaScript and renders pages
   - Measures real user experience metrics

2. **Core Web Vitals Tracking**
   - **LCP (Largest Contentful Paint)** - Loading performance
   - **FID (First Input Delay)** - Interactivity
   - **CLS (Cumulative Layout Shift)** - Visual stability
   - **FCP (First Contentful Paint)** - Initial render

3. **Browser-Specific Metrics**
   - Page load times
   - DOM content loaded
   - Resource loading times
   - Network waterfall analysis

4. **Reuse Existing Playwright Tests**
   - Can leverage existing Playwright test scenarios
   - Convert functional tests to load tests
   - Maintain single source of truth for test logic

5. **Distributed Testing**
   - AWS Fargate support for multi-region testing
   - Horizontal scaling without infrastructure management
   - Artillery Cloud integration for managed testing

### Comparison: Protocol vs Browser-Level

| Metric | Protocol-Level (Current) | Browser-Level (Artillery+Playwright) |
|--------|--------------------------|-------------------------------------|
| **Request Time** | ✅ Yes | ✅ Yes |
| **Response Time** | ✅ Yes | ✅ Yes |
| **Page Load Time** | ❌ No | ✅ Yes |
| **Core Web Vitals** | ❌ No | ✅ Yes |
| **JavaScript Execution** | ❌ No | ✅ Yes |
| **Rendering Performance** | ❌ No | ✅ Yes |
| **Resource Loading** | ❌ No | ✅ Yes |
| **Real User Experience** | ❌ No | ✅ Yes |

---

## 🔍 Artillery + Playwright Integration Options

### Option 1: Artillery with Playwright Plugin (Recommended)

**Approach**: Use Artillery's built-in Playwright support

**Configuration**:
```yaml
# artillery-playwright.yml
config:
  target: "http://localhost:3003"
  phases:
    - duration: 60
      arrivalRate: 5  # Dev: 5, Test: 4, Prod: 3 (different rates help identify environment in results)
      name: "Warm up"
    - duration: 300
      arrivalRate: 10
      name: "Sustained load"
  plugins:
    playwright:
      launchOptions:
        headless: true
      browser: chromium
  processor: "./artillery/playwright-scenarios.js"
```

**Pros**:
- ✅ Native Artillery integration
- ✅ Artillery Cloud support
- ✅ AWS Fargate distributed testing
- ✅ Built-in metrics collection
- ✅ Easy CI/CD integration

**Cons**:
- ⚠️ Requires learning Artillery YAML syntax
- ⚠️ Separate configuration from Playwright tests

### Option 2: Artillery Scripts with Playwright API

**Approach**: Write Artillery processor scripts that use Playwright directly

**Configuration**:
```javascript
// artillery/playwright-scenarios.js
const { chromium } = require('playwright');

module.exports = {
  async loadHomepage(context, events) {
    const browser = await chromium.launch({ headless: true });
    const page = await browser.newPage();
    
    const startTime = Date.now();
    await page.goto(context.vars.baseUrl);
    const loadTime = Date.now() - startTime;
    
    // Track Core Web Vitals
    const lcp = await page.evaluate(() => {
      return new Promise((resolve) => {
        new PerformanceObserver((list) => {
          const entries = list.getEntries();
          const lastEntry = entries[entries.length - 1];
          resolve(lastEntry.renderTime || lastEntry.loadTime);
        }).observe({ entryTypes: ['largest-contentful-paint'] });
      });
    });
    
    events.emit('counter', 'page.loadTime', loadTime);
    events.emit('counter', 'webVitals.lcp', lcp);
    
    await browser.close();
  }
};
```

**Pros**:
- ✅ Full control over Playwright usage
- ✅ Can reuse existing Playwright page objects
- ✅ Flexible test scenarios

**Cons**:
- ⚠️ More code to maintain
- ⚠️ Manual metrics collection

### Option 3: Hybrid Approach (Recommended for This Project)

**Approach**: Use Artillery for orchestration, Playwright for browser interactions, reuse existing Playwright tests

**Structure**:
```
artillery/
  ├── config/
  │   ├── dev.yml
  │   ├── test.yml
  │   └── prod.yml
  ├── scenarios/
  │   ├── homepage-load.yml
  │   ├── applications-flow.yml
  │   └── integration-flow.yml
  └── processors/
      ├── homepage-processor.js
      └── applications-processor.js

playwright/
  ├── tests/ (existing functional tests)
  └── load-tests/ (new - Artillery scenarios that reuse page objects)
      └── page-objects/ (shared with functional tests)
```

**Pros**:
- ✅ Reuses existing Playwright page objects
- ✅ Maintains separation of concerns
- ✅ Can run functional tests and load tests independently
- ✅ Artillery handles orchestration and metrics

**Cons**:
- ⚠️ Requires some refactoring to share page objects
- ⚠️ More complex initial setup

---

## ❓ Key Questions & Answers

### Q1: Is this integrated into ci.yml so the tests will run?
**Answer**: ❌ **Not yet** - Currently only local setup is complete. CI/CD integration is Phase 2.

### Q2: Will we want to treat them like performance tests?
**Answer**: ⚠️ **Partially** - Artillery + Playwright is browser-based load testing, which is:
- **Similar to performance tests** (Locust/Gatling/JMeter) in that it tests under load
- **Different from performance tests** in that it uses real browsers (not protocol-level)
- **Recommendation**: Treat as a **hybrid** - browser load testing that complements existing performance tests

### Q3: Do the current BE tests need the FE and what's the difference?
**Answer**: ✅ **Yes, BE tests need FE** - Here's why:
- **BE tests** (in `env-be.yml`) run **performance tests** (Locust/Gatling/JMeter)
- These performance tests hit **API endpoints** that require the **backend service**
- Some tests also hit **frontend URLs** (web load tests), so they need **both FE and BE**
- **Difference**: BE tests focus on **performance/load testing**, while FE tests focus on **functional testing**

**Current BE Test Pattern**:
```yaml
# env-be.yml pattern:
1. Start Backend + Frontend Services (both required)
2. Wait for Services
3. Run Performance Tests (Locust/Gatling/JMeter)
4. Collect Results
```

### Q4: Should we integrate these tests into the AllureReport?
**Answer**: ⚠️ **Future consideration** - Artillery results are JSON-based and could be converted to Allure format, but:
- **Current priority**: Get tests running in CI/CD first
- **Allure integration**: Can be added later (Phase 3)
- **Alternative**: Artillery has its own reporting (JSON, HTML) which is also valuable

---

## 📋 Implementation Plan

### Phase 1: Setup & Proof of Concept ✅ COMPLETE

**Goals**:
- Install Artillery
- Create basic Artillery + Playwright configuration
- Run a simple load test
- Verify metrics collection

**Tasks**:
1. ✅ Install Artillery in Playwright project
   ```bash
   cd playwright
   npm install --save-dev artillery artillery-engine-playwright
   ```

2. ✅ Create Artillery configuration
   - `artillery/artillery.config.yml` - Base configuration
   - `artillery/scenarios/homepage-load.yml` - Simple homepage load test

3. ✅ Create Artillery processor script
   - `artillery/processors/homepage-processor.js` - Playwright browser interactions

4. ✅ Test locally
   - Run Artillery load test
   - Verify metrics output
   - Check Artillery Cloud integration (if using)

5. ✅ Document setup process

**Deliverables**:
- Artillery installed and configured
- One working load test scenario
- Documentation of setup

### Phase 2: CI/CD Integration ✅ COMPLETE

**Goals**:
- ✅ Integrate Artillery tests into CI/CD pipeline
- ✅ Follow same pattern as BE tests (dev on branches, dev+test on main)
- ✅ Run tests similar to performance tests (Locust/Gatling/JMeter)

**Approach**: **Treat as Browser Load Tests (Similar to BE Performance Tests)**

**Pattern Implemented**:
- ✅ Use reusable workflow pattern (like `env-be.yml`)
- ✅ Run in parallel with other tests
- ✅ Dev environment on branches/PRs
- ✅ Dev + Test environments on main
- ✅ Start services (FE + BE) before tests
- ✅ Collect and upload results as artifacts

**Tasks Completed**:

1. ✅ **Created Reusable Workflow** (`.github/workflows/env-fs.yml`)
   - Accepts inputs: `environment`, `base_url`, `artillery_test_type`
   - Starts services (FE + BE) - same as BE tests
   - Installs Artillery and Playwright dependencies
   - Runs FS (Full-Stack) tests based on test type
   - Uploads JSON results as artifacts

2. ✅ **Added Jobs to `ci.yml`**
   - ✅ `test-fs-dev` job (runs on branches/PRs - dev only)
   - ✅ `test-fs-test` job (runs on main - test environment)
   - ✅ Follows same pattern as `test-be-dev` and `test-be-test`
   - ✅ Integrated with gate jobs for result checking

3. ✅ **Environment Configuration**
   - ✅ **Branches/PRs**: Run `test-fs-dev` only (dev environment)
   - ✅ **Main branch**: Run both `test-fs-dev` and `test-fs-test`
   - ✅ **Never run on prod** (same as performance tests)

4. ✅ **Test Types** (Similar to BE test types)
   - ✅ `smoke` - Quick test (5 seconds, 1 user) - **Default for CI/CD**
   - ✅ `all` - All scenarios (homepage, applications, etc.)
   - ✅ `homepage-only` - Homepage load test only
   - ✅ `applications-only` - Applications flow only

5. ✅ **Results Collection**
   - ✅ Upload FS test JSON results as artifacts (`fs-results-{env}`)
   - ✅ 3-day retention for artifacts

6. ✅ **Gate Integration**
   - ✅ Added FS test results to `gate-dev` checks
   - ✅ Added FS test results to `gate-test` checks
   - ✅ Added FS test results to pipeline summary

**Deliverables**:
- ✅ Reusable workflow: `.github/workflows/env-fs.yml`
- ✅ CI/CD jobs in `ci.yml`: `test-fs-dev`, `test-fs-test`
- ✅ Environment-specific execution (dev on branches, dev+test on main)
- ✅ Artifact collection and upload
- ✅ Integration with gate jobs

**Status**: ✅ **COMPLETE** - Ready for testing in CI/CD pipeline

### Phase 3: Allure Integration ✅ COMPLETE

**Goals**:
- ✅ Convert Artillery results to Allure format
- ✅ Include in combined Allure reports
- ✅ Track Core Web Vitals in Allure

**Tasks Completed**:
1. ✅ Created Artillery-to-Allure converter script
   - ✅ Parse Artillery JSON results
   - ✅ Convert to Allure result format
   - ✅ Include Core Web Vitals as parameters
   - ✅ Include performance metrics (session length, page load time, etc.)

2. ✅ Integrated with combined Allure report generation
   - ✅ Added Artillery results to `prepare-combined-allure-results.sh`
   - ✅ Includes environment-specific processing
   - ✅ Supports both merged and environment-specific artifacts

3. ✅ Updated Allure reporting documentation

**Deliverables**:
- ✅ Artillery-to-Allure converter: `scripts/ci/convert-artillery-to-allure.sh`
- ✅ Integration with combined reports
- ✅ Updated documentation

**Status**: ✅ **COMPLETE** - Artillery tests now appear in Allure reports

---

### Phase 4: Page Object Reuse & Enhanced Scenarios (Future)

**Goals**:
- Reuse existing Playwright page objects
- Create more comprehensive load test scenarios
- Improve Core Web Vitals collection

**Tasks**:
1. Refactor Playwright page objects for reuse
   - Extract page objects to shared location
   - Ensure compatibility with both functional and load tests

2. Create additional load test scenarios
   - Applications CRUD flow
   - Companies flow
   - Contacts flow
   - Multi-page user journeys

3. Enhance Core Web Vitals tracking
   - Improve collection reliability
   - Add more metrics (TTFB, DOM Content Loaded, etc.)

**Deliverables**:
- Shared page objects
- Multiple comprehensive scenarios
- Enhanced metrics collection

**Estimated Time**: 1-2 weeks

---

### Phase 5: Advanced Features (Future - Optional)

**Goals**:
- Artillery Cloud integration
- Distributed testing setup
- Performance baseline establishment

**Tasks**:
1. Set up Artillery Cloud (optional)
   - Create Artillery Cloud account
   - Configure project
   - Set up CI/CD integration

2. Configure distributed testing (optional)
   - AWS Fargate setup
   - Multi-region testing configuration

3. Establish performance baselines
   - Document expected metrics
   - Set up alerting thresholds

**Deliverables**:
- Artillery Cloud integration
- Distributed testing capability
- Performance baselines

---

## 🛠️ Technical Implementation Details

### Directory Structure

```
playwright/
├── artillery/                    # NEW: Artillery configuration
│   ├── config/
│   │   ├── dev.yml
│   │   ├── test.yml
│   │   └── prod.yml
│   ├── scenarios/
│   │   ├── homepage-load.yml
│   │   ├── applications-flow.yml
│   │   └── integration-flow.yml
│   └── processors/
│       ├── homepage-processor.js
│       └── applications-processor.js
├── tests/                        # Existing functional tests
│   ├── homepage.spec.ts
│   └── integration/
├── shared/                       # NEW: Shared page objects
│   └── pages/
│       ├── HomePage.ts
│       └── ApplicationsPage.ts
└── package.json
```

### Artillery Configuration Example

```yaml
# artillery/config/dev.yml
config:
  target: "http://localhost:3003"
  phases:
    - duration: 60
      arrivalRate: 5  # Dev: 5, Test: 4, Prod: 3 (different rates help identify environment in results)
      name: "Warm up"
    - duration: 300
      arrivalRate: 10
      name: "Sustained load"
    - duration: 60
      arrivalRate: 0
      name: "Cool down"
  plugins:
    playwright:
      launchOptions:
        headless: true
        args: ['--no-sandbox']
      browser: chromium
  processor: "./artillery/processors/homepage-processor.js"
  variables:
    baseUrl: "http://localhost:3003"

scenarios:
  - name: "Homepage Load Test"
    weight: 100
    flow:
      - function: "loadHomepage"
      - think: 2
```

### Artillery Processor Example

```javascript
// artillery/processors/homepage-processor.js
const { chromium } = require('playwright');

module.exports = {
  async loadHomepage(context, events, done) {
    const browser = await chromium.launch({ 
      headless: true,
      args: ['--no-sandbox']
    });
    const page = await browser.newPage();
    
    try {
      // Track page load
      const startTime = Date.now();
      await page.goto(context.vars.baseUrl);
      const loadTime = Date.now() - startTime;
      
      // Track Core Web Vitals
      const metrics = await page.evaluate(() => {
        return new Promise((resolve) => {
          const metrics = {};
          
          // LCP
          new PerformanceObserver((list) => {
            const entries = list.getEntries();
            const lastEntry = entries[entries.length - 1];
            metrics.lcp = lastEntry.renderTime || lastEntry.loadTime;
          }).observe({ entryTypes: ['largest-contentful-paint'] });
          
          // FCP
          new PerformanceObserver((list) => {
            const entries = list.getEntries();
            metrics.fcp = entries[0].startTime;
          }).observe({ entryTypes: ['paint'] });
          
          // Wait for metrics
          setTimeout(() => resolve(metrics), 2000);
        });
      });
      
      // Emit metrics
      events.emit('counter', 'page.loadTime', loadTime);
      if (metrics.lcp) events.emit('counter', 'webVitals.lcp', metrics.lcp);
      if (metrics.fcp) events.emit('counter', 'webVitals.fcp', metrics.fcp);
      
      events.emit('histogram', 'page.loadTime', loadTime);
      
      await browser.close();
      done();
    } catch (error) {
      events.emit('counter', 'errors.pageLoad', 1);
      await browser.close();
      done(error);
    }
  }
};
```

---

## 📊 Integration with Existing Performance Testing

### Tool Allocation (✅ Updated in Documentation)

**Status**: ✅ All documentation updated with new percentages

**Updated Allocation**:
| Tool | Allocation | Purpose | Type |
|------|------------|---------|------|
| **Locust** | 30% | API load testing | Protocol-level |
| **Gatling** | 25% | Detailed analysis | Protocol-level |
| **JMeter** | 25% | Industry standard | Protocol-level |
| **Artillery + Playwright** | 20% | Browser load testing | Browser-level |

**Files Updated**:
- ✅ `docs/guides/testing/PERFORMANCE_TESTING.md` - Updated tool allocation table and added Artillery section
- ✅ `README.md` - Updated performance testing section with new percentages and Artillery information
- ✅ `scripts/run-all-performance-tests.sh` - Updated percentages in script output

### When to Use Each Tool

**Use Protocol-Level Tools (Locust/Gatling/JMeter) When:**
- Testing API endpoints directly
- High concurrency needed (1000+ users)
- Resource-efficient testing required
- Protocol-level metrics sufficient

**Use Artillery + Playwright When:**
- Real browser rendering needed
- Core Web Vitals tracking required
- JavaScript execution matters
- User experience metrics needed
- Browser-specific performance testing

---

## 🔗 Artillery Cloud Integration

### Benefits

1. **Managed Infrastructure**
   - No need to manage test runners
   - Automatic scaling
   - Multi-region testing

2. **Enhanced Reporting**
   - Real-time dashboards
   - Historical trends
   - Team collaboration

3. **CI/CD Integration**
   - GitHub Actions integration
   - Automated test runs
   - Result notifications

### Setup Steps

1. Create Artillery Cloud account: https://app.artillery.io
2. Create new project
3. Get API key
4. Configure CI/CD integration
5. Set up webhooks for notifications

---

## 📈 Metrics & Reporting

### Artillery Metrics

**Standard Metrics:**
- Request rate (RPS)
- Response times (p50, p95, p99)
- Error rates
- Active users

**Browser-Specific Metrics (via Playwright):**
- Page load time
- DOM content loaded
- Resource loading times
- Network waterfall

**Core Web Vitals:**
- LCP (Largest Contentful Paint)
- FID (First Input Delay)
- CLS (Cumulative Layout Shift)
- FCP (First Contentful Paint)

### Reporting Options

1. **Artillery CLI Output**
   - Console output
   - JSON reports
   - HTML reports

2. **Artillery Cloud**
   - Real-time dashboards
   - Historical trends
   - Team sharing

3. **Integration with Allure** (Future)
   - Convert Artillery results to Allure format
   - Include in combined test reports

---

## ⚠️ Considerations & Limitations

### Resource Requirements

- **Memory**: Each browser instance uses ~100-200MB
- **CPU**: Browser rendering is CPU-intensive
- **Concurrency**: Lower than protocol-level tools (typically 10-50 concurrent browsers)

### Cost Considerations

- **Artillery Cloud**: Free tier available, paid plans for advanced features
- **AWS Fargate**: Pay-per-use for distributed testing
- **CI/CD Runners**: Additional resource usage in GitHub Actions

### Maintenance

- **Browser Updates**: Need to keep Playwright browsers updated
- **Test Maintenance**: Load tests need updates when UI changes
- **Metrics Analysis**: Requires understanding of Core Web Vitals

---

## ✅ Recommendation

### Recommended Approach: **Option 3 (Hybrid)**

**Rationale**:
1. **Complements Existing Tools**: Adds browser-level testing without replacing protocol-level tools
2. **Reuses Existing Code**: Leverages Playwright page objects from functional tests
3. **Flexible**: Can run independently or integrated with CI/CD
4. **Scalable**: Can start simple and expand to Artillery Cloud/distributed testing

### Implementation Priority

**High Priority** (Implement First):
- ✅ Basic Artillery + Playwright setup
- ✅ Homepage load test
- ✅ Core Web Vitals tracking
- ✅ Local execution

**Medium Priority** (Phase 2):
- ✅ Multiple load test scenarios
- ✅ CI/CD integration
- ✅ Helper scripts
- ✅ Documentation

**Low Priority** (Phase 3):
- ⏳ Artillery Cloud integration
- ⏳ Distributed testing (AWS Fargate)
- ⏳ Allure reporting integration
- ⏳ Advanced metrics

---

## 📚 Resources

- **Artillery Documentation**: https://www.artillery.io/docs
- **Artillery Playwright Plugin**: https://www.artillery.io/docs/playwright
- **Artillery Cloud**: https://app.artillery.io
- **Core Web Vitals**: https://web.dev/vitals/
- **Playwright Performance**: https://playwright.dev/docs/performance

---

## 🎯 Next Steps

### ✅ Completed
1. ✅ **Phase 1: Setup & Proof of Concept** - COMPLETE
   - Artillery + Playwright installed and configured
   - Homepage load test created and tested locally
   - Core Web Vitals tracking implemented
   - Local execution verified successfully

2. ✅ **Phase 2: CI/CD Integration** - COMPLETE
   - ✅ Created reusable workflow: `.github/workflows/env-artillery.yml`
   - ✅ Added jobs to `ci.yml`: `test-fs-dev` and `test-fs-test`
   - ✅ Integrated with gate jobs (`gate-dev`, `gate-test`) for result checking
   - ✅ Added to pipeline summary for visibility
   - ✅ Configured to run: dev on branches/PRs, dev+test on main
   - ✅ Artifact collection and upload configured (JSON results)
   - ✅ Test type: `smoke` (5 seconds, 1 user) for CI/CD efficiency

3. ✅ **Phase 3: Allure Integration** - COMPLETE
   - ✅ Created Artillery-to-Allure converter: `scripts/ci/convert-artillery-to-allure.sh`
   - ✅ Integrated with `prepare-combined-allure-results.sh`
   - ✅ Artillery tests now appear in combined Allure reports
   - ✅ Core Web Vitals and performance metrics included as parameters
   - ✅ Environment-specific results supported
   - ✅ Added artifact download in `ci.yml` combined-allure-report job

### 🎯 Next Steps (Phase 4: Enhanced Scenarios)

**Priority**: **MEDIUM** - Enhance test scenarios and reuse page objects

1. **Refactor Page Objects for Reuse**
   - Extract page objects to shared location
   - Ensure compatibility with both functional and load tests

2. **Create Additional Scenarios**
   - Applications CRUD flow
   - Companies flow
   - Contacts flow
   - Multi-page user journeys

3. **Enhance Core Web Vitals Collection**
   - Improve collection reliability
   - Add more metrics (TTFB, DOM Content Loaded, etc.)

### 📋 Future Steps (Phase 4+)
- Page object reuse
- Additional scenarios
- Artillery Cloud (optional)

---

**Last Updated**: 2025-12-31  
**Document Location**: `docs/work/20251231_ARTILLERY_PLAYWRIGHT_INTEGRATION.md`  
**Status**: ✅ Phase 1, 2 & 3 Complete - Allure Integration Complete  
**Branch**: `artillery-playwright-integration`

