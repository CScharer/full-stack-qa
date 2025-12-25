package com.cjs.qa.atlassian.jira;

import com.cjs.qa.utilities.Constants;

public class IssueType {

  private EIssueType issueType = EIssueType.DEFECT;

  public IssueType(EIssueType issueType) {
    setIssueType(issueType);
  }

  public EIssueType getIssueType() {
    return issueType;
  }

  public void setIssueType(EIssueType issueType) {
    this.issueType = issueType;
  }

  @Override
  public String toString() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(Constants.QUOTE_DOUBLE + "issuetype" + Constants.QUOTE_DOUBLE + ": {");
    stringBuilder.append(
        Constants.QUOTE_DOUBLE
            + "name"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + ""
            + getIssueType().getValue()
            + Constants.QUOTE_DOUBLE);
    stringBuilder.append("}");
    return stringBuilder.toString();
  }
}
