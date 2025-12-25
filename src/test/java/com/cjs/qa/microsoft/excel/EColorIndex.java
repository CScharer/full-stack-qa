package com.cjs.qa.microsoft.excel;

import org.apache.poi.hssf.util.HSSFColor;

public enum EColorIndex {
  // This is not currenly used as the IExcel contains a
  // getFontColorIndex method that allows the use of a string value.
  AQUA(HSSFColor.HSSFColorPredefined.AQUA.getIndex()),
  BLACK(HSSFColor.HSSFColorPredefined.BLACK.getIndex()),
  BLUE(HSSFColor.HSSFColorPredefined.BLUE.getIndex()),
  BLUE_GREY(HSSFColor.HSSFColorPredefined.BLUE_GREY.getIndex()),
  BRIGHT_GREEN(HSSFColor.HSSFColorPredefined.BRIGHT_GREEN.getIndex()),
  BROWN(HSSFColor.HSSFColorPredefined.BROWN.getIndex()),
  CORAL(HSSFColor.HSSFColorPredefined.CORAL.getIndex()),
  CORNFLOWER_BLUE(HSSFColor.HSSFColorPredefined.CORNFLOWER_BLUE.getIndex()),
  DARK_BLUE(HSSFColor.HSSFColorPredefined.DARK_BLUE.getIndex()),
  DARK_GREEN(HSSFColor.HSSFColorPredefined.DARK_GREEN.getIndex()),
  DARK_RED(HSSFColor.HSSFColorPredefined.DARK_RED.getIndex()),
  DARK_TEAL(HSSFColor.HSSFColorPredefined.DARK_TEAL.getIndex()),
  DARK_YELLOW(HSSFColor.HSSFColorPredefined.DARK_YELLOW.getIndex()),
  GOLD(HSSFColor.HSSFColorPredefined.GOLD.getIndex()),
  GREEN(HSSFColor.HSSFColorPredefined.GREEN.getIndex()),
  GREY(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex()),
  GREY_25(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex()),
  GREY_40(HSSFColor.HSSFColorPredefined.GREY_40_PERCENT.getIndex()),
  GREY_50(HSSFColor.HSSFColorPredefined.GREY_50_PERCENT.getIndex()),
  GREY_80(HSSFColor.HSSFColorPredefined.GREY_80_PERCENT.getIndex()),
  INDIGO(HSSFColor.HSSFColorPredefined.INDIGO.getIndex()),
  LAVENDER(HSSFColor.HSSFColorPredefined.LAVENDER.getIndex()),
  LEMON_CHIFFON(HSSFColor.HSSFColorPredefined.LEMON_CHIFFON.getIndex()),
  LIGHT_BLUE(HSSFColor.HSSFColorPredefined.LIGHT_BLUE.getIndex()),
  LIGHT_CORNFLOWER(HSSFColor.HSSFColorPredefined.LIGHT_CORNFLOWER_BLUE.getIndex()),
  LIGHT_GREEN(HSSFColor.HSSFColorPredefined.LIGHT_GREEN.getIndex()),
  LIGHT_ORANGE(HSSFColor.HSSFColorPredefined.LIGHT_ORANGE.getIndex()),
  LIGHT_TURQUOISE(HSSFColor.HSSFColorPredefined.LIGHT_TURQUOISE.getIndex()),
  LIGHT_YELLOW(HSSFColor.HSSFColorPredefined.LIGHT_YELLOW.getIndex()),
  LIME(HSSFColor.HSSFColorPredefined.LIME.getIndex()),
  MAROON(HSSFColor.HSSFColorPredefined.MAROON.getIndex()),
  OLIVE_GREEN(HSSFColor.HSSFColorPredefined.OLIVE_GREEN.getIndex()),
  ORANGE(HSSFColor.HSSFColorPredefined.ORANGE.getIndex()),
  ORCHID(HSSFColor.HSSFColorPredefined.ORCHID.getIndex()),
  PALE_BLUE(HSSFColor.HSSFColorPredefined.PALE_BLUE.getIndex()),
  PINK(HSSFColor.HSSFColorPredefined.PINK.getIndex()),
  PLUM(HSSFColor.HSSFColorPredefined.PLUM.getIndex()),
  RED(HSSFColor.HSSFColorPredefined.RED.getIndex()),
  ROSE(HSSFColor.HSSFColorPredefined.ROSE.getIndex()),
  ROYAL_BLUE(HSSFColor.HSSFColorPredefined.ROYAL_BLUE.getIndex()),
  SEA_GREEN(HSSFColor.HSSFColorPredefined.SEA_GREEN.getIndex()),
  SKY_BLUE(HSSFColor.HSSFColorPredefined.SKY_BLUE.getIndex()),
  TAN(HSSFColor.HSSFColorPredefined.TAN.getIndex()),
  TEAL(HSSFColor.HSSFColorPredefined.TEAL.getIndex()),
  TURQUOISE(HSSFColor.HSSFColorPredefined.TURQUOISE.getIndex()),
  VIOLET(HSSFColor.HSSFColorPredefined.VIOLET.getIndex()),
  WHITE(HSSFColor.HSSFColorPredefined.WHITE.getIndex()),
  YELLOW(HSSFColor.HSSFColorPredefined.YELLOW.getIndex());

  private short value;

  EColorIndex(short value) {
    this.value = value;
  }

  public short getIndex() {
    return value;
  }
}
