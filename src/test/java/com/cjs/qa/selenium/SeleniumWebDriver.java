package com.cjs.qa.selenium;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Point;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import com.cjs.qa.core.Environment;
import com.cjs.qa.core.QAException;
import com.cjs.qa.utilities.CommandLineTests;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.utilities.Processes;
import com.cjs.qa.utilities.RetryableGridConnection;
import com.cjs.qa.utilities.SeleniumGridConfig;
import com.google.gson.JsonObject;

import io.cucumber.java.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;

public class SeleniumWebDriver {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SeleniumWebDriver.class));

  /**
   * Property key for system property "os.name". This is NOT an OS detection variable. For OS
   * detection (IS_WINDOWS, IS_MAC, IS_LINUX), use Constants.IS_WINDOWS, Constants.IS_MAC,
   * Constants.IS_LINUX instead. This constant is used to retrieve the full OS name string (e.g.,
   * "Windows 10", "Mac OS X") for Selenium platform capabilities, not for boolean OS checks.
   */
  public static final String OS_NAME = "os.name";

  public static final String PATH_SCREENSHOTS = Constants.PATH_SCREENSHOTS;
  public static final String FORMAT_SCREENSHOT = "###000";
  private String browser = Browser.CHROME;
  private Capabilities capabilities = null;
  private String gridHubName = "CSCHARER-LAPTOP";
  private int gridHubPort = 4444;
  private String gridHub = "http://" + getGridHubName() + ":" + getGridHubPort() + "/wd/hub";
  private String operatingSystem = System.getProperty(OS_NAME);
  // "Windows 10"
  private Page page = null;
  private boolean remote = true;
  private Scenario scenario = null;
  private int screenshots = 0;
  private SessionId sessionId = null;
  private WebDriver webDriver = null;
  private String vendorURL = null;
  private String version = null;

  /**
   * @param browser
   * @param remote
   * @param vendorURL
   * @param operatingSystem
   * @param version
   * @throws Throwable
   */
  public SeleniumWebDriver(
      String browser, boolean remote, String vendorURL, String operatingSystem, String version)
      throws Throwable {
    killBrowser();
    if (browser != null) {
      setBrowser(browser);
    }
    setRemote(remote);
    setVendorURL(vendorURL);
    if (operatingSystem == null) {
      operatingSystem = System.getProperty(OS_NAME);
      setOperatingSystem(operatingSystem);
    }
    setVersion(version);
    initializeWebDriver();
  }

  public void captureDesktopImage() {
    try {
      final BufferedImage image =
          new Robot()
              .createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
      ImageIO.write(image, "png", new File(getScreenshotFilePathName("Desktop")));
    } catch (final Exception e) {
      e.printStackTrace();
      LOG.error("Failed to capture desktop screenshot", e);
    }
  }

  /**
   * @param scrollAmount
   */
  public void captureScreenshot(int scrollAmount) {
    try {
      if (getWebDriver() != null) {
        String title = getWebDriver().getTitle();
        LOG.debug("Page title: [{}]", title);
        title = FSOTests.fileValidateName(title);
        LOG.debug("Validated title: [{}]", title);
        final int heightScroll = getPage().getScrollHeight();
        final String pathFileName = getScreenshotFilePathName(title);
        if (heightScroll == 0) {
          final File screenshot =
              ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.FILE);
          FileUtils.copyFile(screenshot, new File(pathFileName));
        } else {
          getPage().maximizeWindow();
          getPage().scrollToTop();
          final int heightWindow = getPage().getHeight();
          int heightScrolled = 0;
          LOG.debug("Window height: [{}], scroll height: [{}]", heightWindow, heightScroll);
          final String pathTemp = PATH_SCREENSHOTS + getSessionId() + Constants.DELIMETER_PATH;
          try {
            FileUtils.forceMkdir(new File(pathTemp));
          } catch (final IOException e) {
            LOG.error("Failed to create temp directory: {}", pathTemp, e);
          }
          int fileNumber = 1;
          do {
            final File screenshot =
                ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.FILE);
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(pathTemp);
            stringBuilder.append(title);
            stringBuilder.append("_");
            stringBuilder.append(JavaHelpers.formatNumber(fileNumber, FORMAT_SCREENSHOT));
            stringBuilder.append(".png");
            final String fileName = stringBuilder.toString();
            FileUtils.copyFile(screenshot, new File(fileName));
            getPage().scrollDown(scrollAmount);
            heightScrolled += scrollAmount;
            fileNumber++;
          } while (heightScrolled < heightScroll);
          try {
            final String pathFileNameNew = combineALLImages(pathTemp);
            FileUtils.copyFile(new File(pathFileNameNew), new File(pathFileName));
            FileUtils.deleteDirectory(new File(pathTemp));
          } catch (final Exception e) {
            e.printStackTrace();
          }
        }
      }
    } catch (final IOException e) {
      e.printStackTrace();
      LOG.error("Failed to capture full page screenshot", e);
    }
  }

  /**
   * @param pathTemp
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  private String combineALLImages(String pathTemp) throws IOException {
    final List<String> listFiles = FSOTests.filesList(pathTemp, ".png");
    final List<File> listFile = new ArrayList<>();
    final int columns = 1;
    final int rows = listFiles.size();
    for (String filePathName : listFiles) {
      listFile.add(new File(filePathName));
    }
    final BufferedImage bufferedImageSample = ImageIO.read(listFile.get(0));
    // Initializing the final image
    final BufferedImage bufferedImageFinal =
        new BufferedImage(
            bufferedImageSample.getWidth() * columns,
            bufferedImageSample.getHeight() * rows,
            bufferedImageSample.getType());
    int index = 0;
    for (int indexRow = 0; indexRow < rows; indexRow++) {
      for (int indexColumn = 0; indexColumn < columns; indexColumn++) {
        final String filePathName = listFiles.get(indexRow);
        final String fileName =
            filePathName.substring(
                filePathName.lastIndexOf(Constants.DELIMETER_PATH) + 1, filePathName.length());
        LOG.debug("Adding image file: [{}], [{}]", index, fileName);
        final BufferedImage bufferedImageTemp = ImageIO.read(listFile.get(index));
        bufferedImageFinal
            .createGraphics()
            .drawImage(
                bufferedImageTemp,
                bufferedImageSample.getWidth() * indexColumn,
                bufferedImageSample.getHeight() * indexRow,
                null);
        index++;
      }
    }
    // final String newFile = pathTemp + "FinalImage" + IExtension.JPG
    final String newFile = pathTemp + "FinalImage" + IExtension.PNG;
    boolean success = ImageIO.write(bufferedImageFinal, "png", new File(newFile));
    LOG.debug("Image write success: [{}]", success);
    return newFile;
  }

  public String getBrowser() {
    return browser;
  }

  public Capabilities getCapabilities() {
    return capabilities;
  }

  public String getExecutableVersion(Capabilities capabilities) {
    // (String)capabilities.getCapability("version")
    // Java 17: Switch expression returning value directly
    return switch (getBrowser().toLowerCase(Locale.ENGLISH)) {
      case Browser.EDGE -> (String) capabilities.getCapability("browserVersion");
      case Browser.CHROME, Browser.FIREFOX, Browser.IE, Browser.INTERNET_EXPLORER ->
          (String) capabilities.getCapability("version");
      default -> (String) capabilities.getCapability("version");
    };
  }

  public String getGridHub() {
    return gridHub;
  }

  public String getGridHubName() {
    return gridHubName;
  }

  public int getGridHubPort() {
    return gridHubPort;
  }

  public String getOperatingSystem() {
    return operatingSystem;
  }

  public Page getPage() {
    return page;
  }

  public Scenario getScenario() {
    return scenario;
  }

  /**
   * @param title
   * @return
   */
  private String getScreenshotFilePathName(String title) {
    setScreenshots(getScreenshots() + 1);
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(PATH_SCREENSHOTS);
    try {
      FileUtils.forceMkdir(new File(stringBuilder.toString()));
    } catch (final IOException e) {
      LOG.error("Failed to create screenshot directory", e);
    }
    stringBuilder.append(getBrowser().toLowerCase(Locale.ENGLISH));
    stringBuilder.append("_");
    stringBuilder.append(getSessionId());
    stringBuilder.append("_");
    stringBuilder.append(title);
    stringBuilder.append("_");
    stringBuilder.append(JavaHelpers.formatNumber(getScreenshots(), FORMAT_SCREENSHOT));
    stringBuilder.append(".png");
    return stringBuilder.toString();
  }

  public int getScreenshots() {
    return screenshots;
  }

  public SessionId getSessionId() {
    return sessionId;
  }

  public void getSessionInformation() {
    // Java 17: Pattern matching for instanceof (simplifies type checking and casting)
    if (webDriver instanceof RemoteWebDriver remoteDriver) {
      setCapabilities(remoteDriver.getCapabilities());
      setSessionId(remoteDriver.getSessionId());
    } else if (webDriver instanceof ChromeDriver chromeDriver) {
      setCapabilities(chromeDriver.getCapabilities());
      setSessionId(chromeDriver.getSessionId());
    } else if (webDriver instanceof EdgeDriver edgeDriver) {
      setCapabilities(edgeDriver.getCapabilities());
      setSessionId(edgeDriver.getSessionId());
    } else if (webDriver instanceof FirefoxDriver firefoxDriver) {
      setCapabilities(firefoxDriver.getCapabilities());
      setSessionId(firefoxDriver.getSessionId());
    } else if (webDriver instanceof InternetExplorerDriver ieDriver) {
      setCapabilities(ieDriver.getCapabilities());
      setSessionId(ieDriver.getSessionId());
      // PhantomJS support removed - use Chrome/Firefox headless instead
    } else if (webDriver instanceof HtmlUnitDriver htmlUnitDriver) {
      setCapabilities(htmlUnitDriver.getCapabilities());
      // setSessionId(((HtmlUnitDriver) webDriver).getSessionId())
    } else if (webDriver instanceof SafariDriver safariDriver) {
      setCapabilities(safariDriver.getCapabilities());
      setSessionId(safariDriver.getSessionId());
    } // Opera driver deprecated in Selenium 4
    // // else if (webDriver instanceof OperaDriver)
    // {
    //  // setCapabilities(((OperaDriver) webDriver).getCapabilities());
    //  // setSessionId(((OperaDriver) webDriver).getSessionId());
    // }
    LOG.info("Selenium WebDriver Information: {}", toString());
  }

  public String getVendorURL() {
    return vendorURL;
  }

  public String getVersion() {
    return version;
  }

  public WebDriver getWebDriver() {
    return webDriver;
  }

  public Capabilities getWebDriverCapabilities() {
    if (isRemote()) {
      return ((RemoteWebDriver) getWebDriver()).getCapabilities();
    }
    // Java 17: Switch expression returning value directly
    return switch (getBrowser().toLowerCase(Locale.ENGLISH)) {
      case Browser.EDGE -> ((EdgeDriver) getWebDriver()).getCapabilities();
      case Browser.FIREFOX -> ((FirefoxDriver) getWebDriver()).getCapabilities();
      case Browser.IE, Browser.INTERNET_EXPLORER ->
          ((InternetExplorerDriver) getWebDriver()).getCapabilities();
      case Browser.CHROME -> ((ChromeDriver) getWebDriver()).getCapabilities();
      default -> ((ChromeDriver) getWebDriver()).getCapabilities();
    };
  }

  public void getWebDriverInfo() {
    try {
      if (getWebDriver() != null) {
        String browser = getBrowser();
        LOG.debug("Browser: [{}]", browser);
        final EDriverProperties eDriverProperties =
            EDriverProperties.fromString(browser.toUpperCase(Locale.ENGLISH));
        String webDriver = eDriverProperties.getPathDriver();
        LOG.debug("WebDriver: [{}]", webDriver);
        String executable = eDriverProperties.getPathBinary();
        LOG.debug("Executable: [{}]", executable);
        String webDriverVersion = getWebDriverVersion(webDriver);
        LOG.debug("WebDriver version: [{}]", webDriverVersion);
        try {
          getWebDriverProcesses(webDriver);
        } catch (Exception e) {
          // Log but don't fail - getWebDriverProcesses is informational
          LOG.warn("Could not get WebDriver processes: {}", e.getMessage());
        }
        Capabilities capabilities = getWebDriverCapabilities();
        LOG.debug("Capabilities: [{}]", capabilities.toString());
        String executableVersion = getExecutableVersion(capabilities);
        LOG.debug("Executable version: [{}]", executableVersion);
        if (Browser.CHROME.equalsIgnoreCase(getBrowser())) {
          // Capabilities.getCapability() returns Object, must cast to Map - type safety verified at
          // runtime
          @SuppressWarnings("unchecked")
          Map<String, Object> chromeCapabilities =
              (Map<String, Object>) capabilities.getCapability(Browser.CHROME);
          // for (Entry entry : chromeCapabilities.entrySet())
          // {
          // String key = (String) entry.getKey();
          // Object value = entry.getValue();
          // LOG.debug("key:[{}]", key);
          // LOG.debug("class:[{}], value:[{}]", value.getClass().toString(), value);
          // }
          webDriverVersion = (String) chromeCapabilities.get("chromedriverVersion");
          LOG.debug("Chrome WebDriver version: [{}]", webDriverVersion);
          String[] webDriverVersionArray = webDriverVersion.split(" ");
          webDriverVersion = webDriverVersionArray[0];
          LOG.debug("Parsed WebDriver version: [{}]", webDriverVersion);
        }
      }
    } catch (Exception e) {
      // Catch Exception instead of Throwable to avoid hiding critical JVM errors
      LOG.error("Error getting WebDriver information", e);
    } catch (Error e) {
      // Re-throw Error to avoid hiding critical JVM issues (OutOfMemoryError, etc.)
      LOG.error("Critical JVM error getting WebDriver information", e);
      throw e;
    }
  }

  public void getWebDriverProcesses(String webDriver) throws Exception {
    File file = new File(webDriver);
    webDriver = file.getName();
    // webDdriver = webDdriver.replaceAll(Constants.DELIMETER_PATH, "/");
    // LOG.debug("webDriver: [{}]", webDdriver);
    // String[] webDriverArray = webDriver.split("/");
    // webDriver = webDriverArray[(driverArray.length - 1)];
    LOG.debug("WebDriver: [{}]", webDriver);
    String command =
        CommandLineTests.TASKLIST + " /fo:csv /nh /fi \"imagename eq " + webDriver + "\"";
    LOG.debug("Command: [{}]", command);
    // Wrap Processes constructor call to convert Throwable to Exception
    // Processes constructor now throws Exception instead of Throwable
    Processes processes = new Processes(command);
    // LOG.debug("Processes: {}", processes.toString());
    LOG.debug("DataTable");
    LOG.debug("DataTable: {}", processes.toDataTable().toString());
  }

  public String getWebDriverVersion(String webDriver) throws Exception {
    String webDriverVersion = "";
    if (!Browser.EDGE.equalsIgnoreCase(getBrowser())) {
      String webDriverCommand = "\"" + webDriver + "\" --version";
      LOG.debug("WebDriver command: [{}]", webDriverCommand);
      Map<String, String> webDriverMap = CommandLineTests.runProcess(webDriverCommand, true);
      LOG.debug("WebDriver map: [{}]", webDriverMap.toString());
      String[] webDriverArray = webDriverMap.get("lines").split(" ");
      webDriverVersion = webDriverArray[1];
    }
    return webDriverVersion;
  }

  public void initializeWebDriver() throws Throwable {
    final DesiredCapabilities desiredCapabilities =
        setDesiredCapabilities(getOperatingSystem(), getBrowser());
    final EDriverProperties eDriverProperties =
        EDriverProperties.fromString(getBrowser().toUpperCase(Locale.ENGLISH));
    try {
      LOG.debug("OS name: [{}]", System.getProperty(OS_NAME));
      setLocalExecutables();
      LOG.debug("Desired capabilities: [{}]", desiredCapabilities.toString());
      if (isRemote()) {
        // setProxy(getBrowser(), isRemote(), desiredCapabilities);
        if (getVendorURL() == null) {
          // Use enhanced Grid connection with version validation and retry logic
          String gridUrl = getGridHub();

          // Step 1: Check if Grid is ready (health check)
          if (LOG.isInfoEnabled()) {
            LOG.info("Checking Grid readiness at: {}", gridUrl);
          }
          boolean gridReady = SeleniumGridConfig.isGridReady(gridUrl);
          if (!gridReady) {
            LOG.warn(
                "Grid may not be ready, but proceeding with connection attempt (retry logic will handle)");
          } else {
            LOG.info("✅ Grid is ready");
          }

          // Step 2: Validate Grid version (optional - can be skipped via environment variable)
          String skipVersionCheck = System.getenv("SKIP_VERSION_CHECK");
          if (skipVersionCheck == null || !"true".equalsIgnoreCase(skipVersionCheck)) {
            try {
              if (LOG.isInfoEnabled()) {
                LOG.info("Validating Grid version compatibility...");
              }
              SeleniumGridConfig.validateGridVersion(gridUrl);
            } catch (QAException e) {
              // Version mismatch - fail fast
              LOG.error("Version validation failed: {}", e.getMessage());
              throw new QAException(
                  String.format(
                      "Selenium Grid version validation failed at %s: %s", gridUrl, e.getMessage()),
                  e);
            }
          } else {
            LOG.info("Version validation skipped (SKIP_VERSION_CHECK=true)");
          }

          // Step 3: Attempt connection with retry logic
          if (LOG.isInfoEnabled()) {
            LOG.info("Connecting to Grid at: {} with retry logic", gridUrl);
          }
          try {
            RemoteWebDriver driver =
                RetryableGridConnection.connectWithRetry(gridUrl, desiredCapabilities);
            setWebDriver(driver);
            if (getWebDriver() != null) {
              LOG.info("✅ Remote Web Driver successfully instantiated");
            }
          } catch (QAException e) {
            LOG.error("Failed to connect to Grid after retries: {}", e.getMessage());
            throw new QAException(
                String.format(
                    "Remote Web Driver could not be instantiated at %s: %s",
                    gridUrl, e.getMessage()),
                e);
          }

          LOG.info("Grid Hub: [{}]", gridUrl);
        } else {
          // Vendor URL (not Grid) - use direct connection without validation
          if (LOG.isInfoEnabled()) {
            LOG.info("Connecting to vendor URL: {}", getVendorURL());
          }
          setWebDriver(
              new RemoteWebDriver(URI.create(getVendorURL()).toURL(), desiredCapabilities));
        }
      } else {
        // Java 17: Switch expression with block syntax for complex cases
        switch (getBrowser().toLowerCase(Locale.ENGLISH)) {
          case Browser.EDGE -> {
            // WebDriverManager automatically downloads and sets up Edge driver
            WebDriverManager.edgedriver().setup();
            EdgeOptions edgeOpts = new EdgeOptions();
            edgeOpts.merge(desiredCapabilities);
            setWebDriver(new EdgeDriver(edgeOpts));
          }
          case Browser.FIREFOX -> {
            // WebDriverManager automatically downloads and sets up Gecko driver
            WebDriverManager.firefoxdriver().setup();
            final FirefoxOptions firefoxOptions = new FirefoxOptions();
            LOG.debug(
                "System.getProperty({}{}{}): [{}]",
                Constants.QUOTE_DOUBLE,
                eDriverProperties.getWebDriverType(),
                Constants.QUOTE_DOUBLE,
                System.getProperty(eDriverProperties.getWebDriverType()));
            firefoxOptions.setBinary(eDriverProperties.getPathBinary());
            // setWebDriver(new MarionetteDriver());
            setWebDriver(new FirefoxDriver(firefoxOptions));
            //
            GeckoDriverService service = null;
            try {
              service =
                  new GeckoDriverService.Builder()
                      .usingDriverExecutable(new File("path to geckodriver"))
                      .usingAnyFreePort()
                      .usingAnyFreePort()
                      .build();
              service.start();
              // GeckoDriver needs the Proxy set in
              // RequiredCapabilities
              FirefoxOptions ffOpts = new FirefoxOptions();
              ffOpts.merge(desiredCapabilities);
              setWebDriver(new FirefoxDriver(service, ffOpts));
            } finally {
              if (service != null) {
                if (service.isRunning()) {
                  service.stop();
                }
                service.close();
              }
            }
          }
          case Browser.HTML_UNIT -> {
            // setWebDriver(new
            // HtmlUnitDriver(desiredCapabilities));
            setWebDriver(new HtmlUnitDriver(desiredCapabilities));
            ((HtmlUnitDriver) webDriver).setJavascriptEnabled(true);
            // final WebClient webClient = new WebClient();
            // try
            // {
            // final HtmlPage htmlPage =
            // webClient.getPage("http://stackoverflow" +
            // IExtension.COM + "/");
            // LOG.debug("{}", htmlPage.asNormalizedText());
            // } catch (final Exception e)
            // {
            // LOG.error("Exception occurred", e);
            // }
          }
          case Browser.IE, Browser.INTERNET_EXPLORER -> {
            // (very slow)
            // WebDriverManager automatically downloads and sets up IE driver
            WebDriverManager.iedriver().setup();
            InternetExplorerOptions ieOpts = new InternetExplorerOptions();
            ieOpts.merge(desiredCapabilities);
            setWebDriver(new InternetExplorerDriver(ieOpts));
          }
          case Browser.SAFARI -> {
            // Safari driver is bundled with Safari - no WebDriverManager needed
            setWebDriver(new SafariDriver());
          }
          case Browser.CHROME -> {
            // WebDriverManager automatically downloads and sets up Chrome driver
            WebDriverManager.chromedriver().setup();
            // final ChromeOptions chromeOptions =
            // setChromeOptions(eDriverProperty.getWebDriverType(),
            // eDriverProperty.getPathBinary());
            final ChromeOptions chromeOptions = setChromeOptions(null, null);
            setWebDriver(new ChromeDriver(chromeOptions));
            // setWebDriver(new ChromeDriver(desiredCapabilities));
          }
          default -> {
            // Default to Chrome
            WebDriverManager.chromedriver().setup();
            final ChromeOptions defaultChromeOptions = setChromeOptions(null, null);
            setWebDriver(new ChromeDriver(defaultChromeOptions));
          }
        }
      }
      getSessionInformation();
      // Move browser to last monitor.
      int lastMonitor = JavaHelpers.getMonitorCount() - 1;
      if (lastMonitor != 0) {
        int position = lastMonitor * 2000;
        Point point = new Point(position, 0);
        getWebDriver().manage().window().setPosition(point);
        // Resize browser.
        int pointX = JavaHelpers.getMonitorWidth(lastMonitor);
        int pointY = JavaHelpers.getMonitorHeight(lastMonitor);
        Dimension dimension = new Dimension(pointX, pointY);
        getWebDriver().manage().window().setSize(dimension);
      }
      getWebDriver().manage().window().maximize();
      setPage(new Page(getWebDriver()));
      getWebDriverInfo();
    } catch (final Exception e) {
      throw new QAException(JavaHelpers.getCurrentMethodName(), e);
    }
  }

  public boolean isRemote() {
    return remote;
  }

  public void killBrowser() {
    try {
      if (getWebDriver() != null) {
        getWebDriver().close();
      }
    } catch (final Exception e) {
      // Intentionally empty - browser may already be closed
      if (Environment.isLogAll()) {
        LOG.debug("Browser close failed (expected if already closed)");
      }
    }
    try {
      if (getWebDriver() != null) {
        getWebDriver().quit();
      }
    } catch (final Exception e) {
      // Intentionally empty - browser may already be terminated
      if (Environment.isLogAll()) {
        LOG.debug("Browser quit failed (expected if already terminated)");
      }
    }
    setWebDriver(null);
  }

  /**
   * @param browser
   */
  public void setBrowser(String browser) {
    this.browser = browser;
  }

  /**
   * @param capabilities
   */
  public void setCapabilities(Capabilities capabilities) {
    this.capabilities = capabilities;
  }

  private ChromeOptions setChromeOptions(String pathFileExecutable, String pathFileBinary) {
    final ChromeOptions chromeOptions = new ChromeOptions();
    final ChromeOptions desiredCapabilities = new ChromeOptions();
    final Map<String, Object> mapPeferences = new HashMap<>();
    final List<String> arguments =
        Arrays.asList(
            "disable-popup-blocking",
            "test-type",
            "start-maximized",
            "--disable-popup-blocking",
            "--start-maximized");
    // chromeOptions.addArguments("chrome.switches", "--disable-extensions
    // --disable-extensions-file-access-check
    // --disable-extensions-http-throttling --disable-infobars
    // --enable-automation --start-maximized");
    chromeOptions.addArguments(arguments);
    // chromeOptions.AddUserProfilePreference("credentials_enable_service",
    // false);
    // chromeOptions.AddUserProfilePreference("profile.password_manager_enabled",
    // false);
    mapPeferences.put("credentials_enable_service", false);
    mapPeferences.put("profile.password_manager_enabled", false);
    chromeOptions.setExperimentalOption("prefs", mapPeferences);
    if (pathFileExecutable != null) {
      System.setProperty("webdriver.chrome.driver", pathFileExecutable);
    }
    if (pathFileBinary != null) {
      chromeOptions.setBinary(pathFileBinary);
    }
    desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
    return chromeOptions;
  }

  /**
   * @param operatingSystem
   * @param browser
   * @return
   */
  private DesiredCapabilities setDesiredCapabilities(String operatingSystem, String browser) {
    final EDriverProperties eDriverProperties =
        EDriverProperties.fromString(browser.toUpperCase(Locale.ENGLISH));
    DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
    // Java 17: Switch expression with block syntax for complex cases
    switch (browser.toLowerCase(Locale.ENGLISH)) {
      case "android" -> {
        desiredCapabilities = new DesiredCapabilities();
      }
      case Browser.CHROME -> {
        final ChromeOptions chromeOptions = setChromeOptions(null, null);
        desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.merge(chromeOptions);
      }
      case Browser.EDGE -> {
        EdgeOptions edgeOpts2 = new EdgeOptions();
        desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.merge(edgeOpts2);
      }
      case Browser.FIREFOX -> {
        final FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setBinary(eDriverProperties.getPathBinary());
        desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.merge(firefoxOptions);
      }
      case Browser.HTML_UNIT -> {
        desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setBrowserName(Browser.HTML_UNIT);
      }
      case Browser.HTML_UNIT_WITH_JS -> {
        desiredCapabilities = new DesiredCapabilities();
      }
      case Browser.IE, Browser.INTERNET_EXPLORER -> {
        // (very slow)
        InternetExplorerOptions ieOpts2 = new InternetExplorerOptions();
        ieOpts2.introduceFlakinessByIgnoringSecurityDomains();
        ieOpts2.ignoreZoomSettings();
        ieOpts2.requireWindowFocus();
        desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.merge(ieOpts2);
        desiredCapabilities.setCapability(
            eDriverProperties.getWebDriverType(), eDriverProperties.getPathDriver());
      }
      case "ipad", "iphone" -> {
        desiredCapabilities = new DesiredCapabilities();
      }
      // PhantomJS support removed - use Chrome/Firefox headless instead
      case Browser.SAFARI -> {
        SafariOptions safariOpts = new SafariOptions();
        desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.merge(safariOpts);
      }
      default -> {
        LOG.warn("Unknown browser: {}. Using default capabilities.", browser);
        desiredCapabilities = new DesiredCapabilities();
      }
    }
    // Java 17: Switch expression with multiple case labels
    switch (operatingSystem.toLowerCase(Locale.ENGLISH)) {
      case OS.ANDROID -> desiredCapabilities.setPlatform(Platform.ANDROID);
      case OS.ANY -> desiredCapabilities.setPlatform(Platform.ANY);
      case OS.EL_CAPITAN -> desiredCapabilities.setPlatform(Platform.EL_CAPITAN);
      case OS.LINUX -> desiredCapabilities.setPlatform(Platform.LINUX);
      case OS.MAC -> desiredCapabilities.setPlatform(Platform.MAC);
      case OS.MAVERICKS -> desiredCapabilities.setPlatform(Platform.MAVERICKS);
      case OS.MOINTAIN_LION -> desiredCapabilities.setPlatform(Platform.MOUNTAIN_LION);
      case OS.SNOW_LEOPARD -> desiredCapabilities.setPlatform(Platform.SNOW_LEOPARD);
      case OS.UNIX -> desiredCapabilities.setPlatform(Platform.UNIX);
      case OS.VISTA -> desiredCapabilities.setPlatform(Platform.VISTA);
      case OS.WIN8 -> desiredCapabilities.setPlatform(Platform.WIN8);
      case OS.WIN8_1 -> desiredCapabilities.setPlatform(Platform.WIN8_1);
      case OS.WINDOWS -> desiredCapabilities.setPlatform(Platform.WINDOWS);
      case OS.WINDOWS_10 -> desiredCapabilities.setPlatform(Platform.WIN10);
      case OS.XP -> desiredCapabilities.setPlatform(Platform.XP);
      case OS.YOSEMITE -> desiredCapabilities.setPlatform(Platform.YOSEMITE);
      default -> {
        LOG.warn("Unknown operating system: {}. Using ANY platform.", operatingSystem);
        desiredCapabilities.setPlatform(Platform.ANY);
      }
    }
    return desiredCapabilities;
  }

  /**
   * @param driver
   */
  public void setDriver(WebDriver driver) {
    setWebDriver(driver);
  }

  /**
   * @param gridHub
   */
  public void setGridHub(String gridHub) {
    this.gridHub = gridHub;
  }

  /**
   * @param gridHubName
   */
  public void setGridHubName(String gridHubName) {
    this.gridHubName = gridHubName;
  }

  /**
   * @param gridHubPort
   */
  public void setGridHubPort(int gridHubPort) {
    this.gridHubPort = gridHubPort;
  }

  private void setLocalExecutables() {
    final EDriverProperties eDriverProperties =
        EDriverProperties.fromString(getBrowser().toUpperCase(Locale.ENGLISH));
    // Java 17: Switch expression with block syntax for complex cases
    switch (getBrowser().toLowerCase(Locale.ENGLISH)) {
      case Browser.SAFARI -> {
        // If you wish for safari to forget session everytime
        // SafariOptions.setUseCleanSession(true); // Removed in Selenium 4
        setWebDriver(new SafariDriver());
      }
      case Browser.CHROME, Browser.EDGE, Browser.FIREFOX, Browser.IE, Browser.INTERNET_EXPLORER -> {
        // (IE is very slow)
        System.setProperty(eDriverProperties.getWebDriverType(), eDriverProperties.getPathDriver());
      }
      default -> {
        System.setProperty(eDriverProperties.getWebDriverType(), eDriverProperties.getPathDriver());
      }
    }
    LOG.debug(
        "{} - webDriverType: [{}], PathDriver: [{}], PathBinary: [{}]",
        JavaHelpers.getCurrentMethodName(),
        eDriverProperties.getWebDriverType(),
        eDriverProperties.getPathDriver(),
        eDriverProperties.getPathBinary());
  }

  /**
   * @param operatingSystem
   */
  public void setOperatingSystem(String operatingSystem) {
    this.operatingSystem = operatingSystem;
  }

  /**
   * @param page
   */
  public void setPage(Page page) {
    this.page = page;
  }

  public void setProxy(String browser, boolean isRemote, DesiredCapabilities desiredCapabilities)
      throws Exception {
    if (!isRemote) {
      setGridHubName("localhost");
      setGridHubPort(8080);
      setGridHub("http://" + getGridHubName() + ":" + getGridHubPort() + "/wd/hub");
    }
    String proxyAddress = getGridHubName() + ":" + getGridHubPort();
    // Java 17: Switch expression with block syntax for complex cases
    switch (browser) {
      case Browser.FIREFOX -> {
        JsonObject json = new JsonObject();
        json.addProperty("proxyType", "MANUAL");
        json.addProperty("httpProxy", getGridHub());
        json.addProperty("httpProxyPort", getGridHubPort());
        json.addProperty("sslProxy", getGridHub());
        json.addProperty("sslProxyPort", getGridHubPort());
        desiredCapabilities.setCapability("proxy", json);
      }
      case Browser.CHROME -> {
        // Fall through to the default logic
      }
      default -> {
        Proxy proxy = new Proxy();
        proxy.setHttpProxy(proxyAddress).setSslProxy(proxyAddress);
        // Use setHttpProxy() or setSslProxy() instead of setFtpProxy()
        // This will typically cover the necessary traffic for browser automation.
        // Optional: set a SOCKS proxy if needed
        // proxy.setSocksProxy(proxyServerUrl);
        // proxy.setSocksVersion(5); // Specify SOCKS version if using SOCKS proxy
        // proxy.setHttpProxy(proxyAddress).setFtpProxy(proxyAddress).setSslProxy(proxyAddress);
        desiredCapabilities.setCapability(CapabilityType.PROXY, proxy);
      }
    }
  }

  /**
   * @param remote
   */
  public void setRemote(boolean remote) {
    this.remote = remote;
  }

  /**
   * @param scenario
   */
  public void setScenario(Scenario scenario) {
    this.scenario = scenario;
  }

  /**
   * @param screenshots
   */
  public void setScreenshots(int screenshots) {
    this.screenshots = screenshots;
  }

  /**
   * @param sessionId
   */
  public void setSessionId(SessionId sessionId) {
    this.sessionId = sessionId;
  }

  /**
   * @param vendorURL
   */
  public void setVendorURL(String vendorURL) {
    this.vendorURL = vendorURL;
  }

  /**
   * @param version
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * @param webDriver
   */
  public void setWebDriver(WebDriver webDriver) {
    this.webDriver = webDriver;
  }

  @Override
  public String toString() {
    final StringBuilder stringBuilder = new StringBuilder();
    final Constants c = new Constants();
    c.setFormatPretty(true);
    int newLine = 0;
    if (c.isFormatPretty()) {
      newLine = 1;
    }
    int tab = 0;
    // stringBuilder.append(Constants.nlTab(newLine, tab) + "{");
    // tab = Constants.tabIncriment(tab, (newLine * 1));
    // stringBuilder.append(Constants.nlTab(newLine, tab) + Constants.QUOTE_DOUBLE +
    // "Selenium WebDriver
    // Information" + Constants.QUOTE_DOUBLE + ":");
    stringBuilder.append(Constants.nlTab(newLine, tab) + "{");
    tab = Constants.tabIncriment(tab, newLine * 1);
    stringBuilder.append(
        Constants.nlTab(newLine, tab)
            + Constants.QUOTE_DOUBLE
            + "Grid Hub Name"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + getGridHubName()
            + Constants.QUOTE_DOUBLE
            + ",");
    stringBuilder.append(
        Constants.nlTab(newLine, tab)
            + Constants.QUOTE_DOUBLE
            + "Grid Hub Port"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + getGridHubPort()
            + Constants.QUOTE_DOUBLE
            + ",");
    stringBuilder.append(
        Constants.nlTab(newLine, tab)
            + Constants.QUOTE_DOUBLE
            + "Grid Hub"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + getGridHub()
            + Constants.QUOTE_DOUBLE
            + ",");
    stringBuilder.append(
        Constants.nlTab(newLine, tab)
            + Constants.QUOTE_DOUBLE
            + "Operating System"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + getOperatingSystem()
            + Constants.QUOTE_DOUBLE
            + ",");
    stringBuilder.append(
        Constants.nlTab(newLine, tab)
            + Constants.QUOTE_DOUBLE
            + "Remote"
            + Constants.QUOTE_DOUBLE
            + ": "
            + isRemote()
            + ",");
    final Scenario scenario = getScenario();
    if (scenario == null) {
      stringBuilder.append(
          Constants.nlTab(newLine, tab)
              + Constants.QUOTE_DOUBLE
              + "Scenario"
              + Constants.QUOTE_DOUBLE
              + ": "
              + Constants.QUOTE_DOUBLE
              + scenario
              + Constants.QUOTE_DOUBLE
              + ",");
    } else {
      stringBuilder.append(
          Constants.nlTab(newLine, tab)
              + Constants.QUOTE_DOUBLE
              + "Scenario"
              + Constants.QUOTE_DOUBLE
              + ": "
              + Constants.QUOTE_DOUBLE
              + getScenario().toString()
              + Constants.QUOTE_DOUBLE
              + ",");
    }
    stringBuilder.append(
        Constants.nlTab(newLine, tab)
            + Constants.QUOTE_DOUBLE
            + "Screenshot Count"
            + Constants.QUOTE_DOUBLE
            + ": "
            + getScreenshots()
            + ",");
    stringBuilder.append(
        Constants.nlTab(newLine, tab)
            + Constants.QUOTE_DOUBLE
            + "Session ID"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + getSessionId()
            + Constants.QUOTE_DOUBLE
            + ",");
    final WebDriver webDriver = getWebDriver();
    if (webDriver == null) {
      stringBuilder.append(
          Constants.nlTab(newLine, tab)
              + Constants.QUOTE_DOUBLE
              + "Web Driver"
              + Constants.QUOTE_DOUBLE
              + ": "
              + Constants.QUOTE_DOUBLE
              + webDriver
              + Constants.QUOTE_DOUBLE
              + ",");
    } else {
      stringBuilder.append(
          Constants.nlTab(newLine, tab)
              + Constants.QUOTE_DOUBLE
              + "Web Driver"
              + Constants.QUOTE_DOUBLE
              + ": "
              + Constants.QUOTE_DOUBLE
              + getWebDriver().toString()
              + Constants.QUOTE_DOUBLE
              + ",");
    }
    stringBuilder.append(
        Constants.nlTab(newLine, tab)
            + Constants.QUOTE_DOUBLE
            + "Vendor URL"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + getVendorURL()
            + Constants.QUOTE_DOUBLE
            + ",");
    stringBuilder.append(
        Constants.nlTab(newLine, tab)
            + Constants.QUOTE_DOUBLE
            + "Version"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + getVersion()
            + Constants.QUOTE_DOUBLE
            + ",");
    stringBuilder.append(
        Constants.nlTab(newLine, tab)
            + Constants.QUOTE_DOUBLE
            + "Capabilities"
            + Constants.QUOTE_DOUBLE
            + ":");
    stringBuilder.append(Constants.nlTab(newLine, tab) + "{");
    tab = Constants.tabIncriment(tab, newLine * 1);
    stringBuilder.append(
        Constants.nlTab(newLine, tab)
            + Constants.QUOTE_DOUBLE
            + "Browser"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + getCapabilities().getBrowserName()
            + Constants.QUOTE_DOUBLE
            + ",");
    stringBuilder.append(
        Constants.nlTab(newLine, tab)
            + Constants.QUOTE_DOUBLE
            + "Platform"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + getCapabilities().getPlatformName()
            + Constants.QUOTE_DOUBLE
            + ",");
    stringBuilder.append(
        Constants.nlTab(newLine, tab)
            + Constants.QUOTE_DOUBLE
            + "Version"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + getCapabilities().getBrowserVersion()
            + Constants.QUOTE_DOUBLE
            + "");
    tab = Constants.tabIncriment(tab, newLine * -1);
    stringBuilder.append(Constants.nlTab(newLine, tab) + "}");
    tab = Constants.tabIncriment(tab, newLine * -1);
    stringBuilder.append(Constants.nlTab(newLine, tab) + "}");
    // tab = Constants.tabIncriment(tab, (newLine * -1));
    // stringBuilder.append(Constants.nlTab(newLine, tab) + "}");
    return stringBuilder.toString();
  }
}
