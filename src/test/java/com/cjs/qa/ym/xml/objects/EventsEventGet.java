package com.cjs.qa.ym.xml.objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.cjs.qa.utilities.Constants;

@XmlRootElement(name = "Events.Event.Get")
public class EventsEventGet {

  @XmlElement(name = "Name")
  private String name;

  public String getName() {
    return name;
  }

  @XmlElement(name = "EventID")
  private String eventID;

  public String getEventID() {
    return eventID;
  }

  @XmlElement(name = "StartDateTime")
  private String startDateTime;

  public String getStartDateTime() {
    return startDateTime;
  }

  @XmlElement(name = "EndDateTime")
  private String endDateTime;

  public String getEndDateTime() {
    return endDateTime;
  }

  @XmlElement(name = "LocationName")
  private String locationName;

  public String getLocationName() {
    return locationName;
  }

  @XmlElement(name = "LocationAddress1")
  private String locationAddress1;

  public String getLocationAddress1() {
    return locationAddress1;
  }

  @XmlElement(name = "LocationAddress2")
  private String locationAddress2;

  public String getLocationAddress2() {
    return locationAddress2;
  }

  @XmlElement(name = "LocationCity")
  private String locationCity;

  public String getLocationCity() {
    return locationCity;
  }

  @XmlElement(name = "LocationLocation")
  private String locationLocation;

  public String getLocationLocation() {
    return locationLocation;
  }

  @XmlElement(name = "LocationPostalCode")
  private String locationPostalCode;

  public String getLocationPostalCode() {
    return locationPostalCode;
  }

  @XmlElement(name = "LocationCountry")
  private String locationCountry;

  public String getLocationCountry() {
    return locationCountry;
  }

  @XmlElement(name = "ContactName")
  private String contactName;

  public String getContactName() {
    return contactName;
  }

  @XmlElement(name = "ContactEmail")
  private String contactEmail;

  public String getContactEmail() {
    return contactEmail;
  }

  @XmlElement(name = "ContactPhone")
  private String contactPhone;

  public String getContactPhone() {
    return contactPhone;
  }

  @XmlElement(name = "ShortDescription")
  private String shortDescription;

  public String getShortDescription() {
    return shortDescription;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Name:" + getName() + Constants.NEWLINE);
    stringBuilder.append("Event ID:" + getEventID() + Constants.NEWLINE);
    stringBuilder.append("Start Date Time:" + getStartDateTime() + Constants.NEWLINE);
    stringBuilder.append("End Date Time:" + getEndDateTime() + Constants.NEWLINE);
    stringBuilder.append("Location Name:" + getLocationName() + Constants.NEWLINE);
    stringBuilder.append("Location Address 1:" + getLocationAddress1() + Constants.NEWLINE);
    stringBuilder.append("Location Address 2:" + getLocationAddress2() + Constants.NEWLINE);
    stringBuilder.append("Location City:" + getLocationCity() + Constants.NEWLINE);
    stringBuilder.append("Location Location:" + getLocationLocation() + Constants.NEWLINE);
    stringBuilder.append("Location PostalCode:" + getLocationPostalCode() + Constants.NEWLINE);
    stringBuilder.append("Location Country:" + getLocationCountry() + Constants.NEWLINE);
    stringBuilder.append("Contact Name:" + getContactName() + Constants.NEWLINE);
    stringBuilder.append("Contact Email:" + getContactEmail() + Constants.NEWLINE);
    stringBuilder.append("Contact Phone:" + getContactPhone() + Constants.NEWLINE);
    stringBuilder.append("Short Description:" + getShortDescription() + Constants.NEWLINE);
    return stringBuilder.toString();
  }
}
