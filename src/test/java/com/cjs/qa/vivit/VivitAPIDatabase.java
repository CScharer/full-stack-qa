package com.cjs.qa.vivit;

import java.util.ArrayList;
import java.util.List;

import com.cjs.qa.utilities.Constants;
import com.cjs.qa.utilities.HTML;
import com.cjs.qa.utilities.colors.ColorsHEX;

@Deprecated
public class VivitAPIDatabase {

  private List<VivitAPIDatabaseUpdates> vivitAPIDatabaseUpdates = new ArrayList<>();

  public void add(VivitAPIDatabaseUpdates vivitAPIDatabaseUpdates) {
    getVivitAPIDatabaseUpdates().add(vivitAPIDatabaseUpdates);
  }

  public List<VivitAPIDatabaseUpdates> getVivitAPIDatabaseUpdates() {
    return vivitAPIDatabaseUpdates;
  }

  public void setVivitAPIDatabaseUpdates(List<VivitAPIDatabaseUpdates> vivitAPIDatabaseUpdates) {
    this.vivitAPIDatabaseUpdates = vivitAPIDatabaseUpdates;
  }

  @Override
  public String toString() {
    final StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(HTML.HEADER);
    stringBuilder.append("<html>");
    stringBuilder.append(
        "<table class="
            + Constants.QUOTE_DOUBLE
            + "scroll"
            + Constants.QUOTE_DOUBLE
            + " style="
            + Constants.QUOTE_DOUBLE
            + "border: 1px solid black; border-collapse: collapse;width: 100%;"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append("<tr>");
    stringBuilder.append(
        "<th style="
            + Constants.QUOTE_DOUBLE
            + "border: 1px solid black; border-collapse: collapse; background-color: "
            + ColorsHEX.VIVIT_GOLD.getValue()
            + ";"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append("Database Changes (" + getVivitAPIDatabaseUpdates().size() + ")");
    stringBuilder.append("</th>");
    stringBuilder.append("</tr>");
    stringBuilder.append("</table>");
    stringBuilder.append(
        "<table class="
            + Constants.QUOTE_DOUBLE
            + "scroll"
            + Constants.QUOTE_DOUBLE
            + " style="
            + Constants.QUOTE_DOUBLE
            + "border: 1px solid black; border-collapse: collapse;width: 100%;"
            + Constants.QUOTE_DOUBLE
            + ">");
    stringBuilder.append("<thead>");
    stringBuilder.append("<tr>");
    for (final String fieldName : VivitAPIDatabaseUpdates.getFieldList()) {
      stringBuilder.append(
          "<th style="
              + Constants.QUOTE_DOUBLE
              + "border: 1px solid black; border-collapse: collapse;"
              + " background-color: "
              + ColorsHEX.VIVIT_LIGHTBLUE.getValue()
              + ";"
              + Constants.QUOTE_DOUBLE
              + ">");
      stringBuilder.append("<b>" + fieldName + "</b>");
      stringBuilder.append("</th>");
    }
    stringBuilder.append("</tr>");
    stringBuilder.append("</thead>");
    stringBuilder.append("<tbody>");
    if (getVivitAPIDatabaseUpdates().isEmpty()) {
      getVivitAPIDatabaseUpdates().add(new VivitAPIDatabaseUpdates("None", "None", "None", "None"));
    }
    for (final VivitAPIDatabaseUpdates vivitAPIDatabaseUpdates : getVivitAPIDatabaseUpdates()) {
      stringBuilder.append(vivitAPIDatabaseUpdates.toString());
    }
    stringBuilder.append("</tbody>");
    stringBuilder.append("</table>");
    stringBuilder.append("</html>");
    return stringBuilder.toString();
  }
}
