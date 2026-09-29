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
- **Last Review**: 2026-09-29 (Dependabot #250/#259/#268–#275 security fixes; frontend/playwright stable bumps; legacy Grid 1.x JARs removed from `pom.xml`)
- **Latest Stable Versions Check**: 2026-09-29 (registry check after security refresh)
- **Next Review**: 2026-10-01 (recommended)

### Stable vs. latest

All tracked dependencies are on **stable builds** and suitable for production. Not every dependency is at the **absolute latest** stable release; some have newer patch or minor versions available. That is intentional: the project prioritizes stability and security fixes, and applies non-critical updates during scheduled reviews or via Dependabot PRs.

- **Stable** = Current versions are supported, non-EOL, and (where applicable) security-patched.
- **Latest** = Newest stable release on the registry; may be one or more patch/minor versions ahead.

When in doubt, run `npm outdated`, `./mvnw versions:display-dependency-updates`, or `pip list --outdated` in the relevant project and compare with the "Latest Stable" column and the list below.



### Known available updates

As of **2026-09-29** (after comprehensive stable refresh):

<!-- prettier-ignore-start -->
| Dependency | Current | Latest available | Notes |
| -- | -- | -- | -- |
| TypeScript | 6.0.3 | 7.0.2 | TS 7 deferred — Next 16 / eslint-config-next |
| Vitest | 4.1.11 | 5.0.2 | Vitest **5.x** deferred until toolchain validated |
| Cypress | ^15.21.1 | 15.21.1 | **16.x** major (16.1.1) deferred |
| @testing-library/jest-dom | 6.9.1 | 7.0.1 | Major deferred |
| Hibernate | 6.6.54.Final | 7.4.5.Final | Hibernate **7.x** deferred |
| Artillery `js-yaml` | 5.2.2 (pin) | 5.4.2 | Exact pin for CLI; floating breaks ESM import |
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
| Selenium | 4.49.0 | 4.49.0 | [✅] | 2026-09-29 | Aligned with `env-fe.yml` Grid images; legacy Grid 1.x JARs removed |
| Selenide | 7.18.2 | 7.18.2 | [✅] | 2026-09-29 | Current stable |
| TestNG | 7.11.0 | 7.11.0 | [✅] | 2025-12-25 | Current |
| JUnit | 6.1.2 | 6.1.2 | [✅] | 2026-07-19 | Current stable |
| Cucumber | 7.34.4 | 7.34.4 | [✅] | 2026-07-19 | Current stable |
| REST Assured | 6.0.1 | 6.0.1 | [✅] | 2026-07-19 | Requires Java 17+; Jackson **3.2.2** (`tools.jackson.core`) |
| Allure3 CLI | 3.0.0 | 3.0.0 | [✅] | 2025-12-30 | Active - Allure3 CLI in use (TypeScript-based, npm install) |
| Allure2 Java | 2.35.3 | 2.35.3 | [✅] | 2026-07-19 | allure-testng, allure-junit5, allure-java-commons |
<!-- prettier-ignore-end -->

### Build & Tools

<!-- prettier-ignore-start -->
| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
| -- | -- | -- | -- | -- | -- |
| Maven | 3.9.11 | 3.9.11 | [✅] | 2025-12-19 | Updated in PR #51 |
| Java | 21 | 21 (LTS) | [✅] | 2025-12-25 | Current LTS version |
| Maven Compiler Plugin | 3.15.0 | 3.15.0 | [✅] | 2026-07-19 | Current stable version |
| Maven Surefire Plugin | 3.5.6 | 3.5.6 | [✅] | 2026-07-19 | Current stable |
| Maven Checkstyle Plugin | 3.6.0 | 3.6.0 | [✅] | 2025-12-25 | Current |
| Checkstyle Tool | 13.8.0 | 13.8.0 | [✅] | 2026-07-19 | Current stable |
| SpotBugs | 4.10.3 | 4.10.3 | [✅] | 2026-07-19 | Current stable |
| PMD | 3.28.0 | 3.28.0 | [✅] | 2026-07-19 | maven-pmd-plugin |
<!-- prettier-ignore-end -->

### Performance Testing

<!-- prettier-ignore-start -->
| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
| -- | -- | -- | -- | -- | -- |
| Gatling | 3.15.1 | 3.15.1 | [✅] | 2026-07-19 | With gatling-maven-plugin 4.21.5 |
| JMeter | 5.6.3 | 5.6.3 | [✅] | 2025-12-25 | Current |
| Scala | 2.13.18 | 2.13.18 | [✅] | 2025-12-19 | Updated in PR #51 - For Gatling |
<!-- prettier-ignore-end -->

### Utilities & Libraries

<!-- prettier-ignore-start -->
| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
| -- | -- | -- | -- | -- | -- |
| WebDriverManager | 6.4.0 | 6.4.0 | [✅] | 2026-09-29 | Current stable |
| Log4j 2 | 2.26.1 | 2.26.1 | [✅] | 2026-07-19 | `log4j2.version` (2.x Active Maintenance) |
| Logback Core | 1.5.38 | 1.5.38 | [✅] | 2026-07-19 | Overrides Gatling transitive line |
| Jackson Databind (3.x) | 3.2.2 | 3.2.2 | [✅] | 2026-09-29 | `jackson.version`; tools.jackson.core jackson-databind |
| Jackson Core (2.x) | 2.22.2 | 2.22.2 | [✅] | 2026-09-29 | `jackson2.version` |
| Jackson Databind (2.x) | 2.22.2 | 2.22.2 | [✅] | 2026-09-29 | Explicit com.fasterxml pin |
| Jackson Annotations | 2.22 | 2.22 | [✅] | 2026-07-19 | 2.x annotations alongside Jackson 3 databind (REST Assured 6) |
| Netty BOM | 4.2.18.Final | 4.2.18.Final | [✅] | 2026-09-29 | `netty-codec-http.version` / netty-bom import |
| Hibernate | 6.6.54.Final | 6.6.54.Final | [✅] | 2026-07-19 | `org.hibernate.orm:hibernate-core`; Jakarta Persistence 3.1; clears Dependabot #93 (5.x-only CVE) |
| Apache POI | 5.5.1 | 5.5.1 | [✅] | 2025-12-19 | Updated in PR #51 |
| MSSQL JDBC | 13.4.0.jre11 | 13.4.0.jre11 | [✅] | 2026-04-06 | Current stable |
| PostgreSQL JDBC | 42.7.13 | 42.7.13 | [✅] | 2026-07-19 | Explicit pin in pom.xml |
| JSoup | 1.23.2 | 1.23.2 | [✅] | 2026-09-29 | Current stable |
| Apache HttpClient 5 | 5.6.3 | 5.6.3 | [✅] | 2026-08-13 | `httpclient5.version`; WebDriverManager transitive pin |
| Apache HttpCore 5 | 5.4.3 | 5.4.3 | [✅] | 2026-08-13 | `httpcore5.version`; Dependabot #241 / CVE-2026-54399 |
| HtmlUnit | 4.21.0 | 4.21.0 | [✅] | 2026-08-08 | `htmlunit.version`; driver `htmlunit3-driver` **4.48.0** |
| Appium Java Client | 10.1.1 | 10.1.1 | [✅] | 2026-07-19 | `appium.version` |
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
| Cypress | ^15.21.1 | 15.21.1 | [✅] | 2026-09-29 | Current **15.x**; **16.x** major deferred |
| TypeScript | ^6.0.3 | 7.0.2 | [⚠️] | 2026-07-19 | TS 7 deferred — Next 16 type-check + eslint-config-next require `<6.1.0` |
| @types/node | ^26.6.3 | 26.6.3 | [✅] | 2026-09-29 | Current stable |
| qs (override) | 6.16.0 | 6.16.0 | [✅] | 2026-09-29 | Security override (GHSA-4mjr-xmp4-gh2g / GHSA-x5fp-wj9c-mxmx) |
| lodash (override) | ^4.17.24 | 4.18.1 | [✅] | 2026-04-04 | Transitive hardening |
| form-data (override) | ^4.0.6 | 4.0.6 | [✅] | 2026-07-19 | Dependabot #170 |
| uuid (override) | ^14.0.0 | 14.0.0 | [✅] | 2026-07-19 | Transitive hardening |
| yauzl (override) | >=3.2.1 | 3.4.0 | [✅] | 2026-07-19 | Security override (lock 3.4.0) |
<!-- prettier-ignore-end -->

### Playwright Project (playwright/package.json)

<!-- prettier-ignore-start -->
| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
| -- | -- | -- | -- | -- | -- |
| Playwright | ^1.63.0 | 1.63.0 | [✅] | 2026-09-29 | Bumped for current stable; lock resolves **1.63.x** |
| Artillery | ^2.0.34 | 2.0.34 | [✅] | 2026-09-29 | Current stable on npm |
| lodash (override) | ^4.17.24 | 4.18.1 | [✅] | 2026-04-04 | Transitive hardening |
| csv-parse (override) | ^7.0.3 | 7.0.3 | [✅] | 2026-09-29 | Clears Dependabot **#250** / CVE-2026-85063 (Artillery still declares ^4.x) |
| brace-expansion (override) | ^5.0.12 | 5.0.12 | [✅] | 2026-09-29 | Security override (GHSA-rgw5-rvv9-x895; was ^5.0.8) |
| socket.io-parser (override) | ^4.2.6 | 4.2.7 | [✅] | 2026-04-04 | Artillery transitive (lock 4.2.7) |
| fast-xml-parser (override) | ^5.10.1 | 5.10.1 | [✅] | 2026-07-19 | DoS hardening |
| js-yaml (override) | 5.2.2 | 5.4.2 | [✅] | 2026-09-29 | Exact pin **5.2.2** for Artillery ESM import (do not float to 5.4.x) |
| nanoid (override) | ^3.3.17 | 3.3.18 | [✅] | 2026-08-08 | Security override (Dependabot #238 / CVE-2026-67213; lock 3.3.18) |
| postcss (override) | ^8.5.18 | 8.5.28 | [✅] | 2026-09-29 | Security override (aligned with frontend floor) |
| uuid (override) | ^14.0.0 | 14.0.0 | [✅] | 2026-07-19 | Transitive hardening |
| form-data (override) | ^4.0.6 | 4.0.6 | [✅] | 2026-07-19 | Dependabot #176 |
| minimatch (overrides) | 9.0.7 / 5.1.8 / 3.1.4 | 9.0.7 | [✅] | 2026-02-13 | Per-parent overrides |
| TypeScript | ^6.0.3 | 7.0.2 | [⚠️] | 2026-07-19 | TS 7 deferred — Next 16 type-check + eslint-config-next require `<6.1.0` |
| @types/node | ^26.6.3 | 26.6.3 | [✅] | 2026-09-29 | Current stable |
<!-- prettier-ignore-end -->

### Vibium Project (vibium/package.json)

<!-- prettier-ignore-start -->
| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
| -- | -- | -- | -- | -- | -- |
| Vibium | ^26.8.21 | 26.8.21 | [✅] | 2026-09-29 | Current stable |
| Vitest | ^4.1.10 | 4.1.11 | [✅] | 2026-09-29 | Latest Vitest **4.x** |
| TypeScript | ^6.0.3 | 7.0.2 | [⚠️] | 2026-07-19 | TS 7 deferred — Next 16 type-check + eslint-config-next require `<6.1.0` |
| @types/node | ^26.6.3 | 26.6.3 | [✅] | 2026-09-29 | Current stable |
| nanoid (override) | ^3.3.17 | 3.3.18 | [✅] | 2026-08-08 | Security override (Dependabot #238 / CVE-2026-67213; lock 3.3.18) |
| postcss (override) | ^8.5.28 | 8.5.28 | [✅] | 2026-09-29 | Security override |
| brace-expansion (override) | ^5.0.12 | 5.0.12 | [✅] | 2026-09-29 | Security override |
<!-- prettier-ignore-end -->

### Frontend Project (frontend/package.json)

<!-- prettier-ignore-start -->
| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
| -- | -- | -- | -- | -- | -- |
| React | 19.3.0 | 19.3.0 | [✅] | 2026-09-29 | Pinned with Next **16.3.7** |
| Next.js | 16.3.7 | 16.3.7 | [✅] | 2026-09-29 | Current stable 16.3.x |
| @tanstack/react-query | ^5.104.0 | 5.104.0 | [✅] | 2026-09-29 | Current stable |
| eslint-config-next | 16.3.7 | 16.3.7 | [✅] | 2026-09-29 | Matched to Next **16.3.7** |
| axios | ^1.20.0 | 1.20.0 | [✅] | 2026-09-29 | Current stable |
| TypeScript | ^6.0.3 | 7.0.2 | [⚠️] | 2026-07-19 | TS 7 deferred — Next 16 type-check + eslint-config-next require `<6.1.0` |
| Bootstrap | 5.3.8 | 5.3.8 | [✅] | 2025-12-19 | Updated in PR #51 |
| React Bootstrap | 2.10.10 | 2.10.10 | [✅] | 2025-12-19 | Updated in PR #51 |
| @testing-library/react | 16.3.2 | 16.3.2 | [✅] | 2026-03-13 | Current stable |
| @testing-library/jest-dom | 6.9.1 | 6.9.1 | [✅] | 2025-12-19 | Updated in PR #51 |
| @testing-library/user-event | 14.6.1 | 14.6.1 | [✅] | 2025-12-19 | Updated in PR #51 |
| jsdom | ^29.1.1 | 29.1.1 | [✅] | 2026-07-19 | Vitest 4 compatible |
| ESLint | ^10.11.0 | 10.11.0 | [✅] | 2026-09-29 | Current stable |
| @types/node | ^26.6.3 | 26.6.3 | [✅] | 2026-09-29 | Current stable across Node projects |
| Vite | ^8.3.1 | 8.3.1 | [✅] | 2026-09-29 | Current stable 8.x |
| @vitejs/plugin-react | ^6.0.3 | 6.0.3 | [✅] | 2026-07-19 | Current stable |
| @vitest/coverage-v8 | ^4.1.10 | 4.1.11 | [✅] | 2026-09-29 | Aligned with vitest **4.1.11** floor |
| @vitest/ui | ^4.1.10 | 4.1.11 | [✅] | 2026-09-29 | Aligned with vitest **4.1.11** floor |
| vitest | ^4.1.11 | 4.1.11 | [✅] | 2026-09-29 | Latest **4.x**; Vitest **5.x** deferred (see Known available updates) |
| ajv (override) | >=6.14.0 | 8.18.0 | [✅] | 2026-02-13 | Security override (ReDoS in `$data`, GHSA-2g4f-4pwh-qvx6); lock resolves ajv 8.x from eslint |
| undici (override) | ^7.30.0 | 7.30.0 | [✅] | 2026-09-29 | jsdom transitive; **7.x only** (8.x breaks jsdom 29); clears Dependabot **#268–#275** |
| brace-expansion (override) | ^5.0.12 | 5.0.12 | [✅] | 2026-09-29 | Security override (GHSA-rgw5-rvv9-x895) |
| @babel/core (override) | ^8.0.1 | 8.0.1 | [✅] | 2026-07-19 | Major bump from 7.x (Dependabot #171 line) |
| flatted (override) | >=3.4.2 | 3.4.2 | [✅] | 2026-07-19 | Security override |
| nanoid (override) | ^3.3.17 | 3.3.18 | [✅] | 2026-08-08 | Security override (Dependabot #238 / CVE-2026-67213; lock 3.3.18) |
| postcss (override) | ^8.5.18 | 8.5.28 | [✅] | 2026-09-29 | Security override (Dependabot #222; lock may resolve below registry latest) |
| sharp (override) | ^0.35.5 | 0.35.5 | [✅] | 2026-09-29 | Security override (Dependabot #211); verified `next build` |
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
| structlog | 26.1.0 | 26.1.0 | [✅] | 2026-07-19 | Major bump from 25.x |
| mypy | 2.3.0 | 2.3.0 | [✅] | 2026-07-19 | Major bump from 1.x |
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
| python-dotenv | ==1.2.2 | 1.2.2 | [✅] | 2026-07-19 | Pinned in root `requirements.txt` |
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
| selenium/hub | 4.49.0 | 4.49.0 | [✅] | 2026-09-29 | Pinned in compose + CI (`env-fe.yml`) |
| selenium/node-chrome | 4.49.0 | 4.49.0 | [✅] | 2026-09-29 | Local compose + CI; replaces seleniarm:latest |
| selenium/node-firefox | 4.49.0 | 4.49.0 | [✅] | 2026-09-29 | Local compose.prod + CI |
| selenium/node-edge | 4.49.0 | 4.49.0 | [✅] | 2026-09-29 | CI only (amd64); local ARM uses Chrome stand-in |
<!-- prettier-ignore-end -->

**Note**: All Selenium Grid versions are managed via `selenium_version` in `.github/workflows/env-fe.yml` (default: `4.49.0`) and matching tags in `docker-compose*.yml`. Official **`selenium/*`** images are multi-arch (**amd64/arm64**) for hub/chrome/firefox — do not use unmaintained **`seleniarm/*:latest`**.

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

### Current Status (as of 2026-09-29)

Vulnerability counts change as Dependabot rescans and PRs are merged. Check the live dashboard for current numbers.

**Dependabot Alerts**: https://github.com/CScharer/full-stack-qa/security/dependabot

After the **2026-09-29** refresh: removed legacy **`selenium-grid-hub` / `selenium-grid-core`** (**1.0.5**) from `pom.xml` to clear Dependabot **#259** (FreeMarker **CVE-2026-84939**); frontend `undici` override **`^7.30.0`** (jsdom transitive) clears **#268–#275**; Playwright `csv-parse` override **`^7.0.3`** clears **#250**; `brace-expansion` **`^5.0.12`** in frontend/playwright addresses **GHSA-rgw5-rvv9-x895**. Local `npm audit` reports **0** vulnerabilities in **frontend** and **playwright** after lockfile regeneration.

Earlier fixes still relevant: HttpCore 5 **5.4.3** (#241), `nanoid` (#238), brace-expansion/postcss/sharp/js-yaml (#208–#224), Hibernate 6.6 (#93), Next security line (#210–#221), Jackson 3 (#78), Vite (#80–#84), minimatch #35–#37, qs #13, lodash / socket.io-parser overrides, black #40.

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
    "brace-expansion": "^5.0.12",
    "socket.io-parser": "^4.2.6"
  }
}
```
Apply `brace-expansion` in **frontend** and **playwright**; add `socket.io-parser` in **playwright** only (where Artillery pulls it). Regenerate lockfiles with `npm install` or `npm install --package-lock-only`, then confirm `npm audit` is clean.

**Example 4**: Fixing `undici` (jsdom / Vitest) and `csv-parse` (Artillery)
```json
{
  "overrides": {
    "undici": "^7.30.0",
    "csv-parse": "^7.0.3"
  }
}
```
Use **`undici` ^7.x only** for jsdom 29 (`>=7.29.1` without an upper bound can resolve **undici 8**, which breaks jsdom). Add `undici` in **frontend**; add `csv-parse` in **playwright** only. Pin Playwright `js-yaml` at **5.2.2** (exact) so Artillery’s ESM import keeps working.

The `overrides` section forces all instances of the package (including transitive dependencies) to use the patched version. After adding the override:
1. Run `npm install` to update `package-lock.json`
2. Verify with `npm audit` - should show 0 vulnerabilities for that package
3. Verify with `npm list <package-name>` - should show patched version throughout dependency tree

**Reference**: See Update History section for resolved vulnerabilities.

---

## 📋 Update History

Entries are newest-first.

### 2026-09-29 (comprehensive stable refresh)
- **Maven**: Selenium **4.49.0**, Selenide **7.18.2**, WebDriverManager **6.4.0**, JSoup **1.23.2**, Netty **4.2.18.Final**, `htmlunit3-driver` **4.48.0**; Jackson 3/2.x already at **3.2.2** / **2.22.2** in `pom.xml`.
- **CI**: `env-fe.yml` default Grid **4.49.0** (aligned with `pom.xml`).
- **Docker**: Local `docker-compose*.yml` switched from **`seleniarm/*:latest`** to pinned **`selenium/*:4.49.0`** (multi-arch; clears Phase 4 version-validator warnings). See `docs/work/20260406_SELENIUM_DOCKER_IMAGE_TAGS.md`.
- **npm**: Frontend dev toolchain (ESLint **10.11**, `@types/node` **26.6.3**, Vitest **4.1.11**, testing-library patches, sharp **0.35.5**, postcss **8.5.28**); Cypress **15.21.1** + `qs` **6.16.0**; Vibium **26.8.21**; `@types/node` **26.6.3** in playwright/cypress/vibium.
- **Verify**: `./mvnw -DskipTests compile`, frontend **98** Vitest tests + `next build`, cypress `tsc`, vibium `tsc`, frontend/cypress audits clean (playwright: Artillery `js-yaml` pin trade-off).

### 2026-09-29 (Dependabot #250 / #259 / #268–#275)
- **Maven**: Removed unused legacy **`org.seleniumhq.selenium.grid:selenium-grid-hub`** and **`selenium-grid-core`** (**1.0.5**). Clears Dependabot **#259** / **CVE-2026-84939** (FreeMarker path traversal). Grid runtime unchanged (Docker/CI Selenium **4.49.0**; modern `selenium-grid` / `selenium-server` deps retained).
- **npm (frontend)**: Next **16.3.7**, React **19.3.0**, `eslint-config-next` **16.3.7**, axios **^1.20.0**, `@tanstack/react-query` **^5.104.0**, Vite **^8.3.1**; overrides **`undici` ^7.30.0**, **`brace-expansion` ^5.0.12**. Clears Dependabot **#268–#275**. Verified `npm test -- --run` (98 tests) and `next build`.
- **npm (playwright)**: `@playwright/test` **^1.63.0**; overrides **`csv-parse` ^7.0.3**, **`brace-expansion` ^5.0.12**; **`js-yaml` pinned 5.2.2** for Artillery compatibility. Clears Dependabot **#250**. Verified `npm audit` clean and Playwright test listing.
- **Docs**: VERSION_TRACKING / VERSION_MONITORING / SECURITY / README / docs README / SELENIUM_GRID.md updated.

### 2026-08-13 (Dependabot #241 httpcore5)
- **Maven**: Bumped Apache HttpClient 5 **5.6.1 → 5.6.3** and pinned Apache HttpCore 5 (`httpcore5` / `httpcore5-h2`) to **5.4.3** in `dependencyManagement`. Clears Dependabot **#241** / CVE-2026-54399 (HTTP/1 header parsing memory-exhaustion DoS). `httpclient5` 5.6.1 resolved `httpcore5` **5.4**, which is still in the vulnerable range (`< 5.4.3`).
- **Docs**: VERSION_TRACKING / README / SECURITY / VERSION_MONITORING refreshed for the HttpClient 5 / HttpCore 5 pins.

### 2026-08-08 (Dependabot #238 nanoid + doc version sync)
- **npm**: Added `nanoid` override **^3.3.17** (lock **3.3.18**) in frontend, playwright, and vibium to clear GHSA-2v37-7h3g-55p8 / CVE-2026-67213 (infinite loop when custom generator size is 0). Regenerated lockfiles.
- **Docs**: Synced living version docs with repo pins — JSoup **1.23.1**, Allure2 Java **2.35.3**, vibium `postcss` lock **8.5.25**, frontend/playwright/vibium `nanoid` rows, Cypress `uuid`/`yauzl` overrides, and refreshed README / SECURITY / VERSION_MONITORING / ALLURE_REPORTING summaries.

### 2026-07-26 (Dependabot npm security overrides)
- **npm**: Raised overrides — `brace-expansion` **^5.0.5 → ^5.0.8** (frontend + playwright), `postcss` **^8.5.10 → ^8.5.18** (frontend + playwright + vibium; lock **8.5.23** / **8.5.20**), Playwright `js-yaml` **^5.2.1 → ^5.2.2**, frontend `sharp` **^0.35.3** (clears Dependabot #211; Next still declares optional `^0.34.5`). Regenerated lockfiles; `next build` verified with sharp 0.35.3.
- **Docs**: VERSION_TRACKING / README / SECURITY / monitoring refreshed for the pins above.

### 2026-07-19 (deferred major bumps)
- **Maven**: Jackson 2.x **2.21.5 → 2.22.1** (annotations **2.22**); Hibernate **5.6.15.Final → 6.6.54.Final** (`org.hibernate.orm:hibernate-core`, Jakarta Persistence **3.1**, dropped `hibernate-entitymanager`); migrated test JPA bootstrap (`javax.persistence` → `jakarta.persistence`, `persistence.xml` 3.0).
- **npm**: `@types/node` **25.9.5 → 26.1.1** across frontend/cypress/playwright/vibium; Playwright `js-yaml` override **3.15.0 → 5.2.1**; frontend `@babel/core` override **7.29.6 → 8.0.1**. Regenerated lockfiles. Modernized tsconfigs (`moduleResolution: bundler`, removed `baseUrl` / `ignoreDeprecations`). **TypeScript 7 attempted but reverted to 6.0.3** — the native TS 7 package breaks Next 16's built-in type-check step and `eslint-config-next` (typescript-eslint peer `>=4.8.4 <6.1.0`), which hung the CI frontend startup.
- **Python**: `pyproject.toml` mypy **1.20.0 → 2.3.0**, structlog **25.5.0 → 26.1.0**.
- **Docs**: VERSION_TRACKING / README / SECURITY / monitoring / worklog refreshed for the majors above. Hibernate 7.x left for a later pass.

### 2026-07-19 (safe stable bump + mobile tearDown fix)
- **Maven**: Selenium/Selenide **4.49.0 / 7.17.0**; Jackson 3 **3.2.1**; Netty **4.2.16.Final**; Log4j **2.26.1**; Logback **1.5.38**; Cucumber **7.34.4**; REST Assured **6.0.1**; Gatling **3.15.1**; JUnit **6.1.2**; Allure Java **2.35.3**; Checkstyle **13.8.0**; SpotBugs **4.10.3** (+ plugin **4.10.3.0**); OpenTelemetry **1.64.0**; PostgreSQL JDBC **42.7.13**; htmlunit3-driver **4.49.0**; Surefire **3.5.6**; Compiler plugin **3.15.0**; Secret Manager **2.94.0**; Appium **10.1.1**. Hibernate remains **5.6.15.Final**. Jackson 2.x remains **2.21.5** (2.22.1 available).
- **npm**: Next/React **16.2.10 / 19.2.7**; Vite **8.1.5**; Vitest **4.1.10**; axios **1.18.1**; Cypress **15.18.1**; Playwright **1.61.1**; Vibium **26.5.31**; Artillery **2.0.33**; related overrides (`qs`, `fast-xml-parser`).
- **Python**: FastAPI/uvicorn/ruff/black floors raised; Locust **2.45.0**, requests **2.34.2**.
- **CI**: `env-fe.yml` default `selenium_version` **4.49.0**.
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

### 2025-12-20
- **Selenium Grid**: Centralized version (4.39.0, updated to 4.40.0 on 2026-01-25) and ports via workflow input variables
- **Document Created**: Initial version tracking document

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

- **Selenium Version Alignment**: Client (pom.xml) and Server (CI/CD) versions must match. Currently aligned at **4.49.0** (`env-fe.yml` default).
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
- **Last Updated**: 2026-09-29
- **Next Review**: 2026-10-01 (recommended)
- **Maintainer**: Development Team

**Remember**: This is a living document. Update it regularly to keep version information current!
