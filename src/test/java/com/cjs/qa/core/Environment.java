package com.cjs.qa.core;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.Assert;

import com.cjs.qa.americanairlines.AmericanAirlinesEnvironment;
import com.cjs.qa.dropbox.DropboxEnvironment;
import com.cjs.qa.everyonesocial.EveryoneSocialEnvironment;
import com.cjs.qa.google.GoogleEnvironment;
import com.cjs.qa.hardees.HardeesEnvironment;
import com.cjs.qa.iadhs.IaDhsEnvironment;
import com.cjs.qa.linkedin.LinkedInEnvironment;
import com.cjs.qa.marlboro.MarlboroEnvironment;
import com.cjs.qa.microsoft.MicrosoftEnvironment;
import com.cjs.qa.microsoft.excel.IExcel;
import com.cjs.qa.microsoft.excel.xls.XLS;
import com.cjs.qa.polkcounty.PolkCountyEnvironment;
import com.cjs.qa.selenium.ISelenium;
import com.cjs.qa.united.UnitedEnvironment;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.DateHelpersTests;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.utilities.SoftAssert;
import com.cjs.qa.utilities.XML;
import com.cjs.qa.vivit.VivitEnvironment;
import com.cjs.qa.wellmark.WellmarkEnvironment;

import io.cucumber.java.Scenario;

public class Environment {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(Environment.class));

  public static final String NOT_FOUND_USING_DEFAULT = " NOT FOUND.  USING DEFAULT-";
  public static final String COMPANY = "Core";
  public static final String EMAIL_SIGNATURE =
      FSOTests.fileReadAll(Constants.PATH_OUTLOOK_SIGNATURES + "MSN" + IExtension.HTM);
  private static boolean environmentSet = false;
  public static final String FILE_CONFIG =
      Constants.PATH_ROOT + "config" + Constants.DELIMETER_PATH + "Environments" + IExtension.XML;
  private static String folderData = Constants.PATH_FILES_DATA + COMPANY + Constants.DELIMETER_PATH;
  private static String fileLog = folderData + "Log_" + COMPANY + IExtension.LOG;
  private static Map<Integer, String> scenarioErrors = new HashMap<>();
  private static String gridHub = null;
  private static String gridPort = null;
  public static final String FILE_CONFIG_XML = FSOTests.fileReadAll(FILE_CONFIG);
  public static final String CURRENT_USER = Constants.CURRENT_USER;
  private static SoftAssert softAssert = new SoftAssert();
  private static boolean overrideUser = false;
  private static String browser = ISelenium.BROWSER_DEFAULT;
  private static String company = "CJS";
  private static String environment = "TST";
  private static int timeOutPage = 10;
  private static int timeOutElement = 5;
  private static int timeOutAlert = 5;
  private static boolean runRemote = true;
  private static boolean scrollToObjects = true;
  private static boolean highlightObjects = true;
  private static boolean flashObjects = true;
  private static boolean logAll = true;
  private static boolean logAPI = true;
  private static boolean logSQL = true;

  public Environment() {
    // Log4j 2.x uses auto-configuration (log4j2.xml), no manual configuration needed
    setEnvironmentVariableValues();
  }

  public void addScenarioError(String error) {
    getScenarioErrors().put(getScenarioErrors().size() + 1, error);
  }

  public static void clearScenarioErrors() {
    setScenarioErrors(new HashMap<>());
  }

  public static String getSheetFailure(String filePathName, int defaultSheetCount, String policy)
      throws IOException, QAException {
    final XLS excel = new XLS(filePathName, IExcel.SHEET_SUMMARY);
    // int rowSummary = (excel.getRowCount(IExcel.SHEET_SUMMARY) + 1)
    int rowSummary = excel.getSheetCount() - (defaultSheetCount - 1);
    excel.close();
    return JavaHelpers.formatNumber(rowSummary, "000");
    // + "_" + policy;
  }

  public static String getComputerName() {
    final Map<String, String> env = System.getenv();
    if (env.containsKey("COMPUTERNAME")) {
      return env.get("COMPUTERNAME").toUpperCase(Locale.ENGLISH);
    } else if (env.containsKey("HOSTNAME")) {
      return env.get("HOSTNAME").toUpperCase(Locale.ENGLISH);
    } else {
      return "Unknown Computer";
    }
  }

  public static Boolean noPMDUselessParentheses(Boolean value) {
    return "true".equals(value.toString());
  }

  public static Integer noPMDUselessParentheses(Integer value) {
    return value;
  }

  public static String noPMDUselessParentheses(String value) {
    return value;
  }

  public static void setScrollToObject(boolean scrollToObjects) {
    sysOut(
        "Environment Variable "
            + Constants.QUOTE_DOUBLE
            + "SCROLL_TO_OBJECTS"
            + Constants.QUOTE_DOUBLE
            + "["
            + scrollToObjects
            + "]");
    Environment.scrollToObjects = scrollToObjects;
  }

  public static void setEnvironmentVariableValues() {
    String config = Constants.CURRENT_USER;
    final String hostName = getComputerName();
    setOverrideUser("OVERRIDE_USER", hostName);
    if (overrideUser) {
      config = hostName;
    }
    sysOut("Using config for [" + config + "]");
    setBrowser("BROWSER", config);
    setCompany("COMPANY", config);
    setEnvironment("ENVIRONMENT", config);
    setTimeOutPage("TIME_OUT_PAGE", config);
    setTimeOutElement("TIME_OUT_ELEMENT", config);
    setTimeOutAlert("TIME_OUT_ALERT", config);
    setrunRemote("RUN_REMOTE", config);
    setScrollToObjects("SCROLL_TO_OBJECTS", config);
    setHighlightObjects("HIGHLIGHT_OBJECTS", config);
    setFlashObjects("FLASH_OBJECTS", config);
    setLogAll("LOG_ALL", config);
    setLogAPI("LOG_API", config);
    setLogSQL("LOG_SQL", config);
    environmentSet = true;
  }

  public static void setEnvironmentFileStructure(String project) {
    sysOut("Setting Environment File Structure for the [" + project + "] Project");
    project = project.toLowerCase(Locale.ENGLISH);
    // Java 17: Switch expression with multiple case labels and block syntax
    switch (project) {
      case "americanairlines" -> {
        setFolderData(AmericanAirlinesEnvironment.FOLDER_DATA);
        setFileLog(AmericanAirlinesEnvironment.FILE_LOG);
      }
      case "core", "jenkins", "testclasssetup" -> {
        // No action needed for these projects
      }
      case "dropbox" -> {
        setFolderData(DropboxEnvironment.FOLDER_DATA);
        setFileLog(DropboxEnvironment.FILE_LOG);
      }
      case "everyonesocial" -> {
        setFolderData(EveryoneSocialEnvironment.FOLDER_DATA);
        setFileLog(EveryoneSocialEnvironment.FILE_LOG);
      }
      case "google" -> {
        setFolderData(GoogleEnvironment.FOLDER_DATA);
        setFileLog(GoogleEnvironment.FILE_LOG);
      }
      case "hardees" -> {
        setFolderData(HardeesEnvironment.FOLDER_DATA);
        setFileLog(HardeesEnvironment.FILE_LOG);
      }
      case "iadhs" -> {
        setFolderData(IaDhsEnvironment.FOLDER_DATA);
        setFileLog(IaDhsEnvironment.FILE_LOG);
      }
      case "linkedin" -> {
        setFolderData(LinkedInEnvironment.FOLDER_DATA);
        setFileLog(LinkedInEnvironment.FILE_LOG);
      }
      case "marlboro" -> {
        setFolderData(MarlboroEnvironment.FOLDER_DATA);
        setFileLog(MarlboroEnvironment.FILE_LOG);
      }
      case "microsoft" -> {
        setFolderData(MicrosoftEnvironment.FOLDER_DATA);
        setFileLog(MarlboroEnvironment.FILE_LOG);
      }
      case "polkcounty" -> {
        setFolderData(PolkCountyEnvironment.FOLDER_DATA);
        setFileLog(PolkCountyEnvironment.FILE_LOG);
      }
      case "united" -> {
        setFolderData(UnitedEnvironment.FOLDER_DATA);
        setFileLog(UnitedEnvironment.FILE_LOG);
      }
      case "vivit" -> {
        setFolderData(VivitEnvironment.FOLDER_DATA);
        setFileLog(VivitEnvironment.FILE_LOG);
      }
      case "wellmark" -> {
        setFolderData(WellmarkEnvironment.FOLDER_DATA);
        setFileLog(WellmarkEnvironment.FILE_LOG);
      }
      default ->
          Assert.fail(
              "The setEnvironmentFileStructure has not been defined for the ["
                  + project
                  + "] Project");
    }
  }

  private static String getSysOutHeader() {
    return noPMDUselessParentheses(
        DateHelpersTests.getCurrentDateAndTime()
            + Constants.DELIMETER_LIST
            + Constants.CURRENT_USER
            + Constants.DELIMETER_LIST
            + getComputerName()
            + Constants.DELIMETER_LIST);
  }

  public static void sysOut(String value) {
    if (isLogAll()) {
      StringBuilder stringBuilder = new StringBuilder();
      // stringBuilder.append(getSysOutHeader())
      // stringBuilder.append(getStackInfo())
      // stringBuilder.append(Constants.DELIMETER_LIST)
      stringBuilder.append(value);
      LOG.info(stringBuilder.toString()); // Modernized: Using log4j 2.x instead of System.out
      // stringBuilder.append(Constants.NEWLINE)
      // FSOTests.fileWrite(fileLog, stringBuilder.toString(), true)
    }
  }

  public static void sysOut(int value) {
    sysOut(String.valueOf(value));
  }

  public static void sysOut(Scenario senario, String value) {
    sysOut(value);
    senario.log(value);
  }

  public static void sysOut(Exception e) {
    LOG.error("Exception occurred", e); // Modernized: Using log4j 2.x with exception
    Assert.fail();
  }

  public static void sysOut(Throwable e) {
    LOG.error("Throwable occurred", e); // Modernized: Using log4j 2.x with throwable
  }

  public static void sysOut(String[] value) {
    sysOut(Arrays.toString(value));
  }

  public static void sysOut(List<String> value) {
    sysOut(value.toString());
  }

  public static void sysOut(Map<String, String> value) {
    sysOut(value.toString());
  }

  public static void sysOut(StackTraceElement[] value) {
    sysOut(Arrays.toString(value));
  }

  public static void sysOut(boolean value) {
    sysOut(String.valueOf(value));
  }

  public static String getGridHub() {
    return gridHub;
  }

  public static String getGridPort() {
    return gridPort;
  }

  public static void sysOutFailure(String value) {
    sysOut(value);
    final String messageOut = getSysOutHeader() + value;
    if (fileLog == null) {
      sysOut("fileLog:[" + fileLog + "]" + Constants.NEWLINE + value);
    } else {
      FSOTests.fileWrite(fileLog, messageOut + Constants.TAB, true);
      FSOTests.fileWrite(fileLog, "FAILED", true);
      FSOTests.fileWrite(fileLog, Constants.NL, true);
    }
  }

  private static void setOverrideUser(String node, String config) {
    try {
      final StringBuilder stringBuilder = new StringBuilder();
      final String value = XML.getNode(FILE_CONFIG_XML, config, node);
      if (value != null) {
        overrideUser = Boolean.valueOf(value);
      } else {
        stringBuilder.append(node + NOT_FOUND_USING_DEFAULT);
      }
      stringBuilder.append(
          node
              + ":["
              + overrideUser
              + "], Computer/Host Name:["
              + config
              + "], UserID:["
              + Constants.CURRENT_USER
              + "]");
      sysOut(stringBuilder.toString());
    } catch (final Exception e) {
      sysOut(e);
    }
  }

  private static void setBrowser(String node, String config) {
    try {
      final StringBuilder stringBuilder = new StringBuilder();
      final String value = XML.getNode(FILE_CONFIG_XML, config, node);
      if (value != null) {
        browser = value;
      } else {
        stringBuilder.append(node + NOT_FOUND_USING_DEFAULT);
      }
      stringBuilder.append(node + ":[" + browser + "]");
      sysOut(stringBuilder.toString());
    } catch (final Exception e) {
      sysOut(e);
    }
  }

  private static void setCompany(String node, String config) {
    try {
      final StringBuilder stringBuilder = new StringBuilder();
      final String value = XML.getNode(FILE_CONFIG_XML, config, node);
      if (value != null) {
        company = value;
      } else {
        stringBuilder.append(node + NOT_FOUND_USING_DEFAULT);
      }
      stringBuilder.append(node + ":[" + company + "]");
      sysOut(stringBuilder.toString());
    } catch (final Exception e) {
      sysOut(e);
    }
  }

  private static void setEnvironment(String node, String config) {
    try {
      final StringBuilder stringBuilder = new StringBuilder();

      // PRIORITY 1: Check for system property override (for CI/CD pipeline)
      final String envFromSystem = System.getProperty("test.environment");
      if (envFromSystem != null && !envFromSystem.isEmpty()) {
        environment = envFromSystem.toUpperCase(Locale.ROOT);
        stringBuilder.append(node + " (from system property):[" + environment + "]");
        sysOut(stringBuilder.toString());
        sysOut("✅ Environment set from CI/CD parameter: " + environment);
        return;
      }

      // PRIORITY 2: Read from XML configuration file
      final String value = XML.getNode(FILE_CONFIG_XML, config, node);
      if (value != null) {
        environment = value;
      } else {
        stringBuilder.append(node + NOT_FOUND_USING_DEFAULT);
      }
      stringBuilder.append(node + ":[" + environment + "]");
      sysOut(stringBuilder.toString());
    } catch (final Exception e) {
      sysOut(e);
    }
  }

  private static void setTimeOutPage(String node, String config) {
    try {
      final StringBuilder stringBuilder = new StringBuilder();
      final String value = XML.getNode(FILE_CONFIG_XML, config, node);
      if (value != null) {
        timeOutPage = Integer.valueOf(value);
      } else {
        stringBuilder.append(node + NOT_FOUND_USING_DEFAULT);
      }
      stringBuilder.append(node + ":[" + timeOutPage + "]");
      sysOut(stringBuilder.toString());
    } catch (final Exception e) {
      sysOut(e);
    }
  }

  private static void setTimeOutElement(String node, String config) {
    try {
      final StringBuilder stringBuilder = new StringBuilder();
      final String value = XML.getNode(FILE_CONFIG_XML, config, node);
      if (value != null) {
        timeOutElement = Integer.valueOf(value);
      } else {
        stringBuilder.append(node + NOT_FOUND_USING_DEFAULT);
      }
      stringBuilder.append(node + ":[" + timeOutElement + "]");
      sysOut(stringBuilder.toString());
    } catch (final Exception e) {
      sysOut(e);
    }
  }

  private static void setTimeOutAlert(String node, String config) {
    try {
      final StringBuilder stringBuilder = new StringBuilder();
      final String value = XML.getNode(FILE_CONFIG_XML, config, node);
      if (value != null) {
        timeOutAlert = Integer.valueOf(value);
      } else {
        stringBuilder.append(node + NOT_FOUND_USING_DEFAULT);
      }
      stringBuilder.append(node + ":[" + timeOutAlert + "]");
      sysOut(stringBuilder.toString());
    } catch (final Exception e) {
      sysOut(e);
    }
  }

  private static void setrunRemote(String node, String config) {
    try {
      final StringBuilder stringBuilder = new StringBuilder();
      final String value = XML.getNode(FILE_CONFIG_XML, config, node);
      if (value != null) {
        runRemote = Boolean.valueOf(value);
      } else {
        stringBuilder.append(node + NOT_FOUND_USING_DEFAULT);
      }
      stringBuilder.append(node + ":[" + runRemote + "]");
      sysOut(stringBuilder.toString());
    } catch (final Exception e) {
      sysOut(e);
    }
  }

  private static void setScrollToObjects(String node, String config) {
    try {
      final StringBuilder stringBuilder = new StringBuilder();
      final String value = XML.getNode(FILE_CONFIG_XML, config, node);
      if (value != null) {
        scrollToObjects = Boolean.valueOf(value);
      } else {
        stringBuilder.append(node + NOT_FOUND_USING_DEFAULT);
      }
      stringBuilder.append(node + ":[" + scrollToObjects + "]");
      sysOut(stringBuilder.toString());
    } catch (final Exception e) {
      sysOut(e);
    }
  }

  private static void setHighlightObjects(String node, String config) {
    try {
      final StringBuilder stringBuilder = new StringBuilder();
      final String value = XML.getNode(FILE_CONFIG_XML, config, node);
      if (value != null) {
        highlightObjects = Boolean.valueOf(value);
      } else {
        stringBuilder.append(node + NOT_FOUND_USING_DEFAULT);
      }
      stringBuilder.append(node + ":[" + highlightObjects + "]");
      sysOut(stringBuilder.toString());
    } catch (final Exception e) {
      sysOut(e);
    }
  }

  private static void setFlashObjects(String node, String config) {
    try {
      final StringBuilder stringBuilder = new StringBuilder();
      final String value = XML.getNode(FILE_CONFIG_XML, config, node);
      if (value != null) {
        flashObjects = Boolean.valueOf(value);
      } else {
        stringBuilder.append(node + NOT_FOUND_USING_DEFAULT);
      }
      stringBuilder.append(node + ":[" + flashObjects + "]");
      sysOut(stringBuilder.toString());
    } catch (final Exception e) {
      sysOut(e);
    }
  }

  private static void setLogAll(String node, String config) {
    try {
      final StringBuilder stringBuilder = new StringBuilder();
      final String value = XML.getNode(FILE_CONFIG_XML, config, node);
      if (value != null) {
        logAll = Boolean.valueOf(value);
      } else {
        stringBuilder.append(node + NOT_FOUND_USING_DEFAULT);
      }
      stringBuilder.append(node + ":[" + logAll + "]");
      sysOut(stringBuilder.toString());
    } catch (final Exception e) {
      sysOut(e);
    }
  }

  private static void setLogAPI(String node, String config) {
    try {
      final StringBuilder stringBuilder = new StringBuilder();
      final String value = XML.getNode(FILE_CONFIG_XML, config, node);
      if (value != null) {
        logAPI = Boolean.valueOf(value);
      } else {
        stringBuilder.append(node + NOT_FOUND_USING_DEFAULT);
      }
      stringBuilder.append(node + ":[" + logAPI + "]");
      sysOut(stringBuilder.toString());
    } catch (final Exception e) {
      sysOut(e);
    }
  }

  private static void setLogSQL(String node, String config) {
    try {
      final StringBuilder stringBuilder = new StringBuilder();
      final String value = XML.getNode(FILE_CONFIG_XML, config, node);
      if (value != null) {
        logSQL = Boolean.valueOf(value);
      } else {
        stringBuilder.append(node + NOT_FOUND_USING_DEFAULT);
      }
      stringBuilder.append(node + ":[" + logSQL + "]");
      sysOut(stringBuilder.toString());
    } catch (final Exception e) {
      sysOut(e);
    }
  }

  public static boolean isEnvironmentSet() {
    return environmentSet;
  }

  public static String getConfigFile() {
    return FILE_CONFIG;
  }

  public static String getCurrentUser() {
    return Constants.CURRENT_USER;
  }

  public static boolean isOverrideUser() {
    return overrideUser;
  }

  public static String getBrowser() {
    return browser;
  }

  public static String getCompany() {
    return company;
  }

  public static String getEnvironment() {
    return environment;
  }

  public static int getTimeOutPage() {
    return timeOutPage;
  }

  public static int getTimeOutElement() {
    return timeOutElement;
  }

  public static int getTimeOutAlert() {
    return timeOutAlert;
  }

  public static boolean isRunRemote() {
    return runRemote;
  }

  public static boolean isScrollToObjects() {
    return scrollToObjects;
  }

  public static boolean isHighlightObjects() {
    return highlightObjects;
  }

  public static boolean isFlashObjects() {
    return flashObjects;
  }

  public static boolean isLogAll() {
    return logAll;
  }

  public static boolean isLogAPI() {
    return logAPI;
  }

  public static boolean isLogSQL() {
    return logSQL;
  }

  public static String getFolderData() {
    return folderData;
  }

  public static void setFolderData(String value) {
    folderData = value;
  }

  public static String getFileLog() {
    return fileLog;
  }

  public static void setFileLog(String value) {
    fileLog = value;
  }

  public static Map<Integer, String> getScenarioErrors() {
    return scenarioErrors;
  }

  public static void setScenarioErrors(Map<Integer, String> value) {
    scenarioErrors = value;
  }

  public static SoftAssert getSoftAssert() {
    return softAssert;
  }
}
