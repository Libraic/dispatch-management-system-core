package io.kovin.dispatch.management.system.model.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DocumentStatus {
    WORK_PERMIT("Work Permit"),
    GREEN_CARD("Green Card"),
    CITIZEN("Citizen");

    private final String type;

    public static DocumentStatus from(String type) {
        if (type == null) {
            return null;
        }

        for (DocumentStatus ds : values()) {
            if (ds.type.equals(type)) {
                return ds;
            }
        }

        return null;
    }
}
