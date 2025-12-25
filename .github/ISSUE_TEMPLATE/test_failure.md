---
name: Test Failure Report
about: Report a failing test in the test suite
title: '[TEST FAIL] '
labels: test-failure, bug
assignees: CScharer

---

## Failing Test Information

**Test Class**: <!-- e.g., com.cjs.qa.junit.tests.Scenarios -->
**Test Method**: <!-- e.g., testGoogle -->
**Test Suite**: <!-- e.g., Google, Microsoft, LinkedIn -->

## Failure Details

### Error Message
```
Paste error message here
```

### Stack Trace
```
Paste stack trace here
```

## Test Execution

**Command Used**:
```bash
./mvnw test -Dtest=Scenarios#Google
```

**Browser**: <!-- e.g., Chrome, Firefox, Edge -->
**Grid/Local**: <!-- e.g., Selenium Grid, Local -->
**Environment**: <!-- e.g., dev, test, prod -->

## Failure Characteristics

**Failure Type**:
- [ ] Consistent (fails every time)
- [ ] Flaky (fails intermittently)
- [ ] Environment-specific

**Frequency**: <!-- e.g., Always, 50% of the time, Only on Fridays -->

## Test Output

### Surefire Report
<!-- Attach or link to surefire report -->

Location: `target/surefire-reports/`

### Screenshots
<!-- If the test takes screenshots, attach them or note their location -->



## Investigation

### What I've Tried
<!-- List troubleshooting steps you've already attempted -->

- [ ] Ran test locally
- [ ] Checked for recent code changes
- [ ] Reviewed test logs
- [ ] Verified test data
- [ ] Checked environment configuration

### Findings
<!-- Share what you discovered during investigation -->



## Environment Details

**OS**: <!-- e.g., macOS, Windows, Linux -->
**Java Version**:
```bash
java -version
```

**Maven Version**:
```bash
./mvnw --version
```

**Dependencies**:
```bash
./mvnw dependency:tree | grep [relevant-dependency]
```

## Impact

**Severity**:
- [ ] Blocker - Blocks test execution
- [ ] Critical - Affects critical functionality
- [ ] Major - Affects important functionality
- [ ] Minor - Minor issue

**Tests Affected**: <!-- Number of tests failing -->

## Reproduction Rate
<!-- How often can you reproduce this failure? -->

- [ ] 100% - Every time
- [ ] 75% - Most of the time
- [ ] 50% - Half the time
- [ ] 25% - Occasionally
- [ ] <25% - Rarely

## Additional Context
<!-- Add any other context about the test failure here -->
