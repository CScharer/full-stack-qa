package com.cjs.qa.utilities;

/**
 * Centralized configuration helper for Selenium Grid.
 *
 * <p>This class provides a single place to resolve the Selenium Grid hub URL used by tests.
 *
 * <ul>
 *   <li>Primary source: {@code SELENIUM_REMOTE_URL} environment variable.
 *   <li>Fallback: {@code http://localhost:4444/wd/hub} (matches CI default).
 * </ul>
 */
public final class SeleniumGridConfig {

  private static final String ENV_VAR_NAME = "SELENIUM_REMOTE_URL";
  private static final String DEFAULT_GRID_URL = "http://localhost:4444/wd/hub";

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
}
