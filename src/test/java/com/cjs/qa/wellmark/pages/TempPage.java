package com.cjs.qa.wellmark.pages;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.Page;

public class TempPage extends Page {

  public TempPage(WebDriver webDriver) {
    super(webDriver);
  }

  private List<String> listLinks = new ArrayList<>();

  public List<String> getListLinks() {
    return listLinks;
  }
}
