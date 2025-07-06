package io.kovin.dispatch.management.system.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessage {

    public static final String MISSING_COMPANY_NAME = "The name of the company is missing.";
    public static final String MISSING_COMPANY_START_DATE = "The date depicting the collaboration with the %s company is missing.";
    public static final String COMPANY_NOT_FOUND_BY_UUID = "The company with UUID=[%s] was not found.";
    public static final String BAD_ROLE = "The provided role=[%s] does not exist.";
    public static final String BAD_POSITION = "The provided position=[%s] does not exist.";
    public static final String SUPERVISOR_NOT_FOUND = "The supervisor with name=[%s] was not found.";
    public static final String FIRST_NAME_IS_MANDATORY = "The first name cannot be empty.";
    public static final String LAST_NAME_IS_MANDATORY = "The last name cannot be empty.";
    public static final String EMAIL_IS_MANDATORY = "The e-mail cannot be empty.";
    public static final String PASSWORD_IS_MANDATORY = "The password is mandatory.";
    public static final String BIRTH_DATE_IS_MANDATORY = "The day, month and year of birth are mandatory.";
    public static final String EMPLOYMENT_DATE_IS_MANDATORY = "The employment date of the user is mandatory.";
    public static final String PHONE_NUMBER_IS_MANDATORY = "The phone number cannot be empty.";
    public static final String TRUCK_NUMBER_IS_MANDATORY = "The truck number cannot be empty.";
    public static final String INVALID_MAX_LEGAL_WEIGHT_CAPACITY = "The format of max legal weight capacity is invalid.";
    public static final String INVALID_TRAILER_TYPE = "The trailer type is not valid.";
    public static final String INVALID_TRAILER_LENGTH = "The length of the trailer is not valid.";
    public static final String INVALID_DOCUMENT_STATUS = "The status of the documents is not valid.";
    public static final String INVALID_DRIVER_POSITION = "The position of the driver is not valid.";
    public static final String TRAILER_NUMBER_IS_MANDATORY = "The trailer number cannot be empty.";
    public static final String INVALID_DATE_FORMAT = "The date=[%s] should be passed in dd-MM-YYYY format.";
    public static final String EMAIL_IN_USE = "The provided email is already in use.";
    public static final String INVALID_COMPANY = "The company with name=[%s] does not exist.";
    public static final String BLANK_COMPANY = "You must choose a company.";
    public static final String NEGATIVE_COMMISSION = "The commission cannot be negative.";
    public static final String INVALID_SEARCH_CRITERIA = "The field=[%s] has an invalid format=[%s].";
}
