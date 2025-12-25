package com.cjs.qa.atlassian.jira;

import com.cjs.qa.atlassian.Atlassian;
import com.cjs.qa.utilities.Constants;

public class Jira {

  private String url = "jira" + Atlassian.getDomain();
  private Issue issue;
  private Project project;

  public Jira(Issue issue, Project project) {
    setProject(project);
    setIssue(issue);
  }

  public Issue getIssue() {
    return issue;
  }

  public void setIssue(Issue issue) {
    this.issue = issue;
  }

  public Project getProject() {
    return project;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
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
    stringBuilder.append(Constants.nlTab(newLine, tab) + "{");
    tab = Constants.tabIncriment(tab, newLine * 1);
    stringBuilder.append(
        Constants.nlTab(newLine, tab)
            + Constants.QUOTE_DOUBLE
            + "fields"
            + Constants.QUOTE_DOUBLE
            + ":");
    stringBuilder.append(Constants.nlTab(newLine, tab) + "{");
    stringBuilder.append(Constants.nlTab(newLine, tab) + getProject());
    stringBuilder.append(Constants.nlTab(newLine, tab) + getIssue());
    stringBuilder.append(Constants.nlTab(newLine, tab) + "}");
    tab = Constants.tabIncriment(tab, newLine * -1);
    stringBuilder.append(Constants.nlTab(newLine, tab) + "}");
    return stringBuilder.toString();
  }
}
