package com.cjs.qa.united;

import org.openqa.selenium.WebDriver;

import com.cjs.qa.united.pages.BookTravelPage;
import com.cjs.qa.united.pages.HomePage;
import com.cjs.qa.united.pages.LoginPage;
import com.cjs.qa.united.pages.MemberAccountUpdatePage;
import com.cjs.qa.united.pages.SecurityPage;

public class United {

  private BookTravelPage bookTravelPage;
  private HomePage homePage;
  private LoginPage loginPage;
  private MemberAccountUpdatePage memberAccountUpdatePage;
  private SecurityPage securityPage;

  public United(WebDriver webDriver) {
    bookTravelPage = new BookTravelPage(webDriver);
    homePage = new HomePage(webDriver);
    loginPage = new LoginPage(webDriver);
    memberAccountUpdatePage = new MemberAccountUpdatePage(webDriver);
    securityPage = new SecurityPage(webDriver);
    webDriver.manage().timeouts().pageLoadTimeout(java.time.Duration.ofSeconds(10));
  }

  public BookTravelPage getBookTravelPage() {
    return bookTravelPage;
  }

  public HomePage getHomePage() {
    return homePage;
  }

  public LoginPage getLoginPage() {
    return loginPage;
  }

  public MemberAccountUpdatePage getMemberAccountUpdatePage() {
    return memberAccountUpdatePage;
  }

  public SecurityPage getSecurityPage() {
    return securityPage;
  }
}
