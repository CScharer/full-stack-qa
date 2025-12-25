package com.cjs.qa.utilities;

public enum Log {
  ALL(true),
  METHOD(true),
  MAIL(true);

  private boolean value;

  Log(boolean value) {
    this.value = value;
  }

  public boolean getValue() {
    return value;
  }
}
