package com.cjs.qa.microsoft.objects;

import com.cjs.qa.utilities.Constants;

public class Answers {

  private String id = null; // Dynamic AnswerID
  private String value = null; // Button Text Value
  private String dataID = null; // Correct Index (1)
  private String dataSerpquery = null; // Search Query
  private String dataOptionNumber = null; // Dynamic Option Number (0)
  private String dataOption = null; // Button Text Value
  private boolean correct = false; // Is Correct

  public String getDataID() {
    return dataID;
  }

  public String getDataOption() {
    return dataOption;
  }

  public String getDataOptionNumber() {
    return dataOptionNumber;
  }

  public String getDataSerpquery() {
    return dataSerpquery;
  }

  public String getId() {
    return id;
  }

  public String getValue() {
    return value;
  }

  public boolean isCorrect() {
    return correct;
  }

  public void setCorrect(boolean correct) {
    this.correct = correct;
  }

  public void setDataID(String dataID) {
    this.dataID = dataID;
  }

  public void setDataOption(String dataOption) {
    this.dataOption = dataOption;
  }

  public void setDataOptionNumber(String dataOptionNumber) {
    this.dataOptionNumber = dataOptionNumber;
  }

  public void setDataSerpquery(String dataSerpquery) {
    this.dataSerpquery = dataSerpquery;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("ID: [" + getId() + "]");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("Value: [" + getValue() + "]");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("Data ID: [" + getDataID() + "]");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("Data Search Query: [" + getDataSerpquery() + "]");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("Data Option Number: [" + getDataOptionNumber() + "]");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("Data Option: [" + getDataOption() + "]");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("Correct: [" + isCorrect() + "]");
    return stringBuilder.toString();
  }
}
