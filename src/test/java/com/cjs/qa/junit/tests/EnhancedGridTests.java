package com.cjs.qa.junit.tests;

import java.net.URI;
import java.time.Duration;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
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

/** Enhanced Grid tests demonstrating multi-browser support and advanced scenarios */
@Epic("Selenium Grid Testing")
@Feature("Enhanced Grid Tests")
public class EnhancedGridTests {

  private static final GuardedLogger LOGGER =
      new GuardedLogger(LogManager.getLogger(EnhancedGridTests.class));

  private WebDriver driver;
  private String currentBrowser;

  @BeforeMethod
  @Parameters("browser")
  public void setUp(@Optional("chrome") String browser) throws Exception {
    currentBrowser = browser;
    String gridUrl = SeleniumGridConfig.getGridUrl();

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("\n========================================");
      LOGGER.info("Test Browser: {}", browser.toUpperCase(Locale.ENGLISH));
      LOGGER.info("Grid URL: {}", gridUrl);
      LOGGER.info("========================================");
    }

    // Check if headless mode is requested (default: true)
    String headlessProperty = System.getProperty("headless", "true");
    boolean isHeadless = !"false".equalsIgnoreCase(headlessProperty);

    if ("firefox".equalsIgnoreCase(browser)) {
      FirefoxOptions options = new FirefoxOptions();
      if (isHeadless) {
        options.addArguments("--headless");
      }
      driver = new RemoteWebDriver(URI.create(gridUrl).toURL(), options);
    } else {
      // Default to Chrome
      ChromeOptions options = new ChromeOptions();
      if (isHeadless) {
        options.addArguments("--headless");
      }
      options.addArguments("--no-sandbox");
      options.addArguments("--disable-dev-shm-usage");
      options.addArguments("--disable-gpu");
      driver = new RemoteWebDriver(URI.create(gridUrl).toURL(), options);
    }

    LOGGER.info("✅ Driver initialized in {} mode", isHeadless ? "headless" : "headed");

    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("✅ {} browser initialized\n", currentBrowser.toUpperCase(Locale.ENGLISH));
    }
  }

  @Test(priority = 1, description = "Verify Google homepage loads correctly")
  @Story("Homepage Navigation")
  @Severity(SeverityLevel.CRITICAL)
  @Description("Verify that Google homepage loads successfully and contains expected elements")
  public void testGoogleHomepage() {
    LOGGER.info(">>> Test: Google Homepage");
    Allure.step("Navigate to Google homepage");
    driver.get("https://www.google.com");
    AllureHelper.captureScreenshot(driver, "Google-Homepage");

    String title = driver.getTitle();
    LOGGER.info("Page title: {}", title);

    Assert.assertTrue(title.contains("Google"), "Title should contain 'Google'");
    Assert.assertTrue(
        driver.getCurrentUrl().contains("google.com"), "URL should contain google.com");

    LOGGER.info("✅ Google homepage loaded successfully");
  }

  @Test(priority = 2, description = "Perform a Google search and verify results")
  @Story("Search Functionality")
  @Severity(SeverityLevel.CRITICAL)
  @Description("Verify Google search functionality with proper waits and URL verification")
  public void testGoogleSearch() {
    LOGGER.info(">>> Test: Google Search");
    Allure.step("Navigate to Google homepage");
    driver.get("https://www.google.com");

    final String initialUrl = driver.getCurrentUrl();

    // Find search box and enter search term
    WebElement searchBox = driver.findElement(By.name("q"));
    String searchTerm = "Selenium";
    Allure.step("Enter search term: " + searchTerm);
    searchBox.sendKeys(searchTerm);
    LOGGER.info("Entered search term: {}", searchTerm);

    // Submit search
    Allure.step("Submit search");
    searchBox.sendKeys(Keys.RETURN);

    // Wait for URL to change (indicates search was performed)
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(initialUrl)));

    // Verify URL changed
    String currentUrl = driver.getCurrentUrl();
    LOGGER.info("Results URL: {}", currentUrl);
    AllureHelper.captureScreenshot(driver, "Google-Search-Results");

    Assert.assertNotEquals(currentUrl, initialUrl, "URL should have changed after search");
    Assert.assertTrue(currentUrl.contains("google.com"), "Should still be on Google");

    LOGGER.info("✅ Search completed successfully");
  }

  @Test(priority = 3, description = "Verify GitHub homepage and search")
  @Story("Homepage Navigation")
  @Severity(SeverityLevel.NORMAL)
  @Description("Verify GitHub homepage loads and logo is present")
  public void testGitHubHomepage() {
    LOGGER.info(">>> Test: GitHub Homepage");
    Allure.step("Navigate to GitHub homepage");
    driver.get("https://github.com");

    String title = driver.getTitle();
    LOGGER.info("Page title: {}", title);

    Assert.assertTrue(title.contains("GitHub"), "Title should contain 'GitHub'");

    // Verify GitHub logo is present
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement logo =
        wait.until(
            ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(
                    "[aria-label*='Homepage'], .octicon-mark-github,"
                        + " [data-testid='github-logo']")));

    AllureHelper.captureScreenshot(driver, "GitHub-Homepage");

    Assert.assertNotNull(logo, "GitHub logo should be present");
    LOGGER.info("✅ GitHub homepage verified");
  }

  @Test(priority = 4, description = "Test navigation between multiple sites")
  @Story("Multi-Site Navigation")
  @Severity(SeverityLevel.NORMAL)
  @Description("Verify ability to navigate between multiple websites in same session")
  public void testMultiSiteNavigation() {
    LOGGER.info(">>> Test: Multi-Site Navigation");

    // Navigate to multiple sites
    String[] sites = {"https://www.google.com", "https://github.com", "https://www.wikipedia.org"};

    for (String site : sites) {
      driver.get(site);
      String currentUrl = driver.getCurrentUrl();
      LOGGER.info("Navigated to: {}", currentUrl);

      Assert.assertTrue(
          currentUrl.contains(site.replace("https://www.", "").replace("https://", "")),
          "URL should match expected site");
    }

    LOGGER.info("✅ Multi-site navigation successful");
  }

  @Test(priority = 5, description = "Verify page load performance")
  @Story("Performance Testing")
  @Severity(SeverityLevel.MINOR)
  @Description("Verify page loads within acceptable time limits (<10 seconds)")
  public void testPageLoadPerformance() {
    LOGGER.info(">>> Test: Page Load Performance");
    Allure.step("Measure page load time");

    long startTime = System.currentTimeMillis();
    driver.get("https://www.google.com");
    long endTime = System.currentTimeMillis();

    long loadTime = endTime - startTime;
    LOGGER.info("Page load time: {}ms", loadTime);

    Assert.assertTrue(loadTime < 10000, "Page should load within 10 seconds");
    LOGGER.info("✅ Page load performance acceptable");
  }

  @Test(priority = 6, description = "Test browser capabilities")
  @Story("Browser Features")
  @Severity(SeverityLevel.NORMAL)
  @Description("Verify browser capabilities including JavaScript, cookies, and window management")
  public void testBrowserCapabilities() {
    LOGGER.info(">>> Test: Browser Capabilities");

    driver.get("https://www.google.com");

    // Verify JavaScript is enabled (search box requires JS)
    WebElement searchBox = driver.findElement(By.name("q"));
    Assert.assertTrue(searchBox.isDisplayed(), "Search box should be displayed (JS enabled)");

    // Verify cookies can be retrieved
    var cookies = driver.manage().getCookies();
    LOGGER.info("Cookies found: {}", cookies.size());
    Assert.assertTrue(!cookies.isEmpty(), "Should have at least one cookie");

    // Verify window can be resized
    driver.manage().window().setSize(new org.openqa.selenium.Dimension(1024, 768));
    org.openqa.selenium.Dimension size = driver.manage().window().getSize();
    LOGGER.info("Window size: {}x{}", size.width, size.height);

    LOGGER.info("✅ Browser capabilities verified");
  }

  @Test(priority = 7, description = "Test form interaction")
  @Story("Form Interactions")
  @Severity(SeverityLevel.NORMAL)
  @Description("Verify form field interactions including input, clear, and attribute validation")
  public void testFormInteraction() {
    LOGGER.info(">>> Test: Form Interaction");

    driver.get("https://www.google.com");

    // Interact with search form
    WebElement searchBox = driver.findElement(By.name("q"));

    // Test various input methods
    searchBox.clear();
    searchBox.sendKeys("Selenium");
    Assert.assertEquals(searchBox.getAttribute("value"), "Selenium");

    searchBox.clear();
    searchBox.sendKeys("Grid Testing");
    Assert.assertEquals(searchBox.getAttribute("value"), "Grid Testing");

    LOGGER.info("✅ Form interaction successful");
  }

  @Test(priority = 8, description = "Verify responsive design detection")
  @Story("Responsive Design")
  @Severity(SeverityLevel.MINOR)
  @Description("Verify website works correctly at different viewport sizes")
  public void testResponsiveDesign() {
    LOGGER.info(">>> Test: Responsive Design");

    driver.get("https://www.google.com");

    // Test different viewport sizes
    int[][] viewports = {{1920, 1080}, {1024, 768}, {768, 1024}};

    for (int[] viewport : viewports) {
      driver.manage().window().setSize(new org.openqa.selenium.Dimension(viewport[0], viewport[1]));
      LOGGER.info("Testing viewport: {}x{}", viewport[0], viewport[1]);

      WebElement searchBox = driver.findElement(By.name("q"));
      Assert.assertTrue(
          searchBox.isDisplayed(),
          "Search box should be visible at " + viewport[0] + "x" + viewport[1]);
    }

    LOGGER.info("✅ Responsive design verified");
  }

  @AfterMethod
  public void tearDown(ITestResult result) {
    if (driver != null) {
      // Capture screenshot based on test result
      if (result.getStatus() == ITestResult.FAILURE) {
        LOGGER.error("❌ Test failed - capturing failure evidence...");
        AllureHelper.captureScreenshot(driver, "FAILURE-" + result.getName());
        AllureHelper.attachPageSource(driver);
        AllureHelper.attachBrowserLogs(driver);
        AllureHelper.logBrowserInfo(driver);
      } else if (result.getStatus() == ITestResult.SUCCESS) {
        LOGGER.info("✅ Test passed");
      } else if (result.getStatus() == ITestResult.SKIP) {
        LOGGER.info("⏭️  Test skipped");
      }

      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("\nClosing {} browser...", currentBrowser.toUpperCase(Locale.ENGLISH));
      }
      driver.quit();
      LOGGER.info("Browser closed successfully");
      LOGGER.info("========================================\n");
    }
  }
}
