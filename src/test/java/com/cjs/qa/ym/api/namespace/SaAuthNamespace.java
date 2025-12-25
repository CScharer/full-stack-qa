package com.cjs.qa.ym.api.namespace;

import java.util.Map;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.ym.api.services.YMAPI;
import com.cjs.qa.ym.api.services.YMService;

public class SaAuthNamespace extends YMService {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SaAuthNamespace.class));

  public Map<String, String> authenticate(String username, String password, String passwordHash)
      throws Throwable {
    // Authenticates a member's username and password and binds them to the
    // current API session. Returns the new member's <ID> and <WebsiteID>.
    // The returned <WebsiteID> represents the numeric identifier used by
    // the YourMembership.com application for navigation purposes. It may be
    // used to provide direct navigation to a member's profile, photo
    // gallery, personal blog, etc.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Auth_Authenticate",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Auth.Authenticate"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Username>" + username + "</Username>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Password>" + password + "</Password>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<PasswordHash>" + passwordHash + "</PasswordHash>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }
}
