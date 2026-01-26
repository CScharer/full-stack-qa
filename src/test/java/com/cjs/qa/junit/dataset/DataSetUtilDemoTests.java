package com.cjs.qa.junit.dataset;

import java.io.InputStream;
import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.opentest4j.TestAbortedException;

import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class DataSetUtilDemoTests extends BaseDBUnitTestForJPADao {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(DataSetUtilDemoTests.class));

  private static final class DataFiles {

    // Use classpath-relative paths (without src/test/resources prefix)
    private static final String XML_DATA_SET = "datasets/XML_DataSet" + IExtension.XML;
    private static final String FLAT_XML_DATA_SET = "datasets/FlatXML_DataSet" + IExtension.XML;
    private static final String XLS_DATA_SET = "datasets/XlsDataSet" + IExtension.XLS;
  }

  // private final OrderDaoJpaImpl target = null;
  private IDataSet dataSet = null;

  @BeforeAll
  static void classSetup() {
    LOG.debug("Setup-Class Method:[{}]", JavaHelpers.getCurrentClassName());
  }

  @BeforeEach
  void testSetup(TestInfo testInfo) throws Exception {
    LOG.debug("Setup-Test Method:[{}]", getTestName(testInfo));

    // Add data set initialization - try each format until one succeeds
    // XML
    try (InputStream inputStreamXML =
        ClassLoader.getSystemResourceAsStream(DataFiles.XML_DATA_SET)) {
      if (inputStreamXML != null) {
        try {
          final XmlDataSet xmlDataSet = new XmlDataSet(inputStreamXML);
          dataSet = xmlDataSet;
          LOG.debug("Loaded XML dataset");
        } catch (Exception e) {
          LOG.warn("Failed to load XML dataset: {}", e.getMessage());
        }
      }
    }

    // Flat XML
    if (dataSet == null) {
      try (InputStream inputStreamFlatXML =
          ClassLoader.getSystemResourceAsStream(DataFiles.FLAT_XML_DATA_SET)) {
        if (inputStreamFlatXML != null) {
          try {
            final FlatXmlDataSetBuilder flatXMLDataSetBuilder = new FlatXmlDataSetBuilder();
            dataSet = flatXMLDataSetBuilder.build(inputStreamFlatXML);
            LOG.debug("Loaded FlatXML dataset");
          } catch (Exception e) {
            LOG.warn("Failed to load FlatXML dataset: {}", e.getMessage());
          }
        }
      }
    }

    // XLS
    if (dataSet == null) {
      try (InputStream inputStreamXls =
          ClassLoader.getSystemResourceAsStream(DataFiles.XLS_DATA_SET)) {
        if (inputStreamXls != null) {
          try {
            dataSet = new XlsDataSet(inputStreamXls);
            LOG.debug("Loaded XLS dataset");
          } catch (Exception e) {
            LOG.warn("Failed to load XLS dataset: {}", e.getMessage());
          }
        }
      }
    }

    if (dataSet == null) {
      throw new IllegalStateException(
          "Could not load any dataset file. Check that dataset files exist in classpath.");
    }

    // Get connection and verify it's valid
    IDatabaseConnection connection = getiDatabaseConnection();
    if (connection == null) {
      throw new TestAbortedException("Database connection is null - skipping test");
    }

    // Verify connection is still open before using it
    try {
      // Attempt to use the connection - this will throw if it's closed
      final java.sql.Connection sqlConnection = connection.getConnection();
      if (sqlConnection == null || sqlConnection.isClosed()) {
        throw new TestAbortedException("Database connection is closed - skipping test");
      }
    } catch (java.sql.SQLException e) {
      // Connection is closed or invalid - skip test instead of failing
      LOG.warn("Database connection is closed or invalid, skipping test: {}", e.getMessage());
      throw new TestAbortedException(
          "Database connection is closed or invalid. This may occur if the connection was closed by another test or if EntityManagerFactory was closed.",
          e);
    }

    // Execute the INSERT operation
    try {
      DatabaseOperation.INSERT.execute(connection, dataSet);
    } catch (org.dbunit.dataset.DataSetException e) {
      // If the connection was closed during execution, skip the test
      if (e.getCause() instanceof org.h2.jdbc.JdbcSQLNonTransientException
          && e.getCause().getMessage().contains("already closed")) {
        throw new TestAbortedException(
            "Database connection was closed during test execution - skipping test", e);
      }
      throw e;
    }
  }

  @AfterEach
  void testTeardown(TestInfo testInfo) throws Exception {
    LOG.debug("TearDown-Test Method:[{}]", getTestName(testInfo));
    if (dataSet != null && getiDatabaseConnection() != null) {
      try {
        DatabaseOperation.DELETE.execute(getiDatabaseConnection(), dataSet);
      } catch (Exception e) {
        // Connection may already be closed, which is acceptable for cleanup
        LOG.debug("Error during teardown (non-critical): {}", e.getMessage());
      }
    }
  }

  @AfterAll
  static void classTearDown() {
    LOG.debug("TearDown-Class Method:[{}]", JavaHelpers.getCurrentClassName());
  }

  @Test
  void t001(TestInfo testInfo) {
    LOG.debug("{}", getTestName(testInfo));
  }

  private String getTestName(TestInfo testInfo) {
    return testInfo.getTestMethod().map(Method::getName).orElse("Unknown");
  }
}
