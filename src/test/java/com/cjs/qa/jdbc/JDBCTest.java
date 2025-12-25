package com.cjs.qa.jdbc;

import org.junit.Test;

import com.cjs.qa.vivit.VivitDataTests;

public class JDBCTest {

  @Test
  public void getDatabaseTablesViewsFields() throws Throwable {
    JDBC.exportTableViewSchemaSQLite("TablesViews", VivitDataTests.DATABASE_DEFINITION, true);
  }
}
