package com.cjs.qa.utilities;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;

import com.cjs.qa.core.Environment;
import com.cjs.qa.core.QAException;

public class JavaHelpers {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(JavaHelpers.class));
  public static final String COM_CJS = "com.cjs";
  private static Random random;

  public static Random getRandom() {
    return random;
  }

  public static void setRandom(Random random) {
    JavaHelpers.random = random;
  }

  public JavaHelpers() {
    try {
      // SecureRandom is preferred to Random
      setRandom(SecureRandom.getInstanceStrong());
    } catch (Exception e) {
      LOG.error("Failed to initialize SecureRandom", e);
    }
  }

  /**
   * @param array
   * @param element
   * @return
   */
  public static <T> T[] arrayAppend(T[] array, T element) {
    final int arrayLength = array.length;
    array = Arrays.copyOf(array, arrayLength + 1);
    array[arrayLength] = element;
    return array;
  }

  /**
   * Builds and returns a LinkedMap which represents a csv file. This way is much easier to
   * manipulate csv files and perform tasks such as searching for values inside it and verifying is
   * a given column and/or value is present
   *
   * @return A LinkedMap which represents a csv file.
   * @throws QAException
   */
  public static Map<String, List<String>> buildCsvMap(String filePathName) throws QAException {
    final List<String> fileList = FSOTests.fileReadAllList(filePathName);
    final Map<String, List<String>> csv = new LinkedHashMap<>();
    final List<String> headers = Arrays.asList(fileList.get(0).split(",", -1));
    for (int fileListIndex = 1; fileListIndex < fileList.size(); fileListIndex++) {
      final List<String> words = Arrays.asList(fileList.get(fileListIndex).split(",", -1));
      for (int wordIndex = 0; wordIndex < words.size(); wordIndex++) {
        if (csv.containsKey(headers.get(wordIndex))) {
          csv.get(headers.get(wordIndex)).add(words.get(wordIndex));
        } else {
          final List<String> listAUX = new ArrayList<>();
          listAUX.add(words.get(wordIndex));
          csv.put(headers.get(wordIndex), listAUX);
        }
      }
    }
    return csv;
  }

  /**
   * @param string1
   * @param string2
   * @return
   */
  public static int compareTo(String string1, String string2) {
    final int shortest = string1.length() < string2.length() ? string1.length() : string2.length();
    if (string1.equals(string2)) {
      return 0;
    }
    for (int i = 0; i < shortest; i++) {
      final char c = string1.charAt(i);
      if (c == '(' || c == '-') {
        return 0;
      }
      if (string1.charAt(i) > string2.charAt(i)) {
        return -1;
      } else if (string1.charAt(i) < string2.charAt(i)) {
        return 1;
      }
    }
    return 1;
  }

  /**
   * Counts how many rows contain the given value for the given column in the csv file.
   *
   * @return The number of times the given value appears under the desired column.
   * @throws QAException
   */
  public static int countRowsWithValueCSV(String column, String value, String filePathName)
      throws QAException {
    final Map<String, List<String>> csvMap = buildCsvMap(filePathName);
    if (csvMap.containsKey(column) && csvMap.get(column).contains(value)) {
      return csvMap.get(column).size();
    }
    return 0;
  }

  /**
   * @param value
   * @param repeat
   * @param joinCharacter
   * @return
   */
  public static String createBufferString(String value, int repeat, String joinCharacter) {
    if (joinCharacter == null) {
      joinCharacter = "";
    }
    return String.join(joinCharacter, Collections.nCopies(repeat, value));
  }

  public static String difference(String string1, String string2) {
    return StringUtils.difference(string1, string2);
  }

  // CollectionUtils.disjunction() returns Collection<?>, must cast to Collection<String>
  // Type safety is guaranteed by input parameters being List<String>
  @SuppressWarnings("unchecked")
  public static List<String> difference(List<String> list1, List<String> list2) {
    return new ArrayList<>((Collection<String>) CollectionUtils.disjunction(list1, list2));
  }

  /** */
  public static void displaySystemProperties() {
    Properties properties = System.getProperties();
    LOG.info("Property Count: [{}]", properties.size());
    LOG.info("System Properties DataTable");
    int maxLength = 0;
    for (Entry<Object, Object> entry : properties.entrySet()) {
      String key = (String) entry.getKey();
      if (key.length() > maxLength) {
        maxLength = key.length();
      }
    }
    LOG.debug("maxLength: [{}]", maxLength);
    // When doing a sysOut with the DataTable is corrupts the output.
    // List<List<String>> dataTableList = new ArrayList<>()
    // dataTableList.add(Arrays.asList("Key", "Value"))
    final List<String> propertyList = new ArrayList<>();
    for (Entry<Object, Object> entry : properties.entrySet()) {
      String key = (String) entry.getKey();
      String value = (String) entry.getValue();
      // dataTableList.add(Arrays.asList(key, value))
      int bufferLength = maxLength - key.length();
      String buffer = createBufferString(" ", bufferLength, "");
      String message = "key:[" + key + "], " + buffer + "value:[" + value + "]";
      propertyList.add(message);
    }
    propertyList.sort(null);
    for (String message : propertyList) {
      LOG.debug(message);
    }
    // DataTable dataTable = DataTable.create(dataTableList)
    // LOG.debug("DataTable: {}", dataTable.toString())
  }

  /**
   * Locates recursively or not all files in a given directory.
   *
   * @param root the directory to begin
   * @param recurse true to search as deep as possible. false if only search current directory.
   * @return file array of files found.
   */
  public static File[] findAllFiles(File root, boolean recurse) {
    final List<File> listFiles = new ArrayList<>();
    findAllFilesRecurse(root, listFiles, recurse);
    // convert List to array
    final File[] result = new File[listFiles.size()];
    listFiles.toArray(result);
    return result;
  }

  /**
   * Recursive part of findAllFiles
   *
   * @param root
   * @param files
   */
  private static void findAllFilesRecurse(File root, List<File> files, boolean recurse) {
    final File[] rootContents = root.listFiles();
    if (rootContents == null) {
      files.add(root);
    } else {
      for (final File file : rootContents) {
        // if it is a file or do not go deeper
        if (file.isFile() || !recurse) {
          files.add(file);
        } else {
          // directory
          findAllFilesRecurse(file, files, true);
        }
      }
    }
  }

  /**
   * @param directory
   * @param packageName
   * @return
   * @throws ClassNotFoundException
   */
  public static List<String> findClasses(File directory, String packageName)
      throws ClassNotFoundException {
    final List<String> classesList = new ArrayList<>();
    if (!directory.exists()) {
      return classesList;
    }
    final File[] files = directory.listFiles();
    for (final File file : files) {
      if (file.isDirectory()) {
        assert !file.getName().contains(".");
        classesList.addAll(findClasses(file, packageName + "." + file.getName()));
      } else if (file.getName().endsWith(".class")) {
        classesList.add(
            packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
      }
    }
    return classesList;
  }

  /**
   * @param value
   * @return
   */
  public static String formatCurrency(String value) {
    // final DecimalFormat decimalFormat = new DecimalFormat(format);
    // final String newValue = decimalFormat.format(Double.valueOf(value));
    final NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
    return String.valueOf(numberFormat.format(Double.valueOf(value)));
  }

  /**
   * @param amount
   * @param format
   * @return
   */
  public static String formatNumber(double amount, String format) {
    final DecimalFormat decimalFormat = new DecimalFormat(format);
    return decimalFormat.format(amount);
  }

  /**
   * @param amount
   * @param format
   * @return
   */
  public static String formatNumber(String amount, String format) {
    if (amount.contains(",")) {
      amount = amount.replace(",", "");
    }
    // if (amount.contains("."))
    // {
    return formatNumber(Double.valueOf(amount), format);
    // }
    // return formatNumber(String.valueOf(amount), format);
  }

  /**
   * @param numberMax
   * @return
   */
  public static int generateRandomInteger(int numberMax) {
    if (numberMax == 0) {
      return numberMax;
    }
    return generateRandomInteger(0, numberMax);
  }

  /**
   * @param numberMax
   * @return
   */
  public static int generateRandomInteger(int numberMin, int numberMax) {
    if (numberMin > numberMax) {
      LOG.warn(
          "{}: The numberMin [{}] is greater than the numberMax: [{}]",
          getCurrentMethodName(),
          numberMin,
          numberMax);
    }
    int randomNumber;
    if (numberMax == 0 || numberMax == numberMin) {
      return numberMax;
    }
    // randomNumber = ThreadLocalRandom.current().nextInt(numberMin,
    // numberMax + 1)
    // Random random = new Random()
    // int randomNumber = JavaHelpers.random.nextInt()
    randomNumber = random.nextInt(numberMax + 1 - numberMin) + numberMin;
    return randomNumber;
  }

  /**
   * @param numberMax
   * @param lengthMax
   * @return
   */
  public static String generateRandomInteger(int numberMin, int numberMax, int lengthMax) {
    StringBuilder stringBuilder = new StringBuilder();
    for (int index = 1; index <= lengthMax; index++) {
      final int randomInteger = generateRandomInteger(numberMin, numberMax);
      stringBuilder.append(String.valueOf(randomInteger));
    }
    return stringBuilder.toString();
  }

  /**
   * Returns the name of current class & method debug
   *
   * @return
   */
  /**
   * Returns the name of calling class
   *
   * @return
   */
  public static String getCallingClassName() {
    String className = Thread.currentThread().getStackTrace()[1].getClassName();
    className = className.substring(className.lastIndexOf('.') + 1, className.length());
    return className;
  }

  /**
   * @return
   */
  public static int getCallingLineNumber() {
    final StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
    final String fullString = stackTraceElements[2].toString();
    String lineNumber = fullString.substring(fullString.indexOf(':') + 1, fullString.length() - 1);
    return Integer.valueOf(lineNumber);
  }

  /**
   * Returns the name of calling method
   *
   * @return
   */
  public static String getCallingMethodName() {
    final StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
    return stackTraceElements[3].getMethodName();
  }

  /**
   * @param packageName
   * @return
   * @throws ClassNotFoundException
   * @throws IOException
   */
  public static List<String> getClasses(String packageName)
      throws ClassNotFoundException, IOException {
    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    assert classLoader != null;
    final String path = packageName.replace('.', '/');
    final Enumeration<URL> enumeration = classLoader.getResources(path);
    final List<File> directoriesList = new ArrayList<>();
    while (enumeration.hasMoreElements()) {
      final URL url = enumeration.nextElement();
      directoriesList.add(new File(url.getFile()));
    }
    final List<String> listClasses = new ArrayList<>();
    for (final File directory : directoriesList) {
      listClasses.addAll(findClasses(directory, packageName));
    }
    return listClasses;
  }

  /**
   * Returns the name of current class & method debug
   *
   * @return
   */
  public static String getCurrentClassMethodDebugName() {
    final StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
    final String fullString = stackTraceElements[1].toString();
    String className = Thread.currentThread().getStackTrace()[2].getClassName();
    String classNameTemp = className;
    className = className.substring(className.lastIndexOf('.') + 1, className.length());
    return Environment.noPMDUselessParentheses(
        className
            + fullString.substring(
                fullString.indexOf(classNameTemp) + classNameTemp.length(), fullString.length()));
  }

  /**
   * Returns the name of current class & method
   *
   * @return
   */
  public static String getCurrentClassMethodName() {
    final StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
    final String fullString = stackTraceElements[1].toString();
    String className = Thread.currentThread().getStackTrace()[2].getClassName();
    className = className.substring(className.lastIndexOf('.') + 1, className.length());
    final int stringEnd = fullString.indexOf('(');
    String fullName = fullString.substring(0, stringEnd);
    final int start = fullName.lastIndexOf('.') + 1;
    String methodName = fullName.substring(start);
    return className + '.' + methodName;
  }

  /**
   * Returns the name of current class
   *
   * @return
   */
  public static String getCurrentClassName() {
    String className = Thread.currentThread().getStackTrace()[2].getClassName();
    className = className.substring(className.lastIndexOf('.') + 1, className.length());
    return className;
  }

  public static int getCurrentLineNumber() {
    final StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
    final String fullString = stackTraceElements[1].toString();
    String lineNumber = fullString.substring(fullString.indexOf(':') + 1, fullString.length() - 1);
    return Integer.valueOf(lineNumber);
  }

  /**
   * Returns the name of current method debug
   *
   * @return
   */
  public static String getCurrentMethodDebugName() {
    final StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
    final String fullString = stackTraceElements[1].toString();
    String className = Thread.currentThread().getStackTrace()[2].getClassName();
    return fullString.substring(
        fullString.indexOf(className + '.') + className.length() + 1, fullString.length());
  }

  /**
   * Returns the name of current method
   *
   * @return
   */
  public static String getCurrentMethodName() {
    final StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
    return stackTraceElements[1].getMethodName();
  }

  /**
   * Returns the name of current package, class & method debug
   *
   * @return
   */
  public static String getCurrentPackageClassMethodDebugName() {
    final StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
    return stackTraceElements[1].toString();
  }

  /**
   * Returns the name of current package & class debug
   *
   * @return
   */
  public static String getCurrentPackageClassMethodName() {
    final StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
    final String fullString = stackTraceElements[1].toString();
    final int stringEnd = fullString.indexOf('(');
    return fullString.substring(0, stringEnd);
  }

  /**
   * Returns the name of current package & class
   *
   * @return
   */
  public static String getCurrentPackageClassName() {
    return Thread.currentThread().getStackTrace()[2].getClassName();
  }

  /**
   * Returns the name of current package
   *
   * @return
   */
  public static String getCurrentPackageName() {
    String packageName = Thread.currentThread().getStackTrace()[2].getClassName();
    packageName = packageName.substring(0, packageName.lastIndexOf('.'));
    return packageName;
  }

  /**
   * @param currentPackage
   * @return
   */
  public static List<String> getExclusions(String currentPackage) {
    final String exclusionStandard = "java.lang.Thread";
    final List<String> classesList = new ArrayList<>();
    classesList.add(exclusionStandard);
    currentPackage = currentPackage.replace(".", "/");
    try {
      classesList.addAll(getClasses(currentPackage));
    } catch (final Exception e) {
      LOG.error("Error getting classes for package: {}", currentPackage, e);
    }
    return classesList;
  }

  /**
   * @param name
   * @return
   */
  public static String getHostIPByName(String name) {
    InetAddress inetAddress = null;
    String hostAddress = null;
    try {
      inetAddress = InetAddress.getByName(name);
      hostAddress = inetAddress.getHostAddress();
    } catch (final UnknownHostException e) {
      LOG.error("Failed to get host address for: {}", name, e);
    }
    return hostAddress;
  }

  /**
   * @return
   */
  public static String getHostIPLocal() {
    InetAddress inetAddress = null;
    String hostAddress = null;
    try {
      inetAddress = InetAddress.getLocalHost();
      hostAddress = inetAddress.getHostAddress();
    } catch (final UnknownHostException e) {
      LOG.error("Failed to get local host address", e);
    }
    return hostAddress;
  }

  /**
   * @param ipAddress
   * @return
   */
  public static String getHostNameByIP(String ipAddress) {
    InetAddress inetAddress = null;
    String hostName = null;
    try {
      inetAddress = InetAddress.getByName(ipAddress);
      hostName = inetAddress.getHostName();
    } catch (final UnknownHostException e) {
      LOG.error("Failed to get host name for IP: {}", ipAddress, e);
    }
    return hostName;
  }

  /**
   * @return
   */
  public static String getHostNameLocal() {
    InetAddress inetAddress = null;
    String hostName = null;
    try {
      inetAddress = InetAddress.getLocalHost();
      hostName = inetAddress.getHostName();
    } catch (final UnknownHostException e) {
      LOG.error("Failed to get local host name", e);
    }
    return hostName;
  }

  /**
   * @return
   */
  public static int getMonitorCount() {
    GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    return graphicsEnvironment.getScreenDevices().length;
  }

  /**
   * @param screenDeviceIndex
   * @return
   */
  public static int getMonitorHeight(int screenDeviceIndex) throws Throwable {
    GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] screenDeviceArray = graphicsEnvironment.getScreenDevices();
    return screenDeviceArray[screenDeviceIndex].getDisplayMode().getHeight();
  }

  /**
   * @param screenDeviceIndex
   * @return
   */
  public static int getMonitorWidth(int screenDeviceIndex) throws Throwable {
    GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] screenDeviceArray = graphicsEnvironment.getScreenDevices();
    return screenDeviceArray[screenDeviceIndex].getDisplayMode().getWidth();
  }

  /**
   * @return
   */
  public static String getStackTrace() {
    final StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
    final StringBuilder stringBuilder = new StringBuilder();
    for (StackTraceElement stackTraceElement : stackTraceElements) {
      stringBuilder.append(stackTraceElement.toString() + Constants.NL);
    }
    return stringBuilder.toString();
  }

  /**
   * @param throwable
   * @return
   */
  public static String getStackTrace(Throwable throwable) {
    final Writer writer = new StringWriter();
    try (PrintWriter printWriter = new PrintWriter(writer)) {
      throwable.printStackTrace(printWriter);
    }
    return writer.toString();
  }

  /** Returns the name of current test case class name, used for debugging. */
  public static String getTestCaseClassName() {
    final StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
    for (final StackTraceElement stackTraceElement : stackTraceElements) {
      final String value = stackTraceElement.toString();
      if (value.contains("com.cjs.junit.testsuites")) {
        final int methodEnd = value.lastIndexOf('(');
        final int methodBegin = value.lastIndexOf('c', methodEnd);
        if (methodBegin < 0 || methodBegin >= methodEnd) {
          continue;
        }
        final String caseName = value.substring(methodBegin, methodEnd);
        try {
          Integer.parseInt(caseName.substring(1));
          return value;
        } catch (final Exception a) {
          // Intentionally empty - not a numbered test case, continue searching
          if (Environment.isLogAll()) {
            LOG.debug("Not a numbered test case, continuing search");
          }
        }
      }
    }
    return "";
  }

  /**
   * Searches the stack for a method that has @Test annotation.
   *
   * @return
   */
  public static String getTestCaseMethodName() {
    final StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
    for (final StackTraceElement stackTraceElement : stackTraceElements) {
      try {
        if (stackTraceElement.getClassName().contains(COM_CJS)
            && !stackTraceElement.getMethodName().contains("<init>")) {
          final Class<?> clss = Class.forName(stackTraceElement.getClassName());
          final Method method = clss.getMethod(stackTraceElement.getMethodName());
          if (method.getAnnotation(Test.class) != null) {
            return method.getName();
          }
        }
      } catch (final Exception f) {
        // Intentionally empty - not a test method, continue searching stack
        if (Environment.isLogAll()) {
          LOG.debug("Not a test method, continuing stack search");
        }
      }
    }
    return "null";
  }

  /**
   * @param value
   * @return
   */
  public static boolean hasValue(String value) {
    return value != null && !value.isEmpty();
  }

  /**
   * @param methods
   * @return
   */
  public static boolean isAnyMethodInStack(Set<String> methods) {
    for (final String s : methods) {
      if (isMethodInStack(s)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param iterable
   * @return
   */
  public static boolean isDateSorted(List<String> iterable) {
    final Iterator<String> iterator = iterable.iterator();
    if (!iterator.hasNext()) {
      return true;
    }
    String t = iterator.next();
    while (iterator.hasNext()) {
      String t2 = iterator.next();
      t = t.toLowerCase(Locale.ENGLISH);
      t2 = t2.toLowerCase(Locale.ENGLISH);
      if (DateHelpersTests.compareTo(t, t2) > 0) {
        LOG.warn("Comparison failed: {} > {}", t, t2);
        return false;
      }
      t = t2;
    }
    return true;
  }

  /**
   * Determines if a method name is in the call stack
   *
   * @return
   */
  public static boolean isMethodInStack(String methodName) {
    final StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
    for (StackTraceElement stackTraceElement : stackTraceElements) {
      if (stackTraceElement.toString().contains(methodName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param iterable
   * @return
   */
  public static boolean isSortedStrings(List<String> iterable) {
    final Iterator<String> iterator = iterable.iterator();
    if (!iterator.hasNext()) {
      return true;
    }
    String t = iterator.next();
    while (iterator.hasNext()) {
      String t2 = iterator.next();
      t = t.toLowerCase(Locale.ENGLISH);
      t2 = t2.toLowerCase(Locale.ENGLISH);
      if (compareTo(t, t2) < 0) {
        LOG.warn("Comparison failed: {} < {}", t, t2);
        return false;
      }
      t = t2;
    }
    return true;
  }

  /**
   * @param iterable
   * @return
   */
  public static boolean isSorted(List<Boolean> iterable) {
    final Iterator<Boolean> iterator = iterable.iterator();
    if (!iterator.hasNext()) {
      return true;
    }
    Boolean t = iterator.next();
    while (iterator.hasNext()) {
      final Boolean t2 = iterator.next();
      if (t.compareTo(t2) > 0) {
        LOG.warn("Comparison failed: {} > {}", t, t2);
        return false;
      }
      t = t2;
    }
    return true;
  }

  /**
   * @param iterable
   * @return
   */
  public static boolean isSortedInDescending(List<String> iterable) {
    final List<String> listReversed = new ArrayList<>();
    for (String item : iterable) {
      listReversed.add(item);
    }
    final Iterator<String> iterator = listReversed.iterator();
    if (!iterator.hasNext()) {
      return true;
    }
    String t = iterator.next();
    while (iterator.hasNext()) {
      String t2 = iterator.next();
      t = t.toLowerCase(Locale.ENGLISH);
      t2 = t2.toLowerCase(Locale.ENGLISH);
      if (compareTo(t, t2) > 0) {
        LOG.warn("Comparison failed: {} > {}", t, t2);
        return false;
      }
      t = t2;
    }
    return true;
  }

  /**
   * @param iterable
   * @return
   */
  public static boolean isSortedUpperCaseFirst(List<String> iterable) {
    final Iterator<String> iterator = iterable.iterator();
    if (!iterator.hasNext()) {
      return true;
    }
    String t = iterator.next();
    while (iterator.hasNext()) {
      final String t2 = iterator.next();
      if (compareTo(t, t2) < 0) {
        LOG.warn("Comparison failed: {} < {}", t, t2);
        return false;
      }
      t = t2;
    }
    return true;
  }

  /**
   * @param stringNumber
   * @return
   */
  public static boolean isValidStringNumber(String stringNumber) {
    if (stringNumber.isEmpty()) {
      return false;
    }
    final char[] charNumber = stringNumber.toCharArray();
    for (char ch : charNumber) {
      final String character = String.valueOf(ch);
      if (!".".equals(character) && !Character.isDigit(ch)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Verifies if a given value under a given column is present in the csv file.
   *
   * @throws QAException
   */
  public static boolean isValuePresentInColumnCSV(String value, String column, String filePathName)
      throws QAException {
    final Map<String, List<String>> csvMap = buildCsvMap(filePathName);
    return csvMap.containsKey(column) && csvMap.get(column).contains(value);
  }

  /**
   * Returns Files in specified directory satisfying the Filter
   *
   * @param directory the directory in which to search for files
   * @param filenameFilter restriction on files returned
   * @param recurse true if search subdirectories, false if only search current directory
   * @return Files in directory
   */
  private static Collection<File> listFiles(
      File directory, FilenameFilter filenameFilter, boolean recurse) {
    final List<File> fileList = new ArrayList<>();
    final File[] fileArray = directory.listFiles();
    for (final File file : fileArray) {
      if (filenameFilter == null || filenameFilter.accept(directory, file.getName())) {
        fileList.add(file);
      }
      if (recurse && file.isDirectory()) {
        fileList.addAll(listFiles(file, filenameFilter, recurse));
      }
    }
    return fileList;
  }

  /**
   * Returns a String array containing names of all test classes in project's testsuites package. It
   * does not detect subclasses inside a test class.
   *
   * <p>The method locates all files in the project directory recursively with the extension .java.
   * It then removes the .java extension from each of the strings.
   */
  public static String[] listFilesAsArray() {
    // Recursively find all .java files
    final File path = new File(Project.pathWorkspace());
    final FilenameFilter filenameFilter =
        new FilenameFilter() {
          @Override
          public boolean accept(File file, String filePathName) {
            return filePathName.endsWith(IExtension.JAVA);
          }
        };
    Collection<File> files = listFiles(path, filenameFilter, true);
    final List<File> fileList = new ArrayList<>(files);
    for (int index = fileList.size(); index >= 0; index--) {
      final String value = fileList.get(index).getAbsolutePath();
      if (value.contains("XX")
          || value.contains("testingpackage")
          || value.contains("datageneration")) {
        fileList.remove(fileList.get(index));
      }
    }
    files = fileList;
    // Convert the Collection into an array
    final File[] fileArray = new File[files.size()];
    files.toArray(fileArray);
    final String[] testClassesArray = new String[fileArray.length];
    String temp = "";
    // convert file path to full package declaration for the class
    for (int index = 0; index < fileArray.length; index++) {
      temp = fileArray[index].toString();
      temp = temp.replace(IExtension.JAVA, "").replace(Constants.DELIMETER_PATH, ".");
      // remove .java convert backslash
      if (temp.indexOf(COM_CJS) < 0) {
        testClassesArray[index] = "null";
      } else {
        temp = temp.substring(temp.indexOf(COM_CJS));
        temp = temp.replace("com.", "");
        testClassesArray[index] = temp;
      }
    }
    return testClassesArray;
  }

  /**
   * @param map
   * @return
   */
  public static String mapFormatPretty(Map<String, String> map) {
    return mapFormatPretty(map, false);
  }

  /**
   * @param map
   * @param sorted
   * @return
   */
  public static String mapFormatPretty(Map<String, String> map, boolean sorted) {
    final List<String> list = new ArrayList<>();
    for (final Entry<String, String> entry : map.entrySet()) {
      final String key = entry.getKey();
      final String value = entry.getValue();
      list.add(Constants.TAB + key + ":" + Constants.TAB + value);
    }
    final StringBuilder formatPretty = new StringBuilder();
    if (sorted) {
      list.sort(null);
    }
    for (int index = 0; index < list.size(); index++) {
      if (index > 0) {
        formatPretty.append(Constants.NEWLINE);
      }
      final String value = list.get(index);
      formatPretty.append(value);
    }
    return formatPretty.toString();
  }

  /**
   * @param mapName
   * @param map
   */
  public static void mapSysOut(String mapName, Map<String, String> map) {
    final StringBuilder formatPretty = new StringBuilder();
    formatPretty.append("MAPPING (" + mapName + ")");
    formatPretty.append(mapFormatPretty(map, true));
    LOG.info("Mapping: {}", formatPretty.toString());
  }

  /**
   * @param pdfFilePathName
   * @return
   * @throws QAException
   */
  public static String readPDF(String pdfFilePathName) throws QAException {
    try {
      // prevents overriding files when multiple files are created within
      // same second this could be resolved also by modifying file name
      sleep(1);
      for (int attempt = 0; attempt < 4; attempt++) {
        if (FSOTests.fileExists(pdfFilePathName)) {
          break;
        }
        sleep(2);
      }
      if (!FSOTests.fileExists(pdfFilePathName)) {
        LOG.warn("readPDF: PDF file not found: {}", pdfFilePathName);
        return "null";
      }
      final String filePathName =
          pdfFilePathName.substring(0, pdfFilePathName.lastIndexOf('.')) + IExtension.TXT;
      readPDFIntoTextFile(pdfFilePathName, filePathName);
      return FSOTests.fileReadAll(filePathName);
    } catch (final Exception e) {
      throw new QAException("Failure reading in readPDF:" + Constants.NL + getStackTrace(e), e);
    }
  }

  /**
   * @param pdf
   * @param outputText
   */
  public static void readPDFIntoTextFile(String pdf, String outputText) {
    try (PDDocument document = Loader.loadPDF(new File(pdf))) {
      PDFTextStripper stripper = new PDFTextStripper();
      String text = stripper.getText(document);
      Files.write(Paths.get(outputText), text.getBytes());
    } catch (final Exception e) {
      LOG.error("Error reading PDF file", e);
    }
  }

  /**
   * Opens and reads properties file
   *
   * @param path path to properties file
   * @return loaded properties file
   */
  public static Properties readPropertiesFile(String path) {
    final Properties properties = new Properties();
    try {
      properties.load(new FileInputStream(path));
    } catch (final IOException e) {
      LOG.error("Error Reading Properties File: {}", path, e);
    }
    return properties;
  }

  /**
   * Reads a System.Property or System.Environment value.
   *
   * @param key
   * @return
   */
  public static String readPropertyOrEnv(String key) {
    return readPropertyOrEnv(key, null);
  }

  /**
   * Reads a System.Property or System.Environment value.
   *
   * @param key
   * @param defaultValue
   * @return
   */
  public static String readPropertyOrEnv(String key, Object defaultValue) {
    String value = System.getProperty(key);
    if (value == null) {
      value = System.getenv(key);
    }
    if (value == null) {
      if (defaultValue != null) {
        value = (String) defaultValue;
      } else {
        value = "";
      }
    }
    return value;
  }

  /**
   * Saves properties file with comments
   *
   * @param filePathName properties file to save
   * @param path path to output file
   * @param comments comments to include in properties file
   */
  public static void savePropertiesFile(Properties filePathName, String path, String comments) {
    try {
      filePathName.store(new FileOutputStream(path), comments);
    } catch (final IOException e) {
      LOG.error("Error Saving Properties File: {}", path, e);
    }
  }

  /**
   * @param seconds
   */
  public static void sleep(long seconds) {
    sleep(seconds, 0);
  }

  /**
   * @param seconds
   * @param milliseconds
   */
  public static void sleep(long seconds, long milliseconds) {
    try {
      TimeUnit.SECONDS.sleep(seconds);
      TimeUnit.MILLISECONDS.sleep(milliseconds);
    } catch (Exception e) {
      LOG.error("Error in sleep method", e);
    }
  }

  /**
   * @param values
   * @return
   */
  protected List<String> sortListAscending(List<String> values) {
    Collections.sort(values);
    return values;
  }

  /**
   * @param values
   * @return
   */
  protected List<String> sortListDescending(List<String> values) {
    Collections.reverse(sortListAscending(values));
    return values;
  }
}
