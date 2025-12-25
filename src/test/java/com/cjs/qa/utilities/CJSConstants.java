package com.cjs.qa.utilities;

import com.cjs.qa.core.security.EPasswords;
import com.cjs.qa.selenium.ISelenium;

public class CJSConstants {

  public static final boolean SYSO_ALL = true;
  public static final String PATH_FILES_DATA = Constants.PATH_FILES_DATA;
  public static final String PATH_FILES_DATA_PROJECT =
      Constants.PATH_PROJECT + "Data" + Constants.DELIMETER_PATH;
  public static final String PATH_FILES_TOOLS =
      Constants.PATH_PROJECT + "Tools" + Constants.DELIMETER_PATH;
  public static final String PATH_FILES_SQL =
      PATH_FILES_DATA_PROJECT + "SQL" + Constants.DELIMETER_PATH;
  public static final String PATH_FILES_TEMPLATES =
      PATH_FILES_DATA_PROJECT + "Templates" + Constants.DELIMETER_PATH;
  public static final String BROWSER_DEFAULT = ISelenium.BROWSER_DEFAULT;
  public static final String MAILDOMAIN = "@msn" + IExtension.COM;
  public static final String MAILDOMAIN_AOL = "@aol" + IExtension.COM;
  public static final String MAILDOMAIN_GMAIL = "@gmail" + IExtension.COM;
  public static final String MAILDOMAIN_MSN = MAILDOMAIN;
  public static final String USERID_DHS = "CHRISTOPHER.SCHARER@IOWAID";
  public static final String USERID_VIVIT = "CScharer";
  public static final String MAILDOMAIN_VIVIT = "@Vivit-Worldwide" + IExtension.ORG;
  public static final String EMAIL_ADDRESS_AOL = USERID_VIVIT + MAILDOMAIN_AOL;
  public static final String EMAIL_ADDRESS_GMAIL = "ChrisScharer1416" + MAILDOMAIN_GMAIL;
  public static final String EMAIL_ADDRESS_MSN = "ChrisScharer1416" + MAILDOMAIN_MSN;
  public static final String EMAIL_ADDRESS_VIVIT = "Christopher.Scharer" + MAILDOMAIN_VIVIT;
  public static final String PASSWORD = EPasswords.BTSQA.getValue();
  public static final long serialVersionUID = -5288572903757071304L;
  public static final String EMPTY = "<EMPTY>";
  public static final String URL_BASE = "http://www.cjsconsulting.com/";
  public static final String JIRA_TEST_DATABASE_SERVER = "";
  public static final String JIRA_TEST_DATABASE_PORT = "";
  public static final String JIRA_TEST_DATABASE_NAME = "";
  public static final String QA_SERVICE_ACCOUNT_NAME = "";
  public static final String QA_SERVICE_ACCOUNT_PASSWORD = "";
}
