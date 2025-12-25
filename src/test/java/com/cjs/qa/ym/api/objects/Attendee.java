package com.cjs.qa.ym.api.objects;

public class Attendee {

  private String registerID;
  private String rsvpID;
  private String dateRegister;
  private String lastName;
  private String firstName;
  private String websiteID;
  private String id;
  private String rsvpResponse;
  private DataSet dataSet;

  /** Default constructor for data binding and instantiation. */
  public Attendee() {
    // Default constructor for data binding
  }

  public Attendee(
      String registerID,
      String rsvpID,
      String dateRegister,
      String lastName,
      String firstName,
      String websiteID,
      String id,
      String rsvpResponse) {
    this.registerID = registerID;
    this.rsvpID = rsvpID;
    this.dateRegister = dateRegister;
    this.lastName = lastName;
    this.firstName = firstName;
    this.websiteID = websiteID;
    this.id = id;
    this.rsvpResponse = rsvpResponse;
  }

  public DataSet getDataSet() {
    return dataSet;
  }

  public String getDateRegister() {
    return dateRegister;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getId() {
    return id;
  }

  public String getLastName() {
    return lastName;
  }

  public String getRegisterID() {
    return registerID;
  }

  public String getRsvpID() {
    return rsvpID;
  }

  public String getRsvpResponse() {
    return rsvpResponse;
  }

  public String getWebsiteID() {
    return websiteID;
  }

  public void setDataSet(DataSet dataSet) {
    this.dataSet = dataSet;
  }

  public void setDateRegister(String dateRegister) {
    this.dateRegister = dateRegister;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setRegisterID(String registerID) {
    this.registerID = registerID;
  }

  public void setRsvpID(String rsvpID) {
    this.rsvpID = rsvpID;
  }

  public void setRsvpResponse(String rsvpResponse) {
    this.rsvpResponse = rsvpResponse;
  }

  public void setWebsiteID(String websiteID) {
    this.websiteID = websiteID;
  }
}
