# Surefire Suite Scoping (CI Maven Jobs)

**Status**: Active  
**Related**: [Pipeline Workflow](PIPELINE_WORKFLOW.md), [Test Suites Reference](../testing/TEST_SUITES_REFERENCE.md), [Results baseline](../../../results/RESULTS.md)  
**Script**: `scripts/ci/run-maven-tests.sh`

---

## Purpose

FE CI jobs (Smoke, Grid, Mobile, Responsive, Selenide) must run **only** the classes listed in their TestNG suite XML. They must **not** also execute the broader tree under `src/test/java/com/cjs/qa` via Maven Surefire’s default JUnit discovery.

**This has happened before** (including on Dependabot PRs such as jsoup bumps). When it happens, logs show the expected suite class (for example `MobileBrowserTests`) and then many unrelated classes such as `CommandLineTests`, `FSOTests`, `SecureConfigTest`, `AtlassianTests`, `GTWAPIMethodsTests`, and similar packages. Those extras are not part of the FE job and must stay out of the run.

Do **not** “fix” this by skipping Cypress, Playwright, Robot, Vibium, Grid, Mobile, Responsive, BE, or FS for dependency PRs. Those jobs should keep running. The bug is **suite pollution**, not “too many FE jobs.”

---

## Root cause

`pom.xml` configures **both** Surefire providers:

- `surefire-testng` — honors `-Dsurefire.suiteXmlFiles=…`
- `surefire-junit-platform` — still discovers JUnit 5 tests by default name patterns

So a CI command like:

```bash
./mvnw test -Dsurefire.suiteXmlFiles=src/test/resources/testng-mobile-browser-suite.xml
```

runs the TestNG suite **and** every non-`@Disabled` JUnit class under `src/test/java/com/cjs/qa` that matches `*Test(s).java`.

Historically this was papered over with `@Disabled` on many Windows-specific classes. That is incomplete: many utility/domain JUnit tests remain enabled and still get discovered. Empty or commented-out suite XML made the problem worse (TestNG ran 0 tests; JUnit noise still “passed” the job).

---

## Required behavior

| Job | Suite XML | Must run | Must not run |
| --- | --- | --- | --- |
| Smoke Tests | `testng-smoke-suite.xml` | Classes listed in that suite | Other `com.cjs.qa.*` via JUnit discovery |
| Grid Tests | `testng-<test_suite>-suite.xml` (often smoke) | Suite classes only | Same |
| Mobile Browser | `testng-mobile-browser-suite.xml` | `MobileBrowserTests` | Same |
| Responsive Design | `testng-responsive-suite.xml` | `ResponsiveDesignTests` | Same |
| Selenide | `testng-selenide-suite.xml` | Suite classes only | Same |
| Cypress / Playwright / Robot / Vibium | (non-Maven) | Their own runners | N/A — always run on normal PRs |

Pipeline FE/BE/FS job selection is unchanged for Dependabot and other PRs: still the normal matrix. Only Maven Surefire execution inside a suite job is scoped.

---

## Implementation

`scripts/ci/run-maven-tests.sh` always:

1. Sets `-Dsurefire.suiteXmlFiles=<resolved suite path>` (and legacy `-DsuiteXmlFile` for compatibility).
2. Sets `-Dsurefire.excludes=**/*` so the **JUnit** provider discovers nothing.

Surefire **ignores** includes/excludes for TestNG when `suiteXmlFiles` is set, so the suite still runs exactly the classes in the XML.

Suite XML files under `src/test/resources/` must list the intended TestNG classes (do not leave suites empty/commented out and rely on JUnit discovery).

---

## How to verify in CI logs

A healthy suite job looks like:

```text
Using configured provider org.apache.maven.surefire.testng.TestNGProvider
Using configured provider org.apache.maven.surefire.junitplatform.JUnitPlatformProvider
Running TestSuite
Tests run: N, ...   # N matches suite methods only
```

and does **not** later show:

```text
Running com.cjs.qa.utilities.CommandLineTests
Running com.cjs.qa.utilities.FSOTests
Running com.cjs.qa.junit.tests.AtlassianTests
```

If those lines appear again after a suite job, suite scoping has regressed — fix `run-maven-tests.sh` / Surefire config; do not disable FE frameworks.

---

## Local check

```bash
./scripts/ci/run-maven-tests.sh dev testng-mobile-browser-suite.xml 1
```

Confirm Surefire reports only `MobileBrowserTests` (plus any intentional suite peers), not utilities/domain JUnit classes.

---

## Related history

- JUnit 6 migration increased automatic discovery of `@Test` methods under `com.cjs.qa` (see `docs/work/framework/20260124_JUNIT_4_TO_6_MIGRATION_GUIDE.md`).
- `@Disabled` on some Windows-specific classes is necessary but **not sufficient** while both Surefire providers remain enabled.
- Dependabot Maven PRs previously looked “broken” because Mobile/Responsive failed while logs also showed unrelated `com.cjs.qa` discovery; the correct remediation is suite isolation, not skipping the FE matrix.
