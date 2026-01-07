// Allure3 Configuration File
// This configuration enables history tracking for Allure reports
// See: https://allurereport.org/docs/v3/configure/

module.exports = {
  // Path to the history directory (relative to results directory)
  // History files will be read from and written to this location
  historyPath: "./allure-results-combined/history",
  
  // Append new history entries to existing history (true) or replace (false)
  // Setting to true ensures history accumulates across multiple runs
  appendHistory: true
};

