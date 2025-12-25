package com.cjs.qa.junit.tests;

import static org.junit.Assert.*;

import java.net.URI;

import org.apache.logging.log4j.LogManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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

  @Before
  public void setUp() throws Exception {
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
  public void testGridConnection() {
    assertNotNull("Driver should be initialized", driver);
    LOG.info("✅ Grid connection test PASSED");
  }

  @Test
  public void testNavigateToGoogle() throws Exception {
    LOG.info("Navigating to Google...");
    driver.get("https://www.google.com");

    String title = driver.getTitle();
    LOG.info("Page title: {}", title);

    assertTrue("Title should contain 'Google'", title.contains("Google"));
    LOG.info("✅ Google navigation test PASSED");
  }

  @Test
  public void testNavigateToGitHub() throws Exception {
    LOG.info("Navigating to GitHub...");
    driver.get("https://github.com");

    String title = driver.getTitle();
    LOG.info("Page title: {}", title);

    assertTrue("Title should contain 'GitHub'", title.contains("GitHub"));
    LOG.info("✅ GitHub navigation test PASSED");
  }

  @After
  public void tearDown() {
    if (driver != null) {
      LOG.info("Closing browser...");
      driver.quit();
      LOG.info("Browser closed successfully");
    }
  }
}
