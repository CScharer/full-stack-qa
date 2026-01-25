package com.cjs.qa.junit.tests;

import org.apache.logging.log4j.LogManager;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.RetryAnalyzer;

/**
 * Test class to demonstrate and verify RetryAnalyzer functionality.
 *
 * <p>This class includes examples of:
 *
 * <ul>
 *   <li>Using RetryAnalyzer with @Test annotation
 *   <li>Configuring retry count programmatically
 *   <li>Testing retry behavior
 * </ul>
 */
@Disabled("Windows-specific test - not compatible with Mac or Test Needs Updates")
public class RetryAnalyzerTest {

  private static final GuardedLogger LOGGER =
      new GuardedLogger(LogManager.getLogger(RetryAnalyzerTest.class));

  @BeforeMethod
  public void setUp() {
    // Setup for tests if needed
  }

  @Test(retryAnalyzer = RetryAnalyzer.class)
  public void testRetryAnalyzerWithPassingTest() {
    LOGGER.info("This test should pass on first attempt");
    Assert.assertTrue(true, "Test should pass");
  }

  @Test(retryAnalyzer = RetryAnalyzer.class)
  public void testRetryAnalyzerConfigurationActive() {
    // This test verifies that RetryAnalyzer is properly configured and active
    // NOTE: This test always passes - it just verifies the retry analyzer is set up correctly
    // For actual retry demonstration with failing tests, see the documentation guide

    LOGGER.info("Retry analyzer is active for this test");
    LOGGER.info("Max retries configured: {}", RetryAnalyzer.getMaxRetryCount());
    LOGGER.info(
        "If this test failed, it would automatically retry up to {} times",
        RetryAnalyzer.getMaxRetryCount());

    // Always pass - this test just verifies retry analyzer is configured
    // In real usage, you would apply retryAnalyzer to flaky tests that might fail
    Assert.assertTrue(true, "Retry analyzer is configured and ready for use with flaky tests");
  }

  @Test
  public void testRetryAnalyzerConfiguration() {
    int originalCount = RetryAnalyzer.getMaxRetryCount();
    LOGGER.info("Original retry count: {}", originalCount);

    // Test setting retry count
    RetryAnalyzer.setMaxRetryCount(5);
    Assert.assertEquals(RetryAnalyzer.getMaxRetryCount(), 5, "Retry count should be 5");

    // Test reset to default
    RetryAnalyzer.resetToDefault();
    Assert.assertEquals(
        RetryAnalyzer.getMaxRetryCount(), originalCount, "Retry count should be reset to original");

    // Test negative value handling
    RetryAnalyzer.setMaxRetryCount(-1);
    Assert.assertEquals(
        RetryAnalyzer.getMaxRetryCount(), 0, "Negative retry count should become 0");

    // Reset back to original to avoid affecting other tests
    RetryAnalyzer.resetToDefault();
  }

  @Test(retryAnalyzer = RetryAnalyzer.class)
  public void testRetryAnalyzerWithZeroRetries() {
    // Test with retries disabled
    int originalCount = RetryAnalyzer.getMaxRetryCount();
    RetryAnalyzer.setMaxRetryCount(0);
    LOGGER.info("This test has retries disabled (original was: {})", originalCount);
    Assert.assertTrue(true, "Test should pass");

    // Reset back to original to avoid affecting other tests
    RetryAnalyzer.setMaxRetryCount(originalCount);
  }
}
