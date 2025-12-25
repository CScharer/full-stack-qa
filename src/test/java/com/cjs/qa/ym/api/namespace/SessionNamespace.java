package com.cjs.qa.ym.api.namespace;

import java.util.Map;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.core.security.EPasswords;
import com.cjs.qa.utilities.CJSConstants;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.utilities.XML;
import com.cjs.qa.ym.api.services.YMAPI;
import com.cjs.qa.ym.api.services.YMService;

public class SessionNamespace extends YMService {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SessionNamespace.class));

  public SessionNamespace() throws Throwable {
    setSessionID();
  }

  public static final int TIMEOUT_SESSION = 20;
  private static String sessionID = null;
  private static long timeSessionStart = 0;

  // public static int callID = 0

  public static String getSessionID() throws Throwable {
    final long timeSessionCurrent = System.currentTimeMillis(); // 20 minutes
    final long sessionLimit = TIMEOUT_SESSION * 60 * Constants.MILLISECONDS;
    final long timeSessionElapsed = timeSessionCurrent - timeSessionStart;
    LOG.debug(
        "timeSessionStart:[{}], timeSessionCurrent:[{}], timeSessionElapsed:[{}]",
        timeSessionStart,
        timeSessionCurrent,
        timeSessionElapsed);
    if (timeSessionElapsed >= sessionLimit || timeSessionStart == 0) {
      setSessionID();
    }
    return Constants.nlTab(1, 1) + "<SessionID>" + sessionID + "</SessionID>";
  }

  public static void setSessionID() throws Throwable {
    timeSessionStart = System.currentTimeMillis();
    // SessionNamespace.callID = 0
    resetCallID();
    final Map<String, String> mapResponse = create(true);
    final String sessionID = XML.getTag(mapResponse.get("xml"), "SessionID");
    SessionNamespace.sessionID = sessionID;
    AuthNamespace authNamespace = new AuthNamespace();
    authNamespace.authenticate(CJSConstants.USERID_VIVIT, EPasswords.VIVIT.getValue());
  }

  public Map<String, String> abandon() throws Throwable {
    // Destroys an API session, thus preventing any further calls against
    // it. Returns true (1) if the session was alive and successfully
    // abandoned, else returns false (0).
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Session_Abandon",
        IExtension.HTM);
    final String apiRequest =
        getSessionID()
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Session.Abandon"
            + Constants.QUOTE_DOUBLE
            + ">"
            + YMAPI.LABEL_CALL_METHOD_SUFFIX;
    return getAPIXMLResponse("POST", apiRequest);
  }

  public static Map<String, String> create(boolean refreshSession) throws Throwable {
    // Creates an API session and returns the <SessionID> which must be
    // supplied as a parameter to all subsequent API calls. This method must
    // be called before calling any other method.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Session_Create",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Session.Create"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> ping() throws Throwable {
    // When called at intervals of less than 20 minutes, this method acts as
    // an API session keep-alive. Returns true (1) if the session is still
    // alive, else returns false (0).
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Session_Ping",
        IExtension.HTM);
    final String apiRequest =
        getSessionID()
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Session.Ping"
            + Constants.QUOTE_DOUBLE
            + ">"
            + YMAPI.LABEL_CALL_METHOD_SUFFIX;
    return getAPIXMLResponse("POST", apiRequest);
  }
}
