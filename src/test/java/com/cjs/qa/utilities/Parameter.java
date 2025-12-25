package com.cjs.qa.utilities;

public class Parameter {

  // List<Parameter> parameterList = null;
  private int index = -1;
  private String name = null;
  private String type = null;
  private Object value = null;

  /** Default constructor for data binding and instantiation. */
  public Parameter() {
    // Default constructor for data binding
  }

  public Parameter(Integer index, String name, String type, Object value) {
    this.index = index;
    this.name = name;
    this.type = type;
    this.value = value;
  }

  public Integer getIndex() {
    return index;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public Object getValue() {
    return value;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public void setIndex(Integer index) {
    this.index = index;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setValue(Object value) {
    this.value = value;
  }
}
