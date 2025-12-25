package com.cjs.qa.microsoft.excel;

public enum EExcelConstants {
  DEFAULT_INDEX(0),
  CELL_STYLES_XLS(11),
  CELL_STYLES_XLSZ(21),
  MAX_CELL_HEIGHT(409),
  MAX_CELL_WIDTH(255),
  MAX_COLUMNS_XLS(255),
  MAX_COLUMNS_XLSX(16383),
  MAX_ROWS_XLS(65536),
  MAX_ROWS_XLSX(1048576),
  MAX_SHEET_NAME_LENGTH(31);

  private int value;

  EExcelConstants(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
