package com.cjs.qa.microsoft.word;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.VerticalAlign;
import org.apache.poi.xwpf.usermodel.XWPFAbstractNum;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.junit.Test;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAbstractNum;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLvl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STNumberFormat;

import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;
import com.cjs.qa.utilities.IExtension;

public class WordTests {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(WordTests.class));

  public static void main(String[] args) throws Exception {
    // https://www.tutorialspoint" + IExtension.COM +
    // "/apache_poi_word/index.htm
    final String fileName = "createDOCX" + IExtension.DOCX;
    try (XWPFDocument xwpfDocument = new XWPFDocument()) {
      StringBuilder stringBuilder = new StringBuilder();
      //
      final XWPFParagraph xwpfParagraph1 = xwpfDocument.createParagraph();
      final XWPFRun xwpfRun = xwpfParagraph1.createRun();
      xwpfParagraph1.setBorderLeft(Borders.BASIC_BLACK_DASHES);
      xwpfParagraph1.setBorderRight(Borders.BASIC_BLACK_DASHES);
      xwpfParagraph1.setBorderTop(Borders.BASIC_BLACK_DASHES);
      xwpfParagraph1.setBorderBottom(Borders.BASIC_BLACK_DASHES);
      //
      stringBuilder.append("At tutorialspoint.com, we strive hard to provide quality tutorials ");
      stringBuilder.append(
          "for self-learning purpose in the domains of Academics, Information Technology, ");
      stringBuilder.append("Management and Computer Programming Languages.");
      //
      xwpfRun.setText(stringBuilder.toString());
      final XWPFTable xwpfTable = xwpfDocument.createTable();
      // create first row
      final XWPFTableRow xwpfTableRow01 = xwpfTable.getRow(0);
      xwpfTableRow01.getCell(0).setText("col one, row one");
      xwpfTableRow01.addNewTableCell().setText("col two, row one");
      xwpfTableRow01.addNewTableCell().setText("col three, row one");
      // create second row
      final XWPFTableRow xwpfTableRow02 = xwpfTable.createRow();
      xwpfTableRow02.getCell(0).setText("col one, row two");
      xwpfTableRow02.getCell(1).setText("col two, row two");
      xwpfTableRow02.getCell(2).setText("col three, row two");
      // create third row
      final XWPFTableRow xwpfTableRow03 = xwpfTable.createRow();
      xwpfTableRow03.getCell(0).setText("col one, row three");
      xwpfTableRow03.getCell(1).setText("col two, row three");
      xwpfTableRow03.getCell(2).setText("col three, row three");
      //
      // create paragraph
      final XWPFParagraph xwpfParagraph2 = xwpfDocument.createParagraph();
      // Set Bold an Italic
      final XWPFRun xwpfParagraph2Run1 = xwpfParagraph2.createRun();
      xwpfParagraph2Run1.setBold(true);
      xwpfParagraph2Run1.setItalic(true);
      xwpfParagraph2Run1.setText("Font Style");
      xwpfParagraph2Run1.addBreak();
      // Set text Position
      final XWPFRun xwpfParagraph2Run2 = xwpfParagraph2.createRun();
      xwpfParagraph2Run2.setText("Font Style two");
      xwpfParagraph2Run2.setTextPosition(100);
      // Set Strike through and Font Size and Subscript
      final XWPFRun xwpfParagraph2Run3 = xwpfParagraph2.createRun();
      xwpfParagraph2Run3.setStrikeThrough(true);
      xwpfParagraph2Run3.setFontSize(20);
      xwpfParagraph2Run3.setSubscript(VerticalAlign.SUBSCRIPT);
      xwpfParagraph2Run3.setText(" Different Font Styles");
      //
      // create paragraph
      XWPFParagraph xwpfParagraph3 = xwpfDocument.createParagraph();
      // Set alignment paragraph to RIGHT
      xwpfParagraph3.setAlignment(ParagraphAlignment.RIGHT);
      XWPFRun xwpfRun3 = xwpfParagraph3.createRun();
      stringBuilder = new StringBuilder();
      stringBuilder.append("At tutorialspoint.com, we strive hard to provide quality tutorials ");
      stringBuilder.append(
          "for self-learning purpose in the domains of Academics, Information Technology, ");
      stringBuilder.append("Management and Computer Programming Languages.");
      xwpfRun3.setText(stringBuilder.toString());
      // Create Another paragraph
      xwpfParagraph3 = xwpfDocument.createParagraph();
      // Set alignment paragraph to CENTER
      xwpfParagraph3.setAlignment(ParagraphAlignment.CENTER);
      xwpfRun3 = xwpfParagraph3.createRun();
      stringBuilder = new StringBuilder();
      stringBuilder.append("The endeavor started by Mohtashim, an AMU alumni, ");
      stringBuilder.append(
          "who is the founder and the managing director of Tutorials Point (I) Pvt. Ltd.  ");
      stringBuilder.append(
          "He came up with the website tutorialspoint.com in year 2006 with the help of"
              + " handpicked freelancers, ");
      stringBuilder.append("with an array of tutorials" + " for computer programming languages.");
      xwpfRun3.setText(stringBuilder.toString());
      //
      try (FileOutputStream fileOutputStream = new FileOutputStream(new File(fileName))) {
        xwpfDocument.write(fileOutputStream);
      }
      LOG.debug("{} written successfully", fileName);
    }
    //
    try (XWPFDocument xwpfDocument = new XWPFDocument(new FileInputStream(fileName));
        // using XWPFWordExtractor Class
        XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(xwpfDocument)) {
      LOG.debug("{}", xwpfWordExtractor.getText());
      LOG.debug("{} read successfully", fileName);
    }
  }

  @Test
  public void testNumbering() {
    final String filePathName = Constants.PATH_DESKTOP + "Word_Bullets_NumberedLists.docx";
    try {
      final List<String> listKeys =
          new ArrayList<>(Arrays.asList(new String[] {"1", "1.1", "2", "2.1", "3", "3.1"}));
      final List<String> listSession =
          new ArrayList<String>(
              Arrays.asList(
                  new String[] {"One", "One-One", "Two", "Two-One", "Three", "Three-One"}));
      try (XWPFDocument xwpfDocument = new XWPFDocument()) {
        for (int index = 0; index < 2; index++) {
          XWPFParagraph xwpfParagraph = xwpfDocument.createParagraph();
          XWPFRun xwpfRun = xwpfParagraph.createRun();
          xwpfRun.setText("Session Title:");
          final CTAbstractNum ctAbstractNum = CTAbstractNum.Factory.newInstance();
          // If we have an existing document, we must determine the next
          // free number first.
          ctAbstractNum.setAbstractNumId(BigInteger.valueOf(0));
          final CTLvl ctLvl = ctAbstractNum.addNewLvl();
          // Bullet list
          // ctLvl.addNewNumFmt().setVal(STNumberFormat.BULLET);ctLvl.
          // addNewLvlText().setVal("•");
          // Decimal list
          // ctLvl.addNewNumFmt().setVal(STNumberFormat.DECIMAL);
          ctLvl.addNewNumFmt().setVal(STNumberFormat.DECIMAL_HALF_WIDTH);
          ctLvl.addNewLvlText().setVal("%1.");
          ctLvl.addNewStart().setVal(BigInteger.valueOf(1));
          final XWPFAbstractNum xwpfAbstractNum = new XWPFAbstractNum(ctAbstractNum);
          final XWPFNumbering xwpfNumbering = xwpfDocument.createNumbering();
          final BigInteger bigIntegerAbstractNumberID =
              xwpfNumbering.addAbstractNum(xwpfAbstractNum);
          final BigInteger bigIntegerNumberID = xwpfNumbering.addNum(bigIntegerAbstractNumberID);
          for (int indexKey = 0; indexKey < listKeys.size(); indexKey++) {
            // final String key = listKeys.get(indexKey);
            xwpfParagraph = xwpfDocument.createParagraph();
            xwpfParagraph.setNumID(bigIntegerNumberID);
            // if (!key.contains("."))
            // {
            // xwpfParagraph.getCTP().getPPr().getNumPr().addNewIlvl().setVal(BigInteger.valueOf(1));
            // } else
            // {
            // xwpfParagraph.getCTP().getPPr().getNumPr().addNewIlvl().setVal(BigInteger.valueOf(2));
            // }
            xwpfRun = xwpfParagraph.createRun();
            final String string = listSession.get(indexKey);
            xwpfRun.setText(string);
          }
          xwpfParagraph = xwpfDocument.createParagraph();
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePathName)) {
          xwpfDocument.write(fileOutputStream);
        }
      }
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }
}
