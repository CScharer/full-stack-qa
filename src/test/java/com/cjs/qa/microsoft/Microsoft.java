package com.cjs.qa.microsoft;

import org.openqa.selenium.WebDriver;

import com.cjs.qa.microsoft.pages.BingPage;
import com.cjs.qa.microsoft.pages.RewardsPage;
import com.cjs.qa.microsoft.pages.SignInPage;

public class Microsoft {

  private BingPage bingPage;
  private RewardsPage rewardsPage;
  private SignInPage signInPage;

  public BingPage getBingPage() {
    return bingPage;
  }

  public RewardsPage getRewardsPage() {
    return rewardsPage;
  }

  public SignInPage getSignInPage() {
    return signInPage;
  }

  public Microsoft(WebDriver webDriver) {
    bingPage = new BingPage(webDriver);
    rewardsPage = new RewardsPage(webDriver);
    signInPage = new SignInPage(webDriver);
    webDriver.manage().timeouts().pageLoadTimeout(java.time.Duration.ofSeconds(10));
  }
}
