package com.cjs.qa.utilities;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.core.QAException;

/**
 * Utility methods for testing Selenium Grid functionality.
 *
 * <p>This class provides helper methods for common Grid testing scenarios, including waiting for
 * Grid readiness, version validation, and health checks.
 *
 * <p>These utilities are designed to be used in test setup methods and test cases that need to
 * verify Grid state before or during test execution.
 */
public final class GridTestUtils {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(GridTestUtils.class));

  private static final int DEFAULT_TIMEOUT_SECONDS = 30;
  private static final int DEFAULT_CHECK_INTERVAL_SECONDS = 1;

  private GridTestUtils() {
    // Utility class - do not instantiate
  }

  /**
   * Waits for Selenium Grid to be ready and accessible.
   *
   * <p>This method polls the Grid status endpoint until it returns a ready state or the timeout is
   * reached.
   *
   * @param gridUrl The Grid hub URL (e.g., "http://localhost:4444/wd/hub")
   * @param timeoutSeconds Maximum time to wait in seconds
   * @return true if Grid becomes ready within timeout, false otherwise
   */
  public static boolean waitForGridReady(String gridUrl, int timeoutSeconds) {
    long startTime = System.currentTimeMillis();
    long timeoutMs = TimeUnit.SECONDS.toMillis(timeoutSeconds);

    if (LOG.isInfoEnabled()) {
      LOG.info("Waiting for Grid to be ready at: {} (timeout: {}s)", gridUrl, timeoutSeconds);
    }

    while (System.currentTimeMillis() - startTime < timeoutMs) {
      try {
        if (SeleniumGridConfig.isGridReady(gridUrl)) {
          long elapsed = System.currentTimeMillis() - startTime;
          LOG.info("✅ Grid is ready (took {}ms)", elapsed);
          return true;
        }
      } catch (QAException e) {
        // Grid not ready yet, continue waiting
        if (LOG.isDebugEnabled()) {
          LOG.debug("Grid not ready yet: {}", e.getMessage());
        }
      }

      try {
        Thread.sleep(TimeUnit.SECONDS.toMillis(DEFAULT_CHECK_INTERVAL_SECONDS));
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        LOG.warn("Wait for Grid interrupted");
        return false;
      }
    }

    LOG.warn("❌ Grid did not become ready within {}s", timeoutSeconds);
    return false;
  }

  /**
   * Waits for Selenium Grid to be ready using default timeout.
   *
   * @param gridUrl The Grid hub URL
   * @return true if Grid becomes ready, false otherwise
   */
  public static boolean waitForGridReady(String gridUrl) {
    return waitForGridReady(gridUrl, DEFAULT_TIMEOUT_SECONDS);
  }

  /**
   * Validates that the Selenium Grid server version matches the expected version.
   *
   * <p>This method delegates to {@link SeleniumGridVersionValidator#validateVersion(String)} but
   * provides a test-friendly interface.
   *
   * @param gridUrl The Grid hub URL
   * @param expectedVersion The expected Selenium version (e.g., "4.39.0")
   * @throws QAException if version validation fails
   */
  public static void validateGridVersion(String gridUrl, String expectedVersion)
      throws QAException {
    // Set expected version as system property for validation
    String originalVersion = System.getProperty("selenium.version");
    try {
      System.setProperty("selenium.version", expectedVersion);
      SeleniumGridVersionValidator.validateVersion(gridUrl);
      LOG.info("✅ Grid version validation passed: {}", expectedVersion);
    } finally {
      // Restore original value
      if (originalVersion != null) {
        System.setProperty("selenium.version", originalVersion);
      } else {
        System.clearProperty("selenium.version");
      }
    }
  }

  /**
   * Gets the current status of the Selenium Grid.
   *
   * <p>This method queries the Grid status endpoint and returns a formatted status string.
   *
   * @param gridUrl The Grid hub URL
   * @return Status information string, or error message if unable to query
   */
  public static String getGridStatus(String gridUrl) {
    try {
      boolean isReady = SeleniumGridConfig.isGridReady(gridUrl);
      String serverVersion = SeleniumGridVersionValidator.getGridServerVersion(gridUrl);
      return String.format(
          "Grid Status: %s, Server Version: %s", isReady ? "Ready" : "Not Ready", serverVersion);
    } catch (QAException e) {
      return String.format("Grid Status: Error - %s", e.getMessage());
    }
  }

  /**
   * Checks if Selenium Grid is healthy and ready to accept connections.
   *
   * <p>This method performs a comprehensive health check including readiness and version
   * validation.
   *
   * @param gridUrl The Grid hub URL
   * @return true if Grid is healthy, false otherwise
   */
  public static boolean isGridHealthy(String gridUrl) {
    try {
      // Check if Grid is ready
      if (!SeleniumGridConfig.isGridReady(gridUrl)) {
        LOG.warn("Grid is not ready");
        return false;
      }

      // Check if version can be retrieved (indicates Grid is responding)
      String version = SeleniumGridVersionValidator.getGridServerVersion(gridUrl);
      if (version == null || version.isEmpty()) {
        LOG.warn("Could not retrieve Grid version");
        return false;
      }

      LOG.info("✅ Grid is healthy (version: {})", version);
      return true;
    } catch (QAException e) {
      LOG.warn("Grid health check failed: {}", e.getMessage());
      return false;
    }
  }
}
