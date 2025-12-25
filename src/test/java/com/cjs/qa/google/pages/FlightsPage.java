package com.cjs.qa.google.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.cjs.qa.google.objects.Flight;
import com.cjs.qa.microsoft.excel.IExcel;
import com.cjs.qa.microsoft.excel.xls.XLS;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.Convert;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.ParameterHelper;

public class FlightsPage extends Page {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(FlightsPage.class));

  public FlightsPage(WebDriver webDriver) {
    super(webDriver);
  }

  public static final String HEADINGS_FLIGHT =
      "Airport;Preference;Sort;Airline;Operated By;Time Depart;Time"
          + " Arrive;Duration;Stops;Price";
  public static final String HEADINGS_SUMMARY =
      "Traveler;Airport/Location Depart;Time Depart;Airport/Location Arrive;Time"
          + " Arrive;Stops;Price;URL";
  public static final String BEST_DEPARTING_FLIGHTS =
      ".//div[@class='gws-flights-results__best-flights']";
  public static final String OTHER_DEPARTING_FLIGHTS =
      ".//div[contains(@aria-labelledby,'gws-flights-results__other_flights_heading')]";
  public static final String DEPARTING_FLIGHTS_RECORD =
      "//div[contains(@class,'gws-flights-results__collapsed-itinerary')]";
  public static final By BEST_DEPARTING_FLIGHTS_BY =
      By.xpath(BEST_DEPARTING_FLIGHTS + DEPARTING_FLIGHTS_RECORD);
  public static final By OTHER_DEPARTING_FLIGHTS_BY =
      By.xpath(OTHER_DEPARTING_FLIGHTS + DEPARTING_FLIGHTS_RECORD);
  public static final By ERROR_PAGE =
      By.xpath(".//p[@role='status'][.='Oops, something went wrong.']");

  public List<WebElement> getBestDepartingFlightsList() {
    scrollToElement(BEST_DEPARTING_FLIGHTS);
    return getWebDriver().findElements(BEST_DEPARTING_FLIGHTS_BY);
  }

  public List<WebElement> getOtherDepartingFlightsList() {
    scrollToElement(OTHER_DEPARTING_FLIGHTS);
    return getWebDriver().findElements(OTHER_DEPARTING_FLIGHTS_BY);
  }

  public void getFlightsOld(
      String filePathName,
      List<Map<String, String>> airportListMap,
      String airportTo,
      String dateDepartTo,
      String dateDepartFrom)
      throws Throwable {
    final Map<String, Integer> summaryMap = getHeadingsSummaryMap();
    final Map<String, Integer> flightMap = getHeadingsFlightMap();
    LOG.debug("{}[{}]", Constants.CLASS_METHOD_DEBUG, getCurrentClassMethodDebugName());
    LOG.debug("Parameters: {}", ParameterHelper.getParameters(Arrays.asList(Arrays.asList())));
    if (FSOTests.fileExists(filePathName)) {
      FSOTests.fileDelete(filePathName);
    }
    String sheetSummary = IExcel.SHEET_SUMMARY;
    XLS excel = new XLS(filePathName, sheetSummary);
    excel.createHeadings(sheetSummary, HEADINGS_SUMMARY);
    excel.save();
    int row = 0;
    List<String> airportRetrievedList = new ArrayList<>();
    for (Map<String, String> airportMap : airportListMap) {
      String airportFrom = airportMap.get("Airport");
      final String sheetName = airportFrom;
      String traveler = airportMap.get("Traveler");
      LOG.debug("airportMap:{}", airportMap.toString());
      row++;
      excel.writeCell(sheetSummary, summaryMap.get("Traveler"), row, traveler);
      excel.save();
      if (airportFrom.equals(airportTo)) {
        String addressFrom = getURLAddress(airportMap);
        String url = getURLDrive(addressFrom, airportTo);
        getWebDriver().get(url);
        sleep(2);
        String miles = getDriveMiles();
        String duration = getDriveTime();
        excel.addLink(
            sheetSummary, summaryMap.get("Airport/Location Depart"), row, "URL", addressFrom, url);
        excel.writeCell(sheetSummary, summaryMap.get("Time Depart"), row, "Up to you");
        excel.writeCell(sheetSummary, summaryMap.get("Airport/Location Arrive"), row, airportTo);
        excel.writeCell(sheetSummary, summaryMap.get("Time Arrive"), row, duration);
        excel.writeCell(sheetSummary, summaryMap.get("Stops"), row, "Up to you");
        excel.writeCell(sheetSummary, summaryMap.get("Price"), row, getDrivePrice(miles));
        excel.addLink(sheetSummary, summaryMap.get("URL"), row, "URL", addressFrom, url);
        LOG.debug("airlineMapData:N/A");
      } else {
        airportRetrievedList.add(airportFrom);
        String url = getURLFlight(airportFrom, airportTo, dateDepartTo, dateDepartFrom);
        LOG.debug("url:[{}]", url);
        if (!excel.sheetExists(sheetName)) {
          excel.createSheet(sheetName);
        }
        getWebDriver().get(url);
        sleep(2);
        if (excel.getRowCount(sheetName) < 1) {
          excel.createHeadings(sheetName, HEADINGS_FLIGHT);
        }
        List<Flight> flightList = new ArrayList<>();
        List<WebElement> flightWebElementList = getBestDepartingFlightsList();
        for (int flightWebElementListIndex = 0;
            flightWebElementListIndex < flightWebElementList.size();
            flightWebElementListIndex++) {
          WebElement flightWebElement = flightWebElementList.get(flightWebElementListIndex);
          flightList.add(
              setFlight(airportFrom, "Best", flightWebElementListIndex + 1, flightWebElement));
        }
        flightWebElementList = getOtherDepartingFlightsList();
        for (int flightWebElementListIndex = 0;
            flightWebElementListIndex < flightWebElementList.size();
            flightWebElementListIndex++) {
          WebElement flightWebElement = flightWebElementList.get(flightWebElementListIndex);
          flightList.add(
              setFlight(airportFrom, "Other", flightWebElementListIndex + 1, flightWebElement));
        }
        for (int flightIndex = 0; flightIndex < flightList.size(); flightIndex++) {
          Flight flight = flightList.get(flightIndex);
          if (flightIndex == 0) {
            excel.addLink(
                sheetSummary,
                summaryMap.get("Airport/Location Depart"),
                row,
                "DOCUMENT",
                airportFrom,
                "'" + sheetName + "'!A2");
            excel.writeCell(
                sheetSummary, summaryMap.get("Time Depart"), row, flight.getTimeDepart());
            excel.writeCell(
                sheetSummary, summaryMap.get("Airport/Location Arrive"), row, airportTo);
            excel.writeCell(
                sheetSummary, summaryMap.get("Time Arrive"), row, flight.getTimeArrive());
            excel.writeCell(sheetSummary, summaryMap.get("Stops"), row, flight.getStops());
            double price = flight.getPrice();
            excel.writeCell(sheetSummary, summaryMap.get("Price"), row, price);
            excel.addLink(sheetSummary, summaryMap.get("URL"), row, "URL", sheetName, url);
          }
          excel.writeCell(
              sheetName, flightMap.get("Airport"), flightIndex + 1, flight.getAirport());
          excel.writeCell(
              sheetName, flightMap.get("Preference"), flightIndex + 1, flight.getPreference());
          excel.writeCell(sheetName, flightMap.get("Sort"), flightIndex + 1, flight.getSort());
          excel.writeCell(
              sheetName, flightMap.get("Airline"), flightIndex + 1, flight.getAirline());
          excel.writeCell(
              sheetName, flightMap.get("Operated By"), flightIndex + 1, flight.getOperatedBy());
          excel.writeCell(
              sheetName, flightMap.get("Time Depart"), flightIndex + 1, flight.getTimeDepart());
          excel.writeCell(
              sheetName, flightMap.get("Time Arrive"), flightIndex + 1, flight.getTimeArrive());
          excel.writeCell(
              sheetName, flightMap.get("Duration"), flightIndex + 1, flight.getDuration());
          excel.writeCell(sheetName, flightMap.get("Stops"), flightIndex + 1, flight.getStops());
          double price = flight.getPrice();
          excel.writeCell(sheetName, flightMap.get("Price"), flightIndex + 1, price);
        }
        excel.autoSizeColumns(sheetName);
        excel.save();
      }
      excel.save();
      sleep(3);
    }
    row++;
    for (int index = 0; index < getHeadingsSummaryList().size(); index++) {
      excel.setFormatHeading(sheetSummary, index, row);
    }
    // Need to fix the methods in JavaHelpers and Convert.
    String columnLetter = Convert.fromNumberToLetterExcel(summaryMap.get("Price"));
    // columnLetter = "G";
    String formulaSum = "SUM(" + columnLetter + "2:" + columnLetter + row + ")";
    excel.writeCell(sheetSummary, summaryMap.get("Traveler"), row, "Total");
    excel.writeCellFormula(sheetSummary, summaryMap.get("Price"), row, formulaSum);
    excel.writeCell(sheetSummary, 1, 0, excel.readCell(sheetSummary, 1, 0));
    excel.autoSizeColumns(sheetSummary);
    excel.save();
    excel.close();
  }

  public Flight getDriveData(Map<String, String> driverMap, String airportTo) {
    String traveler = driverMap.get("Traveler");
    String airportFrom = driverMap.get("Airport");
    String addressFrom = getURLAddress(driverMap);
    String url = getURLDrive(addressFrom, airportTo);
    getWebDriver().get(url);
    sleep(2);
    String miles = getDriveMiles();
    String duration = getDriveTime();
    String price = getDrivePrice(miles);
    return new Flight(
        airportFrom,
        "Driving",
        1,
        addressFrom,
        traveler,
        "Up to you",
        duration,
        duration,
        "Up to you",
        Double.valueOf(price));
  }

  public List<Flight> getFlights(
      String airportFrom, String airportTo, String dateDepartTo, String dateDepartFrom)
      throws Throwable {
    String url = getURLFlight(airportFrom, airportTo, dateDepartTo, dateDepartFrom);
    LOG.debug("url:[[{}]", url + "]");
    getWebDriver().get(url);
    sleep(2);
    List<Flight> flightList = new ArrayList<>();
    List<WebElement> flightWebElementList = getBestDepartingFlightsList();
    for (int flightWebElementListIndex = 0;
        flightWebElementListIndex < flightWebElementList.size();
        flightWebElementListIndex++) {
      WebElement flightWebElement = flightWebElementList.get(flightWebElementListIndex);
      flightList.add(
          setFlight(airportFrom, "Best", flightWebElementListIndex + 1, flightWebElement));
    }
    flightWebElementList = getOtherDepartingFlightsList();
    for (int flightWebElementListIndex = 0;
        flightWebElementListIndex < flightWebElementList.size();
        flightWebElementListIndex++) {
      WebElement flightWebElement = flightWebElementList.get(flightWebElementListIndex);
      flightList.add(
          setFlight(airportFrom, "Other", flightWebElementListIndex + 1, flightWebElement));
    }
    return flightList;
  }

  public Flight setFlight(
      String airportFrom, String preference, int index, WebElement flightRecordWebElement) {
    highlightCurrentElement(flightRecordWebElement);
    String airline = getFlightAirline(flightRecordWebElement);
    String airlingOperatedBy = getFlightAirlineOtherInfo(flightRecordWebElement);
    String timeArrive = getFlightTimeArrive(flightRecordWebElement);
    String duration = getFlightDuration(flightRecordWebElement);
    String timeDepart = getFlightTimeDepart(flightRecordWebElement);
    String stops = getFlightStops(flightRecordWebElement);
    String price = getFlightPrice(flightRecordWebElement);
    price = price.replaceAll("\\$", "");
    price = price.replaceAll(",", "");
    Flight flight =
        new Flight(
            airportFrom,
            preference,
            index,
            airline,
            airlingOperatedBy,
            timeDepart,
            duration,
            timeArrive,
            stops,
            Double.valueOf(price));
    LOG.debug("flight:{}", flight.toString());
    return flight;
  }

  public String getFlightAirline(WebElement recordFlight) {
    By by = By.xpath(".//div[contains(@class,'gws-flights-results__carriers')]/span/span/span");
    WebElement webElement = recordFlight.findElement(by);
    return webElement.getText();
  }

  public String getFlightAirlineOtherInfo(WebElement recordFlight) {
    By by = By.xpath(".//span[contains(@class,'gws-flights-results__airline-extra-info')]//span");
    List<WebElement> webElementList = recordFlight.findElements(by);
    StringBuilder stringBuilder = new StringBuilder();
    for (WebElement webElement : webElementList) {
      stringBuilder = new StringBuilder();
      String value = webElement.getText();
      stringBuilder.append(value);
    }
    return stringBuilder.toString();
  }

  public String getFlightDuration(WebElement recordFlight) {
    By by = By.xpath(".//div[contains(@class,'gws-flights-results__duration')]");
    WebElement webElement = recordFlight.findElement(by);
    return webElement.getText();
  }

  public String getFlightPrice(WebElement recordFlight) {
    By by = By.xpath(".//div[contains(@class,'gws-flights-results__price')]");
    WebElement webElement = recordFlight.findElement(by);
    return webElement.getText();
  }

  public String getFlightStops(WebElement recordFlight) {
    By by = By.xpath(".//div[contains(@class,'gws-flights-results__stops')]//div/span");
    WebElement webElement = recordFlight.findElement(by);
    return webElement.getText();
  }

  public String getFlightTimeArrive(WebElement recordFlight) {
    By by = By.xpath(".//div[contains(@class,'gws-flights-results__times')]/div/span/span/span");
    List<WebElement> webElementList = recordFlight.findElements(by);
    WebElement webElement = webElementList.get(1);
    return webElement.getText();
  }

  public String getFlightTimeDepart(WebElement recordFlight) {
    By by = By.xpath(".//div[contains(@class,'gws-flights-results__times')]/div/span/span/span");
    List<WebElement> webElementList = recordFlight.findElements(by);
    WebElement webElement = webElementList.get(0);
    return webElement.getText();
  }

  public String getDriveMiles() {
    By by =
        By.xpath(
            ".//*[@id='section-directions-trip-0']//div[contains(@class,'section-directions-trip-distance')]/div");
    List<WebElement> webElementList = getWebDriver().findElements(by);
    String value = "";
    for (WebElement webElement : webElementList) {
      value = webElement.getText();
    }
    return value.replaceAll(" miles", "");
  }

  public String getDrivePrice(String miles) {
    final Double mileage2019 = .58;
    double price = Double.valueOf(miles) * 2.0; // RoundTrip
    price = price * mileage2019;
    // return JavaHelpers.formatNumber(String.valueOf(price), "$#,##0.00");
    return formatNumber(String.valueOf(price), "###0.00");
  }

  public String getDriveTime() {
    By by =
        By.xpath(
            ".//*[@id='section-directions-trip-0']//div[contains(@class,'section-directions-trip-duration')]/span[1]");
    List<WebElement> webElementList = getWebDriver().findElements(by);
    String value = "";
    for (WebElement webElement : webElementList) {
      value = webElement.getText();
    }
    return value.replaceAll(" miles", "");
  }

  private static List<String> getHeadingsFlightList() {
    return Arrays.asList(HEADINGS_FLIGHT.split(Constants.DELIMETER_LIST));
  }

  private static List<String> getHeadingsSummaryList() {
    return Arrays.asList(HEADINGS_SUMMARY.split(Constants.DELIMETER_LIST));
  }

  private static Map<String, Integer> getHeadingsFlightMap() {
    Map<String, Integer> map = new HashMap<>();
    List<String> headingList = getHeadingsFlightList();
    for (int index = 0; index < headingList.size(); index++) {
      map.put(headingList.get(index), index);
    }
    return map;
  }

  private static Map<String, Integer> getHeadingsSummaryMap() {
    Map<String, Integer> map = new HashMap<>();
    List<String> headingList = getHeadingsSummaryList();
    for (int index = 0; index < headingList.size(); index++) {
      map.put(headingList.get(index), index);
    }
    return map;
  }

  private static String getURLAddress(Map<String, String> airportMap) {
    StringBuilder stringBuilderAddress = new StringBuilder();
    stringBuilderAddress.append(airportMap.get("Address1"));
    stringBuilderAddress.append(", ");
    stringBuilderAddress.append(airportMap.get("City"));
    stringBuilderAddress.append(", ");
    stringBuilderAddress.append(airportMap.get("State"));
    stringBuilderAddress.append(" ");
    stringBuilderAddress.append(airportMap.get("Zip"));
    return stringBuilderAddress.toString().replaceAll("  ", "");
  }

  private static String getURLDrive(String addressFrom, String airportTo) {
    // https://www.google.com/maps/dir/16539+Grant+Ave,+Orland+Park,+IL+60467/ORD+airport,+West+O'Hare+Avenue,+Chicago,+IL/@41.7672521,-88.2343674,10z/data=!4m14!4m13!1m5!1m1!1s0x880e402f139f3e1b:0x9c302ee21d5dfb26!2m2!1d-87.8858303!2d41.5884197!1m5!1m1!1s0x880fb4276a7762f3:0x511747070259ad4b!2m2!1d-87.9073214!2d41.9741625!3e0
    StringBuilder stringBuilderURL = new StringBuilder();
    stringBuilderURL.append("https://www.google.com/maps/dir/");
    // 16539+Grant+Ave,+Orland+Park,+IL+60467
    addressFrom = addressFrom.replaceAll(",", "");
    addressFrom = addressFrom.replaceAll(" ", "+");
    stringBuilderURL.append(addressFrom);
    stringBuilderURL.append("/" + airportTo);
    return stringBuilderURL.toString();
  }

  private static String getURLFlight(
      String airportFrom, String airportTo, String dateDepartTo, String dateDepartFrom) {
    // https://www.google.com/flights#flt=LRH.ORD.2019-06-28*ORD.LRH.2019-06-30;c:USD;e:1;sd:1;t:f
    StringBuilder stringBuilderURL = new StringBuilder();
    stringBuilderURL.append("https://www.google.com/flights#flt=");
    stringBuilderURL.append(airportFrom);
    stringBuilderURL.append(".");
    stringBuilderURL.append(airportTo);
    stringBuilderURL.append(".");
    stringBuilderURL.append(dateDepartTo);
    stringBuilderURL.append("*");
    stringBuilderURL.append(airportTo);
    stringBuilderURL.append(".");
    stringBuilderURL.append(airportFrom);
    stringBuilderURL.append(".");
    stringBuilderURL.append(dateDepartFrom);
    stringBuilderURL.append(";c:USD;e:1;sd:1;t:f");
    return stringBuilderURL.toString();
  }
}
