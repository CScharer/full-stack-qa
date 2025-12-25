package com.cjs.qa.jenkins;

import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;

public final class Jenkins {

  private Jenkins() {
    // Empty
  }

  // JENKINS_HOME
  public static final String COMPANY = "Jenkins";
  public static final String PATH_HOME =
      Constants.PATH_DRIVERS_LOCAL + COMPANY + Constants.DELIMETER_PATH;
  public static final String PATH_JOBS = PATH_HOME + "Jobs" + Constants.DELIMETER_PATH;

  public static void copyFileLog(String filePathDestination) throws Throwable {
    String jenkinsBuildNumber = JavaHelpers.readPropertyOrEnv("BUILD_NUMBER");
    if (JavaHelpers.hasValue(jenkinsBuildNumber)) {
      // Vivit
      // String jenkinsJobName = JavaHelpers.readPropertyOrEnv("JOB_NAME")
      String jenkinsWorkspace =
          JavaHelpers.readPropertyOrEnv("WORKSPACE") + Constants.DELIMETER_PATH;
      // C:\Jenkins\jobs\Vivit\builds\999\log
      String filePathNameSource =
          jenkinsWorkspace
              + "builds"
              + Constants.DELIMETER_PATH
              + jenkinsBuildNumber
              + Constants.DELIMETER_PATH
              + "log";
      // C:\Workspace\Data\Vivit\Data\20190101\Jenkins_999.log
      String filePathNameDestination =
          filePathDestination + COMPANY + "_" + jenkinsBuildNumber + IExtension.LOG;
      if (FSOTests.fileExists(filePathNameSource)) {
        FSOTests.fileCopy(filePathNameSource, filePathNameDestination);
      }
    }
  }
}
