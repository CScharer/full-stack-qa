package com.cjs.qa.atlassian.jira;

import com.cjs.qa.utilities.Constants;

public class Description {

  private String description;

  public Description(String description) {
    setDescription(description);
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String summary) {
    this.description = summary;
  }

  @Override
  public String toString() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(
        Constants.QUOTE_DOUBLE
            + "description"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + ""
            + getDescription()
            + Constants.QUOTE_DOUBLE);
    return stringBuilder.toString();
  }
}
