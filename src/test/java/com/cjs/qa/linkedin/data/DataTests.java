package com.cjs.qa.linkedin.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.cjs.qa.core.security.EPasswords;
import com.cjs.qa.jdbc.JDBC;
import com.cjs.qa.jdbc.JDBCConstants;
import com.cjs.qa.jdbc.SQL;
import com.cjs.qa.linkedin.LinkedIn;
import com.cjs.qa.utilities.CJSConstants;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.DateHelpersTests;
import com.cjs.qa.utilities.Email;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.JavaHelpers;

@Disabled("Windows-specific test - not compatible with Mac or Test Needs Updates")
public class DataTests {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(DataTests.class));

  private static JDBC jdbc = getJdbc();
  public static final String TABLE_LINKEDIN = "t_DOM_LinkedInConnections";
  public static final String TABLE_LINKEDIN_EMAILS = "t_DOM_LinkedInConnections_Emails";
  public static final String TABLE_LINKEDIN_PHONES = "t_DOM_LinkedInConnections_Phones";
  public static final String TABLE_LINKEDIN_WEBSITES = "t_DOM_LinkedInConnections_Websites";
  public static final String FIELD_LINKEDIN_URL = "LinkedIn URL";
  public static final List<String> FIELD_NAMES_LIST = getFieldNames();
  public static final int QUERY_LIMIT = getDailyLimit();
  public static final int RECORD_UPDATE_MIN = 50;

  public static StringBuilder appendRecordContactInfo(
      StringBuilder sqlStringBuilder, String linkedInURL, String fieldName, String fieldValue)
      throws Throwable {
    LOG.debug("***ClassMethodDebug***:[{}]", JavaHelpers.getCurrentClassMethodDebugName());
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("UPDATE [" + TABLE_LINKEDIN + "] ");
    stringBuilder.append("SET ");
    stringBuilder.append("[" + fieldName + "]='" + fieldValue.replaceAll("'", "''") + "' ");
    stringBuilder.append("WHERE [" + FIELD_LINKEDIN_URL + "]='" + linkedInURL + "';");
    sqlStringBuilder.append(stringBuilder);
    if (getQueryRecordCount(sqlStringBuilder) == RECORD_UPDATE_MIN) {
      sqlStringBuilder = updateRecords(sqlStringBuilder);
    }
    return sqlStringBuilder;
  }

  public static StringBuilder appendRecordContactInfoUpdated(
      StringBuilder sqlStringBuilder, String linkedInURL, String fieldName, String fieldValue)
      throws Throwable {
    LOG.debug("***ClassMethodDebug***:[{}]", JavaHelpers.getCurrentClassMethodDebugName());
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("UPDATE [" + TABLE_LINKEDIN + "] ");
    stringBuilder.append("SET ");
    stringBuilder.append(
        "[Last Updated]='"
            + DateHelpersTests.getCurrentDateTime(DateHelpersTests.FORMAT_US_STANDARD_DATE)
            + "' ");
    stringBuilder.append("WHERE [" + FIELD_LINKEDIN_URL + "]='" + linkedInURL + "';");
    sqlStringBuilder.append(stringBuilder);
    if (getQueryRecordCount(sqlStringBuilder) == RECORD_UPDATE_MIN) {
      sqlStringBuilder = updateRecords(sqlStringBuilder);
    }
    return sqlStringBuilder;
  }

  public static StringBuilder appendRecordURL(
      StringBuilder sqlStringBuilder, Map<String, String> map) throws Throwable {
    LOG.debug("***ClassMethodDebug***:[{}]", JavaHelpers.getCurrentClassMethodDebugName());
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("UPDATE [" + TABLE_LINKEDIN + "] ");
    stringBuilder.append(
        "SET ["
            + FIELD_LINKEDIN_URL
            + "]='"
            + map.get(FIELD_LINKEDIN_URL).replaceAll("'", "''")
            + "' ");
    stringBuilder.append(
        "WHERE [First Name]='" + map.get("First Name").replaceAll("'", "''") + "' ");
    stringBuilder.append("AND [Last Name]='" + map.get("Last Name").replaceAll("'", "''") + "' ");
    stringBuilder.append("AND [Company]='" + map.get("Company").replaceAll("'", "''") + "' ");
    stringBuilder.append("AND [Position]='" + map.get("Position").replaceAll("'", "''") + "' ");
    stringBuilder.append(
        "AND [Connected On]='" + map.get("Connected On").replaceAll("'", "''") + "';");
    sqlStringBuilder.append(stringBuilder);
    if (getQueryRecordCount(sqlStringBuilder) == RECORD_UPDATE_MIN) {
      sqlStringBuilder = updateRecords(sqlStringBuilder);
    }
    return sqlStringBuilder;
  }

  public static int getDailyLimit() {
    LOG.debug("***ClassMethodDebug***:[{}]", JavaHelpers.getCurrentClassMethodDebugName());
    final String methodName = JavaHelpers.getCurrentMethodName();
    StringBuilder sqlStringBuilder = new StringBuilder();
    sqlStringBuilder.append(JDBCConstants.SELECT_ALL_FROM);
    sqlStringBuilder.append("[" + TABLE_LINKEDIN + "];");
    String limitString = getJdbc().queryResults(sqlStringBuilder.toString(), "", false);
    int limit = Integer.valueOf(limitString);
    // limit = (limit / (365 * 2));
    limit = limit / 365;
    if (limit < RECORD_UPDATE_MIN) {
      limit = RECORD_UPDATE_MIN;
    }
    LOG.debug("{}:limit:[{}]", methodName, limit);
    return limit;
  }

  public static List<String> getFieldNames() {
    LOG.debug("***ClassMethodDebug***:[{}]", JavaHelpers.getCurrentClassMethodDebugName());
    List<String> list = new ArrayList<>();
    list.add("First Name");
    list.add("Last Name");
    list.add("Company");
    list.add("Position");
    list.add(FIELD_LINKEDIN_URL);
    list.add("Website (Blog)");
    list.add("Website (Company)");
    list.add("Phone (Mobile)");
    list.add("Phone (Work)");
    list.add("Email (Personal)");
    list.add("Email (Work)");
    list.add("Twitter");
    list.add("IM");
    list.add("Address");
    list.add("Birthday");
    list.add("Connected On");
    list.add("Last Updated");
    return list;
  }

  public static JDBC getJdbc() {
    if (jdbc == null) {
      setJdbc(new JDBC("", "QAAuto"));
    }
    return jdbc;
  }

  @Test
  public void testGetQueryLinkedInURL() {
    LOG.debug("***ClassMethodDebug***:[{}]", JavaHelpers.getCurrentClassMethodDebugName());
    LOG.debug("getQueryLinkedInURL():[{}{}]", Constants.NEWLINE, getQueryLinkedInURL());
  }

  public static String getQueryLinkedInURL() {
    LOG.debug("***ClassMethodDebug***:[{}]", JavaHelpers.getCurrentClassMethodDebugName());
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(JDBCConstants.SELECT);
    stringBuilder.append("cast(substr([Last Updated],7,4) || ");
    stringBuilder.append("substr([Last Updated],1,2) || ");
    stringBuilder.append("substr([Last Updated],4,2) As int) ");
    stringBuilder.append(JDBCConstants.AS + "[FilterDate],* ");
    stringBuilder.append("\n" + JDBCConstants.FROM + "[" + TABLE_LINKEDIN + "]");
    stringBuilder.append("\n" + JDBCConstants.WHERE + "[First Name]!=''");
    stringBuilder.append("\n" + JDBCConstants.AND + "[Last Name]!=''");
    // String dateMinus31Days =
    // DateHelpersTests.getCurrentDatePlusMinusDays("yyyyMMdd", -31);
    // stringBuilder.append("\n" + JDBCConstants.AND + "[FilterDate]<" +
    // dateMinus31Days + "");
    // stringBuilder.append("\n" + JDBCConstants.AND + "[FilterDate]<(" +
    // JDBCConstants.SELECT + "STRFTIME('%Y%m%d',Date('now','-31 days')))");
    stringBuilder.append("\n" + JDBCConstants.AND + "[FilterDate] In");
    stringBuilder.append("\n\t(" + JDBCConstants.SELECT_ALL);
    stringBuilder.append("\n\t" + JDBCConstants.FROM + "[v_LinkedIn_UpdateDates]");
    stringBuilder.append("\n\t" + JDBCConstants.GROUP_BY + "[FilterDate]");
    stringBuilder.append("\n\t" + JDBCConstants.ORDER_BY + "[FilterDate]");
    stringBuilder.append("\n\t" + JDBCConstants.LIMIT + "1)");
    stringBuilder.append("\n" + JDBCConstants.AND + "[" + FIELD_LINKEDIN_URL + "]!=''");
    stringBuilder.append("\n" + JDBCConstants.ORDER_BY);
    // Use substr to sort on (ORDER BY) the [Connected On] field.
    stringBuilder.append("\n\t" + "substr([Connected On],8,4)||");
    stringBuilder.append("\n\t\t" + "case substr([Connected On],4,3)");
    List<String> monthList =
        Arrays.asList(
            "Jan;Feb;Mar;Apr;May;Jun;Jul;Aug;Sep;Oct;Nov;Dec".split(Constants.DELIMETER_LIST));
    for (int monthIndex = 0; monthIndex < monthList.size(); monthIndex++) {
      String monthAbbreviation = monthList.get(monthIndex);
      String monthNumber = JavaHelpers.formatNumber(monthIndex + 1, "00");
      stringBuilder.append(
          "\n\t\t\t" + "when '" + monthAbbreviation + "' then '" + monthNumber + "'");
    }
    stringBuilder.append("\n\t\t\t" + "else '00' ");
    stringBuilder.append("\n\t\t" + "end||substr([Connected On],1,2)");
    stringBuilder.append("\n\t" + ",[Last Name],[First Name]");
    if (QUERY_LIMIT > -1) {
      stringBuilder.append("\n" + JDBCConstants.LIMIT + QUERY_LIMIT + ";");
    }
    return stringBuilder.toString();
  }

  public static String getQueryNoLinkedInURL() {
    LOG.debug("***ClassMethodDebug***:[{}]", JavaHelpers.getCurrentClassMethodDebugName());
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("SELECT *,Count(*) As [Counter]");
    stringBuilder.append("\nFROM [" + TABLE_LINKEDIN + "]");
    stringBuilder.append("\nWHERE [First Name]!=''");
    stringBuilder.append("\nAND [Last Name]!=''");
    stringBuilder.append("\nAND [" + FIELD_LINKEDIN_URL + "]=''");
    stringBuilder.append("\nGROUP BY [First Name],[Last Name]");
    stringBuilder.append("\nHAVING [Counter]>1");
    stringBuilder.append("\nORDER BY [Last Name],[First Name]");
    if (QUERY_LIMIT > -1) {
      stringBuilder.append("\nLIMIT " + QUERY_LIMIT);
    }
    return stringBuilder.toString();
  }

  public static int getQueryRecordCount(StringBuilder sqlStringBuilder) {
    LOG.debug("***ClassMethodDebug***:[{}]", JavaHelpers.getCurrentClassMethodDebugName());
    return sqlStringBuilder.toString().split(Constants.DELIMETER_LIST).length;
  }

  public static String getQueryUpdateLinkedInURL() {
    LOG.debug("***ClassMethodDebug***:[{}]", JavaHelpers.getCurrentClassMethodDebugName());
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder = new StringBuilder();
    stringBuilder.append("UPDATE [" + TABLE_LINKEDIN + "] ");
    stringBuilder.append(
        "SET ["
            + FIELD_LINKEDIN_URL
            + "]=REPLACE(REPLACE(["
            + FIELD_LINKEDIN_URL
            + "],'"
            + LinkedIn.LINKEDIN_URL
            + "',''),'/','')");
    return stringBuilder.toString();
  }

  public static String getQueryUpdateNotFound() {
    LOG.debug("***ClassMethodDebug***:[{}]", JavaHelpers.getCurrentClassMethodDebugName());
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder = new StringBuilder();
    stringBuilder.append("UPDATE [" + TABLE_LINKEDIN + "] ");
    stringBuilder.append("SET [" + FIELD_LINKEDIN_URL + "]='' ");
    stringBuilder.append("WHERE [" + FIELD_LINKEDIN_URL + "]='NOT FOUND'; ");
    return stringBuilder.toString();
  }

  @Test
  public void testUpdate() {
    LOG.debug("***ClassMethodDebug***:[{}]", JavaHelpers.getCurrentClassMethodDebugName());
    update();
  }

  public static void update() {
    StringBuilder stringBuilderSQL = new StringBuilder();
    List<String> tableTypeList = Arrays.asList("Email", "Phone", "Website");
    for (String tableType : tableTypeList) {
      stringBuilderSQL.append(JDBCConstants.DELETE_FROM);
      stringBuilderSQL.append("[" + TABLE_LINKEDIN + "_" + tableType + "s];");
      stringBuilderSQL.append(Constants.NEWLINE);
      List<String> fieldTypeList = null;
      switch (tableType) {
        case "Email":
          fieldTypeList = Arrays.asList("Personal", "Work");
          break;
        case "Phone":
          fieldTypeList = Arrays.asList("Home", "Mobile", "Work");
          break;
        case "Website":
          fieldTypeList = Arrays.asList("Blog", "Company");
          break;
        default:
          break;
      }
      if (fieldTypeList != null) {
        for (String fieldType : fieldTypeList) {
          stringBuilderSQL.append(JDBCConstants.INSERT_INTO);
          stringBuilderSQL.append("[" + TABLE_LINKEDIN + "_" + tableType + "s] ");
          stringBuilderSQL.append(JDBCConstants.SELECT);
          stringBuilderSQL.append("[LinkedIn URL],");
          stringBuilderSQL.append("'" + fieldType + "' as [Type],");
          stringBuilderSQL.append("[" + tableType + " (" + fieldType + ")] ");
          stringBuilderSQL.append(JDBCConstants.FROM);
          stringBuilderSQL.append("[" + TABLE_LINKEDIN + "] ");
          stringBuilderSQL.append(JDBCConstants.WHERE);
          stringBuilderSQL.append("[" + tableType + " (" + fieldType + ")]!='';");
          stringBuilderSQL.append(Constants.NEWLINE);
        }
      }
    }
    String[] sqlArray = stringBuilderSQL.toString().split(Constants.NEWLINE);
    for (String sql : sqlArray) {
      SQL.execute(sql);
    }
  }

  public static StringBuilder updateRecords(StringBuilder sqlStringBuilder) throws Throwable {
    LOG.debug("***ClassMethodDebug***:[{}]", JavaHelpers.getCurrentClassMethodDebugName());
    if (!sqlStringBuilder.toString().isEmpty()) {
      int recordsAffected = getJdbc().executeUpdate(sqlStringBuilder.toString(), false);
      String message =
          Constants.NEWLINE + sqlStringBuilder.toString().replaceAll(";", Constants.NEWLINE);
      // LOG.debug("{}", message);
      LOG.debug("recordsAffected:[[{}]", recordsAffected + "]");
      Email.sendEmail(
          CJSConstants.EMAIL_ADDRESS_GMAIL,
          EPasswords.EMAIL_GMAIL.getValue(),
          CJSConstants.EMAIL_ADDRESS_GMAIL,
          "",
          "",
          "LinkedIn Records Affected (" + recordsAffected + ")",
          message,
          "");
      sqlStringBuilder = new StringBuilder();
    }
    return sqlStringBuilder;
  }

  private static void setJdbc(JDBC jdbc) {
    DataTests.jdbc = jdbc;
  }
}
