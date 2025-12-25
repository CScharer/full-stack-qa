package com.cjs.qa.microsoft.excel.xls;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Workbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.cjs.qa.core.QAException;
import com.cjs.qa.utilities.GuardedLogger;

public class XLSBarChart extends XLS {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(XLSBarChart.class));

  private String fileName = "";
  private String sheet = "";

  @Override
  public String getFileName() {
    return fileName;
  }

  public String getSheet() {
    return sheet;
  }

  public XLSBarChart(String fileName, String sheet) throws IOException, QAException {
    super(fileName, sheet);
    this.fileName = fileName;
    this.sheet = sheet;
  }

  public void create(int rowStart, int rowEnd) throws QAException {
    try {
      final DefaultCategoryDataset defaultCategoryDataset = new DefaultCategoryDataset();
      final HSSFSheet sheetXLS = getWorkbook().getSheet(sheet);
      // for (int row = rowStart; row <= sheetXLS.getLastRowNum(); row++)
      for (int row = rowStart; row < rowEnd; row++) {
        final String label = readCell(sheet, 0, row);
        final Number number = Integer.valueOf(readCell(sheet, 1, row));
        LOG.debug("label:[[{}]", label + "], number:[" + number + "]");
        // No grouping in the bar chart, so we put them in fixed group.
        defaultCategoryDataset.addValue(number.doubleValue(), "Count", label);
      }
      /* Create a logical chart object with the chart data collected */
      final String title = sheet.replace("_", " ");
      final JFreeChart jFreeChart =
          ChartFactory.createBarChart(
              title,
              title,
              "Count",
              defaultCategoryDataset,
              PlotOrientation.VERTICAL,
              true,
              true,
              false);
      final int width = 320; /* Width of the chart */
      final int height = 480; /* Height of the chart */
      /* Write chart as PNG to Output Stream */
      final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      ChartUtils.writeChartAsPNG(byteArrayOutputStream, jFreeChart, width, height);
      final int picture =
          getWorkbook().addPicture(byteArrayOutputStream.toByteArray(), Workbook.PICTURE_TYPE_PNG);
      byteArrayOutputStream.close();
      final HSSFPatriarch patriarchXLS = sheetXLS.createDrawingPatriarch();
      final ClientAnchor clientAnchor = new HSSFClientAnchor();
      clientAnchor.setCol1(11);
      clientAnchor.setRow1(1);
      final HSSFPicture pictureXLS = patriarchXLS.createPicture(clientAnchor, picture);
      pictureXLS.resize();
      close();
    } catch (final Exception e) {
      LOG.error("Exception occurred", e);
    }
  }
}
