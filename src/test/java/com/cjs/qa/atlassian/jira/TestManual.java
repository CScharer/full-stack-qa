package com.cjs.qa.atlassian.jira;

import java.util.List;

import com.cjs.qa.utilities.Constants;

public class TestManual {

  private Project project;
  private String summary;
  private String description;
  private String issueType;
  private String testType = "Manual";
  private List<ManualTestSteps> manualTestSteps;

  public TestManual(
      Project project,
      String summary,
      String description,
      String issueType,
      String testType,
      List<ManualTestSteps> manualTestSteps) {
    if (project != null) {
      this.project = project;
    }
    if (!summary.isEmpty()) {
      this.summary = summary;
    }
    if (!description.isEmpty()) {
      this.description = description;
    }
    if (!issueType.isEmpty()) {
      this.issueType = issueType;
    }
    if (!testType.isEmpty()) {
      this.testType = testType;
    }
    if (manualTestSteps != null) {
      this.manualTestSteps = manualTestSteps;
    }
  }

  public String getDescription() {
    return description;
  }

  public List<ManualTestSteps> getManualTestSteps() {
    return manualTestSteps;
  }

  public Project getProject() {
    return project;
  }

  public String getSummary() {
    return summary;
  }

  public String getTestType() {
    return testType;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setManualTestSteps(List<ManualTestSteps> manualTestSteps) {
    this.manualTestSteps = manualTestSteps;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public void setTestType(String testType) {
    this.testType = testType;
  }

  public String getIssueType() {
    return issueType;
  }

  public void setIssueType(String issueType) {
    this.issueType = issueType;
  }

  @Override
  public String toString() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("{");
    stringBuilder.append(Constants.QUOTE_DOUBLE + "fields" + Constants.QUOTE_DOUBLE + ": {");
    stringBuilder.append(getProject().toString() + ",");
    stringBuilder.append(
        Constants.QUOTE_DOUBLE
            + "summary"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + getSummary()
            + Constants.QUOTE_DOUBLE
            + ",");
    stringBuilder.append(
        Constants.QUOTE_DOUBLE
            + "description"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + ""
            + getDescription()
            + Constants.QUOTE_DOUBLE
            + ",");
    stringBuilder.append(
        Constants.QUOTE_DOUBLE
            + "issuetype"
            + Constants.QUOTE_DOUBLE
            + ": {"
            + Constants.QUOTE_DOUBLE
            + "name"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + getIssueType()
            + Constants.QUOTE_DOUBLE
            + " },");
    stringBuilder.append(
        Constants.QUOTE_DOUBLE
            + "customfield_10200"
            + Constants.QUOTE_DOUBLE
            + ": {"
            + Constants.QUOTE_DOUBLE
            + "value"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + getTestType()
            + Constants.QUOTE_DOUBLE
            + " },");
    stringBuilder.append(
        Constants.QUOTE_DOUBLE + "customfield_10004" + Constants.QUOTE_DOUBLE + ": {");
    stringBuilder.append(Constants.QUOTE_DOUBLE + "steps" + Constants.QUOTE_DOUBLE + ": [");
    for (int manualTestStepIndex = 0;
        manualTestStepIndex < getManualTestSteps().size();
        manualTestStepIndex++) {
      final ManualTestSteps manualTestSteps = getManualTestSteps().get(manualTestStepIndex);
      stringBuilder.append(manualTestSteps.toString());
      if (manualTestStepIndex < (getManualTestSteps().size() - 1)) {
        stringBuilder.append(",");
      }
    }
    stringBuilder.append("]");
    stringBuilder.append("}");
    stringBuilder.append("}");
    stringBuilder.append("}");
    stringBuilder.append("}");
    return stringBuilder.toString();
  }
}
