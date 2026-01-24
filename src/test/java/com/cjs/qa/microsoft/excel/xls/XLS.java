package com.cjs.qa.microsoft.excel.xls;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.junit.jupiter.api.Assertions;

import com.cjs.qa.core.QAException;
import com.cjs.qa.microsoft.excel.IExcel;
import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.FSOTests;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.JavaHelpers;

public class XLS implements IExcel {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(XLS.class));

  private int sheetIndex = 0;
  private String fileName = null;
  private CreationHelper creationHelper = null;
  private HSSFWorkbook workbook = null;
  private HSSFSheet workSheet = null;
  private HSSFRow workRow = null;
  private HSSFCellStyle workCellStyle = null;
  private Cell workCell = null;

  public XLS(String fileName, String sheetName) throws QAException, IOException {
    final String fileNameExtensionExpected =
        "." + this.getClass().getSimpleName().toLowerCase(Locale.ENGLISH);
    setFileName(fileName);
    LOG.debug("fileName:[{}], sheet:[{}]", getFileName(), sheetName);
    if (!JavaHelpers.hasValue(sheetName)) {
      sheetName = SHEET_DEFAULT;
    }
    LOG.debug(
        "fileType Expected: [{}], fileName:[{}], sheet:[{}]",
        fileNameExtensionExpected,
        getFileName(),
        sheetName);
    final String fileNameExtensionActual = "." + FilenameUtils.getExtension(getFileName());
    if (!fileNameExtensionActual.equalsIgnoreCase(fileNameExtensionExpected)) {
      Assertions.fail(
          "The file ["
              + getFileName()
              + "] does not match the expected file extension of ["
              + fileNameExtensionExpected
              + "]");
    }
    createWorkbook(sheetName);
    setCellStyles();
    setWorkSheet(getWorkbook().getSheetAt(0));
  }

  public boolean addComment(int sheet, int column, int row, String sComment, boolean visible)
      throws QAException {
    setSheet(sheet);
    // Apache POI Drawing.createDrawingPatriarch() returns raw Drawing type
    // This is a limitation of the Apache POI API, not our code
    @SuppressWarnings("rawtypes")
    final Drawing drawing = getWorkCell().getSheet().createDrawingPatriarch();
    setCreationHelper(getWorkCell().getSheet().getWorkbook().getCreationHelper());
    final ClientAnchor clientAnchor = getCreationHelper().createClientAnchor();
    clientAnchor.setAnchorType(AnchorType.MOVE_AND_RESIZE);
    final Comment comment = drawing.createCellComment(clientAnchor);
    final RichTextString richTextString = getCreationHelper().createRichTextString(sComment);
    setWorkRow(getWorkSheet().getRow(row));
    if (getWorkRow() == null) {
      setWorkRow(getWorkSheet().createRow(row));
    }
    setWorkCell(getWorkRow().getCell(column));
    if (getWorkCell() == null) {
      setWorkCell(getWorkRow().createCell(column));
    }
    setWorkCell(getWorkRow().getCell(column));
    setWorkCellStyle((HSSFCellStyle) getWorkCell().getCellStyle());
    getWorkCell().setCellStyle(getWorkCellStyle());
    clientAnchor.setCol1(getWorkCell().getColumnIndex());
    clientAnchor.setCol2(getWorkCell().getColumnIndex() + 5);
    clientAnchor.setRow1(getWorkCell().getRowIndex());
    clientAnchor.setRow2(getWorkCell().getRowIndex() + 25);
    comment.setVisible(visible);
    comment.setString(richTextString);
    comment.setAuthor(Constants.CURRENT_USER);
    getWorkCell().setCellComment(comment);
    fileWrite();
    return true;
  }

  public boolean addComment(String sheet, int column, int row, String sComment, boolean visible)
      throws QAException {
    if (!sheetExists(sheet)) {
      setWorkSheet(createSheet(sheet));
      sheetIndex = getWorkbook().getSheetIndex(getWorkSheet());
      getWorkbook().getSheetAt(sheetIndex);
      getWorkbook().setActiveSheet(sheetIndex);
      setWorkCell(getWorkRow().createCell(column));
    }
    sheetIndex = getWorkbook().getSheetIndex(sheet);
    return addComment(sheetIndex, column, row, sComment, visible);
  }

  public boolean addLink(
      int sheet, int column, int row, String linkType, String linkText, String linkValue)
      throws QAException {
    Hyperlink hyperlink = null;
    final HyperlinkType hyperlinkType = IExcel.getHyperlinkType(linkType);
    if (!sheetExists(sheet)) {
      setWorkSheet(createSheet("Sheet" + sheet));
    }
    sheetIndex = getWorkbook().getSheetIndex(getWorkSheet());
    getWorkbook().getSheetAt(sheetIndex);
    getWorkbook().setActiveSheet(sheetIndex);
    setWorkRow(getWorkSheet().getRow(row));
    if (getWorkRow() == null) {
      setWorkRow(getWorkSheet().createRow(row));
    }
    setWorkCell(getWorkRow().getCell(column));
    if (getWorkCell() == null) {
      setWorkCell(getWorkRow().createCell(column));
    }
    setCellValue(linkText);
    setCreationHelper(getWorkbook().getCreationHelper());
    hyperlink = getCreationHelper().createHyperlink(hyperlinkType);
    hyperlink.setAddress(linkValue);
    getWorkCell().setHyperlink(hyperlink);
    getWorkCell().setCellStyle(XLSCellStyles.getCellStyle(getWorkbook(), FORMAT_NAME_HYPERLINK));
    //
    // final CellStyle cellStyleNew = getWorkbook().createCellStyle()
    // cellStyleNew.cloneStyleFrom(XLSCellStyles.getCellStyle(getWorkbook(),
    // IExcel.FORMAT_NAME_HYPERLINK))
    // getWorkCell().setCellStyle(cellStyleNew)
    return true;
  }

  public boolean addLink(
      String sheet, int column, int row, String linkType, String linkText, String linkValue)
      throws QAException {
    if (!sheetExists(sheet)) {
      setWorkSheet(getWorkbook().createSheet(sheet));
      setWorkCell(getWorkRow().createCell(column));
    }
    sheetIndex = getWorkbook().getSheetIndex(sheet);
    writeCell(sheet, column, row, linkText);
    return addLink(sheetIndex, column, row, linkType, linkText, linkValue);
  }

  public boolean autoSizeColumn(String sheet, int column) throws QAException {
    sheetIndex = getWorkbook().getSheetIndex(sheet);
    setWorkSheet(getWorkbook().getSheetAt(sheetIndex));
    getWorkSheet().autoSizeColumn(column);
    return true;
  }

  public boolean autoSizeColumns(String sheet) throws QAException {
    // , int column)
    final int columns = getColumnCount(sheet);
    for (int column = 0; column < columns; column++) {
      autoSizeColumn(sheet, column);
    }
    return true;
  }

  public void close() throws QAException, IOException {
    if (getWorkbook() != null) {
      // HSSFOptimiser.optimiseCellStyles(getWorkbook())
      // HSSFOptimiser.optimiseFonts(getWorkbook())
      fileWrite();
      getWorkbook().close();
    }
    setWorkbook(null);
    setWorkSheet(null);
    setWorkRow(null);
    setWorkCell(null);
  }

  public boolean columnExists(int sheet, int column, int row) throws QAException {
    if (sheetExists(sheet) && rowExists(sheet, row)) {
      setWorkCell(getWorkRow().getCell(column));
      if (getWorkCell() != null) {
        return true;
      }
    }
    return false;
  }

  public boolean columnExists(int sheet, String column, int row) throws QAException {
    Iterator<Cell> cellIterator = null;
    if (sheetExists(sheet) && rowExists(sheet, row)) {
      cellIterator = getWorkRow().cellIterator();
      while (cellIterator.hasNext()) {
        setWorkCell(cellIterator.next());
        final String columnName = getCellValue(getWorkCell());
        if (columnName.equals(column)) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean columnExists(String sheet, int column, int row) throws QAException {
    if (sheetExists(sheet) && rowExists(sheet, row)) {
      setWorkCell(getWorkRow().getCell(column));
      if (getWorkCell() != null) {
        return true;
      }
    }
    return false;
  }

  public boolean columnExists(String sheet, String column, int row) throws QAException {
    Iterator<Cell> cellIterator = null;
    if (sheetExists(sheet) && rowExists(sheet, row)) {
      cellIterator = getWorkRow().cellIterator();
      while (cellIterator.hasNext()) {
        setWorkCell(cellIterator.next());
        final String columnName = getCellValue(getWorkCell());
        if (columnName.equals(column)) {
          return true;
        }
      }
    }
    return false;
  }

  public void convertToCSV(String fileName) throws QAException {
    final String sheetName = getWorkSheet().getSheetName();
    final int rows = getRowCount(sheetName);
    final int columns = getColumnCount(sheetName);
    final StringBuilder stringBuilder = new StringBuilder();
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        String value = "";
        value = readCell(sheetName, column, row);
        if (value.contains(",")) {
          stringBuilder.append(Constants.QUOTE_DOUBLE + value + Constants.QUOTE_DOUBLE);
        } else {
          stringBuilder.append(value);
        }
        if (column < (columns - 1)) {
          stringBuilder.append(",");
        }
      }
      if (row < (rows - 1)) {
        stringBuilder.append(Constants.NEWLINE);
      }
    }
    FSOTests.fileWrite(fileName, stringBuilder.toString(), false);
  }

  public void createHeadings(String sheetName, List<String> headings) throws QAException {
    if (!sheetExists(sheetName)) {
      setWorkSheet(createSheet(sheetName));
    }
    sheetIndex = getWorkbook().getSheetIndex(sheetName);
    setWorkSheet(getWorkbook().getSheet(sheetName));
    for (int index = 0; index < headings.size(); index++) {
      setFormatHeading(sheetName, index, 0);
      final String heading = headings.get(index);
      writeRecord(index, 0, heading);
    }
  }

  public void createHeadings(String sheetName, String headings) throws QAException {
    final String[] headers = headings.split(Constants.DELIMETER_LIST);
    final List<String> list = new ArrayList<>();
    for (final String heading : headers) {
      list.add(heading);
    }
    createHeadings(sheetName, list);
  }

  public HSSFSheet createSheet(String sheet) throws QAException {
    if (!sheetExists(sheet)) {
      setWorkSheet(getWorkbook().createSheet(sheet));
    }
    // setWorkSheet(getWorkbook().createSheet(sheet))
    sheetIndex = getWorkbook().getSheetIndex(getWorkSheet());
    fileWrite();
    return getWorkSheet();
  }

  public void createSheet(String sheet, String contents) throws QAException {
    try {
      setWorkbook(new HSSFWorkbook(new FileInputStream(getFileName())));
      setWorkSheet(createWorkSheet(sheet, contents));
      LOG.debug("{}", getWorkSheet().toString());
      try (FileOutputStream fileOutputStream = new FileOutputStream(getFileName())) {
        getWorkbook().write(fileOutputStream);
        fileOutputStream.flush();
      }
      getWorkbook().close();
    } catch (final IOException e) {
      throw new QAException("Error Creating Sheet.", e);
    }
  }

  private void createWorkbook(String sheet) throws QAException, IOException {
    if (FSOTests.fileExists(getFileName())) {
      setWorkbook(new HSSFWorkbook(new FileInputStream(getFileName())));
      if (!sheetExists(sheet)) {
        setWorkSheet(createSheet(sheet));
      }
      sheetIndex = getWorkbook().getSheetIndex(getWorkSheet());
    } else {
      setWorkbook(new HSSFWorkbook());
      setWorkSheet(createSheet(sheet));
      sheetIndex = getWorkbook().getSheetIndex(getWorkSheet());
    }
    fileWrite();
  }

  private HSSFSheet createWorkSheet(String sheetName, String contents) throws QAException {
    final HSSFSheet sheet = getWorkbook().getSheet(sheetName);
    final String[] records = contents.split(Constants.NEWLINE);
    for (int rowIndex = 0; rowIndex < records.length; rowIndex++) {
      final String record = records[rowIndex];
      final Row row = sheet.createRow(rowIndex);
      final String[] columns = record.split(",");
      for (int columnIndex = 0; columnIndex < columns.length; columnIndex++) {
        final String cellValue = columns[columnIndex];
        if (!cellValue.isEmpty()) {
          setWorkCell(row.createCell(columnIndex));
          setCellValue(cellValue);
        }
      }
    }
    return sheet;
  }

  public boolean deleteSheet(int sheet) throws QAException {
    sheetIndex = getWorkbook().getSheetIndex(getWorkSheet());
    getWorkbook().removeSheetAt(sheetIndex);
    if (sheetIndex > 0) {
      sheetIndex--;
    }
    return true;
  }

  public boolean deleteSheet(String sheet) throws QAException {
    sheetIndex = getWorkbook().getSheetIndex(getWorkSheet());
    getWorkbook().removeSheetAt(sheetIndex);
    if (sheetIndex > 0) {
      sheetIndex--;
    }
    return true;
  }

  private void fileWrite() throws QAException {
    try {
      try (FileOutputStream fileOutputStream = new FileOutputStream(new File(getFileName()))) {
        getWorkbook().write(fileOutputStream);
      }
      //
      // setWorkbook(new HSSFWorkbook(new
      // FileInputStream(getFileName())))
      // setCellStyles()
      // setWorkSheet(getWorkbook().getSheetAt(sheetIndex))
      // getWorkbook().setActiveSheet(sheetIndex)
      //
    } catch (final IOException e) {
      throw new QAException("Error Writing to File [" + getFileName() + "].", e);
    }
  }

  private String getCellValue(Cell xCell) throws QAException {
    final FormulaEvaluator formulaEvaluator = null;
    return DATA_FORMATTER.formatCellValue(xCell, formulaEvaluator).trim();
  }

  public int getColumnCount(int sheet) throws QAException {
    int rows = 0;
    int columns = 0;
    if (!sheetExists(sheet)) {
      return columns;
    }
    int columnIndex = 0;
    setWorkSheet(getWorkbook().getSheetAt(sheetIndex));
    rows = getWorkSheet().getPhysicalNumberOfRows();
    for (int row = 0; row < rows; row++) {
      setWorkRow(getWorkSheet().getRow(row));
      columnIndex = getWorkRow().getLastCellNum();
      if (columnIndex > columns) {
        columns = columnIndex;
      }
    }
    return columns;
  }

  public int getColumnCount(String sheet) throws QAException {
    int columns = 0;
    if (!sheetExists(sheet)) {
      return columns;
    }
    int columnIndex = 0;
    sheetIndex = getWorkbook().getSheetIndex(sheet);
    setWorkSheet(getWorkbook().getSheetAt(sheetIndex));
    final int rows = getWorkSheet().getPhysicalNumberOfRows();
    for (int row = 0; row < rows; row++) {
      setWorkRow(getWorkSheet().getRow(row));
      if (getWorkRow() != null) {
        columnIndex = getWorkRow().getLastCellNum();
        if (columnIndex > columns) {
          columns = columnIndex;
        }
      }
    }
    return columns;
  }

  private CreationHelper getCreationHelper() {
    return creationHelper;
  }

  public String getFileName() {
    return fileName;
  }

  public int getRowCount(String sheetName) throws QAException {
    int rowCount = 0;
    if (sheetExists(sheetName)) {
      rowCount = getWorkSheet().getLastRowNum();
    }
    return rowCount;
  }

  public int getSheetCount() {
    return getWorkbook().getNumberOfSheets();
  }

  public HSSFWorkbook getWorkbook() {
    return workbook;
  }

  private Cell getWorkCell() {
    return workCell;
  }

  private HSSFCellStyle getWorkCellStyle() {
    return workCellStyle;
  }

  private HSSFRow getWorkRow() {
    return workRow;
  }

  private HSSFSheet getWorkSheet() {
    return workSheet;
  }

  public String readCell(int sheet, int column, int row) throws QAException {
    String value = null;
    if (sheetExists(sheet)) {
      setWorkRow(getWorkSheet().getRow(row));
      if (getWorkRow() != null) {
        setWorkCell(getWorkRow().getCell(column));
        if (getWorkCell() != null) {
          value = getCellValue(getWorkCell());
        }
      }
    }
    return value;
  }

  public String readCell(int sheet, String column, int row) throws QAException {
    Iterator<Cell> cellIterator = null;
    String value = null;
    int columnIndex = -1;
    if (sheetExists(sheet)) {
      setWorkRow(getWorkSheet().getRow(row));
      if (getWorkRow() != null) {
        cellIterator = getWorkRow().cellIterator();
        while (cellIterator.hasNext()) {
          setWorkCell(cellIterator.next());
          final String columnName = getCellValue(getWorkCell());
          if (columnName.equals(column)) {
            columnIndex = getWorkCell().getColumnIndex();
            break;
          }
        }
        if (columnIndex > -1) {
          // value = readCell(sheet, columnIndex, row)
          value = getCellValue(getWorkCell());
        }
      }
    }
    return value;
  }

  public String readCell(String sheet, int column, int row) throws QAException {
    String value = null;
    if (sheetExists(sheet)) {
      setWorkSheet(getWorkbook().getSheet(sheet));
      setWorkRow(getWorkSheet().getRow(row));
      if (getWorkRow() != null) {
        setWorkCell(getWorkRow().getCell(column));
        if (getWorkCell() != null) {
          value = getCellValue(getWorkCell());
        }
      }
    }
    return value;
  }

  public String readCell(String sheet, String column, int row) throws QAException {
    Iterator<Cell> cellIterator = null;
    String value = null;
    int columnIndex = -1;
    if (sheetExists(sheet)) {
      setWorkSheet(getWorkbook().getSheet(sheet));
      setWorkRow(getWorkSheet().getRow(row));
      if (getWorkRow() != null) {
        cellIterator = getWorkRow().cellIterator();
        while (cellIterator.hasNext()) {
          setWorkCell(cellIterator.next());
          final String columnName = getCellValue(getWorkCell());
          if (columnName.equals(column)) {
            columnIndex = getWorkCell().getColumnIndex();
            break;
          }
        }
        if (columnIndex > -1) {
          // value = readCell(sheet, columnIndex, row)
          value = getCellValue(getWorkCell());
        }
      }
    }
    return value;
  }

  public boolean renameSheet(int sheet, String sheetNew) throws QAException {
    getWorkbook().setSheetName(sheet, sheetNew);
    return true;
  }

  public boolean renameSheet(String sheet, String sheetNew) throws QAException {
    sheetIndex = getWorkbook().getSheetIndex(sheet);
    return renameSheet(sheetIndex, sheetNew);
  }

  public boolean rowExists(int sheet, int row) throws QAException {
    if (sheetExists(sheet)) {
      setWorkRow(getWorkSheet().getRow(row));
      if (getWorkRow() != null) {
        return true;
      }
    }
    return false;
  }

  public boolean rowExists(String sheet, int row) throws QAException {
    if (sheetExists(sheet)) {
      setWorkRow(getWorkSheet().getRow(row));
      if (getWorkRow() != null) {
        return true;
      }
    }
    return false;
  }

  public void save() throws QAException {
    fileWrite();
  }

  public boolean setCellAlignment(String sheet, int column, int row) throws QAException {
    sheetIndex = getWorkbook().getSheetIndex(sheet);
    setWorkSheet(getWorkbook().getSheetAt(sheetIndex));
    setWorkCellStyle((HSSFCellStyle) getWorkCell().getCellStyle());
    getWorkCellStyle().setVerticalAlignment(VerticalAlignment.TOP);
    return true;
  }

  public boolean setCellHeight(String sheet, int column, int row, int height) throws QAException {
    sheetIndex = getWorkbook().getSheetIndex(sheet);
    setWorkSheet(getWorkbook().getSheetAt(sheetIndex));
    return true;
  }

  private void setCellStyles() throws QAException {
    XLSCellStyles.setCellStyles(getWorkbook());
  }

  private void setCellValue(Object value) throws QAException {
    // Java 17: Pattern matching for instanceof
    if (value instanceof Boolean boolValue) {
      getWorkCell().setCellValue(boolValue);
    }
    if (value instanceof Double doubleValue) {
      getWorkCell().setCellValue(doubleValue);
      getWorkCell().setCellStyle(XLSCellStyles.getCellStyle(getWorkbook(), FORMAT_NAME_NUMBER));
    }
    if (value instanceof Float floatValue) {
      getWorkCell().setCellValue(floatValue);
      getWorkCell().setCellStyle(XLSCellStyles.getCellStyle(getWorkbook(), FORMAT_NAME_NUMBER));
    }
    if (value instanceof Integer intValue) {
      getWorkCell().setCellValue(intValue);
      getWorkCell().setCellStyle(XLSCellStyles.getCellStyle(getWorkbook(), FORMAT_NAME_NUMBER));
    }
    if (value instanceof String stringValue) {
      boolean isNumeric = false;
      boolean containsPercentSign = false;
      if (stringValue.contains("%")) {
        containsPercentSign = true;
      }
      if (!containsPercentSign && stringValue.contains(".")) {
        isNumeric = JavaHelpers.isValidStringNumber(stringValue);
      }
      final float floatZero = 0;
      float valueFloat = 0;
      try {
        if (containsPercentSign || isNumeric) {
          // valueFloat = Float.parseFloat(stringValue);
          if (containsPercentSign) {
            final String stringValueNoPercent = stringValue.replace("%", "");
            valueFloat = Float.valueOf(stringValueNoPercent.trim()).floatValue();
            if (valueFloat != floatZero) {
              valueFloat = valueFloat / 100;
            }
            setCellValue(valueFloat);
            getWorkCell()
                .setCellStyle(XLSCellStyles.getCellStyle(getWorkbook(), FORMAT_NAME_PERCENT));
          } else {
            valueFloat = Float.valueOf(stringValue.trim()).floatValue();
            setCellValue(valueFloat);
            getWorkCell()
                .setCellStyle(XLSCellStyles.getCellStyle(getWorkbook(), FORMAT_NAME_NUMBERPERCENT));
          }
        } else {
          getWorkCell().setCellValue(Integer.valueOf(stringValue));
          getWorkCell().setCellStyle(XLSCellStyles.getCellStyle(getWorkbook(), FORMAT_NAME_NUMBER));
        }
      } catch (final Exception e) {
        getWorkCell().setCellValue(stringValue);
      }
    }
  }

  public boolean setCellWidth(String sheet, int column, int row, int width) throws QAException {
    sheetIndex = getWorkbook().getSheetIndex(sheet);
    setWorkSheet(getWorkbook().getSheetAt(sheetIndex));
    return true;
  }

  public boolean setCellWrap(String sheet, int column, int row, boolean wrap) throws QAException {
    sheetIndex = getWorkbook().getSheetIndex(sheet);
    setWorkSheet(getWorkbook().getSheetAt(sheetIndex));
    setWorkCellStyle((HSSFCellStyle) getWorkCell().getCellStyle());
    getWorkCellStyle().setWrapText(wrap);
    getWorkCell().setCellStyle(getWorkCellStyle());
    return true;
  }

  private void setCreationHelper(CreationHelper creationHelper) {
    this.creationHelper = creationHelper;
  }

  private void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public void setFormatBold(int sheet, int column, int row) throws QAException {
    setWorkRow(getWorkSheet().getRow(row));
    if (getWorkRow() == null) {
      setWorkRow(getWorkSheet().createRow(row));
    }
    setWorkCell(getWorkRow().getCell(column));
    if (getWorkCell() == null) {
      setWorkCell(getWorkRow().createCell(column));
    }
    setWorkCell(getWorkRow().getCell(column));
    getWorkCell().setCellStyle(XLSCellStyles.getCellStyle(getWorkbook(), FORMAT_NAME_BOLD));
  }

  public void setFormatBold(String sheet, int column, int row) throws QAException {
    if (!sheetExists(sheet)) {
      setWorkCell(getWorkRow().createCell(column));
    }
    sheetIndex = getWorkbook().getSheetIndex(sheet);
    setFormatBold(sheetIndex, column, row);
  }

  public void setFormatFail(int sheet, int column, int row) throws QAException {
    setWorkRow(getWorkSheet().getRow(row));
    if (getWorkRow() == null) {
      setWorkRow(getWorkSheet().createRow(row));
    }
    setWorkCell(getWorkRow().getCell(column));
    if (getWorkCell() == null) {
      setWorkCell(getWorkRow().createCell(column));
    }
    setWorkCell(getWorkRow().getCell(column));
    getWorkCell().setCellStyle(XLSCellStyles.getCellStyle(getWorkbook(), FORMAT_NAME_FAIL));
  }

  public void setFormatFail(String sheet, int column, int row) throws QAException {
    if (!sheetExists(sheet)) {
      setWorkCell(getWorkRow().createCell(column));
    }
    sheetIndex = getWorkbook().getSheetIndex(sheet);
    setFormatFail(sheetIndex, column, row);
  }

  public boolean setFormatFontBackgroundColor(int sheet, int column, int row, String color)
      throws QAException {
    if (!columnExists(sheet, column, row)) {
      return false;
    }
    if (!COLORS_ALLOWED.contains(color.toLowerCase(Locale.ENGLISH))) {
      return false;
    }
    setWorkCell(getWorkRow().getCell(column));
    getWorkCell().setCellStyle(XLSCellStyles.getCellStyle(getWorkbook(), FORMAT_NAME_HEADING));
    return true;
  }

  public boolean setFormatFontBackgroundColor(String sheet, int column, int row, String color)
      throws QAException {
    if (!sheetExists(sheet)) {
      return false;
    }
    sheetIndex = getWorkbook().getSheetIndex(sheet);
    return setFormatFontBackgroundColor(sheetIndex, column, row, color);
  }

  public boolean setFormatFontColor(int sheet, int column, int row, String color)
      throws QAException {
    if (!columnExists(sheet, column, row)) {
      return false;
    }
    if (!COLORS_ALLOWED.contains(color.toLowerCase(Locale.ENGLISH))) {
      return false;
    }
    setWorkCell(getWorkRow().getCell(column));
    setWorkCellStyle(
        (HSSFCellStyle) XLSCellStyles.getCellStyle(getWorkbook(), FORMAT_NAME_HYPERLINK));
    final HSSFFont hssfFont = getWorkbook().createFont();
    hssfFont.setColor(IExcel.getFontColorIndex(color));
    hssfFont.setBold(true);
    getWorkCellStyle().setFont(hssfFont);
    getWorkCell().setCellStyle(getWorkCellStyle());
    return true;
  }

  public boolean setFormatFontColor(String sheet, int column, int row, String color)
      throws QAException {
    if (!sheetExists(sheet)) {
      return false;
    }
    sheetIndex = getWorkbook().getSheetIndex(sheet);
    return setFormatFontColor(sheetIndex, column, row, color);
  }

  public void setFormatHeading(int sheet, int column, int row) throws QAException {
    setWorkRow(getWorkSheet().getRow(row));
    if (getWorkRow() == null) {
      setWorkRow(getWorkSheet().createRow(row));
    }
    setWorkCell(getWorkRow().getCell(column));
    if (getWorkCell() == null) {
      setWorkCell(getWorkRow().createCell(column));
    }
    setWorkCell(getWorkRow().getCell(column));
    getWorkCell().setCellStyle(XLSCellStyles.getCellStyle(getWorkbook(), FORMAT_NAME_HEADING));
  }

  public void setFormatHeading(String sheet, int column, int row) throws QAException {
    if (!sheetExists(sheet)) {
      setWorkCell(getWorkRow().createCell(column));
    }
    sheetIndex = getWorkbook().getSheetIndex(sheet);
    setFormatHeading(sheetIndex, column, row);
  }

  public void setFormatHyperlink(int sheet, int column, int row) throws QAException {
    setWorkRow(getWorkSheet().getRow(row));
    if (getWorkRow() == null) {
      setWorkRow(getWorkSheet().createRow(row));
    }
    setWorkCell(getWorkRow().getCell(column));
    if (getWorkCell() == null) {
      setWorkCell(getWorkRow().createCell(column));
    }
    setWorkCell(getWorkRow().getCell(column));
    getWorkCell().setCellStyle(XLSCellStyles.getCellStyle(getWorkbook(), FORMAT_NAME_HYPERLINK));
  }

  public void setFormatHyperlink(String sheet, int column, int row) throws QAException {
    if (!sheetExists(sheet)) {
      setWorkCell(getWorkRow().createCell(column));
    }
    sheetIndex = getWorkbook().getSheetIndex(sheet);
    setFormatHyperlink(sheetIndex, column, row);
  }

  public void setFormatNormal(int sheet, int column, int row) throws QAException {
    setWorkRow(getWorkSheet().getRow(row));
    if (getWorkRow() == null) {
      setWorkRow(getWorkSheet().createRow(row));
    }
    setWorkCell(getWorkRow().getCell(column));
    if (getWorkCell() == null) {
      setWorkCell(getWorkRow().createCell(column));
    }
    setWorkCell(getWorkRow().getCell(column));
    getWorkCell().setCellStyle(XLSCellStyles.getCellStyle(getWorkbook(), FORMAT_NAME_NORMAL));
  }

  public void setFormatNormal(String sheet, int column, int row) throws QAException {
    if (!sheetExists(sheet)) {
      setWorkCell(getWorkRow().createCell(column));
    }
    sheetIndex = getWorkbook().getSheetIndex(sheet);
    setFormatNormal(sheetIndex, column, row);
  }

  public void setFormatPass(int sheet, int column, int row) throws QAException {
    setWorkRow(getWorkSheet().getRow(row));
    if (getWorkRow() == null) {
      setWorkRow(getWorkSheet().createRow(row));
    }
    setWorkCell(getWorkRow().getCell(column));
    if (getWorkCell() == null) {
      setWorkCell(getWorkRow().createCell(column));
    }
    setWorkCell(getWorkRow().getCell(column));
    getWorkCell().setCellStyle(XLSCellStyles.getCellStyle(getWorkbook(), FORMAT_NAME_PASS));
  }

  public void setFormatPass(String sheet, int column, int row) throws QAException {
    if (!sheetExists(sheet)) {
      setWorkCell(getWorkRow().createCell(column));
    }
    sheetIndex = getWorkbook().getSheetIndex(sheet);
    setFormatPass(sheetIndex, column, row);
  }

  public void setFormatSection(int sheet, int column, int row) throws QAException {
    setWorkRow(getWorkSheet().getRow(row));
    if (getWorkRow() == null) {
      setWorkRow(getWorkSheet().createRow(row));
    }
    setWorkCell(getWorkRow().getCell(column));
    if (getWorkCell() == null) {
      setWorkCell(getWorkRow().createCell(column));
    }
    setWorkCell(getWorkRow().getCell(column));
    getWorkCell().setCellStyle(XLSCellStyles.getCellStyle(getWorkbook(), FORMAT_NAME_SECTION));
  }

  public void setFormatSection(String sheet, int column, int row) throws QAException {
    if (!sheetExists(sheet)) {
      setWorkCell(getWorkRow().createCell(column));
    }
    sheetIndex = getWorkbook().getSheetIndex(sheet);
    setFormatSection(sheetIndex, column, row);
  }

  public void setFormatStatus(int sheet, int column, int row) throws QAException {
    setWorkRow(getWorkSheet().getRow(row));
    if (getWorkRow() == null) {
      setWorkRow(getWorkSheet().createRow(row));
    }
    setWorkCell(getWorkRow().getCell(column));
    if (getWorkCell() == null) {
      setWorkCell(getWorkRow().createCell(column));
    }
    setWorkCell(getWorkRow().getCell(column));
    getWorkCell().setCellStyle(XLSCellStyles.getCellStyle(getWorkbook(), FORMAT_NAME_STATUS));
  }

  public void setFormatStatus(String sheet, int column, int row) throws QAException {
    if (!sheetExists(sheet)) {
      setWorkCell(getWorkRow().createCell(column));
    }
    sheetIndex = getWorkbook().getSheetIndex(sheet);
    setFormatStatus(sheetIndex, column, row);
  }

  public void setSheet(int sheet) throws QAException {
    sheetIndex = sheet;
    setWorkSheet(getWorkbook().getSheetAt(sheetIndex));
    getWorkbook().getSheetAt(sheetIndex);
    getWorkbook().setActiveSheet(sheetIndex);
    getWorkSheet().setSelected(true);
  }

  public void setSheet(String sheet) throws QAException {
    sheetIndex = getWorkbook().getSheetIndex(sheet);
    setSheet(sheetIndex);
  }

  private void setWorkbook(HSSFWorkbook workbook) {
    this.workbook = workbook;
  }

  private void setWorkCell(Cell workCell) {
    this.workCell = workCell;
  }

  private void setWorkCellStyle(HSSFCellStyle workCellStyle) {
    this.workCellStyle = workCellStyle;
  }

  private void setWorkRow(HSSFRow workRow) {
    this.workRow = workRow;
  }

  private void setWorkSheet(HSSFSheet workSheet) {
    this.workSheet = workSheet;
  }

  public boolean sheetExists(int sheet) throws QAException {
    if (getWorkbook().getNumberOfSheets() >= sheet) {
      setWorkSheet(getWorkbook().getSheetAt(sheet));
      return true;
    }
    return false;
  }

  public boolean sheetExists(String sheet) throws QAException {
    setWorkSheet(getWorkbook().getSheet(sheet));
    return getWorkSheet() != null;
  }

  public String writeCell(String sheetName, int column, int row, Object value) throws QAException {
    if ("".equals(value)) {
      return String.valueOf(value);
    }
    if (!sheetExists(sheetName)) {
      setWorkSheet(createSheet(sheetName));
    }
    setSheet(sheetName);
    setWorkRow(getWorkSheet().getRow(row));
    if (getWorkRow() == null) {
      setWorkRow(getWorkSheet().createRow(row));
    }
    setWorkCell(getWorkRow().getCell(column));
    if (getWorkCell() == null) {
      setWorkCell(getWorkRow().createCell(column));
    }
    setCellValue(value);
    return getCellValue(getWorkCell());
  }

  public String writeCellFormula(String sheetName, int column, int row, String value)
      throws QAException {
    if (value.isEmpty()) {
      return String.valueOf(value);
    }
    if (!sheetExists(sheetName)) {
      setWorkSheet(createSheet(sheetName));
    }
    setWorkSheet(getWorkbook().getSheet(sheetName));
    setSheet(sheetName);
    setWorkRow(getWorkSheet().getRow(row));
    if (getWorkRow() == null) {
      setWorkRow(getWorkSheet().createRow(row));
    }
    setWorkCell(getWorkRow().getCell(column));
    if (getWorkCell() == null) {
      setWorkCell(getWorkRow().createCell(column));
    }
    // setCellType() is deprecated - setCellFormula() automatically sets cell type to FORMULA
    getWorkCell().setCellFormula(value);
    return getCellValue(getWorkCell());
  }

  private boolean writeRecord(int column, int row, String value) throws QAException {
    setWorkRow(getWorkSheet().getRow(row));
    if (getWorkRow() == null) {
      setWorkRow(getWorkSheet().createRow(row));
    }
    setWorkCell(getWorkRow().getCell(column));
    if (getWorkCell() == null) {
      setWorkCell(getWorkRow().createCell(column));
    }
    setCellValue(value);
    return true;
  }
}
