package com.cjs.qa.atlassian.jira;

import com.cjs.qa.utilities.Constants;

public class Component {

  private EComponent eComponent = EComponent.QATOOLS;

  public Component(EComponent eComponent) {
    seteComponent(eComponent);
  }

  public EComponent geteComponent() {
    return eComponent;
  }

  public void seteComponent(EComponent eComponent) {
    this.eComponent = eComponent;
  }

  @Override
  public String toString() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(Constants.QUOTE_DOUBLE + "update" + Constants.QUOTE_DOUBLE + ": {");
    stringBuilder.append(Constants.QUOTE_DOUBLE + "components" + Constants.QUOTE_DOUBLE + ": [");
    stringBuilder.append("{" + Constants.QUOTE_DOUBLE + "set" + Constants.QUOTE_DOUBLE + ":");
    stringBuilder.append("[");
    stringBuilder.append("{");
    stringBuilder.append(
        Constants.QUOTE_DOUBLE
            + "name"
            + Constants.QUOTE_DOUBLE
            + ": "
            + Constants.QUOTE_DOUBLE
            + ""
            + geteComponent().getValue()
            + Constants.QUOTE_DOUBLE);
    stringBuilder.append("}");
    stringBuilder.append("]");
    stringBuilder.append("}");
    stringBuilder.append("]");
    stringBuilder.append("}");
    return stringBuilder.toString();
  }
}
