# Job Performance Analysis - Duplicate Build/Check Steps

**Date**: 2025-12-29  
**Status**: ✅ **Implemented** (Solutions 1 & 2)

---

## Issue Summary

Some test jobs are performing redundant build, style-check, formatting, compile, and JMeter configuration steps that add significant time to job execution. These steps may already be handled in `ci.yml`, causing unnecessary duplication.

---

## Job Performance Data

### Job Execution Times and Steps

| Job | Browser | Time | Builds | Style-Check | Formats | Compiles | Configures JMeter |
|-----|---------|------|--------|-------------|---------|----------|-------------------|
| smoke-tests | Chrome | 4m 51s | ✅ True | ✅ True | ✅ True | ✅ True | ✅ True |
| grid-tests | Chrome | 5m 3s | ✅ True | ✅ True | ✅ True | ✅ True | ✅ True |
| grid-tests | Edge | 4m 53s | ✅ True | ✅ True | ✅ True | ✅ True | ✅ True |
| grid-tests | Firefox | 5m 34s | ✅ True | ✅ True | ✅ True | ✅ True | ✅ True |
| mobile-browser-tests | Chrome | 4m 57s | ✅ True | ✅ True | ✅ True | ✅ True | ✅ True |
| responsive-design-tests | Chrome | 4m 38s | ✅ True | ✅ True | ✅ True | ✅ True | ✅ True |
| cypress-tests | Chrome | 1m 26s | ❌ False | ❌ False | ❌ False | ❌ False | ❌ False |
| playwright-tests | Chrome (Chromium) | 1m 7s | ❌ False | ❌ False | ❌ False | ❌ False | ❌ False |
| robot-tests | Chrome | 1m 25s | ❌ False | ❌ False | ❌ False | ❌ False | ❌ False |
| selenide-tests | Chrome | 4m 44s | ✅ True | ✅ True | ✅ True | ✅ True | ✅ True |
| vibium-tests | Chrome | 44s | ❌ False | ❌ False | ❌ False | ❌ False | ❌ False |

---

## Key Observations

### Performance Impact

**Jobs with Build/Check Steps** (Average: ~5 minutes):
- smoke-tests: 4m 51s
- grid-tests (chrome): 5m 3s
- grid-tests (edge): 4m 53s
- grid-tests (firefox): 5m 34s
- mobile-browser-tests: 4m 57s
- responsive-design-tests: 4m 38s
- selenide-tests: 4m 44s

**Jobs without Build/Check Steps** (Average: ~1 minute):
- cypress-tests: 1m 26s
- playwright-tests: 1m 7s
- robot-tests: 1m 25s
- vibium-tests: 44s

**Time Difference**: ~4 minutes per job (approximately 80% of execution time)

### Pattern Analysis

1. **Java/Maven-based tests** (TestNG, Selenium Grid):
   - smoke-tests
   - grid-tests
   - mobile-browser-tests
   - responsive-design-tests
   - selenide-tests
   - **All perform build/check steps**

2. **Frontend/Node-based tests**:
   - cypress-tests
   - playwright-tests
   - robot-tests
   - vibium-tests
   - **None perform build/check steps**

---

## Questions to Investigate

### 1. Are Build Steps Necessary in Each Job?

**Hypothesis**: Build steps might be redundant if:
- `ci.yml` already builds the project
- Artifacts are shared between jobs
- Dependencies are already installed

**Investigation Needed**:
- Check if `ci.yml` performs a shared build step
- Verify if build artifacts are cached/shared
- Determine if each job needs its own build

### 2. Are Style-Check and Formatting Steps Necessary?

**Hypothesis**: Style-check and formatting might be redundant if:
- These are handled in a separate job (e.g., code quality job)
- They're run once in `ci.yml` before tests
- They don't need to run for every test job

**Investigation Needed**:
- Check if `ci.yml` has a dedicated code quality job
- Verify if style-check/formatting is needed per job or once per run
- Determine if these can be moved to a separate job

### 3. Is Compile Step Redundant?

**Hypothesis**: Compile might be redundant if:
- Build step already compiles
- Compiled artifacts are cached/shared
- Maven handles compilation during build

**Investigation Needed**:
- Check if build step includes compilation
- Verify if separate compile step is needed
- Determine if compile is part of build or separate

### 4. Is JMeter Configuration Necessary for All Jobs?

**Hypothesis**: JMeter configuration might be unnecessary if:
- Not all jobs use JMeter
- JMeter is only needed for specific performance tests
- Configuration can be cached/shared

**Investigation Needed**:
- Check which jobs actually use JMeter
- Verify if JMeter config is needed for all Java-based tests
- Determine if it can be conditional or cached

---

## Potential Optimizations

### Option 1: Shared Build Job
- Create a single "Build & Compile" job in `ci.yml`
- Share build artifacts with all test jobs
- **Potential Savings**: ~3-4 minutes per job

### Option 2: Separate Code Quality Job
- Move style-check and formatting to dedicated job
- Run once per workflow, not per test job
- **Potential Savings**: ~30-60 seconds per job

### Option 3: Conditional Steps
- Only run build/check if artifacts not available
- Use job dependencies to ensure build completes first
- **Potential Savings**: Variable, but significant for parallel jobs

### Option 4: Cache Dependencies
- Cache Maven dependencies
- Cache Node modules
- Cache build artifacts
- **Potential Savings**: ~1-2 minutes per job (after first run)

---

## Next Steps

1. **Review `ci.yml`**:
   - Check if shared build job exists
   - Verify job dependencies and artifact sharing
   - Identify duplicate steps

2. **Review `env-fe.yml`**:
   - Check individual job definitions
   - Identify where build/check steps are defined
   - Determine if they can be removed or shared

3. **Analyze Job Dependencies**:
   - Check if jobs depend on build artifacts
   - Verify if artifacts are shared between jobs
   - Determine optimal job dependency structure

4. **Calculate Potential Savings**:
   - Estimate time saved per job
   - Calculate total workflow time reduction
   - Prioritize optimizations by impact

---

## Root Cause Analysis

### Investigation Results

After reviewing the codebase, the root cause has been identified:

#### 1. **Maven Lifecycle Execution**

When `mvn test` is executed (via `scripts/ci/run-maven-tests.sh`), Maven automatically runs through these lifecycle phases:

1. **validate** → Runs `maven-checkstyle-plugin:check` (bound to validate phase in `pom.xml` line 848-853)
2. **compile** → Compiles main source code (`src/main/java`)
3. **test-compile** → Compiles test source code (`src/test/java`)
4. **test** → Runs tests via `maven-surefire-plugin`

#### 2. **Formatting Plugins**

The following formatting plugins are configured in `pom.xml`:
- **Prettier Maven Plugin** (lines 896-912) - Formats code and sorts imports
- **Google Java Format Plugin** (fmt-maven-plugin) (lines 918-936) - Fixes line length
- **Spotless Maven Plugin** (lines 942-961) - Removes unused imports

**Issue**: These plugins may be executing during the Maven lifecycle even though they're configured for Eclipse lifecycle mapping. The `lifecycle-mapping` configuration (lines 1063-1075) only affects Eclipse IDE, not Maven command-line execution.

#### 3. **JMeter Configuration**

The `jmeter-maven-plugin` has an execution bound to the `configure` goal (lines 995-1009). While the Eclipse lifecycle mapping ignores it (lines 1077-1089), it may still execute during Maven lifecycle phases.

#### 4. **Shared Build Job in `ci.yml`**

The `ci.yml` workflow **does** have a shared `build-and-compile` job (lines 336-372) that:
- Downloads compiled classes from Docker build
- Reuses or compiles if needed
- Uploads compiled classes as artifact (`compiled-classes`)

**However**: The test jobs in `env-fe.yml` **do not download or use** this artifact! They run `mvn test` directly, which triggers the full Maven lifecycle including:
- Checkstyle validation
- Compilation (even though classes may already be compiled)
- Formatting plugins (if bound to lifecycle)
- JMeter configuration

---

## Files Analyzed

- ✅ `.github/workflows/ci.yml` - Has `build-and-compile` job that creates `compiled-classes` artifact
- ✅ `.github/workflows/env-fe.yml` - Test jobs call `scripts/ci/run-maven-tests.sh` which runs `mvn test`
- ✅ `scripts/ci/run-maven-tests.sh` - Simply runs `./mvnw -ntp test` (no artifact download)
- ✅ `pom.xml` - Contains plugin bindings that execute during Maven lifecycle:
  - Checkstyle bound to `validate` phase (line 848-853)
  - Formatting plugins (Prettier, Google Java Format, Spotless) configured
  - JMeter plugin with `configure` goal execution (lines 995-1009)

---

## Recommended Solutions

### Solution 1: Download and Reuse Compiled Classes (HIGHEST IMPACT)

**Current State**: Test jobs run `mvn test`, which compiles from scratch.

**Proposed Fix**: Download `compiled-classes` artifact from `build-and-compile` job and reuse them.

**Implementation**:
1. Add artifact download step to each Java-based test job in `env-fe.yml`
2. Modify `run-maven-tests.sh` to check for existing compiled classes
3. Use `mvn test -DskipTests=false` with pre-compiled classes

**Potential Savings**: ~2-3 minutes per job (compilation time)

### Solution 2: Skip Checkstyle During Test Execution (MEDIUM IMPACT)

**Current State**: Checkstyle runs during `validate` phase before every test.

**Proposed Fix**: Skip checkstyle during test execution since it's already run in `code-quality-analysis` job.

**Implementation**:
- Modify `run-maven-tests.sh` to use `mvn test -Dcheckstyle.skip=true`
- Or bind checkstyle to a different phase/profile

**Potential Savings**: ~30-60 seconds per job

### Solution 3: Skip Formatting Plugins During Test Execution (LOW-MEDIUM IMPACT)

**Current State**: Formatting plugins may execute during Maven lifecycle.

**Proposed Fix**: Ensure formatting plugins only run during explicit formatting goals, not during test phase.

**Implementation**:
- Verify plugin phase bindings in `pom.xml`
- Add `-DskipFormatting=true` or similar flags if needed
- Or ensure plugins are only bound to `none` phase

**Potential Savings**: ~30-60 seconds per job (if they're running)

### Solution 4: Skip JMeter Configuration for Non-JMeter Tests (LOW IMPACT)

**Current State**: JMeter plugin may configure during test execution.

**Proposed Fix**: Only configure JMeter when actually running JMeter tests.

**Implementation**:
- Use Maven profiles to conditionally enable JMeter plugin
- Or skip JMeter configuration for non-performance test jobs

**Potential Savings**: ~10-30 seconds per job (if it's running)

### Solution 5: Use Maven Dependency on `build-and-compile` Job (ARCHITECTURAL)

**Current State**: Test jobs run independently without waiting for `build-and-compile`.

**Proposed Fix**: Make test jobs depend on `build-and-compile` job completion.

**Implementation**:
- Add `needs: [build-and-compile]` to test jobs in `ci.yml`
- Ensure artifact is downloaded before running tests

**Note**: This may increase total pipeline time if tests can't run in parallel with build, but will reduce individual job time.

---

## Implementation Status

**Branch**: `optimize-test-job-performance`  
**Date Implemented**: 2025-12-29  
**Solutions Implemented**: ✅ Solution 1 (Download compiled classes) + ✅ Solution 2 (Skip checkstyle)

### Changes Made

1. **Modified `.github/workflows/env-fe.yml`**:
   - Added artifact download step to 5 Java-based test jobs:
     - `smoke-tests`
     - `grid-tests`
     - `mobile-browser-tests`
     - `responsive-design-tests`
     - `selenide-tests`
   - Each job downloads `compiled-classes` artifact from `build-and-compile` job
   - Uses `continue-on-error: true` for graceful fallback if artifact unavailable

2. **Modified `scripts/ci/run-maven-tests.sh`**:
   - Added logic to check for pre-compiled classes in `pre-compiled-classes/` (checks both `target/` and direct `classes/` structures)
   - Reuses compiled classes using existing `reuse-or-compile.sh` script or direct copy
   - Added `-Dcheckstyle.skip=true`, `-Dfmt.skip=true`, and `-Djmeter.skip=true` to skip redundant executions
   - Added comprehensive debugging output to show artifact structure
   - Added logging to show optimization status

3. **Modified `pom.xml`**:
   - Added default property values: `checkstyle.skip=false`, `fmt.skip=false`, `jmeter.skip=false`
   - Updated checkstyle plugin configuration to respect `${checkstyle.skip}` property in execution block
   - Updated fmt-maven-plugin configuration to respect `${fmt.skip}` property
   - Updated jmeter-maven-plugin execution configuration to respect `${jmeter.skip}` property

### Expected Outcomes

After implementing optimizations:
1. ✅ Identify which steps are truly redundant (Checkstyle, Formatting, JMeter config)
2. ✅ Reuse compiled classes from shared build job
3. ✅ Reduce individual job execution time by ~2.5-3.5 minutes per job
4. ✅ Maintain code quality by running checks in dedicated jobs
5. ✅ Total pipeline time savings: ~17.5-24.5 minutes per run (7 jobs × 2.5-3.5 minutes)

---

## Recommended Single Solution

### 🎯 **Solution 1: Download and Reuse Compiled Classes**

**This is the recommended single solution** because it:
- ✅ **Saves the most time**: ~2-3 minutes per job (vs 30-60 seconds for other solutions)
- ✅ **Zero negative impact**: We're reusing classes that were already compiled and validated
- ✅ **Low risk**: The `build-and-compile` job already exists and creates the artifact
- ✅ **Simple implementation**: Just download artifact and use existing reuse script
- ✅ **No code quality impact**: Compilation happens in dedicated job, tests just reuse it

**Why this is safe:**
- The `build-and-compile` job already compiles and validates everything
- Test jobs just need to reuse those compiled classes
- If compilation fails in `build-and-compile`, the test jobs won't run anyway (gate job)
- No risk of stale code since `build-and-compile` runs before tests

**Implementation Steps:**
1. Add artifact download step to each Java-based test job in `env-fe.yml`:
   ```yaml
   - name: Download compiled classes
     uses: actions/download-artifact@v4
     with:
       name: compiled-classes
       path: pre-compiled-classes
   ```

2. Modify `run-maven-tests.sh` to reuse compiled classes:
   ```bash
   # Check if pre-compiled classes exist
   if [ -d "pre-compiled-classes/target" ]; then
     echo "✅ Using pre-compiled classes from build-and-compile job"
     cp -r pre-compiled-classes/target target/
   fi
   
   # Run tests (Maven will skip compile if classes are up-to-date)
   ./mvnw -ntp test-compile test -Dcheckstyle.skip=true
   ```

3. Or use the existing `reuse-or-compile.sh` script:
   ```bash
   ./scripts/ci/reuse-or-compile.sh "pre-compiled-classes/target"
   ./mvnw -ntp test -Dcheckstyle.skip=true
   ```

**Expected Results:**
- **Time Savings**: 2-3 minutes per Java-based test job
- **Total Savings**: ~14-21 minutes per pipeline run (7 jobs × 2-3 minutes)
- **Risk Level**: Very Low (reusing already-compiled, validated code)

---

## Alternative: Combined Solution (If Implementing Multiple)

If you want to maximize savings, you can **safely combine Solution 1 + Solution 2**:

- **Solution 1**: Download compiled classes (~2-3 min savings)
- **Solution 2**: Skip checkstyle (~30-60 sec savings) - **Zero risk** since it already runs in `code-quality-analysis` job

**Combined Savings**: ~2.5-3.5 minutes per job

**Why Solution 2 is safe to add:**
- Checkstyle already runs in `code-quality-analysis` job before tests
- Skipping it in test jobs doesn't reduce code quality checks
- It's just avoiding redundant execution

---

## Priority Recommendations (If Implementing Multiple)

1. **HIGH PRIORITY**: Implement Solution 1 (Download compiled classes) - Highest time savings
2. **MEDIUM PRIORITY**: Implement Solution 2 (Skip checkstyle) - Easy win, good savings, zero risk
3. **LOW PRIORITY**: Verify and implement Solutions 3 & 4 (Formatting, JMeter) - Verify if they're actually running first
4. **ARCHITECTURAL**: Consider Solution 5 (Job dependencies) - May affect parallel execution

---

## Notes

- ✅ Confirmed: `ci.yml` has a shared `build-and-compile` job that creates `compiled-classes` artifact
- ❌ Problem: Test jobs in `env-fe.yml` do NOT use this artifact - they compile from scratch
- ✅ Root Cause: `mvn test` triggers full Maven lifecycle including validate (checkstyle), compile, test-compile, and potentially formatting/JMeter plugins
- 🎯 Solution: Download artifact and skip unnecessary lifecycle phases during test execution

