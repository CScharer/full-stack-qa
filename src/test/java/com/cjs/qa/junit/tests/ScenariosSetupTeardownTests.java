package com.cjs.qa.junit.tests;

import org.apache.logging.log4j.LogManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.AssumptionViolatedException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;

import com.cjs.qa.selenium.Selenium;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.JavaHelpers;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ScenariosSetupTeardownTests {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(ScenariosSetupTeardownTests.class));

  // private static RTestRun rTestRun = new RTestRun("CJS", "Starting");
  // private static RTestReporter rTestReporter = new RTestReporter();
  @Rule public TestName testName = new TestName();

  @Rule
  public TestWatcher testWatcher =
      new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
          // rTestRun.cRTestSet().cRScenario().cRTest().addStep(description,
          // "Failed");
          LOG.error(
              "Test failed: {}|{}|{}",
              description.getClassName(),
              description.getMethodName(),
              description.getDisplayName(),
              e);
        }

        @Override
        protected void finished(Description description) {
          // rTestRun.cRTestSet().cRScenario().cRTest().addStep(description,
          // "Finished");
          LOG.debug(
              "Test finished: {}|{}|{}",
              description.getClassName(),
              description.getMethodName(),
              description.getDisplayName());
        }

        @Override
        protected void skipped(AssumptionViolatedException e, Description description) {
          // rTestRun.cRTestSet().cRScenario().cRTest().addStep(description,
          // "Skipped");
          LOG.info(
              "Test skipped: {}|{}|{}",
              description.getClassName(),
              description.getMethodName(),
              description.getDisplayName());
        }

        @Override
        protected void starting(Description description) {
          // rTestRun.cRTestSet().cRScenario().cRTest().addStep(description,
          // "Starting");
          LOG.info(
              "Test starting: {}|{}|{}",
              description.getClassName(),
              description.getMethodName(),
              description.getDisplayName());
        }

        @Override
        protected void succeeded(Description description) {
          // rTestRun.cRTestSet().cRScenario().cRTest().addStep(description,
          // "Succeeded");
          LOG.info(
              "Test succeeded: {}|{}|{}",
              description.getClassName(),
              description.getMethodName(),
              description.getDisplayName());
        }
      };

  @BeforeClass
  public static void classSetup() {
    LOG.info("Setup-Class Method: [{}]", JavaHelpers.getCurrentClassName());
    // rTestRun.addTestSet(JavaHelpers.getClassName(true), "Starting");
    // rTestRun.cRTestSet().addScenario(JavaHelpers.getClassName(true),
    // "Starting");
    // testReporter = new TestReporter();
  }

  @Before
  public void testSetup() {
    LOG.info("Setup-Test Method: [{}]", getTestName());
    // rTestRun.cRTestSet().cRScenario().addTest(getTestName(), "Starting");
    // testReporter.addTest(getTestName());
    // testReporter.reportTest(getTestName());
  }

  @After
  public void testTeardown() {
    LOG.info("TearDown-Test Method: [{}]", getTestName());
    // rTestRun.cRTestSet().cRScenario().setScenarioStatus("Finished");
  }

  @AfterClass
  public static void classTearDown() {
    LOG.info("TearDown-Class Method: [{}]", JavaHelpers.getCurrentClassName());
    // rTestRun.cRTest().setTestStatus("Finished");
    // rTestRun.cRTestSet().setTestSetStatus("Finished");
    // rTestRun.setTestRunStatus("Finished");
    // testReporter.reportAll();
  }

  @Test
  public void t001() {
    LOG.info("Running Test: [{}]", getTestName());
    final Selenium mockSelenium = Mockito.mock(Selenium.class);
    Mockito.verify(mockSelenium).getWebDriver();
    LOG.debug("mock: [{}]", mockSelenium.getWebDriver().toString());
  }

  @Test
  public void t002() {
    LOG.info("Running Test: [{}]", getTestName());
    Assert.fail();
  }

  @Test
  @Ignore
  public void tIngore() {
    LOG.info("Running Test: [{}]", getTestName());
  }

  @Test
  public void t003() {
    LOG.info("Running Test: [{}]", getTestName());
    throw new AssumptionViolatedException("t004");
  }

  @Test
  public void t004() {
    LOG.info("Running Test: [{}]", getTestName());
    Assert.fail();
  }

  @Test
  public void t00N() {
    LOG.info("Running Test: [{}]", getTestName());
  }

  public String getTestName() {
    return testName.getMethodName();
  }
}
