package com.cjs.qa.wellmark.pages;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.core.QAException;
import com.cjs.qa.core.security.EPasswords;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.CJSConstants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.wellmark.WellmarkEnvironment;

public class LogInPage extends Page {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(LogInPage.class));

  public LogInPage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final By editUserID = By.xpath(".//*[@id='ctl00_body_userid']");
  private static final By editPassword = By.xpath(".//*[@id='ctl00_body_password']");
  private static final By buttonOK = By.xpath(".//*[@id='ctl00_body_btnOk']");

  public void buttonOKClick() throws QAException {
    clickObject(buttonOK);
  }

  public void editUserIDSet(String value) throws QAException {
    setEdit(editUserID, value);
  }

  public void editPasswordSet(String value) throws QAException {
    setEditPassword(editPassword, value);
  }

  public void login(String userID, String password) throws QAException {
    editUserIDSet(userID);
    editPasswordSet(password);
    buttonOKClick();
  }

  public void login() throws QAException {
    load();
    login(CJSConstants.USERID_VIVIT, EPasswords.MY_WELLMARK.getValue());
    waitPageLoad();
    sleep(5);
  }

  public boolean load() throws QAException {
    maximizeWindow();
    LOG.debug("Loading:[{}]", WellmarkEnvironment.URL_LOGIN);
    getWebDriver().get(WellmarkEnvironment.URL_LOGIN);
    // sleep(5);
    // return objectExists(buttonSignInWithMicrosoft);
    return true;
  }

  public void waitPageLoad() throws QAException {
    final By byButtonContinue = By.xpath(".//*[@id='SSOForm']//input[@value='Continue']");
    do {
      Thread.yield(); // Intentional busy wait loop
    } while (objectExists(byButtonContinue));
  }
}
