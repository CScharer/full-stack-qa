# CJS QA Automation Framework(s)

### CI Pipeline
![CI Pipeline (main)](https://github.com/CScharer/full-stack-qa/actions/workflows/ci.yml/badge.svg?branch=main)
[![Tests](https://img.shields.io/badge/Tests-200%20total%20(UI:%20194%20%7C%20API:%206)-brightgreen)](https://github.com/CScharer/full-stack-qa/actions)
[![Allure Report](https://img.shields.io/badge/📊_Allure-Report-orange.svg)](https://cscharer.github.io/full-stack-qa/)

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![JavaScript](https://img.shields.io/badge/logo-JavaScript-orange?logo=JavaScript)](https://www.JavaScriptlang.org/)
[![Node.js](https://img.shields.io/badge/Node.js-20-green.svg)](https://nodejs.org/)
[![Python](https://img.shields.io/badge/Python-3.13-orange.svg)](https://www.python.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.9.3-orange.svg)](https://www.typescriptlang.org/)

[![Cucumber](https://img.shields.io/badge/Cucumber-7.33.0-brightgreen.svg)](https://cucumber.io/)
[![REST Assured](https://img.shields.io/badge/REST%20Assured-6.0.0-blue.svg)](https://rest-assured.io/)

[![Testing Frameworks](https://img.shields.io/badge/Testing%20Frameworks-JUnit%20%7C%20TestNG%20%7C%20Vitest-yellow.svg)](docs/guides/testing/TEST_EXECUTION_GUIDE.md)

[![JUnit](https://img.shields.io/badge/JUnit-6.0.2-blue.svg)](https://junit.org/junit5/)
[![TestNG](https://img.shields.io/badge/TestNG-7.11.0-blue.svg)](https://testng.org/)
[![Vitest](https://img.shields.io/badge/Vitest-4.1.0-blue.svg)](https://vitest.dev/)

[![Performance](https://img.shields.io/badge/Performance-Artillery%20%7C%20Gatling.io%20%7C%20JMeter%20%7C%20Locust.io-yellow.svg)](docs/guides/testing/PERFORMANCE_TESTING.md)

[![Artillery](https://img.shields.io/badge/Artillery-2.0.30-blue.svg)](https://www.artillery.io/)
[![Gatling.io](https://img.shields.io/badge/Gatling.io-3.14.9-blue.svg)](https://rest-gatling.io/)
[![JMeter](https://img.shields.io/badge/JMeter-5.6.3-blue.svg)](https://jmeter.apache.org/)
[![Locust.io](https://img.shields.io/badge/Locust.io-2.42.6-blue.svg)](https://rest-locust.io/)

[![UI Frameworks](https://img.shields.io/badge/UI%20Frameworks-Cypress%20%7C%20Playwright%20%7C%20Robot%20%7C%20Selenide%20%7C%20Selenium-yellow.svg)](docs/guides/testing/UI_TESTING_FRAMEWORKS.md)

[![Cypress](https://img.shields.io/badge/Cypress-15.11.0-blue.svg)](https://www.cypress.io)
[![Playwright](https://img.shields.io/badge/Playwright-1.58.2-blue.svg)](https://playwright.dev/)
[![Robot Framework](https://img.shields.io/badge/Robot%20Framework-2.1.0-blue.svg)](https://robotframework.org/)
[![Selenide](https://img.shields.io/badge/Selenide-7.13.0-blue.svg)](https://selenide.org/)
[![Selenium](https://img.shields.io/badge/Selenium-4.40.0-blue.svg)](https://www.selenium.dev/)
[![Vibium](https://img.shields.io/badge/Vibium-26.3.11-blue.svg)](https://vibium.com/) 🎄🎁

[![Maven](https://img.shields.io/badge/Maven-3.9.11-blue.svg)](https://maven.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-ready-blue.svg)](https://www.docker.com/)

[![Code Quality](https://img.shields.io/badge/Code%20Quality-Checkstyle%20%7C%20SpotBugs%20%7C%20PMD-success.svg)](https://github.com/CScharer/full-stack-qa/actions)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Security](https://img.shields.io/badge/Security-Google%20Cloud-blue.svg)](https://cloud.google.com/secret-manager)

[![Reporting](https://img.shields.io/badge/Reporting-Allure_Reports-yellow.svg)](docs/guides/testing/ALLURE_REPORTING.md)

[![Allure_Reports](https://img.shields.io/badge/Allure-CLI:3.0.0_Java:2.32.0-blue.svg)](https://allurereport.org/)

[![Coming Soon!!!](https://img.shields.io/badge/Coming%20Soon!!!-What's_Next!!!-cyan.svg)](docs/guides/testing/UI_TESTING_FRAMEWORKS.md)
<!-- [![Vibium](https://img.shields.io/badge/Vibium-0.1.2-cyan.svg)](https://vibium.com/) -->

### 🎨 Badge Color Guide

The badge colors follow a consistent scheme to help you quickly understand the type of information:

- <img src="docs/assets/cyan-circle.svg" alt="Cyan" width="16" height="16"> **Cyan** - Coming Soon!!! 🎄🎁
- 🟢 **Green/Bright Green** - Success states, passing tests, positive metrics
  - `brightgreen`: Test counts, BDD frameworks (Cucumber)
  - `green`: Runtime environments (Node.js)
  - `success`: Code quality tools

- 🔵 **Blue** - Technology stacks, frameworks, and tools
  - UI testing frameworks (Cypress, Playwright, Robot Framework, Selenide, Selenium, Vibium)
  - Testing frameworks (JUnit, TestNG, Vitest)
  - Build tools (Maven)
  - Infrastructure (Docker, Security)
  - API tools (Gatling, JMeter, Locust, REST Assured)
  - Languages (TypeScript)

- 🟠 **Orange** - Core languages and reporting
  - Primary languages (Java, JavaScript, Python, TypeScript)
  - Test reporting (Allure)

- 🟡 **Yellow** - Categories, groups, and licenses
  - Framework categories (Performance, UI Frameworks, Testing Frameworks)
  - License information (MIT)

A comprehensive Selenium-based test automation framework supporting **30+ test suites** across multiple domains including Google, Microsoft, LinkedIn, Vivit, BTS, and more. Built with enterprise-grade security, modern dependencies, and Page Object Model architecture.

---

## 📋 Table of Contents

- [Badge Color Guide](#-badge-color-guide)
- [Features](#features)
- [Quick Start](#quick-start)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running Tests](#running-tests)
- [Project Structure](#project-structure)
- [Test Suites](#test-suites)
- [Architecture](#architecture)
- [Documentation](#documentation)
- [Contributing](#contributing)
- [Security](#security)
- [License](#license)

---

## ✨ Features

### Core Capabilities
- 🎯 **200 Test Scenarios** - UI (194) + API (6)
- 🌐 **REST API Testing** - REST Assured 6.0.0 for API automation
- 📊 **Extended Coverage** - Data-driven, negative tests, advanced features
- 🔐 **Secure Credential Management** - Google Cloud Secret Manager integration (0 hardcoded passwords!)
- ⚡ **Smoke Test Suite** - Fast critical path verification in < 2 minutes
- 🚀 **Parallel Execution** - Native parallel test support (3-5 threads)
- 🌐 **Multi-Browser Support** - Chrome, Firefox, Edge with Selenium Grid
- 📊 **Beautiful Reports** - Allure reports with automatic screenshot capture
- 🐳 **Fully Containerized** - Docker + Docker Compose with 3 environments
- 🤖 **CI/CD Automated** - GitHub Actions pipeline with fail-fast smoke tests
- 🎨 **Page Object Model** - Clean, maintainable test architecture
- 📸 **Visual Evidence** - Screenshots on test failures only
- 🧪 **Professional Testing** - Log4j 2, Allure, TestNG, REST Assured, Vitest

### Modern Technology Stack
- **Java 21** - Latest LTS version
- **Python 3.13** - Latest stable version (for Robot Framework & Locust)
- **Node.js 20** - JavaScript runtime (for Cypress & Playwright)
- **TypeScript 5.9.3** - Type-safe JavaScript (for Cypress & Playwright)
- **Selenium 4.40.0** - Modern WebDriver API with Grid support
- **Playwright 1.58.2** - Fast and reliable end-to-end testing
- **Cypress 15.11.0** - JavaScript end-to-end testing framework
- **Robot Framework 2.1.0** - Keyword-driven test automation
- **REST Assured 6.0.0** - REST API testing & validation
- **Cucumber 7.33.0** - BDD framework with Gherkin
- **TestNG** - Advanced test framework with data providers
- **Log4j 2.25.3** - Professional structured logging (updated via Dependabot PR #52)
- **Maven 3.9.11** - Build management (wrapper included!)
- **Docker & Docker Compose** - Complete containerization
- **Allure3 CLI 3.0.0 (configurable), Allure2 Java 2.32.0** - Beautiful test reporting with screenshots (configurable via `config/environments.json`)
- **GitHub Actions** - Automated CI/CD pipeline
- **Google Cloud Secret Manager** - Enterprise-grade security
- **WebDriverManager 6.3.3** - Automatic driver management

### Recent Improvements (December 18, 2025)
- ✅ **Performance Integration** - Re-targeted 100% of performance tests to hit internal app services (ports 8003/3003).
- ✅ **Stability & Gating** - Implemented Fail-Fast Barrier Propagation across all 7 stages of the CI/CD pipeline.
- ✅ **Unified Performance Suite** - Integrated Locust, Gatling, and JMeter into every environment stage (DEV/TEST).
- ✅ **Next.js Stability** - Optimized internal routing for IPv6 compatibility with Next.js dev servers.
- ✅ **Automation Scaling** - Automatic performance smoke checks on every push to `main`.

---

## 🚀 Quick Start

### **Option 1: Docker (Recommended - No Setup Required!)**

```bash
# 1. Clone the repository
git clone https://github.com/CScharer/full-stack-qa.git
cd full-stack-qa

# 2. Start Selenium Grid
docker-compose up -d selenium-hub chrome-node-1

# 3. Run tests with beautiful Allure reports
./scripts/generate-allure-report.sh

# That's it! Tests run in Docker, report opens automatically! 🎉
```

### **Option 2: Local Execution**

```bash
# 1. Clone the repository
git clone https://github.com/CScharer/full-stack-qa.git
cd full-stack-qa

# 2. Authenticate with Google Cloud (for password retrieval)
gcloud auth application-default login
gcloud config set project cscharer

# 3. Copy configuration templates
cp xml/Companies.xml.template xml/Companies.xml
cp xml/UserSettings.xml.template xml/UserSettings.xml

# 4. Run tests (Maven wrapper included - no Maven install needed!)
./mvnw clean test

# Or use helper script
./scripts/run-tests.sh Scenarios chrome
```

**That's it!** The framework will automatically fetch passwords from Google Cloud Secret Manager.

---

## 📦 Prerequisites

### Required
- **Java 21+** - [Download JDK](https://adoptium.net/)
- **Google Cloud SDK** - [Install gcloud](https://cloud.google.com/sdk/docs/install)
- **Git** - For version control

### Optional (for specific frameworks)
- **Node.js 20+** - [Download Node.js](https://nodejs.org/) (for Cypress & Playwright)
- **Python 3.13+** - [Download Python](https://www.python.org/) (for Robot Framework & Locust)
- **Docker & Docker Compose** - For containerized execution
- **IDE** - IntelliJ IDEA, Eclipse, or VS Code
- **Pre-commit** - Already configured (install with `pip install pre-commit`)

### No Maven Required!
This project includes **Maven Wrapper** (`./mvnw`), so you don't need to install Maven separately.

---

## 💻 Installation

### 1. Clone Repository
```bash
git clone https://github.com/CScharer/full-stack-qa.git
cd full-stack-qa
```

### 2. Setup Google Cloud Authentication
```bash
# Authenticate
gcloud auth application-default login

# Set project
gcloud config set project cscharer

# Verify you can access secrets
gcloud secrets versions access latest --secret="AUTO_BTSQA_PASSWORD"
```

### 3. Copy Configuration Templates
```bash
# Copy template files to create working configurations
cp xml/Companies.xml.template xml/Companies.xml
cp xml/UserSettings.xml.template xml/UserSettings.xml
cp Configurations/Environments.xml.template Configurations/Environments.xml
```

### 4. Verify Installation
```bash
# Compile project
./mvnw clean compile test-compile

# Should see: BUILD SUCCESS ✅
```

### 5. ✅ **AUTOMATIC**: Git Hooks Installation
Git hooks are **automatically maintained** via a post-checkout hook that runs on every `git checkout`.

**What gets installed:**
- **Pre-commit hook**: Formats code automatically (20-40 seconds, no compilation/validation)
- **Pre-push hook**: Formats, compiles, and validates code before push (30-60 seconds)
- **Post-checkout hook**: Ensures hooks are installed and up-to-date on every checkout

**How it works:**
1. **One-time setup**: Run `./scripts/install-git-hooks.sh` once after cloning (installs all hooks including post-checkout)
2. **Automatic maintenance**: The post-checkout hook ensures all hooks are installed and up-to-date on every checkout
3. **No ongoing manual steps**: Once installed, hooks are automatically maintained

**First-time setup (required once per clone):**
```bash
# After cloning, install hooks once:
./scripts/install-git-hooks.sh

# After that, hooks are automatically maintained on every checkout!
```

**Why one-time setup?**
Git hooks live in `.git/hooks/` which isn't tracked by Git (for security). The post-checkout hook maintains everything automatically after the initial install, but it needs to be created first.

**⚠️ IMPORTANT**: The pre-commit hook automatically formats code before each commit, and the pre-push hook validates everything, ensuring:
- **Pre-commit (formatting only)**:
  - Code is formatted (Prettier + Google Java Format) - on commit
  - Unused/duplicate imports are removed (Spotless) - on commit
  - Auto-fixed files are staged automatically
- **Pre-push (all validation)**:
  - Code quality is verified (Checkstyle, PMD) - on push
  - Code compiles successfully - on push
  - Node.js, TypeScript, GitHub Actions, Shell scripts, and Security checks - on push

**Manual Installation (if needed):**
If hooks aren't auto-installed, you can manually run:
```bash
./scripts/install-git-hooks.sh
```

**Note**: You can bypass hooks with `--no-verify` (not recommended):
- `git commit --no-verify` - Skip pre-commit hook
- `git push --no-verify` - Skip pre-push hook

### 6. (Optional) Manual Formatting (if hooks not installed)
```bash
# Run the automated formatting script manually before every commit
./scripts/format-code.sh

# This script:
# - Removes unused/duplicate imports (Spotless)
# - Formats code and sorts imports (Prettier)
# - Fixes line length issues (Google Java Format)
# - Verifies code quality (Checkstyle, PMD)
# - Ensures compilation works
```

**⚠️ IMPORTANT**: If you don't install Git hooks, you **MUST** run `format-code.sh` manually before every commit to maintain code quality and ensure zero violations.

### 7. (Optional) Install Pre-commit Framework Hooks
```bash
pip install pre-commit
pre-commit install
```

---

## ⚙️ Configuration

### Google Cloud Secret Manager

All passwords are securely stored in Google Cloud Secret Manager. The framework automatically retrieves them at runtime.

**Secrets are organized as**:
- Application passwords: `AUTO_*_PASSWORD`
- Company accounts: `AUTO_COMPANY_*_PASSWORD`
- Test credentials: `AUTO_TEST_*`, `AUTO_SAUCELABS_*`

**View your secrets**:
```bash
gcloud secrets list | grep AUTO
```

### Environment Configuration

Configure test execution in `Configurations/Environments.xml`:
- Browser selection (Chrome, Firefox, Edge)
- Timeouts (page, element, alert)
- Grid settings (local vs remote)
- Logging options

See `Configurations/README.md` for details.

---

## 🧪 Running Tests

### ⚠️ Before Running Tests: Format Code First

**REQUIRED**: Always format your code before running tests or committing:

```bash
./scripts/format-code.sh
```

This ensures:
- ✅ Code is properly formatted
- ✅ Imports are sorted correctly
- ✅ Line length violations are fixed
- ✅ Code quality standards are met

### Quick Smoke Tests (⚡ < 2 minutes)

Fast critical path verification before committing:

```bash
# Run smoke tests (5 critical tests)
./scripts/run-smoke-tests.sh

# Or with Docker directly
docker-compose up -d selenium-hub chrome-node-1
docker-compose run --rm tests -Dtest=SmokeTests
docker-compose down
```

**What it does:**
- ✅ Verifies Grid connection
- ✅ Tests basic navigation
- ✅ Checks search functionality
- ✅ Validates form interaction
- ✅ Fast feedback (< 2 min vs 15+ min full suite)

### Extended Test Suite (📊 30+ scenarios)

Comprehensive testing with data-driven, negative, and advanced tests:

```bash
# Run all extended tests
docker-compose up -d selenium-hub chrome-node-1
docker-compose run --rm tests -DsuiteXmlFile=testng-extended-suite.xml
docker-compose down
```

**What it includes:**

**Data-Driven Tests (19 scenarios):**
- ✅ Multiple search queries (5 data sets)
- ✅ Website accessibility (5 sites)
- ✅ Edge case inputs (4 scenarios)

**Negative Tests (7 scenarios):**
- ✅ Non-existent element handling
- ✅ Invalid URL navigation
- ✅ Timeout handling
- ✅ Error recovery
- ✅ Stale element handling

**Advanced Features (7 scenarios):**
- ✅ JavaScript execution
- ✅ Cookie management
- ✅ Window management
- ✅ Keyboard actions
- ✅ Browser navigation
- ✅ Performance metrics

### API Testing (🌐 6 REST API tests)

REST API testing with REST Assured and Robot Framework - **No Selenium Grid required!**

```bash
# Run all API tests (fast, no browser needed)
./scripts/run-api-tests.sh

# Or with Maven directly
./mvnw test -DsuiteXmlFile=testng-api-suite.xml

# Or Robot Framework API tests
python3 -m robot.run src/test/robot/APITests.robot
```

**What it includes:**

**Java API Tests (3 tests - REST Assured):**
- ✅ API contract validation
- ✅ OAuth authentication schema
- ✅ Response schema validation

**Robot Framework API Tests (3 tests - RequestsLibrary):**
- ✅ GET single post
- ✅ GET all posts
- ✅ POST create new post

**Benefits:**
- ⚡ **Fast**: No browser startup overhead
- 🚀 **Lightweight**: Run anywhere (CI/CD, local, Docker)
- 📊 **Integrated**: Same Allure reports as UI tests
- 🔄 **Reusable**: REST Assured for all API testing

### Additional UI Testing Frameworks (🎭 3 Frameworks)

This framework now supports multiple UI testing tools beyond Selenium:

#### **Playwright (TypeScript)**
Fast and reliable end-to-end testing with auto-waiting and network interception:

```bash
# Run Playwright tests
./scripts/run-playwright-tests.sh chromium

# Or directly
cd playwright && npm test
```

**Features:**
- ✅ TypeScript for type safety
- ✅ Auto-waiting for elements
- ✅ Network interception and mocking
- ✅ Multi-browser support (Chromium, Firefox, WebKit)
- ✅ Page Object Model pattern
- ✅ Screenshot and video capture
- ✅ HTML reports

**Test Location:** `playwright/tests/`

#### **Cypress (TypeScript)**
Modern TypeScript end-to-end testing framework with time-travel debugging:

```bash
# Run Cypress tests (interactive mode)
./scripts/run-cypress-tests.sh open

# Run Cypress tests (headless)
./scripts/run-cypress-tests.sh run chrome
```

**Features:**
- ✅ TypeScript for type safety
- ✅ Time-travel debugging
- ✅ Real-time reloads
- ✅ Automatic waiting
- ✅ Network stubbing
- ✅ Screenshot and video capture
- ✅ Cross-browser support

**Test Location:** `cypress/cypress/e2e/`

#### **Robot Framework (Python)**
Keyword-driven test automation with human-readable syntax:

```bash
# Run Robot Framework tests
./scripts/run-robot-tests.sh

# Run specific test file
./scripts/run-robot-tests.sh GoogleSearchTests.robot

# Or with Maven
./mvnw test -Probot
```

**Features:**
- ✅ Human-readable keyword syntax
- ✅ Built-in libraries (Selenium, Requests, etc.)
- ✅ Data-driven testing
- ✅ HTML reports
- ✅ Easy to learn for non-programmers
- ✅ Extensible with custom libraries

**Test Location:** `src/test/robot/`

**Framework Comparison:**

| Framework | Language | Best For | Speed | Learning Curve |
|-----------|----------|----------|-------|----------------|
| **Selenium** | Java | Legacy support, Grid | Medium | Medium |
| **Playwright** | TypeScript | Modern apps, reliability | Fast | Medium |
| **Cypress** | TypeScript | Frontend-heavy apps | Fast | Easy |
| **Robot Framework** | Python | Non-technical testers | Medium | Easy |

### Performance Testing (⚡ 3 Tools)

Load and stress testing with industry-leading tools:

```bash
# Locust (40% - Primary tool, Python)
./scripts/run-locust-tests.sh

# Gatling (30% - Detailed reports, Scala)
./scripts/run-gatling-tests.sh

# JMeter (30% - Industry standard, Java)
./scripts/run-jmeter-tests.sh

# Run all performance tests
./scripts/run-all-performance-tests.sh
```

**Tool Comparison:**

| Tool | Language | Best For | Output |
|------|----------|----------|--------|
| **Artillery + Playwright** (20%) | JavaScript | Browser load testing, Core Web Vitals | JSON + Allure |
| **Gatling** (25%) | Scala | Detailed analysis, beautiful reports | HTML Dashboard |
| **JMeter** (25%) | Java | Industry standard, protocol support | HTML + CSV |
| **Locust** (30%) | Python | Real-time monitoring, flexible scripting | Web UI + HTML |

**What it includes:**

**Artillery + Playwright Tests (20% allocation - Browser-Level):**
- ✅ homepage-minimal-test.yml - Quick smoke test
- ✅ homepage-load.yml - Homepage load testing
- ✅ applications-flow.yml - Full CRUD flow testing
- ✅ Real browser rendering (Chromium, Firefox, WebKit)
- ✅ Core Web Vitals tracking (LCP, FCP, CLS, TTI)
- ✅ JavaScript execution and rendering metrics

**Gatling Tests (25% allocation - Protocol-Level):**
- ✅ ApiLoadSimulation.scala - REST API load test
- ✅ WebLoadSimulation.scala - Web page load test
- ✅ Ramp: 1-50 users over 30s
- ✅ Beautiful HTML reports with graphs

**JMeter Tests (25% allocation - Protocol-Level):**
- ✅ API_Performance_Test.jmx - API load testing
- ✅ Web_Load_Test.jmx - Website load testing
- ✅ 30-50 concurrent users
- ✅ Industry-standard reports

**Locust Tests (30% allocation - Protocol-Level):**
- ✅ api_load_test.py - API performance testing
- ✅ web_load_test.py - Website load testing
- ✅ comprehensive_load_test.py - Complete scenarios
- ✅ Real-time web UI: http://localhost:8089
- ✅ 100-200 concurrent users

**Metrics Collected:**
- ⏱️  Response times (min/max/avg/p95/p99)
- 📊 Throughput (requests per second)
- ✅ Success/failure rates
- 👥 Concurrent users
- 📈 Performance trends

**Automated Execution:**
- 🌙 **Nightly Quick Check** (10 PM CST) - 30-second smoke test
- 📅 **Weekly Comprehensive** (Sunday 10 PM CST) - All 4 tools
- 🎯 **Manual Trigger** - Run any time via GitHub Actions UI
- 🔄 **CI/CD Integration** - Run in main CI pipeline with UI tests

**CI/CD Integration:**
- Performance tests can run in the same pipeline as UI tests
- Environment-aware: Run in `dev`, `test`, or `dev-test` (never prod)
- Parallel execution: Performance tests run simultaneously with UI tests
- Unified reporting: Results included in combined Allure reports
- Options: `ui-only`, `performance-only`, or `all` (both)
- Artillery tests run automatically on branches (dev) and main (dev + test)

**See:** [Performance Testing Guide](docs/guides/testing/PERFORMANCE_TESTING.md)

### Using Helper Scripts (Recommended)

```bash
# Run all local tests (Cypress, Playwright, Robot Framework) - No Docker required!
./scripts/run-tests-local.sh

# Selenium tests (default - requires Docker/Grid)
./scripts/run-tests.sh Scenarios chrome

# Playwright tests
./scripts/run-playwright-tests.sh chromium true

# Cypress tests
./scripts/run-cypress-tests.sh run chrome

# Robot Framework tests
./scripts/run-robot-tests.sh

# API tests
./scripts/run-api-tests.sh

# Performance tests
./scripts/run-all-performance-tests.sh

# Just compile (no tests)
./scripts/compile.sh
```

**💡 Tip**: Use `./scripts/run-tests-local.sh` to run tests without Docker (saves disk space!). See [Local Testing Guide](docs/guides/testing/LOCAL_TESTING.md) for details.

### Using Maven Wrapper Directly

```bash
# Run all tests
./mvnw clean test

# Run specific test class
./mvnw test -Dtest=Scenarios

# Run specific test method
./mvnw test -Dtest=Scenarios#Google

# Run with specific browser
./mvnw test -Dtest=Scenarios#Microsoft -Dbrowser=chrome

# Skip tests during build
./mvnw clean install -DskipTests
```

### Parallel Execution

Tests run in parallel by default (5 threads):

```bash
# Parallel execution is configured in pom.xml
./mvnw test
# Runs with 5 parallel threads automatically
```

---

## 📁 Project Structure

> **💡 Tip**: You can use the `tree` command to get a better view of the Project Structure like so:
> ```bash
> tree -L 3 -d -I "__pycache__|bin|node_modules|results|target*|test-results|venv" ../$(basename "$PWD")
> ```

```
full-stack-qa/
├── src/
│   ├── main/java/com/cjs/qa/app/          # Main application code
│   └── test/java/com/cjs/qa/              # Test suites (30+ packages)
│       ├── google/                         # Google test suite
│       ├── microsoft/                      # Microsoft test suite (33 files)
│       ├── linkedin/                       # LinkedIn test suite
│       ├── vivit/                          # Vivit community tests (25 files)
│       ├── bts/                            # BTS internal apps (60 files)
│       ├── core/                           # Core framework
│       ├── selenium/                       # Selenium wrappers
│       ├── utilities/                      # Helper utilities (43 files)
│       └── ...                             # 25+ more test suites
├── cypress/                                # Cypress test framework
├── playwright/                             # Playwright test framework
├── vibium/                                 # Vibium test framework
├── frontend/                               # Frontend application
├── backend/                                # Backend application
├── scripts/                                # Helper scripts
│   ├── run-tests.sh                        # Easy test execution
│   ├── run-specific-test.sh                # Run specific test
│   └── compile.sh                          # Compile only
├── docs/                                   # Documentation
│   ├── ANALYSIS.md                         # Project analysis
│   └── (Analysis documents archived)
│   └── (INTEGRATION_COMPLETE.md moved to PRIVATE/ folder)
│   └── NEXT_STEPS.md                       # Quick action guide
├── xml/                                    # Configuration files
│   ├── Companies.xml.template              # Company config template
│   └── UserSettings.xml.template           # User settings template
├── Configurations/                         # Environment configs
├── data/                                   # Test data and SQL scripts
├── .github/                                # GitHub templates
│   ├── ISSUE_TEMPLATE/                     # Issue templates
│   ├── pull_request_template.md            # PR template
│   └── CODEOWNERS                          # Code ownership
├── pom.xml                                 # Maven configuration
├── .editorconfig                           # Editor settings
├── .pre-commit-config.yaml                 # Pre-commit hooks
└── mvnw                                    # Maven wrapper (no install needed!)
```

---

## 🎯 Test Suites

| Suite | Files | Description |
|-------|-------|-------------|
| **Google** | 5 | Search, Maps, Flights |
| **Microsoft** | 33 | Azure, Office365, OneDrive, Rewards |
| **LinkedIn** | 8 | Profile, Connections, Jobs |
| **Vivit** | 25 | Community portal testing |
| **BTS** | 60 | Internal PolicyStar applications |
| **Atlassian** | 22 | Jira, Confluence, Bamboo, Stash |
| **Bitcoin** | 1 | Cryptocurrency testing |
| **Dropbox** | 3 | File sharing |
| **United Airlines** | 8 | Booking, Account management |
| **Wellmark** | 8 | Healthcare portal |
| **YourMembership** | 61 | API testing suite |
| ... | ... | 25+ more domains |

**Total**: 394+ test files across 30+ domains

---

## 🏗️ Architecture

### Design Pattern: Page Object Model (POM)

```
Test Layer (Scenarios.java)
    ↓
Page Objects (LoginPage, SearchPage, etc.)
    ↓
Selenium Wrapper (SeleniumWebDriver, Page)
    ↓
WebDriver (Chrome, Firefox, Edge, etc.)
```

### Key Components

- **Core Framework** (`com.cjs.qa.core`)
  - `Environment.java` - Environment management
  - `AutGui.java` - Application under test interface
  - `QAException.java` - Custom exception handling

- **Selenium Layer** (`com.cjs.qa.selenium`)
  - `SeleniumWebDriver.java` - WebDriver wrapper
  - `Page.java` - Base page object
  - `ISelenium.java` - Selenium interface

- **Utilities** (`com.cjs.qa.utilities`)
  - `SecureConfig.java` - Google Cloud Secret Manager integration
  - `GoogleCloud.java` - Secret retrieval
  - `JavaHelpers.java` - Helper methods
  - `FSO.java` - File system operations
  - `Email.java` - Email utilities

- **Page Objects** (per domain)
  - Domain-specific page objects
  - Organized by application/site

---

## 🔐 Security

### Enterprise-Grade Security Standards

This framework maintains security standards that **exceed industry best practices**, implementing multiple layers of protection, automated verification, and continuous monitoring.

**Key Security Features:**
- ✅ **Google Cloud Secret Manager** - Enterprise-grade credential management
- ✅ **Zero credentials in source code** - 100% secure credential storage
- ✅ **AES-256 encryption** at rest - Industry-leading encryption standard
- ✅ **TLS 1.3 encryption** in transit - Latest transport security protocol
- ✅ **Automated security scanning** - Pre-commit hooks and CI/CD checks
- ✅ **CodeQL Security Scanning** - Weekly automated code security analysis with Copilot Autofix
- ✅ **Dependabot** - Automated dependency updates for npm, Python, Maven, GitHub Actions, and Docker
- ✅ **Comprehensive audit logging** - Complete security event tracking
- ✅ **IAM-based access control** - Granular permissions per credential
- ✅ **Automated secret rotation** - Built-in versioning and rotation

### Usage in Tests
```java
// Credentials retrieved securely from Google Cloud Secret Manager
String password = EPasswords.BTSQA.getValue();
String apiKey = EAPIKeys.VIVIT_GT_WEBINAR_CONSUMER_KEY.getValue();
// All credentials managed securely - no hardcoded values ✅
```

### Protected Files

All sensitive configuration files are **protected by .gitignore** and never committed:
- `xml/Companies.xml` - Company credentials
- `xml/UserSettings.xml` - Test credentials
- `Configurations/Environments.xml` - Environment configurations
- Any `*-key.json` - Service account keys

### Security Documentation

For comprehensive security standards and practices, see:
- **[Security Standards & Practices](docs/process/SECURITY.md)** - Complete security documentation
- **[AI Workflow Rules](docs/process/AI_WORKFLOW_RULES.md)** - Development security rules
- **[Pre-Pipeline Validation](docs/process/PRE_PIPELINE_VALIDATION.md)** - Security checks

**Security is a top priority with automated verification and testing in place.**

---

## 📊 Test Reporting

### Allure Reports (Recommended) 🎯

Beautiful, interactive HTML reports with screenshots, graphs, and trends:

```bash
# Option 1: One-command (starts Grid, runs tests, opens report)
./scripts/generate-allure-report.sh

# Option 2: Manual
docker-compose up -d selenium-hub chrome-node-1
docker-compose run --rm tests -Dtest=SimpleGridTest,EnhancedGridTests
allure serve target/allure-results
docker-compose down
```

**Features:**
- 📊 Interactive dashboards with graphs
- 📸 Screenshots automatically captured on failures
- 📈 Historical trends (track improvements)
- 🏷️ Organized by Epic/Feature/Story
- ⏱️ Performance metrics
- 🎯 Severity-based filtering

**See:** [docs/guides/testing/ALLURE_REPORTING.md](docs/guides/testing/ALLURE_REPORTING.md) for complete guide

### Traditional Reports

**JUnit Reports**
```bash
./mvnw test
open target/surefire-reports/index.html
```

**Cucumber Reports**
```bash
./mvnw test
open target/cucumber-reports/cucumber.html
```

**TestNG Reports**
```bash
# Available in target/surefire-reports/
```

---

## 🔧 Development

### Build Commands

```bash
# Clean build
./mvnw clean

# Compile main code
./mvnw compile

# Compile tests
./mvnw test-compile

# Run all tests
./mvnw test

# Package (skip tests)
./mvnw package -DskipTests

# Run specific test suite
./mvnw test -Dtest=Scenarios#Google
```

### Helper Scripts

```bash
# Run tests with browser selection
./scripts/run-tests.sh Scenarios chrome

# Run specific test method
./scripts/run-specific-test.sh Scenarios Microsoft

# Compile without tests (faster)
./scripts/compile.sh
```

### Code Quality

**Status**: ✅ **0 Checkstyle violations**, **0 PMD violations** - All code quality standards met

**Tools**:
- ✅ **Checkstyle** - Google Java Style, 120-char line length, all rules enabled
- ✅ **PMD** - Custom ruleset, 0 violations
- ✅ **Spotless** - Import management (java,javax,org,com ordering)
- ✅ **Prettier** - Code formatting (120-char line length)
- ✅ **Google Java Format** - Line length fixes

**Pre-commit hooks automatically check**:
- ✅ Code formatting (Prettier + Spotless + Google Java Format)
- ✅ Import ordering and cleanup
- ✅ Checkstyle violations
- ✅ PMD violations
- ✅ Trailing whitespace
- ✅ File endings
- ✅ YAML/XML/JSON syntax
- ✅ Hardcoded secrets detection
- ✅ Sensitive file blocking
- ✅ Large file warnings

**Key Features**:
- ✅ **GuardedLogger** - Automatic log statement guards (no manual checks needed)
- ✅ **Zero violations** - All PMD and Checkstyle violations resolved
- ✅ **Automated formatting** - Pre-commit hooks format code automatically
- ✅ **CI optimization** - Separate verification script for faster CI runs

```bash
# Format code before commit (required if hooks not installed)
./scripts/format-code.sh

# Install hooks (one-time setup)
./scripts/install-git-hooks.sh
```

**See**: [Code Quality Guide](docs/guides/java/CODE_QUALITY.md) for complete documentation

---

## 🐳 Docker & Selenium Grid

### Quick Start with Docker

```bash
# 1. Start Selenium Grid
docker-compose up -d selenium-hub chrome-node-1 firefox-node

# 2. View Grid Console
open http://localhost:4444

# 3. Run tests with Allure report
./scripts/generate-allure-report.sh

# 4. Or run tests manually
docker-compose run --rm tests -Dtest=SimpleGridTest,EnhancedGridTests

# 5. Stop everything
docker-compose down
```

### Three Docker Environments

- **`docker-compose.yml`** - Full setup with monitoring (Prometheus + Grafana)
- **`docker-compose.dev.yml`** - Lightweight for development
- **`docker-compose.prod.yml`** - Production with auto-scaling (4 Chrome + 2 Firefox nodes)

```bash
# Use specific environment
docker-compose -f docker-compose.dev.yml up -d
docker-compose -f docker-compose.prod.yml up -d
```

### Selenium Grid Console & Debugging

- **Grid UI**: http://localhost:4444
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)
- **Chrome VNC**: vnc://localhost:5900 or http://localhost:7900 (noVNC)
- **Firefox VNC**: vnc://localhost:5902 or http://localhost:7902 (noVNC)

### Docker Features

- ✅ **Selenium Grid** - Hub + 4 browser nodes (2 Chrome, Firefox, Edge)
- ✅ **VNC/noVNC** - Visual debugging (watch tests run live!)
- ✅ **Monitoring** - Prometheus metrics + Grafana dashboards
- ✅ **Multi-stage builds** - Optimized 414MB image
- ✅ **ARM64 support** - Works on Apple Silicon (M1/M2/M3)
- ✅ **Auto-scaling** - Scale nodes with `docker-compose up --scale`
- ✅ **Health checks** - Automatic service monitoring
- ✅ **WebDriverManager** - No manual driver downloads

**See [docs/guides/infrastructure/DOCKER.md](docs/guides/infrastructure/DOCKER.md) for complete Docker guide**

### Grid Management Scripts

```bash
./scripts/docker/grid-start.sh   # Start Grid
./scripts/docker/grid-stop.sh    # Stop Grid
./scripts/docker/grid-health.sh  # Check health
./scripts/docker/grid-scale.sh   # Scale nodes
```

---

## 🤖 CI/CD - GitHub Actions

### Automated Testing Pipeline

Every push to `main` or `develop` triggers:

```
✅ Build & Compile → ✅ Grid Tests (Chrome) → ✅ Allure Report
                   → ✅ Grid Tests (Firefox) → ✅ Code Quality
                                             → ✅ Docker Build
                                             → ✅ Test Summary
```

**Matrix Testing:**
- 11 tests × 2 browsers = **22 test executions**
- Automatic screenshot capture on test failures only
- Allure report with graphs and trends
- GitHub Pages deployment

**View Results:**
- [GitHub Actions Tab](https://github.com/CScharer/full-stack-qa/actions)
- Check build status badge above
- Download artifacts (test results, screenshots, Allure reports)

**See:** [docs/guides/infrastructure/GITHUB_ACTIONS.md](docs/guides/infrastructure/GITHUB_ACTIONS.md) for complete CI/CD guide

---

## 📚 Documentation

Comprehensive documentation available in `/docs`:

### Core Guides
- **[DOCKER.md](docs/guides/infrastructure/DOCKER.md)** - Complete Docker & Grid guide (500+ lines)
- **[ALLURE_REPORTING.md](docs/guides/testing/ALLURE_REPORTING.md)** - Allure setup & usage (500+ lines)
- **[GITHUB_ACTIONS.md](docs/guides/infrastructure/GITHUB_ACTIONS.md)** - CI/CD pipeline guide (400+ lines)

### Process Documents
- **[PRE_PIPELINE_VALIDATION.md](docs/process/PRE_PIPELINE_VALIDATION.md)** - Pre-commit/pre-push validation checklist to prevent pipeline failures
- **[QUICK_REFERENCE.md](docs/process/QUICK_REFERENCE.md)** - One-page quick reference for critical validation checks
- **[NAMING_STANDARDS.md](docs/process/NAMING_STANDARDS.md)** - Living document for naming conventions (CI/CD, documents, code)
- **[VERSION_TRACKING.md](docs/process/VERSION_TRACKING.md)** - Living document for tracking dependency versions and scheduling updates
- **[VERSION_MONITORING.md](docs/process/VERSION_MONITORING.md)** - Automated version monitoring and alerting system documentation
- **20251220_NEXT_STEPS_AFTER_PR53.md** - Comprehensive work plan for post-PR #53 execution (archived)
- **[AI_WORKFLOW_RULES.md](docs/process/AI_WORKFLOW_RULES.md)** - Detailed workflow rules and guidelines for AI-assisted development

### Getting Started
- **[NAVIGATION.md](docs/NAVIGATION.md)** - Documentation navigation guide
- **INTEGRATION_COMPLETE.md** - Secret Manager setup (moved to PRIVATE/ folder)

### Planning & Analysis
- **ANALYSIS.md** - Full project analysis (archived)
- **ANALYSIS_SUGGESTIONS.md** - 150-task roadmap (archived)

### Implementation Guides
- **ANALYSIS_PS_RESULTS.md** - Password migration results (archived)
- **QUICK_WINS_COMPLETE.md** - Quick wins summary (archived)

### Configuration
- **[xml/README.md](xml/README.md)** - XML configuration setup
- **[Configurations/README.md](Configurations/README.md)** - Environment configuration
- **[scripts/README.md](scripts/README.md)** - Script usage guide

---

## 🤝 Contributing

We welcome contributions! Please follow these guidelines:

1. **Fork the repository**
2. **Clone and checkout** - Git hooks auto-install via post-checkout hook
3. **Create a feature branch** (`git checkout -b feature/amazing-feature`)
4. **Make your changes**
5. **Run tests** (`./mvnw test`)
6. **Commit your changes** (pre-commit hook automatically formats code)
7. **Push to branch** (`git push origin feature/amazing-feature`) - Pre-push hook formats, compiles, and validates code quality
8. **Open a Pull Request** (template will auto-populate)

### Pull Request Guidelines
- Use the PR template provided
- Ensure all tests pass
- Update documentation if needed
- No hardcoded secrets (pre-commit hooks will block)
- Follow code style (.editorconfig)

See **[CODE_OF_CONDUCT.md](docs/process/CODE_OF_CONDUCT.md)** for community guidelines.

---

## 👥 Team Setup

### New Team Member Onboarding

```bash
# 1. Clone repository
git clone https://github.com/CScharer/full-stack-qa.git
cd full-stack-qa

# Git hooks are automatically installed via post-checkout hook
# (Pre-commit hook formats code, pre-push hook verifies quality)

# 2. Authenticate with Google Cloud
gcloud auth application-default login
gcloud config set project cscharer

# 3. Copy templates
cp xml/Companies.xml.template xml/Companies.xml
cp xml/UserSettings.xml.template xml/UserSettings.xml

# 4. Run tests!
./mvnw clean test
```

**No password sharing needed!** All credentials are fetched from Google Cloud Secret Manager.

---

## 🔬 Test Examples

### Run Specific Test Suite
```bash
# Google tests
./mvnw test -Dtest=Scenarios#Google

# Microsoft tests
./mvnw test -Dtest=Scenarios#Microsoft

# LinkedIn tests
./mvnw test -Dtest=Scenarios#LinkedIn
```

### Run with Different Browsers
```bash
# Chrome (default)
./mvnw test -Dbrowser=chrome

# Firefox
./mvnw test -Dbrowser=firefox

# Edge
./mvnw test -Dbrowser=edge
```

### Using Helper Scripts
```bash
# Easy syntax for running tests
./scripts/run-tests.sh Scenarios chrome
./scripts/run-tests.sh Scenarios firefox

# Run specific test method
./scripts/run-specific-test.sh Scenarios Google
```

---

## 🛠️ Technology Details

### Dependencies (Key Libraries)

| Category | Library | Version |
|----------|---------|---------|
| **WebDriver** | Selenium | 4.40.0 |
| **BDD** | Cucumber | 7.33.0 |
| **Testing** | JUnit | 6.0.2 |
| **Testing** | TestNG | 7.11.0 |
| **Testing** | Vitest | 4.1.0 |
| **Database** | JDBC (Multi-DB) | Various |
| **HTTP** | Apache HttpClient | 4.5.14 |
| **JSON** | Gson | 2.13.2 |
| **Excel** | Apache POI | 5.5.1 |
| **PDF** | PDFBox | 3.0.6 |
| **Security** | Google Cloud Secret Manager | 2.82.0 |
| **Driver Management** | WebDriverManager | 6.3.3 |
| **UI Testing** | Playwright (TS) | 1.58.2 |
| **UI Testing** | Cypress (TS) | 15.11.0 |
| **UI Testing** | Robot Framework | 2.1.0 |
| **Database** | H2, SQLite, MSSQL | Various |
| **Docker** | Docker Compose | 3.8 |
| **CI/CD** | GitHub Actions | Latest |

**Total**: 50+ dependencies, all managed via Maven

---

## 📈 Project Stats

- **Test Files**: 394+ Java files
- **Test Suites**: 30+ domains
- **Page Objects**: 150+ pages
- **Utilities**: 43 helper classes
- **Lines of Code**: 100,000+ lines
- **Compilation**: 100% success rate
- **Security**: 43 secrets in Google Cloud
- **Documentation**: 200+ pages

---

## 🏆 Recent Achievements

### November 8, 2025 - Major Infrastructure Update

**✅ Complete Containerized Testing Infrastructure**
- Docker + Selenium Grid (Hub + 4 browser nodes)
- 3 Docker Compose environments (default, dev, prod)
- Monitoring stack (Prometheus + Grafana)
- VNC/noVNC debugging support
- ARM64 (Apple Silicon) compatibility

**✅ Allure Reporting with Screenshots**
- Allure Framework integration (Allure3 CLI: 3.0.0, Allure2 Java: 2.32.0)
- Automatic screenshot capture on test failures only
- Beautiful HTML dashboards with graphs
- Epic/Feature/Story organization
- Historical trend tracking
- **📊 [View Latest Report](https://cscharer.github.io/full-stack-qa/)** - Public GitHub Pages

**✅ GitHub Actions CI/CD Pipeline**
- Automated testing on every push
- Matrix testing (Chrome + Firefox)
- 6 parallel jobs (build, test, report, quality, docker, summary)
- Allure report generation and deployment
- Test artifact retention (7-30 days)

**✅ Working Test Suite (11 tests, 100% passing)**
- SimpleGridTest (3 tests) - Basic Grid verification
- EnhancedGridTests (8 tests) - Comprehensive scenarios
- All with Allure annotations and screenshots
- TestNG parallel execution ready

**✅ Security & Quality**
- 43 passwords secured in Google Cloud
- Zero hardcoded credentials
- Pre-commit hooks active
- WebDriverManager (auto driver management)
- AllureHelper utility for enhanced reporting

**✅ Progress Update**
- 65/150 tasks completed (43%)
- Phase 1 (Security): 100% ✅
- Phase 2 (Docker & Infrastructure): 100% ✅
- Phase 3 (Testing & Reporting): 80% ✅
- 12 commits today, 2,000+ lines added

---

## 🐛 Troubleshooting

### Common Issues

#### "Failed to fetch secret"
```bash
# Solution: Authenticate with Google Cloud
gcloud auth application-default login
```

#### "Maven not found"
```bash
# Solution: Use Maven wrapper (included in repo)
./mvnw clean test
# NOT: mvn clean test
```

#### "Driver not found" (Windows)
```bash
# Solution: Drivers are managed automatically
# No manual driver installation needed
```

#### "Configuration file not found"
```bash
# Solution: Copy template files
cp XML/Companies.xml.template XML/Companies.xml
```

**More help**: See `docs/` directory for comprehensive guides.

---

## 📞 Support

### Getting Help
- **Documentation**: Check `/docs` directory
- **Issues**: Use GitHub issue templates
- **Questions**: Create a discussion on GitHub

### Resources
- [Selenium Documentation](https://www.selenium.dev/documentation/)
- [Cucumber Documentation](https://cucumber.io/docs)
- [Google Cloud Secret Manager](https://cloud.google.com/secret-manager/docs)
- [Maven Documentation](https://maven.apache.org/guides/)

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

Copyright © 2025 CJS Consulting, L.L.C

---

## 🙏 Acknowledgments

- **Framework**: Selenium WebDriver, Cucumber BDD
- **Security**: Google Cloud Secret Manager
- **Build Tool**: Apache Maven
- **Testing**: JUnit, TestNG
- **CI/CD**: GitHub Actions

---

## 📞 Contact

**Organization**: CJS Consulting, L.L.C
**Website**: http://www.cjsconsulting.com
**CI**: Jenkins at http://cscharer-laptop:8080/

---

## 🚀 What's Next?

Check out our roadmap (archived):
- **65/150 tasks completed (43%)** 🎯
- Phase 1 (Security): ✅ COMPLETE
- Quick Wins: ✅ COMPLETE
- Phase 2 (Docker & Infrastructure): ✅ COMPLETE
- Phase 3 (Testing & Reporting): ✅ 80% Complete
- Phase 4 (Advanced Features): 📋 Planned

### Upcoming Features
- Visual Regression Testing
- Cross-browser matrix optimization
- Mobile browser emulation
- Advanced security scanning (SAST/DAST)

**Want to contribute?** See [NAVIGATION.md](docs/NAVIGATION.md) for documentation structure and current work items!

---

<div align="center">

**Built with ❤️ by the CJS QA Team**

⭐ Star this repo if you find it useful!

</div>
