package com.cjs.qa.google;

import org.openqa.selenium.WebDriver;

import com.cjs.qa.google.pages.FlightsPage;
import com.cjs.qa.google.pages.SignInPage;

public class Google {

  private FlightsPage flightsPage;
  private SignInPage signInPage;

  public Google(WebDriver webDriver) {
    flightsPage = new FlightsPage(webDriver);
    signInPage = new SignInPage(webDriver);
    webDriver.manage().timeouts().pageLoadTimeout(java.time.Duration.ofSeconds(120));
  }

  public FlightsPage getFlightsPage() {
    return flightsPage;
  }

  public SignInPage getSignInPage() {
    return signInPage;
  }
}
