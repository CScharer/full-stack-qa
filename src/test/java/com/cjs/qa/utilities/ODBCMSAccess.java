package com.cjs.qa.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.jdbc.JDBCConstants;

public final class ODBCMSAccess {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(ODBCMSAccess.class));

  private ODBCMSAccess() {
    // Utility class - prevent instantiation
  }

  public static void main(String[] args) {
    final List<String> listFields =
        Arrays.asList(
            "SubmissionID",
            "Company",
            "Environment",
            "Status",
            "SubmitType",
            "Submitter",
            "SubmissionTime",
            "StartTime",
            "CompletionTime",
            "FileName",
            "ResultsFile",
            "ResultsSent",
            "EmailAddress",
            "FileNameNew",
            "MachineName",
            "SubmitCompany");
    // Map<String, String> map = new HashMap<>();
    try (Connection connection =
        connectDb(
            "C:"
                + Constants.DELIMETER_PATH
                + "Temp"
                + Constants.DELIMETER_PATH
                + "qatoolsweb"
                + IExtension.MDB)) {
      if (connection != null) {
        final ODBCSQLite odbcSQLite = new ODBCSQLite();
        try (Connection connectionSQLite =
            ODBCSQLite.connectDb(
                "C:"
                    + Constants.DELIMETER_PATH
                    + "Automation"
                    + Constants.DELIMETER_PATH
                    + "BTS.sqlite")) {
          String sql = JDBCConstants.DELETE_FROM + "[Core];";
          odbcSQLite.executeUpdate(connectionSQLite, sql);
          int recordCount = 0;
          try (Statement statement = connection.createStatement();
              ResultSet resultSet =
                  statement.executeQuery(
                      JDBCConstants.SELECT_ALL_FROM
                          + "[tblSubmissionLog] "
                          + JDBCConstants.ORDER_BY
                          + "[SubmissionID]")) {
            while (resultSet.next()) {
              // LOG.info(oResultSet.getString(1));
              final StringBuilder sqlPre = new StringBuilder();
              sqlPre.append(JDBCConstants.INSERT_INTO + "[Core] (");
              final StringBuilder sqlValues = new StringBuilder();
              for (int fieldIndex = 0; fieldIndex < listFields.size(); fieldIndex++) {
                final String field = listFields.get(fieldIndex);
                final String value = resultSet.getString(field);
                // map.put("SubmissionID",
                // resultSet.getString("SubmissionID"));
                // map.put(field, value);
                if (value != null) {
                  if (fieldIndex == listFields.size() - 1) {
                    sqlPre.append("[" + field + "]) VALUES (");
                    sqlValues.append("'" + value + "');");
                  } else {
                    sqlPre.append("[" + field + "], ");
                    sqlValues.append("'" + value + "', ");
                  }
                }
              }
              sql = sqlPre.toString() + sqlValues.toString();
              recordCount += odbcSQLite.executeUpdate(connectionSQLite, sql);
              if (recordCount % 100 == 0) {
                LOG.info(recordCount + " records");
              }
              // LOG.info(map.toString());
            }
            LOG.info(recordCount + " records");
          }
        }
      }
    } catch (final Exception oException) {
      LOG.info(oException.getMessage());
      oException.printStackTrace();
    }
  }

  public static Connection connectDb(String database) {
    LOG.info("Connecting to [" + database + "]");
    try {
      // String dir = System.getProperty("user.dir");
      Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
      return DriverManager.getConnection("jdbc:ucanaccess://" + database);
    } catch (ClassNotFoundException | SQLException oException) {
      JOptionPane.showMessageDialog(null, "Problem connecting to database [" + database + "]");
      LOG.info(oException.getMessage());
      oException.printStackTrace();
      return null;
    }
  }
}
