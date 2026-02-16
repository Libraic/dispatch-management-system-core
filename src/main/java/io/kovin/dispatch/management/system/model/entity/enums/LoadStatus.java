package io.kovin.dispatch.management.system.model.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LoadStatus {
    COVERED("Covered"),
    TRANSIT("Transit"),
    EMPTY("Empty");

    private final String status;
}
