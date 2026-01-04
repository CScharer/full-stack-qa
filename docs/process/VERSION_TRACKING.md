# Version Tracking & Update Schedule

**Date Created**: 2025-12-20  
**Status**: 📋 Living Document  
**Purpose**: Track dependency versions and schedule periodic updates  
**Update Frequency**: Monthly review recommended

> **💡 Automation**: Version validation is now automated via `scripts/validate-dependency-versions.sh` and CI/CD job `validate-versions`. This helps prevent version drift and ensures versions are aligned across the project.
> 
> **✅ Pre-Push Validation**: Pre-push version validation is now implemented! The pre-push hook automatically validates Selenium versions before code is pushed. See [Selenium Grid Enhancements](../work/20260103_SELENIUM_GRID_ENHANCEMENTS.md) for details.

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
- **Last Review**: 2025-12-30
- **Next Review**: 2026-01-30 (recommended)

---

## 📦 Java/Maven Dependencies (pom.xml)

### Core Testing Frameworks

| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
|------------|----------------|---------------|--------|--------------|-------|
| Selenium | 4.39.0 | 4.39.0 | [✅] | 2025-12-20 | Aligned with Grid server version |
| Selenide | 7.13.0 | 7.13.0 | [✅] | 2025-12-19 | Updated in PR #51 |
| TestNG | 7.11.0 | 7.11.0 | [✅] | - | Current |
| JUnit | 4.13.2 | 4.13.2 | [✅] | - | Current (JUnit 5.14.0 available) |
| Cucumber | 7.33.0 | 7.33.0 | [✅] | - | Current |
| REST Assured | 6.0.0 | 6.0.0 | [✅] | 2025-12-19 | Updated in PR #51 - Requires Java 17+, Jackson 3.0.0 |
| Allure3 CLI | 3.0.0 | 3.0.0 | [✅] | 2025-12-30 | Active - Allure3 CLI in use (TypeScript-based, npm install) |
| Allure2 Java | 2.32.0 | 2.32.0 | [✅] | 2025-12-30 | Latest in Maven Central (Java libraries unchanged) |

### Build & Tools

| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
|------------|----------------|---------------|--------|--------------|-------|
| Maven | 3.9.11 | 3.9.11 | [✅] | 2025-12-19 | Updated in PR #51 |
| Java | 21 | 21 (LTS) | [✅] | - | Current LTS version |
| Maven Compiler Plugin | 3.13.0 | 3.14.1 | [⚠️] | 2025-12-30 | Current: 3.13.0, update available: 3.14.1 |
| Maven Surefire Plugin | 3.5.4 | 3.5.4 | [✅] | 2025-12-19 | Updated in PR #51 |
| Maven Checkstyle Plugin | 3.6.0 | 3.6.0 | [✅] | - | Current |
| SpotBugs | 4.9.8 | 4.9.8 | [✅] | - | Current |
| PMD | 3.27.0 | 3.27.0 | [✅] | - | Current |

### Performance Testing

| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
|------------|----------------|---------------|--------|--------------|-------|
| Gatling | 3.14.9 | 3.14.9 | [✅] | - | Current |
| JMeter | 5.6.3 | 5.6.3 | [✅] | - | Current |
| Scala | 2.13.18 | 2.13.18 | [✅] | 2025-12-19 | Updated in PR #51 - For Gatling |
| Artillery | 2.0.0 | 2.0.0 | [✅] | 2025-12-31 | Browser-level load testing with Playwright |

### Utilities & Libraries

| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
|------------|----------------|---------------|--------|--------------|-------|
| WebDriverManager | 6.3.3 | 6.3.3 | [✅] | - | Current |
| Log4j 2 | 2.25.3 | 2.25.3 | [✅] | 2025-12-19 | Updated via Dependabot PR #52 |
| Jackson Databind | 3.0.3 | 3.0.3 | [✅] | 2025-12-30 | Current stable version |
| Jackson Annotations | 2.20 | 2.20 | [✅] | 2025-12-19 | Compatible with Jackson 3.0.0 |
| Apache POI | 5.5.1 | 5.5.1 | [✅] | 2025-12-19 | Updated in PR #51 |
| MSSQL JDBC | 13.2.1.jre11 | 13.2.1.jre11 | [✅] | 2025-12-30 | Current stable version |

---

## 📦 Node.js Dependencies

### Cypress Project (cypress/package.json)

| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
|------------|----------------|---------------|--------|--------------|-------|
| Cypress | 15.8.1 | 15.8.1 | [✅] | 2025-12-30 | Current stable version |
| TypeScript | 5.9.3 | 5.9.3 | [✅] | 2025-12-30 | Current stable version |
| @types/node | 25.0.0 | 25.0.0 | [✅] | 2025-12-19 | Updated in PR #51 |

### Playwright Project (playwright/package.json)

| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
|------------|----------------|---------------|--------|--------------|-------|
| Playwright | 1.57.0 | 1.57.0 | [✅] | - | Current |
| TypeScript | 5.9.3 | 5.9.3 | [✅] | 2025-12-30 | Current stable version |
| @types/node | 25.0.0 | 25.0.0 | [✅] | 2025-12-19 | Updated in PR #51 |

### Vibium Project (vibium/package.json)

| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
|------------|----------------|---------------|--------|--------------|-------|
| Vitest | 4.0.16 | 4.0.16 | [✅] | 2025-12-19 | Updated via Dependabot PR #48 |
| TypeScript | 5.9.3 | 5.9.3 | [✅] | 2025-12-30 | Current stable version |
| @types/node | 25.0.0 | 25.0.0 | [✅] | 2025-12-19 | Updated in PR #51 |

### Frontend Project (frontend/package.json)

| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
|------------|----------------|---------------|--------|--------------|-------|
| React | 19.2.3 | 19.2.3 | [✅] | 2025-12-30 | Current stable version |
| Next.js | 16.1.1 | 16.1.1 | [✅] | 2025-12-30 | Current stable version |
| @tanstack/react-query | 5.90.16 | 5.90.16 | [✅] | 2025-12-30 | Current stable version |
| eslint-config-next | 16.1.1 | 16.1.1 | [✅] | 2025-12-30 | Current stable version |
| TypeScript | 5.9.3 | 5.9.3 | [✅] | 2025-12-30 | Current stable version |
| Bootstrap | 5.3.8 | 5.3.8 | [✅] | 2025-12-19 | Updated in PR #51 |
| React Bootstrap | 2.10.10 | 2.10.10 | [✅] | 2025-12-19 | Updated in PR #51 |
| @testing-library/react | 16.3.0 | 16.3.0 | [✅] | 2025-12-19 | Updated in PR #51 |
| @testing-library/jest-dom | 6.9.1 | 6.9.1 | [✅] | 2025-12-19 | Updated in PR #51 |
| @testing-library/user-event | 14.6.1 | 14.6.1 | [✅] | 2025-12-19 | Updated in PR #51 |
| jsdom | 27.4.0 | 27.4.0 | [✅] | 2025-12-30 | Current stable version |
| ESLint | 9.39.2 | 9.39.2 | [✅] | 2025-12-19 | Updated in PR #51 |
| @types/node | 25.0.0 | 25.0.0 | [✅] | 2025-12-19 | Updated in PR #51 |

---

## 🐍 Python Dependencies

### Root (pyproject.toml)

| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
|------------|----------------|---------------|--------|--------------|-------|
| numpy | 2.3.5 | 2.3.5 | [✅] | 2025-12-19 | Updated in PR #51 - Major version (2.x) |
| structlog | 25.5.0 | 25.5.0 | [✅] | 2025-12-19 | Updated in PR #51 |
| pyright | 1.1.407 | 1.1.407 | [✅] | 2025-12-19 | Updated in PR #51 |

### Backend (backend/requirements.txt)

| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
|------------|----------------|---------------|--------|--------------|-------|
| FastAPI | 0.125.0 | 0.125.0 | [✅] | 2025-12-19 | Updated in PR #51 |
| Uvicorn | 0.38.0 | 0.38.0 | [✅] | 2025-12-19 | Updated in PR #51 |
| Starlette | 0.50.0 | 0.50.0 | [✅] | 2025-12-19 | Updated in PR #51 |
| Pydantic | 2.12.5 | 2.12.5 | [✅] | 2025-12-19 | Updated in PR #51 |
| Pydantic Settings | 2.0.3 | 2.0.3 | [✅] | 2025-12-19 | Updated in PR #51 |
| aiosqlite | 0.21.0 | 0.21.0 | [✅] | 2025-12-19 | Updated in PR #51 |
| httpx | 0.28.1 | 0.28.1 | [✅] | 2025-12-19 | Updated in PR #51 |
| pytest | 9.0.2 | 9.0.2 | [✅] | 2025-12-19 | Updated in PR #51 |
| pytest-asyncio | 1.3.0 | 1.3.0 | [✅] | 2025-12-19 | Updated in PR #51 |
| pytest-cov | 7.0.0 | 7.0.0 | [✅] | 2025-12-19 | Updated in PR #51 |
| python-dotenv | 1.2.1 | 1.2.1 | [✅] | 2025-12-19 | Updated in PR #51 |
| black | 25.12.0 | 25.12.0 | [✅] | 2025-12-19 | Updated in PR #51 |
| ruff | 0.14.9 | 0.14.9 | [✅] | 2025-12-19 | Updated in PR #51 |

### Performance Testing (requirements.txt)

| Dependency | Current Version | Latest Stable | Status | Last Updated | Notes |
|------------|----------------|---------------|--------|--------------|-------|
| Locust | 2.42.6 | 2.42.6 | [✅] | 2025-12-19 | Updated in PR #51 |
| Requests | 2.32.4 | 2.32.4 | [✅] | 2025-12-30 | Adjusted for Locust 2.42.6 compatibility (requires requests<2.32.5) |
| python-dotenv | 1.2.1 | 1.2.1 | [✅] | 2025-12-19 | Updated in PR #51 |
| matplotlib | 3.10.8 | 3.10.8 | [✅] | 2025-12-19 | Updated in PR #51 |
| pandas | 2.3.3 | 2.3.3 | [✅] | 2025-12-19 | Updated in PR #51 |

---

## 🐳 Docker/CI/CD Versions

### Selenium Grid (GitHub Actions Workflow)

| Component | Current Version | Latest Stable | Status | Last Updated | Notes |
|-----------|----------------|---------------|--------|--------------|-------|
| selenium/hub | 4.39.0 | 4.39.0 | [✅] | 2025-12-20 | Centralized via input variable |
| selenium/node-chrome | 4.39.0 | 4.39.0 | [✅] | 2025-12-20 | Centralized via input variable |
| selenium/node-firefox | 4.39.0 | 4.39.0 | [✅] | 2025-12-20 | Centralized via input variable |
| selenium/node-edge | 4.39.0 | 4.39.0 | [✅] | 2025-12-20 | Centralized via input variable |

**Note**: All Selenium Grid versions are now managed via `selenium_version` input variable in `.github/workflows/env-fe.yml` (default: `4.39.0`)

### Selenium Grid Ports (GitHub Actions Workflow)

| Port | Current Value | Status | Last Updated | Notes |
|------|---------------|--------|--------------|-------|
| Hub Port | 4444 | [✅] | 2025-12-20 | Centralized via `se_hub_port` input |
| Event Bus Publish | 4442 | [✅] | 2025-12-20 | Centralized via `se_pub_port` input |
| Event Bus Subscribe | 4443 | [✅] | 2025-12-20 | Centralized via `se_sub_port` input |

**Note**: All Selenium Grid ports are now managed via input variables in `.github/workflows/env-fe.yml`

---

## 🔒 Security Vulnerabilities

### Current Status (as of 2025-12-30)

**Total Vulnerabilities**: 28 (reduced from 29)
- **Critical**: 6 🔴
- **High**: 9 🟠 (reduced from 10 - qs vulnerability fixed)
- **Moderate**: 10 🟡
- **Low**: 3 🟢

**Dependabot Alerts**: https://github.com/CScharer/full-stack-qa/security/dependabot

### Update Strategy

1. **Immediate**: Review and apply Critical security patches
2. **High Priority**: Review and apply High severity patches
3. **Medium Priority**: Review Moderate vulnerabilities (may be acceptable)
4. **Low Priority**: Review Low vulnerabilities (likely acceptable)

**Note**: Many vulnerabilities may be resolved by applying pending dependency updates listed above.

### Fixing Transitive Dependency Vulnerabilities (npm)

When a vulnerability exists in a transitive dependency (not directly in your `package.json`), use npm's `overrides` feature to force a patched version:

**Example**: Fixing `qs` vulnerability in Cypress (Dependabot Alert #1)
```json
{
  "devDependencies": {
    "qs": "^6.14.1"
  },
  "overrides": {
    "qs": "^6.14.1"
  }
}
```

The `overrides` section forces all instances of the package (including transitive dependencies) to use the patched version. After adding the override:
1. Run `npm install` to update `package-lock.json`
2. Verify with `npm audit` - should show 0 vulnerabilities for that package
3. Verify with `npm list <package-name>` - should show patched version throughout dependency tree

**Reference**: See Update History section for resolved vulnerabilities.

---

## 📋 Update History

### 2025-12-20
- **Selenium Grid**: Centralized version (4.39.0) and ports via workflow input variables
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
- **Maven Compiler Plugin**: Current 3.13.0 (update available: 3.14.1)
- **jsdom**: 27.3.0 (update available: 27.4.0)
- **Outdated Dependencies Document**: Created `docs/work/20251230_OUTDATED_DEPENDENCIES.md` with 10 outdated dependencies identified
- **Security Fix - qs (npm)**: Fixed Dependabot alert #1 (High severity) by adding `qs@^6.14.1` as direct dependency and using npm `overrides` to force patched version throughout dependency tree. Vulnerability: ArrayLimit bypass in bracket notation allows DoS via memory exhaustion (GHSA-6rw7-vpxm-498p). Fixed in `cypress/package.json`.
- **Dependency Fix - requests (Python)**: Adjusted `requests` from 2.32.5 to 2.32.4 in `requirements.txt` and `.github/workflows/env-be.yml` to resolve dependency conflict with Locust 2.42.6 (requires `requests<2.32.5`). This fixes the dependency submission workflow failure.

### 2025-12-19
- **REST Assured**: 5.5.6 → 6.0.0 (PR #51)
- **Cypress**: 13.7.0 → 15.2.0 (PR #51)
- **Selenide**: 7.12.3 → 7.13.0 (PR #51)
- **Maven**: 3.9.9 → 3.9.11 (PR #51)
- **Maven Compiler Plugin**: 3.13.1 → 3.13.0 (current, update available: 3.14.1)
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

- **Selenium Version Alignment**: Client (pom.xml) and Server (CI/CD) versions must match. Currently aligned at 4.39.0.
  - **Validation**: Currently validated via scheduled workflow and manual script execution
  - **✅ Implemented**: Pre-push hook validation catches mismatches before code is pushed (see [Selenium Grid Enhancements](../work/20260103_SELENIUM_GRID_ENHANCEMENTS.md))
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
- **Last Updated**: 2025-12-30
- **Next Review**: 2026-01-30 (recommended)
- **Maintainer**: Development Team

**Remember**: This is a living document. Update it regularly to keep version information current!
