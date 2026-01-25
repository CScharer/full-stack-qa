package com.cjs.qa.junit.tests;

import java.net.URI;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import com.cjs.qa.utilities.AllureHelper;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.SeleniumGridConfig;

import io.qameta.allure.*;

/**
 * Data-Driven Tests
 *
 * <p>Demonstrates parameterized testing with multiple data sets Tests the same scenario with
 * different inputs
 *
 * <p>Coverage: - Search functionality with multiple queries - URL validation across different
 * domains - Browser title verification
 */
@Epic("Extended Test Coverage")
@Feature("Data-Driven Tests")
public class DataDrivenTests {

  private static final GuardedLogger LOGGER =
      new GuardedLogger(LogManager.getLogger(DataDrivenTests.class));

  private WebDriver driver;

  @BeforeMethod
  public void setUp() throws Exception {
    LOGGER.info("========================================");
    LOGGER.info("🔢 DATA-DRIVEN TEST SETUP");

    String gridUrl = SeleniumGridConfig.getGridUrl();

    LOGGER.info("Grid URL: {}", gridUrl);
    LOGGER.info("========================================");

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
    LOGGER.info("✅ Driver initialized in {} mode", isHeadless ? "headless" : "headed");
    LOGGER.info("✅ Driver initialized");
  }

  @DataProvider(name = "searchQueries")
  public Object[][] searchQueriesProvider() {
    // Using JSONDataProvider to read from external file
    // This allows test data to be updated without code changes
    return com.cjs.qa.utilities.JSONDataProvider.readJSONArray(
        "test-data/search-queries.json", "queries");
  }

  @Test(dataProvider = "searchQueries")
  @Story("Search Functionality")
  @Severity(SeverityLevel.CRITICAL)
  @Description("Test Google search with multiple search terms")
  public void testSearchWithMultipleQueries(String searchTerm, boolean shouldSucceed) {
    LOGGER.info("\n>>> Test: Search for '{}'", searchTerm);

    Allure.step("Navigate to Google");
    driver.get("https://www.google.com");

    Allure.step("Find search box");
    WebElement searchBox = driver.findElement(By.name("q"));

    Allure.step("Enter search term: " + searchTerm);
    searchBox.sendKeys(searchTerm);
    LOGGER.info("Entered: {}", searchTerm);

    Allure.step("Submit search");
    searchBox.submit();

    // Wait for results
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      LOGGER.warn("Sleep interrupted", e);
    }

    String currentUrl = driver.getCurrentUrl();
    LOGGER.info("Results URL: {}", currentUrl);

    // DEBUG: Screenshot capture commented out - only capture on failure
    // AllureHelper.captureScreenshot(driver, "Search-" + searchTerm.replace(" ", "-"));

    if (shouldSucceed) {
      Assert.assertTrue(
          currentUrl.contains("google.com"), "Should still be on Google after search");
      LOGGER.info("✅ Search for '{}' successful", searchTerm);
    }
  }

  @DataProvider(name = "websiteUrls")
  public Object[][] websiteUrlsProvider() {
    return new Object[][] {
      {"https://www.google.com", "Google"},
      {"https://github.com", "GitHub"},
      {"https://www.wikipedia.org", "Wikipedia"},
      {"https://www.w3.org", "W3C"},
      {"https://www.bing.com", "Bing"},
    };
  }

  @Test(dataProvider = "websiteUrls")
  @Story("Website Accessibility")
  @Severity(SeverityLevel.NORMAL)
  @Description("Verify multiple websites are accessible and load correctly")
  public void testWebsiteAccessibility(String url, String expectedTitleFragment) {
    LOGGER.info("\n>>> Test: Accessing {}", url);

    Allure.step("Navigate to: " + url);
    driver.get(url);

    Allure.step("Verify page loaded");
    String title = driver.getTitle();
    String currentUrl = driver.getCurrentUrl();

    LOGGER.info("Title: {}", title);
    LOGGER.info("URL: {}", currentUrl);

    // DEBUG: Screenshot capture commented out - only capture on failure
    // AllureHelper.captureScreenshot(driver, "Website-" + expectedTitleFragment);

    Allure.step("Verify title contains expected text");
    Assert.assertTrue(
        title
            .toLowerCase(Locale.ENGLISH)
            .contains(expectedTitleFragment.toLowerCase(Locale.ENGLISH)),
        "Title should contain '" + expectedTitleFragment + "'");

    Allure.step("Verify URL is correct");
    Assert.assertTrue(currentUrl.startsWith("http"), "URL should be valid");

    LOGGER.info("✅ {} loaded successfully", expectedTitleFragment);
  }

  @DataProvider(name = "invalidSearches")
  public Object[][] invalidSearchesProvider() {
    return new Object[][] {
      {""}, // Empty search
      {"   "}, // Whitespace only
      {"!@#$%^&*()"}, // Special characters
      {"12345678901234567890123456789012345678901234567890"}, // Very long
    };
  }

  @Test(dataProvider = "invalidSearches")
  @Story("Edge Cases")
  @Severity(SeverityLevel.MINOR)
  @Description("Test search behavior with invalid/edge case inputs")
  public void testSearchEdgeCases(String searchTerm) {
    LOGGER.info("\n>>> Test: Edge case search with: '{}'", searchTerm);

    Allure.step("Navigate to Google");
    driver.get("https://www.google.com");

    Allure.step("Find search box");
    WebElement searchBox = driver.findElement(By.name("q"));

    Allure.step("Enter edge case input");
    searchBox.sendKeys(searchTerm);
    LOGGER.info("Input: '{}'", searchTerm.isEmpty() ? "[EMPTY]" : searchTerm);

    // Don't submit empty searches
    if (!searchTerm.trim().isEmpty()) {
      Allure.step("Submit search");
      searchBox.submit();

      try {
        Thread.sleep(1500);
      } catch (InterruptedException e) {
        LOGGER.warn("Sleep interrupted", e);
      }
    }

    // DEBUG: Screenshot capture commented out - only capture on failure
    // AllureHelper.captureScreenshot(
    //     driver, "EdgeCase-" + (searchTerm.isEmpty() ? "Empty" : "Special"));

    // Verify we're still on a valid page
    String currentUrl = driver.getCurrentUrl();
    Assert.assertTrue(
        currentUrl.startsWith("http"), "Should have valid URL even with edge case input");

    LOGGER.info("✅ Edge case handled gracefully");
  }

  @AfterMethod
  public void tearDown(ITestResult result) {
    if (driver != null) {
      if (result.getStatus() == ITestResult.FAILURE) {
        LOGGER.error("❌ Data-driven test failed");
        AllureHelper.captureScreenshot(driver, "FAILURE-" + result.getName());
        AllureHelper.attachPageSource(driver);
      } else if (result.getStatus() == ITestResult.SUCCESS) {
        LOGGER.info("✅ Data-driven test passed");
      }

      LOGGER.info("Closing browser...");
      driver.quit();
      LOGGER.info("========================================\n");
    }
  }
}
