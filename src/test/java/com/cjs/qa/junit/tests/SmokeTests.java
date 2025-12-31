package com.cjs.qa.junit.tests;

import java.net.URI;
import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;

import com.cjs.qa.utilities.AllureHelper;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.SeleniumGridConfig;

import io.qameta.allure.*;

/**
 * Smoke Test Suite - Fast, critical path verification
 *
 * <p>Purpose: Quick sanity checks before running full test suite Target: < 2 minutes total
 * execution time Coverage: Critical functionality only
 *
 * <p>Run with: ./scripts/run-smoke-tests.sh Or: docker-compose run --rm tests -Dtest=SmokeTests
 */
@Epic("Smoke Tests")
@Feature("Critical Path Verification")
public class SmokeTests {

  private static final GuardedLogger LOGGER =
      new GuardedLogger(LogManager.getLogger(SmokeTests.class));

  private WebDriver driver;

  @BeforeMethod
  public void setUp() throws Exception {
    LOGGER.info("\n========================================");
    LOGGER.info("🔥 SMOKE TEST - Fast Critical Path Check");

    String gridUrl = SeleniumGridConfig.getGridUrl();

    LOGGER.info("Grid URL: {}", gridUrl);
    LOGGER.info("========================================");

    // Fast setup - minimal options
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
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));

    LOGGER.info("✅ Driver initialized in {} mode", isHeadless ? "headless" : "headed");
  }

  @Test(priority = 1, groups = "smoke")
  @Story("Grid Infrastructure")
  @Severity(SeverityLevel.BLOCKER)
  @Description("Verify Selenium Grid is accessible and responsive")
  public void smokeTestGridConnection() {
    LOGGER.info("\n>>> Smoke Test 1: Grid Connection");

    Allure.step("Verify driver is initialized");
    Assert.assertNotNull(driver, "Driver should be initialized");

    Allure.step("Verify session ID exists");
    String sessionId = ((RemoteWebDriver) driver).getSessionId().toString();
    Assert.assertNotNull(sessionId, "Session ID should exist");
    LOGGER.info("Session ID: {}", sessionId);

    LOGGER.info("✅ Grid connection verified");
  }

  @Test(priority = 2, groups = "smoke")
  @Story("Core Navigation")
  @Severity(SeverityLevel.CRITICAL)
  @Description("Verify homepage loads successfully")
  public void smokeTestHomepageLoads() {
    LOGGER.info("\n>>> Smoke Test 2: Homepage Load");

    Allure.step("Navigate to Google homepage");
    driver.get("https://www.google.com");

    Allure.step("Verify page title");
    String title = driver.getTitle();
    LOGGER.info("Page title: {}", title);
    Assert.assertTrue(title.contains("Google"), "Title should contain 'Google'");

    Allure.step("Verify URL is correct");
    String currentUrl = driver.getCurrentUrl();
    Assert.assertTrue(currentUrl.contains("google.com"), "URL should contain google.com");

    LOGGER.info("✅ Homepage loaded successfully");
  }

  @Test(priority = 3, groups = "smoke")
  @Story("Core Functionality")
  @Severity(SeverityLevel.CRITICAL)
  @Description("Verify basic search functionality works")
  public void smokeTestSearchWorks() {
    LOGGER.info("\n>>> Smoke Test 3: Search Functionality");

    Allure.step("Navigate to Google");
    driver.get("https://www.google.com");

    final String initialUrl = driver.getCurrentUrl();

    Allure.step("Locate search box");
    WebElement searchBox = driver.findElement(By.name("q"));
    Assert.assertNotNull(searchBox, "Search box should exist");

    Allure.step("Enter search term");
    String searchTerm = "Selenium WebDriver";
    searchBox.sendKeys(searchTerm);

    Allure.step("Submit search");
    searchBox.sendKeys(Keys.RETURN);

    Allure.step("Wait for results");
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(initialUrl)));

    Allure.step("Verify URL changed");
    String resultUrl = driver.getCurrentUrl();
    Assert.assertNotEquals(resultUrl, initialUrl, "URL should change after search");
    Assert.assertTrue(resultUrl.contains("google"), "Should still be on Google");

    LOGGER.info("✅ Search functionality working");
  }

  @Test(priority = 4, groups = "smoke")
  @Story("Multi-Page Navigation")
  @Severity(SeverityLevel.CRITICAL)
  @Description("Verify navigation between multiple pages")
  public void smokeTestNavigationWorks() {
    LOGGER.info("\n>>> Smoke Test 4: Multi-Page Navigation");

    String[] sites = {"https://www.google.com", "https://github.com"};

    for (String site : sites) {
      Allure.step("Navigate to " + site);
      driver.get(site);

      Allure.step("Verify page loaded");
      String currentUrl = driver.getCurrentUrl();
      Assert.assertTrue(currentUrl.startsWith("http"), "Should have valid URL");
      LOGGER.info("  ✓ Loaded: {}", site);
    }

    LOGGER.info("✅ Multi-page navigation working");
  }

  @Test(priority = 5, groups = "smoke")
  @Story("Form Interaction")
  @Severity(SeverityLevel.NORMAL)
  @Description("Verify basic form input functionality")
  public void smokeTestFormInput() {
    LOGGER.info("\n>>> Smoke Test 5: Form Input");

    Allure.step("Navigate to Google");
    driver.get("https://www.google.com");

    Allure.step("Find search input");
    WebElement searchBox = driver.findElement(By.name("q"));

    Allure.step("Enter text");
    String testText = "Test Input";
    searchBox.sendKeys(testText);

    Allure.step("Verify text entered");
    String enteredValue = searchBox.getAttribute("value");
    Assert.assertTrue(enteredValue.contains(testText), "Text should be entered");

    Allure.step("Clear field");
    searchBox.clear();

    Allure.step("Verify field cleared");
    String clearedValue = searchBox.getAttribute("value");
    Assert.assertTrue(clearedValue.isEmpty() || clearedValue.isEmpty(), "Field should be cleared");

    LOGGER.info("✅ Form input working");
  }

  @AfterMethod
  public void tearDown(ITestResult result) {
    if (driver != null) {
      if (result.getStatus() == ITestResult.FAILURE) {
        LOGGER.error("❌ Smoke test failed - capturing evidence...");
        AllureHelper.captureScreenshot(driver, "SMOKE-FAILURE-" + result.getName());
        AllureHelper.attachPageSource(driver);
        AllureHelper.logBrowserInfo(driver);
      } else if (result.getStatus() == ITestResult.SUCCESS) {
        LOGGER.info("✅ Smoke test passed");
      }

      driver.quit();
      LOGGER.info("========================================\n");
    }
  }
}
