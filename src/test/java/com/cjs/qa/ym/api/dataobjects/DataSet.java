package com.cjs.qa.ym.api.dataobjects;

import javax.xml.bind.annotation.XmlElement;

public class DataSet {

  @XmlElement(name = "strEmail")
  private String strEmail;

  @XmlElement(name = "strWorkTitle")
  private String strWorkTitle;

  @XmlElement(name = "strEmployerName")
  private String strEmployerName;

  @XmlElement(name = "strAddress1")
  private String strAddress1;

  @XmlElement(name = "strAddress2")
  private String strAddress2;

  @XmlElement(name = "strCity")
  private String strCity;

  @XmlElement(name = "strLocation")
  private String strLocation;

  @XmlElement(name = "strPostalCode")
  private String strPostalCode;

  @XmlElement(name = "strCountry")
  private String strCountry;

  @XmlElement(name = "strPhone")
  private String strPhone;

  @XmlElement(name = "strRSVPComments")
  private String strRSVPComments;

  @XmlElement(name = "Custom_AdditionalComments")
  private String customAdditionalComments;

  public String getStrEmail() {
    return strEmail;
  }

  public void setStrEmail(String strEmail) {
    this.strEmail = strEmail;
  }

  public String getStrWorkTitle() {
    return strWorkTitle;
  }

  public void setStrWorkTitle(String strWorkTitle) {
    this.strWorkTitle = strWorkTitle;
  }

  public String getStrEmployerName() {
    return strEmployerName;
  }

  public void setStrEmployerName(String strEmployerName) {
    this.strEmployerName = strEmployerName;
  }

  public String getStrAddress1() {
    return strAddress1;
  }

  public void setStrAddress1(String strAddress1) {
    this.strAddress1 = strAddress1;
  }

  public String getStrAddress2() {
    return strAddress2;
  }

  public void setStrAddress2(String strAddress2) {
    this.strAddress2 = strAddress2;
  }

  public String getStrCity() {
    return strCity;
  }

  public void setStrCity(String strCity) {
    this.strCity = strCity;
  }

  public String getStrLocation() {
    return strLocation;
  }

  public void setStrLocation(String strLocation) {
    this.strLocation = strLocation;
  }

  public String getStrPostalCode() {
    return strPostalCode;
  }

  public void setStrPostalCode(String strPostalCode) {
    this.strPostalCode = strPostalCode;
  }

  public String getStrCountry() {
    return strCountry;
  }

  public void setStrCountry(String strCountry) {
    this.strCountry = strCountry;
  }

  public String getStrPhone() {
    return strPhone;
  }

  public void setStrPhone(String strPhone) {
    this.strPhone = strPhone;
  }

  public String getStrRSVPComments() {
    return strRSVPComments;
  }

  public void setStrRSVPComments(String strRSVPComments) {
    this.strRSVPComments = strRSVPComments;
  }

  public String getCustomAdditionalComments() {
    return customAdditionalComments;
  }

  public void setCustomAdditionalComments(String customAdditionalComments) {
    this.customAdditionalComments = customAdditionalComments;
  }
}
