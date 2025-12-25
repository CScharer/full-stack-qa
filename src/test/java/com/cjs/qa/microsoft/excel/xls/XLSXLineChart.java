/* ====================================================================
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==================================================================== */

package com.cjs.qa.microsoft.excel.xls;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.STLegendPos;

import com.cjs.qa.utilities.IExtension;

/** Line chart example. */
public final class XLSXLineChart {

  private XLSXLineChart() {
    // Utility class - prevent instantiation
  }

  public static void main(String[] args) throws IOException {
    try (XSSFWorkbook workbookXLSX = new XSSFWorkbook()) {
      final XSSFSheet sheetXLSX = workbookXLSX.createSheet("linechart");
      final int numOfRows = 3;
      final int numOfColumns = 10;
      // Create a row and put some cells in it. Rows are 0 based.
      Row row;
      Cell cell;
      for (int rowIndex = 0; rowIndex < numOfRows; rowIndex++) {
        row = sheetXLSX.createRow((short) rowIndex);
        for (int colIndex = 0; colIndex < numOfColumns; colIndex++) {
          cell = row.createCell((short) colIndex);
          cell.setCellValue(colIndex * (rowIndex + 1));
        }
      }
      final XSSFDrawing drawing = sheetXLSX.createDrawingPatriarch();
      final XSSFClientAnchor clientAnchor = drawing.createAnchor(0, 0, 0, 0, 0, 5, 10, 15);
      final XSSFChart chart = drawing.createChart(clientAnchor);
      // Set legend position using low-level API
      chart.getCTChart().addNewLegend().addNewLegendPos().setVal(STLegendPos.TR); // TOP_RIGHT
      // Create line chart data using low-level API for POI 5.x
      CTChart ctChart = chart.getCTChart();
      CTPlotArea ctPlotArea = ctChart.getPlotArea();
      CTLineChart ctLineChart = ctPlotArea.addNewLineChart();
      ctLineChart.addNewVaryColors().setVal(false);
      // Add series
      for (int seriesIndex = 1; seriesIndex < numOfRows; seriesIndex++) {
        CTLineSer ctLineSer = ctLineChart.addNewSer();
        ctLineSer.addNewIdx().setVal(seriesIndex - 1);
        ctLineSer.addNewOrder().setVal(seriesIndex - 1);
        // Category axis data
        CTAxDataSource cads = ctLineSer.addNewCat();
        cads.addNewNumRef().setF("linechart!$A$1:$" + (char) ('A' + numOfColumns - 1) + "$1");
        // Values
        CTNumDataSource ctNumDataSource = ctLineSer.addNewVal();
        ctNumDataSource
            .addNewNumRef()
            .setF(
                "linechart!$A$"
                    + (seriesIndex + 1)
                    + ":$"
                    + (char) ('A' + numOfColumns - 1)
                    + "$"
                    + (seriesIndex + 1));
      }
      // Write the output to a file
      try (FileOutputStream fileOut = new FileOutputStream("ooxml-line-chart" + IExtension.XLSX)) {
        workbookXLSX.write(fileOut);
      }
    }
  }
}
