package com.cjs.qa.united;

import java.util.List;

import com.cjs.qa.core.Environment;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;

public class UnitedEnvironment extends Environment {

  public static final String COMPANY = "United";
  public static final String FOLDER_DATA =
      Constants.PATH_FILES_DATA + COMPANY + Constants.DELIMETER_PATH;
  public static final String FILE_CONFIG =
      Constants.PATH_ROOT
          + "Configurations"
          + Constants.DELIMETER_PATH
          + "Environments"
          + IExtension.XML;
  public static final String FILE_LOG = FOLDER_DATA + "Log_" + COMPANY + IExtension.LOG;
  public static final String URL_HOME = "https://www." + COMPANY + IExtension.COM;
  // ual/en/us";
  public static final String URL_LOGIN = URL_HOME;
  // + "/account/account/signin";
  private final List<String> classExlusionList =
      JavaHelpers.getExclusions(this.getClass().getPackage().getName());

  public List<String> getClassExlusionList() {
    return classExlusionList;
  }
}
