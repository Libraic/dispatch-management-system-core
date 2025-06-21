package io.kovin.dispatch.management.system.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessage {

    public static final String MISSING_COMPANY_NAME = "The name of the company is missing.";
    public static final String COMPANY_NOT_FOUND_BY_UUID = "The company with UUID=[%s] was not found.";
    public static final String BAD_ROLE = "The provided role=[%s] does not exist.";
    public static final String BAD_POSITION = "The provided position=[%s] does not exist.";
    public static final String SUPERVISOR_NOT_FOUND = "The supervisor with name=[%s] was not found.";
    public static final String FIRST_NAME_IS_MANDATORY = "The first name cannot be empty.";
    public static final String LAST_NAME_IS_MANDATORY = "The last name cannot be empty.";
    public static final String EMAIL_IS_MANDATORY = "The e-mail is cannot be empty.";
    public static final String PASSWORD_IS_MANDATORY = "The password is mandatory.";
    public static final String BIRTH_DATE_IS_MANDATORY = "The day, month and year of birth are mandatory.";
    public static final String EMPLOYMENT_DATE_IS_MANDATORY = "The employment date of the user is mandatory.";
    public static final String INVALID_DATE_FORMAT = "The date=[%s] should be passed in dd-MM-YYYY format.";
    public static final String EMAIL_IN_USE = "The provided email is already in use.";
    public static final String INVALID_COMPANY = "The company with name=[%s] does not exist.";
    public static final String NEGATIVE_COMMISSION = "The commission cannot be negative.";
    public static final String INVALID_SEARCH_CRITERIA = "The field=[%s] has an invalid format=[%s].";

}
