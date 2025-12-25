package com.cjs.qa.utilities;

public enum EmailHost {
  AOL("smtp.aol" + IExtension.COM),
  BTS("smtp.live" + IExtension.COM),
  GMAIL("smtp.gmail" + IExtension.COM),
  MSN("smtp.live" + IExtension.COM),
  VIVIT("smtp.office365" + IExtension.COM);

  private String value;

  EmailHost(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
