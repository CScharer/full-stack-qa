package com.cjs.qa.atlassian.jira;

import com.cjs.qa.utilities.Constants;

public class Summary {

  private String summary;

  public Summary(String summary) {
    setSummary(summary);
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
    stringBuilder.append(
        Constants.QUOTE_DOUBLE
            + "summary"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + getSummary()
            + Constants.QUOTE_DOUBLE);
    return stringBuilder.toString();
  }
}
