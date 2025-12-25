package com.cjs.qa.ym.xml.objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.cjs.qa.utilities.Constants;
import com.cjs.qa.ym.xml.objects.dataset.*;

// import com.cjs.qa.ym.xml.objects.dataset.StrEmail;

// PMD: Suppress UnnecessaryImport warning - wildcard import is used for many classes from dataset
// package
@SuppressWarnings("PMD.UnnecessaryImport")
@XmlRootElement(name = "DataSet")
public class DataSet {

  @XmlElement(name = "strEmail")
  private StrEmail email;

  public StrEmail getEmail() {
    return email;
  }

  @XmlElement(name = "strAddress1")
  private StrAddress1 address1;

  public StrAddress1 getAddress1() {
    return address1;
  }

  @XmlElement(name = "strAddress2")
  private StrAddress2 address2;

  public StrAddress2 getAddress2() {
    return address2;
  }

  @XmlElement(name = "strCity")
  private StrCity city;

  public StrCity getCity() {
    return city;
  }

  @XmlElement(name = "strLocation")
  private StrLocation location;

  public StrLocation getLocation() {
    return location;
  }

  @XmlElement(name = "strPostalCode")
  private StrPostalCode postalCode;

  public StrPostalCode getPostalCode() {
    return postalCode;
  }

  @XmlElement(name = "strCountry")
  private StrCountry country;

  public StrCountry getCountry() {
    return country;
  }

  @XmlElement(name = "strPhone")
  private StrPhone phone;

  public StrPhone getPhone() {
    return phone;
  }

  @XmlElement(name = "strWorkTitle")
  private StrWorkTitle workTitle;

  public StrWorkTitle getWorkTitle() {
    return workTitle;
  }

  @XmlElement(name = "strEmployerName")
  private StrEmployerName employerName;

  public StrEmployerName getEmployerName() {
    return employerName;
  }

  @XmlElement(name = "Custom_JobLevel")
  private CustomJobLevel customJobLevel;

  public CustomJobLevel getCustomJobLevel() {
    return customJobLevel;
  }

  @XmlElement(name = "Custom_JobFunction")
  private CustomJobFunction customJobFunction;

  public CustomJobFunction getCustomJobFunction() {
    return customJobFunction;
  }

  @XmlElement(name = "Custom_MayHPEemailyouofferssupportdatesandeventnews")
  private CustomMayHpeEmailYouOffersSupportDatesAndEventNews
      customMayHpeEmailYouOffersSupportDatesAndEventNews;

  public CustomMayHpeEmailYouOffersSupportDatesAndEventNews
      getCustomMayHpeEmailYouOffersSupportDatesAndEventNews() {
    return customMayHpeEmailYouOffersSupportDatesAndEventNews;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Email:" + getEmail() + Constants.NEWLINE);
    stringBuilder.append("Address1:" + getAddress1() + Constants.NEWLINE);
    stringBuilder.append("Address2:" + getAddress2() + Constants.NEWLINE);
    stringBuilder.append("City:" + getCity() + Constants.NEWLINE);
    stringBuilder.append("Location:" + getLocation() + Constants.NEWLINE);
    stringBuilder.append("Postal Code:" + getPostalCode() + Constants.NEWLINE);
    stringBuilder.append("Country:" + getCountry() + Constants.NEWLINE);
    stringBuilder.append("Phone:" + getPhone() + Constants.NEWLINE);
    stringBuilder.append("Work Title:" + getWorkTitle() + Constants.NEWLINE);
    stringBuilder.append("Employer Name:" + getEmployerName() + Constants.NEWLINE);
    stringBuilder.append("Custom_Job Level:" + getCustomJobLevel() + Constants.NEWLINE);
    stringBuilder.append("Custom_Job Function:" + getCustomJobFunction() + Constants.NEWLINE);
    stringBuilder.append(
        "Custom_May HPE emailyouofferssupportdatesandeventnews:"
            + getCustomMayHpeEmailYouOffersSupportDatesAndEventNews()
            + Constants.NEWLINE);
    return stringBuilder.toString();
  }
}
