Feature: Get Drivers by Criteria

  Scenario: When the Driver is properly created in the system, should be able to retrieve by the full name
    Given CreateCompanyRequest request is created
    When the Company is registered in the system
    Then the status code is 201
    Given CreateDriverRequest request is created with following parameters:
      | firstName              | John               |
      | lastName               | Doe                |
      | phoneNumber            | +373-65-123-456    |
      | trailerNumber          | 123                |
      | truckNumber            | 456                |
      | email                  | john.doe@gmail.com |
      | maxLegalWeightCapacity | 10000              |
    When the Driver is registered in the system
    Then the status code is 201
    Given CreateDriverRequest request is created with following parameters:
      | firstName     | Brad                |
      | lastName      | Pitt                |
      | phoneNumber   | +373-65-421-678     |
      | trailerNumber | 567                 |
      | truckNumber   | 980                 |
      | email         | brad.pitt@gmail.com |
    When the Driver is registered in the system
    Then the status code is 201
    Given CreateDriverRequest request is created with following parameters:
      | firstName              | Joana               |
      | lastName               | Moe                 |
      | phoneNumber            | +373-11-222-333     |
      | trailerNumber          | 508                 |
      | truckNumber            | 412                 |
      | email                  | joana.moe@gmail.com |
      | maxLegalWeightCapacity | 20000               |
    When the Driver is registered in the system
    Then the status code is 201
    When the Drivers are retrieved by the following query params:
      | firstName | like:Jo |
    Then the status code is 200
    Given the expected DriverData objects are created from data:
      | firstName | lastName | phoneNumber     | trailerNumber | truckNumber | email               | documentsStatus | maxLegalWeightCapacity |
      | Joana     | Moe      | +373-11-222-333 | 508           | 412         | joana.moe@gmail.com | Citizen         | 20000                  |
      | John      | Doe      | +373-65-123-456 | 123           | 456         | john.doe@gmail.com  | Citizen         | 10000                  |
    Then the expected and actual "Driver Data" lists are equal ignoring fields "state,city"