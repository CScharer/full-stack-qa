package com.cjs.qa.bts.pages;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.cjs.qa.selenium.ISelenium;
import com.cjs.qa.selenium.Page;
import com.cjs.qa.utilities.GuardedLogger;

import io.cucumber.datatable.DataTable;

public class RatePage extends Page {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(RatePage.class));

  public RatePage(WebDriver webDriver) {
    super(webDriver);
  }

  private static final String pageTitle = "RatePage";

  private String getPageTitle() {
    return pageTitle;
  }

  private static final String nodeProcessedErrs =
      ".//*[@id='form1:cplProcessedResults:cplProcessedErrs:";
  private static final By labelCustomStatusMsg = By.xpath(nodeProcessedErrs + "customStatusMsg']");
  private static final By labelPrintStatus =
      By.xpath(nodeProcessedErrs + "processTransactionStatus']");
  private static final By toggleResults =
      By.xpath(nodeProcessedErrs + "cplProcessedErrorsTable']/div[1]/span[1]/button");
  private static final By labelPolicyTotal = By.xpath(nodeProcessedErrs + "overviewTotM']");
  private static final By labelCommercialAuto =
      By.xpath(nodeProcessedErrs + "productsTableEx1:0:overviewTotM1']");
  private static final By labelAgriculturalOutput =
      By.xpath(nodeProcessedErrs + "productsTableEx1:1:overviewTotM1']");
  private static final By labelGeneralLiability =
      By.xpath(nodeProcessedErrs + "productsTableEx1:2:overviewTotM1']");
  private static final By labelPolicywideCoverages =
      By.xpath(nodeProcessedErrs + "productsTableEx1:3:overviewTotM1']");
  private static final By buttonRateNow =
      By.xpath(".//*[@id='form1:cplProcessedResults:flashScreenRateButton']");

  public void verifyCustomStatusMsg(String value) {
    verifyResultData(value, labelCustomStatusMsg);
  }

  public void verifyPrintStatus(String value) {
    verifyResultData(value, labelPrintStatus);
  }

  public void toggleResults() {
    clickObject(toggleResults);
  }

  public void verifyResultPolicyTotal(String value) {
    verifyResultData(value, labelPolicyTotal);
  }

  public void verifyResultCommercialAuto(String value) {
    verifyResultData(value, labelCommercialAuto);
  }

  public void verifyResultAgriculturalOutput(String value) {
    verifyResultData(value, labelAgriculturalOutput);
  }

  public void verifyResultGeneralLiability(String value) {
    verifyResultData(value, labelGeneralLiability);
  }

  public void verifyResultPolicywideCoverages(String value) {
    verifyResultData(value, labelPolicywideCoverages);
  }

  public String getLabelPolicyTotal() {
    return getLabel(labelPolicyTotal);
  }

  public String getLabelCommercialAuto() {
    return getLabel(labelCommercialAuto);
  }

  public String getLabelAgriculturalOutput() {
    return getLabel(labelAgriculturalOutput);
  }

  public String getLabelGeneralLiability() {
    return getLabel(labelGeneralLiability);
  }

  public String getLabelPolicyCoverages() {
    return getLabel(labelPolicywideCoverages);
  }

  public void clickButtonRateNow() {
    clickObject(buttonRateNow);
  }

  // public void populatePage(DataTable table) {
  // List<List<String>> data = table.asLists();
  // for (List<?> item : data) {
  // String field = (String) item.get(0);
  // String value = (String) item.get(1);
  // if (!value.isEmpty()) {
  // sysOut("({Field}" + field + ", {Value}" + value + ");");
  // switch (field.toLowerCase(Locale.ENGLISH)) {
  // case "field1":
  // setEdit1(value);
  // break;
  // case "field2":
  // setEdit2(value);
  // break;
  // case "fieldN":
  // setEditN(value);
  // break;
  // default:
  // sysOut("[" + field + "]" + ISelenium.FIELD_NOT_CODED);
  // break;
  // }
  // }
  // }
  // }
  public void validatePage(DataTable table) {
    final Map<String, String> expected = new HashMap<>();
    final Map<String, String> actual = new HashMap<>();
    final List<List<String>> data = table.asLists();
    for (final List<?> item : data) {
      final String field = (String) item.get(0);
      final String value = (String) item.get(1);
      if (!value.isEmpty()) {
        expected.put(field, value);
        switch (field.toLowerCase(Locale.ENGLISH)) {
          case "policy total":
            actual.put(field, getLabelPolicyTotal());
            break;
          case "commercial auto":
            actual.put(field, getLabelCommercialAuto());
            break;
          case "agricultural output":
            actual.put(field, getLabelAgriculturalOutput());
            break;
          case "general liability":
            actual.put(field, getLabelGeneralLiability());
            break;
          case "policywide coverages":
            actual.put(field, getLabelGeneralLiability());
            break;
          default:
            LOG.warn("[{}]{}", field, ISelenium.FIELD_NOT_CODED);
            break;
        }
      }
    }
    Assertions.assertSame(expected, actual, getPageTitle() + " validatePage");
  }
}
