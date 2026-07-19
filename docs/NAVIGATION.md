# 📚 Documentation Navigation Guide
## CJS QA Automation Framework(s) Documentation

**Last Updated**: April 6, 2026
**Version**: 2.1 - Cleaned Up Structure
**Purpose**: Navigate and understand the documentation structure

---

## 🗺️ DOCUMENTATION STRUCTURE

### Main Documentation (docs/ folder)

```
docs/
├── NAVIGATION.md (this file)           📍 You are here!
├── README.md                            📖 Main documentation overview
│
├── work/                                📋 Work & Planning Documents
│   ├── framework/                       🔧 Framework Migration & Planning
│   │   ├── 20260124_JUNIT_4_TO_6_MIGRATION_GUIDE.md ✅ JUnit 4→6 Migration (Complete)
│   │   ├── 20260120_TEST_FIXTURES_PLAN.md Test fixtures planning
│   │   ├── 20260120_API_MOCK_DATA_TESTS_PLAN.md API mock data tests planning
│   │   ├── 20260117_WIZARD_TESTS_IMPLEMENTATION_PLAN.md Wizard tests implementation
│   │   ├── 20260117_TEST_DATA_CENTRALIZATION_PLAN.md Test data centralization
│   │   ├── 20260117_SHARED_TEST_SPECIFICATION_ANALYSIS.md Shared test specifications
│   │   └── 20260117_CONCURRENT_TEST_DATA_ISOLATION_PLAN.md Concurrent test data isolation
│   ├── MIGRATE_REPO.md                  📦 Repository migration guide (in setup/)
│   └── [date-prefixed documents]        📅 Planning and analysis documents
│
├── guides/                              📚 How-To Guides & Documentation
│   ├── infrastructure/                  🏗️ Infrastructure Setup
│   │   ├── DOCKER.md                    Complete Docker guide
│   │   ├── GITHUB_ACTIONS.md            CI/CD pipeline guide
│   │   ├── GITHUB_PAGES_SETUP.md        Report hosting setup
│   │   ├── PORT_CONFIGURATION.md        Port configuration guide
│   │   ├── SERVICE_SCRIPTS.md           Service management scripts guide
│   │   ├── WORKFLOW_TEST_ORGANIZATION.md Test job grouping and organization
│   │   └── ADD_PERFORMANCE_TO_CICD.md   Performance testing in CI/CD
│   ├── testing/                         🧪 Testing Guides
│   │   ├── TEST_EXECUTION_GUIDE.md      How to run tests
│   │   ├── SMOKE_TEST_PLAN.md           Smoke testing strategy
│   │   ├── PERFORMANCE_TESTING.md       Performance testing guide
│   │   ├── MOBILE_TESTING.md            Mobile testing setup
│   │   └── ALLURE_REPORTING.md          Test reporting guide
│   ├── java/                            ☕ Java Development
│   │   ├── JAVA_17_FEATURES.md          Java 17 features guide
│   │   ├── JAVA_21_FEATURES.md          Java 21 features guide
│   │   └── CODE_QUALITY.md              Code quality tools and standards
│   ├── setup/                           ⚙️ Initial Setup
│   │   └── (INTEGRATION_COMPLETE.md moved to PRIVATE/ folder)
│   └── troubleshooting/                 🔧 Problem Solving
│       └── CI_TROUBLESHOOTING.md        CI/CD troubleshooting
│
├── process/                             📋 Team Processes
│   ├── SECURITY.md                      🔐 Security standards and practices (living document)
│   ├── NAMING_STANDARDS.md              📝 Naming conventions (living document)
│   ├── CODE_OF_CONDUCT.md               Community guidelines
│   └── AI_WORKFLOW_RULES.md             AI-assisted development rules
│
├── architecture/                        🏛️ Architecture Documentation
│   └── decisions/                       Architecture Decision Records
│       └── README.md                    ADR guide (ADRs to be added)
│
├── issues/                              📋 Issue Tracking & Work Items
│   ├── README.md                        Issue management guide
│   └── open/                            Pending issues (to be created on GitHub)
│       └── 20251112_missing-performance-test-files.md (✅ Resolved - files exist)
│
└── archive/                             📦 Historical Documents
    ├── 2025-11/                         November 2025 completed work
    └── 2025-12/                         December 2025 cleanup & optimization
        ├── 20251116_JAVA_21_MIGRATION_PROGRESS.md
        ├── 20251115_LINTER_FIXES_SUMMARY.md
        ├── 20251201_LOCAL_TESTING_RESULTS.md
        ├── 20251114_NEXT_STEPS_SUMMARY.md
        ├── 20251114_PENDING_WORK_SUMMARY.md
        ├── 20251113_REORGANIZATION_COMPLETE.md
        ├── 20251201_SUMMARY.md
        └── 20251114_TEST_RESULTS_DATA_DRIVEN_API_CONTRACT.md
```

### Configuration READMEs (Outside docs/ - Exceptions)

**These .md files exist outside docs/ by design** - they document features local to their directories:

```
Project Structure:
├── README.md                            📖 Main project README (GitHub landing page)
├── xml/README.md                        ⚙️ XML configuration guide
├── config/README.md                     ⚙️ Environment setup guide (includes XML configuration)
├── scripts/README.md                    🔧 Script usage guide
└── .github/
    ├── pull_request_template.md         📋 PR template (GitHub UI)
    └── ISSUE_TEMPLATE/
        ├── bug_report.md                📋 Bug template (GitHub UI)
        ├── feature_request.md           📋 Feature template (GitHub UI)
        └── test_failure.md              📋 Test failure template (GitHub UI)
```

---

## 🎯 QUICK START GUIDES

### 🆕 New to the Project?
**Read in this order:**

1. **[README.md](./README.md)** *(5 min)*
   - Project overview and getting started

2. **INTEGRATION_COMPLETE.md** *(moved to PRIVATE/ folder)*
   - Google Cloud Secret Manager setup
   - Initial configuration

3. **[guides/testing/TEST_EXECUTION_GUIDE.md](./guides/testing/TEST_EXECUTION_GUIDE.md)** *(10 min)*
   - How to run tests
   - Basic test execution

4. **[guides/infrastructure/DOCKER.md](./guides/infrastructure/DOCKER.md)** *(20 min)*
   - Docker setup and usage
   - Selenium Grid guide

---

## 🔍 FINDING WHAT YOU NEED

### By Task/Goal:

<!-- prettier-ignore-start -->
| What You Want To Do | Where To Look |
| -- | -- |
| **Set up project for first time** | `guides/setup/` → `README.md` |
| **Run tests** | `guides/testing/TEST_EXECUTION_GUIDE.md` |
| **Run tests locally (no Docker)** | `guides/testing/LOCAL_TESTING.md` |
| **Set up Docker/Grid** | `guides/infrastructure/DOCKER.md` |
| **Understand CI/CD** | `guides/infrastructure/GITHUB_ACTIONS.md` |
| **Fix CI/CD issues** | `guides/troubleshooting/CI_TROUBLESHOOTING.md` |
| **Code quality standards** | `guides/java/CODE_QUALITY.md` |
| **See Allure reports** | `guides/testing/ALLURE_REPORTING.md` |
| **Performance testing** | `guides/testing/PERFORMANCE_TESTING.md` |
| **Mobile testing** | `guides/testing/MOBILE_TESTING.md` |
| **Understand JUnit migration** | `work/framework/20260124_JUNIT_4_TO_6_MIGRATION_GUIDE.md` |
| **Improve the framework** | `work/` (planning documents) |
| **Learn modern Java/Selenium** | `guides/java/CODE_QUALITY.md` |
| **See what's been done** | `archive/` |
| **Understand decisions** | `architecture/decisions/` |
| **Know team standards** | `process/` |
<!-- prettier-ignore-end -->

---

**Last Updated**: April 6, 2026
**Maintained By**: CJS QA Team
**Version**: 2.1 - Cleaned Up Structure
