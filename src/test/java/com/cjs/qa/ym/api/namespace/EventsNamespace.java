package com.cjs.qa.ym.api.namespace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.ym.api.services.YMAPI;
import com.cjs.qa.ym.api.services.YMService;

public class EventsNamespace extends YMService {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(EventsNamespace.class));

  public static final int PAGES_MAX = 100;

  public static List<String> attendeeList() throws Throwable {
    return Arrays.asList(
        "RegisterID;RsvpID;DateRegister;LastName;FirstName;WebsiteID;ID;RsvpResponse"
            .split(Constants.DELIMETER_LIST));
  }

  public static List<String> customList() throws Throwable {
    return Arrays.asList(
        "Custom_JobLevel;Custom_JobFunction;Custom_MayHPEemailyouofferssupportdatesandeventnews"
            .split(Constants.DELIMETER_LIST));
  }

  public static List<String> dataSetList() throws Throwable {
    return Arrays.asList(
        ("strEmail;strWorkTitle;strEmployerName;strAddress1;strAddress2;strCity;strLocation;"
                + "strPostalCode;strCountry;strPhone;strRSVPComments;Custom_AdditionalComments")
            .split(Constants.DELIMETER_LIST));
  }

  public static List<String> eventList() throws Throwable {
    return Arrays.asList("EventName;EventID;EventDate".split(Constants.DELIMETER_LIST));
  }

  public static List<String> eventInformationList() throws Throwable {
    return Arrays.asList(
        ("Name;EventID;StartDateTime;EndDateTime;LocationName;LocationAddress1;LocationAddress2;"
                + "LocationCity;LocationLocation;LocationPostalCode;LocationCountry;ContactName;"
                + "ContactEmail;ContactPhone;ShortDescription")
            .split(Constants.DELIMETER_LIST));
  }

  public static List<String> eventRegistrationList() throws Throwable {
    return Arrays.asList(
        ("Status;EventName;FirstName;LastName;BadgeNumber;RegistrationID;DateRegistered;Attended;"
                + "OrderID;MemberID;InvoiceID;IsPrimaryRegistrant;PrimaryRegistrantRegistrationID;"
                + "PrimaryRegistrantMemberID;AttendeeType")
            .split(Constants.DELIMETER_LIST));
  }

  public static int getColumnIndex(List<String> list, String columnName) {
    for (int index = 0; index < list.size(); index++) {
      if (list.get(index).equals(columnName)) {
        return index;
      }
    }
    LOG.debug("Could NOT find Column Name [" + columnName + "]");
    return -1;
  }

  public static Map<String, String> attendeeMap() throws Throwable {
    final List<String> fields = attendeeList();
    final Map<String, String> eventMap = new HashMap<>();
    for (final String field : fields) {
      eventMap.put(field, "");
    }
    return eventMap;
  }

  public static Map<String, String> attendeeWithDataSetMap() throws Throwable {
    List<String> fields = new ArrayList<>(attendeeList());
    fields.addAll(dataSetList());
    final Map<String, String> eventMap = new HashMap<>();
    for (final String field : fields) {
      eventMap.put(field, "");
    }
    return eventMap;
  }

  public static Map<String, String> dataSetMap() throws Throwable {
    final List<String> fields = dataSetList();
    final Map<String, String> eventMap = new HashMap<>();
    for (final String field : fields) {
      eventMap.put(field, "");
    }
    return eventMap;
  }

  public static Map<String, String> eventRegistrationWithDataSetAndCustomMap() throws Throwable {
    List<String> fields = new ArrayList<>(eventRegistrationList());
    fields.addAll(dataSetList());
    fields.addAll(customList());
    final Map<String, String> eventMap = new HashMap<>();
    for (final String field : fields) {
      eventMap.put(field, "");
    }
    return eventMap;
  }

  public static Map<String, String> eventMap() throws Throwable {
    final List<String> fields = eventList();
    final Map<String, String> eventMap = new HashMap<>();
    for (final String field : fields) {
      eventMap.put(field, "");
    }
    return eventMap;
  }

  public static Map<String, String> eventInformationMap() throws Throwable {
    final List<String> fields = eventInformationList();
    final Map<String, String> eventMap = new HashMap<>();
    for (final String field : fields) {
      eventMap.put(field, "");
    }
    return eventMap;
  }

  public Map<String, String> allSearch(String searchText, int pageSize, long startRecord)
      throws Throwable {
    // Returns a list of Community Events based on the supplied search term.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Events_All_Search",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Events.All.Search"
            + Constants.QUOTE_DOUBLE
            + ">");
    if (searchText != null) {
      stringBuilder.append(Constants.nlTab(1, 2) + "<SearchText>" + searchText + "</SearchText>");
    }
    if (pageSize > 0) {
      stringBuilder.append(Constants.nlTab(1, 2) + "<PageSize>" + pageSize + "</PageSize>");
    }
    stringBuilder.append(Constants.nlTab(1, 2) + "<StartRecord>" + startRecord + "</StartRecord>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> eventAttendeesGet(int eventID) throws Throwable {
    // Returns a list of all Attendees for the specified event including
    // both Registrations and RSVPs. If the Event Registration contains a
    // related Custom Form, the form data will be included in the <DataSet>
    // element as it is stored in our database. Records for authenticated
    // members also include the <ID> element to cross reference the Member's
    // data.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Events_Event_Attendees_Get",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Events.Event.Attendees.Get"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<EventID>" + eventID + "</EventID>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> eventGet(int eventID) throws Throwable {
    // Returns details about the provided Event ID.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Events_Event_Get",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Events.Event.Get"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<EventID>" + eventID + "</EventID>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }
}
