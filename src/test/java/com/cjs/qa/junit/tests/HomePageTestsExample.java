package com.cjs.qa.junit.tests;

import org.apache.logging.log4j.LogManager;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.cjs.qa.config.EnvironmentConfig;
import com.cjs.qa.utilities.GuardedLogger;
import com.codeborne.selenide.Configuration;

import io.qameta.allure.*;

/**
 * Example test demonstrating usage of EnvironmentConfig utility
 *
 * <p>This is an example file showing how to use the shared config/environments.json via
 * EnvironmentConfig utility. This complements the existing XML-based config system for
 * user-specific settings.
 *
 * <p><strong>Note:</strong> This is an example file. The actual HomePageTests.java can be updated
 * to use EnvironmentConfig if desired.
 */
@Epic("HomePage Tests - Example")
@Feature("HomePage Navigation - Using Shared Config")
public class HomePageTestsExample {

  private static final GuardedLogger LOGGER =
      new GuardedLogger(LogManager.getLogger(HomePageTestsExample.class));

  @BeforeMethod
  public void setUp() {
    // Configure Selenide
    Configuration.browser = "chrome";
    Configuration.headless = !"false".equalsIgnoreCase(System.getProperty("headless", "true"));
    Configuration.timeout = 5000;
    Configuration.pageLoadTimeout = 10000;
    Configuration.browserSize = "1920x1080";

    // Use shared config/environments.json via EnvironmentConfig
    // Priority: 1) baseUrl system property, 2) BASE_URL env var, 3) Shared config
    String baseUrl = System.getProperty("baseUrl");
    if (baseUrl == null || baseUrl.isEmpty()) {
      baseUrl = System.getenv("BASE_URL");
    }
    if (baseUrl == null || baseUrl.isEmpty()) {
      // Use shared config based on ENVIRONMENT env var (defaults to "dev")
      baseUrl = EnvironmentConfig.getFrontendUrl();
      LOGGER.info("Using frontend URL from shared config: {}", baseUrl);
    }
    Configuration.baseUrl = baseUrl;

    LOGGER.info("HomePage test setup complete. Base URL: {}", Configuration.baseUrl);
  }

  @Test
  @Story("HomePage Display")
  @Description("Example test demonstrating EnvironmentConfig usage")
  @Severity(SeverityLevel.MINOR)
  public void testEnvironmentConfigUsage() {
    // Example: Get URLs from shared config
    String frontendUrl = EnvironmentConfig.getFrontendUrl();
    String backendUrl = EnvironmentConfig.getBackendUrl();

    LOGGER.info("Frontend URL from shared config: {}", frontendUrl);
    LOGGER.info("Backend URL from shared config: {}", backendUrl);

    // Example: Get URLs for specific environment
    String testFrontendUrl = EnvironmentConfig.getFrontendUrl("test");
    String prodBackendUrl = EnvironmentConfig.getBackendUrl("prod");

    LOGGER.info("Test environment frontend URL: {}", testFrontendUrl);
    LOGGER.info("Prod environment backend URL: {}", prodBackendUrl);

    // Verify URLs are not empty
    Assert.assertNotNull(frontendUrl, "Frontend URL should not be null");
    Assert.assertNotNull(backendUrl, "Backend URL should not be null");
    Assert.assertTrue(frontendUrl.startsWith("http"), "Frontend URL should be a valid HTTP URL");
    Assert.assertTrue(backendUrl.startsWith("http"), "Backend URL should be a valid HTTP URL");
  }
}
