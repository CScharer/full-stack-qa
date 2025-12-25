package com.cjs.qa.microsoft.excel.xls;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;

import com.cjs.qa.core.QAException;
import com.cjs.qa.microsoft.excel.IExcel;
import com.cjs.qa.utilities.JavaHelpers;

public class XLSCellStyles {

  public static final int CELL_STYLE_INDEX_START = 21;
  public static final int CELL_STYLE_INDEX_DEFINED =
      CELL_STYLE_INDEX_START + IExcel.CELL_STYLE_DEFINED_COUNT;
  private static List<XLSCellStyles> stylesList = new ArrayList<>();
  private Integer styleIndex;
  private String styleName;
  private CellStyle styleObject;

  /** Default constructor for data binding and style management. */
  public XLSCellStyles() {
    // Default constructor for data binding
  }

  public XLSCellStyles(int styleIndex, String styleName, CellStyle styleObject) throws QAException {
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

  public static List<XLSCellStyles> getStylesList() throws QAException {
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

  public void setStylesList(List<XLSCellStyles> stylesList) throws QAException {
    XLSCellStyles.stylesList = stylesList;
  }

  public static void createCellStyleBold(HSSFWorkbook workbook) throws QAException {
    final String cellStyleName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_STYLE, "");
    final int styleIndex = workbook.getNumCellStyles();
    final CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFont(XLSCellFonts.getCellFont(cellStyleName));
    getStylesList().add(new XLSCellStyles(styleIndex, cellStyleName, cellStyle));
  }

  public static void createCellStyleHeading(HSSFWorkbook workbook) throws QAException {
    final String cellStyleName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_STYLE, "");
    final int styleIndex = workbook.getNumCellStyles();
    final CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFillForegroundColor(IExcel.getFontColorIndex("grey_25"));
    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    cellStyle.setFont(XLSCellFonts.getCellFont(cellStyleName));
    getStylesList().add(new XLSCellStyles(styleIndex, cellStyleName, cellStyle));
  }

  public static void createCellStyleHyperlink(HSSFWorkbook workbook) throws QAException {
    final String cellStyleName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_STYLE, "");
    final int styleIndex = workbook.getNumCellStyles();
    final CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFont(XLSCellFonts.getCellFont(cellStyleName));
    getStylesList().add(new XLSCellStyles(styleIndex, cellStyleName, cellStyle));
  }

  public static void createCellStyleNormal(HSSFWorkbook workbook) throws QAException {
    final String cellStyleName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_STYLE, "");
    final int styleIndex = workbook.getNumCellStyles();
    final CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFont(XLSCellFonts.getCellFont(cellStyleName));
    getStylesList().add(new XLSCellStyles(styleIndex, cellStyleName, cellStyle));
  }

  public static void createCellStylePass(HSSFWorkbook workbook) throws QAException {
    final String cellStyleName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_STYLE, "");
    final int styleIndex = workbook.getNumCellStyles();
    final CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFont(XLSCellFonts.getCellFont(cellStyleName));
    getStylesList().add(new XLSCellStyles(styleIndex, cellStyleName, cellStyle));
  }

  public static void createCellStyleFail(HSSFWorkbook workbook) throws QAException {
    final String cellStyleName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_STYLE, "");
    final int styleIndex = workbook.getNumCellStyles();
    final CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFont(XLSCellFonts.getCellFont(cellStyleName));
    getStylesList().add(new XLSCellStyles(styleIndex, cellStyleName, cellStyle));
  }

  public static void createCellStyleSection(HSSFWorkbook workbook) throws QAException {
    final String cellStyleName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_STYLE, "");
    final int styleIndex = workbook.getNumCellStyles();
    final CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFillForegroundColor(IExcel.getFontColorIndex("grey_25"));
    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    cellStyle.setFont(XLSCellFonts.getCellFont(cellStyleName));
    getStylesList().add(new XLSCellStyles(styleIndex, cellStyleName, cellStyle));
  }

  public static void createCellStyleStatus(HSSFWorkbook workbook) throws QAException {
    final String cellStyleName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_STYLE, "");
    final int styleIndex = workbook.getNumCellStyles();
    final CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFillForegroundColor(IExcel.getFontColorIndex("light green"));
    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    cellStyle.setFont(XLSCellFonts.getCellFont(cellStyleName));
    getStylesList().add(new XLSCellStyles(styleIndex, cellStyleName, cellStyle));
  }

  public static void createCellStyleNumber(HSSFWorkbook workbook) throws QAException {
    final String cellStyleName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_STYLE, "");
    final int styleIndex = workbook.getNumCellStyles();
    final CellStyle cellStyle = workbook.createCellStyle();
    final DataFormat dataFormat = workbook.createDataFormat();
    cellStyle.setDataFormat(dataFormat.getFormat("#,###,###,##0"));
    cellStyle.setFont(XLSCellFonts.getCellFont(cellStyleName));
    getStylesList().add(new XLSCellStyles(styleIndex, cellStyleName, cellStyle));
  }

  public static void createCellStyleNumberPercent(HSSFWorkbook workbook) throws QAException {
    final String cellStyleName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_STYLE, "");
    final int styleIndex = workbook.getNumCellStyles();
    final CellStyle cellStyle = workbook.createCellStyle();
    final DataFormat dataFormat = workbook.createDataFormat();
    cellStyle.setDataFormat(dataFormat.getFormat("0.00"));
    cellStyle.setFont(XLSCellFonts.getCellFont(cellStyleName));
    getStylesList().add(new XLSCellStyles(styleIndex, cellStyleName, cellStyle));
  }

  public static void createCellStylePercent(HSSFWorkbook workbook) throws QAException {
    final String cellStyleName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_STYLE, "");
    final int styleIndex = workbook.getNumCellStyles();
    final CellStyle cellStyle = workbook.createCellStyle();
    final DataFormat dataFormat = workbook.createDataFormat();
    cellStyle.setDataFormat(dataFormat.getFormat("0.00%"));
    cellStyle.setFont(XLSCellFonts.getCellFont(cellStyleName));
    getStylesList().add(new XLSCellStyles(styleIndex, cellStyleName, cellStyle));
  }

  public static void setCellStyles(HSSFWorkbook workbook) throws QAException {
    switch (workbook.getNumCellStyles()) {
      case CELL_STYLE_INDEX_START:
        // Styles & Fonts do not exist.
        XLSCellFonts.createCellFonts(workbook);
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
          getStylesList().add(new XLSCellStyles(workbookStyleIndex, cellStyleType, cellStyle));
        }
        break;
      default:
        break;
    }
  }

  public static CellStyle getCellStyle(HSSFWorkbook hssfWorkbook, String styleName)
      throws QAException {
    for (final XLSCellStyles xlsCellStyles : getStylesList()) {
      if (xlsCellStyles.getStyleName().equals(styleName)) {
        return hssfWorkbook.getCellStyleAt(xlsCellStyles.getStyleIndex());
      }
    }
    return null;
  }
}
