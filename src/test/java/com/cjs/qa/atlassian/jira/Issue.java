package com.cjs.qa.atlassian.jira;

import com.cjs.qa.utilities.Constants;

public class Issue {

  private String description = null;
  private String summary = null;
  private String name = "Defect";

  public Issue(String summary, String description, String name) {
    setSummary(summary);
    setDescription(description);
    setName(name);
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
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
            + "summary"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + getSummary()
            + Constants.QUOTE_DOUBLE
            + ",");
    stringBuilder.append(
        Constants.nlTab(newLine, tab)
            + Constants.QUOTE_DOUBLE
            + "description"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + getDescription()
            + Constants.QUOTE_DOUBLE
            + ",");
    stringBuilder.append(
        Constants.nlTab(newLine, tab)
            + Constants.QUOTE_DOUBLE
            + "issuetype"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + "");
    stringBuilder.append(Constants.nlTab(newLine, tab) + "{");
    tab = Constants.tabIncriment(tab, newLine * 1);
    stringBuilder.append(
        Constants.nlTab(newLine, tab)
            + Constants.QUOTE_DOUBLE
            + "name"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + getName()
            + Constants.QUOTE_DOUBLE);
    tab = Constants.tabIncriment(tab, newLine * -1);
    stringBuilder.append(Constants.nlTab(newLine, tab) + "}");
    return stringBuilder.toString();
  }
}
