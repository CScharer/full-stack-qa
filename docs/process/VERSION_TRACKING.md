# Version Tracking & Update Schedule

**Date Created**: 2025-12-20  
**Status**: 📋 Living Document  
**Purpose**: Track dependency versions and schedule periodic updates  
**Update Frequency**: Monthly review recommended

> **💡 Automation**: Version validation is now automated via `scripts/quality/validate-dependency-versions.sh` and CI/CD job `validate-dependency-versions` (see `.github/workflows/ci.yml`). This helps prevent version drift and ensures versions are aligned across the project.
> 
> **✅ Pre-Push Validation**: Pre-push version validation is now implemented! The pre-push hook automatically validates Selenium versions before code is pushed. See [Selenium Grid Configuration Guide](../guides/infrastructure/SELENIUM_GRID.md) for details.

### 🔑 Status Legend
- `[✅]` = Completed / Verified / Current
- `[❌]` = Not Started / Needs Action / Failed
- `[🔍]` = In Progress / Under Investigation / Needs Review
- `[⚠️]` = Warning / Critical Issue / Update Available
- `[⏳]` = Pending / Waiting
- `[⏭️]` = Skipped (with justification)
- `[🔒]` = Locked (Do not update without approval)

---

## 🎯 Purpose

This living document serves as a centralized tracking system for all dependency versions across the repository. It should be reviewed and updated periodically (recommended: monthly) to:
- Identify available updates
- Track version changes over time
- Plan update schedules
- Document breaking changes and compatibility notes
- Maintain security posture

---

## 📅 Update Schedule

### Automated Dependency Management

**✅ Dependabot Fully Configured** (as of 2025-12-31)

All dependency ecosystems are now managed via **Dependabot**:
- **npm** (4 projects): cypress, frontend, vibium, playwright
- **Python/pip** (3 projects): backend, performance, test-data
- **Maven** (Java dependencies)
- **GitHub Actions** (workflow updates)
- **Docker** (container base images)

**Schedule**: Weekly checks (Sundays at 14:00 UTC = 08:00 CST / 09:00 CDT)

**Auto-merge**: Security updates automatically merged after CI/CD passes

**Monthly Audits**: Comprehensive dependency review on first day of each month

### Recommended Review Frequency
- **Automated**: Dependabot creates PRs for available updates (weekly)
- **Monthly**: Review Dependabot PRs and monthly audit reports
- **As needed**: Security patches (auto-merged if CI/CD passes)
- **Quarterly**: Review major version updates (manual review required)

### Last Review Dates
- **Initial Creation**: 2025-12-20
- **Last Review**: 2026-07-19 (safe stable bump across Maven/npm/Python + mobile tearDown fix; docs Latest Stable columns refreshed against registries)
- **Latest Stable Versions Check**: 2026-07-19 (registry check; Current Version columns match repo pins after bump)
- **Next Review**: 2026-08-01 (recommended)

### Stable vs. latest

All tracked dependencies are on **stable builds** and suitable for production. Not every dependency is at the **absolute latest** stable release; some have newer patch or minor versions available. That is intentional: the project prioritizes stability and security fixes, and applies non-critical updates during scheduled reviews or via Dependabot PRs.

- **Stable** = Current versions are supported, non-EOL, and (where applicable) security-patched.
- **Latest** = Newest stable release on the registry; may be one or more patch/minor versions ahead.

When in doubt, run `npm outdated`, `./mvnw versions:display-dependency-updates`, or `pip list --outdated` in the relevant project and compare with the "Latest Stable" column and the list below.



### Known available updates

As of **2026-07-19** (after second safe-bump pass):

<!-- prettier-ignore-start -->
| Dependency | Current | Latest available | Notes |
| -- | -- | -- | -- |
| TypeScript | 6.0.3 | 7.0.2 | Major — schedule separately |
| @types/node | 25.9.5 | 26.1.1 | Major — schedule separately |
| Jackson 2.x (core/databind) | 2.21.5 | 2.22.1 | Deferred minor |
| Hibernate | 5.6.15.Final | 6.6.x / 8.x | Jakarta migration; Dependabot #93 |
| js-yaml (override) | 3.15.0 | 5.x | Major — keep 3.x |
| @babel/core (override) | 7.29.7 | 8.x | Major — keep 7.x |
| mypy | 1.20.0 | 2.x | Major — schedule separately |
| structlog | 25.5.0 | 26.x | Major — schedule separately |
<!-- prettier-ignore-end -->


---

## 📦 Java/Maven Dependencies (pom.xml)

> **💡 Checking Latest Stable Versions**: To verify the latest stable versions for Maven dependencies, use:
> - `./mvnw versions:display-dependency-updates` (shows all available updates)
> - Check [Maven Central](https://search.maven.org/) for specific packages
> - Review Dependabot PRs for automated update suggestions
> - The "Latest Stable" column indicates the newest stable version available (may differ from Current Version if updates are available)

### Core Testing Frameworks

<!-- prettier-ignore-start -->
| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
| -- | -- | -- | -- | -- | -- |
| Selenium | 4.46.0 | 4.46.0 | [✅] | 2026-07-19 | Aligned with Grid workflow default |
| Selenide | 7.17.0 | 7.17.0 | [✅] | 2026-07-19 | Current stable |
| TestNG | 7.11.0 | 7.11.0 | [✅] | - | Current |
| JUnit | 6.1.2 | 6.1.2 | [✅] | 2026-07-19 | Current stable |
| Cucumber | 7.34.4 | 7.34.4 | [✅] | 2026-07-19 | Current stable |
| REST Assured | 6.0.1 | 6.0.1 | [✅] | 2026-07-19 | Requires Java 17+; Jackson **3.2.1** (`tools.jackson.core`) |
| Allure3 CLI | 3.0.0 | 3.0.0 | [✅] | 2025-12-30 | Active - Allure3 CLI in use (TypeScript-based, npm install) |
| Allure2 Java | 2.35.3 | 2.35.3 | [✅] | 2026-07-19 | allure-testng, allure-junit5, allure-java-commons |
<!-- prettier-ignore-end -->

### Build & Tools

<!-- prettier-ignore-start -->
| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
| -- | -- | -- | -- | -- | -- |
| Maven | 3.9.11 | 3.9.11 | [✅] | 2025-12-19 | Updated in PR #51 |
| Java | 21 | 21 (LTS) | [✅] | - | Current LTS version |
| Maven Compiler Plugin | 3.15.0 | 3.15.0 | [✅] | 2026-07-19 | Current stable version |
| Maven Surefire Plugin | 3.5.6 | 3.5.6 | [✅] | 2026-07-19 | Current stable |
| Maven Checkstyle Plugin | 3.6.0 | 3.6.0 | [✅] | - | Current |
| Checkstyle Tool | 13.8.0 | 13.8.0 | [✅] | 2026-07-19 | Current stable |
| SpotBugs | 4.10.3 | 4.10.3 | [✅] | 2026-07-19 | Current stable |
| PMD | 3.28.0 | 3.28.0 | [✅] | 2026-07-19 | maven-pmd-plugin |
<!-- prettier-ignore-end -->

### Performance Testing

<!-- prettier-ignore-start -->
| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
| -- | -- | -- | -- | -- | -- |
| Gatling | 3.15.1 | 3.15.1 | [✅] | 2026-07-19 | With gatling-maven-plugin 4.21.5 |
| JMeter | 5.6.3 | 5.6.3 | [✅] | - | Current |
| Scala | 2.13.18 | 2.13.18 | [✅] | 2025-12-19 | Updated in PR #51 - For Gatling |
<!-- prettier-ignore-end -->

### Utilities & Libraries

<!-- prettier-ignore-start -->
| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
| -- | -- | -- | -- | -- | -- |
| WebDriverManager | 6.3.4 | 6.3.4 | [✅] | 2026-04-06 | Current stable |
| Log4j 2 | 2.26.1 | 2.26.1 | [✅] | 2026-07-19 | `log4j2.version` (2.x Active Maintenance) |
| Logback Core | 1.5.38 | 1.5.38 | [✅] | 2026-07-19 | Overrides Gatling transitive line |
| Jackson Databind (3.x) | 3.2.1 | 3.2.1 | [✅] | 2026-07-19 | `jackson.version`; tools.jackson.core jackson-databind |
| Jackson Core (2.x) | 2.21.5 | 2.22.1 | [⚠️] | 2026-07-19 | `jackson2.version` — stay on 2.21.5 patched line; 2.22.1 available |
| Jackson Databind (2.x) | 2.21.5 | 2.22.1 | [⚠️] | 2026-07-19 | Explicit com.fasterxml pin; 2.22.1 available |
| Jackson Annotations | 2.21 | 2.21 | [✅] | 2026-07-19 | 2.x annotations alongside Jackson 3 databind (REST Assured 6) |
| Netty BOM | 4.2.16.Final | 4.2.16.Final | [✅] | 2026-07-19 | `netty-codec-http.version` / netty-bom import |
| Hibernate | 5.6.15.Final | 5.6.15.Final | [⚠️] | 2026-07-19 | Dependabot #93 / CVE-2026-0603; no patched 5.x; 6.x is Jakarta migration |
| Apache POI | 5.5.1 | 5.5.1 | [✅] | 2025-12-19 | Updated in PR #51 |
| MSSQL JDBC | 13.4.0.jre11 | 13.4.0.jre11 | [✅] | 2026-04-06 | Current stable |
| PostgreSQL JDBC | 42.7.13 | 42.7.13 | [✅] | 2026-07-19 | Explicit pin in pom.xml |
| JSoup | 1.22.1 | 1.22.1 | [✅] | 2026-01-16 | Updated from 1.21.2 (Item 5.2) |
| Google Cloud Secret Manager | 2.94.0 | 2.94.0 | [✅] | 2026-07-19 | Current stable |
| ByteBuddy | 1.18.8 | 1.18.8 | [✅] | 2026-04-06 | Current stable |
| Cucumber Reporting | 5.10.2 | 5.10.2 | [✅] | 2026-01-16 | Updated from 5.10.1 (Item 5.2) |
<!-- prettier-ignore-end -->

---

## 📦 Node.js Dependencies

> **💡 Checking Latest Stable Versions**: To verify the latest stable versions for npm dependencies, use:
> - `npm outdated` (run in each project directory: cypress, playwright, vibium, frontend)
> - Check [npm registry](https://www.npmjs.com/) for specific packages
> - Review Dependabot PRs for automated update suggestions
> - The "Latest Stable" column indicates the newest stable version available (may differ from Current Version if updates are available)

### Cypress Project (cypress/package.json)

<!-- prettier-ignore-start -->
| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
| -- | -- | -- | -- | -- | -- |
| Cypress | ^15.18.1 | 15.18.1 | [✅] | 2026-07-19 | Current stable |
| TypeScript | ^6.0.3 | 7.0.2 | [⚠️] | 2026-07-19 | Current pin **6.0.3** (latest 6.x); TypeScript **7.0.2** available (major — deferred) |
| @types/node | ^25.9.5 | 26.1.1 | [⚠️] | 2026-07-19 | Pinned on **25.9.5**; Node types **26.x** available (major — deferred) |
| qs (override) | ^6.15.3 | 6.15.3 | [✅] | 2026-07-19 | Security override |
| lodash (override) | ^4.17.24 | 4.18.1 | [✅] | 2026-04-04 | Transitive hardening |
| form-data (override) | ^4.0.6 | 4.0.6 | [✅] | 2026-07-19 | Dependabot #170 |
<!-- prettier-ignore-end -->

### Playwright Project (playwright/package.json)

<!-- prettier-ignore-start -->
| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
| -- | -- | -- | -- | -- | -- |
| Playwright | ^1.61.1 | 1.61.1 | [✅] | 2026-07-19 | Current stable |
| Artillery | ^2.0.33 | 2.0.33 | [✅] | 2026-07-19 | Current stable on npm |
| lodash (override) | ^4.17.24 | 4.18.1 | [✅] | 2026-04-04 | Transitive hardening |
| brace-expansion (override) | ^5.0.5 | 5.0.5 | [✅] | 2026-04-04 | Security override |
| socket.io-parser (override) | ^4.2.6 | 4.2.6 | [✅] | 2026-04-04 | Artillery transitive |
| fast-xml-parser (override) | ^5.10.1 | 5.10.1 | [✅] | 2026-07-19 | DoS hardening |
| js-yaml (override) | ^3.15.0 | 3.15.0 | [✅] | 2026-07-19 | Dependabot #200 |
| form-data (override) | ^4.0.6 | 4.0.6 | [✅] | 2026-07-19 | Dependabot #176 |
| minimatch (overrides) | 9.0.7 / 5.1.8 / 3.1.4 | 9.0.7 | [✅] | 2026-02-13 | Per-parent overrides |
| TypeScript | ^6.0.3 | 7.0.2 | [⚠️] | 2026-07-19 | Current pin **6.0.3** (latest 6.x); TypeScript **7.0.2** available (major — deferred) |
| @types/node | ^25.9.5 | 26.1.1 | [⚠️] | 2026-07-19 | Pinned on **25.9.5**; Node types **26.x** available (major — deferred) |
<!-- prettier-ignore-end -->

### Vibium Project (vibium/package.json)

<!-- prettier-ignore-start -->
| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
| -- | -- | -- | -- | -- | -- |
| Vibium | ^26.5.31 | 26.5.31 | [✅] | 2026-07-19 | CLI + optional platform packages |
| Vitest | ^4.1.10 | 4.1.10 | [✅] | 2026-07-19 | Current stable |
| TypeScript | ^6.0.3 | 7.0.2 | [⚠️] | 2026-07-19 | Current pin **6.0.3** (latest 6.x); TypeScript **7.0.2** available (major — deferred) |
| @types/node | ^25.9.5 | 26.1.1 | [⚠️] | 2026-07-19 | Pinned on **25.9.5**; Node types **26.x** available (major — deferred) |
<!-- prettier-ignore-end -->

### Frontend Project (frontend/package.json)

<!-- prettier-ignore-start -->
| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
| -- | -- | -- | -- | -- | -- |
| React | 19.2.7 | 19.2.7 | [✅] | 2026-07-19 | Current stable |
| Next.js | 16.2.10 | 16.2.10 | [✅] | 2026-07-19 | Current stable |
| @tanstack/react-query | ^5.101.2 | 5.101.2 | [✅] | 2026-07-19 | Current stable |
| eslint-config-next | 16.2.10 | 16.2.10 | [✅] | 2026-07-19 | Matched to Next 16.2.x |
| TypeScript | ^6.0.3 | 7.0.2 | [⚠️] | 2026-07-19 | Current pin **6.0.3** (latest 6.x); TypeScript **7.0.2** available (major — deferred) |
| axios | ^1.18.1 | 1.18.1 | [✅] | 2026-07-19 | Current stable |
| Bootstrap | 5.3.8 | 5.3.8 | [✅] | 2025-12-19 | Updated in PR #51 |
| React Bootstrap | 2.10.10 | 2.10.10 | [✅] | 2025-12-19 | Updated in PR #51 |
| @testing-library/react | 16.3.2 | 16.3.2 | [✅] | 2026-03-13 | Current stable |
| @testing-library/jest-dom | 6.9.1 | 6.9.1 | [✅] | 2025-12-19 | Updated in PR #51 |
| @testing-library/user-event | 14.6.1 | 14.6.1 | [✅] | 2025-12-19 | Updated in PR #51 |
| jsdom | ^29.1.1 | 29.1.1 | [✅] | 2026-07-19 | Vitest 4 compatible |
| ESLint | ^10.7.0 | 10.7.0 | [✅] | 2026-07-19 | Current stable |
| @types/node | ^25.9.5 | 26.1.1 | [⚠️] | 2026-07-19 | Pinned on **25.9.5**; Node types **26.x** available (major — deferred) |
| Vite | ^8.1.5 | 8.1.5 | [✅] | 2026-07-19 | Current stable 8.x |
| @vitejs/plugin-react | ^6.0.3 | 6.0.3 | [✅] | 2026-07-19 | Current stable |
| @vitest/coverage-v8 | ^4.1.10 | 4.1.10 | [✅] | 2026-07-19 | Current stable |
| @vitest/ui | ^4.1.10 | 4.1.10 | [✅] | 2026-07-19 | Current stable |
| vitest | ^4.1.10 | 4.1.10 | [✅] | 2026-07-19 | Current stable |
| ajv (override) | >=6.14.0 | 6.14.0 | [✅] | 2026-02-13 | Security override (ReDoS in `$data`, GHSA-2g4f-4pwh-qvx6); transitive from eslint |
| brace-expansion (override) | ^5.0.5 | 5.0.5 | [✅] | 2026-04-04 | Security override (GHSA-f886-m6hf-6m8v); Dependabot #75–#78 |
| @babel/core (override) | ^7.29.6 | 7.29.7 | [✅] | 2026-07-19 | Dependabot #171 |
| flatted (override) | >=3.4.2 | 3.4.2 | [✅] | 2026-07-19 | Security override |
| postcss (override) | ^8.5.10 | 8.5.12 | [✅] | 2026-07-19 | Security override |
<!-- prettier-ignore-end -->

---

## 🐍 Python Dependencies

> **💡 Checking Latest Stable Versions**: To verify the latest stable versions for Python dependencies, use:
> - `pip list --outdated` (shows packages with available updates)
> - Check [PyPI](https://pypi.org/) for specific packages
> - Review Dependabot PRs for automated update suggestions
> - The "Latest Stable" column indicates the newest stable version available (may differ from Current Version if updates are available)

### Root (pyproject.toml)

<!-- prettier-ignore-start -->
| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
| -- | -- | -- | -- | -- | -- |
| numpy | 2.5.1 | 2.5.1 | [✅] | 2026-07-19 | Pinned in pyproject.toml |
| structlog | 25.5.0 | 25.5.0 | [✅] | 2025-12-19 | Updated in PR #51 |
| mypy | 1.20.0 | 1.20.0 | [✅] | 2026-04-06 | Pinned in pyproject.toml |
| pyright | 1.1.411 | 1.1.411 | [✅] | 2026-07-19 | Pinned in pyproject.toml |
<!-- prettier-ignore-end -->

### Backend (backend/requirements.txt)

<!-- prettier-ignore-start -->
| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
| -- | -- | -- | -- | -- | -- |
| FastAPI | >=0.139.2 | 0.139.2 | [✅] | 2026-07-19 | Minimum floor raised to current stable line |
| Uvicorn | >=0.51.0 | 0.51.0 | [✅] | 2026-07-19 | Compatible with FastAPI 0.139.x |
| Starlette | >=0.46.0 | 1.0.0+ | [✅] | 2026-04-06 | FastAPI declares `starlette>=0.46.0`; pip resolves current stable |
| Pydantic | >=2.13.4 | 2.13.4 | [✅] | 2026-07-19 | Minimum floor raised |
| Pydantic Settings | >=2.14.2 | 2.14.2 | [✅] | 2026-07-19 | Minimum floor raised |
| aiosqlite | >=0.22.1 | 0.22.1 | [✅] | 2026-02-13 | Floor unchanged |
| httpx | >=0.28.1 | 0.28.1 | [✅] | 2025-12-19 | Floor unchanged |
| pytest | >=9.1.1 | 9.1.1 | [✅] | 2026-07-19 | Floor raised |
| pytest-asyncio | >=1.4.0 | 1.4.0 | [✅] | 2026-07-19 | Floor raised |
| pytest-cov | >=7.1.0 | 7.1.0 | [✅] | 2026-07-19 | Floor raised |
| python-dotenv | >=1.2.2 | 1.2.2 | [✅] | 2026-07-19 | Floor raised (backend) |
| black | >=26.5.1 | 26.5.1 | [✅] | 2026-07-19 | Dependabot #40 fix line |
| ruff | >=0.15.22 | 0.15.22 | [✅] | 2026-07-19 | Minimum floor raised |
| urllib3 | >=2.7.0 | 2.7.0+ | [✅] | 2026-07-19 | Security floor |
| Werkzeug | >=3.1.8 | 3.1.8+ | [✅] | 2026-07-19 | Security floor |
<!-- prettier-ignore-end -->

### Performance Testing (requirements.txt)

<!-- prettier-ignore-start -->
| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
| -- | -- | -- | -- | -- | -- |
| Locust | 2.45.0 | 2.45.0 | [✅] | 2026-07-19 | `requests` aligned; CI env-be.yml |
| Requests | 2.34.2 | 2.34.2 | [✅] | 2026-07-19 | Compatible with Locust 2.45.0 |
| python-dotenv | >=1.2.2 | 1.2.2 | [✅] | 2026-07-19 | Floor raised (backend) |
| matplotlib | 3.11.1 | 3.11.1 | [✅] | 2026-07-19 | Pinned |
| pandas | 3.0.3 | 3.0.3 | [✅] | 2026-07-19 | Pinned |
| urllib3 | >=2.7.0 | 2.7.0+ | [✅] | 2026-07-19 | Security floor |
<!-- prettier-ignore-end -->

---

## 🐳 Docker/CI/CD Versions

### Test Image (`Dockerfile`)

<!-- prettier-ignore-start -->
| Component | Current Version | Latest Stable | Status | Last Updated | Notes |
| -- | -- | -- | -- | -- | -- |
| Node.js | 20 (NodeSource) | 20 LTS | [✅] | 2026-07-19 | `setup_20.x` in runtime stage |
| npm (global) | 11.x | 11.x | [✅] | 2026-07-19 | Pin `npm@11` — `npm@latest` (12+) requires Node 22+ |
| Runtime base | eclipse-temurin:21-jre | 21 JRE | [✅] | 2026-07-19 | Multi-stage; build uses maven:3.9.9-eclipse-temurin-21 |
<!-- prettier-ignore-end -->

### Selenium Grid (GitHub Actions Workflow)

<!-- prettier-ignore-start -->
| Component | Current Version | Latest Stable | Status | Last Updated | Notes |
| -- | -- | -- | -- | -- | -- |
| selenium/hub | 4.46.0 | 4.46.0 | [✅] | 2026-07-19 | Centralized via input variable |
| selenium/node-chrome | 4.46.0 | 4.46.0 | [✅] | 2026-07-19 | Centralized via input variable |
| selenium/node-firefox | 4.46.0 | 4.46.0 | [✅] | 2026-07-19 | Centralized via input variable |
| selenium/node-edge | 4.46.0 | 4.46.0 | [✅] | 2026-07-19 | Centralized via input variable |
<!-- prettier-ignore-end -->

**Note**: All Selenium Grid versions are now managed via `selenium_version` input variable in `.github/workflows/env-fe.yml` (default: `4.46.0`)

### Selenium Grid Ports (GitHub Actions Workflow)

<!-- prettier-ignore-start -->
| Port | Current Value | Status | Last Updated | Notes |
| -- | -- | -- | -- | -- |
| Hub Port | 4444 | [✅] | 2025-12-20 | Centralized via `se_hub_port` input |
| Event Bus Publish | 4442 | [✅] | 2025-12-20 | Centralized via `se_pub_port` input |
| Event Bus Subscribe | 4443 | [✅] | 2025-12-20 | Centralized via `se_sub_port` input |
<!-- prettier-ignore-end -->

**Note**: All Selenium Grid ports are now managed via input variables in `.github/workflows/env-fe.yml`

---

## 🔒 Security Vulnerabilities

### Current Status (as of 2026-07-19)

Vulnerability counts change as Dependabot rescans and PRs are merged. Check the live dashboard for current numbers.

**Dependabot Alerts**: https://github.com/CScharer/full-stack-qa/security/dependabot

After the **2026-07-19** security refresh (PRs #282–#283), open alerts should be limited to **Hibernate #93** (CVE-2026-0603; no patched 5.x release). Closed in that pass: Jackson 2.x databind (#201–#204), js-yaml (#200), form-data (#170, #176), @babel/core (#171). Earlier history still relevant: Jackson 3 (#78), Vite (#80, #82, #84), minimatch #35–#37, qs #13, fast-xml-parser #11, ajv, logback-core, lodash / brace-expansion / socket.io-parser overrides, black #40.

### Update Strategy

1. **Immediate**: Review and apply Critical security patches
2. **High Priority**: Review and apply High severity patches
3. **Medium Priority**: Review Moderate vulnerabilities (may be acceptable)
4. **Low Priority**: Review Low vulnerabilities (likely acceptable)

**Note**: Many vulnerabilities may be resolved by applying pending dependency updates listed above.

### Fixing Transitive Dependency Vulnerabilities (npm)

When a vulnerability exists in a transitive dependency (not directly in your `package.json`), use npm's `overrides` feature to force a patched version:

**Example 1**: Fixing `qs` vulnerability in Cypress (Dependabot Alert #1 / #13)
```json
{
  "devDependencies": {
    "qs": "^6.15.0"
  },
  "overrides": {
    "qs": "^6.15.0"
  }
}
```

**Example 2**: Fixing `lodash` in Playwright and Cypress (transitive from Artillery / Cypress)
```json
{
  "overrides": {
    "lodash": "^4.17.24"
  }
}
```
Use **^4.17.24** (or higher patched 4.x): **4.17.23** was still flagged; lockfiles resolve to **4.18.1** as of 2026-04-04.

**Example 3**: Fixing `brace-expansion` (eslint / minimatch) and `socket.io-parser` (Artillery)
```json
{
  "overrides": {
    "brace-expansion": "^5.0.5",
    "socket.io-parser": "^4.2.6"
  }
}
```
Apply `brace-expansion` in **frontend** and **playwright**; add `socket.io-parser` in **playwright** only (where Artillery pulls it). Regenerate lockfiles with `npm install` or `npm install --package-lock-only`, then confirm `npm audit` is clean.

The `overrides` section forces all instances of the package (including transitive dependencies) to use the patched version. After adding the override:
1. Run `npm install` to update `package-lock.json`
2. Verify with `npm audit` - should show 0 vulnerabilities for that package
3. Verify with `npm list <package-name>` - should show patched version throughout dependency tree

**Reference**: See Update History section for resolved vulnerabilities.

---

## 📋 Update History

### 2026-07-19 (safe stable bump + mobile tearDown fix)
- **Maven**: Selenium/Selenide **4.46.0 / 7.17.0**; Jackson 3 **3.2.1**; Netty **4.2.16.Final**; Log4j **2.26.1**; Logback **1.5.38**; Cucumber **7.34.4**; REST Assured **6.0.1**; Gatling **3.15.1**; JUnit **6.1.2**; Allure Java **2.35.3**; Checkstyle **13.8.0**; SpotBugs **4.10.3** (+ plugin **4.10.3.0**); OpenTelemetry **1.64.0**; PostgreSQL JDBC **42.7.13**; htmlunit3-driver **4.46.0**; Surefire **3.5.6**; Compiler plugin **3.15.0**; Secret Manager **2.94.0**; Appium **10.1.1**. Hibernate remains **5.6.15.Final**. Jackson 2.x remains **2.21.5** (2.22.1 available).
- **npm**: Next/React **16.2.10 / 19.2.7**; Vite **8.1.5**; Vitest **4.1.10**; axios **1.18.1**; Cypress **15.18.1**; Playwright **1.61.1**; Vibium **26.5.31**; Artillery **2.0.33**; related overrides (`qs`, `fast-xml-parser`).
- **Python**: FastAPI/uvicorn/ruff/black floors raised; Locust **2.45.0**, requests **2.34.2**.
- **CI**: `env-fe.yml` default `selenium_version` **4.46.0**.
- **Tests**: `MobileBrowserTests` uses `ThreadLocal` WebDriver + defensive `quit()` so parallel Surefire methods cannot fail tearDown on a shared/dead Grid session.

- **Follow-up safe bump (same day)**: TypeScript **6.0.3** + `@types/node` **25.9.5** across all Node projects; `@vitejs/plugin-react` **6.0.3**, `jsdom` **29.1.1**, `tsx` **4.23.1**; Jackson 3 **3.2.1**; Surefire **3.5.6**; Compiler plugin **3.15.0**; Secret Manager **2.94.0**; Appium **10.1.1**; Python floors (pydantic/pytest/urllib3/werkzeug); matplotlib **3.11.1**, pandas **3.0.3**; pyproject numpy **2.5.1**, pyright **1.1.411**.


### 2026-07-19 (security / dependency refresh)
- **Maven**: Jackson 3 **3.1.1 → 3.1.4**; Jackson 2.x **2.21.2 → 2.21.5** (+ annotations **2.21**, explicit jackson-databind 2.x); Netty **4.2.12.Final → 4.2.15.Final**; Log4j **2.25.3 → 2.25.4**; Logback **1.5.32 → 1.5.35**; PostgreSQL JDBC **42.7.10 → 42.7.11**. Hibernate remains **5.6.15.Final** (Dependabot #93 pending patched release).
- **npm**: Next **16.2.2 → 16.2.6**; Vite **8.0.5 → 8.0.16**; axios **1.14 → 1.16**; overrides added/raised for **form-data ^4.0.6**, **js-yaml ^3.15.0**, **@babel/core ^7.29.6**, **fast-xml-parser ^5.7.0**, **qs ^6.15.2** (PRs #282–#283).
- **Docker**: Global npm pinned to **npm@11** on Node 20 (do not use `npm@latest` / npm 12+ without Node 22+). Fixes CI image build `EBADENGINE` failure.
- **Docs**: Refreshed VERSION_TRACKING tables, security status, Docker guides, README pins, and related process docs.

### 2026-04-06 (comprehensive stable bump)
- **Scope**: Raised Maven, npm, Python, and CI defaults to current **stable** releases. Skipped Maven Central **pre-releases** (alphas, betas, milestones, RCs). **DBUnit** intentionally left at **2.8.0** (3.x is a breaking migration for existing tests). Carries forward **main** security/doc work: Jackson **3.1.1** (Dependabot #78), Vite **^8.0.5** / lock **8.0.5** (#80, #82, #84), and README `config/` / `xml/` plus `scripts/quality/validate-dependency-versions.sh` references.
- **Maven (`pom.xml`)**: Selenium **4.41.0**, Cucumber **7.34.3**, Netty BOM line **4.2.12.Final**, Jackson **3.1.1** + jackson-core **2.21.2**, Selenide **7.15.1**, Allure Java **2.33.0**, Gatling **3.15.0** + plugin **4.21.5**, Surefire **3.5.5**, Spotless **2.46.1**, PMD plugin **3.28.0**, SpotBugs plugin **4.9.8.3**, JMeter plugin **3.8.0**, Scala plugin **4.9.10**, Mockito **5.23.0**, Appium **10.1.0**, WebDriverManager **6.3.4**, Checkstyle **13.4.0**, Secret Manager **2.88.0**, MSSQL JDBC **13.4.0.jre11**, PostgreSQL JDBC **42.7.10**, SQLite JDBC **3.51.3.0**, htmlunit3-driver **4.41.0**, PDFBox **3.0.7**, Logback **1.5.32**, Allure Maven plugin **2.17.0**, plus additional patch bumps (ByteBuddy, Commons Logging, Joda, Objenesis, Rhino, etc.).
- **npm**: Frontend **Next 16.2.2**, **TypeScript 6.0.2**, **Vitest/Vite toolchain 4.1.2 / 8.0.5**, **jsdom 29**, **ESLint 10.2**, **axios 1.14**, **@tanstack/react-query 5.96.x**; Cypress **15.13.x**; Playwright **1.59.x**; Vibium **26.3.18**; engines **Node >=20**. Regenerated all `package-lock.json` files (`npm install`).
- **Python**: `backend/requirements.txt` floors for **FastAPI 0.135.x**, **uvicorn 0.44+**, **pydantic-settings 2.13+**, **ruff 0.15.9+**; root `requirements.txt` **Locust 2.43.4**, **requests 2.33.1**, **pandas 3.0.2**; `pyproject.toml` **numpy 2.4.4**, **mypy 1.20.0**, **pyright 1.1.408**; `data/core/tests/requirements.txt` pytest floors; `.github/workflows/env-be.yml` pip install aligned with Locust/requests.
- **CI**: `.github/workflows/env-fe.yml` default `selenium_version` **4.41.0** (matches `pom.xml`).
- **Docs**: Root `README.md` badges and dependency table; this file’s tables and review metadata; infrastructure/testing guides that reference the Grid default were updated to **4.41.0** where they document the live default.
- **Verify**: `./mvnw -DskipTests compile` and `frontend` `npm test -- --run` (98 tests) succeeded locally after the bump.

### 2026-04-04
- **Security Fix - npm (Dependabot #75–#78)**: Cleared `npm audit` findings across **frontend**, **cypress**, and **playwright** using `overrides` and updated lockfiles.
  - **brace-expansion** `^5.0.5` → resolved **5.0.5** (GHSA-f886-m6hf-6m8v, moderate) in `frontend/package.json` and `playwright/package.json`.
  - **lodash** `^4.17.24` → resolved **4.18.1** in `cypress/package.json` and `playwright/package.json` (replaces vulnerable **4.17.23** from Cypress and older transitive lines; GHSA-r5fr-rjxr-66jc, GHSA-f23m-r3pf-42rh).
  - **socket.io-parser** `^4.2.6` → resolved **4.2.6** in `playwright/package.json` only (GHSA-677m-j7p3-52f9, high; Artillery transitive).
- **Documentation**: Refreshed Node.js override rows, security section, worked examples, and review dates in this file.

### 2026-03-13
- **Security Fix - black (Python backend)**: Addressed Dependabot #40 (high)
  - `black` 25.12.0 → 26.3.1 in `backend/requirements.txt`
  - Fixes arbitrary cache file writes via unsanitized `--python-cell-magics` filenames
  - Implemented via PR #212 (`chore/update-black-26.3.1`)
- **Node.js Optional Updates → Current Stable**:
  - **Frontend**: `@testing-library/react` 16.3.1 → 16.3.2, `@types/node` 25.3.3 → 25.5.0, `@types/react` 19 → 19.2.14, `@vitejs/plugin-react` 5.1.2 → 6.0.0, `vite` added at 8.0.0, `@vitest/coverage-v8` 4.0.16 → 4.1.0, `@vitest/ui` 4.0.16 → 4.1.0, `eslint` 9.39.2 → 10.0.3, `jsdom` 27.4.0 → 28.1.0, `vitest` 4.0.16 → 4.1.0.
  - **Cypress**: `@types/node` 25.3.3 → 25.5.0.
  - **Playwright**: `@types/node` 25.3.3 → 25.5.0.
  - **Vibium**: `vibium` 0.1.2 → 26.3.11, `@vibium/*` 0.1.2 → 26.3.11, `vitest` 4.0.16 → 4.1.0, `@types/node` 25.3.3 → 25.5.0.
- **Documentation**: Updated Python backend and Node.js sections and review dates in `VERSION_TRACKING.md`

### 2026-02-13
- **Security Fix - Jackson (Maven)**: Addressed Dependabot #26, #27 (high)
  - **Jackson 3**: `jackson.version` 3.0.3 → 3.1.0 in `pom.xml` (tools.jackson.core jackson-core DoS)
  - **Jackson Core 2.x**: Added explicit `com.fasterxml.jackson.core:jackson-core:2.21.1` in `pom.xml` to override vulnerable 2.20.0 from cucumber-reporting (Dependabot #27)
- **Security Fix - minimatch (npm, playwright)**: Addressed Dependabot #35, #36, #37 (high) - ReDoS in matchOne()
  - Added npm `overrides` in `playwright/package.json`: minimatch 9.0.7, 5.1.8 (filelist), 3.1.4 (glob, matcher-collection)
- **Version check – VERSION_TRACKING.md**: Comprehensive review and doc updates
  - **Last Review / Next Review**: Set to 2026-02-13 / 2026-03-01
  - **REST Assured note**: Jackson 3.0.0 → 3.1.0
  - **Cypress**: Added qs ^6.14.2 row (Dependabot #13)
  - **Playwright**: Added fast-xml-parser and minimatch override rows (Dependabot #11, #35–#37)
  - **Frontend**: Next.js 16.1.1 → 16.1.5 (per package.json)
  - **Backend**: Pydantic Settings 2.0.3 → >=2.12.0, aiosqlite 0.21.0 → >=0.22.1, ruff 0.14.9 → >=0.14.10; FastAPI note (>=0.124.4)
  - **Security section**: Stale counts removed; qs example updated to ^6.14.2; reference to recent fixes added
  - **Document Maintenance**: Last Updated 2026-02-13, Next Review 2026-03-01
- **Stable vs. latest**: Added subsection clarifying that all dependencies are on stable builds; some have optional patch/minor updates available. Added "Known available updates" table (Frontend: next 16.1.6, react 19.2.4; Cypress: cypress 15.11.0, qs 6.15.0, @types/node 25.3.3; Playwright: @playwright/test 1.58.2, artillery 2.0.30, @types/node 25.3.3). Updated Node.js tables: "Latest Stable" and status [⚠️] where updates exist; notes indicate updates are optional.
- **Bump all Node.js deps to current stable**: Applied optional updates across frontend, cypress, playwright, vibium. Frontend: next 16.1.5→16.1.6, react/react-dom 19.2.3→19.2.4, @tanstack/react-query 5.90.16→5.90.21, axios 1.13.5→1.13.6, eslint-config-next 16.1.1→16.1.6, @types/node 25→25.3.3. Cypress: cypress 15.8.1→15.11.0, qs 6.14.2→6.15.0, @types/node 25→25.3.3. Playwright: @playwright/test 1.57.0→1.58.2, artillery 2.0.0→2.0.30, @types/node 25→25.3.3. Vibium: @types/node 25→25.3.3. Lockfiles updated; VERSION_TRACKING tables set to [✅] and "Bumped to current stable".
- **Security Fix - ajv (Frontend)**: Addressed 1 moderate (ReDoS in `$data` option, GHSA-2g4f-4pwh-qvx6). Ran `npm audit fix` (ajv 6.12.6→6.14.0 from eslint transitive) and added `overrides: { "ajv": ">=6.14.0" }` in `frontend/package.json` to keep the fix durable. Frontend `npm audit` now reports 0 vulnerabilities.

### 2025-12-20
- **Selenium Grid**: Centralized version (4.39.0, updated to 4.40.0 on 2026-01-25) and ports via workflow input variables
- **Document Created**: Initial version tracking document

### 2025-12-30
- **Version Verification**: Completed comprehensive dependency verification
- **Cypress**: 15.2.0 → 15.8.1 (current stable)
- **TypeScript**: 5.9 → 5.9.3 (current stable) - All projects
- **React**: 19.2.1 → 19.2.3 (current stable)
- **Next.js**: 16.0.10 → 16.1.1 (current stable)
- **@tanstack/react-query**: 5.90.12 → 5.90.16 (current stable)
- **eslint-config-next**: 16.1.0 → 16.1.1 (current stable)
- **jsdom**: 27.3.0 → 27.4.0 (current stable)
- **Jackson Databind**: 3.0.0 → 3.0.3 (current stable)
- **MSSQL JDBC**: 13.2.0.jre11 → 13.2.1.jre11 (current stable)
- **Maven Compiler Plugin**: 3.13.0 → 3.14.1 (current stable, updated 2026-01-24)
- **Outdated Dependencies Document**: Created `docs/work/20251230_OUTDATED_DEPENDENCIES.md` with 10 outdated dependencies identified
- **Security Fix - qs (npm)**: Fixed Dependabot alert #1 (High severity) by adding `qs@^6.14.1` as direct dependency and using npm `overrides` to force patched version throughout dependency tree. Vulnerability: ArrayLimit bypass in bracket notation allows DoS via memory exhaustion (GHSA-6rw7-vpxm-498p). Fixed in `cypress/package.json`.
- **Dependency Fix - requests (Python)**: Adjusted `requests` from 2.32.5 to 2.32.4 in `requirements.txt` and `.github/workflows/env-be.yml` to resolve dependency conflict with Locust 2.42.6 (requires `requests<2.32.5`). This fixes the dependency submission workflow failure.

### 2026-01-25
- **Version Check**: Comprehensive dependency version verification completed
- **Selenium**: Updated from 4.39.0 → 4.40.0 (released 2026-01-18)
  - Updated `pom.xml` selenium.version property
  - Updated `.github/workflows/env-fe.yml` default selenium_version input
  - Aligned client and server versions
  - Status changed from [⚠️] to [✅] - Current stable version
- **All Other Dependencies**: Verified current versions match latest stable releases

### 2026-01-24
- **Maven Compiler Plugin**: 3.13.0 → 3.14.1 (Current stable version)
- **Security Fix - logback-core (Maven)**: Added explicit dependency override for `ch.qos.logback:logback-core` version 1.5.25 to override vulnerable 1.5.20 from Gatling transitive dependency. Vulnerability: ACE vulnerability in configuration file processing (CVE). Fixed in `pom.xml` via PR #190.
- **Security Fix - lodash (npm)**: Added npm `overrides` to force lodash >=4.17.23 in `playwright/package.json` to override vulnerable 4.17.21 from artillery transitive dependency. Vulnerability: Prototype Pollution in `_.unset` and `_.omit` functions (CVE). Fixed via PR #191.

### 2025-12-19
- **REST Assured**: 5.5.6 → 6.0.0 (PR #51)
- **Cypress**: 13.7.0 → 15.2.0 (PR #51)
- **Selenide**: 7.12.3 → 7.13.0 (PR #51)
- **Maven**: 3.9.9 → 3.9.11 (PR #51)
- **Maven Compiler Plugin**: 3.13.1 → 3.13.0 (updated to 3.14.1 on 2026-01-24)
- **Maven Surefire Plugin**: 3.5.2 → 3.5.4 (PR #51)
- **Scala**: 2.13.17 → 2.13.18 (PR #51)
- **Apache POI**: 5.2.3 → 5.5.1 (PR #51)
- **MSSQL JDBC**: 12.8.2.jre11 → 13.2.0.jre11 (PR #51)
- **TypeScript**: 5.3.3 → 5.9 (PR #51) - All projects (cypress, playwright, vibium, frontend)
- **@types/node**: 20.x → 25.0.0 (PR #51) - All projects
- **Frontend Dependencies**: Bootstrap 5.3.8, React Bootstrap 2.10.10, @testing-library/* updates, ESLint 9.39.2, jsdom 27.3.0 (PR #51)
- **Python Backend**: FastAPI 0.125.0, Uvicorn 0.38.0, Starlette 0.50.0, Pydantic 2.12.5, aiosqlite 0.21.0, httpx 0.28.1, python-dotenv 1.2.1, black 25.12.0, ruff 0.14.9 (PR #51)
- **Python Root**: numpy 2.3.5, structlog 25.5.0, pyright 1.1.407 (PR #51)
- **Python Performance**: Locust 2.42.6, Requests 2.32.4 (adjusted from 2.32.5 for Locust compatibility), matplotlib 3.10.8, pandas 2.3.3 (PR #51, adjusted 2025-12-30)
- **pytest**: >=7.4.0 → 9.0.2 (PR #51)
- **pytest-asyncio**: >=0.21.0 → 1.3.0 (PR #51)
- **pytest-cov**: >=4.1.0 → 7.0.0 (PR #51)
- **Log4j 2**: 2.22.0 → 2.25.3 (Dependabot PR #52)
- **Vitest**: 1.1.0 → 4.0.16 (Dependabot PR #48)
- **Checkstyle Tool**: 12.3.0 → 13.0.0 (Item 5.2, 2026-01-16)
- **PostgreSQL JDBC**: 42.7.8 → 42.7.9 (Item 5.2, 2026-01-16)
- **JSoup**: 1.21.2 → 1.22.1 (Item 5.2, 2026-01-16)
- **Google Cloud Secret Manager**: 2.81.0 → 2.82.0 (Item 5.2, 2026-01-16)
- **ByteBuddy**: 1.18.3 → 1.18.4 (Item 5.2, 2026-01-16)
- **Cucumber Reporting**: 5.10.1 → 5.10.2 (Item 5.2, 2026-01-16)

---

## 🔍 How to Use This Document

### Monthly Review Process

1. **Check for Updates**:
   ```bash
   # Java/Maven
   ./mvnw versions:display-dependency-updates
   
   # Node.js (for each project)
   cd cypress && npm outdated
   cd playwright && npm outdated
   cd vibium && npm outdated
   cd frontend && npm outdated
   
   # Python
   pip list --outdated
   ```

2. **Update This Document**:
   - Update "Latest Stable" column with new versions
   - Change status from `[✅]` to `[⚠️]` if update available
   - Add notes about breaking changes or requirements
   - Update "Last Updated" date

3. **Plan Updates**:
   - Prioritize security patches
   - Group non-breaking updates (patch/minor)
   - Schedule major version updates separately
   - Document breaking changes

4. **Apply Updates**:
   - Create feature branch
   - Apply updates incrementally
   - Test locally
   - Update this document with "Last Updated" dates
   - Commit and create PR

### Quarterly Major Update Review

1. Review all `[⚠️]` items
2. Research breaking changes
3. Create update plan
4. Schedule update window
5. Apply and test

---

## 📝 Notes

- **Selenium Version Alignment**: Client (pom.xml) and Server (CI/CD) versions must match. Currently aligned at 4.46.0.
  - **Validation**: Currently validated via scheduled workflow and manual script execution
  - **✅ Implemented**: Pre-push hook validation catches mismatches before code is pushed (see [Selenium Grid Configuration Guide](../guides/infrastructure/SELENIUM_GRID.md))
- **TypeScript Updates**: Consider updating all projects together for consistency.
- **Python Major Versions**: numpy 2.x has breaking changes - review carefully before updating.
- **Security Patches**: Apply immediately when available.
- **Breaking Changes**: Always review changelogs and migration guides before major version updates.
- **Docker Compose Versions**: Selenium Grid Docker image versions should match `pom.xml` version. Pre-push validation will check this automatically.

---

## 🔗 Related Documents

- Dependency Version Audit (archived) - Comprehensive audit
- Pending Dependency Updates Summary (archived) - Update status
- Next Steps After PR #53 (archived) - Work plan
- [Pre-Pipeline Validation Checklist](./PRE_PIPELINE_VALIDATION.md) - Validation process

---

## 📅 Document Maintenance

- **Created**: 2025-12-20
- **Last Updated**: 2026-07-19
- **Next Review**: 2026-08-01 (recommended)
- **Maintainer**: Development Team

**Remember**: This is a living document. Update it regularly to keep version information current!
