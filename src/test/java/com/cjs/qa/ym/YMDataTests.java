package com.cjs.qa.ym;

import java.io.FileReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cjs.qa.core.Environment;
import com.cjs.qa.core.QAException;
import com.cjs.qa.jdbc.JDBC;
import com.cjs.qa.jdbc.JDBCConstants;
import com.cjs.qa.jdbc.SQL;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.Convert;
import com.cjs.qa.utilities.DateHelpersTests;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.utilities.ParameterHelper;
import com.cjs.qa.utilities.XML;
import com.cjs.qa.vivit.VivitDataTests;
import com.cjs.qa.vivit.VivitFoldersFiles;
import com.cjs.qa.vivit.VivitTables;
import com.cjs.qa.vivit.VivitViews;
import com.cjs.qa.ym.api.dataobjects.Item;
import com.cjs.qa.ym.api.dataobjects.Results;
import com.cjs.qa.ym.api.dataobjects.UnmarshallYourMembershipResponse;
import com.cjs.qa.ym.api.dataobjects.YourMembershipResponse;
import com.cjs.qa.ym.api.namespace.EventsNamespace;
import com.cjs.qa.ym.api.objects.Event;
import com.cjs.qa.ym.api.services.YMAPI;

public class YMDataTests extends Environment {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(YMDataTests.class));

  public static final String EVENTID_TEST = "1209865";
  public static final String FORMAT_DATE_WEBINAR = DateHelpersTests.FORMAT_US_STANDARD_DATE;
  // public static final String FORMAT_DATE_WEBINAR = "yyyy-MM-dd HH:mm:ss";
  public static final String LABEL_ACTIVE = "active";
  public static final String LABEL_DROP_TABLE = "DropTable";
  public static final String LABEL_CREATE_TABLE = "CreateTable";
  public static final String LABEL_DELETE_FROM = "DeleteFrom";
  public static final String LABEL_INSERT_INTO = "InsertInto";
  public static final String LABEL_EVENT_ID = "EventID";
  public static final String LABEL_RECORD_COMPLETE = "RecordComplete";
  public static final String LABEL_RECORD_NUMBER = "RecordNumber";
  private static YMAPI ymApi = null;
  private static String eventLocationGTWSearch = "Go To Webinar - ";
  private static String eventNameSearch = "Webinar";
  private static int eventsMax = 100;
  private static Results results = new Results();
  private static List<Event> eventList = new ArrayList<>();

  /**
   * @param apiNamespace
   * @param tableName
   * @param fieldsAddList
   * @return
   * @throws Throwable
   */
  public static boolean addMissingFields(
      String apiNamespace, String tableName, List<String> fieldsAddList) throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(
        ParameterHelper.getParameters(
            Arrays.asList(
                Arrays.asList(VivitDataTests.LABEL_API_NAMESPACE, apiNamespace),
                Arrays.asList(VivitDataTests.LABEL_TABLE_NAME, tableName),
                Arrays.asList("fieldsAddList", fieldsAddList))));
    Map<String, String> fieldNameMapTemp;
    try {
      JDBC jdbc = new JDBC("", VivitDataTests.DATABASE_DEFINITION);
      fieldNameMapTemp = VivitDataTests.getDatabaseUpdatedFields();
      jdbc.addFieldsToTableSQLite(tableName, fieldsAddList);
    } catch (final Exception e) {
      sysOut(e);
      return false;
    }
    StringBuilder sqlStringBuilder = new StringBuilder();
    for (final String fieldName : fieldsAddList) {
      sysOut(
          "Added apiNamespace["
              + apiNamespace
              + "], table["
              + tableName
              + "], fieldName["
              + fieldName
              + "]");
      Map<String, String> fieldNameMap = fieldNameMapTemp;
      fieldNameMap.put("ApiMethod", apiNamespace);
      fieldNameMap.put("TableName", tableName);
      fieldNameMap.put("FieldName", fieldName);
      fieldNameMap.put("Action", VivitDataTests.LABEL_FIELD_ADDED);
      sqlStringBuilder =
          SQL.appendStringBuilderSQLInsertRecord(
              VivitTables.VIVIT_DATABASE_CHANGES, sqlStringBuilder, fieldNameMap);
    }
    if (!"".equals(sqlStringBuilder.toString())) {
      SQL.executeVivit(LABEL_INSERT_INTO, JavaHelpers.getCurrentMethodName(), sqlStringBuilder);
    }
    return true;
  }

  /**
   * @param active
   * @throws Throwable
   */
  public static void exportGTW(boolean active) throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList(Arrays.asList(active, LABEL_ACTIVE))));
    if (!active) {
      return;
    }
    exportGTWEvents();
    exportGTWEventInformation();
    exportGTWEventRegistrationIDs();
    exportGTWEventRegistration();
    exportGTWEventAttendees();
  }

  public static void exportGTWEventAttendees() throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList(Arrays.asList())));
    String statusName = "exportGTWEventAttendees";
    if (VivitDataTests.successFileStatus(statusName, true)) {
      return;
    }
    boolean success = false;
    int attempt = 0;
    final int attemptsMax = 3;
    do {
      try {
        getAllEventAttendees();
        success = true;
      } catch (Exception e) {
        LOG.error(
            "{}",
            QAException.ERROR
                + JavaHelpers.getCurrentClassMethodName()
                + ":Error Extracting Event Attendees DataTests.");
        if (attempt > attemptsMax) {
          throw new QAException(QAException.ERROR + "[" + e.getMessage() + "]", e);
        }
      }
    } while (!success);
    VivitDataTests.successFileCreate(statusName);
  }

  public static void exportGTWEventInformation() throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList(Arrays.asList())));
    String statusName = "exportGTWEventInformation";
    if (VivitDataTests.successFileStatus(statusName, true)) {
      return;
    }
    boolean success = false;
    int attempt = 0;
    final int attemptsMax = 3;
    do {
      try {
        getAllEventInformation();
        success = true;
      } catch (Exception e) {
        LOG.error(
            "{}",
            QAException.ERROR
                + JavaHelpers.getCurrentClassMethodName()
                + ":Error Extracting Event Attendees DataTests.");
        if (attempt > attemptsMax) {
          throw new QAException(QAException.ERROR + "[" + e.getMessage() + "]", e);
        }
      }
    } while (!success);
    VivitDataTests.successFileCreate(statusName);
  }

  public static void exportGTWEventRegistrationIDs() throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList(Arrays.asList())));
    String statusName = "exportGTWEventRegistrationIDs";
    if (VivitDataTests.successFileStatus(statusName, true)) {
      return;
    }
    boolean success = false;
    int attempt = 0;
    final int attemptsMax = 3;
    do {
      try {
        getAllEventRegistrationIDs();
        success = true;
      } catch (Exception e) {
        LOG.error(
            "{}",
            QAException.ERROR
                + JavaHelpers.getCurrentClassMethodName()
                + ":Error Extracting Event Attendees DataTests.");
        if (attempt > attemptsMax) {
          throw new QAException(QAException.ERROR + "[" + e.getMessage() + "]", e);
        }
      }
    } while (!success);
    VivitDataTests.successFileCreate(statusName);
  }

  public static void exportGTWEventRegistration() throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList(Arrays.asList())));
    String statusName = "exportGTWEventRegistration";
    if (VivitDataTests.successFileStatus(statusName, true)) {
      return;
    }
    boolean success = false;
    int attempt = 0;
    final int attemptsMax = 3;
    do {
      try {
        getAllEventRegistration();
        success = true;
      } catch (Exception e) {
        LOG.error(
            "{}",
            QAException.ERROR
                + JavaHelpers.getCurrentClassMethodName()
                + ":Error Extracting Event Attendees DataTests.");
        if (attempt > attemptsMax) {
          throw new QAException(QAException.ERROR + "[" + e.getMessage() + "]", e);
        }
      }
    } while (!success);
    VivitDataTests.successFileCreate(statusName);
  }

  public static void exportGTWEvents() throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList(Arrays.asList())));
    String statusName = "exportGTWEvents";
    if (VivitDataTests.successFileStatus(statusName, true)) {
      return;
    }
    boolean success = false;
    int attempt = 0;
    final int attemptsMax = 3;
    do {
      try {
        long pageRecordStart = 1;
        getAllEvents(pageRecordStart);
        success = true;
      } catch (Exception e) {
        LOG.error(
            "{}",
            QAException.ERROR
                + JavaHelpers.getCurrentClassMethodName()
                + ":Error Extracting Event DataTests.");
        if (attempt > attemptsMax) {
          throw new QAException(QAException.ERROR + "[" + e.getMessage() + "]", e);
        }
      }
    } while (!success);
    VivitDataTests.successFileCreate(statusName);
  }

  public static void exportYMGTWTables() throws Throwable {
    if (FSOTests.fileExists(VivitFoldersFiles.DATA_YMAPI_DATA)) {
      // The file has already been created.
      return;
    }
    JDBC.exportDataFromTableView(
        VivitTables.VIVIT_EVENTS_CURRENT,
        VivitFoldersFiles.DATA_YMAPI_DATA,
        "Events",
        VivitDataTests.DATABASE_DEFINITION,
        true);
    JDBC.exportDataFromTableView(
        VivitTables.VIVIT_EVENTINFORMATION_CURRENT,
        VivitFoldersFiles.DATA_YMAPI_DATA,
        "EventInformation",
        VivitDataTests.DATABASE_DEFINITION,
        false);
    JDBC.exportDataFromTableView(
        VivitViews.VIVIT_EVENT_ATTENDEES_CURRENT,
        VivitFoldersFiles.DATA_YMAPI_DATA,
        "EventAttendees",
        VivitDataTests.DATABASE_DEFINITION,
        false);
    JDBC.exportDataFromTableView(
        VivitTables.VIVIT_YMGTW_LINKS_CURRENT,
        VivitFoldersFiles.DATA_YMAPI_DATA,
        "YMGTWLinks",
        VivitDataTests.DATABASE_DEFINITION,
        false);
    JDBC.exportDataFromTableView(
        VivitTables.VIVIT_EVENT_REGISTRATION_IDS_CURRENT,
        VivitFoldersFiles.DATA_YMAPI_DATA,
        "EventRegistrationIDs",
        VivitDataTests.DATABASE_DEFINITION,
        false);
    JDBC.exportDataFromTableView(
        VivitTables.VIVIT_EVENT_REGISTRATION_CURRENT,
        VivitFoldersFiles.DATA_YMAPI_DATA,
        "EventRegistration",
        VivitDataTests.DATABASE_DEFINITION,
        false);
  }

  /**
   * @param dateTimeFrom
   * @throws Throwable
   */
  public static void exportMemberGroups(String dateTimeFrom) throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(
        ParameterHelper.getParameters(
            Arrays.asList(Arrays.asList(VivitDataTests.LABEL_DATE_TIME_FROM, dateTimeFrom))));
    if (getYmapi() == null) {
      setYmApi(new YMAPI());
    }
    Map<String, String> resultsMap =
        getYmApi().getSaExportNamespace().exportDataMemberGroups(dateTimeFrom);
    sysOut("mapResults:[" + resultsMap.toString() + "]");
  }

  /**
   * @param dateTimeFrom
   * @throws Throwable
   */
  public static void exportMembers(String dateTimeFrom) throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(
        ParameterHelper.getParameters(
            Arrays.asList(Arrays.asList(VivitDataTests.LABEL_DATE_TIME_FROM, dateTimeFrom))));
    if (getYmApi() == null) {
      setYmApi(new YMAPI());
    }
    Map<String, String> resultsMap =
        getYmapi().getSaExportNamespace().exportDataMembers(dateTimeFrom);
    sysOut("mapResults:[" + resultsMap.toString() + "]");
  }

  public static List<Event> getEventList() {
    return eventList;
  }

  public static void setEventList(List<Event> eventList) {
    YMDataTests.eventList = eventList;
  }

  public static void getAllEventAttendees() throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList(Arrays.asList())));
    try {
      List<String> eventIDList = getEventIDListValid();
      for (String eventID : eventIDList) {
        String filePathName =
            VivitFoldersFiles.PATH_API_DATA_YM_EVENT_ATTENDEES + eventID + IExtension.XML;
        String xml;
        if (!FSOTests.fileExists(filePathName)) {
          // Create the Event Attendee information.
          if (getYmapi() == null) {
            setYmApi(new YMAPI());
          }
          Map<String, String> eventAttendeeMap =
              getYmapi().getEventsNamespace().eventAttendeesGet(Integer.parseInt(eventID));
          xml = eventAttendeeMap.get("xml");
          FSOTests.fileWrite(filePathName, xml, false);
        }
      }
    } catch (final Exception e) {
      sysOut(e);
    }
  }

  public static void getAllEventInformation() throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList(Arrays.asList())));
    try {
      List<String> eventIDList = getEventIDListValid();
      // eventIDList.add(EVENTID_TEST);
      for (String eventID : eventIDList) {
        String filePathName =
            VivitFoldersFiles.PATH_API_DATA_YM_EVENT_INFORMATION + eventID + IExtension.XML;
        String xml;
        if (!FSOTests.fileExists(filePathName)) {
          // Create the Event Information information.
          if (getYmapi() == null) {
            setYmApi(new YMAPI());
          }
          Map<String, String> eventInformationMap =
              getYmapi().getEventsNamespace().eventGet(Integer.parseInt(eventID));
          xml = eventInformationMap.get("xml");
          FSOTests.fileWrite(filePathName, xml, false);
        }
      }
    } catch (final Exception e) {
      sysOut(e);
    }
  }

  public static void getAllEventRegistrationIDs() throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList(Arrays.asList())));
    try {
      List<String> eventIDList = getEventIDListValid();
      // eventIDList.add(EVENTID_TEST);
      for (String eventID : eventIDList) {
        String filePathName =
            VivitFoldersFiles.PATH_API_DATA_YM_EVENT_REGISTRATION_IDS + eventID + IExtension.XML;
        String xml;
        if (!FSOTests.fileExists(filePathName)) {
          // Create the Event RegistrationIDs information.
          if (getYmapi() == null) {
            setYmApi(new YMAPI());
          }
          Map<String, String> eventInformationMap =
              getYmApi()
                  .getSaEventsNamespace()
                  .eventRegistrationsGetIDs(Integer.parseInt(eventID), null);
          xml = eventInformationMap.get("xml");
          FSOTests.fileWrite(filePathName, xml, false);
        }
      }
    } catch (final Exception e) {
      sysOut(e);
    }
  }

  public static void getAllEventRegistration() throws Throwable {
    sysOut(" " + Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList(Arrays.asList())));
    try {
      List<String> registrationIDList = getEventRegistrationIDListAll();
      // getEventIDListValid();
      // eventIDList.add(EVENTID_TEST);
      for (String registrationID : registrationIDList) {
        String xml = "";
        String filePathName =
            VivitFoldersFiles.PATH_API_DATA_YM_EVENT_REGISTRATION + registrationID + IExtension.XML;
        if (!FSOTests.fileExists(filePathName)) {
          // Create the Event Registration information.
          if (getYmapi() == null) {
            setYmApi(new YMAPI());
          }
          Map<String, String> eventInformationMap =
              getYmapi().getSaEventsNamespace().eventRegistrationGet(registrationID, "");
          xml = eventInformationMap.get("xml");
          FSOTests.fileWrite(filePathName, xml, false);
        }
      }
    } catch (final Exception e) {
      sysOut(e);
    }
  }

  /**
   * @param pageRecordStart
   * @throws Throwable
   */
  public static void getAllEvents(long pageRecordStart) throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(
        ParameterHelper.getParameters(
            Arrays.asList(Arrays.asList(pageRecordStart, "pageRecordStart"))));
    try {
      boolean recordsExist = false;
      do {
        String filePathName =
            VivitFoldersFiles.PATH_API_DATA_YM_EVENTS + pageRecordStart + IExtension.XML;
        String xml;
        if (!FSOTests.fileExists(filePathName)) {
          // Create the Event information.
          if (getYmapi() == null) {
            setYmApi(new YMAPI());
          }
          Map<String, String> mapResults =
              getYmapi()
                  .getEventsNamespace()
                  .allSearch(getEventNameSearch(), EventsNamespace.PAGES_MAX, pageRecordStart);
          xml = mapResults.get("xml");
          FSOTests.fileWrite(filePathName, xml, false);
        }
        xml = FSOTests.fileReadAll(filePathName);
        int recordCount = getEventMapListAll(xml).size();
        sysOut("recordCount:[" + recordCount + "], filePathName:[" + filePathName + "]");
        recordsExist = recordCount > 0;
        pageRecordStart += EventsNamespace.PAGES_MAX;
      } while (recordsExist);
      // mapResults = allSearch(null, 100, 701)
    } catch (final Exception e) {
      sysOut(e);
    }
  }

  public static List<String> getEventIDListValid() throws Throwable {
    List<String> eventIDList = new ArrayList<>();
    List<String> eventFileList = FSOTests.filesList(VivitFoldersFiles.PATH_API_DATA_YM_EVENTS);
    for (String filePathName : eventFileList) {
      String xml = FSOTests.fileReadAll(filePathName);
      sysOut("filePathName:[" + filePathName + "], xml:[" + xml + "]");
      List<Map<String, String>> eventMapListAll = getEventMapListAll(xml);
      for (Map<String, String> sessionMap : eventMapListAll) {
        if (isValidEventYM(sessionMap)) {
          String eventID = sessionMap.get(LABEL_EVENT_ID);
          eventIDList.add(eventID);
        }
      }
    }
    return eventIDList;
  }

  public static List<String> getEventRegistrationIDListAll() throws Throwable {
    List<String> registrationIDList = new ArrayList<>();
    List<String> registrationIDFileList =
        FSOTests.filesList(VivitFoldersFiles.PATH_API_DATA_YM_EVENT_REGISTRATION_IDS);
    for (String filePathName : registrationIDFileList) {
      String xml = FSOTests.fileReadAll(filePathName);
      sysOut("filePathName:[" + filePathName + "], xml:[" + xml + "]");
      List<Map<String, String>> registrationIDMapListAll = getEventRegistrationIDsMapListAll(xml);
      for (Map<String, String> sessionMap : registrationIDMapListAll) {
        String registrationID = sessionMap.get("RegistrationID");
        registrationIDList.add(registrationID);
      }
    }
    return registrationIDList;
  }

  /**
   * @param xml
   * @return
   * @throws Throwable
   */
  public static List<Map<String, String>> getEventInformationMapListAll(String xml)
      throws Throwable {
    List<Map<String, String>> eventInformationMapList = new ArrayList<>();
    final String xPATHROOT = "//Events.Event.Get";
    StringBuilder stringBuilder = new StringBuilder(xPATHROOT);
    final NodeList eventInformationNodeList = XML.getNodeList(xml, stringBuilder.toString());
    for (int eventInformationIndex = 0;
        eventInformationIndex < eventInformationNodeList.getLength();
        eventInformationIndex++) {
      final Map<String, String> sessionMap = EventsNamespace.eventInformationMap();
      for (final String key : sessionMap.keySet()) {
        final Node eventInformationNode = eventInformationNodeList.item(eventInformationIndex);
        final Element eventInformationElement = (Element) eventInformationNode;
        final String value =
            eventInformationElement.getElementsByTagName(key).item(0).getTextContent();
        sessionMap.put(key, value);
      }
      eventInformationMapList.add(sessionMap);
    }
    return eventInformationMapList;
  }

  /**
   * @param xml
   * @return
   * @throws Throwable
   */
  public static List<Map<String, String>> getEventMapListAll(String xml) throws Throwable {
    List<Map<String, String>> eventsMapList = new ArrayList<>();
    final String xPATHROOT = "//Events.All.Search/Results";
    StringBuilder stringBuilder = new StringBuilder(xPATHROOT);
    final NodeList resultNodeList = XML.getNodeList(xml, stringBuilder.toString());
    for (int resultIndex = 0; resultIndex < resultNodeList.getLength(); resultIndex++) {
      final Node resultNode = resultNodeList.item(resultIndex);
      final Element resultElement = (Element) resultNode;
      final String resultTotal = resultElement.getAttribute("ResultTotal");
      getResults().setResultTotal(Integer.parseInt(resultTotal));
      sysOut("resultTotal:[" + resultTotal + "]");
      stringBuilder.append("/Item");
      final NodeList eventNodeList = XML.getNodeList(xml, stringBuilder.toString());
      for (int eventIndex = 0; eventIndex < eventNodeList.getLength(); eventIndex++) {
        final Map<String, String> sessionMap = EventsNamespace.eventMap();
        for (final String key : sessionMap.keySet()) {
          final Node eventNode = eventNodeList.item(eventIndex);
          final Element eventElement = (Element) eventNode;
          final String value = eventElement.getElementsByTagName(key).item(0).getTextContent();
          sessionMap.put(key, value);
        }
        sessionMap.put(LABEL_RECORD_NUMBER, String.valueOf(eventIndex + 1));
        eventsMapList.add(sessionMap);
        getResults()
            .getItem()
            .add(
                new Item(
                    sessionMap.get("eventName"),
                    sessionMap.get("eventID"),
                    sessionMap.get("eventDate")));
        getEventList()
            .add(
                new Event(
                    sessionMap.get("eventDate"),
                    sessionMap.get("eventID"),
                    sessionMap.get("eventName")));
      }
    }
    return eventsMapList;
  }

  /**
   * @param xml
   * @return
   * @throws Throwable
   */
  public static List<Map<String, String>> getEventMapListValid(String xml) throws Throwable {
    List<Map<String, String>> eventsMapList = new ArrayList<>();
    List<Map<String, String>> eventsMapListAll = getEventMapListAll(xml);
    for (Map<String, String> sessionMap : eventsMapListAll) {
      if (isValidEventYM(sessionMap)) {
        eventsMapList.add(sessionMap);
      }
    }
    return eventsMapList;
  }

  public static String getEventLocationGTWSearch() {
    return eventLocationGTWSearch;
  }

  public static String getEventNameSearch() {
    return eventNameSearch;
  }

  public static int getEventsMax() {
    return eventsMax;
  }

  public static String getInceptionDateTime() throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList()));
    String dateTimeFrom = null;
    if ("test".equals("false")) {
      // DateHelpersTests.getCurrentDateTime("2007-07-01 00:00:00")
      // dateTimeFrom =
      // DateHelpersTests.getCurrentDateTime(getYMInceptionDateTime())
      // Get the past 7 days.
      dateTimeFrom = DateHelpersTests.getCurrentDatePlusMinusDays("yyyy-MM-dd HH:mm:ss", -7);
      dateTimeFrom = DateHelpersTests.getCurrentDateTime(dateTimeFrom);
    }
    dateTimeFrom = DateHelpersTests.getCurrentDateTime("2007-07-01 00:00:00");
    return dateTimeFrom;
  }

  public static List<Map<String, String>> getEventRegistrationIDsMapListAll(String xml)
      throws Throwable {
    List<Map<String, String>> registrationIDMapList = new ArrayList<>();
    final String xPATHROOT = "//RegistrationID";
    StringBuilder stringBuilder = new StringBuilder(xPATHROOT);
    final NodeList registrationIDNodeList = XML.getNodeList(xml, stringBuilder.toString());
    for (int registrationIDIndex = 0;
        registrationIDIndex < registrationIDNodeList.getLength();
        registrationIDIndex++) {
      final Node registrationIDNode = registrationIDNodeList.item(registrationIDIndex);
      final Element registrationIDElement = (Element) registrationIDNode;
      // registrationIDElement.getElementsByTagName("RegistrationID").item(0).getTextContent();
      final String value = registrationIDElement.getTextContent();
      final String isPrimary = registrationIDElement.getAttribute("IsPrimary");
      sysOut("value:[" + value + "], isPrimary:[" + isPrimary + "]");
      Map<String, String> sessionMap = new HashMap<>();
      sessionMap.put("RegistrationID", value);
      sessionMap.put("IsPrimary", isPrimary);
      registrationIDMapList.add(sessionMap);
    }
    return registrationIDMapList;
  }

  public static Results getResults() {
    return results;
  }

  public static void setResults(Results results) {
    YMDataTests.results = results;
  }

  public static YMAPI getYmapi() {
    return ymApi;
  }

  public static YMAPI getYmApi() {
    return ymApi;
  }

  public static void importGoToWebinar() throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList(Arrays.asList())));
    importGoToWebinarData("Events");
    importGoToWebinarData("EventInformation");
    importGoToWebinarData("YMGTWLinks");
    importGoToWebinarData("EventAttendees");
    importGoToWebinarData("EventRegistrationIDs");
    importGoToWebinarData("EventRegistration");
  }

  /**
   * @param tableBackupName
   * @throws Throwable
   */
  public static void importGoToWebinarData(String tableBackupName) throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(
        ParameterHelper.getParameters(
            Arrays.asList(Arrays.asList(tableBackupName, "tableBackupName"))));
    List<StringBuilder> sqlStringBuilderList = new ArrayList<>();
    List<StringBuilder> sqlStringBuilderListTemp = new ArrayList<>();
    switch (tableBackupName) {
      case "Events":
        sqlStringBuilderListTemp.add(importGoToWebinarEvents());
        break;
      case "EventInformation":
        sqlStringBuilderListTemp.add(importGoToWebinarEventInformation());
        break;
      case "YMGTWLinks":
        sqlStringBuilderListTemp.add(importGoToWebinarYMGTWLinks());
        break;
      case "EventAttendees":
        sqlStringBuilderListTemp.add(importGoToWebinarEventAttendees());
        break;
      case "EventRegistrationIDs":
        sqlStringBuilderListTemp.add(importGoToWebinarEventRegistrationIDs());
        break;
      case "EventRegistration":
        sqlStringBuilderListTemp.add(importGoToWebinarEventRegistration());
        break;
      default:
        throw new QAException("Undefined Table:[" + tableBackupName + "]");
    }
    if (sqlStringBuilderListTemp.isEmpty()) {
      return;
    }
    sqlStringBuilderList.add(new StringBuilder(tableBackupName));
    sqlStringBuilderList.addAll(sqlStringBuilderListTemp);
    VivitDataTests.updateTableFromCurrentToPreviousAndInsert(sqlStringBuilderList);
  }

  public static StringBuilder importGoToWebinarEventAttendees() throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList(Arrays.asList())));
    StringBuilder sqlStringBuilder = new StringBuilder();
    List<String> eventAttendeeFileList =
        FSOTests.filesList(VivitFoldersFiles.PATH_API_DATA_YM_EVENT_ATTENDEES);
    for (String filePathName : eventAttendeeFileList) {
      String eventID = filePathName.replace(VivitFoldersFiles.PATH_API_DATA_YM_EVENT_ATTENDEES, "");
      eventID = eventID.replace(IExtension.XML, "");
      String xml = FSOTests.fileReadAll(filePathName);
      Document document = XML.createDocument(xml);
      // //Events.Event.Attendees.Get/Attendees/Attendee
      NodeList attendeeNodeList = document.getElementsByTagName("Attendee");
      sysOut(
          "filePathName:["
              + filePathName
              + "], eventID:["
              + eventID
              + "], attendeeNodeList.getLength():["
              + attendeeNodeList.getLength()
              + "], xml:["
              + Constants.NEWLINE
              + xml
              + "]");
      if (attendeeNodeList.getLength() > 0) {
        for (int attendeeNodeIndex = 0;
            attendeeNodeIndex < attendeeNodeList.getLength();
            attendeeNodeIndex++) {
          final Node attendeeNode = attendeeNodeList.item(attendeeNodeIndex);
          final Element attendeeElement = (Element) attendeeNode;
          NodeList attendeeChildrenNodeList = attendeeNode.getChildNodes();
          Map<String, String> attendeeMap = EventsNamespace.attendeeWithDataSetMap();
          for (int attendeeChildNodeIndex = 0;
              attendeeChildNodeIndex < attendeeChildrenNodeList.getLength();
              attendeeChildNodeIndex++) {
            Node attendeeChildNode = attendeeChildrenNodeList.item(attendeeChildNodeIndex);
            if (attendeeChildNode.getNodeType() == Node.ELEMENT_NODE) {
              final Element attendeeChildElement = (Element) attendeeChildNode;
              String attendeeNodeName = attendeeChildElement.getNodeName();
              final String attendeeNodeValue =
                  attendeeElement.getElementsByTagName(attendeeNodeName).item(0).getTextContent();
              if (!"DataSet".equals(attendeeNodeName)) {
                attendeeMap.put(attendeeNodeName, attendeeNodeValue);
              } else {
                NodeList dataSetChildrenNodeList = attendeeChildNode.getChildNodes();
                for (int dataSetChildNodeIndex = 0;
                    dataSetChildNodeIndex < dataSetChildrenNodeList.getLength();
                    dataSetChildNodeIndex++) {
                  Node dataSetChildNode = dataSetChildrenNodeList.item(dataSetChildNodeIndex);
                  if (dataSetChildNode.getNodeType() == Node.ELEMENT_NODE) {
                    final Element dataSetChildElement = (Element) dataSetChildNode;
                    String dataSetNodeName = dataSetChildElement.getNodeName();
                    String valueNodeValue =
                        dataSetChildNode.getAttributes().getNamedItem("ExportValue").getNodeValue();
                    attendeeMap.put(dataSetNodeName, valueNodeValue);
                  }
                }
              }
            }
          }
          attendeeMap.put(LABEL_EVENT_ID, eventID);
          sqlStringBuilder =
              SQL.appendStringBuilderSQLInsertRecord(
                  VivitTables.VIVIT_EVENT_ATTENDEES_CURRENT, sqlStringBuilder, attendeeMap, true);
        }
      }
    }
    return sqlStringBuilder;
  }

  public static StringBuilder importGoToWebinarEventInformation() throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList()));
    StringBuilder sqlStringBuilder = new StringBuilder();
    List<String> eventFileList =
        FSOTests.filesList(VivitFoldersFiles.PATH_API_DATA_YM_EVENT_INFORMATION);
    for (String filePathName : eventFileList) {
      String xml = FSOTests.fileReadAll(filePathName);
      for (Map<String, String> sessionMap : getEventInformationMapListAll(xml)) {
        sqlStringBuilder =
            SQL.appendStringBuilderSQLInsertRecord(
                VivitTables.VIVIT_EVENTINFORMATION_CURRENT, sqlStringBuilder, sessionMap, true);
      }
    }
    return sqlStringBuilder;
  }

  public static StringBuilder importGoToWebinarYMGTWLinks() throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList()));
    StringBuilder sqlStringBuilder = new StringBuilder();
    List<String> eventFileList =
        FSOTests.filesList(VivitFoldersFiles.PATH_API_DATA_YM_EVENT_INFORMATION);
    for (String filePathName : eventFileList) {
      String xml = FSOTests.fileReadAll(filePathName);
      for (Map<String, String> sessionMap : getEventInformationMapListAll(xml)) {
        // Add the IDs for YM & GTW
        if (isValidEventGTW(sessionMap)) {
          String eventID = sessionMap.get(LABEL_EVENT_ID);
          String locationName = sessionMap.get("LocationName");
          String gtwID = locationName.replaceAll(getEventLocationGTWSearch(), "");
          if (JavaHelpers.isValidStringNumber(gtwID)) {
            Map<String, String> linkMap = new HashMap<>();
            linkMap.put("YMID", eventID);
            linkMap.put("GTWID", gtwID);
            SQL.appendStringBuilderSQLInsertRecord(
                VivitTables.VIVIT_YMGTW_LINKS_CURRENT, sqlStringBuilder, linkMap, true);
          }
        }
      }
    }
    return sqlStringBuilder;
  }

  public static StringBuilder importGoToWebinarEvents() throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList()));
    StringBuilder sqlStringBuilder = new StringBuilder();
    List<String> eventFileList = FSOTests.filesList(VivitFoldersFiles.PATH_API_DATA_YM_EVENTS);
    for (String filePathName : eventFileList) {
      String xml = FSOTests.fileReadAll(filePathName);
      for (Map<String, String> sessionMap : getEventMapListValid(xml)) {
        sqlStringBuilder =
            SQL.appendStringBuilderSQLInsertRecord(
                VivitTables.VIVIT_EVENTS_CURRENT, sqlStringBuilder, sessionMap, true);
      }
    }
    return sqlStringBuilder;
  }

  public static StringBuilder importGoToWebinarEventRegistration() throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList(Arrays.asList())));
    StringBuilder sqlStringBuilder = new StringBuilder();
    List<String> eventRegistrationFileList =
        FSOTests.filesList(VivitFoldersFiles.PATH_API_DATA_YM_EVENT_REGISTRATION);
    for (String filePathName : eventRegistrationFileList) {
      String xml = FSOTests.fileReadAll(filePathName);
      Document document = XML.createDocument(xml);
      // //Sa.Events.Event.Registration.Get/Registration
      NodeList eventRegistrationNodeList =
          document.getElementsByTagName("Sa.Events.Event.Registration.Get");
      sysOut(
          "filePathName:["
              + filePathName
              + "], eventRegistrationNodeList.getLength():["
              + eventRegistrationNodeList.getLength()
              + "], xml:["
              + Constants.NEWLINE
              + xml
              + "]");
      if (eventRegistrationNodeList.getLength() > 0) {
        for (int eventRegistrationNodeIndex = 0;
            eventRegistrationNodeIndex < eventRegistrationNodeList.getLength();
            eventRegistrationNodeIndex++) {
          final Node eventRegistrationNode =
              eventRegistrationNodeList.item(eventRegistrationNodeIndex);
          final Element eventRegistrationElement = (Element) eventRegistrationNode;
          NodeList eventRegistrationChildrenNodeList = eventRegistrationNode.getChildNodes();
          Map<String, String> eventRegistrationMap =
              EventsNamespace.eventRegistrationWithDataSetAndCustomMap();
          for (int eventRegistrationChildNodeIndex = 0;
              eventRegistrationChildNodeIndex < eventRegistrationChildrenNodeList.getLength();
              eventRegistrationChildNodeIndex++) {
            Node eventRegistrationChildNode =
                eventRegistrationChildrenNodeList.item(eventRegistrationChildNodeIndex);

            if (eventRegistrationChildNode.getNodeType() != Node.ELEMENT_NODE) {
              continue; // Guard clause - skip non-element nodes
            }

            final Element eventRegistrationChildElement = (Element) eventRegistrationChildNode;
            String eventRegistrationNodeName = eventRegistrationChildElement.getNodeName();
            final String eventRegistrationNodeValue =
                eventRegistrationElement
                    .getElementsByTagName(eventRegistrationNodeName)
                    .item(0)
                    .getTextContent();

            if (!"DataSet".equals(eventRegistrationNodeName)) {
              processNonDataSetNode(
                  eventRegistrationNodeName, eventRegistrationNodeValue, eventRegistrationMap);
            } else {
              processDataSetNode(eventRegistrationChildNode, eventRegistrationMap);
            }
          }
          sqlStringBuilder =
              SQL.appendStringBuilderSQLInsertRecord(
                  VivitTables.VIVIT_EVENT_REGISTRATION_CURRENT,
                  sqlStringBuilder,
                  eventRegistrationMap,
                  true);
        }
      }
    }
    LOG.debug("Length: {}", sqlStringBuilder.toString().split(Constants.NEWLINE).length);
    return sqlStringBuilder;
  }

  /**
   * Process non-DataSet nodes in event registration XML. Extracted method to reduce nesting depth.
   */
  private static void processNonDataSetNode(
      String nodeName, String nodeValue, Map<String, String> eventRegistrationMap) {
    if (eventRegistrationMap.containsKey(nodeName)) {
      eventRegistrationMap.put(nodeName, nodeValue);
    } else {
      sysOut("The [" + nodeName + "] item does not exist in the" + " eventRegistrationMap!!!");
    }
  }

  /**
   * Process DataSet child nodes in event registration XML. Extracted method to reduce nesting
   * depth.
   */
  private static void processDataSetNode(
      Node dataSetParentNode, Map<String, String> eventRegistrationMap) {
    NodeList dataSetChildrenNodeList = dataSetParentNode.getChildNodes();
    for (int dataSetChildNodeIndex = 0;
        dataSetChildNodeIndex < dataSetChildrenNodeList.getLength();
        dataSetChildNodeIndex++) {
      Node dataSetChildNode = dataSetChildrenNodeList.item(dataSetChildNodeIndex);

      if (dataSetChildNode.getNodeType() != Node.ELEMENT_NODE) {
        continue; // Guard clause - skip non-element nodes
      }

      final Element dataSetChildElement = (Element) dataSetChildNode;
      String dataSetNodeName = dataSetChildElement.getNodeName();

      if (eventRegistrationMap.containsKey(dataSetNodeName)) {
        String valueNodeValue =
            dataSetChildNode.getAttributes().getNamedItem("ExportValue").getNodeValue();
        eventRegistrationMap.put(dataSetNodeName, valueNodeValue);
      } else {
        sysOut(
            "The ["
                + dataSetNodeName
                + "] item does not exist in the"
                + " eventRegistrationMap!!!");
      }
    }
  }

  public static StringBuilder importGoToWebinarEventRegistrationIDs() throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList(Arrays.asList())));
    StringBuilder sqlStringBuilder = new StringBuilder();
    List<String> eventRegistrationFileList =
        FSOTests.filesList(VivitFoldersFiles.PATH_API_DATA_YM_EVENT_REGISTRATION_IDS);
    for (String filePathName : eventRegistrationFileList) {
      String eventID =
          filePathName.replace(VivitFoldersFiles.PATH_API_DATA_YM_EVENT_REGISTRATION_IDS, "");
      eventID = eventID.replace(IExtension.XML, "");
      String xml = FSOTests.fileReadAll(filePathName);
      List<Map<String, String>> registrationIDMapList = getEventRegistrationIDsMapListAll(xml);
      for (Map<String, String> registrationIDMap : registrationIDMapList) {
        registrationIDMap.put("EventID", eventID);
        sqlStringBuilder =
            SQL.appendStringBuilderSQLInsertRecord(
                VivitTables.VIVIT_EVENT_REGISTRATION_IDS_CURRENT,
                sqlStringBuilder,
                registrationIDMap,
                true);
      }
    }
    LOG.debug("Length: {}", sqlStringBuilder.toString().split(Constants.NEWLINE).length);
    return sqlStringBuilder;
  }

  /**
   * @param dateString
   * @return
   */
  public static String getDateReFormatFromStringDate(String dateString) {
    Date date = null;
    List<String> dateFormatsList = getDateFormatsList();
    final SimpleDateFormat simpleDateFormatTo = new SimpleDateFormat(FORMAT_DATE_WEBINAR);
    for (String formatFrom : dateFormatsList) {
      try {
        final SimpleDateFormat simpleDateFormatFrom = new SimpleDateFormat(formatFrom);
        date = simpleDateFormatFrom.parse(dateString);
        return simpleDateFormatTo.format(date);
      } catch (final ParseException e) {
        // Try next format in list
        continue;
      }
    }
    return null;
  }

  public static List<String> getDateFormatsList() {
    List<String> dateFormatsList = new ArrayList<>();
    dateFormatsList.add("EEEE, MMMM d, yyyy");
    dateFormatsList.add("yyyy-MM-dd HH:mm:ss");
    dateFormatsList.add("MMMM d, yyyy");
    dateFormatsList.add(DateHelpersTests.FORMAT_US_STANDARD_DATE);
    return dateFormatsList;
  }

  /**
   * @param sessionMap
   * @return
   * @throws Throwable
   */
  public static boolean isValidEventGTW(Map<String, String> sessionMap) throws Throwable {
    boolean status = false;
    String locationName = sessionMap.get("LocationName");
    sysOut("locationName:[" + locationName + "]");
    if (!JavaHelpers.hasValue(locationName)) {
      return false;
    }
    if (locationName.startsWith(getEventLocationGTWSearch())) {
      status = true;
    }
    return status;
  }

  /**
   * @param eventMap
   * @return
   * @throws Throwable
   */
  public static boolean isValidEventYM(Map<String, String> eventMap) throws Throwable {
    boolean status = false;
    String eventName = eventMap.get("EventName");
    String eventDate = eventMap.get("EventDate");
    eventDate = getDateReFormatFromStringDate(eventDate);
    if (JavaHelpers.hasValue(eventName) && JavaHelpers.hasValue(eventDate)) {
      if (!eventName.startsWith(getEventNameSearch())) {
        return false;
      }
      String currentDate = DateHelpersTests.getCurrentDateTime(FORMAT_DATE_WEBINAR);
      Date dateCurrent = new SimpleDateFormat(FORMAT_DATE_WEBINAR).parse(currentDate);
      Date dateEvent = new SimpleDateFormat(FORMAT_DATE_WEBINAR).parse(eventDate);
      // If the EventDate is >= to the current date.
      // Update the EventDate with the correct format.
      if (dateEvent.compareTo(dateCurrent) >= 0) {
        status = true;
        // eventMap.put("EventDate", eventDate);
      }
    }
    return status;
  }

  /**
   * @param filePathName
   * @param apiNamespace
   * @param tableName
   * @return
   * @throws Throwable
   */
  public static List<StringBuilder> namespaceImport(
      String filePathName, String apiNamespace, String tableName) throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    LOG.debug(
        "{}",
        ParameterHelper.getParameters(
            Arrays.asList(
                Arrays.asList(VivitDataTests.LABEL_FILE_PATH_NAME, filePathName),
                Arrays.asList(VivitDataTests.LABEL_API_NAMESPACE, apiNamespace),
                Arrays.asList(VivitDataTests.LABEL_TABLE_NAME, tableName))));
    String statusName = "namespaceImport-" + apiNamespace + "." + tableName;
    List<StringBuilder> sqlStringBuilderList = new ArrayList<>();
    if (VivitDataTests.successFileStatus(statusName, true)) {
      return sqlStringBuilderList;
    }
    JDBC jdbc = new JDBC("", VivitDataTests.DATABASE_DEFINITION);
    List<String> headingsExpectedList = jdbc.getFieldNamesList(tableName);
    jdbc.close();
    StringBuilder sqlStringBuilder = new StringBuilder();
    final int recordStart = 0;
    final int recordLimitInsert = 25000;
    final int recordLimitDebug = Integer.valueOf(recordLimitInsert / 10);
    try {
      final Map<String, String> headingsExpectedMap = new HashMap<>();
      for (final String heading : headingsExpectedList) {
        headingsExpectedMap.put(heading, "");
      }
      try (Reader reader = new FileReader(filePathName)) {
        // Note: .build() is deprecated in Commons CSV 1.14.1+ but still required by the library
        // The Commons CSV library has not provided a non-deprecated alternative yet.
        // Using CSVParser.parse() with the builder pattern is the recommended approach.
        // See:
        // https://commons.apache.org/proper/commons-csv/apidocs/org/apache/commons/csv/CSVFormat.Builder.html
        @SuppressWarnings("deprecation")
        CSVFormat format = CSVFormat.RFC4180.builder().setHeader().build();
        Iterable<CSVRecord> records = CSVParser.parse(reader, format);
        boolean headingsMapped = false;
        Map<String, String> headingsCSVMap = null;
        sqlStringBuilder = new StringBuilder();
        int recordNumber = 0;
        List<String> headingsExpectedMissingList = new ArrayList<>();
        List<String> headingsCSVMissingList;
        for (final CSVRecord record : records) {
          // final int recordNumber = (int) record.getRecordNumber()
          recordNumber++;
          if (recordNumber >= (recordStart + 1)) {
            if (!headingsMapped) {
              headingsCSVMap = record.toMap();
              final List<String> headingsCSVList =
                  Convert.fromKeySetToList(headingsCSVMap.keySet());
              headingsExpectedList = new ArrayList<>(headingsExpectedList);
              headingsExpectedList.removeAll(VivitDataTests.getDatabaseOnlyFields());
              headingsExpectedList.sort(null);
              headingsCSVList.sort(null);
              sysOut("headingsCSVList:[" + headingsCSVList + "]");
              sysOut("headingsExpectedList:[" + headingsExpectedList + "]");
              if (!headingsCSVList.equals(headingsExpectedList)) {
                headingsExpectedMissingList = new ArrayList<>(headingsExpectedList);
                headingsCSVMissingList = new ArrayList<>(headingsCSVList);
                headingsExpectedMissingList.removeAll(headingsCSVList);
                headingsCSVMissingList.removeAll(headingsExpectedList);
                sysOut("headingsExpectedMissingList:" + headingsCSVMissingList.toString());
                sysOut("headingsCSVMissingList:" + headingsExpectedMissingList.toString());
                if (!headingsExpectedMissingList.isEmpty()
                    && !removeExtraFields(apiNamespace, tableName, headingsExpectedMissingList)) {
                  Assertions.fail(
                      "Expected headings (headingsExpectedMissingList["
                          + headingsExpectedMissingList.toString()
                          + "]) do not match CSV headings and could not be"
                          + " removed from tableName["
                          + tableName
                          + "]");
                }
                if (!headingsCSVMissingList.isEmpty()
                    && !addMissingFields(apiNamespace, tableName, headingsCSVMissingList)) {
                  Assertions.fail(
                      "CSV headings (headingsCSVMissingList["
                          + headingsCSVMissingList.toString()
                          + "]) do not match Expected DB headings and could"
                          + " not be added to tableName["
                          + tableName
                          + "]");
                }
              }
              for (final String key : headingsCSVMap.keySet()) {
                sysOut("heading:[" + key + "]");
              }
              headingsMapped = true;
            }
            final String webSiteMemberId = record.get(VivitDataTests.LABEL_WEB_SITE_MEMBER_ID);
            final Map<String, String> mapMember = new HashMap<>();
            mapMember.put(LABEL_RECORD_NUMBER, String.valueOf(recordNumber));
            mapMember.put(LABEL_RECORD_COMPLETE, "1");
            mapMember.put(VivitDataTests.LABEL_WEB_SITE_MEMBER_ID, webSiteMemberId);
            for (final String field : headingsExpectedList) {
              try {
                if (!VivitDataTests.getDatabaseOnlyFields().contains(field)
                    && !headingsExpectedMissingList.contains(field)) {
                  mapMember.put(field, record.get(field));
                }
              } catch (final Exception e) {
                sysOut("Field Not Found:[" + field + "]");
                mapMember.put(LABEL_RECORD_COMPLETE, "0");
              }
            }
            sqlStringBuilder =
                SQL.appendStringBuilderSQLInsertRecord(
                    tableName, sqlStringBuilder, mapMember, true);
            if (recordNumber % recordLimitDebug == 0) {
              sysOut(JavaHelpers.getCurrentMethodName() + "-Records:[" + recordNumber + "]");
            }
            if (recordNumber % recordLimitInsert == 0) {
              sqlStringBuilderList.add(sqlStringBuilder);
              sqlStringBuilder = new StringBuilder();
            }
          }
        }
        sqlStringBuilderList.add(sqlStringBuilder);
        VivitDataTests.successFileCreate(statusName);
      }
    } catch (final Exception e) {
      sysOut(e);
    }
    return sqlStringBuilderList;
  }

  /**
   * @param dateTimeFrom
   * @throws Throwable
   */
  public static void pullFromYM(String dateTimeFrom) throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList(Arrays.asList())));
    if (!JavaHelpers.hasValue(dateTimeFrom)) {
      dateTimeFrom = getInceptionDateTime();
    }
    boolean success = false;
    int attempt = 0;
    final int attemptsMax = 10;
    do {
      try {
        attempt++;
        LOG.debug("{}", JavaHelpers.getCurrentMethodName() + " attempt " + attempt);
        if (!FSOTests.fileExists(VivitFoldersFiles.DATA_YMAPI_DATA_MEMBERS)) {
          // Export Members from YMAPI
          exportMembers(dateTimeFrom);
        }
        if (!FSOTests.fileExists(VivitFoldersFiles.DATA_YMAPI_DATA_MEMBER_GROUPS)) {
          // Export MemberGroups from YMAPI
          exportMemberGroups(dateTimeFrom);
        }
        exportGTW(true);
        success = true;
      } catch (Exception e) {
        LOG.error(
            "{}",
            QAException.ERROR + JavaHelpers.getCurrentClassMethodName() + ":Pulling data from YM.");
        if (attempt > attemptsMax) {
          throw new QAException(QAException.ERROR + "[" + e.getMessage() + "]", e);
        }
      }
    } while (!success);
    if (getYmapi() != null) {
      Map<String, String> resultsMap = getYmapi().getSessionNamespace().abandon();
      sysOut(VivitDataTests.LABEL_RESULTS_MAP + resultsMap.toString() + "]");
    }
  }

  public static void pushToDB() throws Throwable {
    importGoToWebinar();
    importYMCSV();
  }

  public static void importYMCSV() throws Throwable {
    List<String> apiNamespaceListTrue =
        Arrays.asList(
            "Sa_ExportNamespace.exportDataMembers", "Sa_ExportNamespace.exportDataMemberGroups");
    List<String> apiNamespaceListFalse = Arrays.asList("MemberProducts");
    List<String> apiNamespaceListAll = new ArrayList<>();
    apiNamespaceListAll.addAll(apiNamespaceListTrue);
    apiNamespaceListAll.addAll(apiNamespaceListFalse);
    StringBuilder sqlStringBuilder = new StringBuilder();
    // Re-Create the Previous Member Table.
    sqlStringBuilder.append(JDBCConstants.DROP_TABLE);
    sqlStringBuilder.append("if exists ");
    sqlStringBuilder.append("[" + VivitTables.DOM_VIVIT_MEMBERS_PREVIOUS + "];");
    SQL.executeVivit(
        LABEL_DROP_TABLE,
        VivitTables.DOM_VIVIT_MEMBERS_PREVIOUS + "-" + VivitTables.DOM_VIVIT_MEMBERS_PREVIOUS,
        sqlStringBuilder);
    sqlStringBuilder = new StringBuilder();
    sqlStringBuilder.append(JDBCConstants.CREATE_TABLE);
    sqlStringBuilder.append("[" + VivitTables.DOM_VIVIT_MEMBERS_PREVIOUS + "] ");
    sqlStringBuilder.append(JDBCConstants.AS);
    sqlStringBuilder.append(JDBCConstants.SELECT_ALL_FROM);
    sqlStringBuilder.append("[" + VivitTables.DOM_VIVIT_MEMBERS + "];");
    SQL.executeVivit(
        LABEL_CREATE_TABLE,
        VivitTables.DOM_VIVIT_MEMBERS_PREVIOUS + "-" + VivitTables.DOM_VIVIT_MEMBERS_PREVIOUS,
        sqlStringBuilder);
    for (int apiNamespaceIndex = 0;
        apiNamespaceIndex < apiNamespaceListAll.size();
        apiNamespaceIndex++) {
      String apiNamespace = apiNamespaceListAll.get(apiNamespaceIndex);
      if (apiNamespaceListTrue.contains(apiNamespace)) {
        if (apiNamespaceIndex == 0) {
          sqlStringBuilder = new StringBuilder();
          sqlStringBuilder.append(
              JDBCConstants.DELETE_FROM + "[" + VivitTables.VIVIT_DATABASE_CHANGES + "];");
          SQL.executeVivit(LABEL_DELETE_FROM, VivitTables.VIVIT_DATABASE_CHANGES, sqlStringBuilder);
        }
        sqlStringBuilder = new StringBuilder();
        sqlStringBuilder.append(
            JDBCConstants.DELETE_FROM + "[" + VivitTables.VIVIT_DATABASE_CHANGES + "] ");
        sqlStringBuilder.append(JDBCConstants.WHERE + "[ApiMethod]='" + apiNamespace + "' ");
        sqlStringBuilder.append(
            JDBCConstants.AND
                + "["
                + VivitDataTests.LABEL_TABLE_NAME
                + "] = '"
                + VivitTables.DOM_VIVIT_MEMBERS
                + "';");
        sqlStringBuilder.append(Constants.NEWLINE);
        SQL.executeVivit(
            LABEL_DELETE_FROM,
            VivitTables.VIVIT_DATABASE_CHANGES + "-" + apiNamespace,
            sqlStringBuilder);
      }
      List<StringBuilder> sqlStringBuilderList = new ArrayList<>();
      sqlStringBuilder = new StringBuilder();
      switch (apiNamespace) {
        case "Sa_ExportNamespace.exportDataMembers":
          sqlStringBuilder.append(
              JDBCConstants.DELETE_FROM
                  + "["
                  + VivitTables.DOM_VIVIT_MEMBERS
                  + "];"
                  + Constants.NEWLINE);
          SQL.executeVivit(LABEL_DELETE_FROM, VivitTables.DOM_VIVIT_MEMBERS, sqlStringBuilder);
          sqlStringBuilderList.addAll(
              namespaceImport(
                  VivitFoldersFiles.DATA_YMAPI_DATA_MEMBERS,
                  apiNamespace,
                  VivitTables.DOM_VIVIT_MEMBERS));
          break;
        case "Sa_ExportNamespace.exportDataMemberGroups":
          sqlStringBuilder.append(
              JDBCConstants.DELETE_FROM
                  + "["
                  + VivitTables.DOM_VIVIT_MEMBER_GROUPS
                  + "];"
                  + Constants.NEWLINE);
          SQL.executeVivit(
              LABEL_DELETE_FROM, VivitTables.DOM_VIVIT_MEMBER_GROUPS, sqlStringBuilder);
          sqlStringBuilderList.addAll(
              namespaceImport(
                  VivitFoldersFiles.DATA_YMAPI_DATA_MEMBER_GROUPS,
                  apiNamespace,
                  VivitTables.DOM_VIVIT_MEMBER_GROUPS));
          break;
        case "MemberProducts":
          sqlStringBuilder.append(
              JDBCConstants.DELETE_FROM
                  + "["
                  + VivitTables.DOM_VIVIT_MEMBER_PRODUCTS
                  + "];"
                  + Constants.NEWLINE);
          SQL.executeVivit(
              LABEL_DELETE_FROM, VivitTables.DOM_VIVIT_MEMBER_PRODUCTS, sqlStringBuilder);
          sqlStringBuilderList.addAll(updateMemberProducts());
          break;
        default:
          throw new QAException("Undefined Namespace:[" + apiNamespace + "]");
      }
      for (StringBuilder stringBuilder : sqlStringBuilderList) {
        SQL.executeVivit(LABEL_INSERT_INTO, apiNamespace, stringBuilder);
      }
    }
  }

  /**
   * @param apiNamespace
   * @param tableName
   * @param fieldsRemoveList
   * @return
   * @throws Throwable
   */
  public static boolean removeExtraFields(
      String apiNamespace, String tableName, List<String> fieldsRemoveList) throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(
        ParameterHelper.getParameters(
            Arrays.asList(
                Arrays.asList(VivitDataTests.LABEL_API_NAMESPACE, apiNamespace),
                Arrays.asList(VivitDataTests.LABEL_TABLE_NAME, tableName),
                Arrays.asList("fieldsRemoveList", fieldsRemoveList))));
    Map<String, String> fieldNameMapTemp;
    try {
      JDBC jdbc = new JDBC("", VivitDataTests.DATABASE_DEFINITION);
      fieldNameMapTemp = VivitDataTests.getDatabaseUpdatedFields();
      jdbc.dropFieldsFromTableSQLite(tableName, fieldsRemoveList);
    } catch (final Exception e) {
      sysOut(e);
      return false;
    }
    StringBuilder sqlStringBuilder = new StringBuilder();
    for (final String fieldName : fieldsRemoveList) {
      sysOut(
          "Removed apiNamespace["
              + apiNamespace
              + "], table["
              + tableName
              + "], fieldName["
              + fieldName
              + "]");
      Map<String, String> fieldNameMap = fieldNameMapTemp;
      fieldNameMap.put("ApiMethod", apiNamespace);
      fieldNameMap.put("TableName", tableName);
      fieldNameMap.put("FieldName", fieldName);
      fieldNameMap.put("Action", VivitDataTests.LABEL_FIELD_REMOVED);
      sqlStringBuilder =
          SQL.appendStringBuilderSQLInsertRecord(
              VivitTables.VIVIT_DATABASE_CHANGES, sqlStringBuilder, fieldNameMap);
    }
    if (!"".equals(sqlStringBuilder.toString())) {
      SQL.executeVivit(LABEL_INSERT_INTO, JavaHelpers.getCurrentMethodName(), sqlStringBuilder);
    }
    return true;
  }

  public static void setEventLocationGTWSearch(String eventLocationGTWSearch) {
    YMDataTests.eventLocationGTWSearch = eventLocationGTWSearch;
  }

  public static void setEventNameSearch(String eventNameSearch) {
    YMDataTests.eventNameSearch = eventNameSearch;
  }

  public static void setEventsMax(int eventsMax) {
    YMDataTests.eventsMax = eventsMax;
  }

  public static void setYmApi(YMAPI ymApi) {
    YMDataTests.ymApi = ymApi;
  }

  public static List<StringBuilder> updateMemberProducts() throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    sysOut(ParameterHelper.getParameters(Arrays.asList(Arrays.asList())));
    List<StringBuilder> sqlStringBuilderList = new ArrayList<>();
    String statusName = "updateMemberProducts";
    if (VivitDataTests.successFileExists(statusName)) {
      return sqlStringBuilderList;
    }
    JDBC jdbc = new JDBC("", VivitDataTests.DATABASE_DEFINITION);
    StringBuilder sqlStringBuilder;
    final List<String> delimitedFieldList =
        Arrays.asList(
            ("Micro_Focus_Product_Centers;Micro_Focus_Products_Used;Big_Data_Software_Products_Used;"
                    + "Micro_Focus_Backup_&_Governance_Products_Used;Micro_Focus_ITOM_Products_Used;"
                    + "Micro_Focus_Application_Delivery_Products_Used")
                .split(Constants.DELIMETER_LIST)); // Other_Micro_Focus_Products_Used
    for (final String delimitedField : delimitedFieldList) {
      sqlStringBuilder = new StringBuilder();
      sqlStringBuilder.append(
          JDBCConstants.SELECT
              + "["
              + VivitDataTests.LABEL_WEB_SITE_MEMBER_ID
              + "],["
              + delimitedField
              + "] ");
      sqlStringBuilder.append(JDBCConstants.FROM + "[" + VivitTables.DOM_VIVIT_MEMBERS + "] ");
      sqlStringBuilder.append(JDBCConstants.WHERE + "[" + delimitedField + "]!=''");
      final String sql = sqlStringBuilder.toString();
      final String results = jdbc.queryResults(sql, Constants.DELIMETER_LIST, false);
      sqlStringBuilder = new StringBuilder();
      final String[] records = results.split(Constants.NEWLINE);
      for (final String record : records) {
        final String[] fields = record.split(Constants.DELIMETER_LIST);
        final String webSiteMemberID = fields[0];
        final String productList = fields[1];
        final String[] products = productList.split(Constants.BACKSLASH + "|");
        for (final String product : products) {
          final Map<String, String> mapProduct = new HashMap<>();
          mapProduct.put("ProductLine", delimitedField);
          mapProduct.put(VivitDataTests.LABEL_WEB_SITE_MEMBER_ID, webSiteMemberID);
          mapProduct.put("Product", product.trim());
          sqlStringBuilder =
              SQL.appendStringBuilderSQLInsertRecord(
                  VivitTables.DOM_VIVIT_MEMBER_PRODUCTS, sqlStringBuilder, mapProduct, true);
        }
      }
      sqlStringBuilderList.add(sqlStringBuilder);
    }
    jdbc.close();
    VivitDataTests.successFileCreate(statusName);
    return sqlStringBuilderList;
  }

  public static void stubAttendeeUpdateMe() throws Throwable {
    String otherday =
        DateHelpersTests.getCurrentDatePlusMinusDays(
            DateHelpersTests.FORMAT_YYYY_MM_DD_COMPACT, -1);
    String today = DateHelpersTests.getCurrentDateTime(DateHelpersTests.FORMAT_YYYY_MM_DD_COMPACT);
    String filePathNameSource =
        VivitFoldersFiles.PATH_API_DATA_YM_EVENT_ATTENDEES.replace(today, otherday)
            + EVENTID_TEST
            + IExtension.XML;
    String filePathNameDestination =
        VivitFoldersFiles.PATH_API_DATA_YM_EVENT_ATTENDEES + EVENTID_TEST + IExtension.XML;
    FSOTests.fileWrite(filePathNameDestination, FSOTests.fileReadAll(filePathNameSource), false);
  }

  /**
   * @param dateTimeFrom
   * @throws Throwable
   */
  public static void update(String dateTimeFrom) throws Throwable {
    pullFromYM(dateTimeFrom);
    pushToDB();
    exportYMGTWTables();
  }

  @Test
  public void testApiMethod() throws Throwable {
    if (getYmApi() == null) {
      setYmApi(new YMAPI());
    }
    Map<String, String> apiMap = new HashMap<>();
    // getYmApi().getEventsNamespace().eventGet(Integer.parseInt(EVENTID_TEST));
    // 1209865
    // apiMap = getYmApi().getEventsNamespace().eventGet(1207192);
    // apiMap =
    // getYmApi().getEventsNamespace().eventAttendeesGet(Integer.parseInt(EVENTID_TEST));
    // apiMap =
    // getYmApi().getSaEventsNamespace().eventRegistrationsGetIDs(Integer.parseInt(EVENTID_TEST),
    // null);
    // apiMap =
    // getYmApi().getSaEventsNamespace().eventRegistrationGet("6D507875-6CDC-4E26-ACA4-50C302106A76",
    // "");
    apiMap =
        getYmApi()
            .getSaEventsNamespace()
            .eventRegistrationGet("A53E0C69-6554-49C5-87A1-EED72921F115", "");
    String xml = apiMap.get("xml");
    getYmApi().getSessionNamespace().abandon();
    xml = XML.formatPretty(xml);
    // FSOTests.fileWrite(VivitFoldersFiles.PATH_DATA_TODAY + "test.xml", xml,
    // false);
    FSOTests.fileWrite("C:\\Temp\\test.xml", xml, false);
  }

  @Test
  public void verification() throws Throwable {
    // YourMembershipResponse yourMembership_Response = new
    // UnmarshallEventResponse()
    // yourMembership_Response.getResults().getResultTotal();
    //
    // C:\Workspace\Data\Vivit\Data\20190316\YM\Events\1.xml
    String fileName = "C:\\Workspace\\Data\\Vivit\\Data\\20190316\\YM\\Events\\1.xml";
    final UnmarshallYourMembershipResponse unmarshallYourMembershipResponse =
        new UnmarshallYourMembershipResponse();
    final YourMembershipResponse yourMembershipResponse =
        unmarshallYourMembershipResponse.getFromFile(fileName);
    sysOut(yourMembershipResponse.getErrCode());
    Results results = yourMembershipResponse.getResults();
    int eventCount = results.getResultTotal();
    sysOut("eventCount:[" + eventCount + "]");
  }

  @Test
  public void testMethod() throws Throwable {
    // VivitDataTests.createReportHTMLBillableHours(false, true);
    // exportGTW(true);
    // exportGTWEventRegistrationIDs();
    // exportGTWEventRegistration();
    // VivitDataTests.getGoToWebinarEvents();
    // VivitDataTests.getGoToWebinarEventAttendees();
    // pullFromYM(null);
    // exportGTW(true);
    // pushToDB();
    // importGoToWebinar();
    // importGoToWebinarEvents();
    // importGoToWebinarEventInformation();
    // importGoToWebinarYMGTWLinks();
    // importGoToWebinarEventAttendees();
    // importGoToWebinarEventRegistrationIDs();
    // importGoToWebinarEventRegistration();
    // importYMCSV();
    // VivitDataTests.wrapUp(true);
    // VivitDataTests.createReports(false, true);
    // VivitDataTests.createReportHTMLAutomation(false);
    // VivitDataTests.createReportHTMLBillableHours(false, true);
    // VivitDataTests.createReportHTMLTreasurer(false, true);
    // VivitDataTests.cleanUp();
    // VivitDataTests.finalizeTest();
  }
}
