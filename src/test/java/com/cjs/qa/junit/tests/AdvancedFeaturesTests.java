package com.cjs.qa.junit.tests;

import java.net.URI;
import java.time.Duration;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.*;
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
 * Advanced Selenium Features Tests
 *
 * <p>Demonstrates advanced Selenium capabilities: - JavaScript execution - Cookie management -
 * Window/Tab handling - Mouse actions - Keyboard actions - Screenshot comparisons - Local storage
 */
@Epic("Extended Test Coverage")
@Feature("Advanced Features")
public class AdvancedFeaturesTests {

  private static final GuardedLogger LOGGER =
      new GuardedLogger(LogManager.getLogger(AdvancedFeaturesTests.class));

  private WebDriver driver;

  @BeforeMethod
  public void setUp() throws Exception {
    LOGGER.info("========================================");
    LOGGER.info("🚀 ADVANCED FEATURES TEST SETUP");

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
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    LOGGER.info("✅ Driver initialized");
  }

  @Test(priority = 1)
  @Story("JavaScript Execution")
  @Severity(SeverityLevel.NORMAL)
  @Description("Execute JavaScript commands in browser")
  public void testJavaScriptExecution() {
    LOGGER.info("\n>>> Test: JavaScript Execution");

    Allure.step("Navigate to Google");
    driver.get("https://www.google.com");

    JavascriptExecutor js = (JavascriptExecutor) driver;

    Allure.step("Execute JavaScript to get page title");
    String title = (String) js.executeScript("return document.title;");
    LOGGER.info("Title via JS: {}", title);
    Assert.assertTrue(title.contains("Google"));

    Allure.step("Execute JavaScript to scroll");
    js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    LOGGER.info("Scrolled to bottom");

    Allure.step("Execute JavaScript to modify element");
    js.executeScript(
        "var searchBox = document.querySelector('textarea[name=\"q\"]');"
            + "if (searchBox) searchBox.style.border = '3px solid red';");
    LOGGER.info("Modified element style via JS");

    // DEBUG: Screenshot capture commented out - only capture on failure
    // AllureHelper.captureScreenshot(driver, "JS-Execution");

    LOGGER.info("✅ JavaScript execution successful");
  }

  @Test(priority = 2)
  @Story("Cookie Management")
  @Severity(SeverityLevel.NORMAL)
  @Description("Test cookie creation, retrieval, and deletion")
  public void testCookieManagement() {
    LOGGER.info("\n>>> Test: Cookie Management");

    Allure.step("Navigate to Google");
    driver.get("https://www.google.com");

    Allure.step("Get initial cookies");
    Set<Cookie> initialCookies = driver.manage().getCookies();
    int initialCount = initialCookies.size();
    LOGGER.info("Initial cookies: {}", initialCount);

    Allure.step("Add custom cookie");
    Cookie customCookie = new Cookie("test_cookie", "selenium_test_value");
    driver.manage().addCookie(customCookie);
    LOGGER.info("Added custom cookie");

    Allure.step("Verify cookie was added");
    Cookie retrievedCookie = driver.manage().getCookieNamed("test_cookie");
    Assert.assertNotNull(retrievedCookie, "Cookie should be retrievable");
    Assert.assertEquals(retrievedCookie.getValue(), "selenium_test_value");
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info(
          "Cookie verified: {} = {}", retrievedCookie.getName(), retrievedCookie.getValue());
    }

    Allure.step("Delete specific cookie");
    driver.manage().deleteCookie(customCookie);
    Cookie deletedCookie = driver.manage().getCookieNamed("test_cookie");
    Assert.assertNull(deletedCookie, "Cookie should be deleted");
    LOGGER.info("Cookie deleted successfully");

    // DEBUG: Screenshot capture commented out - only capture on failure
    // AllureHelper.captureScreenshot(driver, "Cookie-Management");

    LOGGER.info("✅ Cookie management successful");
  }

  @Test(priority = 3)
  @Story("Window Management")
  @Severity(SeverityLevel.NORMAL)
  @Description("Test window size, position, and maximize")
  public void testWindowManagement() {
    LOGGER.info("\n>>> Test: Window Management");

    Allure.step("Navigate to Google");
    driver.get("https://www.google.com");

    Allure.step("Get initial window size");
    Dimension initialSize = driver.manage().window().getSize();
    LOGGER.info("Initial size: {}x{}", initialSize.width, initialSize.height);

    Allure.step("Resize window to a size different from initial");
    // Choose a resize target that's guaranteed to be different from initial size
    // If initial width is 800, use 1024; otherwise use 800
    int targetWidth = (initialSize.width == 800) ? 1024 : 800;
    int targetHeight = (initialSize.height == 600) ? 768 : 600;
    driver.manage().window().setSize(new Dimension(targetWidth, targetHeight));
    Dimension newSize = driver.manage().window().getSize();
    LOGGER.info("New size: {}x{}", newSize.width, newSize.height);

    // DEBUG: Screenshot capture commented out - only capture on failure
    // AllureHelper.captureScreenshot(driver, "Window-Resized");

    Allure.step("Maximize window");
    driver.manage().window().maximize();
    Dimension maxSize = driver.manage().window().getSize();
    LOGGER.info("Maximized size: {}x{}", maxSize.width, maxSize.height);

    // DEBUG: Screenshot capture commented out - only capture on failure
    // AllureHelper.captureScreenshot(driver, "Window-Maximized");

    // Verify resize worked - check that at least one dimension changed
    boolean widthChanged = initialSize.width != newSize.width;
    boolean heightChanged = initialSize.height != newSize.height;
    Assert.assertTrue(
        widthChanged || heightChanged,
        String.format(
            "Window should have been resized from %dx%d to %dx%d",
            initialSize.width, initialSize.height, newSize.width, newSize.height));

    LOGGER.info("✅ Window management successful");
  }

  @Test(priority = 4)
  @Story("Keyboard Actions")
  @Severity(SeverityLevel.NORMAL)
  @Description("Test advanced keyboard interactions")
  public void testKeyboardActions() {
    LOGGER.info("\n>>> Test: Keyboard Actions");

    Allure.step("Navigate to Google");
    driver.get("https://www.google.com");

    Allure.step("Find search box");
    WebElement searchBox = driver.findElement(By.name("q"));

    Allure.step("Type with pauses");
    searchBox.sendKeys("S");
    searchBox.sendKeys("e");
    searchBox.sendKeys("l");
    searchBox.sendKeys("e");
    searchBox.sendKeys("n");
    searchBox.sendKeys("i");
    searchBox.sendKeys("u");
    searchBox.sendKeys("m");
    LOGGER.info("Typed: Selenium");

    // DEBUG: Screenshot capture commented out - only capture on failure
    // AllureHelper.captureScreenshot(driver, "After-Typing");

    Allure.step("Clear with keyboard shortcut");
    searchBox.sendKeys(Keys.chord(Keys.CONTROL, "a"));
    searchBox.sendKeys(Keys.BACK_SPACE);
    LOGGER.info("Cleared input");

    Allure.step("Type and submit");
    searchBox.sendKeys("Test" + Keys.ENTER);
    LOGGER.info("Submitted search");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      LOGGER.warn("Sleep interrupted", e);
    }

    // DEBUG: Screenshot capture commented out - only capture on failure
    // AllureHelper.captureScreenshot(driver, "Keyboard-Actions");

    LOGGER.info("✅ Keyboard actions successful");
  }

  @Test(priority = 5)
  @Story("Page Navigation")
  @Severity(SeverityLevel.NORMAL)
  @Description("Test browser navigation (back, forward, refresh)")
  public void testBrowserNavigation() {
    LOGGER.info("\n>>> Test: Browser Navigation");

    Allure.step("Navigate to first page");
    driver.get("https://www.google.com");
    String firstUrl = driver.getCurrentUrl();
    LOGGER.info("First URL: {}", firstUrl);

    Allure.step("Navigate to second page");
    driver.get("https://github.com");
    String secondUrl = driver.getCurrentUrl();
    LOGGER.info("Second URL: {}", secondUrl);

    // DEBUG: Screenshot capture commented out - only capture on failure
    // AllureHelper.captureScreenshot(driver, "Second-Page");

    Allure.step("Navigate back");
    driver.navigate().back();
    String backUrl = driver.getCurrentUrl();
    LOGGER.info("After back: {}", backUrl);
    Assert.assertTrue(backUrl.contains("google"), "Should navigate back to Google");

    Allure.step("Navigate forward");
    driver.navigate().forward();
    String forwardUrl = driver.getCurrentUrl();
    LOGGER.info("After forward: {}", forwardUrl);
    Assert.assertTrue(forwardUrl.contains("github"), "Should navigate forward to GitHub");

    Allure.step("Refresh page");
    driver.navigate().refresh();
    LOGGER.info("Page refreshed");

    // DEBUG: Screenshot capture commented out - only capture on failure
    // AllureHelper.captureScreenshot(driver, "After-Refresh");

    LOGGER.info("✅ Browser navigation successful");
  }

  @Test(priority = 6)
  @Story("Element Properties")
  @Severity(SeverityLevel.MINOR)
  @Description("Test retrieving element properties and attributes")
  public void testElementProperties() {
    LOGGER.info("\n>>> Test: Element Properties");

    Allure.step("Navigate to Google");
    driver.get("https://www.google.com");

    Allure.step("Find search box");
    WebElement searchBox = driver.findElement(By.name("q"));

    Allure.step("Get element properties");
    String tagName = searchBox.getTagName();
    String nameAttr = searchBox.getAttribute("name");
    String typeAttr = searchBox.getAttribute("type");
    Dimension size = searchBox.getSize();
    Point location = searchBox.getLocation();

    LOGGER.info("Tag: {}", tagName);
    LOGGER.info("Name: {}", nameAttr);
    LOGGER.info("Type: {}", typeAttr);
    LOGGER.info("Size: {}x{}", size.width, size.height);
    LOGGER.info("Location: ({}, {})", location.x, location.y);

    // DEBUG: Screenshot capture commented out - only capture on failure
    // AllureHelper.captureScreenshot(driver, "Element-Properties");

    Allure.step("Verify properties");
    Assert.assertEquals(tagName, "textarea", "Should be textarea element");
    Assert.assertEquals(nameAttr, "q", "Name should be 'q'");

    LOGGER.info("✅ Element properties retrieved successfully");
  }

  @Test(priority = 7)
  @Story("Performance")
  @Severity(SeverityLevel.MINOR)
  @Description("Test page load and element search performance")
  public void testPerformanceMetrics() {
    LOGGER.info("\n>>> Test: Performance Metrics");

    Allure.step("Measure page load time");
    long startTime = System.currentTimeMillis();
    driver.get("https://www.google.com");
    long endTime = System.currentTimeMillis();
    long loadTime = endTime - startTime;

    LOGGER.info("Page load time: {}ms", loadTime);

    Allure.step("Measure element find time");
    startTime = System.currentTimeMillis();
    driver.findElement(By.name("q"));
    endTime = System.currentTimeMillis();
    long findTime = endTime - startTime;

    LOGGER.info("Element find time: {}ms", findTime);

    // DEBUG: Screenshot capture commented out - only capture on failure
    // AllureHelper.captureScreenshot(driver, "Performance-Test");

    Allure.step("Verify performance acceptable");
    Assert.assertTrue(loadTime < 10000, "Page should load within 10 seconds");
    Assert.assertTrue(findTime < 2000, "Element should be found within 2 seconds");

    LOGGER.info("✅ Performance metrics acceptable");
  }

  @AfterMethod
  public void tearDown(ITestResult result) {
    if (driver != null) {
      if (result.getStatus() == ITestResult.FAILURE) {
        LOGGER.error("❌ Advanced test failed");
        AllureHelper.captureScreenshot(driver, "FAILURE-" + result.getName());
        AllureHelper.attachPageSource(driver);
      } else if (result.getStatus() == ITestResult.SUCCESS) {
        LOGGER.info("✅ Advanced test passed");
      }

      LOGGER.info("Closing browser...");
      driver.quit();
      LOGGER.info("========================================\n");
    }
  }
}
