Feature: Get Drivers by Criteria

  Scenario: When the Driver is properly created in the system, should be able to retrieve by the full name
    Given CreateCompanyRequest request is created
    When the Company is registered in the system
    Then the status code is 201
    Given CreateDriverRequest request is created with following parameters:
      | firstName   | John               |
      | lastName    | Doe                |
      | phoneNumber | +373-65-123-456    |
      | email       | john.doe@gmail.com |
    When the Driver is registered in the system
    Then the status code is 201
    Given CreateDriverRequest request is created with following parameters:
      | firstName   | Brad                |
      | lastName    | Pitt                |
      | phoneNumber | +373-65-421-678     |
      | email       | brad.pitt@gmail.com |
    When the Driver is registered in the system
    Then the status code is 201
    Given CreateDriverRequest request is created with following parameters:
      | firstName   | Joana               |
      | lastName    | Moe                 |
      | phoneNumber | +373-11-222-333     |
      | email       | joana.moe@gmail.com |
    When the Driver is registered in the system
    Then the status code is 201
    When the Drivers are retrieved by the following query params:
      | firstName | like:Jo |
    Then the status code is 200
    Given the expected DriverData objects are created from data:
      | firstName | lastName | phoneNumber     | email               | documentsStatus |
      | Joana     | Moe      | +373-11-222-333 | joana.moe@gmail.com | Citizen         |
      | John      | Doe      | +373-65-123-456 | john.doe@gmail.com  | Citizen         |
    Then the expected and actual "Driver Data" lists are equal ignoring fields "state,city"

  Scenario: When the Drivers are retrieved by the Company, should only fetch the Drivers of that specific Company
    Given CreateCompanyRequest request is created
    When the Company is registered in the system
    Then the status code is 201
    Given CreateDriverRequest request is created with following parameters:
      | firstName   | Michael                  |
      | lastName    | Dalton                   |
      | phoneNumber | +373-65-123-456          |
      | email       | michael.dalton@gmail.com |
    When the Driver is registered in the system
    Then the status code is 201

    Given CreateCompanyRequest request is created
    When the Company is registered in the system
    Then the status code is 201
    Given CreateDriverRequest request is created with following parameters:
      | firstName   | Angelina                 |
      | lastName    | Jolie                    |
      | phoneNumber | +373-65-421-678          |
      | email       | angelina.jolie@gmail.com |
    When the Driver is registered in the system
    Then the status code is 201

    When the Drivers are retrieved by the following query params:
      | companyId | join |
    Then the status code is 200
    Given the expected DriverData objects are created from data:
      | firstName | lastName | phoneNumber     | email                    | documentsStatus |
      | Angelina  | Jolie    | +373-65-421-678 | angelina.jolie@gmail.com | Citizen         |
    Then the expected and actual "Driver Data" lists are equal ignoring fields "state,city"