package com.cjs.qa.utilities;

import org.json.JSONObject;

public final class JSON {

  private JSON() {}

  /**
   * @param json - The json string value.
   * @param tabSpaces - The number of spaces in each tab/delimiter.
   * @return - Formatted json string.
   */
  public static String formatPretty(String json, final int tabSpaces) {
    final JSONObject jsonObject = new JSONObject(json);
    return jsonObject.toString(tabSpaces);
  }
}
