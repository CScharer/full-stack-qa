package com.cjs.qa.microsoft.excel.xls;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.cjs.qa.utilities.IExtension;

public final class XLSBarChartExample {

  private XLSBarChartExample() {
    // Utility class - prevent instantiation
  }

  public static void main(String[] args) throws Exception {
    String fileName = "C:/Workspace/Github/cjs-app/cjs-app-gui/barChart" + IExtension.XLS;
    fileName = "barChart" + IExtension.XLS;
    /* Read the bar chart data from the excel file */
    try (FileInputStream fileInputStream = new FileInputStream(new File(fileName));
        /*
         * HSSFWorkbook object reads the full Excel document. We will manipulate
         * this object and
         * write it back to the disk with the chart
         */
        HSSFWorkbook workbookXLS = new HSSFWorkbook(fileInputStream)) {
      /* Read chart data worksheet */
      final HSSFSheet sheetXLS = workbookXLS.getSheetAt(0);
      /* Create Dataset that will take the chart data */
      final DefaultCategoryDataset defaultCategoryDataset = new DefaultCategoryDataset();
      /* We have to load bar chart data now */
      /* Begin by iterating over the worksheet */
      /* Create an Iterator object */
      final Iterator<Row> iteratorRow = sheetXLS.iterator();
      /* Loop through worksheet data and populate bar chart dataset */
      String label = "a";
      Number number = 0;
      while (iteratorRow.hasNext()) {
        // Read Rows from Excel document
        final Row row = iteratorRow.next();
        // Read cells in Rows and get chart data
        final Iterator<Cell> iteratorCell = row.cellIterator();
        while (iteratorCell.hasNext()) {
          final Cell cell = iteratorCell.next();
          // POI 3.15
          switch (cell.getCellType()) {
            case NUMERIC:
              number = cell.getNumericCellValue();
              break;
            case STRING:
              label = cell.getStringCellValue();
              break;
            default:
              // Default to string value
              label = cell.getStringCellValue();
              break;
          }
          // POI 4.0
          // switch (cell.getCellType())
          // {
          // case CellType.NUMERIC:
          // number = cell.getNumericCellValue();
          // break;
          // default:
          // case CellType.STRING:
          // label = cell.getStringCellValue();
          // break;
          // }
        }
        /* Add data to the data set */
        /*
         * We don't have grouping in the bar chart, so we put them in fixed
         * group
         */
        defaultCategoryDataset.addValue(number.doubleValue(), "Marks", label);
      }
      /* Create a logical chart object with the chart data collected */
      final JFreeChart jFreeChart =
          ChartFactory.createBarChart(
              "Subject Vs Marks",
              "Subject",
              "Marks",
              defaultCategoryDataset,
              PlotOrientation.VERTICAL,
              true,
              true,
              false);
      /* Dimensions of the bar chart */
      final int width = 640; /* Width of the chart */
      final int height = 480; /* Height of the chart */
      /*
       * We don't want to create an intermediate file. So, we create a byte
       * array output stream
       * and byte array input stream
       * And we pass the chart data directly to input stream through this
       */
      /* Write chart as PNG to Output Stream */
      final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      ChartUtils.writeChartAsPNG(byteArrayOutputStream, jFreeChart, width, height);
      /*
       * We can now read the byte data from output stream and stamp the chart
       * to Excel worksheet
       */
      final int myPictureId =
          workbookXLS.addPicture(byteArrayOutputStream.toByteArray(), Workbook.PICTURE_TYPE_PNG);
      /* we close the output stream as we don't need this anymore */
      byteArrayOutputStream.close();
      /* Create the drawing container */
      final HSSFPatriarch patriarchXLS = sheetXLS.createDrawingPatriarch();
      /* Create an anchor point */
      final ClientAnchor clientAnchor = new HSSFClientAnchor();
      /*
       * Define top left corner, and we can resize picture suitable from there
       */
      clientAnchor.setCol1(4);
      clientAnchor.setRow1(5);
      /* Invoke createPicture and pass the anchor point and ID */
      final HSSFPicture pictureXLS = patriarchXLS.createPicture(clientAnchor, myPictureId);
      /* Call resize method, which resizes the image */
      pictureXLS.resize();
      /* Write changes to the workbook */
      try (FileOutputStream out = new FileOutputStream(new File(fileName))) {
        workbookXLS.write(out);
      }
    }
  }
}
