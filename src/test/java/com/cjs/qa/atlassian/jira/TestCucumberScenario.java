package com.cjs.qa.atlassian.jira;

import com.cjs.qa.utilities.Constants;

public class TestCucumberScenario {

  private Project project;
  private String summary;
  private String description;
  private String issueType = "Test";
  private String testType = "Cucumber";
  private String cucumberTestType = "Scenario";
  private String cucumberScenario = "Given";

  public TestCucumberScenario(
      Project project,
      String summary,
      String description,
      String issueType,
      String testType,
      String cucumberTestType,
      String cucumberScenario) {
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
    if (!cucumberTestType.isEmpty()) {
      this.cucumberTestType = cucumberTestType;
    }
    if (!cucumberScenario.isEmpty()) {
      this.cucumberScenario = cucumberScenario;
    }
  }

  public String getDescription() {
    return description;
  }

  public String getIssueType() {
    return issueType;
  }

  public Project getProject() {
    return project;
  }

  public String getSummary() {
    return summary;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setIssueType(String issueType) {
    this.issueType = issueType;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getTestType() {
    return testType;
  }

  public String getCucumberTestType() {
    return cucumberTestType;
  }

  public String getCucumberScenario() {
    return cucumberScenario;
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
        Constants.QUOTE_DOUBLE
            + "customfield_10201"
            + Constants.QUOTE_DOUBLE
            + ": {"
            + Constants.QUOTE_DOUBLE
            + "value"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + getCucumberTestType()
            + Constants.QUOTE_DOUBLE
            + " },");
    stringBuilder.append(
        Constants.QUOTE_DOUBLE
            + "customfield_10202"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + getCucumberScenario()
            + Constants.QUOTE_DOUBLE);
    stringBuilder.append("}");
    stringBuilder.append("}");
    return stringBuilder.toString();
  }
}
