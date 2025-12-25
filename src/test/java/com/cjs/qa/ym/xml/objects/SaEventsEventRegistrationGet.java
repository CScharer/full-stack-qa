package com.cjs.qa.ym.xml.objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.cjs.qa.utilities.Constants;

@XmlRootElement(name = "Sa.Events.Event.Registration.Get")
public class SaEventsEventRegistrationGet {

  @XmlElement(name = "Status")
  private String status;

  public String getStatus() {
    return status;
  }

  @XmlElement(name = "EventName")
  private String eventName;

  public String getEventName() {
    return eventName;
  }

  @XmlElement(name = "FirstName")
  private String firstName;

  public String getFirstName() {
    return firstName;
  }

  @XmlElement(name = "LastName")
  private String lastName;

  public String getLastName() {
    return lastName;
  }

  @XmlElement(name = "BadgeNumber")
  private String badgeNumber;

  public String getBadgeNumber() {
    return badgeNumber;
  }

  @XmlElement(name = "RegistrationID")
  private String registrationID;

  public String getRegistrationID() {
    return registrationID;
  }

  @XmlElement(name = "DateRegistered")
  private String dateRegistered;

  public String getDateRegistered() {
    return dateRegistered;
  }

  @XmlElement(name = "Attended")
  private String attended;

  public String getAttended() {
    return attended;
  }

  @XmlElement(name = "OrderID")
  private String orderID;

  public String getOrderID() {
    return orderID;
  }

  @XmlElement(name = "MemberID")
  private String memberID;

  public String getMemberID() {
    return memberID;
  }

  @XmlElement(name = "InvoiceID")
  private String invoiceID;

  public String getInvoiceID() {
    return invoiceID;
  }

  @XmlElement(name = "IsPrimaryRegistrant")
  private String isPrimaryRegistrant;

  public String getIsPrimaryRegistrant() {
    return isPrimaryRegistrant;
  }

  @XmlElement(name = "PrimaryRegistrantRegistrationID")
  private String primaryRegistrantRegistrationID;

  public String getPrimaryRegistrantRegistrationID() {
    return primaryRegistrantRegistrationID;
  }

  @XmlElement(name = "PrimaryRegistrantMemberID")
  private String primaryRegistrantMemberID;

  public String getPrimaryRegistrantMemberID() {
    return primaryRegistrantMemberID;
  }

  @XmlElement(name = "AttendeeType")
  private String attendeeType;

  public String getAttendeeType() {
    return attendeeType;
  }

  @XmlElement(name = "DataSet")
  private DataSet dataSet;

  public DataSet getDataSet() {
    return dataSet;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Status:" + getStatus() + Constants.NEWLINE);
    stringBuilder.append("Event Name:" + getEventName() + Constants.NEWLINE);
    stringBuilder.append("First Name:" + getFirstName() + Constants.NEWLINE);
    stringBuilder.append("Last Name:" + getLastName() + Constants.NEWLINE);
    stringBuilder.append("Badge Number:" + getBadgeNumber() + Constants.NEWLINE);
    stringBuilder.append("Registration ID:" + getRegistrationID() + Constants.NEWLINE);
    stringBuilder.append("Date Registered:" + getDateRegistered() + Constants.NEWLINE);
    stringBuilder.append("Attended:" + getAttended() + Constants.NEWLINE);
    stringBuilder.append("Order ID:" + getOrderID() + Constants.NEWLINE);
    stringBuilder.append("Member ID:" + getMemberID() + Constants.NEWLINE);
    stringBuilder.append("Invoice ID:" + getInvoiceID() + Constants.NEWLINE);
    stringBuilder.append("IsPrimary Registrant:" + getIsPrimaryRegistrant() + Constants.NEWLINE);
    stringBuilder.append(
        "Primary Registrant Registration ID:"
            + getPrimaryRegistrantRegistrationID()
            + Constants.NEWLINE);
    stringBuilder.append(
        "Primary Registrant Member ID:" + getPrimaryRegistrantMemberID() + Constants.NEWLINE);
    stringBuilder.append("Attendee Type:" + getAttendeeType() + Constants.NEWLINE);
    stringBuilder.append(
        "Data Set:" + Constants.NEWLINE + getDataSet().toString() + Constants.NEWLINE);
    return stringBuilder.toString();
  }
}
