package com.cjs.qa.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.microsoft.excel.xls.XLS;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;

public class JDBC {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(JDBC.class));
  private String environment = "TST";
  private String database = "QAAuto";
  private DBParameters dbParameters = null;
  private Connection connection = null;
  private Statement statement = null;
  private ResultSet resultSet = null;
  private ResultSetMetaData resultSetMetaData = null;

  public JDBC(String environment, String database) {
    if (!"".equals(environment)) {
      setEnvironment(environment);
    }
    if (!"".equals(database)) {
      setDatabase(database);
    }
    LOG.info(
        "Instantiating JDBC Instance with Database: [{}], Environment: [{}]",
        getDatabase(),
        getEnvironment());
    switch (getDatabase().toLowerCase(Locale.ENGLISH)) {
      case "autocoder":
        dbParameters = DBConnections.getDBParametersAutoCoder(environment);
        break;
      case "autocoderexcel":
        dbParameters = DBConnections.getDBParametersAutoCoderExcel(environment);
        break;
      case "excel":
        dbParameters = DBConnections.getDBParametersMSExcel(environment);
        break;
      case "jira":
        dbParameters = DBConnections.getDBParametersJira(environment);
        break;
      case "qatools":
        dbParameters = DBConnections.getDBParametersQATools(environment);
        break;
      case "qatoolsweb":
        dbParameters = DBConnections.getDBParametersQaToolsWeb(environment);
        break;
      case "qaauto":
      default:
        dbParameters = DBConnections.getDBParametersQAAuto(environment);
        break;
    }
  }

  public boolean addFieldToTableSQLite(String tableName, String fieldName) throws Exception {
    final StringBuilder stringBuilder = new StringBuilder();
    // stringBuilder.append(JDBCConstants.ALTER_TABLE + "[" + tableName +
    // "] ADD COLUMN '" +
    // fieldName + "' CHAR(25)" + tableName + Constants.DELIMETER_LIST)
    // fieldName + "' TEXT " + JDBCConstants.NOT_NULL + " DEFAULT (null);";)
    stringBuilder.append(
        "\n"
            + JDBCConstants.ALTER_TABLE
            + "["
            + tableName
            + "] ADD COLUMN '"
            + fieldName
            + "' TEXT DEFAULT (null);");
    stringBuilder.append(
        "\n"
            + JDBCConstants.UPDATE
            + "["
            + tableName
            + "] "
            + JDBCConstants.SET
            + "'"
            + fieldName
            + "' = '';");
    executeUpdate(stringBuilder.toString(), false);
    return true;
  }

  public boolean addFieldsToTableSQLite(String tableName, List<String> fieldsAddList)
      throws Exception {
    final StringBuilder stringBuilder = new StringBuilder();
    for (final String fieldName : fieldsAddList) {
      // addFieldToTable(tableName, fieldName)
      stringBuilder.append(
          "\n"
              + JDBCConstants.ALTER_TABLE
              + "["
              + tableName
              + "] ADD COLUMN '"
              + fieldName
              + "' TEXT DEFAULT (null);");
      stringBuilder.append(
          "\n"
              + JDBCConstants.UPDATE
              + "["
              + tableName
              + "] "
              + JDBCConstants.SET
              + "'"
              + fieldName
              + "' = '';");
    }
    executeUpdate(stringBuilder.toString(), false);
    return true;
  }

  public static void classforName(String jdbcDriver) {
    // This method was created so Class.forName can always be called even it
    // it might throw an exception.
    // In the instances I've experienced it throwing an exception, the
    // connection = DriverManager.getConnection(connectionString); still
    // worked.
    try {
      // Class.forName(jdbcDriver)
      Class.forName(jdbcDriver).getDeclaredConstructor().newInstance();
    } catch (final Exception e) {
      LOG.error("Error instantiating driver: {}", e.getMessage(), e);
    }
  }

  public void close() {
    LOG.info(
        "Closing Connection to Database: [{}], Environment: [{}]", getDatabase(), getEnvironment());
    if (resultSetMetaData != null) {
      resultSetMetaData = null;
    }
    if (resultSet != null) {
      try {
        resultSet.close();
      } catch (final SQLException e) {
        LOG.error("Error in JDBC operation", e);
      }
      resultSet = null;
    }
    if (statement != null) {
      try {
        statement.close();
      } catch (final SQLException e) {
        LOG.error("Error in JDBC operation", e);
      }
      statement = null;
    }
    if (connection != null) {
      try {
        connection.close();
      } catch (final SQLException e) {
        LOG.error("Error in JDBC operation", e);
      }
      connection = null;
    }
  }

  private void connect(DBParameters parameters) {
    final String jdbcDriver = parameters.getJdbcDriver();
    final String connectionString = parameters.getConnectionString();
    connection = null;
    String message = connectionString;
    if (connectionString.indexOf("password") >= 0) {
      message = connectionString.substring(0, connectionString.indexOf("password"));
    }
    if (connectionString.indexOf("PWD") >= 0) {
      message = connectionString.substring(0, connectionString.indexOf("PWD"));
    }
    LOG.info(
        "Connecting to Environment: [{}], Database: [{}], Connection String: [{}]",
        getEnvironment(),
        getDatabase(),
        message);
    try {
      classforName(jdbcDriver);
      try {
        connection = DriverManager.getConnection(connectionString);
        getStatement();
      } catch (final SQLException e) {
        LOG.error("Failed to connect - Parameters: {}", parameters.toString(), e);
      }
    } catch (final Exception e) {
      LOG.error("Failed to connect - Parameters: {}", parameters.toString(), e);
    }
  }

  public boolean dropFieldFromTableSQLite(String tableName, String fieldName) throws Exception {
    LOG.info("{}]", Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName());
    String tableBackUp = tableName + "_BAK";
    dropTable(tableBackUp);
    // StringBuilder stringBuilder = new StringBuilder("ALTER
    // "+JDBCConstants.TABLE+"[" +
    // tableName + "] DROP COLUMN '" + fieldName + "';")
    // executeUpdate(stringBuilder.toString(), false);
    List<String> fieldNamesList = getFieldNamesList(tableName);
    StringBuilder stringBuilder =
        new StringBuilder("\n" + JDBCConstants.CREATE_TABLE + "[" + tableName + "_BAK] AS");
    stringBuilder.append("\n\t" + JDBCConstants.SELECT);
    boolean subsequentField = false;
    for (int fieldNameAppendIndex = 0;
        fieldNameAppendIndex < fieldNamesList.size();
        fieldNameAppendIndex++) {
      String fieldNameAppend = fieldNamesList.get(fieldNameAppendIndex);
      if (!fieldNameAppend.equals(fieldName)) {
        stringBuilder.append("[" + fieldNameAppend + "]");
        subsequentField = true;
        if (subsequentField && fieldNameAppendIndex < (fieldNamesList.size() - 1)) {
          stringBuilder.append(",");
        }
      }
    }
    stringBuilder.append("\n\t" + JDBCConstants.FROM + "[" + tableName + "];");
    LOG.debug("stringBuilder: [{}]", stringBuilder.toString());
    executeUpdate(stringBuilder.toString(), false);
    // TODO Update
    if ("1".equals("1")) {
      return true;
    }
    dropTable(tableName);
    renameTable(tableBackUp, tableName);
    return true;
  }

  public boolean dropFieldsFromTableSQLite(String tableName, List<String> fieldsRemoveList)
      throws Exception {
    LOG.info("{}]", Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName());
    // TODO This does not set the fields TEXT DEFAULT (null).
    String tableBackUp = tableName + "_BAK";
    dropTable(tableBackUp);
    List<String> fieldNamesList = getFieldNamesList(tableName);
    StringBuilder stringBuilder =
        new StringBuilder("\n" + JDBCConstants.CREATE_TABLE + "[" + tableBackUp + "] AS");
    stringBuilder.append("\n\t" + JDBCConstants.SELECT);
    boolean subsequentField = false;
    for (int fieldNameAppendIndex = 0;
        fieldNameAppendIndex < fieldNamesList.size();
        fieldNameAppendIndex++) {
      String fieldNameAppend = fieldNamesList.get(fieldNameAppendIndex);
      if (!fieldsRemoveList.contains(fieldNameAppend)) {
        stringBuilder.append("\n\t[" + fieldNameAppend + "]");
        subsequentField = true;
        if (subsequentField && fieldNameAppendIndex < (fieldNamesList.size() - 1)) {
          stringBuilder.append(",");
        }
      }
    }
    stringBuilder.append("\n" + JDBCConstants.FROM + "[" + tableName + "];");
    LOG.debug("stringBuilder: [{}]", stringBuilder.toString());
    executeUpdate(stringBuilder.toString(), false);
    // TODO Update
    if ("1".equals("1")) {
      return true;
    }
    dropTable(tableName);
    renameTable(tableBackUp, tableName);
    return true;
  }

  public boolean dropTable(String tableName) throws Exception {
    LOG.info("{}]", Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName());
    final String sql =
        "\n" + JDBCConstants.DROP_TABLE + JDBCConstants.IF_EXISTS + "[" + tableName + "];";
    executeUpdate(sql, false);
    return true;
  }

  public boolean execute(String sql) {
    connect(dbParameters);
    try {
      final boolean success = statement.execute(sql);
      statement.close();
      connection.commit();
      return success;
    } catch (final Exception e) {
      LOG.error("Error in JDBC operation", e);
      return true;
    } finally {
      try {
        connection.close();
      } catch (final SQLException e) {
        LOG.error("Error in JDBC operation", e);
      }
    }
  }

  public int executeUpdate(String sql, boolean autoCommit) throws Exception {
    connect(dbParameters);
    int recordsUpdated = 0;
    connection.setAutoCommit(autoCommit);
    try {
      recordsUpdated = statement.executeUpdate(sql);
      connection.commit();
      statement.close();
    } catch (final SQLException e) {
      throw e;
    } finally {
      try {
        connection.close();
      } catch (final Exception e) {
        LOG.error("Error in JDBC operation", e);
      }
    }
    return recordsUpdated;
  }

  public int executeUpdates(List<String> sqls, boolean autoCommit) {
    connect(dbParameters);
    int recordsUpdated = 0;
    try {
      connection.setAutoCommit(autoCommit);
      for (final String sql : sqls) {
        recordsUpdated += statement.executeUpdate(sql);
        LOG.debug("sql: [{}]", sql);
        connection.commit();
        statement.close();
      }
      return recordsUpdated;
    } catch (final Exception e) {
      LOG.error("Error in JDBC operation", e);
      return 0;
    } finally {
      try {
        connection.close();
      } catch (final SQLException e) {
        LOG.error("Error in JDBC operation", e);
      }
    }
  }

  public static void exportDataFromTableView(
      String tableViewName,
      String filePathName,
      String sheetName,
      String database,
      boolean overwrite)
      throws Throwable {
    JDBC jdbc = new JDBC("", database);
    List<Map<Integer, String>> tableViewListMap =
        jdbc.queryResultsIndex(
            JDBCConstants.SELECT_ALL + JDBCConstants.FROM + "[" + tableViewName + "]", true);
    List<String> headingList = jdbc.getFieldNamesList(tableViewName);
    jdbc.close();
    if (overwrite && FSOTests.fileExists(filePathName)) {
      FSOTests.fileDelete(filePathName);
    }
    final XLS excel = new XLS(filePathName, sheetName);
    for (int recordIndex = 0; recordIndex < tableViewListMap.size(); recordIndex++) {
      Map<Integer, String> tableViewMap = tableViewListMap.get(recordIndex);
      if (recordIndex == 0) {
        excel.createHeadings(sheetName, headingList);
      } else {
        for (Entry<Integer, String> entry : tableViewMap.entrySet()) {
          int column = entry.getKey();
          String value = entry.getValue();
          if (JavaHelpers.hasValue(value)) {
            excel.writeCell(sheetName, column, recordIndex, value);
          }
        }
      }
    }
    excel.autoSizeColumns(sheetName);
    // Always leave the first sheet selected.
    excel.setSheet(0);
    excel.save();
    excel.close();
  }

  public static void exportTableViewSchemaSQLite(
      String sheetName, String database, boolean overwrite) throws Throwable {
    StringBuilder sqlStringBuilder = new StringBuilder();
    JDBC jdbc = new JDBC("", database);
    String filePathName =
        jdbc.dbParameters.getName().replaceAll(IExtension.SQLITE, "_Schema" + IExtension.XLS);
    if (overwrite && FSOTests.fileExists(filePathName)) {
      FSOTests.fileDelete(filePathName);
    }
    sqlStringBuilder.append(JDBCConstants.SELECT + "[type],[name] ");
    sqlStringBuilder.append(JDBCConstants.FROM + "[" + "sqlite_master" + "] ");
    sqlStringBuilder.append(JDBCConstants.WHERE + "[type] != 'index' ");
    sqlStringBuilder.append(JDBCConstants.ORDER_BY + "[type],[name];");
    List<Map<String, String>> tableViewListMap =
        jdbc.queryResultsString(sqlStringBuilder.toString(), false);
    final XLS excel = new XLS(filePathName, sheetName);
    List<String> headingList = Arrays.asList("Type", "Name", "Field");
    excel.createHeadings(sheetName, headingList);
    int recordIndex = 0;
    for (Map<String, String> tableViewMap : tableViewListMap) {
      String objectType = tableViewMap.get("type");
      String objectName = tableViewMap.get("name");
      List<String> fieldNameList = jdbc.getFieldNamesList(objectName);
      for (String fieldName : fieldNameList) {
        recordIndex++;
        excel.writeCell(sheetName, 0, recordIndex, objectType);
        excel.writeCell(sheetName, 1, recordIndex, objectName);
        excel.writeCell(sheetName, 2, recordIndex, fieldName);
      }
    }
    excel.autoSizeColumns(sheetName);
    // Always leave the first sheet selected.
    excel.setSheet(0);
    excel.save();
    excel.close();
    jdbc.close();
  }

  private String getDatabase() {
    return database;
  }

  private String getEnvironment() {
    return environment;
  }

  public DBParameters getDbParameters() {
    return dbParameters;
  }

  public void setDbParameters(DBParameters dbParameters) {
    this.dbParameters = dbParameters;
  }

  public List<String> getFieldNamesList(String table) {
    final String sql = JDBCConstants.SELECT_ALL_FROM + "[" + table + "] LIMIT 0;";
    final List<String> records = queryResultsList(sql, true);
    return Arrays.asList(records.get(0).split(Constants.SYMBOL_TRADEMARK));
  }

  public List<String> getFieldNamesList(String tableName, boolean sqlite) {
    final List<String> listResults = new ArrayList<>();
    try {
      String sql = JDBCConstants.SELECT + "TOP 0 " + JDBCConstants.FROM + tableName;
      if (sqlite) {
        sql = JDBCConstants.SELECT_ALL_FROM + "[" + tableName + "] " + JDBCConstants.LIMIT + "0";
      }
      resultSet = queryResults(sql);
      resultSetMetaData = resultSet.getMetaData();
      final int columns = resultSetMetaData.getColumnCount();
      for (int index = 1; index <= columns; index++) {
        listResults.add(resultSetMetaData.getColumnName(index));
      }
      return listResults;
    } catch (final Exception e) {
      LOG.error("Error in JDBC operation", e);
      return Collections.emptyList();
    }
  }

  public Map<String, String> getFieldNamesMap(String table) {
    final String sql = JDBCConstants.SELECT_ALL_FROM + "[" + table + "] LIMIT 0;";
    final List<Map<String, String>> records = queryResultsString(sql, true);
    return records.get(0);
  }

  public Map<String, String> getFieldNamesMap(String tableName, boolean sqlite) {
    List<String> fieldNameList = getFieldNamesList(tableName, sqlite);
    Map<String, String> fieldNameMap = new HashMap<>();
    for (String fieldName : fieldNameList) {
      fieldNameMap.put(fieldName, null);
    }
    return fieldNameMap;
  }

  private void getStatement() {
    try {
      statement = connection.createStatement();
    } catch (final SQLException e) {
      LOG.error("Error in JDBC operation", e);
    }
  }

  public ResultSet queryResults(String sql) {
    connect(dbParameters);
    try {
      return statement.executeQuery(sql);
    } catch (final Exception e) {
      LOG.error("Error in JDBC operation", e);
      return null;
    }
  }

  public String queryResults(String sql, String delimeter, boolean includeColumnNames) {
    LOG.debug("sql: [{}]", sql);
    String result = "";
    try {
      resultSet = queryResults(sql);
      resultSetMetaData = resultSet.getMetaData();
      final int columns = resultSetMetaData.getColumnCount();
      StringBuilder stringBuilderHeadings = new StringBuilder();
      if (includeColumnNames) {
        for (int index = 1; index < columns + 1; index++) {
          if (!"".equals(stringBuilderHeadings.toString()) && columns < columns + 1) {
            stringBuilderHeadings.append(delimeter);
          }
          stringBuilderHeadings.append(resultSetMetaData.getColumnName(index));
        }
      }
      StringBuilder stringBuilderColumn = new StringBuilder();
      StringBuilder stringBuilderColumns = new StringBuilder();
      while (resultSet.next()) {
        if (!"".equals(stringBuilderColumns.toString())) {
          stringBuilderColumns.append(Constants.NEWLINE);
        }
        for (int index = 1; index <= columns; index++) {
          if (!"".equals(stringBuilderColumn.toString())) {
            stringBuilderColumn.append(delimeter);
          }
          stringBuilderColumn.append(resultSet.getString(index));
        }
        stringBuilderColumns.append(stringBuilderColumn.toString());
        stringBuilderColumn = new StringBuilder();
      }
      StringBuilder stringBuilderResult = new StringBuilder();
      if (includeColumnNames) {
        stringBuilderResult.append(stringBuilderHeadings.toString());
      }
      if (includeColumnNames && stringBuilderColumns.length() > 0) {
        stringBuilderResult.append(Constants.NEWLINE);
      }
      stringBuilderResult.append(stringBuilderColumns.toString());
      result = stringBuilderResult.toString();
      return result;
    } catch (final Exception e) {
      LOG.error("Error in JDBC operation", e);
      return null;
    }
  }

  public List<Map<Integer, String>> queryResultsIndex(String sql, boolean includeColumnNames) {
    LOG.debug("sql: [{}]", sql);
    final List<Map<Integer, String>> listResults = new ArrayList<>();
    try {
      resultSet = queryResults(sql);
      resultSetMetaData = resultSet.getMetaData();
      final int columns = resultSetMetaData.getColumnCount();
      if (includeColumnNames) {
        Map<Integer, String> mapHeadings = new HashMap<>();
        for (int index = 1; index <= columns; index++) {
          mapHeadings.put(index - 1, resultSetMetaData.getColumnName(index));
        }
        listResults.add(listResults.size(), mapHeadings);
      }
      while (resultSet.next()) {
        Map<Integer, String> mapRecord = new HashMap<>();
        for (int index = 1; index <= columns; index++) {
          mapRecord.put(index - 1, resultSet.getString(index));
        }
        listResults.add(listResults.size(), mapRecord);
      }
      return listResults;
    } catch (final Exception e) {
      LOG.error("Error in JDBC operation", e);
      return Collections.emptyList();
    }
  }

  public List<String> queryResultsList(String sql, boolean includeColumnNames) {
    LOG.debug("sql: [{}]", sql);
    final List<String> list = new ArrayList<>();
    StringBuilder stringBuilder = new StringBuilder();
    try {
      resultSet = queryResults(sql);
      resultSetMetaData = resultSet.getMetaData();
      final int columns = resultSetMetaData.getColumnCount();
      for (int index = 1; index <= columns; index++) {
        stringBuilder.append(resultSetMetaData.getColumnName(index));
        stringBuilder.append(Constants.SYMBOL_TRADEMARK);
      }
      if (includeColumnNames) {
        list.add(stringBuilder.toString());
      }
      while (resultSet.next()) {
        stringBuilder = new StringBuilder();
        for (int index = 1; index <= columns; index++) {
          stringBuilder.append(resultSet.getString(index));
          stringBuilder.append(Constants.SYMBOL_TRADEMARK);
        }
        stringBuilder.append(Constants.NEWLINE);
        list.add(stringBuilder.toString());
      }
      return list;
    } catch (final Exception e) {
      LOG.error("Error in JDBC operation", e);
      return Collections.emptyList();
    }
  }

  public List<Map<String, String>> queryResultsString(String sql, boolean includeColumnNames) {
    LOG.debug("sql: [{}]", sql);
    final List<Map<String, String>> listResults = new ArrayList<>();
    try {
      resultSet = queryResults(sql);
      resultSetMetaData = resultSet.getMetaData();
      final int columns = resultSetMetaData.getColumnCount();
      Map<String, String> mapHeadings = new HashMap<>();
      List<String> listHeadings = new ArrayList<>();
      for (int index = 1; index <= columns; index++) {
        mapHeadings.put(
            resultSetMetaData.getColumnName(index), resultSetMetaData.getColumnName(index));
        listHeadings.add(resultSetMetaData.getColumnName(index));
      }
      if (includeColumnNames) {
        listResults.add(listResults.size(), mapHeadings);
      }
      while (resultSet.next()) {
        Map<String, String> mapRecord = new HashMap<>();
        for (int index = 1; index <= columns; index++) {
          mapRecord.put(listHeadings.get(index - 1), resultSet.getString(index));
        }
        listResults.add(listResults.size(), mapRecord);
      }
      return listResults;
    } catch (final Exception e) {
      LOG.error("Error in JDBC operation", e);
      return Collections.emptyList();
    }
  }

  public boolean renameTable(String tableNameOld, String tableNameNew) throws Exception {
    LOG.info("{}]", Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName());
    StringBuilder stringBuilder =
        new StringBuilder("\n" + JDBCConstants.ALTER_TABLE + "[" + tableNameOld + "]");
    stringBuilder.append("\nRENAME TO [" + tableNameNew + "];");
    executeUpdate(stringBuilder.toString(), false);
    return true;
  }

  private void setEnvironment(String environment) {
    this.environment = environment;
  }

  private void setDatabase(String database) {
    this.database = database;
  }
}
