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
`run-maven-tests.sh` calls `./mvnw -ntp test -DsuiteXmlFile=testng-smoke-suite.xml …`.  
Surefire does **not** use `suiteXmlFiles` in `pom.xml`, so `-DsuiteXmlFile` is ignored and Maven uses **default discovery** (`**/Test*.java`, `**/*Test.java`, `**/*Tests.java`, `**/*TestCase.java`). Both **JUnit Jupiter** and **TestNG** providers are enabled, so all non-disabled tests run: JUnit tests with `@Disabled` are skipped; TestNG tests run by pattern.  
Do not add `<suiteXmlFiles>` to Surefire without also updating the TestNG suite so it selects the same tests (or you will reduce/change what runs).