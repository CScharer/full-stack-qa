package com.cjs.qa.bts;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cjs.qa.core.Environment;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.IExtension;

public class BTSEnvironment extends Environment {

  private static boolean environmentSet = false;
  public static final String COMPANY = "BTS";
  public static final String FOLDER_DATA =
      Constants.PATH_FILES_DATA + COMPANY + Constants.DELIMETER_PATH;
  public static final String FILE_CONFIG =
      Constants.PATH_ROOT
          + "Configurations"
          + Constants.DELIMETER_PATH
          + "Environments"
          + IExtension.XML;
  public static final String FILE_LOG = FOLDER_DATA + "Log_" + COMPANY + IExtension.LOG;
  private static Map<Integer, String> scenarioErrors = new HashMap<>();
  protected static final List<String> CLASS_EXCLUSIONS = Arrays.asList("");
  private static String gridHub = null;
  private static String gridPort = null;

  public static boolean isEnvironmentSet() {
    return environmentSet;
  }

  public static void setEnvironmentSet(boolean value) {
    environmentSet = value;
  }

  public static Map<Integer, String> getScenarioErrors() {
    return scenarioErrors;
  }

  public static String getGridHub() {
    return gridHub;
  }

  public static String getGridPort() {
    return gridPort;
  }
}
