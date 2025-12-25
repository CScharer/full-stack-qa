package com.cjs.qa.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.jdbc.JDBCConstants;

public class OdbcMsAccess64 {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(OdbcMsAccess64.class));

  public void main(String[] args) {
    try (Connection oConnection =
        connectDb(
            "C:"
                + Constants.DELIMETER_PATH
                + "Temp"
                + Constants.DELIMETER_PATH
                + "qatoolsweb"
                + IExtension.MDB)) {
      if (oConnection != null) {
        try (Statement statement = oConnection.createStatement();
            ResultSet resultSet =
                statement.executeQuery(JDBCConstants.SELECT_ALL_FROM + "[tblSubmissionLog]")) {
          while (resultSet.next()) {
            // LOG.debug("Result: {}", resultSet.getString(1));
            LOG.info("SubmissionID: {}", resultSet.getString("SubmissionID"));
          }
        }
      }
    } catch (final Exception oException) {
      LOG.error("Error in OdbcMsAccess64 main", oException);
      oException.printStackTrace();
    }
  }

  public Connection connectDb(String database) {
    LOG.info("Connecting to [{}]", database);
    try {
      // String dir = System.getProperty("user.dir");
      Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
      return DriverManager.getConnection(
          "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb, *.accdb)};Dbq=" + database, "", "");
    } catch (ClassNotFoundException | SQLException oException) {
      JOptionPane.showMessageDialog(null, "Problem connecting to database [" + database + "]");
      LOG.error("Failed to connect to database: {}", database, oException);
      oException.printStackTrace();
      return null;
    }
  }
}
