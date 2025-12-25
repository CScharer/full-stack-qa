package com.cjs.qa.microsoft.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cjs.qa.core.QAException;
import com.cjs.qa.core.security.EPasswords;
import com.cjs.qa.microsoft.MicrosoftEnvironment;
import com.cjs.qa.microsoft.MicrosoftReport;
import com.cjs.qa.microsoft.objects.PointsBreakdown;
import com.cjs.qa.microsoft.objects.PointsCard;
import com.cjs.qa.microsoft.objects.RewardsInfo;
import com.cjs.qa.selenium.ISelenium;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.selenium.Selenium;
import com.cjs.qa.utilities.CJSConstants;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.DateHelpersTests;
import com.cjs.qa.utilities.Email;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.XML;

public class RewardsPage extends Page {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(RewardsPage.class));
  public static final int POINTS_PER_SEACH_PC = 5;
  public static final String URL_POINTS_BREAKDOWN =
      "https://account.microsoft" + IExtension.COM + "/rewards/pointsbreakdown";
  public static final String FILE_REPORT =
      MicrosoftEnvironment.FOLDER_DATA + "MicrosoftRewardsReport" + IExtension.HTML;
  public static final String FILE_SIGNATURE =
      Constants.PATH_OUTLOOK_SIGNATURES + "MSN" + IExtension.HTML;
  public static final String POINT_GROUPS =
      "Microsoft Edge bonus;PC search;Mobile search;Shop & Earn;Other activities";
  public static final List<String> POINT_GROUPS_LIST =
      Arrays.asList(POINT_GROUPS.split(Constants.DELIMETER_LIST));
  public static final String SEARCHES_NEEDED_PC = "Microsoft Edge bonus;PC search";
  public static final List<String> SEARCHES_NEEDED_PC_LIST =
      Arrays.asList(SEARCHES_NEEDED_PC.split(Constants.DELIMETER_LIST));
  public static final int HIGHLIGHT_BRIEFLY = 10;
  private static int searchesMin = 34;
  public static final int SEARCH = 1;
  private Selenium selenium = new Selenium(getWebDriver());
  private static final By byButtonSignInWithMicrosoft =
      By.xpath(".//span[.='SIGN IN WITH MICROSOFT']");
  private static final By byPointsDailySet =
      By.xpath(".//*[@id='daily-sets']/mee-card-group[1]/div/mee-card");
  private static final By byPointsMoreActivities =
      By.xpath(".//*[@id='more-activities']/div/mee-card");
  private static final By byPointsAvailableSneakPeek =
      By.xpath(".//*[@id='daily-sets']/mee-card-group[2]/div/mee-card");
  public static final String XPAPTH_USER_BANNER = ".//*[@id='userBanner']";

  public static int getSearchesMin() {
    return searchesMin;
  }

  protected Selenium getSelenium() {
    return selenium;
  }

  private By getByButtonSignInWithMicrosoft() {
    return byButtonSignInWithMicrosoft;
  }

  private By getByPointsDailySet() {
    return byPointsDailySet;
  }

  private By getByPointsMoreActivities() {
    return byPointsMoreActivities;
  }

  private By getByPointsAvailableSneakPeek() {
    return byPointsAvailableSneakPeek;
  }

  private static final MicrosoftReport microsoftReport = new MicrosoftReport();
  private boolean searchesRequired = true;
  private int searchesNeeded = 0;
  private int searchAttempts = 0;

  /**
   * @param webDriver
   */
  public RewardsPage(WebDriver webDriver) {
    super(webDriver);
  }

  public boolean isSearchesRequired() {
    return searchesRequired;
  }

  public void setSearchesRequired(boolean searchesRequired) {
    this.searchesRequired = searchesRequired;
  }

  public int getSearchAttempts() {
    return searchAttempts;
  }

  public void setSearchAttempts(int searchAttempts) {
    this.searchAttempts = searchAttempts;
  }

  public int getSearchesNeeded() {
    return this.searchesNeeded;
  }

  public void setSearchesNeeded(int searchesNeeded) {
    this.searchesNeeded = searchesNeeded;
  }

  private void clickPointsBreakdownViewButton() throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    // getWebDriver().get(URL_POINTS_BREAKDOWN);
    // refresh();
    final By byPointsBreakdown =
        By.xpath(XPAPTH_USER_BANNER + "//span/ng-transclude[.='Points breakdown']/..");
    objectExistsRefresh(byPointsBreakdown, 3, getTimeoutElement());
    final WebElement webElementPointsBreakdown = getWebDriver().findElement(byPointsBreakdown);
    scrollToElement(webElementPointsBreakdown);
    highlightCurrentElementBriefly(webElementPointsBreakdown, HIGHLIGHT_BRIEFLY);
    clickObject(webElementPointsBreakdown);
    sleep(5);
  }

  private void clickPointsBreakdownXButton() throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    clickObject(By.xpath(".//*[@id='modal-host']//button"));
  }

  public void clickSignInWithMicrosoftButton() throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    if (objectExists(getByButtonSignInWithMicrosoft())) {
      clickObject(getByButtonSignInWithMicrosoft());
    }
  }

  public void createReport() throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    String htmlMicrosoftReport =
        XML.HEADING_INFO
            + "<xsl:stylesheet version="
            + Constants.QUOTE_DOUBLE
            + "1.0"
            + Constants.QUOTE_DOUBLE
            + "><html>"
            + getMicrosoftReport().printReports()
            + "</html>";
    htmlMicrosoftReport =
        htmlMicrosoftReport.replace(
            "</html>",
            "<h4>Report Genereated at "
                + DateHelpersTests.getCurrentDateTime(DateHelpersTests.FORMAT_US_STANDARD_DATE_TIME)
                + "</h4></html>");
    FSOTests.fileWrite(FILE_REPORT, htmlMicrosoftReport, false);
  }

  /**
   * @param group
   */
  private void getDashboard10PointsMonthRewardLevel(String group) throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    String xPath = XPAPTH_USER_BANNER + "//persona-body/p";
    final List<WebElement> webElementList = getWebDriver().findElements(By.xpath(xPath));
    final WebElement rewardLevelWebElement = webElementList.get(0);
    highlightCurrentElementBriefly(rewardLevelWebElement, HIGHLIGHT_BRIEFLY);
    RewardsInfo.getRewardsInfoList()
        .add(new RewardsInfo(group, xPath, "Reward Level", rewardLevelWebElement.getText()));
    final WebElement pointsEarnedWebElement = webElementList.get(1);
    highlightCurrentElementBriefly(pointsEarnedWebElement, HIGHLIGHT_BRIEFLY);
    RewardsInfo.getRewardsInfoList()
        .add(
            new RewardsInfo(
                group, xPath, "Points Required (Message)", pointsEarnedWebElement.getText()));
    // final String[] pointsArray = pointsEarnedWebElement.getText().split("
    // ");
    // RewardsInfo.getRewardsInfoList()
    // .add(new RewardsInfo(group, xPath, "Points Required (Month)",
    // removeCommas(pointsArray[2])));
    final List<WebElement> webElementChildren =
        pointsEarnedWebElement.findElements(By.xpath(".//b"));
    final String[] pointsArray = webElementChildren.get(0).getText().split(" ");
    xPath += "//b";
    RewardsInfo.getRewardsInfoList()
        .add(
            new RewardsInfo(group, xPath, "Points Required (Month)", removeCommas(pointsArray[0])));
  }

  /**
   * @param group
   */
  private void getDashboard20PointsAvailable(String group) throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    final String xPath = XPAPTH_USER_BANNER + "//div/p[.='Available points']/..//span";
    final List<WebElement> webElementList = getWebDriver().findElements(By.xpath(xPath));
    final WebElement availablePointsWebElement = webElementList.get(0);
    highlightCurrentElementBriefly(availablePointsWebElement, HIGHLIGHT_BRIEFLY);
    RewardsInfo.getRewardsInfoList()
        .add(
            new RewardsInfo(
                group,
                xPath,
                "Available Points",
                removeCommas(availablePointsWebElement.getText())));
    final WebElement availablePointsLifetimeWebElement = webElementList.get(1);
    RewardsInfo.getRewardsInfoList()
        .add(
            new RewardsInfo(
                group,
                xPath,
                "Lifetime Points (Message)",
                availablePointsLifetimeWebElement.getText()));
    RewardsInfo.getRewardsInfoList()
        .add(
            new RewardsInfo(
                group,
                xPath,
                "Lifetime Points",
                removeCommas(
                    availablePointsLifetimeWebElement.getText().replace(" lifetime points", ""))));
  }

  /**
   * @param group
   */
  private void getDashboard30PointsStreak(String group) throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    final String xPath = XPAPTH_USER_BANNER + "//div/p[.='Streak count']/..//span";
    final List<WebElement> webElementList = getWebDriver().findElements(By.xpath(xPath));
    final WebElement streakDaysWebElement = webElementList.get(0);
    highlightCurrentElementBriefly(streakDaysWebElement, HIGHLIGHT_BRIEFLY);
    RewardsInfo.getRewardsInfoList()
        .add(
            new RewardsInfo(
                group, xPath, "Streak Days", removeCommas(streakDaysWebElement.getText())));
    final WebElement streakBonusWebElement = webElementList.get(1);
    highlightCurrentElementBriefly(streakBonusWebElement, HIGHLIGHT_BRIEFLY);
    RewardsInfo.getRewardsInfoList()
        .add(
            new RewardsInfo(
                group, xPath, "Streak Bonus (Message)", streakBonusWebElement.getText()));
  }

  /**
   * @param group
   */
  private void getDashboard40PointsEarned(String group) throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    final String xPath = XPAPTH_USER_BANNER + "//div/p[.='Points earned']/..//span";
    final List<WebElement> webElementList = getWebDriver().findElements(By.xpath(xPath));
    final WebElement webElementPoints = webElementList.get(0);
    highlightCurrentElementBriefly(webElementPoints, HIGHLIGHT_BRIEFLY);
    final String[] pointsArray = webElementPoints.getText().split(" / ");
    RewardsInfo.getRewardsInfoList()
        .add(new RewardsInfo(group, xPath, "Today (Points Earned)", removeCommas(pointsArray[0])));
    RewardsInfo.getRewardsInfoList()
        .add(
            new RewardsInfo(
                group, xPath, "Today (Points Available)", removeCommas(pointsArray[1])));
  }

  /**
   * @param group
   */
  private void getDashboard50PointsCurrentStreak(String group) throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    WebElement webElementCurrentStreak = null;
    final String xPath = ".//mee-rewards-streak//div[2]/mee-rich-paragraph/p";
    webElementCurrentStreak = getWebDriver().findElement(By.xpath(xPath));
    highlightCurrentElementBriefly(webElementCurrentStreak, HIGHLIGHT_BRIEFLY);
    RewardsInfo.getRewardsInfoList()
        .add(
            new RewardsInfo(
                group, xPath, "Current Streak (Message)", webElementCurrentStreak.getText()));
  }

  private void getDashboardInfo() throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    final String method = getCurrentMethodName();
    sleep(5);
    scrollToTop();
    getDashboard10PointsMonthRewardLevel(method);
    getDashboard20PointsAvailable(method);
    getDashboard30PointsStreak(method);
    getDashboard40PointsEarned(method);
    getDashboard50PointsCurrentStreak(method);
  }

  public MicrosoftReport getMicrosoftReport() {
    return microsoftReport;
  }

  private void getPointBreakdownInfo10() throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    scrollToTop();
    // TODO: Microsoft Rewards Points Breakdown
    final String method = getCurrentMethodName();
    clickPointsBreakdownViewButton();
    final PointsBreakdown pointsBreakdown = new PointsBreakdown();
    pointsBreakdown.setMethod(method);
    pointsBreakdown.setIndex(PointsBreakdown.getPointsBreakdownList().size());
    pointsBreakdown.setGroup("Points Level 2 (Required)");
    String xPathCenter = ".//*[@id='userPointsBreakdown']//mee-rewards-counter-animation//span";
    final By byLevel = By.xpath(xPathCenter);
    final WebElement webElementMessage = getWebDriver().findElement(byLevel);
    // Message
    scrollToElement(webElementMessage);
    highlightCurrentElementBriefly(webElementMessage, HIGHLIGHT_BRIEFLY);
    final String xPathMessage =
        ISelenium.getWebElementXPath(Arrays.asList(1), webElementMessage, "xpath");
    pointsBreakdown.setxPath(xPathMessage);
    pointsBreakdown.setDescription(webElementMessage.getText());
    final String pointsEarned = webElementMessage.findElement(By.xpath("./b[1]")).getText();
    pointsBreakdown.setPointsEarned(Integer.valueOf(pointsEarned));
    pointsBreakdown.setPointsAvailable(500);
    PointsBreakdown.getPointsBreakdownList().add(pointsBreakdown);
  }

  private void getPointBreakdownInfo20() throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    final String method = getCurrentMethodName();
    PointsBreakdown pointsBreakdown = new PointsBreakdown();
    for (final String heading : POINT_GROUPS_LIST) {
      pointsBreakdown = new PointsBreakdown();
      pointsBreakdown.setMethod(method);
      pointsBreakdown.setIndex(PointsBreakdown.getPointsBreakdownList().size());
      pointsBreakdown.setGroup(heading);
      // .//mee-rewards-user-points-details//div[@class='title-detail']/p/a[contains(text(),'Microsoft
      // Edge bonus')]
      // .//a[contains(text(),'Microsoft Edge bonus')]
      final String xPathLevel = ".//a[contains(text(),'" + heading + "')]";
      By byHeadingName = By.xpath(xPathLevel);
      boolean objectHeadingExists = objectExists(byHeadingName, 1);
      if (objectHeadingExists) {
        final WebElement parentWebElement = getWebDriver().findElement(byHeadingName);
        highlightCurrentElementBriefly(parentWebElement, HIGHLIGHT_BRIEFLY);
        By byHeadingPointsEarned = By.xpath("./../../p[2]/b");
        WebElement childWebElement = parentWebElement.findElement(byHeadingPointsEarned);
        highlightCurrentElementBriefly(childWebElement, HIGHLIGHT_BRIEFLY);
        pointsBreakdown.setxPath(childWebElement.toString());
        pointsBreakdown.setxPath(xPathLevel);
        String value = childWebElement.getText();
        pointsBreakdown.setPointsEarned(Integer.valueOf(value));
        By byHeadingPointsAvailable = By.xpath("./../../p[2]");
        childWebElement = parentWebElement.findElement(byHeadingPointsAvailable);
        highlightCurrentElementBriefly(childWebElement, HIGHLIGHT_BRIEFLY);
        pointsBreakdown.setxPath(childWebElement.toString());
        pointsBreakdown.setxPath(xPathLevel);
        value = childWebElement.getText();
        value = value.replace(" / ", "");
        final String shopAndEarn = POINT_GROUPS_LIST.get(3);
        if (!heading.equals(shopAndEarn)) {
          value = removeCommas(value);
          pointsBreakdown.setPointsAvailable(Integer.valueOf(value));
        }
        By byHeadingDescription = By.xpath("./../../p[3]");
        childWebElement = parentWebElement.findElement(byHeadingDescription);
        highlightCurrentElementBriefly(childWebElement, HIGHLIGHT_BRIEFLY);
        pointsBreakdown.setDescription(childWebElement.getText());
        PointsBreakdown.getPointsBreakdownList().add(pointsBreakdown);
      }
    }
    clickPointsBreakdownXButton();
  }

  public void getPoints10CardsAvailable() throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    PointsCard.setPointsCardList(new ArrayList<>());
    getPointsCardsAvailable10DailySet();
    getPointsCardsAvailable20MoreActivities();
    getPointsCardsAvailable30SneakPeek();
    scrollToTop();
  }

  public boolean getPoints20Info() throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    LOG.debug("Loading: [{}]", MicrosoftEnvironment.URL_LOGIN);
    getWebDriver().get(MicrosoftEnvironment.URL_LOGIN);
    RewardsInfo.setRewardsInfoList(new ArrayList<>());
    PointsBreakdown.setPointsBreakdownList(new ArrayList<>());
    int searchAttempts = getSearchAttempts();
    searchAttempts++;
    setSearchAttempts(searchAttempts);
    getPointBreakdownInfo10();
    getPointBreakdownInfo20();
    setSearchesRequired(isSearchesNeeded());
    if (!isSearchesRequired()) {
      getDashboardInfo();
    }
    return isSearchesRequired();
  }

  /**
   * @param parentWebElement
   * @return
   */
  private boolean getPointsCard10Collected(WebElement parentWebElement) throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    final WebElement childWebElement = parentWebElement.findElement(By.xpath(".//span[1]"));
    highlightCurrentElementBriefly(childWebElement, HIGHLIGHT_BRIEFLY);
    return childWebElement.getAttribute("class").contains("mee-icon-SkypeCircleCheck");
  }

  /**
   * @param parentWebElement
   * @return
   */
  private int getPointsCard20Top(WebElement parentWebElement) throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    int cardPointsTop = -1;
    try {
      final WebElement childWebElement = parentWebElement.findElement(By.xpath(".//span[2]"));
      highlightCurrentElementBriefly(childWebElement, HIGHLIGHT_BRIEFLY);
      cardPointsTop = Integer.valueOf(childWebElement.getText().trim());
    } catch (final Exception e) {
      LOG.debug("No points on top of card.");
    }
    return cardPointsTop;
  }

  /**
   * @param parentWebElement
   * @return
   */
  private String getPointsCard30Group(WebElement parentWebElement) throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    final WebElement childWebElement = parentWebElement.findElement(By.xpath(".//div[2]/h3"));
    highlightCurrentElementBriefly(childWebElement, HIGHLIGHT_BRIEFLY);
    return childWebElement.getText().trim();
  }

  /**
   * @param parentWebElement
   * @return
   */
  private String getPointsCard40Description(WebElement parentWebElement) throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    final WebElement childWebElement = parentWebElement.findElement(By.xpath(".//div[2]/p"));
    highlightCurrentElementBriefly(childWebElement, HIGHLIGHT_BRIEFLY);
    return childWebElement.getText().trim();
  }

  /**
   * @param parentWebElement
   * @return
   */
  private String getPointsCard50Bottom(WebElement parentWebElement) throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    final WebElement childWebElement = parentWebElement.findElement(By.xpath(".//div[3]"));
    highlightCurrentElementBriefly(childWebElement, HIGHLIGHT_BRIEFLY);
    return childWebElement.getText().trim();
  }

  private void getPointsCardsAvailable10DailySet() throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    scrollToTop();
    final String method = getCurrentMethodName();
    final By byPoints = getByPointsDailySet();
    final List<WebElement> cardList = getWebDriver().findElements(byPoints);
    final List<PointsCard> pointsCardList = new ArrayList<>();
    for (int cardIndex = 0; cardIndex < cardList.size(); cardIndex++) {
      final WebElement parentWebElement = cardList.get(cardIndex);
      final PointsCard pointsCard =
          getPointsCardsAvailableCard(method, parentWebElement, cardIndex, true);
      pointsCardList.add(pointsCard);
      PointsCard.getPointsCardList().add(pointsCard);
    }
    getPointsCardsAvailable40Uneared(pointsCardList);
  }

  private void getPointsCardsAvailable20MoreActivities() throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    scrollToTop();
    final String method = getCurrentMethodName();
    final By byPoints = getByPointsMoreActivities();
    final List<WebElement> cardList = getWebDriver().findElements(byPoints);
    final List<PointsCard> pointsCardList = new ArrayList<>();
    for (int cardIndex = 0; cardIndex < cardList.size(); cardIndex++) {
      final WebElement parentWebElement = cardList.get(cardIndex);
      final PointsCard pointsCard =
          getPointsCardsAvailableCard(method, parentWebElement, cardIndex, true);
      pointsCardList.add(pointsCard);
      PointsCard.getPointsCardList().add(pointsCard);
    }
    getPointsCardsAvailable40Uneared(pointsCardList);
  }

  private void getPointsCardsAvailable30SneakPeek() throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    scrollToTop();
    final String method = getCurrentMethodName();
    final By byPoints = getByPointsAvailableSneakPeek();
    final By bySneakPeekAtTomorrowsSet =
        By.xpath(
            ".//span[.="
                + Constants.QUOTE_DOUBLE
                + "SNEAK PEEK AT TOMORROW'S SET"
                + Constants.QUOTE_DOUBLE
                + "]");
    final WebElement webElementSneakPeekAtTomorrowsSet =
        getWebDriver().findElement(bySneakPeekAtTomorrowsSet);
    scrollToElement(webElementSneakPeekAtTomorrowsSet);
    clickObject(bySneakPeekAtTomorrowsSet);
    final List<WebElement> cardList = getWebDriver().findElements(byPoints);
    for (int cardIndex = 0; cardIndex < cardList.size(); cardIndex++) {
      final WebElement parentWebElement = cardList.get(cardIndex);
      PointsCard.getPointsCardList()
          .add(getPointsCardsAvailableCard(method, parentWebElement, cardIndex, false));
    }
    final By byGoBackToTodaysSet =
        By.xpath(
            ".//span[.="
                + Constants.QUOTE_DOUBLE
                + "GO BACK TO TODAY'S SET"
                + Constants.QUOTE_DOUBLE
                + "]");
    final WebElement webElementGoBackToTodaysSet = getWebDriver().findElement(byGoBackToTodaysSet);
    scrollToElement(webElementGoBackToTodaysSet);
    clickObject(byGoBackToTodaysSet);
  }

  /**
   * @param pointsCardList
   * @throws QAException
   */
  private void getPointsCardsAvailable40Uneared(List<PointsCard> pointsCardList)
      throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    for (PointsCard pointsCard : pointsCardList) {
      if (!pointsCard.isCollected()) {
        // if (pointsCard.isCollected())
        final int pointsAvailable = pointsCard.getPointsTop();
        switch (pointsAvailable) {
          // Ignore the points message for now, but code to collect
          // the
          // message maybe later.
          case -1:
            break;
          case 5:
            getPointsCardsCollected05(pointsCard);
            break;
          case 10:
            getPointsCardsCollected10(pointsCard);
            break;
          default:
            getPointsCardsCollectedFalse(pointsCard);
            break;
        }
      } else {
        getPointsCardsCollectedTrue(pointsCard);
      }
    }
  }

  /**
   * @param method
   * @param cardIndex
   * @param xPath
   * @param parentWebElement
   * @param collectable
   * @return
   * @throws QAException
   */
  private PointsCard getPointsCardsAvailableCard(
      String method, WebElement parentWebElement, int cardIndex, boolean collectable)
      throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    scrollToElement(parentWebElement);
    highlightCurrentElementBriefly(parentWebElement, HIGHLIGHT_BRIEFLY);
    final PointsCard pointsCard = new PointsCard();
    pointsCard.setMethod(method);
    pointsCard.setIndex(cardIndex);
    pointsCard.setCollectable(collectable);
    if (collectable) {
      pointsCard.setCollected(getPointsCard10Collected(parentWebElement));
      pointsCard.setPointsTop(getPointsCard20Top(parentWebElement));
    }
    pointsCard.setGroup(getPointsCard30Group(parentWebElement));
    pointsCard.setDescription(getPointsCard40Description(parentWebElement));
    pointsCard.setPointsBottom(getPointsCard50Bottom(parentWebElement));
    if (!collectable) {
      final String[] array = pointsCard.getPointsBottom().split(" ");
      pointsCard.setPointsTop(Integer.valueOf(array[0]));
    }
    // Need to set the xPath to the pointsBottom due to update.
    final WebElement childWebElement = parentWebElement.findElement(By.xpath(".//div[3]"));
    String xPath = ISelenium.getAbsoluteXPath(getWebDriver(), childWebElement);
    LOG.debug("Absolute XPath: {}", xPath);
    xPath = ISelenium.getWebElementXPath(Arrays.asList(cardIndex + 1, 0), childWebElement, "xpath");
    highlightCurrentElementBriefly(getWebDriver().findElement(By.xpath(xPath)), HIGHLIGHT_BRIEFLY);
    pointsCard.setxPath(xPath);
    pointsCard.setWebElement(parentWebElement);
    // LOG.debug("{}", webElementParent.getText());
    LOG.debug("pointsCard: {}", pointsCard.toString());
    sleep(0, 250);
    return pointsCard;
  }

  /**
   * @param pointsCard
   * @throws QAException
   */
  private void getPointsCardsCollected05(PointsCard pointsCard) throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    final String method = getCurrentMethodName();
    final String message =
        pointsCard.getMethod() + Constants.NEWLINE + method + ":" + pointsCard.toMessage();
    LOG.debug("Message: {}", message);
    getPointsCardsCollectedAI(pointsCard);
  }

  /**
   * @param pointsCard
   * @throws QAException
   */
  private void getPointsCardsCollected10(PointsCard pointsCard) throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    final String method = getCurrentMethodName();
    final String message =
        pointsCard.getMethod() + Constants.NEWLINE + method + ":" + pointsCard.toMessage();
    LOG.debug("Message: {}", message);
    getPointsCardsCollectedAI(pointsCard);
  }

  /**
   * @param group
   * @throws QAException
   */
  private void getPointsCardsCollectedAI(PointsCard pointsCard) throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    scrollToTop();
    final String method = getCurrentMethodName();
    final String message =
        pointsCard.getMethod() + Constants.NEWLINE + method + ":" + pointsCard.toMessage();
    LOG.debug("Message: {}", message);
    final String xPath = pointsCard.getxPath();
    LOG.debug("xPath: {}", xPath);
    final WebElement parentWebElement = getWebDriver().findElement(By.xpath(xPath));
    scrollToElement(parentWebElement);
    highlightCurrentElementBriefly(parentWebElement, HIGHLIGHT_BRIEFLY);
    clickObject(parentWebElement);
    sleep(5);
    switchToWindowByIndex(1);
    final DailyPollQuizPages dailyPollQuizPages = new DailyPollQuizPages(getWebDriver());
    dailyPollQuizPages.getDailyPollQuizPoints(pointsCard);
    tabCloseExtras();
  }

  /**
   * @param pointsCard
   * @throws QAException
   */
  private void getPointsCardsCollectedFalse(PointsCard pointsCard) throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    final String method = getCurrentMethodName();
    final String message =
        pointsCard.getMethod() + Constants.NEWLINE + method + ":" + pointsCard.toMessage();
    LOG.debug("Message: {}", message);
    // TODO: Incorporate Quizes
    getPointsCardsCollectedAI(pointsCard);
  }

  /**
   * @param pointsCard
   */
  private void getPointsCardsCollectedTrue(PointsCard pointsCard) throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    scrollToTop();
    final String method = getCurrentMethodName();
    final String message =
        pointsCard.getMethod() + Constants.NEWLINE + method + ":" + pointsCard.toMessage();
    LOG.debug("Message: {}", message);
  }

  private boolean isSearchesNeeded() throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    final String method = getCurrentMethodName();
    int pointsEarned = 0;
    int pointsAvailable = 0;
    for (final PointsBreakdown pointsCard : PointsBreakdown.getPointsBreakdownList()) {
      if (SEARCHES_NEEDED_PC_LIST.contains(pointsCard.getGroup())) {
        pointsEarned += pointsCard.getPointsEarned();
        pointsAvailable += pointsCard.getPointsAvailable();
      }
    }
    final int pointsNeeded = pointsAvailable - pointsEarned;
    setSearchesNeeded(pointsNeeded / POINTS_PER_SEACH_PC);
    // final int searchesNeeded = (pointsNeeded / POINTS_PER_SEACH_PC);
    RewardsInfo.getRewardsInfoList()
        .add(
            new RewardsInfo(
                method,
                method + " (pointsAvailable)",
                "Points Available",
                String.valueOf(pointsAvailable)));
    RewardsInfo.getRewardsInfoList()
        .add(
            new RewardsInfo(
                method, method + " (pointsEarned)", "Points Earned", String.valueOf(pointsEarned)));
    RewardsInfo.getRewardsInfoList()
        .add(
            new RewardsInfo(
                method, method + " (pointsNeeded)", "Points Needed", String.valueOf(pointsNeeded)));
    RewardsInfo.getRewardsInfoList()
        .add(
            new RewardsInfo(
                method,
                method + " (searchesNeeded)",
                "Searches Needed",
                String.valueOf(getSearchesNeeded())));
    searchesMin = getSearchesNeeded();
    setSearchesRequired(getSearchesNeeded() > 0 && getSearchAttempts() < 2);
    return isSearchesRequired();
  }

  public boolean load() throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    maximizeWindow();
    getWebDriver().get(MicrosoftEnvironment.URL_LOGIN);
    refresh();
    return objectExists(byButtonSignInWithMicrosoft, 10);
  }

  /**
   * @param value
   * @return
   */
  private String removeCommas(String value) throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    return value.replaceAll(",", "").trim();
  }

  public void sendReport() throws QAException {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    final String emailTO = CJSConstants.EMAIL_ADDRESS_GMAIL;
    final String from = CJSConstants.EMAIL_ADDRESS_MSN;
    final String password = EPasswords.EMAIL_MSN.getValue();
    final String date = new Date().toString();
    final String subject = "Microsoft - Automation (Daily Report) " + date;
    final String embeddedReport = FSOTests.fileReadAll(FILE_REPORT);
    String body = embeddedReport; // + Constants.NEWLINE;
    body += FSOTests.fileReadAll(FILE_SIGNATURE);
    Email.sendEmail(from, password, emailTO, emailTO, emailTO, subject, body, FILE_REPORT);
  }
}
