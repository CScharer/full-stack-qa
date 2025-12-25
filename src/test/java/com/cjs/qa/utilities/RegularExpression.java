package com.cjs.qa.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RegularExpression {

  private RegularExpression() {}

  public static final String EMAIL =
      "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

  public static boolean isValidEmail(String value) {
    if (value == null) {
      return false;
    }
    Pattern pattern = Pattern.compile(EMAIL);
    Matcher matcher = pattern.matcher(value);
    return matcher.matches();
  }

  public static String getOnlyAlpha(String value) {
    Pattern pattern = Pattern.compile("[^a-z A-Z]");
    Matcher matcher = pattern.matcher(value);
    return matcher.replaceAll("");
  }

  public static String getOnlyAlphaNumericCharacters(String value) {
    Pattern pattern = Pattern.compile("[^0-9a-zA-Z_]");
    Matcher matcher = pattern.matcher(value);
    return matcher.replaceAll("");
  }

  public static String getOnlyNumeric(String value) {
    Pattern pattern = Pattern.compile("[^0-9]");
    Matcher matcher = pattern.matcher(value);
    return matcher.replaceAll("");
  }

  public static String removeCharacters(String value, String remove) {
    Pattern pattern = Pattern.compile(remove);
    Matcher matcher = pattern.matcher(value);
    return matcher.replaceAll("");
  }
}
