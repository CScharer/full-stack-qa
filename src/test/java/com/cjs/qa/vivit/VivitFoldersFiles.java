package com.cjs.qa.vivit;

import com.cjs.qa.jdbc.DBConnections;
import com.cjs.qa.utilities.CJSConstants;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.DateHelpersTests;
import com.cjs.qa.utilities.IExtension;

public final class VivitFoldersFiles {

  public static final String PATH_DATA =
      VivitEnvironment.FOLDER_DATA + "Data" + Constants.DELIMETER_PATH;
  public static final String PATH_DATA_TODAY =
      PATH_DATA
          + DateHelpersTests.getCurrentDateTime(DateHelpersTests.FORMAT_YYYY_MM_DD_COMPACT)
          + Constants.DELIMETER_PATH;
  public static final String PATH_API_DATA_YM = PATH_DATA_TODAY + "YM" + Constants.DELIMETER_PATH;
  public static final String PATH_API_DATA_GTW = PATH_DATA_TODAY + "GTW" + Constants.DELIMETER_PATH;
  public static final String DATABASE = DBConnections.getDBParametersQAAuto("").getName();
  public static final String INITIALIZATION_SQL =
      CJSConstants.PATH_FILES_SQL + "Vivit_Initilalize" + IExtension.SQL;
  public static final String DATABASE_WORKING = PATH_API_DATA_YM + "qadb" + IExtension.SQLITE;
  public static final String DATA_FLIGHTS =
      PATH_DATA
          + "Flights"
          + Constants.DELIMETER_PATH
          + DateHelpersTests.getCurrentDateTime(DateHelpersTests.FORMAT_YYYY_MM_DD_COMPACT)
          + IExtension.XLS;
  // API YM Data Import into Database
  public static final String DATA_YMAPI_DATA =
      PATH_API_DATA_YM + "Data_API_YM_ImportAll" + IExtension.XLS;
  // API YM Data Export from API YM
  public static final String DATA_YMAPI_DATA_MEMBERS =
      PATH_API_DATA_YM + "Data_API_YM_ExportMembers" + IExtension.CSV;
  public static final String DATA_YMAPI_DATA_MEMBER_GROUPS =
      PATH_API_DATA_YM + "Data_API_YM_ExportMemberGroups" + IExtension.CSV;
  // API YM Data Update Database
  public static final String PATH_API_DATA_YM_SQL =
      PATH_API_DATA_YM + "SQL" + Constants.DELIMETER_PATH;
  public static final String FILE_SQL_LOG = PATH_API_DATA_YM_SQL + "SQL" + IExtension.LOG;
  public static final String PATH_API_DATA_YM_EVENTS =
      PATH_API_DATA_YM + "Events" + Constants.DELIMETER_PATH;
  public static final String PATH_API_DATA_YM_EVENT_INFORMATION =
      PATH_API_DATA_YM_EVENTS + "EventInformation" + Constants.DELIMETER_PATH;
  public static final String PATH_API_DATA_YM_EVENT_REGISTRATION_IDS =
      PATH_API_DATA_YM_EVENTS + "RegistrationIDs" + Constants.DELIMETER_PATH;
  public static final String PATH_API_DATA_YM_EVENT_REGISTRATION =
      PATH_API_DATA_YM_EVENTS + "Registration" + Constants.DELIMETER_PATH;
  public static final String PATH_API_DATA_YM_EVENT_ATTENDEES =
      PATH_API_DATA_YM_EVENTS + "Attendees" + Constants.DELIMETER_PATH;
  // Reports
  public static final String REPORT_HTM_AUTOMATION_TEMPLATE =
      CJSConstants.PATH_FILES_TEMPLATES + "AutomationReportTemplate" + IExtension.HTM;
  public static final String REPORT_HTM_AUTOMATION =
      PATH_API_DATA_YM + "Report_Automation" + IExtension.HTM;
  public static final String STANDARD_TABLE =
      CJSConstants.PATH_FILES_TEMPLATES + "AutomationStandardTable" + IExtension.HTM;
  public static final String REPORT_HTM_AUTOMATION_TEST_INFORMATION =
      PATH_API_DATA_YM + "Report_TestInformation" + IExtension.HTM;
  public static final String REPORT_HTM_AUTOMATION_BILLABLE_HOURS =
      PATH_API_DATA_YM
          + "Report_Treasurer_Billable_Hours"
          + "_"
          + DateHelpersTests.getCurrentDateTime(DateHelpersTests.FORMAT_MMM)
          + IExtension.HTM;
  public static final String REPORT_HTM_AUTOMATION_TREASURER =
      PATH_API_DATA_YM
          + "Report_Treasurer_Monthly_"
          + DateHelpersTests.getCurrentDateTime(DateHelpersTests.FORMAT_MMM)
          + IExtension.HTM;
  public static final String REPORT_HTM_AUTOMATION_BROKEN_LINKS =
      PATH_API_DATA_YM + "Report_BrokenLinks" + IExtension.HTM;
  public static final String REPORT_HTM_AUTOMATION_DATABASE_CHANGES =
      PATH_API_DATA_YM + "Report_DatabaseChanges" + IExtension.HTM;
  public static final String REPORT_HTM_AUTOMATION_MEMBER_CHANGES =
      PATH_API_DATA_YM + "Report_Member_Changes" + IExtension.HTM;
  public static final String REPORT_HTM_AUTOMATION_EVENTS_YM =
      PATH_API_DATA_YM + "Report_Events_YM" + IExtension.HTM;
  public static final String REPORT_HTM_AUTOMATION_EVENT_ATTENDEES_YM =
      PATH_API_DATA_YM + "Report_EventAttendees_YM" + IExtension.HTM;
  public static final String REPORT_HTM_AUTOMATION_EVENT_REGISTRATION_YM =
      PATH_API_DATA_YM + "Report_EventRegistration_YM" + IExtension.HTM;
  public static final String REPORT_XLS_AUTOMATION_A =
      PATH_API_DATA_YM + "Report_Daily" + IExtension.XLS;
  public static final String REPORT_XLS_AUTOMATION_S =
      PATH_API_DATA_YM + "Report_Daily_Statistics" + IExtension.XLS;
  public static final String REPORT_XLS_AUTOMATION_S_MONTH =
      PATH_API_DATA_YM + "Report_Daily_Statistics_Month" + IExtension.XLS;
  public static final String REPORT_XLS_AUTOMATION_S_YEAR =
      PATH_API_DATA_YM + "Report_Daily_Statistics_Year" + IExtension.XLS;
  public static final String REPORT_XLS_AUTOMATION_T =
      PATH_API_DATA_YM
          + "Report_Treasurer_Monthly_"
          + DateHelpersTests.getCurrentDateTime(DateHelpersTests.FORMAT_MMM)
          + IExtension.XLS;
  public static final String BAT = VivitEnvironment.FOLDER_DATA + "AddChartsBar" + IExtension.BAT;
  public static final String BATRUN =
      VivitEnvironment.FOLDER_DATA + "AddChartsBarRun" + IExtension.BAT;
  public static final String VBS = VivitEnvironment.FOLDER_DATA + "AddChartsBar" + IExtension.BAT;
  public static final String LOGO_SOURCE =
      CJSConstants.PATH_FILES_TEMPLATES + "VivitLogoSource" + IExtension.TXT;

  private VivitFoldersFiles() {
    // Empty
  }
}
