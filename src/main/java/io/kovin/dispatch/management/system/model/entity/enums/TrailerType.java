package io.kovin.dispatch.management.system.model.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TrailerType {
    FLATBED("Flatbed", "FB"),
    STEPDECK("Stepdeck", "SD"),
    FLATBED_CONESTOGA("Flatbed Conestoga", "FB Cong"),
    STEPDECK_CONESTOGA("Stepdeck Conestoga", "SD Cong");

    private final String type;
    private final String code;

    public static TrailerType from(String type) {
        if (type == null) {
            return null;
        }

        for (TrailerType tt : values()) {
            if (tt.type.equals(type)) {
                return tt;
            }
        }

        return null;
    }
}
