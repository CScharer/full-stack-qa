package com.cjs.qa.bts.policy;

import com.cjs.qa.core.Environment;
import com.cjs.qa.utilities.DateHelpersTests;

public class Policy {

  private String userName = null;
  private String computerName = null;
  private String policy = null;
  private String policyNumber = null;
  private String sequenceNumber = null;
  private String dateTimeStamp = DateHelpersTests.getCurrentDateAndTime();

  public String getUserName() {
    return userName;
  }

  public String getComputerName() {
    return computerName;
  }

  public String getPolicy() {
    return policy;
  }

  public String getPolicyNumber() {
    return policyNumber;
  }

  public String getSequenceNumber() {
    return sequenceNumber;
  }

  public String getDateTimeStamp() {
    return dateTimeStamp;
  }

  public static void main(String[] args) {
    // Placeholder main method - not currently used
  }

  public Policy(String policy) {
    this.computerName = Environment.getComputerName();
    this.userName = Environment.getCurrentUser();
    this.dateTimeStamp = DateHelpersTests.getCurrentDateAndTime();
    this.policy = policy;
    this.policyNumber = policy.substring(0, policy.indexOf("-"));
    this.sequenceNumber = policy.substring(policy.indexOf("-") + 1, policy.length());
  }
}
