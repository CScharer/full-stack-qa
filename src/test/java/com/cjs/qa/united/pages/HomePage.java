package com.cjs.qa.united.pages;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.core.QAException;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.united.UnitedEnvironment;
import com.cjs.qa.utilities.GuardedLogger;

public class HomePage extends Page {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(HomePage.class));

  public HomePage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final By buttonSignIn = By.xpath(".//*[@id='loginButton']");
  private static final By buttonSignOut = By.xpath("//button/span[.='Sign out']");

  public void buttonSignInClick() throws QAException {
    clickObject(buttonSignIn);
  }

  public void buttonSignOutClick() throws QAException {
    clickObject(buttonSignOut);
    sleep(1);
  }

  public void load() {
    maximizeWindow();
    LOG.debug("Loading:[{}]", UnitedEnvironment.URL_LOGIN);
    getWebDriver().get(UnitedEnvironment.URL_LOGIN);
  }
}
