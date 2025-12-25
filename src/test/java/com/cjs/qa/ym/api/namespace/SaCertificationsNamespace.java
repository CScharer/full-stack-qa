package com.cjs.qa.ym.api.namespace;

import java.util.Map;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.ym.api.services.YMAPI;
import com.cjs.qa.ym.api.services.YMService;

public class SaCertificationsNamespace extends YMService {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(SaCertificationsNamespace.class));

  public Map<String, String> allGet(boolean isActive) throws Throwable {
    // Return a list of all certification records for the community.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Certifications_All_Get",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Certifications.All.Get"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<IsActive>" + isActive + "</IsActive>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }

  public Map<String, String> creditTypesAllGet() throws Throwable {
    // Return a list of all certifications credit types for the community.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Sa_Certifications_CreditTypes_All_Get",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID() + getSAPasscode());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Sa.Certifications.CreditTypes.All.Get"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }
}
