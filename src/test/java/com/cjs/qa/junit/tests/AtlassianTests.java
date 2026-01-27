package com.cjs.qa.junit.tests;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Test;

import com.cjs.qa.atlassian.jira.ManualTestSteps;
import com.cjs.qa.atlassian.jira.Project;
import com.cjs.qa.atlassian.jira.TestCucumberScenario;
import com.cjs.qa.atlassian.jira.TestGeneric;
import com.cjs.qa.atlassian.jira.TestManual;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.JSON;

public class AtlassianTests {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(AtlassianTests.class));

  @Test
  public void addTestCucumberScenario() {
    final Project project = new Project("QSDT");
    final TestCucumberScenario testCucumberScenario =
        new TestCucumberScenario(
            project,
            "Summary:TestCucumberScenario",
            "Description:TestCucumberScenario",
            "Test",
            "Cucumber",
            "Scenario",
            "Given I Do Something\\nThen I Did Something");
    LOG.info("testCucumberScenario: {}", testCucumberScenario.toString());
    LOG.info(
        "testCucumberScenario formatted: {}",
        JSON.formatPretty(testCucumberScenario.toString(), 4));
  }

  @Test
  public void addTestGeneric() {
    final Project project = new Project("QSDT");
    final TestGeneric testGeneric =
        new TestGeneric(
            project,
            "Summary:TestGeneric",
            "Description:TestGeneric",
            "Test",
            "Generic",
            "TestGeneric.sh");
    LOG.info("testGeneric: {}", JSON.formatPretty(testGeneric.toString(), 4));
  }

  @Test
  public void addTestManual() {
    final Project project = new Project("QSDT");
    final List<ManualTestSteps> manualTestStepsList = new ArrayList<>();
    for (int indexStep = 1; indexStep <= 10; indexStep++) {
      final ManualTestSteps manualTestSteps =
          new ManualTestSteps(
              indexStep, "Step " + indexStep, "Data " + indexStep, "Result " + indexStep);
      manualTestStepsList.add(manualTestSteps);
    }
    final TestManual testManual =
        new TestManual(
            project,
            "Summary:TestManual",
            "Description:TestManual",
            "Test",
            "Manual",
            manualTestStepsList);
    LOG.info("testManual: {}", JSON.formatPretty(testManual.toString(), 4));
  }
}
