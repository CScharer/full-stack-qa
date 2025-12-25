package com.cjs.qa.utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;

public class Dates {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(Dates.class));

  public String addDays(String date, String noOfDays) {
    String sDate = date;
    final Calendar cal = Calendar.getInstance();
    final SimpleDateFormat df = new SimpleDateFormat("M/d/yyyy");
    try {
      final Date d1 = df.parse(date);
      cal.setTime(d1);
      cal.add(Calendar.DATE, Integer.parseInt(noOfDays));
      sDate = df.format(cal.getTime());
    } catch (final Exception ex) {
      LOG.error("Error getting the date parsed with string number of days");
    }
    return sDate;
  }

  public String addDays(String date, int noOfDays) {
    String sDate = date;
    final Calendar cal = Calendar.getInstance();
    final SimpleDateFormat df = new SimpleDateFormat("M/D/yyyy");
    try {
      final Date d1 = df.parse(date);
      cal.setTime(d1);
      cal.add(Calendar.DATE, noOfDays);
      sDate = df.format(cal.getTime());
    } catch (final Exception ex) {
      LOG.error("Error getting date parsed with int number of days");
    }
    return sDate;
  }

  public String addOneMonth(String date) {
    String temp = date;
    final int loc = temp.indexOf("/");
    String month = temp.substring(0, loc);
    temp = temp.substring(loc + 1, temp.length());
    final String day = temp.substring(0, loc);
    String year = temp.substring(loc + 1, temp.length());

    int iMonth = Integer.parseInt(month);
    if (iMonth <= 11) {
      iMonth = iMonth + 1;
      month = Integer.toString(iMonth);
      if (month.length() < 2) {
        month = "0" + month;
      }
    } else {
      iMonth = 1;
      month = Integer.toString(iMonth);
      final int iYear = Integer.parseInt(year) + 1;
      year = Integer.toString(iYear);
    }
    if (month.length() == 1) {
      month = "0" + month;
    }

    date = month + "/" + day + "/" + year;

    return date;
  }

  public String addOneYear(String date) {
    if (!date.isEmpty()) {
      String temp = date;
      int loc = temp.indexOf("/");
      String monthDay = temp.substring(0, loc) + "/";
      temp = temp.substring(loc + 1, temp.length());
      loc = temp.indexOf("/");
      monthDay = monthDay + temp.substring(0, loc) + "/";
      temp = temp.substring(loc + 1, temp.length());

      int year = Integer.parseInt(temp);
      year = year + 1;
      monthDay = monthDay + Integer.toString(year);
      return monthDay;
    }
    return date;
  }

  public String addSixMonths(String date) {
    String temp = date;
    int loc = temp.indexOf("/");
    String month = temp.substring(0, loc);
    temp = temp.substring(loc + 1, temp.length());
    loc = temp.indexOf("/");
    final String day = temp.substring(0, loc);
    String year = temp.substring(loc + 1, temp.length());

    int iMonth = Integer.parseInt(month);
    if (iMonth <= 6) {
      iMonth = iMonth + 6;
      month = Integer.toString(iMonth);
      if (month.length() < 2) {
        month = "0" + month;
      }
    } else {
      iMonth = iMonth - 6;
      month = Integer.toString(iMonth);
      final int iYear = Integer.parseInt(year) + 1;
      year = Integer.toString(iYear);
    }
    if (month.length() == 1) {
      month = "0" + month;
    }

    date = month + "/" + day + "/" + year;
    return date;
  }
}
