package io.kovin.dispatch.management.system.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ImpactedGroup {

    /**
     * Fields related to User registration.
     */
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    BIRTH_DATE("birthDate"),
    EMPLOYMENT_DATE("employmentDate"),
    EMAIL("email"),
    PASSWORD("password"),
    SUPERVISOR("supervisor"),
    WORKLOADS("workloads"),

    /**
     * Fields related to Company registration.
     */
    COMPANY_NAME("name");

    private final String groupName;
}
