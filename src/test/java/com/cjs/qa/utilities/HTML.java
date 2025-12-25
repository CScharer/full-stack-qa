package com.cjs.qa.utilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;

import com.cjs.qa.core.QAException;
import com.cjs.qa.utilities.colors.ColorsHEX;

public class HTML {

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(HTML.class));
  public static final String HEADER = XML.HEADING_INFO;
  public static final String STYLESHEET =
      "<xsl:stylesheet version=" + Constants.QUOTE_DOUBLE + "1.0" + Constants.QUOTE_DOUBLE + ">";
  private String headingColor = ColorsHEX.VIVIT_GOLD.getValue();
  private String headerFooterColor = ColorsHEX.VIVIT_LIGHTBLUE.getValue();

  public HTML(String fileName, String headerFooterColor, String headingColor) {
    if (headingColor != null) {
      this.headingColor = headingColor;
    }
    if (headerFooterColor != null) {
      this.headerFooterColor = headerFooterColor;
    }
    LOG.debug("fileName: [{}]", fileName);
    final StringBuilder stringBuilderReport = new StringBuilder();
    stringBuilderReport.append(HEADER);
    FSOTests.fileWrite(fileName, stringBuilderReport.toString(), false);
  }

  public static String convertStringToHTML(String stringValue) {
    final Map<String, String> map = getHTMLCodes();
    for (final Entry<String, String> entry : map.entrySet()) {
      final String key = entry.getKey();
      final String value = entry.getValue();
      stringValue = stringValue.replaceAll(key, value);
    }
    return stringValue;
  }

  public String createStandardTable(
      List<String> fieldList, List<Map<String, String>> listMapReport) {
    return createStandardTable(fieldList, listMapReport, 0);
  }

  public String createStandardTable(
      List<String> fieldList,
      List<String> fieldNumericList,
      List<String> fieldCurrencyList,
      List<Map<String, String>> reportListMap) {
    return createStandardTable(fieldList, fieldNumericList, fieldCurrencyList, reportListMap, 0);
  }

  public String createStandardTable(
      List<String> fieldList, List<Map<String, String>> reportListMap, int tab) {
    StringBuilder stringBuilderReport = new StringBuilder();
    stringBuilderReport.append("<html>");
    stringBuilderReport.append("<head>");
    stringBuilderReport.append(
        "<link rel="
            + Constants.QUOTE_DOUBLE
            + "stylesheet"
            + Constants.QUOTE_DOUBLE
            + " type="
            + Constants.QUOTE_DOUBLE
            + "text/css"
            + Constants.QUOTE_DOUBLE
            + " href="
            + Constants.QUOTE_DOUBLE
            + "StandardTable.css"
            + Constants.QUOTE_DOUBLE
            + "></link>");
    stringBuilderReport.append("</head>");
    stringBuilderReport.append("<body>");
    StringBuilder stringBuilderTable = new StringBuilder();
    boolean firstRecord = false;
    for (Map<String, String> mapReport : reportListMap) {
      if (!firstRecord) {
        firstRecord = true;
        stringBuilderTable.append(
            "<table  class="
                + Constants.QUOTE_DOUBLE
                + "scroll"
                + Constants.QUOTE_DOUBLE
                + " style="
                + Constants.QUOTE_DOUBLE
                + "border: 1px solid black; border-collapse: collapse;width: 100%;"
                + Constants.QUOTE_DOUBLE
                + ">");
        stringBuilderTable.append("<thead>");
        stringBuilderTable.append("<tr>");
        for (final String field : fieldList) {
          stringBuilderTable.append(
              "<th style="
                  + Constants.QUOTE_DOUBLE
                  + "border: 1px solid black; border-collapse: collapse;"
                  + " background-color: "
                  + this.headingColor
                  + ";"
                  + Constants.QUOTE_DOUBLE
                  + ">");
          stringBuilderTable.append("<b>" + field + "</b>");
          stringBuilderTable.append("</th>");
        }
        tab--;
        stringBuilderTable.append("</tr>");
        stringBuilderTable.append("</thead>");
        stringBuilderTable.append("<tbody>");
      }
      stringBuilderTable.append("<tr>");
      for (final String field : fieldList) {
        final String value = mapReport.get(field);
        stringBuilderTable.append(
            "<td style="
                + Constants.QUOTE_DOUBLE
                + "border: 1px solid black; border-collapse: collapse;"
                + Constants.QUOTE_DOUBLE
                + ">");
        if (value.contains("http://") || value.contains("https://")) {
          stringBuilderTable.append(link(value, value));
        } else {
          stringBuilderTable.append(value);
        }
        stringBuilderTable.append("</td>");
      }
      tab--;
      stringBuilderTable.append("</tr>");
      mapReport = null;
    }
    tab--;
    stringBuilderTable.append("</tbody>");
    stringBuilderTable.append("</table>");
    stringBuilderReport.append(stringBuilderTable.toString());
    stringBuilderTable = null;
    stringBuilderReport.append("</body>");
    stringBuilderReport.append("</html>");
    final String report = stringBuilderReport.toString();
    LOG.debug("Report: {}", Constants.NEWLINE + report);
    stringBuilderReport = null;
    return report;
  }

  public String createStandardTable(
      List<String> fieldList,
      List<String> fieldNumericList,
      List<String> fieldCurrencyList,
      List<Map<String, String>> reportListMap,
      int tab) {
    StringBuilder stringBuilderReport = new StringBuilder();
    stringBuilderReport.append("<html>");
    stringBuilderReport.append("<head>");
    stringBuilderReport.append(
        "<link rel="
            + Constants.QUOTE_DOUBLE
            + "stylesheet"
            + Constants.QUOTE_DOUBLE
            + " type="
            + Constants.QUOTE_DOUBLE
            + "text/css"
            + Constants.QUOTE_DOUBLE
            + " href="
            + Constants.QUOTE_DOUBLE
            + "StandardTable.css"
            + Constants.QUOTE_DOUBLE
            + "></link>");
    stringBuilderReport.append("</head>");
    stringBuilderReport.append("<body>");
    StringBuilder stringBuilderTable = new StringBuilder();
    boolean firstRecord = false;
    for (Map<String, String> mapReport : reportListMap) {
      if (!firstRecord) {
        firstRecord = true;
        stringBuilderTable.append(
            "<table  class="
                + Constants.QUOTE_DOUBLE
                + "scroll"
                + Constants.QUOTE_DOUBLE
                + " style="
                + Constants.QUOTE_DOUBLE
                + "border: 1px solid black; border-collapse: collapse;width: 100%;"
                + Constants.QUOTE_DOUBLE
                + ">");
        stringBuilderTable.append("<thead>");
        stringBuilderTable.append("<tr>");
        for (final String field : fieldList) {
          stringBuilderTable.append(
              "<th style="
                  + Constants.QUOTE_DOUBLE
                  + "border: 1px solid black; border-collapse: collapse;"
                  + " background-color: "
                  + this.headingColor
                  + ";"
                  + Constants.QUOTE_DOUBLE
                  + ">");
          stringBuilderTable.append("<b>" + field + "</b>");
          stringBuilderTable.append("</th>");
        }
        tab--;
        stringBuilderTable.append("</tr>");
        stringBuilderTable.append("</thead>");
        stringBuilderTable.append("<tbody>");
      }
      stringBuilderTable.append("<tr>");
      for (final String field : fieldList) {
        boolean fontBold = false;
        boolean fontUnderline = false;
        boolean fontItalics = false;
        if (Double.valueOf(mapReport.get("Difference$")) < 0) {
          fontItalics = true;
        }
        switch (mapReport.get("Contractor")) {
          case "Month":
          case "Annual":
          case "Cumulative":
            fontBold = true;
            fontItalics = true;
            fontUnderline = true;
            break;
          default:
            // Normal contractor - no special formatting
            break;
        }
        if ("Cumulative".equals(mapReport.get("Contractor"))) {
          stringBuilderTable.append(
              "<td style="
                  + Constants.QUOTE_DOUBLE
                  + "border: 1px solid black; border-collapse: collapse;"
                  + " background-color: "
                  + ColorsHEX.VIVIT_BLUE.getValue()
                  + ";"
                  + Constants.QUOTE_DOUBLE
                  + ">");
        }
        if ("Annual".equals(mapReport.get("Contractor"))) {
          stringBuilderTable.append(
              "<td style="
                  + Constants.QUOTE_DOUBLE
                  + "border: 1px solid black; border-collapse: collapse;"
                  + " background-color: "
                  + ColorsHEX.VIVIT_GOLD.getValue()
                  + ";"
                  + Constants.QUOTE_DOUBLE
                  + ">");
        }
        if (!"Cumulative".equals(mapReport.get("Contractor"))
            && !"Annual".equals(mapReport.get("Contractor"))) {
          stringBuilderTable.append(
              "<td style="
                  + Constants.QUOTE_DOUBLE
                  + "border: 1px solid black; border-collapse: collapse;"
                  + Constants.QUOTE_DOUBLE
                  + ">");
        }
        String value = mapReport.get(field);
        boolean fontRed = false;
        if (fieldNumericList.contains(field)) {
          if (Double.valueOf(value) < 0) {
            fontRed = true;
            fontItalics = true;
          }
          value = JavaHelpers.formatNumber(value, "###,##0.00");
        }
        if (fieldCurrencyList.contains(field)) {
          if (Double.valueOf(value) < 0) {
            fontRed = true;
            fontItalics = true;
          }
          value = JavaHelpers.formatCurrency(value);
        }
        String formatPre = "";
        if (fontRed) {
          stringBuilderTable.append(
              "<font color=" + Constants.QUOTE_DOUBLE + "red" + Constants.QUOTE_DOUBLE + ">");
          fontBold = true;
          fontUnderline = true;
        }
        if (fontBold) {
          formatPre += "<b>";
        }
        if (fontItalics) {
          formatPre += "<i>";
        }
        if (fontUnderline) {
          formatPre += "<u>";
        }
        String formatPost = "";
        if (fontUnderline) {
          formatPost += "</u>";
        }
        if (fontItalics) {
          formatPost += "</i>";
        }
        if (fontBold) {
          formatPost += "</b>";
        }
        if (fontRed) {
          if (!value.contains("(")) {
            value = "(" + value + ")";
          }
        }
        stringBuilderTable.append(formatPre + value + formatPost);
        if (fontRed) {
          stringBuilderTable.append("</font>");
        }
        if ("Cumulative".equals(mapReport.get("Contractor"))
            || "Annual".equals(mapReport.get("Contractor"))) {
          stringBuilderTable.append("</td>");
        } else {
          stringBuilderTable.append("</td>");
        }
      }
      tab--;
      stringBuilderTable.append("</tr>");
      mapReport = null;
    }
    tab--;
    stringBuilderTable.append("</tbody>");
    stringBuilderTable.append("</table>");
    stringBuilderReport.append(stringBuilderTable.toString());
    stringBuilderTable = null;
    stringBuilderReport.append("</body>");
    stringBuilderReport.append("</html>");
    final String report = stringBuilderReport.toString();
    LOG.debug("Report: {}", Constants.NEWLINE + report);
    stringBuilderReport = null;
    return report;
  }

  public String createStandardTableLogo(String logoHREF, String logoSource, int tab) {
    // logoHREF="http://www.vivit-worldwide.org/default.asp?";
    tab = 0;
    StringBuilder stringBuilderReport = new StringBuilder();
    stringBuilderReport.append("<html>");
    stringBuilderReport.append("<head>");
    stringBuilderReport.append(
        "<link rel="
            + Constants.QUOTE_DOUBLE
            + "stylesheet"
            + Constants.QUOTE_DOUBLE
            + " type="
            + Constants.QUOTE_DOUBLE
            + "text/css"
            + Constants.QUOTE_DOUBLE
            + " href="
            + Constants.QUOTE_DOUBLE
            + "TableReport.css"
            + Constants.QUOTE_DOUBLE
            + "></link>");
    stringBuilderReport.append("</head>");
    stringBuilderReport.append("<body>");
    StringBuilder stringBuilderHeader = new StringBuilder();
    stringBuilderHeader.append(
        "<table class="
            + Constants.QUOTE_DOUBLE
            + "scroll"
            + Constants.QUOTE_DOUBLE
            + " style="
            + Constants.QUOTE_DOUBLE
            + "border: 1px solid black; border-collapse: collapse;width: 100%;"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilderHeader.append("<tr>");
    stringBuilderHeader.append(
        "<th style="
            + Constants.QUOTE_DOUBLE
            + "border: 1px solid black; border-collapse: collapse; background-color: "
            + this.headerFooterColor
            + ";"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilderHeader.append(
        "<div style=" + Constants.QUOTE_DOUBLE + "height: 100px" + Constants.QUOTE_DOUBLE + ">");
    stringBuilderHeader.append(
        "<img href="
            + Constants.QUOTE_DOUBLE
            + logoHREF
            + Constants.QUOTE_DOUBLE
            + " src="
            + Constants.QUOTE_DOUBLE
            + logoSource
            + Constants.QUOTE_DOUBLE
            + " alt="
            + Constants.QUOTE_DOUBLE
            + "Error"
            + Constants.QUOTE_DOUBLE
            + " style="
            + Constants.QUOTE_DOUBLE
            + "max-width: 100%; max-height: 100%; display:block; margin:auto;"
            + Constants.QUOTE_DOUBLE
            + " />");
    stringBuilderHeader.append("</div>");
    stringBuilderHeader.append("</th>");
    stringBuilderHeader.append("</tr>");
    stringBuilderHeader.append("</table>");
    final StringBuilder stringBuilderTable = new StringBuilder();
    stringBuilderTable.append(stringBuilderHeader.toString());
    stringBuilderHeader = null;
    tab--;
    stringBuilderReport.append(stringBuilderTable.toString());
    stringBuilderReport.append("</body>");
    stringBuilderReport.append("</html>");
    final String report = stringBuilderReport.toString();
    LOG.debug("Report: {}", Constants.NEWLINE + report);
    stringBuilderReport = null;
    return report;
  }

  public static String formatPretty(String htmlString) throws QAException {
    htmlString = XML.formatPretty(htmlString);
    return htmlString;
  }

  public static Map<String, String> getHTMLCodes() {
    final Map<String, String> map = new HashMap<>();
    map.put(Constants.QUOTE_DOUBLE + "", "&quot;");
    map.put("&", "&amp;");
    map.put("<", "&lt;");
    map.put(">", "&gt;");
    map.put("®", "&reg;");
    map.put("©", "&copy;");
    map.put("©", "&trade;");
    return map;
  }

  public static String link(String href, String value) {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("<a href=" + Constants.QUOTE_DOUBLE + href + Constants.QUOTE_DOUBLE + ">");
    stringBuilder.append(value);
    stringBuilder.append("</a>");
    return stringBuilder.toString();
  }
}
