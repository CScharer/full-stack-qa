package com.cjs.qa.vivit.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cjs.qa.core.Environment;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.vivit.VivitDataTests;
import com.cjs.qa.vivit.VivitEnvironment;
import com.cjs.qa.vivit.objects.GroupLeaderLink;
import com.cjs.qa.vivit.objects.Groups;
import com.cjs.qa.vivit.objects.Leaders;
import com.google.common.base.Stopwatch;

public class GroupPage extends Page {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(GroupPage.class));

  public GroupPage(WebDriver webDriver) {
    super(webDriver);
  }

  private final List<String> listGroupLeaders = new ArrayList<>();
  private static final By byTitleBar = By.xpath(".//*[@id='SpTitleBar']");

  public List<String> getListGroupLeaders() {
    return listGroupLeaders;
  }

  private static final By byLinksGroups =
      By.xpath("//*[@id='CustomPageBody']//a[text()!='contact us today!']");

  public void getGroupPageData() throws Throwable {
    LOG.debug("***ClassMethodDebug***:[{}]", getCurrentClassMethodDebugName());
    String statusName = "getGroupPageData";
    if (VivitDataTests.successFileExists(statusName)) {
      return;
    }
    Groups.setGroupPage(true);
    List<String> groupTypeList = Arrays.asList("LUGS", "SIGS");
    for (String groupType : groupTypeList) {
      LOG.info("Retrieving Data: [{}], [{}]", getCurrentClassMethodName(), groupType);
      getGroupData(groupType);
    }
    final String tables = "Groups;Leaders;GroupLeaderLink";
    String[] tableArray = tables.split(Constants.DELIMETER_LIST);
    for (String table : tableArray) {
      List<StringBuilder> sqlStringBuilderList = new ArrayList<>();
      sqlStringBuilderList.add(new StringBuilder(table));
      switch (table) {
        case "Groups":
          sqlStringBuilderList.add(Groups.appendRecords());
          break;
        case "Leaders":
          sqlStringBuilderList.add(Leaders.appendRecords());
          break;
        case "GroupLeaderLink":
          sqlStringBuilderList.add(GroupLeaderLink.appendRecords());
          break;
        default:
          break;
      }
      VivitDataTests.updateTableFromCurrentToPreviousAndInsert(sqlStringBuilderList);
    }
    Groups.setGroupPage(false);
    VivitDataTests.successFileCreate(statusName);
  }

  private void getGroupData(String group) throws Throwable {
    LOG.debug("***ClassMethodDebug***: [{}]", getCurrentClassMethodDebugName());
    final String symbolId = "id=";
    try {
      final By groupTitle = By.xpath(".//*[@id='ctl00_PageContent_lblPageSummaryTitle']");
      String groupValue = "";
      final String groupType = group.toUpperCase(Locale.ENGLISH);
      String expectedTitle = "";
      switch (groupType) {
        case "LUGS":
          groupValue = VivitEnvironment.URL_LOGIN + "?page=LocalUserGroups";
          expectedTitle = "Vivit Local User Groups";
          break;
        case "SIGS":
          groupValue = VivitEnvironment.URL_LOGIN + "?page=SIGS";
          expectedTitle = "Special Interest Groups";
          break;
        default:
          return;
      }
      getWebDriver().get(groupValue);
      final String title = getWebDriver().findElement(byTitleBar).getText();
      Assertions.assertEquals(expectedTitle, title, title);
      sleep(1, 0);
      final List<WebElement> links = getWebDriver().findElements(byLinksGroups);
      final List<String> chapterURLList = new ArrayList<>();
      for (WebElement element : links) {
        final String urlPage = element.getAttribute("href");
        chapterURLList.add(urlPage);
      }
      // Print formatted URL list.
      StringBuilder stringBuilder = new StringBuilder();
      for (int indexChapterURL = 0; indexChapterURL < chapterURLList.size(); indexChapterURL++) {
        final String chapterURL = chapterURLList.get(indexChapterURL);
        if (indexChapterURL < (chapterURLList.size() - 1)) {
          stringBuilder.append("\n");
        }
        stringBuilder.append(chapterURL);
      }
      LOG.debug("urls: [{}]", stringBuilder.toString());
      for (String chapterURL : chapterURLList) {
        final String pageID = chapterURL;
        LOG.debug("get: [{}]", chapterURL);
        final Stopwatch stopwatch = Stopwatch.createStarted();
        getWebDriver().get(chapterURL);
        stopwatch.stop();
        final long lElapsedTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        String elapsedTime = String.valueOf(lElapsedTime) + "ms";
        LOG.debug(
            "ElapsedTime: [{}], ElapsedTime: [{}]",
            elapsedTime,
            stopwatch.toString().replaceAll(" ", ""));
        stopwatch.reset();
        final List<WebElement> wallPostElements =
            getWebDriver().findElements(By.xpath("//div[@class='WallPost']"));
        String wallPosts = String.valueOf(wallPostElements.size());
        String groupName = "";
        // final String currentURL = getWebDriver().getCurrentUrl();
        // if(currentURL.equalsIgnoreCase(chapterURL))
        if (objectExists(groupTitle, Environment.getTimeOutElement())) {
          final WebElement webElementGroup = getWebDriver().findElement(groupTitle);
          highlightCurrentElement(webElementGroup);
          groupName = webElementGroup.getText();
          switch (groupType) {
            case "LUGS":
              groupName = groupName.replace("Local User Group", groupType);
              break;
            case "SIGS":
              groupName = groupName.replace("Special Interest Group", groupType);
              break;
            default:
              LOG.warn("Unknown group type: {}. Using original group name.", groupType);
              break;
          }
          final Map<String, By> mapByGroupURLsBy = getGroupURLs();
          final Map<String, String> mapGroupURLs = new HashMap<>();
          for (final Entry<String, By> entry : mapByGroupURLsBy.entrySet()) {
            final String keyBy = entry.getKey();
            final By byElement = mapByGroupURLsBy.get(keyBy);
            String objectURL = null;
            if (objectExists(byElement, 1)) {
              final WebElement webElement = getWebDriver().findElement(byElement);
              highlightCurrentElement(webElement);
              objectURL = webElement.getAttribute("href");
              // "https://www.vivit-worldwide.org" + objectURL;
            }
            mapGroupURLs.put(keyBy, objectURL);
          }
          Groups.getGroupsList()
              .add(new Groups(groupType, pageID, groupName, elapsedTime, wallPosts, chapterURL));
          Groups.getGroupsList().get(Groups.getGroupsList().size() - 1).updateURLs(mapGroupURLs);
        } else {
          // Broken Group URL.
          LOG.warn("Missing chapterURL: {}", chapterURL);
          // groupType = groupType;
          // pageID = pageID;
          groupName =
              groupType + ": " + pageID.substring(pageID.lastIndexOf("/") + 1, pageID.length());
          elapsedTime = "N/A";
          wallPosts = "N/A";
          // chapterURL = chapterURL;
          Groups.getGroupsList()
              .add(new Groups(groupType, pageID, groupName, elapsedTime, wallPosts, chapterURL));
        }
        LOG.debug("groupName: [{}]", groupName);
        // Get the labels/hrefs links of the Groups.
        final By hrefLinks = By.xpath(".//*[@id='ctl00_PageContent_tdZone2']/table/tbody/tr/td/b");
        final List<WebElement> webElements = getWebDriver().findElements(hrefLinks);
        for (WebElement webElement : webElements) {
          // Need to verify this so there's no unhandled error.
          highlightCurrentElement(webElement);
          final String leaderType = webElement.getText();
          webElement = webElement.findElement(By.xpath("./../a"));
          highlightCurrentElement(webElement);
          final String leaderName = webElement.getText();
          final String href = webElement.getAttribute("href");
          String webSiteMemberID = href.replace(Constants.NEWLINE, "");
          if (webSiteMemberID.indexOf(symbolId) != -1) {
            webSiteMemberID =
                webSiteMemberID.substring(
                    webSiteMemberID.indexOf(symbolId) + symbolId.length(),
                    webSiteMemberID.length());
          }
          LOG.debug(
              "Leader Type: [{}], Leader: [{}], Href: [{}], webSiteMemberID: [{}]",
              leaderType,
              leaderName,
              href,
              webSiteMemberID);
          if (!Leaders.getLeadersWebSiteMemberIDList().contains(webSiteMemberID)) {
            Leaders.getLeadersList().add(new Leaders(webSiteMemberID, leaderName, href));
          }
          // if
          // (!GroupLeaderLink.getGroupLeaderLinkWebSiteMemberIDList().contains(webSiteMemberID))
          // {
          GroupLeaderLink.getGroupLeaderLinkList()
              .add(new GroupLeaderLink(groupType, pageID, webSiteMemberID, leaderType));
          // }
        }
      }
    } catch (final Exception e) {
      LOG.error("Exception occurred", e);
    }
  }

  private Map<String, By> getGroupURLs() {
    final String rootNodePre = ".//div[@id='pnlNavBarFormat']/table//a/text()[contains(.,'";
    final String rootNodePost = "')]/..";
    final Map<String, By> mapByGroupURLsBy = new HashMap<>();
    mapByGroupURLsBy.put(
        "GroupDirectoryURL", By.xpath(rootNodePre + "Group Directory" + rootNodePost));
    mapByGroupURLsBy.put("CalendarURL", By.xpath(rootNodePre + "Calendar" + rootNodePost));
    mapByGroupURLsBy.put("BlogsURL", By.xpath(rootNodePre + "Blogs" + rootNodePost));
    mapByGroupURLsBy.put("ForumsURL", By.xpath(rootNodePre + "Forums" + rootNodePost));
    mapByGroupURLsBy.put("PhotoGalleryURL", By.xpath(rootNodePre + "Photo Gallery" + rootNodePost));
    return mapByGroupURLsBy;
  }

  public void searchSites() {
    try {
      final String fileName = Environment.getFolderData() + "searchVivitSites" + IExtension.CSV;
      final String ligs = VivitEnvironment.URL_LOGIN + "?page=LocalUserGroups";
      final String sigsUrl = VivitEnvironment.URL_LOGIN + "?page=SIGS";
      final By ligLinks = By.xpath(".//*[@id='CustomPageBody']//a[text()!='contact us today!']");
      final By sigLinks = By.xpath(".//*[@id='CustomPageBody']//a[text()!='contact us today!']");
      final By hrefLinks = By.xpath(".//*[@id='ctl00_PageContent_tdZone2']/table/tbody/tr/td/a");
      // final List<String> urls =
      // Arrays.asList("https://vivitworldwide.site-ym" + IExtension.COM +
      // "/", "https://vivitworldwide.site-ym" + IExtension.COM +
      // "/?page=board", "https://vivitworldwide.site-ym" + IExtension.COM
      // + "/staff/", "https://vivitworldwide.site-ym" + IExtension.COM +
      // "/?page=Volunteer", "https://c.ymcdn" + IExtension.COM +
      // "/sites/www.vivit-worldwide.org/resource/resmgr/docs/2017_VivitMemberBrochure"
      // + IExtension.PDF, "https://vivitworldwide.site-ym" +
      // IExtension.COM + "/?page=HallofFame");
      // for (final String url : urls) {
      // LOG.debug("Searching:[{}]", url);
      // getWebDriver().get(url);
      // }
      getWebDriver().get(ligs);
      sleep(1, 0);
      List<WebElement> links = getWebDriver().findElements(ligLinks);
      for (int index = 0; index < links.size(); index++) {
        final WebElement element = links.get(index);
        final String url = element.getAttribute("href");
        LOG.debug("Clicking: [{}]", url);
        clickObject(element);
        final List<WebElement> webElements = getWebDriver().findElements(hrefLinks);
        for (WebElement webElement : webElements) {
          final String leaderName = webElement.getText();
          final String href = webElement.getAttribute("href");
          webElement = webElement.findElement(By.xpath("./../b"));
          final String leaderType = webElement.getText();
          LOG.debug("Leader Type: [{}], Leader: [{}], Href: [{}]", leaderType, leaderName, href);
        }
        if (index < links.size()) {
          getWebDriver().get(ligs);
          sleep(1, 0);
          links = getWebDriver().findElements(ligLinks);
        }
      }
      getWebDriver().get(sigsUrl);
      sleep(1, 0);
      links = getWebDriver().findElements(sigLinks);
      for (int index = 0; index < links.size(); index++) {
        final WebElement element = links.get(index);
        final String url = element.getAttribute("href");
        LOG.debug("Clicking: [{}]", url);
        clickObject(element);
        final List<WebElement> webElements = getWebDriver().findElements(hrefLinks);
        for (WebElement webElement : webElements) {
          final String leaderName = webElement.getText();
          final String href = webElement.getAttribute("href");
          webElement = webElement.findElement(By.xpath("./../b"));
          final String leaderType = webElement.getText();
          LOG.debug("Leader Type: [{}], Leader: [{}], Href: [{}]", leaderType, leaderName, href);
        }
        if (index < links.size()) {
          getWebDriver().get(sigsUrl);
          sleep(1, 0);
          getWebDriver().findElements(sigLinks);
        }
      }
      final StringBuilder stringBuilderCSV = new StringBuilder();
      FSOTests.fileWrite(fileName, stringBuilderCSV.toString(), false);
      // https://www.vivit-worldwide.org/?page=LocalUserGroups
      // minimizeWindow();
    } catch (final Exception e) {
      LOG.error("Exception occurred", e);
    }
  }

  public void validateGroups() {
    validateGroup("LUGS");
    validateGroup("SIGS");
  }

  public void validateGroup(String group) {
    // Default
    final String groupType = group.toUpperCase(Locale.ENGLISH);
    String url = VivitEnvironment.URL_LOGIN + "?page=LocalUserGroups";
    switch (groupType) {
      case "LUGS":
        url = VivitEnvironment.URL_LOGIN + "?page=LocalUserGroups";
        break;
      case "SIGS":
        url = VivitEnvironment.URL_LOGIN + "?page=SIGS";
        break;
      default:
        LOG.warn("Unknown group type: {}. Using default LUGS URL.", groupType);
        break;
    }
    getWebDriver().get(url);
    final List<WebElement> webElements = getWebDriver().findElements(byLinksGroups);
    LOG.info("Total {}: [{}]", groupType, webElements.size());
    StringBuilder stringBuilder = new StringBuilder();
    for (int index = 0; index < webElements.size(); index++) {
      final WebElement webElement = webElements.get(index);
      highlightCurrentElement(webElement);
      final String item = webElement.getText();
      if (!item.trim().isEmpty()) {
        stringBuilder.append(item + Constants.NEWLINE);
        LOG.debug("{}: [{}]", index, item);
      }
    }
    final String fileData = Environment.getFolderData() + group + "-List" + IExtension.TXT;
    String expected = FSOTests.fileReadAll(fileData);
    expected = expected.replace("CÃ´te d'Ivoire", "Côte d'Ivoire");
    final String actual = stringBuilder.toString();
    Environment.getSoftAssert().assertEquals(expected, actual, group);
  }
}
