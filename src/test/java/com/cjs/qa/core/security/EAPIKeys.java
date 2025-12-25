package com.cjs.qa.core.security;

import com.cjs.qa.utilities.SecureConfig;

/**
 * EAPIKeys - Enum for managing API keys and secrets via Google Cloud Secret Manager.
 *
 * <p>All API keys and secrets are stored securely in Google Cloud Secret Manager with AUTO_ prefix.
 * This enum references the secret keys, not the actual key/secret values.
 *
 * <p>Migration Date: 2025-11-14 Total Secrets: 6
 *
 * <p>Naming Convention: VIVIT_ prefix for Vivit-related API credentials
 *
 * @author CJS QA Team
 * @version 1.0 - Initial implementation
 * @since 2025-11-14
 */
public enum EAPIKeys {
  // GoToWebinar API credentials
  VIVIT_GT_WEBINAR_USER_ID("AUTO_VIVIT_GT_WEBINAR_USER_ID"),
  VIVIT_GT_WEBINAR_CONSUMER_KEY("AUTO_VIVIT_GT_WEBINAR_CONSUMER_KEY"),
  VIVIT_GT_WEBINAR_CONSUMER_SECRET("AUTO_VIVIT_GT_WEBINAR_CONSUMER_SECRET"),

  // YourMembership API credentials
  VIVIT_YM_API_KEY("AUTO_VIVIT_YM_API_KEY"),
  VIVIT_YM_API_SA_PASSCODE("AUTO_VIVIT_YM_API_SA_PASSCODE"),

  // Pluralsight Training credentials
  PLURALSIGHT_TRAINING_USERNAME("AUTO_PLURALSIGHT_TRAINING_USERNAME");

  private final String secretKey;

  EAPIKeys(String secretKey) {
    this.secretKey = secretKey;
  }

  /**
   * Retrieves the API key/secret value from Google Cloud Secret Manager. Values are cached to
   * improve performance and reduce API calls.
   *
   * @return The API key/secret value retrieved from Secret Manager
   * @throws RuntimeException if the secret cannot be retrieved
   */
  public String getValue() {
    return SecureConfig.getPassword(this.secretKey);
  }

  /**
   * Gets the secret key name (for reference/debugging).
   *
   * @return The secret key name in Google Cloud Secret Manager
   */
  public String getSecretKey() {
    return this.secretKey;
  }
}
