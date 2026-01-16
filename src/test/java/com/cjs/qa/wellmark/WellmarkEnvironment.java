package com.cjs.qa.wellmark;

import java.util.List;

import com.cjs.qa.core.Environment;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;

public class WellmarkEnvironment extends Environment {

  public static final String COMPANY = "Wellmark";
  public static final String FOLDER_DATA =
      Constants.PATH_FILES_DATA + COMPANY + Constants.DELIMETER_PATH;
  public static final String FILE_CONFIG =
      Constants.PATH_ROOT + "config" + Constants.DELIMETER_PATH + "Environments" + IExtension.XML;
  public static final String FILE_LOG = FOLDER_DATA + "Log_" + COMPANY + IExtension.LOG;
  public static final String URL_LOGIN =
      "https://ebusiness."
          + COMPANY
          + IExtension.COM
          + "/"
          + COMPANY
          + "SingleSignOn/My"
          + COMPANY
          + "MemberPortal/";
  private final List<String> classExlusionList =
      JavaHelpers.getExclusions(this.getClass().getPackage().getName());

  public List<String> getClassExlusionList() {
    return classExlusionList;
  }
}
