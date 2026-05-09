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
    EMAIL("email");


    private final String groupName;
}
