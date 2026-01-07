// Allure3 Configuration File
// This configuration enables history tracking for Allure reports
// See: https://allurereport.org/docs/v3/configure/
//
// Note: historyPath should point to the history.jsonl file (not directory)
// Since we pass 'allure-results-combined' as the results directory, historyPath should be './history/history.jsonl'

const { defineConfig } = require('allure');

module.exports = defineConfig({
  // Path to the history file (relative to results directory)
  // History file will be read from and written to this location
  // This path is relative to the results directory (allure-results-combined)
  historyPath: "./history/history.jsonl",
  
  // Append new history entries to existing history (true) or replace (false)
  // Setting to true ensures history accumulates across multiple runs
  appendHistory: true
});

