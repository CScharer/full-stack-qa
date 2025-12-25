package com.cjs.qa.atlassian.jira;

import com.cjs.qa.utilities.Constants;

public class ManualTestSteps {

  private int index;
  private String step;
  private String data;
  private String result;

  public ManualTestSteps(int index, String step, String data, String result) {
    this.index = index;
    this.step = step;
    this.data = data;
    this.result = result;
  }

  public String getData() {
    return data;
  }

  public int getIndex() {
    return index;
  }

  public String getResult() {
    return result;
  }

  public String getStep() {
    return step;
  }

  public void setData(String data) {
    this.data = data;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public void setStep(String step) {
    this.step = step;
  }

  @Override
  public String toString() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("{");
    stringBuilder.append(
        Constants.QUOTE_DOUBLE + "index" + Constants.QUOTE_DOUBLE + ": " + getIndex() + ",");
    stringBuilder.append(
        Constants.QUOTE_DOUBLE + "step" + Constants.QUOTE_DOUBLE + ": " + getStep() + ",");
    stringBuilder.append(
        Constants.QUOTE_DOUBLE + "data" + Constants.QUOTE_DOUBLE + ": " + getData() + ",");
    stringBuilder.append(
        Constants.QUOTE_DOUBLE + "result" + Constants.QUOTE_DOUBLE + ": " + getResult());
    stringBuilder.append("}");
    return stringBuilder.toString();
  }
}
