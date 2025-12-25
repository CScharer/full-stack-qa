package com.cjs.qa.microsoft.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cjs.qa.core.QAException;
import com.cjs.qa.jdbc.JDBC;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;

public class BingPage extends Page {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(BingPage.class));

  /**
   * @param webDriver
   */
  public BingPage(WebDriver webDriver) {
    super(webDriver);
  }

  public static final int SEARCH_WAIT_TIME = 1;
  // public static int SEARCHES_MIN = 34;
  private static By bySearch = By.xpath(".//*[@id='sb_form_go']");
  private static By bycurrentPoints = By.xpath(".//*[@id='id_rc']");
  private static int currentPoints = -1;
  private static int searchesMade = 0;
  private static List<String> wordsList = null;

  public static By getBySearch() {
    return bySearch;
  }

  public static By getBycurrentPoints() {
    return bycurrentPoints;
  }

  public static int getCurrentPoints() {
    return currentPoints;
  }

  public static void setCurrentPoints(int points) {
    currentPoints = points;
  }

  public static int getSearchesMade() {
    return searchesMade;
  }

  public static void setSearchesMade(int searchesMade) {
    BingPage.searchesMade = searchesMade;
  }

  public static List<String> getWordsList() {
    return wordsList;
  }

  public static void setWordsList(List<String> wordsList) {
    LOG.debug("{} {}", Constants.CLASS_METHOD_DEBUG, getCurrentClassMethodDebugName());
    List<String> wordsListNew = new ArrayList<>();
    for (String word : wordsList) {
      word = word.replaceAll(Constants.SYMBOL_TRADEMARK, "");
      wordsListNew.add(word);
    }
    BingPage.wordsList = wordsListNew;
  }

  /**
   * @param browseForAnHour
   */
  public void searchRandomSites(boolean browseForAnHour) throws QAException {
    LOG.debug("{} {}", Constants.CLASS_METHOD_DEBUG, getCurrentClassMethodDebugName());
    final boolean selectLink = false;
    final double processTimeHours = 0.05;
    final double processTimeMilliseconds = processTimeHours * 60 * 60 * 1000;
    final int wordLimit = 100;
    boolean searchRequired = false;
    int currentPoints = 0;
    int search = 1;
    String searchValue = "";
    search = search * -1;
    if (search == 1) {
      searchValue = "http://www.bing" + IExtension.COM + "/search?q=search+";
    } else {
      searchValue = "http://www.bing" + IExtension.COM + "/search?q=";
    }
    String url;
    if (browseForAnHour) {
      final JDBC jdbc = new JDBC("", "QAAuto");
      final List<String> badHREFList =
          Arrays.asList(
              "search?;javascript:void(0);=;javascript:void(0);#))]"
                  .split(Constants.DELIMETER_LIST));
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("SELECT * ");
      stringBuilder.append("FROM [t_WordDictionary] [w] ");
      stringBuilder.append("WHERE LENGTH([w].[Word])>=4 ");
      stringBuilder.append("AND [w].[Word] NOT IN(");
      stringBuilder.append("SELECT * ");
      stringBuilder.append("FROM [t_WordsUsed]) ");
      stringBuilder.append("ORDER BY RANDOM() LIMIT " + wordLimit);
      setWordsList(jdbc.queryResultsList(stringBuilder.toString(), false));
      final long startTime = System.currentTimeMillis();
      long elapsedTimeMilliseconds = 0;
      do {
        url = searchValue + String.valueOf(search);
        if (getWordsList().size() >= search) {
          String word = getWordsList().get(search - 1).replaceAll(Constants.SYMBOL_TRADEMARK, "");
          if (!word.isEmpty()) {
            url = searchValue + word;
          }
        }
        search(url);
        search++;
        setSearchesMade(search);
        currentPoints = getCurrentPointsValue(search, currentPoints);
        if (selectLink) {
          clickFirstLink(".//*[@id='b_results']//a[not(contains(@href,'search?'))]", badHREFList);
        }
        elapsedTimeMilliseconds = System.currentTimeMillis() - startTime;
        final double percentCompleteNumber = elapsedTimeMilliseconds / processTimeMilliseconds;
        final String percentCompleteString = formatNumber(percentCompleteNumber, "##0.00%");
        LOG.debug(
            "percentCompleteString: [{}], elapsedTimeMilliseconds: [{}]",
            percentCompleteString,
            elapsedTimeMilliseconds);
        searchRequired =
            (getCurrentPoints() != currentPoints && search < RewardsPage.getSearchesMin())
                || elapsedTimeMilliseconds <= processTimeMilliseconds;
      } while (searchRequired);
    } else {
      do {
        search++;
        url = searchValue + String.valueOf(search);
        search(url);
        currentPoints = getCurrentPointsValue(search, currentPoints);
        searchRequired =
            getCurrentPoints() != currentPoints || search < RewardsPage.getSearchesMin();
      } while (searchRequired);
    }
  }

  private int getCurrentPointsValue(int search, int currentPoints) {
    LOG.debug("{} {}", Constants.CLASS_METHOD_DEBUG, getCurrentClassMethodDebugName());
    if (getCurrentPoints() != currentPoints) {
      setCurrentPoints(currentPoints);
    }
    objectExistsRefresh(getBySearch(), 5, 3);
    WebElement webElement = getWebDriver().findElement(getBySearch());
    hoverObject(getBySearch());
    String text = "Rewards";
    if (objectExists(getBycurrentPoints(), 1)) {
      webElement = getWebDriver().findElement(getBycurrentPoints());
      hoverObject(getBycurrentPoints());
      text = webElement.getText();
    }
    if (text != null) {
      if (!"Rewards".equals(text)) {
        currentPoints = Integer.valueOf(text);
      } else {
        currentPoints += RewardsPage.POINTS_PER_SEACH_PC;
        LOG.warn("Rewards Points Not Showing");
        if (search >= RewardsPage.getSearchesMin()) {
          setCurrentPoints(currentPoints);
        }
      }
    }
    LOG.debug(
        "search: [{}], BingPage.currentPoints: [{}], currentPoints: [{}]",
        search,
        getCurrentPoints(),
        currentPoints);
    return currentPoints;
  }

  /**
   * @param xpathLinks
   * @param badHREFList
   */
  public void clickFirstLink(String xpathLinks, List<String> badHREFList) throws QAException {
    LOG.debug("{} {}", Constants.CLASS_METHOD_DEBUG, getCurrentClassMethodDebugName());
    final By links = By.xpath(xpathLinks);
    final List<WebElement> searchLinks = getWebDriver().findElements(links);
    if (searchLinks.isEmpty()) {
      return;
    }
    WebElement webElement = null;
    String href = null;
    for (final WebElement element : searchLinks) {
      href = element.getAttribute("href");
      boolean hrefBad = false;
      for (final String badHREF : badHREFList) {
        if (href.toLowerCase(Locale.ENGLISH).contains(badHREF.toLowerCase(Locale.ENGLISH))) {
          hrefBad = true;
          break;
        }
      }
      if (!hrefBad) {
        webElement = element;
        break;
      }
    }
    if (webElement != null) {
      LOG.debug("webElement: [{}], href: [{}]", webElement.getAttribute("tagName"), href);
      try {
        clickObject(webElement);
      } catch (final Exception e) {
        LOG.error("Error clicking element", e);
      }
    }
    search(href);
  }

  /**
   * @param url
   */
  private void search(String url) throws QAException {
    LOG.debug("{} {}", Constants.CLASS_METHOD_DEBUG, getCurrentClassMethodDebugName());
    LOG.debug("Searching: [{}]", url);
    getWebDriver().get(url);
    sleep(SEARCH_WAIT_TIME);
    // By audioSound = By.xpath(".//audio[@data-dobid='aud']/../input")
    // By audioSound = By.xpath("*//span[@jsaction='dob.p']")
    By audioSound = By.xpath("*//div[@data-type='audioplay']");
    if (objectExists(audioSound, SEARCH_WAIT_TIME)) {
      clickObject(audioSound);
      sleep(Long.valueOf(SEARCH_WAIT_TIME * 3));
      scrollToTop();
    }
  }

  public void searchVivitSites() throws QAException {
    LOG.debug("{} {}", Constants.CLASS_METHOD_DEBUG, getCurrentClassMethodDebugName());
    final String urlVivit = "http://www.vivit-worldwide.org";
    final String urlVivitYm = "https://vivitworldwide.site-ym";
    final String lugs = urlVivit + "/?page=LocalUserGroups";
    final String sigs = urlVivit + "/?page=SIGS";
    final By lugLinks = By.xpath(".//*[@id='CustomPageBody']//a");
    final By sigLinks = By.xpath(".//*[@id='CustomPageBody']//a");
    maximizeWindow();
    final List<String> urls =
        Arrays.asList(
            urlVivitYm + IExtension.COM + "/",
            urlVivitYm + IExtension.COM + "/?page=board",
            urlVivitYm + IExtension.COM + "/staff/",
            urlVivitYm + IExtension.COM + "/?page=Volunteer",
            "http://c.ymcdn"
                + IExtension.COM
                + "/sites/www.vivit-worldwide.org/resource/resmgr/docs/2017_VivitMemberBrochure"
                + IExtension.PDF,
            urlVivitYm + IExtension.COM + "/?page=HallofFame");
    for (final String url : urls) {
      LOG.debug("Searching: [{}]", url);
      getWebDriver().get(url);
      sleep(SEARCH_WAIT_TIME);
    }
    getWebDriver().get(lugs);
    sleep(5);
    List<WebElement> links = getWebDriver().findElements(lugLinks);
    for (int index = 0; index < links.size(); index++) {
      final WebElement element = links.get(index);
      final String url = element.getAttribute("href");
      LOG.debug("Clicking: [{}]", url);
      clickObject(element);
      sleep(SEARCH_WAIT_TIME);
      tabCloseExtras();
      if (index < links.size()) {
        getWebDriver().get(lugs);
        sleep(5);
        links = getWebDriver().findElements(lugLinks);
      }
    }
    getWebDriver().get(sigs);
    sleep(5);
    links = getWebDriver().findElements(sigLinks);
    for (int index = 0; index < links.size(); index++) {
      final WebElement element = links.get(index);
      final String url = element.getAttribute("href");
      LOG.debug("Clicking: [{}]", url);
      clickObject(element);
      sleep(SEARCH_WAIT_TIME);
      tabCloseExtras();
      if (index < links.size()) {
        getWebDriver().get(sigs);
        sleep(5);
        getWebDriver().findElements(sigLinks);
      }
    }
    // http://www.vivit-worldwide.org/?page=LocalUserGroups
    // minimizeWindow()
  }
}
