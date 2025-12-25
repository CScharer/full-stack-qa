# 🔄 Test Retry Logic Guide

**Last Updated**: 2025-11-14  
**Status**: Active

---

## Overview

**Test Retry Logic** automatically retries failed tests to reduce false failures from flaky tests, network issues, or timing problems. This improves test reliability and reduces maintenance burden.

**Benefits**:
- ✅ **Reduces false failures** - Handles transient issues automatically
- ✅ **Improves CI/CD stability** - Fewer pipeline failures from flaky tests
- ✅ **Configurable** - Set retry count per test or globally
- ✅ **Easy to use** - Simple annotation-based approach

---

## Quick Start

### Basic Usage

Add `retryAnalyzer = RetryAnalyzer.class` to your `@Test` annotation:

```java
import com.cjs.qa.utilities.RetryAnalyzer;
import org.testng.annotations.Test;

@Test(retryAnalyzer = RetryAnalyzer.class)
public void myFlakyTest() {
    // Test implementation
    // If this fails, it will be retried up to 3 times (default)
}
```

### Default Behavior

- **Default max retries**: 3
- **Retry only on failure**: Tests that pass on first attempt are not retried
- **Logging**: Each retry attempt is logged with attempt number

---

## Configuration

### Method 1: System Property (Recommended for CI/CD)

Set the maximum retry count via system property:

```bash
# Maven
./mvnw test -Dtest.retry.max.count=5

# Or in pom.xml surefire plugin configuration
<configuration>
    <systemPropertyVariables>
        <test.retry.max.count>5</test.retry.max.count>
    </systemPropertyVariables>
</configuration>
```

### Method 2: Programmatic Configuration

Set retry count in a `@BeforeSuite` or `@BeforeClass` method:

```java
import com.cjs.qa.utilities.RetryAnalyzer;
import org.testng.annotations.BeforeSuite;

public class MyTestSuite {
    @BeforeSuite
    public void setUpSuite() {
        RetryAnalyzer.setMaxRetryCount(5);
    }
}
```

### Method 3: Per-Test Configuration

Configure retry count before specific tests:

```java
@Test(retryAnalyzer = RetryAnalyzer.class)
public void importantTest() {
    RetryAnalyzer.setMaxRetryCount(5); // Set before test logic
    // Test implementation
}
```

**Note**: Setting retry count in `@Test` method affects all subsequent tests. Use `@BeforeSuite` or `@BeforeClass` for better control.

---

## Examples

### Example 1: Flaky Network Test

```java
@Test(retryAnalyzer = RetryAnalyzer.class)
public void testApiCall() {
    // This test might fail due to network issues
    Response response = apiClient.get("/api/users");
    Assert.assertEquals(response.getStatusCode(), 200);
}
```

**Behavior**: If test fails, it will be retried up to 3 times (default) before marking as failed.

### Example 2: Timing-Sensitive Test

```java
@Test(retryAnalyzer = RetryAnalyzer.class)
public void testElementAppears() {
    // Element might take time to appear
    WebElement element = driver.findElement(By.id("dynamic-element"));
    Assert.assertTrue(element.isDisplayed());
}
```

**Behavior**: Retries help handle cases where elements take longer to appear.

### Example 3: Custom Retry Count

```java
public class CriticalTests {
    @BeforeClass
    public void setUp() {
        // Critical tests get more retries
        RetryAnalyzer.setMaxRetryCount(5);
    }
    
    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void criticalTest() {
        // This test will retry up to 5 times
    }
}
```

### Example 4: Disable Retries

```java
@Test(retryAnalyzer = RetryAnalyzer.class)
public void testNoRetries() {
    RetryAnalyzer.setMaxRetryCount(0); // Disable retries
    // This test will not retry if it fails
}
```

---

## How It Works

1. **Test Execution**: Test runs normally
2. **On Failure**: `RetryAnalyzer.retry()` is called
3. **Retry Decision**: If retry count < max retries, test is retried
4. **Logging**: Each retry attempt is logged
5. **Final Result**: After max retries, test is marked as failed

**Log Output Example**:
```
2025-11-14 10:30:15 WARN  RetryAnalyzer - Retrying test: MyTestClass.myFlakyTest (Attempt 1/3)
2025-11-14 10:30:18 WARN  RetryAnalyzer - Retrying test: MyTestClass.myFlakyTest (Attempt 2/3)
2025-11-14 10:30:21 INFO  RetryAnalyzer - Test passed on attempt 3
```

---

## Best Practices

### ✅ DO

1. **Use for Flaky Tests**: Apply retry logic to tests that occasionally fail due to:
   - Network issues
   - Timing problems
   - Race conditions
   - External dependencies

2. **Set Appropriate Retry Count**: 
   - **1-2 retries**: For minor timing issues
   - **3-5 retries**: For network or external API calls
   - **Avoid excessive retries**: If test fails consistently, fix the root cause

3. **Log Retry Attempts**: Monitor retry logs to identify patterns:
   - Tests that always retry might indicate a real issue
   - Tests that pass on retry might need better waits

4. **Use System Properties for CI/CD**: Set retry count via system properties for different environments

### ❌ DON'T

1. **Don't Use as Band-Aid**: If a test consistently fails, fix the root cause instead of increasing retries

2. **Don't Retry Everything**: Only apply retry logic to tests that are known to be flaky

3. **Don't Set Excessive Retries**: More than 5 retries usually indicates a real problem

4. **Don't Retry Data-Dependent Tests**: Tests that modify data should not be retried (could cause side effects)

---

## API Reference

### RetryAnalyzer Class

**Methods**:

- `setMaxRetryCount(int count)` - Set maximum retry count programmatically
- `getMaxRetryCount()` - Get current maximum retry count
- `resetToDefault()` - Reset retry count to default (from system property or 3)

**System Property**:
- `test.retry.max.count` - Maximum number of retries (default: 3)

---

## Troubleshooting

### Retries Not Working

**Problem**: Tests are not retrying even with `retryAnalyzer` annotation

**Solutions**:
1. Verify `RetryAnalyzer.class` is imported correctly
2. Check that retry count is > 0: `RetryAnalyzer.getMaxRetryCount()`
3. Ensure TestNG version supports `IRetryAnalyzer` (should be fine with current version)

### Too Many Retries

**Problem**: Tests are retrying too many times, slowing down execution

**Solutions**:
1. Reduce retry count: `RetryAnalyzer.setMaxRetryCount(1)`
2. Use system property: `-Dtest.retry.max.count=1`
3. Remove retry analyzer from tests that don't need it

### Retries Not Logging

**Problem**: Can't see retry attempts in logs

**Solutions**:
1. Check log level is set to WARN or lower
2. Verify Log4j configuration includes RetryAnalyzer logger
3. Check console output for retry messages

---

## Integration with CI/CD

### GitHub Actions Example

```yaml
- name: Run tests with retry
  run: |
    ./mvnw test -Dtest.retry.max.count=3
```

### Jenkins Example

```groovy
sh './mvnw test -Dtest.retry.max.count=3'
```

### Different Environments

```bash
# Development: Fewer retries (faster feedback)
-Dtest.retry.max.count=1

# CI/CD: More retries (stability)
-Dtest.retry.max.count=3

# Production smoke tests: No retries (fail fast)
-Dtest.retry.max.count=0
```

---

## Related

- **TestNG Retry**: https://testng.org/doc/documentation-main.html#rerunning
- **Flaky Test Management**: Consider also using test retry at CI/CD level
- **Test Stability**: Retry logic is one tool - also consider:
  - Better waits (WebDriverWait)
  - Test isolation
  - Stable test data

---

## Examples in Codebase

- ✅ `RetryAnalyzer.java` - Main retry analyzer implementation
- ✅ `RetryAnalyzerTest.java` - Example usage and test verification

---

**Questions?** Check the code examples or create a GitHub issue!

