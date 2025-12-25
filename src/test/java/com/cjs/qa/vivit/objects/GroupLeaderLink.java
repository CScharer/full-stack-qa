package com.cjs.qa.vivit.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.jdbc.JDBC;
import com.cjs.qa.jdbc.SQL;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.vivit.VivitTables;

public class GroupLeaderLink {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(GroupLeaderLink.class));

  private static List<GroupLeaderLink> groupLeaderLinkList = new ArrayList<>();
  private static List<String> groupLeaderLinkWebSiteMemberIDList = new ArrayList<>();
  private String groupType;
  private String pageID;
  private String webSiteMemberID;
  private String leaderType;

  /** Default constructor for data binding and instantiation. */
  public GroupLeaderLink() {
    // Default constructor for data binding
  }

  public GroupLeaderLink(
      String groupType, String pageID, String webSiteMemberID, String leaderType) {
    this.groupType = groupType;
    this.pageID = pageID;
    this.webSiteMemberID = webSiteMemberID;
    this.leaderType = leaderType;
    getGroupLeaderLinkWebSiteMemberIDList().add(webSiteMemberID);
  }

  private static String appendRecord(
      String tableName, List<String> fieldsList, GroupLeaderLink record) {
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
        case "Web_Site_Member_ID":
          map.put(field, record.getWebSiteMemberID());
          break;
        case "Position":
          map.put(field, record.getLeaderType());
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
    LOG.debug("***ClassMethodDebug***:[{}]", JavaHelpers.getCurrentClassMethodDebugName());
    final String tableName = VivitTables.PREFIX + JavaHelpers.getCurrentClassName() + "_Current";
    final JDBC jdbc = new JDBC("", "");
    List<String> fieldsList = jdbc.getFieldNamesList(tableName, true);
    jdbc.close();
    final StringBuilder sqlStringBuilder = new StringBuilder();
    for (final GroupLeaderLink groupLeaderLink : getGroupLeaderLinkList()) {
      sqlStringBuilder.append(appendRecord(tableName, fieldsList, groupLeaderLink));
    }
    return sqlStringBuilder;
  }

  public static List<GroupLeaderLink> getGroupLeaderLinkList() {
    return groupLeaderLinkList;
  }

  public static List<String> getGroupLeaderLinkWebSiteMemberIDList() {
    return groupLeaderLinkWebSiteMemberIDList;
  }

  public static void setGroupLeaderLinkWebSiteMemberIDList(
      List<String> groupLeaderLinkWebSiteMemberIDList) {
    GroupLeaderLink.groupLeaderLinkWebSiteMemberIDList = groupLeaderLinkWebSiteMemberIDList;
  }

  public String getGroupType() {
    return groupType;
  }

  public String getLeaderType() {
    return leaderType;
  }

  public String getPageID() {
    return pageID;
  }

  public String getWebSiteMemberID() {
    return webSiteMemberID;
  }

  public static void setGroupLeaderLinkList(List<GroupLeaderLink> groupLeaderLinkList) {
    GroupLeaderLink.groupLeaderLinkList = groupLeaderLinkList;
  }

  public void setGroupType(String groupType) {
    this.groupType = groupType;
  }

  public void setLeaderType(String leaderType) {
    this.leaderType = leaderType;
  }

  public void setPageID(String pageID) {
    this.pageID = pageID;
  }

  public void setWebSiteMemberID(String webSiteMemberID) {
    this.webSiteMemberID = webSiteMemberID;
  }
}
