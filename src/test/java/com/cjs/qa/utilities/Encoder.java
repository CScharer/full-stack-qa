package com.cjs.qa.utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Encoder {

  private static final Logger log = LogManager.getLogger(Encoder.class);
  private static final String characters26 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final String characters52 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  private String characterSet;

  public Encoder(String base) {
    setCharacterSet(base);
  }

  public String getCharacterSet() {
    return characterSet;
  }

  private void setCharacterSet(String base) {
    // Java 17: Switch expression
    setCharacterSet(
        switch (base) {
          case "52" -> characters52;
          default -> characters26;
        });
  }

  public void main(String[] args) {
    // This is designed to be able to use the UPPERCASE and lower-case
    // letters of the alphabet, but
    // we should only use the UPPERCASE unless we know for sure what we need
    // is not case sensitive.
    final String cCHARS = "";
    final StringBuilder stringBuilder = new StringBuilder();
    final DateFormat formatNano = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
    Date date = new Date();
    String dateTimeStamp = formatNano.format(date);
    for (int loop = 1; loop <= 1; loop++) {
      final String valueEncoded = getEncodedValue(cCHARS);
      final String valueDecoded = getDecodedValue(valueEncoded, cCHARS);
      stringBuilder.append(valueDecoded + " - " + valueEncoded + Constants.NL);
    }
    date = new Date();
    stringBuilder.append(dateTimeStamp + Constants.NL);
    dateTimeStamp = formatNano.format(date);
    stringBuilder.append(dateTimeStamp);
    log.debug(stringBuilder.toString());
  }

  public String getDecodedValue(String sValue, String base) {
    int decodedDate = 0;
    int decodedTime = 0;
    int decodedNano = 0;
    String encoded = "";
    String decoded = "";
    setCharacterSet(base);
    encoded = sValue;
    // Java 17: Switch expression with block syntax for multiple statements
    switch (base) {
      case "52" -> {
        decodedDate = decode(encoded.substring(0, 5));
        decodedTime = decode(encoded.substring(5, 9));
        decodedNano = decode(encoded.substring(9));
      }
      default -> {
        decodedDate = decode(encoded.substring(0, 6));
        decodedTime = decode(encoded.substring(6, 11));
        decodedNano = decode(encoded.substring(11));
      }
    }
    decoded = String.valueOf(decodedDate);
    decoded += String.valueOf(decodedTime);
    decoded += String.valueOf(decodedNano);
    return decoded;
  }

  public String getEncodedValue(String sBase) {
    try {
      // We need to wait 1 Nanosecond so we make sure to get a unique ID.
      // TimeUnit.MILLISECONDS.sleep(1);
      TimeUnit.NANOSECONDS.sleep(1);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }
    setCharacterSet(sBase);
    final DateFormat formatDate = new SimpleDateFormat("yyyyMMdd");
    final DateFormat formatTime = new SimpleDateFormat("1HHmmss");
    final DateFormat formatNano = new SimpleDateFormat("1SSS");
    final Date date = new Date();
    final String dateString = formatDate.format(date);
    // Prepend a 1 as the time may be AM and would lose a character.
    final String timeString = formatTime.format(date);
    // Prepend a 1 as the Nanoseconds could have leading zeros which would
    // be lost characters.
    final String nanoString = formatNano.format(date);
    final int dateInt = Integer.valueOf(dateString);
    final int timeInt = Integer.valueOf(timeString);
    final int nanoInt = Integer.valueOf(nanoString);
    String dateEncoded = encode(dateInt);
    dateEncoded += encode(timeInt);
    dateEncoded += encode(nanoInt);
    return dateEncoded;
  }

  public String encode(int iNumber) {
    String text = "";
    final int textLength = (int) Math.ceil(Math.log(iNumber) / Math.log(characterSet.length()));
    for (int loop = 0; loop < textLength; loop++) {
      // loop goes to log base characterSet.length() of iNumber
      // (using change of base formula)
      text += characterSet.charAt(iNumber % characterSet.length());
      iNumber /= characterSet.length();
    }
    return text;
  }

  public int decode(String sText) {
    int number = 0;
    final int textLength = sText.length();
    for (int loop = 0; loop < textLength; loop++) {
      // Explicit cast to int to avoid implicit cast warning (double from Math.pow to int)
      number =
          (int)
              (number
                  + characterSet.indexOf(sText.charAt(0)) * Math.pow(characterSet.length(), loop));
      sText = sText.substring(1);
    }
    return number;
  }
}
