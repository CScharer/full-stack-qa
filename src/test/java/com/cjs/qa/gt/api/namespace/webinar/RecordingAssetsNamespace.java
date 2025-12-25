package com.cjs.qa.gt.api.namespace.webinar;

import java.util.Map;

import com.cjs.qa.gt.api.services.GTWebinarServiceTests;

public class RecordingAssetsNamespace extends GTWebinarServiceTests {

  public Map<String, String> searchForCompletedRecordingAssets(String credentials)
      throws Throwable {
    // /recordingassets/search
    String url = API_GT_BASE + "/recordingassets/search";
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    return getAPIJSONResponse(credentials, "POST", stringBuilder.toString(), url);
  }
}
