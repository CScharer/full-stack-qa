package com.cjs.qa.junit.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.SeleniumGridConfig;

/** Simple test to verify Grid connection and basic Selenium functionality */
public class GridConnectionTest {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(GridConnectionTest.class));

  private WebDriver driver;

  @BeforeEach
  void setUp(TestInfo testInfo) throws Exception {
    // Resolve Selenium Grid URL (with sensible default)
    String gridUrl = SeleniumGridConfig.getGridUrl();

    LOG.info("Connecting to Grid at: {}", gridUrl);

    ChromeOptions options = new ChromeOptions();

    // Check if headless mode is requested (default: true)
    String headlessProperty = System.getProperty("headless", "true");
    boolean isHeadless = !"false".equalsIgnoreCase(headlessProperty);

    if (isHeadless) {
      options.addArguments("--headless");
    }
    options.addArguments("--no-sandbox");
    options.addArguments("--disable-dev-shm-usage");
    options.addArguments("--disable-gpu");

    driver = new RemoteWebDriver(URI.create(gridUrl).toURL(), options);
    LOG.info("✅ Driver initialized in {} mode", isHeadless ? "headless" : "headed");
    LOG.info("Successfully connected to Grid!");
  }

  @Test
  void testGridConnection(TestInfo testInfo) {
    assertNotNull(driver, "Driver should be initialized");
    LOG.info("✅ Grid connection test PASSED");
  }

  @Test
  void testNavigateToGoogle(TestInfo testInfo) throws Exception {
    LOG.info("Navigating to Google...");
    driver.get("https://www.google.com");

    String title = driver.getTitle();
    LOG.info("Page title: {}", title);

    assertTrue(title.contains("Google"), "Title should contain 'Google'");
    LOG.info("✅ Google navigation test PASSED");
  }

  @Test
  void testNavigateToGitHub(TestInfo testInfo) throws Exception {
    LOG.info("Navigating to GitHub...");
    driver.get("https://github.com");

    String title = driver.getTitle();
    LOG.info("Page title: {}", title);

    assertTrue(title.contains("GitHub"), "Title should contain 'GitHub'");
    LOG.info("✅ GitHub navigation test PASSED");
  }

  @AfterEach
  void tearDown() {
    if (driver != null) {
      LOG.info("Closing browser...");
      driver.quit();
      LOG.info("Browser closed successfully");
    }
  }
}
