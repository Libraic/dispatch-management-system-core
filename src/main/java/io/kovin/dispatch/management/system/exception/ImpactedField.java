package io.kovin.dispatch.management.system.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ImpactedField {
    COMPANY("company"),
    COMMISSION("commission"),
    SUPERVISOR("supervisor"),

    DISPATCHER("dispatcher"),
    DRIVER("driver"),
    REVENUE("revenue"),
    MILES("miles")

    ;

    private final String mappedField;
}
