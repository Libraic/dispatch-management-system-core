Feature: Create Drivers in the system

  Scenario: Create a Driver in the system with all required information
    Given CreateCompanyRequest request is created
    When the Company is registered in the system
    Then the status code is 201
    Given CreateDriverRequest request is created
    When the Driver is registered in the system
    Then the status code is 201
    Given the expected DriverData is created
    Then the expected and actual "Driver Data" objects are equal
