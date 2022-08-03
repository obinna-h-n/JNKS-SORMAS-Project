@UI @Sanity @Statistics @UI
Feature: Statistics tab tests

  @tmsLink=SORDEV-5978 @env_main
  Scenario: Add event groups to database export
    Given I log in as a Admin User
    And I click on the Statistics button from navbar
    And I click on the Database Export tab from Statistics directory
    And I click on the Event Groups checkbox from Statistics directory
    Then I click on the Export button from Database Export tab
    And I unzip a downloaded file from Database export
    Then I check if downloaded file generated by Event Groups database export contains required headers