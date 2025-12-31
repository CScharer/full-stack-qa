package com.cjs.qa.marlboro;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.marlboro.pages.EarnPointsPage;
import com.cjs.qa.marlboro.pages.OffersAndActivityPage;
import com.cjs.qa.marlboro.pages.SecurityPage;
import com.cjs.qa.marlboro.pages.SignInPage;
import com.cjs.qa.marlboro.pages.VerifyInformationInterruptPage;
import com.cjs.qa.utilities.IExtension;

public class Marlboro {

  public static final String URL_OFFERS =
      MarlboroEnvironment.URL_BASE + "/pages/offers" + IExtension.HTML;
  private EarnPointsPage earnPointsPage;
  private OffersAndActivityPage offersAndActivityPage;
  private SecurityPage securityPage;
  private SignInPage signInPage;
  private VerifyInformationInterruptPage verifyInformationInterruptPage;
  private By imageAccount = By.xpath("//i[contains(@class,'icon-account')]");

  public EarnPointsPage getEarnPointsPage() {
    return earnPointsPage;
  }

  public OffersAndActivityPage getOffersAndActivityPage() {
    return offersAndActivityPage;
  }

  public SecurityPage getSecurityPage() {
    return securityPage;
  }

  public SignInPage getSignInPage() {
    return signInPage;
  }

  public VerifyInformationInterruptPage getVerifyInformationInterruptPage() {
    return verifyInformationInterruptPage;
  }

  public By getImageAccount() {
    return imageAccount;
  }

  public Marlboro(WebDriver webDriver) {
    earnPointsPage = new EarnPointsPage(webDriver);
    offersAndActivityPage = new OffersAndActivityPage(webDriver);
    securityPage = new SecurityPage(webDriver);
    signInPage = new SignInPage(webDriver);
    verifyInformationInterruptPage = new VerifyInformationInterruptPage(webDriver);
    webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
  }
}
