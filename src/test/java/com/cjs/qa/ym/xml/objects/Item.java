package com.cjs.qa.ym.xml.objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.cjs.qa.utilities.Constants;

@XmlRootElement(name = "Item")
public class Item {

  @XmlElement(name = "EventName")
  private String eventName;

  public String getEventName() {
    return eventName;
  }

  @XmlElement(name = "EventID")
  private String eventId;

  public String getEventID() {
    return eventId;
  }

  @XmlElement(name = "EventDate")
  private String eventDate;

  public String getEventDate() {
    return eventDate;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Event Name:" + getEventName() + Constants.NEWLINE);
    stringBuilder.append("Event ID:" + getEventID() + Constants.NEWLINE);
    stringBuilder.append("Event Date:" + getEventDate());
    return stringBuilder.toString();
  }
}
