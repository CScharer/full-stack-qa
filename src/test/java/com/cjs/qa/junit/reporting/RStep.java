package com.cjs.qa.junit.reporting;

public class RStep {

  private String stepName;
  private String stepStatus;

  public RStep(String stepName, String stepStatus) {
    setStepName(stepName);
    setStepStatus(stepStatus);
  }

  public String getStepName() {
    return stepName;
  }

  public void setStepName(String stepName) {
    this.stepName = stepName;
  }

  public String getStepStatus() {
    return stepStatus;
  }

  public void setStepStatus(String stepStatus) {
    this.stepStatus = stepStatus;
  }
}
