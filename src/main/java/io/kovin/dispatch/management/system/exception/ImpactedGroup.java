package io.kovin.dispatch.management.system.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ImpactedGroup {

    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    BIRTH_DATE("birthDate"),
    EMPLOYMENT_DATE("employmentDate"),
    EMAIL("email"),
    PASSWORD("password"),
    SUPERVISOR("supervisor"),
    WORKLOADS("workloads");

    private final String groupName;
}
