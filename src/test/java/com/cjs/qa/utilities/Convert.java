package com.cjs.qa.utilities;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import io.cucumber.datatable.DataTable;

public final class Convert {

  private Convert() {}

  public static final String LABEL_INSTANCES_OF_MAX = "instancesOfMax";
  public static final String LABEL_LETTER = "letter";
  public static final String LABEL_NUMBER = "number";

  public static List<List<String>> fromDataTableToList(DataTable dataTable) {
    return fromDataTableToList(dataTable, true);
  }

  public static List<List<String>> fromDataTableToList(
      DataTable dataTable, boolean includeEmptyValues) {
    final List<List<String>> listList = dataTable.asLists();
    final List<List<String>> listListNew = new ArrayList<>();
    for (final List<?> item : listList) {
      final String value = (String) item.get(1);
      if (includeEmptyValues || !value.isEmpty()) {
        final List<String> list = new ArrayList<>();
        final String key = (String) item.get(0);
        list.add(key);
        list.add(value);
        listListNew.add(Collections.unmodifiableList(list));
      }
    }
    return listListNew;
  }

  public static Map<String, String> fromDataTableToMap(DataTable dataTable) {
    return fromDataTableToMap(dataTable, true);
  }

  public static Map<String, String> fromDataTableToMap(
      DataTable dataTable, boolean includeEmptyValues) {
    final Map<String, String> map = new HashMap<>();
    final List<List<String>> data = dataTable.asLists();
    for (final List<?> record : data) {
      final String value = (String) record.get(1);
      if (includeEmptyValues || !value.isEmpty()) {
        final String key = (String) record.get(0);
        map.put(key, value);
      }
    }
    return map;
  }

  /**
   * Converts InputStream to String.
   *
   * @return String
   */
  public static String fromInputStreamToString(InputStream inputStream) {
    final StringBuilder stringBuilder = new StringBuilder();
    List<String> lines = null;
    try {
      lines = IOUtils.readLines(inputStream, java.nio.charset.StandardCharsets.UTF_8);
    } catch (final java.io.UncheckedIOException e) {
      e.toString();
    }
    if (lines != null) {
      for (final String line : lines) {
        stringBuilder.append(line + Constants.NL);
      }
    }
    return stringBuilder.toString();
  }

  public static List<String> fromKeySetToList(Set<String> keySet) {
    final List<String> list = new ArrayList<>();
    for (final String value : keySet) {
      list.add(value);
    }
    return list;
  }

  public static File[] fromListPathToFileArray(List<Path> listPath) {
    File[] array = {};
    final int arrayLength = listPath.size();
    array = Arrays.copyOf(array, arrayLength);
    for (int indexPath = 0; indexPath < listPath.size(); indexPath++) {
      Path path = listPath.get(indexPath);
      String filePathName = path.toString();
      File file = new File(filePathName);
      // array = JavaHelpers.arrayAppend(array, file)
      array[indexPath] = file;
    }
    return array;
  }

  public static List<String> fromListPathToListString(List<Path> listPath) {
    List<String> list = new ArrayList<>();
    for (Path path : listPath) {
      String filePathName = path.toString();
      list.add(filePathName);
    }
    return list;
  }

  public static File[] fromListStringToFileArray(List<String> list) {
    File[] array = {};
    for (String filePathName : list) {
      File file = new File(filePathName);
      JavaHelpers.arrayAppend(array, file);
    }
    return array;
  }

  public static DataTable fromListToDataTable(List<List<String>> listList) {
    return fromListToDataTable(listList, true);
  }

  public static DataTable fromListToDataTable(
      List<List<String>> listList, boolean includeEmptyValues) {
    DataTable dataTable = DataTable.create(listList);
    final List<List<String>> listListNew = fromDataTableToList(dataTable, includeEmptyValues);
    dataTable = DataTable.create(listListNew);
    return dataTable;
  }

  public static Map<String, String> fromListToMap(List<List<String>> listList) {
    return fromListToMap(listList, false);
  }

  public static Map<String, String> fromListToMap(
      List<List<String>> listList, boolean includeEmptyValues) {
    return fromDataTableToMap(fromListToDataTable(listList), includeEmptyValues);
  }

  public static DataTable fromMapToDataTable(Map<String, String> map) {
    return fromMapToDataTable(map, true);
  }

  public static DataTable fromMapToDataTable(Map<String, String> map, boolean includeEmptyValues) {
    final List<List<String>> listList = new ArrayList<>();
    for (final Entry<String, String> entry : map.entrySet()) {
      final String value = entry.getValue();
      if (!value.isEmpty() || includeEmptyValues) {
        final List<String> list = new ArrayList<>();
        final String key = entry.getKey();
        list.add(key);
        list.add(value);
        listList.add(Collections.unmodifiableList(list));
      }
    }
    return fromListToDataTable(listList);
  }

  public static List<List<String>> fromMapToList(Map<String, String> map) {
    return fromMapToList(map, true);
  }

  public static List<List<String>> fromMapToList(
      Map<String, String> map, boolean includeEmptyValues) {
    return fromDataTableToList(fromMapToDataTable(map, includeEmptyValues));
  }

  public static String fromNumberToLetter(int number) {
    final int maxNumber = 25;
    if (number < 0 || number > maxNumber) {
      return null;
    }
    return String.valueOf(number + 65);
  }

  public static String fromNumberToLetterExcel(int number) {
    if (number < 0) {
      return null;
    }
    final int maxNumber = 26;
    StringBuilder stringBuilder = new StringBuilder();
    final int instancesOfMax = number / maxNumber;
    Map<String, String> mapInstances = new HashMap<>();
    mapInstances.put(LABEL_INSTANCES_OF_MAX, String.valueOf(instancesOfMax));
    mapInstances.put(LABEL_LETTER, stringBuilder.toString());
    mapInstances.put(LABEL_NUMBER, String.valueOf(number));
    mapInstances = fromNumberToLetterExcelLevel(mapInstances, maxNumber);
    number = Integer.parseInt(mapInstances.get(LABEL_NUMBER));
    stringBuilder.append(mapInstances.get(LABEL_LETTER));
    final int remainder = number % maxNumber;
    if (number >= maxNumber) {
      // stringBuilder.append(String.valueOf(instancesOfMax + 64));
      stringBuilder.append(Character.toString((char) (instancesOfMax + 64)));
      number = remainder;
    }
    if (number >= 0) {
      // stringBuilder.append(String.valueOf(instancesOfMax + 65));
      stringBuilder.append(Character.toString((char) (number + 65)));
    }
    return stringBuilder.toString();
  }

  private static Map<String, String> fromNumberToLetterExcelLevel(
      Map<String, String> mapInstances, int maxNumber) {
    final int instancesOfMax = Integer.parseInt(mapInstances.get(LABEL_INSTANCES_OF_MAX));
    if (instancesOfMax > maxNumber) {
      final int number = Integer.parseInt(mapInstances.get(LABEL_NUMBER));
      String letter = fromNumberToLetterExcel(instancesOfMax - 1);
      final int numberNew = number - ((instancesOfMax - 1) * maxNumber + maxNumber);
      letter += fromNumberToLetterExcel(numberNew);
      mapInstances.put(LABEL_INSTANCES_OF_MAX, String.valueOf(instancesOfMax));
      mapInstances.put(LABEL_LETTER, letter);
      mapInstances.put(LABEL_NUMBER, String.valueOf(-1));
    }
    return mapInstances;
  }

  public static String fromRGBToHex(String rgb) {
    final String[] numberArray = rgb.replace("rgba(", "").replace(")", "").split(",");
    final int number1 = Integer.parseInt(numberArray[0]);
    numberArray[1] = numberArray[1].trim();
    final int number2 = Integer.parseInt(numberArray[1]);
    numberArray[2] = numberArray[2].trim();
    final int number3 = Integer.parseInt(numberArray[2]);
    return String.format("#%02x%02x%02x", number1, number2, number3);
  }
}
