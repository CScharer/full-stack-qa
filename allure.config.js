// Allure3 Configuration File
// This configuration enables history tracking for Allure reports
// See: https://allurereport.org/docs/v3/configure/
//
// Note: historyPath should be relative to the results directory passed to 'allure generate'
// Since we pass 'allure-results-combined' as the results directory, historyPath should be './history'

module.exports = {
  // Path to the history directory (relative to results directory)
  // History files will be read from and written to this location
  // This path is relative to the results directory (allure-results-combined)
  historyPath: "./history",
  
  // Append new history entries to existing history (true) or replace (false)
  // Setting to true ensures history accumulates across multiple runs
  appendHistory: true
};

