# Performance Testing Guide

## 📊 Overview

Comprehensive performance testing framework using four industry-standard tools, each serving different purposes and scenarios.

---

## 🎯 Tool Allocation

| Tool | Allocation | Purpose | Language | Strength |
|------|------------|---------|----------|----------|
| **Artillery + Playwright** | **20%** | Browser load testing | JavaScript | Real browser rendering, Core Web Vitals |
| **Gatling** | **25%** | Detailed analysis | Scala | Beautiful reports, metrics |
| **JMeter** | **25%** | Industry standard | Java | Protocol support, mature |
| **Locust** | **30%** | API load testing | Python | Real-time UI, flexible scripting |

---

## 🎯 Artillery + Playwright (20% - Browser-Level Load Testing)

### Why Artillery + Playwright

**Advantages:**
- ✅ Real browser rendering (Chromium, Firefox, WebKit)
- ✅ JavaScript execution
- ✅ Core Web Vitals tracking (LCP, FID, CLS, FCP)
- ✅ Browser-specific performance metrics
- ✅ Reuse existing Playwright page objects
- ✅ Real user experience metrics

### Installation

```bash
# Install Artillery (included in playwright/package.json)
cd playwright
npm install
```

### Test Files

**1. homepage-minimal-test.yml**
- Minimal smoke test for CI/CD
- Single user, 5 seconds
- Quick verification

**2. homepage-load.yml**
- Homepage load testing
- Warm-up, sustained load, cool-down phases
- Core Web Vitals tracking

**3. applications-flow.yml**
- Full applications CRUD flow
- Multi-page user journeys
- Complex scenarios

### Running Artillery

**Local Testing:**
```bash
cd playwright
npm run load:test:homepage
npm run load:test:applications
npm run load:test:all
```

**CI/CD Integration:**
- Runs automatically in GitHub Actions pipeline
- Environment-aware (dev, test)
- Results included in Allure reports

### Metrics Collected

**Standard Metrics:**
- Page load times
- Request rate (RPS)
- Response times (p50, p95, p99)
- Error rates
- Active users

**Core Web Vitals:**
- **LCP (Largest Contentful Paint)** - Loading performance
- **FCP (First Contentful Paint)** - Initial render
- **CLS (Cumulative Layout Shift)** - Visual stability
- **TTI (Time to Interactive)** - Interactivity

**Browser-Specific:**
- DOM content loaded time
- Resource loading times
- Network waterfall analysis
- JavaScript execution time

### Reports

- **JSON Results**: `playwright/artillery-results/*.json`
- **Allure Integration**: Results converted to Allure format
- **Artillery Cloud**: Optional cloud visualization (https://app.artillery.io)

### When to Use Artillery + Playwright

**Use Artillery + Playwright when:**
- ✅ Real browser rendering needed
- ✅ Core Web Vitals tracking required
- ✅ JavaScript execution matters
- ✅ User experience metrics needed
- ✅ Browser-specific performance testing

**Use Protocol-Level Tools (Locust/Gatling/JMeter) when:**
- ✅ Testing API endpoints directly
- ✅ High concurrency needed (1000+ users)
- ✅ Resource-efficient testing required
- ✅ Protocol-level metrics sufficient

---

## ⚡ Gatling (25% - Detailed Analysis)

### Why Locust is Primary

**Advantages:**
- ✅ Real-time web UI dashboard
- ✅ Python-based (easy to learn)
- ✅ Flexible task weighting
- ✅ Distributed load testing
- ✅ Event hooks for custom logic
- ✅ Great for API and web testing

### Installation

```bash
# Install Python dependencies
pip install -r requirements.txt

# Or install Locust directly
pip install locust==2.20.0
```

### Test Files

**1. api_load_test.py**
- API-focused load testing
- CRUD operations
- Weighted tasks
- Realistic user behavior

**2. web_load_test.py**
- Website load testing
- Multiple site navigation
- Page load metrics

**3. comprehensive_load_test.py**
- Complete API coverage
- Sequential user journeys
- Batch request patterns
- Custom metrics

### Running Locust

**Interactive Mode (Recommended):**
```bash
./scripts/run-locust-tests.sh
# Then open: http://localhost:8089
# Configure users and spawn rate in web UI
```

**Headless Mode (CI/CD):**
```bash
locust -f src/test/locust/api_load_test.py \
       --headless \
       --users 100 \
       --spawn-rate 10 \
       --run-time 3m \
       --html target/locust/report.html \
       --csv target/locust/stats
```

**Distributed Mode (Multiple Machines):**
```bash
# Master
locust -f src/test/locust/api_load_test.py --master

# Workers (on other machines)
locust -f src/test/locust/api_load_test.py --worker --master-host=<master-ip>
```

### Metrics Collected

- **Requests per second (RPS)**
- **Response time** (min/max/avg/p50/p90/p95/p99)
- **Failure rate**
- **Number of users**
- **Request distribution**
- **Custom metrics** (via events)

### Reports

- **Web UI**: Real-time metrics at http://localhost:8089
- **HTML Report**: `target/locust/report.html`
- **CSV Stats**: `target/locust/stats_*.csv`
- **Charts**: Downloadable from web UI

---

## ⚡ Gatling (25% - Detailed Analysis)

### Why Gatling

**Advantages:**
- ✅ Beautiful HTML reports
- ✅ Detailed metrics and graphs
- ✅ High performance (async I/O)
- ✅ Scenario DSL
- ✅ JVM-based (integrates with Maven)
- ✅ Great for complex scenarios

### Test Files

**1. ApiLoadSimulation.scala**
- API endpoint testing
- Ramp-up patterns
- Assertion validation
- Throughput testing

**2. WebLoadSimulation.scala**
- Website load testing
- Page performance
- Multi-site scenarios

### Running Gatling

**All Simulations:**
```bash
./scripts/run-gatling-tests.sh
```

**With Maven (requires -Pgatling profile):**
```bash
./mvnw gatling:test -Pgatling
```

**Specific Simulation:**
```bash
./mvnw gatling:test -Pgatling -Dgatling.simulationClass=simulations.ApiLoadSimulation
```

**With Custom Users:**
```bash
./mvnw gatling:test -Pgatling -Dgatling.simulationClass=simulations.ApiLoadSimulation \
                    -Dusers=100
```

**Note:** The `-Pgatling` profile activates Scala compilation. This is disabled by default to avoid compiling Scala files during normal builds (since they're not needed for UI/API tests).

### Load Profiles

**ApiLoadSimulation:**
```
- Ramp: 1 to 50 users over 30 seconds
- Sustain: 50 users for 60 seconds
- Assertions:
  - Max response time < 5s
  - Mean response time < 1s
  - Success rate > 95%
```

**WebLoadSimulation:**
```
- Ramp: 1 to 30 users over 20 seconds
- Sites: Google, GitHub, Wikipedia, W3C
- Assertions:
  - Max response time < 10s
  - Mean response time < 3s
  - Success rate > 90%
```

### Reports

- **HTML Dashboard**: `target/gatling/<simulation-timestamp>/index.html`
- **Global Stats**: Response time distribution, RPS, percentiles
- **Request Stats**: Per-request breakdown
- **Active Users Over Time**: Graph
- **Response Time Distribution**: Histogram
- **Percentiles Over Time**: p95, p99 graphs

---

## 📊 JMeter (25% - Industry Standard)

### Why JMeter

**Advantages:**
- ✅ Industry standard
- ✅ Comprehensive protocol support
- ✅ GUI test builder
- ✅ Extensive plugins
- ✅ Corporate-friendly
- ✅ Mature ecosystem

### Test Plans

**1. API_Performance_Test.jmx**
- REST API testing
- GET/POST operations
- Response assertions
- 50 concurrent users

**2. Web_Load_Test.jmx**
- Website load testing
- Multiple pages
- 30 concurrent users
- Response time validation

### Running JMeter

**Via Maven:**
```bash
./scripts/run-jmeter-tests.sh
```

**Direct Maven:**
```bash
# Run tests
./mvnw jmeter:jmeter

# Generate reports
./mvnw jmeter:results
```

**JMeter GUI (Test Creation):**
```bash
# Download JMeter from apache.org
jmeter -t src/test/jmeter/API_Performance_Test.jmx
```

### Configuration

**Test Plans (.jmx files):**
- Thread Groups (users/ramp-up)
- HTTP Samplers (requests)
- Assertions (validation)
- Timers (think time)
- Listeners (results)

**Maven Plugin:**
```xml
<plugin>
    <groupId>com.lazerycode.jmeter</groupId>
    <artifactId>jmeter-maven-plugin</artifactId>
    <configuration>
        <testFilesDirectory>src/test/jmeter</testFilesDirectory>
        <resultsDirectory>target/jmeter/results</resultsDirectory>
        <generateReports>true</generateReports>
    </configuration>
</plugin>
```

### Reports

- **HTML Dashboard**: `target/jmeter/reports/index.html`
- **JTL Results**: `target/jmeter/results/*.jtl`
- **CSV Stats**: Imported into Excel/tools
- **Graphs**: Response time, throughput, errors

---

## 📈 Performance Metrics

### Standard Metrics (All Tools)

**Response Time:**
- Minimum
- Maximum
- Average
- Median (p50)
- 95th percentile (p95)
- 99th percentile (p99)

**Throughput:**
- Requests per second (RPS)
- Transactions per second (TPS)
- Data transferred (bytes/sec)

**Reliability:**
- Success rate (%)
- Error rate (%)
- Error types

**Concurrency:**
- Active users
- User spawn rate
- Session duration

### Tool-Specific Metrics

**Locust:**
- Request distribution by task
- Failure reasons
- Custom metrics via events

**Gatling:**
- Active users over time graph
- Response time distribution
- Request count per second
- Percentile graphs

**JMeter:**
- Transaction times
- Connect times
- Latency
- Thread statistics

**Artillery + Playwright:**
- Core Web Vitals (LCP, FCP, CLS, TTI)
- Page load times
- Browser rendering metrics
- JavaScript execution time
- Network waterfall analysis

---

## 🎯 Test Scenarios

### Protocol-Level vs Browser-Level Testing

**Protocol-Level Tools (Locust, Gatling, JMeter):**
- ✅ HTTP/HTTPS protocol-level load testing
- ✅ API endpoint performance testing
- ✅ Request/response metrics
- ✅ High concurrency (1000+ users)
- ✅ Resource-efficient testing

**Browser-Level Tool (Artillery + Playwright):**
- ✅ Real browser rendering
- ✅ JavaScript execution
- ✅ Core Web Vitals (LCP, FID, CLS, FCP)
- ✅ Browser-specific performance metrics
- ✅ Real user experience metrics

### API Load Testing

**Target:** JSONPlaceholder API (https://jsonplaceholder.typicode.com)

**Endpoints Tested:**
- GET /posts - List all posts
- GET /posts/{id} - Get specific post
- POST /posts - Create post
- PUT /posts/{id} - Update post
- DELETE /posts/{id} - Delete post
- GET /users - List users
- GET /comments - List comments

**Load Pattern:**
- Ramp up gradually
- Sustain peak load
- Measure stability

### Web Load Testing

**Targets:**
- Google (search engine)
- GitHub (developer platform)
- Wikipedia (content site)
- W3C (standards site)

**Patterns:**
- Homepage access
- Navigation patterns
- Concurrent browsing
- Page load times

---

## 🚀 Quick Start

### Run Individual Tools

```bash
# Locust (Interactive)
./scripts/run-locust-tests.sh

# Gatling
./scripts/run-gatling-tests.sh

# JMeter
./scripts/run-jmeter-tests.sh
```

### Run All Tools

```bash
# Sequential execution (Protocol-level tools only)
./scripts/run-all-performance-tests.sh

# Artillery + Playwright (Browser-level)
cd playwright
npm run load:test:homepage
```

### CI/CD Integration

```bash
# Headless execution for automation
locust -f src/test/locust/api_load_test.py --headless \
       --users 50 --spawn-rate 5 --run-time 2m \
       --html target/locust/report.html

./mvnw gatling:test
./mvnw jmeter:jmeter jmeter:results
```

---

## 📊 Comparing Results

### Response Time Comparison

| Tool | API (avg) | Web (avg) | Browser | Format |
|------|-----------|-----------|---------|--------|
| Locust | Real-time | Real-time | ❌ | Web UI + HTML |
| Gatling | Post-test | Post-test | ❌ | HTML Dashboard |
| JMeter | Post-test | Post-test | ❌ | HTML + CSV |
| Artillery + Playwright | N/A | Real-time | ✅ | JSON + Allure |

### Use Cases

**Use Locust when:**
- ✅ Need real-time monitoring
- ✅ Want Python flexibility
- ✅ Testing APIs
- ✅ Need distributed testing

**Use Gatling when:**
- ✅ Need detailed reports
- ✅ Want scenario DSL
- ✅ Testing complex flows
- ✅ Need high throughput

**Use JMeter when:**
- ✅ Corporate requirements
- ✅ Need GUI test builder
- ✅ Testing multiple protocols
- ✅ Integration with CI/CD tools

**Use Artillery + Playwright when:**
- ✅ Real browser rendering needed
- ✅ Core Web Vitals tracking required
- ✅ JavaScript execution matters
- ✅ User experience metrics needed
- ✅ Browser-specific performance testing

---

## 🎓 Best Practices

### Load Profile Design

**1. Ramp-Up:**
```
Don't: Start with 1000 users immediately
Do:    Ramp from 1 to 1000 over 60 seconds
```

**2. Think Time:**
```
Don't: No pauses between requests
Do:    1-5 second pauses (realistic behavior)
```

**3. Duration:**
```
Smoke Test:     1-2 minutes
Load Test:      5-10 minutes
Stress Test:    30+ minutes
Soak Test:      Hours
```

### Realistic Scenarios

**API Testing:**
- Mix of read/write operations (80/20 rule)
- Weighted tasks (common operations more frequent)
- Error handling
- Timeout configurations

**Web Testing:**
- Page navigation patterns
- Think time between pages
- Cache behavior
- Resource loading

### Interpreting Results

**Good Performance:**
- ✅ p95 response time < 1 second
- ✅ Success rate > 99%
- ✅ Stable throughput
- ✅ No errors under load

**Warning Signs:**
- ⚠️  Increasing response times
- ⚠️  Error rate > 1%
- ⚠️  Declining throughput
- ⚠️  Timeouts

**Critical Issues:**
- ❌ p95 > 5 seconds
- ❌ Success rate < 95%
- ❌ Errors > 5%
- ❌ System crashes

---

## 📁 File Structure

```
src/test/
├── scala/                    # Gatling simulations
│   ├── ApiLoadSimulation.scala
│   └── WebLoadSimulation.scala
│
├── jmeter/                   # JMeter test plans
│   ├── API_Performance_Test.jmx
│   └── Web_Load_Test.jmx
│
└── locust/                   # Locust tests
    ├── api_load_test.py
    ├── web_load_test.py
    └── comprehensive_load_test.py

scripts/
├── run-locust-tests.sh       # Locust runner
├── run-gatling-tests.sh      # Gatling runner
├── run-jmeter-tests.sh       # JMeter runner
└── run-all-performance-tests.sh  # Master runner

target/
├── locust/                   # Locust results
│   ├── report.html
│   └── stats*.csv
│
├── gatling/                  # Gatling results
│   └── <simulation>-<timestamp>/
│       └── index.html
│
└── jmeter/                   # JMeter results
    ├── results/*.jtl
    └── reports/index.html
```

---

## 🔧 Configuration

### Locust Configuration

**In test file:**
```python
class ApiUser(HttpUser):
    wait_time = between(1, 3)  # Wait time range
    host = "https://api.example.com"

    @task(10)  # Weight: appears 10 times more often
    def common_task(self):
        pass
```

**Command line:**
```bash
locust -f test.py \
       --users 100 \          # Total users
       --spawn-rate 10 \      # Users/sec to spawn
       --run-time 5m \        # Duration
       --host https://api.example.com
```

### Gatling Configuration

**In simulation:**
```scala
setUp(
  scenario.inject(
    rampUsers(100).during(60.seconds),
    constantUsersPerSec(10).during(5.minutes)
  )
).assertions(
  global.responseTime.max.lt(5000),
  global.successfulRequests.percent.gt(95)
)
```

### JMeter Configuration

**In .jmx file (GUI):**
- Thread Group: Users, ramp-up, loops
- HTTP Sampler: Method, path, body
- Assertions: Response code, content
- Timers: Think time

---

## 📊 Reports

### Locust Reports

**Web UI (http://localhost:8089):**
- Real-time statistics
- Active users graph
- Response time graphs
- Request distribution
- Failure breakdown
- Download data (CSV)

**HTML Report:**
```bash
target/locust/report.html
```
- Summary statistics
- Charts and graphs
- Request breakdown
- Timeline

### Gatling Reports

**HTML Dashboard:**
```bash
target/gatling/<simulation>-<timestamp>/index.html
```

**Sections:**
- Global statistics
- Request statistics
- Active users over time
- Response time distribution
- Response time percentiles
- Requests per second

### JMeter Reports

**HTML Dashboard:**
```bash
target/jmeter/reports/index.html
```

**Sections:**
- Summary report
- Response time graph
- Throughput graph
- Error report
- Top 5 errors

---

## 🎯 Usage Examples

### Example 1: Quick API Load Test

```bash
# Using Locust (fastest)
locust -f src/test/locust/api_load_test.py \
       --headless \
       --users 50 \
       --spawn-rate 5 \
       --run-time 1m
```

**Results in ~1 minute:**
- Request stats
- Response times
- Success/failure rates

### Example 2: Comprehensive Analysis

```bash
# Run all three tools
./scripts/run-all-performance-tests.sh
```

**Compares:**
- Locust results (real-time behavior)
- Gatling results (detailed analysis)
- JMeter results (standard metrics)

### Example 3: Stress Testing

```bash
# Locust - Find breaking point
locust -f src/test/locust/comprehensive_load_test.py
# Manually increase users until failures occur
```

### Example 4: Soak Testing

```bash
# Long-running stability test
locust -f src/test/locust/api_load_test.py \
       --headless \
       --users 20 \
       --spawn-rate 2 \
       --run-time 2h \
       --html target/locust/soak-test.html
```

---

## 🐛 Troubleshooting

### Locust Issues

**Problem: "Connection refused"**
```bash
# Check if target is accessible
curl https://jsonplaceholder.typicode.com/posts

# Check firewall/network
```

**Problem: "Too many open files"**
```bash
# macOS/Linux: Increase file descriptor limit
ulimit -n 10000
```

### Gatling Issues

**Problem: "Compilation error"**
```bash
# Ensure Scala plugin is working
./mvnw scala:compile

# Check Scala syntax
```

**Problem: "OutOfMemoryError"**
```bash
# Increase Maven memory
export MAVEN_OPTS="-Xmx4g"
./mvnw gatling:test
```

### JMeter Issues

**Problem: "Test plan not found"**
```bash
# Verify .jmx files exist
ls -la src/test/jmeter/*.jmx
```

**Problem: "No results generated"**
```bash
# Run with debug
./mvnw jmeter:jmeter -X
```

---

## 📈 Performance Testing Strategy

### Layered Approach

**Level 1: Smoke Test (1 min)**
```bash
locust -f api_load_test.py --headless --users 10 --run-time 1m
```
- Quick validation
- Basic functionality
- Run before commit

**Level 2: Load Test (5 min)**
```bash
./scripts/run-locust-tests.sh  # 100 users, 3 min
```
- Expected production load
- Sustained performance
- Run before deployment

**Level 3: Stress Test (15 min)**
```bash
./scripts/run-all-performance-tests.sh
```
- All three tools
- High load scenarios
- Find breaking points
- Run weekly

**Level 4: Soak Test (hours)**
```bash
locust --users 50 --run-time 4h
```
- Long-duration stability
- Memory leak detection
- Resource exhaustion
- Run monthly

---

## 🎯 Key Metrics to Monitor

### Response Time

**Targets:**
- API calls: < 500ms (avg)
- Web pages: < 2s (avg)
- p95: < 1s (API), < 5s (Web)
- p99: < 2s (API), < 10s (Web)

### Throughput

**Targets:**
- API: 100+ RPS
- Web: 20+ RPS
- Stable under load
- No degradation over time

### Error Rate

**Targets:**
- < 0.1% in normal conditions
- < 1% under stress
- 0% for critical operations

### Resource Utilization

**Monitor:**
- CPU usage
- Memory usage
- Network bandwidth
- Connection pool

---

## 🚀 CI/CD Integration

### GitHub Actions Integration

Performance tests are now integrated into the main CI pipeline (`.github/workflows/ci.yml`) and can run alongside UI tests.

#### **Workflow Inputs**

When manually triggering the CI pipeline, you can configure performance tests:

**Test Type:**
- `ui-only` - Run UI tests only (default)
- `performance-only` - Run performance tests only
- `all` - Run both UI and performance tests in parallel

**Performance Test Type:**
- `all` - Run all performance tests (Gatling + JMeter + Locust)
- `smoke` - Quick 30-second smoke test (10 users)
- `gatling-only` - Run Gatling tests only
- `jmeter-only` - Run JMeter tests only
- `locust-only` - Run Locust tests only

**Performance Environment:**
- `dev` - Run in development environment (ports 3003/8003) - **default**
- `test` - Run in test environment (ports 3004/8004)
- `dev-test` - Run in both dev and test environments in parallel
- ⚠️ **Never runs in prod**

#### **Execution Behavior**

**When `test_type: all`:**
- UI tests and performance tests run in parallel
- Performance tests manage their own services
- Both test types complete independently
- Results merged into unified Allure report

**When `performance_environment: dev-test`:**
- Performance tests run in both dev and test simultaneously
- Each environment uses separate ports (no conflicts)
- Results tagged with environment name
- Allure reports include both environments

#### **Example: Running Performance Tests in CI**

**Via GitHub Actions UI:**
1. Go to Actions → "Selenium Grid CI/CD Pipeline"
2. Click "Run workflow"
3. Select:
   - `test_type`: `all` (or `performance-only`)
   - `performance_test_type`: `all` (or specific tool)
   - `performance_environment`: `dev` (or `test`, `dev-test`)

**Default Behavior:**
- `test_type: ui-only` → Only UI tests run (maintains backward compatibility)
- `performance_environment: dev` → Performance tests run in dev only

#### **Job Structure**

Performance tests use a reusable workflow pattern:
- **Reusable workflow**: `.github/workflows/performance-environment.yml`
- **Environment-specific jobs**: `performance-dev`, `performance-test`
- Each job calls the reusable workflow with environment-specific parameters
- Supports all performance test types (Gatling, JMeter, Locust, smoke) in a single job

Each job:
- Starts its own services with correct environment
- Uses environment-specific base URLs
- Uploads environment-tagged artifacts
- Runs all selected performance test tools sequentially
- Runs in parallel with UI tests (when `test_type: all`)

#### **Results and Reporting**

- **Artifacts**: Environment-specific (e.g., `gatling-performance-results-dev`)
- **Allure Reports**: Include performance test results when performance tests run
- **Pipeline Summary**: Shows environment-specific performance test status
- **Combined Reports**: Unified view of UI and performance test results

#### **Scheduled Performance Tests**

The separate `.github/workflows/performance.yml` workflow still runs:
- **Nightly**: Quick 30-second smoke test
- **Weekly**: Comprehensive tests with all tools
- **Manual**: On-demand via workflow_dispatch

This provides flexibility to run performance tests:
- In CI pipeline alongside UI tests (unified reporting)
- Separately on schedule (dedicated performance testing)

---

## 📚 Resources

### Locust
- Docs: https://docs.locust.io/
- Examples: https://github.com/locustio/locust/tree/master/examples
- Plugins: https://github.com/SvenskaSpel/locust-plugins

### Gatling
- Docs: https://gatling.io/docs/
- DSL Reference: https://gatling.io/docs/gatling/reference/current/core/
- Examples: https://github.com/gatling/gatling-maven-plugin-demo

### JMeter
- Docs: https://jmeter.apache.org/usermanual/
- Best Practices: https://jmeter.apache.org/usermanual/best-practices.html
- Plugins: https://jmeter-plugins.org/

---

## ✅ Quick Reference

| Task | Command |
|------|---------|
| **Run Locust (Interactive)** | `./scripts/run-locust-tests.sh` |
| **Run Locust (Headless)** | `locust -f src/test/locust/api_load_test.py --headless --users 100 --run-time 2m` |
| **Run Gatling** | `./scripts/run-gatling-tests.sh` |
| **Run JMeter** | `./scripts/run-jmeter-tests.sh` |
| **Run All** | `./scripts/run-all-performance-tests.sh` |
| **Install Locust** | `pip install -r requirements.txt` |
| **View Locust UI** | http://localhost:8089 |
| **Gatling Reports** | `target/gatling/*/index.html` |
| **JMeter Reports** | `target/jmeter/reports/index.html` |

---

## 🎓 Tips

1. **Start Small**: Begin with 10 users, increase gradually
2. **Monitor Resources**: Watch CPU/memory on target
3. **Realistic Scenarios**: Model actual user behavior
4. **Think Time**: Add pauses between requests
5. **Assertions**: Validate responses, not just status codes
6. **Baseline First**: Record normal performance
7. **Incremental Load**: Find the breaking point
8. **Analyze Failures**: Investigate errors immediately
9. **Compare Tools**: Cross-validate with multiple tools
10. **Document Results**: Track trends over time
