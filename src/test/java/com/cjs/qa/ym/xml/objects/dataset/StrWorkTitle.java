package com.cjs.qa.ym.xml.objects.dataset;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.cjs.qa.utilities.Constants;

@XmlRootElement(name = "strWorkTitle")
public class StrWorkTitle {

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
    stringBuilder.append("Work Title:" + getValue() + Constants.NEWLINE);
    return stringBuilder.toString();
  }
}
