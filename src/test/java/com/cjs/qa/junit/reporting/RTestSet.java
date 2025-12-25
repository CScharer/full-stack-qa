package com.cjs.qa.junit.reporting;

import java.util.ArrayList;
import java.util.List;

public class RTestSet extends RScenario {

  private String testSetName;
  private String testSetStatus;
  private List<RScenario> rScenario = new ArrayList<>();

  public RTestSet(String stepName, String stepStatus) {
    super(stepName, stepStatus);
    setTestSetName(testSetName);
    setTestSetStatus(testSetStatus);
  }

  public void addScenario(String scenarioName, String scenarioStatus) {
    getRScenario().add(new RScenario(scenarioName, scenarioStatus));
  }

  public List<RScenario> getRScenario() {
    return rScenario;
  }

  public RScenario cRScenario() {
    return getRScenario().get(getRScenario().size() - 1);
  }

  public String getTestSetName() {
    return testSetName;
  }

  public String getTestSetStatus() {
    return testSetStatus;
  }

  public void setRScenario(List<RScenario> rScenarioList) {
    this.rScenario = rScenarioList;
  }

  public void setTestSetName(String testSetName) {
    this.testSetName = testSetName;
  }

  public void setTestSetStatus(String testSetStatus) {
    this.testSetStatus = testSetStatus;
  }
}
