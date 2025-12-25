package com.cjs.qa.atlassian.jira;

import com.cjs.qa.utilities.Constants;

public class Assignee {

  private String name;

  public Assignee(String name) {
    setName(name);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(Constants.QUOTE_DOUBLE + "assignee" + Constants.QUOTE_DOUBLE + ": {");
    stringBuilder.append(
        Constants.QUOTE_DOUBLE
            + "name"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + getName()
            + Constants.QUOTE_DOUBLE);
    stringBuilder.append("}");
    return stringBuilder.toString();
  }
}
