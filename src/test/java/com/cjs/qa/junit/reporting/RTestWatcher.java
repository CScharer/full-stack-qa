package com.cjs.qa.junit.reporting;

import org.apache.logging.log4j.LogManager;
import org.junit.AssumptionViolatedException;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.cjs.qa.utilities.GuardedLogger;

public class RTestWatcher implements TestRule {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(RTestWatcher.class));
  private static final RTestRun R_TEST_RUN = new RTestRun(null, null);

  @Rule
  public TestWatcher testWatcher =
      new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
          R_TEST_RUN.cRTestSet().cRScenario().cRTest().addStep(description, "Failed");
          LOG.debug("{}", description.toString());
        }

        @Override
        protected void finished(Description description) {
          R_TEST_RUN.cRTestSet().cRScenario().cRTest().addStep(description, "Finished");
          LOG.debug("{}", description.toString());
        }

        @Override
        protected void skipped(AssumptionViolatedException e, Description description) {
          R_TEST_RUN.cRTestSet().cRScenario().cRTest().addStep(description, "Skipped");
          LOG.debug("{}", description.toString());
        }

        @Override
        protected void starting(Description description) {
          R_TEST_RUN.cRTestSet().cRScenario().cRTest().addStep(description, "Starting");
          LOG.debug("{}", description.toString());
        }

        @Override
        protected void succeeded(Description description) {
          R_TEST_RUN.cRTestSet().cRScenario().cRTest().addStep(description, "Succeeded");
          LOG.debug("{}", description.toString());
        }
      };

  @Override
  public Statement apply(Statement base, Description description) {
    return base;
  }
}
