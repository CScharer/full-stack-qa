package com.cjs.qa.google.pages;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.core.QAException;
import com.cjs.qa.google.GoogleEnvironment;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.GuardedLogger;

public class SignInPage extends Page {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SignInPage.class));

  public SignInPage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final By editSearch = By.name("q");

  public void editSearchSet(String value) throws QAException {
    if (objectExists(editSearch)) {
      setEdit(editSearch, value);
    }
  }

  public void load() throws QAException {
    maximizeWindow();
    LOG.debug("Loading:[{}]", GoogleEnvironment.URL_LOGIN);
    getWebDriver().get(GoogleEnvironment.URL_LOGIN);
  }
}
