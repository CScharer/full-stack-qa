package com.cjs.qa.vivit.pages;

import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cjs.qa.core.Environment;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.vivit.VivitEnvironment;

public class SearchPage extends Page {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SearchPage.class));

  public SearchPage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final By linksGroups =
      By.xpath("//*[@id='CustomPageBody']//a[text()!='contact us today!']");

  public void searchSites() {
    try {
      final String fileName = Environment.getFolderData() + "searchVivitSites" + IExtension.CSV;
      final String ligs = VivitEnvironment.URL_LOGIN + "?page=LocalUserGroups";
      final String sigs = VivitEnvironment.URL_LOGIN + "?page=SIGS";
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
          LOG.debug("Leader Type: [{}]", leaderType);
          LOG.debug("Leader: [{}]", leaderName);
          LOG.debug("Href: [{}]", href);
        }
        if (index < links.size()) {
          getWebDriver().get(ligs);
          sleep(1, 0);
          links = getWebDriver().findElements(ligLinks);
        }
      }
      getWebDriver().get(sigs);
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
          LOG.debug("Leader Type: [{}]", leaderType);
          LOG.debug("Leader: [{}]", leaderName);
          LOG.debug("Href: [{}]", href);
        }
        if (index < links.size()) {
          getWebDriver().get(sigs);
          sleep(1, 0);
          getWebDriver().findElements(sigLinks);
        }
      }
      final StringBuilder stringBuilderCSV = new StringBuilder();
      FSOTests.fileWrite(fileName, stringBuilderCSV.toString(), false);
      // https://www.vivit-worldwide.org/?page=LocalUserGroups
      // minimizeWindow();
    } catch (final Exception e) {
      LOG.error("Error in searchSites", e);
    }
  }

  public void validateGroups() {
    validateGroup("LUGS");
    validateGroup("SIGS");
  }

  public void validateGroup(String group) {
    // Default
    final String groupUpper = group.toUpperCase(Locale.ENGLISH);
    // Java 17: Switch expression returning value directly
    String url =
        switch (groupUpper) {
          case "LUGS" -> VivitEnvironment.URL_LOGIN + "?page=LocalUserGroups";
          case "SIGS" -> VivitEnvironment.URL_LOGIN + "?page=SIGS";
          default -> {
            LOG.warn("Unknown group type: {}. Using default LUGS URL.", groupUpper);
            yield VivitEnvironment.URL_LOGIN + "?page=LocalUserGroups";
          }
        };
    getWebDriver().get(url);
    final List<WebElement> webElements = getWebDriver().findElements(linksGroups);
    LOG.info("Total {}: [{}]", groupUpper, webElements.size());
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
