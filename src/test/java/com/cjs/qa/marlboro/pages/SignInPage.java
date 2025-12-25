package com.cjs.qa.marlboro.pages;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cjs.qa.core.QAException;
import com.cjs.qa.core.security.EPasswords;
import com.cjs.qa.marlboro.MarlboroEnvironment;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.GuardedLogger;

public class SignInPage extends Page {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SignInPage.class));

  public SignInPage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final By editUserName = By.xpath(".//*[@id='UserName']");
  private static final By editPassword = By.xpath(".//*[@id='Password']");
  private static final By editDateOfBirthMonth = By.xpath(".//*[@id='DateOfBirthMonth']");
  private static final By editDateOfBirthDay = By.xpath(".//*[@id='DateOfBirthDay']");
  private static final By editDateOfBirthYear = By.xpath(".//*[@id='DateOfBirthYear']");
  private static final By checkboxRememberUsername = By.xpath(".//*[@id='rememberUsername']");
  private static final By buttonEnter = By.xpath(".//*[@id='loginBtn']");
  private static final By imageAccount = By.xpath(".//a[@data-dtmtext='Account Logo']");
  private static final By linkLogOut = By.xpath(".//a[contains(text(),'LOG OUT')]");

  private By getEditUserName() {
    return editUserName;
  }

  private By getEditPassword() {
    return editPassword;
  }

  private By getEditDateOfBirthMonth() {
    return editDateOfBirthMonth;
  }

  private By getEditDateOfBirthDay() {
    return editDateOfBirthDay;
  }

  private By getEditDateOfBirthYear() {
    return editDateOfBirthYear;
  }

  private By getCheckboxRememberUsername() {
    return checkboxRememberUsername;
  }

  private By getButtonEnter() {
    return buttonEnter;
  }

  private By getImageAccount() {
    return imageAccount;
  }

  private By getLinkLogOut() {
    return linkLogOut;
  }

  public void buttonEnterClick() throws QAException {
    clickObject(getButtonEnter());
  }

  public void checkboxRememberUsernameSet(String value) throws QAException {
    setCheckbox(getCheckboxRememberUsername(), value);
  }

  public void editUserNameSet(String value) throws QAException {
    setEdit(getEditUserName(), value);
  }

  public void editPasswordSet(String value) throws QAException {
    setEditPassword(getEditPassword(), value);
  }

  public void editDateOfBirthMonthSet(String value) throws QAException {
    setEdit(getEditDateOfBirthMonth(), value);
  }

  public void editDateOfBirthDaySet(String value) throws QAException {
    setEdit(getEditDateOfBirthDay(), value);
  }

  public void editDateOfBirthYearSet(String value) throws QAException {
    setEdit(getEditDateOfBirthYear(), value);
  }

  public void imageAccountClick() throws QAException {
    // getWebDriver().get(MarlboroEnvironment.URL_BASE +
    // "/pages/my-account/profile-page"
    // + IExtension.HTML);
    final List<WebElement> webElements = getWebDriver().findElements(getImageAccount());
    final WebElement imageAccount = webElements.get(1);
    clickObject(imageAccount);
  }

  public void linkLogOutClick() throws QAException {
    final List<WebElement> webElements = getWebDriver().findElements(getLinkLogOut());
    final WebElement linkLogOut = webElements.get(0);
    clickObject(linkLogOut);
  }

  public void populate(Map<String, String> mapUser) throws QAException {
    do {
      load();
    } while (!objectExists(getEditUserName(), 1));
    editUserNameSet(mapUser.get("UserName"));
    editPasswordSet(EPasswords.MARLBORO.getValue());
    // editDateOfBirthMonthSet(mapUser.get("Month"));
    // editDateOfBirthDaySet(mapUser.get("Day"));
    // editDateOfBirthYearSet(mapUser.get("Year"));
    // checkboxRememberUsernameSet(LABEL_OPTION_UNCHECKED);
    buttonEnterClick();
  }

  public void load() throws QAException {
    maximizeWindow();
    LOG.debug("Loading:[[{}]", MarlboroEnvironment.URL_LOGIN + "]");
    getWebDriver().get(MarlboroEnvironment.URL_LOGIN);
  }

  public void wrapUp() throws QAException {
    imageAccountClick();
    linkLogOutClick();
  }
}
