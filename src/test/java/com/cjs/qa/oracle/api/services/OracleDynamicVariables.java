package com.cjs.qa.oracle.api.services;

import com.cjs.qa.core.api.WebService;

public class OracleDynamicVariables extends WebService {

  public static String getFirstName(String eMailName) {
    String name = eMailName;
    if (eMailName.indexOf("@") > 0) {
      name = eMailName.substring(0, eMailName.indexOf("@"));
    }
    return "FN" + name;
  }

  public static String getFullName(String eMailName) {
    return getFirstName(eMailName) + " " + getLastName(eMailName);
  }

  public static String getLastName(String eMailName) {
    String name = eMailName;
    if (eMailName.indexOf("@") > 0) {
      name = eMailName.substring(0, eMailName.indexOf("@"));
    }
    return "LN" + name;
  }

  public static String getCONName(String eMailName) {
    String name = eMailName;
    if (eMailName.indexOf("@") > 0) {
      name = eMailName.substring(0, eMailName.indexOf("@"));
    }
    return "QA_CON_" + name;
  }

  public static String getLOCName(String eMailName) {
    String name = eMailName;
    if (eMailName.indexOf("@") > 0) {
      name = eMailName.substring(0, eMailName.indexOf("@"));
    }
    return "QA_LOC_" + name;
  }

  public static String getORGName(String eMailName) {
    String name = eMailName;
    if (eMailName.indexOf("@") > 0) {
      name = eMailName.substring(0, eMailName.indexOf("@"));
    }
    return "QA_ORG_" + name;
  }

  public static String getOWNName(String eMailName) {
    String name = eMailName;
    if (eMailName.indexOf("@") > 0) {
      name = eMailName.substring(0, eMailName.indexOf("@"));
    }
    return "QA_OWN_" + name;
  }

  public static String getPROName(String eMailName) {
    String name = eMailName;
    if (eMailName.indexOf("@") > 0) {
      name = eMailName.substring(0, eMailName.indexOf("@"));
    }
    return "QA_PROJECT_" + name;
  }
}
