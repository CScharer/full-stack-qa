package com.cjs.qa.dropbox.pages;

import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.core.Environment;
import com.cjs.qa.core.QAException;
import com.cjs.qa.core.security.EPasswords;
import com.cjs.qa.dropbox.DropboxEnvironment;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.CJSConstants;
import com.cjs.qa.utilities.GuardedLogger;

public class SignInPage extends Page {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SignInPage.class));

  public SignInPage(WebDriver webDriver) {
    super(webDriver);
  }

  private By checkboxRememberMeInput = By.xpath(".//*[@name='remember_me']");
  private static final By checkboxRememberMe = By.xpath(".//*[@name='remember_me']/..");
  private static final By editEmail = By.xpath(".//*[@name='login_email']");
  private static final By editPassword = By.xpath(".//*[@name='login_password']");
  private static final By buttonNext = By.xpath(".//input[@value='Next']");
  private static final By buttonSignIn = By.xpath(".//button[@id='sign-up-in']");
  private static final By buttonSignInRegular =
      By.xpath(".//button[contains(@class,'signin-button')][@type='submit']");
  private static final By buttonSignInWithGoogle =
      By.xpath(".//button[contains(@class,'auth-google')][@type='button']");

  private By getCheckboxRememberMeInput() {
    return checkboxRememberMeInput;
  }

  private By getCheckboxRememberMe() {
    return checkboxRememberMe;
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

  private By getButtonSignInRegular() {
    return buttonSignInRegular;
  }

  private By getButtonSignInWithGoogle() {
    return buttonSignInWithGoogle;
  }

  public void buttonNextClick() throws QAException {
    if (objectExists(getButtonNext())) {
      clickObject(getButtonNext());
    }
  }

  public void buttonSignInClick() throws QAException {
    if (objectExists(getButtonSignIn())) {
      clickObject(getButtonSignIn());
    }
  }

  public void buttonSignInRegularClick() throws QAException {
    if (objectExists(getButtonSignInRegular())) {
      clickObject(getButtonSignInRegular());
    }
  }

  public void buttonSignInWithGoogleClick() throws QAException {
    if (objectExists(getButtonSignInWithGoogle())) {
      clickObject(getButtonSignInWithGoogle());
    }
  }

  public void checkboxRememberMeSet(String value) throws QAException {
    if (objectExists(getCheckboxRememberMe())) {
      setCheckbox(getCheckboxRememberMeInput(), value);
    }
  }

  @Override
  protected void setCheckbox(By by, String value) {
    if (Environment.isLogAll()) {
      LOG.debug("({Field}{}, {Value}{});", getCheckboxRememberMe().toString(), value);
    }
    switch (value.toLowerCase(Locale.ENGLISH)) {
      case LABEL_OPTION_CHECKED:
        if (!getCheckbox(getCheckboxRememberMeInput()).equals(value)) {
          getWebDriver().findElement(getCheckboxRememberMe()).click();
        }
        break;
      case LABEL_OPTION_UNCHECKED:
        if (!getCheckbox(getCheckboxRememberMeInput()).equals(value)) {
          getWebDriver().findElement(getCheckboxRememberMe()).click();
        }
        break;
      default:
        LOG.warn("The value [{}] is not supported for a Checkbox.", value);
        break;
    }
    if (!getCheckbox(getCheckboxRememberMeInput()).equals(value)) {
      LOG.warn(
          "The value [{}] was not selected for Checkbox [{}].",
          value,
          getCheckboxRememberMe().toString());
    }
  }

  @Override
  protected String getCheckbox(By by) {
    String value;
    value = getWebDriver().findElement(by).getAttribute("aria-checked");
    if ("false".equals(value)) {
      value = LABEL_OPTION_UNCHECKED;
    } else {
      value = LABEL_OPTION_CHECKED;
    }
    if (Environment.isLogAll()) {
      LOG.debug("{}", "({Field}" + by.toString() + ", {Value}" + value + ");");
    }
    return value;
  }

  public void editEmailSet(String value) throws QAException {
    if (objectExists(getEditEmail())) {
      setEdit(getEditEmail(), value);
    }
  }

  public void editPasswordSet(String value) throws QAException {
    if (objectExists(getEditPassword())) {
      setEdit(getEditPassword(), value);
    }
  }

  public void load() throws QAException {
    maximizeWindow();
    LOG.debug("Loading:[{}]", DropboxEnvironment.URL_LOGIN);
    getWebDriver().get(DropboxEnvironment.URL_LOGIN);
    // int waits = 0;
    // do
    // {
    // waits++;
    // } while (objectExists(labelLoading));
    // LOG.debug("Waited For Load:[[{}]", waits + "] times.");
  }

  public void signIn(String value) throws QAException {
    editEmailSet(value);
    buttonNextClick();
  }

  public void signInToVerifyActiveAccount(String eMail) throws QAException {
    load();
    buttonSignInClick();
    editEmailSet(eMail);
    editPasswordSet(EPasswords.DROPBOX.getValue());
    if (CJSConstants.EMAIL_ADDRESS_MSN.equalsIgnoreCase(eMail)) {
      checkboxRememberMeSet(LABEL_OPTION_CHECKED);
    } else {
      checkboxRememberMeSet(LABEL_OPTION_UNCHECKED);
    }
    // buttonSignInRegularClick();
  }

  public void solveCaptcha() throws QAException {
    // .//*[@id='rc-imageselect-target']/table/tbody/tr/td
    // .//*[@id='recaptcha-verify-button']
    // .//*[@id='recaptcha-verify-button']
    // .//*[@id='rc-imageselect']//div[@class='rc-imageselect-desc-no-canonical']/strong
    // .//*[@id='rc-imageselect-target']/table/tbody/tr/td//img
    // getAttribute("src");
    // https://www.google" + IExtension.COM + "/recaptcha/api2/payload?c=03ACgFB9...
    // (Massive reCAPTCHA token URL - ~2287 characters - truncated for readability)
    // ...&k=6LdnLyIUAAAAAOiGPtddh-g3KiJRoDGGPD-6dqXo&id=8a5620dcbbd2c583
    // xhtml:html/xhtml:body/xhtml:img
    // https://www.google" + IExtension.COM + "/search?tbs=sbi:AMhZZiu4OZ2ogWFh0jJZFAtfcBHUZ4hr...
    // (Google image search URL - ~382 characters - truncated for readability)
    // ...Dk-9LsXl_1XzbzAbPfQxv6WznfYRnTfC9_1Gcyd1NvcPvl7qBBAioWlfy4vqGSiuhk_1cNvhEJs...
  }
}
