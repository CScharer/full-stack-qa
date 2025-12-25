@Parallel
Feature: Parallel Feature

  Background: Environment Setup
    Given Environment Setup
      | Browser     | Chrome |
      | Company     | CJS    |
      | LOB         | APP    |
      | Environment | TST    |

  Scenario: Scenario_01
    Given pGiven "Scenario_01"
    When pWhen
    And pAnd
    But pBut
    Then pThen

  Scenario Outline: Scenario_Outline_01
    Given pGiven <Scenario Name>
    When pWhen
    And pAnd
    But pBut
    Then pThen

    Examples: Parallel Examples
      | Scenario Name                 |
      | "Scenario Outline Example_01" |
      | "Scenario Outline Example_02" |
      | "Scenario Outline Example_03" |

  Scenario: Scenario_02
    Given pGiven "Scenario_02"
    When pWhen
    And pAnd
    But pBut
    Then pThen

  Scenario Outline: Scenario_Outline_02
    Given pGiven <Scenario Name>
    When pWhen
    And pAnd
    But pBut
    Then pThen

    Examples: Parallel Examples
      | Scenario Name                 |
      | "Scenario Outline Example_01" |
      | "Scenario Outline Example_02" |
      | "Scenario Outline Example_03" |

  Scenario: Scenario_03
    Given pGiven "Scenario_03"
    When pWhen
    And pAnd
    But pBut
    Then pThen

  Scenario Outline: Scenario_Outline_03
    Given pGiven <Scenario Name>
    When pWhen
    And pAnd
    But pBut
    Then pThen

    Examples: Parallel Examples
      | Scenario Name                 |
      | "Scenario Outline Example_01" |
      | "Scenario Outline Example_02" |
      | "Scenario Outline Example_03" |

  Scenario: Scenario_04
    Given pGiven "Scenario_04"
    When pWhen
    And pAnd
    But pBut
    Then pThen

  Scenario Outline: Scenario_Outline_04
    Given pGiven <Scenario Name>
    When pWhen
    And pAnd
    But pBut
    Then pThen

    Examples: Parallel Examples
      | Scenario Name                 |
      | "Scenario Outline Example_01" |
      | "Scenario Outline Example_02" |
      | "Scenario Outline Example_03" |

  Scenario: Scenario_05
    Given pGiven "Scenario_05"
    When pWhen
    And pAnd
    But pBut
    Then pThen

  Scenario Outline: Scenario_Outline_05
    Given pGiven <Scenario Name>
    When pWhen
    And pAnd
    But pBut
    Then pThen

    Examples: Parallel Examples
      | Scenario Name                 |
      | "Scenario Outline Example_01" |
      | "Scenario Outline Example_02" |
      | "Scenario Outline Example_03" |