package com.cjs.qa.pluralsight;

import org.openqa.selenium.WebDriver;

import com.cjs.qa.pluralsight.pages.LoginPage;
import com.cjs.qa.pluralsight.pages.SessionPage;
import com.cjs.qa.pluralsight.pages.TableOfContentsPage;

public class PluralSightPage {

  private LoginPage loginPage;
  private SessionPage sessionPage;
  private TableOfContentsPage tableOfContentsPage;

  public PluralSightPage(WebDriver webDriver) {
    loginPage = new LoginPage(webDriver);
    sessionPage = new SessionPage(webDriver);
    tableOfContentsPage = new TableOfContentsPage(webDriver);
    webDriver.manage().timeouts().pageLoadTimeout(java.time.Duration.ofSeconds(120));
  }

  public LoginPage getLoginPage() {
    return loginPage;
  }

  public SessionPage getSessionPage() {
    return sessionPage;
  }

  public TableOfContentsPage getTableOfContentsPage() {
    return tableOfContentsPage;
  }
}
