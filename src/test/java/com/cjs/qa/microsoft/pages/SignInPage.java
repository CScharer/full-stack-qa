package com.cjs.qa.microsoft.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.core.security.EPasswords;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.CJSConstants;

public class SignInPage extends Page {

  private static final By checkboxKeepMeSignedIn = By.xpath(".//input[@name='KMSI']");
  private static final By editEmail = By.xpath(".//input[@name='loginfmt']");
  private static final By editPassword = By.xpath(".//input[@name='passwd']");
  private static final By buttonNext = By.xpath(".//input[@value='Next']");
  private static final By buttonSignIn = By.xpath(".//input[@value='Sign in']");

  private By getCheckboxKeepMeSignedIn() {
    return checkboxKeepMeSignedIn;
  }

  private By getEditEmail() {
    return editEmail;
  }

  private By getEditPassword() {
    return editPassword;
  }

  private By getButtonNext() {
    return buttonNext;
  }

  private By getButtonSignIn() {
    return buttonSignIn;
  }

  /**
   * @param webDriver
   */
  public SignInPage(WebDriver webDriver) {
    super(webDriver);
  }

  public void buttonNextClick() {
    if (objectExists(getButtonNext())) {
      clickObject(getButtonNext());
    }
  }

  public void buttonSignInClick() {
    if (objectExists(getButtonSignIn())) {
      clickObject(getButtonSignIn());
    }
  }

  /**
   * @param value
   */
  public void checkboxKeepMeSignedInSet(String value) {
    if (objectExists(getCheckboxKeepMeSignedIn())) {
      setCheckbox(getCheckboxKeepMeSignedIn(), value);
    }
  }

  /**
   * @param value
   */
  public void editEmailSet(String value) {
    if (objectExists(getEditEmail())) {
      setEdit(getEditEmail(), value);
    }
  }

  /**
   * @param value
   */
  public void editPasswordSet(String value) {
    if (objectExists(getEditPassword())) {
      setEdit(getEditPassword(), value);
    }
  }

  public void login() {
    editEmailSet(CJSConstants.EMAIL_ADDRESS_MSN);
    buttonNextClick();
    editPasswordSet(EPasswords.EMAIL_MSN.getValue());
    checkboxKeepMeSignedInSet(LABEL_OPTION_CHECKED);
    buttonSignInClick();
  }

  /**
   * @param value
   */
  public void signIn(String value) {
    editEmailSet(value);
    buttonNextClick();
  }
}
