package com.cjs.qa.utilities;

import java.io.ByteArrayInputStream;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;

/**
 * Utility class for Allure reporting enhancements Provides screenshot capture, logging, and
 * attachment functionality
 */
public final class AllureHelper {

  private AllureHelper() {
    // Utility class - prevent instantiation
  }

  private static final GuardedLogger LOGGER =
      new GuardedLogger(LogManager.getLogger(AllureHelper.class));

  /**
   * Capture and attach screenshot to Allure report
   *
   * @param driver WebDriver instance
   * @param name Screenshot name
   */
  public static void captureScreenshot(WebDriver driver, String name) {
    if (driver != null) {
      try {
        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Allure.addAttachment(name, "image/png", new ByteArrayInputStream(screenshot), ".png");
        LOGGER.info("📸 Screenshot captured: {}", name);
      } catch (Exception e) {
        LOGGER.error("⚠️  Failed to capture screenshot: {}", e.getMessage());
      }
    }
  }

  /**
   * Capture screenshot with default name
   *
   * @param driver WebDriver instance
   */
  public static void captureScreenshot(WebDriver driver) {
    captureScreenshot(driver, "Screenshot-" + System.currentTimeMillis());
  }

  /**
   * Capture screenshot as byte array (for @Attachment annotation)
   *
   * @param driver WebDriver instance
   * @return Screenshot as byte array
   */
  @Attachment(value = "Screenshot", type = "image/png")
  public static byte[] takeScreenshot(WebDriver driver) {
    if (driver != null) {
      try {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
      } catch (Exception e) {
        LOGGER.error("⚠️  Failed to capture screenshot: {}", e.getMessage());
        return new byte[0];
      }
    }
    return new byte[0];
  }

  /**
   * Attach text content to Allure report
   *
   * @param name Attachment name
   * @param content Text content
   */
  public static void attachText(String name, String content) {
    Allure.addAttachment(name, "text/plain", content);
  }

  /**
   * Attach HTML content to Allure report
   *
   * @param name Attachment name
   * @param html HTML content
   */
  public static void attachHtml(String name, String html) {
    Allure.addAttachment(name, "text/html", html);
  }

  /**
   * Attach JSON content to Allure report
   *
   * @param name Attachment name
   * @param json JSON content
   */
  public static void attachJson(String name, String json) {
    Allure.addAttachment(name, "application/json", json);
  }

  /**
   * Log browser information to Allure
   *
   * @param driver WebDriver instance
   */
  public static void logBrowserInfo(WebDriver driver) {
    if (driver != null) {
      try {
        var capabilities = ((org.openqa.selenium.remote.RemoteWebDriver) driver).getCapabilities();
        String browserName = capabilities.getBrowserName();
        String browserVersion = capabilities.getBrowserVersion();
        String platform = capabilities.getPlatformName().toString();

        String info =
            String.format("Browser: %s %s\nPlatform: %s", browserName, browserVersion, platform);

        attachText("Browser Info", info);
        LOGGER.info("ℹ️  Browser info logged to Allure");
      } catch (Exception e) {
        LOGGER.error("⚠️  Failed to log browser info: {}", e.getMessage());
      }
    }
  }

  /**
   * Log page source to Allure (useful for debugging)
   *
   * @param driver WebDriver instance
   */
  public static void attachPageSource(WebDriver driver) {
    if (driver != null) {
      try {
        String pageSource = driver.getPageSource();
        attachHtml("Page Source", pageSource);
        LOGGER.info("📄 Page source attached to Allure");
      } catch (Exception e) {
        LOGGER.error("⚠️  Failed to attach page source: {}", e.getMessage());
      }
    }
  }

  /**
   * Log console logs to Allure (for JavaScript errors)
   *
   * @param driver WebDriver instance
   */
  public static void attachBrowserLogs(WebDriver driver) {
    if (driver != null) {
      try {
        var logs = driver.manage().logs().get("browser");
        StringBuilder logBuilder = new StringBuilder();
        logs.forEach((log) -> logBuilder.append(log.toString()).append("\n"));

        if (logBuilder.length() > 0) {
          attachText("Browser Console Logs", logBuilder.toString());
          LOGGER.info("📋 Browser logs attached to Allure");
        }
      } catch (Exception e) {
        // Some drivers don't support logs
        LOGGER.info("ℹ️  Browser logs not available for this driver");
      }
    }
  }

  /**
   * Create a step with screenshot
   *
   * @param stepName Step name
   * @param driver WebDriver instance
   * @param action Action to perform
   */
  public static void stepWithScreenshot(String stepName, WebDriver driver, Runnable action) {
    Allure.step(
        stepName,
        () -> {
          action.run();
          captureScreenshot(driver, stepName + "-screenshot");
        });
  }
}
