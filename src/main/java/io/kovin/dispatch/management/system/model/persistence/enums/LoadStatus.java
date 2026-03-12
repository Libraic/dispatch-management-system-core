package io.kovin.dispatch.management.system.model.persistence.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LoadStatus {
    DISPATCHED("Dispatched"),
    TRANSIT("Transit"),
    DELIVERED("Delivered");

    private final String status;
}
