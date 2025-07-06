package io.kovin.dispatch.management.system.model.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TrailerType {
    FLATBED("Flatbed"),
    STEPDECK("Stepdeck"),
    FLATBED_CONESTOGA("Flatbed Conestoga"),
    STEPDECK_CONESTOGA("Stepdeck Conestoga");

    private final String type;

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
