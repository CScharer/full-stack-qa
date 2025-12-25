package com.cjs.qa.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.core.AutGui;
import com.cjs.qa.core.Environment;
import com.cjs.qa.core.QAException;
import com.cjs.qa.utilities.CJSConstants;
import com.cjs.qa.utilities.CommandLineTests;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.vivit.VivitFoldersFiles;

public class SQL {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(SQL.class));
  private static final String DATABASE_DEFINITION = "QAAuto";
  private static final String TABLE_DOM_USERS = "tblDOM_Users";
  private static final String TABLE_COMPANY = "tblCompany";
  private static final String TABLE_FILENET = "tblDOM_Filenet";
  private static final String TABLE_ENVIRONMENTS = "tblEnvironments";
  private static final String TABLE_PSTAR = "tblDOM_PSTAR_Service_Accounts";
  private static final String TABLE_SUBMISSIONLOG = "tblSubmissionLog";

  public int exUpdateDbUserToAdmin(String eMail) {
    String sql =
        JDBCConstants.INSERT_INTO
            + "[EntDB_"
            + Environment.getEnvironment()
            + "].[dbo].[PartyRole] VALUES(("
            + JDBCConstants.SELECT
            + "[PartyId] FROM [SSO_"
            + Environment.getEnvironment()
            + "].[dbo].[SSOUser] "
            + JDBCConstants.WHERE
            + "[UserName] = '"
            + eMail
            + "'), 'Admin');";
    sql = AutGui.updateSQL(sql);
    LOG.debug("SQL: {}", sql);
    int recordsUpdated = 0;
    final JDBC jdbc = new JDBC("TST", "Jira");
    try {
      recordsUpdated = jdbc.executeUpdate(sql, true);
      jdbc.close();
    } catch (final Exception e) {
      LOG.error("Error in SQL operation", e);
    }
    return recordsUpdated;
  }

  public int exUpdateDbUserToNonAdmin(String eMail) {
    String sql =
        "DELETE FROM [EntDB_"
            + Environment.getEnvironment()
            + "].[dbo].[PartyRole] "
            + JDBCConstants.WHERE
            + "[PartyId] = ("
            + JDBCConstants.SELECT
            + "[PartyId] FROM [SSO_"
            + Environment.getEnvironment()
            + "].[dbo].[SSOUser] "
            + JDBCConstants.WHERE
            + "[UserName] = '"
            + eMail
            + "')";
    sql = AutGui.updateSQL(sql);
    LOG.debug("SQL: {}", sql);
    int recordsUpdated = 0;
    final JDBC jdbc = new JDBC("TST", "Jira");
    try {
      recordsUpdated = jdbc.executeUpdate(sql, true);
      jdbc.close();
    } catch (final Exception e) {
      LOG.error("Error in SQL operation", e);
    }
    return recordsUpdated;
  }

  public int exSqlGetPartyId(String eMail) {
    int partyID = 0;
    String sql =
        JDBCConstants.SELECT
            + "* "
            + JDBCConstants.FROM
            + "[SSO_"
            + Environment.getEnvironment()
            + "].[dbo].[SSOUser] "
            + JDBCConstants.WHERE
            + "[UserName] = '"
            + eMail
            + "'";
    sql = AutGui.updateSQL(sql);
    final JDBC jdbc = new JDBC("TST", "Jira");
    try (ResultSet resultSet = jdbc.queryResults(sql)) {
      if (resultSet != null && resultSet.next()) {
        partyID = resultSet.getInt("PartyId");
      }
    } catch (final SQLException e) {
      LOG.error("Error in SQL operation: {}", e.getMessage(), e);
    } finally {
      jdbc.close();
    }
    return partyID;
  }

  private static int addDBRecord(String tableName, Map<String, String> map) {
    StringBuilder stringBuilderFields = new StringBuilder();
    StringBuilder stringBuilderValues = new StringBuilder();
    for (final Entry<String, String> entry : map.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();
      key = key.trim();
      if (!"".equals(stringBuilderFields.toString())) {
        stringBuilderFields.append(",");
        stringBuilderValues.append(",");
      }
      if (value == null) {
        value = "";
      }
      stringBuilderFields.append("[" + key + "]");
      stringBuilderValues.append("'" + value + "'");
    }
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(JDBCConstants.INSERT_INTO);
    stringBuilder.append("[" + tableName + "] ");
    stringBuilder.append("(" + stringBuilderFields.toString() + ")");
    stringBuilder.append(JDBCConstants.VALUES);
    stringBuilder.append("(" + stringBuilderValues.toString() + ")");
    final String sql = stringBuilder.toString();
    LOG.debug("SQL: {}", sql);
    int recordsUpdated = 0;
    final JDBC jdbc = new JDBC("", DATABASE_DEFINITION);
    try {
      recordsUpdated = jdbc.executeUpdate(sql, false);
      // jdbc.close()
    } catch (final Exception e) {
      LOG.error("Error in SQL operation", e);
    }
    return recordsUpdated;
  }

  public int addDBRecords(String table, List<String> listFields, List<String> listRecords) {
    String sql = "";
    StringBuilder stringBuilderMega = new StringBuilder();
    StringBuilder stringBuilderFields = new StringBuilder();
    StringBuilder stringBuilderValues = new StringBuilder();
    final List<String> listSQLs = new ArrayList<>();
    for (String listField : listFields) {
      if (!"".equals(stringBuilderFields.toString())) {
        stringBuilderFields.append(",");
      }
      stringBuilderFields.append("[" + listField + "]");
    }
    for (final String listRecord : listRecords) {
      final String[] recordData = listRecord.split(Constants.DELIMETER_LIST);
      // LOG.debug("recordData.length:[{}]", recordData.length);
      for (int index = 0; index < listFields.size(); index++) {
        // for (int recordIndex = 0; recordIndex < recordData.length
        // recordIndex++)
        // String value = recordData[index]
        String value = "";
        if (index > (recordData.length - 1)) {
          value = "";
        } else {
          value = recordData[index];
        }
        if (!"".equals(stringBuilderValues.toString())) {
          stringBuilderValues.append(",");
        }
        if (value == null) {
          value = "";
        }
        stringBuilderValues.append("'" + value + "'");
      }
      final StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(JDBCConstants.INSERT_INTO);
      stringBuilder.append("[" + table + "] ");
      stringBuilder.append("(" + stringBuilderFields.toString() + ")");
      stringBuilder.append(JDBCConstants.VALUES);
      stringBuilder.append("(" + stringBuilderValues.toString() + ")");
      stringBuilderValues = new StringBuilder();
      sql = stringBuilder.toString();
      LOG.debug("SQL: {}", sql);
      stringBuilderMega.append(sql);
      stringBuilderMega.append(Constants.DELIMETER_LIST);
      listSQLs.add(sql);
      // final JDBC jdbc = new JDBC("", DATABASE_DEFINITION)
      // final int recordsUpdated = jdbc.executeUpdate(sql, false)
      // LOG.debug("recordsUpdated:[{}]", recordsUpdated);
    }
    int recordsUpdated = 0;
    final JDBC jdbc = new JDBC("", DATABASE_DEFINITION);
    try {
      LOG.debug("SQL statements: {}", stringBuilderMega.toString());
      recordsUpdated = jdbc.executeUpdate(stringBuilderMega.toString(), false);
      LOG.info("recordsUpdated: [{}]", recordsUpdated);
      // recordsUpdated = jdbc.executeUpdates(sqls, false)
      // jdbc.close()
    } catch (final Exception e) {
      LOG.error("Error in SQL operation", e);
    }
    return recordsUpdated;
  }

  public static List<String> appendQuery(
      String table,
      String fields,
      String values,
      StringBuilder sqlStringBuilder,
      List<String> sqlList) {
    fields = "[" + fields.replaceAll(",", "],[") + "]";
    values = "'" + values.replace("'", "''").replaceAll(",", "','") + "'";
    // Append the record and create all records in the table at once later.
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(JDBCConstants.INSERT_INTO);
    stringBuilder.append("[" + table + "] ");
    stringBuilder.append("(" + fields + ")");
    stringBuilder.append(JDBCConstants.VALUES);
    stringBuilder.append("(" + values + ");");
    sqlStringBuilder.append(stringBuilder.toString());
    sqlList.add(stringBuilder.toString());
    return sqlList;
  }

  public static StringBuilder appendStringBuilderSQLInsertRecord(
      String tableName,
      StringBuilder sqlStringBuilder,
      Map<String, String> mapRecord,
      boolean fullyQualified) {
    String fqL = "";
    String fqR = "";
    if (fullyQualified) {
      fqL = "[";
      fqR = "]";
    }
    final List<String> fieldsList = new ArrayList<>();
    for (final Entry<String, String> entry : mapRecord.entrySet()) {
      final String fieldName = entry.getKey();
      fieldsList.add(fieldName);
    }
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(JDBCConstants.INSERT_INTO + fqL + tableName + fqR + " (");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append(fqL);
    for (int fieldNameIndex = 0; fieldNameIndex < fieldsList.size(); fieldNameIndex++) {
      final String fieldName = fieldsList.get(fieldNameIndex);
      stringBuilder.append(fieldName);
      if (fieldNameIndex < (fieldsList.size() - 1)) {
        stringBuilder.append(fqR + "," + Constants.NEWLINE + fqL);
      }
    }
    stringBuilder.append(fqR);
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append(") VALUES (");
    for (int fieldNameIndex = 0; fieldNameIndex < fieldsList.size(); fieldNameIndex++) {
      final String fieldName = fieldsList.get(fieldNameIndex);
      final String fieldValue = mapRecord.get(fieldName);
      stringBuilder.append(Constants.NEWLINE);
      if (fieldValue == null) {
        stringBuilder.append("''");
      } else {
        stringBuilder.append("'" + parseQuote(fieldValue) + "'");
      }
      if (fieldNameIndex < (fieldsList.size() - 1)) {
        stringBuilder.append(",");
      }
    }
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append(");");
    stringBuilder.append(Constants.NEWLINE);
    sqlStringBuilder.append(stringBuilder.toString());
    // LOG.debug("Appending Records:[{}]", stringBuilder.toString());
    return sqlStringBuilder;
  }

  public static StringBuilder appendStringBuilderSQLInsertRecord(
      String tableName, StringBuilder sqlStringBuilder, Map<String, String> mapPage) {
    final String keySet = mapPage.keySet().toString();
    final String fieldNames = keySet.substring(1, keySet.length() - 1);
    final String[] fields = fieldNames.split(", ");
    // sqlStringBuilder.append(JDBCConstants.INSERT_INTO +"[" +
    // tableName
    // + "] (")
    // sqlStringBuilder.append(keySet.replace(", ", "],["))
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(JDBCConstants.INSERT_INTO + "[" + tableName + "] (");
    stringBuilder.append(fieldNames.replace(", ", ","));
    stringBuilder.append(") VALUES (");
    for (int keyIndex = 0; keyIndex < fields.length; keyIndex++) {
      // for (String key : hashMapRecord.keySet())
      final String fieldValue = mapPage.get(fields[keyIndex]);
      if (fieldValue == null) {
        stringBuilder.append("''");
      } else {
        stringBuilder.append("'" + parseQuote(fieldValue) + "'");
      }
      if (keyIndex < (fields.length - 1)) {
        stringBuilder.append(",");
      }
    }
    stringBuilder.append(");");
    stringBuilder.append(Constants.NEWLINE);
    sqlStringBuilder.append(stringBuilder.toString());
    // LOG.debug("Appending Records:[{}]", stringBuilder.toString());
    return sqlStringBuilder;
  }

  public static int addPSTARPDFCompareRecord(String table, Map<String, String> map) {
    return addDBRecord(table, map);
  }

  public static Connection createDatabaseSQLite(String fileName) throws Throwable {
    final String url = "jdbc:sqlite:" + fileName;
    Class.forName("org" + IExtension.SQLITE + IExtension.JDBC);
    try (Connection connection = DriverManager.getConnection(url)) {
      if (connection != null) {
        final DatabaseMetaData databaseMetaData = connection.getMetaData();
        LOG.info("Driver Name: [{}]", databaseMetaData.getDriverName());
        LOG.info("A new Database [{}] has been created.", fileName);
      }
      return connection;
    } catch (Exception e) {
      throw new QAException("createDatabaseSQLite", e);
    }
  }

  public static int createTableFromQuerySQLite(String tableName, String query) throws Exception {
    final String sql = "CREATE TABLE [" + tableName + "] AS " + query;
    return execute(sql);
  }

  public static void createTableView(Connection connection, String sql, String tableViewName)
      throws Throwable {
    LOG.debug("sql: [{}]", sql);
    try (Statement statement = connection.createStatement()) {
      statement.setQueryTimeout(30); // set timeout to 30 sec.
      // statement.executeUpdate(JDBCConstants.DROP_TABLE +
      // JDBCConstants.IF_EXISTS + tableViewName)
      statement.executeUpdate(sql);
      try (ResultSet resultSet =
          statement.executeQuery(
              JDBCConstants.SELECT_COUNT + "AS Count " + JDBCConstants.FROM + tableViewName)) {
        final DatabaseMetaData databaseMetaData = connection.getMetaData();
        LOG.info("Driver Name: [{}]", databaseMetaData.getDriverName());
        LOG.info("A new Table/View [{}] has been created.", tableViewName);
        while (resultSet.next()) {
          // Read the result set
          LOG.info("Count = {}", resultSet.getString("Count"));
        }
      }
    } catch (Exception e) {
      throw new QAException("createTableView", e);
    }
  }

  public static int executeVivit(
      String action, String sectionName, StringBuilder sqlStringBuilder) {
    LOG.debug("sqlLength: [{}]", sqlStringBuilder.toString().length());
    int recordsAffected = 0;
    LOG.info("{} {}", action, sectionName);
    if (sqlStringBuilder.toString().length() != 0) {
      int sqlFileCount =
          FSOTests.filesGetCount(VivitFoldersFiles.PATH_API_DATA_YM_SQL, IExtension.SQL);
      String sqlFilePrefix = JavaHelpers.formatNumber(sqlFileCount, "000");
      String sqlFileName = sqlFilePrefix + "-" + action + "-" + sectionName + IExtension.SQL;
      String sqlFilePathName = VivitFoldersFiles.PATH_API_DATA_YM_SQL + sqlFileName;
      String sql = sqlStringBuilder.toString();
      FSOTests.fileWrite(sqlFilePathName, sql, true);
      recordsAffected = execute(sql);
      String logLine = sqlFileName + Constants.TAB + recordsAffected + Constants.NEWLINE;
      if (!FSOTests.fileExists(VivitFoldersFiles.FILE_SQL_LOG)) {
        FSOTests.fileWrite(
            VivitFoldersFiles.FILE_SQL_LOG,
            "SQL File" + Constants.TAB + "Records Updated" + Constants.NEWLINE,
            true);
      }
      FSOTests.fileWrite(VivitFoldersFiles.FILE_SQL_LOG, logLine, true);
    }
    return recordsAffected;
  }

  public static int execute(String sql) {
    LOG.debug(
        "***ClassMethodDebug***: [{}] called by [{}]",
        JavaHelpers.getCurrentClassMethodDebugName(),
        JavaHelpers.getCallingMethodName());
    // LOG.debug("{}", sql);
    int recordsUpdated = 0;
    final JDBC jdbc = new JDBC("", DATABASE_DEFINITION);
    try {
      String[] records = sql.split(Constants.DELIMETER_LIST);
      String messagePre = "";
      String messagePost = "";
      if (sql.indexOf(JDBCConstants.DELETE_FROM) > -1) {
        records = sql.split(JDBCConstants.DELETE_FROM);
        messagePre = "DELETING";
        messagePost = "DELETED";
      } else if (sql.indexOf(JDBCConstants.INSERT_INTO) > -1) {
        records = sql.split(JDBCConstants.INSERT_INTO);
        messagePre = "INSERTING";
        messagePost = "INSERTED";
      } else if (sql.indexOf(JDBCConstants.UPDATE) > -1) {
        records = sql.split(JDBCConstants.UPDATE);
        messagePre = "UPDATING";
        messagePost = "UPDATED";
      } else if (sql.indexOf(JDBCConstants.DROP_TABLE) > -1) {
        messagePre = "DROPPING TABLE";
        messagePost = "TABLE DROPPED";
      } else if (sql.indexOf(JDBCConstants.DROP_VIEW) > -1) {
        messagePre = "DROPPING VIEW";
        messagePost = "VIEW DROPPED";
      } else if (sql.indexOf(JDBCConstants.CREATE_TABLE) > -1) {
        messagePre = "CREATING TABLE";
        messagePost = "TABLE CREATED";
      } else if (sql.indexOf(JDBCConstants.CREATE_VIEW) > -1) {
        messagePre = "CREATING VIEW ";
        messagePost = "VIEW CREATED";
      }

      // Java 17: Switch expression with multiple case labels
      switch (messagePre) {
        case "DROPPING TABLE", "DROPPING VIEW", "CREATING TABLE", "CREATING VIEW" ->
            LOG.info("{} {}", messagePre, sql.substring(sql.indexOf('['), sql.indexOf(']') + 1));
        default -> LOG.info("{} [{}] RECORD(S)", messagePre, records.length - 1);
      }
      recordsUpdated = jdbc.executeUpdate(sql, false);
      // Java 17: Switch expression with multiple case labels
      switch (messagePost) {
        case "TABLE DROPPED", "TABLE CREATED", "VIEW DROPPED", "VIEW CREATED" ->
            LOG.info("{} {}", sql.substring(sql.indexOf('['), sql.indexOf(']') + 1), messagePost);
        default -> LOG.info("[{}] RECORD(S) {}", recordsUpdated, messagePost);
      }
    } catch (final Exception e) {
      LOG.error("Error in SQL operation", e);
    }
    return recordsUpdated;
  }

  public static String getCompanyNumber(String company) throws SQLException {
    final String sql =
        JDBCConstants.SELECT
            + "["
            + TABLE_COMPANY
            + "].[Number] "
            + JDBCConstants.FROM
            + "["
            + TABLE_COMPANY
            + "] "
            + JDBCConstants.WHERE
            + "UPPER(["
            + TABLE_COMPANY
            + "].[Abbreviation]) = '"
            + company.toUpperCase(Locale.ENGLISH)
            + "';";
    String companyNumber = "";
    final JDBC jdbc = new JDBC("", DATABASE_DEFINITION);
    try (ResultSet resultSet = jdbc.queryResults(sql)) {
      if (resultSet != null) {
        companyNumber = resultSet.getString("Number");
      }
    } catch (final SQLException e) {
      LOG.error("Error in SQL operation: {}", e.getMessage(), e);
    } finally {
      jdbc.close();
    }
    LOG.info("Value [{}]", companyNumber);
    return companyNumber;
  }

  // public static String getFilenetSplit(String company) throws SQLException
  // {
  // final String sql = JDBCConstants.SELECT+"[" + TABLE_COMPANY +
  // "].[FilenetSplit] "+JDBCConstants.FROM+"[" + TABLE_COMPANY +
  // "] " +
  // JDBCConstants.WHERE + "UPPER([" + TABLE_COMPANY +
  // "].[Abbreviation]) =
  // '" + company.toUpperCase(Locale.ENGLISH) + "';"
  // String filenetSplit = null
  // final JDBC jdbc = new JDBC("", DATABASE_DEFINITION)
  // final ResultSet resultSet = jdbc.queryResults(sql)
  // filenetSplit = resultSet.getString("filenetSplit")
  // LOG.debug("Value [{}]", filenetSplit);
  // jdbc.close()
  // return filenetSplit
  // }
  //
  public static Map<String, String> getPDFCompare(String company, String environment)
      throws SQLException, ClassNotFoundException {
    final Map<String, String> map = new HashMap<>();
    final JDBC jdbc = new JDBC("", DATABASE_DEFINITION);
    // Get the Company Number and FilenetSplit.
    String sql =
        JDBCConstants.SELECT
            + "["
            + TABLE_COMPANY
            + "].* "
            + JDBCConstants.FROM
            + "["
            + TABLE_COMPANY
            + "] "
            + JDBCConstants.WHERE
            + "["
            + TABLE_COMPANY
            + "].[Abbreviation]='"
            + company
            + "';";
    try (ResultSet resultSet = jdbc.queryResults(sql)) {
      if (resultSet != null) {
        map.put("CompanyNumber", resultSet.getString("Number"));
        map.put("FilenetSplit", resultSet.getString("FilenetSplit"));
      }
    }
    // Get the WSDL.
    sql =
        JDBCConstants.SELECT
            + "["
            + TABLE_ENVIRONMENTS
            + "].* "
            + JDBCConstants.FROM
            + "["
            + TABLE_ENVIRONMENTS
            + "] "
            + JDBCConstants.WHERE
            + "["
            + TABLE_ENVIRONMENTS
            + "].[Environment]='"
            + environment
            + "'";
    if (!"".equals(environment)) {
      sql +=
          " " + JDBCConstants.AND + "[" + TABLE_ENVIRONMENTS + "].[Abbreviation]='" + company + "'";
    }
    try (ResultSet resultSet2 = jdbc.queryResults(sql)) {
      if (resultSet2 != null) {
        map.put("WSDL", resultSet2.getString("WSDL"));
      }
    } finally {
      jdbc.close();
    }
    LOG.debug("Map: {}", map);
    return map;
  }

  public static Map<String, String> getPSTARInfo(String company)
      throws SQLException, ClassNotFoundException {
    final Map<String, String> map = new HashMap<>();
    final JDBC jdbc = new JDBC("", DATABASE_DEFINITION);
    // Get the FilenetSplit, Number, Service_Account, Service_Account
    // Password UserID & Filenet Password.
    // String sql = JDBCConstants.SELECT+"c.[Abbreviation],
    // c.[FilenetSplit], c.[Number], p.[Service_Account], p.[Password] AS
    // [pPassword], f.[UserID], f.[Password] AS [fPassword] "
    String sql =
        JDBCConstants.SELECT
            + "c.[Abbreviation], c.[FilenetSplit], c.[Number], p.[Service_Account],"
            + " p.[Password_Java] AS [pPassword], f.[UserID], f.[Password] AS"
            + " [fPassword] ";
    sql += JDBCConstants.FROM + "[" + TABLE_COMPANY + "] c ";
    sql += "LEFT JOIN [" + TABLE_PSTAR + "] p " + JDBCConstants.ON;
    sql += "c.[Abbreviation] = p.[Abbreviation] ";
    sql += "LEFT JOIN [" + TABLE_FILENET + "] f " + JDBCConstants.ON;
    sql += "c.[FilenetSplit] = f.[FilenetSplit] ";
    sql += JDBCConstants.WHERE + "c.[Abbreviation]='" + company.toUpperCase(Locale.ENGLISH) + "';";
    try (ResultSet resultSet = jdbc.queryResults(sql)) {
      if (resultSet != null) {
        map.put("FilenetSplit", resultSet.getString("FilenetSplit"));
        LOG.debug("FilenetSplit: [{}]", map.get("FilenetSplit"));
        map.put("Number", resultSet.getString("Number"));
        LOG.debug("Company Number: [{}]", map.get("Number"));
        map.put("Service_Account", resultSet.getString("Service_Account"));
        LOG.debug("Service_Account: [{}]", map.get("Service_Account"));
        map.put("pPassword", resultSet.getString("pPassword"));
        map.put("UserID", resultSet.getString("UserID"));
        LOG.debug("UserID: [{}]", map.get("UserID"));
        if (map.get("UserID") == null) {
          map.put("UserID", "");
          LOG.debug("UserID: [{}]", map.get("UserID"));
          map.put("fPassword", "");
        } else {
          map.put("fPassword", resultSet.getString("fPassword"));
        }
      }
    } catch (final SQLException e) {
      LOG.error("Error in SQL operation: {}", e.getMessage(), e);
    } finally {
      jdbc.close();
    }
    return map;
  }

  public static String getURL(String company, String environment)
      throws SQLException, ClassNotFoundException {
    // final String sql = JDBCConstants.SELECT+"[URL]
    // "+JDBCConstants.FROM+"[tblURLs]
    // "+JDBCConstants.WHERE+"[ABBREVIATION] = '" + company + "'
    // " +
    // Java 17: Text block for cleaner SQL query construction
    String sql =
        """
      %s[%s].[URL] %s[%s] %s[%s].[Environment]='%s'%s
        """
            .formatted(
                JDBCConstants.SELECT,
                TABLE_ENVIRONMENTS,
                JDBCConstants.FROM,
                TABLE_ENVIRONMENTS,
                JDBCConstants.WHERE,
                TABLE_ENVIRONMENTS,
                environment,
                // NOTE: Using Environment.noPMDUselessParentheses to avoid PMD violation
                // while maintaining code clarity
                Environment.noPMDUselessParentheses(
                    !"".equals(company)
                        ? " "
                            + JDBCConstants.AND
                            + "["
                            + TABLE_ENVIRONMENTS
                            + "].[Abbreviation]='"
                            + company
                            + "'"
                        : ""))
            .trim();
    String url = null;
    final JDBC jdbc = new JDBC("", DATABASE_DEFINITION);
    try (ResultSet resultSet = jdbc.queryResults(sql)) {
      if (resultSet != null) {
        url = resultSet.getString("URL");
      }
    } catch (final SQLException e) {
      LOG.error("Error in SQL operation: {}", e.getMessage(), e);
    } finally {
      jdbc.close();
    }
    LOG.info("Value [{}]", url);
    return url;
  }

  public static String getUserName(String company, String environment)
      throws SQLException, ClassNotFoundException {
    // Java 17: Text block for cleaner SQL query construction
    String sql =
        """
      %s[%s].[UserName] %s[%s] %s[%s].[Environment]='%s'%s
        """
            .formatted(
                JDBCConstants.SELECT,
                TABLE_ENVIRONMENTS,
                JDBCConstants.FROM,
                TABLE_ENVIRONMENTS,
                JDBCConstants.WHERE,
                TABLE_ENVIRONMENTS,
                environment,
                // NOTE: Using Environment.noPMDUselessParentheses to avoid PMD violation
                // while maintaining code clarity
                Environment.noPMDUselessParentheses(
                    !"".equals(company)
                        ? " "
                            + JDBCConstants.AND
                            + "["
                            + TABLE_ENVIRONMENTS
                            + "].[Abbreviation]='"
                            + company
                            + "'"
                        : ""))
            .trim();
    String userName = null;
    final JDBC jdbc = new JDBC("", DATABASE_DEFINITION);
    try (ResultSet resultSet = jdbc.queryResults(sql)) {
      if (resultSet != null) {
        userName = resultSet.getString("UserName");
      }
    } catch (final SQLException e) {
      LOG.error("Error in SQL operation: {}", e.getMessage(), e);
    } finally {
      jdbc.close();
    }
    LOG.info("Value [{}]", userName);
    return userName;
  }

  public static String getUniqueString(String database, String sql) {
    String uniqueString = null;
    final JDBC jdbc = new JDBC("", database);
    try {
      LOG.debug("sql: [{}]", sql);
      try (ResultSet resultSet = jdbc.queryResults(sql)) {
        if (resultSet != null && resultSet.next()) {
          uniqueString = resultSet.getString("UniqueString");
          LOG.info("UniqueString: [{}]", uniqueString);
        }
      }
    } catch (final Exception e) {
      LOG.error("Error in SQL operation", e);
    } finally {
      jdbc.close();
    }
    return uniqueString;
  }

  public static Object parseQuote(Object value) {
    // Java 17: Pattern matching for instanceof
    if (value instanceof String valueNew) {
      if (valueNew.contains("'")) {
        return valueNew.replaceAll("'", "''");
      } else {
        return valueNew;
      }
    } else {
      return value;
    }
  }

  public static void updateQAToolsWebSubmissionLog(String sql) throws Exception {
    // Failed attempt at connecting to the access db.
    // updateSubmissionStatus(fileName, "Running")
    final String batchFileUpdate =
        CJSConstants.PATH_FILES_DATA + "PDF_BATCH_FILE_UPDATE" + IExtension.BAT;
    String command =
        Constants.QUOTE_DOUBLE
            + CJSConstants.PATH_FILES_TOOLS
            + "Update_tblSubmissionLog"
            + IExtension.VBS
            + Constants.QUOTE_DOUBLE
            + " "
            + Constants.QUOTE_DOUBLE
            + sql
            + Constants.QUOTE_DOUBLE;
    FSOTests.fileWrite(batchFileUpdate, command, false);
    command = "CMD /C " + Constants.QUOTE_DOUBLE + batchFileUpdate + Constants.QUOTE_DOUBLE;
    CommandLineTests.runProcess(command);
  }

  public static int updateSubmissionStatus(String fileName, String status) {
    // String currentDateTime = DateHelpersTests.getCurrentDateAndTime()
    final String sql =
        "UPDATE ["
            + TABLE_SUBMISSIONLOG
            + "] SET [Status] = '"
            + status
            + "' "
            + JDBCConstants.WHERE
            + "[FileNameNew]) = '"
            + fileName
            + "', ;";
    final JDBC jdbc = new JDBC("", "QATOOLSWEB");
    int recordsUpdated = 0;
    try {
      recordsUpdated = jdbc.executeUpdate(sql, true);
      jdbc.close();
    } catch (final Exception e) {
      LOG.error("Error in SQL operation", e);
    }
    return recordsUpdated;
  }

  public static boolean validEmailActive(String eMail) throws ClassNotFoundException, SQLException {
    boolean validEmail = false;
    // You should be able to change this from using the " + TABLE_DOM_USERS
    // + " Table to use the QATOOLS_USERS_AUTHORIZED View and not need to
    // include the " "+JDBCConstants.AND+"[" + TABLE_DOM_USERS +
    // "].[Active]='TRUE'".
    final String sql =
        JDBCConstants.SELECT
            + "["
            + TABLE_DOM_USERS
            + "].[EMail] "
            + JDBCConstants.FROM
            + "["
            + TABLE_DOM_USERS
            + "] "
            + JDBCConstants.WHERE
            + "LOWER(["
            + TABLE_DOM_USERS
            + "].[EMail])='"
            + eMail.toLowerCase(Locale.ENGLISH)
            + "' "
            + JDBCConstants.AND
            + "["
            + TABLE_DOM_USERS
            + "].[Active]='TRUE';";
    // String sql =
    // JDBCConstants.SELECT+"[QATOOLS_USERS_AUTHORIZED].[EMail]
    // FROM
    // [QATOOLS_USERS_AUTHORIZED] WHERE
    // LOWER([QATOOLS_USERS_AUTHORIZED].[EMail])='" + eMail.toLowerCase(Locale.ENGLISH) +
    // "';"
    final JDBC jdbc = new JDBC("", DATABASE_DEFINITION);
    try (ResultSet resultSet = jdbc.queryResults(sql)) {
      LOG.debug("Searching EMail [{}]", sql);
      if (resultSet != null && resultSet.next()) {
        validEmail = true;
      }
    } catch (final SQLException e) {
      LOG.error("Error in SQL operation: {}", e.getMessage(), e);
    } finally {
      jdbc.close();
    }
    return validEmail;
  }
}
