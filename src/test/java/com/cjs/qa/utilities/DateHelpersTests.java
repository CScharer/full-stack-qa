package com.cjs.qa.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.junit.Test;

public class DateHelpersTests {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(DateHelpersTests.class));
  public static final String DELIMETER = "/";
  public static final String FORMAT_DD_MMM_YYYY = "dd-MMM-yyyy";
  public static final String FORMAT_YYYY_MM_DD_COMPACT = "yyyyMMdd";
  public static final String FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
  public static final String FORMAT_EEEE = "EEEE";
  public static final String FORMAT_MMMM = "MMMM";
  public static final String FORMAT_MMM = "MMM";
  public static final String FORMAT_MM = "MM";
  public static final String FORMAT_MM_DD_YY = "MMddyy";
  public static final String FORMAT_M = "M";
  public static final String FORMAT_DD = "dd";
  public static final String FORMAT_D = "d";
  public static final String FORMAT_HH = "HH";
  public static final String FORMAT_H = "H";
  public static final String FORMAT_KK = "KK";
  public static final String FORMAT_KK_LOWER = "kk";
  public static final String FORMAT_MM_LOWER = "mm";
  public static final String FORMAT_M_LOWER = "m";
  public static final String FORMAT_SSS = "SSS";
  public static final String FORMAT_SS = "ss";
  public static final String FORMAT_S = "s";
  public static final String FORMAT_YYYY = "yyyy";
  public static final String FORMAT_YY_MM_DD_HH_MM_SS = "yyMMddHHmmss";
  public static final String FORMAT_YY = "yy";
  public static final String FORMAT_Y = "y";
  public static final String FORMAT_YYMMDD = "yyMMdd";
  public static final String FORMAT_US_STANDARD_DATE =
      FORMAT_MM + DELIMETER + FORMAT_DD + DELIMETER + FORMAT_YYYY;
  public static final String FORMAT_US_STANDARD_TIME =
      FORMAT_HH + ":" + FORMAT_MM_LOWER + ":" + FORMAT_SS;
  public static final String FORMAT_US_STANDARD_DATE_TIME =
      FORMAT_US_STANDARD_DATE + " " + FORMAT_US_STANDARD_TIME;
  public static final String FORMAT_DATE_TIME_STAMP =
      FORMAT_YYYY
          + FORMAT_MM
          + FORMAT_DD
          + "_"
          + FORMAT_HH
          + FORMAT_MM_LOWER
          + FORMAT_SS
          + "."
          + FORMAT_SSS;

  /** Gets the currentDate in the general format: MM/dd/yyyy */
  public static String getCurrentDate() {
    return getCurrentDateTime(FORMAT_US_STANDARD_DATE);
  }

  public static String getCurrentDatePlusMinusDays(String format, Integer days) {
    if (format == null) {
      format = FORMAT_US_STANDARD_DATE;
    }
    final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
    final LocalDateTime now = LocalDateTime.now();
    LocalDateTime then;
    if (days < 0) {
      days = days * -1;
      then = now.minusDays(days);
    } else {
      then = now.plusDays(days);
    }
    return then.format(dateTimeFormatter);
  }

  public static String getDatePlusMinus(
      String date, String changeType, Integer value, String format) {
    try {
      boolean add = true;
      if (date == null) {
        date = getCurrentDateTime(FORMAT_US_STANDARD_DATE_TIME);
      }
      if (value < 0) {
        add = false;
        value = value * -1;
      }
      if (format == null) {
        format = FORMAT_US_STANDARD_DATE_TIME;
      }
      final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
      final int month = Integer.parseInt(date.substring(0, 2));
      final int day = Integer.parseInt(date.substring(3, 5));
      final int year = Integer.parseInt(date.substring(6, 10));
      final int hour = Integer.parseInt(date.substring(11, 13));
      final int minute = Integer.parseInt(date.substring(14, 16));
      final int second = Integer.parseInt(date.substring(17, 19));
      final LocalDateTime now = LocalDateTime.of(year, month, day, hour, minute, second);
      LocalDateTime then = null;
      switch (changeType) {
        case FORMAT_S:
          if (add) {
            then = now.plusSeconds(value);
          } else {
            then = now.minusSeconds(value);
          }
          return then.format(dateTimeFormatter);
        case FORMAT_M_LOWER:
          if (add) {
            then = now.plusMinutes(value);
          } else {
            then = now.minusMinutes(value);
          }
          return then.format(dateTimeFormatter);
        case FORMAT_H:
          if (add) {
            then = now.plusHours(value);
          } else {
            then = now.minusHours(value);
          }
          return then.format(dateTimeFormatter);
        case FORMAT_M:
          if (add) {
            then = now.plusMonths(value);
          } else {
            then = now.minusMonths(value);
          }
          return then.format(dateTimeFormatter);
        case FORMAT_Y:
          if (add) {
            then = now.plusYears(value);
          } else {
            then = now.minusYears(value);
          }
          return then.format(dateTimeFormatter);
        case FORMAT_D:
        default:
          if (add) {
            then = now.plusDays(value);
          } else {
            then = now.minusDays(value);
          }
          return then.format(dateTimeFormatter);
      }
    } catch (final Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Test
  public void getDatePlusMinusTestCase() {
    final String now = getCurrentDateTime(FORMAT_US_STANDARD_DATE_TIME);
    LOG.info("Current date/time: {}", now);
    LOG.info("{} | {}", FORMAT_Y, getDatePlusMinus(now, FORMAT_Y, 1, FORMAT_YYYY));
    LOG.info("{} | {}", FORMAT_M, getDatePlusMinus(now, FORMAT_M, 1, FORMAT_MM));
    LOG.info("{} | {}", FORMAT_D, getDatePlusMinus(now, FORMAT_D, 1, FORMAT_DD));
    LOG.info("{} | {}", FORMAT_H, getDatePlusMinus(now, FORMAT_H, 1, FORMAT_HH));
    LOG.info("{} | {}", FORMAT_M_LOWER, getDatePlusMinus(now, FORMAT_M_LOWER, 1, FORMAT_MM_LOWER));
    LOG.info("{} | {}", FORMAT_S, getDatePlusMinus(now, FORMAT_S, 1, FORMAT_SS));
    LOG.info("{} | {}", FORMAT_Y, getDatePlusMinus(now, FORMAT_Y, -1, FORMAT_YYYY));
    LOG.info("{} | {}", FORMAT_M, getDatePlusMinus(now, FORMAT_M, -1, FORMAT_MM));
    LOG.info("{} | {}", FORMAT_D, getDatePlusMinus(now, FORMAT_D, -1, FORMAT_DD));
    LOG.info("{} | {}", FORMAT_H, getDatePlusMinus(now, FORMAT_H, -1, FORMAT_HH));
    LOG.info("{} | {}", FORMAT_M_LOWER, getDatePlusMinus(now, FORMAT_M_LOWER, -1, FORMAT_MM_LOWER));
    LOG.info("{} | {}", FORMAT_S, getDatePlusMinus(now, FORMAT_S, -1, FORMAT_SS));
  }

  public static String getDatePlusMinusDays(String date, Integer days, String format) {
    if (format == null) {
      format = FORMAT_US_STANDARD_DATE;
    }
    final String[] iDate = date.split(DELIMETER);
    final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
    final LocalDateTime now =
        LocalDateTime.of(
            Integer.valueOf(iDate[2]), Integer.valueOf(iDate[0]), Integer.valueOf(iDate[1]), 0, 0);
    LocalDateTime then;
    if (days < 0) {
      days = days * -1;
      then = now.minusDays(days);
    } else {
      then = now.plusDays(days);
    }
    return then.format(dateTimeFormatter);
  }

  /**
   * Gets the current date and time in the following format: MM/dd/yyyy HH:MM:SSS Used for logging
   * date time during execution.
   */
  public static String getCurrentDateAndTime() {
    return getCurrentDateTime(
        FORMAT_US_STANDARD_DATE + " " + FORMAT_US_STANDARD_TIME + "." + FORMAT_SSS);
  }

  /**
   * Returns the currentDate using a SimpleDateFormat explained in http://docs.
   * oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html
   *
   * @param sDateFormat
   * @return
   */
  public static String getCurrentDateTime(String format) {
    final Calendar calendar = Calendar.getInstance();
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
    return simpleDateFormat.format(calendar.getTime());
  }

  /**
   * @param format
   * @return
   */
  public static String getCurrentDateTimeStamp() {
    return getCurrentDateTime(FORMAT_DATE_TIME_STAMP);
  }

  /** Gets the current time in the following format: HH:mm */
  public static String getCurrentTime() {
    return getCurrentDateTime(FORMAT_HH + ":" + FORMAT_MM_LOWER);
  }

  /** Gets the currentDate in the general format: MM/yyyy */
  public static String getCurrentMonthFourDigitYear() {
    return getCurrentDateTime(FORMAT_MM + DELIMETER + FORMAT_YYYY);
  }

  /**
   * Compares two dates with each other in dd-MMM-yyyy format
   *
   * @param date1
   * @param date2
   * @return
   */
  public static int compareTo(String date1, String date2) {
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DD_MMM_YYYY);
    try {
      final Date date1compare = simpleDateFormat.parse(date1);
      final Date date2compare = simpleDateFormat.parse(date2);
      return date1compare.compareTo(date2compare);
    } catch (final ParseException e) {
      e.printStackTrace();
    }
    return -2;
  }

  /**
   * Compares two dates with each other in the given format format
   *
   * @param format
   * @param date1
   * @param date2
   * @return
   */
  public static int compareTo(String format, String date1, String date2) {
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
    try {
      final Date date1compare = simpleDateFormat.parse(date1);
      final Date date2compare = simpleDateFormat.parse(date2);
      return date1compare.compareTo(date2compare);
    } catch (final ParseException e) {
      e.printStackTrace();
    }
    return -2;
  }

  /** Format of yyMMddHHmmss */
  public static String getTimeStamp() {
    return getCurrentDateTime(FORMAT_YY_MM_DD_HH_MM_SS);
  }

  /**
   * @return
   */
  public static String getTimeZoneDisplayName() {
    final TimeZone timeZone = Calendar.getInstance().getTimeZone();
    // (i.e. Moscow Standard Time)
    LOG.debug("Display Name: [{}]", timeZone.getDisplayName());
    // (i.e. Europe/Moscow)
    LOG.debug("ID: [{}]", timeZone.getID());
    return timeZone.getDisplayName();
  }

  /**
   * @return
   */
  public static String getTimeZoneID() {
    final TimeZone timeZone = Calendar.getInstance().getTimeZone();
    // (i.e. Moscow Standard Time)
    LOG.debug("Display Name: [{}]", timeZone.getDisplayName());
    // (i.e. Europe/Moscow)
    LOG.debug("ID: [{}]", timeZone.getID());
    return timeZone.getID();
  }

  public static String getDateFromUnix(String date) {
    final long unixDate = Long.parseLong(date);
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_YYYY_MM_DD_COMPACT);
    return simpleDateFormat.format(unixDate);
  }

  /** Format of dd-MMM-yyyy */
  public static String getCurrentCJSDate() {
    return getCurrentDateTime(FORMAT_DD_MMM_YYYY);
  }

  public static boolean validateCJSDateFormat(String date) {
    final String pattern = FORMAT_DD_MMM_YYYY;
    return validateTimeFormat(date, pattern);
  }

  public static boolean validateDocDateFormat(String date) {
    final String pattern = FORMAT_MMM + " " + FORMAT_DD + ", " + FORMAT_YYYY;
    return validateTimeFormat(date, pattern);
  }

  public static boolean validateMessageDateFormat(String date) {
    final String pattern =
        FORMAT_EEEE
            + ", "
            + FORMAT_DD_MMM_YYYY
            + " "
            + FORMAT_KK
            + ":"
            + FORMAT_MM_LOWER
            + ":"
            + FORMAT_SS
            + " a";
    return validateTimeFormat(date, pattern);
  }

  public static String getCurrentDateAllNumbers() {
    return getCurrentDateTime(FORMAT_MM_DD_YY);
  }

  public static String getCurrentDayOfTheWeek() {
    return getCurrentDateTime(FORMAT_EEEE);
  }

  public static String getCurrentNumberDayOfTheMonth() {
    return getCurrentDateTime(FORMAT_DD);
  }

  public static String getCurrentMonthYear() {
    return getCurrentDateTime(FORMAT_MM + DELIMETER + FORMAT_YY);
  }

  public static String getCurrentMonth() {
    return getCurrentDateTime(FORMAT_MM);
  }

  public static boolean validateDateSignedFormat(String date) {
    final String pattern = FORMAT_DD_MMM_YYYY + " " + FORMAT_HH + ":" + FORMAT_MM + " a";
    return validateTimeFormatLeniently(date, pattern);
  }

  public static boolean validateViewMessagesDateFormat(String date) {
    final String pattern = FORMAT_DD_MMM_YYYY + " " + FORMAT_US_STANDARD_TIME + " a";
    return validateTimeFormatLeniently(date, pattern);
  }

  public static boolean validateViewMessagesDateTimeFormat(String date) {
    final String pattern = FORMAT_US_STANDARD_TIME + " a";
    return validateTimeFormatLeniently(date, pattern);
  }

  public static boolean validateTimeFormatLeniently(String date, String pattern) {
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
    simpleDateFormat.setLenient(true);
    try {
      simpleDateFormat.applyPattern(pattern);
      simpleDateFormat.parse(date);
      return true;
    } catch (final ParseException e) {
      // Parse failed - return false per validation contract
      return false;
    }
  }

  public static boolean validateTimeFormat(String date, String pattern) {
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
    simpleDateFormat.setLenient(false);
    try {
      simpleDateFormat.applyPattern(pattern);
      simpleDateFormat.parse(date);
      return true;
    } catch (final ParseException e) {
      // Parse failed - return false per validation contract
      return false;
    }
  }

  public static boolean validateTimeFormatStrictly(String date, String pattern) {
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
    simpleDateFormat.setLenient(false);
    try {
      simpleDateFormat.applyPattern(pattern);
      return simpleDateFormat.format(simpleDateFormat.parse(date)).equals(date);
    } catch (final ParseException e) {
      // Parse failed - return false per validation contract
      return false;
    }
  }

  public static int stringToMonth(String m) {
    if (m.length() != 3) {
      return -1;
    }
    final SimpleDateFormat inputFormat = new SimpleDateFormat(FORMAT_MMM);
    final Calendar cal = Calendar.getInstance();
    try {
      cal.setTime(inputFormat.parse(m));
    } catch (final ParseException e) {
      return -1;
    }
    final SimpleDateFormat outputFormat = new SimpleDateFormat(FORMAT_M); // 01-12
    return Integer.valueOf(outputFormat.format(cal.getTime()));
  }

  public static GregorianCalendar parseDate(String date) {
    // 15-Jul-2012
    final List<String> parts = Arrays.asList(date.split("-"));
    final GregorianCalendar gregorianCalendar = new GregorianCalendar();
    gregorianCalendar.set(
        Integer.parseInt(parts.get(2)),
        stringToMonth(parts.get(1)),
        Integer.parseInt(parts.get(0)));
    return gregorianCalendar;
  }

  public static int getDateDifference(
      GregorianCalendar gregorianCalendar2, GregorianCalendar gregorianCalendar1) {
    return (int)
        ((gregorianCalendar1.getTime().getTime() - gregorianCalendar2.getTime().getTime())
            / (1000 * 60 * 60 * 24));
  }

  /**
   * Returns the date after X days have passed from the current date
   *
   * @param days
   * @return
   */
  public static String getDateAfterXDays(int days) {
    try {
      final String date = getCurrentDateTime(FORMAT_DD_MMM_YYYY);
      final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DD_MMM_YYYY);
      final Calendar c = Calendar.getInstance();
      c.setTime(simpleDateFormat.parse(date));
      c.add(Calendar.DATE, days);
      return simpleDateFormat.format(c.getTime());
    } catch (final ParseException e) {
      e.printStackTrace();
    }
    return "null";
  }

  /**
   * Returns the date after X days have passed from the current date
   *
   * @param days
   * @param format
   * @return
   */
  public static String getDateAfterXDays(int days, String format) {
    try {
      final String date = getCurrentDateTime(FORMAT_DD_MMM_YYYY);
      final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DD_MMM_YYYY);
      final Calendar c = Calendar.getInstance();
      c.setTime(simpleDateFormat.parse(date));
      c.add(Calendar.DATE, days);
      return new SimpleDateFormat(format).format(c.getTime());
    } catch (final ParseException e) {
      e.printStackTrace();
    }
    return "null";
  }

  /**
   * Returns the date after X days have passed from the current date
   *
   * @param days
   * @return
   */
  public static String getDateAfterXDaysForACH(int days) {
    try {
      final String date = getCurrentDateTime(FORMAT_YYMMDD);
      final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_YYMMDD);
      final Calendar c = Calendar.getInstance();
      c.setTime(simpleDateFormat.parse(date));
      c.add(Calendar.DATE, days);
      return simpleDateFormat.format(c.getTime());
    } catch (final ParseException e) {
      e.printStackTrace();
    }
    return "null";
  }

  public static boolean validateVDRFormat(String date) {
    final String pattern = FORMAT_MMM + "-" + FORMAT_YY;
    return validateTimeFormat(date, pattern);
  }

  public static String getDaySuffix(int day) {
    if (day >= 1 && day <= 31) {
      if (day >= 11 && day <= 13) {
        return "th";
      }
      switch (day % 10) {
        case 1:
          return "st";
        case 2:
          return "nd";
        case 3:
          return "rd";
        default:
          return "th";
      }
    }
    return "";
  }

  public static String getDayOfMonthWithoutSuffix() {
    String dayOfMonth = getCurrentNumberDayOfTheMonth();
    if (dayOfMonth.charAt(0) == '0') {
      dayOfMonth = dayOfMonth.substring(1);
    }
    return dayOfMonth;
  }

  public static String getDayOfMonthWithSuffix() {
    String dayOfMonth = getCurrentNumberDayOfTheMonth();
    if (dayOfMonth.charAt(0) == '0') {
      dayOfMonth = dayOfMonth.substring(1);
    }
    return dayOfMonth + getDaySuffix(Integer.parseInt(dayOfMonth));
  }

  // returns the hour 1-24
  public static String getHour() {
    return getCurrentDateTime(FORMAT_KK_LOWER);
  }

  public static String getMinute() {
    return getCurrentDateTime(FORMAT_MM_LOWER);
  }

  public static String getMonthAfterXDays(int days) {
    try {
      final String date = getCurrentDateTime(FORMAT_MMMM);
      final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_MMMM);
      final Calendar c = Calendar.getInstance();
      c.setTime(simpleDateFormat.parse(date));
      c.add(Calendar.DATE, days);
      return simpleDateFormat.format(c.getTime());
    } catch (final ParseException e) {
      e.printStackTrace();
    }
    return "null";
  }

  public static String getYearAfterXDays(int days) {
    try {
      final String date = getCurrentDateTime(FORMAT_DD_MMM_YYYY);
      final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DD_MMM_YYYY);
      final Calendar c = Calendar.getInstance();
      c.setTime(simpleDateFormat.parse(date));
      c.add(Calendar.DATE, days);
      return simpleDateFormat.format(c.getTime()).split("-")[2];
    } catch (final ParseException e) {
      e.printStackTrace();
    }
    return "null";
  }

  public static String firstOfTheMonth() {
    return "01-" + getCurrentDateTime(FORMAT_MMM + "-" + FORMAT_YYYY);
  }

  public static int getDaysInMonth(String date) {
    Calendar calendar = Calendar.getInstance();
    final String[] dateArray = date.split(DELIMETER);
    calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dateArray[1]));
    calendar.set(Calendar.MONTH, Integer.valueOf(dateArray[0]));
    calendar.set(Calendar.YEAR, Integer.valueOf(dateArray[2]));
    // calendar.set(Integer.valueOf(iDate[2]), Integer.valueOf(iDate[0]),
    // Integer.valueOf(iDate[1]))
    final int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    LOG.info("[{}] days in the month of [{}]", daysInMonth, date);
    return daysInMonth;
  }

  public static String endOfTheMonthv2() {
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DD_MMM_YYYY);
    final Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.MONTH, 1);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.add(Calendar.DAY_OF_MONTH, -1);
    return simpleDateFormat.format(calendar.getTime());
  }

  /**
   * Adds one year to a date formatted as 'dd-MMM-yyyy'
   *
   * @param date
   * @return
   */
  public static String addOneYear(String date) {
    try {
      final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DD_MMM_YYYY);
      final Calendar calendar = Calendar.getInstance();
      calendar.setTime(simpleDateFormat.parse(date));
      calendar.add(Calendar.YEAR, 1);
      return simpleDateFormat.format(calendar.getTime());
    } catch (final ParseException e) {
      e.printStackTrace();
    }
    return "null";
  }

  public static String addDays(String date, int numberOfDays) {
    try {
      final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DD_MMM_YYYY);
      final Calendar calendar = Calendar.getInstance();
      calendar.setTime(simpleDateFormat.parse(date));
      calendar.add(Calendar.DAY_OF_YEAR, numberOfDays);
      return simpleDateFormat.format(calendar.getTime());
    } catch (final ParseException e) {
      e.printStackTrace();
    }
    return "null";
  }

  public static String addDays2(String date, int numberOfDays) {
    try {
      final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_US_STANDARD_DATE);
      final Calendar calendar = Calendar.getInstance();
      calendar.setTime(simpleDateFormat.parse(date));
      calendar.add(Calendar.DAY_OF_YEAR, numberOfDays);
      return simpleDateFormat.format(calendar.getTime());
    } catch (final ParseException e) {
      e.printStackTrace();
    }
    return "null";
  }

  public static String addMonths(String date, int numberOfMonths) {
    try {
      final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DD_MMM_YYYY);
      final Calendar calendar = Calendar.getInstance();
      calendar.setTime(simpleDateFormat.parse(date));
      calendar.add(Calendar.MONTH, 1 * numberOfMonths);
      return simpleDateFormat.format(calendar.getTime());
    } catch (final ParseException e) {
      e.printStackTrace();
    }
    return "null";
  }

  public static String getDB2Date() {
    return getCurrentDateTime(FORMAT_YYYY_MM_DD);
  }

  public static boolean validateDB2Date(String date) {
    final String pattern = FORMAT_YYYY_MM_DD;
    return validateTimeFormat(date, pattern);
  }

  public static boolean isWeekday(String date) {
    try {
      final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DD_MMM_YYYY);
      final Calendar calendar = Calendar.getInstance();
      calendar.setTime(simpleDateFormat.parse(date));
      final int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
      if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
        return true;
      }
    } catch (final ParseException e) {
      e.printStackTrace();
    }
    return false;
  }

  public static String getMondayXWeeksFromDate(String date, int x) {
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DD_MMM_YYYY);
    final Calendar calendar = Calendar.getInstance();
    try {
      calendar.setTime(simpleDateFormat.parse(date));
    } catch (final ParseException e) {
      e.printStackTrace();
    }
    final int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
    calendar.add(Calendar.DATE, 7 * x);
    switch (currentDay) {
      case Calendar.MONDAY:
        calendar.add(Calendar.DATE, 7);
        break;
      case Calendar.TUESDAY:
        calendar.add(Calendar.DATE, 6);
        break;
      case Calendar.WEDNESDAY:
        calendar.add(Calendar.DATE, 5);
        break;
      case Calendar.THURSDAY:
        calendar.add(Calendar.DATE, 4);
        break;
      case Calendar.FRIDAY:
        calendar.add(Calendar.DATE, 3);
        break;
      case Calendar.SATURDAY:
        calendar.add(Calendar.DATE, 2);
        break;
      case Calendar.SUNDAY:
        calendar.add(Calendar.DATE, 1);
        break;
      default:
        break;
    }
    return simpleDateFormat.format(calendar.getTime());
  }

  /**
   * adds x weeks to current date, THEN proceeds to look for the next Monday.
   *
   * @param weeks How many weeks added
   * @return Monday x weeks from now
   */
  public static String getMondayXWeeksFromNow(int weeks) {
    return getMondayXWeeksFromDate(getCurrentCJSDate(), weeks);
  }

  public static String toVLWSFormat(String date) {
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yy");
    try {
      return simpleDateFormat.format(simpleDateFormat.parse(date));
    } catch (final ParseException e) {
      e.printStackTrace();
    }
    return "null";
  }

  public static String convertDate(String date, String inputFormat, String outputFormat) {
    try {
      final SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(inputFormat);
      final Calendar calendar = Calendar.getInstance();
      calendar.setTime(simpleDateFormat1.parse(date));
      final SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(outputFormat);
      return simpleDateFormat2.format(calendar.getTime());
    } catch (final Exception e) {
      e.printStackTrace();
      return "null";
    }
  }

  public static String convertNumberMonthToFullMonth(String date) {
    return convertDate(date, FORMAT_M, FORMAT_MMMM);
  }

  public static String convertDB2DateToBTSDate(String date) {
    return convertDate(date, FORMAT_YYYY_MM_DD, FORMAT_DD_MMM_YYYY);
  }

  public static String convertBTSDateToDB2Date(String date) {
    return convertDate(date, FORMAT_DD_MMM_YYYY, FORMAT_YYYY_MM_DD);
  }

  public static String getCurrentYear() {
    return getCurrentDateTime(FORMAT_YYYY);
  }

  public static String getYearAfterXYears(int years) {
    final int year = Integer.parseInt(getCurrentYear()) + years;
    return Integer.toString(year);
  }

  public static String convertDateToAllNumbers(String date) {
    return convertDate(date, FORMAT_DD_MMM_YYYY, FORMAT_MM_DD_YY);
  }

  public static String convertDateToCsvusFormat(String date) {
    return convertDate(date, FORMAT_DD_MMM_YYYY, FORMAT_US_STANDARD_DATE);
  }

  public static String getDateAfterXBusinessDays(int days) {
    final Calendar calendar = Calendar.getInstance();
    int i = 0;
    while (i < days) {
      calendar.add(Calendar.DAY_OF_YEAR, 1);
      if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
          && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
        i++;
      }
    }
    final SimpleDateFormat form = new SimpleDateFormat(FORMAT_DD_MMM_YYYY);
    return form.format(calendar.getTime());
  }

  public static String getDateAfterXBusinessDaysForACH(int days) {
    final Calendar calendar = Calendar.getInstance();
    int i = 0;
    // if starting on a weekend, Monday doesn't get counted for settlement
    // date
    if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
        || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
      i++;
    }
    while (i < days) {
      calendar.add(Calendar.DAY_OF_YEAR, 1);
      if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
          && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
        i++;
      }
    }
    final SimpleDateFormat form = new SimpleDateFormat(FORMAT_YYMMDD);
    return form.format(calendar.getTime());
  }

  public static String getDateAfterXBusinessDaysForCSVusAPI(int days) {
    final Calendar calendar = Calendar.getInstance();
    int i = 0;
    // if starting on a weekend, Monday doesn't get counted for settlement
    // date
    if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
        || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
      i++;
    }
    while (i < days) {
      calendar.add(Calendar.DAY_OF_YEAR, 1);
      if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
          && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
        i++;
      }
    }
    final SimpleDateFormat form = new SimpleDateFormat("MMddYYYY");
    return form.format(calendar.getTime());
  }

  public static String getFeeBillDateAndTime() {
    return getCurrentDateTime("yyyy-MM-dd HH:mm");
  }

  public static String convertServerDateToBTSDate(String date) {
    return convertDate(date, FORMAT_US_STANDARD_DATE, FORMAT_DD_MMM_YYYY);
  }

  public static String getMonthEndDateAfterXMonths(int months) {
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DD_MMM_YYYY);
    final Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    if (months == 0) {
      months++;
    }
    calendar.add(Calendar.MONTH, months);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.add(Calendar.DAY_OF_MONTH, -1);
    if (simpleDateFormat.format(calendar.getTime()).equals(getCurrentCJSDate())) {
      calendar.add(Calendar.MONTH, 2);
      calendar.set(Calendar.DAY_OF_MONTH, 1);
      calendar.add(Calendar.DAY_OF_MONTH, -1);
    }
    return simpleDateFormat.format(calendar.getTime());
  }

  public static String getCurrentRAODate() {
    return getCurrentDateTime(FORMAT_YYMMDD);
  }

  public static int getWorkingDays(String dateStart, String dateEnd) {
    int workDays = 0;
    if (!dateStart.equals(dateEnd)) {
      Calendar calendarStart = Calendar.getInstance();
      Calendar calendarEnd = Calendar.getInstance();
      String[] dateArray = dateStart.split(DELIMETER);
      calendarStart.set(
          Integer.valueOf(dateArray[2]),
          Integer.valueOf(dateArray[0]),
          Integer.valueOf(dateArray[1]));
      dateArray = dateEnd.split(DELIMETER);
      calendarEnd.set(
          Integer.valueOf(dateArray[2]),
          Integer.valueOf(dateArray[0]),
          Integer.valueOf(dateArray[1]));
      if (calendarStart.getTimeInMillis() > calendarEnd.getTimeInMillis()) {
        // Swap the dates if they've been passed
        // backwards.
        final Calendar calendarTemp = calendarStart;
        calendarStart = calendarEnd;
        calendarEnd = calendarTemp;
      }
      do {
        // excluding start date
        // calendarStart.add(Calendar.DAY_OF_MONTH, 1);
        switch (calendarStart.get(Calendar.DAY_OF_WEEK)) {
          case Calendar.SUNDAY:
            break;
          case Calendar.MONDAY:
          case Calendar.TUESDAY:
          case Calendar.WEDNESDAY:
          case Calendar.THURSDAY:
          case Calendar.FRIDAY:
            workDays++;
            LOG.debug(
                "{} - {}{}{}{}{}",
                calendarStart.get(Calendar.DAY_OF_WEEK),
                calendarStart.get(Calendar.MONTH),
                DELIMETER,
                calendarStart.get(Calendar.DAY_OF_MONTH),
                DELIMETER,
                calendarStart.get(Calendar.YEAR));
            break;
          case Calendar.SATURDAY:
            break;
          default:
            // This should never happen as Calendar.DAY_OF_WEEK is 1-7
            LOG.warn("Unexpected day of week: {}", calendarStart.get(Calendar.DAY_OF_WEEK));
            break;
        }
        // if (calendarStart.get(Calendar.DAY_OF_WEEK) !=
        // Calendar.SATURDAY
        // && calendarStart.get(Calendar.DAY_OF_WEEK) !=
        // Calendar.SUNDAY)
        // {
        // workDays++;
        // }
        // including start date
        calendarStart.add(Calendar.DAY_OF_MONTH, 1);
      } while (calendarStart.getTimeInMillis() < calendarEnd.getTimeInMillis()); // excluding
    }
    // Return 0 if start and end are the same
    return workDays;
  }

  /**
   * parameter1: date1 format in MM/dd/yyyy parameter2: date2 format in MM/dd/yyyy returns: boolean
   */
  public static boolean isDate1BeforeDate2(String date1, String date2) {
    int index1 = date1.indexOf(DELIMETER);
    int index2 = date1.lastIndexOf(DELIMETER);
    final int mmDate1 = Integer.parseInt(date1.substring(0, index1));
    final int ddDate1 = Integer.parseInt(date1.substring(index1 + 1, index2));
    final int yyyyDate1 = Integer.parseInt(date1.substring(index2 + 1));
    index1 = date2.indexOf(DELIMETER);
    index2 = date2.lastIndexOf(DELIMETER);
    final int mmDate2 = Integer.parseInt(date2.substring(0, index1));
    final int ddDate2 = Integer.parseInt(date2.substring(index1 + 1, index2));
    final int yyyyDate2 = Integer.parseInt(date2.substring(index2 + 1));
    if (yyyyDate1 < yyyyDate2) {
      return true;
    }
    if (yyyyDate1 == yyyyDate2) {
      if (mmDate1 < mmDate2) {
        return true;
      }
      if (mmDate1 == mmDate2) {
        if (ddDate1 < ddDate2) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * parameter1: date1 format in MM/dd/yyyy parameter2: date2 format in MM/dd/yyyy returns: boolean
   */
  public static boolean isDate1AfterDate2(String date1, String date2) {
    int index1 = date1.indexOf(DELIMETER);
    int index2 = date1.lastIndexOf(DELIMETER);
    final int mmDate1 = Integer.parseInt(date1.substring(0, index1));
    final int yyyyDate1 = Integer.parseInt(date1.substring(index2 + 1));
    index1 = date2.indexOf(DELIMETER);
    index2 = date2.lastIndexOf(DELIMETER);
    final int mmDate2 = Integer.parseInt(date2.substring(0, index1));
    final int yyyyDate2 = Integer.parseInt(date2.substring(index2 + 1));
    if (yyyyDate1 > yyyyDate2) {
      return true;
    }
    if (yyyyDate1 == yyyyDate2) {
      if (mmDate1 > mmDate2) {
        return true;
      }
      if (mmDate1 >= mmDate2) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param date
   * @return
   */
  public static boolean isValidDate(String date) {
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DD_MMM_YYYY);
    try {
      simpleDateFormat.parse(date);
      return true;
    } catch (final ParseException e) {
      e.printStackTrace();
      return false;
    }
  }
}
