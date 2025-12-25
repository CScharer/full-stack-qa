package com.cjs.qa.rest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.core.QAException;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.JavaHelpers;

public final class REST {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(REST.class));

  private REST() {
    // Utility class - prevent instantiation
  }

  public static Map<String, String> getAPIJSONResponse(
      String credentials, String requestMethod, String apiRequest, String url) throws QAException {
    final Map<String, String> map = new HashMap<>();
    LOG.debug(
        "getAPIJSONResponse Parameters:\n\trequestMethod:[{}]\n\turl:[{}]\n\tapiRequest[{}]",
        requestMethod,
        url,
        apiRequest);
    String responseCode = "-1";
    map.put("responseCode", responseCode);
    HttpURLConnection httpURLConnection = null;
    final StringBuilder stringBuilderJSON = new StringBuilder();
    String line = "";
    try {
      final URL oURL = URI.create(url).toURL();
      httpURLConnection = (HttpURLConnection) oURL.openConnection();
      httpURLConnection.setDoInput(true);
      httpURLConnection.setDoOutput(true);
      httpURLConnection.setInstanceFollowRedirects(false);
      if (!requestMethod.isEmpty()) {
        httpURLConnection.setRequestMethod(requestMethod);
      }
      if (JavaHelpers.hasValue(credentials)) {
        final String encoding =
            Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        httpURLConnection.setRequestProperty("Authorization", "Basic " + encoding);
      }
      // httpURLConnection.setRequestProperty("X-Atlassian-Token:",
      // "no-check")
      httpURLConnection.setRequestProperty("Content-Type", "application/json");
      httpURLConnection.setRequestProperty("Accept", "application/json");
      httpURLConnection.setRequestProperty("charset", StandardCharsets.UTF_8.toString());
      // httpURLConnection.setRequestProperty("charset",
      // StandardCharsets.US_ASCII.toString());
      if (JavaHelpers.hasValue(apiRequest)) {
        httpURLConnection.setRequestProperty(
            "Content-Length", "" + Integer.toString(apiRequest.getBytes().length));
      } else {
        httpURLConnection.setRequestProperty("X-Requested-With", "Curl");
        httpURLConnection.setRequestProperty("Content-Length", "0");
      }
      httpURLConnection.setUseCaches(false);
      //
      // final PrintStream printStream = new
      // PrintStream(httpURLConnection.getOutputStream());
      // printStream.print(apiRequest);
      // printStream.close();
      //
      try (DataOutputStream dataOutputStream =
          new DataOutputStream(httpURLConnection.getOutputStream())) {
        if (JavaHelpers.hasValue(apiRequest)) {
          dataOutputStream.writeBytes(apiRequest);
        }
        dataOutputStream.flush();
        responseCode = String.valueOf(httpURLConnection.getResponseCode());
        map.put("responseCode", responseCode);
        final String responseMessage = String.valueOf(httpURLConnection.getResponseMessage());
        map.put("responseMessage", responseMessage);
        // if (responseCode == HttpURLConnection.HTTP_OK)
        // {
        // final BufferedReader bufferedReader = new BufferedReader(new
        // InputStreamReader(httpURLConnection.getInputStream(),
        // StandardCharsets.UTF_8));
        try (BufferedReader bufferedReader =
            new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
          while ((line = bufferedReader.readLine()) != null) {
            stringBuilderJSON.append(line);
          }
        }
        // }
      } finally {
        httpURLConnection.disconnect();
      }
    } catch (final Exception e) {
      throw new QAException(e);
    }
    map.put("json", stringBuilderJSON.toString());
    return map;
  }
}
