package com.cjs.qa.vivit;

import java.time.Duration;

import org.openqa.selenium.WebDriver;

import com.cjs.qa.vivit.pages.BlogsPage;
import com.cjs.qa.vivit.pages.BoDPage;
import com.cjs.qa.vivit.pages.CalendarsPage;
import com.cjs.qa.vivit.pages.ForumsPage;
import com.cjs.qa.vivit.pages.GroupPage;
import com.cjs.qa.vivit.pages.HomePage;
import com.cjs.qa.vivit.pages.MyMemberProfilePage;
import com.cjs.qa.vivit.pages.SearchPage;
import com.cjs.qa.vivit.pages.StaffPage;

public class Vivit {

  private BlogsPage blogsPage;
  private BoDPage bodPage;
  private CalendarsPage calendarsPage;
  private ForumsPage forumsPage;
  private GroupPage groupPage;
  private HomePage homePage;
  private MyMemberProfilePage myMemberProfilePage;
  private SearchPage searchPage;
  private StaffPage staffPage;

  public Vivit(WebDriver webDriver) {
    blogsPage = new BlogsPage(webDriver);
    bodPage = new BoDPage(webDriver);
    calendarsPage = new CalendarsPage(webDriver);
    forumsPage = new ForumsPage(webDriver);
    groupPage = new GroupPage(webDriver);
    homePage = new HomePage(webDriver);
    myMemberProfilePage = new MyMemberProfilePage(webDriver);
    searchPage = new SearchPage(webDriver);
    staffPage = new StaffPage(webDriver);
    webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
  }

  public BlogsPage getBlogsPage() {
    return blogsPage;
  }

  public BoDPage getBodPage() {
    return bodPage;
  }

  public CalendarsPage getCalendarsPage() {
    return calendarsPage;
  }

  public ForumsPage getForumsPage() {
    return forumsPage;
  }

  public GroupPage getGroupPage() {
    return groupPage;
  }

  public HomePage getHomePage() {
    return homePage;
  }

  public MyMemberProfilePage getMyMemberProfilePage() {
    return myMemberProfilePage;
  }

  public SearchPage getSearchPage() {
    return searchPage;
  }

  public StaffPage getStaffPage() {
    return staffPage;
  }
}
