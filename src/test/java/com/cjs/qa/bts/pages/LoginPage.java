package com.cjs.qa.bts.pages;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.core.Environment;
import com.cjs.qa.jdbc.SQL;
import com.cjs.qa.selenium.ISelenium;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.GuardedLogger;

import io.cucumber.datatable.DataTable;

public class LoginPage extends Page {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(LoginPage.class));

  public LoginPage(WebDriver webDriver) {
    super(webDriver);
  }

  public String openGenesys(String company, String environment)
      throws SQLException, ClassNotFoundException {
    String sURL = null;
    String sUserName = null;
    String sPassword = null;
    if (company == null) {
      company = "";
    }
    if (environment == null) {
      environment = "";
    }
    sURL = SQL.getURL(company, environment);
    sUserName = SQL.getUserName(company, environment);
    if (sURL != null) {
      getWebDriver().get(sURL);
      if (getTitle().toString().equals(getLoginTitle().toString())) {
        setEditUserName(sUserName);
        switch (sUserName.toLowerCase(Locale.ENGLISH)) {
          case "btsqa":
            // ⚠️ SECURITY REVIEW NEEDED: This file contains potential credentials
            // This is not a password, bu a placeholder for a real password.
            sPassword = "btsqa";
            break;
          case "btsrobot":
            // ⚠️ SECURITY REVIEW NEEDED: This file contains potential credentials
            // This is not a password, bu a placeholder for a real password.
            sPassword = "btsrobot";
            break;
          default:
            LOG.warn("Unknown user: {}. Password not set.", sUserName);
            break;
        }
        setEditPassword(sPassword);
        clickButtonSubmit();
      }
    } else {
      LOG.warn("company [{}], environment [{}] not configured.", company, environment);
    }
    return sURL;
  }

  // private String password = Passwords.BTSROBOT_0.getValue();
  // private String uName = "btsrobot";
  // BLD A Genesys
  private static final By editUserName = By.id("username");
  private static final By editPassword = By.id("password");
  private static final By buttonSubmit = By.id("//button[.='Submit']");
  private static final String pageTitle = "LoginPage";

  private String getPageTitle() {
    return pageTitle;
  }

  private static final String loginTitle = "Login";

  private String getLoginTitle() {
    return loginTitle;
  }

  public void verifyPage() {
    verifyTitle(getPageTitle());
  }

  public void setEditUserName(String value) {
    setEdit(editUserName, value);
  }

  public String getEditUserName() {
    return getEdit(editUserName);
  }

  public void setEditPassword(String value) {
    setEditPassword(editPassword, value);
  }

  public String getEditPassword() {
    return getEdit(editPassword);
  }

  public void clickButtonSubmit() {
    clickObject(buttonSubmit);
    // getWebElement(editPassword).sendKeys(Keys.ENTER);
  }

  public void populatePageCuke(DataTable table) {
    populatePage(table);
    clickButtonSubmit();
  }

  public void populatePage(DataTable table) {
    final List<List<String>> data = table.asLists();
    for (final List<?> item : data) {
      final String field = (String) item.get(0);
      final String value = (String) item.get(1);
      if (!value.isEmpty()) {
        if (Environment.isLogAll()) {
          LOG.debug("{}: [{}]", field, value);
        }
        switch (field.toLowerCase(Locale.ENGLISH)) {
          case "username":
            setEditUserName(value);
            break;
          case "password":
            setEditPassword(value);
            break;
          default:
            LOG.warn("[{}]{}", field, ISelenium.FIELD_NOT_CODED);
            break;
        }
      }
    }
  }

  public void validatePage(DataTable table) {
    final Map<String, String> expected = new HashMap<>();
    final Map<String, String> actual = new HashMap<>();
    final List<List<String>> data = table.asLists();
    for (final List<?> item : data) {
      final String field = (String) item.get(0);
      final String value = (String) item.get(1);
      if (!value.isEmpty()) {
        expected.put(field, value);
        switch (field.toLowerCase(Locale.ENGLISH)) {
          case "username":
            actual.put(field, getEditUserName());
            break;
          case "password":
            actual.put(field, getEditPassword());
            break;
          default:
            LOG.warn("[{}]{}", field, ISelenium.FIELD_NOT_CODED);
            break;
        }
      }
    }
    Assertions.assertSame(expected, actual, getPageTitle() + " validatePage");
  }
  // public void openBrowser(String company, String environment) {
  // getWebDriver().get("http://bts-blda/policystarweb/login.faces");
  // }
}
