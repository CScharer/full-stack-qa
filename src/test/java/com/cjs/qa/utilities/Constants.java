package com.cjs.qa.utilities;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Constants {

  public static final String DELIMETER_PATH = System.getProperty("file.separator");
  private static final String FS = DELIMETER_PATH;
  public static final String PATH_LOCAL = "C:" + FS;
  // System.getenv("USERNAME").toUpperCase(Locale.ENGLISH)
  public static final String CURRENT_USER =
      System.getProperty("user.name").toUpperCase(Locale.ENGLISH);
  public static final String PATH_TEMP = System.getProperty("java.io.tmpdir");
  public static final String PATH_TEMP_WINDOWS = PATH_LOCAL + "WINDOWS" + FS + "TEMP" + FS;
  public static final String PATH_CURRENT_USER = PATH_LOCAL + "Users" + FS + CURRENT_USER + FS;
  public static final String PATH_CURRENT_USER_HOME = System.getProperty("user.home") + FS;
  public static final String PATH_DESKTOP = PATH_CURRENT_USER + "Desktop" + FS;
  public static final String PATH_DOWNLOADS = PATH_CURRENT_USER + "Downloads" + FS;
  public static final String PATH_APPDATA = PATH_CURRENT_USER + "AppData" + FS;
  public static final String PATH_APPDATA_LOCAL = PATH_APPDATA + "Local" + FS;
  public static final String PATH_APPDATA_ROAMING = PATH_APPDATA + "Roaming" + FS;
  public static final String PATH_SQLITE3_FILE =
      PATH_APPDATA_LOCAL + "Android" + FS + "sdk" + FS + "platform-tools" + FS + "sqlite3.exe";
  public static final String PATH_OUTLOOK_SIGNATURES =
      PATH_APPDATA_ROAMING + "Microsoft" + FS + "Signatures" + FS;
  public static final String PATH_FILES_WORKSPACE = PATH_LOCAL + "Workspace" + FS;
  public static final String PATH_FILES_DATA = PATH_FILES_WORKSPACE + "Data" + FS;
  public static final String PATH_FILES_DATA_DATABASES = PATH_FILES_DATA + "Databases" + FS;
  public static final String PATH_FILES_XML = PATH_FILES_DATA + "XML" + FS;
  public static final String PATH_FILES_SOAPUI_RESULTS = PATH_FILES_DATA + "SoapUI" + FS;
  public static final String PATH_PROJECT = System.getProperty("user.dir") + FS;
  public static final String PATH_ROOT = PATH_PROJECT;
  public static final String PATH_DRIVERS_LOCAL =
      "C:" + FS + "Selenium" + FS + "Grid2" + FS + "Drivers" + FS;
  public static final String PATH_DRIVERS_REPOSITORY =
      PATH_ROOT + "src" + FS + "test" + FS + "resources" + FS + "Drivers" + FS;
  public static final String PATH_SCREENSHOTS = PATH_ROOT + "Screenshots" + FS;
  public static final String PATH_FILES_CONFIGURATIONS = PATH_ROOT + "config" + FS;
  public static final String DELIMETER_LIST = System.getProperty("path.separator");
  public static final String BACKSLASH = "\\";
  public static final String BACKSPACE = "\b";
  public static final String CR = "\r";
  public static final String FORMFEED = "\f";
  public static final String NL = "\n";
  // System.getProperty("line.separator")
  public static final String NEWLINE = CR + NL;
  public static final String PIPE = "|";
  public static final String QUOTE_DOUBLE = "\"";
  public static final String QUOTE_SINGLE = "\'";
  public static final String TAB = "\t";
  public static final String SYMBOL_COPYRIGHT = "©";
  public static final String SYMBOL_REGISTERED = "®";
  public static final String SYMBOL_TRADEMARK = "™";
  public static final String EMPTY = "<EMPTY>";
  public static final int MILLISECONDS = 1000;
  public static final String CJS = SYMBOL_REGISTERED + SYMBOL_COPYRIGHT + "cjs" + SYMBOL_TRADEMARK;
  public static final String CLASS_METHOD_DEBUG = "***ClassMethodDebug***:[";
  // All 50 U.S States
  protected static final List<String> US_ALLSTATES =
      Arrays.asList(
          "Alaska",
          "Alabama",
          "Arkansas",
          "Arizona",
          "California",
          "Colorado",
          "Connecticut",
          "Delaware",
          "Florida",
          "Georgia",
          "Hawaii",
          "Iowa",
          "Idaho",
          "Illinois",
          "Indiana",
          "Kansas",
          "Kentucky",
          "Louisiana",
          "Massachusetts",
          "Maryland",
          "Maine",
          "Michigan",
          "Minnesota",
          "Missouri",
          "Mississippi",
          "Montana",
          "North Carolina",
          "North Dakota",
          "Nebraska",
          "New Hampshire",
          "New Jersey",
          "New Mexico",
          "Nevada",
          "New York",
          "Ohio",
          "Oklahoma",
          "Oregon",
          "Pennsylvania",
          "Rhode Island",
          "South Carolina",
          "South Dakota",
          "Tennessee",
          "Texas",
          "Utah",
          "Virginia",
          "Vermont",
          "Washington",
          "Wisconsin",
          "West Virginia",
          "Wyoming",
          "District of Columbia");

  private boolean formatPretty = false;

  public boolean isFormatPretty() {
    return formatPretty;
  }

  public void setFormatPretty(boolean formatPretty) {
    this.formatPretty = formatPretty;
  }

  public static String cData(String string) {
    return "<![CDATA[" + string + NL + TAB + "]]>";
  }

  public static String cData(StringBuilder stringBuilder) {
    return "<![CDATA[" + stringBuilder.toString() + NL + TAB + "]]>";
  }

  public static String nlTab(int newLines, int tabs) {
    StringBuilder stringBuilder = new StringBuilder();
    for (int line = 0; line < newLines; line++) {
      stringBuilder.append(NEWLINE);
    }
    for (int tab = 0; tab < tabs; tab++) {
      stringBuilder.append(TAB);
    }
    return stringBuilder.toString();
  }

  public static int tabIncriment(int tab, int incriment) {
    tab += incriment;
    return tab;
  }
}
