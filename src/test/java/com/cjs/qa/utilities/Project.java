package com.cjs.qa.utilities;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;

public final class Project {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(Project.class));

  private Project() {
    // Utility class - prevent instantiation
  }

  private static String fileSeperator = System.getProperty("file.separator");
  private static ConfigComponents product;
  private static String root;

  public static void setProduct(ConfigComponents productValue) {
    product = productValue;
    root = pathWorkspace() + transformPath(product.name() + "/");
  }

  public static String environmentXML() {
    return path() + transformPath("environment" + IExtension.XML);
  }

  /**
   * @return Absolute path to Automation folder
   */
  private static String pathAutomation() {
    String automationPath = "";
    final String osName = System.getProperty("os.name");
    try {
      if (osName.startsWith("Linux") || osName.startsWith("Mac")) {
        automationPath = "/Automation/";
      } else if (osName.startsWith("Windows")) {
        automationPath = "C:/Automation/";
      } else {
        throw new IOException();
      }
    } catch (final IOException e) {
      LOG.error("Error getting Automation Path!", e);
      automationPath = null;
    }
    return automationPath;
  }

  /**
   * @return Absolute path to Automation Workspace
   */
  public static String pathWorkspace() {
    String directoryFolder = transformPath(pathAutomation() + "BTS/");
    FSOTests.folderCreate(directoryFolder);
    return directoryFolder;
  }

  /**
   * @return Absolute path to Automation Repository
   */
  public static String pathRepository(String filePath) {
    String directoryFolder = transformPath(pathAutomation() + "repository/" + filePath);
    FSOTests.folderCreate(directoryFolder);
    return directoryFolder;
  }

  /** Transforms the subpath to an OS independent format */
  public static String transformPath(String subPath) {
    if (subPath.contains(Constants.DELIMETER_PATH)) {
      LOG.warn("Backslash is not allowed in path!");
      return null;
    } else {
      return subPath.replace("/", fileSeperator);
    }
  }

  public static String pathFramework() {
    String directoryFolder = pathWorkspace() + transformPath("Framework/");
    FSOTests.folderCreate(directoryFolder);
    return directoryFolder;
  }

  public static String testResources(String filePath) {
    final String testResources = "src/test/resources/" + filePath;
    String directoryFolder = pathFramework() + transformPath(testResources + filePath);
    FSOTests.folderCreate(directoryFolder);
    return directoryFolder;
  }

  public static String dataFiles(String filePath) {
    final String dataFiles = "src/test/resources/datafiles/";
    String directoryFolder = path() + transformPath(dataFiles + filePath);
    FSOTests.folderCreate(directoryFolder);
    return directoryFolder;
  }

  public static String dataFiles() {
    String directoryFolder = path() + transformPath("src/test/resources/datafiles/");
    FSOTests.folderCreate(directoryFolder);
    return directoryFolder;
  }

  public static String notifications(String filePath) {
    final String notifications = "src/test/resources/notifications/";
    String directoryFolder = path() + transformPath(notifications + filePath);
    FSOTests.folderCreate(directoryFolder);
    return directoryFolder;
  }

  public static String documents(String filePath) {
    final String documents = "src/test/resources/documents/";
    String directoryFolder = path() + transformPath(documents + filePath);
    FSOTests.folderCreate(directoryFolder);
    return directoryFolder;
  }

  public static String queryresults(String filePath) {
    final String queryresults = "src/test/resources/queryresults/";
    String directoryFolder = path() + transformPath(queryresults + filePath);
    FSOTests.folderCreate(directoryFolder);
    return directoryFolder;
  }

  public static String templates(String filePath) {
    final String templates = "src/main/java/com/bts/framework/templates/";
    String directoryFolder = pathFramework() + transformPath(templates + filePath);
    FSOTests.folderCreate(directoryFolder);
    return directoryFolder;
  }

  public static String tools(String filePath) {
    final String tools = "src/main/java/com/bts/framework/tools/";
    String directoryFolder = pathFramework() + transformPath(tools + filePath);
    FSOTests.folderCreate(directoryFolder);
    return directoryFolder;
  }

  public static String testsuites(String filePath) {
    final String testsuites = "src/test/java/com/bts/" + product + "/testsuites/";
    String directoryFolder = path() + transformPath(testsuites + filePath);
    FSOTests.folderCreate(directoryFolder);
    return directoryFolder;
  }

  public static String scripts(String filePath) {
    final String scripts = "src/main/java/com/bts/framework/utils/scripts/";
    String directoryFolder = pathFramework() + transformPath(scripts + filePath);
    FSOTests.folderCreate(directoryFolder);
    return directoryFolder;
  }

  public static String environment(String filePath) {
    final String environment = "src/main/java/com/bts/framework/environment/";
    String directoryFolder = pathFramework() + transformPath(environment + filePath);
    FSOTests.folderCreate(directoryFolder);
    return directoryFolder;
  }

  public static String downloads(String filePath) {
    final String downloads = "target/test-downloads/";
    String directoryFolder = pathFramework() + transformPath(downloads + filePath);
    FSOTests.folderCreate(directoryFolder);
    return directoryFolder;
  }

  public static String screenshots(String filePath) {
    final String screenshots = "target/test-screenshots/";
    String directoryFolder = pathFramework() + transformPath(screenshots + filePath);
    FSOTests.folderCreate(directoryFolder);
    return directoryFolder;
  }

  public static String dataGeneration() {
    String directoryFolder = dataFiles() + transformPath("datagenerationfiles/");
    FSOTests.folderCreate(directoryFolder);
    return directoryFolder;
  }

  public static String manualverification() {
    String directoryFolder = path() + transformPath("manualverification/");
    FSOTests.folderCreate(directoryFolder);
    return directoryFolder;
  }

  public static String path() {
    return root;
  }

  public static String screenShots() {
    String directoryFolder = path() + transformPath("target/test-screenshots");
    FSOTests.folderCreate(directoryFolder);
    return directoryFolder;
  }
}
