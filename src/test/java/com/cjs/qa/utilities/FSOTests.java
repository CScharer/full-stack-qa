package com.cjs.qa.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.cjs.qa.core.Environment;
import com.cjs.qa.core.QAException;

@Disabled("Windows-specific test - not compatible with Mac or Test Needs Updates")
public class FSOTests {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(FSOTests.class));
  public static final int SECONDS_MINUTES_05 = Constants.MILLISECONDS * 60 * 5;
  public static final int SECONDS_MINUTES_10 = Constants.MILLISECONDS * 60 * 10;
  public static final int URL_TIMEOUT_CONNECTION = SECONDS_MINUTES_10;
  public static final int URL_TIMEOUT_READ = SECONDS_MINUTES_05;

  @Test
  public void testFilesFolders() {
    // String pathTest = Constants.PATH_PROJECT + "src\\test\\java\\"
    String pathTest = Constants.PATH_PROJECT;
    // displayList(filesGetList(pathTest, ".java"))
    // displayList(foldersGetList(pathTest))
    displayListPath("listFolders", foldersList(pathTest));
    // displayListPath("listFolders", listFolders(pathTest, true))
    displayListPath("filesList(pathTest)", filesList(pathTest));
    displayListPath("filesList(pathTest, false)", filesList(pathTest, false));
    String extension = IExtension.BAT;
    displayListPath(
        "filesList(pathTest, false, fileFilter)", filesList(pathTest, false, extension));
    // displayListPath("filesList", filesList(pathTest, false))
    sysOut(String.valueOf(filesGetCount(pathTest)));
    sysOut(String.valueOf(filesGetCount(pathTest, false)));
    sysOut(String.valueOf(filesGetCount(pathTest, false, extension)));
    //
    List<Path> pathList = pathsList(Constants.PATH_FILES_DATA_DATABASES, IExtension.BAK);
    File[] files = Convert.fromListPathToFileArray(pathList);
    displayArrayFile("FSOTests.pathsList(pathDatabase, \"" + IExtension.BAK + "\")", files);
  }

  private static void displayListPath(String type, List<String> list) {
    sysOut(type + ":[" + list.size() + "]");
    for (String path : list) {
      path = path.replace(Constants.PATH_PROJECT, "");
      sysOut(type + ":[" + path + "]");
    }
  }

  private static void displayArrayFile(String type, File[] files) {
    sysOut(type + ":[" + files.length + "]");
    for (File file : files) {
      String filePathName = file.getPath() + file.getName();
      sysOut(type + ":[" + filePathName + "]");
    }
  }

  private static void sysOut(String message) {
    LOG.info(message);
  }

  //
  // public void main(String[] args)
  // {
  // LOG.debug("Host Name:[{}]", getHostNameLocal());
  // LOG.debug("Host IP:[{}]", getHostIPLocal());
  // LOG.debug("Host Name:[{}]", getHostNameByIP(getHostIPLocal()));
  // LOG.debug("Host IP:[{}]", getHostIPByName(getHostNameLocal()));
  // LOG.debug("Host Name:[{}]", getHostNameByIP("172.217.4.228"));
  // LOG.debug("Host IP:[{}]", getHostIPByName("www.google" + IExtension.COM));
  // }
  //

  /**
   * @param filePathNameSource
   * @param filePathNameDestination
   * @throws QAException
   */
  public static void fileCopy(File filePathNameSource, File filePathNameDestination)
      throws QAException {
    try {
      if (!filePathNameDestination.exists()) {
        filePathNameDestination.createNewFile();
      }
      try (InputStream inputStream = new FileInputStream(filePathNameSource)) {
        try (OutputStream outputStream = new FileOutputStream(filePathNameDestination)) {
          final byte[] buffer = new byte[1024];
          int length;
          // copy the file content in bytes
          while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
          }
        } catch (Exception e2) {
          throw new QAException("OutputStream", e2);
        }
      } catch (Exception e1) {
        throw new QAException("InputStream", e1);
      }
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * @param filePathNameSource
   * @param filePathNameDestination
   * @throws QAException
   */
  public static void fileCopy(String filePathNameSource, String filePathNameDestination)
      throws QAException {
    fileCopy(new File(filePathNameSource), new File(filePathNameDestination));
  }

  /**
   * @param filePathName
   * @return
   */
  public static boolean fileExists(String filePathName) {
    final File file = new File(filePathName);
    return file.exists();
  }

  /**
   * Deletes a file or directory recursively
   *
   * @param file file to delete
   * @throws IOException
   */
  public static void fileDelete(File file) {
    if (file.isDirectory()) {
      // directory is empty, then delete it
      if (file.list().length == 0) {
        file.delete();
      } else {
        // list all the directory contents
        final String[] files = file.list();
        for (final String fileTemp : files) {
          // construct the file structure
          final File fileDelete = new File(file, fileTemp);
          // recursive delete
          fileDelete(fileDelete);
        }
        // check the directory again, if empty then delete it
        if (file.list().length == 0) {
          file.delete();
        }
      }
    } else {
      // if file, then delete it
      file.delete();
    }
  }

  /**
   * @param filePathName
   */
  public static void fileDelete(String filePathName) {
    final File file = new File(filePathName);
    if (file.exists()) {
      fileDelete(file);
    }
  }

  /**
   * @param linkURL
   * @param filePathName
   * @return
   * @throws QAException
   */
  public static boolean fileDownload(String linkURL, String filePathName) throws QAException {
    final File file = new File(filePathName);
    try {
      final URL url = URI.create(linkURL).toURL();
      // FileUtils.copyURLToFile(url, file)
      FileUtils.copyURLToFile(url, file, URL_TIMEOUT_CONNECTION, URL_TIMEOUT_READ);
    } catch (final Exception e) {
      throw new QAException(JavaHelpers.getCurrentMethodName(), e);
    }
    return true;
  }

  /**
   * @param exportURI
   * @param filePathName
   * @throws QAException
   */
  public static void fileDownloadFromURL(String exportURI, String filePathName) throws QAException {
    LOG.debug("exportURI: [{}]", exportURI);
    boolean success = false;
    int attempt = 0;
    final int attemptsMax = 3;
    do {
      attempt++;
      LOG.debug("{} attempt {}", JavaHelpers.getCurrentMethodName(), attempt);
      try {
        final URL url = URI.create(exportURI).toURL();
        final File file = new File(filePathName);
        // FileUtils.copyURLToFile(url, file)
        FileUtils.copyURLToFile(url, file, URL_TIMEOUT_CONNECTION, URL_TIMEOUT_READ);
        success = true;
      } catch (final Exception e) {
        LOG.error(
            "{} {}: Error Downloading File.",
            QAException.ERROR,
            JavaHelpers.getCurrentClassMethodName());
        if (attempt > attemptsMax) {
          throw new QAException("***ERROR***:[" + e.getMessage() + "]", e);
        }
      }
    } while (!success);
  }

  /**
   * @param filePathName
   * @return
   */
  public static String fileReadAll(String filePathName) {
    String contents = null;
    try {
      if (fileExists(filePathName)) {
        contents = new String(Files.readAllBytes(Paths.get(filePathName)));
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }
    return contents;
  }

  /**
   * @param filePathName
   * @return
   * @throws QAException
   */
  public static List<String> fileReadAllList(String filePathName) throws QAException {
    String line = null;
    final List<String> contents = new ArrayList<>();
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePathName))) {
      while ((line = bufferedReader.readLine()) != null) {
        contents.add(line);
      }
    } catch (IOException e) {
      throw new QAException("", e);
    }
    return contents;
  }

  /**
   * @param filePathName
   * @return
   */
  public static long fileSize(String filePathName) {
    return new File(filePathName).length();
  }

  /**
   * @param filePathName
   * @return
   */
  public static String fileValidateName(String filePathName) {
    final String replacer = "_";
    final Map<String, String> invalidCharacters = getCharactersInvalid();
    final Map<String, String> escapedCharacters = getCharactersEsacped();
    for (final Entry<String, String> entry : invalidCharacters.entrySet()) {
      final String replacee = entry.getValue();
      if (filePathName.contains(replacee)) {
        final String key = entry.getKey();
        LOG.debug("Replacing [{}: {}] with [{}]", key, replacee, replacer);
        if (escapedCharacters.containsKey(key)) {
          filePathName = replaceEscapedCharacter(filePathName, replacee, replacer);
        } else {
          filePathName = filePathName.replaceAll(replacee, replacer);
        }
      }
    }
    while (filePathName.contains(replacer + replacer)) {
      filePathName = filePathName.replaceAll(replacer + replacer, replacer);
    }
    return filePathName;
  }

  /**
   * @param filePathName
   * @param fileContent
   * @param append
   */
  public static void fileWrite(String filePathName, String fileContent, boolean append) {
    final String folderName =
        filePathName.substring(0, filePathName.lastIndexOf(Constants.DELIMETER_PATH));
    folderCreate(folderName);
    final File file = new File(filePathName);
    try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, append))) {
      if (append) {
        bufferedWriter.append(fileContent);
      } else {
        bufferedWriter.write(fileContent);
      }
    } catch (final IOException e) {
      LOG.error("Unable to write file: [{}]", filePathName);
    }
  }

  public static int filesGetCount(String path) {
    return filesList(path, false).size();
  }

  public static int filesGetCount(String path, boolean recursive) {
    return filesList(path, recursive).size();
  }

  public static int filesGetCount(String path, String extension) {
    return filesList(path, false, extension).size();
  }

  public static int filesGetCount(String path, boolean recursive, String extension) {
    return filesList(path, recursive, extension).size();
  }

  public static List<String> filesList(String path) {
    return filesList(path, false, null);
  }

  public static List<String> filesList(String path, boolean recursive) {
    final List<Path> pathList = new ArrayList<>();
    filesList(path, pathList, recursive, null);
    return Convert.fromListPathToListString(pathList);
  }

  public static List<String> filesList(String path, String extension) {
    return filesList(path, false, extension);
  }

  public static List<String> filesList(String pathName, boolean recursive, String extension) {
    FileFilter fileFilter = null;
    if (JavaHelpers.hasValue(extension)) {
      fileFilter = setFileFilter(extension);
    }
    final List<Path> pathList = new ArrayList<>();
    pathsList(pathName, pathList, recursive, fileFilter);
    return Convert.fromListPathToListString(pathList);
  }

  private static List<Path> filesList(
      String path, List<Path> fileList, boolean recursive, FileFilter fileFilter) {
    final File root = new File(path);
    File[] files = null;
    if (fileFilter == null) {
      files = root.listFiles();
    } else {
      files = root.listFiles(fileFilter);
    }
    if (files == null) {
      return fileList;
    }
    for (final File file : files) {
      if (file.isDirectory()) {
        if (recursive) {
          filesList(file.getAbsolutePath(), fileList, recursive, fileFilter);
        }
      } else {
        fileList.add(file.toPath());
      }
    }
    return fileList;
  }

  /**
   * Copies a folder recursively or copies a file. Creates a destination folder if it does not
   * exist.
   *
   * @param filePathNameSource source folder
   * @param filePathNameDestination destination folder
   * @throws IOException
   * @throws QAException
   */
  public static void folderCopy(File filePathNameSource, File filePathNameDestination)
      throws QAException {
    if (filePathNameSource.isDirectory()) {
      // if directory not exists, create it
      if (!filePathNameDestination.exists()) {
        filePathNameDestination.mkdir();
        // LOG.debug("Directory copied from {} to {}", src, dest);
      }
      // list all the directory contents
      final String[] files = filePathNameSource.list();
      for (final String file : files) {
        // construct the src and dest file structure
        final File srcFile = new File(filePathNameSource, file);
        final File destFile = new File(filePathNameDestination, file);
        // recursive copy
        folderCopy(srcFile, destFile);
      }
    } else {
      // if file, then copy it
      // Use bytes stream to support all file types
      try (InputStream inputStream = new FileInputStream(filePathNameSource)) {
        try (OutputStream outputStream = new FileOutputStream(filePathNameDestination)) {
          final byte[] buffer = new byte[1024];
          int length;
          // copy the file content in bytes
          while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
          }
        } catch (Exception e2) {
          throw new QAException("OutputStream", e2);
        }
      } catch (Exception e1) {
        throw new QAException("InputStream", e1);
      }
      // LOG.debug("File copied from {} to {}", src, dest);
    }
  }

  /**
   * @param folderPath
   * @return
   */
  public static boolean folderCreate(String folderPath) {
    File file = new File(folderPath);
    if (file.exists()) {
      return file.isDirectory();
    }
    file.mkdirs();
    return true;
  }

  /**
   * @param path
   * @return
   */
  public static boolean folderExists(String path) {
    final File file = new File(path);
    return file.exists() && file.isDirectory();
  }

  public static int foldersGetCount(String path) {
    return foldersList(path, false).size();
  }

  public static int foldersGetCount(String path, boolean recursive) {
    return foldersList(path, recursive).size();
  }

  public static List<String> foldersList(String path) {
    return foldersList(path, false);
  }

  public static List<String> foldersList(String path, boolean recursive) {
    final List<Path> pathList = new ArrayList<>();
    foldersList(path, pathList, recursive);
    return Convert.fromListPathToListString(pathList);
  }

  private static List<Path> foldersList(String path, List<Path> folderList, boolean recursive) {
    final File root = new File(path);
    final File[] files = root.listFiles();

    if (files == null) {
      return folderList;
    }
    for (final File file : files) {
      if (file.isDirectory()) {
        folderList.add(file.toPath());
        if (recursive) {
          foldersList(file.getAbsolutePath(), folderList, recursive);
        }
      }
    }
    return folderList;
  }

  public static Map<String, String> getCharactersEsacped() {
    final Map<String, String> map = new HashMap<>();
    map.put("asterisk", "*");
    map.put("back slash", Constants.BACKSLASH);
    map.put("dollar sign", "$");
    map.put("left bracket", "{");
    map.put("pipe", "|");
    map.put("plus sign", "+");
    map.put("question mark", "?");
    return map;
  }

  public static Map<String, String> getCharactersInvalid() {
    final Map<String, String> map = new HashMap<>();
    map.put("ampersand", "&");
    map.put("asterisk", "*");
    map.put("at sign", "@");
    map.put("back slash", Constants.BACKSLASH);
    map.put("backtick", "`");
    map.put("back space", Constants.BACKSPACE);
    map.put("carrage return", Constants.CR);
    map.put("colon", ":");
    map.put("dollar sign", "$");
    map.put("double quotes", Constants.QUOTE_DOUBLE);
    map.put("equal sign", "=");
    map.put("exclamation point", "!");
    map.put("form feed", Constants.FORMFEED);
    map.put("forward slash", "/");
    map.put("left angle bracket", "<");
    map.put("left bracket", "{");
    map.put("line feed", Constants.NL);
    map.put("new line", Constants.NEWLINE);
    map.put("percent", "%");
    map.put("pipe", "|");
    map.put("plus sign", "+");
    map.put("pound", "#");
    map.put("question mark", "?");
    map.put("right angle bracket", ">");
    map.put("right bracket", "}");
    map.put("single quotes", Constants.QUOTE_SINGLE);
    return map;
  }

  public static List<Path> pathsList(String path) {
    return pathsList(path, false, null);
  }

  public static List<Path> pathsList(String path, boolean recursive) {
    final List<Path> pathList = new ArrayList<>();
    pathsList(path, pathList, recursive, null);
    return pathList;
  }

  public static List<Path> pathsList(String path, String extension) {
    return pathsList(path, false, extension);
  }

  public static List<Path> pathsList(String path, boolean recursive, String extension) {
    FileFilter fileFilter = null;
    if (JavaHelpers.hasValue(extension)) {
      fileFilter = setFileFilter(extension);
    }
    final List<Path> pathList = new ArrayList<>();
    pathsList(path, pathList, recursive, fileFilter);
    return pathList;
  }

  private static List<Path> pathsList(
      String path, List<Path> pathsList, boolean recursive, FileFilter fileFilter) {
    final File root = new File(path);
    File[] files = null;
    if (fileFilter == null) {
      files = root.listFiles();
    } else {
      files = root.listFiles(fileFilter);
    }
    if (files == null) {
      return pathsList;
    }
    for (final File file : files) {
      if (file.isDirectory()) {
        if (recursive && fileFilter != null) {
          filesList(file.getAbsolutePath(), pathsList, recursive, fileFilter);
        }
      } else {
        pathsList.add(file.toPath());
      }
    }
    return pathsList;
  }

  /**
   * @param value
   * @param replacee
   * @param replacer
   * @return
   */
  private static String replaceEscapedCharacter(String value, String replacee, String replacer) {
    String valueNew = value;
    int index = valueNew.indexOf(replacee);
    while (index > -1) {
      if (index == 0) {
        valueNew = valueNew.substring(1, valueNew.length());
      } else {
        valueNew =
            valueNew.substring(0, index)
                + replacer
                + valueNew.substring(index + replacee.length(), valueNew.length());
      }
      index = valueNew.indexOf(replacee);
    }
    return valueNew;
  }

  /**
   * @param extension
   * @return
   */
  public static FileFilter setFileFilter(String extension) {
    return new FileFilter() {
      @Override
      public boolean accept(File fileName) {
        return Environment.noPMDUselessParentheses(
            fileName.isFile()
                && fileName
                    .getName()
                    .toLowerCase(Locale.ENGLISH)
                    .endsWith(extension.toLowerCase(Locale.ENGLISH)));
      }
    };
  }

  public static String[] sortByLastModified(List<String> filePathNameList, boolean ascending) {
    File[] fileArray = {};
    for (String filePathName : filePathNameList) {
      File file = new File(filePathName);
      fileArray = JavaHelpers.arrayAppend(fileArray, file);
    }
    fileArray = sortByLastModified(fileArray, ascending);
    String[] files = {};
    for (File file : fileArray) {
      files = JavaHelpers.arrayAppend(files, file.getPath());
    }
    return files;
  }

  /**
   * @param files
   * @param ascending
   * @return
   */
  public static File[] sortByLastModified(File[] files, boolean ascending) {
    if (ascending) {
      Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR);
    } else {
      Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
    }
    return files;
  }
}
