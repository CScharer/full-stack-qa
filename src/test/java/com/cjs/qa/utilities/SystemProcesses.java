package com.cjs.qa.utilities;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.junit.Assert;

import com.cjs.qa.core.Environment;
import com.cjs.qa.jdbc.JDBC;
import com.cjs.qa.jdbc.JDBCConstants;
import com.cjs.qa.jdbc.SQL;

public final class SystemProcesses {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SystemProcesses.class));

  private SystemProcesses() {
    // Utility class - prevent instantiation
  }

  private static JDBC jdbc = new JDBC("", "");
  public static final String FILE_DATA_CSV = Environment.getFolderData() + "Data" + IExtension.CSV;
  public static final String USERID = "CHRIS";
  public static final String SYSTEM = "CScharer-Laptop-EA92K856";
  public static final String FORMAT = "CSV";
  public static final String EXTENSION = "." + FORMAT.toLowerCase(Locale.ENGLISH);
  public static final String PRIMARY_KEY_PROCESS = "PID";
  public static final List<String> LIST_PROCESS_TYPE =
      Arrays.asList("M;SVC;APPS;V".split(Constants.DELIMETER_LIST));
  public static final List<String> LIST_FIELDS_PROCESS_STANDARD =
      Arrays.asList("Image Name;PID".split(Constants.DELIMETER_LIST));
  public static final Map<String, List<String>> FIELDS_PROCESS_MAP = getProcessFields();

  public static void check() {
    try {
      final String dateTimeStamp = DateHelpersTests.getCurrentDateAndTime();
      createBackupTable("t_Core_Processes");
      SQL.execute(JDBCConstants.DELETE_FROM + "[t_Core_Processes]");
      for (final String processType : LIST_PROCESS_TYPE) {
        final String fileData = Environment.getFolderData() + "Process_" + processType + EXTENSION;
        String command =
            "CMD /C TASKLIST /S "
                + SYSTEM
                + "/U "
                + USERID
                + " /P PASSWORD /"
                + processType
                + " /FO:"
                + FORMAT
                + ">"
                + fileData;
        command = "CMD /C TASKLIST /" + processType + " /FO:" + FORMAT + ">" + fileData;
        command = "CMD /C TASKLIST /" + processType + " /FO:" + FORMAT + ">" + FILE_DATA_CSV;
        LOG.debug("command: [{}]", command);
        final Map<String, String> mapResults = CommandLineTests.runProcess(command, true);
        LOG.debug("mapResults: [{}]", mapResults.toString());
        final String tableName = "t_Core_Processes_" + processType;
        final List<String> fieldsList = FIELDS_PROCESS_MAP.get(processType);
        importData(dateTimeStamp, tableName, fieldsList);
        final StringBuilder sqlStringBuilder = new StringBuilder();
        // Java 17: Switch expression with block syntax
        switch (processType) {
          case "M" -> {
            sqlStringBuilder.append(JDBCConstants.INSERT_INTO + "[t_Core_Processes] ");
            sqlStringBuilder.append("([DateTimeStamp],[PID],[Image Name],[Modules]) ");
            sqlStringBuilder.append(
                JDBCConstants.SELECT + "[DateTimeStamp],[PID],[Image Name],[Modules] ");
            sqlStringBuilder.append(JDBCConstants.FROM + "[t_Core_Processes_" + processType + "];");
          }
          default -> {
            final List<Map<String, String>> records =
                jdbc.queryResultsString(
                    JDBCConstants.SELECT_ALL_FROM + "[t_Core_Processes_" + processType + "]", true);
            final List<String> listField = new ArrayList<>();
            for (int recordIndex = 0; recordIndex < records.size(); recordIndex++) {
              final Map<String, String> mapRecord = records.get(recordIndex);
              if (recordIndex == 0) {
                for (final String key : mapRecord.keySet()) {
                  if (!"DateTimeStamp".equals(key) && !LIST_FIELDS_PROCESS_STANDARD.contains(key)) {
                    listField.add(key);
                  }
                }
              } else {
                sqlStringBuilder.append(JDBCConstants.UPDATE + "[t_Core_Processes] ");
                sqlStringBuilder.append("SET ");
                for (int fieldIndex = 0; fieldIndex < listField.size(); fieldIndex++) {
                  sqlStringBuilder.append("[");
                  final String field = listField.get(fieldIndex);
                  {
                    final String value = mapRecord.get(field).replaceAll("'", "''");
                    if ("Mem Usage".equals(field)) {
                      sqlStringBuilder.append(field + "_" + processType + "]='" + value + "'");
                    } else {
                      sqlStringBuilder.append(field + "]='" + value + "'");
                    }
                    if (fieldIndex < (listField.size() - 1)) {
                      sqlStringBuilder.append(",");
                    }
                  }
                }
                sqlStringBuilder.append(
                    " "
                        + JDBCConstants.WHERE
                        + "[PID]="
                        + mapRecord.get(PRIMARY_KEY_PROCESS)
                        + Constants.DELIMETER_LIST);
                sqlStringBuilder.append(Constants.NEWLINE);
              }
            }
          }
        }
        SQL.execute(sqlStringBuilder.toString());
      }
    } catch (final Exception e) {
      LOG.error("Error in SystemProcesses", e);
    }
  }

  private static void createBackupTable(String tableName) {
    final String tableNamePrevous =
        tableName.replace("t_Core_Processes", "t_Core_ProcessesPrevious");
    String sql = JDBCConstants.DROP_TABLE + JDBCConstants.IF_EXISTS + "[" + tableNamePrevous + "]";
    SQL.execute(sql);
    sql =
        JDBCConstants.CREATE_TABLE
            + "["
            + tableNamePrevous
            + "] "
            + JDBCConstants.AS
            + JDBCConstants.SELECT_ALL_FROM
            + "["
            + tableName
            + "]";
    SQL.execute(sql);
  }

  public static Map<String, List<String>> getProcessFields() {
    final Map<String, List<String>> fieldsProcessMap = new HashMap<>();
    StringBuilder sqlStringBuilder = new StringBuilder();
    final List<String> listFieldsProcessGrouped = new ArrayList<>();
    listFieldsProcessGrouped.addAll(LIST_FIELDS_PROCESS_STANDARD);
    for (final String processType : LIST_PROCESS_TYPE) {
      final List<String> listFieldsProcess = new ArrayList<>();
      List<String> listFieldsTemp = new ArrayList<>();
      listFieldsProcess.addAll(LIST_FIELDS_PROCESS_STANDARD);
      // Java 17: Switch expression with block syntax
      switch (processType) {
        case "APPS" -> {
          listFieldsTemp = Arrays.asList("Mem Usage;Package Name".split(Constants.DELIMETER_LIST));
          listFieldsProcess.addAll(listFieldsTemp);
        }
        case "M" -> {
          listFieldsTemp = Arrays.asList("Modules".split(Constants.DELIMETER_LIST));
          listFieldsProcess.addAll(listFieldsTemp);
        }
        case "SVC" -> {
          listFieldsTemp = Arrays.asList("Services".split(Constants.DELIMETER_LIST));
          listFieldsProcess.addAll(listFieldsTemp);
        }
        case "V" -> {
          listFieldsTemp =
              Arrays.asList(
                  "Session Name;Session#;Mem Usage;Status;User Name;CPU Time;Window Title"
                      .split(Constants.DELIMETER_LIST));
          listFieldsProcess.addAll(listFieldsTemp);
        }
        default -> LOG.warn("Unknown process type: {}. Using standard fields only.", processType);
      }
      fieldsProcessMap.put(processType, listFieldsProcess);
      sqlStringBuilder.append(
          Constants.nlTab(0, 0)
              + JDBCConstants.CREATE_TABLE
              + "`t_Core_Processes_"
              + processType
              + "` (");
      sqlStringBuilder.append(
          Constants.nlTab(1, 1)
              + "`DateTimeStamp` TEXT "
              + JDBCConstants.NOT_NULL
              + "DEFAULT (null),");
      for (int listIndex = 0; listIndex < listFieldsProcess.size(); listIndex++) {
        final String fieldName = listFieldsProcess.get(listIndex);
        if (!LIST_FIELDS_PROCESS_STANDARD.contains(fieldName)) {
          if ("Mem Usage".equals(fieldName)) {
            listFieldsProcessGrouped.add(fieldName + "_" + processType);
          } else {
            listFieldsProcessGrouped.add(fieldName);
          }
        }
        if (PRIMARY_KEY_PROCESS.equals(fieldName)) {
          sqlStringBuilder.append(
              Constants.nlTab(1, 1)
                  + "`"
                  + fieldName
                  + "` NUMERIC "
                  + JDBCConstants.NOT_NULL
                  + "DEFAULT -1");
        } else {
          sqlStringBuilder.append(
              Constants.nlTab(1, 1)
                  + "`"
                  + fieldName
                  + "` TEXT "
                  + JDBCConstants.NOT_NULL
                  + "DEFAULT (null)");
        }
        if (listIndex < (listFieldsProcess.size() - 1)) {
          sqlStringBuilder.append(",");
        }
      }
      sqlStringBuilder.append(Constants.nlTab(0, 0) + ");");
    }
    // sqlStringBuilder = new StringBuilder();
    sqlStringBuilder.append(
        Constants.nlTab(0, 0) + JDBCConstants.CREATE_TABLE + "`t_Core_Processes` (");
    sqlStringBuilder.append(
        Constants.nlTab(1, 1)
            + "`DateTimeStamp` TEXT "
            + JDBCConstants.NOT_NULL
            + "DEFAULT (null),");
    for (int listIndex = 0; listIndex < listFieldsProcessGrouped.size(); listIndex++) {
      final String fieldName = listFieldsProcessGrouped.get(listIndex);
      if (PRIMARY_KEY_PROCESS.equals(fieldName)) {
        sqlStringBuilder.append(
            Constants.nlTab(1, 1)
                + "`"
                + fieldName
                + "` NUMERIC "
                + JDBCConstants.NOT_NULL
                + "DEFAULT -1");
      } else {
        sqlStringBuilder.append(Constants.nlTab(1, 1) + "`" + fieldName + "` TEXT DEFAULT (null)");
      }
      if (listIndex < (listFieldsProcessGrouped.size() - 1)) {
        sqlStringBuilder.append(",");
      }
    }
    sqlStringBuilder.append(Constants.nlTab(0, 0) + ");");
    // final String sql = sqlStringBuilder.toString();
    // LOG.debug("{}", sql);
    sqlStringBuilder = null;
    // jdbc.executeUpdate(sql, false);
    // sqlStringBuilder = new StringBuilder();
    return fieldsProcessMap;
  }

  private static void importData(
      String dateTimeStamp, String tableName, List<String> listHeadingsExpected) {
    final int recordStart = 0;
    final int recordLimit = 25000;
    createBackupTable(tableName);
    SQL.execute("DELETE FROM [" + tableName + "]");
    StringBuilder stringBuilder = new StringBuilder();
    try {
      JavaHelpers.sleep(0, Constants.MILLISECONDS * 15);
      final Map<String, String> mapHeadingsExpected = new HashMap<>();
      for (final String heading : listHeadingsExpected) {
        mapHeadingsExpected.put(heading, "");
      }
      try (Reader reader = new FileReader(FILE_DATA_CSV)) {
        // Note: .build() is deprecated in Commons CSV 1.14.1+ but still required by the library
        // The Commons CSV library has not provided a non-deprecated alternative yet.
        // Using CSVParser.parse() with the builder pattern is the recommended approach.
        // See:
        // https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.Builder.html
        @SuppressWarnings("deprecation")
        CSVFormat format = CSVFormat.RFC4180.builder().setHeader().build();
        Iterable<CSVRecord> records = CSVParser.parse(reader, format);
        boolean headingsMapped = false;
        Map<String, String> mapHeadingsCSV = null;
        stringBuilder = new StringBuilder();
        int recordNumber = 0;
        for (final CSVRecord record : records) {
          // final int recordNumber = (int) record.getRecordNumber();
          recordNumber++;
          if (recordNumber >= (recordStart + 1)) {
            if (!headingsMapped) {
              mapHeadingsCSV = record.toMap();
              LOG.debug("listHeadingsExpected: [{}]", listHeadingsExpected);
              LOG.debug("mapHeadingsExpected: [{}]", mapHeadingsExpected.keySet());
              LOG.debug("mapHeadingsCSV: [{}]", mapHeadingsCSV.keySet());
              if (!mapHeadingsCSV.keySet().equals(mapHeadingsExpected.keySet())) {
                Assert.fail("CSV headings do not match Expected DB headings");
              }
              for (final String key : mapHeadingsCSV.keySet()) {
                LOG.debug("heading: [{}]", key);
              }
              headingsMapped = true;
            }
            final Map<String, String> memberMap = new HashMap<>();
            for (final String field : listHeadingsExpected) {
              try {
                memberMap.put(field, record.get(field));
              } catch (final Exception e) {
                LOG.error("Error in SystemProcesses", e);
              }
            }
            memberMap.put("DateTimeStamp", dateTimeStamp);
            stringBuilder =
                SQL.appendStringBuilderSQLInsertRecord(tableName, stringBuilder, memberMap, true);
            if (recordNumber % recordLimit == 0) {
              SQL.execute(stringBuilder.toString());
              stringBuilder = new StringBuilder();
            }
          }
        }
        SQL.execute(stringBuilder.toString());
        records = null;
      }
      FSOTests.fileDelete(FILE_DATA_CSV);
    } catch (final Exception e) {
      LOG.error("Error in SystemProcesses", e);
    }
  }
}
