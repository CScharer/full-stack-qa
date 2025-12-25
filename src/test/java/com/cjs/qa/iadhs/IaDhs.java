package com.cjs.qa.iadhs;

import org.openqa.selenium.WebDriver;

import com.cjs.qa.iadhs.pages.CasePaymentsPage;
import com.cjs.qa.iadhs.pages.SignInPage;

public class IaDhs {

  private CasePaymentsPage casePaymentsPage;
  private SignInPage signInPage;

  public CasePaymentsPage getCasePaymentsPage() {
    return casePaymentsPage;
  }

  public SignInPage getSignInPage() {
    return signInPage;
  }

  public IaDhs(WebDriver webDriver) {
    casePaymentsPage = new CasePaymentsPage(webDriver);
    signInPage = new SignInPage(webDriver);
    webDriver.manage().timeouts().pageLoadTimeout(java.time.Duration.ofSeconds(120));
  }
}
