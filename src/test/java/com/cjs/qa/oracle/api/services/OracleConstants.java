package com.cjs.qa.oracle.api.services;

import com.cjs.qa.utilities.Constants;

public class OracleConstants {

  public static final boolean EXTRA_LOGGING = false;
  public static final boolean MAPPING = true;
  public static final String API_BASE = "cjsconsulting"; // Use
  // "cjsconsultingperf"
  // for
  // UAT
  public static final String API_ORG_VERSION_MAJOR = "v1";
  public static final String API_ORG_VERSION_MINOR = "m1";
  public static final String API_ROL_VERSION_MAJOR = "v1";
  public static final String API_ROL_VERSION_MINOR = "m1";
  public static final String API_SSO_VERSION_MAJOR = "v2";
  public static final String API_SSO_VERSION_MINOR = "m1";
  public static final String API_JSON_SUCCESS =
      Constants.QUOTE_DOUBLE
          + "status"
          + Constants.QUOTE_DOUBLE
          + ":"
          + Constants.QUOTE_DOUBLE
          + "Success"
          + Constants.QUOTE_DOUBLE
          + "";
}
