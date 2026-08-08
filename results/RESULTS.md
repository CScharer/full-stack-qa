# Results

## This is simply for downloaded results from the pipeline.

## Pipeline baseline (what should still run)

**Reference run**: [Security: Fix lodash prototype pollution vulnerability (CVE) (#191) — Run 21314090747](https://github.com/CScharer/full-stack-qa/actions/runs/21314090747) (push to `main` @ 591d71d).

That run is the baseline for “expected pipeline behavior.” The same jobs and Maven test scope should still run.

**FE test jobs that ran (per environment DEV / TEST / PROD):**

- **Smoke Tests** — Maven via `run-maven-tests.sh` with `testng-smoke-suite.xml` (~4 min each)
- **Grid Tests** — matrix (chrome, firefox, edge)
- **Mobile Browser Tests** (~4 min)
- **Responsive Design Tests** (~4 min)
- **Cypress Tests** (~2 min)
- **Playwright Tests** (~2 min)
- **Robot Framework Tests** (~1.5 min)
- **Selenide Tests** (~4 min)
- **Vibium Tests** (~1 min)

**Maven / Smoke Tests:**  
`run-maven-tests.sh` calls `./mvnw -ntp test -Dsurefire.suiteXmlFiles=<suite> -Dsurefire.excludes=**/* …`.  
`-Dsurefire.suiteXmlFiles` scopes the **TestNG** provider to that suite. `-Dsurefire.excludes=**/*` blocks the **JUnit Platform** provider from also discovering unrelated tests under `src/test/java/com/cjs/qa` (utilities, gt, Atlassian, etc.). Surefire ignores includes/excludes for TestNG when a suite XML is set, so the suite classes still run.  

**Do not skip** Cypress/Playwright/Robot/Vibium/Grid/Mobile/Responsive for Dependabot or Maven-only PRs. If logs show those extra `com.cjs.qa` classes inside a suite job, suite scoping has regressed — see `docs/guides/infrastructure/SUREFIRE_SUITE_SCOPING.md` (this has happened before).