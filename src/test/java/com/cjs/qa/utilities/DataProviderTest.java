package com.cjs.qa.utilities;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit tests for Data Provider utilities.
 *
 * <p>Tests that data providers can read files correctly and return data in the expected format.
 */
public class DataProviderTest {

  @Test
  public void testJSONDataProviderReadsFile() {
    Object[][] data = JSONDataProvider.readJSONArray("test-data/search-queries.json", "queries");

    Assert.assertNotNull(data, "Data should not be null");
    Assert.assertTrue(data.length > 0, "Should have at least one data row");

    // Verify first row structure
    Assert.assertEquals(data[0].length, 2, "First row should have 2 columns");
    Assert.assertEquals(data[0][0], "Selenium WebDriver", "First column should match");
    Assert.assertEquals(data[0][1], true, "Second column should be boolean true");
  }

  @Test
  public void testCSVDataProviderReadsFile() {
    Object[][] data = CSVDataProvider.readCSV("test-data/users.csv", true);

    Assert.assertNotNull(data, "Data should not be null");
    Assert.assertTrue(data.length > 0, "Should have at least one data row");

    // Verify first row structure (email, password, expectedResult, description)
    Assert.assertTrue(data[0].length >= 3, "First row should have at least 3 columns");
  }

  @DataProvider(name = "testData")
  public Object[][] getTestData() {
    return JSONDataProvider.readJSONArray("test-data/search-queries.json", "queries");
  }

  @Test(dataProvider = "testData")
  public void testDataProviderIntegration(String searchTerm, boolean shouldSucceed) {
    // This test verifies that the data provider works with TestNG
    Assert.assertNotNull(searchTerm, "Search term should not be null");
    // Just verify we can use the data - actual test logic would go here
    Assert.assertTrue(true, "Data provider integration works");
  }
}
