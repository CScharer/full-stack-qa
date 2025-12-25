package com.cjs.qa.junit.tests;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.junit.Test;

import com.cjs.qa.jdbc.EDBDriver;
import com.cjs.qa.utilities.GuardedLogger;

public class EDBDriverTests {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(EDBDriverTests.class));
  private final List<String> databaseTypeList =
      Arrays.asList("DB2", "MICROSOFT", "QUICKBOOKS", "SQLITE", "SQLSERVER", "UCANACCESS");

  private List<String> getDatabaseTypeList() {
    return databaseTypeList;
  }

  @Test
  public void edbDrivers() {
    for (final String databaseType : getDatabaseTypeList()) {
      final EDBDriver eDBDriver = EDBDriver.fromString(databaseType);
      LOG.info(
          "Driver Type: [{}], JDBC Driver: [{}], URL Prefix: [{}]",
          databaseType,
          eDBDriver.getJdbcDriver(),
          eDBDriver.getUrlPrefix());
    }
  }
}
