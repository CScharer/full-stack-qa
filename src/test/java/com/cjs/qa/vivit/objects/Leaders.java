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

public class Leaders {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(Leaders.class));

  private static List<Leaders> leadersList = new ArrayList<>();
  private static List<String> leadersWebSiteMemberIDList = new ArrayList<>();
  private String webSiteMemberID;
  private String leaderName;
  private String href;

  /** Default constructor for data binding and instantiation. */
  public Leaders() {
    // Default constructor for data binding
  }

  public Leaders(String webSiteMemberID, String leaderName, String href) {
    this.webSiteMemberID = webSiteMemberID;
    this.leaderName = leaderName;
    this.href = href;
    getLeadersWebSiteMemberIDList().add(webSiteMemberID);
  }

  private static String appendRecord(String tableName, List<String> fieldsList, Leaders record) {
    StringBuilder stringBuilder = new StringBuilder();
    final Map<String, String> map = new HashMap<>();
    for (final String field : fieldsList) {
      switch (field) {
        case "Web_Site_Member_ID":
          map.put(field, record.getWebSiteMemberID());
          break;
        case "Name":
          map.put(field, record.getLeaderName());
          break;
        case "Href":
          map.put(field, record.getHref());
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
    for (final Leaders leaders : getLeadersList()) {
      sqlStringBuilder.append(appendRecord(tableName, fieldsList, leaders));
    }
    return sqlStringBuilder;
  }

  public static List<Leaders> getLeadersList() {
    return leadersList;
  }

  public static List<String> getLeadersWebSiteMemberIDList() {
    return leadersWebSiteMemberIDList;
  }

  public static void setLeadersWebSiteMemberIDList(List<String> leadersWebSiteMemberIDList) {
    Leaders.leadersWebSiteMemberIDList = leadersWebSiteMemberIDList;
  }

  public String getHref() {
    return href;
  }

  public String getLeaderName() {
    return leaderName;
  }

  public String getWebSiteMemberID() {
    return webSiteMemberID;
  }

  public void setHref(String href) {
    this.href = href;
  }

  public void setLeaderName(String leaderName) {
    this.leaderName = leaderName;
  }

  public static void setLeadersList(List<Leaders> leadersList) {
    Leaders.leadersList = leadersList;
  }

  public void setWebSiteMemberID(String webSiteMemberID) {
    this.webSiteMemberID = webSiteMemberID;
  }
}
