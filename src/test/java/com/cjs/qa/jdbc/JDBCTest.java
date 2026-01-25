package com.cjs.qa.jdbc;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.cjs.qa.vivit.VivitDataTests;

@Disabled("Windows-specific test - not compatible with Mac or Test Needs Updates")
public class JDBCTest {

  @Test
  public void getDatabaseTablesViewsFields() throws Throwable {
    JDBC.exportTableViewSchemaSQLite("TablesViews", VivitDataTests.DATABASE_DEFINITION, true);
  }
}
