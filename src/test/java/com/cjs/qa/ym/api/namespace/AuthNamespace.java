package com.cjs.qa.ym.api.namespace;

import java.util.Map;

import com.cjs.qa.utilities.Constants;
import com.cjs.qa.ym.api.services.YMAPI;
import com.cjs.qa.ym.api.services.YMService;

public class AuthNamespace extends YMService {

  public Map<String, String> authenticate(String userID, String password) throws Throwable {
    StringBuilder stringBuilder = new StringBuilder();
    // stringBuilder.append(getSAPasscode()+SessionNamespace.getSessionID())
    stringBuilder.append(SessionNamespace.getSessionID());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Auth.Authenticate"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Username>" + userID + "</Username>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Password>" + password + "</Password>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> createToken(
      String userID, String password, boolean persist, String retUrl) throws Throwable {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(getSAPasscode() + SessionNamespace.getSessionID());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Auth.Authenticate"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Username>" + userID + "</Username>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Password>" + password + "</Password>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<Persist>" + persist + "</Persist>");
    stringBuilder.append(Constants.nlTab(1, 2) + "<RetUrl>" + retUrl + "</RetUrl>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }
}
