Feature: Negative scenarios on creating the User in the system

  Scenario: When the User is created without the required fields, should raise an error
    Given an empty CreateUserRequest is created
    When the User is registered in the system
    Then the status code is 400
    Given the expected GroupsErrorResponse list is created from the following data:
      | firstName      | The first name cannot be empty.               |
      | lastName       | The last name cannot be empty.                |
      | password       | The password is mandatory.                    |
      | birthDate      | The birth date is mandatory.                  |
      | employmentDate | The employment date of the user is mandatory. |
      | email          | The e-mail cannot be empty.                   |
    Then the expected and actual "Groups Error Response" objects are equal

  Scenario: When the User is created with invalid Workload data, should raise an error
    Given an invalid CreateWorkloadRequest is created
    Given an invalid CreateSupervisorRequest is created
    Given CreateUserRequest is created
    When the User is registered in the system
    Then the status code is 400
    Given the expected GroupsErrorResponse list is created from the following data table:
      | group      | field      | position | errorMessage                               |
      | workloads  | company    | 0        | You must choose a company.                 |
      | workloads  | commission | 0        | The commission cannot be negative.         |
      | supervisor |            |          | The supervisor with name=[] was not found. |
    Then the expected and actual "Groups Error Response" objects are equal
