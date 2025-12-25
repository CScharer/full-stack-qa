package com.cjs.qa.atlassian.jira;

import com.cjs.qa.utilities.Constants;

public class Project {

  private String project = "QSDT";

  public Project(String project) {
    setProject(project);
  }

  public String getProject() {
    return project;
  }

  public void setProject(String project) {
    this.project = project;
  }

  @Override
  public String toString() {
    final StringBuilder stringBuilder = new StringBuilder();
    final Constants c = new Constants();
    int newLine = 0;
    if (c.isFormatPretty()) {
      newLine = 1;
    }
    int tab = 0;
    tab = Constants.tabIncriment(tab, newLine * 2);
    stringBuilder.append(
        Constants.nlTab(newLine, tab)
            + Constants.QUOTE_DOUBLE
            + "project"
            + Constants.QUOTE_DOUBLE
            + ":");
    stringBuilder.append(Constants.nlTab(newLine, tab) + "{");
    tab = Constants.tabIncriment(tab, newLine * 1);
    stringBuilder.append(
        Constants.nlTab(newLine, tab)
            + Constants.QUOTE_DOUBLE
            + "key"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + getProject()
            + Constants.QUOTE_DOUBLE
            + "");
    tab = Constants.tabIncriment(tab, newLine * -1);
    stringBuilder.append(Constants.nlTab(newLine, tab) + "}");
    return stringBuilder.toString();
  }
}
