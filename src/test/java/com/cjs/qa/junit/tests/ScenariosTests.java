package com.cjs.qa.junit.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.AssumptionViolatedException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cjs.qa.americanairlines.AmericanAirlines;
import com.cjs.qa.core.Environment;
import com.cjs.qa.core.QAException;
import com.cjs.qa.core.security.EPasswords;
import com.cjs.qa.dropbox.Dropbox;
import com.cjs.qa.everyonesocial.EveryoneSocial;
import com.cjs.qa.google.Google;
import com.cjs.qa.hardees.Hardees;
import com.cjs.qa.iadhs.IaDhs;
import com.cjs.qa.jdbc.JDBC;
import com.cjs.qa.jdbc.JDBCConstants;
import com.cjs.qa.linkedin.LinkedIn;
import com.cjs.qa.marlboro.Marlboro;
import com.cjs.qa.maven.objects.TestRunCommand;
import com.cjs.qa.microsoft.Microsoft;
import com.cjs.qa.pluralsight.PluralSightPage;
import com.cjs.qa.polkcounty.PolkCounty;
import com.cjs.qa.selenium.ISelenium;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.selenium.SeleniumWebDriver;
import com.cjs.qa.selenium.WebDriverValidation;
import com.cjs.qa.united.United;
import com.cjs.qa.utilities.CJSConstants;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.DateHelpersTests;
import com.cjs.qa.utilities.Email;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.utilities.SoftAssert;
import com.cjs.qa.utilities.WMIC;
import com.cjs.qa.utilities.colors.ColorPalette;
import com.cjs.qa.vivit.Vivit;
import com.cjs.qa.vivit.VivitDataTests;
import com.cjs.qa.vivit.pages.VivitDataFactory;
import com.cjs.qa.wellmark.Wellmark;
import com.cjs.qa.ym.YMAPIMethodsTests;

import io.cucumber.datatable.DataTable;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ScenariosTests {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(ScenariosTests.class));

  private static void sendEmail(
      String from, String password, String subject, String body, String attachment)
      throws Throwable {
    body += " (from " + from + ")";
    Email.sendEmail(from, password, from, from, from, subject, body, attachment);
  }

  @Rule public TestName testName = new TestName();

  public static final String DATABASE_QA = "QAAuto";
  private static final int SCROLL_PIXELS = 400;
  private SeleniumWebDriver seleniumWebDriver;
  // private final String browser = ISelenium.BROWSER_DEFAULT
  // "mvn clean test -Dtest=" + JavaHelpers.getCurrentClassMethodName() +
  // "#methodName -Dtags=" + Constants.QUOTE_DOUBLE + "@" +
  // Constants.QUOTE_DOUBLE + " -DfailIfNoTests=false"
  // mvn clean test -DargLine="-Xms64m -Xmx128m" -DfailIfNoTests=false
  // -Dtest=com.cjs.qa.junit.tests.Scenarios#Google -Dtag.ids="@"
  // -Dtest=com.cjs.qa.junit.tests.Scenarios#Jenkins -Dtag.ids="@"
  // -Dtest=com.cjs.qa.junit.tests.Scenarios#Microsoft -Dtag.ids="@"
  // -Dtest=com.cjs.qa.junit.tests.Scenarios#Marlboro -Dtag.ids="@"
  // -Dtest=com.cjs.qa.junit.tests.Scenarios#Vivit -Dtag.ids="@"
  // -MAVEN_OPTS -XX:MaxPermSize=-Xms256m -Xmx512m
  // mvn clean test -Dtest=com.cjs.qa.cucumber.steps.vivit
  // -Daut="vivit"-Dtags="@Debug"
  // -DfailIfNoTests=false
  // mvn clean test -Dtest=%PATH_JUNIT_TESTS_CLASS%#%JOB_BASE_NAME% -Dtags="@"
  // -DfailIfNoTests=false
  private AmericanAirlines americanAirlines;
  private Dropbox dropbox;
  private EveryoneSocial everyoneSocial;
  private Google google;
  private Hardees hardees;
  private IaDhs iadhs;
  private LinkedIn linkedIn;
  private Marlboro marlboro;
  private Microsoft microsoft;
  private Wellmark wellmark;
  private PolkCounty polkCounty;
  private United united;
  private Vivit vivit;
  private static Map<String, Integer> mapTestCount = new HashMap<>();
  private static List<List<String>> testWatcherList = new ArrayList<>();
  private static String[] listStatus =
      "failed;finished;skipped;starting;succeeded".split(Constants.DELIMETER_LIST);

  @Rule
  public TestWatcher testWatcher =
      new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
          updateTestWatcher(description, JavaHelpers.getCurrentMethodName());
        }

        @Override
        protected void finished(Description description) {
          updateTestWatcher(description, JavaHelpers.getCurrentMethodName());
        }

        @Override
        protected void skipped(AssumptionViolatedException e, Description description) {
          updateTestWatcher(description, JavaHelpers.getCurrentMethodName());
        }

        @Override
        protected void starting(Description description) {
          updateTestWatcher(description, JavaHelpers.getCurrentMethodName());
        }

        @Override
        protected void succeeded(Description description) {
          updateTestWatcher(description, JavaHelpers.getCurrentMethodName());
        }

        private void updateTestWatcher(Description description, String status) {
          testWatcherList.add(
              Arrays.asList(
                  description.getClassName(),
                  description.getMethodName(),
                  description.getDisplayName(),
                  status));
          mapTestCount.put(status, mapTestCount.get(status) + 1);
        }
      };

  @Test
  public void testClassSetup01() throws Throwable {
    // Empty
  }

  @Test
  public void testClassSetup02() throws Throwable {
    // Empty
  }

  @Test
  public void testClassSetup03() throws Throwable {
    // Empty
  }

  @BeforeClass
  public static void beforeClassSetup() {
    LOG.info("{}]", Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName());
    testWatcherList = new ArrayList<>();
    testWatcherList.add(Arrays.asList("Class Name", "Method Name", "Display Name", "Status"));
    for (final String status : listStatus) {
      mapTestCount.put(status, 0);
    }
  }

  @Before
  public void beforeTestSetup() throws Throwable {
    LOG.debug(
        "{}], TestName: [{}]",
        Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName(),
        getTestName());
    // mavenCommand = "mvn clean test " + getDArgLine() + "
    // -DfailIfNoTests=false -Dtest=" + this.getClass().getName() + "#" +
    // getTestName() + " -Dtags=" + Constants.QUOTE_DOUBLE + "@" +
    // Constants.QUOTE_DOUBLE
    String mavenCommand = new TestRunCommand(this.getClass().getName(), getTestName()).toString();
    LOG.info("mavenCommand: [{}]", mavenCommand);
    Environment.setEnvironmentVariableValues();
    final String[] methodElements = getTestName().split("_");
    String methodTest = methodElements[0];
    Environment.setEnvironmentFileStructure(methodTest);
    getJenkinsInfo();
    setSeleniumWebDriver(new SeleniumWebDriver(null, Environment.isRunRemote(), null, null, null));
    methodTest = methodTest.toLowerCase(Locale.ENGLISH);
    // Java 17: Switch expression with block syntax for complex cases
    switch (methodTest) {
      case "americanairlines" ->
          setAmericanAirlines(new AmericanAirlines(getSeleniumWebDriver().getWebDriver()));
      case "dropbox" -> setDropbox(new Dropbox(getSeleniumWebDriver().getWebDriver()));
      case "everyonesocial" ->
          setEveryoneSocial(new EveryoneSocial(getSeleniumWebDriver().getWebDriver()));
      case "google" -> setGoogle(new Google(getSeleniumWebDriver().getWebDriver()));
      case "hardees" -> setHardees(new Hardees(getSeleniumWebDriver().getWebDriver()));
      case "iadhs" -> {
        Environment.setScrollToObject(false);
        setIadhs(new IaDhs(getSeleniumWebDriver().getWebDriver()));
      }
      case "jenkins" -> {
        // Empty by design - no action needed for jenkins
      }
      case "linkedin" -> {
        setLinkedIn(new LinkedIn(getSeleniumWebDriver().getWebDriver()));
        Environment.setScrollToObject(false);
      }
      case "marlboro" -> {
        // overrideDay is currently always true, but kept for future flexibility
        // If overrideDay becomes false in the future, uncomment the validation below:
        // final boolean overrideDay = false;
        // final String dayExpect = "1";
        // final String monthDay = DateHelpersTests.getCurrentDateTime("d");
        // if (!overrideDay && !monthDay.equals(dayExpect)) {
        //   Assert.fail(
        //       "The month day [" + monthDay + "] is not the expected day [" + dayExpect + "].");
        //   return; // Early return if validation fails
        // }
        // Set Marlboro if overrideDay is true or day matches
        setMarlboro(new Marlboro(getSeleniumWebDriver().getWebDriver()));
      }
      case "microsoft" -> {
        getSeleniumWebDriver().killBrowser();
        getSeleniumWebDriver().setBrowser("Edge");
        getSeleniumWebDriver().initializeWebDriver();
        setMicrosoft(new Microsoft(getSeleniumWebDriver().getWebDriver()));
      }
      case "polkcounty" -> setPolkCounty(new PolkCounty(getSeleniumWebDriver().getWebDriver()));
      case "united" -> {
        Environment.setScrollToObject(false);
        setUnited(new United(getSeleniumWebDriver().getWebDriver()));
      }
      case "vivit" -> {
        Environment.setScrollToObject(false);
        setVivit(new Vivit(getSeleniumWebDriver().getWebDriver()));
      }
      case "wellmark" -> {
        Environment.setScrollToObject(false);
        setWellmark(new Wellmark(getSeleniumWebDriver().getWebDriver()));
      }
      default -> {
        // Empty by design - unknown test method, no action
      }
    }
  }

  @After
  public void afterTestTeardown() {
    try {
      // Placeholder for future cleanup logic
      assert true; // No-op statement to satisfy PMD
    } catch (Exception throwable) {
      throwable.printStackTrace();
    }
    LOG.debug(
        "{}], TestName: [{}]",
        Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName(),
        getTestName());
    // LOG.debug("QAException: [{}]", QAException.getQaErrorMessage())
    final boolean failures = mapTestCount.get("failed") != 0;
    if (failures) {
      captureImages();
    }
    getSeleniumWebDriver().killBrowser();
  }

  @AfterClass
  public static void afterClassTearDown() {
    LOG.info("{}]", Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName());
    LOG.info("testWatcher DataTable");
    // testWatcherList.sort(null)
    DataTable dataTable = DataTable.create(testWatcherList);
    LOG.info("DataTable: {}", dataTable.toString());
    int count = 0;
    for (final Entry<String, Integer> entry : mapTestCount.entrySet()) {
      // Java 17: Switch expression with multiple case labels
      switch (entry.getKey()) {
        case "finished", "starting" -> {
          // Skip these entries
        }
        default -> {
          count += entry.getValue();
        }
      }
      LOG.debug("{}: [{}]", entry.getKey(), entry.getValue());
    }
    LOG.info("Count Tests: [{}]", count);
  }

  public String getTestName() {
    return testName.getMethodName();
  }

  @Test
  // @Ignore
  public void americanAirlines() throws Throwable {
    LOG.info("{}]", Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName());
    getAmericanAirlines().getVacationabilityPage().populate();
  }

  public void captureImages() {
    try {
      if (getSeleniumWebDriver() != null) {
        if (getSeleniumWebDriver().getWebDriver() != null) {
          getSeleniumWebDriver().captureScreenshot(SCROLL_PIXELS);
        }
        getSeleniumWebDriver().captureDesktopImage();
      }
    } catch (final Exception e) {
      LOG.error("There was an error trying to capture images: {}", e.getMessage(), e);
    }
  }

  @Test
  public void core() throws Throwable {
    LOG.info("{}]", Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName());
    // SystemProcesses.check()
    try {
      // /?[:<BRIEF|FULL>] Usage information.
      // CLASS - Escapes to full WMI schema.
      // PATH - Escapes to full WMI object paths.
      // CONTEXT - Displays the state of all the global switches.
      // QUIT/EXIT - Exits the program.
      final WMIC wmic = new WMIC();
      final String dateTimeStamp = DateHelpersTests.getCurrentDateTime("yyyyMMdd_HHmmss");
      // wmic.setNamespace("namespace")
      wmic.setRole(Constants.DELIMETER_PATH + "root" + Constants.DELIMETER_PATH + "cli");
      wmic.setNode("BTSDESEWS08");
      wmic.setImplevel("Impersonate");
      wmic.setAuthlevel("Default");
      wmic.setLocale("MS_409");
      wmic.setPrivileges("ENABLE");
      wmic.setTrace("ON");
      wmic.setRecord(
          Constants.QUOTE_DOUBLE
              + Constants.DELIMETER_PATH
              + "wrbts"
              + Constants.DELIMETER_PATH
              + "shared"
              + Constants.DELIMETER_PATH
              + "bts QA"
              + Constants.DELIMETER_PATH
              + "QATools"
              + Constants.DELIMETER_PATH
              + "Logs"
              + Constants.DELIMETER_PATH
              + wmic.getNode()
              + "_"
              + dateTimeStamp
              + IExtension.XML
              + Constants.QUOTE_DOUBLE);
      wmic.setInteractive("OFF");
      wmic.setFailfast("ON");
      wmic.setUser("btsqa");
      wmic.setPassword("AUTO_BTSQA_PASSWORD");
      wmic.setOutput(
          Constants.QUOTE_DOUBLE
              + Constants.DELIMETER_PATH
              + "wrbts"
              + Constants.DELIMETER_PATH
              + "shared"
              + Constants.DELIMETER_PATH
              + "bts QA"
              + Constants.DELIMETER_PATH
              + "QATools"
              + Constants.DELIMETER_PATH
              + "Logs"
              + Constants.DELIMETER_PATH
              + wmic.getNode()
              + "_"
              + dateTimeStamp
              + IExtension.LOG
              + Constants.QUOTE_DOUBLE);
      wmic.setOutput("STDOUT");
      // wmic.setAppend("append")
      wmic.setAggregate("ON");
      // wmic.setAuthority("authority")
      wmic.getWmicAlias().setAlias(null);
      wmic.setCommand(
          "CMD /C "
              + Constants.QUOTE_DOUBLE
              + "C:"
              + Constants.DELIMETER_PATH
              + "Users"
              + Constants.DELIMETER_PATH
              + "btsqa"
              + Constants.DELIMETER_PATH
              + "Desktop"
              + Constants.DELIMETER_PATH
              + "TEST_WMIC"
              + IExtension.BAT
              + Constants.QUOTE_DOUBLE);
      final Map<String, String> results = wmic.run();
      LOG.info("results: [{}]", results);
    } catch (final Exception e) {
      LOG.error("Error in wmic test", e);
    }
  }

  @Test
  public void dropbox() throws Throwable {
    LOG.info("{}]", Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName());
    JDBC jdbc = new JDBC("", DATABASE_QA);
    StringBuilder sqlStringBuilder = new StringBuilder();
    sqlStringBuilder.append(
        JDBCConstants.SELECT_ALL + JDBCConstants.FROM + "[v_Dropbox_ActiveAccounts]");
    // sqlStringBuilder.append(" " + JDBCConstants.WHERE + "[Email] = '" +
    // CJSConstants.EMAIL_ADDRESS_GMAIL + "';")
    final String eMailList = jdbc.queryResults(sqlStringBuilder.toString(), "", false);
    if (!eMailList.isEmpty()) {
      LOG.info("eMailList: [{}]", eMailList);
      final String[] eMails = eMailList.split(Constants.NEWLINE);
      for (String eMail : eMails) {
        LOG.debug("eMail: [{}]", eMail);
        getDropbox().getSignInPage().signInToVerifyActiveAccount(eMail);
      }
    }
  }

  @Test
  public void everyoneSocial() throws Throwable {
    LOG.info("{}]", Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName());
    getEveryoneSocial()
        .getSignInPage()
        .login(CJSConstants.EMAIL_ADDRESS_VIVIT, EPasswords.EVERYONE_SOCIAL.getValue());
    getEveryoneSocial().getNavigationPage().clickLinkStreamsCompanyStream();
    getEveryoneSocial().sharePosts();
    getEveryoneSocial().getNavigationPage().clickLinksStreams();
    getEveryoneSocial().getNavigationPage().clickLinksPersonal();
    getEveryoneSocial().getNavigationPage().clickLinksMyStatus();
    getEveryoneSocial().getNavigationPage().clickLinksProfiles();
    getEveryoneSocial().getNavigationPage().clickLinksTools();
  }

  public AmericanAirlines getAmericanAirlines() {
    return americanAirlines;
  }

  public Dropbox getDropbox() {
    return dropbox;
  }

  public EveryoneSocial getEveryoneSocial() {
    return everyoneSocial;
  }

  public Google getGoogle() {
    return google;
  }

  public IaDhs getIadhs() {
    return iadhs;
  }

  public void getJenkinsInfo() {
    String pathTemp = Constants.PATH_TEMP_WINDOWS;
    final String extension = IExtension.BAT;
    List<String> filesJenkinsList = FSOTests.filesList(pathTemp, IExtension.BAT);
    if (!filesJenkinsList.isEmpty()) {
      LOG.debug("pathTemp: [{}]", pathTemp);
      LOG.debug("extension: [{}]", extension);
      LOG.debug("filesJenkinsList: [{}]", filesJenkinsList.toString());
      for (String filePathName : filesJenkinsList) {
        LOG.debug("filePathName: [{}]", filePathName);
        String fileContents = FSOTests.fileReadAll(filePathName);
        LOG.debug("fileContents: [{}]", fileContents);
      }
    }
  }

  public Hardees getHardees() {
    return hardees;
  }

  public LinkedIn getLinkedIn() {
    return linkedIn;
  }

  public Marlboro getMarlboro() {
    return marlboro;
  }

  public Microsoft getMicrosoft() {
    return microsoft;
  }

  public PolkCounty getPolkCounty() {
    return polkCounty;
  }

  public SeleniumWebDriver getSeleniumWebDriver() {
    return seleniumWebDriver;
  }

  public United getUnited() {
    return united;
  }

  public Vivit getVivit() {
    return vivit;
  }

  public Wellmark getWellmark() {
    return wellmark;
  }

  @Test
  public void google() throws Throwable {
    LOG.info("{}]", Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName());
    JavaHelpers.displaySystemProperties();
    getGoogle().getSignInPage().load();
    getGoogle().getSignInPage().editSearchSet("Searching");
    getGoogle().getSignInPage().pressEnter();
  }

  @Test // @Ignore
  public void hardees() throws Throwable {
    LOG.info("{}]", Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName());
    getHardees().getSurveyPage().populate();
  }

  @Test
  public void iaDhs() throws Throwable {
    getIadhs().getSignInPage().signIn(CJSConstants.USERID_DHS, EPasswords.IADHS.getValue());
    getIadhs().getCasePaymentsPage().getPreviousPayments();
  }

  @Test
  public void jenkins() throws Throwable {
    LOG.info("{}]", Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName());
    final SoftAssert softAssert = new SoftAssert();
    // Firefox tests disabled until framework changes are complete
    List<String> browserList = Arrays.asList(ISelenium.BROWSER_DEFAULT, "edge", "ie");
    // List<String> browserList = Arrays.asList(ISelenium.BROWSER_DEFAULT, "edge", "firefox", "ie");
    for (String browser : browserList) {
      LOG.info("Validating Browser Version: [{}]", browser);
      final WebDriverValidation webDriverValidation =
          new WebDriverValidation(getSeleniumWebDriver().getWebDriver());
      webDriverValidation.validate(browser, softAssert);
    }
    softAssert.assertAll();
    browserList = Arrays.asList(ISelenium.BROWSER_DEFAULT, "edge");
    // browserList = Arrays.asList("ie","firefox")
    for (String browser : browserList) {
      jenkinsWebDriverValidate(browser, softAssert);
    }
    softAssert.assertAll();
  }

  private void jenkinsWebDriverValidate(String browser, SoftAssert softAssert) throws Throwable {
    if (!ISelenium.BROWSER_DEFAULT.equalsIgnoreCase(browser)) {
      setSeleniumWebDriver(
          new SeleniumWebDriver(browser, Environment.isRunRemote(), null, null, null));
    }
    LOG.info("Testing Browser: [{}]", getSeleniumWebDriver().getBrowser());
    try {
      List<String> urlList =
          Arrays.asList(
              "https://www.google"
                  + IExtension.COM
                  + "/search?source=hp&ei=6-S2WvWLLISAsQXKn5bQCA"
                  + "&q=selenium+scrolling&oq=selenium+scrolling"
                  + "&gs_l=psy-ab.3...44501.49572.0.49803.24.14.1.0.0.0.467.467.4-1.2.0"
                  + "....0...1.1.64.psy-ab..21.3.604.6..0j46j35i39k1j0i227i67k1"
                  + "j0i20i263k1j0i67k1j0i46k1j0i131k1.134.9dIQCkpwP-Y");
      for (final String url : urlList) {
        getSeleniumWebDriver().getWebDriver().get(url);
        captureImages();
      }
    } catch (final Exception e) {
      softAssert.assertEquals(browser, "", "Driver could not be instanciated: [" + browser + "]");
    }
    getSeleniumWebDriver().killBrowser();
  }

  @Test
  public void jenkinsColors() throws Throwable {
    getSeleniumWebDriver().killBrowser();
    getSeleniumWebDriver().setBrowser(ISelenium.BROWSER_DEFAULT);
    getSeleniumWebDriver().initializeWebDriver();
    getSeleniumWebDriver()
        .getWebDriver()
        .get("https://htmlcolorcodes" + IExtension.COM + "/color-names/");
    final WebDriver webDriver = getSeleniumWebDriver().getWebDriver();
    // final Selenium selenium = new Selenium(webDriver)
    final List<WebElement> sectionsWebElement =
        webDriver.findElements(By.xpath("/html/body/main/article/section"));
    final List<ColorPalette> colorPaletteList = new ArrayList<>();
    for (final WebElement sectionWebElement : sectionsWebElement) {
      final List<WebElement> rowsWebElement =
          sectionWebElement.findElements(By.xpath("./table/tbody/tr"));
      for (int rowIndex = 0; rowIndex < rowsWebElement.size(); rowIndex++) {
        Page page = new Page(webDriver);
        final WebElement rowWebElement = rowsWebElement.get(rowIndex);
        page.scrollToElement(rowWebElement);
        page.highlightCurrentElement(rowWebElement);
        String palette = null;
        if (rowIndex == 0) {
          final WebElement paletteWebElement = rowWebElement.findElement(By.xpath("./th/h5"));
          page.highlightCurrentElement(paletteWebElement);
          palette = paletteWebElement.getText();
          LOG.debug("palette: [{}]", palette);
        } else {
          final WebElement nameWebElement = rowWebElement.findElement(By.xpath("./td[2]/h4"));
          page.highlightCurrentElement(nameWebElement);
          final String name = nameWebElement.getText();
          final WebElement hexWebElement = rowWebElement.findElement(By.xpath("./td[3]/h4"));
          page.highlightCurrentElement(hexWebElement);
          final String hex = hexWebElement.getText();
          final WebElement rgbWebElement = rowWebElement.findElement(By.xpath("./td[4]/h4"));
          page.highlightCurrentElement(rgbWebElement);
          final String rgb = rgbWebElement.getText();
          final ColorPalette colorPalette = new ColorPalette(palette, name, hex, rgb);
          colorPaletteList.add(colorPalette);
          LOG.debug("colorPalette: {}", colorPalette.toString());
        }
      }
    }
    final ColorPalette colorPalette = new ColorPalette("", "", "", "");
    final String hex = colorPalette.toCode(colorPaletteList, "HEX");
    LOG.info("HEX code: {}", hex);
    final String rgb = colorPalette.toCode(colorPaletteList, "RGB");
    LOG.info("RGB code: {}", rgb);
  }

  @Test
  public void jenkinsPluralsight() throws Throwable {
    final String filePathname =
        Constants.PATH_DESKTOP + "PluralSightSessionInformation" + IExtension.TXT;
    final List<String> listSessions = new ArrayList<>();
    final String urlPluralsight = "https://app.pluralsight";
    listSessions.add(urlPluralsight + IExtension.COM + "/library/courses/communication-skills");
    listSessions.add(
        urlPluralsight + IExtension.COM + "/library/courses/effective-email-communication");
    listSessions.add(
        urlPluralsight
            + IExtension.COM
            + "/library/courses/working-communicating-with-different-personalities");
    listSessions.add(
        urlPluralsight + IExtension.COM + "/library/courses/project-management-beginners-guide");
    listSessions.add(
        urlPluralsight
            + IExtension.COM
            + "/library/courses/project-scope-managing-resources-defining");
    listSessions.add(
        urlPluralsight
            + IExtension.COM
            + "/library/courses/technical-writing-software-documentation");
    listSessions.add(
        urlPluralsight + IExtension.COM + "/library/courses/test-driven-development-java");
    // final String url =
    // URL_PLURALSIGHT + IExtension.COM +
    // "/player?course=scrum-master-fundamentals-scrum-master"
    // + "&author=jeremy-jarrell&name=scrum-master-fundamentals-scrum-master-m0"
    // + "&clip=0&mode=live"
    final PluralSightPage pluralSight = new PluralSightPage(getSeleniumWebDriver().getWebDriver());
    pluralSight.getLoginPage().login();
    FSOTests.fileDelete(filePathname);
    for (String session : listSessions) {
      final String sessionInformation = pluralSight.getSessionPage().getSessionInformation(session);
      FSOTests.fileWrite(filePathname, sessionInformation, true);
      final String tableOfContents =
          pluralSight.getTableOfContentsPage().getTableOfContents(session);
      FSOTests.fileWrite(filePathname, tableOfContents, true);
      FSOTests.fileWrite(filePathname, Constants.NEWLINE, true);
      LOG.info("Session information: {}", sessionInformation);
    }
  }

  @Test
  public void linkedIn() throws Throwable {
    LOG.info("{}]", Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName());
    // getLinkedIn().run(getSeleniumWebDriver().getWebDriver());
    getLinkedIn()
        .getLoginPage()
        .login(
            getLinkedIn().getLoginAlternatePage(),
            CJSConstants.EMAIL_ADDRESS_MSN,
            EPasswords.LINKEDIN.getValue());
    getLinkedIn().getConnectionURLS(getSeleniumWebDriver().getWebDriver(), true);
    getLinkedIn().getConnectionContactInfo(getSeleniumWebDriver().getWebDriver(), true);
  }

  @Test
  public void marlboro() throws Throwable {
    LOG.info("{}]", Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName());
    final SoftAssert softAssert = new SoftAssert();
    JDBC jdbc = new JDBC("", DATABASE_QA);
    StringBuilder sqlStringBuilder = new StringBuilder();
    sqlStringBuilder.append(JDBCConstants.SELECT_ALL + JDBCConstants.FROM + "[t_Marlboro] ");
    sqlStringBuilder.append(
        JDBCConstants.WHERE + "[UserName] " + JDBCConstants.NOT + "IN('ZScharer') ");
    sqlStringBuilder.append(JDBCConstants.ORDER_BY + "[UserName];");
    final List<Map<String, String>> listMapUsers =
        jdbc.queryResultsString(sqlStringBuilder.toString(), false);
    LOG.debug("listMapUsers: [{}]", listMapUsers.toString());
    for (final Map<String, String> mapUser : listMapUsers) {
      LOG.debug("mapUser: [{}]", mapUser.toString());
      getMarlboro().getSignInPage().populate(mapUser);
      JavaHelpers.sleep(3);
      // getMarlboro().getSecurityPage().buttonNotNowClick()
      // getMarlboro().getVerifyInformationInterruptPage().PopulatePage()
      getSeleniumWebDriver().getWebDriver().get(Marlboro.URL_OFFERS);
      if (getMarlboro().getOffersAndActivityPage().labelCouponInTheMailValidate()) {
        softAssert.assertEquals(
            "THANK YOU FOR YOUR REQUEST.",
            getMarlboro().getOffersAndActivityPage().getlabelMessage(),
            mapUser.get("UserName"));
        JavaHelpers.sleep(2);
      } else {
        getMarlboro().getOffersAndActivityPage().collectCoupons();
        JavaHelpers.sleep(2);
        softAssert.assertEquals(
            "THANK YOU FOR YOUR REQUEST.",
            getMarlboro().getOffersAndActivityPage().getlabelMessage(),
            mapUser.get("UserName"));
      }
      getMarlboro().getSignInPage().wrapUp();
      // getMarlboro().getOffersAndActivityPage().buttonLogOutClick()
      JavaHelpers.sleep(2);
    }
    softAssert.assertAll();
  }

  @Test
  public void marlboroEarnPoints() throws Throwable {
    LOG.info("{}]", Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName());
    JDBC jdbc = new JDBC("", DATABASE_QA);
    String sql =
        JDBCConstants.SELECT_ALL
            + JDBCConstants.FROM
            + "[t_Marlboro] "
            + JDBCConstants.WHERE
            + "[UserName] IN ('AScharer') "
            + JDBCConstants.ORDER_BY
            + "[UserName]";
    final List<Map<String, String>> listMapUsers = jdbc.queryResultsString(sql, false);
    LOG.debug("listMapUsers: [{}]", listMapUsers.toString());
    for (final Map<String, String> mapUser : listMapUsers) {
      LOG.debug("mapUser: [{}]", mapUser.toString());
      getMarlboro().getSignInPage().populate(mapUser);
      JavaHelpers.sleep(3);
      getMarlboro().getEarnPointsPage().earnPoints(getMarlboro(), mapUser);
    }
  }

  @Test
  public void microsoft() throws Throwable {
    LOG.info("{}]", Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName());
    try {
      if (getMicrosoft().getRewardsPage().load()) {
        getMicrosoft().getRewardsPage().clickSignInWithMicrosoftButton();
        getMicrosoft().getSignInPage().login();
        getMicrosoft().getRewardsPage().load();
      }
      // getMicrosoft().getRewardsPage().getPoints10CardsAvailable()
      // getMicrosoft().getBingPage().searchRandomSites(true)
      // getMicrosoft().getRewardsPage().getPoints20Info()
      int searches = 0;
      while (getMicrosoft().getRewardsPage().isSearchesRequired() && searches < 2) {
        getMicrosoft().getBingPage().searchRandomSites(true);
        getMicrosoft().getRewardsPage().getPoints20Info();
        searches++;
      }
      getMicrosoft().getRewardsPage().getPoints10CardsAvailable();
      getMicrosoft().getRewardsPage().createReport();
      getMicrosoft().getRewardsPage().sendReport();
    } catch (Exception e) {
      throw new QAException(e);
    }
  }

  @Test
  public void polkCounty() {
    LOG.info("{}]", Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName());
    getPolkCounty().getMain().getInmatesOnTheWeb(true);
  }

  public void setAmericanAirlines(AmericanAirlines americanAirlines) {
    this.americanAirlines = americanAirlines;
  }

  public void setDropbox(Dropbox dropbox) {
    this.dropbox = dropbox;
  }

  public void setEveryoneSocial(EveryoneSocial everyoneSocial) {
    this.everyoneSocial = everyoneSocial;
  }

  public void setGoogle(Google google) {
    this.google = google;
  }

  public void setHardees(Hardees hardees) {
    this.hardees = hardees;
  }

  public void setIadhs(IaDhs iadhs) {
    this.iadhs = iadhs;
  }

  public void setLinkedIn(LinkedIn linkedIn) {
    this.linkedIn = linkedIn;
  }

  public void setMarlboro(Marlboro marlboro) {
    this.marlboro = marlboro;
  }

  public void setMicrosoft(Microsoft microsoft) {
    this.microsoft = microsoft;
  }

  public void setPolkCounty(PolkCounty polkCounty) {
    this.polkCounty = polkCounty;
  }

  public void setSeleniumWebDriver(SeleniumWebDriver seleniumWebDriver) {
    this.seleniumWebDriver = seleniumWebDriver;
  }

  public void setUnited(United united) {
    this.united = united;
  }

  public void setVivit(Vivit vivit) {
    this.vivit = vivit;
  }

  public void setWellmark(Wellmark wellmark) {
    this.wellmark = wellmark;
  }

  @Test
  public void testQueryAccess() throws Throwable {
    final JDBC jdbc = new JDBC("", "QAAutoWEB");
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(JDBCConstants.SELECT_ALL);
    stringBuilder.append(JDBCConstants.FROM + "[Hours_Contractor] ");
    stringBuilder.append(JDBCConstants.WHERE + "[Hours]>0 ");
    stringBuilder.append(JDBCConstants.ORDER_BY + "[Year_Month],[Contractor]");
    String invoicing = jdbc.queryResults(stringBuilder.toString(), ",", true);
    LOG.info("invoicing: [{}]", invoicing);
    stringBuilder = new StringBuilder();
    stringBuilder.append(JDBCConstants.SELECT_ALL);
    stringBuilder.append(JDBCConstants.FROM + "[Hours_Contractor] ");
    final String year = DateHelpersTests.getCurrentDateTime("yyyy");
    String month =
        String.format("%02d", Integer.valueOf(DateHelpersTests.getCurrentDateTime("MM")) - 1);
    if ("0".equals(month)) {
      month = "12";
    }
    stringBuilder.append(JDBCConstants.WHERE + "[Year_Month]='" + year + month + "' ");
    stringBuilder.append(JDBCConstants.AND + "[Hours]>0 ");
    stringBuilder.append(JDBCConstants.ORDER_BY + "[Year_Month],[Contractor]");
    invoicing = jdbc.queryResults(stringBuilder.toString(), ",", true);
    LOG.info("invoicing: [{}]", invoicing);
  }

  @Test
  public void testQueryMapping() throws Throwable {
    final String tableFrom = "t_DOM_Vivit Contractor";
    final String tableTo = "t_DOM_Vivit ContractorNew";
    JDBC jdbc = new JDBC("", DATABASE_QA);
    StringBuilder sqlStringBuilder = new StringBuilder();
    sqlStringBuilder.append(JDBCConstants.DELETE_FROM + "[" + tableTo + "]");
    int recordsDeleted = 0;
    try {
      recordsDeleted = jdbc.executeUpdate(sqlStringBuilder.toString(), false);
    } catch (final Exception e) {
      LOG.error("Error deleting records", e);
    }
    LOG.info("recordsDeleted: [{}]", recordsDeleted);
    final Map<String, String> fields = new HashMap<>();
    fields.put("Title", "Job Title");
    fields.put("Contractor", "Contractor Name");
    fields.put("Company", "Employer");
    fields.put("EMail", "Email Address");
    fields.put("ActiveContractor", "Active Contractor");
    fields.put("ActiveBoD", "Active Director");
    fields.put("PositionDescription", "Position");
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(JDBCConstants.SELECT + "[");
    final String keySet = fields.keySet().toString();
    final String keyValues = fields.values().toString();
    // LOG.debug("keySet:[{}]", keySet);
    // for (String key : fields.keySet())
    // {
    // LOG.debug("key:[{}]", key);
    // }
    stringBuilder.append(keySet.substring(1, keySet.length() - 1).replace(", ", "],["));
    stringBuilder.append("] ");
    stringBuilder.append(JDBCConstants.FROM + "[" + tableFrom + "]");
    List<Map<String, String>> listResults = jdbc.queryResultsString(stringBuilder.toString(), true);
    LOG.debug("listResults: [{}]", listResults.toString());
    stringBuilder = new StringBuilder();
    stringBuilder.append(JDBCConstants.INSERT_INTO + "[" + tableTo + "] ([");
    stringBuilder.append(keyValues.substring(1, keyValues.length() - 1).replace(", ", "],["));
    stringBuilder.append("])" + JDBCConstants.VALUES + "(");
    sqlStringBuilder = new StringBuilder();
    for (int index = 1; index < listResults.size(); index++) {
      sqlStringBuilder.append(stringBuilder.toString());
      StringBuilder stringBuilderRecord = new StringBuilder();
      final Map<String, String> mapRecord = listResults.get(index);
      LOG.debug("mapRecord: [{}]", mapRecord.toString());
      for (final String key : fields.keySet()) {
        if (stringBuilderRecord.length() > 0) {
          stringBuilderRecord.append("','");
        }
        stringBuilderRecord.append(mapRecord.get(key).replaceAll("'", "''"));
      }
      LOG.debug("stringBuilderRecord: [{}]", stringBuilderRecord.toString());
      sqlStringBuilder.append("'" + stringBuilderRecord.toString() + "'");
      sqlStringBuilder.append(");");
    }
    LOG.debug("sqlStringBuilder: [{}]", sqlStringBuilder.toString());
    int recordsAdded = 0;
    try {
      recordsAdded = jdbc.executeUpdate(sqlStringBuilder.toString(), false);
    } catch (final Exception e) {
      LOG.error("Error adding records", e);
    }
    LOG.info("recordsAdded: [{}]", recordsAdded);
  }

  @Test
  public void jenkinsTestSendEMail() throws Throwable {
    final String date = new Date().toString();
    final String subject = "Test-Send Email";
    String from = "";
    // Empty password is intentional - will be set from EPasswords enum below
    String password = "";
    String body = "<h1>Test</h1><p>Send <b>Email</b>.</p><p>" + date + "</p>";
    String signatureFile = "";
    String attachment = signatureFile;
    try {
      from = CJSConstants.EMAIL_ADDRESS_AOL;
      password = EPasswords.EMAIL_AOL.getValue();
      signatureFile = Constants.PATH_OUTLOOK_SIGNATURES + "AOL" + IExtension.HTML;
      attachment = signatureFile;
      body += FSOTests.fileReadAll(signatureFile);
      sendEmail(from, password, subject, body, attachment);
      //
      from = CJSConstants.EMAIL_ADDRESS_GMAIL;
      password = EPasswords.EMAIL_GMAIL.getValue();
      signatureFile = Constants.PATH_OUTLOOK_SIGNATURES + "GMail" + IExtension.HTML;
      body += FSOTests.fileReadAll(signatureFile);
      attachment = signatureFile;
      sendEmail(from, password, subject, body, attachment);
      //
      from = CJSConstants.EMAIL_ADDRESS_MSN;
      password = EPasswords.EMAIL_MSN.getValue();
      signatureFile = Constants.PATH_OUTLOOK_SIGNATURES + "MSN" + IExtension.HTML;
      body += FSOTests.fileReadAll(signatureFile);
      attachment = signatureFile;
      sendEmail(from, password, subject, body, attachment);
      //
      from = CJSConstants.EMAIL_ADDRESS_VIVIT;
      password = EPasswords.EMAIL_VIVIT.getValue();
      signatureFile = Constants.PATH_OUTLOOK_SIGNATURES + "Vivit" + IExtension.HTML;
      body += FSOTests.fileReadAll(signatureFile);
      attachment = signatureFile;
      sendEmail(from, password, subject, body, attachment);
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void united() throws Throwable {
    LOG.info("{}]", Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName());
    getUnited().getHomePage().load();
    getUnited().getHomePage().buttonSignInClick();
    getUnited().getLoginPage().login("JGD80526", EPasswords.UNITED.getValue(), "unchecked");
    getUnited().getSecurityPage().answerSecurityQuestions();
    getUnited()
        .getMemberAccountUpdatePage()
        .validateMemberAccountInformation(
            "2211 Grand Ave Unit 3\nWest Des Moines, IA 502655628",
            CJSConstants.EMAIL_ADDRESS_MSN,
            "+1 (515) 681-7770");
    // Mari Crist Lusaran Dahunan
    // getUnited().BookTravelPage.bookTravel("Des Moines, IA, US (DSM)",
    // "Cebu, PH (CEB)", "Dec 11, 2017",
    // "Dec 19, 2017")
    // .//*[@id='fl-results-loader-full']/div[@class='spinner-container']
    getUnited().getHomePage().buttonSignInClick();
    getUnited().getHomePage().buttonSignOutClick();
  }

  @Test
  public void vivit() throws Throwable {
    LOG.info("{}]", Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName());
    // setVivit(new Vivit(getSeleniumWebDriver().getWebDriver()))
    // getVivit().BoDPage.getData();
    // getVivit().StaffPage.getData();
    getVivit()
        .getHomePage()
        .signIn(CJSConstants.USERID_VIVIT, EPasswords.VIVIT.getValue(), "checked");
    // getVivit().getHomePage().clickUpdateProfileLink()
    VivitDataFactory vivitDataFactory = new VivitDataFactory();
    DataTable dataTable = vivitDataFactory.getBio("Web_Site_Member_ID", "10969660");
    // vivitDataFactory.getBio("Username", CJSConstants.USERID_VIVIT)
    // vivitDataFactory.getBio("Email_Address",
    // CJSConstants.EMAIL_ADDRESS_VIVIT)
    // vivitDataFactory.getBio("Email_Address_Alternate",
    // CJSConstants.EMAIL_ADDRESS_MSN)
    LOG.info("DataTable: {}", dataTable.toString());
    // getVivit().MyMemberProfilePage.populatePageAndValidate(dataTable)
    // getVivit().SearchPage.validateGroups()
    VivitDataTests.initializeTest(getVivit());
    VivitDataTests.initializeData(getSeleniumWebDriver());
    getVivit().getGroupPage().getGroupPageData();
    // getVivit().getSearchPage().searchSites()
    getVivit().getBlogsPage().getBlogData();
    getVivit().getCalendarsPage().getCalendarData();
    getVivit().getForumsPage().getForumData();
    getVivit().getHomePage().clickButtonSignOut();
    getSeleniumWebDriver().killBrowser();
    VivitDataTests.wrapUp(true, true);
  }

  @Test
  public void vivitCommutes() throws Throwable {
    Environment.setEnvironmentFileStructure("Google");
    setGoogle(new Google(getSeleniumWebDriver().getWebDriver()));
    Environment.setScrollToObject(false);
    VivitDataTests.getGoogleCommutes(getGoogle());
    // getSeleniumWebDriver().killBrowser();
    // VivitDataTests.updateGoogleCommutesXLS(new JDBC("", DATABASE_QA));
  }

  @Test
  public void vivitTest() throws Throwable {
    // VivitDataTests.createReportHTMLTestInformation(getSeleniumWebDriver());
    getSeleniumWebDriver().killBrowser();
    // YMDataTests.push();
    // YMDataTests.getAllEventInformation();
    // YMDataTests.importGoToWebinar();
    VivitDataTests.wrapUp(true, false);
    // VivitDataTests.finalizeTest();
  }

  @Test
  public void vivitCreateYmapiMapping() throws Throwable {
    Assert.fail("This has already created the tables.");
    YMAPIMethodsTests.createMapping(getSeleniumWebDriver().getWebDriver());
  }

  // @Ignore
  @Test
  public void wellmark() throws Throwable {
    LOG.info("{}]", Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName());
    getWellmark().getLogInPage().login();
    getWellmark().getPopUpPage().linkNoThanksClick();
    getWellmark().getHomePage().linkSeeAllClaimsClick();
    getWellmark().getClaimsAndSpendingPage().getRecords();
    // getWellmark().DetailsPage.getData()
  }
}
