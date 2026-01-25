package com.cjs.qa.utilities;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

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
@Disabled("Windows-specific test - not compatible with Mac or Test Needs Updates")
public class SecureConfigTest {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SecureConfigTest.class));

  @Test
  public void testSecretRetrieval() {
    LOG.info("Testing SecureConfig.getPassword()...");

    String password = SecureConfig.getPassword("AUTO_BTSQA_PASSWORD");
    assertNotNull(password, "Password should not be null");
    assertFalse(password.isEmpty(), "Password should not be empty");
    assertTrue(password.length() >= 8, "Password should have minimum length");

    LOG.info("✅ SecureConfig.getPassword() test passed!");
  }

  @Test
  public void testEPasswordsIntegration() {
    LOG.info("Testing EPasswords enum integration...");

    String password = EPasswords.BTSQA.getValue();
    assertNotNull(password, "EPasswords should return a value");
    assertFalse(password.isEmpty(), "EPasswords value should not be empty");
    assertTrue(password.length() >= 8, "EPasswords should return valid password");

    LOG.info("✅ EPasswords integration test passed!");
  }

  @Test
  public void testCaching() {
    LOG.info("Testing SecureConfig caching...");

    // Clear cache
    SecureConfig.clearCache();
    assertEquals(0, SecureConfig.getCacheSize(), "Cache should be empty after clear");

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
    assertTrue(secondCallTime < firstCallTime, "Cached retrieval should be faster");

    LOG.info("  First call time: " + firstCallTime + "ms");
    LOG.info("  Second call time (cached): " + secondCallTime + "ms");
    LOG.info("✅ Caching test passed!");
  }

  @Test
  public void testMultiplePasswords() {
    LOG.info("Testing multiple password retrieval...");

    String btsqa = EPasswords.BTSQA.getValue();
    String linkedin = EPasswords.LINKEDIN.getValue();
    String dropbox = EPasswords.DROPBOX.getValue();

    assertNotNull(btsqa, "BTSQA password should not be null");
    assertNotNull(linkedin, "LinkedIn password should not be null");
    assertNotNull(dropbox, "Dropbox password should not be null");

    LOG.info("  BTSQA: " + (btsqa != null ? "✅" : "❌"));
    LOG.info("  LinkedIn: " + (linkedin != null ? "✅" : "❌"));
    LOG.info("  Dropbox: " + (dropbox != null ? "✅" : "❌"));
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
