package com.cjs.qa.jdbc;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.utilities.CJSConstants;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.DateHelpersTests;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;

public final class SQLite {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(SQLite.class));

  private SQLite() {
    // Utility class - prevent instantiation
  }

  public static void main(String[] args) throws Throwable {
    try {
      createProductSummaryTableAndViews("tblProductSummaryControl", "tblProductSummaryTest");
    } catch (final Exception e) {
      LOG.error("Error in SQLite main", e);
    }
  }

  public static void createProductSummaryTableAndViews(
      String tableNameControl, String tableNameTest) throws Throwable {
    final List<String> fields =
        Arrays.asList(
            "Product", "UnitState", "SubUnit", "Total", "Coverage", "SubCoverage", "Premium");
    final String dateTimeStamp = DateHelpersTests.getCurrentDateAndTime();
    LOG.debug("dateTimeStamp: [{}]", dateTimeStamp);
    String fileName =
        DateHelpersTests.convertDate(
            dateTimeStamp, "MM/dd/yyyy HH:mm:ss.SSS", "yyyyMMdd_HHmmss.SSS");
    fileName = "ProductSummary";
    LOG.debug("fileName: [{}]", fileName);
    final String pathData = CJSConstants.PATH_FILES_DATA;
    final String pathFileName = pathData + fileName + IExtension.SQLITE; // ".db";
    FSOTests.fileDelete(pathFileName);
    try (Connection connection = SQL.createDatabaseSQLite(pathFileName)) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(JDBCConstants.CREATE_TABLE + "'<TABLE_NAME>' (");
      for (String field : fields) {
        stringBuilder.append("'" + field + "' TEXT NOT NULL");
        if (!"Premium".equals(field)) {
          stringBuilder.append(",");
        }
      }
      stringBuilder.append(");");
      SQL.createTableView(
          connection,
          stringBuilder.toString().replace("<TABLE_NAME>", tableNameControl),
          tableNameControl);
      SQL.createTableView(
          connection,
          stringBuilder.toString().replace("<TABLE_NAME>", tableNameTest),
          tableNameTest);
      final String viewNameAdded = "V_ADDED";
      final String viewNameChanged = "V_CHANGED";
      final String viewNameDeleted = "V_DELETE";
      stringBuilder = new StringBuilder();
      stringBuilder.append(
          JDBCConstants.CREATE_VIEW
              + Constants.QUOTE_DOUBLE
              + "<VIEW_NAME>"
              + Constants.QUOTE_DOUBLE
              + " "
              + JDBCConstants.AS
              + JDBCConstants.SELECT
              + "tl.* ");
      stringBuilder.append(JDBCConstants.FROM + "[<TABLE_LEFT>] tl ");
      stringBuilder.append(JDBCConstants.LEFT_JOIN + "[<TABLE_RIGHT>] tr ");
      for (int index = 0; index < fields.size(); index++) {
        final String field = fields.get(index);
        if (!"Premium".equals(field)) {
          if (index == 0) {
            stringBuilder.append(JDBCConstants.ON + "tl.[" + field + "] = tr.[" + field + "]");
          } else {
            stringBuilder.append(JDBCConstants.AND + "tl.[" + field + "] = tr.[" + field + "]");
          }
        }
      }
      stringBuilder.append(" " + JDBCConstants.WHERE + "tr.[Premium] <PREMIUM_VALUE>;");
      String sql =
          stringBuilder
              .toString()
              .replace("<VIEW_NAME>", viewNameDeleted)
              .replace("<TABLE_LEFT>", tableNameControl)
              .replace("<TABLE_RIGHT>", tableNameTest)
              .replace("<PREMIUM_VALUE>", "=''");
      SQL.createTableView(connection, sql, viewNameDeleted);
      sql =
          stringBuilder
              .toString()
              .replace("<VIEW_NAME>", viewNameAdded)
              .replace("<TABLE_LEFT>", tableNameTest)
              .replace("<TABLE_RIGHT>", tableNameControl)
              .replace("<PREMIUM_VALUE>", "=''");
      SQL.createTableView(connection, sql, viewNameAdded);
      sql =
          stringBuilder
              .toString()
              .replace("<VIEW_NAME>", viewNameChanged)
              .replace("<TABLE_LEFT>", tableNameControl)
              .replace("<TABLE_RIGHT>", tableNameTest)
              .replace("<PREMIUM_VALUE>", "!=tl.[Premium]");
      SQL.createTableView(connection, sql, viewNameChanged);
    }
  }
}
