package com.cjs.qa.utilities;

import java.util.Locale;

import org.apache.logging.log4j.LogManager;

public class ReadFromExcel {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(ReadFromExcel.class));

  public void whichTestType(String type) {
    // Java 17: Switch expression (void method, so using block syntax)
    switch (type.toLowerCase(Locale.ENGLISH)) {
      case "policyverification" -> {
        // go to policyVerification class to do all the things
      }
      case "policyverificationbuild" -> {
        // go to policyVerificationBuild class to do all the things
      }
      case "policyentry" -> {
        // go to policyEntry class to do all the things
      }
      case "policyentrybuild" -> {
        // go to policyEntryBuild class to do all the things
      }
      default -> LOG.warn("Unknown test type: {}. No action taken.", type);
    }
  }
}
