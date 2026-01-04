package com.cjs.qa.utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.core.QAException;

/**
 * Centralized configuration helper for Selenium Grid.
 *
 * <p>This class provides a single place to resolve the Selenium Grid hub URL used by tests and
 * perform validation checks.
 *
 * <ul>
 *   <li>Primary source: {@code SELENIUM_REMOTE_URL} environment variable.
 *   <li>Fallback: {@code http://localhost:4444/wd/hub} (matches CI default).
 * </ul>
 *
 * <p>Enhanced features:
 *
 * <ul>
 *   <li>Version validation against Grid server
 *   <li>Grid readiness/health checks
 *   <li>Configuration getters for retry parameters
 * </ul>
 */
public final class SeleniumGridConfig {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SeleniumGridConfig.class));

  private static final String ENV_VAR_NAME = "SELENIUM_REMOTE_URL";
  private static final String DEFAULT_GRID_URL = "http://localhost:4444/wd/hub";
  private static final String STATUS_ENDPOINT = "/wd/hub/status";
  private static final int CONNECTION_TIMEOUT_MS = 5000;
  private static final int READ_TIMEOUT_MS = 5000;

  private SeleniumGridConfig() {
    // Utility class - do not instantiate
  }

  /**
   * Resolve the Selenium Grid hub URL for tests.
   *
   * @return The grid URL from {@code SELENIUM_REMOTE_URL} if set and non-blank, otherwise the
   *     default {@value #DEFAULT_GRID_URL}.
   */
  public static String getGridUrl() {
    String gridUrl = System.getenv(ENV_VAR_NAME);
    if (gridUrl == null || gridUrl.isBlank()) {
      return DEFAULT_GRID_URL;
    }
    return gridUrl;
  }

  /**
   * Validates that the Selenium Grid server version matches the client version.
   *
   * <p>This method delegates to {@link SeleniumGridVersionValidator#validateVersion(String)}.
   *
   * @param gridUrl The Grid hub URL to validate
   * @throws QAException if version validation fails or Grid is not accessible
   */
  public static void validateGridVersion(String gridUrl) throws QAException {
    SeleniumGridVersionValidator.validateVersion(gridUrl);
  }

  /**
   * Checks if Selenium Grid is ready and accessible.
   *
   * <p>This method queries the Grid status endpoint to determine if the Grid is ready to accept
   * connections.
   *
   * @param gridUrl The Grid hub URL to check
   * @return true if Grid is ready, false otherwise
   * @throws QAException if unable to connect to Grid
   */
  public static boolean isGridReady(String gridUrl) throws QAException {
    try {
      // Construct status URL
      String statusUrl =
          gridUrl.endsWith("/")
              ? gridUrl + STATUS_ENDPOINT.substring(1)
              : gridUrl + STATUS_ENDPOINT;

      if (LOG.isDebugEnabled()) {
        LOG.debug("Checking Grid readiness at: {}", statusUrl);
      }

      URL url = URI.create(statusUrl).toURL();
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setConnectTimeout(CONNECTION_TIMEOUT_MS);
      connection.setReadTimeout(READ_TIMEOUT_MS);

      int responseCode = connection.getResponseCode();
      if (responseCode != HttpURLConnection.HTTP_OK) {
        if (LOG.isDebugEnabled()) {
          LOG.debug(
              "Grid status check returned HTTP {}: {}",
              responseCode,
              connection.getResponseMessage());
        }
        return false;
      }

      // Read and parse response to check if ready
      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          response.append(line);
        }

        // Check if response contains "ready": true
        String responseStr = response.toString();
        boolean isReady =
            responseStr.contains("\"ready\":true") || responseStr.contains("\"ready\" : true");

        if (LOG.isDebugEnabled()) {
          LOG.debug("Grid readiness check result: {}", isReady);
        }

        return isReady;
      }
    } catch (Exception e) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Grid readiness check failed: {}", e.getMessage());
      }
      return false;
    }
  }

  /**
   * Gets the maximum number of retry attempts for Grid connections.
   *
   * <p>This method delegates to {@link RetryableGridConnection#getMaxRetries()}.
   *
   * @return Maximum retry attempts
   */
  public static int getMaxRetries() {
    return RetryableGridConnection.getMaxRetries();
  }

  /**
   * Gets the base delay for exponential backoff in retry logic.
   *
   * <p>This method delegates to {@link RetryableGridConnection#getRetryBaseDelay()}.
   *
   * @return Base delay in milliseconds
   */
  public static long getRetryBaseDelay() {
    return RetryableGridConnection.getRetryBaseDelay();
  }

  /**
   * Gets the maximum delay for exponential backoff in retry logic.
   *
   * <p>This method delegates to {@link RetryableGridConnection#getRetryMaxDelay()}.
   *
   * @return Maximum delay in milliseconds
   */
  public static long getRetryMaxDelay() {
    return RetryableGridConnection.getRetryMaxDelay();
  }

  /**
   * Gets the total timeout for all retry attempts.
   *
   * <p>This method delegates to {@link RetryableGridConnection#getRetryTimeout()}.
   *
   * @return Timeout in milliseconds
   */
  public static long getRetryTimeout() {
    return RetryableGridConnection.getRetryTimeout();
  }
}
