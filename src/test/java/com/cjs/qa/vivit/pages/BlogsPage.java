package com.cjs.qa.vivit.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.vivit.VivitDataTests;
import com.cjs.qa.vivit.objects.Blogs;
import com.cjs.qa.vivit.objects.Groups;

public class BlogsPage extends Page {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(BlogsPage.class));

  public BlogsPage(WebDriver webDriver) {
    super(webDriver);
  }

  private String blogsUrlCommunity = "https://www.vivit-worldwide.org/members/blogs.asp";
  private static final By byTableBlogs =
      By.xpath(".//*[@id='SpContent_Container']/form/table/tbody/tr");

  private String getBlogsUrlCommunity() {
    return blogsUrlCommunity;
  }

  public void getBlogData() throws Throwable {
    LOG.debug("***ClassMethodDebug***: [{}]", getCurrentClassMethodDebugName());
    String statusName = "getBlogData";
    if (VivitDataTests.successFileExists(statusName)) {
      return;
    }
    for (final Groups groups : Groups.getGroupsList()) {
      if (hasValue(groups.getBlogsURL())) {
        LOG.info(
            "Retrieving Data: [{}], [{}], [{}]",
            getCurrentClassMethodName(),
            groups.getGroupName(),
            groups.getBlogsURL());
        getBlogs(groups.getBlogsURL(), groups.getGroupName());
      } else {
        LOG.debug(
            "Retrieving Null: [{}], [{}], [{}]",
            getCurrentClassMethodName(),
            groups.getGroupName(),
            groups.getBlogsURL());
      }
    }
    getBlogs(getBlogsUrlCommunity(), "Community");
    List<StringBuilder> sqlStringBuilderList = new ArrayList<>();
    sqlStringBuilderList.add(new StringBuilder("Blogs"));
    sqlStringBuilderList.add(Blogs.appendRecords());
    VivitDataTests.updateTableFromCurrentToPreviousAndInsert(sqlStringBuilderList);
    VivitDataTests.successFileCreate(statusName);
  }

  private void getBlogs(String url, String blogPage) {
    getWebDriver().get(url);
    String urlActual = getWebDriver().getCurrentUrl();
    if (!url.equalsIgnoreCase(urlActual)) {
      LOG.warn("blogPage: [{}], [{}] DOES NOT EQUAL [{}]", blogPage, url, urlActual);
    }
    final List<WebElement> listBlogRecords = getWebDriver().findElements(byTableBlogs);
    final Map<Integer, String> mapFields = new HashMap<>();
    for (int record = 0; record < listBlogRecords.size(); record++) {
      final WebElement elementRecord = listBlogRecords.get(record);
      String xPath = "./td";
      if (record > 0) {
        xPath += "/a";
      }
      final List<WebElement> listWebElements = elementRecord.findElements(By.xpath(xPath));
      final Map<String, String> mapBlogs = new HashMap<>();
      String groupName = null;
      String blogID = null;
      String name = null;
      String posts = null;
      String lastActivity = null;
      String blogURL = null;
      for (int fieldIndex = 0; fieldIndex < listWebElements.size(); fieldIndex++) {
        if (record == 0) {
          final WebElement webElement = listWebElements.get(fieldIndex);
          highlightCurrentElement(webElement);
          final String fieldValue = webElement.getText();
          mapFields.put(fieldIndex, fieldValue);
        } else {
          final WebElement webElement = listWebElements.get(fieldIndex);
          highlightCurrentElement(webElement);
          final String fieldValue = webElement.getText();
          final String href = webElement.getAttribute("href");
          blogID = href.split("/")[4];
          String fieldName = mapFields.get(fieldIndex);
          // Account for Sort/Options
          if (mapFields.size() == 4) {
            fieldName = mapFields.get(fieldIndex + 1);
          }
          switch (fieldName) {
            case "Blog Name / Description":
              mapBlogs.put("BlogID", blogID);
              mapBlogs.put("GroupName", blogPage);
              groupName = blogPage;
              mapBlogs.put("BlogURL", href);
              blogURL = href;
              name = fieldValue;
              mapBlogs.put("Name", fieldValue);
              name = fieldValue;
              break;
            case "No. of Posts":
              mapBlogs.put("Posts", fieldValue);
              posts = fieldValue;
              break;
            case "Last Activity":
              mapBlogs.put("LastActivity", fieldValue);
              lastActivity = fieldValue;
              break;
            default:
              LOG.warn("Unknown field name: {}. Skipping.", fieldName);
              break;
          }
        }
      }
      if (record > 0) {
        int numberOfPosts = 0;
        if (hasValue(posts)) {
          numberOfPosts = Integer.valueOf(posts);
        }
        Blogs.getBlogsList()
            .add(
                new Blogs(
                    groupName,
                    blogID,
                    name,
                    Integer.valueOf(numberOfPosts),
                    lastActivity,
                    blogURL));
      }
    }
  }
}
