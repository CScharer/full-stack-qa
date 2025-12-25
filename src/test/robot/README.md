# Robot Framework Tests

This directory contains Robot Framework test files (.robot) for the full-stack-qa framework.

## Prerequisites

- Java 21+
- Python 3.13+ (for Robot Framework)
- Maven (for running via Maven plugin)

## Installation

### Install Robot Framework
```bash
pip install robotframework
pip install robotframework-seleniumlibrary
pip install robotframework-requests
```

## Running Tests

### Via Maven
```bash
# Run all Robot Framework tests
./mvnw robotframework:run

# Run specific test file
./mvnw robotframework:run -DtestCasesDirectory=src/test/robot/GoogleSearchTests.robot
```

### Via Robot Framework CLI
```bash
# Run all tests
robot src/test/robot/

# Run specific test file
robot src/test/robot/GoogleSearchTests.robot

# Run with specific browser
robot --variable BROWSER:firefox src/test/robot/GoogleSearchTests.robot
```

## Test Structure

- `*.robot` files contain test cases, keywords, and variables
- Tests are organized by functionality (UI tests, API tests, etc.)

## Configuration

Edit `pom.xml` Robot Framework plugin configuration to modify:
- Test cases directory
- Output directory
- Log level
- Browser selection

