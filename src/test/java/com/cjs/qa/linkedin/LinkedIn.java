package com.cjs.qa.linkedin;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.core.security.EPasswords;
import com.cjs.qa.linkedin.data.DataTests;
import com.cjs.qa.linkedin.pages.ConnectionsPage;
import com.cjs.qa.linkedin.pages.ContactInfoPage;
import com.cjs.qa.linkedin.pages.HomePage;
import com.cjs.qa.linkedin.pages.LoginAlternatePage;
import com.cjs.qa.linkedin.pages.LoginPage;
import com.cjs.qa.utilities.CJSConstants;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.JavaHelpers;

public class LinkedIn {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(LinkedIn.class));

  public static final String LINKEDIN_URL =
      LinkedInEnvironment.URL_LOGIN.toLowerCase(Locale.ENGLISH) + "in/";
  private ConnectionsPage connectionsPage;
  private ContactInfoPage contactInfoPage;
  private HomePage homePage;
  private LoginAlternatePage loginAlternatePage;
  private LoginPage loginPage;

  public LinkedIn(WebDriver webDriver) {
    connectionsPage = new ConnectionsPage(webDriver);
    contactInfoPage = new ContactInfoPage(webDriver);
    homePage = new HomePage(webDriver);
    loginAlternatePage = new LoginAlternatePage(webDriver);
    loginPage = new LoginPage(webDriver);
    webDriver.manage().timeouts().pageLoadTimeout(java.time.Duration.ofSeconds(10));
  }

  public ConnectionsPage getConnectionsPage() {
    return connectionsPage;
  }

  public ContactInfoPage getContactInfoPage() {
    return contactInfoPage;
  }

  public HomePage getHomePage() {
    return homePage;
  }

  public LoginAlternatePage getLoginAlternatePage() {
    return loginAlternatePage;
  }

  public LoginPage getLoginPage() {
    return loginPage;
  }

  public void getConnectionContactInfo(WebDriver webDriver, boolean run) throws Throwable {
    LOG.debug(
        "{}", Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    String methodName = JavaHelpers.getCurrentMethodName();
    if (!run) {
      LOG.debug("{}:run:[{}]", methodName, run);
      return;
    }
    List<Map<String, String>> linkedInMapList =
        DataTests.getJdbc().queryResultsString(DataTests.getQueryLinkedInURL(), false);
    if (linkedInMapList.isEmpty()) {
      LOG.debug("{}:linkedInMapList.size():[{}]", methodName, linkedInMapList.size());
      return;
    }
    contactInfoPage.getContactInfoPageData(linkedInMapList);
    DataTests.update();
  }

  public void getConnectionURLS(WebDriver webDriver, boolean run) throws Throwable {
    LOG.debug(
        "{}", Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    String methodName = JavaHelpers.getCurrentMethodName();
    if (!run) {
      LOG.debug("{}:run:[{}]", methodName, run);
      return;
    }
    List<Map<String, String>> linkedInMapList =
        DataTests.getJdbc().queryResultsString(DataTests.getQueryNoLinkedInURL(), false);
    if (linkedInMapList.isEmpty()) {
      LOG.debug("{}:linkedInMapList.size():[{}]", methodName, linkedInMapList.size());
      return;
    }
    homePage.clickButtonMyNetwork();
    sleepRandom(2, 4, 250, 750);
    String urlConnections = LinkedInEnvironment.URL_LOGIN + "mynetwork/invite-connect/connections/";
    webDriver.get(urlConnections);
    StringBuilder sqlStringBuilder = new StringBuilder();
    for (Map<String, String> map : linkedInMapList) {
      if (JavaHelpers.hasValue(map.get("First Name"))
          && JavaHelpers.hasValue(map.get("Last Name"))) {
        connectionsPage.setEditSearchbyname(
            map.get("First Name").trim() + " " + map.get("Last Name").trim());
        sleepRandom(2, 4, 250, 750);
        String href = connectionsPage.getUserHREF();
        if (JavaHelpers.hasValue(href)) {
          href = href.toLowerCase(Locale.ENGLISH);
          if (href.contains(LINKEDIN_URL)) {
            // href = href.substring(href.indexOf(LINKEDIN_URL))
            href = href.replaceAll(LINKEDIN_URL, "");
          }
          href = href.replaceAll("/", "");
          map.put(DataTests.FIELD_LINKEDIN_URL, href);
          sqlStringBuilder = DataTests.appendRecordURL(sqlStringBuilder, map);
        }
      }
    }
    DataTests.updateRecords(sqlStringBuilder);
    DataTests.getJdbc().executeUpdate(DataTests.getQueryUpdateNotFound(), false);
    DataTests.getJdbc().executeUpdate(DataTests.getQueryUpdateLinkedInURL(), false);
  }

  public void run(WebDriver webDriver) throws Throwable {
    loginPage.login(
        loginAlternatePage, CJSConstants.EMAIL_ADDRESS_MSN, EPasswords.LINKEDIN.getValue());
    getConnectionURLS(webDriver, true);
    getConnectionContactInfo(webDriver, true);
  }

  public static void sleepRandom(
      int secondsMin, int secondsMax, int millisecondsMin, int millisecondsMax) {
    LOG.debug(
        "{}", Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    String methodName = JavaHelpers.getCurrentMethodName();
    int seconds = JavaHelpers.generateRandomInteger(secondsMin, secondsMax);
    int milliseconds = JavaHelpers.generateRandomInteger(millisecondsMin, millisecondsMax);
    LOG.debug(
        "{}-randomNumberSeconds:[{}], randomNumberMilliSeconds:[{}]",
        methodName,
        seconds,
        milliseconds);
    JavaHelpers.sleep(seconds, milliseconds);
  }
}
