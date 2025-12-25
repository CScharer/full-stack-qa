package com.cjs.qa.iadhs.pages;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.core.QAException;
import com.cjs.qa.iadhs.IaDhsEnvironment;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.GuardedLogger;

public class SignInPage extends Page {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SignInPage.class));

  public SignInPage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final By editUserName = By.xpath(".//*[@id='userId']");
  private static final By editPassword = By.xpath(".//*[@id='xyz']");
  private static final By buttonSignIn = By.xpath(".//*[@id='signInBtn']");

  private By getEditUserName() {
    return editUserName;
  }

  private By getEditPassword() {
    return editPassword;
  }

  private By getButtonSignIn() {
    return buttonSignIn;
  }

  public void buttonSignInClick() throws QAException {
    clickObject(getButtonSignIn());
  }

  public void editUserNameSet(String value) throws QAException {
    setEdit(getEditUserName(), value);
  }

  public void editPasswordSet(String value) throws QAException {
    setEditPassword(getEditPassword(), value);
  }

  public void load() throws QAException {
    maximizeWindow();
    LOG.debug("Loading:[{}]", IaDhsEnvironment.URL_LOGIN);
    getWebDriver().get(IaDhsEnvironment.URL_LOGIN);
  }

  public void signIn(String userName, String password) throws QAException {
    do {
      load();
    } while (!objectExists(getEditUserName(), 1));
    editUserNameSet(userName);
    editPasswordSet(password);
    buttonSignInClick();
  }
}
