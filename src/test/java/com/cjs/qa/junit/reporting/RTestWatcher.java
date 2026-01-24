package com.cjs.qa.junit.reporting;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.junit.runner.Description;

import com.cjs.qa.utilities.GuardedLogger;

/**
 * JUnit 6 TestWatcher extension for test lifecycle callbacks.
 *
 * <p>Note: This class has been migrated from JUnit 4 TestRule to JUnit 6 Extension API. To use this
 * watcher, annotate your test class with: {@code @ExtendWith(RTestWatcher.class)}
 */
public class RTestWatcher implements TestWatcher {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(RTestWatcher.class));
  private static final RTestRun R_TEST_RUN = new RTestRun(null, null);

  /**
   * Creates a Description-like object from ExtensionContext for compatibility with RTest.addStep
   */
  private Description createDescription(ExtensionContext context) {
    String className = context.getTestClass().map(Class::getName).orElse("Unknown");
    String methodName = context.getTestMethod().map((m) -> m.getName()).orElse("Unknown");
    String displayName = context.getDisplayName();
    return Description.createTestDescription(className, methodName + " - " + displayName);
  }

  @Override
  public void testFailed(ExtensionContext context, Throwable cause) {
    R_TEST_RUN.cRTestSet().cRScenario().cRTest().addStep(createDescription(context), "Failed");
    LOG.debug(
        "Test failed: {}|{}|{}",
        context.getTestClass().map(Class::getName).orElse("Unknown"),
        context.getTestMethod().map((m) -> m.getName()).orElse("Unknown"),
        context.getDisplayName());
  }

  @Override
  public void testAborted(ExtensionContext context, Throwable cause) {
    R_TEST_RUN.cRTestSet().cRScenario().cRTest().addStep(createDescription(context), "Skipped");
    LOG.debug(
        "Test skipped: {}|{}|{}",
        context.getTestClass().map(Class::getName).orElse("Unknown"),
        context.getTestMethod().map((m) -> m.getName()).orElse("Unknown"),
        context.getDisplayName());
  }

  @Override
  public void testSuccessful(ExtensionContext context) {
    R_TEST_RUN.cRTestSet().cRScenario().cRTest().addStep(createDescription(context), "Succeeded");
    LOG.debug(
        "Test succeeded: {}|{}|{}",
        context.getTestClass().map(Class::getName).orElse("Unknown"),
        context.getTestMethod().map((m) -> m.getName()).orElse("Unknown"),
        context.getDisplayName());
  }

  @Override
  public void testDisabled(ExtensionContext context, java.util.Optional<String> reason) {
    R_TEST_RUN.cRTestSet().cRScenario().cRTest().addStep(createDescription(context), "Disabled");
    LOG.debug(
        "Test disabled: {}|{}|{}",
        context.getTestClass().map(Class::getName).orElse("Unknown"),
        context.getTestMethod().map((m) -> m.getName()).orElse("Unknown"),
        context.getDisplayName());
  }
}
