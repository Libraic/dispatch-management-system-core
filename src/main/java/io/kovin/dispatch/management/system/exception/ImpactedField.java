package io.kovin.dispatch.management.system.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ImpactedField {
    FIRST_NAME("firstNameError"),
    LAST_NAME("lastNameError"),
    PASSWORD("passwordError"),
    BIRTH_DATE("birthDateError"),
    EMPLOYMENT_DATE("employmentDateError"),
    COMPANY("companyError"),
    COMMISSION("commissionError"),
    USER("userError"),
    POSITION("positionError"),
    ROLE("roleError"),
    EMAIL("emailError");

    private final String mappedField;
}
