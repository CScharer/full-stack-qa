package com.cjs.qa.atlassian.jira;

public enum EIssueType {
  DEFECT("Defect"),
  NEW_FEATURE("New Feature"),
  TASK("Task"),
  DESIGN("Design"),
  ENHANCEMENT("Enhancement"),
  EPIC("Epic"),
  STORY("Story"),
  REGULATORY("Regulatory"),
  CONFIGURATION("Configuration"),
  REPORTING("Reporting"),
  TEST("Test"),
  TEST_SET("Test Set"),
  TEST_EXECUTION("Test Execution"),
  TEST_PLAN("Test Plan"),
  TEST_PRE_CONDITION("Pre-Condition"),
  PRINT("Print"),
  TEST_RELEASE("Test Release"),
  SUB_TASK("Sub-task"),
  PRINT_SUB_TASK("Print Sub-task");

  private String value;

  EIssueType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
