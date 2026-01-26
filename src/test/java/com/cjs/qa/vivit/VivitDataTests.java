package com.cjs.qa.vivit;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.cjs.qa.core.Environment;
import com.cjs.qa.core.QAException;
import com.cjs.qa.core.security.EPasswords;
import com.cjs.qa.google.Google;
import com.cjs.qa.google.objects.Flight;
import com.cjs.qa.gt.GTWebinarDataTests;
import com.cjs.qa.gt.api.services.GTWebinarServiceTests;
import com.cjs.qa.jdbc.DBParameters;
import com.cjs.qa.jdbc.JDBC;
import com.cjs.qa.jdbc.JDBCConstants;
import com.cjs.qa.jdbc.SQL;
import com.cjs.qa.jenkins.Jenkins;
import com.cjs.qa.microsoft.excel.IExcel;
import com.cjs.qa.microsoft.excel.xls.XLS;
import com.cjs.qa.selenium.EDriverProperties;
import com.cjs.qa.selenium.SeleniumWebDriver;
import com.cjs.qa.utilities.CJSConstants;
import com.cjs.qa.utilities.CommandLineTests;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.Convert;
import com.cjs.qa.utilities.DateHelpersTests;
import com.cjs.qa.utilities.Email;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.HTML;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.utilities.ParameterHelper;
import com.cjs.qa.utilities.XML;
import com.cjs.qa.utilities.colors.ColorsHEX;
import com.cjs.qa.ym.YMDataTests;
import com.cjs.qa.ym.api.services.YMAPI;
import com.cjs.qa.ym.api.services.YMService;

@Disabled("Deprecated and Requires VivitDataTests.DATABASE_DEFINITION")
public class VivitDataTests extends Environment {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(VivitDataTests.class));

  public static final String DATABASE_DEFINITION = "QAAuto";
  public static final Boolean DEBUGGING = true;
  public static final int DAYS_TO_KEEP_BACKUPS = 3;
  public static final String EMAIL_ADDRESS_DIRECTORS = "Directors" + CJSConstants.MAILDOMAIN_VIVIT;
  public static final String REPORT_DAY_BILLABLE_HOURS = "01";
  public static final String REPORT_DAY_TREASURER = "10";
  public static final int FILE_COUNT_EXPECTED = 14;
  public static final int RECORD_LIMIT_100 = 100;
  public static final String LOGO_SOURCE = FSOTests.fileReadAll(VivitFoldersFiles.LOGO_SOURCE);
  public static final String STYLE_BORDER =
      "border: 1px solid black; border-collapse: collapse;width: 100%;";
  public static final String LABEL_API_NAMESPACE = "apiNamespace";
  public static final String LABEL_ATTACHMENT = "attachment";
  public static final String LABEL_DATE_TIME_FROM = "dateTimeFrom";
  public static final String LABEL_FILE_PATH_NAME = "filePathName";
  public static final String LABEL_LABEL = "Label";
  public static final String LABEL_INITIALIZE_STATUS = "initializeTest";
  public static final String LABEL_FIELD_ADDED = "Added";
  public static final String LABEL_FIELD_REMOVED = "Removed";
  public static final String LABEL_EVENT_ID = "EventID";
  public static final String LABEL_RECORD_COMPLETE = "RecordComplete";
  public static final String LABEL_RECORD_NUMBER = "RecordNumber";
  public static final String LABEL_RESULTS_MAP = "resultsMap:[";
  public static final String LABEL_SEND_EMAIL = "sendEmail";
  public static final String LABEL_TABLE_NAME = "tableName";
  public static final String LABEL_WEB_SITE_MEMBER_ID = "Web_Site_Member_ID";
  public static final String QUERY_REPORT_AUTOMATION_EMAIL =
      JDBCConstants.SELECT
          + "[EMail] "
          + JDBCConstants.FROM
          + "["
          + VivitViews.VIVIT_AUTOMATION_REPORT
          + "]";
  public static final String QUERY_REPORT_BILLABLE_HOURS_EMAIL =
      JDBCConstants.SELECT
          + "[EMail] "
          + JDBCConstants.FROM
          + "["
          + VivitViews.VIVIT_CONTRACTORS_BILLABLE_HOURS
          + "]";
  private static final List<String> DATABASE_ONLY_FIELDS =
      Arrays.asList(LABEL_RECORD_COMPLETE, LABEL_RECORD_NUMBER);

  public static List<String> getDatabaseOnlyFields() {
    return DATABASE_ONLY_FIELDS;
  }

  @Test
  public void temporaryTest() {
    List<String> sqlFileList =
        FSOTests.filesList(VivitFoldersFiles.PATH_API_DATA_YM_SQL, IExtension.SQL);
    // t_Vivit_Events_Current
    for (String filePathName : sqlFileList) {
      StringBuilder stringBuilder = new StringBuilder();
      // sysOut(filePathName)
      String fileContents = FSOTests.fileReadAll(filePathName);
      if (fileContents.contains("DELETE FROM")) {
        stringBuilder.append("DELETE");
      }
      if (fileContents.contains("INSERT INTO")) {
        if (!"".equals(stringBuilder.toString())) {
          stringBuilder.append(", ");
        }
        stringBuilder.append("INSERT");
      }
      if (fileContents.contains("UPDATE")) {
        if (!"".equals(stringBuilder.toString())) {
          stringBuilder.append(", ");
        }
        stringBuilder.append("UPDATE");
      }
      String fileName = new File(filePathName).getName();
      sysOut(fileName + " " + stringBuilder.toString());
    }
  }

  /**
   * @param classMethodType
   * @throws Throwable
   */
  public static void checkStatus(String classMethodType) throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList()));
    Boolean success = successFileStatus(classMethodType, true);
    if (!success) {
      String successFilePathName = successFileName(classMethodType);
      throw new QAException(
          JavaHelpers.getCurrentClassMethodDebugName()
              + "- The success file ["
              + successFilePathName
              + "] was either missing or showed the last run failed!");
    }
  }

  @Test
  public void createStandardTables() throws QAException {
    final String fileStandardTable = getFolderData() + "StandardTable" + IExtension.HTML;
    HTML html =
        new HTML(
            fileStandardTable,
            ColorsHEX.VIVIT_GOLD.getValue(),
            ColorsHEX.VIVIT_LIGHTBLUE.getValue());
    final List<String> viewsList =
        Arrays.asList(
            ("v_Vivit_Broken_Links;v_Vivit_GroupLeaders_Current_By_Group;"
                    + "v_Vivit_GroupLeaders_Current_By_Role;v_Vivit_Contractors_Billable_Hours")
                .split(Constants.DELIMETER_LIST));
    JDBC jdbc = new JDBC("", DATABASE_DEFINITION);
    for (int viewIndex = 0; viewIndex < viewsList.size(); viewIndex++) {
      final String view = viewsList.get(viewIndex);
      FSOTests.fileWrite(fileStandardTable, "<h><b>" + view + "</b></h>" + Constants.NEWLINE, true);
      String sql =
          JDBCConstants.SELECT_ALL
              + JDBCConstants.SELECT_COUNT_FROM
              + "["
              + view
              + "] "
              + JDBCConstants.LIMIT
              + "0";
      final List<String> fieldList = jdbc.getFieldNamesList(sql, true);
      sql = JDBCConstants.SELECT_ALL + JDBCConstants.SELECT_COUNT_FROM + "[" + view + "]";
      final List<Map<String, String>> reportListMap = jdbc.queryResultsString(sql, false);
      String stringHTML = html.createStandardTable(fieldList, reportListMap);
      if (viewIndex < (viewsList.size() - 1)) {
        stringHTML += Constants.NEWLINE;
      }
      FSOTests.fileWrite(fileStandardTable, stringHTML, viewIndex != 0);
      String xml = XML.formatPretty(stringHTML);
      sysOut(xml);
    }
    jdbc.close();
  }

  public static void deleteOldDataBackups() throws Throwable {
    // Delete backup files older than 3 days old.
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList(Arrays.asList())));
    final String formatDate = DateHelpersTests.FORMAT_YYYY_MM_DD_COMPACT;
    final String date =
        DateHelpersTests.getCurrentDatePlusMinusDays(formatDate, DAYS_TO_KEEP_BACKUPS * -1);
    final String folderName = VivitFoldersFiles.PATH_DATA + date;
    FSOTests.fileDelete(folderName);
    List<Path> pathList = FSOTests.pathsList(Constants.PATH_FILES_DATA_DATABASES, IExtension.BAK);
    File[] filesBackup = Convert.fromListPathToFileArray(pathList);
    filesBackup = FSOTests.sortByLastModified(filesBackup, false);
    FileTime fileTime;
    final int deleteDaysAgo = Integer.parseInt(date);
    for (int fileIndex = 0; fileIndex < 0; fileIndex++) {
      final File file = filesBackup[fileIndex];
      final String filePathName = file.getAbsolutePath();
      final Path path = Paths.get(filePathName);
      try {
        fileTime = Files.getLastModifiedTime(path);
        DateFormat dateFormat = new SimpleDateFormat(formatDate);
        final Integer lastModified = Integer.valueOf(dateFormat.format(fileTime.toMillis()));
        if (fileIndex >= DAYS_TO_KEEP_BACKUPS) {
          sysOut(
              "Deleting filePathName:["
                  + filePathName
                  + "], ["
                  + dateFormat.format(fileTime.toMillis())
                  + "]");
          if (lastModified < deleteDaysAgo) {
            FSOTests.fileDelete(filePathName);
          }
        } else {
          sysOut(
              "Preserved filePathName:["
                  + filePathName
                  + "], ["
                  + dateFormat.format(fileTime.toMillis())
                  + "]");
        }
      } catch (final Exception e) {
        sysOut(e);
      }
    }
  }

  /**
   * @param tableGroupName
   */
  public static void dropAndCreateBackUpTables(String tableGroupName) {
    StringBuilder sqlStringBuilder = new StringBuilder();
    String datePreviousBackUp =
        DateHelpersTests.getCurrentDatePlusMinusDays(
            DateHelpersTests.FORMAT_YYYY_MM_DD_COMPACT, DAYS_TO_KEEP_BACKUPS * -1);
    final String dateCurrentBackUp =
        DateHelpersTests.getCurrentDateTime(DateHelpersTests.FORMAT_YYYY_MM_DD_COMPACT);
    String tableName = VivitTables.PREFIX + tableGroupName;
    // Drop the old backup table.
    String tableNameBackUpPrevious = tableName + "_" + datePreviousBackUp;
    sqlStringBuilder.append(JDBCConstants.DROP_TABLE);
    sqlStringBuilder.append(JDBCConstants.IF_EXISTS + "[" + tableNameBackUpPrevious + "];");
    sqlStringBuilder.append(Constants.NEWLINE);
    sysOut(sqlStringBuilder.toString());
    SQL.executeVivit("DropIfExists", tableNameBackUpPrevious, sqlStringBuilder);
    // Drop the current backup table.
    String tableNameBackUp = tableName + "_" + dateCurrentBackUp;
    sqlStringBuilder = new StringBuilder();
    sqlStringBuilder.append(JDBCConstants.DROP_TABLE);
    sqlStringBuilder.append(JDBCConstants.IF_EXISTS + "[" + tableNameBackUp + "];");
    sqlStringBuilder.append(Constants.NEWLINE);
    sysOut(sqlStringBuilder.toString());
    SQL.executeVivit("DropIfExists", tableNameBackUp, sqlStringBuilder);
    // Create the new current backup table from the Previous table.
    final String tableNamePrevious = tableName + "_" + "Previous";
    sqlStringBuilder = new StringBuilder();
    sqlStringBuilder.append(JDBCConstants.CREATE_TABLE + "[" + tableNameBackUp + "] ");
    sqlStringBuilder.append(JDBCConstants.AS);
    sqlStringBuilder.append(JDBCConstants.SELECT_ALL_FROM + "[" + tableNamePrevious + "];");
    sqlStringBuilder.append(Constants.NEWLINE);
    sysOut(sqlStringBuilder.toString());
    SQL.executeVivit("CreateTable", tableNameBackUp, sqlStringBuilder);
  }

  public static String getEmailCopy() {
    return CJSConstants.EMAIL_ADDRESS_VIVIT;
  }

  public static String getEmailFrom() {
    return CJSConstants.EMAIL_ADDRESS_VIVIT;
  }

  public static String getEmailPassword() {
    return EPasswords.EMAIL_VIVIT.getValue();
  }

  /**
   * @param query
   * @return
   */
  public static String getEmailTo(String query) {
    JDBC jdbc = new JDBC("", DATABASE_DEFINITION);
    String eMailTo = jdbc.queryResults(query, "", false);
    jdbc.close();
    eMailTo = eMailTo.replaceAll(Constants.NEWLINE, ",");
    sysOut("eMailTo:[" + eMailTo + "]");
    return eMailTo;
  }

  /**
   * @param header
   * @param footer
   * @param fieldList
   * @param reportListMap
   * @return
   * @throws Throwable
   */
  public static String getBillableHoursTable(
      String header, String footer, List<String> fieldList, List<Map<String, String>> reportListMap)
      throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(
        ParameterHelper.getParameters(
            Arrays.asList(
                Arrays.asList("header", header),
                Arrays.asList("footer", footer),
                Arrays.asList("fields", fieldList))));
    int tab = 0;
    StringBuilder stringBuilderReport = new StringBuilder();
    stringBuilderReport.append(HTML.HEADER);
    stringBuilderReport.append(Constants.nlTab(1, tab++) + "<html>");
    stringBuilderReport.append(Constants.nlTab(1, tab++) + "<head>");
    stringBuilderReport.append(
        Constants.nlTab(1, tab--)
            + "<link rel="
            + Constants.QUOTE_DOUBLE
            + "stylesheet"
            + Constants.QUOTE_DOUBLE
            + " type="
            + Constants.QUOTE_DOUBLE
            + "text/css"
            + Constants.QUOTE_DOUBLE
            + " href="
            + Constants.QUOTE_DOUBLE
            + "TableReport.css"
            + Constants.QUOTE_DOUBLE
            + "></link>");
    stringBuilderReport.append(Constants.nlTab(1, tab) + "</head>");
    stringBuilderReport.append(Constants.nlTab(1, tab++) + "<body>");
    StringBuilder stringBuilderTable = new StringBuilder();
    boolean firstRecord = false;
    for (Map<String, String> mapReport : reportListMap) {
      if (!firstRecord) {
        firstRecord = true;
        if (header != null) {
          StringBuilder stringBuilderHeader = new StringBuilder();
          stringBuilderHeader.append(
              Constants.nlTab(1, tab++)
                  + "<table class="
                  + Constants.QUOTE_DOUBLE
                  + "scroll"
                  + Constants.QUOTE_DOUBLE
                  + " style="
                  + Constants.QUOTE_DOUBLE
                  + STYLE_BORDER
                  + Constants.QUOTE_DOUBLE
                  + ">");
          stringBuilderHeader.append(Constants.nlTab(1, tab++) + "<tr>");
          stringBuilderHeader.append(
              Constants.nlTab(1, tab++)
                  + "<th style="
                  + Constants.QUOTE_DOUBLE
                  + "border: 1px solid black; border-collapse: collapse;"
                  + " background-color: "
                  + ColorsHEX.VIVIT_GRAY.getValue()
                  + ";"
                  + Constants.QUOTE_DOUBLE
                  + ">");
          stringBuilderHeader.append(
              Constants.nlTab(1, tab++)
                  + "<div style="
                  + Constants.QUOTE_DOUBLE
                  + "height: 100px"
                  + Constants.QUOTE_DOUBLE
                  + ">");
          stringBuilderHeader.append(
              Constants.nlTab(1, tab--)
                  + "<img href="
                  + Constants.QUOTE_DOUBLE
                  + "https://www.vivit-worldwide.org/default.asp?"
                  + Constants.QUOTE_DOUBLE
                  + " src="
                  + Constants.QUOTE_DOUBLE
                  + "https://www.vivit-worldwide.org/graphics/logo.png"
                  + Constants.QUOTE_DOUBLE
                  + " alt="
                  + Constants.QUOTE_DOUBLE
                  + "Error"
                  + Constants.QUOTE_DOUBLE
                  + " style="
                  + Constants.QUOTE_DOUBLE
                  + "max-width: 100%; max-height: 100%; display:block;"
                  + " margin:auto;"
                  + Constants.QUOTE_DOUBLE
                  + " />");
          stringBuilderHeader.append(Constants.nlTab(1, tab--) + "</div>");
          stringBuilderHeader.append(Constants.nlTab(1, tab--) + "</th>");
          stringBuilderHeader.append(Constants.nlTab(1, tab--) + "</tr>");
          stringBuilderHeader.append(Constants.nlTab(1, tab) + "</table>");
          stringBuilderHeader.append(
              Constants.nlTab(1, tab++)
                  + "<table class="
                  + Constants.QUOTE_DOUBLE
                  + "scroll"
                  + Constants.QUOTE_DOUBLE
                  + " style="
                  + Constants.QUOTE_DOUBLE
                  + STYLE_BORDER
                  + Constants.QUOTE_DOUBLE
                  + ">");
          stringBuilderHeader.append(Constants.nlTab(1, tab++) + "<tr>");
          stringBuilderHeader.append(
              Constants.nlTab(1, tab++)
                  + "<th style="
                  + Constants.QUOTE_DOUBLE
                  + "border: 1px solid black; border-collapse: collapse;"
                  + " background-color: "
                  + ColorsHEX.VIVIT_GOLD.getValue()
                  + ";"
                  + Constants.QUOTE_DOUBLE
                  + ">");
          stringBuilderHeader.append(Constants.nlTab(1, tab--) + "<b>" + header + "</b>");
          stringBuilderHeader.append(Constants.nlTab(1, tab--) + "</th>");
          stringBuilderHeader.append(Constants.nlTab(1, tab--) + "</tr>");
          stringBuilderHeader.append(Constants.nlTab(1, tab) + "</table>");
          stringBuilderTable.append(stringBuilderHeader.toString());
        }
        stringBuilderTable.append(
            Constants.nlTab(1, tab++)
                + "<table class="
                + Constants.QUOTE_DOUBLE
                + "scroll"
                + Constants.QUOTE_DOUBLE
                + " style="
                + Constants.QUOTE_DOUBLE
                + STYLE_BORDER
                + Constants.QUOTE_DOUBLE
                + ">");
        stringBuilderTable.append(Constants.nlTab(1, tab++) + "<thead>");
        stringBuilderTable.append(Constants.nlTab(1, tab++) + "<tr>");
        for (final String field : fieldList) {
          stringBuilderTable.append(
              Constants.nlTab(1, tab++)
                  + "<th style="
                  + Constants.QUOTE_DOUBLE
                  + "border: 1px solid black; border-collapse: collapse;"
                  + " background-color: "
                  + ColorsHEX.VIVIT_LIGHTBLUE.getValue()
                  + ";"
                  + Constants.QUOTE_DOUBLE
                  + ">");
          stringBuilderTable.append(Constants.nlTab(1, tab--) + "<b>" + field + "</b>");
          stringBuilderTable.append(Constants.nlTab(1, tab) + "</th>");
        }
        tab--;
        stringBuilderTable.append(Constants.nlTab(1, tab--) + "</tr>");
        stringBuilderTable.append(Constants.nlTab(1, tab) + "</thead>");
        stringBuilderTable.append(Constants.nlTab(1, tab++) + "<tbody>");
      }
      stringBuilderTable.append(Constants.nlTab(1, tab++) + "<tr>");
      // List<String> fieldFormatList =
      // Arrays.asList("HrWk;WkYr;HrsMnth;HrsYr;Actual#;Remaining#".split(Constants.DELIMETER_LIST));
      List<String> fieldFormatList = new ArrayList<>(fieldList);
      for (int i = 0; i < 3; i++) {
        fieldFormatList.remove(0);
      }
      for (final String field : fieldList) {
        String value = mapReport.get(field);
        if (fieldFormatList.contains(field)) {
          // final NumberFormat numberFormat =
          // NumberFormat.getNumberInstance(Locale.US);
          // value =
          // String.valueOf(numberFormat.format(Double.valueOf(value)));
          value = JavaHelpers.formatNumber(value, "#,##0.00");
        }
        stringBuilderTable.append(
            Constants.nlTab(1, tab++)
                + "<td style="
                + Constants.QUOTE_DOUBLE
                + "border: 1px solid black; border-collapse: collapse;"
                + Constants.QUOTE_DOUBLE
                + ">");
        stringBuilderTable.append(Constants.nlTab(1, tab--) + value);
        stringBuilderTable.append(Constants.nlTab(1, tab) + "</td>");
      }
      tab--;
      stringBuilderTable.append(Constants.nlTab(1, tab) + "</tr>");
    }
    tab--;
    stringBuilderTable.append(Constants.nlTab(1, tab--) + "</tbody>");
    stringBuilderTable.append(Constants.nlTab(1, tab--) + "</table>");
    if (footer != null) {
      tab++;
      StringBuilder stringBuilderFooter = new StringBuilder();
      stringBuilderFooter.append(
          Constants.nlTab(1, tab++)
              + "<table class="
              + Constants.QUOTE_DOUBLE
              + "scroll"
              + Constants.QUOTE_DOUBLE
              + " style="
              + Constants.QUOTE_DOUBLE
              + STYLE_BORDER
              + Constants.QUOTE_DOUBLE
              + ">");
      stringBuilderFooter.append(Constants.nlTab(1, tab++) + "<tr>");
      stringBuilderFooter.append(
          Constants.nlTab(1, tab++)
              + "<td style="
              + Constants.QUOTE_DOUBLE
              + "border: 1px solid black; border-collapse: collapse;"
              + " background-color: "
              + ColorsHEX.VIVIT_GOLD.getValue()
              + ";"
              + Constants.QUOTE_DOUBLE
              + ">");
      stringBuilderFooter.append(
          Constants.nlTab(1, tab--)
              + "<font color="
              + Constants.QUOTE_DOUBLE
              + "red"
              + Constants.QUOTE_DOUBLE
              + "><b>Note:</b> </font><i>"
              + footer
              + "</i>");
      stringBuilderFooter.append(Constants.nlTab(1, tab--) + "</td>");
      stringBuilderFooter.append(Constants.nlTab(1, tab--) + "</tr>");
      stringBuilderFooter.append(Constants.nlTab(1, tab--) + "</table>");
      stringBuilderTable.append(stringBuilderFooter.toString());
    }
    stringBuilderReport.append(stringBuilderTable.toString());
    stringBuilderReport.append(Constants.nlTab(1, tab--) + "</body>");
    stringBuilderReport.append(Constants.nlTab(1, 0) + "<html>");
    final String report = stringBuilderReport.toString();
    sysOut(Constants.NEWLINE + report);
    return report;
  }

  /**
   * @param sb
   * @param tableName
   * @param fieldName
   * @return
   * @throws Throwable
   */
  public static String getUpdatedQuery(StringBuilder sb, String tableName, String fieldName)
      throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(
        ParameterHelper.getParameters(
            Arrays.asList(
                Arrays.asList("sb", sb),
                Arrays.asList(LABEL_TABLE_NAME, tableName),
                Arrays.asList("fieldName", fieldName))));
    sb.append(Constants.nlTab(0, 0) + JDBCConstants.SELECT);
    sb.append(Constants.nlTab(1, 0) + "[" + fieldName + "],");
    sb.append(Constants.nlTab(1, 0) + "Count([" + fieldName + "]) AS [Count],");
    sb.append(Constants.nlTab(1, 0) + "ROUND(Count([" + fieldName + "]))/ROUND(");
    sb.append(
        Constants.nlTab(1, 2)
            + "("
            + JDBCConstants.SELECT_COUNT_FROM
            + "["
            + tableName
            + "])) AS [Percent] ");
    sb.append(Constants.nlTab(1, 0) + JDBCConstants.FROM + "[" + tableName + "] ");
    sb.append(
        Constants.nlTab(1, 0)
            + JDBCConstants.WHERE
            + "["
            + fieldName
            + "] "
            + JDBCConstants.NOT_NULL);
    sb.append(Constants.nlTab(1, 0) + JDBCConstants.AND + "[" + fieldName + "]!='' ");
    sb.append(Constants.nlTab(1, 0) + JDBCConstants.GROUP_BY + "UPPER([" + fieldName + "]) ");
    sb.append(Constants.nlTab(1, 0) + JDBCConstants.UNION_ALL);
    sb.append(Constants.nlTab(1, 0) + JDBCConstants.SELECT);
    sb.append(Constants.nlTab(1, 0) + "'Members (Data Provided)' AS [" + fieldName + "],");
    sb.append(Constants.nlTab(1, 0) + "Count([" + fieldName + "]) AS [Count],");
    sb.append(Constants.nlTab(1, 0) + "ROUND(Count([" + fieldName + "]))/ROUND(");
    sb.append(
        Constants.nlTab(1, 2)
            + "("
            + JDBCConstants.SELECT_COUNT_FROM
            + "["
            + tableName
            + "])) AS [Percent] ");
    sb.append(Constants.nlTab(1, 0) + JDBCConstants.FROM + "[" + tableName + "] ");
    sb.append(
        Constants.nlTab(1, 0)
            + JDBCConstants.WHERE
            + "["
            + fieldName
            + "] "
            + JDBCConstants.NOT_NULL);
    sb.append(Constants.nlTab(1, 0) + JDBCConstants.AND + "[" + fieldName + "]!='' ");
    sb.append(Constants.nlTab(1, 0) + JDBCConstants.UNION_ALL);
    sb.append(Constants.nlTab(1, 0) + JDBCConstants.SELECT);
    sb.append(Constants.nlTab(1, 0) + "'All Memebers' AS [" + fieldName + "],");
    sb.append(Constants.nlTab(1, 0) + "ROUND(");
    sb.append(
        Constants.nlTab(1, 0)
            + "("
            + JDBCConstants.SELECT_COUNT_FROM
            + "["
            + tableName
            + "])) AS [Count],");
    sb.append(Constants.nlTab(1, 0) + "ROUND(1) AS [Percent] ");
    sb.append(Constants.nlTab(1, 0) + JDBCConstants.ORDER_BY + "[Count] DESC,[" + fieldName + "]");
    // sb.append(c.nlTab(1, 0) + " " + JDBCConstants.LIMIT +
    // RECORD_LIMIT_100)
    return sb.toString();
  }

  public static Map<String, String> getDatabaseUpdatedFields() {
    JDBC jdbc = new JDBC("", DATABASE_DEFINITION);
    return jdbc.getFieldNamesMap(VivitTables.VIVIT_DATABASE_CHANGES, true);
  }

  /**
   * @param list
   * @param columnName
   * @return
   */
  public static int getColumnIndex(List<String> list, String columnName) {
    for (int index = 0; index < list.size(); index++) {
      if (columnName.equals(list.get(index))) {
        return index;
      }
    }
    sysOut("Could NOT find Column Name [" + columnName + "]");
    return -1;
  }

  /**
   * @param attachment
   * @throws Throwable
   */
  public static void sendAutomationReport(String attachment) throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(
        ParameterHelper.getParameters(Arrays.asList(Arrays.asList(LABEL_ATTACHMENT, attachment))));
    final String date = new Date().toString();
    final String subject = "Vivit - Automation (Daily Statistics Report) " + date;
    final String body = VivitEnvironment.EMAIL_SIGNATURE;
    Email.sendEmail(
        getEmailFrom(),
        getEmailPassword(),
        getEmailTo(QUERY_REPORT_AUTOMATION_EMAIL),
        "",
        "",
        subject,
        body,
        attachment);
  }

  /**
   * @param embeddedReport
   * @param attachment
   * @throws Throwable
   */
  public static void sendAutomationReport(String embeddedReport, String attachment)
      throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(
        ParameterHelper.getParameters(
            Arrays.asList(
                Arrays.asList("embeddedReport", embeddedReport),
                Arrays.asList(LABEL_ATTACHMENT, attachment))));
    final String date = new Date().toString();
    final String subject = "Vivit - Automation (Daily Report) " + date;
    String body = embeddedReport;
    body += VivitEnvironment.EMAIL_SIGNATURE;
    Email.sendEmail(
        getEmailFrom(),
        getEmailPassword(),
        getEmailTo(QUERY_REPORT_AUTOMATION_EMAIL),
        "",
        "",
        subject,
        body,
        attachment);
  }

  @Test
  public void sendAutomationReportsTest() throws Throwable {
    String embeddedReport = FSOTests.fileReadAll(VivitFoldersFiles.REPORT_HTM_AUTOMATION);
    List<String> listReports =
        Arrays.asList(
            VivitFoldersFiles.REPORT_XLS_AUTOMATION_A, VivitFoldersFiles.REPORT_XLS_AUTOMATION_S);
    sendAutomationReports(embeddedReport, listReports);
  }

  /**
   * @param embeddedReport
   * @param listReports
   * @throws Throwable
   */
  public static void sendAutomationReports(String embeddedReport, List<String> listReports)
      throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(
        ParameterHelper.getParameters(
            Arrays.asList(
                Arrays.asList("embeddedReport", embeddedReport),
                Arrays.asList("listReports", listReports))));
    final String date = new Date().toString();
    final String subject = "Vivit - Automation (Daily Reports) " + date;
    String body = embeddedReport; // + Constants.NEWLINE
    body += VivitEnvironment.EMAIL_SIGNATURE;
    Email.sendEmail(
        getEmailFrom(),
        getEmailPassword(),
        getEmailTo(QUERY_REPORT_AUTOMATION_EMAIL),
        "",
        "",
        subject,
        body,
        listReports);
  }

  /**
   * @param body
   * @param attachment
   * @throws Throwable
   */
  public static void sendTreasurersReport(String body, String attachment) throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(
        ParameterHelper.getParameters(
            Arrays.asList(
                Arrays.asList("body", body), Arrays.asList(LABEL_ATTACHMENT, attachment))));
    final String date = new Date().toString();
    final String subject = "Vivit - Treasurer (Monthly Report) " + date;
    body += VivitEnvironment.EMAIL_SIGNATURE;
    Email.sendEmail(
        getEmailFrom(),
        getEmailPassword(),
        EMAIL_ADDRESS_DIRECTORS,
        "",
        "",
        subject,
        body,
        attachment);
  }

  public static void compactDatabase() throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList()));
    sysOut("Compacting:[" + VivitFoldersFiles.DATABASE + "]");
    final String compactCommand = "VACUUM;";
    final String command =
        "CMD /C "
            + Constants.QUOTE_DOUBLE
            + Constants.QUOTE_DOUBLE
            + Constants.PATH_SQLITE3_FILE
            + Constants.QUOTE_DOUBLE
            + " "
            + Constants.QUOTE_DOUBLE
            + VivitFoldersFiles.DATABASE
            + Constants.QUOTE_DOUBLE
            + " "
            + Constants.QUOTE_DOUBLE
            + ""
            + compactCommand
            + Constants.QUOTE_DOUBLE
            + Constants.QUOTE_DOUBLE
            + "";
    sysOut("command:[" + command + "]");
    long databaseSize = FSOTests.fileSize(VivitFoldersFiles.DATABASE);
    sysOut("[databaseSize], [" + databaseSize + "] bytes");
    final int retunCode = CommandLineTests.runProcess(command);
    sysOut("retunCode:[" + retunCode + "]");
    databaseSize = new File(VivitFoldersFiles.DATABASE).length();
    sysOut("[databaseSize], [" + databaseSize + "] bytes");
  }

  @Test
  public void createReportsAutomationTest() throws Throwable {
    createReportHTMLAutomation(false);
    sysOut("DONE!");
  }

  /**
   * @param sendEmail
   * @param overideMonthDay
   * @throws Throwable
   */
  public static void createReports(boolean sendEmail, boolean overideMonthDay) throws Throwable {
    createReportHTMLBillableHours(sendEmail, overideMonthDay);
    createReportHTMLTreasurer(sendEmail, overideMonthDay);
    createReportHTMLAutomation(sendEmail);
  }

  /**
   * @param sendEmail
   * @throws Throwable
   */
  public static void createReportHTMLAutomation(boolean sendEmail) throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(
        ParameterHelper.getParameters(Arrays.asList(Arrays.asList(LABEL_SEND_EMAIL, sendEmail))));
    if (!FSOTests.fileExists(VivitFoldersFiles.PATH_API_DATA_YM)) {
      FSOTests.folderCreate(VivitFoldersFiles.PATH_API_DATA_YM);
    }
    final List<String> listReports = new ArrayList<>();
    listReports.add(VivitFoldersFiles.REPORT_XLS_AUTOMATION_A);
    String embeddedReport =
        createReportAutomation(VivitFoldersFiles.REPORT_XLS_AUTOMATION_A, false);
    getHTMLMemberChanges();
    final String htmlMemberChanges =
        FSOTests.fileReadAll(VivitFoldersFiles.REPORT_HTM_AUTOMATION_MEMBER_CHANGES);
    embeddedReport = embeddedReport.replace("LABEL_REPLACE_MEMBER_CHANGES", htmlMemberChanges);
    getHTMLYMGTWEvents();
    final String htmlYMEvents =
        FSOTests.fileReadAll(VivitFoldersFiles.REPORT_HTM_AUTOMATION_EVENTS_YM);
    embeddedReport = embeddedReport.replace("LABEL_REPLACE_EVENTS_YM", htmlYMEvents);
    getHTMLYMGTWEventAttendees();
    // final String htmlYMEventAttendees = FSO
    // .fileReadAll(VivitFoldersFiles.REPORT_AUTOMATION_EVENT_ATTENDEES_YM);
    // embeddedReport =
    // embeddedReport.replace("LABEL_REPLACE_EVENT_ATTENDEES_YM",
    // htmlYMEventAttendees);
    getHTMLYMGTWEventRegistration();
    // final String htmlYMEventRegistration = FSO
    // .fileReadAll(VivitFoldersFiles.REPORT_AUTOMATION_EVENT_REGISTRATION_YM);
    // embeddedReport =
    // embeddedReport.replace("LABEL_REPLACE_EVENT_REGISTRATION_YM",
    // htmlYMEventRegistration);
    getHTMLBrokenLinks();
    final String htmlBrokenLinks =
        FSOTests.fileReadAll(VivitFoldersFiles.REPORT_HTM_AUTOMATION_BROKEN_LINKS);
    embeddedReport = embeddedReport.replace("LABEL_REPLACE_BROKEN_LINKS", htmlBrokenLinks);
    getHTMLDatabaseChanges();
    final String htmlDatabaseChanges =
        FSOTests.fileReadAll(VivitFoldersFiles.REPORT_HTM_AUTOMATION_DATABASE_CHANGES);
    String embeddedReportTemp = embeddedReport;
    embeddedReportTemp =
        embeddedReportTemp.replace("LABEL_REPLACE_DATABASE_CHANGES", htmlDatabaseChanges);
    embeddedReportTemp =
        embeddedReportTemp.replace("LABEL_REPLACE_API_VERSION_YM", YMService.API_VERSION);
    embeddedReportTemp =
        embeddedReportTemp.replace(
            "LABEL_REPLACE_API_VERSION_GTW", GTWebinarServiceTests.API_VERSION);
    //
    String htmlTestInformation =
        FSOTests.fileReadAll(VivitFoldersFiles.REPORT_HTM_AUTOMATION_TEST_INFORMATION);
    embeddedReportTemp += htmlTestInformation;
    //
    // Replace Header & html tags added from individual reports.
    embeddedReportTemp = embeddedReportTemp.replaceAll("&amp;", "&");
    embeddedReportTemp = embeddedReportTemp.replaceAll("&", "&amp;");
    // embeddedReportTemp = embeddedReportTemp.replace(HTML.HEADER, "");
    // embeddedReportTemp = embeddedReportTemp.replaceAll(HTML.HEADER, "");
    embeddedReportTemp = embeddedReportTemp.replaceAll("<html>", "");
    embeddedReportTemp = embeddedReportTemp.replaceAll("</html>", "");
    embeddedReport = "<html>" + embeddedReportTemp + "</html>";
    embeddedReport = XML.formatPretty(embeddedReport);
    embeddedReportTemp = embeddedReport;
    embeddedReport = HTML.HEADER + Constants.NEWLINE + embeddedReportTemp;
    sysOut("embeddedReport:[" + embeddedReport + "]");
    FSOTests.fileWrite(VivitFoldersFiles.REPORT_HTM_AUTOMATION, embeddedReport, false);
    listReports.add(VivitFoldersFiles.REPORT_XLS_AUTOMATION_S);
    listReports.add(VivitFoldersFiles.DATA_YMAPI_DATA);
    // createReportAutomationStatistics(VivitFoldersFiles.REPORT_XLS_AUTOMATION_S,
    // false)
    String dayTomorrow = DateHelpersTests.getCurrentDatePlusMinusDays(DateHelpersTests.FORMAT_D, 1);
    if ("1".equals(dayTomorrow)) {
      createReportAutomationStatisticsForPeriod(
          VivitViews.VIVIT_MEMBER_CHANGE_MONTH, VivitFoldersFiles.REPORT_XLS_AUTOMATION_S_MONTH);
      listReports.add(VivitFoldersFiles.REPORT_XLS_AUTOMATION_S_MONTH);
    }
    String yearToday = DateHelpersTests.getCurrentDateTime(DateHelpersTests.FORMAT_YYYY);
    String yearTomorrow =
        DateHelpersTests.getCurrentDatePlusMinusDays(DateHelpersTests.FORMAT_YYYY, 1);
    if (!yearTomorrow.equals(yearToday)) {
      createReportAutomationStatisticsForPeriod(
          VivitViews.VIVIT_MEMBER_CHANGE_YEAR, VivitFoldersFiles.REPORT_XLS_AUTOMATION_S_YEAR);
      listReports.add(VivitFoldersFiles.REPORT_XLS_AUTOMATION_S_YEAR);
    }
    if (sendEmail) {
      sendAutomationReports(embeddedReport, listReports);
    }
  }

  /**
   * @param filePathName
   * @param sendEmail
   * @return
   * @throws Throwable
   */
  public static String createReportAutomation(String filePathName, boolean sendEmail)
      throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    LOG.debug(
        "{}",
        ParameterHelper.getParameters(
            Arrays.asList(
                Arrays.asList(LABEL_FILE_PATH_NAME, filePathName),
                Arrays.asList(LABEL_SEND_EMAIL, sendEmail))));
    FSOTests.fileDelete(filePathName);
    JDBC jdbc = new JDBC("", DATABASE_DEFINITION);
    final Map<String, String> reportMap = new HashMap<>();
    StringBuilder stringBuilder;
    // stringBuilder.append(JDBCConstants.SELECT +
    // "[Name],[Timing
    // (seconds)]
    // ")
    // stringBuilder.append(JDBCConstants.FROM +
    // "["+VivitViews.VIVIT_GROUPPAGE_TIMINGS_SECONDS+"] ")
    // stringBuilder.append(JDBCConstants.WHERE + "[Type]='" +
    // groupType
    // +
    // "S'
    // ")
    // stringBuilder.append(JDBCConstants.ORDER_BY + "[Timing
    // (seconds)]
    // "
    // +
    // sort + " ")
    // stringBuilder.append(JDBCConstants.LIMIT + "1")
    // final String result =
    // jdbc.queryResults(stringBuilder.toString(), " -
    // ", false)
    // mapReport.put(type + "_" + groupType, result)
    final List<String> groupTypes = Arrays.asList("LUG", "SIG");
    final List<String> groupSorts = Arrays.asList("ASC;FASTEST", "DESC;SLOWEST");
    for (final String groupType : groupTypes) {
      for (final String groupSort : groupSorts) {
        final String[] typeSort = groupSort.split(Constants.DELIMETER_LIST);
        final String sort = typeSort[0];
        final String type = typeSort[1];
        stringBuilder = new StringBuilder();
        stringBuilder.append(JDBCConstants.SELECT + "[Name],[Timing (seconds)] ");
        stringBuilder.append(
            JDBCConstants.FROM + "[" + VivitViews.VIVIT_GROUPPAGE_TIMINGS_SECONDS + "] ");
        stringBuilder.append(JDBCConstants.WHERE + "[Type]='" + groupType + "S' ");
        stringBuilder.append(JDBCConstants.AND + "[Timing (seconds)] != 0 ");
        stringBuilder.append(JDBCConstants.ORDER_BY + "[Timing (seconds)] " + sort + " ");
        stringBuilder.append(JDBCConstants.LIMIT + "1;");
        final String result = jdbc.queryResults(stringBuilder.toString(), " - ", false);
        reportMap.put(type + "_" + groupType, result);
      }
    }
    stringBuilder = new StringBuilder();
    stringBuilder.append(JDBCConstants.SELECT + "[Name], [Label] ");
    stringBuilder.append(JDBCConstants.FROM + "[" + VivitTables.VIVIT_REPORTS_AUTOMATION + "] ");
    stringBuilder.append(JDBCConstants.ORDER_BY + "[Sort];");
    final List<Map<String, String>> reportListMap =
        jdbc.queryResultsString(stringBuilder.toString(), false);
    int sheetLinkIndex = 1;
    for (final Map<String, String> tableMap : reportListMap) {
      final String viewName = tableMap.get("Name");
      final String labelName = tableMap.get(LABEL_LABEL);
      tableMap.put(viewName, labelName);
      reportMap.put(viewName, "0");
      final String sheetLinkName = labelName;
      sysOut(
          "viewName:["
              + viewName
              + "], labelName:["
              + labelName
              + "], sheetLinkName:["
              + sheetLinkName
              + "]");
      final String sql = JDBCConstants.SELECT_ALL + JDBCConstants.FROM + "[" + viewName + "]";
      final String data = jdbc.queryResults(sql, Constants.DELIMETER_LIST, true);
      sysOut("data:[" + data + "]");
      if (!"".equals(data)) {
        final String[] records = data.split(Constants.NEWLINE);
        reportMap.put(viewName, String.valueOf(records.length - 1));
        if (records.length > 1) {
          sheetLinkIndex++;
          final String sheetName = "S" + JavaHelpers.formatNumber(sheetLinkIndex, "000");
          final XLS excel = IExcel.updateSummarySheetXLS(filePathName, sheetName, sheetLinkName);
          int column = 0;
          // int row = (excel.getRowCount(sheetName))
          int row = 1;
          boolean firstRecord = true;
          for (final String record : records) {
            if (!"".equals(record)) {
              final String[] values = record.split(Constants.DELIMETER_LIST);
              for (final String value : values) {
                if (firstRecord) {
                  excel.writeCell(sheetName, column, row, value);
                  excel.setFormatHeading(sheetName, column, row);
                } else {
                  excel.writeCell(sheetName, column, row, value);
                }
                column++;
              }
              firstRecord = false;
              column = 0;
              row++;
            }
          }
          excel.autoSizeColumns(sheetName);
          excel.writeCell(IExcel.SHEET_SUMMARY, 0, 0, excel.readCell(IExcel.SHEET_SUMMARY, 0, 0));
          excel.close();
        }
      }
    }
    jdbc.close();
    String embeddedReport = FSOTests.fileReadAll(VivitFoldersFiles.REPORT_HTM_AUTOMATION_TEMPLATE);
    for (Entry<String, String> entry : reportMap.entrySet()) {
      String key = entry.getKey();
      final String replacee = key.replace(VivitViews.PREFIX, "");
      final String replacer = reportMap.get(key);
      embeddedReport = embeddedReport.replace(replacee, replacer);
    }
    embeddedReport = XML.formatPretty(embeddedReport);
    FSOTests.fileWrite(VivitFoldersFiles.REPORT_HTM_AUTOMATION, embeddedReport, false);
    if (sendEmail) {
      sendAutomationReport(embeddedReport, VivitFoldersFiles.REPORT_HTM_AUTOMATION);
    }
    return embeddedReport;
  }

  /**
   * @param filePathName
   * @param sendEmail
   * @throws Throwable
   */
  public static void createReportAutomationStatistics(String filePathName, boolean sendEmail)
      throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    LOG.debug(
        "{}",
        ParameterHelper.getParameters(
            Arrays.asList(
                Arrays.asList(LABEL_FILE_PATH_NAME, filePathName),
                Arrays.asList(LABEL_SEND_EMAIL, sendEmail))));
    FSOTests.fileDelete(filePathName);
    JDBC jdbc = new JDBC("", DATABASE_DEFINITION);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(JDBCConstants.SELECT_ALL);
    // [Dynamic,][QueryName],[Query]")
    stringBuilder.append(JDBCConstants.FROM + "[" + VivitTables.VIVIT_QUERIES + "] ");
    stringBuilder.append(JDBCConstants.WHERE + "[UseReport]=1;");
    final List<Map<String, String>> reportListMap =
        jdbc.queryResultsString(stringBuilder.toString(), false);
    int sheetLinkIndex = 1;
    for (final Map<String, String> tableMap : reportListMap) {
      final String queryName = tableMap.get("QueryName");
      final String fieldName = queryName.substring(2);
      final String sheetLinkName = fieldName;
      // if (sheetName.length() > IExcel.MAX_SHEET_NAME_LENGTH)
      // {
      // sheetName = sheetName.replace("Micro_Focus", "MF")
      // }
      final String dynamicQuery = tableMap.get("Dynamic");
      String sql = tableMap.get("Query");
      if ("1".equals(dynamicQuery)) {
        StringBuilder sqlStringBuilder = new StringBuilder();
        sql = getUpdatedQuery(sqlStringBuilder, VivitTables.DOM_VIVIT_MEMBERS, fieldName);
        sqlStringBuilder = new StringBuilder();
        sqlStringBuilder.append(JDBCConstants.UPDATE + "[" + VivitTables.VIVIT_QUERIES + "] ");
        sqlStringBuilder.append(JDBCConstants.SET + "[Query]='" + sql.replace("'", "''") + "' ");
        sqlStringBuilder.append(JDBCConstants.WHERE + "[QueryName]='" + queryName + "';");
        final int recordsUpdated =
            SQL.executeVivit(
                "Update", VivitTables.VIVIT_QUERIES + "-" + queryName, sqlStringBuilder);
        if (recordsUpdated != 1) {
          sysOut(recordsUpdated);
        }
      }
      LOG.debug("sql:[{}], queryName:[{}], sheetLinkName:[{}]", sql, queryName, sheetLinkName);
      final String data = jdbc.queryResults(sql, Constants.DELIMETER_LIST, true);
      if (!"".equals(data)) {
        final String[] records = data.split(Constants.NEWLINE);
        final String[] fields = records[0].split(Constants.DELIMETER_LIST);
        if ("0".equals(dynamicQuery) || (records.length > 1 && records.length < RECORD_LIMIT_100)) {
          sysOut("records.length:[" + records.length + "]");
          sheetLinkIndex++;
          final String sheetName = "S" + JavaHelpers.formatNumber(sheetLinkIndex, "000");
          final XLS excel = IExcel.updateSummarySheetXLS(filePathName, sheetName, sheetLinkName);
          int column = 0;
          // int row = (excel.getRowCount(sheetName))
          int row = 1;
          boolean firstRecord = true;
          for (final String record : records) {
            if ("".equals(record)) {
              continue; // Guard clause - skip empty records
            }

            final String[] values = record.split(Constants.DELIMETER_LIST);
            for (int index = 0; index < values.length; index++) {
              String value = values[index];
              final String columnName = fields[index];

              if (firstRecord) {
                writeExcelHeader(excel, sheetName, column, row, value);
              } else {
                writeExcelDataCell(excel, sheetName, column, row, columnName, value, dynamicQuery);
              }
              column++;
            }
            firstRecord = false;
            column = 0;
            row++;
          }
          excel.autoSizeColumns(sheetName);
          excel.writeCell(IExcel.SHEET_SUMMARY, 0, 0, excel.readCell(IExcel.SHEET_SUMMARY, 0, 0));
          excel.close();
          // if (dynamicQuery.equals("1"))
          // {
          // XLSBarChart xlsBarChart = new XLSBarChart(filePathName,
          // sheetName)
          // xlsBarChart.create(2, 4)
          // }
        }
      }
    }
    jdbc.close();
    if (sendEmail) {
      sendAutomationReport(filePathName);
    }
  }

  /** Write Excel header cell with formatting. Extracted method to reduce nesting depth. */
  private static void writeExcelHeader(
      XLS excel, String sheetName, int column, int row, String value) throws QAException {
    excel.writeCell(sheetName, column, row, value);
    excel.setFormatHeading(sheetName, column, row);
  }

  /**
   * Write Excel data cell with conditional formatting based on column type. Extracted method to
   * reduce nesting depth.
   */
  private static void writeExcelDataCell(
      XLS excel,
      String sheetName,
      int column,
      int row,
      String columnName,
      String value,
      String dynamicQuery)
      throws QAException {
    switch (columnName) {
      case "Count":
      case "Year":
      case "Month":
      case "Day":
      case "Last Login":
      case "Last Updated":
      case "Last Approved":
      case "Last Registered":
        String numericValue = handleNumericColumn(value);
        excel.writeCell(sheetName, column, row, Double.valueOf(numericValue));
        break;
      case "Percent":
        String percentValue = handlePercentColumn(value, dynamicQuery);
        excel.writeCell(sheetName, column, row, percentValue);
        break;
      default:
        excel.writeCell(sheetName, column, row, value);
        break;
    }
  }

  /**
   * Handle numeric column values (convert "null" to "0"). Extracted method to reduce nesting depth.
   */
  private static String handleNumericColumn(String value) {
    return "null".equals(value) ? "0" : value;
  }

  /**
   * Handle percent column values with conditional formatting. Extracted method to reduce nesting
   * depth.
   */
  private static String handlePercentColumn(String value, String dynamicQuery) {
    final String processedValue = "null".equals(value) ? "0" : value;

    if ("0".equals(dynamicQuery)) {
      return JavaHelpers.formatNumber(processedValue, "0.0000%");
    } else {
      return JavaHelpers.formatNumber(processedValue, "0.00%");
    }
  }

  /**
   * @param filePathName
   * @param sendEmail
   * @throws Throwable
   */
  public static void createReportAutomationStatisticsForPeriod(String view, String filePathName)
      throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(
        ParameterHelper.getParameters(
            Arrays.asList(Arrays.asList(LABEL_FILE_PATH_NAME, filePathName))));
    FSOTests.fileDelete(filePathName);
    JDBC jdbc = new JDBC("", DATABASE_DEFINITION);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(JDBCConstants.SELECT_ALL);
    stringBuilder.append(
        JDBCConstants.FROM + "[" + VivitTables.VIVIT_REPORT_FIELDS_STATISTIICAL + "] ");
    stringBuilder.append(JDBCConstants.ORDER_BY + "[Order];");
    final List<Map<String, String>> reportListMap =
        jdbc.queryResultsString(stringBuilder.toString(), false);
    int newMemberCount =
        Integer.parseInt(jdbc.queryResults("select count() from [" + view + "];", "", false));
    String sheetSummary = IExcel.SHEET_SUMMARY;
    int sheetLinkIndex = 1;
    for (final Map<String, String> tableMap : reportListMap) {
      final String fieldName = tableMap.get("FieldName");
      final String sheetLinkName = fieldName;
      String sql = getQueryForPeriodField(view, fieldName);
      LOG.debug("sql:[{}], fieldName:[{}], sheetLinkName:[{}]", sql, fieldName, sheetLinkName);
      List<Map<String, String>> recordListMap = jdbc.queryResultsString(sql, true);
      double recordCount = recordListMap.size() - 1;
      // if (recordListMap.size() > 1 && recordCount < RECORD_LIMIT_100)
      if (recordListMap.size() > 1) {
        sysOut("Record Count:[" + recordCount + "]");
        sheetLinkIndex++;
        final String sheetName = "S" + JavaHelpers.formatNumber(sheetLinkIndex, "000");
        final XLS excel = IExcel.updateSummarySheetXLS(filePathName, sheetName, sheetLinkName);
        int majorityRow = 0;
        excel.writeCell(sheetSummary, 1, majorityRow, "Majority");
        excel.setFormatHeading(sheetSummary, 1, majorityRow);
        excel.writeCell(sheetSummary, 2, majorityRow, "Count");
        excel.setFormatHeading(sheetSummary, 2, majorityRow);
        excel.writeCell(sheetSummary, 3, majorityRow, "Percent");
        excel.setFormatHeading(sheetSummary, 3, majorityRow);
        excel.writeCell(sheetSummary, 4, majorityRow, newMemberCount);
        int column = 0;
        // int row = (excel.getRowCount(sheetName))
        int row = 1;
        for (int recordListMapIndex = 0;
            recordListMapIndex < recordListMap.size();
            recordListMapIndex++) {
          Map<String, String> recordMap = recordListMap.get(recordListMapIndex);
          if (recordListMapIndex == 0) {
            excel.writeCell(sheetName, column, row, recordMap.get(fieldName));
            excel.setFormatHeading(sheetName, column, row);
            column++;
            excel.writeCell(sheetName, column, row, recordMap.get("Count"));
            excel.setFormatHeading(sheetName, column, row);
            // excel.writeCell(sheetName, column, row, sql);
            // excel.writeCell(sheetName, column, (row - 1), "SQL");
            // excel.setFormatHeading(sheetName, column, (row - 1));
            // excel.addComment(sheetName, column, (row - 1), sql,
            // false);
            excel.addComment(sheetName, 0, 0, sql, false);
          } else {
            String count = recordMap.get("Count");
            if (recordListMapIndex == 1) {
              majorityRow = excel.getRowCount(sheetSummary);
              excel.writeCell(sheetSummary, 1, majorityRow, recordMap.get(fieldName));
              excel.writeCell(sheetSummary, 2, majorityRow, Double.valueOf(count));
              String percent =
                  JavaHelpers.formatNumber(
                      Double.valueOf(newMemberCount / Double.valueOf(count)), "0.0000%");
              excel.writeCell(sheetSummary, 3, majorityRow, percent);
              excel.writeCellFormula(
                  sheetSummary, 3, majorityRow, "SUM(C" + (majorityRow + 1) + "/E$1)");
            }
            excel.writeCell(sheetName, column, row, recordMap.get(fieldName));
            column++;
            excel.writeCell(sheetName, column, row, Double.valueOf(count));
          }
          column = 0;
          row++;
        }
        excel.autoSizeColumns(sheetName);
        excel.writeCell(sheetSummary, 0, 0, excel.readCell(sheetSummary, 0, 0));
        excel.close();
      }
    }
    jdbc.close();
  }

  private static String getQueryForPeriodField(String view, String fieldName) {
    StringBuilder sqlStringBuilderSQL = new StringBuilder();
    sqlStringBuilderSQL.append("select [" + fieldName + "],[Count] from");
    sqlStringBuilderSQL.append(Constants.NEWLINE);
    sqlStringBuilderSQL.append(
        "(select ["
            + fieldName
            + "],count() as [Count] from ["
            + view
            + "] where ["
            + fieldName
            + "] not null and ["
            + fieldName
            + "]!='' group by ["
            + fieldName
            + "]");
    sqlStringBuilderSQL.append(Constants.NEWLINE);
    sqlStringBuilderSQL.append("union all");
    sqlStringBuilderSQL.append(Constants.NEWLINE);
    sqlStringBuilderSQL.append(
        "select '<EMPTY>' as ["
            + fieldName
            + "],(select count() from ["
            + view
            + "] where ["
            + fieldName
            + "] is null or ["
            + fieldName
            + "]='') as [Count])");
    sqlStringBuilderSQL.append(Constants.NEWLINE);
    sqlStringBuilderSQL.append("group by [" + fieldName + "]");
    sqlStringBuilderSQL.append(Constants.NEWLINE);
    sqlStringBuilderSQL.append("order by [Count] desc;");
    return sqlStringBuilderSQL.toString();
  }

  /**
   * @param sendEmail
   * @param overideMonthDay
   * @throws Throwable
   */
  public static void createReportHTMLBillableHours(boolean sendEmail, boolean overideMonthDay)
      throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(
        ParameterHelper.getParameters(
            Arrays.asList(Arrays.asList("overideMonthDay", overideMonthDay))));
    final String monthDay = DateHelpersTests.getCurrentDateTime(DateHelpersTests.FORMAT_DD);
    if (!REPORT_DAY_BILLABLE_HOURS.equals(monthDay) && !overideMonthDay) {
      return;
    }
    JDBC jdbc = new JDBC("", DATABASE_DEFINITION);
    final String sql =
        JDBCConstants.SELECT_ALL
            + JDBCConstants.FROM
            + "["
            + VivitViews.VIVIT_CONTRACTORS_BILLABLE_HOURS
            + "];";
    final List<Map<String, String>> reportListMap = jdbc.queryResultsString(sql, false);
    final List<String> fieldList =
        jdbc.getFieldNamesList(VivitViews.VIVIT_CONTRACTORS_BILLABLE_HOURS, true);
    final List<String> listEmail = new ArrayList<>();
    sysOut("reportListMap:[" + reportListMap + "]");
    for (final Map<String, String> mapContractor : reportListMap) {
      sysOut("mapContractor:[" + mapContractor + "]");
      final String eMail = mapContractor.get("EMail");
      if (!listEmail.contains(eMail)) {
        listEmail.add(eMail);
      }
    }
    sysOut("listEmail:[" + listEmail + "]");
    final String date = new Date().toString();
    final String billableMonth = DateHelpersTests.getCurrentDateTime("MMMM");
    final String header =
        "Approved Billable Hours for the month of " + billableMonth + " (" + date + ")";
    final String footer =
        "Any additional hours need approval and requests for approval should be submitted"
            + " to the President and Treasurer within a week of the end of the month the"
            + " request is for.";
    String report = getBillableHoursTable(header, footer, fieldList, reportListMap);
    FSOTests.fileWrite(VivitFoldersFiles.REPORT_HTM_AUTOMATION_BILLABLE_HOURS, report, false);
    final String subject =
        "Vivit - Billable Hours (for the month of " + billableMonth + ") " + date;
    String body = report; // + Constants.NEWLINE
    body += VivitEnvironment.EMAIL_SIGNATURE;
    final String attachment = "";
    if (sendEmail) {
      Email.sendEmail(
          getEmailFrom(),
          getEmailPassword(),
          getEmailTo(QUERY_REPORT_BILLABLE_HOURS_EMAIL),
          "",
          "",
          subject,
          body,
          attachment);
    }
  }

  /**
   * @param sendEmail
   * @param overideMonthDay
   * @throws Throwable
   */
  public static void createReportHTMLTreasurer(boolean sendEmail, boolean overideMonthDay)
      throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(
        ParameterHelper.getParameters(
            Arrays.asList(
                Arrays.asList(LABEL_SEND_EMAIL, sendEmail),
                Arrays.asList("overideMonthDay", overideMonthDay))));
    final String monthDay = DateHelpersTests.getCurrentDateTime(DateHelpersTests.FORMAT_DD);
    if (!REPORT_DAY_TREASURER.equals(monthDay) && !overideMonthDay) {
      return;
    }
    FSOTests.fileDelete(VivitFoldersFiles.REPORT_XLS_AUTOMATION_T);
    JDBC jdbc = new JDBC("", DATABASE_DEFINITION);
    final List<String> fieldStandardTableList =
        jdbc.getFieldNamesList(VivitViews.VIVIT_CONTRACTOR_HOURS_BTA, true);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(JDBCConstants.SELECT + "[h].* ");
    stringBuilder.append(
        JDBCConstants.FROM + "[" + VivitViews.VIVIT_CONTRACTOR_HOURS_BTA_HIGHLEVEL + "] [h]");
    stringBuilder.append(
        JDBCConstants.INNER_JOIN + "[" + VivitViews.VIVIT_REPORT_TREASURER_YEARS + "] [y] ");
    stringBuilder.append(JDBCConstants.ON + "SUBSTR([h].[Year_Month],1,4)=[y].[Years]");
    List<Map<String, String>> reportListMap =
        jdbc.queryResultsString(stringBuilder.toString(), false);
    final HTML html =
        new HTML(
            VivitFoldersFiles.REPORT_HTM_AUTOMATION_TREASURER,
            ColorsHEX.VIVIT_GRAY.getValue(),
            ColorsHEX.VIVIT_LIGHTBLUE.getValue());
    StringBuilder stringBuilderReport = new StringBuilder();
    stringBuilderReport.append(
        html.createStandardTableLogo(VivitEnvironment.URL_LOGIN, LOGO_SOURCE, 0));
    FSOTests.fileWrite(
        VivitFoldersFiles.REPORT_HTM_AUTOMATION_TREASURER, stringBuilderReport.toString(), false);
    stringBuilderReport.append(
        Constants.NEWLINE
            + html.createStandardTable(
                Arrays.asList("Years"),
                jdbc.queryResultsString(
                    JDBCConstants.SELECT_ALL
                        + JDBCConstants.FROM
                        + "["
                        + VivitViews.VIVIT_REPORT_TREASURER_YEARS
                        + "]",
                    false)));
    FSOTests.fileWrite(
        VivitFoldersFiles.REPORT_HTM_AUTOMATION_TREASURER, stringBuilderReport.toString(), false);
    final List<String> fieldNumericList =
        Arrays.asList("Budgeted#;Actual#;Difference#".split(Constants.DELIMETER_LIST));
    final List<String> fieldCurrencyList =
        Arrays.asList("Budgeted$;Actual$;Difference$".split(Constants.DELIMETER_LIST));
    stringBuilderReport.append(
        Constants.NEWLINE
            + html.createStandardTable(
                fieldStandardTableList, fieldNumericList, fieldCurrencyList, reportListMap));
    FSOTests.fileWrite(
        VivitFoldersFiles.REPORT_HTM_AUTOMATION_TREASURER, stringBuilderReport.toString(), false);
    // ****************************************************************************************************
    stringBuilderReport.append(createReportHTMLTreasurerContractors(jdbc, html));
    FSOTests.fileWrite(
        VivitFoldersFiles.REPORT_HTM_AUTOMATION_TREASURER, stringBuilderReport.toString(), false);
    // ****************************************************************************************************
    stringBuilder = new StringBuilder();
    stringBuilder.append(JDBCConstants.SELECT + "[tbl_name] ");
    stringBuilder.append(JDBCConstants.FROM + "[" + VivitTables.VIVIT_REPORTS_TREASURER + "]");
    reportListMap = jdbc.queryResultsString(stringBuilder.toString(), false);
    for (final Map<String, String> tableMap : reportListMap) {
      final String viewName = tableMap.get("tbl_name");
      stringBuilder = new StringBuilder();
      stringBuilder.append(JDBCConstants.SELECT + "Count(*) ");
      stringBuilder.append(JDBCConstants.FROM + "[" + viewName + "]");
      final String recordCount =
          jdbc.queryResults(stringBuilder.toString(), Constants.DELIMETER_LIST, false);
      stringBuilder = new StringBuilder();
      stringBuilder.append(JDBCConstants.UPDATE + "[" + VivitTables.VIVIT_REPORTS_TREASURER + "] ");
      stringBuilder.append(JDBCConstants.SET + "[Record_Count]=" + recordCount + " ");
      stringBuilder.append(JDBCConstants.WHERE + "[tbl_name]='" + viewName + "'");
      int recordsAffected = 0;
      try {
        recordsAffected = jdbc.executeUpdate(stringBuilder.toString(), false);
      } catch (final Exception e) {
        sysOut(e);
      }
      sysOut("recordsAffected:[" + recordsAffected + "]");
    }
    stringBuilder = new StringBuilder();
    stringBuilder.append(JDBCConstants.SELECT_ALL);
    stringBuilder.append(JDBCConstants.FROM + "[" + VivitTables.VIVIT_REPORTS_TREASURER + "] ");
    stringBuilder.append(JDBCConstants.ORDER_BY + "[Record_Count] DESC");
    reportListMap = jdbc.queryResultsString(stringBuilder.toString(), false);
    sysOut("reportListMap:[" + reportListMap.toString() + "]");
    for (final Map<String, String> mapVIEW : reportListMap) {
      final String viewName = mapVIEW.get("tbl_name");
      final String labelName = mapVIEW.get(LABEL_LABEL);
      final String recordCount = mapVIEW.get("Record_Count");
      final String sheetName = labelName;
      final String sql =
          JDBCConstants.SELECT
              + "[h].* "
              + JDBCConstants.FROM
              + "["
              + viewName
              + "]"
              + " [h] "
              + JDBCConstants.INNER_JOIN
              + "["
              + VivitViews.VIVIT_REPORT_TREASURER_YEARS
              + "] [y] "
              + JDBCConstants.ON
              + "SUBSTR([h].[Year_Month],1,4)=[y].[Years]";
      final String data = jdbc.queryResults(sql, Constants.DELIMETER_LIST, true);
      sysOut("data:[" + data + "]");
      if (!"".equals(data)) {
        final String[] records = data.split(Constants.NEWLINE);
        final String[] fields = records[0].split(Constants.DELIMETER_LIST);
        if (records.length > 1) {
          final XLS excel = new XLS(VivitFoldersFiles.REPORT_XLS_AUTOMATION_T, sheetName);
          int column = 0;
          int row = excel.getRowCount(sheetName);
          if (row == 1) {
            row--;
          }
          boolean firstRecord = true;
          for (final String record : records) {
            if (!"".equals(record)) {
              final String[] values = record.split(Constants.DELIMETER_LIST);
              for (int index = 0; index < values.length; index++) {
                final String value = values[index];
                final String fieldName = fields[index];
                if (firstRecord) {
                  // if (!headersWritten)
                  // {
                  excel.writeCell(sheetName, column, row, value);
                  excel.setFormatHeading(sheetName, column, row);
                  excel.writeCell(
                      sheetName,
                      column + 1,
                      row,
                      records.length - 1 + " Records of (" + recordCount + ")");
                  // headersWritten = true
                  // }
                } else {
                  switch (fieldName) {
                    case "Year_Month":
                    case "Hours":
                    // value = formatNumber(value,
                    // "#0.00")
                    // break
                    case "HrWk":
                    case "WkYr":
                    case "HrMnth":
                    case "HrYr":
                    // value = formatNumber(value,
                    // "#,##0")
                    // break
                    case "Rate":
                    case "Budgeted":
                    case "Actual":
                    case "Difference":
                      // value = formatNumber(value,
                      // "$###,###.00")
                      // break
                      excel.writeCell(sheetName, column, row, Double.valueOf(value));
                      break;
                    default:
                      excel.writeCell(sheetName, column, row, value);
                      break;
                  }
                  // excel.writeCell(sheetName, column, row,
                  // value)
                }
                column++;
              }
              firstRecord = false;
              column = 0;
              row++;
            }
          }
          excel.autoSizeColumns(sheetName);
          final String firstSheet = reportListMap.get(0).get(LABEL_LABEL);
          excel.writeCell(firstSheet, 0, 0, excel.readCell(firstSheet, 0, 0));
          excel.close();
        }
      }
    }
    jdbc.close();
    if (sendEmail) {
      sendTreasurersReport(
          "Attached you will find the current Contractor Burn Rates for the following"
              + " years:"
              + Constants.NEWLINE
              + stringBuilderReport.toString()
              + Constants.NEWLINE,
          VivitFoldersFiles.REPORT_XLS_AUTOMATION_T);
    }
  }

  /**
   * @param jdbc
   * @param html
   * @return
   * @throws Throwable
   */
  public static String createReportHTMLTreasurerContractors(JDBC jdbc, HTML html) throws Throwable {
    final List<String> fieldStandardTableList =
        jdbc.getFieldNamesList(VivitViews.VIVIT_CONTRACTOR_HOURS_BTA_VREPORT_TREASURER_YEARS, true);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(JDBCConstants.SELECT_ALL_FROM);
    stringBuilder.append("[" + VivitViews.VIVIT_CONTRACTOR_HOURS_BTA_VREPORT_TREASURER_YEARS + "]");
    final List<Map<String, String>> reportListMap =
        jdbc.queryResultsString(stringBuilder.toString(), false);
    final List<String> fieldNumericList =
        Arrays.asList("Budgeted#;Actual#;Difference#".split(Constants.DELIMETER_LIST));
    final List<String> fieldCurrencyList =
        Arrays.asList("Budgeted$;Actual$;Difference$".split(Constants.DELIMETER_LIST));
    return noPMDUselessParentheses(
        Constants.NEWLINE
            + html.createStandardTable(
                fieldStandardTableList, fieldNumericList, fieldCurrencyList, reportListMap));
  }

  public static void createDatabaseBackup() throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList()));
    final String fileDateStamp =
        IExtension.SQLITE
            + "."
            + DateHelpersTests.getCurrentDateTime(DateHelpersTests.FORMAT_DATE_TIME_STAMP)
            + IExtension.BAK;
    final String databaseBackup =
        VivitFoldersFiles.DATABASE.replace(IExtension.SQLITE, fileDateStamp);
    FileUtils.copyFile(new File(VivitFoldersFiles.DATABASE), new File(databaseBackup));
  }

  /**
   * @param filePathName
   * @throws Throwable
   */
  public static void formatReportBarCharts(String filePathName) throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(
        ParameterHelper.getParameters(
            Arrays.asList(Arrays.asList(LABEL_FILE_PATH_NAME, filePathName))));
    final int timeout = 2 * 60;
    String command =
        "cscript //S //T:"
            + timeout
            + " "
            + Constants.QUOTE_DOUBLE
            + VivitFoldersFiles.VBS
            + Constants.QUOTE_DOUBLE
            + " "
            + Constants.QUOTE_DOUBLE
            + filePathName
            + Constants.QUOTE_DOUBLE
            + "";
    sysOut("command:[" + command + "]");
    Map<String, String> mapProcess = new HashMap<>();
    try {
      mapProcess = CommandLineTests.runProcess(command, true);
      sysOut("mapProcess:[" + mapProcess + "]");
      if (!"0".equals(mapProcess.get("status"))) {
        Assertions.fail("VBScript Did Not Work");
      }
    } catch (final Exception e) {
      sysOut(e);
    }
  }

  public static void getGoogleCommutes(Google google) throws Throwable {
    JDBC jdbc = new JDBC("", DATABASE_DEFINITION);
    StringBuilder stringBuilderQuery = new StringBuilder();
    stringBuilderQuery.append(JDBCConstants.SELECT_ALL_FROM);
    stringBuilderQuery.append("[" + VivitTables.DOM_VIVIT_FLIGHT_AGENDA + "];");
    List<Map<String, String>> agendaMapList =
        jdbc.queryResultsString(stringBuilderQuery.toString(), false);
    Map<String, String> agendaMap = agendaMapList.get(0);
    final String airportTo = agendaMap.get("Airport");
    String dateDepartTo = agendaMap.get("DateDepartTo");
    String dateDepartFrom = agendaMap.get("DateDepartFrom");
    stringBuilderQuery = new StringBuilder();
    stringBuilderQuery.append(JDBCConstants.SELECT + JDBCConstants.DISTINCT + "[Airport]");
    stringBuilderQuery.append(JDBCConstants.FROM);
    stringBuilderQuery.append("[" + VivitViews.VIVIT_DIRECTORS_CONTRACTORS_AIRPORTS + "]");
    stringBuilderQuery.append(" " + JDBCConstants.WHERE + "[Airport] ");
    stringBuilderQuery.append("not in('" + airportTo + "');");
    List<Map<String, String>> flyingListMap =
        jdbc.queryResultsString(stringBuilderQuery.toString(), false);
    StringBuilder stringBuilderSQL = new StringBuilder();
    for (Map<String, String> flyingMap : flyingListMap) {
      String airportFrom = flyingMap.get("Airport");
      List<Flight> flightList =
          google.getFlightsPage().getFlights(airportFrom, airportTo, dateDepartTo, dateDepartFrom);
      for (Flight flight : flightList) {
        stringBuilderSQL = appendRecordFlight(stringBuilderSQL, flight);
      }
    }
    stringBuilderQuery = new StringBuilder();
    stringBuilderQuery.append(JDBCConstants.SELECT_ALL_FROM);
    stringBuilderQuery.append("[" + VivitViews.VIVIT_DIRECTORS_CONTRACTORS_AIRPORTS + "]");
    stringBuilderQuery.append(" " + JDBCConstants.WHERE + "[Airport] ");
    stringBuilderQuery.append("in('" + airportTo + "');");
    List<Map<String, String>> driverListMap =
        jdbc.queryResultsString(stringBuilderQuery.toString(), false);
    for (Map<String, String> driverMap : driverListMap) {
      Flight flight = google.getFlightsPage().getDriveData(driverMap, airportTo);
      sysOut("flight:" + flight.toString());
      stringBuilderSQL = appendRecordFlight(stringBuilderSQL, flight);
    }
    updateGoogleFlights(jdbc, stringBuilderSQL.toString());
    updateGoogleCommutesXLS(jdbc);
  }

  public static void updateGoogleCommutesXLS(JDBC jdbc) throws Throwable {
    // Budget
    String sheetSummary = IExcel.SHEET_SUMMARY;
    String sheetFlights = "Flights";
    JDBC.exportDataFromTableView(
        VivitViews.VIVIT_COMMUTE_BDGET_WTOTALS,
        VivitFoldersFiles.DATA_FLIGHTS,
        sheetSummary,
        DATABASE_DEFINITION,
        true);
    JDBC.exportDataFromTableView(
        VivitTables.DOM_VIVIT_FLIGHTS,
        VivitFoldersFiles.DATA_FLIGHTS,
        sheetFlights,
        DATABASE_DEFINITION,
        false);
    XLS excel = new XLS(VivitFoldersFiles.DATA_FLIGHTS, sheetSummary);
    int rows = excel.getRowCount(sheetSummary);
    int columns = excel.getColumnCount(sheetSummary);
    String value = "";
    for (int row = 0; row < rows; row++) {
      value = excel.readCell(sheetFlights, 1, row);
    }
    rows = excel.getRowCount(sheetSummary);
    columns = excel.getColumnCount(sheetSummary);
    String formula;
    for (int row = 1; row < rows; row++) {
      for (int column = 1; column < columns; column++) {
        String columnName = excel.readCell(sheetSummary, column, 0);
        // if (row == 1 && columnName.equalsIgnoreCase("Count"))
        // {
        // excel.addComment(sheetSummary, column, 0,
        // "As of " + DateHelpersTests.getCurrentDateTime("EEEE, MMM d, yyyy
        // @ HH:mm:ss.SSS a"), false);
        // }
        String airport = excel.readCell(sheetSummary, 1, row);
        switch (columnName) {
          case "Lowest":
          case "Highest":
          case "Average":
          case "Median (Treasurer)":
            value = excel.readCell(sheetSummary, column, row);
            break;
          case "Median (Actual)":
            if (airport.length() == 3) {
              String rowStart = getFlightStartRow(excel, sheetFlights, airport);
              value = excel.readCell(sheetSummary, 2, row);
              int rowEnd = Integer.valueOf(rowStart) + Integer.valueOf(value);
              rowEnd--;
              String columnLetter = Convert.fromNumberToLetterExcel(9);
              formula =
                  "median("
                      + sheetFlights
                      + "!"
                      + columnLetter
                      + rowStart
                      + ":"
                      + columnLetter
                      + rowEnd
                      + ")";
              excel.writeCellFormula(sheetSummary, column, row, formula);
            }
            break;
          case "URL":
            value = excel.readCell(sheetSummary, 1, row);
            String url = excel.readCell(sheetSummary, column, row);
            if (row > 0) {
              excel.addLink(sheetSummary, column, row, "URL", airport, url);
            }
            break;
          default:
            break;
        }
      }
    }
    excel.save();
    for (int column = 0; column < columns; column++) {
      String columnName = excel.readCell(sheetSummary, column, 0);
      String columnLetter = Convert.fromNumberToLetterExcel(column);
      switch (columnName) {
        case "Traveler":
          formula =
              "counta(" + sheetSummary + "!" + columnLetter + "2" + ":" + columnLetter + rows + ")";
          excel.writeCell(sheetSummary, column, rows, "0");
          excel.writeCellFormula(sheetSummary, column, rows, formula);
          excel.setFormatHeading(sheetSummary, column, rows);
          break;
        case "Count":
        case "Total":
        case "Lowest":
        case "Highest":
        case "Average":
        case "Median (Actual)":
          formula =
              "sum(" + sheetSummary + "!" + columnLetter + "2" + ":" + columnLetter + rows + ")";
          excel.writeCell(sheetSummary, column, rows, "0");
          excel.writeCellFormula(sheetSummary, column, rows, formula);
          excel.setFormatHeading(sheetSummary, column, rows);
          break;
        case "Median (Treasurer)":
          formula =
              "sum(" + sheetSummary + "!" + columnLetter + "2" + ":" + columnLetter + rows + ")";
          excel.writeCell(sheetSummary, column, rows, "0");
          excel.writeCellFormula(sheetSummary, column, rows, formula);
          excel.setFormatFontBackgroundColor(sheetSummary, column, rows, "green");
          excel.setFormatHeading(sheetSummary, column, rows);
          excel.addComment(
              sheetSummary,
              column,
              rows,
              "As of " + DateHelpersTests.getCurrentDateTime("EEEE, MMM d, yyyy @ HH:mm:ss.SSS a"),
              false);
          break;
        default:
          excel.setFormatHeading(sheetSummary, column, rows);
          break;
      }
      excel.save();
    }
    excel.autoSizeColumns(sheetSummary);
    // Always leave the first sheet selected.
    excel.setSheet(0);
    excel.save();
    excel.close();
  }

  private static String getFlightStartRow(XLS excel, String sheetName, String airport)
      throws Throwable {
    int rows = excel.getRowCount(sheetName);
    for (int row = 1; row < rows; row++) {
      String airportFound = excel.readCell(sheetName, 0, row);
      if (airportFound.equalsIgnoreCase(airport)) {
        return String.valueOf(row + 1);
      }
    }
    return "-1";
  }

  private static StringBuilder appendRecordFlight(StringBuilder stringBuilderSQL, Flight flight) {
    sysOut("flight:" + flight.toString());
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(JDBCConstants.INSERT_INTO + "[" + VivitTables.DOM_VIVIT_FLIGHTS + "] (");
    stringBuilder.append("[" + "Airport" + "],");
    stringBuilder.append("[" + "Preference" + "],");
    stringBuilder.append("[" + "Sort" + "],");
    stringBuilder.append("[" + "Airline" + "],");
    stringBuilder.append("[" + "Operated By" + "],");
    stringBuilder.append("[" + "Time Depart" + "],");
    stringBuilder.append("[" + "Time Arrive" + "],");
    stringBuilder.append("[" + "Duration" + "],");
    stringBuilder.append("[" + "Stops" + "],");
    stringBuilder.append("[" + "Price" + "]");
    stringBuilder.append(") values (");
    stringBuilder.append("'" + SQL.parseQuote(flight.getAirport()) + "',");
    stringBuilder.append("'" + SQL.parseQuote(flight.getPreference()) + "',");
    stringBuilder.append(flight.getSort() + ",");
    stringBuilder.append("'" + SQL.parseQuote(flight.getAirline()) + "',");
    stringBuilder.append("'" + SQL.parseQuote(flight.getOperatedBy()) + "',");
    stringBuilder.append("'" + SQL.parseQuote(flight.getTimeDepart()) + "',");
    stringBuilder.append("'" + SQL.parseQuote(flight.getTimeArrive()) + "',");
    stringBuilder.append("'" + SQL.parseQuote(flight.getDuration()) + "',");
    stringBuilder.append("'" + SQL.parseQuote(flight.getStops()) + "',");
    stringBuilder.append(flight.getPrice() + ");");
    if (JavaHelpers.hasValue(stringBuilderSQL.toString())) {
      stringBuilderSQL.append(Constants.NEWLINE);
    }
    stringBuilderSQL.append(stringBuilder.toString());
    return stringBuilderSQL;
  }

  private static void updateGoogleFlights(JDBC jdbc, String sql) throws Throwable {
    StringBuilder stringBuilderSQL = new StringBuilder();
    // stringBuilderSQL.append(JDBCConstants.DELETE_FROM + "[" +
    // VivitTables.DOM_VIVIT_FLIGHT_TRAVELERS + "];");
    // stringBuilderSQL.append(Constants.NEWLINE);
    stringBuilderSQL.append(JDBCConstants.DELETE_FROM + "[" + VivitTables.DOM_VIVIT_FLIGHTS + "];");
    jdbc.executeUpdate(stringBuilderSQL.toString(), false);
    jdbc.executeUpdate(sql, false);
  }

  public static void getGoogleFlights(
      Google google, String airportTo, String dateDepartTo, String dateDepartFrom)
      throws Throwable {
    JDBC jdbc = new JDBC("", DATABASE_DEFINITION);
    StringBuilder stringBuilderSQL = new StringBuilder();
    stringBuilderSQL.append(JDBCConstants.SELECT_ALL_FROM);
    stringBuilderSQL.append("[" + VivitViews.VIVIT_DIRECTORS_CONTRACTORS_AIRPORTS + "]");
    stringBuilderSQL.append(" " + JDBCConstants.WHERE + "[Airport] ");
    stringBuilderSQL.append("in('LHR')");
    stringBuilderSQL.append(";");
    List<Map<String, String>> airportListMap =
        jdbc.queryResultsString(stringBuilderSQL.toString(), false);
    google
        .getFlightsPage()
        .getFlightsOld(
            VivitFoldersFiles.DATA_FLIGHTS,
            airportListMap,
            airportTo,
            dateDepartTo,
            dateDepartFrom);
  }

  public static void getHTMLBrokenLinks() throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList()));
    JDBC jdbc = new JDBC("", DATABASE_DEFINITION);
    final String sql =
        JDBCConstants.SELECT_ALL + JDBCConstants.FROM + VivitViews.VIVIT_BROKEN_LINKS;
    final List<Map<Integer, String>> brokenLinksListMap = jdbc.queryResultsIndex(sql, true);
    jdbc.close();
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(
        "<table class="
            + Constants.QUOTE_DOUBLE
            + "scroll"
            + Constants.QUOTE_DOUBLE
            + " style="
            + Constants.QUOTE_DOUBLE
            + STYLE_BORDER
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append("<tr>");
    stringBuilder.append(
        "<th style="
            + Constants.QUOTE_DOUBLE
            + "border: 1px solid black; border-collapse: collapse; background-color: "
            + ColorsHEX.VIVIT_GOLD.getValue()
            + ";"
            + Constants.QUOTE_DOUBLE
            + ">");
    // Need to account for the headings map.
    stringBuilder.append("Broken Links (" + (brokenLinksListMap.size() - 1) + ")");
    stringBuilder.append("</th>");
    stringBuilder.append("</tr>");
    stringBuilder.append("</table>");
    final Map<String, Integer> fieldNameMap = new HashMap<>();
    for (int brokenLinksListMapIndex = 0;
        brokenLinksListMapIndex < brokenLinksListMap.size();
        brokenLinksListMapIndex++) {
      final Map<Integer, String> brokenLinkMap = brokenLinksListMap.get(brokenLinksListMapIndex);
      sysOut("brokenLinkMap:" + brokenLinkMap.toString());
      if (brokenLinksListMapIndex == 0) {
        stringBuilder.append(
            "<table class="
                + Constants.QUOTE_DOUBLE
                + "scroll"
                + Constants.QUOTE_DOUBLE
                + " style="
                + Constants.QUOTE_DOUBLE
                + STYLE_BORDER
                + Constants.QUOTE_DOUBLE
                + ">");
        stringBuilder.append("<thead>");
        stringBuilder.append("<tr>");
        for (final Entry<Integer, String> entry : brokenLinkMap.entrySet()) {
          stringBuilder.append(
              "<th style="
                  + Constants.QUOTE_DOUBLE
                  + "border: 1px solid black; border-collapse: collapse;"
                  + " background-color: "
                  + ColorsHEX.VIVIT_LIGHTBLUE.getValue()
                  + ";"
                  + Constants.QUOTE_DOUBLE
                  + ">");
          final int key = (int) entry.getKey();
          final String value = (String) entry.getValue();
          fieldNameMap.put(value, key);
          stringBuilder.append("<b>" + value + "</b>");
          stringBuilder.append("</th>");
        }
        stringBuilder.append("</tr>");
        stringBuilder.append("</thead>");
        stringBuilder.append("<tbody>");
      } else {
        // stringBuilder.append("<tbody>")
        stringBuilder.append("<tr>");
        for (final Entry<Integer, String> entry : brokenLinkMap.entrySet()) {
          stringBuilder.append(
              "<td style="
                  + Constants.QUOTE_DOUBLE
                  + "border: 1px solid black; border-collapse: collapse;"
                  + Constants.QUOTE_DOUBLE
                  + ">");
          final int key = (int) entry.getKey();
          final String value = (String) entry.getValue();
          int keyChapterULR = fieldNameMap.get("ChapterURL");
          if (key == keyChapterULR) {
            stringBuilder.append(HTML.link(value, value));
          } else {
            stringBuilder.append(value);
          }
          stringBuilder.append("</td>");
        }
        stringBuilder.append("</tr>");
        // stringBuilder.append("</tbody>")
      }
    }
    if (brokenLinksListMap.size() == 1) {
      // stringBuilder.append("<tbody>")
      stringBuilder.append("<tr>");
      for (int i = 0; i < fieldNameMap.size(); i++) {
        stringBuilder.append(
            "<td style="
                + Constants.QUOTE_DOUBLE
                + "border: 1px solid black; border-collapse: collapse;"
                + Constants.QUOTE_DOUBLE
                + ">");
        stringBuilder.append("None");
        stringBuilder.append("</td>");
      }
      stringBuilder.append("</tr>");
      // stringBuilder.append("</tbody>")
    }
    stringBuilder.append("</tbody>");
    stringBuilder.append("</table>");
    String htmlBrokenLinks = "<html>" + stringBuilder.toString() + "</html>";
    htmlBrokenLinks = XML.formatPretty(htmlBrokenLinks);
    FSOTests.fileWrite(
        VivitFoldersFiles.REPORT_HTM_AUTOMATION_BROKEN_LINKS, htmlBrokenLinks, false);
  }

  public static void getHTMLDatabaseChanges() throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList()));
    JDBC jdbc = new JDBC("", DATABASE_DEFINITION);
    final String sql =
        JDBCConstants.SELECT_ALL + JDBCConstants.FROM + VivitTables.VIVIT_DATABASE_CHANGES;
    final List<Map<Integer, String>> databaseChangesListMap = jdbc.queryResultsIndex(sql, true);
    jdbc.close();
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(
        "<table class="
            + Constants.QUOTE_DOUBLE
            + "scroll"
            + Constants.QUOTE_DOUBLE
            + " style="
            + Constants.QUOTE_DOUBLE
            + STYLE_BORDER
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append("<tr>");
    stringBuilder.append(
        "<th style="
            + Constants.QUOTE_DOUBLE
            + "border: 1px solid black; border-collapse: collapse; background-color: "
            + ColorsHEX.VIVIT_GOLD.getValue()
            + ";"
            + Constants.QUOTE_DOUBLE
            + ">");
    // Need to account for the headings map.
    stringBuilder.append("Database Changes (" + (databaseChangesListMap.size() - 1) + ")");
    stringBuilder.append("</th>");
    stringBuilder.append("</tr>");
    stringBuilder.append("</table>");
    final Map<String, Integer> fieldNameMap = new HashMap<>();
    for (int databaseChangesListMapIndex = 0;
        databaseChangesListMapIndex < databaseChangesListMap.size();
        databaseChangesListMapIndex++) {
      final Map<Integer, String> brokenLinkMap =
          databaseChangesListMap.get(databaseChangesListMapIndex);
      sysOut("brokenLinkMap:" + brokenLinkMap.toString());
      if (databaseChangesListMapIndex == 0) {
        stringBuilder.append(
            "<table class="
                + Constants.QUOTE_DOUBLE
                + "scroll"
                + Constants.QUOTE_DOUBLE
                + " style="
                + Constants.QUOTE_DOUBLE
                + STYLE_BORDER
                + Constants.QUOTE_DOUBLE
                + ">");
        stringBuilder.append("<thead>");
        stringBuilder.append("<tr>");
        for (final Entry<Integer, String> entry : brokenLinkMap.entrySet()) {
          stringBuilder.append(
              "<th style="
                  + Constants.QUOTE_DOUBLE
                  + "border: 1px solid black; border-collapse: collapse;"
                  + " background-color: "
                  + ColorsHEX.VIVIT_LIGHTBLUE.getValue()
                  + ";"
                  + Constants.QUOTE_DOUBLE
                  + ">");
          final int key = (int) entry.getKey();
          final String value = (String) entry.getValue();
          fieldNameMap.put(value, key);
          stringBuilder.append("<b>" + value + "</b>");
          stringBuilder.append("</th>");
        }
        stringBuilder.append("</tr>");
        stringBuilder.append("</thead>");
        stringBuilder.append("<tbody>");
      } else {
        // stringBuilder.append("<tbody>")
        stringBuilder.append("<tr>");
        for (final Entry<Integer, String> entry : brokenLinkMap.entrySet()) {
          stringBuilder.append(
              "<td style="
                  + Constants.QUOTE_DOUBLE
                  + "border: 1px solid black; border-collapse: collapse;"
                  + Constants.QUOTE_DOUBLE
                  + ">");
          final String value = (String) entry.getValue();
          stringBuilder.append(value);
          stringBuilder.append("</td>");
        }
        stringBuilder.append("</tr>");
        // stringBuilder.append("</tbody>")
      }
    }
    if (databaseChangesListMap.size() == 1) {
      // stringBuilder.append("<tbody>")
      stringBuilder.append("<tr>");
      for (int i = 0; i < fieldNameMap.size(); i++) {
        stringBuilder.append(
            "<td style="
                + Constants.QUOTE_DOUBLE
                + "border: 1px solid black; border-collapse: collapse;"
                + Constants.QUOTE_DOUBLE
                + ">");
        stringBuilder.append("None");
        stringBuilder.append("</td>");
      }
      stringBuilder.append("</tr>");
      // stringBuilder.append("</tbody>")
    }
    stringBuilder.append("</tbody>");
    stringBuilder.append("</table>");
    String htmlDatabaseChanges = "<html>" + stringBuilder.toString() + "</html>";
    htmlDatabaseChanges = XML.formatPretty(htmlDatabaseChanges);
    FSOTests.fileWrite(
        VivitFoldersFiles.REPORT_HTM_AUTOMATION_DATABASE_CHANGES, htmlDatabaseChanges, false);
  }

  public static void createReportHTMLTestInformation(SeleniumWebDriver seleniumWebDriver)
      throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList()));
    final List<Map<Integer, String>> testInformationListMap =
        getReportHTMLTestInformationListMap(seleniumWebDriver);
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(
        "<table class="
            + Constants.QUOTE_DOUBLE
            + "scroll"
            + Constants.QUOTE_DOUBLE
            + " style="
            + Constants.QUOTE_DOUBLE
            + STYLE_BORDER
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append("<tr>");
    stringBuilder.append(
        "<th style="
            + Constants.QUOTE_DOUBLE
            + "border: 1px solid black; border-collapse: collapse; background-color: "
            + ColorsHEX.VIVIT_PURPLE.getValue()
            + ";"
            + Constants.QUOTE_DOUBLE
            + ">");
    // Need to account for the headings map.
    stringBuilder.append("Test Information");
    stringBuilder.append("</th>");
    stringBuilder.append("</tr>");
    stringBuilder.append("</table>");
    final Map<String, Integer> fieldNameMap = new HashMap<>();
    for (int testInformationListMapIndex = 0;
        testInformationListMapIndex < testInformationListMap.size();
        testInformationListMapIndex++) {
      final Map<Integer, String> testInformationLinkMap =
          testInformationListMap.get(testInformationListMapIndex);
      sysOut("testInformationLinkMap:" + testInformationLinkMap.toString());
      if (testInformationListMapIndex == 0) {
        stringBuilder.append(
            "<table class="
                + Constants.QUOTE_DOUBLE
                + "scroll"
                + Constants.QUOTE_DOUBLE
                + " style="
                + Constants.QUOTE_DOUBLE
                + STYLE_BORDER
                + Constants.QUOTE_DOUBLE
                + ">");
        stringBuilder.append("<thead>");
        stringBuilder.append("<tr>");
        for (final Entry<Integer, String> entry : testInformationLinkMap.entrySet()) {
          stringBuilder.append(
              "<th style="
                  + Constants.QUOTE_DOUBLE
                  + "border: 1px solid black; border-collapse: collapse;"
                  + " background-color: "
                  + ColorsHEX.VIVIT_LIGHTBLUE.getValue()
                  + ";"
                  + Constants.QUOTE_DOUBLE
                  + ">");
          final int key = (int) entry.getKey();
          final String value = (String) entry.getValue();
          fieldNameMap.put(value, key);
          stringBuilder.append("<b>" + value + "</b>");
          stringBuilder.append("</th>");
        }
        stringBuilder.append("</tr>");
        stringBuilder.append("</thead>");
        stringBuilder.append("<tbody>");
      } else {
        // stringBuilder.append("<tbody>")
        stringBuilder.append("<tr>");
        for (final Entry<Integer, String> entry : testInformationLinkMap.entrySet()) {
          stringBuilder.append(
              "<td style="
                  + Constants.QUOTE_DOUBLE
                  + "border: 1px solid black; border-collapse: collapse;"
                  + Constants.QUOTE_DOUBLE
                  + ">");
          final String value = (String) entry.getValue();
          stringBuilder.append(value);
          stringBuilder.append("</td>");
        }
        stringBuilder.append("</tr>");
        // stringBuilder.append("</tbody>")
      }
    }
    if (testInformationListMap.size() == 1) {
      // stringBuilder.append("<tbody>")
      stringBuilder.append("<tr>");
      for (int i = 0; i < fieldNameMap.size(); i++) {
        stringBuilder.append(
            "<td style="
                + Constants.QUOTE_DOUBLE
                + "border: 1px solid black; border-collapse: collapse;"
                + Constants.QUOTE_DOUBLE
                + ">");
        stringBuilder.append("None");
        stringBuilder.append("</td>");
      }
      stringBuilder.append("</tr>");
      // stringBuilder.append("</tbody>")
    }
    stringBuilder.append("</tbody>");
    stringBuilder.append("</table>");
    String htmlTestInformation = "<html>" + stringBuilder.toString() + "</html>";
    htmlTestInformation = XML.formatPretty(htmlTestInformation);
    FSOTests.fileWrite(
        VivitFoldersFiles.REPORT_HTM_AUTOMATION_TEST_INFORMATION, htmlTestInformation, false);
  }

  private static List<Map<Integer, String>> getReportHTMLTestInformationListMap(
      SeleniumWebDriver seleniumWebDriver) throws Throwable {
    List<Map<Integer, String>> testInformationListMap = new ArrayList<>();
    Map<Integer, String> testInformationMap = new HashMap<>();
    testInformationMap.put(1, "Property");
    testInformationMap.put(2, "Value");
    testInformationListMap.add(new HashMap<>(testInformationMap));
    testInformationMap.put(1, "Operating System");
    testInformationMap.put(2, seleniumWebDriver.getOperatingSystem());
    testInformationListMap.add(new HashMap<>(testInformationMap));
    testInformationMap.put(1, "Platform");
    testInformationMap.put(2, seleniumWebDriver.getCapabilities().getPlatformName().toString());
    testInformationListMap.add(new HashMap<>(testInformationMap));
    testInformationMap.put(1, "Browser");
    testInformationMap.put(2, seleniumWebDriver.getCapabilities().getBrowserName());
    testInformationListMap.add(new HashMap<>(testInformationMap));
    testInformationMap.put(1, "Browser Version");
    testInformationMap.put(2, seleniumWebDriver.getCapabilities().getBrowserVersion());
    testInformationListMap.add(new HashMap<>(testInformationMap));
    testInformationMap.put(1, "WebDriver");
    testInformationMap.put(2, seleniumWebDriver.getWebDriver().toString());
    testInformationListMap.add(new HashMap<>(testInformationMap));
    testInformationMap.put(1, "WebDriver Version");
    final EDriverProperties eDriverProperties =
        EDriverProperties.fromString(
            seleniumWebDriver.getCapabilities().getBrowserName().toUpperCase(Locale.ENGLISH));
    String webDriver = eDriverProperties.getPathDriver();
    testInformationMap.put(2, seleniumWebDriver.getWebDriverVersion(webDriver));
    testInformationListMap.add(new HashMap<>(testInformationMap));
    testInformationMap.put(1, "Session ID");
    testInformationMap.put(2, seleniumWebDriver.getSessionId().toString());
    testInformationListMap.add(new HashMap<>(testInformationMap));
    testInformationMap.put(1, "DateTimeStamp");
    testInformationMap.put(
        2,
        DateHelpersTests.getCurrentDateTime("yyyy-MM-dd HH:mm:ss.SSS")
            + " ("
            + DateHelpersTests.getTimeZoneDisplayName()
            + "-"
            + DateHelpersTests.getTimeZoneID()
            + ")");
    testInformationListMap.add(new HashMap<>(testInformationMap));
    return testInformationListMap;
  }

  public static void getHTMLMemberChanges() throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList()));
    JDBC jdbc = new JDBC("", DATABASE_DEFINITION);
    final String sql =
        JDBCConstants.SELECT_ALL + JDBCConstants.FROM + VivitViews.VIVIT_MEMBER_CHANGES;
    final List<Map<Integer, String>> gtwEventsListMap = jdbc.queryResultsIndex(sql, true);
    jdbc.close();
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(
        "<table class="
            + Constants.QUOTE_DOUBLE
            + "scroll"
            + Constants.QUOTE_DOUBLE
            + " style="
            + Constants.QUOTE_DOUBLE
            + STYLE_BORDER
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append("<tr>");
    stringBuilder.append(
        "<th style="
            + Constants.QUOTE_DOUBLE
            + "border: 1px solid black; border-collapse: collapse; background-color: "
            + ColorsHEX.VIVIT_GOLD.getValue()
            + ";"
            + Constants.QUOTE_DOUBLE
            + ">");
    // Need to account for the headings map.
    stringBuilder.append("Member Changes (Added/Removed)");
    stringBuilder.append("</th>");
    stringBuilder.append("</tr>");
    stringBuilder.append("</table>");
    final Map<String, Integer> fieldNameMap = new HashMap<>();
    int memberChange = 0;
    for (int gtwEventsListMapIndex = 0;
        gtwEventsListMapIndex < gtwEventsListMap.size();
        gtwEventsListMapIndex++) {
      final Map<Integer, String> gtwEventsMap = gtwEventsListMap.get(gtwEventsListMapIndex);
      sysOut("gtwEventsMap:" + gtwEventsMap.toString());
      if (gtwEventsListMapIndex == 0) {
        stringBuilder.append(
            "<table class="
                + Constants.QUOTE_DOUBLE
                + "scroll"
                + Constants.QUOTE_DOUBLE
                + " style="
                + Constants.QUOTE_DOUBLE
                + STYLE_BORDER
                + Constants.QUOTE_DOUBLE
                + ">");
        stringBuilder.append("<thead>");
        stringBuilder.append("<tr>");
        for (final Entry<Integer, String> entry : gtwEventsMap.entrySet()) {
          stringBuilder.append(
              "<th style="
                  + Constants.QUOTE_DOUBLE
                  + "border: 1px solid black; border-collapse: collapse;"
                  + " background-color: "
                  + ColorsHEX.VIVIT_LIGHTBLUE.getValue()
                  + ";"
                  + Constants.QUOTE_DOUBLE
                  + ">");
          final int key = (int) entry.getKey();
          final String value = (String) entry.getValue();
          fieldNameMap.put(value, key);
          stringBuilder.append("<b>" + value + "</b>");
          stringBuilder.append("</th>");
        }
        stringBuilder.append("</tr>");
        stringBuilder.append("</thead>");
        stringBuilder.append("<tbody>");
      } else {
        // stringBuilder.append("<tbody>")
        stringBuilder.append("<tr>");
        for (final Entry<Integer, String> entry : gtwEventsMap.entrySet()) {
          stringBuilder.append(
              "<td style="
                  + Constants.QUOTE_DOUBLE
                  + "border: 1px solid black; border-collapse: collapse;"
                  + Constants.QUOTE_DOUBLE
                  + ">");
          final int key = (int) entry.getKey();
          String value = (String) entry.getValue();
          if (fieldNameMap.get("Percent") == key) {
            value = JavaHelpers.formatNumber(value, "##0.00########%");
          } else {
            if (fieldNameMap.get("Change") == key) {
              if (Integer.valueOf(value) > 0) {
                memberChange = 1;
              } else if (Integer.valueOf(value) < 0) {
                memberChange = -1;
              }
            }
            value = JavaHelpers.formatNumber(value, "###,##0");
          }
          String stylePre = "";
          String stylePost = "";
          if (key >= fieldNameMap.get("Change")) {
            switch (memberChange) {
              case -1:
                stylePre = "<b><font color=\"" + ColorsHEX.RED.getValue() + "\">";
                stylePost = "</font></b>";
                break;
              case 1:
                stylePre = "<b><font color=\"" + ColorsHEX.GREEN.getValue() + "\">";
                stylePost = "</font></b>";
                break;
              default:
                break;
            }
          }
          stringBuilder.append(stylePre + value + stylePost);
          stringBuilder.append("</td>");
        }
        stringBuilder.append("</tr>");
        // stringBuilder.append("</tbody>")
      }
    }
    stringBuilder.append("</tbody>");
    stringBuilder.append("</table>");
    String htmlMemberChanges = "<html>" + stringBuilder.toString() + "</html>";
    htmlMemberChanges = XML.formatPretty(htmlMemberChanges);
    FSOTests.fileWrite(
        VivitFoldersFiles.REPORT_HTM_AUTOMATION_MEMBER_CHANGES, htmlMemberChanges, false);
  }

  public static void getHTMLYMGTWEvents() throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList()));
    JDBC jdbc = new JDBC("", DATABASE_DEFINITION);
    final String sql =
        JDBCConstants.SELECT_ALL
            + JDBCConstants.FROM
            + VivitViews.VIVIT_EVENTS_CURRENT_GTW_REGISTRATION;
    final List<Map<Integer, String>> gtwEventsListMap = jdbc.queryResultsIndex(sql, true);
    jdbc.close();
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(
        "<table class="
            + Constants.QUOTE_DOUBLE
            + "scroll"
            + Constants.QUOTE_DOUBLE
            + " style="
            + Constants.QUOTE_DOUBLE
            + STYLE_BORDER
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append("<tr>");
    stringBuilder.append(
        "<th style="
            + Constants.QUOTE_DOUBLE
            + "border: 1px solid black; border-collapse: collapse; background-color: "
            + ColorsHEX.VIVIT_GOLD.getValue()
            + ";"
            + Constants.QUOTE_DOUBLE
            + ">");
    // Need to account for the headings map.
    stringBuilder.append("Current/Upcoming YM/GTW Events (" + (gtwEventsListMap.size() - 1) + ")");
    stringBuilder.append("</th>");
    stringBuilder.append("</tr>");
    stringBuilder.append("</table>");
    final Map<String, Integer> fieldNameMap = new HashMap<>();
    for (int gtwEventsListMapIndex = 0;
        gtwEventsListMapIndex < gtwEventsListMap.size();
        gtwEventsListMapIndex++) {
      final Map<Integer, String> gtwEventsMap = gtwEventsListMap.get(gtwEventsListMapIndex);
      sysOut("gtwEventsMap:" + gtwEventsMap.toString());
      if (gtwEventsListMapIndex == 0) {
        stringBuilder.append(
            "<table class="
                + Constants.QUOTE_DOUBLE
                + "scroll"
                + Constants.QUOTE_DOUBLE
                + " style="
                + Constants.QUOTE_DOUBLE
                + STYLE_BORDER
                + Constants.QUOTE_DOUBLE
                + ">");
        stringBuilder.append("<thead>");
        stringBuilder.append("<tr>");
        for (final Entry<Integer, String> entry : gtwEventsMap.entrySet()) {
          stringBuilder.append(
              "<th style="
                  + Constants.QUOTE_DOUBLE
                  + "border: 1px solid black; border-collapse: collapse;"
                  + " background-color: "
                  + ColorsHEX.VIVIT_LIGHTBLUE.getValue()
                  + ";"
                  + Constants.QUOTE_DOUBLE
                  + ">");
          final int key = (int) entry.getKey();
          final String value = (String) entry.getValue();
          fieldNameMap.put(value, key);
          stringBuilder.append("<b>" + value + "</b>");
          stringBuilder.append("</th>");
        }
        stringBuilder.append("</tr>");
        stringBuilder.append("</thead>");
        stringBuilder.append("<tbody>");
      } else {
        // stringBuilder.append("<tbody>")
        stringBuilder.append("<tr>");
        for (final Entry<Integer, String> entry : gtwEventsMap.entrySet()) {
          stringBuilder.append(
              "<td style="
                  + Constants.QUOTE_DOUBLE
                  + "border: 1px solid black; border-collapse: collapse;"
                  + Constants.QUOTE_DOUBLE
                  + ">");
          final int key = (int) entry.getKey();
          final String value = (String) entry.getValue();
          int keyYMID = fieldNameMap.get("YMID");
          int keyGTWID = fieldNameMap.get("GTWID");
          String href = "";
          if (key == keyGTWID) {
            // https://global.gotomeeting.com/join/963830877
            href = "https://global.gotowebinar.com/join/" + value;
            stringBuilder.append(HTML.link(href, value));
          } else if (key == keyYMID) {
            href =
                "https://www.vivit-worldwide.org/events/EventDetails.aspx?id=" + value + "&group=";
            stringBuilder.append(HTML.link(href, value));
          } else {
            stringBuilder.append(value);
          }
          stringBuilder.append("</td>");
        }
        stringBuilder.append("</tr>");
        // stringBuilder.append("</tbody>")
      }
    }
    if (gtwEventsListMap.size() == 1) {
      // stringBuilder.append("<tbody>")
      stringBuilder.append("<tr>");
      for (int i = 0; i < fieldNameMap.size(); i++) {
        stringBuilder.append(
            "<td style="
                + Constants.QUOTE_DOUBLE
                + "border: 1px solid black; border-collapse: collapse;"
                + Constants.QUOTE_DOUBLE
                + ">");
        stringBuilder.append("None");
        stringBuilder.append("</td>");
      }
      stringBuilder.append("</tr>");
      // stringBuilder.append("</tbody>")
    }
    stringBuilder.append("</tbody>");
    stringBuilder.append("</table>");
    String htmlGTWEventsLinks = "<html>" + stringBuilder.toString() + "</html>";
    htmlGTWEventsLinks = XML.formatPretty(htmlGTWEventsLinks);
    FSOTests.fileWrite(
        VivitFoldersFiles.REPORT_HTM_AUTOMATION_EVENTS_YM, htmlGTWEventsLinks, false);
  }

  public static void getHTMLYMGTWEventAttendees() throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList()));
    JDBC jdbc = new JDBC("", DATABASE_DEFINITION);
    final String sql =
        JDBCConstants.SELECT_ALL
            + JDBCConstants.FROM
            + VivitViews.VIVIT_EVENT_ATTENDEES_CURRENT_GTW;
    final List<Map<Integer, String>> gtwEventAttendeesListMap = jdbc.queryResultsIndex(sql, true);
    jdbc.close();
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(
        "<table class="
            + Constants.QUOTE_DOUBLE
            + "scroll"
            + Constants.QUOTE_DOUBLE
            + " style="
            + Constants.QUOTE_DOUBLE
            + STYLE_BORDER
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append("<tr>");
    stringBuilder.append(
        "<th style="
            + Constants.QUOTE_DOUBLE
            + "border: 1px solid black; border-collapse: collapse; background-color: "
            + ColorsHEX.VIVIT_GOLD.getValue()
            + ";"
            + Constants.QUOTE_DOUBLE
            + ">");
    // Need to account for the headings map.
    stringBuilder.append(
        "Current/Upcoming YM/GTW Event Attendees (" + (gtwEventAttendeesListMap.size() - 1) + ")");
    stringBuilder.append("</th>");
    stringBuilder.append("</tr>");
    stringBuilder.append("</table>");
    final Map<String, Integer> fieldNameMap = new HashMap<>();
    for (int gtwEventAttendeesListMapIndex = 0;
        gtwEventAttendeesListMapIndex < gtwEventAttendeesListMap.size();
        gtwEventAttendeesListMapIndex++) {
      final Map<Integer, String> gtwEventAttendeesMap =
          gtwEventAttendeesListMap.get(gtwEventAttendeesListMapIndex);
      sysOut("gtwEventAttendeesMap:" + gtwEventAttendeesMap.toString());
      if (gtwEventAttendeesListMapIndex == 0) {
        stringBuilder.append(
            "<table class="
                + Constants.QUOTE_DOUBLE
                + "scroll"
                + Constants.QUOTE_DOUBLE
                + " style="
                + Constants.QUOTE_DOUBLE
                + STYLE_BORDER
                + Constants.QUOTE_DOUBLE
                + ">");
        stringBuilder.append("<thead>");
        stringBuilder.append("<tr>");
        for (final Entry<Integer, String> entry : gtwEventAttendeesMap.entrySet()) {
          stringBuilder.append(
              "<th style="
                  + Constants.QUOTE_DOUBLE
                  + "border: 1px solid black; border-collapse: collapse;"
                  + " background-color: "
                  + ColorsHEX.VIVIT_LIGHTBLUE.getValue()
                  + ";"
                  + Constants.QUOTE_DOUBLE
                  + ">");
          final int key = (int) entry.getKey();
          final String value = (String) entry.getValue();
          fieldNameMap.put(value, key);
          stringBuilder.append("<b>" + value + "</b>");
          stringBuilder.append("</th>");
        }
        stringBuilder.append("</tr>");
        stringBuilder.append("</thead>");
        stringBuilder.append("<tbody>");
      } else {
        // stringBuilder.append("<tbody>")
        stringBuilder.append("<tr>");
        for (final Entry<Integer, String> entry : gtwEventAttendeesMap.entrySet()) {
          stringBuilder.append(
              "<td style="
                  + Constants.QUOTE_DOUBLE
                  + "border: 1px solid black; border-collapse: collapse;"
                  + Constants.QUOTE_DOUBLE
                  + ">");
          final int key = (int) entry.getKey();
          final String value = (String) entry.getValue();
          int keyYMID = fieldNameMap.get("EventID");
          int keyGTWID = fieldNameMap.get("GTWID");
          int keyWebsiteID = fieldNameMap.get("WebsiteID");
          String href = "";
          if (key == keyGTWID) {
            // https://global.gotomeeting.com/join/963830877
            href = "https://global.gotowebinar.com/join/" + value;
            stringBuilder.append(HTML.link(href, value));
          } else if (key == keyYMID) {
            href =
                "https://www.vivit-worldwide.org/events/EventDetails.aspx?id=" + value + "&group=";
            stringBuilder.append(HTML.link(href, value));
          } else if (key == keyWebsiteID) {
            href = "https://www.vivit-worldwide.org/members/default.asp?id=" + value;
            stringBuilder.append(HTML.link(href, value));
          } else {
            stringBuilder.append(value);
          }
          stringBuilder.append("</td>");
        }
        stringBuilder.append("</tr>");
        // stringBuilder.append("</tbody>")
      }
    }
    if (gtwEventAttendeesListMap.size() == 1) {
      // stringBuilder.append("<tbody>")
      stringBuilder.append("<tr>");
      for (int i = 0; i < fieldNameMap.size(); i++) {
        stringBuilder.append(
            "<td style="
                + Constants.QUOTE_DOUBLE
                + "border: 1px solid black; border-collapse: collapse;"
                + Constants.QUOTE_DOUBLE
                + ">");
        stringBuilder.append("None");
        stringBuilder.append("</td>");
      }
      stringBuilder.append("</tr>");
      // stringBuilder.append("</tbody>")
    }
    stringBuilder.append("</tbody>");
    stringBuilder.append("</table>");
    String htmlGTWEventAttendeesLinks = "<html>" + stringBuilder.toString() + "</html>";
    htmlGTWEventAttendeesLinks = XML.formatPretty(htmlGTWEventAttendeesLinks);
    FSOTests.fileWrite(
        VivitFoldersFiles.REPORT_HTM_AUTOMATION_EVENT_ATTENDEES_YM,
        htmlGTWEventAttendeesLinks,
        false);
  }

  public static void getHTMLYMGTWEventRegistration() throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList()));
    JDBC jdbc = new JDBC("", DATABASE_DEFINITION);
    final String sql =
        JDBCConstants.SELECT_ALL
            + JDBCConstants.FROM
            + VivitViews.VIVIT_EVENT_REGISTRATION_CURRENT_GTW;
    final List<Map<Integer, String>> gtwEventRegistrationListMap =
        jdbc.queryResultsIndex(sql, true);
    jdbc.close();
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(
        "<table class="
            + Constants.QUOTE_DOUBLE
            + "scroll"
            + Constants.QUOTE_DOUBLE
            + " style="
            + Constants.QUOTE_DOUBLE
            + STYLE_BORDER
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append("<tr>");
    stringBuilder.append(
        "<th style="
            + Constants.QUOTE_DOUBLE
            + "border: 1px solid black; border-collapse: collapse; background-color: "
            + ColorsHEX.VIVIT_GOLD.getValue()
            + ";"
            + Constants.QUOTE_DOUBLE
            + ">");
    // Need to account for the headings map.
    stringBuilder.append(
        "Current/Upcoming YM/GTW Event Registrations ("
            + (gtwEventRegistrationListMap.size() - 1)
            + ")");
    stringBuilder.append("</th>");
    stringBuilder.append("</tr>");
    stringBuilder.append("</table>");
    final Map<String, Integer> fieldNameMap = new HashMap<>();
    for (int gtwEventRegistrationsListMapIndex = 0;
        gtwEventRegistrationsListMapIndex < gtwEventRegistrationListMap.size();
        gtwEventRegistrationsListMapIndex++) {
      final Map<Integer, String> gtwEventRegistrationsMap =
          gtwEventRegistrationListMap.get(gtwEventRegistrationsListMapIndex);
      sysOut("gtwEventAttendeesMap:" + gtwEventRegistrationsMap.toString());
      if (gtwEventRegistrationsListMapIndex == 0) {
        stringBuilder.append(
            "<table class="
                + Constants.QUOTE_DOUBLE
                + "scroll"
                + Constants.QUOTE_DOUBLE
                + " style="
                + Constants.QUOTE_DOUBLE
                + STYLE_BORDER
                + Constants.QUOTE_DOUBLE
                + ">");
        stringBuilder.append("<thead>");
        stringBuilder.append("<tr>");
        for (final Entry<Integer, String> entry : gtwEventRegistrationsMap.entrySet()) {
          stringBuilder.append(
              "<th style="
                  + Constants.QUOTE_DOUBLE
                  + "border: 1px solid black; border-collapse: collapse;"
                  + " background-color: "
                  + ColorsHEX.VIVIT_LIGHTBLUE.getValue()
                  + ";"
                  + Constants.QUOTE_DOUBLE
                  + ">");
          final int key = (int) entry.getKey();
          final String value = (String) entry.getValue();
          fieldNameMap.put(value, key);
          stringBuilder.append("<b>" + value + "</b>");
          stringBuilder.append("</th>");
        }
        stringBuilder.append("</tr>");
        stringBuilder.append("</thead>");
        stringBuilder.append("<tbody>");
      } else {
        // stringBuilder.append("<tbody>")
        stringBuilder.append("<tr>");
        for (final Entry<Integer, String> entry : gtwEventRegistrationsMap.entrySet()) {
          stringBuilder.append(
              "<td style="
                  + Constants.QUOTE_DOUBLE
                  + "border: 1px solid black; border-collapse: collapse;"
                  + Constants.QUOTE_DOUBLE
                  + ">");
          final int key = (int) entry.getKey();
          final String value = (String) entry.getValue();
          int keyYMID = fieldNameMap.get("YMID");
          int keyGTWID = fieldNameMap.get("GTWID");
          int keyWebsiteID = fieldNameMap.get(LABEL_WEB_SITE_MEMBER_ID);
          String href = "";
          if (key == keyGTWID) {
            // https://global.gotomeeting.com/join/963830877
            href = "https://global.gotowebinar.com/join/" + value;
            stringBuilder.append(HTML.link(href, value));
          } else if (key == keyYMID) {
            href =
                "https://www.vivit-worldwide.org/events/EventDetails.aspx?id=" + value + "&group=";
            stringBuilder.append(HTML.link(href, value));
          } else if (key == keyWebsiteID) {
            href = "https://www.vivit-worldwide.org/members/default.asp?id=" + value;
            stringBuilder.append(HTML.link(href, value));
          } else {
            stringBuilder.append(value);
          }
          stringBuilder.append("</td>");
        }
        stringBuilder.append("</tr>");
        // stringBuilder.append("</tbody>")
      }
    }
    if (gtwEventRegistrationListMap.size() == 1) {
      // stringBuilder.append("<tbody>")
      stringBuilder.append("<tr>");
      for (int i = 0; i < fieldNameMap.size(); i++) {
        stringBuilder.append(
            "<td style="
                + Constants.QUOTE_DOUBLE
                + "border: 1px solid black; border-collapse: collapse;"
                + Constants.QUOTE_DOUBLE
                + ">");
        stringBuilder.append("None");
        stringBuilder.append("</td>");
      }
      stringBuilder.append("</tr>");
      // stringBuilder.append("</tbody>")
    }
    stringBuilder.append("</tbody>");
    stringBuilder.append("</table>");
    String htmlGTWEventRegistrationsLinks = "<html>" + stringBuilder.toString() + "</html>";
    htmlGTWEventRegistrationsLinks = XML.formatPretty(htmlGTWEventRegistrationsLinks);
    FSOTests.fileWrite(
        VivitFoldersFiles.REPORT_HTM_AUTOMATION_EVENT_REGISTRATION_YM,
        htmlGTWEventRegistrationsLinks,
        false);
  }

  /**
   * @param dateTimeFrom
   * @throws Throwable
   */
  @Test
  public void ymApiGetTest(String dateTimeFrom) throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(
        ParameterHelper.getParameters(
            Arrays.asList(Arrays.asList(LABEL_DATE_TIME_FROM, dateTimeFrom))));
    try {
      if (!JavaHelpers.hasValue(dateTimeFrom)) {
        dateTimeFrom = YMDataTests.getInceptionDateTime();
      }
      YMDataTests.setYmApi(new YMAPI());
      Map<String, String> mapResults = new HashMap<>();
      // mapResults =
      // YMDataTests.getYmApi().Sa_PeopleNamespace.profileGet("9B22C96F-4EFF-4D73-A2E3-09BB462687E3")
      sysOut("mapResults:[" + mapResults.toString() + "]");
      final StringBuilder sqlStringBuilder = new StringBuilder();
      sqlStringBuilder.append(JDBCConstants.SELECT + "[API_GUID] ");
      sqlStringBuilder.append(JDBCConstants.FROM + "[" + VivitTables.DOM_VIVIT_MEMBERS + "] ");
      sqlStringBuilder.append(
          JDBCConstants.WHERE + "[" + LABEL_WEB_SITE_MEMBER_ID + "]='10969660'");
      // sqlStringBuilder.append("WHERE
      // [Primary_Technology_Role]LIKE'Chief Information Officer
      // (CIO)Â%'")
      final String xml = "<MemberTypeCode>1PUBLIC</MemberTypeCode>";
      // final String xml = "<CustomFieldResponse FieldCode=" +
      // Constants.QUOTE_DOUBLE + "Technology" + Constants.QUOTE_DOUBLE +
      // " Visibility="
      // + Constants.QUOTE_DOUBLE + Constants.QUOTE_DOUBLE +
      // "<Values><Value>Practitioner</Value></Values></CustomFieldResponse>"
      // final String xml = "<CustomFieldResponse FieldCode=" +
      // Constants.QUOTE_DOUBLE + "Technology" + Constants.QUOTE_DOUBLE +
      // " Visibility="
      // + Constants.QUOTE_DOUBLE + Constants.QUOTE_DOUBLE +
      // "<Values><Value>Chief
      // Information Officer</Value></Values></CustomFieldResponse>"
      YMDataTests.getYmApi().getSaPeopleNamespace().updateMemberData(sqlStringBuilder, xml);
      // StringBuilder sqlStringBuilder = new StringBuilder()
      // String xml = ""
      // // Export from YMAPI & Import to Database
      // // Events
      // sqlStringBuilder =
      // YMDataTests.getYmApi().getEventsNamespace().getAllDataEvents(sqlStringBuilder)
      // // Events
      // mapResults = YMDataTests.getYmApi().getSaEventsNamespace().allGetIDs(null,
      // null, null,
      // null, null)
      // xml = mapResults.get("xml")
      // sqlStringBuilder =
      // YMDataTests.getYmApi().getSaEventsNamespace().getEventSQL(xml)
      // // importDataMembers(VivitTables.DOM_VIVIT_MEMBERS)
      // Abandon YMAPI Session
      mapResults = YMDataTests.getYmApi().getSessionNamespace().abandon();
      sysOut("mapResults:[" + mapResults.toString() + "]");
    } catch (final Exception e) {
      sysOut(e);
    }
  }

  /**
   * @param vivit
   * @throws Throwable
   */
  public static void initializeTest(Vivit vivit) throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList(Arrays.asList(vivit, "vivit"))));
    final String logoSource = vivit.getHomePage().getLogoSource();
    setLogoSource(logoSource);
    checkStatus(LABEL_INITIALIZE_STATUS);
  }

  public static void finalizeTest() throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList(Arrays.asList())));
    List<String> successFileList = FSOTests.filesList(VivitFoldersFiles.PATH_DATA, IExtension.TXT);
    // if (successFileList.size() == FILE_COUNT_EXPECTED)
    // {
    for (String filePathName : successFileList) {
      if (!filePathName.contains(LABEL_INITIALIZE_STATUS)) {
        sysOut("Deleting filePathName:[" + filePathName + "]");
        FSOTests.fileDelete(filePathName);
      }
    }
    // }
  }

  public static void initializeData(SeleniumWebDriver seleniumWebDriver) throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList()));
    String statusName = "initializeData";
    if (successFileExists(statusName)) {
      return;
    }
    successFileDelete(statusName);
    createReportHTMLTestInformation(seleniumWebDriver);
    createDatabaseBackup();
    databaseInitialize(true);
    successFileCreate(statusName);
  }

  /**
   * @param initialize
   * @throws Throwable
   */
  public static void databaseInitialize(boolean initialize) throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList(Arrays.asList("initialize", initialize))));
    if (!initialize) {
      return;
    }
    FileUtils.copyFile(
        new File(VivitFoldersFiles.DATABASE), new File(VivitFoldersFiles.DATABASE_WORKING));
    String initializationQueries = FSOTests.fileReadAll(VivitFoldersFiles.INITIALIZATION_SQL);
    // Run each query individually (this will require connecting to the
    // database for each query.
    // final String[] listQueries =
    // initializationQueries.split(Constants.NEWLINE)
    // for (final String query : listQueries)
    // {
    // sysOut("query:[" + query + "]")
    // SQL.execute(query)
    // }
    // Run all queries at once (this will only require one connection to the
    // database for all queries.
    sysOut("initializationQueries:" + initializationQueries + "]");
    final int recordsUpdated =
        SQL.executeVivit(
            "Initialization",
            JavaHelpers.getCurrentMethodName(),
            new StringBuilder(initializationQueries));
    sysOut("recordsUpdated:[" + recordsUpdated + "]");
    StringBuilder sqlStringBuilder = new StringBuilder();
    sqlStringBuilder.append(
        JDBCConstants.DELETE_FROM + "[" + VivitTables.VIVIT_DATABASE_CHANGES + "];");
    sqlStringBuilder.append(Constants.NEWLINE);
    SQL.executeVivit("DeleteFrom", VivitTables.VIVIT_DATABASE_CHANGES, sqlStringBuilder);
  }

  /**
   * @param source
   * @throws Throwable
   */
  public static void setLogoSource(String source) throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList(Arrays.asList("source", source))));
    FSOTests.fileWrite(VivitFoldersFiles.LOGO_SOURCE, source, false);
  }

  @Test
  public void qbTest() throws QAException {
    final DBParameters dbConnection = new DBParameters("QUICKBOOKS");
    sysOut(dbConnection.toString());
    dbConnection.setUrlPrefix(dbConnection.getUrlPrefix().replace("QuickBooks Data", "//"));
    dbConnection.setName(
        "C:"
            + Constants.DELIMETER_PATH
            + "Users"
            + Constants.DELIMETER_PATH
            + "Public"
            + Constants.DELIMETER_PATH
            + "Documents"
            + Constants.DELIMETER_PATH
            + "Intuit"
            + Constants.DELIMETER_PATH
            + "QuickBooks"
            + Constants.DELIMETER_PATH
            + "Company Files"
            + Constants.DELIMETER_PATH
            + "Vivit.QBW");
    dbConnection.setPassword(EPasswords.QB_VIVIT.getValue());
    dbConnection.setConnectionString(
        dbConnection.getUrlPrefix()
            + dbConnection.getName()
            + Constants.DELIMETER_LIST
            + "memory="
            + dbConnection.getMemory());
    sysOut(dbConnection.toString());
    try {
      final JDBC jdbc = new JDBC("", DATABASE_DEFINITION);
      jdbc.setDbParameters(dbConnection);
      final String sql = JDBCConstants.SELECT_ALL_FROM + "customer";
      jdbc.execute(sql);
    } catch (final Exception e) {
      sysOut(e.getMessage());
    }
  }

  /**
   * @param classMethodType
   */
  public static void successFileCreate(String classMethodType) {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(
        ParameterHelper.getParameters(
            Arrays.asList(Arrays.asList(classMethodType, "classMethodType"))));
    String filePathName = successFileName(classMethodType);
    FSOTests.fileWrite(filePathName, "true", false);
  }

  /**
   * @param classMethodType
   * @return
   */
  public static boolean successFileExists(String classMethodType) {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(
        ParameterHelper.getParameters(
            Arrays.asList(Arrays.asList(classMethodType, "classMethodType"))));
    String filePathName = successFileName(classMethodType);
    return FSOTests.fileExists(filePathName);
  }

  /**
   * @param classMethodType
   */
  public static void successFileDelete(String classMethodType) {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(
        ParameterHelper.getParameters(
            Arrays.asList(Arrays.asList(classMethodType, "classMethodType"))));
    String filePathName = successFileName(classMethodType);
    FSOTests.fileDelete(filePathName);
  }

  /**
   * @param classMethodType
   * @return
   */
  public static String successFileName(String classMethodType) {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(
        ParameterHelper.getParameters(
            Arrays.asList(Arrays.asList(classMethodType, "classMethodType"))));
    return VivitFoldersFiles.PATH_DATA + classMethodType + IExtension.TXT;
  }

  /**
   * @param classMethodType
   * @param successStatus
   * @return
   */
  public static boolean successFileStatus(String classMethodType, boolean successStatus) {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(
        ParameterHelper.getParameters(
            Arrays.asList(
                Arrays.asList(successStatus, "successStatus"),
                Arrays.asList(classMethodType, "classMethodType"))));
    String filePathName = successFileName(classMethodType);
    if (!FSOTests.fileExists(filePathName)) {
      FSOTests.fileWrite(filePathName, String.valueOf(false), false);
    }
    String fileStatus = FSOTests.fileReadAll(filePathName);
    return Boolean.valueOf(fileStatus) == successStatus;
  }

  /**
   * @param dateTimeFrom
   * @throws Throwable
   */
  public static void updateAPIData(String dateTimeFrom) throws Throwable {
    YMDataTests.update(dateTimeFrom);
    GTWebinarDataTests.update();
  }

  /**
   * @param stringBuilderSQLList
   */
  public static void updateTableFromCurrentToPreviousAndInsert(
      List<StringBuilder> stringBuilderSQLList) {
    final String labelCurrent = "_Current";
    final String labelPrevious = "_Previous";
    StringBuilder stringBiulderSQL = stringBuilderSQLList.get(0);
    String[] tableArray = stringBiulderSQL.toString().split(Constants.DELIMETER_LIST);
    // For each table update the tables with the exception of the Calendars
    // table which only has Current.
    for (String table : tableArray) {
      if (!"Calendars".equalsIgnoreCase(table)) {
        dropAndCreateBackUpTables(table);
        String queryDeletePrevious =
            JDBCConstants.DELETE_FROM + "[" + VivitTables.PREFIX + table + labelPrevious + "];";
        sysOut(queryDeletePrevious);
        SQL.executeVivit(
            "DeleteFrom", table + labelPrevious, new StringBuilder(queryDeletePrevious));
        String queryInsertPrevious =
            JDBCConstants.INSERT_INTO
                + "["
                + VivitTables.PREFIX
                + table
                + labelPrevious
                + "] "
                + JDBCConstants.SELECT_ALL_FROM
                + "["
                + VivitTables.PREFIX
                + table
                + labelCurrent
                + "];";
        sysOut(queryInsertPrevious);
        SQL.executeVivit(
            "InsertInto", table + labelPrevious, new StringBuilder(queryInsertPrevious));
      }
      String queryDeleteCurrent =
          JDBCConstants.DELETE_FROM + "[" + VivitTables.PREFIX + table + labelCurrent + "];";
      SQL.executeVivit("DeleteFrom", table + labelCurrent, new StringBuilder(queryDeleteCurrent));
      // Run the queries to update the Current table(s).
      for (int stringBiulderSQLIndex = 1;
          stringBiulderSQLIndex < stringBuilderSQLList.size();
          stringBiulderSQLIndex++) {
        // String table = tableArray[(stringBiulderSQLIndex - 1)]
        String queryInsertCurrent =
            "Inserting data into [" + VivitTables.PREFIX + table + labelCurrent + "];";
        sysOut(queryInsertCurrent);
        stringBiulderSQL = stringBuilderSQLList.get(stringBiulderSQLIndex);
        SQL.executeVivit("InsertInto", table + labelCurrent, stringBiulderSQL);
      }
    }
  }

  @Test
  public void wrapUpTest() throws Throwable {
    wrapUp(false, false);
  }

  public static void cleanUp() throws Throwable {
    deleteOldDataBackups();
    compactDatabase();
  }

  /**
   * @param sendEmail
   * @throws Throwable
   */
  public static void wrapUp(boolean sendEmail, boolean updateData) throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(
        ParameterHelper.getParameters(Arrays.asList(Arrays.asList(LABEL_SEND_EMAIL, sendEmail))));
    if (updateData) {
      updateAPIData(null);
      createReportAutomationStatistics(VivitFoldersFiles.REPORT_XLS_AUTOMATION_S, false);
    }
    createReports(sendEmail, false);
    cleanUp();
    finalizeTest();
    Jenkins.copyFileLog(VivitFoldersFiles.PATH_DATA_TODAY);
  }
}
