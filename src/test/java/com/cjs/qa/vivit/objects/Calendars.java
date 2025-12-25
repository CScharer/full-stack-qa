package com.cjs.qa.vivit.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cjs.qa.jdbc.JDBC;
import com.cjs.qa.jdbc.SQL;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.vivit.VivitTables;

public class Calendars {

  private static List<Calendars> calendarsList = new ArrayList<>();
  private String calendarID;
  private String groupName;
  private String name;
  private String date;
  private String details;
  private String eventURL;

  /** Default constructor for data binding and instantiation. */
  public Calendars() {
    // Default constructor for data binding
  }

  private static String appendRecord(String tableName, List<String> fieldsList, Calendars record) {
    StringBuilder stringBuilder = new StringBuilder();
    final Map<String, String> map = new HashMap<>();
    for (final String field : fieldsList) {
      switch (field) {
        case "CalendarID":
          map.put(field, record.getCalendarID());
          break;
        case "GroupName":
          map.put(field, record.getGroupName());
          break;
        case "Name":
          map.put(field, record.getName());
          break;
        case "Date":
          map.put(field, record.getDate());
          break;
        case "Details":
          map.put(field, record.getDetails());
          break;
        case "EventURL":
          map.put(field, record.getEventURL());
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
    for (final Calendars calendars : getCalendarsList()) {
      sqlStringBuilder.append(appendRecord(tableName, fieldsList, calendars));
    }
    return sqlStringBuilder;
  }

  public static List<Calendars> getCalendarsList() {
    return calendarsList;
  }

  public Calendars(
      String calendarID,
      String groupName,
      String name,
      String date,
      String details,
      String eventURL) {
    this.calendarID = calendarID;
    this.groupName = groupName;
    this.name = name;
    this.date = date;
    this.details = details;
    this.eventURL = eventURL;
  }

  public String getCalendarID() {
    return calendarID;
  }

  public String getDate() {
    return date;
  }

  public String getDetails() {
    return details;
  }

  public String getEventURL() {
    return eventURL;
  }

  public String getGroupName() {
    return groupName;
  }

  public String getName() {
    return name;
  }

  public void setCalendarID(String calendarID) {
    this.calendarID = calendarID;
  }

  public static void setCalendarsList(List<Calendars> calendarsList) {
    Calendars.calendarsList = calendarsList;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public void setDetails(String details) {
    this.details = details;
  }

  public void setEventURL(String eventURL) {
    this.eventURL = eventURL;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public void setName(String name) {
    this.name = name;
  }
}
