package com.cjs.qa.utilities.colors;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.GuardedLogger;

public class ColorPalette {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(ColorPalette.class));
  private String palette;
  private String name;
  private String hex;
  private String rgb;

  public ColorPalette(String palette, String name, String hex, String rgb) {
    this.palette = palette;
    this.name = name;
    this.hex = hex;
    this.rgb = rgb;
  }

  public String getHex() {
    return hex;
  }

  public String getName() {
    return name;
  }

  public String getPalette() {
    return palette;
  }

  public String getRgb() {
    return rgb;
  }

  public void setHex(String hex) {
    this.hex = hex;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPalette(String palette) {
    this.palette = palette;
  }

  public void setRgb(String rgb) {
    this.rgb = rgb;
  }

  public String toCode(List<ColorPalette> colorPaletteList, String type) {
    type = type.toUpperCase(Locale.ENGLISH);
    final StringBuilder stringBuilder = new StringBuilder();
    final List<String> names = new ArrayList<>();
    stringBuilder.append("public enum Colors" + type);
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("{");
    stringBuilder.append(Constants.NEWLINE);
    for (int colorPaletteIndex = 0;
        colorPaletteIndex < colorPaletteList.size();
        colorPaletteIndex++) {
      final ColorPalette colorPalette = colorPaletteList.get(colorPaletteIndex);
      final String name = colorPalette.getName();
      stringBuilder.append(name);
      // Avoid duplicate names/values.
      if (!names.contains(name)) {
        stringBuilder.append(name);
        stringBuilder.append("(");
        switch (type) {
          case "HEX":
            stringBuilder.append(
                Constants.QUOTE_DOUBLE + colorPalette.getHex() + Constants.QUOTE_DOUBLE + "");
            break;
          case "RGB":
            stringBuilder.append(
                Constants.QUOTE_DOUBLE + colorPalette.getRgb() + Constants.QUOTE_DOUBLE + "");
            break;
          default:
            LOG.warn("Unknown color type: {}. Using empty value.", type);
            stringBuilder.append("\"\"");
            break;
        }
        stringBuilder.append(")");
        if (colorPaletteIndex < (colorPaletteList.size() - 1)) {
          stringBuilder.append(",");
        }
      }
    }
    stringBuilder.append(Constants.DELIMETER_LIST);
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("private String value;");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("private Colors" + type + "(String value)");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("{");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("this.value = value;");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("}");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("public String getValue()");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("{");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("return value;");
    stringBuilder.append(Constants.NEWLINE);
    stringBuilder.append("}");
    return stringBuilder.toString();
  }

  @Override
  public String toString() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Palette:" + getPalette());
    stringBuilder.append("Name:" + getName());
    stringBuilder.append("Hex:" + getHex());
    stringBuilder.append("Rgb:" + getRgb());
    return stringBuilder.toString();
  }
}
