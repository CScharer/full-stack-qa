package com.cjs.qa.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.utilities.GuardedLogger;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Environment Configuration Utility for Java Tests
 *
 * <p>This utility reads environment-specific URLs and ports from the shared {@code
 * config/environments.json} file. This complements the existing XML-based configuration system
 * ({@code Configurations/Environments.xml}) which handles user-specific settings (browser,
 * timeouts, logging flags).
 *
 * <p><strong>Key Differences:</strong>
 *
 * <ul>
 *   <li><strong>XML Config</strong>: User-specific settings (browser preferences, timeouts,
 *       logging)
 *   <li><strong>JSON Config</strong>: Environment-specific URLs/ports (dev/test/prod)
 * </ul>
 *
 * <p><strong>Usage:</strong>
 *
 * <pre>{@code
 * // Get backend URL for current environment
 * String backendUrl = EnvironmentConfig.getBackendUrl();
 *
 * // Get frontend URL for specific environment
 * String frontendUrl = EnvironmentConfig.getFrontendUrl("test");
 *
 * // Get full environment configuration
 * EnvironmentConfig.EnvironmentInfo config = EnvironmentConfig.getEnvironmentConfig("prod");
 * }</pre>
 *
 * <p><strong>Environment Variable:</strong>
 *
 * <ul>
 *   <li>Uses {@code ENVIRONMENT} environment variable or system property (defaults to "dev")
 *   <li>Values: "dev", "test", "prod" (case-insensitive)
 * </ul>
 */
public final class EnvironmentConfig {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(EnvironmentConfig.class));

  private static final String CONFIG_FILE = "/config/environments.json";
  private static final String ENV_VAR_NAME = "ENVIRONMENT";
  private static final String DEFAULT_ENVIRONMENT = "dev";

  // Cache loaded config to avoid repeated file reads
  private static JsonObject configCache = null;

  private EnvironmentConfig() {
    // Utility class - do not instantiate
  }

  /**
   * Gets the current environment name from system property or environment variable.
   *
   * @return Environment name (dev, test, or prod), defaults to "dev"
   */
  public static String getCurrentEnvironment() {
    String env = System.getProperty(ENV_VAR_NAME);
    if (env == null || env.isEmpty()) {
      env = System.getenv(ENV_VAR_NAME);
    }
    if (env == null || env.isEmpty()) {
      return DEFAULT_ENVIRONMENT;
    }
    return env.toLowerCase(Locale.ENGLISH);
  }

  /**
   * Loads and caches the environments.json configuration file.
   *
   * @return JsonObject containing the full configuration
   * @throws RuntimeException if the config file cannot be loaded
   */
  private static JsonObject loadConfig() {
    if (configCache != null) {
      return configCache;
    }

    try (InputStream inputStream = EnvironmentConfig.class.getResourceAsStream(CONFIG_FILE)) {
      if (inputStream == null) {
        throw new RuntimeException(
            "Configuration file not found: "
                + CONFIG_FILE
                + ". Make sure config/environments.json is in the classpath.");
      }

      try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
        JsonElement jsonElement = JsonParser.parseReader(reader);
        configCache = jsonElement.getAsJsonObject();
        LOG.debug("Successfully loaded configuration from: {}", CONFIG_FILE);
        return configCache;
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to load configuration file: " + CONFIG_FILE, e);
    }
  }

  /**
   * Gets the environment configuration for a specific environment.
   *
   * @param environment Environment name (dev, test, prod) - case-insensitive
   * @return EnvironmentInfo containing URLs and ports
   * @throws RuntimeException if environment is not found (defaults to "dev")
   */
  public static EnvironmentInfo getEnvironmentConfig(String environment) {
    if (environment == null || environment.isEmpty()) {
      environment = getCurrentEnvironment();
    }
    environment = environment.toLowerCase(Locale.ENGLISH);

    JsonObject config = loadConfig();
    JsonObject environments = config.getAsJsonObject("environments");

    if (!environments.has(environment)) {
      LOG.warn("Environment '{}' not found in config, defaulting to 'dev'", environment);
      environment = DEFAULT_ENVIRONMENT;
    }

    JsonObject envConfig = environments.getAsJsonObject(environment);
    JsonObject backend = envConfig.getAsJsonObject("backend");
    JsonObject frontend = envConfig.getAsJsonObject("frontend");

    return new EnvironmentInfo(
        backend.get("url").getAsString(),
        backend.get("port").getAsInt(),
        frontend.get("url").getAsString(),
        frontend.get("port").getAsInt());
  }

  /**
   * Gets the backend URL for the current environment.
   *
   * @return Backend URL (e.g., "http://localhost:8003")
   */
  public static String getBackendUrl() {
    return getBackendUrl(getCurrentEnvironment());
  }

  /**
   * Gets the backend URL for a specific environment.
   *
   * @param environment Environment name (dev, test, prod) - case-insensitive
   * @return Backend URL (e.g., "http://localhost:8003")
   */
  public static String getBackendUrl(String environment) {
    return getEnvironmentConfig(environment).getBackendUrl();
  }

  /**
   * Gets the frontend URL for the current environment.
   *
   * @return Frontend URL (e.g., "http://localhost:3003")
   */
  public static String getFrontendUrl() {
    return getFrontendUrl(getCurrentEnvironment());
  }

  /**
   * Gets the frontend URL for a specific environment.
   *
   * @param environment Environment name (dev, test, prod) - case-insensitive
   * @return Frontend URL (e.g., "http://localhost:3003")
   */
  public static String getFrontendUrl(String environment) {
    return getEnvironmentConfig(environment).getFrontendUrl();
  }

  /**
   * Gets the backend port for the current environment.
   *
   * @return Backend port (e.g., 8003)
   */
  public static int getBackendPort() {
    return getBackendPort(getCurrentEnvironment());
  }

  /**
   * Gets the backend port for a specific environment.
   *
   * @param environment Environment name (dev, test, prod) - case-insensitive
   * @return Backend port (e.g., 8003)
   */
  public static int getBackendPort(String environment) {
    return getEnvironmentConfig(environment).getBackendPort();
  }

  /**
   * Gets the frontend port for the current environment.
   *
   * @return Frontend port (e.g., 3003)
   */
  public static int getFrontendPort() {
    return getFrontendPort(getCurrentEnvironment());
  }

  /**
   * Gets the frontend port for a specific environment.
   *
   * @param environment Environment name (dev, test, prod) - case-insensitive
   * @return Frontend port (e.g., 3003)
   */
  public static int getFrontendPort(String environment) {
    return getEnvironmentConfig(environment).getFrontendPort();
  }

  /** Immutable data class containing environment configuration information. */
  public static final class EnvironmentInfo {

    private final String backendUrl;
    private final int backendPort;
    private final String frontendUrl;
    private final int frontendPort;

    private EnvironmentInfo(
        String backendUrl, int backendPort, String frontendUrl, int frontendPort) {
      this.backendUrl = backendUrl;
      this.backendPort = backendPort;
      this.frontendUrl = frontendUrl;
      this.frontendPort = frontendPort;
    }

    public String getBackendUrl() {
      return backendUrl;
    }

    public int getBackendPort() {
      return backendPort;
    }

    public String getFrontendUrl() {
      return frontendUrl;
    }

    public int getFrontendPort() {
      return frontendPort;
    }

    @Override
    public String toString() {
      return String.format(
          "EnvironmentInfo{backendUrl='%s', backendPort=%d, frontendUrl='%s', frontendPort=%d}",
          backendUrl, backendPort, frontendUrl, frontendPort);
    }
  }
}
