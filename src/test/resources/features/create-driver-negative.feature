Feature: Negative scenarios on creating the Driver in the system

  Scenario: When the Driver is created without the required fields, should raise an error
    Given an empty CreateDriverRequest is created
    When the Driver is registered in the system
    Then the status code is 400
    Given the expected GroupErrorResponse list is created from the following data:
      | firstName              | The first name cannot be empty.                     |
      | lastName               | The last name cannot be empty.                      |
      | email                  | The e-mail cannot be empty.                         |
      | phoneNumber            | The phone number cannot be empty.                   |
      | trailerNumber          | The trailer number cannot be empty.                 |
      | truckNumber            | The truck number cannot be empty.                   |
      | maxLegalWeightCapacity | The format of max legal weight capacity is invalid. |
      | trailerType            | The trailer type is not valid.                      |
      | trailerLength          | The length of the trailer is not valid.             |
      | documentStatus         | The status of the documents is not valid.           |
      | position               | The position of the driver is not valid.            |
    Then the expected and actual "Group Error Response" objects are equal

  Scenario: When the Driver is created without the Company, should raise an error
    Given a CreateDriverRequest without Company is created
    When the Driver is registered in the system
    Then the status code is 404
    Then the expected ErrorResponse is created from the following data:
      | message | The company with UUID=[null] was not found. |
      | status  | NOT_FOUND                                   |
    Then the expected and actual "Error Response" objects are equal