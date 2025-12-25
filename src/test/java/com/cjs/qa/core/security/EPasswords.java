package com.cjs.qa.core.security;

import com.cjs.qa.utilities.SecureConfig;

/**
 * EPasswords - Enum for managing application passwords via Google Cloud Secret Manager.
 *
 * <p>All passwords are now stored securely in Google Cloud Secret Manager with AUTO_ prefix. This
 * enum references the secret keys, not the actual password values.
 *
 * <p>Migration Date: 2025-11-08 Total Secrets: 20 (updated 2025-11-14)
 *
 * @author CJS QA Team
 * @version 2.0 - Migrated to Google Cloud Secret Manager
 */
public enum EPasswords {
  BTSROBOT("AUTO_BTSROBOT_PASSWORD"),
  BTSROBOT_00("AUTO_BTSROBOT_00_PASSWORD"),
  BTSQA("AUTO_BTSQA_PASSWORD"),
  DROPBOX("AUTO_DROPBOX_PASSWORD"),
  EMAIL_GMAIL("AUTO_EMAIL_GMAIL_PASSWORD"),
  EMAIL_AOL("AUTO_EMAIL_AOL_PASSWORD"),
  EMAIL_MSN("AUTO_EMAIL_MSN_PASSWORD"),
  EMAIL_VIVIT("AUTO_EMAIL_VIVIT_PASSWORD"),
  EVERYONE_SOCIAL("AUTO_EVERYONE_SOCIAL_PASSWORD"),
  IADHS("AUTO_IADHS_PASSWORD"),
  LINKEDIN("AUTO_LINKEDIN_PASSWORD"),
  MARLBORO("AUTO_MARLBORO_PASSWORD"),
  MY_WELLMARK("AUTO_MY_WELLMARK_PASSWORD"),
  QB_VIVIT("AUTO_QB_VIVIT_PASSWORD"),
  UNITED("AUTO_UNITED_PASSWORD"),
  UNITED_SECURITY_QUESTIONS("AUTO_UNITED_SECURITY_QUESTIONS"),
  UNITED_SECURITY_ANSWERS("AUTO_UNITED_SECURITY_ANSWERS"),
  VIVIT("AUTO_VIVIT_PASSWORD"),
  VIVIT_GT_WEBINAR_PASSWORD("AUTO_VIVIT_GT_WEBINAR_PASSWORD"),
  PLURALSIGHT_TRAINING_PASSWORD("AUTO_PLURALSIGHT_TRAINING_PASSWORD");

  private final String secretKey;

  EPasswords(String secretKey) {
    this.secretKey = secretKey;
  }

  /**
   * Retrieves the password value from Google Cloud Secret Manager. Values are cached to improve
   * performance and reduce API calls.
   *
   * @return The password value retrieved from Secret Manager
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
