package com.cjs.qa.microsoft.excel.xlsx;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cjs.qa.core.QAException;
import com.cjs.qa.microsoft.excel.IExcel;
import com.cjs.qa.utilities.JavaHelpers;

public class XLSXCellFonts {

  private static List<XLSXCellFonts> fontsList = new ArrayList<>();
  private Font fontObject;
  private String fontName;

  /** Default constructor for data binding and font management. */
  public XLSXCellFonts() {
    // Default constructor for data binding
  }

  public XLSXCellFonts(Font fontObject, String fontName) throws QAException {
    this.fontObject = fontObject;
    this.fontName = fontName;
  }

  public static List<XLSXCellFonts> getFontsList() throws QAException {
    return fontsList;
  }

  public static void setFontsList(List<XLSXCellFonts> fontsList) throws QAException {
    XLSXCellFonts.fontsList = fontsList;
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

  public static void createCellFontBold(XSSFWorkbook workbook) throws QAException {
    final String cellFontName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_FONT, "");
    final Font font = workbook.createFont();
    font.setBold(true);
    getFontsList().add(new XLSXCellFonts(font, cellFontName));
  }

  public static void createCellFontHeading(XSSFWorkbook workbook) throws QAException {
    final String cellFontName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_FONT, "");
    final Font font = workbook.createFont();
    font.setColor(IExcel.getFontColorIndex("black"));
    font.setBold(true);
    getFontsList().add(new XLSXCellFonts(font, cellFontName));
  }

  public static void createCellFontHyperlink(XSSFWorkbook workbook) throws QAException {
    final String cellFontName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_FONT, "");
    final Font font = workbook.createFont();
    // font.setBold(true);
    font.setUnderline(Font.U_SINGLE);
    font.setColor(IExcel.getFontColorIndex("blue"));
    getFontsList().add(new XLSXCellFonts(font, cellFontName));
  }

  public static void createCellFontNormal(XSSFWorkbook workbook) throws QAException {
    final String cellFontName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_FONT, "");
    final Font font = workbook.createFont();
    getFontsList().add(new XLSXCellFonts(font, cellFontName));
  }

  public static void createCellFontPass(XSSFWorkbook workbook) throws QAException {
    final String cellFontName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_FONT, "");
    final Font font = workbook.createFont();
    font.setBold(true);
    font.setColor(IExcel.getFontColorIndex("green"));
    getFontsList().add(new XLSXCellFonts(font, cellFontName));
  }

  public static void createCellFontFail(XSSFWorkbook workbook) throws QAException {
    final String cellFontName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_FONT, "");
    final Font font = workbook.createFont();
    font.setBold(true);
    font.setColor(IExcel.getFontColorIndex("red"));
    getFontsList().add(new XLSXCellFonts(font, cellFontName));
  }

  public static void createCellFontSection(XSSFWorkbook workbook) throws QAException {
    final String cellFontName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_FONT, "");
    final Font font = workbook.createFont();
    font.setBold(true);
    getFontsList().add(new XLSXCellFonts(font, cellFontName));
  }

  public static void createCellFontStatus(XSSFWorkbook workbook) throws QAException {
    final String cellFontName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_FONT, "");
    final Font font = workbook.createFont();
    font.setBold(true);
    getFontsList().add(new XLSXCellFonts(font, cellFontName));
  }

  public static void createCellFontNumber(XSSFWorkbook workbook) throws QAException {
    final String cellFontName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_FONT, "");
    final Font font = workbook.createFont();
    getFontsList().add(new XLSXCellFonts(font, cellFontName));
  }

  public static void createCellFontNumberPercent(XSSFWorkbook workbook) throws QAException {
    final String cellFontName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_FONT, "");
    final Font font = workbook.createFont();
    getFontsList().add(new XLSXCellFonts(font, cellFontName));
  }

  public static void createCellFontPercent(XSSFWorkbook workbook) throws QAException {
    final String cellFontName =
        JavaHelpers.getCurrentMethodName().replace(IExcel.PREFIX_CREATE_CELL_FONT, "");
    final Font font = workbook.createFont();
    getFontsList().add(new XLSXCellFonts(font, cellFontName));
  }

  public static void createCellFonts(XSSFWorkbook workbook) throws QAException {
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
    for (final XLSXCellFonts qaCellFonts : getFontsList()) {
      if (qaCellFonts.getFontName().equals(styleName)) {
        return qaCellFonts.getFontObject();
      }
    }
    return null;
  }
}
