package com.cjs.qa.ym.api.namespace;

import com.cjs.qa.core.QAException;
import com.cjs.qa.ym.api.services.YMService;

public class ExportStatus extends YMService {

  public static final String COMPLETE = "COMPLETE";
  public static final String FAILURE = "FAILURE";
  public static final String UNKNOWN = "UNKNOWN";
  public static final String WORKING = "WORKING";

  public static String getValue(String status) throws QAException {
    switch (status) {
      case "0":
        return UNKNOWN;
      case "1":
        return WORKING;
      case "2":
        return COMPLETE;
      case "-1":
      default:
        return FAILURE;
    }
  }
}
