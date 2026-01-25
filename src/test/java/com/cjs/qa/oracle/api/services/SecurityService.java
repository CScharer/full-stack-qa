package com.cjs.qa.oracle.api.services;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;

import com.cjs.qa.core.Environment;
import com.cjs.qa.core.api.WebService;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.JavaHelpers;

public class SecurityService extends WebService {

  private final String baseAPISecurity =
      "https://SecurityAPI."
          + OracleConstants.API_BASE
          + ".net/"
          + Environment.getEnvironment()
          + "/"
          + OracleConstants.API_ORG_VERSION_MAJOR
          + "/"
          + OracleConstants.API_ORG_VERSION_MINOR;

  /**
   * @param ssoUserTokenId
   * @param organizationName
   * @param userIDs
   * @return
   */
  public Map<String, String> createClaim(
      String ssoUserTokenId, String organizationName, String userIDs) {
    // Create Claim
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String apiRequest =
        "{Name: "
            + Constants.QUOTE_DOUBLE
            + "organization/"
            + organizationName
            + "/edit"
            + Constants.QUOTE_DOUBLE
            + ",UserIds: ["
            + userIDs
            + "]}";
    final String requestURL = baseAPISecurity + "/SharedEntity/Claim/";
    final String json = getAPIJSONResponse("POST", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assertions.assertTrue(json.contains(OracleConstants.API_JSON_SUCCESS), map.toString());
    return map;
  }
}
