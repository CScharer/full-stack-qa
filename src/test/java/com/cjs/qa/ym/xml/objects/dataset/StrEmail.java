package com.cjs.qa.ym.xml.objects.dataset;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.cjs.qa.utilities.Constants;

@XmlRootElement(name = "strEmail")
public class StrEmail {

  @XmlAttribute(name = "ExportValue")
  private String exportValue;

  public String getExportValue() {
    return exportValue;
  }

  @XmlElement(name = "Value")
  private String value;

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Export Value:" + getExportValue() + Constants.NEWLINE);
    stringBuilder.append("Email:" + getValue() + Constants.NEWLINE);
    return stringBuilder.toString();
  }
}
