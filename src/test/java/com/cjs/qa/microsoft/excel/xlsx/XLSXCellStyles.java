package com.cjs.qa.microsoft.excel.xlsx;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cjs.qa.core.QAException;
import com.cjs.qa.microsoft.excel.IExcel;
import com.cjs.qa.utilities.JavaHelpers;

public class XLSXCellStyles {

  public static final int CELL_STYLE_INDEX_START = 1;
  public static final int CELL_STYLE_INDEX_DEFINED =
      CELL_STYLE_INDEX_START + IExcel.CELL_STYLE_DEFINED_COUNT;
  private static List<XLSXCellStyles> stylesList = new ArrayList<>();
  private Integer styleIndex;
  private String styleName;
  private CellStyle styleObject;

  /** Default constructor for data binding and style management. */
  public XLSXCellStyles() {
    // Default constructor for data binding
  }

  public XLSXCellStyles(int styleIndex, String styleName, CellStyle styleObject)
      throws QAException {
    this.styleIndex = styleIndex;
    this.styleName = styleName;
    this.styleObject = styleObject;
  }

  public Integer getStyleIndex() throws QAException {
    return styleIndex;
  }

  public String getStyleName() throws QAException {
    return styleName;
  }

  public CellStyle getStyleObject() throws QAException {
    return styleObject;
  }

  public static List<XLSXCellStyles> getStylesList() throws QAException {
    return stylesList;
  }

  public void setStyleIndex(Integer styleIndex) throws QAException {
    this.styleIndex = styleIndex;
  }

  public void setStyleName(String styleName) throws QAException {
    this.styleName = styleName;
  }

  public void setStyleObject(CellStyle styleObject) throws QAException {
    this.styleObject = styleObject;
  }

  public void setStylesList(List<XLSXCellStyles> stylesListParam) throws QAException {
    stylesList = stylesListParam;
  }

  public static void createCellStyleBold(XSSFWorkbook workbook) throws QAException {
    final String cellStyleName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_STYLE, "");
    final int styleIndex = workbook.getNumCellStyles();
    final CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFont(XLSXCellFonts.getCellFont(cellStyleName));
    getStylesList().add(new XLSXCellStyles(styleIndex, cellStyleName, cellStyle));
  }

  public static void createCellStyleHeading(XSSFWorkbook workbook) throws QAException {
    final String cellStyleName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_STYLE, "");
    final int styleIndex = workbook.getNumCellStyles();
    final CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFillForegroundColor(IExcel.getFontColorIndex("grey_25"));
    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    cellStyle.setFont(XLSXCellFonts.getCellFont(cellStyleName));
    getStylesList().add(new XLSXCellStyles(styleIndex, cellStyleName, cellStyle));
  }

  public static void createCellStyleHyperlink(XSSFWorkbook workbook) throws QAException {
    final String cellStyleName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_STYLE, "");
    final int styleIndex = workbook.getNumCellStyles();
    final CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFont(XLSXCellFonts.getCellFont(cellStyleName));
    getStylesList().add(new XLSXCellStyles(styleIndex, cellStyleName, cellStyle));
  }

  public static void createCellStyleNormal(XSSFWorkbook workbook) throws QAException {
    final String cellStyleName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_STYLE, "");
    final int styleIndex = workbook.getNumCellStyles();
    final CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFont(XLSXCellFonts.getCellFont(cellStyleName));
    getStylesList().add(new XLSXCellStyles(styleIndex, cellStyleName, cellStyle));
  }

  public static void createCellStylePass(XSSFWorkbook workbook) throws QAException {
    final String cellStyleName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_STYLE, "");
    final int styleIndex = workbook.getNumCellStyles();
    final CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFont(XLSXCellFonts.getCellFont(cellStyleName));
    getStylesList().add(new XLSXCellStyles(styleIndex, cellStyleName, cellStyle));
  }

  public static void createCellStyleFail(XSSFWorkbook workbook) throws QAException {
    final String cellStyleName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_STYLE, "");
    final int styleIndex = workbook.getNumCellStyles();
    final CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFont(XLSXCellFonts.getCellFont(cellStyleName));
    getStylesList().add(new XLSXCellStyles(styleIndex, cellStyleName, cellStyle));
  }

  public static void createCellStyleSection(XSSFWorkbook workbook) throws QAException {
    final String cellStyleName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_STYLE, "");
    final int styleIndex = workbook.getNumCellStyles();
    final CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFillForegroundColor(IExcel.getFontColorIndex("grey_25"));
    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    cellStyle.setFont(XLSXCellFonts.getCellFont(cellStyleName));
    getStylesList().add(new XLSXCellStyles(styleIndex, cellStyleName, cellStyle));
  }

  public static void createCellStyleStatus(XSSFWorkbook workbook) throws QAException {
    final String cellStyleName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_STYLE, "");
    final int styleIndex = workbook.getNumCellStyles();
    final CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFillForegroundColor(IExcel.getFontColorIndex("light green"));
    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    cellStyle.setFont(XLSXCellFonts.getCellFont(cellStyleName));
    getStylesList().add(new XLSXCellStyles(styleIndex, cellStyleName, cellStyle));
  }

  public static void createCellStyleNumber(XSSFWorkbook workbook) throws QAException {
    final String cellStyleName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_STYLE, "");
    final int styleIndex = workbook.getNumCellStyles();
    final CellStyle cellStyle = workbook.createCellStyle();
    final DataFormat dataFormat = workbook.createDataFormat();
    cellStyle.setDataFormat(dataFormat.getFormat("#,###,###,##0"));
    cellStyle.setFont(XLSXCellFonts.getCellFont(cellStyleName));
    getStylesList().add(new XLSXCellStyles(styleIndex, cellStyleName, cellStyle));
  }

  public static void createCellStyleNumberPercent(XSSFWorkbook workbook) throws QAException {
    final String cellStyleName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_STYLE, "");
    final int styleIndex = workbook.getNumCellStyles();
    final CellStyle cellStyle = workbook.createCellStyle();
    final DataFormat dataFormat = workbook.createDataFormat();
    cellStyle.setDataFormat(dataFormat.getFormat("0.00"));
    cellStyle.setFont(XLSXCellFonts.getCellFont(cellStyleName));
    getStylesList().add(new XLSXCellStyles(styleIndex, cellStyleName, cellStyle));
  }

  public static void createCellStylePercent(XSSFWorkbook workbook) throws QAException {
    final String cellStyleName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_STYLE, "");
    final int styleIndex = workbook.getNumCellStyles();
    final CellStyle cellStyle = workbook.createCellStyle();
    final DataFormat dataFormat = workbook.createDataFormat();
    cellStyle.setDataFormat(dataFormat.getFormat("0.00%"));
    cellStyle.setFont(XLSXCellFonts.getCellFont(cellStyleName));
    getStylesList().add(new XLSXCellStyles(styleIndex, cellStyleName, cellStyle));
  }

  public static void setCellStyles(XSSFWorkbook workbook) throws QAException {
    switch (workbook.getNumCellStyles()) {
      case CELL_STYLE_INDEX_START:
        // Styles & Fonts do not exist.
        XLSXCellFonts.createCellFonts(workbook);
        createCellStyleBold(workbook);
        createCellStyleHeading(workbook);
        createCellStyleHyperlink(workbook);
        createCellStyleNormal(workbook);
        createCellStylePass(workbook);
        createCellStyleFail(workbook);
        createCellStyleSection(workbook);
        createCellStyleStatus(workbook);
        createCellStyleNumber(workbook);
        createCellStyleNumberPercent(workbook);
        createCellStylePercent(workbook);
        break;
      case CELL_STYLE_INDEX_DEFINED:
        // Styles & Fonts already exist.
        for (int cellStyleIndex = 0;
            cellStyleIndex < IExcel.CELL_STYLE_LIST.size() - 1;
            cellStyleIndex++) {
          final int workbookStyleIndex = CELL_STYLE_INDEX_START + cellStyleIndex;
          final String cellStyleType = IExcel.CELL_STYLE_LIST.get(cellStyleIndex);
          final CellStyle cellStyle =
              workbook.getCellStyleAt(CELL_STYLE_INDEX_START + cellStyleIndex);
          getStylesList().add(new XLSXCellStyles(workbookStyleIndex, cellStyleType, cellStyle));
        }
        break;
      default:
        break;
    }
  }

  public static CellStyle getCellStyle(XSSFWorkbook xssfWorkbook, String styleName)
      throws QAException {
    for (final XLSXCellStyles cellStyle : getStylesList()) {
      if (cellStyle.getStyleName().equals(styleName)) {
        return xssfWorkbook.getCellStyleAt(cellStyle.getStyleIndex());
      }
    }
    return null;
  }
}
