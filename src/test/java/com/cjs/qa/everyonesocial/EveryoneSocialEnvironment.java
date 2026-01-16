package com.cjs.qa.everyonesocial;

import java.util.List;

import com.cjs.qa.core.Environment;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;

public class EveryoneSocialEnvironment extends Environment {

  public static final String COMPANY = "EveroneSocial";
  public static final String FOLDER_DATA =
      Constants.PATH_FILES_DATA + COMPANY + Constants.DELIMETER_PATH;
  public static final String FILE_CONFIG =
      Constants.PATH_ROOT + "config" + Constants.DELIMETER_PATH + "Environments" + IExtension.XML;
  public static final String FILE_LOG = FOLDER_DATA + "Log_" + COMPANY + IExtension.LOG;
  public static final String URL_BASE = "https://app." + COMPANY + IExtension.COM;
  public static final String URL_MY_PAGE =
      URL_BASE + "/enterprise_clients/hpe-christopher-scharer/company/24541";
  public static final String URL_LOGIN = URL_BASE + "/enterprise-login";
  private final List<String> classExlusionList =
      JavaHelpers.getExclusions(this.getClass().getPackage().getName());

  public List<String> getClassExlusionList() {
    return classExlusionList;
  }
}
