package com.cjs.qa.google.objects;

public class Flight {

  // Airport;Preference;Sort;Airline;Operated By;Time Depart;Time
  // Arrive;Duration;Stops;Price
  private String airport;
  private String preference;
  private int sort;
  private String airline;
  private String operatedBy;
  private String timeDepart;
  private String duration;
  private String timeArrive;
  private String stops;
  private double price;

  public Flight(
      String airport,
      String preference,
      int sort,
      String airline,
      String operatedBy,
      String timeDepart,
      String duration,
      String timeArrive,
      String stops,
      double price) {
    this.airport = airport;
    this.preference = preference;
    this.sort = sort;
    this.airline = airline;
    this.operatedBy = operatedBy;
    this.timeDepart = timeDepart;
    this.duration = duration;
    this.timeArrive = timeArrive;
    this.stops = stops;
    this.price = price;
  }

  public String getAirline() {
    return airline;
  }

  public String getAirport() {
    return airport;
  }

  public String getDuration() {
    return duration;
  }

  public String getOperatedBy() {
    return operatedBy;
  }

  public String getPreference() {
    return preference;
  }

  public double getPrice() {
    return price;
  }

  public int getSort() {
    return sort;
  }

  public String getStops() {
    return stops;
  }

  public String getTimeArrive() {
    return timeArrive;
  }

  public String getTimeDepart() {
    return timeDepart;
  }

  public void setAirline(String airline) {
    this.airline = airline;
  }

  public void setAirport(String airport) {
    this.airport = airport;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  public void setOperatedBy(String operatedBy) {
    this.operatedBy = operatedBy;
  }

  public void setPreference(String preference) {
    this.preference = preference;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public void setSort(int sort) {
    this.sort = sort;
  }

  public void setStops(String stops) {
    this.stops = stops;
  }

  public void setTimeArrive(String timeArrive) {
    this.timeArrive = timeArrive;
  }

  public void setTimeDepart(String timeDepart) {
    this.timeDepart = timeDepart;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Airport:[" + getAirport() + "], ");
    stringBuilder.append("Preference:[" + getPreference() + "], ");
    stringBuilder.append("Sort:[" + getSort() + "], ");
    stringBuilder.append("Airline:[" + getAirline() + "], ");
    stringBuilder.append("Airline Operated By:[" + getOperatedBy() + "], ");
    stringBuilder.append("Time Depart:[" + getTimeDepart() + "], ");
    stringBuilder.append("Duration:[" + getDuration() + "], ");
    stringBuilder.append("Time Arrive:[" + getTimeArrive() + "], ");
    stringBuilder.append("Stops:[" + getStops() + "], ");
    stringBuilder.append("Price:[" + getPrice() + "]");
    return stringBuilder.toString();
  }
}
