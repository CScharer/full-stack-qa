package com.cjs.qa.selenium;

import java.util.Locale;

import com.cjs.qa.utilities.Constants;

public enum EDriverProperties {
  // MicrosoftWebDriver
  CHROME(
      "webdriver.chrome.driver",
      Constants.PATH_DRIVERS_LOCAL + "chromedriver.exe",
      "C:/Program Files (x86)/Google/Chrome/Application/chrome.exe"),
  EDGE(
      "webdriver.edge.driver",
      Constants.PATH_DRIVERS_LOCAL + "MicrosoftWebDriver.exe",
      "C:/Windows/SystemApps/Microsoft.MicrosoftEdge_8wekyb3d8bbwe/MicrosoftEdge.exe"),
  MICROSOFTEDGE(
      "webdriver.edge.driver",
      Constants.PATH_DRIVERS_LOCAL + "MicrosoftWebDriver.exe",
      "C:/Windows/SystemApps/Microsoft.MicrosoftEdge_8wekyb3d8bbwe/MicrosoftEdge.exe"),
  MSEDGE(
      "webdriver.edge.driver",
      Constants.PATH_DRIVERS_LOCAL + "MicrosoftWebDriver.exe",
      "C:/Windows/SystemApps/Microsoft.MicrosoftEdge_8wekyb3d8bbwe/MicrosoftEdge.exe"),
  FIREFOX(
      "webdriver.gecko.driver",
      Constants.PATH_DRIVERS_LOCAL + "geckodriver.exe",
      "C:/Program Files/Mozilla Firefox/firefox.exe"),
  FIREFOX2(
      "webdriver.gecko.driver",
      Constants.PATH_DRIVERS_LOCAL + "geckodriver.exe",
      "C:/Program Files (x86)/Mozilla Firefox/firefox.exe"),
  IE(
      "webdriver.ie.driver",
      Constants.PATH_DRIVERS_LOCAL + "IEDriverServer.exe",
      "C:/Program Files (x86)/Internet Explorer/iexplore.exe"),
  INTERNETEXPLORER(
      "webdriver.ie.driver",
      Constants.PATH_DRIVERS_LOCAL + "IEDriverServer.exe",
      "C:/Program Files (x86)/Internet Explorer/iexplore.exe"),
  SAFARI(
      "webdriver.safari.driver",
      Constants.PATH_DRIVERS_LOCAL + "safari.exe",
      "C:/Program Files (x86)/Safari/Safari.exe");

  private String webdriverType;
  private String pathDriver;
  private String pathBinary;

  EDriverProperties(String webdriver, String path, String pathBinary) {
    this.webdriverType = webdriver;
    this.pathDriver = path;
    this.pathBinary = pathBinary;
  }

  public static EDriverProperties fromString(String name) {
    return getEnumFromString(EDriverProperties.class, name);
  }

  /**
   * A common method for all enums since they can't have another base class
   *
   * @param <T> Enum type
   * @param c enum type. All enums must be all caps.
   * @param string case insensitive
   * @return corresponding enum, or null
   */
  public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string) {
    return Enum.valueOf(c, string.trim().toUpperCase(Locale.ENGLISH));
  }

  public String getWebDriverType() {
    return webdriverType;
  }

  public String getPathDriver() {
    return pathDriver;
  }

  public String getPathBinary() {
    return pathBinary;
  }
}
