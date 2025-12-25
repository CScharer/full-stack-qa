package com.cjs.qa.jdbc;

import java.util.Locale;

public enum EDBDriver {
  DB2("com.ibm.db2.jcc.DB2Driver", "jdbc:db2://"),
  MICROSOFT("sun.jdbc.odbc.JdbcOdbcDriver", "jdbc:odbc:"),
  QUICKBOOKS("sun.jdbc.odbc.JdbcOdbcDriver", "jdbc:odbc:QuickBooks Data"),
  SQLITE("org.sqlite.JDBC", "jdbc:sqlite:"),
  SQLSERVER("com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://"),
  UCANACCESS("net.ucanaccess.jdbc.UcanaccessDriver", "jdbc:ucanaccess://");

  private String jdbcDriver;
  private String urlPrefix;

  EDBDriver(String jdbcDriver, String urlPrefix) {
    this.jdbcDriver = jdbcDriver;
    this.urlPrefix = urlPrefix;
  }

  public static EDBDriver fromString(String name) {
    return getEnumFromString(EDBDriver.class, name);
  }

  /**
   * A common method for all enums since they can't have another base class
   *
   * @param <T> Enum type
   * @param c enum type. All enums must be all caps.
   * @param string case insensitive
   * @return corresponding enum, or null
   */
  public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string) {
    return Enum.valueOf(c, string.trim().toUpperCase(Locale.ENGLISH));
  }

  public String getJdbcDriver() {
    return jdbcDriver;
  }

  public String getUrlPrefix() {
    return urlPrefix;
  }
}
