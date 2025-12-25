package com.cjs.qa.junit.reporting;

import java.util.ArrayList;
import java.util.List;

public class RTestRun extends RTestSet {

  private String testRunName;
  private String testRunStatus;
  private List<RTestSet> rTestSet = new ArrayList<>();

  public RTestRun(String stepName, String stepStatus) {
    super(stepName, stepStatus);
    setTestRunName(testRunName);
    setTestRunStatus(testRunStatus);
  }

  public void addTestSet(String testSetName, String testSetStatus) {
    getRTestSet().add(new RTestSet(testSetName, testSetStatus));
  }

  public RTestSet cRTestSet() {
    return getRTestSet().get(getRTestSet().size() - 1);
  }

  public List<RTestSet> getRTestSet() {
    return rTestSet;
  }

  public String getTestRunName() {
    return testRunName;
  }

  public String getTestRunStatus() {
    return testRunStatus;
  }

  public void setRTestSet(List<RTestSet> rTestSetList) {
    this.rTestSet = rTestSetList;
  }

  public void setTestRunName(String testRunName) {
    this.testRunName = testRunName;
  }

  public void setTestRunStatus(String testRunStatus) {
    this.testRunStatus = testRunStatus;
  }
}
