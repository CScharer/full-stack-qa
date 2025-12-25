package com.cjs.qa.utilities;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;

/**
 * SecureConfig - Utility class for retrieving secrets from Google Cloud Secret Manager.
 *
 * <p>This class provides a caching layer on top of GoogleCloud.getKeyValue() to improve performance
 * and reduce API calls to Secret Manager.
 *
 * <p>Usage: String password = SecureConfig.getPassword("AUTO_BTSQA_PASSWORD");
 *
 * @author CJS QA Team
 * @version 1.0
 * @since 2025-11-08
 */
public final class SecureConfig {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SecureConfig.class));

  private SecureConfig() {
    // Utility class - prevent instantiation
  }

  private static final String PROJECT_ID = "cscharer";
  private static final Map<String, String> CACHE = new ConcurrentHashMap<>();
  private static boolean cacheEnabled = true;

  /**
   * Retrieves a password/secret from Google Cloud Secret Manager. Results are cached to minimize
   * API calls.
   *
   * @param secretKey The name of the secret in Google Cloud (e.g., "AUTO_BTSQA_PASSWORD")
   * @return The secret value as a String
   * @throws RuntimeException if the secret cannot be retrieved
   */
  public static String getPassword(String secretKey) {
    if (secretKey == null || secretKey.trim().isEmpty()) {
      throw new IllegalArgumentException("Secret key cannot be null or empty");
    }

    // Check cache first
    if (cacheEnabled && CACHE.containsKey(secretKey)) {
      return CACHE.get(secretKey);
    }

    try {
      String value = GoogleCloud.getKeyValue(PROJECT_ID, secretKey);

      // Cache the result
      if (cacheEnabled) {
        CACHE.put(secretKey, value);
      }

      return value;
    } catch (IOException e) {
      String errorMsg = "Failed to fetch secret from Google Cloud Secret Manager: " + secretKey;
      LOG.error("ERROR: {}", errorMsg);
      throw new RuntimeException(errorMsg, e);
    }
  }

  /**
   * Retrieves a secret with a fallback value if the secret doesn't exist.
   *
   * @param secretKey The name of the secret
   * @param fallbackValue The value to return if secret retrieval fails
   * @return The secret value or fallback value
   */
  public static String getPasswordOrDefault(String secretKey, String fallbackValue) {
    try {
      return getPassword(secretKey);
    } catch (Exception e) {
      LOG.warn("WARNING: Failed to retrieve secret '{}', using fallback value", secretKey);
      return fallbackValue;
    }
  }

  /** Clears the secret cache. Useful for testing or forcing fresh secret retrieval. */
  public static void clearCache() {
    CACHE.clear();
    LOG.info("SecureConfig cache cleared. Size: {}", CACHE.size());
  }

  /**
   * Enables or disables caching.
   *
   * @param enabled true to enable caching, false to disable
   */
  public static void setCacheEnabled(boolean enabled) {
    cacheEnabled = enabled;
    if (!enabled) {
      clearCache();
    }
  }

  /**
   * Returns the current cache size.
   *
   * @return Number of cached secrets
   */
  public static int getCacheSize() {
    return CACHE.size();
  }

  /**
   * Preloads a secret into the cache without returning the value. Useful for warming up the cache
   * at application startup.
   *
   * @param secretKey The name of the secret to preload
   */
  public static void preloadSecret(String secretKey) {
    try {
      getPassword(secretKey);
      LOG.debug("Preloaded secret: {}", secretKey);
    } catch (Exception e) {
      LOG.warn("WARNING: Failed to preload secret: {}", secretKey);
    }
  }

  /**
   * Preloads multiple secrets into the cache.
   *
   * @param secretKeys Array of secret names to preload
   */
  public static void preloadSecrets(String... secretKeys) {
    for (String key : secretKeys) {
      preloadSecret(key);
    }
  }
}
