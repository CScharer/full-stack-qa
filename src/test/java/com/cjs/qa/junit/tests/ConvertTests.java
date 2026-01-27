package com.cjs.qa.junit.tests;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Test;

import com.cjs.qa.microsoft.excel.IExcel;
import com.cjs.qa.utilities.Convert;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.JavaHelpers;

import io.cucumber.datatable.DataTable;
import io.cucumber.datatable.DataTable.TableConverter;
import io.cucumber.datatable.DataTableTypeRegistry;
import io.cucumber.datatable.DataTableTypeRegistryTableConverter;

public class ConvertTests {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(ConvertTests.class));

  // DataTable converter setup for Cucumber 7.3.4+
  private static final DataTableTypeRegistry registry = new DataTableTypeRegistry(Locale.ENGLISH);
  private static final TableConverter tableConverter =
      new DataTableTypeRegistryTableConverter(registry);

  private static DataTable getDataTable() {
    return DataTable.create(getList(), tableConverter);
  }

  private static List<List<String>> getList() {
    return Arrays.asList(
        Arrays.asList("One", "1"),
        Arrays.asList("Two", "2"),
        Arrays.asList("Three", ""),
        Arrays.asList("Four", "4"),
        Arrays.asList("Five", "5"),
        Arrays.asList("Six", "6"),
        Arrays.asList("Seven", ""),
        Arrays.asList("Eight", "8"),
        Arrays.asList("Nine", "9"),
        Arrays.asList("Ten", "10"));
  }

  private static Map<String, String> getMap() {
    final Map<String, String> map = new HashMap<>();
    map.put("One", "1");
    map.put("Two", "2");
    map.put("Three", "");
    map.put("Four", "4");
    map.put("Five", "5");
    map.put("Six", "6");
    map.put("Seven", "");
    map.put("Eight", "8");
    map.put("Nine", "9");
    map.put("Ten", "10");
    return map;
  }

  @Test
  public void fromDataTableToList() {
    final String method = JavaHelpers.getCurrentMethodName();
    LOG.debug("method: [{}]", method);
    LOG.debug("Method name: {}", method);
    final DataTable dataTable = getDataTable();
    List<List<String>> listList;
    listList = Convert.fromDataTableToList(dataTable);
    LOG.debug("{}: [{}]", method, listList.toString());
    listList = Convert.fromDataTableToList(dataTable, true);
    LOG.debug("{}, true: [{}]", method, listList.toString());
    listList = Convert.fromDataTableToList(dataTable, false);
    LOG.debug("{}, false: [{}]", method, listList.toString());
  }

  @Test
  public void fromDataTableToMap() {
    final String method = JavaHelpers.getCurrentMethodName();
    LOG.debug("method: [{}]", method);
    final DataTable dataTable = getDataTable();
    Map<String, String> map;
    map = Convert.fromDataTableToMap(dataTable);
    LOG.debug("{}: [{}]", method, map.toString());
    map = Convert.fromDataTableToMap(dataTable, true);
    LOG.debug("{}, true: [{}]", method, map.toString());
    map = Convert.fromDataTableToMap(dataTable, false);
    LOG.debug("{}, false: [{}]", method, map.toString());
  }

  @Test
  public void fromListToDataTable() {
    final String method = JavaHelpers.getCurrentMethodName();
    LOG.debug("method: [{}]", method);
    final List<List<String>> listList = getList();
    DataTable dataTable;
    dataTable = Convert.fromListToDataTable(listList);
    LOG.debug("{}: [{}]", method, dataTable.toString());
    dataTable = Convert.fromListToDataTable(listList, true);
    LOG.debug("{}, true: [{}]", method, dataTable.toString());
    dataTable = Convert.fromListToDataTable(listList, false);
    LOG.debug("{}, false: [{}]", method, dataTable.toString());
  }

  @Test
  public void fromListToMap() {
    final String method = JavaHelpers.getCurrentMethodName();
    LOG.debug("method: [{}]", method);
    final List<List<String>> listList = getList();
    Map<String, String> map;
    map = Convert.fromListToMap(listList);
    LOG.debug("{}: [{}]", method, map.toString());
    map = Convert.fromListToMap(listList, true);
    LOG.debug("{}, true: [{}]", method, map.toString());
    map = Convert.fromListToMap(listList, false);
    LOG.debug("{}, false: [{}]", method, map.toString());
  }

  @Test
  public void fromMapToDataTable() {
    final String method = JavaHelpers.getCurrentMethodName();
    LOG.debug("method: [{}]", method);
    final Map<String, String> map = getMap();
    LOG.debug("map: [{}]", map.toString());
    DataTable dataTable;
    dataTable = Convert.fromMapToDataTable(map);
    LOG.debug("{}: [{}]", method, dataTable.toString());
    dataTable = Convert.fromMapToDataTable(map, true);
    LOG.debug("{}, true: [{}]", method, dataTable.toString());
    dataTable = Convert.fromMapToDataTable(map, false);
    LOG.debug("{}, false: [{}]", method, dataTable.toString());
  }

  @Test
  public void fromMapToList() {
    final String method = JavaHelpers.getCurrentMethodName();
    LOG.debug("method: [{}]", method);
    final Map<String, String> map = getMap();
    LOG.debug("map: [{}]", map.toString());
    List<List<String>> listList;
    listList = Convert.fromMapToList(map);
    LOG.debug("{}: [{}]", method, listList.toString());
    listList = Convert.fromMapToList(map, true);
    LOG.debug("{}, true: [{}]", method, listList.toString());
    listList = Convert.fromMapToList(map, false);
    LOG.debug("{}, false: [{}]", method, listList.toString());
  }

  @Test
  public void fromNumberToLetter() {
    final String method = JavaHelpers.getCurrentMethodName();
    LOG.debug("method: [{}]", method);
    for (int number = -1; number <= 26; number++) {
      LOG.debug("number: [{}], letter: [{}]", number, Convert.fromNumberToLetter(number));
    }
  }

  @Test
  public void fromNumberToLetterExcel() {
    final String method = JavaHelpers.getCurrentMethodName();
    final String formatNumber = "###,###,##0";
    final int numberStep = 1;
    final int numberStart = -1;
    final int numberEnd = IExcel.MAX_COLUMNS_XLSX + 1;
    LOG.debug("method: [{}]", method);
    for (int number = numberStart; number <= numberEnd; number += numberStep) {
      LOG.debug(
          "number: [{}], letter: [{}]",
          JavaHelpers.formatNumber(number, formatNumber),
          Convert.fromNumberToLetterExcel(number));
    }
  }
}
