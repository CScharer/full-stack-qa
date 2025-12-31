package com.cjs.qa.everyonesocial;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cjs.qa.everyonesocial.pages.CompanyStreamPage;
import com.cjs.qa.everyonesocial.pages.CulturePage;
import com.cjs.qa.everyonesocial.pages.CustomerStoriesCaseStudiesPage;
import com.cjs.qa.everyonesocial.pages.EventsPage;
import com.cjs.qa.everyonesocial.pages.MicroFocusPage;
import com.cjs.qa.everyonesocial.pages.NavigationPage;
import com.cjs.qa.everyonesocial.pages.ProductLaunchesPage;
import com.cjs.qa.everyonesocial.pages.SignInPage;
import com.cjs.qa.everyonesocial.pages.TechBeaconPage;
import com.cjs.qa.utilities.JavaHelpers;

public class EveryoneSocial {

  private CompanyStreamPage companyStreamPage;
  private CulturePage culturePage;
  private CustomerStoriesCaseStudiesPage customerStoriesCaseStudiesPage;
  private EventsPage eventsPage;
  private MicroFocusPage microFocusPage;
  private NavigationPage navigationPage;
  private ProductLaunchesPage productLaunchesPage;
  private SignInPage signInPage;
  private TechBeaconPage techBeaconPage;
  private WebDriver webDriver = null;

  public CompanyStreamPage getCompanyStreamPage() {
    return companyStreamPage;
  }

  public CulturePage getCulturePage() {
    return culturePage;
  }

  public CustomerStoriesCaseStudiesPage getCustomerStoriesCaseStudiesPage() {
    return customerStoriesCaseStudiesPage;
  }

  public EventsPage getEventsPage() {
    return eventsPage;
  }

  public MicroFocusPage getMicroFocusPage() {
    return microFocusPage;
  }

  public NavigationPage getNavigationPage() {
    return navigationPage;
  }

  public ProductLaunchesPage getProductLaunchesPage() {
    return productLaunchesPage;
  }

  public SignInPage getSignInPage() {
    return signInPage;
  }

  public TechBeaconPage getTechBeaconPage() {
    return techBeaconPage;
  }

  /** Default constructor for data binding and instantiation. */
  public EveryoneSocial() {
    // Default constructor for data binding
  }

  public EveryoneSocial(WebDriver webDriver) {
    setWebDriver(webDriver);
    companyStreamPage = new CompanyStreamPage(webDriver);
    culturePage = new CulturePage(webDriver);
    customerStoriesCaseStudiesPage = new CustomerStoriesCaseStudiesPage(webDriver);
    eventsPage = new EventsPage(webDriver);
    microFocusPage = new MicroFocusPage(webDriver);
    navigationPage = new NavigationPage(webDriver);
    productLaunchesPage = new ProductLaunchesPage(webDriver);
    signInPage = new SignInPage(webDriver);
    techBeaconPage = new TechBeaconPage(webDriver);
    webDriver.manage().timeouts().pageLoadTimeout(java.time.Duration.ofSeconds(10));
  }

  private WebDriver getWebDriver() {
    return webDriver;
  }

  private void setWebDriver(WebDriver webDriver) {
    this.webDriver = webDriver;
  }

  public List<WebElement> webElementListShare() {
    final String buttonsShare = ".//button/span[contains(text(),'Share')]";
    List<WebElement> webElementListShared = webElementListShared();
    @SuppressWarnings("unchecked")
    List<WebElement> webElementListShare =
        (List<WebElement>) getWebDriver().findElement(By.xpath(buttonsShare));
    webElementListShare.removeAll(webElementListShared);
    return webElementListShare;
  }

  public List<WebElement> webElementListShared() {
    final String buttonsShared = ".//button/span[contains(text(),'Shared')]";
    @SuppressWarnings("unchecked")
    List<WebElement> result =
        (List<WebElement>) getWebDriver().findElement(By.xpath(buttonsShared));
    return result;
  }

  public void clickButtonCancel() {
    By byCancel = By.xpath(".//*[@id='sharePanel']//button[.='Cancel']");
    getCompanyStreamPage().getWebDriver().findElement(byCancel).click();
  }

  public void clickButtonFinish() {
    By byFinish = By.xpath(".//*[@id='sharePanel']//button[.='Finish']");
    getCompanyStreamPage().getWebDriver().findElement(byFinish).click();
  }

  public boolean sharePosts() {
    List<WebElement> webElementListShared = webElementListShare();
    for (WebElement webElement : webElementListShared) {
      getCompanyStreamPage().scrollToElement(webElement);
      getCompanyStreamPage().flashCurrentElement(webElement, 20);
    }
    List<WebElement> webElementListShare = webElementListShare();
    for (WebElement webElement : webElementListShare) {
      // Only do this for 3 items.
      // webElement.click();
      // JavaHelpers.sleep(3);
      // clickButtonFinish();
      // JavaHelpers.sleep(3);
      getCompanyStreamPage().scrollToElement(webElement);
      getCompanyStreamPage().flashCurrentElement(webElement, 20);
    }

    JavaHelpers.sleep(3);
    return true;
  }
}
