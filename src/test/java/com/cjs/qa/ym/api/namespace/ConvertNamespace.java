package com.cjs.qa.ym.api.namespace;

import java.util.Map;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;
import com.cjs.qa.ym.api.services.YMAPI;
import com.cjs.qa.ym.api.services.YMService;

public class ConvertNamespace extends YMService {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(ConvertNamespace.class));

  public Map<String, String> toEasternTime(String localTime, String localGmtBias) throws Throwable {
    // Converts the given local time to current Eastern Time, factoring in
    // adjustments for Daylight Savings Time.
    LOG.debug(
        "{} - {}{}{}",
        JavaHelpers.getCurrentClassMethodName(),
        URL_YM_API_DOC,
        "Convert_ToEasternTime",
        IExtension.HTM);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(SessionNamespace.getSessionID());
    stringBuilder.append(
        Constants.nlTab(1, 1)
            + YMAPI.LABEL_CALL_METHOD_PREFIX
            + Constants.QUOTE_DOUBLE
            + "Convert.ToEasternTime"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append(Constants.nlTab(1, 2) + "<LocalTime>" + localTime + "</LocalTime>");
    stringBuilder.append(
        Constants.nlTab(1, 2) + "<LocalGmtBias>" + localGmtBias + "</LocalGmtBias>");
    stringBuilder.append(Constants.nlTab(1, 1) + YMAPI.LABEL_CALL_METHOD_SUFFIX);
    return getAPIXMLResponse("POST", stringBuilder.toString());
  }
}
