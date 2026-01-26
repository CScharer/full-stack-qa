package com.cjs.qa.junit.dataset;

import java.io.InputStream;
import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
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

    DatabaseOperation.INSERT.execute(getiDatabaseConnection(), dataSet);
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
