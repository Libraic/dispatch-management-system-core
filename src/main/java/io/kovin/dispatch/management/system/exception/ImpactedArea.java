package io.kovin.dispatch.management.system.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ImpactedArea {

    BASIC_INFORMATION("BASIC_INFORMATION"),
    EMPLOYMENT_INFORMATION("EMPLOYMENT_INFORMATION"),
    WORKLOAD("WORKLOAD"),
    NOTES("NOTES");

    private final String area;
}
