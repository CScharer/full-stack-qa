package com.cjs.qa.united.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.core.QAException;
import com.cjs.qa.selenium.Page;

public class BookTravelPage extends Page {

  public BookTravelPage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final By editFrom = By.xpath(".//*[@id='flightSearch']//input[@id='Origin']");
  private static final By editTo = By.xpath(".//*[@id='flightSearch']//input[@id='Destination']");
  private static final By editDepartDate =
      By.xpath(".//*[@id='flightSearch']//input[@id='DepartDate']");
  private static final By editReturnDate =
      By.xpath(".//*[@id='flightSearch']//input[@id='ReturnDate']");
  private static final By buttonSearch = By.xpath(".//*[@id='flightBookingSubmit']");

  public void editFromSet(String value) throws QAException {
    setEdit(editFrom, value);
  }

  public void editToSet(String value) throws QAException {
    setEdit(editTo, value);
  }

  public void editDepartDateSet(String value) throws QAException {
    setEdit(editDepartDate, value);
  }

  public void editReturnDateSet(String value) throws QAException {
    setEdit(editReturnDate, value);
  }

  // public void CheckboxRememberMeSet(String value) throws QAException
  // {
  // setCheckbox(CheckboxRememberMe, value);
  // }

  public void buttonSearchClick() throws QAException {
    clickObject(buttonSearch);
  }

  public void bookTravel(String from, String to, String departDate, String returnDate)
      throws QAException {
    editFromSet(from);
    editToSet(to);
    editDepartDateSet(departDate);
    editReturnDateSet(returnDate);
    clickObject(By.xpath(".//*[@id='flightSearch']//label[@for='AwardTravel']"));
    buttonSearchClick();
  }
}
