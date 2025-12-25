package com.cjs.qa.linkedin.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cjs.qa.core.QAException;
import com.cjs.qa.linkedin.LinkedInEnvironment;
import com.cjs.qa.selenium.ISelenium;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.GuardedLogger;

import io.cucumber.datatable.DataTable;

public class LoginAlternatePage extends Page {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(LoginAlternatePage.class));

  public LoginAlternatePage(WebDriver webDriver) {
    super(webDriver);
  }

  // DOCUMENTATION
  // getEditEmail:
  // Gets the value of the [Email] Edit field.
  // setEditEmail:
  // Sets the value of the [Email] Edit field.
  // getEditPassword:
  // Gets the value of the [Password] Edit field.
  // setEditPassword:
  // Sets the value of the [Password] Edit field.
  // clickButtonSignIn:
  // Clicks the [Sign in] Button.
  // clickButtonSubmit:
  // Clicks the [Submit] Button.
  // populatePage:
  // Populates the value of the all of the fields.
  // validatePage:
  // Validates the value of the all of the fields.

  // INPUTS
  // |Email|Email|
  // |Password|Password|

  // TABLE DEF
  // DataTable dataTable = Convert.toDataTable(Arrays.asList(
  // Arrays.asList("Email", "Email"),
  // Arrays.asList("Password", "Password")));

  public By getByEditEmail() {
    return By.xpath(".//input[@id='username']");
  }

  public By getByEditPassword() {
    return By.xpath(".//input[@id='password']");
  }

  public By getByButtonSignIn() {
    return By.xpath(".//a[@data-tracking-will-navigate='Sign in']");
  }

  public By getByButtonSubmit() {
    return By.xpath(".//button[.='Sign in']");
  }

  public String getEditEmail() {
    return getEdit(getByEditEmail());
  }

  public boolean isEditEmailDisplayed() {
    return isDisplayed(getByEditEmail());
  }

  public boolean isEditEmailEnabled() {
    return isEnabled(getByEditEmail());
  }

  public void setEditEmail(String value) {
    // setEdit(getByEditEmail(), value);
    WebElement webElement = getWebDriver().findElement(getByEditEmail());
    webElement.clear();
    webElement.sendKeys(value);
  }

  public String getEditPassword() {
    return getEdit(getByEditPassword());
  }

  public boolean isEditPasswordDisplayed() {
    return isDisplayed(getByEditPassword());
  }

  public boolean isEditPasswordEnabled() {
    return isEnabled(getByEditPassword());
  }

  public void setEditPassword(String value) {
    // setEditPassword(getByEditPassword(), value);
    WebElement webElement = getWebDriver().findElement(getByEditPassword());
    webElement.clear();
    webElement.sendKeys(value);
  }

  public void clickButtonSignIn() {
    // clickObject(getByButtonSignIn());
    getWebDriver().findElement(getByButtonSignIn()).click();
  }

  public void clickButtonSubmit() {
    // clickObject(getByButtonSubmit());
    getWebDriver().findElement(getByButtonSubmit()).click();
  }

  public boolean isButtonSubmitDisplayed() {
    return isDisplayed(getByButtonSubmit());
  }

  public boolean isButtonSubmitEnabled() {
    return isEnabled(getByButtonSubmit());
  }

  public void load() throws QAException {
    maximizeWindow();
    LOG.debug("Loading: [{}]", LinkedInEnvironment.URL_LOGIN);
    getWebDriver().get(LinkedInEnvironment.URL_LOGIN);
  }

  // SWITCHES POPULATE
  public void populatePage(DataTable dataTable) {
    List<List<String>> list = dataTable.asLists();
    for (List<?> item : list) {
      String field = (String) item.get(0);
      String value = (String) item.get(1);
      if (!value.isEmpty()) {
        LOG.debug("{Field}{}, {Value}{}", field, value);
        switch (field.toLowerCase(Locale.ENGLISH)) {
          case "email":
            setEditEmail(value);
            break;
          case "password":
            setEditPassword(value);
            break;
          default:
            value = "[" + field + "]" + ISelenium.FIELD_NOT_CODED;
            LOG.warn("[{}]{}", field, ISelenium.FIELD_NOT_CODED);
            break;
        }
      }
    }
  }

  // SWITCHES VALIDATE
  public void validatePage(DataTable dataTable) {
    Map<String, String> expected = new HashMap<>();
    Map<String, String> actual = new HashMap<>();
    List<List<String>> list = dataTable.asLists();
    for (List<?> item : list) {
      String field = (String) item.get(0);
      String value = (String) item.get(1);
      if (!value.isEmpty()) {
        LOG.debug("{Field}{}, {Value}{}", field, value);
        expected.put(field, value);
        switch (field.toLowerCase(Locale.ENGLISH)) {
          case "email":
            value = getEditEmail();
            break;
          case "password":
            value = getEditPassword();
            break;
          default:
            value = "[" + field + "]" + ISelenium.FIELD_NOT_CODED;
            LOG.warn("[{}]{}", field, ISelenium.FIELD_NOT_CODED);
            break;
        }
        actual.put(field, value);
      }
    }
    Assert.assertSame(
        this.getClass().getName() + "validatePage", expected.toString(), actual.toString());
  }

  public void login(String emailAddressUserName, String password) throws Throwable {
    // clickButtonSignIn();
    setEditEmail(emailAddressUserName);
    setEditPassword(password);
    clickButtonSubmit();
    load();
  }
}
