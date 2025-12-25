package com.cjs.qa.vivit.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cjs.qa.jdbc.JDBC;
import com.cjs.qa.jdbc.SQL;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.vivit.VivitTables;

public class Forums {

  private static List<Forums> forumsList = new ArrayList<>();
  private String groupName;
  private String forumID;
  private String name;
  private int topics;
  private int posts;
  private String lastActivity;
  private String forumURL;

  /** Default constructor for data binding and instantiation. */
  public Forums() {
    // Default constructor for data binding
  }

  public Forums(
      String groupName,
      String forumID,
      String name,
      int topics,
      int posts,
      String lastActivity,
      String forumURL) {
    this.groupName = groupName;
    this.forumID = forumID;
    this.name = name;
    this.topics = topics;
    this.posts = posts;
    this.lastActivity = lastActivity;
    this.forumURL = forumURL;
  }

  private static String appendRecord(String tableName, List<String> fieldsList, Forums record) {
    StringBuilder stringBuilder = new StringBuilder();
    final Map<String, String> map = new HashMap<>();
    for (final String field : fieldsList) {
      switch (field) {
        case "GroupName":
          map.put(field, record.getGroupName());
          break;
        case "ForumID":
          map.put(field, record.getForumID());
          break;
        case "Name":
          map.put(field, record.getName());
          break;
        case "Topics":
          map.put(field, String.valueOf(record.getTopics()));
          break;
        case "Posts":
          map.put(field, String.valueOf(record.getPosts()));
          break;
        case "LastActivity":
          map.put(field, record.getLastActivity());
          break;
        case "ForumURL":
          map.put(field, record.getForumURL());
          break;
        default:
          map.put(field, "Field Not In Case");
          break;
      }
    }
    stringBuilder = SQL.appendStringBuilderSQLInsertRecord(tableName, stringBuilder, map, true);
    return stringBuilder.toString();
  }

  public static StringBuilder appendRecords() {
    final String tableName = VivitTables.PREFIX + JavaHelpers.getCurrentClassName() + "_Current";
    final JDBC jdbc = new JDBC("", "");
    List<String> fieldsList = jdbc.getFieldNamesList(tableName, true);
    jdbc.close();
    final StringBuilder sqlStringBuilder = new StringBuilder();
    for (final Forums forums : getForumsList()) {
      sqlStringBuilder.append(appendRecord(tableName, fieldsList, forums));
    }
    return sqlStringBuilder;
  }

  public static List<Forums> getForumsList() {
    return forumsList;
  }

  public String getForumID() {
    return forumID;
  }

  public String getForumURL() {
    return forumURL;
  }

  public String getGroupName() {
    return groupName;
  }

  public String getLastActivity() {
    return lastActivity;
  }

  public String getName() {
    return name;
  }

  public int getPosts() {
    return posts;
  }

  public int getTopics() {
    return topics;
  }

  public void setForumID(String forumID) {
    this.forumID = forumID;
  }

  public static void setForumsList(List<Forums> forumsList) {
    Forums.forumsList = forumsList;
  }

  public void setForumURL(String forumURL) {
    this.forumURL = forumURL;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public void setLastActivity(String lastActivity) {
    this.lastActivity = lastActivity;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPosts(int posts) {
    this.posts = posts;
  }

  public void setTopics(int topics) {
    this.topics = topics;
  }
}
