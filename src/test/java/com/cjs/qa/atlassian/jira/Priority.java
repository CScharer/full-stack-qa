package com.cjs.qa.atlassian.jira;

import com.cjs.qa.utilities.Constants;

public class Priority {

  private EPriority ePriority = EPriority.CRITICAL;

  public Priority(EPriority ePriority) {
    setePriority(ePriority);
  }

  public EPriority getePriority() {
    return ePriority;
  }

  public void setePriority(EPriority ePriority) {
    this.ePriority = ePriority;
  }

  @Override
  public String toString() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(Constants.QUOTE_DOUBLE + "priority" + Constants.QUOTE_DOUBLE + ": {");
    stringBuilder.append(
        Constants.QUOTE_DOUBLE
            + "name"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + getePriority().getValue()
            + Constants.QUOTE_DOUBLE);
    stringBuilder.append("}");
    return stringBuilder.toString();
  }
}
