package com.cjs.qa.everyonesocial.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.Page;

public class NavigationPage extends Page {

  public NavigationPage(WebDriver webDriver) {
    super(webDriver);
  }

  // DOCUMENTATION
  // clickLinkMyStatsAnalytics:
  // Clicks the [MyStatsAnalytics] Link.
  // clickLinkMyStatsLeaderboard:
  // Clicks the [MyStatsLeaderboard] Link.
  // clickLinkPersonalManageStreams:
  // Clicks the [PersonalManage Streams] Link.
  // clickLinkPersonalMyStream:
  // Clicks the [PersonalMy Stream] Link.
  // clickLinkProfilesFacebook:
  // Clicks the [ProfilesFacebook] Link.
  // clickLinkProfilesLinkedIn:
  // Clicks the [ProfilesLinkedIn] Link.
  // clickLinkProfilesManageProfiles:
  // Clicks the [ProfilesManage Profiles] Link.
  // clickLinkProfilesTwitter:
  // Clicks the [ProfilesTwitter] Link.
  // clickLinkProfilesXing:
  // Clicks the [ProfilesXing] Link.
  // clickLinkStreamsCompanyStream:
  // Clicks the [StreamsCompany Stream] Link.
  // clickLinkStreamsCulture:
  // Clicks the [StreamsCulture] Link.
  // clickLinkStreamsCustomerStoriesCaseStudies:
  // Clicks the [StreamsCustomer Stories/Case Studies] Link.
  // clickLinkStreamsEvents:
  // Clicks the [StreamsEvents] Link.
  // clickLinkStreamsManageStreams:
  // Clicks the [StreamsManage Streams] Link.
  // clickLinkStreamsMicroFocus:
  // Clicks the [StreamsMicroFocus] Link.
  // clickLinkStreamsProductLaunches:
  // Clicks the [StreamsProduct Launches] Link.
  // clickLinkStreamsTechBeacon:
  // Clicks the [StreamsTechBeacon] Link.
  // clickLinkToolsAddOns:
  // Clicks the [ToolsAdd-Ons] Link.
  // clickLinkToolsHelpCenter:
  // Clicks the [ToolsHelp Center] Link.
  // clickLinkToolsLogout:
  // Clicks the [ToolsLogout] Link.
  // clickLinkToolsSettings:
  // Clicks the [ToolsSettings] Link.
  // populatePage:
  // Populates the value of the all of the fields.
  // validatePage:
  // Validates the value of the all of the fields.

  // INPUTS

  // TABLE DEF
  // DataTable dataTable = Convert.toDataTable(Arrays.asList(
  // Arrays.asList("MyStatsAnalytics", "MyStatsAnalytics"),
  // Arrays.asList("MyStatsLeaderboard", "MyStatsLeaderboard"),
  // Arrays.asList("PersonalManage Streams", "PersonalManage Streams"),
  // Arrays.asList("PersonalMy Stream", "PersonalMy Stream"),
  // Arrays.asList("ProfilesFacebook", "ProfilesFacebook"),
  // Arrays.asList("ProfilesLinkedIn", "ProfilesLinkedIn"),
  // Arrays.asList("ProfilesManage Profiles", "ProfilesManage Profiles"),
  // Arrays.asList("ProfilesTwitter", "ProfilesTwitter"),
  // Arrays.asList("ProfilesXing", "ProfilesXing"),
  // Arrays.asList("StreamsCompany Stream", "StreamsCompany Stream"),
  // Arrays.asList("StreamsCulture", "StreamsCulture"),
  // Arrays.asList("StreamsCustomer Stories/Case Studies", "StreamsCustomer
  // Stories/Case Studies"),
  // Arrays.asList("StreamsEvents", "StreamsEvents"),
  // Arrays.asList("StreamsManage Streams", "StreamsManage Streams"),
  // Arrays.asList("StreamsMicroFocus", "StreamsMicroFocus"),
  // Arrays.asList("StreamsProduct Launches", "StreamsProduct Launches"),
  // Arrays.asList("StreamsTechBeacon", "StreamsTechBeacon"),
  // Arrays.asList("ToolsAdd-Ons", "ToolsAdd-Ons"),
  // Arrays.asList("ToolsHelp Center", "ToolsHelp Center"),
  // Arrays.asList("ToolsLogout", "ToolsLogout"),
  // Arrays.asList("ToolsSettings", "ToolsSettings")));

  public By getByLinkStreamsCompanyStream() {
    return By.xpath(".//li[@class='streamMenu']//span[.='Company Stream']");
  }

  public By getByLinkPersonalManageStreams() {
    return By.xpath(".//li[@class='personalMenu']//span[.='Manage Streams']");
  }

  public By getByLinkMyStatsAnalytics() {
    return By.xpath(".//li[@class='statsMenu']//span[.='Analytics']");
  }

  public By getByLinkMyStatsLeaderboard() {
    return By.xpath(".//li[@class='statsMenu']//span[.='Leaderboard']");
  }

  public By getByLinkProfilesLinkedIn() {
    return By.xpath(".//li[@class='queueMenu']//li[@class='linkedin']/a");
  }

  public By getByLinkProfilesFacebook() {
    return By.xpath(".//li[@class='queueMenu']//li[@class='twitter']/a");
  }

  public By getByLinkProfilesTwitter() {
    return By.xpath(".//li[@class='queueMenu']//li[@class='facebook']/a");
  }

  public By getByLinkProfilesXing() {
    return By.xpath(".//li[@class='queueMenu']//li[@class='xing']/a");
  }

  public By getByLinkProfilesManageProfiles() {
    return By.xpath(".//li[@class='queueMenu']//li[@class='profiles']/a");
  }

  public By getByLinkToolsHelpCenter() {
    return By.xpath(".//li[@class='toolsMenu']//span[.='Help Center']");
  }

  public By getByLinkToolsAddOns() {
    return By.xpath(".//li[@class='toolsMenu']//span[.='Add-Ons']");
  }

  public By getByLinkStreamsCulture() {
    return By.xpath(".//li[@class='streamMenu']//span[.='Culture']");
  }

  public By getByLinkToolsSettings() {
    return By.xpath(".//li[@class='toolsMenu']//span[.='Settings']");
  }

  public By getByLinkToolsLogout() {
    return By.xpath(".//li[@class='toolsMenu']//span[.='Logout']");
  }

  public By getByLinkStreamsCustomerStoriesCaseStudies() {
    return By.xpath(".//li[@class='streamMenu']//span[.='Customer Stories/Case Studies']");
  }

  public By getByLinkStreamsEvents() {
    return By.xpath(".//li[@class='streamMenu']//span[.='Events']");
  }

  public By getByLinkStreamsProductLaunches() {
    return By.xpath(".//li[@class='streamMenu']//span[.='Product Launches']");
  }

  public By getByLinkStreamsTechBeacon() {
    return By.xpath(".//li[@class='streamMenu']//span[.='TechBeacon']");
  }

  public By getByLinkStreamsMicroFocus() {
    return By.xpath(".//li[@class='streamMenu']//span[.='MicroFocus']");
  }

  public By getByLinkStreamsManageStreams() {
    return By.xpath(".//li[@class='streamMenu']//span[.='Manage Streams']");
  }

  public By getByLinkPersonalMyStream() {
    return By.xpath(".//li[@class='personalMenu']//span[.='My Stream']");
  }

  public void clickLinkStreamsCompanyStream() {
    clickObject(getByLinkStreamsCompanyStream());
  }

  public boolean isLinkStreamsCompanyStreamDisplayed() {
    return isDisplayed(getByLinkStreamsCompanyStream());
  }

  public boolean isLinkStreamsCompanyStreamEnabled() {
    return isEnabled(getByLinkStreamsCompanyStream());
  }

  public void clickLinkPersonalManageStreams() {
    clickObject(getByLinkPersonalManageStreams());
  }

  public boolean isLinkPersonalManageStreamsDisplayed() {
    return isDisplayed(getByLinkPersonalManageStreams());
  }

  public boolean isLinkPersonalManageStreamsEnabled() {
    return isEnabled(getByLinkPersonalManageStreams());
  }

  public void clickLinkMyStatsAnalytics() {
    clickObject(getByLinkMyStatsAnalytics());
  }

  public boolean isLinkMyStatsAnalyticsDisplayed() {
    return isDisplayed(getByLinkMyStatsAnalytics());
  }

  public boolean isLinkMyStatsAnalyticsEnabled() {
    return isEnabled(getByLinkMyStatsAnalytics());
  }

  public void clickLinkMyStatsLeaderboard() {
    clickObject(getByLinkMyStatsLeaderboard());
  }

  public boolean isLinkMyStatsLeaderboardDisplayed() {
    return isDisplayed(getByLinkMyStatsLeaderboard());
  }

  public boolean isLinkMyStatsLeaderboardEnabled() {
    return isEnabled(getByLinkMyStatsLeaderboard());
  }

  public void clickLinkProfilesLinkedIn() {
    clickObject(getByLinkProfilesLinkedIn());
    clickLinkPopUpGotIt();
  }

  public boolean isLinkProfilesLinkedInDisplayed() {
    return isDisplayed(getByLinkProfilesLinkedIn());
  }

  public boolean isLinkProfilesLinkedInEnabled() {
    return isEnabled(getByLinkProfilesLinkedIn());
  }

  public void clickLinkProfilesFacebook() {
    clickObject(getByLinkProfilesFacebook());
    clickLinkPopUpGotIt();
  }

  public boolean isLinkProfilesFacebookDisplayed() {
    return isDisplayed(getByLinkProfilesFacebook());
  }

  public boolean isLinkProfilesFacebookEnabled() {
    return isEnabled(getByLinkProfilesFacebook());
  }

  public void clickLinkProfilesTwitter() {
    clickObject(getByLinkProfilesTwitter());
    clickLinkPopUpGotIt();
  }

  public boolean isLinkProfilesTwitterDisplayed() {
    return isDisplayed(getByLinkProfilesTwitter());
  }

  public boolean isLinkProfilesTwitterEnabled() {
    return isEnabled(getByLinkProfilesTwitter());
  }

  public void clickLinkProfilesXing() {
    clickObject(getByLinkProfilesXing());
    clickLinkPopUpGotIt();
  }

  public boolean isLinkProfilesXingDisplayed() {
    return isDisplayed(getByLinkProfilesXing());
  }

  public boolean isLinkProfilesXingEnabled() {
    return isEnabled(getByLinkProfilesXing());
  }

  private void clickLinkPopUpGotIt() {
    // By byGotIt = By.xpath("//button[@data-role='end'][=.'Got It!']");
    By byGotIt = By.xpath("//button[@data-role='end']");
    if (objectExists(byGotIt, 3)) {
      clickObject(byGotIt);
    }
  }

  public void clickLinkProfilesManageProfiles() {
    clickObject(getByLinkProfilesManageProfiles());
    clickLinkPopUpGotIt();
  }

  public boolean isLinkProfilesManageProfilesDisplayed() {
    return isDisplayed(getByLinkProfilesManageProfiles());
  }

  public boolean isLinkProfilesManageProfilesEnabled() {
    return isEnabled(getByLinkProfilesManageProfiles());
  }

  public void clickLinkToolsHelpCenter() {
    clickObject(getByLinkToolsHelpCenter());
  }

  public boolean isLinkToolsHelpCenterDisplayed() {
    return isDisplayed(getByLinkToolsHelpCenter());
  }

  public boolean isLinkToolsHelpCenterEnabled() {
    return isEnabled(getByLinkToolsHelpCenter());
  }

  public void clickLinkToolsAddOns() {
    clickObject(getByLinkToolsAddOns());
  }

  public boolean isLinkToolsAddOnsDisplayed() {
    return isDisplayed(getByLinkToolsAddOns());
  }

  public boolean isLinkToolsAddOnsEnabled() {
    return isEnabled(getByLinkToolsAddOns());
  }

  public void clickLinkStreamsCulture() {
    clickObject(getByLinkStreamsCulture());
  }

  public boolean isLinkStreamsCultureDisplayed() {
    return isDisplayed(getByLinkStreamsCulture());
  }

  public boolean isLinkStreamsCultureEnabled() {
    return isEnabled(getByLinkStreamsCulture());
  }

  public void clickLinkToolsSettings() {
    clickObject(getByLinkToolsSettings());
  }

  public boolean isLinkToolsSettingsDisplayed() {
    return isDisplayed(getByLinkToolsSettings());
  }

  public boolean isLinkToolsSettingsEnabled() {
    return isEnabled(getByLinkToolsSettings());
  }

  public void clickLinkToolsLogout() {
    clickObject(getByLinkToolsLogout());
  }

  public boolean isLinkToolsLogoutDisplayed() {
    return isDisplayed(getByLinkToolsLogout());
  }

  public boolean isLinkToolsLogoutEnabled() {
    return isEnabled(getByLinkToolsLogout());
  }

  public void clickLinkStreamsCustomerStoriesCaseStudies() {
    clickObject(getByLinkStreamsCustomerStoriesCaseStudies());
  }

  public boolean isLinkStreamsCustomerStoriesCaseStudiesDisplayed() {
    return isDisplayed(getByLinkStreamsCustomerStoriesCaseStudies());
  }

  public boolean isLinkStreamsCustomerStoriesCaseStudiesEnabled() {
    return isEnabled(getByLinkStreamsCustomerStoriesCaseStudies());
  }

  public void clickLinkStreamsEvents() {
    clickObject(getByLinkStreamsEvents());
  }

  public boolean isLinkStreamsEventsDisplayed() {
    return isDisplayed(getByLinkStreamsEvents());
  }

  public boolean isLinkStreamsEventsEnabled() {
    return isEnabled(getByLinkStreamsEvents());
  }

  public void clickLinkStreamsProductLaunches() {
    clickObject(getByLinkStreamsProductLaunches());
  }

  public boolean isLinkStreamsProductLaunchesDisplayed() {
    return isDisplayed(getByLinkStreamsProductLaunches());
  }

  public boolean isLinkStreamsProductLaunchesEnabled() {
    return isEnabled(getByLinkStreamsProductLaunches());
  }

  public void clickLinkStreamsTechBeacon() {
    clickObject(getByLinkStreamsTechBeacon());
  }

  public boolean isLinkStreamsTechBeaconDisplayed() {
    return isDisplayed(getByLinkStreamsTechBeacon());
  }

  public boolean isLinkStreamsTechBeaconEnabled() {
    return isEnabled(getByLinkStreamsTechBeacon());
  }

  public void clickLinkStreamsMicroFocus() {
    clickObject(getByLinkStreamsMicroFocus());
  }

  public boolean isLinkStreamsMicroFocusDisplayed() {
    return isDisplayed(getByLinkStreamsMicroFocus());
  }

  public boolean isLinkStreamsMicroFocusEnabled() {
    return isEnabled(getByLinkStreamsMicroFocus());
  }

  public void clickLinkStreamsManageStreams() {
    clickObject(getByLinkStreamsManageStreams());
  }

  public boolean isLinkStreamsManageStreamsDisplayed() {
    return isDisplayed(getByLinkStreamsManageStreams());
  }

  public boolean isLinkStreamsManageStreamsEnabled() {
    return isEnabled(getByLinkStreamsManageStreams());
  }

  public void clickLinkPersonalMyStream() {
    clickObject(getByLinkPersonalMyStream());
  }

  public boolean isLinkPersonalMyStreamDisplayed() {
    return isDisplayed(getByLinkPersonalMyStream());
  }

  public boolean isLinkPersonalMyStreamEnabled() {
    return isEnabled(getByLinkPersonalMyStream());
  }

  public void clickLinksStreams() {
    clickLinkStreamsCompanyStream();

    clickLinkStreamsCulture();

    clickLinkStreamsCustomerStoriesCaseStudies();

    clickLinkStreamsEvents();

    clickLinkStreamsProductLaunches();

    clickLinkStreamsTechBeacon();

    clickLinkStreamsMicroFocus();
    captureScreenshot();
    // clickLinkStreamsManageStreams();
    // captureScreenshot();
  }

  public void clickLinksPersonal() {
    clickLinkPersonalMyStream();
    captureScreenshot();
    // clickLinkPersonalManageStreams();
    // captureScreenshot();
  }

  public void clickLinksMyStatus() {
    clickLinkMyStatsAnalytics();
    clickLinkMyStatsLeaderboard();
    captureScreenshot();
  }

  public void clickLinksProfiles() {
    clickLinkProfilesLinkedIn();
    clickLinkProfilesFacebook();
    clickLinkProfilesTwitter();
    captureScreenshot();
    // clickLinkProfilesXing();
    // captureScreenshot();
    clickLinkProfilesManageProfiles();
    captureScreenshot();
  }

  public void clickLinksTools() {
    // clickLinkToolsHelpCenter();
    clickLinkToolsAddOns();
    clickLinkToolsSettings();
    clickLinkToolsLogout();
    captureScreenshot();
  }
}
