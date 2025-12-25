package com.cjs.qa.ym.api.objects;

import java.util.List;

public class Event {

  private String eventDate;
  private String eventID;
  private String eventName;
  private String isValid;
  private List<Registration> registrationList;

  /** Default constructor for data binding and instantiation. */
  public Event() {
    // Default constructor for data binding
  }

  public Event(String eventDate, String eventID, String eventName) {
    this.eventDate = eventDate;
    this.eventID = eventID;
    this.eventName = eventName;
  }

  public String getEventDate() {
    return eventDate;
  }

  public String getEventID() {
    return eventID;
  }

  public String getEventName() {
    return eventName;
  }

  public String getIsValid() {
    return isValid;
  }

  public List<Registration> getRegistrationList() {
    return registrationList;
  }

  public void setEventDate(String eventDate) {
    this.eventDate = eventDate;
  }

  public void setEventID(String eventID) {
    this.eventID = eventID;
  }

  public void setEventName(String eventName) {
    this.eventName = eventName;
  }

  public void setIsValid(String isValid) {
    this.isValid = isValid;
  }

  public void setRegistrationList(List<Registration> registrationList) {
    this.registrationList = registrationList;
  }
}
