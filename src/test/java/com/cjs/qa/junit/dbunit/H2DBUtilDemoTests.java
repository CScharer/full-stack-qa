package com.cjs.qa.junit.dbunit;

import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.operation.DatabaseOperation;
import org.joda.time.DateTime;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;

import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.JavaHelpers;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class H2DBUtilDemoTests extends BaseDBUnitTestForJPADao {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(H2DBUtilDemoTests.class));

  // private final OrderDaoJpaImpl target = null;
  private DefaultDataSet dataSet = null;

  @BeforeAll
  static void classSetup() {
    LOG.debug("Setup-Class Method:[{}]", JavaHelpers.getCurrentClassName());
  }

  @BeforeEach
  void testSetup(TestInfo testInfo) throws Exception {
    LOG.debug("Setup-Test Method:[{}]", getTestName(testInfo));
    dataSet = new DefaultDataSet();
    final DefaultTable orderSourceEntityTable =
        new DefaultTable("OrderSourceEntity", DBDataDef.ORDER_SOURCE_ENTITY_COLUMNS);
    final Object[][] orderSourceRows = createOrderSourceRows();
    for (final Object[] currentOrderSourceRow : orderSourceRows) {
      orderSourceEntityTable.addRow(currentOrderSourceRow);
    }
    dataSet.addTable(orderSourceEntityTable);

    final DefaultTable orderEntityTable =
        new DefaultTable("OrderEntity", DBDataDef.ORDER_ENTITY_COLUMNS);
    final Object[][] orderRows = createOrderRowData();
    for (final Object[] currentOrderRow : orderRows) {
      orderEntityTable.addRow(currentOrderRow);
    }
    dataSet.addTable(orderEntityTable);
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

  private Object[][] createOrderSourceRows() {
    return new Object[][] {
      new Object[] {
        1,
        "so",
        "Store Ordrer",
        "cbrown",
        new DateTime().withYear(2012).withMonthOfYear(12).withDayOfMonth(31).toDate(),
      },
      new Object[] {
        2,
        "wo",
        "Web Ordrer",
        "lvanpelt",
        new DateTime().withYear(2012).withMonthOfYear(12).withDayOfMonth(31).toDate(),
      },
      new Object[] {
        3,
        "un",
        null,
        "lvanpelt",
        new DateTime().withYear(2013).withMonthOfYear(1).withDayOfMonth(1).toDate(),
      },
    };
  }

  private Object[][] createOrderRowData() {
    return new Object[][] {
      // Refernce the web order.
      new Object[] {
        1,
        "Customer 1 Order 1",
        "ORD1",
        1,
        new DateTime().withYear(2013).withMonthOfYear(12).withDayOfMonth(23).toDate(),
        250000,
        null,
        1,
        2,
      },
      // Refernce the store order.
      new Object[] {
        2,
        "Customer 1 Order 2",
        "ORD2",
        1,
        new DateTime().withYear(2013).withMonthOfYear(12).withDayOfMonth(23).toDate(),
        250000,
        new DateTime().withYear(2013).withMonthOfYear(12).withDayOfMonth(26).toDate(),
        1,
        1,
      },
    };
  }

  @Test
  void t001(TestInfo testInfo) {
    LOG.debug("{}", getTestName(testInfo));
  }

  private String getTestName(TestInfo testInfo) {
    return testInfo.getTestMethod().map(Method::getName).orElse("Unknown");
  }
}
