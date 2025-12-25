package com.cjs.qa.vivit.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.core.QAException;
import com.cjs.qa.jdbc.JDBC;
import com.cjs.qa.jdbc.JDBCConstants;
import com.cjs.qa.jdbc.SQL;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.vivit.VivitDataTests;
import com.cjs.qa.vivit.VivitTables;

public class Groups {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(Groups.class));

  private static List<Groups> groupsList = new ArrayList<>();
  private String groupType;
  private String pageID;
  private String groupName;
  private String elapsedTime;
  private String wallPosts;
  // Added Later.
  private String chapterURL;
  private String groupDirectoryURL;
  private String calendarURL;
  private String blogsURL;
  private String forumsURL;
  private String photoGalleryURL;
  private static boolean isGroupPage = false;
  public static final String BASE_LIST = "Type;PageID;Name;Timing;WallPosts;ChapterURL";
  public static final String EXTENDED_LIST =
      "GroupDirectoryURL;CalendarURL;BlogsURL;ForumsURL;PhotoGalleryURL";

  /** Default constructor for data binding and instantiation. */
  public Groups() {
    // Default constructor for data binding
  }

  public Groups(
      String groupType,
      String pageID,
      String groupName,
      String elapsedTime,
      String wallPosts,
      String chapterURL) {
    this.groupType = groupType;
    this.pageID = pageID;
    this.groupName = groupName;
    this.elapsedTime = elapsedTime;
    this.wallPosts = wallPosts;
    this.chapterURL = chapterURL;
  }

  public static List<String> getBaseList() {
    return Arrays.asList(BASE_LIST.split(Constants.DELIMETER_LIST));
  }

  public static List<String> getExtendedList() {
    return Arrays.asList(EXTENDED_LIST.split(Constants.DELIMETER_LIST));
  }

  public static boolean isGroupPage() {
    return isGroupPage;
  }

  public static void setGroupPage(boolean isGroupPage) {
    Groups.isGroupPage = isGroupPage;
  }

  private static String appendRecord(String tableName, List<String> fieldsList, Groups record) {
    StringBuilder stringBuilder = new StringBuilder();
    final Map<String, String> map = new HashMap<>();
    for (final String field : fieldsList) {
      switch (field) {
        case "Type":
          map.put(field, record.getGroupType());
          break;
        case "PageID":
          map.put(field, record.getPageID());
          break;
        case "Name":
          map.put(field, record.getGroupName());
          break;
        case "Timing":
          map.put(field, record.getElapsedTime());
          break;
        case "WallPosts":
          map.put(field, record.getWallPosts());
          break;
        case "ChapterURL":
          map.put(field, record.getChapterURL());
          break;
        case "GroupDirectoryURL":
          map.put(field, record.getGroupDirectoryURL());
          break;
        case "CalendarURL":
          map.put(field, record.getCalendarURL());
          break;
        case "BlogsURL":
          map.put(field, record.getBlogsURL());
          break;
        case "ForumsURL":
          map.put(field, record.getForumsURL());
          break;
        case "PhotoGalleryURL":
          map.put(field, record.getPhotoGalleryURL());
          break;
        default:
          map.put(field, "Field Not In Case");
          break;
      }
    }
    stringBuilder = SQL.appendStringBuilderSQLInsertRecord(tableName, stringBuilder, map, true);
    return stringBuilder.toString();
  }

  public static StringBuilder appendRecords() throws Throwable {
    LOG.debug("***ClassMethodDebug***:[{}]", JavaHelpers.getCurrentClassMethodDebugName());
    final String tableName = VivitTables.PREFIX + JavaHelpers.getCurrentClassName() + "_Current";
    final JDBC jdbc = new JDBC("", "");
    List<String> fieldsList = jdbc.getFieldNamesList(tableName, true);
    jdbc.close();
    final StringBuilder sqlStringBuilder = new StringBuilder();
    for (final Groups groups : getGroupsList()) {
      sqlStringBuilder.append(appendRecord(tableName, fieldsList, groups));
    }
    return sqlStringBuilder;
  }

  public static void intializeGroupsList(List<Groups> groupsList) throws Throwable {
    JDBC jdbc = new JDBC("", VivitDataTests.DATABASE_DEFINITION);
    StringBuilder sqlStringBuilder = new StringBuilder();
    sqlStringBuilder.append(
        JDBCConstants.SELECT_ALL_FROM + "[" + VivitTables.VIVIT_GROUPS_CURRENT + "] ");
    sqlStringBuilder.append(JDBCConstants.ORDER_BY + "[" + "Name" + "];");
    List<Map<String, String>> groupListMap =
        jdbc.queryResultsString(sqlStringBuilder.toString(), false);
    jdbc.close();
    for (Map<String, String> groupMap : groupListMap) {
      String groupType = groupMap.get("Type").toString();
      String pageID = groupMap.get("PageID").toString();
      String groupName = groupMap.get("Name").toString();
      String elapsedTime = groupMap.get("Timing").toString();
      String wallPosts = groupMap.get("WallPosts").toString();
      String chapterURL = groupMap.get("ChapterURL").toString();
      groupsList.add(new Groups(groupType, pageID, groupName, elapsedTime, wallPosts, chapterURL));
      Map<String, String> mapGroupURLs = new HashMap<>();
      for (String fieldName : getExtendedList()) {
        String fieldValue = (String) groupMap.get(fieldName);
        mapGroupURLs.put(fieldName, fieldValue);
      }
      groupsList.get(groupsList.size() - 1).updateURLs(mapGroupURLs);
    }
    setGroupsList(groupsList);
    if (getGroupsList().isEmpty()) {
      throw new QAException(
          JavaHelpers.getCurrentMethodName()
              + ":No records were found in the ["
              + VivitTables.VIVIT_GROUPS_CURRENT
              + "] table.");
    }
  }

  public static List<Groups> getGroupsList() throws Throwable {
    if (!isGroupPage() && groupsList.isEmpty()) {
      // Groups.getGroupsList()
      intializeGroupsList(groupsList);
      // We need to ensure the GroupsList is populated for the following:
      // BlogsPage.getBlogData()
      // CalendarsPage.getCalendarData()
      // ForumsPage.getForumData()
    }
    return groupsList;
  }

  public String getBlogsURL() {
    return blogsURL;
  }

  public String getCalendarURL() {
    return calendarURL;
  }

  public String getChapterURL() {
    return chapterURL;
  }

  public String getElapsedTime() {
    return elapsedTime;
  }

  public String getForumsURL() {
    return forumsURL;
  }

  public String getGroupDirectoryURL() {
    return groupDirectoryURL;
  }

  public String getGroupName() {
    return groupName;
  }

  public String getGroupType() {
    return groupType;
  }

  public String getPageID() {
    return pageID;
  }

  public String getPhotoGalleryURL() {
    return photoGalleryURL;
  }

  public String getWallPosts() {
    return wallPosts;
  }

  public void setBlogsURL(String blogsURL) {
    this.blogsURL = blogsURL;
  }

  public void setCalendarURL(String calendarURL) {
    this.calendarURL = calendarURL;
  }

  public void setChapterURL(String chapterURL) {
    this.chapterURL = chapterURL;
  }

  public void setElapsedTime(String elapsedTime) {
    this.elapsedTime = elapsedTime;
  }

  public void setForumsURL(String forumsURL) {
    this.forumsURL = forumsURL;
  }

  public void setGroupDirectoryURL(String groupDirectoryURL) {
    this.groupDirectoryURL = groupDirectoryURL;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public static void setGroupsList(List<Groups> groupsList) {
    Groups.groupsList = groupsList;
  }

  public void setGroupType(String groupType) {
    this.groupType = groupType;
  }

  public void setPageID(String pageID) {
    this.pageID = pageID;
  }

  public void setPhotoGalleryURL(String photoGalleryURL) {
    this.photoGalleryURL = photoGalleryURL;
  }

  public void setWallPosts(String wallPosts) {
    this.wallPosts = wallPosts;
  }

  public void updateURLs(Map<String, String> map) {
    for (final Entry<String, String> entry : map.entrySet()) {
      final String field = entry.getKey();
      final String value = entry.getValue();
      switch (field) {
        case "GroupDirectoryURL":
          setGroupDirectoryURL(value);
          break;
        case "CalendarURL":
          setCalendarURL(value);
          break;
        case "BlogsURL":
          setBlogsURL(value);
          break;
        case "ForumsURL":
          setForumsURL(value);
          break;
        case "PhotoGalleryURL":
          setPhotoGalleryURL(value);
          break;
        default:
          break;
      }
    }
  }
}
