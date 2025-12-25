package com.cjs.qa.junit.tests.mobile;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;

/**
 * Mobile Testing Configuration for Selenium and Appium. Provides utilities for mobile browser
 * emulation and native app testing.
 */
public final class MobileTestsConfiguration {

  private MobileTestsConfiguration() {
    // Utility class - private constructor
  }

  /** Mobile device configurations for emulation. */
  public enum MobileDevice {
    IPHONE_14_PRO(393, 852, "iPhone 14 Pro", "iOS 16", true),
    IPHONE_SE(375, 667, "iPhone SE", "iOS 15", true),
    SAMSUNG_GALAXY_S21(360, 800, "Samsung Galaxy S21", "Android 11", false),
    PIXEL_7(412, 915, "Google Pixel 7", "Android 13", false),
    PIXEL_9(412, 892, "Google Pixel 9", "Android 14", false),
    IPAD_AIR(820, 1180, "iPad Air", "iOS 16", true),
    GALAXY_TAB_S7(800, 1280, "Samsung Galaxy Tab S7", "Android 11", false);

    private final int width;
    private final int height;
    private final String deviceName;
    private final String platformVersion;
    private final boolean isIos;

    MobileDevice(int width, int height, String deviceName, String platformVersion, boolean isIos) {
      this.width = width;
      this.height = height;
      this.deviceName = deviceName;
      this.platformVersion = platformVersion;
      this.isIos = isIos;
    }

    public int getWidth() {
      return width;
    }

    public int getHeight() {
      return height;
    }

    public String getDeviceName() {
      return deviceName;
    }

    public String getPlatformVersion() {
      return platformVersion;
    }

    public boolean isIos() {
      return isIos;
    }

    public Dimension getDimension() {
      return new Dimension(width, height);
    }
  }

  /**
   * Create Chrome browser with mobile device emulation.
   *
   * @param gridUrl Selenium Grid URL
   * @param device Mobile device to emulate
   * @return RemoteWebDriver configured for mobile emulation
   * @throws MalformedURLException if grid URL is invalid
   */
  public static RemoteWebDriver createMobileChromeDriver(String gridUrl, MobileDevice device)
      throws MalformedURLException {
    final ChromeOptions options = new ChromeOptions();

    // Enable mobile emulation
    final Map<String, Object> mobileEmulation = new HashMap<>();
    Map<String, Object> deviceMetrics = new HashMap<>();
    deviceMetrics.put("width", device.getWidth());
    deviceMetrics.put("height", device.getHeight());
    deviceMetrics.put("pixelRatio", 3.0);

    mobileEmulation.put("deviceMetrics", deviceMetrics);
    mobileEmulation.put(
        "userAgent",
        "Mozilla/5.0 (Linux; Android 11) AppleWebKit/537.36 (KHTML, like Gecko) "
            + "Chrome/91.0.4472.120 Mobile Safari/537.36");

    options.setExperimentalOption("mobileEmulation", mobileEmulation);

    // Additional mobile-friendly options
    options.addArguments("--disable-blink-features=AutomationControlled");
    options.addArguments("--disable-dev-shm-usage");
    options.addArguments("--no-sandbox");

    return new RemoteWebDriver(URI.create(gridUrl).toURL(), options);
  }

  /**
   * Create Firefox browser with mobile device emulation.
   *
   * @param gridUrl Selenium Grid URL
   * @param device Mobile device to emulate
   * @return RemoteWebDriver configured for mobile emulation
   * @throws MalformedURLException if grid URL is invalid
   */
  public static RemoteWebDriver createMobileFirefoxDriver(String gridUrl, MobileDevice device)
      throws MalformedURLException {
    FirefoxOptions options = new FirefoxOptions();

    // Set user agent for mobile
    options.addPreference(
        "general.useragent.override",
        "Mozilla/5.0 (Linux; Android 11; Pixel 5) AppleWebKit/537.36 "
            + "(KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36");

    RemoteWebDriver driver = new RemoteWebDriver(URI.create(gridUrl).toURL(), options);

    // Set window size to match mobile device
    driver.manage().window().setSize(device.getDimension());

    return driver;
  }

  /**
   * Create Android driver for native app testing (requires Appium server).
   *
   * @param appiumUrl Appium server URL
   * @param device Mobile device configuration
   * @param appPackage App package name
   * @param appActivity App activity name
   * @return AndroidDriver for native app testing
   * @throws MalformedURLException if Appium URL is invalid
   */
  public static AndroidDriver createAndroidDriver(
      String appiumUrl, MobileDevice device, String appPackage, String appActivity)
      throws MalformedURLException {
    UiAutomator2Options options = new UiAutomator2Options();
    options.setDeviceName(device.getDeviceName());
    options.setPlatformName("Android");
    options.setPlatformVersion(device.getPlatformVersion());
    options.setAppPackage(appPackage);
    options.setAppActivity(appActivity);
    options.setAutomationName("UiAutomator2");
    options.setNoReset(true);

    return new AndroidDriver(URI.create(appiumUrl).toURL(), options);
  }

  /**
   * Create iOS driver for native app testing (requires Appium server).
   *
   * @param appiumUrl Appium server URL
   * @param device Mobile device configuration
   * @param bundleId App bundle ID
   * @return IOSDriver for native app testing
   * @throws MalformedURLException if Appium URL is invalid
   */
  public static IOSDriver createIosDriver(String appiumUrl, MobileDevice device, String bundleId)
      throws MalformedURLException {
    XCUITestOptions options = new XCUITestOptions();
    options.setDeviceName(device.getDeviceName());
    options.setPlatformName("iOS");
    options.setPlatformVersion(device.getPlatformVersion());
    options.setBundleId(bundleId);
    options.setAutomationName("XCUITest");
    options.setNoReset(true);

    return new IOSDriver(URI.create(appiumUrl).toURL(), options);
  }

  /**
   * Create generic Appium driver with custom capabilities.
   *
   * @param appiumUrl Appium server URL
   * @param capabilities Custom capabilities
   * @return AppiumDriver with custom configuration
   * @throws MalformedURLException if Appium URL is invalid
   */
  public static AppiumDriver createAppiumDriver(String appiumUrl, MutableCapabilities capabilities)
      throws MalformedURLException {
    return new AppiumDriver(URI.create(appiumUrl).toURL(), capabilities);
  }

  /**
   * Get default Appium server URL (for local testing).
   *
   * @return Default Appium server URL
   */
  public static String getDefaultAppiumUrl() {
    return "http://localhost:4723";
  }

  /**
   * Check if a device is a tablet based on screen size.
   *
   * @param device Mobile device to check
   * @return true if device is a tablet
   */
  public static boolean isTablet(MobileDevice device) {
    return device.getWidth() >= 768 && device.getHeight() >= 1024;
  }

  /**
   * Get recommended viewport sizes for responsive testing.
   *
   * @return Array of Dimension objects for common viewports
   */
  public static Dimension[] getResponsiveViewports() {
    return new Dimension[] {
      new Dimension(375, 667), // iPhone SE / 8
      new Dimension(390, 844), // iPhone 12/13/14
      new Dimension(393, 852), // iPhone 14 Pro
      new Dimension(360, 800), // Samsung Galaxy S21
      new Dimension(412, 915), // Pixel 7
      new Dimension(768, 1024), // iPad Portrait
      new Dimension(1024, 768), // iPad Landscape
      new Dimension(820, 1180), // iPad Air
      new Dimension(1366, 768), // Desktop (small)
      new Dimension(1920, 1080), // Desktop (full HD)
    };
  }
}
