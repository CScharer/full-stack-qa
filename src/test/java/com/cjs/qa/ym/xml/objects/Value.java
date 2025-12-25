package com.cjs.qa.ym.xml.objects;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Value")
public class Value {

  // @XmlElement(name = "Value")
  private Value value;

  public Value getValue() {
    return value;
  }
}
