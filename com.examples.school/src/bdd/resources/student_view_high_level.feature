Feature: Student Application Frame
  Specifications of the behavior of the Student Application Frame

  Background: 
    Given The database contains a few students
    And The Student View is shown

  Scenario: Delete a not existing student
    Given The user selects a student from the list
    But The student is in the meantime removed from the database
    When The user clicks the "Delete Selected" button
    Then An error is shown containing the name of the selected student
    And The student is removed from the list
