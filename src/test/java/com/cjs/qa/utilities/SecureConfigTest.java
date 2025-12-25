package com.cjs.qa.utilities;

import static org.junit.Assert.*;

import org.apache.logging.log4j.LogManager;
import org.junit.Test;

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

  @Test
  public void testSecretRetrieval() {
    LOG.info("Testing SecureConfig.getPassword()...");

    String password = SecureConfig.getPassword("AUTO_BTSQA_PASSWORD");
    assertNotNull("Password should not be null", password);
    assertFalse("Password should not be empty", password.isEmpty());
    assertTrue("Password should have minimum length", password.length() >= 8);

    LOG.info("✅ SecureConfig.getPassword() test passed!");
  }

  @Test
  public void testEPasswordsIntegration() {
    LOG.info("Testing EPasswords enum integration...");

    String password = EPasswords.BTSQA.getValue();
    assertNotNull("EPasswords should return a value", password);
    assertFalse("EPasswords value should not be empty", password.isEmpty());
    assertTrue("EPasswords should return valid password", password.length() >= 8);

    LOG.info("✅ EPasswords integration test passed!");
  }

  @Test
  public void testCaching() {
    LOG.info("Testing SecureConfig caching...");

    // Clear cache
    SecureConfig.clearCache();
    assertEquals("Cache should be empty after clear", 0, SecureConfig.getCacheSize());

    // First retrieval (should hit Secret Manager)
    long startTime = System.currentTimeMillis();
    String password1 = SecureConfig.getPassword("AUTO_BTSQA_PASSWORD");
    final long firstCallTime = System.currentTimeMillis() - startTime;

    assertEquals("Cache should have 1 item", 1, SecureConfig.getCacheSize());

    // Second retrieval (should use cache - much faster)
    startTime = System.currentTimeMillis();
    String password2 = SecureConfig.getPassword("AUTO_BTSQA_PASSWORD");
    long secondCallTime = System.currentTimeMillis() - startTime;

    assertEquals("Cached password should match", password1, password2);
    assertTrue("Cached retrieval should be faster", secondCallTime < firstCallTime);

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

    assertNotNull("BTSQA password should not be null", btsqa);
    assertNotNull("LinkedIn password should not be null", linkedin);
    assertNotNull("Dropbox password should not be null", dropbox);

    LOG.info("  BTSQA: " + (btsqa != null ? "✅" : "❌"));
    LOG.info("  LinkedIn: " + (linkedin != null ? "✅" : "❌"));
    LOG.info("  Dropbox: " + (dropbox != null ? "✅" : "❌"));
    LOG.info("✅ Multiple password test passed!");
  }

  @Test
  public void testGetSecretKey() {
    LOG.info("Testing EPasswords.getSecretKey()...");

    String secretKey = EPasswords.BTSQA.getSecretKey();
    assertEquals("Secret key should match", "AUTO_BTSQA_PASSWORD", secretKey);

    LOG.info("✅ getSecretKey() test passed!");
  }
}
