package com.cjs.qa.atlassian.jira;

public enum EPriority {
  TRIVIAL("Trivial"),
  MINOR("Minor"),
  HIGH("High"),
  MAJOR("Major"),
  CRITICAL("Critical"),
  BLOCKER("Blocker");

  private String value;

  EPriority(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
