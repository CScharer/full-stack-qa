package com.cjs.qa.utilities;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Constants {

  public static final String DELIMETER_PATH = System.getProperty("file.separator");
  private static final String FS = DELIMETER_PATH;
  private static final String OS_NAME = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
  public static final boolean IS_WINDOWS = OS_NAME.contains("windows");
  public static final boolean IS_MAC = OS_NAME.contains("mac");
  public static final boolean IS_LINUX = OS_NAME.contains("linux") || OS_NAME.contains("unix");

  // Cross-platform local path: Windows uses C:\, Mac/Linux use root /
  public static final String PATH_LOCAL = IS_WINDOWS ? "C:" + FS : FS;
  // System.getenv("USERNAME").toUpperCase(Locale.ENGLISH)
  public static final String CURRENT_USER =
      System.getProperty("user.name").toUpperCase(Locale.ENGLISH);
  public static final String PATH_TEMP = System.getProperty("java.io.tmpdir");
  // Windows-specific temp path, fallback to system temp on other platforms
  public static final String PATH_TEMP_WINDOWS =
      IS_WINDOWS ? PATH_LOCAL + "WINDOWS" + FS + "TEMP" + FS : PATH_TEMP;
  // Cross-platform user path: Windows uses C:\Users\, Mac/Linux use /Users/ or /home/
  public static final String PATH_CURRENT_USER =
      IS_WINDOWS
          ? PATH_LOCAL + "Users" + FS + CURRENT_USER + FS
          : IS_MAC ? FS + "Users" + FS + CURRENT_USER + FS : FS + "home" + FS + CURRENT_USER + FS;
  public static final String PATH_CURRENT_USER_HOME = System.getProperty("user.home") + FS;
  public static final String PATH_DESKTOP = PATH_CURRENT_USER_HOME + "Desktop" + FS;
  public static final String PATH_DOWNLOADS = PATH_CURRENT_USER_HOME + "Downloads" + FS;
  // Windows-specific AppData paths, null/empty on other platforms
  public static final String PATH_APPDATA =
      IS_WINDOWS ? PATH_CURRENT_USER + "AppData" + FS : PATH_CURRENT_USER_HOME + ".config" + FS;
  public static final String PATH_APPDATA_LOCAL =
      IS_WINDOWS ? PATH_APPDATA + "Local" + FS : PATH_CURRENT_USER_HOME + ".local" + FS;
  public static final String PATH_APPDATA_ROAMING =
      IS_WINDOWS ? PATH_APPDATA + "Roaming" + FS : PATH_CURRENT_USER_HOME + ".config" + FS;
  // Windows-specific SQLite path, use system PATH or common locations on other platforms
  public static final String PATH_SQLITE3_FILE =
      IS_WINDOWS
          ? PATH_APPDATA_LOCAL + "Android" + FS + "sdk" + FS + "platform-tools" + FS + "sqlite3.exe"
          : IS_MAC
              ? FS + "usr" + FS + "bin" + FS + "sqlite3"
              : FS + "usr" + FS + "bin" + FS + "sqlite3";
  // Windows-specific Outlook path, not applicable on other platforms
  public static final String PATH_OUTLOOK_SIGNATURES =
      IS_WINDOWS
          ? PATH_APPDATA_ROAMING + "Microsoft" + FS + "Signatures" + FS
          : PATH_CURRENT_USER_HOME + ".outlook" + FS + "Signatures" + FS;
  // Cross-platform workspace: Windows uses C:\Workspace, Mac/Linux use user home
  public static final String PATH_FILES_WORKSPACE =
      IS_WINDOWS ? PATH_LOCAL + "Workspace" + FS : PATH_CURRENT_USER_HOME + "Workspace" + FS;
  public static final String PATH_FILES_DATA = PATH_FILES_WORKSPACE + "Data" + FS;
  public static final String PATH_FILES_DATA_DATABASES = PATH_FILES_DATA + "Databases" + FS;
  public static final String PATH_FILES_XML = PATH_FILES_DATA + "xml" + FS;
  public static final String PATH_FILES_SOAPUI_RESULTS = PATH_FILES_DATA + "SoapUI" + FS;
  public static final String PATH_PROJECT = System.getProperty("user.dir") + FS;
  public static final String PATH_ROOT = PATH_PROJECT;
  // Cross-platform drivers path: Windows uses C:\, Mac/Linux use user home or /opt
  public static final String PATH_DRIVERS_LOCAL =
      IS_WINDOWS
          ? "C:" + FS + "Selenium" + FS + "Grid2" + FS + "Drivers" + FS
          : IS_MAC
              ? PATH_CURRENT_USER_HOME + "selenium" + FS + "drivers" + FS
              : FS + "opt" + FS + "selenium" + FS + "drivers" + FS;
  // PATH_DRIVERS_REPOSITORY removed - legacy constant, WebDriverManager handles drivers now
  // public static final String PATH_DRIVERS_REPOSITORY =
  //     PATH_ROOT + "src" + FS + "test" + FS + "resources" + FS + "Drivers" + FS;
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

  /**
   * Detects if tests are running in CI/CD pipeline. Checks common CI environment variables: CI,
   * GITHUB_ACTIONS, CONTINUOUS_INTEGRATION
   *
   * @return true if running in CI, false if running locally
   */
  public static boolean isRunningInCI() {
    return ("true".equalsIgnoreCase(System.getenv("CI"))
        || "true".equalsIgnoreCase(System.getenv("GITHUB_ACTIONS"))
        || "true".equalsIgnoreCase(System.getenv("CONTINUOUS_INTEGRATION")));
  }
}
