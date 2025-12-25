package com.cjs.qa.ym.api.namespace;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cjs.qa.core.QAException;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.utilities.XML;
import com.cjs.qa.ym.api.services.YMAPI;
import com.cjs.qa.ym.api.services.YMService;

public class SaEventsNamespace extends YMService {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SaEventsNamespace.class));

  public Map<String, String> eventMap() throws Throwable {
    final List<String> fields =
        Arrays.asList(
            ("Name;EventID;StartDateTime;EndDateTime;LocationName;LocationAddress1;LocationAddress2;"
                    + "LocationCity;LocationLocation;LocationPostalCode;LocationCountry;ContactName;"
                    + "ContactEmail;ContactPhone;ShortDescription;Active")
                .split(Constants.DELIMETER_LIST));
    final Map<String, String> eventMap = new HashMap<>();
    for (final String field : fields) {
      eventMap.put(field, "");
    }
    return eventMap;
  }

  public Map<String, String> sessionMap() throws Throwable {
    final List<String> fields =
        Arrays.asList(
            "ID;Name;Description;Presenter;SeatLimit;StartDateTime;EndDateTime;CreatedDate;UpdatedDate"
                .split(Constants.DELIMETER_LIST));
    final Map<String, String> sessionMap = new HashMap<>();
    for (final String field : fields) {
      sessionMap.put(field, "");
    }
    return sessionMap;
  }

  public StringBuilder getEventSQL(String sXML) throws Throwable, Exception {
    final StringBuilder sqlStringBuilder = new StringBuilder();
    final List<String> arrayEventIDs = XML.getNodes(sXML, "Sa.Events.All.GetIDs", "EventID");
    if (!arrayEventIDs.isEmpty()) {
      Collections.sort(arrayEventIDs);
      LOG.debug("arrayEventIDs:[{}]", arrayEventIDs.toString());
      for (int index = 0; index < (arrayEventIDs.size() - 1); index++) {
        final int eventID = Integer.parseInt(arrayEventIDs.get(index));
        // eventID = 395201
        Map<String, String> mapResults = eventGet(eventID);
        sXML = mapResults.get("xml");
        //
        final String xpathRoot = "//Sa.Events.Event.Get";
        String xpath = xpathRoot;
        final NodeList nodesEvent = XML.getNodeList(sXML, xpath);
        for (int indexEvent = 0; indexEvent < nodesEvent.getLength(); indexEvent++) {
          final Map<String, String> eventMap = eventMap();
          for (final String key : eventMap.keySet()) {
            final Node nodeEvent = nodesEvent.item(indexEvent);
            final Element elementEvent = (Element) nodeEvent;
            final String value = elementEvent.getElementsByTagName(key).item(0).getTextContent();
            eventMap.put(key, value);
          }
          LOG.debug("eventMap:[{}]", eventMap.toString());
          xpath = xpathRoot + "/SessionGroups/SessionGroup";
          final NodeList nodesSessionGroup = XML.getNodeList(sXML, xpath);
          for (int indexSessionGroup = 0;
              indexSessionGroup < nodesSessionGroup.getLength();
              indexSessionGroup++) {
            final Node nodeSessionGroup = nodesSessionGroup.item(indexSessionGroup);
            final Element elementSessionGroup = (Element) nodeSessionGroup;
            final String groupName = elementSessionGroup.getAttribute("Name");
            final String groupID = elementSessionGroup.getAttribute("ID");
            xpath =
                xpathRoot
                    + "/SessionGroups/SessionGroup"
                    + "[@ID='"
                    + groupID
                    + "']"
                    + "[@Name='"
                    + groupName
                    + "']";
            final NodeList nodesGroupSession = XML.getNodeList(sXML, xpath);
            for (int indexGroupSession = 0;
                indexGroupSession < nodesGroupSession.getLength();
                indexGroupSession++) {
              final Map<String, String> sessionMap = sessionMap();
              for (final String key : sessionMap.keySet()) {
                final Node nodeGroupSession = nodesGroupSession.item(indexGroupSession);
                final Element elementGroupSession = (Element) nodeGroupSession;
                final String value =
                    elementGroupSession.getElementsByTagName(key).item(0).getTextContent();
                sessionMap.put(key, value);
              }
              LOG.debug("sessionMap:[{}]", sessionMap.toString());
            }
          }
          xpath = xpathRoot + "/UngroupedSessions/Session";
          final NodeList nodesUngroupedSession = XML.getNodeList(sXML, xpath);
          for (int indexUngroupedSession = 0;
              indexUngroupedSession < nodesUngroupedSession.getLength();
              indexUngroupedSession++) {
            final Map<String, String> sessionMap = sessionMap();
            for (final String key : sessionMap.keySet()) {
              final Node nodeUngroupedSession = nodesUngroupedSession.item(indexUngroupedSession);
              final Element elementUngroupedSession = (Element) nodeUngroupedSession;
              final String value =
                  elementUngroupedSession.getElementsByTagName(key).item(0).getTextContent();
              sessionMap.put(key, value);
            }
            LOG.debug("sessionMap:[{}]", sessionMap.toString());
          }
        }
        mapResults = eventRegistrationsGetIDs(eventID, null);
        sXML = mapResults.get("xml");
        final List<String> arrayRegistrationIDs =
            XML.getNodes(sXML, "Sa.Events.Event.Registrations.GetIDs", "RegistrationID");
        LOG.debug("arrayRegistrationIDs:[{}]", arrayRegistrationIDs.toString());
      }
    }
    return sqlStringBuilder;
  }

  public Map<String, String> allGetIDs(
      String startDate, String endDate, String name, String status, String lastModifiedDate)
      throws Throwable {
    // Returns a list of Events for the community optionally filtered by
    // date or event name.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Events_All_GetIDs",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Events.All.GetIDs"
            + Constants.QUOTE_DOUBLE
            + ">");
    if (startDate != null) {
      stringBuilder.append(Constants.nlTab(1, 2) + "<StartDate>" + startDate + "</StartDate>");
    }
    if (endDate != null) {
      stringBuilder.append(Constants.nlTab(1, 2) + "<EndDate>" + endDate + "</EndDate>");
    }
    if (name != null) {
      stringBuilder.append(Constants.nlTab(1, 2) + "<Name>" + name + "</Name>");
    }
    if (status != null) {
      switch (status) {
        case "Inactive":
          status = "0";
          break;
        case "Active":
          status = "1";
          break;
        default:
          break;
      }
      stringBuilder.append(Constants.nlTab(1, 2) + "<Status>" + status + "</Status>");
    }
    if (lastModifiedDate != null) {
      stringBuilder.append(
          Constants.nlTab(1, 2) + "<LastModifiedDate>" + lastModifiedDate + "</LastModifiedDate>");
    }
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> eventGet(int eventID) throws Throwable {
    // Returns details about the provided Event ID, including session
    // information.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Events_Event_Get",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Events.Event.Get"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<EventID>" + eventID + "</EventID>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> eventRegistrationGet(String registrationID, String badgeNumber)
      throws Throwable {
    // Returns Event Registration details for the provided Event and Event
    // Registration ID. If the Event Registration contains a related Custom
    // Form, the form data will be included in the <DataSet> element as it
    // is stored in our database.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Events_Event_Registration_Get",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Events.Event.Registration.Get"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<RegistrationID>" + registrationID + "</RegistrationID>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<BadgeNumber>" + badgeNumber + "</BadgeNumber>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> eventRegistrationsGetIDs(int eventID, String status) throws Throwable {
    // Returns a list of Registration IDs for the specified Event ID.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Events_Event_Registrations_GetIDs",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Events.Event.Registrations.GetIDs"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<EventID>" + eventID + "</EventID>");
    // -1 = Cancelled,0 = Incomplete,1 = Open, 2 = Processed
    if (status != null) {
      switch (status) {
        case "Cancelled":
          status = "-1";
          break;
        case "Incomplete":
          status = "0";
          break;
        case "Open":
          status = "1";
          break;
        case "Processed":
          status = "2";
          break;
        default:
          throw new QAException(
              JavaHelpers.getCurrentMethodName() + " - Invalid status filter [" + status + "]");
      }
      stringBuilder.append(Constants.nlTab(1, 2) + "<Status>" + status + "</Status>");
    }
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }
}
