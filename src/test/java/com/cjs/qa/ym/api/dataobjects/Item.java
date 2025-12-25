package com.cjs.qa.ym.api.dataobjects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Item")
public class Item {

  @XmlElement(name = "EventName")
  private String eventName;

  @XmlElement(name = "EventID")
  private String eventID;

  @XmlElement(name = "EventDate")
  private String eventDate;

  public Item(String eventName, String eventID, String eventDate) {
    this.eventName = eventName;
    this.eventID = eventID;
    this.eventDate = eventDate;
  }

  public String getEventName() {
    return eventName;
  }

  public void setEventName(String eventName) {
    this.eventName = eventName;
  }

  public String getEventID() {
    return eventID;
  }

  public void setEventID(String eventID) {
    this.eventID = eventID;
  }

  public String getEventDate() {
    return eventDate;
  }

  public void setEventDate(String eventDate) {
    this.eventDate = eventDate;
  }
}
