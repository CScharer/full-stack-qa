package com.cjs.qa.oracle.api.services;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

import com.cjs.qa.core.api.WebService;
import com.cjs.qa.utilities.IExtension;
import com.cjs.qa.utilities.JavaHelpers;

public class ExampleService extends WebService {

  private static final String BASE_API_EXAMPLE = "http://jsonplaceholder.typicode" + IExtension.COM;

  /**
   * @param ssoUserTokenId
   * @return
   */
  public Map<String, String> getExample(String ssoUserTokenId) {
    // Create Claim
    final Map<String, String> map = new HashMap<>();
    map.put("API_Method", JavaHelpers.getCurrentMethodName().toString());
    final String apiRequest = "{}";
    final String requestURL = BASE_API_EXAMPLE + "/posts/1";
    final String json = getAPIJSONResponse("GET", requestURL, apiRequest, ssoUserTokenId);
    map.put("JSON", json);
    Assert.assertTrue(map.toString(), json.contains(OracleConstants.API_JSON_SUCCESS));
    return map;
  }
}
