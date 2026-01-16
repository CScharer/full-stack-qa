package com.cjs.qa.vivit;

import java.util.List;

import com.cjs.qa.core.Environment;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;

public class VivitEnvironment extends Environment {

  public static final String COMPANY = "Vivit";
  public static final String EMAIL_SIGNATURE =
      FSOTests.fileReadAll(Constants.PATH_OUTLOOK_SIGNATURES + COMPANY + IExtension.HTM);
  public static final String FOLDER_DATA =
      Constants.PATH_FILES_DATA + COMPANY + Constants.DELIMETER_PATH;
  public static final String FILE_CONFIG =
      Constants.PATH_ROOT + "config" + Constants.DELIMETER_PATH + "Environments" + IExtension.XML;
  public static final String FILE_LOG = FOLDER_DATA + "Log_" + COMPANY + IExtension.LOG;
  public static final String URL_LOGIN = "https://www." + COMPANY + "-worldwide.org/";
  private final List<String> classExlusionList =
      JavaHelpers.getExclusions(this.getClass().getPackage().getName());

  public List<String> getClassExlusionList() {
    return classExlusionList;
  }
}
