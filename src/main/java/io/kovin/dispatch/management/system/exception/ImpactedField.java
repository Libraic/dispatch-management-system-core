package io.kovin.dispatch.management.system.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ImpactedField {
    COMPANY("company"),
    COMMISSION("commission"),
    SUPERVISOR("supervisor");

    private final String mappedField;
}
