package com.cjs.qa.americanairlines.pages;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.americanairlines.AmericanAirlinesEnvironment;
import com.cjs.qa.core.QAException;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.GuardedLogger;

public class VacationabilityPage extends Page {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(VacationabilityPage.class));

  public VacationabilityPage(WebDriver webDriver) {
    super(webDriver);
  }

  private int searches = 180;
  private static final By labelLoading =
      By.xpath(".//*[@id='cphBody_divLoader']//div[@class='loader-text']");
  private static final By editFirstName = By.xpath(".//*[@id='txtFirst']");
  private static final By editLastName = By.xpath(".//*[@id='txtLast']");
  private static final By editEmail = By.xpath(".//*[@id='txtEmail']");
  private static final By editAANumber = By.xpath(".//*[@id='txtAANumber']");
  private static final By checkboxAgree =
      By.xpath(".//*[@id='divRegistration']//label[@for='chkAgree']/span");
  private static final By buttonRegister = By.xpath(".//*[@id='btnRegister']");
  private static final By labelThankYou =
      By.xpath(".//*[@id='cphBody_divStaticPage']//div[@class='box-title-copy']");

  private int getSearches() {
    return searches;
  }

  private void setSearches(int searches) {
    this.searches = searches;
  }

  private By getLabelLoading() {
    return labelLoading;
  }

  private By getEditFirstName() {
    return editFirstName;
  }

  private By getEditLastName() {
    return editLastName;
  }

  private By getEditEmail() {
    return editEmail;
  }

  private By getEditAANumber() {
    return editAANumber;
  }

  private By getCheckboxAgree() {
    return checkboxAgree;
  }

  private By getButtonRegister() {
    return buttonRegister;
  }

  private By getLabelThankYou() {
    return labelThankYou;
  }

  public void editFirstNameSet(String value) throws QAException {
    setEdit(getEditFirstName(), value);
  }

  public void editLastNameSet(String value) throws QAException {
    setEdit(getEditLastName(), value);
  }

  public void editEmailSet(String value) throws QAException {
    setEdit(getEditEmail(), value);
  }

  public void editAANumberSet(String value) throws QAException {
    setEdit(getEditAANumber(), value);
  }

  public void checkboxAgreeSet(String value) throws QAException {
    setCheckbox(getCheckboxAgree(), value);
  }

  public void buttonRegisterClick() throws QAException {
    clickObject(getButtonRegister());
    waitExists(getLabelThankYou());
    setSearches(getSearches() + 1);
    LOG.debug("Registered:[{}] times.", getSearches());
  }

  public void load() throws QAException {
    maximizeWindow();
    LOG.debug("Loading:[{}]", AmericanAirlinesEnvironment.URL_LOGIN);
    getWebDriver().get(AmericanAirlinesEnvironment.URL_LOGIN);
    int waits = 0;
    do {
      waits++;
    } while (objectExists(getLabelLoading()));
    LOG.debug("Waited For Load:[{}] times.", waits);
  }

  public void populate() throws QAException {
    load();
    // editFirstNameSet("Christopher");
    // editLastNameSet("Scharer");
    // editEmailSet(CJSConstants.EMAIL_ADDRESS_MSN);
    // editAANumber("7L86P34");
    // checkboxAgreeSet(LABEL_OPTION_CHECKED);
    // buttonRegisterClick();
  }
}
