package com.cjs.qa.utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.json.JSONObject;

import com.cjs.qa.core.QAException;

/**
 * Utility class for validating Selenium Grid server version against client version.
 *
 * <p>This class provides version validation functionality to ensure Grid server and client versions
 * are compatible before attempting to connect.
 *
 * <p>Features:
 *
 * <ul>
 *   <li>Query Grid status endpoint to get server version
 *   <li>Compare with client version from Selenium library or pom.xml
 *   <li>Support configurable version tolerance (exact match, minor version, patch version)
 *   <li>Provide clear error messages for mismatches
 * </ul>
 */
public final class SeleniumGridVersionValidator {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SeleniumGridVersionValidator.class));

  private static final String STATUS_ENDPOINT = "/wd/hub/status";
  private static final String VERSION_PROPERTY_KEY = "selenium.version";
  private static final int CONNECTION_TIMEOUT_MS = 5000;
  private static final int READ_TIMEOUT_MS = 5000;

  /** Version tolerance levels for compatibility checking. */
  public enum VersionTolerance {
    /** Must match exactly (e.g., 4.39.0 == 4.39.0) */
    EXACT,
    /** Allow minor version differences (e.g., 4.39.0 == 4.40.0) */
    MINOR,
    /** Allow patch version differences (e.g., 4.39.0 == 4.39.1) */
    PATCH,
    /** Skip version validation */
    NONE,
  }

  private SeleniumGridVersionValidator() {
    // Utility class - do not instantiate
  }

  /**
   * Validates that the Selenium Grid server version matches the client version.
   *
   * @param gridUrl The Grid hub URL (e.g., "http://localhost:4444/wd/hub")
   * @throws QAException if version validation fails or Grid is not accessible
   */
  public static void validateVersion(String gridUrl) throws QAException {
    VersionTolerance tolerance = getVersionTolerance();
    if (tolerance == VersionTolerance.NONE) {
      LOG.info("Version validation skipped (tolerance set to NONE)");
      return;
    }

    String serverVersion = getGridServerVersion(gridUrl);
    String clientVersion = getClientVersion();

    if (LOG.isInfoEnabled()) {
      LOG.info("Validating Grid version compatibility:");
      LOG.info("  Server version: {}", serverVersion);
      LOG.info("  Client version: {}", clientVersion);
      LOG.info("  Tolerance: {}", tolerance);
    }

    if (!isVersionCompatible(serverVersion, clientVersion, tolerance)) {
      String errorMessage =
          String.format(
              "Selenium Grid server version (%s) does not match client version (%s) with tolerance %s",
              serverVersion, clientVersion, tolerance);
      LOG.error(errorMessage);
      throw new QAException(errorMessage);
    }

    LOG.info("✅ Version validation passed: {} == {}", serverVersion, clientVersion);
  }

  /**
   * Gets the Selenium Grid server version by querying the status endpoint.
   *
   * @param gridUrl The Grid hub URL
   * @return The server version string (e.g., "4.39.0")
   * @throws QAException if unable to connect to Grid or parse response
   */
  public static String getGridServerVersion(String gridUrl) throws QAException {
    try {
      // Construct status URL
      String statusUrl =
          gridUrl.endsWith("/")
              ? gridUrl + STATUS_ENDPOINT.substring(1)
              : gridUrl + STATUS_ENDPOINT;

      if (LOG.isDebugEnabled()) {
        LOG.debug("Querying Grid status endpoint: {}", statusUrl);
      }

      URL url = URI.create(statusUrl).toURL();
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setConnectTimeout(CONNECTION_TIMEOUT_MS);
      connection.setReadTimeout(READ_TIMEOUT_MS);

      int responseCode = connection.getResponseCode();
      if (responseCode != HttpURLConnection.HTTP_OK) {
        throw new QAException(
            String.format(
                "Failed to get Grid status: HTTP %d - %s",
                responseCode, connection.getResponseMessage()));
      }

      // Read response
      StringBuilder response = new StringBuilder();
      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          response.append(line);
        }
      }

      // Parse JSON response
      JSONObject jsonResponse = new JSONObject(response.toString());
      JSONObject value = jsonResponse.getJSONObject("value");
      String version = value.optString("version", null);

      if (version == null || version.isEmpty()) {
        throw new QAException("Grid status response does not contain version information");
      }

      if (LOG.isDebugEnabled()) {
        LOG.debug("Grid server version: {}", version);
      }

      return version;
    } catch (QAException e) {
      throw e;
    } catch (Exception e) {
      throw new QAException(
          String.format("Failed to get Grid server version from %s: %s", gridUrl, e.getMessage()),
          e);
    }
  }

  /**
   * Gets the Selenium client version from the library or pom.xml.
   *
   * @return The client version string (e.g., "4.39.0")
   */
  public static String getClientVersion() {
    // Try to get from system property first
    String version = System.getProperty(VERSION_PROPERTY_KEY);
    if (version != null && !version.isEmpty()) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Client version from system property: {}", version);
      }
      return version;
    }

    // Try to get from environment variable
    version = System.getenv("SELENIUM_VERSION");
    if (version != null && !version.isEmpty()) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Client version from environment variable: {}", version);
      }
      return version;
    }

    // Try to get from Selenium library properties
    try {
      Properties props = new Properties();
      props.load(
          SeleniumGridVersionValidator.class.getResourceAsStream(
              "/META-INF/maven/org.seleniumhq.selenium/selenium-java/pom.properties"));
      version = props.getProperty("version");
      if (version != null && !version.isEmpty()) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Client version from Selenium library: {}", version);
        }
        return version;
      }
    } catch (Exception e) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Could not read version from Selenium library properties: {}", e.getMessage());
      }
    }

    // Fallback: return default or read from pom.xml (would require Maven model parsing)
    // For now, log warning and return a placeholder
    LOG.warn(
        "Could not determine client version automatically. "
            + "Set SELENIUM_VERSION environment variable or selenium.version system property.");
    return "unknown";
  }

  /**
   * Checks if two versions are compatible based on the specified tolerance.
   *
   * @param serverVersion The Grid server version
   * @param clientVersion The client version
   * @param tolerance The version tolerance level
   * @return true if versions are compatible, false otherwise
   */
  public static boolean isVersionCompatible(
      String serverVersion, String clientVersion, VersionTolerance tolerance) {
    if (tolerance == VersionTolerance.NONE) {
      return true;
    }

    if (serverVersion == null || clientVersion == null) {
      return false;
    }

    if (serverVersion.equals(clientVersion)) {
      return true;
    }

    if (tolerance == VersionTolerance.EXACT) {
      return false;
    }

    // Parse version strings (format: MAJOR.MINOR.PATCH)
    String[] serverParts = serverVersion.split("\\.");
    String[] clientParts = clientVersion.split("\\.");

    if (serverParts.length < 2 || clientParts.length < 2) {
      // Invalid version format, require exact match
      return false;
    }

    try {
      int serverMajor = Integer.parseInt(serverParts[0]);
      int clientMajor = Integer.parseInt(clientParts[0]);
      int serverMinor = Integer.parseInt(serverParts[1]);
      int clientMinor = Integer.parseInt(clientParts[1]);

      // Major versions must match
      if (serverMajor != clientMajor) {
        return false;
      }

      if (tolerance == VersionTolerance.MINOR) {
        // Minor versions can differ
        return true;
      }

      if (tolerance == VersionTolerance.PATCH) {
        // Minor versions must match for patch tolerance
        if (serverMinor != clientMinor) {
          return false;
        }
        // Patch versions can differ
        return true;
      }
    } catch (NumberFormatException e) {
      LOG.warn("Invalid version format: server={}, client={}", serverVersion, clientVersion);
      return false;
    }

    return false;
  }

  /**
   * Gets the version tolerance from environment variable or system property.
   *
   * @return The version tolerance level
   */
  private static VersionTolerance getVersionTolerance() {
    String toleranceStr = System.getenv("SELENIUM_GRID_VERSION_TOLERANCE");
    if (toleranceStr == null || toleranceStr.isEmpty()) {
      toleranceStr = System.getProperty("selenium.grid.version.tolerance");
    }

    if (toleranceStr == null || toleranceStr.isEmpty()) {
      return VersionTolerance.EXACT; // Default to exact match
    }

    try {
      return VersionTolerance.valueOf(toleranceStr.toUpperCase());
    } catch (IllegalArgumentException e) {
      LOG.warn("Invalid version tolerance value: {}. Using default: EXACT", toleranceStr);
      return VersionTolerance.EXACT;
    }
  }
}
