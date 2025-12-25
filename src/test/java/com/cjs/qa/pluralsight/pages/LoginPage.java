package com.cjs.qa.pluralsight.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.core.security.EAPIKeys;
import com.cjs.qa.core.security.EPasswords;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.IExtension;

public class LoginPage extends Page {

  public LoginPage(WebDriver webDriver) {
    super(webDriver);
  }

  public static final String DEFAULT_URL = "https://app.pluralsight" + IExtension.COM;
  private static final By editUserName = By.id("Username");
  private static final By editPassword = By.id("Password");
  private static final By buttonSignIn = By.id("login");

  public void buttonSignInClick() {
    clickObject(buttonSignIn);
  }

  public void editUserNameSet(String value) {
    setEdit(editUserName, value);
  }

  public void editPasswordSet(String value) {
    setEditPassword(editPassword, value);
  }

  public void login() {
    getWebDriver().get(DEFAULT_URL);
    // Credentials migrated to Google Cloud Secret Manager
    // See: docs/issues/open/cleanup-hardcoded-passwords.md
    final String userName = EAPIKeys.PLURALSIGHT_TRAINING_USERNAME.getValue();
    final String password = EPasswords.PLURALSIGHT_TRAINING_PASSWORD.getValue();
    editUserNameSet(userName);
    editPasswordSet(password);
    buttonSignInClick();
  }
}
