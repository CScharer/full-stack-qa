package com.cjs.qa.cucumber.steps.vivit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.core.Environment;
import com.cjs.qa.core.QAException;
import com.cjs.qa.selenium.ISelenium;
import com.cjs.qa.selenium.Selenium;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.Convert;
import com.cjs.qa.utilities.DateHelpersTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.utilities.Reports;
import com.cjs.qa.vivit.Vivit;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.But;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class StepsVivit extends Environment {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(StepsVivit.class));

  private Scenario scenarioObject = null;
  private Selenium selenium = null;
  private WebDriver webDriver = null;
  private final Map<String, List<Map<String, String>>> mapListTest = new HashMap<>();
  private final List<Map<String, String>> listMapTest = new ArrayList<>();
  private Map<String, String> mapTest = Reports.getColumnsString(Reports.STRING_SUMMARY);
  private String browser = ISelenium.BROWSER_DEFAULT;
  private double timeStarted = 0;
  private double timeCompleted = 0;
  private Vivit vivit;
  private String application = null;
  private String document = null;
  private String xml = null;

  protected Scenario getScenarioObject() {
    return scenarioObject;
  }

  protected void setScenarioObject(Scenario scenarioObject) {
    this.scenarioObject = scenarioObject;
  }

  protected Selenium getSelenium() {
    return selenium;
  }

  protected void setSelenium(Selenium selenium) {
    this.selenium = selenium;
  }

  protected WebDriver getWebDriver() {
    return webDriver;
  }

  protected void setWebDriver(WebDriver webDriver) {
    this.webDriver = webDriver;
  }

  protected Map<String, List<Map<String, String>>> getMapListTest() {
    return mapListTest;
  }

  protected List<Map<String, String>> getListMapTest() {
    return listMapTest;
  }

  protected Map<String, String> getMapTest() {
    return mapTest;
  }

  protected void setMapTest(Map<String, String> mapTest) {
    this.mapTest = mapTest;
  }

  protected String getStepBrowser() {
    return browser;
  }

  protected void setStepBrowser(String browser) {
    this.browser = browser;
  }

  protected double getTimeStarted() {
    return timeStarted;
  }

  protected void setTimeStarted(double timeStarted) {
    this.timeStarted = timeStarted;
  }

  protected double getTimeCompleted() {
    return timeCompleted;
  }

  protected void setTimeCompleted(double timeCompleted) {
    this.timeCompleted = timeCompleted;
  }

  protected Vivit getVivit() {
    return vivit;
  }

  protected void setVivit(Vivit vivit) {
    this.vivit = vivit;
  }

  protected String getApplication() {
    return application;
  }

  protected void setApplication(String application) {
    this.application = application;
  }

  protected String getDocument() {
    return document;
  }

  protected void setDocument(String document) {
    this.document = document;
  }

  protected String getXml() {
    return xml;
  }

  protected void setXml(String xml) {
    this.xml = xml;
  }

  // mvn clean install -Dtest=forkCount="5" -Dtags="@Vivit"
  @Before
  public void testSetup(Scenario scenario) throws Exception {
    mapTest.put(
        "Started",
        DateHelpersTests.getCurrentDateTime(
            DateHelpersTests.FORMAT_US_STANDARD_DATE_TIME + ".SSS"));
    setTimeStarted(System.currentTimeMillis());
    setScenarioObject(scenario);
    mapTest.put("API", "true");
    mapTest.put("DB", "true");
    mapTest.put("GUI", "true");
    mapTest.put("Test Name", getScenarioObject().getName());
    String sessionId = getScenarioObject().getId();
    mapTest.put("Scenario ID", sessionId);
    mapTest.put("Session ID", sessionId);
    LOG.debug("User Name:[[{}]", Constants.CURRENT_USER + "]");
    LOG.debug("{}", "mapTest:" + mapTest.toString() + " Starting...");
    if (isEnvironmentSet()) {
      setEnvironmentVariableValues();
    }
    try {
      if ("true".equals(mapTest.get("GUI"))) {
        setWebDriver(ISelenium.browserProfiling(getStepBrowser(), getScenarioObject()));
        setSelenium(new Selenium(getWebDriver()));
        getSelenium().getSessionInformation();
        sessionId = getSelenium().getSessionId().toString();
        mapTest.put("Session ID", sessionId);
      }
    } catch (final Exception e) {
      LOG.error("Exception occurred", e);
    }
    LOG.debug("{}", "mapTest:" + mapTest.toString());
  }

  @After
  public void testTeardown() throws QAException {
    LOG.debug("{}", "mapTest:" + mapTest.toString() + " Tearing Down...");
    if (getScenarioObject().isFailed()) {
      LOG.error("mapTest:[{}] FAILED", mapTest.toString());
    }
    mapTest.put("Status", getScenarioObject().getStatus().toString());
    mapTest.put(
        "Completed",
        DateHelpersTests.getCurrentDateTime(
            DateHelpersTests.FORMAT_US_STANDARD_DATE_TIME + ".SSS"));
    setTimeCompleted(System.currentTimeMillis());
    mapTest.put(
        "Elapsed (seconds)",
        String.valueOf((getTimeCompleted() - getTimeStarted()) / Constants.MILLISECONDS));
    listMapTest.add(mapTest);
    mapListTest.put("Summary", listMapTest);
    if (getScenarioObject().isFailed()) {
      if ("true".equals(mapTest.get("GUI"))) {
        getSelenium().embedScreenshot(getScenarioObject(), getWebDriver());
      }
    }
    getSelenium().killBrowser(getWebDriver());
    try {
      Reports.createReportExcelLock(getScenarioObject(), mapListTest);
    } catch (final Exception e) {
      LOG.debug("{}", e.getMessage());
    }
    LOG.debug("{}", "mapTest:" + mapTest.toString() + "] Torn Down!!!");
    setScenarioObject(null);
    setSelenium(null);
  }

  @Given("^Environment Setup Vivit$")
  public void environmentSetupVivit(DataTable table) throws Throwable {
    setStepBrowser("");
    final List<List<String>> data = table.asLists();
    for (final List<?> item : data) {
      final String field = (String) item.get(0);
      final String value = (String) item.get(1);
      if (!value.isEmpty()) {
        switch (field.toLowerCase(Locale.ENGLISH)) {
          case "browser":
            setStepBrowser(value.toUpperCase(Locale.ENGLISH).trim());
            // setStepBrowser(Environment.getStepBrowser());
            mapTest.put("Browser", getStepBrowser());
            break;
          case "company":
            final String company = value.toUpperCase(Locale.ENGLISH).trim();
            mapTest.put("Company", company);
            break;
          case "lob":
            final String lob = value.toUpperCase(Locale.ENGLISH).trim();
            mapTest.put("LOB", lob);
            break;
          case "environment":
            final String environment = value.toUpperCase(Locale.ENGLISH).trim();
            mapTest.put("Environment", environment);
            break;
          default:
            LOG.debug("Unknown field: [{}]", field + ". Skipping value: " + value);
            break;
        }
        LOG.debug("{}:[{}]", field, value);
      }
    }
    // if (browser.isEmpty())
    // {
    // browser = Environment.getStepBrowser();
    // webDriver = selenium.browserProfiling(webDriver, browser,
    // Environment.isRunRemote());
    // sessionId = (((RemoteWebDriver)
    // getWebDriver()).getSessionId()).toString();
    // LOG.debug("{}", "mapTest:" + mapTest.toString());
    // }
    setVivit(new Vivit(getWebDriver()));
  }

  // TODO ************************************************** GUI
  // *********************************************************
  @Given("^pGiven \"([^\"]*)\"$")
  public void pGiven(String scenarioName) throws Throwable {
    mapTest.put("Test Name", scenarioName);
    LOG.debug("pGiven:[[{}]", scenarioName + "]");
    final String randomNumber = JavaHelpers.generateRandomInteger(1, 10, 2);
    if ("1".equals(randomNumber)) {
      // throw new Exception(scenarioName);
      // Assert.fail(scenarioName);
      addScenarioError(
          "Random Error Generator. randomNumber:["
              + randomNumber
              + "]"
              + Constants.NEWLINE
              + mapTest.toString().replaceAll(", ", Constants.NEWLINE));
    }
    Assertions.assertEquals(0, getScenarioErrors().size());
    // getWebDriver().get(VivitEnvironment.URL_LOGIN);
  }

  @And("^pAnd$")
  public void pAnd() throws Throwable {
    LOG.debug("pAnd");
    // getWebDriver().get("http://www.vivit-worldwide.org/page/2017board");
  }

  @But("^pBut$")
  public void pBut() throws Throwable {
    LOG.debug("pBut");
    // getWebDriver().get("http://www.vivit-worldwide.org/staff/");
  }

  @When("^pWhen$")
  public void pWhen() throws Throwable {
    LOG.debug("pWhen");
    // getWebDriver().get("http://www.vivit-worldwide.org/?page=Local_Chapters");
  }

  @Then("^pThen$")
  public void pThen() throws Throwable {
    LOG.debug("pThen");
    // getWebDriver().get("http://www.vivit-worldwide.org/?page=LocalUserGroups");
    // getWebDriver().get("http://www.vivit-worldwide.org/?page=SIGS");
  }

  @Given("^Board of Directors Page \"([^\"]*)\" \"([^\"]*)\"$")
  public void boardOfDirectorsPage(String scenarioName, String url) throws Throwable {
    mapTest.put("Scenario Name", scenarioName);
    LOG.debug("boardOfDirectorsPage-scenarioName;[{}], url:[{}]", scenarioName, url);
    getWebDriver().get(url);
  }

  @Then(
      "^Board Member \"([^\"]*)\" Exists \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\","
          + " \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\"$")
  public void boardMemberExists(
      String name,
      String title,
      String eMail,
      String company,
      String address,
      String country,
      String duties)
      throws Throwable {
    LOG.debug(
        "boardMemberExists-name:[{}], title:[{}], eMail:[{}], company:[{}], address:[{}], country:[{}], duties:[{}]",
        name,
        title,
        eMail,
        company,
        address,
        country,
        duties);
    final DataTable table =
        Convert.fromListToDataTable(
            Arrays.asList(
                Arrays.asList("Name", name),
                Arrays.asList("Title", title),
                Arrays.asList("eMail", eMail),
                Arrays.asList("Company", company),
                Arrays.asList("Address", address),
                Arrays.asList("Country", country),
                Arrays.asList("Duties", duties)));
    LOG.debug("table:[{}]", table.toString());
    // vivit.BoDPage.validatePage(table);
  }

  @Given("^Staff Page \"([^\"]*)\" \"([^\"]*)\"$")
  public void staffPage(String scenarioName, String url) throws Throwable {
    LOG.debug("staffPage-scenarioName;[[{}]", scenarioName + "], url:[" + url + "]");
    mapTest.put("Scenario Name", scenarioName);
    getWebDriver().get(url);
  }

  @Then("^Staff Member \"([^\"]*)\" Exists \"([^\"]*)\", \"([^\"]*)\"$")
  public void staffMemberExists(String nameTitle, String eMail, String phone) throws Throwable {
    LOG.debug("staffMemberExists-nameTitle:[{}], eMail:[{}], phone:[{}]", nameTitle, eMail, phone);
    final DataTable table =
        Convert.fromListToDataTable(
            Arrays.asList(
                Arrays.asList("Name-Title", nameTitle),
                Arrays.asList("eMail", eMail),
                Arrays.asList("Phone", phone)));
    LOG.debug("table:[{}]", table.toString());
    // vivit.StaffPage.validatePage(table);
  }

  // *********************************************************
  // TODO ************************************************ API REST
  // ******************************************************

  // ***************************************************** API REST
  // ******************************************************
  // TODO ************************************************ API SOAP
  // ******************************************************

  // ***************************************************** API SOAP
  // ******************************************************
  // TODO *********************************************** API SOAPUI
  // *****************************************************

  // **************************************************** API SOAPUI
  // *****************************************************
}
