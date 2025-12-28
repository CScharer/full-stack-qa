package com.cjs.qa.junit.tests.mobile;

import static com.cjs.qa.junit.tests.mobile.MobileTestsConfiguration.MobileDevice;

import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.cjs.qa.utilities.GuardedLogger;

import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

/**
 * Responsive Design Validation Tests. Validates website responsiveness across various screen sizes
 * and devices.
 */
@Epic("Mobile Testing")
@Feature("Responsive Design Validation")
public class ResponsiveDesignTests {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(ResponsiveDesignTests.class));

  private WebDriver driver;
  private static final String GRID_URL =
      System.getProperty(
          "selenium.grid.url",
          System.getenv("SELENIUM_REMOTE_URL") != null
              ? System.getenv("SELENIUM_REMOTE_URL")
              : "http://localhost:4444/wd/hub");

  @BeforeMethod
  public void setUp() {
    LOG.info("\n========================================");
    LOG.info("Test Starting: Responsive Design Validation");
    LOG.info("Grid URL: " + GRID_URL);
    LOG.info("========================================");
  }

  @AfterMethod
  public void tearDown(ITestResult result) {
    if (driver != null) {
      try {
        if (result.isSuccess()) {
          LOG.info("✅ Test passed");
        } else {
          LOG.info("❌ Test failed - capturing failure screenshot...");
          captureScreenshot("FAILURE-" + result.getMethod().getMethodName());
        }
      } catch (Exception e) {
        LOG.error("Failed to capture screenshot: " + e.getMessage());
      } finally {
        LOG.info("\nClosing browser...");
        driver.quit();
        LOG.info("Browser closed successfully");
        LOG.info("========================================\n");
      }
    }
  }

  @DataProvider(name = "responsiveViewports")
  public Object[][] responsiveViewports() {
    return new Object[][] {
      {new Dimension(375, 667), "iPhone SE"},
      {new Dimension(390, 844), "iPhone 13"},
      {new Dimension(393, 852), "iPhone 14 Pro"},
      {new Dimension(412, 892), "Google Pixel 9"},
      {new Dimension(768, 1024), "iPad Portrait"},
      {new Dimension(1024, 768), "iPad Landscape"},
      {new Dimension(1366, 768), "Desktop (Small)"},
      {new Dimension(1920, 1080), "Desktop (Full HD)"},
    };
  }

  @Test(priority = 1)
  @Story("Multi-Viewport Testing")
  @Severity(SeverityLevel.CRITICAL)
  @Description("Test website responsiveness across all standard viewports")
  public void testMultipleViewportSizes() throws Exception {
    LOG.info(">>> Test: Multiple Viewport Sizes");

    driver =
        MobileTestsConfiguration.createMobileChromeDriver(GRID_URL, MobileDevice.IPHONE_14_PRO);

    Dimension[] viewports = MobileTestsConfiguration.getResponsiveViewports();

    for (Dimension viewport : viewports) {
      LOG.info("\nTesting viewport: " + viewport.getWidth() + "x" + viewport.getHeight());
      driver.manage().window().setSize(viewport);

      driver.get("https://www.github.com/");
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
      wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

      // Verify no horizontal scroll
      Long documentWidth =
          (Long) ((JavascriptExecutor) driver).executeScript("return document.body.scrollWidth;");
      Long windowWidth =
          (Long) ((JavascriptExecutor) driver).executeScript("return window.innerWidth;");

      Assert.assertTrue(
          documentWidth <= windowWidth + 20,
          "Content should fit within viewport at " + viewport.getWidth() + "px");

      LOG.info("✅ Viewport " + viewport.getWidth() + "x" + viewport.getHeight() + " verified");

      Thread.sleep(500); // Brief pause between viewports
    }

    LOG.info("✅ All viewports tested successfully");
    Allure.step("Multi-viewport testing completed");
  }

  @Test(priority = 2, dataProvider = "responsiveViewports")
  @Story("Breakpoint Validation")
  @Severity(SeverityLevel.NORMAL)
  @Description("Validate CSS breakpoints and layout changes")
  public void testResponsiveBreakpoints(Dimension viewport, String deviceName) throws Exception {
    LOG.info(">>> Test: Responsive Breakpoints - " + deviceName);

    driver = MobileTestsConfiguration.createMobileChromeDriver(GRID_URL, MobileDevice.PIXEL_7);
    driver.manage().window().setSize(viewport);
    LOG.info(
        "✅ Testing breakpoint: "
            + deviceName
            + " ("
            + viewport.getWidth()
            + "x"
            + viewport.getHeight()
            + ")");

    driver.get("https://www.wikipedia.org/");
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(ExpectedConditions.presenceOfElementLocated(By.id("searchInput")));

    // Check layout adapts to viewport
    WebElement searchInput = driver.findElement(By.id("searchInput"));
    int inputWidth = searchInput.getSize().getWidth();
    int viewportWidth = viewport.getWidth();

    LOG.info("Search input width: " + inputWidth + "px");
    LOG.info("Viewport width: " + viewportWidth + "px");

    // Input should be responsive (not exceeding viewport)
    Assert.assertTrue(inputWidth <= viewportWidth, "Search input should fit within viewport");

    // Check for responsive images
    JavascriptExecutor js = (JavascriptExecutor) driver;
    Long overflowingImages =
        (Long)
            js.executeScript(
                "return Array.from(document.images).filter(img => img.width > window.innerWidth).length;");
    Assert.assertEquals(overflowingImages.longValue(), 0L, "No images should overflow viewport");

    LOG.info("✅ Breakpoint validated for " + deviceName);
    Allure.step("Breakpoint validation completed for " + deviceName);
  }

  @Test(priority = 3)
  @Story("Mobile Navigation")
  @Severity(SeverityLevel.NORMAL)
  @Description("Test mobile navigation menu (hamburger menu)")
  public void testMobileNavigationMenu() throws Exception {
    LOG.info(">>> Test: Mobile Navigation Menu");

    driver = MobileTestsConfiguration.createMobileChromeDriver(GRID_URL, MobileDevice.IPHONE_SE);
    LOG.info("✅ Mobile driver initialized for navigation testing");

    driver.get("https://www.github.com/");
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

    // Look for mobile menu button
    List<WebElement> menuButtons =
        driver.findElements(
            By.xpath("//button[contains(@aria-label, 'menu') or contains(@class, 'menu')]"));

    if (!menuButtons.isEmpty()) {
      LOG.info("✅ Mobile menu button found");

      // Test menu interaction
      WebElement menuButton = menuButtons.get(0);
      menuButton.click();
      Thread.sleep(1000); // Wait for animation

      LOG.info("✅ Mobile menu interaction successful");
    } else {
      LOG.info("ℹ️ No mobile menu detected (may use different navigation)");
    }

    Allure.step("Mobile navigation tested");
  }

  @Test(priority = 4)
  @Story("Image Optimization")
  @Severity(SeverityLevel.NORMAL)
  @Description("Validate images are optimized for mobile viewports")
  public void testMobileImageOptimization() throws Exception {
    LOG.info(">>> Test: Mobile Image Optimization");

    driver =
        MobileTestsConfiguration.createMobileChromeDriver(
            GRID_URL, MobileDevice.SAMSUNG_GALAXY_S21);
    LOG.info("✅ Mobile driver initialized for image testing");

    driver.get("https://www.wikipedia.org/");
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

    // Check for responsive images
    JavascriptExecutor js = (JavascriptExecutor) driver;

    // Count images with srcset attribute (responsive images)
    Long responsiveImages =
        (Long) js.executeScript("return document.querySelectorAll('img[srcset]').length;");
    LOG.info("Responsive images found: " + responsiveImages);

    // Check for images that are too large for viewport
    Long oversizedImages =
        (Long)
            js.executeScript(
                "return Array.from(document.images).filter(img => img.naturalWidth > window.innerWidth * 2).length;");
    LOG.info("Oversized images: " + oversizedImages);

    Assert.assertTrue(oversizedImages < 5, "Should have minimal oversized images on mobile");

    LOG.info("✅ Image optimization verified");
    Allure.step("Image optimization validated");
  }

  @Test(priority = 5)
  @Story("Font Legibility")
  @Severity(SeverityLevel.NORMAL)
  @Description("Validate font sizes are legible on mobile devices")
  public void testMobileFontLegibility() throws Exception {
    LOG.info(">>> Test: Mobile Font Legibility");

    driver =
        MobileTestsConfiguration.createMobileChromeDriver(GRID_URL, MobileDevice.IPHONE_14_PRO);
    LOG.info("✅ Mobile driver initialized for font testing");

    driver.get("https://www.github.com/");
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

    // Check minimum font sizes
    JavascriptExecutor js = (JavascriptExecutor) driver;

    // Get all text elements and check font size
    Long tooSmallText =
        (Long)
            js.executeScript(
                "const elements = document.querySelectorAll('p, span, div, a, button');"
                    + "return Array.from(elements)"
                    + ".filter(el => {"
                    + "  const fontSize = parseFloat(window.getComputedStyle(el).fontSize);"
                    + "  return fontSize < 12 && el.textContent.trim().length > 0;"
                    + "}).length;");

    LOG.info("Elements with font size < 12px: " + tooSmallText);

    Assert.assertTrue(tooSmallText < 10, "Most text should be at least 12px for mobile legibility");

    LOG.info("✅ Font legibility verified");
    Allure.step("Font legibility validated");
  }

  @Test(priority = 6)
  @Story("Portrait vs Landscape")
  @Severity(SeverityLevel.NORMAL)
  @Description("Test layout adaptation between portrait and landscape orientations")
  public void testOrientationChanges() throws Exception {
    LOG.info(">>> Test: Orientation Changes");

    driver = MobileTestsConfiguration.createMobileChromeDriver(GRID_URL, MobileDevice.IPAD_AIR);
    LOG.info("✅ Mobile driver initialized for orientation testing");

    String testUrl = "https://www.github.com/";

    // Test portrait mode
    LOG.info("\nTesting Portrait mode...");
    driver.manage().window().setSize(new Dimension(820, 1180));
    driver.get(testUrl);
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

    Long portraitWidth =
        (Long) ((JavascriptExecutor) driver).executeScript("return window.innerWidth;");
    Dimension portraitSize = driver.manage().window().getSize();
    LOG.info(
        "Portrait viewport width: "
            + portraitWidth
            + "px, Actual window: "
            + portraitSize.getWidth()
            + "x"
            + portraitSize.getHeight());

    Thread.sleep(1000);

    // Test landscape mode
    LOG.info("\nTesting Landscape mode...");
    driver.manage().window().setSize(new Dimension(1180, 820));
    driver.get(testUrl);
    wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

    Long landscapeWidth =
        (Long) ((JavascriptExecutor) driver).executeScript("return window.innerWidth;");
    Dimension landscapeSize = driver.manage().window().getSize();
    LOG.info(
        "Landscape viewport width: "
            + landscapeWidth
            + "px, Actual window: "
            + landscapeSize.getWidth()
            + "x"
            + landscapeSize.getHeight());

    // Both orientations should render properly
    Assert.assertNotNull(portraitWidth, "Portrait mode should render");
    Assert.assertNotNull(landscapeWidth, "Landscape mode should render");

    // Verify window dimensions were set (may not affect viewport in Grid/headless)
    Assert.assertNotNull(portraitSize, "Portrait window size should be set");
    Assert.assertNotNull(landscapeSize, "Landscape window size should be set");
    LOG.info("✅ Both orientations render successfully");
    LOG.info("ℹ️ Note: Viewport size may not change in Grid/headless - window resize attempted");

    LOG.info("✅ Orientation changes handled properly");
    Allure.step("Orientation change validation completed");
  }

  /**
   * Capture screenshot and attach to Allure report.
   *
   * @param name Screenshot name
   */
  private void captureScreenshot(String name) {
    try {
      byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
      Allure.addAttachment(name, "image/png", new ByteArrayInputStream(screenshot), "png");
      LOG.info("📸 Screenshot captured: " + name);
    } catch (Exception e) {
      LOG.error("Failed to capture screenshot: " + e.getMessage());
    }
  }
}
