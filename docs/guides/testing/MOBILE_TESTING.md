# Mobile Testing Guide

## Overview

Comprehensive mobile testing capabilities for the CJS QA Selenium framework, including:
- **Mobile Browser Testing** - Device emulation for Chrome and Firefox
- **Appium Integration** - Native iOS and Android app testing
- **Responsive Design Validation** - Cross-device layout verification

## Features

### 1. Mobile Device Emulation

Test mobile websites using Chrome/Firefox mobile emulation:

```java
// Create mobile Chrome driver with iPhone 14 Pro emulation
WebDriver driver = MobileTestsConfiguration.createMobileChromeDriver(
    "http://selenium-hub:4444/wd/hub",
    MobileDevice.IPHONE_14_PRO
);
```

**Supported Devices:**
- iPhone 14 Pro (393x852)
- iPhone SE (375x667)
- Samsung Galaxy S21 (360x800)
- Google Pixel 7 (412x915)
- Google Pixel 9 (412x892)
- iPad Air (820x1180)
- Galaxy Tab S7 (800x1280)

### 2. Appium Native App Testing

Test native iOS and Android apps:

**Android Testing:**
```java
AndroidDriver driver = MobileTestsConfiguration.createAndroidDriver(
    "http://localhost:4723",
    MobileDevice.SAMSUNG_GALAXY_S21,
    "com.example.app",
    "com.example.app.MainActivity"
);
```

**iOS Testing:**
```java
IOSDriver driver = MobileTestsConfiguration.createIosDriver(
    "http://localhost:4723",
    MobileDevice.IPHONE_14_PRO,
    "com.example.app"
);
```

### 3. Responsive Design Validation

Validate websites across 10+ viewport sizes:

```java
Dimension[] viewports = MobileTestsConfiguration.getResponsiveViewports();
for (Dimension viewport : viewports) {
    driver.manage().window().setSize(viewport);
    // Validate layout at each breakpoint
}
```

## Test Suites

### MobileBrowserTests

Tests mobile-specific browser functionality:
- ✅ Mobile device emulation
- ✅ Touch interactions
- ✅ Mobile page performance
- ✅ Viewport configuration
- ✅ Mobile form input

**Run Command:**
```bash
mvn test -Dtest=MobileBrowserTests
```

### ResponsiveDesignTests

Validates responsive design implementation:
- ✅ Multi-viewport testing
- ✅ CSS breakpoint validation
- ✅ Mobile navigation menus
- ✅ Image optimization
- ✅ Font legibility
- ✅ Portrait/landscape orientation

**Run Command:**
```bash
mvn test -Dtest=ResponsiveDesignTests
```

## Running Mobile Tests

### With Selenium Grid (Recommended)

```bash
# Start Selenium Grid
docker-compose up -d

# Run mobile tests
docker-compose run --rm tests -Dtest=MobileBrowserTests

# Or run in parallel with all tests
mvn test
```

### Local Execution

```bash
# Run mobile browser tests
mvn test -Dtest=MobileBrowserTests -Dselenium.grid.url=http://localhost:4444/wd/hub

# Run responsive design tests
mvn test -Dtest=ResponsiveDesignTests -Dselenium.grid.url=http://localhost:4444/wd/hub
```

## Appium Setup (For Native Apps)

### Prerequisites

1. **Install Appium:**
```bash
npm install -g appium
npm install -g appium-doctor
```

2. **Install Drivers:**
```bash
# Android
appium driver install uiautomator2

# iOS
appium driver install xcuitest
```

3. **Verify Setup:**
```bash
appium-doctor --android
appium-doctor --ios
```

### Start Appium Server

```bash
appium --port 4723
```

### Run Native App Tests

```java
// Android example
AndroidDriver driver = MobileTestsConfiguration.createAndroidDriver(
    "http://localhost:4723",
    MobileDevice.PIXEL_7,
    "com.android.chrome",
    "com.google.android.apps.chrome.Main"
);

// iOS example
IOSDriver driver = MobileTestsConfiguration.createIosDriver(
    "http://localhost:4723",
    MobileDevice.IPHONE_14_PRO,
    "com.apple.mobilesafari"
);
```

## Configuration

### System Properties

| Property | Description | Default |
|----------|-------------|---------|
| `selenium.grid.url` | Selenium Grid URL | `http://selenium-hub:4444/wd/hub` |
| `appium.server.url` | Appium server URL | `http://localhost:4723` |
| `mobile.device` | Default device for tests | `IPHONE_14_PRO` |

### Example Configuration

```bash
mvn test \
  -Dtest=MobileBrowserTests \
  -Dselenium.grid.url=http://selenium-hub:4444/wd/hub \
  -Dmobile.device=SAMSUNG_GALAXY_S21
```

## Best Practices

### 1. Device Selection

Choose devices that represent your user base:
- **iPhone:** 14 Pro, SE (current + legacy)
- **Android:** Pixel 9, Pixel 7, Galaxy S21 (latest + flagship + mid-range)
- **Tablet:** iPad Air, Galaxy Tab (larger screens)

### 2. Viewport Testing

Test critical breakpoints:
- **Mobile:** 375px, 390px, 412px
- **Tablet:** 768px, 820px, 1024px
- **Desktop:** 1366px, 1920px

### 3. Touch Target Sizing

Ensure interactive elements are at least **44x44 pixels** for accessibility.

### 4. Performance

Mobile devices have:
- Slower CPUs
- Limited memory
- Variable network speeds

Test with **realistic expectations** (3-5 second load times acceptable).

### 5. Orientation Testing

Always test both:
- **Portrait** mode (default)
- **Landscape** mode (often overlooked)

## Troubleshooting

### Issue: Device Not Found (Appium)

```
Error: An unknown server-side error occurred while processing the command.
Original error: Could not find a connected Android device
```

**Solution:**
```bash
# Check connected devices
adb devices

# Start emulator if none connected
emulator -avd Pixel_7_API_33
```

### Issue: WebDriver Not Found

```
Error: Could not create MobileChromeDriver
```

**Solution:**
```bash
# Verify Selenium Grid is running
curl http://localhost:4444/status

# Restart Grid if needed
docker-compose restart selenium-hub
```

### Issue: Touch Targets Too Small

**Solution:**
Update CSS to meet minimum size requirements:
```css
.button {
    min-width: 44px;
    min-height: 44px;
    padding: 12px;
}
```

## CI/CD Integration

### GitHub Actions Example

```yaml
- name: Run Mobile Tests
  run: |
    docker-compose up -d
    docker-compose run --rm tests -Dtest=MobileBrowserTests,ResponsiveDesignTests
    docker-compose down
```

## Reporting

Mobile test results are integrated with:
- **Allure Reports** - Visual test results with screenshots
- **Console Output** - Real-time test progress
- **TestNG Reports** - Detailed execution reports

**View Allure Reports:**
```bash
mvn allure:serve
```

## Advanced Usage

### Custom Device Configuration

```java
MobileDevice customDevice = new MobileDevice(
    411, 823, // width, height
    "Custom Device",
    "Android 13",
    false // isIOS
);

WebDriver driver = MobileTestsConfiguration.createMobileChromeDriver(
    gridUrl,
    customDevice
);
```

### Mobile-Specific Waits

```java
// Wait for mobile keyboard
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
wait.until(ExpectedConditions.presenceOfElementLocated(By.id("search")));

// Wait for orientation change
Thread.sleep(1000); // Allow time for reflow
```

### Network Throttling (Chrome)

```java
ChromeOptions options = new ChromeOptions();
Map<String, Object> networkConditions = new HashMap<>();
networkConditions.put("latency", 50); // 50ms latency
networkConditions.put("downloadThroughput", 1024 * 1024); // 1 Mbps
networkConditions.put("uploadThroughput", 512 * 1024); // 512 Kbps
options.setExperimentalOption("networkConditions", networkConditions);
```

## Resources

- [Appium Documentation](https://appium.io/docs/en/2.0/)
- [Selenium Mobile Testing](https://www.selenium.dev/documentation/webdriver/drivers/mobile/)
- [Chrome DevTools Protocol](https://chromedevtools.github.io/devtools-protocol/)
- [iOS WebDriver](https://github.com/appium/appium-xcuitest-driver)
- [Android WebDriver](https://github.com/appium/appium-uiautomator2-driver)

## Support

For issues or questions:
1. Check the troubleshooting section above
2. Review test logs in `target/surefire-reports/`
3. Check Allure report screenshots
4. Consult Appium/Selenium documentation

---

**Last Updated:** 2025-11-10
**Version:** 1.0.0
