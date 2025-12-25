package com.cjs.qa.atlassian.jira;

import com.cjs.qa.utilities.Constants;

public class TimeTracking {

  private String originalEstimate;
  private String remainingEstimate;

  public TimeTracking(String originalEstimate, String remainingEstimate) {
    setOriginalEstimate(originalEstimate);
    setRemainingEstimate(remainingEstimate);
  }

  public String getOriginalEstimate() {
    return originalEstimate;
  }

  public void setOriginalEstimate(String originalEstimate) {
    this.originalEstimate = originalEstimate;
  }

  public String getRemainingEstimate() {
    return remainingEstimate;
  }

  public void setRemainingEstimate(String remainingEstimate) {
    this.remainingEstimate = remainingEstimate;
  }

  @Override
  public String toString() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(Constants.QUOTE_DOUBLE + "timetracking" + Constants.QUOTE_DOUBLE + ": {");
    stringBuilder.append(
        Constants.QUOTE_DOUBLE
            + "originalEstimate"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + getOriginalEstimate()
            + Constants.QUOTE_DOUBLE
            + ",");
    stringBuilder.append(
        Constants.QUOTE_DOUBLE
            + "remainingEstimate"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + getRemainingEstimate()
            + Constants.QUOTE_DOUBLE);
    stringBuilder.append("}");
    return stringBuilder.toString();
  }
}
