package com.cjs.qa.selenium;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.support.ui.Select;

import com.cjs.qa.core.Environment;
import com.cjs.qa.core.QAException;
import com.cjs.qa.utilities.CommandLineTests;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.DateHelpersTests;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.IExtension;
import com.google.gson.JsonObject;

import io.cucumber.java.Scenario;

public interface ISelenium {
  Logger LOG = LogManager.getLogger(ISelenium.class);

  String BROWSER_DEFAULT = "chrome";
  String SELENIUM_GRID_HUB_NAME = "CSCHARER-LAPTOP";
  int SELENIUM_GRID_HUB_NAMEPORT = 4444;
  String SELENIUM_GRID_HUB =
      "http://"
          + SELENIUM_GRID_HUB_NAME
          + ":"
          + String.valueOf(SELENIUM_GRID_HUB_NAMEPORT)
          + "/wd/hub";
  String FIELD_NOT_CODED = " Field is NOT Coded or the Name is Incorrect!";
  String PATH_SCREENSHOTS = "./target/reports/Screenshots/";

  /**
   * @param webDriver
   * @param filePathName
   */
  default void captureScreenshot(WebDriver webDriver, String filePathName) {
    if (Environment.isLogAll()) {
      LOG.debug("{}", "webDriver:[" + webDriver.toString() + "]");
    }
    try {
      // Screenshot capture - commented out for future use
      // final byte[] screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
      // scenarioObject.attach(screenshot, "image/png");
      @SuppressWarnings("unused")
      final byte[] screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
    } catch (final Exception e) {
      LOG.error("Error Capturing Screenshot.");
    }
  }

  /**
   * @param scenarioObject
   * @param fileName
   */
  default void cukeAttachFile(Scenario scenarioObject, String fileName) {
    // This is not working yet.
    try {
      scenarioObject.attach(
          Files.readAllBytes(Paths.get(fileName)), "application/vnd.ms-excel", fileName);
    } catch (final IOException e) {
      LOG.error("Exception occurred", e);
    }
  }

  /**
   * @param scenarioObject
   * @param webDriver
   */
  default void embedScreenshot(Scenario scenarioObject, WebDriver webDriver) {
    if (Environment.isLogAll()) {
      LOG.debug(
          "scenarioObject.ID:[{}], scenarioObject.Name:[{}], webDriver:[{}]",
          scenarioObject.getId(),
          scenarioObject.getName(),
          webDriver.toString());
    }
    try {
      final byte[] screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
      scenarioObject.attach(screenshot, "image/png", "screenshot");
    } catch (final Exception e) {
      LOG.error("Error Capturing Screenshot.");
    }
  }

  // /**
  // * @param webDriver
  // * @param xPath
  // * @return
  // */
  // public default boolean flashCurrentElement(WebDriver webDriver, String
  // xPath)
  // {
  // try
  // {
  // final By by = By.xpath(xPath);
  // final WebElement webElement = webDriver.findElement(by);
  // return new Page(webDriver).flashCurrentElement(webDriver, webElement,
  // 25);
  // } catch (final Exception e)
  // {
  // e.printStackTrace();
  // }
  // return false;
  // }

  /**
   * @param parentList
   * @param webElement
   * @param byType
   * @return
   */
  static String getWebElementXPath(List<Integer> parentList, WebElement webElement, String byType)
      throws QAException {
    final String webElementString = webElement.toString();
    // [[[[RemoteWebDriver: MicrosoftEdge on ANY
    // (F5466750-F292-4D4A-9637-5B0755B92C49)] -> xpath:
    // [[[[EdgeDriver: MicrosoftEdge on ANY
    // (F5466750-F292-4D4A-9637-5B0755B92C49)] -> xpath:
    final String indexer = webElementString.substring(0, webElementString.indexOf("Driver:"));
    final int parents = (indexer.lastIndexOf("[") + 1) / 2;
    if (parents != parentList.size()) {
      Assert.fail(
          "The parentList ["
              + parentList.size()
              + "] does not match the number of parents ["
              + parents
              + "]");
    }
    final String[] nodes = webElementString.split(" -> xpath: .");
    final StringBuilder stringBuilder = new StringBuilder();
    for (int indexLevel = 1; indexLevel <= parentList.size(); indexLevel++) {
      final int nodeIndex = parentList.get(indexLevel - 1);
      String node = nodes[indexLevel];
      final String[] elements = node.split(Constants.DELIMETER_PATH + "]");
      final List<String> nodeList = new ArrayList<>();
      for (final String element : elements) {
        if (element.contains("[")) {
          nodeList.add(element + "]");
        } else {
          nodeList.add(element);
        }
      }
      node = String.join("", nodeList);
      stringBuilder.append(node);
      if (nodeIndex > 0) {
        stringBuilder.append("[" + nodeIndex + "]");
      }
    }
    return stringBuilder.toString();
  }

  // http://stackoverflow" + IExtension.COM +
  // "/questions/10660291/highlight-elements-in-webdriver-during-runtime
  static String getAbsoluteXPath(WebDriver webDriver, WebElement element) {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("function absoluteXPath(element){");
    stringBuilder.append("var comp, comps=[];");
    stringBuilder.append("var parent=null;");
    stringBuilder.append("var xpath='';");
    stringBuilder.append("var getPos=function(element){");
    stringBuilder.append("var position=1, curNode;");
    stringBuilder.append("if (element.nodeType==Node.ATTRIBUTE_NODE){");
    stringBuilder.append("return null;");
    stringBuilder.append("}");
    stringBuilder.append(
        "for (curNode=element.previousSibling; curNode; curNode=curNode.previousSibling){");
    stringBuilder.append("if (curNode.nodeName==element.nodeName){");
    stringBuilder.append("++position;");
    stringBuilder.append("}}");
    stringBuilder.append("return position;");
    stringBuilder.append("};");
    stringBuilder.append("if (element instanceof Document){");
    stringBuilder.append("return '/';");
    stringBuilder.append("}");
    stringBuilder.append(
        "for (; element && !(element instanceof Document);"
            + " element=element.nodeType==Node.ATTRIBUTE_NODE ? element.ownerElement :"
            + " element.parentNode){");
    stringBuilder.append(
        "comp=comps[comps.length] ={};switch (element.nodeType){case Node.TEXT_NODE:");
    stringBuilder.append(
        "comp.name='text()';break;case Node.ATTRIBUTE_NODE:comp.name='@' +" + " element.nodeName;");
    stringBuilder.append("break;");
    stringBuilder.append(
        "case Node.PROCESSING_INSTRUCTION_NODE:comp.name='processing-instruction()';");
    stringBuilder.append("break;");
    stringBuilder.append(
        "case Node.COMMENT_NODE:comp.name='comment()';break;case Node.ELEMENT_NODE:");
    stringBuilder.append("comp.name=element.nodeName;break;}comp.position=getPos(element);}");
    stringBuilder.append("for (var i=comps.length - 1; i >= 0; i--){comp=comps[i];");
    stringBuilder.append(
        "xpath += '/' + comp.name.toLowerCase(Locale.ENGLISH);if (comp.position !== null){");
    stringBuilder.append("xpath += '[' + comp.position + ']';}}return xpath;}");
    stringBuilder.append("return absoluteXPath(arguments[0]);");
    return (String)
        ((JavascriptExecutor) webDriver).executeScript(stringBuilder.toString(), element);
  }

  // /**
  // * @param webDriver
  // * @param xPath
  // * @return
  // */
  // public default boolean highlightCurrentElement(WebDriver webDriver,
  // String xPath)
  // {
  // try
  // {
  // final By by = By.xpath(xPath);
  // final WebElement webElement = webDriver.findElement(by);
  // new Page(webDriver).highlightCurrentElement(webDriver, webElement);
  // return true;
  // } catch (final Exception e)
  // {
  // e.printStackTrace();
  // }
  // return false;
  // }

  /**
   * @param webDriver
   */
  default void killBrowser(WebDriver webDriver) {
    try {
      webDriver.close();
    } catch (final Exception e) {
      // Intentionally empty - browser may already be closed
      if (Environment.isLogAll()) {
        LOG.error("Browser close failed (expected if already closed)");
      }
    }
    try {
      webDriver.quit();
    } catch (final Exception e) {
      // Intentionally empty - browser may already be terminated
      if (Environment.isLogAll()) {
        LOG.error("Browser quit failed (expected if already terminated)");
      }
    }
    webDriver = null;
  }

  /**
   * @param webDriver
   * @param browser
   */
  default void killBrowser(WebDriver webDriver, String browser) {
    if (Environment.isLogAll()) {
      LOG.debug("{}", "browser:[" + browser + "], driver:[" + webDriver.toString() + "]");
    }
    try {
      webDriver.close();
    } catch (final Exception e) {
      // Intentionally empty - browser may already be closed
      if (Environment.isLogAll()) {
        LOG.error("Browser close failed (expected if already closed)");
      }
    }
    try {
      webDriver.quit();
    } catch (final Exception e) {
      // Intentionally empty - browser may already be terminated
      if (Environment.isLogAll()) {
        LOG.error("Browser quit failed (expected if already terminated)");
      }
    }
    killBrowserProcesses(browser);
  }

  /**
   * @param browser
   */
  static void killBrowserProcesses(String browser) {
    if (Environment.isLogAll()) {
      LOG.debug("browser:[[{}]", browser + "]");
    }
    String processesRunning = null;
    switch (browser.toLowerCase(Locale.ENGLISH)) {
      case "chrome":
        processesRunning = "Chrome.exe" + Constants.DELIMETER_LIST + "chromedriver.exe";
        break;
      case "edge":
        processesRunning =
            "MicrosoftEdge.exe" + Constants.DELIMETER_LIST + "MicrosoftWebDriver.exe";
        break;
      case "firefox":
        break;
      case "ie":
        processesRunning = "iexplore.exe" + Constants.DELIMETER_LIST + "IEDriverServer.exe";
        break;
      case "safari":
        processesRunning = null;
        break;
      default:
        break;
    }
    if (processesRunning != null) {
      try {
        final String[] processes = processesRunning.split(Constants.DELIMETER_LIST);
        for (final String process : processes) {
          if (CommandLineTests.isProcessRunning(process)) {
            CommandLineTests.killProcess(process);
          }
        }
      } catch (final Exception e) {
        e.printStackTrace();
        LOG.error("Exception occurred", e);
      }
    }
  }

  /**
   * @param webDriver
   */
  default void maximize(WebDriver webDriver) {
    try {
      webDriver.manage().window().maximize();
    } catch (final Exception e) {
      // Intentionally empty - maximize may not be supported in headless/Grid
      if (Environment.isLogAll()) {
        LOG.debug("Maximize not supported (expected in headless/Grid mode)");
      }
    }
  }

  // /**
  // * @param webDriver
  // * @param by
  // */
  // public default void moveToElement(WebDriver webDriver, By by)
  // {
  // final WebElement webElement = webDriver.findElement(by);
  // moveToElement(webDriver, webElement);
  // }
  //
  // /**
  // * @param webDriver
  // * @param webElement
  // */
  // public default void moveToElement(WebDriver webDriver, WebElement
  // webElement)
  // {
  // // ((JavascriptExecutor)
  // // webDriver).executeScript("arguments[0].moveToElement(true);",
  // // webElement);
  // final Actions actions = new Actions(webDriver);
  // actions.moveToElement(webElement).perform();
  // }
  //
  // /**
  // * @param webDriver
  // * @param xPath
  // */
  // public default void moveToElement(WebDriver webDriver, String xPath)
  // {
  // moveToElement(webDriver, By.xpath(xPath));
  // }
  //
  // /**
  // * @param webDriver
  // * @param by
  // */
  // public default void scrollToElement(WebDriver webDriver, By by)
  // {
  // final WebElement webElement = webDriver.findElement(by);
  // scrollToElement(webDriver, webElement);
  // }
  //
  // /**
  // * @param webDriver
  // * @param webElement
  // */
  // public default void scrollToElement(WebDriver webDriver, WebElement
  // webElement)
  // {
  // ((JavascriptExecutor)
  // webDriver).executeScript("arguments[0].scrollIntoView(true);",
  // webElement);
  // }
  //
  // /**
  // * @param webDriver
  // * @param xPath
  // */
  // public default void scrollToElement(WebDriver webDriver, String xPath)
  // {
  // scrollToElement(webDriver, By.xpath(xPath));
  // }

  static ChromeOptions setChromeOptions() {
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
    desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
    return chromeOptions;
  }

  /**
   * @param operatingSystem
   * @param browser
   * @return
   */
  static DesiredCapabilities setDesiredCapabilities(String operatingSystem, String browser) {
    DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
    switch (browser.toLowerCase(Locale.ENGLISH)) {
      case "android":
        desiredCapabilities = new DesiredCapabilities();
        break;
      case "chrome":
        final ChromeOptions chromeOptions = setChromeOptions();
        desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.merge(chromeOptions);
        break;
      case "edge":
        EdgeOptions edgeOptions = new EdgeOptions();
        desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.merge(edgeOptions);
        break;
      case "firefox":
        System.setProperty(
            "webdriver.gecko.driver", Constants.PATH_DRIVERS_LOCAL + "geckodriver.exe");
        // System.setProperty("webdriver.gecko.driver",
        // "/home/user/bin/");
        // System.setProperty("webdriver.gecko.driver",
        // "/home/user/bin/geckodriver.exe");
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.merge(firefoxOptions);
        break;
      case "htmlunit":
        desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setBrowserName("htmlunit");
        break;
      case "htmlunitwithjs":
        desiredCapabilities = new DesiredCapabilities();
        break;
      case "ie":
      case "internet explorer":
        InternetExplorerOptions ieOptions = new InternetExplorerOptions();
        desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.merge(ieOptions);
        break;
      case "ipad":
        desiredCapabilities = new DesiredCapabilities();
        break;
      case "iphone":
        desiredCapabilities = new DesiredCapabilities();
        break;
      case "opera":
        desiredCapabilities = new DesiredCapabilities();
        break;
      // PhantomJS support removed - use Chrome/Firefox headless instead
      case "safari":
        SafariOptions safariOptions = new SafariOptions();
        desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.merge(safariOptions);
        break;
      default:
        LOG.debug("Unknown browser: [{}]", browser + ". Using default capabilities.");
        desiredCapabilities = new DesiredCapabilities();
        break;
    }
    switch (operatingSystem.toUpperCase(Locale.ENGLISH)) {
      case "android":
        desiredCapabilities.setPlatform(Platform.ANDROID);
        break;
      case "any":
        desiredCapabilities.setPlatform(Platform.ANY);
        break;
      case "el capitan":
        desiredCapabilities.setPlatform(Platform.EL_CAPITAN);
        break;
      case "linux":
        desiredCapabilities.setPlatform(Platform.LINUX);
        break;
      case "mac":
        desiredCapabilities.setPlatform(Platform.MAC);
        break;
      case "mavericks":
        desiredCapabilities.setPlatform(Platform.MAVERICKS);
        break;
      case "mountain_lion":
        desiredCapabilities.setPlatform(Platform.MOUNTAIN_LION);
        break;
      case "snow_leopard":
        desiredCapabilities.setPlatform(Platform.SNOW_LEOPARD);
        break;
      case "unix":
        desiredCapabilities.setPlatform(Platform.UNIX);
        break;
      case "vista":
        desiredCapabilities.setPlatform(Platform.VISTA);
        break;
      case "win8":
        desiredCapabilities.setPlatform(Platform.WIN8);
        break;
      case "win8_1":
        desiredCapabilities.setPlatform(Platform.WIN8_1);
        break;
      case "windows 10":
        desiredCapabilities.setPlatform(Platform.WIN10);
        break;
      case "windows":
        desiredCapabilities.setPlatform(Platform.WINDOWS);
        break;
      case "xp":
        desiredCapabilities.setPlatform(Platform.XP);
        break;
      case "yosemite":
        desiredCapabilities.setPlatform(Platform.YOSEMITE);
        break;
      default:
        LOG.warn("Unknown operating system: {}. Using ANY platform.", operatingSystem);
        desiredCapabilities.setPlatform(Platform.ANY);
        break;
    }
    return desiredCapabilities;
  }

  /**
   * @param webDriver
   * @param field
   * @param values
   * @return
   */
  default boolean webCheckboxSelect(WebDriver webDriver, String field, String values) {
    boolean matchAll = true;
    boolean match = false;
    if (Environment.isLogAll()) {
      LOG.debug("");
    }
    final String[] valueArray = values.split(Constants.DELIMETER_LIST);
    final List<WebElement> itemList = webDriver.findElements(By.name(field));
    LOG.debug("Total " + field + "(s) " + itemList.size());
    for (final String value : valueArray) {
      for (WebElement webElement : itemList) {
        String item = webElement.getAttribute("value");
        if (item.trim().equals(value)) {
          if (Environment.isLogAll()) {
            final String message = String.format("Field:[%s], Value[%s]", field, value);
            LOG.debug("{}", message);
          }
          item = webElement.getAttribute("checked");
          if (item == null) {
            webElement.click();
          }
          match = true;
          break;
        }
      }
      if (!match) {
        matchAll = match;
      }
    }
    return matchAll;
    /*
     * element = By.name(field); valueActual =
     * webDriver.findElement(element).getAttribute("value"); if (
     * !valueActual.equals(value) ) {
     * webDriver.findElement(element).click(); }
     */
  }

  /**
   * @param webDriver
   * @param field
   * @param value
   * @return
   */
  default boolean webComboBoxSelect(WebDriver webDriver, String field, String value) {
    boolean match = false;
    int index = 0;
    Select dropdown;
    String item = null;
    String[] items = null;
    String message = null;
    if (Environment.isLogAll()) {
      LOG.debug("");
    }
    final List<WebElement> optionElements = webDriver.findElements(By.name(field));
    if (optionElements.size() == 1) {
      item = optionElements.get(0).getText();
      items = item.split(Constants.NEWLINE);
      dropdown = new Select(webDriver.findElement(By.name(field)));
      dropdown.selectByVisibleText(value);
      match = true;
      if (match) {
        return match;
      }
      for (index = 0; index < items.length; index++) {
        if (items[index].equalsIgnoreCase(value)) {
          dropdown = new Select(webDriver.findElement(By.name(field)));
          dropdown.selectByIndex(index + 1);
          final List<WebElement> selectedOption = dropdown.getAllSelectedOptions();
          final String valueActual = selectedOption.get(0).getText();
          if (!valueActual.equalsIgnoreCase(value)) {
            dropdown.selectByVisibleText(value);
            if (Environment.isLogAll()) {
              message = String.format("Field:[%s], Value[%s]", field, value);
              LOG.debug("{}", message);
            }
          }
          match = true;
          break;
        }
      }
    } else {
      for (index = 0; index < optionElements.size(); index++) {
        item = optionElements.get(index).getText();
        if (item.trim().equals(value)) {
          if (Environment.isLogAll()) {
            message = String.format("Field:[%s], Value[%s]", field, item);
            LOG.debug("{}", message);
          }
          match = true;
        }
      }
    }
    return match;
  }

  /**
   * @param browser
   * @param scenario
   * @return
   */
  static WebDriver browserProfiling(String browser, Scenario scenario) {
    WebDriver webDriver = null;
    try {
      final String operatingSystem = "Windows 10";
      final DesiredCapabilities desiredCapabilities =
          setDesiredCapabilities(operatingSystem, browser);
      // desiredCapabilities.setCapability("platform", operatingSystem);
      // desiredCapabilities.setCapability("version", "14.14393");
      desiredCapabilities.setCapability("name", scenario.getName());
      final StringBuilder stringBuilderURL = new StringBuilder();
      // stringBuilderURL.append("http://");
      stringBuilderURL.append(SELENIUM_GRID_HUB);
      final String url = stringBuilderURL.toString();
      LOG.debug("url:[{}]", url + "]");
      webDriver = new RemoteWebDriver(URI.create(url).toURL(), desiredCapabilities);
      final Selenium selenium = new Selenium(webDriver);
      selenium.getSessionInformation();
    } catch (final Exception e) {
      e.printStackTrace();
    }
    return webDriver;
  }

  /**
   * @param browser
   * @param remote
   * @return
   */
  static WebDriver browserProfiling(String browser, boolean remote) {
    WebDriver webDriver = null;
    try {
      final String operatingSystem = System.getProperty("os.name");
      final DesiredCapabilities desiredCapabilities =
          setDesiredCapabilities(operatingSystem, browser);
      if ("htmlunit".equals(browser.toLowerCase(Locale.ENGLISH))) {
        webDriver = new HtmlUnitDriver(desiredCapabilities);
        return webDriver;
      }
      if (remote) {
        LOG.debug("{}", SELENIUM_GRID_HUB);
        webDriver = new RemoteWebDriver(URI.create(SELENIUM_GRID_HUB).toURL(), desiredCapabilities);
      } else {
        webDriver = browserProfiling(webDriver, browser);
      }
      webDriver.manage().window().maximize();
      // Point oPoint = new Point(0, 0);
      // webDriver.manage().window().setPosition(oPoint);
      // Dimension oDimension = new Dimension(725, 725);
      // webDriver.manage().window().setSize(oDimension);
      final Selenium selenium = new Selenium(webDriver);
      selenium.getSessionInformation();
    } catch (final Exception e) {
      LOG.error("Exception occurred", e);
    }
    return webDriver;
  }

  /**
   * @param webDriver
   * @param browser
   * @param remote
   * @return
   */
  default WebDriver browserProfiling(WebDriver webDriver, String browser, boolean remote) {
    try {
      final String operatingSystem = System.getProperty("os.name").toUpperCase(Locale.ENGLISH);
      DesiredCapabilities desiredCapabilities = setDesiredCapabilities(operatingSystem, browser);
      // AppLOG.debug("{}", "desiredCapabilities:[" +
      // desiredCapabilities.toString() + "]");
      final String browserLowercase = browser.toLowerCase(Locale.ENGLISH);
      // Java 17: Switch expression with block syntax for complex cases
      switch (browserLowercase) {
        case "firefox" -> {
          // webDriver = new FirefoxDriver(desiredCapabilities);
          final String proxy = "localhost";
          final int port = 8080;
          final JsonObject jsonObject = new JsonObject();
          jsonObject.addProperty("proxyType", "MANUAL");
          jsonObject.addProperty("httpProxy", proxy);
          jsonObject.addProperty("httpProxyPort", port);
          jsonObject.addProperty("sslProxy", proxy);
          jsonObject.addProperty("sslProxyPort", port);
          desiredCapabilities = new DesiredCapabilities();
          desiredCapabilities.setCapability("proxy", jsonObject);
          // final FirefoxBinary firefoxBinary = null;
          // final GeckoDriverService service = new
          // GeckoDriverService.Builder(firefoxBinary)
          // .usingDriverExecutable(new
          // File(System.getProperty("webdriver.gecko.driver")))
          // .usingAnyFreePort().usingAnyFreePort().build();
          GeckoDriverService service = null;
          try {
            service =
                new GeckoDriverService.Builder()
                    .usingDriverExecutable(new File(System.getProperty("webdriver.gecko.driver")))
                    .usingAnyFreePort()
                    .usingAnyFreePort()
                    .build();
            service.start();
            // GeckoDriver currently needs the Proxy set in
            // RequiredCapabilities
            // webDriver = new FirefoxDriver(service,
            // desiredCapabilities, desiredCapabilities);
            FirefoxOptions ffOpts = new FirefoxOptions();
            ffOpts.merge(desiredCapabilities);
            webDriver = new FirefoxDriver(ffOpts);
          } finally {
            if (service != null) {
              if (service.isRunning()) {
                service.stop();
              }
              service.close();
            }
          }
        }
        case "htmlunit" -> {
          webDriver = new HtmlUnitDriver(desiredCapabilities);
        }
        default -> {
          if (remote) {
            if ("firefox".equals(browserLowercase)) {
              System.setProperty(
                  "webdriver.gecko.driver", Constants.PATH_DRIVERS_LOCAL + "geckodriver.exe");
              // File profileDirectory = new File("C:/Temp/");
              // FirefoxProfile firefoxProfile = new
              // FirefoxProfile(profileDirectory);
              // File extension = new
              // File("C:/Temp/firefox_extension.xpi");
              // firefoxProfile.addExtension(extension);
              // desiredCapabilities =
              // new FirefoxOptions();
              // desiredCapabilities =
              // setDesiredCapabilities(operatingSystem, browser);
              // desiredCapabilities.setCapability(FirefoxDriver.PROFILE,
              // firefoxProfile);
            }
            LOG.debug("{}", SELENIUM_GRID_HUB);
            webDriver =
                new RemoteWebDriver(URI.create(SELENIUM_GRID_HUB).toURL(), desiredCapabilities);
          } else {
            webDriver = browserProfiling(webDriver, browser);
          }
        }
      }
      // webDriver = (WebDriver) new
      // DefaultSelenium(SELENIUM_GRID_HUB_NAME,
      // SELENIUM_GRID_HUB_NAMEPORT, "*firefox",
      // "http://www.google"+IExtension.COM);
      webDriver.manage().window().maximize();
      // Point oPoint = new Point(0, 0);
      // webDriver.manage().window().setPosition(oPoint);
      // Dimension oDimension = new Dimension(725, 725);
      // webDriver.manage().window().setSize(oDimension);
      final Selenium selenium = new Selenium(webDriver);
      selenium.getSessionInformation();
      selenium.toString();
    } catch (final Exception e) {
      // e.printStackTrace();
      LOG.error("Exception occurred", e);
    }
    return webDriver;
  }

  /**
   * @param webDriver
   * @param browser
   * @return
   */
  static WebDriver browserProfiling(WebDriver webDriver, String browser) {
    killBrowserProcesses(browser);
    try {
      // String STANDARD_FIREFOX = "C:/Users/" + System.getenv("USERNAME")
      // + "/AppData/Local/Mozilla Firefox/firefox.exe";
      // if (browser.toLowerCase(Locale.ENGLISH).equals("firefox"))
      // {
      // switch (Constants.CURRENT_USER)
      // {
      // case "BTSQA":
      // STANDARD_FIREFOX = "C:/Program Files/Mozilla
      // Firefox/firefox.exe";
      // break;
      // case "CHRIS":
      // STANDARD_FIREFOX = "C:/Program Files (x86)/Mozilla
      // Firefox/firefox.exe";
      // STANDARD_FIREFOX = "C:/Program Files/Mozilla
      // Firefox/firefox.exe";
      // break;
      // default:
      // STANDARD_FIREFOX = "C:/Users/" + System.getenv("USERNAME")
      // + "/AppData/Local/Mozilla Firefox/firefox.exe";
      // break;
      // }
      // // STANDARD_FIREFOX = "C:/Program Files (x86)/Mozilla
      // // Firefox/firefox.exe";
      // }
      String message = null;
      // String browserDefault = "Firefox (Default)"; // XLS, XML, Text,
      // Properties
      // Firefox Profiling
      // "C:" + Constants.DELIMETER_PATH + "Program Files (x86)" +
      // Constants.DELIMETER_PATH + "Mozilla Firefox" +
      // Constants.DELIMETER_PATH + "firefox.exe" firefox -P
      // "C:" + Constants.DELIMETER_PATH + "Users" +
      // Constants.DELIMETER_PATH + "CScharer" + Constants.DELIMETER_PATH
      // + "AppData" + Constants.DELIMETER_PATH + "Local" +
      // Constants.DELIMETER_PATH + "Mozilla Firefox" +
      // Constants.DELIMETER_PATH + "firefox.exe"
      // firefox
      // -P
      final String browserDefault = "firefox";
      String driverPath = "";
      if (Environment.isLogAll()) {
        message =
            String.format(
                "Browser:[%s], Default:[%s], Driver:[%s]", browser, browserDefault, webDriver);
        LOG.debug("{}", message);
      }
      // Loads all the profiles
      // final ProfilesIni profilesIni = new ProfilesIni();
      // FirefoxProfile seleniumProfile = null;
      switch (browser.toLowerCase(Locale.ENGLISH)) {
        case "chrome":
          // Version 54.0.2840.71 m
          driverPath = Constants.PATH_DRIVERS_LOCAL + "chromedriver.exe";
          System.setProperty("webdriver.chrome.driver", driverPath);
          final ChromeOptions chromeOptions = setChromeOptions();
          webDriver = new ChromeDriver(chromeOptions);
          break;
        case "edge":
          // Microsoft Edge 38.14393.0.0
          // Microsoft EdgeHTML 14.14393
          driverPath = Constants.PATH_DRIVERS_LOCAL + "MicrosoftWebDriver.exe";
          System.setProperty("webdriver.edge.driver", driverPath);
          webDriver = new EdgeDriver();
          break;
        case "firefox":
          // // Version 46.0.1
          // // final DesiredCapabilities capabilities =
          // // new FirefoxOptions();
          // // Tell the Java bindings to use Marionette. This will
          // not be
          // // necessary in the future, when Selenium will
          // auto-detect
          // what
          // // remote end it is talking to.
          // // capabilities.setCapability("marionette", true);
          // // webDriver = new RemoteWebDriver(capabilities);
          // seleniumProfile = profilesIni.getProfile("Selenium");
          // // webDriver = new FirefoxDriver(new FirefoxBinary(new
          // // File(STANDARD_FIREFOX)), seleniumProfile,
          // capabilities);
          // // webDriver = new FirefoxDriver(seleniumProfile);
          // webDriver = new FirefoxDriver(new FirefoxBinary(new
          // File(STANDARD_FIREFOX)), seleniumProfile);
          // if (AppEnvironment.isLogAll())
          // {
          // sMessage = String.format("Implemented Profile:[%s]",
          // seleniumProfile);
          // AppLOG.debug("{}", sMessage);
          // }
          System.setProperty(
              "webdriver.gecko.driver", Constants.PATH_DRIVERS_LOCAL + "geckodriver.exe");
          webDriver = new FirefoxDriver();
          break;
        case "htmlunit":
          webDriver = new HtmlUnitDriver();
          ((HtmlUnitDriver) webDriver).setJavascriptEnabled(true);
          try (WebClient webClient = new WebClient()) {
            try {
              final HtmlPage page =
                  webClient.getPage("http://stackoverflow" + IExtension.COM + "/");
              LOG.debug("{}", page.asNormalizedText());
            } catch (final Exception e) {
              LOG.error("Exception occurred", e);
            }
          }
          break;
        case "ie": // (very slow)
          // Version 11.0.9600.18499
          // Update Versions: 11.0.36 (KB3191492)
          // Product ID: 00150-20000-00003-AA459
          driverPath = Constants.PATH_DRIVERS_LOCAL + "IEDriverServer.exe";
          System.setProperty("webdriver.ie.driver", driverPath);
          webDriver = new InternetExplorerDriver();
          break;
        // case "opera": // (least stable)
        // sDriver = Constants.PATH_DRIVERS + "operadriver-1.5.jar";
        // System.setProperty("webdriver.opera.driver", sDriver);
        // webDriver = // new OperaDriver();
        // break;
        /*
         * case "RemoteWeb": webDriver = new RemoteWebDriver(); break;
         */
        case "safari":
          webDriver = new SafariDriver();
          break;
        default:
          // browser += "-" + browserDefault;
          // // Loads all the profiles
          // seleniumProfile = profilesIni.getProfile("Selenium");
          // // webDriver = new FirefoxDriver(seleniumProfile);
          // webDriver = new FirefoxDriver(new FirefoxBinary(new
          // File(STANDARD_FIREFOX)), seleniumProfile);
          // if (AppEnvironment.isLogAll())
          // {
          // sMessage = String.format("Implemented Profile:[%s]",
          // seleniumProfile);
          // AppLOG.debug("{}", sMessage);
          // }
          break;
      }
      if (Environment.isLogAll()) {
        message = String.format("Implemented Browser:[%s] Driver From [%s]", browser, driverPath);
        LOG.debug("{}", message);
      }
      // Position to 2nd monitor.
      if (Environment.isRunRemote()) {
        final Point windowPosition = new Point(2000, 1);
        webDriver.manage().window().setPosition(windowPosition);
      }
      webDriver.manage().window().maximize();
      // Point oPoint = new Point(0, 0);
      // webDriver.manage().window().setPosition(oPoint);
      // Dimension oDimension = new Dimension(725, 725);
      // webDriver.manage().window().setSize(oDimension);
      final Selenium selenium = new Selenium(webDriver);
      selenium.getSessionInformation();
      selenium.toString();
    } catch (final Exception e) {
      e.printStackTrace();
      LOG.error("Exception occurred", e);
    }
    return webDriver;
  }

  /**
   * @param webDriver
   * @param fileName
   */
  default void saveScreenshot(WebDriver webDriver, String fileName) {
    String message = null;
    final String screenshotCounter =
        DateHelpersTests.getCurrentDateTime(DateHelpersTests.FORMAT_DATE_TIME_STAMP);
    final String screenshot = String.format("%05d_%s.png", screenshotCounter, fileName);
    final File srcFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
    if (Environment.isLogAll()) {
      message = String.format("Saving:[%s]", Constants.PATH_ROOT + screenshot + "]");
      LOG.debug("{}", message);
    }
    try {
      FileUtils.copyFile(srcFile, new File(screenshot));
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * @param webDriver
   * @param elementParent
   * @param functionName
   * @return
   */
  default boolean webPageGetObjects(
      WebDriver webDriver, WebElement elementParent, String functionName) {
    String message = null;
    if (Environment.isLogAll()) {
      message = String.format("Driver [%s]", webDriver);
      LOG.debug("{}", message);
    }
    final String source = webDriver.getPageSource();
    String fileName = Constants.PATH_ROOT + "source" + IExtension.TXT;
    FSOTests.fileWrite(fileName, source, false);
    final Document doc = Jsoup.parse(source);
    // Convert JSoup Elements to List<Element> to use interface instead of implementation
    final List<Element> elementList = new ArrayList<>(doc.getAllElements());
    final StringBuilder sb = new StringBuilder();
    sb.append(
        "elementID"
            + Constants.TAB
            + "elementName"
            + Constants.TAB
            + "elementHref"
            + Constants.TAB
            + "elementText"
            + Constants.NL);
    for (final Element element : elementList) {
      final String elementID = element.attr("id");
      final String elementName = element.attr("name");
      final String elementHref = element.attr("href");
      final String elementText = element.text();
      /*
       * if (element.hasAttr("id") && elementText.isEmpty() &&
       * elementHref.isEmpty()) { sb.append(elementID + Constants.TAB +
       * elementName + Constants.TAB + elementHref + Constants.TAB +
       * elementText + Constants.NEWLINE); }
       */
      sb.append(
          elementID
              + Constants.TAB
              + elementName
              + Constants.TAB
              + elementHref
              + Constants.TAB
              + elementText
              + Constants.NEWLINE);
    }
    fileName = Constants.PATH_ROOT + functionName + IExtension.XLS;
    FSOTests.fileWrite(fileName, sb.toString(), true);
    return true;
  }
}
