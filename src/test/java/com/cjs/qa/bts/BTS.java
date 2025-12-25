package com.cjs.qa.bts;

import org.openqa.selenium.WebDriver;

import com.cjs.qa.bts.pages.AddNewAddressClientPage;
import com.cjs.qa.bts.pages.AddNewPolicyPage;
import com.cjs.qa.bts.pages.AddNewProductPage;
import com.cjs.qa.bts.pages.AdminFunctionsPage;
import com.cjs.qa.bts.pages.BiAdvantagePage;
import com.cjs.qa.bts.pages.BiAgencyInformationPage;
import com.cjs.qa.bts.pages.BiBasicInfoPage;
import com.cjs.qa.bts.pages.BiBillingInformationPage;
import com.cjs.qa.bts.pages.BiBillingPage;
import com.cjs.qa.bts.pages.BiButtonsPage;
import com.cjs.qa.bts.pages.BiLeftNav;
import com.cjs.qa.bts.pages.BiMiscellaneousInformationPage;
import com.cjs.qa.bts.pages.BiRightNav;
import com.cjs.qa.bts.pages.BiTerrorismPage;
import com.cjs.qa.bts.pages.BiUnderwritingPage;
import com.cjs.qa.bts.pages.BtsCrosswordChallengePage;
import com.cjs.qa.bts.pages.CPLResultsPage;
import com.cjs.qa.bts.pages.CopyPolicyPage;
import com.cjs.qa.bts.pages.IssuePage;
import com.cjs.qa.bts.pages.IssuePageLeftNav;
import com.cjs.qa.bts.pages.LoginPage;
import com.cjs.qa.bts.pages.ManageProductsPage;
import com.cjs.qa.bts.pages.ManageProductsRightNav;
import com.cjs.qa.bts.pages.NamedInsuredsPage;
import com.cjs.qa.bts.pages.PolicyIssuancePage;
import com.cjs.qa.bts.pages.PremiumSummaryPage;
import com.cjs.qa.bts.pages.PremiumSummaryRightNav;
import com.cjs.qa.bts.pages.RateLeftNavPage;
import com.cjs.qa.bts.pages.RatePage;
import com.cjs.qa.bts.pages.RateRightNav;
import com.cjs.qa.bts.pages.ReferenceMaterialsPage;
import com.cjs.qa.bts.pages.RequestPrintPage;
import com.cjs.qa.bts.pages.RequestPrintRightNav;
import com.cjs.qa.bts.pages.SearchPage;
import com.cjs.qa.bts.pages.SearchResultsPage;
import com.cjs.qa.bts.pages.SearchRightNav;
import com.cjs.qa.bts.pages.SidbarAdminFunctionsPage;
import com.cjs.qa.bts.pages.SidbarReferenceMaterialsPage;
import com.cjs.qa.bts.pages.Sidebar;
import com.cjs.qa.core.Environment;
import com.cjs.qa.utilities.SoftAssert;

public class BTS {

  private SoftAssert softAssert;
  private BtsCrosswordChallengePage btsCrosswordChallengePage;
  private AddNewAddressClientPage addNewAddressClientPage;
  private AddNewPolicyPage addNewPolicyPage;
  private AddNewProductPage addNewProductPage;
  private AdminFunctionsPage adminFunctionsPage;
  private BiAdvantagePage biAdvantagePage;
  private BiAgencyInformationPage biAgencyInformationPage;
  private BiBasicInfoPage biBasicInfoPage;
  private BiBillingInformationPage biBillingInformationPage;
  private BiBillingPage biBillingPage;
  private BiButtonsPage biButtonsPage;
  private BiLeftNav biLeftNav;
  private BiMiscellaneousInformationPage biMiscellaneousInformationPage;
  private BiRightNav biRightNav;
  private BiTerrorismPage biTerrorismPage;
  private BiUnderwritingPage biUnderwritingPage;
  private CopyPolicyPage copyPolicyPage;
  private CPLResultsPage cplResultsPage;
  private IssuePage issuePage;
  private IssuePageLeftNav issuePageLeftNav;
  private LoginPage loginPage;
  private ManageProductsPage manageProductsPage;
  private ManageProductsRightNav manageProductsRightNav;
  private NamedInsuredsPage namedInsuredsPage;
  private PolicyIssuancePage policyIssuancePage;
  private PremiumSummaryPage premiumSummaryPage;
  private PremiumSummaryRightNav premiumSummaryRightNav;
  private RatePage ratePage;
  private RateLeftNavPage rateLeftNavPage;
  private RateRightNav rateRightNav;
  private ReferenceMaterialsPage referenceMaterialsPage;
  private RequestPrintPage requestPrintPage;
  private RequestPrintRightNav requestPrintRightNav;
  private SearchPage searchPage;
  private SearchResultsPage searchResultsPage;
  private SearchRightNav searchRightNav;
  private Sidebar sidebar;
  private SidbarAdminFunctionsPage sidbarAdminFunctionsPage;
  private SidbarReferenceMaterialsPage sidbarReferenceMaterialsPage;

  public SoftAssert getSoftAssert() {
    return softAssert;
  }

  public BtsCrosswordChallengePage getBtsCrosswordChallengePage() {
    return btsCrosswordChallengePage;
  }

  public AddNewAddressClientPage getAddNewAddressClientPage() {
    return addNewAddressClientPage;
  }

  public AddNewPolicyPage getAddNewPolicyPage() {
    return addNewPolicyPage;
  }

  public AddNewProductPage getAddNewProductPage() {
    return addNewProductPage;
  }

  public AdminFunctionsPage getAdminFunctionsPage() {
    return adminFunctionsPage;
  }

  public BiAdvantagePage getBiAdvantagePage() {
    return biAdvantagePage;
  }

  public BiAgencyInformationPage getBiAgencyInformationPage() {
    return biAgencyInformationPage;
  }

  public BiBasicInfoPage getBiBasicInfoPage() {
    return biBasicInfoPage;
  }

  public BiBillingInformationPage getBiBillingInformationPage() {
    return biBillingInformationPage;
  }

  public BiBillingPage getBiBillingPage() {
    return biBillingPage;
  }

  public BiButtonsPage getBiButtonsPage() {
    return biButtonsPage;
  }

  public BiLeftNav getBiLeftNav() {
    return biLeftNav;
  }

  public BiMiscellaneousInformationPage getBiMiscellaneousInformationPage() {
    return biMiscellaneousInformationPage;
  }

  public BiRightNav getBiRightNav() {
    return biRightNav;
  }

  public BiTerrorismPage getBiTerrorismPage() {
    return biTerrorismPage;
  }

  public BiUnderwritingPage getBiUnderwritingPage() {
    return biUnderwritingPage;
  }

  public CopyPolicyPage getCopyPolicyPage() {
    return copyPolicyPage;
  }

  public CPLResultsPage getCplResultsPage() {
    return cplResultsPage;
  }

  public IssuePage getIssuePage() {
    return issuePage;
  }

  public IssuePageLeftNav getIssuePageLeftNav() {
    return issuePageLeftNav;
  }

  public LoginPage getLoginPage() {
    return loginPage;
  }

  public ManageProductsPage getManageProductsPage() {
    return manageProductsPage;
  }

  public ManageProductsRightNav getManageProductsRightNav() {
    return manageProductsRightNav;
  }

  public NamedInsuredsPage getNamedInsuredsPage() {
    return namedInsuredsPage;
  }

  public PolicyIssuancePage getPolicyIssuancePage() {
    return policyIssuancePage;
  }

  public PremiumSummaryPage getPremiumSummaryPage() {
    return premiumSummaryPage;
  }

  public PremiumSummaryRightNav getPremiumSummaryRightNav() {
    return premiumSummaryRightNav;
  }

  public RatePage getRatePage() {
    return ratePage;
  }

  public RateLeftNavPage getRateLeftNavPage() {
    return rateLeftNavPage;
  }

  public RateRightNav getRateRightNav() {
    return rateRightNav;
  }

  public ReferenceMaterialsPage getReferenceMaterialsPage() {
    return referenceMaterialsPage;
  }

  public RequestPrintPage getRequestPrintPage() {
    return requestPrintPage;
  }

  public RequestPrintRightNav getRequestPrintRightNav() {
    return requestPrintRightNav;
  }

  public SearchPage getSearchPage() {
    return searchPage;
  }

  public SearchResultsPage getSearchResultsPage() {
    return searchResultsPage;
  }

  public SearchRightNav getSearchRightNav() {
    return searchRightNav;
  }

  public Sidebar getSidebar() {
    return sidebar;
  }

  public SidbarAdminFunctionsPage getSidbarAdminFunctionsPage() {
    return sidbarAdminFunctionsPage;
  }

  public SidbarReferenceMaterialsPage getSidbarReferenceMaterialsPage() {
    return sidbarReferenceMaterialsPage;
  }

  public BTS(WebDriver webDriver) {
    softAssert = new SoftAssert();
    btsCrosswordChallengePage = new BtsCrosswordChallengePage(webDriver);
    addNewAddressClientPage = new AddNewAddressClientPage(webDriver);
    addNewPolicyPage = new AddNewPolicyPage(webDriver);
    addNewProductPage = new AddNewProductPage(webDriver);
    adminFunctionsPage = new AdminFunctionsPage(webDriver);
    biAdvantagePage = new BiAdvantagePage(webDriver);
    biAgencyInformationPage = new BiAgencyInformationPage(webDriver);
    biBasicInfoPage = new BiBasicInfoPage(webDriver);
    biBillingInformationPage = new BiBillingInformationPage(webDriver);
    biBillingPage = new BiBillingPage(webDriver);
    biButtonsPage = new BiButtonsPage(webDriver);
    biLeftNav = new BiLeftNav(webDriver);
    biMiscellaneousInformationPage = new BiMiscellaneousInformationPage(webDriver);
    biRightNav = new BiRightNav(webDriver);
    biTerrorismPage = new BiTerrorismPage(webDriver);
    biUnderwritingPage = new BiUnderwritingPage(webDriver);
    copyPolicyPage = new CopyPolicyPage(webDriver);
    cplResultsPage = new CPLResultsPage(webDriver);
    issuePage = new IssuePage(webDriver);
    issuePageLeftNav = new IssuePageLeftNav(webDriver);
    loginPage = new LoginPage(webDriver);
    manageProductsPage = new ManageProductsPage(webDriver);
    manageProductsRightNav = new ManageProductsRightNav(webDriver);
    namedInsuredsPage = new NamedInsuredsPage(webDriver);
    policyIssuancePage = new PolicyIssuancePage(webDriver);
    premiumSummaryPage = new PremiumSummaryPage(webDriver);
    premiumSummaryRightNav = new PremiumSummaryRightNav(webDriver);
    ratePage = new RatePage(webDriver);
    rateLeftNavPage = new RateLeftNavPage(webDriver);
    rateRightNav = new RateRightNav(webDriver);
    referenceMaterialsPage = new ReferenceMaterialsPage(webDriver);
    requestPrintPage = new RequestPrintPage(webDriver);
    requestPrintRightNav = new RequestPrintRightNav(webDriver);
    searchPage = new SearchPage(webDriver);
    searchResultsPage = new SearchResultsPage(webDriver);
    searchRightNav = new SearchRightNav(webDriver);
    sidebar = new Sidebar(webDriver);
    sidbarAdminFunctionsPage = new SidbarAdminFunctionsPage(webDriver);
    sidbarReferenceMaterialsPage = new SidbarReferenceMaterialsPage(webDriver);
    webDriver
        .manage()
        .timeouts()
        .pageLoadTimeout(java.time.Duration.ofSeconds(Environment.getTimeOutPage()));
  }
}
