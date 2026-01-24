package com.cjs.qa.junit.dataset;

import java.io.InputStream;

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

    private static final String PATH_DATA_FILES = "src/test/resources/datasets/";
    private static final String XML_DATA_SET = PATH_DATA_FILES + "XML_DataSet" + IExtension.XML;
    private static final String FLAT_XML_DATA_SET =
        PATH_DATA_FILES + "FlatXML_DataSet" + IExtension.XML;
    private static final String XLS_DATA_SET = PATH_DATA_FILES + "Xls_DataSet" + IExtension.XLS;
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

    // Add data set initialization
    // XML
    try (InputStream inputStreamXML =
        ClassLoader.getSystemResourceAsStream(DataFiles.XML_DATA_SET)) {
      final XmlDataSet xmlDataSet = new XmlDataSet(inputStreamXML);
      dataSet = xmlDataSet;
    }

    // Flat XML
    try (InputStream inputStreamFlatXML =
        ClassLoader.getSystemResourceAsStream(DataFiles.FLAT_XML_DATA_SET)) {
      final FlatXmlDataSetBuilder flatXMLDataSetBuilder = new FlatXmlDataSetBuilder();
      dataSet = flatXMLDataSetBuilder.build(inputStreamFlatXML);
    }

    // XLS
    try (InputStream inputStreamXls =
        ClassLoader.getSystemResourceAsStream(DataFiles.XLS_DATA_SET)) {
      dataSet = new XlsDataSet(inputStreamXls);
    }

    DatabaseOperation.INSERT.execute(getiDatabaseConnection(), dataSet);
  }

  @AfterEach
  void testTeardown(TestInfo testInfo) throws Exception {
    LOG.debug("TearDown-Test Method:[{}]", getTestName(testInfo));
    DatabaseOperation.DELETE.execute(getiDatabaseConnection(), dataSet);
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
    return testInfo.getTestMethod().map((method) -> method.getName()).orElse("Unknown");
  }
}
