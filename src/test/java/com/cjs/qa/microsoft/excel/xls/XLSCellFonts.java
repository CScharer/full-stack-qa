package com.cjs.qa.microsoft.excel.xls;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;

import com.cjs.qa.core.QAException;
import com.cjs.qa.microsoft.excel.IExcel;
import com.cjs.qa.utilities.JavaHelpers;

public class XLSCellFonts {

  private static List<XLSCellFonts> fontsList = new ArrayList<>();
  private Font fontObject;
  private String fontName;

  /** Default constructor for data binding and font management. */
  public XLSCellFonts() {
    // Default constructor for data binding
  }

  public XLSCellFonts(Font fontObject, String fontName) throws QAException {
    this.fontObject = fontObject;
    this.fontName = fontName;
  }

  public static List<XLSCellFonts> getFontsList() throws QAException {
    return fontsList;
  }

  public static void setFontsList(List<XLSCellFonts> fontsList) throws QAException {
    XLSCellFonts.fontsList = fontsList;
  }

  public Font getFontObject() throws QAException {
    return fontObject;
  }

  public void setFontObject(Font fontObject) throws QAException {
    this.fontObject = fontObject;
  }

  public String getFontName() throws QAException {
    return fontName;
  }

  public void setFontName(String fontName) throws QAException {
    this.fontName = fontName;
  }

  public static void createCellFontBold(HSSFWorkbook workbook) throws QAException {
    final String cellFontName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_FONT, "");
    final Font font = workbook.createFont();
    font.setBold(true);
    getFontsList().add(new XLSCellFonts(font, cellFontName));
  }

  public static void createCellFontHeading(HSSFWorkbook workbook) throws QAException {
    final String cellFontName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_FONT, "");
    final Font font = workbook.createFont();
    font.setColor(IExcel.getFontColorIndex("black"));
    font.setBold(true);
    getFontsList().add(new XLSCellFonts(font, cellFontName));
  }

  public static void createCellFontHyperlink(HSSFWorkbook workbook) throws QAException {
    final String cellFontName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_FONT, "");
    final Font font = workbook.createFont();
    // font.setBold(true);
    font.setUnderline(Font.U_SINGLE);
    font.setColor(IExcel.getFontColorIndex("blue"));
    getFontsList().add(new XLSCellFonts(font, cellFontName));
  }

  public static void createCellFontNormal(HSSFWorkbook workbook) throws QAException {
    final String cellFontName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_FONT, "");
    final Font font = workbook.createFont();
    getFontsList().add(new XLSCellFonts(font, cellFontName));
  }

  public static void createCellFontPass(HSSFWorkbook workbook) throws QAException {
    final String cellFontName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_FONT, "");
    final Font font = workbook.createFont();
    font.setBold(true);
    font.setColor(IExcel.getFontColorIndex("green"));
    getFontsList().add(new XLSCellFonts(font, cellFontName));
  }

  public static void createCellFontFail(HSSFWorkbook workbook) throws QAException {
    final String cellFontName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_FONT, "");
    final Font font = workbook.createFont();
    font.setBold(true);
    font.setColor(IExcel.getFontColorIndex("red"));
    getFontsList().add(new XLSCellFonts(font, cellFontName));
  }

  public static void createCellFontSection(HSSFWorkbook workbook) throws QAException {
    final String cellFontName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_FONT, "");
    final Font font = workbook.createFont();
    font.setBold(true);
    getFontsList().add(new XLSCellFonts(font, cellFontName));
  }

  public static void createCellFontStatus(HSSFWorkbook workbook) throws QAException {
    final String cellFontName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_FONT, "");
    final Font font = workbook.createFont();
    font.setBold(true);
    getFontsList().add(new XLSCellFonts(font, cellFontName));
  }

  public static void createCellFontNumber(HSSFWorkbook workbook) throws QAException {
    final String cellFontName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_FONT, "");
    final Font font = workbook.createFont();
    getFontsList().add(new XLSCellFonts(font, cellFontName));
  }

  public static void createCellFontNumberPercent(HSSFWorkbook workbook) throws QAException {
    final String cellFontName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_FONT, "");
    final Font font = workbook.createFont();
    getFontsList().add(new XLSCellFonts(font, cellFontName));
  }

  public static void createCellFontPercent(HSSFWorkbook workbook) throws QAException {
    final String cellFontName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_FONT, "");
    final Font font = workbook.createFont();
    getFontsList().add(new XLSCellFonts(font, cellFontName));
  }

  public static void createCellFonts(HSSFWorkbook workbook) throws QAException {
    createCellFontBold(workbook);
    createCellFontHeading(workbook);
    createCellFontHyperlink(workbook);
    createCellFontNormal(workbook);
    createCellFontPass(workbook);
    createCellFontFail(workbook);
    createCellFontSection(workbook);
    createCellFontStatus(workbook);
    createCellFontNumber(workbook);
    createCellFontNumberPercent(workbook);
    createCellFontPercent(workbook);
  }

  public static Font getCellFont(String styleName) throws QAException {
    for (final XLSCellFonts qaCellFonts : getFontsList()) {
      if (qaCellFonts.getFontName().equals(styleName)) {
        return qaCellFonts.getFontObject();
      }
    }
    return null;
  }
}
