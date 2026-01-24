package com.cjs.qa.junit.tests;

import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.mockito.Mockito;
import org.opentest4j.TestAbortedException;

import com.cjs.qa.selenium.Selenium;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.JavaHelpers;

@TestMethodOrder(MethodOrderer.MethodName.class)
@ExtendWith(ScenariosSetupTeardownTests.TestWatcherExtension.class)
public class ScenariosSetupTeardownTests {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(ScenariosSetupTeardownTests.class));

  // private static RTestRun rTestRun = new RTestRun("CJS", "Starting");
  // private static RTestReporter rTestReporter = new RTestReporter();

  // TestWatcher extension for JUnit 6
  static class TestWatcherExtension implements TestWatcher {

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
      // rTestRun.cRTestSet().cRScenario().cRTest().addStep(description,
      // "Failed");
      LOG.error(
          "Test failed: {}|{}|{}",
          context.getTestClass().map(Class::getName).orElse("Unknown"),
          context.getTestMethod().map(Method::getName).orElse("Unknown"),
          context.getDisplayName(),
          cause);
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
      // rTestRun.cRTestSet().cRScenario().cRTest().addStep(description,
      // "Skipped");
      LOG.info(
          "Test skipped: {}|{}|{}",
          context.getTestClass().map(Class::getName).orElse("Unknown"),
          context.getTestMethod().map(Method::getName).orElse("Unknown"),
          context.getDisplayName());
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
      // rTestRun.cRTestSet().cRScenario().cRTest().addStep(description,
      // "Succeeded");
      LOG.info(
          "Test succeeded: {}|{}|{}",
          context.getTestClass().map(Class::getName).orElse("Unknown"),
          context.getTestMethod().map(Method::getName).orElse("Unknown"),
          context.getDisplayName());
    }
  }

  @BeforeAll
  static void classSetup() {
    LOG.info("Setup-Class Method: [{}]", JavaHelpers.getCurrentClassName());
    // rTestRun.addTestSet(JavaHelpers.getClassName(true), "Starting");
    // rTestRun.cRTestSet().addScenario(JavaHelpers.getClassName(true),
    // "Starting");
    // testReporter = new TestReporter();
  }

  @BeforeEach
  void testSetup(TestInfo testInfo) {
    LOG.info("Setup-Test Method: [{}]", getTestName(testInfo));
    // rTestRun.cRTestSet().cRScenario().addTest(getTestName(), "Starting");
    // testReporter.addTest(getTestName());
    // testReporter.reportTest(getTestName());
  }

  @AfterEach
  void testTeardown(TestInfo testInfo) {
    LOG.info("TearDown-Test Method: [{}]", getTestName(testInfo));
    // rTestRun.cRTestSet().cRScenario().setScenarioStatus("Finished");
  }

  @AfterAll
  static void classTearDown() {
    LOG.info("TearDown-Class Method: [{}]", JavaHelpers.getCurrentClassName());
    // rTestRun.cRTest().setTestStatus("Finished");
    // rTestRun.cRTestSet().setTestSetStatus("Finished");
    // rTestRun.setTestRunStatus("Finished");
    // testReporter.reportAll();
  }

  @Test
  void t001(TestInfo testInfo) {
    LOG.info("Running Test: [{}]", getTestName(testInfo));
    final Selenium mockSelenium = Mockito.mock(Selenium.class);
    Mockito.verify(mockSelenium).getWebDriver();
    LOG.debug("mock: [{}]", mockSelenium.getWebDriver().toString());
  }

  @Test
  void t002(TestInfo testInfo) {
    LOG.info("Running Test: [{}]", getTestName(testInfo));
    Assertions.fail();
  }

  @Test
  @Disabled
  void tIngore(TestInfo testInfo) {
    LOG.info("Running Test: [{}]", getTestName(testInfo));
  }

  @Test
  void t003(TestInfo testInfo) {
    LOG.info("Running Test: [{}]", getTestName(testInfo));
    throw new TestAbortedException("t004");
  }

  @Test
  void t004(TestInfo testInfo) {
    LOG.info("Running Test: [{}]", getTestName(testInfo));
    Assertions.fail();
  }

  @Test
  void t00N(TestInfo testInfo) {
    LOG.info("Running Test: [{}]", getTestName(testInfo));
  }

  private String getTestName(TestInfo testInfo) {
    return testInfo.getTestMethod().map(Method::getName).orElse("Unknown");
  }
}
