package com.cjs.qa.gt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cjs.qa.core.Environment;
import com.cjs.qa.gt.api.namespace.webinar.AuthNamespace;
import com.cjs.qa.gt.api.namespace.webinar.WebinarsNamespace;
import com.cjs.qa.gt.api.services.GTWebinarServiceTests;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.utilities.XML;

public class GTWebinarDataTests extends Environment {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(GTWebinarDataTests.class));

  public static final String EVENTID_TEST = "4085145690110151939";

  public static List<Map<String, String>> getEventMapListAll(String xml) throws Throwable {
    List<Map<String, String>> eventMapList = new ArrayList<>();
    final String xPATHROOT = "//Events.Event.Get";
    StringBuilder stringBuilder = new StringBuilder(xPATHROOT);
    final NodeList eventNodeList = XML.getNodeList(xml, stringBuilder.toString());
    for (int eventIndex = 0; eventIndex < eventNodeList.getLength(); eventIndex++) {
      final Map<String, String> sessionMap = new HashMap<>();
      // WebinarsNamespace.eventMap()
      for (final String key : sessionMap.keySet()) {
        final Node eventNode = eventNodeList.item(eventIndex);
        final Element eventElement = (Element) eventNode;
        final String value = eventElement.getElementsByTagName(key).item(0).getTextContent();
        sessionMap.put(key, value);
      }
      eventMapList.add(sessionMap);
    }
    return eventMapList;
  }

  public static void update() throws Throwable {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
    pullFromGTWebinar(null);
    pushToDB();
    pushToGTWebinar();
  }

  private static void pushToGTWebinar() {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
  }

  private static void pushToDB() {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
  }

  // Parameter reserved for future implementation - currently unused but kept for API compatibility
  @SuppressWarnings("unused")
  private static void pullFromGTWebinar(String dateTimeFrom) {
    sysOut(Constants.CLASS_METHOD_DEBUG + JavaHelpers.getCurrentClassMethodDebugName() + "]");
  }

  @Test
  public void testAuth() throws Throwable {
    AuthNamespace authNamespace = new AuthNamespace();
    authNamespace.oauth(
        GTWebinarServiceTests.getApiConsumerKey()
            + ":"
            + GTWebinarServiceTests.getApiConsumerSecret());
    LOG.debug("AccessToken:[{}]", GTWebinarServiceTests.getAccessToken());
    WebinarsNamespace webinarsNamespace = new WebinarsNamespace();
    webinarsNamespace.getAllWebinarsForAnAccount("", GTWebinarServiceTests.getAccountKey());
  }
}
