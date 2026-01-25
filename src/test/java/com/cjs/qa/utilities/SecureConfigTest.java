package com.cjs.qa.utilities;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.cjs.qa.core.security.EPasswords;

/**
 * Test class for SecureConfig integration with Google Cloud Secret Manager.
 *
 * <p>This test verifies that: 1. SecureConfig can retrieve secrets from Google Cloud 2. EPasswords
 * enum works with Secret Manager 3. Caching is working correctly
 *
 * @author CJS QA Team
 * @version 1.0
 */
public class SecureConfigTest {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SecureConfigTest.class));

  private boolean credentialsAvailable = false;

  @BeforeEach
  public void checkCredentialsAvailability() {
    // Clear cache to ensure we're testing with fresh credentials
    SecureConfig.clearCache();

    try {
      // Try to get a password - this will fail if credentials aren't available
      SecureConfig.getPassword("AUTO_BTSQA_PASSWORD");
      credentialsAvailable = true;
      LOG.info("Google Cloud credentials available - using real credentials");
    } catch (RuntimeException e) {
      if (e.getMessage() != null
          && (e.getMessage().contains("default credentials were not found")
              || e.getMessage().contains("Failed to fetch secret"))) {
        LOG.warn(
            "Google Cloud credentials not available - using mocked responses: {}", e.getMessage());
        credentialsAvailable = false;
      } else {
        // Re-throw if it's a different error
        throw e;
      }
    } catch (Exception e) {
      // Catch any other exceptions (like IOException from Google Cloud)
      if (e.getMessage() != null
          && (e.getMessage().contains("default credentials were not found")
              || e.getMessage().contains("Failed to fetch secret")
              || e.getMessage().contains("credentials were not found"))) {
        LOG.warn(
            "Google Cloud credentials not available - using mocked responses: {}", e.getMessage());
        credentialsAvailable = false;
      } else {
        // Re-throw if it's a different error
        throw new RuntimeException(e);
      }
    }
  }

  @Test
  public void testSecretRetrieval() {
    LOG.info("Testing SecureConfig.getPassword()...");

    if (credentialsAvailable) {
      // Use real credentials
      String password = SecureConfig.getPassword("AUTO_BTSQA_PASSWORD");
      assertNotNull(password, "Password should not be null");
      assertFalse(password.isEmpty(), "Password should not be empty");
      assertTrue(password.length() >= 8, "Password should have minimum length");
    } else {
      // Mock Google Cloud Secret Manager calls
      try (MockedStatic<GoogleCloud> mockedGoogleCloud = Mockito.mockStatic(GoogleCloud.class)) {
        mockedGoogleCloud
            .when(() -> GoogleCloud.getKeyValue("cscharer", "AUTO_BTSQA_PASSWORD"))
            .thenReturn("mock-btsqa-password-12345678");

        String password = SecureConfig.getPassword("AUTO_BTSQA_PASSWORD");
        assertNotNull(password, "Password should not be null");
        assertFalse(password.isEmpty(), "Password should not be empty");
        assertTrue(password.length() >= 8, "Password should have minimum length");
      }
    }

    LOG.info("✅ SecureConfig.getPassword() test passed!");
  }

  @Test
  public void testEPasswordsIntegration() {
    LOG.info("Testing EPasswords enum integration...");

    if (credentialsAvailable) {
      // Use real credentials
      String password = EPasswords.BTSQA.getValue();
      assertNotNull(password, "EPasswords should return a value");
      assertFalse(password.isEmpty(), "EPasswords value should not be empty");
      assertTrue(password.length() >= 8, "EPasswords should return valid password");
    } else {
      // Mock Google Cloud Secret Manager calls
      try (MockedStatic<GoogleCloud> mockedGoogleCloud = Mockito.mockStatic(GoogleCloud.class)) {
        mockedGoogleCloud
            .when(() -> GoogleCloud.getKeyValue("cscharer", "AUTO_BTSQA_PASSWORD"))
            .thenReturn("mock-btsqa-password-12345678");

        String password = EPasswords.BTSQA.getValue();
        assertNotNull(password, "EPasswords should return a value");
        assertFalse(password.isEmpty(), "EPasswords value should not be empty");
        assertTrue(password.length() >= 8, "EPasswords should return valid password");
      }
    }

    LOG.info("✅ EPasswords integration test passed!");
  }

  @Test
  public void testCaching() {
    LOG.info("Testing SecureConfig caching...");

    if (credentialsAvailable) {
      // Clear cache
      SecureConfig.clearCache();
      assertEquals(0, SecureConfig.getCacheSize(), "Cache should be empty after clear");
      // Use real credentials
      // First retrieval (should hit Secret Manager)
      long startTime = System.currentTimeMillis();
      String password1 = SecureConfig.getPassword("AUTO_BTSQA_PASSWORD");
      final long firstCallTime = System.currentTimeMillis() - startTime;

      assertEquals(1, SecureConfig.getCacheSize(), "Cache should have 1 item");

      // Second retrieval (should use cache - much faster)
      startTime = System.currentTimeMillis();
      String password2 = SecureConfig.getPassword("AUTO_BTSQA_PASSWORD");
      long secondCallTime = System.currentTimeMillis() - startTime;

      assertEquals(password1, password2, "Cached password should match");
      // Cached retrieval should be equal or faster (allowing for timing variations)
      assertTrue(
          secondCallTime <= firstCallTime,
          "Cached retrieval should be equal or faster (first: "
              + firstCallTime
              + "ms, second: "
              + secondCallTime
              + "ms)");

      LOG.info("  First call time: " + firstCallTime + "ms");
      LOG.info("  Second call time (cached): " + secondCallTime + "ms");
    } else {
      // Clear cache before mocking
      SecureConfig.clearCache();
      assertEquals(0, SecureConfig.getCacheSize(), "Cache should be empty after clear");

      // Mock Google Cloud Secret Manager calls
      try (MockedStatic<GoogleCloud> mockedGoogleCloud = Mockito.mockStatic(GoogleCloud.class)) {
        mockedGoogleCloud
            .when(() -> GoogleCloud.getKeyValue("cscharer", "AUTO_BTSQA_PASSWORD"))
            .thenReturn("mock-btsqa-password-12345678");

        // First retrieval (should hit Secret Manager)
        long startTime = System.currentTimeMillis();
        String password1 = SecureConfig.getPassword("AUTO_BTSQA_PASSWORD");
        final long firstCallTime = System.currentTimeMillis() - startTime;

        assertEquals(1, SecureConfig.getCacheSize(), "Cache should have 1 item");

        // Second retrieval (should use cache - much faster)
        startTime = System.currentTimeMillis();
        String password2 = SecureConfig.getPassword("AUTO_BTSQA_PASSWORD");
        long secondCallTime = System.currentTimeMillis() - startTime;

        assertEquals(password1, password2, "Cached password should match");
        // Note: Timing comparison may not be reliable with mocked responses (both calls are very
        // fast)
        // For mocked responses, we verify caching works by checking cache size and value equality
        if (firstCallTime > 0) {
          // Only check timing if first call took measurable time
          assertTrue(
              secondCallTime <= firstCallTime,
              "Cached retrieval should be equal or faster (first: "
                  + firstCallTime
                  + "ms, second: "
                  + secondCallTime
                  + "ms)");
        }

        LOG.info("  First call time: " + firstCallTime + "ms");
        LOG.info("  Second call time (cached): " + secondCallTime + "ms");
      }
    }

    LOG.info("✅ Caching test passed!");
  }

  @Test
  public void testMultiplePasswords() {
    LOG.info("Testing multiple password retrieval...");

    if (credentialsAvailable) {
      // Use real credentials
      String btsqa = EPasswords.BTSQA.getValue();
      String linkedin = EPasswords.LINKEDIN.getValue();
      String dropbox = EPasswords.DROPBOX.getValue();

      assertNotNull(btsqa, "BTSQA password should not be null");
      assertNotNull(linkedin, "LinkedIn password should not be null");
      assertNotNull(dropbox, "Dropbox password should not be null");

      LOG.info("  BTSQA: " + (btsqa != null ? "✅" : "❌"));
      LOG.info("  LinkedIn: " + (linkedin != null ? "✅" : "❌"));
      LOG.info("  Dropbox: " + (dropbox != null ? "✅" : "❌"));
    } else {
      // Mock Google Cloud Secret Manager calls
      try (MockedStatic<GoogleCloud> mockedGoogleCloud = Mockito.mockStatic(GoogleCloud.class)) {
        mockedGoogleCloud
            .when(() -> GoogleCloud.getKeyValue("cscharer", "AUTO_BTSQA_PASSWORD"))
            .thenReturn("mock-btsqa-password-12345678");
        mockedGoogleCloud
            .when(() -> GoogleCloud.getKeyValue("cscharer", "AUTO_LINKEDIN_PASSWORD"))
            .thenReturn("mock-linkedin-password-12345678");
        mockedGoogleCloud
            .when(() -> GoogleCloud.getKeyValue("cscharer", "AUTO_DROPBOX_PASSWORD"))
            .thenReturn("mock-dropbox-password-12345678");

        String btsqa = EPasswords.BTSQA.getValue();
        String linkedin = EPasswords.LINKEDIN.getValue();
        String dropbox = EPasswords.DROPBOX.getValue();

        assertNotNull(btsqa, "BTSQA password should not be null");
        assertNotNull(linkedin, "LinkedIn password should not be null");
        assertNotNull(dropbox, "Dropbox password should not be null");

        LOG.info("  BTSQA: " + (btsqa != null ? "✅" : "❌"));
        LOG.info("  LinkedIn: " + (linkedin != null ? "✅" : "❌"));
        LOG.info("  Dropbox: " + (dropbox != null ? "✅" : "❌"));
      }
    }

    LOG.info("✅ Multiple password test passed!");
  }

  @Test
  public void testGetSecretKey() {
    LOG.info("Testing EPasswords.getSecretKey()...");

    String secretKey = EPasswords.BTSQA.getSecretKey();
    assertEquals("AUTO_BTSQA_PASSWORD", secretKey, "Secret key should match");

    LOG.info("✅ getSecretKey() test passed!");
  }
}
