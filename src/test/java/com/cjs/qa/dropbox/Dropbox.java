package com.cjs.qa.dropbox;

import org.openqa.selenium.WebDriver;

import com.cjs.qa.dropbox.pages.SignInPage;

public class Dropbox {

  private SignInPage signInPage;

  public SignInPage getSignInPage() {
    return signInPage;
  }

  public Dropbox(WebDriver webDriver) {
    signInPage = new SignInPage(webDriver);
    webDriver.manage().timeouts().pageLoadTimeout(java.time.Duration.ofSeconds(10));
  }
}
