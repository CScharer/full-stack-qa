package com.cjs.qa.utilities;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.core.Environment;
import com.cjs.qa.core.QAException;
import com.cjs.qa.microsoft.excel.IExcel;
import com.cjs.qa.microsoft.excel.xls.XLS;

public final class Reporting {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(Reporting.class));

  private Reporting() {
    // Utility class - prevent instantiation
  }

  public static String getLogStatus(String report) {
    return Environment.noPMDUselessParentheses(
        "dateTimeStamp:["
            + DateHelpersTests.getCurrentDateTime(
                DateHelpersTests.FORMAT_US_STANDARD_DATE_TIME + ".SSS")
            + "], "
            + report
            + Constants.NEWLINE);
  }

  public static synchronized void updateReportExcel(
      String fileName, String value, List<Map<String, String>> results)
      throws Exception, QAException {
    LOG.debug("Acquiring Lock: fileName: [{}], value: [{}]", fileName, value);
    try {
      final String fileLock = CJSConstants.PATH_FILES_DATA + "Reporting.lck";
      try (RandomAccessFile randomAccessFile = new RandomAccessFile(fileLock, "rw");
          FileChannel fileChannel = randomAccessFile.getChannel()) {
        LOG.debug("Lock Acquired: fileName: [{}], value: [{}]", fileName, value);
        final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        final byte[] byteOut = value.getBytes();
        byteBuffer.put(byteOut);
        int bytesRead = fileChannel.read(byteBuffer);
        while (bytesRead != -1) {
          LOG.debug("Read {} bytes", bytesRead);
          byteBuffer.flip();
          while (byteBuffer.hasRemaining()) {
            // Removed System.out.print - use LOG.debug if character output needed
            fileChannel.write(byteBuffer);
          }
          byteBuffer.clear();
          bytesRead = fileChannel.read(byteBuffer);
        }
        writeReportExcel(fileName, value, results);
        LOG.debug("Report Written: fileName: [{}], value: [{}]", fileName, value);
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  private static void writeReportExcel(
      String fileName, String report, List<Map<String, String>> results)
      throws Exception, QAException {
    LOG.debug("Writing Report: [{}]", report);
    final Map<String, String> statusMap = results.get(0);
    final String sheetName = IExcel.SHEET_SUMMARY;
    final XLS excel = new XLS(fileName, sheetName);
    int row = 0;
    int column = 0;
    for (final String key : statusMap.keySet()) {
      excel.writeCell(sheetName, column, row, key);
      column++;
    }
    row = excel.getRowCount(sheetName) + 1;
    column = 0;
    for (final String key : statusMap.keySet()) {
      final String value = statusMap.get(key);
      excel.writeCell(sheetName, column, row, value);
      column++;
    }
    // excel.save();
    excel.close();
    FSOTests.fileWrite(fileName, getLogStatus(report), true);
  }
}
