package com.cjs.qa.utilities;

import org.apache.logging.log4j.LogManager;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Test Retry Analyzer for TestNG
 *
 * <p>Automatically retries failed tests up to a configurable maximum number of attempts. This helps
 * reduce false failures from flaky tests, network issues, or timing problems.
 *
 * <p>Usage:
 *
 * <pre>{@code
 * @Test(retryAnalyzer = RetryAnalyzer.class)
 * public void flakyTest() {
 *     // Test implementation
 * }
 *
 * // Or with custom retry count:
 * @Test(retryAnalyzer = RetryAnalyzer.class)
 * public void importantTest() {
 *     RetryAnalyzer.setMaxRetryCount(5); // Before test runs
 *     // Test implementation
 * }
 * }</pre>
 *
 * <p>Configuration:
 *
 * <ul>
 *   <li>Default max retries: 3 (configurable via system property or method call)
 *   <li>System property: {@code test.retry.max.count} (e.g., {@code -Dtest.retry.max.count=5})
 * </ul>
 */
public class RetryAnalyzer implements IRetryAnalyzer {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(RetryAnalyzer.class));
  private static final String MAX_RETRY_PROPERTY = "test.retry.max.count";
  private static final int DEFAULT_MAX_RETRY = 3;

  private static int maxRetryCount = getMaxRetryFromProperty();

  private int retryCount = 0;

  /**
   * Gets the maximum retry count from system property or default value.
   *
   * @return Maximum number of retries
   */
  private static int getMaxRetryFromProperty() {
    String property = System.getProperty(MAX_RETRY_PROPERTY);
    if (property != null && !property.trim().isEmpty()) {
      try {
        int count = Integer.parseInt(property.trim());
        if (count >= 0) {
          LOG.info("Retry count set from system property: {}", count);
          return count;
        }
      } catch (NumberFormatException e) {
        LOG.warn(
            "Invalid retry count in system property '{}': {}. Using default: {}",
            MAX_RETRY_PROPERTY,
            property,
            DEFAULT_MAX_RETRY);
      }
    }
    return DEFAULT_MAX_RETRY;
  }

  /**
   * Sets the maximum retry count programmatically.
   *
   * <p>Note: This should be called before tests run, ideally in a @BeforeSuite or @BeforeClass
   * method.
   *
   * @param count Maximum number of retries (must be >= 0)
   */
  public static void setMaxRetryCount(int count) {
    if (count < 0) {
      LOG.warn("Retry count cannot be negative. Setting to 0.");
      maxRetryCount = 0;
    } else {
      maxRetryCount = count;
      LOG.info("Retry count set programmatically: {}", maxRetryCount);
    }
  }

  /**
   * Gets the current maximum retry count.
   *
   * @return Maximum number of retries
   */
  public static int getMaxRetryCount() {
    return maxRetryCount;
  }

  /** Resets the retry count to default (from system property or DEFAULT_MAX_RETRY). */
  public static void resetToDefault() {
    maxRetryCount = getMaxRetryFromProperty();
    LOG.info("Retry count reset to default: {}", maxRetryCount);
  }

  @Override
  public boolean retry(ITestResult result) {
    if (maxRetryCount <= 0) {
      return false; // Retries disabled
    }

    if (retryCount < maxRetryCount) {
      retryCount++;
      String testName = result.getMethod().getMethodName();
      String className = result.getTestClass().getName();
      LOG.warn(
          "Retrying test: {}.{} (Attempt {}/{})", className, testName, retryCount, maxRetryCount);
      return true;
    }

    // Max retries reached
    retryCount = 0; // Reset for next test
    return false;
  }

  /**
   * Gets the current retry count for this test instance.
   *
   * @return Current retry attempt number
   */
  public int getRetryCount() {
    return retryCount;
  }

  /** Resets the retry count for this test instance. */
  public void reset() {
    retryCount = 0;
  }
}
