package com.cjs.qa.bitcoin;

import java.math.BigDecimal;

/**
 * Java 17: Record for immutable data class.
 *
 * <p>Represents Bitcoin rate information with timestamp. Records provide:
 *
 * <ul>
 *   <li>Automatic getters (no get prefix)
 *   <li>Automatic equals(), hashCode(), toString()
 *   <li>Immutable by default
 *   <li>Compact syntax
 * </ul>
 */
public record Bitcoin(String rate, BigDecimal rateFloat, String dateTimeStamp) {
  @Override
  public String toString() {
    // Java 17: Text block for cleaner string formatting
    return """
    Date Time Stamp:[%s], Rate:[%s], Rate Float:[%s]
    """
        .formatted(dateTimeStamp(), rate(), rateFloat())
        .trim();
  }
}
